<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.utils.Sql_switcher,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.businessobject.sys.SysParamBo" %>

<%
    int dbtype=Sql_switcher.searchDbServer();
    String aurl = (String) request.getServerName();
    String port = request.getServerPort() + "";
    String url_p = "HTTP://" + aurl + ":" + port;

    String userName = null;
    UserView usView = (UserView)session.getAttribute(WebConstant.userView);
    if(usView != null){
        userName=usView.getUserName();
    }
    String themes = "";
    if(usView!=null)
        themes=SysParamBo.getSysParamValue("THEMES",usView.getUserName());
    ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");
    ArrayList objectList=implementForm.getPerObjectDataListform().getList();
    ArrayList allObjs = implementForm.getAllObjs();
    String planStatus = implementForm.getPlanStatus();
    String method = implementForm.getMethod();
    String busitype = implementForm.getBusitype(); // 业务分类字段 =0(绩效考核); =1(能力素质)
    String templateStatus=implementForm.getTemplateStatus();//1 权重 0 分值
    String HandEval=implementForm.getHandEval();
    String isDistribute = implementForm.getIsDistribute();
    String sqlString = implementForm.getSqlString();
    String object_type = implementForm.getObject_type();
    String planid=implementForm.getPlanid();
    if(templateStatus!=null && templateStatus.equals("1"))
        templateStatus="true";
    else
        templateStatus="false";
    pageContext.setAttribute("templateStatus",templateStatus);
    String disabled="disabled";
    String inverse="";
    if(planStatus.equals("3")||planStatus.equals("5"))
    {
        disabled="";
        inverse="disabled";
    }
    String disabled2="";
    if(HandEval!=null&&HandEval.equalsIgnoreCase("TRUE"))
        disabled2="disabled";
    if(planid.equals(""))
    {
        disabled="disabled";
        disabled2="disabled";
        inverse="disabled";
    }
    String orderSql = implementForm.getOrderSql().trim();
    RecordVo planVo = implementForm.getPlanVo();
    int gather_type=planVo.getInt("gather_type");//0 网上 1 机读 2 网上+机读
    String scoreWay = implementForm.getScoreWay();//0 数据采集 1 网上打分
    String isBachGenerateTarget = implementForm.getIsBachGenerateTarget();

    int dataSize = implementForm.getPerObjectDataList().size();

    // 计划实施功能权限控制
    String p0Funcids="";
    if(!disabled.equals("disabled"))
    {
        if((planStatus.equals("3") || planStatus.equals("5")) && method.equals("2"))
        {  //目标计划发布和暂停
            if(isDistribute.equals("0"))
            {
                p0Funcids+=",326030102";
            }else if(planStatus.equals("3") && isDistribute.equals("1"))
            {
                p0Funcids+=",326030102";
            }
            p0Funcids+=",326030103";

        }else
        {
            p0Funcids+=",326030102";
        }
    }else
    {
        if(planStatus.equals("8") && method.equals("2"))
        { //目标计划分发
            p0Funcids+=",326030102";
        }else if(planStatus.equals("4") && method.equals("2"))
        {//目标计划启动
            p0Funcids+=",326030102";
        }
    }
    if(!planStatus.equals("5")&&!planStatus.equals("3"))
    {
        p0Funcids+=",326030103";
    }
    if(planStatus.equals("3") || planStatus.equals("8"))
    {
        p0Funcids+=",326030100,326030101";
    }else
    {
        p0Funcids+=",326030100,326030101";
    }
    p0Funcids+=",326030105";

    if(method.equals("2") && allObjs.size()>0)
    { //目标计划
        p0Funcids+=",326030106";
    }
    if(method.equals("2") && allObjs.size()>0 && (planStatus.equals("3") || planStatus.equals("5") || planStatus.equals("8")))
    {
        p0Funcids+=",326030127";
    }
    p0Funcids+=",326030107";

    // 考核对象功能权限控制
    String p1Funcids="";
    if(!disabled.equals("disabled"))
    {
        p1Funcids+=",326030108";
        if(object_type.equals("2"))
            p1Funcids+=",326030109";
        p1Funcids+=",326030110,326030111";
        if(isDistribute.equals("0") && !object_type.equals("2"))
        {
        }
    }
    if(method.equals("1")&&!templateStatus.equals("false")&&objectList.size()>0)
    {
        p1Funcids+=",326030112";
    }
    if(method.equals("2") && templateStatus.equals("false"))
    {
        p1Funcids+=",326030113";
    }else if(method.equals("2") && templateStatus.equals("true"))
    {
        p1Funcids+=",326030113";
    }else if(objectList.size()>0)
    {
        p1Funcids+=",326030134";
    }
    p1Funcids+=",326030114,326030128";

    // 考核主体功能权限控制
    String p2Funcids="";
    if(planStatus.equals("3") || planStatus.equals("5"))
    {  //发布和暂停
        if(method.equals("2"))
        {
            p2Funcids+=",326030115";
        }
        if(gather_type!=1)
        {//网上打分 非机读
            p2Funcids+=",326030116,326030117,326030118,326030119";
        }
        if((method.equals("1")) && (busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")))
        {
            if(gather_type!=1)
            {//目标不会设置为机读 所以不考虑目标计划的机读情况
                p2Funcids+=",326030120";
            }
            p2Funcids+=",326030121";
        }else if( method.equals("2"))
        {
            p2Funcids+=",326030122,326030123";
        }
    }
    p2Funcids+=",326030124";
    if((objectList.size()>0) && (busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")))
    {
        p2Funcids+=",326030125";
    }

    String funcidstr = p0Funcids+p1Funcids+p2Funcids;
    String[] funcids = funcidstr.split(",");
    boolean dispMenu=false;
    for(int i=0;i<funcids.length;i++)
    {
        String temp = funcids[i];
        if(temp.length()>0 && usView.hasTheFunction(temp))
        {
            dispMenu=true;break;
        }
    }
%>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge; IE=9; IE=8; IE=7">
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Insert title here</title>
</head>

<hrms:themes />
<style>
    input[type="button" i]:disabled {
        color: #d5d5d5 !important;
        background-color: #fcfcfc;
        border-color: #e2e2e2;
    }
    .button{
        color:#414141 !important;
    }
    .fixedtab
    {
        width:100%;
        overflow:auto;
        BORDER-BOTTOM:#94B6E6 1pt solid;
        BORDER-LEFT: #94B6E6 1pt solid;
        BORDER-RIGHT: #94B6E6 1pt solid;
        BORDER-TOP: #94B6E6 1pt solid;
    }
    #zjTable .button{
        height:22px;
    }
</style>
<hrms:themes />
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="implement.js"></script>
<script language="JavaScript" src="../../module/utils/js/template.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>

<script language="javascript">

    var theWidth = (document.documentElement.clientWidth || document.body.clientWidth)-25;

    var menu1 = new Array();
    var menu2 = new Array();
    var menu3 = new Array();

    //定义下拉菜单的数组
    //计划号不为null
    //组装计划实施下拉菜单
    <% if(!disabled.equals("disabled")){ %>
    <% if((planStatus.equals("3") || planStatus.equals("5")) && method.equals("2")){  //目标计划发布和暂停%>
    <% if(isDistribute.equals("0")){%>
    <% if(usView.hasTheFunction("326030102")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstart'/>",
        icon:"/images/compute.gif",
        handler: startPlan2
    });
    <%}%>
    <%}else if(planStatus.equals("3") && isDistribute.equals("1")){ %>
    <% if(usView.hasTheFunction("326030102")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstart'/>",
        icon:"/images/compute.gif",
        handler: startPlan2

    });
    <%}%>
    <%}%>
    <% if(usView.hasTheFunction("326030103")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstop'/>",
        icon:"/images/bm8.bmp",
        handler: pause,
        disabled:true
    });
    <%}%>
    <%}else{ if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){%>
    <% if(usView.hasTheFunction("326030102")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstart'/>",
        icon:"/images/compute.gif",
        handler: startPlan2
    });
    <%}%>
    <%}else{%>
    <% if(usView.hasTheFunction("36030201")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstart'/>",
        icon:"/images/compute.gif",
        handler: startPlan2
    });
    <%}%>
    <% }} }else{ %>
    <% if(planStatus.equals("8") && method.equals("2")){ //目标计划分发%>
    <% if(usView.hasTheFunction("326030102")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstart'/>",
        icon:"/images/compute.gif",
        handler: startPlan2
    });
    <%}%>
    <% }else if(planStatus.equals("4") && method.equals("2")){//目标计划启动 %>
    <% if(usView.hasTheFunction("326030102")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstart'/>",
        icon:"/images/compute.gif",
        handler: startPlan2,
        disabled:true
    });
    <%}%>
    <%
            }
        }

        if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
            if(!planStatus.equals("5")&&!planStatus.equals("3")){
    %>
    <% if(usView.hasTheFunction("326030103")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstop'/>",
        icon:"/images/bm8.bmp",
        handler: pause
    });
    <%}%>
    <% } if(planStatus.equals("3") || planStatus.equals("8")){%>
    <% if(usView.hasTheFunction("326030100")){%>
    menu1.push({ text: "<bean:message key='menu.performance.scorestatus'/>",
        icon:"/images/groups.gif",
        disabled:true,
        handler:function () {
            showScoreStatus("${implementForm.encrptPlanid}","${implementForm.busitype}");
        }
    });
    <%}%>
    <% if(usView.hasTheFunction("326030101")){%>
    menu1.push({ text: "<bean:message key='menu.performance.clearscore'/>",
        icon:"/images/del.gif",
        disabled:true,
        handler:valuedelete
    });
    <%}%>
    <%}else{ %>
    <% if(usView.hasTheFunction("326030100")){%>
    menu1.push({ text: "<bean:message key='menu.performance.scorestatus'/>",
        icon:"/images/groups.gif",
        handler:function () {
            showScoreStatus("${implementForm.encrptPlanid}","${implementForm.busitype}");
        }
    });
    <%}%>
    <% if(usView.hasTheFunction("326030101")){%>
    menu1.push({ text: "<bean:message key='menu.performance.clearscore'/>",
        icon:"/images/del.gif",
        handler: valuedelete
    });
    <%}%>
    <%}%>
    <% if(usView.hasTheFunction("326030105")){%>
    menu1.push({ text: "<bean:message key='plan.data.maintenance'/>",
        handler: function () {
            planDataWH('${implementForm.planid}');
        }
    });
    <%}%>
    <%}else{
        if(!planStatus.equals("5")&&!planStatus.equals("3")){
    %>
    <% if(usView.hasTheFunction("36030202")){%>
    menu1.push({ text: "<bean:message key='menu.performance.planstop'/>",
        icon:"/images/bm8.bmp",
        handler: pause
    });
    <%}%>
    <% } if(planStatus.equals("3") || planStatus.equals("8")){%>
    <% if(usView.hasTheFunction("36030203")){%>
    menu1.push({ text: "<bean:message key='menu.performance.scorestatus'/>",
        icon:"/images/groups.gif",
        handler:function () {
            showScoreStatus("${implementForm.encrptPlanid}","${implementForm.busitype}");
        },
        disabled:true
    });
    <%}%>
    <% if(usView.hasTheFunction("36030204")){%>
    menu1.push({ text: "<bean:message key='menu.performance.clearscore'/>",
        icon:"/images/del.gif",
        handler: valuedelete,
        disabled:true
    });
    <%}%>
    <%}else{ %>
    <% if(usView.hasTheFunction("36030203")){%>
    menu1.push({ text: "<bean:message key='menu.performance.scorestatus'/>",
        icon:"/images/groups.gif",
        handler:function () {
            showScoreStatus("${implementForm.encrptPlanid}","${implementForm.busitype}");
        }
    });
    <%}%>
    <% if(usView.hasTheFunction("36030204")){%>
    menu1.push({ text: "<bean:message key='menu.performance.clearscore'/>",
        icon:"/images/del.gif",
        handler: valuedelete
    });
    <%}%>
    <%}%>
    <% if(usView.hasTheFunction("326030105")){%>
    menu1.push({ text: "<bean:message key='plan.data.maintenance'/>",
        handler: function () {
            planDataWH('${implementForm.planid}');
        }
    });
    <%}%>
    <%} if(method.equals("2") && allObjs.size()>0){ //目标计划%>
    <hrms:priv func_id="326030106">
    menu1.push({ text: "<bean:message key='jx.implement.target_card_set'/>",
        handler: target_card_set
    });
    </hrms:priv>
    <%}%>
    <% if(method.equals("2") && allObjs.size()>0 && (planStatus.equals("3") || planStatus.equals("5") || planStatus.equals("8"))){ //目标计划%>
    <logic:equal name="implementForm" property="isBachGenerateTarget"  value="1">
    <hrms:priv func_id="326030127">
    menu1.push({ text: "<bean:message key='jx.implement.batchCreateTarget'/>",
        icon:"/images/compute.gif",
        handler: function () {
            batchCreateTarget('${implementForm.planid}')
        }
    });
    </hrms:priv>
    </logic:equal>
    <%}%>

    <%
        if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){
    %>
    <% if(usView.hasTheFunction("326030107")){%>
    menu1.push({ text: "<bean:message key='report.actuarial_report.exportExcel'/>",
        icon:"/images/export.gif",
        handler: function () {
            exportObjs('${implementForm.planid}','${implementForm.code}','${implementForm.codeset}','${implementForm.isDistribute}');
        }
    });
    <%}%>
    <%}else{%>
    <% if(usView.hasTheFunction("36030206")){%>
    menu1.push({ text: "<bean:message key='report.actuarial_report.exportExcel'/>",
        icon:"/images/export.gif",
        handler: function () {
            exportObjs('${implementForm.planid}','${implementForm.code}','${implementForm.codeset}','${implementForm.isDistribute}');
        }
    });
    <%}%>
    <%}%>
    <% 	if(object_type.equals("2")){%>
    <hrms:priv func_id="326030107">
    menu1.push({ text: "<bean:message key='report.actuarial_report.dataSynchronism'/>",
        icon:"/images/add_del.gif",
        handler: function () {
            dataSynchronism('${implementForm.planid}','${implementForm.code}','${implementForm.codeset}','${implementForm.isDistribute}');
        }
    });
    </hrms:priv>
    <% }%>
    <%if((busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")) && (gather_type!=0) && (planStatus.equals("4") || planStatus.equals("6")) && (HandEval.equalsIgnoreCase("false")) && (!method.equals("2"))){%>
    <hrms:priv func_id="326030221">
    menu1.push({ text: "<bean:message key='jx.dataGather.buildExcelTable'/>",
        handler: buildExcelTable
    });
    </hrms:priv>
    <%}%>
    menu1.push({ text: "<bean:message key='jx.implement.evaluateRelationDetailRank'/>",
        handler: showDetailRankPage
    });

    //考核对象下拉菜单
    <%
        if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))
        {
            if(!disabled.equals("disabled")){ %>
    <hrms:priv func_id="326030108">
    menu2.push({ text: "<bean:message key='lable.performance.handworkselect'/>",
        handler: function () {
            handSelect('${implementForm.object_type}','${implementForm.plan_b0110}');
        }
    });
    </hrms:priv>
    <logic:equal name="implementForm" property="object_type"  value="2">
    <hrms:priv func_id="326030109">
    menu2.push({ text: "<bean:message key='menu.performance.conditionselect'/>",
        handler: conditionselect
    });
    </hrms:priv>
    </logic:equal>
    <hrms:priv func_id="326030110">
    menu2.push({ text: "<bean:message key='jx.implement.batchSetObjType'/>",
        handler: batchSetObjType
    });
    </hrms:priv>
    <%
        if(method.equals("2")){
    %>
    <hrms:priv func_id="326030136">
    menu2.push({ text: "<bean:message key='jx.implement.batchSetObjKhRelations'/>",
        handler: function () {
            batchSetObjKhRelations('${implementForm.planid}');
        }
    });
    </hrms:priv>
    <%
        }%>
    <hrms:priv func_id="326030111">
    menu2.push({ text: "<bean:message key='menu.performance.deleteobject'/>",
        icon:"/images/del.gif",
        handler:delObjects
    });
    </hrms:priv>
    <%
        if(isDistribute.equals("0") && !object_type.equals("2")){
    %>
    <%
            }}%>

    <% if(objectList.size()>0){ %>
    <hrms:priv func_id="326030134">
    menu2.push({ text: "<bean:message key='menu.performance.setmainbodygradescope'/>",
        handler:gradeScope
    });
    </hrms:priv>
    <% } %>
    <%if(method.equals("1")&&!templateStatus.equals("false")&&objectList.size()>0){ %>
    <hrms:priv func_id="326030112">
    menu2.push({ text: "<bean:message key='menu.performance.setdynapoint'/>",
        icon:"/images/compute.gif",
        handler:setdynatargetpropotion,
        disabled:!"${templateStatus}"
    });
    </hrms:priv>
    <% 	}
    }else
    {
        if(!disabled.equals("disabled")){ %>
    <hrms:priv func_id="36030207">
    menu2.push({ text: "<bean:message key='lable.performance.handworkselect'/>",
        handler:function () {
            handSelect('${implementForm.object_type}','${implementForm.plan_b0110}');
        }
    });
    </hrms:priv>
    <logic:equal name="implementForm" property="object_type"  value="2">
    <hrms:priv func_id="36030208">
    menu2.push({ text: "<bean:message key='menu.performance.conditionselect'/>",
        handler:conditionselect
    });
    </hrms:priv>
    </logic:equal>
    <hrms:priv func_id="36030209">
    menu2.push({ text: "<bean:message key='jx.implement.batchSetObjType'/>",
        handler:batchSetObjType
    });
    </hrms:priv>
    <%
        if(method.equals("2")){
    %>
    <hrms:priv func_id="36030209">
    menu2.push({ text: "<bean:message key='jx.implement.batchSetObjKhRelations'/>",
        handler:function(){batchSetObjKhRelations('${implementForm.planid}');}
    });
    </hrms:priv>
    <%
        }%>
    <hrms:priv func_id="36030210">
    menu2.push({ text: "<bean:message key='menu.performance.deleteobject'/>",
        icon:"/images/del.gif",
        handler:delObjects
    });
    </hrms:priv>
    <%
        if(isDistribute.equals("0") && !object_type.equals("2")){
    %>
    <%
            }}%>

    <% if(false){ // 隐藏
        if(objectList.size()>0){ %>
    <hrms:priv func_id="36030211">
    menu2.push({ text: "<bean:message key='menu.performance.setmainbodygradescope'/>",
        handler:gradeScope
    });
    </hrms:priv>
    <% } %>
    <%if(method.equals("1")&&!templateStatus.equals("false")&&objectList.size()>0){ %>
    <hrms:priv func_id="36030211">
    menu2.push({ text: "<bean:message key='menu.performance.setdynapoint'/>",
        icon:"/images/compute.gif",
        handler:setdynatargetpropotion,
        disabled:!"${templateStatus}"
    });
    </hrms:priv>
    <% 	}}
    } if(method.equals("2") && templateStatus.equals("false")){%>
    <hrms:priv func_id="326030113">
    menu2.push({ text: "<bean:message key='jx.implement.setdynaitemvalue'/>",
        handler:setdynaitem
    });
    </hrms:priv>
    <%	}else if(method.equals("2") && templateStatus.equals("true")){%>
    <hrms:priv func_id="326030113">
    menu2.push({ text: "<bean:message key='jx.implement.setdynaitemrank'/>",
        handler:setdynaitem
    });
    </hrms:priv>
    <%	}%>

    <%
        if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))
        { %>
    <hrms:priv func_id="326030128">
    menu2.push({ text: "<bean:message key='jx.evalution.synchronizeObjs'/>",
        handler:function () {
            synchronizeObjs('${implementForm.code}');
        },
        icon:"/images/sort.gif"
    });
    </hrms:priv>
    <hrms:priv func_id="326030114">
    menu2.push({ text: "<bean:message key='label.zp_exam.sort'/>",
        handler:function () {
            taxis('${implementForm.code}');
        },
        icon:"/images/sort.gif"
    });
    </hrms:priv>
    <%}else{%>
    <hrms:priv func_id="36030213">
    menu2.push({ text: "<bean:message key='jx.evalution.synchronizeObjs'/>",
        handler:function () {
            synchronizeObjs('${implementForm.code}');
        },
        icon:"/images/sort.gif"
    });
    </hrms:priv>
    <hrms:priv func_id="36030214">
    menu2.push({ text: "<bean:message key='label.zp_exam.sort'/>",
        handler:function () {
            taxis('${implementForm.code}');
        },
        icon:"/images/sort.gif"
    });
    </hrms:priv>
    <%}%>
    //考核主体下拉菜单
    <%
    if(planStatus.equals("3") || planStatus.equals("5")){  //发布和暂停
    %>
    <hrms:priv func_id="326030115">
    menu3.push({ text: "<bean:message key='performance.implement.importKhRela'/>",
        handler:function () {
            importKhRela('${implementForm.templateid}');
        },
        icon:"/images/import.gif"
    });
    </hrms:priv>
    <%if(gather_type!=1){//网上打分 非机读
        if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))
        {
    %>
    <hrms:priv func_id="326030116">
    menu3.push({ text: "<bean:message key='menu.performance.setpermainbody'/>",
        handler:function () {
            mainBodySel("${implementForm.templateid}");
        }
    });
    </hrms:priv>
    <hrms:priv func_id="326030117">
    menu3.push({ text: "<bean:message key='menu.performance.copypermainbody'/>",
        handler:copyKhMainBody
    });
    </hrms:priv>
    <hrms:priv func_id="326030118">
    menu3.push({ text: "<bean:message key='menu.performance.pastepermainbody'/>",
        handler:pasteKhMainBody
    });
    </hrms:priv>
    <hrms:priv func_id="326030119">
    menu3.push({ text: "<bean:message key='menu.performance.clearpermainbody'/>",
        handler:function () {
            cleanMainBody('${implementForm.templateid}');
        },
        icon:'/images/del.gif'
    });
    </hrms:priv>
    <hrms:priv func_id="326030130">
    menu3.push({ text: "<bean:message key='menu.performance.aotoMainBodySelpropotion'/>",
        handler:aotoMainBodySel
    });
    </hrms:priv>
    <%	}else{ %>
    <hrms:priv func_id="36030215">
    menu3.push({ text: "<bean:message key='menu.performance.setpermainbody'/>",
        handler:function () {
            mainBodySel("${implementForm.templateid}");
        }
    });
    </hrms:priv>
    <hrms:priv func_id="36030216">
    menu3.push({ text: "<bean:message key='menu.performance.copypermainbody'/>",
        handler:copyKhMainBody
    });
    </hrms:priv>
    <hrms:priv func_id="36030217">
    menu3.push({ text: "<bean:message key='menu.performance.pastepermainbody'/>",
        handler:pasteKhMainBody
    });
    </hrms:priv>
    <hrms:priv func_id="36030218">
    menu3.push({ text: "<bean:message key='menu.performance.clearpermainbody'/>",
        icon:"/images/del.gif",
        handler:function (){
            cleanMainBody('${implementForm.templateid}');
        }
    });
    </hrms:priv>
    <hrms:priv func_id="36030220">
    menu3.push({ text: "<bean:message key='menu.performance.aotoMainBodySelpropotion'/>",
        handler:aotoMainBodySel
    });
    </hrms:priv>
    <% }} %>
    <% if((method.equals("1")) && (busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))){%>
    <%if(gather_type!=1){//目标不会设置为机读 所以不考虑目标计划的机读情况 %>
    <hrms:priv func_id="326030120">
    menu3.push({ text: "<bean:message key='menu.performance.pointpowerset'/>",
        handler:function () {
            powerset("${implementForm.templateid}","point");
        }
    });
    </hrms:priv>
    <%} %>
    <hrms:priv func_id="326030121">
    menu3.push({ text: "<bean:message key='performance.implement.restorepointpower'/>",
        handler:function () {
            recoverPriv('${implementForm.planid}','point');
        }
    });
    </hrms:priv>
    <% }else if( method.equals("2")){ %>
    <hrms:priv func_id="326030122">
    menu3.push({ text: "<bean:message key='menu.performance.itempowerset'/>",
        handler:function () {
            powerset("${implementForm.templateid}","item");
        }
    });
    </hrms:priv>
    <hrms:priv func_id="326030123">
    menu3.push({ text: "<bean:message key='performance.implement.restoreitempower'/>",
        handler:function () {
            recoverPriv('${implementForm.planid}','item');
        }
    });
    </hrms:priv>
    <%} } %>

    <%
        if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))
        {
    %>
    <hrms:priv func_id="326030124">
    menu3.push({ text: "<bean:message key='menu.performance.setmainbodypropotion'/>",
        handler:Weight
    });
    </hrms:priv>
    <%}else{ %>
    <hrms:priv func_id="36030219">
    menu3.push({ text: "<bean:message key='menu.performance.setmainbodypropotion'/>",
        handler:Weight
    });
    </hrms:priv>
    <%} if((objectList.size()>0)&&busitype.equals("0")){ %>
    <hrms:priv func_id="326030125">
    menu3.push({ text: "<bean:message key='menu.performance.setdynamainbodypropotion'/>",
        handler:function (){
            setdynamainbodypropotion('${implementForm.planid}');
        }
    });
    </hrms:priv>
    <% }else if((objectList.size()>0)&&busitype.equals("1")){ %>
    <hrms:priv func_id="36030222">
    menu3.push({ text: "<bean:message key='menu.performance.setdynamainbodypropotion'/>",
        handler:function (){
            setdynamainbodypropotion('${implementForm.planid}');
        }
    });
    </hrms:priv>
    <%} %>
    var themes = "<%=themes %>";
    var bgColor="";
    var borderColors="";
    bgColor="#F9F9F9";
    borderColors = "#C5C5C5";

    Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mc{border:1px solid "+borderColors+";background-color:"+bgColor+" !important;}","menu_ms_bg");
    Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-ml{display:none;background:none;}","");
    Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mr{display:none;background:none;}","");
    Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small{border-color:"+borderColors+" !important;background-color:"+bgColor+" !important;}","menu1");
    Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small{background-color:"+bgColor+" !important;}","");
    Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{padding:2px 4px !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-btn-over .x-btn-default-toolbar-small-br{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-btn-over .x-btn-default-toolbar-small-bl{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-btn-over .x-btn-default-toolbar-small-tr{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-btn-over .x-btn-default-toolbar-small-tl{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-frame-tr{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-frame-bl{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-frame-tl{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-frame-br{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-frame-tc{background-image:none !important;}","");
    Ext.util.CSS.createStyleSheet(".toolbars .x-frame-bc{background-image:none !important;}","");
    Ext.onReady(function(){
        //设置查询框宽度
        document.getElementById("zjTable").style.width=(theWidth+3)+'px';
        document.getElementById("queryblank_div").style.width=(theWidth-5)+'px';
        //计划号为null
        if(Ext.isEmpty('${implementForm.planid}')){
            Ext.create("Ext.Toolbar", {
                renderTo: "toolbars",
                width: 200,
                margin:0,
                padding:0,
                border:false,
                items:[{
                    text: "<bean:message key='menu.performance.planimplement'/>",
                    name:"p0"
                },{
                    text: "<bean:message key='lable.performance.perObject'/>",
                    name:"p1"
                },{
                    text: "<bean:message key='lable.performance.perMainBody'/>",
                    name:"p2"
                }]
            });
        }else{
            //计划实施
            var menuitem1 = new Ext.menu.Menu({
                allowOtherMenus: false,
                items: menu1
            });
            //考核对象
            var menuitem2 = new Ext.menu.Menu({
                allowOtherMenus: false,
                items: menu2
            });
            //考核主体
            var menuitem3 = new Ext.menu.Menu({
                allowOtherMenus: false,
                items: menu3
            });


            //最终菜单
            var toolbar = Ext.create("Ext.Toolbar", {
                renderTo: "toolbars",
                width: 280,
                margin:0,
                padding:0,
                border:false,
                items:[{
                    text: "<bean:message key='menu.performance.planimplement'/>",
                    menu: menuitem1,
                    width:75
                },{
                    text: "<bean:message key='lable.performance.perObject'/>",
                    menu: menuitem2,
                    width:75
                },{
                    text: "<bean:message key='lable.performance.perMainBody'/>",
                    menu: menuitem3,
                    width:75
                }]
            });
            var s_disabled = true;
            var s_hidden = true;
            <%if(method.equals("2") && planStatus.equals("5")){ %>
            <%if(object_type.equals("2") || (!object_type.equals("2") && isDistribute.equals("1"))){ //人员 或者 有团队负责人的团队%>
                s_hidden = false;
            <%}else{ //无团队负责人的团队%>
                s_hidden =false;
                s_disabled = false;
            <%}
            } else if(method.equals("2") && planStatus.equals("8")){%>
                s_hidden =false;
                s_disabled = false;
            <%} else if(method.equals("2") && planStatus.equals("3")){%>
            <logic:equal name="implementForm"  property="isDistribute"  value="1">
                s_hidden =false;
                s_disabled = true;
            </logic:equal>
            <logic:equal name="implementForm"  property="isDistribute"  value="0">
                 s_disabled = false;
                 s_hidden =false;
            </logic:equal>
            <%} else{
                if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))
                {
            %>
            <hrms:priv func_id="326030102">
                s_disabled = "<%=disabled%>"=="disabled";
                s_hidden =false;
            </hrms:priv>
            <%	}else{%>
            <hrms:priv func_id="36030201">
                s_disabled = "<%=disabled%>"=="disabled";
                s_hidden =false;
            </hrms:priv>
            <%}}%>
            var startMenus = new Array();
            startMenus.push({ text: "<bean:message key='performance.implement.startScore'/>",
                handler:function (){
                    startPlan('score');
                }
            });
            <% 	if(method.equals("1")){%>
             startMenus.push({ text: "<bean:message key='performance.implement.startResult'/>",
            handler:function (){
                startPlan('result');
            }
        });
        <% 	}%>
            var paddingTop = 2;
        if (Ext.ieVersion == 0 ||Ext.ieVersion>8){
            paddingTop = 5;
        }
        Ext.create("Ext.Toolbar",{
            width: 55,
            border:false,
            renderTo:'startMenuBar',
            height:30,
            style:'position:relative;top:9px;padding-top:'+paddingTop+'px !important;',
            hidden:s_hidden,
            items:[{
                text: "<bean:message key='performance.implement.start'/>",
                height:(Ext.ieVersion == 0 ||Ext.ieVersion>8)?22:25,
                disabled:s_disabled,
                cls:'x-table-plain1',
                menu: startMenus,
                width:50
            }]
        })
            if (!s_hidden) {
                document.getElementById("zjTable").style.marginTop = '-14px';
            }
    }

    });
        var aclientWidth=(document.documentElement.clientWidth || document.body.clientWidth)-20;
        var aclientHeight=document.documentElement.clientHeight || document.body.clientHeight
        var up_percent=230/490;
        var down_percent=150/490;
        var objsize=<%=(objectList.size())%>;
    var up_height= aclientHeight-110;
    var down_height=down_percent*aclientHeight;

    var ori_class="";
    var ori_obj;
    var select_objectid="${implementForm.object_id}";
    var orgCode = '${implementForm.code}';
    var khobjtype = '<%=object_type%>';
    var khmehtod = '<%=method%>';
    var planStatus='<%=planStatus %>';
    var khObjCount = '<%=objectList.size()%>';
    var plan_gather_type='<%=gather_type%>';

    function show(opt)
    {
        if(select_objectid.length>0)
        {
            if(opt=='2')
            {
                document.getElementById("dele").style.display="none";
                document.getElementById("recover").style.display="block";
            }
            if(opt=='1')
            {
                document.getElementById("dele").style.display="block";
                document.getElementById("recover").style.display="none";
            }

            document.getElementById("desc").src="/performance/implement/performanceImplement.do?b_mainbody=query&objectid="+select_objectid+"&template_id=${implementForm.templateid}&opt="+opt;
        }
    }
    function resizeWindowRefrsh()
    {
        aclientHeight=document.documentElement.clientHeight || document.body.clientHeight;
        if(queryhidden==1)
        {
            if(aclientHeight-145>0)
            {
                document.getElementById('a_table_div').style.height=(aclientHeight-145)+"px";
            }
        }
        else
        {
            if(aclientHeight-115>0)
            {
                document.getElementById('a_table_div').style.height=(aclientHeight-110)+"px";
            }
        }
        // 考核实施页面考核对象区域高度较小时，点击[计划实施]按钮后弹出菜单就看不到了  chent add 20171018
        if(aclientHeight < 330){
            var iframe_body2 = parent.Ext.getCmp('iframe_body2');
            var body2_height = iframe_body2.getHeight();//下部分考核主体页面
            iframe_body2.setHeight(body2_height-50);//考核主体页面缩短50，如果不符合考核对象页面高度，会再次触发resize。
        }
    }

    var queryhidden=0;
    function visiblequery()
    {
        if(queryhidden==0)
        {
            var queryblank=document.getElementById("queryblank");
            if(queryblank)
                queryblank.style.display="block";
            var querydata=document.getElementById("querydata");
            if(querydata)
                querydata.style.display="block";
            queryhidden=1;
            var obj=document.getElementById("querydesc");
            obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询隐藏&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
            document.getElementById('a_table_div').style.top=103;
            document.getElementById('a_table_div').style.height=(aclientHeight-145);
        }
        else
        {
            var queryblank=document.getElementById("queryblank");
            if(queryblank)
                queryblank.style.display="none";
            var querydata=document.getElementById("querydata");
            if(querydata)
                querydata.style.display="none";
            var obj=document.getElementById("querydesc");
            obj.innerHTML="[&nbsp;<a href=\"javascript:visiblequery();\" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;";
            queryhidden=0;
            document.getElementById('a_table_div').style.top=68;
            document.getElementById('a_table_div').style.height=(aclientHeight-110);
        }
    }
    function queryR()
    {
        implementForm.action="/performance/implement/performanceImplement.do?b_query=link&operate=query";
        implementForm.submit();
    }
    function return_bt()
    {
        implementForm.action="/performance/kh_plan/performPlanList.do?b_query=return&currentPlanid=${implementForm.planid}&scrollValue=${implementForm.scrollValue}";
        implementForm.target="il_body";
        implementForm.submit();
    }

    // 生成Excel测评表和识别模板
    // 如果文件存在优先使用：c:\hrp2000\data\per_考核模板号.xlt，否则使用c:\hrp2000\data\绩效测评表模板\目录下模板
    function buildExcelTable()
    {
        var aurl="<%=url_p%>";
        var DBType="<%=dbtype%>";
        var UserName="<%=userName%>";
        var PlanId="${implementForm.planid}";
        if(!AxManager.setup(null, "ocrm", 0, 0, null, AxManager.omrreaderPkgName, null, false, aurl))
            return;
        var obj = document.getElementById('ocrm');
        obj.SetURL(aurl);    //设置URL
        obj.SetDBType(DBType);  // 设置数据库类型 0:access 1: sqlserver 2:oracle 3:db2
        obj.SetPlanType(0);    // 计划类型: 0绩效(默认值),1民主推荐
        obj.SetPlanId(PlanId); //考核计划设置计划  编号
        obj.SetUserName(UserName);

        var param = "<%=sqlString%>";  // 可以加考核对象条件,只输出满足条件的考核对象
        // 条件直接用于per_object表,空表示不限制
        //  param = "B0110='214'";
        obj.GenExcelAndMod(param);
    }

    // 重写importKhRela函数，在目标考核引入考核关系后修改选中考核对象的考核关系为“非标准” --> 刘蒙
    var importKhRela = function(templateID) {
        var objs = eval("document.implementForm.objectIDs");
        var objectIDs = "";
        if(objs) {
            if(objs.length) {
                for(var i = 0; i < objs.length; i++) {
                    if(objs[i].checked == true) {
                        objectIDs +=objs[i].value + "@";
                    }
                }
            } else {
                if(objs.checked == true) {
                    objectIDs += objs.value + "@";
                }
            }
        }

        if(objectIDs == "") {
            alert(SELECT_KHOBJ);
            return;
        }
        if(confirm(KH_IMPLEMENT_INF10)) {
            var hashvo=new ParameterSet();
            hashvo.setValue("operate", "init");
            hashvo.setValue("objectid", select_objectid);
            hashvo.setValue("template_id", templateID);
            hashvo.setValue("objectIDs", objectIDs);

            var _planid = "${implementForm.planid}";
            _planid = _planid === "null" ? "" : _planid;
            var _code = "${implementForm.code}";
            _code = _code === "null" ? "" : _code;
            hashvo.setValue("planid", _planid);
            hashvo.setValue("code", _code);
            var request=new Request({asynchronous:false,onSuccess:fn,functionId:'9023000114'},hashvo);
        }
    };
    function fn(outparamters) {
        var currPage = "${implementForm.perObjectDataListform.pagination.current }";
        var url = "/performance/implement/performanceImplement.do?b_query=query&amp;operate=init0";
        url += "&perObjectDataListform.pagination.current=" + currPage;
        window.location = url;
    }
    function sub1(o)
    {
        implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init0";
        implementForm.submit();
    }
</script>


<!-- onResize="resizeWindowRefrsh()" -->
<body  onResize="resizeWindowRefrsh()" style="overflow:hidden;" >
<html:form action="/performance/implement/performanceImplement">
    <html:hidden name="implementForm" property="str_sql"/>
    <html:hidden name="implementForm" property="orderSql"/>
    <input type="hidden" id="planid" value="${implementForm.planid}"/>
    <html:hidden name="implementForm"  property="planStatus"/>
    <html:hidden name="implementForm"  property="noApproveTargetCanScore"/>

    <div id="date_panel"  style='position:relative;z-index:5'>
        <select name="date_box" multiple="multiple" size="3"  style="width:110px"  onchange="setSelectValue('<%=method %>','<%=planStatus %>');" >
            <option value="none" selected="selected"></option>
            <option value="score"><bean:message key="performance.implement.startScore"/></option>
            <option value="result"><bean:message key="performance.implement.startResult"/></option>
        </select>
    </div>

    <table width='100%'>
        <tr><td>
            <div id="toolbars" class="toolbars"/>
        </td></tr>
    </table>
    <table width='100%' id="zjTable"><tr width='100%'><td align='left'>
        <%
            if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1"))
            {
        %>
        <hrms:priv func_id="326030108"><input id="clo" type="button" extra="button" onclick="handSelect('${implementForm.object_type}','${implementForm.plan_b0110}')"  <%=disabled%>  allowPushDown="false" down="false" value="<bean:message key="jx.eval.selectobj"/>"></hrms:priv>
        <%
            if(planStatus.equals("3") || planStatus.equals("5")){  //发布和暂停
                if(gather_type!=1){//网上打分 非机读 %>
        <hrms:priv func_id="326030130"><input type="button" class="button" onclick="aotoMainBodySel()"    allowPushDown="false" down="false" value='<bean:message key="menu.performance.aotoMainBodySelpropotion"/>'/>&nbsp;</hrms:priv>
        <%}}%>
        <hrms:priv func_id="326030111"><input type="button" class="button" onclick="delObjects()" <%=disabled%>  allowPushDown="false" down="false" value='<bean:message key="kh.field.delete"/>'/>&nbsp;</hrms:priv>
        <%}else{%>
        <hrms:priv func_id="36030207"><input type="button" class="button" onclick="handSelect('${implementForm.object_type}','${implementForm.plan_b0110}')"  <%=disabled%>  allowPushDown="false" down="false" value='<bean:message key="jx.eval.selectobj"/>'/>&nbsp;</hrms:priv>
        <%
            if(planStatus.equals("3") || planStatus.equals("5")){  //发布和暂停
                if(gather_type!=1){//网上打分 非机读 %>
        <hrms:priv func_id="36030220"><input type="button" class="button" onclick="aotoMainBodySel()" allowPushDown="false" down="false" value='<bean:message key="menu.performance.aotoMainBodySelpropotion"/>'/>&nbsp;</hrms:priv>
        <%}}%>
        <hrms:priv func_id="36030210"><input type="button" class="button" onclick="delObjects()" <%=disabled%>  allowPushDown="false" down="false" value='<bean:message key="kh.field.delete"/>'/>&nbsp;</hrms:priv>

        <%}%>
        <logic:equal name="implementForm"  property="method"  value="2">
            <logic:equal name="implementForm"  property="planStatus"  value="5">
                <logic:equal name="implementForm"  property="isDistribute"  value="1">
                    <hrms:priv func_id="326030104"><input type="button" class="button" onclick="distributePlan()"  allowPushDown="false" down="false" value='<bean:message key="performance.plan.distribute"/>'/>&nbsp;</hrms:priv>
                </logic:equal>
            </logic:equal>
        </logic:equal>
        <%if(method.equals("2") && planStatus.equals("3")){%>
        <logic:equal name="implementForm"  property="isDistribute"  value="1">
            <hrms:priv func_id="326030104"><input type="button" class="button" onclick="distributePlan()"  allowPushDown="false" down="false" value='<bean:message key="performance.plan.distribute"/>'>&nbsp;</hrms:priv>
        </logic:equal>
        <%}%>
        <span id="startMenuBar" class="toolbars" style="display: inline-block;margin-left:-2px;"></span>
        <%if(busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")){%>
        <hrms:priv func_id="326030103"><input type="button"  class="button" onclick="pause()" <%=inverse%>  allowPushDown="false" down="false" value='<bean:message key="hire.jp.pos.pausee"/>'/>&nbsp;</hrms:priv>
        <%	}else{%>
        <hrms:priv func_id="36030202"><input type="button"  class="button"  onclick="pause()" <%=inverse%>  allowPushDown="false" down="false" value='<bean:message key="hire.jp.pos.pausee"/>'/>&nbsp;</hrms:priv>
        <%	}%>

        <%if((busitype==null || busitype.trim().length()<=0 || !busitype.equals("1")) && ((gather_type!=0) || (gather_type==0 && scoreWay.equals("0"))) && (planStatus.equals("4") || planStatus.equals("6")) && (HandEval.equalsIgnoreCase("false")) && (!method.equals("2"))){%>
        <hrms:priv func_id="326030126"><input id="cl4" type="button" class='button' onclick="dataGather()" allowPushDown="false" down="false" value='<bean:message key="jx.khplan.param1.title61"/>'/>&nbsp;</hrms:priv>
        <%}else if("1".equals(busitype)&& ((gather_type!=0) || (gather_type==0 && scoreWay.equals("0"))) && (planStatus.equals("4") || planStatus.equals("6")) && (HandEval.equalsIgnoreCase("false")) && (!method.equals("2"))){%>
        <hrms:priv func_id="36030221"><input id="cl4" type="button" class='button' onclick="dataGather()" allowPushDown="false" down="false" value='<bean:message key="jx.khplan.param1.title61"/>'/>&nbsp;</hrms:priv>
        <%} %>
        <input id="cl_return" type="button" class='button' onclick="return_bt()" allowPushDown="false" down="false" value='<bean:message key="button.return"/>'/>
        &nbsp;&nbsp;&nbsp;&nbsp;

        &nbsp;&nbsp;
        <span id="querydesc" nowrap>
[&nbsp;<a href="javascript:visiblequery();" >查询显示&nbsp;</a>]&nbsp;&nbsp;&nbsp;
</span>
    </td>
    </tr>
        <tr style="display:none;height:30px;line-height:25px;" id='queryblank' >
            <td>
                <div id="queryblank_div" style="background-color:#F5F8F8;height:29px;border:#C4D8EE 1pt solid;">
                <script language='javascript' >
			        if(!getBrowseVersion()) {//非ie
			        	document.write("<div  style='padding-top:3px;'>");
			        }
			    </script>
	                <bean:message key="lable.appraisemutual.examineobject"/>:
	                <html:text name="implementForm" property="queryA0100" style="height: 22px;margin-top:1px;"/>
	                &nbsp;&nbsp;<input type="button" name="query" value="查询"  class="button"  onclick="queryR();"/>&nbsp;&nbsp;&nbsp;
                <script language='javascript' >
			        if(!getBrowseVersion()) {//非ie
			        	document.write("</div>");
			        }
			    </script>
                </div>
            </td>
        </tr>
    </table>


    <script language='javascript' >
        var theHeight=document.documentElement.clientHeight-110;
        <%if(dispMenu==false){%>
        theHeight=theHeight+25;
        <%}%>
        document.write("<div  id=\"a_table_div\" style='width:"+theWidth+"px;margin:2px 0px 0px 3px;'>");
    </script>


    <table id='a_table' width="100%" cellspacing="0"   align="left" cellpadding="0" class="ListTable">
        <thead>
        <tr>
            <td align="center" width='40'  class="TableRow common_background_color common_border_color">
                <input type="checkbox" name="selbox" onclick="batch_select(this,'objectIDs');" title='<bean:message key="label.query.selectall"/>'>
            </td>
            <logic:equal name="implementForm" property="object_type"  value="2">
                <td align="center"  class="TableRow" nowrap >
                    <bean:message key="b0110.label"/>
                </td>
                <td align="center"   class="TableRow" nowrap >
                    <%
                        FieldItem fielditem = DataDictionary.getFieldItem("E0122");
                    %>
                    <%=fielditem.getItemdesc()%>
                </td>
                <td align="center"   class="TableRow" nowrap >
                    <bean:message key="e01a1.label"/>
                </td>
                <td align="center"   class="TableRow" nowrap >
                    <bean:message key="hire.employActualize.name"/>
                </td>
                <td align="center" class="TableRow" nowrap >
                    <bean:message key="performance.implement.objecttype"/>
                </td>
            </logic:equal>
            <logic:notEqual name="implementForm" property="object_type"  value="2">
                <td align="center" class="TableRow" nowrap >
                    <bean:message key="b0110.label"/>
                </td>
                <td align="center" class="TableRow" nowrap >
                    <bean:message key="tree.unroot.undesc"/>/<bean:message key="tree.umroot.umdesc"/>
                </td>
                <td align="center"  class="TableRow  common_background_color common_border_color" nowrap >
                    <bean:message key="performance.implement.objecttype"/>
                </td>
            </logic:notEqual>
            <% 	if(method.equals("2") && (object_type.equals("2") || (!object_type.equals("2") && isDistribute.equals("1")))){%>
            <td align="center" class="TableRow common_background_color common_border_color" nowrap >
                <bean:message key="performance.relation"/>
            </td>
            <%  } %>
            <%if(orderSql.length()==0) {%>
            <td align="center"  class="TableRow common_background_color common_border_color" nowrap >
                <bean:message key="label.order"/>
            </td>
            <%  } %>
        </tr>
        </thead>
        <%  int i=0; %>

        <hrms:extenditerate id="element" name="implementForm" property="perObjectDataListform.list" indexes="indexes"  pagination="perObjectDataListform.pagination" pageCount="${implementForm.pagerows}" scope="session">

            <% i++;
                if(i%2==1){ %>
            <tr class='trShallow'  id="<bean:write name="element" property="object_id" filter="true"/>">
                        <% } else { %>
            <tr class='trDeep'  id="<bean:write name="element" property="object_id" filter="true"/>">
                <% } %>
                <logic:equal name="implementForm" property="object_type"  value="2">
                    <td align="center"  class="RecordRow common_border_color" nowrap>
                        <input type='checkbox' name='objectIDs' value='<bean:write name="element" property="object_id" filter="true"/>'  />
                    </td>
                    <td align="left"  class="RecordRow" onclick='selectRow("${implementForm.planid}","<bean:write name="element" property="object_id" filter="true"/>",this,"${implementForm.templateid}")'   nowrap >
                        &nbsp;<bean:write name="element" property="b0110" filter="true"/>
                    </td>
                    <td align="left"  class="RecordRow" onclick='selectRow("${implementForm.planid}","<bean:write name="element" property="object_id" filter="true"/>",this,"${implementForm.templateid}")'  nowrap>
                        &nbsp;<bean:write name="element" property="e0122" filter="true"/>
                    </td>
                    <td align="left"  class="RecordRow" onclick='selectRow("${implementForm.planid}","<bean:write name="element" property="object_id" filter="true"/>",this,"${implementForm.templateid}")'  nowrap>
                        &nbsp;<bean:write name="element" property="e01a1" filter="true"/>
                    </td>
                    <td align="left"  class="RecordRow" onclick='selectRow("${implementForm.planid}","<bean:write name="element" property="object_id" filter="true"/>",this,"${implementForm.templateid}")'  nowrap >
                        &nbsp;<bean:write name="element" property="a0101" filter="true"/>
                    </td>

                    <%
                        LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
                        String object_id=(String)abean.get("object_id");
                        pageContext.setAttribute("object_id",object_id);
                        if(planStatus.equals("3")||planStatus.equals("5"))
                        {
                    %>
                    <td align="center"  class="RecordRow" nowrap>
                        <html:select name="element" property="body_id" size="1"  onchange="setObjType('${object_id}','${implementForm.planid}',this)"  >
                            <html:optionsCollection property="objectTypeList" value="dataValue" label="dataName"/>
                        </html:select>
                    </td>
                    <logic:equal name="implementForm" property="method"  value="2">
                        <td align="center"  class="RecordRow" nowrap>
                            <html:select name="element" property="kh_relations" size="1" onchange="setKhRelation('${object_id}','${implementForm.planid}',this)"  >
                                <html:option value="0"><bean:message key="khrelation.option1"/></html:option>
                                <html:option value="1"><bean:message key="khrelation.option2"/></html:option>
                            </html:select>
                        </td>
                    </logic:equal>
                    <% }else{ %>
                    <td align="center"  class="RecordRow" nowrap>
                        <html:select name="element" property="body_id" size="1"  disabled="true" >
                            <html:optionsCollection property="objectTypeList" value="dataValue" label="dataName"/>
                        </html:select>
                    </td>
                    <logic:equal name="implementForm" property="method"  value="2">
                        <td align="center"  class="RecordRow" nowrap>
                            <html:select name="element" property="kh_relations" size="1" disabled="true" >
                                <html:option value="0"><bean:message key="khrelation.option1"/></html:option>
                                <html:option value="1"><bean:message key="khrelation.option2"/></html:option>
                            </html:select>
                        </td>
                    </logic:equal>
                    <% } %>


                </logic:equal>
                <logic:notEqual name="implementForm" property="object_type"  value="2">
                    <td align="center"  class="RecordRow" nowrap>
                        <input type='checkbox' name='objectIDs'  value='<bean:write name="element" property="object_id" filter="true"/>'   />
                    </td>
                    <td align="left"  class="RecordRow" onclick='selectRow("${implementForm.planid}","<bean:write name="element" property="object_id" filter="true"/>",this,"${implementForm.templateid}")'  nowrap >
                        &nbsp;<bean:write name="element" property="b0110" filter="true"/>
                    </td>
                    <td align="left"  class="RecordRow" onclick='selectRow("${implementForm.planid}","<bean:write name="element" property="object_id" filter="true"/>",this,"${implementForm.templateid}")'  nowrap>
                        &nbsp; <bean:write name="element" property="a0101" filter="true"/>
                    </td>

                    <%
                        LazyDynaBean aabean=(LazyDynaBean)pageContext.getAttribute("element");
                        String aobject_id=(String)aabean.get("object_id");
                        pageContext.setAttribute("object_id",aobject_id);

                        if(planStatus.equals("3")||planStatus.equals("5")){
                    %>
                    <td align="center"  class="RecordRow" nowrap>
                        <html:select name="element" property="body_id" size="1" onchange="setObjType('${object_id}','${implementForm.planid}',this)"  >
                            <html:optionsCollection property="objectTypeList" value="dataValue" label="dataName"/>
                        </html:select>
                    </td>
                    <% 	if(method.equals("2") && isDistribute.equals("1")){%>
                    <td align="center"  class="RecordRow" nowrap>
                        <html:select name="element" property="kh_relations" size="1" onchange="setKhRelation('${object_id}','${implementForm.planid}',this)"  >
                            <html:option value="0"><bean:message key="khrelation.option1"/></html:option>
                            <html:option value="1"><bean:message key="khrelation.option2"/></html:option>
                        </html:select>
                    </td>

                    <%} } else { %>
                    <td align="center"  class="RecordRow" nowrap>
                        <html:select name="element" property="body_id" size="1"  disabled="true"  >
                            <html:optionsCollection property="objectTypeList" value="dataValue" label="dataName"/>
                        </html:select>
                    </td>
                    <% 	if(method.equals("2") && (object_type.equals("2") || (!object_type.equals("2") && isDistribute.equals("1")))){%>
                    <td align="center"  class="RecordRow" nowrap>
                        <html:select name="element" property="kh_relations" size="1" disabled="true" >
                            <html:option value="0"><bean:message key="khrelation.option1"/></html:option>
                            <html:option value="1"><bean:message key="khrelation.option2"/></html:option>
                        </html:select>
                    </td>

                    <%} } %>
                    </td>
                </logic:notEqual>
                <%if(orderSql.length()==0) {%>
                <td align="center"  class="RecordRow common_border_color" nowrap width='80'>
                    <logic:notEqual name="element" property="count" value="1">
                        &nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="object_id" filter="true"/>','up','${implementForm.code}','${implementForm.codeset}')">
                        <img src="../../images/up01.gif" width="12" height="17" border=0></a>
                    </logic:notEqual>
                    <logic:equal name="element" property="count" value="1">
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    </logic:equal>
                    <%
                        LazyDynaBean a_bean=(LazyDynaBean)pageContext.getAttribute("element");
                        String count = null==(String)a_bean.get("count")?"0":(String)a_bean.get("count");
                        if(Integer.parseInt(count)==dataSize){
                    %>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                    <% }else{%>
                    &nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="object_id" filter="true"/>','down','${implementForm.code}','${implementForm.codeset}')">
                    <img src="../../images/down01.gif" width="12" height="17" border=0></a>
                    <% }%>
                </td>
                <%} %>
            </tr>
        </hrms:extenditerate>
    </table>
    <table  height="43" class='RecordRowP'  align='center' width="100%" >
        <tr>
            <td valign="bottom" class="tdFontcolor">
                <bean:message key="label.page.serial"/>
                <bean:write name="implementForm" property="perObjectDataListform.pagination.current" filter="true" />
                <bean:message key="label.page.sum"/>
                <bean:write name="implementForm" property="perObjectDataListform.pagination.count" filter="true" />
                <bean:message key="label.page.row"/>
                <bean:write name="implementForm" property="perObjectDataListform.pagination.pages" filter="true" />
                <bean:message key="label.page.page"/>&nbsp;&nbsp;
                每页显示<html:text property="pagerows" name="implementForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:sub1(0);">刷新</a>
            </td>
            <td align="right" nowrap class="tdFontcolor">
                <p align="right">
                    <hrms:paginationlink name="implementForm" property="perObjectDataListform.pagination" nameId="perObjectDataListform">
                    </hrms:paginationlink>
            </td>
        </tr>
    </table>
    </div>
</html:form>
<script language='javascript'>
    /**
     * 兼容fireEvent方法
     * @param el
     */
    function myfireEvent(el){
        if (document.createEvent) { // DOM Level 2 standard
            var evt;
            evt = document.createEvent("MouseEvents");
            evt.initEvent("click", true, true);
            el.dispatchEvent(evt);
        } else if (el.fireEvent) { // IE
            el.fireEvent('onclick');
        }
    }
    Element.hide('date_panel');
    <%
    if(gather_type!=1){//计划采集类型：网上打分 非机读 %>
    var table = document.getElementById('a_table');
    if(table.rows.length>1)
    {
        myfireEvent(table.rows[1].cells[1]);
    }
    else
        parent.ril_body2.location="/performance/implement/performanceImplement.do?br_mainbody=query&objectid=aaa&template_id=${implementForm.templateid}";
    <%}else{%>
    if(parent.viewport!=null){
//		parent.viewport.items.get(1).toggleCollapsible(false);
    }else
        parent.ril_body.rows="*,0";
    resizeWindowRefrsh();
    <%}
    if(gather_type==1&&request.getParameter("b_startPause")!=null){
    %>
    if(parent.viewport!=null){
//		parent.viewport.items.get(1).toggleCollapse(false);
    }else
        parent.ril_body.rows="*,0";
    resizeWindowRefrsh();

    <%}%>
    function setSecondPage(page)
    {
        sub_page=page;
    }
    function sub(o)
    {
        implementForm.action="/performance/implement/performanceImplement.do?b_query=query&operate=init0";
        implementForm.submit();
    }
    var aa=document.getElementsByTagName("input");
    for(var i=0;i<aa.length;i++){
        if(aa[i].type=="text"){
            aa[i].className="inputtext";
        }
    }
</script>
<!-- xus 19/12/26 【56636】V77绩效管理：考核实施中“启动”按钮缺线-->
<style type="text/css">
.x-table-plain, .x-table-plain1{
	margin-left: 2px;
}
</style>

</body>
</html>