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
    int version = lockclient.getVersion();
    boolean bflag=lockclient.isBtest();
    String verdesc="";
    if(bflag)
    {
    	verdesc=ResourceFactory.getProperty("label.sys.about.test");
    }      
    String css_url="/css/css1.css";
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
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
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
	  <!--    <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
	    
	    
	    <script type="text/javascript" src="../../ext/ext-all.js"></script>
	     <script type="text/javascript" src="../../ext/ext-lang-zh_CN.js"></script>-->
	     <hrms:linkExtJs/>
		<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js"></script>
	    <script type="text/javascript" src="../../ext/rpc_command.js"></script> 
		<link type="text/css" rel="stylesheet" href="../../css/login6.css" media="all" />
		<link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" />
</head>
<script language="JavaScript">
<%--function SetIEOpt()
   {
      var obj=document.getElementById('SetIE'); 
      if (obj != null)
      {
         obj.SetIEOptions('<%=url_p%>');      
      }     
   }--%>
  function isclose()
  {
  	if(confirm("确定要注销吗？"))
  	{
   		var url = "/templates/index/bilogon.jsp";
   		url="/servler/sys/logout?flag=45";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
		//window.opener=null;//不会出现提示信息
   		//parent.window.close();	
  	}
  }
  function mustclose()
  {
   		var url = "/templates/index/bilogon.jsp";
   		url="/servler/sys/logout?flag=45";
		newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }
  
  function reNew(str)
{
    var re;
	re=/%26amp;/g;
	str=str.replace(re,"&");
	re=/%26apos;/g;  
	str=str.replace(re,"'");
	re=/%26lt;/g;  
	str=str.replace(re,"<");
	re=/%26gt;/g;  
	str=str.replace(re,">");
	re=/%26quot;/g;  
	str=str.replace(re,"\"");
	re=/%25/g;
	str=str.replace(re,"%");
	re=/````/g;
	str=str.replace(re,",");
	return(str);		
}
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
function trim(s)
{ 
	return s.replace(/^\s+|\s+$/, ''); 
}
  function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
   }
function  keyWord_filter(value)
	{ 
	   // return value;
	
		if (value == null||trim(value).length==0) {
            return value;
        }   
        var result="";
        for (var i=0; i<value.length; ++i) {
            switch (value.charAt(i)) {
	            case '<':
	                result+="＜";
	                break;
	            case '>': 
	                result+="＞";
	                break;
	            case '"': 
	                result+="＂";
	                break;
	            case '\'': 
	                result+="＇";
	                break; 
	            case ';': 
	                result+="；";
	                break;
	            case '(': 
	                result+="〔";
	                break;
	            case ')': 
	                result+="〕";
	                break; 
	            case '+':
	                result+="＋";
	                break;
	            default:
	                result+=value.charAt(i);
	                break;
            }    
        } 
     	
 		result=replaceAll(result,"--", "－－");
        result=replaceAll(result,"%3C","＜");
        result=replaceAll(result,"%3c","＜");
        result=replaceAll(result,"%3E","＞");
        result=replaceAll(result,"%3e","＞");
        result=replaceAll(result,"%22","＂");
        result=replaceAll(result,"%27","＇");
		result=replaceAll(result,"%3B","；");
		result=replaceAll(result,"%3b","；");
		result=replaceAll(result,"%28","〔");
		result=replaceAll(result,"%29","〕");
		result=replaceAll(result,"%2B","＋");
		result=replaceAll(result,"%2b","＋");   
        return result;
	}
