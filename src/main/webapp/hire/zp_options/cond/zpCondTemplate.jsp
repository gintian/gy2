 <%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.hire.zp_options.cond.ZpCondTemplateConstantForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  

 <html>
<head>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<SCRIPT type="text/javascript">
//this.status ="简历筛选模板";
function getFieldType(n){
zpCondTemplateConstantForm.action="/hire/zp_options/cond/zpCondTemplate.do?b_query=query&type="+n;
zpCondTemplateConstantForm.submit();
}
function show(n,m,isCheck){
var c=document.getElementById("a"+m);
var z=document.getElementById("b"+m);
var inputValue=document.getElementsByName(n);

var flag = document.getElementsByName(isCheck);
if(flag[0].checked){
c.style.display="block";
z.style.display="block";
for(var i=0;i<inputValue.length;i++){
inputValue[i].style.display="block";
}
flag[0].value="false";
}

if(flag[0].checked == false){

c.style.display="none";
z.style.display="none";
for(var i=0;i<inputValue.length;i++){
inputValue[i].style.display="none";
}
flag[0].value="true";
}
}
function sub(j){
 <%int n=0;%> 
  <logic:iterate id="element" name="zpCondTemplateConstantForm" property="fieldSetList" indexId="index"> 
	   	    
             <logic:equal name="element" property="itemtype" value="N">
            <logic:equal name="element" property="flag" value="true">
               var a<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].s_value");
               if(a<%=n%>[0].value !=''){
                  var myReg =/^(-?\d+)(\.\d+)?$/
		  if(!myReg.test(a<%=n%>[0].value)) 
		   {
		    alert("<bean:write  name="element" property="itemdesc"/>"+PLEASEWRITENUMBER+"！");
		    return;
		   }
		}
             
            </logic:equal>
               
              <logic:equal name="element" property="flag" value="false">
           var a<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].s_value");
           if(a<%=n%>[0].value !=''){
                var myReg =/^(-?\d+)(\.\d+)?$/
		        if(!myReg.test(a<%=n%>[0].value)) 
		        {
		            alert("<bean:write  name="element" property="itemdesc"/>"+BEGIN_VALUE_INPUT_NUMBER+"！");
		             return;
		         }
		    }
		var b<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].e_value");
		 var flagC = document.getElementsByName("fieldSetList[<%=n%>].flag");
		 if(flagC[0].checked){
         if(b<%=n%>[0].value !=''){
              var myReg =/^(-?\d+)(\.\d+)?$/
	           if(!myReg.test(b<%=n%>[0].value)) 
		       {
		         alert("<bean:write  name="element" property="itemdesc"/>"+END_VALUE_INPUT_NUMBER+"！");
		         return;
		       }
		 }
		 }
               </logic:equal>
             
              <logic:equal name="element" property="flag" value="false">
             
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
              
	   		 </logic:equal>
          </logic:equal>
            
            
            
         <logic:equal name="element" property="itemtype" value="D">
            
		      
		      <logic:equal name="element" property="flag" value="true"> 
		       var a<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].s_value");
		     
               if(a<%=n%>[0].value !=''){
                   if(!checkDateTime(a<%=n%>[0].value))
                   {
                       alert("<bean:write  name="element" property="itemdesc"/>"+VALUE_FORMAT_WRONG_RGIHT_IS+"！");
		               return;
                   }
               }
		      </logic:equal>
		      <logic:equal name="element" property="flag" value="false">
		       var a<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].s_value");
		        if(a<%=n%>[0].value !=''){
                   if(!checkDateTime(a<%=n%>[0].value))
                   {
                       alert("<bean:write  name="element" property="itemdesc"/>"+BEGIN_VALUE_FORMAT_WRONG_RGIHT_IS+"！");
		               return;
                   }
               }
                var b<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].e_value");
                 var flagC = document.getElementsByName("fieldSetList[<%=n%>].flag");
		      if(flagC[0].checked){
               if(b<%=n%>[0].value !=''){
                   if(!checkDateTime(b<%=n%>[0].value))
                   {
                       alert("<bean:write  name="element" property="itemdesc"/>"+END_VALUE_FORMAT_WRONG_RGIHT_IS+"！");
		               return;
                   }
               }
               }
		      </logic:equal> 
		      
		       <logic:equal name="element" property="flag" value="false">
              
	   		 </logic:equal>
	   	     <logic:equal name="element" property="flag" value="true"> 
	   		 </logic:equal>
		 </logic:equal>
			 
	<logic:equal name="element" property="itemtype" value="A">
	 <logic:notEqual name="element" property="codesetid" value="0">
	       
			    
	 <logic:equal name="element" property="flag" value="true">
	 
			    
