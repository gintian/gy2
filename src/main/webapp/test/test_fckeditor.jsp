<%@ page language="java" contentType="text/html; charset=GB18030"
    pageEncoding="GB18030"%>
<%@ taglib uri="/WEB-INF/tlds/FCKeditor.tld" prefix="FCK"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>Insert title here</title>
</head>
<body>

<form action="">
<FCK:editor basePath="/fckeditor/" id="sss"
        imageBrowserURL="/fckeditor/editor/filemanager/browser/default/browser.html?Type=Image&Connector=connectors/jsp/connector"
        linkBrowserURL="/fckeditor/editor/filemanager/browser/default/browser.html?Connector=connectors/jsp/connector"
        flashBrowserURL="/fckeditor/editor/filemanager/browser/default/browser.html?Type=Flash&Connector=connectors/jsp/connector"
        imageUploadURL="/editor/filemanager/upload/simpleuploader?Type=Image"
        linkUploadURL="/editor/filemanager/upload/simpleuploader?Type=File"
        flashUploadURL="/editor/filemanager/upload/simpleuploader?Type=Flash">
This is some <strong>sample text</strong>. You are using <a href="http://www.fredck.com/fckeditor/">fckeditor</a>.
</FCK:editor> 
</form>   
</body>
</html>