<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
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
	BORDER-COLLAPSE: collapse;
	height: 280px;
	overflow: auto;
}

</style>
<body >
  	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top">
			<table width="440" class="ListTableF"><tr><td>	
			<div id="treemenu"></div>
			</td></tr></table>
		</td>	
	</tr>
	<tr>
		<td height="30px" align='center' >
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.enter"/>'  onclick='enter()' />
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.cancel"/>'  onclick='goback()' />
		</td>
	</tr>
	</table>

</body>
</html>

<SCRIPT LANGUAGE=javascript>
    var info="";
    if(!window.showModalDialog){
        info=parent.window.opener.window.dialogArguments;
    }else{
        info=dialogArguments;
    }
	var m_sXMLFile	= "/performance/kh_system/kh_field/create_field_tree.jsp?pointsetid="+info[0]+"&subsys_id="+info[1];
	var newwindow;
	var root=new xtreeItem("root","指标分类","","mil_body","<bean:message key="static.select"/>","/images/add_all.gif",m_sXMLFile);
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
        parent.window.returnValue="";
        if(window.showModalDialog) {
            parent.window.close();;
        }else{
            parent.window.opener.window.plField_check_window_ok("");
            window.open("about:blank","_top").close();
        }
	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
		    
			alert("请选择考核指标分类！");
			return;
			 
		}	
		var tmps="";
		tmps=root.getSelected()+","+root.getSelectedTitle();


        parent.window.returnValue=tmps;
        if(window.showModalDialog) {
            parent.window.close();;
        }else{
            parent.window.opener.window.plField_check_window_ok(tmps);
            window.open("about:blank","_top").close();
        }
	}
	
</script>
	
