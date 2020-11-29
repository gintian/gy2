<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm,
				com.hrms.hjsj.sys.FieldItem,
				org.apache.commons.beanutils.LazyDynaBean,
				java.util.ArrayList" %>
<%
LawBaseForm lawForm=(LawBaseForm)session.getAttribute("lawbaseForm");
ArrayList lawBaseFileList = lawForm.getLawBaseFileList();
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<%

	String isInsert =request.getParameter("isInsert");
	isInsert=isInsert==null?"0":isInsert;
	String file_id = request.getParameter("file_id");
	file_id=file_id==null?"":file_id;
 %>
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
<SCRIPT LANGUAGE="javascript">
   var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel'); 

     }

   }
   
   function showDateSelectBox(srcobj)
   {
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');   
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
        style.position="absolute";
		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+date_desc.offsetHeight;
		style.width="150px";
      }                 
   }
   function IsDigital() 
	{
		return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
	}
	function IsDigital2() 
	{
		//只能输入数字，不能输入小数点
		return ((event.keyCode >= 48) && (event.keyCode <= 57)); 
	}
    function getArguments(up_base)
    {
    	var up_node,base_id,val;
    	var paraArray=dialogArguments;
    	up_node = paraArray[0];
    	if(up_node==null)
    	   return;
    	base_id=up_node.uid;
    	val=MM_findObj_(up_base);
    	if(val==null)
    	  return;
    	val.value=base_id;
    	
    }	
</SCRIPT>
<script language="JavaScript">
 
function isfiletypeCheck(xname){
	var InString=xname.value;
	if(!InString || typeof(InString) == "undefined" || InString.length < 1){
		xname.outerHTML = xname.outerHTML;
        return true;
	}
    
	var fileType="";
	for (Count=InString.length-1; Count >= 0; Count--) 
	{
		TempChar= InString.substring (Count-1, Count);
		if(TempChar=="."){
			fileType=InString.substring (Count,InString.length);
			break;
		}
	}
	if(fileType=="DOCX" ||fileType=="docx"||fileType=="xlsx"||fileType=="XLSX"||fileType=="doc" || fileType=="DOC" || fileType=="xls" || fileType=="XLS" || fileType=="PDF"||fileType=="pdf"||fileType=="txt" || fileType=="html" || fileType=="txt" || fileType=="TXT"||fileType=="htm"||fileType=="html")
	{
		return true;
	}else{
		xname.outerHTML = xname.outerHTML;
		return false;
	}
}
              
