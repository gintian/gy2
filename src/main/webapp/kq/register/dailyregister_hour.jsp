<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm"%> 
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript" src="/js/meizzDate_saveop.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
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
</script> 
<script language="javascript">
 function go_creat()
   {
      //dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_link=link";
      //dailyRegisterForm.submit();
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      target_url="/kq/register/daily_registerdata.do?b_link=link";
       var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
	    return false;	   
	if(return_vo.pick_type!='')
	{
	    var waitInfo=eval("wait");	   
	    waitInfo.style.display="block";	
	    var tul="/kq/register/daily_registerdata.do?b_creat=link&creat_type="+return_vo.creat_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end+"&creat_pick="+return_vo.creat_pick;
	    dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_creat=link&creat_type="+return_vo.creat_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end+"&creat_pick="+return_vo.creat_pick+"&creat_state="+return_vo.creat_state;
	    dailyRegisterForm.submit();
	}
   }   
   function empchange()
   {
       var len=document.dailyRegisterForm.elements.length;
       var i;
     
        for (i=0;i<len;i++)
        {
         if (document.dailyRegisterForm.elements[i].type=="checkbox")
          {
             
            document.dailyRegisterForm.elements[i].checked=false;
          }
        }
      dailyRegisterForm.action="/kq/register/empchange.do?b_search=link&registerdate=${dailyRegisterForm.registerdate}";
      dailyRegisterForm.submit();
   }
   
   function go_count()
   {
      if(confirm(KQ_DAILY_DATA_SAVE_HINT))
      {
         var target_url;
         var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
         target_url="/kq/register/count_register.do?b_select=link";
         //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
          var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:596px; dialogHeight:354px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      }
   }
   function showCollect()
   {
      dailyRegisterForm.action="/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body&flag=noself";
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
    function show_ambiquity()
   {
       dailyRegisterForm.action="/kq/register/ambiquity/search_ambiquity.do?b_search=link&action=search_ambiquitydata.do&target=mil_body&flag=noself";
       dailyRegisterForm.target="il_body";
       dailyRegisterForm.submit();
   } 
   function selectKq()
   {
       var target_url;
       var winFeatures = "dialogHeight:600px; dialogLeft:450px;";        
       target_url="/kq/register/select/selectfiled.do?b_init=link&select_flag=q03";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
        var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:596px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
		   return false;	   
		if(return_vo)
		{
		   dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link&select_flag=1&selectResult="+return_vo;
           dailyRegisterForm.submit();
		}
   }  
 
   function pickup_data()
   {
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      target_url="/kq/register/daily_registerdata.do?b_pickupdate=link";
       var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
		   return false;	   
	if(return_vo.pick_type!='')
	{
	    var waitInfo=eval("wait");	   
	    waitInfo.style.display="block";	      
	    dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_pickup=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&pick_type="+return_vo.pick_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end;
	    dailyRegisterForm.submit();
	}
   }   
   function go_search()
   {
      dailyRegisterForm.action="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body";
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
   function sing_count()
   {
       if(confirm(KQ_DAILY_DATA_SAVE_HINT))
       {
         dailyRegisterForm.action="/kq/register/sing_oper/singcountdata.do?b_saveselect=link";
         dailyRegisterForm.submit();
       }
   }
   function sing_collect()
   {
       if(confirm(KQ_DAILY_DATA_SAVE_HINT))
      {
         dailyRegisterForm.action="/kq/register/sing_oper/singcollectdata.do?b_saveselect=link";
         dailyRegisterForm.submit();
      }
   }
    function sing_pickup()
   {
      dailyRegisterForm.action="/kq/register/sing_oper/singpickdata.do?b_saveselect=link";
      dailyRegisterForm.submit();
   }
   function sing_operation()
   {
       if(confirm(KQ_DAILY_DATA_SAVE_HINT))
       {
           dailyRegisterForm.action="/kq/register/sing_oper/sing_operation.do?b_saveselect=link";
           dailyRegisterForm.submit();
       }
   } 
   function indicator()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/register/indicator/indicator.do?b_query=link&re_flag=1";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   }
   function kqreport()
   {
      dailyRegisterForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&userbase=${dailyRegisterForm.userbase}&code=${dailyRegisterForm.code}&coursedate=${dailyRegisterForm.kq_duration}&kind=${dailyRegisterForm.kind}&self_flag=tran";
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
   function return_overrule()
   {
       var len=document.dailyRegisterForm.elements.length;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
           if (document.dailyRegisterForm.elements[i].type=="checkbox")
            {
              if( document.dailyRegisterForm.elements[i].checked==true)
                isCorrect=true;
            }
       }
       if(!isCorrect)
       {
          alert(KQ_SELECT_PERSON);
          return false;
       }
       //dailyRegisterForm.action="/kq/register/select_collectdata.do?b_saveover=link";
       //dailyRegisterForm.submit();
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/register/select_collectdata.do?br_saveover=link&flag=day&sb=new";
       //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
       return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:540px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
	    return false;	
       if(return_vo.save=="1")
       {
           var overrule=return_vo.text;
           var o_obj=document.getElementById('overrule');
           o_obj.value=overrule;
           dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_rule=link";
           dailyRegisterForm.target="mil_body";
           dailyRegisterForm.submit();
       }
   }
    function changeys(dd)
 {
 	if(dd==1){
 		dailyRegisterForm.action="/kq/register/daily_registerdata_hour.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}";
    	dailyRegisterForm.submit();
 	}else if(dd==2){
 		dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_search=link";
    	dailyRegisterForm.submit();
 	}
 }
</script> 
<html:form action="/kq/register/daily_registerdata_hour">

<logic:equal name="dailyRegisterForm" property="error_flag" value="0">
<script language="javascript">
function showOverrule(userbase,a0100,kq_duration)
    	{
    	   var target_url;
    	   var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
    	   target_url="/kq/register/daily_registerdata.do?b_overrlue=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+kq_duration;
    	   newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
    	}
  function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
   function change()
   {
      dailyRegisterForm.action="/kq/register/daily_registerdata_hour.do?b_hour=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}";
      dailyRegisterForm.submit();
   }  
 
   function getSelect(columns,code)
   {  	  
     if(!confirm(KQ_DAILY_DATA_SAVE_YESNO))
     {
         return false;
     }else
     {
        var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
     }
     var i=0;
     var r=0;	    
     var y=0;
     var st=0;
     var sr="";
     st=columns.lastIndexOf(","); 
     if(st==columns.length-1)   
       columns=columns.substring(0,st); 
     columns=columns+",";
     var forms= new Array();
     var hashvo=new ParameterSet();
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
        hashvo.setValue("forms",forms);	
        hashvo.setValue("columns",columns);
        hashvo.setValue("code",code);
        hashvo.setValue("kind","${dailyRegisterForm.kind}");    		
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'15301110005'},hashvo);
     }else
     {
        alert("没有记录，无法执行此操作！");
        return false;
     }
     
   }
  
  function isArray(obj) 
  { 
      return (obj.constructor.toString().indexOf('Array')!= -1);
  } 
  function showSelect(outparamters)
  { 
     var tes=outparamters.getValue("type");
     MusterInitData(); 
     if(tes=="success"){
        alert("数据保存成功");
     }else if(tes=="nosave"){
       alert("数据保存失败");
     }else{
        alert("数据保存失败");
     }
  }
  function gocollect()
   {
      //dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_collect=link&action=collect_registerdata.do&target=mil_body";
     // dailyRegisterForm.target="il_body";
      //dailyRegisterForm.submit();
      if(confirm("请确认更改数据是否已保存!"))
      {
        dailyRegisterForm.action="/kq/register/daily_registerdata.do?br_collect=link";
        dailyRegisterForm.target="il_body";
        dailyRegisterForm.submit();
       }
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
     {
        showCollect();//月汇总
     }else if(state==2)
     {
        show_ambiquity();//不定期
     }
  }
