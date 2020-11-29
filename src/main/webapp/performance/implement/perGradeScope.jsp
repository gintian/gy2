<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm"%>

<%
    String planStatus = (String)request.getParameter("planStatus");
    String isClose=null;
    if(request.getParameter("closeWin")!=null){
        isClose = (String)request.getParameter("closeWin");
    }
%>

<html>

<style>

    div#treemenu
    {
        BORDER-BOTTOM:#94B6E6 1pt solid;
        BORDER-LEFT: #94B6E6 1pt solid;
        BORDER-RIGHT: #94B6E6 1pt solid;
        BORDER-TOP: #94B6E6 1pt solid;
        width: 400px;
        height: 200px;
        overflow: auto;
    }
</style>
<script LANGUAGE=javascript src="/js/function.js"></script>
<script language='javascript'>
    if ("<%=isClose%>"=="true"){
        parent.window.close();
    }
    function onkeyperssfunc(e){
        e = window.event || e;
        if (IsDigit(e)==0){
            if(e.returnValue){
                e.returnValue = false ;
            }
            if(e.preventDefault ){
                e.preventDefault();
            }
            return false;
        }
        return true;
    }
    function IsDigit(e)
    {
        var keycode;
        if(navigator.appName == "Microsoft Internet Explorer"){
            keycode = event.keyCode;

        }else{
            keycode = e.which;
        }
        if ( !(((keycode >= 48) && (keycode <= 57))
                || (keycode == 13) || (keycode == 46)
                || (keycode == 45)))
        {
            return 0;
        }
        return keycode;

    }
    //检验数字类型
    function checkValue(obj)
    {
        if(obj.value.length>0)
        {
            if(!checkIsNum2(obj.value))
            {
                alert('请输入数值！');
                obj.value='';
                obj.focus();
            }
        }
    }

    function enter()
    {
        var downScore;
        var upScore;
        var number=0;
        var flag=true;
        for(var i=0;i<document.implementForm.elements.length;i++)
        {
            if(document.implementForm.elements[i].type=='text')
            {
                if(number==0 || number%2==0)
                {
                    downScore=0;
                    upScore=0;
                }
                if(ltrim(rtrim(document.implementForm.elements[i].value))!='')
                {
                    if(number==0 || number%2==0)
                        downScore=document.implementForm.elements[i].value;
                    else
                        upScore=document.implementForm.elements[i].value;
                }
                if(number!=0 && number%2!=0)
                {
                    if((downScore!=null && downScore.length>0) && (upScore!=null && upScore.length>0))
                    {
                        if(parseFloat(downScore)>parseFloat(upScore))
                        {
                            alert("下限分值不能大于上限分值！");
                            flag=false;
                            break;
                        }
                    }
                }
                number++;
            }
        }
        if(!flag)
            return;

        document.implementForm.action="/performance/implement/performanceImplement.do?b_saveGradeScope=link&closeWin=true";
        document.implementForm.submit();
    }

</script>


<body>
<html:form action="/performance/implement/performanceImplement">
    <table width='489' align="center">
        <tr>
            <td width='100%'>
                <div id='treemenu' style="width:100%">
                    <table width="100%" border="0" cellspacing="0"
                           cellpadding="0" class="ListTable">
                        <thead>
                        <tr>
                            <td align="center" class="TableRow" nowrap style="border-left:0;border-top:0;">
                                <bean:message key='label.serialnumber' />
                            </td>
                            <td align="center" class="TableRow" nowrap style="border-top:0;">
                                <bean:message key='kpi.originalData.KpiTargetObjecType' />
                            </td>
                            <td align="center" class="TableRow" nowrap style="border-top:0;">
                                <bean:message key='label.performance.downScopeValue' />
                            </td>
                            <td align="center" class="TableRow" nowrap style="border-right:0;border-top:0;">
                                <bean:message key='label.performance.upScopeValue' />
                            </td>
                        </tr>
                        </thead>
                        <%
                            int i = 0;
                        %>
                        <logic:iterate id="element" name="implementForm" property="gradeScopeList" indexId="index">
                            <%
                                if (i % 2 == 0) {
                            %>
                            <tr class="trShallow">
                                        <%
									} else {
								%>
                            <tr class="trDeep">

                                <%
                                    }
                                    i++;
                                %>
                                <td align="center" class="RecordRow" nowrap style="border-left:0;">
                                    <%=(i)%>
                                </td>
                                <td align="center" class="RecordRow" nowrap>
                                    <bean:write name="element" property="name" filter="true" />
                                </td>
                                <td align="center" class="RecordRow" nowrap>
                                    <input type="text" name="<%="gradeScopeList[" + index + "].DownScope"%>" id="downScopeScore"
                                           onblur='checkValue(this)' onkeypress="onkeyperssfunc(event)" class="inputtext" value="<bean:write  name="element" property="DownScope"/>"
                                           size='6' />

                                </td>
                                <td align="center" class="RecordRow" nowrap  style="border-right:0;">
                                    <input type="text" name="<%="gradeScopeList[" + index + "].UpScope"%>" id="upScopeScore"
                                           onblur='checkValue(this)' onkeypress="onkeyperssfunc(event)" class="inputtext" value="<bean:write  name="element" property="UpScope"/>"
                                           size='6' />

                                </td>
                            </tr>
                        </logic:iterate>
                    </table>
                </div>
            </td>
        </tr>
        <tr>
            <td align="center" style="height:35px">

                <%if(planStatus!=null && planStatus.trim().length()>0 && !planStatus.equals("3") && !planStatus.equals("5") && !planStatus.equals("8")){ %>
                <input type='button' id="bodyDefine" value='<bean:message key='button.ok' />' disabled onclick='enter()' class="mybutton">
                <%}else{ %>
                <input type='button' id="bodyDefine" value='<bean:message key='button.ok' />' onclick='enter()' class="mybutton">
                <%} %>
                &nbsp;
                <input type='button' value='<bean:message key='lable.tz_template.cancel' />'
                       onclick='javascript:parent.window.close()' class="mybutton">
            </td>
        </tr>
    </table>

</html:form>
</body>
</html>
