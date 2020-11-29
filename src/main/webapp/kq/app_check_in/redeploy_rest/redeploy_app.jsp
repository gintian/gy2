<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/kq/kq.js"></script>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 585px;height: 90%;
 line-height:15px; 
 border-width:0px; 
 margin-top:5px;
 
/* border-style: groove;
 border-width :thin ;*/
}
</STYLE>
<script type="text/javascript">
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
     var content=return_vo.content;
     if(content.indexOf("UN")!=-1||content.indexOf("UM")!=-1||content.indexOf("@K")!=-1)
     {
        alert("请选择人员！");
        return false;
     }
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
     hashvo.setValue("z3str","调休日期");
     obj_v=document.getElementById("q25z1");       
     hashvo.setValue("z1",obj_v.value);
     hashvo.setValue("z1str","当班日期");
     var request=new Request({method:'post',onSuccess:saveF,functionId:'1510010052'},hashvo);
   }
	function saveF(outparamters) {
   		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
     		var class_id=outparamters.getValue("class_id");     
     		if(class_id=="0") {
       			if(confirm("确定要报批吗？")) {
		          redeployAppForm.action="/kq/app_check_in/redeploy_rest/app_redeploy.do?b_save=link&app_flag=add";
		          //redeployAppForm.target="mil_body";
		          redeployAppForm.submit();
		          var thevo=new Object();
                  thevo.flag="true";
                  window.returnValue=thevo;     
                  window.close();
                  window.close();
		          window.close(); 
       			}
     		}else {
		       alert("调休日期没有休息班次,不能调休！");
		       return false;
     		}
     	} else {
     		alert(resultStr);
     	}
     
   }
</script>
<html:form action="/kq/app_check_in/redeploy_rest/app_redeploy">
<div id="d" class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="border:1px solid #C4D8EE;" class="common_border_color">
 <thead>
   <tr>
    <td align="center" class="TableRow" nowrap style="border-left:0px solid #C4D8EE;border-right:0px solid #C4D8EE;border-top:0px solid #C4D8EE;">
	    <bean:message key="kq.redeploy.app.name"/>&nbsp;&nbsp;	
    </td>            	        	        	        
   </tr>
  </thead>
  <tr>
   <td width="100%" align="center" valign="Top" nowrap >
 	<fieldset align="center" style="width:94%;">
      <legend ><bean:message key="kq.exchange.emp.nonce"/></legend>
      <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
        <tr> 
          <td height="30" width="230" align="center" > <bean:message key="kq.exchange.a0101"/>  </td>
          <td height="30" > 
          <html:text styleClass="inputtext" name="redeployAppForm" property="ex_vo.string(a0101)" styleId="a0101" size="10" onclick="getEmp('c');" readonly="true"/>  
          &nbsp;<a href="###" onclick="getEmp('c');"><img src="/images/role_assign.gif" border=0></a>
          <html:hidden name="redeployAppForm" property="ex_vo.string(a0100)" styleId="a0100" styleClass="text"/> 
          <html:hidden name="redeployAppForm" property="ex_vo.string(nbase)" styleId="nbase" styleClass="text"/>
          <html:hidden name="redeployAppForm" property="ex_vo.string(b0110)" styleId="b0110" styleClass="text"/> 
          <html:hidden name="redeployAppForm" property="ex_vo.string(e0122)" styleId="e0122" styleClass="text"/>
          <html:hidden name="redeployAppForm" property="ex_vo.string(e01a1)" styleId="e01a1" styleClass="text"/>
         </td>
        <tr> 
        <tr> 
          <td height="30" align="center" > <bean:message key="kq.exchange.class"/>  </td>
          <td height="30" > 
          <html:hidden name="redeployAppForm" property="ex_vo.string(q25z7)" styleId="q25z7"/>  
          <input Class="inputtext" name="class_name" type="text" size="10" maxlength="10" id="class_name" readonly="true">
          </td>
        <tr> 
      </table>
     </fieldset>
   </td>
  </tr>

  <tr align="center">
    <td width="100%" align="center" valign="Top" nowrap>
    <br>
       <fieldset align="center" style="width:94%;">
          <legend ><bean:message key="kq.redeploy.move.date"/></legend>
          <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr>
              <td height="30" align="center" width="230" >  <bean:message key="kq.exchange.nonce.date"/> </td>
              <td height="30" > 
              <html:text styleClass="inputtext" name="redeployAppForm" property="ex_vo.string(q25z1)" styleId="q25z1" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);getClass('c');" readonly="true"/>  
              </td>
            <tr>
            <tr>
              <td height="30" align="center" > <bean:message key="kq.redeploy.move.date"/>  </td>
              <td height="30" > 
              <html:text styleClass="inputtext" name="redeployAppForm" property="ex_vo.string(q25z3)" styleId="q25z3" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" readonly="true"/>  
              </td>
            </tr> 
          </table>
       </fieldset>
    </td>
  </tr>
  <tr align="center"> 
     <td height="20">
     <br/>
	<fieldset align="center" style="width:94%;">
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
	<fieldset align="center" style="width:94%;">
        <legend >意见</legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审核人&nbsp;  
               </td>
               <td class="tdFontcolor" nowrap > 
                <html:text styleClass="inputtext" name="redeployAppForm" property="ex_vo.string(q2509)"/>  
               </td>
            <tr> 
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审核人意见&nbsp;  
               </td>
               <td  class="tdFontcolor" nowrap >  
               <html:textarea name="redeployAppForm" property="ex_vo.string(q2511)" cols="41" rows="3" styleClass="text5"/>
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审批人&nbsp; 
               </td>
               <td  class="tdFontcolor" nowrap >          
               <html:text styleClass="inputtext" name="redeployAppForm" property="ex_vo.string(q2513)"/>                 
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审批人意见&nbsp; 
               </td>
               <td class="tdFontcolor" nowrap >  
                 <html:textarea name="redeployAppForm" property="ex_vo.string(q2515)" cols="41" rows="3" styleClass="text5"/>
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
  <tr><td height=10></td></tr>
</table>
</div>
<table  width="100%" align="center">
  <tr> 
    <td align="center" style="height:35px;"> 
      <input type="button" name="b_next" value="<bean:message key="button.ok"/>"  onclick="saveRe();" class="mybutton"> 
      <input type="button" name="b_next" value="<bean:message key="lable.tz_template.cancel"/>" onclick="window.close();" class="mybutton" > 
    </td>
  </tr>
</table>
</html:form> 