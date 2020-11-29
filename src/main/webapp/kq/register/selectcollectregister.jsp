<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,java.util.Map"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.HashMap" %>
<%
DailyRegisterForm daily=(DailyRegisterForm)session.getAttribute("dailyRegisterForm");
String lockedNumStr=daily.getLockedNum();
int lockedNum=Integer.parseInt(lockedNumStr);
UserView userView = (UserView)session.getAttribute(WebConstant.userView);
String moduleFlag = daily.getModuleFlag();
String returnvalue_ = "";
if("1".equalsIgnoreCase(moduleFlag)){
    returnvalue_ = "zizhu";
}
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
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
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css"> 
<hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/select_collectdata">
<script language="javascript">
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
   function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  }
   function change()
   {
      dailyRegisterForm.action="/kq/register/select_collectdata.do?b_query=link";
      dailyRegisterForm.submit();
   }  
    var newwindow=null;
    	function showOverrule(userbase,a0100,kq_duration)
    	{
    	   var target_url;
    	   
    	   
    	   var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
    	   target_url="/kq/register/daily_registerdata.do?b_overrlue=link&userbase="+userbase+"&a0100="+a0100+"&kq_duration="+$URL.encode(kq_duration);
    	   
    	    newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
	  
    	}
    function go_daily()
   {  var returnvalue = "<%=returnvalue_%>";
      dailyRegisterForm.action="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do&target=mil_body&flag=noself&viewPost=kq&returnvalue="+returnvalue;
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   } 
   function ambiquity()
   {
	   	// 35728 在切换浏览不定期页面时需要清空勾选
   		delChecked();
       	dailyRegisterForm.action="/kq/register/ambiquity/search_ambiquity.do?b_search=link&action=search_ambiquitydata.do&target=mil_body&flag=noself&kind=2&viewPost=kq";
       	dailyRegisterForm.target="il_body";
       	dailyRegisterForm.submit();
   }  
   function selectKq(){
	   	var winFeatures = "dialogWidth:715px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
		var target_url = "/kq/query/searchfiled.do?b_init=link`table=q03";
		if($URL)
            target_url = $URL.encode(target_url);
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,1,winFeatures);   
	    if(return_vo){   
		   dailyRegisterForm.action="/kq/register/select_collectdata.do?b_query=link&select_flag=2&selectResult="+$URL.encode(return_vo);
           dailyRegisterForm.submit();
		}
   }   
    function show_state(state)
   {
     //var state = s.options[s.selectedIndex].value;     
     if(state==0)
     {
       go_daily();
     }else if(state==1)
     {
        
     }else if(state==2)
     {
        ambiquity();//不定期
     }
  }
   function gocollect()
   {
      if(confirm("您确定要进行月汇总？"))
      {
         dailyRegisterForm.action="/kq/register/daily_registerdata.do?br_collect=link";
         dailyRegisterForm.target="il_body";
         dailyRegisterForm.submit();
      }
   }
   function refer()
   {
      if(!app_opinion())
        return false;
      var info = KQ_DAILYREGISTER_REFER;
      if(confirm(info)){
      var waitInfo=eval("wait");	   
	      waitInfo.style.display="block";
	      dailyRegisterForm.action="/kq/register/select_collectdata.do?b_refer=link";
	      dailyRegisterForm.submit();
      }
   } 
   function audit()
   {
      if(!app_opinion())
        return false;
      var info = KQ_DAILYREGISTER_AUDIT;
      if(confirm(info)){
	      var waitInfo=eval("wait");	   
	      waitInfo.style.display="block";
	      dailyRegisterForm.action="/kq/register/select_collectdata.do?b_audit=link";
	      dailyRegisterForm.submit();
      }
   }
    function approve_data()
   {
       var hashvo=new ParameterSet();
       hashvo.setValue("code","${dailyRegisterForm.code}");
       hashvo.setValue("kind","${dailyRegisterForm.kind}");
       hashvo.setValue("select_pre","${dailyRegisterForm.select_pre}");
       hashvo.setValue("action", "check");
       var request=new Request({method:'post',asynchronous:false,onSuccess:execApprove,functionId:'15301110019'},hashvo);
   }

   function execApprove(outparamters) {
       var checkResult = outparamters.getValue("check_result");

       if (checkResult && checkResult === "false") {
           alert("当前没有已报批状态数据，无法执行批准操作！");
           return;
       }

       if(!app_opinion())
           return false;
       var info = KQ_DAILYREGISTER_APPROVE;
       if(confirm(info)){
           var waitInfo=eval("wait");
           waitInfo.style.display="block";
           dailyRegisterForm.action="/kq/register/select_collectdata.do?b_approve=link";
           dailyRegisterForm.submit();
       }
   }

   function MusterInitData()
  {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
  }
   function kqreport()
   {
   		delChecked();
      dailyRegisterForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=2&userbase=${dailyRegisterForm.userbase}&code=${dailyRegisterForm.code}&coursedate=${dailyRegisterForm.kq_duration}&kind=${dailyRegisterForm.kind}&sortitem="+getEncodeStr("${dailyRegisterForm.sortitem}");
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
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
        startSeal();
        
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
           alert('请先将所有用户的考勤信息进行月汇总审批！');
           return false;
        }
         var isseal=false;
        if(q07=="have")
        {
           if(confirm("部门考勤信息是否需要进行日汇总！"))
           {
              return false;
           }else
           {
             isseal=true;
           }
           
        }
        if(q09=="have")
        {
           if(confirm("部门考勤信息是否需要进行月汇总！"))
           {
              return false;
           }else
           {
             isseal=true;
           }
        }
        if(isseal)
        {
          startSeal();
        }
     }
   }
   function startSeal()
   {
      if(confirm("是否进行归档操作？\n提示：当月末处理完成后，本期间将不能再作归档处理，除非解封期间！"))
        {
            getPigeonhole();
        }else
        {
            getSeal();
        }
   }
   function getSeal()
   {
       <hrms:priv func_id="2702027"> 
         if(confirm("是否进行封存考勤期间处理？\n提示：封存后，将进入下一个期间，本期间将不能再进行修改，除非解封期间！"))
         {
           dailyRegisterForm.action="/kq/register/select_collectdata.do?b_useal=link";
           dailyRegisterForm.submit();
          }
       </hrms:priv>   
   }
   function getPigeonhole()
   {
           var target_url;
           var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
           target_url="/kq/register/pigeonhole.do?b_search=link&re_flag=coll";
           newwindow=window.open(target_url,'_blank','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   }
   function read_Pigeonflag(flag)
   {
      if(flag=="true")
      {
         alert("归档成功！");  
         getSeal();      
      }else if(flag=="false")
      {
         alert("归档失败，请重试！");
      }
   }
   function showPigeFlag(outparamters)
   {
     var flag=outparamters.getValue("pigeonhole_flag");
     
      var request=new Request({method:'post',asynchronous:false,onSuccess:showPigeFlag,functionId:'15302110004'});
   } 
   function return_overrule()
   {
       var len=document.dailyRegisterForm.elements.length;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
           if (document.dailyRegisterForm.elements[i].type=="checkbox")
            {
              if( document.dailyRegisterForm.elements[i].checked==true && document.dailyRegisterForm.elements[i].name!='selbox')
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
       target_url="/kq/register/select_collectdata.do?br_saveover=link&sb=new";
       //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
        return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
        return false;	
       if(return_vo.save=="1")
       {
           var overrule=return_vo.text;
           var o_obj=document.getElementById('overrule');
           o_obj.value=overrule;
           dailyRegisterForm.action="/kq/register/select_collectdata.do?b_overrule=link";
           dailyRegisterForm.target="mil_body";
           dailyRegisterForm.submit();
       }
   }
   function kqdayilreport()
   {
   	var db=document.getElementById("pre").value;
      if(db=="")
      	db="all";
      delChecked();
      dailyRegisterForm.action="/kq/register/print_report.do?b_view=link&action=print_kqreport.do&target=mil_body&report_id=1&userbase=${dailyRegisterForm.userbase}&code=${dailyRegisterForm.code}&coursedate=${dailyRegisterForm.kq_duration}&kind=${dailyRegisterForm.kind}&self_flag=coll&dbtype="+db+"&sortitem="+getEncodeStr("${dailyRegisterForm.sortitem}");
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
   
   // 去掉勾选
	function delChecked() {
   		var input = document.getElementsByTagName("input");
   		for (i = 0; i < input.length; i++) {
   			if (input[i].type == "checkbox" && input[i].checked == true) {
   				input[i].checked = false;
   			}
   		}
   	}
   function go_must()
   {
      var returnURL = getEncodeStr("${dailyRegisterForm.returnURL}");
      var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${dailyRegisterForm.relatTableid}&closeWindow=1";
      urlstr+="&returnURL="+returnURL + "&kqpre=${dailyRegisterForm.select_pre}";
      var return_vo= window.showModalDialog(urlstr,"", 
        		"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");

   } 
   function indicator()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/options/adjustcode/adjustcode.do?b_hideview=link`table=q03`flag=1`isSave=no";
       if($URL)
           target_url = $URL.encode(target_url);
         var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
         return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:440px; dialogHeight:370px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(return_vo)
        change();
   }
   function viewAll()
   {
       dailyRegisterForm.action="/kq/register/select_collectdata.do?b_query=link&select_flag=0";
      dailyRegisterForm.submit();
   } 
   function selectflag()
   {
      dailyRegisterForm.action="/kq/register/select_collectdata.do?b_query=link&select_flag=1";
      dailyRegisterForm.submit();
   }
   function checkOperationApplyState(flag)
   {
     var hashvo=new ParameterSet();
     hashvo.setValue("code","${dailyRegisterForm.code}");
     hashvo.setValue("kind","${dailyRegisterForm.kind}");
     hashvo.setValue("flag",flag);
     hashvo.setValue("select_pre","${dailyRegisterForm.select_pre}");  
     var request=new Request({method:'post',asynchronous:false,onSuccess:showCheck,functionId:'15301110137'},hashvo);
   } 
   function showCheck(outparamters)
   {
       var mess=outparamters.getValue("mess");
       var flag=outparamters.getValue("flag");
       var msg = outparamters.getValue("msg");
       var status = outparamters.getValue("status");
       if("0" == status)//不控制
       {
    	   if(mess!="")
	       {
	         if(confirm(mess+"是否继续此操作？"))
	         {
	            if(flag=="audit")
	            {
	             audit();
	            }else if(flag=="refer")
	            {
	              refer();
	            }
	         }
	       }else
	       {
	          if(flag=="audit")
	          {
	             audit();
	          }else if(flag=="refer")
	          {
	            refer();
	          }
	       }
       }else//月汇总数据审核控制
       {
	       if(msg=='0')
	       {
	         alert("审核公式未定义！");
	         return;
	       }
	       if(msg == "no")
	       {
		       if(mess!="")
		       {
		         if(confirm(mess+"是否继续此操作？"))
		         {
		            if(flag=="audit")
		            {
		             audit();
		            }else if(flag=="refer")
		            {
		              refer();
		            }
		         }
		       }else
		       {
		          if(flag=="audit")
		          {
		             audit();
		          }else if(flag=="refer")
		          {
		            refer();
		          }
		       }
	       }else
	       {
	    	   var content = outparamters.getValue("content");
	    	   if("0" == content){//强制控制
		    	   var filename=outparamters.getValue("fileName");
		    	   window.location.target="mil_body";
		    	   window.location.href = "/servlet/vfsservlet?fileid=" + filename +"&fromjavafolder=true";
		    	   alert("员工月汇总数据未能通过审核，请查看审核报告！");
		       }else{//预警提示
		    	   if(flag=="audit")
		           {
			           if(msg == "no"){
			        	   audit();
					   }else{
				           if(confirm("有月汇总数据未能通过审核，是否继续报审？")){
				               audit();
					       }
					   }
		           }else if(flag=="refer")
		           {
		        	   if(msg == "no"){
			        	   audit();
					   }else{
			        	   if(confirm("有月汇总数据未能通过审核，是否继续报批？")){
			                   refer();
						   }
					   }
		           }
			   }
	       }
       }
   }
   function app_opinion()
   {
      var target_url="/kq/register/select_collectdata.do?br_appopin=link";
      var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(return_vo==null)
       return false;      
      if(return_vo.flag!="true")
        return false;
      var fObj=document.getElementById("overrule");
      if(fObj!=null)
      {
        fObj.value=return_vo.overrule;        
      }
       return true;
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
     columns = columns.replace("ctime,","").replace("dbid,","").replace("a0000,","").replace("modusername,","").replace("modtime,","");
     var forms= new Array();
     var hashvo=new HashMap(); //ParameterSet();
     while(i!=-1)
     {		  
	    i=columns.indexOf(",",r);		
	    if(i!=-1){
	      var str=columns.substring(r,i); 	
	      if(str=="state"||str=="overrule")
	      {
	          r=i+1;
	          continue;
	      }
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
        /**hashvo.setValue("forms",forms);	
        hashvo.setValue("columns",columns);
        hashvo.setValue("code",code);
        hashvo.setValue("kind","${dailyRegisterForm.kind}");  
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'15301110078'},hashvo);**/
        
         hashvo.put("forms",forms);
		hashvo.put("columns",columns);
		hashvo.put("code",code); 
		hashvo.put("kind","${dailyRegisterForm.kind}");
		Rpc({functionId:'15301110078',success:showSelect},hashvo);
     }else
     {
    	 var waitInfo=eval("wait");  
         waitInfo.style.display="none"; 
        alert("没有记录，无法执行此操作！");
        return false;
     }
   }
   function showSelect(outparamters)
  { 
     var value=outparamters.responseText;
     var map=Ext.decode(value);
	 var tes = map.type;
	 var waitInfo=eval("wait"); 
	 waitInfo.style.display="none";
     if(tes=="success"){
        alert("数据保存成功");
     }else if(tes=="nosave"){
       alert("非起草和驳回状态，数据不允许保存！");
     }else{
        alert("数据保存失败");
     }
  }
   function isArray(obj) 
   { 
      return (obj.constructor.toString().indexOf('Array')!= -1);
   }
   function kq_batch()
   {
   		var winFeatures = "dialogHeight:400px; dialogLeft:250px;";
   		var target_url = "/kq/register/select_collectdata.do?b_batch=link`table=q05";
   		if($URL)
            target_url = $URL.encode(target_url);
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
   		var return_vo= window.showModalDialog(iframe_url,1, 
        				"dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
   		if(return_vo)
		{
	    var obj = new Object();
	    obj.type=return_vo.type;
	    if(obj.type=="1")
	    {
	      dailyRegisterForm.action="/kq/register/select_collectdata.do?b_search=link";
	      dailyRegisterForm.submit();
	    }
		}
   		
   }
function changeys(dd)
{
	if(dd==2){
 		dailyRegisterForm.action="/kq/register/select_collectdata.do?b_search=link&selectys=2&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}";
    	dailyRegisterForm.submit();
 	}else if(dd==1){
 		dailyRegisterForm.action="/kq/register/select_collectdata.do?b_search=link&selectys=1";
    	dailyRegisterForm.submit();
 	}
} 

 function syncorder() {
		dailyRegisterForm.action="/kq/register/select_collectdata.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&syncorder=1";		
      	dailyRegisterForm.submit();
   }
  //导出摸板
function downLoadTemp()
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("colums",$F('columns'));
	hashvo.setValue("tablename","Q05");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'15301110100'},hashvo);
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid=" + outName +"&fromjavafolder=true";
}

//导入模板数据
function exportTempData()
{
	document.dailyRegisterForm.action="/kq/register/daily_register.do?br_import=init&tablename=Q05";
  	document.dailyRegisterForm.submit();
}
 var check_obj;
 function checkValue(obj,item,nbase,a0100,q03z0)
   {
      check_obj=obj;
      var values=obj.value;
      if(values=="")
      {
         //return false;
    	  values="0";
      }
           
      if (isNaN(values)) {
		alert("只能是数字");
        obj.value="";
              check_obj.focus(); 
              return false;
      	}
      var hashvo=new ParameterSet();
      hashvo.setValue("kq_value",values);
      hashvo.setValue("kq_item",item);
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("a0100",a0100);
      hashvo.setValue("q03z0",q03z0);
      hashvo.setValue("table","q05");
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'15301110077'},hashvo);
   }
   function showCheckFlag(outparamters)
   {
      var check_flag=outparamters.getValue("flag");
      var check_mess=outparamters.getValue("mess");
      if(check_flag=="false")
      {
         alert(check_mess);         
         check_obj.value = ""; 
         check_obj.focus();
      }
   }
   
   function sort(){
		var url = "/kq/register/daily_registerdata_sort.do?b_search=link&setid=q03&checkflag=0";	
		var return_obj = window.showModalDialog(url,"","dialogWidth:510px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no")
		
		if (return_obj) {
			dailyRegisterForm.action="/kq/register/select_collectdata.do?b_search=link&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&syncorder=2&sortitem="+getEncodeStr(return_obj);
      		dailyRegisterForm.submit();
      	}
		
		
   }

   function auditCollect(){
	   var hashvo = new ParameterSet();
		hashvo.setValue("code","${dailyRegisterForm.code}"); 
		hashvo.setValue("kind","${dailyRegisterForm.kind}");
	   var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'15301110099'},hashvo);	
   }
   function check_ok(outparameters)
   {
	   
     var msg=outparameters.getValue("msg");
     if(msg=='0')
     {
       alert("审核公式未定义!");
       return;
     }
     if(msg=='no')
     {
        alert("审核完毕！");
        return;
     }
     else{
    	 
        var filename=outparameters.getValue("fileName");
        if(filename==null)
        	return;
        window.location.target="mil_body";
		window.location.href = "/servlet/vfsservlet?fileid=" + filename +"&fromjavafolder=true";
     }
   }

   var blankhidden=0;
   function visibleblank(){
       var blank=document.getElementById("queryblank");
       if(blankhidden==0){
        blank.style.display='block';
        blankhidden=1;
        var querydesc=document.getElementById("querydesc");
        querydesc.innerHTML="&nbsp;&nbsp;[&nbsp;<a href=\"javascript:visibleblank();\" >查询隐藏&nbsp;</a>]";
       }else{
            blank.style.display='none';
            blankhidden=0;
            var querydesc=document.getElementById("querydesc");
            querydesc.innerHTML="&nbsp;&nbsp;[&nbsp;<a href=\"javascript:visibleblank();\" >查询显示&nbsp;</a>]";
       }
   }
