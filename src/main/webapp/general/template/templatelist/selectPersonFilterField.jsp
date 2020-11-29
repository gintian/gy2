<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript">
<!--



function bankdisk_isClose(){
var sql ="${templateListForm.filterStr}";
var isclose="${templateListForm.issave}";
var conid="${templateListForm.filterCondId}";
var description ="${templateListForm.description}"; 
var obj=new Object();

if(parseInt(isclose)==1)
{
   obj.isclose=isclose;
   obj.sql=sql; 
   obj.condid=conid;
   returnValue=obj;
  window.close();
}else if(parseInt(isclose)==2){
  alert(description);
}

}
var date_desc;
function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
function showDateSelectBox(srcobj)
   {
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
   }
   function bankdisk_savecond(str)
   {
      var one=document.getElementById("hz0");
      var desc=one.innerHTML;
      var two=document.getElementsByName("personFilterList["+0+"].oper");
      var oper=two[0].value;
      var three=document.getElementsByName("personFilterList["+0+"].hzvalue");
      if(three==null||three.length==0)
      {
         three=document.getElementsByName("personFilterList["+0+"].value");
      }
     var value=three[0].value;
     var theArr=new Array(desc,oper,value); 
     var thecodeurl ="/gz/gz_accountingt/bankdisk/personFilter.do?br_input=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth:350px; dialogHeight:190px;resizable:no;center:yes;scroll:yes;status:no");
     
     //var name=window.prompt(str,desc+oper+value);
     if(return_vo==null)
     {
       return;
     }
     var nameobj=new Object();
     nameobj.name=return_vo.name;
     name = nameobj.name;
     if(trim(name).length==0)
     {
         alert(GZ_BANKDISK_INFO5);
         return;
     }
//     templateListForm.condName.value=name;
     templateListForm.action="/gz/gz_accountingt/bankdisk/personFilter.do?b_save=save";
     templateListForm.submit();
      }
function checkExpr(type,size)
{
  
 <%int t=0;%> 
  <logic:iterate id="element" name="templateListForm" property="personFilterList" indexId="index"> 
	   	      	    
             <logic:equal name="element" property="fieldtype" value="N">
               var a<%=t%>=document.getElementsByName("personFilterList[<%=t%>].value");
               if(a<%=t%>[0].value !=''){
                  var myReg =/^(-?\d+)(\.\d+)?$/
	        	  if(!myReg.test(a<%=t%>[0].value)) 
	        	   {
	            	    alert("<bean:write  name="element" property="hz"/>"+GZ_BANKDISK_INFO4+"!");
	            	    return;
	         	   }
         		}
		</logic:equal>
             
        
	         
        <%t++;%>
 </logic:iterate>
    var expr = templateListForm.expr.value;
    var hashvo=new ParameterSet();
    hashvo.setValue("expr",expr);
    hashvo.setValue("size",size);
    hashvo.setValue("type",type);
   	var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:"0570040033"},hashvo);
} 
function check_ok(outparameters)
{
   var type=outparameters.getValue("type");
   var info=outparameters.getValue("info");
   if(info=='0')
   {
     
      if(type=='2')
      {
         templateListForm.action="/general/template/personFilterResult.do?b_query=query";
		templateListForm.submit();
      }
   }
   else
   {
     alert(info);
      return;
   }
}
function insertText(strtxt)
{
   var expr_editor=$("expr");
   expr_editor.focus();
   var element = document.selection;
   if (element!=null) 
   {
    var rge = element.createRange();
		if (rge!=null)	
	        rge.text=strtxt;
   }
}
function check()
{
    var code=window.event.keyCode;
    //106.109.107
    var ret=true;
    if(code==106||code==109||code==107)
    {
    }
    else if(code==8||code==46)
    {
    }
   else if(97<=code&&code<=105)
   {
      
   }else if(48<=code&&code<=57)
   {
   }
   else
   { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else
        {
           window.event.returnValue=false;
        }
     
   }
}
function ctrlKey()
{
    var code =window.event.keyCode;
    if((window.event.shiftKey)&&code==222)
    {
        window.event.returnValue=false;
    }
}
function logicChange(n,obj){
    this.logicArr[n]=obj.value;
}
function hiddendiv(obj){
	var div = document.getElementById("date_panel"); 
    var x=event.clientX; 
    var y=event.clientY; 
    var divx1 = div.offsetLeft; 
    var divy1 = div.offsetTop; 
    var divx2 = div.offsetLeft + div.offsetWidth; 
    var divy2 = div.offsetTop + div.offsetHeight;
   	if( x < divx1 || x > divx2 || y < divy1 || y > divy2){ 
   	 Element.hide('date_panel');
       }
      
}
function showorno(obj){
	var x=event.clientX; 
    var y=event.clientY; 
	var act = document.activeElement.id;
	if(act!="datatext"){
	    Element.hide('date_panel');  	
	}
}
//-->
</script>

<%
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag="";
	if(userView != null)
	{
	    bosflag = userView.getBosflag();
	}
    
