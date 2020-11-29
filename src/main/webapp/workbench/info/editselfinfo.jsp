<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.actionform.selfinfomation.SelfInfoForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%// 在标题栏显示当前用户和日期 2004-5-10 
String userName = null;
String css_url = "/css/css1.css";
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
String codevalue="";String code="";String isAll="";
if (userView != null) {
css_url = userView.getCssurl();
if (css_url == null || css_url.equals(""))
	css_url = "/css/css1.css";
if(!userView.isSuper_admin()){ 
    code=userView.getManagePrivCode();
    codevalue=userView.getManagePrivCodeValue();
    if("UN".equalsIgnoreCase(code)&&(codevalue==null||codevalue.length()==0)){
    	isAll="all";
    }
  }else{
  	isAll="all";
  } 
}
String bosflag="";
if(userView!=null){
   	bosflag = userView.getBosflag();
}

String orgtemp = "";String orgtempview = "";String postemp = "";String postempview = "";String kktemp = "";
String kktempview = "";String birthdayfield = "";String agefield = "";
String workagefield = "";String postagefield = "";String axfield = "";String axviewfield = "";
%>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<hrms:linkExtJs/>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
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
<script type="text/javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/js/wz_tooltip.js"></script>
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
	var ViewProperties=new ParameterSet();
</script>
</head>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/workbench/info/editselfinfo.js"></script>
<%int i = 0;int flag = 0;%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<logic:equal value="new" name="selfInfoForm" property="actiontype"> 
	<logic:equal value="A01" name="selfInfoForm" property="setname">
		<body onload="changepos('${selfInfoForm.kind}')">
	</logic:equal>
	<logic:notEqual value="A01" name="selfInfoForm" property="setname">
		<body>
	</logic:notEqual>
</logic:equal>
<logic:notEqual value="new" name="selfInfoForm" property="actiontype">
	<body>
</logic:notEqual>
	<hrms:themes />
<style>
.AddTableRow {
	height:22px;
	valign:middle;
	padding:0 5px 0 5px;
}
.textColorRead{
	align:middle;
}
</style>
<script type="text/javascript">
var date_desc;
var code_value="<%=codevalue%>";  
var isAall = "<%=isAll %>"; 
  function validate(){
     var item = document.getElementById("a0101");
      if(item!=null){
          check();
      }
    var tag=true;    
     <logic:iterate  id="element"    name="selfInfoForm"  property="infoFieldList" indexId="index"> 
     <logic:notEqual value="#####" name="element" property="itemid">
      <logic:equal name="element" property="visible" value="true">   
            <bean:define id="fl" name="element" property="fillable"/>
            <bean:define id="desc" name="element" property="itemdesc"/>
            <bean:define id="itemid" name="element" property="itemid"/>
             var valueInputs=document.getElementsByName("<%="infoFieldList["+index+"].value"%>");
              var dobj=valueInputs[0];
            <%
                if("2".equalsIgnoreCase(userView.analyseFieldPriv(itemid.toString()))) {
            %>
              if("${fl}"=="true"&&dobj.value.length<1){
                alert("${desc}"+"必须填写！");
                return ;
              }
              <%}%>
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
       <logic:equal name="element" property="itemtype" value="A"> 
       	<logic:equal name="element" property="codesetid" value="0"> 
	       var itemValue=document.getElementsByName("<%="infoFieldList["+index+"].value"%>")[0].value;
	       var itemlength = ${element.itemlength}
	       if(IsOverStrLength(itemValue, itemlength)){
	    	   var msg = ITEMVALUE_MORE_LENGTH;
	    	   msg = msg.replace('{0}', itemlength).replace('{1}', itemlength/2);
	    	   alert("${desc}"+msg);
		       return false;
	       }
       	</logic:equal>  
       </logic:equal>  
      </logic:equal>  
      check(document.getElementsByName('<%="infoFieldList["+index+"].value"%>')[0]);
      </logic:notEqual>
     </logic:iterate>    
     return tag;
  }
