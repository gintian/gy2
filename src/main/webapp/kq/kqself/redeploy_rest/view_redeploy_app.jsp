<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/popcalendar2.js"></script>
<script type="text/javascript">
<!--

function getSelectedEmploy()
{
	 var flag,selecttype,dbtype,priv;
	 flag=1;
	 selecttype=2;
	 dbtype=1;         
         if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
        var theurl="/kq/app_check_in/exchange_class/app_emp.do?flag="+flag+"&selecttype="+selecttype+"&dbtype="+dbtype+
                "&priv="+priv;
        var return_vo= window.showModalDialog(theurl,1, 
           "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo; 
	
}
var emp_flag="";
function getEmp(flag)
{
   var return_vo=getSelectedEmploy();   
   emp_flag=flag;
   if(return_vo)
   {
     var hashvo=new ParameterSet();
     var oper_id=window.dialogArguments;       
     hashvo.setValue("a0100",return_vo.content);    
     var request=new Request({asynchronous:false,onSuccess:link_success,functionId:'1510010051'},hashvo);        
   
   }
}
function link_success(outparamters)
{
    var userinfo=outparamters.getValue("userinfo");
    
       var Obj=document.getElementById("a0100");
       Obj.value=userinfo.a0100;
       Obj=document.getElementById("nbase");
       Obj.value=userinfo.nbase;
       Obj=document.getElementById("b0110");
       Obj.value=userinfo.b0110;
       Obj=document.getElementById("e0122");
       Obj.value=userinfo.e0122;
       Obj=document.getElementById("e01a1");
       Obj.value=userinfo.e01a1;
       Obj=document.getElementById("a0101");
       Obj.value=userinfo.name;	    	
    getClass(emp_flag);	
}
var class_flag;
function getClass(flag)
{
  class_flag=flag;
  var a0100,nbase,date;
  
    a0100=document.getElementById("a0100").value;
    nbase=document.getElementById("nbase").value;
    date=document.getElementById("q25z1").value;    
 
  var hashvo=new ParameterSet();
  hashvo.setValue("a0100",a0100);
  hashvo.setValue("nbase",nbase);
  hashvo.setValue("date",date);
   var request=new Request({asynchronous:false,onSuccess:setClassName,functionId:'1510010052'},hashvo);        
}
function setClassName(outparamters)
{
  var class_name=outparamters.getValue("class_name");
  var class_id=outparamters.getValue("class_id");
 
    var Obj=document.getElementById("class_name");
    Obj.value=class_name;
    Obj=document.getElementById("q25z7");
    Obj.value=class_id; 
}
function rep_dateValue(obj)
{
  var d_value=obj.value;
  if(d_value!="")
  {
    d_value=d_value.replace("-",".");
    d_value=d_value.replace("-",".");
    obj.value=d_value;
  }
}
//-->
</script>
<script language="javascript">
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   function getdate(tt,flag)
   {
     outObject=tt;     
     var hashvo=new ParameterSet();     
     hashvo.setValue("date",tt.value);  
     hashvo.setValue("flag",flag);     		
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1510010020'},hashvo);
   }
   function showSelect(outparamters)
   { 
     var tes=outparamters.getValue("re_date");     
     outObject.value=tes;
   }
   function getKqCalendarVar()
   {
     var request=new Request({method:'post',onSuccess:setKqCalendarVar,functionId:'15388800008'});
   }
   function setKqCalendarVar(outparamters)
   {
       weeks=outparamters.getValue("weeks");  
       feasts=outparamters.getValue("feasts");  
       turn_dates=outparamters.getValue("turn_dates");  
       week_dates=outparamters.getValue("week_dates");  
   } 
   function saveRe()
   {
     var obj=document.getElementById("a0101");
     var o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("当班人姓名不能为空！");
        return false;
     }
     obj=document.getElementById("class_name");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("当班人班次不能为空！");
        return false;
     }    
     obj=document.getElementById("q25z1");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("当班日期不能为空！");
        return false;
     }
     obj=document.getElementById("q25z3");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("调休日期不能为空！");
        return false;
     }     
     var hashvo=new ParameterSet();  
     var obj_v=document.getElementById("a0100");  
     hashvo.setValue("a0100",obj_v.value);  
     obj_v=document.getElementById("nbase");       
     hashvo.setValue("nbase",obj_v.value); 
     obj_v=document.getElementById("q25z3");       
     hashvo.setValue("date",obj_v.value);
     var request=new Request({method:'post',onSuccess:saveF,functionId:'1510010052'},hashvo);
   }
   function saveF(outparamters)
   {
     var class_id=outparamters.getValue("class_id");     
     if(class_id=="0")
     {
       if(confirm("确实要保存吗！"))
       {
          redeployAppForm.action="/kq/app_check_in/redeploy_rest/app_redeploy.do?b_save=link&app_flag=edit";
          redeployAppForm.target="mil_body";
          redeployAppForm.submit();
          window.close(); 
       }
     }else
     {
       alert("调休日期没有休息班次,不能调休！");
       return false;
     }
     
   }
