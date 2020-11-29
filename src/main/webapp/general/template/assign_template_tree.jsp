<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<script language="jscript">
	   Global.defaultInput=1;
	   Global.showroot=false;
	   if(<bean:write name="resourceForm" property="type"/>==15){
	   	Global.defaultchecklevel=3;
	   }else{
	   	Global.defaultchecklevel=2;
	   }
	   Global.checkvalue="<bean:write name="resourceForm" property="law_dir" />";	  
	   function save()
	   {
	   	  var str_id=root.getSelected();
	   	  //if(str_id=="")
	   	  //  return;
          var hashvo=new ParameterSet();
          hashvo.setValue("flag","<bean:write name="resourceForm" property="flag" />");
          hashvo.setValue("roleid","<bean:write name="resourceForm" property="roleid" />");	        
          hashvo.setValue("res_flag","<bean:write name="resourceForm" property="res_flag" />");
          hashvo.setValue("law_dir",str_id);
   　       var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'10400201021'},hashvo);        
	   }
	   function save_ok(outparamters)
	   {
		   var isCorrect = outparamters.getValue("isCorrect");
		   if("true"==isCorrect){
			   alert("保存成功!");
		   }
	   }
</script>

<html:form action="/general/template/assign_template_tree"> 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
			<tr align="left">
				<td valign="top">
					<html:button styleClass="mybutton" property="b_save" onclick="save();"><bean:message key="button.save"/></html:button>
					<html:button styleClass="mybutton" property="b_all" onclick="root.allSelect();"><bean:message key="label.query.selectall"/></html:button>
					<html:button styleClass="mybutton" property="b_clear" onclick="root.allClear();"><bean:message key="label.query.clearall"/></html:button>
				</td>
			</tr>			 	            
         <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="resourceForm" property="template_tree" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>  
<script type="text/javascript">
	root.expandAll();
</script>    
</html:form>
