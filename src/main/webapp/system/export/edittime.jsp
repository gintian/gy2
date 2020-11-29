<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>
<html>
	<head>
		
		<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	String aa =  SafeCode.decode(request.getParameter("info2"));
%>
<title>
		用户名：<%=userName%>　当前日期：<%=date%>
		</title>
<style type="text/css">

.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 60px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
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
	/*浏览器兼容 input显示问题修改 wangbs 20190319*/
	width: 17px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
.table_edit{
	background:#FFFFFF; 
	border-collapse:collapse; 
	font-size:12px;
	border:1px solid #C4D8EE;
}
ul{list-style:none;} 
.ListTable {
	border:1px solid #8EC2E6;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    margin-top:5px;
}
</style>
<script language="javascript" src="/ajax/constant.js"></script>
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
<script language="javascript" src="/system/export/edittime.js"></script>
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


<script language="JavaScript">
var temp;
if(parent.Ext){
    var operateTarget = parent.Ext.getCmp("dateSetWin");
    if(operateTarget){
        temp = operateTarget.arr;
	}else{
        temp=dialogArguments;
	}
}else{
    temp=dialogArguments;
}

var trigger=temp[0]? temp[0]:0; // bug 32986 wangb 20180112 add 后台作业 作业时间没有配置 默认显示简单规则类型
var aa = temp[1]? temp[1]:""; 
var openFrequency=1;
var dayFrequency=1;
var hasendtime=2;
var openmoth=1;
var d = new Date();
var years = d.getYear();
var month = add_zero(d.getMonth()+1);
var days = add_zero(d.getDate());
function add_zero(temp)
{
 if(temp<10) return "0"+temp;
 else return temp;
}


