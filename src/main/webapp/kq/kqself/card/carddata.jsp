<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/kq/kq.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<% int i=0;%>
<script language="javascript">
  function changes()
  {
	  var start = $F('start_date');
 	 var end = $F('end_date');
 	 
 	 if(!isDate(start,"yyyy.MM.dd"))
      {
         alert("起始日期格式不正确,请输入正确的时间格式！\nyyyy.MM.dd");
         return false;
      }
 	 if(!isDate(end,"yyyy.MM.dd"))
      {
         alert("结束日期格式不正确,请输入正确的时间格式！\nyyyy.MM.dd");
         return false;
      }
   //szk验证时间 
   var dd = eval("document.baseNetSignInForm.start_date");
	  var ks = dd.value;
	  var jsd=eval("document.baseNetSignInForm.end_date");
	  var js = jsd.value;
	  ks=replaceAll(ks,"-",".");
	  js=replaceAll(js,"-",".");
	  if(ks>js)
	  {
	  	alert(KQ_CHECK_TIME_HINT);
	  	return false;
	  }
      baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_query=link";
      baseNetSignInForm.submit();
  }
  function makeup()
  {
      baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_makeup=link";
      baseNetSignInForm.submit();
  }
  function deltet_card()
  {
  	 var len=document.baseNetSignInForm.elements.length;
     var i;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
          if(document.baseNetSignInForm.elements[i].type=="checkbox")
          {
              if(document.baseNetSignInForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      }
      if(isCorrect)
      {      
      		if(confirm("确认要删除已选记录？（只能删除起草和驳回的记录）"))
      		{
         		baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_delete=link";
         		baseNetSignInForm.submit();
      		}else
      		{
      			return false;
      		}
      }else
      {
      	alert("请先选择人员！");
        return false;
      }      
  }
  function apprea_card()
  {	 
  	 var request=new Request({method:'post',asynchronous:false,onSuccess:setapprove,functionId:'15502110214'});
  }
  
  function setapprove(outparamters){
  	  
      var apppeo = outparamters.getValue("apppeo");
      if(apppeo.length <= 1){
      	if(apppeo.length == 0){
      		var app_account = "null";
      		if(confirm("确认要报批该记录？"))
		      {
		         baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_appear=link&account="+app_account;
		         baseNetSignInForm.submit();
		      } 
      	}else{
      	  var list = apppeo[0];
      	  var array = list.split(",");
      	  var app_account = array[2];
      	  
	      if(confirm("确认要报批该记录？"))
		      {
		         baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_appear=link&account=" + getEncodeStr(app_account);
		         baseNetSignInForm.submit();
		      } 
      	}
      }else{
      	var dh="360px";
		var wh="360px";
		var target_url="";
		var return_vo;
		var str_app="";
		for(var i=0;i<apppeo.length;i++){
			
			var list = apppeo[i].split(",");
			for(var j=0;j<list.length;j++){
				str_app = str_app + list[j]+"`";
			}
			str_app = str_app + "~";
		}
		target_url = "/kq/kqself/card/carddata.do?b_selapp=link&str_app="+str_app;
		return_vo = window.showModalDialog(target_url,1,
		"dialogWidth:"+wh+";dialogHeight:"+dh+";resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		if(return_vo){
			var app_account = return_vo.app_account;
			if(confirm("确认要报批该记录？"))
		      {
		         baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_appear=link&account=" + getEncodeStr(app_account);
		         baseNetSignInForm.submit();
		      } 
		 }
      }
   } 
</script>
<html:form action="/kq/kqself/card/carddata">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
 <tr height="25">
    <td align="left" valign="bottom" style="padding-bottom: 5px;" nowrap>
			<bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" value="${baseNetSignInForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date"  value="${baseNetSignInForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
            &nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"> 
    </td>         
 </tr>

</table>
<div class="fixedDiv2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <thead>
    <tr>
    <td align="center"  class="TableRow" style="border-left: none;border-top:none;" nowrap>
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
     </td> 
     <td align="center" class="TableRow" style="border-top:none;" nowrap>
	姓名
     </td>
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
	卡号
      </td> 
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
	日期
      </td> 
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
	时间
      </td> 
      <td align="center" class="TableRow" style="border-top:none;" nowrap>
	说明
      </td>
       <logic:equal name="baseNetSignInForm" property="isInout_flag" value="true">
      <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
	进出标志
      </td>
      </logic:equal>
       <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap>
	审批标志
      </td>
    </tr>  
  </thead>
  <hrms:paginationdb id="element" name="baseNetSignInForm" sql_str="baseNetSignInForm.sql_self" table="" where_str="baseNetSignInForm.where_self" columns="${baseNetSignInForm.column_self}" order_by="${baseNetSignInForm.order_self}" page_id="pagination" pagerows="20"  indexes="indexes">
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
           <td align="center" class="RecordRow"  style="border-left:none;" nowrap>              
                <hrms:checkmultibox name="baseNetSignInForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
           </td>  
           <td align="center" class="RecordRow" nowrap>
             <bean:write name="element" property="a0101" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="card_no" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="work_date" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="work_time" filter="false"/>
           </td>
           <td align="center" class="RecordRow" nowrap>
              <bean:write name="element" property="location" filter="false"/>
           </td> 
            <logic:equal name="baseNetSignInForm" property="isInout_flag" value="true">
           <td align="center" class="RecordRow" nowrap>
           <logic:equal name="element" property="inout_flag" value="-1">
                                   出
                               </logic:equal>
                                <logic:equal name="element" property="inout_flag" value="0">
                                   不限
                                </logic:equal>
                                <logic:equal name="element" property="inout_flag" value="1">
                                   进
             </logic:equal>
           </td> 
           </logic:equal>
           <td align="center" class="RecordRow" style="border-right:none;" nowrap>
             <hrms:codetoname codeid="23" name="element" codevalue="sp_flag" codeitem="codeitem" scope="page"/>  	      
                <bean:write name="codeitem" property="codename" />&nbsp;  
           </td>         
       </tr>
      </hrms:paginationdb>
</table>
</div>
<div style="width:expression(document.body.clientWidth-10);">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="baseNetSignInForm" property="pagination" nameId="baseNetSignInForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
  </table>  
  </div>
    <table  width="100%" align="center">
		<tr>
		    <td align="center">	
		        <hrms:priv func_id="0B041"> 
		    	<input type="button" name="br_return" value='补签' class="mybutton" onclick="makeup();"> 
		    	<input type="button" name="br_return" value='报批' class="mybutton" onclick="apprea_card();"> 
		    	</hrms:priv>
		    	
		    	<hrms:priv func_id="0B042"> 
		        <input type="button" name="br_return" value='<bean:message key="button.delete"/>' class="mybutton" onclick="deltet_card();"> 
		        </hrms:priv>
		       
			</td>
		</tr>
  </table>
</html:form>
<script type="text/javascript">

</script>