</logic:equal>
 <logic:equal name="element" property="flag" value="false">
  
			 
   </logic:equal>
			  
			     <logic:equal name="element" property="flag" value="false">
              
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
              
	   		 </logic:equal>
	 </logic:notEqual>
	 
	<logic:equal name="element" property="codesetid" value="0">
			    
			    <logic:equal name="element" property="flag" value="true">
			    var a<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].s_value");
			    if(a<%=n%>[0].value !='')
			    {
			       if(IsOverStrLength(a<%=n%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
							return;
						}
					}
			    
			   
			   
			    </logic:equal>
			    <logic:equal name="element" property="flag" value="false">
			     var a<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].s_value");
			    if(a<%=n%>[0].value !='')
			    {
			       if(IsOverStrLength(a<%=n%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+BEGIN_VALUE_OVER_LENGTH_SCOPE);
							return;
						}
					}
			
                             var b<%=n%>=document.getElementsByName("fieldSetList[<%=n%>].e_value");
			    if(b<%=n%>[0].value !='')
			    {
			       if(IsOverStrLength(b<%=n%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+END_VALUE_OVER_LENGTH_SCOPE);
							return;
						}
					}			   
			    </logic:equal>
			   <logic:equal name="element" property="flag" value="false">
              
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
             
	   		 </logic:equal>
	 </logic:equal>
     </logic:equal>
	         
        <%n++;%>
 </logic:iterate>

for(var i=0;i<j;i++){
var tem=document.getElementsByName("fieldSetList["+i+"].flag");
if(tem[0].value == "true"){
tem[0].checked = true;
}
}


zpCondTemplateConstantForm.action="/hire/zp_options/cond/zpCondTemplate.do?b_save=save";
zpCondTemplateConstantForm.submit();
}
function modifySimple()
{
   zpCondTemplateConstantForm.action="/hire/zp_options/cond/getZpCondFieldsList.do?b_search=search&type=0&templateid=-1";
   zpCondTemplateConstantForm.submit();
}
function newComplex()
{
   zpCondTemplateConstantForm.action="/hire/zp_options/cond/getZpCondFieldsList.do?b_search=search&type=1&templateid=-1";
   zpCondTemplateConstantForm.submit();

}
function deleteComplex()
{
  var id="";
  var obj=document.getElementsByName("tid");
  if(obj!=null)
  {
     for(var i=0;i<obj.length;i++)
     {
        if(obj[i].checked)
        { 
         id+=","+obj[i].value;
        }
     }
  }
  if(trim(id).length==0)
  {
     alert(PLEASE_SELECT_DELETE_TEMPLATE+"!");
     return;
  }
  id=id.substring(1);
  //3960000008
  if(confirm(GZ_ACCOUNTING_ENTERDELETEITEM+"？"))
  {
     zpCondTemplateConstantForm.action="/hire/zp_options/cond/getZpCondFieldsList.do?b_delete=delete&id="+id;
     zpCondTemplateConstantForm.submit();
  }
}
var type="0";
function allselect()
{
     var obj=document.getElementsByName("tid");
     if(obj)
     {
      if(type=='0')
      {
        for(var i=0;i<obj.length;i++)
        {
             obj[i].checked=true;
        }
        type="1";
      }else
      {
          for(var i=0;i<obj.length;i++)
          {
             obj[i].checked=false;
          }
          type="0";
      }
           
              
    }
}
function initClose()
{
   window.close();
}
function clearCodeValue(hz,hzv)
  {
    var o1=document.getElementsByName(hz);
    if(o1)
    {
      if(trim(o1[0].value)=='')
      {
          var o2=document.getElementsByName(hzv);
          if(o2)
            o2[0].value='';
          o1[0].value='';
      }
    }
  }
  function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
