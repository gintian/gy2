<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 585px;height: 90%;
 line-height:15px; 
 border-width:0px; 
 margin-top:5px;
 /*border-style: groove;
 border-width :thin ;*/
 
}
</STYLE>
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
    if(emp_flag=="c")
    {
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
    }else
    {
      var Obj=document.getElementById("ex_nbase");
      Obj.value=userinfo.nbase;
      Obj=document.getElementById("q19a1");
      Obj.value=userinfo.name;	
       Obj=document.getElementById("q19a0");
      Obj.value=userinfo.a0100;	
    }	
    getClass(emp_flag);	
}
var class_flag;
function getClass(flag)
{
  class_flag=flag;
  var a0100,nbase,date;
  if(class_flag=="c")
  {
    a0100=document.getElementById("a0100").value;
    nbase=document.getElementById("nbase").value;
    date=document.getElementById("q19z1").value;
   
    
  }else if(class_flag=="e")
  {
    a0100=document.getElementById("q19a0").value;
    nbase=document.getElementById("ex_nbase").value;
    date=document.getElementById("q19z3").value;
  }
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
  if(class_flag=="c")
  {
    var Obj=document.getElementById("class_name");
    Obj.value=class_name;
    Obj=document.getElementById("q19z7");
    Obj.value=class_id;
  }else if(class_flag=="e")
  {
    var Obj=document.getElementById("exclass_name");
    Obj.value=class_name;
    Obj=document.getElementById("q19z9");
    Obj.value=class_id;
  }
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
     obj=document.getElementById("q19a1");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("调班人姓名不能为空！");
        return false;
     }
     obj=document.getElementById("exclass_name");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("调班人班次不能为空！");
        return false;
     }
     obj=document.getElementById("q19z1");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("当班日期不能为空！");
        return false;
     }
     obj=document.getElementById("q19z3");
     o_value=obj.value;
     if(o_value==""||o_value.length<=0)
     {
        alert("调班日期不能为空！");
        return false;
     }
     obj=document.getElementById("nbase");
     var e_obj=document.getElementById("ex_nbase");
     o_value=obj.value;
     var e_value=e_obj.value;
     if(o_value!=e_value)
     {
        alert("双方必须是同一人员库！");
        return false;
     }
     
	var hashvo=new ParameterSet();
  	hashvo.setValue("z1",document.getElementById("q19z1").value);
  	hashvo.setValue("z1str","当班日期");
  	hashvo.setValue("z3",document.getElementById("q19z3").value);
  	hashvo.setValue("z3str","调班日期");
   	var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
   
     
   }
   function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			if(confirm("确定要报批吗？")) {
         		exchangeAppForm.action="/kq/app_check_in/exchange_class/app_exchange.do?b_save=link&app_flag=add";
		        exchangeAppForm.submit();
		        var thevo=new Object();
                thevo.flag="true";
                window.returnValue=thevo;     
                window.close();
      		}
   		} else {
   			alert(resultStr);
   		} 
   }
