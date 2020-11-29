<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%int i=0;%>
<style type="text/css">
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 21px; 
 PADDING-BOTTOM: 21px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#7b9ebd 1px solid;
 background-color:#EBF1F9;
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 5px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 5px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 5px; 
 PADDING-BOTTOM: 5px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#7b9ebd 1px solid;
 background-color:#EBF1F9;
}
</style>
<script language="javascript">
function symbol(cal){
	if(document.getElementById("expression").pos!=null){
		document.getElementById("expression").pos.text=cal;
	}else{
		alert("<bean:message key='org.autostatic.mainp.move.cursor.edit'/>");
	}
}
function symsave(){
	var expre = document.getElementById("expre").value;
	window.returnValue = expre;
	window.close();
}
function symup(){
	projectForm.action = "/org/autostatic/mainp/statistics_conditions.do?b_query=link";
	projectForm.submit();
}
function savecrond(){
	projectForm.action = "/org/autostatic/mainp/setconditions.do?b_save=link";
	projectForm.submit();
}
</script>
<base id="mybase" target="_self">
<html:form action="/org/autostatic/mainp/setconditions">
<br>
<br>
<html:hidden name="projectForm" property="expre"/>
<logic:equal name="projectForm" property="savecrond" value="1">
<script language="javascript">
symsave();
</script>
</logic:equal>
<table border="0"  cellspacing="0" width="85%" class="ListTable1"  cellpadding="2" align="center">
   <tr>   
      <td align="center" nowrap class="TableRow"><bean:message key="label.query.number"/></td>                                        	      
      <td align="center" nowrap class="TableRow"><bean:message key="label.query.field"/></td>
      <td align="center" nowrap class="TableRow"><bean:message key="label.query.relation"/></td>
      <td align="center" nowrap class="TableRow"><bean:message key="label.query.value"/></td>          	
   </tr>
   <logic:iterate id="element" name="projectForm"  property="factorlist" indexId="index">
   <tr>
      <td align="center" class="RecordRow" nowrap ><%=i+1%></td> 
      <td align="center" class="RecordRow" nowrap >
         <bean:write name="element" property="hz" />&nbsp;
      </td>  
      <td align="center" class="RecordRow" nowrap >
         <hrms:optioncollection name="projectForm" property="operlist" collection="list"/>
            <html:select name="projectForm" property='<%="factorlist["+index+"].oper"%>' size="1">
               <html:options collection="list" property="dataValue" labelProperty="dataName"/>
            </html:select>
      </td>
      <!--日期型 -->                            
      <logic:equal name="element" property="fieldtype" value="D">
      <td align="left" class="RecordRow" nowrap>                
	  		<html:text name="projectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="10" styleClass="text4" />
      </td>                           
      </logic:equal>
      <!--备注型 -->                              
      <logic:equal name="element" property="fieldtype" value="M">
      <td align="left" class="RecordRow" nowrap>                
         <html:text name="projectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength='<%="factorlist["+index+"].itemlen"%>' styleClass="text4"/>                               
      </td>                           
      </logic:equal>
      <!--字符型 -->                                                    
      <logic:equal name="element" property="fieldtype" value="A">
      <td align="left" class="RecordRow" nowrap>
         <logic:notEqual name="element" property="codeid" value="0">
            <html:hidden name="projectForm" property='<%="factorlist["+index+"].value"%>' styleClass="text4"/>                               
            <html:text name="projectForm" property='<%="factorlist["+index+"].hzvalue"%>' size="30" maxlength="50" styleClass="text4" onchange="fieldcode(this,1)"/>
            <logic:notEqual name="element" property="codeid" value="UN">                                
                <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
            </logic:notEqual>   
            <logic:equal name="element" property="codeid" value="UN">
                <logic:equal name="projectForm" property="type" value="2"> 
                    <img src="/images/code.gif" onclick='openCondCodeDialog("UM","<%="factorlist["+index+"].hzvalue"%>");'/>
                </logic:equal> 
                <logic:notEqual name="projectForm" property="type" value="2">
                    <img src="/images/code.gif" onclick='openCondCodeDialog("${element.codeid}","<%="factorlist["+index+"].hzvalue"%>");'/>
                </logic:notEqual>                                       
            </logic:equal>                                                                                                       
         </logic:notEqual> 
         <logic:equal name="element" property="codeid" value="0">
              <html:text name="projectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
         </logic:equal>                               
      </td>                           
      </logic:equal> 
      <!--数据值-->                            
      <logic:equal name="element" property="fieldtype" value="N">
      <td align="left" class="RecordRow" nowrap>                
          <html:text name="projectForm" property='<%="factorlist["+index+"].value"%>' size="30" maxlength="${element.itemlen}" styleClass="text4"/>                               
      </td>                           
      </logic:equal>                 
   </tr>  
    <%i++;%>
   </logic:iterate>
</table>
<table border="0"  cellspacing="0" width="85%" class="ListTable1"  cellpadding="2" align="center">
  <tr>
  	<td width="75%">
  		 <span><bean:message key="label.query.expression"/></span><br>
          <html:textarea name="projectForm" property="expression" onclick="this.pos=document.selection.createRange();" rows="4" cols="38"/>
  	</td>
  	<td valign="BOTTOM">
  		<table border="0"  cellspacing="0" width="100%" class="ListTable1"  cellpadding="2" align="center">
  			<tr>
  				<td><input name="arebrackets" type="button" class="btn2" id="arebrackets" onclick="symbol('(');" value="（"></td>
  				<td><input name="and" type="button" class="btn2" id="and" onclick="symbol('*');" value="<bean:message key='kq.formula.even'/>"></td>
  				<td rowspan="2"><input name="no" type="button" class="btn1" id="no" onclick="symbol('!');" value="<bean:message key='kq.formula.not'/>"></td>
  			</tr>
  			<tr>
  				<td><input name="antibrackets " type="button" class="btn2" id="antibrackets" onclick="symbol(')');" value="）"></td>
  				<td><input name="or " type="button" class="btn2" id="or" onclick="symbol('+');"  value="<bean:message key='kq.formula.or'/>"></td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <tr>
  	<td>
  		<input type="hidden" name="arr">
  		<input type="button" name="button1"  value="<bean:message key='button.query.pre'/>" onclick="symup();" Class="mybutton" > 
  		<input type="button" name="button2"  value=" <bean:message key='button.ok'/> " onclick="savecrond();" Class="mybutton" > 
  		<input type="button" name="button3"  value=" <bean:message key='button.cancel'/> " onclick="window.close();" Class="mybutton" > 
  	</td>
  	<td>&nbsp;</td>
  </tr>
  </table>
</html:form>