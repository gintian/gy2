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
		window.location.href="/workbench/info/showinfodata.do?b_batchinout=link";
	}
    function imports()
    {
    	 var fileEx = selfInfoForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }
       
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='xls' || temp.toLowerCase()=='xlsx')
    	{
    		selfInfoForm.target="nil_body";
    		document.selfInfoForm.action="/workbench/info/showinfodata1.do?b_importdata=link";
  			document.selfInfoForm.submit();
  			/* zgd 2014-7-15 批量导入添加进度条*/
	    	var x=document.body.clientWidth/2-300;
		    var y=document.body.clientHeight/2-125;
			var waitInfo=eval("wait");
			waitInfo.style.top=y;
			waitInfo.style.left=x;
			waitInfo.style.display="block";
			//【5194】员工管理：批量导入，选择导入文件，多次点击导入按钮，提示信息不对。 jingq add 2014.11.20
			document.getElementsByName("b_update")[0].disabled = true;
    	}
    	else
    	{
    		alert("导入的文件必须为excel格式");
    	}
    }

  </script>
	<hrms:themes />
	<body>
 <div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
      <tr>

        <td class="td_style" height="24">正在导入,请稍候...</td>

      </tr>
      <tr>
        <td style="font-size:12px;line-height:200%" align=center>
          <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
		<form name="selfInfoForm" method="post"
			action="/workbench/info/showinfodata.do"
			enctype="multipart/form-data">
			<table border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:10px;">
				<tr>
					<td>
						<fieldset>
							<legend>
								选择导入文件
							</legend>
							<table border="0" cellspacing="0" align="center" cellpadding="0">
								<tr>
									<td width="400" align="center">
										<Br>
										文件
										<input type="file" name="file" size="40" class="text6">
										<br>
										<br>
									</td>
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td height="5px">
					</td>
				</tr>
				<tr>
					<td align="left">
						<font-size=2>
						提示：请用下载的Excel模板来导入数据！模板格式不允许修改！
						</font-size>
					</td>
				</tr>
			</table>
			<table border="0" cellspacing="0" align="center" cellpadding="0"
				style="width: 50%;">
				<tr>
					<td height="5px">
					</td>
				</tr>
				<tr>
					<td align="center">
						<input type="button" name="b_update"
							value="<bean:message key='menu.gz.import'/>" class="mybutton"
							onClick="imports()">
						<input type="button" name="b_update"
							value="<bean:message key='button.return'/>" class="mybutton"
							onClick="goback();">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
<script>
if(!getBrowseVersion()){//兼容非IE浏览器样式 修改   wangb  20180206  bug 34447
	var file = document.getElementsByClassName('text6')[0]; //下载文件框 高度调整
	file.style.height = '26px';
}
</script>