%>
<%	    if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<html:form action="/general/template/personFilter">
<%	    if (!"hcm".equals(bosflag)){	   
%>
<br>
<%} %>
	<table width="590" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable" >
		<tr height="20">
			<!--	<td width=1 valign="top" class="tableft1"></td>
       		 <td width=130 align=center class="tabcenter"><bean:message key="gz.bankdisk.querycondition"/></td>
       		 <td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="700"></td>    -->

			<td align="left" colspan="4" class="TableRow" >				
				<bean:message key="gz.bankdisk.querycondition" />				
			</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle" align="center" nowrap>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable"
					style="border-right: medium none;">
					<thead>
						<tr>
						<tr>
							<td align="center" class="TableRow" style="border-top:0;border-right:0;border-left:0;">
								<bean:message key="gz.bankdisk.sequencenumber"/>
							</td>
							<td align="center" class="TableRow" style="border-top:0;border-right:0;">
								<bean:message key="gz.bankdisk.queryfield" />
							</td>
							<td align="center" class="TableRow" style="border-top:0;border-right:0;">
								<bean:message key="gz.bankdisk.relationcharacter" />
							</td>
							<td align="center" class="TableRow" style="border-top:0;border-right:0;">
								<bean:message key="gz.bankdisk.queryvalue" />
							</td>
						</tr>
					</thead>
					<%
					    int i = 0;
					%>
					<logic:iterate id="element" name="templateListForm"
						property="personFilterList" indexId="index">
						<tr>
							<td align="center" class="RecordRow"
								style="BORDER-LEFT: #C4D8EE 0pt solid;" nowrap>
								<%=i + 1%>
							</td>
							<td align="center" id=<%="hz" + i%> class="RecordRow" nowrap>
								<bean:write name="element" property="hz" />
							</td>
							<td align="center" class="RecordRow" nowrap>
								<html:select name="templateListForm"
									property='<%="personFilterList[" + i + "].oper"%>' size="1">
									<html:optionsCollection property="operlist" value="dataValue"
										label="dataName" />
								</html:select>
							</td>
							<!--日期型 -->
							<logic:equal name="element" property="fieldtype" value="D">
								<td align="left" class="RecordRow"
									style="BORDER-right: #C4D8EE 0pt solid;" nowrap>
									<html:text styleId="datatext" name="templateListForm"
										property='<%="personFilterList[" + i + "].value"%>' size="40"
										ondblclick="showDateSelectBox(this);" onblur="hiddendiv(this)"
										styleClass="text4" />
									<!--  -->
									<!--   <html:text name="templateListForm" property='<%="personFilterList[" + i + "].value"%>'  size="40"  maxlength="${element.itemlen}" ondblclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');"/>         
