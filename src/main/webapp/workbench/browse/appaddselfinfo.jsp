<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String codevalue="";
	String code="";
	if(userView != null){
	   css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  if(!userView.isSuper_admin())
	  { 
	    code=userView.getManagePrivCode();
	    codevalue=userView.getManagePrivCodeValue();
	  } 
         //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
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
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<!-- 引入ext 和代码控件      wangb 20180206 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
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
    document.oncontextmenu = function() {return false;}
</script>
<%String orgtemp="";
  String orgtempview="";
  String postemp="";
  String postempview="";
  String kktemp="";
  String kktempview="";
  int rowss=1;
  String birthdayfield = "";
  String agefield = "";
  String workagefield = "";
  String postagefield = "";
  String axfield = "";
  String axviewfield = "";
 
  %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
   var date_desc;    
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
       event.srcElement.releaseCapture();   
     }
   }   
   function showDateSelectBox(srcobj)
   {/* zxj 20150627 此框出现会导致输入日期时弹出一堆div，看不出有什么用处，暂注释掉
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
       */
   }  
</script>
<script language="javascript">

  function exeButtonAction(actionStr,target_str)
   {
     // alert(actionStr);
       target_url=actionStr;
       window.open(target_url,target_str); 
   }

  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
     <logic:notEqual value="#####" name="element" property="itemid">
      		<bean:define id="fl" name="element" property="fillable"/>
		    <bean:define id="desc" name="element" property="itemdesc"/>
		     var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
        	  var dobj=valueInputs[0];
        	  if("${fl}"=='true'&&dobj.value.length<1){
          	  alert("${desc}"+MUSTER_INPUT+'！');

          	return ;
          }
          <logic:equal name="element" property="itemid" value="b0110">
              <%orgtemp="infoFieldList["+index+"].value";%>  
              <%orgtempview="infoFieldList["+index+"].viewvalue";%>              
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e0122">
                 <%postemp="infoFieldList["+index+"].value";%>
                  <%postempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          <logic:equal name="element" property="itemid" value="e01a1">
                 <%kktemp="infoFieldList["+index+"].value";%>
                  <%kktempview="infoFieldList["+index+"].viewvalue";%>
          </logic:equal>
          

          <logic:equal name="element" property="itemid" value="${selfInfoForm.birthdayfield}">
                  <%birthdayfield="infoFieldList["+index+"].value";%>
             </logic:equal>
           <logic:equal name="element" property="itemid" value="${selfInfoForm.agefield}">
                  <%agefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.workagefield}">
                  <%workagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
               <logic:equal name="element" property="itemid" value="${selfInfoForm.postagefield}">
                  <%postagefield="infoFieldList["+index+"].value";%>
             </logic:equal>
             <logic:equal name="element" property="itemid" value="${selfInfoForm.axfield}">
                  <%axfield="infoFieldList["+index+"].value";%>
                 <%axviewfield="infoFieldList["+index+"].viewvalue";%>
             </logic:equal>
          
          
        <logic:equal name="element" property="itemtype" value="D">   
         
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infoFieldList["+index+"].value"%>');
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
	</logic:notEqual>
      </logic:iterate>   
      
      if(tag == undefined)
    	  tag = true;
      
     return tag;   
  }
  
  function checkValue(obj,itemlength,decimalwidth)
  {
     if(decimalwidth=='')
      return true;
     if(itemlength=='')
      return true;
     var t_len=obj.value;
     if(t_len!="")
     {
        var decimalw=parseInt(decimalwidth,10);	
        var itemlen=parseInt(itemlength,10);	
        var inde=t_len.indexOf(".");
        if(inde==-1)
        {
          if(t_len.length>itemlen)
          {
            alert(INTEGER_LENGTH_SET+itemlen+","+PLEASE_UPDATE+"！");
            obj.focus(); 
            return false;
          }
        }else
        {
           var q_srt=t_len.substring(0,inde);
           var n_srt=t_len.substring(inde+1);           
           if(q_srt.length>itemlen)
           {
             alert(INTEGER_LENGTH_SET+itemlen+","+PLEASE_UPDATE+"！");
             obj.focus(); 
             return false;
           }else if(n_srt.length>decimalw)
           {
              alert(DECIMAL_LENGTH_SET+decimalw+","+PLEASE_UPDATE+"！");
              obj.focus(); 
              return false;
           }
        }
     }
  }
function changinfor(){
  	selfInfoForm.action="/selfservice/selfinfo/inforchange.do?b_query=link";
	selfInfoForm.submit();
}
 function IsDigit() 
 { 
    return event.keyCode==46;    
 } 
