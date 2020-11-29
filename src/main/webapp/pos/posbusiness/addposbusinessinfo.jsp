<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.actionform.pos.PosBusinessForm"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PosBusinessForm posBusinessForm = (PosBusinessForm)session.getAttribute("posBusinessForm");
	String a_code = posBusinessForm.getA_code();
 %>

<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="javascript">
function validatelen() 
{
   <logic:equal name="posBusinessForm" property="first" value="0">
     if(posBusinessForm.codeitemid.value.length!="${posBusinessForm.len}")
     {
       //alert("<bean:message key="error.org.codelength"/>" + "${posBusinessForm.len}" + "!");//本机 
       alert("<bean:message key="label.posbusiness.curcode"/>" +"长度为${posBusinessForm.len}" + "!");//显示提示语句有误  本机 改为 本级 wangb 20170803 30316
       return false;
     }
    
   </logic:equal>   
    if(IsOverStrLength(posBusinessForm.codeitemdesc.value,50))
     {
       alert(ERROR_CODELENGTH_TOLONG + "50" + "!");
       return false;
     } 
     return beforesave();
}
function back()
{
	posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_query=del";
    posBusinessForm.submit();
}
function estop()
{
	return event.keyCode!=34&&event.keyCode!=39&&event.keyCode!=44;
}
function check(){
	var codeitemid=document.getElementsByName('codeitemid')[0].value;
	if(codeitemid.length==0){
		alert("本级代码不能为空！");
		return false;
	}
	if(!/^[A-Z0-9]*$/.test(codeitemid)){
		alert("本级代码只能为数字或大写字母！");
		return false;
	}
    /*代码长度校验是否通过 guodd 2020-02-11*/
    var codeWarn = document.getElementById("codeWarn");
	if(codeWarn && codeWarn.getAttribute("legal") == 'false'){
        return false;
    }

	var codeitemdesc=document.getElementsByName("codeitemdesc")[0].value;
	codeitemdesc = trim(codeitemdesc);
	if(codeitemdesc.length==0){
		alert("代码名称不能为空！");
		return false;
	}
	<logic:equal value="1" name="userView" property="version_flag">
	<logic:equal value="1" name="posBusinessForm" property="validateflag">
		if($F('start_date')==''){
			alert("有效日期起不能为空！");
			return false;
		}
		if($F('end_date')==''){
			alert("有效日期止不能为空！");
			return false;
		}
	</logic:equal>
	</logic:equal>
	var codeitemdesc=$F("codeitemdesc");
  	var reg=/^[\\`~!#\$%^&\*()\+\{\}\|:"<>\?\-=/']*$/;
  	for(var i=0;i<codeitemdesc.length;i++){
		 var c=codeitemdesc.substring(i,i+1);
		 if(reg.test(c)){
		 	alert('名称不能是特殊字符!\n\`~!#$%^&*()+{}|\\:"<>?-=/\'');
		 	return false;
		 }
  	}
	return true;
}
/*改为异步判断代码号是否存在 wangb 20171111 32588*/
var doingSave=false;
function check0(){
	//xus 18/5/15 37063  光标在保存按钮上 敲回车 会执行两次保存
	if(doingSave)
		return;
	doingSave=true;
  	var hashvo = new ParameterSet();
	//hashvo.setValue("codesetid",document.getElementsByName('code')[0].value);
	//hashvo.setValue("codeitemid",document.getElementsByName('codeitemid')[0].value);
	var a_code = '<%=a_code%>';
	//区分 a_code 保存值 代码类id 长度为2 ， 代码类+上级代码    wangb 20171219  33472
	if(a_code.length == 2){
		hashvo.setValue("codesetid",a_code);//获取代码类 codesetid 值
		hashvo.setValue("codeitemid",document.getElementsByName('codeitemid')[0].value);
	}else{
		hashvo.setValue("codesetid",'<%=a_code.substring(0,2)%>');//获取代码类codesetid 值
		hashvo.setValue("codeitemid",'<%=a_code.substring(2)%>'+document.getElementsByName('codeitemid')[0].value);//上级代码 +本级代码
	}
	var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'18010000020'},hashvo);
}

function check_ok(outparamters){
	var msg = outparamters.getValue('msg');
	if(msg == '1'){
		doingSave = false;
		alert("代码号已占用，请重新输入！");
		return;
	}
	posBusinessForm.action ='/pos/posbusiness/searchposbusinesslist.do?b_save=link';
	posBusinessForm.submit();
}
document.body.onkeydown=function(){
		if(window.event.keyCode=='13'){
			if(check()){
				//posBusinessForm.submit();
				check0();//执行异步判断代码号  wangb20171111 32558
			}
		}else
			window.returnValue=true;
}

function returnback(){
	<logic:empty name="posBusinessForm" property="param">
		selectNode('${posBusinessForm.a_code }');
	</logic:empty>
	posBusinessForm.action="/pos/posbusiness/searchposbusinesslist.do?b_query=link&encryptParam=<%=PubFunc.encrypt("full=1&a_code="+a_code)%>";
	posBusinessForm.submit();
}

function selectNode(codesetid){
  	var currnode=parent.frames['mil_menu'].Global.selectedItem;
  	if(codesetid==currnode.uid)
  		return;
  	if(currnode.uid=="root"){
  		var currnode1=null;
  		if(/^\d*$/.test(codesetid)){
  			currnode1=currnode.childNodes[0];
  		}else{
  			currnode1=currnode.childNodes[1];
  		}
  		if(!currnode1)
  			return;
		currnode1.openURL();
		currnode1.expand();
		var nodes = currnode1.childNodes;
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].uid==codesetid){
				var node = nodes[i];
				node.select();
			}
		}
	}else{
		currnode.openURL();
		currnode.expand();
		var nodes = currnode.childNodes;
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].uid==codesetid){
				var node = nodes[i];
				node.select();
			}
		}
	}
  }
  function beforesave(){
  <logic:equal value="1" name="userView" property="version_flag">
  <logic:equal value="1" name="posBusinessForm" property="validateflag">
  	var start_date=$F("start_date");
  	var end_date=$F("end_date");
  	//alert(start_date+" "+end_date);
  	//验证时间格式  jingq add 2014.6.9
  	if(TestTime(start_date)==false||TestTime(end_date)==false){
  		return false;
  	}
  	if(compareDate(start_date,end_date)){
  		alert("有效日期止不能小于有效日期起！");
  		return false;
  	}
  	</logic:equal>
  	</logic:equal>
  	return true;
  }
  function compareDate(DateOne,DateTwo)    
{     
   
var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ("-"));    
var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ("-")+1);    
var OneYear = DateOne.substring(0,DateOne.indexOf ("-"));    
   
var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ("-"));    
var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ("-")+1);    
var TwoYear = DateTwo.substring(0,DateTwo.indexOf ("-"));    
   
if (Date.parse(OneMonth+"/"+OneDay+"/"+OneYear) >    
Date.parse(TwoMonth+"/"+TwoDay+"/"+TwoYear))    
{    
return true;    
}    
else   
{    
return false;    
}    
  }
  function toBig(obj){
  	var v=obj.value;
  	var str=v.substring(v.length-1,v.length);
  	if(/^[a-z]*$/.test(str)){
  		//obj.value=v.substring(0,v.length-1)+str.toUpperCase();
  		obj.value=v.toUpperCase();
  	}
  }
  
//判断输入的日期是否为指定格式	YYYY-MM-DD
function TestTime(str){
	var temp = true;
	if(str!=null&&str.length>0){
		if(str.length==10){
			var s = str.split("");
			for(var i=0;i<s.length;i++){
				if(i==4||i==7){
					if(s[i]!="-"){
						alert('<bean:message key="search.date_style.error"/>');
						temp = false;
						break;
					}
				} else {
					var reg = /^[0-9]+[0-9]*]*$/;
					if(!reg.test(s[i])){
						alert('<bean:message key="search.date_style.error"/>');
						temp = false;
						break;
					}
				}
			}
		} else {
			alert('<bean:message key="search.date_style.error"/>');
			temp = false;
		}
	} else {
		temp = true;
	}
	return temp;
}

/*校验当前代码长度是否符合要求  guodd 2020-02-11*/
function checkCode(input){
    var len = ${posBusinessForm.len};
    var codeWarn = document.getElementById("codeWarn");
    if(!codeWarn)
        return;
    if(input.value.length<len){
        codeWarn.style.display='block';
        codeWarn.setAttribute("legal","false");
    }else{
        codeWarn.style.display='none';
        codeWarn.setAttribute("legal","true");
    }
}
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/pos/posbusiness/searchposbusinesslist" onsubmit="return validatelen()"> 
<html:hidden name="posBusinessForm" property="validateflag"/>
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.posbusiness.maintenance"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>    -->
       		<td align="left" colspan="1" class="TableRow"><bean:message key="label.posbusiness.maintenance"/>&nbsp;</td>           	      
  </tr>  
   <tr>
      <td colspan="4" class="framestyle3" width="100%" align="center">
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"> 
             <tr  align="center" class="list3">
              <td colspan="2" style="padding-top:5px;">
                 <div align="center"><bean:write name="posBusinessForm" property="labelmessage"  filter="true"/></div>
              </td>
             </tr>            
             <tr  align="right" class="list3" style="height:30px;">
                <td>
                  &nbsp;<bean:message key="label.posbusiness.superiorcode"/>&nbsp;
                </td>
                <td align="left">
                   <html:text   name="posBusinessForm" property="code" readonly="true" styleClass="textColorRead common_border_color" style="width:300px;"/>
                </td>
             </tr> 
             <tr  align="right" class="list3" style="height:30px;">
               <td style="padding-left:45px;">
                  &nbsp;<font color="red">*</font> <bean:message key="label.posbusiness.curcode"/>&nbsp;
                </td>
               <td align="left">
                 <html:text style="width:300px;"  name="posBusinessForm" property="codeitemid"  styleClass="textColorWrite" maxlength="${posBusinessForm.len}"  onkeypress="event.returnValue=estop(this)" onkeyup="toBig(this);checkCode(this);"/>
               </td>
             </tr>
             <!--添加代码长度错误提示信息，自定属性legal代表代码长度是否合法。有同级代码时显示，没同级代码不控制 guodd 2020-02-11 -->
             <logic:equal value="0" name="posBusinessForm" property="first">
             <tr>
                 <td></td><td> <span id="codeWarn" style="color:red;display:none;" legal="true">代码长度必须为${posBusinessForm.len}位</span></td>
             </tr>
             </logic:equal>
             <tr  align="right" class="list3" style="height:30px;">
                <td>
                   &nbsp;<font color="red">*</font>
                   <logic:equal value="PS_CODE" property="param" name="posBusinessForm">
                   		职务<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                   </logic:equal>
                   <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
                   		基准岗位<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                   </logic:equal>
                   <logic:notEqual value="PS_CODE" property="param" name="posBusinessForm">
                   		<logic:notEqual value="PS_C_CODE" property="param" name="posBusinessForm">
                   			<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                   		</logic:notEqual>
                   </logic:notEqual>
                </td>
               <td align="left">
                  <html:text style="width:300px;"  name="posBusinessForm" property="codeitemdesc"  styleClass="textColorWrite" maxlength="50"/>
               </td>
             </tr> 
             <logic:notEqual value="yes" name="posBusinessForm" property="islevel">
	              <tr  align="right" class="list3">
	                <td>
	                   &nbsp;
	                   <logic:equal value="PS_CODE" property="param" name="posBusinessForm">
	                   		<bean:message key="conlumn.codeitemid.pscaption"/>&nbsp;
	                   </logic:equal>
	                   <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
	                   		基准<bean:message key="conlumn.codeitemid.psccaption"/>&nbsp;
	                   </logic:equal>
	                  	<logic:notEqual value="PS_CODE" property="param" name="posBusinessForm">
	                  		<logic:notEqual value="PS_C_CODE" property="param" name="posBusinessForm">
	                  		转换代码&nbsp;
	                		</logic:notEqual>
	                	</logic:notEqual>
	                </td>
	               <td align="left">
	                  <html:text style="width:300px;"  name="posBusinessForm" property="corcode"  styleClass="textColorWrite" maxlength="50"/>
	               </td>
	             </tr> 
             </logic:notEqual>
             <logic:equal value="1" name="userView" property="version_flag">
             <logic:equal value="1" name="posBusinessForm" property="validateflag">
             	<tr  align="right" class="list3">
	                <td>
	                   &nbsp;<font color="red">*</font> <bean:message key="conlumn.codeitemid.start_date"/>&nbsp;
	                </td>
	                <%
	                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                	String date = sdf.format(new Date());
	                 %>
	               <td align="left" height="30px">
	                    <input type="text" name="start_date" value="<%=date %>" maxlength="50" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='<%=date %>'; }"/>
	             </td>
	             </tr>
	             <tr  align="right" class="list3">
	                <td height="30px">
	                   &nbsp;<font color="red">*</font> <bean:message key="conlumn.codeitemid.end_date"/>&nbsp;
	                </td>
	               <td align="left">
	                  <input type="text" name="end_date" value="9999-12-31" maxlength="50" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='9999-12-31'; }"/>
	               </td>
	             </tr>
             </logic:equal>
             </logic:equal>
             <tr><td>&nbsp;</td></tr>
          </table>
       </td>
   </tr> 
     <tr style="padding-top: -5px;">
        <td colspan="4" width="100%" align="center">
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"   cellpadding="0" align="center"> 
             <tr  align="center">
                <td colspan="2" height="35px;" align="center">
					<!-- 改为普通按按钮 异步方式检验代码号 wangb 20171111 32558 -->
                	<input type="button" name="b_save" onclick ="return check() && check0();" class="mybutton" value="<bean:message key="button.save"/>"/>                
                	<!--<hrms:submit styleClass="mybutton"  property="b_save" onclick="return check();">
                     			<bean:message key="button.save"/> </hrms:submit>-->
	           		<input type="button" name="btnreturn" value="<bean:message key="button.return"/>" onclick="returnback();" class="mybutton">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
             </tr>  
           </table>
        </td>
      </tr>
  </table>
</html:form>
<script language="javascript">
  if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20190308
	  //今天日期文字 大小和显示位置处理   wangb  20190308
	  var lblToday = document.getElementById('lblToday');
	  lblToday.style.lineHeight='';
  	  var td = lblToday.parentNode;
  	  td.style.position = 'relative';
  	  var a = lblToday.getElementsByTagName('a')[0];
  	  a.style.fontSize = '12px';
  	  a.style.position = 'absolute';	
  	  a.style.right = '-7px';	
 	  a.style.top = '4px';	
  	  a.style.transform = 'scale(0.75)';
  	  var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
  	  var isSafari = userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") == -1; //判断是否Safari浏览器 
	  if(isSafari){// safari 浏览器 特殊处理    今天日期文字显示位置处理   wangb  20190308
		 var stimeIframe = document.getElementById('stime');
		 stimeIframe.setAttribute('height','30');
		 a.style.right = '-10px';	
	 	 a.style.top = '12px';
	 	 a.style.fontSize='13px';
	  }
  }
</script>