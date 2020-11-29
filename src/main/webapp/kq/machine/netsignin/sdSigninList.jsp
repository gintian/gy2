<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<% int i=0;%>
<script language="javascript">
	var bean_value
    function setbean(workdate)
   {
     var hashvo=new ParameterSet();
     hashvo.setValue("workdate",workdate);	
     
     hashvo.setValue("restdate","${singleRegisterForm.rest_date}");
     hashvo.setValue("b0110","${singleRegisterForm.b0110_value}");        	
     var request=new Request({method:'post',onSuccess:getBean,functionId:'15301110999'},hashvo);
   }
    function getBean(outparamters)
    {
      bean_value=outparamters.getValue("onedate");      
    }
    function showBean()
    {      
      return bean_value;
    }
    //把1转换是
    var yesfile
    function setbeantime(ont)
    {
    	yesfile="是";
    }
    function showBeanFile()
    {      
      return yesfile;
    }
    //返回
function goback()
  {
      netSigninForm.action="/kq/machine/netsignin/empNetSingnin_data.do?b_search=link";
      netSigninForm.submit();
  }
  //查询
  function changes()
  {
  	var dbsign = document.getElementById("dbsign").value;
    var a0100sign = document.getElementById("a0100sign").value;
  	netSigninForm.action="/kq/machine/netsignin/sdsigninlist.do?b_sdlist=link&dbsign="+dbsign+"&a0100sign="+a0100sign+"&filg=2";
    netSigninForm.submit();
  }
  //补签
  function makeup()
  {
  	  var dbsign = document.getElementById("dbsign").value;
      var a0100sign = document.getElementById("a0100sign").value;
      netSigninForm.action="/kq/machine/netsignin/sdsigninlist.do?b_sdmake=link&dbsign="+dbsign+"&a0100sign="+a0100sign;
      netSigninForm.submit();
  }
  //取消
  function formationStoreroom(time)
  {
  if(time==null)
  {
  	return;
  }
  	if(confirm("确认取消签到吗?"))
  	{
  		var hashvo=new ParameterSet();
  	var dbsign = document.getElementById("dbsign").value;
    var a0100sign = document.getElementById("a0100sign").value;
    hashvo.setValue("a0100",a0100sign);
	hashvo.setValue("nbase",dbsign);
	hashvo.setValue("sdmakeup_date",time); //时间
	hashvo.setValue("sdjudge","0");  //1=签到 0 补签
    var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15221400013'},hashvo);
  	}
  }
  function showReturn(outparamters)
   {
      var mess=outparamters.getValue("mess");
      alert(mess);
      netSigninForm.action="/kq/machine/netsignin/sdsigninlist.do?b_sdlist=link";
      netSigninForm.submit();
   } 
</script>
<html:form action="/kq/machine/netsignin/sdsigninlist">
	<table width="70%" border="0" cellspacing="0" align="center" cellpadding="0">
	<html:hidden styleId="dbsign" name="netSigninForm" property="dbsign"/>
	<html:hidden styleId="a0100sign" name="netSigninForm" property="a0100sign"/>
	<tr>
		<td>
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
		<tr>
			<td align= "left" nowrap>
				<bean:write  name="netSigninForm" property="sdb0110"/>&nbsp;
				<bean:write name="netSigninForm" property="sde0122"/>&nbsp; 
				<bean:write name="netSigninForm" property="sda0101"/>
			</td>
		</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
		 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
		 	<thead>
		 	<tr>
				<td align="left"  class="TableRow" nowrap colspan="3">
						<bean:message key="label.from"/>
   	  	 				<input type="text" name="start_date" value="${netSigninForm.start_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate">
   	  	 				<bean:message key="label.to"/>
   	  	 				<input type="text" name="end_date"  value="${netSigninForm.end_date}" extra="editor" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate">
            			&nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"> 
				</td>
			</tr>
		 		<tr>
     			<td align="center" class="TableRow"  nowrap>
					工作日期
     			</td>
      			<td align="center" class="TableRow"  nowrap>
					上岛
      			</td>
      			<td align="center" class="TableRow"  nowrap>
					取消
      			</td>  
      			</tr>
		 	</thead>		 
		 	<hrms:paginationdb id="element" name="netSigninForm" sql_str="netSigninForm.sql_self" table="" where_str="netSigninForm.where_self" columns="netSigninForm.column_self" order_by="netSigninForm.order_self" page_id="pagination" pagerows="31"  indexes="indexes">
		 		<%
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
          		<logic:iterate id="info" name="netSigninForm"  property="fielditemlist">
          		
          			 <td align="center" class="RecordRow" nowrap>
          			 <logic:equal name="info" property="viewvalue" value="q03z0">
          			 <script language="javascript"> 
                                 setbean('<bean:write name="element" property="${info.itemid}" filter="true"/>'); 
                                 document.writeln(showBean());
                     </script> 
          			 </logic:equal>
          			 <logic:equal name="info" property="viewvalue" value="sd">
          			 <script language="javascript"> 
                                 setbeantime('<bean:write name="element" property="${info.itemid}" filter="true"/>'); 
                                 document.writeln(showBeanFile());
                     </script>
          			 
          			</logic:equal>
          			<logic:equal name="info" property="viewvalue" value="sdvalue">
          			 	<a href="javascript:formationStoreroom('<bean:write name="element" property="${info.itemid}" filter="true"/>')">取消</a>
          			</logic:equal>
          		</td>    
          		</logic:iterate>
          		
		 	</hrms:paginationdb>
		 </table>
		 <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="netSigninForm" property="pagination" nameId="netSigninForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>
		 </td>
	</tr>
	<table  width="100%" align="center">
		<tr>
		    <td align="center">
		    	<hrms:priv func_id="0C3464">         
                        <input type="button" name="br_return" value='补签' class="mybutton" onclick="makeup();"> 
		        </hrms:priv>	
		        <input type="button" name="br_return" value='<bean:message key="button.return"/>' class="mybutton" onclick="goback();"> 
		        
			</td>
		</tr>
  </table>
	</table>
</html:form>