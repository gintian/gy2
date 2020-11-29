<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">


<style>
.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	text-align:center;
	height: 30px;
}
 
 
</style>
<hrms:themes />
<script language='javascript' >
function checkNum(obj){
  	if(trim(obj.value).length>0)
  	{
	  	 var myReg =/^(-?\d+)(\.\d+)?$/
		 if(!myReg.test(obj.value)) 
		 {
		 	alert("<bean:message key='gz.formula.input.num'/>");
		 	obj.value="";
		 	obj.focus();
		 }
	}
  }
  
  function setName(){
        document.salaryStandardForm.action="/gz/formula/standard.do?b_query=init&opt=alert";
      	document.salaryStandardForm.submit();
  }
  
  function  updateColumn(type){
  		var infos=new Array();
  	    var thecodeurl="/gz/templateset/standard.do?b_updateColumn=update`type="+type;
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
        var return_value= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:580px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");	
		        
  		document.salaryStandardForm.action="/gz/formula/standard.do?b_query=init";
  		document.salaryStandardForm.submit();
  } 
  </script>
  <html:form action="/gz/formula/standard">
<table width="100%" border="0" align="center">
    <tr>
    	<td align="center" height="20"><bean:write name="salaryStandardForm" property="gzStandardName"/></td>
    </tr>
</table>
  ${salaryStandardForm.gzStandardItemHtml}
  <br>
  <table  border="0" cellspacing="0" cellpadding="0" align="center">
  <tr>
    <td align='left' > 
      <input type="button" class="mybutton" value="<bean:message key='options.save'/>" onclick="setName()" />&nbsp;
    </td>
  </tr>
</table>
<br><br><br>&nbsp;&nbsp;
<table width="100%" border="0" class="ListTable1">
    <tr>
    	<td width="150" class="RecordRow" >&nbsp;<bean:message key='report.parse.orientation.across'/>:
    		<logic:notEqual name="salaryStandardForm" property="hfactor_name" value="">
    			<bean:write name="salaryStandardForm" property="hfactor_name"/>
    			<logic:notEqual name="salaryStandardForm" property="s_hfactor_name" value="">|
    			</logic:notEqual>
    		</logic:notEqual>
    		
    		<logic:notEqual name="salaryStandardForm" property="s_hfactor_name" value="">
    			<bean:write name="salaryStandardForm" property="s_hfactor_name"/>
    		</logic:notEqual>&nbsp;
    	</td>
    	<td  width="150"  class="RecordRow"  ><bean:message key='report.parse.orientation.erect'/>:
    		<logic:notEqual name="salaryStandardForm" property="vfactor_name" value="">
    			<bean:write name="salaryStandardForm" property="vfactor_name"/>
    			<logic:notEqual name="salaryStandardForm" property="s_vfactor_name" value="">|
    			</logic:notEqual>
    		</logic:notEqual>
    		<logic:notEqual name="salaryStandardForm" property="s_vfactor_name" value="">
    			|<bean:write name="salaryStandardForm" property="s_vfactor_name"/>
    		</logic:notEqual>&nbsp;
    	</td>
    	<td width="100"  class="RecordRow" ><bean:message key='gz.formula.results'/>:
    		<logic:notEqual name="salaryStandardForm" property="desc" value="">
    			<bean:write name="salaryStandardForm" property="desc"/>
    		</logic:notEqual>
    	</td>
    
    </tr>
</table>
</html:form>