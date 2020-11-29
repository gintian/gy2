<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.transaction.ykcard.ThrowsInfoSelfYkcardTrans,java.sql.Connection"%>
<%@page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.ykcard.CardTagParamForm,com.hrms.frame.utility.AdminDb"%>
<%@page import="com.hjsj.hrms.businessobject.ykcard.DataEncapsulation,com.hjsj.hrms.valueobject.ykcard.CardTagParamView"%>
<%

Connection conn =null;
UserView userView=null;
try{
	userView=(UserView)session.getAttribute(WebConstant.userView);
	conn = AdminDb.getConnection();
	CardTagParamForm wx_cardTagParamForm= (CardTagParamForm)session.getAttribute("wx_cardTagParamForm");
	String b_init = request.getParameter("b_init");
	if(b_init!=null){
		ThrowsInfoSelfYkcardTrans t = new ThrowsInfoSelfYkcardTrans();
		HashMap map = new HashMap();
		HashMap formHM = new HashMap();
		map.put("requestPamaHM", formHM);
		formHM.put("flag", request.getParameter("flag"));
		formHM.put("a0100", request.getParameter("a0100"));
		formHM.put("pre", request.getParameter("pre"));
		formHM.put("userbase", request.getParameter("userbase"));
		formHM.put("tabid", request.getParameter("tabid"));
		formHM.put("b0110", request.getParameter("b0110"));
		t.setFormHM(map);
		t.setUserView(userView);
		t.execute();
	
		wx_cardTagParamForm = new CardTagParamForm();
		wx_cardTagParamForm.setA0100((String)map.get("a0100"));
		wx_cardTagParamForm.setFlag((String)map.get("flag"));
		wx_cardTagParamForm.setPre((String)map.get("pre"));
		wx_cardTagParamForm.setUserbase((String)map.get("userbase"));
		wx_cardTagParamForm.setTabid((String)map.get("tabid"));
		wx_cardTagParamForm.setB0110((String)map.get("b0110"));
		wx_cardTagParamForm.setCardtype((String)map.get("cardtype"));
		
		pageContext.setAttribute("wx_cardTagParamForm", wx_cardTagParamForm);
		session.setAttribute("wx_cardTagParamForm", wx_cardTagParamForm);
	}else{
		
		CardTagParamView cardparam=new CardTagParamView();
		wx_cardTagParamForm.setCardparam(cardparam);
		String queryflagtype= request.getParameter("cardparam.queryflagtype");
		int tmpint = 0;
		try{
			tmpint = Integer.parseInt(queryflagtype);
		}catch(Exception e){
		}
		cardparam.setQueryflagtype(tmpint);
		try{
			tmpint = Integer.parseInt(request.getParameter("cardparam.cyear"));
		}catch(Exception e){
		}
		cardparam.setCyear(tmpint);
		try{
			tmpint = Integer.parseInt(request.getParameter("cardparam.csyear"));
		}catch(Exception e){
		}
		cardparam.setCsyear(tmpint);
		try{
			tmpint = Integer.parseInt(request.getParameter("cardparam.cyyear"));
		}catch(Exception e){
		}
		cardparam.setCyyear(tmpint);
		try{
			tmpint = Integer.parseInt(request.getParameter("cardparam.cmonth"));
		}catch(Exception e){
		}
		cardparam.setCmonth(tmpint);
		try{
			tmpint =Integer.parseInt(request.getParameter("cardparam.season"));
		}catch(Exception e){
		}
		cardparam.setSeason(tmpint);
		try{
			tmpint = Integer.parseInt(request.getParameter("cardparam.ctimes"));
		}catch(Exception e){
		}
		cardparam.setCtimes(tmpint);
		cardparam.setCdatestart(request.getParameter("cardparam.cdatestart"));
		cardparam.setCdateend(request.getParameter("cardparam.cdateend"));
	}	
	}catch(Exception e){
		e.printStackTrace();
	}	finally{
		if(conn!=null){
		try{
			conn.close();
			}catch(Exception e){
			e.printStackTrace();
			}
		}
	}
	
	String isMobile = request.getParameter("isMobile");
	String isIOS = request.getParameter("isIOS");
    String _return = request.getParameter("return");
	String browser = DataEncapsulation.analyseBrowser(request.getHeader("user-agent"));
	String a0100 = request.getParameter("a0100");
	
	if(userView != null)
	{
		if(a0100==null||a0100.length()==0)
			a0100=userView.getA0100();
	}
	
 %>
 
 <script type="text/javascript" src="/ext/ext-all.js"></script>