function change() {
 	var list = document.getElementById("list");
 	va = list.value;
 	if (va == "A01") {
 		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defend=link&isAppEdite=1";
 	} else {
 		selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_defendother=search&setname="+va+"&flag=infoself&isAppEdite=1"
 	}
 	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
}
function approveall(chg_id) {
if (chg_id == "") {
	return false;
}
if(confirm("您确定要整体报批吗？整体报批后将不能修改！")){	
		//selfInfoForm.action="/selfservice/selfinfo/inforchange.do?b_query=link&savEdit=baopi&chg_id="+chg_id;
		selfInfoForm.action= "/workbench/browse/appeditselfinfo.do?b_prove=link&isAppEdite=1&&savEdit=baopi&chg_id="+chg_id;
		selfInfoForm.target = "mil_body";	 
   		selfInfoForm.submit();
   	}
}
function approveReturn(a0100) {
	selfInfoForm.action="/workbench/browse/appeditselfinfo.do?b_defendother=search&isAppEdite=1";
	selfInfoForm.target="mil_body";
	selfInfoForm.submit();
}
</script>
<script type="text/javascript" src="/js/dict.js"></script>
<%
	int i=0;
	int flag=0;
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<html:form action="/selfservice/selfinfo/editselfinfo" >
	
<script language="javascript">  
    function getchangeposun(outparamters)
    {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      AjaxBind.bind(selfInfoForm.orgparentcode,/*$('orgparentcode')*/orgparentcode);
      AjaxBind.bind(selfInfoForm.deptparentcode,/*$('deptparentcode')*/deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
      var valueInputsun=document.getElementsByName("<%=postemp%>");
      var dobjun=valueInputsun[0];
      dobjun.value="";
      valueInputsun=document.getElementsByName("<%=postempview%>");
      dobjun=valueInputsun[0];
      dobjun.value="";
      var cc=document.getElementsByName("<%=kktemp%>");
      var dobjkk=cc[0];
      dobjkk.value="";
      cc=document.getElementsByName("<%=kktempview%>");
      dobjkk=cc[0];
      dobjkk.value="";
      //AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
      
  
  }
   function getchangeposum(outparamters)
   {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var orgvalue=outparamters.getValue("orgvalue");

      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=outparamters.getValue("orgviewvalue");
         var valueInputsun=document.getElementsByName("<%=orgtemp%>");
         var dobjun=valueInputsun[0];
         dobjun.value=orgvalue;
         valueInputsun=document.getElementsByName("<%=orgtempview%>");
         dobjun=valueInputsun[0];
         dobjun.value=orgvalueview;
      }
      var valueInputskk=document.getElementsByName("<%=kktemp%>");
      var dobjkk=valueInputskk[0];
      dobjkk.value="";
      valueInputskk=document.getElementsByName("<%=kktempview%>");
      dobjkk=valueInputskk[0];
      dobjkk.value="";
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      AjaxBind.bind(selfInfoForm.orgparentcode,/*$('orgparentcode')*/orgparentcode);
      AjaxBind.bind(selfInfoForm.deptparentcode,/*$('deptparentcode')*/deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
  
  }
   function getchangeposkk(outparamters)
   {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      var orgvalue=outparamters.getValue("orgvalue");
      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=outparamters.getValue("orgviewvalue");
         var valueInputsun=document.getElementsByName("<%=orgtemp%>");
         var dobjun=valueInputsun[0];
         dobjun.value=orgvalue;
         valueInputsun=document.getElementsByName("<%=orgtempview%>");
         dobjun=valueInputsun[0];
         dobjun.value=orgvalueview;
      }
       var deptvalue=outparamters.getValue("deptvalue");
      if(deptvalue!=null && deptvalue.length>0)
      {
         var deptviewvalue=outparamters.getValue("deptviewvalue");
         var valueInputsum=document.getElementsByName("<%=postemp%>");
         var dobjum=valueInputsum[0];
         dobjum.value=deptvalue;
         valueInputsum=document.getElementsByName("<%=postempview%>");
         dobjum=valueInputsum[0];
         dobjum.value=deptviewvalue;
      }
      AjaxBind.bind(selfInfoForm.orgparentcode,/*$('orgparentcode')*/orgparentcode);
     // AjaxBind.bind(selfInfoForm.deptparentcode,/*$('deptparentcode')*/deptparentcode);
     // AjaxBind.bind(selfInfoForm.posparentcode,/*$('posparentcode')*/posparentcode);
  
  }
  function changepos(pretype)
  {   
      var valueInputsun=document.getElementsByName("<%=orgtemp%>");
      var dobjun=valueInputsun[0];
      var valueInputsum=document.getElementsByName("<%=postemp%>");
      var dobjum=valueInputsum[0];
      var valueInputskk=document.getElementsByName("<%=kktemp%>");
      var dobjkk=valueInputskk[0];
      var hashvo=new ParameterSet();
      hashvo.setValue("pretype",pretype);
      hashvo.setValue("orgparentcodestart",dobjun.value);
      hashvo.setValue("deptparentcodestart",dobjum.value);
      hashvo.setValue("posparentcodestart",dobjkk.value);
      if(pretype=="UN")
      {
        var request=new Request({method:'post',onSuccess:getchangeposun,functionId:'02010001012'},hashvo);
      }
     if(pretype=="UM")
      {
        var request=new Request({method:'post',onSuccess:getchangeposum,functionId:'02010001012'},hashvo);
      }
      if(pretype=="@K")
      {
        var request=new Request({method:'post',onSuccess:getchangeposkk,functionId:'02010001012'},hashvo);
      }
  }
  function prove(){
if(validate()){
if(confirm(APP_DATA_NOT_UPDATE+"?")){
selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_appeal=link&buttonvalue=0";
selfInfoForm.submit();
}else{
return;
}
}
}

