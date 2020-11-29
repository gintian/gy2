<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="java.util.*,
				 com.hrms.struts.constant.SystemConfig,
                 com.hjsj.hrms.actionform.performance.singleGrade.SingleGradeForm,
                 com.hjsj.hrms.utils.ResourceFactory" %>
<%

String file_max_size="512";
if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
{
	file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
	if(file_max_size.toLowerCase().indexOf("k")!=-1)
	file_max_size=file_max_size.substring(0,file_max_size.length()-1);
}

UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String optUrl="";
if(request.getParameter("optUrl")!=null)
	optUrl=request.getParameter("optUrl");
SingleGradeForm singleGradeForm=(SingleGradeForm)session.getAttribute("singleGradeForm");
String isnullAffix=singleGradeForm.getIsnullAffix();
if(singleGradeForm.getPerformanceType()==null)
	singleGradeForm.setPerformanceType("0");
String s_rejectCause=singleGradeForm.getS_rejectCause();
String g_rejectCause=singleGradeForm.getG_rejectCause();
String state="0";
if(optUrl!=null&&(optUrl.equalsIgnoreCase("goal")||optUrl.equalsIgnoreCase("goal2")))
	state=singleGradeForm.getGoalState();
else
	state=singleGradeForm.getSummaryState();
String desc="未提交";
if(state.equals("1"))
	desc="已提交";
else if(state.equals("2"))
	desc="已批准";
else if(state.equals("3"))
	desc="驳回";
String model=singleGradeForm.getModel();

	

%>
<!--[if !IE]><!--> 
<style type="text/css">
	.inputtext{
		border:none !important;
	}
