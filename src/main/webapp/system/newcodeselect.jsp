<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle,com.hrms.hjsj.sys.IResourceConstant"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
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
	
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
   String isMobile=request.getParameter("isMobile");
   isMobile = isMobile==null?"":isMobile;
   pageContext.setAttribute("isMobile",isMobile);
	
	String codevalue ="";
	String codesetid = request.getParameter("codesetid");
	String hirechannel = request.getParameter("hirechannel");
	
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
	var webserver=<%=flag%>;
	
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
        var input_code_id;
         var codesetid,codevalue,name,itemdesc,hirechannel;
         var targetobj,targethidden;
         var isAccord="<%=request.getParameter("isAccord") %>";  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
         codesetid = "<%=request.getParameter("codesetid") %>";
         codevalue = "<%=request.getParameter("codeitemid") %>";
         var mytarget="<%=request.getParameter("mytarget") %>";
         var isCheckBox="<%=request.getParameter("isCheckBox") %>";
         var oldInputs=parent.document.getElementsByName(mytarget); 
         //显示代码描述的对象
         targetobj=oldInputs[0];
       //根据代码显示的对象名称查找代码值名称	
         var target_name=targetobj.name;
         var hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
         hidden_name=hidden_name.replace(".viewvalue",".value");
         var hiddenInputs=parent.document.getElementsByName(hidden_name);
         if(hiddenInputs!=null)
         {
        	//代码值对象
             targethidden=hiddenInputs[0];
         }
         input_code_id=codesetid;   
        
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
	    
	    return values;
	}   
 
   	function savecode()
   	{
   		
   		   var khtargetcard = '<%=khtargetcard %>';
   		   var strs= new Array();  
    	   var currnode,codeitemid,str,code;
    	   var values = "";
    	   currnode=Global.selectedItem;
    	    
    	   if(currnode==null)
    	    	return;
    	   if(isCheckBox=='1'){   
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
    	        		
    	        parent.hidePopWin(false);	
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
	    	   if(isCheckBox=='1'){
	    	   		targetobj.value=getSelectedText();
	    	   		targethidden.value=codeitemid;
	    	   }
	    	   else
    	   	   {	
    	   	   		targetobj.value=currnode.text;
    	   			targethidden.value=codeitemid.substring(input_code_id.length);
    	   		}
    	   }
    	   if((codeitemid=="" ||codeitemid=="root") && input_code_id=="1_06"){
    	   		targetobj.value="";
    	        targethidden.value="";
    	   		return;
    	   }else
    	   		parent.hidePopWin(false);;	
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
   </SCRIPT>
   <style type="text/css">
	body {  

	font-size: 12px;
	}
   </style>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu" style="height: 270px;overflow: auto;border-style:inset ;border-width:2px"></div>
   <SCRIPT LANGUAGE=javascript>
         
         var m_sXMLFile="";
         var root;
         
         var hashvo = new ParameterSet();
         hashvo.setValue("codesetid",codesetid); 
         var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'1020010150'},hashvo);
         
           
	     function return_ok(parameters){
	        
	    	 itemdesc = parameters.getValue("itemdesc");
             hirechannel = "<%=hirechannel%>";
             m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=ALL";	 //
             if("64"==codesetid||"65"==codesetid)
             	m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=<%=codevalue %>&privflag=1";	 //
             if(hirechannel!=null && hirechannel!="" && hirechannel!='null')
                m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=ALL"+"&hirechannel="+hirechannel;
             if(itemdesc!=null && itemdesc!="" && itemdesc!='null')
                root=new xtreeItem("root",itemdesc,"","",itemdesc,"/images/add_all.gif",m_sXMLFile);
             else
                root=new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();";
             if(isCheckBox=="1"){ 
	             Global.defaultInput=1;
	             Global.showroot=false;
             }
             root.setup(document.getElementById("treemenu"));
	     
	     }
   </SCRIPT> 
   <br> 
   <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();">
    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="parent.hidePopWin(false);;">    
<BODY>
</HTML>


