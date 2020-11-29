<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<% int i=0;
%>
<script language="JavaScript" >
  function validate()
  {
	 var ro=$F('scope');
	 if(ro=="2")
	 {
	     var str11,str22;   
	     var expr=$F('tend');
         var expr2=$F('tstart');
         str11=new Date(expr2.replace("-",",")).getTime();
         str22=new Date(expr.replace("-",",")).getTime();
         if(str22<str11)
         {
            alert("起始时间大于结束时间！");
            return false;
         }
      }
  }
  
  function usee()
  {
      alert("操作成功，数据已删除！");
  }

  function selectAll(){
	  var obj = document.getElementById("selAll");
	  var len=document.kqInitCodeForm.elements.length;
      var i;
	  if(obj.checked == true)
	  {
	      for (i=0;i<len;i++)
	      {
	         if (document.kqInitCodeForm.elements[i].type=="checkbox")
	          {
	            document.kqInitCodeForm.elements[i].checked=true;
	          }
	      }
	  }else
	  {
		  for (i=0;i<len;i++)
	      {
	         if (document.kqInitCodeForm.elements[i].type=="checkbox")
	          {
	            document.kqInitCodeForm.elements[i].checked=false;
	          }
	      }
      }
  }
  function saveRe()
  {
       var len=document.kqInitCodeForm.elements.length;
       var i;
       var flag="false";
       for (i=0;i<len;i++)
       {
           if (document.kqInitCodeForm.elements[i].type=="checkbox")
           {
             if(document.kqInitCodeForm.elements[i].checked==true)
             {
                 flag="true";
             }
           }
      }
      var count_start = "";
      var count_end = "";
      var obj2=document.getElementById("scope2");
      if(obj2.checked==true)
      {
      		count_start=document.kqInitCodeForm.tstart.value;
	    	count_end=document.kqInitCodeForm.tend.value;
	    	if(!isDate(count_start,"yyyy-MM-dd"))
	      	{
	          alert("开始时间格式错误！");
              return;
	      	}else if(!isDate(count_end,"yyyy-MM-dd"))
	      	{
	          alert("结束时间格式错误！");
              return;
	      	}
	      	
	    	// 开始时间不能大于结束时间
	      	if (count_start > count_end) {
	      		alert("开始时间不能大于结束时间！");
	      		return;
	      	}
      }
      if(flag=="true")
      {
         if(confirm("您确定要删除所选表的数据吗？"))
         {
         
           kqInitCodeForm.action="/kq/options/initcode/kq_initcode.do?b_ok=link&count_start="+count_start+"&count_end="+count_end;
           kqInitCodeForm.submit();
         }
      }else
      {
         alert("请选择删除数据的业务表！");
         return false;
      }
  }
  function scope_select()
  {
     var obj1=document.getElementById("scope1"); 
     var obj2=document.getElementById("scope2");
     if(obj1.checked==true)
     {
         
         document.getElementById("tstart").disabled=true;
         document.getElementById("tend").disabled=true;
     }
      if(obj2.checked==true)
     {
         document.getElementById("scope2").disabled=false;
         document.getElementById("tstart").disabled=false;
         document.getElementById("tend").disabled=false;
     }
  }
</script>
<html:form action="/kq/options/initcode/kq_initcode" onsubmit="alert('ddf');return validate()">
	<br>
