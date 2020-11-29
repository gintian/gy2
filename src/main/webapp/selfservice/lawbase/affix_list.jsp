<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<jsp:useBean id="lawBaseForm" class="com.hjsj.hrms.actionform.lawbase.LawBaseForm" scope="session" />
<% int i = 0;%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">

<script type="text/javascript">
    function redirect() {
      parent.location = "/selfservice/lawbase/law_maintenance0.do?basetype=${lawbaseForm.basetype}";
    }
    
    function validatefilepath(){
	 	var mediapath=document.getElementsByName("content")[0].value;
		var flag = (mediapath.length>2)&&validateUploadFilePath(mediapath);
		if(flag){
		 	document.getElementById("wait").style.display="block";
			document.getElementById('buttonOk').disabled=true;
			document.getElementById('buttonDel').disabled=true;
			document.getElementById('buttonReturn').disabled=true;
			lawbaseForm.action="/selfservice/lawbase/affix.do?b_append=link";
			lawbaseForm.submit();
		}
	 }
    function validate(){
		var len = document.lawbaseForm.elements.length;
		var isCorrect = false;
		for(var i=0;i<len;i++){
			if(document.lawbaseForm.elements[i].type == "checkbox"){
				if(document.lawbaseForm.elements[i].checked == true && document.lawbaseForm.elements[i].name != "selbox")
					isCorrect = true;
			}
		}
		if(!isCorrect){
			alert("请选择需要删除的附件！");
		}else{
			return (confirm("确定删除选择的附件？"))
		}
    }
</script>
<hrms:themes cssName="content.css"></hrms:themes>
<form action="/selfservice/lawbase/affix.do" name="lawbaseForm" method="post" enctype="multipart/form-data">
	<table cellpadding="0" cellspacing="0" border="0" width="70%" style="border-collapse: collapse;margin-top: 55px;" align="center"> 
	<tr><td>
	<div class="RecordRow" style=" height:350px;width:100%;padding: 0;overflow:auto; ">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" style="border-collapse: collapse;">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap style="border-left: 0px;border-top: none;">
					 &nbsp;<input type="checkbox" name="selbox" onclick="batch_select(this,'paginationForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap>
					名 称
					&nbsp;
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap>
					版本
					&nbsp;
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap>
					创建日期
					&nbsp;
				</td>
				<td align="center" class="TableRow" style="border-top: none;" nowrap>
					创建人
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap style="border-right: 0px;border-top: none;">
					下载
					&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="lawbaseForm" property="paginationForm.list" indexes="indexes" pagination="paginationForm.pagination" pageCount="${lawbaseForm.pagerows}" scope="session">
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
          <td align="center" class="RecordRow" nowrap style="border-left: 0px;">
					&nbsp;<hrms:checkmultibox name="lawbaseForm" property="paginationForm.select" value="true" indexes="indexes" />&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(name)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(version)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
     				<bean:write name="element" property="string(create_time)" filter="true" />
                    
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(create_user)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap style="border-right: 0px;">
					<bean:define id="fileid" property="string(fileid)" name="element"/>
					<a href="/servlet/vfsservlet?fileid=<%=fileid %>" target=_blank>下载</a>
					&nbsp;
				</td>
          </tr>
		</hrms:extenditerate>
	</table>
</div>
</td></tr>
<tr><td>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		         <!--   <bean:message key="label.page.serial"/>
				<bean:write name="lawbaseForm" property="paginationForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
				<bean:write name="lawbaseForm" property="paginationForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
				<bean:write name="lawbaseForm" property="paginationForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/> --> 
				<hrms:paginationtag name="lawbaseForm"
								pagerows="${lawbaseForm.pagerows}" property="paginationForm.pagination"
							    refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="lawbaseForm" property="paginationForm.pagination" nameId="paginationForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
</td></tr>
<tr><td>
    <table width="100%" align="center" class="RecordRowP">
		<tr>
		    <td align="right" style="width: 20%;">文件名</td>
			<td >
				<input type="text" class="text6" style="width: 235px;" name="fileName" >
			</td>
		</tr>
		<tr>
		    <td align="right" style="width: 20%;">  附件上传</td>
		    <td>
		        <INPUT type="file" class="text6" style="width: 300px;" name= "content">
		    </td>
		</tr>
	</table>
</td></tr>
<tr><td>
	<table width="100%" align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center" style="padding-top: 5px;">
				<input type="button" name="b_append" id="buttonOk" class="mybutton" value="<bean:message key="button.ok"/>" onclick="return validatefilepath();">
				<input type="submit" name="b_del" id="buttonDel" class="mybutton" value="<bean:message key="button.delete" />"  onclick="return validate();">
				<!-- <input type="submit" name="br_return" class="mybutton" value="<bean:message key="button.return" />">  -->
				<input type="button" name="btnreturn" id="buttonReturn" value='返回' onclick="returnback();" class="mybutton">
			</td>
		</tr>
	</table>
   </td></tr>
  </table>
</form>
<div id='wait' style='position:absolute;top:180;left:300;display: none;'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style" height="40">正在保存...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;" class="complex_border_color" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
</table>
</div>
<script type="text/javascript">
<!--
function returnback(){
		
			window.location.href="/selfservice/lawbase/affix.do?br_return=link";
		
	}

//-->
</script>


