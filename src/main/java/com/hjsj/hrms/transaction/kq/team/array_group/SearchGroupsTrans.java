package com.hjsj.hrms.transaction.kq.team.array_group;

import com.hjsj.hrms.businessobject.kq.team.GroupsArray;
import com.hjsj.hrms.businessobject.kq.team.KqClassArrayConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchGroupsTrans extends IBusiness implements KqClassArrayConstant {

    public void execute() throws GeneralException {
    	 try {
             HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
             String a_code = (String) hm.get("return_code");
             
             String unCodeitemid = (String) hm.get("unCodeitemid");
             a_code=unCodeitemid;
             
             a_code = null == a_code ? "" : a_code;
             // 选中了班组或人员时，进入班组设置，显示权限范围内班组即可
             if (a_code.startsWith("GP") || a_code.startsWith("EP"))
                 a_code = "";
             GroupsArray groupsArray = new GroupsArray(this.getFrameconn(), this.userView);
             String groupOrgWhr = groupsArray.getGroupOrgWhr(a_code);
             
             StringBuffer whereBuffer = new StringBuffer();
             whereBuffer.append(" FROM " + kq_shift_group_table);
             if (groupOrgWhr != null && groupOrgWhr.length() > 0) {
                 whereBuffer.append(" WHERE ");
                 whereBuffer.append(groupOrgWhr);
             }
             StringBuffer sqlstr = new StringBuffer();
             sqlstr.append("select " + kq_shift_group_Id + "," + kq_shift_group_name + "," + kq_shift_org_id);
             sqlstr.append(" ").append(whereBuffer.toString());
             sqlstr.append(" order by ");
             sqlstr.append(Sql_switcher.substr("org_id", "2", Sql_switcher.length("org_id") + "-2"));
             sqlstr.append(",group_id");

             ArrayList vo_list = new ArrayList();
             ArrayList codesetid = new ArrayList();
             ContentDAO dao = new ContentDAO(this.getFrameconn());
             try {
                 this.frowset = dao.search(sqlstr.toString());
                 RecordVo vo = null;
                 while (this.frowset.next()) {
                     if ((this.userView.isHaveResource(IResourceConstant.KQ_CLASS_GROUP, this.frowset.getString("group_id")))) {
                         vo = new RecordVo(kq_shift_group_table);
                         vo.setString(kq_shift_group_Id, this.frowset.getString(kq_shift_group_Id));
                         vo.setString(kq_shift_group_name, this.frowset.getString(kq_shift_group_name));
                         String org_id = this.frowset.getString(kq_shift_org_id);
                         if (org_id != null && org_id.length() > 2) {
                             codesetid.add(org_id.substring(0, 2));
                             org_id = org_id.substring(2);
                         } else {
                             codesetid.add("");
                             org_id = "";
                         }
                         vo.setString(kq_shift_org_id, org_id);
                         vo_list.add(vo);
                     }
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
             this.getFormHM().put("vo_list", vo_list);
             this.getFormHM().put("codesetid", codesetid);
             this.getFormHM().put("a_code", a_code);
             this.getFormHM().put("unCodeitemid", unCodeitemid);
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
}
