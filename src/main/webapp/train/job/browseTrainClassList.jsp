<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainClassBo"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant,com.hjsj.hrms.utils.ResourceFactory" %>
<style>
.TableRow_twoRows {
    background-image:url(../../images/listtableheaderm.jpg);
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #94B6E6; 
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:20;
	font-weight: bold;	
	valign:middle;
}

 .TableRow {

	background-position : center left;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;

}
</style>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>

<script language="javascript">
    var ViewProperties = new ParameterSet();
	
	function operate(operator,r3101,a0100,dbpre)
	{
		
		var hashvo=new ParameterSet();
		hashvo.setValue("r3101",r3101);
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("dbpre",dbpre);
		var In_paramters="operator="+operator;  
		if(operator=='del')
		{
			if(!confirm(WITHDRAWAL_TRAIN_JOB_APP_CONFIRM))
			{
				return;
			}
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'2020020202'},hashvo);
		}
		if(operator=='add'){
		var hash=new ParameterSet();
		hash.setValue("classid",r3101);
		hash.setValue("personstr","");
		hash.setValue("msg","2");
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returns,functionId:'2020040012'},hash);
			function returns(outparamters){
				var flag=outparamters.getValue("flag");
				if(flag=="true"){
					var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'2020020202'},hashvo);
				}else{
					alert(flag);
				}
			}
		}
	}

	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
		alert(info);
		trainClassForm.action="/train/job/browseTrainClassList.do?b_query=link";
		trainClassForm.submit();
	}
	
	function showR4015(descId){
		if(descId!="-1"){
			document.getElementById("r4015desc").innerHTML=document.getElementById("r4015"+descId).value;
			document.getElementById("r4015Hidden").style.display='none';
			document.getElementById("r4015Show").style.display='';
		}else{
			document.getElementById("r4015Hidden").style.display='';
			document.getElementById("r4015Show").style.display='none';
		}
	}
	 function openw(url){
		var ww = 800;
		var hh = 600;
		window.open(url, "_new","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no,top=0,left=0,width="+ww+",height="+hh);
		   
	}
	function eventDesc(id,readonly){
		var code ="/train/job/browseTrainClassList.do?b_event=link`id=r3117`read="+readonly+"`flag=add`classid="+id;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+code;
		openw(iframe_url);     
	}
	function search(flag){
	   var classname=document.getElementById("classname").value;
	   classname = getEncodeStr(classname);
	   trainClassForm.action="/train/job/browseTrainClassList.do?b_query=link&classname="+$URL.encode(classname);
	   trainClassForm.submit();
   }

</script>
<hrms:themes/>
<div id="r4015Hidden">
<html:form action="/train/job/browseTrainClassList">
<%int i=0;
  int m = -1;
  String r3101 = "";
  boolean flag = true;
