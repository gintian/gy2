<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String showDb="0";
	if(request.getParameter("showDb")!=null)
		showDb=request.getParameter("showDb");
		
	String showSelfNode="0";
	if(request.getParameter("showSelfNode")!=null)
		showSelfNode=request.getParameter("showSelfNode");
		String level ="-1";
			if(request.getParameter("level")!=null)
		level=request.getParameter("level");	//该参数可以控制单选按钮，比如所属单位  xgq20101108
		String viewunit = "";
			if(request.getParameter("viewunit")!=null&&("0".equals(request.getParameter("viewunit"))||"1".equals(request.getParameter("viewunit"))))
		viewunit=request.getParameter("viewunit");	//该参数可以控制按操作单位或管理范围进行展现，1为操作单位，0为管理范围  xgq20101108
    String flag="0";
    if(request.getParameter("flag")!=null)
        flag=request.getParameter("flag");
    String selecttype="0";
    if(request.getParameter("selecttype")!=null)
    {
        selecttype=request.getParameter("selecttype");
    }
    String dbpre="usr";
     if(request.getParameter("dbpre")!=null)
    {
        dbpre=request.getParameter("dbpre");
    }
    String priv="1";
    if(request.getParameter("priv")!=null)
    {
        priv=request.getParameter("priv");
    }
    
    String tabid="-1";
    if(request.getParameter("tabid")!=null)  //人事异动模板id
    {
        tabid=request.getParameter("tabid");
    }
    
    String isfilter="0";
    if(request.getParameter("isfilter")!=null)
    {
        isfilter=request.getParameter("isfilter");
    }
    String SYS_FILTER_FACTOR="";
    if(isfilter.equals("1"))
    	SYS_FILTER_FACTOR=SafeCode.encode((String)request.getSession().getAttribute("SYS_FILTER_FACTOR"));
    String nmodule = "";
    if(request.getParameter("nmodule")!=null)
    {
    	nmodule=request.getParameter("nmodule");
    }
    
    String dbtype="0";
     if(request.getParameter("dbtype")!=null)
    {
        dbtype=request.getParameter("dbtype");
    }
    String checklevel = "3";
    if(request.getParameter("checklevel") != null) {
        checklevel = request.getParameter("checklevel");
    }
    //为人事异动姓名查询列增加提示信息
    String generalmessage = "";
     if(request.getParameter("generalmessage") != null) {
        generalmessage = request.getParameter("generalmessage");
        generalmessage = SafeCode.decode(generalmessage);
    }
     String needboxcheck = "";
     if(request.getParameter("needboxcheck") != null){
         needboxcheck = request.getParameter("needboxcheck");
     }
     
     String checktitle="";
     if(request.getParameter("checktitle") != null){
         checktitle = request.getParameter("checktitle");
     }
     String cascade="false";
     if(request.getParameter("cascade") != null){
    	 cascade = request.getParameter("cascade");
     }
     String prompt= "0";//tiany 添加prompt参数 用来判断是否提示用户更新或是追加选择的机构
     if(request.getParameter("prompt") != null){
    	 prompt = request.getParameter("prompt");
     }
     String dbvalue="";
     if(request.getParameter("dbvalue") != null){
    	 dbvalue = request.getParameter("dbvalue");
     }
     //是否使用考勤权限，看到orgtreetag里面有privtype属性，直接拿来用 wangrd 20151130
     String privtype="";
     if(request.getParameter("privtype") != null){
    	 privtype = request.getParameter("privtype");
     }

     //传入已选单位 格式: ,UN01,UM01011, zhanghua 2018-06-13
     String selectedValues="";
    if(request.getParameter("selectedValues") != null){
        selectedValues = request.getParameter("selectedValues");
    }
     
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script LANGUAGE=javascript src="/js/validate.js"></script> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script type="text/javascript">
	var selecttype='<%=selecttype%>';
	
	//Global.needboxcheck = '<%=needboxcheck%>';
	function closeWin(){
		if(parent.parent.Ext){//非IE浏览器关闭弹窗方法
				if(parent.parent.Ext.getCmp('ps_parameter_getOrg'))
					parent.parent.Ext.getCmp('ps_parameter_getOrg').close();
				else if(parent.parent.Ext.getCmp("select_kpiorg_dialog_win"))
					parent.parent.Ext.getCmp("select_kpiorg_dialog_win").close();
				else if(parent.parent.Ext.getCmp("select_org_emp"))
					parent.parent.Ext.getCmp("select_org_emp").close();
				else if(parent.parent.Ext.getCmp("getSelectedEmploy_showModalDialogs"))
					parent.parent.Ext.getCmp("getSelectedEmploy_showModalDialogs").close();
		}else if(parent.parent.window){
			parent.parent.window.close();
		}else{//ie浏览器关闭弹窗防范 wangb 20190318
			parent.window.close();
		}
	}
	
	function getemploy()
	{
		var thevo=new Object();
		
		/*
		if(Global.needboxcheck == "1"){
			var checkvalue = "";
			var checktitle = "";
			for(var i=0;i<checkvalues.length;i++){
				if(checkvalues[i].length<1)
					continue;
				checkvalue+=checkvalues[i]+",";
				checktitle+=checktitles[i]+",";
			}
			thevo.content = checkvalue;
			thevo.title = checktitle;
			 window.returnValue=thevo;
		     window.close();
		     return;
		}
		*/
	/*
     var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;
     if(iconurl!="/images/man.gif") 
       return;    
	 window.returnValue=currnode.uid;
	 */
	 var prompt ="<%=prompt%>";
	 var data="<%=selectedValues %>"==""?"${ logonUserForm.checkvalue }":"<%=selectedValues %>";
	 var bestrow = true ;// 是否覆盖原来的机构
	 if(prompt=="1"&&data!=""){
		 if(window.execScript)
	 		execScript("n = msgbox('您是否在原有的机构上追加机构？',3,'提示')","vbscript");
		 else
			n = 7;
 
       switch(n){ 
       
         case  6:// 点击是 不覆盖 （追加）
         	bestrow = false;
         break; 
         
         case   7:  //点击否  覆盖（不追加）
          	bestrow = true;
         break;
             
         return;
         case   2:     
         return;
      }
	 }
   
	 if(trim(root.getSelected()).length==0)
	 {
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
	     if(selecttype=='2'&&cont.length>0)
	     {
	     	cont=cont.substring(0,cont.length-1);
	    	tit=tit.substring(0,tit.length-1);
	     }
	     if(!bestrow){
	 	 	cont =	deleteRepeat(cont,data);
	 	 	//更新节点，去掉移除的节点  wangb  20180420  bug 36668
	 	 	cont =  removeRepeat(Global.removeCodeValues,cont);
	 	 	tit = deleteRepeat("${ logonUserForm.checktitle }",tit);
	 	 	Global.removeCodeTitles = Global.removeCodeTitles == undefined? '':Global.removeCodeTitles;
	 	 	tit = removeRepeat(Global.removeCodeTitles,tit);
	 	}
	     thevo.content=cont;
	     thevo.title=tit;
	     thevo.bestrow = bestrow;
	 }
	 else
	 {
	 var cont ;
	 if(!bestrow){
	    cont =removeRepeat(Global.removeCodeValues,data);
	    cont =	deleteRepeat(cont,root.getSelected(),1);
	    Global.removeCodeTitles = Global.removeCodeTitles == undefined? '':Global.removeCodeTitles;
	 	tit = removeRepeat(Global.removeCodeTitles,"${ logonUserForm.checktitle }");
	 	tit = deleteRepeat(tit,root.getSelectedTitle(),2);
	 	/*
	      cont =	deleteRepeat(root.getSelected(),"${ logonUserForm.checkvalue }",1);
	      //更新节点，去掉移除的节点  wangb  20180420  bug 36668
	      cont =  removeRepeat(Global.removeCodeValues,cont);
	 	  tit = deleteRepeat("${ logonUserForm.checktitle }",root.getSelectedTitle(),2);
	 	  Global.removeCodeTitles = Global.removeCodeTitles == undefined? '':Global.removeCodeTitles;
	 	  tit = removeRepeat(Global.removeCodeTitles,tit);
	 	  */
	 	  if(addCodeCount != addTitleCount){
	 	  	tit = tit +repeatTitle;
	 	  	tit = tit.replace(',,',',');
	 	  }
	     }else{
	     cont= root.getSelected();
	     tit =root.getSelectedTitle();
	     }
	     thevo.content=cont;
	     thevo.title=tit;
	     thevo.bestrow = bestrow;
	 }
	 //培训教师 内部 选人时不需要进行人员是否关联了业务帐号的校验 chenxg 2019-08-15
	 if("<%=nmodule%>" != "6" && !checkStatus(thevo))
	 	return false;
	 
	 if(parent.parent.Ext){//非IE浏览器  ext弹窗 方法
	 	if(parent.parent.Ext.getCmp('ps_parameter_getOrg')){
			var extWin = parent.parent.Ext.getCmp('ps_parameter_getOrg');
		 	extWin.msg = thevo;
		}else if(parent.parent.Ext.getCmp('select_org_emp')){
			parent.parent.openReturnValue(thevo);
		}else if(parent.parent.Ext.getCmp('getSelectedEmploy_showModalDialogs')){
			parent.parent.openReturnValue(thevo);
		}else{
               parent.parent.window.returnValue=thevo;
           }
	}else if(parent.parent.window.opener){//liujx 20190319
		var operType = parent.parent.window.opener.type;
		if(operType == 'orgEmp')
			parent.parent.window.opener.openEmpHistoryReturn(thevo);
		else if (operType == 'orgEmp6')
			parent.parent.window.opener.openEmpHistoryReturn6(thevo);
		parent.parent.window.close();
	}else{//ie浏览器
		parent.window.returnValue=thevo;
	}
 	closeWin();
	}
	
	function checkStatus(thevo){/* add by xiegh 需求 37660 */
		 if("${logonUserForm.flag}" != '1' || thevo.content == "") return true;//logonUserForm.flag =1 加载人员 || 没有选中节点
		 var hashvo=new ParameterSet();
	     hashvo.setValue("selfHelpUser",thevo.content);
	     hashvo.setValue("checkflag","true");
	     var flag = true;
		 var request=new Request({asynchronous:false,onSuccess:link_success,functionId:'1010010043'},hashvo);
			function link_success(outparamters){
				var userinfo=outparamters.getValue("isRelated");//isRelated:该自助用户是否被其他业务用户关联
				var selfUserName=outparamters.getValue("selfUserName");//该自助用户已关联的业务用户
				<%--业务关联自助用户只能是一对一，否则影响人事异动其他模块业务  wangb 20191122 --%>
				if(userinfo == 'true'){
					alert("该自助用户已经被业务用户["+selfUserName+"]关联，请重新选择自助用户!");
					flag = false
				}
			}
		return flag;
	}

	//移除 wangb 20180418     bug 36668
	function removeRepeat(value2,value1){
		value1 = ","+value1+",";
		value2 = value2.replace(/\s+/g,""); //去空格
		value2 = value2.replace(/,,/g,",");//去掉空的
		value2 = ","+ value2 + ",";
		if(value2.length ==0){
			value1 = value1.replace(",,",",");//去掉空的
			value1 = value1.substring(1);
			return value1;
		}
		var strs = value2.split(",");
		for(var i=0;i<strs.length ;i++){
			var strValue = strs[i].replace(/\s+/g,"");//去空格
			var str = ","+strValue+",";
			if(strValue.length!=0){
				if(value1.indexOf(str) > -1){
					value1 = value1.replace(","+strValue+",",",");
				} 
			}
		}
		value1 = value1.replace(/,,/g,",");//去掉空的
		value1 = value1.substring(1);
		return value1;
	}
	var addCodeCount=0;
	var addTitleCount=0;
	var repeatTitle='';
	//去重复 直接用逗号分隔
	function deleteRepeat(value2,value1,flag){
		value2 = value2.replace(/\s+/g,""); //去空格
		value2 = value2.replace(",,",",");//去掉空的
		value2 = ","+ value2 + ",";
		if(value2.length ==0){
			return value1;
		}
		var strs=value1.split(","); //字符分割 
		for (var i=0;i<strs.length ;i++ ) 
		{ 
			var strValue = strs[i].replace(/\s+/g,"");//去空格
			var str = ","+strValue+",";
			if(strValue.length!=0){
				if(value2.indexOf(str)<0){
					value2+=","+strValue;
					if(flag==1)
						addCodeCount++;
					else
						addTitleCount++;
				}else{
					repeatTitle=','+strValue;
				} 
			}
		} 
		var strValue2s = value2.split(",");
		var value = "";
		for(var i = 0 ; i < strValue2s.length ; i++){
			if(!strValue2s[i])
				continue;
			value += strValue2s[i] +",";
		}
		return value;
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
		          var desc = obj.options[i].value;
		          if(typeof(desc)=="undefined" || desc == null || desc.length < 1){
		        	  continue;
				  }
	             num++;
	             cont+=desc+",";
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
	    
	  	if(selecttype=='2'&&cont.length>0)
		{
	     	cont=cont.substring(0,cont.length-1);
	    	tit=tit.substring(0,tit.length-1);
	  	}
	  	
	 	if(desc == null || desc.length < 1)
		 	return;
	 	
	 	thevo.content=cont;
	 	thevo.title=tit;
	 	//培训教师 内部 选人时不需要进行人员是否关联了业务帐号的校验 chenxg 2020-01-03
	     if("<%=nmodule%>" != "6") {
		 	if(!checkStatus(thevo)){
		 		return false;
		 	}
	     }
	 	
 		if(parent.parent.Ext){//非IE浏览器  ext弹窗 方法
	 		if(parent.parent.Ext.getCmp('ps_parameter_getOrg')){
				var extWin = parent.parent.Ext.getCmp('ps_parameter_getOrg');
			 	extWin.msg = thevo;
	 		}else if(parent.parent.Ext.getCmp('select_org_emp')){
	 			parent.parent.openReturnValue(thevo);
	 		}else if(parent.parent.Ext.getCmp('getSelectedEmploy_showModalDialogs')){
	 			parent.parent.openReturnValue(thevo);
	 		}
	 	}else if(parent.parent.window.opener){//liujx 20190319
	 		var operType = parent.parent.window.opener.type;
			if(operType == 'orgEmp')
				parent.parent.window.opener.openEmpHistoryReturn(thevo);
			else if (operType == 'orgEmp6')
				parent.parent.window.opener.openEmpHistoryReturn6(thevo);
			parent.parent.window.close();
	 	}else{//ie浏览器
	 		parent.window.returnValue=thevo;
	 	}
	 	//window.returnValue=thevo;
	 	closeWin();		 
	}
	Global.defaultradiolevel = 3;//choice user
	var lever = <%=level%>
	if(lever!="-1"){
	Global.defaultradiolevel = lever;
	}
	function showDateSelectBox(srcobj,event)
   {
		var keycode;
		var temp = document.getElementsByName("contenttype");
		if(getBrowseVersion()/*navigator.appName == "Microsoft Internet Explorer"*/){
	        keycode = event.keyCode;  

	    }else{
	        keycode = event.which;  
	    }	
		if(keycode==38||keycode==40||keycode==13){ //当上下回车的时候不进交易类
			return;
		}
		optionNum = -1;
		if( temp[0].options[0]){
	        temp[0].value = temp[0].options[0].value;  
	    }
		
   		if($F('selectname')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');
      var pos=getAbsPosition(date_desc);
      
      var datap = document.getElementById('date_panel');
      datap.style.position= "absolute";
      datap.style.left= pos[0]+"px";
      datap.style.top=pos[1]-date_desc.offsetHeight+42+"px";
      datap.style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1+"px";
	  /*
	  with($('date_panel'))
	  {
        style.position="absolute";
        if(getBrowseVersion() == 10 || !getBrowseVersion()){
			style.left=pos[0]+5;
			style.top=pos[1]-date_desc.offsetHeight+44;
        }else{
			style.left=pos[0];
			style.top=pos[1]-date_desc.offsetHeight+42;
			style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
        }
      }
      */
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.logonUserForm.a_name.value));
	  hashVo.setValue("dbpre","<%=dbpre%>");
	  hashVo.setValue("showDb","<%=showDb%>");
	  hashVo.setValue("priv","<%=priv%>");
	  hashVo.setValue("showSelfNode","<%=showSelfNode%>");
	  hashVo.setValue("dbtype","<%=dbtype%>");
	  hashVo.setValue("viewunit","<%=viewunit%>");
	  hashVo.setValue("nmodule","<%=nmodule%>");
	  hashVo.setValue("isfilter","<%=isfilter%>");
	  hashVo.setValue("tabid","<%=tabid%>");
	  hashVo.setValue("SYS_FILTER_FACTOR","<%=SYS_FILTER_FACTOR%>");
	  hashVo.setValue("flag","4");
	  hashVo.setValue("dbvalue","<%=dbvalue%>");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'3020071012'},hashVo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
   		var str_value=outparamters.getValue("str_value");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(logonUserForm.contenttype,namelist);
			if(typeof(str_value)!="undefined"&&str_value.length>0)
			{ 
				var objs=str_value.split("`");
				var _objs= new Array();
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[0]]=temp[1];
				}
				
				var obj=document.getElementsByName("contenttype");
				for(var i=0;i<obj[0].options.length;i++)
	  			{
	  				var _value=obj[0].options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj[0].options[i].title=desc;
	  				}else{
	  					obj[0].options[i].disabled=true;
			  		}
	  			}
			}
		}
   }
   
   
   
   //var checkvalues = '${logonUserForm.checkvalue }'.split(",");
   //var checktitles = '<%= checktitle%>'.split(",");
   function onClickBox(event){
	   evt = event || window.event;
	   Obj = evt.srcElement;
	   value = Obj.value;
	   title = Obj.title;
	   if(Obj.checked == true){
		   checkvalues.push(value);
		   checktitles.push(title);
	   }else{
		   for(var k=0;k<checkvalues.length;k++){
			   if(checkvalues[k] == value){
				   checkvalues[k] = "";
				   checktitles[k] = "";
				   continue;
			   }
		   }
	   } 
	   
   }
   
   
   
   //
