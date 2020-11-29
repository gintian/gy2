<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.train.resource.course.CoursewareForm,com.hjsj.hrms.businessobject.train.MediaServerParamBo,com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.FieldItem"%>
<%@page import="java.util.Map,java.util.Iterator"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<style>
<!--
.textbox{border:solid 1px #98C2E8;}
.textboxMul{border:solid 1px #98C2E8;}
.span{border:1px solid #98C2E8; position:absolute; overflow:hidden;margin-left: 4px;}
-->
</style>
<%
	//String filepath = request.getSession().getServletContext()
	//		.getRealPath("/");
	String filepath = request.getSession().getServletContext().getRealPath("/");
	if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
	{
		filepath=session.getServletContext().getResource("/").getPath();//.substring(0);
	   if(filepath.indexOf(':')!=-1)
		  {
		   filepath=filepath.substring(1);   
		  }
		  else
		  {
			  filepath=filepath.substring(0);      
		  }
	   int nlen=filepath.length();
		  StringBuffer buf=new StringBuffer();
		  buf.append(filepath);
		  //buf.setLength(nlen-1);
		  filepath=buf.toString();
	}
	filepath = filepath.replace("\\", "``");
	filepath = SafeCode.encode(PubFunc.encrypt(filepath));
%>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/ckEditor/CKEditor.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/common.js"></script>

<script type="text/javascript" src="/general/swfupload/js/swfupload_single.js"></script>
<script type="text/javascript" src="/general/swfupload/js/swfupload.queue_single.js"></script>
<script type="text/javascript" src="/general/swfupload/js/fileprogress_single.js"></script>
<script type="text/javascript" src="/general/swfupload/js/handlers_single.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript">

	function closedView(flag){
		var aa = eval("tableEdit");
		var bb= eval("table");
		var cc=eval("table1");
		var oEditor;
		oEditor = Ext.getCmp("ckeditorid");
        aa.style.display = 'none';
        document.getElementById("define").style.display="none";
    	document.getElementById("return").style.display="none";
        bb.style.display = '';
        cc.style.display = '';
        oEditor.getHtml();
	}
	
	
	function loadCKEditor(){
		if(Ext.getCmp("ckeditorid"))
	    	Ext.getCmp("ckeditorid").destroy();
		
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
		         hidden :true,
		         width: 680,
		     	 height: 455, 
				 items: [CKEditor],	 			  
				 renderTo: "tableEdit"
			});
	}
	
	function showView(flg,id) {
		var aa = eval("tableEdit");
		var bb= eval("table");
		var cc=eval("table1");
		var oEditor;	
		var el=document.getElementById(id);	
		if (flg == true) {
	    	this.loadCKEditor();
			Ext.getCmp('ckeditorPanel').show();
	    	oEditor = Ext.getCmp("ckeditorid");
	    	document.getElementById("define").style.display="";
	    	document.getElementById("return").style.display="";
	    	document.getElementsByName("tempname")[0].value=el.name;
	    	if(document.getElementById("div6").innerHTML != undefined && document.getElementById("div6").innerText !="" )
	    		Ext.getCmp("ckeditorid").setValue(document.getElementById("div6").innerHTML);
    	    aa.style.display = '';
	        bb.style.display = "none"; 
	        cc.style.display = "none";
	    } else {
	        oEditor = Ext.getCmp("ckeditorid");
	    	var tempname=document.getElementsByName("tempname")[0].value;
	        aa.style.display = 'none';
	        bb.style.display = '';
	        cc.style.display = '';
	        document.getElementById("define").style.display="none";
	    	document.getElementById("return").style.display="none";
 	        document.getElementsByName(tempname)[0].value = oEditor.getHtml();
 	       	var tmpvalue= oEditor.getHtml();
 	  		document.getElementById("div6").innerHTML = tmpvalue;
	    }
	    
	}
	
    function IsDigit() 
    { 
    return ((event.keyCode != 96)); 
    }      
    function namelen()
    {
      var obj=document.getElementById("setname");
      var len=obj.value.length;
      alert(len);
      if(len>=50)
      {
        alert("文件名称字数不能超过50！");
        return false;
      }else
      {
        return true;
      }
    }
   
    
    function delete_file(ext,fileid)
    {
    	var  text;
    	if(ext=="ext")
    	{
    		text = "确定要删除文件吗？";
    	}
    	else if (ext=="orgext")
    	{
    		text = "确定要删除原件吗？";
    	}
    	if(confirm(text))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("ext",ext);
			hashvo.setValue("file_id",fileid);
			var request=new Request({method:'post',asynchronous:false,onSuccess:delete_ok,functionId:'10400201047'},hashvo);
		}
    }
    function delete_ok(outparamters)
	{
		var mess = outparamters.getValue("mess");
		if(mess=="ok"){
			coursewareForm.action = parent.frames["mil_menu"].Global.selectedItem.action;
			coursewareForm.submit();
		}
    }
    function change(){
    	var selectr5105 = document.getElementById("selectr5105").value;
		var _tr5 = document.getElementById("trnamer5113");
		var _tr6 = document.getElementById("trnamer5115");
		if(selectr5105==2){
			_tr5.style.display = 'none';
			_tr6.style.display = '';
			document.getElementById("editr5115").value=document.getElementById("tempedit").value;
		}else{
			document.getElementById("tempedit").value = document.getElementById("editr5115").value;
			document.getElementById("editr5115").value="";
			_tr6.style.display = 'none';
			_tr5.style.display = '';
			loadobj(selectr5105);
		}
		var _tr7 = document.getElementById("trnamer5117");
		if(_tr7){
			if(selectr5105==3)
				_tr7.style.display = '';
			else
				_tr7.style.display = 'none';
		}
		var _tr8 = document.getElementById("trnameurl");
		if(_tr8){
			if(selectr5105==6){
				_tr8.style.display = '';
				_tr5.style.display = 'none';
			}else
				_tr8.style.display = 'none';
		}
    }
    function change1(){
    	var selectr5105 = document.getElementById("selectr5105").value;
		var _tr5 = document.getElementById("trnamer5113");
		var _tr6 = document.getElementById("trnamer5115");	
		if(selectr5105==2){
			_tr5.style.display = 'none';
			_tr6.style.display = '';
		}else{
			_tr6.style.display = 'none';
			_tr5.style.display = '';
			loadobj(selectr5105);
		}
		var _tr7 = document.getElementById("trnamer5117");
		if(_tr7){
			if(selectr5105==3)
				_tr7.style.display = '';
			else
				_tr7.style.display = 'none';
		}
		var _tr8 = document.getElementById("trnameurl");
		if(_tr8){
			if(selectr5105==6){
				_tr8.style.display = '';
				_tr5.style.display = 'none';
			}else
				_tr8.style.display = 'none';
		}
    }
