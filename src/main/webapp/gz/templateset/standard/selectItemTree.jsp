<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.interfaces.performance.PointTreeByXml,
				 com.hjsj.hrms.actionform.performance.showkhresult.DirectionAnalyseForm" %>
<%
	String css_url="/css/css1.css";
	String type=request.getParameter("type");
	//System.out.print(type);
	
%>


<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>  
	<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>  
	<SCRIPT LANGUAGE=javascript>
	
	function goback()
	{
		window.close();
	
	}
	
	
	
	
	
	
</SCRIPT>     
</HEAD>
<style>
div#treemenu {
BORDER-BOTTOM:#94B6E6 1pt inset; 
BORDER-COLLAPSE: collapse;
BORDER-LEFT: #94B6E6 1pt inset; 
BORDER-RIGHT: #94B6E6 1pt inset; 
BORDER-TOP: #94B6E6 1pt inset; 
width: 325px;
height: 250px;
overflow: auto;
}

</style>
<hrms:themes />
<body >
	<table width="410px" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="margin-top:-3px;margin-left:-10px;">
	<tr>
		<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="gz.templateset.selectPoint"/>：</td>
	</tr>
	<tr>  
		<td valign="top">
			
			<table><tr><td>&nbsp;</td><td>	
			<div id="treemenu"></div>
			</td></tr></table>
			
		</td>	
		<td valign="top"  >
			
			
					<table border=0 width="100%" height="100%" >
					<tr><td height='150' valign='top' >
							<div id='b_add' style='display:none' >
						<input type='button'  class="mybutton" value=' <bean:message key="button.new.add"/> '  onclick='add()' />
						<br> 
							</div>
				           <!-- 【5212】薪资管理：新建薪资标准表，选择日期型指标时，出现的三个按钮，按钮之间距离太近了不对。   jingq add 2015.01.15 -->
				           <div id='b_edit' style='display:none' style="margin-top:10px;">
			            <input type='button'  class="mybutton" value=' <bean:message key="label.edit"/> '  onclick='edit()' />
			            <br>
							</div>
							
							<div id='b_delete' style='display:none' style="margin-top:10px;">
			            <input type='button' class="mybutton" value=' <bean:message key="button.setfield.delfield"/> '  onclick='del()' />
							</div>
						
					</td></tr>

					</table>
		
		</td>
	</tr>
					<tr><td height='35' align='center' > 
			           <input type='button'  class="mybutton" value=' <bean:message key="lable.tz_template.enter"/> '  onclick='enter()' />
			           <input type='button'  class="mybutton" value=' <bean:message key="lable.tz_template.cancel"/> '  onclick='goback()' />
					</td></tr>
	