function encode(strIn)
{
	strIn=keyWord_filter(strIn); //过滤特殊字符，防止XSS跨站,SQL注入漏洞  dengcan
	var intLen=strIn.length;
	var strOut="";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp=strIn.charCodeAt(i);
		if (strTemp>255)
		{
			tmp = strTemp.toString(16);
			for(var j=tmp.length; j<4; j++) tmp = "0"+tmp;
			strOut = strOut+"^"+tmp;
		}
		else
		{
			if (strTemp < 48 || (strTemp > 57 && strTemp < 65) || (strTemp > 90 && strTemp < 97) || strTemp > 122)
			{
				tmp = strTemp.toString(16);
				for(var j=tmp.length; j<2; j++) tmp = "0"+tmp;
				strOut = strOut+"~"+tmp;
			}
			else
			{
				strOut=strOut+strIn.charAt(i);
			}
		}
	}
	return (strOut);
}
function getEncodeStr(str) {
	return encode(getValidStr(str));
	//return escape(getValidStr(str));
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
			var map=Ext.util.JSON.decode(value);
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
</script>
<body onbeforeunload="">
   <div id="header">
       <div id="header_left">
       <%if(version < 70) { %>
	   	<div class="logon"><img src="../../images/login6/logon.png" width="279" height="69" alt="" />
	   <%} else { %>
	    <div class=""><img src="../../images/login6/hcmlogon.png" width="279" height="69" alt="" />
	   <%} %>
	   		<div class="nav" style="width:100%;width:expression(document.body.clientWidth -510)" id="toolbar">
           </div>	
	   	</div>
		          
	   </div>   
	   
	   
	   <div id="header_right">
    	 	<div class="inner_header_right">  
    	 <div class="inner_header_left">  	 
		<ul>
		  <li><a href="/templates/index/bi_portal.do?b_query=link" target="i_body"><span>主页</span></a></li>
		  <li><a href="/system/options/portaltailor.do?b_search=link&portalid=01" target="i_body" class="nav02"><span><bean:message key="label.portal.options"/></span></a></li>
		  <hrms:priv func_id="000101,3010" module_id=""> 	
		    <li><a href="/system/security/resetup_password.do" target="i_body" class="nav03"><span><bean:message key="label.mail.password"/></span></a></li>
		  </hrms:priv>
		  <hrms:priv func_id="3011,000102" module_id=""> 	
		    <li><a href="/system/security/about_hrp.do?b_query=link&status=1" target="i_body" class="nav04"><span><bean:message key="label.banner.product"/></span></a></li>
		  </hrms:priv>
<%--		  <hrms:priv func_id="000107,3017" module_id=""> 
		  <li><a href="javascript:SetIEOpt();" class="nav05"><span>设置</span></a></li>
		  </hrms:priv>--%>
		  <hrms:priv func_id="3013,000103" module_id=""> 	
		    <li><a href="###" onclick="isclose();" class="nav06"><span>注销</span></a></li>
		  </hrms:priv>
		  <li><a href="###" onclick="parent.window.close();" class="nav07"><span>退出</span></a></li>
		</ul>
		 </div>
		 	 
	   </div>
	   </div>
 <%if("1".equals(convenient_search)){ %>
	   <div><input class='queryid' title='<%=generalmessage %>' id="selectname" onkeyup="" /><img src="/images/sou.png" style="clear:both;z-index: 2000; position: absolute;bottom: 7px;right: 31px;cursor: pointer;" onmouseover="javascript:ccc(this);" onmouseout="javascript:cccc(this);" alt="搜索" onclick="queryperson();"/></div>
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
  			  <%}%>	
  	   <%}%>
   </div>
   <div id="main_content_border"  style="z-index: 999;width: 100%">
      <iframe src="/templates/index/bi_portal.do?b_query=link" name="i_body" id="center_iframe" scrolling="auto" width="100%" frameborder="0" style="z-index:999; "></iframe>     
   </div>   
<%--<hrms:priv func_id="000107,3017,0B4,0B401,0B405" module_id=""> 
<div id='axc' style='display:none'/>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }

 InitAx();
 </script>
</hrms:priv>--%>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
Ext.BLANK_IMAGE_URL="../../ext/resources/images/default/s.gif";
Ext.useShims = true;
Ext.onReady(function()
{
	function clickHandler(item, e) {
    	window.open(item.href,item.hrefTarget); 
	}    
	/**输出主菜单*/
    <hrms:extmenu moduleid="21"/>	
<%if("1".equals(session.getAttribute("isSSO"))){// 单点登录进来才检查 %>
    AxManager.checkBrowserSettings('<%=url_p%>');
<%}%>
});
function ccc(obj){
	obj.style.width="19px";
	obj.style.height="19px";
}
function cccc(obj){
	obj.style.width="18px";
	obj.style.height="18px";
}
function queryperson(){
	var selectname=document.getElementById("selectname").value;
	document.getElementById("selectname").value="";
	selectname=$URL.encode(getEncodeStr(selectname));
	if(selectname == null || selectname == ""){
		alert('<%=selectmessage%>');
		return false;
	}
	//sysForm.target="i_body";
	//sysForm.action="/workbench/query/query_interface1.do?b_queryperson=link&selectname="+selectname+"&path=bi";
	//sysForm.submit();
	var url = "/workbench/query/query_interface1.do?b_queryperson=link&selectname="+selectname+"&path=bi";
	newwin=window.open(url,"i_body","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
}
setHight();
	function setHight() { 
		var obj = document.getElementById("center_iframe");
		var bodyHeight = document.body.offsetHeight;
		var bodyWidth = document.body.offsetWidth;
		//var divHeight = document.getElementById("div").offsetHeight;
		obj.height = bodyHeight - 70;
		obj.width = bodyWidth;
	}
	
$(window).resize(function (){
   setHight();
});
</script>
</body>
</html>