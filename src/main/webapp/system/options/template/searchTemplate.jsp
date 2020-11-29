<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
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
	//this.status ="招聘管理 / 通知模板";
	function add()
	{
		templateSetForm.action='/sys/options/template/searchTemplate.do?b_add=link&templateType=32&zploop=1';
    	templateSetForm.submit();
	}
	function del()
	{
		var num=0;
		
		for(var i=0;i<templateSetForm.elements.length;i++)
		{
			if(templateSetForm.elements[i].type=='checkbox'&&templateSetForm.elements[i].checked==true)
				num++;
				
		}
			
		if(num==0)
		{
			alert("请选择需删除的通知模板！");
			return;
		}
	
		if(confirm("请确认删除通知模板?"))
		{
			templateSetForm.action='/sys/options/template/searchTemplate.do?b_delete=delete';
	    	templateSetForm.submit();
	    }
	}
	function SearchType(){
	   templateSetForm.action='/sys/options/template/searchTemplate.do?b_query=query&queryType=type&opt=2';
	   templateSetForm.submit();
	}
	function ZploopSearchType(){
	
	  var zploop_list = templateSetForm.zpLoop;
	  for(var i=0;i<zploop_list.length;i++){
	      if(zploop_list.options[i].selected == true){
	         templateSetForm.action='/sys/options/template/searchTemplate.do?b_query=query&queryType=zploop&opt=2';
	         templateSetForm.submit();
	       }
	  }
    }

 </script>
  </head>
  <body>
    <html:form action="/sys/options/template/searchTemplate">
	<%
	    if(bosflag!=null&&!bosflag.equals("hcm")){
	%>
	<Br>
	<%
	}
	%>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:10px;">
   	  <thead>
   	   
   	  <tr height="25">
      <td align="left"  nowrap colspan="7"  class="TableRow"> 
           <bean:message key="lable.tz_template.template_type3"/>
             <html:radio property="template_type" value="#" onclick="SearchType();"/>全部
            <html:radio property="template_type" value="0" onclick="SearchType();"/><bean:message key="lable.tz_template.template_type1"/>
           <html:radio property="template_type" value="1" onclick="SearchType();"/><bean:message key="lable.tz_template.template_type2"/>
          
              <logic:equal name="templateSetForm" property="id" value="32">
                <bean:message key="lable.tz_template.zploop1"/> 
                 <hrms:optioncollection name="templateSetForm" property="zpLoop_list" collection="list"   />
		             <html:select name="templateSetForm" property="zpLoop" size="1" onchange="ZploopSearchType();">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
              </logic:equal>
      </td>
     </tr>  
        
	 <tr>
            <td align="center" class="TableRow" nowrap>
             <bean:message key="column.select"/>&nbsp;&nbsp;
             </td>
            <logic:equal name="templateSetForm" property="id" value="32">
            <td align="center" class="TableRow" nowrap>
            <bean:message key="lable.tz_template.zploop2"/>
            </td>
            </logic:equal>
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="lable.tz_template.name"/>
	   	 	</td>
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="lable.tz_template.title"/> 
	   		 </td>
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="lable.tz_template.address"/>	
	    	</td>            
		    <td align="center" class="TableRow" nowrap>
			<bean:message key="lable.tz_template.template_type4"/>	
		    </td>
		     <td align="center" class="TableRow" nowrap>
				<bean:message key="lable.tz_template.edit"/>    	
		    </td>	        	        	        
         </tr>
   	  </thead>
   	    <% int i=0; String className="trShallow"; %>
   	   <logic:iterate id="element" name="templateSetForm" property="alist"  offset="0"> 
	   	      <tr>
            <td align="center" class="RecordRow" nowrap>
            <html:multibox property="selected_template_id_array"><bean:write name="element" property="template_id" /></html:multibox>
            </td>  
             <logic:equal name="templateSetForm" property="id" value="32">
            <td align="center" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="codeitemdesc" />&nbsp;
            </td> 
             </logic:equal> 
            <td align="center" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="name" />&nbsp; 
	         
	   	 	</td>
            <td align="center" class="RecordRow" nowrap>
	          &nbsp;<bean:write name="element" property="title" />&nbsp;	 
	   		 </td>
            <td align="center" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="address" />&nbsp;
	    	</td>            
		    <td align="center" class="RecordRow" nowrap>
			&nbsp;<bean:write name="element" property="template_type" />&nbsp;
		    </td>
		     <td align="center" class="RecordRow" nowrap>
		     <bean:define id="template_id" name="element" property="template_id"/>
	         <%
	         	String str1 = "template_id="+template_id.toString();
	         	
	         %>
		     <a href="/sys/options/template/searchTemplate.do?b_edit=edit&encryptParam=<%=PubFunc.encrypt(str1)%>">
			   <img src="/images/edit.gif" border=0> 
			 </a>  	
		    </td>	        	        	        
         </tr>
   	     
   	     </logic:iterate>
   	</table> 
   	<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;"> 
            
              <input type="button" name="b_add" value="<bean:message key="lable.tz_template.new"/>" class="mybutton" onClick="add();">
         	 
         	 
         	  <input type="button" name="b_delete" value="<bean:message key="lable.tz_template.delete"/>" onclick="del();" class="mybutton">
             
            </td>
          </tr>   
          <html:hidden property="id"/>   
</table>  
   	  </html:form>
    
  </body>
</html>