</table>	
<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
	var focus_obj_node;

	
	var m_sXMLFile	= "/gz/templateset/standard/select_item_tree.jsp?flag=0&id=0&type=<%=type%>";		
	var newwindow;
	var root=new xtreeItem("root",GZ_TEMPLATESET_SELECTPOINT,"","mil_body",GZ_TEMPLATESET_SELECTPOINT,"/images/add_all.gif",m_sXMLFile);
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
	
	
	function anaylse(values)
	{
		
		var temps=values.split(",");
		var type='<%=type%>'
		//alert(values+"  "+type)
		if(type=='4')
		{
			for(var i=0;i<temps.length;i++)
			{
				if(temps[i].length>0)
				{
					var a_temp=temps[i].split("~");
					if(a_temp[0]*1!=2)
					{
						alert(GZ_TEMPLATESET_INFO21+"！");
						return false;
					}
				}
			}
			
		}
		else
		{
			var t="";
			for(var i=0;i<temps.length;i++)
			{
				if(temps[i].length>0)
				{
					var a_temp=temps[i].split("~");
					var a_temp2=a_temp[1].split("#");
					if(t=="")
					{
						t=a_temp2[0];
					}
					if(a_temp2[0]!=t)
					{
						alert(GZ_TEMPLATESET_info43 + "! ");
						return false;
					}
					if(a_temp[0]*1==0||a_temp[0]*1==1||a_temp[0]*1==2)
					{
						alert(GZ_TEMPLATESET_INFO22+"！");
						return false;
					}
				}
				
				
				if(temps[i].length>0)
				{
					var a_temp=temps[i].split("~");
					var a_temp2=a_temp[1].split("#");
					var a_temp3=a_temp2[0].split("/");
					
					if(a_temp[0]*1==4)
					{
						if(a_temp3[1]!='UN')
						{
							alert(GZ_TEMPLATESET_SELECTUN+"！");
							return false;
						}
					}
					else if(a_temp[0]*1==5)
					{
						if(a_temp3[1]!='UM')
						{
							alert(GZ_TEMPLATESET_SELECTUM+"！");
							return false;
						}
					}
					else if(a_temp[0]*1==6)
					{
						if(a_temp3[1]!='@K')
						{
							alert(GZ_TEMPLATESET_SELECTK+"！");
							return false;
						}
					}
					
				}
				
			}
		
		}
		return true;
	}
	
	function enter()
	{
		if(root.getSelected()=="")
		{
				alert(GZ_TEMPLATESET_SELECTRELATIONPOINT+"！");
				return;
		}	
		if(!anaylse(root.getSelected()))
			return;
		var type='<%=type%>';
		if(type=='4')
		{
			/* 新建薪资标准表时，在结果指标中插入指标时，选中一个指标后点击确定，系统仍提示“结果指标只能选择一个”，不对。 xiaoyun 2014-10-29 start */
			//if(root.getSelected().split(",").length>2)
			var selectIds = root.getSelected().split(",");
			var len = 0;
			for(var i=0; i < selectIds.length; i++) {
				if(selectIds[i].length>0) {
					len++;
				}
			}
			//if(root.getSelected().split(",").length>3)
			if(len>1)
			/* 新建薪资标准表时，在结果指标中插入指标时，选中一个指标后点击确定，系统仍提示“结果指标只能选择一个”，不对。 xiaoyun 2014-10-29 end */
			{
				alert(GZ_TEMPLATESET_INFO23+"！");
				return;
			}		
		}
		
		
		returnValue=root.getSelected();
		window.close();
	}
	
	
	//重读叶子节点
	function reloadNode()
	{
		var obj=focus_obj_node;	
	//	alert(obj.text+"  "+obj.uid)
		var temps=obj.uid.split("~");
		if(temps[0]=='7'||temps[0]=='8')
			obj=obj.parent;
		
		if(obj.getFirstChild().text==obj.text)
		{
			
				obj.loadChildren();
				obj.expand();
		
		}
		else
		{
			obj.clearChildren();
			obj.loadChildren();
			obj.expand();
		}
	}
	
	
	
	
	
	
	
	function showEidtButton1()
	{
		var obj=eval("b_add");
		obj.style.display="block";
		var obj1=eval("b_edit");
		obj1.style.display="block";
		var obj2=eval("b_delete");
		obj2.style.display="block";
		
		focus_obj_node=Global.selectedItem;
	}
	
	function showEidtButton2()
	{
		var obj=eval("b_add");
		obj.style.display="block";
		var obj1=eval("b_edit");
		obj1.style.display="block";
		var obj2=eval("b_delete");
		obj2.style.display="block";
		
		focus_obj_node=Global.selectedItem;
	}
	
	function hideButton()
	{
		var obj=eval("b_add");
		obj.style.display="none";
		var obj1=eval("b_edit");
		obj1.style.display="none";
		var obj2=eval("b_delete");
		obj2.style.display="none";
		
		focus_obj_node=Global.selectedItem;
	}
	
	
	function add()
	{
		
		var temp=focus_obj_node.uid;
		var temps1=temp.split("~");
		var temps2=temps1[1].split("#");
		
		var infos=new Array();
	    thecodeurl="/gz/templateset/standard/standardData.do?b_init=init`operate=0`uid="+temps2[0]; 
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	    var whl_str= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:650px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");	
	    reloadNode();
	}
	
	function edit()
	{
		var temp=root.getSelected();
		if(temp=="")
		{
			alert(GZ_TEMPLATESET_INFO24+"!");
			return;
		}
		
		if(temp.split(",").length>2)
		{
			alert(GZ_TEMPLATESET_INFO25);
			return;
		}
		
		var temps=temp.split(",");
		for(var i=0;i<temps.length;i++)
		{
			if(temps[i]!="")
			{
				var temps1=temps[i].split("~");
				if(temps1[0]!="7"&&temps1[0]!="8")
				{
					alert(GZ_TEMPLATESET_INFO26+"!");
					return;
				}
			}
		}
		var temps1=temps[0].split("~");
		var temps2=temps1[1].split("#");
		var infos=new Array();
		thecodeurl="/gz/templateset/standard/standardData.do?b_init=init`operate=1`uid="+temps2[0]+"`item_id="+temps2[1]; 
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	    var whl_str= window.showModalDialog(iframe_url, infos, 
		        "dialogWidth:650px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");	
	    reloadNode();
		
	}
	
	function del()
	{
		var temp=root.getSelected();
		if(temp=="")
		{
			alert(GZ_TEMPLATESET_INFO27+"!");
			return;
		}
		
		var temps=temp.split(",");
		
		
		var item="";
		for(var i=0;i<temps.length;i++)
		{
			if(temps[i]!="")
			{
				var temps1=temps[i].split("~");
				if(temps1[0]!="7"&&temps1[0]!="8")
				{
					alert(GZ_TEMPLATESET_INFO28+"!");
					return;
				}
				
				var temps2=temps1[1].split("#");
				if(item=="")
					item=temps2[0];
				if(item!=temps2[0])
				{
					alert(GZ_TEMPLATESET_INFO29+"!");
					return;
				}
			}
		}
		
		var obj=focus_obj_node;	
		var temps=obj.uid.split("~");
		if(temps[0]=='7'||temps[0]=='8')
			obj=obj.parent;
		
		if(obj.uid.split("~")[1]!=item)
		{
			alert(GZ_TEMPLATESET_INFO30);
			return;
		}
		
		var hashvo = new ParameterSet();
		hashvo.setValue("current_id",obj.uid.split("~")[1]);
		var In_paramters="value="+temp; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnInfo,functionId:'3020010111'},hashvo);			
	
	}
	
	 function ReturnInfo(outparamters)
   	 {
   	 	 isSub=outparamters.getValue("isSub");
   	 	 reloadNode2(isSub);
	 }
	 
	 
	 //重读叶子节点
	function reloadNode2(isSub)
	{
		var obj=focus_obj_node;	

		var temps=obj.uid.split("~");
		if(temps[0]=='7'||temps[0]=='8')
			obj=obj.parent;
		if(obj.getFirstChild().text==obj.text)
		{
			if(isSub=='1')
			{
				obj.loadChildren();
				obj.expand();
			}
		}
		else
		{
			obj.clearChildren();
			if(isSub=='1')
			{
				obj.loadChildren();
				obj.expand();
			}
		}
	}
	 
	 
</SCRIPT>
