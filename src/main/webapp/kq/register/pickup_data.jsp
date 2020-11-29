<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script> 
 var url="/kq/register/daily_registerdata.do?b_pickup=link" ;   //要加载的目标页 

 function window.onload()
 { 
   location=url;                       //网页加载后转到目标页 
 }  
</script>
<html:form action="/kq/register/daily_registerdata">
<table align="center" width="80%" valign='middle'height="300">
  <tr>
     <td valign='middle'>
         <table border="1" width="37%" cellspacing="0" cellpadding="4" style="border-collapse: collapse" bgcolor="#F7FAFF" height="87" align="center">
           <tr>
             <td bgcolor="#3399FF" style="font-size:12px;color:#ffffff" height=24><bean:message key="kq.register.pickup1.go"/>...</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center><bean:message key="kq.register.pickup2.go"/>...

               <marquee style="border:1px solid #000000" direction="right" width="300" scrollamount="5" scrolldelay="10" bgcolor="#ECF2FF">
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
     </td>
  </tr>
</table>
</html:form>