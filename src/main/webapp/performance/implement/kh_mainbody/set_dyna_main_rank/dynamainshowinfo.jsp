<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.implement.DynaMainRankForm" %>
<%int i =0;
DynaMainRankForm dynaMainRankForm=(DynaMainRankForm)session.getAttribute("dynaMainRankForm");
%>

<html:form action="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   		<thead>
   			<tr>
   				<td  width="8%" align="center" class="TableRow" nowrap>
   					<bean:message key="label.serialnumber"/>
   				</td>
   				<td width="35%" align="center" class="TableRow" nowrap>
   					<bean:message key="lable.performance.perObject"/>
   				</td>
   				<td width="35%" align="center" class="TableRow" nowrap>
   					<bean:message key="lable.performance.perMainBodySort"/>
   				</td>
   				<td width="22%" align="center" class="TableRow" nowrap>
   					<bean:message key="label.kh.template.qz"/>
   				</td>
   			</tr>
   		</thead>
   		
   		<logic:iterate id="element" name="dynaMainRankForm" property="rolelist">
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
        		<td  align="center" class="RecordRow" nowrap>
        			<%=i%>
        		</td>
        		<td  align="left" class="RecordRow" nowrap>
        			&nbsp;<bean:write name="element" property="perObject" />
        		</td>
        		<td  align="left" class="RecordRow" nowrap>
        			&nbsp;<bean:write name="element" property="perMainBodySort" />
        		</td>
        		<td  align="center" class="RecordRow" nowrap>
        			<%-- 确认的主体权重为0，且不允许修改 add by 刘蒙 --%>
					<bean:define id="rankValue" value="${element.map.rank }"></bean:define>
					<bean:define id="isDisabled" value=""></bean:define>
					<logic:equal name="element" property="pbOpt" value="1">
						<bean:define id="isDisabled" value="disabled"></bean:define>
						<bean:define id="rankValue" value="0.0"></bean:define>
					</logic:equal>
        			<input id="<%=i-1%>" type="text" name="rolelist[<%=i-1%>].rank" value="${rankValue }"
        				 size="10" onkeypress="event.returnValue=IsDigit(this);" ${isDisabled } class="inputtext">
        		</td>
        	</tr>
   		</logic:iterate>
   	</table>
   	<table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1">
   		<tr align="center" > 
   			<td width="100%" colspan="4" style="padding-top:5px;">
   					<html:button styleClass="mybutton" property="b_save" onclick="IsDouble(this)">
						<bean:message key="button.ok" />
					</html:button>
					<hrms:submit styleClass="mybutton" property="b_default">
						<bean:message key="performance.implement.defaultpoint" />
					</hrms:submit>	
				<html:button styleClass="mybutton" property="cancel" onclick="javascript:window.open('','_top').close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
   		</tr>
   	</table>

<script language="javascript">
	if("${dynaMainRankForm.successflag}"=="0"){
		alert(PERFORMANCE_SINGLEGRADE_SAVESUCCESS);//避免重复点击值来不及传到后台报错  2013.11.14 pjf
		<%
			dynaMainRankForm.setSuccessflag("1");
		%>
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
function IsDouble(btn)      
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

    var sum = 0.0;
    for(var j=0;j<=<%=i-1%>;j++){
    	var str = document.getElementById(j).value;
    	sum += str*1000000000000000;
    }

    if(sum!=1000000000000000){
    	alert(NOT_EQUEAL_ONE);
    }else{
    	if(btn)
    		btn.disabled = true;//防止重复提交
    	dynaMainRankForm.action="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?b_save=link";
	    dynaMainRankForm.submit();
	    //此处提示会在火狐下一闪而逝，修改为从后台传回成功标记，前台根据标记提示是否成功
	    //alert(PERFORMANCE_SINGLEGRADE_SAVESUCCESS);//避免重复点击值来不及传到后台报错  2013.11.14 pjf
	}

/*
	dynaMainRankForm.action="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?b_save=link";
	dynaMainRankForm.submit();
*/
} 
//解决 默认权重按钮 被自动添加选中效果的问题 haosl 2018-03-28
var btn = document.getElementsByName('b_default')[0];
if(btn){
	btn.focus();
	btn.blur();
}

</script>
</html:form>
