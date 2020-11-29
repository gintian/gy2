<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   String table = (String)request.getParameter("table");
   if(table == null)
	   table = "";
   String opinionlength=(String)request.getParameter("opinionlength");
%>
<script language="javascript">
  
  function valide(falg)
  {  
	  var opinionlength=<%=opinionlength%>
      var text=$F('result');
      //if(text=="")
      //{
      //  alert("审批意见不能为空！");
      //  return false;
     // }
     var radio1="01";  
     /*
     var radio_ob =document.getElementsByName("radio");  
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
     */
     var len = 0;
     for(var i=0;i<text.length;i++){
		if(text.charCodeAt(i) > 255)
			len += 2;
		else
			len += 1;
		if(len > opinionlength){
			var length="";
			if(opinionlength%2!=0)
			{
				length=(opinionlength-1)/2;
			}else
			{
				length=opinionlength/2;
			}
			alert("审批意见请控制字数在"+length+"个汉字或"+opinionlength+"个字符之内！");
			return false;			
		}
	 }
     if(confirm("确定对所选记录进行批量审批吗?"))
     {
       var thevo=new Object();
       thevo.radio=radio1;
       thevo.text=text;
       thevo.falg=falg;
       window.returnValue=thevo;
	   window.close();
     }
     
  }
	

</script>
<html:form  action="/kq/app_check_in/all_app_data">
	<div class="fixedDiv2" style="height: 100%;border: none">
<fieldset align="center" style="width:100%;">
 <legend ><bean:message key="kq.approve.idea"/></legend>
  <table width="95%" border="0" cellpadding="0"  cellspacing="0" align="center">
   
         <tr class="list3">
            <td class="framestyle9" align="center" valign="middle" style="border: 0px;">
            	<html:textarea name="appForm" property="result"   cols="49" rows="20" style="height:110px;width:98%;font-size:9pt"/>
             </td>
            </tr>
            <tr class="list3">
              <td style="display:none">
            	<html:radio name="appForm" property="radio" value="01"/><bean:message key="label.agree"/>&nbsp;&nbsp;&nbsp;
            	<html:radio name="appForm" property="radio" value="02"/><bean:message key="label.nagree"/>&nbsp;&nbsp;&nbsp;
              </td>
            </tr>      
   </table>        
    </fieldset>
 
    <table width="250" border="0" cellpmoding="0" cellspacing="0"  align="center"   cellpadding="0">                                                 
        <tr class="list3" style="padding-top: 5px">
        <td align="center" colspan="2">
         <%
           if(table.equalsIgnoreCase("q11")){
         %>
           <hrms:priv func_id="270102,0C3412">  
             <input type="button"  value="批准" class="mybutton" onclick="valide('03')"> 
           </hrms:priv>
           <hrms:priv func_id="27010c,0C341c"> 
              <input type="button"  value="审核" class="mybutton" onclick="valide('02')">  
           </hrms:priv>
         <%
           }else if(table.equalsIgnoreCase("q13")){
         %>
           <hrms:priv func_id="270122,0C3432">  
             <input type="button"  value="批准" class="mybutton" onclick="valide('03')"> 
           </hrms:priv>
           <hrms:priv func_id="27012c,0C343c"> 
              <input type="button"  value="审核" class="mybutton" onclick="valide('02')">  
           </hrms:priv>
         <%
           }else if(table.equalsIgnoreCase("q15")){
         %>
           <hrms:priv func_id="270112,0C3422">  
             <input type="button"  value="批准" class="mybutton" onclick="valide('03')"> 
           </hrms:priv>
           <hrms:priv func_id="27011c,0C342c"> 
              <input type="button"  value="审核" class="mybutton" onclick="valide('02')">  
           </hrms:priv>
         <%
           }
         %>
         <%
         	if(table.equalsIgnoreCase("Q19")){
         %>
         	<input type="button" value="批准" class="mybutton" onclick="valide('03')">
            <input type="button"  value="审核" class="mybutton" onclick="valide('02')">
         <%
         	} else if (table.equalsIgnoreCase("Q25")){
         %>
         	<input type="button" value="批准" class="mybutton" onclick="valide('03')">
            <input type="button"  value="审核" class="mybutton" onclick="valide('02')">
         <%
         	}
         %>
                 
         <input type="reset"  value="<bean:message key="button.clear"/>" class="mybutton">
         <input type="button"  value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();"> 
        </td>
       </tr>          
      </table>
     </div>
</html:form>
