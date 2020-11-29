<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
function checkRadio(){
    var len=document.outsyncFrom.elements.length;
    var i;
    for (i=0;i<len;i++)
    {
        if (document.outsyncFrom.elements[i].type=="checkbox")
        {
            if(document.outsyncFrom.elements[i].checked){
                if (document.outsyncFrom.elements[i].name != "selbox"){
                    return true;
                }
            }
        }
    }
    return false;
}

function edit(sys_id){
    outsyncFrom.action = "/system/outsync/outsynclist.do?b_edit=link&flag=0&sys_id=" + sys_id;
    outsyncFrom.submit();
}
function add(){
    outsyncFrom.action = "/system/outsync/outsynclist.do?b_add=link&flag=1";
    outsyncFrom.submit();
}

function isExist(){
    var vo=new ParameterSet();
    var request = new Request({method:'post',onSuccess:valid,functionId:'1010040011'});
}   
function valid(outparamters){
    if(checkRadio()){
        var sub_flag = true;
        var org_table = outparamters.getValue("org_table");
        var hr_table = outparamters.getValue("hr_table");
        if(hr_table == 0){
            if(!confirm("人员表不存在，是否启动")){
                sub_flag = false;
            }
        }
        if(org_table == 0){
            if(!confirm("机构表不存在，是否启动")){
                sub_flag = false;
            }
        }
        if(sub_flag){
            outsyncFrom.action = "/system/outsync/outsynclist.do?b_valid=link&flag=2";
            outsyncFrom.submit();
        }
    }else{
        alert("请选择对象！");
    }
}

function del(){
    if(checkRadio()){
        if(confirm("确定要删除外部同步系统设置么？")){
            outsyncFrom.action = "/system/outsync/outsynclist.do?b_del=link&flag=3";
            outsyncFrom.submit();
        }
    }else{
        alert("请选择对象！");
    }
}
function rebreak(){
    outsyncFrom.action = "/sys/export/SearchHrSyncSet.do?b_query=link";
    outsyncFrom.submit();
}

