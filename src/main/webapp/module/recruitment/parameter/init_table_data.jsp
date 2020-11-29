<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm" %>
<html>
<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link> 
<script language="javascript" src="/ajax/constant.js"></script>
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
<script language="javascript" src="/js/constant.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/components/extWidget/field/DateTimeField.js"></script>
 
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />

<% int tablesize=0; 
   int setsize=0;
try{
	ParameterForm parameterForm2=(ParameterForm)session.getAttribute("parameterForm2");
	if(parameterForm2.getTableListSize()==null){
		tablesize=0;
	}else
		tablesize=Integer.parseInt(parameterForm2.getTableListSize());
	if(parameterForm2.getFieldSetListSize()==null)
		setsize=0;
	else
		setsize=Integer.parseInt(parameterForm2.getFieldSetListSize());
%>
<script type="text/javascript">
//<!--
function allSelect(obj)
{
   var value=obj.checked;
   var table=document.getElementsByName("tables");
   var set=document.getElementsByName("setids");
   if(obj.id=="setAll"){
	   if(table)
	   {
	      for(var i=0;i<table.length;i++)
	      {
	           if(value)
	           {
	              table[i].checked=true;
	           }
	           else
	           {
	              table[i].checked=false;
	           }
	      }
	  }
	}
	if(obj.id=="resumeAll"){
	  if(set)
	  {
	      for(var i=0;i<set.length;i++)
	      {
	           if(value)
	           {
	              set[i].checked=true;
	           }
	           else
	           {
	              set[i].checked=false;
	           }
	      }
	  }
	}
}
function visibleTime(type)
{
   if(type=='0')
   {
	   Ext.getCmp('sscop').setReadOnly(false);
	   Ext.getCmp('escop').setReadOnly(false);
   }
   else
   {
	   Ext.getCmp('sscop').setReadOnly(true);
	   Ext.getCmp('escop').setReadOnly(true);
   }
}
function sub()
{
	Ext.Msg.confirm("提示信息",CONFIRM_INIT_WILL_DELETE_ALLDATA+"！",function(btn){
	   if(btn=="yes")
	   {
	   var value1=document.getElementById("setAll").checked;
	   var value2=document.getElementById("resumeAll").checked;
	   var value = value1&&value2;
	   var table=document.getElementsByName("tables");
	   var set=document.getElementsByName("setids");
	   var  tableStr="";
	   var setStr="";
	   var tnum=0;
	   var snum=0;
	   if(table)
	   {
	      for(var i=0;i<table.length;i++)
	      {
	           if(value1)
	           {
	              tableStr+="/"+table[i].value;
	           }
	           else
	           {
	              if(table[i].checked)
	              {
	                 tableStr+="/"+table[i].value;
	              }
	           }
	      }
	  }
	  if(set)
	  {
	      for(var i=0;i<set.length;i++)
	      {
	           if(value2)
	           {
	              setStr+="/"+set[i].value;
	           }
	           else
	           {
	              if(set[i].checked)
	              {
	                 setStr+="/"+set[i].value;
	              }
	           }
	      }
	  }
	  if(tableStr==''&&setStr=="")
	  {
		  Ext.showAlert(SELECT_TABLE_TO_INIT+"!");
	      return;
	  }
	  var zero=document.getElementById("zero");
	  var stime=Ext.getCmp('sscop').getValue();
	  var etime=Ext.getCmp('escop').getValue();
	  var type="1";
	  if(zero.checked)
	    type="0";
	   if(type=='1'&&trim(stime).length<=0)
	   {
		   Ext.showAlert(SELECT_START_TIME+"！");
	       return;
	   }
	   
	   if(type=='1'&&trim(etime).length<=0)
	   {
	       Ext.showAlert(SELECT_END_TIME+"！");
	       return;
	   }
	   if(type=='1')
	   {
	        var reg = /^(\d{4})((-|\.)(\d{1,2}))((-|\.)(\d{1,2}))$/;;
			if(!reg.test(stime))
			{
				Ext.showAlert(STARTTIME_FORMAT+"！");
				return;
			}
			if(!reg.test(etime))
			{
				Ext.showAlert(ENDTIME_FORMAT+"！");
				return;
			}
			var syear = stime.substring(0,4);
			var smonth=stime.substring(5,7);
			var sday=stime.substring(8);
			//if(!isValidDate(sday,smonth,syear))
			//{
			 //  alert(tableStr);
			 //  alert("起始时间的时间范围不正确,请注意年，月，日的有效性！");
			 //  return;
			//}
			var eyear = etime.substring(0,4);
			var emonth=etime.substring(5,7);
			var eday=etime.substring(8);
			//if(!isValidDate(eday, emonth, eyear))
			//{
			  // alert("结束时间的时间范围不正确,请注意年，月，日的有效性！");
			   //return;
			//}
			if(syear>eyear||(syear==eyear&&smonth>emonth)||(syear==eyear&&smonth==emonth&&sday>eday))
			{
				Ext.showAlert(ENDTIME_LARGER_STARTTIME+"！");
			    return;
			}
	   }
	   var hashVo=new ParameterSet();
	   hashVo.setValue("isAllDelete",value?"1":"0");
	   hashVo.setValue("type",type);
	   hashVo.setValue("tableStr",tableStr);
	   hashVo.setValue("setStr",setStr);
	   hashVo.setValue("stime",stime);
	   hashVo.setValue("etime",etime);
	   var request=new Request({method:'post',asynchronous:false,onSuccess:sub_ok,functionId:'ZP0000002369'},hashVo);			
	   }
	});
}
function sub_ok(outparameters)
{
  var msg = outparameters.getValue("msg");
  if(msg=='0')
  {
     Ext.Msg.alert("提示信息",DATA_INIT_SUCCESS+"！");
     return;
  }
  else
  {
	  Ext.Msg.alert("提示信息",DATA_INIT_FALITRUE);
     return;
  }
}
function checkSelect(Object, flag){
	//初始化职位信息时同时删除这些信息
	if(Object.value.toUpperCase()=="Z03"){
		//应聘信息表
		document.getElementById("ZP_POS_TACHE").checked=Object.checked;
		//考生管理表
		document.getElementById("Z63").checked=Object.checked;
		//收藏职位表
		document.getElementById("ZP_POS_COLLECTION").checked=Object.checked;
	}
	
	if(!Object.checked){
		if("set"===flag)
		    document.getElementById("setAll").checked=false;
		else if("resume"===flag)
		    document.getElementById("resumeAll").checked=false;
	}
}

//-->
</script>
<body>
<div id="lcbase_div" class="hj-wzm-xq-all" style="display: none;OVERFLOW:hidden;">
<div style="width:90%;">
<html:form action="/hire/parameterSet/configureParameter/init_table_data">
  <table width="100%"  height='100%'   align="center"  id="tabl"> 
	<tr> <td  valign="top"  align='left' >
		<div class="hj-zm-lc-one" style="width:90%;float:left">
			<h2>
				<bean:message key="kq.init.select"/>
			</h2>
		<table width="100%" border="0" cellspacing="10"  align="center" cellpadding="0" class="DetailTable">
		<tr>
		<html:hidden name="parameterForm2" property="tableNames"/>
		<td align="center" nowrap>
		
		<div align="left" style="width:90%;height:100%;margin-top:15px">
		<span><input type="checkbox" id="setAll" onclick="allSelect(this)"/><bean:message key="hire.data.table"/></span><hr style="border:none;border-top:1px #c5c5c5 solid;heigth:1px;line-height:80%" size="1px" noshade="noshade">
		<div style="overflow:auto;width:380px;height:120px;float:left;" >
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				<% int i=0; %>
				<logic:iterate id="element" name="parameterForm2" property="tableList" indexId="index">
				<% if(i==0&&i%2==0) {%>
				  <tr>
				<%} %>
				<td width="30%"><input type="checkbox" name="tables" id="<bean:write name="element" property="table"/>" value="<bean:write name="element" property="table"/>" onclick="checkSelect(this, 'set');" />&nbsp;
				<bean:write name="element" property="tablename"/></td>
				
				<% if((i+1)%2==0||i==(tablesize-1)){%>
				</tr>
				<%} %>
				<% i++; %>
				</logic:iterate>
				</table>
			</div>
		</div>
		</td>
		</tr>
		<tr align="center">
		<td valign="top"  align='center'>
		<div align="left" style="width:90%;heigth:100%;">
		<span><input type="checkbox" id="resumeAll" onclick="allSelect(this)"/><bean:message key="hire.a01oth.set"/></span><hr style="border:none;border-top:1px #c5c5c5 solid;heigth:1px;line-height:80%" size="1px" noshade="noshade">
		<div style="overflow:auto;width:380px;height:auto;float:left" >
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				<% int j=0; %>
				<logic:iterate id="element" name="parameterForm2" property="fieldSetList" indexId="index">
				<% if(j==0&&j%2==0) {%>
				  <tr>
				<%} %>
				<td width="30%"><input type="checkbox" name="setids" id="<bean:write name="element" property="table"/>" value="<bean:write name="element" property="setid"/>" onclick="checkSelect(this, 'resume');" />&nbsp;
				<bean:write name="element" property="setdesc"/></td>
				
				<% if((j+1)%2==0||j==(setsize-1)){%>
				</tr>
				<%} %>
				<% j++; %>
				</logic:iterate>
				</table>
				</div>
		</div>
		</td>
		</tr>
</table>
</div>
</td>
</tr>
<tr height="100%">
	<td align="left" >
		<div class="hj-zm-lc-one" style="width:90%;heigth:100%">
			<h2>
				请设置删除表的数据范围
			</h2>
			<div style="overflow:hidden;width:760px;height:100%;float:left">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-left: 7%;">
					<tr>
						<td align="left">
						 <input type="radio" id="zero" name="timetype" value="0" onclick="visibleTime('1');" checked /><bean:message key="kq.init.allc"/>
						</td>
					</tr>
					<br/>
					<tr height="50px">
						<td align="left">
						 <span style="vertical-align:middle;"><input type="radio" id="one" name="timetype" value="1" onclick="visibleTime('0');"><bean:message key="kq.init.tscope"/></span>
						</td>
						<td>
						 <div id="datatime" width="100%" height="100%"></div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	</td>
</tr>
</table>
<div class="hj-zm-lc-one" style="width:90%;heigth:100%;margin-top:5px;" id="tabl1">
	<font color="red"><bean:message key="hire.init.warn"/>&nbsp;&nbsp;</font>
</div>
</html:form>
</div>
</div>
</body>
<script type="text/javascript">
function isIE() { //ie?  
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 }

Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
 	var Panel = new Ext.Panel({      
 		xtype:'panel',
		id:'view_panel',
		title:'<div style="float:left"><bean:message key="hire.data.init"/></div><div style="float: right;margin-right: 5%" align="center"><a href="javascript:void(0);" style="margin-right: 2%" onclick="sub();">确定</a></div>',
	  	html:"<div id='topPanel'></div>",
	  	region:'center',
	  	border:false
	});
 	new Ext.Viewport({
    	layout:'border',
    	title:'创建流程',
        padding:"0 5 0 5",
        renderTo: Ext.get('panel'),
     	style:'backgroundColor:white',
        items:[Panel]
         
     });
     
 	document.getElementById('topPanel').appendChild(document.getElementById('lcbase_div'));
 	document.getElementById('lcbase_div').style.display="block";
 	
 	var view_panel = Ext.getCmp('view_panel');
    view_panel.setAutoScroll(true);
    var winHeight =parent.document.body.clientHeight;
    view_panel.setHeight(winHeight);
    if(isIE()){
    	Ext.getDom('tabl').style.marginLeft="10%";
    	Ext.getDom('tabl1').style.marginLeft="10%";
     }
    else{
    	Ext.getDom('tabl').style.marginLeft="5%";
    	Ext.getDom('tabl1').style.marginLeft="5%";
    }

    Ext.create('Ext.form.Panel', {
        renderTo: 'datatime',
        width: 600,
        border:0,
        layout:{type:'hbox'},
        items: [{xtype:'container',layout:{type:'hbox'},items:[{xtype:'label',html:'从',padding:4},{
            xtype: 'datetimefield',
	        id:'sscop',
            labelAlign : 'right',
            labelSeparator : "",
            name: 'stime1',
            format:'Y-m-d'
        }]}, {xtype:'container',layout:{type:'hbox'},items:[{xtype:'label',html:'至',padding:4},{
            xtype: 'datetimefield',
	        id:'escop',
            labelAlign : 'right',
            labelSeparator : "",
            name: 'etime1',
            format:'Y-m-d'
        }]}]
    });
});
</script>
</html>
<%}catch(Exception e){e.printStackTrace();}%>