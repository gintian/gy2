<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.kq.options.struts.KqStrutForm"%>
<%@page import="com.hjsj.hrms.utils.*"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hjsj.hrms.utils.FuncVersion" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<bean:define id="rul" name="kqStrutForm" property="holiday_minus_rule"
	type="String"></bean:define>
<%
	String type = "0";
	String[] str = null;
	String[] arr = null;
	if (rul != null && rul.length() > 0) {
		rul = PubFunc.keyWord_reback(rul);
		str = rul.split(",");
		if (str != null && str.length == 7) {
			type = "2";
		} 
		else if(str.length==1 && "1".equals(str[0]))
		{
			type = "1";
		}
	} 
	
	UserView userView = (UserView)request.getSession().getAttribute("userView");
	FuncVersion fv = new FuncVersion(userView);
	//是否可以使用调休加班（调休假）功能
	boolean canUsedLeaveForOT = fv.haveKqLeaveTypeUsedOverTimeFunc();

%>

<style type="text/css">
.m_arrow {
    width: 16px;
    height: 8px;
    font-family: "Webdings";
    font-size: 7px;
    line-height: 2px;
    padding-left: 2px;
    cursor: default;
    float:left
}

.input_number {
    text-align: right;
}
</style>

<script language=JavaScript> 
  function checkradio(obj){
	   if(obj.checked){
		   document.getElementsByName("checkControl_content")[0].disabled=false;
		   document.getElementsByName("checkControl_content")[1].disabled=false;
	   }else{
		   document.getElementsByName("checkControl_content")[0].disabled="true";
		   document.getElementsByName("checkControl_content")[1].disabled="true";
	   }
  }
   
  function saveStruts()
  { 
	  var hashvo=new ParameterSet();
	  <hrms:priv func_id="270304">
	  //考勤参数
     var g_no=$F('kq_g_no');
     var card_no=$F('kq_cardno'); 
         
     if(g_no!=""&&card_no!="")
     {
        if(g_no==card_no)
        {
           alert("考勤卡号与工号不能用同一指标！");
           return false;
        }
     }
    
     var standard_hours = $F('standard_hours');
     if(trim(standard_hours)=="")
     {
        standard_hours = 8;     
     }
  	//var vaa = standard_hours.replace(/[^0-9]+/, ''); 
  	//isNaN(val)   是否为数字 vaa.length != standard_hours.length
 	if (isNaN(standard_hours) || 1-parseFloat(standard_hours)==1){
 		alert("请填入有效的标准工时！");
         return false;
 	}
     
     ///为首钢添加的考勤日期参数
     hashvo.setValue("kq_startdate_field",$F('kq_startdate_field')=="#"?"":$F('kq_startdate_field')); 
     hashvo.setValue("kq_enddate_field",$F('kq_enddate_field')=="#"?"":$F('kq_enddate_field')); 
     hashvo.setValue("dept_changedate_field",$F('dept_changedate_field')=="#"?"":$F('dept_changedate_field'));
	 if (document.getElementById("kq_orgview_post").checked==true) {
     	hashvo.setValue("kq_orgview_post","1");
     } else {
     	hashvo.setValue("kq_orgview_post","0");
     }
	
     hashvo.setValue("standard_hours",standard_hours); //标准工时 
     hashvo.setValue("kq_g_no",$F('kq_g_no'));    
     hashvo.setValue("kq_cardno",$F('kq_cardno'));  
     hashvo.setValue("kq_type",$F('kq_type'));
     //hashvo.setValue("rest_kqclass",$F('rest_kqclass'));
     
     hashvo.setValue("kq_bzindex",$F('kq_bzindex'));
     hashvo.setValue("kq_thbzindex",$F('kq_thbzindex'));

     var pigeonhole_type_ob=document.getElementsByName("pigeonhole_type");
     var pigeonhole_type="0";
     if(pigeonhole_type_ob!=null)
     {
        for(var i=0;i<pigeonhole_type_ob.length;i++)
        {
           if(pigeonhole_type_ob[i].checked==true)
             pigeonhole_type=pigeonhole_type_ob[i].value;
        }
     }
     hashvo.setValue("pigeonhole_type",pigeonhole_type);//归档审批方式
     var up_dailyregister="0";
     var up_dailyregister_ob=document.getElementsByName("up_dailyregister");
     if(up_dailyregister_ob!=null)
     {
        for(var i=0;i<up_dailyregister_ob.length;i++)
        {
           if(up_dailyregister_ob[i].checked==true)
             up_dailyregister=up_dailyregister_ob[i].value;
        }
     }
     hashvo.setValue("up_dailyregister",up_dailyregister);//修改日明细登记数据
     
     var logon_kq_hint_obj = document.getElementById("logon_kq_hint");
     if(logon_kq_hint_obj != null){
         if(logon_kq_hint_obj.checked == true)
             hashvo.setValue("logon_kq_hint","1");
         else
             hashvo.setValue("logon_kq_hint","0");
     }

     var checkControl_status="";
     var checkControl_status_ob=document.getElementById("checkControl_status");
     if(checkControl_status_ob.checked==true)
      checkControl_status='1';
     else
      checkControl_status='0';
  
     var checkControl_content="";
     var checkControl_content_ob=document.getElementsByName("checkControl_content");
     if(checkControl_content_ob!=null)
     {
        for(var i=0;i<checkControl_content_ob.length;i++)
        {
           if(checkControl_content_ob[i].checked==true)
            checkControl_content=checkControl_content_ob[i].value;
        }
     }
     if (document.getElementById("self_accept_month_data").checked==true) {
         hashvo.setValue("self_accept_month_data","1");
       } else {
         hashvo.setValue("self_accept_month_data","0");
       }
     
     hashvo.setValue("checkControl_status",checkControl_status);
     hashvo.setValue("checkControl_content",checkControl_content);
     </hrms:priv>

     <hrms:priv func_id="270305">
     //考勤人员库
     var messi=new Array();
     var mes=$F('messi'); 
     if(mes==null||mes.length<=0)
     {
        messi[0]="";       
     } else if ("string"==typeof mes) {
    	 messi[0] = mes;
     } else {
         messi = mes;
     }
     hashvo.setValue("messi",messi);
     </hrms:priv>

     <hrms:priv func_id="270306">
     //参考指标
     var parmes=$F('par_mes');
     if(parmes==null||parmes.length<=0)
     {
        parmes=new Array();
        parmes[0]=""; 
     }   
     hashvo.setValue("par_mes",parmes);
     </hrms:priv>

     <hrms:priv func_id="270302">
     //项目统计
     var statq03=$F('par_stat_q03');
     if(statq03==null||statq03.length<=0)
     {
        statq03=new Array();
        statq03[0]=""; 
     }
     hashvo.setValue("stat_q03",statq03);
     </hrms:priv>

     <hrms:priv func_id="270307">
     //假期管理
     var holimes=$F('holi_mes');
     if(holimes==null||holimes.length<=0)
     {
        holimes=new Array();
        holimes[0]="";
     }        
     if(holimes.indexOf("06")==-1)
     {
        alert('<bean:message key="kq.feast.manage.must.select"/>');
        
        return false;
     } 
     hashvo.setValue("holi_mes",holimes);
     
     // 假期扣减规则     
     var radio1 = document.getElementById("radio1");
     var radios = document.getElementById("radios");
     if (radio1.checked == true) {
     	hashvo.setValue("holiday_minus_rule","0");
     } else if(radios.checked == true)
     {
    	 hashvo.setValue("holiday_minus_rule","1");
         }
     else{
     	if (!vali("param1")) {
     		return false;
     	} else if (!vali("param2")||!opinionHolirule2("param2")) {
     		return false;
     	} else if (!vali("param3")||!opinionHolirule("param1","param3",0)) {
     		return false;
     	} else if (!vali("param4")||!opinionHolirule("param3","param4",1)) {
     		return false;
     	} else if (!vali("param5")||!opinionHolirule2("param5")) {
     		return false;
     	} else if (!vali("param6")||!opinionHolirule("param4","param6",0)) {
     		return false;
     	} else if (!vali("param7")||!opinionHolirule2("param7")) {
     		return false;
     	} else {
     		var str = $F("param1") + "," + $F("param2") + ";" + $F("param3") + "," + $F("param4") + ","+ $F("param5") +";" + $F("param6") + "," + $F("param7");
     		hashvo.setValue("holiday_minus_rule",str);
     	}
     }
     </hrms:priv>

     <hrms:priv func_id="270308">   
   //节假日有排班算作加班;0:不算加班；1：算加班
     var overtime_hol= document.getElementById("overtime_hol");
     var overtime_hol_mode="0";
     if(overtime_hol.checked==true)
     {
        overtime_hol_mode="1";
     }else
     {
        overtime_hol_mode="0";
     }
     hashvo.setValue("overtime_hol",overtime_hol_mode);
     
     var status_vos= document.getElementById("over_status");
     var over_status="0";
     if(status_vos.checked==true)
     {
        over_status="1";
     }else
     {
       over_status="0";
     }   
     status_vos= document.getElementById("leave_status");
     var leave_status="0";
     if(status_vos.checked==true)
     {
        leave_status="1";
     }else
     {
       leave_status="0";
     } 
     status_vos= document.getElementById("leave_rule_late_status");
     var leave_rule_late_status="0";
     if(status_vos.checked==true)
     {
        leave_rule_late_status="1";
     }else
     {
       leave_rule_late_status="0";
     }

     if(leave_rule_late_status=="1"&&leave_status=="1")
     {
        alert("请假登记天数不能既有提前天数又有最迟天数，只能选一个！");
        return false;
     }

     if("1" == over_status && $F('over_rule').length > 0){
         if("0" != $F('over_rule') && !checkIsIntNum($F('over_rule'))){
             alert("加班最迟登记天数需为有效的整数！");
             return false;
         }
     }
     hashvo.setValue("over_rule",$F('over_rule'));
     hashvo.setValue("over_status",over_status); 
     hashvo.setValue("leave_status",leave_status);
     if("1" == leave_status && $F('leave_rule').length > 0){
         if("0" != $F('leave_rule') && !checkIsIntNum($F('leave_rule'))){
             alert("请假提前登记天数需为有效的整数！");
             return false;
         }
     }
     hashvo.setValue("leave_rule",$F('leave_rule'));  
     hashvo.setValue("leave_rule_late_status",leave_rule_late_status); 
     if("1" == leave_rule_late_status && $F('leave_rule_late').length > 0){
         if("0" != $F('leave_rule_late') && !checkIsIntNum($F('leave_rule_late'))){
             alert("请假最迟登记天数需为有效的整数！");
             return false;
         }
     }

     hashvo.setValue("leave_rule_late",$F('leave_rule_late'));

     var opinion_overtime_type_ob =document.getElementsByName("opinion_overtime_type");  
     var opinion_overtime_type="1";  
     if(opinion_overtime_type_ob!=null)
     {
        for(var i=0;i<opinion_overtime_type_ob.length;i++)
        {
           if(opinion_overtime_type_ob[i].checked==true)
             opinion_overtime_type=opinion_overtime_type_ob[i].value;
        }
     }  
     var rest_overtime_time_ob =document.getElementsByName("rest_overtime_time");  
     var rest_overtime_time="1";  
     if(rest_overtime_time_ob!=null)
     {
        for(var i=0;i<rest_overtime_time_ob.length;i++)
        {
           if(rest_overtime_time_ob[i].checked==true)
             rest_overtime_time=rest_overtime_time_ob[i].value;
        }
     }

     if($F('min_overtime').length > 0 && "0" != $F('min_overtime') && !checkIsIntNum($F('min_overtime'))){
         alert("加班时间不少于X分钟需为有效的整数！");
         return false;
     }
     if($F('overtime_max_limit').length>0 && "0" != $F('overtime_max_limit') && !checkIsIntNum($F('overtime_max_limit'))) {
         alert("加班最大限额需为有效的整数！");
         return false;
     }
     hashvo.setValue("min_overtime",$F('min_overtime')); //加班时间不少于X分钟     
     hashvo.setValue("DURATION_OVERTIME_MAX_LIMIT", $F('overtime_max_limit')); //期间加班最大限额（小时/期间）

     //已批申请登记数据是否可以删除
     var approved_delete= document.getElementById("approved_delete");
     var approved_delete_mode="0";
     if(approved_delete.checked==true)
     {
        approved_delete_mode="1";
     }else
     {
        approved_delete_mode="0";
     }
     hashvo.setValue("approved_delete",approved_delete_mode); //已批申请登记数据是否可以删除 0：不删除 1：删除
     
     //已批申请登记数据是否可以删除
     var officeleave_enable_leave_overtime = document.getElementById("officeleave_enable_leave_overtime");
     var officeleave_enable_leave_overtime_mode="0";
     if(officeleave_enable_leave_overtime.checked==true)
     {
    	 officeleave_enable_leave_overtime_mode="1";
     }else
     {
    	 officeleave_enable_leave_overtime_mode="0";
     }
     hashvo.setValue("officeleave_enable_leave_overtime",officeleave_enable_leave_overtime_mode); //已批申请登记数据是否可以删除 0：不删除 1：删除
     </hrms:priv>

     <hrms:priv func_id="270309">
     status_vos= document.getElementById("repair_card_status");
     var repair_card_status="0";
     if(status_vos.checked==true)
     {
        repair_card_status="1";
     }else
     {
       repair_card_status="0";
     }
     var restleave_type_ob=document.getElementsByName("restleave_type");
     var restleave_type="2";
     if(restleave_type_ob!=null)
     {
        for(var i=0;i<restleave_type_ob.length;i++)
        {
           if(restleave_type_ob[i].checked==true)
             restleave_type=restleave_type_ob[i].value;
        }
     }
	 var flextime_ruler_ob=document.getElementsByName("flextime_ruler");
	 var flextime_ruler="0";
	 if(flextime_ruler_ob != null){
	 	for(var j=0;j<flextime_ruler_ob.length;j++){
	 		if(flextime_ruler_ob[j].checked == true)
	 			flextime_ruler = flextime_ruler_ob[j].value;
	 		
	 	}
	 }
     
     var check_inout_match_obj=document.getElementsByName("check_inout_match");
     var check_inout_match="";
     if(check_inout_match_obj!=null)
     {
        for(var i=0;i<check_inout_match_obj.length;i++)
        {
           if(check_inout_match_obj[i].checked==true)
             check_inout_match=check_inout_match_obj[i].value;
        }
     }
     
     hashvo.setValue("restleave_type",restleave_type);
	 hashvo.setValue("flextime_ruler",flextime_ruler);
      
     hashvo.setValue("repair_card_status",repair_card_status);
     if("1" == repair_card_status && $F('repair_card_num').length > 0){
	     if("0" != $F('repair_card_num') && !checkIsIntNum($F('repair_card_num'))){
		 	 alert("考勤期间补刷卡次数需为有效的整数！");
		 	 return false;
	     }
     }
     hashvo.setValue("repair_card_num",$F('repair_card_num')); 

     status_vos= document.getElementById("magcard_flag");
     var magcard_flag="0";
     
     if(status_vos!=null&&status_vos.checked==true)
     {
        magcard_flag="1";
     }else
     {
       magcard_flag="0";
     }
     hashvo.setValue("magcard_cardid",$F('magcard_cardid'));
     hashvo.setValue("magcard_flag",magcard_flag);
     hashvo.setValue("magcard_setid",$F('magcard_setid'));
     hashvo.setValue("magcard_com",$F('magcard_com'));
     
     var cardearly =document.getElementById("cardearly").value;
     if(cardearly>"720")
     {
     	cardearly="720"
     }
     hashvo.setValue("cardearly",cardearly);//提前多少分钟算作早到
     
     var str = $F('card_interval');
     if(str.indexOf(".")>-1)
     {
     	alert("重复刷卡间隔,请输入整数");
     	return false;
     }
     hashvo.setValue("rest_overtime",rest_overtime_time);
     hashvo.setValue("opinion_overtime_type",opinion_overtime_type);
     if("0" != $F('min_mid_leave_time') && !checkIsIntNum($F('min_mid_leave_time')) 
    		 && $F('min_mid_leave_time').length >0 && "0" == check_inout_match){
	 	 alert("单次进出小于等于X分钟不计离岗时长需为有效的整数！");
	 	 return false;
     }
     hashvo.setValue("min_mid_leave_time",$F('min_mid_leave_time'));   
     if($F('card_interval').length > 0 && "0" != $F('card_interval') && !checkIsIntNum($F('card_interval'))){
	 	 alert("重复刷卡间隔需为有效的整数！");
	 	 return false;
     }
     hashvo.setValue("card_interval",$F('card_interval')); //重复刷卡间隔

     if(cardearly.length > 0 && "0" != cardearly && !checkIsIntNum(cardearly)){
         alert("提前多少分钟算作早到需为有效的整数！");
         return false;
     }
     
     hashvo.setValue("check_inout_match",check_inout_match);////刷卡匹配情况
     hashvo.setValue("approve_relation",$F('approve_relation'));//补刷卡审批关系
     hashvo.setValue("card_causation",$F('card_causation'));//补刷卡原因代码项
     </hrms:priv>

     <hrms:priv func_id="27030e">
     hashvo.setValue("need_busicompare",$F('need_busicompare'));//业务申请是否与实际刷卡作比对
     hashvo.setValue("busi_cardbegin",$F('busi_cardbegin'));//刷卡开始时间最早从申请起始时间前X分钟起
     hashvo.setValue("busi_cardend",$F('busi_cardend'));//刷卡开始时间最早从申请起始时间前X分钟止
     hashvo.setValue("busifact_diff",$F('busifact_diff'));//申请时长小于刷卡时长超过X分钟计为异常 
     hashvo.setValue("busi_morethan_fact",$F('busi_morethan_fact'));//申请时长大于刷卡时长超过X分钟计为异常

     if(document.getElementById("leave_need_check")!=null){
         var obj = document.getElementById("leave_need_check");
         if(obj.checked == true){
             hashvo.setValue("leave_need_check",'1'); //需要比对请假申请
         }else{
             hashvo.setValue("leave_need_check",'0'); //无需比对请假申请
         }
      }
      hashvo.setValue("leave_compare_rule",$F('leave_compare_rule'));
      if(document.getElementById("leave_updata_data")!=null){
         var obj = document.getElementById("leave_updata_data");
         if(obj.checked == true){
             hashvo.setValue("leave_updata_data",'1'); //自动修正数据
         }else{
             hashvo.setValue("leave_updata_data",'0'); //手动修正数据
         }
       }
      if(document.getElementById("overtime_need_check")!=null){
         var obj = document.getElementById("overtime_need_check");
         if(obj.checked == true){
             hashvo.setValue("overtime_need_check",'1'); //需要比对加班申请
         }else{
             hashvo.setValue("overtime_need_check",'0'); //无需比对加班申请
         }
      }
      hashvo.setValue("overtime_compare_rule",$F('overtime_compare_rule'));
      if(document.getElementById("overtime_updata_data")!=null){
         var obj = document.getElementById("overtime_updata_data");
         if(obj.checked == true){
             hashvo.setValue("overtime_updata_data",'1'); //自动修正数据
         }else{
             hashvo.setValue("overtime_updata_data",'0'); //手动修正数据
         }
      }
      if(document.getElementById("officeleave_need_check")!=null){
             var obj = document.getElementById("officeleave_need_check");
         if(obj.checked == true){
             hashvo.setValue("officeleave_need_check",'1'); //需要比对公出申请
         }else{
             hashvo.setValue("officeleave_need_check",'0'); //无需比对公出申请
         }
      }
      hashvo.setValue("officeleave_compare_rule",$F('officeleave_compare_rule'));
      if(document.getElementById("officeleave_updata_data")!=null){
         var obj = document.getElementById("officeleave_updata_data");
         if(obj.checked == true){
             hashvo.setValue("officeleave_updata_data",'1'); //自动修正数据
         }else{
             hashvo.setValue("officeleave_updata_data",'0'); //手动修正数据
         }
      }
     </hrms:priv>

     <hrms:priv func_id="27030b">
     //数据处理模式
     var data_processing="0";
     var data_processing_ob=document.getElementsByName("data_processing");
     if(data_processing_ob!=null)
     {
        for(var i=0;i<data_processing_ob.length;i++)
        {
           if(data_processing_ob[i].checked==true)
             data_processing=data_processing_ob[i].value;
        }
     }
     hashvo.setValue("data_processing",data_processing); //数据处理模式
     
     //启用精简处理方式 0：原处理方式 1：首钢特殊处理方式
     var quick_analyse= document.getElementById("quick_analyse_mode");
     var quick_analyse_mode="0";
     if(quick_analyse.checked==true)
     {
        quick_analyse_mode="1";
     }else
     {
        quick_analyse_mode="0";
     }
     hashvo.setValue("quick_analyse_mode",quick_analyse_mode); //启用精简处理方式 0：原处理方式 1：首钢特殊处理方式
     </hrms:priv>

     <hrms:priv func_id="27030a">
     //网上签到
     var net_sign_check_ip_ob=document.getElementsByName("net_sign_check_ip");
     var net_sign_check_ip="1";
     if(net_sign_check_ip_ob!=null)
     {
        for(var i=0;i<net_sign_check_ip_ob.length;i++)
        {
           if(net_sign_check_ip_ob[i].checked==true)
             net_sign_check_ip=net_sign_check_ip_ob[i].value;
        }
     }
     hashvo.setValue("net_sign_check_ip",net_sign_check_ip);
     
  	 var net_sign= document.getElementById("net_sign_approve");
     var net_sign_approve="0";
     if (net_sign){
	     if(net_sign.checked==true)
	     {
	        net_sign_approve="1";
	     }else
	     {
	       net_sign_approve="0";
	     }
	 }
     hashvo.setValue("net_sign_approve",net_sign_approve); //网上签到 签到签退数据需审批 0：无需审批 1：需要审批
     </hrms:priv>

     <hrms:priv func_id="270303">
     //考勤薄
     var kqcard_q03=$F('kqcard_q03_ip');
     if(kqcard_q03==null||kqcard_q03.length<=0)
     {
        kqcard_q03=new Array();
        kqcard_q03[0]=""; 
     }
     hashvo.setValue("kqcard_q03",kqcard_q03);
     </hrms:priv>
     
     <hrms:priv func_id="27030d">
     //加班扩展
     if(document.getElementById("turn_enable")!=null){
     	var turn_enable = document.getElementById("turn_enable");
	    if(turn_enable.checked == true){
	     	hashvo.setValue("turn_enable",'1'); //休息日转加班 启用
	    }else{
	     	hashvo.setValue("turn_enable",'0'); //休息日转加班 禁用
	    }
     }
     
     var turn_charge = document.getElementsByName("turn_charge");
     for(var i = 0;i < turn_charge.length;i++){
     	if(turn_charge[i].checked == true){
     		hashvo.setValue("turn_charge",turn_charge[i].value); //休息日转加班
     	}
     }

     <% if(canUsedLeaveForOT) { %>
     var overtimeToOffs = document.getElementsByName("overtimeToOff");
     var overtimeToOff ="";
     for(var j = 0;j<overtimeToOffs.length;j++){
     	if(overtimeToOffs[j].checked == true)
     		overtimeToOff = overtimeToOff + overtimeToOffs[j].value + ","
     }
     hashvo.setValue("overtimeToOff",overtimeToOff);
	 hashvo.setValue("vacationToOff",$F('vacationToOff'));
	 if(overtimeToOff.length > 0 && $F('vacationToOff').length > 0 
			 && !checkIsIntNum($F('validityTime'))){
	 	alert("调休有效期限需为有效的整数！");
	 	return false;
	 }
	 hashvo.setValue("validityTime",$F('validityTime'));
	 
	 hashvo.setValue("overtimeForLeaveCycle",$F('overtimeForLeaveCycle'));
	 if(overtimeToOff.length > 0 && $F('overtimeForLeaveMaxHour').length > 0 
			 && "0" != $F('overtimeForLeaveMaxHour') && !checkIsIntNum($F('overtimeForLeaveMaxHour'))){
        alert("未调休加班限额需为有效的整数！");
        return false;
	 }
	 hashvo.setValue("overtimeForLeaveMaxHour", $F('overtimeForLeaveMaxHour'));
	 <% } %>
	 	     
     var turn_tlong = document.getElementsByName("turn_tlong");
     for(var i = 0;i < turn_tlong.length;i++){
     	if(turn_tlong[i].checked == true){
     		hashvo.setValue("turn_tlong",turn_tlong[i].value); 
     	}
     }
     if(document.getElementById("turn_time")==null)
     	return;
     var turn_time = document.getElementById("turn_time");
     hashvo.setValue("turn_time",turn_time.value); 
     
     if(document.getElementById("turn_classid") == null)
     	return;
     var turn_classid = document.getElementById("turn_classid");
     hashvo.setValue("turn_classid",turn_classid.value);
     var turn_appdoc = document.getElementsByName("turn_appdoc");
     for(var i = 0;i < turn_appdoc.length;i++){
     	if(turn_appdoc[i].checked == true){
     		hashvo.setValue("turn_appdoc",turn_appdoc[i].value);
     	}
     }
     </hrms:priv>
     
     //考勤同步
     var sync_ojb= document.getElementById("sync_carddata");     
     if(sync_ojb.value=="true")
     {
         hashvo.setValue("sync_table",$F('sync_table')); //同步表名称
         hashvo.setValue("sync_base",$F('sync_base')); //同步库名称
         hashvo.setValue("sync_post",$F('sync_post')); //同步库端口
         hashvo.setValue("sync_url",$F('sync_url')); //同步库地址
         hashvo.setValue("sync_basetype",$F('sync_basetype'));
         hashvo.setValue("sync_pass",$F('sync_pass'));
         hashvo.setValue("sync_user",$F('sync_user'));
     }
     
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'15205110001'},hashvo);
}	
   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        alert("编辑成功");        
     }else
     {
        alert("编辑失败");
     }     
   } 
   function opinionHolirule2(name1)
   {
       var obj1 = document.getElementsByName(name1);
       if(obj1)
       {
          var value1=obj1[0].value;
          value1=parseFloat(value1);
          if(value1>1||value1<=0)
          {
             alert("假期管理中假期扣减规则中定义天数不能小于等于0或大于1天!");              
     		 return false;
          }
       }
       return true;
   }
   function opinionHolirule(name1,name2,flag)
   {
      var obj1 = document.getElementsByName(name1);
      var obj2 = document.getElementsByName(name2);
      if(obj1&&obj2)
      {
         var value1=obj1[0].value;
         var value2=obj2[0].value;
         value1=parseFloat(value1);
         value2=parseFloat(value2);
         if(flag==1)
         {
           if(value1>=value2)
           {
              alert("假期管理中假期扣减规则中前面参数值不能大于等于后面的参数值");              
     		  return false;
           }
         }else
         {
            if(value1>value2)
            {
              alert("假期管理中假期扣减规则中前面参数值不能大于后面的参数值");
              obj1.focus;
     		  return false;
            }
         }
         
      }
      return true;
   }
   function vali(param) {
   	var param1 = document.getElementsByName(param)[0].value;
     	if (trim(param1) == "" || isNaN(trim(param1))) {
     		alert("假期扣减规则中参数不能为空，并且参数必须为数字");
     		return false;
     	} 
     	
     	return true;
   } 

   //实数
   function IsDigit() 
   { 
       var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
       return ((keyCode >= 46) && (keyCode <= 57) && (keyCode != 47) ); 
   }
   
   //整数
   function IsDigits() 
   { 	     
	   var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
       return ((keyCode >= 48) && (keyCode <= 57)); 
   } 
   
   function IsInputValue(textid) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if (!fObj) return;		
		
		var cmd = event.srcElement.innerText=="5"?true:false;
		if(fObj.value==null||fObj.value.length<=0)
		  fObj.value=0;
		  
		var maxlen = fObj.maxLength;		
		var radix = Math.pow(10,maxlen)-1;	
	
		var i = parseInt(fObj.value,10);
		if (i==radix&&cmd) {
			i = 0;
		} else if (i==0&&!cmd) {
			i = radix;
		} else {
			cmd?i++:i--;
		}		
		fObj.value = i;
		fObj.select();
   } 
   function imports()
    {
        var fileEx = kqStrutForm.importfile.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }
        if(!validateUploadFilePath(fileEx))
        {
			alert("请选择正确的文件！");
			return;
        }
        var template_type=document.getElementById("template_type").value;        
        if(template_type=="Q11")
        {
           if(!confirm("确定上传文件是加班类型的吗？"))
           {
              return false;
           }
        }else if(template_type=="Q13")
        {
           if(!confirm("确定上传文件是公出类型的吗？"))
           {
              return false;
           }
        }else if(template_type=="Q15")
        {
           if(!confirm("确定上传文件是请假类型的吗？"))
           {
              return false;
           }
        }else
        {
           alert("选择模板类型有误！");
        }
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='doc')
    	{
   			kqStrutForm.action="/kq/options/struts/select_parameter.do?b_upload=link&tab_name=tab8";
            kqStrutForm.submit();
    	}
    	else
    	{
    		alert("导入的文件必须为word格式");
    	}
    }
    
    function test(name)
    {
      if(name!=null&&name!=""&&name!="undefined"&&name.length>0)
      {
        var obj=$('pageset');
        obj.setSelectedTab(name);
      }
      changeview();
      changeOtForLeaveCycle();
    }
    function deleteTemplate(b)
    {
       var name= document.getElementById("IDs_"+b).value;           
       if(confirm("确定删除此模板?"))
       {
          kqStrutForm.action="/kq/options/struts/select_parameter.do?b_deleteT=link&tname="+name+"&tab_name=tab8";
          kqStrutForm.submit();
       }
    } 
    
    function clickchange() {
    	var obj = document.getElementById("radio1");
    	var obj2 = document.getElementById("radios"); //
    	if (obj.checked == true ||obj2.checked == true) {
    		for (i = 1; i <= 7; i++) {
    			var ob = document.getElementsByName("param"+i)[0];  
    			ob.style.backgroundColor="#f4f7f7"; 
    			ob.value="";			
    			ob.disabled=true;
    		}
    	} else {
    		for (i = 1; i <= 7; i++) {
    			var ob = document.getElementsByName("param"+i)[0];
    			ob.style.backgroundColor="white"; 
    			ob.disabled=false;
    		}
    	
    	}
    	
    }


