<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.demandPlan.EngagePlanForm,org.apache.commons.beanutils.LazyDynaBean" %>
<html>
<head>
<title></title>
</head>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String tt=request.getParameter("operate");
	EngagePlanForm form=(EngagePlanForm)session.getAttribute("engagePlanForm");
	String infoflag="1";
	 infoflag=form.getInfoflag();
	 if(infoflag==null||"".equalsIgnoreCase(infoflag)){
	 infoflag="1";
	 }
	String info="";
	if(tt!=null&&tt.equalsIgnoreCase("issue")){
		info=form.getInfo();
	}
	
	
 %>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script language="JavaScript" src="../../js/constant.js"></script>
<script language="javascript">
	//this.status ="招聘管理 / 招聘计划";
	function add()
	{
		engagePlanForm.action='/hire/demandPlan/engagePlan.do?b_init=link&origin=b&editopt=0';
    	engagePlanForm.submit();
	}
	
	
	function init(id)
	{
		if(confirm(CONFIRE_INIT_PLAN+"？"))
		{
			engagePlanForm.action='/hire/demandPlan/engagePlan.do?b_initPlan=link&z0101='+id;
    		engagePlanForm.submit();
		}
	}
	
	
	
	
	function del()
	{
		var num=0;
		for(var i=0;i<engagePlanForm.elements.length;i++)
		{
			if(engagePlanForm.elements[i].type=='checkbox'&&engagePlanForm.elements[i].checked==true&&engagePlanForm.elements[i].name!='selbox')
				num++;
		}
		if(num==0)
		{
			alert(PLEASE_SELECT_PLAN_TO_DELETE);
			return;
		}
		if(confirm(PLEASE_CONFIRM_TO_DELETE_PLAN+"?"))
		{
			var flag="1";//同时删除用工需求
			if(!confirm(IF_TO_DELETE_REQUIREMENT+"?"))
					flag="0";
		
			engagePlanForm.action='/hire/demandPlan/engagePlan.do?b_delete=delete&operate=del&flag='+flag;
	    	engagePlanForm.submit();
	    }
	}
	
	
	function issue()
	{
		var num=0;
		var az0129=document.getElementsByName("az0129");
		var az0129s='';
		for(var i=0;i<engagePlanForm.elements.length;i++)
		{
			if(engagePlanForm.elements[i].type=='checkbox'&&engagePlanForm.elements[i].checked==true&&engagePlanForm.elements[i].name!='selbox')
			 az0129s = az0129[num].value;
				num++;
		}
		if(az0129s=='')
		{
			alert(PLEASE_SELECT_TO_RELEASE_PLAN+"！");
			return;
		}
		if(az0129s=='已发布'){
		alert('计划已发布!');
		}else{
		if(confirm(PLEASE_CONFIRM_TO_RELEASE_PLAN+"？"))
		{
			engagePlanForm.action='/hire/demandPlan/engagePlan.do?b_delete=delete&operate=issue';
    		engagePlanForm.submit();
    	}
    	}
	}
	function returnFlowPhoto()
   {
   document.location="/general/tipwizard/tipwizard.do?br_retain=link";
  // engagePlanForm.target="il_body";
  // engagePlanForm.submit();
  }
	
	function document._document_oncontextmenu() 
{
var e = window.event;

return false; 
}
function ret(){
	engagePlanForm.action='/hire/demandPlan/engagePlan.do?b_query=query';
	engagePlanForm.submit();
}
</script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<hrms:themes></hrms:themes>
<%
  String bosflag= userView.getBosflag();//得到系统的版本号
  if(bosflag!=null&&bosflag.equalsIgnoreCase("hcm")){
%>
  <link href="/hire/css/layout.css" rel="stylesheet" type="text/css">
<%
  }
