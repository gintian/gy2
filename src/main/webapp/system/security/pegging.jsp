<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.fixedDiv1{
	overflow:hidden; 
	height:30px;
	width:640px;
	line-height:30px; 
	BORDER: #94B6E6 1pt solid; 
	background-color:#f4f7f7;
	font-weight:bold;
}
.fixedDiv2 
{ 
	overflow:auto; 
	height:100%;
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: medium none; 
}
.fixedDiv3 
{ 
	overflow:hidden; 	
	width:640px; 
	BORDER: #94B6E6 1pt solid; 
	border-top:0px;
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
function abolish(){
	var temp=selectcheckeditem();
	if(temp.length>0){
		if(confirm("确认要撤销吗？")){
		   var hashvo=new ParameterSet();          
	       hashvo.setValue("selected", temp);
	       hashvo.setValue("id", "${privForm.id}");
	       //alert(temp);
	       var request=new Request({method:'post',onSuccess:afterabolish,functionId:'1010010089'},hashvo);
		}
	}
}
function afterabolish(outparamters){
	var msg=outparamters.getValue("msg");
	if("ok"==msg){
		privForm.action="/system/security/pegging.do?b_query=link&name="+$URL.encode('${privForm.name}')+"&id=${privForm.id}&flag=${privForm.flag}";
		privForm.submit();
	}
}
 function selectcheckeditem()
   {
    var a=0;
	var selectid=new Array();	

	    for(var i=0;i<document.privForm.elements.length;i++)
	    {			
		   if(document.privForm.elements[i].type=='checkbox'&&document.privForm.elements[i].name!="selbox")
		   {	
		     
		       if(document.privForm.elements[i].checked==true)
		       {
			      selectid[a++]=document.privForm.elements[i].value;						
		       }
		  }
	   }
	
	if(selectid.length==0)
	{
		alert(REPORT_INFO9+"!");
		return '';
	}
	return selectid;	
 }
</script>
<html:form action="/system/security/pegging">
<!-- 
<table style="width:640px;" border="0" cellpadding="0" cellspacing="0">
<tr height="20">
    <td align="left" class="TableRow1">${privForm.name } &nbsp;</td>             	      
</tr> 
</table>
 -->
<div class="fixedDiv1" style="border-bottom:0px;">
&nbsp;${privForm.name }&nbsp;
</div>
<table style="width:640;" border="0" cellpadding="0" cellspacing="0">
<tr><td width="640px;" height="expression(document.body.clientHeight-150);">
<div class="fixedDiv2" style="width:640px;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr class="fixedHeaderTr">
           <logic:equal value="func" property="flag" name="privForm">
           <td align="center" class="TableRow" nowrap style="border-left:none;border-top:none;" width="40">
		<input type="checkbox" name="selbox" onclick="batch_select(this,'peggingListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
	    </td>
	    </logic:equal>
           <td align="center" class="TableRow" width="180" nowrap style="border-top:none;border-left:none;">
			<bean:message key="label.org.type_org"/>
	    </td>
        <td align="center" class="TableRow" width=70% nowrap style="border-top:none;">
			<bean:message key="column.sys.name"/>
	    </td>
	    <logic:notEqual value="func" name="privForm" property="flag">
            <td align="center" class="TableRow" width=10% nowrap style="border-top:none;">
		<bean:message key="read.label"/>
	    </td>	    
        <td align="center" class="TableRow" width=10% nowrap style="border-top:none;">
		<bean:message key="write.label"/>
	    </td>
	    </logic:notEqual>
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="privForm" property="peggingListForm.list" indexes="indexes"  pagination="peggingListForm.pagination" pageCount="${privForm.pagerows}" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
          <logic:equal value="func" property="flag" name="privForm">
          <td align="center" class="RecordRow" nowrap style="border-left:none;"> 
          	<bean:define id="iid" name="element" property="iid"></bean:define>  
          	<bean:define id="status" name="element" property="status"></bean:define>
          	<logic:equal value="T" name="element" property="status">
    		 	<input type="checkbox" disabled="disabled" name="selbox"/>&nbsp;
	    	</logic:equal> 
	    	<logic:notEqual value="T" name="element" property="status">
    		 	<hrms:checkmultibox name="privForm" property="peggingListForm.select" value="${iid}`${status }" indexes="indexes"/>&nbsp;
	    	</logic:notEqual>
	    </td>
	    </logic:equal>
          <td align="left" class="RecordRow" nowrap style="border-left:none;">
             &nbsp;<bean:write name="element" property="type"/>
	    </td>
            <td align="left" class="RecordRow" nowrap>
             &nbsp;<bean:write name="element" property="name"/>
	    </td>
	    <logic:notEqual value="func" name="privForm" property="flag">
            <td align="center" class="RecordRow" 	style="word-break:break-all;"  nowrap>
                    <logic:equal name="element" property="priv" value="1">
                    	<input type=radio checked="checked"/>
                    </logic:equal>   	   	             	            	              	              	            	               	             	             	             	             	             	             	               
	    </td>
        <td align="center" class="RecordRow" 	style="word-break:break-all;"  nowrap>
                    <logic:equal name="element" property="priv" value="2">
                    	<input type=radio checked="checked"/>
                    </logic:equal>   
	    </td>
	    </logic:notEqual>
          </tr>
      </hrms:extenditerate>
        
</table>
</div>
</td></tr>
</table>
<div class="fixedDiv3" style="width:640px;"> 
<table  width="100%" align="center" >
		<tr>
		    <td valign="bottom" >
		            <hrms:paginationtag name="privForm" pagerows="${privForm.pagerows}" property="peggingListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td  align="right" nowrap >
				 <p align="right"><hrms:paginationlink name="privForm" property="peggingListForm.pagination" nameId="peggingListForm">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
        <td align="center"  nowrap height="35px;">
        <logic:equal value="func" property="flag" name="privForm">
          <hrms:priv func_id="300380101"> 
        	<html:button property="b_abolish" styleClass="mybutton" onclick="abolish();">&nbsp;<bean:message key='button.abolish'/>&nbsp;</html:button>&nbsp;&nbsp;
          </hrms:priv>
        </logic:equal>
            <html:button property="b_close" styleClass="mybutton" onclick="top.window.close();">&nbsp;<bean:message key='button.close'/>&nbsp;</html:button>
        </td>
   </tr> 
</table>
</html:form>
