<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%
	String type=request.getParameter("type");
%>
<html>
<head>

<title>Insert title here</title>
</head>

	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<script language="JavaScript" src="/module/utils/js/template.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>    
<style>
div#treemenu 
{
	border:1px solid;
	BORDER-COLLAPSE: collapse;
	border-color:#94B6E6;
	width: 440px;
	height: 280px;
	overflow: auto;
}

</style>
<hrms:themes />
<body>
  	<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground " style="margin-left:-3px;">
	<tr>  
		<td valign="top" align="center">
			<table><tr><td>	
			<div id="treemenu" class='common_border_color'></div>
			</td></tr></table>
		</td>	
	</tr>
	<tr>
		<td align='center' >
			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.enter"/>'  onclick='enter()' />

			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.cancel"/>'  onclick='goback()' />
		</td>
	</tr>
	</table>

</body>
</html>

<script type="text/javascript">
    var info=""
    if(!window.showModalDialog){
        info=parent.window.opener.window.dialogArguments;
    }else{
        info=dialogArguments;
    }
    var object_type="";
    var object_id="";
    if(info.length>=3)
       object_type=info[2];
    if(info.length>=4)
       object_id=info[3];
	var m_sXMLFile	= "/performance/kh_system/kh_template/create_point_tree.jsp?templateID="+info[0]+"&pointsetid=0&flag=0&subsys_id="+info[1]+"&object_type="+object_type+"&object_id="+object_id;		
	var newwindow;
	var root=new xtreeItem("root","<bean:message key="static.select"/>","","mil_body","<bean:message key="static.select"/>","/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=1;
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
        if(window.showModalDialog){
            parent.window.close();
        }else{
        	//xus 19/9/2 浏览器兼容：能力素质-素质模型-岗位序列素质模型-新增	
        	if(parent.window.opener.newPostModal_callback){
        		parent.window.close();
        	}else if(parent.window.opener.window.dialogArguments){
                window.open("about:blank","_top").close();
            }else{
                parent.window.close();
            }
        }
	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
		     if(info.length>3)
		     {
				alert("请选择素质指标！");
				return;
			 }else{
			    alert("请选择绩效指标！");
				return;
			 }
		}	
		
		var temps=root.getSelected().split(",");
		var points="";
		for(var i=0;i<temps.length;i++)
		{
			if(trim(temps[i]).length>0)
			{
				if(temps[i].substring(0,2)!='1_')
				{
					alert("不能选择指标类别");
					return;
				}
				points+=","+temps[i].substring(2);
			}
		}

        parent.window.returnValue=points;

        if(window.showModalDialog){
            parent.window.close();
        }else{
        	//xus 19/9/2 浏览器兼容：能力素质-素质模型-岗位序列素质模型-新增	
        	if(parent.window.opener.newPostModal_callback){
        		parent.window.opener.newPostModal_callback(points);
        		parent.window.close();
            }else if(parent.window.opener.window.dialogArguments){
                parent.window.opener.window.addpoint_ok(points,<%=type%>);
                window.open("about:blank","_top").close();
            }else{
                parent.window.close();
            }
        }
	}
	
</script>
	
