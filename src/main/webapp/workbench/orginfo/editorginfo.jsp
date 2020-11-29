<%@page import="java.util.Date"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hjsj.hrms.actionform.org.OrgInfoForm"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<script type="text/javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript" src="/components/ckEditor/CKEditor.js"></script>
<style>
    .x-grid-td {
        overflow: hidden;
        border-width: 0;
        vertical-align: middle;
        height:23px;
    }

    .x-tree-elbow-img {
        width: 16px;
        height: 23px;
        line-height: 23px;
        margin-right: 0
    }

    .x-tree-icon {
        width: 16px;
        height: 23px;
        line-height: 23px;
        color: gray;
        font-size: 16px
    }

    /*treepanel 图片*/
    .x-tree-elbow-img {
        width: 16px;
        height: 23px;
        line-height: 23px;
        margin-right: 0
    }
    .x-grid-cell-inner-treecolumn {
        padding: 0 6px 0 0
    }
</style>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	//var ViewProperties=new ParameterSet();
</script>  	
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
var textAreaId = "";
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="orgInfoForm"  property="infofieldlist" indexId="index"> 
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
		  if(tag==false) {
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
  function open(url)
  {
  	var kind = url.indexOf("kind=");
    var kindstr = url.substring(kind+5,url.lenght);
    kind = kindstr.indexOf("&");
    kindstr = kindstr.substring(0,kind);
    if(kindstr=="")
    {
    	alert('请选择单位!');
    	return;
    }
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
  		alert('请选择单位!');
  	}
  	
  }
  function save()
  {
   var type = "${orgInfoForm.type}";
   var setname = "${orgInfoForm.setname}"
   if(type =="1") {
	   try{
		   var htmlEditor = Ext.getCmp("htmlEditor");
		   var oldInputs = document.getElementById("content");
		   oldInputs.value = htmlEditor.getHtml(); 
       }catch(e){
       }   
   }
    <logic:iterate  id="element"    name="orgInfoForm"  property="infofieldlist" indexId="index"> 
       <bean:define id="fl" name="element" property="fillable"/>
       <bean:define id="desc" name="element" property="itemdesc"/>
       <bean:define id="itemid" name="element" property="itemid"/>
        <logic:equal name="element" property="itemtype" value="M">
        	
          <logic:equal name="element" property="inputtype" value="1">
          try{
        	  var htmlEditor = Ext.getCmp("htmlEditor");
        	  oldInputs = document.getElementById("content_${itemid}");
              oldInputs.value = htmlEditor.getHtml(); 
            }catch(e){
            }
          </logic:equal>  
        </logic:equal>
    </logic:iterate>

    if(!validate())
    {
      return false;
    }
    
   orgInfoForm.action="/workbench/orginfo/editorginfodata.do?b_save=save&xuj=xuj&setname=" + setname;
   orgInfoForm.submit();
   }
  
   function hiddenContent(id,button) {
	    var htmlEditorPanel = Ext.getCmp("htmlEditorPanel"); 
	    if(htmlEditorPanel.isHidden()) {
	        var textArea = document.getElementById(id);
	        var htmlEditor = Ext.getCmp("htmlEditor");
	        htmlEditor.setValue(textArea.value);
	        htmlEditorPanel.show();
	        var vo = document.getElementById(button);
	        var htmlstr="<table  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\">";
            htmlstr=htmlstr+"<tr><td width=\"20\">";
            htmlstr=htmlstr+"<img src=\"/images/tree_collapse.gif\" border=\"0\" alt=\"编辑\" onclick='hiddenContent(\""+id+"\",\""+button+"\");'>";
            htmlstr=htmlstr+"</td><td>";
            htmlstr=htmlstr+"<a href=\"###\" onclick='hiddenContent(\""+id+"\",\""+button+"\");'>隐藏</a>";
            htmlstr=htmlstr+"</td></tr>";
            htmlstr=htmlstr+" </table>"
	        vo.innerHTML=htmlstr;
	   } else{
		    htmlEditorPanel.hide();
	        var vo = document.getElementById(button);
	        var htmlstr="<table  border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\">";
            htmlstr=htmlstr+"<tr><td width=\"20\">";
            htmlstr=htmlstr+"<img src=\"/images/tree_expand.gif\" border=\"0\" alt=\"编辑\" onclick='hiddenContent(\""+id+"\",\""+button+"\");'>";
            htmlstr=htmlstr+"</td><td>";
            htmlstr=htmlstr+"<a href=\"###\" onclick='hiddenContent(\""+id+"\",\""+button+"\");'>显示</a>";
            htmlstr=htmlstr+"</td></tr>";
            htmlstr=htmlstr+" </table>"
	        vo.innerHTML=htmlstr;
	   }
   }

