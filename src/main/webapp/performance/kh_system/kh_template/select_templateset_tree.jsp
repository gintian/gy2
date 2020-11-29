<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<html>
<head>

<title>Insert title here</title>
</head>

	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>    
<style>
div#treemenu 
{
	BORDER-BOTTOM:#94B6E6 1pt inset; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 1pt inset; 
	BORDER-RIGHT: #94B6E6 1pt inset; 
	BORDER-TOP: #94B6E6 1pt inset; 
	width: 405px;
	height: 280px;
	overflow: auto;
}

</style>
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosFlag = userView.getBosflag();
	String style = !"hcm".equalsIgnoreCase(bosFlag) ? "margin-top:10" : "";
%>
<body>
  	<table width="440" align="left" style="<%=style %>" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top" align="left">
			<table><tr><td>	
			<div id="treemenu" style="width:433;" class="complex_border_color"></div>
			</td></tr></table>
		</td>	
	</tr>
	<tr>
		<td align='center' style="padding-top:4px;">
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.enter"/>'  onclick='enter()' />
			&nbsp;
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.cancel"/>'  onclick='goback()' />
		</td>
	</tr>
	</table>

</body>
</html>

<SCRIPT LANGUAGE=javascript>
    var info=window.dialogArguments || parent.parent.dialogArguments ||parent.opener.dialogArguments;
	var m_sXMLFile	= "/performance/kh_system/kh_template/create_templateset_tree.jsp?templatesetid="+info[0]+"&subsys_id="+info[1];
	var newwindow;
	var root=new xtreeItem("root","模板分类","","mil_body","模板分类","/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=2;
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	
	
	function goback()
	{
		returnValue="";

        var info=""
        if(window.showModalDialog){
            window.close();
        }else{
            if(parent.opener.dialogArguments){
                window.open("about:blank","_top").close();
            }else{
                window.close();
            }
		}


	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
		    
			alert("请选择考核模版分类！");
			return;
			 
		}	
		var tmps="";
		tmps=root.getSelected()+","+root.getSelectedTitle();

		
		parent.returnValue=tmps;
        if(window.showModalDialog){
        	parent.window.close();
        }else{
            if (parent.opener.selecttemplateset_Ok){
                parent.opener.selecttemplateset_Ok(tmps);
                window.open("about:blank","_top").close();
            }else if(parent.parent.selecttemplateset_Ok){
                parent.parent.selecttemplateset_Ok(tmps);
                window.open("about:blank","_top").close();
            }else{
            	parent.window.close();
            }
        }
	}
	
</script>
	
