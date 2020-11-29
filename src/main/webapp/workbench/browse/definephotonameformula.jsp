<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>
<%
	String formula = request.getParameter("formula");
	formula = SafeCode.decode(formula);
%>
<head>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../js/constant.js"></script>
	<script language="javascript">
function symbol(editor,strexpr){
	var expr_editor=document.getElementById(editor);
	expr_editor.focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}else{
		var word = expr_editor.value;
				var _length=strexpr.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strexpr+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length);
	}
}

function changeCodeValue(){
	//update by xiegh date20180308 bug35302
  	//var item=$F('itemid');
	var item = document.getElementById("itemid").value;
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
    symbol('sformula',item);
}

function function_Wizard(busi,formula){
   //var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&busi="+busi; 
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link`busi="+busi; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var dw=400,dh=430;
    if(getBrowseVersion()){
    	var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    	if(return_vo!=null)
			symbol(formula,return_vo);
    }else{
    	//非IE浏览器使用open弹窗    bug 35013  20180227 wangb
    	var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
    }
}
/*非IE浏览器弹窗调用方法   bug 35013  20180227 wangb*/
function openReturn(return_vo){
	if(return_vo!=null)
		symbol('sformula',return_vo);//对比ie浏览器的代码  symbol的第一个参数是写死的 sformula 之前没有传参 之前报错  xiegh bug35469
}
function getformula(){
	//var sformula=$F('sformula');
	var sformula = document.getElementById("sformula").value;
	if(!sformula) {
		alert(SET_FORMULA_CONTENT);
		return;
	}
		
	var hashvo=new HashMap();
	hashvo.put("sformula",getEncodeStr(sformula));  
	hashvo.put('stat','photo');            
   	//var request=new Request({onSuccess:afterCheck,functionId:'11080204098'},hashvo);  
   	Rpc({functionId:'11080204098',async:false,success:afterCheck},hashvo);//update by xiegh date20180308 bug35302
	function afterCheck(outparamters){
			var result = Ext.decode(outparamters.responseText);
			var flag=result.flag; 
			if("ok"==flag){
				if(getBrowseVersion()){//IE浏览器 弹窗
					parent.window.returnValue=sformula;
				}else{//非IE浏览器  回调父页面方法返回数据   wangb 20180227
					parent.opener.openReturn(sformula);
				}
				parent.window.close();
			}else{
				alert(getDecodeStr(flag));
			}
		} 	 
}
</script>
<hrms:themes />
	<style>
.div_table {
	border-width: 1px;
	BORDER-BOTTOM: #aeac9f 1pt solid;
	BORDER-LEFT: #aeac9f 1pt solid;
	BORDER-RIGHT: #aeac9f 1pt solid;
	BORDER-TOP: #aeac9f 1pt solid;
}

.tdFontcolor {
	text-decoration: none;
	Font-family: ????;
	font-size: 12px;
	height: 20px;
	align:center;
}

.btn1 {
	PADDING-RIGHT: 0px;
	PADDING-LEFT: 0px;
	FONT-SIZE: 12px;
	CURSOR: hand;
	COLOR: black;
	PADDING-TOP: 1px;
	PADDING-BOTTOM: 0px;
	background-image: none;
	WIDTH: 20px;
}

.btn2 {
	PADDING-RIGHT: 3px;
	PADDING-LEFT: 3px;
	FONT-SIZE: 12px;
	CURSOR: hand;
	COLOR: black;
	PADDING-TOP: 1px;
	PADDING-BOTTOM: 0px;
	background-image: none;
	WIDTH: 20px;
}

.btn3 {
	PADDING-RIGHT: 2px;
	PADDING-LEFT: 2px;
	FONT-SIZE: 12px;
	CURSOR: hand;
	COLOR: black;
	PADDING-TOP: 1px;
	PADDING-BOTTOM: 0px;
	background-image: none;
	WIDTH: 20px;
}

#scroll_box {
	border: 1px solid #eee;
	height: 280px;
	width: 270px;
	overflow: auto;
	margin: 1em 1;
}

.ListTablex {
	border: 1px solid #8EC2E6;
	border-collapse: collapse;
	BORDER-BOTTOM: medium none;
	BORDER-LEFT: medium none;
	BORDER-TOP: #94B6E6 1pt solid;
	margin-top: 5px;
}

fieldset{
	padding: 0;
}
</style>
	<title><bean:message key="workbench.browse.photoexport" />
	</title>
</head>
<html:form action="/workbench/browse/view_photo">
	<table width="500px" border="0" cellspacing="0" align="center"
		cellpadding="0" style="margin-left: 4px;">
		<tr>
			<td valign="top" width="500px">
				<fieldset align="center" style="width: auto;">
					<legend>
						<bean:message key="hmuster.label.expressions" />
					</legend>
					<table width="100%" border="0" height="100%" cellspacing="0" align="center"
		cellpadding="0" style="margin-bottom:5px;">
						<tr>
							<td colspan="2" align="left" style="padding-left: 5px;">
								<%-- bug 37074 非IE浏览器文本域禁止拉伸 添加resize 样式   wangb 20180502--%>
								<html:textarea name="browseForm" property="definephotoname"
									cols="56" rows="9" styleId="sformula" value="<%=formula%>"
									style="font-size:13px;height:90px!important;width:420px!important;width:414px;resize:none;"></html:textarea>
							</td>
						</tr>
						<tr>
							<td width="205px" align="left" style="padding-left: 5px;">
								<fieldset align="left" style="width: 200px; height: 80px;padding-left: 0px;">
									<legend>
										<bean:message key="gz.formula.operational.symbol" />
									</legend>
									<table width="190px" border="0" align="center">
										<tr>
											<td>
												<input type="button" value="0"
													onclick="symbol('sformula','0');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="1"
													onclick="symbol('sformula','1');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="2"
													onclick="symbol('sformula','2');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="3"
													onclick="symbol('sformula','3');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="4"
													onclick="symbol('sformula','4');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="+"
													onclick="symbol('sformula','+');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="-"
													onclick="symbol('sformula','-');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="("
													onclick="symbol('sformula','(');" class="smallbutton btn2">
											</td>
										</tr>
										<tr>
											<td>
												<input type="button" value="5"
													onclick="symbol('sformula','5');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="6"
													onclick="symbol('sformula','6');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="7"
													onclick="symbol('sformula','7');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="8"
													onclick="symbol('sformula','8');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="9"
													onclick="symbol('sformula','9');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="*"
													onclick="symbol('sformula','*');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value="/"
													onclick="symbol('sformula','/');" class="smallbutton btn2">
											</td>
											<td>
												<input type="button" value=")"
													onclick="symbol('sformula',')');" class="smallbutton btn2">
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
							<td id="tdId">
								<fieldset align="left" id="fsId" style="width: 210px; height: 80px;">
									<legend>
										<bean:message key='org.maip.reference.projects' />
									</legend>
									<table width="180" border="0">
										<tr>
											<td height="40">
												<bean:message key="gz.formula.project" />
												<hrms:optioncollection name="browseForm"
													property="stringfieldlist" collection="list" />
												<html:select name="browseForm" styleId="itemid"
													property="definephotoname" onchange="changeCodeValue();"
													style="width:140">
													<option></option>
													<html:options collection="list" property="dataValue"
														labelProperty="dataName" />
												</html:select>
											</td>
										</tr>
									</table>
								</fieldset>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
			<td width="5px"></td>
			<td valign="top" style="padding-left: 5px;padding-top: 7px;">
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td>
							<html:button styleClass="mybutton" property="br_return"
								onclick="function_Wizard('A01','sformula');">
								<bean:message key="button.wizard" />
							</html:button>
						</td>
					</tr>
					<tr><td height="20px"></td></tr>
					<tr>
						<td>
							<html:button styleClass="mybutton" property="br_return"
								onclick="getformula();">
								<bean:message key="lable.tz_template.enter" />
							</html:button>
						</td>
					</tr>
					<tr><td height="20px"></td></tr>
					<tr>
						<td>
							<html:button styleClass="mybutton" property="br_return"
								onclick="parent.window.close();">
								<bean:message key="lable.tz_template.cancel" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion()==10){//非IE浏览器 样式修改    wangb  20180227
	var form = document.getElementsByName('browseForm')[0];
	form.style.width ='99%';
}

if(isCompatibleIE()){
	document.getElementById('fsId').style.width = "193px";
	document.getElementById('tdId').style.paddingLeft ="15px";
}
</script>