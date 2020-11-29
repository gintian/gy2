<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" href="/css/locked-column2.css" type="text/css">

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all.js"></script>
<link rel='stylesheet' href='/ext/resources/css/ext-all.css' type='text/css' />
<style type="text/css">
body {
	background-color: transparent;
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
	float: left;
	margin-top: 1px;
	<!--[if gte IE 9]>padding-top: 1px;<![endif]-->
	<!--[if lt IE 8]>paddint-top:0px;<![endif]-->
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 14px;
	height: 12px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}

input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted;
	BORDER-LEFT: #FFFFFF 0pt dotted;
	BORDER-RIGHT: #FFFFFF 0pt dotted;
	BORDER-TOP: #FFFFFF 0pt dotted;
}

.unnamed2 {
	border: 1px solid #666666;
	background-color: #FFFFFF;
}
</style>
<STYLE type=text/css>
.div2 {
	overflow: auto;
	width: 100%;
	height: 270px;
	line-height: 15px;
	border-width: 1px;
	border-style: groove;
	border-width: thin;
}
</STYLE>
<hrms:themes></hrms:themes>
<script language="javascript">
var checkflag = "false";
  function selAll()
  {
     var len=document.repairKqCardFrom.elements.length;
     var i;
     if(checkflag=="false")
     {
       for (i=0;i<len;i++)
       {
         if (document.repairKqCardFrom.elements[i].type=="checkbox")
         {
              document.repairKqCardFrom.elements[i].checked=true;
          }
       }
       checkflag="true";
     }else
     {
       for (i=0;i<len;i++)
       {
         if (document.repairKqCardFrom.elements[i].type=="checkbox")
         {
              document.repairKqCardFrom.elements[i].checked=false;
          }
       }
       checkflag="false";
     }
     
     
  } 
  
  function IsDigit() 
  { 
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    return ((keyCode >= 48) && (keyCode <= 57));  
  } 
 function IsInputValue(textid) 
 {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if (!fObj) return;		
		var cmd = event.srcElement.innerText=="5"?true:false;		
		if(fObj.value=="")
		{
		   fObj.value="1";
		}else
		{
		   var i = parseInt(fObj.value,10);
		   var radix = 301-1;		
		   if (i==radix&&cmd) {
			i = 1;
		   } else if (i==1&&!cmd) {
			i = radix;
		   } else {
			cmd?i++:i--;
		   }		
		   fObj.value = i;
		}
		
		fObj.select();
} 
</script>
<script language="javascript">
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getdate(tt,flag)
   {
     outObject=tt;     
     var hashvo=new ParameterSet();     
     hashvo.setValue("date",tt.value);  
     hashvo.setValue("flag",flag);     		
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1510010020'},hashvo);
   }
   function showSelect(outparamters)
   { 
     var tes=outparamters.getValue("re_date");     
     outObject.value=tes;
   }
   
   	function splitLeng(){
		var o = document.getElementById('causation');
		var str = o.value;
		if(str.length <= 25){ 
			return; 
		}
		var strMaxLeng = 50;
		var tempStr = "";
		var strLeng = 0;
		
		for(var i = 0;i < str.length;i++){
			if(str.charCodeAt(i)>255) {
				strLeng += 2;
			}else{
				strLeng++;
			}
			if(strLeng > 50){
				o.value = tempStr;
			}else{
				tempStr += str.charAt(i);
			}
		}
	}
