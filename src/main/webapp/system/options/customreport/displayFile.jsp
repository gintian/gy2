<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.io.File"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.ResourceFactory"%>
<HTML>
<head>
<title></title>
<%	String filepath = request.getParameter("filepath"); 
	String filename = request.getParameter("filename");
	filename = PubFunc.decrypt(filename);
	filepath = PubFunc.decrypt(filepath);
	filepath = filepath.replace("\\","/");
	File file = new File(filepath);
	if(!file.exists()){
		out.println("<script language='javascript'>");
		out.println("alert('"+ResourceFactory.getProperty("bi.document.display.notexists")+"');");
		out.println("window.opener=null;window.open('','_self');window.close();");
		out.println("</script>");
		return;
	}

	filename = URLDecoder.decode(filename);
	int index = filename.lastIndexOf(".");
	String type = filename.substring(index+1);
	String fileType = "";
	if("doc".equalsIgnoreCase(type)||"docx".equalsIgnoreCase(type)||"dot".equalsIgnoreCase(type))
		fileType = "Word.Application";
	else if("xls".equalsIgnoreCase(type)||"xlsx".equalsIgnoreCase(type))
		fileType = "Excel.Application";
	else if("ppt".equalsIgnoreCase(type)||"pptx".equalsIgnoreCase(type))
		fileType = "PowerPoint.Application";
	else 
		fileType = "MSProject.Application";
	String recordId = filename.substring(0,index);
%>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">

<style>
</style>
</head>

<script id="clientEventHandlersJS" language="javascript">
//<!--
<%
String encryptFilePath = SafeCode.encode(PubFunc.encryption(filepath));
String encryptFileName = SafeCode.encode(filename);
%>
function download() {
	window.location.target="_self";
	window.location.href="/servlet/DisplayOleContent?fromflag=multimedia&openflag=true&filePath=<%=encryptFilePath%>&displayfilename=<%=encryptFileName%>";
}

function CreateWord() {
	document.all.OA1.CreateNew("Word.Application");	
}

function CreateExcel() {
	document.all.OA1.CreateNew("Excel.Application");		
}

function CreatePpt() {
	if(document.all.OA1.CreateNew("PowerPoint.Application") == false) {
		window.alert("Failed to create powerpoint instance. Please shut off others ms office instance in the task manager.");
	}
}

function OpenFromLocal() {
	document.all.OA1.OpenFileDialog();
	//You can call the Open method to open silently.
}

function OpenFromServer() {
	document.all.OA1.HttpInit();
	document.all.OA1.HttpAddpostString("DocumentID", "<%=filename %>");
	var url = "<%=SystemConfig.getPropertyValue("hrp_logon_url") %>";
	if(!url)
		url = "<%=request.getScheme() %>://<%=request.getServerName() %>:<%=request.getServerPort() %>";
	
	document.all.OA1.HttpOpenFileFromStream(url + '/servlet/DisplayFiles?filename=<%=URLEncoder.encode(filename) %>&filepath=<%=encryptFilePath%>',"<%=fileType%>");
	//if(document.all.OA1.IsOpened()){
		document.OA1.ShowMenubar(true); 
		document.OA1.Toolbars= true;
	//}
}

function CreateNew() {
	document.all.OA1.CreateNew();
}

function PrintDoc() {
	if(document.all.OA1.IsOpened()) {
		document.all.OA1.PrintDialog();
	}
}

function PrintPreview() {
	if(document.all.OA1.IsOpened()){
		document.all.OA1.PrintPreview();
	}
}

function ProtectDoc() {
	if(document.all.OA1.GetCurrentProgID() != "PowerPoint.Application")	{
		if(document.all.OA1.IsOpened()){
			if(document.all.OA1.GetCurrentProgID() == "Word.Application"){
				//wdAllowOnlyFormFields
				document.all.OA1.ProtectDoc(2);
			} else if(document.all.OA1.GetCurrentProgID() == "Excel.Application"){
				document.all.OA1.ProtectDoc(1);
			}
		}
	} else{
			document.all.OA1.SlideShowPlay(true);
	}
}


function UnProtectDoc() {
	if(document.all.OA1.GetCurrentProgID() != "PowerPoint.Application") {
		if(document.all.OA1.IsOpened()) {
			document.all.OA1.UnProtectDoc();
		}
	} else {
		document.all.OA1.SlideShowExit();
	}
}

function DocIsDirty() {
	if(document.all.OA1.IsOpened()){
		if(document.OA1.IsDirty) {
			window.alert("The file has been modified!");
		} else {
			window.alert("The file hasn't been modified!");
		} 
	}
}

function ShowHideToolbar() {
	if(document.all.OA1.GetCurrentProgID() != "PowerPoint.Application"){
		if(document.all.OA1.IsOpened()){
			var x = document.OA1.Toolbars; 
			document.OA1.Toolbars= ! x;
		}
	} else {
		window.alert("The method doesn't work for the MS PowerPoint!");
	}
}

