<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
<script language="javascript" src="/js/validateDate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
<style type="text/css">
body {
	margin: 0px;
}

.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 50px;
	height: 22px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 12px;
}

.m_arrow {
	width: 14px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 16px;
	height: 13px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}

.input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted;
	BORDER-LEFT: #FFFFFF 0pt dotted;
	BORDER-RIGHT: #FFFFFF 0pt dotted;
	BORDER-TOP: #FFFFFF 0pt dotted;
}
</style>
<script language="javascript">
function setFocusObj(obj,time_vv) 
   {		
	this.fObj = obj;
	time_r=time_vv;		
   }
   function IsInputTimeValue() 
   {	     
       event.cancelBubble = true;
       var fObj=this.fObj;		
       if (!fObj) return;		
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.lenght<=0)
	  fObj.value="0";
       var i = parseInt(fObj.value,10);		
       var radix=parseInt(time_r,10)-1;				
       if (i==radix&&cmd) {
           i = 0;
       } else if (i==0&&!cmd) {
	   i = radix;
       } else {
	   cmd?i++:i--;
       }	
       if(i==0)
       {
	  fObj.value = "00"
       }else if(i<10&&i>0)
       {
	  fObj.value="0"+i;
       }else{
	  fObj.value = i;
       }			
       fObj.select();
    } 
    function sync()
    {
        var thevo=new Object();
	    thevo.start_date=$F('start_date');
	    thevo.end_date=$F('end_date');
        thevo.start_hh=$F('start_hh');
        thevo.start_mm=$F('start_mm');
        thevo.end_hh=$F('end_hh');
        thevo.end_mm=$F('end_mm');
        if(thevo.start_date.length!=10||thevo.start_hh.length!=2||thevo.start_mm.length!=2)
        {
           alert("开始时间有误请检查！时间格式为yyyy.MM.dd HH:mm");
           return false;
        }
        if(thevo.end_date.length!=10||thevo.end_hh.length!=2||thevo.end_mm.length!=2)
        {
           alert("结束时间有误请检查！时间格式为yyyy.MM.dd HH:mm");
           return false;
        }
       if(confirm("确定要同步吗？"))
       {
          window.returnValue=thevo;
	      window.close();  
       }
       
    }
</script>
<html:form action="/kq/machine/search_card">
	<div class="fixedDiv3" style="padding-left: 10px;padding-top: 5px;">
	<table width="100%" border="0" cellpadding="1" cellspacing="0"
		align="right">
		<tr height="20">
			<td colspan="0" align="center" class="TableRow">
				设置时间范围
			</td>
		</tr>
		<tr>
			<td class="framestyle9">
				<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
					<tr>
						<td height="10">
						</td>
						<td>
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							开始时间&nbsp;
						</td>
						<td align="right" class="tdFontcolor" nowrap>
							<table border="0" cellspacing="0" align="left" cellpadding="0">
								<tr>
									<td>
										<input type="text" class="inputtext" name="start_date"
											value="${kqCardDataForm.start_date}" extra="editor"
											style="width: 100px; font-size: 10pt; text-align: left"
											id="editor1" dropDown="dropDownDate">
										&nbsp;&nbsp;
									</td>
									<td width="40" nowrap style="background-color: #FFFFFF";>
										<div class="m_frameborder inputtext">
											<input type="text" class="m_input" maxlength="2"
												name="start_hh" value="${kqCardDataForm.start_hh}"
												onfocus="setFocusObj(this,24);">
											<font color="#000000"><strong>:</strong>
											</font>
											<input type="text" class="m_input" maxlength="2"
												name="start_mm" value="${kqCardDataForm.start_mm}"
												onfocus="setFocusObj(this,60);">
										</div>
									</td>
									<td>
										<table border="0" cellspacing="2" cellpadding="0">
											<tr>
												<td>
													<button id="0_up" class="m_arrow"
														onmouseup="IsInputTimeValue();">
														5
													</button>
												</td>
											</tr>
											<tr>
												<td>
													<button id="0_down" class="m_arrow" style="margin-bottom: 10px;"
														onmouseup="IsInputTimeValue();">
														6
													</button>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="right" class="tdFontcolor" nowrap>
							结束时间&nbsp;
						</td>
						<td align="right" class="tdFontcolor" nowrap>
							<table border="0" cellspacing="0" align="left" cellpadding="0">
								<tr>
									<td>
										<input type="text" class="inputtext" name="end_date"
											value="${kqCardDataForm.end_date}" extra="editor"
											style="width: 100px; font-size: 10pt; text-align: left"
											id="editor2" dropDown="dropDownDate">
										&nbsp;&nbsp;
									</td>
									<td width="40" nowrap style="background-color: #FFFFFF">
										<div class="m_frameborder inputtext">
											<input type="text" class="m_input" maxlength="2"
												name="end_hh" value="${kqCardDataForm.end_hh}"
												onfocus="setFocusObj(this,24);">
											<font color="#000000"><strong>:</strong>
											</font>
											<input type="text" class="m_input" maxlength="2"
												name="end_mm" value="${kqCardDataForm.end_mm}"
												onfocus="setFocusObj(this,60);">
										</div>
									</td>
									<td>
										<table border="0" cellspacing="2" cellpadding="0">
											<tr>
												<td>
													<button id="0_up" class="m_arrow"
														onmouseup="IsInputTimeValue();">
														5
													</button>
												</td>
											</tr>
											<tr>
												<td>
													<button id="0_down" class="m_arrow" style="margin-bottom: 10px;"
														onmouseup="IsInputTimeValue();">
														6
													</button>
												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td height="10"></td>
			<td></td>
		</tr>
		<tr>
			<td>
				<fieldset align="center">
					<legend>
						<bean:message key="kq.kq_rest.shuoming" />
					</legend>
					<br>
					<table width="100%" border="0" cellpmoding="5" cellspacing="5"
						class="DetailTable" cellpadding="5">
						<tr>
							<td width="100%" height="30">
								如果时间范围设置过大，速度会慢，可以分多次设置时间范围进行操作！
							</td>
						</tr>

					</table>
					<br>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="center" style="padding-top: 5px">
				<input type="button" class="mybutton" name="dd"
					value='<bean:message key="button.ok"/>' onclick="sync();">
				
				<input type="button" class="mybutton" name="dd"
					value='<bean:message key="button.close"/>'
					onclick="window.close();">
			</td>
		</tr>
	</table>
	</div>
</html:form>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
  
</script>
