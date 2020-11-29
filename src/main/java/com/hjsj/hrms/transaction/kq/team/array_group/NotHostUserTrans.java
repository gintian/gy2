package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:过滤出主集中的班组，人员班组分配表中不一致的人员</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 18, 2010:11:11:51 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class NotHostUserTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String nbase = (String) hm.get("nbase");
        String nbaseWhr = "1=1";
        if (null != nbase && !"".equals(nbase) && !"all".equalsIgnoreCase(nbase)) {
            nbaseWhr = "nbase='" + nbase + "'";
        }            
            
        String group_id = (String) hm.get("group_id"); //班组id
        
        //找出主集中与选择的班组一致的人员
        String groupName = getGroupName(group_id);
        String hostindex = getHostIndex(groupName);
        //排班对应指标        
        String hostid = KqParam.getInstance().getShiftGroupItem();
        String groupA01Whr = "";
        if (!"".equals(hostindex) || hostindex.length() > 0) {
            groupA01Whr = hostid + "='" + hostindex + "'";
        } else {
            groupA01Whr = hostid + "='" + groupName + "'";
        }   
        
        //考勤自己的取权限的方法
        String privCode = RegisterInitInfoData.getKqPrivCode(userView);
        String privCodeValue = RegisterInitInfoData.getKqPrivCodeValue(userView);
        String orgWhr = "";
        if (!"".equals(privCodeValue)) {
            if (privCode != null && "UN".equals(privCode))
                orgWhr = "b0110 like '" + privCodeValue + "%'";
            else if (privCode != null && "UM".equals(privCode))
                orgWhr = "e0122 like '" + privCodeValue + "%'";
            else if (privCode != null && "@K".equals(privCode))
                orgWhr = "e01a1 like '" + privCodeValue + "%'";
        }
        
        String groupWhr = "EXISTS(";
        KqUtilsClass kqUtils = new KqUtilsClass(frameconn, userView); 
        ArrayList privDBList = kqUtils.getKqPreList();
        int dbCount = privDBList.size();
        for (int i=0; i<dbCount; i++) {
            String aNbase = (String)privDBList.get(i);
            groupWhr = groupWhr 
                     + " select 1 from " + aNbase + "A01 where " + aNbase + "A01.A0100=kq_group_emp.A0100"
                     + " and kq_group_emp.nbase='" + aNbase + "'" 
                     + " and " + groupA01Whr; 
            if (!"".equals(""))
                groupWhr = groupWhr + " AND " + orgWhr;
            
            if (i < dbCount - 1)
                groupWhr = groupWhr + " UNION ";
        }
        groupWhr = groupWhr + ")";
        
        String privWhr = RegisterInitInfoData.getKqEmpPrivWhr(frameconn, userView, "kq_group_emp");

        String columns = "dbid,dbname,nbase,a0100,a0101,b0110,e0122,group_id";
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select " + columns);
        sqlstr.append(" from kq_group_emp left join dbname");
        sqlstr.append(" ON nbase=pre");
        sqlstr.append(" where " + nbaseWhr + " and group_id!='" + group_id + "'");
        sqlstr.append(" AND " + privWhr);
        sqlstr.append(" AND " + groupWhr);
        
        this.getFormHM().put("sqlstr", sqlstr.toString());
        this.getFormHM().put("column", columns);
        this.getFormHM().put("hostid", hostid);
        this.getFormHM().put("nbase", nbase);
        this.getFormHM().put("group_id", group_id);
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
        String index = KqParam.getInstance().getShiftGroupItem().toUpperCase();
        
        String codeitemid = "";
        RowSet rowSet = null;
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            String codesetid = "";
            String sql = "select codesetid from fielditem where itemid='" + index + "' and fieldsetid='A01'";
            rowSet = dao.search(sql);
            while (rowSet.next()) {
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
}
