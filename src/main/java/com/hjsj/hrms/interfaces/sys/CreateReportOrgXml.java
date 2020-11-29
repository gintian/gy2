package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

public class CreateReportOrgXml {
	
	
	public String outCodeTree(String unitcode,String report_type) throws GeneralException{
		StringBuffer xmls = new StringBuffer();
		StringBuffer strsql = new StringBuffer();
		ResultSet rset = null;
		Connection conn = AdminDb.getConnection();
		Element root = new Element("TreeNode");
        unitcode=PubFunc.getReplaceStr(unitcode);
		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try
		{

			strsql.append(sqlstr(unitcode,report_type));
			ContentDAO dao = new ContentDAO(conn);
			 
			rset = dao.search(strsql.toString());
		
			while (rset.next())
			{
				Element child = new Element("TreeNode");
				String _unitcode = rset.getString("unitcode");
				if (_unitcode == null)
					_unitcode = "";
			 
				_unitcode = _unitcode.trim();
				child.setAttribute("id",_unitcode);
				child.setAttribute("text", rset.getString("unitname"));
				child.setAttribute("title",_unitcode);
				StringBuffer xmlstr = new StringBuffer();
				xmlstr.append("/system/report_orgtree.jsp?unitcode="+_unitcode+"&report_type="+report_type);
				child.setAttribute("icon", "/images/unit.gif"); 
				child.setAttribute("xml",xmlstr.toString());
				root.addContent(child);
				
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
	
	private String sqlstr(String unitcode,String report_type){
		StringBuffer sql = new StringBuffer();
		sql.append("select unitcode,unitname");
		sql.append(" from tt_organization where ");
		
		
		if(unitcode==null||unitcode.trim().length()==0){
				sql.append(" parentid=unitcode  ");
		}else{
				sql.append(" parentid='"+unitcode+"' and parentid<>unitcode ");
		}
		
		int yy=0;
		int mm=0;
		int dd=0;
		Calendar d=Calendar.getInstance();
		yy=d.get(Calendar.YEAR);
		mm=d.get(Calendar.MONTH)+1;
		dd=d.get(Calendar.DATE);
		
		if(!"all".equalsIgnoreCase(report_type))
		{
			try
			{
				Connection conn = AdminDb.getConnection();
				ContentDAO dao = new ContentDAO(conn);
				
				
				ResultSet rset = dao.search("select * from tt_cycle where id="+report_type);
				if(rset.next())
				{
					Date date=rset.getDate("bos_date");
					Calendar cd=Calendar.getInstance();
					cd.setTime(date);
					yy=cd.get(Calendar.YEAR);
					mm=cd.get(Calendar.MONTH);
					dd=cd.get(Calendar.DATE);
				}
				if (rset != null)
					rset.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
		sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
		sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
		sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
		sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
		sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");	 			
	
		sql.append(" order by a0000");
		
		
		
		
		
		
		return sql.toString();
	}
	
	 
}
