<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %> 
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
 <link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/js/dict.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>  	
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag="";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(userView != null){
	   css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  	 
	manager=userView.getUnitIdByBusi("4");
	}

%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="dutyInfoForm"  property="infofieldlist" indexId="index"> 
        <bean:define id="fl" name="element" property="fillable"/>
        <bean:define id="desc" name="element" property="itemdesc"/>
        <bean:define id="itemid" name="element" property="itemid"/>
        var valueInputs=document.getElementsByName("<%="infofieldlist["+index+"].value"%>");
        var dobj=valueInputs[0];
        if("${itemid}"=="codeitemid"){
        	var v=dobj.value;
        	var reg=new RegExp("^[A-Z0-9]+$");
        	for(var i=0;i<v.length;i++){
				var c=v.substr(i,1);
				if(!reg.test(c)){
					alert("${desc}"+"只能为大写字母或数字!");
					dobj.value="";
					dobj.focus();
					return false;
				}
			}
        }           
        	if("${fl}"=='true'&&dobj.value.trim().length<1){
              	alert("${desc}"+'必须填写！');

              	return false;
             }
             if("${itemid}"=="codeitemdesc"){
             	var v=dobj.value;
             	if(v.indexOf("\"")>-1||v.indexOf("'")>-1||v.indexOf("\'")>-1||v.indexOf("\"")>-1||v.indexOf(" ")>-1){
             		alert("${desc}"+'不能包括空格或\'或\"或\‘或\“');
             		return false;
             	}
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
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infofieldlist["+index+"].value"%>');
             var dobj=valueInputs[0];
               tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;   
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
  function check()
  {
    var code="${dutyInfoForm.code}";
    var kind="${dutyInfoForm.code}";
    if(code==""||kind=="")
    {
       alert("请选定所要操作的职位信息!");
       return false; 
    }else
    {
       return true; 
    }
  }
  function open(url)
  {
    var temp = url.indexOf("a0100=");
    var str = url.substring(temp+6,url.lenght);
    temp = str.indexOf("&");
    str = str.substring(0,temp);
    if(str!="")
    {
	    var thecodeurl =url; 
	 	var return_vo= window.showModalDialog(thecodeurl, "", 
		    "dialogWidth:620px; dialogHeight:400px;resizable:yes;center:yes;scroll:yes;status:no");
		while(return_vo!=null)
		{
			var thecodeurl =url; 
	 		var return_vo= window.showModalDialog(thecodeurl, "", 
		    "dialogWidth:620px; dialogHeight:400px;resizable:yes;center:yes;scroll:yes;status:no");
		}
    }else
  	{
  		alert('请选择职位!');
  	}
	
  }
  function exeReturn(returnStr,target)
{
  //target_url=returnStr;
 // window.open(target_url,target); 
   dutyInfoForm.action=returnStr;
   dutyInfoForm.target=target;
   dutyInfoForm.submit();
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
	int j=0;
	int n=0;
%>
<hrms:themes></hrms:themes>
<html:form action="/workbench/dutyinfo/editorginfodata" onsubmit="return validate()">

<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1"  style="margin-top:5px;">
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
         <td align="right"  nowrap valign="middle" class="RecordCellleft">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
         </td>
         <td align="left"  nowrap valign="middle" class="RecordCellright">
             <html:text   name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
            <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
               <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth +1}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%" />&nbsp;&nbsp;&nbsp;&nbsp; 
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
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
                 <html:hidden name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' />  
                    <html:text name="dutyInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' readonly="true"  styleClass="textColorRead" /> &nbsp;&nbsp;&nbsp;&nbsp; 
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
             &nbsp;<input type="text" name='<%="infofieldlist["+index+"].value"%>' value="<bean:write name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>" style='BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:150' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
            <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
               <html:text  name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth +1}" /> &nbsp;&nbsp;&nbsp;&nbsp; 
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
                 <td align="left"  nowrap valign="middle"  colspan="3" class="RecordCellright">
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
                  <html:textarea name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
           <logic:notEqual value="${dutyInfoForm.ps_superior }" name="element" property="itemid">
              <html:hidden name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
              <html:text name="dutyInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>'  styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>  
              <img  src="/images/code.gif" align=absmiddle onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>");' />&nbsp;&nbsp;&nbsp;&nbsp;
                <logic:equal name="element"  property="fillable" value="true">
                    <font color="red">*</font>
                    </logic:equal>
           </logic:notEqual>
           <logic:equal value="${dutyInfoForm.ps_superior }" name="element" property="itemid">
              <html:hidden name="dutyInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
              <html:text name="dutyInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>'  styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/>  
              <img  src="/images/code.gif" align=absmiddle onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infofieldlist["+index+"].viewvalue"%>","<%=manager%>","2");' />&nbsp;&nbsp;&nbsp;&nbsp;
                <logic:equal name="element"  property="fillable" value="true">
                    <font color="red">*</font>
                    </logic:equal>
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

 <%if(flag==1)
 {
 %>
    <td colspan="2">
    </td>
 </tr>
 <% } %>
 
 </table>
 <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" >
 <tr>
  <td  width="100%" align="center"  nowrap colspan="4">
    &nbsp;&nbsp;<html:hidden name="dutyInfoForm" property="edittype"/> 
        <html:hidden name="dutyInfoForm" property="edit_flag"/> 
        <html:hidden name="dutyInfoForm" property="grade"/> 
        <html:hidden name="dutyInfoForm" property="first"/> 
        <html:hidden name="dutyInfoForm" property="code"/>       
        <logic:equal name="dutyInfoForm" property="setprv" value="2">
          <logic:equal name="dutyInfoForm" property="edit_flag" value="new">
           <hrms:submit styleClass="mybutton"  property="b_save" onclick='return check();'>
                    <bean:message key="button.save"/>
	       </hrms:submit>   
	       </logic:equal>
	       <logic:notEqual name="dutyInfoForm" property="edit_flag" value="new">
	           <hrms:submit styleClass="mybutton"  property="b_edit" onclick='return check();'>
                    <bean:message key="button.save"/>
	          </hrms:submit> 
	      </logic:notEqual>
	   </logic:equal> 
	    <logic:equal name="dutyInfoForm" property="returnvalue" value="scanduty">
	     <logic:equal name="dutyInfoForm" property="edit_flag" value="new">
	        <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${dutyInfoForm.code}','nil_body')">
	     </logic:equal>
	     <logic:notEqual name="dutyInfoForm" property="edit_flag" value="new">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${dutyInfoForm.return_codeid}','nil_body')">                 
          </logic:notEqual>
         </logic:equal>
         <logic:equal name="dutyInfoForm" property="returnvalue" value="orgpre">
         	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/org/orgpre/postable.do?b_query=link&b0110=${dutyInfoForm.b0110 }&setid=${dutyInfoForm.setid}&a_code=${dutyInfoForm.a_code}&infor=${dutyInfoForm.infor}&unit_type=${dutyInfoForm.unit_type}&nextlevel=${dutyInfoForm.nextlevel}','mil_body')">                 
         </logic:equal>
         <logic:equal name="dutyInfoForm" property="returnvalue" value="75"><!--预警-->
         	<logic:equal value="bi" name="userView" property="bosflag">
         		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','i_body')">                 
         	</logic:equal>
         	<logic:notEqual value="bi" name="userView" property="bosflag">
         		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
         	</logic:notEqual>
         </logic:equal>
  </td>
 </tr>
   <tr>
   <td height="10"></td>
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
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
  
</script>
<script language="javascript">
var code_desc; 
function addDict(code,obj)
{
  Element.hide('dict');
  var value=obj.value;  
  if(value=="")
   return false;
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