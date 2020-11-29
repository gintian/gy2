
<HTML><HEAD><TITLE>asdadadad</TITLE>
<META http-equiv=Content-Type content="text/html; charset=UTF-8">
<SCRIPT language=javascript>
<!--
var iniCols, noCols, o_mf, o_ms, s;

function ini() {
	o_mf = parent.document.getElementById("forum");
	o_ms = document.getElementById("menuSwitch");
	noCols = iniCols = o_mf.cols;
	if ((pos = noCols.indexOf(",")) != -1) {
		noCols = "0" + noCols.substring(pos);
	}	
	s = false;
}

function changeLeft(){
	s = !s;
	o_mf.cols = s ? noCols : iniCols;
	o_ms.innerHTML = s ? "<img border=0 alt='open'width=7 height='28' src='/images/right_arrow.gif'>" : "<img border=0 alt='close' width=7 height='28' src='/images/left_arrow.gif'>";
}
//-->
</SCRIPT>
<style type="text/css">

</style>
<META content="MSHTML 6.00.2800.1528" name=GENERATOR></HEAD>
<BODY onload=ini() >
<TABLE height="100%" cellSpacing=0 cellPadding=0 width="100%" border=0>
  <TBODY>
  <TR bgcolor="#DCEFFE">
    <TD width="8" height="100%" valign="top"><A id=menuSwitch 
      href="javascript:changeLeft();"><IMG alt='close' 
      src="/images/left_arrow.gif" width=7 border=0 height="28"></A> 
</TD></TR></TBODY></TABLE></BODY></HTML>
