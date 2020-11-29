<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%
DailyRegisterForm daily=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
String lockedNumStr=daily.getLockedNum();
int lockedNum=Integer.parseInt(lockedNumStr);
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String moduleFlag = daily.getModuleFlag();
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
<script language="javascript" src="/js/xtree.js"></SCRIPT>
<script language="javascript" src="/js/meizzDate_saveop.js"></script>
<script language="javascript" src="dailyregister.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script language="javascript" src="/js/function.js"></SCRIPT>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css">  

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

<script language="javascript"> 
   <%if(!(userView.hasTheFunction("2702032")) && !(userView.hasTheFunction("0C3113")) ){%>
   window.location="/kq/register/daily_registerdata.do?br_collect=link";
   <% }else{%>
	
 //录入方式
  var num; 
  var row;
  var line; 
  var inNum; 
  function inputType2(obj,event)
  {
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode; 
    if (keyCode == 13)
    { 
        if (num==null)
          num=1;    
        var id_value=obj.getAttribute("id");
        if(id_value=="")
           id_value="1";
        var int_id=parseInt(id_value);
        var focus_id="1";
        var fObj;    
       if(num==1)
       {
         if(int_id==row*inNum)
         {
          fObj=document.getElementById(focus_id);        
         }else
         {
           focus_id=int_id+1;
           fObj=document.getElementById(focus_id);
         }
       }else
       {
         var end_row_num=(row-1)*inNum
         if(int_id<end_row_num)
         {
            focus_id=int_id+inNum;
             fObj=document.getElementById(focus_id);
         }else if(int_id==row*inNum)
         {
            fObj=document.getElementById("1");
         }else
         {
           focus_id=inNum-(row*inNum)%int_id+1;
           fObj=document.getElementById(focus_id);
         }
       
      }
      fObj.focus(); 
      return false;
    }
    else if(keyCode == 37||keyCode == 38||keyCode == 39||keyCode == 40)
    {
       var id_value=obj.getAttribute("id");
       if(id_value=="")
         id_value="1";
       var focus_id="1";
       var fObj;    
       var int_id=parseInt(id_value);
       if(keyCode == 37)//← 
       {
         if(int_id==1)
         {
           fObj=document.getElementById("1");  
         }else
         {
           focus_id=int_id-1;
           fObj=document.getElementById(focus_id);
         }
       }else if(keyCode == 38)//↑
       {
          if(int_id>inNum)
          {
            
            int_id=int_id-inNum;
            fObj=document.getElementById(int_id);  
          }else
          {
            fObj=document.getElementById(id_value);  
          }
       }else if(keyCode == 39)//→ 
       {
          if(int_id==row*inNum)
          {
            fObj=document.getElementById(focus_id);        
          }else
          {
            focus_id=int_id+1;
            fObj=document.getElementById(focus_id);
          }
       }else if(keyCode == 40)//↓ 
       {
           var end_row_num=(row-1)*inNum
           if(int_id<end_row_num)
           {
             focus_id=int_id+inNum;
             fObj=document.getElementById(focus_id);
           }else if(int_id==row*inNum)
           {
              fObj=document.getElementById("1");
           }else
           {
             focus_id=inNum-(row*inNum)%int_id+1;
             fObj=document.getElementById(focus_id);
           }
       }
       fObj.focus(); 
       return false;
    }
    else
    {
      return true;
    }    
  }  
  function inputNum(n)
  {
   num=n;      
  }
   function returnS(s){
     row=s;
   }
   function returnLine(l){
     line=l;
   }
   function returnInNum(n){
     inNum=n;
   } 
   
   <%}%>
