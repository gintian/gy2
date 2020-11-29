<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   String table=(String)request.getParameter("table");
   if(table==null)
     table="";
%>
<script language="javascript">
	function valide(falg)
  {  
   	var dd = document.getElementById("un11").value;
   	if(dd==""||dd.length<0)
   	{
   		alert("请选择审核意见!");
   		return;
   	}
      //if(text=="")
      //{
      //  alert("审批意见不能为空！");
      //  return false;
     // }
     var radio_ob =document.getElementsByName("radio");  
     var radio1="";  
     if(radio_ob!=null)
     {
        for(var i=0;i<radio_ob.length;i++)
        {
           if(radio_ob[i].checked==true)
             radio1=radio_ob[i].value;
        }
     } 
     if(radio1=="")
     {
       alert("是否同意！");
       return false;
     }     
     if(confirm("确定对所选记录进行批量签批吗?"))
     {
       var thevo=new Object();
       thevo.radio=radio1;
       thevo.text=dd;
       thevo.falg=falg;
       window.returnValue=thevo;
	   window.close();
     }
     
  }
</script>
<html:form  action="/kq/app_check_in/all_app_data">
<br>
<fieldset align="center" style="width:50%;">
<legend ><bean:message key="kq.approve.idea"/></legend>
	<table width="250" border="0" cellpadding="0"  cellspacing="0" align="center">
		<tr class="list3">
		<%
           if(table.equalsIgnoreCase("q11")){
         %>
         <hrms:priv func_id="27010c,0C341c">
			<tr>
			<td align="left" nowrap>
				审核意见:
				<hrms:optioncollection name="appForm" property="group11"
								collection="list" />
							<html:select name="appForm" property="unit11" size="1" styleId="un11">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
			</td>
			</tr>
		</hrms:priv>
		<%
           }else if(table.equalsIgnoreCase("q13")){
         %>
         <hrms:priv func_id="27012c,0C343c">
			<tr>
			<td nowrap align="left">
				审核意见:
				<hrms:optioncollection name="appForm" property="group11"
								collection="list" />
							<html:select name="appForm" property="unit11" size="1" styleId="un11">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
			</td>
			</tr>
         </hrms:priv>
         <%
           }else if(table.equalsIgnoreCase("q15")){
         %>
         <hrms:priv func_id="27011c,0C342c">
			<tr>
			<td align="left" nowrap>
				审核意见:
				<hrms:optioncollection name="appForm" property="group11"
								collection="list" />
							<html:select name="appForm" property="unit11" size="1" styleId="un11">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
			</td>
			</tr>
         </hrms:priv>
         <%
           }
         %>
		</tr>
		<tr class="list3">
            <td nowrap align="left">
            	<html:radio name="appForm" property="radio" value="01"  /><bean:message key="label.agree"/>&nbsp;&nbsp;&nbsp;
            	<html:radio name="appForm" property="radio" value="02"  /><bean:message key="label.nagree"/>&nbsp;&nbsp;&nbsp;
             </td>
        </tr>  
	</table>
</fieldset>
<br>
       <br>
    <table width="200" border="0" cellpmoding="0" cellspacing="0"  align="center"   cellpadding="0">                                                 
        <tr class="list3">
        <td align="center" colspan="2">
         <%
           if(table.equalsIgnoreCase("q11")){
         %>
           <hrms:priv func_id="27010c,0C341c"> 
              <input type="button"  value="审核" class="mybutton" onclick="valide('02')">  
           </hrms:priv>
         <%
           }else if(table.equalsIgnoreCase("q13")){
         %>
           <hrms:priv func_id="27012c,0C343c"> 
              <input type="button"  value="审核" class="mybutton" onclick="valide('02')">  
           </hrms:priv>
         <%
           }else if(table.equalsIgnoreCase("q15")){
         %>
           <hrms:priv func_id="27011c,0C342c"> 
              <input type="button"  value="审核" class="mybutton" onclick="valide('02')">  
           </hrms:priv>
         <%
           }
         %>
              
                 
         <input type="reset"  value="<bean:message key="button.clear"/>" class="mybutton">
         <input type="button"  value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();"> 
        </td>
       </tr>          
      </table>
	
</html:form>