function isfiletype(xname){
	var InString=xname.value;
	var TempChar;
	var fileType="";
		    
	if(xname.value=="")
	{
		
 		
 		
 		//实施日期的验证变量
 			var seyear=document.forms[0].elements[10].value;
 			var semonth=document.forms[0].elements[11].value;
 			var seday=document.forms[0].elements[12].value;
 		        var datetp1=seyear+'-'+semonth+'-'+seday;
 		 		
 		//失效日期的验证变量
 			var thyear=document.forms[0].elements[13].value;
 			var thmonth=document.forms[0].elements[14].value;
 			var thday=document.forms[0].elements[15].value;
 			var datetp=thyear+'-'+thmonth+'-'+thday;
 			
 			
 			if(seyear=='' && semonth=='' && seday==''&& thyear=='' && thmonth=='' && thday=='')
 			{
 				if(confirm('确认提交吗?'))
				{
					return true;
				}
				else
				{
		  	 		return false;
				}
 			}
 			else if((seyear=='' && semonth=='' && seday=='') && !(thyear=='' && thmonth=='' && thday==''))
 			{
 				if(!isdatestring (datetp,'失效日期'))
 				{
 					return false;
 				}
 			}
 			else if(!(seyear=='' && semonth=='' && seday=='') && (thyear=='' && thmonth=='' && thday==''))
 			{
 				if(!isdatestring (datetp1,'实施日期'))
 				{
 					return false;
 				} 
 			}
 			else 
 			{	//实施日期为真，失效日期为假 			
 				if(checkdatestring(datetp1) && !checkdatestring(datetp))
 				{
 					alert("您输入的[失效日期]不正确!");
 					return false;
 				}
 				else if(!checkdatestring(datetp1) && checkdatestring(datetp))
 				{
 					alert("您输入的[实施日期]不正确!");
 					return false;
 				}
 				else if(!checkdatestring(datetp1) && !checkdatestring(datetp))
 				{
 					alert("您输入的[实施日期]不正确!");
 					return false;
 				}
 				else
 				{
 					if(confirm('确认提交吗?'))
					{
					return true;
					}
					else
					{
		  	 		return false;
					}	
 				}
 				
 			}
 	 		
		
	}
	else
	{
		for (Count=InString.length-1; Count >= 0; Count--) 
		{
			TempChar= InString.substring (Count-1, Count);
			if(TempChar=="."){
				fileType=InString.substring (Count,InString.length);
				break;
			}
		}
		
		if(fileType=="doc" || fileType=="DOC" || fileType=="xls" || fileType=="XLS" || fileType=="PDF"||fileType=="pdf"||fileType=="txt" || fileType=="html" || fileType=="txt" || fileType=="TXT"||fileType=="htm"||fileType=="html")
		{
					
 		
 			//实施日期的验证变量
 			var seyear=document.forms[0].elements[10].value;
 			var semonth=document.forms[0].elements[11].value;
 			var seday=document.forms[0].elements[12].value;
 		        var datetp1=seyear+'-'+semonth+'-'+seday;
 		 		
 			//失效日期的验证变量
 			var thyear=document.forms[0].elements[13].value;
 			var thmonth=document.forms[0].elements[14].value;
 			var thday=document.forms[0].elements[15].value;
 			var datetp=thyear+'-'+thmonth+'-'+thday;
 			
 			
 			if(seyear=='' && semonth=='' && seday==''&& thyear=='' && thmonth=='' && thday=='')
 			{
 				if(confirm('确认提交吗?'))
				{
					return true;
				}
				else
				{
		  	 		return false;
				}
 			}
 			else if((seyear=='' && semonth=='' && seday=='') && !(thyear=='' && thmonth=='' && thday==''))
 			{
 				if(!isdatestring (datetp,'失效日期'))
 				{
 					return false;
 				}
 			}
 			else if(!(seyear=='' && semonth=='' && seday=='') && (thyear=='' && thmonth=='' && thday==''))
 			{
 				if(!isdatestring (datetp1,'实施日期'))
 				{
 					return false;
 				} 
 			}
 			else 
 			{	//实施日期为真，失效日期为假
 				if(checkdatestring(datetp1) && !checkdatestring(datetp))
 				{
 					alert("您输入的[失效日期]不正确!");
 					return false;
 				}
 				else if(!checkdatestring(datetp1) && checkdatestring(datetp))
 				{
 					alert("您输入的[实施日期]不正确!");
 					return false;
 				}
 				else if(!checkdatestring(datetp1) && !checkdatestring(datetp))
 				{
 					alert("您输入的[实施日期]不正确!");
 					return false;
 				}
 				else
 				{
 					if(confirm('确认提交吗?'))
					{
					return true;
					}
					else
					{
		  	 		return false;
					}	
 				}
 				
 			}
 	 		
		
		}
		else
		{
			alert("请选择文本类型的文件！");
			xname.select();
			return false;
		}
	}
	
}

//判断是否为日期

function isdatestring (xname,memo)
{
	        var flage=0;
	        var strSeparator = "-"; //日期分隔符
		var strDateArray;
		var intYear;
		var intMonth;
		var intDay;
		var boolLeapYear;
		strDateArray = xname.split(strSeparator);
		if(strDateArray.length!=3){ flage=1; }
		if(isNaN(strDateArray[0])||isNaN(strDateArray[1])||isNaN(strDateArray[2]))
		{ flage=1; }
		intYear = parseInt(strDateArray[0],10);
		intMonth = parseInt(strDateArray[1],10);
		intDay = parseInt(strDateArray[2],10);
		
		if(isNaN(intYear)||isNaN(intMonth)||isNaN(intDay))
		{ flage=1; }
		if(intMonth>12||intMonth<1){ flage=1; }
		if((intMonth==1||intMonth==3||intMonth==5||intMonth==7||intMonth==8||intMonth==10||intMonth==12)&&(intDay>31||intDay<1)) {flage=1; }
		if((intMonth==4||intMonth==6||intMonth==9||intMonth==11)&&(intDay>30||intDay<1)){ flage=1; }
		if(intMonth==2)
		{
			if(intDay<1) {flage=1;}
			boolLeapYear = false;
			if((intYear%100)==0){
				if((intYear%400)==0) boolLeapYear = true;
			}
			else{
				if((intYear%4)==0) boolLeapYear = true;
			}
			if(boolLeapYear)
			{
				if(intDay>29){flage=1;}
			}
			else
			{
				if(intDay>28){flage=1;}
			}
		}
		
	if (flage==1)
	{
	  alert("您输入的["+memo+"]不正确!");
	  return false;
	}
	else
	{
		if(confirm('确认提交吗?'))
		{
		return true;
		}
		else
		{
		   return false;
		}
	}
	
}