</script> 
<script language="javascript">
    
   function empchange()
   {
       var len=document.dailyRegisterForm.elements.length;
       var i;
       var waitInfo=eval("wait");	   
	    waitInfo.style.display="block";	
        for (i=0;i<len;i++)
        {
         if (document.dailyRegisterForm.elements[i].type=="checkbox")
          {
             
            document.dailyRegisterForm.elements[i].checked=false;
          }
        }
      dailyRegisterForm.action="/kq/register/empchange.do?b_search=link&registerdate=${dailyRegisterForm.registerdate}&re_static=0";
      dailyRegisterForm.submit();
   }  
   function kqreport()
   {
      var db=document.getElementById("pre").value;
      if(db=="")
      	db="all";
      delChecked();
      dailyRegisterForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&userbase=${dailyRegisterForm.userbase}&code=${dailyRegisterForm.code}&coursedate=${dailyRegisterForm.kq_duration}&kind=${dailyRegisterForm.kind}&self_flag=tran&privtype=kq&dbtype="+db+"&sortitem="+getEncodeStr("${dailyRegisterForm.sortitem}");
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
// function hour(){
// 	dailyRegisterForm.action="/kq/register/daily_registerdata_hour.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}";
//    dailyRegisterForm.submit();
// }
 function changeys(dd)
 {
 	if(dd==2){
 		dailyRegisterForm.action="/kq/register/daily_registerdata_hour.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}";
    	dailyRegisterForm.submit();
 	}else if(dd==1){
 		dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_search=link";
    	dailyRegisterForm.submit();
 	}
 }
</script> 
<html:form action="/kq/register/daily_registerdata">
<logic:equal name="dailyRegisterForm" property="error_flag" value="0">
<script language="javascript">
function showOverrule(userbase,a0100,kq_duration)
{
    	   var target_url;
    	   var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
    	   target_url="/kq/register/daily_registerdata.do?b_overrlue=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+kq_duration;
    	   newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
 }
function change()
{
      dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}";
      dailyRegisterForm.submit();
}  

   function getSelect(columns,code)
   {  
     var i=0;
     var r=0;	    
     var y=0;
     var st=0;
     var sr="";
     st=columns.lastIndexOf(","); 
     if(st==columns.length-1)   
       columns=columns.substring(0,st); 
     columns=columns+",";
     
     columns = columns.replace("ctime,","").replace("dbid,","").replace("a0000,","").replace("modusername,","").replace("modtime,","");
     
     var forms= new Array();
     var hashvo=new HashMap();
     while(i!=-1)
     {		  
	i=columns.indexOf(",",r);
		
	if(i!=-1){
	   var str=columns.substring(r,i); 
	   if(!isArray($F(str))){
	       var d=new Array();
               d=$F(str).split(",");
               forms[y]=d;              
	    }else{
	    
	      forms[y]=$F(str);
	   }	   	        
	   y++;
	}
        r=i+1;	       	        
     } 
     if(row>0)
     {       
        hashvo.put("forms",forms);
		hashvo.put("columns",columns);
		hashvo.put("code",code); 
		hashvo.put("kind","${dailyRegisterForm.kind}");
		var waitInfo=eval("wait");       
		waitInfo.style.display="block";
		Rpc({functionId:'15301110005',timeout:900000000,async:true,success:showSelect},hashvo);
     }else
     {
        alert("没有记录，无法执行此操作！");
        return false;
     }
   }
   
   function showSelect(outparamters)
   { 
      var value=outparamters.responseText;
         var map=Ext.decode(value);
         var tes = map.type;
      MusterInitData();
      if(tes=="success"){
         alert("数据保存成功");
      }else if(tes=="nosave"){
        alert("数据保存失败");
      }else{
         alert("数据保存失败");
      }
   }
  function isArray(obj) 
  { 
      return (obj.constructor.toString().indexOf('Array')!= -1);
  } 
  
  function gocollect()
   {
        dailyRegisterForm.action="/kq/register/daily_registerdata.do?br_collect=link";
        dailyRegisterForm.target="il_body";
        dailyRegisterForm.submit();
   }
   function updateDate()
   {
      dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_update=link";
      dailyRegisterForm.submit();
   }
  function show_state(state)
  {
     //var state = s.options[s.selectedIndex].value;
     if(state==0)
     {
     
     }else if(state==1)
     {	delChecked();
        showCollect();//月汇总
     }else if(state==2)
     {
     	delChecked();
        show_ambiquity();//不定期
     }
  }
function go_must(){
	  
      var returnURL = getEncodeStr("${dailyRegisterForm.returnURL}");
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${dailyRegisterForm.relatTableid}&closeWindow=1";
      urlstr+="&returnURL="+returnURL + "&kqpre=${dailyRegisterForm.select_pre}"; 
      window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}  
