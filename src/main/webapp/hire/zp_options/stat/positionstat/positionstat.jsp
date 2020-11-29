<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.hire.zp_options.positionstat.PositionStatForm"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
<!--
var i=0;
function showpos(){
    var d1 = document.getElementById("editor1").value;
    var d2 = document.getElementById("editor2").value;
    
   	if(d1>d2){
   		alert("结束时间不能早于起始时间");
   		return;
   }
    
    if(trim(d1).length<=0||trim(d2).length<=0)
    {
        alert(APPOINT_COUNT_INTERVAL+"！");
        return;
    }
    if(checkDateTime(d1)&&checkDateTime(d2))
    {
    	positionStatForm.action="/hire/zp_options/stat/positionstat/positionstat.do?b_query=link";
        positionStatForm.submit();
	}else{
	   alert(COUNT_TIME_FORMAT_WRONG+"!");
	}
}
function outfile()
{
   	var hashvo=new ParameterSet();
   	hashvo.setValue("starttime","${positionStatForm.starttime}");
   	hashvo.setValue("endtime","${positionStatForm.endtime}");
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:out_ok,functionId:'3000001107'},hashvo);					
}
function out_ok(outparameters)
{
  var outName=outparameters.getValue("outName");
  var name=outName.substring(0,outName.length);
  name = decode(name);
  var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+name);
}
//-->
</script>
<%
PositionStatForm positionStatForm = (PositionStatForm)session.getAttribute("positionStatForm");
String schoolPosition=positionStatForm.getSchoolPosition();
String columnName="e01a1.label";
if(schoolPosition!=null&&schoolPosition.length()>0)
    columnName="e01a1.major.label";

 %>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/stat/positionstat/positionstat"> 
 <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:0px;">
<tr>
<td>
		<table border="0" cellspacing="0" align="left" cellpadding="0" style="margin-top:0px;">
		<tr>
		<td align="left">
		<bean:message key="hire.count.time"/>：<bean:message key="label.from"/> <input type="text" name="starttime"  extra="editor" style="width:95px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate" value="${positionStatForm.starttime}">
		           <bean:message key="label.to"/><input type="text" name="endtime"  extra="editor" style="width:95px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate" value="${positionStatForm.endtime}">
		 </td>
		 <td>
		 &nbsp;&nbsp;<BUTTON name="tfquery" class="mybutton" onclick="showpos();"><bean:message key="infor.menu.query"/></BUTTON>
		 </td> 
		 </td>         
		</tr>
		</table>
		
	</td>
	</tr>
	<tr>
	<td align="left">
	<table width="100%"  border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow" style="margin-top:5px;">
   	  <thead>
           <tr>
           			<td align="center" class="TableRow" nowrap> <bean:message key="label.title.org"/></td> 
           			<td align="center" class="TableRow" nowrap> <bean:message key="label.title.dept"/></td>
           			<td align="center" class="TableRow" nowrap> <bean:message key="<%=columnName%>"/></td>
           			<td align="center" class="TableRow" nowrap> <bean:message key="hire.position.count"/> </td>
           			<td align="center" class="TableRow" nowrap> <bean:message key="hire.apply.person"/> </td>
           		<%
           		    ArrayList condlist = positionStatForm.getCondlist();
           		    for(int n=0;n<condlist.size();n++)
           		    {
           		    LazyDynaBean  bean = (LazyDynaBean)condlist.get(n);
           		    String name=(String)bean.get("name"); 
           		 %>
           		 <td align="center" class="TableRow" nowrap><%=name%> </td>
           		 <%
           		 }
           		  %>
           </tr>
      </thead>
	<%
	  int i=0;
	 %>
	 <hrms:extenditerate id="element" name="positionStatForm" property="recordListform.list" indexes="indexes"  pagination="recordListform.pagination" pageCount="15" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
        <%
        }
        %>
         <td align="left" class="RecordRow" width="30%" nowrap>
         &nbsp;<bean:write name="element" property="un"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
           
         &nbsp;<bean:write name="element" property="um"/>&nbsp;

         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="atk"/>&nbsp;
         </td>
         <td align="right" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="z0313"/>&nbsp;
         </td>
         <bean:define id="z0301" name="element" property="z0301"/>
         <bean:define id="z0311" name="element" property="z0311"/>
         <bean:define id="count" name="element" property="count"/>
         <%
         	String str1 = "type=1&zp_pos_id="+z0301.toString()+"&atk="+z0311.toString()+"&count="+count.toString();
         	
         %>
          <td align="right" class="RecordRow" width="25%" nowrap>
        <a href="/hire/zp_options/stat/positionstat/person_wish.do?b_detail=detail&encryptParam=<%=PubFunc.encrypt(str1)%>"> &nbsp;<bean:write name="element" property="count"/>&nbsp;</a>
         </td>
         <%
         		String str2="";
              for(int h=0;h<condlist.size();h++)
              {
                    LazyDynaBean  bean = (LazyDynaBean)condlist.get(h);
           		    String conid=(String)bean.get("id"); 
          %>
         <bean:define id="conid1" name="element" property="<%=conid %>"/>
         <%
		    str2 = "type=2&zp_pos_id="+z0301.toString()+"&atk="+z0311.toString()+"&count="+conid1.toString()+"&condid="+conid;
         %>
        <td align="center" class="RecordRow" width="25%" nowrap>
          <a href="/hire/zp_options/stat/positionstat/person_wish.do?b_detail=detail&encryptParam=<%=PubFunc.encrypt(str2)%>">
         &nbsp;<bean:write name="element" property="<%=conid%>"/>&nbsp;
         </a>
         </td> 
         <%
         }
          %>
            </tr>	
            <%
          i++;          
          %> 	    
	</hrms:extenditerate> 
	</table>		
	</td>
	</tr>
	<tr>
	<td align="center">
	 <table  width='100%'  class='RecordRowP'  align='center' >
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
		   <bean:write name="positionStatForm" property="recordListform.pagination.current" filter="true"/>
		   <bean:message key="hmuster.label.paper"/>
		   <bean:message key="hmuster.label.total"/>
		   <bean:write name="positionStatForm" property="recordListform.pagination.count" filter="true"/>
		   <bean:message key="label.every.row"/>
		   <bean:message key="hmuster.label.total"/>
		   <bean:write name="positionStatForm" property="recordListform.pagination.pages" filter="true"/>
		   <bean:message key="hmuster.label.paper"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="positionStatForm" property="recordListform.pagination" nameId="recordListform" propertyId="recordListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr>   	    	     	  
</table> 
</td>
</tr>
<tr>
<td>
<table width="100%" align="center" style="margin-top:0px;margin-left:0px;" cellpadding="0" cellspacing="0">
<tr>
<td align="center" padding="0px">
<input type="button" name="back" class="mybutton" value="<bean:message key="goabroad.collect.educe.excel"/>" onclick="outfile();"style="margin-left:0px; margin-top:5px;"/>
<logic:equal value="dxt" name="positionStatForm" property="returnflag">
<hrms:tipwizardbutton flag="retain" target="il_body" formname="positionStatForm"/> 
</logic:equal>
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>

