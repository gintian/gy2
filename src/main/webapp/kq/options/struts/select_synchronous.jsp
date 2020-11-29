<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.kq.options.struts.KqStrutForm" %>
<%
	KqStrutForm form = (KqStrutForm) request.getSession().getAttribute("kqStrutForm");
	List list = form.getConnStrList();
 %>
 
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/options/struts/kqsynchronous" method="post">
<table width="80%" align="center">
	<tr>
		<td class="RecordRow" valign="top">
			<br/>
			<table width="90%" align="center" cellpadding="0" cellspacing="0">
				<tr bgcolor="#F4F7F7">
					<td class="TableRow" align="center" style="border-right: 0px;" nowrap>
						<input type="checkbox" name="synAll" onclick="selAll(this);" />
					</td>
					<td class="TableRow" align="center" style="border-right: 0px;" nowrap>
						&nbsp;序号&nbsp;
					</td>
					<td class="TableRow" align="center" style="border-right: 0px;" nowrap>
						&nbsp;描述&nbsp;
					</td>
					<td class="TableRow" align="center" style="border-right: 0px;" nowrap>
						&nbsp;类型&nbsp;
					</td>
					<td class="TableRow" align="center" style="border-right: 0px;" nowrap>
						&nbsp;IP地址&nbsp;
					</td>
					<td class="TableRow" align="center" style="border-right: 0px;" nowrap>
						&nbsp;状态&nbsp;
					</td>
					<td class="TableRow" align="center" nowrap>
						&nbsp;编辑&nbsp;
					</td>
				</tr>
				<logic:iterate id="ob" name="kqStrutForm" property="connStrList" indexId="index">
				<tr>
					<td class="RecordRow" align="center" style="border-top: 0px;border-right: 0px;" nowrap>
						<input type="checkbox" name="check<%=index %>" value="<bean:write name="ob" property="id"/>"/>
					</td>
					<td class="RecordRow" align="center" style="border-top: 0px;border-right: 0px;" nowrap>
						&nbsp;<bean:write name="ob" property="id"/>&nbsp;
					</td>
					<td class="RecordRow" align="center" style="border-top: 0px;border-right: 0px;" nowrap>
						&nbsp;<bean:write name="ob" property="desc"/>&nbsp;
					</td>
					<td class="RecordRow" align="center" style="border-top: 0px;border-right: 0px;" nowrap>
						&nbsp;<bean:write name="ob" property="type"/>&nbsp;
					</td>
					<td class="RecordRow" align="center" style="border-top: 0px;border-right: 0px;" nowrap>
						&nbsp;<bean:write name="ob" property="ip"/>&nbsp;
					</td>
					<td class="RecordRow" align="center" style="border-top: 0px;border-right: 0px;" nowrap>
						<logic:notEqual name="ob" property="status" value="1">
						&nbsp;<bean:message key="column.sys.unvalid"/>&nbsp;
						</logic:notEqual>
						<logic:equal name="ob" property="status" value="1">
						&nbsp;<bean:message key="column.sys.valid"/>&nbsp;
						</logic:equal>
					</td>
					<td class="RecordRow" align="center" style="border-top: 0px;" nowrap>
						&nbsp;
						<img src="/images/edit.gif" border=0 onclick="updateone('2','<bean:write name="ob" property="id"/>')" style="cursor: pointer;">
						&nbsp;
					</td>
				</tr>
				</logic:iterate>
				<tr>
					<td colspan="7" align="center"
						style="padding-top: 6px; padding-left: 20px;">
						<html:button property="b_add" styleClass="mybutton" onclick="updateone('1')"><bean:message key="button.insert"/></html:button>
						<input type="button" class="mybutton" value="启用" onclick="updateone('4')"/>
						<input type="button" class="mybutton" value="暂停" onclick="updateone('5')"/>
						<input type="button" class="mybutton" value="删除" onclick="updateone('3')"/>
						<input type="button" class="mybutton" value="重新同步" onclick="sync()"/>
					</td>
				</tr>
			</table>
			<br>
		</td>
	</tr>
</table>

	<div id='wait' style='position:absolute;top:200;left:400;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>
</html:form>
<script>
	function selAll(all){
		var ckbs = document.getElementsByTagName("input");
		for(var i=0;i<ckbs.length;i++){
			if(ckbs[i].type=="checkbox"&&ckbs[i].name!="synAll"){
				ckbs[i].checked=all.checked;
			}
		}
	}
	function sync() {
		var hashvo=new ParameterSet();
     	var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'15205110009'},hashvo);
	}
	function showSelect (outparamters) {
		var waitInfo=eval("wait");	   
        waitInfo.style.display="none";
		var message = outparamters.getValue("err_message");
		if(message != null && message.length > 0){
			alert(message);
			return false;
		}else{
			alert("数据同步完成！");
		}
	}
	function updateone(type,selectid) {
		if (type == "1") {//新增
			document.forms[0].action="/kq/options/struts/kqsynchronous.do?b_add=link&type="+type;
			document.forms[0].submit();
		} else if (type == "3"){// 删除
			var opts = "";
			var ckbs = document.getElementsByTagName("input");
			for(var i=0;i<ckbs.length;i++){
				if(ckbs[i].type=="checkbox"&&ckbs[i].name!="synAll" &&ckbs[i].checked==true){
					opts = opts + "," + ckbs[i].value;
				}
			}
			if (opts.length > 0) {
				opts = opts.substr(1);
			} else {
				alert("未选择记录！");
				return false;
			}
			document.forms[0].action="/kq/options/struts/kqsynchronous.do?b_delete=link&type="+type+"&ids="+opts;
			document.forms[0].submit();
		} else if (type == "2") {// 修改
			document.forms[0].action="/kq/options/struts/kqsynchronous.do?b_add=link&type="+type+"&ids="+selectid;
			document.forms[0].submit();
		} else if (type == "4" || type == "5") {// 4启用,5暂停
			var opts = "";
			var ckbs = document.getElementsByTagName("input");
			for(var i=0;i<ckbs.length;i++){
				if(ckbs[i].type=="checkbox"&&ckbs[i].name!="synAll" &&ckbs[i].checked==true){
					opts = opts + "," + ckbs[i].value;
				}
			}
			if (opts.length > 0) {
				opts = opts.substr(1);
			} else {
				alert("未选择记录！");
				return false;
			}
			document.forms[0].action="/kq/options/struts/kqsynchronous.do?b_delete=link&type="+type+"&ids="+opts;
			document.forms[0].submit();
		}
	}
	//暂时用
	function addsyn(){
		window.location.href="/kq/options/struts/kqsynchronous.do?b_add=link";
	}
	
	
	//指定父窗体的高度
/**var doc = document,
	p = window;
while(p = p.parent){
	var frames = p.frames,frame,i = 0;
	while(frame = frames[i++]){
		if(frame.document == doc){
			frame.frameElement.style.height = doc.body.scrollHeight;
			doc = p.document;
			break;
		}
	}
	if(p == top){
	break;
	}
}*/

//屏蔽鼠标右键
/**if (window.Event) 
	document.captureEvents(Event.MOUSEUP); 

function nocontextmenu() { 
	event.cancelBubble = true 
	event.returnValue = false; 
	return false; 
} 
document.oncontextmenu = nocontextmenu; //对ie5.0以上 
*/
</script>