</script>
<script language="javascript">  
	this.fObj = null;
	var time_r=0;
	function setFocusObj(obj,time_vv) {		
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

  function setRepair(fashion)
  {
     var vo_obj= document.getElementById('repair_fashion');
     vo_obj.value=fashion;
     if(fashion=="0")
     {
        var waitInfo=eval("intricacy");	
	    waitInfo.style.display="none";
	    waitInfo=eval("easy");	
	    waitInfo.style.display="block";	    
     }else if(fashion=="1")
     {
        var waitInfo=eval("easy");	
	    waitInfo.style.display="none";
	    waitInfo=eval("intricacy");	
	    waitInfo.style.display="block";	    
     }
  }
  function getSelEmp() {
      var empTab = document.getElementById("tbl");
      var rowCnt = empTab.rows.length;
      var colCnt = empTab.rows[0].cells.length;
      var ids = "";

      for (var i=1;i<rowCnt;i++)
      {   
          if(empTab.rows[i].cells[colCnt-1].getElementsByTagName("input")[0].checked==true)
          { 
             var empId;
             
             if(getIEVersion()!="")
             {
                 empId=empTab.rows[i].cells[0].innerText; 
             }
             else
             {
                 empId=empTab.rows[i].cells[0].textContent;
             }              
             ids = ids + trim(empId) + ",";
          }
      }
      return ids;
 }
  var flag_biaozhi = 0;  
   function repairCard()
  {
	   var hashvo=new ParameterSet();
	   var empids = getSelEmp();
	    if(empids=='')
	    {
	      alert("请选择人员！");
	      return;
	    }
	    
  	  var flag = 0;
      var vo_obj= document.getElementById('repair_fashion');
      var fashion= vo_obj.value;
      if(fashion=="0")
      {
          flag = 1;
          var easy_date_o=document.getElementById("editor1").value;
          if(easy_date_o==null)
          	return false;
          var easy_date_o1=easy_date_o;
          if(trim(easy_date_o1)=="")
          {
          	alert("补刷日期不能为空！");
          	return false;
          }else if(!isDate(easy_date_o1,"yyyy.MM.dd"))
          {
              alert("补刷日期格式错误，应该为yyyy.MM.dd！");
              return false;
          }
          var easyTime = Ext.getCmp('easy_time_field').getValue();
          var easyTimeHour = Ext.Date.format(easyTime,'H');
          var easyTimeMinute = Ext.Date.format(easyTime,'i');

          if(easyTimeHour=="")
          {
              alert("补刷时间不能为空！");
              return false;
          }
          
          hashvo.setValue("jddate", easy_date_o1);
          hashvo.setValue("easy_hh", easyTimeHour);
          hashvo.setValue("easy_mm", easyTimeMinute);
      }
      else if(fashion=="1")
      {
      	
          var repair_flag_o=document.getElementsByName("repair_flag");          
          if(repair_flag_o==null)
            return false;
          var repair_flag_o1="";
          for(var i=0;i<repair_flag_o.length;i++)          
          {
            if(repair_flag_o[i].checked)
               repair_flag_o1=repair_flag_o[i].value;
          }          
          if(repair_flag_o1=="0")
          {flag = 2;
              var statr_date_o=document.getElementsByName("statr_date"); 
              if(statr_date_o==null)
                return false;
              var statr_date_o1=statr_date_o[0];
              if(statr_date_o1.value=="")
              {
                alert("开始日期不能为空！");
                return false;
              }
              var end_date_o=document.getElementsByName("end_date"); 
              if(end_date_o==null)
                return false;
              var end_date_o1=end_date_o[0];
              if(end_date_o1.value=="")
              {
                alert("结束日期不能为空！");
                return false;
              }
              var symd = statr_date_o1.value;
              var eymd = end_date_o1.value;
              var sysDate = new Date();
              symd = symd.replace(/\./g, "/");
              eymd = eymd.replace(/\./g, "/");
              var appsDate = new Date(Date.parse(symd.replace(/-/g, "/"))); 
              var appeDate = new Date(Date.parse(eymd.replace(/-/g, "/")));
          	  if(appsDate.getTime() > appeDate.getTime()){
          	  	alert("开始时间大于结束时间！");
          	  	return false;
          	  }
          	  if(sysDate.getTime() < appeDate.getTime()){
          	    alert("申请时间大于当前系统时间！");
          	    return false;
          	  }
          	hashvo.setValue("statr_date", statr_date_o1.value);
            hashvo.setValue("end_date", end_date_o1.value);
            var classflag = document.getElementsByName("class_flag");
            for (var j=0; j<classflag.length; j++) {
                if(classflag[j].checked) {
                    hashvo.setValue("class_flag", classflag[j].value);
                }
            }
          }else if(repair_flag_o1=="1")
          {
           flag = 3;
         var cycle_date_o=document.getElementsByName("cycle_date"); 
             if(cycle_date_o==null)
              return false;            
             var cycle_date_o1=cycle_date_o[0];
             if(cycle_date_o1.value=="")
             {
                alert("循环日期不能为空！");
                return false;
             }
			var addDay = document.getElementById("cycle_num").value;
			if(addDay > 1){
			    addDay = addDay - 1;
			} else {
			    addDay = 0;
			}
          	var cycleTime = Ext.getCmp('cycle_time_field').getValue();
            var cycleTimeHour = Ext.Date.format(cycleTime,'H');
            var cycleTimeMinute = Ext.Date.format(cycleTime,'i');

            if(cycleTimeHour=="")
            {
                alert("补刷时间不能为空！");
                return false;
            }
          	
          	hashvo.setValue("cycle_date", cycle_date_o1.value);
            hashvo.setValue("cycle_num", addDay);
            hashvo.setValue("cycle_hh", cycleTimeHour);
            hashvo.setValue("cycle_mm", cycleTimeMinute);
          	
          }
          
      }    
      //var hashvo=new ParameterSet();
      if(flag=="1"){
        var z1 = document.getElementById("editor1").value;
      	hashvo.setValue("z1",z1);
      	hashvo.setValue("z1str","补刷时间");
      	hashvo.setValue("z3",z1);
      	hashvo.setValue("flag",flag);
      	
      } else if (flag=="2") {
      	var start_dateObj = document.getElementsByName("statr_date")[0];
   		var end_dateObj = document.getElementsByName("end_date")[0];
   		hashvo.setValue("z1",start_dateObj.value);
  		hashvo.setValue("z1str","开始时间");
  		hashvo.setValue("z3",end_dateObj.value);
  		hashvo.setValue("z3str","结束时间");	
  		hashvo.setValue("flag",flag);
      } else if (flag == "3") {
      	var obj = document.getElementsByName("cycle_date")[0];
      	var cycle_num = document.getElementById("cycle_num").value;
      	if(!checkIsIntNum(cycle_num)){
			alert("循环天数需为整数！");
			return false;
        }
      	hashvo.setValue("z1",obj.value);
  		hashvo.setValue("z1str","循环时间");
  		hashvo.setValue("cycle_num",cycle_num);
  		hashvo.setValue("flag",flag);
      }
      //var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
          
      if (flag_biaozhi = 0) {
      	return ;
      } 
      
      var ip_adr=getLocalIPAddressf();
      if(ip_adr=="")
      {
         alert("无法得到客户端的计算机IP，请重新打开该页面，允许Active控件交互！");
      }else
      {
         var causation_o=document.getElementsByName("causation");             
         if(causation_o==null)
           return false;
         var causation_o1=causation_o[0];             
         if(causation_o1.value=="")
         {
            alert("补刷卡原因不能为空！");
            return false;
         }else if(IsOverStrLength(trim(causation_o1.value), 250))
         {
               alert("补刷卡原因字数不能超过250个英文字符或125个汉字！");
            return false;
         }
         else
         {
        	 hashvo.setValue("repair_fashion", fashion);
             if("1"==fashion)
                 hashvo.setValue("repair_flag", repair_flag_o1);
             hashvo.setValue("into_flag", document.getElementsByName("into_flag")[0].value);
             hashvo.setValue("causation", getEncodeStr(causation_o1.value));
             hashvo.setValue("ip_adr", ip_adr);
             hashvo.setValue("ids", empids);
             hashvo.setValue("temp_emp_table", "${repairKqCardFrom.temp_emp_table}");
             var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'15211001202'},hashvo);
         }             
      }
          
           
  }
   function returnResult(outparamters) {
	    var resultStr = outparamters.getValue("repair_reflag");
	    if (resultStr == "ok") {
	    	var repeatFlag = outparamters.getValue("repeat_flag");
	    	var infmsg = "补刷成功，是否继续？";
	    	if(repeatFlag == "1"){
	    		infmsg += "\n（部分人员相同时点已有刷卡，不再重复补签）";
			}
	        if(!confirm(infmsg)){
	        	//selectflag();   
	        	var thevo=new Object();
                thevo.flag="true";
                window.returnValue=thevo;
                window.dialogArguments.location.href=window.dialogArguments.document.location.href;
                window.close();
	        }
	    } else {
	        alert(resultStr);
	    } 
	 }
  /**取得本地机器ip地址*/