function changeview(){
	//休息日转加班
	<hrms:priv func_id="27030d">
	var flag = "";
	var turn_charge = document.getElementsByName("turn_charge");
	
	for(var n = 0;n<turn_charge.length;n++){
		if(turn_charge[n].checked == true){
			flag = turn_charge[n].value;
		}
	}
	var tr_turn_tlong_2 = document.getElementById("tr_turn_tlong_2");
	var addApp = document.getElementById("tr_turn_addApp");

	var turn_tlong = document.getElementsByName("turn_tlong");
	var value;
	for(var i =0; i<turn_tlong.length;i++){
		if(turn_tlong[i].checked == true){
			value = turn_tlong[i].value;
		}
	}
	 
	if(flag == 1){
		tr_turn_tlong_2.style.display = "none";
		addApp.style.display="none";
		var turn_tlong = document.getElementsByName("turn_tlong");
		for(var i = 0;i<turn_tlong.length;i++){
			if(turn_tlong[i].checked == true && turn_tlong[i].value == 0){
				for(var j = 0;j<turn_tlong.length;j++){
					if(turn_tlong[j].value == 2){
						turn_tlong[j].checked = true;
					}
				}
			}
		}
	}else if(flag == 0 && value == 2){
		document.getElementById("tr_turn_tlong_2").style.display = "";
		document.getElementById("tr_turn_addApp").style.display="none";
	}else{
		document.getElementById("tr_turn_tlong_2").style.display = "";
		document.getElementById("tr_turn_addApp").style.display="";
	}
	</hrms:priv>

	//进出刷卡分析离岗时间
	<hrms:priv func_id="270309">
	var obj = $F('check_inout_match');
	if(obj[0] == "1" )
		document.getElementById("leave_time").style.display = "none";
	else
		document.getElementById("leave_time").style.display = "block";
    </hrms:priv>

	//申请比对
	<hrms:priv func_id="27030e">
	var o1 = $F('leave_compare_rule');
	if(o1 == "0"){
		document.getElementById("leave_updata_data").checked = "";
		document.getElementById("leave_updata_data").disabled = "disabled";
	}else{
		document.getElementById("leave_updata_data").disabled = "";
	}

	var o2 = $F('overtime_compare_rule');
	if(o2 == "0"){
		document.getElementById("overtime_updata_data").checked = "";
		document.getElementById("overtime_updata_data").disabled = "disabled";
	}else{
		document.getElementById("overtime_updata_data").disabled = "";
	}

	var o3 = $F('officeleave_compare_rule');
	if(o3 == "0"){
		document.getElementById("officeleave_updata_data").checked = "";
		document.getElementById("officeleave_updata_data").disabled = "disabled";
	}else{
		document.getElementById("officeleave_updata_data").disabled = "";
	}
	</hrms:priv>
		
}