<script type="text/javascript" src="/ext/rpc_command.js"></script>

 <% if(!("1".equals(isMobile)&&"0".equals(_return))){%>
<script type="text/javascript">
function validatedate()
{
	<%if("1".equals(isMobile)){%>	
	var _queryflagtype=document.getElementById("queryflagtypeid");
	if(_queryflagtype.value==2){
		var str=document.getElementById("starttimeid").value;
		if(str.length>0&&!chechdate(str)){
			alert("请输入正确的日期!如:2010-1-25");
			return false;
		}
		str=document.getElementById("endtimeid").value;
		if(str.length>0&&!chechdate(str)){
			alert("请输入正确的日期!如:2010-1-25");
			return false;
		}
	}	
	<%} %>
	return true;
}

function chechdate(str){
		var reg = /^(\d{1,4})-(\d{1,2})-(\d{1,2})$/; //创建正则表达式校验时间对象
		var r = str.match(reg);
		if(r==null)
			return false;
		else
		{
			var d= new Date(r[1], --r[2],r[3]);
			if(d.getFullYear()!=r[1])
				return false;
			if(d.getMonth()!=r[2])
				return false;
			if(d.getDate()!=r[3])
				return false;
		}
		return true;
}
//-->
</script>
<%} %>
<body <%if(!"1".equals(isMobile)){%>ondragstart="return false" onselectstart ="return false" onselect="return false" oncopy="return false" onbeforecopy="return false"onmouseup="return false"<%} %>>         
<form action="/ykcard/wx_selfcard.jsp" method="post"  name="wx_cardTagParamForm">
	<input type="hidden" name="isMobile" value="<%=isMobile %>" />
	<input type="hidden" name="isIOS" value="<%=isIOS %>" />
	<input type="hidden" name="return" value="<%=_return %>" />
	<input type="hidden" name="a0100" value="<%=a0100 %>" />
	<table>
		<tr width="100%">
			<td>
               <hrms:ykcard name="wx_cardTagParamForm"  property="cardparam" nid="<%=a0100 %>" b0110="${wx_cardTagParamForm.b0110}" nbase="${wx_cardTagParamForm.pre}" tabid="${wx_cardTagParamForm.tabid}" cardtype="${wx_cardTagParamForm.cardtype}" disting_pt="javascript:screen.width" userpriv="selfinfo" havepriv="1" istype="0" infokind="1" fieldpurv="1" queryflag="0" isMobile="<%=isMobile %>"  browser="<%=browser %>" returnvalue="<%=_return %>"/>
		    </td>
		</tr>
	</table>
	<%if("1".equals(isMobile)&&"0".equals(_return)){ %>
	   <input style="display: none" type="submit"  id="androidSubmit" value="ss">
	<%} %>

</form>
</body>
<script language='javascript'>
function showPDF(response) {
		var value=response.responseText;
		var map=Ext.decode(value);
	<% if ("1".equals(isMobile)) { %>
        <% if ("1".equals(isIOS)) { %> // ios端
	        if (map.succeed) {
	            var url = map.url;
	            document.location = "objc::loadingPDF::/servlet/DisplayOleContent?mobile=1&filename=" + url;
	        } else { //有错误
	            var message = map.message;
				message =escape(message).toLocaleLowerCase().replace(/%u/gi,"\\u");
				document.location="objc::showToast::"+message; 
	        } 
        <% } else { %> // android端
	        if (map.succeed) {
	            var url = map.url;
	            window.location.target = "_blank";
	            window.location.href = "/servlet/DisplayOleContent?mobile=1&filename=" + url;
	        } else { //有错误
	        	window.Android.showToast(map.message);
	        } 
        <% } %>
	<% } else { %>
        var url = map.url;
	url=decode(url)
        var win = open("/servlet/vfsservlet?fromjavafolder=true&fileid=" + url);
	<% } %>
}

