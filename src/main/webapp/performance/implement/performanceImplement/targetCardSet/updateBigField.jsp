<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.implement.ImplementForm" %>

<html>
  <head>
   
  </head>
  <script language='javascript'>
  	function enter(theflag)
  	{
  		var thevo=new Object();
		thevo.flag=true;
		var targetPointCol="${implementForm.targetPointCol}";  
		
		var subStr = 'p04ad';
		if((targetPointCol.toLowerCase()==subStr.toLowerCase()) && ((document.getElementById('theValue').value).length>100))
		{
			alert("描述备注100"+OBJECTCARDINFO12);
			return;
		}		
		thevo.theValue=getEncodeStr(document.getElementById('theValue').value);
      if(window.showModalDialog){
        parent.window.returnValue=thevo;
      }else{
        if(parent.parent.updateBigField_ok){
          parent.parent.updateBigField_ok(thevo);
        }
      }
      closeWindow();
  	}
    function closeWindow(){
  	    if(window.showModalDialog){
  	        parent.window.close();
        }else{
  	       if(parent.parent.Ext && parent.parent.Ext.getCmp("updateBigFieldWin")){
             parent.parent.Ext.getCmp("updateBigFieldWin").close();
           }else{
             parent.parent.window.close();
           }
        }
    }
  </script>
   
<hrms:themes />
  <body>
  <form name='form1'>
    <table>
    <tr><td colspan='3'>

    </td></tr>
    <tr>
    <td> 
    	<html:textarea  style="width:470px;height:360px;" name="implementForm" property="targetMemoField" styleId="theValue"></html:textarea>
    </td>
    </tr>
    <tr>

    <td align='center' >
    
     <input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="lable.tz_template.enter"/>'  />

 	 <input type='button' class="mybutton"   onclick='closeWindow()' value='<bean:message key="lable.tz_template.cancel"/>'  />
    </td>
    </tr>
    
    </table>
  </form> 
  </body>
</html>