function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}
function showMenu(flag){
	var days=document.getElementsByName("days");
	var value='';
	for(var i=0;i<days.length;i++){
		if(days[i].checked==true){
			value=days[i].value;
			document.getElementById('cont'+value).style.display='block';
			openFrequency=days[i].value;
		}else{
			document.getElementById('cont'+days[i].value).style.display='none';
		}
	}
}
function select_month(flag,name){
	
	var tt=document.getElementsByName(name);
	
	if(flag==1){
		for(var i=0;i<tt.length;i++){
			if(tt[i].checked==true){
				openmoth=tt[i].value;
				var aa=document.getElementById('cont_3_'+tt[i].value+'_month1'); 
				aa.disabled=false;	
				var aa=document.getElementById('cont_3_'+tt[i].value+'_month2'); 
				aa.disabled=false;	
				if(document.getElementById('cont_3_'+tt[i].value+'_month3')){
					var aa=document.getElementById('cont_3_'+tt[i].value+'_month3'); 
					aa.disabled=false;	
				}
			}else{
				var aa=document.getElementById('cont_3_'+tt[i].value+'_month1'); 
				aa.disabled=true;
				var aa=document.getElementById('cont_3_'+tt[i].value+'_month2'); 
				aa.disabled=true;	
				if(document.getElementById('cont_3_'+tt[i].value+'_month3')){
					var aa=document.getElementById('cont_3_'+tt[i].value+'_month3'); 
					aa.disabled=true;	
				}
				
			}
		}
	}
	if(flag==2){
		for(var i=0;i<tt.length;i++){
		
			if(tt[i].checked==true){
				var aa=document.getElementsByName('pl_'+tt[i].value); 
				dayFrequency=tt[i].value;
				for(var k=0;k<aa.length;k++){
					aa[k].disabled=false;
				}
			}else{
				var aa=document.getElementsByName('pl_'+tt[i].value); 
				for(var k=0;k<aa.length;k++){
					aa[k].disabled=true;
				} 
			}
		}
	}
	if(flag==3){
		for(var i=0;i<tt.length;i++){
			
			if(tt[i].checked==true){
				hasendtime=tt[i].value
				if(tt[i].value==1){				
					var aa=document.getElementById('last_end'); 
					aa.disabled=false;
					
				}
			}else{
				if(tt[i].value==1){
					var aa=document.getElementById('last_end'); 
					aa.disabled=true;
					
				}
				
			}
		}
	}
	if(flag==4){
		tt=document.getElementById('select_content');
		var options=tt.options;
		for(var i=0;i<options.length;i++){
			if(options[i].selected){
				document.getElementById(name+options[i].value).style.display='block';
			}else{
				document.getElementById(name+options[i].value).style.display='none';
			}
		}		
		
	}
}
function selectedit(){
	 var tt=document.getElementById('select_content');
	   var options=tt.options;
		for(var i=0;i<options.length;i++){
			if(options[i].selected==true){
				if(options[i].value=='1'){
					edit();
				}else{
					fzEdit();
				}
			}
		}
}
function fzEdit(){
	var time_mm=new Array();
	var openday;
	var openweek;
	var openweekdays='';
	var openmonthday;
	var openmonthnum;
	var openmonth_week_num;
	var openmonth_week_day;
	var openmonth_week_month
	
	var daytime;
	var daycycle;
	var daycycletype;
	var daycyclesttime;
	var daycycleendtime;
	
	var lasttimebegin;
	var endtime;
	var tem=document.getElementsByName('days');
	select_month(2,"pl");
	if(openFrequency!=null){
		if(openFrequency==1){
			openday=document.getElementById('every_cont_1_days').value;
			if(openday>0){
			
			}
		}
		if(openFrequency==2){
			openweek=document.getElementById('every_cont_2_week').value;
			if(openweek.substring(0,1)==0){
				openweek=openweek.substring(1);
			}
			var tee=document.getElementsByName("cont_weeks");
			for(var i=0;i<tee.length;i++){
				if(tee[i].checked){
					openweekdays+=","+tee[i].value;
				}
			}
			if(openweekdays!=null&&openweekdays.length>0){
				openweekdays=openweekdays.substring(1);
			}
		}
		if(openFrequency==3){
			if(openmoth==null||openmoth==1){
			    //浏览器兼容 不能name当id用了 wangbs 20190319
				openmonthday=document.getElementById('every_cont_3_1_month_day').value;
				if(openmonthday.substring(0,1)==0){
					openmonthday=openmonthday.substring(1);
				}
				openmonthnum=document.getElementById('every_cont_3_1_month_day_month').value;
				if(openmonthnum.substring(0,1)==0){
					openmonthnum=openmonthnum.substring(1);
				}
			}
			if(openmoth!=null&&openmoth==2){
				openmonth_week_num=document.getElementById('cont_3_2_month_num').value;
				openmonth_week_day=document.getElementById('cont_3_2_month_weekday').value;
				openmonth_week_month=document.getElementById('every_cont_3_2_month_months').value;
				if(openmonth_week_month.substring(0,1)==0){
					openmonth_week_month=openmonth_week_month.substring(1);
				}
			}
		}
	}
	if(dayFrequency!=null){
		if(dayFrequency==1){
			daytime=document.getElementById('every_pl_1_h').value+":"+document.getElementById('every_pl_1_min').value+":"+document.getElementById('every_pl_1_mm').value;
		}
		if(dayFrequency==2){
			daycycle=document.getElementById('every_pl_2_num').value;
			daycycle=substr(daycycle);
			daycycletype=document.getElementById('pl_2_type').value;
			daycyclesttime=document.getElementById('every_pl_2_began_h').value+":"+document.getElementById('every_pl_2_began_min').value+":"+document.getElementById('every_pl_2_began_mm').value;
			daycycleendtime=document.getElementById('every_pl_2_end_h').value+":"+document.getElementById('every_pl_2_end_min').value+":"+document.getElementById('every_pl_2_end_mm').value;
		}
	}
	lasttimebegin=document.getElementById('last_begin').value;
	if(hasendtime==null||hasendtime==1){
		endtime=document.getElementById('last_end').value;
	}
	var te=new Array();
	var ta;
	var time=new Array();
	var flag=false;
	if(lasttimebegin!=null&&lasttimebegin.length>0){
		ta=lasttimebegin.split('-');
		if(ta[1].substring(0,1)==0){
			ta[1]=ta[1].substring(1);
		}
		if(ta[2].substring(0,1)==0){
			ta[2]=ta[2].substring(1);
		}
		if(hasendtime!=null&&hasendtime==2){
			time[6]='*';
			time[4]=ta[1];			
			time[3]=ta[2];
		}
		if(hasendtime==null||hasendtime==1){
			if(endtime!=null&&endtime.length>0){
				te=endtime.split('-');
				if(te[1].substring(0,1)==0){
					te[1]=te[1].substring(1);
				}
				if(te[2].substring(0,1)==0){
					te[2]=te[2].substring(1);
				}
				if(eval(te[0])<eval(ta[0])){
					alert("请设置持续时间的截止日期大于起始日期！1");
					return;
				}
				if(te[0]>=ta[0]){
					if(te[0]==ta[0]){
						if(eval(te[1])<eval(ta[1])){
							alert("请设置持续时间的截止日期大于起始日期！2");
							return;
						}else{
							if(te[1]==ta[1]){
								time[4]=te[1];
								if(eval(te[2])<eval(ta[2])){
									alert("请设置持续时间的截止日期大于起始日期！3");
									return;
								}else{
									if(te[2]==ta[2]){
										time[3]=te[2];
										flag=true;
									}else{
										time[3]=ta[2]+'-'+te[2];
									}
								}
							}else{
								time[4]=ta[1]+'-'+te[1];
								time[3]='*'
							}
						}
						time[6]=ta[0];
					}else{
						time[6]=ta[0]+"-"+te[0];
						time[4]=ta[1];
						time[3]=ta[2];
					}
				}
			}else{
				time[6]='';
				
				time[4]=ta[1];
				time[3]=ta[2];
			}
		} else {
			if(openFrequency==null||openFrequency==1){ 
				time[4]="*";
			}
		}
	}
	

	
	if(openFrequency==null||openFrequency==1){
		if(openday!=null&&openday.length>0){
			if(openday.substring(0,1)==0){
				openday=openday.substring(1);
			}
			if(openday>1){
				if(flag){
					alert("结束日期于发生频率设置相冲突,请重新考虑时间规则！");
					return;
				}else{
					time[3]=time[3]+"/"+openday;
				}
			}else{
				if(flag){
				
				}else{
					if(time[3]==null||time[3].length==0)
						time[3]='*';
					else{
					
					}
				}
			}
		
		}
		time[5]='?';
		if(time[4]!=null&&time[4].length!=0){
		
		}else{
			time[4]='*';
		}
		
		if(hasendtime!=null&&hasendtime==2){
			if(openday<=1)
				time[3]='*';
		}
	}
	if(openFrequency!=null&&openFrequency==2){
		var tem;
		if(openweek!=null&&openweek.length!=0){
			tem=openweek;
			if(openweek==0){
				tem=1;
			}
		}else{
			tem=1;
		}
		if(openweekdays!=null&&openweekdays.length>0){
			if(tem==1){
				time[5]=openweekdays;
			}else{
				time[5]=openweekdays+"/"+tem;
			}
		}
		time[3]='?';
		time[4]='*';
	}
	if(openFrequency!=null&&openFrequency==3){
		time[5]='?';
		if(openmoth==null||openmoth==1){
			openmonthnum=substr(openmonthnum);
			if(openmonthnum>1){
				time[4]=time[4]+"/"+openmonthnum;
			}else{
				time[4]='*';
			}
			openmonthday=substr(openmonthday);
			time[3]=openmonthday;
		}
		if(openmoth!=null&&openmoth==2){
			if(openmonth_week_num!=null){
				if(openmonth_week_day!=null){
					if(openmonth_week_day<8){
						if(openmonth_week_num=='L')
							time[5]=openmonth_week_day+openmonth_week_num;
						else{
							time[5]=openmonth_week_day+"#"+openmonth_week_num;
						}
					}
					if(openmonth_week_day==8){
						time[3]=openmonth_week_num;
					}
					if(openmonth_week_day==9){
						time[3]=openmonth_week_num+'W';
					}
					if(openmonth_week_day==10){
						if(openmonth_week_num=='L'){
							//复杂规则 按月时，选择最后一周时， 工作日规则不对
							time[5]="1L|7L";
						}else{
							time[5]="1#"+openmonth_week_num+",7#"+openmonth_week_num;
						}
					}
					if(openmonth_week_day!=8&&openmonth_week_day!=9){
						time[3]='?';
					}
				}
			}
			if(openmonth_week_month!=null){
				if(openmonth_week_month>1){
					//复杂规则 按月时， 间隔x个月，值不对
					time[4]= "/"+openmonth_week_month;//time[4]+"/"+openmonth_week_month;
				}else{
					time[4]="*";
				}
			}
		}
	
	}
	if(dayFrequency==null||dayFrequency==1){
		if(daytime!=null){
			var tem=daytime.split(":");
			tem[0]=substr(tem[0]);
			if(tem[1].substring(0,1)==0){
					tem[1]=tem[1].substring(1);
			}
			if(tem[2].substring(0,1)==0){
					tem[2]=tem[2].substring(1);
			}
			time[2]=tem[0];
			time[1]=tem[1];
			time[0]=tem[2];
		}
	}
	if(dayFrequency!=null&&dayFrequency==2){
		var tr=daycyclesttime.split(":");
		tr[0]=substr(tr[0]);
		tr[1]=substr(tr[1]);
		tr[2]=substr(tr[2]);
		var tt=daycycleendtime.split(":");
		tt[0]=substr(tt[0]);
		tt[1]=substr(tt[1]);
		tt[2]=substr(tt[2]);
		if(parseInt(tt[0])<parseInt(tr[0])){
			alert("请设置每日频率的截止时间大于起始时间！");
			return;
		}else{
			if(parseInt(tt[0])==parseInt(tr[0])){
				if(daycycletype==2){
					time[0]=tr[2];
					time[1]=tr[1];
					time[2]=tr[0];
				}else{
					if(parseInt(tt[1])<parseInt(tr[1])){
						alert("请设置每日频率的截止时间大于起始时间！");
						return;
					}else{
						if(parseInt(tt[1])==parseInt(tr[1])){
							time[0]=tr[2];
							time[1]=tr[1];
							time[2]=tr[0];
						}else{
							time[0]=tr[2];
							time[1]=tr[1]+"-"+tt[1];
							//if(daycycle>1){
								time[1]=time[1]+"/"+daycycle;
							//}	
							time[2]=tr[0];
						}
					}
				}
			}else{
				if(daycycletype==2){
					time[0]=tr[2];
					time[1]=tr[1];
					time[2]=tr[0]+"-"+tt[0];
					//if(daycycle>1){
						time[2]=time[2]+"/"+daycycle;
					//}
				}else{
					time[0]=tr[2];
					time[1]=tr[1];
					//if(daycycle>1){
						time[1]=time[1]+"/"+daycycle;
					//}
					time[2]=tr[0]+"-"+tt[0];
				}
			}
		}
	}
	var timew='';
	for(var i=0;i<time.length;i++){
		timew+=time[i]+" ";
	}
	var info=new Array();
	info[1]=timew;
	info[0]='1';

	//兼容浏览器 wanbgs 20190319
	winClose(info);
	// window.returnValue=info;
	// window.close();
}
function substr(str){
	if(str.substring(0,1)==0){
		str=str.substring(1);
	}
	return str;
}
//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题

