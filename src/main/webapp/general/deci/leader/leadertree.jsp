<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html:form action="/general/deci/leader/leaderframe"> 

  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >   
	         
         <tr>        
           <td align="left" colspan="2" > 
                 <hrms:orgtree action="/general/deci/leader/leaderframedata.do?b_search=link" target="mil_body" flag="0"  priv="1" showroot="false" dbpre="" lv="1" loadtype="${leaderForm.loadtype}"/>			           
           </td>
         </tr>            
   </table>
</html:form>