function calculatebirthday(obj)
  {
     //alert(obj.value);
      var hashvo=new ParameterSet();
      hashvo.setValue("idcardvalue",obj.value);
      var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'02010001013'},hashvo);
  }


function getBirthdayAge(outparamters)
  {
     var birthdayvalue=outparamters.getValue("birthdayvalue");
     var agevalue=outparamters.getValue("agevalue");
      var axvalue=outparamters.getValue("axvalue");
     if(birthdayvalue!=null)
     {
    	 
         var valueInputs=document.getElementsByName("<%=birthdayfield%>");
        
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=birthdayvalue;
        
     }
     }
     if(agevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=agefield%>");
         var dobj=valueInputs[0];
          if(dobj!=null)
         dobj.value=agevalue;
     }  
     if(axvalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=axfield%>");
         var dobj=valueInputs[0];
         //alert(axvalue);
          if(dobj!=null)
         dobj.value=axvalue;
         if(axvalue==1)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            //alert(axvalue);
            if(dobj!=null)
            dobj.value=MAN_PERSON;
         }else if(axvalue==2)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            //alert(axvalue);
            if(dobj!=null)
            dobj.value=WOMAN_PERSON;
         }
     }        
     
  }
  function CalculateWorkDate(obj)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("workdatevalue",obj.value);
      var request=new Request({method:'post',onSuccess:getWorkAge,functionId:'02010001014'},hashvo);
  }
  function getWorkAge(outparamters)
  {
     var workagevalue=outparamters.getValue("workagevalue");
     if(workagevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=workagefield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=workagevalue;
         <logic:equal name="selfInfoForm" property="workdatefield" value="${selfInfoForm.startpostfield}">
             valueInputs=document.getElementsByName("<%=postagefield%>");
             dobj=valueInputs[0];
             dobj.value=workagevalue;
         </logic:equal>   
         }
     }     
  }
  function CalculatePostAge(obj)
  {
      var hashvo=new ParameterSet();
      hashvo.setValue("postdatevalue",obj.value);
      var request=new Request({method:'post',onSuccess:getPostAge,functionId:'02010001015'},hashvo);
  }
  function getPostAge(outparamters)
  {
     var postagevalue=outparamters.getValue("postagevalue");
     if(postagevalue!=null)
     {
         var valueInputs=document.getElementsByName("<%=postagefield%>");
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=postagevalue;  
         <logic:equal name="selfInfoForm" property="startpostfield" value="${selfInfoForm.workdatefield}">
             valueInputs=document.getElementsByName("<%=workagefield%>");
             dobj=valueInputs[0];
             dobj.value=workagevalue;
         </logic:equal>   
     }    
     } 
  }
  

  function proves(){
if(validate()){
if(confirm(APP_DATA_NOT_UPDATE+"?")){
selfInfoForm.action="/workbench/browse/appeditnotselfinfo.do?b_appeals=link&isAppEdite=1";
selfInfoForm.submit();
}else{
return;
}
}
}


  function saves(){
if(validate()){

selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_saves=link&isAppEdite=1";
selfInfoForm.submit();
}else{
return;
}
}

  function savesre(){
if(validate()){

selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_savere=link&isAppEdite=1";
selfInfoForm.submit();
}else{
return;
}
}
  function diswrite(o){
  
  	o.disabled="true";
  }
  function writeable(){
 	 var tablevos=document.getElementsByTagName("INPUT");
 	 if(validate()){
      	for(var i=0;i<tablevos.length;i++){
      	tablevos[i].disabled="";
      	}
      	selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_save=link";
      	selfInfoForm.submit();
      	}
  }
  
   function writeabless(){
 	 var tablevos=document.getElementsByTagName("INPUT");
 	 if(validate()){
 		var saveButton = document.getElementsByName("appSaves")[0];
 		if(saveButton)
 			saveButton.disabled=true;
			
      	for(var i=0;i<tablevos.length;i++){
      		tablevos[i].disabled="";
      	}
      	
      	selfInfoForm.action="/workbench/browse/appeditselfinfo.do?b_savess=link&isAppEdite=1";
      	selfInfoForm.submit();
     }
  }
  
  function capp(){
  
   if(confirm(APPLIC_OK+'？')){
  		 selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_app=link";
      	selfInfoForm.submit();
   }else{
   return;
   }
  }
  
  
 function handleEnter (field, event) {
  var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
  alert(keyCode);
  return true;
 }      
  
  
