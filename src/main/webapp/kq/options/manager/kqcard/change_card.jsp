<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<%@ page import="com.hjsj.hrms.actionform.kq.options.manager.ChangeCardForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<%
ChangeCardForm changeCardForm=(ChangeCardForm)session.getAttribute("changeCardForm");
String flag=changeCardForm.getFlag();
changeCardForm.setFlag("");
session.setAttribute("changeCardForm",changeCardForm);
if(flag!=null&&flag.equals("ok"))
{
   out.println("<script language=\"javascript\">");
   out.println("var thevo=new Object();");
   out.println("thevo.flag=\"true\";");
   out.println("window.returnValue=thevo;");
   out.println("window.close();");
   out.println("</script>");   
   return;
}
%>
<script language="javascript">   
   function change_Emp(obj)
  {
    var card_no=obj.value;    
    var gObj=document.getElementById("kq_cardno"); 
    var kq_cardno=gObj.value;    
    if(card_no!="")
    {
      var hashvo=new ParameterSet();
      hashvo.setValue("card_no",card_no); 
      hashvo.setValue("kq_cardno",kq_cardno);    
      var request=new Request({method:'post',asynchronous:true,onSuccess:cardMessage,functionId:'15207000032'},hashvo); 
    }    
  }
  var mess;
  var s_f;
  function cardMessage(outparamters)
  {
    var message=outparamters.getValue("message");   
    mess=message;
    var tb_obj=document.getElementById("tdm");    
    tb_obj.innerHTML=message;
    var a0100=outparamters.getValue("a0100"); 
    var nbase=outparamters.getValue("nbase");
    var s_flag=outparamters.getValue("s_flag");
    var a_Obj=document.getElementById("a0100");
    a_Obj.value=a0100;
    var n_Obj=document.getElementById("nbase");
    n_Obj.value=nbase;
    var s_Obj=document.getElementById("s_flag");
    s_Obj.value=s_flag; 
    s_f=s_flag;   
  }
   function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
  function saveCard()
{
       var new_card_obj=document.getElementById("new_card");
       var new_card=new_card_obj.value;       
       var old_card_obj=document.getElementById("old_card");
       var old_card=old_card_obj.value;
       if(s_f=="2"||s_f=="0")
       {
          alert(mess);
          return false;
       }else
       {
          if(old_card!="")
          {
            if(new_card!="")
            {
               var str="更换卡号之前必须将考勤机上的刷卡数据下载至本系统中，\n否则可能导致数据分析不正常！";
               str=str+"\n\n";
               str=str+"您确定要更换卡号吗？";
               if(confirm(str))
               {
                  var waitInfo=eval("wait");	
	              waitInfo.style.display="block"; 
                  changeCardForm.action="/kq/options/manager/changecard.do?b_save=link";                 
                  changeCardForm.submit();
                
               }            
               }else
               {
                 alert("请选择新卡卡号！");
               }
            }else
            {
               alert("原卡卡号不能为空！");
           }
      } 
}
function createCard()
  {
    var request=new Request({method:'post',asynchronous:true,onSuccess:reSetCardList,functionId:'15207000022'});
  
  }
  function reSetCardList(outparamters)
  {
       var card_list=outparamters.getValue("card_list"); 
       var list_vo,select_vo,vos,i;
       vos=card_list;
       if(vos==null||vos.length<=0)
  	  return false;
       list_vo=vos;       
       vos= document.getElementsByName("new_card");  
       if(vos==null)
  	  return false;
       select_vo=vos[0];
       for(var i=0;i<list_vo.length;i++)
       {
         var no = new Option();         
    	 no.value=list_vo[i].dataValue;
    	 no.text=list_vo[i].dataName;
    	 if(validateSetField(select_vo,no.value))
    	 {
    	   select_vo.options[select_vo.options.length]=no;
    	 }
    	    	 
       }     
  }
  function validateSetField(select_vo,value)
    {
        for(var i=0;i<select_vo.length;i++)
       {
         var org_value=select_vo.options[i].value;         
         if(org_value==value)
         {
           return false
         }
       }
       return true;
    }
</script>
<html:form action="/kq/options/manager/changecard">
<div  class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
	     &nbsp;<bean:message key="kq.card.change"/>&nbsp;&nbsp;	    
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	   <td width="100%" class="framestyle9">
   	     <table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1"> 
   	       <tr>
   	         <td>
   	          &nbsp;&nbsp;
   	         </td>
   	         <td>
   	         <html:hidden name="changeCardForm" property="kq_cardno" styleClass="text"/>
   	          <html:hidden name="changeCardForm" property="nbase" styleClass="text"/>
   	          <html:hidden name="changeCardForm" property="a0100" styleClass="text"/>
   	           <html:hidden name="changeCardForm" property="s_flag" styleClass="text"/>
   	         </td>  	        
   	       </tr>
   	       <tr>
   	         <td>
   	            <bean:message key="kq.card.old_card"/> 	           
   	         </td>
   	         <td height="30" >
   	         <html:text name="changeCardForm" styleClass="text4" property="old_card" styleId="old_card" onchange="change_Emp(this);" onkeypress="event.returnValue=IsDigit();"/>
   	         </td>
   	       </tr>
   	        <tr>
   	         <td height="30">
   	           <bean:message key="kq.card.new_card"/>
   	         </td>
   	         <td colspan="2">
   	          <hrms:optioncollection name="changeCardForm" property="card_list" collection="list" />
	          <html:select name="changeCardForm" property="new_card" size="1">
                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  </html:select>
                  &nbsp; &nbsp;<input type="button" name="b_addfield" value="<bean:message key="kq.card.auto.createcard"/>"  class="mybutton" onclick="createCard();">
   	         </td>
   	         <!--  <td>
   	           <input type="button" name="b_addfield" value="<bean:message key="kq.card.auto.createcard"/>"  class="mybutton" onclick="createCard();">
   	         </td>-->
   	       </tr>
   	        <tr>   	         
   	         <td colspan="3">
   	            <fieldset align="center" style="width:100%;">
    		     <legend ><bean:message key="kq.card.emp.message"/></legend>
    		      <table border="0" cellspacing="5"  cellpadding="0" width="100%">
                         <tr>
                          <td valign="middle" height="50" id="tdm">
                             &nbsp;
                          </td>
                          </tr>
                       </table>
   	             </fieldset>
   	         </td>
   	       </tr>
   	       <tr>
   	         <td height="30">
   	          <bean:message key="kq.card.lost"/>
   	         </td>
   	         <td colspan="2">
   	         <html:multibox name="changeCardForm" property="lost_flag" value="0"/>&nbsp;
   	          
   	         </td>
   	       </tr>
   	     </table>
   	   </td>
   	  </tr>
   	  <tr>
   	   <td align="center" nowrap style="height:35px;border: none"> 
   	     <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="saveCard();" class="mybutton">
             <input type="button" name="b_next" value="<bean:message key="button.cancel"/>" onclick="window.close();" class="mybutton">	      	       
   	   </td>
   	  </tr>
</table>
</div>
</html:form>
<div id='wait' style='position:absolute;top:200;left:150;display:none;'>
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
<script language="javascript"> 
  var waitInfo=eval("wait");	
  waitInfo.style.display="none";
</script>