function getLocalIPAddressf()
{
    var obj = null;
    var rslt = "";   
    try
    {
        obj=document.getElementById('SetIE');
        	rslt = obj.GetIP();
        obj = null;
    }
    catch(e)
    {
    	//异常发生
    }
    return rslt;
}

function selectflag(){
	repairKqCardFrom.action="/kq/machine/repair_card.do?b_query=link";
    //repairKqCardFrom.target="_self";
    repairKqCardFrom.submit();
}
function openselect(){
	var winFeatures = "dialogHeight:600px; dialogLeft:450px;"; 
	var target_url="/kq/machine/select/selectfiled.do?b_init=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url,1, 
		"dialogWidth:596px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");      
	if(return_vo!=null)
		window.location.href="/kq/machine/repair_card.do?b_query=link";
}

  function opConent(obj)
  {
     if(obj==null)
       return false;
     var value=obj.value;
     if(value=="")
       value="00";
     if(value.length==1)
      value="0"+value;
     if(value.length<1)
      value="00";     
     obj.value=value;
  }
  function focusTr(flag){
		if("1" == flag){
			document.getElementById("tr1").style.background = "#E5F2FF";
			document.getElementById("tr2").style.background = "";
		}else if("2" == flag){
			document.getElementById("tr1").style.background = "";
			document.getElementById("tr2").style.background = "#E5F2FF";
		}
  }
  document.body.focus();
