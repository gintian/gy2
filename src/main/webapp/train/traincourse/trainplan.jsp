<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	String checktemp=(String)request.getParameter("checktemp");
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <hrms:themes/>
   <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
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
</HEAD>
<script language="JavaScript">
function savecode(){
	var currnode = Global.selectedItem;	
    if(currnode==null || currnode.uid=="root")
    	return;   
    var codeitemid=currnode.uid;
    var codeitemdesc=currnode.text;
    returnValue=codeitemid+"`"+codeitemdesc;
    window.close();
}
function showDateSelectBox(srcobj)
   {
     ////  var targetobj=new Object();
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
      hashVo.setValue("classId",classId);
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'1020010153'},hashVo);
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
     returnValue=cont+"`"+tit;///zhangcq 2016/4/18
     window.close();
	}
</SCRIPT>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
 <table border="0" width="100%" cellpadding="0" cellspacing="0">
	<tr align="left">
		<td valign="top" nowrap>
			<bean:message key="column.name" />&nbsp;<Input type='text' name='a_name' style="width: 260px;" class="textColorWrite" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' />
		</td>
	</tr>
	<tr>
		<td align="left" style="margin: 0;padding: 5px 0 0 1px;">

		   <div id="treemenu" style="height: 300px; width: 290px; overflow: auto; border-width: 1px;" class="complex_border_color" ondblclick="savecode();"></div>

		</td>
	</tr>
   	<tr>
   		<td align="center" style="padding-top: 5px;">
   			<input type="button" value="确定" class="mybutton" onclick="savecode();">
   			<input type="button" value="取消" class="mybutton" onclick="window.close();">
   		</td>
   	</tr>
   		<tr><td height="30">&nbsp;</td></tr>
   <SCRIPT LANGUAGE=javascript> 
		     var classId=parent.dialogArguments;
		     var m_sXMLFile="/train/traincourse/trainplan_tree.jsp?classId="+classId;	 //
             var root=new xtreeItem("root","培训计划","","","培训计划","/images/open.png",m_sXMLFile);
             root.setup(document.getElementById("treemenu"));
   </SCRIPT>
   <div id="date_panel" style="display: none;">
			<select id="date_box" name="contenttype"
				onblur="Element.hide('date_panel');" multiple="multiple"
				style="width: expression(document.body.clientWidth-60);" size="6" ondblclick="okSelect();">
			</select>
		</div>
</table> 
<BODY>
</HTML>


