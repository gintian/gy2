锘�
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<script language="javascript">
/***/
	function MusterInitData()
	{
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
	}
	function submitDATA()
	{
	        
	   var waitInfo=eval("wait");	   
	   waitInfo.style.display="block";
	}
	function submitUpload()
	{
	  var name = document.getElementsByName('file')[0];
	 
	  if(name.value.substring(name.value.length-4,name.value.length)=='.xml')
	  {
	  	  submitDATA();
		  	portalMainForm.action="/system/bos/portal/portalMain.do?b_query=link";
		  portalMainForm.target="il_body";
	      portalMainForm.method="post";
	      portalMainForm.enctype="multipart/form-data";
	      portalMainForm.submit();
      }else
      {
      	alert("璇烽�夋嫨xml鏍煎紡鏂囦欢杩涜涓婁紶");
      }    
	}	
</script>
<html>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes> 
  <body>
  <div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height=24>姝ｅ湪鎺ユ敹鏁版嵁璇风◢鍊�....</td>
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
    <table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top:-7px;">
			<tr>
				<td valign="top">
					<form name="portalMainForm" method="post" action="/system/bos/portal/portalMain.do" enctype="multipart/form-data">
						<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr height="20">
								<!--  <td width="10" valign="top" class="tableft"></td>
								<td width="130" align=center class="tabcenter">

			                                            鎺ユ敹鏂囦欢									
								
								</td>
								<td width="10" valign="top" class="tabright"></td>
								<td valign="top" class="tabremain" width="500"></td>-->
								<td align="left" class="TableRow">

			                                            鎺ユ敹鏂囦欢									
								
								</td>
							</tr>
							<tr>
								<td class="framestyle9">

									<table border="0" cellpmoding="0" cellspacing="5" class="DetailTable" cellpadding="0" align="center">

										<tr>
											<td height="10">
												<input id="file" name="file" type="file" size="50" class="text4">
											
											</td>
										</tr>
									</table>
								</td>
							</tr>
							<br>

							<tr class="list3">
								<td align="center" style="height:35px;">
									<input type="button" name="b_query" value="纭畾" onclick="submitUpload();" class="mybutton">
								</td>
							</tr>
						</table>
					</form>
				</td>
			</tr>
		</table>

  </body>
</html>

<script language="javascript">
 MusterInitData();	
</script>