%>
<body>
<html:form action="/hire/demandPlan/engagePlan">
    <%
        if(bosflag!=null&&!bosflag.equalsIgnoreCase("hcm")){
    %>
        <br/>
    <%
        }
    %>
	<% if(tt!=null&&tt.equalsIgnoreCase("issue")&&infoflag.equalsIgnoreCase("2")){ %>
	<table  width="100%" align='center' class="normalmp">
	<tr>
	<td  align='center'>
	<%=info %>
	<br></td>
	</tr>
	<tr>
	<td  align='center'>
	 <input type="button" onclick='ret();' value="<bean:message key="button.return"/>" class="mybutton"/>
	<br></td>
	</tr>
	</table>
	<%}else{ %>
	<table width="100%"  border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow normalmp" >
   	  <thead>
           <tr>
           <td align="center" class="TableRow" nowrap>
    			 <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'><!-- 选择checkbox -->
	       </td>
           
           <logic:iterate id="element" name="engagePlanForm" property="tableHeadList"  offset="1"> 
	      		<td align="center" class="TableRow" nowrap>
	      		&nbsp;&nbsp;&nbsp;<bean:write name="element" filter="false"/>&nbsp;&nbsp;&nbsp;<!-- 挨着输出表头信息 -->
	      		</td>
            </logic:iterate>

		       
		      <td align="center" class="TableRow" nowrap>
				<bean:message key="lable.zp_plan_detail.pos_id"/><!-- 招聘岗位 -->
		     </td> 
		     <td align="center" class="TableRow" nowrap>
				<bean:message key="kq.feast_type_list.modify"/>
		     </td> 
		     <!-- 
		     <td align="center" class="TableRow" nowrap>
				<bean:message key="hire.demandplan.engagePlan.init"/>
		     </td>   
		     -->
		      		        	        	        
         </tr>
   	  </thead>
   	  
   	 
   	  <% int i=0; String className="trShallow"; %>
   	   <hrms:paginationdb id="element" name="engagePlanForm" sql_str="${engagePlanForm.str_sql}" table="" where_str="${engagePlanForm.str_whl}" columns="${engagePlanForm.columnName}" order_by="${engagePlanForm.order_by}" fromdict="1"  page_id="pagination" pagerows="15" indexes="indexes">
			  <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
            <td align="center" class="RecordRow" nowrap>
             	 	<hrms:checkmultibox name="engagePlanForm" property="pagination.select" value="true" indexes="indexes"/>
            </td>
           
           <%
           	EngagePlanForm engagePlanForm=(EngagePlanForm)session.getAttribute("engagePlanForm");
            ArrayList    list=engagePlanForm.getPlanFieldList();
           	for(int a=0;a<list.size();a++)
            {
           		   	 	LazyDynaBean aBean=(LazyDynaBean)list.get(a);
           		   	 	String id=(String)aBean.get("id");      		   	 	
           		   	 	String settype=(String)aBean.get("setType");
           		   	 	String  type=(String)aBean.get("type");
           		   	 	
           		   	 	if(settype.equals("0"))
           		   	 	{
           		   	 		if(type.equalsIgnoreCase("N")||type.equalsIgnoreCase("D")){
           %>
           
           				<td align="right" class="RecordRow" nowrap> 
	          			<bean:write name="element" property="<%=id%>" filter="false"/>
	          			<input type='hidden'  name='az0129' value='<bean:write name="element" property="az0129" />' />
	          			</td>
           
           <%}else{ %>
           				<td align="left" class="RecordRow" nowrap> 
	          			<bean:write name="element" property="<%=id%>" filter="false"/>
	          			<input type='hidden'  name='az0129' value='<bean:write name="element" property="az0129" />' />
	          			</td>
           
           <%}
           				}
           				else
           				{
           
           %>
                    <td align="left" class="RecordRow" nowrap> 
		           <hrms:codetoname codeid="<%=settype%>" name="element" codevalue="<%=id%>" codeitem="codeitem" scope="page"/>  	      
		          	<bean:write name="codeitem" property="codename" />
		          	<input type='hidden'  name='az0129' value='<bean:write name="element" property="az0129" />' />
		          	</td>
           
           <% 			}
           } 
           %>
           
	       <bean:define id="z0101" name="element" property="z0101" />
	       <%
	       	String _z0101=PubFunc.encrypt(z0101.toString());
	       %>
            <td align="center" class="RecordRow" nowrap>
            		
               			<a href="/hire/demandPlan/engagePlan.do?b_queryPosition=query&z0101=<%=_z0101 %>">	
               			 <img src="/images/view.gif" border=0>
               			</a>
               		
               
	   		 </td>
            <td align="center" class="RecordRow" nowrap>
            	&nbsp;
            	<hrms:priv func_id="310133">
            	<logic:notEqual name="element" property="z0129" value='06' >
	            	<a href="/hire/demandPlan/engagePlan.do?b_init=link&origin=b&editopt=1&z0101=<%=_z0101 %>">
	              		 <img src="/images/edit.gif" border=0>
	                </a>
                </logic:notEqual>
                </hrms:priv>
                &nbsp;
	  	    </td>  
	  	    <!-- 
	  	     <td align="center" class="RecordRow" nowrap>
	  	     	<logic:notEqual name="element" property="z0129" value='06' >
	            	<a href="javascript:init('<bean:write name="element" property="z0101" filter="false"/>')" >
	              		 <img src="/images/add_del.gif" border=0>
	                </a>
                </logic:notEqual>
	  	     </td>
	       	    -->
	       	    
	       	    
	       	    
	       	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
        
</table>

<table  width='100%'  class='RecordRowP'  align='center'>
		<tr>
		   <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true"/>
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true"/>
					<bean:message key="label.every.row"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true"/>
					<bean:message key="hmuster.label.paper"/>
			</td>
			<td  align="right" nowrap class="tdFontcolor">
			<p align="right"><hrms:paginationdblink name="engagePlanForm" property="pagination" nameId="engagePlanForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   	  
</table> 


<table  width="85%" align="center">
          <tr>
            <td align="center"> 
            <hrms:priv func_id="310131">
              <input type="button" name="b_add" value="<bean:message key="button.insert"/>" class="mybutton" onClick="add()">
         	 </hrms:priv>
         	 <hrms:priv func_id="310132">
         	  <input type="button" name="b_delete" value="<bean:message key="kq.search_feast.delete"/>" onclick="del();" class="mybutton">
             </hrms:priv>
             <hrms:priv func_id="310134">
              <input type="button" name="b_add2" value="<bean:message key="button.release"/>" onclick="issue()" class="mybutton" > 
           	 </hrms:priv>
           	 <logic:equal value="dxt" name="engagePlanForm" property="returnflag">
           	 <%if(userView.getBosflag()!=null&&(userView.getBosflag().equalsIgnoreCase("hl")||userView.getBosflag().equalsIgnoreCase("hcm"))){ %>
   <input type="button" onclick='returnFlowPhoto();' value="<bean:message key="button.return"/>" class="mybutton"/>
      <%} %>
      </logic:equal>
            </td>
          </tr>          
</table>

<%} %>


</html:form>
</body>
</html>