</script>

<script language="JavaScript">

	function openappealpage(){
	var theurl="/selfservice/selfinfo/showpersoninfo.do?b_query=link&a01001=${selfInfoForm.a0100}&pdbflag1=${selfInfoForm.userbase}&setprv=2";
	var retvalue=	window.showModalDialog(theurl, true, 
        "dialogWidth:800px; dialogHeight:1000px;resizable:no;center:yes;scroll:yes;status:no");   
	if(retvalue!=null){
		window.parent.location.reload();
	}
	} 
	
	function appealok(){
	alert(INFOR_APP_OK+'！');

	// window.returnValue='ok';
		window.location.reload();
	
}
function appeal(outparamters){

	var cset=outparamters.getValue("cset");
	var citem=outparamters.getValue("citem");
	if(citem.length>0){
		alert(citem);
		window.close();
	}else{
		if(cset.length>0){
			if(confirm(cset)){
				var pars="a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
				var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appealok,functionId:'0201001098'});
				
			}else{
				window.close();
			}
		}else{
			var pars="a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
			var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appealok,functionId:'0201001098'});
	
		
		}
	
	}
}

function checkdate(){
	if(confirm(APP_DATA_NOTUPDATE+"?")){
	var dbname='<%=userView.getDbname()%>';
	
	var pars="a0100=${selfInfoForm.a0100}&pdbflag="+dbname;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appeal,functionId:'0201001099'});
	}
}
	

