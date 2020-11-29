<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
UserView uv = (UserView)session.getAttribute(WebConstant.userView);
String isEditable = "true";
if(!uv.hasTheFunction("3110402"))
	isEditable = "false";
%>

<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="/ext/ext6/resources/ext-theme.css" type="text/css" />
<script type="text/javascript" src="/ext/ext6/ext-additional.js"></script>
<script type="text/javascript" src="/components/tableFactory/tableFactory.js"></script>

<script>
    function searchhunters(value,meta,record){
    	var groupid = record.data.z6000_e; 
    	var num = record.data.hunters;
    	return '<a href="/recruitment/headhuntermanage/searchheadhunter.do?b_search=link&huntergroupid='+groupid+'" target="_self">账号分配（'+num+'）</a>';
    }
  /*  function commonrender(value,meta,record){
		return value.replace(/</g,"&lt;"); 
    }
    function companyrender(value,meta,record){
		return value.replace(/<br>/g,"\r\n"); 
    } */
    function hunternamerender(value,meta,record){
    	if(!<%=isEditable%>)
 		   return value.replace(/</g,"&lt;");
    	var groupid = record.data.z6000_e; 
    	return "<a href='JavaScript:edithunter(\""+groupid+"\")' >"+value.replace(/</g,"&lt;")+"</a>";
    }
    function edithunter(groupid){
    	window.location.href='/recruitment/headhuntermanage/searchheadhuntergroup.do?b_edit=link&subType=view&huntergroupid='+groupid;
    }
    function insertheadergroup(param){
    	window.location.href='/recruitment/headhuntermanage/searchheadhuntergroup.do?b_edit=link&subType=insert';
    }
    function actionRow(){
    	if(!<%=isEditable%>)
  		   return false;
    	var groupid = arguments[1].data.z6000_e;
    	window.location.href='/recruitment/headhuntermanage/searchheadhuntergroup.do?b_edit=link&subType=view&huntergroupid='+groupid;
    }
    function loaduser(options,records){
    	if(records.length<1){
    		Ext.Msg.alert('信息', '请选择记录');
    		return;
    	}
    	var groupid = records[0].data.z6000_e;
    	window.location.href='/recruitment/headhuntermanage/searchheadhunter.do?b_search=link&huntergroupid='+groupid;
    }
    function deletegroup(){
    	var deletedata = Ext.getCmp('tableFactory_tablePanel').getSelectionModel().getSelection();
    	if(deletedata.length<1)
        {
    		Ext.Msg.alert('提示信息',"请选择删除数据！");
			return;
        }
    	var datalist = "";
    	for(var i=0;i<deletedata.length;i++)
    	{
    		datalist += deletedata[i].data.z6000_e+"`";
        }
    	var map = new HashMap();
		map.put("deletedata", datalist);
    	Rpc( {
    		functionId : 'ZP0000002152',
    		success :returnResult
    	}, map);
    }
    function returnResult(param)
    {
    	var result = Ext.decode(param.responseText);
    	var hinttext = result.hinttext;
    	if(hinttext!="")
    	{
    		Ext.Msg.alert('提示信息',hinttext);
        }
    	var store = Ext.data.StoreManager.lookup('tableFactory_dataStore');
        store.load();
    	Ext.getCmp('tableFactory_tablePanel').getSelectionModel().clearSelections();//清空选择记录
    }
    Ext.onReady(function(){
    	<hrms:tableFactory title="猎头管理" subModuleId="zp_headhunter_00001" sqlProperty="sqlstr"  jsObjName="tableFactory" autoRender="true" constantName="${headHunterGroupForm.constantxml}"  currentPage="${headHunterGroupForm.pageable.pageNumber }"
    	                    isColumnFilter="true" fieldAnalyse="true" columnProperty="groupcolumns" formName="headHunterGroupForm" pagesize="${headHunterGroupForm.pageable.pageSize }" isScheme="true" isSetScheme="true" schemePosition="toolbar"  showPublicPlan="${headHunterGroupForm.showPublicPlan}">
    	</hrms:tableFactory>
    });
</script>



