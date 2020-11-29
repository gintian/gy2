<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.module.recruitment.resumecenter.actionform.ResumeForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="org.apache.commons.lang.StringUtils,com.hjsj.hrms.utils.PubFunc"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
ResumeForm resumeForm=(ResumeForm)session.getAttribute("resumeForm");
int length = resumeForm.getOthPos().size();
int length2 = resumeForm.getLastPos().size();
String resumeid1 = PubFunc.encrypt(resumeForm.getResumeid());
String zp_pos_id1 = PubFunc.encrypt(resumeForm.getZp_pos_id());
String nbase = PubFunc.encrypt(resumeForm.getNbase());
String flag = resumeForm.getFlag();
%>
<link rel="stylesheet" href="/module/recruitment/css/style.css" type="text/css" />
<link rel="stylesheet" href="/module/recruitment/css/stars.css" type="text/css" media="screen">
<script type="text/javascript" src="/module/recruitment/js/stars.js"></script>
<script type="text/javascript" src="/components/tableFactory/tableFactory.js"></script>
<script type="text/javascript" src="/module/recruitment/resumecenter/resumecenterlist/resumeInfo.js"></script>
<script src="/components/fileupload/FileUpLoad.js" type="text/javascript"></script>
<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />
<style>
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
.addButton{
width:66px;
height:24px;
border:none;
margin-top:8px;
color:#FFF;
float:right;
background:#529FE5;
	}
</style>
<head>
	<title>人员简历信息</title>
</head>
<script language="JavaScript">
    Global.resumeid = "<%=resumeid1 %>";
    Global.nbase = "<%=nbase %>";
    Global.username = "${resumeForm.username}";
    Global.email = "${resumeForm.email}";
    Global.from = "${resumeForm.from}";
    Global.current="${resumeForm.current }";
    Global.pagesize="${resumeForm.pagesize }";
    Global.schemeValues="${resumeForm.schemeValues }";
    Global.rowindex="${resumeForm.rowindex }";
	  
</script>
<body onload="setGlobalPos();">
<form action="" method="post" name="resumeForm"></form>
<input id="zp_pos_id" type="hidden" value="<%=zp_pos_id1 %>" />
<input type="hidden" value="<bean:write  name="resumeForm" property="username" filter="true"/>" id="username"/>
<div id="header" style="display:none;" class='hj-zm-hxr-three-right1'>
       <font face="微软雅黑" style="font-weight:bold;">候选人简历</font>
        &nbsp;&nbsp;&nbsp;&nbsp;
 	<strong><label><bean:write  name="resumeForm" property="username" filter="true"/></label></strong>&nbsp;&nbsp;&nbsp;
