<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
 function batchupdate()
   {
      //userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_batchupdate=link";
      //userManagerForm.target="mil_body";
      //userManagerForm.submit();
      var count_start=document.userManagerForm.kq_code.value;
      window.returnValue=count_start;
	  window.close();  
   }
  function push()
  {
     
     parent.document.location.reload();
   
  }
</script>
<body>

<html:form action="/kq/options/manager/usermanagerdata">
<div class="fixedDiv3">
 <table  width="100%" border="0" cellpadding="1" cellspacing="0" align="center" >   
                             <tr height="20">
       		                <!--  <td width=10 valign="top" class="tableft"></td>
       		               <td width=190 align=center class="tabcenter">批量更改考勤方式</td>   
       		               <td width=15 valign="top" class="tabright"></td>
       		               <td valign="top" class="tabremain" width="150"></td>--> 
       		               <td  align=center class="TableRow">批量更改考勤方式</td>          		           	      
                               </tr>                                         
         <tr>
	   <td width="100%" valign="middle" class="framestyle9" >       
               <br>
               <table width="100%" border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" align="center" >
                <tr> 
                 <td align="right" class="tdFontcolor" nowrap > 考勤方式 &nbsp; </td>                
                 <td align="left" class="tdFontcolor" nowrap>
                   <hrms:optioncollection name="userManagerForm" property="codelist" collection="list" />
	           <html:select name="userManagerForm" property="kq_code" size="1" >
                    <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select> 
                 </td>
                <!--数据值-->
               </tr>
              </table>
            <br>
          </td>
       </tr>
         <tr align="center" class="list3"> 
          <td height="35"> 
             <input type="button" name="btnreturn" value='确定' onclick="batchupdate();window.close();" class="mybutton"> 
              <input type="button" name="btnreturn" value='关闭' onclick="window.close();" class="mybutton"> 
            
          </td>
          </tr>  
</table>
</div>
</html:form>
</body>
