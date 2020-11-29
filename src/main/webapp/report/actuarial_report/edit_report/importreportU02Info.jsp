<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>

	</head>
	<%
	String updatehistory = request.getParameter("updatehistory");
	 %>
<script language='javascript'>
function goback()
{
	 //document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=get&id=${editReport_actuaialForm.id}&unitcode=${editReport_actuaialForm.unitcode}"
	document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?br_import=init&updatehistory=<%=updatehistory%>";
	document.editReport_actuaialForm.submit();
}
function tijiao()
{
   var waitInfo=eval("wait");	   
   waitInfo.style.display="block";
   document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?b_importsubmit=link"
   document.editReport_actuaialForm.submit();
}
function document.oncontextmenu() 
{ 
      return  false; 
} 
function MusterInitData()
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
}
</script>
<hrms:themes />
<body>
<form name="editReport_actuaialForm" method="post" action="/report/actuarial_report/edit_report/editreportU02List">
			<br>
			<br>
			  <fieldset align="center" style="width:50%;">
				<legend>
					上传统计信息
				</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="0">
					<tr>
						<td width="400">
							<Br>
							<bean:write name="editReport_actuaialForm" property="importInfo" filter="false" />
							<br>
							

						</td>
					</tr>
					<tr>
						<td>
							&nbsp;
						</td>
					</tr>
					<tr>
						<td align="center">							
							<input type="button" name="b_update" value="确认提交" class="mybutton"
								onClick="tijiao()">
							<input type="button" name="b_update" value="返回" class="mybutton"
								onClick="goback()">
						</td>
					</tr>
				</table>
			</fieldset>         	
                  
		</form>
	</body>	
</html>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在处理数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<script language="javascript">
 MusterInitData();
</script>