</div>
<div id="funcDiv" style="display:none">
<div class="hj-wzm-xq-all">
    	<div class="hj-zm-hxr-all" id="resumeDiv" style="margin-left:auto;margin-right:auto">
            	<logic:notEqual name="resumeForm" property="from" value="process">
            <div class="hj-zm-hxr-two">
            <%if(length2>0){ %>
            	<p>
            	<%if(length>0){ %>
            	第一志愿职位：
            	<%} %>
            	<logic:iterate id="pos"    name="resumeForm"  property="lastPos">
	                <bean:write  name="pos" property="position" filter="true"/>
					<logic:notEqual value="" name="pos" property="place">-</logic:notEqual>
	                <bean:write  name="pos" property="place" filter="true"/>
	                </input>
	                <input id="lastPosition" type="hidden" value='<bean:write  name="pos" property="position" filter="true"/>' />
		        </logic:iterate>
            	<br />
        <%if(length>0){ %>
                   其它志愿职位：
                   <%} %>
				<logic:iterate id="pos"    name="resumeForm"  property="othPos">
	                <bean:write  name="pos" property="position" filter="true"/>
	                <logic:notEqual value="" name="pos" property="place">-</logic:notEqual>
	                <bean:write  name="pos" property="place" filter="true"/>
	                </input>&nbsp;&nbsp;
		        </logic:iterate>
                </p>
                <%} %>
            </div>
           </logic:notEqual>
           <div class="hj-zm-hxr-three">
            
            
              <div class="hj-zm-hxr-three-top">
                
                 
                 <div class="bh-clear"></div>
                <div class="hj-zm-hxr-three-yi">
                <h2><img id="a01img" src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('a01')"/>&nbsp;<bean:message key="gz.report.baseinfomation"/></h2>
                <div id="a01" style="display:block;">
                <%
                ArrayList fieldSetList=resumeForm.getFieldSetList();	
				HashMap resumeBrowseSetMap=resumeForm.getResumeBrowseSetMap();
				HashMap setShowFieldMap=resumeForm.getSetShowFieldMap();
				String  zp_pos_id=resumeForm.getZp_pos_id();
				String resumeid=resumeForm.getResumeid();
				ArrayList a01InfoList=(ArrayList)resumeBrowseSetMap.get("a01");
				if(a01InfoList==null)
					a01InfoList=new ArrayList();
				out.print("<div style='float:left;width:70%;'>");
				out.print("<table width='100%' border='1' cellpadding='0' cellspacing='0' style='line-height:20px;margin-top:10px;padding-bottom:10px;padding-left:10px;'>");
				boolean newline = false;
	      		int count = 0;
	      		out.print("<tr><td colspan='2'>");
	      		for(int i=0;i<a01InfoList.size();i++)
		      	{
					LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String value=((String)abean.get("value")).trim();
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		if("a0101".equalsIgnoreCase(itemid))
		      			out.print("<span>"+value+"</span><br/>");
		      	}
	      		out.print("</td></tr>");
	      		out.print("<tr><td colspan='2'>");
				for(int i=0;i<a01InfoList.size();i++)
		      	{
					LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String value=((String)abean.get("value")).trim();
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		if("a0107".equalsIgnoreCase(itemid)){
		      			newline = true;
		      			if(StringUtils.isNotEmpty(value)) {
			      			if(count>0)
				      			out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
			      			
			      			out.print(value);
			      			count++;
		      			}
		      		}
		      		if("a0127".equalsIgnoreCase(itemid)){
		      			newline = true;
		      			if(StringUtils.isNotEmpty(value)) {
			      			if(count>0)
				      			out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
			      			
			      			out.print(value);
			      			count++;
		      			}
		      		}
		      		if("a0111".equalsIgnoreCase(itemid)){
		      			newline = true;
		      			if(StringUtils.isNotEmpty(value)) {
			      			if(count>0)
				      			out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
			      			count++;
			      			if(value != null && !"".equals(value)){
				      			String[] str = value.split("-");
				      			out.print(str[0]+"年"+str[1]+"月生");
			      			}
		      			}
		      		}
		      	}
				out.print("</td></tr>");
				int itemdescLen = 0;
				for(int i=0; i<a01InfoList.size(); i++){
					LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemdesc=(String)abean.get("itemdesc");
		      		if(itemdescLen < itemdesc.length())
		      			itemdescLen = itemdesc.length();
				}
				for(int i=1;i<fieldSetList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
					String setid=(String)abean.get("fieldSetId");
		      		ArrayList dataList=(ArrayList)resumeBrowseSetMap.get(setid.toLowerCase());
		      		ArrayList showFieldList=(ArrayList)setShowFieldMap.get(setid.toLowerCase());
		      		if(dataList==null)
	                   {
	                       continue;
	                   }
	                      
	               for(int n=0;n<dataList.size();n++)
	               {
	               		 if(n!=0)
	               		 {
		                 }
	                     if(showFieldList!=null)
	                     {
	         				for(int s=0; s<showFieldList.size(); s++){
	         					LazyDynaBean abean1=(LazyDynaBean)showFieldList.get(s);
	         		      		String itemdesc=(String)abean1.get("itemdesc");
	         		      		if(itemdescLen < itemdesc.length())
	         		      			itemdescLen = itemdesc.length();
	         				} 
	                     }
	               }
		      	}
		      	for(int i=0;i<5&&i<a01InfoList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String itemdesc=(String)abean.get("itemdesc");
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
					String value=((String)abean.get("value")).trim();
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		
		      		if(itemid != null && "A0101,A0107,A0111,A0127".contains(itemid.toUpperCase()))
		      		    continue;
		      		out.print("<tr><td style='white-space: nowrap;' align='right' width='"+itemdescLen*14+"px'>"+itemdesc+"：</td>");
           			out.print("<td>"+value+"</td></tr>");
		      		 
		      	}
		      	for(int i=5;i<a01InfoList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String itemdesc=(String)abean.get("itemdesc");
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
					String value=(String)abean.get("value");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		
		      		if(itemid != null && "A0101,A0107,A0111,A0127".contains(itemid.toUpperCase()))
		      		    continue;
		      		out.print("<tr><td style='white-space: nowrap;' align='right' width='"+itemdescLen*14+"px'>"+itemdesc+"：</td>");
                    out.print("<td>"+value+"</td></tr>");
	                
		      	}
		      	out.print("</table>");
		      	out.print("</div>");
		      	out.print("<div class='hj-zm-hxr-yi-right'>");
         		%>
        		<hrms:ole name="resumeForm" dbpre="${resumeForm.nbase}" a0100="resumeid" scope="session" height="120" width="85"/>
        		<%
        		out.print("</div>");
		      %>
		      </div>
		      </div>
		      <%
		      	for(int i=1;i<fieldSetList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
					String setid=(String)abean.get("fieldSetId");
		      		String setdesc=(String)abean.get("fieldSetDesc");
		      		ArrayList dataList=(ArrayList)resumeBrowseSetMap.get(setid.toLowerCase());
		      		ArrayList showFieldList=(ArrayList)setShowFieldMap.get(setid.toLowerCase());
	      			out.print("<div class='bh-clear'></div>");
	      			out.print("<div class='hj-zm-hxr-three-three'>");
		      %>
		      <h2><img id="<%=setid %>img" src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('<%=setid %>')"/>&nbsp;<%=setdesc%></h2>
		      <div id="<%=setid %>" style="display:block;">
		      <%
                   if(dataList==null)
                   {
                       continue;
                   }
                      
               for(int n=0;n<dataList.size();n++)
               {
               		 if(n!=0)
               		 {
	                 }
                     LazyDynaBean a_bean=(LazyDynaBean)dataList.get(n);	
                     
                   if(showFieldList!=null)
                   {
                	   out.println("<table width='100%' border='1' cellpadding='0' cellspacing='0' style='line-height:20px;margin-top:10px;padding-bottom:10px;padding-left:10px;'>");
               		for(int j=0;j<showFieldList.size();j++)
               		{
               			LazyDynaBean aa_bean=(LazyDynaBean)showFieldList.get(j);
               			String itemid=(String)aa_bean.get("itemid");
               			String itemtype=(String)aa_bean.get("itemtype");
               			String itemdesc=(String)aa_bean.get("itemdesc");
               			String itemmemo=(String)aa_bean.get("itemmemo");
               			String value=(String)a_bean.get(itemid);
               			itemmemo = itemmemo.replace("\r\n","<br>");
               			if(value==null||value.equals(""))
               			   value="&nbsp;";
               			out.println("<tr style='text-align:right;vertical-align:top;'>");
               			out.print("<td width='"+itemdescLen*14+"px'>");
               			out.print(itemdesc+"：");
               			out.print("</td>");
               			out.print("<td align='left'>");
	           			out.print(value);
	           			out.print("</td>");
	           			out.println("</tr>");
               		}
               		out.println("</table>");
          		   }
                   if(n!=dataList.size()-1)
                       out.println("<div style='border-bottom:1px #c5c5c5 dashed;'></div>");
                   
                }
       
              %>
               <%
               out.println("</div>");
			     }
			     %>
			     </div>
			 <div class="bh-clear"></div>
             <div class="hj-zm-hxr-three-yi">
			 <h2 style="margin-top: 10px"><img id="uploadimg" style="vertical-align:middle;" src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('upload')"/>&nbsp;<span style="vertical-align:middle;"><bean:message key="hire.resume.attach"/></span></h2>
             <div id="upload" style="display:block;" >
 				<TABLE  cellPadding=0 style="width:90%" align=center border=0 class="table" id="table">
 				<%
					String nodeid="";
					String createuser = "";
					String createtime = "";
					ArrayList uploadFIleList = resumeForm.getUploadFileList();
					for (int k = 0; k < uploadFIleList.size(); k++) {
						LazyDynaBean a_bean = (LazyDynaBean) uploadFIleList.get(k);
						String temnode = (String) a_bean.get("linkid");
						String title = (String) a_bean.get("title");//显示标题
						String createTime = (String) a_bean.get("createTime");//创建时间
						String createUser = (String) a_bean.get("createUser");//创建人
						String nodeName = (String) a_bean.get("nodename");//环节名称
						String imageUrl = (String) a_bean.get("imageUrl");//图片地址
						String id = (String) a_bean.get("id");//文件主键id
						String filePath = (String) a_bean.get("path");//文件绝对路径
						String filename = (String) a_bean.get("fileName");
						String encryptFileName = (String) a_bean.get("encryptFileName");
						String preview = (String)a_bean.get("preview");//是否可以预览
						String previews = (String)a_bean.get("previews");//是否有预览权限
						String seq = (String)a_bean.get("seq");//环节序号
						
						String tem = "";
						if("display:block;".equals(preview)&&"display:block;".equals(previews))
							tem = "";
						else
							tem = "display:none;";
						String download = (String)a_bean.get("download");//是否有下载权限
						String del = (String)a_bean.get("del");//是否有删除权限
						
						if(k==0){//个人简历
							out.println("<tr id='"+seq+"' name='newlink' style='padding-left:20px;'>");
							out.println("<td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+(String) a_bean.get("createUserName")+createTime+temnode+"'>");
							out.println("<span id='"+temnode+"'>"+nodeName+"</span>");
							out.println("<br/><div style='border-bottom:1px #c5c5c5 dashed;'></div><br/><div>"+title+"</div>");
							out.println("<ul id='"+id+"' name='aa'>");
							 out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;width:300px;overflow:hidden\">");
							 out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");
							 out.println("<a href='/DownLoadCourseware?url="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
							 out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile'  title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
										+(String) a_bean.get("fileSize")+ "&nbsp;&nbsp; <a href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;<a href='/DownLoadCourseware?url="+filePath+"' style='"+download+"'>下载</a>&nbsp;&nbsp;<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></li></ul>");
						}else{
							if(temnode.equals(nodeid)&&createUser.equals(createuser)&&createTime.equals(createtime)){//同一环节  同一人  上传时间相同
								out.println("<ul id='"+id+"' name='aa'>");
								 out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;padding-top:8px;width:300px;overflow:hidden\">");
								 out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");
								 out.println("<a href='/DownLoadCourseware?url="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								 out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile'  title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
											+ (String) a_bean.get("fileSize")+"&nbsp;&nbsp; <a href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;<a href='/DownLoadCourseware?url="+filePath+"' style='"+download+"'>下载</a>&nbsp;&nbsp;<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></ul></li>");
							}else if(!temnode.equals(nodeid)){//不是同一个环节
								out.println("</td></tr>");
								out.println("<tr  id='"+seq+"' name='newlink'>");
								out.println("<td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+(String) a_bean.get("createUserName")+createTime+temnode+"'>");
								out.println("<span id='"+temnode+"'>"+nodeName+"</span>");
								out.println("<br/><div style='border-bottom:1px #c5c5c5 dashed;'></div><br/><div>"+title+"</div>");
								out.println("<ul id='"+id+"' name='aa'>");
								 out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;width:300px;overflow:hidden\">");
								 out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");
								 out.println("<a href='/DownLoadCourseware?url="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								 out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile'  title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
											+(String) a_bean.get("fileSize")+ "&nbsp;&nbsp;<a href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;<a href='/DownLoadCourseware?url="+filePath+"' style='"+download+"'>下载</a>&nbsp;&nbsp;<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></li></ul>");
							}else{//同一个环节  上传人或上传日期不一致
								out.println("</td></tr>");
								out.println("<tr>");
								out.println("<td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+(String) a_bean.get("createUserName")+createTime+temnode+"'>");
								out.println("<br/>"+title);
								out.println("<ul id='"+id+"' name='aa'>");
								 out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;padding-top:8px;width:300px;overflow:hidden\">");
								 out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");
								 out.println("<a href='/DownLoadCourseware?url="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								 out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile' title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
											+ (String) a_bean.get("fileSize")+"&nbsp;&nbsp; <a href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;<a href='/DownLoadCourseware?url="+filePath+"' style='"+download+"'>下载</a>&nbsp;&nbsp;<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></ul></li>");
							}
						}
						nodeid=temnode;
						createuser = createUser;
						createtime = createTime;
					}%>
	      
                  	 </TABLE>
               	 </div>
               	 </div>
           	 <%if(!"1".equals(flag)){ %>
     			<h2 style="margin-top: 10px"><img id="evaluationimg" src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('evaluation')"/>&nbsp;简历评价</h2>
                 <div id="evaluation" style="display:block;padding-bottom: 20px;padding-left: 40px;">
					<table width="800px">
                 		<tr>
                 			<td style="vertical-align: middle;" align="left" width="80px">
								<span>我的评价</span>
							</td>
                 			<td align="left" width="720">
									<html:hidden styleId="score" name="resumeForm" property="evaluationBean.score" />
									<div style="float: left;width: 250px;"><span id="starlist" style="display: inline"></span></div>
									<div style="padding-right: 10px;padding-top: 3px;" id="textMsg">
										<span>&nbsp;<a href="javascript:void(0);" onclick="Global.ReEvaluation()" style="font-family: '微软雅黑';">重新评价</a></span>
									</div>
                 			</td>
                 		</tr>
						<tr>
							<td style="margin-bottom: 5px" valign="top" align="left" width="80">&nbsp;</td>
							<td align="left" style="vertical-align: middle;width: 720px;">
								<div style="width: 100%" id="div_r">
									<div id="content_r" style="width: 100%;margin-top:8px;padding:0;word-break: break-all;word-wrap: break-word;">
										<bean:define id="mycontent" name="resumeForm" property="evaluationBean.content"></bean:define>
										<%
											String content = (String)mycontent;
										%>
										<%=content %>
									</div>
								</div>
								<div style="width: 100%;" id="div_w">
									<div>
										<textarea id="addContent" rows="" cols="" style="width: 100%; height: 100px; margin-left: 0px; word-break: break-all; overflow: auto; margin-bottom: 10px;border:1px #c5c5c5 solid;"></textarea>
									</div>
									<div style="float: right;">
										<input type="button" class="addButton" value="发布评价" onclick="Global.addEvaluation()" style="cursor: pointer;" />
									</div>
								</div>
							</td>
						</tr>
					</table>
                 </div>
               <%} %>
             </div>   
        </div>
    </div>
    </div>
    </div>
    
