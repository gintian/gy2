<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>
   
  </head>
  <style>
	.self_td 
	{ 
	    BORDER-TOP: #94B6E6 1pt solid ; 	
	}
  </style>
  
  
  <script language="JavaScript" src="../../js/validateDate.js"></script>
  <script language='javascript'>
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
  
  function isDigit(s)   
  {   
		var patrn=/^[0-9]{1,20}$/;   
		if (!patrn.exec(s)) return false  
			return true  
  }  
  
  function validate()
  {
	   var aa=document.gzStandardPackageForm.startDate;
	   if(aa.value == "")
	   {
	    alert(GZ_ACCOUNTING_IFNO4+"！");
	    aa.focus();
	    return false;
	   }
	   
		var temps;
		var flag=false;
		if(aa.value.indexOf(".")!=-1)
		{
			temps=aa.value.split(".");
			flag=true;
		}
		if(aa.value.indexOf("-")!=-1)
		{
			temps=aa.value.split("-");
			flag=true;
		}
		if(flag==false)
		{
			 alert(REPORT_INFO14+"！");
			 aa.focus();
			 return false;
		}
		
		if(temps.length!=3)
		{
			alert(REPORT_INFO14+"！");
			aa.focus();
			return false;
		}
		
		for(var i=0;i<temps.length;i++)
		{
			if(!isDigit(temps[i]))
			{
				alert(REPORT_INFO14+"！");
				aa.focus();
				return false;
			}
		}
		if(!(temps[0]>=1900&&temps[0]<=2100))
		{
		     alert(REPORT_INFO6+"!");
		     aa.focus();
		     return false;
	     	}
		chkyearinteger=parseInt(temps[0],10);
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
		    chkmonths=temps[1];
		    chkmonthsinteger=parseInt(temps[1],10);
		    if(!(chkmonthsinteger>=1&&chkmonthsinteger<=12))
		    {
		     alert(REPORT_INFO7+"!");
		     aa.focus();
		     return false;
		    }
	    //判断日期是否符合条件
		    chkdays=temps[2];
		    chkdaysinteger=parseInt(chkdays,10);
		   
		    switch(chkmonthsinteger)
		    {
		     case 1:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[0]))
		        {
		         alert("1"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 2:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[1]))
		        {
		         alert("2"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case 3:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[2]))
		        {
		         alert("3"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 4:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[3]))
		        {
		         alert("4"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 5:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[4]))
		        {
		         alert("5"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 6:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[5]))
		        {
		         alert("6"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case 7:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[6]))
		        {
		         alert("7"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 8:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[7]))
		        {
		         alert("8"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     
		     case 9:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[8]))
		        {
		         alert("9"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 10:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[9]))
		        {
		         alert("10"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 11:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[10]))
		        {
		         alert("11"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 12:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[11]))
		        {
		         alert("12"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     }//日期判断结束


  		  return true;
  }
  
  	function cancel()
  	{
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_query=link";
  		document.gzStandardPackageForm.submit();
  	}
  
  	function  next()
  	{
  		if(trim(document.gzStandardPackageForm.packName.value).length==0)
  		{
  			alert(GZ_TEMPLATESET_INFO1+"！");
  			return;
  		}
  	
  		if(document.gzStandardPackageForm.startDate.value.length==0)
  		{
  			alert(GZ_TEMPLATESET_FILLSTARTDATE+"！");
  			return;
  		}
  		if(document.gzStandardPackageForm.startDate.value.length>0&&!validate())
  			return;
  		
  		var a=document.getElementById("a");
		var b=document.getElementById("b");
  		a.style.display="none";
  		b.style.display="block";
  		
  	}
  
  
  	function  up()
  	{
  		var a=document.getElementById("a");
		var b=document.getElementById("b");
  		b.style.display="none";
  		a.style.display="block";
  	}
  	
  	
  	function sub()
  	{
  		for(var i=0;i<document.gzStandardPackageForm.newStandards.options.length;i++)
  		{
  			document.gzStandardPackageForm.newStandards.options[i].selected=true;
  		}
  		if(document.gzStandardPackageForm.newStandards.options.length==0)
  		{
  			document.gzStandardPackageForm.newStandards.options[0]=new Option("","#");
  			document.gzStandardPackageForm.newStandards.options[0].selected=true;
  		}
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_savePack=save";
  		document.gzStandardPackageForm.submit();
  	}
  	
  
  </script>
