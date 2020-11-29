package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.codeselector.interfaces.CodeDataFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * <p>Title: GetProjectMemberRoleSelectTreeResource </p>
 * <p>Description: 获取项目工时 人员角色列表</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2016-1-13 下午3:48:58</p>
 * @author liuyang
 * @version 1.0
 */
public class GetProjectMemberRoleSelectTreeResource extends CodeDataFactory {

    public ArrayList createCodeData(String codesetid, String code, UserView userView) {

        ArrayList list = getProjectMemberRoleTreeResource("tree",code);
        ArrayList myselect = new ArrayList();
        String isExist = "";
        for (int i = 0; i < list.size(); i++) {
            HashMap map = (HashMap) list.get(i);
            HashMap codeobj = new HashMap();
            if ("01".equals(map.get("id")))
                continue;
            codeobj.put("id", map.get("id"));
            codeobj.put("text", map.get("text"));
            isExist = "NO";
            ArrayList listj = getProjectMemberRoleTreeResource("tree",map.get("id").toString());
            if (listj.size()==0)
                codeobj.put("leaf", Boolean.TRUE);
            else 
                codeobj.put("leaf", Boolean.FALSE);
            codeobj.put("codesetid", codesetid);
            myselect.add(codeobj);
        }
        return myselect;
    }
    
    
    public ArrayList searchCodeByText(String codesetid, String text, UserView userView) {

        ArrayList list = getProjectMemberRoleTreeResource("text",text);
        ArrayList myselect = new ArrayList();
        String isExist = "";
        for (int i = 0; i < list.size(); i++) {
            HashMap map = (HashMap) list.get(i);
            HashMap codeobj = new HashMap();
            if ("01".equals(map.get("id")))
                break;
            codeobj.put("id", map.get("id"));
            codeobj.put("text", map.get("text"));
            codeobj.put("leaf", Boolean.TRUE);
            codeobj.put("codesetid", codesetid);
            myselect.add(codeobj);
        }
        return myselect;
    }
    
    /**
     * 
     * @Title:getProjectMemberRoleTreeResource
     * @Description：获取承担角色的角色列表
     * @author liuyang
     * @param tip 区分是显示树或者是模糊查询 text是模糊 ,tree是树
     * @param code 节点id
     * @return
     */
    private ArrayList getProjectMemberRoleTreeResource(String tip,String code) {
        ArrayList list = new ArrayList();
        RowSet rs = null;
        Connection conn = null;
        try {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            StringBuffer str = new StringBuffer();
            str.append(" select * from codeItem where 1=1 ");
            if (!"root".equals(code)) {
                if("tree".equals(tip))
                    str.append(" and parentid ='"+code+"' and codeitemid<>parentid " );
                else
                    str.append(" and codeitemdesc like '%" + code + "%'");
            }else{
                str.append(" and parentid = codeitemid");
            }
            str.append(" and codesetid='81'");
            rs = dao.search(str.toString());
            while (rs.next()) {
                HashMap map = new HashMap();
                map.put("id", rs.getString("codeitemid"));
                map.put("text", rs.getString("codeitemdesc"));
                map.put("parentid", rs.getString("parentid"));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            PubFunc.closeDbObj(conn);
            PubFunc.closeResource(rs);
        }
        return list;
    }
}
