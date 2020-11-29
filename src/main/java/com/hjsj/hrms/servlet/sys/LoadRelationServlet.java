/**
 * 
 */
package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:加载汇报关系树
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * create time at:Jan 30, 20131:56:01 PM
 * 
 * @author xuj
 * @version 4.0
 */
public class LoadRelationServlet extends HttpServlet {
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// target="+target+"&paramkey="+paramkey+"&dbnamekey="+dbnamekey+"&a0100key="+a0100key;
		String target = (String) req.getParameter("target");
		String action = (String) req.getSession().getAttribute(
				"SYS_LOAD_RELATION_ACTION");
		String paramkey = (String) req.getParameter("paramkey");
		String dbnamekey = (String) req.getParameter("dbnamekey");
		String a0100key = (String) req.getParameter("a0100key");
		String b0110key = (String) req.getParameter("b0110key");
		String mainbody_id = (String) req.getParameter("mainbody_id");
		String default_line = (String)req.getParameter("default_line");

		try {

			String xmlc = outRelationTree(target, action, paramkey, dbnamekey,
					b0110key,a0100key, mainbody_id,default_line);
			resp.setContentType("text/xml;charset=UTF-8");
			resp.getWriter().println(xmlc);
		} catch (Exception ee) {
			ee.printStackTrace();
		}

	}

	private String outRelationTree(String target, String action,
			String paramkey, String dbnamekey,String b0110key, String a0100key,
			String mainbody_id,String default_line) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		StringBuffer strsql = new StringBuffer();
		ResultSet rset = null;
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			Element root = new Element("TreeNode");
			root.setAttribute("id", "00");
			root.setAttribute("text", "root");
			root.setAttribute("title", "organization");
			Document myDocument = new Document(root);
			String theaction = "javascript:void(0);";
			if("-1".equals(default_line)){
				if(1==com.hrms.hjsj.utils.Sql_switcher.searchDbServer())
					strsql
					.append("select a.object_id object_id from per_mainbody_std  a,per_mainbodyset b where a.body_id=b.body_id and b.level=1 and mainbody_id='"+mainbody_id.substring(3)+"'");
				else
					strsql
					.append("select a.object_id object_id from per_mainbody_std  a,per_mainbodyset b where a.body_id=b.body_id and b.level_o=1 and mainbody_id='"+mainbody_id.substring(3)+"'");
			}else{
				strsql
				.append("select tm.object_id object_id from t_wf_relation tr  left join t_wf_mainbody tm on tr.Relation_id=tm.Relation_id where tr.default_line="+default_line+" and upper(tm.mainbody_id)='"
						+ mainbody_id.toUpperCase() + "' and tm.sp_grade=9");
			}
			// strsql.append(" order by ");
			
			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(strsql.toString());
			HashMap dbrepmap = new HashMap();
			while (rset.next()) {
				String object_id = rset.getString("object_id");
				String dbpre ="";
				String a0100 ="";
				if(object_id.length() != 11)
				{
					if("-1".equals(default_line)){
						dbpre = "Usr";
						a0100 = object_id;
					}else{
						continue;
					}
				}else{
					dbpre = object_id.substring(0, 3);
					a0100 = object_id.substring(3);
				}
				if("-1".equals(default_line)){
					dbpre = "Usr";
					a0100 = object_id;
				}else{
					dbpre = object_id.substring(0, 3);
					a0100 = object_id.substring(3);
				}
				if (dbrepmap.containsKey(dbpre)) {
					StringBuffer a0100str = (StringBuffer) dbrepmap.get(dbpre);
					a0100str.append(",'" + a0100 + "'");
				} else {
					StringBuffer a0100str = new StringBuffer();
					a0100str.append(",'" + a0100 + "'");
					dbrepmap.put(dbpre, a0100str);
				}
			}

			for (Iterator i = dbrepmap.keySet().iterator(); i.hasNext();) {
				String dbpre = (String) i.next();
				StringBuffer a0100str = (StringBuffer) dbrepmap.get(dbpre);
				strsql.setLength(0);
				strsql.append("select b0110,a0100,a0101 from " + dbpre
						+ "a01 where a0100 in ('###'" + a0100str.toString()
						+ ") order by a0000");
				rset = dao.search(strsql.toString());
				while (rset.next()) {
					String a0100 = rset.getString("a0100");
					String b0110 = rset.getString("b0110");
					String a0101 = rset.getString("a0101");
					Element child = new Element("TreeNode");
					child.setAttribute("id", dbpre + a0100);
					child.setAttribute("text", a0101);
					child.setAttribute("title", a0101);
				    if(action!=null&&action.length()>0)
				    {
				    	action=action.replaceAll("`", "&");
				    	if(action.indexOf('?')==-1){
				    		if(paramkey!=null&&paramkey.length()>0){
				    			if(b0110key!=null&&b0110key.length()>0)
				    				theaction=action+"?"+paramkey+"="+dbpre+a0100;
				    			else
				    				theaction=action+"?"+paramkey+"="+dbpre+a0100+"&"+b0110key+"="+b0110;
				    		}else if(dbnamekey!=null&&dbnamekey.length()>0&&a0100key!=null&&a0100key.length()>0){
				    			if(b0110key!=null&&b0110key.length()>0)
				    				theaction=action+"?"+dbnamekey+"="+dbpre+"&"+a0100key+"="+a0100+"&"+b0110key+"="+b0110;
				    			else
				    				theaction=action+"?"+dbnamekey+"="+dbpre+"&"+a0100key+"="+a0100;
				    		}
				    	}else{
				    		if(paramkey!=null&&paramkey.length()>0){
				    			if(b0110key!=null&&b0110key.length()>0)
				    				theaction=action+"&"+paramkey+"="+dbpre+a0100+"&"+b0110key+"="+b0110;
				    			else
				    				theaction=action+"&"+paramkey+"="+dbpre+a0100;
				    		}else if(dbnamekey!=null&&dbnamekey.length()>0&&a0100key!=null&&a0100key.length()>0){
				    			if(b0110key!=null&&b0110key.length()>0)
				    				theaction=action+"&"+dbnamekey+"="+dbpre+"&"+a0100key+"="+a0100+"&"+b0110key+"="+b0110;
				    			else
				    				theaction=action+"&"+dbnamekey+"="+dbpre+"&"+a0100key+"="+a0100;
				    		}
			    		}
				    	
				    }
				    if(theaction.indexOf("encryptParam")==-1){
						//将url链接参数加密为一个参数encryptParam  xuj add 2014-9-2  start
						int index = theaction.indexOf("&");
						if(index>-1){
							String allurl = theaction.substring(0,index);
							String allparam = theaction.substring(index);
							theaction=allurl+"&encryptParam="+PubFunc.encrypt(allparam);
						}
						//将url链接参数加密为一个参数encryParam  xuj add 2014-9-2  end
					}
					child.setAttribute("href", theaction);
					child.setAttribute("target", target);
					String url="/system/load_relationtree?target="+target+"&paramkey="+paramkey+"&dbnamekey="+dbnamekey+"&a0100key="+a0100key+"&mainbody_id="+dbpre+a0100+"&default_line="+default_line;
					child.setAttribute("xml", url);
					child.setAttribute("icon", "/images/man.gif");
					root.addContent(child);
				}
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
			// System.out.println("SQL=" +xmls.toString());
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
		    PubFunc.closeResource(rset);
		    PubFunc.closeResource(conn);
		}
		return xmls.toString();
	}
}
