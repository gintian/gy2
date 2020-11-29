<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 42px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;	
	font-size: 6px;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input {
	width: 18px;
	height: 14px;
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
.div2
{
 overflow:auto; 
 width: 100%;height: 280px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 
} 
</STYLE>
<script language="javascript">
var checkflag = "false";
  function selAll()
  {
     var len=document.dataAnalyseForm.elements.length;
     var i;
     if(checkflag=="false")
     {
       for (i=0;i<len;i++)
       {
         if (document.dataAnalyseForm.elements[i].type=="checkbox")
         {
              document.dataAnalyseForm.elements[i].checked=true;
          }
       }
       checkflag="true";
     }else
     {
       for (i=0;i<len;i++)
       {
         if (document.dataAnalyseForm.elements[i].type=="checkbox")
         {
              document.dataAnalyseForm.elements[i].checked=false;
          }
       }
       checkflag="false";
     }
  }
  function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
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
	function getKqCalendarVar()
   {
     var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'});
   }
   function setKqCalendarVar(outparamters)
   {
       weeks=outparamters.getValue("weeks");  
       feasts=outparamters.getValue("feasts");  
       turn_dates=outparamters.getValue("turn_dates");  
       week_dates=outparamters.getValue("week_dates");  
   } 
</script>
<script language="javascript">
    this.fObj = null;
	var time_r=0;
function setFocusObj(obj,time_vv) {		
		this.fObj = obj;
		time_r=time_vv;		
}
//简单规则
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
  function rep_dateValue(obj)
  {
    var d_value=obj.value;
    if(d_value!="")
    {
       d_value=d_value.replace("-",".");
       d_value=d_value.replace("-",".");
       obj.value=d_value;
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
  
  var flag_state = 0;
  function repairCard()
  {
	  var hashvo=new ParameterSet();

    var empids = getSelEmp();
    if(empids=='')
    {
      alert("请选择人员！");
      return;
    }
	  
	  var vo_obj= document.getElementById('repair_fashion');
    var fashion= vo_obj.value;
    var str="";
    var flag_biaozhi = 0;

    if(fashion=="0")
    {	
      flag_biaozhi = 1;
  	  var easy_date_o=document.getElementsByName("jddate"); 
      if(easy_date_o==null)
        return false;
      
      var easy_date_o1=easy_date_o[0];
      if(easy_date_o1.value=="")
      {
          alert("补刷日期不能为空！");
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
      
      hashvo.setValue("jddate", easy_date_o1.value);
      hashvo.setValue("easy_hh", easyTimeHour);
      hashvo.setValue("easy_mm", easyTimeMinute);
    }else if(fashion=="1")
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
      { 
        flag_biaozhi = 2;
        var statr_date_o=document.getElementsByName("statr_date"); 
        if(statr_date_o==null)
          return false;

        var statr_date_o1=statr_date_o[0];
        if(statr_date_o1.value=="")
        {
          alert("开始日期不能为空！");
          return false;
        }

        var end_date_o=document.getElementsByName("end_date_patch"); 
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
        //var sysDate = new Date();
        symd = symd.replace(/\./g, "/");
        eymd = eymd.replace(/\./g, "/");
        var appsDate = new Date(Date.parse(symd.replace(/-/g, "/"))); 
        var appeDate = new Date(Date.parse(eymd.replace(/-/g, "/")));
    	  if(appsDate.getTime() > appeDate.getTime()){
    	  	alert("开始时间大于结束时间！");
    	  	return false;
    	  }
          	  
      	hashvo.setValue("statr_date", statr_date_o1.value);
        hashvo.setValue("end_date_patch", end_date_o1.value);
        var classflag = document.getElementsByName("class_flag");
        for (var j=0; j<classflag.length; j++) {
        	if(classflag[j].checked) {
        		hashvo.setValue("class_flag", classflag[j].value);
        	}
        }
      }else if(repair_flag_o1=="1")
      {
         flag_biaozhi = 3;
         var cycle_date_o=document.getElementsByName("cycle_date"); 
         if(cycle_date_o==null)
          return false;            
         var cycle_date_o1=cycle_date_o[0];
         if(cycle_date_o1.value=="")
         {
            alert("循环日期不能为空！");
            return false;
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
         hashvo.setValue("cycle_num", Ext.getCmp('cycle_num_field').getValue());
         hashvo.setValue("cycle_hh", cycleTimeHour);
         hashvo.setValue("cycle_mm", cycleTimeMinute);
      }
    }

	   // if (flag_biaozhi == 1) {
    //  		var obj = document.getElementsByName("jddate")[0];
    //  		hashvo.setValue("z1",obj.value);
  		// 	hashvo.setValue("z1str","补刷日期");
    //  	} else if (flag_biaozhi == 2) {
    //  		var objstart = document.getElementsByName("statr_date")[0];
    //  		hashvo.setValue("z1",objstart.value);
  		// 	hashvo.setValue("z1str","开始日期");
  		// 	var objend = document.getElementsByName("end_date_patch")[0];
  		// 	hashvo.setValue("z3",objend.value);
  		// 	hashvo.setValue("z3str","结束日期");
    //  	} else if (flag_biaozhi == 3) {
    //  		var obj = document.getElementsByName("cycle_date")[0];
    //  		hashvo.setValue("z1",obj.value);
  		// 	hashvo.setValue("z1str","循环日期");
    //  	}
     	//补刷卡不用进行日明细状态的判断，参考补刷卡模块 wangmj 2013-12-27
     	//var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
     	//if (flag_state == 0) {
     	//	return;
     	//}
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
        }

        if(causation_o1.value.length>=50)
        {
          alert("补刷卡原因字数不能超过50个字！");
          return false;
        }
         
        hashvo.setValue("repair_fashion", fashion);
        if("1"==fashion)
        	hashvo.setValue("repair_flag", repair_flag_o1);
        hashvo.setValue("into_flag", document.getElementsByName("into_flag")[0].value);
        hashvo.setValue("causation", getEncodeStr(causation_o1.value));
        hashvo.setValue("ip_adr", ip_adr);
        hashvo.setValue("ids", empids);
        var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'152110013122'},hashvo);
         
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
		if(!confirm(infmsg))
			returnS();
	} else {
		alert(resultStr);
	} 
 }
 function  returnS()
 {
    dataAnalyseForm.action="/kq/machine/analyse/data_analyse_data.do?b_query=link";
    dataAnalyseForm.target="mil_body";
    dataAnalyseForm.submit();
 }
  function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
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
<html:form action="/kq/machine/analyse/analyse_patch" styleId="repairKqCardFromId">
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    <br/>
	<% int i=1;%>
	<tr style="border: 0px;">
		<td align="left" class="TableRow" nowrap>
			<bean:message key="kq.repair.card"/>&nbsp;&nbsp;
			<html:hidden name="dataAnalyseForm" styleId="repair_fashion" property="repair_fashion" styleClass="text"/>  
		</td>
	</tr>
	<tr>
		<td width="100%" align="center" class="RecordRow" style="border-top: 0px;padding:0;"  valign="Top" nowrap>

					<div id="tbl-container"  class="div2" style="border-style: solid;border-width: 0px;">
						<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 id="tbl" class="ListTable">
						<thead>
						<tr>
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
						 <th align="center" height="18" class="TableRow" style="border-top: none;border-right: none" nowrap>
							<input type="checkbox" name="aa" value="true"
								onclick="selAll()">
						</th>
						</tr>
						</thead>
						<tbody>
						<hrms:paginationdb id="element" name="dataAnalyseForm" sql_str="dataAnalyseForm.sqlstrpatch" table="" where_str="" columns="dataAnalyseForm.columnpatch" order_by="dataAnalyseForm.orderpatch" pagerows="4999" page_id="pagination">
							<bean:define id="nbase" name="element" property="nbase"/>
							<bean:define id="a0100" name="element" property="a0100"/>
							<% 
							  String empid = SafeCode.encode(PubFunc.encrypt(nbase.toString()+a0100.toString()));
							%>
							<tr>
                              <td style="display:none"><%=empid %></td>
							  <td align="center" class="RecordRow" style="border-left: none"  nowrap> 
                              <%=i%>
                              </td>
                              
	                          <td align="left" class="RecordRow" nowrap>
                                  &nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	                   <bean:write name="codeitem" property="codename" />&nbsp; 
                              </td>
                              <td align="left" class="RecordRow" nowrap>
                                 &nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	                  <bean:write name="codeitem" property="codename" />&nbsp;  
                              </td>
	                      	  <td align="left" class="RecordRow" nowrap>           
                                 &nbsp;<bean:write name="element" property="a0101" filter="true"/>        	
	                          </td>
                              <td align="left" class="RecordRow" nowrap>           
                                 &nbsp;<bean:write name="element" property="g_no" filter="true"/>        	
	                      	  </td>
                               <td align="left" class="RecordRow" nowrap>           
                                 &nbsp;<bean:write name="element" property="card_no" filter="true"/>        	
	                      	  </td>
                              <td align="center" class="RecordRow" style="border-right: none"  nowrap> 
                               <hrms:checkmultibox name="dataAnalyseForm" property="pagination.select" value="1" indexes="indexes"/>
                              </td>
							</tr>
							<%i++;%>
						</hrms:paginationdb>
						</tbody>
						</TABLE>
					</div>
		
		</td>
	</tr>
	<tr>
            <td>
              <table border="0" cellspacing="0"  align="left" cellpadding="0">
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
    <tr>
    	<td valign="Top" nowrap class="DetailTable" >
    	  <div id="intricacy" style="padding:5px;border-color:#C4D8EE;border-width:1px;border-style:solid;height:70px;width:100%;" class="common_border_color"> 
    	  	<table width="100%"  border="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
    	  		<tr id="tr1" >
    	  			<td>
    	  				<table width="100%"  border="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                          <tr> 
                          <td height="25" width="100"> 
                           <html:radio name="dataAnalyseForm" property="repair_flag" value="0"/>
                           &nbsp;<bean:message key="kq.shift.circs"/>&nbsp;
                          </td> 
                           <td> 
                            <bean:message key="label.from"/>
                            <!--<html:text name="dataAnalyseForm" property="statr_date" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);"/> -->
                            <input type="text" name="statr_date" extra="editor" dataType="simpledate" class="inputtext" style="width:100px;font-size:10pt;text-align:left"  size="10" value="${dataAnalyseForm.statr_date }" onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);" onchange="rep_dateValue(this);" readonly="readonly"> 
                            &nbsp;<bean:message key="label.to"/>
                            <!--<html:text name="dataAnalyseForm" property="end_date_patch" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" /> -->
                            <input type="text" name="end_date_patch" extra="editor" dataType="simpledate" class="inputtext" style="width:100px;font-size:10pt;text-align:left"  size="10" value="${dataAnalyseForm.end_date_patch }" onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);" onchange="rep_dateValue(this);" readonly="readonly"> 
                          </td>  
                          <td height="25"> 
                           <html:radio name="dataAnalyseForm" property="class_flag"  value="0"/>
                           <bean:message key="kq.repair.up_down"/>
                           <html:radio name="dataAnalyseForm" property="class_flag" value="1"/>
                           <bean:message key="kq.repair.up"/>
                           <html:radio name="dataAnalyseForm" property="class_flag" value="2"/>
                           <bean:message key="kq.repair.down"/>
                          </td>                   
                          </tr>                         
                        </table>
    	  			</td>
    	  		</tr>
    	  		<tr align="center"> 
    	  			<td>
	                	<hr width="100%">
    	  			</td>
                </tr>
                <tr id="tr2">                     
                      <td> 
                        <table width="100%"  border="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                          <tr> 
                          <td height="25" width="100"> 
                           <html:radio name="dataAnalyseForm" property="repair_flag" value="1"/>&nbsp;多天
                          </td> 
                          <td width="300"> 
                           <table border="0" cellspacing="0" cellpadding="0">
   	                       <tr>
   	                        <td valign="middle"> 
                                <bean:message key="kq.repair.cycle.time"/>&nbsp;
                            </td>                                
                            <td align="center">
                                <table border="0" cellspacing="0" cellpadding="0">
		                            <tr>
          		                   <td>
          		                      &nbsp;<!--<html:text name="dataAnalyseForm" property="cycle_date" styleClass="text5" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" readonly="true"/>-->
          		                        <input type="text"  extra="editor" dataType="simpledate" name="cycle_date" class="inputtext" style="width:100px;font-size:10pt;text-align:left" size="10" value="${dataAnalyseForm.cycle_date }" onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);" onchange="rep_dateValue(this);" readonly="readonly">
          		                   &nbsp;
          		                   </td>
                                 <td><div id="cycle_time"></div></td>
          		                  </tr>
          		                  </table>
                            </td>
                          </tr>
                          </table>                             
                        </td>  
                        <td align="left">                           
                          <table border="0" cellspacing="0" cellpadding="0">
                          <tr>
   	                        <td valign="middle" width="45%"><bean:message key="kq.repair.cycle.day"/>&nbsp;&nbsp;
   	                        </td>
   	                        <td align="left">
   	                          <div id="cycle_num"></div> 	          
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
    	  <div id="easy" style="padding:5px;border-color:#C4D8EE;border-width:1px;border-style:solid;height:70px;width:100%;" class="common_border_color">
    	    <table>
    	      <tr>
    	      	<td>
    	   		  <table border="0" cellspacing="0" cellpadding="0">
    	   		  	<tr>
    	   		  	  <td  valign="middle">
    	   		  	  	 补刷时间&nbsp;
    	   		  	  </td>
    	   		  	  <td align="center">
    	   		  	     <table border="0" cellspacing="0" cellpadding="0">
    	   		  	        <tr>
    	   		  	           <td>
    	   		  	             &nbsp;<!--<html:text name="dataAnalyseForm" property="jddate" styleClass="text5" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" readonly="true"/>  -->
		                   		 <input type=text value="${dataAnalyseForm.jddate }" extra="editor" dataType="simpledate" name="jddate" class="inputtext" style="width:100px;font-size:10pt;text-align:left" size="10" onclick="getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);" onchange="rep_dateValue(this);" readonly="readonly" >
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
						                       <html:select name="dataAnalyseForm" property="into_flag" size="1" >
						                       <html:option value="0">不限</html:option>
						                       <html:option value="-1">出</html:option>                      
						                       <html:option value="1">进</html:option>
						                       </html:select> 
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
                                        <logic:equal name="dataAnalyseForm" property="card_causation" value="">
				                          <html:textarea cols="45" rows="4" name="dataAnalyseForm"  property="causation">              
				                          </html:textarea> 
				                        </logic:equal>
				                        <logic:notEqual name="dataAnalyseForm" property="card_causation" value="">
				                          <html:text name="dataAnalyseForm" property='causation' size="20"   readonly="true"/> 
				                          <input type="hidden" name="causation_hidden" value="1"> 
				                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogText("${dataAnalyseForm.card_causation}","causation","causation_hidden");'/>
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
        <tr>
    
    <tr>
      <td align="center" nowrap style="height:35px;">
        <input type="button" name="b_delete" value='<bean:message key="label.query.selectall"/>' class="mybutton" onclick="selAll();">  
        <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="repairCard();" class="mybutton">  
        <input type="button" name="b_next" value="<bean:message key="button.return"/>" onclick="history.back();" class="mybutton">	      	       
      </td>
    </tr>
</table>

<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在处理....</td>
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
 <div id='axc' style='display:none'/>
 
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
    if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
      return;
 }
 InitAx();

 MusterInitData();
</script>
</html:form>
<script language="javascript">
setRepair("0");
//focusTr('1');

createTimeField('easy_time_field', 'easy_time');
createTimeField('cycle_time_field', 'cycle_time');

var cycleNumField = Ext.create("Ext.form.field.Number",{
        id : 'cycle_num_field',
        label: '',
        width : 70,
        value: 1,
        minValue: 1,
        maxValue: 999,
        renderTo: 'cycle_num'
   });

</script>

