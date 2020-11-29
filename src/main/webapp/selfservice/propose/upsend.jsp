<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
	String uploadfilemaxsize = SystemConfig.getPropertyValue("uploadfilemaxsize");
try{
	Integer.parseInt(uploadfilemaxsize);
}catch(Exception e){
	uploadfilemaxsize = "20";
}
pageContext.setAttribute("uploadfilemaxsize", uploadfilemaxsize);
	
 %>
<style type="text/css">
<!--
.framestyle1 {
   BACKGROUND-COLOR:#F7FAFF;
   BORDER-BOTTOM: #94B6E6 0pt solid; 
   BORDER-LEFT: #94B6E6 1pt solid; 
   BORDER-RIGHT: #94B6E6 1pt solid; 
   BORDER-TOP: #94B6E6 1pt solid; 
}
-->
</style>
<hrms:themes></hrms:themes>
 <script type="text/javascript" src="../../../js/validate.js"></script>
<script type="text/javascript" src="../../../components/ckEditor/CKEditor.js"></script>
<script language="javascript">
   function setDescrption()
   {
		var oldInputs = document.getElementsByName('htmlFileListvo.string(description)');
		var txtobj=oldInputs[0];
		var oEditor = Ext.getCmp("ckeditorid");
		var tmpvalue=oEditor.getHtml();
		txtobj.value = tmpvalue;
   }
   
   function validatefilepath(){
	 	var mediapath=document.htmlFileListForm.file.value;
	 	if(mediapath.length>2)
	 		return validateUploadFilePath(mediapath);
	 	else
	 		return true;
	 }
	//点击保存按钮提交表单时，验证标题文本框内容不能超过40个字符 wangb 12936  20170519
	function vailTitle(){
		var title=document.getElementsByTagName('input')[0].value;
		/*提示2遍 去掉   
		if(title.length == 0){
			alert("标题不能为空");
			return false;
		} 
		*/
		var byteLength=0;
		for(var i=0 ; i< title.length ; i++){
			var charCode=title.charCodeAt(i);
			if(charCode >=0 && charCode <=128)
				byteLength+=1;
			else
				byteLength+=2;
		}
		if(byteLength>40){
			alert("标题长度不能超过40个字符！");
			return false;
		}
		return true;
		
	}
	function myClear(){ 
		var oEditor = Ext.getCmp("ckeditorid");
		oEditor.setValue("");
	}
</script>
<html:form action="/selfservice/propose/upsend"  enctype="multipart/form-data">
<table align="center" width="700" style="margin-top:6px;" cellpadding="0" cellspacing="0" align="center" class="ftable complex_border_color">
          <tr>
       		<td align="left" colspan="2" class="TableRow"><bean:message key="lable.resource_list.upfile"/></td>
          </tr> 
          <tr>

		        <td align="right" nowrap valign="center" >
			    <!--<html:hidden  name="htmlFileListForm" property="htmlFileListvo.string(status)" value="1"/>-->
			    <bean:message key="conlumn.resource_list.title"/>
			    </td>
			    <td align="left" nowrap><html:text name="htmlFileListForm" property="htmlFileListvo.string(name)" styleClass="text4" style="width:300px;"/>
		        </td>
         </tr>
     
     <tr>
			<td align="right" nowrap valign="top" >
			   <bean:message key="conlumn.resource_list.descrption"/>
			</td>
			<td id="tableEdit" align="left" nowrap class="common_border_color">
			    <html:textarea name="htmlFileListForm" property="htmlFileListvo.string(description)" cols="80" rows="20" style="filter:alpha(Opacity=100);display:none;"/>
			    <script type="text/javascript">
				var oldInputs = document.getElementsByName('htmlFileListvo.string(description)');                             
	              var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
		              id:'ckeditorid',
		              functionType:"standard",         
		              width:'100%',
			      	  height:'100%'      
		            });  
	          	
	          	 var Panel = Ext.create('Ext.panel.Panel', {
	    			 id:'ckeditorPanel',			 
	                 border: false,
	                 width: '100%',
	                 height:300,
	    			 items: [CKEditor],	 			  
	    			 renderTo: "tableEdit"
	    			});
	          	 
	          	var oEditor = Ext.getCmp("ckeditorid");
	          	oEditor.setValue(oldInputs[0].value);
                   </script>		    
			</td>
     </tr>
     
     <tr>
			  <td align="right" nowrap valign="center" ><bean:message key="lable.resource_list.upfile"/></td>
			  <td align="left" nowrap> <html:file name="htmlFileListForm" property="file" style="width:300px;" styleClass="text4" onchange="checkFileMaxSize(this,${uploadfilemaxsize })"/></td>
     </tr>
	 <tr>
		<!-- 添加是否允许下载    jingq add  2014.6.3 -->
			<td align="right" nowrap valign="center">
				<bean:message key="conlumn.resource_listt.allowup" />
			</td>
			<td>
				<html:radio name="htmlFileListForm" property="htmlFileListvo.string(status)" value="1" />
				<bean:message key="datestyle.yes" />
				&nbsp;
				<html:radio name="htmlFileListForm" property="htmlFileListvo.string(status)" value="0" />
				<bean:message key="datesytle.no" />
			</td>
	 </tr>
     <tr>
       <td  align="center" colspan="2" style="height: 35px">
						<hrms:submit styleClass="mybutton" property="b_add" onclick="document.htmlFileListForm.target='_self';setDescrption();validate( 'R','htmlFileListvo.string(name)','标题');return (vailTitle()&&document.returnValue &&validatefilepath()&& ifqrbc());validateUploadFilePath(document.getElementsByName('file')[0].value);">
            					<bean:message key="button.save"/>
	 					</hrms:submit>
	 					<html:reset styleClass="mybutton" property="reset" onclick="myClear()">
	 					<bean:message key="button.clear"/>
	 					</html:reset>	 
	 					<input type="button" class="mybutton" name="br_return" value="<bean:message key="button.return"/>" onclick="history.go(-1);"/>
         				<%-- <hrms:submit styleClass="mybutton" property="br_return">
            				<bean:message key="button.return"/>
            			</hrms:submit> --%>
         </td>
     </tr>
</table>


</html:form>
<script>
	if(!getBrowseVersion() || getBrowseVersion() == 10){
		var file = document.getElementsByName('file')[0];
		file.style.height='26px';
		file.style.lineHeight='14px';
	}

</script>