function savecourseware0(){
    <% int m=0; %>
    var r5105=document.getElementById("selectr5105").value;
	<logic:iterate  id="element1"    name="coursewareForm"  property="itemlist"> 
	<%
		FieldItem abean1=(FieldItem)pageContext.getAttribute("element1");
	    boolean isFillable=abean1.isFillable();	
	    int len = abean1.getItemlength();
	    String itemdesc=abean1.getItemdesc();
	%>		
		var aa<%=m%>=document.getElementsByName("itemlist[<%=m%>].value");
		<%if("r5103".equalsIgnoreCase(abean1.getItemid())){
		    if(len > 50)
		        len = 50;
		%>
			if(IsOverStrLength(aa<%=m%>[0].value,<%=len %>)){
	 			alert("<%=itemdesc%>"+TRAIN_ROOM_MORE_LENGTH1+<%=len %>+TRAIN_ROOM_MORE_LENGTH2+<%=len %>/2+TRAIN_ROOM_MORE_LENGTH3);
	 			aa<%=m%>[0].focus();
	 			aa<%=m%>[0].value='';
	 			buttonDisabled();
	 			return;
	 		}
		<%}%>
		
		if(<%="r5117".equalsIgnoreCase(abean1.getItemid())%>&&r5105!=3){//时长验证
		}else if(r5105!="2"&&<%="r5115".equalsIgnoreCase(abean1.getItemid())%>){//多媒体课件 不需要判断文本内容
		}else if(<%="r5113".equalsIgnoreCase(abean1.getItemid())%>){//文本课件 不需要判断课件URL
		<%if("r5113".equalsIgnoreCase(abean1.getItemid())){%>
			if(r5105!="2"&&<%=isFillable%>){
				if((!"${coursewareForm.r5100}"&&document.getElementById("path_name").value=='')||("${coursewareForm.r5100}"&&!"${element1.value}")){
				alert("上传课件"+THIS_IS_MUST_FILL+"！");
				return;
			}}
		<%}%>
	    }else{	
			if(<%=isFillable%>)
			{
				if(aa<%=m%>[0].value=="")
				{
					alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
					return;						
				}
			}
		}
		<% m++; %>
	</logic:iterate>
	 var name = document.getElementById("r5103name").value;
  if(name==""){
    alert("请输入课件名称！");
    return;
  }
  
	document.getElementById("wait").style.display="";
	document.getElementById("r5103name").readonly="readonly";
	document.getElementById("meidasave").disabled=true;
	document.getElementById("returnbutton").disabled=true;
    savecourseware();
}
//验证非法字符
function checkComments(){ 
//33-47 ascii码对应!"#$%&"()*+'-./     58-64 ascii码对应 ：；<=>?@      91-96 ascii码对应[\]^_`      123-126 ascii码对应{|}~
if (( event.keyCode > 32 && event.keyCode < 48) || 
	( event.keyCode > 57 && event.keyCode < 65) || 
	( event.keyCode > 90 && event.keyCode < 97) || 
	( event.keyCode > 122 && event.keyCode < 127)) { 
		event.returnValue = false; 
	} 
} 


