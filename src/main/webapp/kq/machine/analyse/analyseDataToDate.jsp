<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>

<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
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
<script type="text/javascript">
<!--
function sub(){
	var start_date = document.getElementById('start_date').value;
	var end_date = document.getElementById('end_date').value;
	  if(start_date > end_date)
      {
        alert(KQ_CHECK_TIME_HINT);
        return false;
      }
	  if(!isDate(start_date,"yyyy.MM.dd")){
          alert("起始日期格式错误,请输入正确的日期格式！\nyyyy.MM.dd");
          return false;
   	}
   	if(!isDate(end_date,"yyyy.MM.dd")){
  		alert("结束日期格式错误,请输入正确的日期格式！\nyyyy.MM.dd");
      	return false;
   	}
	var obj=new Object();
	obj.start_date=start_date;
	obj.end_date=end_date;
	window.returnValue=obj;
	window.close();
}
//-->
</script>
<html:form action="/kq/machine/analyse/analyse_result">
<div class="fixedDiv2" style="height: 100%;border: none">
	<table width="100%">
		
		<tr>
			<td align="center" width="100%">
				<fieldset>
					<legend>
						请选择时间范围<input type="hidden" name="dateValue" id="dateValue">
					</legend>
					<div>
						<br>
						从&nbsp;
						<input type="text" name="trainAtteForm" size="12" extra="editor" dataType="simpledate"
							value="${dataAnalyseForm.start_date}" 
							 class="inputtext" style="width: 100px; font-size: 10pt; text-align: left"
							id="start_date" onclick='saveCurrDateValue(this);getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' 
							onchange="rep_dateValue(this);restoreDateValue(this,kq_duration)" />
						<br />
						<br />
						到&nbsp;
						<input type="text" name="trainAtteForm" size="12" extra="editor" dataType="simpledate"
							value="${dataAnalyseForm.end_date}" 
							 class="inputtext" style="width: 100px; font-size: 10pt; text-align: left"
							id="end_date" onclick='saveCurrDateValue(this);getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' 
							onchange="rep_dateValue(this);restoreDateValue(this,kq_duration)" />
					</div>
					<br />
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="center" style="padding-top: 5px;">
				<input type="button" class="mybutton" value='确定' onclick="sub();" />
				<input type="button" name="b_cls" value="关闭" class="mybutton"
					onclick="window.close();" />
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
  initDocument();
</script>