
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
<script language="JavaScript" src="/js/validateDate.js"></script>
	<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<script language="javascript">
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
	function onsave(){
	
	var date =trim(document.getElementsByName("adddate")[0].value);
	var year =trim(document.getElementsByName("year")[0].value);
	var type = trim(document.getElementsByName("itemtype")[0].value);
	var name = trim(document.getElementsByName("name")[0].value);
	if(name==""){
	alert("请输入名称");
	return;
	}
	if(date.length>0&&!validate()){
	return;
	}
	if(type==""){
	alert("请选择评估方法");
	return;
	}
	if(year!=""){
	 for(var i=0;i<year.length;i++){
      oneNum=year.substring(i,i+1);
      if (oneNum<"0" || oneNum>"9")
      {
         alert("请输入数字");
         return ;
      }
     }
	}
	var pars="date="+date+"&year="+year+"&type="+type;  
     var request=new Request({method:'post',asynchronous:false,parameters:pars,
                              onSuccess:validatereport2,functionId:'03060000111'});
	}
	function validatereport2(outparamters){
 	var flag=outparamters.getValue("flag");
	 if(flag=="true"){
 	alert("存在填报日期,年度,精算评估方法一致的填报周期");
 	return;
 		}else{
 		alert("保存成功");
	 	reportCycleForm.action = "/report/actuarial_report/fill_cycle.do?b_add2=save";
	 	reportCycleForm.submit();
	 	 }
	}
	function validate()
  {
	   var aa=document.getElementsByName("adddate")[0];
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
	 	 
	 	
   </script>
   <hrms:themes/>
	<body>
		<html:form action="/report/actuarial_report/fill_cycle">
			<br>
			<br>
			<br>
			<table width="60%" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable1">
				<thead>
					<tr>
						<td align="left" class="TableRow" colspan="2">
							<bean:message key="reportcyclelist.cycle" />
						</td>
					</tr>
				</thead>
				<tr class="trShallow">
				
								<td align="right" height="30" width="30%" class="RecordRow" nowrap>
									<bean:message key="reportcyclelist.name" />
								</td>
								<td class="RecordRow" nowrap>
									<html:text name="reportCycleForm" size="14" style="width:200px" styleId="name" property="reportcyclevo.string(name)" maxlength="100"  styleClass="textColorWrite"  />
								</td>
							</tr>
							<tr class="trDeep">
								<td align="right" height="30" class="RecordRow" nowrap>
									<bean:message key="reportcyclelist.adddate" />
								</td>
								<td class="RecordRow" nowrap>
									<input type="text" name="adddate" size="14" style="width:200px"  value='<bean:write name="reportCycleForm" property="reportcyclevo.string(bos_date)" />'  extra="editor"  id="editor4"  
							dropDown="dropDownDate"   />
								</td>
							</tr>
							<tr class="trShallow">
								<td align="right" height="30" class="RecordRow" nowrap>
									<bean:message key="reportcyclelist.year" />
								</td>
								<td class="RecordRow" nowrap>
									<html:text name="reportCycleForm" size="14" style="width:200px" styleId="year" property="reportcyclevo.string(theyear)" maxlength="4"  styleClass="textColorWrite" />
								</td>
							</tr>
							<tr class="trDeep">
								<td align="right" height="30" class="RecordRow" nowrap>
									<bean:message key="reportcyclelist.emethod" />
								</td>
								<td class="RecordRow" nowrap>
								<html:select  styleId="itemtype"  style="width:200px"  name="reportCycleForm" property="reportcyclevo.string(kmethod)"  styleClass="textColorWrite"  >
									<html:option value="">
									</html:option>
									<html:option value="0">
										<bean:message key="reportcyclelist.actuarial.allmethod"/>
									</html:option>
									<html:option value="1">
										<bean:message key="reportcyclelist.actuarial.rollmethod"/>
									</html:option>
									</html:select>
								</td>
							</tr>


			</table>

			<table width="60%" align="center">
				<tr>
					<td align="center">

						<input type="button" name="b_add2"
							value="<bean:message key="lable.menu.main.save"/>"
							class="mybutton" onClick="onsave()">
				<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>

					</td>
				</tr>
			</table>


		</html:form>
	</body>
</html>