//-->
</script>
<html:form action="/system/outsync/outsynclist.do">
    <table width="80%" border="0" cellpadding="0" cellspacing="0"
        align="center">
        <tr>
            <td>
                <table width="100%" border="0" cellpadding="0" cellspacing="0"
                    class="ListTable">
                    <tr>
                        <td align="center" class="TableRow" nowrap>
                            <input type="checkbox" name="selbox"
                                onclick="batch_select(this,'pagination.select');"
                                title='<bean:message key="label.query.selectall"/>'>
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="label.code" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="column.name" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="label.interface.address" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="label.external.function.name" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="label.messages" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="label.failure.count" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="column.sys.status" />
                        </td>
                        <td align="center" class="TableRow" nowrap>
                            <bean:message key="label.commend.edit" />
                        </td>
                    </tr>
                    <hrms:paginationdb id="element" name="outsyncFrom"
                        sql_str="outsyncFrom.sql_str" where_str="outsyncFrom.sql_where"
                        columns="outsyncFrom.columns" page_id="pagination" pagerows="10"
                        indexes="indexes">
                        <tr>
                            <td align="center" class="RecordRow" nowrap>
                                &nbsp;
                                <logic:notEqual name="element" property="sys_id" value="hrcloud">
                                <hrms:checkmultibox name="outsyncFrom"
                                    property="pagination.select" value="true" indexes="indexes" />
                                </logic:notEqual>
                                &nbsp;
                            </td>
                            <td align="left" class="RecordRow" nowrap>
                                &nbsp;<bean:write name="element" property="sys_id" />&nbsp;
                            </td>
                            <td align="left" class="RecordRow" nowrap>
                                &nbsp;<bean:write name="element" property="sys_name" />&nbsp;
                            </td>
                            <td align="left" class="RecordRow" nowrap>
                                &nbsp;<bean:write name="element" property="url" />&nbsp;
                            </td>
                            <td align="center" class="RecordRow" nowrap>
                                &nbsp;<bean:write name="element" property="sync_method" />&nbsp;
                            </td>
                            <logic:notEmpty name="element" property="send">
	                            <logic:equal name="element" property="send" value="1">
	                                <td align="center" class="RecordRow" nowrap>
	                                    &nbsp;变动通知&nbsp;
	                                </td>
	                            </logic:equal>
	                            <logic:equal name="element" property="send" value="0">
	                                <td align="center" class="RecordRow" nowrap>
	                                    &nbsp;<bean:message key="label.synchronous.unsend"/>&nbsp;
	                                </td>
	                            </logic:equal>
	                            <logic:equal name="element" property="send" value="2">
	                                <td align="center" class="RecordRow" nowrap>
	                                    &nbsp;变动内容&nbsp;
	                                </td>
	                            </logic:equal>
	                            
	                            <logic:equal name="element" property="send" value="3">
	                                <td align="center" class="RecordRow" nowrap>
	                                    &nbsp;指标变动前后内容&nbsp;
	                                </td>
	                            </logic:equal>
                            </logic:notEmpty>
                            <logic:empty name="element" property="send">
                                <td align="center" class="RecordRow" nowrap>
                                    &nbsp;<bean:message key="label.synchronous.unsend"/>&nbsp;
                                </td>
                            </logic:empty>
                            <td align="center" class="RecordRow" nowrap>
                                <bean:define id="time" name="element" property="fail_time"></bean:define>
                                <bean:define id="max" name="outsyncFrom" property="max_time"></bean:define>
                                <%
                                    int fail_time = 0;
                                    int max_time = 0;
                                    
                                    if(time != null && !time.toString().equals(""))
                                        fail_time = Integer.parseInt((String)time);
                                    
                                    if(max != null && !max.toString().equals(""))
                                        max_time = Integer.parseInt((String) max);
                                    
                                    if (fail_time >= max_time && max_time != 0) {
                                        out.write("<font color='red'>" + time + "</font>");
                                    } else {
                                        out.write((String)time);
                                    }
                                %>
                            </td>
                            <logic:equal name="element" property="state" value="0">
                                <td align="center" class="RecordRow" nowrap>
                                    &nbsp;<bean:message key="column.sys.unvalid" />&nbsp;
                                </td>
                            </logic:equal>
                            <logic:equal name="element" property="state" value="1">
                                <td align="center" class="RecordRow" nowrap>
                                    &nbsp;<bean:message key="column.sys.valid" />&nbsp;
                                </td>
                            </logic:equal>
                            <td align="center" class="RecordRow" nowrap>
                            	<logic:notEqual name="element" property="sys_id" value="hrcloud">
                                <a href="javascript:edit('<bean:write name="element" property="sys_id" filter="true"/>');">
                                    <img src="/images/edit.gif" border='0'> </a>
                                </logic:notEqual>
                            </td>
                        </tr>
                    </hrms:paginationdb>
                </table>
            </td>
        </tr>
        <tr>
            <td class="RecordRowP">
                <table width="100%" align="center">
                    <tr>
                        <td align="left" nowrap class="tdFontcolor">
                            <bean:message key="label.page.serial" />
                            <bean:write name="pagination" property="current" filter="true" />
                            <bean:message key="label.page.sum" />
                            <bean:write name="pagination" property="count" filter="true" />
                            <bean:message key="label.page.row" />
                            <bean:write name="pagination" property="pages" filter="true" />
                            <bean:message key="label.page.page" />
                        </td>
                        <td align="right" nowrap class="tdFontcolor">
                            <hrms:paginationdblink name="outsyncFrom" property="pagination"
                                nameId="outsyncFrom" scope="page">
                            </hrms:paginationdblink>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td align="center" valign="bottom" height="35px">
                <html:button styleClass="mybutton" property="b_add" onclick="add()">
                    <bean:message key="button.insert" />
                </html:button>
                <html:button styleClass="mybutton" property="b_del" onclick='del()'>
                    <bean:message key="button.delete" />
                </html:button>
                <html:button styleClass="mybutton" property="b_valid"
                    onclick='isExist();'>
                    <bean:message key="column.sys.valid" />
                </html:button>
                <html:button styleClass="mybutton" property="b_return"
                    onclick="rebreak();">
                    <bean:message key="button.return" />
                </html:button>
            </td>
        </tr>
    </table>
</html:form>

