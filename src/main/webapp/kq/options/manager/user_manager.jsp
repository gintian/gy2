<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<script type="text/javascript" src="/kq/kq.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.kq.options.manager.UserManagerForm"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String userFullName=null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);	
	String fields="";
	String tables="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  
      fields=userView.getFieldpriv().toString();
	  tables=userView.getTablepriv().toString();
	  userName=userView.getUserName(); 
	  userFullName=userView.getUserFullName(); 
	}
	String superUser="0";
	if(userView.isSuper_admin())  
	  superUser="1";
	else
    {
       if(fields==null||fields.length()<=0)
	       fields=",";
	   if(tables==null||tables.length()<=0)
	       tables=","; 
	}
	UserManagerForm userManagerForm=(UserManagerForm)session.getAttribute("userManagerForm");
    session.setAttribute("selectWhere",userManagerForm.getSelectWhere());
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
%>
<%String aurl = (String)request.getServerName();
  String port=request.getServerPort()+"";  
 String url_p=SystemConfig.getCsClientServerURL(request); 
%>
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
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%
	int i=0;
	String name=null;
	
%>
<SCRIPT language=JavaScript>
var row=1;
function countChoices(obj,name,type,i,r) 
{
   alert("dd");
   var box1=name+"01"+i;
   var box2=name+"02"+i;
   var box3=name+"03"+i;
   alert(box1);
if(r==1)
{
  
  document.browseRegisterForm.box1.checked=true;
  document.browseRegisterForm.box2.checked=false;
  document.browseRegisterForm.box3.checked=false;
  
}
 if(r==2)
{
  
   document.browseRegisterForm.box2.checked=true;
   document.browseRegisterForm.box1.checked=false;
   document.browseRegisterForm.box3.checked=false;
}
 if(r==3)
{
   document.browseRegisterForm.box3.checked=true;
   document.browseRegisterForm.box1.checked=false;
   document.browseRegisterForm.box2.checked=false;
}

}
  function batch()
   {
       var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:350x;"; 
       
       target_url="/kq/options/manager/usermanagerdata.do?b_batch=link";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=250,left=350,width=306,height=206');
        var vo;
        if (checkIE().indexOf("IE|6") != -1) {
        	vo=window.showModalDialog(iframe_url,1, 
        "dialogWidth:306px; dialogHeight:200px;resizable:no;ce nter:yes;scroll:yes;status:no;scrollbars:yes");
        } else {
        vo=window.showModalDialog(iframe_url,1, 
        "dialogWidth:306px; dialogHeight:200px;resizable:no;ce nter:yes;scroll:yes;status:no;scrollbars:yes");
       //userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_semp=link";
       //userManagerForm.submit();
       }
       if(vo!=null&&vo!="")
       {
          userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_batchupdate=link&kq_code="+vo;
          userManagerForm.target="mil_body";
          userManagerForm.submit();
       }
   }
   
 function checkIE() {
	var userAgent = navigator.userAgent;
	var browserVersion = parseFloat(navigator.appVersion);
	var browserName = navigator.appName;
	if (userAgent.indexOf("Opera") != -1) {//Opera浏览器
		if (navigator.appName == "Opera") {
			return "Opera|" + navigator.appVersion;
		} else {
			var reg = new RegExp("Opera (\\d+\\.\\d+)");
			reg.test(userAgent);
			return "Opera|" + RegExp["$1"];
		}
	} else if (userAgent.indexOf("compatible") != -1 && userAgent.indexOf("MSIE") != -1) {
		var reg = new RegExp("MSIE (\\d+\\.\\d+)");
		reg.test(userAgent);
		return "MSIE|" + RegExp["$1"];
	} else if (userAgent.indexOf("KHTML") != -1 || userAgent.indexOf("Konqueror") != -1 
				|| userAgent.indexOf("AppleWebKit") != -1) {
		if (userAgent.indexOf("AppleWebKit") != -1) {
			var reg = new RegExp("AppleWebKit\\/(\\d+(\\.\\d*)?)");
			reg.test(userAgent);
			return "SAFARI|" + RegExp["$1"];
		}
		if (userAgent.indexOf("Konqueror") != -1) {
			var reg = new RegExp("Konqueror\\/(\\d+(\\.\\d+(\\.\\d+)?)?)");
			reg.test(userAgent);
			return "Konqueror|" + RegExp["$1"];
		}
	} else if (userAgent.indexOf("Gecko") != -1) {
		var reg = new RegExp("rv:(\\d+\\.\\d(\\.\\d+)?)");
		reg.test(userAgent);
		return "Mozilla|" + RegExp["$1"];
	} else if (userAgent.indexOf("Mozilla") == 0 && browserName == "Netscape" 
			&& browserVersion >= 4.0 && browserVersion < 5.0) {
		return "Netscape|" + browserVersion;
	} else {
		return "Other";
	}
}
  
	function shaixuan(){
       	var winFeatures = "dialogWidth:730px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
		var target_url = "/kq/query/searchfiled.do?b_init=link`table=userManager";
		if($URL)
            target_url = $URL.encode(target_url);
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,1,winFeatures);
        if(return_vo){
         	userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_search=link&select_flag=2&selectResult="+$URL.encode(return_vo);
     		userManagerForm.submit();
       }
	}  
	
   function kq_work_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       var kq_cardno="${userManagerForm.kq_cardno}";
       var kq_gno="${userManagerForm.kq_gno}"
       if(kq_cardno!="")
       {
          target_url="/kq/options/manager/sendcard.do?b_work=link`privtype=kq`kq_cardno="+kq_cardno+"`kq_gno="+kq_gno+"`target=rr`viewPost=kq";
          if($URL)
              target_url = $URL.encode(target_url);
          var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
           var return_vo= window.showModalDialog(iframe_url,1, 
           "dialogWidth:506px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
           if(return_vo&&return_vo.flag=="true")
           {
              change();
           }
       }else
       {
          alert("考勤卡号没有定义!");
       }
      
   }
   function kq_batch_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       var kq_cardno="${userManagerForm.kq_cardno}";
       var kq_gno="${userManagerForm.kq_gno}"
       if(kq_cardno!="")
       {
         target_url="/kq/options/manager/batchsendcard.do?b_send=link`privtype=kq`kq_cardno="+kq_cardno+"`kq_gno="+kq_gno+"`target=rr`viewPost=kq";
         if($URL)
             target_url = $URL.encode(target_url);
         var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
        // var win=newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=506,height=406');
         var return_vo= window.showModalDialog(iframe_url,1, 
         "dialogWidth:506px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
          if(return_vo&&return_vo.flag=="true")
          {
            change();
          }
        }else
       {
          alert("考勤卡号没有定义!");
       }       
   }
    function kq_change_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:300px;"; 
       var kq_cardno="${userManagerForm.kq_cardno}"; 
       if(kq_cardno!="")
       {
         target_url="/kq/options/manager/changecard.do?b_change=link`kq_cardno="+kq_cardno+"`target=rr";
         if($URL)
             target_url = $URL.encode(target_url);
         var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
         //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=506,height=386');
          var return_vo= window.showModalDialog(iframe_url,1, 
         "dialogWidth:506px; dialogHeight:332px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
         if(return_vo&&return_vo.flag=="true")
          {
            change();
          }
       }else
       {
         alert("考勤卡号没有定义!");
       }      
   }
   function kq_cancell_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;";              
       target_url="/kq/options/manager/cancellcard.do?b_search=link";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       var return_vo= window.showModalDialog(iframe_url,1, 
       "dialogWidth:406px; dialogHeight:306px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(return_vo&&return_vo.flag=="true"){
            change();
       }
      // newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=406,height=306');
   }
   function set_len_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;";              
       target_url="/kq/options/manager/cardlen.do?b_search=link";
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       var return_vo= window.showModalDialog(iframe_url,1, 
       "dialogWidth:406px; dialogHeight:180px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(return_vo&&return_vo.flag=="true"){
            change();
       }
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=406,height=306');
   }
   function eidt_card(nbase,a0100,cardno)
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       var kq_cardno="${userManagerForm.kq_cardno}"; 
       if(kq_cardno!="")
       {
         target_url="/kq/options/manager/eidtcard.do?b_search=link`nbase="+nbase+"`a0100="+a0100+"`cardno="+cardno+"`kq_cardno="+kq_cardno;
         if($URL)
             target_url = $URL.encode(target_url); 
         var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
         //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=200,left=320,width=406,height=306');
          var return_vo= window.showModalDialog(iframe_url,1, 
         "dialogWidth:406px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
         if(return_vo&&return_vo.flag=="true")
         {
            window.location.href="/kq/options/manager/user_manager.jsp";
         }
       }else
       {
         alert("考勤卡号没有定义!");
       }              
       
   }
   function changeType(obj,ty)
   {
      var type_value = obj.options[obj.selectedIndex].value; 
      var a0100=document.getElementById("ab"+ty).value;
      var nbase=document.getElementById("nb"+ty).value;
      var hashvo=new ParameterSet(); 
      hashvo.setValue("a0100",a0100);
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("type_value",type_value);
      var kq_cardno="${userManagerForm.kq_cardno}";
      var kq_type="${userManagerForm.kq_type}";
      hashvo.setValue("kq_cardno",kq_cardno);
      hashvo.setValue("kq_type",kq_type);
      hashvo.setValue("rowN",ty);
      hashvo.setValue("isCreate","no");
      var request=new Request({method:'post',onSuccess:showSelect,functionId:'15207000018'},hashvo);    
    
   }
   function showSelect(outparamters)
   {
      var cardno=outparamters.getValue("cardno");   
      var rowN=outparamters.getValue("rowN"); 
      var kq_cardno="${userManagerForm.kq_cardno}";
      if(kq_cardno!=null&&kq_cardno.length>0)
      {
        if(cardno!=null&&cardno.length>0)
        {
           var obj=document.getElementById("cn"+rowN);
           obj.innerHTML=cardno;
        }
        
      }
   }
     var checkflag = "false";

 function selAll()
  {
      var len=document.userManagerForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.userManagerForm.elements[i].type=="checkbox")
            {
              document.userManagerForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.userManagerForm.elements[i].type=="checkbox")
          {
            document.userManagerForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  } 
  
  function change()
  {
     userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_view=link&code=${userManagerForm.code}&kind=${userManagerForm.kind}&issearch=1";
     userManagerForm.submit();
  }
  function sort()
  {
     var thecodeurl ="/kq/options/manager/sorting.do?b_query=link"; 
     var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    	var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:580px; dialogHeight:340px;resizable:no;center:yes;scroll:yes;status:no"); 
    
     if(return_vo==""||return_vo=="undefined"||return_vo == null) {
       return false; 
     }  
     
     if($URL)
    	 return_vo = $URL.encode(return_vo);
     userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_search=link&sort="+return_vo;
     userManagerForm.submit();
  }
  function mag_card()
  {
    newwindow=window.open("/kq/options/manager/magcard.do?b_search=link&action=magcarddata.do&target=mil_body&flag=noself&menu=1&treetype=org&privtype=kq",'il_body');
  }
  function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;//tomcat路径
      var DBType="${userManagerForm.dbType}";//1：mssql，2：oracle，3：DB2
      var UserName="<%=userName%>";   //登陆用户名暂时用su     
      var obj = document.getElementById('CardPreview1');  
      if(obj==null)
      {
         return false;
      } 
      var superUser="<%=superUser%>";
      var menuPriv="<%=fields%>";
      var tablePriv="<%=tables%>";

      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("<%=userFullName%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
}
function printCard()
{
   var hashvo=new ParameterSet();
   var inforkind="1";   
   var pers=new Array();
   hashvo.setValue("inforkind","1"); 
   var a0100s="";
   var len=document.userManagerForm.elements.length;   
   var location=1;
   for (i=0;i<len;i++)
   {
     if (document.userManagerForm.elements[i].type=="checkbox"&&document.userManagerForm.elements[i].name!="aa")
     {
        if(document.userManagerForm.elements[i].checked==true)
        {
          var a0100=document.getElementById("ab"+location).value; 
          var base =document.getElementById("nb"+location).value;
          a0100s=base+"`"+a0100;
          pers[i]=a0100s;   
          location++;        
        }       
     }
   }  
   if(location==1)
   {
      alert("请选择人员！");
      return false;
   }
   hashvo.setValue("pers",pers); 
   var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100079'},hashvo);
}