function tuck_application(){
	 var turn_charge = document.getElementsByName("turn_charge");
	 var flag;
	 if (null == turn_charge) 
	     return;
	 if ("undefined" == turn_charge)
	     return;
	 if (0 == turn_charge.length)
	     return;	
	 for(var n = 0;n<turn_charge.length;n++){
		if(turn_charge[n].checked == true){
			flag = turn_charge[n].value;
		}
	 }
	 
	 var turn_tlong = document.getElementsByName("turn_tlong");
	 if(turn_tlong == null)
		 return;
	 if(turn_tlong == "undefined")
		 return;
	 if(turn_tlong.length == 0)
		 return;
	 var value;
	 for(var i =0; i<turn_tlong.length;i++){
		 if(turn_tlong[i].checked == true){
			 value = turn_tlong[i].value;
		 }
	 }
	 
	 var application = document.getElementById("tr_turn_addApp");
	 if(value == 2){
		 application.style.display = "none";
		 var turn_appdoc = document.getElementsByName("turn_appdoc");
		 turn_appdoc[0].checked = true;
	 }else{
		 if(flag == 0)
			 application.style.display = "";
		 if(flag == 1)
			 application.style.display = "none";
	 }
}
//提交时候 验证 休息日转加班 数据是否规范
function turn_v(){
	var turn_tlong = document.getElementsByName("turn_tlong");
	for(var i = 0;i<turn_tlong.length;i++){
		if(turn_tlong[i].checked == true){
			if(turn_tlong[i].value == 2){
				var turn_time = document.getElementById("turn_time");
				if(!checkIsNum(turn_time.value)){
					return false;
				}
			}else if(turn_tlong[i].value == 1){
				var turn_classid = document.getElementById("turn_classid");
				if(turn_classid.value == '#'){
					return false;
				}
			}else{
				return true;
			}
		}
	}
	return true;
}

function standard_hourscheck()
{
    var standard_hours = $F('standard_hours');
    if(trim(standard_hours)=="")
    {
       standard_hours = 8;     
    }
 	//var vaa = standard_hours.replace(/[^0-9]+/, ''); 
 	//isNaN(val)   是否为数字 vaa.length != standard_hours.length
	if (isNaN(standard_hours) || 1-parseFloat(standard_hours)==1){
		alert("请填入有效的标准工时！");
        return false;
	}
  
}

function hiddenLeaveTime(){
	var obj = $F('check_inout_match');
	if(obj[0] == "1" )
		document.getElementById("leave_time").style.display = "none";
	else
		document.getElementById("leave_time").style.display = "block";
}

