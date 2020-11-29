<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
 <%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
   EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
   String netHref=employPortalForm.getNetHref();
String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl;
    String lftype=employPortalForm.getLfType();
    if(lftype==null)
     lftype="0";

%>
<html>
<head>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<style  id="iframeCss">
</style>
<script language="javascript">
var width=window.screen.width-300;
var height=window.screen.height-760;
function SetIEOpt()
   {
      obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
   }
   function SETTDCOLOR(obj,tdcolor)
   {
      obj.style.backgroundColor =tdcolor;
   }
   
</script>
</head>
<body>
<table  width="100%" border="0" cellspacing="0" cellpadding="0" style='ALIGN:center'>
   <tr>
    <td>
    

     <%if(lftype.equals("1")){ %>
	<script language="javascript">
       var bname=checkBrowser();
       var ahref="<%=netHref%>";
       if(bname.indexOf("MSIE")!=-1&&ahref.length>0)
       {
           var left=(document.body.clientWidth-1000)/2;
           document.write("<div id=gg1 style=\"position:absolute;left:"+left+"px;top:0px;width:1000px;height:124px;z-index:1;filter:Alpha(Opacity=0)\">");
           document.write("<img onClick=\"window.open('"+ahref+"');\" src=\"\images\arrow.gif\" width=\"100%\" height=\"100%\" style=\"cursor:hand;\"/>");
           document.write("</div>");
           document.write("<div style=\"z-index:-1;\">");
       }else if(ahref.length>0)
       {
          document.write("<div id=gg1 style=\"position:absolute;left:"+left+"px;top:0px;width:1000px;height:124px;z-index:1;filter:Alpha(Opacity=0)\">");
          	///document.write("<button style=\"width:1000;height:124;background:transparent;border:0;padding:0;cursor:hand;\" onclick=\"window.open('"+ahref+"');\"> ");
          document.write("<a style='display:block;width:100%;height:100%;' href='"+ahref+"' target='_blank'> <img src='/images/tree_space.gif'; width=\"100%\"; height=\"100%\"; style=\"cursor:pointer;\"/></a>");
          document.write("</div>");
          document.write("<div style=\"z-index:-1;\">");
       }else{
         document.write("<div >");
       }
    </script>
    <%} %>
    	
 			<%if(lftype.equals("1")){ %>
             <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="1000" height="124">  
             <param name="movie" value="/images/hire_header.swf">  
             <param name="wmode" value="transparent">  
             <embed wmode="transparent" src="/images/hire_header.swf" width="1000" height="124" type="application/x-shockwave-flash" />  
            </object>
            
           <%}else {%>
           <div>
           <%
           
              if(netHref!=null&&netHref.length()>0){ %>
          <a href="<%=netHref%>" target="_blank"><img src="/images/hire_header.gif" border="0"/></a>
           <%}else{ %>
            <img src='/images/hire_header.gif' border='0'/>
          	<%} %>
          	</div>
          	<%} %>
		
		
		<%if(lftype.equals("1")){ %>
	<script language="javascript">
       var bname=checkBrowser();
       var ahref="<%=netHref%>";
       if(bname.indexOf("MSIE")!=-1&&ahref.length>0)
       {
           document.write("</div>");
           
       }else if(ahref.length>0)
       {
          document.write("</div>");
       }else{
         document.write("</div>");
       }
    </script>
    <%} %>


     </td>
 </tr>
 </table>

</body>
</html>