function showPrint(outparamters)
{
   var personlist=outparamters.getValue("personlist");    
   var cardid="${userManagerForm.magcard_cardid}"; 
  if(cardid=="")
  {
     alert("没有定义工作证登记表!");
     return false;
  }
  var obj = document.getElementById('CardPreview1');
  if(obj==null)
  {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
  }
  obj.SetCardID("${userManagerForm.magcard_cardid}");
  obj.SetDataFlag("1");
  obj.SetNBASE("usr");
  obj.ClearObjs();  
  var isCorrect=false;
  if(personlist!=null)
  {
    for(var i=0;i<personlist.length;i++)
    {
       obj.AddObjId(personlist[i].dataValue);
    }
    try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
    obj.ShowCardModal();
  }
}
</SCRIPT>
<script type="text/javascript">
<!--
	function kq_empty_card(mark){
		var msg = "清空卡号前必须先将考勤机上的刷卡数据下载至本系统中,\n";
        msg=msg+"否则可能导致数据不能正常分析!\n\n";
        msg=msg+"你确定要收回考勤卡号么?";
    	if(confirm(msg)){
    		userManagerForm.action="/kq/options/manager/usermanagerdata.do?b_emptycard=link&empOrUn=" + mark;
    		userManagerForm.submit();
    	}
    } 
    
