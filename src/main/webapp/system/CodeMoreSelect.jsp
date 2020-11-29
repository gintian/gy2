<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
    String css_url="/css/css1.css";
	String bosflag="";
	String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(userView!=null)
		manager=userView.getManagePrivCodeValue();  
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  {
	   css_url="/css/css1.css";
	   bosflag=userView.getBosflag();   
	   themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	  }
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
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
        var input_code_id;        
   	function savecode2()
   	{
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;      
           
    	   /*输入相关代码类和选择的相关代码一致*/    	 
    	    /*输入相关代码类和选择的相关代码一致*/      	    	   
    	   if(isAccord==1)
    	   {
    	      if(!((input_code_id=="UM")||(input_code_id=="@K")))
		      {
    	   	    if((input_code_id!=codeitemid.substring(0,2))&&codeitemid.substring(0,2)!="1_")
    	   	    {
    	   	      targetobj.value="";
    	   	      targethidden.value="";
    	   	      return ;  
    	   	    }
    	    	   	   
    	      }else if(codeitemid=="" ||codeitemid=="root" || codeitemid.substring(0,input_code_id.length)!=input_code_id)
    	      {
    	        targetobj.value="";
    	   	    targethidden.value="";
    	        return ; //如果点击了根节点或者非想要选择的组织机构（部门和职位就只能选择部门和职位）   	   
    	      }
	       }else if(isAccord==3)//兼职
	       {
	          if(codeitemid==""||(codeitemid.substring(0,2)!="UN"&&codeitemid.substring(0,2)!="UM"))
    	      {
    	        targetobj.value="";
    	   	    targethidden.value="";
    	        return ; //如果点击了根节点或者非想要选择的组织机构（部门和职位就只能选择部门和职位）   	   
    	      }
	       }
    	   targetobj.value=currnode.text;
    	   targethidden.value=codeitemid.substring(input_code_id.length);
    	   //targetobj.fireEvent("onchange");  chenmengqing added 20070819,谁把它放开啦。。。	
   	}
   </SCRIPT>
   <style type="text/css">
	body {  

	font-size: 12px;
	}
   </style>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
	<table width="100%" border="0" cellspacing="1" align="center"
			cellpadding="0">
			<tr align="left">
				<td valign="top" nowrap>
					<bean:message key="column.name" />&nbsp;<Input type='text' class="text4" name='a_name' style="width: expression(document.body.clientWidth-40);" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' />
				    <div id="date_panel" style="display: none;">
		            <select id="date_box" name="contenttype"
		                onblur="Element.hide('date_panel');" multiple="multiple"
		                style="width: expression(document.body.clientWidth-40);" size="6" ondblclick="okSelect();">
		            </select>
		        </div>
				</td>
			</tr>
			<tr>
				<td align="left" style="padding-top: 3px;">
					<div id="treemenu" style="height: 300px;width:290px;;overflow: auto;border-style:solid ;border-width:1px;" class="complex_border_color"></div>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding-top: 5px;">
				    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();">
				    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="winClose();">
				    <input type="button" name="btncancel" value='清空' class="mybutton" onclick="clear_C();winClose();">      
				</td>
			</tr>
		</table>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray; 
             //19/3/23 xus 历史时点-查询-高级 机构查询图标谷歌不弹窗
             if(!window.showModalDialog){
            	 if(parent.parent.Ext.getCmp('history_select_code'))
              	 	paraArray = parent.parent.Ext.getCmp('history_select_code').dialogArguments;
             }else{
                 paraArray =  parent.window.dialogArguments || parent.dialogArguments|| parent.parent.dialogArguments; 
             }
             var targetobj,targethidden;	
             var isAccord=paraArray[4];  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  0：需要  1：不需要 3:兼职
             if(!isAccord)
            	 isAccord = 1;
        	 
             codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];  
             var type = paraArray[5];  
             input_code_id=codesetid;
             var m_sXMLFile="/system/GetCodeTreeMore.jsp?codesetid="+codesetid+"&codeitemid=ALL";
             if (Global.defaultInput == 1) {
	             var codevalues = targethidden.value.split(",");
	             var codes = "";
	             for (i = 0; i < codevalues.length; i++) {
	             	codes += ("," + codesetid + codevalues[i]);
	             }
	             Global.checkvalue= "," + codes + ",";
             } else {
             	Global.checkvalue= codesetid + targethidden.value ;
             }
             
             var root;
			 if("UN" == codesetid || "UM" == codesetid || "@K" == codesetid)
             	root = new xtreeItem("root","组织机构","","","组织机构","/images/add_all.gif",m_sXMLFile);
			 else
             	root = new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
             
             Global.closeAction="savecode();winClose();";
             Global.defaultInput=1;
             root.setup(document.getElementById("treemenu"));
             var hiddenValues = targethidden.value;
             if(!hiddenValues)
            	 hiddenValues = "";
             
             hiddenValues = replaceAll(hiddenValues, "｜", "|");
   		 	 var selectValues = replaceAll(hiddenValues,"|","," + codesetid);
     		 if(selectValues)
     			selectValues = codesetid + selectValues;
     			
             Global.checkvalue = "," + selectValues + ",";
	     
   </SCRIPT>
