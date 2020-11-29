<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language='JavaScript' src='/components/codeSelector/codeSelector.js'></script>
<script language="javascript">
var whereStr = "";
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(response) {
		var map	 = Ext.decode(response.responseText);
		var setlist=map.setlist;
		var selectList = map.itemlist;
		if(setlist) {
			var setObj = document.getElementsByName("setlist")[0];
			var fieldObj = document.getElementsByName("left_fields")[0];
			for(var i = 0; i < setlist.length; i++)
				setObj.options[i] = new Option(setlist[i].dataName, setlist[i].dataValue);

			for(var i = 0; i < selectList.length; i++)
				fieldObj.options[i] = new Option(selectList[i].dataName, selectList[i].dataValue);
		}
	}
	/**显示指标*/
	function showFieldList(response) {
		var map	 = Ext.decode(response.responseText);
		var fieldlist = map.itemlist;
		var fieldObj = document.getElementsByName("left_fields")[0];
		fieldObj.options.length=0;
		for(var i = 0; i < fieldlist.length; i++)
			fieldObj.options[i] = new Option(fieldlist[i].dataName, fieldlist[i].dataValue);
		
	}


				
	/**查询指标*/
	function searchFieldList() {
	    var map = new HashMap();
	    map.put("infokind",'<%=request.getParameter("infokind")%>');
	   	var tablename= document.getElementsByName("setlist")[0].value;
	    map.put("fieldsetid",tablename);
	    Rpc({functionId:'0202001020',async:false,success:showFieldList},map);
	}
	
		
	function sub() {
		var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=document.getElementsByName("right_fields")[0];
		if(rightFields.options.length==0) {
			alert(UNDEFINED_EXPORT_ITEM);
	    	return;
		}
		
		for(var i=0;i<rightFields.options.length;i++) {
			rightFiledIDs+="`"+rightFields.options[i].value;
			rightFieldNames+=","+rightFields.options[i].text;
			
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++) {
				if(rightFields.options[j].value==a_value) {
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			
			if(n>1) {
				alert(a_text+FIELD_NOT_REPEAT);
				return;
			}
		}

		var historyRadios = document.getElementsByName("history");
		var historyValue = 1;
		var selectDate = "";
		var times = 0;
		var selectField = "";
		var where = "";
		for(var i = 0; i < historyRadios.length; i++){
			var radio = historyRadios[i];
			if(radio.checked) {
				historyValue = radio.value;
				break;
			}
		}
		
		if(historyValue == 2) {
			var year = document.getElementById("selectedYear").value;
			var month = document.getElementById("selectedMonth").value;
			selectDate = year + "-" + month;
			times = document.getElementById("times").value;
		} else if(historyValue == 3) {
			selectField = document.getElementById("selectedfield").value;
			var fields = selectField.split("/");
			selectField = fields[0];
			if(fields.length < 2) {
				where = whereStr;
			} else {
				var from = "", to = "";
				if(fields[2] == "D") {
					from = Ext.getCmp("fromDate").value;
					to = Ext.getCmp("toDate").value;
				} else if(fields[3] == "0") {
					from = document.getElementById("from").value;
					to = document.getElementById("to").value;
				} else {
					from = document.getElementsByName("from.value")[0].value;
					to = document.getElementsByName("to.value")[0].value;
				}

				if(!from)
					from = "#";
				else if(fields[2] == "D")
					from = Ext.util.Format.date(from,'Y-m-d');
				
				if(!to)
					to = "#";
				else if(fields[2] == "D")
					to = Ext.util.Format.date(to,'Y-m-d');
				
				where = from + ":" + to;
			}
		}
		/*员工查询导出excel时指标选择界面  */
			var infos=new Array();
			infos[0]=rightFiledIDs.substring(1);
			infos[1]=rightFieldNames.substring(1);
			infos[2]=historyValue;
			infos[3]='<%=request.getParameter("infokind")%>';
			infos[4]='<%=request.getParameter("dbpre")%>';
			infos[5]='<%=request.getParameter("isQuery")%>';
			infos[6]=selectDate;
			infos[7]=times + "";
			infos[8]=selectField;
			infos[9]=where;
			
		var queryback = '<%=request.getParameter("callback")%>';
		if(queryback && queryback.length>0&&queryback!="null"&&(parent.window || parent.parent)){
			//2个页面调用通过 传递callback 参数值 对应页面调用对应方法  wangb 20180201
			if(queryback == 'closeAction'){
				parent.window.opener[queryback](infos);
				parent.window.close();
			}else if(queryback == 'openReturn'){
				parent.parent.opener.openReturn(infos);
				parent.parent.window.close();
			}else if(queryback == 'exportExcel'){//自助服务、统计分析、导出excel 功能   wangb 20180207 bug 34607
				if(getBrowseVersion()){
					parent.window.returnValue=infos;
					parent.window.close();
				}else{
					parent.parent.returnValue(infos);
				}
			}
		} else {
			returnValue=infos;
		    window.close();
		}
	}
	
	
	
	/**填充花名册指标和排序指标*/
	function filloutData() {
	    setselectitem('right_fields');
	    setselectitem('sort_right_fields');		
	}
	
	/**初化数据*/
	function MusterInitData(flag) {
	    var map = new HashMap();
	    map.put("infokind", flag);
   		Rpc({functionId:'0202001020',async:false,success:showSetList},map);
	}
	
	function show() {
		var historyRadios = document.getElementsByName("history");
		var historyValue = 1;
		for(var i = 0; i < historyRadios.length; i++){
			var radio = historyRadios[i];
			if(radio.checked) {
				historyValue = radio.value;
				break;
			}
		}
		
		var once = document.getElementById("once");
		var part = document.getElementById("part");
		if(1 == historyValue) {
			if(once)
				once.style.display="none";
			
			if(part)
				part.style.display="none";
			
		} else if(2 == historyValue) {
			if(once)
				once.style.display="block";
			
			if(part)
				part.style.display="none";
			
		} else {
			if(once)
				once.style.display="none";
			
			if(part)
				part.style.display="block";
			
			setFields();
		}
	}
	
	function checkFielset(){
		var rightFields=document.getElementsByName("right_fields")[0];
		if(rightFields.options.length==0) {
			var history = document.getElementById("history");
			var onceHistory = document.getElementById("onceHistory");
			var partHistory = document.getElementById("partHistory");
			var once = document.getElementById("once");
			var part = document.getElementById("part");
			var onceMsg = document.getElementById("onceMsg");
			var partMsg = document.getElementById("partMsg");
			if(history) {
				history.checked="true";
				history.disabled="disabled";
			}
			
			if(onceHistory)
				onceHistory.disabled="disabled";
			
			if(partHistory)
				partHistory.disabled="disabled";
			
			if(once)
				once.style.display="none";
			
			if(part)
				part.style.display="none";
			
			if(onceMsg)
				onceMsg.style.display="block";
			
			if(partMsg)
				partMsg.style.display="block";
			
	    	 return ;
		}
		
		var fields = "";
		for(var i=0;i<rightFields.options.length;i++) {
			fields += ","+rightFields.options[i].value;
		}
		
		var map = new HashMap();
	    map.put("fields", fields);
   		Rpc({functionId:'0202001025',async:false,success:showfield},map);
	}
	
	function showfield(response){
		var map	= Ext.decode(response.responseText);
		var flag = map.flag;
		var history = document.getElementById("history");
		var onceHistory = document.getElementById("onceHistory");
		var partHistory = document.getElementById("partHistory");
		var once = document.getElementById("once");
		var part = document.getElementById("part");
		var onceMsg = document.getElementById("onceMsg");
		var partMsg = document.getElementById("partMsg");
		if(1 == flag) {
			if(history)
				history.disabled="";
			
			if(onceHistory && "0" != map.changeFlag) {
				onceHistory.disabled="";
				if(onceMsg)
					onceMsg.style.display="none";
				
			}
			
			if(partHistory)
				partHistory.disabled="";
			
			if(partMsg)
				partMsg.style.display="none";
			
			var selectedfield = document.getElementById("selectedfield");
			selectedfield.options.length = 0;
			var fields = map.fieldList;
			for(var i = 0; i < fields.length; i++) {
				var field = fields[i];
				var id = field.split(":")[0];
				var text = field.split(":")[1];
				var option=document.createElement("option") 
				option.setAttribute("value", id);
				option.appendChild(document.createTextNode(text));
				selectedfield.appendChild(option) 
			}
			
			var selectedYear = document.getElementById("selectedYear");
			selectedYear.options.length = 0;
			var yearList = map.yearList;
			for(var i = 0; i < yearList.length; i++) {
				var year = yearList[i];
				var option=document.createElement("option") 
				option.setAttribute("value", year);
				option.appendChild(document.createTextNode(year));
				selectedYear.appendChild(option) 
			}
			
		} else {
			if(history) {
				history.checked="true";
				history.disabled="disabled";
			}
			
			if(onceHistory)
				onceHistory.disabled="disabled";
			
			if(partHistory)
				partHistory.disabled="disabled";
			
			if(once)
				once.style.display="none";
			
			if(part)
				part.style.display="none";
			
			if(onceMsg)
				onceMsg.style.display="block";
			
			if(partMsg)
				partMsg.style.display="block";
		}
	}
	
	function setFields() {
		var fieldSelect = document.getElementById("selectedfield");
		for(var i=0;i<fieldSelect.options.length;i++) {
			if(fieldSelect.options[i].selected==true) {
				var values=fieldSelect.options[i].value;
				var codeid=values.split("/");
				document.getElementById("character").style.display="none"; 
				document.getElementById("date").style.display="none"; 
				document.getElementById("code").style.display="none"; 
				if(codeid.length<2){
					var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&isPriv=1&allfields=1&fieldsetid="+codeid[0]; 
					var iTop = (window.screen.availHeight - 440) / 2;  //获得窗口的垂直位置
					var iLeft = (window.screen.availWidth - 710) / 2; //获得窗口的水平位置 
					window.open(thecodeurl,"_blank","left="+iLeft+",top="+iTop+",width=700,height=410,scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	    			document.getElementById("code").innerHTML="";
				} else if(codeid[2]=='D') {
					var now = new Date();
					var month=now.getMonth()+1;
					if(month<10)
						month="0"+month;
				
					var fromDate=(now.getYear()-1)+"-"+month+"-"+now.getDate()
				    var toDate=now.getYear()+"-"+month+"-"+now.getDate()

					document.getElementById("date").style.display="block"; 	
					document.getElementById('fromDate').value=fromDate;
					document.getElementById('toDate').value=toDate;
					document.getElementById("code").innerHTML="";
				} else if(codeid[3]==0) {
					document.getElementById("character").style.display="block";
				} else {
				    var fromImage="";
				    var toImage="";
				    var hidden="";
				    if(codeid[3]=="UN"||codeid[3]=='UM'||codeid[3]=="@K") {//update by xiegh on date bug35761
				    	//fromImage="<img src=\"/images/code.gif\" onclick='openCondCodeDialogsx(\""+codeid[3]+"\",\"from.hzvalue\");' align=\"middle\"/>&nbsp;";
				    	fromImage="&nbsp;<img src='/images/code.gif'  id='"+i+"from.value' align=\"absmiddle\" plugin='codeselector' codesetid="+codeid[3]+" inputname='from.hzvalue' valuename='from.value'   onlyselectcodeset='true'  id='"+i+".hzvalue_0'  onclick ='codeClick([\""+i+"from.value\"]);' />&nbsp;";
				    	//toImage="<img src=\"/images/code.gif\" onclick='openCondCodeDialogsx(\""+codeid[3]+"\",\"to.hzvalue\");' align=\"middle\"/>&nbsp;";
				    	toImage="&nbsp;<img src='/images/code.gif'  id='"+i+"to.value' align=\"absmiddle\" plugin='codeselector' codesetid="+codeid[3]+" inputname='to.hzvalue' valuename='to.value'   onlyselectcodeset='true'  id='"+i+".hzvalue_0'  onclick ='codeClick([\""+i+"to.value\"]);' />&nbsp;";
				    	hidden="<Input type='hidden' name='from.value' /><input type='hidden' name='to.value' /> ";
				    } else {
				    /* 	fromImage="<img src=\"/images/code.gif\" onclick='openCondCodeDialog(\""+codeid[3]+"\",\"from.hzvalue\");' align=\"middle\"/>&nbsp;";
				    	toImage="<img src=\"/images/code.gif\" onclick='openCondCodeDialog(\""+codeid[3]+"\",\"to.hzvalue\");' align=\"middle\"/>&nbsp;"; */
				    	fromImage="&nbsp;<img src='/images/code.gif' id='"+i+"from.value' align=\"absmiddle\" plugin='codeselector' codesetid="+codeid[3]+" inputname='from.hzvalue' valuename='from.value'   onlyselectcodeset='true'  id='"+i+".hzvalue_0'  onclick ='codeClick([\""+i+"from.value\"]);' />&nbsp;";
				    	toImage="&nbsp;<img src='/images/code.gif'  id='"+i+"to.value' align=\"absmiddle\" plugin='codeselector' codesetid="+codeid[3]+" inputname='to.hzvalue' valuename='to.value'   onlyselectcodeset='true'  id='"+i+".hzvalue_0'  onclick ='codeClick([\""+i+"to.value\"]);' />&nbsp;";
				    	hidden="<Input type='hidden' name='from.value' /><input type='hidden' name='to.value' /> ";
				    }
					var html=hidden+"从 <input type=text name='from.hzvalue'  size=\"10\" class=\"text4\" readOnly /> "+fromImage
						+"到 <input type=text name='to.hzvalue'  size=\"10\" class=\"text4\"   readOnly  />&nbsp;"+toImage;
					document.getElementById("code").style.display="block";
					document.getElementById("code").innerHTML=html;	
				}	
				break;
			}
		}

	}
	
	function returnFun(return_vo){
		if(return_vo!=null)
			whereStr = return_vo;
	} 
	
	function codeClick(eleId){
		setEleConnect(eleId);
	}
</script>
<style>
<!--
.nobottom {
	border-bottom: 0pt solid; 
}
.notop {
	border-top: 0pt solid;
}
-->
</style>
<html:form action="/hire/parameterSet/configureParameter">

<table width='99%' border="0" cellspacing="0" style="margin-left: 5px;" align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
   	   <td class="RecordRow nobottom" align="center" width="100%" nowrap>
   	   <table width="100%">
   	   <tr>
            <td width="100%" align="center" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
			<select name="setlist" size="1"  style="width:100%" onchange="searchFieldList();checkFielset();">    
			    <option value="1111">#</option>
                        </select>
                       
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');checkFielset();" style="height:159px;width:100%;font-size:9pt">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');checkFielset();">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');checkFielset();">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     
		              <select  size="10"  name="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');checkFielset();" style="height:180px;width:100%;font-size:9pt">
		             
		        </select>
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem(document.getElementsByName('right_fields')[0]);">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem(document.getElementsByName('right_fields')[0]);">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
            </table>
            </td>
            </tr>
            <tr>
                <td class="RecordRow nobottom" align="left">
                	<bean:message key="label.query.history.scope"/> 
                </td>
            </tr>
            <tr>
                <td class="RecordRow notop nobottom" align="left">
                <table width="100%">
                <tr>
                	<td width="100%;" height="30px;" align="left">
                 		<input type="radio" name="history" disabled="disabled" id="history" checked="checked" onclick="show()" value="1"/>
						<bean:message key="label.query.last.history"/> 
                	</td>
            	</tr>
            	<tr>
	                <td width="100%;" height="30px;" align="left">
	                	<div style="height: 30px;padding-top:2px;display: block;float: left;">
	                		<div style="display: block;float: left;">
	                    		<input type="radio" name="history" disabled="disabled" id="onceHistory" onclick="show()" value="2"/>
								<bean:message key="label.query.once.history"/> 
	                    	</div>
	                    	<div style="display: block;float: left;">
	                    		<span id='onceMsg'><bean:message key="label.query.only.one.yearsChange.subset"/></span>
	                    	</div>
	                   	</div>
	                    <div id="once" style="height: 30px;margin-left: 10px;display: none;float: left;">
					         <select id="selectedYear" size="1">
					         </select>
					         	<bean:message key="label.query.year"/>
					         <select id="selectedMonth" size="1">
					         	<option value="01">01</option>
					         	<option value="02">02</option>
					         	<option value="03">03</option>
					         	<option value="04">04</option>
					         	<option value="05">05</option>
					         	<option value="06">06</option>
					         	<option value="07">07</option>
					         	<option value="08">08</option>
					         	<option value="09">09</option>
					         	<option value="10">10</option>
					         	<option value="11">11</option>
					         	<option value="12">12</option>
					         </select>
					         	<bean:message key="label.query.month"/>
					         	&nbsp;<bean:message key="label.query.NO"/><input type="text" id="times" class="text4" value="1" size="3">次
						</div>
		            </td>
	            </tr>
	            <tr>
	                <td width="100%;" height="30px;" align="left">
	                	<div style="height: 30px;padding-top:2px;display: block;float: left;">
	                		<div style="display: block;float: left;">
		                    	<input type="radio" disabled="disabled" name="history" id="partHistory" onclick="show()"  value="3"/>
		                    	<bean:message key="label.query.part.history"/> 
		                    </div>
		                    <div style="display: block;float: left;">
		                    	<span id='partMsg'><bean:message key="label.query.only.one.subset"/></span>
		                    </div>
		                </div>
	                    <div id="part" style="height: 30px;margin-left: 10px;display: none;float: left;">
					         <select id="selectedfield" style="width: 120px;float: left;" size="1" onchange="setFields()"></select>
					         <div id="character" style="margin-left:5px;display: none;float: left;">
					         	<bean:message key="label.query.from"/>
					          	<input type="text" class="text4" id="from" size="10"/>
					         	<bean:message key="label.query.to"/>
					          	<input type="text" class="text4" id="to"  size="10"/>
					         </div>
					          <div id="date" style="margin-left:5px;display: none;float: left;">
					          	<div id="fromDateIndex" style="display: block;float: left;"></div>
					          	<div id="toDateIndex" style="display: block;float: left;"></div>
							  </div>	
							  <div id="code" style="margin-left:5px;display: none;float: left;"></div>                     		
						</div>
					</td>
				</tr>
                </table>
	            </td>
            </tr>
          <tr style="height:35px;">
          <td valign="middle" align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" nowrap>
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	  
	      &nbsp;
	       <html:button styleClass="mybutton" property="b_close" onclick="winClose();">
            		      <bean:message key="button.close"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>
<script language="javascript">
   MusterInitData('<%=request.getParameter("infokind")%>');
   
   //关闭弹窗方法  wangb 20180207
   function winClose(){
   		if('<%=request.getParameter("callback")%>' == 'exportExcel'){//自助服务 统计分析 导出excel 弹窗 关闭
   			if(getBrowseVersion()){//ie浏览器关闭
   				parent.window.close();
   			}else{//非ie浏览器关闭
   				parent.parent.winClose();
   			}
   		}else{//其他页面关闭
   			parent.window.close();//add by xiegh  on date 20180305 谁将window写成windwo了
   		}
   }
   
   Ext.onReady(function() {
	   Ext.create('Ext.form.field.Date', {
		   id:'fromDate',
		   width: "130px",
		   labelWidth: 20,
		   labelAlign: "right",
		   renderTo: "fromDateIndex",
		   xtype: 'datefield',
		   anchor: '100%',
		   format: 'Y-m-d',
		   fieldLabel: '<bean:message key="label.query.from"/>',
		});
	   
	   Ext.create('Ext.form.field.Date', {
		   id:'toDate',
		   width: "130px",
		   labelWidth: 20,
		   labelAlign: "right",
		   renderTo: "toDateIndex",
		   xtype: 'datefield',
		   anchor: '100%',
		   format: 'Y-m-d',
		   fieldLabel: '<bean:message key="label.query.to"/>',
		});
	});
   
</script>