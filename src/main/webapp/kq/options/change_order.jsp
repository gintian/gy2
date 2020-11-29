<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
   function showDataStr(outparamters)
    {
       var alllist=outparamters.getValue("orlist");
       AjaxBind.bind(kqItemForm.order,alllist);
    }
   function SearchDataParam(item_id)
    {
     
       var in_paramters="item_ids="+item_id;
       var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showDataStr,functionId:'15204110018'});
   }
  function save()
   {
      setselectitem('order');
      window.open("/kq/options/kq_item_detail.do",'il_body');
      //kqItemForm.submit();
      window.close();
   }	
   function saves()
  {
       setselectitem('order');
    	   kqItemForm.action="/kq/options/change_order.do?b_save=link";
      	 kqItemForm.target="_self";
         kqItemForm.submit();
         //window.close();
	  
  }
</script>
<html:form action="/kq/options/change_order">
<div class="fixedDiv3">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   <thead>
     <tr>
     <td align="center" class="TableRow" style="border-bottom: 0px;" nowrap>
	<bean:message key="kq.item.change.order"/>&nbsp;&nbsp;
     </td>            	        	        	        
     </tr>
   </thead>
   <tr>
      <td width="100%" align="center" class="RecordRow" nowrap>
          <table>
             <tr>
               <td width="46%" align="center">
                 <table width="100%">
                   <tr>
                     <td height="250" valign="top">   
                    <select name="order" multiple="multiple"  style="height:250px;width:100%;font-size:9pt">
                   </select>
                     </td>
                   </tr>
                 </table>             
               </td>
               <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('order'));">
            	       <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('order'));">
            	      <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                
             </tr>
         </table>             
       </td>
     </tr>
     <tr>
        <td align="center" class="RecordRow" nowrap style="height:35px;border-top: 0px;">
        <input type="button"  value="<bean:message key="button.ok"/>" class="mybutton" onclick="saves();"> 
	       <html:button styleClass="mybutton" property="cancel" onclick="window.close();"><bean:message key="button.cancel"/></html:button>       	       
	          	
         </td>
     </tr>     
</table></div>
</html:form>
<script language="javascript">
   SearchDataParam('<bean:write name="kqItemForm"  property="codeitemid"/>');
</script>