<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<script language="JavaScript">
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
  function validate(aa,bb)
  {
	   if(aa.value == "")
	   {
	    alert(bb+":"+GZ_ACCOUNTING_IFNO4+"！");
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
			 alert(bb+":"+REPORT_INFO14+"！");
			 aa.focus();
			 return false;
		}
		
		if(temps.length!=3)
		{
			alert(bb+":"+REPORT_INFO14+"！");
			aa.focus();
			return false;
		}
		
		for(var i=0;i<temps.length;i++)
		{
			if(!isDigit(temps[i]))
			{
				alert(bb+":"+REPORT_INFO14+"！");
				aa.focus();
				return false;
			}
		}
		if(!(temps[0]>=1900&&temps[0]<=2100))
		{
		     alert(bb+":"+REPORT_INFO6+"!");
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
		     alert(bb+":"+REPORT_INFO7+"!");
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
		         alert(bb+":1"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 2:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[1]))
		        {
		         alert(bb+":2"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case 3:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[2]))
		        {
		         alert(bb+":3"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 4:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[3]))
		        {
		         alert(bb+":4"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 5:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[4]))
		        {
		         alert(bb+":5"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 6:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[5]))
		        {
		         alert(bb+":6"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		    
		     case 7:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[6]))
		        {
		         alert(bb+":7"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 8:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[7]))
		        {
		         alert(bb+":8"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     
		     case 9:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[8]))
		        {
		         alert(bb+":9"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 10:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[9]))
		        {
		         alert(bb+":10"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 11:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[10]))
		        {
		         alert(bb+":11"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     case 12:if(!(chkdaysinteger>0&&chkdaysinteger<=mon[11]))
		        {
		         alert(bb+":12"+REPORT_INFO8+"!");
		         aa.focus();
		         return false;
		        }
		        break;
		     }//日期判断结束


  		  return true;
  }
function query(Report_id,id,unitcode,flag)
{
  
   if(Report_id=="U01")
   {
      editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU01.do?b_query=link&from_model=edit&opt=1&unitcode="+unitcode+"&id="+id+"&flag="+flag;
      editReport_actuaialForm.submit();
   }else if(Report_id.indexOf("U02")!=-1)
   {
      editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt=1&from_model=edit&unitcode="+unitcode+"&id="+id+"&flag="+flag+"&report_id="+Report_id;
      editReport_actuaialForm.submit();
   }else if(Report_id=="U03")
   {
   		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_initReport3=query&from_model=edit&opt=1&id="+id+"&unitcode="+unitcode;
   		document.editReport_actuaialForm.submit();
   }else if(Report_id=="U04")
   {
   		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_initReport4=query&opt=1&id="+id+"&unitcode="+unitcode;
   		document.editReport_actuaialForm.submit();
   }else if(Report_id=="U05")
   {
   		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_queryReport5=query&from_model=edit&opt=1&id="+id+"&unitcode="+unitcode;
   		document.editReport_actuaialForm.submit();
   }
}

function sub()
{
			var hashvo=new ParameterSet();
		var isfillpara = trim('${editReport_actuaialForm.isfillpara}');
		if(isfillpara!=null&&isfillpara!=""){
  		var ispara=isfillpara.split(",");
  			 <logic:equal  name="editReport_actuaialForm"  property="u02_3flag" value="1">
		if(confirm("表2-3 内退人员处于未填状态，是否继续？")){
		}else{
		return;
		}
		
	 </logic:equal>
  		for(var i=0;i<ispara.length;i++){
  		if(trim(document.getElementsByName(ispara[i])[0].value)==""){
  		alert(ispara[i]+"不能为空!");
  		return;
  		}
  		}
  		}
  		var isfillpara2 = trim('${editReport_actuaialForm.isfillpara2}');
  			if(isfillpara2!=null&&isfillpara2!=""){
  			var ispara2=isfillpara2.split(",");
  		for(var i=0;i<ispara2.length;i++){
  		if(trim(document.getElementsByName(ispara2[i])[0].value)==""){
  		alert(ispara2[i]+"不能为空!");
  		return;
  		}
  		if(!validate(document.getElementsByName(ispara2[i])[0],ispara2[i])){
		return;
		}
  		}
  		}
		var paracopy = trim('${editReport_actuaialForm.paracopy}');
		if(paracopy!=null&&paracopy!=""){
  		var para=paracopy.split(",");
  		for(var i=0;i<para.length;i++){
  		
  		hashvo.setValue(para[i],trim(document.getElementsByName(para[i])[0].value));
  		}
  		}
  		var paracopy2 = trim('${editReport_actuaialForm.paracopy2}');
  		if(paracopy2!=null&&paracopy2!=""){
  		var para2=paracopy2.split(",");
  		for(var i=0;i<para2.length;i++){
  		
		hashvo.setValue(para2[i],trim(document.getElementsByName(para2[i])[0].value));
  		}
  		}
		document.editReport_actuaialForm.button_goback.disabled="true";
		var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
		hashvo.setValue("paracopy",paracopy);
		hashvo.setValue("paracopy2",paracopy2);
		hashvo.setValue("id","${editReport_actuaialForm.id}");
		hashvo.setValue("unitcode","${editReport_actuaialForm.unitcode}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:subSuccess,onFailure:failure,functionId:'03060000227'},hashvo);

}
function description(report_id,unitcode,cycle_id)
{	  
	  		var info='';
			var thecodeurl="/report/report_collect/reportOrgCollecttree.do?b_lookDesc=description`bopt=3`report_id="+report_id+"`unitcode="+unitcode+"`cycle_id="+cycle_id;	
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
			var win= window.showModalDialog(iframe_url,info, 
		        "dialogWidth:430px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");

}
function subSuccess(outparamters)
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
	document.editReport_actuaialForm.button_goback.disabled="";
	var url=document.location.href;
	document.location=url;
}
function failure(outparamters)
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
	document.editReport_actuaialForm.button_goback.disabled="";
	//var url=document.location.href;
	//document.location=url;
}
function MusterInitData()
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
}

