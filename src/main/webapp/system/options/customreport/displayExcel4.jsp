<%@page contentType="text/html;charset=UTF-8"%>
<%@page import="java.net.URLDecoder"%>
<HTML>
<head>
<title></title>
<%	String filename = request.getParameter("filename"); 
	filename = URLDecoder.decode(filename);
	int index = filename.indexOf(".");
	String type = filename.substring(index);
	String recordId = filename.substring(0,index);
	
	//filename = URLEncoder.encode(filename);
	//System.out.println(filename);
%>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">




<style>
</style>
</head>

<SCRIPT ID=clientEventHandlersJS LANGUAGE=javascript>
<!--
 
function OpenFromLocal()
{
		document.all.OA1.OpenFileDialog();
		//You can call the Open method to open silently.
}

function OpenFromServer()
{
		document.all.OA1.HttpInit();
		//document.all.OA1.HttpAddpostString("EDA_GETSTREAMDATA", "EDA_YES");
		document.all.OA1.HttpAddpostString("DocumentID", "<%=filename %>");
		//document.all.OA1.Open('http://<%=request.getServerName() %>:<%=request.getServerPort() %>/servlet/DisplayOleFile?filename=<%=filename %>');
		document.all.OA1.HttpOpenFileFromStream('<%=request.getScheme() %>://<%=request.getServerName() %>:<%=request.getServerPort() %>/servlet/DisplayCustomerReportExcelFile?filename=<%=filename %>',"Excel.Application");
		//if(document.all.OA1.IsOpened()){

			document.OA1.ShowMenubar(false); 
			document.OA1.Toolbars= false;
		//}
}

function CreateNew()
{
		document.all.OA1.CreateNew();
}

function PrintDoc()
{
		if(document.all.OA1.IsOpened()){
			document.all.OA1.PrintDialog();
		}
}

function PrintPreview()
{
		if(document.all.OA1.IsOpened()){
			document.all.OA1.PrintPreview();
		}
}

function ProtectDoc()
{
		if(document.all.OA1.IsOpened()){
			document.all.OA1.ProtectDoc(1);
		}
}


function UnProtectDoc()
{
		if(document.all.OA1.IsOpened()){
			document.all.OA1.UnProtectDoc();
		}
}

function DocIsDirty()
{
		if(document.all.OA1.IsOpened()){
			if(document.OA1.IsDirty)
			{
					window.alert("The file has been modified!");
			}
			else{
					window.alert("The file hasn't been modified!");
			} 
		}
}

function ShowHideToolbar()
{
		if(document.all.OA1.IsOpened()){
			var x = document.OA1.Toolbars;
			document.OA1.ShowMenubar(! x); 
			document.OA1.Toolbars= ! x;
		}
}

function DisableRightClick()
{
		if(document.all.OA1.IsOpened()){
			document.all.OA1.DisableViewRightClickMenu(true);
		}
}

function SaveAs()
{
		if(document.all.OA1.IsOpened()){
			document.all.OA1.SaveFileDialog();
			//You can call the SaveAs method to save silently.
		}
}

function SavetoServer()
{
	if(document.OA1.IsOpened)
	{
		document.OA1.HttpInit();
		var sFileName = document.OA1.GetDocumentName();

		document.OA1.HttpAddPostOpenedFile (sFileName);
		//document.OA1.HttpAddPostOpenedFile (sFileName, -4143); //save as xls file then upload
		//document.OA1.HttpAddPostOpenedFile (sFileName, 51); //save as xlxs file then upload
		
		document.OA1.HttpPost("http://www.ocxt.com/demo/upload_weboffice.php");
		if(document.OA1.GetErrorCode() == 0 || document.OA1.GetErrorCode() == 200)
		{		
			var sPath = "Save successfully! You can download it at http://www.ocxt.com/demo/" + sFileName;
			window.alert(sPath);
		}
		else
		{
			window.alert("you need enable the IIS Windows Anonymous Authentication if you have not set the username and password in the HttpPost method. you need set the timeout and largefile size in the web.config file.");
		}	
	}
	else{
		window.alert("Please open a document firstly!");
	}
}

function CloseDoc()
{
		if(document.OA1.IsOpened)
		{
			document.all.OA1.CloseDoc();
		}
}

function VBAProgramming()
{
		if(document.OA1.IsOpened)
		{			
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

function OA1_NotifyCtrlReady() 
{
		document.OA1.ShowRibbonTitlebar (false);
		//document.OA1.ShowMenubar (false);
		//document.OA1.Toolbars = false;
		//disalbe the save button and save as button
		document.OA1.DisableFileCommand(1, true);//wdUIDisalbeOfficeButton
		document.OA1.DisableFileCommand(2, true);//wdUIDisalbeNew
		document.OA1.DisableFileCommand(4, true);//wdUIDisalbeOpen
		//If you want to open a document when the page loads, you should put the code here.
		//document.all.OA1.Open("http://www.ocxt.com/demo/samples/sample.doc");
		
		document.OA1.LicenseName = "Daoning6227685802";
		document.OA1.LicenseCode = "EDO8-5537-1270-ABEB";
}

function OA1_BeforeDocumentOpened()
{
		//document.OA1.DisableFileCommand(1, true);//wdUIDisalbeOfficeButton
		//document.OA1.DisableFileCommand(2, true);//wdUIDisalbeNew
		//document.OA1.DisableFileCommand(4, true);//wdUIDisalbeOpen
		//document.OA1.DisableFileCommand(16, true);//wdUIDisalbeSave
		//document.OA1.DisableFileCommand(32, true);//wdUIDisalbeSaveAs		
}

function OA1_DocumentOpened()
{
		//You can do the office automation here
		//var objWord = document.OA1.ActiveDocument;
		//objWord.Content.Text = "You can do the office Automation with the Edraw Office Viewer Component.";
}

function OA_DocumentBeforePrint()
{
    //window.alert("OA_DocumentBeforePrint");
    // document.OA1.DisableStandardCommand(4, true);//cmdTypePrint = 0x00000004, // prevent print
}

function OA_WindowBeforeRightClick()
{
    //window.alert("OA_WindowBeforeRightClick");
    //document.OA1.DisableStandardCommand(8, true);//cmdTypeRightClick = 0x00000008, // prevent right click
}

function OA_BeforeDocumentSaved()
{
    //window.alert("OA_BeforeDocumentSaved");
    //document.OA1.DisableStandardCommand(1, true);//cmdTypeSave  = 0x00000001,  // prevent save
}
//-->
</SCRIPT>

<script   language="javascript">    
window.onbeforeunload   =   function()     //author:   meizz    
{  
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
	<object classid="clsid:7677E74E-5831-4C9E-A2DD-9B1EF9DF2DB4" id="OA1" width="100%" height="100%" codebase="/cs_deploy/officeviewer.cab#version=8,0,0,520">
			<!-- NOTE: The officeviewer.cab file in edrawsoft.com is the trial version. If you have the full version, you should upload the officeviewer.cab file to your own site. Then change the codebase.//-->
			<param name="Toolbars" value="-1">
			<param name="LicenseName" value="Daoning6227685802">
			<param name="LicenseCode" value="EDO8-5537-1270-ABEB">
			<param name="BorderColor" value="15647136">
			<param name="BorderStyle" value="2">
		</object>
		
		<script language="JavaScript" type="text/javascript" src="NoIEActivate.js"></script>
	
</table>

</body>

</html>
<script>
	OpenFromServer();
</script>