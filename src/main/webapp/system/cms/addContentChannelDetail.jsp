<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<html:html>
<head>
<script type="text/javascript">

function isIE() { //ie?  
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 }
function fillout(){
	var oEditor = Ext.getCmp("ckeditorid");
    var oldInputs = document.getElementsByName("contentvo.string(content)");
    oldInputs[0].value = oEditor.getHtml();
}

function loadCKEditor(){
	var oldInputs = document.getElementsByName('contentvo.string(content)');                             
    var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
        id:'ckeditorid',
        functionType:"standard",         
        width:'100%',
    	  height:'100%'      
      });  
	
    if(Ext.getCmp("ckeditorPanel"))
    	Ext.getCmp("ckeditorPanel").destroy();
    
	 var Panel = Ext.create('Ext.panel.Panel', {
		 id:'ckeditorPanel',			 
		 bodyStyle: 'border-width:0 0 0 0; background:transparent',
         width: '100%',
         height: 500, 
		 items: [CKEditor],	 			  
		 renderTo: "contentPanel"
		});
  
	var oEditor = CKEditor;
	oEditor.setValue(oldInputs[0].value);
}

function displayTR(){
   var contentUrl = document.getElementById("contentUrl");
   var contentUrl2 = document.getElementById("contentUrl2");
   var targeType = document.getElementById("targeTypeId");
   var contentContent = document.getElementById("contentContent");
   var contentContent2 = document.getElementById("contentContent2");
   var contentType = document.getElementsByName("contentvo.string(content_type)");
   if(contentType[0].value == 1){
       contentUrl.style.display="none";
       contentUrl2.style.display="none";
       contentContent.style.display="block";
       contentContent2.style.display="block";
       if(Ext.getCmp("ckeditorid").ckeditorReady){
    	   this.loadCKEditor();
	    }
   }
   
   if(contentType[0].value == 0 ){
	       contentContent.style.display="none";
	       contentContent2.style.display="none";
	       contentUrl2.style.display="block";
	       contentUrl.style.display="block";
	}
}


