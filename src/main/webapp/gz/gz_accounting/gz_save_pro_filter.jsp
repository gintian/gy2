<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript">
<%
String scopeflag = request.getParameter("scopeflag")==null?"": request.getParameter("scopeflag");
%>
function sub(){
    var porjectname = document.getElementById("porjectname").value;
    if(porjectname==null||porjectname=='')
    {
      alert(GZ_ACCOUNTING_ENTERFILTERNAME+"!");
      return;
    }
    if(porjectname.length>50)   
    {   
        alert(GZ_ACCOUNTING_CHARLENGTHFIFTY+"！");   
        return ;   
    }
    var scopeflag='${accountingForm.scopeflag}';
    var scope ="<%=scopeflag%>";
  	if(scope!="2"){
     if (document.accountingForm.attributeflag[0].checked){
			scopeflag="1";
		}else{
			scopeflag="0";
		}
	}
   var thevo=new Object();
   thevo.pname =porjectname;
    thevo.sflag =scopeflag;
    window.returnValue = thevo;
  	window.close();
}
function breturn(){
	//window.returnValue = "";
	window.close();

}
</script>
<html:form action="/gz/gz_accounting/gzprofilter">
	<table border="0" align="center">
		<tr>
			<td align="center">
			<table  width="100%" border="0" cellspacing="0"    align="left" cellpadding="0" class="ListTable">
   
 			 <thead>
 				 <tr>	
    
 				   	 <td   align="center" class="TableRow" nowrap><bean:message key="label.gz.project.save" /></td>    
 				 </tr>
 			 </thead>
 				 <tr>	
						<td align="center" class="RecordRow">
							<input type="text" name="porjectname" size="25"
								value="<bean:write name="accountingForm" property="chkName"/>" />
						</td>
					</tr>
					<tr>
						<td class="RecordRow">
							<%if(!scopeflag.equals("2")){ %>
								<logic:equal name="accountingForm" property="scopeflag" value="1">
								<input type="radio" name="attributeflag" checked >&nbsp;
								<bean:message key="label.gz.private"/>
								</logic:equal>
								<logic:equal name="accountingForm" property="scopeflag" value="0">
								<input type="radio" name="attributeflag"  >&nbsp;
								<bean:message key="label.gz.private"/>
								</logic:equal>  
								<logic:equal name="accountingForm" property="scopeflag" value="0">
								<input type="radio" name="attributeflag"  checked>&nbsp;
								<bean:message key="label.gz.public"/>
								</logic:equal> 
								<logic:equal name="accountingForm" property="scopeflag" value="1">
								<input type="radio" name="attributeflag"  >&nbsp;
								<bean:message key="label.gz.public"/>
								</logic:equal> 
								<%} %>
							
						</td>
					</tr>
					
					
				</table>
			</td>
		</tr>
		<tr>
		<td>
		<table width="100%" border="0" cellspacing="0" >
		<tr>
						<td align="center">
						 	
							<html:button styleClass="mybutton" property="b_next"
								onclick="sub();">
								<bean:message key="button.ok" />
							</html:button>
							
							<html:button styleClass="mybutton" property="b_return"
								onclick="breturn();">
								<bean:message key="button.cancel" />
							</html:button>
						</td>
					</tr>
		</table>
		</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
<!--
var paraArray=dialogArguments; 
<%if(request.getParameter("rename")!=null&&request.getParameter("rename").equals("1")){%>
var desc = paraArray[0];
document.getElementById("porjectname").value=desc;
<%}%>
//-->
</script>