</script>
<html:form action="/kq/app_check_in/redeploy_rest/app_redeploy">
<div  class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="RecordRow" style="border-style:solid;border-width:1px;" >
 <thead>
   <tr>
    <td align="left" class="TableRow" nowrap>
	    <bean:message key="kq.redeploy.app.name"/>&nbsp;&nbsp;	
    </td>            	        	        	        
   </tr>
  </thead>
  <tr>
   <td width="100%" align="center" valign="Top" nowrap>
     <table border="0" cellspacing="1" cellpadding="1" width="100%">
        <tr> 
          <td width="100%"> <fieldset align="center" style="width:90%;">
                  <legend ><bean:message key="kq.exchange.emp.nonce"/></legend>
                  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.a0101"/>: </td>
                      <td height="30" >                      
                      <bean:write name="redeployAppForm" property="ex_vo.string(a0101)" filter="false"/>&nbsp;  
                      </td>
                    <tr> 
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.class"/>: </td>
                      <td height="30" >                      
                      <bean:write name="redeployAppForm" property="class_name" filter="false"/>&nbsp;  
                      </td>
                    <tr> 
                  </table>
                  </fieldset>
             </td>
           
        </tr>        
     </table>        
   </td>
  </tr>
  <tr>
    <td width="100%" align="center" valign="Top" nowrap>
    <br>
       <fieldset align="center" style="width:90%;">
          <legend ><bean:message key="kq.redeploy.move.date"/></legend>
          <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
              <td height="30" align="center" > <bean:message key="kq.exchange.nonce.date"/> &nbsp;</td>
              <td height="30" >              
              <bean:write name="redeployAppForm" property="ex_vo.string(q25z1)" filter="false"/>&nbsp;  
              </td>
              <td height="30" align="center" > <bean:message key="kq.redeploy.move.date"/>&nbsp; </td>
              <td height="30" >              
              <bean:write name="redeployAppForm" property="ex_vo.string(q25z3)" filter="false"/>&nbsp;  
              </td>
            <tr> 
          </table>
       </fieldset>
    </td>
  </tr>
  <tr align="center"> 
     <td height="20">
	<fieldset align="center" style="width:90%;">
        <legend ><bean:message key="kq.redeploy.reason"/></legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td height="70" align="center" >                 
                  <html:textarea name="redeployAppForm" property="ex_vo.string(q2507)" cols="50" rows="5" styleClass="text5"/>
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
  <tr align="center"> 
     <td>
     <br>
	<fieldset align="center" style="width:90%;">
        <legend ><bean:message key="kq.app.org.mind"/></legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  <bean:message key="kq.app.dept.lead"/>:&nbsp;  
               </td>
               <td class="tdFontcolor" nowrap >               
               <bean:write name="redeployAppForm" property="ex_vo.string(q2509)" filter="false"/>&nbsp;  
               </td>
            <tr> 
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  <bean:message key="kq.app.dept.mind"/>:&nbsp;  
               </td>
               <td  class="tdFontcolor" nowrap >  
              <bean:write name="redeployAppForm" property="ex_vo.string(q2511)" filter="false"/>&nbsp;  
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  <bean:message key="kq.app.unit.lead"/>:&nbsp; 
               </td>
               <td  class="tdFontcolor" nowrap >          
               <bean:write name="redeployAppForm" property="ex_vo.string(q2513)" filter="false"/>&nbsp;                 
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                  <bean:message key="kq.app.unit.mind"/>:&nbsp; 
               </td>
               <td class="tdFontcolor" nowrap >  
                 <bean:write name="redeployAppForm" property="ex_vo.string(q2515)" filter="false"/>&nbsp;  
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
</table>
<table  width="100%" align="center">
  <tr> 
    <td align="center">        
      <input type="button" name="b_next" value="<bean:message key="button.close"/>" onclick="window.close();" class="mybutton" > 
    </td>
  </tr>
</table>
</div>
</html:form> 