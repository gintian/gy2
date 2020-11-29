package com.hjsj.hrms.servlet.performance;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * <p>Title:KhFieldTree.java</p>
 * <p>Description:考核指标指标树</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-1-11 下午04:08:12</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class KhFieldTree extends HttpServlet{

	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException 
	{
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		String pointsetid=(String)req.getParameter("pointsetid");
		String subsys_id=(String)req.getParameter("subsys_id");
		String b0110 = (String)req.getParameter("b0110");
		UserView userView=(UserView)req.getSession().getAttribute(WebConstant.userView);
		StringBuffer xmlTree = new StringBuffer();
		req.setCharacterEncoding("GBK");
		try
		{
			xmlTree=this.getXmlTree(pointsetid, subsys_id,b0110,userView);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		resp.setContentType("text/xml;charset=UTF-8");
		resp.getWriter().println(xmlTree.toString());  
		
	}
	private StringBuffer getXmlTree(String pointsetid,String subsys_id,String b0110,UserView userView)
	{
		Connection con=null;
		RowSet rs = null;
		StringBuffer buf = new StringBuffer();
		try
		{
			StringBuffer sql = new StringBuffer();
			if("-1".equalsIgnoreCase(pointsetid))
			{
				sql.append("select scope,pointsetid,pointsetname,parent_id,b0110,child_id,seq,validflag,subsys_id from per_pointset where parent_id is null and subsys_id='"+subsys_id+"' order by seq");
			}
			else
			{
				sql.append("select scope,pointsetid,pointsetname,b0110,parent_id,child_id,seq,validflag,subsys_id from per_pointset where parent_id ='");
				sql.append(pointsetid+"' and ");
				sql.append("subsys_id='");
				sql.append(subsys_id+"'  order by seq");
			}
			
			con=(Connection)AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sql.toString());
			Element root = new Element("TreeNode");
			root.setAttribute("id","$$00");
			root.setAttribute("text","root");
			root.setAttribute("title","root");
			Document myDocument = new Document(root);
			String yxb0110 = "";
			yxb0110 = getyxb0110(userView,con);
			while(rs.next())
			{
				String unit = rs.getString("b0110");
				
				if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId())&&!"HJSJ".equalsIgnoreCase(unit))
    			{
					/*if(!userView.isHaveResource(IResourceConstant.KH_FIELD,rs.getString("pointsetid")))
        			{
        				continue;
        			}*/
					 
					if(rs.getString("scope")!=null&& "1".equals(rs.getString("scope"))&&!(unit.length()>yxb0110.length()?unit.substring(0, yxb0110.length()):unit).equalsIgnoreCase(yxb0110)){
						continue;
					}

        		}
				Element child = new Element("TreeNode");
				child.setAttribute("id",rs.getString("pointsetid"));
				child.setAttribute("text",rs.getString("pointsetname"));
				child.setAttribute("title",rs.getString("pointsetname"));
				if(isHaveChild(rs.getString("pointsetid"),con)){
					///servlet/performance/KhFieldTree
					child.setAttribute("xml","/servlet/performance/KhFieldTree?pointsetid="+rs.getInt("pointsetid")+"&subsys_id="+subsys_id+"&b0110="+b0110);
				}
				child.setAttribute("target","mil_body");

				child.setAttribute("href","/performance/kh_system/kh_field/init_kh_iframe.do?b_query=link&encryptParam="+PubFunc.encrypt("entery=1&pointsetid="+rs.getInt("pointsetid")+"&subsys_id="+subsys_id+""));
				if(rs.getInt("validflag")==1)
			    	child.setAttribute("icon","/images/open1.png");
				else
					child.setAttribute("icon","/images/open.png");
				root.addContent(child);
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format=Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			buf.append(outputter.outputString(myDocument));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(con!=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		}
		return buf;
	}
	public static String getyxb0110(UserView userView,Connection con) {
		String b0110 = "";
		String codePrefix ="";
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if("UN`".equalsIgnoreCase(operOrg))
				return "";
			if (operOrg!=null && operOrg.length() > 3)
			{
				    String[] temp = operOrg.split("`");
				    b0110 = temp[0].substring(2,temp[0].length());
				    codePrefix = temp[0].substring(0,2);
				    if("UN".equalsIgnoreCase(codePrefix))//如果是单位
						b0110 = b0110;
					else//如果是部门
						b0110 = getUnit(b0110,con);
			} 

			else{
				    b0110 =  userView.getUserOrgId();
			}
			if(b0110.trim().length()==0)
				b0110="x";
	
		return b0110;
	}
	public static String getUnit(String codeid,Connection con){
		String unit = "";
		RowSet rs = null;
		try{
			String style = "";//返回UM或者UN
			StringBuffer sb = new StringBuffer();
			sb.append("select codesetid,codeitemid from organization where codeitemid= (select parentid from organization where codeitemid='"+codeid+"')");
			ContentDAO dao = new ContentDAO(con);
			rs = dao.search(sb.toString());
			if(rs.next()){
				style = rs.getString("codesetid");
				unit = rs.getString("codeitemid");
			}
			if("UM".equalsIgnoreCase(style))
				getUnit(unit,con);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return unit;
	}
	

	private boolean isHaveChild(String pointsetid,Connection con)
	{
		boolean flag=false;
		RowSet rs = null;
		try
		{
			String sql ="select pointsetid from per_pointset where parent_id ='"+pointsetid+"'";
			ContentDAO dao = new ContentDAO(con);
			
			rs = dao.search(sql);
			while(rs.next())
			{
				flag=true;
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					//rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
