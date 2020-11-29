package com.hjsj.hrms.utils.components.codeselector;

import com.hjsj.hrms.module.kq.util.KqPrivBo;
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
 * <p>Description: 得到考勤人员库列表（树）</p>
 * <p>Company: hjsj</p>
 * <p>create time: 2018-4-18</p>
 * @author zhaoxj
 * @version 1.0
 */
public class GetKqNbaseTree extends CodeDataFactory {

	@Override
	public ArrayList createCodeData(String codesetid, String code, UserView userView) {
	    ArrayList itemList = new ArrayList();
	    Connection conn = null;
	    RowSet rs = null;
	    try {
	        conn = AdminDb.getConnection();
	        ArrayList nbases = KqPrivBo.getB0110Dase(userView, conn);
	        StringBuffer buf = new StringBuffer();
	        buf.append("(");
	        for (int i = 0; i < nbases.size(); i++) {
	            buf.append(" Upper(pre)='" + nbases.get(i).toString().toUpperCase() + "'");
	            if (i != nbases.size() - 1)
	                buf.append(" or ");
	        }
	        buf.append(")");
	        StringBuffer sql = new StringBuffer();
	        sql.append("select dbname,pre from dbname where 1=1 and ");
	        if (buf != null && buf.toString().length() > 0)
	            sql.append(buf.toString());
	        sql.append("ORDER BY dbid");
	        
	        ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                HashMap treeitem = new HashMap();
                treeitem.put("id", rs.getString("pre"));
                treeitem.put("text", rs.getString("dbname"));
                treeitem.put("codesetid", "@@");
                treeitem.put("itemdesc", rs.getString("dbname"));
                treeitem.put("leaf", Boolean.TRUE);
                treeitem.put("checked", false);
                
                itemList.add(treeitem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
            PubFunc.closeDbObj(conn);
        }
	    
        return itemList;
	}

	@Override
	public ArrayList searchCodeByText(String codesetid, String text, UserView userView) {
		
		return null;
	}
}
