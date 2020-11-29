<%@page import="com.hrms.frame.utility.AdminDb"%>
<%@page import="com.hrms.frame.dao.ContentDAO"%>
<%@page import="com.hjsj.hrms.businessobject.sys.ConstantXml"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.frame.dao.utility.DateUtils" %>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.MyLessonBo"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainCourseBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<style>
body{text-align: center;}
.tbl-container
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
}
.t_cell_locked 
{
	border: inset 1px #C4D8EE;
	BACKGROUND-COLOR: #ffffff;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-size: 12px;
	border-collapse:collapse; 
	
	background-position : center left;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	position: relative;
	z-index: 10;
	
}
.t_cell_locked_b {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-size: 12px;
	border-collapse:collapse; 
}

.t_header_locked
{
	/*background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	background-color:#f4f7f7;
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	valign:middle;
	font-weight: bold;	
	text-align:center;
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 15;
}
	 		
.t_cell_locked2 
{
	/*  background-image:url(/images/listtableheader_deep-8.jpg);*/
	background-repeat:repeat;
	background-position : center left;
	background-color:#f4f7f7;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt;
	font-weight: bold;	
	valign:middle;
	left: expression(document.getElementById("tbl-container").scrollLeft); /*IE5+ only*/
	top: expression(document.getElementById("tbl-container").scrollTop); /*IE5+ only*/
	position: relative;
	z-index: 20;
	
}
</style>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<script type="text/javascript">
<!--
	//查询
	function valueChange() {
	    myUploadCourseForm.action="/train/resource/myupload/myuploadcourse.do?b_query=link";
		myUploadCourseForm.submit();
	}
	
	// 敲击enter键时提交
	function handleKeyDown(event) {
		if (event.keyCode == 13) {
			myUploadCourseForm.submit();
		}
	}
	
	// 上传
	function upload() {
		var request=new Request({method:'post',asynchronous:true,onSuccess:docan,functionId:'202003010101'},"");		
	}
	function docan(outparamters){
		var param=outparamters.getValue("check");
		if(param == "yes"){
			myUploadCourseForm.action="/train/resource/myupload/myuploadcourse.do?b_upload=link";
			myUploadCourseForm.submit();
		}else{
			alert("DIY课程类别不允许为空!");
		}
	}
	//删除上传课程
	function del(){
		var s = document.getElementsByName("r5022");
		var courses = document.getElementsByName("r5000");
		var course="";
		for(var i = 0 ; i < courses.length; i++){
			if(courses[i].checked){
				if(s[i].value == "01" || s[i].value == "07"){
					course += courses[i].value + ",";
				}
			}
		}
		if(course != "" && course.length > 0){
			if(course != null && confirm("确认要删除吗?")){
				var hashvo = new ParameterSet();
				hashvo.setValue("course", course.substring(0, course.length - 1));
				var request = new Request({method:"post", asynchronous:false, onSuccess:show, functionId:"2020030105"}, hashvo);
			}
		}else{
			alert("请选择要删除的记录!");
			return null;
		}
	}
	
	//报批
	function appeal(){
		var s = document.getElementsByName("r5022");
		var courses = document.getElementsByName("r5000");
		var course = "";
		for(var i = 0 ; i < courses.length ; i++){
			if(courses[i].checked){
					if(s[i].value == "01"){
						course += courses[i].value + ",";			
					}else if(s[i].value == "07"){
						alert("只能对起草状态的课程进行报批");
						return null;
					}
				}
		}
		if(course != "" && course.length > 0){
			if(course != null && confirm("确认要报批吗?")){
				var hashvo = new ParameterSet();
				hashvo.setValue("course",course.substring(0,course.length-1));
				var request = new Request({method:"post", asynchronous:false, onSuccess:show, functionId:"2020030106"}, hashvo);
			}
		}else{
			alert("请选择要报批的课程!");
			return null;
		}
	}
	function show(){
	document.forms[0].action ="/train/resource/myupload/myuploadcourse.do";
	  document.forms[0].submit();
	}
	
	//浏览
	function shows(courseid,classes) {
	var map = new HashMap();
	map.put("r5000",courseid);
	Rpc({functionId:'2020030198'},map);
		
	var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=my`classes="+classes+"`lesson=" + courseid;
	var fram = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode(url);
	window.open(fram,"learnwindow",'fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
		
	}
	function showcourse(courseid,classes,coursetype) {
		var url = "/train/resource/mylessons/learncoursebyextjs.jsp?opt=sss`classes="+classes+"`lesson=" + courseid +"`coursetype="  +coursetype;
		var fram = "/train/resource/mylessons/learniframe.jsp?src="+url;
		window.open(fram,'','fullscreen=yes,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
}
//-->
</script>
<html:form action="/train/resource/myupload/myuploadcourse">
	<html:hidden name="myUploadCourseForm" property="diyType" />
	<%
		int i = 0;
	%>
	<table style="width:100%;border:0;padding:0 4px;" cellpadding="0" cellspacing="0" >
	<tr>
		<td style="padding-bottom:6px"><bean:message key="train.resource.course.myupload.coursename"/>&nbsp;&nbsp;<html:text name="myUploadCourseForm" property="courseName" onchange="valueChange();" onkeydown="handleKeyDown(event);" styleClass="text4" styleId="courseName"></html:text></td>
	</tr>
	<tr>
	<td>
	<table cellspacing="0" align="center" cellpadding="0" style="width:100%;border:1px solid #c4d8ee;border-collapse: collapse;">
		<thead>
			<tr><!-- 全选 -->
				<td align="center" class="TableRow" style="border-left-width: 0px;border-top: none;" width="5%">
					<input type="checkbox" name="selbox" onclick="batch_select(this,'r5000');" title='<bean:message key="label.query.selectall"/>' />
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap width="20%">
						<bean:message key="train.resource.course.myupload.coursename"/>
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap width="35%">
						<bean:message key="train.resource.course.myupload.coursedesc"/>
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap width="20%">
						<bean:message key="train.resource.course.myupload.uploadtime"/>
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap width="10%">
						<bean:message key="train.resource.course.myupload.status"/>
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap width="10%" style="border-right-width: 0px;">
						<bean:message key="train.resource.course.myupload.browse"/>
				</td>
				
			</tr>
		</thead>
		<hrms:paginationdb id="element2" name="myUploadCourseForm"
			sql_str="myUploadCourseForm.sql" table="" where_str="myUploadCourseForm.strWhere"
			columns="myUploadCourseForm.columns" page_id="pagination"
			pagerows="${myUploadCourseForm.pagerows}" order_by="myUploadCourseForm.orderBy">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onclick="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" onclick="javascript:tr_onclick(this,'E4F2FC')">
				<%
					}
								i++;
				%>
				<bean:define id="idid" name="element2" property="r5000" />
				<bean:define id="classes" name="element2" property="r5022"/>
				<%String r5000 = SafeCode.encode(PubFunc.encrypt(idid.toString())); 
				  String r5022 = SafeCode.encode(PubFunc.encrypt(classes.toString())); %>
				
				<td align="center" class="RecordRow" nowrap style="border-left-width: 0px;">
					<logic:equal  name="element2" property="r5022" value="01">
					  <input type="checkbox" name="r5000" value='<%=r5000 %>' title=''/>
					</logic:equal>
					<logic:equal  name="element2" property="r5022" value="07">
					  <input type="checkbox" name="r5000" value='<%=r5000 %>'' title=''/>
					</logic:equal>
					<logic:notEqual  name="element2" property="r5022" value="01">
					  <logic:notEqual  name="element2" property="r5022" value="07">
					    <input type="checkbox" name="r5000" value='<%=r5000 %>'' title='' style='display:none';/>
					  </logic:notEqual>
					</logic:notEqual>
				</td>
				
				<td align="left" class="RecordRow" nowrap>
					<!--&nbsp;<bean:write name="element2" property="r5003"/>&nbsp;	-->
						<bean:define id="names" name="element2" property="r5003" />
						&nbsp;<%=names%>&nbsp;
				</td>
				<%String r5012 = TrainCourseBo.getr5012(r5000); %>
				<td align="left" class="RecordRow" title="<%=r5012 %>" nowrap>
					<bean:define id="desc" name="element2" property="r5012" />			
					<!--&nbsp;<bean:write name="element2" property="r5012"/>&nbsp;	-->
					&nbsp;<%=desc%>&nbsp;				
				</td>
				<td align="left" class="RecordRow" nowrap>
					
					<bean:define id="time" name="element2" property="create_time"></bean:define>
					<!-- &nbsp;<bean:write name="element2" property="create_time" filter="true" format="yyyy-MM-dd"/>-->
					<%				
									String d = "";
									if(time.toString().length() >= 18 ){										
										Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(time.toString().replace(".","-")); 									
										 d = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
									}else{
										Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(time.toString().replace(".","-"));
										d = new SimpleDateFormat("yyyy-MM-dd").format(date1);
									}
					 %>
					&nbsp;<%=d%>&nbsp;
						
				</td>
				<td align="center" class="RecordRow" nowrap>
				<input type="hidden" name="r5022" value = '<bean:write name="element2" property="r5022"/>' />
 					<hrms:codetoname codeid="23" name="element2" codevalue="r5022" codeitem="codeitem" scope="page" />  	      
       	    	   					 &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;					
				</td>
				<td align="center" class="RecordRow" nowrap style="border-right-width: 0px;">
				<% if(MyLessonBo.checkShow(idid.toString())){ %>
					&nbsp;<a href="javascript:;" onclick="shows('<%=r5000 %>','<%=r5022 %>','<bean:write name="element2" property="r5105"/>');"><bean:message key="train.resource.course.myupload.browse"/></a>
				<%} %>
				</td>						
			</tr>
		</hrms:paginationdb>
	</table>
	</td>
	</tr>
	<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="myUploadCourseForm"
								pagerows="${myUploadCourseForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="myUploadCourseForm"
									property="pagination" nameId="myUploadCourseForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>			
			<td align="left" style="padding-top: 10px;">	
	
				<input type="button" class="mybutton" value='<bean:message key="lable.fileup" />' onclick="upload();" />
		   		<input type="button" class="mybutton" value="<bean:message key="button.appeal" />" onclick="appeal();"/>		    
				<input type="button" class="mybutton" value='<bean:message key="button.delete" />' onclick="del();"/>				
			</td>
		</tr>
	</table>
</html:form>