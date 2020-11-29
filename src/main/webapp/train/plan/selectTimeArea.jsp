<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
	<title><bean:message key="jx.khplan.timeframe"/></title>
    <link rel="stylesheet" href="/css/css1.css" type="text/css">
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script language="JavaScript" src="/js/popcalendar3.js"></script>
	<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
	<style>
		.TEXT_NB {
			BACKGROUND-COLOR:transparent;
			BORDER-BOTTOM: #94B6E6 1pt solid; 
			BORDER-LEFT: medium none; 
			BORDER-RIGHT: medium none; 
			BORDER-TOP: medium none;
		}
   </style>
	<SCRIPT LANGUAGE=javascript>
	
	function sub()
	{
		var objarr=new Array();		
		var a_qssj=eval("document.trainMovementForm.qssj");	
		var a_jssj=eval("document.trainMovementForm.jssj");	
		
		objarr[0]=a_qssj.value;
		objarr[1]=a_jssj.value;
		returnValue=objarr;
		window.close();
		
	}

	
	
	
	
	</SCRIPT>
</head>
<body>
<base id="mybase" target="_self">		
<html:form action="/train/plan/searchCreatPlanList">
   <br>
    <fieldset align="center" style="width:90%;">
        <legend ></legend>
        <br>
        <table border="0" cellspacing="0" width="100%"  align="center" cellpadding="0" >
          
          <tr > 
            <td> 
               <table border="0"  cellspacing="0" width="100%" class="ListTable"  cellpadding="2" align="center">
                <tr> 
                  <td colspan="4"> 
                  <br>
                  <table border="0"  cellspacing="0" width="97%" class="ListTable1"  cellpadding="2" align="center">
                      <tr> 
                        <td width="30%" align="center" nowrap class="TableRow">&nbsp;</td>
                        <td width="70%" align="center" nowrap class="TableRow">&nbsp;</td>                     
                      </tr>
                      
                      
                      <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                           <bean:message key="kq.strut.start"/>&nbsp;
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                          <input type='text' name='qssj' class='TEXT_NB'  value="${trainMovementForm.startTime}" size='30'   readonly   onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'  value='' /> &nbsp;
                        </td>
                      </tr>
						
					 <tr> 
                        <td align="center" class="RecordRow" nowrap > 
                           <bean:message key="rsbd.wf.end_d"/>&nbsp;
                        </td>
                        <td align="center" class="RecordRow" nowrap >
                          <input type='text' name='jssj' class='TEXT_NB'  size='30'  value="${trainMovementForm.endTime}"  readonly   onclick='popUpCalendar(this,this, dateFormat,"","",true,false)'  value='' /> &nbsp;
                        </td>
                      </tr>	


                      <tr> 
                        <td align="center" nowrap class="RecordRow" colspan="2">&nbsp;</td>
                      </tr>
                    </table></td>
                </tr>
                <tr> 
                  <td height="15" colspan="2"></td>
                </tr>
              </table>	            				
			</td>
          </tr>		  
          <tr> 
            <td align="center"> <br>
            <input type="reset" value="<bean:message key="button.clear"/>" class="mybutton">          
            <input type="button" name="b_update" value="<bean:message key="button.ok"/>"  onclick='sub()'  class="mybutton"> 
            </td>
          </tr>
        </table>
	</fieldset>
</html:form>
</body>
</html>


