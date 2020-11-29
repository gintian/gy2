<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.actionform.performance.InitDataForm,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView" %>
<html>  
  <head>
   <%
   	InitDataForm initDataForm=(InitDataForm)session.getAttribute("initDataForm");
    ArrayList    tableList=initDataForm.getTableList();
   String temp=request.getParameter("modelflag");
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
   
   
    %> 
   
  </head>

  <hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script language='javascript' >
  function allSet()
  {
  	var a_value=true;
  	if(!document.initDataForm.all_init.checked)
  		a_value=false;
  	var temps=document.getElementsByName("tabName");
  	for(var i=0;i<temps.length;i++)
  		temps[i].checked=a_value;
  	if(a_value)	
  	{
  		document.initDataForm.timeScope[0].checked=true;
  		document.initDataForm.timeScope[1].disabled=true;
  		document.initDataForm.startDate.value="";
  		document.initDataForm.endDate.value="";
  		document.initDataForm.startDate.disabled=true;
  		document.initDataForm.endDate.disabled=true;
  	}
  	if(!a_value)
  	{
  		document.initDataForm.timeScope[1].disabled=false;
  	}	
  }
  
  function setValue(obj)
  {
  	if(obj.checked)
  	{
  		var temps=document.getElementsByName("tabName");
  		if(obj.value=='per_pointset')
  		{
  			for(var i=0;i<temps.length;i++)
  			{
  				if(temps[i].value=='per_point'||temps[i].value=='per_grade')
	  				temps[i].checked=true;
  			}
  		}
  		if(obj.value=='per_point')
  		{
  			for(var i=0;i<temps.length;i++)
  			{
  				if(temps[i].value=='per_grade')
	  				temps[i].checked=true;
  			}
  		}
  		if(obj.value=='per_template_set')
  		{
  			for(var i=0;i<temps.length;i++)
  			{
  				if(temps[i].value=='per_template'||temps[i].value=='per_template_item'||temps[i].value=='per_template_point')
	  				temps[i].checked=true;
  			}
  		}
  		if(obj.value=='per_template')
  		{
  			for(var i=0;i<temps.length;i++)
  			{
  				if(temps[i].value=='per_template_item'||temps[i].value=='per_template_point')
	  				temps[i].checked=true;
  			}
  		}
  		if(obj.value=='per_template_item')
  		{
  			for(var i=0;i<temps.length;i++)
  			{
  				if(temps[i].value=='per_template_point')
	  				temps[i].checked=true;
  			}
  		}
  		
  	}
  }
  
  
  function scope_select()
  {
  	if(document.initDataForm.timeScope[0].checked==true)
  	{
  		document.initDataForm.startDate.value="";
  		document.initDataForm.endDate.value="";
  		document.initDataForm.startDate.disabled=true;
  		document.initDataForm.endDate.disabled=true;
  	}
  	if(document.initDataForm.timeScope[1].checked==true)
  	{
  		document.initDataForm.startDate.disabled=false;
  		document.initDataForm.endDate.disabled=false;
  	}
  }
  
  
  function enter()
  { 	
  	var hashvo=new ParameterSet();		
    var tabids=new Array();
    for(var i=0;i<document.initDataForm.tabName.length;i++)
    {
    	if(document.initDataForm.tabName[i].checked)
    		tabids[tabids.length]=document.initDataForm.tabName[i].value;
    }
    if(tabids.length==0)
    {
    	alert(P_I_INF15+"！");
    	return;
    }
    if(!confirm(P_I_INF17+"？"))
  		return;
    
    hashvo.setValue("busitype",document.initDataForm.busitype.value);
    hashvo.setValue("tabName",tabids);	
    var timeScope="0";
    if(document.initDataForm.timeScope[1].checked)
    	timeScope="1";
    hashvo.setValue("timeScope",timeScope);
    if(timeScope=="1")
    {
    	 if(document.initDataForm.startDate.value.length>0)
    	 {
    	 	if(!validate(document.initDataForm.startDate,P_STARTDATE))
    	 		return;
    	 }else if(document.initDataForm.startDate.value.length==0)
    	 {
    	 	alert(SELECT_START_TIME);
    	 	return;
    	 }
         if(document.initDataForm.endDate.value.length>0)
    	 {
    	 	if(!validate(document.initDataForm.endDate,P_ENDDATE))
    	 		return;
    	 }else if(document.initDataForm.endDate.value.length==0)
    	 {
    	 	alert(SELECT_END_TIME);
    	 	return;
    	 }
    	 
    	 var stime=document.initDataForm.startDate.value;
 		 var etime=document.initDataForm.endDate.value;
    	 
    	var reg = /^(\d{4})((-|\.)(\d{1,2}))((-|\.)(\d{1,2}))$/;;
		if(!reg.test(stime))
		{
			alert(STARTTIME_FORMAT+"！");
			return;
		}
		if(!reg.test(etime))
		{
			alert(ENDTIME_FORMAT+"！");
			return;
		}
		var syear = stime.substring(0,4);
		var smonth=stime.substring(5,7);
		var sday=stime.substring(8);

		var eyear = etime.substring(0,4);
		var emonth=etime.substring(5,7);
		var eday=etime.substring(8);
	
		if(syear>eyear||(syear==eyear&&smonth>emonth)||(syear==eyear&&smonth==emonth&&sday>eday))
		{
		    alert(ENDTIME_LARGER_STARTTIME+"！");
		    return;
		}
		    	 
    	 hashvo.setValue("startDate",document.initDataForm.startDate.value);
    	 hashvo.setValue("endDate",document.initDataForm.endDate.value);
    }
    var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'90100150008'},hashvo);
  }
  
  function returnInfo(outparamters)
  {
  		alert(P_I_INF16+"!")
  }
  
  
  </script>
  
  <body>
   <html:form action="/performance/per_initData">
   <html:hidden name="initDataForm" property="busitype"/>
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		&nbsp;&nbsp;<bean:message key="jx.options.performancedataInit" />	
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
            <br>
            <table align="center" width="100%">
             <tr>
              <td>
               <fieldset align="center" style="width:95%;">
                  <legend ><bean:message key="kq.init.select" /></legend>
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                 
                 <% for(int i=0;i<tableList.size();i++){ 
                 		if(i==0)
                 			out.print("<tr>");
                 		if(i!=0&&i%4==0)
                 			out.print("</tr><tr>");
                 		LazyDynaBean abean=(LazyDynaBean)tableList.get(i);
                 		String id=(String)abean.get("id");
                 		String desc=(String)abean.get("desc");
                 		
                 %>
                
                  
                   <td align="left" nowrap valign="left">        
                    <input type="checkbox"  onclick='setValue(this)'  name="tabName" value="<%=id%>" ><%=desc%>        
                   </td>
                  <% } %>
                  </tr>
                  
                  
                  
                  
                    
                </table>       
	       </fieldset>
              </td>
             </tr>
             <tr>
        	    <td > 
        	    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        	     <input type="checkbox" name="all_init" value="1" onclick="allSet();" id="status"> 
        	     <bean:message key="jx.options.allDataInit" />	
                    </td>                    
                  </tr>
             <tr>
             <td>
             <br>
                <fieldset align="center" style="width:95%;">
                <legend ><bean:message key="kq.init.tscope" /> </legend>
                  <table border="0" cellspacing="0"  align="center" cellpadding="2" >
                   <tr >
                    <td align="left" nowrap valign="right">        
                       <input type="radio" name="timeScope" value="0" checked="checked" onclick="scope_select();" id="scope1"><bean:message key="kq.init.allc" />     
                    </td>
                    <td align="left" nowrap valign="right">        
          
                    </td>
                    </tr>  
                    <tr >
                     <td align="left" nowrap valign="right">        
                     <input type="radio" name="timeScope" value="1" onclick="scope_select();" id="scope2"><bean:message key="jx.khplan.timeframe" />&nbsp;&nbsp;&nbsp;      
                     </td>
                     <td align="left" nowrap valign="right">        
                       <bean:message key="kq.rule.from" />
                       <!--  <input type="text" style="display:none" name="startDate" disabled  maxlength="10" size="10" value="" onfocus="inittime(false);setday(this);" id="tstart">-->
                       <input type="text" name="startDate" disabled value="${initDataForm.startDate}"  extra="editor" style="width:100px;font-size:10pt;text-align:left" id="tstart" dropDown="dropDownDate">
                       <bean:message key="kq.init.tand" />
                       <!-- <input type="text" style="display:none" name="endDate" disabled maxlength="10" size="10" value="" onfocus="inittime(false);setday(this);" id="tend">-->
                       <input type="text" name="endDate" disabled value="${initDataForm.endDate}" extra="editor" style="width:100px;font-size:10pt;text-align:left" id="tend" dropDown="dropDownDate">
                     </td>
                   </tr>  
                 </table>
	     </fieldset>
             </td>
             </tr>
             <tr>
              <td align="center" width="100%" height="30" valign="middle">
                 <table border="0" cellspacing="0" cellpadding="2" width="95%">
                   <tr>
                    <td width="80%">
                      &nbsp;&nbsp;
                      <font color="black">
                      <bean:message key="kq.inti.clew" />
                      </font>
                    </td>                   
                   </tr>
	         </table>
              </td>
              </tr>
            </table> 
          </td>
        </tr> 
 </table>	
 
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<tr>                   
    	<td align="center" style="height:35px">                      
	    	<input type="button" name="b_next" value="<bean:message key="reporttypelist.confirm" />"  onclick="enter();" class="mybutton"> 
	    								 <%if("capability".equals(temp)){ %>
         <hrms:tipwizardbutton flag="capability" target="il_body" formname="initDataForm"/>  
         <%}else if("performance".equals(temp)){ %>
         <hrms:tipwizardbutton flag="performance" target="il_body" formname="initDataForm"/>  
         <%} %> 

        </td>
	</tr>
</table>  
   
   </html:form>
  </body>
</html>