function checkdatestring (xname)
{
	        var flage=0;
	        var strSeparator = "-"; //日期分隔符
		var strDateArray;
		var intYear;
		var intMonth;
		var intDay;
		var boolLeapYear;
		strDateArray = xname.split(strSeparator);
		if(strDateArray.length!=3){ flage=1; }
		if(isNaN(strDateArray[0])||isNaN(strDateArray[1])||isNaN(strDateArray[2]))
		{ flage=1; }
		intYear = parseInt(strDateArray[0],10);
		intMonth = parseInt(strDateArray[1],10);
		intDay = parseInt(strDateArray[2],10);
		if(intYear=='' && intMonth=='' && intDay=='')
		{
		flage=1;
		}
		if(isNaN(intYear)||isNaN(intMonth)||isNaN(intDay))
		{ flage=1; }
		if(intMonth>12||intMonth<1){ flage=1; }
		if((intMonth==1||intMonth==3||intMonth==5||intMonth==7||intMonth==8||intMonth==10||intMonth==12)&&(intDay>31||intDay<1)) {flage=1; }
		if((intMonth==4||intMonth==6||intMonth==9||intMonth==11)&&(intDay>30||intDay<1)){ flage=1; }
		if(intMonth==2)
		{
			if(intDay<1) {flage=1;}
			boolLeapYear = false;
			if((intYear%100)==0){
				if((intYear%400)==0) boolLeapYear = true;
			}
			else{
				if((intYear%4)==0) boolLeapYear = true;
			}
			if(boolLeapYear)
			{
				if(intDay>29){flage=1;}
			}
			else
			{
				if(intDay>28){flage=1;}
			}
		}
		
	if (flage==1)
	{
	
	  return false;
	}
	else
	{
		return true;
			
	}
	
}
     
	function showView(flg) {
		//2016/1/5 wangjl 缺陷 15714 修改id="table"
		var aa = document.getElementById("tableEdit");
		var bb= document.getElementById("content");
		var oEditor;		
		if (flg == true) {
	    	oEditor = FCKeditorAPI.GetInstance('FCKeditor1'); 
    	    if (oEditor.EditorDocument == null) {
	        	alert("编辑器正在加载中！请重新点击编辑概要按钮！");
	    	    return;
	    	}
    	    aa.style.display = 'block';
	        bb.style.display = "none"; 
    	    oEditor.EditorDocument.body.innerHTML = lawbaseForm.digest.value; 
	    } else {
	        oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
	        if (oEditor.EditorDocument == null) {
	        	alert("编辑器正在加载中！请重新点击编辑概要按钮！");
	    	    return;
	    	}
	        aa.style.display = 'none';
	        bb.style.display = 'block';      
 	        lawbaseForm.digest.value = oEditor.GetXHTML(true);
 	        
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
    function remove()
    {
    	Element.hide('date_panel');
    }
    function openOrgTreeDialog()
    {
    	var thecodeurl="/selfservice/lawbase/law_into_base.do?b_orgtree=link"; 
        var oldobj=lawbaseForm.b0110;
        var hiddenobj=lawbaseForm.transfercodeitemid;
            var theArr=new Array(oldobj,hiddenobj); 
        var popwin= window.showModalDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    }
    function   ValidateValue(textbox) 
                { 
                          var   IllegalString ="\`~!#$%^&*()+{}|\\:\" <> ?-=/,\'"; 
                          var   textboxvalue=   textbox.value; 

                          var   index= textboxvalue.length-1; 
                          
                          var   s   =   textbox.value.charAt(index); 
                          
                          if(IllegalString.indexOf(s)>=0) 
                          { 
                                s   =   textboxvalue.substring(0,index); 
                                textbox.value   =   s; 
                          } 
                }
    function checkword(e,textbox){
		var e = window.event ? window.event : e;
		var   textboxvalue=   textbox.value; 
    if(e.keyCode==34||e.keyCode==47||e.keyCode==92||e.keyCode==96||e.keyCode==59||e.keyCode==126||e.keyCode==38||e.keyCode==123||e.keyCode==124||e.keyCode==125)
    	textbox.value =textboxvalue;
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
			lawbaseForm.action = parent.frames["mil_menu"].Global.selectedItem.action;
			lawbaseForm.submit();
		}
    }
    
    function validatefilepath(){
    	<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content,">           
		 	<logic:match name="lawbaseForm" property="field_str_item" value="ext`">           
		 	var mediapath=document.lawbaseForm.file.value;
		 	if(mediapath.length>2){
			 	document.returnValue=validateUploadFilePath(mediapath);
		 	}
		 	</logic:match>
		</logic:notMatch>
		<logic:equal name="lawbaseForm" property="basetype" value="5">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">           
			<logic:match name="lawbaseForm" property="field_str_item" value="originalext`"> 
			var mediapath=document.lawbaseForm.manuscript.value;
			if(mediapath.length>2){
		 		document.returnValue=validateUploadFilePath(mediapath);
		 	}
		 	</logic:match>
		</logic:notMatch>
		</logic:equal>
			
	}
	
function submitok(){
	var ttitle=document.getElementById("t_title")?document.getElementById("t_title").value:'标题';
	var t_issue_date=document.getElementById('t_issue_date')?document.getElementById('t_issue_date').value:'颁布日期';
	var t_implement_date=document.getElementById('t_implement_date')?document.getElementById('t_implement_date').value:'实施日期';
	var t_valid_date=document.getElementById('t_valid_date')?document.getElementById('t_valid_date').value:'失效日期';
	var flag=validate( 'R','lawFileVo.string(title)',ttitle,'RD','first_date.',t_issue_date, 'D', 'second_date.',t_implement_date,'D','third_date.', t_valid_date);
	if(!flag)
		return false;
	if(document.getElementsByName('file')[0]){
		if(!isfiletypeCheck(document.getElementsByName('file')[0])){
			alert(FILE_TYPE_ERROR);
			return false;
		}
	}
	if(document.getElementById('content_type')){
		if (document.getElementById('content_type').value.length>5) {
			alert(CONTENT_TYPE_LENGTH);
			return false;
		}
	}	
	if(document.getElementsByName('manuscript')[0]){
		if(!isfiletypeCheck(document.getElementsByName('manuscript')[0])){
			alert(FILE_TYPE_ERROR_YJ);
			return false;
		}
		
	}
	<%
		StringBuffer fills= new StringBuffer();
		for(int i=0;i<lawBaseFileList.size();i++){
			LazyDynaBean bean = (LazyDynaBean)lawBaseFileList.get(i);
			String inputtype = (String)bean.get("inputtype");
			String itemdesc = (String)bean.get("itemdesc");
			if("true".equalsIgnoreCase(inputtype)){
				fills.append("lawBaseFileList["+i+"].value:"+itemdesc+"`");
			}
		}
	%>
	var fills = "<%=fills.toString() %>";
	//alert(fills);
	var names = fills.split("`");
	for(var i=0;i<names.length;i++){
		var name=names[i];
		if(name.length>0){
			var tmp = name.split(":");
			if(tmp.length==2){
				var _element=document.getElementsByName(tmp[0]);
				if(_element&&_element[0]){
					var _value=(_element[0]).value;
					if(_value.length<=0){
						alert(tmp[1]+"不能为空");
						flag=false;
						break;
					}
				}
			}
		}
	}
	if(!flag)
		return false;
	validatefilepath();
	if(document.returnValue){
		lawbaseForm.action = "/selfservice/lawbase/law_into_base.do?b_save=link&isInsert=<%=isInsert %>&file_id=<%=file_id %>";
		lawbaseForm.submit();
		document.getElementById("wait").style.display="block";
		document.getElementById('buttonok').disabled=true;
		document.getElementById('buttonReset').disabled=true;
		if (document.getElementById('buttonShow')) {
			document.getElementById('buttonShow').disabled=true;
		}
		document.getElementById('bottonReturn').disabled=true;
	}
}
function checkValue(flag,len,decWidth,obj){
	if(flag==1){
		var a=obj.value.length;
		if(a>len){
			alert("长度最多为"+len+"位!");
			return false;
		}	
	}else if(flag==2){
		var a=obj.value;
		if(a.indexOf(".")!=-1){
			var integerPart=a.substring(0,a.indexOf("."));
			var decPart=a.substring(a.indexOf(".")+1,a.length);
			if(integerPart.length>len){
				alert("整数部分最多为"+len+"位!");
				return false;
			}
			if(decPart.length>decWidth){
				alert("小数部分最多为"+decWidth+"位!");
				return false;
			}
		}else{
			if(a.length>len){
				alert("整数部分最多为"+len+"位!");
				return false;
			}
		}
	}
}

</script>
<style>
.text4{
	width: 200px;
}
</style>
<base id="mybase" target="_self">

<html:form action="/selfservice/lawbase/law_into_base"  enctype="multipart/form-data">

	<table id="content" class="DetailTable" width="400" cellpadding="0" cellspacing="0" align="center" style="margin-top: 50px;">
		<tr height="20">
			<td align="left" colspan="4" class="TableRow">
						<logic:equal name="lawbaseForm" property="basetype" value="4">
				知识中心维护
				</logic:equal>
				<logic:equal name="lawbaseForm" property="basetype" value="1">
				   <bean:message key="label.lawfile.repair" />
				</logic:equal>
				<logic:equal name="lawbaseForm" property="basetype" value="5">
				文件档案维护
				</logic:equal>
			</td>
		</tr>

<%if(lawBaseFileList.size()==0){ %>
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr class="list3">
						<td nowrap width="110" align="right">
							<bean:message key="lable.lawfile.title" />&nbsp;
						</td>
						<td align="left" nowrap>
							<html:text name="lawbaseForm" styleClass="text4" property="lawFileVo.string(title)" maxlength="35" size="35" onkeyup="ValidateValue(this)" onkeypress="event.returnValue=IsDigit();checkword(event,this);"/>
						</td>
					</tr>

				</table>
			</td>
		</tr>

		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",type,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.typenum" />&nbsp;
						</td>
						<td>
							<html:text name="lawbaseForm" styleClass="text4" maxlength="30" property="lawFileVo.string(type)" onkeypress="event.returnValue=IsDigit();"/>
						</td>
					</tr>

				</table>
			</td>
		</tr>
		</logic:notMatch>
		</logic:notEqual>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content_type,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.contenttype" />&nbsp;
						</td>
						<td>
							<html:text name="lawbaseForm" styleClass="text4" maxlength="10" styleId="content_type" property="lawFileVo.string(content_type)"  onkeypress="event.returnValue=IsDigit();" ondblclick="showDateSelectBox('content_type');" onkeydown="if (event.keyCode==13)  remove();" />
						</td>
						<TD>
							&nbsp;&nbsp;<img  src="/images/code.gif" onclick="showDateSelectBox('content_type');" style="vertical-align:middle;">
						</TD>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.valid" />&nbsp;
						</td>
						<td>
							<html:select name="lawbaseForm"  styleClass="text4" property="lawFileVo.string(valid)">
								<html:option value="1">
									<bean:message key="lable.lawfile.availability" />
								</html:option>
								<html:option value="0">
									<bean:message key="lable.lawfile.allabolish" />
								</html:option>
								<html:option value="2">
									<bean:message key="lable.lawfile.partabolish" />
								</html:option>
								<html:option value="3">
									<bean:message key="lable.lawfile.editing" />
								</html:option>
								<html:option value="4">
									<bean:message key="lable.lawfile.other" />
								</html:option>
							</html:select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		</logic:notEqual>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",note_num,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.notenum" />&nbsp;
						</td>
						<td>
							<html:text name="lawbaseForm" maxlength="30" styleClass="text4" property="lawFileVo.string(note_num)" onkeypress="event.returnValue=IsDigit();"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		<logic:equal name="lawbaseForm" property="basetype" value="5">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",b0110,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.ascriptionunit" />&nbsp;
						</td>
						<td>
							<html:hidden name="lawbaseForm" property="transfercodeitemid"/> 
							<html:text styleId="b0110"  styleClass="text4" name="lawbaseForm" property="lawFileVo.string(b0110)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>
						</td>
						<TD>
							&nbsp;&nbsp;<img  src="/images/code.gif" onclick='javascript:openOrgTreeDialog();' style="vertical-align: middle;"/>&nbsp;
						</TD>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		</logic:equal>
		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_org,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.issue_org" />&nbsp;
						</td>
						<td>
							<html:text name="lawbaseForm" maxlength="100" styleClass="text4" property="lawFileVo.string(issue_org)" onkeypress="event.returnValue=IsDigit();"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",notes,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.note" />&nbsp;
						</td>
						<td>
							<html:text name="lawbaseForm" maxlength="100" styleClass="text4" property="lawFileVo.string(notes)" onkeypress="event.returnValue=IsDigit();"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		</logic:notEqual>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",issue_date,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">

				<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.printmandate" />&nbsp;
						</td>
						<td>
							<html:text property="first_date.year" size="4" maxlength="4" styleClass="text"/>
							<bean:message key="datestyle.year" />
							<html:text property="first_date.month" size="4" maxlength="2" styleClass="text"/>
							<bean:message key="datestyle.month" />
							<html:text property="first_date.date" size="4" maxlength="2" styleClass="text"/>
							<bean:message key="datestyle.day" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		<logic:notEqual name="lawbaseForm" property="basetype" value="5">
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",implement_date,">           
			<tr>
				<td colspan="4" class="RecordRow" style="border-top: none;"">
					<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
						<tr>
							<td width="110" align="right">
								<bean:message key="lable.lawfile.actualizedate" />&nbsp;
							</td>
							<td>
								<html:text property="second_date.year" size="4" maxlength="4" styleClass="text"/>
								<bean:message key="datestyle.year" />
								<html:text property="second_date.month" size="4" maxlength="2" styleClass="text"/>
								<bean:message key="datestyle.month" />
								<html:text property="second_date.date" size="4" maxlength="2" styleClass="text"/>
								<bean:message key="datestyle.day" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</logic:notMatch>
			<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",valid_date,">           
			<tr>
				<td colspan="4" class="RecordRow" style="border-top: none;"">
					<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
						<tr>
							<td width="110" align="right">
								<bean:message key="lable.lawfile.invalidationdate" />&nbsp;
							</td>
							<td>
								<html:text property="third_date.year" size="4" maxlength="4" styleClass="text"/>
								<bean:message key="datestyle.year" />
								<html:text property="third_date.month" size="4" maxlength="2" styleClass="text"/>
								<bean:message key="datestyle.month" />
								<html:text property="third_date.date" size="4" maxlength="2" styleClass="text"/>
								<bean:message key="datestyle.day" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</logic:notMatch>
		</logic:notEqual>
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",name,">           
			<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="column.law_base.filename" />&nbsp;
						</td>
						<td>
							<html:text name="lawbaseForm" styleClass="text4" property="lawFileVo.string(name)" size="35" maxlength="49" onkeypress="event.returnValue=IsDigit();"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",content,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.upfile" />&nbsp;
						</td>
						<td>

							<html:file name="lawbaseForm"  styleClass="text4" property="file" accept="doc,txt,xls,pdf"/>
							<logic:notEqual name="lawbaseForm" property="lawFileVo.string(ext)" value="">
								<td style="padding-top: 3px;">
									<IMG src="/images/lawdelete.gif" title="删除文件" onclick="delete_file('ext','<bean:write name="lawbaseForm" property="lawFileVo.string(file_id)" filter="true" />');" />
								</td>
							</logic:notEqual>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		<logic:equal name="lawbaseForm" property="basetype" value="5">
		<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",originalfile,">           
		<tr>
			<td colspan="4" class="RecordRow" style="border-top: none;"">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td width="110" align="right">
							<bean:message key="lable.lawfile.upmanuscript" />&nbsp;
						</td>
						<td>

							<html:file name="lawbaseForm" styleClass="text4" property="manuscript" accept="doc,txt,xls,pdf"/>
							<logic:notEqual name="lawbaseForm" property="lawFileVo.string(originalext)" value="">
								<td style="padding-top: 3px;">
									<IMG src="/images/lawdelete.gif" title="删除原件" onclick="delete_file('orgext','<bean:write name="lawbaseForm" property="lawFileVo.string(file_id)" filter="true" />');"/>
								</td>
							</logic:notEqual>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		</logic:notMatch>
		</logic:equal>
<%
}else{
	for(int i=0;i<lawBaseFileList.size();i++){
	LazyDynaBean bean = (LazyDynaBean)lawBaseFileList.get(i);
	String itemid = (String)bean.get("itemid");
	if(itemid.equalsIgnoreCase("digest"))
		continue;
	String itemdesc = (String)bean.get("itemdesc");
	String itemtype = (String)bean.get("itemtype");
	int decWidth=Integer.parseInt((String)bean.get("decWidth"));
	int len = Integer.parseInt((String)bean.get("len"));
	String codesetid = (String)bean.get("codesetid");
	String value = (String)bean.get("value");
	String viewvalue = (String)bean.get("viewvalue");
	
	if(itemid.equalsIgnoreCase("ext")){
		itemdesc="上传"+itemdesc;
	}
	if(itemid.equalsIgnoreCase("originalext")){
		itemdesc="上传"+itemdesc;
	}
%>
<tr>
	<td class="framestyle3 RecordRow">
	<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
	<tr>
		<td width='80' align="right" nowrap>
		<%=itemdesc %>&nbsp;
		</td>
		<td >
		
<%
		if(itemid.equalsIgnoreCase("title")){
%>			
			<input type="hidden" id="t_title" value="<%=itemdesc %>">
			<html:text name="lawbaseForm" property="lawFileVo.string(title)" styleClass="text4" size="35" onkeyup="ValidateValue(this)" onkeypress="event.returnValue=IsDigit();checkword(event,this);"/>
<% 
		}else if(itemid.equalsIgnoreCase("type")){
%>			
			<html:text name="lawbaseForm" property="lawFileVo.string(type)" styleClass="text4" onkeypress="event.returnValue=IsDigit();"/>
<% 
		}else if(itemid.equalsIgnoreCase("content_type")){
%>
			<html:text name="lawbaseForm" styleId="content_type" property="lawFileVo.string(content_type)" styleClass="text4" onkeypress="event.returnValue=IsDigit();" ondblclick="showDateSelectBox('content_type');" onkeydown="if (event.keyCode==13)  remove();" />
			</td>
			<td>
			&nbsp;<img  src="/images/code.gif" onclick="showDateSelectBox('content_type');" style="vertical-align:middle;">
<% 
		}else if(itemid.equalsIgnoreCase("valid")){
%>
			<html:select name="lawbaseForm" property="lawFileVo.string(valid)">
								<html:option value="1">
									<bean:message key="lable.lawfile.availability" />
								</html:option>
								<html:option value="0">
									<bean:message key="lable.lawfile.allabolish" />
								</html:option>
								<html:option value="2">
									<bean:message key="lable.lawfile.partabolish" />
								</html:option>
								<html:option value="3">
									<bean:message key="lable.lawfile.editing" />
								</html:option>
								<html:option value="4">
									<bean:message key="lable.lawfile.other" />
								</html:option>
							</html:select>
<%
		}else if(itemid.equalsIgnoreCase("note_num")){
%>
			<html:text name="lawbaseForm" property="lawFileVo.string(note_num)" styleClass="text4" onkeypress="event.returnValue=IsDigit();"/>
<% 
		}else if(itemid.equalsIgnoreCase("b0110")){
%>
			<html:hidden name="lawbaseForm" property="transfercodeitemid"/> 
			<html:text styleId="b0110" name="lawbaseForm" property="lawFileVo.string(b0110)" styleClass="text4" onkeypress="event.returnValue=IsDigit();" readonly="true"/>
			</td>
			<td>
			&nbsp;<img  src="/images/code.gif" onclick='javascript:openOrgTreeDialog();' style="vertical-align: middle;"/>&nbsp;
<% 
		}else if(itemid.equalsIgnoreCase("issue_org")){
%>
			<html:text name="lawbaseForm" property="lawFileVo.string(issue_org)" styleClass="text4" onkeypress="event.returnValue=IsDigit();"/>
<% 
		}else if(itemid.equalsIgnoreCase("notes")){
%>
			<html:text name="lawbaseForm" property="lawFileVo.string(notes)" styleClass="text4" onkeypress="event.returnValue=IsDigit();"/>
<% 
		}else if(itemid.equalsIgnoreCase("issue_date")){
%>
							<input type="hidden" id="t_issue_date" value="<%=itemdesc %>">
							<html:text property="first_date.year" size="4" maxlength="4" styleClass="text" />
							<bean:message key="datestyle.year" />
							<html:text property="first_date.month" size="4" maxlength="2" styleClass="text" />
							<bean:message key="datestyle.month" />
							<html:text property="first_date.date" size="4" maxlength="2" styleClass="text" />
							<bean:message key="datestyle.day" />
<% 
		}else if(itemid.equalsIgnoreCase("implement_date")){
%>
							<input type="hidden" id="t_implement_date" value="<%=itemdesc %>">
							<html:text property="second_date.year" size="4" maxlength="4" styleClass="text" />
							<bean:message key="datestyle.year" />
							<html:text property="second_date.month" size="4" maxlength="2" styleClass="text" />
							<bean:message key="datestyle.month" />
							<html:text property="second_date.date" size="4" maxlength="2" styleClass="text" />
							<bean:message key="datestyle.day" />
<% 
		}else if(itemid.equalsIgnoreCase("valid_date")){
%>
							<input type="hidden" id="t_valid_date" value="<%=itemdesc %>">
							<html:text property="third_date.year" size="4" maxlength="4" styleClass="text" />
							<bean:message key="datestyle.year" />
							<html:text property="third_date.month" size="4" maxlength="2" styleClass="text" />
							<bean:message key="datestyle.month" />
							<html:text property="third_date.date" size="4" maxlength="2" styleClass="text" />
							<bean:message key="datestyle.day" />
<% 
		}else if(itemid.equalsIgnoreCase("name")){
%>
			<html:text name="lawbaseForm" property="lawFileVo.string(name)" styleClass="text4" size="35" maxlength="49" onkeypress="event.returnValue=IsDigit();"/>
<% 		
		}else if(itemid.equalsIgnoreCase("ext")){
%>	
		<html:file name="lawbaseForm" property="file" styleClass="text4" accept="doc,txt,xls,pdf"/>
		&nbsp;
		<logic:notEqual name="lawbaseForm" property="lawFileVo.string(ext)" value="">
			<td>
			<IMG src="/images/lawdelete.gif" title="删除文件" onclick="delete_file('ext','<bean:write name="lawbaseForm" property="lawFileVo.string(file_id)" filter="true" />');" />
			</td>
		</logic:notEqual>
<%
		}else if(itemid.equalsIgnoreCase("originalext")){
%>
		<html:file name="lawbaseForm" property="manuscript" styleClass="text4" accept="doc,txt,xls,pdf"/>
		&nbsp;
		<logic:notEqual name="lawbaseForm" property="lawFileVo.string(originalext)" value="">
		<td>
		<IMG src="/images/lawdelete.gif" title="删除原件" onclick="delete_file('orgext','<bean:write name="lawbaseForm" property="lawFileVo.string(file_id)" filter="true" />');"/>
		</td>
		</logic:notEqual>

<% 
		}else if(itemid.equalsIgnoreCase("viewcount")){
	
%>
		<html:text name="lawbaseForm" property="lawFileVo.string(viewcount)" styleClass="text4" onkeypress="event.returnValue=IsDigit();"/> 
<%
}else{
	if(itemtype.equalsIgnoreCase("A")){
		if(codesetid.equals("0")){
			//字符型
			out.println("<input type=\"text\"  ");	
			out.print(" name=\"lawBaseFileList["+i+"].value\"  size='30'   value=\""+value+"\"  />");
		}else{
			//代码型
			out.print("<input type='hidden' value='"+value+"'  name='lawBaseFileList["+i+"].value' value='"+value+"' />");
			out.print(" <input type='text' name='lawBaseFileList["+i+"].viewvalue' value='"+viewvalue+"' readonly='true' />");
			out.print("</td><td>");
         	out.print("<img src='/images/overview_obj.gif' border=0 width=20 height=20 onclick='openInputCodeDialog(\""+codesetid+"\",\"lawBaseFileList["+i+"].viewvalue\");'/>");
		    }
	}else if(itemtype.equalsIgnoreCase("D")){
		out.println("<input type='text'  size='20'  name='lawBaseFileList["+i+"].value'  value='"+value+"' ");
		out.print("  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",false,false)' ");	
		out.print("  />");
	}else if(itemtype.equalsIgnoreCase("M")){
		out.println("<textarea name=\"lawBaseFileList["+i+"].value\" rows='10'  wrap='ON' cols='60' class='textboxMul'>"+value+"</textarea>&nbsp;");
	}else if(itemtype.equalsIgnoreCase("N")){
		if(decWidth==0)
			out.println("<input type=\"text\" style=\"ime-mode:disabled\" onpaste=\"return false\" name=\"lawBaseFileList["+i+"].value\"   value='"+value+"' size='20' onkeypress='event.returnValue=IsDigital2();' onblur='checkValue(1,"+len+","+decWidth+",this);' />&nbsp;");
		else
			out.println("<input type=\"text\" style=\"ime-mode:disabled\" onpaste=\"return false\" name=\"lawBaseFileList["+i+"].value\"   value='"+value+"' size='20' onkeypress='event.returnValue=IsDigital();' onblur='checkValue(2,"+len+","+decWidth+",this);'/>&nbsp;");
	}
}
%>			
		</td>
		</tr>
		</table>
		</td>
		</tr>
<% 
	}
} 
%>
		<tr class="list3">
			<td align="center" colspan="4" style="padding-top: 5px;">			        
				<input id='buttonok' type='button' class='mybutton' onclick="submitok();" value="<bean:message key="button.ok" />" />
				<html:reset styleClass="mybutton" property="reset" styleId="buttonReset">
					<bean:message key="button.clear" />
				</html:reset>
				<logic:notEqual name="lawbaseForm" property="basetype" value="5">
					<logic:match name="lawbaseForm" property="field_str_item" value="digest">
						<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",digest,">           
						     <INPUT type="button" id="buttonShow" value="<bean:message key="button.edit" />" Class="mybutton" onclick="showView(true)">
						</logic:notMatch>
					</logic:match>
					<logic:empty name="lawbaseForm" property="field_str_item">
					<logic:notMatch name="lawbaseForm" property="viewhide${lawbaseForm.basetype}" value=",digest,">           
						     <INPUT type="button" id="buttonShow" value="<bean:message key="button.edit" />" Class="mybutton" onclick="showView(true)">
						</logic:notMatch>
					</logic:empty>
				</logic:notEqual>
				<input type="hidden" name="digest" value="<bean:write  name="lawbaseForm" property="digest" filter="true"/>">
				<input type="button" name="btnreturn" id="bottonReturn" value='返回' onclick="returnback();" class="mybutton">							
			</td>
		</tr>

	</table>
	 <div id="date_panel" style="display:none;" onmouseout="remove();">
	 	<html:select styleId ="date_box" name="lawbaseForm" multiple="multiple" style="width:155" size="6" property="lawFileVo.string(content_type)" onchange="setSelectValue();" onclick="setSelectValue();">    
			<html:optionsCollection property="contentlist" value="dataValue" label="dataName"/>	
		</html:select>
	 </div>

	<div id="tableEdit" style="display:none;">
		<script type="text/javascript">
              //<!--
              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
              oFCKeditor.BasePath	= '/fckeditor/';
              oFCKeditor.Height	= 430 ;
			  oFCKeditor.ToolbarSet='Apply';
              oFCKeditor.Create() ;
              //-->
            </script>
            <br>
		    <INPUT type="button" value="<bean:message key="button.ok" />" Class="mybutton" onclick="showView(false)">
	</div>
</html:form>
<div id='wait' style='position:absolute;top:180;left:300;display: none;'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style" height="40">正在保存...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;" class="complex_border_color" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
</table>
</div>
<script type="text/javascript">
<!--
function returnback(){
		window.location.href="/selfservice/lawbase/law_into_base.do?br_return=link";
	}

//-->
</script>