</script>


 <% SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
 		rowss=new Integer(selfInfoForm.getRownums()).intValue();
 		
      if(selfInfoForm.getInfoFieldList().size()>0){ %>  
<table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" style="margin-top: 5px">

 <html:hidden name="selfInfoForm" property="userbase"/> 
 <html:hidden name="selfInfoForm" property="actiontype"/> 
 <html:hidden name="selfInfoForm" property="a0100"/> 
 <html:hidden name="selfInfoForm" property="i9999"/> 
  <html:hidden name="selfInfoForm" property="orgparentcode"/> 
  <html:hidden name="selfInfoForm" property="deptparentcode"/> 
  <html:hidden name="selfInfoForm" property="posparentcode"/> 
<logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
<logic:equal value="#####" name="element" property="itemid">
	<logic:notEqual value="0" name="element" property="itemlength">
		<%if (i % 2 != 0) {
			i++;
		%>
		<td colspan="2" class="AddTableRow"></td>
		<%
		} 
		flag = 0;
		%>
		</table>
		</div>
		</td></tr>
	</logic:notEqual>
	<tr class='trDeep1'>
		<td colspan='4'>
    		<img src='/images/new_target_wiz.gif'>&nbsp;<a href='javascript:void(0)' onclick="showinfo('show${element.itemlength }')">
    		<bean:write name="element" property="itemdesc"/></a>
    	</td>
    </tr>
    <tr class='trShallow1'><td colspan='4'>
    <logic:equal value="0" name="element" property="itemlength">
    	<div id="show${element.itemlength }" style='display:block;'>
    </logic:equal>
    <logic:notEqual value="0" name="element" property="itemlength">
    	<div id="show${element.itemlength }" style='display:none;'>
    </logic:notEqual>
    <table  border='0' cellspacing='1' cellpadding='1' width='100%' class='ListTable3'>
</logic:equal>
<logic:notEqual value="#####" name="element" property="itemid">
   <logic:equal name="element" property="priv_status" value="1"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
        <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
          <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image} 
              <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
         </td>
         <td align="left"  nowrap valign="top">
             &nbsp;<html:text   name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'  onclick="Element.hide('dict');" styleClass="textColorRead" readonly="true" maxlength="${element.itemlength}" />
            <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>&nbsp;                
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
          <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
              <hrms:textnewline text="${element.itemdesc}"  len="10"></hrms:textnewline> 
         </td>
         <td align="left"  nowrap valign="top">
            &nbsp;<html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" maxlength="${element.itemlength}" />
            <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>&nbsp;              
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
            <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
             <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>  
           </td>
           <td align="left"  nowrap valign="top">
               &nbsp;<html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth}" />
               <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>&nbsp;                 
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;%>
            <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             
            <logic:equal name="element"  property="fillable" value="false">
                 &nbsp;
              ${image}
              <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>   
            </logic:equal>        
                 </td>
                 <td align="left"  nowrap valign="top"  colspan="3">
                 <!-- style="width:550px;height:100px;" 员工管理模块，文本域样式 jingq add 2014.10.21 -->
                  &nbsp;<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" rows="10"  cols="66" style="width:550px;height:100px;" styleClass="textColorRead"/>
                  <logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
          			 </logic:equal>                 
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>               
              <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
                 <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
               
                </td>
                <td align="left"  nowrap valign="top" colspan="3">
                  &nbsp;<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" rows="10" readonly="true" cols="66" style="width:550px;height:100px;" styleClass="textColorRead"/>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
           			</logic:equal>&nbsp;
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
           <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
                 <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
           </td>
           <td align="left"  nowrap valign="top">
                 <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'/>  
                    &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" /> 
            	<logic:equal name="element"  property="fillable" value="true">
            		<font color="red">*</font>
          		 </logic:equal>&nbsp;                    
           </td>
         <%if(flag==0){%>
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:notEqual>
               
   </logic:equal>  
   <logic:equal name="element" property="priv_status" value="2"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
        <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
            <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
            <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
         </td>
         <td align="left"  nowrap valign="top">
            <logic:notEmpty name="selfInfoForm" property="workdatefield">
										<logic:equal name="selfInfoForm" property="workdatefield" value="${element.itemid}"><!-- 【8804】员工管理/信息浏览/申请修改，增加学历子集，日期型指标又出现时分秒了，不对。 jingq add 2015.04.16 -->
                     &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"  extra="editor"   class="text4" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate"  onblur="CalculateWorkDate(this)"/>
										</logic:equal>
										<logic:notEqual name="selfInfoForm" property="workdatefield" value="${element.itemid}">
											<logic:notEmpty name="selfInfoForm" property="startpostfield">
												<logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                            &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor"  class="text4" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate" onblur="CalculatePostAge(this)" />
												</logic:equal>
												<logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                          &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor"  class="textColorWrite" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate" onblur="Element.hide('date_panel');"  onclick="showDateSelectBox(this);" />
												</logic:notEqual>
											</logic:notEmpty>
											<logic:empty name="selfInfoForm" property="startpostfield">
                    &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor"  class="text4" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate" onblur="Element.hide('date_panel');"  onclick="showDateSelectBox(this);" />
											</logic:empty>
										</logic:notEqual>
									</logic:notEmpty>
									<logic:empty name="selfInfoForm" property="workdatefield">
										<logic:notEmpty name="selfInfoForm" property="startpostfield">
											<logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                     &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor"  class="text4" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate" onblur="CalculatePostAge(this)"  />  
                 </logic:equal>
											<logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                  &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" class="text4" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate" onblur="Element.hide('date_panel');" onclick="showDateSelectBox(this);" />
											</logic:notEqual>
										</logic:notEmpty>
										<logic:empty name="selfInfoForm" property="startpostfield">
                 &nbsp;<input type="text"  name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor"  class="text4" style='width:200px' maxlength="${element.itemlength}" dataType="simpledate" itemlength=${element.itemlength } dropDown="dropDownDate" onblur="Element.hide('date_panel');" onclick="showDateSelectBox(this);" />
										</logic:empty>
									</logic:empty>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
           
           
                    
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
             <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
                 <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
         </td>
         <td align="left"  nowrap valign="top">
           <logic:notEmpty name="selfInfoForm" property="idcardfield">
										<logic:equal name="selfInfoForm" property="idcardfield" value="${element.itemid}">
            
                   &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="calculatebirthday(this)" />
										</logic:equal>
										<logic:notEqual name="selfInfoForm" property="idcardfield" value="${element.itemid}">
                   &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" />
										</logic:notEqual>
									</logic:notEmpty>
									<logic:empty name="selfInfoForm" property="idcardfield">
                 &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" />
									</logic:empty>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
           
           
                    
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
              <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo} >            
             &nbsp;
              ${image}
               <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
       
           </td>
           <td align="left"  nowrap valign="top">
             <logic:equal name="element"  property="decimalwidth" value="">
               &nbsp;<html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>&nbsp;&nbsp;&nbsp;&nbsp;   
             </logic:equal>
             <logic:notEqual name="element"  property="decimalwidth" value="">
               &nbsp;<html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth+1}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>&nbsp;&nbsp;&nbsp;&nbsp;   
             </logic:notEqual>
            <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>   &nbsp;               
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;%>
              <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
                  <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>         
                 </td>
                 <td align="left"  nowrap valign="top"  colspan="3">
                  &nbsp;<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  rows="10"  cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
          			 </logic:equal>   &nbsp;                  
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>               
             <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
                    <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>    
                </td>
                <td align="left"  nowrap valign="top" colspan="3">
                  &nbsp;<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  rows="10"  cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
          			 </logic:equal>   &nbsp;                    
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
             <hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
              <td align="right" nowrap valign="top" ${itemmemo}>            
             &nbsp;
              ${image}
             <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline> 
             </td>
            <td align="left"  nowrap valign="top">
                <logic:equal name="element" property="itemid" value="b0110">
                   <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="b0110"  onchange="changepos('${element.codesetid}')"/>
                   <%-- 去掉键盘事件 提供模糊查询功能       wangb 20171127             onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" --%>  
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" /> 
                     <%--<img  src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>",selfInfoForm.orgparentcode.value,"1");'/>&nbsp;--%>
                     <%-- 使用代码组件控件兼容非IE浏览器 wangb 20180206 bug 34544--%>
                     <img src="/images/code.gif" align="absmiddle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
                 </logic:equal>
                <logic:equal name="element" property="itemid" value="e0122">
                    <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e0122"  onchange="changepos('${element.codesetid}')"/>  
                      <%-- 去掉键盘事件 提供模糊查询功能       wangb 20171127             onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" --%>
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);"/> 
                     <%--<img  src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>",selfInfoForm.deptparentcode.value,"2");'/>&nbsp;--%>
                     <%-- 使用代码组件控件兼容非IE浏览器 wangb 20180206 bug 34544--%>
                     <img src="/images/code.gif" align="absmiddle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
                </logic:equal>
                 <logic:equal name="element" property="itemid" value="e01a1">
                   <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e01a1"  onchange="changepos('${element.codesetid}')"/>  
                      <%-- 去掉键盘事件 提供模糊查询功能       wangb 20171127             onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" --%>
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" /> 
                     <%--<img  src="/images/code.gif" onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>",selfInfoForm.posparentcode.value,"2");'/>&nbsp;--%>
                     <%-- 使用代码组件控件兼容非IE浏览器 wangb 20180206 bug 34544--%>
                     <img src="/images/code.gif" align="absmiddle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
                </logic:equal>
               <logic:notEqual name="element" property="itemid" value="b0110">
                <logic:notEqual name="element" property="itemid" value="e0122">
                    <logic:notEqual name="element" property="itemid" value="e01a1">
                      <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'/>  
                      <%-- 去掉键盘事件 提供模糊查询功能       wangb 20180206             onkeyup="addDict('${element.codesetid}',this);" onkeydown="return inputType2(this,event)" --%>
                      &nbsp;<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" /> 
                      <!-- 如果 不需要手工输入代码，可以在 后面增加一个 onfocus="diswrite(this);" -->
                      <%--<img  src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="infoFieldList["+index+"].viewvalue"%>");'/>&nbsp;--%>
                      <%-- 使用代码组件控件兼容非IE浏览器 wangb 20180206 bug 34544--%>
                     <logic:equal name="selfInfoForm" property="setname" value="${selfInfoForm.part_setid}">
					 	<logic:equal name="element" property="itemid" value="${selfInfoForm.part_unit}">
                       		<img src="/images/code.gif"  align="absmiddle" style="vertical-align:middle" id="infoFieldList<%=index %>" onlySelectCodeset="false" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
						</logic:equal>
						<logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_unit}">
						     <logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_pos}">
                       			<img src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
							 </logic:notEqual>
							 <logic:equal name="element" property="itemid" value="${selfInfoForm.part_pos}">
                       			<img src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
							 </logic:equal>
						</logic:notEqual>
					</logic:equal>
					<logic:notEqual name="selfInfoForm" property="setname" value="${selfInfoForm.part_setid}">
                     <img src="/images/code.gif" align="absmiddle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
                    </logic:notEqual>
                    </logic:notEqual>
                </logic:notEqual>
               </logic:notEqual>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
          			 </logic:equal>   &nbsp;                 
               </td>
         <%if(flag==0){%>
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:notEqual>
               
   </logic:equal>   
   </logic:notEqual>
</logic:iterate>  
<logic:equal value="1" name="selfInfoForm" property="mainsort">
	</table>
		</div>
		</td></tr>
</logic:equal>
 <tr>
  <td align="center" class="trShallow1" nowrap colspan="4">
  	<logic:equal value="1" name="selfInfoForm" property="isDraft">
	<hrms:priv func_id="26011202">
		<input type="button" name="apsssss" class="mybutton" onclick='provesDraft();' value='<bean:message key="button.appeal"/>'/>&nbsp;
	</hrms:priv>
	</logic:equal>
	<logic:notEqual value="1" name="selfInfoForm" property="isDraft">
	<hrms:priv func_id="26011202">
		<input type="button" name="apsssss" class="mybutton" onclick='proves();' value='<bean:message key="button.appeal"/>'/> &nbsp;
	</hrms:priv>	
	</logic:notEqual>
    <logic:equal value="1" name="selfInfoForm" property="isDraft">
    	<input type="button" name="appSaves" class="mybutton" onclick="writeablessDraft()" value='<bean:message key="button.save"/>'/> 
    </logic:equal>
    <logic:notEqual value="1" name="selfInfoForm" property="isDraft">
    	<input type="button" name="appSaves" class="mybutton" onclick="writeabless()" value='<bean:message key="button.save"/>'/> 
    </logic:notEqual>
    &nbsp;
    <input type="button" name="return" class="mybutton" onclick="approveReturn('${selfInfoForm.a0100 }')" value='<bean:message key="button.return"/>'/> 
  </td>
 </tr>    
 </table> 
 <%}else{%>
 <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <br>
 <br>
 <tr>  
     <td align="center"  nowrap>
        <bean:message key="workbench.info.nomainfield"/>
     </td>
 </tr>    
 </table>  
 <%}%>
 <div id="date_panel">
	<select name="date_box" multiple="multiple" size="10"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
		<option value="1992">1992</option>	
		<option value="1992.4">1992.04</option>
		<option value="1992.4.12">1992.04.12</option>		    
	</select>