<BODY>
<script type="text/javascript">
var selectTitles = targetobj.value;
if(!selectTitles)
	selectTitles = "";
	
selectTitles = replaceAll(selectTitles, "｜", "|");
function savecode() {
		addOrRemoveCode();
		var val= root.getSelected();
		val = sub(val);
		var reg = new RegExp(",","g");

		if(val == "false" || !val){
			if(hiddenValues) {
				targethidden.value=hiddenValues.substring(1).replace(reg,"|");	;
				targetobj.value= selectTitles.substring(0, selectTitles.length - 1);
				winClose();
				return false;
			} else
				return false;
		} 
		
		var titles = root.getSelectedTitle();
		if (Global.defaultInput == 1) {
			val = (hiddenValues + val).replace(reg,"|");	
			targethidden.value=val.substring(1,val.length -1);
			var titleArr = titles.split(",");
			var title = "|" + selectTitles;
			for (i = 0; i < titleArr.length; i++) {
				var tit = titleArr[i].split(":");
				if(!tit[0] || val.indexOf("|" + tit[0] + "|") < 0)
					continue;
					
				if (tit[1] && title.indexOf("|" + tit[1] + "|") < 0) {
					title += tit[1] + "|";
				}
			}

			if (title.length > 0) {
				targetobj.value = title.substring(1,title.length - 1);
			} else {
				targetobj.value="";
			}
			
		} else {		
			savecode2();
		}

		winClose();
    }
 function clear_C()
 {
       targetobj.value="";
       targethidden.value=""; 
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
      hashVo.setValue("allcode","yes");
      hashVo.setValue("codesetid",codesetid);
      hashVo.setValue("codeitemid","ALL");
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
     winClose();
	}

	function sub(value){
		if(!value)
			return "";

		var hiddenValue = "," + replaceAll(hiddenValues,"|",",") + ",";
		var values = value.split(",");
		value = "";
		for(var i = 0; i < values.length; i++){
			var v = values[i];
			if(!v)
				continue;

			if(isAccord==3 && (codeitemid==""||(v.substring(0,2)!="UN" && v.substring(0,2)!="UM")))
				continue;
			
			if(isAccord==1 || (codesetid && (v.indexOf(codesetid) == 0 || 
					("2" == type && v.indexOf("UN") == 0) || ("3" == type && v.indexOf("UM") == 0))))
				v = v.substr(codesetid.length, v.length);
			else
				continue;

			if(hiddenValue.indexOf(v) > -1)
				continue;
			
			value += "," + v; 	
		}

		if(!value)
			value = "false";
		else
			value += ","; 

		return value;
		
	}

function addOrRemoveCode(){
	var selectTitle = selectTitles.split("|");
	var hiddenValue = hiddenValues.split("|");
	var removeCodeValues = Global.removeCodeValues;
	if(!removeCodeValues)
		removeCodeValues = "";
	
	selectTitles = "";
	hiddenValues = "";
	
	if(codesetid)
		removeCodeValues = replaceAll(removeCodeValues, "," + codesetid,",");
	
	for(var i = 0; i < hiddenValue.length;i++){
		if(!hiddenValue[i])
			continue;

		if(removeCodeValues.indexOf("," + hiddenValue[i] + ",") < 0){
			hiddenValues += "," + hiddenValue[i];
			selectTitles += selectTitle[i] + "|";
		}
	}
}
//19/3/23 xus 浏览器兼容 关闭窗口
function winClose(){
	if(!window.showModalDialog){
		if(parent.parent.Ext.getCmp('history_select_code'))
			parent.parent.Ext.getCmp('history_select_code').close();
	}else{
		window.close();
    }
}

if(!getBrowseVersion()){
	document.getElementById("date_box").style.marginLeft="27px";
}
</script>
</HTML>