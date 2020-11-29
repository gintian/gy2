<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
<head>
     <style type="text/css">
      a:link {color: blue; text-decoration:none}
      a:hover {color:black; text-decoration:none} 
        .self_info{
            Height:28px;
            width:100%;
            background:#dfe8f6;
            border:1px;
            padding:0;
            margin:0;
            font: normal 11px tahoma,arial,helvetica,sans-serif;
            padding-top:6px;
        }
            
    </style> 
        
</head>
<body style="height:100%;width:100%;overflow:hidden;margin:0;padding:0;TEXT-ALIGN:center;">
    <br>
    <div id="selfinfo">
        <hrms:studentinfo/>
        
    </div> 

</body>
</html>