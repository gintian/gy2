<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="com.hrms.frame.dao.RecordVo,com.hjsj.hrms.actionform.sys.LogonUserForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	boolean isBThree = false;
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  isBThree = userView.isBThreeUser();
	}
	//String account_logon_interval=SystemConfig.getPropertyValue("account_logon_interval");
	String	account_logon_interval=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.ACCOUNT_LOGON_INTERVAL);
	String password_lock_days=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.PASSWORD_LOCK_DAYS);
	int paramlength = password_lock_days.length();
	int passlocjdayslength = account_logon_interval.length();
	RecordVo uservo=new RecordVo("operuser");
	int usernameLen = Integer.parseInt((String)uservo.getAttrLens().get("username"));
	
%>
<link href="/css/xtree.css" type="text/css" rel="stylesheet"/>
<link href="/ajax/skin.css" type="text/css" rel="stylesheet"/>
<script type='text/javascript' src='../../module/utils/js/template.js' ></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<style>
.toldname{
	width:237;
	border: 1pt solid #C4D8EE;
	color:#ccc;
}
</style>
<hrms:themes></hrms:themes>
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

<script language="javascript">
  function add_group_ok(outparamters)
  {
     var currnode=Global.selectedItem;
     /*不允许在超级用户组下创建用户*/
     //alert(currnode.uid);
     if(currnode.uid=="超级用户组")
        currnode=currnode.parent;
     var iconurl=currnode.icon;
     if(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif")
         currnode=currnode.parent;   
            
     var groupid=outparamters.getValue("groupid");
     var groupname=outparamters.getValue("groupname");
     var ungroupname=groupname;//getEncodeStr(groupname);//escape(groupname);
     if(currnode.load)
     {                                               
	 	var tmp = new xtreeItem(groupname,groupname,"/system/security/assignpriv_tab.do?br_query=link&rp=0&user_flag=0&a_tab=funcpriv&role_id="+$URL.encode(ungroupname),"mil_body",groupname,"/images/groups.gif","/system/logonuser/search_user_servlet?level0=1&groupid="+groupid+"&username="+$URL.encode(ungroupname));
	 	currnode.add(tmp);
     }
     else
     	currnode.expand();
  }
  
  function add_group()
  {
  	 var currnode=Global.selectedItem;
  	 var curruid =currnode.uid;
  	 var issuperadmin="<%=userView.isSuper_admin() %>";
  	 if('false'==issuperadmin&&'root'==curruid){
  	 	alert('非超级用户不能在根节点下新建用户组!');
  	 	return;
  	 }
     var currname=1;
     var title=prompt("<bean:message key="column.name"/>","");
     if(title==null)
     	return;
     var ctrlvalue="%'$#@!~^&*()_+\"'";							//用户组名称不能含有\   jingq   add  2014.5.29
      if(title.length>0&&(ctrlvalue.indexOf(title.substring(0,1))!=-1||title.indexOf("'")!=-1||title.indexOf("(")!=-1||title.indexOf(")")!=-1||title.indexOf("（")!=-1||title.indexOf("）")!=-1)||title.indexOf("\\")!=-1)
      {
      	alert("<bean:message key="error.user.number"/>");
      	return;
      }
      //tiany添加对输入名称长度的控制 start
      if(getByteLen(title)><%=usernameLen%>){
      	alert("<bean:message key="column.name"/>不能超过"+<%=usernameLen%>+"字符!");
      	return;
      }
      //end
     currname=currnode.text;
     var hashvo=new ParameterSet();
     hashvo.setValue("groupname",title);
     hashvo.setValue("currname",currname);	        
	var request=new Request({asynchronous:false,onSuccess:add_group_ok,functionId:'1010010036'},hashvo);        
  }
function getByteLen(str) {    //传入一个字符串
   var byteLen = 0, len = str.length;
	if( !str ) return 0;
	for( var i=0; i<len; i++ )
		byteLen += str.charCodeAt(i) > 255 ? 2 : 1;
	return byteLen;	                  

}	  
	
  function delete_ok(outparamters)
  {
     var currnode=Global.selectedItem;
     var preitem=currnode.getPreviousSibling();
     currnode.remove();
     preitem.select(preitem);
  }
   
  function checkuser(userid,currnode){
  	for(var i=0;i<currnode.childNodes.length;i++){
  		var node=currnode.childNodes[i];
  		if(node.uid.toUpperCase()==userid){
  			return true;
  		}else{
  			if(node.childNodes.length>0)
  				checkuser(userid,node);
  			else
  				return false
  		}
  	}
  }  	
  function delete_object()
  {

     var currnode=Global.selectedItem;
     if(currnode==null)
       return;
     if(currnode.uid=="超级用户组"||currnode.uid.toLowerCase()=="su"||currnode.uid=="root")
     	return;
     
     if("<%=userView.getUserId() %>"==currnode.uid){
     	alert("当前用户不能删除其本身!");
     	return;
     }
     if("true"=="<%=isBThree %>")//三员用户不能增删改超级用户组下用户
     {
         var parentnode = currnode.parent;
         if(parentnode.uid=="超级用户组")
         {
        	 return;
         }
     }
     if(checkuser("<%=userView.getUserId() %>".toUpperCase(),currnode)){
     	alert("当前用户不能删除其所在的组!");
     	return;
     }
     if(!ifdelete(currnode.uid))
       return; 
        	 
     currname=currnode.text;
     var hashvo=new ParameterSet();
     hashvo.setValue("currname",currname);	        
   　 var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'1010010038'},hashvo);        
  }
  
  function add_user_ok(outparamters)
  {
     var currnode=Global.selectedItem;
     var iconurl=currnode.icon;
     if(iconurl!="/images/groups.gif")
         currnode=currnode.parent;
     
     var groupid=outparamters.getValue("groupid");
     var groupname=outparamters.getValue("groupname");
     var flag=outparamters.getValue("flag");
     var state = outparamters.getValue("state");
     var ungroupname=groupname;//getEncodeStr(groupname);//escape(groupname);
     if(currnode.load)
     {
		var imgurl;
		<%if(paramlength>0||passlocjdayslength>0){%>
		if(flag=="1"){
			if(state==0)
		  		imgurl="/images/admin_lock.gif";
			else
				imgurl="/images/not_admin.gif";
		}else{
			if(state==0)
		  		imgurl="/images/admin_lock.gif";
			else
		  		imgurl="/images/admin.gif";
		  }	
		<%}else{%>
		if(flag=="1"){
				imgurl="/images/not_admin.gif";
		}else{
		  		imgurl="/images/admin.gif";
		  }
		<%}%>
        if(currnode.uid=="超级用户组"||currnode.uid.toLowerCase()=="su"||currnode.uid=="root")		    
		 	var tmp = new xtreeItem(groupname,groupname,"/system/logonuser/su_info.do","mil_body",groupname,imgurl,"/system/logonuser/search_user_servlet?level0=1&groupid="+groupid+"&username="+$URL.encode(ungroupname));
	 	else
		 	var tmp = new xtreeItem(groupname,groupname,"/system/security/assignpriv_tab.do?br_query=link&rp=0&user_flag=0&a_tab=funcpriv&role_id="+$URL.encode(ungroupname)+"&role_name="+$URL.encode(ungroupname),"mil_body",groupname,imgurl,"/system/logonuser/search_user_servlet?level0=1&groupid="+groupid+"&username="+$URL.encode(ungroupname));
	 	currnode.add(tmp);
     }
     else
     	currnode.expand();
  
  }
  /*flag=1　user,=0 admin*/
  function add_user(flag)
  {
     var currname;
     var currnode=Global.selectedItem;       
     if(currnode.uid=="root")
		return;   
     if((currnode.uid=="超级用户组"||currnode.parent.uid=="超级用户组")&&"true"=="<%=isBThree %>")//三员用户不能增删改超级用户组下用户
     {
    	 return;
     }
     var bflag=false;
     var dw=670,dh=350;
     var dl=(screen.width-dw)/2;
     var dt=(screen.height-dh)/2;		
     
     /*浏览器兼容 弹框修改 guodd 2019-03-19*/
     window.userAction = 'add';
     if(window.showModalDialog){
    	 var return_vo= window.showModalDialog("/system/logonuser/add_edit_user.do", bflag, 
    	         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no");
    	 add_or_update_user_success(return_vo,flag);
     }else{
    	 window.newUserFlag = flag;
    	 window.open("/system/logonuser/add_edit_user.do",'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
     }
     
  }
  
  function update_object_ok(outparamters)
  {
	  <% if(paramlength>0||passlocjdayslength>0){ %>
	  var flag=outparamters.getValue("flag");
	  var state = outparamters.getValue("state");
	  var imgurl;
		if(flag=="1"){
			if(state==0)
		  		imgurl="/images/admin_lock.gif";
			else
				imgurl="/images/not_admin.gif";
		}else{
			if(state==0)
		  		imgurl="/images/admin_lock.gif";
			else
		  		imgurl="/images/admin.gif";
		  }
		var currnode=Global.selectedItem;
		currnode.setIcon(imgurl);
		<%}%>
  }
  
  function update_object()
  {
     var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;  
     var bflag=true;
      //修改用户组名称   jingq add  2014.4.29
      if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif")){
     	if(currnode.uid=="超级用户组"||currnode.uid.toLowerCase()=="su"||currnode.uid=="root"){
     		if(currnode.uid=="超级用户组"){
     			alert("不允许修改超级用户组！");
     		} else if(currnode.uid=="root"){
     			alert("不允许修改根节点！");
     		}
    		return;
    	}
     	var theurl="/system/logonuser/update_user_group.jsp?group_name="+$URL.encode(currnode.uid);
     	var dw=300,dh=200,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
     	
     	 /*浏览器兼容 弹框修改 guodd 2019-03-19*/
        if(window.showModalDialog){
        	var return_vo= window.showModalDialog(theurl, bflag, 
        	         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
       		update_group_success(return_vo);
        }else{
       	 	window.open(theurl,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
        }
     	
     } else {
    	 var parentid = currnode.parent.uid;//三员用户不能增删改超级用户组下用户和本身  jingq upd 2015.06.12
      	if(("true"=="<%=isBThree %>")&& (("<%=userView.getUserId() %>"==currnode.uid)||(parentid=="超级用户组")))//三员用户不能增删改超级用户组下用户
      	{
           return;
      	} else {
		     var theurl="/system/logonuser/add_edit_user.do?b_query=link&user_name="+$URL.encode(currnode.uid);
		     var dw=520,dh=300,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
		     
		     window.userAction = 'update';
		     /*浏览器兼容 弹框修改 guodd 2019-03-19*/
		     if(window.showModalDialog){
		    	 	var return_vo= window.showModalDialog(theurl, bflag, 
		    		        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
		    	 	add_or_update_user_success(return_vo);
		     }else{
		    	 	window.open(theurl,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
		     }
  	 
   		}        
  	}
  }
  
  function update_group_success(return_vo){
	if(return_vo!=null){
   		var oldname = return_vo.oldname;
   		var newname = return_vo.newname;
   		var hashvo = new ParameterSet();
   		hashvo.setValue("oldname",oldname);
   		hashvo.setValue("newname",newname);
   		//执行修改操作的交易类
   		var request = new Request({asynchronous:false,onSuccess:updgroup_ok,functionId:'1010010079'},hashvo);
   	}
  }
  function add_or_update_user_success(return_vo,flag){
	  if(return_vo==null)
	  	 	return ;
	  
	  if(window.userAction=='add'){
		  if(flag==undefined && window.newUserFlag==undefined)
		  	 	return ;
		    flag = flag||window.newUserFlag;
		  	var currnode=Global.selectedItem;    
		  	 /*返回来的值类型怎不对,不能直接按Object对象用*/
		     var user_vo=new Object();
		     var name="";
		     user_vo.username=name=return_vo.username;
		     user_vo.fullname=return_vo.fullname;
		     user_vo.password=return_vo.password;
		     user_vo.pwdok=return_vo.pwdok;
		     user_vo.state=return_vo.state;
		     user_vo.email=return_vo.email;
		     user_vo.phone=return_vo.phone;
		     user_vo.org_dept=return_vo.org_dept;
		     currname=currnode.uid;
		     var hashvo=new ParameterSet();
		     hashvo.setValue("user_vo",user_vo);
		     hashvo.setValue("flag",flag);
		     hashvo.setValue("currname",currname);    
			 var request=new Request({asynchronous:false,onSuccess:add_user_ok,functionId:'1010010037'},hashvo);   
		   　 	 window.newUserFlag=undefined;
		  
	  }else if(window.userAction=='update'){
		  /*返回来的值类型怎不对,不能直接按Object对象用*/
		     var user_vo=new Object();
		     user_vo.username=return_vo.username;
		     user_vo.fullname=return_vo.fullname;
		     //user_vo.password=return_vo.password;
		     user_vo.state=return_vo.state;
		     user_vo.email=return_vo.email;
		     user_vo.phone=return_vo.phone;
		     user_vo.org_dept=return_vo.org_dept;  
		     //currname=currnode.uid;
		     var hashvo=new ParameterSet();
		     hashvo.setValue("user_vo",user_vo);
		   　 var request=new Request({asynchronous:false,onSuccess:update_object_ok,functionId:'1010010040'},hashvo);
	  }
	  
	  window.userAction = undefined;
	  
	  	 
  }
  
  function changepwd(){
  	 var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;  
     //if(currnode.uid=="超级用户组"||currnode.uid.toLowerCase()=="su"||currnode.uid=="root")
     //	return;
     if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif"))
     	return;     
     var bflag=true;
     var theurl="/system/security/resetup_password.do?b_edit=link&user_name="+getEncodeStr(currnode.uid);
     theurl = encodeURI(theurl);
     var dw=680,dh=160,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
     /*浏览器兼容 弹框修改 guodd 2019-03-19*/
     if(window.showModalDialog){
    	 	window.showModalDialog(theurl, bflag, 
    		        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
     }else{
    	 	window.open(theurl,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
     }
     //var return_vo= window.showModalDialog(theurl, bflag, 
     //   "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
  	 //if(return_vo==null)
  	 //	return ;
  }
  
  
  function linkemploy()
  {
     var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;     
     if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif"))
     	return;
     var bflag=true;
     var theurl="/system/logonuser/link_employ.do?b_query=link&user_name="+$URL.encode(currnode.uid);
     var dw=450,dh=250,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
     
     /*浏览器兼容 弹框修改 guodd 2019-03-19*/
     var return_vo = undefined;
     if(window.showModalDialog){
    	 return_vo = window.showModalDialog(theurl,currnode.uid, 
    		        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    	 linkemploy_success(return_vo);
     }else{
    	 window.open(theurl,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
     }
     
     
  }
  
  function linkemploy_success(return_vo){
		//系统管理—用户授权—给用户重新关联人员后，节点title还是原先的。  jingq add 2014.10.27
		var currnode=Global.selectedItem; 
	     if(return_vo!=""&&return_vo!=undefined){
	 		currnode.setTitle(return_vo);
	   	 } else if(return_vo==false){
	   		currnode.setTitle(currnode.text);
	   	 }
  }
  
  function assign_role()
  {
     var currnode=Global.selectedItem; 
     var curruid=currnode.uid;
     if(curruid=='su')
     	return;
     var iconurl=currnode.icon;     
     if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif"))
     	return;
     var dw=564,dh=520,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
     var theurl="/system/security/assign_role.do?b_query=link`ret_ctrl=1`a_userflag=0`a_roleid="+currnode.uid;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);   
	 
	 
	 /*浏览器兼容 弹框修改 guodd 2019-03-19*/
     if(window.showModalDialog){
    	 window.showModalDialog(iframe_url,"_blank", 
    		        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
     }else{
    	 window.open(iframe_url,'','width='+dw+',height='+dh+',left='+dl+',top='+dt+',resizable=no,titlebar=yes,location=no,status=no,scrollbars=no');
     }
	 
  }
  
  function view_priv_mx()
  {
     var currnode=Global.selectedItem;
     if(currnode==null)
       return;  
     if(currnode.uid=="超级用户组"||currnode.uid.toLowerCase()=="su"||currnode.uid=="root")
     	return;   
     var iconurl=currnode.icon;       	
     if((iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif"))
     {
	   userPopedom("2","",$URL.encode(currnode.uid));	   
     }
     else
     {
     	orgPopedom($URL.encode(currnode.uid),"0"); //组描述信息
     }
  }
  
  function busi_org_dept()
  {
     var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;  
     //if(currnode.uid=="超级用户组"||currnode.uid.toLowerCase()=="su"||currnode.uid=="root")
     //	return;
     if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"||iconurl=="/images/admin_lock.gif"))
     	return;     
     var bflag=true;
     var theurl="/system/logonuser/add_edit_user.do?b_query_busi_org_dept=link&user_name="+currnode.uid;
     var return_v=window.showModalDialog(theurl, bflag, 
        "dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");	
  	if(return_v!=null&&return_v.length>0){
  		var hashvo=new ParameterSet();
  		hashvo.setValue("username",currnode.uid);
    	 hashvo.setValue("return_v",return_v);
   　 		var request=new Request({asynchronous:false,onSuccess:update_object_ok,functionId:'1010010049'},hashvo);        
  	}
  }
  //交易类执行成功，修改树节点名称,id
  function updgroup_ok(outparameters){
  	var currnode = Global.selectedItem;
  	var text = outparameters.getValue("newname");
  	currnode.setText(getDecodeStr(text));
  	currnode.uid = text;
  }
  //根据输入的信息查询用户名称查询	jingq  add   2014.5.12
  var lastValue="";
  function bsearch(obj){
  	if($F('oldname')==""){
  		Element.hide('date_panel');
  		return false;
  	}else{
  		if(lastValue==''){
  			lastValue=$F('oldname');
  		}else{
  			if(lastValue==$F('oldname')){
  				Element.hide('date_panel');
  		  		return false;
  			}
  		}
  	}
  	date_desc = document.getElementById(obj);
  	Element.show('date_panel');
  	var pos=getAbsPosition(date_desc);
	with($('date_panel')){				//定位下拉文本框位置
    	style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-date_desc.offsetHeight+44;
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
    }
  	var oldname = document.getElementById(obj).value;
  	var s="";					//处理特殊字符
	var b = "";
	for(var j=0;j<oldname.length;j++){
		s = oldname.substring(j,j+1);
		if(s=='#'){
			b+="nbspa";
		} else if(s=="；"){
			b+="quanjiao;hao";
		} else {
			b+=s;
		}
	}
	oldname = b;
	oldname = getEncodeStr(oldname);
  	var hashvo = new ParameterSet();
  	hashvo.setValue("oldname", oldname);
  	hashvo.setValue("searchFlag", '1');
  	var request = new Request({asynchronous:false,onSuccess:search_ok,functionId:'1010010091'},hashvo);
  }
  //将查询出的用户名集合放入list
  function search_ok(outparameters){
	//输入查询结束后 全局变量lastValue置空
	lastValue="";
  	var list = outparameters.getValue("namelist");
  	//处理特殊字符
  	for ( var i = 0; i < list.length; i++) {
		var temp = list[i];
		var dataname = temp.dataName;
		var datavalue = temp.dataValue;
		dataname = getDecodeStr(dataname);
		datavalue = getDecodeStr(datavalue);
		dataname = testStr(dataname);
		datavalue = testStr(datavalue);
		temp.dataName = dataname;
		temp.dataValue = datavalue;
		list[i] = temp;
	}
  	AjaxBind.bind($('date_box'),list);		//为select_box附加数据
  }
  
  //双击事件    双击后根据选择的用户名执行操作
  function okSelect(){
  	var obj = document.getElementById("date_box");
  	var oldname,fname,temp;
  	var root = Global.selectedItem;
  	while(root.uid!="root"){	//判断节点是否为根节点
  		root = root.root();
  	}
  	root.collapseChildren();	//关闭所有子节点
	var list = root.childNodes;
    if(obj){
		for(var i=0;i<obj.options.length;i++){		
	    	if(obj.options[i].selected){
	            var t=obj.options[i].value;
	            if(t.indexOf("(")!=-1){
	            	temp = t.substring(0,t.indexOf("("));
	            	fname = t.substring(t.indexOf("("),t.length);
	            } else {
	            	temp = t;
	            	fname = "";
	            }
	            var arr = temp.split("\\");					//将用户组名和用户名截取，存入数组
	            for ( var j = 0; j < arr.length; j++) {			
					for ( var x = 0; x < list.length; x++) {
						if(list[x].text==arr[j]){			//遍历数组，找到对应的树节点
							if(j==arr.length-1){			//如果是数组的最后一个，执行节点的action，否则展开节点
								oldname = list[x].text;
								list[x].select();
							} else {
								list[x].expand();
								list = list[x].childNodes;
							}
							break;
						}
					}
				}
	        }
	    }
	}
	document.getElementById("oldname").value=oldname+fname;	//文本框显示选中的用户名
	document.getElementById("date_panel").style.display="none";		//下拉文本框消失
  }
  
  //文本框单击事件    文本框有内容，且下拉框隐藏，则查询
  function show(){
  	var text = document.getElementById("oldname").value;
  	if(text.length==0){
  		Element.hide('date_panel');
  	} else if(document.getElementById("date_panel").style.display!="none"){
  		Element.hide('date_panel');
  	} else {
  		bsearch('oldname');
  	}
  }
  
  function TextFocus(){
  	var obj = document.getElementById("oldname");
  	if(obj.value!="<bean:message key="search.user_group.text"/>"){
  		return;
  	} else {
  		obj.value = "";
  		obj.style.color="#000";
  	}
  }
  function TextBlur(){
   	var obj = document.getElementById("oldname");
  	if(obj.value==""){
  		obj.value = "<bean:message key="search.user_group.text"/>";
  		obj.style.color="#ccc";
  	}
  }
  function outContent(){
  	document.getElementById("WzTtDiV").style.visibility="hidden";//点击时先隐藏漂浮模块
  	var obj = document.getElementById("date_box");
  	if(obj){
		for(var i=0;i<obj.options.length;i++){		
	    	if(obj.options[i].selected){		//判断路径是否显示完全
	            var t=obj.options[i].value;
	            var f = obj.options[i].text;
	            if(t!=f){
	            	config.FontSize='10pt';//提示信息中的字体大小
					Tip(t,STICKY,true);
	            }
	        }
	    }
	}
  }
  function hide(){	//当tooltip存在时，不隐藏下拉列表
  	if(document.getElementById("WzTtDiV").style.visibility=="hidden"){
  		Element.hide('date_panel');
  	} else {
  		return false;
  	}
  }
  //处理' " ” “
  function testStr(str){
  	var b = "";
  	var s = "";
  	for(var j=0;j<str.length;j++){
		s = str.substring(j,j+1);
		if(s=="＇"){		
			b+="\'";
		} else if(s=="＂"){
			b+="\"";
		} else if(s=="”"){
			b+="\”";
		} else if(s=="“"){
			b+="\“";
		} else {
			b+=s;
		}
	}
	return b;
  }
</script>
<style type="text/css">

</style>
<!-- hr平台，系统管理，用户管理，工具条上面是双线，不对。   jingq upd 2014.11.3 -->
<body  style="margin-left:0;margin-top:0;"> 
<script language="javascript" type="text/javascript" src="/js/wz_tooltip.js"></script>
<html:form action="/system/logonuser/search_user_tree">
		<!-- 
  			<hrms:menubar menu="menu2" id="menubar2">
       			<hrms:menuitem name="mitem1" label="label.user">
       				<hrms:menuitem name="mitem11" label="label.add.group" icon="/images/add_all.gif" url="add_group();"  enabled="true" visible="true" function_id="3003101,08020101"/>       				
       				<hrms:menuitem name="mitem12" label="label.add.admin" icon="/images/add.gif"  url="add_user(0);"  enabled="true" visible="true" function_id="3003102,08020102"/>       				
       				<hrms:menuitem name="mitem13" label="label.add.user" icon="/images/add_2.gif" url="add_user(1);"  enabled="true" visible="true" function_id="3003103,08020103"/>       				
       				<hrms:menuitem name="mitem14" label="button.edit" icon="/images/write_obj.gif" url="update_object();"  enabled="true" visible="true" function_id="3003104,08020104"/>       				
       				<hrms:menuitem name="mitem14" label="label.banner.changepwd" icon="/images/pic_c.gif" url="changepwd();"  enabled="true" visible="true" function_id="3003110,08020110"/>  
       				<hrms:menuitem name="mitem15" label="button.delete" icon="/images/delete_all.gif" url="delete_object();"  enabled="true" visible="true" function_id="3003105,,08020105"/>       				
       			</hrms:menuitem>
       			<hrms:menuitem name="mitem2" label="label.priv">
       				<hrms:menuitem name="mitem21" label="label.role.assign" icon="/images/role_assign.gif"  url="assign_role();"  enabled="true" visible="true" function_id="3003106,08020106"/>       				
       				<hrms:menuitem name="mitem22" label="label.link.employ" icon="/images/link.gif" url="linkemploy();"  enabled="true" visible="true" function_id="3003107,08020107"/>       				
       				<hrms:menuitem name="mitem23" label="label.priv.mx" icon="/images/viewpriv.gif"  url="view_priv_mx();"  enabled="true" visible="true" function_id="30035,080102"/>       				
       				<!-- hrms:menuitem name="mitem24" label="label.priv.busi_org_dept" icon="/images/write_obj.gif" url="busi_org_dept();"  enabled="true" visible="true" function_id="3003108,08020108"/>   -->     				
       			</hrms:menuitem>
  			</hrms:menubar>
  		-->  
   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" >   
	 <tr align="left" class="toolbar"  style="padding-left:2px;width:expression(document.body.clientWidth);overflow: auto;">
		<td valign="middle">
			<hrms:priv func_id="3003101,08020101">
				<img src="/images/add_all.gif" border=0 title='<bean:message key="label.add.group"/>' style="cursor:hand;" onclick="add_group();"/>
			</hrms:priv>
			<hrms:priv func_id="3003103,08020103">
				<img src="/images/add.gif" border=0 title='<bean:message key="label.add.user"/>' style="cursor:hand;" onclick="add_user(1);"/>
			</hrms:priv>
			<hrms:priv func_id="3003102,08020102">
			<img src="/images/add_2.gif" border=0 title='<bean:message key="label.add.admin"/>' style="cursor:hand;" onclick="add_user(0);"/>
			</hrms:priv>
			<hrms:priv func_id="3003105,08020105">
			<img src="/images/del.gif" border=0 title='<bean:message key="button.delete"/>' style="cursor:hand;" onclick="delete_object();"/>
			</hrms:priv>
			<hrms:priv func_id="3003104,08020104">
			<img src="/images/edit.gif" border=0 title='<bean:message key="button.edit"/>' style="cursor:hand;" onclick="update_object();"/>
			</hrms:priv>
			<hrms:priv func_id="3003110,08020110"> 			
			   <img src="/images/pic_c.gif" border=0 title='<bean:message key="label.banner.changepwd"/>' style="cursor:hand;" onclick="changepwd();"/>
		    </hrms:priv>
			<hrms:priv func_id="3003107,08020107">
			<img src="/images/link.gif" border=0 title='<bean:message key="label.link.employ"/>' style="cursor:hand;" onclick="linkemploy();"/>
			</hrms:priv>
			<hrms:priv func_id="3003106,08020106">
			<img src="/images/role_assign.gif" border=0 title='<bean:message key="label.role.assign"/>' style="cursor:hand;" onclick="assign_role();"/>
		    </hrms:priv>
		    <hrms:priv func_id="30035,080102"> 			
			   <img src="/images/viewpriv.gif" border=0 title='<bean:message key="label.priv.mx"/>' style="cursor:hand;" onclick="view_priv_mx();"/>
		    </hrms:priv>	
		</td>
	 </tr>          
	 	<tr>
	 		<td>
	 			<!-- form表单只有一个文本框时按回车键将会自动将表单提交 wangb 20170603 28225-->
	 			<input type='text' style='display:none'/>
	 			&nbsp;<input class="toldname text4" type="text" id="oldname" name="name" style="width:237;color:#ccc;" onkeyup="setTimeout(bsearch('oldname'),300);" onclick="show();" value="<bean:message key="search.user_group.text"/>"
	 			onfocus="TextFocus()" onblur="TextBlur()" autocomplete="off"></input><!-- 解决浏览器自带的历史记录下拉框 -->
	 			<div id="date_panel" style="display: none;">
					<select id="date_box" name="namelist" multiple="multiple" style="width: 237" size="6" ondblclick="okSelect();"
					 onmouseout="hide();tt_HideInit();" onclick="outContent();">	<!-- 调用wz_tooltip控件 -->
						
					</select>
				</div>
	 		</td>
	 	</tr>
         <tr>
           <td align="left"> 
            <div id="treemenu" ondragend="dragend('operuser','username','groupid','1010010098');" style="overflow-x: auto;overflow-y:auto;"> 
             <SCRIPT LANGUAGE=javascript> 
               <hrms:priv func_id="08020109,3003109"> 
               	setDrag(true); 
               </hrms:priv>	
               <bean:write name="logonUserForm" property="userTree" filter="false"/>
             	
             </SCRIPT>
             </div>             
           </td>
           </tr>           
   </table>
</html:form>
</body>
<script language="javascript">
  initDocument();
  var treemenu = document.getElementById('treemenu');
  treemenu.style.height=document.body.clientHeight-63+'px';
  treemenu.style.windth=document.body.clientWidth+'px';
</script>