<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.hjsj.sys.Des,com.hrms.frame.utility.AdminDb"%>
<%@ page import="java.util.Date,com.hrms.hjsj.sys.DataDictionary,com.hrms.hjsj.sys.FieldItem" %>
<%@ page import="com.hrms.frame.dao.utility.DateUtils,java.sql.*,com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter" %>
<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js">
</script>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if (userView!=null){
		String path = request.getSession().getServletContext().getRealPath("/js");
		if (SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
			path = session.getServletContext().getResource("/js").getPath();//.substring(0);
			if (path.indexOf(':') != -1) {
				path = path.substring(1);
			} else {
				path = path.substring(0);
			}
			int nlen = path.length();
			StringBuffer buf = new StringBuffer();
			buf.append(path);
			buf.setLength(nlen - 1);
			path = buf.toString();
		}
		userView.getHm().put("js_path", path);
	}
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2; 
	String generalmessage ="可以输入\"姓名\"";
	String selectmessage ="请输入\"姓名\"";
	String convenient_search=SystemConfig.getPropertyValue("convenient_search");
	if("1".equals(convenient_search)){
		Connection conn = null;
		try{	
			conn = AdminDb.getConnection();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
					 String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
					 FieldItem item = DataDictionary.getFieldItem(onlyname);
						if (item != null&&!"a0101".equalsIgnoreCase(onlyname)&&!userView.analyseFieldPriv(item.getItemid()).equals("0")) {
							generalmessage+=",\""+item.getItemdesc()+"\"";
							selectmessage+=",\""+item.getItemdesc()+"\"";
						}
						String pinyin_field = sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
						item  = DataDictionary.getFieldItem(pinyin_field.toLowerCase());
						if (!(pinyin_field == null|| pinyin_field.equals("") || pinyin_field.equals("#")||item==null||item.getUseflag().equals("0"))&&!"a0101".equalsIgnoreCase(pinyin_field)&&!userView.analyseFieldPriv(item.getItemid()).equals("0")){
							generalmessage+=",\""+item.getItemdesc()+"\"";
							selectmessage+=",\""+item.getItemdesc()+"\"";
						}
						generalmessage+="进行查询";
						selectmessage+="进行查询！";
						
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(conn!=null)
				conn.close();
		}
	}
	int ntool = 2;//计算工具栏 条数
