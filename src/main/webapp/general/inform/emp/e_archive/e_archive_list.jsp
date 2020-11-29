<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes><!-- 【6974】信息浏览，切换到电子档案子集，界面的风格不对  jingq add 2015.02.03 -->
<script type="text/javascript">
<!--
 function browser(filename,a0100,nbase,typeid)
 {
 var ip=getLocalIPAddress();
 var name=getComputerName();
 var hashvo=new ParameterSet();
 hashvo.setValue("filename",filename);
 hashvo.setValue("ip",ip);
 hashvo.setValue("name",name);
 hashvo.setValue("a0100",a0100);
 hashvo.setValue("nbase",nbase);
 hashvo.setValue("typeid",typeid);
  	var request=new Request({method:'post',asynchronous:false,onSuccess:note_ok,functionId:'10100960002'},hashvo);

  	 	        
 }
 function note_ok(outparameters)
 {

 var filename=outparameters.getValue("filename");
 var logid=outparameters.getValue("logid");
 var height=window.screen.height-85;
 var Actual_Version=browserinfo();
 if (Actual_Version!=null&&Actual_Version.length>0&&Actual_Version=='7.0')
 {
				  	
				   height=height-25;
 }

  var theURL="/general/inform/emp/e_archive/view_e_archive.do?b_query=query&filename="+filename+"&logid="+logid+"&w="+(window.screen.width-10)+"&h="+height;
  var retvo= window.showModalDialog(theURL,"", 
            "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=yes;status=no;");
  if(retvo==null||retvo!=null)
  {
    var hashvo=new ParameterSet();
	hashvo.setValue("flag","1");		
   	var request=new Request({method:'post',asynchronous:false,onSuccess:note_logout_ok,functionId:'10100960003'},hashvo);
  }
 }
 function note_logout_ok(outparameters)
 {
 }
 function browserinfo(){
        var Browser_Name=navigator.appName;
        var Browser_Version=parseFloat(navigator.appVersion);
        var Browser_Agent=navigator.userAgent;
        
        var Actual_Version;
        var is_IE=(Browser_Name=="Microsoft Internet Explorer");
        if(is_IE){
            var Version_Start=Browser_Agent.indexOf("MSIE");
            var Version_End=Browser_Agent.indexOf(";",Version_Start);
            Actual_Version=Browser_Agent.substring(Version_Start+5,Version_End)
        }
       return Actual_Version;
    }
//-->
</script>
<html:html>
<body>
<html:form action="/general/inform/emp/e_archive/e_archive_list">
<table width="80%" border="0" cellspacing="1" align="center" cellpadding="1">
<tr>
<td colspan="8" nowrap>
(<bean:message key="columns.archive.unit"/>:<bean:write name="archiveForm" property="b0110"/>&nbsp;&nbsp;
<bean:message key="columns.archive.um"/>:<bean:write name="archiveForm" property="e0122"/>&nbsp;&nbsp;
<bean:message key="columns.archive.name"/>:<bean:write name="archiveForm" property="a0101"/>
)
</td>
</tr>
<tr>
<td>
<table width="100%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
<thead>
<tr>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.typeid"/>
</td>
<td align="center" width="15%" class="TableRow" nowrap>
<bean:message key="columns.archive.archivename"/>
</td>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.year"/>
</td>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.month"/>
</td>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.day"/>
</td>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.share"/>
</td>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.page"/>
</td>
<td align="center" width="8%" class="TableRow" nowrap>
<bean:message key="columns.archive.browse"/>
</td>
</tr>
</thead>
<% int i=0; %>
 <hrms:extenditerate id="element" name="archiveForm" property="archiveListform.list" indexes="indexes"  pagination="archiveListform.pagination" pageCount="15" scope="session">
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
          i++;          
          %> 
         <td align="left" class="RecordRow" nowrap>
         &nbsp;<bean:write name="element" property="typeid"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" nowrap>
         &nbsp;<bean:write name="element" property="archivename"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" nowrap>
         &nbsp;<bean:write name="element" property="year"/>&nbsp;
         </td>
         <td align="left" class="RecordRow"  nowrap>
         &nbsp;<bean:write name="element" property="month"/>&nbsp;
         </td>
         <td align="left" class="RecordRow"  nowrap>
         &nbsp;<bean:write name="element" property="day"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" nowrap>
         &nbsp;<bean:write name="element" property="share"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" nowrap>
         &nbsp;<bean:write name="element" property="page"/>&nbsp;
         </td>
         <td align="center" class="RecordRow" nowrap>
<img src="/images/browser.gif" border="0" width="20" height="20" alt="浏览" onclick="browser('<bean:write name="element" property="filename"/>','<bean:write name="element" property="a0100"/>','<bean:write name="element" property="nbase"/>','<bean:write name="element" property="itemid"/>');" style="cursor:hand"/>
         </td>
       
            </tr>		    
	</hrms:extenditerate> 
	</table>
	</td>
	</tr>
	</table>
    <table  width="80%" align="center">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>第
		   <bean:write name="archiveForm" property="archiveListform.pagination.current" filter="true"/>
		   页
		   共
		   <bean:write name="archiveForm" property="archiveListform.pagination.count" filter="true"/>
		   条
		   共
		   <bean:write name="archiveForm" property="archiveListform.pagination.pages" filter="true"/>
		   页
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="archiveForm" property="archiveListform.pagination" nameId="archiveListform" propertyId="archiveListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr>
   	  
</table> 
</html:form>
</body>
</html:html>