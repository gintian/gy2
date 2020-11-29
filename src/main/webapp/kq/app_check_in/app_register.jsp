<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@page import="com.hjsj.hrms.utils.OperateDate"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script LANGUAGE=javascript src="/js/function.js"></script>
<script LANGUAGE=javascript src="/js/xtree.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	int i = 0;
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<%
	boolean hiddenQ1104 = false;
	FieldItem fieldItem = DataDictionary.getFieldItem("q1104");
	if(fieldItem != null){
		String state = fieldItem.getState();
		if("0".equals(state))
			hiddenQ1104 = true;
	}
%>
<style type="text/css">
body {
	background-color: transparent;
	margin: 0px;
}

.m_frameborder {
	border: 1px solid;
	width: 40px;
	height: 18px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
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
	width: 13px;
	height: 15px;
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
</style>
<STYLE type=text/css>
.div2 {
	overflow: auto;
	width: 230px;
	height: 230px;
	line-height: 15px;
	border-width: 1px;
	border-style: solid;
	/*border-width :thin ;*/
	border-color: #C4D8EE;
	margin-top: 0px;
}
</STYLE>
<script language="javascript">
var isspan="false";
function org_Emp(nbase,a_code)
{
   
}
  function addSetCard(outparamters)
  {
     var selected_emp=outparamters.getValue("selected_emp");
     if(selected_emp==null||selected_emp.length<=0)
       return false;     
     for(var s=0;s<selected_emp.length;s++)
     {
        var r_name=selected_emp[s].dataName; 
	    var r_code=selected_emp[s].dataValue;	    
        var no = new Option();
    	no.value=r_code;
    	no.text=r_name;
    	var vos= document.getElementsByName('emp_fields');
    	vos.options[vos.options.length]=no;
     }
  }
  function getemp()
  {
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    var id = currnode.uid;
    var text=currnode.text;
    if(id.indexOf("UN")!=-1||id.indexOf("UM")!=-1||id.indexOf("@K")!=-1||id.indexOf("root")!=-1)
      return;  
    var no = new Option();
    no.value=id;
    no.text=text;
    var vos= document.getElementsByName('emp_fields');
    var emp_vo=vos[0];
    var isC=true;
    var url = document.location.href;
    var tablename=""
    var parameter = new Array();
    parameter = url.split("=");
    tablename = parameter[parameter.length-1];
    for(i=0;i<emp_vo.options.length;i++)
    {
       var select_ob=emp_vo.options[i];
       if(id==select_ob.value)
       {
          isC=false;
          var a = document.getElementsByName('emp_fields');
      	  var b =a[0];
      	  if(b.options.length == 1){
      		b.options[0].selected = true;
      	  }else{
      	  	for(var j=0;j<b.options.length;j++){
      	  		if(b.options[j].value == id){
      	  			b.options[j].selected = true;
      	  		}else{
      	  			b.options[j].selected = false;
      	  		}
      	  	}
      	  }
       }
    }
    if(!isC){
       if(tablename.indexOf("Q15")!= -1)
	       searchdays();
    }
    if(isC)
    {
      emp_vo.options[emp_vo.options.length]=no;
      var a = document.getElementsByName('emp_fields');
      var b =a[0];
      if(b.options.length == 1){
      	b.options[0].selected = true;
      }else{
	      for(var i=0;i < b.options.length;i++){
	      	  if(i < (b.options.length-1))
	      	  	b.options[i].selected = false;
	      	  if(i == (b.options.length-1)){
			    b.options[i].selected = true;
	      	  }
	      }
      }
      if(tablename.indexOf("Q15")!= -1)
	      searchdays();
    } 
  }
  function setApp_fashion(fashion)
  {
     var vo_obj= document.getElementById('app_fashion');
     vo_obj.value=fashion;
     if(fashion=="0")
     {
        var waitInfo=eval("intricacy");	
	    waitInfo.style.display="none";
	    waitInfo=eval("easy");	
	    waitInfo.style.display="block";	
	    waitInfo=eval("jump");	
	    waitInfo.style.display="none";
	    var aobl=document.getElementById('a_0');
	    if(aobl)
	      aobl.style.color="red";
	     aobl=document.getElementById('a_1');
	    if(aobl)
	      aobl.style.color="#1B4A98";
	       aobl=document.getElementById('a_2');
	    if(aobl)
	      aobl.style.color="#1B4A98"; 
     }else if(fashion=="1")
     {
        var waitInfo=eval("easy");	
	    waitInfo.style.display="none";
	    waitInfo=eval("intricacy");	
	    waitInfo.style.display="block";	    
	    waitInfo=eval("jump");	
	    waitInfo.style.display="none";
	    var aobl=document.getElementById('a_0');
	    if(aobl)
	      aobl.style.color="#1B4A98";
	     aobl=document.getElementById('a_1');
	    if(aobl)
	      aobl.style.color="red";
	       aobl=document.getElementById('a_2');
	    if(aobl)
	      aobl.style.color="#1B4A98"; 
     }else if(fashion=="2")
     {
        var waitInfo=eval("easy");	
	    waitInfo.style.display="none";
	    waitInfo=eval("intricacy");	
	    waitInfo.style.display="none";	    
	    waitInfo=eval("jump");	
	    waitInfo.style.display="block";
	    var aobl=document.getElementById('a_0');
	    if(aobl)
	      aobl.style.color="#1B4A98";
	     aobl=document.getElementById('a_1');
	    if(aobl)
	      aobl.style.color="#1B4A98";
	       aobl=document.getElementById('a_2');
	    if(aobl)
	      aobl.style.color="red"; 
     }
  }
  
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getKqCalendarVar()
   {
     var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
   }
   function setkqcalendar(outparamters)
   {
      weeks=outparamters.getValue("weeks");  
      feasts=outparamters.getValue("feasts"); 
      turn_dates=outparamters.getValue("turn_dates"); 
      week_dates=outparamters.getValue("week_dates");  
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
   function IsInputValue(textid) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if(fObj.disabled==true)
		  return false;		
		if (!fObj) return;
		if(fObj.value=="")
		  fObj.value="0";		
		var cmd = event.srcElement.innerText=="5"?true:false;
		var i = parseInt(fObj.value,10);
		var radix = 200-1;		
		if(textid=="dert_value")
		{
		   cmd?i++:i--;
		}else
		{
		   if (i==radix&&cmd) {
			  i = 0;
		   } else if (i==0&&!cmd) {
			i = radix;
		   } else {
			cmd?i++:i--;
		   }
		}
		fObj.value = i;
		fObj.select();
}
    function changeClassID(obj)
    {
        var class_id=obj.value;
        var vo_obj= document.getElementById('app_fashion');
        var app_fash=vo_obj.value;
       	if(class_id!="" && class_id!="#"){
       		if(app_fash=="2")
        	{
        		document.getElementsByName("2_up")[0].disabled= true;
        		document.getElementsByName("2_up")[1].disabled= true;
        		document.getElementsByName("2_down")[0].disabled= true;
        		document.getElementsByName("2_down")[1].disabled= true;
        		
        		document.getElementById("start_h2").disabled= true;
        		document.getElementById("start_m2").disabled= true;
        		document.getElementById("end_h2").disabled= true;
        		document.getElementById("end_m2").disabled= true;
        	}else if(app_fash=="1"){
        	    document.getElementsByName("1_up")[0].disabled= true;
        	    document.getElementsByName("1_up")[1].disabled= true;
        		document.getElementsByName("1_down")[0].disabled= true;
        		document.getElementsByName("1_down")[1].disabled= true;
        		
        	    document.getElementById("start_h").disabled= true;
        		document.getElementById("start_m").disabled= true;
        		document.getElementById("end_h").disabled= true;
        		document.getElementById("end_m").disabled= true;
        	}
       }else {
       		if(app_fash=="2")
        	{
        	    document.getElementsByName("2_up")[0].disabled= false;
        		document.getElementsByName("2_up")[1].disabled= false;
        		document.getElementsByName("2_down")[0].disabled= false;
        		document.getElementsByName("2_down")[1].disabled= false;
        		document.getElementById("start_h2").disabled= false;
        		document.getElementById("start_m2").disabled= false;
        		document.getElementById("end_h2").disabled= false;
        		document.getElementById("end_m2").disabled= false;
        	}else if(app_fash=="1"){
        	    document.getElementsByName("1_up")[0].disabled= false;
        		document.getElementsByName("1_up")[1].disabled= false;
        		document.getElementsByName("1_down")[0].disabled= false;
        		document.getElementsByName("1_down")[1].disabled= false;
        	    document.getElementById("start_h").disabled= false;
        		document.getElementById("start_m").disabled= false;
        		document.getElementById("end_h").disabled= false;
        		document.getElementById("end_m").disabled= false;
        	}
       }
       var hashvo=new ParameterSet();
       hashvo.setValue("class_id",class_id);
       if(app_fash == "0") {
       		var request=new Request({method:'post',asynchronous:false,onSuccess:setTiemEasy,functionId:'1510030082'},hashvo);
       }else{
       		var request=new Request({method:'post',asynchronous:false,onSuccess:setTiem,functionId:'1510030082'},hashvo);
       }
    }
    function setTiem(outparamters)
    {
        var vo_obj= document.getElementById('app_fashion');
        isspan=outparamters.getValue("isspan");
        var app_fash=vo_obj.value;
        var start_h_id="start_h";
        var start_m_id="start_m";
        var end_h_id="end_h";
        var end_m_id="end_m";
        if(app_fash=="2")
        {
           start_h_id="start_h2";
           start_m_id="start_m2";
           end_h_id="end_h2";
           end_m_id="end_m2";
        }
        var start_h=outparamters.getValue("start_h");
        var start_m=outparamters.getValue("start_m");
        var end_h=outparamters.getValue("end_h");
        var end_m=outparamters.getValue("end_m");    
        //29356 时间为空直接返回
        if(start_h.length==0 || start_m.length==0 || end_h.length==0 || end_m.length==0){
            //alert("该班次的开始或结束时间为空，请选择其他班次！");
            return;
        }   
        var class_id=outparamters.getValue("class_id");         
        var vo_obj;        
        if(start_h!="")
        {
          vo_obj= document.getElementById(start_h_id);         
          vo_obj.value=start_h;
        }
        if(start_m!="")
        {
          vo_obj= document.getElementById(start_m_id);
          vo_obj.value=start_m;
        }
        if(end_h!="")
        {
          vo_obj= document.getElementById(end_h_id);
          vo_obj.value=end_h;
        }
        if(end_m!="")
        {
          vo_obj= document.getElementById(end_m_id);
          vo_obj.value=end_m;
        }          
        if(class_id!=""&&class_id!="#")
        {
           //document.getElementById("start_h").disabled=true;
           //document.getElementById("start_m").disabled=true; 
           //document.getElementById("end_h").disabled=true;
           //document.getElementById("end_m").disabled=true;            
        }else
        {
          // document.getElementById("start_h").disabled=false;
          // document.getElementById("start_m").disabled=false; 
          // document.getElementById("end_h").disabled=false;
          // document.getElementById("end_m").disabled=false; 
        }
                
    }
    function IsDigit() 
    { 
       return ((event.keyCode >= 48) && (event.keyCode <= 57)); 
    } 
    function IsDigitNegative()
    {
      return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
    }
    function input(sub_flag)
    {
       var tag=true;      
       var vo_obj= document.getElementById('app_fashion');
       var app_fash=vo_obj.value;
       var hashvo=new ParameterSet();
       hashvo.setValue("app_fashion",app_fash);	
       var objarr=new Array();
       var emp_vos= document.getElementsByName('emp_fields');
       var emp_vo=emp_vos[0];       
       for(i=0;i<emp_vo.options.length;i++)
       {
          objarr[i]=emp_vo.options[i].value;         
       }
       if(objarr.length<=0)
       {
         alert("请选择人员");
         return false;
       }
       hashvo.setValue("arrPer",objarr);
       var IftoRest = $F('IftoRest');
       <logic:equal name="appRegisterForm" property="table" value="Q11">
       <logic:notEmpty name="appRegisterForm" property="isExistIftoRest">
       	if(IftoRest == ""){
           	alert("请选择是否调休！");
       		return false;
        }
       	hashvo.setValue("IftoRest",IftoRest);
       </logic:notEmpty>
       </logic:equal>
       if(app_fash=="0")//简单
       {
         var objs = document.all.item("app_way");
       	 var b_flag = "0";
       	 for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		 }
		 if(b_flag != "2"){
         	var start_d=$F('easy_app_start_date');   
         	if(!isDate(start_d,"yyyy-MM-dd"))
         	{
             	alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
             	tag=false;
             	return tag;
         	}
         }else if(b_flag == "2"){
         	var scope_s_t = $F("scope_start_time");
         	var scope_e_t = $F("scope_end_time");
         	if(!isDate(scope_s_t,"yyyy-MM-dd HH:mm"))
         	{
             	alert("开始时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
             	tag=false;
             	return tag;
         	}
         	if(!isDate(scope_e_t,"yyyy-MM-dd HH:mm"))
         	{
             	alert("结束时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
             	tag=false;
             	return tag;
         	}
         }

         var app_type=$F('app_type');
       	 if(trim(app_type) == ""){
       		alert("请选择申请类型！");
            return false;
         }
			
         var app_reas=$F('app_reason');
      	 if(trim(app_reas)=="")
      	 {
         	alert("申请事由不能为空！");
         	return false;
      	 }     
		 if (b_flag=="1"){
			var start_time_h=$F("start_time_h");
			var start_time_m=$F("start_time_m");
        	var hr_count = $F("hr_count");
        	if(start_time_h=="" || start_time_m==""){
        		alert("请填写开始时间！");
        		return false;
        	}
        	if (hr_count == "" || hr_count == "0" || hr_count == "00"){
        		alert("请填写申请时间！");
        		return false;
        	}
         }else if(b_flag == "0"){
         	var date_count = $F("date_count");
        	<logic:equal name="appRegisterForm" property="table" value="Q11">
        		<%if(!hiddenQ1104){%>
        		var class_id =$F("class_id_e");
        		if(class_id=="#" || class_id==""){
        			alert("请选择参考班次！");
        			return false;
        		}
        		<%}%>
        	</logic:equal>
        	if (date_count == "" || date_count == "0" || date_count == "00"){
        		alert("请填写申请天数！");
        		return false;
        	}
         }        
         getEndDate(sub_flag);
         return false;
       }else if(app_fash=="1")//复杂
       {
          var start_d=$F('intricacy_app_start_date');
          var end_d=$F('intricacy_app_end_date');          
          if(!isDate(start_d,"yyyy-MM-dd"))
          {
                 alert("申请开始日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                 tag=false;
                 return tag;
          }else if(!isDate(end_d,"yyyy-MM-dd"))
          {
                 alert("申请结束日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
                 tag=false;
                 return tag;
          }
          var c="申请开始日期不能大于申请结束日期！";           
          if(start_d>end_d)
          {
              alert(c);
              tag=false;
              return tag;
          }else{
              tag=true;
          }
          var start_h= $F('start_h');
          var start_m= $F('start_m');
          var end_h= $F('end_h');
          var end_m= $F('end_m');
          var start_time=start_h+":"+start_m;
          var end_time=end_h+":"+end_m;

          if(!isDate(start_d + " " + start_time,"yyyy-MM-dd HH:mm"))
          {
                 alert("申请开始日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 tag=false;
                 return tag;
          }else if(!isDate(end_d + " " + end_time,"yyyy-MM-dd HH:mm"))
          {
                 alert("申请结束日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
                 tag=false;
                 return tag;
          }
          
          var now = new Date();
          var year = now.getYear();
          var month = now.getMonth();
          var date = 1;
          if(isspan!="true")
          {
             var a=new Date(year,month,date,start_h,start_m);          
             var b=new Date(year,month,date,end_h,end_m);                  
             if(a>=b)
             {
                alert("申请开始时间不能大于申请结束时间！");
                tag=false;
                return tag;
              }
          }
          
          hashvo.setValue("start_d",start_d);
          hashvo.setValue("end_d",end_d);
          hashvo.setValue("start_t",start_time);
          hashvo.setValue("end_t",end_time);          
          var fashion_type=$F('intricacy_app_fashion');
          hashvo.setValue("fashion_type",fashion_type+"");          
          hashvo.setValue("class_id",$F('class_id_f'));
      }else if(app_fash=="2")//跳天
      {
          var objapp=new Array();
          var app_vos= document.getElementsByName('app_dates');
          var app_vo=app_vos[0];       
          for(i=0;i<app_vo.options.length;i++)
          {
             objapp[i]=app_vo.options[i].value;         
          }
          if(objapp.length<=0)
          {
            alert("请选择日期");
           return false;
          }
          var start_h= $F('start_h2');
          var start_m= $F('start_m2');
          var end_h= $F('end_h2');
          var end_m= $F('end_m2');
          var start_time=start_h+":"+start_m;
          var end_time=end_h+":"+end_m;
          if(start_time==end_time)
          {
             alert("开始结束时间不能相同！");
             return false;
          }
          if(!isDate("1949-10-01" + " " + start_time,"yyyy-MM-dd HH:mm"))
          {
                 alert("申请开始时段无效,请输入正确的时间格式！\n00:00 - 23:59");
                 tag=false;
                 return tag;
          }else if(!isDate("1949-10-01" + " " + end_time,"yyyy-MM-dd HH:mm"))
          {
                 alert("申请结束时段无效,请输入正确的时间格式！\n00:00 - 23:59");
                 tag=false;
                 return tag;
          }
          
          hashvo.setValue("start_t",start_time);
          hashvo.setValue("end_t",end_time); 
          hashvo.setValue("app_dates",objapp);
          hashvo.setValue("class_id",$F('class_id_t'));
      
      }      
      var app_type=$F('app_type');
      if(trim(app_type)=="")
      {
         alert("请选择申请类型！");
         return false;
      }
      hashvo.setValue("app_type",$F('app_type'));
      var app_reas=$F('app_reason');
      if(trim(app_reas)=="")
      {
         alert("申请事由不能为空！");
         return false;
      }     
      hashvo.setValue("app_reason",$F('app_reason'));  
      hashvo.setValue("table",$F('table'));    
      hashvo.setValue("sub_flag",sub_flag);
      hashvo.setValue("dert_itemid",$F('dert_itemid'));
      hashvo.setValue("dert_value",$F('dert_value'));
  	  <logic:equal name="appRegisterForm" property="table" value="Q11">
	  <logic:notEmpty name="appRegisterForm" property="appReaCodesetid">
	      hashvo.setValue("appReaCode",$F('appReaCode'));
	      hashvo.setValue("appReaField",$F('appReaField'));
	      var codesetid = document.getElementById("appReaCodesetid").value;
  	      hashvo.setValue("appReaCodesetid",codesetid);
	  </logic:notEmpty>
	  <logic:notEmpty name="appRegisterForm" property="isExistIftoRest">
 		  hashvo.setValue("IftoRest",IftoRest);
 		  hashvo.setValue("isExistIftoRest", "${appRegisterForm.isExistIftoRest}");
  	  </logic:notEmpty>
      </logic:equal>
      var request=new Request({method:'post',asynchronous:false,onSuccess:reFlag,functionId:'1510030081'},hashvo);
    }
    function reFlag(outparamters)
    {
       var reflag=outparamters.getValue("reflag");
       if(reflag=="ok")
       {
         if(!confirm("申请成功，是否继续申请？"))
         {
            var thevo=new Object();
            thevo.flag="true";
            window.returnValue=thevo;
            window.close();
         }        
       }else
       {
    	   if(reflag.indexOf("<br/>")!= -1)
    		   reflag = replaceAll(reflag, '<br/>', '\n');
   		   alert(reflag);
       }
    }
    var old_a0101="";
    function addShiftEmployee(obj)
    {
        var a0101=$F('a0101');        
        if(a0101=="")
          closee();
        if(a0101==old_a0101)
           return;        
        var targetobj,hiddenobj;
        var currnode=Global.selectedItem;	
        if(currnode==null)
    	  return;  
        
        var id = currnode.uid;    
        var select_type=$F('select_type'); 
   	    var hashvo=new ParameterSet();	
        hashvo.setValue("a0101",getEncodeStr(a0101));
        hashvo.setValue("select_type",select_type);
        hashvo.setValue("a_code",id);
        var request=new Request({asynchronous:false,onSuccess:showA0101,functionId:'1510030083'},hashvo); 
    }
    function showA0101(outparamters)
    {
		var objlist=outparamters.getValue("objlist");		
		if(objlist!=null && 0<objlist.length){
		  AjaxBind.bind($('a0101_box'),objlist);	
		  showSelectBox(document.getElementById('a0100ID'));
		}	
    }
    function showSelectBox(srcobj)
    {
      Element.show('a0101_pnl');   
      Element.show('closee'); 
      var pos=getAbsPosition(srcobj);
	  with($('a0101_pnl'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-8;
 		    style.posTop=pos[1]-1+srcobj.offsetHeight;
		    style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth-10;
      }                 
    }  
    function setSelectValue()
    {
       var objid,i;
       var objid_text="";
       var obj=$('a0101_box');       
   	   for(i=0;i<obj.options.length;i++)
       {
          if(obj.options[i].selected)
          {
             objid=obj.options[i].value;
             objid_text=obj.options[i].text;
          }
       } 
       if(objid_text=="")
          return false;
       var vos= document.getElementsByName('emp_fields');
       var emp_vo=vos[0];
       var isC=true;
       for(i=0;i<emp_vo.options.length;i++)
       {
         var select_ob=emp_vo.options[i];
         if(select_ob.value==objid)
         {
          isC=false;
          select_ob.selected == true;
         }
      }   
      var no = new Option();
      no.value=objid;
      var kh1=objid_text.indexOf("(");
      var kh2=objid_text.indexOf(")");            
      objid_text=objid_text.substring(kh1+1,kh2);
      no.text=objid_text;
	    var url = document.location.href;
	    var tablename=""
	    var parameter = new Array();
	    parameter = url.split("=");
	    tablename = parameter[parameter.length-1];
	    for(i=0;i<emp_vo.options.length;i++)
	    {
	       var select_ob=emp_vo.options[i];
	       if(objid==select_ob.value)
	       {
	          isC=false;
	          var a = document.getElementsByName('emp_fields');
	      	  var b =a[0];
	      	  if(b.options.length == 1){
	      		b.options[0].selected = true;
	      	  }else{
	      	  	for(var j=0;j<b.options.length;j++){
	      	  		if(b.options[j].value == objid){
	      	  			b.options[j].selected = true;
	      	  		}else{
	      	  			b.options[j].selected = false;
	      	  		}
	      	  	}
	      	  }
	       }
	    }
      if(isC)
      {
         emp_vo.options[emp_vo.options.length]=no;
	      var a = document.getElementsByName('emp_fields');
	      var b =a[0];
	      if(b.options.length == 1){
	      	b.options[0].selected = true;
	      }else{
		      for(var i=0;i < b.options.length;i++){
		      	  if(i < (b.options.length-1))
		      	  	b.options[i].selected = false;
		      	  if(i == (b.options.length-1)){
				    b.options[i].selected = true;
		      	  }
		      }
	      }
	      if(tablename.indexOf("Q15")!= -1)
		      searchdays();
	    } 
	    if(!isC){
	       if(tablename.indexOf("Q15")!= -1)
		       searchdays();
	    }

	    if (obj.options.length==1)
		    closee();
    }  
    function closee()
    {
       Element.hide('a0101_pnl');
       Element.hide('closee');
       var obj=document.getElementById("a0100ID");
       obj.value="";
    }
    function bacthShiftEmployee()
    {
        var targetobj,hiddenobj;
        var currnode=Global.selectedItem;	
        if(currnode==null)
    	  return;  
        var id = currnode.uid;
        if(id.indexOf("UN")==-1&&id.indexOf("UM")==-1&&id.indexOf("@K")==-1)
        {
            alert("请选择单位、部门或岗位");
            return false;
        }
        if(id=="root")
           id="UN";
           
        var target_url="/kq/team/array/cycle_shift_employee.do?b_employee=link`object_flag=0`a_code="+id;
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
         var return_vo= window.showModalDialog(iframe_url,0, 
        "dialogWidth:500px; dialogHeight:510px;resizable:no;center:yes;scroll:yes;status:no");
        if(return_vo!=null&&return_vo.length>0)
        {
           var objA=return_vo.split("`");
           for(var i=0;i<objA.length;i++)
           {
              var one_obj=objA[i];              
              if(one_obj!=""&&one_obj.length>0)
              {
                 var objO=one_obj.split("^");
                 var id=objO[0]+objO[1];
                 var text=objO[2];
                 setSelectOpions(id,text);
              }
           }
        }
    }
    function setSelectOpions(id,text)
    {
       var vos= document.getElementsByName('emp_fields');
       var emp_vo=vos[0];
       var isC=true;
       for(i=0;i<emp_vo.options.length;i++)
       {
         var select_ob=emp_vo.options[i];
         if(select_ob.value==id)
         {
          isC=false;
         }
      }      
      var no = new Option();
      no.value=id;  
      no.text=text;
      if(isC)
      {
         emp_vo.options[emp_vo.options.length]=no;
      }       
    }  
    function setapp_date()
    {
    	var tNow=document.getElementById('timejump');
    	getKqCalendarVar();
    	popUpCalendar(tNow,tNow,weeks,feasts,turn_dates,week_dates,false,false);
  	 
   
    }
    function getdatevalue()
    {
      var value= document.getElementById('timejump').value;
  	  var vos= document.getElementsByName("app_dates");
  	  var app_vo=vos[0];
   	  var no = new Option();
	  no.value=value;
	  no.text=value;
	  app_vo.options[app_vo.options.length]=no;
    }
    function closeWin()
    {
        var thevo=new Object();
        thevo.flag="true";
        window.returnValue=thevo;
        window.close();
    }
    
    function init(){
		var objs = document.all.item("app_way");
		var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var time = document.all.item("time");
		var day = document.getElementById("day");
		<logic:equal name="appRegisterForm" property="table" value="Q11">
		var day_scope = document.getElementById("day_scope")
		</logic:equal>
		var tr1 = document.all.item("time_scope");
		var tr2 = document.all.item("time_noscope");
		if (b_flag=="1"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="none";
    		}
			for(var i =0;i<tr2.length;i++){
    			tr2[i].style.display="";
    		}
			for(var i =0;i<time.length;i++){
    			time[i].style.display="";
    		}
    		<logic:equal name="appRegisterForm" property="table"	value="Q11">
				day_scope.style.display="none";
			</logic:equal>
    		day.style.display="none";
    		getStartTime();
		} else if (b_flag == "0"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="none";
    		}
			for(var i =0;i<tr2.length;i++){
    			tr2[i].style.display="";
    		}
			for(var i =0;i<time.length;i++){
    			time[i].style.display="none";
    		}
    		<logic:equal name="appRegisterForm" property="table"	value="Q11">
    		<%
    		if(!hiddenQ1104)
    		{
    		%>
				day_scope.style.display="";
			<%}%>
			</logic:equal>
    		day.style.display="";
		} else if (b_flag=="2"){
			for(var i =0;i<tr1.length;i++){
    			tr1[i].style.display="";
    		}
			for(var i =0;i<tr2.length;i++){
    			tr2[i].style.display="none";
    		}
    		for(var i =0;i<time.length;i++){
    			time[i].style.display="none";
    		}
    		<logic:equal name="appRegisterForm" property="table"	value="Q11">
				day_scope.style.display="none";
			</logic:equal>
    		day.style.display="none";
    		getStartTime();
		}
	}
	
	function getEndDate(sub_flag){
	    var vo_obj= document.getElementById('app_fashion');
        var app_fash=vo_obj.value;
        if(app_fash!=0){
        	return;
        }
	   	var hashvo=new ParameterSet();	
		var objs = document.all.item("app_way");
		var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var start_d=$F("easy_app_start_date");
		var objarr = new Array();
        var emp_vos = document.getElementsByName('emp_fields');
        var emp_vo = emp_vos[0];       
        var app_type = $F("app_type");
        var scope_s_t = $F("scope_start_time");
        var scope_e_t = $F("scope_end_time");
        for(i=0;i<emp_vo.options.length;i++)
        {
            objarr[i]=emp_vo.options[i].value;         
        }
        if(objarr.length<=0 || app_type == ""){
            return;
        }
        if(b_flag != 2){
        	if(!isDate(start_d,"yyyy-MM-dd")){
        		return;
        	}
        }else {
        	if(!isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		return;
        	}
        	if(!isDate(scope_e_t,"yyyy-MM-dd HH:mm")){
        		return;
        	}
        }
		if (b_flag=="1"){
			var start_time_h=$F("start_time_h");
			var start_time_m=$F("start_time_m");
        	var hr_count = $F("hr_count"); //.replace(/[^0-9]+/, '');
        // 	document.appRegisterForm.hr_count.value = hr_count;
        	if(start_time_h=="" || start_time_m==""){
        		return;
        	}
        	if (hr_count == "" || hr_count == "0" || hr_count == "00"){
        		return;
        	}
        	hashvo.setValue("start_time_h",start_time_h);
        	hashvo.setValue("start_time_m",start_time_m);
        	hashvo.setValue("hr_count",hr_count);
        	hashvo.setValue("app_way","1");
        } else if(b_flag == "0"){
        	var date_count = $F("date_count").replace(/[^0-9]+/, '');
        	document.appRegisterForm.date_count.value = date_count;
        	<logic:equal name="appRegisterForm" property="table" value="Q11">
        		var class_id =$F("class_id_e");
        		if(class_id == "#"&& !<%= hiddenQ1104%>)
        		{
					return;
            	}
        		hashvo.setValue("class_id",class_id);
        	</logic:equal>
        	if (date_count == "" || date_count == "0" || date_count == "00"){
        		return;
        	}
        	hashvo.setValue("date_count",date_count);
        	
        }else if(b_flag == "2"){
        	hashvo.setValue("scope_start_time",scope_s_t);
        	hashvo.setValue("scope_end_time",scope_e_t);
        }
		var IftoRest = $F('IftoRest');
        <logic:equal name="appRegisterForm" property="table" value="Q11">
        <logic:notEmpty name="appRegisterForm" property="isExistIftoRest">
       		hashvo.setValue("IftoRest",IftoRest);
       		hashvo.setValue("isExistIftoRest", "${appRegisterForm.isExistIftoRest}");
        </logic:notEmpty>
        </logic:equal>
        hashvo.setValue("app_way",b_flag);
        hashvo.setValue("arrPer",objarr);
        hashvo.setValue("start_d",start_d);
        hashvo.setValue("table",$F("table"));
        hashvo.setValue("dert_itemid",$F("dert_itemid"));
        hashvo.setValue("dert_value",$F("dert_value"));
        hashvo.setValue("app_type",$F("app_type"));
        hashvo.setValue("sub_flag",sub_flag);

        if(sub_flag == '0' && b_flag != "2"){
        	var request=new Request({method:'post',asynchronous:false,onSuccess:setEndDate,functionId:'1510010115'},hashvo);
		}else if(sub_flag != '0'){
			var request=new Request({method:'post',asynchronous:false,onSuccess:addEasyApp,functionId:'1510010115'},hashvo);
		}
	}
	function setEndDate(outparamters){
		if (outparamters.getValue("err_message") != ""){
            alert(outparamters.getValue("err_message"));
            return false;
        }
        if (outparamters.getValue("pro_message") != ""){
            if(!window.confirm(outparamters.getValue("pro_message"))){
                return false;
            }
        }
		document.appRegisterForm.easy_app_end_date.value = outparamters.getValue("endDate");
	}
	
	var scope_s_t_t;
    
	function getStartTime()
    {
    	var hashvo=new ParameterSet();	
    	var objs = document.all.item("app_way");
    	var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var objarr = new Array();
		var emp_vos = document.getElementsByName('emp_fields');
        var emp_vo = emp_vos[0];       
        for(i=0;i<emp_vo.options.length;i++)
        {
            objarr[i]=emp_vo.options[i].value;         
        }
		var start_d;
		if(b_flag == "1"){
		 	start_d=$F("easy_app_start_date");
		 	if(objarr.length<=0 || !isDate(start_d,"yyyy-MM-dd")){
            	return false;
        	}
        	hashvo.setValue("start_d",start_d);
		}else if(b_flag == "2"){
			var scope_s_t=$F("scope_start_time");
			if(objarr.length<=0 || !isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
            	return false;
        	}
        	start_d = scope_s_t.substring(0,10)
        	if(start_d == scope_s_t_t){
        		return false;
        	}
		}
        hashvo.setValue("arrPer",objarr);
        hashvo.setValue("start_d",start_d);
		if(document.appRegisterForm.table.value!="Q11"){
			if (b_flag != "0"){
				var request=new Request({method:'post',onSuccess:setStartTime,functionId:'1510010117'},hashvo);
			}
		}
    }

	function setStartTime(outparamters){
		var objs = document.all.item("app_way");
    	var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
		var start_h = outparamters.getValue("start_time_h");
		var start_m = outparamters.getValue("start_time_m");
		if(b_flag == "1"){
			document.appRegisterForm.start_time_h.value = start_h;
			document.appRegisterForm.start_time_m.value = start_m;
		}else if(b_flag == "2"){
			var scope_s_t = document.appRegisterForm.scope_start_time.value;
        	if(isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		scope_s_t_t = scope_s_t.substring(0,10);
        		scope_s_t = scope_s_t.substring(0,11) + start_h + ":" + start_m;
        		document.appRegisterForm.scope_start_time.value = scope_s_t;
        	}
		}
	}
	
	function addEasyApp(outparamters){
		if (outparamters.getValue("err_message") != ""){
    		alert(outparamters.getValue("err_message"));
    		return false;
    	}
    	if (outparamters.getValue("pro_message") != ""){
    		if(!window.confirm(outparamters.getValue("pro_message"))){
    			return false;
    		}
        }
        var count;
		var start_date;
		var objs = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
        if(b_flag == "0"){
        	count = "申请天数：" + document.appRegisterForm.date_count.value + "天";
        	start_date = "开始时间：" + outparamters.getValue("startDate");
        } else if(b_flag == "1"){
        	count = "申请时间：" + document.appRegisterForm.hr_count.value + "小时";
        	start_date = "开始时间：" + outparamters.getValue("startDate");
        }
        var end_date = "结束时间：" + outparamters.getValue("endDate");
        
    	document.appRegisterForm.easy_app_end_date.value = outparamters.getValue("endDate");
    	var hashvo=new ParameterSet();  
    	    
		var start_d = $F("easy_app_start_date");
		var end_d = $F("easy_app_end_date");
		var scope_start_time = $F("scope_start_time");
		var scope_end_time = $F("scope_end_time");
		var objarr = new Array();
        var emp_vos = document.getElementsByName('emp_fields');
        var emp_vo = emp_vos[0];      
        var app_type = $F("app_type");
        var app_reas=$F('app_reason');
        for(i=0;i<emp_vo.options.length;i++)
        {
            objarr[i]=emp_vo.options[i].value;         
        }
		if (b_flag=="1"){
			var start_time_h=$F("start_time_h");
			var start_time_m=$F("start_time_m");
        	var hr_count = $F("hr_count");
        	hashvo.setValue("start_time_h",start_time_h);
        	hashvo.setValue("start_time_m",start_time_m);
        	hashvo.setValue("hr_count",hr_count);
        } else if(b_flag=="0") {
        	var date_count = $F("date_count");
        	<logic:notEqual name="appRegisterForm" property="table" value="Q15">
        		var class_id =$F("class_id_e");
        		hashvo.setValue("class_id",class_id);
        	</logic:notEqual>
        	hashvo.setValue("date_count",date_count);
        } else if(b_flag == "2"){
        	hashvo.setValue("scope_start_time",scope_start_time);
        	hashvo.setValue("scope_end_time",scope_end_time);
        	<logic:equal name="appRegisterForm" property="table" value="Q11">
        		var class_id =$F("class_id_e");
        		hashvo.setValue("class_id",class_id);
        	</logic:equal>
        }
        hashvo.setValue("app_way",b_flag);
        hashvo.setValue("arrPer",objarr);
        hashvo.setValue("app_reas",app_reas);
        hashvo.setValue("start_d",start_d);
        hashvo.setValue("end_d",end_d);
        hashvo.setValue("table",$F("table"));
        hashvo.setValue("app_type",$F("app_type"));
        hashvo.setValue("sub_flag",outparamters.getValue("sub_flag"));
        <logic:equal name="appRegisterForm" property="table" value="Q11">
  	    <logic:notEmpty name="appRegisterForm" property="appReaCodesetid">
  	      hashvo.setValue("appReaCode",$F('appReaCode'));
  	      hashvo.setValue("appReaField",$F('appReaField'));
  	      var codesetid = document.getElementById("appReaCodesetid").value;
  	      hashvo.setValue("appReaCodesetid",codesetid);
  	    </logic:notEmpty>
        </logic:equal>
        
        if(b_flag != "2" && window.confirm(count+"\n" + start_date + "\n" + end_date)){
        	var request=new Request({method:'post',asynchronous:false,onSuccess:reFlag,functionId:'1510010116'},hashvo);
        }else if(b_flag == "2"){
        	var request=new Request({method:'post',asynchronous:false,onSuccess:reFlag,functionId:'1510010116'},hashvo);
        }
	}
	
	function setTiemEasy(outparamters){
		var objs = document.all.item("app_way");
        var b_flag = "0";
		for(var i = 0;i<objs.length;i++){
			if(objs[i].checked==true){
				b_flag = objs[i].value;
			}
		}
	    var start_h=outparamters.getValue("start_h");
        var start_m=outparamters.getValue("start_m");
        var end_h=outparamters.getValue("end_h");
        var end_m=outparamters.getValue("end_m");     
        var class_id=outparamters.getValue("class_id");
        if(class_id != "#" && class_id != "" && b_flag == 2){
        	var scope_s_t = document.appRegisterForm.scope_start_time.value;
        	if(isDate(scope_s_t,"yyyy-MM-dd HH:mm")){
        		scope_s_t = scope_s_t.substring(0,11) + start_h + ":" + start_m;
        		document.appRegisterForm.scope_start_time.value = scope_s_t;
        	}
        	var scope_e_t = document.appRegisterForm.scope_end_time.value;
        	if(isDate(scope_e_t,"yyyy-MM-dd HH:mm")){
        		scope_e_t = scope_e_t.substring(0,11) + end_h + ":" + end_m;
        		document.appRegisterForm.scope_end_time.value = scope_e_t;
        	}
        }
	}
	function searchdays(){
		var url = document.location.href;
	    var tablename=""
	    var parameter = new Array();
	    parameter = url.split("=");
	    tablename = parameter[parameter.length-1];
		if(tablename.indexOf("Q15")!= -1){
			var emp_fields = document.getElementsByName('emp_fields');
			var emp_field = emp_fields[0];
			var peopleName="";
			var objarr = new Array;
			var i;
			for(i=0;i<emp_field.options.length;i++)
	        {
	            objarr[i]=emp_field.options[i].value;
	           	if(emp_field.options[i].selected){
	           		peopleName = emp_field.options[i].value;
	           	}         
	        }	
			
			var app_type=$F('app_type');
			if(peopleName == null)
				return false;
			if(peopleName != null && app_type != null && app_type.length > 0){
				var hashvo=new ParameterSet();	
				hashvo.setValue("peopleName",peopleName);
				hashvo.setValue("app_type",app_type);
				var obj = $F('app_way');//0 天   1小时   2区间
				var app_way = obj[0];
				var start_d = "";
				var start_h = "";
				var start_m = "";
				var end_d = "";
				var date_count = "";
				var hr_count = "";
				if("0" == app_way)
				{
					start_d = $F("easy_app_start_date");
					end_d = $F('easy_app_end_date');
					date_count = $F('date_count');
					if(!isDate(start_d,"yyyy-MM-dd")){
						alert("申请开始 日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
		        		return;
		        	}
				}else if ("1" == app_way)
				{
					start_d = $F("easy_app_start_date");
					end_d = $F('easy_app_end_date');
					start_h = $F('start_time_h');
					start_m = $F('start_time_m');
					hr_count = $F('hr_count');
					if(!isDate(start_d,"yyyy-MM-dd")){
						alert("申请开始 日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
		        		return;
		        	}
				}else if ("2" == app_way)
				{
					start_d = $F('scope_start_time');
					end_d = $F('scope_end_time');
					if(!isDate(start_d,"yyyy-MM-dd HH:mm")){
						alert("申请开始 日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
		        		return;
		        	}
		        	if(!isDate(end_d,"yyyy-MM-dd HH:mm")){
			        	alert("申请结束日期格式不正确,请输入正确的时间格式！\nyyyy-MM-dd HH:mm");
		        		return;
		        	}
				}
				hashvo.setValue("app_way",app_way);
				hashvo.setValue("start_d",start_d);
				hashvo.setValue("start_h",start_h);
				hashvo.setValue("start_m",start_m);
				hashvo.setValue("end_d",end_d);
				hashvo.setValue("date_count",date_count);
				hashvo.setValue("hr_count",hr_count);
				var request=new Request({method:'post',asynchronous:false,onSuccess:setLeftDays,functionId:'1510010118'},hashvo);
			}
		}
	}
	function setLeftDays(outparamters){
		var leftdays0fvacation = outparamters.getValue("leftdays0fvacation");
		var isshow = outparamters.getValue("isshow");
		if(leftdays0fvacation != -1){
			if(isshow == "1" ){
				document.getElementById('div').style.display = 'block';
			}else{
				document.getElementById('div').style.display = 'none';
			}
		document.getElementById("leftdays0fvacation").innerHTML = leftdays0fvacation;
		}
	}
	function isshow(){
		var app_type=$F('app_type');
		var hashvo=new ParameterSet();	
		hashvo.setValue("app_type",app_type);
		hashvo.setValue("start_d",$F("easy_app_start_date"));
		var request=new Request({method:'post',asynchronous:false,onSuccess:showornot,functionId:'1510010119'},hashvo);
	}

	function showornot(outparamters){
		var isshow = outparamters.getValue("isshow");
		var leftdays0fvacation = outparamters.getValue("leftdays0fvacation");
		var emp_fields = document.getElementsByName('emp_fields');
			var emp_field = emp_fields[0];
			var peopleName;
			var objarr = new Array;
			var i;
			for(i=0;i<emp_field.options.length;i++)
	        {
	            objarr[i]=emp_field.options[i].value;
	           	if(emp_field.options[i].selected){
	           		peopleName = emp_field.options[i].value;
	           	}         
	        }
		if(isshow == "1" && peopleName != null && leftdays0fvacation != -1){
			document.getElementById('div').style.display = 'block';
		}else{
			document.getElementById('div').style.display = 'none';
		}
	}
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<html:form action="/kq/app_check_in/app_register">
	<!-- <div class="fixedDiv2" style="height:100%;border: none;width:540"> -->
		<table width="540" height="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable" style="padding:0 0 0 9;">
			<tr>
				<td>
					<table border="0" cellspacing="0" align="center" cellpadding="0"
						class="ListTable" width="100%" height="100%">
						<tr>
							<td align="left" width="44%">
								备选人员
								<html:hidden name="appRegisterForm" property="table"
									styleClass="text" />
								<html:hidden name="appRegisterForm" styleId="app_fashion"
									property="app_fashion" styleClass="text" />
							</td>
							<td align="center" width="12%">

							</td>
							<td valign="top" align="left" width="44%">
								已选人员
							</td>
						</tr>
						<tr>
							<td align="left" valign="top" width="44%">
								<div id="tbl_container" onclick="getemp();isshow();getEndDate('0');" class="div2 complex_border_color">
									<hrms:kqpersontree flag="1" showroot="false" dbtype="0"
										priv="1" target="app" />
								</div>
							</td>
							<td align="center" width="12%">
								<html:button styleClass="mybutton" property="b_addfield"
									onclick="getemp();">
									<bean:message key="button.setfield.addfield" />
								</html:button>
								<br>
								<br />
								<html:button styleClass="mybutton" property="b_delfield"
									onclick="removeitem('emp_fields');">
									<bean:message key="button.setfield.delfield" />
								</html:button>
							</td>
							<td valign="top" align="center" width="42%" >
								<div  style="height: 210px; width: 100%; overflow:hidden;" class="div2 complex_border_color">
									<select name="emp_fields" multiple="multiple"
										ondblclick="removeitem('emp_fields');isshow();"
										onclick="searchdays();isshow();" 
										style="height:220px ; width: 102%;font-size: 9pt; margin:-2px 0px 0px -2px;">
									</select>
								</div>
							</td>
						</tr>
						<tr>
							<td height="30" 　nowrap>
								<table border="0" cellspacing="0" align="left" cellpadding="0">
									<tr>
										<td>
											<a href="###" onclick="setApp_fashion('0');" id="a_0">简单申请</a>
											&nbsp;&nbsp;
										</td>
										<td>
											<logic:equal name="appRegisterForm" property="table"
												value="Q11">
												<a href="###" onclick="setApp_fashion('1');" id="a_1">复杂申请</a> &nbsp;
                   </logic:equal>
										</td>
										<td>
											&nbsp;
											<a href="###" onclick="setApp_fashion('2');" id="a_2">跳天申请</a>
										</td>
									</tr>
								</table>
							</td>
							<td>

							</td>
							<td valign="middle" nowrap>
								<table border="0" cellspacing="0" align="left" cellpadding="0">
									<tr>
										<td valign="middle">
										   <select id="select_type" size="1">
						            		  <option value="0">姓名</option>                      
						                      <option value="1">工号</option>
						                      <option value="2">考勤卡号</option>
           									</select>
           								</td>
           								<td valign="middle">
											<input type="text" name="a0101" id="a0100ID" value=""
												class="text4"
												style="width: 100px; font-size: 10pt; text-align: left; margin-left: 2px;"
												id="a0101" onkeyup="addShiftEmployee(this);">
											&nbsp;&nbsp;
										</td>
										<td valign="top">
											<div id="closee">
												<a href="###" onclick="closee();"><font color="red">关闭</font>
												</a>
											</div>
										</td>
									</tr>
								</table>

							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td height="135" width="100%">
								<div id="intricacy"
									style="padding:0 5 0 5; border-color: #C4D8EE; border-width: 1px; border-style: solid; height: 115px; width: 100%;" class='complex_border_color'>
									<table width="100%">
										<tr>
											<td width="49%" height="30">
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60">
														<logic:equal name="appRegisterForm" property="table" value="Q11">
														  加班日期
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q13">
														  公出日期
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q15">
														  请假日期
														</logic:equal>															
														</td>
														<td>
															<html:text name="appRegisterForm"
																property='intricacy_app_start_date' size="10"
																maxlength="20"
																onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
																styleClass="TEXT4" />
														</td>
														<td>
															~&nbsp;&nbsp;&nbsp;&nbsp;
														</td>
														<td>
															<html:text name="appRegisterForm"
																property='intricacy_app_end_date' size="10"
																maxlength="20"
																onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
																styleClass="TEXT4" />
														</td>
													</tr>
												</table>
											</td>
											<td width="2%">

											</td>
											<td width="49%" height="30">
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60" nowrap="nowrap">
													    <logic:equal name="appRegisterForm" property="table" value="Q11">
														  加班时段
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q13">
														  公出时段
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q15">
														  请假时段
														</logic:equal>
														</td>
														<td width="65">
															<table border="0" cellspacing="0" align="left"
																cellpadding="0">
																<tr>
																	<td width="40" nowrap style="background-color: #FFFFFF;">
																		<div class="m_frameborder inputtext">
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_start_time_h" id="start_h"
																				value="${appRegisterForm.intricacy_app_start_time_h}"
																				onfocus="setFocusObj(this,24);"
																				onkeypress="event.returnValue=IsDigit();">
																			<font color="#000000"><strong>:</strong> </font>
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_start_time_m" id="start_m"
																				value="${appRegisterForm.intricacy_app_start_time_m}"
																				onfocus="setFocusObj(this,60);"
																				onkeypress="event.returnValue=IsDigit();">
																		</div>
																	</td>
																	<td>
																		<table border="0" cellspacing="2" cellpadding="0">
																			<tr>
																				<td>
																					<button id="1_up" class="m_arrow"
																						onmouseup="IsInputTimeValue();">
																						5
																					</button>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<button id="1_down" class="m_arrow"
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
														<td align="center">
															~
														</td>
														<td width="70">
															<table border="0" cellspacing="0" align="left"
																cellpadding="0">
																<tr>
																	<td width="40" nowrap style="background-color: #FFFFFF;">
																		<div class="m_frameborder inputtext">
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_end_time_h" id="end_h"
																				value="${appRegisterForm.intricacy_app_end_time_h}"
																				onfocus="setFocusObj(this,24);"
																				onkeypress="event.returnValue=IsDigit();">
																			<font color="#000000"><strong>:</strong> </font>
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_end_time_m" id="end_m"
																				value="${appRegisterForm.intricacy_app_end_time_m}"
																				onfocus="setFocusObj(this,60);"
																				onkeypress="event.returnValue=IsDigit();">
																		</div>
																	</td>
																	<td>
																		<table border="0" cellspacing="2" cellpadding="0">
																			<tr>
																				<td>
																					<button id="1_up" class="m_arrow"
																						onmouseup="IsInputTimeValue();">
																						5
																					</button>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<button id="1_down" class="m_arrow"
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
											<td>
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60">
														<logic:equal name="appRegisterForm" property="table" value="Q11">
														  加班方式
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q13">
														  公出方式
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q15">
														  请假方式
														</logic:equal>
														</td>
														<td>
														   <table width="100%" border="0" cellspacing="0" align="left"
													        cellpadding="0">
													         <tr>
													            <td>
													              <html:radio name="appRegisterForm"
																property="intricacy_app_fashion" value="0"
																styleId='scope2' />
															   &nbsp;每天一次
													            </td>
													         </tr>
													          <tr>
													            <td>
													             <html:radio name="appRegisterForm"
																property="intricacy_app_fashion" value="1"
																styleId='scope2' />
															&nbsp;每公休日一次
													            </td>
													         </tr>
													        </table>
														</td>
													</tr>
												</table>
											</td>
											<td width="2%">

											</td>
										</tr>
										<tr id="td1">
											<td>
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60" nowrap="nowrap">
															参考班次
														</td>
														<td align="left">
															<hrms:optioncollection name="appRegisterForm"
																property="class_list" collection="list" />
															<html:select name="appRegisterForm" property="class_id"
																styleId="class_id_f" onchange="changeClassID(this);"
																size="1">
																<html:options collection="list" property="dataValue"
																	labelProperty="dataName" />
															</html:select>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
								<div id="easy"
									style="padding:0 5 0 5; border-color: #C4D8EE; border-width: 1px; border-style: solid; height: 95px; width: 100%;" class='complex_border_color'>
									<table width="100%">
										<tr>
											<td align="left" colspan="2">
												<bean:message key="kq.class.applyscope" />
												<html:radio name="appRegisterForm" property="app_way"
													value="0" onclick="init();getEndDate('0');"></html:radio>
												<bean:message key="kq.shift.relief.day" />
												<html:radio name="appRegisterForm" property="app_way"
													value="1" onclick="init();getEndDate('0');"></html:radio>
												<bean:message key="kq.class.hour" />
												<html:radio name="appRegisterForm" property="app_way"
													value="2" onclick="init();"></html:radio>
												<bean:message key="kq.time.space" />
											</td>
										</tr>
										<tr>
											<td width="50%" align="left" nowrap="nowrap">
												<div id="time_noscope">
													<bean:message key="kq.deration_details.start" />
													<input type="text" name="easy_app_start_date" size="20" value="${appRegisterForm.easy_app_start_date}"
														maxlength="10" id="easy_app_start_date"
														onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
														class="TEXT4"
														onpropertychange="getEndDate('0');getStartTime();searchdays();" />
												</div>
												<div id="time_scope" style="display: none;">
													<bean:message key="kq.deration_details.start" />
													<input type="text" name="scope_start_time"
														value="${appRegisterForm.scope_start_time}" size="20"
														maxlength="16" id="scope_start_time"
														onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
														onpropertychange="getStartTime();" class="TEXT4" />
												</div>
											</td>
											<td nowrap="nowrap">
												<div id="day">
													<logic:equal name="appRegisterForm" property="table" value="Q11">
										                加班天数
							                        </logic:equal>
								                    <logic:equal name="appRegisterForm" property="table" value="Q13">
										                公出天数
								                    </logic:equal>
								                    <logic:equal name="appRegisterForm" property="table" value="Q15">
										                请假天数
								                    </logic:equal>
													<html:text property="date_count" maxlength="3" size="10"
														styleClass="TEXT4" style="text-align:right"
														onchange="getEndDate('0');"
														onkeypress="event.returnValue=IsDigit();" value="1"></html:text>
													<bean:message key="kq.rest.day" />
												</div>
												<div id="time"  style="display: none;">
													<table border="0" cellspacing="0" cellpadding="0">
														<tr>
															<td>
																<bean:message key="kq.strut.start" />
																&nbsp;
															</td>
															<td width="40" nowrap style="background-color: #FFFFFF";>
																<div class="m_frameborder inputtext">
																	<input type="text" class="m_input" maxlength="2"
																		name="start_time_h" id="start_time_h"
																		value="${appRegisterForm.intricacy_app_start_time_h}"
																		onfocus="setFocusObj(this,24);"
																		onchange="getEndDate('0');"
																		onkeypress="event.returnValue=IsDigit();">
																	<font color="#000000"><strong>:</strong> </font>
																	<input type="text" class="m_input" maxlength="2"
																		name="start_time_m" id="start_time_m"
																		value="${appRegisterForm.intricacy_app_start_time_m}"
																		onfocus="setFocusObj(this,60);"
																		onchange="getEndDate('0');"
																		onkeypress="event.returnValue=IsDigit();">
																</div>
															</td>
															<td>
																<table border="0" cellspacing="2" cellpadding="0">
																	<tr>
																		<td>
																			<button id="0_up" class="m_arrow"
																				onclick="getEndDate('0');"
																				onmouseup="IsInputTimeValue();">
																				5
																			</button>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<button id="0_down" class="m_arrow"
																				onclick="getEndDate('0');"
																				onmouseup="IsInputTimeValue();">
																				6
																			</button>
																		</td>
																	</tr>
																</table>
															</td>
														</tr>
													</table>
												</div>
												<div id="time_scope" style="display: none;">
													(日期格式：<%
													out
																.print(OperateDate.dateToStr(new Date(),
																		"yyyy-MM-dd HH:mm"));
												%>)
												</div>
											</td>
										</tr>
										<tr>
											<td width="50%" align="left" nowrap="nowrap">
												<div id="time_noscope">
													<bean:message key="lable.zp_plan.end_date" />
													<html:text name="appRegisterForm"
														style="background-color:#F8F8F8;"
														property='easy_app_end_date' size="20" maxlength="20"
														styleClass="TEXT4" readonly="true" />
												</div>
												<div id="time_scope" style="display: none;">
													<bean:message key="lable.zp_plan.end_date" />
													<input type="text" name="scope_end_time" size="20"
														maxlength="16" id="scope_end_time"  	value="${appRegisterForm.scope_end_time}"
														onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,false);'
														class="TEXT4" />
												</div>
											</td>
											<td nowrap="nowrap" id="td2">
												<logic:equal name="appRegisterForm" property="table" value="Q11">
													<div id="day_scope">
														参考班次
														<hrms:optioncollection name="appRegisterForm"
															property="class_list" collection="list" />
														<html:select name="appRegisterForm" property="class_id"
															styleId="class_id_e" onchange="getEndDate('0');changeClassID(this);" size="1">
															<html:options collection="list" property="dataValue"
																labelProperty="dataName" />
														</html:select>
													</div>
												</logic:equal>
												<div id="time" style="display: none;">
													<bean:message key="kq.class.applytime" />
													<html:text property="hr_count" maxlength="4" size="16"
														styleClass="TEXT4" style="text-align:right"
														onkeypress="event.returnValue=IsDigitNegative();"
														onchange="getEndDate('0');" value="1"></html:text>
													<bean:message key="kq.class.hour" />
												</div>
											</td>
										</tr>
									</table>
								</div>
								<div id="jump"
									style="padding:0 5 0 5; border-color: #C4D8EE; border-width: 1px; border-style: solid; height: 135px; width: 100%;" class='complex_border_color'>
									<table width="100%" border="0" cellspacing="0" cellpadding="0">
										<tr>
											<td width="49%" height="30">
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60">
														<logic:equal name="appRegisterForm" property="table" value="Q11">
														  加班时段
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q13">
														  公出时段
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q15">
														  请假时段
														</logic:equal>
														</td>
														<td width="65">
															<table border="0" cellspacing="0" align="left"
																cellpadding="0">
																<tr>
																	<td width="40" nowrap style="background-color: #FFFFFF";>
																		<div class="m_frameborder inputtext">
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_start_time_h" id="start_h2"
																				value="${appRegisterForm.intricacy_app_start_time_h}"
																				onfocus="setFocusObj(this,24);"
																				onkeypress="event.returnValue=IsDigit();">
																			<font color="#000000"><strong>:</strong> </font>
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_start_time_m" id="start_m2"
																				value="${appRegisterForm.intricacy_app_start_time_m}"
																				onfocus="setFocusObj(this,60);"
																				onkeypress="event.returnValue=IsDigit();">
																		</div>
																	</td>
																	<td>
																		<table border="0" cellspacing="2" cellpadding="1">
																			<tr>
																				<td>
																					<button id="2_up" class="m_arrow"
																						onmouseup="IsInputTimeValue();">
																						5
																					</button>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<button id="2_down" class="m_arrow"
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
														<td align="center">
															~
														</td>
														<td width="70">
															<table border="0" cellspacing="0" align="left"
																cellpadding="0">
																<tr>
																	<td width="40" nowrap style="background-color: #FFFFFF";>
																		<div class="m_frameborder inputtext">
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_end_time_h" id="end_h2"
																				value="${appRegisterForm.intricacy_app_end_time_h}"
																				onfocus="setFocusObj(this,24);"
																				onkeypress="event.returnValue=IsDigit();">
																			<font color="#000000"><strong>:</strong> </font>
																			<input type="text" class="m_input" maxlength="2"
																				name="intricacy_app_end_time_m" id="end_m2"
																				value="${appRegisterForm.intricacy_app_end_time_m}"
																				onfocus="setFocusObj(this,60);"
																				onkeypress="event.returnValue=IsDigit();">
																		</div>
																	</td>
																	<td>
																		<table border="0" cellspacing="2" cellpadding="1">
																			<tr>
																				<td>
																					<button id="2_up" class="m_arrow"
																						onmouseup="IsInputTimeValue();">
																						5
																					</button>
																				</td>
																			</tr>
																			<tr>
																				<td>
																					<button id="2_down" class="m_arrow"
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
											<td width="34%">

											</td>
										</tr>
										<logic:equal value="Q11" name="appRegisterForm" property="table">
										<tr id="td3">
											<td width="49%" height="30">
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60" nowrap>
															参考班次
														</td>
														<td align="left">
															<hrms:optioncollection name="appRegisterForm"
																property="class_list" collection="list" />
															<html:select name="appRegisterForm" property="class_id"
																styleId="class_id_t" onchange="changeClassID(this);"
																size="1">
																<html:options collection="list" property="dataValue"
																	labelProperty="dataName" />
															</html:select>
														</td>
													</tr>
												</table>
											</td>
										</tr>
										</logic:equal>
										<tr>
											<td height="30" colspan="3" valign="bottom">
												<table width="100%" border="0" cellspacing="0" align="left"
													cellpadding="0">
													<tr>
														<td width="60">
														<logic:equal name="appRegisterForm" property="table" value="Q11">
														  加班日期
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q13">
														  公出日期
														</logic:equal>
														<logic:equal name="appRegisterForm" property="table" value="Q15">
														  请假时日期
														</logic:equal>
														</td>
														<td valign="bottom" 　width="100">
															<select name="app_dates" multiple="multiple"
																ondblclick="removeitem('app_dates');"
																style="height: 70px; width: 200px; font-size: 9pt">
															</select>
														</td>
														<td align="left" width="200">
															<input type="button" class="mybutton" name="dd"
																value='选择日期' onclick="setapp_date();">
																<input type="hidden" value="${appRegisterForm.easy_app_start_date}" onchange='getdatevalue();' id="timejump"/>
															<input type="button" class="mybutton" name="dd"
																value='撤销日期' onclick="removeitem('app_dates');">
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td>
					<table>
						<tr>
							<td>
								<table width="100%" border="0"  align="left" cellpadding="1" cellspacing="0">
									<tr>
										<td width="60" id="app_style">
											<logic:equal name="appRegisterForm" property="table" value="Q11">
													加班类型
										    </logic:equal>
											<logic:equal name="appRegisterForm" property="table" value="Q13">
													公出类型
											</logic:equal>
											<logic:equal name="appRegisterForm" property="table" value="Q15">
													请假类型
											</logic:equal>
										</td>
										<td>
											<hrms:optioncollection name="appRegisterForm"
												property="app_type_list" collection="list" />
											<html:select name="appRegisterForm" property="app_type" size="1" styleId="app_type"
												onchange="getEndDate('0');searchdays();">
												<html:options collection="list" property="dataValue"
													labelProperty="dataName" />
											</html:select>
										</td>
										
										<logic:equal name="appRegisterForm" property="table" value="Q11">
											<logic:notEqual name="appRegisterForm" property="dert_itemid"
												value="">
												<td width="60">
													休息扣除
													<html:hidden name="appRegisterForm" property="dert_itemid"
														styleClass="text" />
												</td>
												<td nowrap>
													<table>
														<tr>
															<td>
																<html:text name="appRegisterForm" styleId='dert_value'
																	property="dert_value" size="4"
																	onkeypress="event.returnValue=IsDigitNegative();" />
																&nbsp;
															</td>
															<td>
																<table border="0" cellspacing="2" cellpadding="0">
																	<tr>
																		<td>
																			<button id="1_up" class="m_arrow"
																				onmouseup="IsInputValue('dert_value');">
																				5
																			</button>
																		</td>
																	</tr>
																	<tr>
																		<td>
																			<button id="1_down" class="m_arrow"
																				onmouseup="IsInputValue('dert_value');">
																				6
																			</button>
																		</td>
																	</tr>
																</table>
															</td>
															<td>
																分钟 &nbsp;&nbsp;
																<font color="red">(加班中间休息时长)</font>
															</td>
														</tr>
													</table>
												</td>
											</logic:notEqual>
										</logic:equal>
									</tr>
								</table>
							</td>
							<td>
								<div id="div" style="display:none">
								<table>
									<tr>
										<td>
											<logic:equal name="appRegisterForm" property="table" value="Q15">
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;假期剩余天数&nbsp;
												<font id="leftdays0fvacation" style="border:0;color:#0066FF;font-weight:bold;" size="2"></font>&nbsp;
												天
											</logic:equal>
										</td>
									</tr>
								</table>
								</div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<logic:equal name="appRegisterForm" property="table" value="Q11">
			<logic:notEmpty name="appRegisterForm" property="appReaCodesetid">
			<html:hidden property="appReaCodesetid" name="appRegisterForm" styleId="appReaCodesetid"/>
			<tr>
				<td>
					<table width="100%" border="0" cellspacing="0" cellpadding="0" 
						align="left">
						<tr>
							<td width="60">
								加班原因
							</td>
							<td align="left">
								<input type="text" name="appReaCodedesc" id="appReaCodedesc"
										size="20" readonly="readonly" disabled="disabled"/>
									<input type="hidden" name="appReaCode" id="appReaCode"/>
									<img src="/images/code.gif"
										onclick='javascript:openInputCodeDialogText("${appRegisterForm.appReaCodesetid}","appReaCodedesc","appReaCode");' />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</logic:notEmpty>
			<logic:notEmpty name="appRegisterForm" property="isExistIftoRest">
				<tr>
					<td>
						<table width="100%" border="0" 
							align="left">
							<tr>
								<td width="60">
									是否调休
								</td>
								<td align="left">
									<html:select property="IftoRest" name="appRegisterForm" value="" size="1">
										<html:option value=""></html:option>
										<html:option value="1">是</html:option>
										<html:option value="2">否</html:option>
									</html:select>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</logic:notEmpty>
			</logic:equal>
			<tr>
				<td>
					<table width="100%" border="0"
						align="left">
						<tr>
							<td width="60">
								<logic:equal name="appRegisterForm" property="table" value="Q11">
										加班事由
							    </logic:equal>
								<logic:equal name="appRegisterForm" property="table" value="Q13">
										公出事由
								</logic:equal>
								<logic:equal name="appRegisterForm" property="table" value="Q15">
										请假事由
								</logic:equal>
							</td>
							<td align="left">
								<html:textarea name="appRegisterForm" property='app_reason'
									styleId="app_reason" cols="55" rows="4" style="width:100%;" 
									styleClass=""/>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td align="center" colspan="2" style="height: 35px;">

					<logic:equal name="appRegisterForm" property="table" value="Q11">
						<hrms:priv func_id="0C341a,27010a">
							<input type="button" class="mybutton" name="dd"
								value="<bean:message key="button.report"/>"
								onclick="input('08');">
						</hrms:priv>
						<hrms:priv func_id="0C341b,27010b">
							<input type="button" class="mybutton" name="dd"
								value="<bean:message key="button.appeal"/>"
								onclick="input('02');">
						</hrms:priv>
						<hrms:priv func_id="270102,0C3412">
							<input type="button" class="mybutton" name="dd"
								value='<bean:message key="button.approve"/>'
								onclick="input('03');">
						</hrms:priv>
					</logic:equal>
					<logic:equal name="appRegisterForm" property="table" value="Q13">
						<hrms:priv func_id="0C343a,27012a">
							<input type="button" class="mybutton" name="dd"
								value="<bean:message key="button.report"/>"
								onclick="input('08');">
						</hrms:priv>
						<hrms:priv func_id="0C343b,27012b">
							<input type="button" class="mybutton" name="dd"
								value="<bean:message key="button.appeal"/>"
								onclick="input('02');">
						</hrms:priv>
						<hrms:priv func_id="270122,0C3432">
							<input type="button" class="mybutton" name="dd"
								value='<bean:message key="button.approve"/>'
								onclick="input('03');">
						</hrms:priv>
					</logic:equal>
					<logic:equal name="appRegisterForm" property="table" value="Q15">
						<hrms:priv func_id="0C342a,27011a">
							<input type="button" class="mybutton" name="dd"
								value="<bean:message key="button.report"/>"
								onclick="input('08');">
						</hrms:priv>
						<hrms:priv func_id="0C342b,27011b">
							<input type="button" class="mybutton" name="dd"
								value="<bean:message key="button.appeal"/>"
								onclick="input('02');">
						</hrms:priv>
						<hrms:priv func_id="270112,0C3422">
							<input type="button" class="mybutton" name="dd"
								value='<bean:message key="button.approve"/>'
								onclick="input('03');">
						</hrms:priv>
					</logic:equal>
					<input type="button" class="mybutton" name="dd" value="批量选人"
						onclick="bacthShiftEmployee();">
					<html:button styleClass="mybutton" property="br_return"
						onclick="closeWin();">
						<bean:message key="button.close" />
					</html:button>

				</td>
			</tr>
		</table>
		<!-- </div> -->
	</html:form>
</body>
<div id="a0101_pnl" style="border-style: none;position:absolute">
	<select name="a0101_box" multiple="multiple" size="10"
		class="dropdown_frame" style="width: 170"
		ondblclick="setSelectValue();isshow();">
	</select>
</div>
<script language="javascript">
setApp_fashion("0");
Element.hide('a0101_pnl');
Element.hide('closee');
<logic:equal value="Q11" name="appRegisterForm" property="table">
	<%
		if(hiddenQ1104)
		{
	%>
		Element.hide('td1');
		Element.hide('day_scope');
		Element.hide('td3');
	<%
		}
	%>
</logic:equal>
</script>