package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;

public class ParttimeTag  extends BodyTagSupport {
	private HashMap part_map=new HashMap();
	private String a0100;
	private String nbase;
	private String name;	
	private String uplevel;
	private String scope;
	private String code;
	private String kind;
	private String b0110_desc="";
	private String e0122_desc="";
	private String part_desc="";
	private String descOfPart="";//zgd 2014-7-18 为了适应员工本身岗位为空，兼职不为空的情况。
	/* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
	/** 兼职信息(/unitCode,deptCode,akCode/unitCode,deptCode,akCode..) */
	private String partInfo;
	/** 部门编码 */
	private String deptCode;
	/** 单位编码 */
	private String unitCode;
	
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getPartInfo() {
		return partInfo;
	}
	public void setPartInfo(String partInfo) {
		this.partInfo = partInfo;
	}
	/* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
	public int doEndTag() throws JspException 
    {
		Object objb0110 = TagUtils.getInstance().lookup(this.pageContext, this.name, "b0110", this.scope);
    	String b0110="";
    	String e0122="";
    	if(objb0110!=null)
    	{
    		b0110=((String)objb0110).trim();
    	}
    	Object obje0122 = TagUtils.getInstance().lookup(this.pageContext, this.name, "e0122", this.scope);
    	if(obje0122!=null)
    	{
    		e0122=((String)obje0122).trim();
    	}
		if(part_map==null)
		{
			String un_name=AdminCode.getCodeName("UN",b0110);	
			this.pageContext.setAttribute(this.b0110_desc,un_name);			
			CodeItem item = null;
			if(this.uplevel!=null&&this.uplevel.length()>0)
			{
				   item=AdminCode.getCode("UM",e0122,Integer.parseInt(this.uplevel));
			}else
			{
				   item=AdminCode.getCode("UM",e0122);
			}
			if(item!=null)
				this.pageContext.setAttribute(this.e0122_desc,item.getCodename());
			else
				this.pageContext.setAttribute(this.e0122_desc,"");
			return SKIP_BODY;
		}	
		
		String part_setid="";
		String part_unit="";
		String appoint="";
		String flag="";
		String part_pos="";
		String part_dept="";
		String part_order="";
		String part_format="";
		
		if(part_map.get("flag")!=null && ((String)part_map.get("flag")).trim().length()>0)
			flag=(String)part_map.get("flag");
		
		if(part_map.get("setid")!=null && ((String)part_map.get("setid")).trim().length()>0) {
		    part_setid=(String)part_map.get("setid");		    
		}
		
		if(part_map.get("unit")!=null && ((String)part_map.get("unit")).trim().length()>0) {
		    part_unit=(String)part_map.get("unit");
		    if (!fieldExist(part_setid, part_unit)) {
		        part_unit = "";
            }
		}
		
		if(part_map.get("appoint")!=null && ((String)part_map.get("appoint")).trim().length()>0) {
		    appoint=(String)part_map.get("appoint");
		    if (!fieldExist(part_setid, appoint)) {
		        appoint = "";
            }
		}
		
		if(part_map.get("pos")!=null && ((String)part_map.get("pos")).trim().length()>0) {
		    part_pos=(String)part_map.get("pos");
		    if (!fieldExist(part_setid, part_pos)) {
		        part_pos = "";
            }
		}
		
		if(part_map.get("dept")!=null && ((String)part_map.get("dept")).trim().length()>0) {
		    part_dept=(String)part_map.get("dept");
		    if (!fieldExist(part_setid, part_dept)) {
		        part_dept = "";
            }
		}
		
		if(part_map.get("order")!=null && ((String)part_map.get("order")).trim().length()>0) {
		    part_order=(String)part_map.get("order");
		    if (!fieldExist(part_setid, part_order)) {
		        part_order = "";
            }
		}
		
		if(part_map.get("format")!=null && ((String)part_map.get("format")).trim().length()>0) {
		    part_format=(String)part_map.get("format");
            if (!fieldExist(part_setid, part_format)) {
                part_format = "";
            }
		}
		
		boolean isreturn=false;
		String itemid = part_pos;
		if(StringUtils.isEmpty(part_pos)) {
		    if(StringUtils.isEmpty(part_dept)) {
		        if(StringUtils.isNotEmpty(part_unit))
		            itemid = part_unit;
		    } else 
		        itemid = part_dept;
		}
		
		if(!"true".equalsIgnoreCase(flag))
			isreturn=true;   
		if(part_setid==null||part_setid.length()<=0)
			isreturn=true;    	
		
    	if(a0100==null||a0100.length()<=0)
    		isreturn=true;  
    	
    	if(nbase==null||nbase.length()<=0)
    		isreturn=true;  	
    	
    	if(StringUtils.isEmpty(itemid))
    		isreturn=true;  

    	if(appoint==null||appoint.length()<=0)
    		isreturn=true;  
    	
    	FieldItem fielitem=DataDictionary.getFieldItem(itemid);
    	if(fielitem==null)
    		isreturn=true;  
    	
    	if(isreturn)
    	{
    		String un_name=AdminCode.getCodeName("UN",b0110);	
			this.pageContext.setAttribute(this.b0110_desc,un_name);			
			CodeItem item = null;
			if(this.uplevel!=null&&this.uplevel.length()>0)
			{
				   item=AdminCode.getCode("UM",e0122,Integer.parseInt(this.uplevel));
			}else
			{
				   item=AdminCode.getCode("UM",e0122);
			}
			if(item!=null)
			{
				this.pageContext.setAttribute(this.e0122_desc,item.getCodename());				
			}else
				this.pageContext.setAttribute(this.e0122_desc,"");	
			return SKIP_BODY;
    	}
    	String codesetid=fielitem.getCodesetid();
    	
    	StringBuffer sql=new StringBuffer();
    	Connection conn=null;
    	RowSet rs=null;
    	boolean isview=false;
    	if(code!=null)
    	{
    		if("2".equals(kind))
    		{
    			if(code.indexOf(b0110)==-1&&b0110.indexOf(code)==-1)
    			{
    				isview=true;
    			}
    		}else if("1".equals(kind))
    		{	/* 标识：3081 信息浏览照片墙显示兼职信息 xiaoyun 2014-7-21 start */
    			if(StringUtils.isEmpty(e0122)) {
    				isview = true;
    			}
    			/* 标识：3081 信息浏览照片墙显示兼职信息 xiaoyun 2014-7-21 end */
    			if(code.indexOf(e0122)==-1&&e0122.indexOf(code)==-1)
    			{
    				isview=true;
    			}
    		}
    	}
		try{
			   conn=AdminDb.getConnection();
			   ContentDAO dao=new ContentDAO(conn);
			   sql.append("select ");
			   if(StringUtils.isNotEmpty(part_pos))
			       sql.append(part_pos+" as part_pos,");
			   
		       if(part_unit!=null&&part_unit.length()>0)
		    		sql.append(part_unit+" part_unit,");
		       if(part_dept!=null&&part_dept.length()>0)
		    		sql.append(part_dept+" part_dept,");
		       if(part_format!=null&&part_format.length()>0)
		    		sql.append(part_format+" part_format,");
		       
		       if(sql.toString().endsWith(","))
		           sql.setLength(sql.length() - 1);
		       
		       sql.append(" from "+nbase+part_setid+" where a0100='"+a0100+"'");
		       if(appoint!=null&&appoint.length()>0)
		    		sql.append(" and "+appoint+"='0' ");
		       if(part_order!=null&&part_order.length()>0)
		    		sql.append(" order by "+part_order);
			   rs=dao.search(sql.toString());
			   String pos="";
			   String unit="";
			   String dept="";
			   String un_value="";		
			   String um_value="";	
			   String format="";
			   String unit_code="";
			   String dept_code="";
			   int unit_len=0;
			   int dept_len=0;
			   StringBuffer buf=new StringBuffer();
			   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
			   StringBuffer partInfoTemp = new StringBuffer();
			   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
			   StringBuffer cufbuf=new StringBuffer();
			   CodeItem item = null;
			   //单位显示
			   if(unit_code!=null&&unit_code.length()>0)
			   {
				   item=AdminCode.getCode("UN",unit_code);
				   if(item==null)
				   {
					   item=AdminCode.getCode("UM",unit_code);
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-21 start */
				   if(StringUtils.isNotEmpty(this.unitCode)) {
					   this.pageContext.setAttribute(this.unitCode, unit_code);
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-21 end */
			   }
			   if(item==null)
			   {
				   item=AdminCode.getCode("UN",b0110);
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-21 start */
				   if(StringUtils.isNotEmpty(this.unitCode)) {
					   this.pageContext.setAttribute(this.unitCode, b0110);
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-21 end */
			   }
			   if(item!=null)
			   {
				   this.pageContext.setAttribute(this.b0110_desc, item.getCodename());
			   }else
			   {
				   this.pageContext.setAttribute(this.b0110_desc, "");
			   }
			   //部门显示
			   item = null;
			   if(dept_code!=null&&dept_code.length()>0)
			   {
				   if(this.uplevel!=null&&this.uplevel.length()>0)
				   {
					   item=AdminCode.getCode("UM",dept_code,Integer.parseInt(this.uplevel));					  
				   }else
				   {
					   item=AdminCode.getCode("UM",dept_code);
				   }	
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
				   if(StringUtils.isNotEmpty(this.deptCode)) {
					   this.pageContext.setAttribute(this.deptCode, dept_code);
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
			   }
			   if(item==null)
			   {
				   if(this.uplevel!=null&&this.uplevel.length()>0)
				   {
					   item=AdminCode.getCode("UM",e0122,Integer.parseInt(this.uplevel));
				   }else
				   {
					   item=AdminCode.getCode("UM",e0122);
				   }	
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
				   if(StringUtils.isNotEmpty(this.deptCode)) {
					   this.pageContext.setAttribute(this.deptCode, e0122);
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
			   }
			   if(item!=null)
			   {
				   this.pageContext.setAttribute(this.e0122_desc, item.getCodename());
			   }else
			   {
				   this.pageContext.setAttribute(this.e0122_desc, "");
			   }
			   
			   String formatTemp = "";
			   while(rs.next()) {
			       //兼职单位
				   if(part_unit!=null&&part_unit.length()>0) {
					   unit=rs.getString("part_unit");
					   if(unit!=null&&unit.length()>0) {
						   un_value=AdminCode.getCodeName("UN",unit);
						   if(un_value==null||un_value.length()<=0)
							   un_value=AdminCode.getCodeName("UM",unit); 
						   
						   if(isview) {
							   if(unit.indexOf(code)==0||code.indexOf(unit)==0) {								   
								   if(unit_len<unit.length()) {
									   if("2".equals(kind)&&unit.length()<=code.length()) {
										   unit_len=unit.length();
										   unit_code=unit;
									   } else {
										   unit_len=unit.length();
										   unit_code=unit;
									   }
									   
								   }
							   }
						   }
					   }					   
				   }
				   //兼职部门
				   if(part_dept!=null&&part_dept.length()>0) {
					   dept=rs.getString("part_dept");
					   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
					   partInfoTemp.append("/").append(dept).append(",");
					   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
					   if(dept!=null&&dept.length()>0) {
						   um_value=AdminCode.getCodeName("UM",dept);	
						   if(isview) {
							   if(dept.indexOf(code)==0||code.indexOf(dept)==0) {								   
								   if(dept_len<dept.length()) {
									   if("1".equals(kind)&&dept.length()<=code.length()) {
										   dept_len=dept.length();
										   dept_code=dept;
									   } else {
										   dept_len=dept.length();
										   dept_code=dept;
									   }
									   
								   }
							   }
						   }
					   }
				   }
				   
				   //兼职部门
                   if(StringUtils.isNotEmpty(part_pos)) {
                       pos=rs.getString("part_pos");
                       /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
                       partInfoTemp.append(pos);
                       /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
                       if(codesetid!=null&&codesetid.length()>0&&!"0".equals(codesetid))
                           pos=AdminCode.getCodeName(codesetid,pos);
                   }
                   
				   //缺陷1322 如果兼职单位和主集单位一样时，在员工管理信息浏览不应该显示兼职单位 zgd 2014-8-1 新增两变量为了判断兼职单位部门是否与主集单位部门一致
				   cufbuf.setLength(0);
				   if(un_value!=null&&un_value.length()>0){
				       //zgd 2014-8-1 当主集中单位与兼职单位不一致时，兼职岗位中才需添加单位
				       if(unit!=null&&!unit.equalsIgnoreCase(b0110)) 
				           cufbuf.append(un_value+"/");						  
				   }
				   
				   if(um_value!=null&&um_value.length()>0){
				       //zgd 2014-8-1 当主集中部门与兼职部门不一致时，兼职岗位中才需添加部门
				       if(dept!=null&&!dept.equalsIgnoreCase(e0122))
				           cufbuf.append(um_value+"/");						  
				   }
				   
				   if(pos!=null&&pos.length()>0)
					   cufbuf.append(pos);
				   
				   //zgd 2014-1-21 当单位、部门或岗位值都较多时，长度30不够用做处理后可能会出现乱码，改成60.
				   buf.append(PubFunc.reLineString(cufbuf.toString(), 90, "<br/>"));
				   if(part_format!=null&&part_format.length()>0) {
				       format=rs.getString("part_format");
				       if(format!=null&&format.length()>=0){
				           format = format.replaceAll("\\\\n", "<br/>");
				           formatTemp = format;
				           if(buf.length() > 0 && !buf.toString().endsWith(format))
				               buf.append(format);
				       }
				   }
					
			   }
			   
			   if(buf!=null&&buf.length()>0) {
				   //wangjl 2015-11-30 加入样式解决修改后页面不一致的问题
				   if(buf.toString().endsWith("<br/>"))
					   buf.setLength(buf.length()-5);
				   
				   if(buf.toString().endsWith(formatTemp))
				       buf.setLength(buf.length() - formatTemp.length());
				   
				   
				   this.pageContext.setAttribute(this.part_desc, "<div style='margin-top:-5px;line-height:16px;margin-bottom: 5px;'>"+buf.toString()+"</div>");//zgd 2014-7-5 兼职框间隔较大
				   this.pageContext.setAttribute(this.descOfPart, buf.toString());//zgd 2014-7-18 获取没有div样式的兼职信息
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
				   if(StringUtils.isNotEmpty(this.partInfo)) {
					   this.pageContext.setAttribute(this.partInfo, partInfoTemp.toString());
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
			   }else {
				   this.pageContext.setAttribute(this.part_desc, "");
				   this.pageContext.setAttribute(this.descOfPart, "");
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 start */
				   if(StringUtils.isNotEmpty(this.partInfo)) {
					   this.pageContext.setAttribute(this.partInfo, "");
				   }
				   /* 标识：3081 信息浏览中，有兼职情况的岗位应该把兼职情况的显示出来 xiaoyun 2014-7-18 end */
			   }
			}catch(Exception e) {
				e.printStackTrace();				
			} finally {
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
	public HashMap getPart_map() {
		return part_map;
	}
	public void setPart_map(HashMap part_map) {
		this.part_map = part_map;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}	
	public String getUplevel() {
		return uplevel;
	}
	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}
	public String getB0110_desc() {
		return b0110_desc;
	}
	public void setB0110_desc(String b0110_desc) {
		this.b0110_desc = b0110_desc;
	}
	public String getE0122_desc() {
		return e0122_desc;
	}
	public void setE0122_desc(String e0122_desc) {
		this.e0122_desc = e0122_desc;
	}
	public String getPart_desc() {
		return part_desc;
	}
	public void setPart_desc(String part_desc) {
		this.part_desc = part_desc;
	}
	public String getDescOfPart() {
		return descOfPart;
	}
	public void setDescOfPart(String descOfPart) {
		this.descOfPart = descOfPart;
	}
	
	/**
	 * 判断指标是否存在
	 * @param fieldSetId 子集
	 * @param field  指标
	 * @return
	 */
	private boolean fieldExist(String fieldSetId, String field) {
	    if(StringUtils.isEmpty(fieldSetId) || StringUtils.isEmpty(field))
	        return false;
	    
	    FieldItem item = DataDictionary.getFieldItem(field, fieldSetId);
        return null != item && !"0".equals(item.getUseflag());         
    }
}
