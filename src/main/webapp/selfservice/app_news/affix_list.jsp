<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<jsp:useBean id="lawBaseForm" class="com.hjsj.hrms.actionform.lawbase.LawBaseForm" scope="session" />
<% int i = 0;%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript">
  function deletefile()
  {
	var len=document.appNewsForm.elements.length;
     var isCorrect=false;	
     for (i=0;i<len;i++)
     {
           if (document.appNewsForm.elements[i].type=="checkbox")
            {
              if( document.appNewsForm.elements[i].checked==true)
                isCorrect=true;
            }
     }
    if(isCorrect)
    {
    	if(confirm(DEL_ALL_CHOICE_FILE))
     {
          appNewsForm.action = "/selfservice/app_news/appmessage.do?b_delfile=link";
          appNewsForm.submit();
     }
    }
  }
  function check()
  {
  	var path=appNewsForm.newsfile.value;
		var fso=new ActiveXObject("Scripting.FileSystemObject");
		if(path.length>0)   
		if(!fso.FileExists(path))
		{
			alert(CHOICE_FILE_NOT_EXIST);
			return;
		}
		if(path.length>0)  {
		  	appNewsForm.action = "/selfservice/app_news/appmessage.do?b_addfile=link";
		    appNewsForm.submit();
	    }
  }
  function goback()
  {
  	if('${appNewsForm.type}'=='receive'&&'${appNewsForm.isdraft}'=='0'){
	  	appNewsForm.action = "/selfservice/app_news/appmessage.do?b_writemassage=link&isdraft=0&news_id="+'${appNewsForm.news_id}';
	  	appNewsForm.submit();
	}
	else if('${appNewsForm.type}'=='select'&&'${appNewsForm.isdraft}'=='1')
	{	
		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_query=link&type=select&isdraft=1";
	  	appNewsForm.submit();
	}
	else if('${appNewsForm.type}'=='receive'&&'${appNewsForm.isdraft}'=='1')
	{
		appNewsForm.action = "/selfservice/app_news/appmessage.do?b_query=link&type=receive&isdraft=1&news_id=";
	  	appNewsForm.submit();
	}
  }
</script>

<html:form action="/selfservice/app_news/appmessage" enctype="multipart/form-data">
	<br>
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="column.select" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="column.law_base.name" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="label.commend.date" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="reportcheck.download" />
					&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="appNewsForm" property="affixListForm.list" indexes="indexes" pagination="affixListForm.pagination" pageCount="10" scope="session">
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
          <td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="appNewsForm" property="affixListForm.select" value="true" indexes="indexes" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="name" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="createtime" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<a href="/sys/downloadall?id=<bean:write name="element" property="ext_file_id" filter="true"/>&fileid=ext_file_id&tablename=appoint_news_ext_file&filenamecolumn=name&ext=ext&content=content"><bean:message key="reportcheck.download" /></a>
					&nbsp;
				</td>
          </tr>
		</hrms:extenditerate>
	</table>

	<table width="80%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial"/>
				<bean:write name="appNewsForm" property="affixListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
				<bean:write name="appNewsForm" property="affixListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
				<bean:write name="appNewsForm" property="affixListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page"/>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="appNewsForm" property="affixListForm.pagination" nameId="affixListForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	
	<logic:equal name="appNewsForm" property="isdraft" value="0">
    <table width="70%" align="center">
		<tr>
		    <td>
		                <bean:message key="conlumn.mediainfo.info_sort"/>
		    </td>
			<td >
				<html:text name="appNewsForm" property="fileName"></html:text>
			</td>
		</tr>
		<tr>
		    <td>
		        <bean:message key="lable.board.uploadaccessories"/>
		    </td>
		    <td>
		        <html:file name="appNewsForm" property="newsfile" size="20" accept="doc,txt,xls,pdf"/>&nbsp;
		    </td>
		</tr>
	</table>
	</logic:equal>
	<table width="80%" align="center">
		<tr>
			<td align="center">
				<logic:equal name="appNewsForm" property="isdraft" value="0">
				<input type="button" name="b_addfile" class="mybutton" value="<bean:message key="button.ok"/>" onclick="check()">
				</logic:equal>
				<input type="button" name="b_delfile" class="mybutton" value="<bean:message key="button.delete" />" onclick="deletefile();">
				<input type="button" name="return" class="mybutton" value="<bean:message key="button.return" />" onclick="goback();">
			</td>
		</tr>
	</table>
   
</html:form>