var orgtemp="<%=orgtemp%>";
var orgtempview="<%=orgtempview%>";
var postemp="<%=postemp%>";
var postempview="<%=postempview%>";
var kktemp="<%=kktemp%>";
var kktempview="<%=kktempview%>";
var returnValue = "${selfInfoForm.returnvalue}";
var uplevel = "${selfInfoForm.uplevel}";
var nbase = "${selfInfoForm.userbase}";
var birthdayfield = "<%=birthdayfield%>";
var agefield = "<%=agefield%>";
var axfield = "<%=axfield%>";
var axviewfield = "<%=axviewfield%>";
var workagefield = "<%=workagefield%>";
var postagefield = "<%=postagefield%>";
var startpostfield = "${selfInfoForm.startpostfield}";
var workdatefield = "${selfInfoForm.workdatefield}";
</script>
<body>

	<html:form action="/workbench/info/editselfinfo" onsubmit="return validate()">
	<html:hidden name="selfInfoForm" property="setname" />
	<html:hidden name="selfInfoForm" property="idTypeValue" styleId="idTypeValue" />
    <html:hidden name="selfInfoForm" property="idType" styleId="idType" />
    <html:hidden name="selfInfoForm" property="cardflag" styleId="cardflag" />
	<logic:equal value="1" name="selfInfoForm" property="isBrowse">
		<logic:equal value="A01" name="selfInfoForm" property="setname">
		<logic:equal name="selfInfoForm" property="setprv" value="2">
			<div style="margin-left:63px;margin-bottom:15px;">
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
	</logic:equal>
		<%SelfInfoForm selfInfoForm = (SelfInfoForm) session.getAttribute("selfInfoForm");
			int rowss = Integer.parseInt(selfInfoForm.getRownums());
			String actiontype = selfInfoForm.getActiontype();
			if (selfInfoForm.getInfoFieldList().size() > 0) {%>
		<%if("hcm".equals(bosflag)){ %>
		<table width="85%" border="0" cellspacing="1" align="center" cellpadding="1" style="margin-top: 12px">
		<%}else{ %>
		<table width="85%" border="0" cellspacing="1" align="center" cellpadding="1" style="margin-top: 10px">
		<%} %>
			<html:hidden name="selfInfoForm" property="userbase" />
			<html:hidden name="selfInfoForm" property="actiontype" />
			<html:hidden name="selfInfoForm" property="a0100" />
			<html:hidden name="selfInfoForm" property="i9999" />
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
				<%if (flag == 0) {
						if (i % 2 == 0) {
					%>
						<tr class="trShallow1">
					<%} else {%>
						<tr class="trDeep1">
					<%}
					i++;
					flag = rowss;
				} else {
					flag = 0;
				}%>
                       <td align="right" nowrap class="AddTableRow" valign="middle" ${itemmemo}>
                       		${image} <hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>
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
									<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>'  readonly="true" styleClass="textColorRead" maxlength="${element.itemlength}" onfocus="Element.hide('dict');"/><!-- zgd 2014-3-5 进入前先取消模糊匹配框 -->
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="A">
								<td align="left" nowrap valign="middle" class="AddTableRow">
									<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" onfocus="Element.hide('dict');" styleClass="textColorRead" maxlength="${element.itemlength}"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
								<td align="left" nowrap valign="middle" class="AddTableRow">
									<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" styleClass="textColorRead" maxlength="${element.itemlength + element.decimalwidth + 1}" onfocus="Element.hide('dict');" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<%if (flag == 0) {
					if (i % 2 == 0) {
					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;%>
	                            <td align="right" nowrap class="AddTableRow" valign="middle"
	                                ${itemmemo}>${image} <hrms:textnewline
	                                    text="${element.itemdesc}" len="20"></hrms:textnewline>
	                            </td>
								<td align="left" nowrap valign="middle" colspan="3" class="AddTableRow">
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" rows="10" cols="66" style="width:550px;height:100px;" styleClass="textColorRead" onfocus="Element.hide('dict');"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%} else {
					flag = 0;%>
								<td colspan="2" class="AddTableRow">
								</td>
								</td>
								<%if (flag == 0) {
						if (i % 2 == 0) {
						%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
						i++;
						flag = rowss;
					} else {
						flag = 0;
					}%>
	                            <td align="right" nowrap class="AddTableRow" valign="middle"
	                                ${itemmemo}>${image} <hrms:textnewline
	                                    text="${element.itemdesc}" len="20"></hrms:textnewline>
	                            </td>
								<td align="left" nowrap valign="middle" colspan="3" class="AddTableRow">
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="true" rows="10" cols="66" style="width:550px;height:100px;" styleClass="textColorRead" onfocus="Element.hide('dict');"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%}%>
								<%flag = 0;%>
							</tr>
						</logic:equal>
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
							<td align="left" nowrap valign="middle" class="AddTableRow">
								<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />
								<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' readonly="true" styleClass="textColorRead" onfocus="Element.hide('dict');"/>
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
								<td align="left"  nowrap valign="middle" class="AddTableRow">
            <logic:notEmpty name="selfInfoForm" property="workdatefield">
                 <logic:equal name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                     <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value" %>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onchange="CalculateWorkDate(this)" onblur="CalculateWorkDate(this)" onfocus="Element.hide('dict');"/> 
                 </logic:equal>
                <logic:notEqual name="selfInfoForm" property="workdatefield" value="${element.itemid}">
                   <logic:notEmpty name="selfInfoForm" property="startpostfield">
                        <logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                            <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onchange="CalculatePostAge(this)" onblur="CalculatePostAge(this)" onfocus="Element.hide('dict');"/>
                        </logic:equal>
                        <logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                          <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onfocus="Element.hide('dict');"/>
                        </logic:notEqual>
                   </logic:notEmpty>
                  <logic:empty name="selfInfoForm" property="startpostfield">
                    <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onfocus="Element.hide('dict');"/> 
                   </logic:empty>
                </logic:notEqual>
            </logic:notEmpty>
            <logic:empty name="selfInfoForm" property="workdatefield">
               <logic:notEmpty name="selfInfoForm" property="startpostfield">
                 <logic:equal name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                     <input type="text" name='<%="infoFieldList["+index+"].value"%>' onblur="CalculatePostAge(this)" value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onfocus="Element.hide('dict');"/>  
                 </logic:equal>
                <logic:notEqual name="selfInfoForm" property="startpostfield" value="${element.itemid}">
                  <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onfocus="Element.hide('dict');"/>
                </logic:notEqual>
              </logic:notEmpty>
              <logic:empty name="selfInfoForm" property="startpostfield">
                 <input type="text" name='<%="infoFieldList["+index+"].value"%>' value="<bean:write name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' />" class="textColorRead" style='width:200px;background-color:#ffffff;' extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate" onfocus="Element.hide('dict');"/>
              </logic:empty>
            </logic:empty>
             <logic:equal name="element"  property="fillable" value="true">
               <font color="red">*</font>
             </logic:equal>
         </td> 
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="A">
								<td align="left" nowrap valign="middle" class="AddTableRow">
									<logic:notEmpty name="selfInfoForm" property="idcardfield">
										<logic:equal name="selfInfoForm" property="idcardfield" value="${element.itemid}">
           					 <html:hidden styleId="idcardDesc" property="itemdesc" name="element"/>
                   <html:text name="selfInfoForm" styleId="idcard" readonly="<%=readOnly %>" property='<%="infoFieldList["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="calculatebirthday(this)" onfocus="Element.hide('dict');"/>
										<html:hidden name="element" property="fillable" styleId="idcardflag"/>
										</logic:equal>
										<logic:notEqual name="selfInfoForm" property="idcardfield" value="${element.itemid}">
										<logic:notEqual name="element" property="itemid" value="a0101">
                   							<html:text name="selfInfoForm" readonly="<%=readOnly %>" property='<%="infoFieldList["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onfocus="Element.hide('dict');"/>
										</logic:notEqual>
										<logic:equal name="element" property="itemid" value="a0101">
											<html:text name="selfInfoForm" styleId="a0101" onkeyup="check();" property='<%="infoFieldList["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength}" onfocus="Element.hide('dict');"/>
										</logic:equal>
										</logic:notEqual>
									</logic:notEmpty>
									<logic:empty name="selfInfoForm" property="idcardfield">
                 <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' readonly="<%=readOnly %>" styleClass="textColorWrite" maxlength="${element.itemlength}" onfocus="Element.hide('dict');"/>
									</logic:empty>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
								<td align="left" nowrap valign="middle" class="AddTableRow">
								    <logic:greaterThan name="element" property="decimalwidth" value="0">
								        <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onfocus="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth +1}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
								    </logic:greaterThan>
								    <logic:lessEqual name="element" property="decimalwidth" value="0">
								       <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onfocus="Element.hide('dict');" styleClass="textColorWrite" maxlength="${element.itemlength}" onblur="checkValue(this,'${element.itemlength}','${element.decimalwidth}');"/>
								    </logic:lessEqual>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<%if (flag == 0) {
					if (i % 2 == 0) {
					%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
					i++;
					flag = rowss;%>
                                <td align="right" nowrap class="AddTableRow" valign="middle"
                                    ${itemmemo}>${image} <hrms:textnewline
                                        text="${element.itemdesc}" len="20"></hrms:textnewline>
                                </td>
								<td align="left" nowrap valign="middle" colspan="3" class="AddTableRow">
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onfocus="Element.hide('dict');" rows="10" cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%} else {
					flag = 0;%>
								<td colspan="2" class="AddTableRow">
								</td>
								</td>

								<%if (flag == 0) {
						if (i % 2 == 0) {
						%>
							<tr class="trShallow1">
								<%} else {%>
							<tr class="trDeep1">
								<%}
						i++;
						flag = rowss;
					} else {
						flag = 0;
					}%>
								<td align="right" nowrap valign="middle" class="AddTableRow">
									<hrms:textnewline text="${element.itemdesc}" len="20"></hrms:textnewline>									
								</td>
								<td align="left" nowrap valign="middle" colspan="3" class="AddTableRow">
									<html:textarea name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' onfocus="Element.hide('dict');" rows="10" cols="66" style="width:550px;height:100px;" styleClass="textColorWrite"/>
									<logic:equal name="element" property="fillable" value="true">
										<font color="red">*</font>
									</logic:equal>
								</td>
								<%}%>
								<%flag = 0;%>
							</tr>
						</logic:equal>
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
							<td align="left" nowrap valign="middle" class="AddTableRow">
							
		<!--************************start***********单位部门代码级联**********************************************************
			方法1：changepos(参数1)参数1: 0=岗位 1=部门 2=单位  功能：更新text框里面的内容
			方法2：changeLowerLevel(参数1,参数2)参数1：当前选择input的valuename 参数2：孩子节点的ID 功能：更新下级codeselector的parentid
			update by xiegh on date 20180315 
		 -->
				<logic:equal name="element" property="itemid" value="b0110">
					<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="b0110" onchange="changepos('${element.codesetid}')" />  
             		<html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>'  styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('b0110');" onclick="styleDisplay(this);"/><!-- onkeyup="addDict('${element.codesetid}',this);" -->
                   	<img src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" nmodule='4' ctrltype='3' inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>" afterfunc="changeLowerLevel('<%="infoFieldList["+index+"].value"%>','deptId');changepos('2');"/>
                </logic:equal>
				<logic:equal name="element" property="itemid" value="e0122">
					    <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e0122" onchange="changepos('${element.codesetid}');changetitle(this);" />  
                        <hrms:codetoname codeid="UM" name="element" codevalue="value" codeitem="codeitem" scope="page" uplevel="${selfInfoForm.uplevel}"/>  	      
                        <html:text name="selfInfoForm" title="${codeitem.codename }" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e0122');" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/><!-- onkeyup="addDict('${element.codesetid}',this);"  -->
                       	<img  src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id="deptId" onlySelectCodeset="true" plugin="codeselector"  codesetid="${element.codesetid}" nmodule='4' ctrltype='3' inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>" afterfunc="changeLowerLevel('<%="infoFieldList["+index+"].value"%>','jobId');changepos('1');"/>
                </logic:equal>
				<logic:equal name="element" property="itemid" value="e01a1">
					  <html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="e01a1" onchange="changepos('${element.codesetid}')" />  
                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);changeOrg('e01a1');" onkeydown="return inputType2(this,event)" onclick="styleDisplay(this);"/><!-- onkeyup="addDict('${element.codesetid}',this);" -->
                      <img src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id='jobId' onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" nmodule='4' ctrltype='3' inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"  afterfunc="changepos('0');"/>
                </logic:equal>
               	<script>
						function changeLowerLevel(curentId,childId){
							if(document.getElementById(childId))
								document.getElementById(childId).setAttribute("parentid",document.getElementsByName(curentId)[0].value);
						}
				</script>
      <!--***********************************单位部门代码级联****end******************************************************  -->
								<logic:notEqual name="element" property="itemid" value="b0110">
									<logic:notEqual name="element" property="itemid" value="e0122">
										<logic:notEqual name="element" property="itemid" value="e01a1">
												<html:hidden name="selfInfoForm" property='<%="infoFieldList["+index+"].value"%>' styleId="${element.itemid }" /> 
	                      <html:text name="selfInfoForm" property='<%="infoFieldList["+index+"].viewvalue"%>' styleClass="textColorWrite" onchange="fieldcode(this,2);checkDict('${element.codesetid}',this);" onclick="styleDisplay(this);"/>
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
												   <logic:notEqual name="element" property="itemid" value="${selfInfoForm.part_unit}">
												    <logic:notEqual name="element" property="codesetid" value="UM">
                       	<img src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id="infoFieldList<%=index %>" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
											        </logic:notEqual>
											        <logic:equal name="element" property="codesetid" value="UM">
                        <img src="/images/code.gif" align="absmiddle" style="vertical-align:middle" id="infoFieldList<%=index %>" onlySelectCodeset="false" plugin="codeselector" codesetid="${element.codesetid}" inputname='<%="infoFieldList["+index+"].viewvalue"%>'  valuename="<%="infoFieldList["+index+"].value"%>"/>
                                                    </logic:equal>
											        </logic:notEqual>
											    </logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
								</logic:notEqual>
								<logic:equal name="element" property="fillable" value="true">
									<font color="red">*</font>
								</logic:equal>
							</td>
					</logic:notEqual>
				</logic:equal>
				<logic:notEqual name="element" property="itemtype" value="M">
				<%if (flag == 0) {%>
					</tr>
				<%} else {%>
				<logic:equal name="element" property="rowflag" value="${index}">
						<td colspan="2" class="AddTableRow"></td>
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
				<td height="3px"></td>
			</tr>
			<tr>
				<td align="center" nowrap colspan="4" style="padding-bottom: 5px;">
					<logic:equal name="selfInfoForm" property="setname" value="A01">
						<html:hidden name="selfInfoForm" property="tolastpageflagsub" value="no" />
						<logic:equal name="selfInfoForm" property="actiontype" value="new">
							<html:hidden name="selfInfoForm" property="tolastpageflag" value="yes" />
						</logic:equal>
						<logic:notEqual name="selfInfoForm" property="actiontype" value="new">
							<html:hidden name="selfInfoForm" property="tolastpageflag" value="no" />
						</logic:notEqual>
					</logic:equal>
					<logic:notEqual name="selfInfoForm" property="setname" value="A01">
						<logic:equal name="selfInfoForm" property="actiontype" value="new">
							<html:hidden name="selfInfoForm" property="tolastpageflagsub" value="yes" />
						</logic:equal>
						<logic:notEqual name="selfInfoForm" property="actiontype" value="new">
							<html:hidden name="selfInfoForm" property="tolastpageflagsub" value="no" />
						</logic:notEqual>
					</logic:notEqual>
					<logic:equal name="selfInfoForm" property="setprv" value="2">
						<logic:equal name="selfInfoForm" property="setname" value="A01">
						<logic:notEqual name="selfInfoForm" property="writeable" value="1">
						</logic:notEqual>
						</logic:equal>
					</logic:equal>
					<logic:equal name="selfInfoForm" property="setprv" value="2">
						<logic:equal name="selfInfoForm" property="setname" value="A01">
								<button type="button" name="savass" class="mybutton" onclick="save();">
									<bean:message key="button.save" />
								</button>
						</logic:equal>
						<logic:notEqual name="selfInfoForm" property="setname" value="A01">
							<button type="button" name="savasss" id="saveid" class="mybutton" onclick="savesub();">
								<bean:message key="button.save" />
							</button>
							<logic:notEqual name="selfInfoForm" property="actiontype" value="update">
							<button type="button" name="savass" class="mybutton" id="savereturnid" onclick="savere();">
								<bean:message key="button.savereturn" />
								</button>
							</logic:notEqual>
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="selfInfoForm" property="setprv" value="3">
						<logic:equal name="selfInfoForm" property="setname" value="A01">
							<button type="button" name="savass" class="mybutton" onclick="save();">
								<bean:message key="button.save" />
							</button>
						</logic:equal>
						<logic:notEqual name="selfInfoForm" property="setname" value="A01">
							<button type="button" name="savass" class="mybutton" id="saveid" onclick="savesub();">
								<bean:message key="button.save" />
							</button>
						</logic:notEqual>
					</logic:equal>
<logic:notEqual name="selfInfoForm" property="writeable" value="1">
	<%String checkOk = "";%>
	<logic:equal value="1" name="selfInfoForm" property="generalsearch">
		<%checkOk = "&check=ok";%>
	</logic:equal>
	<logic:notEqual value="1" name="selfInfoForm" property="generalsearch">
		<%checkOk = "";%>
	</logic:notEqual>
					<logic:equal name="selfInfoForm" property="setname" value="A01">
						<logic:equal name="selfInfoForm" property="returnvalue" value="1">
						  	<logic:equal value="1" name="selfInfoForm" property="isBrowse">
					    			<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100=<bean:write name="selfInfoForm" property="a0100"/>&flag=notself&returnvalue=1&fromPhoto=${browseForm.fromPhoto}','nil_body');">
					    	</logic:equal>
					    	<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
						  		<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/editselfinfo.do?br_return=link','il_body');">
					    	</logic:notEqual>
					    </logic:equal>
					     <logic:equal name="selfInfoForm" property="returnvalue" value="64">
                               <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${selfInfoForm.politics }&param=Y&a_code=${selfInfoForm.a_code }','_parent')">                 
                        </logic:equal>
                        <logic:equal name="selfInfoForm" property="returnvalue" value="65">
                               <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/dtgh/party/person/searchbusinesslist.do?b_search=link&politics=${selfInfoForm.politics }&param=V&a_code=${selfInfoForm.a_code }','_parent')">                 
                        </logic:equal>
					    <logic:equal name="selfInfoForm" property="returnvalue" value="2">
					    	<logic:notEqual value="1" name="selfInfoForm" property="isAdvance">
					    		<logic:equal value="1" name="selfInfoForm" property="isBrowse">
					    			<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100=<bean:write name="selfInfoForm" property="a0100"/>&flag=notself&returnvalue=1','nil_body');">
					    		</logic:equal>
					    		<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
					    			<logic:equal value="1" name="selfInfoForm" property="returns">
						  				<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link&returnvalue=dxt<%=checkOk %>','nil_body');">
						  			</logic:equal>
						  			<logic:notEqual value="1" name="selfInfoForm" property="returns">
						  				<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_queryinfo=link<%=checkOk %>','nil_body');">
						  			</logic:notEqual>
					    		</logic:notEqual>
					    	</logic:notEqual>
					    	<logic:equal value="1" name="selfInfoForm" property="isAdvance">
					    		<logic:equal value="1" name="selfInfoForm" property="isBrowse">
					    			<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/browse/showselfinfo.do?b_search=link&userbase=${browseForm.userbase}&a0100=<bean:write name="selfInfoForm" property="a0100"/>&flag=notself&returnvalue=1','nil_body');">
					    		</logic:equal>
					    		<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
						  			<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/showinfodata.do?b_searchinfo=link&check=ok&isAdvance=1','nil_body');">
					    		</logic:notEqual>
					    	</logic:equal>
					    </logic:equal>
					    <logic:equal name="selfInfoForm" property="returnvalue" value="74">
                             <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body');trun();">                 
                        </logic:equal>
                        <logic:equal name="selfInfoForm" property="returnvalue" value="73">
                              <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction2('/system/warn/result_manager.do?b_query2=link')">                 
                        </logic:equal>
                        <logic:equal name="selfInfoForm" property="returnvalue" value="75">
                               <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body')">                 
                        </logic:equal>
					</logic:equal>
					<logic:notEqual name="selfInfoForm" property="setname" value="A01">
						<logic:equal value="1" name="selfInfoForm" property="isBrowse">
							<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/searchselfdetailinfo.do?b_searchsort=search&flag=noself&isAppEdite=1&a0100=${browseForm.a0100}&setname=${selfInfoForm.setname}&isBrowse=1','mil_body')">
						</logic:equal>
						<logic:notEqual value="1" name="selfInfoForm" property="isBrowse">
							<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/editselfinfo.do?b_returnsub=link&setname=${selfInfoForm.setname }','mil_body')">
						</logic:notEqual>
					</logic:notEqual>
			</logic:notEqual>
			<logic:equal name="selfInfoForm" property="writeable" value="1">
					<input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="window.close();">
			</logic:equal>
				</td>
			</tr>
			<tr>
				<td height="10px"></td>
			</tr>
		</table>
		<%} else {%>
		<table width="80%" border="0" cellspacing="1" align="center" cellpadding="1">
			<br>
			<br>
			<tr>
				<td align="center" nowrap>
					<bean:message key="workbench.info.nomainfield" />
				</td>
				<td align="center" nowrap>
				  <logic:equal name="selfInfoForm" property="returnvalue" value="1">
					  <input type="button" name="returnbutton" value='<bean:message key="button.return"/>' class="mybutton" onclick="exeButtonAction('/workbench/info/editselfinfo.do?b_return=link','il_body')">
				  </logic:equal>
				   <logic:equal name="selfInfoForm" property="returnvalue" value="74">
                             <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body');trun();">                 
                        </logic:equal>
                        <logic:equal name="selfInfoForm" property="returnvalue" value="73">
                              <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body')">                 
                        </logic:equal>
                        <logic:equal name="selfInfoForm" property="returnvalue" value="75">
                               <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/system/warn/result_manager.do?b_query2=link','il_body')">                 
                        </logic:equal>
				</td>
			</tr>
		</table>
		<%}%>
		<div id="date_panel">
			<select name="date_box" multiple="multiple" size="10" style="width:200" onchange="setSelectValue();">
				<option value="1992.4.12">
					1992.4.12
				</option>
				<option value="1992.4">
					1992.4
				</option>
				<option value="1992">
					1992
				</option>
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
   if(dobj.type=="text"){
   	 dobj.focus();
   	 return;
   }}}
