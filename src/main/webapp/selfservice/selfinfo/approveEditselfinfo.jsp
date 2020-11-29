<%@page import="com.hrms.hjsj.sys.FieldItem"%>
<%@page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@page import="com.hrms.hjsj.sys.ConstantParamter"%>
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
	String isAll="";
	if(userView != null){
	   css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  if(!userView.isSuper_admin())
	  { 
	    code=userView.getManagePrivCode();
	    codevalue=userView.getManagePrivCodeValue();
	    if("UN".equalsIgnoreCase(code)&&(codevalue==null||codevalue.length()==0)){
	               	isAll="all";
	               }
	             }else{
	             	isAll="all";
	             }  
	  } 
	   
	   String fflag = request.getParameter("flag");
	   if(!"notself".equals(fflag)){
	   	isAll="all";
	   }
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript" src="/selfservice/selfinfo/approveEditselfinfo.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
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
  RecordVo vo=ConstantParamter.getConstantVo("SS_EMAIL");
  String mailField=vo.getString("str_value");
  FieldItem item=DataDictionary.getFieldItem(mailField,"A01");
  String mailDesc="";
  if(item!=null){
	  mailDesc=item.getItemdesc();
  }
  mailField=mailField.toLowerCase();
  %>
<script language="javascript">
   var date_desc;    
   function checkLength(itemdesc, object, length,itemid) {
	   var value = "";
	   if(object)
		   value = object.value;
	   var mailField='<%=mailField%>';
  	   if(value&&value.length>0&&mailField==itemid){
  		 var reg=new RegExp(/^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/);
  		 var flag=reg.test(value);
  		 if(!flag){
  			 alert("<%=mailDesc%>"+"格式不正确!");
  			 return;
  		 }
	   }
	   
	   if(IsOverStrLength(value,length)){
		   var msg = ITEMVALUE_MORE_LENGTH;
		   msg = msg.replace("{0}", length).replace("{1}", length/2);
           alert(itemdesc + msg);
           return;
       }
   }
