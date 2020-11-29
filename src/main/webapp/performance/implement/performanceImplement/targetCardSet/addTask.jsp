<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 com.hrms.struts.taglib.CommonData" %>

<%	
	String beforeitemid=(String)request.getParameter("beforeitemid");
	String closeWin = request.getParameter("callBackFunc");
%>
				 	
<script>
    <%if(closeWin!=null){%>
    	takeClose(true);
    <%}%>
	function saveTaskContent(type)
	{
		var taskcontent = document.getElementById('taskcontent').value;
				

		if(type=='save')
		{
            implementForm.action="/performance/implement/performanceImplement/targetCardSet.do?b_addTask=link&type="+type+"&taskcontent="+getEncodeStr(taskcontent)+"&itemid=<%=beforeitemid%>&callBackFunc=closeWin";
            implementForm.submit();
            //takeClose();
		}else
		{
            implementForm.action="/performance/implement/performanceImplement/targetCardSet.do?b_addTask=link&type="+type+"&taskcontent="+getEncodeStr(taskcontent)+"&itemid=<%=beforeitemid%>";
            implementForm.submit();
			document.getElementById('taskcontent').value='';
			takeClose(false);
		}
				
		/*
		var thevo=new Object();
       	thevo.flag="true";
       	thevo.taskcontent=taskcontent;       	
       	window.returnValue=thevo;
		window.close();		
		*/
	}
	function setEnable(theVal)
	{
		if(ltrim(rtrim(theVal))!='') 
		{
			document.getElementById('ok_bt').disabled=false;
			document.getElementById('ok_ct').disabled=false;
		}else
		{
			document.getElementById('ok_bt').disabled=true;	
			document.getElementById('ok_ct').disabled=false;
		}
	}
	function takeClose(closeflag)
	{
        var thevo=new Object();
        thevo.flag="true";
        if(window.showModalDialog){
            parent.window.returnValue=thevo;
        }else if (parent.window.opener.newtask_ok){
            parent.window.opener.newtask_ok(thevo);
        }
        if(closeflag){
			closeWin();
		}
	}
	function closeWin(){
        parent.window.close();
    }
</script>		 
<html:form action="/performance/implement/performanceImplement/targetCardSet"> 

	<table border="0" cellspacing="0" align="center" cellpadding="2">
			<tr>
						<td height='10' nowrap>
							&nbsp;
						</td>
			</tr>
			<tr>
						<td align="center" nowrap >
						<fieldset align="center" style="width:300;">
							<legend>
									<bean:message key='jx.implement.target_card_set.taskcontent' />
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="2">
								<tr>
									<td style="height:35px" nowrap>
										<input type="text" size='40' id='taskcontent' onkeyup="setEnable(this.value)">
									</td>
								</tr>
							</table>
						</fieldset>
						</td>
			</tr>
			</table>			
			
			<table width="100%">
				<tr>
					<td align="center">
						
						<input type="button" class="mybutton" disabled id='ok_bt'
							value="<bean:message key='button.save' />" onClick="saveTaskContent('save');" />
												
						<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" 
							disabled id='ok_ct' onclick="saveTaskContent('save_continue');" class="mybutton"> 
						
						<input type="button" class="mybutton"
							value="<bean:message key='button.cancel' />"
							onClick="closeWin();">
					</td>
				</tr>
			</table>
</html:form>