</script>
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
<html:form action="/kq/machine/repair_card" styleId="repairKqCardFromId">
<div class="" align="center" style="height: 100%;width: 100%;border: none;padding:0 0 0 20;">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0"  >
		<%
			int i = 1;
		%>
		<tr >
			<td align="left"  style="border-bottom-width: 0px;" nowrap>
				&nbsp;
				<bean:message key="kq.repair.card" />
				&nbsp;
				<html:hidden name="repairKqCardFrom" property="a_code"
					styleClass="text" />
				<html:hidden name="repairKqCardFrom" property="nbase"
					styleClass="text" />
				<html:hidden name="repairKqCardFrom" property="temp_emp_table"
					styleClass="text" />
				<html:hidden name="repairKqCardFrom" styleId="repair_fashion"
					property="repair_fashion" styleClass="text" />
				&nbsp;
				<logic:notEqual name="repairKqCardFrom" property="noCardFlag" value="1">
				按&nbsp;<html:select name="repairKqCardFrom" property="select_type"  size="1">
			            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
			                <html:option value="1">工号</html:option>
			                <html:option value="2">考勤卡号</html:option>
		              </html:select>
		              <input type="text" name="select_name" value="${repairKqCardFrom.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">&nbsp;
		              <button class="mybutton" onclick="javascript:selectflag();">查询</button>&nbsp; 
		              <button class="mybutton" onclick="openselect();">条件查询</button>
                </logic:notEqual>
			</td>
		</tr>
