package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

/**
 * 
 * <p>Title:是否有人员，有人员展现人员页面否则直接出现时间页面</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 21, 2010:9:59:09 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class JudgeUserTrans extends IBusiness {

    public void execute() throws GeneralException {
        try {
            String nbase1 = (String) this.formHM.get("nbase1");
            String group_id1 = (String) this.formHM.get("group_id1");
            String a01Name = getJudgeUser(nbase1, group_id1);
            this.getFormHM().put("msg", a01Name);
            this.getFormHM().put("nbase", nbase1);
            this.getFormHM().put("group_id", group_id1);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private String getJudgeUser(String nbase, String group_id) {
        String flag = "false";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        //找出主集中与选择的班组一致的人员
        String groupName = getGroupName(group_id);
        String hostindex = getHostIndex(groupName);
        //排班对应指标
        String hostid = gethostid();
        String privCode = RegisterInitInfoData.getKqPrivCode(userView);
        String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
        KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
        String kq_dbase = para.getNbase();
         String[] kq_dbases = kq_dbase.split(",");
         for (int i = 0; i < kq_dbases.length; i++)
			{
         	 if ( kq_dbases[i].equals(nbase))
				{
         		kq_dbases= new String[1];
         		kq_dbases[0]=nbase;
         		 break;
				}
			}
        StringBuffer sqlstr = new StringBuffer();
        for (int i = 0; i < kq_dbases.length; i++)
		{
	        sqlstr.append("select a0100 from kq_group_emp");
	        sqlstr.append(" where nbase='" + kq_dbases[i] + "' and group_id!='" + group_id + "'");
	        if (!"".equals(privCodeValue)) {
	            if (privCode != null && "UN".equals(privCode))
	                sqlstr.append(" and b0110 like '" + privCodeValue + "%'");
	            else if (privCode != null && "UM".equals(privCode))
	                sqlstr.append(" and e0122 like '" + privCodeValue + "%'");
	            else if (privCode != null && "@K".equals(privCode))
	                sqlstr.append(" and e01a1 like '" + privCodeValue + "%'");
	        }
	        sqlstr.append(" and EXISTS");
	        sqlstr.append("(select * from " + kq_dbases[i] + "A01 where " + kq_dbases[i] + "A01.A0100=kq_group_emp.A0100");
	        if (!"".equals(hostindex) || hostindex.length() > 0) {
	            sqlstr.append(" and " + hostid + "='" + hostindex + "'");
	        } else {
	            sqlstr.append(" and " + hostid + "='" + groupName + "'");
	        }
	        sqlstr.append(" and e0122 like '" + privCodeValue + "%')");
	        
	        String privWhr = RegisterInitInfoData.getWhereINSql(userView, kq_dbases[i]);
	        sqlstr.append(" and a0100 in (select a0100 " + privWhr + ")");
	        
	        sqlstr.append(" UNION ");
		}
        if (sqlstr.length() > 0)
        	sqlstr.setLength(sqlstr.length() - 7);
        try {
            rowSet = dao.search(sqlstr.toString());
            while (rowSet.next()) {
                String a0100N = rowSet.getString("a0100");
                if (a0100N != null) {
                    if (!"".equals(a0100N) || a0100N.length() > 0) {
                        flag = "true";
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return flag;
    }

    //班组中文名称
    private String getGroupName(String groupid) {
        String name = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        String sql = "select name from kq_shift_group where group_id='" + groupid + "'";
        try {
            rowSet = dao.search(sql);
            while (rowSet.next()) {
                name = rowSet.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return name;
    }

    //结构参数中设排班对应的指标
    private String getHostIndex(String groupName) {
        String codeitemid = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            String index = gethostid();
            String codesetid = "";
            String sql = "select codesetid from fielditem where itemid='" + index + "' and fieldsetid='A01'";
            rowSet = dao.search(sql);
            if (rowSet.next()) {
                codesetid = rowSet.getString("codesetid");
            }

            if (codesetid != null) {
                if (!"".equals(codesetid) || codesetid.length() > 0) {
                    StringBuffer sqlb = new StringBuffer();
                    sqlb.append("select codeitemid from codeitem where codesetid='" + codesetid + "'");
                    sqlb.append(" and codeitemdesc='" + groupName + "'");
                    rowSet = dao.search(sqlb.toString());
                    while (rowSet.next()) {
                        codeitemid = rowSet.getString("codeitemid");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rowSet);
        }
        return codeitemid;
    }

    private String gethostid() {
        return KqParam.getInstance().getShiftGroupItem();
    }
}