function ly(){
	document.getElementById("r5103name").disabled=false;
}

</script>
<hrms:themes></hrms:themes>
<body onLoad="change1();">
<html:form action="/train/resource/courseware"
	enctype="multipart/form-data">
	<center>
	<html:hidden name="coursewareForm" property="a_code" styleId="classCode"/>
	<html:hidden name="coursewareForm" property="r5000" />
	<html:hidden name="coursewareForm" property="r5100" />
	<input type="hidden" name="filepath" value="<%=filepath%>" />
	<html:hidden name="coursewareForm" property="newPath" styleId="newPathId" />
	<input type="hidden" id="tempedit" value="" />
	<table id="table" width="750" border="0" cellpadding="0"
		cellspacing="0" align="center"  style="margin-top: 10px;line-height: 30px;">
		<tr>
		    <td align="left" class="TableRow" colspan="2" style="height: 30px;">&nbsp;<bean:message key="train.course.addcourseware"/>&nbsp;</td>     
		</tr>
		<logic:iterate id="element" indexId="index" name="coursewareForm"
			property="itemlist">
			<%
					FieldItem abean = (FieldItem) pageContext.getAttribute("element");
								boolean isFillable1 = abean.isFillable();
							%>
			<tr id="trname${element.itemid }">
				
							<logic:equal name="element" property="itemid" value="r5113">
								<td width="130" class="RecordRow" align="right" valign="bottom" style="padding-bottom: 5px;border-top:0px;border-right:0px;">
									&nbsp;<bean:message key="train.course.upcourseware" />&nbsp;
								</td>
								<td class="RecordRow" style="padding-left:3px;padding-bottom: 5px;border-top:0px;" align="left" valign="bottom">
									<input type="hidden" name="path_old" id="path_old" value='<bean:write name="element" property="value" />'>
									<span id="divupload" style="width: 410px;"></span>
									<%
												if (isFillable1) {
											%> <font color='red'>*</font>&nbsp;<%
									 	}
									 %>
								</td>
							</logic:equal>
							<logic:notEqual name="element" property="itemid" value="r5113">
								<td width="130" class="RecordRow" align="right" valign="top"style="border-top:0px;border-right:0px;">
									&nbsp;<bean:write name="element" property="itemdesc" filter="true" />&nbsp;
								</td class="RecordRow" style="border-top:0px;" align="left">
								<logic:equal name="element" property="itemid" value="r5103">
									<td class="RecordRow" style="border-top:0px;" align="left">
										&nbsp;<input type="text" id="r5103name"  style="width:225px"
											name='<%="itemlist[" + index
											+ "].value"%>' class="textColorWrite"
											value="${element.value }" onkeypress="checkComments();" onkeyup="buttonDisabled();"/>&nbsp;
										<%
												if (isFillable1) {
											%> <font color='red'>*</font>&nbsp;<%
									 	}
									 %>
									</td>
								</logic:equal>
								<logic:notEqual name="element" property="itemid" value="r5103">
									<logic:notEqual name="element" property="codesetid" value="0">
										<logic:equal name="element" property="itemtype" value="D">
											<td class="RecordRow" style="border-top:0px;" align="left">
												&nbsp;<input type="text"  
													name='<%="itemlist[" + index
													+ "].value"%>'
													maxlength="50" size="29" id="${element.itemid}"
													extra="editor" class="textColorWrite"
													style="font-size: 10pt; text-align: left"
													dropDown="dropDownDate" value="${element.value}"
													onchange=" if(!validate(this,'${element.itemdesc}')) {this.focus(); this.value=''; }">&nbsp;
													<%
															if (isFillable1) {
														%> <font color='red'>*</font>&nbsp;<%
												 	}
												 %>
											</td>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="D">
											<td class="RecordRow" valign="middle" style="border-top:0px;" align="left">
											<select style="width:225px;border:1px solid;padding: 0px;margin: 3px;" id="selectr5105" onchange="change()"
											name="<%="itemlist[" + index + "].value"%>"> 
											<logic:iterate id="r5105arr" name="coursewareForm" property="r5105">
												<logic:equal value="${r5105arr.key }" name="element" property="value">
													<option value="${r5105arr.key }" selected="selected">${r5105arr.value }</option>
												</logic:equal>
												<logic:notEqual value="${r5105arr.key }" name="element" property="value">
													<option value="${r5105arr.key }">${r5105arr.value }</option>
												</logic:notEqual>
											</logic:iterate>
											</select>&nbsp;
											<%
															if (isFillable1) {
														%> <font color='red'>*</font>&nbsp;<%
												 	}
												 %>
											</td>
											<!-- <td>
												<input type="hidden"
													name='<%="itemlist[" + index
													+ "].value"%>' value="${element.value }"/>
												<input type="text"
													name='<%="itemlist[" + index
													+ "].viewvalue"%>' value="${element.viewvalue }"/>
											</td>
											<TD>
												<img src="/images/code.gif"
													onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="itemlist[" + index
													+ "].viewvalue"%>");' />
											</TD> -->

										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="codesetid" value="0">
										<logic:equal name="element" property="itemtype" value="D">
											<td class="RecordRow" style="border-top:0px;" align="left">
												&nbsp;<input type="text"
													name='<%="itemlist[" + index
													+ "].value"%>'
													maxlength="50" size="29" id="${element.itemid}"
													extra="editor" class="textColorWrite"
													style="font-size: 10pt; text-align: left;width: 225px;"
													dropDown="dropDownDate" value="${element.value}"
													onchange=" if(!validate(this,'${element.itemdesc}')) {this.focus(); this.value=''; }">&nbsp;
													<%
															if (isFillable1) {
														%> <font color='red'>*</font>&nbsp;<%
												 	}
												 %>
											</td>
										</logic:equal>
										<logic:equal name="element" property="itemtype" value="N">
											<td class="RecordRow" style="border-top:0px;" align="left">
											<bean:define id="num" name="element" property="itemlength"></bean:define>
												&nbsp;<input type="text"
													name='<%="itemlist[" + index
													+ "].value"%>'
													maxlength="<%=num.toString() %>" size="29" id="${element.itemid}" 
													style="font-size: 10pt; text-align: left;width: 225px;"
													class="textColorWrite" onkeypress="event.returnValue=IsDigit2(this);"  onblur='isNumber(this);'
													value="${element.value}" />&nbsp;
													<%
															if (isFillable1) {
														%> <font color='red'>*</font>&nbsp;<%
												 	}
												 %>
											</td>
										</logic:equal>
										<logic:equal name="element" property="itemtype" value="A">
											<td class="RecordRow" style="border-top:0px;" align="left">
												&nbsp;<input type="text"
													name='<%="itemlist[" + index
													+ "].value"%>'
													maxlength="50" size="29" id="${element.itemid}" 
													style="font-size: 10pt; text-align: left;width: 225px;"
													class="textColorWrite" value="${element.value}" />&nbsp;
													<%
															if (isFillable1) {
														%> <font color='red'>*</font>&nbsp;<%
												 	}
												 %>
											</td>
										</logic:equal>
										<logic:equal name="element" property="itemtype" value="M">
											<td class="RecordRow" style="padding: 5px;padding-left: 0px;border-top:0px;" align="left">
													<logic:notEqual value="r5115" name="element" property="itemid">
													&nbsp;<html:textarea name="coursewareForm" styleClass="textboxMul common_border_color"  styleId="edit${element.itemid }"
													property='<%="itemlist[" + index
													+ "].value"%>' rows="10" cols="75"></html:textarea>
													&nbsp;
													</logic:notEqual>
													
													<logic:equal value="r5115" name="element" property="itemid">
													<input type="hidden" name="tempname" />
													<html:hidden name="coursewareForm"  styleId="edit${element.itemid }"
													property='<%="itemlist[" + index  + "].value"%>' /> 
														<!--<html:textarea name="coursewareForm"  styleId="edit${index }"
														property='<%="itemlist[" + index
														+ "].value"%>' rows="6" cols="75"></html:textarea>
														<script language="JavaScript">
															var oe = new FCKeditor('edit6');
															oe.ToolbarSet="My1";
															oe.Width=550;
													        oe.ReplaceTextarea();
														</script> -->
														<div id="div6" style="width: 545px;height: 157px;border: 1px solid #98C2E8;overflow-y: scroll;overflow-x: auto;padding-left: 3px;margin-left: 5px;float: left;" class="common_border_color" >
														  <bean:write name="coursewareForm" property='<%="itemlist[" + index + "].value"%>' filter="false"/>
														</div>&nbsp;
														<img alt="编辑" src="/images/edit.gif" onclick="javascript:showView(true,'edit${element.itemid }');"/>&nbsp;
													</logic:equal>
													<%
															if (isFillable1) {
														%> <font color='red'>*</font>&nbsp;<%
												 	}
												 %>
											</td>
										</logic:equal>
									</logic:equal>
								</logic:notEqual>
							</logic:notEqual>
						
			</tr>
		</logic:iterate>
		<tr id="trnameurl">
			<td width="130" class="RecordRow" align="right" valign="bottom" style="border-top:0px;border-right:0px;">
				&nbsp;<bean:message key="lable.channel_detail.out_url" />&nbsp;
			</td>
			<td class="RecordRow" style="padding-left:3px;"style="border-top:0px;" align="left">
				<input type="text" name='url' Class="textColorWrite" style="width: 365px;" value="${coursewareForm.url }"/>
			</td>
		</tr>
	</table>

