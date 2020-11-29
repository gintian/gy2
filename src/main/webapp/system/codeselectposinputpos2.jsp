<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
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
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
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
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
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
        var input_code_id;
        var selfForm;
   	function savecode()
   	{
      	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   //当前节点不为null且节点处于可选状态 canSelect为false时，为不可选,直接return；反之为可选
    	   if(currnode==null || currnode.selectable =='false'){
    	   		alert("此代码类中只有末级代码项可以选!");
    	    	return;
    	   }   
    	   codeitemid=currnode.uid;
    	    /*输入相关代码类和选择的相关代码一致
    	   if(isAccord==1)
    	   {
	    	   if(input_code_id!=codeitemid.substring(0,2)&&!flag)
	    	     return ;  
	    	   if(input_code_id!=codeitemid.substring(0,2)&&(flag=="1" || flag=="2"))
	    	     return ; 
	    	} 	*/   
    	   targetobj.value=currnode.text
    	   targethidden.value = codeitemid;
	       window.close();//add by xiegh bug35675
    	   //alert(codeitemid.substring(0,2));	
    	   /*if(!flag)
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
    	  targethidden.fireEvent("onchange"); */ //chenmengqing changed at 200605
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
     window.close();
	}
   </SCRIPT>
   <style type="text/css">
	#treemenu {  
	height: 300px;
	overflow: auto;
	border-style:inset ;
	border-width:2px;
	width: 290px;
	}
   </style>   
</HEAD>
<hrms:themes></hrms:themes>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
<table width="100%" border="0" cellspacing="1" align="center"
			cellpadding="1">
			<tr align="left">
				<td valign="top" nowrap>
					<bean:message key="column.name" /><Input class=text4 type='text' name='a_name' style="width: expression(document.body.clientWidth-60);" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' />
				</td>
			</tr>
			<tr>
				<td align="left">
				   <div id="treemenu" style="height: 300px;overflow: auto;border: solid 1px;" class="complex_border_color"></div>
				</td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td align="center">
				    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
				    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();" style="margin-left:0px;">    
				</td>
			</tr>
		</table>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
             var targetobj,targethidden;
             var flag;	//输入代码类和选中代码类是否需要相同
             var isAccord=1;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
                //代码值对象    
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
            var m_sXMLFile	= "/system/get_code_treeinputinfo2.jsp?codesetid="+codesetid;	 
			 var root=new xtreeItem("root","培训资料","","","培训资料","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
   <div id="date_panel" style="display: none;">
		<select id="date_box" name="contenttype"
			onblur="Element.hide('date_panel');" multiple="multiple"
			style="width: expression(document.body.clientWidth-60);" size="6" ondblclick="okSelect();">
		</select>
	</div>
</body>
</HTML>


