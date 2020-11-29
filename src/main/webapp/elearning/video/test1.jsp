
<%@ page language="java" import="java.util.*" pageEncoding="GB18030"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>Insert title here</title>
<link rel="stylesheet" href="../../css/css1.css" type="text/css">
	<script language="JavaScript" src="../player/swfobject.js"></script>
<style type="text/css">
.kebg {
	BORDER-RIGHT: #7abeff 1px solid; BORDER-TOP: #7abeff 1px solid; BORDER-LEFT: #7abeff 1px solid; BACKGROUND-COLOR: #fff
}
.kebg2 {
	BORDER-RIGHT: none;
	BORDER-TOP: none;
	BORDER-LEFT: none;
	BACKGROUND-COLOR: #fff;
	border-bottom-color: #7abeff;
	border-bottom-width: 1px;
	border-bottom-style: solid;
}
.ketypebg {
	BACKGROUND-IMAGE: url(/pictures/blue/ketitlebg.gif)
}
.selftext1 {
	font-family: "宋体";
	font-size: 14px;
	font-style: normal;
	line-height: normal;
	font-weight: 600;
	font-variant: normal;
	color: #003399;
	text-decoration: none;
}
.selfline1 {
	border-top-width: 1px;
	border-top-style: dotted;
	border-right-style: none;
	border-bottom-style: none;
	border-left-style: none;
	border-top-color: #E6E6E6;
	border-right-width: 0px;
	border-bottom-width: 0px;
	border-left-width: 0px;
}
</style>
<SCRIPT language=JavaScript> 
function view_kjjj()
{

     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="../mylession/kjjj.htm";
     newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=450,height=300');
}
function view_jsjj()
{

     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="../mylession/jsjj.htm";
     newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=450,height=300');
}
function view_wjwz()
{

     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="../mylession/xjwz.htm";
     newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=450,height=300');
}
</script>
<style type="text/css">
.lian12hong {
	font-family: "宋体";
	font-size: 13px;
	font-style: normal;
	line-height: normal;
	font-weight: 600;
	font-variant: normal;
	color: #003366;
	text-decoration: none;
}
</style>
	
</head>
<body>
<table width="70%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
        
    <td background="../../images/ketitlebg.gif"> 
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>          
          <td width="120" height="10" align="left" valign="middle">
		   </td>        
        </tr>
      </table>
    </td>
      </tr>	 
	  <tr>
        
    <td height="1" bgcolor="#EEEEEE" > </td>
      </tr>	   
      <tr>
        <td>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td bgcolor="#FFFFFF">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td> 
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td>  
 <object type="application/x-shockwave-flash" data="../player/FlowPlayerBlack.swf" width="320" height="240" id="FlowPlayer">
	<param name="allowScriptAccess" value="sameDomain" />
	<param name="movie" value="../player/FlowPlayerBlack.swf" />
	<param name="quality" value="high" />
	<param name="scale" value="noScale" />
	<param name="wmode" value="transparent" />
	<param name="allowNetworking" value="all" />
	<param name="flashvars" value="config={ 
		autoPlay: true, 
		loop: false, 
		initialScale: 'scale',
		playList: [
			{ url: '<%=basePath%>/elearning/video/av1.flv' }
		],
		showPlayListButtons: true
		}" />
</object>
</td>
                    </tr>
                  </table>
                </td>
                <td width="1" align="left" valign="top" bgcolor="#EBEBEB"> </td>
                <td align="left" valign="top">
                 <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td height="50" class="lian12hong" align="center"> 
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                          <tr> 
                            <td width="30" align="right"><img src="../images/bi.gif" width="18" height="19"></td>
                            <td width="10">&nbsp;</td>
                            <td class="lian12hong">演示课件</td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    <tr> 
                      <td align="center"> <a href="###" onClick="view_kjjj();"><img src="../images/kjjj.jpg" width="110" height="35" border="0"></a></td>
                    </tr>
                    <tr> 
                      <td height="10" align="center">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td align="center"> <a href="###" onClick="view_jsjj();"><img src="../images/jsjj.jpg" width="110" height="35" border="0"></a></td>
                    </tr>
                    <tr> 
                      <td align="center">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td align="center"><a href="###" onClick="view_wjwz();"><img src="../images/xxwz.jpg" width="110" height="35" border="0"></a></td>
                    </tr>
                    <tr> 
                      <td align="center">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td align="center"><a href="gsjj.doc" target="_blank"><img src="../images/zlxx.jpg" width="110" height="35" border="0"></a></td>
                    </tr>
                    <tr> 
                      <td align="center">&nbsp;</td>
                    </tr>
                    <tr> 
                      <td align="center"><a href="../mylession/mycourse.htm"><img src="../images/quit.jpg" width="110" height="35" border="0"></a></td>
                    </tr>
                  </table></td>
                <td width="1" align="left" valign="top" bgcolor="#CCCCCC"> </td>
              </tr>
            </table>
          </td>
        </tr>
        <tr> 
          <td height="1"> <table width="100%" border="0" cellspacing="0" cellpadding="0" class="selfline1">
              <tr> 
                <td height="1"> </td>
              </tr>
            </table></td>
        </tr>      
      </table></td>
      </tr>    
    </table>
   
</body>
</html>