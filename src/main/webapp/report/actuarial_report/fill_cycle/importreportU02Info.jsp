<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>

	</head>
<script language='javascript'>
function goback()
{
	 
	document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?br_import=init";
	document.reportCycleForm.submit();
}
function tijiao(Report_id)
{
   var waitInfo=eval("wait");	   
   waitInfo.style.display="block";
    if(Report_id.indexOf("U02")!=-1)
   			{
     		 document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_importsubmit=link"
   			 document.reportCycleForm.submit(); 
  			 }else if(Report_id=="U03")
  			 {
   			document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_importsubmit03=link"
  		    document.reportCycleForm.submit();
  			 }else if(Report_id=="U04")
  			 {
   			document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?b_importsubmit04=link"
            document.reportCycleForm.submit();
   			}
   
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
<form name="reportCycleForm" method="post" action="/report/actuarial_report/fill_cycle">
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
							<bean:write name="reportCycleForm" property="importInfo" filter="false" />
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
								onClick="tijiao('${reportCycleForm.report_id}')">
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
  <table border="1" width="400" cellspacing="0" cellpadding="4"  class="table_style" height="87" align="center">
           <tr>
             <td class="td_style"  height=24>正在处理数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