function exeReturn(returnStr,target) {
   var obj=parent.parent.frames['nil_menu'];
   if(!obj)
	   obj=parent.parent.frames['mil_menu'];
   
   var currnode=obj.Global.selectedItem;
   var code=currnode.uid;
   if(code!='root')
	   returnStr=returnStr+code.substring(2);
   	
   parent.window.location.target=target;
   parent.window.location.href = returnStr;
}
function exeButtonReturn(actionStr,target_str) {
	orgInfoForm.action=actionStr;
   	orgInfoForm.target=target_str;
  	orgInfoForm.submit();
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
<hrms:themes/>
<html:form action="/workbench/orginfo/editorginfodata" onsubmit="return validate()">

<script>
function gotunext(){
	var request=new Request({asynchronous:false,onSuccess:getContext,functionId:'9030000001'});

}
function getContext(){
	alert('ok');
}

function OrgDataIsChange(){
	 var flag=false;
	 <logic:iterate  id="element"    name="orgInfoForm"  property="infofieldlist" indexId="index"> 
	   var oldValue='${orgInfoForm.infofieldlist[index].value}';
	   var obj=document.getElementsByName("<%="infofieldlist["+index+"].value"%>")[0];
	   if(obj.value!=null&&obj.value!==oldValue){
		  flag=true;
	   }
	 </logic:iterate>
	 return flag;
}

</script>
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
         <td align="left"  nowrap valign="middle" class="RecordCellright">
             <html:text   name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
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
            <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
               <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth + 1}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
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
                   <logic:equal name="element" property="inputtype" value="1">
                          <div>
                        <table>
                          <tr>
                            <td id="${element.itemid}_button">
                            <script type="text/javascript">
                            	textAreaId = 'content_${element.itemid}';
                            </script>
                              <table  border="0" cellspacing="0"  align="center" cellpadding="0">
                              <tr><td width="20">
                               <img src="/images/tree_expand.gif" border="0" alt="编辑" onclick="hiddenContent('content_${element.itemid}','${element.itemid}_button');">
                              </td><td>
                               <a href="###" onclick="hiddenContent('content_${element.itemid}','${element.itemid}_button');">显示</a>
                              </td></tr>
                              </table>
                              </td>
                             </tr>
                        </table>
                        
                      </div>
                      </td></tr>
                      <%if(i%2==0){%>
              		<tr class="trShallow1">            
             		<%}else{%>
               <tr class="trDeep1">  
             <%}i++;%><td align="center" nowrap colspan="4" class="RecordCellright">
                          <html:textarea name="orgInfoForm" styleId="content_${element.itemid}" property='<%="infofieldlist["+index+"].value"%>'  rows="3"  cols="60" style="display:none"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                          <div id='htmlEditorDiv' style="margin-top: -33px;"></div>
                     </logic:equal>
                    <logic:notEqual name="element" property="inputtype" value="1">
                  <html:textarea styleClass="complex_border_color" name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                    </logic:notEqual>
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
                <td align="left"  nowrap valign="middle" colspan="3" class="RecordCellright">
                  <html:textarea styleClass="complex_border_color" name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' readonly="true"  rows="5"  cols="60" />&nbsp;&nbsp;&nbsp;&nbsp; 
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
            <logic:notEqual value="###" name="element" property="codesetid">
           <td align="left"  nowrap valign="middle" class="RecordCellright">
                 <html:hidden name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
                    <html:text name="orgInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' readonly="true" onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"  styleClass="textColorRead" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" /> &nbsp;&nbsp;&nbsp;&nbsp; 
             </logic:notEqual>
   <logic:equal value="###" name="element" property="codesetid">
         <td align="left"  nowrap valign="middle"  class="RecordCellright">
         	<html:select name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="text6">
         		<html:optionsCollection property="codesetidlist" value="dataValue" label="dataName" />
         	</html:select>
             &nbsp;&nbsp;&nbsp;&nbsp;  
   </logic:equal>   
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
         <td align="right" nowrap valign="middle"  class="RecordCellleft">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>         
         </td>
         <td align="left"  nowrap valign="middle"  class="RecordCellright">	
             <input type="text" name='<%="infofieldlist["+index+"].value"%>' value="<bean:write name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>" class="complex_border_color" style='width:200px' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"/>&nbsp;&nbsp;&nbsp;&nbsp; 
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
         <td align="left"  nowrap valign="middle"  class="RecordCellright">
            <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}"/>  &nbsp;&nbsp;&nbsp;&nbsp; 
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
           <td align="right" nowrap valign="middle"  class="RecordCellleft">        
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>         
           </td>
           <td align="left"  nowrap valign="middle"  class="RecordCellright">
               <html:text  name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth + 1}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
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
                 <td align="right" nowrap valign="middle"  class="RecordCellleft">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
                 </td>
                 <td align="left"  nowrap valign="middle"  colspan="3"  class="RecordCellright">
                 <logic:equal name="element" property="itemid" value="${orgInfoForm.orgFieldID}">
                 <logic:equal name="orgInfoForm" property="type" value="1">
                     <div>
                        <table>
                          <tr>
                            <td id="editbutton">
                            <script type="text/javascript">
                            	textAreaId = 'content';
                            </script>
                               <table  border="0" cellspacing="0"  align="center" cellpadding="0">
                              <tr><td width="20">
                              <img src="/images/tree_expand.gif" border="0" alt="编辑" onclick="hiddenContent('content','editbutton');">
                              </td><td>
                               <a href="###" onclick="hiddenContent('content','editbutton');">显示</a>
                              </td></tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        
                      </div>
                      </td></tr>
                      <%if(i%2==0){%>
              		<tr class="trShallow1">            
             		<%}else{%>
               <tr class="trDeep1">  
             <%}i++;%><td align="center" nowrap colspan="4" valign="middle"  class="RecordCellright">
                          <html:textarea name="orgInfoForm" styleId="content" property='<%="infofieldlist["+index+"].value"%>'  rows="3"  cols="60" style="display:none"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                          <div id='htmlEditorDiv' style="margin-top: -33px;"></div>
                       </logic:equal>
                       <logic:notEqual name="orgInfoForm" property="type" value="1">
                       <html:textarea styleClass="complex_border_color" name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                       </logic:notEqual>
                 </logic:equal>
                  <logic:notEqual name="element" property="itemid" value="${orgInfoForm.orgFieldID}">
                     <logic:equal name="element" property="inputtype" value="1">
                          <div>
                        <table>
                          <tr>
                            <td id="${element.itemid}_button">
                            <script type="text/javascript">
                            	textAreaId = 'content_${element.itemid}';
                            </script>
                              <table  border="0" cellspacing="0"  align="center" cellpadding="0">
                              <tr><td width="20">
                              <img src="/images/tree_expand.gif" border="0" alt="编辑" onclick="hiddenContent('content_${element.itemid}','${element.itemid}_button');">
                              </td><td>
                               <a href="###" onclick="hiddenContent('content_${element.itemid}','${element.itemid}_button');">显示</a>
                              </td></tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        
                      </div>
                      </td></tr>
                      <%if(i%2==0){%>
              		<tr class="trShallow1">            
             		<%}else{%>
               <tr class="trDeep1">  
             <%}i++;%><td align="center" nowrap colspan="4" class="RecordCellright">
                          <html:textarea name="orgInfoForm" styleId="content_${element.itemid}" property='<%="infofieldlist["+index+"].value"%>'  rows="3"  cols="60" style="display:none"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                          <div id='htmlEditorDiv' style="margin-top: -33px;"></div>
                     </logic:equal>
                    <logic:notEqual name="element" property="inputtype" value="1">
                     <html:textarea styleClass="complex_border_color" name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                     </logic:notEqual>
                  </logic:notEqual>
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
                <td align="right" nowrap valign="middle"  class="RecordCellleft">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
                </td>
                <td align="left"  nowrap valign="middle" colspan="3"  class="RecordCellright">
                 <logic:equal name="element" property="itemid" value="${orgInfoForm.orgFieldID}">
                 <logic:equal name="orgInfoForm" property="type" value="1">
                      <div>
                        <table>
                          <tr>
                            <td id="editbutton" valign="middle" align="left">
                            <script type="text/javascript">
                            	textAreaId = 'content';
                            </script>
                             <table  border="0" cellspacing="0"  align="center" cellpadding="0">
                              <tr><td width="20">
                              <img src="/images/tree_expand.gif" border="0" alt="编辑" onclick="hiddenContent('content','editbutton');">
                              </td><td>
                               <a href="###" onclick="hiddenContent('content','editbutton');">显示</a>
                              </td></tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        
                      </div>
                      </td></tr>
                      <%if(i%2==0){%>
              		<tr class="trShallow1">            
             		<%}else{%>
               <tr class="trDeep1">  
             <%}i++;%><td align="center" nowrap colspan="4"  class="RecordCellright">
                          <html:textarea name="orgInfoForm" styleId="content" property='<%="infofieldlist["+index+"].value"%>'  rows="3"  cols="60" style="display:none"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                          <div id='htmlEditorDiv' style="margin-top: -33px;"></div>
                       </logic:equal>
                       <logic:notEqual name="orgInfoForm" property="type" value="1">
                      <html:textarea styleClass="complex_border_color" name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                       </logic:notEqual>
                 </logic:equal>
                  <logic:notEqual name="element" property="itemid" value="${orgInfoForm.orgFieldID}">
                     <logic:equal name="element" property="inputtype" value="1">
                          <div>
                        <table>
                          <tr>
                            <td id="${element.itemid}_button">
                            <script type="text/javascript">
                            	textAreaId = 'content_${element.itemid}';
                            </script>
                              <table  border="0" cellspacing="0"  align="center" cellpadding="0">
                              <tr><td width="20">
                               <img src="/images/tree_expand.gif" border="0" alt="编辑" onclick="hiddenContent('content_${element.itemid}','${element.itemid}_button');">
                              </td><td>
                               <a href="###" onclick="hiddenContent('content_${element.itemid}','${element.itemid}_button');">显示</a>
                              </td></tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                        
                      </div>
                      </td></tr>
                      <%if(i%2==0){%>
              		<tr class="trShallow1">            
             		<%}else{%>
               <tr class="trDeep1">  
             <%}i++;%><td align="center" nowrap colspan="4"  class="RecordCellright">
                          <html:textarea name="orgInfoForm" styleId="content_${element.itemid}" property='<%="infofieldlist["+index+"].value"%>'  rows="3"  cols="60" style="display:none"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                          <div id='htmlEditorDiv' style="margin-top: -33px;"></div>
                     </logic:equal>
                    <logic:notEqual name="element" property="inputtype" value="1">
                    <html:textarea styleClass="complex_border_color" name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'  rows="7"  cols="80" style="width:80%"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                    </logic:notEqual>
                  </logic:notEqual>
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
           <td align="right" nowrap valign="middle"  class="RecordCellleft">        
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>          
           </td>
           <td align="left"  nowrap valign="middle"  class="RecordCellright">
                 <html:hidden name="orgInfoForm" property='<%="infofieldlist["+index+"].value"%>'/>  
                 	<!-- 去掉键盘事件 提供模糊查询功能       wangb 20171127             onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" -->
                    <html:text name="orgInfoForm" property='<%="infofieldlist["+index+"].viewvalue"%>' onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"  styleClass="textColorWrite"  onclick="styleDisplay(this);"/>
                    <logic:equal name="element" property="codesetid" value="@K"> 
                    	<!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117  -->
                        <img src="/images/code.gif" align="absmiddle" id="infofieldlist<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" nmodule='4' ctrltype='3' inputname='<%="infofieldlist["+index+"].viewvalue"%>'  valuename="<%="infofieldlist["+index+"].value"%>"/>
                    </logic:equal> 
                    <logic:notEqual name="element" property="codesetid" value="@K">
                    	<!-- 使用代码组件控件兼容非IE浏览器 wangb 20171117  -->
                        <img src="/images/code.gif" align="absmiddle" id="infofieldlist<%=index %>" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infofieldlist["+index+"].viewvalue"%>'  valuename="<%="infofieldlist["+index+"].value"%>"/>
                    </logic:notEqual>
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
 <tr><td height="5"></td></tr>
 <tr>
  <td align="center"  nowrap colspan="4" >
    &nbsp;&nbsp;
         <html:hidden name="orgInfoForm" property="edittype"/> 
         <html:hidden name="orgInfoForm" property="contentField"/>
          <html:hidden name="orgInfoForm" property="contentFieldValue"/>
        <logic:equal name="orgInfoForm" property="setprv" value="2">               
	       <input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save();"/>
	     </logic:equal> 
	     <logic:equal name="orgInfoForm" property="returnvalue" value="scan">
	       <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/orginfo/searchorginfodata.do?b_query=link&code=','nil_body')">
         </logic:equal>
         <logic:equal name="orgInfoForm" property="returnvalue" value="75"><!--预警-->
         <logic:equal value="bi" name="userView" property="bosflag">
             <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonReturn('/system/warn/result_manager.do?b_query=link','i_body');">                 
         </logic:equal>
         <logic:notEqual value="bi" name="userView" property="bosflag">
             <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonReturn('/system/warn/result_manager.do?b_query=link','il_body');">                 
         </logic:notEqual>
         </logic:equal>
  </td>
 </tr>  
 <tr><td height="20"></td></tr>  <!--add by xiegh bug36356  -->
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