</script>
</head>

<body onload="setTPinput()">
<hrms:themes></hrms:themes>
<% int m=0;%>    
<html:form action="/hire/zp_options/cond/zpCondTemplate">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
	<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
   	  <thead>
   	   
   	  <tr height="25" >
      <td align="left"  class="RecordRow" nowrap colspan="3"> 
            <html:radio  property="zp_cond_template_type" value="0" onclick="getFieldType('0')"/><img src="/images/link.gif" border="0"/><bean:message key="label.zp_options.cond_simple"/>
           <html:radio  property="zp_cond_template_type" value="1" onclick="getFieldType('1')"/><img src="/images/link.gif" border="0"/><bean:message key="label.zp_options.cond_complex"/>
               
      </td>
     </tr>  
     <logic:equal name="zpCondTemplateConstantForm" property="zp_cond_template_type" value="0">
     <tr height='25'>
            <td align="center" class="TableRow" nowrap>
            <bean:message key="label.zp_options.cond_fields"/>
            </td>
           
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="label.zp_options.default_value"/>
	   	 	</td>
            <td align="center" class="TableRow" nowrap>
	         <bean:message key="label.zp_options.start_end"/> 
	   		 </td>
         </tr> 
         </thead> 
          
      <logic:iterate id="element" name="zpCondTemplateConstantForm" property="fieldSetList" indexId="index"> 
	   	      <tr height="15%">
	   	      <td align="left" class="RecordRow" nowrap>
	   	      <input type="hidden" name="<%="fieldSetList["+index+"].itemid"%>" value="<bean:write name="element" property="itemid"/>"/>
	   	      <bean:write name="element" property="itemdesc"/>
	   	      </td>
            
            
          <logic:equal name="element" property="itemtype" value="N">
           <td align="center"  class="RecordRow" nowrap>
          
               
               <logic:equal name="element" property="flag" value="true">
              <table border="0" cellspacing="1" class="trShallow" align="center" cellpadding="1">
               <tr>
               <td width="10%">&nbsp;</td>
               <td width="5%" align="center"  style="display:none" id="<%="a"+index%>" ><bean:message key="label.from"/></td>
               <td width="30%" align="center" >
               <input type="text" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" maxlength="<bean:write name="element" property="itemlength"/>">
               </td>
               <td width="5%">&nbsp;</td>
               <td width="5%" style="display:none" align="left"  id="<%="b"+index%>"><bean:message key="label.to"/></td>
               <td width="30%" align="center" >
               <input type="text" style="display:none" name="<%="fieldSetList["+index+"].e_value"%>" value="" maxlength="<bean:write name="element" property="itemlength"/>">
               </td>
               <td width="5%">&nbsp;</td>
               <td width="10%">&nbsp;</td>
               </tr>
               </table>
               </logic:equal>
               
              <logic:equal name="element" property="flag" value="false">
               <table border="0" cellspacing="1" class="trShallow" align="center" cellpadding="1">
               <tr>
               <td width="10%">&nbsp;</td>
               <td width="5%" align="center"  id="<%="a"+index%>">
             	<bean:message key="label.from"/>
              </td>
              <td width="30%" align="center"><input type="text" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" maxlength="<bean:write name="element" property="itemlength"/>">
             </td>
             <td width="5%">&nbsp;</td>
             <td width="5%" align="left" id="<%="b"+index%>">
             <bean:message key="label.to"/></td>
             <td width="30%" align="center"><input type="text" name="<%="fieldSetList["+index+"].e_value"%>" value="<bean:write name="element" property="e_value"/>" maxlength="<bean:write name="element" property="itemlength"/>">
             </td>
             <td width="5%">&nbsp;</td>
             <td width="10%">&nbsp;</td>
              </tr>
              </table>
               </logic:equal>
              </td>
              <logic:equal name="element" property="flag" value="false">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" checked="checked" value="false" onclick="show('<%="fieldSetList["+index+"].e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>')"/>	 
	   		 </td>
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" value="false" onclick="show('<%="fieldSetList["+index+"].e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>')"/>	 
	   		 </td>
	   		 </logic:equal>
          </logic:equal>
            
            
            
         <logic:equal name="element" property="itemtype" value="D">
            <td align="center" class="RecordRow" nowrap>
		      
		      <logic:equal name="element" property="flag" value="true"> 
		      <table border="0" cellspacing="1" align="center" class="trShallow" cellpadding="1">
		      <tr>
		       <td width="10%">&nbsp;</td>
		       <td width="5%" align="center"  style="display:none" id="<%="a"+index%>" ><bean:message key="label.from"/></td> 
		       <td width="30%" align="center" >
		      	<input type="text" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' />
		      </td>
		      <td width="5%">&nbsp;</td>
		      <td width="5%" align="center"  style="display:none" id="<%="b"+index%>"><bean:message key="label.to"/></td>
		      <td width="30%" align="center" >
		      <input type="text" style="display:none" name="<%="fieldSetList["+index+"].e_value"%>" value="" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' />
		      </td>
		      <td width="5%">&nbsp;</td>
		      <td width="10%">&nbsp;</td>
		      </tr></table>
		      </logic:equal>
		      <logic:equal name="element" property="flag" value="false">
		       <table border="0" cellspacing="1" class="trShallow" align="center" cellpadding="1">
		       <tr>
		       <td width="10%">&nbsp;</td>
		       <td width="5%"align="center"  id="<%="a"+index%>"><bean:message key="label.from"/></td>
		       <td width="30%" align="center"><input type="text" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'/>
		       </td>
		       <td width="5%"> &nbsp;</td>
		       <td width="5%" align="center" id="<%="b"+index%>"><bean:message key="label.to"/></td>
		       <td width="30%" align="center">
		        <input type="text" name="<%="fieldSetList["+index+"].e_value"%>" value="<bean:write name="element" property="e_value"/>" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'/>
		       </td>
		       <td width="5%">&nbsp;</td>
		       <td width="10%">&nbsp;</td>
		      </tr>
		      </table>
		      </logic:equal> 
		      </td>
		       <logic:equal name="element" property="flag" value="false">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" checked="checked" value="false" onclick="show('<%="fieldSetList["+index+"].e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>')"/>	 
	   		 </td>
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" value="false" onclick="show('<%="fieldSetList["+index+"].e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>')"/>	 
	   		 </td>
	   		 </logic:equal>
		 </logic:equal>
			 
	<logic:equal name="element" property="itemtype" value="A">
	 <logic:notEqual name="element" property="codesetid" value="0">
	        <td align="center"  class="RecordRow" nowrap>
			    
	 <logic:equal name="element" property="flag" value="true">
	 <table border="0"cellspacing="1" align="center" class="trShallow" cellpadding="1"> 
	 <tr>
	 		   <td width="10%">
		       	<input type="hidden" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" readonly/>
	           </td>
	          <td width="5%" align="center" style="display:none"  id="<%="a"+index%>" ><bean:message key="label.from"/></td> 
	          <td width="30%" align="center" >
			    <input type="text" onchange='clearCodeValue("<%="fieldSetList["+index+"].view_s_value"%>","<%="fieldSetList["+index+"].s_value"%>")' name="<%="fieldSetList["+index+"].view_s_value"%>" value="<bean:write name="element" property="view_s_value"/>"/>
			    </td>
			    <td width="5%" align="center">
			    <span>
			    <img  src="/images/code.gif" style="position:relative;top:3px;"onclick='javascript:openInputCodeDialogS_value("<bean:write  name="element" property="codesetid"/>","<%="fieldSetList["+index+"].view_s_value"%>");'/>
			    </span>
			    </td>
			   <td width="5%" style="display:none" align="center"  id="<%="b"+index%>" ><bean:message key="label.to"/></td>
			   <td width="30%" align="center"> 
			    <input type="text" style="display:none" onchange='clearCodeValue("<%="fieldSetList["+index+"].view_e_value"%>","<%="fieldSetList["+index+"].e_value"%>")'name="<%="fieldSetList["+index+"].view_e_value"%>" value=""/>
			    </td>
			    <td width="5%" align="center">
			    <img style="display:none" name="<%="fieldSetList["+index+"].view_e_value"%>" src="/images/code.gif" onclick='javascript:openInputCodeDialogE_value("<bean:write  name="element" property="codesetid"/>","<%="fieldSetList["+index+"].view_e_value"%>");'/>
			   </td>
			   <td width="10%"> <input type="hidden" name="<%="fieldSetList["+index+"].e_value"%>" value="" readonly/></td>
			    </tr>
			    </table>
