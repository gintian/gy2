<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
	<script language="javascript" src="/js/constant.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<SCRIPT LANGUAGE=javascript>

	var n=-1;
	var info=parent.infos;
	var a_bean=new Array();
	
	if(info[0]!="null")
	{
		if(info[0].indexOf("#")==-1)
			a_bean[0]=info[0];
		else
			a_bean=info[0].split("#");  //校验结果数组
	
	}
	var a_2=info[1];
	
	
	//初始化屏幕颜色
	function clear_screen_color()
	{
		for(var i=0;i<a_2.length;i++)
		{
			for(var j=0;j<a_2[0].length;j++)
			{
				a_2[i][j].style.background="#FFFFFF";
			}
		} 
	
	}
	
	
	
	//将报表颜色恢复为默认状态
	function clear_color()
	{
			var bean_info_arr=a_bean[n-1].split("@");
			if(bean_info_arr[2]=="null")		//语法错误
			{		
				if(bean_info_arr[6]=="null")   //列校验
				{
					var row_num=bean_info_arr[5].split(",");						
					for(var a=0;a<row_num.length-1;a++)
					{
						var left_num=bean_info_arr[3].split(",");
						var right_num=bean_info_arr[4].split(",");						
						
						for(var b=0;b<left_num.length-1;b++)
						{
							a_2[row_num[a]][left_num[b]].style.background="#FFFFFF";
						}
						for(var b=0;b<right_num.length-1;b++)
						{
							a_2[row_num[a]][right_num[b]].style.background="#FFFFFF";
						}
					}
				}
				else							//行校验
				{
					var col_num=bean_info_arr[6].split(",");						
					for(var a=0;a<col_num.length-1;a++)
					{
						var left_num=bean_info_arr[3].split(",");
						var right_num=bean_info_arr[4].split(",");						
						
						for(var b=0;b<left_num.length-1;b++)
						{
							a_2[left_num[b]][col_num[a]].style.background="#FFFFFF";
						}
						for(var b=0;b<right_num.length-1;b++)
						{
							a_2[right_num[b]][col_num[a]].style.background="#FFFFFF";
						}
					}
				}				
			}
	}
	
	//展现表内校验结果
	function inner_validate()
	{
		n++;			
		if(n>=a_bean.length)
		{
			if(a_bean.length!=0)
			{
				clear_color();
			}
			f1.area.value=VALIDATEFINISHED+'！';
			n=-1;
			
		}
		else
		{	
			if(n>=1)
			{
				clear_color();							
			}
			var bean_info_arr=a_bean[n].split("@");
			if(bean_info_arr[2]!="null")		//语法错误
			{
				f1.area.value=bean_info_arr[0]+"\n"+bean_info_arr[2];
			}
			else
			{
				f1.area.value=bean_info_arr[0]+"\n"+bean_info_arr[1];	
				if(bean_info_arr[6]=="null")   //列校验
				{
					var row_num=bean_info_arr[5].split(",");						
					for(var a=0;a<row_num.length-1;a++)
					{
						var left_num=bean_info_arr[3].split(",");
						var right_num=bean_info_arr[4].split(",");						
						
						for(var b=0;b<left_num.length-1;b++)
						{
							a_2[row_num[a]][left_num[b]].style.background="#FF0000";
						}
						for(var b=0;b<right_num.length-1;b++)
						{
							a_2[row_num[a]][right_num[b]].style.background="#FFFF00";
						
						}
					}
				}
				else							//行校验
				{
					var col_num=bean_info_arr[6].split(",");					
					var left_num=bean_info_arr[3].split(",");
					var right_num=bean_info_arr[4].split(",");		
					for(var a=0;a<col_num.length-1;a++)
					{
						for(var b=0;b<left_num.length-1;b++)
						{
							a_2[left_num[b]][col_num[a]].style.background="#FF0000";
							
						}
						for(var b=0;b<right_num.length-1;b++)
						{
							a_2[right_num[b]][col_num[a]].style.background="#FFFF00";			
						}
					}
				}
				
			}
		}
		
	}

	
	
	function closeWindow()
	{
		var valWin = parent.Ext.getCmp('promptlyValidatWin');
		if(valWin)
			valWin.close();
		else
			window.close();	
	
	}
	
	
	</script>
<HEAD>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
</HEAD>
<style>
.DetailTable{
	width:expression(document.body.clientWidth-10);
}
</style>
<body bgcolor="#F7FAFF"  onload='clear_screen_color()' >
<form name='f1'>
		<table  width="100%" align="center" height="95%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
		        <tr>  
		         <td width="100%" height="100%" align='center' >
						<TEXTAREA   name='area' rows='10' cols='45' style="height:99%;width:100%;">
						</TEXTAREA>
		         </td>
		         </tr>
				<tr>
					<td align="center">
						<INPUT type='button' value=' <bean:message key="edit_report.continue"/> '  class='mybutton' onclick='inner_validate()' style="margin-top: 2px;">				
						<INPUT type='button' value=' <bean:message key="button.cancel"/> ' class='mybutton' onclick='closeWindow()' style="margin-top: 2px;">
					</td>
				</tr>
		 </table>


</form>

</body>