<script>
function addsave(isrefresh,codesetid,codeitemid,codeitemdesc,issuperuser,manageprive)
   {
   	//alert(isrefresh+" setid:"+codesetid+" itemid:"+codeitemid+" desc:"+codeitemdesc+" sup:"+issuperuser+" mana:"+manageprive);
   	 if(isrefresh.length<1||codesetid.length<1||codeitemid.length<1||codeitemdesc.length<1||issuperuser.length<1){
   	 	return;
   	 }
   	 
   	 if(isrefresh=='save')
   	 {
   	 	var obj=parent.parent.frames['nil_menu'];
   	 	if(!obj)
   	 		obj=parent.parent.frames['mil_menu'];
   	 	var currnode = obj.Global.selectedItem;
	   	 currnode = currnode.root();
	     if(currnode) {
	        var itemNode = getTreeItem(codesetid+'${orgInfoForm.parentid}', currnode);
	        if(itemNode) {
	            itemNode.select();
	            currnode = itemNode;
	        }
	     }
	     
   	 	var pt = currnode.getLastChild();
   	 	if(pt.uid==codesetid+codeitemid)
   	 		return;
   	 	var uid = codesetid+codeitemid;
   	 	var text = codeitemdesc;
   	 	var title = codeitemdesc;
   	 	var issuperuser = issuperuser;
   	 	var manageprive = manageprive;
   	 	var kind = '0';
   	 	if(codesetid=='UM')
   	 		kind='1';
   	 	else if(codesetid=='UN')
   	 		kind='2';
   	 	var orgtype = "org";

   	 	var action = "/workbench/orginfo/searchorginfodata.do?b_search=link&code="+codeitemid+"&kind="+kind+"&orgtype="+orgtype;
   	 	var xml = "/common/vorg/loadtree?params=child&treetype=org&parentid="+codeitemid + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorginfodata.do&target=nil_body";
   	 	//if(currnode==currnode.root())
   	 		//currnode = currnode.getFirstChild();
   	 	if(currnode.load)
   	 	{
   	 		var imgurl;
   	 		if(codesetid=='UM'){
   	 			
   	 				imgurl="/images/dept.gif";
   	 			
   	 		}
   	 		else if(codesetid=='UN'){
   	 			
   	 				imgurl="/images/unit.gif";
   	 			
   	 		}
   	 		//var tmp = new xtreeItem(uid,text,action,"mil_body",title,imgurl,xml);
   	 		//currnode.expand();
   	 		if(codesetid!='@K')
   	 				obj.add(uid,text,action,"nil_body",title,imgurl,xml);
   	 	}else
   	 		currnode.expand();
   	 }
   }
   function update(isrefresh,codesetid,codeitemid,codeitemdesc)
   {
   	//alert(isrefresh+" setid:"+codesetid+" itemid:"+codeitemid+" desc:"+codeitemdesc);
   	if(isrefresh=='update')
   	{
   		var obj=parent.parent.frames['nil_menu'];
   	 	if(!obj)
   	 		obj=parent.parent.frames['mil_menu'];
   	 	var currnode;
   	 	//liuy 2015-1-26 6888：编辑报表/反查：对6号表反查查看单位基本情况，修改后点击保存，报网页上有错误 start
   	 	if(obj){
	   		currnode=obj.Global.selectedItem;
	   		if((codesetid+codeitemid)==currnode.uid){
	   			currnode.setText(codeitemdesc);
	   			return;
	   		}
	   		if(currnode.load)
				for(var i=0;i<=currnode.childNodes.length-1;i++){
					if((codesetid+codeitemid)==currnode.childNodes[i].uid)
						currnode.childNodes[i].setText(codeitemdesc);
	   		}
   	 	}
   	 	//liuy 2015-1-26 end
   	}
   }