%>
<table>
<tr>
<td>&nbsp;<bean:message key="train.job.trainClassName"/>&nbsp;</td><td><html:text name="trainClassForm" property="classname" styleClass="TEXT4" styleId="classname"/></td>
<td><input type="button" class="mybutton" value='<bean:message key="button.query"/>' onclick="search('2')"></td>
</tr>
</table>
<table width="100%" border="0" cellspacing="0"  cellpadding="0" class="ListTable">
		<thead>
				<tr>
					<logic:iterate id="element" name="trainClassForm" property="list"
						indexId="index">
						<logic:equal name="element" property="itemid" value="r3130">
							<td align="center" class="TableRow" rowspan="2" nowrap>
								&nbsp;
								<bean:write name="element" property="itemdesc" filter="true" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" rowspan="2" nowrap>
								<bean:message key="hire.interviewExamine.description" />
							</td>
						</logic:equal>
						<logic:equal name="element" property="itemid" value="r4013">
							<hrms:priv func_id="090601">
								<td align="center" class="TableRow" nowrap colspan="2">
									<bean:message key="conlumn.infopick.educate.enterfor" />
								</td>
							</hrms:priv>
							<td align="center" class="TableRow" rowspan="2" nowrap>
								&nbsp;
								<bean:write name="element" property="itemdesc" filter="true" />
								&nbsp;
							</td>
						</logic:equal>
						<logic:notEqual name="element" property="itemid" value="r3101">
							<logic:notEqual name="element" property="itemid" value="r3130">
								<logic:notEqual name="element" property="itemid" value="r4013">
									<td align="center" class="TableRow" rowspan="2" nowrap>
										&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
										&nbsp;
									</td>
								</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
					</logic:iterate>
				</tr>
				<hrms:priv func_id="090601">
					<tr>
						<td align="center" class="TableRow" nowrap>
							&nbsp;&nbsp;
							<bean:message key='button.aplly' />
							&nbsp;&nbsp;
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key='workdiary.message.withdraw.application' />
						</td>
					</tr>
				</hrms:priv>
			</thead>
          <hrms:extenditerate id="element" name="trainClassForm" property="trainClassListForm.list" indexes="indexes"  pagination="trainClassListForm.pagination" pageCount="20" scope="session">
        	<bean:define id="nid" name='element' property='r3101'/>
          <logic:iterate id="fielditem"  name="trainClassForm"  property="list" indexId="index">
            <%
              	if(i%2==0 && m != i){%>
                <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'');">
              <%}else if(m != i) {%>
                <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'');">
              <%} 
               m = i;%>  
            
        	<logic:equal name="fielditem" property="itemid" value="r3130">
             <td align="left" class="RecordRow" >
              	&nbsp;<bean:write name="element" property="r3130"/>
              </td>  
             <td align="center" class="RecordRow" >
             	<a href="/train/job/browseTrainClassList.do?b_queryDesc=query&operator=1&r3101=${nid}" >    
                 <img src="/images/view.gif" border=0>    
             	</a>
             </td>
             </logic:equal>
			 <logic:notEqual name="fielditem" property="itemid" value="r3130">
			 <logic:notEqual name="fielditem" property="itemid" value="r3101">
			 <logic:notEqual name="fielditem" property="itemid" value="r4013">
			 <logic:notEqual name="fielditem" property="itemid" value="r4015">
			 <logic:notEqual name="fielditem" property="itemid" value="r3117">
		     <logic:notEqual name="fielditem" property="itemid" value="r3125">
			 	<logic:notEqual name="fielditem" property="codesetid" value="0">
					 <td align="left" class="RecordRow" nowrap>
							 <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" />
								 &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
					 </td>
				</logic:notEqual>
				<logic:equal name="fielditem" property="codesetid" value="0">
					<logic:equal name="fielditem" property="itemtype" value="N">
						<td align="right" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false" />&nbsp;
						</td>
					</logic:equal>
					<logic:notEqual name="fielditem" property="itemtype" value="N">
					<logic:equal name="fielditem" property="itemtype" value="M">
					<bean:define id="itemid" value="${fielditem.itemid}"></bean:define>
					<bean:define id="fieldsetid" value="${fielditem.fieldsetid}"></bean:define>
					<%
           			UserView userViews=(UserView)session.getAttribute(WebConstant.userView);
           			String value = QuestionesBo.checkDate(itemid.toString(),fieldsetid.toString(),nid.toString(),"",""); %>
						<td align="left" class="RecordRow" style="width: 100px;" nowrap title="<%=value %>">
							<div STYLE="width: 100px; overflow: hidden; text-overflow: ellipsis; white-space:nowrap;">
								 &nbsp;<%=value %>&nbsp;
							</div>
						</td>
					</logic:equal>
					<logic:notEqual name="fielditem" property="itemtype" value="M">
						<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="${fielditem.itemid}" filter="false" /> &nbsp;
						</td>
					</logic:notEqual>
					</logic:notEqual>
				</logic:equal>
			</logic:notEqual>
			<logic:equal name="fielditem" property="itemid" value="r3125">
		            <td align="left" class="RecordRow" nowrap> 
		             	<bean:define id="r3125" name="element" property="${fielditem.itemid}" />
		                   &nbsp;<%=TrainClassBo.getPlanName(r3125.toString()) %>&nbsp;
		    </logic:equal>
			</logic:notEqual>
			<logic:equal name="fielditem" property="itemid" value="r3117">
			      	<td align="center" class="RecordRow" >
			      	<img src="/images/view.gif" onclick="eventDesc('${nid}','0')" border="0"  style="cursor:hand;">
			      	</td>
			      </logic:equal>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			<logic:equal name="fielditem" property="itemid" value="r4013">
		  <hrms:priv func_id="090601"> 
		  <bean:define id="r4013" name="element" property="r4013"/>
          <td align="center" class="RecordRow" >
          	<logic:equal name="element" property="isOverTime" value="1">
          	<logic:equal name="element" property="studentsCount" value="1">
             	<logic:equal name="element" property="r4013" value="">
             		<input type='button' value="<bean:message key='button.aplly'/>"  class="mybutton"   onclick='operate("add","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             	<logic:equal name="element" property="r4013" value="0">
             		<input type='button' value="<bean:message key='button.aplly'/>"  class="mybutton"   onclick='operate("add","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             	<logic:equal name="element" property="r4013" value="07">
             		<input type='button' value="<bean:message key='button.aplly'/>"  class="mybutton"   onclick='operate("add","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             	<logic:notEqual name="element" property="r4013" value="">
             	<logic:notEqual name="element" property="r4013" value="0">
             	<logic:notEqual name="element" property="r4013" value="01">
             	<logic:notEqual name="element" property="r4013" value="07">
             	
             		<bean:message key="workdiary.message.has.applied"/>
             	
             	</logic:notEqual>
             	</logic:notEqual>
             	</logic:notEqual>
             	</logic:notEqual>
             </logic:equal>
             <logic:equal name="element" property="studentsCount" value="0">
                <bean:message key="train.job.class.studentsCount"/>
             </logic:equal>
          </logic:equal>
          <logic:equal name="element" property="isOverTime" value="2">
             	<bean:message key="train.job.class.startTime"/>
           </logic:equal>
           <logic:equal name="element" property="isOverTime" value="3">
             	<bean:message key="train.job.class.endTime"/>
            </logic:equal>
             </td>
             <td align="center" class="RecordRow" >
             <logic:equal name="element" property="isOverTime" value="1">
             	<logic:equal name="element" property="r4013" value="02">
             		    <input type='button' value="<bean:message key='workdiary.message.withdraw.application'/>"  class="mybutton"   onclick='operate("del","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             	<logic:equal name="element" property="r4013" value="03">
             		    <input type='button' value="<bean:message key='workdiary.message.withdraw.application'/>"  class="mybutton"   onclick='operate("del","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             	<logic:equal name="element" property="r4013" value="08">
             		     <input type='button' value="<bean:message key='workdiary.message.withdraw.application'/>"  class="mybutton"   onclick='operate("del","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             	<logic:equal name="element" property="r4013" value="07">
             			<input type='button' value="<bean:message key='workdiary.message.withdraw.application'/>"  class="mybutton"   onclick='operate("del","${nid}","${trainClassForm.a0100}","${trainClassForm.dbname}")' >
             	</logic:equal>
             </logic:equal>
             </td> 
             
             </hrms:priv>
             <td align="center" class="RecordRow" >
             		<hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" />
								 &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
             </td> 
             </logic:equal>
             
             <logic:equal name="fielditem" property="itemid" value="r4015">
             <td align="center" class="RecordRow" >
           		<logic:notEqual value="" name="element" property="r4015">
           			<%
           			UserView userViews=(UserView)session.getAttribute(WebConstant.userView);
           			String value = QuestionesBo.checkDate("r4015","r40",userViews.getA0100(),"",nid.toString()); %>
           			<input type="hidden" name="r4015<%=i %>" id="r4015<%=i %>" value='<%=value %>' />
           			<a href="javascript:showR4015('<%=i %>');"><img src="/images/view.gif" border=0></a>
           		</logic:notEqual>
             </td>
              </logic:equal>
             </logic:iterate>
             <%i++; %>
          </tr>
          </hrms:extenditerate>
