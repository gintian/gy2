<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.options.template.TemplateSetForm" %>
<%@ page import="java.util.*" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
      
  <hrms:themes></hrms:themes>
  <script language="javascript">
   function add(){
   if(document.templateSetForm.name.value ==""||document.templateSetForm.name.value ==" "){
       alert("请填写模板名称");
       return;
     }
     if(document.templateSetForm.title.value ==""||document.templateSetForm.title.value ==" "){
       alert("请填写邮件标题");
       return;
     }
     if(document.templateSetForm.address.value ==""||document.templateSetForm.address.value ==" "){
       alert("请填写回复邮件地址");
       return;
       }
      var emailStr=document.templateSetForm.address.value;
      var emailPat=/^[0-9a-zA-Z_\.\-]+@([0-9a-zA-Z_\-\.])+[\.]+[a-z]{2,4}$/;
      var matchArray=emailStr.match(emailPat);
      if (matchArray==null) {
      alert("请填写正确的邮件地址");
       return;
      }
       
     templateSetForm.action='/sys/options/template/searchTemplate.do?b_add2=add';
     templateSetForm.submit();
   }
	function changeFieldSet(){
	var v = templateSetForm.zbj_id.value;
	var zpoop = templateSetForm.zpLoop.value;
  	var hashvo=new ParameterSet();
  	
  	
    hashvo.setValue("setid",v);
    hashvo.setValue("zploop_id",zpoop);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010020906'},hashvo);					
  }
  
  function resultChangeFieldSet(outparamters){
  	//Element.hide('cid');
  	var fielditemlist=outparamters.getValue("zb_list");
	AjaxBind.bind(templateSetForm.zb_id,fielditemlist);
	
  }
	
	function insertTxt(strtxt)
	{
	    if(strtxt==null)
	   	 	return ;
	   
	    if((strtxt.toString()).indexOf("(")!=-1)
	     	strtxt="["+strtxt+"]";
	    
	    if(strtxt=="且"||strtxt=="如果"||strtxt=="或"||strtxt=="否则"||strtxt=="结束"||strtxt=="那么"||strtxt=="非")
	     {
	        var ddd=" "+strtxt+" ";
	        var expr_editor=$('content');
		    expr_editor.focus();
		    var element = document.selection;
		    if (element!=null) 
		    {
		  	  var rge = element.createRange();
		      if (rge!=null)	
		  	     rge.text=ddd;
		    }
	     }else{
		  	var expr_editor=$('content');
		    expr_editor.focus();
			var element = document.selection;
			if (element!=null) 
			{
			  var rge = element.createRange();
			  if (rge!=null)	
			  	  rge.text=strtxt;
			  }
		}
	}
	 function changeFieldItem(){
  	 var m = document.templateSetForm.zb_id.value;
  	 insertTxt(m);
  	 }
  	 function get(){
  	 templateSetForm.action="/sys/options/template/searchTemplate.do?b_change=change";
  	 templateSetForm.submit();
  	 }
  	 function can(){
  	 window.location.href="/sys/options/template/searchTemplate.do?b_query=link&templateType=32";
	}
   </script>
  
  <body >
  <html:form action="/sys/options/template/searchTemplate">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
  <table width="700px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:0px;">
          <thead>
            <tr> 
              <td align="left" class="TableRow" nowrap><bean:message key="lable.tz_template.template"/>&nbsp; </td>
            </tr>
          </thead>
          <tr>
           <td align="center" class="RecordRow" nowrap>
          		<table border='0' >
          		<logic:equal name="templateSetForm" property="id" value="32">
		          <tr> 
		            <td align="right" height='30'  nowrap><bean:message key="lable.tz_template.zploop2"/></td>
					<td>	
					  <hrms:optioncollection name="templateSetForm" property="zpLoop_list" collection="list"   />
		             <html:select name="templateSetForm" property="zpLoopNew" size="1" onchange="get();">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
   
					</td>
					<td>
					</td>
				 </tr> 
				 </logic:equal>
				 <tr>
				   <td align="right" height="30" nowrap><bean:message key="lable.tz_template.template_type4"/></td>
				   <td><html:radio property="type" value="0"/><bean:message key="lable.tz_template.template_type1"/>
				       <html:radio property="type" value="1"/><bean:message key="lable.tz_template.template_type2"/>
				   </td>
			    </tr> 
                <tr>
                   <td align="right" height="30" nowrap><bean:message key="lable.tz_template.name"/></td>
                   <td><html:text property="name" styleClass="TEXT4" /></td>
                </tr>
                <tr>
                    <td align="right" height="30" nowrap><bean:message key="lable.tz_template.title"/></td>
                    <td><html:text property="title" styleClass="TEXT4" /></td>
                </tr> 
                <tr>
                     <td algin="right" height="30" nowrap><bean:message key="lable.tz_template.address"/></td> 
                     <td><html:text property="address" styleClass="TEXT4" /></td>
                 </tr>
                 <tr><td algin="right" height="30" nowrap>可嵌入指标</td>
                 <td>
                  <hrms:optioncollection name="templateSetForm" property="zbj_list" collection="list"   />
		             <html:select name="templateSetForm" property="zbj_id" size="1" onchange="changeFieldSet();">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                 <hrms:optioncollection name="templateSetForm" property="zb_list" collection="list"   />
		             <html:select name="templateSetForm" property="zb_id" size="1" onchange="changeFieldItem();">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
                 </td>
                 </tr>
                 <tr>
                      <td align="right" width="70" nowrap><bean:message key="lable.tz_template.tz_content"/></td>
                      <td><html:textarea name="templateSetForm" property="content" cols="60" rows="20"/>
                    
                     </td> 
                 </tr>      
                      
		</table>
       </td>
	 </tr>
	 
    </table>
   
  	<table  width="70%" align="center">
          <tr>
            <td align="center"> 
            
              <input type="button" name="b_add2" value="<bean:message key="lable.tz_template.enter"/>" class="mybutton" onClick="javascript:add()">
         	 
         	 
         	  <input type="button" value="<bean:message key="button.return"/>" class="mybutton" onclick="can();">
             
            </td>
          </tr>          
     </table> 
     <html:hidden property="id"/>
  
    </html:form>			                               
  </body>
</html>
