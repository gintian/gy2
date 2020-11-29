<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,
				com.hjsj.hrms.actionform.performance.AppraiseMutualForm,
				java.util.*"%>
<% 
String file_max_size="512";
if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
{
	file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
	if(file_max_size.toLowerCase().indexOf("k")!=-1)
	file_max_size=file_max_size.substring(0,file_max_size.length()-1);
}
AppraiseMutualForm appraiseMutualForm=(AppraiseMutualForm)session.getAttribute("appraiseMutualForm");
String isnull=appraiseMutualForm.getIsnull();
String PlanNum=appraiseMutualForm.getPlanNum();
String rejectCauseDesc=appraiseMutualForm.getRejectCauseDesc();
String  planStatus=appraiseMutualForm.getPlanStatus();
String isSelf=appraiseMutualForm.getIsSelf();
String optUrl="";
if(request.getParameter("optUrl")!=null)
	optUrl=request.getParameter("optUrl");
String fromflag="";
	if(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("status"))
	{
	   fromflag = "&fromflag=status";
	}

String objStatus="";	
if(optUrl.equals("summary")||optUrl.equals("summary2")){
	objStatus=appraiseMutualForm.getSummaryState();
}
else
{
	objStatus=appraiseMutualForm.getGoalState();
}	
	
	
%>

<script language='javascript' >

function approve()
{
	document.appraiseMutualForm.action="/selfservice/performance/view_summary.do?b_opt=opt1<%=fromflag%>&planNum=<%=(request.getParameter("planNum"))%>&objectId=<%=(request.getParameter("objectId"))%>&optUrl=<%=optUrl%>";
	document.appraiseMutualForm.submit();
}

function del(article_id,plan_id)
{
		if(!confirm("请确认执行删除操作?"))
		{
			return;
		}
		if(document.appraiseMutualForm.summary&&document.appraiseMutualForm.summary.value.length==0)
			document.appraiseMutualForm.summary.value=" ";
		if(document.appraiseMutualForm.goalContext&&document.appraiseMutualForm.goalContext.value.length==0)
			document.appraiseMutualForm.goalContext.value=" ";	
	    document.appraiseMutualForm.action="/selfservice/performance/view_summary.do?b_opt=opt3&article_id="+article_id+"<%=fromflag%>&planNum=<%=(request.getParameter("planNum"))%>&objectId=<%=(request.getParameter("objectId"))%>&optUrl=<%=optUrl%>";
	    document.appraiseMutualForm.submit();
		
}


function validateSize(opt)
{
          var f_obj;
          if(opt==1)
	          f_obj=document.getElementsByName("file");
    	  else if(opt==4)
    	      f_obj=document.getElementsByName("goalfile");
          if(f_obj)
          {
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
          }
          return true;
}
	

function upload(opt)
{
	
	     if(opt==1||opt==4)
	     {
	     	if(!validateSize(opt))
	     		return;
	     }
	
		if(document.appraiseMutualForm.summary&&document.appraiseMutualForm.summary.value.length==0)
			document.appraiseMutualForm.summary.value=" ";
		if(document.appraiseMutualForm.goalContext&&document.appraiseMutualForm.goalContext.value.length==0)
			document.appraiseMutualForm.goalContext.value=" ";		
		if(opt==7)
		{
			if(!confirm("提交后将不能再编辑,请确认提交?"))
			{
				return;
			}
		}	
		
		if((opt==1&&trim(document.appraiseMutualForm.fileName.value).length==0)||(opt==4&&trim(document.appraiseMutualForm.goalfileName.value).length==0))
		{
			if(opt==4)
			{	
				var temp_url=document.appraiseMutualForm.goalfile.value;
				document.appraiseMutualForm.goalfileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
				
			}
			else
			{
				var temp_url=document.appraiseMutualForm.file.value;
			    document.appraiseMutualForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
			   
			} 
		}
		
		
		 
		var opt_str="";
		if(opt==1||opt==4)
			opt_str="opt6";
	  	else if(opt==2)
	  		opt_str="opt4";
	  	else if(opt==7)
	  		opt_str="opt5";
		document.appraiseMutualForm.action="/selfservice/performance/view_summary.do?b_opt="+opt_str+"<%=fromflag%>&planNum=<%=(request.getParameter("planNum"))%>&objectId=<%=(request.getParameter("objectId"))%>&optUrl=<%=optUrl%>";
	    document.appraiseMutualForm.submit();
}