</script>


<html:hidden name="dailyRegisterForm" property="columns"/>
<html:hidden name="dailyRegisterForm" property="selectResult"/>
<table width="100%"  border="0" cellspacing="0"  align="left" cellpadding="0">
 <tr>
  <td nowrap>
   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr>   
        <td nowrap>
  <table border="0" cellspacing="0"  align="left" cellpadding="0">
  <tr>
    <td nowrap>
   <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
      
     <hrms:menuitem name="text" label="文件" function_id="270201,0C310">
     <hrms:menuitem name="mitem1" label="button.download.template" icon="/images/export.gif" url="javascript:downLoadTemp();" function_id="2702017,0C3108"/> 
     <hrms:menuitem name="mitem2" label="import.tempData" icon="/images/import.gif" url="javascript:exportTempData();" function_id="2702018,0C3109"/> 
     <hrms:menuitem name="mitem3" label="考勤表" icon="/images/write.gif" url="javascript:kqdayilreport();" function_id="2702013,0C3102"/>  
     <hrms:menuitem name="mitem4" label="简单花名册" icon="/images/view.gif" url="javascript:kqreport();" function_id="2702019,0C3103"/>       
     <hrms:menuitem name="mitem5" label="高级花名册" icon="/images/add_del.gif" url="javascript:go_must();" function_id="2702014,0C3104"/>  
     </hrms:menuitem>
     <hrms:menuitem name="rec3" label="编辑" function_id="270204,0C315,2702015,2702012,0C3114">
     <hrms:menuitem name="mitem1" label="显示&隐藏指标" icon="/images/add_del.gif" url="javascript:indicator();" function_id="2702015,0C3106"/>
     <hrms:menuitem name="mitem2" label="查询" icon="/images/write.gif" url="javascript:selectKq();" function_id="2702012,0C3114"/>
     <%       	
     if(daily.getUp_dailyregister()!=null&&daily.getUp_dailyregister().equals("0")){%>   
     <hrms:menuitem name="mitem3" label="月汇总批量修改" icon="/images/write.gif" url="javascript:kq_batch();" function_id="2702041,0C3152"/>   
     <% }%>
     <hrms:menuitem name="mitem4" label="人员排序" icon="/images/sort.gif" url="javascript:sort();" function_id="2702043,0C3128"/>
     <hrms:menuitem name="mitem4" label="同步人员顺序" icon="/images/view.gif" url="javascript:syncorder();" function_id="0C3127,2702042"/>
     </hrms:menuitem>
     <hrms:menuitem name="rec" label="审批" function_id="270206,0C313,0C314">
     <hrms:menuitem name="mitem1" label="报审" icon="/images/sort.gif" url="javascript:checkOperationApplyState('audit');" function_id="2702061,0C3141"/>
     <hrms:menuitem name="mitem2" label="报批" icon="/images/add_del.gif" url="javascript:checkOperationApplyState('refer');" function_id="2702060,0C3130"/>        
     <hrms:menuitem name="mitem3" label="批准" icon="/images/write.gif" url="javascript:approve_data();" function_id="2702062,0C3132"/>  
     <hrms:menuitem name="mitem4" label="个别驳回" icon="/images/del.gif" url="javascript:return_overrule();" command="" function_id="2702063,0C3133"/>
     </hrms:menuitem>
     <hrms:menuitem name="aiw" label="业务处理" function_id="270202,0C312">
     <hrms:menuitem name="mitem1" label="月汇总" icon="/images/write.gif" url="javascript:gocollect();" function_id="2702022,0C3122"/>
     <hrms:menuitem name="mitem1" label="审核" icon="/images/write.gif" url="javascript:auditCollect();" />  
     </hrms:menuitem> 
     <hrms:menuitem name="rec2" label="浏览" function_id="270203,0C311">
     <%
     	
     	String flg = daily.getFlag();
     	//<hrms:menuitem name="mitem2" label="月汇总" icon="/images/sort.gif" url="javascript:show_state('1');" function_id="2702030,0C3110"/>
     	if(flg!=null&&flg.equals("1")){ 
     %>   
     <hrms:menuitem name="mitem1" label="日明细" icon="/images/add_del.gif" url="javascript:show_state('0');" function_id="2702032,0C3113"/>        
     <hrms:menuitem name="mitem2" label="不定期" icon="/images/write.gif" url="javascript:show_state('2');" function_id="2702031,0C3117"/>  
     <% }else{%>
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
        <td align= "left" nowrap>&nbsp;&nbsp;
	           <html:button styleClass="mybutton" property="bc_btn1" onclick="viewAll();"><bean:message key="workdiary.message.view.all.infor"/></html:button>
	        </td>
	   </logic:equal>
       <td align= "left" nowrap>
         &nbsp;审批状态&nbsp;
         </td>   
       <td align= "left" nowrap>
         <hrms:optioncollection name="dailyRegisterForm" property="showtypelist" collection="list" />
           <html:select name="dailyRegisterForm" property="showtype" size="1" onchange="change()">
           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
           </html:select>  
       </td> 
       <td align= "left" nowrap>
      &nbsp;项目过滤&nbsp;
      </td>   
       <td align= "left" nowrap>
           <html:select name="dailyRegisterForm" property="kqitem" size="1" onchange="change();">
                <html:optionsCollection property="kqitemlist" value="dataValue" label="dataName"/>	        
            </html:select>
       </td>
       <td align= "left" nowrap>
           &nbsp;  按&nbsp; 
           </td>   
       <td align= "left" nowrap>
       <html:select name="dailyRegisterForm" property="select_type" size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
           </html:select>        
          <input type="text" name="select_name" class="inputtext" style="width:100px;font-size:10pt;text-align:left">&nbsp;
           <button extra="button" onclick="javascript:selectflag();">查询</button> 
       </td>
       <td id="querydesc" nowrap>
               &nbsp;&nbsp;[&nbsp;<a href="javascript:visibleblank();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;
       </td>
       <td align= "left" nowrap>
        &nbsp;
       
        <!--<bean:message key="kq.register.daily.menu"/>
        <bean:write name="dailyRegisterForm" property="kq_duration"/>-->
        <html:hidden name="dailyRegisterForm" property="kq_duration"/>
        <html:hidden name="dailyRegisterForm" property="code" styleClass="text"/>
        <html:hidden name="dailyRegisterForm" property="userbase" styleClass="text"/>
        <html:hidden name="dailyRegisterForm" property="kind" styleClass="text"/>   
        <html:hidden name="dailyRegisterForm" property="returnURL" styleClass="text"/>
        <html:hidden name="dailyRegisterForm" property="overrule" styleId="overrule" styleClass="text"/>   
        <html:hidden name="dailyRegisterForm" property="msg" styleClass="text" value=""/>
      </td>
       
    </tr>
    </table>
  </td>
 </tr>
 <tr><td height="3px"></td></tr>
 <tr>
  <td>
 <%int i=0;
   int n=0;
   String name=null;
   int num_s=0;
   int s=0;
   String names=null;
   int num_ss=0;
   int lock=0;
   HashMap infoMap=(HashMap)daily.getKqItem_hash();

 %>

 <script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-104)+";width:99%'  >");
 </script> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      <thead>
      <tr class="trShallow1"  style="background-color:#fff;" id="queryblank" style="display: none;">
            <td width="100%"  class="RecordRow" colspan="${dailyRegisterForm.columnCount}"  nowrap valign="middle">
                    <table width="400px">
           <td align= "left" width="120px" nowrap>
            &nbsp; <html:select name="dailyRegisterForm" property="select_pre" size="1" styleId="pre" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>         
            </html:select>
            </td>  
	        <td align= "left" width="200px" nowrap> &nbsp;
			        时间显示方式&nbsp; 
			        <logic:notEqual name="dailyRegisterForm" property="selectys" value="2">
                    <select size="1"   name="selectysf"   onchange="changeys(this.value);">
                        <option   value="1">默认</option>   
                        <option   value="2">HH:mm</option> 
                    </select>
                </logic:notEqual>
                <logic:equal name="dailyRegisterForm" property="selectys" value="2">
                    <select size="1"   name="selectysf"   onchange="changeys(this.value);">
                        <option   value="2">HH:mm</option> 
                        <option   value="1">默认</option>   
                    </select>
                </logic:equal> 
	        </td>   
	      <td></td>
	      </table>
        </td> 
      </tr>
         <tr>
            <td align="center" class="TableRow" nowrap style="border-top:none">
		<!--<bean:message key="column.select"/>&nbsp;-->
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
            </td>             
            <logic:iterate id="element"    name="dailyRegisterForm"  property="fielditemlist"> 
               <logic:equal name="element" property="visible" value="true">
                  <logic:equal name="element" property="itemtype" value="A">
                    <%if(i<lockedNum) {%>
                        <td align="center" class="TableRow" nowrap style="border-top:none">
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}else{ %>
                        <td align="center" class="TableRow" nowrap style="border-top:none">
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}
                   i++;
                   %>
                 </logic:equal>
                 <logic:notEqual name="element" property="itemtype" value="A">
                   <td align="center" class="TableRow" nowrap style="border-top:none">
                  <hrms:textnewline text="${element.itemdesc}" len="5"></hrms:textnewline>
                   </td>
                 </logic:notEqual>
              </logic:equal>
           </logic:iterate>         	        
           </tr>
      </thead>    
      <%i=0; %>   
      <hrms:paginationdb id="element" name="dailyRegisterForm" sql_str="dailyRegisterForm.sqlstr" table="" where_str="dailyRegisterForm.strwhere" columns="dailyRegisterForm.columns" order_by="dailyRegisterForm.orderby" pagerows="${dailyRegisterForm.pagerows}" page_id="pagination">
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
           <% int  inNum=0;lock=0;%>  
          <bean:define id="nbase" name="element" property="nbase" scope="page"></bean:define>     
          <bean:define id="a0100" name="element" property="a0100" scope="page"></bean:define> 
          <bean:define id="q03z0" name="element" property="q03z0" scope="page"></bean:define>   
          <td align="center" class="RecordRow" nowrap>
              <hrms:checkmultibox name="dailyRegisterForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
          </td>
          <% int  inNums=0;%>          
            <logic:iterate id="info" name="dailyRegisterForm"  property="fielditemlist">
                 <%
               		FieldItem item=(FieldItem)pageContext.getAttribute("info");
               		names=item.getItemid(); 
              	 %>   
              	 <%
              	 	String nbase0 = PubFunc.encrypt(nbase.toString());
              	 	String a01000 = PubFunc.encrypt(a0100.toString());
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
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                             <html:hidden name="element" property="${info.itemid}"/>                          
                          </logic:equal>
                         </logic:notEqual>
                         <logic:equal name="info" property="codesetid" value="0">                          
                             <logic:notEqual name="info" property="itemid" value="a0101">
                              <logic:notEqual name="info" property="itemid" value="overrule">
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                               <html:hidden name="element" property="${info.itemid}"/> 
                             </logic:notEqual>
                             </logic:notEqual>
                             <logic:equal name="info" property="itemid" value="a0101">
                               <logic:notEqual name="info" property="itemid" value="overrule">
                                <a href="/kq/register/single_register.do?b_browse=link&rflag=05&code=${dailyRegisterForm.code}&kind=${dailyRegisterForm.kind}&userbase=<%=nbase0 %>&start_date=${dailyRegisterForm.start_date}&end_date=${dailyRegisterForm.end_date}&A0100=<%=a01000 %>&marker=0">
                                &nbsp;<bean:write name="element" property="${info.itemid}" filter="true"/></a>&nbsp;
                                 <html:hidden name="element" property="${info.itemid}"/>
                               </logic:notEqual>
                             </logic:equal>
                              <logic:equal name="info" property="itemid" value="overrule">
                               <logic:notEqual name="info" property="itemid" value="a0101">
                                &nbsp;
                                  <img src="/images/edit.gif" border="0" alt="点击查看审批意见" onclick="showOverrule('<%=nbase0 %>','<%=a01000 %>','${dailyRegisterForm.kq_duration}');">
                               </logic:notEqual>
                             </logic:equal>  
                         </logic:equal>
                        </td>
                      </logic:equal>
					  <!-- 日期 -->
					  <logic:equal name="info" property="itemtype" value="D">
	                  	<td align="right" class="RecordRow" nowrap>
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
                   <!--数字-->
                      <logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap>
                       <%
                         num_s++;
                         request.setAttribute("num_s",num_s+""); 
                       %>
                       <logic:equal name="info" property="itemid" value="q03z1">
                        &nbsp;<html:text name="element" property="${info.itemid}"   size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp;  
                       </logic:equal>
                       <logic:notEqual name="info" property="itemid" value="q03z1">
                       <logic:notEqual name="dailyRegisterForm" property="up_dailyregister" value="1"> <!--参数设置：允许修改，再判断有没有修改权限,0业务1自助  -->
                       <%if((userView.hasTheFunction("2702026") && "0".equals(moduleFlag)) || (userView.hasTheFunction("0C3101") && "1".equals(moduleFlag)) || userView.isSuper_admin()) {%>
                          <logic:equal name="element" property="q03z5" value="01">
                            <logic:equal name="dailyRegisterForm" property="selectys" value="1">
                             <logic:notEqual name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}"  size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)"  onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                             </logic:notEqual>
                             <logic:equal name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)"  onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                             </logic:equal>
                            </logic:equal>
                            <logic:notEqual name="dailyRegisterForm" property="selectys" value="1">
                              &nbsp;<%
                         		num_ss++;
                         		request.setAttribute("num_ss",num_ss+""); 
                      		  %>
                       		  <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(names)+""%>'/>
                       			<%
                                inNums++;
                        		%>  
                          	</logic:notEqual> 
                          </logic:equal> 
                          <logic:equal name="element" property="q03z5" value="07">
                           <logic:equal name="dailyRegisterForm" property="selectys" value="1">  
                            <logic:notEqual name="element" property="${info.itemid}" value="0">
                              &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                            </logic:notEqual>
                             <logic:equal name="element" property="${info.itemid}" value="0">
                               &nbsp;<html:text name="element" property="${info.itemid}" value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)"  onkeypress="event.returnValue=IsDigit();" onblur="checkValue(this,'${info.itemid}','${nbase}','${a0100}','${q03z0}');"/>&nbsp; 
                             </logic:equal>
                           </logic:equal>
                           <logic:notEqual name="dailyRegisterForm" property="selectys" value="1">
                             &nbsp; <%
                         		num_ss++;
                         		request.setAttribute("num_ss",num_ss+""); 
                      		  %>
                       		  <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(names)+""%>'/>
                       			<%
                                inNums++;
                        		%>  
                          	</logic:notEqual> 
                          </logic:equal> 
                          <logic:notEqual name="element" property="q03z5" value="01">
                            <logic:notEqual name="element" property="q03z5" value="07">
                             <logic:equal name="dailyRegisterForm" property="selectys" value="1">
                              <logic:notEqual name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                              </logic:notEqual>
                              <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                              </logic:equal>
                             </logic:equal>
                             <logic:notEqual name="dailyRegisterForm" property="selectys" value="1">
                             &nbsp; <%
                         		num_ss++;
                         		request.setAttribute("num_ss",num_ss+""); 
                      		  %>
                       		  <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(names)+""%>'/>
                       			<%
                                inNums++;
                        		%>  
                          	</logic:notEqual> 
                            </logic:notEqual> 
                          </logic:notEqual>
                       <% } else { %>
                          <logic:equal name="dailyRegisterForm" property="selectys" value="1">
                          <logic:notEqual name="element" property="${info.itemid}" value="0">  
                             &nbsp;<html:text name="element" property="${info.itemid}"   size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                          </logic:notEqual>
                          <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;"  styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                          </logic:equal>
                          </logic:equal>
                          <logic:notEqual name="dailyRegisterForm" property="selectys" value="1">
                              <%
                         		num_ss++;
                         		request.setAttribute("num_ss",num_ss+""); 
                      		  %>
                       		  <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(names)+""%>'/>
                       			<%
                                inNums++;
                        		%>  
                          	</logic:notEqual> 
                       <%  } %>
                       </logic:notEqual>
                       
                       <logic:equal name="dailyRegisterForm" property="up_dailyregister" value="1">
                         <logic:equal name="dailyRegisterForm" property="selectys" value="1">
                          <logic:notEqual name="element" property="${info.itemid}" value="0">  
                             &nbsp;<html:text name="element" property="${info.itemid}"  size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                          </logic:notEqual>
                          <logic:equal name="element" property="${info.itemid}" value="0">                                                                                                   
                               &nbsp;<html:text name="element" property="${info.itemid}"  value="" size="8" styleClass="text" style="text-align:right;" styleId='${num_s}' onkeydown="return inputType2(this,event)" onkeypress="event.returnValue=IsDigit();" readonly="true"/>&nbsp; 
                          </logic:equal>
                         </logic:equal>
                         <logic:notEqual name="dailyRegisterForm" property="selectys" value="1">
                              <%
                         		num_ss++;
                         		request.setAttribute("num_ss",num_ss+""); 
                      		  %>
                       		  <hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=abean.get(names)+""%>'/>
                       			<%
                                inNums++;
                        		%>  
                          	</logic:notEqual> 
                        </logic:equal>

                         <!--<logic:greaterThan name="element" property="${info.itemid}" value="0">
                            <bean:write name="element" property="${info.itemid}"/>
                         </logic:greaterThan> -->
                         </logic:notEqual>
                       <%
                         inNum++;
                       %> 
                      </td>
                  </logic:equal>
                 </logic:equal> 
               <!---->               
            </logic:iterate>   
          </tr>
           <%
            if(inNum!=0){
              n=inNum;
            }
            i++;  
            %>  
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
         <% if (daily.getPagination().getPages() == daily.getPagination().getCurrent()){ %>
          <bean:define id="map" name="dailyRegisterForm" property="collectMap"></bean:define>
         <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
        	 <td align="center" class="RecordRow" nowrap colspan="">
              	&nbsp;&nbsp;汇总&nbsp;&nbsp;
          	</td>
          	<logic:iterate id="info" name="dailyRegisterForm"  property="fielditemlist">
          		<logic:equal name="info" property="visible" value="true">
          		
          			<logic:equal name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap>
                      	<bean:define id="itemid" name="info" property="itemid"></bean:define>
                      	<%
	                      	Map m = (Map) map; 
                      	%>
                      	
                      	<logic:equal name="dailyRegisterForm" property="selectys" value="2">
	                      	<logic:equal name="info" property="itemid" value="q03z1">
	                      		&nbsp;<%=m.get(itemid.toString().toLowerCase()) %>
	                      	</logic:equal>
                      		<logic:notEqual name="info" property="itemid" value="q03z1">
		                      	&nbsp;<hrms:kqvaluechange kqItem_hash="<%=infoMap%>" itemid="${info.itemid}" value='<%=(String)m.get(itemid.toString().toLowerCase()) %>'/>
                      		</logic:notEqual>
                      	</logic:equal>
                      	<logic:equal name="dailyRegisterForm" property="selectys" value="1">
	                      	&nbsp;<%=m.get(itemid.toString().toLowerCase()) %>
                      	</logic:equal>
                      </td>
                    </logic:equal>
                    <logic:notEqual name="info" property="itemtype" value="N">
                      <td align="center" class="RecordRow" nowrap>
                      	&nbsp;
                      </td>
                    </logic:notEqual>
                  
          		</logic:equal>
          	</logic:iterate>
        </tr>
        <% }%>
                                    	    		        	        	        
      </table>