%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=SystemConfig.getCsClientServerURL(request);
    String userName = null;
    String pwd=null;
    boolean bexchange=false;
    int status=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    boolean bflag=lockclient.isBtest();
    String verdesc="";
    if(bflag)
    {
    	verdesc=ResourceFactory.getProperty("label.sys.about.test");
    }      
    String css_url="/css/css1.css";
    
    bexchange=userView.isBexchange();
	status=userView.getStatus(); 
    StringBuffer buf=new StringBuffer();
    String sys_name=SystemConfig.getPropertyValue("sys_name");
    if(sys_name.length()==0)
    {
    	sys_name="贵州银行人力资源系统";
    }
    String value=SystemConfig.getPropertyValue("display_employee_info");
    String view_time=SystemConfig.getPropertyValue("banner_viewTime");
    boolean bvalue=false;
    if(value.length()==0||value.equalsIgnoreCase("true"))
    {
    	bvalue=true;
    } 
    //欢迎提示是否显示人员机构信息 xuj add 2013-11-26
    String display_field_info = SystemConfig.getPropertyValue("display_field_info").toLowerCase();
    if(SystemConfig.isScrollWelcome()&&bvalue)
    {
    	if(display_field_info.length()==0){
		    String orgid=userView.getUserOrgId();
		    String deptid=userView.getUserDeptId();
		    String posid=userView.getUserPosId();
		    buf.append("&nbsp;");
		    buf.append(AdminCode.getCodeName("UN",orgid));
		    buf.append("&nbsp;");
		    buf.append(AdminCode.getCodeName("UM",deptid));
		    buf.append("&nbsp;");
		    buf.append(AdminCode.getCodeName("@K",posid));
    	}else{
    		if(display_field_info.indexOf("b0110")!=-1){
    			String orgid=userView.getUserOrgId();
    			buf.append("&nbsp;");
    			buf.append(AdminCode.getCodeName("UN",orgid));
    		}else if(display_field_info.indexOf("e0122")!=-1){
    			String deptid=userView.getUserDeptId();
    			buf.append("&nbsp;");
    			buf.append(AdminCode.getCodeName("UM",deptid));
    		}else if(display_field_info.indexOf("e01a1")!=-1){
    			String posid=userView.getUserPosId();
    			buf.append("&nbsp;");
    			buf.append(AdminCode.getCodeName("@K",posid));
    		}
    	}
    	buf.append("&nbsp;");
    	buf.append(userView.getUserFullName());
    	buf.append("&nbsp;");
    }	    
    if(status==0)
    {
    	userName=userView.getS_userName();
    	if(userView.isBEncryPwd())
    	{
    		Des des=new Des();
    		pwd=des.DecryPwdStr(userView.getS_pwd());
    	}
    	else
    		pwd=userView.getS_pwd();
    }
    else
    {
    	userName=userView.getUserName();
    	if(userView.isBEncryPwd())
    	{
    		Des des=new Des();
    		pwd=des.DecryPwdStr(userView.getPassWord());
    	}
    	else    	
    		pwd=userView.getPassWord();    
    }
    boolean bself=true;
    if(status!=4)
    {
        String a0100=userView.getA0100();
    	if(a0100==null||a0100.length()==0)
    	{
    		bself=false;
    	}
    }
    if(userView != null){
       css_url=userView.getCssurl();
       if(css_url==null||css_url.equals(""))
	  css_url="/css/css1.css";
       //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
    }
    
    String time = String.valueOf(System.currentTimeMillis()); 
%>
<!--  	    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />


<script type="text/javascript" src="../../ext/ext-all.js"></script>-->
<hrms:linkExtJs/>
<script type="text/javascript" src="../../ext/rpc_command.js"></script> 
<script type="text/javascript" src="/js/validate.js"></script> 
<script type="text/javascript" src="/ajax/basic.js"></script> 
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript">
//防止打开多个页面造成数据混乱 guodd 2015-12-18
window.document.oncontextmenu = function(){return false;};
 
  function logout()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/hrlogon.jsp";
   		url="/servler/sys/logout?flag=26";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }  
   function isclose()
  {
  	if(confirm("确定要退出吗？"))
  	{
   		var url = "/templates/close.jsp";
   		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
   		//parent.window.close();	
  	}
  }
  function mustclose()
  {
   		var url = "/templates/close.jsp";
   		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
   		//parent.window.close();	
  }
    //签到											
	function netsingin(singin_flag)
	{
		 var ip_addr="";
		 ip_addr=getLocalIPAddressf();
    	 //var hashvo=new ParameterSet();	
    	 //hashvo.setValue("singin_flag",singin_flag);
    	 //hashvo.setValue("ip_addr",ip_addr);
       // var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15502110200'},hashvo);
	
		var map = new HashMap();
        map.put("singin_flag",singin_flag);
        map.put("ip_addr",ip_addr);
        Rpc({functionId:'15502110200',success:showReturn},map);
	}
	function showReturn(response)
	{
      	/*var mess=outparamters.getValue("mess");
      	alert(mess);
       	if(mess.indexOf("成功")!=-1){
      		sysForm.action="/kq/kqself/card/carddata.do?b_query=link";
      		sysForm.target="il_body";
      		sysForm.submit();
      	}*/
      	var value=response.responseText;
      	//alert(value);
			var map=Ext.decode(value);
			if(map.succeed.toString()=='false'){
				alert(map.message);
			}else{
				var mess=map.mess;
				alert(mess);
				if(mess.indexOf("成功")!=-1){
		      		sysForm.action="/kq/kqself/card/carddata.do?b_query=link";
		      		sysForm.target="il_body";
		      		sysForm.submit();
	      		}
	      	}
	}