</script>
<html:form action="/kq/app_check_in/exchange_class/app_exchange">
<div id="d" class="fixedDiv2" style="height: 100%;border: none">

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="framestyle">
 <thead>
   <tr>
    <td align="center" class="TableRow" nowrap style="border-top:0px #C4D8EE solid;border-right:0px #C4D8EE solid;border-left:0px #C4D8EE solid;">
	    <bean:message key="kq.exchange.app.name"/>&nbsp;&nbsp;	
    </td>            	        	        	        
   </tr>
  </thead>
   <tr>
   <td width="100%" align="center" valign="Top" nowrap>
     <table border="0" cellspacing="1" cellpadding="1" width="90%">
        <tr> 
          <td width="42%"> <fieldset align="center" style="width:100%;">
                  <legend ><bean:message key="kq.exchange.emp.nonce"/></legend>
                  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.a0101"/> </td>
                      <td height="30" > 
                      <html:text styleClass="inputtext" name="exchangeAppForm" property="ex_vo.string(a0101)" styleId="a0101" size="10" onclick="getEmp('c');" readonly="true"/>  
                      &nbsp;<a href="###" onclick="getEmp('c');"><img src="/images/role_assign.gif" border=0></a>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(a0100)" styleId="a0100" styleClass="text"/> 
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(nbase)" styleId="nbase" styleClass="text"/>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(b0110)" styleId="b0110" styleClass="text"/> 
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(e0122)" styleId="e0122" styleClass="text"/>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(e01a1)" styleId="e01a1" styleClass="text"/>
                     </td>
                    <tr> 
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.class"/> </td>
                      <td height="30" > 
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(q19z7)" styleId="q19z7"/>  
                      <html:text styleClass="inputtext" name="exchangeAppForm" property="class_name" size="10" styleId="class_name"/>
                      </td>
                    <tr> 
                  </table>
                  </fieldset>
           </td>
           <td width="6%">&nbsp;</td>
           <td width="42%"> <fieldset align="center" style="width:100%;">
                  <legend ><bean:message key="kq.exchange.emp.exchange"/></legend>
                  <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.a0101"/> </td>
                      <td height="30" >                       
                      <html:text styleClass="inputtext" name="exchangeAppForm" property="ex_vo.string(q19a1)" styleId="q19a1" size="10" onclick="getEmp('e');" readonly="true"/>  
                      &nbsp;<a href="###" onclick="getEmp('e');"><img src="/images/role_assign.gif" border=0></a>
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(q19a0)" styleId="q19a0" styleClass="text"/> 
                      <input name="ex_nbase" type="hidden" styleId="ex_nbase" value="<bean:write name="exchangeAppForm" property="ex_vo.string(nbase)" filter="false"/>">
                      </td>
                    <tr> 
                    <tr> 
                      <td height="30" align="center" > <bean:message key="kq.exchange.class"/> </td>
                      <td height="30" > 
                      <html:text styleClass="inputtext" name="exchangeAppForm" property="exclass_name" styleId="exclass_name" size="10" />
                      <html:hidden name="exchangeAppForm" property="ex_vo.string(q19z9)" styleId="q19z9"/>  
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
          <legend ><bean:message key="kq.exchange.move.date"/></legend>
          <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
              <td height="30" align="center" > <bean:message key="kq.exchange.nonce.date"/> </td>
              <td height="30" >
              <html:text name="exchangeAppForm" styleClass="inputtext" property="ex_vo.string(q19z1)" styleId="q19z1" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);getClass('c');" readonly="true"/>  
              </td>
              <td height="30" align="center" > <bean:message key="kq.exchange.move.date"/> </td>
              <td height="30" >
              <html:text name="exchangeAppForm" styleClass="inputtext" property="ex_vo.string(q19z3)" styleId="q19z3" size="10" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);getClass('e');" readonly="true"/>  
              </td>
            <tr> 
          </table>
       </fieldset>
    </td>
  </tr>
  <tr align="center"> 
  
     <td height="20">
     <br>
	<fieldset align="center" style="width:90%;">
        <legend ><bean:message key="kq.exchange.reason"/></legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td height="70" align="center" >                 
                  <html:textarea name="exchangeAppForm" property="ex_vo.string(q1907)" cols="50" rows="5" styleClass="text5"/>
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
 <tr><td height="10"></td></tr>
   <tr align="center"> 
     <td>
	<fieldset align="center" style="width:90%;">
        <legend >意见</legend>
        <table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审核人&nbsp;  
               </td>
               <td class="tdFontcolor" nowrap > 
                <html:text name="exchangeAppForm" property="ex_vo.string(q1909)" styleClass="inputtext"/>  
               </td>
            <tr> 
            <tr> 
               <td  align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审核人意见&nbsp;  
               </td>
               <td  class="tdFontcolor" nowrap >  
               <html:textarea name="exchangeAppForm" property="ex_vo.string(q1911)" cols="41" rows="3" styleClass="text5"/>
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审批人&nbsp; 
               </td>
               <td  class="tdFontcolor" nowrap >          
               <html:text name="exchangeAppForm" styleClass="inputtext" property="ex_vo.string(q1913)"/>                 
               </td>
            <tr> 
            <tr> 
               <td align="right" height="25" width="25%" class="tdFontcolor" nowrap >                 
                   审批人意见&nbsp; 
               </td>
               <td class="tdFontcolor" nowrap >  
                 <html:textarea name="exchangeAppForm" property="ex_vo.string(q1915)" cols="41" rows="3" styleClass="text5"/>
               </td>
            <tr> 
         </table>
        </fieldset>
      </td>
  </tr>
  <tr><td height="10"></td></tr>
</table>
</div>
<table  width="100%" align="center">
  <tr> 
    <td align="center" style="height:35px;"> 
      <input type="button" name="b_next" value="<bean:message key="button.ok"/>"  onclick="saveRe();" class="mybutton"> 
      <input type="button" name="b_next" value="<bean:message key="button.close"/>" onclick="window.close();" class="mybutton" > 
    </td>
  </tr>
</table>

</html:form> 