<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.general.inform.search.SearchInformForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.util.*"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();  
	SearchInformForm form=(SearchInformForm)session.getAttribute("searchInformForm");
    /**
    * 由先前的按人员管理范围控制改成按如规则进行控制
    * 人员、单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
    * cmq changed at 2012-09-29
    */	
	if(form.getType().equalsIgnoreCase("1")||form.getType().equalsIgnoreCase("2")||form.getType().equalsIgnoreCase("3"))
	{
		manager=userView.getUnitIdByBusi("4");
	}
	//end.
    
    /**
    * 计件薪资 单位和岗位按业务范围-操作单位-人员管理范围优先级进行控制 
    * wangrd changed at 2013-01-10
    */	
	if(form.getType().equalsIgnoreCase("6"))
	{
		manager=userView.getUnitIdByBusi("1");
	}
	//end.
	if(userView.isSuper_admin())
	    manager = "";
	
	ArrayList factorlist=form.getFactorlist();
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	
	Map<String , String[]> parameMap = request.getParameterMap();
	String inforflag = "false";
	if(parameMap != null && parameMap.get("inforflag") != null && parameMap.get("inforflag").length > 0 
	        && "2".equalsIgnoreCase((String)parameMap.get("inforflag")[0]))
	    inforflag = "true";
	String moduleFlag=(String)request.getParameter("moduleFlag");
	if(moduleFlag==null){
		moduleFlag="";
	}
