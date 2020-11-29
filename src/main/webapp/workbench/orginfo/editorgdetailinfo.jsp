<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/js/dict.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script language="javascript"> 
  function exeButtonAction(actionStr,target_str)
   {       
      // target_url=actionStr;
       //window.open(target_url,target_str); 
       //alert("dd");
       orgInfoForm.action=actionStr;
       //orgInfoForm.target=target_str;
       orgInfoForm.submit();
   }
</script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="orgInfoForm"  property="infofieldlist" indexId="index"> 
     <bean:define id="fl" name="element" property="fillable"/>
        <bean:define id="desc" name="element" property="itemdesc"/>
        var valueInputs=document.getElementsByName("<%="infofieldlist["+index+"].value"%>");
        var dobj=valueInputs[0];       
           if("${fl}"=='true'&&dobj.value.length<1){
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

</script>
<%
	int i=0;
	int flag=0;
%>
<hrms:themes/>
<html:form action="/workbench/orginfo/editorgdetailinfodata" onsubmit="return validate()">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
<logic:iterate  id="element"    name="orgInfoForm"  property="infofieldlist" indexId="index"> 
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
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>         
         </td>
         <td align="left"  nowrap valign="middle"  class="RecordCellright">
             <html:text   name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorWrite" maxlength="${element.itemlength}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
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
         <td align="right" nowrap valign="middle"  class="RecordCellleft">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
         </td>
         <td align="left"  nowrap valign="middle"  class="RecordCellright">
            <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
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
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>         
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
               <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + (element.decimalwidth>0?element.decimalwidth+1:0)}" />&nbsp;&nbsp;&nbsp;&nbsp;   
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
                 <td align="right" nowrap valign="middle" class="RecordCellleft">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
                 </td>
                 <td align="left"  nowrap valign="middle" class="RecordCellright"  colspan="3">
                  <html:textarea name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%"/>
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
                <td align="right" nowrap valign="middle" class="RecordCellleft">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
                </td>
                <td align="left"  nowrap valign="middle" class="RecordCellright" colspan="3">
                  <html:textarea name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
                 <html:hidden name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
                    <html:text name="orgInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' readonly="true"  styleClass="textColorRead" /> &nbsp;&nbsp;&nbsp;&nbsp; 
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
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
             <input type="text" name='<%="infofieldlist["+index+"].value"%>' class="textColorWrite"  value="<bean:write name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>" style='width:200px' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>&nbsp;&nbsp;&nbsp;&nbsp;  
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
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
            <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
               <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength + (element.decimalwidth>0?element.decimalwidth+1:0)}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
                 <td align="right" nowrap valign="middle" class="RecordCellleft">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
                 </td>
                 <td align="left"  nowrap valign="middle" class="RecordCellright"  colspan="3">
                  <html:textarea name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
                <td align="right" nowrap valign="middle" class="RecordCellleft">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>         
                </td>
                <td align="left"  nowrap valign="middle" class="RecordCellright" colspan="3">
                  <html:textarea name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
           </td>
           <td align="left"  nowrap valign="middle" class="RecordCellright">
                 <html:hidden name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
                 	<!-- 去掉键盘事件 提供模糊查询功能       wangb 20171127             onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" -->
                    <html:text name="orgInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" onclick="styleDisplay(this);"  styleClass="textColorWrite" />  
                    <!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117  -->
                 <img src="/images/code.gif" align="absmiddle" id="infofieldlist<%=index %>" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infofieldlist["+index+"].viewvalue"%>'  ctrltype="3" nmodule="4" valuename="<%="infofieldlist["+index+"].value"%>"/>
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
 <tr><td height="10"></td></tr>
 
 <tr>
  <td align="center"  nowrap colspan="4">
    &nbsp;&nbsp;
            <logic:equal name="orgInfoForm" property="setprv" value="2">
               <hrms:submit styleClass="mybutton"  property="b_save">
                    <bean:message key="button.ok"/>
	       </hrms:submit> 
	     </logic:equal>
	     <logic:equal name="orgInfoForm" property="setprv" value="3">
               <hrms:submit styleClass="mybutton"  property="b_save">
                    <bean:message key="button.ok"/>
	       </hrms:submit> 
	     </logic:equal>
	        <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/orginfo/editorgdetailinfodata.do?b_return=link','mil_body')">
	       <!-- <hrms:submit styleClass="mybutton"  property="b_return">
                    <bean:message key="button.return"/>
	       </hrms:submit>     -->  
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
Element.hide('dict');
</script>  
