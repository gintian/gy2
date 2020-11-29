<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language='javascript'>
	function checkDownCheckbox(id)
	{
		if(document.getElementById(id).checked==false)
		{
			if(id=='copy_self' && document.getElementById("copy_khmainbodytype").checked==true)
				document.getElementById(id).checked=true;
			else if(id=='copy_khmainbodytype' && document.getElementById("copy_khobject").checked==true)
			    document.getElementById(id).checked=true;
			else if(id=='copy_khobject' && document.getElementById("copy_khmainbody").checked==true)
			    document.getElementById(id).checked=true;  
		    else if(id=='copy_khmainbody' && document.getElementById("copy_khmainbody_pri").checked==true)
			    document.getElementById(id).checked=true;  
		}
	}
	function checkSomeone(id)
	{
		var obj = document.getElementById(id);
		if(obj.disabled!='true')		
			obj.checked=true;
	}
	function selectSelf(id)
	{
		//选中下面不许选不中上面
		checkDownCheckbox(id);	
		if(id=='copy_khmainbodytype')
			document.getElementById("copy_self").checked=true;
		if(id=='copy_khobject')
		{
			document.getElementById("copy_self").checked=true;
			checkSomeone('copy_khmainbodytype');
		}
		if(id=='copy_khmainbody')
		{
			document.getElementById("copy_self").checked=true;
			checkSomeone('copy_khobject');
			checkSomeone('copy_khmainbodytype');
		}	
		if(id=='copy_khmainbody_pri')
		{
			document.getElementById("copy_self").checked=true;
			checkSomeone('copy_khobject');
			checkSomeone('copy_khmainbodytype');
		    checkSomeone('copy_khmainbody');
		}
		setEnableOnButton();
	}
	function setEnableOnButton()
	{	
		var flag = false;
		var objs = document.getElementsByTagName('input');
		for(var i=0;i<objs.length;i++)
		{
			if(objs[i].type=='checkbox' && objs[i].checked==true)
				flag=true;
		}
		if(flag==true)
			document.getElementById('copybtn').disabled=false;
		else
			document.getElementById('copybtn').disabled=true;
	}
	function goback()
	{
		if("${param.oper}"=="close")
		{
			var thevo=new Object();
       		thevo.flag="true";
            parent.window.returnValue=thevo;
       	}
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            parent.parent.saveas_window_ok(thevo);
        }
	}
	function copy()
	{						
		var planId = document.getElementById("planId").value;
		var busitype = document.getElementById("busitype").value;
				
		if(document.getElementById('copy_khmainbody_pri').checked==true)
		{
			var signDesc = COPY_POINTPRIV;
			if(busitype!=null && busitype==1)
				signDesc = COPY_POINTPRIV1;
				
			if(confirm(signDesc))
			{
				if (confirm('<bean:message key="jx.khplan.copyinfo"/>'))
				{
					document.getElementById('copybtn').disabled=true;
					document.examPlanForm.action="/performance/kh_plan/examPlanSaveAs.do?b_save=link&planId="+$URL.encode(planId)+"&type=copy&oper=close";
					document.examPlanForm.submit();	
				}
			}
		}
		else
		{
			if (confirm('<bean:message key="jx.khplan.copyinfo"/>'))
			{
				document.getElementById('copybtn').disabled=true;
				document.examPlanForm.action="/performance/kh_plan/examPlanSaveAs.do?b_save=link&planId="+$URL.encode(planId)+"&type=copy&oper=close";
				document.examPlanForm.submit();	
			}
		}
	}
	function myClose()
	{
		if("${param.oper}"=="close")
		{
			var thevo=new Object();
       		thevo.flag="true";
       		window.returnValue=thevo;
            if(!window.showModalDialog) {
                parent.parent.saveas_window_ok(thevo);
            }
       	}
	}
