<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<hrms:themes />

<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<title></title>
<script type="text/javascript">
	function sureff(){
		var check=document.getElementsByName("ys")[0];
		var info=new Array();
		if(check.checked==true){
			
			info[1]="1";
			info[0]='ok';
		}else{
			info[1]="2";
			info[0]='ok';
		}
		window.returnValue=info;
		window.close();
	}
</script>
</head>
<body>
<table  width="100%"  border="0" align='center'>
	<tr>

	<td width="100%" align='center'>
	<table  width="100%"  class='ListTable' border="0" align='center'>
		<tr>
			<td class="TableRow" align="left" width="100%" >
			&nbsp;&nbsp;删除组织单元考核指标
			</td>
		</tr>
		<tr>
			<td width="100%" class="RecordRow" align='center' style='padding-bottom:20px;'>
				<fieldset style="width:90%;margin-bottom:20px;">
					<legend>
						提示信息
					</legend>
					<table width="100%">
						<tr>
							<td align='left'>
								&nbsp;&nbsp;&nbsp;&nbsp;您真的希望删除当前选中组织单元考核指标吗？
							</td>
						</tr>
						<tr>
							<td align='left'>
								&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' value='1' name='ys'>包含下级组织单元的考核指标
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
	</td>
	</tr>
	<tr>
	<td colspan='3' align='center'>
		<input type="button" class="mybutton" onclick="sureff();" value="确定"><input type="button" class="mybutton" onclick="window.close();" value="取消">
	</td>
	</tr>
</table>
</body>
</html>