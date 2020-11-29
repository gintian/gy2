<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<script type="text/javascript" src="/js/dict.js"></script>
<script language="javascript">
  function exeButtonAction(actionStr,target_str)
   {
     // alert(actionStr);
      // target_url=actionStr;
      // window.open(target_url,target_str); 
      dutyInfoForm.action=actionStr;
      dutyInfoForm.submit();
   }
</script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="dutyInfoForm"  property="infofieldlist" indexId="index"> 
        <bean:define id="fl" name="element" property="fillable"/>
        <bean:define id="desc" name="element" property="itemdesc"/>
        var valueInputs=document.getElementsByName("<%="infofieldlist["+index+"].value"%>");
        var dobj=valueInputs[0];       
           if("${fl}"=='true'&&dobj.value.trim().length<1){
          	alert("${desc}"+'必须填写！');
          	return false;
         }
        <logic:equal name="element" property="itemtype" value="D">   
          var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
          var dobj=valueInputs[0];
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj,"${desc}处") &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
               tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth},"${desc}处") &&  tag ;
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
      </logic:iterate>    
     return tag;   
  }
//ie兼容trim方法
if(!String.prototype.trim) {
    String.prototype.trim = function () {
        return this.replace(/^\s+|\s+$/g,'');
    };
}
</script>

<%
	int i=0;
	int flag=0;
