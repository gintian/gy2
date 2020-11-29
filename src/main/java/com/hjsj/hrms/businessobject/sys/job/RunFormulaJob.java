package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;


public class RunFormulaJob implements Job {

    private Logger log = LoggerFactory.getLogger(RunFormulaJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        log.info("[计算公式后台作业]任务开始");
        long start = System.currentTimeMillis();
        // TODO Auto-generated method stub
        Connection conn = null;
        RowSet rs = null;
        RowSet rs1 = null;
        try {
            //System.out.println("------------->"+"进入后台作业");
            conn = (Connection) AdminDb.getConnection();
            BatchBo batchbo = new BatchBo();
            ContentDAO dao = new ContentDAO(conn);
            String password = "";
            rs = dao.search("select Password from operuser where UserName='su'");
            String check = "0";
            if (rs.next()) {
                password = rs.getString("Password");
                password = password != null ? password : "";
                check = "1";
            }
            UserView uv = new UserView("su", password, conn);
            uv.canLogin();
            String job_param = "";
            ContentDAO dao1 = new ContentDAO(conn);
            rs1 = dao1.search("select job_param from t_sys_jobs where jobclass='com.hjsj.hrms.businessobject.sys.job.RunFormulaJob'");
            if (rs1.next()) {
                job_param = rs1.getString("job_param");
            }
            if (job_param == null) {
                job_param = "";
            }
            if (uv != null && "1".equals(check)) {
                //System.out.println("------------->"+"后台作业进入59行,job_param="+job_param);
                //如果参数指定人员库，则计算指定人员库 guodd 2019-10-22
                if (job_param.toUpperCase().indexOf("NBASE=") != -1) {
                    /*人员库参数格式：NBASE=Usr,Oth,..... */
                    //截取出人员库参数
                    String startStr = job_param.substring(job_param.toUpperCase().indexOf("NBASE="));
                    String valueStr = startStr.substring(0, startStr.indexOf(";") > 0 ? startStr.indexOf(";") + 1 : startStr.length());
                    //将人员库参数从作业参数中删除（实际计算时处理参数没考虑人员库参数，不删除会导致分析出错）
                    job_param = job_param.replace(valueStr, "");
                    job_param = job_param.endsWith(";") ? job_param.substring(0, job_param.length() - 1) : job_param;

                    //循环人员库执行计算
                    valueStr = valueStr.substring(6, valueStr.length()).replace(";", "");
                    String[] nbases = valueStr.split(",");
                    for (int i = 0; i < nbases.length; i++) {
                        batchbo.colUpdate(conn, uv, nbases[i], "", "", "0", "0", "0", "1", job_param);
                    }
                } else { //没指定计算所有人员库
                    rs = dao.search("select Pre from dbname");
                    while (rs.next()) {
                        String dbname = rs.getString("Pre");
//					batchbo.colUpdate1(conn,uv,dbname,
//					"","","0","0","0","5");//培训
					/*batchbo.colUpdate(conn,uv,dbname,
							"","","0","0","0","1");*///人员
                        //System.out.println("------------->"+"进入后台作业69行(人员计算公式):"+dbname);
                        batchbo.colUpdate(conn, uv, dbname, "", "", "0", "0", "0", "1", job_param);
                    }
                }
				
				/*batchbo.colUpdate(conn,uv,"",
						"","","0","0","0","2");*///单位
                batchbo.colUpdate(conn, uv, "", "", "", "0", "0", "0", "2", job_param);
				/*batchbo.colUpdate(conn,uv,"",
						"","","0","0","0","3");*///职位
                batchbo.colUpdate(conn, uv, "", "", "", "0", "0", "0", "3", job_param);
            }

            log.info("[计算公式后台作业]任务结束===[consume time is {} ms]===", (System.currentTimeMillis() - start));
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            log.error("计算公式后台作业执行sql报错!,desc:{}", e);
            e.printStackTrace();
        } catch (Exception e) {
            log.error("计算公式执行后台作业发生异常,desc:{}", e);
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs1);
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(conn);
        }
    }


}