</style>
<!--<![endif]-->
<style>
.TEXT_NB {
	BACKGROUND-COLOR:transparent;
	
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}
.textarea1{
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
}
</style>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language='javascript'>
	function b_return()
	{
		<% if((model!=null&&model.equals("3"))||(optUrl!=null&&optUrl.indexOf("2")!=-1)){ %>
		document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_query2=b_query2";
		<% }else{ %>
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_query2=b_query2&operate=selfgrade";
		<% }  %>
		document.singleGradeForm.submit();
	}
	
	
	
	function validateSize(opt)
    {
          var f_obj;
          var fileurl;
          if(opt==1)
          {
	          f_obj=document.getElementsByName("file");
	          fileurl=document.singleGradeForm.file.value;
	      }
    	  else if(opt==4)
    	  {
    	      f_obj=document.getElementsByName("goalfile");
    	      fileurl=document.singleGradeForm.goalfile.value;
    	  }
          if(f_obj)
          {          	
          	// 防止上传漏洞
			var isRightPath = validateUploadFilePath(fileurl);
			if(!isRightPath)	
				return false;
          
          /*
             var value=f_obj[0].value;            
             var photoEx=value.substring(value.lastIndexOf(".")); 
             photoEx=photoEx.toLowerCase();
             var  obj=document.getElementById('FileView'); 
             if (obj != null)
             {
                obj.SetFileName(value);
                var facSize=obj.GetFileSize();                  
                var  photo_maxsize="<%=(file_max_size)%>"   
                if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024)
                {  
                   
                   alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
                   return false;
                }     
             }
             */
          }
          return true;
       }
	
	 
	
	
	function upload(opt)
	{	/// zzk 2014/2/7  上传附件名称长度不得大于50字节
		if(opt==1&&document.singleGradeForm.file.value!=null){
			var temp_url=document.singleGradeForm.file.value;
		    var fielName =temp_url.substring(temp_url.lastIndexOf ("\\")+1);
		    var fielNameLength=fielName.length + fielName.replace(/[\u0000-\u00ff]/g, "").length;
			if(fielNameLength>50){
				alert("附件名称长度不能超过50字节(一个汉字占两字节)!");
				return;
			}
		}
		var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
		if (userAgent.indexOf("Safari") > -1)
		{
			upload2(opt);
		}
		else
		{
			if(opt==1||opt==4)
		     {
		     	if(!validateSize(opt)){
		     		// xus 19/12/17 校验失败时清空文件输入框 【55434】
		     		document.getElementsByName('file')[0].value = '';
		     		return;
		     	}
		     }
		
		
			if(document.singleGradeForm.summary&&document.singleGradeForm.summary.value.length==0)
				document.singleGradeForm.summary.value=" ";
			if(document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
				document.singleGradeForm.goalContext.value=" ";	
			if(opt==7||opt==8)
			{
				if(!confirm("提交后将不能再编辑,请确认提交?"))
				{
					return;
				}
			}	
			
			if((opt==1&&trim(document.singleGradeForm.fileName.value).length==0)||(opt==4&&trim(document.singleGradeForm.goalfileName.value).length==0))
			{
				if(opt==4)
				{	
					var temp_url=document.singleGradeForm.goalfile.value;
					document.singleGradeForm.goalfileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
					
				}
				else
				{
					var temp_url=document.singleGradeForm.file.value;
				   document.singleGradeForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
				   
				} 
			}
			

		 
			if(opt==1)
				document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile=save&optUrl=<%=optUrl%>&opt="+opt;
			else if(opt==2||opt==5||opt==7||opt==8)
			{
				<% if((model!=null&&model.equals("3"))||(optUrl!=null&&optUrl.indexOf("2")!=-1)){ %>
				document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_saveSummary4=save&optUrl=<%=optUrl%>&opt="+opt;
				<% }else { %>
				document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_saveSummary3=save&optUrl=<%=optUrl%>&opt="+opt;
				<% } %>
			}
			else if(opt==4)
				document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile=save&optUrl=<%=optUrl%>&opt="+opt;
			document.singleGradeForm.submit();
		}
	     
	}
	
	function upload2(opt)
	{
	
	     if(opt==1||opt==4)
	     {
	     	if(!validateSize(opt)){
	     		// xus 19/12/17 校验失败时清空文件输入框 【55434】
	     		document.getElementsByName('file')[0].value = '';	     		
	     		return;
	     	}
	     }
	
	
		if(document.singleGradeForm.summary&&document.singleGradeForm.summary.value.length==0)
			document.singleGradeForm.summary.value=" ";
		if(document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
			document.singleGradeForm.goalContext.value=" ";	
		if(opt==7||opt==8)
		{
			if(!confirm("提交后将不能再编辑,请确认提交?"))
			{
				return;
			}
		}	
		
		if((opt==1&&trim(document.singleGradeForm.fileName.value).length==0)||(opt==4&&trim(document.singleGradeForm.goalfileName.value).length==0))
		{
			if(opt==4)
			{	
				var temp_url=document.singleGradeForm.goalfile.value;
				document.singleGradeForm.goalfileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
				
			}
			else
			{
				var temp_url=document.singleGradeForm.file.value;
			   document.singleGradeForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
			   
			} 
		}
		
	 
		if(opt==1)
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile=save&optUrl=<%=optUrl%>&opt="+opt;
		else if(opt==2||opt==5||opt==7||opt==8)
		{
			<% if((model!=null&&model.equals("3"))||(optUrl!=null&&optUrl.indexOf("2")!=-1)){ %>
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_saveSummary4=save&optUrl=<%=optUrl%>&opt="+opt;
			<% }else { %>
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_saveSummary3=save&optUrl=<%=optUrl%>&opt="+opt;
			<% } %>
		}
		else if(opt==4)
			document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile=save&optUrl=<%=optUrl%>&opt="+opt;
		document.singleGradeForm.submit();
	}
	
	function del(article_id,plan_id)
	{
		if(!confirm("请确认执行删除操作?"))
		{
			return;
		}
		if(document.singleGradeForm.summary&&document.singleGradeForm.summary.value.length==0)
			document.singleGradeForm.summary.value=" ";
		if(document.singleGradeForm.goalContext&&document.singleGradeForm.goalContext.value.length==0)
			document.singleGradeForm.goalContext.value=" ";	
	<% if(optUrl.equals("summary")||optUrl.equals("summary2")){ %>	
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile=save&optUrl=<%=optUrl%>&plan_id="+plan_id+"&article_id="+article_id+"&opt=3";
		document.singleGradeForm.submit();
	<% }else{ %>
		document.singleGradeForm.action="/selfservice/performance/selfGrade.do?b_extrafile=save&optUrl=<%=optUrl%>&article_id="+article_id+"&opt=6";
		document.singleGradeForm.submit();
	<% } %>
		
		
	}
	
	
	/**author:zangxj *day:2014-06-07 *绩效模板文件下载按钮 */
	function downAffix(){
		var hashvo=new ParameterSet();
		hashvo.setValue("opt","down");
		hashvo.setValue("plan_id","${singleGradeForm.dbpre}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'90100160022'},hashvo);
	}	
	function showFieldList(outparamters){
		var outName=outparamters.getValue("outname");
		var isnullAffix=outparamters.getValue("isnullAffix");
		if(isnullAffix=="null"){
		alert("没有上传模板");
			return;
		}
		if(outName!=null&&outName.length>1)
			window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
	
</script>

<form name="singleGradeForm" method="post" action="/selfservice/performance/selfGrade.do" enctype="multipart/form-data" >
	<input type="hidden" name="errorMsg" value="${singleGradeForm.errorMsg }" />
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top：10px;">
          <tr height="20">
       		<td class="TableRow" width='100%' style="BORDER-BOTTOM: 0pt solid;">
       	
	      	<% if(optUrl.equals("summary")||optUrl.equals("summary2")){ %>
	       		<logic:equal name="singleGradeForm" property="performanceType" value="0">
	       		<%
	       		    String info=SystemConfig.getPropertyValue("per_examineInfo");
					if(info==null||info.length()==0)
					{			 
					%>
					<bean:message key="lable.performance.perSummary"/>
					<%
					}		
					else
						out.print(info);
								 
	       		%>
	       			
				</logic:equal>
				<logic:equal name="singleGradeForm" property="performanceType" value="1">
					<bean:message key="lable.performance.personalReport"/>
				</logic:equal>
				<% } else { %>
					<bean:message key="lable.performance.perGoal"/>
				<% } %>
				
				(<%=desc%>)
       		&nbsp;</td>
       		       	      
          </tr> 
          <tr>
            <td   class="framestyle">
		               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
		                      <tr class="list3">
		                	  	 <td align="left" nowrap >
		                	  	 	<% if(optUrl.equals("summary")||optUrl.equals("summary2")){ 
		                	  	 		String agent = request.getHeader("USER-AGENT").toLowerCase(); 
		                	  	 		if(agent.indexOf("safari")!=-1){
		                	  	 	%>
		                	  	 	<html:textarea name="singleGradeForm" property="summary" cols="90" rows="25"  styleClass="textarea1 common_border_color"/>
		                	  	 	<%}else{ %>
		                 	     <html:textarea name="singleGradeForm" property="summary" cols="90" rows="25"   styleClass="textarea1 common_border_color"  /><!-- 去掉了编辑器 -->

		                          	<%}} else { 
		                          		String agent = request.getHeader("USER-AGENT").toLowerCase(); 
		                          		if(agent.indexOf("safari")!=-1){
		                          	%>
		                          	<html:textarea name="singleGradeForm" property="goalContext"  cols="90" rows="25"  styleClass="textarea1 common_border_color"/>
		                          	<%}else{ %>
		                          	<html:textarea name="singleGradeForm" property="goalContext"  cols="90" rows="25"  styleClass="textarea1 common_border_color"/><!-- 去掉了编辑器 -->
		                            
		                            <% }} %>
		                          </td>
		                      </tr>
		                      
		                       <logic:equal name="singleGradeForm" property="allowUploadFile" value="true">
		                      <tr class="list3">
		                	  	 <td align="left"  valign='top' nowrap ><Br>
			                 	     	<table border=0 >
			                 	     	<tr><td valign='top'>
			                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
			                 	   		  </td>
			                 	   		  </tr><tr>
			                 	   		  <td valign='top' >
			                 	   		  
			                 	   		  <table border="0" cellpmoding="0" cellspacing="0" cellpadding="0"  >
			                 	   		  <% if(optUrl.equals("summary")||optUrl.equals("summary2")){ %>
			                 	   		  <logic:iterate id="element" name="singleGradeForm" property="summaryFileIdsList" >
			                 	   		  	<tr><td>&nbsp;&nbsp;
			                 	   		  	<a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
			                 	   		  	<bean:write name="element" property="name" />
			                 	   		  	</a>
			                 	   		  	</td><td>
			                 	   		  	<% if(state.equals("0")||state.equals("3")){ %>
			                 	   		  	 &nbsp;<a href="javascript:del(<bean:write name="element" property="id" />,${singleGradeForm.dbpre})">
			                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
			                 	   		  	</a>
			                 	   		  	<% } %>
			                 	   		   	</td></tr>
			                 	   		  </logic:iterate>
			                 	   		  <% } else { %>
			                 	   		  <logic:iterate id="element" name="singleGradeForm" property="goalFileIdsList" >
			                 	   		   	<tr><td>&nbsp;&nbsp;
			                 	   		   	 <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
			                 	   		   	<bean:write name="element" property="name" />
			                 	   		   	 </a>
			                 	   		   	</td><td>
			                 	   		   
			                 	   		  	<% if(state.equals("0")||state.equals("3")){ %>
			                 	   		  	 &nbsp;<a href="javascript:del(<bean:write name="element" property="id" />,${singleGradeForm.dbpre})">
			                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
			                 	   		  	</a>
			                 	   		  	<%} %>
			                 	   		    </td></tr>
			                 	   		  </logic:iterate>
			                 	   		  <% } %>
			                 	   		  </table>
		                 	     	  </td>
		                 	     	  </tr></table>
		                 	     	  
		                          </td>
		                      </tr>
		                      
		                      <tr class="list3">
		                	  	 <td align="left" nowrap ><Br>
		                 	 <% if(optUrl.equals("summary")||optUrl.equals("summary2")){ 
		                 	 		 if(state.equals("0")||state.equals("3")){ 
		                 	 %>
		                 	
		                 	 	  <fieldset align="left" style="margin:0px 5px;">
		    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
		                 	 	  
		                 	 	    &nbsp;文件名称:<input type='text'  maxLength=30  class='TEXT_NB common_border_color'  size='20'  name='fileName'     />
		                 	 	   <Br>&nbsp;&nbsp;<input name="file" type="file" onchange='upload(1)'   size="40" style='margin-top:5px;'    class="inputtext"  >&nbsp;&nbsp;
		                 	 	  
		                 	    	<br> &nbsp;
		                 	    	</fieldset>
		                 	  
		                 	    <%} } else {
		                 	    
		                 	    	 if(state.equals("0")||state.equals("3")){
		                 	     %>
		                 	    
		                 	    	<fieldset align="left" style="margin:0px 5px;">
		    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
		                 	    	
		                 	    	 &nbsp; 文件名称:<input type='text' maxLength=30  class='TEXT_NB common_border_color' size='20' name='goalfileName'  />
		                 	    	 <Br>&nbsp;&nbsp;<input name="goalfile" type="file"  onchange='upload(4)'   size="40" style='margin-top:5px;'  class="inputtext"  >&nbsp;&nbsp;
			                 	 
		                 	    	 <br> &nbsp;
		                 	    	 </fieldset>
		                 	     
		                 	    <% } } %>
		                          </td>
		                      </tr>
		                    </logic:equal>  
		                    
		                 </table>  
		           <logic:equal name="singleGradeForm" property="allowUploadFile" value="true">       
                 	 <Br>
            	   </logic:equal>
                      	<% if((optUrl.equals("summary")||optUrl.equals("summary2"))&&s_rejectCause!=null&&s_rejectCause.trim().length()>0){ %>
                  			&nbsp;驳回原因：<br>
                  			 <html:textarea  name="singleGradeForm"  cols="90" rows="8"  readonly="true"  property="s_rejectCause"></html:textarea>
                  		<% } else if((optUrl.equals("goal")||optUrl.equals("goal2"))&&g_rejectCause!=null&&g_rejectCause.trim().length()>0){ %>
                  		&nbsp;驳回原因：<br>
                  			 <html:textarea  name="singleGradeForm"  cols="90" rows="8"  readonly="true"  property="g_rejectCause"></html:textarea>
                  		<% } %>
                 
              </td>
          </tr>
                                              
          <tr class="list3">
            <td align="left" colspan="2">
			<%-- bug 36686 按钮与格线贴合 添加换行br标签   wangb 20180417--%>
			<br>
       <% if(optUrl.equals("summary")||optUrl.equals("summary2")){
       		 if(state.equals("0")||state.equals("3")){
        %>
        
       
	 	<input type='button' value='<bean:message key="button.temporary.save"/>' onclick='upload(2)' class="mybutton" style="margin-top:5px;"/>	
	 	<html:button styleClass="mybutton" property="br_home" onclick="upload(7)">
		            		<bean:message key="button.submit"/>
						</html:button>
	 	
	 	 <%} } else {
	 	 	 if(state.equals("0")||state.equals("3")){
	 	  %>
	 	 
	 	 <input type='button' value='<bean:message key="button.save"/>' onclick='upload(5)' class="mybutton" />	
	 	 <html:button styleClass="mybutton" property="br_home" onclick="upload(8)">
		            		<bean:message key="button.submit"/>
		 </html:button>
	 	 
	 	 
	 	 <% } } %>
	 	 <!-- 绩效模板文件上传按钮 author:zangxj  day:2014-06-07 -->
	 	 <%if(isnullAffix != "null"){%>
	 	 <input type='button' value='<bean:message key='train.job.export'/>' onclick='downAffix()' class="mybutton" />   
	 	<% }  %> 
	 	<input type='button' value='<bean:message key="reportcheck.return"/>' onclick='b_return()' class="mybutton" />
 
            </td>
          </tr>          
      </table>
	<script>
		(function() {
			if (document.singleGradeForm.errorMsg.value) {
				alert(document.singleGradeForm.errorMsg.value);
			}
		})();
	</script>
</form>
