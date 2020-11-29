<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,
				java.util.*,
				com.hrms.frame.utility.AdminCode,
				com.hjsj.hrms.actionform.gz.gz_accounting.voucher.VoucherForm,
				org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	String type=request.getParameter("type"); ////1:财务凭证  2:按月汇总
	String _code=request.getParameter("_code");
	
	String depcode_value="";
	String depcode_desc="";
	if(_code!=null&&_code.trim().length()>0&&!"null".equalsIgnoreCase(_code))
	{
		 depcode_desc=AdminCode.getCodeName(_code.substring(0,2),_code.substring(2));
		 depcode_value=_code.substring(2);
	}
	Calendar d=Calendar.getInstance();
	int year=d.get(Calendar.YEAR);
	int month=d.get(Calendar.MONTH)+1;
	int day=d.get(Calendar.DATE);

	String amonth=month+"";
	String aday=day+"";
	if(month<10)
		amonth="0"+amonth;	
	if(day<10)
		aday="0"+aday;	
	String time=year+"-"+amonth+"-"+aday;
 %>


<html>
<head>
 
</head>
  


 



<script language='javascript' >



/**obj中的值+1*/
function incyear(){
	 
	var obj=document.getElementsByName("theyear")[0]; 
	var value=obj.value*1;
	value = value+1;
	obj.value = value;
}
/**obj中的值减-1*/
function decyear(){
		var obj=document.getElementsByName("theyear")[0];
	    var value=obj.value*1;
		value = value-1;
		if(value<=1990)
			value=1990;
		obj.value = value;
}

/**obj中的值+1*/
function incmonth(){
	var obj=document.getElementsByName("themonth")[0];
	var value=obj.value;
	if(value.substring(0,1)=="0")
	   value=value.substring(1,value.length);
   	value=value*1;     	   
	value = value+1;
	if(value>12)
	  value=12;
	obj.value = value;
}
/**obj中的值减-1*/
function decmonth(){
		var obj=document.getElementsByName("themonth")[0];
	    var value=obj.value;
		if(value.substring(0,1)=="0")
	   	   value=value.substring(1,value.length);	
	   	value=value*1;         
		value = value-1;
		if(value<=0)
			value=1;
		obj.value = value;
}




function closeWindow()
{
	window.close();
}

function isDigit(s)   
{   
		var patrn=/^[0-9]{1,20}$/;   
		if (!patrn.exec(s)) return false  
			return true  
}  

function confirm(type) 
{
	var year=document.getElementsByName("theyear")[0].value;
	var month=document.getElementsByName("themonth")[0].value;
	var count="0";
	if(type=='1')
	   count=document.getElementsByName("count")[0].value;
 	var voucher_date=document.getElementsByName("voucher_date")[0].value;
 	var deptcode=document.getElementsByName("deptcode.value")[0].value;
	var flag="0";
	if(type=='1'){
		if(document.getElementsByName("org1")[0].checked&&document.getElementsByName("org2")[0].checked)
				flag=3;
		else if(document.getElementsByName("org1")[0].checked)
				flag=1;
		else if(document.getElementsByName("org2")[0].checked)
				flag=2;
		
		if(flag=="0")
		{
			alert("需选择复选框内容!");
			return;
		}		
	}
			
	if(year==''||month=='')
	{
		alert("请填写计提月份!");
		return;
	}
	else
	{
		if(!isDigit(year)||!isDigit(month))
		{
			alert("计提月份格式不正确！");
			return;
		}
		if(year*1<=1900||year*1>=2100)
		{
			alert("年要大于1900,小于2100!");
			return;
		}
		if(month*1<1||month*1>12)
		{
			alert("月要大于等于1,小于等于12!");
			return;
		} 
	}
	
	if(voucher_date=='')
	{
		alert("请填写凭证日期!");
		return;
	}
	else
	{
		if(!checkDateTime(voucher_date)) 
		{
				    		alert("凭证日期输入不正确,请输入有效的日期且格式为yyyy-mm-dd！");
				    		return;
		}
		
	}
	if(deptcode=='||'||deptcode=='')
	{
		alert("请填写单位部门!");
		return;
	}
	
	var retvo=new Array();
	retvo[0]=year;
	retvo[1]=month;
	retvo[2]=count;
	retvo[3]=voucher_date;
	retvo[4]=deptcode;
	retvo[5]=flag;
	window.returnValue=retvo;
	window.close();
}