function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  }
</script>
<script language="javascript">
  function getApp()
   {  	if(confirm("您确定要对当前考勤期间进行月末处理吗？"))
       {  
         var request=new Request({method:'post',onSuccess:showApp,functionId:'15301110034'});
       }
   }
   function showApp(outparamters)
   {
     var tes=outparamters.getValue("notapptag");
     
     if(tes=="seal")
     {
        if(confirm("是否进行归档操作？\n提示:当月末处理完成后.本期间将不能再作归档处理.除非解封期间!"))
        {
            getPigeonhole();
        }else
        {
            getSeal();
        }
        
     }else if(tes=="noseal")
     {
        var q03=outparamters.getValue("notapp_list");
        var q07=outparamters.getValue("notQ07_list");
        var q09=outparamters.getValue("notQ09_list");        
        if(q03=="have")
        {
            alert('请先将考勤信息进行审批！');
           return false;
        }
        if(q07=="have")
        {
           alert("请先将部门考勤信息进行日汇总！");
           return false;
        }
        if(q09=="have")
        {
           alert("请先将部门考勤信息进行月汇总！");
           return false;
        }
     }
   }
   function getSeal()
   {
         <hrms:priv func_id="2702027"> 
         if(confirm("是否进行封存考勤期间处理!\n提示:如果封存.将会跳进下一个考勤期间,本期间将不能再进行修改.除非解封期间!"))
         {
          dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_useal=link";
          dailyRegisterForm.submit();
          }
         </hrms:priv>
   }
   function getPigeonhole()
   {
           var target_url;
           var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           target_url="/kq/register/pigeonhole.do?b_search=link&re_flag=dail";
           newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   }
   function read_Pigeonflag(flag)
   {
      <%
        DailyRegisterForm dailyRegisterForm1=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
        String pige_flag= dailyRegisterForm1.getPigeonhole_flag(); 
        if(pige_flag=="true")
        {
      %>
          alert("归档成功!");          
                     
      <%
        }else if(pige_flag=="false")
        {
       %>
          alert("归档失败，请重试！");
      
      <% 
        }else if(pige_flag=="s_true")
        {
       %>
         alert("个人业务归档成功!");
      <% 
        }else if(pige_flag=="s_false")
        {
      %>
        alert("个人业务归档失败，请重试！"); 
      <%         
        }  
        dailyRegisterForm1.setPigeonhole_flag("xxx"); 
        session.setAttribute("dailyRegisterForm",dailyRegisterForm1);     
      %>                
   }
   
   function showPigeFlag(outparamters)
   {
      var flag=outparamters.getValue("pigeonhole_flag");
      var request=new Request({method:'post',asynchronous:false,onSuccess:showPigeFlag,functionId:'15302110004'});
   }   
   function viewAll()
   {
       dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&select_flag=0";
      dailyRegisterForm.submit();
   }
   function syncorder() {
		dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&syncorder=1";		
      	dailyRegisterForm.submit();
   }
   function sort(){
		var url = "/kq/register/daily_registerdata_sort.do?b_search=link&setid=q03&checkflag=0";	
		var return_obj = window.showModalDialog(url,"","dialogWidth:510px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no")
		
		if (return_obj) {
			dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&syncorder=2&sortitem="+$URL.encode(getEncodeStr(return_obj));
      		dailyRegisterForm.submit();
      	}
		
		
   }
   
   function selectflag()
   {
      dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&select_flag=1";
      dailyRegisterForm.submit();
   }   
   //导出摸板
function downLoadTemp()
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("colums","${dailyRegisterForm.columns}");
	hashvo.setValue("tablename","Q03");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'15301110100'},hashvo);
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
}