var optionNum = -1;  //记录当前选中的option;
   
function updown(e){
	var keycode;
    if(navigator.appName == "Microsoft Internet Explorer"){
         keycode = event.keyCode;  
    }else{
         keycode = e.which;  
    }
    
    var elem2 = document.getElementsByName("contenttype");
    var optionLength = elem2[0].childNodes.length;
    
    if(optionLength>0){
	    switch (keycode) {
	    case 38://up键
        	optionNum--;
            if(optionNum<0)
            	optionNum=optionLength-1;
            elem2[0].value = elem2[0].childNodes[optionNum].value;
	        break;
	
	    case 40://down键
        	optionNum++;
            if(optionNum>=optionLength) 
            	optionNum=0;
            elem2[0].value = elem2[0].childNodes[optionNum].value;
	        break;
	        
	    case 13://回车键
	    	//19/3/27 xus 屏蔽输入框回车键
            e.preventDefault ? e.preventDefault() : e.returnValue = false;
            var objid;
            if (optionNum<0) return;//陈总机器报错 屏蔽 wangrd 2015-05-15
            objid = elem2[0].childNodes[optionNum].value;
            if(objid){
                if(objid.length>3){
                   okSelect();
                }else{
                    e.returnValue=false;
                }
            }
	         
	        break;
	     
	    }
    }
}    
   
   
function selectupdown(e){
	var keycode;
    if(navigator.appName == "Microsoft Internet Explorer"){
    	keycode = event.keyCode;  
        
    }else{
        keycode = e.which;  
    }
    if(keycode==13){
        var objid,i;
           var obj=$('contenttype');           
           for(i=0;i<obj.options.length;i++)
           {
              if(obj.options[i].selected)
                objid=obj.options[i].value;
           }       
           if(objid)
           {
               if(objid.length>3){
            	   okSelect();  
               }else{
                    e.returnValue=false;//在人员库回车键不做任何操作
                }
           }
    }
    
}   
 
