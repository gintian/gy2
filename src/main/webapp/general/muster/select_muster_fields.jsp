<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
		bosflag = bosflag != null ? bosflag : "";
	}
%>
<script language="javascript" src="/js/common.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript">
	var validateValue=true;

	function showResponse(outparamters)
	{
		var sss=outparamters.getValue("vo").values.a0101
		AjaxBind.bind($("aaa1"),sss);
	}
	
	function test()
	{
	/*
	   var ele=$('aaa1');
           alert(ele);	
		var eles=Form.getElements('musterForm');
		for(var i=0;i<eles.length;i++)
		{
		  alert(eles[i].name);
		}
		eles=Form.getInputs('musterForm','text');
		for(i=0;i<eles.length;i++)
		{
		  alert(eles[i].name);
		}	
		*/	
		var pars="yyy=1987&kkk=要要要要";
		
		var forms=new Array();
		forms.push("musterForm");
		var request=new Request({method:"post",asynchronous:false,parameters:pars,onSuccess:showResponse,functionId:"0520000001"},forms);
	}
	function testelement()
	{
		//Element.toggle('ss0');
		Element.remove("ss0");
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		AjaxBind.bind(musterForm.setlist,/*$('setlist')*/setlist);
		AjaxBind.bind(musterForm.sortsetlist,/*$('setlist')*/setlist);
		if($("setlist").options.length>0)
		{
		  $("setlist").options[0].selected=true;
		  myFireEvent($("setlist"));
		  $("sortsetlist").options[0].selected=true;
		  myFireEvent($("sortsetlist"));
		}
	}
	/* 兼容fireEvent方法 */
	function myFireEvent(el) { 
		var evt; 
		if (document.createEvent) {
			evt = document.createEvent("MouseEvents"); 
			evt.initMouseEvent("change", true, true, window, 
			0, 0, 0, 0, 0, false, false, false, false, 0, null); 
			el.dispatchEvent(evt); 
		} else if (el.fireEvent) { // IE 
			el.fireEvent("onchange"); 
		} 
	}
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(musterForm.left_fields,fieldlist);
	}

	function showSortFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(musterForm.sort_left_fields,fieldlist);
	}
				
	/**查询指标*/
	function searchFieldList()
	{
	
	   var tablename=$F("setlist");
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:"post",asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:"0520000002"});
	}
	function searchSortFieldList()
	{
	   var tablename=$F("sortsetlist");
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:"post",asynchronous:false,parameters:in_paramters,onSuccess:showSortFieldList,functionId:"0520000002"});
	}
		
	function firstToSecondStep()
	{
	
		var rightFields=$("right_fields")
		if(rightFields.options.length==0)
		{
			alert(GENERAL_SELECT_ITEMNAME);
			return;
		}
		
		for(var i=0;i<rightFields.options.length;i++)
		{
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			if(n>1)
			{
				alert(a_text+ITEM_NOT_RESET+"！");
				return;
			}
		}
	  Element.hide("first");
	  Element.show("second");
	}
	
	function secondToFirstStep()
	{
	  Element.show("first");
	  Element.hide("second");
	}
	
	function secondToThirdStep()
	{
		<logic:equal name="musterForm" property="infor_Flag" value="1">
		var rightFields=$("right_fields")
		var n=0;
		for(var i=0;i<rightFields.options.length;i++){
			var a_value=rightFields.options[i].value;
			if(a_value!=null&&a_value.length>0){
				if(a_value.substring(1,3)=="01"){
					continue;
				}else{
					toggles("prhistoryview");
					n=1;
					break;
				}
			}
	  }
	  if(n==0){
	  	 hides("prhistoryview");
	  	 hides("viewremainset");
		 document.musterForm.history.value="0";
	  }
	  </logic:equal>
	  Element.show("third");
	  Element.hide("second");		
	}

	function thirdToSecondStep()
	{
	  Element.show("second");
	  Element.hide("third");		
	}
	
	/**填充花名册指标和排序指标*/
	function filloutData(){
	    setselectitem("right_fields");
	   // setselectitem("sort_right_fields");
	   var sortitem = document.getElementById("sortitem").value;		
	    if(sortitem==null||sortitem.length<1){
	   		validateValue=true;
	   	}else{
	   		validateValue=true;
	   		<logic:equal name="musterForm" property="refleshtree" value="1">
	   			self.parent.nil_menu.location ="/general/muster/hmuster/rostertree.jsp";
	   		</logic:equal>   
	   	}
	   	<logic:equal name="musterForm" property="infor_Flag" value="1">
          if(document.getElementById("prhistory").checked){
          		if(document.getElementById("prmainset").checked){
          			document.musterForm.repeat_mainset.value="1";
          		}else{
          			document.musterForm.repeat_mainset.value="0";
          		}
          }else{
          		document.musterForm.repeat_mainset.value="0";
          }
        </logic:equal> 
        var obj = document.getElementById("mt");
        var num=0;
        for(var i=0;i<obj.options.length;i++)
        {
           if(obj.options[i].selected)
              num++;
        }  
        if(num==0)
        {
           alert(MUSTER_INFO1);
           return;//false
        }
        var name=document.getElementsByName("mustername")[0].value;
        if(trim(name)=='')
        {
           alert("花名册名称不能为空！");
           return;
        }
        var x=(document.body.scrollWidth-500)/2;
		var y=(document.body.scrollHeight-285)/2; 
		var waitInfo=eval("wait");
		waitInfo.style.top=y;
		waitInfo.style.left=x;	   
		waitInfo.style.display="block";
        document.getElementById('finished').disabled=true;
        musterForm.action="/general/muster/select_muster_fields.do?b_finished=link&closeWindow=0&isClose=0&returnflag=${musterForm.returnflag}";
        musterForm.target="_self";
        musterForm.submit();
       // return true;
	}
	
	function validateSort(){
	
		for(var i=0;i<document.musterForm.sort_right_fields.options.length;i++)
		{
			var temps=document.musterForm.sort_right_fields.options[i].value;		
			var flag=false;
			for(var j=0;j<document.musterForm.right_fields.options.length;j++)
			{
				var temps2=document.musterForm.right_fields.options[j].value;
				if(temps2==temps)
				{
					flag=true;
					break;
				}
			}
			if(!flag)
			{
				alert(SORT_MUSTER_ITEM+"！");
				return false
			}
		}
		return true;
	}
	
	
	/**初化数据*/
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
	   var hashvo=new ParameterSet();
	   var testvo=new Object();
	   testvo.name="chen";
	   testvo.age=12;
	   testvo.birth=new Date();
	   var arr=new Array();
	   arr[0]=12;
	   arr[1]="ssssdfa";
	   hashvo.setValue("aaa","hello world");
	   hashvo.setValue("testvo",testvo);
	   hashvo.setValue("arr",arr);
   	   var request=new Request({method:"post",asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:"0520000001"},hashvo);
	}
	function historySet(obj){
		if(obj.checked){
			toggles("viewremainset");
			document.musterForm.history.value="1";
		}else{
			hides("viewremainset");
			document.musterForm.history.value="0";
		}
	}
	function checkChar(){ 
		var k = window.event.keyCode;
		if ( k==39||k==47||k==34){
			return false;
		}
	}   
	function returnFirst(){
   		document.location="/general/tipwizard/tipwizard.do?br_employee=link";
	}
