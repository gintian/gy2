<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hjsj.hrms.actionform.kq.kqself.KqSelfForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/validateDate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/kq/kq.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript">
function dels()
   {
      //if(ifdel())
     var len=document.kqselfForm.elements.length;
     var i;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
          if(document.kqselfForm.elements[i].type=="checkbox")
          {
              if(document.kqselfForm.elements[i].checked==true)
              {
                isCorrect=true;
                break;
              }
          }
      }
      if(isCorrect)
      {
      	if(confirm("确认要删除！"))
      	{
        	kqselfForm.action="/kq/kqself/search_kqself.do?b_del=link";
        	kqselfForm.submit();
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
   function approves(sp_flag)
   {
        var len=document.kqselfForm.elements.length;
        var i;
        var isCorrect=false;
        var appList = new Array();
        var a = 0;
        var b = 1;
        for (i=0;i<len;i++)
        {
            var aCheckBox = document.kqselfForm.elements[i];
            if(aCheckBox.type=="checkbox" && aCheckBox.checked==true && aCheckBox.name.substring(0,17)=="pagination.select")
            {
                var endIndex = aCheckBox.name.indexOf("]");
                var checkIndex = parseInt(aCheckBox.name.substring(18,endIndex)) + 1;
            	appList[a++] = document.getElementById("ID_" + checkIndex).value;
            	isCorrect = true;
            }
        }
        if(isCorrect)
        {
            var hashvo = new ParameterSet();
            hashvo.setValue("sp_flag",sp_flag);
            hashvo.setValue("appList",appList);
            hashvo.setValue("table","${kqselfForm.table}");
    	    var request = new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'1510020017'},hashvo);

        }else
        {
        	alert(KQ_SELF_APP_CONVERT_CONFIRM);
            return false;
        }
   }
   function update()
   {
      
       alert("已批纪录不可以修改！");
     
   }
    function changes(str)
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
      var dd = eval("document.kqselfForm.start_date");
   	  var ks = dd.value;
   	  var jsd=eval("document.kqselfForm.end_date");
   	  var js = jsd.value;
   	  ks=replaceAll(ks,"-",".");
   	  js=replaceAll(js,"-",".");
   	  if(ks>js)
   	  {
   	  	alert(KQ_CHECK_TIME_HINT);
   	  	return false;
   	  }
      kqselfForm.action="/kq/kqself/search_kqself.do?b_query=link&wo="+str;
      kqselfForm.submit();
   }
   function cancelMess(flag)
   {
     if(flag=="01")
      alert("该申请假期没有同意，所以不能销假！");
     else if(flag=="03")
        alert("该申请假期没有批准，所以不能销假！");
   }
   function cxapply(flag)
   {
   	 	alert("该申请不是报审或待批状态，不能撤销！");
   	 	return;
   }
   function backout(id,audit_flag)
   {
   		if(confirm("确认撤销申请？"))
   		{
   		kqselfForm.action="/kq/kqself/cancel_kqself.do?b_undo=link&id="+id+"&audit_flag="+audit_flag;
   		kqselfForm.submit();
   		}
   }
function changeStatus(){
	kqselfForm.action="/kq/kqself/search_kqself.do?b_query=link";
    kqselfForm.submit();
}
function turnTo(flag){
    var len=document.kqselfForm.elements.length;
    var i;
    var isCorrect=false;
    var appList = new Array();
    var a = 0;
    var b = 1;
    for (i=0;i<len;i++)
    {
        var aCheckBox = document.kqselfForm.elements[i];
        if(aCheckBox.type=="checkbox" && aCheckBox.checked==true && aCheckBox.name.substring(0,17)=="pagination.select")
        {
            var endIndex = aCheckBox.name.indexOf("]");
            var checkIndex = parseInt(aCheckBox.name.substring(18,endIndex)) + 1;
        	appList[a++] = document.getElementById("ID_" + checkIndex).value;
        	isCorrect = true;
        }
    }
    if(isCorrect)
    {
        var hashvo = new ParameterSet();
        hashvo.setValue("flag",flag);
        hashvo.setValue("appList",appList);
        
        if("1" == flag)
        {
	    	if(confirm(KQ_SELF_APP_CONVERT_OVER))
	    	{
	    		var request = new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'1510020030'},hashvo);
	     	}
        }else if("0" == flag)
        {
        	if(confirm(KQ_SELF_APP_CONVERT_OFF))
	    	{
        		var request = new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'1510020030'},hashvo);
	     	}
        }else
     	{
     		return false;
     	}
    }else
    {
    	alert(KQ_SELF_APP_CONVERT_CONFIRM);
        return false;
    }
}
function returnInfo(outparamters){
	var mess = outparamters.getValue("errorMessage");
	mess = getDecodeStr(mess);
	if(mess != "" && mess != null && mess != "undefine")
		alert(mess);
	window.location.href = "/kq/kqself/search_kqself.do?b_query=link";
}
</script>