</body>
<script type="text/javascript">
Ext.onReady(function(){
    Ext.widget('viewport',{
    	layout:'fit',
    	padding:"0 5 0 5",
    	style:'backgroundColor:white',
    	items:[{
    			  xtype:'panel',
    			  id:'view_panel',
    			  title:"<div id='headPanel'></div>",
    			  html:"<div id='topPanel'></div>",
    			  border:false
    			}]
    });
    document.getElementById('headPanel').appendChild(document.getElementById('header'));
    document.getElementById('header').style.display="block";
    document.getElementById('topPanel').appendChild(document.getElementById('funcDiv'));
    document.getElementById('funcDiv').style.display="block";
    var view_panel = Ext.getCmp('view_panel');
    view_panel.setAutoScroll(true);
    var winHeight =parent.document.body.clientHeight;
    view_panel.setHeight(winHeight);
  //设置评价显示
	var score = '<bean:write name="resumeForm" property="evaluationBean.score"/>';
	if("1"!=<%=flag%>){
		if(score==0)
		{ 
			Ext.getDom('score').value="-1";
			Ext.getDom("div_r").style.display="none";
			Ext.getDom("div_w").style.display="block";
			Ext.getDom("textMsg").style.display="none";
		}else{ 
			Ext.getDom("div_r").style.display="block";
			Ext.getDom("div_w").style.display="none";
		}
			initstar('starlist'); 	
	}
});	
</script>
