 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
                 com.hrms.struts.constant.SystemConfig,
                 com.hjsj.hrms.actionform.performance.singleGrade.SingleGradeForm,
                 com.hjsj.hrms.utils.ResourceFactory,
                 com.hrms.struts.constant.SystemConfig" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
SingleGradeForm singleGradeForm=(SingleGradeForm)session.getAttribute("singleGradeForm");
String file_max_size="512";
if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").trim().length()>0)
{
	file_max_size=SystemConfig.getPropertyValue("appendix_size").trim();
	if(file_max_size.toLowerCase().indexOf("k")!=-1)
	file_max_size=file_max_size.substring(0,file_max_size.length()-1);
}
String summarystate=singleGradeForm.getSummaryState()!=null?singleGradeForm.getSummaryState():"0";
String summarydesc="未提交";
if(summarystate.equals("1"))
	summarydesc="已提交";
else if(summarystate.equals("2"))
	summarydesc="已批准";
else if(summarystate.equals("3"))
	summarydesc="驳回";	
 

%>
<script type="text/javascript">
<!--
	function search(){
		singleGradeForm.action="/selfservice/performance/selfGrade.do?b_search=search";
		singleGradeForm.submit();
	}
	function shangchuan(type)
	{
	  if(type=='2')
	  {
	     var path=singleGradeForm.file.value;
	     if(path==null||trim(path).length==0)
	     {
		    alert(SINGLEGRADE_INFO2);
		    return;
	     }
	    
	    // 防止上传漏洞
		var isRightPath = validateUploadFilePath(path);
		if(!isRightPath)	
			return;
	    
	     if(trim(document.singleGradeForm.fileName.value).length==0)
		 {		
			var temp_url=document.singleGradeForm.file.value;
			document.singleGradeForm.fileName.value=temp_url.substring(temp_url.lastIndexOf ("\\")+1);		  		
		 }
	  }
	  var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
	  var oldInputs = document.getElementsByName("summary");
	  oldInputs[0].value = oEditor.GetXHTML(true);
	  if(document.singleGradeForm.summary.value.length==0)
				document.singleGradeForm.summary.value=" ";
	  
	  singleGradeForm.action="/selfservice/performance/selfGrade.do?b_submit=submit&type="+type;
	  singleGradeForm.submit();
	  
	  
	}

//-->
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<form name="singleGradeForm" method="post" action="/selfservice/performance/selfGrade.do" enctype="multipart/form-data" >
<br>	
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
      <tr>
<td align="left" colspan="4">

<bean:message key="label.commend.plan"/>    
	<hrms:optioncollection name="singleGradeForm" property="dblist" collection="list"   />
             <html:select name="singleGradeForm"  property="dbpre" size="1" onchange="search();">
             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
        </html:select>
        </td>
        </tr>
        <logic:notEqual name="singleGradeForm" property="dbpre" value="0">
          <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=170 align=center class="tabcenter">&nbsp;
					<bean:message key="lable.performance.personalReport"/>(<%=summarydesc%>)
					&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> -->  
       		<td align=center class="TableRow">&nbsp;
					<bean:message key="lable.performance.personalReport"/>(<%=summarydesc%>)
					&nbsp;</td>            	      
          </tr> 
          <tr>
            <td class="framestyle9">
            
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                      <tr class="list3">
                	  	 <td align="left" nowrap >
                 	     <html:textarea name="singleGradeForm" property="summary" cols="80" rows="40"   style="display:none;"  />
                 	     <script type="text/javascript">
					              var oldInputs = document.getElementsByName('summary');                             
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					              oFCKeditor.BasePath	= '/fckeditor/';
					              
					              oFCKeditor.Height	= 500 ;			
					              oFCKeditor.Width	=590;			            
					              oFCKeditor.ToolbarSet='Apply';
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
           
                          </script>
                          
                          </td>
                      </tr>
                      
                      
                      
                      
                       <tr class="list3">
                	  	 <td align="left"  valign='top' nowrap >
                	  	  <table   width='100%' ><tr><Td width='40%' valign='top' >
                	  	 
                	  	 
                 	     	<table border=0 ><tr><td valign='top'>
                 	   		  &nbsp;<bean:message key="label.zp_employ.uploadfile"/>：
                 	   		  </td></tr>
                 	   		  <tr>
                 	   		  <td>
                 	   		  
                 	   		  <table border="0" cellpmoding="0" cellspacing="0" cellpadding="0"  >
                 	   		  <logic:iterate id="element" name="singleGradeForm" property="summaryFileIdsList" >
                 	   		    <tr><td>&nbsp;&nbsp; 
                 	   		    <a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   			 <bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	</td><td>&nbsp;
                 	   		  		<% if(summarystate.equals("0")||summarystate.equals("3")){ %>
                 	   
                 	   		  	
                 	   		  	<a href="/selfservice/performance/selfGrade.do?b_delete=delete&planid=${singleGradeForm.dbpre}&id=<bean:write name="element" property="id" />">
                 	   		  		<image src='/images/del.gif' border=0 title='<bean:message key="label.zp_employ.deletefile"/>' >
                 	   		  	</a>
                 	   		  	<% } %>
                 	   		   </td></tr>
                 	   		  </logic:iterate>
                 	   		  </table>
                 	     	  </td>
                 	     	  </tr></table>
                 	     	  </td></tr></table>
                          </td>
             	  </tr>
               <% if(summarystate.equals("0")||summarystate.equals("3")){ %>
	             <tr class="list3">
	                	  	 <td align="left" nowrap >
	                	  	 <Br>
	                	  	   <fieldset align="center" style="width:98%;">
    							 <legend><bean:message key="label.zp_employ.uploadfileInfo"/><%=(file_max_size)%>K</legend>
	                	  	
	                 	 	   &nbsp;文件名称:<input type='text'  maxLength=30  class='TEXT_NB'  size='20'      name='fileName' />
	                 	 	   <Br>&nbsp;&nbsp;<input name="file" type="file"   onchange="shangchuan('2')"    size="40">
	                 	       
                 	    	   <br>&nbsp;
                 	    	  </fieldset>
                 	    	  <br>&nbsp;
	                       </td>
	              </tr>	
	             <% } %>
                      
                    
                 </table>  
                    
              </td>
          </tr>
             <% if(summarystate.equals("0")||summarystate.equals("3")){ %>                                 
          <tr class="list3">
            <td colspan="2" style="height:35px;">
	 	  
	 	   <input type="button" class="mybutton" value="<bean:message key="button.save"/>" name="save" onclick="shangchuan('1');"/> 
	 	    <html:button styleClass="mybutton" property="br_home" onclick="shangchuan('3')">
		            		<bean:message key="button.submit"/>
		   </html:button>  
            </td>
          </tr> 
          <%    } %>
          </logic:notEqual>         
      </table>
</form>
