<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
	SelfInfoForm selfInfoForm=(SelfInfoForm)session.getAttribute("selfInfoForm");
	  String inputchinfor = selfInfoForm.getInputchinfor();
	  String approveflag = selfInfoForm.getApproveflag();
	 
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
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript"
	src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript"
	src="/components/extWidget/proxy/TransactionProxy.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script type="text/javascript" src="/selfservice/selfinfo/editselfinfo.js"></script>
<style>
input:disabled {
	border: 1px solid #DDD;
	background-color: #F5F5F5;
	color: #ACA899;
}
</style>
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
  int i=0;
  int flag=0;
  %>
<script language="javascript">
   var date_desc,code_desc,orgtemp="",orgtempview="",postemp="",postempview="",kktemp="",kktempview="",
   birthdayfield = "",agefield = "",workagefield = "",postagefield = "",axfield = "",axviewfield = "";
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
          
             <logic:equal name="element" property="itemtype" value="A">
             <logic:equal name="element" property="codesetid" value="0">
             var itemvalue=document.getElementsByName("<%="infoFieldList["+index+"].value"%>")[0].value;
             if(IsOverStrLength(itemvalue,'${element.itemlength}')){
                 var msg = ITEMVALUE_MORE_LENGTH;
                 msg = msg.replace("{0}", '${element.itemlength}').replace("{1}", parseInt(${element.itemlength}/2));
                 alert('${element.itemdesc}' + msg);
                 return false;
             }
            </logic:equal>
            </logic:equal>
        <logic:equal name="element" property="itemtype" value="D">   
          var d_value=dobj.value;
          d_value=replaceAll(d_value,"-",".");
          tag= validateDate(dobj,d_value) && tag;     
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
      if(tag != false)
    	  tag = true;
    
     return tag;   
  }
orgtemp = "<%=orgtemp%>";
orgtempview = "<%=orgtempview%>";
postemp = "<%=postemp%>";
postempview = "<%=postempview%>";
kktemp = "<%=kktemp%>";
kktempview = "<%=kktempview%>";
birthdayfield = "<%=birthdayfield%>";
agefield = "<%=agefield%>";
workagefield = "<%=workagefield%>";
postagefield = "<%=postagefield%>";
axfield = "<%=axfield%>";
axviewfield = "<%=axviewfield%>";

