
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.func.FunctionMainForm" %>
<html>

  <hrms:themes></hrms:themes>
  <script LANGUAGE=javascript src="/js/function.js"></script> 
 	<script LANGUAGE=javascript src="/system/bos/func/funcment.js"></script>  
  <script language="javascript">
     function subSave()
  {
		
		var function_id =getEncodeStr(trim(document.getElementsByName("editfunction_id")[0].value));
		var function_name =getEncodeStr(trim(document.getElementsByName("editfunction_name")[0].value));
		if(function_id.length==0)
		{
			alert("功能号不能为空");
			return;
		}
	 	if(function_name.length==0)
		{
			alert("功能名称不能为空");
			return;
		}
		var prefunc_id=getEncodeStr(document.getElementsByName("prefunc_id")[0].value);
	 		document.functionMainForm.action="/system/bos/func/functionMain.do?b_findedit=edit&function_id="+function_id+"&function_name="+$URL.encode(function_name)+"&prefunc_id="+prefunc_id;
			document.functionMainForm.submit();
  
  	}
  	<%  
  	 if(request.getParameter("b_findedit")==null)
 	 	{
		 	FunctionMainForm functionMainForm=(FunctionMainForm)session.getAttribute("functionMainForm");
		 	
			functionMainForm.setEditfunction_id(functionMainForm.getCodeitemid()); 
			functionMainForm.setEditfunction_name(functionMainForm.getCodeitemdesc());
		}
  	
  	
  
  	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){ %> 
	 	
	 	var funcflag="${functionMainForm.funcflag}";
	 	var precodeitemid="${functionMainForm.precodeitemid}";
	 	var editfunction_id="${functionMainForm.editfunction_id}";
	 	if(funcflag=='true'&&precodeitemid!=editfunction_id)
	 	{
	 		alert("该功能号id已存在,请重新输入!");
	 		
	 	}

	<% }
	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){
	 %>
	 	var funcflag="${functionMainForm.funcflag}";
	 	var precodeitemid="${functionMainForm.precodeitemid}";
	 	var editfunction_id="${functionMainForm.editfunction_id}";
	 	if(funcflag=='true'&&precodeitemid==editfunction_id||funcflag=='false')
	{
  		 var func_base_vo = new Object();
		    func_base_vo.function_id = editfunction_id;
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
  <body>
  <html:form action="/system/bos/func/functionMain">
  <html:hidden styleId="prefunc_id" name="functionMainForm" property="precodeitemid" />
  <table width="390" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
          <thead>
            <tr> 
              <td align="left" class="TableRow"><bean:message key="lable.func.main.editfunc"/>&nbsp; </td>
            </tr>
          </thead>
          <tr>
           <td align="center" class="RecordRow" nowrap>
          		<table border='0' >
          	  
                <tr>
                   <td align="right" height="30" ><bean:message key="lable.func.main.id"/></td>
                   <td><html:text name="functionMainForm" property="editfunction_id" maxlength="20"  styleClass="textColorWrite" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/></td>
                </tr>
               
                <tr>
                    <td align="right" height="30" ><bean:message key="lable.func.main.name"/></td>
                    <td><html:text name="functionMainForm" property="editfunction_name" maxlength="20"  styleClass="textColorWrite" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/></td>
                </tr> 
               
                      
		</table>
       </td>
	 </tr>
	 
    </table>
   
  	<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;"> 
            
         	 <button extra="mybutton" id="clo1" onclick="subSave()"  allowPushDown="false" down="false"><bean:message key="lable.func.main.save"/></button>
         	 
         	  <input type="button" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="can();">
             
            </td>
          </tr>          
     </table> 
  
  
    </html:form>			                               
  </body>
</html>
