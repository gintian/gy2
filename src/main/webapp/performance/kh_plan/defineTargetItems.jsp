<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>  
<%@ page import="com.hjsj.hrms.actionform.performance.kh_plan.ExamPlanForm" %>
<script language="JavaScript" src="/performance/kh_plan/defineTargetItems.js"></script>
<script language="javascript" src="/module/utils/js/template.js"/>
<% int count=0; %>
<script type="text/javascript"></script>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<!--  onsubmit="return false;"组织表单自动提交动作，form表单只有一个输入框时，当我们点击回车的时候会自动提交数据，并刷新页面。踏马恶心的设定 -->
<html:form action="/performance/kh_plan/kh_params" onsubmit="return false;">
	 
	<html:hidden name="examPlanForm" property="targetItem" styleId="targetItem"/>
	<html:hidden name="examPlanForm" property="targetCalcItem" styleId="targetCalcItem"/>	
	<html:hidden name="examPlanForm" property="targetTraceItem" styleId="targetTraceItem"/>	
	<html:hidden name="examPlanForm" property="targetCollectItem" styleId="targetCollectItem"/>	
	<html:hidden name="examPlanForm" property="targetDefineItem" styleId="targetDefineItem"/>	
	<html:hidden name="examPlanForm" property="targetMustFillItem" styleId="targetMustFillItem"/>	
	<html:hidden name="examPlanForm" property="targetUsePrevious" styleId="targetUsePrevious"/>
	<table border="0" cellspacing="0" cellpadding="0" align="center" height="500" width="100%">
		<tr>
			<td with="100%">
				<div id="mbk_param"  width="500" height="200"></div>
			</td>
		</tr>
		<tr>
			<td height="6">
			</td>
		</tr>
		<tr>
			<td>
				<div id="mbk_param2"  width="500" height="235"></div>
			</td>
		</tr>
	</table>
	<table border="0" cellspacing="1" cellpadding="2"  width="100%">
		<tr>
			<td height="6" align="center" style="height:35px">   
					<input type='button' class="mybutton" property="b_ok"
					onclick='defineTargetItem();' name="ok"
					value='<bean:message key="button.ok"/>' id="ok" />&nbsp;
					<input type='button' class="mybutton" property="b_cancel"
					onclick='closewindow();' id="cancel" name='cancel'
					value='<bean:message key="button.cancel"/>' />
			</td>
		</tr>
	</table>
