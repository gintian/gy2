<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
	<title><bean:message key="train.plan.review.select.plan"/></title>
    <link rel="stylesheet" href="/css/css1.css" type="text/css">
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 

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
		var value="";
		var num=0;
		for(var i=0;i<document.trainMovementForm.trainPlanID.options.length;i++)
		{
			if(document.trainMovementForm.trainPlanID.options[i].selected==true)
			{	
				num++;
				value=document.trainMovementForm.trainPlanID.options[i].value;
			}
		}
		if(num==0)
		{
			alert(SELECT_PLAN);
			return;
		}
		else if(num>1)
		{
			alert(SELECT_ONE_PLAN);
			return;
		}
		else
		{
			returnValue=value;
			window.close();
		}
	}

	
	
	
	
	</SCRIPT>
</head>
<hrms:themes></hrms:themes>
<body>
<base id="mybase" target="_self">		
<html:form action="/train/plan/searchCreatPlanList">
  <table width="100%" >
  	<tr height='10'><td></td><td></td></tr>
  	<tr><td width='15'>&nbsp;</td>
  	<td>
  	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		    &nbsp;&nbsp;<bean:message key="train.plan.review.plan"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table cellpadding="0" border="0" cellspacing="0">
                <tr>
                 <td align="center"  width="55%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                       	<bean:message key="train.plan.review.select.train"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    
                   <tr>
                       <td align="center">
                         <select name="left_fields" multiple="multiple"  readOnly style="height:220px;width:100%;font-size:9pt">
                         	 <logic:iterate  id="element"    name="trainMovementForm"  property="selectedList" indexId="index">
                      			<option value='<bean:write  name="element" property="dataValue"/>'   selected ><bean:write  name="element" property="dataName"/></option>
                      		</logic:iterate>
                      	 </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="2%" align="center">
                   <input type='button' value='<bean:message key="train.plan.input.plan.add"/>' class="mybutton" />
	              
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="train.plan.review.candidates.plan"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select name="trainMovementForm" property="trainPlanID" multiple="multiple" size="10"  style="height:220px;width:100%;font-size:9pt">
                           <html:optionsCollection property="ratifyTrainPlanList" value="dataValue" label="dataName"/>   		      
 		     </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="2%" align="center">
                 
                </td>                                
                </tr>
              </table>             
            </td>
          </tr>
            
          <tr>
          <td align="center" class="RecordRow" style="height: 35px;" nowrap>
               
            	<Input type='button' value='<bean:message key="button.ok"/>' onclick='sub()' class="mybutton"   />
	       	       
          </td>
          </tr>   
</table>
  
  	</td></tr></table>
  
  
  
  
  
  
  
  
</html:form>
</body>
</html>