//-->
</script><hrms:themes /> <!-- 7.0css -->
 <% int s=0;%>
<html:form action="/kq/options/manager/usermanagerdata">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<html:hidden property="kq_code" name="userManagerForm" />
<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
   <td>
      <table>
      <tr>
	   <td align="left" width="300">
		 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <tr>
	      <td align="left">
            <hrms:menubar menu="menu2" id="menubar1" target="mil_body">  
              <hrms:menuitem name="rec" label="编辑" function_id="270801,270802,270803">
              <hrms:menuitem name="mitem1" label="批量更改考勤方式" icon="/images/quick_query.gif" url="javascript:batch();" function_id="270801"/>       
              <hrms:menuitem name="mitem2" label="查询" icon="/images/sort.gif" url="javascript:shaixuan();" command="" function_id="270802" />              
              <hrms:menuitem name="mitem3" label="人员排序" icon="/images/view.gif" url="javascript:sort();" command="" function_id="270803" />              
              </hrms:menuitem> 
            <logic:notEqual name="userManagerForm" property="magcard_flag" value="1">
              <hrms:menuitem name="rece" label="考勤卡" function_id="270810,270811,27081201,27081202,270813,270814,270815">
              <hrms:menuitem name="mitem2" label="手动发卡" icon="/images/sort.gif" url="javascript:kq_work_card();" command="" function_id="270810" />
              <hrms:menuitem name="mitem3" label="批量发卡" icon="/images/sort.gif" url="javascript:kq_batch_card();" command=""  function_id="270811"/>
              <hrms:menuitem name="mitem2" label="收回考勤卡" icon="" command="" function_id="270812">
              	<hrms:menuitem name="mitem2" label="按机构收回" icon="/images/add_del.gif" url="javascript:kq_empty_card('1');" command="" function_id="27081201" />
			  	<hrms:menuitem name="mitem2" label="按人员收回" icon="/images/add_del.gif" url="javascript:kq_empty_card('2');" command="" function_id="27081202" />
              </hrms:menuitem>
              <hrms:menuitem name="mitem3" label="换卡" icon="/images/add_del.gif" url="javascript:kq_change_card();" function_id="270813"/>  
              <hrms:menuitem name="mitem4" label="已作废卡" icon="/images/view.gif" url="javascript:kq_cancell_card();" function_id="270814"/>    
              <hrms:menuitem name="mitem3" label="设置卡号长度" icon="/images/write.gif" url="javascript:set_len_card();" function_id="270815"/>       
              </hrms:menuitem>
            </logic:notEqual>   
            <logic:equal name="userManagerForm" property="magcard_flag" value="1">  
              <hrms:menuitem name="rece" label="发卡" function_id="270821,270822">
              <hrms:menuitem name="mitem2" label="发卡登记" icon="/images/sort.gif" url="javascript:mag_card();" command="" function_id="270821" />
              <hrms:menuitem name="mitem3" label="打印工作证" icon="/images/write.gif" url="javascript:printCard();" command=""  function_id="270822"/>
              </hrms:menuitem> 
            </logic:equal>          
           </hrms:menubar>
          </td>
         </tr>
        </table>
      </td>
      <td nowrap align="left" valign="middle"> &nbsp;
         <html:select name="userManagerForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
          </html:select>        
      </td>
      <td nowrap align="left" valign="middle"> 
            &nbsp; 姓名&nbsp;
    </td>
      <td nowrap align="left" valign="middle"> 
         <html:text name="userManagerForm" styleClass="inputtext" property="select_name" style="width:100px;font-size:10pt;text-align:left"></html:text>
         <input type="button" name="br_return" value='查询' class="mybutton" onclick="change();">          
         
      </td>
     </tr>
    </table>
   </td>   
  </tr>
   <tr><td height="3px"></td></tr>
  <tr>
  <td>
  <div class="fixedDiv2">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr class="fixedHeaderTr">
            <td align="center" class="TableRow" style="border-top: none;border-left: none;border-right: none;" nowrap>
		     <input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;           
		    </td>  
            <logic:iterate id="element"   name="userManagerForm"  property="fieldlist" indexId="index">
             <logic:equal name="element" property="visible" value="true">
               <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	           </td> 
	          </logic:equal> 
            </logic:iterate> 
             <logic:notEqual name="userManagerForm" property="magcard_flag" value="1">
               <logic:notEqual name="userManagerForm" property="kq_cardno" value=""> 
               <hrms:priv func_id="27080">
                <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
                &nbsp;修改卡号&nbsp;
            	</td> 
            	</hrms:priv>
              </logic:notEqual>	    
            </logic:notEqual>
          </tr>
   	  </thead>
   	  <!--
   	  order by b0110,e0122,e01a1,A0000 ,top union 出错，暂时不加排序
   	  -->    	     	 		 	  	 	 
      <hrms:paginationdb id="element" name="userManagerForm" sql_str="userManagerForm.strsql" table="" where_str="" columns="userManagerForm.columns" order_by="userManagerForm.orderby" page_id="pagination" pagerows="20" distinct="" keys="" indexes="indexes" >
	    <%
          if(s%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}s++;         
          request.setAttribute("num",s+"");  
          request.setAttribute("nb","nb"+s); 
          request.setAttribute("tp","tp"+s); 
          request.setAttribute("ab","ab"+s); 
          request.setAttribute("cn","cn"+s);         
          %>   
	     <td align="center" class="RecordRow" style="border-top: none;border-right: none;border-left: none;" nowrap>   
                <hrms:checkmultibox name="userManagerForm" property="pagination.select" value="true" nameId="1" propertyId="1"  indexes="indexes"/>&nbsp;
                <html:hidden name="element" property="nbase" styleId='${nb}'/>
            	 <html:hidden name="element" property="a0100" styleId='${ab}'/>
             </td>	    
            <%
              int r=1;              
            %>            
            <logic:iterate id="fielditem"  name="userManagerForm"  property="fieldlist" indexId="index">
              <logic:equal name="fielditem" property="visible" value="true">
            <%
            	FieldItem item=(FieldItem)pageContext.getAttribute("fielditem");
            	name=item.getItemid();             	  
            	if(name.equals("t1"))
            	{
            	%>
            	<td align="center" class="RecordRow" style="border-top: none;border-right: none;" nowrap id='${cn}'>&nbsp;
            	<bean:write name="element" property="t1" filter="true"/>  
            	</td> 
            	<%
            	
            	}else if(name.equals("t2"))
            	{
            	%>
            	   <td align="center" class="RecordRow" style="border-top: none;border-right: none;" nowrap>&nbsp;
            	     <bean:write name="element" property="t2" filter="true"/>  
            	   </td> 
            	
            	<%
            	}else if(name.equals("t3"))
            	{ 
            	%>
            	 <td align="center" class="RecordRow" style="border-top: none;border-right: none;" nowrap>
            	<%
                     if (userView.hasTheFunction("27080")) {
            	%>&nbsp;
            	    <hrms:optioncollection name="userManagerForm" property="typelist" collection="list"/>
	                  <html:select name="userManagerForm" property='<%="pagination.curr_page_list["+i+"]."+name%>' size="1" styleId='${tp}' onchange="changeType(this,'${num}');">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select>  
               <% } else { %> 
                    <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>            
                      &nbsp;<bean:write name="codeitem" property="codename" />
               <% } %>
	             </td> 
            	<%
            	   r++;
            	}else
            	{
            	%>
            	   <td align="left" class="RecordRow" style="border-top: none;border-right: none;" nowrap>            	   
            	    <logic:notEqual name="fielditem" property="codesetid" value="0">
            	    	<logic:notEqual name="fielditem" property="itemid" value="e0122">             	   
            	       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
                      &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
                       </logic:notEqual>
                       <logic:equal name="fielditem" property="itemid" value="e0122">
                       	 <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${userManagerForm.uplevel}"/>  	      
                      		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
                       </logic:equal>
                    </logic:notEqual>
                    <logic:equal name="fielditem" property="codesetid" value="0">
                     &nbsp;<bean:write name="element" property="${fielditem.itemid}"/>&nbsp;
                    </logic:equal>
            	   </td> 
            	<%
            	}    
            %>            
              </logic:equal> 
            </logic:iterate> 
            <logic:notEqual name="userManagerForm" property="magcard_flag" value="1">  
              <logic:notEqual name="userManagerForm" property="kq_cardno" value=""> 
              <hrms:priv func_id="27080">
                <td align="center" class="RecordRow" style="border-top: none;border-right: none;" nowrap>&nbsp;
                 <bean:define id="nbase1" name="element" property="nbase"/>
		         <bean:define id="a01001" name="element" property="a0100"/>
		         <bean:define id="t11" name="element" property="t1"/>
		         <%
		         		//参数加密
		    		     String nbase = PubFunc.encrypt(nbase1.toString());
		    		     String a0100 = PubFunc.encrypt(a01001.toString());
		    		     String t1 = PubFunc.encrypt(t11.toString());
		         %>
                <a href="###" onclick="eidt_card('<%=nbase %>','<%=a0100 %>','<%=t1 %>');">
            	 <img src="/images/edit.gif" border="0" alt="修改卡号">  
            	 </a>            	 
            	</td> 
              </hrms:priv>
              </logic:notEqual>
            </logic:notEqual>            
	        <%i++;%>  
	       <SCRIPT language=JavaScript>
               
               row++;
            </SCRIPT>   	    		        	        	        
          </tr>
      </hrms:paginationdb>
   </table></div>
   </td>
   </tr>
   <tr>
     <td width="100%" align="center">
     <table  width="100%" class="RecordRowP" align="center">
		<tr>		
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="userManagerForm" property="pagination" nameId="userManagerForm" scope="page">
				</hrms:paginationdblink></p>
			</td>
		</tr>
      </table>
     </td>
   </tr>
  <!-- <tr>
     <td width="100%" align="center">
        <table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 		</hrms:submit>
            </td>
          </tr>          
         </table>
     </td>
   </tr>-->
   <tr>
  <td width="100%">
    <logic:equal value="dxt" name="userManagerForm" property="returnvalue">  
     <hrms:tipwizardbutton flag="workrest" target="2" formname="userManagerForm"/> 
    </logic:equal>
  </td>
  </tr> 
</table> 


</html:form>
<script language="javascript">
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
  hide_nbase_select('select_pre');
</script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<script language="javascript">
   initCard();
   document.oncontextmenu=function(e){return false;}   
</script>
