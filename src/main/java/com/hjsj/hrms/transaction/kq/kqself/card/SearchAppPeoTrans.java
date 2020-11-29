package com.hjsj.hrms.transaction.kq.kqself.card;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SearchAppPeoTrans extends IBusiness {

    public void execute() throws GeneralException {

        ArrayList apppeo = new ArrayList();
        RowSet rs = null;
        try {
            String relationid = KqParam.getInstance().getCardWfRelation();//审批关系id
            if ("#".equals(relationid))
                relationid = "0";
            
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            String nbase = this.userView.getDbname();
            String a0100 = this.userView.getA0100();

            String objId = nbase + a0100;
            
            StringBuffer sb = new StringBuffer();
            sb.append("select mainbody_id from t_wf_mainbody");
            sb.append(" where object_id = '" + objId + "'");
            sb.append(" and relation_id ='" + relationid + "'");
            
            this.frowset = dao.search(sb.toString());
            if (this.frowset.next()) {
                sb.append("' and sp_grade ='9'");
            } else {
                objId = getOrgSpObject(dao, relationid);
            }

            sb.setLength(0);
            sb.append("select mainbody_id from t_wf_mainbody");
            sb.append(" where object_id = '" + objId + "'");
            sb.append(" and relation_id ='" + relationid + "'");
            sb.append(" and sp_grade ='9'");
            
            this.frowset = dao.search(sb.toString());
            while (this.frowset.next()) {
                String app_nbase_a0100 = this.frowset.getString("mainbody_id");
                String dbpre = app_nbase_a0100.substring(0, app_nbase_a0100.length() - 8);
                String app_a0100 = app_nbase_a0100.substring(app_nbase_a0100.length() - 8);
                String tablename = dbpre + "a01";

                sb.setLength(0);
                sb.append("select * from " + tablename + " where a0100 ='" + app_a0100 + "'");

                rs = dao.search(sb.toString());
                sb.delete(0, sb.length());
                DbNameBo dbbo = new DbNameBo(this.getFrameconn());
                String usernameFld = dbbo.getLogonUserNameField().toUpperCase();
                while (rs.next()) {
                    String username = rs.getString(usernameFld);
                    if (username == null || "".equals(username.trim()))
                        continue;

                    String e0122 = rs.getString("e0122");
                    if (null == e0122)
                        e0122 = "";
                    
                    String a0101 = rs.getString("a0101");
                    if (null == a0101)
                        a0101 = "";
                    
                    apppeo.add(e0122 + "," + a0101 + "," + rs.getString(usernameFld));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        this.getFormHM().put("apppeo", apppeo);
    }
    
    /**
     * 从岗位开始上溯查找指定了审批关系的第一个组织机构
     * @Title: getOrgSpObject   
     * @Description:    
     * @param dao
     * @param relationId
     * @return
     */
    private String getOrgSpObject(ContentDAO dao, String relationId) {
        String objId = "";
        
        String orgid = this.userView.getUserPosId();
        if (null == orgid || "".equals(orgid)) {
            orgid = this.userView.getUserDeptId();
            if (null == orgid || "".equals(orgid))
                orgid = this.userView.getUserOrgId();
        } 
        
        if (null == orgid || "".equals(orgid))
            return "";
        
        StringBuffer sb = new StringBuffer();
        sb.append("select object_id from t_wf_mainbody");
        sb.append(" where (object_id='@K[orgid]'");
        sb.append(" or object_id='UM[orgid]'");
        sb.append(" or object_id='UN[orgid]')");
        sb.append(" and relation_id ='" + relationId + "'");
        
        try {
            while (orgid.length()>0) {
                String sql = sb.toString().replace("[orgid]", orgid);
                this.frowset = dao.search(sql);
                if (this.frowset.next()) {
                    objId = this.frowset.getString("object_id");
                    break;
                }
                
                orgid = orgid.substring(0, orgid.length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return objId;
    }
}