function DisableRightClick() {
	if(document.all.OA1.GetCurrentProgID() != "PowerPoint.Application") {
		if(document.all.OA1.IsOpened()) {
			document.all.OA1.DisableSaveHotKey(true);
			document.all.OA1.DisablePrintHotKey(true);
			document.all.OA1.DisableCopyHotKey(true);
		}
	} else {
		window.alert("The method doesn't work for the MS PowerPoint, Project and Visio!");
	}
}

function SaveAs() {
	if(document.all.OA1.IsOpened()){
		document.all.OA1.SaveFileDialog();
		//You can call the SaveAs method to save silently.
	}
}

function SavetoServer() {
	if(document.OA1.IsOpened) {
		document.OA1.SetAppFocus();
		document.OA1.HttpInit();
		var today = new Date();
		var myGuid = (today.getMonth()+1).toString();
		myGuid += today.getDate().toString();
		myGuid += today.getYear().toString();
		myGuid += today.getHours().toString();
		myGuid += today.getMinutes().toString();
		myGuid += today.getSeconds().toString();
		var sFileName;
		if(document.all.OA1.GetCurrentProgID() == "Word.Application"){
			sFileName = myGuid + ".doc";
		} else if(document.all.OA1.GetCurrentProgID() == "Excel.Application"){
			sFileName = myGuid + ".xls";
		} else if(document.all.OA1.GetCurrentProgID() == "PowerPoint.Application"){
			sFileName = myGuid + ".ppt";
		} else if(document.all.OA1.GetCurrentProgID() == "Visio.Application"){
			sFileName = myGuid + ".vsd";
		} else if(document.all.OA1.GetCurrentProgID() == "MSProject.Application"){
			sFileName = myGuid + ".mpp";
		} else{
			sFileName = myGuid + ".tmp";
		}

		document.OA1.HttpAddPostOpenedFile (sFileName);
		//document.OA1.HttpPost("http://www.ocxt.com/demo/upload_weboffice.php");
		document.OA1.HttpPost("http://localhost:6596/streame/UploadAction.aspx");
		//If you have not set the WebUsername and WebPassword, you need enable the window anonymous authentication.

		if(document.OA1.GetErrorCode() == 0 || document.OA1.GetErrorCode() == 200) {
		    var sPath = "Save successfully!";
				window.alert(sPath);
		} else {
			window.alert("you need enable the IIS Windows Anonymous Authentication if you have not set the username and password in the HttpPost method. you need set the timeout and largefile size in the web.config file.");
		}
	} else {
		window.alert("Please open a document firstly!");
	}
}

function CloseDoc() {
	if(document.OA1.IsOpened) {
		document.all.OA1.CloseDoc();
	}
}

function VBAProgramming() {
	if(document.OA1.IsOpened) {			
		if(document.all.OA1.GetCurrentProgID() == "Word.Application"){
			var objWord = document.OA1.ActiveDocument;
			var range = objWord.Range(0,0);
			var WTable = objWord.Tables.Add(range, 3,3);
			WTable.Cell(1,1).Range.Font.Name = "Times New Roman";		   
			WTable.Cell(1,1).Range.Text = "Automation 1";    
			WTable.Cell(1,2).Range.Font.Size = 18;    
			WTable.Cell(1,2).Range.Bold = true;   
			WTable.Cell(1,2).Range.Font.Italic = true;  
			WTable.Cell(1,2).Range.Text = "Automation 2";     
			WTable.Cell(2,1).Range.ParagraphFormat.Alignment = 1; // 0= Left, 1=Center, 2=Right   
			WTable.Cell(2,1).Range.Font.Name = "Arial";   
			WTable.Cell(2,1).Range.Font.Size = 12;   
			WTable.Cell(2,1).Range.Bold = false;   
			WTable.Cell(2,1).Range.ParagraphFormat.Alignment = 2;     
			WTable.Cell(3,3).Range.Font.Name = "Times New Roman";    
			WTable.Cell(3,3).Range.Font.Size = 14;    
			WTable.Cell(3,3).Range.Bold = true;    
			WTable.Cell(3,3).Range.Font.Underline = true;  
			WTable.Cell(3,3).Range.ParagraphFormat.Alignment = 0;  
			WTable.Cell(3,2).Range.Text = "Automation 3";
			//var appWord = document.OA1.GetApplication;
			//appWord.Selection.Find.Execute('edraw',   false, false, false,false,  false,   1, false,   false, 'cutedraw',   2,false,  false,   //false,   false);

		} else if(document.all.OA1.GetCurrentProgID() == "Excel.Application"){
			var objExcel = document.OA1.GetApplication();	
			var worksheet = objExcel.ActiveSheet;
			worksheet.cells(1,1).value ="100";
			worksheet.cells(1,2).value ="101";
			worksheet.cells(1,3).value ="102";
			worksheet.cells(2,1).value ="103";
			worksheet.cells(2,2).value ="104";
			worksheet.cells(2,3).value ="105";
		}
	}
}