</script>
<hrms:themes />
<html:form action="/report/actuarial_report/edit_report/editreportlist">
<html:hidden styleId="paracopy" name="editReport_actuaialForm"
				property="paracopy" />
				<html:hidden styleId="paracopy2" name="editReport_actuaialForm"
				property="paracopy2" />
				<html:hidden styleId="isfillpara" name="editReport_actuaialForm"
				property="isfillpara" />
				<html:hidden styleId="isfillpara2" name="editReport_actuaialForm"
				property="isfillpara2" />
	<br>
	<logic:equal  name="editReport_actuaialForm"  property="isCollectUnit" value="1">
	<div align='center' ><Br><br>	您负责的填报单位是汇总单位，请到报表汇总模块处理相关数据！</div>
	</logic:equal>
	<logic:notEqual  name="editReport_actuaialForm"  property="isCollectUnit" value="1">
	<logic:notEqual name="editReport_actuaialForm"  property="cancelunit" value="0">
	<div align='center' ><Br><br>	您负责的填报单位当前周期不存在！</div>
	</logic:notEqual>
	</logic:notEqual>
	<logic:equal name="editReport_actuaialForm"  property="cancelunit" value="0">
	
	
	<logic:equal  name="editReport_actuaialForm"  property="isCollectUnit" value="0">
	
  <table width="100%" border="0" cellspacing="0" style="margin-top: -17px;" align="center" cellpadding="0" class="ListTable">
    <tr>         	 
      <td align="center" class="TableRow" width="70%" nowrap>报表名称</td>	
      <td align="center" class="TableRow" width="20%" nowrap>状态</td>     	   	  
      <td align="center" class="TableRow" width="10%" nowrap>操作</td>		 
    </tr>
    <%