var blankhidden=0;
function visibleblank(){
	var blank=document.getElementById("queryblank");
	if(blankhidden==0){
	 /*兼容非IE浏览器页面显示样式问题 display值为空  wangb 20171026*/
	 blank.style.display='';
	 blankhidden=1;
	 var querydesc=document.getElementById("querydesc");
	 querydesc.innerHTML="[&nbsp;<a href=\"javascript:visibleblank();\" >查询隐藏&nbsp;</a>]";
	}else{
		 blank.style.display='none';
		 blankhidden=0;
		 var querydesc=document.getElementById("querydesc");
		 querydesc.innerHTML="[&nbsp;<a href=\"javascript:visibleblank();\" >查询显示&nbsp;</a>]";
	}
}
</script>
<hrms:themes /> <!-- 7.0css -->
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<table width="100%" border="0" cellspacing="0" cellpadding="0"> 
 <tr>
  <td nowrap>
  
  <% int s=0;%>
   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
       <td nowrap>
          <table>
  <tr>
  <td>
   <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
     <hrms:menuitem name="file" label="文件" function_id="270201,0C310">
     <hrms:menuitem name="mitem1" label="考勤表" icon="/images/quick_query.gif" url="javascript:kqreport();" function_id="2702013,0C3102"/>
     <hrms:menuitem name="mitem2" label="高级花名册" icon="/images/quick_query.gif" url="javascript:go_must();" function_id="2702014,0C3104"/>
     
     <hrms:menuitem name="mitem3" label="模板下载"  function_id="2702017,0C3108">
      	<hrms:menuitem name="mitem1" label="固定模板" icon="/images/export.gif" url="javascript:downLoadTemp();" function_id="27020171,0C31081"/>
      	<hrms:menuitem name="mitem2" label="自定义模板" icon="/images/export.gif" url="javascript:customTemp();" function_id="27020172,0C31082"/>
     </hrms:menuitem>
     <hrms:menuitem name="mitem4" label="import.tempData" icon="/images/import.gif" url="javascript:exportTempData();" function_id="2702018,0C3109"/>
     </hrms:menuitem>
     <hrms:menuitem name="rec3" label="编辑" function_id="270204,0C315,2702015,0C3106,2702016,0C3107,2702012,0C3114">
     <hrms:menuitem name="mitem1" label="显示&隐藏指标" icon="/images/quick_query.gif" url="javascript:fashion_set('1');" command="" function_id="2702015,0C3106"/>
     <hrms:menuitem name="mitem2" label="调整指标顺序" icon="/images/quick_query.gif" url="javascript:fashion_set('0');" function_id="2702016,0C3107"/>
     <hrms:menuitem name="mitem3" label="查询" icon="/images/view.gif" url="javascript:selectKq();" function_id="2702012,0C3114"/>
    
     <% 
      DailyRegisterForm dailyRegisterForm=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");	
      String d = dailyRegisterForm.getUp_dailyregister();
      if(d.equals("0")){%>   
     <hrms:menuitem name="mitem4" label="批量修改" icon="/images/write.gif" url="javascript:kq_batch();" function_id="2702040,0C3151"/>   
     <% }%>
     <hrms:menuitem name="mitem4" label="人员排序" icon="/images/sort.gif" url="javascript:sort();" function_id="2702043,0C3128"/>
      <hrms:menuitem name="mitem4" label="同步人员顺序" icon="/images/sort.gif" url="javascript:syncorder();" function_id="0C3127,2702042"/>
     </hrms:menuitem>
     <hrms:menuitem name="rec" label="业务处理" function_id="270202,0C312,2702011,0C3105,2702010,0C3100">
     <hrms:menuitem name="mitem2" label="生成日考勤明细表" icon="/images/write.gif"  url="javascript:go_creat();" command="" function_id="2702010,0C3100"/> 
     <hrms:menuitem name="mitem1" label="人员变动比对" icon="/images/add_del.gif" url="javascript:empchange();" function_id="2702011,0C3105"/>   
     <hrms:menuitem name="mitem3" label="统计" icon="/images/add_del.gif" url="javascript:pickup_data('${dailyRegisterForm.code}','${dailyRegisterForm.kind}');" function_id="2702020,0C3120"/>        
     <hrms:menuitem name="mitem4" label="计算" icon="/images/sort.gif" url="javascript:go_count();" function_id="2702021,0C3121"/>
     <hrms:menuitem name="mitem5" label="月汇总" icon="/images/write.gif" url="javascript:gocollect();" function_id="2702022,0C3122"/>  
     <hrms:menuitem name="mitem6" label="不定期汇总" icon="/images/add_del.gif" url="javascript:ambiquity();" function_id="2702023,0C3124"/>    
     <hrms:menuitem name="mitem7" label="个人业务处理" icon="/images/quick_query.gif" url="javascript:sing_operation();" function_id="2702024,0C3123"/>
     </hrms:menuitem>
     <hrms:menuitem name="rec2" label="浏览" function_id="270203,0C311">
     <% 
     	String flg=daily.getFlag();
     	//<hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:show_state('0');" function_id="2702032,0C3113"/> 
     	if(flg!=null&&flg.equals("0")){
     %>   
     <hrms:menuitem name="mitem1" label="月汇总" icon="/images/sort.gif" url="javascript:show_state('1');" function_id="2702030,0C3110"/>
     <hrms:menuitem name="mitem2" label="不定期" icon="/images/write.gif" url="javascript:show_state('2');" function_id="2702031,0C3117"/>  
     <%}else{%>
     <hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:show_state('0');" function_id="2702032,0C3113"/> 
     <hrms:menuitem name="mitem2" label="月汇总" icon="/images/sort.gif" url="javascript:show_state('1');" function_id="2702030,0C3110"/>
     <hrms:menuitem name="mitem3" label="不定期" icon="/images/write.gif" url="javascript:show_state('2');" function_id="2702031,0C3117"/>  
     <%} %>
     </hrms:menuitem>
 </hrms:menubar>
 </td>
 </tr>
  </table>
       </td>        
       <logic:equal name="dailyRegisterForm" property="select_flag" value="1">
        <td align= "left" nowrap>
	           <html:button styleClass="mybutton" property="bc_btn1" onclick="viewAll();"><bean:message key="workdiary.message.view.all.infor"/></html:button>
	        </td>
	   </logic:equal>
	   
	    <td align= "left" nowrap>
       <!-- <select name="showstate" size="1" onchange="show_state(this)">        
            <option value="0">日明细</option>  
            <option value="1">月汇总</option>   
            <option value="2">不定期</option>   
       </select>-->
        <html:hidden name="dailyRegisterForm" property="returnURL" styleClass="text"/>
           &nbsp;审批状态 </td>  <td align= "left" nowrap>
           <html:select name="dailyRegisterForm" property="sp_flag" size="1" onchange="change();">
                <html:optionsCollection property="splist" value="dataValue" label="dataName"/>	        
            </html:select>
       </td>  
	   <logic:equal name="dailyRegisterForm" property="status_flag" value="1">
	        <td nowrap>
	              &nbsp;&nbsp;&nbsp;数据状态&nbsp;&nbsp;
	             <html:select name="dailyRegisterForm" property="kqstatus"  size="1" onchange="change()">
            	   <html:optionsCollection property="kqstatuslist" value="dataValue" label="dataName"/>
                 </html:select>
	        </td>
	   </logic:equal>	        
	    <td align= "left" nowrap>
      &nbsp;项目过滤 </td>  <td align= "left" nowrap>
           <html:select name="dailyRegisterForm" property="kqitem" size="1" onchange="change();">
                <html:optionsCollection property="kqitemlist" value="dataValue" label="dataName"/>	        
            </html:select>
       </td> 
      
      <td align= "left" nowrap>
        <bean:message key="kq.register.daily.menu"/> </td>  
        <td align= "left" nowrap>
        ${dailyRegisterForm.workcalendar} 
        <html:hidden name="dailyRegisterForm" property="code" styleClass="text"/>                   
      </td>  
      <td align= "left" nowrap>
            按 </td>  <td align= "left" nowrap>
            <html:select name="dailyRegisterForm" property="select_type"  size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select>
           <input type="text" name="select_name" value='<bean:write name="dailyRegisterForm"  property="select_name"/>'
             class="inputtext" style="width:100px;font-size:10pt;text-align:left">
           &nbsp;<button extra="button" onclick="javascript:selectflag();">查询</button> 
           
       </td>
       <td id="querydesc" nowrap>
               [&nbsp;<a href="javascript:visibleblank();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;
       </td>
       </tr>
    </table>
  </td>
 </tr>
       
 <tr>
  <td id="tab_include">
  
 <%int i=0;
   int n=0;
   String name=null;
   int num_s=0;
   int lock=0;
 %>
 <script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-130)+";width:100%'  >");
 </script> 
    <table width="100%" border="0" id="GV" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      	 <!-- 多个style标签 在非IE浏览器不起作用 wangb 20171101 -->
         <tr class="trShallow1"  style="background-color:#fff;display:none;" id="queryblank">
		 		<td width="100%"  class="RecordRow" colspan="${dailyRegisterForm.columnCount}"  nowrap valign="middle">
		 			<table width="400px">
		 				<tr class="trShallow1" style="background-color:#fff">
		 					<td class="RecordRow" style="border: none;"   nowrap >
		        
		           <html:select name="dailyRegisterForm" property="select_pre" size="1" styleId="pre" onchange="change();">
		                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
		            </html:select>&nbsp;
		     </td>
		     <td class="RecordRow" style="border: none;"   nowrap >
		      &nbsp;&nbsp;&nbsp;&nbsp;
		        <bean:message key="kq.register.daily.lrtype"/>&nbsp;
		         <input name="pl_type" type="radio" value="row" onclick="return inputNum(1)" checked> 
		        <bean:message key="kq.register.daily.lrtyperow"/>
		       <input name="pl_type" type="radio" value="line" onclick="return inputNum(<bean:write 
		
		           name="dailyRegisterForm"  property="num"/>)">
		       <bean:message key="kq.register.daily.lrtypeline"/>
		       
		       &nbsp;&nbsp;&nbsp;&nbsp;
		       <html:hidden name="dailyRegisterForm" property="msg" styleClass="text" value=""/>
		       <html:hidden name="dailyRegisterForm" property="code" styleClass="text"/>
		      <html:hidden name="dailyRegisterForm" property="userbase" styleClass="text"/>
		      <html:hidden name="dailyRegisterForm" property="overrule" styleId='overrule' 	styleClass="text"/>
		     </td>
		     <td class="RecordRow" style="border: none;"   nowrap >
		      &nbsp;
		      	时间显示方式
		      	</td>
		      	<td class="RecordRow" style="border: none;"   nowrap >
		      	<select size="1"   name="selectys"   onchange="changeys(this.value);">
		      		 <option   value="1">默认</option>   
		      		 <option   value="2">HH:mm</option> 
		      	</select>
		    
		       </td>
		     </tr>
      </table>
        </td>
    </tr>
      
      
      
         <tr>
            <td align="center" class="TableRow" nowrap style="border-top:none;">
		<!--<bean:message key="column.select"/>&nbsp;-->
		    <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>
            <logic:equal value="all" name="dailyRegisterForm" property="registerdate">
	           	<td align="center" class="TableRow" nowrap style="border-top:none;">
	           		<bean:message key="kq.wizard.riqi"/>
	           	</td>
           </logic:equal>
            
            <logic:iterate id="element"    name="dailyRegisterForm"  property="fielditemlist" indexId="index"> 
          
              <logic:equal name="element" property="visible" value="true">
               
                 <logic:equal name="element" property="itemtype" value="A">
                    <%if(i<lockedNum) {%>
                        <td align="center" class="TableRow" nowrap style="border-top:none;">
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}else{ %>
                        <td align="center" class="TableRow" nowrap style="border-top:none;">
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                  <%}
                   i++;
                   %>
                 </logic:equal>
                 <logic:notEqual name="element" property="itemtype" value="A">
                   <td align="center" class="TableRow" nowrap style="border-top:none;">
                   <hrms:textnewline text="${element.itemdesc}" len="5"></hrms:textnewline>
                   
                   </td>
                 </logic:notEqual>
              </logic:equal>
           </logic:iterate>
             <!-- <td align="center" class="TableRow" nowrap>
                 审批意见&nbsp; 
              </td>-->      	        
         </tr>
     
      <%i=0; %>
      <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.sqlstr" table="" where_str="dailyRegisterForm.strwhere" columns="dailyRegisterForm.columns" order_by="dailyRegisterForm.orderby" page_id="pagination" pagerows="${dailyRegisterForm.pagerows}"  indexes="indexes">
        
          <%          
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}          
          %>   
          <bean:define id="nbase" name="element" property="nbase" scope="page"></bean:define>     
          <bean:define id="a0100" name="element" property="a0100" scope="page"></bean:define> 
          <bean:define id="q03z0" name="element" property="q03z0" scope="page"></bean:define>   
          <% int  inNum=0;lock=0;%>   
           <td align="center" class="RecordRow" nowrap>   
                <hrms:checkmultibox name="dailyRegisterForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
           </td> 
           <logic:equal value="all" name="dailyRegisterForm" property="registerdate">
           <td align="center" class="RecordRow" nowrap>   
                <bean:write name="element" property="q03z0"/>&nbsp;
           </td> 
           </logic:equal>
            <logic:iterate id="info" name="dailyRegisterForm"  property="fielditemlist" indexId="index"> 
              <%
               FieldItem item=(FieldItem)pageContext.getAttribute("info");
               name=item.getItemid();    
              %>
               <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <%if(lock<lockedNum) {%>
                         <td align="left" class="RecordRow" nowrap>
                       <%}else{ %>
                          <td align="left" class="RecordRow" nowrap>
                       <% }
                       lock++;
                       %>
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <logic:notEqual name="info" property="itemid" value="e01a1">
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${dailyRegisterForm.uplevel}"/>  	      
                              &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                             <html:hidden name="element" property="${info.itemid}"/>   
                          </logic:notEqual>                         
                          <logic:equal name="info" property="itemid" value="e01a1">                          
                          		<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>
                          		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          		<html:hidden name="element" property="${info.itemid}"/>                            	
                          </logic:equal>  
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                          <logic:notEqual name="info" property="itemid" value="a0101">
                          	
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                          </logic:notEqual>
                          <logic:equal name="info" property="itemid" value="a0101">
						         <bean:define id="nbase1" name="element" property="nbase"/>
						         <bean:define id="a01001" name="element" property="a0100"/>
						         <%
						         	String nbase2=PubFunc.encrypt(nbase1.toString());
						         	String a01002=PubFunc.encrypt(a01001.toString());
						         %>
                                &nbsp;<a href="/kq/register/single_register.do?b_single=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&userbase=<%=nbase2 %>&start_date=${dailyRegisterForm.start_date}&end_date=${dailyRegisterForm.end_date}&A0100=<%=a01002 %>&moduleFlag=${dailyRegisterForm.moduleFlag}">
                               <bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                          </logic:equal>
                       </logic:equal>
                      </td>                      
                   </logic:equal>
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap>
                       <%
                         num_s++;
                         request.setAttribute("num_s",num_s+"");
                       %>
                       <logic:notEqual name="dailyRegisterForm" property="up_dailyregister" value="1"> <!--允许修改  -->
                       <%if((userView.hasTheFunction("2702026") && "0".equals(moduleFlag)) || (userView.hasTheFunction("0C3101") && "1".equals(moduleFlag)) || userView.isSuper_admin()) {%>
                          <logic:equal name="element" property="q03z5" value="01">  
                             <logic:notEqual name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}"  size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');" />&nbsp; 
                             </logic:notEqual>
                             <logic:equal name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                             </logic:equal> 
                          </logic:equal> 
                          <logic:equal name="element" property="q03z5" value="07">  
                            <logic:notEqual name="element" property="${info.itemid}" value="0">
                              &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');" />&nbsp; 
                            </logic:notEqual>
                             <logic:equal name="element" property="${info.itemid}" value="0"> 
                               &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');" />&nbsp; 
                             </logic:equal>
                          </logic:equal> 
                          <%--<logic:equal value="1" name="dailyRegisterForm" property="haveApprove"><!-- 可以修改，且有审批权限 -->
                          	<logic:equal name="element" property="q03z5" value="02">  
                             <logic:notEqual name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}"  size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onchange="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');" />&nbsp; 
                             </logic:notEqual>
                             <logic:equal name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onchange="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                             </logic:equal> 
                            </logic:equal> 
                            <logic:equal name="element" property="q03z5" value="08">  
                             <logic:notEqual name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}"  size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onchange="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');" />&nbsp; 
                             </logic:notEqual>
                             <logic:equal name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onchange="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                             </logic:equal> 
                            </logic:equal> 
                          </logic:equal>
                          <logic:notEqual value="1" name="dailyRegisterForm" property="haveApprove"><!-- 可以修改，但没审批权限 -->
                          	<logic:equal name="element" property="q03z5" value="02">  
                             <logic:notEqual name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;&nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:notEqual>
                              <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:equal>
                            </logic:equal> 
                            <logic:equal name="element" property="q03z5" value="08">  
                             <logic:notEqual name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;&nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:notEqual>
                              <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:equal>
                            </logic:equal> 
                          </logic:notEqual>--%>
                          <logic:notEqual name="element" property="q03z5" value="01">
                            <logic:notEqual name="element" property="q03z5" value="07">  
	                              <logic:notEqual name="element" property="${info.itemid}" value="0">                                                                                                   
	                               &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
	                              </logic:notEqual>
	                              <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
	                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
	                              </logic:equal>
                            </logic:notEqual> 
                          </logic:notEqual>
                       <% } else { %>
                             <logic:notEqual name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:notEqual>
                              <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:equal>
                       <% } %>
                          
                        </logic:notEqual>
                        
                        <logic:equal name="dailyRegisterForm" property="up_dailyregister" value="1"><!-- 不允许修改 -->
                          <logic:notEqual name="element" property="${info.itemid}" value="0">  
                            &nbsp;<html:text name="element" property="${info.itemid}"   size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                          </logic:notEqual>
                           <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;
                              </logic:equal>
                        </logic:equal>
                           <%
                                inNum++;
                            %> 
                      </td>
                  </logic:equal>
                  <logic:equal name="info" property="itemtype" value="D">
                  	<td align="center" class="RecordRow" nowrap>
                  		<logic:equal name="info" property="itemid" value="ctime">
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                        </logic:equal>
                        <logic:equal name="info" property="itemid" value="modtime">
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                        </logic:equal>
                        <logic:notEqual name="info" property="itemid" value="ctime">
                        	<logic:notEqual name="info" property="itemid" value="modtime">
	                        	<logic:equal value="18" name="info" property="itemlength">
	                  	    	  &nbsp;<html:text name="element" property='${info.itemid}' size="18" maxlength="18" styleClass="text" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,true,true);' onchange="rep_dateValue(this);" readonly="true"/>&nbsp;
	                        	</logic:equal>
	                        	<logic:notEqual value="18" name="info" property="itemlength">
                  	    	  	  &nbsp;<html:text name="element" property='${info.itemid}' size="15" maxlength="10" styleClass="text" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this,'${info.itemlength}');" readonly="true"/>&nbsp;
	                        	</logic:notEqual>
                  	    	 </logic:notEqual>
                  	    </logic:notEqual>
                  	</td>
                  </logic:equal>
                  <logic:equal name="info" property="itemtype" value="M">
                  	<td align="center" class="RecordRow" nowrap>
                  	    &nbsp;
                  	</td>
                  </logic:equal>
                </logic:equal>   
               <!---->               
            </logic:iterate>
            <!--<td class="RecordRow" nowrap align="center">
            <img src="/images/edit.gif" border="0" alt="点击察看意见" onclick="showOverrule('<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>','${dailyRegisterForm.kq_duration}');">
            </td>-->  
            <%
            if(inNum!=0){
              n=inNum;
            }
            i++;  
            %>  
          </tr>
          <%
          s++;
          %>
        </hrms:paginationdb> 
        <script language="javascript"> 
          returnS(<%=s%>); 
        </script> 
       	<script language="javascript"> 
          returnLine(<bean:write name="dailyRegisterForm"  property="num"/>); 
        </script> 
        <script language="javascript"> 
            returnInNum(<%=n%>); 
        </script>                             	    		        	        	        
    </table>
    <script language='javascript' >
	document.write("</div>");
    </script>
   </td>
  </tr>   
  <tr>
   <td style="" >
    <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-120)+";width:100%;margin-top: 25px'  >");
	</script>
     <table  width="100%" class="RecordRowTop0">
       <tr>          
       <td  valign="bottom" align="left" height="30" nowrap>
           <hrms:paginationtag name="dailyRegisterForm"
								pagerows="${dailyRegisterForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td   valign="bottom" align="right" nowrap>
	     <hrms:paginationdblink name="dailyRegisterForm" property="pagination" nameId="dailyRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	  <td></td>
	</tr>
     </table>
     <table  width="100%">
       <tr>   
          <td align="left"  nowrap>       
          <!--     
	      <hrms:priv func_id="2702026,0C3101"> 
	       <input type="button" name="b_save" value='<bean:message key="kq.emp.button.save"/>' onclick="getSelect('<bean:write name="dailyRegisterForm"  property="columns"/>','<bean:write name="dailyRegisterForm"  property="code"/>');" class="mybutton">  
	      </hrms:priv>   
	      -->    
	      <hrms:priv func_id="2702063,0C3133"> 
	       <input type="button" name="b_save" value='个人驳回' onclick="return_overrule();" class="mybutton">  
	      </hrms:priv>
	      <logic:equal value="dxt" name="dailyRegisterForm" property="returnvalue">  	    
	         <hrms:tipwizardbutton flag="workrest" target="il_body" formname="dailyRegisterForm"/> 
	      </logic:equal>
	      
          <bean:write name="dailyRegisterForm" property="state_message" filter="true"/>&nbsp;
       </td>     
       </tr>
     </table>
     <script language='javascript' >
	  document.write("</div>");
      if(!getBrowseVersion()){//非IE浏览器 getBrowseVersion()返回值为0 
    	  var tblContainer = document.getElementById('tbl-container');
    	  tblContainer.style.width='99%';
    	  var table_page = document.getElementById('page');
    	  table_page.style.width = '99.2%';
   		  table_page.style.marginTop='38px';
      }
    </script>
   </td>
 </tr>
 
</table>
</logic:equal>
<logic:notEqual name="dailyRegisterForm" property="error_flag" value="0">
<script language="javascript">
var error_str=kqErrorProcess('<bean:write name="dailyRegisterForm"  property="error_flag"/>','<bean:write name="dailyRegisterForm"  property="error_message"/>','<bean:write name="dailyRegisterForm"  property="error_return"/>');
document.write(error_str);
</script>
</logic:notEqual>

</html:form>
<iframe name="mysearchframe" style="display: none;"></iframe>
<script language="javascript">
 read_Pigeonflag("${dailyRegisterForm.pigeonhole_flag}");
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
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td id="wait_title" class="td_style common_background_color" height="24"><bean:message key="classdata.isnow.wiat"/></td>
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
<script language="javascript">
 //MusterInitData();
 hide_nbase_select('select_pre');
 //myScroll('tbl-container',1,6,'GV','tab_include');
 function showmsg(){
 	<logic:notEmpty name="dailyRegisterForm" property="msg">
 		var msg = "<bean:write name="dailyRegisterForm" property="msg"/>";
 		alert(msg);
 	</logic:notEmpty> 
 }
 window.setTimeout("showmsg()",100);
 document.oncontextmenu = function() {return false;}
</script>
