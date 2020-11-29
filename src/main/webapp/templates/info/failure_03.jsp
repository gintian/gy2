<%@
  page contentType = "text/html;charset=UTF-8"
%>
<html>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr height="70"> 
    <td height="70" background="/images/police_top_back.jpg"><img src="/images/top_new.jpg"></td>
  </tr>
  <tr align="center" valign="middle"> 
    <td> <table width="400" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td width="19%" rowspan="2"><img src="/images/police_notice01.jpg" width="96" height="85"></td>
          <td width="81%">&nbsp;</td>
        </tr>
        <tr> 
          <td><font size="3">��璇�!&nbsp;<%=(String)session.getAttribute("errMsg")%></font></td>
        </tr>
      </table>
      <br> <br> <br> <br> </td>
  </tr>
  <tr bgcolor="66B141"> 
    <td height="8"></td>
  </tr>
</table>
</body>
</html>