function changeOtForLeaveCycle() {
	<% if(canUsedLeaveForOT) { %>
	var validityTime = document.getElementById('validityTime');
	if(!validityTime)
		return;
	
	if(!document.getElementById('validityTimeTd'))
		return;
	
	var otForLeaveCycle = $F('overtimeForLeaveCycle');
	if(otForLeaveCycle=="0" || otForLeaveCycle=="4") {
	  document.getElementById('validityTimeTd').style.display = "block";
	  if(otForLeaveCycle == "0")
		  document.getElementById('validityTimeUnit').innerText = "天";
	  else
		  document.getElementById('validityTimeUnit').innerText = "月";
	} else {
		document.getElementById('validityTimeTd').style.display = "none";
	}
	<% } %>
}
//-->
</script>
<body onload="test('${kqStrutForm.tab_name}');" onpaste="return false">
	<html:form action="/kq/options/struts/select_parameter"
		enctype="multipart/form-data" style="height:100%;">

		<hrms:tabset name="pageset" height="580" type="false">
			<hrms:tab name="tab1" label="考勤参数" visible="true" function_id="270304">
				<table width="90%" align="center">
					<tr>
						<td  valign="top">
							<br>
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>
												<bean:message key="kq.strut.param" />
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td align="left">
														<table width="100%">
															<tr>
																<td align="right" width="70" nowrap>
																	<bean:message key="kq.strut.gno" />
																</td>
																<td align="left" nowrap valign="left" width="135">
																	<html:select name="kqStrutForm" property="kq_g_no"
																		size="1">
																		<html:optionsCollection property="nlist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
																<td width="50"></td>
																<td align="left" nowrap valign="middle">
																	考勤开始日期
																	<html:select name="kqStrutForm"
																		property="kq_startdate_field" size="1">
																		<html:optionsCollection property="kq_datelist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
															</tr>
															<tr>
																<td align="right" nowrap valign="middle">
																	<bean:message key="kq.strut.cno" />
																</td>
																<td align="left" nowrap valign="middle">
																	<html:select name="kqStrutForm" property="kq_cardno"
																		size="1">
																		<html:optionsCollection property="nlist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
																<td width="50"></td>
																<td align="left" nowrap valign="middle">
																	考勤结束日期
																	<html:select name="kqStrutForm"
																		property="kq_enddate_field" size="1">
																		<html:optionsCollection property="kq_datelist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
															</tr>
															<tr>
																<td align="right" nowrap valign="middle">
																	<bean:message key="kq.strut.expr" />
																</td>
																<td align="left" nowrap valign="middle">
																
																	<html:select name="kqStrutForm" property="kq_type"
																		size="1">
																		<html:optionsCollection property="tlist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
																<td width="50"></td>
																<td align="left" nowrap valign="middle">
																部门变动日期
																	<html:select name="kqStrutForm"
																		property="dept_changedate_field" size="1">
																		<html:optionsCollection property="kq_datelist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
															</tr>
															<tr>
																<td align="right" nowrap valign="middle">
																	班组指标
																</td>
																<td align="left" nowrap valign="middle">
																	<html:select name="kqStrutForm" property="kq_bzindex"
																		size="1">
																		<html:optionsCollection property="bzlist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
																<td width="50"></td>
																<td align="left" nowrap valign="middle" colspan="2">
																	<logic:equal name="kqStrutForm"	property="kq_orgview_post" value="1">                      	
                        											<input type="checkbox" id="kq_orgview_post" name="kq_orgview_post"
																			checked="checked" value="1" />
                        											</logic:equal>
																	<logic:notEqual name="kqStrutForm" property="kq_orgview_post" value="1">                      	
                        											<input type="checkbox" id="kq_orgview_post" name="kq_orgview_post" 
                        													value="1" />
                        											</logic:notEqual>
                        											机构树不显示岗位
																</td>

															</tr>
															<tr>
																<td align="right" nowrap valign="middle">
																	考勤部门
																</td>
																<td align="left" nowrap valign="middle">
																	<html:select name="kqStrutForm" property="kq_thbzindex"
																		size="1">
																		<html:optionsCollection property="thlist"
																			value="dataValue" label="dataName" />
																	</html:select>
																</td>
																<td width="50"></td>
																<td align="left" nowrap valign="middle">
																	<logic:equal name="kqStrutForm"
																		property="logon_kq_hint" value="1">
																		<input type="checkbox" name="logon_kq_hint"
																			value="1" checked="checked">
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm" 
																		property="logon_kq_hint" value="1">
																		<input type="checkbox" name="logon_kq_hint" value="0">
																	</logic:notEqual>
																	登录时进行刷卡、请假和公出申请提醒
																</td>
															</tr>
															<tr>
																<td align="right" nowrap valign="middle">
																	标准工时
																</td>
																<td align="left" nowrap valign="middle">
																	<html:text name="kqStrutForm" property="standard_hours" styleId="standard_hours" 
																		size="5" onkeypress="event.returnValue=IsDigit();" onblur="standard_hourscheck()"
																		 maxlength="4" styleClass="textborder common_border_color input_number"></html:text>
																	&nbsp;小时
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
									<td>
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												归档审批方式
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30" align="left" nowrap>
														&nbsp; &nbsp;
														<html:radio name="kqStrutForm" property="pigeonhole_type"
															value="0" />
														审批方式归档
														<html:radio name="kqStrutForm" property="pigeonhole_type"
															value="1" />
														非审批方式归档

													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												是否允许修改日明细登记数据
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30" align="left" nowrap>
														&nbsp; &nbsp;
														<html:radio name="kqStrutForm" property="up_dailyregister"
															value="0" />
														允许
														<html:radio name="kqStrutForm" property="up_dailyregister"
															value="1" />
														不允许
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<br>
									</td>
								</tr>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>
												员工月汇总数据审核控制
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0" >
												<tr>
												  <td height="30" >
												         &nbsp;&nbsp;
												          <logic:equal value="1" name="kqStrutForm" property="checkControl_status">
												          <input type="checkbox" name="checkControl_status" id="checkControl_status"  checked="checked" onclick="checkradio(this)"/>
												       </logic:equal>
												       <logic:notEqual value="1" name="kqStrutForm" property="checkControl_status">
												          <input type="checkbox" name="checkControl_status" id="checkControl_status"  onclick="checkradio(this)"/>
												       </logic:notEqual>
												                    是否进行数据审核控制
												        &nbsp; &nbsp;
                                                                                                                                                    控制方式
                                                        &nbsp; &nbsp;
                                                          <logic:equal value="1" name="kqStrutForm" property="checkControl_status">
                                                            <html:radio name="kqStrutForm" property="checkControl_content"
                                                                value="0" />
                                                                                                                                                        强制控制
                                                            <html:radio name="kqStrutForm" property="checkControl_content"
                                                                value="1" />
                                                                                                                                                        预警提示 
                                                          </logic:equal>
                                                           <logic:notEqual value="1" name="kqStrutForm" property="checkControl_status">
                                                               <html:radio name="kqStrutForm" property="checkControl_content"
                                                                value="0" disabled="true"/>
                                                                                                                                                        强制控制
                                                            <html:radio name="kqStrutForm" property="checkControl_content"
                                                                value="1" disabled="true"/>
                                                                                                                                                        预警提示 
                                                           </logic:notEqual>
												</tr>
												<tr>
												  <td height="30" >
												        &nbsp;&nbsp;
                                                         <logic:equal name="kqStrutForm"    property="self_accept_month_data" value="1">                        
                                                                <input type="checkbox" id="self_accept_month_data" name="self_accept_month_data"
                                                                checked="checked" value="1" />
                                                        </logic:equal>
                                                        <logic:notEqual name="kqStrutForm" property="self_accept_month_data" value="1">                         
                                                                <input type="checkbox" id="self_accept_month_data" name="self_accept_month_data" 
                                                                        value="1" />
                                                        </logic:notEqual>
                                                                                                                                                    员工是否对月汇总数据进行确认 
												  </td>
												</tr>
											</table>   
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<br>
									</td>
								</tr>
								<tr>
									<td valign="top" width="100%">
										<fieldset align="center" style="width: 90%; padding: 5px;">
											<legend>
												<bean:message key="kq.kq_rest.shuoming" />
											</legend>
											<br>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kq.strut.infos" />
													</td>
												</tr>

											</table>
											<br>
										</fieldset>
									</td>
								</tr>

							</table>
							<br>
							<br>
						</td>
					</tr>

				</table>
			</hrms:tab>
			<hrms:tab name="tab3" label="考勤人员库" visible="true"	function_id="270305">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%; padding: 10px;">
											<legend>
												<bean:message key="label.select" />
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														<table>
															<logic:iterate id="element" name="kqStrutForm"
																property="slist" indexId="index">
																<tr>
																	<td align="left" nowrap class="tdFontcolor" colspan="4">
																		<logic:equal name="kqStrutForm"
																			property='<%="selist[" + index + "]"%>' value="1">
																			<input type="checkbox" name="messi"
																				value="${element.dataValue}" checked="true">
																			<bean:write name="element" property="dataName" />
																		</logic:equal>
																		<logic:notEqual name="kqStrutForm"
																			property='<%="selist[" + index + "]"%>' value="1">
																			<input type="checkbox" name="messi"
																				value="${element.dataValue}">
																			<bean:write name="element" property="dataName" />
																		</logic:notEqual>
																	</td>
																</tr>
															</logic:iterate>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
							<br>
							<br>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab4" label="参考指标" visible="true" function_id="270306">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%; padding-left: 10px;">
											<legend>
												<bean:message key="label.select" />
											</legend>
											<div  style="height:expression(document.body.clientHeight-180);width: 100%;overflow: auto;">
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														<table>
															<logic:iterate id="element" name="kqStrutForm"
																property="par_list" indexId="index">
																<tr>
																	<td align="left" nowrap class="tdFontcolor" colspan="4">
																		<logic:equal name="kqStrutForm"
																			property='<%="par_select_list[" + index + "]"%>'
																			value="1">
																			<input type="checkbox" name="par_mes"
																				value="${element.dataValue}" checked="true">
																			<bean:write name="element" property="dataName" />
																		</logic:equal>
																		<logic:notEqual name="kqStrutForm"
																			property='<%="par_select_list[" + index + "]"%>'
																			value="1">
																			<input type="checkbox" name="par_mes"
																				value="${element.dataValue}">
																			<bean:write name="element" property="dataName" />
																		</logic:notEqual>
																	</td>
																</tr>
															</logic:iterate>
														</table>
													</td>
												</tr>
											</table>
											</div>
										</fieldset>
									</td>
								</tr>
							</table>
							<br>
							<br>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab5" label="假期管理" visible="true"
				function_id="270307">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%; padding: 10px;">
											<legend>
												<bean:message key="label.select" />
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														<table>
															<logic:iterate id="element" name="kqStrutForm"
																property="holi_list" indexId="index">
																<tr>
																	<td align="left" nowrap class="tdFontcolor" colspan="4">
																		<logic:equal name="kqStrutForm"
																			property='<%="holi_select_list[" + index + "]"%>'
																			value="1">
																			<input type="checkbox" name="holi_mes"
																				value="${element.dataValue}" checked="true">
																			<bean:write name="element" property="dataName" />
																		</logic:equal>
																		<logic:notEqual name="kqStrutForm"
																			property='<%="holi_select_list[" + index + "]"%>'
																			value="1">
																			<input type="checkbox" name="holi_mes"
																				value="${element.dataValue}">
																			<bean:write name="element" property="dataName" />
																		</logic:notEqual>
																	</td>
																</tr>
															</logic:iterate>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<br />
									</td>
								</tr>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>
												<bean:message key="kq.param.holidays.manager.rules" />
											</legend>
											<table width="60%" border="0" cellspacing="0"
												class="DetailTable" cellpadding="0">
												<tr>
													<td height="10">
													</td>
												</tr>
												<tr>
													<td height="25" align="center" width="7%">
														<%
														if ("0".equals(type)) {
														%>
														<input id="radio1" type="radio" name="rule"
															checked="checked" onClick="clickchange()" />
														<%
														} else {
														%>
														<input id="radio1" type="radio" name="rule"
															onClick="clickchange()" />
														<%
														}
														%>
													</td>
													<td colspan="7">
														<bean:message key="kq.param.holidays.manager.rules.shiji" />
													</td>
												</tr>
													<tr>
													<td height="25" align="center" width="7%">
														<%
														if ("1".equals(type)) {
														%>
														<input id="radios" type="radio" name="rule"
															checked="checked" onClick="clickchange()" />
														<%
														} else {
														%>
														<input id="radios" type="radio" name="rule"
															onClick="clickchange()" />
														<%
														}
														%>
													</td>
													<td colspan="7">
														<bean:message key="kq.param.holidays.manager.rules.standardhours" />
													</td>
												</tr>
												<tr>
													<td height="25" align="center" width="7%">
														<%
														if ("0".equals(type) || "1".equals(type)) {
														%>
														<input type="radio" name="rule" onClick="clickchange()" />
														<%
														} else {
														%>
														<input type="radio" name="rule" onClick="clickchange()"
															checked="checked" />
														<%
														}
														%>
													</td>
													<td colspan="7">
														<bean:message key="kq.param.holidays.manager.rules.custom" />
													</td>
												</tr>
												<%
												if ("0".equals(type) ||  "1".equals(type)) {
												%>
												<tr>
													<td height="28">
														&nbsp;
													</td>
													<td>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														<bean:message key="kq.param.holidays.manager.rules.lt" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param1" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,1);"
															/>
														<bean:message key="kq.param.holidays.manager.rules.hour" />
														<bean:message key="kq.param.holidays.manager.rules.an" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param2" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,2);"/>
														<bean:message key="kq.param.holidays.manager.rules.day" />
													</td>

												</tr>
												<tr>
													<td height="28">
														&nbsp;
													</td>
													<td>
														<bean:message key="kq.param.holidays.manager.rules.gt" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param3" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,1);"/>
														<bean:message key="kq.param.holidays.manager.rules.qie" />
														<bean:message key="kq.param.holidays.manager.rules.lt" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param4" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,1);"/>
														<bean:message key="kq.param.holidays.manager.rules.hour" />
														<bean:message key="kq.param.holidays.manager.rules.an" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param5" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,2);"/>
														<bean:message key="kq.param.holidays.manager.rules.day" />
													</td>
												</tr>
												<tr>
													<td height="28">
														&nbsp;
													</td>
													<td>
														<bean:message key="kq.param.holidays.manager.rules.gt" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param6" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,1);"/>
														<bean:message key="kq.param.holidays.manager.rules.hour" />
														<bean:message key="kq.param.holidays.manager.rules.an" />
														<input style="width: 40px; background-color: #f4f7f7;"
															type="text" name="param7" value="" class="textborder common_border_color input_number"
															disabled="disabled"
															onkeypress="event.returnValue=IsDigit();" 
															onkeyup="checkNUM2(this,2,2);"/>
														<bean:message key="kq.param.holidays.manager.rules.day" />
													</td>
												</tr>
												<%
												} else {
												%>
												<tr>
													<td height="28">
														&nbsp;
													</td>
													<td>
														&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
														<bean:message key="kq.param.holidays.manager.rules.lt" />
														<input style="width: 40px;" type="text" name="param1"
															value="<%=str[0]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.hour" />
														<bean:message key="kq.param.holidays.manager.rules.an" />
														<input style="width: 40px;" type="text" name="param2"
															value="<%=str[1]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.day" />
													</td>

												</tr>
												<tr>
													<td height="28">
														&nbsp;
													</td>
													<td>
														<bean:message key="kq.param.holidays.manager.rules.gt" />
														<input style="width: 40px;" type="text" name="param3"
															value="<%=str[2]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.qie" />
														<bean:message key="kq.param.holidays.manager.rules.lt" />
														<input style="width: 40px;" type="text" name="param4"
															value="<%=str[3]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.hour" />
														<bean:message key="kq.param.holidays.manager.rules.an" />
														<input style="width: 40px;" type="text" name="param5"
															value="<%=str[4]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.day" />
													</td>
												</tr>
												<tr>
													<td height="28">
														&nbsp;
													</td>
													<td>
														<bean:message key="kq.param.holidays.manager.rules.gt" />
														<input style="width: 40px;" type="text" name="param6"
															value="<%=str[5]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.hour" />
														<bean:message key="kq.param.holidays.manager.rules.an" />
														<input style="width: 40px;" type="text" name="param7"
															value="<%=str[6]%>" class="textborder common_border_color input_number"
															onkeypress="event.returnValue=IsDigit();" />
														<bean:message key="kq.param.holidays.manager.rules.day" />
													</td>
												</tr>
												<%
												}
												%>
											</table>
										</fieldset>
									</td>
								<tr>
									<td>
										<br>
									</td>
								</tr>
								<tr>
									<td valign="top" width="100%">
										<fieldset align="center" style="width: 90%;">
											<legend>
												<bean:message key="kq.kq_rest.shuoming" />
											</legend>
											<br>
											<table width="100%" border="0" cellpmoding="5"
												cellspacing="5" class="DetailTable" cellpadding="5">
												<tr>
													<td width="100%" height="30">
														<bean:message key="kq.hols.infos"/>
													</td>
												</tr>

											</table>
											<br>
										</fieldset>
									</td>
								</tr>

							</table>
							<br>
							<br>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab6" label="业务申请" visible="true" function_id="270308">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>加班	</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30" align="left">
														<table width="320" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td align="left" width="160">
																	&nbsp;
																	<logic:equal name="kqStrutForm" property="overtime_hol"	value="1">
																		<input type="checkbox" name="overtime_hol" value="1" styleId='overtime_hol' checked="true">
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm"	property="overtime_hol" value="1">
																		<input type="checkbox" name="overtime_hol"	styleId='overtime_hol' value="1">
																	</logic:notEqual>
																	节假日有排班算作加班
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td width="100%" height="30" align="left">
														<table width="320" border="0" cellspacing="0" cellpadding="0">
															<tr>
																<td align="left" width="160" nowrap>
																	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 加班时间不少于
																</td>
																<td valign="middle" align="right" width="40">
																	<html:text name="kqStrutForm" styleId='min_overtime'
																		property="min_overtime" size="4" maxlength="4"
																		onkeypress="event.returnValue=IsDigits();" 
																		onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"
																		/>
																	
																</td>
																<td valign="middle" align="left"  width="25">
																	<table border="0" cellspacing="2" cellpadding="0">
																		<tr>
																			<td>
																				<button id="1_up" class="m_arrow"
																					onmouseup="IsInputValue('min_overtime');">
																					5
																				</button>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<button id="1_down" class="m_arrow"
																					onmouseup="IsInputValue('min_overtime');">
																					6
																				</button>
																			</td>
																		</tr>
																	</table>
																</td>
																<td align="left">
																	分钟
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td width="100%" height="30" align="left" nowrap>
														<table width="320" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td align="left" width="160">
																	&nbsp;
																	<logic:equal name="kqStrutForm" property="over_status"
																		value="1">
																		<input type="checkbox" name="over_status" value="1"
																			styleId='over_status' checked="true">
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm"
																		property="over_status" value="1">
																		<input type="checkbox" name="over_status"
																			styleId='over_status' value="1">
																	</logic:notEqual>
																	<bean:message key="kq.Overtime_rule.enrol" />

																</td>
																<td valign="middle" align="right" width="40">
																	<html:text name="kqStrutForm" styleId='over_rule'
																		property="over_rule" size="4" maxlength="3"
																		onkeypress="event.returnValue=IsDigits();" 
																		onkeyup="checkNUM2(this,3,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
																</td>
																<td valign="middle" align="left" width="25">
																	<table border="0" cellspacing="2" cellpadding="0">
																		<tr>
																			<td>
																				<button id="1_up" class="m_arrow"
																					onmouseup="IsInputValue('over_rule');">
																					5
																				</button>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<button id="1_down" class="m_arrow"
																					onmouseup="IsInputValue('over_rule');">
																					6
																				</button>
																			</td>
																		</tr>
																	</table>
																</td>
																<td align="left">
																	<bean:message key="kq.Overtime_rule.day" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
												  <td width="100%" height="30" align="left" nowrap>
                                                        <table width="320" border="0" cellspacing="0"
                                                            cellpadding="0">
                                                            <tr>
                                                                <td align="left" width="160">
                                                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;                                                                    
                                                                    <bean:message key="kq.Overtime_rule.max.limit" />

                                                                </td>
                                                                <td valign="middle" align="right"  width="40">
                                                                    <html:text name="kqStrutForm" styleId='overtime_max_limit'
                                                                        property="overtime_max_limit" size="4" maxlength="3"
                                                                        onkeypress="event.returnValue=IsDigits();" 
                                                                        onkeyup="checkNUM2(this,3,0)"
                                                                        styleClass="textborder common_border_color input_number"
                                                                        />
                                                                </td>
                                                                <td valign="middle" align="left" width="25">
                                                                    <table border="0" cellspacing="2" cellpadding="0">
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_up" class="m_arrow"
                                                                                    onmouseup="IsInputValue('overtime_max_limit');">
                                                                                    5
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_down" class="m_arrow"
                                                                                    onmouseup="IsInputValue('overtime_max_limit');">
                                                                                    6
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                                <td align="left">
                                                                    <bean:message key="kq.Overtime_rule.max.limit.unit" />
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
									<td>
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												请假
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30" align="left">
														<table width="320" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td valign="middle" align="left" width="160" nowrap>
																	&nbsp;
																	<logic:equal name="kqStrutForm"
																		property="leave_rule_late_status" value="1">
																		<input type="radio" name="A" value="1"
																			Id='leave_rule_late_status' checked="true">
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm"
																		property="leave_rule_late_status" value="1">
																		<input type="radio" name="A"
																			Id='leave_rule_late_status' value="1">
																	</logic:notEqual>

																	请假最迟登记天数

																</td>
																<td valign="middle" align="right" width="40">
																	<html:text name="kqStrutForm" styleId='leave_rule_late'
																		property="leave_rule_late" size="4" maxlength="3"
																		onkeypress="event.returnValue=IsDigits();" 
																		onkeyup="checkNUM2(this,3,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
																</td>
																<td valign="middle" align="left" width="25">
																	<table border="0" cellspacing="2" cellpadding="0">
																		<tr>
																			<td>
																				<button id="1_up" class="m_arrow"
																					onmouseup="IsInputValue('leave_rule_late');">
																					5
																				</button>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<button id="1_down" class="m_arrow"
																					onmouseup="IsInputValue('leave_rule_late');">
																					6
																				</button>
																			</td>
																		</tr>
																	</table>
																</td>
																<td align="left">
																	<bean:message key="kq.Overtime_rule.day" />
																</td>
															</tr>
														</table>
													</td>
												</tr>
												<tr>
													<td width="100%" height="30" align="left">
														<table width="320" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td align="left" width="160" nowrap>
																	&nbsp;
																	<logic:equal name="kqStrutForm" property="leave_status"
																		value="1">
																		<input type="radio" name="A" value="1"
																			Id='leave_status' checked="true">
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm"
																		property="leave_status" value="1">
																		<input type="radio" name="A" Id='leave_status'
																			value="1">
																	</logic:notEqual>
																	<bean:message key="kq.Leavetime_rule.enrol" />
																</td>
																<td valign="middle" align="right" width="40">
																	<html:text name="kqStrutForm" styleId='leave_rule'
																		property="leave_rule" size="4" maxlength="3"
																		onkeypress="event.returnValue=IsDigits();" 
																		onkeyup="checkNUM2(this,3,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
																</td>
																<td valign="middle" align="left"  width="25">
																	<table border="0" cellspacing="2" cellpadding="0">
																		<tr>
																			<td>
																				<button id="1_up" class="m_arrow"
																					onmouseup="IsInputValue('leave_rule');">
																					5
																				</button>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<button id="1_down" class="m_arrow"
																					onmouseup="IsInputValue('leave_rule');">
																					6
																				</button>
																			</td>
																		</tr>
																	</table>
																</td>
																<td align="left">
																	<bean:message key="kq.Overtime_rule.day" />
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
                                    <td valign="top" width="100%">
                                        <br>
                                        <fieldset align="center" style="width: 90%;">
                                            <legend>
                                                公出
                                            </legend>
                                            <table width="100%" border="0"
                                                cellspacing="0" class="DetailTable" cellpadding="0">
                                                <tr>
                                                    <td width="100%" height="30">
                                                        <logic:equal name="kqStrutForm" property="officeleave_enable_leave_overtime"
                                                            value="1">
                    &nbsp;&nbsp;<input type="checkbox" name="officeleave_enable_leave_overtime"
                                                                value="1" styleId='officeleave_enable_leave_overtime' checked="true">
                                                        </logic:equal>
                                                        <logic:notEqual name="kqStrutForm"
                                                            property="officeleave_enable_leave_overtime" value="1">
                    &nbsp;&nbsp;<input type="checkbox" name="officeleave_enable_leave_overtime"
                                                                styleId='officeleave_enable_leave_overtime' value="1">
                                                        </logic:notEqual>
                                                        公出期间允许请假、加班
                                                    </td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
								<tr>
									<td valign="top" width="100%">
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												是否判断申请加班类型与申请日期相符
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														&nbsp;
														<html:radio name="kqStrutForm"
															property="opinion_overtime_type" value="0" />
														不判断
														<html:radio name="kqStrutForm"
															property="opinion_overtime_type" value="1" />
														判断

													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td valign="top" width="100%">
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												节假日或公休日加班申请是否允许申请多次
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														&nbsp;
														<html:radio name="kqStrutForm"
															property="rest_overtime_time" value="1" />
														只允许申请一次
														<html:radio name="kqStrutForm"
															property="rest_overtime_time" value="0" />
														多次
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr style="display:none"><!-- 目前系统此参数无效,隐藏 -->
									<td valign="top" width="100%">
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												默认班次
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														&nbsp; &nbsp; 休息班加班默认统计用班次:
														<html:select name="kqStrutForm" property="rest_kqclass"
															size="1">
															<html:optionsCollection property="class_list"
																value="dataValue" label="dataName" />
														</html:select>
													</td>
												</tr>
											</table>
											<br>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td valign="top" width="100%">
										<br>
										<fieldset align="center" style="width: 90%;">
											<legend>
												已批申请允许删除
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														<logic:equal name="kqStrutForm" property="approved_delete"
															value="1">
		          	&nbsp;&nbsp;<input type="checkbox" name="approved_delete"
																value="1" styleId='approved_delete' checked="true">
														</logic:equal>
														<logic:notEqual name="kqStrutForm"
															property="approved_delete" value="1">
		          	&nbsp;&nbsp;<input type="checkbox" name="approved_delete"
																styleId='approved_delete' value="1">
														</logic:notEqual>
														已批申请允许删除
													</td>
												</tr>
											</table>
											<br>
										</fieldset>
									</td>
								</tr>
								<br>
							</table>
						</td>
					</tr>
					<br>
				</table>
			</hrms:tab>
			<hrms:tab name="tab7" label="刷卡" visible="true" function_id="270309">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" align="center">
								<tr>
									<td width="100%" align="center">
                                        <fieldset align="center" style="width: 90%; padding: 3px;">
                                            <legend>重复刷卡</legend>
    										<table width="90%" align="left" border="0"
    											cellspacing="0" class="DetailTable" cellpadding="0">
    											<tr>
    												<td height="25" align="left">
    													<table width="200" border="0" cellspacing="0"
    														cellpadding="0">
    														<tr>
    															<td align="left" width="100">
    																&nbsp;&nbsp; 重复刷卡间隔
    															</td>
    															<td valign="middle" align="right">
    																<html:text name="kqStrutForm" styleId='card_interval'
    																	property="card_interval" size="4" maxlength="4"
    																	onkeypress="event.returnValue=IsDigits();" 
    																	onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
    															</td>
    															<td valign="middle" align="left">
    																<table border="0" cellspacing="2" cellpadding="0">
    																	<tr>
    																		<td>
    																			<button id="1_up" class="m_arrow"
    																				onmouseup="IsInputValue('card_interval');">
    																				5
    																			</button>
    																		</td>
    																	</tr>
    																	<tr>
    																		<td>
    																			<button id="1_down" class="m_arrow"
    																				onmouseup="IsInputValue('card_interval');">
    																				6
    																			</button>
    																		</td>
    																	</tr>
    																</table>
    															</td>
    															<td align="left">
    																分钟
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
									<td valign="top" width="100%">
										<fieldset align="center" style="width: 90%; padding: 3px;">
											<legend>
												进出刷卡分析离岗时间
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">&nbsp;
														<html:radio name="kqStrutForm"
															property="check_inout_match" value="1" onclick="hiddenLeaveTime();"/>
														检查进出刷卡匹配
														<html:radio name="kqStrutForm"
															property="check_inout_match" value="0" onclick="hiddenLeaveTime();"/>
														不检查进出刷卡匹配情况<br>
													</td>
												</tr>
												<tr id="leave_time">
													<td>
													<table width="300" border="0" cellspacing="0"
															cellpadding="0">
														<tr>
															<td align="left">
																&nbsp;&nbsp;&nbsp;单次进出小于等于
															</td>
															<td valign="middle" width="50" align="right">
															<html:text name="kqStrutForm" styleId='min_mid_leave_time'
																property="min_mid_leave_time" size="4" maxlength="4"
																onkeypress="event.returnValue=IsDigit();"
																 styleClass="textborder common_border_color input_number"
																 onkeyup="checkNUM2(this,4,0);" />
																&nbsp;
															</td>
															<td valign="middle" align="left">
																<table border="0" cellspacing="2" cellpadding="0">
																	<tr>
																		<td>
																			<button id="1_up" class="m_arrow"
																				onmouseup="IsInputValue('min_mid_leave_time');">
																				5
																			</button>
																		</td>
																	</tr>
																	<tr style="vertical-align:top">
																		<td>
																			<button id="1_down" class="m_arrow"
																				onmouseup="IsInputValue('min_mid_leave_time');">
																				6
																			</button>
																		</td>
																	</tr>
																</table>
															</td>
															<td>
																分钟不记离岗时长
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
									<td valign="top" width="100%">
										<fieldset align="center" style="width: 90%; padding: 3px;">
											<legend>
												中间休息时段有出入刷卡,则离岗时长应
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">&nbsp;
														<html:radio name="kqStrutForm" property="restleave_type"
															value="0" />
														休息时段全扣
														<html:radio name="kqStrutForm" property="restleave_type"
															value="1" />
														实际出入时长扣
														<html:radio name="kqStrutForm" property="restleave_type"
															value="2" />
														不扣
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td valign="top" width="100%">
										<fieldset align="center" style="width: 90%; padding: 3px;">
											<legend>
												弹性班规则
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">&nbsp;
														<html:radio name="kqStrutForm" property="flextime_ruler"
															value="0" />
														完全弹性
														<html:radio name="kqStrutForm" property="flextime_ruler"
															value="1" />
														不完全弹性
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<hrms:priv func_id="27082">
									<tr>
										<td>
											<fieldset align="center" style="width: 90%; padding: 3px;">
												<legend>
													发卡
												</legend>
												<table width="100%" border="0"
													cellspacing="0" class="DetailTable" cellpadding="0">
													<tr>
														<td width="100%" align="left" height="30">
															<table width="100%" align="left">
																<tr>
																	<td width="140">

																		<logic:equal name="kqStrutForm"
																			property="magcard_flag" value="1">
                                                                        &nbsp;<input type="checkbox"
																				name="magcard_flag" value="1" styleId='magcard_flag'
																				checked="true">
																		</logic:equal>
																		<logic:notEqual name="kqStrutForm"
																			property="magcard_flag" value="1">
                                                                            &nbsp;<input type="checkbox"
																				name="magcard_flag" value="1" styleId='magcard_flag'>
																		</logic:notEqual>
																		从磁卡中读取卡号
																	</td>
																	<td>
																		<html:select name="kqStrutForm"
																			property="magcard_setid" size="1">
																			<html:optionsCollection property="setList"
																				value="dataValue" label="dataName" />
																		</html:select>

																	</td>
																</tr>
																<tr>

																	<td>
																		&nbsp;&nbsp;工作证登记表
																	</td>
																	<td>
																		<html:select name="kqStrutForm"
																			property="magcard_cardid" size="1">
																			<html:optionsCollection property="cardlist"
																				value="dataValue" label="dataName" />
																		</html:select>

																	</td>
																</tr>
																<tr>
																	<td>
																		&nbsp;&nbsp;选择读卡机串口
																	</td>
																	<td>
																		<html:select name="kqStrutForm" property="magcard_com"
																			size="1">
																			<html:option value="1">COM1</html:option>
																			<html:option value="2">COM2</html:option>
																			<html:option value="3">COM3</html:option>
																			<html:option value="4">COM4</html:option>
																		</html:select>

																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
											</fieldset>
										</td>
									</tr>
								</hrms:priv>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%; padding: 3px;">
											<legend>
												补刷卡原因代码项定义
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0"
												valign="top">
												<tr>
													<td width="145" height="30">
														&nbsp;&nbsp;&nbsp;选择代码项
													</td>
													<td>
														<hrms:importgeneraldata showColumn="codesetdesc"
															valueColumn="codesetid" flag="true" paraValue=""
															sql="select codesetid,codesetdesc from codeset where codesetid<>'@K' and codesetid<>'UN' and codesetid<>'UM' ORDER BY CODESETID"
															collection="list" scope="page" />
														<html:select name="kqStrutForm" property="card_causation"
															size="1">
															<html:option value="#">请选择...</html:option>
															<html:options collection="list" property="dataValue"
																labelProperty="dataName" />
														</html:select>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%; padding: 3px;">
											<legend>
												考勤期间补刷卡次数
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30" align="left" nowrap>
														<table width="230" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td align="left" width="150">
																	&nbsp;
																	<logic:equal name="kqStrutForm"
																		property="repair_card_status" value="1">
																		<input type="checkbox" name="repair_card_status"
																			value="1" styleId='repair_card_status' checked="true">
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm"
																		property="repair_card_status" value="1">
																		<input type="checkbox" name="repair_card_status"
																			styleId='repair_card_status' value="1">
																	</logic:notEqual>
																	考勤期间补刷卡次数

																</td>
																<td valign="middle" align="right">
																	<html:text name="kqStrutForm" styleId='repair_card_num'
																		property="repair_card_num" size="4" maxlength="4"
																		onkeypress="event.returnValue=IsDigits();"
																		onkeyup="checkNUM2(this,4,0);" 
                                                                        styleClass="textborder common_border_color input_number"/>
																</td>
																<td valign="middle" align="left">
																	<table border="0" cellspacing="2" cellpadding="0">
																		<tr>
																			<td>
																				<button id="1_up" class="m_arrow"
																					onmouseup="IsInputValue('repair_card_num');">
																					5
																				</button>
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<button id="1_down" class="m_arrow"
																					onmouseup="IsInputValue('repair_card_num');">
																					6
																				</button>
																			</td>
																		</tr>
																	</table>
																</td>
																<td align="left">
																	次
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
									<td width="100%" valign="top">
										<fieldset align="center" style="width: 90%; padding: 3px;">
											<legend >
												补刷卡审批关系
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30" align="left" nowrap>
														<table width="360" border="0" cellspacing="0"
															cellpadding="0">
															<tr>
																<td align="left" width="150">
																	&nbsp;&nbsp;&nbsp;选择审批关系
																</td>
																<td valign="middle" align="left">
																	<html:select name="kqStrutForm" property="approve_relation">
																		<html:optionsCollection property="relationlist" name="kqStrutForm" 
																							value="dataValue" label="dataName" />
																	</html:select>
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
									<td width="100%" align="center">
                                        <fieldset align="center" style="width: 90%; padding: 3px;">
                                            <legend >
                                                早到
                                            </legend>
    										<table width="90%" align="left" border="0"
    											cellspacing="0" class="DetailTable" cellpadding="0">
    											<tr>
    												<td height="25" align="left">
    													<table width="280" border="0" cellspacing="0"
    														cellpadding="0">
    														<tr>
    															<td align="right" width="80">
    																提前
    																<html:text name="kqStrutForm" styleId='cardearly'
    																	property="cardearly" size="4" maxlength="4"
    																	onkeypress="event.returnValue=IsDigits();" 
    																	onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
    															</td>
    															<td width="25">
    																<table border="0" cellspacing="2" cellpadding="0">
    																	<tr>
    																		<td>
    																			<button id="1_up" class="m_arrow"
    																				onmouseup="IsInputValue('cardearly');">
    																				5
    																			</button>
    																		</td>
    																	</tr>
    																	<tr>
    																		<td>
    																			<button id="1_down" class="m_arrow"
    																				onmouseup="IsInputValue('cardearly');">
    																				6
    																			</button>
    																		</td>
    																	</tr>
    																</table>
    																</td>
    																<td>
    																分钟上班算作早到
    															</td>
    														</tr>
    													</table>
    												</td>
    											</tr>
    										</table>
                                        </fieldset>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab15" label="申请比对" visible="true" function_id="27030e">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<table width="100%" align="center">
								<tr>
									<td height="30" align="center">
										<table width="90%" border="0" cellspacing="0"
											class="DetailTable" cellpadding="0" valign="top">
											<tr>
												<td height="40">
													&nbsp;业务申请是否与实际刷卡作比对
													<html:select name="kqStrutForm" property="need_busicompare"
														size="1">
														<html:option value="0">否</html:option>
														<html:option value="1">是</html:option>

													</html:select>
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr><td width="100%" valign="top">
                                        <fieldset align="center" style="width: 90%; padding: 5px">
                                            <legend>
                                                比对时间
                                            </legend>
                                            <table width="100%" border="0"
                                                cellspacing="0" class="DetailTable" cellpadding="0"
                                                valign="top">
                                                <tr>
									                 <td height="30">
                                                        <table width="350" border="0" cellspacing="0"
                                                            cellpadding="0">
                                                            <tr>
                                                                <td align="left" width="220">
                                                                    &nbsp; 刷卡开始时间最早从申请起始时间前
                                                                </td>
                                                                <td valign="middle" align="right">
                                                                    <html:text name="kqStrutForm" styleId='busi_cardbegin'
                                                                        property="busi_cardbegin" size="4" maxlength="4"
                                                                        onkeypress="event.returnValue=IsDigits();" 
                                                                        onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
                                                                </td>
                                                                <td valign="middle" align="left">
                                                                    <table border="0" cellspacing="2" cellpadding="0">
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_up" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busi_cardbegin');">
                                                                                    5
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_down" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busi_cardbegin');">
                                                                                    6
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                                <td align="left">
                                                                    分钟起
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td height="30">
                                                        <table width="350" border="0" cellspacing="0"
                                                            cellpadding="0">
                                                            <tr>
                                                                <td align="left" width="220">
                                                                    &nbsp; 刷卡结束时间最迟到申请结束时间后
                                                                </td>
                                                                <td valign="middle" align="right">
                                                                    <html:text name="kqStrutForm" styleId='busi_cardend'
                                                                        property="busi_cardend" size="4" maxlength="4"
                                                                        onkeypress="event.returnValue=IsDigits();" 
                                                                        onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
                                                                </td>
                                                                <td valign="middle" align="left">
                                                                    <table border="0" cellspacing="2" cellpadding="0">
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_up" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busi_cardend');">
                                                                                    5
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_down" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busi_cardend');">
                                                                                    6
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                                <td align="left">
                                                                    分钟止
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td height="30">
                                                        <table width="400" border="0" cellspacing="0"
                                                            cellpadding="0">
                                                            <tr>
                                                                <td align="left" width="220">
                                                                    &nbsp; 申请时长小于刷卡时长超过
                                                                </td>
                                                                <td valign="middle" align="right">
                                                                    <html:text name="kqStrutForm" styleId='busifact_diff'
                                                                        property="busifact_diff" size="4" maxlength="4"
                                                                        onkeypress="event.returnValue=IsDigits();" 
                                                                        onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
                                                                </td>
                                                                <td valign="middle" align="left">
                                                                    <table border="0" cellspacing="2" cellpadding="0">
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_up" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busifact_diff');">
                                                                                    5
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_down" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busifact_diff');">
                                                                                    6
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                                <td align="left">
                                                                    分钟计为异常
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td height="30">
                                                        <table width="400" border="0" cellspacing="0"
                                                            cellpadding="0">
                                                            <tr>
                                                                <td align="left" width="220">
                                                                    &nbsp; 申请时长大于刷卡时长超过
                                                                </td>
                                                                <td valign="middle" align="right">
                                                                    <html:text name="kqStrutForm"
                                                                        styleId='busi_morethan_fact'
                                                                        property="busi_morethan_fact" size="4" maxlength="4"
                                                                        onkeypress="event.returnValue=IsDigits();" 
                                                                        onkeyup="checkNUM2(this,4,0);"
                                                                        styleClass="textborder common_border_color input_number"/>
                                                                </td>
                                                                <td valign="middle" align="left">
                                                                    <table border="0" cellspacing="2" cellpadding="0">
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_up" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busi_morethan_fact');">
                                                                                    5
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                        <tr>
                                                                            <td>
                                                                                <button id="1_down" class="m_arrow"
                                                                                    onmouseup="IsInputValue('busi_morethan_fact');">
                                                                                    6
                                                                                </button>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                                <td align="left">
                                                                    分钟计为异常
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </tr>
                                            </table>
                                        </fieldset>
                                    </td>
                                </tr>
								<tr><td height="5px"></td></tr>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;  padding: 10px">
											<legend>
												比对处理规则
											</legend>
											<table align="center" width="100%" >
												<tr>
													<td>
														<fieldset align="center" style="width:100%; padding: 10px">
															<legend>
																请假
															</legend>
															<table align="left" width="90%">
																<tr>
																	<td align="left" height="30">
																		<logic:equal value="1" name="kqStrutForm" property="leave_need_check">
																			&nbsp;<input type="checkbox" name="leave_need_check" checked="checked"/>&nbsp;
																		</logic:equal>
																		<logic:notEqual value="1" name="kqStrutForm" property="leave_need_check">
																			&nbsp;<input type="checkbox" name="leave_need_check"/>&nbsp;
																		</logic:notEqual>
																		&nbsp;需要比对&nbsp;
																	</td>
																</tr>
																<tr>
																	<td height="30">
																		<table width="90%" align="left">
																			<tr>
																				<td width="50%">
																				&nbsp;申请与实际刷卡&nbsp;
																				<html:select property="leave_compare_rule" name="kqStrutForm" onchange="changeview()">
																					<html:option value="0">手工处理</html:option>
																					<html:option value="1">按时间短者计算</html:option>
																					<html:option value="2">按时间长者计算</html:option>
																				</html:select>&nbsp;&nbsp;&nbsp;
																				
																				<logic:equal name="kqStrutForm" property="leave_updata_data" value="1">
																				&nbsp;<input type="checkbox" name="leave_updata_data" checked="checked" styleId="leave_updata_data"/>&nbsp;
																				</logic:equal>
																				<logic:notEqual name="kqStrutForm" property="leave_updata_data" value="1">
																				&nbsp;<input type="checkbox" name="leave_updata_data" styleId="leave_updata_data"/>&nbsp;
																				</logic:notEqual>
																				自动修正数据（刷卡或申请单）
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
													<td>
														<fieldset align="center" style="width:100%; padding: 10px">
															<legend>
																加班
															</legend>
															<table align="left" width="90%">
																<tr>
																	<td align="left" height="30">
																		<logic:equal value="1" name="kqStrutForm" property="overtime_need_check">
																			&nbsp;<input type="checkbox" name="overtime_need_check"/ checked="checked">&nbsp;
																		</logic:equal>
																		<logic:notEqual value="1" name="kqStrutForm" property="overtime_need_check">
																			&nbsp;<input type="checkbox" name="overtime_need_check"/>&nbsp;
																		</logic:notEqual>
																		&nbsp;需要比对&nbsp;
																	</td>
																</tr>
																<tr>
																	<td height="30">
																		<table width="90%" align="left">
																			<tr>
																				<td width="50%">
																				&nbsp;申请与实际刷卡&nbsp;
																				<html:select property="overtime_compare_rule" name="kqStrutForm" onchange="changeview()">
																					<html:option value="0">手工处理</html:option>
																					<html:option value="1">按时间短者计算</html:option>
																					<html:option value="2">按时间长者计算</html:option>
																				</html:select>&nbsp;&nbsp;&nbsp;
																				
																				<logic:equal name="kqStrutForm" property="overtime_updata_data" value="1">
																				&nbsp;<input type="checkbox" name="overtime_updata_data" checked="checked"/>&nbsp;
																				</logic:equal>
																				<logic:notEqual name="kqStrutForm" property="overtime_updata_data" value="1">
																				&nbsp;<input type="checkbox" name="overtime_updata_data"/>&nbsp;
																				</logic:notEqual>
																				自动修正数据（刷卡或申请单）
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</fieldset>
													</td>
												</tr><tr>
													<td>
														<fieldset align="center" style="width:100%; padding: 10px">
															<legend>
																公出
															</legend>
															<table align="left" width="90%">
																<tr>
																	<td align="left" height="30">
																		<logic:equal value="1" name="kqStrutForm" property="officeleave_need_check">
																			&nbsp;<input type="checkbox" name="officeleave_need_check"/ checked="checked">&nbsp;
																		</logic:equal>
																		<logic:notEqual value="1" name="kqStrutForm" property="officeleave_need_check">
																			&nbsp;<input type="checkbox" name="officeleave_need_check"/>&nbsp;
																		</logic:notEqual>
																		&nbsp;需要比对&nbsp;
																	</td>
																</tr>
																<tr>
																	<td height="30">
																		<table width="90%" align="left">
																			<tr>
																				<td width="50%">
																				&nbsp;申请与实际刷卡&nbsp;
																				<html:select property="officeleave_compare_rule" name="kqStrutForm" onchange="changeview()">
																					<html:option value="0">手工处理</html:option>
																					<html:option value="1">按时间短者计算</html:option>
																					<html:option value="2">按时间长者计算</html:option>
																				</html:select>&nbsp;&nbsp;&nbsp;
																				
																				<logic:equal name="kqStrutForm" property="officeleave_updata_data" value="1">
																				&nbsp;<input type="checkbox" name="officeleave_updata_data" checked="checked"/>&nbsp;
																				</logic:equal>
																				<logic:notEqual name="kqStrutForm" property="officeleave_updata_data" value="1">
																				&nbsp;<input type="checkbox" name="officeleave_updata_data"/>&nbsp;
																				</logic:notEqual>
																				自动修正数据（刷卡或申请单）
																				</td>
																			</tr>
																		</table>
																	</td>
																</tr>
															</table>
														</fieldset>
													</td>
												</tr>
												<tr><td height="5px"></td></tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab8" label="申请模板" visible="true" function_id="270301">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" align="center">
								<tr>
									<td>
										<table width="90%" border="0" cellspacing="0" align="center"
											cellpadding="0" class="ListTable">
											<tr>
												<td align="center" class="TableRow" nowrap>
													申请类型
												</td>
												<td align="center" class="TableRow" nowrap>
													模板名称
												</td>
												<td align="center" class="TableRow" nowrap>
													删除
												</td>
											</tr>
											<%
											int i = 0;
											%>
											<hrms:extenditerate id="element" name="kqStrutForm"
												property="templateListForm.list" indexes="indexes"
												pagination="templateListForm.pagination" pageCount="20"
												scope="session">
												<%
												if (i % 2 == 0) {
												%>
												<tr class="trShallow">
													<%
													} else {
													%>
												
												<tr class="trDeep">
													<%
													}
													%>
													<td align="center" class="RecordRow" nowrap>
														<logic:equal name="element" property="string(name)"
															value="Kq_template_Q11">
															<input type="hidden" name="IDs" id="IDs_<%=i%>"
																value="q11" />
                                                                                                                                                                加班申请
                                                        </logic:equal>
														<logic:equal name="element" property="string(name)"
															value="Kq_template_Q13">
															<input type="hidden" name="IDs" id="IDs_<%=i%>"
																value="q13" />
                                                                                                                                                                公出申请
                                                        </logic:equal>
														<logic:equal name="element" property="string(name)"
															value="Kq_template_Q15">
															<input type="hidden" name="IDs" id="IDs_<%=i%>"
																value="q15" />
                                                                                                                                                                请假申请
                                                        </logic:equal>
													</td>
													<td align="center" class="RecordRow" nowrap>
														<bean:write name="element" property="string(description)"
															filter="true" />
														&nbsp;
													</td>
													<td align="center" class="RecordRow" nowrap>
														<a herf="#" onclick="deleteTemplate('<%=i%>');"> <img
																src="/images/del.gif" border=0>
														</a>
													</td>
												</tr>
												<%
												i++;
												%>
											</hrms:extenditerate>
										</table>


									</td>
								</tr>
								<tr>
									<td height="20">
										<br>
										<br>
										<br>
									</td>
								</tr>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>
												选择导入文件模板
											</legend>
											<table border="0" cellspacing="0" align="center"
												cellpadding="0">
												<tr>
													<td>
														<Br>
														模板类型
														<select name="template_type" id="template_type">
															<option value="Q11">
																加班申请
															</option>
															<option value="Q13">
																公出申请
															</option>
															<option value="Q15">
																请假申请
															</option>
														</select>
													</td>
												</tr>
												<tr>
													<td width="480">
														<Br>
														文&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;件
														<input type="file"  name="importfile" size="40" class="text4">
													</td>
												</tr>
												<tr>
													<td>
														&nbsp;
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
								<tr>
									<td align="center" valign="bottom" height="30px">
										<input type="button" name="b_update" value="上传"
											class="mybutton" onClick="imports()">
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:priv func_id="270302">
				<hrms:tab name="tab9" label="统计项目" visible="true" function_id="">
					<table width="90%" align="center">
						<tr>
							<td valign="top">
								<br>
								<table width="100%" border="0" cellspacing="0"
									class="DetailTable" cellpadding="0" valign="top">
									<tr>
										<td>
											<fieldset align="center" style="width: 90%; padding: 10px;">
												<legend>
													<bean:message key="label.select" />
												</legend>
												<table width="100%" border="0"
													cellspacing="0" class="DetailTable" cellpadding="0">
													<tr>
														<td width="100%" height="30">
															<table>
																<logic:iterate id="element" name="kqStrutForm"
																	property="statq03_list" indexId="index">
																	<tr>
																		<td align="left" nowrap class="tdFontcolor"
																			colspan="4">
																			<logic:equal name="kqStrutForm"
																				property='<%="par_statq03_list[" + index + "]"%>'
																				value="1">
																				<input type="checkbox" name="par_stat_q03"
																					value="${element.dataValue}" checked="true">
																				<bean:write name="element" property="dataName" />
																			</logic:equal>
																			<logic:notEqual name="kqStrutForm"
																				property='<%="par_statq03_list[" + index + "]"%>'
																				value="1">
																				<input type="checkbox" name="par_stat_q03"
																					value="${element.dataValue}">
																				<bean:write name="element" property="dataName" />
																			</logic:notEqual>
																		</td>
																	</tr>
																</logic:iterate>
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
				</hrms:tab>
			</hrms:priv>
			<hrms:priv func_id="27030a">
				<hrms:tab name="tab10" label="网上签到" visible="true" function_id="">
					<table width="90%" align="center">
						<tr>
							<td valign="top">
								<table width="100%" border="0" cellspacing="0"
									class="DetailTable" cellpadding="0" valign="top">
									<tr>
										<td>
											<fieldset align="center" style="width: 90%; padding: 10px">
												<legend>
													签到签退时IP地址
												</legend>
												<table width="100%" border="0"
													cellspacing="0" class="DetailTable" cellpadding="0">
													<tr>
														<td width="100%" height="30">
															<table>
																<tr>
																	<td align="left" nowrap class="tdFontcolor" colspan="4">
																		<html:radio name="kqStrutForm"
																			property="net_sign_check_ip" value="0" />
																		IP不绑定
																		<html:radio name="kqStrutForm"
																			property="net_sign_check_ip" value="1" />
																		IP绑定
																		<html:radio name="kqStrutForm"
																			property="net_sign_check_ip" value="2" />
																		有IP则绑定，无IP不绑定
																	</td>
																</tr>
																<tr>
																	<td>
																		<logic:equal name="kqStrutForm"
																			property="net_sign_approve" value="1">
																			<input type="checkbox" name="net_sign_approve"
																				value="1" styleId='net_sign_approve' checked="true">
																		</logic:equal>
																		<logic:notEqual name="kqStrutForm"
																			property="net_sign_approve" value="1">
																			<input type="checkbox" name="net_sign_approve"
																				styleId='net_sign_approve' value="1">
																		</logic:notEqual>
																		签到签退数据需审批
																		<br>
																	</td>
																</tr>
															</table>
														</td>
													</tr>
												</table>
										</td>
									</tr>
								</table>
								<br>
								<br>
							</td>
						</tr>
					</table>
				</hrms:tab>
			</hrms:priv>
			<hrms:tab name="tab11" label="考勤薄" visible="true"
				function_id="270303">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;padding-left: 10px;">
											<legend>
												选择考勤表显示汇总指标
											</legend>
										<div  style="height:expression(document.body.clientHeight-180);width: 100%;overflow: auto;">
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="0">
												<tr>
													<td width="100%" height="30">
														<table>
															<logic:iterate id="element" name="kqStrutForm"
																property="timecard03_list" indexId="index">
																<tr>
																	<td align="left" nowrap class="tdFontcolor" colspan="4">
																		<logic:equal name="kqStrutForm"
																			property='<%="kq_timecard03_list[" + index + "]"%>'
																			value="1">
																			<input type="checkbox" name="kqcard_q03_ip"
																				value="${element.dataValue}" checked="true">
																			<bean:write name="element" property="dataName" />
																		</logic:equal>
																		<logic:notEqual name="kqStrutForm"
																			property='<%="kq_timecard03_list[" + index + "]"%>'
																			value="1">
																			<input type="checkbox" name="kqcard_q03_ip"
																				value="${element.dataValue}">
																			<bean:write name="element" property="dataName" />
																		</logic:notEqual>
																	</td>
																</tr>
															</logic:iterate>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab12" label="数据处理" visible="true"
				function_id="27030b">
				<table width="90%" align="center">
					<tr>
						<td valign="top">
							<br>
							<table width="100%" border="0" cellspacing="0"
								class="DetailTable" cellpadding="0" valign="top">
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>
												数据处理方式
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="10px">
												<tr>
													<td width="100%" height="30">
														<table>
															<tr>
																<td>&nbsp; &nbsp;
																	<logic:equal name="kqStrutForm"
																		property="quick_analyse_mode" value="0">
																		<input type="checkbox" name="quick_analyse_mode"
																			value="1" styleId='quick_analyse_mode'>
																	</logic:equal>
																	<logic:notEqual name="kqStrutForm"
																		property="quick_analyse_mode" value="0">
																		<input type="checkbox" name="quick_analyse_mode"
																			styleId='quick_analyse_mode' value="1" checked="true">
																	</logic:notEqual>
																	启用精简处理方式
																</td>
															</tr>
															<tr>
																<td style="color: blue;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
																	注意：有弹性班次或需分析离岗时长、延时加班、休息日刷卡转加班等情况时，请勿设置此项！                                                                    
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
										<br>
									</td>
								</tr>
								<tr>
									<td>
										<fieldset align="center" style="width: 90%;">
											<legend>
												数据处理模式
											</legend>
											<table width="100%" border="0"
												cellspacing="0" class="DetailTable" cellpadding="10px;">
												<tr>
													<td width="100%" height="30">
														<table>
															<tr>
																<td>
																	&nbsp; &nbsp;
																	<html:radio name="kqStrutForm"
																		property="data_processing" value="0" />
																	分用户处理
																	<html:radio name="kqStrutForm"
																		property="data_processing" value="1" />
																	集中处理
																</td>
															</tr>
														</table>
													</td>
												</tr>
											</table>
										</fieldset>
									</td>
								</tr>
							</table>
							<br>
							<br>
						</td>
					</tr>
				</table>
			</hrms:tab>
			<hrms:tab name="tab14" label="加班扩展" visible="true" function_id="27030d">
				<table border="0" align="center" width="90%">
                    <tr>
                        <td  valign="top">
                            <br>
                            <table width="100%" border="0" cellspacing="0" class="DetailTable" cellpadding="0" valign="top">
					           <tr>
						          <td>
							         <fieldset align="center" style="width: 90%; padding: 10px">
								        <legend>休息日转加班</legend>
							            <table align="left" width="100%">
    								        <tr>
    									        <td align="left" valign="middle">
    												&nbsp;<html:checkbox name="kqStrutForm" property="turn_enable" value='1'/>启用
    									        </td>
    								        </tr>
    								        <tr>
									            <td align="left" valign="middle">
										            <fieldset id="" style="width: 100%; padding: 10px">
											        <legend>刷卡要求</legend>
														<html:radio name="kqStrutForm" property="turn_charge" onclick="changeview()" value="0"/>
														需要进出匹配&nbsp;
														<html:radio name="kqStrutForm" property="turn_charge" onclick="changeview()" value="1"/>
														有刷卡即加班
										            </fieldset>
									            </td>
								            </tr>
								            <tr>
                                                <td></td>
                                            </tr>
								            <tr>
									            <td align="left" valign="middle">
										        <fieldset id=""  align="center" style="width: 100%; padding: 10px">
											    <legend>加班时长</legend>
											        <table width="100%">
												    <tr>
													    <td width="20px;">
														    <html:radio name="kqStrutForm" property="turn_tlong" value="2" onclick="tuck_application()"/>
													    </td>
													    <td align="left" width="80px;">
														    默认时长
													    </td>
													    <td align="left">
														    <html:text name="kqStrutForm" property="turn_time" size="4" 
															  onkeypress="event.returnValue=IsDigit();" 
															  onkeyup="checkNUM2(this,4,0);"
                                                              styleClass="textborder common_border_color input_number"
															  maxlength="4"></html:text>
														    &nbsp;小时
													    </td>
												    </tr>
    												<tr>
    													<td width="20px;">
    														<html:radio name="kqStrutForm" property="turn_tlong"
    															value="1" onclick="tuck_application()"></html:radio>
    													</td>
    													<td align="left" width="80px;">
    														参考班次时长
    													</td>
    													<td align="left">
    														<html:select name="kqStrutForm" property="turn_classid">
    															<html:optionsCollection name="kqStrutForm" label="dataName"
    																value="dataValue" property="turn_classlist" />
    														</html:select>
    													</td>
    												</tr>
    												<tr id="tr_turn_tlong_2">
    													<td width="20px;">
    														<html:radio name="kqStrutForm" property="turn_tlong"
    															value="0" onclick="tuck_application()"></html:radio>
    													</td>
    													<td align="left" colspan="2">
    														实际刷卡时长
    													</td>
    												</tr>
    											    </table>
										        </fieldset>
									            </td>
								            </tr>
            								<tr id='tr_turn_addApp'>
            									<td align="left">
            										<fieldset id="" style="width: 100%; padding: 10px">
            											<legend>加班申请单</legend>
            											<table width="100%">
            												<tr>
            													<td width="20px;">
            														<html:radio name="kqStrutForm" property="turn_appdoc" value="0"/>
            													</td>
            													<td align="left">
            														不需要生成申请单
            													</td>
            												</tr>
            												<tr>
            													<td width="20px;">
            														<html:radio name="kqStrutForm" property="turn_appdoc" value="1"/>
            													</td>
            													<td align="left">
            														生成申请单，需要确认
            													</td>
            												</tr>
            												<tr>
            													<td width="20px;">
            														<html:radio name="kqStrutForm" property="turn_appdoc" value="2"/>
            													</td>
            													<td align="left">
            														生成申请单，不需要确认
            													</td>
            												</tr>
            				
            											</table>
            										</fieldset>
            									</td>
            								</tr>
            							</table>
            						</fieldset>
						          </td>
					           </tr>
					           <tr><td>&nbsp;</td></tr>
					           <% if(canUsedLeaveForOT) { %>
            					<tr>
            						<td>
            							<fieldset  align="center" style="width: 90%; padding: 10px">
            								<legend>加班转调休</legend>
            								<table align="center" width="100%" >
            									<tr>
            										<td>
            											<fieldset id="" style="width: 100%; height: 50px; padding: 10px" align="center">
            											<legend>
            												请选择可用来调休的加班
            											</legend>
            											<table width="50%"><%int x = 1; %>
            												<logic:iterate id="element" name="kqStrutForm"
            													property="vacation_list" indexId="index">
            												<%if(x%5==1) {%>
            												<tr>
            												<%} %>
            													<td align="left" valign="middle" nowrap class="tdFontcolor">
            														<logic:equal name="kqStrutForm"
            															property='<%="vacation_select_list[" + index + "]"%>'
            															value="1">
            															<input type="checkbox" name="overtimeToOff"
            																value="${element.dataValue}" checked="true">
            															<bean:write name="element" property="dataName" />
            														</logic:equal>
            														<logic:notEqual name="kqStrutForm"
            															property='<%="vacation_select_list[" + index + "]"%>'
            															value="1">
            															<input type="checkbox" name="overtimeToOff"
            																value="${element.dataValue}">
            															<bean:write name="element" property="dataName" />
            														</logic:notEqual>
            													</td>
            												<%if(x%5==0) {%>
            												</tr>
            												<%} %>
            												<%x++; %>
            												</logic:iterate>
            											</table>
            										</fieldset>
            										</td>
            									</tr>
            									<tr>
            										<td style="padding-left: 20px;">
														<table>
															<tr>
																<td align="left">调休假</td>
																<td align="left">
																	<html:select name="kqStrutForm" property="vacationToOff">
			            												<html:optionsCollection property="vacationTypeList" name="kqStrutForm" value="dataValue" label="dataName" />
			            											</html:select>
																</td>
															</tr>
			            									<tr><td></td><td></td></tr>
															<tr>
			            										<td align="left">调休有效期限</td>
			            										<td align="left">
                                                                    <html:select name="kqStrutForm" property="overtimeForLeaveCycle" onchange="changeOtForLeaveCycle()">
                                                                        <html:optionsCollection property="overtimeForLeaveCycleList" name="kqStrutForm" value="dataValue" label="dataName" />
                                                                    </html:select>
                                                                </td>
			            										<td align="left" id="validityTimeTd">
			            											<html:text property="validityTime" name="kqStrutForm" size="5" 
			            											onkeypress="event.returnValue=IsDigits();"
			            											onkeyup="checkNUM2(this,4,0);"
			                                                        styleClass="textborder common_border_color input_number"/>
			            											<span id="validityTimeUnit">天</span>
			            										</td>
			            									</tr>
			            									<tr>
			            										<td align="left">未调休加班限额</td>
			            										<td align="left">
                                                                <html:text property="overtimeForLeaveMaxHour" name="kqStrutForm" size="5" 
                                                                    onkeypress="event.returnValue=IsDigits();"
                                                                    onkeyup="checkNUM2(this,4,0);"
                                                                    styleClass="textborder common_border_color input_number"/>
                                                                    &nbsp;小时
                                                                </td>
			            									</tr>
														</table>
            										</td>
            									</tr>
            								</table>
            							</fieldset>
            						</td>
            					</tr>
            					<% } %>
                            </table>
                        </td>
                    </tr>
				</table>
			</hrms:tab>
			<hrms:priv func_id="27030c">
			<hrms:tab name="tab13" label="同步配置" visible="true"
				function_id="27030c">
				<!-- /kq/options/struts/kqsynchronous 核二三同步配置 liwc -->
				<iframe src="/kq/options/struts/kqsynchronous.do?b_query=link"
					frameborder="0" scrolling="auto" width="100%" height="100%"
					marginwidth="0" marginheight="0" align="top"></iframe>
			</hrms:tab>
			</hrms:priv>
		</hrms:tabset>
		<table width="50%" align="center" valign="top">
			<tr>
				<td align="center">
					<input type="hidden" name="sync_carddata" id="sync_carddata"
						value="${kqStrutForm.sync_carddata}" />
					<input type="button" name="btnreturn"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick=" saveStruts();">
					<hrms:tipwizardbutton flag="workrest" target="il_body"
						formname="kqStrutForm" />
				</td>
			</tr>
		</table>
	</html:form>