</script>
<script language="JavaScript" src="sorting.js"></script>
<style>
.fixedtab {
	border: 1px solid;
}
</style>
<hrms:themes />
<style>
.TableRow_lr{
	/*BORDER-BOTTOM: 1pt solid;*/
}
.fixedtab {
	height: 240px;
	width: 100%;
	overflow: auto;
}
<%
	if ("hl".equalsIgnoreCase(bosflag)) {
%>
.RecordRow {
	margin-top: 10px;
}
.ListTable {
	margin-top: 0px;
}
#dis_sort_table {
	border: 1px solid;
	height: 240px;
	width: 100%;
	overflow: auto;
	margin: 0 1;
}
<%
	}
%>
.ListTable1{
	border:0;
}
</style>
<html:form action="/general/muster/select_muster_fields"
	onsubmit="javascript:return validateValue">
	<html:hidden property="infor_Flag" />
	<!--花名册指标-->
	<div id="first" style="filter: alpha(Opacity =                 100);">
		<table width="700px" border="0" cellspacing="0" align="center"
			cellpadding="0" class="RecordRow">
			<thead>
				<tr>
					<td align="left" class="TableRow" nowrap colspan="3">
						<bean:message key="label.query.selectfield" />
					</td>
				</tr>
			</thead>
			<tr>
				<td align="center" nowrap>
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" style="border-top: 0;">
						<tr>
							<td align="center" width="46%">
								<table align="center" width="100%" border="0" cellspacing="0"
									cellpadding="0">
									<tr>
										<td align="left">
											<bean:message key="selfservice.query.queryfield" />
										</td>
									</tr>
									<tr>
										<td align="left">
											<select name="setlist" size="1" style="width: 100%"
												onchange="searchFieldList();">
												<option value="1111">
													#
												</option>
											</select>
										</td>
									</tr>
									<tr>
										<td height="6px"></td>
									</tr>
									<tr>
										<td align="left">
											<select name="left_fields" multiple="multiple"
												ondblclick="additem('left_fields','right_fields');removeitem('left_fields');"
												style="height: 216px; width: 100%; font-size: 9pt">
											</select>
										</td>
									</tr>
								</table>
						</td>

						<td width="48px" align="center">
							<table border="0" cellspacing="0" align="center" cellpadding="0">
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_addfield"
											onclick="additem('left_fields','right_fields');removeitem('left_fields')">
											<bean:message key="button.setfield.addfield" />
										</html:button>
									</td>
								</tr>
								<tr>
									<td height="30px"></td>
								</tr>
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_delfield"
											onclick="additem('right_fields','left_fields');removeitem('right_fields');">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
								</tr>
							</table>
						</td>

						<td width="46%" align="left">
							<table align="center" width="100%" border="0" cellspacing="0"
								cellpadding="0" style="margin-top: 5px;">
								<tr>
									<td width="100%" align="left">
										<bean:message key="selfservice.query.queryfieldselected" />
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">
										<html:select name="musterForm" property="right_fields"
											multiple="multiple" size="10"
											ondblclick="additem('right_fields','left_fields');removeitem('right_fields');"
											style="height:250px;width:100%;font-size:9pt">
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="48px" align="center">
							<table border="0" cellspacing="0" align="center" cellpadding="0">
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_up"
											onclick="upItem($('right_fields'));">
											<bean:message key="button.previous" />
										</html:button>
									</td>
								</tr>
								<tr>
									<td height="30px"></td>
								</tr>
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_down"
											onclick="downItem($('right_fields'));">
											<bean:message key="button.next" />
										</html:button>
									</td>
								</tr>
							</table>
						</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" class="RecordRow" nowrap colspan="3"
					style="height: 35px;">
					<html:button styleClass="mybutton" property="b_next"
						onclick="firstToSecondStep();">
						<bean:message key="button.query.next" />
					</html:button>
				</td>
			</tr>
		</table>
	</div>
	<!--花名册排序指标选择-->
	<div id="second" style="filter: alpha(Opacity = 100); display: none;">
		<table width="700px" border="0" cellspacing="0" align="center"
			cellpadding="0" class="RecordRow">
			<thead>
				<tr>
					<td align="left" class="TableRow" nowrap colspan="3">
						<bean:message key="label.query.sortfield" />
					</td>
				</tr>
			</thead>
			<tr>
				<td align="center" nowrap>
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" style="border-top: 0;">
						<tr>
							<td align="center" width="46%">
								<table align="center" width="100%" border="0" cellspacing="0"
									cellpadding="0" style="margin-top: 0px;">
									<tr>
										<td align="left">
											<bean:message key="selfservice.query.queryfield" />
											&nbsp;&nbsp;
										</td>
									</tr>
									<tr>
										<td align="left">
											<select name="sortsetlist" size="1" style="width:100%"
												onchange="searchSortFieldList();">
												<option value="1111">
													#
												</option>
											</select>
										</td>
									</tr>
									<tr>
										<td height="4px"></td>
									</tr>
									<tr>
										<td align="left">
											<select name="sort_left_fields" multiple="multiple"
												ondblclick="addfield();removeitem('sort_left_fields');"
												style="height: 216px; width:100%; font-size: 9pt">
											</select>
										</td>
									</tr>
								</table>
							</td>
							<td width="48px" align="center">
								<table border="0" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="center">
											<html:button styleClass="mybutton" property="b_addfield"
												onclick="addfield();removeitem('sort_left_fields');">
												<bean:message key="button.setfield.addfield" />
											</html:button>
										</td>
									</tr>
									<tr>
										<td height="30px"></td>
									</tr>
									<tr>
										<td align="center">
											<html:button styleClass="mybutton" property="b_delfield"
												onclick="deletefield();">
												<bean:message key="button.setfield.delfield" />
											</html:button>
										</td>
									</tr>
								</table>
							</td>
							<td width="46%" align="center">
								<table align="center" width="100%" border="0" cellspacing="0"
									cellpadding="0" style="margin-top: 5px;margin-bottom:5px;">
									<tr>
										<td width="100%" align="left">
											<bean:message key="label.query.selectedsortfield" />
										</td>
									</tr>
									<tr>
										<td width="100%" align="left">
											<div id="dis_sort_table" class="fixedtab" width="100%">
												<table width="100%" border="0" cellspacing="0"
									cellpadding="0" class="ListTable">
													<tr>
														<td class="TableRow" width="10%" align="left" style="border-left: none;border-top: none;">
															&nbsp;
														</td>
														<td class="TableRow" width="65%" align="center" style="border-top: none;">
															<bean:message key="field.label" />
														</td>
														<td class="TableRow" width="25%" align="center" style="border-right: none;border-top: none;">
															<bean:message key="label.query.baseDesc" />
														<td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
								</table>
						</td>
						<td width="48px" align="center">
							<table border="0" cellspacing="0" align="center" cellpadding="0">
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_up"
											onclick="upSort();">
											<bean:message key="button.previous" />
										</html:button>
									</td>
								</tr>
								<tr>
									<td height="30px"></td>
								</tr>
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_down"
											onclick="downSort();">
											<bean:message key="button.next" />
										</html:button>
									</td>
								</tr>
							</table>
						</td>
						</tr>
					</table>
			</td>
			</tr>
			<tr>
				<td align="center" class="RecordRow" nowrap colspan="3"
					style="height: 35px;">
					<html:button styleClass="mybutton" property="b_s_pre"
						onclick="secondToFirstStep();">
						<bean:message key="button.query.pre" />
					</html:button>
					<html:button styleClass="mybutton" property="b_s_next"
						onclick="secondToThirdStep();">
						<bean:message key="button.query.next" />
					</html:button>
				</td>
			</tr>
		</table>
	</div>
	<!--花名册保存-->
	<div id="third"
		style="filter: alpha(Opacity = 100); display: none;">
		<table width="700px" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
				<tr>
					<td align="left" class="TableRow" nowrap colspan="2">
						<bean:message key="label.muster.savemuster" />
						&nbsp;&nbsp;
					</td>
				</tr>
			</thead>
			<logic:equal name="musterForm" property="infor_Flag" value="1">
				<tr>
					<td align="right" class="RecordRow" nowrap><bean:message key="menu.base" /></td>
					<td align="left" class="RecordRow" nowrap>
						<html:select name="musterForm" property="dbpre" size="1">
							<html:optionsCollection property="dblist" value="dataValue"
								label="dataName" />
						</html:select>

					</td>
				</tr>
			</logic:equal>
			<!--须初始化-->
			<tr>
				<td align="right" class="RecordRow" nowrap><bean:message key="muster.label.type" /></td>
				<td align="left" class="RecordRow" nowrap>
					<html:select name="musterForm" property="mustertype" styleId="mt"
						size="1">
						<html:optionsCollection property="typelist" value="dataValue"
							label="dataName" />
					</html:select>
					&nbsp;&nbsp;
				</td>
			</tr>
			<tr>
				<td align="right" class="RecordRow" nowrap><bean:message key="muster.label.name" /></td>
				<td align="left" class="RecordRow" nowrap>
					<html:text name="musterForm" property="mustername"
						onkeypress="event.returnValue=checkChar();" size="50"
						maxlength="100" styleClass="text4" />
					<input type='hidden' name="used_flag" value="0">
				</td>
			</tr>
			<logic:equal name="musterForm" property="infor_Flag" value="1">
				<tr>
					<td class="RecordRow" nowrap align="right">
						<html:hidden name="musterForm" property="history" />
						<div id="prhistoryview" style="display: none">
							<input type="checkbox" name="prhistory" id="prhistory"
								onclick="historySet(this);" value="0">
							<bean:message key="muster.label.history" />
						</div>
					</td>
					<td class="RecordRow" nowrap align="left">
						<html:hidden name="musterForm" property="repeat_mainset" />
						<div id="viewremainset" style="display: none">
							<input type="checkbox" name="prmainset" id="prmainset" value="0">
							<bean:message key="workdiary.message.includes.mainfor" />
						</div>
					</td>
				</tr>
			</logic:equal>
			<tr>
				<td align="center" class="RecordRow" nowrap colspan="2"
					style="height: 35px;">
					<html:button styleClass="mybutton" property="b_t_pre"
						onclick="thirdToSecondStep();">
						<bean:message key="button.query.pre" />
					</html:button>
					<!--  
              <html:submit styleClass="mybutton" property="b_finished" onclick="if(filloutData()){validate('R','mustername',ROSTER_NAME);if(document.returnValue){document.getElementById('b_finished').disabled=true;}return document.returnValue;}else{return false;}">
                    <bean:message key="button.muster.finished"/>
	      	  </html:submit> -->
					<input type="button" id="finished" class="mybutton"
						value="<bean:message key="button.muster.finished"/>"
						onclick="filloutData();" />
				</td>
			</tr>
		</table>
	</div>
	<input type="hidden" name="sortitemid" id="sortitemid">
	<html:hidden name="musterForm" property="sortitem" styleId="sortitem" />
	<input type="hidden" name="flag" value="1">
