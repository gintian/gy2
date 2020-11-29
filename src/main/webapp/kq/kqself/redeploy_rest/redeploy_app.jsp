<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/popcalendar.js"></script>
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
   var flag_biaozhi = 0; 
   function saveRe(flag)
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
     hashvo.setValue("flag",flag);
     
     hashvo.setValue("z1",document.getElementById("q25z1").value);
     hashvo.setValue("z3",document.getElementById("q25z3").value);
     hashvo.setValue("z1str","当班日期");
     hashvo.setValue("z3str","调休日期");
     var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
     if (flag_biaozhi == 1) {
     var request=new Request({method:'post',onSuccess:saveF,functionId:'1510010052'},hashvo);
     }
   }
   
    function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			flag_biaozhi = 1;
   		} else {
   			flag_biaozhi = 0;
   			alert(resultStr);
   		} 
   }
   
   function saveF(outparamters)
   {
     var class_id=outparamters.getValue("class_id");     
     var flag=outparamters.getValue("flag");
     if(class_id=="0")
     {
     	          var info='';
     if(flag=='02')
     	info='确定要报批吗？';
     else if(flag=='01')
     	info='确定要保存吗？';
     if(confirm(info))
       {
          redeployAppForm.action="/kq/kqself/redeploy_rest/app_redeploy.do?b_save=link&flag="+flag+"&app_flag=add";
          //redeployAppForm.target="il_body";
          redeployAppForm.submit();
          window.opener.window.location.reload();
          window.close(); 
       }
     }else
     {
       alert("调休日期没有休息班次,不能调休！");
       return false;
     }
     
   }
</script>
<html:form action="/kq/kqself/redeploy_rest/app_redeploy">
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
                  <legend ><bean:message key="rsbd.wf.applyemp"/></legend>
                  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.a0101"/> &nbsp;</td>
                      <td height="30" > 
                      <html:text name="redeployAppForm" styleClass="textColorWrite" property="ex_vo.string(a0101)" styleId="a0101" size="10" readonly="true"/>  
                      <html:hidden name="redeployAppForm" property="ex_vo.string(a0100)" styleId="a0100" styleClass="text"/> 
                      <html:hidden name="redeployAppForm" property="ex_vo.string(nbase)" styleId="nbase" styleClass="text"/>
                      <html:hidden name="redeployAppForm" property="ex_vo.string(b0110)" styleId="b0110" styleClass="text"/> 
                      <html:hidden name="redeployAppForm" property="ex_vo.string(e0122)" styleId="e0122" styleClass="text"/>
                      <html:hidden name="redeployAppForm" property="ex_vo.string(e01a1)" styleId="e01a1" styleClass="text"/>
                     </td>
                    <tr> 
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.class"/> &nbsp;</td>
                      <td height="30" > 
                      <html:hidden name="redeployAppForm" property="ex_vo.string(q25z7)" styleId="q25z7"/>  
                      <input name="class_name" class="textColorWrite" type="text" size="10" maxlength="10" id="class_name" readonly="true">
                      </td>
                    <tr> 
                  </table>
                  </fieldset>
             </td>
           
        </tr>        
     </table>        
   </td>
  </tr>
    <tr align="center"> 
      <td height=5></td>
  </tr>
  <tr>
    <td width="100%" align="center" valign="Top" nowrap>
       <fieldset align="center" style="width:90%;">
          <legend ><bean:message key="kq.redeploy.move.date"/></legend>
          <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
              <td height="30" align="center" > <bean:message key="kq.exchange.nonce.date"/> &nbsp;</td>
              <td height="30" >
              <html:text name="redeployAppForm" styleClass="textColorWrite" property="ex_vo.string(q25z1)" styleId="q25z1" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);getClass('c');"/>  
              </td>
            <tr>
            <tr>
              <td height="30" align="center" > <bean:message key="kq.redeploy.move.date"/> &nbsp; </td>
              <td height="30" >
              <html:text name="redeployAppForm" styleClass="textColorWrite" property="ex_vo.string(q25z3)" styleId="q25z3" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);"/>  
              </td>
            </tr> 
          </table>
       </fieldset>
    </td>
  </tr>
  <tr align="center"> 
      <td height="5"></td>
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
            <tr><td height="5"></td></tr>
         </table>
        </fieldset>
        <br>
      </td>
  </tr> 
  <tr><td height="5"></td></tr> 
</table>
<table  width="100%" align="center">
  <tr> 
    <td align="center">     
      <input type="button" name="b_next" value="<bean:message key="button.appeal"/>"  onclick="saveRe('02');" class="mybutton"> 
       <input type="button" name="b_next" value="<bean:message key="button.save"/>"  onclick="saveRe('01');" class="mybutton"> 
      <input type="button" name="b_next" value="<bean:message key="lable.tz_template.cancel"/>" onclick="window.close();" class="mybutton" > 
    </td>
  </tr>
</table>
</div>
</html:form> 
<script language="javascript">
  getClass('c');
</script>