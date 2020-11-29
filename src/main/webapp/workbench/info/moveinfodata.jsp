<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
.TableRow{
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
</style>
<script language="javascript">
function moverec()
{
   var o_obj=document.getElementsByName('b_move');  
   if(o_obj)
   {
      var obj=o_obj[0];
      obj.disabled = true;      
   }
   
   
   var wait = document.getElementById("wait");
   if(wait)
	   wait.style.display="";
   
   selfInfoForm.action="/workbench/info/moveinfodata.do?b_move=link";
   selfInfoForm.submit();
   movePersonFlag();
}

function movePersonFlag() {
	var hashVo=new ParameterSet();
	hashVo.setValue("moveFlag", 1);
	var request=new Request({method:'post',asynchronous:true,onSuccess:callBackFun,functionId:'0202001021'},hashVo);
}

function callBackFun(flag) {
	setTimeout("getExportFileName()",10000);
}
</script>
<hrms:themes />
<html:form action="/workbench/info/moveinfodata">
<table width="40%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="margin-top: 8px">
  <thead>
     <tr>
        <td align="left"  nowrap class="TableRow">
     	      <bean:message key="workbench.info.infomove"/>
    	</td>         
     </tr>
 </thead>
<tr><td>&nbsp;&nbsp;</td></tr>
 <tr>
    <td align="center"  nowrap >
     	     <bean:message key="label.query.dbpre"/>&nbsp;
    	         <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="selfInfoForm.todbcond" collection="list" scope="page"/>
              <html:select name="selfInfoForm" property="touserbase" size="1">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>
	    </td>         
   </tr>
   <tr><td>&nbsp;&nbsp;</td></tr>  
 </table>
 <table width="40%" border="0" cellspacing="0"  align="center" cellpadding="0">
     <tr>
     	<td height="5px"></td>
     </tr>
     <tr>
            <td align="center">
              <logic:notEqual name="selfInfoForm" property="ismove" value="no">                
	 	        <input type="button" name="b_move"  value="<bean:message key="button.ok"/>" class="mybutton" onclick='moverec()'>  	
	 	</logic:notEqual>
	 	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
            </td>
          </tr> 
 </table>
</html:form>
<div id='wait' style='position:absolute;top:45%;left:35%;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height="24"><bean:message key="workbench.info.moving"/></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
