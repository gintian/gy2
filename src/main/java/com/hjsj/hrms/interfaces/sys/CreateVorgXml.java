package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CreateVorgXml {
	  /**
     * 输出虚拟机构xml串
     * @return
     * @throws GeneralException
     */
	public String outCodeTree(String setid,String codeitemid,String type) throws GeneralException{
		StringBuffer xmls = new StringBuffer();
		StringBuffer strsql = new StringBuffer();
		ResultSet rset = null;
		Connection conn = AdminDb.getConnection();
		Element root = new Element("TreeNode");
        setid=PubFunc.getReplaceStr(setid);
        codeitemid=PubFunc.getReplaceStr(codeitemid);
		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try
		{

			strsql.append(sqlstr(codeitemid,type));
			ContentDAO dao = new ContentDAO(conn);
			ArrayList itemlist = vorgItemList(dao,rset);
			rset = dao.search(strsql.toString());
			String flag = "0";
			while (rset.next())
			{
				Element child = new Element("TreeNode");
				String itemid = rset.getString("codeitemid");
				if (itemid == null)
					itemid = "";
				for(int i=0;i<itemlist.size();i++){
					String vorgid = (String)itemlist.get(i);
					String arr[] = vorgid.split("::");
					if(arr!=null&&arr.length==2){
						if(arr[1].equals(itemid)){
							flag = "2";
							break;
						}
						if(vorgid.startsWith(itemid)){
							flag = "1";
							break;
						}
					}
				}
				if("0".equals(flag))
					continue;
				itemid = itemid.trim();
				String codesetid = rset.getString("codesetid");
				if("vorg".equals(type))
					child.setAttribute("id", codesetid + itemid);
				else
					child.setAttribute("id","org");
				child.setAttribute("text", rset.getString("codeitemdesc"));
				child.setAttribute("title", itemid + ":" + rset.getString("codeitemdesc"));
				StringBuffer xmlstr = new StringBuffer();
				xmlstr.append("/system/vorgtree.jsp?codesetid=");
				xmlstr.append(codesetid);
				xmlstr.append("&codeitemid=");
				xmlstr.append(itemid);
				xmlstr.append("&type=");
				if("2".equals(flag)){
					xmlstr.append("vorg");
				}else{
					xmlstr.append("org");
				}
				
				if (!itemid.equalsIgnoreCase(rset.getString("childid")))
					child.setAttribute("xml", xmlstr.toString());
				if ("UN".equals(codesetid))
					if("vorg".equals(type))
						child.setAttribute("icon", "/images/vroot.gif");
					else
						child.setAttribute("icon", "/images/unit.gif");
				else if ("UM".equals(codesetid)){
					if("vorg".equals(type))
						child.setAttribute("icon", "/images/vdept.gif");
					else
						child.setAttribute("icon", "/images/dept.gif");
				}else if ("@K".equals(codesetid))
					child.setAttribute("icon", "/images/pos_l.gif");
				else
					child.setAttribute("icon", "/images/table.gif");
				root.addContent(child);
				flag = "0";
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
			// System.out.println("SQL=" +xmls.toString());
		} catch (SQLException ee)
		{
			ee.printStackTrace();
			GeneralExceptionHandler.Handle(ee);
		} finally
		{
			try
			{
				if (rset != null)
				{
					rset.close();
				}
				if (conn != null)
				{
					conn.close();
				}
			} catch (SQLException ee)
			{
				ee.printStackTrace();
			}

		}
		return xmls.toString();
	}
	private String sqlstr(String codeitemid,String type){
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid,codeitemid,codeitemdesc,childid");
		if("vorg".equals(type)){
			sql.append(" from vorganization where ");
		}else
			sql.append(" from organization where ");
		if(codeitemid!=null&&codeitemid.trim().length()>0&&!"all".equalsIgnoreCase(codeitemid)){
			sql.append(" parentid='");
			sql.append(codeitemid);
			sql.append("' and parentid<>codeitemid");
		}else{
			sql.append(" parentid=codeitemid");
		}
		sql.append(" and codesetid in('UN','UM')");
		return sql.toString();
	}
	private ArrayList vorgItemList(ContentDAO dao,ResultSet rset){
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select codeitemid,parentid");
		sql.append(" from vorganization");
		try {
			rset = dao.search(sql.toString());
			while(rset.next()){
				list.add(rset.getString(1)+"::"+rset.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
