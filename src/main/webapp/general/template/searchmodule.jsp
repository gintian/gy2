<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig"%>
<%
	String tabid="";
	String sp_flag="";
	String ishave="";
	String view ="";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	String operationname=(String)templateForm.getOperationname();
	String staticid=(String)templateForm.getStaticid();
	String returnflag = request.getParameter("returnflag");
	if(!"noback".equals(returnflag)){
		int version = userView.getVersion();
		if(view!=null&&view=="list"){
			returnflag="listhome";
		}else if(view!=null&&view=="card"){
	   		returnflag="9";
		}else{
			if(version>=50)
				returnflag="listhome";
			else
	    		returnflag="9";
		} 
	}
%>
<script type="text/javascript">
<!--
	function edit_inf(tabid)
	{
		templateForm.action="/general/template/search_module.do?b_edit=link&tabid="+tabid;
		templateForm.submit();
	}

	function fill_out(sp_flag,tabid,view)
	{
    var version = "<%=userView.getVersion()%>";
        if(view!=null&&view=="list"){
            document.location="/general/template/templatelist.do?b_init=init&sp_flag=1&staticid=<%=staticid%>&operationname=<%=operationname%>&ins_id=0&returnflag=listhome&task_id=0&tabid="+tabid; 
        }else if(view!=null&&view=="card"){
                window.location.href="/general/template/edit_form.do?b_query=link&taskid=0&businessModel=0&operationname=<%=operationname%>&returnflag=9&sp_flag=1&ins_id=0&tabid="+tabid;
            }
            else{
         if(version>=50){ 
            document.location="/general/template/templatelist.do?b_init=init&sp_flag=1&staticid=<%=staticid%>&operationname=<%=operationname%>&ins_id=0&returnflag=listhome&task_id=0&tabid="+tabid;
         }else{
        window.location.href="/general/template/edit_form.do?b_query=link&taskid=0&businessModel=0&operationname=<%=operationname%>&returnflag=9&sp_flag=1&ins_id=0&tabid="+tabid;
        } 
	}
	}	
//-->
</script>
<style type="text/css">
<!--
.unnamed1 {	
	font-size: 12px;
	font-style: normal;
	line-height: normal;
	text-decoration: none;
}
-->
.Bold
{
	font-weight: bold;
}

.Title
{
	font-weight: bold;
	font-size: 18px;
	color: #cc3300;
}

.Code
{
	border: #8b4513 1px solid;
	padding-right: 5px;
	padding-left: 5px;
	color: #000066;
	font-family: 'Courier New' , Monospace;
	background-color: #ff9933;
}
</style>
<html:form action="/general/template/search_module">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:50px;">
<tr>
  <td width="100%" nowrap>

    <table  border="0" cellpadding="0" cellspacing="0" align="center" >		
	  <tr>
	  <td>
	     <table  border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">		
		<tr >
		  <logic:iterate id="element"  name="templateForm"  property="templist" indexId="index">
			<td align="center"  valign="top" class="RecordRow" nowrap="nowrap">
             <bean:write name="element" property="content" filter="false"/><br>
			</td>
   		  </logic:iterate>  			
		</tr>
		<tr >
		  <logic:iterate id="element"  name="templateForm"  property="templist" indexId="index">
		    <%
            	LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            	tabid=(String)item.get("tabid");
            	sp_flag=(String)item.get("sp_flag");
            	ishave=(String)item.get("ishave");
            	view =(String)item.get("view");
            %>
			<td align="center" height="40"  valign="middle" class="RecordRow" style="height:35">		     	
    		 <%if(ishave==null||!ishave.equals("1")) {%> 		
    		   <hrms:priv func_id="3300103,331013,3203" module_id=""> 				
	 		    <INPUT type="button" class="mybutton" onclick="edit_inf('<%=tabid%>');" name="b_edit" value='流程说明' disabled='true'>
    		   </hrms:priv> 	    
	 		   <INPUT type="button" class="mybutton"  name="bc_btn1" value='业务处理' disabled='true'>
			 <%}else{ %>
			   <hrms:priv func_id="3300103,331013,3203,3240113,3250103,3219,3709,3719,3729,3739" module_id=""> 				
	 		    <INPUT type="button" class="mybutton" onclick="edit_inf('<%=tabid%>');" name="b_edit" value='流程说明'>
    		 </hrms:priv> 	
			   <INPUT type="button" class="mybutton" onclick="fill_out('<%=sp_flag%>','<%=tabid%>','<%=view%>');" name="bc_btn1" value='业务处理'>
			 <%} %>
			</td>
   		 </logic:iterate>  			
		 </tr>		
	    </table>
	  </td>	     
	  </tr>
	 </table>
	
   </td>         
  </tr>
</table>
</html:form>
