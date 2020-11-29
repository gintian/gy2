<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.PerAnalyseForm,
                 com.hrms.struts.taglib.CommonData,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<html>
  <head>
    <hrms:themes />
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >

  </head>
  <script type="text/javascript" src="/js/constant.js"></script>
  <script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
  <script language='javascript' >
  	var a_object_id="";
  	function getPerResultTable(object_id)
  	{
  		//window.main.sub(object_id,document.perAnalyseForm.planIds.value);
		a_object_id=object_id;
  		window.main.location="/performance/kh_result/kh_result_tables.do?b_init=link&opt=analyse&a_distinctionFlag=0&a_objectId="+object_id+"&planID="+document.perAnalyseForm.planIds.value;
  	}
  	
  	function changePlan()
  	{
  	    document.perAnalyseForm.action="/performance/perAnalyse.do?b_perResultTable0=query";
		document.perAnalyseForm.target="detail";
		document.perAnalyseForm.submit();
  	
  	}
  	
  	
  	function executeExcel2()
	{
		if(a_object_id=="")
		{
			alert(P_A_INFO3+"!");
			return;
	    }
	    var hashvo=new ParameterSet();
		hashvo.setValue("distinctionFlag","0");
		hashvo.setValue("object_id",a_object_id);
		hashvo.setValue("plan_id",document.perAnalyseForm.planIds.value);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showFile2,functionId:'90100130025'},hashvo);
	}
  	
  	function showFile2(outparamters)
	{
		//zhangh 2020-4-7 下载改为使用VFS
		var outName=outparamters.getValue("fileName");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
  	
  </script>
  <body>
  	
  
  
   <html:form action="performance/perAnalyse">  
    &nbsp;<font size='2'> <bean:message key="kh.field.plan"/>:</font>&nbsp;
	<html:select name="perAnalyseForm" property="planIds" size="1" onchange="changePlan()">
  	 <html:optionsCollection property="perPlanList" value="dataValue" label="dataName"/>
	</html:select>&nbsp;
	 <input type='button' value='<bean:message key="general.inform.muster.output.excel"/>' onclick='executeExcel2()' class='mybutton' />
	<logic:equal name="perAnalyseForm" property="fromModule" value="analyse">
								<logic:equal name="perAnalyseForm" property="busitype" value="0">	
								<hrms:tipwizardbutton flag="performance" target="il_body" formname="perAnalyseForm"/> 
								</logic:equal>	
								<logic:equal name="perAnalyseForm" property="busitype" value="1">	
								<hrms:tipwizardbutton flag="capability" target="il_body" formname="perAnalyseForm"/> 
								</logic:equal>	
	</logic:equal>
	<Br>
	
	
	<iframe src="/templates/welcome/welcome.html" width="100%" height="93%" scrolling="auto" frameborder="0" name="main"></iframe>
	<script language='javascript'>
		//if(!getBrowseVersion()) {//非IE，将iframe的il_body置为和ie一样的name  detail
        if(parent.parent.frames["il_body"])
            parent.parent.frames["il_body"].name = "detail";
		//}
		var obj = parent.mil_menu.Global.selectedItem;
		var ori_text = "";
		//第一次进来的时候找到需要显示的第一个数据，可能是部门可能是人员
		//因为定位到第一个人的时候是在menu的树中，后台得不到前台找到树的数据进行查找，找到第一个人
		while(obj.getFirstChild()&&ori_text!=obj.getFirstChild().text)
		{
			ori_text=obj.getFirstChild().text;
			i++;
			obj.getFirstChild().expand();
			var a_obj=obj.getFirstChild();
			obj=a_obj;
			if(i==8)
				break;
		}
		a_object_id = obj.uid;//输出excel的时候需要用到
	  	window.main.location="/performance/kh_result/kh_result_tables.do?b_init=link&opt=analyse&a_distinctionFlag=0&a_objectId="+obj.uid+"&planID="+document.perAnalyseForm.planIds.value;
	</script>
   
   </html:form>
  </body>
</html>