</table>
<table width="100%" cellpadding="0" cellspacing="0" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
			 <bean:message key="label.page.serial"/>
			 <bean:write name="trainClassForm" property="trainClassListForm.pagination.current" filter="true" />
    		 <bean:message key="label.page.sum"/>
		     <bean:write name="trainClassForm" property="trainClassListForm.pagination.count" filter="true" />
		     <bean:message key="label.page.row"/>
		     <bean:write name="trainClassForm" property="trainClassListForm.pagination.pages" filter="true" />
		     <bean:message key="label.page.page"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="trainClassForm" property="trainClassListForm.pagination"
                  		nameId="trainClassListForm" propertyId="trainClassListProperty">
         			</hrms:paginationlink>

			</td>
		</tr>
	</table>	
</html:form>
</div>
<table id="r4015Show" class="ListTable" style="display: none;width: 400px;height: 200px;" cellpadding="0" cellspacing="0" align="center">
	<tr><td><br/></td></tr>
	<tr>
		<td class="TableRow" align="center"><bean:message key="kq.register.overrule"/></td>
	</tr>
	<tr>
		<td class="RecordRow">
		<div id="r4015desc" style="width: 100%;height: 160px;padding: 10px;">
			<bean:message key="kq.register.overrule"/>
		</div>
		</td>
	</tr>
	<tr>
		<td align="center" style="padding: 8px;"><input type='button' value="<bean:message key='button.return'/>"  class="mybutton"   onclick="showR4015('-1');" ></td>
	</tr>
</table>