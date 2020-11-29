<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page
	import="com.hrms.frame.utility.DateStyle,com.hrms.hjsj.sys.IResourceConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%!private String analyseManagePriv(String managed_str) {
		if (managed_str.length() < 3)
			return "";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
		String ids = "";
		for (int i = 0; i < strS.length; i++) {
			String id = strS[i];
			if (id != null && id.length() > 1) {
				boolean check = true;
				for (int j = 0; j < strS.length; j++) {
					String id_s = strS[j];
					if (id_s != null && id_s.length() > 1) {
						if (id.length() > id_s.length()) {
							if (id.substring(2, id.length()).startsWith(
									id_s.substring(2, id_s.length()))) {
								check = false;
								ids = id_s;
								break;
							}
						} else {
							if (id.equalsIgnoreCase(id_s)) {
								continue;
							}
							if (id_s.substring(2, id_s.length()).startsWith(
									id.substring(2, id.length()))) {
								check = false;
								ids = id_s;
								break;
							}
						}
					}
				}
				if (check) {
					if (sb.indexOf(id) == -1)
						sb.append("','" + id.substring(2));
				} else {
					if (id.length() < ids.length()) {
						if (sb.indexOf(id) == -1)
							sb.append("','" + id.substring(2));
					}
				}
			}
		}
		if (sb.length() < 4)
			return "";
		else
			return sb.substring(3);
	}%>
<%
	String css_url = "/css/css1.css";

	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String manager = "";
	if (userView != null)
		manager = userView.getManagePrivCodeValue();
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}

	String date = DateStyle.getSystemDate().getDateString();
	int flag = 1;
	String webserver = SystemConfig.getPropertyValue("webserver");
	if (webserver.equalsIgnoreCase("websphere"))
		flag = 2;

	String isMobile = request.getParameter("isMobile");
	isMobile = isMobile == null ? "" : isMobile;
	pageContext.setAttribute("isMobile", isMobile);

	String codevalue = "";
	String codesetid = request.getParameter("codesetid");
	String hirechannel = request.getParameter("hirechannel");
	String isValidCtr = request.getParameter("isValidCtr");
	isValidCtr = isValidCtr == null ? "" : isValidCtr;

	// 目标卡制订专用参数  JinChunhai 2011.08.20
	String khtargetcard = request.getParameter("khtargetcard");

	if ("65".equals(codesetid) || "64".equals(codesetid)) {
		int res_type = IResourceConstant.PARTY;
		if ("65".equals(codesetid))
			res_type = IResourceConstant.MEMBER;

		codevalue = userView.getResourceString(res_type);
		if (codevalue.length() < 3) {
			if (userView.isSuper_admin() && !userView.isBThreeUser())
				codevalue = "ALL";
			else {
				if (codevalue.equals("64") || codevalue.equals("65"))
					codevalue = "ALL";
				else
					codevalue = "";
			}
		} else {
			codevalue = this.analyseManagePriv(codevalue);
			if (codevalue.length() < 1)
				codevalue = "ALL";
		}
	}