function go_must(){
      var returnURL = getEncodeStr("${dailyRegisterForm.returnURL}");
      var condition = getEncodeStr(getDecodeStr("${dailyRegisterForm.condition}"));
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${dailyRegisterForm.relatTableid}&closeWindow=1";
      	urlstr+="&returnURL="+returnURL+"&condition="+condition;
      window.open(urlstr);
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
           //var target_url;
           //var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           //target_url="/kq/register/notapp.do?b_search=link";
           //newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=140,left=180,width=596,height=354'); 
           alert('请先将考勤信息进行审批！');
           return false;
        }
        if(q07=="have")
        {
           //var target_url;
           //var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           //target_url="/kq/register/notq07.do?b_search=link";
           //newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
           alert("请先将部门考勤信息进行日汇总！");
           return false;
        }
        if(q09=="have")
        {
           //var target_url;
           //var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           //target_url="/kq/register/notq09.do?b_search=link";
           //newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=250,width=596,height=354'); 
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
   function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   var check_obj;
   function checkValue(obj,item)
   {
      check_obj=obj;
      var values=obj.value;
      if(values=="")
      {
         return false;
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("kq_value",values);
      hashvo.setValue("kq_item",item);
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'15301110077'},hashvo);
   }
   function showCheckFlag(outparamters)
   {
      var check_flag=outparamters.getValue("flag");
      var check_mess=outparamters.getValue("mess");
      if(check_flag=="false")
      {
         alert(check_mess);         
         check_obj.focus(); 
      }
   } 
   function viewAll()
   {
       dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&select_flag=0";
      dailyRegisterForm.submit();
   }
   function selectflag()
   {
      dailyRegisterForm.action="/kq/register/daily_registerdata_hour.do?b_query=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&select_flag=1";
      dailyRegisterForm.submit();
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function fashion_set(flag)
   {
      var target_url="";
      var return_vo;
      if(flag=="0")
      {
         target_url="/kq/options/adjustcode/adjustcode.do?b_order=link&table=q03&flag=1&isSave=no";
         return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      }else if(flag=="1")
      {
         
         target_url="/kq/options/adjustcode/adjustcode.do?b_hideview=link`table=q03`flag=1`isSave=no";
         var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
         return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      }
      if(return_vo)
        change();
   }
   function getdate(tt)
   {
       var strvalue=tt.value;
       strvalue=strvalue.replace(/\-/g,".");       
       tt.value=strvalue;
   }

   var blankhidden=0;
   function visibleblank(){
   	var blank=document.getElementById("queryblank");
   	if(blankhidden==0){
   	 blank.style.display='block';
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
<table> 

 <tr>
  <td>
  
  <% int s=0;%>
   <table width="30%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>
       <td>
          <table>
  <tr>
  <!-- 
  <td>
   <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
     <hrms:menuitem name="file" label="文件" function_id="270201,0C310">
     <hrms:menuitem name="mitem1" label="生成日考勤明细表" icon="/images/write.gif"  url="javascript:go_creat();" command="" function_id="2702010,0C3100"/> 
     <hrms:menuitem name="mitem2" label="人员变动比对" icon="/images/add_del.gif" url="javascript:empchange();" function_id="2702011,0C3105"/>
     <hrms:menuitem name="mitem3" label="查询" icon="/images/view.gif" url="javascript:selectKq();" function_id="2702012,0C3114"/>       
     <hrms:menuitem name="mitem4" label="考勤表" icon="/images/quick_query.gif" url="javascript:kqreport();" function_id="2702013,0C3102"/>
     <hrms:menuitem name="mitem5" label="高级花名册" icon="/images/quick_query.gif" url="javascript:go_must();" function_id="2702014,0C3104"/>
     <hrms:menuitem name="mitem6" label="显示&隐藏指标" icon="/images/write.gif" url="javascript:fashion_set('1');" command="" function_id="2702015,0C3106"/>
     <hrms:menuitem name="mitem7" label="指标顺序" icon="/images/quick_query.gif" url="javascript:fashion_set('0');" function_id="2702016,0C3107"/>       
     </hrms:menuitem>
     <hrms:menuitem name="rec" label="业务处理" function_id="270202,0C312">   
     <hrms:menuitem name="mitem1" label="统计" icon="/images/add_del.gif" url="javascript:pickup_data();" function_id="2702020,0C3120"/>        
     <hrms:menuitem name="mitem2" label="计算" icon="/images/sort.gif" url="javascript:go_count();" function_id="2702021,0C3121"/>
     <hrms:menuitem name="mitem3" label="月汇总" icon="/images/write.gif" url="javascript:gocollect();" function_id="2702022,0C3122"/>  
     <hrms:menuitem name="mitem4" label="不定期汇总" icon="/images/add_del.gif" url="javascript:ambiquity();" function_id="2702023,0C3124"/>    
     <hrms:menuitem name="mitem5" label="个人业务处理" icon="/images/quick_query.gif" url="javascript:sing_operation();" function_id="2702024,0C3123"/>    
     </hrms:menuitem>
     <hrms:menuitem name="rec2" label="浏览" function_id="270203,0C311">   
     <hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:show_state('0');" function_id="2702032,0C3113"/>        
     <hrms:menuitem name="mitem2" label="月汇总" icon="/images/sort.gif" url="javascript:show_state('1');" function_id="2702030,0C3110"/>
     <hrms:menuitem name="mitem3" label="不定期" icon="/images/write.gif" url="javascript:show_state('2');" function_id="2702031"/>  
     </hrms:menuitem>
 </hrms:menubar>
 </td>
 -->
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
        <html:hidden name="dailyRegisterForm" property="condition" styleClass="text"/>  
           &nbsp;审批状态
            </td><td align= "left" nowrap>
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
      &nbsp;项目过滤
       </td><td align= "left" nowrap>
           <html:select name="dailyRegisterForm" property="kqitem" size="1" onchange="change();">
                <html:optionsCollection property="kqitemlist" value="dataValue" label="dataName"/>	        
            </html:select>
       </td> 
      <td align= "left" nowrap>
        <bean:message key="kq.register.daily.menu"/>
         </td><td align= "left" nowrap>
        ${dailyRegisterForm.workcalendar} 
        <html:hidden name="dailyRegisterForm" property="code" styleClass="text"/>                   
      </td>       
      <td align= "left" nowrap>
             按  </td><td align= "left" nowrap>
             <html:select name="dailyRegisterForm" property="select_type"  size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select>
           <input type="text" name="select_name" value="${dailyRegisterForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">
           &nbsp;<button extra="button" onclick="javascript:selectflag();">查询</button> 
           &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
       </td>
      <td id="querydesc" nowrap>
               [&nbsp;<a href="javascript:visibleblank();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;
       </td>
    </tr>
    </table>
  </td>
 </tr>
 
 
 <tr>
  <td>
  
 <%int i=0;
   int n=0;
   String name=null;
   int num_s=0;
 %>
  <script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-130)+";width:100%'  >");
  </script> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
 <tr class="trShallow1" style="background-color:#fff" id="queryblank" style="display: none;">
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
			       <html:hidden name="dailyRegisterForm" property="code" styleClass="text"/>
			      <html:hidden name="dailyRegisterForm" property="userbase" styleClass="text"/>
			      <html:hidden name="dailyRegisterForm" property="overrule" styleId='overrule' styleClass="text"/>
			      </td>
		  		   <td class="RecordRow" style="border: none;"   nowrap >
			      &nbsp;
			      	时间显示方式
			      	</td>
		   		  <td class="RecordRow" style="border: none;"   nowrap >
			      	<select size="1"   name="selectys"   onchange="changeys(this.value);">
			      		 <option   value="1">HH:mm</option> 
			      		 <option   value="2">默认</option>   
			      	</select>
			     
			       </td>
			     </tr>
	      </table>
	        </td>
	    </tr>
 
         <tr>
            <td align="center" class="TableRow" nowrap style="border-left:none;border-top:none">
				<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>  
            <logic:iterate id="element"    name="dailyRegisterForm"  property="fielditemlist" indexId="index"> 
          
              <logic:equal name="element" property="visible" value="true">
                <td align="center" class="TableRow" nowrap style="border-top:none">
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
                </td>
              </logic:equal>
           </logic:iterate>
             <!-- <td align="center" class="TableRow" nowrap>
                 审批意见&nbsp; 
              </td>-->      	        
         </tr>      
      
      <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.sqlstr" table="" where_str="dailyRegisterForm.strwhere" columns="dailyRegisterForm.columns" order_by="dailyRegisterForm.orderby" pagerows="19" page_id="pagination" indexes="indexes">
          <%
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}          
          %>        
          <% int  inNum=0;%>   
           <td align="center" class="RecordRow" nowrap style="border-left:none">   
                <hrms:checkmultibox name="dailyRegisterForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
           </td> 
            <logic:iterate id="info" name="dailyRegisterForm"  property="fielditemlist" indexId="index"> 
              <%
               DailyRegisterForm dailyRegisterForm=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
               FieldItem item=(FieldItem)pageContext.getAttribute("info");
               name=item.getItemid(); 
              %>
               <logic:equal name="info" property="visible" value="false">
                  <html:hidden name="element" property="${info.itemid}"/>  
                </logic:equal>
                <logic:equal name="info" property="visible" value="true">
                <!--字符型-->
                    <logic:equal name="info" property="itemtype" value="A">
                       <logic:notEqual name="info" property="codesetid" value="0">
                          <td align="left" class="RecordRow" nowrap>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                              &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                             <html:hidden name="element" property="${info.itemid}"/>                              
                          </td>  
                       </logic:notEqual>
                       <logic:equal name="info" property="codesetid" value="0">
                          <logic:notEqual name="info" property="itemid" value="a0101">
                             <td align="left" class="RecordRow" nowrap>
                               &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                             </td> 
                          </logic:notEqual>
                          <logic:equal name="info" property="itemid" value="a0101">
                              <td align="left" class="RecordRow" nowrap> &nbsp;
                               <a href="/kq/register/single_register.do?b_single=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&userbase=<bean:write name="element" property="nbase" filter="true"/>&start_date=${dailyRegisterForm.start_date}&end_date=${dailyRegisterForm.end_date}&A0100=<bean:write name="element" property="a0100" filter="true"/>">
                               <bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                             </td>
                          </logic:equal>  
                       </logic:equal>
                                            
                   </logic:equal>
                   <!--数字-->
                   <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap>
                       <%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                         HashMap infoMap=(HashMap)dailyRegisterForm.getKqItem_hash();
                         //out.println("d = "+abean.get(name));
                       %>
                         <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(name)+""%>'/>
                       <%
                                inNum++;
                        %> 
                      </td>
                  </logic:equal>
                  <logic:equal name="info" property="itemtype" value="D">
                  	<td align="center" class="RecordRow" nowrap>
                       &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/>&nbsp;
                       <html:hidden name="element" property="${info.itemid}"/> 
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
   <td>
    <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-120)+";width:100%;margin-top: 25px'  >");
	</script>
     <table  width="100%" align="left" class="RecordRowTop0">
       <tr>          
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td align="right" nowrap class="tdFontcolor">
	     <p align="right"><hrms:paginationdblink name="dailyRegisterForm" property="pagination" nameId="dailyRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	  <td></td>
	</tr>
     </table>
    <script language='javascript' >
	  document.write("</div>");
    </script>
   </td>
 </tr>
 <tr>
 <!--  
      <td width="20%" align="left"  nowrap>          
	      <input type="button" name="br_approve"
								value='<bean:message key="button.return"/>' class="mybutton"
								onclick="history.back();">
       </td>
  	-->
  	<td width="20%" align="left"  nowrap> 
  	<hrms:tipwizardbutton flag="workrest" target="il_body" formname="dailyRegisterForm"/> 
          <bean:write name="dailyRegisterForm" property="state_message" filter="true"/>&nbsp;
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
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>
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
 MusterInitData();
 hide_nbase_select('select_pre');
</script>