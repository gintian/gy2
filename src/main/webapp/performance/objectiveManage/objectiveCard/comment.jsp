<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.constant.SystemConfig,
                 com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
                 com.hjsj.hrms.utils.ResourceFactory" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String model_opt=request.getParameter("model_opt");

String clientName="";
if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
	clientName="zglt";
String file_max_size="512";
if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
{
	file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
	if(file_max_size.toLowerCase().indexOf("k")!=-1)
	file_max_size=file_max_size.substring(0,file_max_size.length()-1);
}

ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
String isnull=objectCardForm.getIsnull();
String planid=objectCardForm.getPlanid();
String  planStatus=objectCardForm.getPlanStatus();
String  summaryState=objectCardForm.getSummaryState();
String  isUnderLeader=objectCardForm.getIsUnderLeader();
String plan_objectType=objectCardForm.getPlan_objectType();
ArrayList summary_planList=objectCardForm.getSummary_planList();
String    summary_planID=objectCardForm.getSummary_planID();
String    errorInfo=objectCardForm.getErrorInfo(); 
String desc="未提交";
if(summaryState.equals("1"))
	desc="已提交";
else if(summaryState.equals("2"))
	desc="已批准";
else if(summaryState.equals("3"))
	desc="驳回";
%>

<script language='javascript'>

<% 
	if(errorInfo!=null&&errorInfo.trim().length()>0)
	{
%>
	alert('<%=errorInfo%>');
<%
	}

 %>


	function b_return()
	{
		window.close();
	}
	
	function validateSize()
    {
    	var file_url=document.objectCardForm.file.value;
          var f_obj=document.getElementsByName("file");
          if(f_obj)
          {
          	// 防止上传漏洞
			var isRightPath = validateUploadFilePath(file_url);
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
	{
		if(trim(document.objectCardForm.summary.value).length==0)
			document.objectCardForm.summary.value=" ";
			
		if(opt==4)
		{
			if(!confirm("提交后将不能再编辑,请确认提交?"))
			{
				return;
			}
		}		
		
		if(opt==1)
		{
			if(!validateSize(opt))
	     		return;
	     	if(trim(document.objectCardForm.fileName.value).length==0)
			{
				var temp_url=document.objectCardForm.file.value;
				document.objectCardForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);
			}	
			document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_saveComment=save&model_opt=<%=model_opt%>&_opt="+opt;
		}
		else if(opt==2||opt==4)
			document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_saveComment=save&model_opt=<%=model_opt%>&_opt="+opt;
		document.objectCardForm.submit();
	}
	
	function del(id)
	{
		if(!confirm("请确认执行删除操作?"))
		{
			return;
		}
		if(document.objectCardForm.summary.value.length==0)
			document.objectCardForm.summary.value=" ";
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_saveComment=save&article_id="+id+"&model_opt=<%=model_opt%>&_opt=3";
		document.objectCardForm.submit();
	}
	
	
	
	
	
	
function approve()
{
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_opt=opt1&model_opt=<%=model_opt%>";
	document.objectCardForm.submit();
}



function reject()
{
    var arguments=new Array();
    arguments[0]="";
	arguments[1]="驳回原因"; 
    var strurl="/gz/gz_accounting/rejectCause.jsp";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
    var ss=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=350px;resizable=yes;scroll=no;status=no;");  
	if(ss)
	{
	    document.objectCardForm.rejectCause.value=ss[0];
		document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_opt=opt2&model_opt=<%=model_opt%>";
		document.objectCardForm.submit();
	}
}
	
	
function changePlan()
{
	
	var _temps=document.objectCardForm.summary_planID.value.split("/");
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_searchComment=link&_plan_id="+_temps[0]+"&model_opt="+_temps[1];
	document.objectCardForm.submit();
}	
	
/**author:zangxj *day:2014-06-07 *绩效模板文件下载按钮 */
function downAffix(){
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","down");
	hashvo.setValue("plan_id",<%=planid%>);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'90100160022'},hashvo);
}	
function showFieldList(outparamters){
	var outName=outparamters.getValue("outname");
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}	
	