</script>
<script language="javascript">
  function validate()
  {
	  if(!checkIDCard())
		  return false;
	  
    var tag=true;    
     <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
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
          
             <logic:equal name="element" property="itemtype" value="A">
             <logic:equal name="element" property="codesetid" value="0">
             var itemvalue=document.getElementsByName("<%="infoFieldList["+index+"].value"%>")[0].value;
             var mailField='<%=mailField%>';
             if("${element.itemid}"==mailField){//校验邮箱格式是否正确
            	 if(itemvalue&&itemvalue.length>0){
	            	 var reg=new RegExp(/^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/);
		      		 var flag=reg.test(itemvalue);
		      		 if(!flag){
		      			 alert("<%=mailDesc%>"+"格式不正确!");
		      			 return;
		      		 }
            	 }
             }
             if(IsOverStrLength(itemvalue,'${element.itemlength}')){
                 var msg = ITEMVALUE_MORE_LENGTH;
                 msg = msg.replace("{0}", '${element.itemlength}').replace("{1}", parseInt(${element.itemlength}/2));
                 alert('${element.itemdesc}' + msg);
                 return false;
             }
            </logic:equal>
            </logic:equal>
            
        <logic:equal name="element" property="itemtype" value="D">   
          var desc='${selfInfoForm.infoFieldList[index].itemdesc}';
          tag= checkDate(dobj,desc) && tag;      
	  if(tag==false)
	  {
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
             var dobj=valueInputs[0];
             var desc='${selfInfoForm.infoFieldList[index].itemdesc}'+":";
              tag=checkNUM1(dobj,desc) &&  tag ;  
	      if(tag==false)
	      {
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName('<%="infoFieldList["+index+"].value"%>');
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
	      {
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
      </logic:iterate>    
      if(tag == undefined)
    	  tag = true;
      
     return tag;   
  }
 //主集切换前校验内容是否发生改变 
 function dataIsChange(){
	 var flag=false;
 	 <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
		<logic:notEqual value="#####" name="element" property="itemid">
	 	   var oldValue='${selfInfoForm.infoFieldList[index].value}';
	 	   var obj=document.getElementsByName("<%="infoFieldList["+index+"].value"%>")[0];
	 	   if(obj.value!==oldValue){
	 		  return true;
	 	   }
		</logic:notEqual>
 	 </logic:iterate>
 	 return flag
 }
</script>
<script type="text/javascript" src="/js/dict.js"></script>
<%
	int i=0;
	int flag=0;
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<style>
.AddTableRow {
	valign:middle;
	padding:0 5px 0 5px;
}
</style>
<html:form action="/selfservice/selfinfo/editselfinfo" >

	<logic:equal name="selfInfoForm" property="isAppEdite" value="1">
		<logic:equal name="selfInfoForm" property="setprv" value="2">
			<div style="margin-left:0px;margin-bottom:15px;">
				<bean:message key="selfinfo.listinfo"/>
					<select id="list" name="fieldsetid" onchange="change();">
						<logic:iterate id="setList" name="selfInfoForm" property="infoSetList">
							<logic:equal name="setList" property="priv_status" value="2">
								
									<logic:equal value="${setList.fieldsetid }" name="selfInfoForm" property="setname">
										<option value="${setList.fieldsetid }" selected="selected">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:equal>
									<logic:notEqual value="${setList.fieldsetid }" name="selfInfoForm" property="setname">
										<option value="${setList.fieldsetid }">
											<bean:write name="setList" property="customdesc"/>
										</option>
									</logic:notEqual>
								
							</logic:equal>
						</logic:iterate>
					</select>
			</div>
		</logic:equal>
	</logic:equal>

<script language="javascript">  
    function getchangeposun(outparamters)
    {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
      AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);
      document.getElementById("deptId").setAttribute("parentid",deptparentcode);
      document.getElementById("jobId").setAttribute("parentid",deptparentcode);
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
         document.getElementById("deptId").setAttribute("parentid",orgvalue);
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
      AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
      AjaxBind.bind(selfInfoForm.deptparentcode, deptparentcode);
      AjaxBind.bind(selfInfoForm.posparentcode, posparentcode);
  
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
      AjaxBind.bind(selfInfoForm.orgparentcode, orgparentcode);
  }
   
function changeOrg (pretype) {
   var value = "";
   var unIdInputs = document.getElementsByName("<%=orgtempview%>");
   var valueInputsun = document.getElementsByName("<%=orgtemp%>");
   if('b0110' == pretype) {
       if(unIdInputs != null && unIdInputs != "undefined" && unIdInputs.length > 0)
           value = unIdInputs[0].value;
       
       if(valueInputsun != null && valueInputsun != "undefined" && valueInputsun.length > 0) {
           if(!value) {
               valueInputsun[0].value = "";
               document.getElementById("deptId").setAttribute("parentid","");
               document.getElementById("jobId").setAttribute("parentid","");
               changepos("2");
           }
       }
       
   } else if('e0122' == pretype) {
       var umIdInputs = document.getElementsByName("<%=postempview%>");
       var valueInputsum = document.getElementsByName("<%=postemp%>");
       if(umIdInputs != null && umIdInputs != "undefined" && umIdInputs.length > 0)
           value = umIdInputs[0].value;
       
       if(valueInputsum != null && valueInputsum != "undefined" && valueInputsum.length > 0) {
           if(!value) {
               valueInputsum[0].value = "";
        	   if(valueInputsun && valueInputsun.length > 0 && valueInputsun[0].value)
        		    document.getElementById("jobId").setAttribute("parentid",valueInputsun[0].value);
        	   else
        		    document.getElementById("jobId").setAttribute("parentid","");
        	   
               changepos("1");
           }
       }
       
   } else if('e01a1' == pretype) {
       var kkIdInputs = document.getElementsByName("<%=kktempview%>");
       var valueInputskk = document.getElementsByName("<%=kktemp%>");
       if(kkIdInputs != null && kkIdInputs != "undefined" && kkIdInputs.length > 0)
           value = kkIdInputs[0].value;
       
       if(valueInputskk != null && valueInputskk != "undefined" && valueInputskk.length > 0) {
           if(!value) {
               valueInputskk[0].value = "";
           }
       }
   }
}
   
  function changepos(pretype)
  {   
  	  if('0'==pretype){
  	  	pretype='@K';
  	  }
  	  if('1'==pretype){
  	  	pretype='UM';
  	  }
  	  if('2'==pretype){
  	  	pretype='UN';
  	  }
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
          if(dobj!=null)
         dobj.value=axvalue;
         if(axvalue==1)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            if(dobj!=null)
            dobj.value=MAN_PERSON;
         }else if(axvalue==2)
         {
            var valueInputs=document.getElementsByName("<%=axviewfield%>");
            dobj=valueInputs[0];
            if(dobj!=null)
            dobj.value=WOMAN_PERSON;
         }
     }        
     
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
  
  function approveall() {
		if (validate()) {
			if (confirm("您确定要整体报批吗？整体报批后个人信息将不能修改！")) {
				selfInfoForm.action = "/selfservice/selfinfo/appEditselfinfo.do?b_approveall=link&savEdit=appbaopi&a0100=${selfInfoForm.a0100}&userbase=${selfInfoForm.userbase}";
				selfInfoForm.target = "mil_body";
				selfInfoForm.submit();
			}
		}
}

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

function checkdata(){
	if(confirm(APP_DATA_NOTUPDATE+"?")){
	var dbname='<%=userView.getDbname()%>';
	var pars="a0100=${selfInfoForm.a0100}&pdbflag="+dbname;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appeal,functionId:'0201001099'});
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

<DIV id="overDiv" class="RecordRow" style="POSITION: absolute; Z-INDEX: 0;BORDER:0;overflow:visible;background-image:/images/mainbg.jpg"></DIV>
 <% SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
 		rowss=new Integer(selfInfoForm.getRownums()).intValue();
 		
      if(selfInfoForm.getInfoFieldList().size()>0){ %>  
<table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">

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
<hrms:itemmemo id="itemmemo" img="image" setname="${selfInfoForm.setname}" itemid="${element.itemid}"></hrms:itemmemo>
   <logic:notEqual name="element" property="itemtype" value="M">
   <%if(flag==0){
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

    <td align="right" nowrap valign="middle" class="AddTableRow" ${itemmemo}>            
    ${image}
    <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>  
    </td>
    </logic:notEqual>
   <logic:equal name="element" property="priv_status" value="1"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
        
         <td align="left"  nowrap valign="middle" class="AddTableRow">
             <html:text   name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'  onclick="Element.hide('dict');" styleClass="textColorRead" readonly="true" maxlength="${element.itemlength}" />
            <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>                
         </td> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
         <td align="left"  nowrap valign="middle" class="AddTableRow">
            <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" maxlength="${element.itemlength}" />
            <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>              
         </td>
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
               <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth}" />
               <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>                 
           </td>
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
              <td align="right" nowrap valign="middle" class="AddTableRow" ${itemmemo}>            
             
            <logic:equal name="element"  property="fillable" value="false">
              ${image}
              <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>   
            </logic:equal>        
                 </td>
                 <td align="left" nowrap valign="middle" style="padding-top:1px;padding-bottom:2px;" class="AddTableRow" colspan="3">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onclick="Element.hide('dict');" rows="10"  cols="66" style="width:550px;height:100px;" styleClass="textColorRead"/>
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
              <td align="right" nowrap valign="middle" class="AddTableRow" ${itemmemo}>            
              ${image}
                 <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>
               
                </td>
                <td align="left" nowrap valign="middle" style="padding-top:1px;padding-bottom:2px;" class="AddTableRow" colspan="3">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" rows="10" readonly="true" cols="66" style="width:550px;height:100px;" styleClass="textColorRead"/>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
           			</logic:equal>
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
           <logic:equal name="element"  property="itemid" value="b0110">
                 <html:hidden name="selfInfoForm"  styleId="b0110" property='<%="infoFieldList["+index+"].value"%>'/>  
                 </logic:equal>
                 <logic:equal name="element"  property="itemid" value="e0122">
                 <html:hidden name="selfInfoForm"  styleId="e0122" property='<%="infoFieldList["+index+"].value"%>'/>  
                 </logic:equal>
                 <logic:notEqual name="element"  property="itemid" value="b0110">
                 <logic:notEqual name="element"  property="itemid" value="e0122">
                 <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'/>  
                 </logic:notEqual>
                 </logic:notEqual>
                    <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" /> 
            	<logic:equal name="element"  property="fillable" value="true">
            		<font color="red">*</font>
          		 </logic:equal>                    
           </td>
      </logic:notEqual>
   </logic:equal>  

   <logic:equal name="element" property="priv_status" value="2"> 
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
         <td align="left"  nowrap valign="middle" class="AddTableRow">
           <logic:notEmpty name="selfInfoForm" property="workdatefield">
				<logic:equal name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                	<input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"  extra="editor" Class="textColorWrite" maxlength="${element.itemlength}"  itemlength="${element.itemlength}" autocomplete="off" dataType="simpledate"  dropDown="dropDownDate"  onblur="CalculateWorkDate(this)"/>
				</logic:equal>
				<logic:notEqual name="selfInfoForm" property="workdatefield" value="${element.itemid}">
					<logic:notEmpty name="selfInfoForm" property="startpostfield">
						<logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                           <input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" Class="textColorWrite" maxlength="${element.itemlength}" itemlength="${element.itemlength}" autocomplete="off" dataType="simpledate"  dropDown="dropDownDate" onblur="CalculatePostAge(this)" />
						</logic:equal>
						<logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                        	<input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" Class="textColorWrite" maxlength="${element.itemlength}" itemlength="${element.itemlength}" autocomplete="off" dataType="simpledate"  dropDown="dropDownDate" onblur="checkDate(this);Element.hide('date_panel');"  />
						</logic:notEqual>
					</logic:notEmpty>
					<logic:empty name="selfInfoForm" property="startpostfield">
                    	<input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" Class="textColorWrite" maxlength="${element.itemlength}" itemlength="${element.itemlength}" autocomplete="off" dataType="simpledate"  dropDown="dropDownDate" onblur="checkDate(this);Element.hide('date_panel');"  />
					</logic:empty>
				</logic:notEqual>
			</logic:notEmpty>
			<logic:empty name="selfInfoForm" property="workdatefield">
				<logic:notEmpty name="selfInfoForm" property="startpostfield">
					<logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                    	<input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" Class="textColorWrite" maxlength="${element.itemlength}" itemlength="${element.itemlength}" dataType="simpledate" autocomplete="off"  dropDown="dropDownDate" onblur="CalculatePostAge(this)"  />
                 	</logic:equal>
					<logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                  		<input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" Class="textColorWrite" maxlength="${element.itemlength}" itemlength="${element.itemlength}" dataType="simpledate" autocomplete="off"  dropDown="dropDownDate" onblur="checkDate(this);Element.hide('date_panel');"  />
					</logic:notEqual>
				</logic:notEmpty>
				<logic:empty name="selfInfoForm" property="startpostfield">
                	<input type="text"  name='<%="infoFieldList["+index+"].value"%>' onchange="timeCheck(this,'${element.itemdesc}')" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" extra="editor" Class="textColorWrite" maxlength="${element.itemlength}" itemlength="${element.itemlength}" dataType="simpledate" autocomplete="off"  dropDown="dropDownDate" onblur="checkDate(this);Element.hide('date_panel');"  />
				</logic:empty>
			</logic:empty>
			<logic:equal name="element" property="fillable" value="true">
				<font color="red">*</font>
			</logic:equal>
         </td> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
         <td align="left"  nowrap valign="middle" class="AddTableRow">
           <logic:notEmpty name="selfInfoForm" property="idcardfield">
			<logic:equal name="selfInfoForm" property="idcardfield" value="${element.itemid}">
            	<html:hidden styleId="idcardDesc" property="itemdesc" name="element"/>
				<html:hidden name="element" property="fillable" styleId="idcardflag"/>
                <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="idcard" onclick="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="calculatebirthday(this)" />
			</logic:equal>
			<logic:notEqual name="selfInfoForm" property="idcardfield" value="${element.itemid}">
                   <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" onchange="checkLength('${element.itemdesc}',this,'${element.itemlength}','${element.itemid}');" styleClass="textColorWrite" maxlength="${element.itemlength}" />
			</logic:notEqual>
		  </logic:notEmpty>
		  <logic:empty name="selfInfoForm" property="idcardfield">
                 <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');" onchange="checkLength('${element.itemdesc}',this,'${element.itemlength}','${element.itemid}');" styleClass="textColorWrite" maxlength="${element.itemlength}" />
		  </logic:empty>
		 <logic:equal name="element" property="fillable" value="true">
			<font color="red">*</font>
		</logic:equal>
         </td>
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
           <td align="left"  nowrap valign="middle" class="AddTableRow">
             <logic:equal name="element"  property="decimalwidth" value="">
               <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth}" onchange="checkValue(this,'${element.itemlength}','${element.decimalwidth}','${element.itemdesc}');"/>   
             </logic:equal>
             <logic:notEqual name="element"  property="decimalwidth" value="">
               <html:text  name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth+1}" onchange="checkValue(this,'${element.itemlength}','${element.decimalwidth}','${element.itemdesc}');"/>   
             </logic:notEqual>
            <logic:equal name="element"  property="fillable" value="true">
            <font color="red">*</font>
           </logic:equal>                  
           </td>
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
              <td align="right" nowrap valign="middle" class="AddTableRow" ${itemmemo}>            
              ${image}
                  <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>         
                 </td>
                 <td align="left" style="padding-top:1px;padding-bottom:2px;" nowrap valign="middle" class="AddTableRow" colspan="3">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  rows="10"  cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
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
              <td align="right" nowrap valign="middle" class="AddTableRow" ${itemmemo}>            
              ${image}
                    <hrms:textnewline text="${element.itemdesc}" len="10"></hrms:textnewline>      
                </td>
                <td align="left" style="padding-top:1px;padding-bottom:2px;" nowrap valign="middle" class="AddTableRow" colspan="3">
                  <html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onclick="Element.hide('dict');"  rows="10"  cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
          			 </logic:equal>                       
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
	     <td align="left"  nowrap valign="middle" class="AddTableRow">
                <logic:equal name="element" property="itemid" value="b0110">
                   <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="b0110"  onchange="changepos('${element.codesetid}')"/>  
                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>'  onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('b0110');" /> 
                     <img src="/images/code.gif" align="absmiddle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="UN" nmodule='4' ctrltype='3' inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>" afterfunc="changeLowerLevel('<%="infoFieldList["+index+"].value"%>','deptId');changepos('2');"/>
                 </logic:equal>
                <logic:equal name="element" property="itemid" value="e0122">
                    <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e0122"  onchange="changepos('${element.codesetid}')"/>  
                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e0122');"/> 
                     <img src="/images/code.gif" align="absmiddle" id="deptId" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" nmodule='4' ctrltype='3' inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>" afterfunc="changeLowerLevel('<%="infoFieldList["+index+"].value"%>','jobId');changepos('1');"/>
                </logic:equal>
                 <logic:equal name="element" property="itemid" value="e01a1">
                   <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e01a1"  onchange="changepos('${element.codesetid}')"/>  
                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e01a1');" /> 
                     <img src="/images/code.gif" align="absmiddle" id="jobId" onlySelectCodeset="true" plugin="codeselector" codesetid="@K" nmodule='4' ctrltype='3' inputname='<%="infoFieldList["+index+"].viewvalue"%>' afterfunc="changepos('0');" valuename="<%="infoFieldList["+index+"].value"%>"/>
                </logic:equal>
                   <script>
						
				   </script>
				
               <logic:notEqual name="element" property="itemid" value="b0110">
                <logic:notEqual name="element" property="itemid" value="e0122">
                    <logic:notEqual name="element" property="itemid" value="e01a1">
                      <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'/> 
                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' onclick="styleDisplay(this);" styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" /> 
                     <img src="/images/code.gif" align="absmiddle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
                    </logic:notEqual>
                </logic:notEqual>
               </logic:notEqual>
            		<logic:equal name="element"  property="fillable" value="true">
            			<font color="red">*</font>
          			 </logic:equal>                    
               </td>
      </logic:notEqual>
   </logic:equal>   
    <logic:notEqual name="element" property="itemtype" value="M">
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
</logic:notEqual>
</logic:iterate>  
<logic:equal value="1" name="selfInfoForm" property="mainsort">
	</table>
		</div>
		</td></tr>