<tr><td height="4px"></td></tr>
		<tr>
			<td width="100%" align="center" class="RecordRow" style="padding:0" valign="Top" nowrap>
				
							<div id="tblcontainer" class="div2"
								style="border-style: solid; border-width: 0px;">
								<table cellSpacing=0 cellPadding=0 width="100%" border=0
									id="tbl" class="ListTable">
									<thead>
										<tr class="fixedHeaderTr">
										    <th style="display:none">id</th>
											<th class="TableRow" style="border-top: none;border-left: none" nowrap>
												<bean:message key="label.serialnumber" />
											</th>
											<th class="TableRow" style="border-top: none" nowrap>
												单位名称
											</th>
											<th class="TableRow" style="border-top: none" nowrap>
												部门
											</th>
											<th class="TableRow" style="border-top: none" nowrap>
												姓名
											</th>
											<th class="TableRow" style="border-top: none" nowrap>
												工号
											</th>
											<th class="TableRow" style="border-top: none" nowrap>
												考勤卡号
											</th>
											<th align="center" height="18" class="TableRow" style="border-top: none;" nowrap>
												<input type="checkbox" name="aa" value="true"
													onclick="selAll()">
											</th>
										</tr>
									</thead>
									<tbody>
										<hrms:paginationdb id="element" name="repairKqCardFrom"
											sql_str="repairKqCardFrom.sqlstr" table=""
											where_str="repairKqCardFrom.where"
											columns="repairKqCardFrom.column"
											order_by="order by b0110,e0122,a0100" pagerows="${repairKqCardFrom.pagerows}"
											page_id="pagination">
											<bean:define id="nbase" name="element" property="nbase"/>
				                            <bean:define id="a0100" name="element" property="a0100"/>
				                            <% 
				                              String empid = SafeCode.encode(PubFunc.encrypt(nbase.toString()+a0100.toString()));
				                            %>
											
											<tr>
											    <td style="display:none"><%=empid %></td>
												<td align="center" class="RecordRow" style="border-left: none" nowrap>
													<%=i%>
												</td>
												<td align="left" class="RecordRow" nowrap>
													&nbsp;
													<hrms:codetoname codeid="UN" name="element"
														codevalue="b0110" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codename" />
													&nbsp;
												</td>
												<td align="left" class="RecordRow" nowrap>
													&nbsp;
													<hrms:codetoname codeid="UM" name="element"
														codevalue="e0122" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codename" />
													&nbsp;
												</td>

												<td align="left" class="RecordRow" nowrap>
													&nbsp;
													<bean:write name="element" property="a0101" filter="true" />
												</td>
												<td align="left" class="RecordRow" nowrap>
													&nbsp;
													<bean:write name="element" property="g_no" filter="true" />
												</td>
												<td align="left" class="RecordRow" nowrap>
													&nbsp;
													<bean:write name="element" property="card_no" filter="true" />
												</td>
												<td align="center" class="RecordRow" nowrap>
													<hrms:checkmultibox name="repairKqCardFrom"
														property="pagination.select" value="true"
														indexes="indexes" />
												</td>
											</tr>
											<%
												i++;
											%>
										</hrms:paginationdb>
									</tbody>
								</table>
							</div>
			</td>
		</tr>
		 <tr>
		   <td >
		     <table  style="width:100%;" class="RecordRowP" align="left">       
		            <tr>
		        <td valign="bottom" class="tdFontcolor">
		        <hrms:paginationtag name="repairKqCardFrom" pagerows="${repairKqCardFrom.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		        </td>
		        <td   align="right" nowrap class="tdFontcolor">
		             <p align="right"><hrms:paginationdblink name="repairKqCardFrom" property="pagination" nameId="repairKqCardFrom" scope="page">
		            </hrms:paginationdblink>
		        </td>
		        </tr>   
		     </table>
		   </td>
		 </tr>
		 <tr><td height="4px"></td></tr>
		<tr>
			<td>
				<table border="0" cellspacing="0" align="left" cellpadding="0">
					<tr>
						<td>
							<a href="###" onclick="setRepair('0');">简单规则</a> &nbsp;&nbsp;
						</td>
						<td>
							<a href="###" onclick="setRepair('1');">复杂规则</a>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr><td height="4px"></td></tr>
		<tr>
			<td valign="top" nowrap >
				<div id="intricacy"
					style="padding: 8px; border-color: #C4D8EE; border-width: 1px; border-style: solid; height: 30px; width: 100%;" class="DetailTable common_border_color">
					<table width="100%" border="0" cellspacing="0"
						class="DetailTable" cellpadding="0">
						<tr id="tr1">
							<td>
								<table width="100%" border="0" cellspacing="0"
									class="DetailTable" cellpadding="0">
									<tr>
										<td width="100" valign="top" nowrap="nowrap">
											<html:radio name="repairKqCardFrom" property="repair_flag"
												value="0"/>
											&nbsp;
											<bean:message key="kq.shift.circs" />
											&nbsp;
										</td>
										<td valign="top" nowrap="nowrap" align="left">
											<bean:message key="label.from" />
											<input type="text" class="inputtext" name="statr_date" readonly="readonly" size="12" extra="editor" dataType="simpledate" value="${repairKqCardFrom.statr_date}" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
											&nbsp;
											<bean:message key="label.to" />
											<input type="text" class="inputtext" name="end_date" readonly="readonly" size="12" extra="editor" dataType="simpledate" value="${repairKqCardFrom.end_date}" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
										</td>
										<td valign="top" nowrap="nowrap">
											<html:radio name="repairKqCardFrom" property="class_flag"
												value="0" />
											<bean:message key="kq.repair.up_down" />
											<html:radio name="repairKqCardFrom" property="class_flag"
												value="1" />
											<bean:message key="kq.repair.up" />
											<html:radio name="repairKqCardFrom" property="class_flag"
												value="2" />
											<bean:message key="kq.repair.down" />
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr align="center">
							<td colspan="4">
								<hr width="100%">
							</td>
						</tr>
						<tr id="tr2">
							<td>
								<table width="100%" border="0" cellpmoding="0" cellspacing="0"
									class="DetailTable" cellpadding="0">
									<tr>
										<td valign="top" height="25" width="100">
											<html:radio name="repairKqCardFrom" property="repair_flag"
												value="1"/>
											&nbsp;多天
										</td>
										<td width="300" valign="top">
											<!------>
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td >
														<bean:message key="kq.repair.cycle.time" />
														&nbsp;
													</td>
													<td valign="top" align="center">
													<table border="0" cellspacing="0" cellpadding="0">
                                                            <tr>
                                                                <td>
                                                                    &nbsp;
                                                                    <input type="text" style="line-height:18px;vertical-align:bottom;padding-top:3px;" class="inputtext complex_border_color" name="cycle_date" size="12" readonly="readonly" extra="editor" dataType="simpledate" value="${repairKqCardFrom.cycle_date}" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);"/>
                                                                    &nbsp;
                                                                </td>
                                                                <td>
                                                                    <div id="cycle_time"></div>
                                                                </td>
						                                    </tr>
					                                  </table>
													</td>
												</tr>
											</table>
										</td>
										<td align="left">
											<table border="0" cellspacing="0" cellpadding="0">
												<tr>
													<td width="45%">
														<bean:message key="kq.repair.cycle.day" />
														&nbsp;&nbsp;
													</td>
													<td valign="top" align="left">
														<table border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td align="left" valign="middle">
																	<html:text  name="repairKqCardFrom" styleId='id_len' 
																		property="cycle_num" size="4" maxlength="2" styleClass="inputtext complex_border_color"
																		onkeypress="event.returnValue=IsDigit();" />
																	&nbsp;
																</td>
																<td valign="middle" align="left">
																	<table border="0" cellspacing="2" cellpadding="0">
																		<tr>
																			<td>
																				<button id="1_up" class="m_arrow"
																					onmouseup="IsInputValue('id_len');">
																					5
																				</button>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<button id="1_down" class="m_arrow"
																					onmouseup="IsInputValue('id_len');">
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
								</table>
							</td>
						<tr>
					</table>
				</div>
				<div id="easy"
					style="padding: 5px; border-color: #C4D8EE; border-width: 1px; border-style: solid; height: 70px; width: 100%;" class="common_border_color">
					<table>
						<tr>
							<td>
								<table border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td valign="middle">
											补刷时间&nbsp;
										</td>
										<td align="center">
										<table border="0" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td>
                                                        &nbsp;
                                                        <input type="text" style="line-height:18px;vertical-align:bottom;padding-top:3px;" 
                                                             class="inputtext complex_border_color" name="editor1" readonly="readonly" 
                                                             extra="editor" dataType="simpledate" value="${repairKqCardFrom.statr_date}"
                                                             onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' 
                                                             onchange="rep_dateValue(this);">
                                                        &nbsp;
                                                    </td>
                                                    <td>
							                            <div id="easy_time"></div>
						                            </td>
					                            </tr>
					                         </table>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr><td height="4px"></td></tr>
		<tr>
			<td valign="Top" nowrap class="DetailTable">
			<div id="d" style="padding: 5px; border-color: #C4D8EE; border-width: 1px; border-style: solid; height: 70px; width: 100%;" class="common_border_color">
				<table>
					<tr>
						<td>
							<table cellspacing="0" class="DetailTable" cellpadding="0">
								<tr height="5px">
									<td colspan="2">
									</td>
								</tr>
								<tr>
									<td align="right" style="border: 0px;">
										出入类型&nbsp;&nbsp;&nbsp;
									</td>
									<td valign="middle" style="border: 0px;">
										<%--<logic:equal name="repairKqCardFrom" property="isInout_flag" value="true">--%>
										<html:select name="repairKqCardFrom" property="into_flag"
											size="1">
											<html:option value="0">不限</html:option>
											<html:option value="-1">出</html:option>
											<html:option value="1">进</html:option>
										</html:select>
										<%--</logic:equal>--%>
									</td>
								</tr>
								<tr height="5px">
									<td colspan="2">
									</td>
								</tr>
								<tr>
									<td align="right" style="border: 0px;">
										补刷原因&nbsp;&nbsp;&nbsp;
									</td>
									<td style="border: 0px;">
										<logic:equal name="repairKqCardFrom" property="card_causation"
											value="">
											<textarea name="causation" cols="60" rows="3" ></textarea>
										</logic:equal>
										<logic:notEqual name="repairKqCardFrom"
											property="card_causation" value="">
											<html:text name="repairKqCardFrom" property='causation'
												size="20" readonly="true" />
											<input type="hidden" name="causation_hidden" value="1">
											<img src="/images/code.gif"
												onclick='javascript:openInputCodeDialogText("${repairKqCardFrom.card_causation}","causation","causation_hidden");' />
										</logic:notEqual>
									</td>
								</tr>
								<tr height="5px">
									<td colspan="2">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
			</td>
        </tr>
		<tr>
			<td align="center" nowrap style="height: 65px;">
			<!-- 
				<input type="button" name="b_delete"
					value='<bean:message key="label.query.selectall"/>'
					class="mybutton" onclick="selAll();">
				&nbsp;&nbsp;&nbsp;&nbsp;
				 -->
				<input type="button" name="btnreturn"
					value='<bean:message key="button.ok"/>' onclick="repairCard();"
					class="mybutton">
				
				<input type="button" name="b_next"
					value="<bean:message key="button.close"/>"
					onclick="window.close();" class="mybutton">
			</td>
		</tr>
		<tr><td></td></tr>
	</table>
	</div>
</html:form>
 <div id='axc' style='display:none'/>
 
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 InitAx();
 </script>
<script language="javascript">
setRepair("0");
selAll();
</script>
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
 //focusTr('1');
 createTimeField('easy_time_field', 'easy_time', "${repairKqCardFrom.easy_hh}");
createTimeField('cycle_time_field', 'cycle_time', "${repairKqCardFrom.easy_hh}");
</script>