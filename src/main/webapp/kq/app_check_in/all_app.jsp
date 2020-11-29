<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
<script language="javascript" src="/js/validateDate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css">  
<hrms:themes /> <!-- 7.0css -->
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.kq.app_check_in.AppForm" %>
<%
  boolean isSuper=false;
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  if(userView.isSuper_admin())
     isSuper=true;
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
	//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
	document.body.oncontextmenu=function(){return false;};
//document.oncontextmenu=stop 

</script>
<%
	int i=0;
	int r=0;
%>
<script language="JavaScript">
function update()
   {
      
       alert("该信息已批复执行不可以修改！");
     
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
 </script>
<script language="javascript">
   function changes(str)
   {
      if(str=="1")
      {
        //Element.allselect('kqitem');
      }

      if(str=="2")
      {
        //Element.allselect('spflag');      	
      }     
      var falg=true;
      if(!validate(eval("document.appForm.start_date"),"起始日期"))
      {
         return false;
      }
      if(!validate(eval("document.appForm.end_date"),"结束日期"))
      {
         return false;
      }
      //szk验证时间 
      var dd = eval("document.appForm.start_date");
   	  var ks = dd.value;
   	  var jsd=eval("document.appForm.end_date");
   	  var js = jsd.value;
   	  ks=replaceAll(ks,"-",".");
   	  js=replaceAll(js,"-",".");
   	  if(ks>js)
   	  {
   	  	alert(KQ_CHECK_TIME_HINT);
   	  	return false;
   	  }
      appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&wo="+str+"&select_flag=1&dotflag=1&jump=link";
      appForm.submit();
   }
   function clearselect()
   {
      appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&select_flag=0&full=1";
      appForm.submit();
   }
   function adds(ss)
  {
     var len=document.appForm.elements.length;
     var uu;
      for (i=0;i<len;i++)
        {
           if (document.appForm.elements[i].type=="checkbox")
            {
              if(document.appForm.elements[i].checked==true)
              {
                uu="dd";
               
               }
            }
         }
        if(uu=="dd")
       {
           appForm.action="/kq/app_check_in/all_app_data.do?b_search=link";
           appForm.submit();
    	   target_url="/kq/app_check_in/choose_or.do?b_select=link&audit_flag="+ss;
    	    newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=180,left=350,width=380,height=270'); 
        }else
	{
	     alert("没有选择记录！");
             return false;
	}
  }
  function appregister()
  {
       var target_url;      
       target_url="/kq/app_check_in/app_register.do?b_query=link`table=${appForm.table}";
       if($URL)
	      	target_url = $URL.encode(target_url);
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       var return_vo= window.showModalDialog(iframe_url,"app", 
       "dialogWidth:560px; dialogHeight:635px;resizable:no;center:yes;scroll:yes;status:no");
       if(!return_vo)
	    return false;
       if(return_vo.flag=="true")
       {
          appForm.action="/kq/app_check_in/all_app_data.do?b_search=link";
          appForm.submit();
       }
  } 
 var checkflag = "false";

 function selAll()
  {
    appForm.action="/kq/app_check_in/all_app_data.do?b_allselect=link";
    appForm.submit();    
  } 

function change_print(){
	var returnURL = getEncodeStr("${appForm.returnURL}");
	var urlstr = "/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=81&relatTableid=${appForm.relatTableid}&closeWindow=1";
 	urlstr+="&returnURL="+returnURL;
    window.showModalDialog(urlstr,1, 
        "dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
}

   function manu()
   {
      parent.menuc.toggleCollapse(false);
      appForm.action="/kq/app_check_in/manuselect.do?b_query=link";
      //appForm.action="/kq/app_check_in/all_app_data.do?b_manu=link";
      //appForm.target="il_body";
      appForm.submit();
   }
   
   
   /**
   * 
   *	高级查询
   *
   */
	function selectKq(){
		var winFeatures = "dialogWidth:735px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
		var target_url = "/kq/query/searchfiled.do?b_init=link`table=${appForm.table}";
		if($URL)
            target_url = $URL.encode(target_url);
		var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,1,winFeatures);
		if(return_vo){
			appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&select_flag=2&selectResult="+$URL.encode(return_vo);
			appForm.submit();
		}
	}
	  
   function selectflag()
   {
      appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&select_flag=1";
      appForm.submit();
   }
   function viewAll()
   {
      appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&select_flag=0";
      appForm.submit();
   }
   function changeDb()
   {
      appForm.action="/kq/app_check_in/all_app_data.do?b_search=link";
      appForm.submit();
   }   
   function noselect()
   {
     var len=document.appForm.elements.length;
     var i;
     var isCorrect=false;
     var del = 0;
     for (i=0;i<len;i++)
     {
          if(document.appForm.elements[i].type=="checkbox")
          {
              if(document.appForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      } 
      <logic:notEqual  name="appForm" property="approved_delete" value="1">
      	for (j=0; j<len; j++) {
			if(document.appForm.elements[j].type=="checkbox") {
				if(document.appForm.elements[j].checked==true) {
              	var checkName = document.appForm.elements[j].name;
              	var obj = document.getElementsByName("hidden"+checkName+"_input");
              	if (obj[0] && obj[0].value=="03") {
              		del = 1;
					break;
              	}
               
              }
          }
      	}
      	if (del == 1) {
      		alert("已批数据不能删除!");
      		return ;
      	}
      </logic:notEqual>     
      if(isCorrect)
      {
        var mess="是否要删除所选记录？含有销假的记录将无法删除！";
        //var ta="${appForm.table}";
        //if(ta=="Q15")
           //mess=mess+"含有销假的记录将无法删除！";
      	if(confirm(mess)){
	         appForm.action="/kq/app_check_in/all_app_data.do?b_delete=link"; 
		     appForm.submit();
	     }else
	     {
	        return false;
	     }
      }else
      {
        alert("请先选择人员！");
        return false;
      }  
   }
   function abateselect()
   {
           
      if(haveSelected())
      {
        var mess="是否要废除所选记录？废除记录将无法恢复！";
        var ta="${appForm.table}";
      	if(confirm(mess)){
	         appForm.action="/kq/app_check_in/all_app_data.do?b_abate=link"; 
		     appForm.submit();
	     }else
	     {
	        return false;
	     }
      }else
      {
        alert("请先选择人员！");
        return false;
      } 
   }
   function haveSelected()
   {
       var len=document.appForm.elements.length;
       var i;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
          if(document.appForm.elements[i].type=="checkbox")
          {
              if(document.appForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      }  
      return isCorrect;
   }
   function batchsubscribe()
   {
  
   	  var len=document.appForm.elements.length;
 		var opinionlength=document.getElementById("opinionlength").value;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.appForm.elements[i].type=="checkbox")
            {
              if(document.appForm.elements[i].checked==true)
              {
                uu="dd";
               
               }
            }
        }
       if(uu=="dd")
       {
          if(confirm("是否进行批量审批？"))
         {
       	    var target_url="/kq/app_check_in/all_app_data.do?br_batchsub=link&table=${appForm.table}&opinionlength="+opinionlength;
            var return_vo= window.showModalDialog(target_url,"app", 
           "dialogWidth:300px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no");
           if(!return_vo)
	        return false;
	       var sp_result=return_vo.text;
	       if(sp_result==""||sp_result=="undefined")
	        sp_result="";
	       var obj=document.getElementById("sp_result");      
           obj.value=sp_result+"";   
	       appForm.action="/kq/app_check_in/all_app_data.do?b_batchsub=link&radio="+return_vo.radio+"&flag="+return_vo.falg+"&table=${appForm.table}";
           appForm.submit();
          }
       }else
       {
         alert("请先选择人员！");
         return false;
       }
   
   }
   //批量签批批准
   function passvalide()
   {
   if(confirm("是否进行批量签批？"))
   {
   		var len=document.appForm.elements.length;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.appForm.elements[i].type=="checkbox")
            {
              if(document.appForm.elements[i].checked==true)
              {
                uu="dd";
               
               }
            }
        }
       if(uu=="dd")
       {
       		//alert("类型 = "+bflag);
       		var target_url="/kq/app_check_in/all_app_data.do?b_groupsub=link&table=${appForm.table}";
       		var return_vo= window.showModalDialog(target_url,"app", 
          	"dialogWidth:300px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no");
          	if(!return_vo)
	        return false;
	      var sp_result=return_vo.text;
	      if(sp_result==""||sp_result=="undefined")
	        sp_result="";
	      var obj=document.getElementById("sp_result");      
          obj.value=sp_result+"";   
	      appForm.action="/kq/app_check_in/all_app_data.do?b_batchsub=link&radio="+return_vo.radio+"&flag="+return_vo.falg+"&table=${appForm.table}&dotflag=1";
          appForm.submit();
       }else
       {
         alert("请先选择人员！");
         return false;
       }
   }
   }
   //批量签批审核
   function lookvalide()
   {
   if(confirm("是否进行批量审核？"))
   {
   		var len=document.appForm.elements.length;
       var uu;
       for (i=0;i<len;i++)
       {
           if (document.appForm.elements[i].type=="checkbox")
            {
              if(document.appForm.elements[i].checked==true)
              {
                uu="dd";
               
               }
            }
        }
       if(uu=="dd")
       {
       		//alert("类型 = "+bflag);
       		var target_url="/kq/app_check_in/all_app_data.do?b_auditing=link&table=${appForm.table}&dotflag=1";
       		var return_vo= window.showModalDialog(target_url,"app", 
          	"dialogWidth:300px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no");
          	if(!return_vo)
	        return false;
	      var sp_result=return_vo.text;
	      if(sp_result==""||sp_result=="undefined")
	        sp_result="";
	      var obj=document.getElementById("sp_result");      
          obj.value=sp_result+"";   
	      appForm.action="/kq/app_check_in/all_app_data.do?b_batchsub=link&radio="+return_vo.radio+"&flag="+return_vo.falg+"&table=${appForm.table}";
          appForm.submit();
       }else
       {
         alert("请先选择人员！");
         return false;
       }
   }
   }
   //增加全部选项
