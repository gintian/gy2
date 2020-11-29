<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<%
int i=0;
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
    function usee(f) {
    	if (f ==2 ) {
	   		alert("操作成功！");
	   }
	   if (f == 3) {
	   	alert("${kqInitCodeForm.erro}");
	   }
    }
   var checkflag = "false";
   function allSet()
   {
          var obj=document.getElementById("status");   
	  if( obj.checked==true)
	  {
	    checkflag = "false";
	    selAll();
	    document.getElementById("scope1").checked=true;
	    document.getElementById("scope1").disabled=true;
            document.getElementById("scope2").disabled=true;
            document.getElementById("tstart").disabled=true;
            document.getElementById("tend").disabled=true;
	  }else
	  {
	     checkflag = "true";
	     selAll();
	     document.getElementById("scope1").disabled=false;
             document.getElementById("scope2").disabled=false;
             document.getElementById("tstart").disabled=false;
             document.getElementById("tend").disabled=false;
	  }	   
    }
  var checkflag = "false";

  function selAll()
  {
      var len=document.kqInitCodeForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.kqInitCodeForm.elements[i].type=="checkbox")
            {
              document.kqInitCodeForm.elements[i].checked=true;
              if(document.kqInitCodeForm.elements[i].id!="status")
              {
                document.kqInitCodeForm.elements[i].disabled=true;
              }
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.kqInitCodeForm.elements[i].type=="checkbox")
          {
            document.kqInitCodeForm.elements[i].checked=false;
            document.kqInitCodeForm.elements[i].disabled=false; 
          }
        }
        checkflag = "false";    
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
      var count_start;
      var count_end;
      var obj2=document.getElementById("scope2");
      if(obj2.checked==true)
      {
      		count_start= document.getElementById("tstart").value;
	    	count_end= document.getElementById("tend").value;
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
         if(confirm("您确定要清除冗余数据吗？"))
         {
         	if (!count_start) {
         		count_start = "";
         	}
         	if (!count_end) {
         		count_end = "";
         	}
         	// 加滚动条
			var waitInfo=eval("wait");	   
        	waitInfo.style.display="block";
           	kqInitCodeForm.action="/kq/options/initcode/kq_reddate.do?b_ok=link";
           	kqInitCodeForm.submit();
         }
      }else
      {
         alert("请选择要清除冗余数据的业务表！");
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
</script>
<html:form action="/kq/options/initcode/kq_reddate" onsubmit="return validate()">
	<br>
<table width="480" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		&nbsp;<bean:message key="kq.init.reddate"/>&nbsp;	
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
                  <legend ><bean:message key="kq.init.sel"/> </legend>
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                  <tr >
                   <td align="left" nowrap align="left">        
                    <html:checkbox name="kqInitCodeForm" property="outime" value="1" /> <bean:message key="kq.init.addj"/>        
                   </td>
                   <td align="left" nowrap align="left">        
                    <html:checkbox name="kqInitCodeForm" property="q19" value="1" /> <bean:message key="kq.init.q19"/>        
                   </td>
                   <td align="left" nowrap align="left">        
                   <html:checkbox name="kqInitCodeForm" property="staffl" value="1" /> <bean:message key="kq.init.staff"/>   
                   </td>
                  <td align="left" nowrap align="left">        
                   <html:checkbox name="kqInitCodeForm" property="jqgl" value="1" /> <bean:message key="kq.init.jqgl"/>   
                   </td>
                  </tr>
                  <tr >
                   <td align="left" nowrap align="left">        
                         <html:checkbox name="kqInitCodeForm" property="rest" value="1" /> <bean:message key="kq.init.rest"/>           
                   </td>
                   <td align="left" nowrap align="left">        
                       <html:checkbox name="kqInitCodeForm" property="ypsk" value="1" /> <bean:message key="kq.init.ypsk"/>    
                   </td>
                   <td align="left" nowrap align="left">      
                   <html:checkbox name="kqInitCodeForm" property="staffy" value="1" /> <bean:message key="kq.init.staffy"/>
                   </td>
                   <td align="left" nowrap align="left">      
                   <html:checkbox name="kqInitCodeForm" property="bzry" value="1" /> <bean:message key="kq.init.bzry"/>
                   </td>
                  </tr>    
                  <tr >
                   <td align="left" nowrap align="left">        
                    <html:checkbox name="kqInitCodeForm" property="out" value="1" /> <bean:message key="kq.init.out"/>       
                   </td>
                   <td align="left" nowrap align="left">        
                        <html:checkbox name="kqInitCodeForm" property="txsq" value="1" /> <bean:message key="kq.init.txsq"/> 
                   </td>
                   <td align="left" nowrap align="left">        
                       <html:checkbox name="kqInitCodeForm" property="shift" value="1" /> <bean:message key="kq.init.shift"/>    
                   </td>
                   <td align="left" nowrap align="left">        
                       <html:checkbox name="kqInitCodeForm" property="group" value="1" /> <bean:message key="kq.init.kqbz"/>    
                   </td>
                  </tr> 
                  <tr>
        	    <td align="left" nowrap align="left">          
                       <!--<html:checkbox name="kqInitCodeForm" property="kqorg" value="1" />单位班组排班表   --> 
                   </td>
                   <td align="left" nowrap align="left">        
                       
                   </td>
                  </tr> 
                  <tr>
        	    <td align="left" nowrap align="left" colspan="4">
                    </td>                    
                  </tr>  
                </table>       
	       </fieldset>
              </td>
             </tr>
             <tr>
        	    <td align="left" nowrap valign="left" style="padding-left:27px">
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
                    <td align="left" nowrap align="right">        
                       <html:radio name="kqInitCodeForm" property="scope" value="1" styleId='scope1' onclick="scope_select();"/> <bean:message key="kq.init.allc"/>      
                    </td>
                    <td align="left" nowrap align="right">        
          
                    </td>
                    </tr>  
                    <tr >
                     <td align="left" nowrap align="right">        
                     <html:radio name="kqInitCodeForm" property="scope" value="2" styleId='scope2' onclick="scope_select();"/> <bean:message key="kq.init.tscope"/>&nbsp;&nbsp;&nbsp;      
                     </td>
                     <td align="left" nowrap align="right">        
                       <bean:message key="label.query.from"/><input type="text" name="count_start" value="${kqInitCodeForm.tstart}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="tstart" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
					   <bean:message key="kq.init.tand"/><input type="text" name="count_end" value="${kqInitCodeForm.tend}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="tend" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
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
                      <bean:message key="kq.inti.redundant.data.hint"/>
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
	
		<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style common_background_color" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
</html:form>
<logic:equal name="kqInitCodeForm" property="mess" value="2">
	<script language="javascript">
	   usee('2');
	   
	  //allSet();
 </script>
 </logic:equal>
 <logic:equal name="kqInitCodeForm" property="mess" value="3">
	<script language="javascript">
	   usee('3');
	  //allSet();
 </script>
</logic:equal>
<script type="text/javascript">
<!--
	document.getElementById("scope1").checked=true;
    document.getElementById("tstart").disabled=true;
    document.getElementById("tend").disabled=true;
//-->
</script>

