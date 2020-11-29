<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.smartphone.SPhoneForm"%>
<%@ page import="java.util.ArrayList"%>
<%
	 SPhoneForm sPhoneForm = (SPhoneForm)session.getAttribute("sphoneForm");
	 ArrayList dblist=sPhoneForm.getDblist();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>
	 <link rel="stylesheet" href="../jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/jquery.mobile-1.0a2.min.js"></script>	
	 <script type="text/javascript" src="../jquery/rpc_command.js"></script>	 
</head>
<body>
<div data-role="page" id="mainbar"> 
<html:form action="/phone-app/app/statchart">
	<div data-role="header"> 
	    <logic:equal value="chart" name="sphoneForm" property="returnvalue">
		   <a href="/phone-app/app/statchart.do?b_query=link&statid=${sphoneForm.statid}" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		</logic:equal>
		 <logic:equal value="list" name="sphoneForm" property="returnvalue">
		   <a href="/phone-app/app/statlist.do?b_query=link" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		</logic:equal>
		<h1>统计选项</h1>
	</div>	
	<div data-role="content">
	      <div data-role="fieldcontain" class="ui-field-contain ui-body ui-br">
						<label for="select-choice-1" class="select ui-select" style="font-weight: bold;">人员库</label>
						<div class="ui-select">
						<select name="select-choice-1" id="select-choice-1" tabindex="-1">
						    <logic:iterate id="element" name="sphoneForm"  property="dblist" indexId="index">
						      <logic:equal name="element" property="dataValue" value="${sphoneForm.nbase}">
						         <option value="${element.dataValue}" selected>${element.dataName}</option>	
						      </logic:equal>
						      <logic:notEqual name="element" property="dataValue" value="${sphoneForm.nbase}">
						         <option value="${element.dataValue}">${element.dataName}</option>	
						      </logic:notEqual> 					       
						    </logic:iterate>
						</select>
						</div>
		</div>
		 <div data-role="fieldcontain" class="ui-field-contain ui-body ui-br">
						<label for="select-choice-1" class="select ui-select" style="font-weight: bold;">统计图型</label>
						<div class="ui-select">
						<select name="select-choice-2" id="select-choice-2" tabindex="-1">
						     <logic:equal name="sphoneForm" property="charttype" value="1">
						         <option value="1" selected>柱状图</option>
						      </logic:equal>
						      <logic:notEqual name="sphoneForm" property="charttype" value="1">
						         <option value="1">柱状图</option>
						      </logic:notEqual> 
						      <logic:equal name="sphoneForm" property="charttype" value="2">
						         	<option value="2" selected>饼状图</option>
						      </logic:equal>
						      <logic:notEqual name="sphoneForm" property="charttype" value="2">
						         	<option value="2">饼状图</option>
						      </logic:notEqual> 
						      <logic:equal name="sphoneForm" property="charttype" value="3">
						        <option value="3" selected>线状图</option>	
						      </logic:equal>
						      <logic:notEqual name="sphoneForm" property="charttype" value="3">
						         <option value="3">线状图</option>	
						      </logic:notEqual> 	
						</select>
						</div>
		</div>
		
		<a href="javascript:setok('${sphoneForm.returnvalue}');" role="button" aria-label="button" data-theme="a" class="ui-btn ui-btn-corner-all ui-shadow ui-btn-up-a"><span class="ui-btn-inner ui-btn-corner-all"><span class="ui-btn-text">确定</span></span></a>
   	</div>
   	<!-- div data-role="footer"> 
		<h4>hjsoft</h4> 
	</div> -->

</html:form>
</div>
</body>
</html>
<script type="text/javascript">   
function setok(returnvalue)
{
    var obj=document.getElementsByName('select-choice-1');
    obj=obj[0];
    var nbase="";
    for(i=0,j=0;i<obj.options.length;i++)
    {
      if(obj.options[i].selected)
      {
    	 nbase=obj.options[i].value; 
    	 break;   	
      }
    }
    obj=document.getElementsByName('select-choice-2');
    obj=obj[0];
    var charttype="";
    for(i=0,j=0;i<obj.options.length;i++)
    {
      if(obj.options[i].selected)
      {
    	charttype=obj.options[i].value;    
    	break;	
      }
    }    
    if(returnvalue=="list")
    {
       sphoneForm.action="/phone-app/app/statlist.do?b_query=link&nbase="+nbase+"&charttype="+charttype;
    }else
    {
       sphoneForm.action="/phone-app/app/statchart.do?b_query=link&nbase="+nbase+"&charttype="+charttype;
    }
	sphoneForm.submit(); 
}
</script>