<table id="table1" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top: 5px;line-height: 30px;">
	<tr>
			<td align="center">
				<input type="button" class="mybutton"
					value="<bean:message key="button.save" />"
					onclick="savecourseware0();" id="meidasave"/>
				<!-- <html:reset styleClass="mybutton" property="reset">
					<bean:message key="button.clear" />
				</html:reset>
				<input type="reset" class="mybutton" value='<bean:message key="button.clear" />' id="resetbutton" /> -->
				<input type="button" class="mybutton" 
					value="<bean:message key="button.return" />"
					onclick="returnfromaddcourseware();" id="returnbutton" />
			</td>
		</tr>
</table>

	<div id="tableEdit"  width="800px" style="display:none;margin-top: 5px;" align="center">
		<script type="text/javascript">
	     var CKEditor = Ext.create('EHR.ckEditor.CKEditor',{
		  	 id:'ckeditorid',
		     functionType:"standard",         
		     width:'100%',
		 	 height:'100%'      
	      });  
	   	
	   	 var Panel = Ext.create('Ext.panel.Panel', {
			 id:'ckeditorPanel',			 
	         hidden :true,
	         width: 680,
	     	 height: 455, 
			 items: [CKEditor],	 			  
			 renderTo: "tableEdit"
		});
            </script>
	</div>
	
	<div id="buttonEdit" >
	<INPUT type="button" value="<bean:message key="button.ok" />"
			Class="mybutton"  id="define"  style="margin-top: 5px;display:none" onclick="showView(false)">
		<INPUT type="button" value="<bean:message key="button.return" />"
			Class="mybutton"  id="return"  style="margin-top: 5px;display:none" onclick="closedView(false)">
	</div>
	
	</center>
