<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%@ page import="java.util.*,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hrms.struts.constant.WebConstant" %>

<%
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    int versionFlag = 1;
    if (userView != null)
        versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版

%>
<body onresize="resizeWindowRefrsh()">
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
    .myfixedDiv
    {
        overflow:auto;
        BORDER-BOTTOM: #C4D8EE 1pt solid;
        BORDER-LEFT: #C4D8EE 1pt solid;
        BORDER-RIGHT: #C4D8EE 1pt solid;
        BORDER-TOP: #C4D8EE 1pt solid ;
    }
    .fixedHeaderTr td{
        border-top: 0px;
        border-color:#C4D8EE !important;
    }
</style>
<script>
    function resizeWindowRefrsh()
    {
        var aclientHeight=document.body.clientHeight;
        if(aclientHeight-55>0)
        {
            document.getElementById('myfixedDiv').style.height=aclientHeight-55;
        }
    }
    var degree_id;
    function showDia(iframe_url, width, height, callback, id) {
        var config = {
            width:width,
            height:height,
            type:'2',
            id:id
        }

        modalDialog.showModalDialogs(iframe_url,id,config,callback);
    }

    function highSet(degreeID)
    {
        var target_url="/performance/options/degreeHighSetList.do?b_query=search`degreeID="+degreeID;
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        /*  if(isIE6()){
             var return_vo=window.showModalDialog(iframe_url,'degreeHighSet','dialogWidth:810px; dialogHeight:450px;resizable:yes;center:yes;scroll:no;status:no;minimize:yes;maximize:yes;');
         }else{
             var return_vo=window.showModalDialog(iframe_url,'degreeHighSet','dialogWidth:800px; dialogHeight:450px;resizable:yes;center:yes;scroll:no;status:no;minimize:yes;maximize:yes;');
         } */
        showDia(iframe_url, 820, 460, "", "perde_win");
    }
    function add()
    {
        var target_url="/performance/options/perDegreeAdd.do?b_add=link`info=save";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        /* if(isIE6()){
            var return_vo=window.showModalDialog(iframe_url,'perdegreeAddWin','dialogWidth:470px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
        }else{
            var return_vo=window.showModalDialog(iframe_url,'perdegreeAddWin','dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
        } */
        showDia(iframe_url, 470, 400, add_ok, "perde_add_win");
    }

    function add_ok(return_vo) {
        if(!return_vo)
            return false;
        if(return_vo.flag=="true")
        {
            alert('保存成功');//应测试要求添加提示信息  haosl add 20170419
            parent.location="/performance/options/perDegreeList.do?b_query=link&degreeId=lastone";
            //perDegreeForm.action="/performance/options/perDegreeList.do?b_query=link";
            //perDegreeForm.submit();

        }
    }
    var degreeId_ = "";
    function edit(degreeId)
    {
        degreeId_ = degreeId;
        var target_url="/performance/options/perDegreeAdd.do?b_edit=link`degreeId="+degreeId+"`info=edit";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        /* if(isIE6()){
            var return_vo=window.showModalDialog(iframe_url,'perdegreeEditWin','dialogWidth:460px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
        }else{
            var return_vo=window.showModalDialog(iframe_url,'perdegreeEditWin','dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
        } */

        showDia(iframe_url, 470, 400, perde_edit_ok, "perde_edit_win");
    }

    function perde_edit_ok(return_vo){
        if(!return_vo)
            return false;
        if(return_vo.flag=="true")
            parent.location="/performance/options/perDegreeList.do?b_query=link&degreeId="+degreeId_;
    }
    function checkdelete(){
        var str="";
        for(var i=0;i<document.perDegreeForm.elements.length;i++)
        {
            if(document.perDegreeForm.elements[i].type=="checkbox" && document.perDegreeForm.elements[i].name!="selbox")
            {
                if(document.perDegreeForm.elements[i].checked==true)
                {
                    str+=document.perDegreeForm.elements[i+1].value+"/";
                }
            }
        }
        if(str.length==0)
        {
            alert(SEL_ONE_DEGREE_DEL);
            return;
        }else{
            if(confirm("确认删除所选等级分类吗？"))
            {
                var hashvo=new ParameterSet();
                hashvo.setValue("deletestr",str);
                var request=new Request({method:'post',asynchronous:false,onSuccess:delInfo,functionId:'9026003005'},hashvo);
            }
        }
    }
    function delInfo(outparamters)
    {
        var flag=outparamters.getValue("delflag");
        if(flag=='0')
            alert(NOTDEL_PERDEGREE);
        else
            parent.location="/performance/options/perDegreeList.do?b_query=link";
    }
    function setCvalue(nid){
        if(nid!='${perDegreeForm.degreeId}')
        {
            var c = document.getElementById('${perDegreeForm.degreeId}');
            var tr = c.parentNode.parentNode;
            if(tr.style.backgroundColor!='')
            {
                tr.style.backgroundColor = '' ;
            }
        }
        degree_id=nid;
        parent.frames['a'].location="/performance/options/perDegreedescList.do?b_query=link&degreeId="+nid;
    }
    function tr_bgcolor(nid){
        var tablevos=document.getElementsByTagName("input");
        for(var i=0;i<tablevos.length;i++){
            if(tablevos[i].type=="checkbox"){
                var cvalue = tablevos[i];
                var td = cvalue.parentNode.parentNode;
                td.style.backgroundColor = '';
            }
        }
        var c = document.getElementById(nid);
        var tr = c.parentNode.parentNode;
        if(tr.style.backgroundColor!=''){
            tr.style.backgroundColor = '' ;
        }else{
            tr.style.backgroundColor = 'FFF8D2' ;
        }
    }
    function checkdegree()
    {
        var str="";
        var count=0;
        for(var i=0;i<document.perDegreeForm.elements.length;i++)
        {
            if(document.perDegreeForm.elements[i].type=="checkbox" && document.perDegreeForm.elements[i].name!="selbox")
            {
                if(document.perDegreeForm.elements[i].checked==true)
                {
                    str=document.perDegreeForm.elements[i+1].value;
                    count++;
                }
            }
        }
        if(count>1 || count==0)
        {
            alert(SEL_ONE_DEGREE);
            return;
        }
        var target_url="/performance/options/perDegreeList.do?b_check=link`degreeID="+str;
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
        var config = {
            width:430,
            height:400,
            type:'2'
        }

        modalDialog.showModalDialogs(iframe_url,'perdegreecheckWin',config);
       // var return_vo=window.showModalDialog(iframe_url,'perdegreecheckWin','dialogWidth:430px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
    }

