<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,				
				com.hrms.struts.constant.WebConstant" %>
<%

	ObjectCardForm objectCardForm = (ObjectCardForm)session.getAttribute("objectCardForm");
	String datajson = objectCardForm.getDatajson();

%>

<link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all-old.css" />
<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all-old.js"></script>
<script type="text/javascript" src="/ext/ColumnNodeUI.js"></script>
<link rel="stylesheet" type="text/css" href="/ext/column-tree.css" />

<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>

<style type="text/css"> 
.x-tree-node{color:black;font:normal 11px arial,tahoma,helvetica,sans-serif;white-space:normal;} 
</style>

<script language='javascript' >
var columns = "${objectCardForm.columns}";
var columnsHead = eval("(" + columns + ")"); // 将JSON数据对象化

var datajson = "${objectCardForm.datajson}";
var json = eval("(" + datajson + ")"); // 将JSON数据对象化

Ext.onReady(function()
{
    var tree = new Ext.tree.ColumnTree({
        width: 625,
        height: 330,
        rootVisible:false,
        autoScroll:true,
        closable:true,
//      useArrows:true,     // 展开树的子节点图标为三角箭头
        
        title: '任务分解情况表',
        renderTo: 'tree-example',        
        columns:columnsHead,
        loader: new Ext.tree.TreeLoader({
        //  dataUrl:'/performance/objectiveManage/objectiveCard.do?b_extJson=query',
            uiProviders:{
                'col': Ext.tree.ColumnNodeUI
            }
        }),
        root: new Ext.tree.AsyncTreeNode({
            text:'Tasks',
            children: json            
        })
    });
    tree.getRootNode().expand(true,true); // 默认展开树的所有节点     
//	tree.expand(true,true);	 
//	tree.ExpandLevel = 2;
});

</script>

<html:form action="/performance/objectiveManage/objectiveCard">
				
	<div id="tree-example"></div>													
	
</html:form>

