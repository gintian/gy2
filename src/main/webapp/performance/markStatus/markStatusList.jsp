<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.markStatus.markStatusForm,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.FieldItem,
			     org.apache.commons.beanutils.LazyDynaBean,
			     java.util.*"%>
<hrms:themes />

<%
			markStatusForm a_markStatusForm=(markStatusForm)session.getAttribute("markStatusForm");
			String object_type=a_markStatusForm.getObject_type();	  // 1:部门  2:人员
			String selectFashion=a_markStatusForm.getSelectFashion(); // 查询方式 1:按考核主体  2:考核对象
			String isFlag=a_markStatusForm.getIsFlag();  //0:无目标,无报告 1:有报告无目标 2有目标 有报告
			String checkall=a_markStatusForm.getCheckall();
			String g_optUrl="goal";
			String s_optUrl="summary";
			if(object_type.equals("1")||object_type.equals("3")||object_type.equals("4"))
			{
				g_optUrl="goal2";
				s_optUrl="summary2";
			}
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	} 
%>

<style>

.ListTable_self {
    BACKGROUND-COLOR: #FFFFFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
 }   
 
</style>
<script language="JavaScript" src="/ajax/basic.js"></script>
<script language="JavaScript"src="../../../js/showModalDialog.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language='javascript'>

function change()
{
    var dept=document.getElementById("dept");
    if(dept)
       dept.options[0].selected=true;
   	//切换计划的时候应该重置当前页码  haosl 2018-8-13
    var current = document.getElementsByName("current")[0];
    if(current){
    	current.value='1';
    }
	markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=link&firstpage=1&opt=clear";
	markStatusForm.submit();

}

function sub()
{
 	<%  if(!(selectFashion.equals("2")&&(object_type.equals("1")||object_type.equals("3")||object_type.equals("4")))){   %>
	var obj=eval("document.markStatusForm.name");
	if(trim(obj.value).length==0)
	{
		obj.value=" ";
	}
 	<% } %>
	markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=link";
	markStatusForm.submit();
}
function reScore(strurl)
{
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl); 
    var config = {
	      	width:430,
	      	height:330,
	      	type:'1',
	      	title:'重新打分',
	      	id:'reScoreWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,openFlag);
    /*
   
   	*/
}
function openFlag(flag){
	if(flag)
    {
       markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=link";
	   markStatusForm.submit();
    }
}
function openView(url)
{
  	window.open(url,"_blank","width="+(window.screen.width-40)+",left=15,height="+window.screen.height+",top=0,toolbar=no,menubar=no,status=no,scrollbars=yes, resizable=no,location=no");  
}

// 考评进度统计表
function selectMaOrOb()
{   
	markStatusForm.action="/performance/markStatus/markStatusList.do?b_select=link";
	markStatusForm.submit();
}



function allSelect()
{
	   markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=link&opt=all";
	   markStatusForm.submit();
}
function allClear()
{
	   markStatusForm.action="/performance/markStatus/markStatusList.do?b_search=link&opt=clear";
	   markStatusForm.submit();
}


function sendMessage()
{
   var n=0;
   var to_a0100="";
   var isAll="0"
   for(var i=0;i<document.markStatusForm.elements.length;i++)
   {
		if(document.markStatusForm.elements[i].type=='checkbox'&&document.markStatusForm.elements[i].checked)
		{
			if(document.markStatusForm.elements[i].name!='checkall')
			{
				to_a0100+=","+document.markStatusForm.elements[i].value;
				n++;
			}
			else
			{
				isAll="1";
				break;
			}
		}
	}
	if(n==0&&isAll=="0")
	{
		alert("请选择需发送消息的考核主体!");
		return;
	} 
	if(!confirm("只对未打分和正在打分的主体发送邮件?")){
		return;
	}
	var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=2`plan_id="+document.markStatusForm.checkPlanId.value+"`to_a0100="+to_a0100;
    url+="`departid="+document.markStatusForm.department.value+"`isAll="+isAll+"`name="+getEncodeStr(document.markStatusForm.name.value);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url)
    var config = {
	      	width:700,
	      	height:500,
	      	type:'1',
	      	title:'发送通知',
	      	id:'sendMessageWin'
    	}
 	modalDialog.showModalDialogs(iframe_url,'template_win',config);
}


</script>

<html:form action="/performance/markStatus/markStatusList">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0"  >
	<tr align="center" nowrap valign="center">
   <td align="center"  height='25'   nowrap valign="left"     >   
   			<strong><font size='4'>	<bean:message key="lable.performance.markStatusRepot"/>  </font></strong>
   </td>
   </tr>
</table>