function excecutePDF()
{

        var map = new HashMap();
        var tab_id="${wx_cardTagParamForm.tabid}";
        if(tab_id=="")
        {
           tab_id="${re_tabid}";  
        }
        map.put("nid","<%=a0100 %>");
        map.put("b0110","${wx_cardTagParamForm.b0110}");
        map.put("flag","${wx_cardTagParamForm.flag}");
        map.put("tabid",tab_id);
        map.put("cardid","1");
        if(${wx_cardTagParamForm.cardparam.queryflagtype}==1)
        {
           map.put("cyear","${wx_cardTagParamForm.cardparam.cyear}");
        }else if(${wx_cardTagParamForm.cardparam.queryflagtype}==3)
        {
           map.put("cyear","${wx_cardTagParamForm.cardparam.csyear}");
        }else if(${wx_cardTagParamForm.cardparam.queryflagtype}==4)
        {
           map.put("cyear","${wx_cardTagParamForm.cardparam.cyyear}");
        }        
        map.put("userpriv","noinfo");
        map.put("istype","0");        
        map.put("cmonth","${wx_cardTagParamForm.cardparam.cmonth}");
        map.put("season","${wx_cardTagParamForm.cardparam.season}");
        map.put("ctimes","${wx_cardTagParamForm.cardparam.ctimes}");
        map.put("cdatestart","${wx_cardTagParamForm.cardparam.cdatestart}");
		map.put("cdateend","${wx_cardTagParamForm.cardparam.cdateend}");
		map.put("querytype","${wx_cardTagParamForm.cardparam.queryflagtype}");
		map.put("infokind","1");
		map.put("userbase","${wx_cardTagParamForm.userbase}");
	    map.put("pre","${wx_cardTagParamForm.pre}");
		map.put("fieldpurv","1");
	    map.put("exce","PDF");
	    <%if("1".equals(isMobile)){%>
	    map.put("isMobile","1"); 
	    var platform=navigator.platform;
		map.put("platform",platform);
		<%}%>
	    Rpc({functionId:'07020100005',success:showPDF},map); 
	/*
		var hashvo=new ParameterSet();
        var tab_id="${wx_cardTagParamForm.tabid}";  
        if(tab_id=="")
        {
           tab_id="${re_tabid}";  
        } 
        hashvo.setValue("nid","${userView.a0100}");
        hashvo.setValue("b0110","${wx_cardTagParamForm.b0110}");
        hashvo.setValue("flag","${wx_cardTagParamForm.flag}");
        hashvo.setValue("tabid",tab_id);
        hashvo.setValue("cardid","1");
        if(${wx_cardTagParamForm.cardparam.queryflagtype}==1)
        {
           hashvo.setValue("cyear","${wx_cardTagParamForm.cardparam.cyear}");
        }else if(${wx_cardTagParamForm.cardparam.queryflagtype}==3)
        {
           hashvo.setValue("cyear","${wx_cardTagParamForm.cardparam.csyear}");
        }else if(${wx_cardTagParamForm.cardparam.queryflagtype}==4)
        {
           hashvo.setValue("cyear","${wx_cardTagParamForm.cardparam.cyyear}");
        }        
        hashvo.setValue("userpriv","selfinfo");
        hashvo.setValue("istype","0");        
        hashvo.setValue("cmonth","${wx_cardTagParamForm.cardparam.cmonth}");
        hashvo.setValue("season","${wx_cardTagParamForm.cardparam.season}");
        hashvo.setValue("ctimes","${wx_cardTagParamForm.cardparam.ctimes}");
        hashvo.setValue("cdatestart","${wx_cardTagParamForm.cardparam.cdatestart}");
		hashvo.setValue("cdateend","${wx_cardTagParamForm.cardparam.cdateend}");
		hashvo.setValue("querytype","${wx_cardTagParamForm.cardparam.queryflagtype}");
		hashvo.setValue("infokind","1");
		hashvo.setValue("userbase","${wx_cardTagParamForm.userbase}");
	        hashvo.setValue("pre","${wx_cardTagParamForm.pre}");
		hashvo.setValue("fieldpurv","1");
	    var In_paramters="exce=PDF"; 
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'07020100005'},hashvo);
	*/
}
function changTabid(obj) { 
<% if ("1".equals(isMobile)) { %>
	var tabid = obj; 
<% } else { %>
    var tabid = obj.value; 
<% } %>
    var a0100 = "${wx_cardTagParamForm.a0100}";
    var pre = "${wx_cardTagParamForm.pre}";
    var b0110 = "${wx_cardTagParamForm.b0110}";
    var flag = "${wx_cardTagParamForm.flag}";
    document.wx_cardTagParamForm.pageid.value = 0;
    document.wx_cardTagParamForm.action = "/ykcard/wx_selfcard.jsp?a0100=" + a0100 + "&flag=" + flag + "&pre=" + pre + "&b0110=" + b0110 + "&tabid=" + tabid;
    document.wx_cardTagParamForm.submit();
}
<% if("0".equals(_return)){%>
function returnback(){
	document.wx_cardTagParamForm.action="/phone-app/app/emolument.do?b_init=link&flag=infoself";
    document.wx_cardTagParamForm.submit();
}
<%}%>
<% if("1".equals(isMobile)&&"0".equals(_return)){%>
	<% if("1".equals(isMobile)&&"0".equals(_return)&&!"1".equals(isIOS)){%>//tiany begin 修改ios时无需调用该方法
		Android.setPer_year_list_str(document.getElementById('per_year_list_strid').value);
	<%} %>
	//end 
function doAndroidParam(queryflagtype,cyyear,cyear,cmonth,cdatestart,cdateend,csyear,season){

	document.getElementsByName("cardparam.queryflagtype")[0].value=queryflagtype;
	document.getElementsByName("cardparam.cyyear")[0].value=cyyear;
	document.getElementsByName("cardparam.cyear")[0].value=cyear;
	document.getElementsByName("cardparam.cmonth")[0].value=cmonth;
	document.getElementsByName("cardparam.cdatestart")[0].value=cdatestart;
	document.getElementsByName("cardparam.cdateend")[0].value=cdateend;
	document.getElementsByName("cardparam.csyear")[0].value=csyear;
	document.getElementsByName("cardparam.season")[0].value=season;
	document.getElementById("androidSubmit").click();
}

<%}%>
</script>   
