<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*, com.hrms.hjsj.sys.EncryptLockClient" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.kh_plan.ExamPlanForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.constant.WebConstant,
                 com.hrms.hjsj.sys.EncryptLockClient" %>
<%@page import="com.hjsj.hrms.utils.FuncVersion" %>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">

<style>

    .textColorReadPer {
        BACKGROUND-COLOR:#E4E4E4;
        border: 1pt solid #C4D8EE;}

    .textColorWritePer {
        border: 1pt solid #C4D8EE;}

</style>
<%
    ExamPlanForm myForm=(ExamPlanForm)session.getAttribute("examPlanForm");
    int dataSize = myForm.getSetlist().size();
    int currentPage = myForm.getSetlistform().getPagination().getCurrent();
    int pagecount = myForm.getSetlistform().getPagination().getPageCount();
    int pages = myForm.getSetlistform().getPagination().getPages();
    int lastIndex = pagecount;//当前页的最后一条

    if(pages>1&&currentPage<pages)
        lastIndex = pagecount;
    else if(pages>1&&currentPage==pages)
        lastIndex=dataSize-pagecount*(currentPage-1);
    else if(pages==1)
        lastIndex = dataSize;

    //是否有目标管理的功能
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    boolean flag = false;
    if(lockclient!=null)
    {
        if(lockclient.isHaveBM(29)){
            flag=true;
        }
    }
%>
<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../../components/extWidget/proxy/TransactionProxy.js"></script>
<script language="javascript" src="/performance/kh_plan/examPlanAdd.js"></script>
<script language="JavaScript" src="../../components/codeSelector/codeSelector.js"></script>
<html:form action="/performance/kh_plan/examPlanAdd">

    <html:hidden name="examPlanForm" property="busitype" styleId="planBusitype" />
    <html:hidden name="examPlanForm" styleId="theyear" property="examPlanVo.string(theyear)"/>
    <html:hidden name="examPlanForm" styleId="themonth" property="examPlanVo.string(themonth)"/>
    <html:hidden name="examPlanForm" styleId="thequarter" property="examPlanVo.string(thequarter)"/>
    <html:hidden name="examPlanForm" styleId="start_date" property="examPlanVo.string(start_date)"/>
    <html:hidden name="examPlanForm" styleId="end_date" property="examPlanVo.string(end_date)"/>
    <%-- 连接模板和参数的桥梁 add by 刘蒙 --%>
    <html:hidden name="examPlanForm" property="requiredFieldStr" styleId="requiredFieldStr"/>

    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td valign="top">
                <table width="100%" border="0" cellpadding="3" cellspacing="0"
                       align="center" class="ListTableF">
                    <tr height="20">
                        <td colspan="4" align="left" class="TableRow">
                            <logic:equal name="examPlanForm" property="busitype" value="0">
                                <bean:message key="lable.performance.perPlan" />&nbsp;
                            </logic:equal>
                            <logic:notEqual name="examPlanForm" property="busitype" value="0">
                                <bean:message key="lable.performance.evaluatePerPlan" />&nbsp;
                            </logic:notEqual>
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.planid" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30" styleClass="textColorRead"
                                       name="examPlanForm" styleId="plan_id"
                                       property="examPlanVo.string(plan_id)" readonly="true" />
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.spstatus" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:hidden name="examPlanForm" styleId="status"
                                         property="examPlanVo.string(status)" />
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" property="statusName"  readonly="true" styleClass="textColorRead"/>
                        </td>
                    </tr>
                    <tr class="trDeep1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.name" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30" styleClass="textColorWrite"
                                       name="examPlanForm" styleId="name"
                                       property="examPlanVo.string(name)" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color='red'>*</font>
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.plantype" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:radio name="examPlanForm" styleId="plan_type0"
                                        property="examPlanVo.string(plan_type)" value="0" />
                            <bean:message key="jx.khplan.norecordname" />
                            <html:radio name="examPlanForm" styleId="plan_type1"
                                        property="examPlanVo.string(plan_type)" value="1" />
                            <bean:message key="jx.khplan.recordname" />
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.unit" />
                        </td>
                        <td align="left"  class="RecordRow" nowrap>
                            <html:hidden  name="examPlanForm" styleId="b0110"
                                          property="examPlanVo.string(b0110)" />
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" styleId="codeName"
                                       property="codeName"  readonly="true" styleClass="textColorRead"/>
                            <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                <img ctrltype='3' nmodule='5' src="/images/edit.gif" plugin="codeselector" codesetid="UM" inputname="codeName" valuename="examPlanVo.string(b0110)" multiple ="false"/>
                            </logic:equal>
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.objectype" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:radio name="examPlanForm" styleId="object_type"
                                        property="examPlanVo.string(object_type)" value="2" />
                            <bean:message key="label.query.employ" />

                            <logic:equal name="examPlanForm" property="busitype" value="0">
                                <html:radio name="examPlanForm" styleId="object_type"
                                            property="examPlanVo.string(object_type)" value="1" />
                                <bean:message key="jx.khplan.team" />
                                <html:radio name="examPlanForm" styleId="object_type"
                                            property="examPlanVo.string(object_type)" value="3" />
                                <bean:message key="jx.khplan.unit" />
                                <html:radio name="examPlanForm" styleId="object_type"
                                            property="examPlanVo.string(object_type)" value="4" />
                                <bean:message key="column.sys.dept" />
                            </logic:equal>
                        </td>
                    </tr>
                    <tr class="trDeep1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.cycle" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" styleId="cycle"
                                       property="examPlanVo.string(cycle)"  readonly="true"   styleClass="textColorRead"/>
                            <a onclick="getCycle();">

                                <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                    <img src="/images/edit.gif" border=0>
                                </logic:equal>
                                <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="5">
                                    <img src="/images/view.gif" border=0>
                                </logic:equal>
                                <logic:notEqual name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                    <logic:notEqual name="examPlanForm" property="examPlanVo.string(status)" value="5">
                                        <img src="/images/view.gif" border=0>
                                    </logic:notEqual>
                                </logic:notEqual>
                            </a>
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.gathertype" /><!-- 采集类型 -->
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:radio name="examPlanForm" styleId="gather_type"
                                        property="examPlanVo.string(gather_type)" value="0" />
                            <bean:message key="jx.khplan.internet" /><!-- 网上 -->
                            <%
                                if(lockclient.isHaveBM(21)){}//机读控制
                            %>
                            <logic:equal name="examPlanForm" property="busitype" value="1">
								<span id="hide_radio">
									<logic:equal name="examPlanForm" property="byModel" value="False">
                                        <html:radio name="examPlanForm" styleId="gather_type"
                                                    property="examPlanVo.string(gather_type)" value="1"  />
                                        <bean:message key="label.module.jd" /><!-- 机读 -->
                                    </logic:equal>
									<logic:notEqual name="examPlanForm" property="byModel" value="False">
                                        <html:radio name="examPlanForm" styleId="gather_type"
                                                    property="examPlanVo.string(gather_type)" value="1" disabled="true"  />
                                        <bean:message key="label.module.jd" /><!-- 机读 -->
                                    </logic:notEqual>
								</span>
                            </logic:equal>
                            <logic:equal name="examPlanForm" property="busitype" value="0">
								<span id="hide_radio">
									<html:radio name="examPlanForm" styleId="gather_type"
                                                property="examPlanVo.string(gather_type)" value="1" />
                                    <bean:message key="label.module.jd" /><!-- 机读 -->
									<html:radio name="examPlanForm" styleId="gather_type"
                                                property="examPlanVo.string(gather_type)" value="2" />
                                    <bean:message key="label.module.internetandjd" /><!-- 网上+机读 -->
								</span>
                            </logic:equal>
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.khtimeqj" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <input type='text' id="khtimeqj" maxlength="50" size="30"  readonly="true" class="textColorRead">
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.khmethod" /><!-- 考核方法 -->
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <input type="hidden" id="methodflag" value="">
                            <html:radio name="examPlanForm" styleId="method"
                                        property="examPlanVo.string(method)" value="1" onclick="setMehod()"/>
                            <bean:message key="jx.khplan.khmethod1" /><!-- 360度 -->

                            <logic:equal name="examPlanForm" property="busitype" value="0">
                                <% if(flag){ //如果锁写了目标管理，才能使用目标管理功能 %>
                                <html:radio name="examPlanForm" styleId="method"
                                            property="examPlanVo.string(method)" value="2" onclick="setMehod()"/><!-- 目标管理 -->
                                <bean:message key="jx.khplan.khmethod2" />
                                <% } %>
                            </logic:equal>
                        </td>
                    </tr>
                    <tr class="trDeep1">
                        <td align="right" class="RecordRow" nowrap>
                            <logic:equal name="examPlanForm" property="busitype" value="0">
                                <bean:message key="jx.khplan.template" /><!-- 关联模板 -->
                            </logic:equal>
                            <logic:equal name="examPlanForm" property="busitype" value="1">
                                <bean:message key="jx.khplan.templatenl" />
                            </logic:equal>
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:hidden  name="examPlanForm" styleId="template_id"
                                          property="examPlanVo.string(template_id)" />
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" styleId="templateName"
                                       property="templateName"  readonly="true"  styleClass="textColorRead"/>
                            <a onclick="getTemplate();">

                                <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                    <img src="/images/edit.gif" border=0>
                                </logic:equal>
                                <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="5">
                                    <img src="/images/view.gif" border=0>
                                </logic:equal>
                                <logic:notEqual name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                    <logic:notEqual name="examPlanForm" property="examPlanVo.string(status)" value="5">
                                        <img src="/images/view.gif" border=0>
                                    </logic:notEqual>
                                </logic:notEqual>

                            </a>&nbsp;<font color='red'>*</font>
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.planparam" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:hidden name="examPlanForm" styleId="parameter_content"
                                         property="examPlanVo.string(parameter_content)" />
                            <a onclick="getParam();">

                                <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                    <img src="/images/edit.gif" border=0>
                                </logic:equal>
                                <logic:equal name="examPlanForm" property="examPlanVo.string(status)" value="5">
                                    <img src="/images/edit.gif" border=0>
                                </logic:equal>
                                <logic:notEqual name="examPlanForm" property="examPlanVo.string(status)" value="0">
                                    <logic:notEqual name="examPlanForm" property="examPlanVo.string(status)" value="5">
                                        <img src="/images/view.gif" border=0>
                                    </logic:notEqual>
                                </logic:notEqual>

                            </a>&nbsp;<font color='red'>*</font>
                        </td>
                    </tr>
                    <tr  class="trShallow1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.agreeuser" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30"
                                       styleId="agree_user" name="examPlanForm"
                                       property="examPlanVo.string(agree_user)"  readonly="true" styleClass="textColorRead"/>
                        </td>
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.agreedate" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30"
                                       styleId="agree_date" name="examPlanForm"
                                       property="examPlanVo.string(agree_date)"  readonly="true" styleClass="textColorRead"/>
                        </td>
                    </tr>
                    <tr  class="trDeep1">
                        <td align="right" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.approveresult" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" styleId="approve_result"
                                       property="examPlanVo.string(approve_result)"  readonly="true" styleClass="textColorRead"/>
                        </td>
                        <td class="RecordRow" align="right">
                            <bean:message key="jx.plan.plan_visibility" />
                        </td>
                        <td class="RecordRow">
                            <html:checkbox styleId="plan_visibility" name="examPlanForm"
                                           property="plan_visibility" value="1" />
                            <bean:message key="jx.plan.plan_visibility_text" />
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td colspan="4" class="RecordRow">
                            <bean:message key="jx.khplan.agreeidea" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="RecordRow">
                            <table border="0" cellspacing="0" width="100%"
                                   cellpadding="2" align="center">
                                <tr  class="trDeep1">
                                    <td>
                                        <html:textarea name="examPlanForm" styleId="agreeidea"
                                                       property="examPlanVo.string(agree_idea)" cols="90" rows="6"
                                                       readonly="true" ></html:textarea>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td colspan="4" class="RecordRow">
                            <bean:message key="jx.khplan.plandescrip" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="RecordRow">
                            <table border="0" cellspacing="0" width="100%"
                                   cellpadding="2" align="center">
                                <tr  class="trDeep1">
                                    <td>
                                        <html:textarea name="examPlanForm" styleId="descript"
                                                       property="examPlanVo.string(descript)" cols="90" rows="6"
                                        ></html:textarea>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr  class="trShallow1">
                        <td colspan="4" class="RecordRow">
                            <bean:message key="jx.khplan.khmb" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="RecordRow">
                            <table border="0" cellspacing="0" width="100%"
                                   cellpadding="2" align="center">
                                <tr  class="trDeep1">
                                    <td>
                                        <html:textarea name="examPlanForm" styleId="target"
                                                       property="examPlanVo.string(target)" cols="90" rows="6"
                                        ></html:textarea>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td colspan="4" class="RecordRow">
                            <bean:message key="jx.khplan.khcontent" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="RecordRow">
                            <table border="0" cellspacing="0" width="100%"
                                   cellpadding="2" align="center">
                                <tr  class="trDeep1">
                                    <td>
                                        <html:textarea name="examPlanForm" styleId="content"
                                                       property="examPlanVo.string(content)" cols="90" rows="6"
                                        ></html:textarea>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr  class="trShallow1">
                        <td colspan="4" class="RecordRow">
                            <bean:message key="jx.khplan.khprograme" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="RecordRow">
                            <table border="0" cellspacing="0" width="100%"
                                   cellpadding="2" align="center">
                                <tr  class="trDeep1">
                                    <td>
                                        <html:textarea name="examPlanForm" styleId="flow"
                                                       property="examPlanVo.string(flow)" cols="90" rows="6"
                                        ></html:textarea>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr class="trShallow1">
                        <td colspan="4" class="RecordRow">
                            <bean:message key="jx.khplan.khresultapply" />
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="RecordRow">
                            <table border="0" cellspacing="0" width="100%"
                                   cellpadding="2" align="center">
                                <tr  class="trDeep1">
                                    <td>
                                        <html:textarea name="examPlanForm" styleId="result"
                                                       property="examPlanVo.string(result)" cols="90" rows="6"
                                        ></html:textarea>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr  class="trShallow1">
                        <td align="left" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.creator" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" styleId="create_user"
                                       property="examPlanVo.string(create_user)"  readonly="true" styleClass="textColorRead"/>
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <bean:message key="jx.khplan.createdate" />
                        </td>
                        <td align="left" class="RecordRow" nowrap>
                            <html:text maxlength="50" size="30"
                                       name="examPlanForm" styleId="create_date"
                                       property="examPlanVo.string(create_date)" readonly="true" styleClass="textColorRead" />
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <table width='100%' align='center'>
                    <tr>
                        <td align='center' style="padding-top:5px;">
                            <input type='button'
                                   value='<bean:message key='button.save' />'
                                   class="mybutton" onclick='save();'>
                            <input type='button' id="button_goback"
                                   value='<bean:message key='button.return' />'
                                   onclick='goback();' class="mybutton">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    <script>
        setkhtimeqj();
        checkTemplateType();//根据模板是含有个性项目 确定考核方法
        var status = document.getElementById('status').value;
        if(status !='0'/*  && status !='5' */)//暂停的计划也不允许修改 chent 20170418
        {
            for(var i=0;i<document.examPlanForm.elements.length;i++) {
                document.examPlanForm.elements[i].disabled = true;
                setDisabledBackColor(document.examPlanForm.elements[i]);
            }
            document.getElementById('button_goback').disabled=false;
            setDisabledBackColor(document.getElementById('button_goback'));
        }
        var approve_result = document.getElementById('approve_result').value;
        if(approve_result =='1')
            document.getElementById('approve_result').value='<bean:message key='label.agree' />';
        if(approve_result =='0')
            document.getElementById('approve_result').value='<bean:message key='label.nagree' />';

        setCycle();
        if(status =='5')
        {	//暂停状态 考核计划类型为不记名时候不允许修改
            var plan_type = document.getElementById('plan_type0');
            if(plan_type.checked)
            {
                plan_type.disabled=true;
                setDisabledBackColor(plan_type);
                document.getElementById('plan_type1').disabled=true;
                setDisabledBackColor(document.getElementById('plan_type1'));
            }

        }

        function setDisabledBackColor(dom) {
            if(!window.showModalDialog) {
                if (dom.disabled == true) {
                    dom.style.background = "#d9d9d9";
                    dom.style.color="#979797";
                } else {
                    dom.style.background = "";
                    dom.style.color="";
                }
            }
        }
    </script>
</html:form>
