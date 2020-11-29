<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.actionform.performance.options.PerRelationForm,com.hjsj.hrms.businessobject.sys.SysParamBo" %>

<script language="javascript" src="/performance/options/kh_relation/per_relation.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<style>
.fixedtab 
{ 
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 1pt solid;
	BORDER-RIGHT: #C4D8EE 1pt solid;
	BORDER-TOP: #C4D8EE 1pt solid;
}
.fixedHeaderTr1 td{
    BORDER-COLOR: #C4D8EE !important;
    BORDER-TOP:0px;
}
.toolbars{
    position:relative;
    left:-5px;
}
</style>
<hrms:themes />
<%
	PerRelationForm myForm=(PerRelationForm)session.getAttribute("perRelationForm");	
	HashMap joinedObjs = (HashMap)myForm.getJoinedObjs();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	boolean flag = true;
	String themes = "";
	if(userView!=null)
		themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
%>
<script>

//window.status=KH_RELATION_INFO3;
var aclientHeight=document.body.clientHeight;
document.body.onbeforeunload=function(){ 
	window.status='';
}
function resizeWindowRefrsh()
{
        var aclientHeight=document.documentElement.clientHeight||document.body.clientHeight;
		if(aclientHeight-84>0)
		{
			document.getElementById('a_table_div').style.height=aclientHeight-84;
		}
}
function sub1(o)
{
    perRelationForm.action="/performance/options/kh_relation.do?b_queryObj=link&operate=init0";
	perRelationForm.submit();
}