</script>
   <style type="text/css">
	#treemenu {  
	height: 300px;overflow: auto;
	/*auto;border-style:inset ;
	border-width:2px*/
	}
   </style>
   <hrms:themes></hrms:themes>
<html:form action="/system/logonuser/org_employ_tree"> 
   <table width="96%" border="0" cellspacing="0" cellspacing="0" align="center">
   <%if(!flag.equals("0")&&!selecttype.equals("0")){ %>   
	 <tr align="left" valign="top">
		<td valign="top" nowrap align="left">
		<bean:message key="columns.archive.name"/><Input type='text' style="margin-left:5px;width:241px;" name='a_name'  size='35' id="selectname" onkeyup="showDateSelectBox('selectname',event)" onkeydown="updown(event);" title='<%=generalmessage %>' class="text4"/>
		</td>
	 </tr>
	 <%} %>          
         <tr>

<%--如果传入selectedValues 则使用selectedValues 否则使用原本后台取到的logonUserForm.checkvalue zhanghua2018-06-13--%>
           <td width="100%" align="left" valign="top" class="RecordRow" style="border:none;padding: 3px 0 0 0;margin: 0px auto;">
               <div id="treemenuDiv" >
               <%if(selectedValues==""){ %>
                 <hrms:orgtree cascade="<%=cascade %>" checkvalue="${logonUserForm.checkvalue}" checklevel="<%=checklevel %>" flag="${logonUserForm.flag}" showDb="<%=showDb%>" showSelfNode="<%=showSelfNode%>"  loadtype="${logonUserForm.loadtype}" showroot="false" selecttype="${logonUserForm.selecttype}" dbtype="${logonUserForm.dbtype}" priv="${logonUserForm.priv}" isfilter="${logonUserForm.isfilter}" nmodule="<%=nmodule %>" viewunit="<%=viewunit %>" dbvalue="<%=dbvalue %>"  privtype="<%=privtype %>" divStyle="height:300px;width:100%;overflow-x:auto;overflow-y:auto;" />
               <%}else{ %>
               <hrms:orgtree cascade="<%=cascade %>" checkvalue="<%=selectedValues %>" checklevel="<%=checklevel %>" flag="${logonUserForm.flag}" showDb="<%=showDb%>" showSelfNode="<%=showSelfNode%>"  loadtype="${logonUserForm.loadtype}" showroot="false" selecttype="${logonUserForm.selecttype}" dbtype="${logonUserForm.dbtype}" priv="${logonUserForm.priv}" isfilter="${logonUserForm.isfilter}" nmodule="<%=nmodule %>" viewunit="<%=viewunit %>" dbvalue="<%=dbvalue %>"  privtype="<%=privtype %>" divStyle="height:300px;width:100%;overflow-x:auto;overflow-y:auto;" />
               <%} %>
               </div>
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="closeWin();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>        
   </table>
   <div id="date_panel" style="display:none;">
     <%if(selecttype.equals("2")){ %>
		<select id="date_box" name="contenttype" onkeydown="selectupdown(event);" onblur="Element.hide('date_panel');" style="width:254" size="10" ondblclick="okSelect();">
        </select>
        <%}else{ %>
        <select id="date_box" name="contenttype" onkeydown="selectupdown(event);" onblur="Element.hide('date_panel');"  multiple="multiple"  style="width:254" size="10" ondblclick="okSelect();">
        </select>
        <%} %>
	 </div>
</html:form>


<script language="JavaScript">
    document.getElementById('treemenuDiv').style.width=document.body.clientWidth-20;
</script>