-->
								</td>
							</logic:equal>
							<!--字符型 -->
							<logic:equal name="element" property="fieldtype" value="A">
								<td align="left" class="RecordRow"
									style="BORDER-right: #C4D8EE 0pt solid;" nowrap>
									<logic:notEqual name="element" property="codeid" value="0">
										<logic:equal name="element" property="codeid" value="UN">
											<html:hidden name="templateListForm"
												property='<%="personFilterList[" + i + "].value"%>' />
											<html:text name="templateListForm"
												property='<%="personFilterList[" + i + "].hzvalue"%>' size="40"
												maxlength="${element.itemlen}" onchange="fieldcode(this,2)"
												onkeydown="ctrlKey();" readonly="true" styleClass="text4" />
											<span style="vertical-align: bottom"><img src="/images/code.gif" align="absMiddle"
												onclick="openCondCodeDialogsx('${element.codeid}','<%="personFilterList[" + index + "].hzvalue"%>','0');" /></span>
										</logic:equal>
										<logic:equal name="element" property="codeid" value="UM">
											<html:hidden name="templateListForm"
												property='<%="personFilterList[" + i + "].value"%>' />
											<html:text name="templateListForm"
												property='<%="personFilterList[" + i + "].hzvalue"%>' size="40"
												maxlength="${element.itemlen}" onchange="fieldcode(this,2)"
												onkeydown="ctrlKey();" readonly="true" styleClass="text4" />
											<span style="vertical-align: bottom"><img src="/images/code.gif" align="absMiddle"
												onclick="openCondCodeDialogsx('${element.codeid}','<%="personFilterList[" + index + "].hzvalue"%>','0');" /></span>
										</logic:equal>
										<logic:equal name="element" property="codeid" value="@K">
											<html:hidden name="templateListForm"
												property='<%="personFilterList[" + i + "].value"%>' />
											<html:text name="templateListForm"
												property='<%="personFilterList[" + i + "].hzvalue"%>' size="40"
												maxlength="${element.itemlen}" onchange="fieldcode(this,2)"
												onkeydown="ctrlKey();" readonly="true" styleClass="text4" />
											<span style="vertical-align: bottom"><img src="/images/code.gif" align="absMiddle"
												onclick="openCondCodeDialogsx('${element.codeid}','<%="personFilterList[" + index + "].hzvalue"%>','0');" /></span>
										</logic:equal>
										<logic:notEqual name="element" property="codeid" value="UN">
											<logic:notEqual name="element" property="codeid" value="UM">
												<logic:notEqual name="element" property="codeid" value="@K">
													<html:hidden name="templateListForm"
														property='<%="personFilterList[" + i + "].value"%>' />
													<html:text name="templateListForm"
														property='<%="personFilterList[" + i + "].hzvalue"%>'
														size="40" maxlength="${element.itemlen}"
														onchange="fieldcode(this,1)" onkeydown="ctrlKey();"
														readonly="true" styleClass="text4" />
													<span style="vertical-align: bottom"><img src="/images/code.gif" align="absMiddle"
														onclick='openCondCodeDialog("${element.codeid}","<%="personFilterList[" + i + "].hzvalue"%>");' /></span>
												</logic:notEqual>
											</logic:notEqual>
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="codeid" value="0">
										<html:text name="templateListForm"
											property='<%="personFilterList[" + i + "].value"%>' size="40"
											maxlength='${element.itemlen}' onkeydown="ctrlKey();"
											styleClass="text4" />
									</logic:equal>
								</td>

							</logic:equal>
							<!--数据值-->
							<logic:equal name="element" property="fieldtype" value="N">
								<td align="left" class="RecordRow"
									style="BORDER-right: #C4D8EE 0pt solid;" nowrap>
									<html:text name="templateListForm"
										property='<%="personFilterList[" + i + "].value"%>' size="40"
										maxlength='${element.itemlen}' onkeydown="ctrlKey();"
										styleClass="text4" />
								</td>
							</logic:equal>
							<!--备注型-->
							<logic:equal name="element" property="fieldtype" value="M">
								<td align="left" class="RecordRow"
									style="BORDER-right: #C4D8EE 0pt solid;" nowrap>
									<html:text name="templateListForm"
										property='<%="personFilterList[" + i + "].value"%>' size="40"
										maxlength="${element.itemlen}" onkeydown="ctrlKey();"
										styleClass="text4" />
								</td>
							</logic:equal>


						</tr>

						<%
						    i++;
						%>
					</logic:iterate>
				
					<tr>
						<td align="left" colspan="4" class="RecordRow" 
							style="BORDER-left: 0pt solid; BORDER-right: 0pt solid;" nowrap>
							 <span><bean:message key="gz.bankdisk.factorexpression" /></span><br>
							<html:textarea name="templateListForm" property="expr" rows="5"
								cols="90" onkeydown="check(); "  style="margin-left:0px;margin-bottom:2px"></html:textarea>
						</td>
					</tr>
						<td colspan="4" align="left" class="RecordRow" nowrap
							style="height: 35px; BORDER-left: 0pt solid; BORDER-right: 0pt solid;">
							&nbsp;
							<input type="button"
								value="<bean:message key="gz.bankdisk.moveover"/>" name=""
								class="mybutton" onclick="insertText('*');" />
							<input type="button" value="<bean:message key="gz.bankdisk.or"/>"
								name="" class="mybutton" onclick="insertText('+');" />
							<input type="button"
								value="<bean:message key="gz.bankdisk.not"/>" name=""
								class="mybutton" onclick="insertText('!');" />
							<input type="button" value="(" name="" class="mybutton"
								onclick="insertText('(');" />
							<input type="button" value=")" name="" class="mybutton"
								onclick="insertText(')');" />
						</td>
					</tr>

					<tr>
						<td colspan="4" align="center" class="RecordRow"
							style="height: 35px; BORDER-bottom: 0pt solid; BORDER-left: 0pt solid; BORDER-right: 0pt solid;">
							<hrms:submit styleClass="mybutton" property="br_return">
								<bean:message key="button.query.pre" />
							</hrms:submit>
							<input type="button" class="mybutton" name="query"
								value="<bean:message key="button.ok"/>"
								onclick="checkExpr('2','<%=i%>');" />

							<input type="button" class="mybutton" name="cancel"
								value="<bean:message key="button.cancel"/>"
								onclick="window.close();" />
							<input type="hidden" name="tabid"
								value="${templateListForm.tabid}" />
							<input type="hidden" name="rightFields"
								value="${templateListForm.rightFields}" />

						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div id="date_panel" onblur="showorno();">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();" onblur="showorno();">    
			    <option value="$YRS[10]"><bean:message key="gz.bankdisk.yearlimit"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentyear"/>"><bean:message key="gz.bankdisk.currentyear"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentmonth"/>"><bean:message key="gz.bankdisk.currentmonth"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentday"/>"><bean:message key="gz.bankdisk.currentday"/></option>					    
			    <option value="<bean:message key="gz.bankdisk.today"/>"><bean:message key="gz.bankdisk.today"/></option>
			    <option value="<bean:message key="gz.bankdisk.stopdate"/>"><bean:message key="gz.bankdisk.stopdate"/></option>
                <option value="1992.4.12">1992.4.12</option>	
                <option value="1992.4">1992.4</option>	
                <option value="1992">1992</option>			    
			    <option value="????.??.12">????.??.12</option>
			    <option value="????.4.12">????.4.12</option>
			    <option value="????.4">????.4</option>			    			    		    
                        </select>
                    </div>
</html:form>
<script type="text/javascript">
<!--
 bankdisk_isClose();
 Element.hide('date_panel');
//-->
</script>
