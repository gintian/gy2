<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/dict.js"></script> 
<html:form action="/performance/commend/insupportcommend/initInSupportCommend">

<table id="test" width="100%" border="0"><tr><td>
<hrms:dataset name="inSupportCommendForm" property="commendList" scope="session" setname="${inSupportCommendForm.tabname}"  setalias="p02_set" readonly="false" editable="true" select="true" sql="${inSupportCommendForm.sql}" buttons="movefirst,prevpage,moveprev,movenext,nextpage,movelast">
	     
	     <hrms:commandbutton name="delete" functionId="9010030006"  hint="确认执行删除操作？\r\n只能删除起草，结束状态下的纪录！" refresh="true" type="selected" setname="${inSupportCommendForm.tabname}">
	          删除
	     </hrms:commandbutton>  
	     
	    
	     <hrms:commandbutton name="save" functionId="9010030007" hint="确认保存！" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}">
	     	保存
	     </hrms:commandbutton>
	     
	     
	      <hrms:commandbutton name="apply" functionId="3000000151" refresh="true" type="selected" setname="${inSupportCommendForm.tabname}" >
	     导出Excel
	     </hrms:commandbutton>
	</hrms:dataset>
</td></tr></table>
</html:form>

