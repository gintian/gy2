<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
   
  </head>
  
  <body>
   
   <table width='100%' border=0>
   <tr>   
   <td>
   <table width="99%"   border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <tr>
         <td align="left"  class="TableRow" nowrap>        	
        	 <textarea name="message" cols="57" rows="20" class="text5"></textarea>         	
         </td>
        </tr>            
    </table>    
    </td>
   </tr>
   <tr>
     <td align="center">
        <html:button styleClass="mybutton" property="orgmapset" onclick="winclose();">
			<bean:message key="button.close"/>
		</html:button>	
     </td>
   </tr>
 </table>
   <script language='javascript'>
   </script>
  <script language='javascript'>
  var info;
  if(parent.Ext && parent.Ext.getCmp('send_result')){
	var win = parent.Ext.getCmp('send_result');
	info = win.arguments;
  }else{
  	info=dialogArguments;
  }
          
  //var expr_editor=document.getElementById('message');
  document.getElementsByName('message')[0].innerText = info[0];
  //alert(expr_editor.style.visible);
 // if(null != expr_editor && ""!= expr_editor.style.display){  
 //	 expr_editor.focus(); 
 // }  
 // var element = document.selection;
 // if (element!=null) 
 // {
//	  	var rge = element.createRange();
////	   	if (rge!=null)	
//	  	     rge.text=info[0];
 // }
 //关闭弹窗方法  wangb 20190320
 function winclose(){
 	if(parent.Ext && parent.Ext.getCmp('send_result')){
			var win = parent.Ext.getCmp('send_result');
			win.close();
			return;
	}
 	window.close();
 }
  </script>
  </body>
</html>