/**取得本地机器ip地址*/
function getLocalIPAddressf()
{
    var obj = null;
    var rslt = "";   
    try
    {
    	if(!AxManager.setup("axc", "SetIE", 0, 0, null, AxManager.setIEName))
	        return;
        
        obj=document.getElementById('SetIE');        
        rslt = obj.GetIP();        
        obj = null;
    }
    catch(e)
    {
    	//异常发生    	
    }
    return rslt;
}	
function change_agent(obj)
{
     document.getElementById("agentId").value=obj.value;
     sysForm.action="/selfservice/selfinfo/agent/agent.do?b_agent=link";     
     sysForm.target="_parent";
     sysForm.submit();
}
</script>
<SCRIPT language="javascript" type="text/javascript" >
function StringToDate(DateStr,TimeStr) 
{ 
　　
　　var arys= DateStr.split('.');
   var arts= TimeStr.split(':');
　  var myDate = new Date(arys[0],--arys[1],arys[2],arts[0],arts[1],arts[2]); 
   return myDate;
} 

function reloop(){    
    var timevalue = document.getElementById('timevalueid');
    var tmpvalue = parseInt(timevalue.value);
	var time=new Date(tmpvalue);
	timevalue.value=tmpvalue+1000;
	var datetime=time;	
	var month = time.getMonth()+1;
	var date = time.getDate();
	var year = time.getFullYear();
	var hour = time.getHours(); 
	var minute = time.getMinutes();
	var second = time.getSeconds();
					
	var day = time.getDay();
	if (minute < 10) 
	   minute="0"+minute;
	if (second < 10) 
	   second="0"+second; 
	var apm="AM"; 
	if (hour>12) 
	{
	    hour=hour-12;
	    apm="PM" ;
	}
	var weekday = 0;
	switch(time.getDay())
	{
		case 0:
		weekday = "星期日";
		break;
		case 1:
		weekday = "星期一";
		break;
		case 2:
		weekday = "星期二";
		break;
		case 3:
		weekday = "星期三";
		break;
		case 4:
		weekday = "星期四";
		break;
		case 5:
		weekday = "星期五";
		break;
		case 6:
		weekday = "星期六";
		break;
	}	
	var datestr=datetime.getFullYear()+"."+(datetime.getMonth()+1)+"."+datetime.getDate();	
	timestr=datetime.getHours()+":"+datetime.getMinutes()+":"+datetime.getSeconds();
	document.getElementById("t").value=year+"年"+month+"月"+date+"日"+" "+hour+":"+minute+":"+second+apm;
	//setTimeout("reloop(\""+datetime+"\")",1000);
	setTimeout("reloop()",1000);
 }	
 
 function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
}										
	function showDateSelectBox(srcobj)
   {
   		if(document.getElementById('selectname').value=="")
   		{
   			document.getElementById('date_panel').style.display="none";
   			return false ;
   		}
   		
      date_desc=document.getElementById(srcobj);
      document.getElementById('date_panel').style.display="";
      var pos=getAbsPosition(date_desc);
	  with(document.getElementById('date_panel'))
	  {
        style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-date_desc.offsetHeight+42;
		style.width=(date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1;
      }
      //var hashVo = new ParameterSet();
      var map = new HashMap();
      map.put("name",getEncodeStr(document.sysForm.selectname.value));
	  map.put("dbpre","usr");
	  map.put("showDb","1");
	  map.put("priv","1");
	  map.put("dbtype","0");
	  map.put("viewunit","0");
	  map.put("isfilter","0");
	  map.put("SYS_FILTER_FACTOR","");
	  map.put("flag","4");
	  Rpc({functionId:'3020071012',success:shownamelist},map);
      //var request=new Request({method:'post',onSuccess:shownamelist,functionId:'3020071012'},hashVo);
   }
   function shownamelist(outparamters)
   {
   		/*var namelist=outparamters.getValue("namelist");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(sysForm.contenttype,namelist);
		}*/
		var value=outparamters.responseText;
      	//alert(value);
			var map=Ext.decode(value);
			if(map.succeed.toString()!='false'){
				var namelist=map.namelist;
				if(namelist.length==0){
					document.getElementById('date_panel').style.display="none"
				}
				else{
					bind(sysForm.contenttype,namelist);
				}
			}else{
				document.getElementById('date_panel').style.display="none"
				alert(map.message);
			}
   }	
  
function bindSelect(elem,value)
{
		if (typeof(value) != "object" || value.constructor != Array) {
			this.reportError(elem,value,"Array Type Needed for binding select!");
		}
		// delete all the nodes.
		while (elem.childNodes.length > 0) {
			elem.removeChild(elem.childNodes[0]);
		}
		// bind data
		for (var i = 0; i < value.length; i++) 
		{
			var option = document.createElement("OPTION");
			var data = value[i];
			if (data == null || typeof(data) == "undefined") {
				option.value = "";
				option.text = "";
			}
			if (typeof(data) != 'object') {
				option.value = data;
				option.text = data;
			} else {
				option.value = data.dataValue;
				option.text = data.dataName;	
			}
			elem.options.add(option);
		}
}

function bind(elem,value)
{	
	bindSelect(elem, value);
}

function queryperson(e){
    e=e?e:(window.event?window.event:null);
    if (e.keyCode==13||e==1){  
        var selectname=document.getElementById("selectname").value;
        document.getElementById("selectname").value="";
        selectname=$URL.encode(getEncodeStr(selectname));
        if(selectname == null || selectname == ""){
            alert(selectmessage);
            return false;
        }
        sysForm.target="il_body";
        sysForm.action="/workbench/query/query_interface1.do?b_queryperson=link&selectname="+selectname+"&path=";
        sysForm.submit();
    }
}
									
</SCRIPT>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link type="text/css" rel="stylesheet" href="../../css/login6.css" media="all" />
<style type="text/css">
INPUT {
	font-size: 12px;
	border-style:none ;
    background-color:transparent;
}
</style>
</head>
<body onbeforeunload="">
<form name="sysForm" action="" method=post>
<input type="hidden" name="agentId">
<input type="hidden" id="timevalueid" value="<%=time %>"/>
   <div id="header">
       <div id="header_left">
	       <div class="logon"><!-- <img src="../../images/login6/logon.png" width="279" height="69" alt="" /> --></div>
		   <div class="nav"  id="toolbar">
           </div>
           <h2>
              <%if(view_time!=null&&view_time.equals("true")){ %>
     		        <INPUT id="t" name="time" type="text" size="27" readonly="readonly">
     		     <%}else{ %>
     		        <INPUT id="t2" name="time" type="text" size="20" readonly="readonly">
     		  <%} %>
	      </h2>	       
	   </div>   
	   
	   
	   <div id="header_right"> 
    	<div class="inner_header_right" id='rtools'>       	
    	 <div class="inner_header_left" id="ltools">  
    	 <div id="inner_header_left_1">  
    	  
		  <% if(bself){%>
    	    <hrms:agent></hrms:agent>
    	   <%} %>
		  
    	 </div>
		<ul>

		  	<li><a href="/templates/index/portal.do?b_query=link" target="il_body"><span>主页</span></a></li>

		  <hrms:priv func_id="3012" module_id=""> 
		  <li><% ntool += 1; %><a href="/system/options/portaltailor.do?b_search=link&portalid=02" target="il_body" class="nav02"><span><bean:message key="label.portal.options"/></span></a></li>
		  </hrms:priv>
		  <hrms:priv func_id="000101,3010" module_id=""> 	
		    <li><% ntool += 1; %><a href="/system/security/resetup_password.do" target="il_body" class="nav03"><span><bean:message key="label.mail.password"/></span></a></li>
		  </hrms:priv>
		  <hrms:priv func_id="3011,000102" module_id=""> 	
		    <li><% ntool += 1; %><a href="/system/security/about_hrp.do?b_query=link&status=1" target="il_body" class="nav04"><span><bean:message key="label.banner.product"/></span></a></li>
		  </hrms:priv>
		  <!-- 
		  <hrms:priv func_id="000107,3017" module_id=""> 
		  <li><% ntool += 1; %><a href="javascript:SetIEOpt();" class="nav05"><span>设置</span></a></li>
		  </hrms:priv>
		   -->
		  <hrms:priv func_id="000106,3016" module_id=""> 
		  <% if(bself){%>
		     <li><% ntool += 1; %><a href="/selfservice/selfinfo/agent/agentinfo.do?b_search=link" target="il_body" class="nav06"><span>代理</span></a></li>
		     <%} %>
		  </hrms:priv>
		  <% if(bself){%>						
		     <hrms:priv func_id="0B4" module_id="5"> 
		       <hrms:priv func_id="0B401" module_id="5"> 
		          <li><% ntool += 1; %><a href="###" onclick="javascript:netsingin('0');" class="nav07"><span>签到</span></a></li>
		        </hrms:priv> 
		        <hrms:priv func_id="0B405" module_id="5"> 
		         <li><% ntool += 1; %><a href="###" onclick="javascript:netsingin('1');" class="nav08"><span>签退</span></a></li>
		        </hrms:priv> 
		     </hrms:priv>
		  <% }%>	 
		   	
		    <li><% ntool += 1; %><a href="###" onclick="logout();" class="nav09"><span>注销</span></a></li>
		  <!-- 
		  <hrms:priv func_id="3013,000103" module_id="">  
		  </hrms:priv>
		   -->
		  <li><a href="###" onclick="isclose();" class="nav10"><span>退出</span></a></li>
		</ul>
		 </div>
	   </div>
	   </div>
	   <%if("1".equals(convenient_search)){ %>
	   <div><input class='queryid' title='<%=generalmessage %>' id="selectname" onkeypress="queryperson(event)" /><img src="/images/sou.png" style="clear:both;z-index: 2000; position: absolute;bottom: 7px;right: 31px;cursor: pointer;" onmouseover="javascript:ccc(this);" onmouseout="javascript:cccc(this);" alt="搜索" onclick="queryperson();"/></div>
		</div>
		<div id="date_panel" style="display:none;">
			<select id="date_box" name="contenttype"  onblur=""  multiple="multiple"  style="width:254" size="6" ondblclick="okSelect();">
			</select>
		</div>
		<%}else{ %>
		<%if(SystemConfig.isScrollWelcome()){%>
		<h1> 
				<MARQUEE id=m1 SCROLLAMOUNT="5" SCROLLDELAY="200">欢迎<%=buf.toString().trim()%>登录<%=sys_name%></MARQUEE>
	   </h1>
	   <%} %>
	   <%} %>
</form> 

  <hrms:priv func_id="000107,3017,0B4,0B401,0B405" module_id="">
    <div id='axc' style='display:none'/>
  </hrms:priv>

 <script type="text/javascript">
 function SetIEOpt()
 {
    if(!AxManager.setup("axc", "SetIE", 0, 0, SetIEOpt, AxManager.setIEName))
        return;
    var obj=document.getElementById('SetIE'); 
    if (obj != null)
    {
       obj.SetIEOptions('<%=url_p%>');      
    }     
 }
 </script>
 
<script language="JavaScript">
Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
Ext.useShims = true;
Ext.onReady(function()
{
	function clickHandler(item, e) {
    	window.open(item.href,item.hrefTarget); 
	}    
	/**输出主菜单*/
    <hrms:extmenu moduleid="9999"/>	
   
});

<%if(view_time!=null&&view_time.equals("true")){ %>
reloop();
<%}%>

function ccc(obj){
	obj.style.width="19px";
	obj.style.height="19px";
}
function cccc(obj){
	obj.style.width="18px";
	obj.style.height="18px";
}

function _w(){
		var tools=document.getElementById('header');
		var w=tools.clientWidth;
		var mlrw= w- <%=ntool * 24 %>;
		if(mlrw<770){
			tools.style.width= "<%=ntool * 24 + 820 %>px";
		}else{
			tools.style.width= "100%";
		}
}
$(window).resize(function (){
  // _w();
});
//window.onresize=_w;
</script>
</body>
</html>