<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0"  >
	<tr align="center" nowrap valign="center">
	   <td align="left"  height='25'   nowrap valign="left"    >        
	     <bean:message key="lable.performance.perPlan"/>：&nbsp;         
	 
	       <hrms:optioncollection name="markStatusForm" property="checkPlanList" collection="list" />
	             <html:select name="markStatusForm" property="checkPlanId" size="1" onchange="change();">
	             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
	        </html:select>
	   	 </td>
	 </tr>
	 
	 
	 <tr align="center" nowrap valign="center">
	   <td align="left"  height='30'   nowrap valign="left" >        
	     <bean:message key="performance.implement.queryObject"/>：&nbsp;         
	       <html:select name="markStatusForm" property="selectFashion" size="1">
                              <html:optionsCollection name="markStatusForm" property="fashionList" value="dataValue" label="dataName"/>
          </html:select>
          &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="workbench.pos.posname"/>:         
	       <html:select styleId="dept" name="markStatusForm" property="department" size="1">
                              <html:optionsCollection name="markStatusForm" property="departmentList" value="dataValue" label="dataName"/>
           </html:select>
           <%  if(!(selectFashion.equals("2")&&(object_type.equals("1")||object_type.equals("3")||object_type.equals("4")))){   %>
           &nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="label.title.name"/>:
           <html:text  name="markStatusForm" property="name" size='10' ></html:text>
            <% } %> 
           &nbsp;&nbsp;
           <html:button styleClass="mybutton" property="b_next" onclick="sub()">
	            	<bean:message key="infor.menu.query"/>
		   </html:button> 
		    <% if(selectFashion.equalsIgnoreCase("1")){ %>
           <html:button styleClass="mybutton" property="b_message" onclick="sendMessage()">
	            	发送通知
		   </html:button> 
		   <%  } %>
		   <hrms:priv func_id="06060402,32603010002">
	           <html:button styleClass="mybutton" property="b_select" onclick="selectMaOrOb();">
		            	<bean:message key="jx.selfScore.markStatusList"/>
			   </html:button>  
		   </hrms:priv>		   
	   	 </td>
	 </tr>
</table>


	   	
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
<thead>
    <tr class="trDeep" > 
    <% if(selectFashion.equalsIgnoreCase("1")){ %>
    <td align="center" class="TableRow_2rows"  nowrap rowspan="2" >
  		<logic:equal value="0" name="markStatusForm" property="checkall">
		<html:checkbox property="checkall" name="markStatusForm" value="1" onclick="allSelect();"></html:checkbox>
		</logic:equal>
		<logic:equal value="1" name="markStatusForm" property="checkall">
		<html:checkbox property="checkall" name="markStatusForm" value="1" onclick="allClear();"></html:checkbox>
		</logic:equal>
    </td>
    <% } %>
    
    <%  
    	if(!(selectFashion.equals("2")&&(object_type.equals("1")||object_type.equals("3")||object_type.equals("4"))))
    	{   
    		FieldItem fielditem = DataDictionary.getFieldItem("E0122");
    %>
      <td align="center" class="TableRow_2rows"  nowrap rowspan="2" >&nbsp;<%=fielditem.getItemdesc()%>&nbsp;</td>
    <% } %>
      <td align="center" class="TableRow_2rows"  width='20%' nowrap rowspan="2" > 
      <logic:equal name="markStatusForm" property="selectFashion"   value="1">
      	<bean:message key="lable.performance.perMainBody"/>
      </logic:equal>
       <logic:equal name="markStatusForm" property="selectFashion"  value="2">
       	<bean:message key="lable.performance.perObject"/>
      </logic:equal>
      </td>
      <td align="center" class="TableRow"  colspan="4" >
      <logic:equal name="markStatusForm" property="selectFashion"  value="1">
     	 <bean:message key="lable.performance.perObject"/>
      </logic:equal>
      <logic:equal name="markStatusForm" property="selectFashion"  value="2">
      	<bean:message key="lable.performance.perMainBody"/>
      </logic:equal>
      </td>         
      
      <logic:equal name="markStatusForm" property="isFlag"  value="1">
        <td align="center" class="TableRow_2rows"  nowrap rowspan="2" >
       		&nbsp;<bean:message key="lable.performance.perSummary"/>&nbsp;
        </td>
      </logic:equal>
      <logic:equal name="markStatusForm" property="isFlag"  value="2">
        <td align="center" class="TableRow_2rows"  nowrap rowspan="2" >
       		&nbsp;绩效目标&nbsp;
        </td>
        <td align="center" class="TableRow_2rows"  nowrap rowspan="2" >
       		&nbsp;<bean:message key="info.appleal.state11"/>&nbsp;
        </td>
      </logic:equal>
      
        <logic:equal value="1" name="markStatusForm" property="selectFashion">
        <hrms:priv func_id="06060401,32603010001">
       <td align="center" class="TableRow_2rows" rowspan="2" nowrap>
        <bean:message key="label.performance.rescoreOrReconfirm"/>
        </td>
        </hrms:priv>
        </logic:equal>
     
   </tr>
   <tr class="trDeep" >      
      <td align="center" class="TableRow"  width="22%"   ><bean:message key="lable.performance.notMark"/></td>
      <td align="center" class="TableRow"  width="22%" ><bean:message key="lable.performance.marking"/></td>	 
      <td align="center" class="TableRow"  width="22%"  ><bean:message key="lable.performnace.noMark"/></td>
      <td align="center" class="TableRow"  width="22%"  ><bean:message key="lable.performance.marked"/></td>	         	  
   </tr>
