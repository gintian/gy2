<%@ page contentType="text/html; charset=UTF-8" language="java"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <link rel="stylesheet" href="/css/css1.css" type="text/css">
  <hrms:themes />
   <script language="JavaScript" src="/js/function.js"></script>
  
  </head>
  
  <body>
  
  <div id="process" style="position:absolute;top:10px;left:10px;width:360px;height: 370px;overflow: auto;border: 1px solid #ccc;"></div>
    <script>
        var desc=replaceAll(dialogArguments,' ','&nbsp;');
        desc=replaceAll(desc,'\r\n','<br>');
        desc=replaceAll(desc,'\n','<br>');
    	document.getElementById("process").innerHTML=desc;
    
    </script>
   
  </body>
</html>
