<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>

<style>

div#treemenu 
{	
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
	height:420px;
	width:930px;
}
div#treemenu2
{
    overflow-y:hidden;
    overflow-x:hidden;
    width:100%;
}

</style>

<script language="javascript">

    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	
</script>

<script language="javascript" src="/js/xtree.js"></script>
<script type="text/javascript">
<!--
Global.defaultInput=1;
Global.showroot=false;
function sub()
{
   	var ids=root.getSelected();
   	if(trim(ids).length==0)
   	{
     	alert("请选择需要导入的指标!");
     	return;
   	}
   	var waitInfo=eval("wait");		
   	waitInfo.style.display="block";
   	document.getElementById("qx").disabled=true;
   	document.getElementById("qc").disabled=true;
   	document.getElementById("dr").disabled=true;
   	document.getElementById("gb").disabled=true;
   	var hashvo=new ParameterSet();
   	hashvo.setValue("selectid",getEncodeStr(ids));
   	hashvo.setValue("type","1");
   	hashvo.setValue("subsys_id","${khFieldForm.subsys_id}");
   	hashvo.setValue("pointsetid","${khFieldForm.pointsetid}");
   	var request=new Request({asynchronous:false,onSuccess:importok,functionId:'9021001039'},hashvo);
}
function importok(outparameters)
{
    var waitInfo=eval("wait");		
    waitInfo.style.display="none";
   	var countTemplate=outparameters.getValue("countTemplate");
   	var countPoint=outparameters.getValue("countPoint");
   	alert("导入完毕，共导入"+countPoint+"个指标");
   	  	  
   	self.parent.location="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;
   	    	
//    khFieldForm.action="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;     	      
//   	khFieldForm.submit();
   	
//    var obj = new Object();
//    obj.flag="1";
//    returnValue=obj;
//    window.close();
}
function sel(type)
{
   	if(type=='1')
       	root.expandAll();
   	for(var i=0;i<document.forms[0].elements.length;i++)
   	{
     	if(document.forms[0].elements[i].type=='checkbox')
     	{
         	if(type=='1')
             	document.forms[0].elements[i].checked=true;
         	else
             	document.forms[0].elements[i].checked=false;
     	}
   	}
}
function isClosed(flag)
{
    if(flag=='2')
    {
      	alert("无效的文件,导入失败");
      	
      	self.parent.location="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;
      	
//      	returnValue=null;
//      	window.close();
    }
}
function backFlag()
{
//    self.parent.location="/performance/kh_system/kh_field/kh_field_tree.do?b_query=link&subsys_id="+khFieldForm.subsys_id.value;
	
	self.location="/performance/kh_system/kh_field/kh_field_tree.do?br_import1=link";
	
}
//-->
</script>
<html:form action="/performance/kh_system/kh_field/kh_field_tree">

	<div id="wait" style='position:absolute;top:100;left:375;display:none;'>
 
		<table border="1" width="50%" cellspacing="0" cellpadding="4" class="table_style" height="135" align="center">
			<tr>
			
				<td class="td_style" id='wait_desc' height=24>
					正在导入数据...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
	
<br>
	<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" >
		<thead>
			<tr>
				<td align="left" style="height:100px"> 
			 		<fieldset>
			 		<legend>
						说明
					</legend>
			 	    	<table>
			 	    		<tr>
			 	    			<td>
			 	     				1、星号表示相应指标已经存在,导入时会覆盖已有的指标.<br>
			 	     				2、导入指标时会同时导入标准标度.
			 	     			</td>
			 	    		</tr>
			 	    	</table>
			 	     </fieldset>
				</td>
			</tr> 				
			<tr>
				<td>
	 				<fieldset>
			 	     	<legend>请选择指标</legend>
	   						<table width="100%" class="mainbackground">			 	     			 	     			      
	         					<tr>
	           						<td align="center">  
	            						<div id="treemenu" align="left" style="overflow:auto;"> 
	             							<SCRIPT LANGUAGE="javascript">                 
	               								<bean:write name="khFieldForm" property="tree" filter="false"/>
	             							</SCRIPT>
	             						</div>           
	           						</td>
	           					</tr>           
	    					</table>  
	      			</fieldset>
	    		</td>
	    	</tr>
			<tr>
				<td align="center" style="height:40px">
					<input type="hidden" name="selectid" value=""/>
					<input type="button" value="全选" id="qx" name="allselect" class="mybutton" onclick="sel('1');"/>
					<input type="button" value="全撤" id="qc" class="mybutton" name="unselect" onclick="sel('2');"/>
					<input type="button" name="export" id="dr" class="mybutton" value="<bean:message key="menu.gz.import"/>" onclick="sub();"/>
					<input type="button" name="ca" id="gb" class="mybutton" value="<bean:message key="button.return"/>" onclick="backFlag();"/>
				</td>
			</tr>
	</table>
	<html:hidden name="khFieldForm" property="subsys_id"/>
	<html:hidden name="khFieldForm" property="pointsetid"/>
</html:form>

<script type="text/javascript">
<!--
	isClosed("${khFieldForm.flag}");
 	initDocument();
 	root.expandAll();
 	for(var i=0;i<document.forms[0].elements.length;i++)
   	{
     	if(document.forms[0].elements[i].type=='checkbox')
     	{
           	document.forms[0].elements[i].checked=true;
     	}
   	}
//-->
</script>
