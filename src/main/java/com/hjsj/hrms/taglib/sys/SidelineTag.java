package com.hjsj.hrms.taglib.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
/**
 * 显示兼职
 * @author Owner
 *
 */
public class SidelineTag extends BodyTagSupport {
    private String a0100;
    private String nbase;
    private String part_setid;//兼职子集
    private String part_appoint;//兼职状态标识   
    private String part_pos;//兼职职务
    private String part_unit;//兼职单位
    private String part_dept;//兼职部门
    private String part_order;//兼职排序
    private String part_format;//兼职内容格式   
    
    public String getPart_unit() {
		return part_unit;
	}
	public void setPart_unit(String part_unit) {
		this.part_unit = part_unit;
	}
	public String getPart_appoint() {
		return part_appoint;
	}
	public void setPart_appoint(String part_appoint) {
		this.part_appoint = part_appoint;
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
	public String getPart_setid() {
		return part_setid;
	}
	public void setPart_setid(String part_setid) {
		this.part_setid = part_setid;
	}
	public int doEndTag() throws JspException 
    {
       	if(part_setid==null||part_setid.length()<=0)
    		return SKIP_BODY;    	
    	if(a0100==null||a0100.length()<=0)
    		return SKIP_BODY;
    	if(nbase==null||nbase.length()<=0)
    		return SKIP_BODY;    	
    	if(part_pos==null||part_pos.length()<=0)
    		return SKIP_BODY;
    	FieldItem fielitem=DataDictionary.getFieldItem(part_pos);
    	if(fielitem==null)
    		return SKIP_BODY;
    	String codesetid=fielitem.getCodesetid();
    	StringBuffer sql=new StringBuffer();
    	Connection conn=null;
    	RowSet rs=null;
    	sql.append("select "+part_pos+" as part_pos  ");
    	if(part_unit!=null&&part_unit.length()>0)
    		sql.append(","+part_unit+" part_unit ");
    	if(part_dept!=null&&part_dept.length()>0)
    		sql.append(","+part_dept+" part_dept ");
    	if(part_format!=null&&part_format.length()>0)
    		sql.append(","+part_format+" part_format ");
    	sql.append(" from "+nbase+part_setid+" where a0100='"+a0100+"'");
    	if(part_appoint!=null&&part_appoint.length()>0)
    		sql.append(" and "+part_appoint+"='0' ");
    	if(part_order!=null&&part_order.length()>0)
    		sql.append(" order by "+part_order);
		try{
		   conn=AdminDb.getConnection();
		   ContentDAO dao=new ContentDAO(conn);
		   rs=dao.search(sql.toString());
		   String pos="";
		   String unit="";
		   String un_value="";		
		   String um_value="";	
		   String format="";
		   StringBuffer buf=new StringBuffer();
		   while(rs.next())
		   {
			   pos=rs.getString("part_pos");
			   if(part_unit!=null&&part_unit.length()>0)//兼职单位
			   {
				   unit=rs.getString("part_unit");
				   un_value=AdminCode.getCodeName("UN",unit);
				   if(un_value==null||un_value.length()<=0)
					   un_value=AdminCode.getCodeName("UM",unit);
			   }
			   if(part_dept!=null&&part_dept.length()>0)//兼职单位
			   {
				   unit=rs.getString("part_dept");				   
				   um_value=AdminCode.getCodeName("UM",unit);
			   }
			   if(codesetid!=null&&codesetid.length()>0&&!"0".equals(codesetid))
			   {
				   pos=AdminCode.getCodeName(codesetid,pos);
			   }			  
			   if(pos!=null&&pos.length()>0)
			   {
				   buf.append("&nbsp;&nbsp;");
				   if(un_value!=null&&un_value.length()>0)
					   buf.append(un_value+"/");
				   if(um_value!=null&&um_value.length()>0)
					   buf.append(um_value+"/");
				   buf.append(""+pos+"&nbsp;");				  
				   if(part_format!=null&&part_format.length()>0)
				   {
					   format=rs.getString("part_format");
					   if(format!=null&&format.length()>=0)
					   {
						   buf.append(format.replaceAll("/n", "<br>"));
					   }				  
				   }
			   }
				
		   }
		   if(buf!=null&&buf.length()>0)
		   {
			   buf.setLength(buf.length());			   
			   JspWriter out=pageContext.getOut();
			   out.println("<br>");
			   out.println(buf.toString());
			   out.println("&nbsp;");
		   }
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
	public String getPart_pos() {
		return part_pos;
	}
	public void setPart_pos(String part_pos) {
		this.part_pos = part_pos;
	}
	public String getPart_dept() {
		return part_dept;
	}
	public void setPart_dept(String part_dept) {
		this.part_dept = part_dept;
	}
	public String getPart_order() {
		return part_order;
	}
	public void setPart_order(String part_order) {
		this.part_order = part_order;
	}
	public String getPart_format() {
		return part_format;
	}
	public void setPart_format(String part_format) {
		this.part_format = part_format;
	}
	
}