</logic:equal>
 <logic:equal name="element" property="flag" value="false">
  <table border="0" cellspacing="1" class="trShallow" align="center" cellpadding="1">
  <tr>
   <td width="10%"><input type="hidden" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" readonly/>
			   </td>
  <td align="center" width="5%" id="<%="a"+index%>">
               <bean:message key="label.from"/></td>
               <td align="center" width="30%">
               <input type="text" onchange='clearCodeValue("<%="fieldSetList["+index+"].view_s_value"%>","<%="fieldSetList["+index+"].s_value"%>")' name="<%="fieldSetList["+index+"].view_s_value"%>" value="<bean:write name="element" property="view_s_value"/>"/>
			    </td>
			    <td width="5%" align="center">
			    <span>
			    <img  src="/images/code.gif" style="position:relative;top:3px;" onclick='javascript:openInputCodeDialogS_value("<bean:write  name="element" property="codesetid"/>","<%="fieldSetList["+index+"].view_s_value"%>");'/>
			    </span>
			   </td>
			   
			  <td width="5%" align="center" id="<%="b"+index%>">
			 <bean:message key="label.to"/></td>
			   <td width="30%" align="center">
			   <input type="text" onchange='clearCodeValue("<%="fieldSetList["+index+"].view_e_value"%>","<%="fieldSetList["+index+"].e_value"%>")' name="<%="fieldSetList["+index+"].view_e_value"%>" value="<bean:write name="element" property="view_e_value"/>"/>
			   </td><td width="5%" align="center"><span>
			    <img  src="/images/code.gif" style="position:relative;top:3px;"name="<%="fieldSetList["+index+"].view_e_value"%>" onclick='javascript:openInputCodeDialogE_value("<bean:write  name="element" property="codesetid"/>","<%="fieldSetList["+index+"].view_e_value"%>");'/>
			    </span>
                 </td><td width="10%"><input type="hidden" name="<%="fieldSetList["+index+"].e_value"%>" value="<bean:write name="element" property="e_value"/>" readonly/></td>
                 </tr>
                 </table>
   </logic:equal>
			     </td>
			     <logic:equal name="element" property="flag" value="false">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" checked="checked" value="false" onclick="show('<%="fieldSetList["+index+"].view_e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>');"/>	 
	   		 </td>
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" value="false" onclick="show('<%="fieldSetList["+index+"].view_e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>');"/>	 
	   		 </td>
	   		 </logic:equal>
	 </logic:notEqual>
	 
	<logic:equal name="element" property="codesetid" value="0">
	            <td align="center"  class="RecordRow" nowrap>
			    
			    <logic:equal name="element" property="flag" value="true">
			    <table border="0" class="trShallow" cellspacing="1" align="center" cellpadding="1">
			    <tr><td width="10%">&nbsp;</td>
			    <td align="center" width="5%" style="display:none" id="<%="a"+index%>" ><bean:message key="label.from"/></td> 
			    <td width="30%" align="center">
			    <input type="text" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" maxlength="<bean:write name="element" property="itemlength"/>"/>
			    </td>
			    <td width="5%">&nbsp;</td>
			    <td width="5%" style="display:none"  id="<%="b"+index%>" align="center"><bean:message key="label.to"/></td>
			    <td width="30%" align="center"> 
			    <input type="text" style="display:none" name="<%="fieldSetList["+index+"].e_value"%>" value="" maxlength="<bean:write name="element" property="itemlength"/>"/>
			   </td><td width="5%">&nbsp;</td><td width="10%">&nbsp;</td>
			   </tr></table>
			    </logic:equal>
			    <logic:equal name="element" property="flag" value="false">
			     <table border="0" cellspacing="1" class="trShallow" align="center" cellpadding="1">
			     <tr><td width="10%">&nbsp;</td>
			     <td align="center" width="5%" id="<%="a"+index%>">
			   <bean:message key="label.from"/></td><td width="30%" align="center"><input type="text" name="<%="fieldSetList["+index+"].s_value"%>" value="<bean:write name="element" property="s_value"/>" maxlength="<bean:write name="element" property="itemlength"/>"/>
			   </td><td width="5%">&nbsp;</td><td width="5%" align="center" id="<%="b"+index%>"><bean:message key="label.to"/></td><td width="30%"><input type="text" name="<%="fieldSetList["+index+"].e_value"%>" value="<bean:write name="element" property="e_value"/>" maxlength="<bean:write name="element" property="itemlength"/>"/></td>
			    <td width="5%">&nbsp;</td><td width="10%">&nbsp;</td>
			    </tr>
			   </table>
			    </logic:equal>
			    </td>
			   <logic:equal name="element" property="flag" value="false">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" checked="checked" value="false" onclick="show('<%="fieldSetList["+index+"].e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>')"/>	 
	   		 </td>
	   		 </logic:equal>
	   		 <logic:equal name="element" property="flag" value="true">
              <td align="center" class="RecordRow" nowrap>
	         <input type="checkbox" name="<%="fieldSetList["+index+"].flag"%>" value="false" onclick="show('<%="fieldSetList["+index+"].e_value"%>','<%=index%>','<%="fieldSetList["+index+"].flag"%>')"/>	 
	   		 </td>
	   		 </logic:equal>
	 </logic:equal>
     </logic:equal>
	         
         </tr>
         <% m++;%>
 </logic:iterate>