%>
<script type="text/javascript">
	var moduleFlag="<%=moduleFlag%>";
	var mark ='<%=request.getParameter("mark")==null? "":request.getParameter("mark")%>';//自助服务/统计分析/ 设置统计范围  wangb 20180207
	var saveCallBack = '<%=request.getParameter("callback")%>';
	var manageCode = '<%=manager%>';
	function openCodeDialog(codeid,mytarget,managerstr,flag) 
    {
        var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
        if(mytarget==null)
          return;
        var oldInputs=document.getElementsByName(mytarget);
        oldobj=oldInputs[0];
        //根据代码显示的对象名称查找代码值名称	
        target_name=oldobj.name;
        hidden_name=target_name.replace("viewvalue","itemvalues");
        hidden_name=hidden_name.replace(".hzvalue",".value");
        hidden_name=hidden_name.replace("name1","namevalue"); 
        var hiddenInputs=document.getElementsByName(hidden_name);
        if(hiddenInputs!=null&&hiddenInputs.length>0)
        {
        	hiddenobj=hiddenInputs[0];
        	codevalue=managerstr;
        }else{
        	hiddenobj=document.getElementById(hidden_name);
        	codevalue=managerstr;
        }
        var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag);  
        if(codeid == "UN"){
            var type = ${searchInformForm.type };
            if(type == '1')
        	    thecodeurl="/org/orgpre/getorgcode.jsp?ctrl_type=2&levelctrl=0";
        	else
        	    thecodeurl="/org/orgpre/getorgcode.jsp?ctrl_type=1&levelctrl=0";
        }else
            thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
        var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        if (isIE6()) {
            dw=305;
            dh=420;
        }
        var popwin= window.showModalDialog(thecodeurl, theArr, 
            "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    }
</script>
<style type="text/css"> 
.strTable{
	border: 1px solid #eee;
	height: 240px;    
	width: 400px;            
	overflow: auto;    
	/*IE9中用overflow: auto时出现滚动条后需要点击一下才会显示，用overflow-y: scroll就没有问题*/
	overflow-y: scroll;      
	margin: 1em 1;
	/*position:absolute; *//*使用绝对定位 里面的样式有问题   */
}
</style>
<script language="JavaScript" src="./generalsearch.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language='JavaScript' src='/components/codeSelector/codeSelector.js'></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<hrms:themes />
<style>
.generalsearchTable{
	width:expression(document.body.clientWidth-10);
<%if("hl".equalsIgnoreCase(bosflag)){ %>
	margin-top:10px;
<%}%>
}

.x-btn-default-toolbar-small .x-frame-tl{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-tc{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-tr{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-bl{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-bc{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-br{
	background-color: #c5c5c5;
	background-image: none;
}
</style>
<html:form action="/general/inform/search/generalsearch">
<input type="hidden" name="itemid" id="itemid">
<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="generalsearchTable">
  <tr> 
    <td width="30%" height="260" align="center"> 
      <fieldset style="width:90%;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" style="padding-left:5px;padding-right:5px;">
        <tr> 
          <td height="27"> 
          <html:select name="searchInformForm" styleId="fieldid" property="fieldid" onchange="change();" style="width:100%"> 
            <html:optionsCollection property="setlist" value="dataValue" label="dataName" /> 
            </html:select> 
            </td>
        </tr>
        <tr> 
          <td height="220" style="padding-bottom:2px;"> 
            <select name="item_field" multiple="multiple" ondblclick="additemtr('item_field');" style="height:220px;width:100%;font-size:9pt">
            </select>
          </td>
        </tr>
      </table>
     </fieldset>
    </td>
    <td width="48px" rowspan="2" align="center">
    	<table width="100%" border="0" align="center">
        	<tr> 
          		<td height="60" style="padding-left: 15px" align="center"> 
            		<input type="button" name="Submit111" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" class="mybutton" style="margin-bottom: 20px;"> 
            		<input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='button.delete'/>" class="mybutton"> 
          		</td>
        	</tr>
      </table>
    </td>
   <td align="center" height="260" width="60%" valign="top">
   		<fieldset style="width:93%;">
      	<legend><bean:message key='general.inform.search.condset'/></legend>
     	<!-- 下面 div #strTable 要求宽度必须为400，否则定义条件界面错乱。此table必须设置不能小于400，否则下面盛不下div。 guodd 2018-03-26 -->
    	<table width="410" height="100%" border="0" style="margin-left: -2px"  align="center" cellpadding="0" cellspacing="0">
        	<tr> 
         		<td height="250" valign="top">
         			<div id="strTable" class="strTable common_border_color" style="margin-left:0px;margin-top:0px;">
         				<table width="100%" id="tablestr" border="0" cellpadding="0" cellspacing="0" style="border: 0px;">
         					<tr>
         						<td align="center" class="TableRow" width="15%" style="border-left: 0px;border-top:none;"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow" width="30%" nowrap style="border-left: 0px;border-top:none;"><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow" width="15%" style="border-left: 0px;border-top:none;"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow" nowrap style="border-left: 0px;border-right: 0px;border-top:none;"><bean:message key='label.query.value'/></td>
         					</tr>
         					<logic:equal name="searchInformForm" property="type" value="5">
         					<% int ww=0; %>
				      		 <logic:iterate id="element" name="searchInformForm"  property="factorlist" indexId="index">  
				      		 	<tr  onclick="onSelects('<%=ww %>_<bean:write name="element" property="fieldname" />');">
					      		 	<td align="center" class="RecordRow" nowrap style="border-left: 0px;border-top:none;">
					      		 	<%if(ww>0) {%>
						      		 		<select name="log" id='<%=ww %>_<bean:write name="element" property="fieldname" />_logic' name="<%=ww %>_<bean:write name="element" property="fieldname" />_logic" onchange='logicChange(<%=ww %>,this);' size="1">                              	
												<option value="*">且</option>
				                               	<option value="+">或</option>
			                               	</select>
			                               	<script type="text/javascript">
			                               		var selected="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].log"%>' />";
			                               		//document.getElementById('${index }log').value=selected;
			                               		var _options=document.getElementById('<%=ww %>_<bean:write name="element" property="fieldname" />_logic').options;
			                               			if("*"==selected){
			                               				_options[0].selected=true;
			                               			}
			                               			if("+"==selected){
			                               				_options[1].selected=true;
			                               			}
			                               </script>
		                               <%}else{ %>
		                               		&nbsp;
		                               <%} %>
					      		 	</td>
					                <td align="center" class="RecordRow" nowrap style="border-left: 0px;border-top:none;">
					                    <bean:write name="element" property="hz" />&nbsp;
					                    <input type="hidden" name="itemid" id='itemid' value="<bean:write name="element" property="fieldname" />" >
					                 </td>
					                 <td align="center" class="RecordRow" nowrap style="border-left: 0px;border-top:none;">
			                               <select  id='<%=ww %>_<bean:write name="element" property="fieldname" />_eq' name="<%=ww %>_<bean:write name="element" property="fieldname" />_eq" size="1" onchange="eqChange(this)">
				                                <option value="=">=</option>
				                               	<option value="&gt;">&gt;</option>
												<option value="&gt;=">&gt;=</option>
												<option value="&lt;">&lt;</option>
												<option value="&lt;=">&lt;=</option>
												<option value="&lt;&gt;">&lt;&gt;</option>  
			                               </select>
			                               
			                               <script type="text/javascript">
			                               		idArr[<%=ww%>]='<bean:write name="element" property="fieldname" />';
			                               		<% if(ww==0){%>
			                               				logicArr[<%=ww%>]="";
			                               		<% }else{%>
			                               				this.logicArr[<%=ww%>]='<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].log"%>' />';
			                               		<%}%>
			                               		eqArr[<%=ww%>]="<bean:write name="searchInformForm" property='<%= "factorlist["+ww+"].oper"%>' />";
			                               		descArr[<%=ww%>]=' <bean:write name="element" property="hz" />';
			                               		typeArr[<%=ww%>]=' <bean:write name="element" property="fieldtype" />';
			                               		codeArr[<%=ww%>]='<bean:write name="element" property="codeid" />';
			                               		var selected="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].oper"%>' />";
			                               		var _options=document.getElementById('<%=ww %>_<bean:write name="element" property="fieldname" />_eq').options;
			                               			if("="==selected){
			                               				_options[0].selected=true;
			                               			}
			                               			if("&gt;"==selected){
			                               				_options[1].selected=true;
			                               			}
			                               			if("&gt;="==selected){
			                               				_options[2].selected=true;
			                               			}
			                               			if("&lt;"==selected){
			                               				_options[3].selected=true;
			                               			}
			                               			if("&lt;="==selected){
			                               				_options[4].selected=true;
			                               			}
			                               			if("&lt;&gt;"==selected){
			                               				_options[5].selected=true;
			                               			}
			                               </script>
			                               
			                          </td>
			                          <!--日期型 -->                            
			                          <logic:equal name="element" property="fieldtype" value="D">
			                            <td align="left" class="RecordRow" nowrap style="border-left: 0px;border-right: 0px;border-top:none;">              
			                            	<input type="text" value="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].value"%>' />" size="30" maxlength="30" name="<%=ww %>_<bean:write name="element" property="fieldname" />.value"  id='<%=ww %>_<bean:write name="element" property="fieldname" />.value' class="text4" ondblclick="showDateSelectBox(this);" />
			                            </td>                           
			                          </logic:equal>
			                          <logic:equal name="element" property="fieldtype" value="M">
			                            <td align="left" class="RecordRow" nowrap style="border-left: 0px;border-right: 0px;border-top:none;">             
			                            	<input type="text" value="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].value"%>' />" size="30" maxlength='<%="factorlist["+ww+"].itemlen"%>' name="<%=ww %>_<bean:write name="element" property="fieldname" />.value"  id='<%=ww %>_<bean:write name="element" property="fieldname" />.value' class="text4" />
			                            </td>                           
			                          </logic:equal>
			                            <!--字符型 -->                                                    
			                          <logic:equal name="element" property="fieldtype" value="A">
			                            <td align="left" class="RecordRow" nowrap style="border-left: 0px;border-right: 0px;border-top:none;">
			                              <logic:notEqual name="element" property="codeid" value="0">
			                                <input type="hidden" value="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].value"%>' />" name="<%=ww %>_<bean:write name="element" property="fieldname" />.value" id='<%=ww %>_<bean:write name="element" property="fieldname" />.value' class="text4"/>
			                                <input type="text" onblur="hzvalueTovalue('<%=ww %>_<bean:write name="element" property="fieldname" />');" value="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].hzvalue"%>' />" name="<%=ww %>_<bean:write name="element" property="fieldname" />.hzvalue" id="<%=ww %>_<bean:write name="element" property="fieldname" />.hzvalue" style="width:120px" styleClass="text4">
                                              <logic:equal name="element" property="fieldname" value="b0110">
                                              		<!-- xus 19/12/16 【55260】V7.6.2绩效管理：考核实施，自动分配考核主体，设置条件保存后，在打开，查询图标跑上边了 -->
                                                      <img src="/images/code.gif" style="vertical-align: text-top;" onclick='openCodeDialog("UN","<%=ww %>_<bean:write name="element" property="fieldname" />.hzvalue","<%=manager %>",1);'/>
			                                         </logic:equal>
			                                         <logic:notEqual name="element" property="fieldname" value="b0110">   
			                                         	<logic:equal name="element" property="fieldname" value="e0122"> 
			                                         	<!-- xus 19/12/16 【55260】V7.6.2绩效管理：考核实施，自动分配考核主体，设置条件保存后，在打开，查询图标跑上边了 -->
			                                           		<img src="/images/code.gif" style="vertical-align: text-top;" onclick='openInputCodeDialogOrgInputPos("UM","<%=ww %>_<bean:write name="element" property="fieldname" />.hzvalue","<%=manager%>",1);'/>
			                                         	</logic:equal>
			                                         	<logic:notEqual name="element" property="fieldname" value="e0122"> 
			                                         	<!-- xus 19/12/16 【55260】V7.6.2绩效管理：考核实施，自动分配考核主体，设置条件保存后，在打开，查询图标跑上边了 -->
			                                       	   		<img src="/images/code.gif" style="vertical-align: text-top;" onclick='openCondCodeDialog("${element.codeid}","<%=ww %>_<bean:write name="element" property="fieldname" />.hzvalue");'/>
			                                         	</logic:notEqual>                                          	                                                                                   
			                                         </logic:notEqual>
			                                
			                              </logic:notEqual> 
			                              <logic:equal name="element" property="codeid" value="0">
			                              		<input type="text" value="<bean:write name="searchInformForm" property='<%="factorlist["+ww+"].value"%>' />" size="30" maxlength="${element.itemlen}" name="<%=ww %>_<bean:write name="element" property="fieldname" />.value"  id='<%=ww %>_<bean:write name="element" property="fieldname" />.value' class="text4" />
			                              </logic:equal>                               
			                            </td>                           
			                          </logic:equal> 
			                          <!--数据值-->                            
			                          <logic:equal name="element" property="fieldtype" value="N">
			                            <td align="left" class="RecordRow" nowrap style="border-left: 0px;border-right: 0px;border-top:none;">    
			                            	<input type="text" name="<%=ww %>_<bean:write name="element" property="fieldname" />.value"  id='<%=ww %>_<bean:write name="element" property="fieldname" />.value' onkeypress="event.returnValue=IsDigit();" style="width:120px">  
			                            </td>                           
			                          </logic:equal>   
				                <% ww++; %>
				                </tr>
				      		 </logic:iterate>
      		 				</logic:equal>
         				</table>
         				</div>
         			</td>
        		</tr>
     		</table>
      	</fieldset>
   </td>
  </tr>
</table>
<table width="100%" border="0" align="center">
	<tr> 
	  <logic:equal name="searchInformForm" property="type" value="2">
      	<td height="25" width="200">
      </logic:equal>
	  <logic:notEqual name="searchInformForm" property="type" value="2">
      	<td height="25" width="400">
      </logic:notEqual>
    	<table width="100%" border="0">
    		<tr>
    		<logic:notEqual name="searchInformForm" property="type" value="5">
    		 <logic:equal value="0" name="searchInformForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like" id="like"><bean:message key='label.query.like'/></td>
    			<td id="viewHistory" style="display:none"><input type="checkbox" name="history"><bean:message key='label.query.history'/></td>
    		</logic:equal>
    		<logic:equal value="A01" name="searchInformForm" property="fieldSetId">
    			<td width="160"><input type="checkbox" name="like" id="like"><bean:message key='label.query.like'/>
    			<logic:equal value="1" name="searchInformForm" property="secondflag">
    				<input type="checkbox" id="second" name="second"><bean:message key='label.query.second'/>
    			</logic:equal>
    			</td>
    			<td id="viewHistory" style="display:none"><input type="checkbox" name="history"><bean:message key='label.query.history'/></td>
    		</logic:equal>
    		<logic:notEqual value="0" name="searchInformForm" property="fieldSetId">
    		  <logic:notEqual value="A01" name="searchInformForm" property="fieldSetId">
    			<td width="80"><input type="checkbox" name="like" id="like"><bean:message key='label.query.like'/></td>
    		  </logic:notEqual>
    		</logic:notEqual>
    		</logic:notEqual>
    		</tr>
    		
    	</table>
      </td>
   	<logic:equal name="searchInformForm" property="type" value="2">
      <td height="25">
    	<table  border="0">
    		<tr>
    			<td>&nbsp;</td>
    			<td><input type="radio" name="unite" value="1"><bean:message key='label.query.dept'/></td>
    			<td><input type="radio" name="unite" value="0"><bean:message key='label.query.org'/></td>
    			<td><input type="radio" name="unite" value="2" checked><bean:message key='label.query.all'/></td>
      		</tr>
    	</table>
      </td>
 	</logic:equal>
      <td height="25" align="left">
    		<logic:equal name="searchInformForm" property="type" value="5">
    		<input type="button" name="searchok" value="<bean:message key="button.ok"/>" 
    		    onclick='searchSetCond("${searchInformForm.a_code}","${searchInformForm.tablename}","${searchInformForm.type}","${searchInformForm.fieldSetId}","<%=inforflag %>");' 
    		    Class="mybutton">
    		</logic:equal>
    		<logic:notEqual name="searchInformForm" property="type" value="5">
    		 <input type="button" name="searchok" value="<bean:message key='button.query'/>" 
    		  onclick='searchSetCond("${searchInformForm.a_code}","${searchInformForm.tablename}","${searchInformForm.type}","${searchInformForm.fieldSetId}","<%=inforflag %>");' 
    		  Class="mybutton">
    		</logic:notEqual>	
    		<input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="windowClose();" Class="mybutton">

      </td>
  </tr>
  <logic:notEqual name="searchInformForm" property="type" value="5">
  <tr id="lert"><td colspan="5" align="left">提示：字符型、代码型指标可使用通配符 "*" 或 "?" 辅助查询</td></tr>
  </logic:notEqual>
</table>
<div id="date_panel">
	<select name="date_box" multiple="multiple" size="10"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
		<option value="$YRS[10]"><bean:message key='general.inform.search.years'/></option>
		<option value="<bean:message key='general.inform.search.this.years'/>"><bean:message key='general.inform.search.this.years'/></option>
		<option value="<bean:message key='general.inform.search.this.month'/>"><bean:message key='general.inform.search.this.month'/></option>
		<option value="<bean:message key='general.inform.search.this.day'/>"><bean:message key='general.inform.search.this.day'/></option>				    
		<option value="<bean:message key='general.inform.search.day'/>"><bean:message key='general.inform.search.day'/></option>
		<option value="<bean:message key='kq.wizard.edate'/>"><bean:message key='kq.wizard.edate'/></option>
		<option value="1992.4.12">1992.04.12</option>	
		<option value="1992.4">1992.04</option>	
		<option value="1992">1992</option>			    
		<option value="1992-04-12">1992-04-12</option>
		<option value="1992-04">1992-04</option>			    			    		    
	</select>
</div>
</html:form>
<script language="JavaScript">
change();
Element.hide('date_panel');
</script>