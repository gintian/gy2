<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<script language="javascript">
/***/
	function MusterInitData()
	{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
	   document.getElementById("ly").style.display="none";
	}
	function submitDATA()
	{
	   var waitInfo=eval("wait");	   
	   waitInfo.style.display="block";
	   document.getElementById("ly").style.display="";
	}
	function submitUpload(basetype)
	{
	  var name = document.getElementsByName('file')[0];
	  if(!validateUploadFilePath(name.value))
      {
			alert("请选择正确的文件！");
			return;
      }
	  if(name.value.substring(name.value.length-4,name.value.length)=='.zip')
	  {
	  	  submitDATA();
		  if(basetype=="4")
		    receiveReportForm.action="/selfservice/lawbase/lawbase_upload.do?b_query2=link";
		  else if(basetype=="1")
		     receiveReportForm.action="/selfservice/lawbase/lawbase_upload.do?b_query=link";
		  else if(basetype=="5")
		  	receiveReportForm.action="/selfservice/lawbase/lawbase_upload.do?b_query3=link";
		  receiveReportForm.target="il_body";
	      receiveReportForm.method="post";
	      receiveReportForm.enctype="multipart/form-data";
	      receiveReportForm.submit();
      }else
      {
      	alert("请选择zip格式文件进行上传");
      }    
	}	
</script>
<html>
<link href="/css/css1.css" rel="stylesheet" type="text/css"> 
<hrms:themes cssName="content.css"></hrms:themes>
  <body>
  
    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top:60px;">
			<tr>
				<td valign="top">
					<form name="receiveReportForm" method="post" action="/selfservice/lawbase/lawbase_upload.do" enctype="multipart/form-data">
						<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr height="20">
								<!-- td width="10" valign="top" class="tableft"></td>
								<td width="130" align=center class="tabcenter">

			                                            接收文件									
								
								</td>
								<td width="10" valign="top" class="tabright"></td>
								<td valign="top" class="tabremain" width="500"></td> -->
								<td align="left" colspan="4" class="TableRow">&nbsp;接收文件</td>
							</tr>
							<tr>
								<td colspan="4" class="framestyle3">

									<br>
									<table border="0" cellpmoding="0" cellspacing="5" class="DetailTable" cellpadding="0" align="center">

										<tr>
											<td height="50">
												<input id="file" name="file" type="file" size="50" class="text6">
												<input name="base_id" type="hidden" value="<%=request.getParameter("base_id")%>">
												<input name="basetype" type="hidden" value="<%=request.getParameter("basetype")%>">
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<tr class="list3">
								<td colspan="4" height="35px;" align="center">
									<input type="button" name="b_query" value="确定" onclick="submitUpload('<%=request.getParameter("basetype")%>');" class="mybutton">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>
		</table>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div>
  <div id='wait' style='position:absolute;top:200px;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">

           <tr>
             <td class="td_style" height=24>正在接收数据请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;BORDER: #C4D8EE 1pt solid;" align=center>
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
  </body>
</html>

<script language="javascript">
 MusterInitData();	
</script>