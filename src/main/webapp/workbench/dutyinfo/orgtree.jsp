<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
</script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script language="javascript">
function to_report_relations()
{
	var hashvo=new ParameterSet();
	
   	var request=new Request({asynchronous:false,onSuccess:dialogOk,functionId:'0405050028'},hashvo); 	
}
function dialogOk(outparamters)
{
	var result=outparamters.getValue("result");
	if(result=="yes")
	{
		var thecodeurl ="/pos/posreport/report_relations_tree.do?b_search=link&openwin=1";
		//var return_vo= window.showModalDialog(thecodeurl, "_top", "dialogWidth:850px; dialogHeight:550px; resizable:no; center:yes; scroll:yes; status:yes;");
//	   dutyInfoForm.action=thecodeurl;
//       dutyInfoForm.target="il_body";
//       dutyInfoForm.submit();
		openwin(thecodeurl);
	}else{
		alert("请先设置汇报关系参数");
	}
}
function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-100)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
</script>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<body style="margin:0px;">
<html:form action="/workbench/dutyinfo/editorginfo"> 
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" style="margin:0px;">
    	<tr>
           <td align="left"> 
           <div class="toolbar" style="width: 400px">
                 <!-- <html:button styleClass="mybutton" property="b_button" onclick="to_report_relations();">
				  <bean:message key="pos.info.report.relations"/>
				</html:button>  -->
				<input type="image" name="relations" src="/images/img_a.gif" alt="<bean:message key="pos.info.report.relations"/>" onclick="to_report_relations();">
			</div>
           </td>
           </tr>           
       <tr>
           <td align="left">  
            <div id="treemenu" style="height: expression(document.body.clientHeight-50);width:expression(document.body.clientWidth);overflow-x: hidden;overflow-y:auto;"> 
             <SCRIPT LANGUAGE=javascript>    
               <bean:write name="dutyInfoForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
</html:form>
</body>