</html:form>
<script language="javascript">

	/**取IE版本，如果返回0为其他浏览器*/
	var addBtns = document.getElementsByName('b_addfield');
	var delBtns = document.getElementsByName('b_delfield');
	var upBtns = document.getElementsByName('b_up');
	var downBtns = document.getElementsByName('b_down');
	//IE11 非兼容模式
	if(getBrowseVersion() == 10){
		for(var i = 0; i < addBtns.length; i++){
			addBtns[i].style.marginLeft = '5px';
		}
		for(var i = 0; i < delBtns.length; i++){
			delBtns[i].style.marginLeft = '5px';
		}
		for(var i = 0; i < upBtns.length; i++){
			upBtns[i].style.marginLeft = '5px';
		}
		for(var i = 0; i < downBtns.length; i++){
			downBtns[i].style.marginLeft = '5px';
		}
	}
	
   //var ViewProperties=new ParameterSet();
   MusterInitData('<bean:write name="musterForm"  property="infor_Flag"/>');
</script>
<div id="wait" style='position: absolute; top: 285; left: 120; display: none; width: 500px; heigth: 250px'>
				<table border="1" width="50%" cellspacing="0" cellpadding="4"
					class="table_style" height="100" align="center">
					<tr>
						<td class="td_style" height=24>
							<bean:message key="hmuster.label.wait" />
						</td>
					</tr>
					<tr>
						<td style="font-size: 12px; line-height: 200%" align=center>
							<marquee class="marquee_style" direction="right" width="400"
								scrollamount="5" scrolldelay="10">
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
				<iframe src="javascript:false"
					style="position: absolute; visibility: inherit; top: 0px; left: 0px; width: 285px; height: 120px; z-index: -1; filter ='progid: DXImageTransform . Microsoft . Alpha(style = 0, opacity = 0) ';">
				</iframe>
			</div>
<script>
	if(getBrowseVersion() == 10 || !getBrowseVersion()){ //非ie兼容视图    样式修改  wangb 20190308
		var firstDiv = document.getElementById('first');
		var firsttd = firstDiv.children[0].children[1].children[0].children[0];
		firsttd.style.borderRight='#C4D8EE 1pt solid';
		
		var firstTable = firstDiv.children[0].children[1].children[0].children[0].children[0].children[0].children[0].children[1].children[0];
		firstTable.style.marginLeft='6px';
		var right_fields = document.getElementsByName('right_fields')[0];
		right_fields.style.width='98%';
		right_fields.style.marginBottom='4px';
		
		var secondDiv = document.getElementById('second');
		var secondtd = secondDiv.children[0].children[1].children[0].children[0];
		secondtd.style.borderRight='#C4D8EE 1pt solid';
		var secondTable1 = secondDiv.children[0].children[1].children[0].children[0].children[0].children[0].children[0].children[1].children[0];
		secondTable1.style.marginLeft='6px';
		var secondTable2 = secondDiv.children[0].children[1].children[0].children[0].children[0].children[0].children[0].children[3].children[0];
		secondTable2.style.marginLeft='8px';
	}
</script>			