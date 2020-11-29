package com.hjsj.hrms.taglib.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
/**
 * 风险警示
 * <p>Title:CautionColorTag.java</p>
 * <p>Description>:CautionColorTag.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jan 20, 2010 5:58:34 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class CautionColorTag extends BodyTagSupport {

	private String a0100="";
	private String nbase="";
    private String caution_field="";//警示指标 
    private String codeitems="";//指标代码项  01,02,03(以逗号隔开)
    private String colors="";//代码项对应的颜色 #000000,#002233,#cceeww(以逗号隔开)
    private String bgcolor="";
	public String getBgcolor() {
		return bgcolor;
	}
	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getCaution_field() {
		return caution_field;
	}
	public void setCaution_field(String caution_field) {
		this.caution_field = caution_field;
	}
	public String getCodeitems() {
		return codeitems;
	}
	public void setCodeitems(String codeitems) {
		this.codeitems = codeitems;
	}
	public String getColors() {
		return colors;
	}
	public void setColors(String colors) {
		this.colors = colors;
	}
	public int doEndTag() throws JspException 
	{
			
		if(caution_field==null||caution_field.length()<=0)//代码指标
			return SKIP_BODY;
		if(codeitems==null||codeitems.length()<=0)//代码
			return SKIP_BODY;
		if(colors==null||colors.length()<=0)//颜色对应代码指标
			return SKIP_BODY;
		Connection conn=null;
		RowSet rs=null;
		try{
			
			FieldItem item=DataDictionary.getFieldItem(caution_field);
			if(item==null)
				return  SKIP_BODY;			
			if(item.getCodesetid()==null||item.getCodesetid().length()<=0|| "0".equals(item.getCodesetid()))
				return  SKIP_BODY;
			conn=AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			StringBuffer sql=new StringBuffer();
			if("A01".equalsIgnoreCase(item.getFieldsetid()))
			{
				sql.append("select "+item.getItemid()+" colorId from "+this.nbase+"A01");
				sql.append(" where a0100='"+this.a0100+"'");
			}else{
				sql.append("select "+item.getItemid()+" colorId from "+this.nbase+item.getFieldsetid()+"");
				sql.append(" where a0100='"+this.a0100+"'");
				sql.append("and  i9999=(select max(b.i9999) from "+this.nbase+item.getFieldsetid()+"  b where b.a0100='"+this.a0100+"') ");
			}
			rs=dao.search(sql.toString());
			if(rs.next())
			{
				String colorId=rs.getString("colorId");
				if(colorId!=null&&colorId.length()>0)
				{
					//System.out.println(colorId);
					String color=colorMap(codeitems,colors,colorId);
					if(color!=null&&color.length()>0)
					{
						 /*pageContext.getOut().println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\""+color+"\">");
						 pageContext.getOut().println("<tr><td height='100%'>&nbsp;");
						 pageContext.getOut().println("<tr><td>");
						 pageContext.getOut().println("</td></tr>");
						 pageContext.getOut().println("</table>");*/
						pageContext.setAttribute(bgcolor, color);
					}else
						pageContext.setAttribute(bgcolor, "");
				}else
					pageContext.setAttribute(bgcolor, "");
			}else
			  pageContext.setAttribute(bgcolor, "");
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try{
			 if(rs!=null)
				 rs.close();
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return SKIP_BODY;	
	}
	/**
	 * 指标和颜色对应
	 * @param codeitems
	 * @param colors
	 * @return
	 */
	private String colorMap(String codeitems,String colors,String colorId)
	{
		String[] codes=codeitems.split(",");
		String[] colorsArr=colors.split(",");
		HashMap map=new HashMap();
		for(int i=0;i<codes.length;i++)
		{
			if(colorsArr.length>i)
			{
				map.put(codes[i], colorsArr[i]);
			}
		}
		
		return (String)map.get(colorId);
		
	}
}
