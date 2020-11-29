<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
	String isMobile=request.getParameter("isMobile");
	isMobile = isMobile==null?"":isMobile;
	pageContext.setAttribute("isMobile",isMobile);
	String ctrl_type = request.getParameter("ctrl_type");
	String levelctrl = request.getParameter("levelctrl");
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
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
	var webserver=<%=flag%>;
	
</script>



   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
   var input_code_id;
   var selfForm;
	function savecode()
	{
 	   var currnode,codeitemid;
	   currnode=Global.selectedItem;	
	   if(currnode==null)
	    	return;   
	   codeitemid=currnode.uid;
	   
   	   if(codeitemid=="" ||codeitemid=="root"){
	   		targetobj.value="";
	        targethidden.value="";
	   }else{
   	//alert(currnode.text);
		   targetobj.value=currnode.text
		   //alert(codeitemid.substring(0,2));	
		   if(!flag)
			     targethidden.value=codeitemid.substring(2);
		   else
		   {
			     if(flag=="query")
			        targethidden.value=codeitemid.substring(2);    	     
			     else if(flag=="1" || flag=="2")
			        targethidden.value=codeitemid.substring(2);   
			     else
			     	targethidden.value=codeitemid;
		   }
	   }
	   try{
   	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
   	   		targethidden.fireEvent('onchange'); 
		         //ie  
		    }else{ 
		        targethidden.onchange(); 
		    }  
		}catch(e){
		}
		
		winclose();
	   
	  //targethidden.fireEvent("onchange");  //chenmengqing changed at 200605
	  //top.close();
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
      hashVo.setValue("codesetid",codesetid);
      hashVo.setValue("codeitemid",codevalue);
      hashVo.setValue("isfirstnode",flag);
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'1020010151'},hashVo);
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
     winclose();
	}
	
	//关闭弹窗方法 wangb 201903014	 
	function winclose(){
		if(parent.Ext){
			parent.Ext.getCmp('code_dialog').close();
		}else{
			window.close();
		}
   }
   </SCRIPT>
   <style type="text/css">
	#treemenu {  
	height: 300px;
	overflow: auto;
	width:auto;
	/*border-style:inset ;
	border-width:2px*/
	}
   </style>   
</HEAD>
<hrms:themes/>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
<div class="fixedDiv3">
<SCRIPT LANGUAGE=javascript>
	
	var codesetid,codevalue,name;
	var paraArray;
	paraArray=parent.dialogArguments; 
	var targetobj,targethidden;
	var flag;	//输入代码类和选中代码类是否需要相同
		   //代码值对象    
	
	if(parent.Ext){
		var extWin = parent.Ext.getCmp('code_dialog');
		paraArray = extWin.params;
	}else{
		paraArray=parent.dialogArguments;
	}
	codesetid = paraArray[0];
	codevalue = paraArray[1];
	//显示代码描述的对象
	targetobj=paraArray[2];
	//代码值对象
	targethidden=paraArray[3];
	flag=paraArray[4];
	input_code_id=codesetid; 
	if(paraArray.length==6)
		 isAccord=paraArray[5];
</SCRIPT>      
 <table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="1">
			<tr align="left">
				<td valign="top" nowrap>
					<bean:message key="column.name" /><Input class=text4 type='text' name='a_name' style="width: 255px;margin-left:5px;" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' />
				</td>
			</tr>
			<tr>
				<td align="left">
				  <hrms:orgtree   flag="0"  loadtype="<%=ctrl_type %>" priv="1" showroot="false" viewunit="1" nextlevel="<%=levelctrl %>" divStyle="height: 300px;overflow: auto;" rootaction="0" nmodule="4"/>

				</td>
			</tr>
			<tr>
				<td align="center" height="35px;">
				    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
				    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="winclose();" style="margin-left:0px;">    
				</td>
			</tr>
		</table>  
		<div id="date_panel" style="display: none;">
			<select id="date_box" name="contenttype"
				onblur="Element.hide('date_panel');" multiple="multiple"
				style="width: expression(document.body.clientWidth-45);" size="6" ondblclick="okSelect();">
			</select>
		</div>  
<BODY>
</HTML>


