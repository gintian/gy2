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
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PosBusinessForm posBusinessForm = (PosBusinessForm)session.getAttribute("posBusinessForm");
	String a_code = posBusinessForm.getA_code();
%>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="javascript">
function returnback(){
	<logic:empty name="posBusinessForm" property="param">
		selectNode('${posBusinessForm.a_code }')
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
  
function check(){
	var codeitemdesc = document.getElementsByName("codeitemdesc")[0].value;
	codeitemdesc = trim(codeitemdesc);
	if(codeitemdesc.length<1){
		var msg="名称";
				   <logic:equal value="PS_CODE" property="param" name="posBusinessForm">
                   		msg="职务名称";
                   </logic:equal>
                   <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
                   		msg="岗位名称";
                   </logic:equal>
        alert(msg+"不能为空!");
        return false;
	} else {
		if(IsOverStrLength(posBusinessForm.codeitemdesc.value,50))
	     {
	       alert(CODE_DESC_LEN_HINT);
	       return false;
	     }		
	}
	var codeitemdesc=$F("codeitemdesc");
  	var reg=/^[\\`~!#\$%^&\*()\+\{\}\|:"<>\?\-=/']*$/;
  	for(var i=0;i<codeitemdesc.length;i++){
		 var c=codeitemdesc.substring(i,i+1);
		 if(reg.test(c)){
		 	alert('名称不能是特殊字符!\n\`~!#$%^&*()+{}|\\:"<>?-=/\'');
		 	return false;
		 }
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
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/pos/posbusiness/searchposbusinesslist" onsubmit="return beforesave();"> 
<hrms:priv func_id="3007207,323812,3110803,40002">
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.posbusiness.maintenance"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   -->     
       		<td align="left" colspan="1" class="TableRow"><bean:message key="label.posbusiness.maintenance"/>&nbsp;</td>           	             	      
  </tr>  
   <tr>
      <td colspan="4" class="framestyle3" width="100%" align="center">
           <br>
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"> 
             <tr  align="right" class="list3" style="height:30px;">
               <td>
                  &nbsp;<bean:message key="label.posbusiness.curcode"/>&nbsp;
                </td>
               <td align="left">
                 <html:text style="width:300px;" name="posBusinessForm" property="codeitemid"  readonly="true" styleClass="textColorRead common_border_color" maxlength="${posBusinessForm.len}"/>
               </td>
             </tr> 
             <tr  align="right" class="list3" style="height:30px;">
                <td style="padding-left:50px;">
                   &nbsp;
                   <logic:equal value="PS_CODE" property="param" name="posBusinessForm">
                   		职务<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                   </logic:equal>
                   <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
                   		岗位<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                   </logic:equal>
                   <logic:notEqual value="PS_CODE" property="param" name="posBusinessForm">
                   		<logic:notEqual value="PS_C_CODE" property="param" name="posBusinessForm">
                   			<bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
                   		</logic:notEqual>
                   </logic:notEqual>
                </td>
               <td align="left">
                  <html:text   name="posBusinessForm" property="codeitemdesc"  styleClass="textColorWrite" maxlength="50" style="width:300px;"/>
               		<font color="red">*</font>
               </td>
             </tr> 
             <logic:notEqual value="yes" name="posBusinessForm" property="islevel">
	              <tr  align="right" class="list3" style="height:30px;">
	                <td>
	                   &nbsp;<logic:equal value="PS_CODE" property="param" name="posBusinessForm">
	                   		<bean:message key="conlumn.codeitemid.pscaption"/>&nbsp;
	                   </logic:equal>
	                   <logic:equal value="PS_C_CODE" property="param" name="posBusinessForm">
	                   		<bean:message key="conlumn.codeitemid.psccaption"/>&nbsp;
	                   </logic:equal>
	                  	<logic:notEqual value="PS_CODE" property="param" name="posBusinessForm">
	                  		<logic:notEqual value="PS_C_CODE" property="param" name="posBusinessForm">
	                  		转换代码&nbsp;
	                		</logic:notEqual>
	                	</logic:notEqual>
	                </td>
	               <td align="left">
	                  <html:text   name="posBusinessForm" property="corcode"  styleClass="textColorWrite" maxlength="50" style="width:300px;"/>
	               </td>
	             </tr> 
             </logic:notEqual>
             <logic:equal value="1" name="userView" property="version_flag">
             <logic:equal value="1" name="posBusinessForm" property="validateflag">
	             <tr  align="right" class="list3" style="height:30px;">
	                <td>
	                   &nbsp;<bean:message key="conlumn.codeitemid.start_date"/>&nbsp;
	                </td>
	                 <%
	                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                	String date = sdf.format(new Date());
	                 %>
	               <td align="left" style="padding-bottom: 3px;">
	                  <input type="text" name="start_date" value="${posBusinessForm.start_date }" maxlength="50" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='<%=date %>'; }"/>
	             <font color="red">*</font>
	             </td>
	             </tr> 
	             <tr  align="right" class="list3" style="height:30px;">
	                <td>
	                   &nbsp;<bean:message key="conlumn.codeitemid.end_date"/>&nbsp;
	                </td>
	               <td align="left">
	                  <input type="text" name="end_date" value="${posBusinessForm.end_date }" maxlength="50" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:150px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='9999-12-31'; }"/>
	               <font color="red">*</font>
	               </td>
	             </tr> 
             </logic:equal>
             </logic:equal>
             <tr><td>&nbsp;</td></tr>
          </table>
       </td>
   </tr> 
  
   <tr>  
   <td colspan="4" class="" width="100%" align="center">
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"   cellpadding="0" align="center"> 
             <tr  align="center">
                <td colspan="2" height="35px;">
                   <hrms:submit styleClass="mybutton"  property="b_update" onclick="return check();">
                     <bean:message key="button.save"/>
	           </hrms:submit>&nbsp;
	           <html:button property="b_return" styleClass="mybutton" value="返回" onclick="returnback();"></html:button>
                </td>
            </tr>  
            </table>
            </td>
            </tr> 

  </table>
  </hrms:priv>
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