<%
int i=0;
%>
   
<html:form action="/kq/kqself/search_kqself">
<html:hidden property="table"/>
<table border="0" width="100%" cellspacing="0"  align="center" cellpadding="0"> 
 <tr>
  <td>
      <table>
       <tr>
        <td align="left"  nowrap colspan="${kqselfForm.cols}"> 
        	<logic:equal value="Q11" name="kqselfForm" property="table">
        	<logic:notEmpty name="kqselfForm" property="field">
	        	<bean:message key="kq.self.app.state"/>
	        	<html:select property="select_flag" styleId="select_flag" name="kqselfForm" size="1" onchange="changeStatus();">
	        		<html:optionsCollection property="appStatusList" label="dataName" value="dataValue"/>
	        	</html:select>&nbsp;
        	</logic:notEmpty>
        	</logic:equal> 
			<bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" value="${kqselfForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" 
   	  	 	  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);' 
   	  	 	  onchange="rep_dateValue(this);">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date"  value="${kqselfForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" 
   	  	 	  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false,false);'
   	  	 	  onchange="rep_dateValue(this);">
            &nbsp;<input type="button" name="br_return" value='<bean:message key="button.query"/>' class="mybutton" onclick="changes();"> 
        	&nbsp;<hrms:kqcourse/>
       </td> 
       </tr>
      </table>
  </td>
 </tr>
 <tr>
  <td>
  <div class="fixedDiv2">
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">

  	
    <tr >
     <td align="center"  nowrap class="TableRow" style="border-top: none;border-left: none;">
		<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
      </td> 
      <logic:iterate id="element" name="kqselfForm"  property="flist" indexId="index">
         <logic:equal name="element" property="visible" value="true">
            <td align="center"  nowrap class="TableRow" style="border-top:none;">
                <bean:write name="element" property="itemdesc" />&nbsp;
            </td>
         </logic:equal>    
      </logic:iterate>
      <td align="center"  nowrap class="TableRow" style="border-top:none;">
            <bean:message key="label.edit"/>            	
      </td>
      
      <logic:equal name="kqselfForm" property="table" value="Q11">
         <hrms:priv func_id="0B217"> 
	      <td align="center"  nowrap class="TableRow" style="border-top:none;border-right: none;">
	            <bean:message key="label.view"/>
	      </td>
        </hrms:priv>
       </logic:equal>
       <logic:notEqual name="kqselfForm" property="table" value="Q11">
            <td align="center"  nowrap class="TableRow" style="border-top:none;border-right: none;">
                <bean:message key="label.view"/>
            </td>
       </logic:notEqual>   
       <logic:equal name="kqselfForm" property="table" value="Q11">
        <hrms:priv func_id="0B210"> 
         <td align="center"  nowrap class="TableRow" style="border-top:none;">
           撤销加班	
         </td>
         <td align="center" class="TableRow" style="border-top:none;" nowrap>
           撤销加班标识	
          </td>
         </hrms:priv>
         <hrms:priv func_id="0B218">
           <td align="center"  style="border-top:none;border-right: none;" nowrap class="TableRow">
           撤销申请	
           </td>
         </hrms:priv>
       </logic:equal>
       
       <logic:equal name="kqselfForm" property="table" value="Q13">
        <hrms:priv func_id="0B230"> 
         <td align="center"  nowrap class="TableRow" style="border-top:none;">
           撤销公出	
         </td>
         <td align="center" class="TableRow" style="border-top:none;" nowrap>
           撤销公出标识	
          </td>
         </hrms:priv>
         <hrms:priv func_id="0B238">
           <td align="center"  style="border-top:none;border-right: none;" nowrap class="TableRow">
           撤销申请	
           </td>
         </hrms:priv>
       </logic:equal>
       
       <logic:equal name="kqselfForm" property="table" value="Q15">
        <hrms:priv func_id="0B220"> 
         <td align="center"  nowrap class="TableRow" style="border-top:none;">
           销假	
         </td>
         <td align="center" class="TableRow" style="border-top:none;" nowrap>
           销假标识	
          </td>
         </hrms:priv>
         <hrms:priv func_id="0B225">
           <td align="center"  style="border-top:none;border-right: none;" nowrap class="TableRow">
           撤销申请	
           </td>
         </hrms:priv>
       </logic:equal>
    </tr>  