<script language='javascript' >
	document.write("</div>");
</script>
     </td>
   </tr> 
   <tr>
   <td height="30px">
   <script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-70)+";width:99%;z-index:10;'>");
	</script>
     <table  width="100%" class="RecordRowP" height="30px">
       <tr>          
       <td width="60%" valign="bottom" align="left" height="30" nowrap>
              <hrms:paginationtag name="dailyRegisterForm"
								pagerows="${dailyRegisterForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
	  </td>
	  <td  width="40%" valign="bottom" align="right" nowrap>
	     <hrms:paginationdblink name="dailyRegisterForm" property="pagination" nameId="dailyRegisterForm" scope="page">
             </hrms:paginationdblink>
	  </td>
	  <td></td>
	</tr>
     </table>
     <table  width="100%">
       <tr> 
          <td align="left"  nowrap>   
          &nbsp;<hrms:kqcourse/>  
          <!--     
	      <logic:equal name="dailyRegisterForm" property="selectys" value="1">       
            <hrms:priv func_id="2702026,0C3101"> 
	         <input type="button" name="b_save" value='<bean:message key="kq.emp.button.save"/>' onclick="getSelect('<bean:write name="dailyRegisterForm"  property="columns"/>','<bean:write name="dailyRegisterForm"  property="code"/>');" class="mybutton">  
	        </hrms:priv>&nbsp;&nbsp;
	       </logic:equal>
	        --> 
	      &nbsp;&nbsp;<bean:write name="dailyRegisterForm" property="state_message" filter="true"/>&nbsp;
       </td>     
       </tr>
     </table>
     <script language='javascript' >
	  document.write("</div>");
    </script> 
   
   </td>
 </tr>
</table>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在接收数据请稍候....</td>
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
 MusterInitData();	
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
   hide_nbase_select('select_pre');
   function showmsg(){
 	<logic:notEmpty name="dailyRegisterForm" property="msg">
 		var msg = "<bean:write name="dailyRegisterForm" property="msg"/>"
 		alert(msg);
 	</logic:notEmpty> 
   }
   window.setTimeout("showmsg()",100);
   document.oncontextmenu = function() {return false;}
</script>