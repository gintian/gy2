<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*"%>
<script language="JavaScript" src="/js/validate.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />

<script language="javascript" src="../../../module/workplan/workplanhr/examPlanAdd.js"></script>
<script language="javascript" src="/workplan/js/relateplan.js"></script>

 <%
 String plantype=(String)request.getParameter("plantype");
 String periodtype=(String)request.getParameter("periodtype");
 String periodyear=(String)request.getParameter("periodyear");
 String periodmonth=(String)request.getParameter("periodmonth");
 String periodweek=(String)request.getParameter("periodweek");
 
 plantype=(plantype!=null)?plantype:"";
 periodtype=(periodtype!=null)?periodtype:"";
 periodyear=(periodyear!=null)?periodyear:"";
 periodmonth=(periodmonth!=null)?periodmonth:"";
 periodweek=(periodweek!=null)?periodweek:"";
%>

<script language='javascript' >
	function ok(obj)
	{
		if (obj.disbled==true ) {
		  return ;
		}
		obj.disabled = true;
		var txtformulaname=document.getElementById('planname');
		var txttemplate_id=document.getElementById('template_id');
		if (txtformulaname==null){
		    obj.disabled = false;
			return;
		}
		if (txttemplate_id==null){
		    obj.disabled = false;
			return;
		}
		var planname=txtformulaname.value;
		var template_id=txttemplate_id.value;
	
		if (planname==''){		
			alert("计划名称不能为空!");
			obj.disabled = false;
			return;
		}
		if (template_id==''){		
			alert("考核模板必须设置！");
			obj.disabled = false;
			return;
		}
		//新增新计划  
		//  document.examPlanForm.action="/workplan/relate_plan/add_plan.do?b_save=link";
       // document.examPlanForm.submit(); 
		//return;
		saveNewPlan();
	}
	


</script>
<body >
<table align="center" width="95%" style="">
        <input id="status" name ="status" type="hidden" value="0">
        <input id="plan_id" name="plan_id" type="hidden" value="0">
        <input id="planBusitype" type="hidden" value="">
        <input id="requiredFieldStr" type="hidden" value="">
        
        <input id="plantype" type="hidden" value="<%=plantype %>">
        <input id="periodtype" type="hidden" value="<%=periodtype %>">
        <input id="periodyear" type="hidden" value="<%=periodyear %>">
        <input id="periodmonth" type="hidden" value="<%=periodmonth %>">        
        <input id="periodweek" type="hidden" value="<%=periodweek %>">
	<tr>
		<td>
		  <fieldset align="left" style="width:94%;">
		   <legend><bean:message key="button.update"/><bean:message key="train.quesType.type_name"/></legend>
			<table width="98%" border="0" cellspacing="0" align="center" cellpadding="0">				
				<tr>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td width="30%" align="right">
						计划名称：
				     </td>  
					 <td width="70%" align="left">
						<input id="planname" type="text" class="textColorWrite"  name="planname" 
						class="inputtext" style="width:200px" value="">				   
				     </td>    
				</tr>
				
                <tr valign="middle">
                    <td width="30%" align="right" >
                       关联模板：
                     </td>  
                     <td width="70%" align="left" >                                         
                        <INPUT id="template_id" name="template_id" type=hidden value="" > 
                        <INPUT id="templateName" class="textColorRead" readOnly maxLength=50 size=30 
                            value="" name=templateName>
                         <A onclick=getTemplate();><IMG border=0 src="/images/edit.gif"> </A> 
                     </td>    
                     
                      
                </tr>
				<tr>
					<td>&nbsp;</td>
				</tr>
			</table>
			</fieldset>
		</td>
	</tr>
	<tr height="30">
		<td align="center">
			<table align="center">
	    		<tr >
			  	  <td align="center">
					<input type="button" value="<bean:message key='button.ok'/>"
                                        onclick="ok(this);" Class="mybutton"> &nbsp;
				    <input type="button" value="<bean:message key='button.cancel'/>"
                                        onclick="closeWin()" Class="mybutton"> &nbsp;
				  </td>
		    	</tr>
	    	</table>
		</td>
	</tr>
</table>
</body >

<script language='javascript'>
  initAddForm();
  function closeWin(){
	  parent.Ext.getCmp("addplan").destroy();
  }
</script>

  