addsave('${orgInfoForm.isrefresh}','${orgInfoForm.codesetid}','${orgInfoForm.codeitemid}','${orgInfoForm.codeitemdesc}','${orgInfoForm.issuperuser}','${orgInfoForm.manageprive}');
<logic:notEqual name="orgInfoForm" property="returnvalue" value="75">
	  	update('${orgInfoForm.isrefresh}','${orgInfoForm.codesetid}','${orgInfoForm.codeitemid}','${orgInfoForm.codeitemdesc}'); 
</logic:notEqual>

Ext.onReady(function() {
	var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
		width:'100%',
        height:'100%',
        functionType:"standard",
        id: "htmlEditor",
    });
	
    if(document.getElementById("htmlEditorDiv")) {
        Ext.create('Ext.panel.Panel', {
	        id:'htmlEditorPanel',             
	        border: 1,
	        width: (document.body.clientWidth-100),
	        height: 460, 
	        hidden: true,
	        items: [CKEditor],               
	        renderTo: 'htmlEditorDiv'
       });
       
       if(textAreaId) {
		   var textArea = document.getElementById(textAreaId);
		   var htmlEditor = Ext.getCmp("htmlEditor");
	  	   htmlEditor.setValue(textArea.value);
       }
    }	
});

//获取分类树上对应的节点 itemid：节点id；currnode：节点
function getTreeItem(itemid, currnode) {
    if(currnode) {
        if(itemid==currnode.uid)
            return currnode;
        else if(currnode.load){
            var childNode;
            for (var i = 0; i < currnode.childNodes.length; i++) {
                childNode = currnode.childNodes[i];
                if(childNode.uid == "Loading..."){
                    childNode = undefined;
                    continue;
                }
                
                childNode = getTreeItem(itemid, childNode);
                if(childNode)
                    break;
            }
            
            if(childNode)
                return childNode;

        } else if(!currnode.load)
            return false;
    }
}
parent.editType = "${orgInfoForm.edittype}";
parent.b00Code = "${orgInfoForm.codeitemid}";
</script>
<%
	OrgInfoForm oif = (OrgInfoForm)request.getSession().getAttribute("orgInfoForm");
	oif.setIsrefresh("");
%>