</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<style>
.AddTableRow {
	valign: middle;
	padding: 0 5px 0 5px;
}
</style>
<html:form action="/selfservice/selfinfo/editselfinfo">
	<script language="javascript">  
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
         var valueInputs=document.getElementsByName(workagefield);
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=workagevalue;
         <logic:equal name="selfInfoForm" property="workdatefield" value="${selfInfoForm.startpostfield}">
             valueInputs=document.getElementsByName(postagefield);
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
         var valueInputs=document.getElementsByName(postagefield);
         var dobj=valueInputs[0];
         if(dobj!=null){
         dobj.value=postagevalue;  
         <logic:equal name="selfInfoForm" property="startpostfield" value="${selfInfoForm.workdatefield}">
             valueInputs=document.getElementsByName(workagefield);
             dobj=valueInputs[0];
             dobj.value=workagevalue;
         </logic:equal>   
     }    
     } 
  }
  function proves(){
	  if(!checkIDCard())
		  return false;
	  
	  if(validate()){
		  if(confirm(APP_DATA_NOT_UPDATE+"?")){
			<%if((inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
			  selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_appappeals=link&isAppEdite=1";
			<%}else{%>
			  selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_appeals=link&isAppEdite=1";
			<%}%>
		      selfInfoForm.submit();
		  }else{
			  return;
		  }
	  }
  }
  function saves(){
	  if(!checkIDCard())
		  return false;
	  var savebtn = document.getElementsByName("v_saves")[0];
	  if(savebtn) savebtn.disabled=true;
	  if(validate()){
		<%if((inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
		  selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_appsaves=link&isAppEdite=1";
		<%} else {%>
	      selfInfoForm.action="/selfservice/selfinfo/editselfinfo.do?b_saves=link&isAppEdite=1";
		<%}%>
		  selfInfoForm.submit();
	  }else{
		  if(savebtn) 
			  savebtn.disabled=false;
		  return;
	  }
  }

function checkdata(){
	if(confirm(APP_DATA_NOTUPDATE+"?")){
	var dbname='<%=userView.getDbname()%>';
	var pars="selfinfo=1";
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appeal,functionId:'0201001099'});
	}
}
</script>
	<div id="overDiv" class="RecordRow"
		style="display: none; POSITION: absolute; Z-INDEX: 0; BORDER: 0; background-color: #FFFFCC; overflow: visible; background-image: /images/mainbg.jpg"></div>
	<% 
 		rowss=new Integer(selfInfoForm.getRownums()).intValue();
		String actiontype = selfInfoForm.getActiontype();
      if(selfInfoForm.getInfoFieldList().size()>0){ %>
	<table width="85%" border="0" cellspacing="1" align="center"
		cellpadding="1" class="ListTable">

		<html:hidden name="selfInfoForm" property="userbase" />
		<html:hidden name="selfInfoForm" property="actiontype" />
		<html:hidden name="selfInfoForm" property="a0100" />
		<html:hidden name="selfInfoForm" property="i9999" />
		<html:hidden name="selfInfoForm" property="setname" />
		<html:hidden name="selfInfoForm" property="orgparentcode" />
		<html:hidden name="selfInfoForm" property="deptparentcode" />
		<html:hidden name="selfInfoForm" property="posparentcode" />

		<logic:iterate id="element" name="selfInfoForm" property="infoFieldList" indexId="index">
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
			<% if(flag==0){
	             if(i%2==0){
            %>
					<tr class="trShallow1">
				<%} else {%>
					<tr class="trDeep1">
				<%}
             i++;
             flag=rowss;          
             }else{
               flag=0;           
             }%>
				<td align="right" nowrap valign="middle" class="AddTableRow" ${itemmemo}>
					${image} 
					<hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
					<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].itemid"%>' /> 
					<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].fieldsetid"%>' /> 
					<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].itemtype"%>' /> 
					<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].itemlength"%>' /> 
					<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].codesetid"%>' />
				</td>
			</logic:notEqual>
			<logic:equal name="element" property="priv_status" value="1">
				<logic:equal name="element" property="codesetid" value="0">
					<logic:equal name="element" property="itemtype" value="D">
							<td align="left" nowrap valign="middle" class="AddTableRow">
								<html:text name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									onclick="Element.hide('dict');" styleClass="textColorRead"
									readonly="true" maxlength="${element.itemlength}"/> 
								<logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="A">
							<td align="left" nowrap valign="middle" class="AddTableRow">
								<html:text name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									readonly="true" onclick="Element.hide('dict');"
									styleClass="textColorRead" maxlength="${element.itemlength}" />
								<logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="N">
							<td align="left" nowrap valign="middle" class="AddTableRow">
								<html:text name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									readonly="true" onclick="Element.hide('dict');"
									styleClass="textColorRead"
									maxlength="${element.itemlength + element.decimalwidth + 1}" /> 
								<logic:equal name="element" property="fillable" value="true">
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
							<td align="right" nowrap valign="middle" class="AddTableRow"
								${itemmemo}><logic:equal name="element" property="fillable"
									value="false">
              ${image}
              <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
								</logic:equal></td>
							<td align="left" nowrap valign="middle" class="AddTableRow" colspan="3"
								style="padding-top:1px;padding-bottom:2px;">
								<html:textarea name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									readonly="true" onclick="Element.hide('dict');" rows="10"
									cols="66" style="width:550px;height:100px;resize:none;"
									styleClass="textColorRead" /> <html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].fieldsetid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemtype"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemlength"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].codesetid"%>' /> <logic:equal
									name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal></td>
							<%       
             }else{
               flag=0;%>
							<td colspan="2"></td>
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
							<td align="right" nowrap valign="middle" class="AddTableRow"
								${itemmemo}>${image} <hrms:textnewline
									text="${element.itemdesc}" len="20"></hrms:textnewline>

							</td>
							<td align="left" nowrap valign="middle" class="AddTableRow"
								colspan="3"><html:textarea name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									onclick="Element.hide('dict');" rows="10" readonly="true"
									cols="66" style="width:550px;height:100px;resize:none;"
									styleClass="textColorRead" /> <html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].fieldsetid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemtype"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemlength"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].codesetid"%>' /> <logic:equal
									name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal></td>

							<%         
             }%>

							<%flag=0;%>

						</tr>
					</logic:equal>
				</logic:equal>
				<logic:notEqual name="element" property="codesetid" value="0">
						<td align="left" nowrap valign="middle" class="AddTableRow">
							<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' /> 
							<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>'
								readonly="true" onclick="Element.hide('dict');" styleClass="textColorRead" /> 
							<logic:equal name="element" property="fillable" value="true">
								<font color="red">*</font>
							</logic:equal>
						</td>
				</logic:notEqual>

			</logic:equal>
			<logic:equal name="element" property="priv_status" value="2">
			<bean:define id="isSequenceable" name="element"  property="sequenceable"></bean:define>
			<%
					boolean readOnly = false;
					if(!"new".equalsIgnoreCase(actiontype) && "true".equalsIgnoreCase(isSequenceable.toString()))
						readOnly = true;
				%>			
				<logic:equal name="element" property="codesetid" value="0">
					<logic:equal name="element" property="itemtype" value="D">
							<td align="left" nowrap valign="middle" class="AddTableRow">
								<logic:notEmpty name="selfInfoForm" property="workdatefield">
									<logic:equal name="selfInfoForm" property="workdatefield" value="${element.itemid}">
										<input name='<%="infoFieldList["+index+"].value"%>'
											value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
											Class="textColorWrite" maxlength="${element.itemlength}"
											onblur="CalculateWorkDate(this)" extra="editor"
											itemlength=${element.itemlength } dataType="simpledate"
											dropDown="dropDownDate" onchange="timeCheck(this,'${element.itemdesc}')"/>
									</logic:equal>
									<logic:notEqual name="selfInfoForm" property="workdatefield"
										value="${element.itemid}">
										<logic:notEmpty name="selfInfoForm" property="startpostfield">
											<logic:equal name="selfInfoForm" property="startpostfield"
												value="${element.itemid}">
												<input name='<%="infoFieldList["+index+"].value"%>'
													value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
													Class="textColorWrite" maxlength="${element.itemlength}"
													onblur="CalculatePostAge(this)" extra="editor" onchange="timeCheck(this,'${element.itemdesc}')"
													itemlength=${element.itemlength } dataType="simpledate"
													dropDown="dropDownDate" />
											</logic:equal>
											<logic:notEqual name="selfInfoForm" property="startpostfield"
												value="${element.itemid}">
												<input name='<%="infoFieldList["+index+"].value"%>'
													value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
													Class="textColorWrite" maxlength="${element.itemlength}"
													extra="editor" itemlength=${element.itemlength } onchange="timeCheck(this,'${element.itemdesc}')"
													dataType="simpledate" dropDown="dropDownDate" />
											</logic:notEqual>
										</logic:notEmpty>
										<logic:empty name="selfInfoForm" property="startpostfield">
											<input name='<%="infoFieldList["+index+"].value"%>'
												value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
												Class="textColorWrite" maxlength="${element.itemlength}"
												extra="editor" itemlength=${element.itemlength } onchange="timeCheck(this,'${element.itemdesc}')"
												dataType="simpledate" dropDown="dropDownDate" />
										</logic:empty>
									</logic:notEqual>
								</logic:notEmpty> <logic:empty name="selfInfoForm" property="workdatefield">
									<logic:notEmpty name="selfInfoForm" property="startpostfield">
										<logic:equal name="selfInfoForm" property="startpostfield"
											value="${element.itemid}">
											<input name='<%="infoFieldList["+index+"].value"%>'
												value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
												Class="textColorWrite" maxlength="${element.itemlength}"
												onblur="CalculatePostAge(this)" extra="editor" onchange="timeCheck(this,'${element.itemdesc}')"
												itemlength=${element.itemlength } dataType="simpledate"
												dropDown="dropDownDate" />
										</logic:equal>
										<logic:notEqual name="selfInfoForm" property="startpostfield"
											value="${element.itemid}">
											<input name='<%="infoFieldList["+index+"].value"%>'
												value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
												Class="textColorWrite" maxlength="${element.itemlength}"
												extra="editor" itemlength=${element.itemlength } onchange="timeCheck(this,'${element.itemdesc}')"
												dataType="simpledate" dropDown="dropDownDate" />
										</logic:notEqual>
									</logic:notEmpty>
									<logic:empty name="selfInfoForm" property="startpostfield">
										<input name='<%="infoFieldList["+index+"].value"%>'
											value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />"
											Class="textColorWrite" maxlength="${element.itemlength}"
											extra="editor" itemlength=${element.itemlength } onchange="timeCheck(this,'${element.itemdesc}')"
											dataType="simpledate" dropDown="dropDownDate" />
									</logic:empty>
								</logic:empty> <logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="A">
							<td align="left" nowrap valign="middle" class="AddTableRow">

								<logic:notEmpty name="selfInfoForm" property="idcardfield">
									<logic:equal name="selfInfoForm" property="idcardfield"
										value="${element.itemid}">
										<html:hidden styleId="idcardDesc" property="itemdesc"
											name="element" />
										<html:hidden name="element" property="fillable"
											styleId="idcardflag" />
										<html:text name="selfInfoForm" readonly="<%=readOnly %>"
											property='<%="infoFieldList["+index+"].value"%>'
											styleId="idcard" onclick="Element.hide('dict');"
											styleClass="textColorWrite" maxlength="${element.itemlength}"
											onblur="calculatebirthday(this)" />
									</logic:equal>
									<logic:notEqual name="selfInfoForm" property="idcardfield"
										value="${element.itemid}">
										<html:text name="selfInfoForm" readonly="<%=readOnly %>"
											property='<%="infoFieldList["+index+"].value"%>'
											onclick="Element.hide('dict');"
											onchange="checkLength('${element.itemdesc}',this,'${element.itemlength}');"
											styleClass="textColorWrite" maxlength="${element.itemlength}" />
									</logic:notEqual>
								</logic:notEmpty> <logic:empty name="selfInfoForm" property="idcardfield">
									<html:text name="selfInfoForm" readonly="<%=readOnly %>"
										property='<%="infoFieldList["+index+"].value"%>'
										onclick="Element.hide('dict');" styleClass="textColorWrite"
										maxlength="${element.itemlength}" />
								</logic:empty> <logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="N">
							<td align="left" nowrap valign="middle" class="AddTableRow">
								<logic:equal name="element" property="decimalwidth" value="">
									<html:text name="selfInfoForm"
										property='<%="infoFieldList["+index+"].value"%>'
										onclick="Element.hide('dict');" styleClass="textColorWrite"
										maxlength="${element.itemlength + element.decimalwidth + 1}"
										onchange="checkValue(this,'${element.itemlength}','${element.decimalwidth}');" />
								</logic:equal> <logic:notEqual name="element" property="decimalwidth" value="">
									<html:text name="selfInfoForm"
										property='<%="infoFieldList["+index+"].value"%>'
										onclick="Element.hide('dict');" styleClass="textColorWrite"
										maxlength="${element.itemlength + element.decimalwidth+1}"
										onchange="checkValue(this,'${element.itemlength}','${element.decimalwidth}');" />
								</logic:notEqual> <logic:equal name="element" property="fillable" value="true">
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
							<td align="right" nowrap valign="middle" class="AddTableRow"
								${itemmemo}>${image} <hrms:textnewline
									text="${element.itemdesc}" len="20"></hrms:textnewline>
							</td>
							<td align="left" nowrap valign="middle" class="AddTableRow" style="padding-top:1px;padding-bottom:2px;"
								colspan="3"><html:textarea name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									onclick="Element.hide('dict');" rows="10" cols="66"
									style="width:550px;height:100px;resize:none;"
									styleClass="textColorWrite" /> <html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].fieldsetid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemtype"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemlength"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].codesetid"%>' /> <logic:equal
									name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal></td>
							<%       
             }else{
               flag=0;%>
							<td colspan="2"></td>
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
							<td align="right" nowrap valign="middle" class="AddTableRow"
								${itemmemo}>${image} <hrms:textnewline
									text="${element.itemdesc}" len="20"></hrms:textnewline>
							</td>
							<td align="left" nowrap valign="middle" class="AddTableRow" style="padding-top:1px;padding-bottom:2px;"
								colspan="3"><html:textarea name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									onclick="Element.hide('dict');" rows="10" cols="66"
									style="width:550px;height:100px;resize:none;"
									styleClass="textColorWrite" /> <html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].fieldsetid"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemtype"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].itemlength"%>' /> <html:hidden
									name="selfInfoForm"
									property='<%="infoFieldList["+index+"].codesetid"%>' /> <logic:equal
									name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal></td>

							<%         
             }%>

							<%flag=0;%>

						</tr>
					</logic:equal>
				</logic:equal>
				<logic:notEqual name="element" property="codesetid" value="0">
						<td align="left" nowrap valign="middle" class="AddTableRow">
							<logic:equal name="element" property="itemid" value="b0110">
								<html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									styleId="b0110" onchange="changepos('${element.codesetid}')" />
								<html:text name="selfInfoForm"
									property='<%="infoFieldList["+index+"].viewvalue"%>'
									onclick="styleDisplay(this);" styleClass="textColorWrite"
									onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('b0110');" />
								<img src="/images/code.gif" align="absmiddle"
									style="vertical-align: middle" id="orgid"
									onlySelectCodeset="true" plugin="codeselector" codesetid="UN"
									inputname='<%="infoFieldList["+index+"].viewvalue"%>'
									valuename="<%="infoFieldList["+index+"].value"%>" />
							</logic:equal> <logic:equal name="element" property="itemid" value="e0122">
								<html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									styleId="e0122" onchange="changepos('${element.codesetid}')" />
								<html:text name="selfInfoForm"
									property='<%="infoFieldList["+index+"].viewvalue"%>'
									onclick="styleDisplay(this);" styleClass="textColorWrite"
									onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e0122');" />
								<img src="/images/code.gif" align="absmiddle"
									style="vertical-align: middle" id="deptid"
									onlySelectCodeset="true" plugin="codeselector" codesetid="UM"
									inputname='<%="infoFieldList["+index+"].viewvalue"%>'
									valuename="<%="infoFieldList["+index+"].value"%>" />
							</logic:equal> <logic:equal name="element" property="itemid" value="e01a1">
								<html:hidden name="selfInfoForm"
									property='<%="infoFieldList["+index+"].value"%>'
									styleId="e01a1" onchange="changepos('${element.codesetid}')" />
								<html:text name="selfInfoForm"
									property='<%="infoFieldList["+index+"].viewvalue"%>'
									onclick="styleDisplay(this);" styleClass="textColorWrite"
									onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e01a1');" />
								<img src="/images/code.gif" align="absmiddle"
									style="vertical-align: middle" id="jobid"
									onlySelectCodeset="true" plugin="codeselector" codesetid="@K"
									inputname='<%="infoFieldList["+index+"].viewvalue"%>'
									valuename="<%="infoFieldList["+index+"].value"%>" />
							</logic:equal> <logic:notEqual name="element" property="itemid" value="b0110">
								<logic:notEqual name="element" property="itemid" value="e0122">
									<logic:notEqual name="element" property="itemid" value="e01a1">
										<html:hidden name="selfInfoForm"
											property='<%="infoFieldList["+index+"].value"%>' />
										<html:text name="selfInfoForm"
											property='<%="infoFieldList["+index+"].viewvalue"%>'
											onclick="styleDisplay(this);" styleClass="textColorWrite"
											onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" />
										
									<logic:equal name="element" property="codesetid" value="UM">
										<img src="/images/code.gif" align="absmiddle" onlySelectCodeset="false" 
											style="vertical-align: middle" onlySelectCodeset="true" ctrltype="0"
											plugin="codeselector" codesetid="${element.codesetid}"
											inputname='<%="infoFieldList["+index+"].viewvalue"%>'
											valuename="<%="infoFieldList["+index+"].value"%>" />
									</logic:equal>
									<logic:notEqual name="element" property="codesetid" value="UM">
										<img src="/images/code.gif" align="absmiddle" onlySelectCodeset="true" 
											style="vertical-align: middle" onlySelectCodeset="true" ctrltype="0"
											plugin="codeselector" codesetid="${element.codesetid}"
											inputname='<%="infoFieldList["+index+"].viewvalue"%>'
											valuename="<%="infoFieldList["+index+"].value"%>" />
									</logic:notEqual>
									</logic:notEqual>
								</logic:notEqual>
							</logic:notEqual> <logic:equal name="element" property="fillable" value="true">
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
					<td colspan="2"></td>
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
			<td align="center" class="trShallow1" nowrap colspan="4"><logic:notEqual
					value="1" name="selfInfoForm" property="approveflag">
					<logic:equal name="selfInfoForm" property="setname" value="A01">
						<logic:equal name="selfInfoForm" property="setprv" value="2">
							<button name="sf" class="mybutton" onclick="writeable();">
								<bean:message key="button.save" />
							</button>
						</logic:equal>
						<logic:equal name="selfInfoForm" property="setprv" value="3">
							<button name="sf" class="mybutton" onclick="writeable();">
								<bean:message key="button.save" />
							</button>
						</logic:equal>
					</logic:equal>
					<logic:notEqual name="selfInfoForm" property="setname" value="A01">
						<logic:equal name="selfInfoForm" property="setprv" value="2">
							<button name="v_saves" class="mybutton" onclick="saves()">
								<bean:message key="button.save" />
							</button>
						</logic:equal>
						<logic:equal name="selfInfoForm" property="setprv" value="3">
							<button name="v_saves" class="mybutton" onclick="saves()">
								<bean:message key="button.save" />
							</button>
						</logic:equal>
						<logic:equal name="selfInfoForm" property="actiontype" value="new">
							<button name="v_saves" class="mybutton" onclick="savesre()">
								<bean:message key="button.savereturn" />
							</button>
						</logic:equal>
					</logic:notEqual>
				</logic:notEqual> <logic:equal name="selfInfoForm" property="inputchinfor" value="1">
					<logic:equal name="selfInfoForm" property="approveflag" value="1">
						<logic:equal value="A01" name="selfInfoForm" property="setname">
							<button type="button" name="sf" class="mybutton" onclick="appEdite();">
								<bean:message key="selfinfo.defend" />
							</button>
						</logic:equal>
					</logic:equal>
				</logic:equal> <logic:equal name="selfInfoForm" property="inputchinfor" value="1">
					<logic:equal name="selfInfoForm" property="approveflag" value="1">
						<logic:notEqual name="selfInfoForm" property="isAble" value="1">
							<script>
		      				input = document.getElementsByTagName("input");
		      				for (i = 0; i < input.length; i++) {
		      					if (input[i].type == "text") {
		      						input[i].className = "textColorRead";
		      						input[i].readOnly = "true";
		      					}
		      				}
		      				
		      				img = document.getElementsByTagName("img");
		      				for(i = 0; i < img.length; i++) {
		      					src = img[i].src;
		      					
		      					src = src.substr(src.lastIndexOf("/"));
		      					if (src == "/code.gif") {
		      						img[i].style.display = "none";
		      					}
		      				}
		      			</script>
						</logic:notEqual>
					</logic:equal>
				</logic:equal> <logic:equal value="1" name="selfInfoForm" property="approveflag">
					<logic:equal value="1" name="selfInfoForm" property="viewbutton">
						<hrms:user_state name="sss" userid="<%=userView.getUserId()%>"
							dbname="<%=userView.getDbname()%>"
							tablename="${selfInfoForm.setname}"></hrms:user_state>
						<bean:define id="sid" name="selfInfoForm" property="setname" />
						<SCRIPT language="javascript">
	      	
	      var flag=selfInfoForm.sss.value;
	      var inputchinfor="${selfInfoForm.inputchinfor}";
	      var approveflag = "${selfInfoForm.approveflag}";
	      if (inputchinfor=="1" && approveflag=="1") {
	      	<logic:notEqual name="selfInfoForm" property="isAble" value="1" >
	      		input = document.getElementsByTagName("input");
		      				for (i = 0; i < input.length; i++) {
		      					if (input[i].type == "text") {
		      						input[i].className = "textColorRead";
		      						input[i].readOnly = "true";
		      					}
		      				}
	      	</logic:notEqual>
	      }
	      if(flag=='0'||flag=='2'||flag==''||flag=='5'||inputchinfor=='1'){
	      <logic:equal name="selfInfoForm" property="setprv" value="2">
	      	<logic:equal name="selfInfoForm" property="setname" value="A01">
	      		<logic:equal name="selfInfoForm" property="approveflag" value="1">
	      			<logic:equal name="selfInfoForm" property="inputchinfor" value="1">
		      			<logic:equal name="selfInfoForm" property="isAble" value="1" >
		      				input = document.getElementsByTagName("input");
		      				for (i = 0; i < input.length; i++) {
		      					if (input[i].type == "text") {
		      						input[i].className = "textColorRead";
		      						input[i].readOnly = "true";
		      					}
		      				}
		      				
		      				img = document.getElementsByTagName("img");
		      				for(i = 0; i < img.length; i++) {
		      					src = img[i].src;
		      					
		      					src = src.substr(src.lastIndexOf("/"));
		      					if (src == "/code.gif") {
		      						img[i].style.display = "none";
		      					}
		      				}
		      			</logic:equal>
	      			</logic:equal>
	      		</logic:equal>
	      	</logic:equal>
	      </logic:equal>
	      document.write('<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	    '  				<logic:equal name="selfInfoForm" property="inputchinfor" value="1">'+
         '								<logic:equal name="selfInfoForm" property="isAppEdite" value="1">'+
            '								<button name="appSaves" class="mybutton" onclick="writeable()"><bean:message key="button.save"/></button>'+
            '								<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
	       '									<hrms:priv func_id="01030104">'+
	       					'						<button name="apde" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>'+
	      '										</hrms:priv>'+
	      '										<button name="return" class="mybutton" onclick="appReturn();"><bean:message key="button.return"/></button>'+
	      '									</logic:equal>'+
	      '								</logic:equal>'+
         '						</logic:equal>'+
	      				'</logic:equal>');
	      document.write('<logic:equal name="selfInfoForm" property="setprv" value="2">'+
	      '					<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	      '						<logic:notEqual name="selfInfoForm" property="inputchinfor" value="1">'+
		      '						<button name="sf" class="mybutton" onclick="writeable();">临时<bean:message key="button.save"/></button>'+
		      '						<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
		       '						<hrms:priv func_id="01030104">'+
					'						<button name="apde" class="mybutton" onclick="prove();"><bean:message key="button.appeal"/></button>'+
		      '							</hrms:priv>'+
		      '						</logic:equal>'+
	      '						</logic:notEqual>'+						
            '				</logic:equal>'+
	      '				</logic:equal>'+
	      '<logic:equal name="selfInfoForm" property="setprv" value="3">'+
	       '<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	       '<logic:equal value="0" name="selfInfoForm" property="inputchinfor">'+
	      '<hrms:submit styleClass="mybutton"  property="b_save"><bean:message key="button.save"/></hrms:submit></logic:equal>'+
	       ' </logic:equal>'+
	       ' </logic:equal>'+
	     '<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
	     '<logic:equal value="0" name="selfInfoForm" property="inputchinfor">'+
	      '<hrms:priv func_id="01030115">'+
	      '<logic:equal name="selfInfoForm" property="setname" value="A01">'+
	      ' <button name="allok" class="mybutton" onclick="checkdata()">'+APP_ALL+'</button> '+    
	      ' </logic:equal>'+
	      '</hrms:priv>'+
	      '</logic:equal>'+
	      '</logic:equal>'
	      
	      );
	      
	      }else{
	      if(flag==3){
	      
	     
	      document.write('<logic:equal name="selfInfoForm" property="setprv" value="2">'+
	      '<logic:equal value="1" name="selfInfoForm" property="approveflag">'+
	      '<button name="appsss" class="mybutton" onclick="capp();">'+PLEASE_APPLIC+'</button>'+
	      '</logic:equal>'+
          '</logic:equal>'
	      
	      );
	      
	      }
	      
	      }
	      </SCRIPT>

						<logic:notEqual name="selfInfoForm" property="setname" value="A01">
							<logic:equal value="1" name="selfInfoForm" property="approveflag">
								<hrms:priv func_id="01030104">
									<logic:equal value="1" name="selfInfoForm" property="isDraft">
										<input type="button" name="apsssss" class="mybutton"
											onclick='provesDraft();'
											value="<bean:message key="button.appeal"/>" />
									</logic:equal>
									<logic:notEqual value="1" name="selfInfoForm"
										property="isDraft">
										<input class="mybutton" type="button" name="apsssss"
											onclick="proves()"
											value="<bean:message key="button.appeal"/>" />
									</logic:notEqual>
								</hrms:priv>
								<logic:notEqual value="1" name="selfInfoForm" property="isDraft">
									<input class="mybutton" type="button" name="v_saves"
										onclick="saves()" value="<bean:message key="button.save"/>" />
								</logic:notEqual>
								<logic:equal value="1" name="selfInfoForm" property="isDraft">
									<input class="mybutton" type="button" name="v_saves"
										onclick="savesDraft()"
										value="<bean:message key="button.save"/>" />
								</logic:equal>

							</logic:equal>
							<logic:notEqual value="1" name="selfInfoForm"
								property="approveflag">
								<logic:equal name="selfInfoForm" property="setprv" value="2">
									<button name="v_saves" class="mybutton" onclick="saves()">
										<bean:message key="button.save" />
									</button>
								</logic:equal>
							</logic:notEqual>
							<%if(!(inputchinfor.equals("1")&&approveflag.equals("1"))) {%>
							<hrms:submit styleClass="mybutton" property="b_return">
								<bean:message key="button.return" />
							</hrms:submit>
							<%}else{  %>
							<input class="mybutton" type="button" name="re"
								onclick="breturn('<bean:write name="selfInfoForm" property="setname"/>')"
								value="<bean:message key="button.return"/>" />
							<%} %>
						</logic:notEqual>
					</logic:equal>
					<logic:notEqual value="1" name="selfInfoForm" property="viewbutton">
						<logic:notEqual name="selfInfoForm" property="setname" value="A01">
							<input class="mybutton" type="button" name="re"
								onclick="breturn('<bean:write name="selfInfoForm" property="setname"/>')"
								value="<bean:message key="button.return"/>" />
						</logic:notEqual>
					</logic:notEqual>
				</logic:equal></td>
		</tr>
	</table>
	<%}else{%>
	<table width="80%" border="0" cellspacing="1" align="center"
		cellpadding="1">
		<br>
		<br>
		<tr>
			<td align="center" nowrap><bean:message
					key="workbench.info.nomainfield" /></td>
		</tr>
	</table>
	<%}%>
	<div id="date_panel">
		<select name="date_box" multiple="multiple" size="10"
			style="width: 120" onchange="setSelectValue();"
			onclick="setSelectValue();">
			<option value="1992">1992</option>
			<option value="1992.4">1992.04</option>
			<option value="1992.4.12">1992.04.12</option>
		</select>
	</div>
	<script language="javascript">
Element.hide('date_panel');
   getfirstfocuse();
   
</script>

	<script language="JavaScript" src="/performance/workdiary/workdiary.js"></script>
	<div id=dict style="border-style: nono">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr>
				<td><select name="dict_box" multiple="multiple" size="10"
					class="dropdown_frame" style="width: 200;display: none;"
					ondblclick="setSelectCodeValue();"
					onkeydown="return inputType(this,event)"
					onblur="Element.hide('dict');">
				</select></td>
			</tr>
			</div>
			</html:form>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="javascript">
  initDocument();
  rendererFun();
<logic:notEmpty name="selfInfoForm" property="msg">
alert('<bean:write  name="selfInfoForm" property="msg"/>');
</logic:notEmpty>
<logic:notEmpty name="selfInfoForm" property="formationMsg">
	  alert('<bean:write  name="selfInfoForm" property="formationMsg"/>');
<%
    selfInfoForm.setFormationMsg("");
%>
</logic:notEmpty>
</script>