%>
<HTML>
	<HEAD>
		<TITLE></TITLE>
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
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<script language="JavaScript">
	var input_code_id;
    function getSelectedText() {
	    var currnode, values,str;
	    values = "";
	    var strs= new Array(); 
	    var checkitems = document.getElementsByName("treeItem-check");
	    for (var i = 0; i < checkitems.length; i++) {
	            currnode = checkitems[i];
	            var rr = currnode.title;
	            if (currnode.checked) { 
                    str=rr.split(":")
	                values = values + str[1] + ",";
	            }
	    }
	    return values.substring(0,values.length-1);
	}   
    
    function closeWin(){
    		if(parent.Ext && parent.Ext.getCmp('openCodeDialog')){//ext 关闭弹窗   wangb 20190318
    			parent.Ext.getCmp('openCodeDialog').close();
    		}
    		else if(parent.Ext && parent.Ext.getCmp('openInputCodeDialogText')){//ext 关闭弹窗   wangb 20190318
    			parent.Ext.getCmp('openInputCodeDialogText').close();
    		}
    		else if(parent.parent.Ext){
    			parent.parent.Ext.getCmp("openCondCodeDialogWin").close();
    		}else{
	    		parent.window.close();
    		}
    }
    
   	function savecode()
   	{
   		
   		   var khtargetcard = '<%=khtargetcard%>';
   		   var paraArray;
   		   
   		   if(parent.Ext && parent.Ext.getCmp('openCodeDialog'))//ext 弹窗 传递参数  wangb 20190318
   		   	   paraArray = parent.Ext.getCmp('openCodeDialog').theArr;
   		   else if(parent.Ext && parent.Ext.getCmp('openInputCodeDialogText'))//ext 弹窗 传递参数  wangb 20190318
   		   	   paraArray = parent.Ext.getCmp('openInputCodeDialogText').theArr;
   		   else
   		   	   paraArray=window.dialogArguments || parent.parent.dialogArguments;
   		   var strs= new Array();  
    	   var currnode,codeitemid,str,code;
    	   var values = "";
    	   currnode=Global.selectedItem;
    	   //当前节点不为null且节点处于可选状态 canSelect为false时，为不可选,直接return；反之为可选
    	   if(currnode==null || currnode.selectable =='false'){
    	   		alert("此代码类中只有末级代码项可以选!");
    	    	return;
    	   }   	   
    	   if(paraArray.length==6&&paraArray[5]=='1'){   
    	  	 code=currnode.getSelected();
    	  	 strs =code.split(",");
    	  	  for(var z=0;z<strs.length;z++)
		      {
		         str = strs[z];	
		         if(str.length>codesetid.length&&str.substring(0,codesetid.length)==codesetid){		       
		          	values = values + str.substring(codesetid.length,str.length)+",";		         
		         }else{
		         	values = values + str;		          		    
		         }
		         					
	          }
    	  	codeitemid = values;
    	  
    	   }else{
    	   
    	  	 codeitemid=currnode.uid;
    	  	 
    	   }    
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
    	        		
    	       closeWin();	
    	        return ;
    	   }
    	   /*输入相关代码类和选择的相关代码一致*/    	 
    	    /*输入相关代码类和选择的相关代码一致*/      	    	   
    	   if(isAccord==1)
    	   {
    	      if(!((input_code_id=="UM")||(input_code_id=="@K")))
		      {
    		    if(input_code_id=="55_1"||input_code_id=="55_2")
    		    	input_code_id=input_code_id.substring(0,2);
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
	    	   if(paraArray.length==6&&paraArray[5]=='1'){
	    	   		targetobj.value=getSelectedText();
	    	   		targethidden.value=codeitemid;
	    	   }
	    	   else
    	   	   {	
    	   	   		targetobj.value=currnode.text;
    	   	   		//【55017】V76报表管理：编辑报表，编辑报表参数，修改保存后，悬浮的内容还是修改之前的
    	   	   		targetobj.title=currnode.text;
    	   	   		
    	   	   		if(input_code_id=="55_1"||input_code_id=="55_2")
		    			input_code_id=input_code_id.substring(0,2);
    	   			targethidden.value=codeitemid.substring(input_code_id.length);
    	   		}
    	   }
    	   if((codeitemid=="" ||codeitemid=="root") && input_code_id=="1_06"){
    	   		targetobj.value="";
    	        targethidden.value="";
    	   		return;
    	   }else
    	   		closeWin();	
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
		style.left=pos[0];
		style.top=pos[1]-date_desc.offsetHeight+42;
		style.width=((date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1)+"px";
      }
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.getElementById("selectname").value));
      hashVo.setValue("codesetid",codesetid);
      hashVo.setValue("allcode","yes");
      if(codesetid=="55_1"||codesetid=="55_2"){
	      hashVo.setValue("codeitemid",codevalue);
      }else if("64"==codesetid||"65"==codesetid){
		  hashVo.setValue("codeitemid","<%=codevalue%>");
		  hashVo.setValue("privflag","1");
      }else if(hirechannel!=null && hirechannel!="" && hirechannel!='null'){
		  hashVo.setValue("codeitemid","ALL");
		  hashVo.setValue("hirechannel",hirechannel);
      }else{
	      hashVo.setValue("codeitemid","ALL");
	      hashVo.setValue("isValidCtr","<%=isValidCtr%>");
      }
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
     closeWin();
	}
	 
   </SCRIPT>
		<style type="text/css">
