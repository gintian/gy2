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
          <td> 权限获取中<span id=t></span></td>
        </tr>
      </table>
      <br> <br> <br> <br> </td>
  </tr>
  <tr bgcolor="66B141"> 
    <td height="８"></td>
  </tr>
</table>
<form name=form1 action=/templates/index/guestLogon.jsp>

</form>
<script>
       var bar = 0 
	loading() 
	function loading(){ 
		text=".";
		for(i=0;i<bar;i++){
			text=text+".";
		}
		t.innerText = text;
		setTimeout("loading()",200);
		bar++;
		if(bar==7){
		   bar=0;
		  }
	}
       form1.submit();
</script>
</body>