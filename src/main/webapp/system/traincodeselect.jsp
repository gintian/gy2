<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle,com.hrms.hjsj.sys.IResourceConstant"%>
<%!
	private String analyseManagePriv(String managed_str){
		if(managed_str.length()<3)
			return "";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
 		 String ids="";
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(sb.indexOf(id)==-1)
 						sb.append("','"+id.substring(2));
 				 }else{
 					 if(id.length()<ids.length()){
 						if(sb.indexOf(id)==-1)
 							sb.append("','"+id.substring(2));
 					 }
 				 }
 			 }
 		 }
 		if(sb.length()<4)
			return "";
		else
			return sb.substring(3);
	}
 %>
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
	String codevalue ="";
	String codesetid = request.getParameter("codesetid");
	
	// 目标卡制订专用参数  JinChunhai 2011.08.20
	String khtargetcard = request.getParameter("khtargetcard");
	
	if("65".equals(codesetid)||"64".equals(codesetid)){
		int res_type = IResourceConstant.PARTY;
		if("65".equals(codesetid))
			 res_type = IResourceConstant.MEMBER;
			 
		codevalue = userView.getResourceString(res_type);
		    	   if(codevalue.length()<3){
		    		   if(userView.isSuper_admin()&&!userView.isBThreeUser())
		    			   codevalue="ALL";
		    		   else{
		    			   if(codevalue.equals("64")||codevalue.equals("65"))
		    				   codevalue="ALL";
		    			   else
		    				   codevalue=""; 
		    		   }
		    	   }else{
		    		   codevalue=this.analyseManagePriv(codevalue);
		    			if(codevalue.length()<1)   
		    				codevalue="ALL";
		    		}
	}
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
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
        var input_code_id;  
   	function savecode()
   	{
   		   var khtargetcard = <%=khtargetcard %>
   		   
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;      
    	   
    	   // 目标卡制订专用  JinChunhai 2011.08.20  目的是在选择根节点时把编辑框置为空
    	   if((khtargetcard!=null && khtargetcard.length>0 && khtargetcard=='targetcard') && (codeitemid==null || codeitemid.length<=0 || codeitemid=="" || codeitemid=="root"))
    	   {
    	   		targetobj.value="";
    	        targethidden.value="";   	        		
    	        		
    	        try
    	        {
					if (navigator.appName.indexOf("Microsoft")!= -1) 
					{ 
						targethidden.fireEvent('onchange'); 
						//ie  
					}else
					{ 
						targethidden.onchange();  
					}  
				}catch(e)
				{}
    	        		
    	        window.close();	
    	        return ;
    	   }
    	   
    	   /*输入相关代码类和选择的相关代码一致*/    	 
    	    /*输入相关代码类和选择的相关代码一致*/      	    	   
    	   if(isAccord==1)
    	   {
    	      if(!((input_code_id=="UM")||(input_code_id=="@K")))
		      {
    	   	    if((input_code_id!=codeitemid.substring(0,2))&&codeitemid.substring(0,2)!="1_")
    	    	 return ;    	   
    	      }
	       }else if(isAccord==3)//兼职
	       {
	          if(codeitemid==""||(codeitemid.substring(0,2)!="UN"&&codeitemid.substring(0,2)!="UM"))
    	      {
    	        return ; //如果点击了根节点或者非想要选择的组织机构（部门和职位就只能选择部门和职位）   	   
    	      }
	       }
	       if((codeitemid=="" ||codeitemid=="root") && input_code_id=="UN"){
    	        targetobj.value="公共资源";
    	        targethidden.value="HJSJ";
    	   }else if(codeitemid=="" ||codeitemid=="root"){
    	   		targetobj.value="";
    	        targethidden.value="";
    	   }else{
    	   		if (Global.defaultInput == 1) {
    	   			if (root.getSelectedTitle().length > 0) {
    	   				var sele = root.getSelectedTitle().split(",");
    	   				for (var i = 0; i < sele.length; i++) {
    	   					if (sele[i].length <= 0) {
    	   						continue;
    	   					}
    	   					
    	   					if (i == 0) {
    	   						targetobj.value=sele[i];
    	   						
    	   					} else {
    	   						targetobj.value+=",";
    	   						targetobj.value+=sele[i];
    	   					}
    	   				}
    	   			}
    	   			
    	   			if (root.getSelected().length > 0) {
    	   				var sele = root.getSelected().split(",");
    	   				for (var i = 0; i < sele.length; i++) {
    	   					if (sele[i].length <= 0) {
    	   						continue;
    	   					}
    	   					
    	   					if (i == 0) {
    	   						targethidden.value=sele[i].substr(2);
    	   						
    	   					} else {
    	   						targethidden.value+=",";
    	   						targethidden.value+=sele[i].substr(2);
    	   					}
    	   				}
    	   			}
    	   			//targetobj.value=root.getSelectedTitle();
	    	   		//targethidden.value=root.getSelected();
    	   			//alert(root.getSelected()+"--"+root.getSelectedTitle());
    	   		} else {
	    	   		targetobj.value=currnode.text;
	    	   		targethidden.value=codeitemid.substring(input_code_id.length);
    	   		}
    	   }
    	   if((codeitemid=="" ||codeitemid=="root") && input_code_id=="1_06"){
    	   		targetobj.value="";
    	        targethidden.value="";
    	   		return;
    	   }else
    	   		window.close();	
    	   //targethidden.fireEvent("onchange"); //fzg added 20101214
    	  try{
    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
    	  		 targethidden.fireEvent('onchange'); 
		        //ie  
		    }else{ 
		        targethidden.onchange();  
		    }  
		}catch(e){
		}
		    	   
    	   //targetobj.fireEvent("onchange");  chenmengqing added 20070819,谁把它放开啦。。。
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
      hashVo.setValue("codeitemid","");
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
	body {  

	font-size: 12px;
	}
   </style>
</HEAD>
<hrms:themes></hrms:themes>
<div class="fixedDiv3">
	<table width="100%" border="0" cellspacing="1" align="center"
			cellpadding="1">
			<tr align="left">
				<td valign="top" nowrap>
					<bean:message key="column.name" />&nbsp;<Input class=text4 type='text' name='a_name' style="width: expression(document.body.clientWidth-45);" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' />
				</td>
			</tr>
			<tr>
				<td align="left">
				   <div id="treemenu" style="height: 300px;overflow: auto;width:100%;"  class="complex_border_color"></div>
				</td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td align="center">
				    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();">
				    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();" style="margin-left:0px;">    
				</td>
			</tr>
		</table>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=parent.dialogArguments; 
             var targetobj,targethidden;	
             var isAccord=0;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
             codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];  
             input_code_id=codesetid;
             var m_sXMLFile="/pos/posbusiness/train_get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=";             
             var root=new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();";
             <%if("68".equals(codesetid)){%>
             Global.defaultInput=1;//checkbox =2 radio
             <%}%>
             Global.showroot = false;
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
	<div id="date_panel" style="display: none;">
			<select id="date_box" name="contenttype"
				onblur="Element.hide('date_panel');" multiple="multiple"
				style="width: expression(document.body.clientWidth-60);" size="6" ondblclick="okSelect();">
			</select>
		</div>
</div>
</HTML>


