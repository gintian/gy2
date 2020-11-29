<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    
  </head>
  <script language="javascript" src="/js/validateDate.js"></script>
  <script language='javascript'>
  
  
  
  
  
  	function  enters()
  	{
  		var value="";
  		for(var i=0;i<document.gztemplateSetForm.initType.length;i++)
  		{	
  			if(document.gztemplateSetForm.initType[i].checked==true)
  				value=document.gztemplateSetForm.initType[i].value;
  		}
  		
  		if(value==2)
  		{
  			if(document.gztemplateSetForm.startDate.value==''&&document.gztemplateSetForm.endDate.value=='')
  				return;
  			
  			if(document.gztemplateSetForm.startDate.value!=''&&!validate(document.gztemplateSetForm.startDate))	
	  				return;
  			
  			if(document.gztemplateSetForm.endDate.value!=''&&!validate(document.gztemplateSetForm.endDate))	
  					return;
  			
  		}
  		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_init=int";
		document.gztemplateSetForm.submit();
  	}
  	
  	
  	
  	function clearData()
  	{ 		
  		document.gztemplateSetForm.startDate.value="";
  		document.gztemplateSetForm.endDate.value="";
  		document.gztemplateSetForm.startDate.readOnly=true;
  		document.gztemplateSetForm.endDate.readOnly=true;
  	}
  	
 	function fireInput()
 	{
 		document.gztemplateSetForm.startDate.readOnly=false;
  		document.gztemplateSetForm.endDate.readOnly=false;
 	}
  	
  	
  	
  	function goback()
  	{
  		document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_query=link";
		document.gztemplateSetForm.submit();
  	}
  	
  	
  
  </script>
  
  <body>
    <html:form action="/gz/templateset/gz_templatelist">
    <table align='center' >
    <tr><td>
    
    <Br><br>
    <fieldset align="center" style="width:500;"><legend ><bean:message key="button.gzdata.init"/></legend>
					<table border="0" cellspacing="0"  align="left" cellpadding="0" >					
						<tr height="22px;">
							<td align='left' >
								<input type="radio" name="initType" value="1"  onclick='clearData()'  checked="checked">&nbsp;<bean:message key="hire.jp.pos.all"/>
							</td>	
								
						</tr>	
			
						<tr style="padding-bottom: 2px;">
							<td align='left'  valign="middle" >
								<input type="radio" name="initType" value="2" onclick='fireInput()' >&nbsp;<bean:message key="jx.khplan.timeframe"/>
								<Br>
								<span style="margin-left: 4px; margin-bottom: 5px;">
								<bean:message key="kq.rule.from"/>&nbsp;<input  type="text" name="startDate" extra="editor" size='23' id="editor4"  
							dropDown="dropDownDate"  value="" />
								<bean:message key="kq.shift.cycle.dateto"/>&nbsp;
								<input  type="text" name="endDate" extra="editor" size='23' id="editor4"  
							dropDown="dropDownDate"  value="" />
								</span>
							</td>
									
						</tr>			
				</table>
				</fieldset>
    	</td></tr>
    	<tr align="left">
    		<td algin='center'>
    			<Input type='button'  value='<bean:message key="reporttypelist.confirm"/>'  class="mybutton"  onclick='enters()' />
    			<Input type='button'  value='<bean:message key="reporttypelist.cancel"/>'  class="mybutton"  onclick='goback()' />
    		</td>
    	</tr>
    	
    	
    	</table>
   </html:form> 
  </body>
  
  <script language='javascript'>
  
	    document.gztemplateSetForm.startDate.readOnly=true;
  		document.gztemplateSetForm.endDate.readOnly=true;
  </script>
  
</html>