//function document.oncontextmenu() 
//{ 
//  	return false; 
//} 
function edit()
{
	var	issuedate = " ";
	var enddate =" ";
	var str = $('issuedate');
	var str2 = $('enddate');
	if((str!=null&&str.value!=""))
  	{
		if(validate(str)){
			issuedate = str.value;
		}else{
			return;
		}
  	}
  	if((str2!=null&&str2.value!=""))
  	{
		if(validate(str2)){
			enddate = str2.value;
		}else{
			return;
		}
  	} 
  	var start_h = document.getElementById("start_h");
  	var start_m = document.getElementById("start_m");
  	var start_mm = document.getElementById("start_mm");
  	var h="",m="",mm="",h2="",m2="",mm2="";
  	if(start_h.value!="")
  		h=start_h.value;
  	if(start_m.value!="")
  		m=start_m.value;
  	if(start_mm.value!="")
  		mm=start_mm.value;

  	var start_h2 = document.getElementById("start_h2");
  	var start_m2 = document.getElementById("start_m2");
  	var start_mm2 = document.getElementById("start_mm2");
  	if(start_h2.value!="")
  		h2=start_h2.value;
  	if(start_m2.value!="")
  		m2=start_m2.value;
  	if(start_mm2.value!="")
  		mm2=start_mm2.value;
  	
	var state = document.exportForm.state.value;
	if(state==null||state=="")
	{
		alert("请输入循环次数");
		document.exportForm.state.focus();
		return;
	}	
	if(!IsInteger(state))
		return;
	var times = document.exportForm.times.value;
	if(times==null||times=="")
	{
		alert("请输入间隔时间");
		document.exportForm.times.focus();
		return;
	}	
	var info=new Array();
	if(!IsInteger(times))
		return;
	if(issuedate==" "&&enddate==" "){
		info[1] = issuedate+"|"+enddate+"|"+state+"|"+times;
	}
	else if(issuedate==" "&&enddate!=" "){
		info[1] = issuedate+"|"+enddate+" "+h2+":"+m2+":"+mm2+"|"+state+"|"+times;
	}
	else if(issuedate!=" "&&enddate==" "){
		info[1] = issuedate+" "+h+":"+m+":"+mm+"|"+enddate+"|"+state+"|"+times;
	}
	else{
		info[1] = issuedate+" "+h+":"+m+":"+mm+"|"+enddate+" "+h2+":"+m2+":"+mm2+"|"+state+"|"+times;
	}
	info[0]='0';
	//浏览器兼容 wangbs 20190319
	winClose(info);
	// returnValue=info;
	// window.close();
}
function winClose(info) {
    if(parent.Ext){
        var operateTarget = parent.Ext.getCmp("dateSetWin");
        if(operateTarget){
            operateTarget.return_vo = info?info:"";
            operateTarget.close();
        }
    }
}
function IsInteger(str)      
{        
    if(str.length!=0){     
        reg=/^[-+]?\d*$/;      
        if(!reg.test(str)){     
            alert("请输入正整数类型!");//请将“整数类型”要换成你要验证的那个属性名称！     
            return false;
        }     
    }
    return true;     
}
function IsDigit(obj) {
	if((event.keyCode >= 46) && (event.keyCode <= 57)){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
		
		if((values.lastIndexOf(".")<values.length)&&(values.indexOf(".")!=1))
			return true;
		else
			return false;
	}else{
		return false;
	}
}   
   this.fObj = null;
   var time_r=0; 
   function setFocusObj(obj,time_vv) 
   {		
	this.fObj = obj;
	time_r=time_vv;		
   }
   function IsInputTimeValue() 
   {
       event.cancelBubble = true;
       var fObj=this.fObj;		
       if (!fObj)
       		 return;	
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.length<=0)
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
    function testTime(theObj,type)
    {
    	if(!IsInteger(theObj.value))
    	{
    		theObj.select();
    		theObj.focus();
    		return;
    	}
		if(type=='h')
		if(parseInt(theObj.value)<0 || parseInt(theObj.value)>23) 
    	{    
			alert("小时应该是0到23的整数!");
 		    theObj.select();
    		theObj.focus(); 
    	}
    	if(type=='m')
		if(parseInt(theObj.value)<0 || parseInt(theObj.value)>59) 
    	{    
			alert("分钟应该是0到59的整数!");
 		    theObj.select();
    		theObj.focus(); 
    	}
    	if(type=='mm')
		if(parseInt(theObj.value)<0 || parseInt(theObj.value)>59) 
    	{    
			alert("秒应该是0到59的整数!");
 		    theObj.select();
    		theObj.focus(); 
    	}
    	if(type=='D')
		if(parseInt(theObj.value)<0 || parseInt(theObj.value)>31) 
    	{    
			alert("天应该是0到31的整数!");
 		    theObj.select();
    		theObj.focus(); 
    	}
    	if(type=='w')
		if(parseInt(theObj.value)<0 || parseInt(theObj.value)>31) 
    	{    
			alert("周应该是0到52的整数!");
 		    theObj.select();
    		theObj.focus(); 
    	}
    	if(type=='mon')
		if(parseInt(theObj.value)<0 || parseInt(theObj.value)>12) 
    	{    
			alert("月应该是1到12的整数!");
 		    theObj.select();
    		theObj.focus(); 
    	}
    }
	</script>
	<link href="<%=css_url%>" ref="stylesheet" type="text/css"/>
   <link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
	<hrms:themes></hrms:themes>
	</head>
	<body onKeyDown="return pf_ChangeFocus();" style='align:center'>
		<html:form action="/system/export/editclass_info">
			<table  height="90%" align="center" width="100%" class='ListTable'  cellpadding="0" cellspacing="0">
				<tr>  
	   				 <td valign="top" style="overflow:auto" align='center'>
	   	<table width="640"  align='center' class='ListTable'  >
			<tr>
				<td class='TableRow' width='100%'>
					作业规则
				 </td>
			</tr>
			<tr>
				<td width='100%' class="RecordRow">
					规则类型
					<select onchange='select_month(4,"content")' id='select_content'>
						<option value='1'>简单</option>
						<option value=2>复杂</option>
					</select>
				 </td>
			</tr>
			<tr>
				<td align='center' class="RecordRow">
				<div id='content1' style='display:block;'>
					<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
							<br>
							<TR height="10">
								<TD></TD>
							</TR>
							<TR>
								<TD width="25%" align="right" style="padding-right:10px;"><bean:message key="kq.strut.start"/></TD>
								<%--低版本可以name当id用 为兼容谷歌ie11 改为id属性 wangbs 20190319--%>
								<TD width="25%;"><input type="text" id="issuedate" extra="editor" value="" style="width:200px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate" class="text4"></TD>
								<td width="15%;" align="right">时分秒</td>
								<td width="35%" style="padding-left:5px;">
									<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
							             <tr>
							               <td width="40" nowrap style="background-color:#FFFFFF";> 
							                <div class="m_frameborder" nowrap>
							                 <input type="text" class="m_input" maxlength="2" name="intricacy_app_start_time_h" id="start_h" value="00" onblur="testTime(this,'h')" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="intricacy_app_start_time_m" id="start_m" value="00" onblur="testTime(this,'m')" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="intricacy_app_start_time_mm" id="start_mm" value="00" onblur="testTime(this,'mm')" onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);">
							               </div>
							             </td>
							             <td>
							             <table border="0" cellspacing="2" cellpadding="0">
											 <%--解决按钮刷新页面的问题 按钮默认type是submit wangbs 20190319--%>
											 <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
							                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
							             </table>
							            </td>
							            </tr>
							       </table> 
						       </td>
							</TR>
							<TR height="7">
								<TD></TD>
							</TR>
							<TR>
								<TD align="right" style="padding-right:10px;"><bean:message key="kq.strut.end"/></TD>
								<TD><input type="text" name="enddate" extra="editor" value="" style="width:200px;font-size:10pt;text-align:left" id="enddate"  dropDown="dropDownDate" class="text4"></TD>
								<td align="right">时分秒</td>
								<td width="65" style="padding-left:5px;">
									<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
							             <tr>
							               <td width="40" nowrap style="background-color:#FFFFFF";> 
							                <div class="m_frameborder" nowrap>
							                 <input type="text" class="m_input" maxlength="2" name="intricacy_app_start_time_h" id="start_h2" value="00" onblur="testTime(this,'h')"  onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="intricacy_app_start_time_m" id="start_m2" value="00" onfocus="setFocusObj(this,60);" onblur="testTime(this,'m')"  onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="intricacy_app_start_time_mm" id="start_mm2" value="00" onblur="testTime(this,'mm')"  onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);">
							               </div>
							             </td>
							             <td>
							             <table border="0" cellspacing="2" cellpadding="0">
											 <%--解决按钮刷新页面的问题 按钮默认type是submit wangbs 20190319--%>
							                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
							                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
							             </table>
							            </td>
							            </tr>
							       </table> 
						       </td>
							</TR>
							<TR height="7">
								<TD></TD>
							</TR>
							<TR>
								<TD align="right" style="padding-right:10px;">重复次数</TD>
								<%--低版本可以name当id用 为兼容谷歌ie11 改为id属性 wangbs 20190319--%>
								<TD><input type="text" id="state" value="" style="width:200px;font-size:10pt;text-align:left" class="text4"/></TD>
							</TR>
							<TR height="7">
								<TD></TD>
							</TR>
							<TR>
								<TD align="right" style="padding-right:10px;">间隔时间(分钟)</TD>
								<TD><input type="text" id="times" value="" style="width:200px;font-size:10pt;text-align:left" class="text4"/></TD>
							</TR>
							<TR height="200">
								<TD ></TD>
							</TR>
							
						</table>
					</div>
					<div id='content2' style='display:none;'>
					<table width="95%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
						<tr>
							<td width=20%>
								<fieldset style='height:113px;'>
									<legend>
										发生频率
									</legend>
									<table width='100%'>
									<tr>
									<td>
									<input type='radio' name=days value=1 onclick='showMenu(this)' checked>每天（D）
									 </td>
									</tr>
									
									<tr>
									<td>
										<input type='radio' name=days value=2 onclick='showMenu(this)'>每周（W）
									 </td>
									</tr>
									
									<tr>
									<td>
									<input type='radio' name=days value=3 onclick='showMenu(this)'>每月（M）
									 </td>
									</tr>									
									</table>
							
								</fieldset>
							 </td>
							<td width=70%>
								<div id="cont1">
									<fieldset style='height:113px;margin-left:5px;'>
										<legend>
											每天
										</legend>
										<table>
										
										<tr>
										<td valign='middle'>
											每隔
										 </td>
										<td valign='middle'>
											<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
									             <tr>
									               <td width="40" nowrap style="background-color:#FFFFFF";> 
									                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;">
									                 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;width:50px;" maxlength="2" name="cont_1_days" id="every_cont_1_days" value="1" onblur="testTime(this,'D')" onfocus="setFocusObj(this,32);" onkeypress="event.returnValue=IsDigit(this);">
									               </div>
									              </td>
									             <td>
									             <table border="0" cellspacing="2" cellpadding="0">
									                <tr><td><button type="button" id="cont_1_days_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
									                <tr><td><button type="button" id="cont_1_days_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
									             </table>
									             </td>
									            </tr>
									       </table>
									      
									        </td>
									       <td valign='middle'>
									       天
									        </td>
										</table>
									</fieldset>
								</div>
								<div id="cont2" style='display:none'>
									<fieldset style='height:113px;margin-left:5px;'>
										<legend>
											每周
										</legend>
										<table>
										
										<tr>
										<td align='right' width='30%'>
											每隔
										 </td>
										<td align='left' width='20%'>
											<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
									             <tr>
									               <td width="40" nowrap style="background-color:#FFFFFF";> 
									                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;">
									                 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;width:50px;" maxlength="2" name="cont_2_week" id="every_cont_2_week" value="1" onblur="testTime(this,'W')" onfocus="setFocusObj(this,53);" onkeypress="event.returnValue=IsDigit(this);">
									               </div>
									              </td>
									             <td>
									             <table border="0" cellspacing="0" cellpadding="0">
									                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
									                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
									             </table>
									             </td>
									            </tr>
									       </table>
									        </td>
									       <td align='left' width='50%'>
									       周，
									        </td>
									      </tr>
									      <tr>
									      <td colspan=3>
									        <input type='checkbox' name='cont_weeks' value="2">周一&nbsp; 
									        <input type='checkbox' name='cont_weeks' value="3">周二&nbsp; 
									        <input type='checkbox' name='cont_weeks' value="4">周三&nbsp; 
									        <input type='checkbox' name='cont_weeks' value="5">周四&nbsp; 
									        <input type='checkbox' name='cont_weeks' value="6">周五&nbsp; 
									        <input type='checkbox' name='cont_weeks' value="7">周六&nbsp;
									        <input type='checkbox' name='cont_weeks' value="1" checked>周日&nbsp;
									       </td>
									      </tr>
										</table>
									</fieldset>
								</div>
								<div id="cont3" style='display:none'>
									<fieldset style='height:113px;margin-left:5px;'>
										<legend>
											每月
										</legend>
										<table>
										
										<tr>
											<td width='10%'>
											<input type='radio' name='month' value=1 onclick='select_month(1,"month")' checked>第：
											 </td>
											<td id='cont_3_1_month1' width='20%'>											
												<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
									            	 <tr>
									               <td width="40" nowrap style="background-color:#FFFFFF";> 
									                   <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;">
									                   <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;width:50px;" maxlength="2" name="cont_3_1_month_day" id="every_cont_3_1_month_day" value="1" onblur="testTime(this,'D')" onfocus="setFocusObj(this,31);" onkeypress="event.returnValue=IsDigit(this);">
									               </div>
									              </td>
									             <td>
									             <table border="0" cellspacing="0" cellpadding="0">
									                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
									                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
									             </table>
									             </td>
									            </tr>
									      	   </table>								
											</td>
											<td width='10%'>
											天，&nbsp;&nbsp;&nbsp;每隔
											 </td>
											<td id='cont_3_1_month2' width='35%' align='left' style='white-space:nowrap'  >
													<table cellspacing="0" cellpadding="0">
														<tr>
														<td>
														<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
											             <tr>
											               <td width="40" nowrap style="background-color:#FFFFFF";> 
											                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;">
											                 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;width:50px;" maxlength="2" name="cont_3_1_month_day_month" id="every_cont_3_1_month_day_month" value="1" onblur="testTime(this,'mon')" onfocus="setFocusObj(this,12);" onkeypress="event.returnValue=IsDigit(this);">
											               </div>
											              </td>
											             <td>
											             <table border="0" cellspacing="0" cellpadding="0">
											                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
											                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
											             </table>
											             </td>
									     
														<td>
														个月
														 </td>
														</tr>
													</table>
														
													 </td>
												  </tr>
												  </table>
												      	
												 </td>
											<td colspan='2'>
											</td>
											</tr>
									      	<tr>
										      <td width='10%'>
										      	<input type='radio' name='month' value=2 onclick='select_month(1,"month")'>第：								      
										       </td>
										      <td id='cont_3_2_month1' disabled width='10%'>
											      <select id='cont_3_2_month_num' style='width:100%'>
												      	<option value=1>
												      		一个
												      	</option>
												      
												      	<option value=2>
												      		二个
												      	</option>
												      	
												      	<option value=3>
												      		三个
												      	</option>	
												      	<option value=4>
												      		四个
												      	</option>	
												      	<option value=L>
												      		末个
												      	</option>					    
											      </select>
											       </td>
											      <td width='5%' id='cont_3_2_month2' disabled>
												      <select  id='cont_3_2_month_weekday' >
												    	<option value=1>
												      		星期日
												      	</option>	
												      	<option value=2>
												      		星期一
												      	</option>
												      
												      	<option value=3>
												      		星期二
												      	</option>
												      	
												      	<option value=4>
												      		星期三
												      	</option>	
												      	<option value=5>
												      		星期四
												      	</option>	
												      	<option value=6>
												      		星期五
												      	</option>
												      	<option value=7>
												      		星期六
												      	</option>
												      	

												      	<option value=9>
												      		工作日
												      	</option>	
												      	<%-- 复杂定时任务一次实现不了 wangb 20190826
												      	<option value=10>
												      		周末
												      	</option>
												      	 --%>					    
												      </select>											    
											   </td>
											  
											
										      <td id='cont_3_2_month3'width='20%' disabled colspan='2'>
										      <table cellspacing="0" cellpadding="0">
										      <tr>
										      <td >
											   ，每隔
											   </td>
										      <td>
										       <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
										             <tr>
										               <td width="40" nowrap style="background-color:#FFFFFF";> 
										                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;">
										                 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;width:50px;" maxlength="2" name="cont_3_2_month_months" id="every_cont_3_2_month_months" value="1" onblur="testTime(this,'mon')" onfocus="setFocusObj(this,12);" onkeypress="event.returnValue=IsDigit(this);">
										               </div>
										              </td>
										             <td>
										             <table border="0" cellspacing="0" cellpadding="0">
										                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
										                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
										             </table>
										             </td>
										            </tr>
										       		</table>		
										      </td>
										       <td>
										      个月
										       </td>
										      </tr>
										      
										      </table>
												  									      	
										       </td>
										     
									      </tr>
										</table>
									</fieldset>
				</div>
				 </td>
			</tr>
			<tr>
			<td colspan=2>
				<fieldset style='height:200px;'>
					<legend>
						每日频率
					</legend>
					<table>
						<tr>
							<td valign='top'>
								<input type='radio' value=1 name='pl' onclick='select_month(2,"pl")' checked>一次发生于
							 </td>
							<td id='pl_1' valign='top' colspan='4'>
								<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
									 <tr>
									   <td width="40" nowrap style="background-color:#FFFFFF";> 
												   <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;width:70px;">
													    <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;" maxlength="2" name="pl_1_h" id="every_pl_1_h" value="01" onblur="testTime(this,'h')"  onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font>
										            	<input type="text" class="m_input" style="margin-left:0px;margin-right:0px;" maxlength="2" name="pl_1_min" id="every_pl_1_min" value="00" onfocus="setFocusObj(this,60);" onblur="testTime(this,'m')"  onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font>
										                <input type="text" class="m_input" style="margin-left:0px;margin-right:0px;" maxlength="2" name="pl_1_mm" id="every_pl_1_mm" value="00" onblur="testTime(this,'mm')"  onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);">
									               </div>
									              </td>
									             <td>
									             <table border="0" cellspacing="0" cellpadding="0">
									                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
									                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
									             </table>
									             </td>
									            </tr>
									       </table>
								
							 </td>						
						</tr>	
						<tr>
							<td valign='top' >
								<input type='radio' value=2 name='pl' onclick='select_month(2,"pl");'>发生周期
							</td>
							<td id='pl_2' disabled>
								<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
										             <tr>
										               <td width="40" nowrap style="background-color:#FFFFFF";> 
										                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;">
										                 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;width:50px;" maxlength="2" name="pl_2_num" id="every_pl_2_num" value="01" onblur="testTime(this,'m')" onfocus="setFocusObj(this,59);" onkeypress="event.returnValue=IsDigit(this);">
										               </div>
										              </td>
										             <td>
										             <table border="0" cellspacing="2" cellpadding="0">
										                <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
										                <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
										             </table>
										             </td>
										            </tr>
								</table>
							</td>
							<td id='pl_2' disabled>
												<select id='pl_2_type' >
															<option value=1>
																分钟
															</option>
															<option value='2' selected >
																小时
															</option>
														</select>
							</td>
							<td id='pl_2' disabled>
								起始于
							</td>
							<td id='pl_2' disabled>
									<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
										             		<tr>
											               		<td width="40" nowrap style="background-color:#FFFFFF";> 
												                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;width:70px;">
												             	 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;" maxlength="2" name="pl_2_began_h" id="every_pl_2_began_h" value="01" onblur="testTime(this,'h')"  onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font>
												                 <input type="text" class="m_input" style="margin-left:0px;margin-right:0px;" maxlength="2" name="pl_2_began_min" id="every_pl_2_began_min" value="00" onfocus="setFocusObj(this,60);" onblur="testTime(this,'m')"  onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font>
												                 <input type="text" class="m_input" style="margin-left:0px;margin-right:0px;" maxlength="2" name="pl_2_began_mm" id="every_pl_2_began_mm" value="00" onblur="testTime(this,'mm')"  onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);">
											              		 </div>
											             		 </td>
											            		 <td>
													             <table border="0" cellspacing="2" cellpadding="0">
													                <tr><td><button type="button" id="0_up_pl_began" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
													                <tr><td><button type="button" id="0_down_pl_began" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
													             </table>
											            		 </td>
										          			  </tr>
									       				</table>
							</td>
						</tr>
						<tr>
						<td id='pl_2' disabled style="padding-left:5px;">
						结束于
						</td>
						<td id='pl_2' disabled>
						<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
										             <tr>
										               <td width="40" nowrap style="background-color:#FFFFFF";> 
										                <div class="m_frameborder" style="float:left;text-align:left;padding-left:0px;width:70px;">
										             	 <input type="text" class="m_input" style="float:left;text-align:left;margin-left:0px;margin-right:0px;" maxlength="2" name="pl_2_end_h" id="every_pl_2_end_h" value="23" onblur="testTime(this,'h')"  onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font>
							                			<input type="text" class="m_input" style="margin-left:0px;margin-right:0px;" maxlength="2" name="pl_2_end_min" id="every_pl_2_end_min" value="59" onfocus="setFocusObj(this,60);" onblur="testTime(this,'m')"  onkeypress="event.returnValue=IsDigit(this);"><font color="#000000"><strong>:</strong></font>
							               			  <input type="text" class="m_input" style="margin-left:0px;margin-right:0px;" maxlength="2" name="pl_2_eng_mm" id="every_pl_2_end_mm" value="59" onblur="testTime(this,'mm')"  onfocus="setFocusObj(this,60);"  onkeypress="event.returnValue=IsDigit(this);">
										               </div>
										              </td>
										             <td>
										             <table border="0" cellspacing="0" cellpadding="0">
										                <tr><td><button type="button" id="0_up_pl_end" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
										                <tr><td><button type="button" id="0_down_pl_end" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
										             </table>
										             </td>
										            </tr>
										       		</table>
						</td>	
						</tr>	
					</table>
				</fieldset>
			</td>
			</tr>
			<tr>
				<td colspan='2'>
					<fieldset style="margin-bottom:5px;">
						<legend>
						持续时间
						</legend>
						<table cellpadding="0" cellspacing="0">
							<tr>
								<td rowspan=2 valign='top'>
									开始日期
									<input type="text" name="begandate" extra="editor" value="<%=date %>" style="width:100px;font-size:10pt;text-align:left" id="last_begin"  dropDown="dropDownDate" class="text4">
								 </td>
								<td >
									<input type='radio' name='lastendtime' value='1' onclick='select_month(3,"lastendtime")'>
									结束日期
									<input type="text" name="mxenddate" extra="editor" value="<%=date %>" style="width:100px;font-size:10pt;text-align:left" id="last_end"  dropDown="dropDownDate" disabled class="text4">
								 </td>
							</tr>
							<tr>
								<td>
									<input type='radio' name='lastendtime' value='2' onclick='select_month(3,"lastendtime")' checked>
									无结束日期
								 </td>
							</tr>
						</table>
					</fieldset>
				 </td>
			</tr>
		
		</table>
		</div>					
		</td>
			</tr>
		
		</table>
	   	</td></tr>
   		<tr height="35px;">
			<td colspan='2' align='center' height="35px;">
				<input type='button' class="mybutton" name='edit2' onclick='selectedit();' value='确定'>
				<input type='button' class="mybutton" name='edit2' onclick='winClose();' value='取消'>
			</td>
		</tr>
		</table>
		</html:form>
		
	</body>
</html>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
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
    var tt=document.getElementById('select_content');
    var options=tt.options;
		for(var i=0;i<options.length;i++){
			if(options[i].value==(parseInt(trigger)+1)){
				options[i].selected=true;
				document.getElementById("content"+options[i].value).style.display='block';
			}else{
				options[i].selected=false;
				document.getElementById("content"+options[i].value).style.display='none';
			}
		}
	var myDate = new Date(); 
	var year = myDate.getFullYear();    //获取完整的年份(4位,1970-????)
	var month=myDate.getMonth()+1;       //获取当前月份(0-11,0代表1月)
	var day=myDate.getDate();
	if(trigger==1){
		aaa();
	}else{
		bbb();
	}
</script>
<script language="javascript">
  initDocument();
</script>