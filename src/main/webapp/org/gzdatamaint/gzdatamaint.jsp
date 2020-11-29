<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.org.gzdatamaint.GzDataMaintForm,java.util.*"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
	
	
	GzDataMaintForm gzDataMaintForm=(GzDataMaintForm)session.getAttribute("gzDataMaintForm"); 
	String returnflag=gzDataMaintForm.getReturnflag();
	String fieldsetid=gzDataMaintForm.getFieldsetid();
	String state=userView.analyseTablePriv(fieldsetid.toUpperCase());  // 2013-11-17  dengc  解决薪资基础数据维护子集、指标权限控制不对问题
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="./gzdatamaint.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<hrms:themes></hrms:themes>
<html:form action="/org/gzdatamaint/gzdatamaint">
<logic:equal value="1" name="gzDataMaintForm" property="hasParam">
<br>
<br>
<br>
<p align="center">
<bean:message key="workdiary.message.related.subset.notset"/>
<br>
<logic:equal value="2" name="gzDataMaintForm" property="gzflag">
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="gzDataMaintForm"/> 
</logic:equal>
<logic:equal value="3" name="gzDataMaintForm" property="gzflag">
<hrms:tipwizardbutton flag="insurance" target="il_body" formname="gzDataMaintForm"/> 
</logic:equal>
</p>
</logic:equal>
<logic:equal value="0" name="gzDataMaintForm" property="hasParam">
<html:hidden name="gzDataMaintForm" property="sort_str" />
<html:hidden name="gzDataMaintForm" property="infor" />
<%if("hl".equals(hcmflag)){ %>
<table><tr><td>
<%}else{ %>
<table style="margin-top:-5px;"><tr><td>
<%} %>