</script>
<body onbeforeunload="myClose();">
<html:form action="/performance/kh_plan/examPlanSaveAs">
	<input type='hidden' id="planId" value="<%=request.getParameter("planId")%>">
	<input type='hidden' id="type" value="<%=request.getParameter("type")%>">
	<input type='hidden' id="busitype" value="<%=request.getParameter("busitype")%>">
	<table width="100%" align="left" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td width="470" valign="left">
				<fieldset style="width:470">
					<legend>
						<bean:message key="jx.khplan.copystep" />
					</legend>
				<table width="100%" border="0" cellpadding="0" cellspacing="0"
					align="left">
					<tr height="20">
						<td align="left" nowrap>
							<html:checkbox  styleId="copy_self" name="examPlanForm" property="copy_self" value="1" onclick="selectSelf('copy_self');"/>
					
							<bean:message key="jx.khplan.copy_self" />
						</td>
					</tr>
					<tr height="20">
						<td align="left" nowrap>
							<html:checkbox styleId="copy_khmainbodytype" name="examPlanForm" property="copy_khmainbodytype"
								value="1"  onclick="selectSelf('copy_khmainbodytype');"/>
					
							<bean:message key="jx.khplan.copy_khmainbody_type" />
						</td>
					</tr>
					<tr height="20">
						<td align="left" nowrap>
							<html:checkbox styleId="copy_khobject" name="examPlanForm" property="copy_khobject"
								value="1" onclick="selectSelf('copy_khobject');"/>					
							<bean:message key="jx.khplan.copy_khobject" />
						</td>
					</tr>
					<tr height="20">
						<td align="left" nowrap>
							<html:checkbox styleId="copy_khmainbody" name="examPlanForm" property="copy_khmainbody"
								value="1" onclick="selectSelf('copy_khmainbody');"/>						
							<bean:message key="jx.khplan.copy_khmainbody" />	
						</td>
					</tr>
					<tr height="20">
						<td align="left" nowrap>
							<html:checkbox styleId="copy_khmainbody_pri" name="examPlanForm" property="copy_khmainbody_pri"
								value="1" onclick="selectSelf('copy_khmainbody_pri');"/>								
							<bean:write name="examPlanForm" property="copy_khmainbody_pri_title" filter="false" />
							
						</td>
					</tr>
				</table>
			</fieldset>
			</td>
			<td width="100" align="center" valign="top">
				<div style="position:relative;top:7px;">
					<input type='button' id='copybtn'
						value='<bean:message key='jx.khplan.startcopy' />'
						class="mybutton" onclick='copy();'>
				</div>
				<div style="position:relative;top:30px;">
					<input type='button'
						value='关      闭'
						onclick='goback();' class="mybutton">
				</div>
			</td>
		</tr>
		<tr>
			<td width="470">
				<table border="0" cellspacing="0" width="100%" 
					cellpadding="2" align="left">
					<tr>
						<td>
							<html:textarea name="examPlanForm" styleId="result"
								property="copyResultStr" 
								styleClass="textboxMul" style="width:100%;height:300px"></html:textarea>
						</td>
					</tr>
				</table>
			</td>
            <td width="100"></td>
		</tr>
	</table>
	<script>	
		var type = document.getElementById("type").value;
		if(type=='nocopy')
		{
			var copyself = document.getElementById("copy_self").checked;	
			var copy_khmainbody_pri = document.getElementById("copy_khmainbody_pri").checked;		
			var copy_khmainbodytype = document.getElementById("copy_khmainbodytype").checked;	
		    var copy_khobject = document.getElementById("copy_khobject").checked;	
			var copy_khmainbody = document.getElementById("copy_khmainbody").checked;	
			
			//alert('copyself:'+copyself+' copy_khmainbody_pri:'+copy_khmainbody_pri+' copy_khmainbodytype:'+copy_khmainbodytype+' copy_khobject:'+copy_khobject+' copy_khmainbody:'+copy_khmainbody);    
			if(copyself==true)
				document.getElementById("copy_self").checked=false;
			else
				document.getElementById("copy_self").disabled='true';
				
			if(copy_khmainbodytype==true)
				document.getElementById("copy_khmainbodytype").checked=false;
			else
				document.getElementById("copy_khmainbodytype").disabled='true';
					
			if(copy_khobject==true)
				document.getElementById("copy_khobject").checked=false;
			else
				document.getElementById("copy_khobject").disabled='true';		
			
		   if(copy_khmainbody==true)
				document.getElementById("copy_khmainbody").checked=false;
		   else
				document.getElementById("copy_khmainbody").disabled='true';
				
		   if(copy_khmainbody_pri==true)
				document.getElementById("copy_khmainbody_pri").checked=false;
		   else
				document.getElementById("copy_khmainbody_pri").disabled='true';	
		}
		else if(type=='copy')
		{
			document.getElementById("copy_khmainbody_pri").disabled='true';
			document.getElementById("copy_khmainbody").disabled='true';
			document.getElementById("copy_khobject").disabled='true';
			document.getElementById("copy_khmainbodytype").disabled='true';	
			document.getElementById("copy_self").disabled='true';
		}
		setEnableOnButton();
		if(type=='copy')
			document.getElementById('copybtn').disabled=true;
	</script>
</html:form>
</body>