function OA1_NotifyCtrlReady() {
		document.OA1.ShowRibbonTitlebar (true);
		document.OA1.ShowMenubar (true);
		document.OA1.Toolbars = true;
		//disalbe the save button and save as button
		document.OA1.DisableFileCommand(1, false);//wdUIDisalbeOfficeButton
		document.OA1.DisableFileCommand(2, true);//wdUIDisalbeNew
		document.OA1.DisableFileCommand(4, true);//wdUIDisalbeOpen
		//If you want to open a document when the page loads, you should put the code here.
		//document.all.OA1.Open("http://www.ocxt.com/demo/samples/sample.doc");
		
		document.OA1.LicenseName = "Daoning6227685802";
		document.OA1.LicenseCode = "EDO8-5537-1270-ABEB";
}

function OA1_BeforeDocumentOpened() {
		//document.OA1.DisableFileCommand(1, true);//wdUIDisalbeOfficeButton
		//document.OA1.DisableFileCommand(2, true);//wdUIDisalbeNew
		//document.OA1.DisableFileCommand(4, true);//wdUIDisalbeOpen
		//document.OA1.DisableFileCommand(16, true);//wdUIDisalbeSave
		//document.OA1.DisableFileCommand(32, true);//wdUIDisalbeSaveAs		
}

function OA1_DocumentOpened() {
		//You can do the office automation here
		//var objWord = document.OA1.ActiveDocument;
		//objWord.Content.Text = "You can do the office Automation with the Edraw Office Viewer Component.";
}

function OA_DocumentBeforePrint() {
    //window.alert("OA_DocumentBeforePrint");
    // document.OA1.DisableStandardCommand(4, true);//cmdTypePrint = 0x00000004, // prevent print
}

function OA_WindowBeforeRightClick() {
    //window.alert("OA_WindowBeforeRightClick");
    //document.OA1.DisableStandardCommand(8, true);//cmdTypeRightClick = 0x00000008, // prevent right click
}

function OA_BeforeDocumentSaved() {
    //window.alert("OA_BeforeDocumentSaved");
    //document.OA1.DisableStandardCommand(1, true);//cmdTypeSave  = 0x00000001,  // prevent save
}
//-->
</SCRIPT>

<script   language="javascript">    
window.onbeforeunload = function() {  
	if(document.all.OA1)
  		document.all.OA1.CloseDoc();
} 
</script>

<SCRIPT LANGUAGE=javascript FOR=OA1 EVENT=NotifyCtrlReady>
<!--
 OA1_NotifyCtrlReady();
//-->
</SCRIPT>

<script language="javascript" for="OA1" event="DocumentOpened()"> 
  OA1_DocumentOpened();
</script>

<script language="javascript" for="OA1" event="BeforeDocumentOpened()"> 
  OA1_BeforeDocumentOpened();
</script>

<script language="javascript" for="OA1" event="BeforeDocumentSaved()"> 
  OA_BeforeDocumentSaved();
</script>

<script language="javascript" for="OA1" event="DocumentBeforePrint()"> 
  OA_DocumentBeforePrint();
</script>

<script language="javascript" for="OA1" event="WindowBeforeRightClick()"> 
  OA_WindowBeforeRightClick();
</script>

<body topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0">
	   <%
	   String filetype = "jpg;jpeg;png;bmp;doc;docx;dot;xls;xlsx;ppt;pptx";
	   if(filetype.indexOf(type) < 0){
	   	%>
	   	<script>
	   	download();
	   	</script>
	   	<%
	   }else {
	   %>
	    <object classid="clsid:7677E74E-5831-4C9E-A2DD-9B1EF9DF2DB4" id="OA1" width="100%" height="99%" codebase="/cs_deploy/officeviewer.cab#version=8,0,0,520">
			<!-- NOTE: The officeviewer.cab file in edrawsoft.com is the trial version. If you have the full version, you should upload the officeviewer.cab file to your own site. Then change the codebase.//-->
			<param name="Toolbars" value="-1">
			<param name="LicenseName" value="Daoning6227685802">
			<param name="LicenseCode" value="EDO8-5537-1270-ABEB">
			<param name="BorderColor" value="15647136">
			<param name="BorderStyle" value="2">
		</object>
		
		<script language="JavaScript" type="text/javascript" src="NoIEActivate.js"></script>
		<%} %>
	<img id="bmp" style="display: none;" align="middle"></img>
</body>
<script>
var temp = "<%=type%>";
var filetype = "jpg;jpeg;png;bmp;doc;docx;dot;xls;xlsx;ppt;pptx";
if(filetype.indexOf(temp) > -1) {
	if("jpg;jpeg;png;bmp".indexOf(temp)==-1) {
		OpenFromServer();
	} else {
		document.getElementById("OA1").style.display = "none";
		document.getElementById("bmp").src="/servlet/displayImage?filepath=<%=encryptFilePath%>";
		document.getElementById("bmp").removeAttribute("style");
	}
}
</script>
</html>