<table><tr><td>
<hrms:menubar menu="menubar1" id="menubar1">
<% if(state.equals("2")){ %>
  	<hrms:menuitem name="gz1" label="infor.menu.edit" >
    	<hrms:menuitem name="m1" label="infor.menu.new" icon="/images/quick_query.gif" url="insert('${gzDataMaintForm.tablename}','${gzDataMaintForm.a_code}','add');" command="" />
    	<hrms:menuitem name="m2" label="infor.menu.ins" icon="/images/deal.gif" url="insert('${gzDataMaintForm.tablename}','${gzDataMaintForm.a_code}','insert');" command="" />  
    	<hrms:menuitem name="m3" label="infor.menu.del" icon="/images/del.gif" url="" command="delselected" hint="general.inform.search.confirmed.del" />  
    </hrms:menuitem> 
 <% } %>   
  	<hrms:menuitem name="batch" label="menu.gz.batch" >
    	<hrms:menuitem name="mitem1" label="infor.menu.batupdate_s" icon="/images/add_del.gif" url="batchHand(1,'${gzDataMaintForm.a_code}','4','${gzDataMaintForm.gzflag}');" command="" enabled="true" visible="true"/>
      	<hrms:menuitem name="mitem2" label="infor.menu.batupdate_m" icon="/images/write.gif" url="batchHand(2,'${gzDataMaintForm.a_code}','4','${gzDataMaintForm.gzflag}');" command="" enabled="true" visible="true"/>
      <% if(state.equals("2")){ %>
      	<hrms:menuitem name="mitem3" label="infor.menu.batupdate_a" icon="" url="batchHand(3,'${gzDataMaintForm.a_code}','${gzDataMaintForm.infor}','${gzDataMaintForm.gzflag}');" command="" enabled="true" visible="true"/>
      	<hrms:menuitem name="mitem4" label="infor.menu.batupdate_d" icon="" url="batchHand(4,'${gzDataMaintForm.a_code}','${gzDataMaintForm.infor}','${gzDataMaintForm.gzflag}');" command="" enabled="true" visible="true"/>
     <% } %>   
    	<hrms:menuitem name="batch5" label="infor.menu.compute" icon="" url="batchCond('${gzDataMaintForm.a_code}','${gzDataMaintForm.infor}','${gzDataMaintForm.unit_type}','${gzDataMaintForm.gzflag}');" command="" />
  	</hrms:menuitem>  
  	<hrms:menuitem name="show" label="infor.menu.view">
      	<hrms:menuitem name="show1 " label="infor.menu.ssort" icon="" url="to_sort_subset_info('${gzDataMaintForm.fieldsetid}');" command=""/>
      	<hrms:menuitem name="show2" label="infor.menu.hide" icon="" url="to_hide_field('${gzDataMaintForm.fieldsetid}');" command=""/>
      	<hrms:menuitem name="show3" label="infor.menu.sortitem" icon="" url="to_sort_field('${gzDataMaintForm.fieldsetid}');" command=""/>
      	<logic:equal name="gzDataMaintForm" property="viewdata" value="1">
      		<hrms:menuitem name="show4" label="org.gzdatamaint.gzdatamaint.viewdata" icon="" url="viewRecord('0','${gzDataMaintForm.infor}','${gzDataMaintForm.gzflag}','${gzDataMaintForm.a_code}');" checked="true" command=""/>
      	</logic:equal>
      	<logic:notEqual name="gzDataMaintForm" property="viewdata" value="1">
      		<hrms:menuitem name="show4" label="org.gzdatamaint.gzdatamaint.viewdata" icon="" url="viewRecord('1','${gzDataMaintForm.infor}','${gzDataMaintForm.gzflag}','${gzDataMaintForm.a_code}');" command=""/>
      	</logic:notEqual>
 	</hrms:menuitem>
 	<logic:equal name="gzDataMaintForm" property="gzflag" value="2"> 
 	<hrms:priv func_id="32412"> 
 	<hrms:menuitem name="set" label="kh.field.config">
      	<hrms:menuitem name="set1 " label="org.gzdatamaint.gzdatamaint.fieldset" icon="" url="setSort('${gzDataMaintForm.infor}','${gzDataMaintForm.gzflag}')" command=""/>
 	</hrms:menuitem> 
 	</hrms:priv>
 	</logic:equal>
 	<logic:equal name="gzDataMaintForm" property="gzflag" value="3"> 
 	<hrms:priv func_id="32506"> 
 	<hrms:menuitem name="set" label="kh.field.config">
      	<hrms:menuitem name="set1 " label="org.gzdatamaint.gzdatamaint.fieldset" icon="" url="setSort('${gzDataMaintForm.infor}','${gzDataMaintForm.gzflag}')" command=""/>
 	</hrms:menuitem> 
 	</hrms:priv>
 	</logic:equal>
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;
</td>
</tr></table>
<hrms:dataset name="gzDataMaintForm" property="itemlist" scope="session" 
	setname="${gzDataMaintForm.tablename}"  setalias="position_set" 
	readonly="false" editable="true" select="true" pagerows="20" 
	sql="${gzDataMaintForm.selectsql}" buttons="movefirst,prevpage,nextpage,movelast">      
	<% if(state.equals("2")){ %>
	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" type="selected" setname="${gzDataMaintForm.tablename}" onclick="insert('${gzDataMaintForm.tablename}','${gzDataMaintForm.a_code}','add');" >
     <bean:message key="button.insert"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="inserts" hint="" functionId="" refresh="true" type="selected" setname="${gzDataMaintForm.tablename}" onclick="insert('${gzDataMaintForm.tablename}','${gzDataMaintForm.a_code}','insert');" >
     <bean:message key="button.new.insert"/>
   </hrms:commandbutton>
   
    <hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="1602010232" refresh="true" type="selected" setname="${gzDataMaintForm.tablename}" >
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   
   <% } %>
    <hrms:commandbutton name="savedata" functionId="1602010235" refresh="false" type="all-change" setname="${gzDataMaintForm.tablename}" >
     <bean:message key="button.save"/>
   </hrms:commandbutton>   
   <hrms:commandbutton name="compute"  functionId="" refresh="true" type="selected" onclick="batchCond('${gzDataMaintForm.a_code}','${gzDataMaintForm.infor}','${gzDataMaintForm.unit_type}','${gzDataMaintForm.gzflag}');" setname="${gzDataMaintForm.tablename}">
     <bean:message key="button.computer"/>
   </hrms:commandbutton>  
  <% if(returnflag!=null&&returnflag.equalsIgnoreCase("dxt")){ %>
   <hrms:commandbutton name="firstpage" function_id="" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${gzDataMaintForm.tablename}" onclick="returnFirst('${gzDataMaintForm.gzflag}');" >
     <bean:message key="reportcheck.return"/>
   </hrms:commandbutton> 
  <% } %>
</hrms:dataset>
<%if("hl".equals(hcmflag)){ %>
<table border="0" width="300" style="position:absolute;left:360px;top:30px;">
<%}else{ %>
<table border="0" width="300" style="position:absolute;left:360px;top:35px;">
<%} %>

 <tr><td>
 <bean:message key="infor.label.setlist"/>
	<html:select name="gzDataMaintForm" property="fieldsetid" style="width:150" onchange="changeSub();">
		<html:optionsCollection property="fieldlist" value="dataValue" label="dataName" />
	</html:select>
	</td></tr>
</table>
<html:hidden name="gzDataMaintForm" property="viewdata"/>
<html:hidden name="gzDataMaintForm" property="a_code"/>
<html:hidden name="gzDataMaintForm" property="infor"/>
<html:hidden name="gzDataMaintForm" property="gzflag"/>
</logic:equal>
</html:form>
