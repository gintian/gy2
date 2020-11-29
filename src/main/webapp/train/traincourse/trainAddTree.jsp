<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(userView!=null)
		manager=userView.getManagePrivCodeValue();  
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
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
<script language="javascript" src="/js/constant.js"></script>
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
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
<style>
 #treemenu{
 	height: 300px;
 	width: 100%;
 	overflow: auto;
 	border-style: inset;
 	border-width: 2px;
 }
   
</style>
<script language="JavaScript">     
   		function savecode()
   		{
    	   var currnode;
    	   currnode=Global.selectedItem;    	   
    	   if(currnode==null)
    	    	return;  
    	   if(currnode.uid==""||currnode.uid=="root")  
    	        return ;    	   
   			var thevo=new Object();
			thevo.flag="true";
			thevo.value=currnode.uid;
			thevo.text=currnode.text;
			window.returnValue=thevo;			
			window.close();
   	}
   	function showDateSelectBox(srcobj)
   {
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
        style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-date_desc.offsetHeight+42;
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.getElementById("selectname").value));
      hashVo.setValue("classid","${param.classid}");
      hashVo.setValue("r3702","${param.r3702}");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'1020010154'},hashVo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
		AjaxBind.bind($('date_box'),namelist);
   }
   function okSelect()
	{
	     var thevo=new Object();
	
	     var obj=document.getElementById("date_box");
	     var cont="";
	     var tit="";
	     var num=0;
	     if(obj)
	     {
	       for(var i=0;i<obj.options.length;i++)
	       {
	          if(obj.options[i].selected)
	          {
	             num++;
	             cont+=obj.options[i].value+",";
	             var temp=obj.options[i].text;
	             if(temp.indexOf("/")==-1)
	             {
	                if(temp.indexOf("(")==-1)
	                    tit+=temp+",";
	                else
	                   tit+=temp.substring(0,temp.indexOf("("))+",";
	             }
	             else
	             {
	                 var arr =temp.split("/");
	                temp=arr[arr.length-1];
	                 if(temp.indexOf("(")==-1)
	                    tit+=temp+",";
	                else
	                   tit+=temp.substring(0,temp.indexOf("("))+",";
	             }
	        }
	    }
	 }
	    
	  if(cont.length>0)
	  {
	     	cont=cont.substring(0,cont.length-1);
	    	tit=tit.substring(0,tit.length-1);
	  }  
	    
	 thevo.content=cont;
	 thevo.title=tit;
	 targetobj.value=tit;
	 targethidden.value=cont;
     window.close();
	}
   </SCRIPT>
</HEAD>
<hrms:themes/>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
	<table width="100%" border="0" cellspacing="1" align="center"
			cellpadding="1">
			<tr align="left">
				<td valign="top" nowrap>
					<bean:message key="column.name" />&nbsp;<Input type='text' class="text4" name='a_name' style="width: expression(document.body.clientWidth-50);" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' />
				</td>
			</tr>
			<tr>
				<td align="left">
				   <div id="treemenu" class="complex_border_color"></div>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding-top: 5px;">
				    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();window.close();">
				    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();">    
				</td>
			</tr>
		</table>
   <SCRIPT LANGUAGE=javascript> 
  	  var m_sXMLFile	= '/train/traincourse/get_code_tree.jsp?classid=${param.classid}&r3702=${param.r3702}';		
	  var root=new xtreeItem("root","代码项目","","","代码项目","/images/unit.gif",m_sXMLFile);
   	  Global.closeAction="savecode();window.close();";
   	  root.setup(document.getElementById("treemenu"));
   </SCRIPT> 
   <div id="date_panel" style="display: none;">
		<select id="date_box" name="contenttype"
			onblur="Element.hide('date_panel');" multiple="multiple"
			style="width: expression(document.body.clientWidth-60);" size="6" ondblclick="okSelect();">
		</select>
	</div>
<BODY>
</HTML>