function selectobject()
{
	var return_vo=select_org_dialog2(0,2,1,1,0,1,"1",1);
 	if(return_vo)
	{
	 
		document.getElementsByName("deptcode.viewvalue")[0].value = return_vo.title;
	 	document.getElementsByName("deptcode.value")[0].value = return_vo.content.substring(2);
	 	  		
	}
}


function resize(){//add by xiegh on 20170914 ie8,9无法去除滚动条，因为里面的iframe宽度100%  但是父界面是共用的 故如此修改
	parent.document.getElementById("childFrame").height="450px";
}
</script>
<body onload="resize();">

<html:form action="/gz/gz_accounting/voucher/financial_voucher">


<fieldset align="center" style="width:435px;height:230px;">
    							 <legend >请选择范围</legend>


<table    align='center' width='435px;'>

<tr height='35' ><td align='right' >计提月份</td><td>
	<table><tr>
			 <td valign="middle">&nbsp;
	          	<input type='text' name='theyear' value='<%=year%>'  style="width:50" class="inputtext"/>                     
	          <td valign="middle">                      
	          </td>
	          <td valign="middle" align="left">
	             <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='incyear()'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='decyear()'>6</button></td></tr>
	             </table>
	          </td>
			  <td><bean:message key="hmuster.label.year"/></td>	          
	   				  <td valign="middle"> 
	       	  			 
	       	  				<input type='text' name='themonth' value='<%=month%>' style="width:50" class="inputtext"/>                             
	         		 </td>
	          		 <td valign="middle" align="left">
	             		<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='incmonth()'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='decmonth()'>6</button></td></tr>
	             		</table>
	                 </td>	
	           <td><bean:message key="hmuster.label.month"/></td>	   
	</tr></table>
</td></tr>
<tr  <%=(type.equals("2")?"style='display:none'":"")%>  height='35' ><td align='right' >发放次数</td><td style="padding-left: 4px;">&nbsp;
	<select name='count' >
		<option value='all'>全部</option>
		<option value='1'>1次</option>
	    <option value='2'>2次</option>
	    <option value='3'>3次</option>
	    <option value='4'>4次</option>
	    <option value='5'>5次</option>
	    <option value='6'>6次</option>
	    <option value='7'>7次</option>
	    <option value='8'>8次</option>
	    <option value='9'>9次</option>
	    <option value='10'>10次</option>
	</select>
</td></tr>
<tr height='35' ><td align='right' >凭证日期</td><td style="padding-left: 4px;">&nbsp;
<!-- 
<input type='text'  size='15'  name='voucher_date'  value=''   onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'   /> 
 -->
<input id="sscop" value='<%=time%>' type="text"  size="12" name="voucher_date" extra="editor" id="editor2"  dropDown="dropDownDate"/>

</td></tr>
<tr  ><td align='right'  valign='bottom' ><table><tr><td >单位部门</td></tr></table></td><td  valign='top' > 

<table   ><tr><td valign='top' >&nbsp;
 <input type='text' size='30' name='deptcode.viewvalue' value='<%=depcode_desc%>' readOnly  class="inputtext"/> 
 </td><td valign='bottom' >
 <a href='javascript:selectobject();'  ><span style="padding-top: 3px;">  <img  src='/images/code.gif' border=0 width=18 height=18 /></span> </a> 
<input type='hidden' size='5' name='deptcode.value' value='<%=depcode_value%>' readOnly />
 </td></tr></table>

</td></tr>
<%if(type.equals("1")){ %>
<tr  height='15' align='center' ><td colspan='2'> &nbsp; 
		<input type='checkbox' name='org1' value='1' checked />当前选中机构&nbsp;&nbsp;<input type='checkbox' name='org2' value='1'  /> 下一级机构
	
</td></tr>
<%} %>
<tr  height='15' align='center' ><td colspan='2'> &nbsp; </td></tr>
</table>

   </fieldset>
<table align='center' ><tr><td>
<input type='button' class="mybutton" value="确定"  onclick='confirm("<%=type%>")'  />
<input type='button' class="mybutton" value="取消"  onclick='closeWindow()'  /> 
</td></tr></table>


</html:form>
</body>


<script language="javascript"> 
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   


</script>


</html>