function reject()
{
    var arguments=new Array();
    arguments[0]="";
	arguments[1]="驳回原因"; 
    var strurl="/gz/gz_accounting/rejectCause.jsp";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
    if (!window.showModalDialog){
        window.dialogArguments = arguments;
    }
    var config={
        width:470,
        height:350,
        type:'2',
        dialogArguments:arguments
    }
    modalDialog.showModalDialogs(iframe_url,'',config,view_reject_ok);
}
function view_reject_ok(ss){
    if(ss)
    {
        document.appraiseMutualForm.rejectCause.value=ss[0];
        document.appraiseMutualForm.action="/selfservice/performance/view_summary.do?b_opt=opt2<%=fromflag%>&planNum=<%=(request.getParameter("planNum"))%>&objectId=<%=(request.getParameter("objectId"))%>&optUrl=<%=optUrl%>";
        document.appraiseMutualForm.submit();
    }
}
function closeWin()
{
  <%if(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("status")){%>
  window.opener.location="/performance/markStatus/markStatusList.do?b_search=link";
  <%}%>
  window.close();
}

/**author:zangxj *day:2014-06-07 *绩效模板文件下载按钮 */
function downAffix(){
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","down");
	hashvo.setValue("plan_id",<%=PlanNum%>);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'90100160022'},hashvo);
}	
function showFieldList(outparamters){
	var outName=outparamters.getValue("outname");
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
	
	
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script> 
<form name="appraiseMutualForm" method="post" action="/selfservice/performance/view_summary.do"  enctype="multipart/form-data" >
<br>	
<br>
      <table width="570" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td align=center class="TableRow">&nbsp;
       		<% if(optUrl.equals("summary")||optUrl.equals("summary2")){ %>
       		
       		<logic:equal name="appraiseMutualForm" property="performanceType" value="0">
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
       		<logic:equal name="appraiseMutualForm" property="performanceType" value="1">
       			述职报告
       		</logic:equal>
       			<logic:equal  name="appraiseMutualForm"   property="summaryState"  value="1">(已提交)</logic:equal>
       			<logic:equal  name="appraiseMutualForm"   property="summaryState"  value="0">(未提交)</logic:equal>
       			<logic:equal  name="appraiseMutualForm"   property="summaryState"  value="2">(已批准)</logic:equal>
       			<logic:equal  name="appraiseMutualForm"   property="summaryState"  value="3">(驳回)</logic:equal>
       		<% } else { %>
					<bean:message key="lable.performance.perGoal"/>
					<logic:equal  name="appraiseMutualForm"   property="goalState"  value="1">(已提交)</logic:equal>
       			    <logic:equal  name="appraiseMutualForm"   property="goalState"  value="0">(未提交)</logic:equal>
       			    <logic:equal  name="appraiseMutualForm"   property="goalState"  value="2">(已批准)</logic:equal>
       				<logic:equal  name="appraiseMutualForm"   property="goalState"  value="3">(驳回)</logic:equal>
			<% } %>
       		
       		&nbsp;</td>
       		             	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	  <td align="center" nowrap style="padding:4 0 0 4">
                 	    <% 
                 	      
                 	      if(optUrl.equals("summary")||optUrl.equals("summary2")){
                 	     %>
                 	    
						<html:textarea name="appraiseMutualForm"  property="summary" cols="90" rows="30"  style=""   />		<!-- 去掉了编辑器 -->
		
		 				<% }else { %>
                 	     <html:textarea name="appraiseMutualForm"   property="goalContext" cols="80" rows="30"  style=""  /><!-- 去掉了编辑器 -->

                 	     <% } %>
                          </td>                      
                      </tr>
                      <logic:equal name="appraiseMutualForm" property="allowUploadFile" value="true">   
                       <tr class="list3">
                	  	 <td align="left"  valign='top' nowrap ><Br>
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  &nbsp;附件：
                 	   		   </td>
                 	   		  </tr><tr><td  valign='top'  >
                 	   		  
                 	   		   <table border="0" cellpmoding="0" cellspacing="0" cellpadding="0"  >
                 	   		  <% if(optUrl.equals("summary")||optUrl.equals("summary2")){ %>
                 	   		  	 <logic:iterate id="element" name="appraiseMutualForm" property="summaryFileIdsList" >
	                 	   		   <tr><td height='20' >&nbsp;&nbsp;
	                 	   		    <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
	                 	   			  <bean:write name="element" property="name" />
	                 	   		  	</a>
	                 	   		    </td>
	                 	   		    <td>
	                 	   		    <% if(isSelf.equalsIgnoreCase("true")&&(objStatus.equals("0")||objStatus.equals("3"))){ %>
                 	   		  	 &nbsp;<a href="javascript:del(<bean:write name="element" property="id" />,'Usr')">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  		</a>
                 	   		  		<%} %>
	                 	   		    </td>
	                 	   		    </tr>
                 	   		 	 </logic:iterate>
                 	   		   <% } else { %>
                 	   		  <logic:iterate id="element" name="appraiseMutualForm" property="goalFileIdsList" >
	                 	   		   <tr><td  height='20'  >&nbsp;&nbsp;
	                 	   		    <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
	                 	   			  <bean:write name="element" property="name" />
	                 	   		  	</a>
	                 	   		  </td>
	                 	   		   <td>
	                 	   		    <% if(isSelf.equalsIgnoreCase("true")&&(objStatus.equals("0")||objStatus.equals("3"))){ %>
                 	   		  	 &nbsp;<a href="javascript:del(<bean:write name="element" property="id" />,'Usr')">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  		</a>
                 	   		  		<%} %>
	                 	   		    </td>
	                 	   		  </tr>
                 	   		 	 </logic:iterate>
                 	   		  <% } %>	
                 	   		  </table>
                 	   		  
                 	     	  </td>
                 	     	  </tr>
                 	     	  
                 	     	  <% if(isSelf.equalsIgnoreCase("true")){ %>
                 	     	   <tr class="list3">
			                	  	 <td align="left" nowrap ><Br>
			                 	 <% if(optUrl.equals("summary")||optUrl.equals("summary2")){ 
			                 	 		 if(objStatus.equals("0")||objStatus.equals("3")){ 
			                 	 %>
			                 	
			                 	 	  <fieldset align="center" style="width:98%;">
			    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
			                 	 	  
			                 	 	    &nbsp;文件名称:<input type='text'  maxLength=30  class='TEXT_NB'  size='20'  name='fileName'   />
			                 	 	   <Br>&nbsp;&nbsp;<input name="file" type="file" onchange='upload(1)'   size="40"    >&nbsp;&nbsp;
			                 	 	  
			                 	    	<br> &nbsp;
			                 	    	</fieldset>
			                 	  
			                 	    <%} } else {
			                 	    
			                 	    	 if(objStatus.equals("0")||objStatus.equals("3")){
			                 	     %>
			                 	    
			                 	    	<fieldset align="center" style="width:98%;">
			    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
			                 	    	
			                 	    	 &nbsp; 文件名称:<input type='text' maxLength=30  class='TEXT_NB' size='20' name='goalfileName'  />
			                 	    	 <Br>&nbsp;&nbsp;<input name="goalfile" type="file"  onchange='upload(4)'   size="40"     >&nbsp;&nbsp;
				                 	 
			                 	    	 <br> &nbsp;
			                 	    	 </fieldset>
			                 	     
			                 	    <% } } %>
			                          </td>
                                </tr>
                 	     	   <% } %>
                 	     	  
                 	     	  
                 	     	  
                 	     	  
                 	     	  
                 	     	   <% if((optUrl.equals("summary")||optUrl.equals("summary2"))&&rejectCauseDesc!=null&&rejectCauseDesc.trim().length()>0){ %>
					            
					            <tr><td><br> &nbsp;驳回原因：</td></tr>
                 	     	    <tr><td>
					           <html:textarea  name="appraiseMutualForm"  cols="80" rows="8"  readonly="true"  property="rejectCauseDesc"></html:textarea>
					            </td></tr>
					            
					            <% }else if(rejectCauseDesc!=null&&rejectCauseDesc.trim().length()>0){ %>
					           
					             <tr><td><br> &nbsp;驳回原因：</td></tr>
                 	     	    <tr><td>
					             <html:textarea  name="appraiseMutualForm"  cols="80" rows="8" readonly="true"    property="rejectCauseDesc"></html:textarea>
					            </td></tr>
					           
					            <% } %>
                 	     	 
                 	     	  </table>
                 	     	  
                          </td>
                      </tr>
                    </logic:equal>  
                    
                 </table>     
              </td>
          </tr>                                                     
          <tr class="list3">
            <td align="center" style="height:35px;">
         	
	 	    <logic:equal  name="appraiseMutualForm"   property="isUnderLeader"  value="1">
	 	    <%
	 	    if(!planStatus.equals("7")){
	 	    
	 	     if(optUrl.equals("summary")||optUrl.equals("summary2")){ %>
				<logic:equal  name="appraiseMutualForm"   property="summaryState"  value="1">
		 	    <html:button styleClass="mybutton" property="btnclose" onclick="approve()">
	            		批准
		 	    </html:button>
		 	     <html:button styleClass="mybutton" property="btnclose" onclick="reject();">
	            		驳回
		 	    </html:button>
		 	    </logic:equal>
		 	 <% }else{ %>
		 	    <logic:equal  name="appraiseMutualForm"   property="goalState"  value="1">
		 	    <html:button styleClass="mybutton" property="btnclose" onclick="approve()">
	            		批准
		 	    </html:button>
		 	    <html:button styleClass="mybutton" property="btnclose" onclick="reject();">
	            		驳回
		 	    </html:button>
		 	    </logic:equal>
		 	  <% }
		 	  
		 	  }
		 	   %>
		 	   
       		</logic:equal>
       		
       		
       		 <%  
       		 if(isSelf.equalsIgnoreCase("true")&&(objStatus.equals("0")||objStatus.equals("3"))){
             %>
        
       
	 	   <input type='button' value='<bean:message key="button.temporary.save"/>' onclick='upload(2)' class="mybutton" />	
	     	<html:button styleClass="mybutton" property="br_home" onclick="upload(7)">
		            		<bean:message key="button.submit"/>
			 </html:button>
	 	
	 	    <%  }   %>
	 	    <!-- 绩效模板文件上传按钮 author:zangxj  day:2014-06-07 -->
	 	    <%if( isnull !="null"){ %>
       		<html:button styleClass="mybutton" property="br_home" onclick="downAffix()">
		    <bean:message key='train.job.export'/>
			</html:button>
			<%} %>
       		<html:button styleClass="mybutton" property="btnclose" onclick="closeWin();">
            		<bean:message key="button.close"/>
	 	    </html:button>
            </td>
          </tr>          
      </table>
<script language='javascript'>
 
</script>
      <Input type='hidden' name='rejectCause'  value='' />
</form>