function changelocations(obj)
{
	var obj = obj.value;
	var str = "3";
	if(obj==0||obj==1)
	{
		value1.style.display="block";
		return;
	}else
	{
		value1.style.display="none";
		appForm.action="/kq/app_check_in/all_app_data.do?b_search=link&wo="+str+"&select_flag=1&dotflag=1";
       	appForm.submit();
	}
}
function change(obj)
{
	var obj = obj;
	if(obj==0||obj==1)
	{
		value1.style.display="block";
		return;
	}else
	{
		value1.style.display="none";
		return;
	}
}
function initvalue()
{
	var ss = document.getElementById("va").value
	change(ss);
}
function openappstatistics(nbase,a0100,b0110)
{
  
   var target_url="/kq/app_check_in/all_app_data.do?b_statistics=link&a0100="+a0100+"&nbase="+nbase+"&b0110="+b0110;
        return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
}

function export_excel(flag){
	var target_url="/kq/app_check_in/all_app_data.do?b_excel=link&flag=" + flag;
    var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	if(return_vo != null){
		var hashvo=new ParameterSet();
		var fieldArray = return_vo.split(",");
		hashvo.setValue("fileList",fieldArray);
		hashvo.setValue("table","${appForm.table}")
		hashvo.setValue("flag", flag);
		var request = new Request({method:'post',onSuccess:showExportInfo,functionId:'15301110212'},hashvo);
	}
}

function showExportInfo(outparamters){
	if(outparamters){
		var name=outparamters.getValue("name");
		var mess=outparamters.getValue("mess");
		if(mess != ""){
			alert(mess);
			export_excel(1);
		}else{
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true";
		}
	}
}