</script>
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
<form name="objectCardForm" method="post" action="/performance/objectiveManage/objectiveCard.do" enctype="multipart/form-data" >
<br>	

    <%
    if(SystemConfig.getPropertyValue("show_all_reports")!=null&&SystemConfig.getPropertyValue("show_all_reports").equalsIgnoreCase("true"))
    {
     if(model_opt.equals("read")&&isUnderLeader.equals("1")){  %>
	   <table width="580" border="0" cellpadding="0" cellspacing="0" align="center">
	   <tr><td align='left' >
		   
		  同期考核计划:<select name='summary_planID' onchange='changePlan()'  >
		  			<% for(int i=0;i<summary_planList.size();i++){
		  					LazyDynaBean abean=(LazyDynaBean)summary_planList.get(i);
		  					String _plan_id=(String)abean.get("plan_id");
		  					String checked="";
		  					if(_plan_id.equalsIgnoreCase(summary_planID))
		  						checked="selected";
		  					out.println("<option value='"+(String)abean.get("plan_id")+"/"+(String)abean.get("model_opt")+"'  "+checked+" >"+(String)abean.get("name")+"</option>");
		  			} %>
		  </select>
	   
	   </td></tr>
	   </table>
	<% }
	}
	 %>
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
         
        
         
         
          <tr height="20">
       		<td nowap class="TableRow"  width='100%' style="border-bottom:0px">
       		
       		<% if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("bjpt")){ %>
       				附件说明(<%=desc%>)&nbsp;
       		
       		<% }else{ %>
		       	<% if(plan_objectType.equals("1")||plan_objectType.equals("3")||plan_objectType.equals("4")) {%>
		       		<bean:message key="info.appleal.state12"/>(<%=desc%>)&nbsp;
		       	<% } else { %>
					<bean:message key="info.appleal.state11"/>(<%=desc%>)&nbsp;
	       		<% } %>
       		
       		<% } %>
       		</td>
       	          	      
          </tr> 
          <tr>
            <td   class="framestyle">
            
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
               
              		 <%   if(clientName.equalsIgnoreCase("zglt")) {  %>
              		 
               
         				 <tr class="list3">
         
                	  	 <td align="left"  valign='top' nowrap ><Br>
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td>
                 	   		  </tr>
                 	   		  
                 	   		  
                 	   		   <logic:iterate id="element" name="objectCardForm" property="summaryFileIdsList" >
                 	   		  	<tr><td>&nbsp;&nbsp;
                 	   		  	<a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   		  	<bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>
                 	   		  	<% 
                 	   		  	if(!planStatus.equals("7")){
                 	   		  	if((summaryState.equals("0")||summaryState.equals("3"))&&model_opt.equals("edit")){ %>
                 	   		  	 &nbsp;<a href="javascript:del(<bean:write name="element" property="id" />)">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	</a>
                 	   		  	<% }
                 	   		  	} %>
                 	   		   	</td></tr>
                 	   		  </logic:iterate>
                 	   		  
                 	   		  
                 	     	  </table>
                 	     	  
                          </td>
                     
                      </tr>
                      
                      
                       <%
                       if(!planStatus.equals("7")){
                       if((summaryState.equals("0")||summaryState.equals("3"))&&model_opt.equals("edit")){ %>
   
                      <tr class="list3">
 
                	  	 <td align="left" nowrap >
                 	    	
                 	    	<fieldset align="center" style="width:98%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=file_max_size%>K</legend>
                 	 	  
                 	 	    &nbsp;文件名称:<input type='text'  maxLength=30  class='TEXT_NB common_border_color'  size='20'  name='fileName' />
                 	 	   <Br>&nbsp;&nbsp;<input name="file" onchange='upload(1)'  type="file" size="40" class="inputtext">  
                 	 	   &nbsp;&nbsp;
                 	    	<br> &nbsp;
                 	    	</fieldset>
                 	    	<br>&nbsp;
                          </td>
                      </tr>
                      <% }
                      }
                       %>
         			<% } %>
                      <tr class="list3" >
                	  	 <td align="left" nowrap >
                	  <% if(clientName.equalsIgnoreCase("zglt")) { %>
                 	     <html:textarea name="objectCardForm" property="summary" style="height:300;width:550;" styleClass="textarea1 common_border_color">  </html:textarea>
                      <% }else{ %>
                         <html:textarea name="objectCardForm" property="summary" style="height:300;width:550;" styleClass="textarea1 common_border_color">  </html:textarea>
                        <% } %>
                          </td>
                      </tr>
                      
                      <%   if(clientName.length()==0) {  %>
                      <tr class="list3">
                       <logic:equal name="objectCardForm" property="allowUploadFile" value="true">
                	  	 <td align="left"  valign='top' nowrap ><Br>
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td>
                 	   		  </tr>
                 	   		   <logic:iterate id="element" name="objectCardForm" property="summaryFileIdsList" >
                 	   		  	<tr><td>&nbsp;&nbsp;
                 	   		  	<a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   		  	<bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>
                 	   		  	<% 
                 	   		  	if(!planStatus.equals("7")){
                 	   		  	if((summaryState.equals("0")||summaryState.equals("3"))&&model_opt.equals("edit")){ %>
                 	   		  	 &nbsp;<a href="javascript:del(<bean:write name="element" property="id" />)">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	</a>
                 	   		  	<% }
                 	   		  	} %>
                 	   		   	</td></tr>
                 	   		  </logic:iterate>
                 	   		
                 	   		  
                 	     	  </table>
                 	     	  
                          </td>
                          </logic:equal>
                      </tr>
                      
                      
                      
                       <%
                       if(!planStatus.equals("7")){
                       if((summaryState.equals("0")||summaryState.equals("3"))&&model_opt.equals("edit")){ %>
                      <tr class="list3">
                         		<logic:equal name="objectCardForm" property="allowUploadFile" value="true">
                	  	 <td align="left" nowrap >
                 	    	
                 	    	<fieldset align="center" style="width:96%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=file_max_size%>K</legend>
                 	 	  
                 	 	    &nbsp;文件名称:<input type='text'  maxLength=20  class='TEXT_NB common_border_color'  size='20'  name='fileName' />
                 	 	   <Br>&nbsp;&nbsp;<input name="file" onchange='upload(1)'  type="file" size="40" class="inputtext">  
                 	 	   &nbsp;&nbsp;
                 	    	<br> &nbsp;
                 	    	</fieldset>
                 	    	<br>&nbsp;
                 	    	
                          </td>
                          </logic:equal>
                      </tr>
                      <% }
                      
                      }
                       %>
                       
                       
                     <% } %>  
                      
                 	 <%if(summaryState.equals("3")){ %>
                      	<TR><Td>
                  		<Br>&nbsp;驳回原因：<br>
                  			 <html:textarea  name="objectCardForm"  cols="80" rows="8"  readonly="true"  property="rejectCauseDesc" styleClass="textarea1"></html:textarea>
                  		</Td></TR>
                     <% } %>
                      
                 </table>  

              </td>
       
          </tr>
                                              
          <tr class="list3">
            <td align="center" colspan="2">
            <br>
	 <%
	 if(!planStatus.equals("7")&&(planid.equals(summary_planID)||(summary_planID==null||summary_planID.trim().length()==0))){
	 
	 if((summaryState.equals("0")||summaryState.equals("3"))&&model_opt.equals("edit")){ %>
	 	<input type='button' value='<bean:message key="button.temporary.save"/>' onclick='upload(2)' class="mybutton" />
	 	<input type='button' value='提交' onclick='upload(4)' class="mybutton" />
	 <%} %>	
	 <% if(model_opt.equals("read")&&isUnderLeader.equals("1")){ %>
	 	<%if(summaryState.equals("1")){ %>
	 	<input type='button' value='批准' onclick='approve()' class="mybutton" />
	 	<input type='button' value='驳回' onclick='reject()' class="mybutton" />
	 	<% }
	 	  if(summaryState.equals("2")){
	 	 %>
	 	<input type='button' value='驳回' onclick='reject()' class="mybutton" />
	 	<% } %>
	 
	 <% }
	 
	 }
	  %>
	 
	 
	 	<!-- 绩效模板文件上传按钮 author:zangxj  day:2014-06-07 -->
	 	 <%if( isnull !="null"){ %>
       	<html:button styleClass="mybutton" property="br_home" onclick="downAffix()">
		<bean:message key='train.job.export'/>
		</html:button>
		<%} %>
	 	<input type='button' value='<bean:message key="button.close"/>' onclick='b_return()' class="mybutton" />       
            </td>
          </tr>          
      </table>
      
      <input type='hidden' value='' name='rejectCause' />
</form>

<script language='javascript' >
	<%
		if(model_opt.equals("read")){
			out.println("document.objectCardForm.summary.readOnly=true");
		}
	 %>
	
</script>


