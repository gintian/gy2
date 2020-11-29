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
				</td>
			</tr>
			<tr>
				<td align="left" style="padding-top: 3px;">
					<div id="treemenu" style="height: 300px;width:290px;;overflow: auto;border-style:solid ;border-width:1px;" class="complex_border_color"></div>
				</td>
			</tr>
			<tr>
				<td align="center" style="padding-top: 5px;">
				    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();window.close();">
				    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();">
				    <input type="button" name="btncancel" value='清空' class="mybutton" onclick="clear_C();window.close();">      
				</td>
			</tr>
		</table>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
             var targetobj,targethidden;	
             var isAccord=1;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
             codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];   
             input_code_id=codesetid;
             var m_sXMLFile="/system/get_code_tree_train.jsp?codesetid="+codesetid+"&codeitemid=ALL"+"&flag="+paraArray[5];	 //
             try {
             eval(paraArray[4]);
             } catch(e) {
             	alert("属性设置错误！");
             }
             
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
             
             var root=new xtreeItem("root","组织机构","","","组织机构","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             Global.defaultInput=1;
             root.setup(document.getElementById("treemenu"));
             
	     
   </SCRIPT>
   <div id="date_panel" style="display: none;">
			<select id="date_box" name="contenttype"
				onblur="Element.hide('date_panel');" multiple="multiple"
				style="width: expression(document.body.clientWidth-40);" size="6" ondblclick="okSelect();">
			</select>
		</div>
<BODY>
<script type="text/javascript">
function savecode() {
		var val= root.getSelected();
		var titles = root.getSelectedTitle();
		if (Global.defaultInput == 1) {
			var reg = new RegExp(",","g");
			val = "," + val;			
			val = val.replace(reg,",");			
			targethidden.value=val.substring(1,val.length -1);
			var titleArr = titles.split(",");
			var title = "";
			for (i = 0; i < titleArr.length; i++) {
				var tit = titleArr[i].split(":");
				if (tit[1]) {
					title += "," + tit[1];
				}
			}
			if (title.length > 0) {
				targetobj.value=title.substring(1,title.length);
			} else {
				targetobj.value="";
			}
		} else {		
			savecode2();
		}
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
      hashVo.setValue("flag","ture");
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
</script>
</HTML>