</logic:equal>
<logic:equal value="1" name="zpCondTemplateConstantForm" property="zp_cond_template_type">
    <tr height='25'>
            <td width="10%" align="center" class="TableRow" nowrap>
            <input type="checkbox" name="select" onclick="allselect();"/>
            </td>
           
            <td width="80%" align="center" class="TableRow" nowrap>
	         <bean:message key="general.template.nodedefine.templatename"/>
	   	 	</td>
            <td width="10%" align="center" class="TableRow" nowrap>
	         <bean:message key="label.edit"/> 
	   		 </td>
         </tr> 
         </thead> 
         <% int t=0; %>
         <logic:iterate id="data" name="zpCondTemplateConstantForm" property="complexTemplateList" indexId="index">
         <tr>
           <td align="center" class="RecordRow">
           <input type="checkbox" name="tid" value="<bean:write name="data" property="id"/>"/>
           </td>
           <td align="left" class="RecordRow">
           <bean:write name="data" property="name"/>
           </td>
           <td align="center" class="RecordRow">
           <a href="/hire/zp_options/cond/getZpCondFieldsList.do?b_search=search&type=1&templateid=<bean:write name="data" property="id"/>"><img src="/images/edit.gif" border="0"/></a>
           </td>
           </tr>
         </logic:iterate>
         <tr>
            <td align="center" valign="bottom" colspan="3" style="padding-top:5px;"> 
              <button class="mybutton" value="" onclick="newComplex();"><bean:message key="lable.tz_template.new"/></button>
              <button class="mybutton" value="" onclick="deleteComplex();" style="margin-left:5px;"><bean:message key="lable.tz_template.delete"/></button>
              
         	</td>
         
</logic:equal>
</table> 
<logic:equal value="0" name="zpCondTemplateConstantForm" property="zp_cond_template_type">
    <table  width="85%" align="center">
          <tr>
            <td valign="bottom" align='center' style="padding-top:3px;"> 
              <button class="mybutton" value="" onclick="modifySimple();"><bean:message key="hire.select.queryfield"/></button>
              <button class="mybutton" value="" onclick="sub('<%=m%>');"  style="margin-left:5px;"><bean:message key="button.save"/></button>
         	</td>
         </tr>
    </table>
</logic:equal>
   	  </html:form>
    
</body>
<script type="text/javascript">
initClose();
</script>
</html>