</logic:equal>
 <tr>
  <td align="center" class="trShallow1" nowrap colspan="4">
    <logic:equal value="1" name="selfInfoForm" property="approveflag">
    	<logic:equal value="1" name="selfInfoForm" property="inputchinfor">
    		<logic:equal value="A01" name="selfInfoForm" property="setname">
    			<logic:equal value="1" name="selfInfoForm" property="isAble">
    			<hrms:priv func_id="01030115">
    			<input type="button" class="mybutton" name="zheng" value="整体报批" onclick="approveall();"/>
    			</hrms:priv>
    			</logic:equal>
    		</logic:equal>
    	</logic:equal>
    </logic:equal>
    <logic:notEqual value="1" name="selfInfoForm" property="approveflag">
       <logic:equal name="selfInfoForm" property="setname" value="A01">
          <logic:equal name="selfInfoForm" property="setprv" value="2"> 
          	<logic:equal value="1" name="selfInfoForm" property="isAble">
             <button name="sf" class="mybutton" onclick="writeable();"><bean:message key="button.save"/></button>
          	</logic:equal>
          </logic:equal>
       </logic:equal>
       <logic:notEqual name="selfInfoForm" property="setname" value="A01">
          <logic:equal name="selfInfoForm" property="setprv" value="2"> 
            <button name="v_saves" class="mybutton" onclick="saves()"><bean:message key="button.save"/></button>
          </logic:equal>
          <logic:equal name="selfInfoForm" property="actiontype" value="new">
          	<button name="v_saves" class="mybutton" onclick="savesre()"><bean:message key="button.savereturn"/>
          </logic:equal>	
       </logic:notEqual>
    </logic:notEqual>
    <logic:equal value="1" name="selfInfoForm" property="approveflag">
    <logic:equal value="1" name="selfInfoForm" property="viewbutton">
	      <hrms:user_state name="sss" userid="<%=userView.getUserId()%>" dbname="<%=userView.getDbname()%>" tablename="${selfInfoForm.setname}"></hrms:user_state>
	      <bean:define id="sid" name="selfInfoForm" property="setname"/>
	      <SCRIPT language="javascript">
	      function multimediahref(){
		    		var thecodeurl =""; 
		    		var return_vo=null;
		    		var setname = "A01";
		    		var a0100 = '${selfInfoForm.a0100}';
		    		var dbname = '${selfInfoForm.userbase}';
		    		var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		    	  	thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&dbflag=A&canedit=selfedit&setid="+setname+
		    	  			"&a0100="+a0100+"&nbase="+dbname+"&keyvalue="+a0100+"&sequence=1";
                    thecodeurl = thecodeurl.replace(/&/g,"`");
		    	 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);//update by xiegh bug35564
		    	  	if(getBrowseVersion()){
			    	  	return_vo= window.showModalDialog(iframe_url, "", 
			    	  	"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
		    	  	}else{
		    	  		var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
		    			var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
		    			window.open(iframe_url,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+dl+",top="+dt);
		    	  	}
		  }
	      var flag=selfInfoForm.sss.value;
	      var inputchinfor="${selfInfoForm.inputchinfor}";
	      
	      if(flag=='0'||flag=='2'||flag==''||flag=='5'||inputchinfor=='1'){
	      
	      document.write('<logic:equal name="selfInfoForm" property="setprv" value="2">'+
	      '<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	     
	      '		<logic:notEqual name="selfInfoForm" property="isAppEdite" value="1">'+
	      '<input type="button"  name="sf" onclick="appEdite();" class="mybutton" value="<bean:message key="selfinfo.defend"/>" />'+
            ' 	</logic:notEqual>'+
         '		<logic:equal name="selfInfoForm" property="isAppEdite" value="1">'+
           
            '<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
            '<logic:equal value="1" name="selfInfoForm" property="isAble">'+
             '<input type="button"  name="appSaves" onclick="writeabless();" class="mybutton" value="<bean:message key="button.save"/>" />'+
	       '<hrms:priv func_id="01030104">'+'&nbsp<input type="button"  name="apde" onclick="prove();" class="mybutton" value="<bean:message key="button.appeal"/>" />'+
	      '</hrms:priv>'+
	      '<logic:equal value="1" name="selfInfoForm" property="multimedia_file_flag">'+
	      '<input type="button"  onclick="multimediahref();" class="mybutton" value="<bean:message key="conlumn.resource_list.name"/>" />'+//update by xiegh bug36304
	      '</logic:equal>'+
	      '</logic:equal>'+
	      '</logic:equal>'+
            '	</logic:equal>'+
         '</logic:equal>'+
            
	      '</logic:equal>'+

	      '<logic:equal name="selfInfoForm" property="setprv" value="3">'+
      '<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	     
	      '		<logic:notEqual name="selfInfoForm" property="isAppEdite" value="1">'+
	      '<input type="button"  name="sf" onclick="appEdite();" class="mybutton" value="<bean:message key="selfinfo.defend"/>" />'+
            ' 	</logic:notEqual>'+
         '		<logic:equal name="selfInfoForm" property="isAppEdite" value="1">'+
           
            '<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
            '<logic:equal value="1" name="selfInfoForm" property="isAble">'+
             '<input type="button"  name="appSaves" onclick="writeabless();" class="mybutton" value="<bean:message key="button.save"/>" />'+
	       '<hrms:priv func_id="01030104">'+'&nbsp<button name="apde" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>'+
	      
	      '</hrms:priv>'+
	      '</logic:equal>'+
	      '</logic:equal>'+
            '	</logic:equal>'+
         '</logic:equal>'+
          '</logic:equal>'+
	     '<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
	     '<logic:equal value="0" name="selfInfoForm" property="inputchinfor">'+
	      '<hrms:priv func_id="01030115">'+
	      '<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	      ' <button name="allok" class="mybutton" onclick="checkdata()">'+APP_ALL+'</button> '+
	      '<input type="button"  name="allok" onclick="checkdata();" class="mybutton" value="'+APP_ALL+'" />'+
	      ' </logic:equal>'+
	      '</hrms:priv>'+
	      '</logic:equal>'+
	      '</logic:equal>'
	      
	      );
	      
	      }else{
	      if(flag==3){
	      
	     
	      document.write('<logic:equal name="selfInfoForm" property="setprv" value="2">'+
	      '<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
	      '&nbsp;<input type="button"  name="appsss" onclick="capp();" class="mybutton" value="'+PLEASE_APPLIC+'" />'+
	      '</logic:equal>'+
          '</logic:equal>'
	      
	      );
	      
	      }
	      
	      }
	      </SCRIPT>

	       <logic:notEqual name="selfInfoForm" property="setname" value="A01">
	       		<logic:equal value="1" name="selfInfoForm" property="approveflag">
	       	 		<hrms:priv func_id="01030104">
	      	 			<button name="apsssss" class="mybutton" onclick='proves();'>
	      					<bean:message key="button.appeal"/>
	      	   			</button>
 	        	 	</hrms:priv>
	      			<button name="v_saves" class="mybutton" onclick="saves()"><bean:message key="button.save"/></button>
	        	</logic:equal>
	        	<logic:notEqual value="1" name="selfInfoForm" property="approveflag">        
	        	  <logic:equal name="selfInfoForm" property="setprv" value="2"> 
	          		<button name="v_saves" class="mybutton" onclick="saves()"><bean:message key="button.save"/></button>
	        	  </logic:equal>
	        	</logic:notEqual>
	         <input class="mybutton" type="button" name="re" onclick="breturn()" value="<bean:message key="button.return"/>"/> 
	       </logic:notEqual>
	 </logic:equal>	
	 <logic:notEqual value="1" name="selfInfoForm" property="viewbutton">
	 <logic:notEqual name="selfInfoForm" property="setname" value="A01">
	 	<input class="mybutton" type="button" name="re" onclick="breturn()" value="<bean:message key="button.return"/>"/> 
	 </logic:notEqual> 
	 </logic:notEqual>    
    </logic:equal>
    <logic:equal name="selfInfoForm" property="inputchinfor" value="1">
		<logic:equal name="selfInfoForm" property="approveflag" value="1"> 
    		<logic:equal name="selfInfoForm" property="setname" value="A01">
    			<button name="return" class="mybutton" onclick="appReturn();"><bean:message key="button.return"/></button> 
    		</logic:equal>
    	</logic:equal>
    </logic:equal>
    
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
       var vos;
		    if(code=="UM")
		      vos= document.getElementById('b0110');
		    else if(code=="@K"){
		    	vos= document.getElementById('e0122');
		    	if(vos.value==''){
		    		vos= document.getElementById('b0110');
		    	}
		    }
		    var code_value="<%=codevalue%>";
   for(var i=0;i<g_dm.length;i++)
   {
		dmobj=g_dm[i];	
		if(code=="UM"||code=="@K"||code=="UN")
		{
		    if(vos)
		    {
		       var b_value=vos.value;		       
		       if(b_value==null||b_value=="")
		       {
		          b_value=""
		       }
		       if((code_value!=""||'<%=isAll %>'=='all')&&code_value.length<=dmobj.ID.substring(2).length)
		       {
		           
		           if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code+code_value)==0&&dmobj.ID.indexOf(code+b_value)==0)|| (dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		           {
		             if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		                  var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
     							var parentdesc=outparamters.getValue("parentdesc");
				                 var no = new Option();
		    	                 no.value=dmobj.ID;
		    	                 no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
				                 dict_vo.options[r]=no;
					             r++;
					          }
		              }
		           }
		       }else
		       {
		          if(code_value.length>0&&((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0)||(dmobj.ID.indexOf(code+value)==0)))
		          {
		          if(dmobj.ID.substring(2).indexOf(code_value)==0)
		              {
		              var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
 							 var parentdesc=outparamters.getValue("parentdesc");
		            var no = new Option();
    	            no.value=dmobj.ID;
    	            no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
		            dict_vo.options[r]=no;
			        r++;
			        }
			        }
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
		              var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
 							  var parentdesc=outparamters.getValue("parentdesc");
		                 var no = new Option();
    	                 no.value=dmobj.ID;
    	                no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
		                 dict_vo.options[r]=no;
			             r++;
			             }
		              }
		           }
		       }else
		       {
		              if((dmobj.V.indexOf(value)!=-1&&dmobj.ID.indexOf(code)==0&&dmobj.ID.indexOf(code+b_value)==0)||(dmobj.ID.indexOf(code+value)==0&&dmobj.ID.indexOf(code+b_value)==0))
		              {
		              var hashvo=new ParameterSet();
     					  hashvo.setValue("a_code",dmobj.ID);
     					  var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'10200770001'},hashvo);
							function getBirthdayAge(outparamters)
 							 {
 							  var parentdesc=outparamters.getValue("parentdesc");
		               var no = new Option();
    	               no.value=dmobj.ID;
    	                no.text=(parentdesc.length>0?parentdesc+"/":"")+dmobj.V;
		               dict_vo.options[r]=no;
			           r++;
			           }
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
Element.hide('dict');
initDate()
initDocument();
<logic:notEmpty name="selfInfoForm" property="formationMsg">
	  alert('<bean:write  name="selfInfoForm" property="formationMsg"/>');
	<%
		SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
		selfInfoForm.setFormationMsg("");
	%>

</logic:notEmpty> 
</script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>  
<script>
//设置form表单外边距  下拉框 超出边框下显示   wangb 20180207 bug 34462 
var form = document.getElementsByName('selfInfoForm')[0];
form.style.marginTop='10px';
form.style.marginLeft='10px';

var valueInputsun=document.getElementsByName("<%=orgtemp%>");
if(valueInputsun && valueInputsun.length>0)
    document.getElementById("deptId").setAttribute("parentid",valueInputsun[0].value);

var valueInputsum=document.getElementsByName("<%=postemp%>");
if(valueInputsum && valueInputsum.length>0){
    if(valueInputsum[0].value)	
        document.getElementById("jobId").setAttribute("parentid",valueInputsum[0].value);
    else
    	document.getElementById("jobId").setAttribute("parentid",valueInputsun[0].value);
}      
</script>