//-->
</script>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7;">
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/ckEditor/CKEditor.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<style type="text/css">
.framestyle1 {
	/*
   BACKGROUND-COLOR:#F7FAFF;
    */
   BORDER-BOTTOM: #C4D8EE 1pt solid; 
   BORDER-LEFT: #C4D8EE 1pt solid; 
   BORDER-RIGHT: #C4D8EE 1pt solid; 
   BORDER-TOP: #C4D8EE 0pt solid; 
}
</style>
</head>
<hrms:themes></hrms:themes>
<body onload="displayTR();" >
<html:form action="/sys/cms/addContentChannelDetail">
       <table width="700px" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
          <tr height="20">
       		<td  align=left class="TableRow" id="topic"><bean:message key="lable.channel.content"/>&nbsp;</td>
          </tr> 
          <tr>
            <td class="framestyle1" width="700px">
               <table border="0" cellspacing="2"  width="700px" cellpadding="0">    
                      <tr class="list3" style="width:700px;display: block" >
                	      <td align="right" width="200" nowrap ><bean:message key="lable.content_channel.title"/>&nbsp;&nbsp;</td>
                	      <td align="left" width="500" nowrap >
                	      	<html:text name="channelContentDetailForm" property="contentvo.string(title)"  styleClass="TEXT4" size="30"  maxlength="250" />    	      
                          </td>
                      </tr>  
                      
                       <tr class="list3" style="width:700px;display: block" >
                           <td align="right" width="200" nowrap><bean:message key="lable.content_channel.type"/>&nbsp;&nbsp;</td>
                          <td  width="70" nowrap>
                                <html:select name="channelContentDetailForm" property="contentvo.string(content_type)" size="1" style="width:67px" onchange="displayTR();">
                                    <html:option value="1">内容</html:option>
                                     <html:option value="0">超链</html:option>
                                </html:select>  
                                </td>
                                <td  width="30" nowrap><bean:message key="lable.channel.hide"/></td>
                                <td  width="400" nowrap><html:checkbox name="channelContentDetailForm" property="contentvo.string(visible)" value="0" /></td>              
                           
                      </tr>  
                      
                      
                      
           			  <tr class="list3"  style="<bean:write name="channelContentDetailForm" property="display"/>" id="contentUrl">
                	      <td align="right" width="200" nowrap><bean:message key="lable.content_channel.outurl"/>&nbsp;&nbsp;</td>
                	      <td align="left" width="75%" colspan="3" nowrap>
                	      	<html:text name="channelContentDetailForm" property="contentvo.string(out_url)" size="50" maxlength="250" styleClass="text4"/>
                	      </td>
                	                             
		             </tr>   
		                 
		             <tr class="list3" style="width:700px;display: block">
                           <td align="right" width="200" nowrap><bean:message key="lable.content_channel.params"/>&nbsp;&nbsp;</td>
                          <td align="left" width="75%"  colspan="3" nowrap>
                          <div id="contentUrl2" style="display: block;float: left;margin-right: 10px;" >
                          		<html:text name="channelContentDetailForm"  property="contentvo.string(params)" size="50" maxlength="250" styleClass="text4"/>
                          </div>
                                <html:select name="channelContentDetailForm" property="contentvo.string(target)" size="1">
                                    <html:option value="_blank">_blank</html:option>
                                    <html:option value="_self">_self</html:option>
                                    <html:option value="_top">_top</html:option>
                                    <html:option value="_parent">_parent</html:option>                                  
                                </html:select>                            
                          </td> 
                                                 
                     </tr>
                                     
                </table>
              </td>      
           </tr>                 

           <tr class="list3"  id="contentContent" style="<bean:write name="channelContentDetailForm" property="content_display"/>">
                      <td id = "contentPanel" class="framestyle1"  align="left" colspan="4" width="100%"  nowrap><bean:message key="lable.channel_detail.content"/>&nbsp;&nbsp;</td>
           </tr>
           <tr class="list3" id="contentContent2"  style="<bean:write name="channelContentDetailForm" property="content_display"/>">
                      <td align="left"  nowrap class="list3">
                      <html:textarea name="channelContentDetailForm" property="contentvo.string(content)" cols="100" rows="600" style="display:none;"/>
                      <script type="text/javascript">
                      var oldInputs = document.getElementsByName('contentvo.string(content)');                             
                      var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
                          id:'ckeditorid',
                          functionType:"standard",         
                          width:'100%',
                      	  height:'100%'      
                        });  
                  	
                  	 var Panel = Ext.create('Ext.panel.Panel', {
                  		 id:'ckeditorPanel',			 
                  		 bodyStyle: 'border-width:0 0 0 0; background:transparent',
                         width: '100%',
                         height: 500, 
                  		 items: [CKEditor],	 			  
                  		 renderTo: "contentPanel"
                  		});
                    
                  	var oEditor = Ext.getCmp("ckeditorid");
                  	oEditor.setValue(oldInputs[0].value);        
           
                       </script>
 					   <html:hidden name="channelContentDetailForm" property="content_id"/> 
                	   <html:hidden name="channelContentDetailForm" property="channel_id"/>
 					</td>  
          </tr>                                                   
          <tr class="list3" >
            <td align="center" style="padding-top:5px;">
         	<html:submit styleClass="mybutton" property="b_save" onclick="fillout();document.channelContentDetailForm.target='_self';validate('R','contentvo.string(title)','内容标题');return (document.returnValue);">
            		<bean:message key="button.save"/>
	 	    </html:submit>
	       	<html:submit styleClass="mybutton" property="br_return" >
            		<bean:message key="button.return"/>
	 	    </html:submit>            
            </td>
          </tr>          
      </table>
</html:form>
</body>
</html:html>
