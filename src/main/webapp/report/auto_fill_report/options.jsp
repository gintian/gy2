<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/validateDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<script language="JavaScript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*
function document.oncontextmenu() 
{ 
  	return false; 
} 
*/
</script>
   <link href="../../css/css1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<hrms:themes />
<script language="javascript" src="../../ajax/constant.js"></script>
<script language="javascript" src="../../ajax/basic.js"></script>
<script language="javascript" src="../../ajax/common.js"></script>
<script language="javascript" src="../../ajax/control.js"></script>
<script language="javascript" src="../../ajax/dataset.js"></script>
<script language="javascript" src="../../ajax/editor.js"></script>
<script language="javascript" src="../../ajax/dropdown.js"></script>
<script language="javascript" src="../../ajax/table.js"></script>
<script language="javascript" src="../../ajax/menu.js"></script>
<script language="javascript" src="../../ajax/tree.js"></script>
<script language="javascript" src="../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../ajax/command.js"></script>
<script language="javascript" src="../../ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="../../ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

<script language="javascript">
 /*变量声明部分*/ 
  var start1;//用于判断-号出现的位置
  var i;//用于判断字符串中'-'号的出现位置,定义的循环变量
  var chkyear;//用于截取年
  var chkyearinteger;
  var chkmonths;//用于截取月
  var chkmonthsinteger;
  var chkdays;//用于截取日
  var chkdaysinteger;
  var chk1;//用于按位判断输入的年,月,日是否为整数
  var chk2;
  var mon=new Array(12);/*声明一个日期天数的数组*/
  mon[0]=31;
  mon[1]=28;
  mon[2]=31;
  mon[3]=30;
  mon[4]=31;
  mon[5]=30;
  mon[6]=31;
  mon[7]=31;
  mon[8]=30;
  mon[9]=31;
  mon[10]=30;
  mon[11]=31;
  
  function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
  
  
  //输入的字符串为空时,提示错误
  function check(aa){ 
   if(!document.getElementsByName("startdate")[0].value)
   {
	    alert("起始日期不能为空！");
	    document.getElementsByName("startdate")[0].focus();
	    return false;
   }
   if(!validate(document.getElementsByName("startdate")[0],"起始日期"))
   		return;
   if(aa.value == "")
   {
    alert(REPORT_INFO1+"！");
    aa.focus();
    return false;
   }
   
   document.getElementsByName("startdate")[0].value=replaceAll(document.getElementsByName("startdate")[0].value,'.','-')	
   aa.value=replaceAll(aa.value,'.','-')	
   
   //输入的字符串不为固定格式10个字符时,提示错误
   if(aa.value.length != 10)
   {
    alert(REPORT_INFO2+"！");
    aa.focus();
    return false;
   }
   //输入的字符串为10个字符时,按项判断是否符合规定的格式
   else
   {
    if(aa.value.substring(4,5)!='-')
   {
    alert(REPORT_INFO3);
    aa.focus();
    return false;
   }
   if(aa.value.substring(7,8)!='-')
   {
    alert(REPORT_INFO4);
    aa.focus();
    return false;
   }
    for(i=0;i<6;i++)
    {
     start1=aa.value.substring(i,i+1);
     if(start1=='-' && i!=4)
     {
      alert(REPORT_INFO5);
      aa.focus();
      return false;
     }
     else
     {
      continue;
     }
    }
    for(i=6;i<=9;i++)
    {
     start1=aa.value.substring(i,i+1);
     if(start1=='-'&& i!=7)
     {
      alert(REPORT_INFO5);
      aa.focus();
      return false;
     }
     else
     {
      continue;
     }
    }
    
    
    //按位判断每位是否为整数
    for(i=0;i<=9;i++)
    {
     chk1=aa.value.substring(i,i+1);
     if(chk1=='-')
     {
     continue;
     }
     chk2=parseInt(chk1,10);
     if(!(chk2>=0&&chk2<=9))
     {
      alert(chk1+REPORT_NONUMBER+"！");
      aa.focus();
      return false;
     } 
    }
    //判断年是否符合条件
    chkyear=aa.value.substring(0,4);
    chkyearinteger=parseInt(chkyear,10);
    if(!(chkyearinteger>=1900&&chkyearinteger<=2100))
    {
     alert(REPORT_INFO6+"!");
     aa.focus();
     return false;
    }
    
    //根据年设2月份的日期
    if(chkyearinteger%100==0||chkyearinteger%4==0)
    {
    mon[1]=29;
    }
    else
    {
    mon[1]=28;
    }
    //判断月是否符合条件
    chkmonths=aa.value.substring(5,7);
    chkmonthsinteger=parseInt(chkmonths,10);
    if(!(chkmonthsinteger>=1&&chkmonthsinteger<=12))
    {
     alert(REPORT_INFO7+"!");
     aa.focus();
     return false;
    }
    //判断日期是否符合条件
    chkdays=aa.value.substring(8,10);
    chkdaysinteger=parseInt(chkdays,10);
    switch(chkmonths)
    {
     case "01":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[0]))
        {
         alert("1"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "02":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[1]))
        {
         alert("2"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
    
     case "03":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[2]))
        {
         alert("3"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "04":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[3]))
        {
         alert("4"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "05":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[4]))
        {
         alert("5"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "06":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[5]))
        {
         alert("6"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
    
     case "07":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[6]))
        {
         alert("7"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "08":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[7]))
        {
         alert("8"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     
     case "09":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[8]))
        {
         alert("9"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "10":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[9]))
        {
         alert("10"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "11":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[10]))
        {
         alert("11"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "12":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[11]))
        {
         alert("12"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     }//日期判断结束
     	//【54378】V76报表管理：自动生成/取数范围，起始日期大于截止日期保存时没有提示了
	     if(document.getElementsByName("startdate")[0].value>aa.value)
	     {
	  	    alert(REPORT_INFO75);
	  	    document.getElementsByName("startdate")[0].focus();
	  	  	aa.focus();
	  	    return false;
	     }
	    if (document.reportOptionForm.checkbox.checked){
			document.reportOptionForm.setResult.value="1";
		}else{
			document.reportOptionForm.setResult.value="0";
		}
		 if (document.reportOptionForm.checkboxupdateflag[0].checked){
			document.reportOptionForm.updateflag.value="1";
		}else{
			document.reportOptionForm.updateflag.value="0";
		}
		reportOptionForm.action="/report/auto_fill_report/options.do?b_update=update";
   		reportOptionForm.submit();
   }    
}	
</script>
<body onKeyDown="return pf_ChangeFocus();">
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" >
		<tr>  
			<td valign="top">
				<form name="reportOptionForm" method="post" action="/report/auto_fill_report/options.do">
					<fieldset align="center" style="width:50%;margin:auto;"><legend ><bean:message key="options.title"/></legend>

					<table border="0" cellspacing="0"  align="center" cellpadding="0" >
						<% int i =0; %>
						<hrms:extenditerate id="element" name="reportOptionForm" property="dbNameListForm.list"   indexes="indexes"  pagination="dbNameListForm.pagination" pageCount="120" scope="session">
							<%if(i%2==0){ %>
							<tr>
							<td>

								<logic:equal name="element" property="string(flag)" value="1">
								<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select"  value="false" indexes="indexes"/>&nbsp;
								</logic:equal>  
								<logic:equal name="element" property="string(flag)" value="0">
								<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select" value="true" indexes="indexes"/>&nbsp;
								</logic:equal> 

								<bean:write name="element" property="string(dbname)" filter="false"/>&nbsp;
							</td>		
							<%}else{ %>
							
								<td>

								<logic:equal name="element" property="string(flag)" value="1">
								<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select"  value="false" indexes="indexes"/>&nbsp;
								</logic:equal>  
								<logic:equal name="element" property="string(flag)" value="0">
								<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select" value="true" indexes="indexes"/>&nbsp;
								</logic:equal> 

								<bean:write name="element" property="string(dbname)" filter="false"/>&nbsp;
							  </td>
								</tr>
							<%}i++; %>
						
						</hrms:extenditerate>
					<%if(i%2!=0) {%>
						<td></td>
						</tr>
						<%} %>
						
						<tr height="25">  
							<td >起始日期
							<input  type="text" name="startdate" extra="editor"  id="editor4"  
							dropDown="dropDownDate"  value="<bean:write name='reportOptionForm' property='startdate'/>">
							</td>
						</tr> 
						<tr >  
							<td ><bean:message key="options.enddate"/>
							<input  type="text" name="appDate" extra="editor"  id="editor4"  
							dropDown="dropDownDate"  value="<bean:write name='reportOptionForm' property='appDate'/>">
							</td>
						</tr>  
						<tr >  
							<td>
								<logic:equal name="reportOptionForm" property="result" value="1">
								<input type="checkbox" name="checkbox" checked>&nbsp;
								</logic:equal>  
								<logic:equal name="reportOptionForm" property="result" value="0">
								<input type="checkbox" name="checkbox" >&nbsp;
								</logic:equal> 
								<input type="hidden" name="setResult">
								<bean:message key="options.scanqueryresult"/>
							</td>
						</tr> 
						<tr >  
							<td colspan="2">
								<logic:equal name="reportOptionForm" property="updateflag" value="1">
								<input type="radio" name="checkboxupdateflag" checked >&nbsp;
								<bean:message key="options.condition.getnumber"/>
								</logic:equal>
								<logic:equal name="reportOptionForm" property="updateflag" value="0">
								<input type="radio" name="checkboxupdateflag"  >&nbsp;
								<bean:message key="options.condition.getnumber"/>
								</logic:equal>  
							</td>
							</tr>
							<tr>
							<td colspan="2">	
								<logic:equal name="reportOptionForm" property="updateflag" value="0">
								<input type="radio" name="checkboxupdateflag"  checked>&nbsp;
								<bean:message key="options.all.getnumber"/>
								</logic:equal> 
								<logic:equal name="reportOptionForm" property="updateflag" value="1">
								<input type="radio" name="checkboxupdateflag"  >&nbsp;
								<bean:message key="options.all.getnumber"/>
								</logic:equal> 
								<input type="hidden" name="updateflag">
							</td>
						 </tr>  
				</table>
				</fieldset>
				
				<table align='center' >
				<tr>
						<td align="center" >
							<input type="button" name="b_update" value="<bean:message key='options.save'/>" class="mybutton" onClick="check(reportOptionForm.appDate)">     
							<input type="reset" value="<bean:message key='options.reset'/>" class="mybutton">
							<hrms:tipwizardbutton flag="report" target="il_body" formname="reportOptionForm"/> 
						</td>
				</tr>
				</table>
				
			</form>
		</td>
		</tr>
	</table>
</body>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);

</script>
<script language="javascript">
  initDocument();
</script>