<table width="480" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		&nbsp;<bean:message key="kq.init.code"/>&nbsp;	
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
                  <legend ><bean:message key="kq.init.select"/> </legend>
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                  <tr>
                   <td align="left" nowrap valign="left">        
                    <html:checkbox name="kqInitCodeForm" property="out" value="1" /><bean:message key="kq.init.out"/>        
                   </td>
                   <td align="left" nowrap valign="left">        
                    <html:checkbox name="kqInitCodeForm" property="q19" value="1" /><bean:message key="kq.init.q19"/>         
                   </td>
                   <td align="left" nowrap valign="left">        
                    <html:checkbox name="kqInitCodeForm" property="staffl" value="1" /><bean:message key="kq.init.staff"/>        
                   </td>
                   <td align="left" nowrap valign="left">        
                   <html:checkbox name="kqInitCodeForm" property="deptl" value="1" /><bean:message key="kq.init.dept"/>   
                             
                   </td>
                  </tr>
                  <tr >
                   <td align="left" nowrap valign="left">        
                         <html:checkbox name="kqInitCodeForm" property="rest" value="1" /><bean:message key="kq.init.rest"/>           
                   </td>
                   <td align="left" nowrap valign="left">
                   	<html:checkbox property="dxjb" name="kqInitCodeForm" value="1"/><bean:message key="kq.init.txjb"/>
                   </td>
                   <td align="left" nowrap valign="left">        
                    <html:checkbox name="kqInitCodeForm" property="staffy" value="1" /><bean:message key="kq.init.staffy"/>           
                   </td>
                   <td align="left" nowrap valign="left">      
                   <html:checkbox name="kqInitCodeForm" property="depty" value="1" /><bean:message key="kq.init.depty"/>
                        
                   </td>
                  </tr>    
                  <tr >
                   <td align="left" nowrap valign="left">        
                    <html:checkbox name="kqInitCodeForm" property="outime" value="1" /><bean:message key="kq.init.addj"/>       
                   </td>
                   <td align="left" nowrap valign="left">        
                    <html:checkbox name="kqInitCodeForm" property="txsq" value="1" /><bean:message key="kq.init.txsq"/>         
                   </td>
                   <td align="left" nowrap valign="left">        
                        <html:checkbox name="kqInitCodeForm" property="shift" value="1" /><bean:message key="kq.init.shift"/>        
                   </td>
                   <td align="left" nowrap valign="left">        
                       <html:checkbox name="kqInitCodeForm" property="ypsk" value="1" /><bean:message key="kq.init.ypsk"/>    
                   </td>
                  </tr> 
                  <tr>
        	    <td align="left" nowrap valign="left">  
        	     <html:checkbox name="kqInitCodeForm" property="jqgl" value="1" /><bean:message key="kq.init.jqgl"/>       
                    </td>
                    <td align="left" nowrap valign="left">      
                       <html:checkbox name="kqInitCodeForm" property="kqbz" value="1" /><bean:message key="kq.init.kqbz"/>     
                    </td>
                    <td align="left" nowrap valign="left">        
                       <html:checkbox name="kqInitCodeForm" property="bzry" value="1" /><bean:message key="kq.init.bzry"/>    
                   </td>
                   <td align="left" nowrap valign="left">        
                       <html:checkbox name="kqInitCodeForm" property="kqorg" value="1" /><bean:message key="kq.init.kqorg"/>   
                   </td>
                  </tr> 
                  <tr>
                   <td align="left" nowrap valign="left">        
                       <html:multibox name="kqInitCodeForm" property="all_init" value="1" styleId='status'/><bean:message key="kq.init.all_init"/>
                   </td>
                   <td align="left" nowrap valign="left">        
                       <html:multibox name="kqInitCodeForm" property="kqCard" value="1" styleId='status'/><bean:message key="kq.init.kqCard"/>
                   </td>
                   <td align="left" nowrap valign="left">        
                       <html:multibox name="kqInitCodeForm" property="kqType" value="1" styleId='status'/><bean:message key="kq.init.kqType"/>
                   </td>
                  </tr>  
                </table>       
	       </fieldset>
              </td>
             </tr>
             <tr>
        	    <td align="left" nowrap valign="left">&nbsp;&nbsp;&nbsp;
        	    	<input type="checkbox" name="selAll" id="selAll" value="1" onclick="selectAll();"/><bean:message key="label.query.selectall"/>
                </td>                    
             </tr>
             <tr>
             <td>
             <br>
                <fieldset align="center" style="width:95%;">
                <legend ><bean:message key="kq.init.tscope"/> </legend>
                  <table border="0" cellspacing="0"  align="center" cellpadding="2" >
                   <tr >
                    <td align="left" nowrap valign="right">        
                       <html:radio name="kqInitCodeForm" property="scope" value="1" styleId='scope1' onclick="scope_select();"/><bean:message key="kq.init.allc"/>      
                    </td>
                    <td align="left" nowrap valign="right">        
          
                    </td>
                    </tr>  
                    <tr >
                     <td align="left" nowrap valign="right">        
                     <html:radio name="kqInitCodeForm" property="scope" value="2" styleId='scope2' onclick="scope_select();"/><bean:message key="kq.init.tscope"/>&nbsp;&nbsp;&nbsp;      
                     </td>
                     <td align="left" nowrap valign="right">        
                       <bean:message key="label.query.from"/><input type="text" name="kqInitCodeForm" value="${kqInitCodeForm.tstart}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="tstart" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
					   <bean:message key="kq.init.tand"/><input type="text" name="kqInitCodeForm" value="${kqInitCodeForm.tend}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="tend" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
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
                      <bean:message key="kq.inti.clew"/>
                      </font>
                    </td>
                    <td align="left">                    
	           
                    </td>
                   </tr>
	         </table>
              </td>
              </tr>
            </table> 
          </td>
        </tr> 
 </table>	
  <table align="center">
  	<tr>
  		<td style="height:35px;">
  		 <input type="button" name="b_next" value="<bean:message key="button.ok"/>"  onclick="saveRe();" class="mybutton">
	            <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqInitCodeForm"/> 
  		</td>
  	</tr>
  </table>
	
</html:form>
<logic:equal name="kqInitCodeForm" property="mess" value="2">
	<script language="javascript">
	   usee();
 </script>
</logic:equal>
<script type="text/javascript">
	document.getElementById("scope1").checked=true;
    document.getElementById("tstart").disabled=true;
    document.getElementById("tend").disabled=true;
</script>