body {
	font-size: 12px;
}
</style>
    <hrms:themes />
	</HEAD>
	<body topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr align="left">
				<td valign="top" nowrap>
					<bean:message key="column.name" /><Input type='text' name='a_name' style="width: 264px;margin-left:5px;" id="selectname" onkeyup="showDateSelectBox('selectname')" title='' class="text4"/>
				</td>
			</tr>
			<tr>
				<td align="left" style="padding-top:4px;">
					<div id="treemenu"
						style="height: 301px; width: 290px; overflow: auto;" class="complex_border_color"></div> <!-- 将height的值从300px改为301px，可以避免正好11个代码项时出现滚动条  liuzy 20150713 -->

				</td>
			</tr>
			<tr>
				<td height="5px"></td>
			</tr>
			<tr>
				<td align="center">
					<input type="button" name="btnok"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick="savecode();">
						<input type="button" name="btncancel"
						value='<bean:message key="button.cancel"/>' class="mybutton"
						onclick="closeWin();" style="margin-left:0px;">
				</td>
			</tr>
		</table>
		<SCRIPT LANGUAGE=javascript>
         var codesetid,codevalue,name,itemdesc,hirechannel;
         var paraArray;
   		 
   		 if(parent.Ext && parent.Ext.getCmp('openCodeDialog'))//ext 弹窗 传递参数  wangb 20190318
   		   paraArray = parent.Ext.getCmp('openCodeDialog').theArr;
   		 else if(parent.Ext && parent.Ext.getCmp('openInputCodeDialogText'))//ext 弹窗 传递参数  wangb 20190318
   		   paraArray = parent.Ext.getCmp('openInputCodeDialogText').theArr;
   		 else
   		   paraArray=window.dialogArguments || parent.parent.dialogArguments;
         var targetobj,targethidden;
         var isAccord=0;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
         codesetid = paraArray[0];
         codevalue = paraArray[1];
         //显示代码描述的对象
         targetobj=paraArray[2];
         //代码值对象
         targethidden=paraArray[3];  
         input_code_id=codesetid;
         var m_sXMLFile="";
         var root;
         
         var hashvo = new ParameterSet();
         hashvo.setValue("codesetid",codesetid); 
         var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'1020010150'},hashvo);
         
           
	     function return_ok(parameters){
	        
	         //var codesetid,codevalue,name,itemdesc,hirechannel;
             //var paraArray=parent.dialogArguments; 
             //var targetobj,targethidden;	
             //var isAccord=0;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
            // codesetid = paraArray[0];
             //codevalue = paraArray[1];
             //显示代码描述的对象
             //targetobj=paraArray[2];
             //代码值对象
            // targethidden=paraArray[3];  
            // input_code_id=codesetid;
             itemdesc = parameters.getValue("itemdesc");
             hirechannel = "<%=hirechannel%>";
             if(codesetid=="55_1"||codesetid=="55_2")
            	 m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid="+codevalue;
             else
             	m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=ALL&isValidCtr=<%=isValidCtr%>";	 //
             if("64"==codesetid||"65"==codesetid)
             	m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=<%=codevalue%>&privflag=1";	 //
             if(hirechannel!=null && hirechannel!="" && hirechannel!='null')
                m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=ALL"+"&hirechannel="+hirechannel;
             if(paraArray.length==5)
              	isAccord=paraArray[4];
             if(itemdesc!=null && itemdesc!="" && itemdesc!='null')
                root=new xtreeItem("root",itemdesc,"","",itemdesc,"/images/add_all.gif",m_sXMLFile);
             else
                root=new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();";
             if(paraArray.length==6&&paraArray[5]=="1"){ 
	             Global.defaultInput=1;
	             Global.showroot=false;
             }
             root.setup(document.getElementById("treemenu"));
             ///if(hirechannel!=null && hirechannel!="" && hirechannel!='null')
                ///root.expandAll();  //root.expandAll();一次展开所有代码类太慢 故注释掉
	     
	     }
   </SCRIPT>
		<div id="date_panel" style="display: none;">
			<select id="date_box" name="contenttype"
				onblur="Element.hide('date_panel');" multiple="multiple"
				size="6" ondblclick="okSelect();">
			</select>
		</div>
	</BODY>
	
	<script type="text/javascript">
		var date_box = document.getElementById("date_box");
		if(date_box){
			var width="150px";
			if(date_panel){
				width = document.body.clientWidth-40;
			}
			
			date_box.style.width=width;		
		}
	</script>
</HTML>