</thead>
<% int i=0; %>
    <hrms:extenditerate id="element" name="markStatusForm" property="markStatusListform.list" indexes="indexes"  pagination="markStatusListform.pagination"  pageCount="10" scope="session">
        <%
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
         <% if(selectFashion.equalsIgnoreCase("1")){ %>
          <td align="left" class="RecordRow" nowrap >&nbsp;
          <input type='checkbox'  name='selectbox' <%=(checkall.equals("1")?"checked":"")%> value='<bean:write  name="element" property="object_id" filter="true"/>' />
          &nbsp;</td>  
          <% } %>
          <%  if(!(selectFashion.equals("2")&&(object_type.equals("1")||object_type.equals("3")||object_type.equals("4")))){   %>
         	 <td align="left" class="RecordRow" nowrap >
              &nbsp;<bean:write  name="element" property="e0122" filter="false"/>&nbsp; &nbsp;&nbsp;        
             </td>  
          <% }  %>
             <td align="left" class="RecordRow" >
              <bean:write  name="element" property="userName" filter="false"/>&nbsp;         
             </td>  
             <td align="left" class="RecordRow" >
              <bean:write  name="element" property="noMark" filter="false"/>&nbsp;         
             </td>
             <td align="left" class="RecordRow" >
              <bean:write  name="element" property="marking" filter="true"/>&nbsp;         
             </td>
             <td align="left" class="RecordRow" >
              <bean:write  name="element" property="notMark" filter="false"/>&nbsp;         
             </td>
             <td align="left" class="RecordRow" >
              <bean:write  name="element" property="marked" filter="true"/>&nbsp;         
             </td>
             
             
             <logic:equal name="markStatusForm" property="isFlag"  value="1">
		       <td align="center" class="RecordRow" >
		       	  <logic:notEqual  name="element" property="s"  value="没填写">
		       			<a href='javascript:openView("/selfservice/performance/view_summary.do?b_query=link&fromflag=status&planNum=${markStatusForm.checkPlanId}&objectId=<bean:write  name="element" property="object_id" filter="true"/>&optUrl=<%=s_optUrl%>");'>
		       	   </logic:notEqual>
		       		<bean:write  name="element" property="s" filter="false"/>
		       	    <logic:notEqual  name="element" property="s"  value="没填写">
		       	    	</a>
		       	    </logic:notEqual>
		        </td>
		     </logic:equal>
		     <logic:equal name="markStatusForm" property="isFlag"  value="2">
		        <td align="center" class="RecordRow" >
		        <logic:notEqual  name="element" property="g"  value="没填写">
		       			<a href='javascript:openView("/selfservice/performance/view_summary.do?b_query=link&fromflag=status&planNum=${markStatusForm.checkPlanId}&objectId=<bean:write  name="element" property="object_id" filter="true"/>&optUrl=<%=g_optUrl%>");'>
		       	</logic:notEqual>
		       		<bean:write  name="element" property="g" filter="false"/>
		       	<logic:notEqual  name="element" property="g"  value="没填写">	
		       		    </a>
		       	</logic:notEqual>
		        </td>
		        <td align="center" class="RecordRow" >
		        <logic:notEqual  name="element" property="s"  value="没填写">
		       			<a href='javascript:openView("/selfservice/performance/view_summary.do?b_query=link&fromflag=status&planNum=${markStatusForm.checkPlanId}&objectId=<bean:write  name="element" property="object_id" filter="true"/>&optUrl=<%=s_optUrl%>");'>
		       	</logic:notEqual>
		       		<bean:write  name="element" property="s" filter="false"/>
		       	<logic:notEqual  name="element" property="s"  value="没填写">
		       	    	</a>
		       	</logic:notEqual>	
		       		
		        </td>
		     </logic:equal>
       
        <logic:equal value="1" name="markStatusForm" property="selectFashion">
        <hrms:priv func_id="06060401,32603010001">
        <td align="center" class="RecordRow" nowrap>
            <a href="javascript:reScore('/performance/markStatus/reScore.do?b_search=link`planid=${markStatusForm.checkPlanId}`mainbodyid=<bean:write  name="element" property="object_id" filter="true"/>');">${element.map.pbOpt }</a>
        </td>
        </hrms:priv>
        </logic:equal>
    
          </tr>
        </hrms:extenditerate> 

  </table>


<table  width="90%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="markStatusForm" property="markStatusListform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="markStatusForm" property="markStatusListform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="markStatusForm" property="markStatusListform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
	               
		          <p align="right">
		          <hrms:paginationlink name="markStatusForm" property="markStatusListform.pagination"
				nameId="markStatusListform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<br>


</html:form>
<script>
		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
	</script>