</script>
<%
    int i=0;
%>
<html:form action="/performance/options/perDegreeList">
    <table width="100%" border="0">
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td align="left" nowrap>

                            <input type='button' class="mybutton" property="b_add"
                                   onclick='add()' value='<bean:message key="jx.param.addegree"/>' />


                            <input type='button' class="mybutton" property="b_delete"
                                   onclick='checkdelete()'
                                   value='<bean:message key="jx.param.deldegree"/>' />

                            <input type='button' class="mybutton" property="b_check"
                                   onclick='checkdegree()'
                                   value='<bean:message key="kq.formula.check"/>' />
                            <logic:equal name="perDegreeForm" property="busitype" value="1">
                                <hrms:tipwizardbutton flag="capability" target="il_body" formname="perDegreeForm"/>
                            </logic:equal>
                            <logic:equal name="perDegreeForm" property="busitype" value="0">
                                <hrms:tipwizardbutton flag="performance" target="il_body" formname="perDegreeForm"/>
                            </logic:equal>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td nowrap>
                <div id="myfixedDiv" class="myfixedDiv">
                    <table width="100%" id='a_table' border="0" cellspacing="0"
                           align="center" cellpadding="0" class="ListTable">
                        <tr class="fixedHeaderTr">
                            <td align="center"  class="TableRow_right common_background_color common_border_color" nowrap>
                                <input type="checkbox" name="selbox"
                                       onclick="batch_select(this, 'setlistform.select');">
                            </td>
                            <td align="center"  class="TableRow" nowrap style="display:none">
                                <bean:message key="lable.menu.main.id" />
                            </td>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="kjg.gather.xuhao" />
                            </td>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="jx.param.degreename" />
                            </td>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="jx.param.degreedescrip" />
                            </td>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="jx.param.degreeflag" />
                            </td>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="jx.param.fengbiflag" />
                            </td>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="kh.field.flag" />
                            </td>

                            <% if(versionFlag==1){%>
                            <td align="center"  class="TableRow" nowrap>
                                <bean:message key="button.sys.cond" />
                            </td>
                            <% } %>

                            <td align="center"  class="TableRow_left common_background_color common_border_color" nowrap>
                                <bean:message key="kh.field.edit" />
                            </td>

                        </tr>
                        <hrms:extenditerate id="element" name="perDegreeForm"
                                            property="setlistform.list" indexes="indexes"
                                            pagination="setlistform.pagination" pageCount="1000"
                                            scope="session">
                            <bean:define id="nid" name="element" property="string(degree_id)" />

                            <%
                                if (i % 2 == 0)
                                {
                            %>
                            <tr class="trShallow" onclick="tr_onclick(this,'#F3F5FC');">
                                        <%
										} else
										{
								%>

                            <tr class="trDeep" onclick="tr_onclick(this,'#E4F2FC');">
                                <%
                                    }
                                    i++;
                                %>
                                <td align="center" style="border-top:0px;" class="RecordRow_right" nowrap onclick="setCvalue('${nid}');">
                                    <hrms:checkmultibox name="perDegreeForm"
                                                        property="setlistform.select" value="true" indexes="indexes" />
                                    <Input type='hidden' id="${nid}"
                                           value='<bean:write name="element" property="string(degree_id)" filter="true"/>' />
                                </td>
                                <td align="left" style="border-top:0px;display:none;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');">
                                    &nbsp;<bean:write name="element" property="string(degree_id)"	filter="true" />

                                    <Input type='hidden'
                                           value='<bean:write name="element" property="string(degree_id)" filter="true"/>'
                                           name='degree_id' />
                                </td>
                                <td align="left" style="border-top:0px;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');">
                                    &nbsp;<%=i %>
                                </td>
                                <td align="left" style="border-top:0px;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');">
                                    &nbsp;<bean:write name="element" property="string(degreename)"
                                                      filter="false" />
                                </td>
                                <td align="left" style="border-top:0px;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');">
                                    &nbsp;<bean:write name="element" property="string(degreedesc)"
                                                      filter="false" />
                                </td>
                                <td align="left" style="border-top:0px;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');"> &nbsp;
                                    <logic:equal name="element" property="string(flag)" value="0">
                                        <bean:message key="jx.param.mark" />
                                    </logic:equal>
                                    <logic:equal name="element" property="string(flag)" value="1">
                                        <bean:message key="jx.param.bili" />
                                    </logic:equal>
                                    <logic:equal name="element" property="string(flag)" value="2">
                                        <bean:message key="jx.param.mix" />
                                    </logic:equal>
                                    <logic:equal name="element" property="string(flag)" value="3">
                                        <bean:message key="jx.param.wx_ratic" />
                                    </logic:equal>
                                    <logic:equal name="element" property="string(flag)" value="4">
                                        <bean:message key="lable.performance.evaluation.ppd" />
                                    </logic:equal>
                                    <logic:equal name="element" property="string(flag)" value="5">
                                        <bean:message key="label.kh.template.total" />
                                    </logic:equal>
                                </td>
                                <td align="left" style="border-top:0px;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');"> &nbsp;
                                    <logic:notEqual name="element" property="string(flag)" value="1">
                                        <logic:equal name="element" property="string(domainflag)"
                                                     value="0">
                                            <bean:message key="jx.param.upmargin" />
                                        </logic:equal>
                                        <logic:equal name="element" property="string(domainflag)"
                                                     value="1">
                                            <bean:message key="jx.param.downmargin" />
                                        </logic:equal>
                                    </logic:notEqual>
                                </td>
                                <td align="left" style="border-top:0px;" class="RecordRow" nowrap
                                    onclick="setCvalue('${nid}');"> &nbsp;
                                    <logic:equal name="element" property="string(used)" value="1">
                                        <bean:message key="lable.lawfile.availability" />
                                    </logic:equal>
                                    <logic:equal name="element" property="string(used)" value="0">
                                        <bean:message key="lable.lawfile.invalidation" />
                                    </logic:equal>
                                </td>

                                <% if(versionFlag==1){%>
                                <td align="center" style="border-top:0px;" class="RecordRow" onclick="setCvalue('${nid}');" nowrap>
                                    <a
                                            onclick="highSet('<bean:write name="element" property="string(degree_id)" filter="true"/>');"><img
                                            src="/images/edit.gif" border=0 style="cursor:hand;"> </a>
                                </td>
                                <% } %>

                                <td align="center" style="border-top:0px;" class="RecordRow_left" onclick="setCvalue('${nid}');" nowrap>
                                    <a
                                            onclick="edit('<bean:write name="element" property="string(degree_id)" filter="true"/>');"><img
                                            src="/images/edit.gif" border=0 style="cursor:hand;"> </a>
                                </td>
                            </tr>
                        </hrms:extenditerate>
                    </table>
                </div>
            </td>
        </tr>
    </table>
</html:form>
<script>
    //tr_bgcolor('${perDegreeForm.degreeId}');
    //parent.frames['a'].location="/performance/options/perDegreedescList.do?b_query=link&degreeId=${perDegreeForm.degreeId}";
    var myfixedDiv = document.getElementById('myfixedDiv');
    if(myfixedDiv){
        myfixedDiv.style.height=(document.body.clientHeight-55)+"px";
        myfixedDiv.style.width=(document.body.clientWidth-10)+"px";
    }
    var table = document.getElementById('a_table');
    if(table.rows.length>1)
    {
        for(var i=1;i<table.rows.length;i++)
        {
            if(ltrim(rtrim(table.rows[i].cells[1].innerText))=='${perDegreeForm.degreeId}')
            {
                //table.rows[i].cells[2].fireEvent("onclick");
                table.rows[i].cells[2].onclick();
                break;
            }
        }
    }
    else
    {
        parent.frames['a'].location="/performance/options/perDegreedescList.do?b_query=link&degreeId=${perDegreeForm.degreeId}";
    }

</script>
</body>