function import_excel(){
	appForm.action="/kq/app_check_in/all_app_data.do?br_selectfile=link"; 
    appForm.submit();
}
//汇总审批 
function sumapproval(){
	 parent.menuc.toggleCollapse(false);
     appForm.action="/kq/app_check_in/sumapproval.do?b_query=link";
     appForm.submit();
}
</script>
<html:form action="/kq/app_check_in/all_app_data" styleId="form1">
	<logic:equal name="appForm" property="sign" value="8"> 
	    <font color=#ff000><bean:write name="appForm" property="message"/></font>
	</logic:equal>
	<logic:notEqual name="appForm" property="sign" value="8"> 
	</logic:notEqual>
	<html:hidden property="opinionlength" styleId="opinionlength"/>
	<html:hidden name="appForm" property="returnURL" styleClass="text"/>
	<html:hidden name="appForm" property="sp_result" styleId="sp_result" styleClass="text"/>
	<html:hidden name="appForm" property="bflag" styleId="bflag" styleClass="text"/>
  <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
  <thead>
     <tr height="25"> 
       <td>
	       <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" > 
		       <tr>
		        <td align="left"   nowrap colspan="${appForm.cols}">           
		         &nbsp;&nbsp;<hrms:kqcourse/>
		        </td>
		       </tr>
	       </table>
       </td> 
     </tr>

     <tr height="25">
       <td align="left"  nowrap colspan="${appForm.cols}">
     	   <table>
     	 	  <tr  style="white-space: nowrap">
		     	 	<td style="white-space: nowrap" >
		          <html:select name="appForm" property="select_pre" styleId="select_pre" size="1" onchange="changeDb();">
		            <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
		          </html:select>
		          </td>
		          <td style="white-space: nowrap" >
		          <logic:equal name="appForm" property="table" value="Q15">
		        	  按假别
		          </logic:equal>
		          
		          <logic:equal name="appForm" property="table" value="Q11">
		        	  按加班类型
		          </logic:equal>
		          
		          <logic:equal name="appForm" property="table" value="Q13">
		        	  按公出类型
		          </logic:equal>
		          </td>
		          <td style="white-space: nowrap" >
			        <html:select name="appForm" property="showtype" size="1" onchange="changes('1');">
						    <html:optionsCollection property="showtypelist" value="dataValue" label="dataName"/>                
		          </html:select>&nbsp; 
		           </td>
		          <td style="white-space: nowrap" >
			       <bean:message key="label.by.spflag"/>  
			        </td>
		          <td style="white-space: nowrap" >
			        <html:select name="appForm" property="sp_flag" size="1" onchange="changes('2');">
		            <html:optionsCollection property="splist" value="dataValue" label="dataName"/>	        
		          </html:select>
		           </td>
		          <td style="white-space: nowrap" >
		          &nbsp; 按
		           </td>
		          <td style="white-space: nowrap" >
		          <html:select name="appForm" property="select_type"  size="1">
		            <html:option value="0">姓名</html:option>                      
		            <html:option value="1">工号</html:option>
		            <html:option value="2">考勤卡号</html:option>
		          </html:select>
		           </td>
		          <td style="white-space: nowrap" >
		          <logic:empty name="appForm" property="select_name">
			          <input type="text" class="inputtext" name="select_name" style="width:100px;font-size:10pt;text-align:left">			
		          </logic:empty>
		          <logic:notEmpty name="appForm" property="select_name">
		          	  <input type="text"  class="inputtext"  name="select_name" style="width:100px;font-size:10pt;text-align:left" value='<bean:write name="appForm" property="select_name"/>'>			
		          </logic:notEmpty>
		           </td>
		          <td style="white-space: nowrap" >
		          <bean:message key="label.by.time.domain"/>
		           </td>
		          <td style="white-space: nowrap" >
		          <html:select name="appForm" styleId="va" property="select_time_type"  size="1" onchange="changelocations(this);">
		      		  <html:option value="0">按起止时间</html:option>                      
		            <html:option value="1">按申请日期</html:option>
		            <html:option value="2">全部显示</html:option>
		           </html:select>
		         </td>
	           <td nowrap>
	             <div id="value1" style="display:none;">
					 	     <bean:message key="label.from"/>
			   	  	 	 <input type="text"  class="inputtext"  name="start_date" value="${appForm.start_date}" style="width:100px;font-size:10pt;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
			   	  	 	 <bean:message key="label.to"/>
			   	  	 	 <input type="text"  class="inputtext"  name="end_date"  value="${appForm.end_date}" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>&nbsp;
			   	  	 	 <input type="button" name="br_return" value='查询' class="mybutton" onclick="changes('3');">          
			      
			             <input type="button" name="br_return" value='显示全部' class="mybutton" onclick="clearselect();">          
			     
			           <logic:equal name="appForm" property="table" value="Q15">
			       	     <hrms:priv func_id="270117,0C3427" module_id="">
				             <input type="button" name="br_return" value='条件查询' class="mybutton" onclick="selectKq();">
				           </hrms:priv>
			           </logic:equal>
			           <logic:equal name="appForm" property="table" value="Q11">
					         <hrms:priv func_id="270107,0C3417" module_id="">
				             <input type="button" name="br_return" value='条件查询' class="mybutton" onclick="selectKq();">
				           </hrms:priv>
			           </logic:equal>
			           <logic:equal name="appForm" property="table" value="Q13">
				           <hrms:priv func_id="270127,0C3437" module_id="">  
				             <input type="button" name="br_return" value='条件查询' class="mybutton" onclick="selectKq();">
				           </hrms:priv>
			           </logic:equal>
	             </div>
	           </td>
           </tr>
         </table>
       </td>
     </tr>
     
     <tr>
      <td style="border: 0;" style="white-space: nowrap">
		    <script language='javascript' >
			    document.write("<div id=\"tbl-container\"  style='left:5;height:"+(document.body.clientHeight-160)+";width:99%'  >");
		    </script> 
        <table width="100%" align="center" cellpadding="0" cellspacing="0" class="ListTableF" style="border-left: none">
          <tr>
            <td align="center" class="TableRow"  style="white-space: nowrap;border-left:none">
		          <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
            </td>
            <logic:equal name="appForm" property="table" value="Q15">
              <hrms:priv func_id="270114,0C3424"> 
	       	      <td align="center" class="TableRow" style="white-space: nowrap">
		              <bean:message key="label.view"/>      	
	              </td>
              </hrms:priv>
            </logic:equal>
						<logic:equal name="appForm" property="table" value="Q11">
							<hrms:priv func_id="270104,0C3414"> 
								<td align="center" class="TableRow" style="white-space: nowrap">
								  <bean:message key="label.view"/>      	
								</td>
							</hrms:priv>
						</logic:equal>
						<logic:equal name="appForm" property="table" value="Q13">
							<hrms:priv func_id="270124,0C3434"> 
								<td align="center" class="TableRow" style="white-space: nowrap">
								  <bean:message key="label.view"/>      	
								</td>
							</hrms:priv>
						</logic:equal>
						<td align="center" class="TableRow" style="white-space: nowrap">
						   <bean:message key="jx.param.objectdegree2"/>            	
						</td>
						<logic:equal name="appForm" property="table" value="Q15">
							<hrms:priv func_id="270118,0C3428"> 
								<td align="center" class="TableRow" style="white-space: nowrap">销假</td>
								<td align="center" class="TableRow" style="white-space: nowrap">销假标识	</td>
							</hrms:priv>
							<hrms:priv func_id="27011f,0C342f">
							  <td align="center" class="TableRow" style="white-space: nowrap">代销假</td>
							</hrms:priv>
						</logic:equal>
						
						<logic:equal name="appForm" property="table" value="Q11">
							<hrms:priv func_id="270109,0C3419"> 
								<td align="center" class="TableRow" style="white-space: nowrap">撤销加班</td>
								<td align="center" class="TableRow" style="white-space: nowrap">撤销加班标识	</td>
							</hrms:priv>
							<hrms:priv func_id="27010f,0C341f">
							  <td align="center" class="TableRow" style="white-space: nowrap">代撤销加班</td>
							</hrms:priv>
						</logic:equal>
						
						<logic:equal name="appForm" property="table" value="Q13">
							<hrms:priv func_id="270129,0C3439"> 
								<td align="center" class="TableRow" style="white-space: nowrap">撤销公出</td>
								<td align="center" class="TableRow" style="white-space: nowrap">撤销公出标识	</td>
							</hrms:priv>
							<hrms:priv func_id="27012f,0C343f">
							  <td align="center" class="TableRow" style="white-space: nowrap">代撤销公出</td>
							</hrms:priv>
						</logic:equal>
						
			      <logic:iterate id="element" name="appForm"  property="searchfieldlist" indexId="index">
			         <logic:equal name="element" property="visible" value="true">
			            <td align="center" class="TableRow" style="white-space: nowrap">
			                <bean:write name="element" property="itemdesc" filter="true"/>
			            </td>
			         </logic:equal>    
			      </logic:iterate>           
          </tr>
          <hrms:paginationdb id="element" pagerows="${appForm.pagerows}" name="appForm" sql_str="appForm.sql_str" table=""  where_str="appForm.cond_str" order_by="${appForm.orderby}" columns="${appForm.columns}" page_id="pagination"  indexes="indexes">
	          <%
		          AppForm appForm = (AppForm)request.getSession().getAttribute("appForm");
		          String table = appForm.getTable();
		          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		          String id;
		          String classidtoname;
		          Object obj = abean.get(table.toLowerCase()+"04");
		          obj=obj!=null?obj:"";
		          classidtoname = String.valueOf(obj);
		          String z5=(String)abean.get(table.toLowerCase()+"z5");
		          String re=(String)abean.get(table.toLowerCase()+"07"); //事由 
		          String ree = re;
		          if(re.length() > 10)
		        	  ree= re.substring(0,10)+"...";
		          // 开始时间
		          String z1 = (String)abean.get(table.toLowerCase()+"z1");
		          String z3 = (String)abean.get(table.toLowerCase()+"z3");
		          if(i%2==0)
		          {
	          %>
	          <tr class="trShallow">  
	          <%}
	          else
	          {%>
	          <tr class="trDeep">
	          <%
	          }
	          i++;  
	          %>  
            <td align="center" class="RecordRow" style="white-space: nowrap;border-left:none">
		          <hrms:kqdurationjudge startDate="<%=z1 %>">
		            <logic:equal name="appForm" property="approved_delete" value="1">
		              <logic:equal name="element" property="state" value="1">
		                <hrms:checkmultibox name="appForm" property="pagination.select"  value="false"  indexes="indexes"/>
		              </logic:equal>           
		              <logic:notEqual name="element" property="state" value="1">
		                <hrms:checkmultibox name="appForm" property="pagination.select"  value="true" indexes="indexes"/>
		              </logic:notEqual>
		            </logic:equal>
		            <logic:notEqual  name="appForm" property="approved_delete" value="1">
		              <%if(!(z5!=null&&z5.equalsIgnoreCase("03"))){ %>
		               <logic:equal name="element" property="state" value="1">
		                 <hrms:checkmultibox name="appForm" property="pagination.select"  value="false"  indexes="indexes"/>
		               </logic:equal>           
		               <logic:notEqual name="element" property="state" value="1">
		                 <hrms:checkmultibox name="appForm" property="pagination.select"  value="true" indexes="indexes"/>
		               </logic:notEqual>
		              <%} else { %>
		               <logic:equal name="element" property="state" value="1">
		                 <hrms:checkmultibox name="appForm" property="pagination.select"  value="false"  indexes="indexes"/>
		                 <input type="hidden" name="hiddenpagination.select[${indexes}]_input" value="03"/>
		               </logic:equal>           
		               <logic:notEqual name="element" property="state" value="1">
		                 <hrms:checkmultibox name="appForm" property="pagination.select"  value="true" indexes="indexes"/>
		                 <input type="hidden" name="hiddenpagination.select[${indexes}]_input" value="03"/>
		               </logic:notEqual>
		              <%} %>
		            </logic:notEqual>
		          </hrms:kqdurationjudge>	          
              <input type="hidden" name="IDs" id="IDs_<%=i%>" value='<bean:write name="element" property="${appForm.key_field}" filter="false"/>' />
            </td>
       	   	<bean:define id="key_field1" name="element" property="${appForm.key_field}"/>
	         <bean:define id="nbase1" name="element" property="nbase"/>
	         <bean:define id="a01001" name="element" property="a0100"/>
	         <%
	         		//参数加密
	    		     String str1 = "bill_id=" + key_field1 + "&dbpre=" + nbase1 + "&a0100=" + a01001;
	         %>
            
            <logic:equal name="appForm" property="table" value="Q11">
              <hrms:priv func_id="270104,0C3414">
	               <td align="center" class="RecordRow"  style="white-space: nowrap"> 
	                 <a href="/kq/app_check_in/view_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/view.gif" border="0"></a>
	               </td>
               </hrms:priv>
            </logic:equal> 
            <logic:equal name="appForm" property="table" value="Q13">
              <hrms:priv func_id="270124,0C3434">
                <td align="center" class="RecordRow"  style="white-space: nowrap"> 
                  <a href="/kq/app_check_in/view_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/view.gif" border="0"></a>
                </td>
              </hrms:priv>
            </logic:equal> 
            <logic:equal name="appForm" property="table" value="Q15">     
              <hrms:priv func_id="270114,0C3424">
                <td align="center" class="RecordRow"  style="white-space: nowrap">  
                  <a href="/kq/app_check_in/view_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/view.gif" border="0"></a>
                </td>
              </hrms:priv>
            </logic:equal> 
                 
            <td align="center" class="RecordRow" style="white-space: nowrap">
              <hrms:kqdurationjudge startDate="<%=z1 %>">
                <logic:equal name="appForm" property="table" value="Q11">
	                <%if(isSuper)
	                  {
	                %>
	                 <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
	                <%
	                 }else{
	                %>
	                <hrms:priv func_id="270102,0C3412"> 
	                  <logic:equal name="element" property="${appForm.sp_field}" value="02"> 
	                    <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
	                  </logic:equal>
	                </hrms:priv>
	                <hrms:priv func_id="27010c,0C341c"> 
	                  <logic:equal name="element" property="${appForm.sp_field}" value="08"> 
	                    <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
	                  </logic:equal>
	                </hrms:priv>
	                <%
	                }%>
                </logic:equal> 
            
	              <logic:equal name="appForm" property="table" value="Q13">
	                <%if(isSuper)
	                  {
	                %>
	                  <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
	                <%
	                 }else{
	                %>
	                  <hrms:priv func_id="270122,0C3432"> 
	                    <logic:equal name="element" property="${appForm.sp_field}" value="02"> 
	                      <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
	                    </logic:equal>
	                  </hrms:priv>
	                  <hrms:priv func_id="27012c,0C343c"> 
	                    <logic:equal name="element" property="${appForm.sp_field}" value="08"> 
	                      <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
	                    </logic:equal>
	                  </hrms:priv>
	                <%
	                }%>
	              </logic:equal> 
		            <logic:equal name="appForm" property="table" value="Q15">          
		             <%if(isSuper)
		               {
		               %>
		                  <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
		               <%
		                 }else{
		               %>
	                 <hrms:priv func_id="270112,0C3422"> 
		                 <logic:equal name="element" property="${appForm.sp_field}" value="02"> 
		                   <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
		                 </logic:equal>
	                 </hrms:priv>
	                 <hrms:priv func_id="27011c,0C342c"> 
		                 <logic:equal name="element" property="${appForm.sp_field}" value="08"> 
		                   <a href="/kq/app_check_in/change_app.do?b_query=link&encryptParam=<%=PubFunc.encrypt(str1) %>"><img src="/images/edit.gif" border="0"></a>
		                 </logic:equal>
	                 </hrms:priv>		                 
		               <%
		               }%> 		              
		            </logic:equal>
              </hrms:kqdurationjudge>          
            </td>
            
            <logic:equal name="appForm" property="table" value="Q11">
              <hrms:priv func_id="270109,0C3419"> 
	              <%             
	             	  abean=(LazyDynaBean)  pageContext.getAttribute("element");
	             	  id=(String)abean.get("q1101");
	             	  //参数加密
		         	  String str2 = "id=" + key_field1;
	              %>
	              <td align="center"  class="RecordRow" style="white-space: nowrap">
	             	  <hrms:kqdurationjudge startDate="<%=z3 %>" >
	                    <hrms:cancelleave id='<%=id%>' flag="1" table="<%=table %>">  
	                       <a href="/kq/app_check_in/cancel_app.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                    		  <img src="/images/edit.gif" border=0>
	                    	  </a>
	                    	  <font color="red">*</font>
	                    </hrms:cancelleave>
	                    <hrms:cancelleaveno id='<%=id%>' flag="1" table="<%=table %>">  
	                       <a href="/kq/app_check_in/cancel_app.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                    		  <img src="/images/view.gif" border="0">
	                    	  </a>
	                    </hrms:cancelleaveno>
	                </hrms:kqdurationjudge>
                </td>
                
                <td align="center"  class="RecordRow" style="white-space: nowrap">
                 	<hrms:tagsell id='<%=id%>' tableName="q11">                 	
                 	</hrms:tagsell>
                </td>
              </hrms:priv>
              <bean:define id="sp_field1" name="element" property="${appForm.sp_field}"/>
				      <hrms:priv func_id="27010f,0C341f">                 
                <%
	             	abean=(LazyDynaBean)  pageContext.getAttribute("element");
	             	id=(String)abean.get("q1101");
	             	String q11z5d=(String)abean.get("q11z5");
	             	String str3 = "id="+key_field1+"&audit_flag="+sp_field1;
	              %>
                <td align="center"  class="RecordRow" style="white-space: nowrap">               
	               	<hrms:consignment id='<%=id%>' flag="1" table="<%=table %>">
	                  <%if(q11z5d.equals("03"))
	                  { %>
	                  <hrms:kqdurationjudge startDate="<%=z3 %>">
		               	  <hrms:notonecancelleave id='<%=id%>'  table="<%=table %>">
		             		 		<a href="/kq/app_check_in/fugle_posture.do?b_select1=link&encryptParam=<%=PubFunc.encrypt(str3) %>">
		                       		<img src="/images/edit.gif" border="0"></a>
		                  </hrms:notonecancelleave>
		                  <hrms:onecancelleave id='<%=id%>' table="<%=table %>">
		                    <a href="/kq/app_check_in/fugle_posture.do?b_select=link&encryptParam=<%=PubFunc.encrypt(str3) %>">
		                      <img src="/images/edit.gif" border="0">
		                    </a>
		                  </hrms:onecancelleave>
	                  </hrms:kqdurationjudge>
	               	  <%}%>
	               	</hrms:consignment> 
                </td>
              </hrms:priv>
            </logic:equal>
            
            <logic:equal name="appForm" property="table" value="Q13">
              <hrms:priv func_id="270129,0C3439"> 
	              <%             
	             	  abean=(LazyDynaBean)  pageContext.getAttribute("element");
	             	  id=(String)abean.get("q1301");
	             	  //参数加密
		         	  String str2 = "id=" + key_field1;
	              %>
	              <td align="center"  class="RecordRow" style="white-space: nowrap">
	             	  <hrms:kqdurationjudge startDate="<%=z3 %>" >
	                    <hrms:cancelleave id='<%=id%>' flag="1" table="<%=table %>">  
	                       <a href="/kq/app_check_in/cancel_app.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                    		  <img src="/images/edit.gif" border=0>
	                    	  </a>
	                    	  <font color="red">*</font>
	                    </hrms:cancelleave>
	                    <hrms:cancelleaveno id='<%=id%>' flag="1" table="<%=table %>">  
	                       <a href="/kq/app_check_in/cancel_app.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                    		  <img src="/images/view.gif" border="0">
	                    	  </a>
	                    </hrms:cancelleaveno>
	                </hrms:kqdurationjudge>
                </td>
                
                <td align="center"  class="RecordRow" style="white-space: nowrap">
                 	<hrms:tagsell id='<%=id%>' tableName="q13">                 	
                 	</hrms:tagsell>
                </td>
              </hrms:priv>
              <bean:define id="sp_field1" name="element" property="${appForm.sp_field}"/>
				      <hrms:priv func_id="27012f,0C343f">                 
                <%
	             	abean=(LazyDynaBean)  pageContext.getAttribute("element");
	             	id=(String)abean.get("q1301");
	             	String q13z5d=(String)abean.get("q13z5");
	             	String str3 = "id="+key_field1+"&audit_flag="+sp_field1;
	              %>
                <td align="center"  class="RecordRow" style="white-space: nowrap">               
	               	<hrms:consignment id='<%=id%>' flag="1" table="<%=table %>">
	                  <%if(q13z5d.equals("03"))
	                  { %>
	                  <hrms:kqdurationjudge startDate="<%=z3 %>">
		               	  <hrms:notonecancelleave id='<%=id%>' table="<%=table %>">
		             		 		<a href="/kq/app_check_in/fugle_posture.do?b_select1=link&encryptParam=<%=PubFunc.encrypt(str3) %>">
		                       		<img src="/images/edit.gif" border="0"></a>
		                  </hrms:notonecancelleave>
		                  <hrms:onecancelleave id='<%=id%>' table="<%=table %>">
		                    <a href="/kq/app_check_in/fugle_posture.do?b_select=link&encryptParam=<%=PubFunc.encrypt(str3) %>">
		                      <img src="/images/edit.gif" border="0">
		                    </a>
		                  </hrms:onecancelleave>
	                  </hrms:kqdurationjudge>
	               	  <%}%>
	               	</hrms:consignment> 
                </td>
              </hrms:priv>
            </logic:equal>
            
            <logic:equal name="appForm" property="table" value="Q15">
              <hrms:priv func_id="270118,0C3428"> 
	              <%             
	             	  abean=(LazyDynaBean)  pageContext.getAttribute("element");
	             	  id=(String)abean.get("q1501");
	             	  //参数加密
		         	  String str2 = "id=" + key_field1;
	              %>
	              <td align="center"  class="RecordRow" style="white-space: nowrap">
	             	  <hrms:kqdurationjudge startDate="<%=z3 %>" >
	                    <hrms:cancelleave id='<%=id%>' flag="1" table="<%=table %>">  
	                       <a href="/kq/app_check_in/cancel_app.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                    		  <img src="/images/edit.gif" border=0>
	                    	  </a>
	                    	  <font color="red">*</font>
	                    </hrms:cancelleave>
	                    <hrms:cancelleaveno id='<%=id%>' flag="1" table="<%=table %>">  
	                       <a href="/kq/app_check_in/cancel_app.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                    		  <img src="/images/view.gif" border="0">
	                    	  </a>
	                    </hrms:cancelleaveno>
	                </hrms:kqdurationjudge>
                </td>
                
                <td align="center"  class="RecordRow" style="white-space: nowrap">
                 	<hrms:tagsell id='<%=id%>' tableName="q15">                 	
                 	</hrms:tagsell>
                </td>
              </hrms:priv>
              <bean:define id="sp_field1" name="element" property="${appForm.sp_field}"/>
				      <hrms:priv func_id="27011f,0C342f">                 
                <%
	             	abean=(LazyDynaBean)  pageContext.getAttribute("element");
	             	id=(String)abean.get("q1501");
	             	String q15z5d=(String)abean.get("q15z5");
	             	String str3 = "id="+key_field1+"&audit_flag="+sp_field1;
	              %>
                <td align="center"  class="RecordRow" style="white-space: nowrap">               
	               	<hrms:consignment id='<%=id%>' flag="1" table="<%=table %>">
	                  <%if(q15z5d.equals("03"))
	                  { %>
	                  <hrms:kqdurationjudge startDate="<%=z3 %>">
		               	  <hrms:notonecancelleave id='<%=id%>' table="<%=table %>">
		             		 		<a href="/kq/app_check_in/fugle_posture.do?b_select1=link&encryptParam=<%=PubFunc.encrypt(str3) %>">
		                       		<img src="/images/edit.gif" border="0"></a>
		                  </hrms:notonecancelleave>
		                  <hrms:onecancelleave id='<%=id%>' table="<%=table %>">
		                    <a href="/kq/app_check_in/fugle_posture.do?b_select=link&encryptParam=<%=PubFunc.encrypt(str3) %>">
		                      <img src="/images/edit.gif" border="0">
		                    </a>
		                  </hrms:onecancelleave>
	                  </hrms:kqdurationjudge>
	               	  <%}%>
	               	</hrms:consignment> 
                </td>
              </hrms:priv>
            </logic:equal>
            <logic:iterate id="fielditem" name="appForm"  property="searchfieldlist" indexId="index">
              <logic:equal name="fielditem" property="visible" value="true">              		         
                <logic:notEqual name="fielditem" property="itemtype" value="D">
                  <logic:notEqual name="fielditem" property="codesetid" value="0">
                    <logic:empty name="fielditem" property="codesetid">
                    	<logic:equal name="fielditem" property="itemtype" value="N">
                 			<td align="left" class="RecordRow" style="white-space: nowrap">
                         <hrms:kqclassname classid="<%=classidtoname %>" />        
                       </td> 
                			</logic:equal>
                    </logic:empty>                      	
                    		
                    <logic:notEmpty name="fielditem" property="codesetid">
                     <td align="left" class="RecordRow" style="white-space: nowrap">
                        <logic:notEqual name="fielditem" property="itemid" value="q1104"> 
                          <logic:notEqual name="fielditem" property="itemid" value="e0122"> 
                            <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
                              <bean:write name="codeitem" property="codename" />   
                        	</logic:notEqual>
                        </logic:notEqual>
                        <logic:equal name="fielditem" property="itemid" value="q1104">   
                          <%
        	                  abean=(LazyDynaBean)pageContext.getAttribute("element");
        	                  id=(String)abean.get("q1104");      	                           
                          %>
                          <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                        </logic:equal>
                        <logic:equal name="fielditem" property="itemid" value="e0122">
                        	<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${appForm.uplevel}"/>  	      
                          	<bean:write name="codeitem" property="codename" />  
                         	<html:hidden name="element" property="${fielditem.itemid}"/>   
                        </logic:equal> 
                     </td>
                    </logic:notEmpty>             
                  </logic:notEqual>
                  
                  <logic:equal name="fielditem" property="codesetid" value="0">                     
                    <logic:equal name="fielditem" property="itemid" value="q1104">   
                      <%
    	                  abean=(LazyDynaBean)pageContext.getAttribute("element");
    	                  id=(String)abean.get("q1104");        	                           
                      %>
                      <td align="left" class="RecordRow" style="white-space: nowrap">
                        <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                      </td>
                    </logic:equal> 
                    <logic:notEqual name="fielditem" property="itemid" value="q1104">   
                     	<logic:equal name="appForm" property="visi" value="${fielditem.itemid}">
                     		<td align="left" class="RecordRow" width="12%" title="<%= re %>" style="white-space: nowrap">
                          <%= ree %>
                        </td>
                      </logic:equal> 
                   		<logic:notEqual name="appForm" property="visi" value="${fielditem.itemid}">
                   			<td align="left" class="RecordRow" style="white-space: nowrap">
                 	  			<logic:equal name="appForm" property="table" value="Q15">
                 	     			<logic:notEqual name="fielditem" property="itemid" value="a0101"> 
                 	        			<bean:write name="element" property="${fielditem.itemid}" filter="false"/>                 
                 	     			</logic:notEqual>
                 	     			<bean:define id="nbase1" name='element' property="nbase"/>
                 	     			<bean:define id="a01001" name='element' property="a0100"/>
                 	     			<bean:define id="b01101" name='element' property="b0110"/>
						          <%
						          	//参数加密
						          	String nbase2=PubFunc.encrypt(nbase1.toString());
						            String a01002=PubFunc.encrypt(a01001.toString());
						            String b01102=PubFunc.encrypt(b01101.toString());
						          %>
               	      			<logic:equal name="fielditem" property="itemid" value="a0101">   
               	        			<a href="###" onclick="openappstatistics('<%=nbase2 %>','<%=a01002 %>','<%=b01102 %>')">
               	          			<bean:write name="element" property="${fielditem.itemid}" filter="false"/>                
               	        			</a>
               	      			</logic:equal>
                 	  			</logic:equal>
                   	  			
                 	  			<logic:notEqual name="appForm" property="table" value="Q15">
                 	    			<bean:write name="element" property="${fielditem.itemid}" filter="false"/>                 
                 	 			  </logic:notEqual>                           
                    		</td>
                   		</logic:notEqual>
                    </logic:notEqual>
                  </logic:equal>                                            
                </logic:notEqual>
                <logic:equal name="fielditem" property="itemtype" value="D">
                   <td align="left" class="RecordRow" style="white-space: nowrap">
                     <bean:write name="element" property="${fielditem.itemid}" filter="false"/>   
                   </td>
                </logic:equal>                         
              </logic:equal>    
            </logic:iterate>          
          </tr>
          <%r++;%>
        </hrms:paginationdb>
        </table>
        <script language='javascript' >
	        document.write("</div>");
        </script>
      </td>
     </tr>
     
     <tr>
      <td colspan="${appForm.cols}" style="white-space: nowrap">
 			<script language='javascript' >
		       document.write("<div  id='page'  style='left:5;width:99%'  >");
	      </script> 
        <table  width="100%" align="center"  class="RecordRowTop0">
		      <tr>
		        <td valign="bottom" class="tdFontcolor">
					    <hrms:paginationtag name="appForm" pagerows="${appForm.pagerows}" property="pagination" scope="page" refresh="true">
					    </hrms:paginationtag>
			      </td>
	          <td align="right" class="tdFontcolor">
		          <hrms:paginationdblink name="appForm" property="pagination" nameId="appForm" scope="page">
				      </hrms:paginationdblink>
			      </td>
		      </tr>
        </table>
        <table  width="90%" align="left">
	        <tr>
	          <td align="left" valign="middle" nowrap height="30">
		          <logic:equal name="appForm" property="table" value="Q11">
		          
				        <logic:equal name="appForm" property="bflag" value="A">
					        <hrms:priv func_id="270102,0C3412">  
				            <input type="button"  name="b_select" value="批准" class="mybutton" onclick="passvalide()"> 
				          </hrms:priv>
				          <hrms:priv func_id="27010c,0C341c"> 
				            <input type="button" name="b_select" value="审核" class="mybutton" onclick="lookvalide()">  
				          </hrms:priv>
		            </logic:equal>
		            
		            <hrms:priv func_id="27010e,0C341e" module_id="">	
		              <input type="button" name="b_select" value="申请登记" class="mybutton" onclick="appregister();">
		            </hrms:priv>
		            
		            <hrms:priv func_id="270100,0C3410" module_id="">	
		      	      <input type="button" name="b_select" value="<bean:message key="label.button.manu"/>" class="mybutton" onclick="manu();">
	              </hrms:priv>  
	              
		            <hrms:priv func_id="270101,0C3411" module_id="">	
		      	      <html:button styleClass="mybutton" property="b_con" onclick="window.location.replace('/kq/app_check_in/conselect.do');"><bean:message key="label.button.con"/></html:button>
	              </hrms:priv>
	              
	              <hrms:priv func_id="270106,0C3416" module_id="">	
	                <logic:equal name="appForm" property="sortid" value="1">
	                  <input type="button" name="br_return" value='打印' class="mybutton" onclick="change_print();"> 
	                </logic:equal>
	              </hrms:priv>
	              
	         	    <hrms:priv func_id="270108,0C3418" module_id="">	
	          		  <input type="button" name="prot_excel" value='导出Excel' class="mybutton" onclick="export_excel(0);"> 
	          	  </hrms:priv>
	          	  
	          	  <hrms:priv func_id="27010h,0C341h" module_id="">
	          	    <input type="button" name="download_template" value='下载模板' class="mybutton" onclick="export_excel(1);"> 
	          	  </hrms:priv>
	              <hrms:priv func_id="27010i,0C341i" module_id="">
	          	    <input type="button" name="data_import" value='数据导入' class="mybutton" onclick="import_excel();"> 
	              </hrms:priv>
	          	  
	              <logic:equal name="appForm" property="bflag" value="M"> 
		      		    <hrms:priv func_id="27010d,0C341d" module_id="">	
		        		    <input type="button" name="b_select" value="批量审批" class="mybutton" onclick="batchsubscribe();">
		      		    </hrms:priv>
		      	    </logic:equal>
		      	 <!-- 汉口银行汇总审批
		      	   <logic:equal name="appForm" property="applytime" value="q11z4"> 
		      	     <hrms:priv func_id="27010j,0C341j" module_id="">
	          	    <input type="button" name="b_allselect" value='汇总审批' class="mybutton" onclick="sumapproval();" > 
	             </hrms:priv>
		      	   </logic:equal> 
		      	 -->  
		      	    <hrms:priv func_id="27010g,0C341g" module_id="">
	                <input type='button' class="mybutton" onclick='abateselect()' value='<bean:message key="button.abate"/>'/>
	              </hrms:priv>
	              
	              <hrms:priv func_id="270103,0C3413" module_id="">
	                <input type='button' class="mybutton" onclick='noselect()' value='<bean:message key="button.delete"/>'/>	 
		            </hrms:priv>	            
	            </logic:equal>  
	            
	            <logic:equal name="appForm" property="table" value="Q13">
					      <logic:equal name="appForm" property="bflag" value="A">
						      <hrms:priv func_id="270122,0C3432">  
			              <input type="button"  name="b_select" value="批准" class="mybutton" onclick="passvalide()"> 
			            </hrms:priv>
			            
			            <hrms:priv func_id="27012c,0C343c"> 
			              <input type="button" name="b_select" value="审核" class="mybutton" onclick="lookvalide()">  
			            </hrms:priv>		            
					      </logic:equal>
					      
					      <hrms:priv func_id="27012e,0C343e" module_id="">	
					        <input type="button" name="b_select" value="申请登记" class="mybutton" onclick="appregister();">
					      </hrms:priv>
					      
		            <hrms:priv func_id="270120,0C3430" module_id="">		        
		      	      <input type="button" name="b_select" value="<bean:message key="label.button.manu"/>" class="mybutton" onclick="manu();">
	              </hrms:priv>  
	              
		            <hrms:priv func_id="270121,0C3431" module_id="">	
		      	      <html:button styleClass="mybutton" property="b_con" onclick="window.location.replace('/kq/app_check_in/conselect.do');"><bean:message key="label.button.con"/></html:button>
	              </hrms:priv>
	              
	              <hrms:priv func_id="270126,0C3436" module_id="">	
	                <logic:equal name="appForm" property="sortid" value="1">
	                  <input type="button" name="br_return" value='打印' class="mybutton" onclick="change_print();"> 
	                </logic:equal>
	              </hrms:priv>
	              
	              <hrms:priv func_id="270128,0C3438" module_id="">	
	          	    <input type="button" name="prot_excel" value='导出Excel' class="mybutton" onclick="export_excel(0);"> 
	              </hrms:priv>
	              
	              <hrms:priv func_id="27012h,0C343h" module_id="">
	          	    <input type="button" name="download_template" value='下载模板' class="mybutton" onclick="export_excel(1);"> 
	              </hrms:priv>
	              <hrms:priv func_id="27012i,0C343i" module_id="">
	          	    <input type="button" name="data_import" value='数据导入' class="mybutton" onclick="import_excel();"> 
	              </hrms:priv>
	              
	              <logic:equal name="appForm" property="bflag" value="M">    
						      <hrms:priv func_id="27012d,0C343d" module_id="">	
						        <input type="button" name="b_select" value="批量审批" class="mybutton" onclick="batchsubscribe();">
						      </hrms:priv>
						    </logic:equal>
						    
	              <hrms:priv func_id="27012g,0C343g" module_id="">
	                <input type='button' class="mybutton" onclick='abateselect()' value='<bean:message key="button.abate"/>'/>
	              </hrms:priv>
	              
	              <hrms:priv func_id="270123,0C3433" module_id="">          
		              <input type='button' class="mybutton" onclick='noselect()' value='<bean:message key="button.delete"/>'/>	  
		            </hrms:priv> 
	            </logic:equal>
	            
	            <logic:equal name="appForm" property="table" value="Q15">
		            <logic:equal name="appForm" property="bflag" value="A">
		     	        <hrms:priv func_id="270112,0C3422">  
	                  <input type="button"  name="b_select" value="批准" class="mybutton" onclick="passvalide()"> 
	                </hrms:priv>
	                
	                <hrms:priv func_id="27011c,0C342c"> 
	                  <input type="button" name="b_select" value="审核" class="mybutton" onclick="lookvalide()">  
	                </hrms:priv>
		            </logic:equal>
		            
					      <hrms:priv func_id="27011e,0C342e" module_id="">	
					        <input type="button" name="b_select" value="申请登记" class="mybutton" onclick="appregister();">
					      </hrms:priv>	 
					         
					      <hrms:priv func_id="270110,0C3420" module_id="">		        
		      	      <input type="button" name="b_select" value="<bean:message key="label.button.manu"/>" class="mybutton" onclick="manu();">
	              </hrms:priv>  
	             
		            <hrms:priv func_id="270111,0C3421" module_id="">	
		      	      <html:button styleClass="mybutton" property="b_con" onclick="window.location.replace('/kq/app_check_in/conselect.do');"><bean:message key="label.button.con"/></html:button>
	              </hrms:priv>
	             
	              <hrms:priv func_id="270116,0C3426" module_id="">	
	                <logic:equal name="appForm" property="sortid" value="1">
	                  <input type="button" name="br_return" value='打印' class="mybutton" onclick="change_print();"> 
	                </logic:equal>
	              </hrms:priv>
	             
	              <hrms:priv func_id="270119,0C3429" module_id="">	
	          	    <input type="button" name="prot_excel" value='导出Excel' class="mybutton" onclick="export_excel(0);"> 
	              </hrms:priv>
	              
	              <hrms:priv func_id="27011h,0C342h" module_id="">
	          	    <input type="button" name="download_template" value='下载模板' class="mybutton" onclick="export_excel(1);"> 
	              </hrms:priv>
	             <hrms:priv func_id="27011i,0C342i" module_id="">
	          	    <input type="button" name="data_import" value='数据导入' class="mybutton" onclick="import_excel();"> 
	             </hrms:priv>
	             
	              <logic:equal name="appForm" property="bflag" value="M">    
						      <hrms:priv func_id="27011d,0C342d" module_id="">	
						        <input type="button" name="b_select" value="批量审批" class="mybutton" onclick="batchsubscribe();">
						      </hrms:priv>
						    </logic:equal>
						   
	              <hrms:priv func_id="270113,0C3423" module_id="">
	  	            <input type='button' class="mybutton" onclick='noselect()' value='<bean:message key="button.delete"/>'/>	
		            </hrms:priv>
	            </logic:equal>    
	               	
	            <logic:equal value="dxt" name="appForm" property="returnvalue"> 
	              <hrms:tipwizardbutton flag="workrest" target="il_body" formname="appForm"/> 
	            </logic:equal>
	          </td>
	        </tr>         
        </table>
	      <script language='javascript' >
		      document.write("</div>");
	      </script>
      </td>
    </tr>
  </table>
</html:form>
<iframe name="mysearchframe" style="display: none;"></iframe>
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
        initvalue();
        
      hide_nbase_select('select_pre');
</script>
