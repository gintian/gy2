<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%int i =0;%>
<%
	String flag = request.getParameter("flag");
%>
<html:form action="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion">
	<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   		<thead>
   			<tr>
   				<td  width="10%" align="center" class="TableRow" nowrap>
   					<bean:message key="label.serialnumber"/>
   				</td>
   				<td width="60%" align="center" class="TableRow" nowrap>
   					<bean:message key="menu.field"/>
   				</td>
   				<td width="30%" align="center" class="TableRow" nowrap>
   					<bean:message key="label.kh.template.qz"/>
   				</td>
   			</tr>
   		</thead>
   		
   		<logic:iterate id="element" name="dynaTargetRankForm" property="rolelist">
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
        		<td width="10%" align="right" class="RecordRow" nowrap>
        			<%=i%>&nbsp;
        		</td>
        		<td width="60%" align="left" class="RecordRow" nowrap>&nbsp;
        			<bean:write name="element" property="perObject" />
        		</td>
        		<td width="20%" align="center" class="RecordRow" nowrap>
        			<input id="<%=i-1%>" type="text" name="rolelist[<%=i-1%>].rank" value="<bean:write name="element" property="rank" />" onblur='checkValue(this)' onkeypress="event.returnValue=IsDigit(this);" size="10" >
        		</td>
        	</tr>
   		</logic:iterate>
   	</table>
   	<br>
   	<table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1">
   		<tr align="center" > 
   			<td width="100%" colspan="4">
				<html:button styleClass="mybutton" property="b_save" onclick="IsDouble()">
					<bean:message key="button.ok" />
				</html:button>&nbsp;
				<hrms:submit styleClass="mybutton" property="b_default">
					<bean:message key="performance.implement.defaultpoint" />
				</hrms:submit>&nbsp;
				<html:button styleClass="mybutton" property="cancel" onclick="parent.parent.window.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
   		</tr>
   	</table>

<script language="javascript"><!--

//检验数字大小
function checkValue(obj)
{
  	if(obj.value.length>0)
  	{
  		if(obj.value>1)
  		{
  			alert('权重无效！');
  			obj.value='';
  			obj.focus();
  		}
  	} 
}

function IsDigit(obj) 
{
	if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
	{
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
			return false;
		if((event.keyCode == 46) && (values.length==0))//首位是.
			return false;	
			
			
		return true;
	}
	return false;	
}

function IsDouble()      
{      
	for(var j=0;j<=<%=i-1%>;j++)
	{
        var str = document.getElementById(j).value;  
        if(str.length!=0)
        {     
	        reg=/^[-\+]?\d+(\.\d+)?$/;     
	        if(!reg.test(str))
	        {     
	            alert(INPUT_DOUBLE_TYPE_FALSENESS);
	            return ;     
	        }     
        }else
          	document.getElementById(j).value='0';
    }
/*    
    var sum = 0.0;
    for(var j=0;j<=<%=i-1%>;j++)
    {
    	var str = document.getElementById(j).value;
    	sum += str*1000000000000000;
    }
    if(sum>1000000000000000)
    {
    	alert(NOT_MORE_THEN_ONE);
    }else
    {
    	dynaTargetRankForm.action="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do?b_save=link";
    	dynaTargetRankForm.submit();
	}
	*/
	
	dynaTargetRankForm.action="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do?b_save=link&flag=save";
    dynaTargetRankForm.submit();
	
}   
<%if("save".equals(flag)){%>
alert("保存成功！");
<% }%> 
--></script>
</html:form>
