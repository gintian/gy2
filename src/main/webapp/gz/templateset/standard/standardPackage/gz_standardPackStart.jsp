<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>
  
  </head>
  <script languge='javascript'>
  var desc=dialogArguments;
  var today=new Date();
  var month=today.getMonth()+1;
  if(month<10)
  	month="0"+month;
  var date=today.getDate();
  if(date<10)
  	date="0"+date;
  var today_str=today.getYear()+"-"+month+"-"+date;
 
  
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
  
  
  function sub()
  {
	    var aa=document.f1.startDate;
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
  		  returnValue=document.f1.startDate.value;
		  window.close();	
  }
  
  </script>
  <body>
  <form name='f1' >
    <br>
    <TABLE width='100%' ><tr> <td>
    <script languge='javascript'> document.write("&nbsp;&nbsp;"+GZ_TEMPLATESET_ENTERSTART+"\""+desc+"\"？");  </script>
    <br><br>
    &nbsp;&nbsp;<bean:message key="label.gz.startDate"/>:(<bean:message key="kjg.title.dategeshi"/>：yyyy-MM-dd)&nbsp;&nbsp;&nbsp;&nbsp;
    <input  type="text" name="startDate"  value="" class="inputtext">
  	</td>
  	<td align='right' > 
  		<input type='button' value=' <bean:message key="reporttypelist.confirm"/> ' onclick='sub()' class="mybutton" >
  		<br><br>
  		<input type='button' value=' <bean:message key="kq.register.kqduration.cancel"/> ' onclick='window.close();' class="mybutton" >
  	</td>
  	</tr></TABLE>
  
  </form>
  </body>
  <script language='javascript'>
  	document.f1.startDate.value=today_str;
  </script>
</html>
