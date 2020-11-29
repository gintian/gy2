<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.hjsj.sys.IResourceConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.kh_system.kh_template.KhTemplateForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	KhTemplateForm myForm=(KhTemplateForm)session.getAttribute("khTemplateForm");
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	String isHaveResource = "0";
	if(userView.isRWHaveResource(IResourceConstant.KH_MODULE, myForm.getTemplateid()) ){
		isHaveResource = "1";
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>　用户名：<%=userName%>　当前日期：<%=date%></title>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
document.oncontextmenu = function(e) {return false;}
//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
//这种写法有问题，ie下会报参数错误什么鬼，先注释掉，使用上面的方法
<%-- function document.oncontextmenu() 
{  
    if(isVisible=='2'||(isVisible=='1' && <%=isHaveResource%>=='0'))
    {
      return false;
    }
    if(t_type=='1')
    {
       var tyle=document.all.date_panel.style;
         tyle.position="absolute";
         if(document.body.scrollTop>event.y)
             tyle.top=event.y+"px";
         else
             tyle.top=(document.body.scrollTop+event.y)+"px";
         tyle.left=(document.body.scrollLeft+event.x)+"px";
         if(isHaveItem=='1')
         {
             
             document.getElementById("b_2").style.display="none"; 
             document.getElementById("b_3").style.display="none";
             document.getElementById("b_4").style.display="none";
             document.getElementById("b_5").style.display="none";
             document.getElementById("b_6").style.display="none";
             if(document.getElementById("b_7"))
                document.getElementById("b_7").style.display="none";
         }
         Element.show('date_panel');   
         var expr_editor=$('date_box');
          expr_editor.focus();
         }
         //else
        // {
         //     if(beforetype=='1')
         //     {
         //         document.getElementById("b_5").style.display="none";
         //     }else
        //      {
        ///          document.getElementById("b_3").style.display="none";
        //      }
       //  }
      
  	return false;
}  --%>

</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">

</head>
<body onKeyDown="return pf_ChangeFocus(event);" oncontextmenu = "return true" onclick="Element.hide('date_panel');">
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top">
       <hrms:insert parameter="HtmlBody" />
    </td>
  </tr>
</table>
</body>
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script> 
</html>