%>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<hrms:themes/>
<html:form action="/workbench/dutyinfo/editorgdetailinfodata" onsubmit="return validate()">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<logic:iterate  id="element"    name="dutyInfoForm"  property="infofieldlist" indexId="index"> 
    <logic:equal name="element" property="priv_status" value="1"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
        <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="middle" class="RecordCellleft">        
            <bean:write  name="element" property="itemdesc"/>
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
             <html:text   name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" />   
          <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="middle" class="RecordCellleft">        
            <bean:write  name="element" property="itemdesc"/>
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
            <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" />   
          <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="middle" class="RecordCellleft">        
              <bean:write  name="element" property="itemdesc"/>
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
               <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + (element.decimalwidth>0?element.decimalwidth+1:0)}" />   
             <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;%>
                 <td align="right" style="width: 15%" nowrap valign="middle" class="RecordCellleft">
                   <bean:write  name="element" property="itemdesc"/>
                 </td>
                 <td align="left" style="width: 85%"  nowrap valign="middle"  colspan="3" class="RecordCellright">
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%" /> 
                  <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>               
                <td align="right" style="width: 15%" nowrap valign="middle" class="RecordCellleft">
                   <bean:write  name="element" property="itemdesc"/>
                </td>
                <td align="left" style="width: 85%"   nowrap valign="middle" colspan="3" class="RecordCellright">
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%" />
                <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="middle" class="RecordCellleft">        
              <bean:write  name="element" property="itemdesc"/>          
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
                 <html:hidden name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
                    <html:text name="dutyInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' readonly="true"  styleClass="textColorRead" />  
           <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
           </td>
         <%if(flag==0){%>
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:notEqual>
               
   </logic:equal>  
   
   
   
   
   
     
   <logic:equal name="element" property="priv_status" value="2"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
        <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="middle" class="RecordCellleft">        
            <bean:write  name="element" property="itemdesc"/>
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
             <input type="text" name='<%="infofieldlist["+index+"].value"%>' value="<bean:write name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>" class="textColorWrite" extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/> 
         <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="middle" class="RecordCellleft">        
            <bean:write  name="element" property="itemdesc"/>
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
            <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" />   
          <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="middle" class="RecordCellleft">        
              <bean:write  name="element" property="itemdesc"/>
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
               <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength + (element.decimalwidth>0?element.decimalwidth+1:0)}" />   
            <logic:equal name="element"  property="fillable" value="true">
           <font color="red">*</font>
           </logic:equal>
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;%>
                 <td style="width: 15%" align="right" nowrap valign="middle" class="RecordCellleft">
                   <bean:write  name="element" property="itemdesc"/>
                 </td>
                 <td align="left" style="width: 85%"  nowrap valign="middle"   class="RecordCellright">
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/> 
                <logic:equal name="element"  property="fillable" value="true">
                   <font color="red">*</font>
                </logic:equal>
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>               
                <td align="right" style="width: 15%" nowrap valign="middle" class="RecordCellleft">
                   <bean:write  name="element" property="itemdesc"/>
                </td>
                <td align="left" style="width: 85%;"  nowrap valign="middle" colspan="3" class="RecordCellright">
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/> 
                 <logic:equal name="element"  property="fillable" value="true">
                   <font color="red">*</font>
                 </logic:equal>
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="middle" class="RecordCellleft">        
              <bean:write  name="element" property="itemdesc"/>
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
                 <html:hidden name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
                   <html:text name="dutyInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"  onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"  styleClass="textColorWrite" />
                  <img align="absmiddle" src="/images/code.gif"    onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>");'/>  
               <logic:equal name="element"  property="fillable" value="true">
              <font color="red">*</font>
               </logic:equal>
            </td>
         <%if(flag==0){%>
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:notEqual>
               
   </logic:equal>  
</logic:iterate> 
 <tr>
  <td align="center"  nowrap colspan="4">
    &nbsp;&nbsp;
        <logic:equal name="dutyInfoForm" property="setprv" value="2">
               <hrms:submit styleClass="mybutton"  property="b_save" onclick='return checkLenth();'>
                    <bean:message key="button.ok"/>
	       </hrms:submit> 
	    </logic:equal>
	    <logic:equal name="dutyInfoForm" property="setprv" value="3">
               <hrms:submit styleClass="mybutton"  property="b_save" onclick='return checkLenth();'>
                    <bean:message key="button.ok"/>
	       </hrms:submit> 
	    </logic:equal>
	       <input type="button" name="returnbutton"  value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/dutyinfo/editorgdetailinfodata.do?b_return=link','mil_body')">
	       <!-- <hrms:submit styleClass="mybutton"  property="b_return">
                    <bean:message key="button.return"/>
	       </hrms:submit>  -->     
  </td>
 </tr>    
 </table>
</html:form>
<div id=dict style="border-style:nono">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)"  onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>
<script language="javascript">
var code_desc; 
function addDict(code,obj)
{
  Element.hide('dict');
  var value=obj.value;  
  if(value=="")
   return false;
  Element.show('dict');  
  var dmobj;
  var vos= document.getElementsByName('dict_box');
  var dict_vo=vos[0];
  var isC=true;
  code_desc=obj;
  for(var i=dict_vo.options.length-1;i>=0;i--)
  {
      dict_vo.options.remove(i);
  }   
    var no = new Option();
   no.value="";
   no.text="";
   dict_vo.options[0]=no;
   var r=1;      
   for(var i=0;i<g_dm.length;i++)
   {
		dmobj=g_dm[i];		 
		if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		{
		    var no = new Option();
    	    no.value=dmobj.ID;
    	    no.text=dmobj.V;
		    dict_vo.options[r]=no;
			r++;
		}
   } 
   if(r==1)
   {
      obj.value="";
      Element.hide('dict'); 
      return false;
   }
   Element.show('dict');  
   var pos=getAbsPosition(obj);
   with($('dict'))
   {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
 		    style.posTop=pos[1]-1+obj.offsetHeight;
		    style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
   }  
}
 function setSelectCodeValue()
   {
     if(code_desc)
     {
        var vos= document.getElementsByName('dict_box');
        var dict_vo=vos[0];
        var isC=true;
        for(var i=0;i<dict_vo.options.length;i++)
        {
          if(dict_vo.options[i].selected)
          {
            code_desc.value=dict_vo[i].text;
            var code_name=code_desc.name;
            if(code_name!="")
            {
               var code_viewname=code_name.substring(0,code_name.indexOf("."));
               var view_vos= document.getElementsByName(code_viewname+".value");
               var view_vo=view_vos[0];    
               if(dict_vo[i].value!=null)          
                 view_vo.value=dict_vo[i].value.substring(2);
             }
          }
        }        
        Element.hide('dict');   
        event.srcElement.releaseCapture(); 
     }
  }
   function inputType(obj,event)
  {
     var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
     if(keyCode==13)
     {
       setSelectCodeValue();
     }     
  }
  function inputType2(obj,event)
  {
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if(keyCode == 40)
    {
       var vos= document.getElementsByName('dict');
       var vos1=vos[0];       
       if(vos1.style.display!="none")
       {
          var vos= document.getElementsByName('dict_box');
          var dict_vo=vos[0];          
          dict_vo.focus(); 
       }
    }
  }
  function styleDisplay(obj)
  {
     var obj_name=obj.name;
     if(code_desc)
     {
        var code_name=code_desc.name;
        if(code_name!=obj_name)
        {
          Element.hide('dict');
        }
     }
  }
  function checkDict(code,obj)
  {
    if(!code_desc)
      return false;
    var code_name=code_desc.name;
    var code_viewname=code_name.substring(0,code_name.indexOf("."));
    var view_vos= document.getElementsByName(code_viewname+".value");
    var view_vo=view_vos[0];  
    if(view_vo==null||view_vo=="")
    {
      obj.value="";
      return false;
    }
    var isC=false;
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];		 
		if(dmobj.ID==(code+view_vo))
		{
		    isC=true;
		    break;
		}
   } 
   if(!isC)
   {
      obj.value="";
      return false;
   } 
}
//校验每个字符型指标是否超过长度
function checkLenth(){
    <logic:iterate  id="element" name="dutyInfoForm"  property="infofieldlist" indexId="index">
    <logic:equal name="element" property="itemtype" value="A">
    <logic:equal name="element" property="codesetid" value="0">
        if(IsOverStrLength(document.getElementsByName("<%="infofieldlist["+index+"].value"%>")[0].value,${element.itemlength})){
            alert('${element.itemdesc}长度不能超过${element.itemlength}个字符或${Integer.valueOf(element.itemlength/2)}个汉字');
            return false;
        }
    </logic:equal>
    </logic:equal>
    </logic:iterate>
}
Element.hide('dict');
</script>  