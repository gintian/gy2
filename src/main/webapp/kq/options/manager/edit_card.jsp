<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<%@ page import="com.hjsj.hrms.actionform.kq.options.manager.EditKqCardForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<%
EditKqCardForm editKqCardForm=(EditKqCardForm)session.getAttribute("editKqCardForm");
String flag=editKqCardForm.getFlag();
editKqCardForm.setFlag("");
session.setAttribute("editKqCardForm",editKqCardForm);
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

   var save_flag=true;
   var textObj;

  function change_Emp()
  {
    var obj=document.getElementById("cardno");
    var card_no=obj.value;  
    if(card_no == "${editKqCardForm.old_cardno}"){
    	alert("卡号未更新，请重新选择！");
    	return;
    }
    textObj=obj;  
    var id_len="${editKqCardForm.id_len}";
    if(card_no!=""&&card_no.length>0)
    {
       if(card_no.length!=id_len)
       {
         if(confirm('<bean:message key="kq.card.overtop.len"/>'))
         {
            opinionCard(card_no); 
         }else
         {
           obj.value="${editKqCardForm.old_cardno}";
         }
       }else
       {
         opinionCard(card_no);
       } 
    }else if(card_no=="")
    {
    	saveCard();
        }
   
    
  }
  function changecard(obj)
  {
    var card_obj=document.getElementById("cardno");
    card_obj.value=obj.value;
  }
  function opinionCard(card_no)
  {
    if(card_no!="")
    {
      var hashvo=new ParameterSet();
      hashvo.setValue("card_no",card_no);  
      hashvo.setValue("old_cardno","${editKqCardForm.old_cardno}")   
      hashvo.setValue("a0100","${editKqCardForm.a0100}")  
      hashvo.setValue("nbase","${editKqCardForm.nbase}")     
      hashvo.setValue("kq_cardno","${editKqCardForm.kq_cardno}")   
      var request=new Request({method:'post',asynchronous:true,onSuccess:cardMessage,functionId:'15207000035'},hashvo); 
    }
  }
  function cardMessage(outparamters)
  {
     var flag=outparamters.getValue("flag");
     if(flag=="true")
     {
       	save_flag=true;
   	 	exitsCard();
   	 	return;
     }
	 //如果卡号作废，提示信息  并返回
     if(flag=="cancellation"){
    	alert('<bean:message key="kq.card.de.input"/>');
     } else {
       save_flag=false;
       alert('<bean:message key="kq.card.de.use"/>');
       var card_obj=document.getElementById("cardno");
       card_obj.value="${editKqCardForm.old_cardno}";
     }
     document.getElementById("cardno").focus();
  }
   function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
  function exitsCard()
  {
	
		
     var hashvo=new ParameterSet();
     var card_obj=document.getElementById("cardno");
     var card_no=card_obj.value;
     hashvo.setValue("card_no",card_no);  
     var request=new Request({method:'post',asynchronous:true,onSuccess:exitsResult,functionId:'15207000036'},hashvo);      
	    
  }
  function exitsResult(outparamters)
  {
    var exist_falg=outparamters.getValue("exist_falg");    
    if(exist_falg!="true")
    {
       var card_obj=document.getElementById("cardno");
       var card_no=card_obj.value;
       if(confirm("该卡号还未生成!\n您确定要生成卡号<"+card_no+">吗?"))
       {
          createCard(card_no);
       }
    }else
    {
      saveCard();
    }
  }
  function createCard(card_no)
  {
     var hashvo=new ParameterSet();
     hashvo.setValue("card_no",card_no);  
     var request=new Request({method:'post',asynchronous:true,onSuccess:createResult,functionId:'15207000037'},hashvo);          
  }  
  function createResult(outparamters)
  {
     var flag=outparamters.getValue("flag");
    
     if(flag=="true")
     {
       saveCard();       
     }else
     {
       alert('<bean:message key="kq.create.card.lost"/>');
     }
  }
  function saveCard()
  {
     var card_obj=document.getElementById("cardno");
     var card_no=card_obj.value;
     var old_card="${editKqCardForm.old_cardno}";
     var clew="";
     if(card_no==null||card_no.length<=0)
     {
        if(!confirm("是要将卡号清空吗？"))
        {
         textObj.value="${editKqCardForm.old_cardno}";
         return false;
        }else
        {
           clew="请确认保存！";
        }
     }else
     {
       clew="更换卡号前必须先将考勤机上的刷卡数据下载至本系统中,\n";
       clew=clew+"否则可能导致数据不能正常分析!\n\n";
       clew=clew+"您确定现在要将<"+old_card+">更换为<"+card_no+">吗?";
     }     
     if(confirm(clew))
     {
        editKqCardForm.action="/kq/options/manager/eidtcard.do?b_save=link";        
        editKqCardForm.submit();
     }
  }
  function createAutoCard()
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
       vos= document.getElementsByName("new_cardno");  
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
<html:form action="/kq/options/manager/eidtcard">
<div class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
	     &nbsp;<bean:message key="kq.card.edit.name"/>&nbsp;&nbsp;	    
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
   	         <html:hidden name="editKqCardForm" property="kq_cardno" styleClass="text"/>
   	          <html:hidden name="editKqCardForm" property="nbase" styleClass="text"/>
   	          <html:hidden name="editKqCardForm" property="a0100" styleClass="text"/>  
   	          <html:hidden name="editKqCardForm" property="id_len" styleClass="text"/>   	            	           
   	          <html:hidden name="editKqCardForm" property="old_cardno" styleClass="text"/>   	            	           
   	         </td>  	        
   	       </tr>
   	       <tr>
   	         <td>
   	            <bean:message key="kq.card.card_no"/>&nbsp;	           
   	         </td>
   	         <td height="30">
   	         <html:text name="editKqCardForm" property="cardno" styleClass="TEXT4" maxlength="20" onkeypress="event.returnValue=IsDigit();"/>
   	         </td>
   	       </tr>
   	        <tr>   	         
   	         <td colspan="2">
   	            <fieldset align="center" style="width:100%;">
    		     <legend ><bean:message key="kq.card.emp.message"/></legend>
    		      <table border="0" cellspacing="5"  cellpadding="0" width="100%">
                         <tr>
                          <td valign="middle" height="50" id="tdm">
                             <bean:write name="editKqCardForm" property="card_message" filter="false"/>
                          </td>
                          </tr>
                       </table>
   	             </fieldset>
   	         </td>
   	       </tr>
   	        <tr>
   	         <td height="30">
   	           <bean:message key="kq.card.edit.nouse"/>
   	         </td>
   	         <td>
   	          <hrms:optioncollection name="editKqCardForm" property="card_list" collection="list" />
	          <html:select name="editKqCardForm" property="new_cardno" onchange="changecard(this);" size="1">
                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  </html:select>
                  &nbsp;&nbsp;&nbsp;
                   <input type="button" name="b_addfield" value="<bean:message key="kq.card.auto.createcard"/>"  class="mybutton" onclick="createAutoCard();">
   	         </td>
   	       </tr>   	      
   	     </table>
   	   </td>
   	  </tr>
   	  <tr>
   	   <td align="center"  nowrap style="height:35px;border:none"> 
   	     <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="change_Emp();" class="mybutton">						   
         <input type="button" name="b_next" value="<bean:message key="button.cancel"/>" onclick="window.close();" class="mybutton">	    	       
   	   </td>
   	  </tr>
</table>
</div>
</html:form>