</html:form>
<script>

    Ext.onReady(function(){
        var param0='';
        param0 += '<div style=\'height:170px;width:100%; overflow: auto;\'>' + 
        	'<table width="90%" height="100%" border="0" cellspacing="0" align="center"' +
            'cellpadding="0" class="ListTable" id=\'targetDefineTable\'>' +
            '<tr><td align="left" colspan="2"><span style=\'position:relative;bottom:3px;\'>任务内容名称&nbsp;&nbsp;</span>' +
            '<input type="text" name="taskNameDesc" value="${examPlanForm.taskNameDesc}" style="margin-bottom: 3px;" class="inputtext"/> </td></tr>' +
            '<logic:iterate id="element" name="examPlanForm" property="targetDefineItemList">' +
            '<tr>' +
            '<td align="center" class="RecordRow" width="15%">' +
            '<input name="targetDefineItems" type="checkbox" onclick="changeTargetDefineItems(this)"' +
            'value="<bean:write name="element" property="itemid" filter="true" />"' +
            '<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '<td align="left" class="RecordRow" nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:write name="element" property="itemdesc" filter="true" />' +
            '</td>' +
            '</tr>' +
            '</logic:iterate>' +
            '</table>' + 
            '</div>';

        Ext.create('Ext.TabPanel', {
            renderTo: 'mbk_param',
            width: 550,
            height:200,
            activeTab: 0,//激活的页数
            frame: false, //出现渲染的边框
            items: [
                {
                    title:'目标卡指标',
                    html:param0
                }
            ]

        });
        var param1='',param2='',param3='',param4='',param5='';
        param1+= '<div style=\'height:200px;width:100%; overflow: auto;\'>' +
            '<table width="90%" border="0" cellspacing="0" align="center"' +
            'cellpadding="0" class="ListTable" id=\'targetTraceTable\'>' +
            '<logic:iterate id="element" name="examPlanForm" property="targetTraceItemList">' +
            '<tr>' +
            '<td align="center" class="RecordRow" width="15%">' +
            '<input name="targetTraceItems" type="checkbox" onclick="setEnable()" ' +
            'value="<bean:write name="element" property="itemid" filter="true" />"' +
            '<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '<td align="left" class="RecordRow" nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:write name="element" property="itemdesc" filter="true" />' +
            '</td>' +
            '</tr>' +
            '</logic:iterate>' +
            '</table>' +
            '</div>' +
            '<table width="90%" border="0" cellspacing="0" align="center"' +
            'cellpadding="0">' +
            '<tr>' +
            '<td align="center" width="15%">' +
            '<html:checkbox styleId="allowLeaderTrace" name="examPlanForm" property="allowLeaderTrace" value="1" />' +
            '</td>' +
            '<td align="left"  nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:message key="plan.param.allowLeaderTrace" />' +
            '</td>' +
            '</tr>' +
            '</table>' ;
        param2+= '<div style=\'height:200px;width:100%; overflow: auto;\'>' +
			'<table width="90%" border="0" cellspacing="0" align="center"' +
            'cellpadding="0" class="ListTable" id=\'targetCollectTable\'>' +
            '<tr>' +
            '  <td align="center" class="TableRow" nowrap>' +
            '<input type="checkbox" id="selboxid" name="selbox" onclick="allSelect(this);" title=\'<bean:message key="label.query.selectall"/>\'>&nbsp;        ' +
            '</td> ' +
            '<td align="center" class="TableRow" nowrap>&nbsp;&nbsp;' +
            '<bean:message key="plan.param.targetName" />&nbsp;' +
            '</td> ' +
            '<td align="center" class="TableRow" nowrap>' +
            '<bean:message key="plan.param.isMust" />&nbsp;' +
            ' </td> ' +
            '</tr>' +
            '<logic:iterate id="element" name="examPlanForm" property="targetCollectItemList">' +
            '<logic:notEqual value="ATTACHMENT" name="element" property="itemid">' +
            '<tr>' +
            '<td align="center" class="RecordRow" nowrap width="15%">' +
            '<input id="<bean:write name="element" property="itemdesc" filter="true" />" name="targetCollectItems" type="checkbox" onclick="setEnable()" ' +
            'value="<bean:write name="element" property="itemid" filter="true" />"' +
            '<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '<td align="left" class="RecordRow" nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:write name="element" property="itemdesc" filter="true" />' +
            '</td>' +
            '<td align="center" class="RecordRow" nowrap>' +
            ' <input id="<bean:write name="element" property="itemdesc" filter="true" />Flag" name="targetCollectItemsFlag" disabled type="checkbox" value="1"' +
            ' <logic:notEqual name="element" property="selectedmust" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '</tr>' +
            '</logic:notEqual>' +
            '</logic:iterate>' +
            '</table></div>';


        param3+='<div style=\'height:200px;width:100%; overflow: auto;\'>' +
			' <table heigth=\'100%\'  width="100%" bgcolor=\'white\' >' +
            ' <tr><td width=\'80%\' valign=\'top\' >' +
            '<table width="90%" border="0" cellspacing="0" align="center" ' +
            'cellpadding="0" class="ListTable"  name="targetCalcTable" id="targetCalcTable" >' +
            ' <logic:iterate id="element" name="examPlanForm" property="targetCalcItemList">' +
            '<tr>' +
            '<td  onclick="setObj(this);clickMouse();"  align="center" class="RecordRow" nowrap width="15%">' +
            '<input id="targetCalcItems" name="targetCalcItems" type="checkbox"' +
            'value="<bean:write name="element" property="itemid" filter="true" />"' +
            '<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '<td  onclick="setObj(this);clickMouse();"  align="left" class="RecordRow" nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:write name="element" property="itemdesc" filter="true" />' +
            '</td>' +
            '</tr>' +
            '</logic:iterate>' +
            '</table>' +
            ' </td>' +
            ' <td width=\'20%\'>' +
            ' ' +
            ' <div style=\'margin-top:45px;\'>' +
            '<table>' +
            '<tr>' +
            '<td>' +
            '<a href="javaScript:SetRow(\'up\')"><img src="../../images/up01.gif" width="12" height="17" border=0></a> ' +
            '</td>' +
            '</tr>' +
            '<tr>' +
            '</tr>' +
            '<tr>' +
            '</tr>' +
            '<tr>' +
            '<td>' +
            '<a href="javaScript:SetRow(\'down\')"><img src="../../images/down01.gif" width="12" height="17" border=0></a> ' +
            '</td>' +
            '</tr>' +
            '</table>' +
            ' </div>' +
            ' </td></tr>' +
            ' </table></div>';

        param4+='<div style=\'height:200px;width:100%; overflow: auto;\'>' +
			'<table width="90%" border="0" cellspacing="0" align="center"' +
            'cellpadding="0" class="ListTable" id=\'targetMustFillTable\'>' +
            '<logic:iterate id="element" name="examPlanForm" property="targetMustFillItemList">' +
            '<tr>' +
            '<td align="center" class="RecordRow" nowrap width="15%">' +
            '<input name="targetMustFillItems" type="checkbox" onclick="setEnable()" ' +
            'value="<bean:write name="element" property="itemid" filter="true" />"' +
            '<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '<td align="left" class="RecordRow" nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:write name="element" property="itemdesc" filter="true" />' +
            '</td>' +
            '</tr>' +
            '</logic:iterate>' +
            '</table></div>';

        param5+='<div style=\'height:200px;width:100%; overflow: auto;\'>' +
			'<table width="90%" border="0" cellspacing="0" align="center"' +
            'cellpadding="0" class="ListTable" id=\'targetUsePreviousTable\'>' +
            '<logic:iterate id="element" name="examPlanForm" property="targetUsePreviousList">' +
            '<tr>' +
            '<td align="center" class="RecordRow" nowrap width="15%">' +
            '<input name="targetUsePreviousb" type="checkbox" onclick="setEnable()" ' +
            'value="<bean:write name="element" property="itemid" filter="true" />"' +
            '<logic:notEqual name="element" property="selected" value="0">checked</logic:notEqual> />' +
            '</td>' +
            '<td align="left" class="RecordRow" nowrap>' +
            '&nbsp;&nbsp;' +
            '<bean:write name="element" property="itemdesc" filter="true" />' +
            '</td>' +
            '</tr>' +
            '</logic:iterate>' +
            '</table></div>';

        Ext.create('Ext.TabPanel', {
            renderTo: 'mbk_param2',
            width: 550,
            height:250,
            activeTab: 0,//激活的页数
            deferredRender:false,//关闭延时加载
            frame: false, //出现渲染的边框
            items: [
                {
                    title:"目标卡跟踪显示指标",
                    html:param1
                },
                {
                    title:"目标卡采集指标",
                    html:param2
                },
                <%
					ExamPlanForm myForm=(ExamPlanForm)session.getAttribute("examPlanForm");
					String calItemStr = myForm.getCalItemStr();
					if(calItemStr!=null && calItemStr.length()>0){%>
                {
                    title: "目标卡计算指标",
                    html: param3
                },
                <%} %>
                {
                    title:"目标卡必填指标",
                    html:param4
                },
                {
                    title:"引入上期目标卡指标",
                    html:param5
                }
            ]

        });



        if("${param.oper}"=='close')
        {
            var thevo=new Object();
            thevo.flag="true";
            thevo.targetItem=examPlanForm.targetItem.value;
            thevo.targetCalcItem=examPlanForm.targetCalcItem.value;
            thevo.targetTraceItem=examPlanForm.targetTraceItem.value;
            thevo.targetCollectItem=examPlanForm.targetCollectItem.value;
            thevo.allowLeaderTrace=examPlanForm.allowLeaderTrace.checked;
            thevo.targetDefineItem=examPlanForm.targetDefineItem.value;
            thevo.targetMustFillItem=examPlanForm.targetMustFillItem.value;
            thevo.targetUsePrevious=examPlanForm.targetUsePrevious.value;
            thevo.taskNameDesc=getEncodeStr(examPlanForm.taskNameDesc.value);
            parent.window.returnValue=thevo;
            if(window.showModalDialog) {
                parent.window.close();
            }else{
                window.top.opener.defineIndex_window_ok(thevo);
                window.open("about:blank","_top").close();
            }
        }else if("${param.oper}"=='init')
        {
            var info= parent.window.dialogArguments || window.top.opener.dialogArguments;
            setEnable();
            examPlanForm.allowLeaderTrace.checked=info[0];
            if(info[1]!='0' && info[1]!='5')
                document.getElementById('ok').disabled=true;
        }


    });

	function insertrow(newRow,CopyRow,thename,checkObj)
    {
        var value2 = "1";
        var tabstr = "";
        myNewCell=newRow.insertCell(0);
        myNewCell.className = "RecordRow";
        myNewCell.align="center";   
        if(thename != "targetCollectItems")
              tabstr="<input type=\"checkbox\" name=\""+thename+"\"  onclick=\"setEnable()\" value=\""+checkObj.value+"\"/>";   
        else
            tabstr="<input type=\"checkbox\" id=\""+CopyRow.cells[1].innerHTML+"\" name=\""+thename+"\"  onclick=\"setEnable()\" value=\""+checkObj.value+"\"/>";
        myNewCell.innerHTML = tabstr;
        
        myNewCell=newRow.insertCell(1);
        myNewCell.className = "RecordRow";
        myNewCell.innerHTML = CopyRow.cells[1].innerHTML;
        myNewCell.align="left";
        
        var checktempid = CopyRow.cells[1].innerHTML+"Flag";
        var checktempname = thename+"Flag";
        if(thename == "targetCollectItems"){
            myNewCell=newRow.insertCell(2);
            myNewCell.className = "RecordRow";
            myNewCell.align="center";        
            tabstr="<input type=\"checkbox\" id=\""+checktempid+"\" name=\""+checktempname+"\"   value=\""+value2+"\"/>";     
            myNewCell.innerHTML = tabstr;
        }
        globalFuc();
    }
	globalFuc();





    function globalFuc()
    {
        var objglobal = document.getElementsByName("targetCollectItems");
        var flag = "true";
        if(objglobal.length==0)
            flag = "false";
        for(var i=0;i<objglobal.length;i++)
        {
            var checkid = objglobal[i].id;
            if(objglobal[i].checked==false) {
                flag = "false";
                document.getElementById(checkid+"Flag").disabled=true;
                document.getElementById(checkid+"Flag").checked=false;
            } else{
                document.getElementById(checkid+"Flag").disabled=false;
            }
        }
        if(flag=="true"){
            if(document.getElementById("selboxid") != null)
                document.getElementById("selboxid").checked=true;
        }
        else
        {
            if(document.getElementById("selboxid") != null)
                document.getElementById("selboxid").checked=false;
        }
    }
    var calItemStr='${examPlanForm.calItemStr}'

    function getTargetTraceItems(elementName)
    {
        var items = document.getElementsByName(elementName);
        var name2 = elementName+"Flag";
        var items2 = document.getElementsByName(name2);
        var itemStr='';
        for(var i=0;i<items.length;i++)
        {
            if(elementName != "targetCollectItems"){
                if(items[i].checked==true)
                    itemStr+=items[i].value+',';
            } else{
                if(items[i].checked==true){
                    if(items2[i].checked==true)
                        itemStr+=items[i].value+':'+"1"+',';
                    else
                        itemStr+=items[i].value+':'+"0"+',';
                }
            }
        }
        return itemStr;
    }
    function setEnable()
    {
        globalFuc();
        var targetTraceItems1 = document.getElementsByName("targetTraceItems");
        var targetCollectItems1 = document.getElementsByName("targetCollectItems");

        for(var i=0;i<targetTraceItems1.length;i++)
        {
            if(targetTraceItems1[i].checked==true)
            {
                if(targetTraceItems1[i].value.toUpperCase()=='ATTACHMENT')
                {
                    document.getElementById('allowLeaderTrace').disabled=false;
                    return;
                }
                for(var j=0;j<targetCollectItems1.length;j++)
                {
                    if(targetCollectItems1[j].value==targetTraceItems1[i].value)
                    {
                        if(targetCollectItems1[j].checked==false)
                        {
                            document.getElementById('allowLeaderTrace').disabled=false;
                            return;
                        }
                    }
                }
            }
        }

        document.getElementById('allowLeaderTrace').checked=false;
        document.getElementById('allowLeaderTrace').disabled=true;
    }
    function defineTargetItem()
    {

        var inputs=document.getElementsByName("targetCalcItems");
        var s="";
        for(i=0;i<inputs.length;i++){
            if(inputs[i].checked)
                s+=inputs[i].value+",";
        }
        for(i=0;i<inputs.length;i++){
            if(!inputs[i].checked){
                s+=inputs[i].value+",";
            }
        }
        examPlanForm.targetItem.value=s;
        examPlanForm.targetCalcItem.value=getTargetTraceItems("targetCalcItems");
        examPlanForm.targetTraceItem.value=getTargetTraceItems("targetTraceItems");
        examPlanForm.targetCollectItem.value=getTargetTraceItems("targetCollectItems");
        examPlanForm.targetMustFillItem.value=getTargetTraceItems("targetMustFillItems");
        examPlanForm.targetUsePrevious.value=getTargetTraceItems("targetUsePreviousb");
        var targetDefineItem_value = getTargetTraceItems("targetDefineItems");
        if(targetDefineItem_value=='')
            targetDefineItem_value=',';
        examPlanForm.targetDefineItem.value=targetDefineItem_value;
        examPlanForm.action='/performance/kh_plan/kh_params.do?b_defTargetItem=link&oper=close';
        examPlanForm.submit();
    }

    var _toObj;
    function setObj(o_obj)
    {
        _toObj=o_obj;
    }

    function allSelect(obj)
    {
        var objs = document.getElementsByName("targetCollectItems");
        if(obj.checked==true){
            for(var i=0;i<objs.length;i++)
            {
                objs[i].checked=true;
            }
        } else {
            for(var i=0;i<objs.length;i++)
            {
                objs[i].checked=false;
            }
        }
        globalFuc();
    }
    function changeTargetDefineItems(theObj)
    {
        var targetDefineTable=document.getElementById("targetDefineTable");
        var obj_tr = theObj.parentNode.parentNode;
        var theTable,theTables,theCollectTable,newRow;
        if(theObj.value.toUpperCase()=='RATER')
            return;
        if(theObj.checked)
        {
            theTable = document.getElementById("targetTraceTable");
            newRow=theTable.insertRow(theTable.rows.length);
            insertrow(newRow,obj_tr,"targetTraceItems",theObj);
            if(theObj!=null && theObj.value.toUpperCase()!='ATTACHMENT')
            {
                theTable = document.getElementById("targetCollectTable");
                newRow=theTable.insertRow(theTable.rows.length);
                insertrow(newRow,obj_tr,"targetCollectItems",theObj);
            }
            theTable = document.getElementById("targetMustFillTable");
            newRow=theTable.insertRow(theTable.rows.length);
            insertrow(newRow,obj_tr,"targetMustFillItems",theObj);

            theTable = document.getElementById("targetUsePreviousTable");
            newRow=theTable.insertRow(theTable.rows.length);
            insertrow(newRow,obj_tr,"targetUsePreviousb",theObj);

            var isOrnotBr = "yes";
            if(calItemStr.indexOf(","+theObj.value)!=-1)
            {
                var objs = document.getElementsByTagName('input');
                for(var i=0;i<objs.length;i++)
                {
                    if(objs[i].type=="checkbox" && objs[i].name=='targetCalcItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase())
                    {
                        isOrnotBr = "no";
                        break;
                    }
                }
                if(isOrnotBr=="yes")
                {
                    theTable = document.getElementById("targetCalcTable");
                    newRow=theTable.insertRow(theTable.rows.length);
                    insertrow(newRow,obj_tr,"targetCalcItems",theObj);
                    newRow.attachEvent('onclick',clickMouse2);
                    setObj(newRow.cells[0]);
                    clickMouse();
                }
            }
        }
        else
        {
            theTable = document.getElementById("targetTraceTable");
            theCollectTable = document.getElementById("targetCollectTable");
            theTables = document.getElementById("targetCalcTable");
            var objs = document.getElementsByTagName('input');
            for(var i=0;i<objs.length;i++)
            {
                if(objs[i].type=="checkbox" && objs[i].name=='targetTraceItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase())
                {
                    var temp_tr = objs[i].parentNode.parentNode;
                    for(var x=0;x<theTable.rows.length;x++)
                    {
                        if(theTable.rows[x]==temp_tr)
                        {
                            theTable = document.getElementById("targetTraceTable");
                            theTable.deleteRow(x);

                            theTable = document.getElementById("targetMustFillTable");
                            theTable.deleteRow(x);

                            theTable = document.getElementById("targetUsePreviousTable");
                            theTable.deleteRow(x);

                            break;
                        }
                    }
                }
                if(objs[i].type=="checkbox" && objs[i].name=='targetCollectItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase())
                {
                    var temp_tr = objs[i].parentNode.parentNode;
                    for(var x=0;x<theCollectTable.rows.length;x++)
                    {
                        if(theCollectTable.rows[x]==temp_tr)
                        {
                            if(theObj!=null && theObj.value.toUpperCase()!='ATTACHMENT')
                            {
                                theCollectTable = document.getElementById("targetCollectTable");
                                theCollectTable.deleteRow(x);
                            }
                            break;
                        }
                    }
                }

                if(objs[i].type=="checkbox" && objs[i].name=='targetCalcItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase() && (theObj!=null && theObj.value.toUpperCase()!='TASK_SCORE' && theObj.value.toUpperCase()!='ATTACHMENT'))
                {
                    var temp_tr = objs[i].parentNode.parentNode;
                    for(var x=0;x<theTables.rows.length;x++)
                    {

                        if(theTables.rows[x]==temp_tr)
                        {
                            theTables = document.getElementById("targetCalcTable");
                            theTables.deleteRow(x);
                            break;
                        }
                    }
                    break;
                }
            }
        }
        setEnable();
    }
    function closewindow()
    {
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.open("about:blank","_top").close();
        }
    }




</script>