</script>
</body>
<div id=dict style="border-style:nono">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">    
     <tr>
     <td>
       <select name="dict_box" multiple="multiple" size="10" class="dropdown_frame" style="width:200" ondblclick="setSelectCodeValue();" ondblclick="setSelectCodeValue();" onkeydown="return inputType(this,event)" onblur="Element.hide('dict');">    
       </select>
     </td>
     </tr>
</div>
<script language="javascript">
reloadMenu("${selfInfoForm.a0100}","${selfInfoForm.setname}",'${selfInfoForm.actiontype}');
</script>  
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   __t.type="custom";
   __t.path="/general/muster/select_code_tree.do";
   __t.readFields="codeitemid";
   __t.cachable=true;__t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   var dropDownList=createDropDown("dropDownList");
   var _list=dropDownList;
   _list.type="list";
   _list.tag="";
   _array_dropdown[_array_dropdown.length]=_list;
   initDropDown(_list);   
</script>
<script language="javascript">
  initDocument();
</script> 
<logic:notEmpty name="selfInfoForm" property="msg">
<script language='javascript'>
alert('<bean:write  name="selfInfoForm" property="msg"/>');
</script>
</logic:notEmpty> 
<script language="javascript">
   Element.hide('date_panel');
   Element.hide('dict');
   getfirstfocuse();
   function getfirstfocuse(){
   var objsss=document.getElementsByTagName("input");
   
   for(var i=0;i<objsss.length;i++){
   var dobj=objsss[i];
   if(dobj.type=="text"&&dobj.className!="editor"){
   	 dobj.focus();
   	 return;
   }}}
   	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}}
    var valueInputsun=document.getElementsByName(orgtemp);
    if(valueInputsun && valueInputsun.length>0 && document.getElementById("deptId"))
        document.getElementById("deptId").setAttribute("parentid",valueInputsun[0].value);
    var valueInputsum=document.getElementsByName(postemp);
    if(valueInputsum && valueInputsum.length>0){
    	if( document.getElementById("jobId")){
	        if(valueInputsum[0].value)  
	            document.getElementById("jobId").setAttribute("parentid",valueInputsum[0].value);
	        else
	            document.getElementById("jobId").setAttribute("parentid",valueInputsun[0].value);      
    	}
    }
</script>
