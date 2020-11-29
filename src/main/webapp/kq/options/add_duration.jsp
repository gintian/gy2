<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.options.KqDurationForm"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<script language="JavaScript" >
	function valide()
	{
	
	   var tag=true;   
     var ta="";
     var das="";
     var m=0;
     var tes,www;
     var ver=Array();
     var tem=Array();
     
	   ver[0] = $F('start');
	   ver[1] = $F('end');
	    
	   for(m=0;m<2;m++)
	    {
	         www=ver[m].replace(".","-");
	    	    tes=www.replace(".","-");
	    	    
	    	    tem=tes.split("-");
	    	    if(tem.length!=3)
	    	    {
	    	      alert("日期格式不正确,正确格式为yyyy-mm-dd！");
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
	    if(ver[0] > ver[1]){
		    alert("起始日期不能大于终止日期！");
			return false;
		}
	    
        var salaryYear = $F('duration.string(gz_year)');
        if (salaryYear=='' || isNaN(salaryYear) || salaryYear<'2000' ||  salaryYear>'2100') {
            alert('请输入正确的工资年度！(年度范围：2000~2100)');
            return false;
        }
          
        var salaryMonth = $F('yue');
        salaryMonth = parseInt(salaryMonth,10);
        if (salaryMonth=='' || isNaN(salaryMonth) || salaryMonth<1 ||  salaryMonth>12) { 
            alert('请输入正确的工资月度！(月度范围：1~12)');
            return false;
        }
        salaryMonth=salaryMonth+'';
        if (salaryMonth.length==1)
            document.getElementsByName('yue')[0].value = '0' + salaryMonth;
        
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
	
</script>
	
<style type="text/css">
.td_padding {
    padding-left: 5px;
}
</style>
<html:form action="/kq/options/add_duration"  onsubmit="return valide()">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" style="margin-top:55px;">
		 <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4" style="border-top-width:0px;border-left-width:0px;border-right-width:0px;"><bean:message key="kq.deration_details.kqqj"/></td>
		 </tr>
         <tr>
            <td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
         </tr>
         <tr >            
           <td align="right" height="20" nowrap valign="center" >        
              <bean:message key="kq.deration_details.kqnd"/>    
           </td>
           <td class="td_padding" nowrap >
           	  <bean:write  name="kqDurationForm" property="duration.string(kq_year)" filter="true"/>&nbsp;
           </td>
           <td align="right" nowrap valign="center">        
              <bean:message key="kq.deration_details.kqqj"/>          
           </td>
           <td class="td_padding"  nowrap >
           	  <bean:write  name="kqDurationForm" property="duration.string(kq_duration)" filter="true"/>&nbsp;
           </td>
           </tr>
           <tr > 
           <td align="right" height="20" nowrap valign="center" >        
             <bean:message key="kq.deration_details.gznd"/>        
           </td>
           <td class="td_padding"  nowrap>
               <html:text property="duration.string(gz_year)"  maxlength="4" styleClass="text"/>
           </td>           
           <td align="right" nowrap valign="center">        
             <bean:message key="kq.deration_details.gzqj"/>        
           </td>
           <td class="td_padding"  nowrap>
               <html:text name="kqDurationForm" property="yue" maxlength="2" styleClass="text"/>
           </td>
          </tr>
          <tr>            
           <td align="right" height="20" nowrap valign="center">        
             <bean:message key="kq.deration_details.start"/>        
           </td>
           <td class="td_padding"  nowrap>
              <html:text name="kqDurationForm" property="start" maxlength="12" styleClass="text"/>
           </td>
           <td align="right" nowrap valign="center" >        
              <bean:message key="kq.deration_details.end"/>          
           </td>
           <td class="td_padding"  nowrap>
               <html:text name="kqDurationForm" property="end" maxlength="12" styleClass="text"/>
            </td>
           </tr>		   		   
           <tr>
           	<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td>
           </tr>
 </table>
 <table align="center">
 <tr>
  <td align="center"  nowrap style="height:35px;">
           <hrms:submit styleClass="mybutton" property="b_save" onclick="document.kqDurationForm.target='_self';validate('D','duration.string(kq_start)','起始日期');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	        	</hrms:submit>
        <input type="button" class="mybutton"  name="dd" value="<bean:message key="button.return"/>" onclick="history.go(-1)">    
  </td>
 </tr>    
 </table> 

</html:form>