</div>
</html:form>
<script language="javascript">
Element.hide('date_panel');
   getfirstfocuse();
   function getfirstfocuse(){
   var objsss=document.getElementsByTagName("input");
   
   for(var i=0;i<objsss.length;i++){
   var dobj=objsss[i];
   if(dobj.type=="text"  && dobj.getAttribute("extra")==null){
   	 dobj.focus();
   	 return;
   }
   }   
   }
</script>

<script language="JavaScript" src="/performance/workdiary/workdiary.js"></script>
<div id=dict style="border-style:nono">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">     
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)" onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>
<script language="javascript">
var code_desc; 
function addDict(code,obj)
{
  Element.hide('dict');
  var value=obj.value;  
  if(value=="")
   return false;
  var dmobj;
  var vos= document.getElementsByName('dict_box');
  var dict_vo=vos[0];
  var isC=true;
  code_desc=obj;
  for(var i=dict_vo.options.length-1;i>=0;i--)
  {
      dict_vo.options.remove(i);
  }
   var no = new Option();
   no.value="";
   no.text="";
   dict_vo.options[0]=no;
   var r=1;      
   for(var i=0;i<g_dm.length;i++)
   {
		dmobj=g_dm[i];	
		if(code=="UM"||code=="@K"||code=="UN")
		{
		    var vos;
		    if(code=="UM")
		      vos= document.getElementById('b0110');
		    else if(code=="@K")
		      vos= document.getElementById('e0122');
		    var code_value="<%=codevalue%>";
		    if(vos)
		    {
		       var b_value=vos.value;
		       if(b_value==null||b_value=="")
		       {
		          b_value=""
		       }
		       if(code_value!=""&&code_value.length<=dmobj.ID.substring(2).length)
		       {
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		              if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                 no.text=dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
		              }
		           }
		       }else
		       {
		          if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		          {
		            var no = new Option();
    	            no.value=dmobj.ID;
    	            no.text=dmobj.V;
		            dict_vo.options[r]=no;
			       r++;
		          }
		       }
		       
		    }else
		    {
		       if(code_value!=""&&code_value.length>=dmobj.ID.substring(2).length)
		       {
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		              if(code_value.indexOf(dmobj.ID.substring(2))==0)
		              {
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                 no.text=dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
		              }
		           }
		       }else
		       {
		              if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		              {
		               var no = new Option();
    	               no.value=dmobj.ID;
    	               no.text=dmobj.V;
		               dict_vo.options[r]=no;
			           r++;
		             }
		       }
		    }
		    
		}else
		{	 
		  if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0))
		  {
		    
		    var no = new Option();
    	    no.value=dmobj.ID;
    	    no.text=dmobj.V;
		    dict_vo.options[r]=no;		    
			r++;
		  }
	    }
   }    
   if(r==1)
   {
      obj.value="";
      Element.hide('dict'); 
      return false;      
   }   
   Element.show('dict');  
   var pos=getAbsPosition(obj);  
   with($('dict'))
   {
	   style.position="absolute";
       style.posLeft=pos[0]-1;
 	   style.posTop=pos[1]-1+obj.offsetHeight;
	   style.width=(obj.offsetWidth<150)?150:obj.offsetWidth+1;
   }  
}
 function setSelectCodeValue()
   {
     if(code_desc)
     {
        var vos= document.getElementsByName('dict_box');
        var dict_vo=vos[0];
        var isC=true;
        for(var i=0;i<dict_vo.options.length;i++)
        {
          if(dict_vo.options[i].selected)
          {
            code_desc.value=dict_vo[i].text;
            var code_name=code_desc.name;
            if(code_name!="")
            {
               var code_viewname=code_name.substring(0,code_name.indexOf("."));
               var view_vos= document.getElementsByName(code_viewname+".value");
               var view_vo=view_vos[0];               
               if(dict_vo[i].value!=null)          
                 view_vo.value=dict_vo[i].value.substring(2);
               view_vo.fireEvent("onchange");
             }
          }
        }
        Element.hide('dict');   
        event.srcElement.releaseCapture(); 
     }
  }
  function inputType(obj,event)
  {
     var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
     if(keyCode==13)
     {
       setSelectCodeValue();
     }
     
  }
  function inputType2(obj,event)
  {
    var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;
    if(keyCode == 40)
    {
       var vos= document.getElementsByName('dict');
       var vos1=vos[0];       
       if(vos1.style.display!="none")
       {
          var vos= document.getElementsByName('dict_box');
          var dict_vo=vos[0];          
          dict_vo.focus(); 
       }
    }
  }
  function styleDisplay(obj)
  {
     var obj_name=obj.name;
     if(code_desc)
     {
        var code_name=code_desc.name;
        if(code_name!=obj_name)
        {
          Element.hide('dict');
        }
     }
  }
   function checkDict(code,obj)
  {
    var code_name=obj.name;
    var code_viewname=code_name.substring(0,code_name.indexOf("."));
    var view_vos= document.getElementsByName(code_viewname+".value");
    var view_vo=view_vos[0];  
    if(view_vo==null||view_vo=="")
    {
      obj.value="";
      return false;
    }
    var isC=false;
    for(var i=0;i<g_dm.length;i++)
    {
		dmobj=g_dm[i];		 
		if(dmobj.ID==(code+view_vo))
		{
		    isC=true;
		    break;
		}
   } 
   if(!isC)
   {
      obj.value="";
      return false;
   } 
}
Element.hide('dict');
function appEdite() {
	selfInfoForm.action="/selfservice/selfinfo/appEditselfinfo.do?b_edit=link&isAppEdite=1";
    selfInfoForm.submit();
}
function appSave() {
	selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01";
	selfInfoForm.target="mil_body";
    selfInfoForm.submit();
}

function breturn() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_return=link&isAppEdite=1";
	selfInfoForm.target="mil_body";
    selfInfoForm.submit();
}

function appReturn() {
	selfInfoForm.action = "/selfservice/selfinfo/editselfinfo.do?b_edit=edit&i9999=I9999&actiontype=update&setname=A01";
	selfInfoForm.target="mil_body";
    selfInfoForm.submit();
}
function writeablessDraft() {
	if(validate()) {
		selfInfoForm.action = "/workbench/browse/appeditnotselfinfo.do?b_save=save&isAppEdite=1";
		selfInfoForm.target="mil_body";
	    selfInfoForm.submit();
    }
}
function provesDraft() {
	if(validate()) {
		selfInfoForm.action = "/workbench/browse/appeditnotselfinfo.do?b_save=approve&isAppEdite=1";
		selfInfoForm.target="mil_body";
	    selfInfoForm.submit();
    }
}
function showinfo(divId) {
	var fObj=document.getElementById(divId);
	if(fObj.style.display=='none') {
		fObj.style.display='block';
	}else {
		fObj.style.display='none';
	}
}
</script> 
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
   initDocument();
</script> 
<script language="JavaScript" src="/js/wz_tooltip.js"></script>  