<hrms:paginationdb id="element" name="kqselfForm" sql_str="kqselfForm.sql" table="" where_str="kqselfForm.where" columns="${kqselfForm.com}" order_by="kqselfForm.order" page_id="pagination"  indexes="indexes">
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
          KqSelfForm appForm = (KqSelfForm)request.getSession().getAttribute("kqselfForm");
          String table = appForm.getTable();
          LazyDynaBean beans=(LazyDynaBean)pageContext.getAttribute("element");
          String z1 = (String)beans.get(table.toLowerCase()+"z1");
          String z3 = (String)beans.get(table.toLowerCase()+"z3");  
          String q01 = (String)beans.get(table.toLowerCase()+"01");  
			//	事由 
          String re = (String)beans.get(table.toLowerCase()+"07");
			String rel = table.toLowerCase()+"07";
		  String ree= re;
		  if(re.length() > 10)
			  ree = re.substring(0,10)+"...";
          
          %>     
           <td align="center" class="RecordRow" style="border-top:none;border-left: none;" nowrap>
           		<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>">
              <logic:match name="element" property="${kqselfForm.sp_field}" value="01">           
                <hrms:checkmultibox name="kqselfForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                <input type="hidden" id="ID_<%=i %>" value="<%=q01%>">                               
              </logic:match> 
              <logic:match name="element" property="${kqselfForm.sp_field}" value="07">           
                <hrms:checkmultibox name="kqselfForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                <input type="hidden" id="ID_<%=i %>" value="<%=q01%>">                               
              </logic:match>
              </hrms:kqdurationjudge>
          </td>    
            <logic:iterate id="flist" name="kqselfForm"  property="flist" indexId="index">
             <logic:equal name="flist" property="visible" value="true">
                  <logic:notEqual name="flist" property="itemtype" value="D">
                        <logic:notEqual name="flist" property="codesetid" value="0">  
                          <logic:notEqual name="flist" property="itemid" value="q1104"> 
                             <logic:notEqual name="flist" property="itemid" value="q1504">      
                              <hrms:codetoname codeid="${flist.codesetid}" name="element" codevalue="${flist.itemid}" codeitem="codeitem" scope="page"/>  	      
                             	 <td align="left" class="RecordRow" nowrap>
                               &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
                               	</td>
                              </logic:notEqual>     
                           </logic:notEqual> 
                          <logic:equal name="flist" property="itemid" value="q1504">   
                            <%
             	             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
             	             String id=(String)abean.get("q1504");
                           %>
                            <td align="left" class="RecordRow" nowrap> 
                           <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                           </td>
                           </logic:equal> 
                          <logic:equal name="flist" property="itemid" value="q1104">   
                            <%
                             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
            	             String id=(String)abean.get("q1104");
                           %>            
                           	 <td align="left" class="RecordRow" nowrap>              
                           <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                           </td>
                           </logic:equal>  
                        </logic:notEqual>                       
                        <logic:equal name="flist" property="codesetid" value="0">                          
                           <logic:equal name="flist" property="itemid" value="q1104">   
                            <%
             	             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
             	             String id=(String)abean.get("q1104");
                           %>          
                           	 <td align="left" class="RecordRow" nowrap>                
                           <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                           </td>
                           </logic:equal>                               
                           <logic:equal name="flist" property="itemid" value="q1504">   
                            <%
             	             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
             	             String id=(String)abean.get("q1504");
                           %>
                            <td align="left" class="RecordRow" nowrap> 
                           <hrms:kqclassname classid="<%=id%>"></hrms:kqclassname>
                           </td>
                           </logic:equal>   
                           <logic:notEqual name="flist" property="itemid" value="q1104"> 
                             <logic:notEqual name="flist" property="itemid" value="q1504"> 
                              <logic:notEqual name="flist" property="itemid" value="q11z4"> 
	                              <logic:equal name="flist" property="itemid" value="<%= rel %>"> 
                                  <td align="left" class="RecordRow" title="<%= re %>" nowrap>
                                   &nbsp;
                                   <%= ree %>
                                   &nbsp; </td>
	                              </logic:equal> 
	                              <logic:notEqual name="flist" property="itemid" value="<%= rel %>">   
                           			<td align="left" class="RecordRow"  nowrap>
                                   &nbsp;
                                   <bean:write name="element" property="${flist.itemid}" filter="false"/> 
                                   &nbsp; </td>
                          		 </logic:notEqual>    
                              </logic:notEqual>
                              <logic:equal name="flist" property="itemid" value="q11z4">
                                <td align="left" class="RecordRow"  nowrap>
                                   &nbsp;
                                   <bean:write name="element" property="${flist.itemid}" filter="false"/> 
                                   &nbsp; </td>
                              </logic:equal>     
                             </logic:notEqual>     
                           </logic:notEqual>           
                        </logic:equal>                   
                    </logic:notEqual>
                    <logic:equal name="flist" property="itemtype" value="D">
                       <td align="center" class="RecordRow" nowrap>
                           <bean:write name="element" property="${flist.itemid}" filter="false"/>&nbsp;
                       </td>
                    </logic:equal>    
               </logic:equal>    
              </logic:iterate>
              <bean:define id="key_field1" name="element" property="${kqselfForm.key_field}"/>
	         <bean:define id="sp_field1" name="element" property="${kqselfForm.sp_field}"/>
	         
	         <%
	         		//参数加密
	    		     String key_field = PubFunc.encrypt(key_field1.toString());
	        		 String sp_field = PubFunc.encrypt(sp_field1.toString());
	         %>
              <td align="center" class="RecordRow" nowrap>
              	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>">
                <logic:match name="element" property="${kqselfForm.sp_field}" value="01"> 
                      <a href="/kq/kqself/search_kqself.do?b_view=link&id=<%=key_field %>&audit_flag=<%=sp_field %>&app_flag=<bean:write name='element' property='flag' filter='true'/>">
                      <img src="/images/edit.gif" border=0>
                      </a>
                </logic:match>  
                <logic:match name="element" property="${kqselfForm.sp_field}" value="07"> 
                      <a href="/kq/kqself/search_kqself.do?b_view=link&id=<%=key_field %>&audit_flag=<%=sp_field %>&app_flag=<bean:write name='element' property='flag' filter='true'/>">
                      <img src="/images/edit.gif" border=0>
                      </a>
                </logic:match>
                </hrms:kqdurationjudge>                                                    
              </td>
              <%
		            //参数加密
		 		     String str2 = "id=" + key_field1 + "&audit_flag=" + sp_field1;
              %>
	         <logic:equal name="kqselfForm" property="table" value="Q11">
		         <hrms:priv func_id="0B217"> 
		         <td align="center" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
                   <a href="/kq/kqself/search_kqself.do?b_viewrecord=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
                     <img src="/images/view.gif" border=0>    
                    </href>             
                 </td>
		        </hrms:priv>
		       </logic:equal>
		       <logic:notEqual name="kqselfForm" property="table" value="Q11">
		            <td align="center" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
                   <a href="/kq/kqself/search_kqself.do?b_viewrecord=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
                     <img src="/images/view.gif" border=0>    
                    </href>             
                 </td>
		       </logic:notEqual>  
		       
              <logic:equal name="kqselfForm" property="table" value="Q11">
              <hrms:priv func_id="0B210"> 
                 <td align="center"  class="RecordRow"  nowrap>
                   <logic:equal name="element" property="q11z5" value="03"> 
                     <logic:equal name="element" property="q11z0" value="01">
                     	<hrms:kqdurationjudge startDate="<%=z3 %>" > 
                         <hrms:notonecancelleave id='<%=q01 %>' table="<%=table %>">
	                        <a href="/kq/kqself/cancel_kqself.do?b_cancel1=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                        <img src="/images/edit.gif" border=0></a>
                         </hrms:notonecancelleave>
                         <hrms:onecancelleave id='<%=q01 %>' table="<%=table %>">
                         <a href="/kq/kqself/cancel_kqself.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:onecancelleave>
                         </hrms:kqdurationjudge>
                     </logic:equal>
                   </logic:equal>
                    <logic:equal name="element" property="q11z5" value="03"> 
                      <logic:notEqual name="element" property="q11z0" value="01">
                      <hrms:kqdurationjudge startDate="<%=z3 %>">  
                        <a href="###" onclick="cancelMess('01')">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:kqdurationjudge>
                      </logic:notEqual>
                   </logic:equal>  
                   <logic:notEqual name="element" property="q11z5" value="03">
                   <hrms:kqdurationjudge startDate="<%=z3 %>">  
                        <a href="###" onclick="cancelMess('03')">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:kqdurationjudge>
                   </logic:notEqual>
                 </td>
                 <td align="center"  class="RecordRow"  nowrap>
                 <%
             	             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
             	             String id=(String)abean.get("q1101");
                       %>      
                 	<hrms:tagsell id='<%=id%>' tableName="q11">
                 	
                 	</hrms:tagsell>
                 </td>
                </hrms:priv>
                <%
                	String key_field2=PubFunc.encrypt(key_field1.toString());
                	String sp_field2=PubFunc.encrypt(sp_field1.toString());	
                %>
                <hrms:priv func_id="0B218">
                 <td align="center"  class="RecordRow" style="border-top:none;border-right: none;" nowrap>
                 	<logic:equal name="element" property="q11z5" value="02">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 	<a href="###" onclick="backout('<%=key_field2 %>','<%=sp_field2 %>')">
                 		<!--<a href="/kq/kqself/cancel_kqself.do?b_undo=link&id=<bean:write name="element" property="${kqselfForm.key_field}" filter="true"/>&audit_flag=<bean:write name="element" property="${kqselfForm.sp_field}" filter="true"/>">-->
                 		<img src="/images/edit.gif" border=0></a>
                 		</hrms:kqdurationjudge>
                 	</logic:equal>
                 	<logic:equal name="element" property="q11z5" value="08">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 	<a href="###" onclick="backout('<%=key_field2 %>','<%=sp_field2 %>')">
                 		<!--<a href="/kq/kqself/cancel_kqself.do?b_undo=link&id=<bean:write name="element" property="${kqselfForm.key_field}" filter="true"/>&audit_flag=<bean:write name="element" property="${kqselfForm.sp_field}" filter="true"/>">-->
                 		<img src="/images/edit.gif" border=0></a>
                 		</hrms:kqdurationjudge>
                 	</logic:equal>
                 	<logic:notEqual name="element" property="q11z5" value="02">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 		<logic:notEqual name="element" property="q11z5" value="08">
                 		<a href="###" onclick="cxapply('01')">
                 		<img src="/images/edit.gif" border=0></a>
                 		
                 		</logic:notEqual>
                 		</hrms:kqdurationjudge>
                 	</logic:notEqual>
                 </td>
                 </hrms:priv>
              </logic:equal>
              
              <logic:equal name="kqselfForm" property="table" value="Q13">
              <hrms:priv func_id="0B230"> 
                 <td align="center"  class="RecordRow"  nowrap>
                   <logic:equal name="element" property="q13z5" value="03"> 
                     <logic:equal name="element" property="q13z0" value="01">
                     	<hrms:kqdurationjudge startDate="<%=z3 %>" > 
                         <hrms:notonecancelleave id='<%=q01 %>'  table="<%=table %>">
	                        <a href="/kq/kqself/cancel_kqself.do?b_cancel1=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                        <img src="/images/edit.gif" border=0></a>
                         </hrms:notonecancelleave>
                         <hrms:onecancelleave id='<%=q01 %>' table="<%=table %>">
                         <a href="/kq/kqself/cancel_kqself.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:onecancelleave>
                         </hrms:kqdurationjudge>
                     </logic:equal>
                   </logic:equal>
                    <logic:equal name="element" property="q13z5" value="03"> 
                      <logic:notEqual name="element" property="q13z0" value="01">
                      <hrms:kqdurationjudge startDate="<%=z3 %>">  
                        <a href="###" onclick="cancelMess('01')">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:kqdurationjudge>
                      </logic:notEqual>
                   </logic:equal>  
                   <logic:notEqual name="element" property="q13z5" value="03">
                   <hrms:kqdurationjudge startDate="<%=z3 %>">  
                        <a href="###" onclick="cancelMess('03')">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:kqdurationjudge>
                   </logic:notEqual>
                 </td>
                 <td align="center"  class="RecordRow"  nowrap>
                 <%
             	             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
             	             String id=(String)abean.get("q1301");
                       %>      
                 	<hrms:tagsell id='<%=id%>' tableName="q13">
                 	
                 	</hrms:tagsell>
                 </td>
                </hrms:priv>
                <%
                	String key_field2=PubFunc.encrypt(key_field1.toString());
                	String sp_field2=PubFunc.encrypt(sp_field1.toString());	
                %>
                <hrms:priv func_id="0B238">
                 <td align="center"  class="RecordRow" style="border-top:none;border-right: none;" nowrap>
                 	<logic:equal name="element" property="q13z5" value="02">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 	<a href="###" onclick="backout('<%=key_field2 %>','<%=sp_field2 %>')">
                 		<!--<a href="/kq/kqself/cancel_kqself.do?b_undo=link&id=<bean:write name="element" property="${kqselfForm.key_field}" filter="true"/>&audit_flag=<bean:write name="element" property="${kqselfForm.sp_field}" filter="true"/>">-->
                 		<img src="/images/edit.gif" border=0></a>
                 		</hrms:kqdurationjudge>
                 	</logic:equal>
                 	<logic:equal name="element" property="q13z5" value="08">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 	<a href="###" onclick="backout('<%=key_field2 %>','<%=sp_field2 %>')">
                 		<!--<a href="/kq/kqself/cancel_kqself.do?b_undo=link&id=<bean:write name="element" property="${kqselfForm.key_field}" filter="true"/>&audit_flag=<bean:write name="element" property="${kqselfForm.sp_field}" filter="true"/>">-->
                 		<img src="/images/edit.gif" border=0></a>
                 		</hrms:kqdurationjudge>
                 	</logic:equal>
                 	<logic:notEqual name="element" property="q13z5" value="02">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 		<logic:notEqual name="element" property="q13z5" value="08">
                 		<a href="###" onclick="cxapply('01')">
                 		<img src="/images/edit.gif" border=0></a>
                 		
                 		</logic:notEqual>
                 		</hrms:kqdurationjudge>
                 	</logic:notEqual>
                 </td>
                 </hrms:priv>
              </logic:equal>
              
              <logic:equal name="kqselfForm" property="table" value="Q15">
              <hrms:priv func_id="0B220"> 
                 <td align="center"  class="RecordRow"  nowrap>
                   <logic:equal name="element" property="q15z5" value="03"> 
                     <logic:equal name="element" property="q15z0" value="01">
                     	<hrms:kqdurationjudge startDate="<%=z3 %>" > 
                         <hrms:notonecancelleave id='<%=q01 %>'  table="<%=table %>">
	                        <a href="/kq/kqself/cancel_kqself.do?b_cancel1=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
	                        <img src="/images/edit.gif" border=0></a>
                         </hrms:notonecancelleave>
                         <hrms:onecancelleave id='<%=q01 %>' table="<%=table %>">
                         <a href="/kq/kqself/cancel_kqself.do?b_cancel=link&encryptParam=<%=PubFunc.encrypt(str2) %>">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:onecancelleave>
                         </hrms:kqdurationjudge>
                     </logic:equal>
                   </logic:equal>
                    <logic:equal name="element" property="q15z5" value="03"> 
                      <logic:notEqual name="element" property="q15z0" value="01">
                      <hrms:kqdurationjudge startDate="<%=z3 %>">  
                        <a href="###" onclick="cancelMess('01')">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:kqdurationjudge>
                      </logic:notEqual>
                   </logic:equal>  
                   <logic:notEqual name="element" property="q15z5" value="03">
                   <hrms:kqdurationjudge startDate="<%=z3 %>">  
                        <a href="###" onclick="cancelMess('03')">
                         <img src="/images/edit.gif" border=0></a>
                         </hrms:kqdurationjudge>
                   </logic:notEqual>
                 </td>
                 <td align="center"  class="RecordRow"  nowrap>
                 <%
             	             LazyDynaBean abean=(LazyDynaBean)  pageContext.getAttribute("element");
             	             String id=(String)abean.get("q1501");
                       %>      
                 	<hrms:tagsell id='<%=id%>' tableName="q15">
                 	
                 	</hrms:tagsell>
                 </td>
                </hrms:priv>
                <%
                	String key_field2=PubFunc.encrypt(key_field1.toString());
                	String sp_field2=PubFunc.encrypt(sp_field1.toString());	
                %>
                <hrms:priv func_id="0B225">
                 <td align="center"  class="RecordRow" style="border-top:none;border-right: none;" nowrap>
                 	<logic:equal name="element" property="q15z5" value="02">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 	<a href="###" onclick="backout('<%=key_field2 %>','<%=sp_field2 %>')">
                 		<!--<a href="/kq/kqself/cancel_kqself.do?b_undo=link&id=<bean:write name="element" property="${kqselfForm.key_field}" filter="true"/>&audit_flag=<bean:write name="element" property="${kqselfForm.sp_field}" filter="true"/>">-->
                 		<img src="/images/edit.gif" border=0></a>
                 		</hrms:kqdurationjudge>
                 	</logic:equal>
                 	<logic:equal name="element" property="q15z5" value="08">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 	<a href="###" onclick="backout('<%=key_field2 %>','<%=sp_field2 %>')">
                 		<!--<a href="/kq/kqself/cancel_kqself.do?b_undo=link&id=<bean:write name="element" property="${kqselfForm.key_field}" filter="true"/>&audit_flag=<bean:write name="element" property="${kqselfForm.sp_field}" filter="true"/>">-->
                 		<img src="/images/edit.gif" border=0></a>
                 		</hrms:kqdurationjudge>
                 	</logic:equal>
                 	<logic:notEqual name="element" property="q15z5" value="02">
                 	<hrms:kqdurationjudge startDate="<%=z1 %>" endDate="<%=z3 %>"> 
                 		<logic:notEqual name="element" property="q15z5" value="08">
                 		<a href="###" onclick="cxapply('01')">
                 		<img src="/images/edit.gif" border=0></a>
                 		
                 		</logic:notEqual>
                 		</hrms:kqdurationjudge>
                 	</logic:notEqual>
                 </td>
                 </hrms:priv>
              </logic:equal>
         </tr>
    </hrms:paginationdb>
    </table>   
    </div>
  </td>
 </tr>
     <tr>
      <td  nowrap colspan="${kqselfForm.cols}">
      <div style="*width:expression(document.body.clientWidth-10);">
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
		          <p align="right"><hrms:paginationdblink name="kqselfForm" property="pagination" nameId="kqselfForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    
      </table>
      </div>
  </td>
 </tr>
