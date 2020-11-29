<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
<script type="text/javascript" src="../../../components/ckEditor/CKEditor.js"></script>
<hrms:themes></hrms:themes>
<script language="javascript">
   function setDescrption()
   {       
 	  var oldInputs = document.getElementsByName('htmlFileListvo.string(description)');
       if(oldInputs==null)
         	 return;  
		var txtobj=oldInputs[0];
		var oEditor = Ext.getCmp("ckeditorid");
		var tmpvalue=oEditor.getHtml();
		txtobj.value = tmpvalue;
   }
   
   function validatefilepath(){
		 	var mediapath=document.getElementsByName("file")[0].value;
		 	if(mediapath.length>2){
			 	document.returnValue=validateUploadFilePath(mediapath);
			 	return document.returnValue;
		 	}
	}
	function myClear(){ 
		var oEditor = Ext.getCmp("ckeditorid");
		oEditor.setValue("");
	}
   //点击保存按钮提交表单时，验证标题文本框内容不能超过40个字符 wangb 12936  20170519
   function vailTitle(){
	   var title=document.getElementsByTagName('input')[0].value;
	   if(title.length == 0){
		   alert("标题不能为空");
		   return false;
	   }
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
</script>
<html:form action="/selfservice/propose/addhtmlfile" enctype="multipart/form-data">
	<table width="700" style="margin-top:6px;" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
		<tr height="20">
			<td align="left" class="TableRow" colspan="2">
				<bean:message key="lable.resource_list.upfilerepair" />
			</td>
		</tr>
		<tr>
						<td align="right" nowrap valign="center">
							<bean:message key="conlumn.resource_list.title" />
						</td>
						<td align="left" nowrap>
							<html:text name="htmlFileListForm" property="htmlFileListvo.string(name)" maxlength="40" styleClass="text4" style="width:300px;"/>
						</td>
		</tr>
		<tr>
						<td  valign="top" align="right">							
							<bean:message key="conlumn.resource_list.descrption" />
						</td>
						<td id="tableEdit">
							<html:textarea name="htmlFileListForm" property="htmlFileListvo.string(description)" cols="80" rows="20" style="filter:alpha(Opacity=100);display:none;" />
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
			<td align="right"><bean:message key="lable.lawfile.upfile" /></td>
			<td><input type="file" name="file" style="width:300px;" class="text4" onchange="checkFileMaxSize(this,${uploadfilemaxsize })"></td>
		</tr>
		<tr>
		<!-- 流程上传修改界面样式    jingq upd  2014.5.19 -->
			<td align="right">
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
		<tr >
			<td align="center" colspan="2" style="height: 35px">
							<hrms:submit styleClass="mybutton" property="b_save" onclick="document.htmlFileListForm.target='_self';setDescrption();validate( 'R','htmlFileListvo.string(name)','标题');return (document.returnValue&&vailTitle()&&validatefilepath() && ifqrbc());validateUploadFilePath(document.getElementsByName('file')[0].value);">
								<bean:message key="button.save" />
							</hrms:submit>

							<html:reset styleClass="mybutton" property="reset" onclick="myClear()">
								<bean:message key="button.clear" />
							</html:reset>

							<%-- <hrms:submit styleClass="mybutton" property="br_return">
								<bean:message key="button.return" />
							</hrms:submit> --%>
							<input type="button" class="mybutton" name="br_return" value="<bean:message key="button.return"/>" onclick="history.go(-1);"/>
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