</html:form>
<div id='wait' style='position:absolute;top:180px;left:300px;display: none;'>
<table border="1" width="430px" cellspacing="0" cellpadding="4" class="table_style" height="100" align="center">
           <tr>

             <td class="td_style" height="40px">正在保存课件...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;" class="complex_border_color" align=center>
               <marquee class="marquee_style" direction="right" width="430px" scrollamount="7" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height='8px'>
                     <td bgcolor=#3399FF width='8px'></td>
                         <td></td>
                         <td bgcolor=#3399FF width='8px'></td>
                         <td></td>
                         <td bgcolor=#3399FF width='8px'></td>
                         <td></td>
                         <td bgcolor=#3399FF width='8px'></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
</table>
</div>
<script>
function loadobj(type){
	
	var obj = new Object();
	// 显示文件名的输入框的样式默认为class='textColorRead'定义的样式
  	obj.inputStyle = "width:300px;";
  	// “选择文件”按钮的样式，默认样式为class='mybutton'定义的样式
  	obj.buttonStyle = "";
  	// 单个文件的最大值，默认为500,
  	<%String fileSize = MediaServerParamBo.getFileSize();
  	if (fileSize == null || fileSize.length() <= 0) {%>
  		obj.fileMaxSize = "500";
  	<%} else {%>
  		obj.fileMaxSize = "<%=fileSize%>";
  	<%}%>
  	//单个上传文件的单位，默认为MB,还可以使用GB、KB
  	obj.fileSizeUnit = "MB";
  	// 文件的扩展名，限定上传文件的类型,默认是任意类型（*.*）,多个文件类型用分号隔开，例如*.jpg;*.jpeg;*.gif
  	if(type==3){
	  	<%String mediaServerType = MediaServerParamBo.getMediaServerType();
	  	
	  	if ("red5".equalsIgnoreCase(mediaServerType)) {%>
	  		obj.fileExt = "*.mp3;*.mp4;*.flv;*.f4v";
	  	<%} else if ("microsoft".equalsIgnoreCase(mediaServerType)) {%>
	  		obj.fileExt = "*.asf;*.wma;*.wmv";
	  	<%} else if ("HTTP".equalsIgnoreCase(mediaServerType)) {%>
	  		obj.fileExt = "*.mp4";
	  	<%} else {%>
	  		obj.fileExt = "*.mp3;*.flv;*.f4v;*.mp4";
	  	<%}%>
	  	
	  	// 文件类型的描述，默认为“文件类型”
		obj.file_types_desc = "多媒体文件";
  	}else if (type == 1){
  		obj.fileExt = "*.doc;*.docx;*.xls;*.xlsx;*.pdf;*.ppt;*.pptx;*.zip";
  		
  		// 文件类型的描述，默认为“文件类型”
		obj.file_types_desc = "普通文件";
  	} else if (type == 4) {
  		obj.fileExt = "*.zip";
  		
  		// 文件类型的描述，默认为“文件类型”
		obj.file_types_desc = "SCORM标准课件";
  	}
  	// 需要传入的其他参数
  	obj.post_params = {
        "lessonid" : "${coursewareForm.r5000}",
		// 61代表上传培训的多媒体文件
		"keyCode" : "61",
		"fileName" : getEncodeStr(document.getElementById("r5103name").value),
		"fileType" : type
	};
	
	// 最多上传的文件个数，默认为100
	obj.file_upload_limit=100;
	obj.file_queue_limit = 1;
	// 上传开始后，哪些按钮需要禁用，将按钮的id列出，多个用逗号隔开
	obj.forbiddenButton = "meidasave,returnbutton";
	// 上传时哪些组件的值不能为空，将组件的id列出，多个用逗号隔开
	obj.isNotNullIds = "r5103name";
	obj.isNotNullDesc = "课件名称不能为空！";
  	swfuploadsingle("divupload","path","/train/media/upload","61",obj);
}
//linbz 20170418 6813 首次进培训课件时把newPath初始化
document.getElementById("newPathId").value = "";
</script>
</body>