</table>
  
 


  <table  width="100%" align="center">
		<tr>
		    <td align="center">
	 	        <logic:equal name="kqselfForm" property="table" value="Q15">
		 	        <hrms:priv func_id="0B223">
				        <hrms:submit styleClass="mybutton" property="b_add">
		            		<bean:message key="button.new.add"/>
			 	        </hrms:submit>
			    	</hrms:priv>
	 	            <hrms:priv func_id="0B221"> 
	 	            	<input type="button" name="br_return" value='<bean:message key="button.report"/>' class="mybutton" onclick="approves('08')"> 
	 	            </hrms:priv>  
	 	            <hrms:priv func_id="0B222"> 
	 	            	<input type="button" name="br_return" value='<bean:message key="button.appeal"/>' class="mybutton" onclick="approves('02')"> 
	 	            </hrms:priv> 
	 	            <hrms:priv func_id="0B224">
		        		<input type="button" name="br_return" value='<bean:message key="button.delete"/>' class="mybutton" onclick="dels()">
		        	</hrms:priv>  
	 	        </logic:equal> 
	 	        <logic:equal name="kqselfForm" property="table" value="Q11">
		 	        <hrms:priv func_id="0B213">
				        <hrms:submit styleClass="mybutton" property="b_add">
		            		<bean:message key="button.new.add"/>
			 	        </hrms:submit>
			    	</hrms:priv>
	 	            <hrms:priv func_id="0B211"> 
	 	            	<input type="button" name="br_return" value='<bean:message key="button.report"/>' class="mybutton" onclick="approves('08')"> 
	 	            </hrms:priv>  
	 	            <hrms:priv func_id="0B212"> 
	 	            	<input type="button" name="br_return" value='<bean:message key="button.appeal"/>' class="mybutton" onclick="approves('02')"> 
	 	            </hrms:priv>
	 	            <hrms:priv func_id="0B214">
		        		<input type="button" name="br_return" value='<bean:message key="button.delete"/>' class="mybutton" onclick="dels()">
		       		</hrms:priv>   
	 	        </logic:equal> 
	 	        <logic:equal name="kqselfForm" property="table" value="Q13">
		 	        <hrms:priv func_id="0B233">
				        <hrms:submit styleClass="mybutton" property="b_add">
		            		<bean:message key="button.new.add"/>
			 	        </hrms:submit>
			    	</hrms:priv>
	 	            <hrms:priv func_id="0B231"> 
	 	            	<input type="button" name="br_return" value='<bean:message key="button.report"/>' class="mybutton" onclick="approves('08')"> 
	 	            </hrms:priv>  
	 	            <hrms:priv func_id="0B232"> 
	 	            	<input type="button" name="br_return" value='<bean:message key="button.appeal"/>' class="mybutton" onclick="approves('02')"> 
	 	            </hrms:priv>
	 	            <hrms:priv func_id="0B234">
		        		<input type="button" name="br_return" value='<bean:message key="button.delete"/>' class="mybutton" onclick="dels()">
		        	</hrms:priv>   
	 	        </logic:equal> 

                <logic:equal name="kqselfForm" property="table" value="Q11">
                <logic:notEmpty name="kqselfForm" property="field">
                	<hrms:priv func_id="0B215">
	                	<input type="button" value='<bean:message key="kq.self.app.turnTo.overtime"/>' class="mybutton" onclick="turnTo('1');">
                	</hrms:priv>
                	<hrms:priv func_id="0B216">
	                	<input type="button" value='<bean:message key="kq.self.app.turnTo.workingdaysoff"/>' class="mybutton" onclick="turnTo('0');">
                	</hrms:priv>
                </logic:notEmpty>
                </logic:equal>
		        
			</td>
		</tr>
  </table>
</html:form>
