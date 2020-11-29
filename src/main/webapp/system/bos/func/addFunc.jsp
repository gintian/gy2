
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.func.FunctionMainForm" %>
<html>

  <hrms:themes></hrms:themes>
  <script LANGUAGE=javascript src="/js/function.js"></script> 
  <script language="javascript">
 
   function sub()
  {

		if(trim(document.getElementsByName("addfunction_id")[0].value).length==0)
		{
			alert("功能号不能为空");
			return;
		}
		var infor =getEncodeStr(trim(document.getElementsByName("addfunction_id")[0].value));
		var function_name =getEncodeStr(trim(document.getElementsByName("addfunction_name")[0].value));
		var parentid =getEncodeStr(trim(document.getElementsByName("parentid")[0].value));
	 	if(function_name.length==0)
		{
			alert("功能名称不能为空");
			return;
		}
		
	 	document.functionMainForm.action="/system/bos/func/functionMain.do?b_findadd=add&function_id="+$URL.encode(infor)+"&function_name="+$URL.encode(function_name)+"&parentid="+$URL.encode(parentid);
		document.functionMainForm.submit();		
   }
 

	<%
	
	 if(request.getParameter("b_findadd")==null)
 	 	{
		 	FunctionMainForm functionMainForm=(FunctionMainForm)session.getAttribute("functionMainForm"); 
			functionMainForm.setAddfunction_id(""); 
			functionMainForm.setAddfunction_name("");
		}
	 if(request.getParameter("b_findadd")!=null&&request.getParameter("b_findadd").equals("add")){ %> 
	 	
	 	var funcflag="${functionMainForm.funcflag}";
	 	if(funcflag=='true')
	 	{
	 		alert("该功能号已存在,请重新输入!");
	 		
	 	}

	<% }
	if(request.getParameter("b_findadd")!=null&&request.getParameter("b_findadd").equals("add")){
	 %>
	 	var funcflag="${functionMainForm.funcflag}";
	 	if(funcflag=='false'){
	var function_name ="${functionMainForm.addfunction_name}";
	var parentid ="${functionMainForm.parentid}";
	var function_id="${functionMainForm.addfunction_id}";
  		// document.functionMainForm.action="/system/bos/func/functionMain.do?b_SaveFunc=link&function_name="+function_name+"&function_id="+function_id+"&parentid="+parentid;
		//document.functionMainForm.submit();
  		 var func_base_vo = new Object();
		    func_base_vo.function_name = function_name;
		    func_base_vo.function_id = function_id;
		   	func_base_vo.parentid=parentid;
		    window.returnValue = func_base_vo;
  	window.close();  	
  	}
<%}
	%>

    function can(){
  window.close();
  }
  function checkComments(s){
    var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）—|{}【】‘；：”“'。，、？%]");
    var rs = ""; 
    for (var i = 0; i < s.length; i++) { 
       rs = rs+s.substr(i, 1).replace(pattern, '');       
        } 
     return rs; 
    }
    function checkForm(){
     if(event.keyCode ==34){
        event.returnValue = false;
       }
     }
   </script>
  <body >
  <html:form action="/system/bos/func/functionMain">
  <html:hidden styleId="parentid" name="functionMainForm" property="parentid" />
  <table width="390" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
          <thead>
            <tr> 
              <td align="left" class="TableRow"><bean:message key="lable.func.main.addfunc"/>&nbsp; </td>
            </tr>
          </thead>
          <tr>
           <td align="center" class="RecordRow" nowrap>
          		<table border='0' >
          	
                <tr>
                   <td align="right" height="30" ><bean:message key="lable.func.main.id"/></td>
                   <td><html:text name="functionMainForm" property="addfunction_id" maxlength="20" styleClass="textColorWrite" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/></td>
                </tr>
                <tr>
                    <td align="right" height="30" ><bean:message key="lable.func.main.name"/></td>
                    <td><html:text name="functionMainForm" property="addfunction_name" maxlength="20" styleClass="textColorWrite" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/></td>
                </tr> 
               
                      
		</table>
       </td>
	 </tr>
	 
    </table>
   
  	<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;"> 
            
              <input type="button" name="b_add2" value="<bean:message key="lable.func.main.return"/>" class="mybutton" onClick="sub()">
         	 
         	 
         	  <input type="button" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="can();">
             
            </td>
          </tr>          
     </table> 
  
  
    </html:form>			                               
  </body>
</html>
