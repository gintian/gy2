<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <hrms:themes></hrms:themes>
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
   <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
   <SCRIPT LANGUAGE=javascript>
      	function delete_base()
    	{
    	 
    	  
    	   var currnode,codeitemid,target_url;
    	   currnode=Global.selectedItem;
    	   if(currnode==null)
    	    	return;
    	    
    	   codeitemid=currnode.uid;
    	   if(codeitemid=="root")
    	   {
    	   	alert('不能删除根目录!');
    	   }
    	   else
    	   {
    	    if(confirm("确认删除"+codeitemid+"年度吗?"))
	      {
    	     var theArr=new Array(currnode);
    	     target_url="/kq/options/durationlist.do?b_delete=link&kq_year="+codeitemid;
    	  
    	     parent.location.href=target_url;
    	     }
    	     else
    	     {
    	       return false;
    	     }
    	    }
    	   
    	 }
   </SCRIPT>     
</HEAD>
<body   topmargin="0" marginheight="0" marginwidth="0">
<hrms:priv func_id="27031">	
<table id="durTab" width="112%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
	<tr align="left" valign="middle" class="toolbar"  style="padding-left:2px;width:expression(document.body.clientWidth);overflow: auto;">  
    <td valign="middle">			
		&nbsp; <input type="image" name="b_delete" src="/images/del.gif" alt="删除考勤期间年度" onclick="delete_base();">
	</td>
	</tr>
	<tr>
    	<td>
	    <div id="treemenu" ></div>
    </td>
  </tr>
</table> 
</hrms:priv>
<BODY>
</HTML>

<script language="javascript" src="/kq/kq.js"></script>
<SCRIPT LANGUAGE=javascript>
var m_sXMLFile	= "/kq/options/kq_duration_tree.jsp";	 
var newwindow;
var root=new xtreeItem("root","年度考勤期间","/kq/options/duration_details.jsp?b_query=link&kq_year","mil_body","年度考勤期间","",m_sXMLFile);
root.setup(document.getElementById("treemenu"));
if(newwindow!=null)
{
newwindow.focus();
}
if(parent.parent.myNewBody!=null)
 {
	parent.parent.myNewBody.cols="*,0"
 }
tabWidthForEdge("durTab");
</SCRIPT>