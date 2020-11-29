package com.hjsj.hrms.taglib.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.taglib.TagUtility;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.taglib.TagUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.util.Date;

public class PhotoViewInfoTag  extends BodyTagSupport {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4272603093187489929L;
	private String a0100;
    private String nbase;
    private String itemid;
    private String scope;
    private String name;
    /* 标识：2023 新版照片墙改动(每个指表项分别对应一个超链接) xiaoyun 2014-6-19 start */
    /** 请求参数(多个参数间用) */
    private String params;
    /** 是否为二维统计 true为是，其他为否 */
    private String isTowStatic;
    /* 标识：2023 新版照片墙改动(每个指表项分别对应一个超链接) xiaoyun 2014-6-19 end */
    /* 照片墙中的信息都放在这个标签里面生成（照片除外） xiaoyun 2014-7-1 start */
    /** 是否未设置照片显示指标（true为是，其他为否） */
    private String isNotSetQuota;
    /** 部门名 */
    private String departName;
    /** 岗位名 */
    private String jobName;
    /* 照片墙中的信息都放在这个标签里面生成（照片除外） xiaoyun 2014-7-1 end */
    /* 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 start */
    /** 兼职信息 */
    private String partInfo;
    /** 是否为信息浏览页面传来的请求（true为是，其他为否） */
    private String isInfoView;   
    /** 兼职部门编码 */
    private String deptCode;
    /** 兼职单位编码 */
    private String unitCode;
    /** 岗位编码 */
    private String jobCode;
    /* 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 end */
    
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public String getPartInfo() {
		return partInfo;
	}
	public void setPartInfo(String partInfo) {
		this.partInfo = partInfo;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
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
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public int doEndTag() throws JspException {
		JspWriter out = pageContext.getOut();
		/* 照片墙中的信息都放在这个标签里面生成（照片除外） xiaoyun 2014-7-1 start */
		if(StringUtils.isNotEmpty(isNotSetQuota) && StringUtils.equals(isNotSetQuota, "true")) {
			try{
				out.println(spliceHtml());
			} catch (Exception e) {
				e.printStackTrace();
			}
			jobName = "";
			return SKIP_BODY;
		}
		/* 照片墙中的信息都放在这个标签里面生成（照片除外） xiaoyun 2014-7-1 end */
		if(this.itemid==null||this.itemid.length()<=0)
			 return SKIP_BODY;
		if(scope==null|| "".equals(scope))
            scope="session";		
		/*Object value=TagUtility.getClassValue(this.pageContext,nbase);         
        if(value==null)
            return 0;
        nbase=(String)value;*/
        Object value=TagUtils.getInstance().lookup(pageContext,name,a0100,scope);
        if(value==null)
            return 0;
        a0100=(String)value;
        value=TagUtility.getClassValue(this.pageContext,itemid);         
        if(value==null)
            return 0;
        String itemidvalue[]=value.toString().split(",");      
        if(itemidvalue==null||itemidvalue.length==0)
        {
             return 0;
        }
        /* 是否为第一条记录（照片墙自定义显示指标，第一条记录需要特殊处理） xiaoyun 2014-7-4 start */
    	boolean isFirst;
    	/* 是否为第一条记录（照片墙自定义显示指标，第一条记录需要特殊处理） xiaoyun 2014-7-4 end */
        for(int v = 0; v < itemidvalue.length; v++){
        	 /* 是否为第一条记录（照片墙自定义显示指标，第一条记录需要特殊处理） xiaoyun 2014-7-4 start */
        	if(v == 0) {
        		isFirst = true;
        	}else {
        		isFirst = false;
        	} 
        	/* 是否为第一条记录（照片墙自定义显示指标，第一条记录需要特殊处理） xiaoyun 2014-7-4 end */
        	this.itemid = itemidvalue[v];      
        	FieldItem fielditem = DataDictionary.getFieldItem(itemid);
        	//指标在业务字典中不存在时，跳出循环
        	if(fielditem == null)
        	    continue;
        	
        	String setid = fielditem.getFieldsetid();
        	String sql = "";
        	if(setid != null && "A01".equalsIgnoreCase(setid))
        	{
        		sql = "select "+itemid+" itemid from "+this.nbase+setid+" where a0100='"+this.a0100+"'";
        	}else
        	{
        		sql = "select "+itemid+" itemid from "+this.nbase+setid+" where a0100='"+this.a0100+"' order by i9999 desc";
        	}
        	Connection conn=null;
        	try{
        		conn=AdminDb.getConnection();
        		ContentDAO dao=new ContentDAO(conn);
        		RowSet rs=dao.search(sql);
        		if(rs.next()){
        			out.println(spliceStr(rs, fielditem, isFirst));
        		}
        		
        	}catch(Exception e)
        	{
        		e.printStackTrace();
        		
        	}
        	finally
        	{
        		PubFunc.closeDbObj(conn);
        	}
        }
        jobName = "";
		return SKIP_BODY;
	}
	
	/**
	 * 组织要返回的html代码，未指定显示指标
	 * @return
	 * @author xiaoyun 2014-7-1
	 */
	private char[] spliceHtml() {
		StringBuffer html = new StringBuffer();
		String e0122_desc = (String)pageContext.getAttribute("e0122_desc");
		if(StringUtils.isEmpty(e0122_desc)) {
			e0122_desc = departName;
		}	
		/* 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 start */
		// 职务信息(包含岗位、兼职信息)
		String jobs = "";
		if(StringUtils.isNotEmpty(isInfoView) && StringUtils.equals("true", isInfoView)) {
			if(StringUtils.isNotEmpty(partInfo)){
				String[] parts = partInfo.split("/");
				if(StringUtils.isNotEmpty(deptCode)) {
					jobs = getJobNames(parts, this.deptCode);				
				}else {
					jobs = getJobNames(parts, this.unitCode);
				}
			}
		}
		/* 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 end */
		// 截取适应照片信息所在段落的字符串
		String splitDept = "";
		if(splitDept!=null && StringUtils.isNotEmpty(e0122_desc)) {
			splitDept = PubFunc.splitString(e0122_desc, 22);
			if(!splitDept.equals(e0122_desc)) {
				html.append("<p style='margin-top:-38px;' class='info' onmouseover=\"Tip('"+e0122_desc+"',STICKY ,true)\" onmouseout=\"UnTip()\">");
				//html.append("部门：");		
				splitDept = splitDept.substring(0, splitDept.length() - 1);
				splitDept = splitDept + "...";
				html.append(splitDept);
			}else {
				html.append("<p style='margin-top:-38px;' class='info'>");
				html.append(e0122_desc);
			}
			html.append("</p>");
		}
		String splitJob = "";
		if(StringUtils.isNotEmpty(jobCode)) {
			/* 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 start */
			if(StringUtils.isNotEmpty(isInfoView) && StringUtils.equals("true", isInfoView)) {
				if(StringUtils.isNotEmpty(jobs)) {
					if(StringUtils.isNotEmpty(this.deptCode)) {
						if(this.jobCode.startsWith(this.deptCode)){
							jobName = AdminCode.getCodeName("@K", jobCode);
							jobName += "、" + jobs;
						}else {
							jobName = jobs;
						}
					}else {
						if(this.jobName.startsWith(this.unitCode)){
							jobName = AdminCode.getCodeName("@K", jobCode);
							jobName += "、" + jobs;
						}else {
							jobName = jobs;
						}
					}
				}else{
					jobName = AdminCode.getCodeName("@K", jobCode);
				}				
			}
		}
		/* 标识：3081 照片墙增加兼职信息说明 xiaoyun 2014-7-17 end */
		if(jobName!=null)
			splitJob = PubFunc.splitString(jobName, 22);
		else
			jobName = "";
		if(!splitJob.equals(jobName)) {
			html.append("<p style='margin-top:-10px;' class='info' onmouseover=\"Tip('"+jobName+"',STICKY ,true)\" onmouseout=\"UnTip()\">");
			//html.append("<p style='margin-top:-22px;' class='info' onmouseover=\"Tip('"+jobName+"',STICKY ,true)\" onmouseout=\"UnTip()\">");
			//html.append("岗位：");				
			splitJob = splitJob.substring(0, splitJob.length() - 1);
			splitJob = splitJob + "...";
			html.append(splitJob);				
			html.append("</p>");
		}else {
			html.append("<p style='margin-top:-10px;' class='info'>");
			//html.append("<p style='margin-top:-22px;' class='info'>");				
			html.append(jobName);
			html.append("</p>");
		}
		
		return html.toString().toCharArray();
	}
	
	/**
	 * 拼接兼职职务字符串
	 * @param partInfos 兼职信息
	 * @param codeitemid 机构编码
	 * @return
	 * @author xiaoyun 2014-7-18
	 */
	private static String getJobNames(String[] partInfos, String codeitemid){		
		StringBuffer jobNames = new StringBuffer();
		for(int i = 0; i < partInfos.length; i++) {
			String partInfo = partInfos[i];	
			if(StringUtils.isEmpty(partInfo))
			    continue;
			
			String[] parts = partInfo.split(",");
			if(parts != null)
			if(parts[0].equals(codeitemid)) {
				jobNames.append(AdminCode.getCodeName("@K", parts[1])).append("、");
			}
		}
		if(jobNames.toString().endsWith("、")){
			jobNames.deleteCharAt(jobNames.length() - 1);
		}
		return jobNames.toString();
	} 
	/**
	 * 拼接要返回的html代码
	 * @param rs
	 * @param fielditem
	 * @param isFirst 是否为首条记录
	 * @return
	 * @author xiaoyun 2014-6-19
	 */
	private String spliceStr(RowSet rs, FieldItem fielditem, boolean isFirst) throws Exception {
		String codesetid=fielditem.getCodesetid();
		if(codesetid==null||codesetid.length()<=0) {
			codesetid="0";
		}
		// 初始化要返回的字符串
		StringBuffer html = new StringBuffer();
		// 得到js中的参数值
		String[] paramArr = null;
		// 段落标签<p>
		String pLabel = "";
		// 初始化onclick事件
		String onclick = "winhref(";
		// 拼接js函数调用
		if(StringUtils.isNotEmpty(params)) {
			paramArr = params.split(",");
			for (int i = 0; i < paramArr.length; i++) {
				String param = paramArr[i];
				if(i < (paramArr.length-1)) {
					onclick += "'"+param+"'"+",";
				} else {
					onclick += "'"+param+"'";
				}
			}
		}
		onclick += ")";
		if(isFirst) {
			pLabel = "<p style='margin-top:-5px;' class='info'>";
		}else {
			pLabel = "<p style='margin-top:-15px;' class='info'>";
		}
		
		if("0".equals(codesetid)) { // 一般类型
			if("D".equalsIgnoreCase(fielditem.getItemtype())) {
				Date date=rs.getDate("itemid");
				if(date!=null) {
					html.append(pLabel);
					html.append(DateUtils.format(date,"yyyy.MM.dd"));
					html.append("</p>");
				}
			} else{
				String value = rs.getString("itemid")!=null&&rs.getString("itemid").length()>0?rs.getString("itemid"):"";		
				html.append(generateHtml(value, onclick, pLabel));			
			}
		} else {
			String value = AdminCode.getCodeName(codesetid,rs.getString("itemid"));
			html.append(generateHtml(value, onclick, pLabel));
		} 
		return html.toString();
	}
	
	/**
	 * 组织html代码
	 * @param value 超链接中的值
	 * @param onclick 点击事件触发的方法
	 * @param pLabel 段落标签
	 * @return
	 * @author xiaoyun 2014-7-4
	 */
	private String generateHtml(String value,String onclick,String pLabel){
		StringBuffer html = new StringBuffer();
		html.append(pLabel);
		// 截取照片信息div一行可以显示的字符串			
		String temp = "";
		if(StringUtils.isNotEmpty(value)) {
			temp = PubFunc.splitString(value, 22);	
			boolean isEqual = temp.equals(value);
			if(!isEqual) {
				temp = temp.substring(0, temp.length()-1);
				temp = temp + "...";
			}
			if(StringUtils.equals(isTowStatic, "true")) {
				if(!isEqual) { // 超过页面能显示的字数，增加鼠标事件
					html.append("<a href='").append(params).append("' target='_self' onmouseover=\"Tip('" + value + "',STICKY ,true)\" onmouseout=\"UnTip()\" ").append(" >");
				} else {
					html.append("<a href='"+ params +"' target='_self'>");
				}
			} else {					
				if(!isEqual) { // 超过页面能显示的字数，增加鼠标事件
					html.append("<a href='###' target='_self' onclick=\"").append(onclick).append("\"").
						append(" onmouseover=\"Tip('" + value + "',STICKY ,true)\" onmouseout=\"UnTip()\" ").append(" >");
				} else {
					html.append("<a href='###' target='_self' onclick=\"").append(onclick).append("\">");
				}
			}
		}
		if("a0101".equalsIgnoreCase(itemid)) {
			html.append("<strong>").append(temp).append("</strong>");
		} else {
			html.append(temp);
		}
		html.append("</a>");
		html.append("</p>");
		return html.toString();
	}

	public String getIsTowStatic() {
		return isTowStatic;
	}
	public void setIsTowStatic(String isTowStatic) {
		this.isTowStatic = isTowStatic;
	}
	public String getDepartName() {
		return departName;
	}
	public void setDepartName(String departName) {
		this.departName = departName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getIsNotSetQuota() {
		return isNotSetQuota;
	}
	public void setIsNotSetQuota(String isNotSetQuota) {
		this.isNotSetQuota = isNotSetQuota;
	}
	public String getIsInfoView() {
		return isInfoView;
	}
	public void setIsInfoView(String isInfoView) {
		this.isInfoView = isInfoView;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getJobCode() {
		return jobCode;
	}
	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}
}