<style type="text/css">
.RecordRow_top {
	BACKGROUND-COLOR: #FFFFFF;
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
}
</style>
  <body>
  <html:form action="/gz/templateset/standard/standardPackage">
  
  <div id='a'>

	  <table width="303" height="143" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:60px;">
	  <thead>
	  <tr>
	    <td height="20" colspan="2" class="TableRow" >&nbsp;<bean:message key="label.gz.salaryhistoryname"/></td>
	  </tr>
	  </thead>
	  <tr>
	    <td width="86" class="RecordRow" ><div align=""left"">&nbsp;<bean:message key="kh.field.fieldname"/></div></td>
	    <td width="201" class="RecordRow" >&nbsp;<input type="text" name="packName" size='20' style='width:160' class='complex_border_color' /></td>
	  </tr> 
	  <tr>
	    <td class="RecordRow" > <div align="left">&nbsp;<bean:message key="label.gz.startDate"/></div></td>
	    <td class="RecordRow" >
	  
				&nbsp;<input type="text" name="startDate"  extra="editor"  id="editor4"   style='width:160'
							dropDown="dropDownDate"    size='22' value="" class='complex_border_color' />			
	    </td>
	  </tr>
	  <tr>
	    <td class="RecordRow"  height="26">&nbsp;</td>
	    <td class="RecordRow" >&nbsp;<input type="checkbox" name="isStart" value="1"  />
	      <bean:message key="label.gz.startNewHistory"/></td>
	  </tr>
	  <tr>
	    <td height="35"  class="RecordRow" colspan="2">
		    <div align="center" style="padding-top: 2px;">
		      <input name="Button" type="button" class="mybutton" value="<bean:message key="gz.bankdisk.nextstep"/>" onclick="next()"/>&nbsp;
		      <input name="Button" type="button" class="mybutton" value="<bean:message key="reportcheck.return"/>" onclick="cancel()"/>
		    </div>
	    </td>
	  </tr>
	</table>
  </div>
  
  <div id='b' style='display:none'>
  	  <Br><br>
	  <table width="800" border="0" cellspacing="0" cellpadding="0" align="center">
	  <tr>
	    <td height="20" colspan="3" class="TableRow"><bean:message key="label.gz.copyGzStandard"/></td>
	  </tr>
	  <tr>
	  <td class="RecordRow_top common_border_color" align="center" colspan="3">
	  <table><tr>
	    <td width="350">&nbsp;&nbsp;<bean:message key="label.gz.currentStandard"/></td>
	    <td width="41"><div align="left"></div></td>
	    <td width="350">&nbsp;&nbsp;<bean:message key="label.gz.importStandard"/></td>
	   </tr>
	   </table>
	   </td>
	  </tr>
	  <tr>
	  <td class="RecordRow_top common_border_color" align="center" colspan="3">
	  <table><tr>
	    <td>
	    <select name="left_fields" size="15" style='width:339'  multiple ondblclick="additem('left_fields','newStandards');; removeitem('left_fields');"   >
	    </select>    </td>
	    <td><div align="center">
	      <input name="Button" type="button" class="mybutton" style="margin-bottom: 20px;" value=" &gt;&gt; "  onclick="additem('left_fields','newStandards');; removeitem('left_fields');" /><Br>
	      <input name="Submit2" type="button" class="mybutton" value=" &lt;&lt; " onclick="additem('newStandards','left_fields'); removeitem('newStandards');" />
	    </div></td>
	    <td>
	    <select name="newStandards" size="15"  style='width:330' multiple  ondblclick="additem('newStandards','left_fields'); removeitem('newStandards');"  >
	      <logic:iterate id="element" name="gzStandardPackageForm" property="currentStandardList"   >
	      	<option value='<bean:write name="element" property="id" filter="true"/>' ><bean:write name="element" property="name" filter="true"/></option>
	      </logic:iterate>

	    </select></td>
	    </tr>
	    </table>
	    </td>
	  </tr>
	  
	  <tr>
	    <td colspan="3" class="RecordRow" align="center" style="padding-top:3px;padding-bottom:3px;">
	      <input name="Button" type="button" class="mybutton" value="<bean:message key="static.back"/>" onclick="up()" />
	      <input name="Submit3" type="button" class="mybutton" value="<bean:message key="kq.add_feast.save"/>" onclick='sub()' />
	    </td>
	  </tr>
	</table>
  </div>
  
    </html:form>
  </body>
</html>
