package com.hjsj.hrms.interfaces.report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class GetUnitBytabidByXml {
	private String selfunitcode="";
	private String unitcode="";
	private String tsort="";
	private String init="";
	public GetUnitBytabidByXml(){
	}
	public GetUnitBytabidByXml(String selfunitcode,String unitcode,String tsort,String init){
		this.selfunitcode=selfunitcode;
		this.unitcode=unitcode;
		this.tsort=tsort;
		this.init=init;
	}
	public String outTreeByxml()throws GeneralException {
		StringBuffer xmls=new StringBuffer();
		
		Element root = new Element("TreeNode");
		
		root.setAttribute("id", "0");
		root.setAttribute("text","");
		root.setAttribute("title","");
		
		Document myDocument = new Document(root);
		
		try {
			ArrayList list=this.getUnitlist();
			for(Iterator t=list.iterator();t.hasNext();){
				LazyDynaBean bean=(LazyDynaBean)t.next();
				Element child = new Element("TreeNode");

				if(bean.get("reporttypes")!=null){
					String reporttypes=(String)bean.get("reporttypes");
					if(reporttypes!=null&&reporttypes.length()!=0){
						reporttypes=","+reporttypes;
						if(reporttypes.indexOf(","+this.tsort+",")!=-1){
							child.setAttribute("id", (String)bean.get("unitcode")+"/"+(String)bean.get("unitname"));
							child.setAttribute("text", (String)bean.get("unitname"));
							child.setAttribute("title", (String)bean.get("unitname"));
							child.setAttribute("target", "mil_body");
							child.setAttribute("icon", "/images/unit.gif");
							child.setAttribute("xml" ,"search_rep_rej_unit_tree.jsp?unitcode='&selfunitcode=" + (String)bean.get("unitcode") + "&tsort="+this.tsort+"&init=2");
							root.addContent(child);
						}else{
							
						}
					}else{
						
					}
				}
			}
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmls.toString();
	}
	public ArrayList getUnitlist(){
		ArrayList list=new ArrayList();
		RowSet rs =null;
		Connection conn=null;;
		
		StringBuffer sql=new StringBuffer();
		try {
			
			conn = AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			sql.append("select * from tt_organization where parentid='"+this.selfunitcode+"' and parentid<> unitcode");
			Calendar d=Calendar.getInstance();
			
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			
			
			sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			
			sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			
			rs=dao.search(sql.toString());
			LazyDynaBean bean=null;
			while(rs.next()){
				bean=new LazyDynaBean();
				bean.set("unitcode", rs.getString("unitcode"));
				bean.set("unitname", rs.getString("unitname"));
				if(Sql_switcher.readMemo(rs, "reporttypes").length()==0){
					continue;
				}
				bean.set("reporttypes", Sql_switcher.readMemo(rs, "reporttypes"));
				list.add(bean);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	finally{
			
				try {
					if (rs != null) {
						rs.close();
					}
					if(conn!=null){
						conn.close();
					}
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			
		}
		return list;
	}
	public String getTypes(){
		String types="";
		StringBuffer bu=new StringBuffer("select * from tt_organization where unitcode='");
		bu.append(this.selfunitcode);
		bu.append("'");
		Calendar d=Calendar.getInstance();
		
		int yy=d.get(Calendar.YEAR);
		int mm=d.get(Calendar.MONTH)+1;
		int dd=d.get(Calendar.DATE);
		
		
		bu.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
		bu.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
		bu.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
		
		bu.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
		bu.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
		bu.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
		RowSet rs =null;
		Connection conn=null;;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			rs=dao.search(bu.toString());
			if(rs.next()){
				types=Sql_switcher.readMemo(rs,"reperttypes");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	finally{
			
			try {
				if (rs != null) {
					rs.close();
				}
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		
	}
	
		return types;
		
	}
}
