package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.infor.MoveInfoBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:TemplateSubmitInfoJob.java</p>
 * <p>Description>:广东中烟：异动时间生效后，执行移库操作，并调用存储过程将人事异动所有变动信息提交至子集，
 *    涉及组织变动与人员离职、离退业务、离岗退养等移库业务</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014-4-16 上午10:26:09</p>
 * <p>@author:wangrd</p>
 * <p>@version: 6.0</p>
 */

public class TemplateSubmitInfoJob implements Job  {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
        Connection conn = null;
        RowSet rs = null;
        try {
            conn = (Connection) AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            String job_param = "";
            String password = "";
            rs = dao.search("select Password from operuser where UserName='su'");
            if (rs.next()) {
                password = rs.getString("Password");
                password = password != null ? password : "";
            }
            UserView uv = new UserView("su", password, conn);
            //dbnamebo类移库方法changeBusiTableData于2018年7月增加了根据userview判断版本的代码，此处需为userview赋版本号，默认71版本
        	String lock=SystemConfig.getPropertyValue("lock_version");
			if(StringUtils.isBlank(lock)){
				lock="76";
			}
			uv.setVersion(Integer.parseInt(lock));
            MoveInfoBo moveInfo = new MoveInfoBo(conn, uv);
            moveInfo.setBNeedScanFormation(false);
            moveInfo.setBNeedCheckOnly(false);
            StringBuffer buf = new StringBuffer();
            buf.append("select * from t_sys_jobs  where jobclass ='");
            buf.append("com.hjsj.hrms.businessobject.sys.job.TemplateSubmitInfoJob");
            buf.append("'");
            rs = dao.search(buf.toString());
            if (rs.next()) {
                job_param = rs.getString("job_param") == null ? "" : rs.getString("job_param");
                job_param= job_param.replace("＝", "=");
            }
            if ("".equals(job_param)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有配置参数,参数格式如下：setId =,validDateFld=,destBaseFld=,resultFld=,procName="));
            }
            //解析参数
            String curBase = "usr", setId = "", validDateFld = "", destBaseFld = "", resultFld = "", procName = "";
            String[] arr_job = job_param.split(",");
            for (int i = 0; i < arr_job.length; i++) {
                String str = arr_job[i];
                int m = str.indexOf("=");
                String param = str.substring(0, m);
                String value = str.substring(m + 1);
                if ("setid".equalsIgnoreCase(param)) {
                    setId = value;
                } else if ("validDateFld".equalsIgnoreCase(param)) {
                    validDateFld = value;
                } else if ("destBaseFld".equalsIgnoreCase(param)) {
                    destBaseFld = value;
                } else if ("resultFld".equalsIgnoreCase(param)) {
                    resultFld = value;
                } else if ("procName".equalsIgnoreCase(param)) {
                    procName = value;
                }
            }

            if (setId == null || "".equals(setId)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有配置异动子集,格式:setId=异动子集"));
            }
            if (validDateFld == null || "".equals(validDateFld)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有配置异动生效时间指标,格式:validDateFld="));
            }
            if (destBaseFld == null || "".equals(destBaseFld)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有配置目标库指标,格式:destBaseFld="));
            }
            if (resultFld == null || "".equals(resultFld)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有配置处理结果指标,格式:resultFld="));
            }
            if (procName == null || "".equals(procName)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有配置存储过程,格式:procName="));
            }

            String curDate = PubFunc.getStringDate("yyyy-MM-dd");
            DbWizard dbw = new DbWizard(conn);

            //循环所有人员库
            StringBuffer sql = new StringBuffer();
            sql.append("select dbid,dbname,pre from dbname ");
            CallableStatement cstmt = null; // 存储过程
            try {
                RowSet dbset = null;
                ArrayList list = new ArrayList();
                dbset = dao.search(sql.toString());
                while (dbset.next()) {
                    list.clear();
                    curBase = dbset.getString("pre");
                    String changeTabName = curBase + setId;
                    if (!dbw.isExistTable(changeTabName, false)) {
                        throw GeneralExceptionHandler.Handle(new Exception("表" + changeTabName + "不存在"));
                        // continue;
                    }
                    buf.setLength(0);
                    buf.append("select A.a0100,A.b0110,A.E0122,A.e01A1").append(",B."+destBaseFld);
                    buf.append(" from ");
                    buf.append(changeTabName+" B ");
                    buf.append(" left join  ");
                    buf.append(curBase+"A01 A ");                    
                    buf.append(" on A.A0100=B.A0100 ");
                    buf.append(" where ");
                    buf.append("B."+validDateFld).append("<=").append(Sql_switcher.dateValue(curDate));
                    buf.append(" and ");
                    buf.append(Sql_switcher.sqlNull("B."+resultFld, "2")).append("=").append("'2'");
                    buf.append(" and ");
                    buf.append(Sql_switcher.sqlNull("B."+destBaseFld, curBase))
                           .append("<>'").append(curBase).append("'");
                 
                    rs = dao.search(buf.toString());
                    while (rs.next()) {
                        String destBase = rs.getString(destBaseFld) == null ? "" : rs.getString(destBaseFld);
                        if (!"".equals(destBase)) { // 移库
                            if (rs.getString("a0100")==null) {
                                continue;
                            }
                            LazyDynaBean lazy = new LazyDynaBean();
                            lazy.set("a0100", rs.getString("a0100"));
                            String e01a1=rs.getString("e01a1");
                            if (e01a1==null) {
                                e01a1="";
                            }
                            lazy.set("e01a1", e01a1);
                            lazy.set("touserbase", destBase);
                            list.add(lazy);
                        }
                    }
                    if (list.size() > 0) {
                        String mess = moveInfo.MoveEmployees(curBase, "", list);
                        if (moveInfo.isBHaveErrorToThrow()) {
                            throw GeneralExceptionHandler.Handle(new GeneralException("", mess, "", ""));
                        }
                    }
                }
                // 调用存储过程
                String validateSql1 = "";
                switch (Sql_switcher.searchDbServer()) {
                    case 1 : // MSSQL
                        validateSql1 = "select   count(*)  from   sysobjects   where   ID in (SELECT id FROM sysobjects as a WHERE OBJECTPROPERTY(id, N'IsProcedure') = 1 and ";
                        validateSql1 += " id = object_id(N'[dbo].[" + procName + "]'))";

                        break;
                    case 2 :// oracle
                        validateSql1 = "SELECT count(*) FROM all_objects WHERE object_type='PROCEDURE' AND upper(object_name)='" + procName.toUpperCase() + "'";
                        break;
                }

                // 判断有无存储过程名
                int nn = 0;
                RowSet rowSet = dao.search(validateSql1);
                if (rowSet.next()) {
                    nn = rowSet.getInt(1);
                }
                if (nn == 0) {
                    throw GeneralExceptionHandler.Handle(new Exception("存储过程不存在"));

                }
                StringBuffer sqlCall = new StringBuffer("{call  " + procName);
                sqlCall.append("}");
                
                cstmt = conn.prepareCall(sqlCall.toString());
                cstmt.execute();


            } catch (Exception e) {
                e.printStackTrace();
            }finally {
            	PubFunc.closeResource(cstmt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
        }
        
    }    
    
      
}