int i=0;
String flag="";
%>
    <hrms:extenditerate id="element" name="editReport_actuaialForm" property="editreportForm.list" indexes="indexes"  pagination="editreportForm.pagination" pageCount="30" scope="session">
     <logic:notEqual name="editReport_actuaialForm"  property="kmethod" value="0">
	   		 <logic:notEqual name="element" property="Report_id"  value="U02_1">
	   		  <logic:notEqual name="element" property="Report_id"  value="U02_2">
	   		   <logic:notEqual name="element" property="Report_id"  value="U02_4">
     <%
       LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
      if(i%2==0)
      {
     %>
     <tr class="trShallow">
     <%}
     else
     {%>
     <tr class="trDeep">
     <%
     }
     i++;          
     %> 
      <td align="left" class="RecordRow"  nowrap>   &nbsp;&nbsp;  
        <%
          out.print(abean.get("report_name"));
         %>
      </td>
      <td align="left" class="RecordRow" nowrap>  &nbsp;&nbsp; 
        <%if(abean.get("flag")!=null&&((String)abean.get("flag")).equals("-1"))
        {
           out.print("未填");
        }else if(((String)abean.get("flag")).equals("0"))
        {
           out.print("正在编辑");
        }else if(((String)abean.get("flag")).equals("1"))
        {
           out.print("已上报");
           flag=flag+"1";
        }else if(((String)abean.get("flag")).equals("2"))
        {
           out.print("<a href='###' onclick=\"description('"+abean.get("Report_id")+"','"+abean.get("unitcode")+"','"+abean.get("id")+"');\">");
           out.print("驳回");
           out.print("</a>");
        }else if(((String)abean.get("flag")).equals("3"))
        {
           out.print("封存");
        }
        %>
      </td>
      <td class="RecordRow" align="center" nowrap>  
    
      
      <a href="###" onclick="query('<%=abean.get("Report_id")%>','<%=abean.get("id")%>','<%=abean.get("unitcode")%>','<%=abean.get("flag")%>');">  
        <%if(abean.get("flag")!=null&&((String)abean.get("flag")).equals("-1"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("0"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("1"))
        {
           out.print("<img src='/images/view.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("2"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("3"))
        {
           out.print("<img src='/images/view.gif' border=0>");
        }
        %>
        </a>   
      
      </td>
    </tr>
    </logic:notEqual>
    </logic:notEqual>
    </logic:notEqual>
    </logic:notEqual>
        <logic:equal name="editReport_actuaialForm"  property="kmethod" value="0">
     <%
       LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
      if(i%2==0)
      {
     %>
     <tr class="trShallow">
     <%}
     else
     {%>
     <tr class="trDeep">
     <%
     }
     i++;          
     %> 
      <td align="left" class="RecordRow"  nowrap>   &nbsp;&nbsp;  
        <%
          out.print(abean.get("report_name"));
         %>
      </td>
      <td align="left" class="RecordRow" nowrap>  &nbsp;&nbsp; 
        <%if(abean.get("flag")!=null&&((String)abean.get("flag")).equals("-1"))
        {
           out.print("未填");
        }else if(((String)abean.get("flag")).equals("0"))
        {
           out.print("正在编辑");
        }else if(((String)abean.get("flag")).equals("1"))
        {
           out.print("已上报");
           flag=flag+"1";
        }else if(((String)abean.get("flag")).equals("2"))
        {
           out.print("<a href='###' onclick=\"description('"+abean.get("Report_id")+"','"+abean.get("unitcode")+"','"+abean.get("id")+"');\">");
           out.print("驳回");
           out.print("</a>");
        }else if(((String)abean.get("flag")).equals("3"))
        {
           out.print("封存");
        }
        %>
      </td>
      <td class="RecordRow" align="center" nowrap>  
      
       
      <a href="###" onclick="query('<%=abean.get("Report_id")%>','<%=abean.get("id")%>','<%=abean.get("unitcode")%>','<%=abean.get("flag")%>');">  
        <%if(abean.get("flag")!=null&&((String)abean.get("flag")).equals("-1"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("0"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("1"))
        {
           out.print("<img src='/images/view.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("2"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("3"))
        {
           out.print("<img src='/images/view.gif' border=0>");
        }
        %>
        </a>   
     
      </td>
    </tr>
    </logic:equal>
    
   </hrms:extenditerate>
  </table>
  <Br>
  <logic:equal  name="editReport_actuaialForm"  property="isAllSub" value="0">
   
   <table width="100%"  align="center"  border="0" style="margin-top: -17px;">
   ${editReport_actuaialForm.htmlbody}
   <tr><td align="center">
   <input type='button' id="button_goback" value='统一提交' onclick='sub();' class="mybutton">
   </td></tr>
   </table>
  </logic:equal>
    <logic:notEqual  name="editReport_actuaialForm"  property="isAllSub" value="0">
   <table width="100%"  align="center"  border="0">
   ${editReport_actuaialForm.htmlbody2}
     </table>
  </logic:notEqual>
   	</logic:equal>
   	</logic:equal>
</html:form>
<div id='wait' style='position: absolute; top: 200; left: 250;'>
	<table border="1" width="400" cellspacing="0" cellpadding="4"
		 class="table_style" height="87"
		align="center">
		<tr>
			<td class="td_style" 
				height=24>
				正在提交数据请稍候....
			</td>
		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee class="marquee_style" direction="right"
					width="300" scrollamount="5" scrolldelay="10" >
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
<script language="javascript">
 MusterInitData();
</script>