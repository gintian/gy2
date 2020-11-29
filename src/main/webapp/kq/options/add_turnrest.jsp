<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript">
  var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
  function saves()
  {

    	 kqTurnRestForm.action="/kq/options/add_turnrest.do?b_save=link";
    	 kqTurnRestForm.target="il_body";
         kqTurnRestForm.submit();
         //window.open("/kq/options/search_rest.do?b_query=link",'il_body');
         //window.close();	   
  }
  
  function valide()
	{
	
	   var tag=true;   
     var ta="";
     var das="";
     var m=0;
     var tes,www;
     var ver=Array();
     var tem=Array();

	    ver[0] = $F('rdate');
	    ver[1] = $F('tdate');
	    
	    
	    for(m=0;m<2;m++)
	    {
	         www=ver[m].replace(".","-");
	         if(0==www.length || ""==www){
	        	 alert('日期不可以为空,请检查！');
	        	 return false;
	         }
	    	    tes=www.replace(".","-");
	    	    tes=tes.substring(0,10);	    	    
	    	    tem=tes.split("-");
	    	    if(tem.length!=3)
	    	    {
	    	      alert('日期格式不正确,正确格式为yyyy-mm-dd！');
	    	       return false;
	    	    }	    	   
	    	     
	    	     if(tem[1].length==1)
	    	       ta="-0"+tem[1];
	    	     else
	    	       ta="-"+tem[1];

	    	     if(tem[2].length==1)
	    	       das="-0"+tem[2];
	    	     else
	    	       das="-"+tem[2];
	    	   
	    	           tag= checkDat(tem[0]+ta+das);
	    	           if(tag==false)
	                {
	                    return false;
	                }	            
	    	 }
	    	     
        var thevo=new Object();
	    thevo.start=ver[0];
        thevo.end=ver[1];
        thevo.tid="${kqTurnRestForm.tid}";
        window.returnValue=thevo;	
        if (!window.showModalDialog) 
        	 parent.saveTurnRest(thevo);
        else
        	window.returnValue=thevo;
        closeWindow();        
	}
  
    function closeWindow(vo) {
    	if (!window.showModalDialog) {
    		parent.Ext.getCmp("turnRestWin").close();
    	}else{
    		window.close(); 
    	}
    }
	
	 function checkDat(str)
	  {
	    var ret=false;
	      var mm="";
	      var dd="";
	      var tem="";
	      var cc=0;
        var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/; 
        var r = str.match(reg); 
          
        if(r==null)
        {
           alert("日期格式不正确,正确格式为yyyy-mm-dd！");
           return false; 
        }
         var d=new Date(r[1], r[3]-1,r[4]);
            dd=""+d.getDate();
            cc=d.getMonth()+1;
            mm=""+cc;
        if(mm.length==1&&dd.length==2)
        {
          tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+d.getDate();
        }
         if(dd.length==1&&mm.length==2)
        {
          tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+("0"+d.getDate());
        }
        if(dd.length==1&&mm.length==1)
        {
          tem=d.getFullYear()+r[2]+("0"+(d.getMonth()+1))+r[2]+("0"+d.getDate());
        }
        if(dd.length==2&&mm.length==2)
        {
         tem=d.getFullYear()+r[2]+(d.getMonth()+1)+r[2]+d.getDate();
        }
         if(tem==str)
         {
             ret=true;
         }else{
            alert("日期格式不正确,正确格式为yyyy-mm-dd！");
            return false;
         }
       return ret;
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
</script>
<html:form  action="/kq/options/add_turnrest">
<div class="fixedDiv3">
  <table width="100%" border="0" cellpadding="0" class="ListTable" cellspacing="0" align="center">
  			<html:hidden name="kqTurnRestForm" property="tid"/>
      <tr class="list3">
         <td align="center" class="TableRow" nowrap ><bean:message key="kq.rest.rdate"/></td>
           <td align="center" class="TableRow" nowrap ><bean:message key="kq.rest.tdate"/></td>
         </tr> 
         <tr class="list3">
           	<td class="RecordRow" style="padding: 0 2 0 2"><html:text name="kqTurnRestForm" property="rdate" style="border:none" maxlength="10"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'/> </td>
            <td class="RecordRow" style="padding: 0 2 0 2"><html:text name="kqTurnRestForm" property="tdate" style="border:none" maxlength="10"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'/> </td>
            
           </tr>   
   </table>        
       <br>
    <table width="40%" border="0" cellpadding="0" class="ListTable" cellspacing="0" align="center">                                                 
        <tr class="list3">
        <td align="center" colspan="2">
         <input type="button"  value="<bean:message key="button.save"/>" class="mybutton" onclick="valide();"> 
         <input type="button"  value="取消" class="mybutton" onclick="closeWindow();"> 
        </td>
       </tr>          
      </table></div>
</html:form>