var themes = "<%=themes %>";
var bgColor="";
var borderColors="";
if(themes=="default"){
    Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{color:#414141 !important;}");
}
bgColor="#F9F9F9";
borderColors = "#C5C5C5";
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mc{height:23px !important;border:1px solid "+borderColors+";background-color:"+bgColor+" !important;}","menu_ms_bg");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-ml{display:none;background:none !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mr{display:none;background:none !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small{border-color:"+borderColors+" !important;background-color:"+bgColor+" !important;}","menu1");
Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small{background-color:"+bgColor+" !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{padding:2px 4px !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-bc{height:0px !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-tc{height:0px !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-br{padding-right:0px !important;}","");
Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-tr{padding-right:0px !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-br{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-bl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-tr{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-tl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tr{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-frame-bl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tl{background-image:none !important;}","");
Ext.util.CSS.createStyleSheet("#toolbars .x-frame-br{background-image:none !important;}","");
Ext.onReady(function(){
   var menu1 = new Ext.menu.Menu({
       allowOtherMenus: false,
       items: [
           new Ext.menu.Item({
               text: "手工选择",
               icon:"/images/quick_query.gif",
               handler: function(){
            	   handSelect('2');
               }
           }),
           new Ext.menu.Item({
               text: "条件选择",
               icon:"/images/quick_query.gif",
               handler: function(){
            	   conditionselect();
               }
           }),
           new Ext.menu.Item({
               text: "批量设置考核对象类别",
               handler: function(){
            	   batchSetObjType();
               }
           }),
           new Ext.menu.Item({
               text: "删除考核对象",
               icon:"/images/del.gif",
               handler: function(){
            	   delObjects();
               }
           }),
           new Ext.menu.Item({
               text: "查询",
               icon:"/images/quick_query.gif",
               handler: function(){
            	   query();
               }
           }),
           new Ext.menu.Item({
               text: "同步人员顺序",
               icon:"/images/write.gif",
               handler: function(){
            	   sortman();
               }
           })
       ]
   }); 
   var menu2 = new Ext.menu.Menu({
       allowOtherMenus: false,
       items: [
           new Ext.menu.Item({
               text: "个别自动生成",
               icon:"/images/edit.gif",
               handler: function(){
            	   autoGetBody('individual');
               }
           }),new Ext.menu.Item({
               text: "批量自动生成",
               handler: function(){
            	   autoGetBody('batch');
               }
           }),new Ext.menu.Item({
               text: "指定考核主体",
               icon:"/images/compute.gif",
               handler: function(){
            	   mainBodySel();
               }
           }),new Ext.menu.Item({
               text: "清除考核主体",
               handler: function(){
            	   cleanMainBody();
               }
           }),new Ext.menu.Item({
               text: "复制考核主体",
               handler: function(){
            	   copyKhMainBody();
               }
           }),new Ext.menu.Item({
               text: "粘贴考核主体",
               icon:"/images/check.gif",
               handler: function(){
            	   pasteKhMainBody();
               }
           })
       ]
   }); 
   var toolbar = Ext.create("Ext.Toolbar", {
       renderTo: "toolbars",
       width: 215,
       margin:'0 -2 -4 0',
       border:false,
       items:[{
	        text: "考核对象",
	        menu: menu1,
	        height:24
	    },{
	        text: "考核主体",
	        menu: menu2,
	        height:24
	    }]
   });
});
</script>
<body onResize="resizeWindowRefrsh()" style="overflow:hidden;" >
<html:form action="/performance/options/kh_relation"> 
<table width='100%'>

<tr><td>
		<div id="toolbars" class="toolbars"/>
	</td>
	
</tr></table>

	<script language='javascript' >
		var theHeight=0;
		<%if(userView.hasTheFunction("326060701")||userView.hasTheFunction("326060702")||userView.hasTheFunction("326060703")||userView.hasTheFunction("326060704")||userView.hasTheFunction("326060705")||userView.hasTheFunction("326060706")|| userView.hasTheFunction("326060707")||userView.hasTheFunction("326060708")||userView.hasTheFunction("326060709")||userView.hasTheFunction("326060710")||userView.hasTheFunction("326060711")||userView.hasTheFunction("326060712")){ %>
			theHeight=document.documentElement.clientHeight-102;
		<%}else{ %>	
			theHeight=document.documentElement.clientHeight-80;
		<%}%>
        var theWidth = document.body.clientWidth-12;
        document.write("<div class=\"fixedtab\" id=\"a_table_div\" style='position:absolute;left:5px;height:"+theHeight+"px;width:"+theWidth+"px;'>");
 	</script> 
  <table id='a_table' width="100%" border="0" cellspacing="0"    align="center" cellpadding="0" class="ListTable">
   	  <thead>
 		<tr class="fixedHeaderTr1"> 
	         <td align="center"  class="TableRow" nowrap style="border-left:0px;">				
				 <input type="checkbox" name="selbox" onclick="batch_select(this,'objectID');" title='<bean:message key="label.query.selectall"/>'>
	         </td>         
	         <td align="center"  class="TableRow" nowrap >
			   	<bean:message key="b0110.label"/>
		     </td>          
	         <td align="center"  class="TableRow" nowrap >
				 <%
	         		 FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         	 %>	         
			 	 <%=fielditem.getItemdesc()%>
		     </td>
		     <td align="center"  class="TableRow" nowrap >
			 	 <bean:message key="e01a1.label"/>
		     </td>
		      <td align="center"  class="TableRow" nowrap >
			 	 <bean:message key="hire.employActualize.name"/>
		     </td>
		     <td align="center" style="border-right:0px;" class="TableRow" nowrap>
				 <bean:message key="performance.implement.objecttype"/>
		     </td> 	   	        	        
         </tr>
   	  </thead>
   	  <%  int i=0; %>
   	  <hrms:extenditerate id="element" name="perRelationForm" property="perObjectForm.list" indexes="indexes"  pagination="perObjectForm.pagination" pageCount="${perRelationForm.pagerows}" scope="session">
   	  	<bean:define id="oid" name="element" property="object_id" />
   	  	<% 
   	  			  	
		     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
		     		String object_id=(String)abean.get("object_id");
		     	 	String canEdit = joinedObjs.get(object_id)!=null?"0":"1";
   	  	
   	  	i++;
   	  	   if(i%2==1){ %>
   	  	   <tr class='trShallow' onClick="javascript:tr_onclick(this,'#F3F5FC')">
   	  	   <% } else { %>	   
   	  	   	<tr class='trDeep'  onClick="javascript:tr_onclick(this,'#E4F2FC')">
   	  	   <% } %>
		
	         <td align="center" class="RecordRow" nowrap onclick='selectRow("${oid}","<%=canEdit %>")'  style="border-left:0px;border-top:0px;">
			  	<input type='checkbox' name='objectID' value='${oid}'  />
	         </td>         
	         <td align="left" style="border-top:0px;" class="RecordRow" onclick='selectRow("${oid}","<%=canEdit %>")' nowrap >
			    &nbsp;<bean:write name="element" property="b0110" filter="true"/>
		     </td>          
	         <td align="left" style="border-top:0px;" class="RecordRow" onclick='selectRow("${oid}","<%=canEdit %>")' nowrap>
			    &nbsp; <bean:write name="element" property="e0122" filter="true"/>
		     </td>   
		     <td align="left" style="border-top:0px;" class="RecordRow" onclick='selectRow("${oid}","<%=canEdit %>")' nowrap>
			     &nbsp;<bean:write name="element" property="e01a1" filter="true"/>
	         </td>         
	         <td align="left" style="border-top:0px;" class="RecordRow" onclick='selectRow("${oid}","<%=canEdit %>")' nowrap >
			    &nbsp; <bean:write name="element" property="a0101" filter="true"/>
		     </td>          
	         <td align="center" style="border-top:0px;border-right:0px;" class="RecordRow" nowrap onclick='selectRow("${oid}","<%=canEdit %>")'  style="border-right:0px;">
	         
	         <%	if(joinedObjs.get(object_id)!=null){%>
				<html:select name="element" property="obj_body_id" size="1" disabled="true" style="width:160px">
			  	  <html:optionsCollection property="allObjectTypes" value="dataValue" label="dataName"/>
				</html:select>	
			<%}else{%>	         		  
				<html:select name="element" property="obj_body_id" size="1" onchange="setType('${oid}','','obj',this,'','')" style="width:160px">
			  	  <html:optionsCollection property="objectTypes" value="dataValue" label="dataName"/>
				</html:select>	
			<%}%>			  	
		     </td>         	        	        
         </tr>   	  
   	   </hrms:extenditerate>  	  
	</table>
    <table width="100%" align="center" style="border-left: 0px;border-right: 0px;" class="RecordRowP"  >
        <tr>
            <td valign="bottom" align="left" class="tdFontcolor">
                <span>
                <bean:message key="label.page.serial"/>
                <bean:write name="perRelationForm"
                            property="perObjectForm.pagination.current" filter="true" />
                <bean:message key="label.page.sum"/>
                <bean:write name="perRelationForm"
                            property="perObjectForm.pagination.count" filter="true" />
                <bean:message key="label.page.row"/>
                <bean:write name="perRelationForm"
                            property="perObjectForm.pagination.pages" filter="true" />
                <bean:message key="label.page.page"/>&nbsp;&nbsp;
                每页显示<html:text property="pagerows" name="perRelationForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:sub1(0);">刷新</a>
                </span>
            </td>
            <td align="right" nowrap class="tdFontcolor">
                <span style="float: right;">
                    <hrms:paginationlink name="perRelationForm"
                                         property="perObjectForm.pagination" nameId="perObjectForm"
                                         propertyId="roleListProperty">
                    </hrms:paginationlink>
                </span>
            </td>
        </tr>
    </table>
    </div>
	<html:hidden name="perRelationForm" property="paramStr"/>
</html:form>
</body>
<script type="text/javascript">
var a_code="${perRelationForm.a_code}";
var table = document.getElementById('a_table');
if(table.rows.length>1)
{
	myFireEvent(table.rows[1]);
	//触发选中行事件，刷新下面的主体列表。
	myFireEvent(table.rows[1].cells[1]);
}
else {
    //parent.frames[0] 导向的应该是左侧的组织机构的那个iframe,应该是parent.frames['ril_body2']吧 haosl 2019年4月1日
	parent.frames['ril_body2'].location="/performance/options/kh_relation/mainBodyList.do?b_queryBody=link&objectid=aaa";
}

/* 兼容fireEvent方法 */
function myFireEvent(el) {
	var evt;
	if (document.createEvent) {
		evt = document.createEvent("MouseEvents");
		evt.initMouseEvent("click", true, true, window,
				0, 0, 0, 0, 0, false, false, false, false, 0, null);
		el.dispatchEvent(evt);
	} else if (el.fireEvent) { // IE
		el.fireEvent("onclick");
	}
}
</script>