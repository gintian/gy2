<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<html>
<head>
	<%
	String unitcode= request.getParameter("unitcode");
	 %>
<title>Insert title here</title>
</head>

	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>    
<style>
div#treemenu {
BORDER-BOTTOM:#94B6E6 1pt inset; 
BORDER-COLLAPSE: collapse;
BORDER-LEFT: #94B6E6 1pt solid; 
BORDER-RIGHT: #94B6E6 1pt inset; 
BORDER-TOP: #94B6E6 1pt solid; 
width: 400px;
height: 280px;
overflow: auto;
}

</style>
<script type="text/javascript">
	var copypoints="";
	var copyorg="";
</script>
<body topmargin="0" leftmargin="12" marginheight="0" marginwidth="0" >
  	<table width="440" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	<tr>  
		<td valign="top" align='left' >
			<table>
				<tr>
					<td>	
					<div id="treemenu" class="complex_border_color" style="width:436"></div>
					</td>
				</tr>
			</table>
		</td>	
	</tr>
	<tr>
		<td colspan='2' align='center' style="padding-top:4px;">
			<input type='button'  class="mybutton" value='<bean:message key="label.query.selectall"/>'  onclick='selectAll()' />

			<input type='button'  class="mybutton" value='<bean:message key="button.all.reset"/>'  onclick='undoAll()' />

			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.enter"/>'  onclick=' surepoint()' />

			<input type='button'  class="mybutton" value='<bean:message key="lable.tz_template.cancel"/>'  onclick='goback()' />
		</td>
	</tr>
	</table>

</body>
</html>

<SCRIPT LANGUAGE=javascript>
var m_sXMLFile	= "/performance/kh_system/kh_field/create_newpointtree.jsp?unitcode=<%=unitcode%>"+"&pointsetid=0&flag=0&subsys_id="+'33';
	var newwindow;
	var root=new xtreeItem("root","<bean:message key="static.select"/>","","mil_body","<bean:message key="static.select"/>","/images/add_all.gif",m_sXMLFile);
	Global.defaultInput=1;
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));	
	
	function surepoint(){
		if(root.getSelected()==""){
			alert("请选择绩效指标！");
			return;
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
		returnValue=points;
		window.close();
	}
	function goback()
	{
		returnValue="";
		window.close();
	}

	function selectAll()
	{
		  root.expandAll();
		  var currnode,values;
		  values="";
		  if(Global.defaultInput==1)
		  {
		        var checkitems=document.getElementsByName("treeItem-check");
		        for(var i=0;i<checkitems.length;i++)
		        {
			          currnode=checkitems[i];
			          var codeValue = currnode.value;
			          if(codeValue.substring(0,2)=='1_')
			          	  currnode.checked=true;      
		        }
		  }
		  else if(Global.defaultInput==2)
		  {
		  }
	}
	function undoAll()
	{
		  root.allClear();
	}
</script>
	
