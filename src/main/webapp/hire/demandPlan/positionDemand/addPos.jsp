<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language='javascript' >
    var info=dialogArguments;
    
    
    function sub()
    {

    	var v=f1.posID.value;
    	var reg=new RegExp("^[A-Z0-9]+$");
    	for(var i=0;i<v.length;i++){
			var c=v.substr(i,i+1);
			if(!reg.test(c)){
				alert(CODE_INPUT_ENGLISH_OR_NUMBER_ONLY);
				f1.posID.value="";
				f1.posID.focus();
				return false;
			}
		}
    
    	if(!IsOverStrLength(f1.posID.value,info[0]))
    	{
	    //	if(/^[A-Za-z0-9]+$/.test(f1.posID.value))
	    	{
	    		if(info[1].length!=0&&f1.posID.value.length!=info[0])
	    		{
	    			alert(CODE_LENGTH_ISNOT_RIGHT+"！");
					return;	    		
	    		}
	    	
	    		if(info[1].indexOf("#"+f1.posID.value)!=-1)
	    		{
	    				alert(CODE_EXIST_PLEASE_REFILL+"!");
	    				return;
	    		}
	    	}
	   /* 	else
	    	{
	    		alert("编码只能输入英文或数字！")
	    		return;
	    	}*/
	    }
	    else
	    {
	    	alert(CODE_LENGTH_NOT_AGREE_REQUEST+"！");
	    	return;
	    }
	    if(IsOverStrLength(f1.posName.value,50))
	    {
	    	alert(POSITION_NAME_LENGTH_NOT_BIGGER_50+"!");
	    	return;
	    }
	    if(trim(f1.posName.value).length==0)
	    {
	    	alert(PLEASE_FILL_POSITION_NAME+"!");
	    	return;
	    }
	    var a_value=new Array();
	    a_value[0]=f1.posID.value;
	    a_value[1]=f1.posName.value;
	    returnValue=a_value;
	    window.close();	
    }
    
    function closeWindow()
    {
    	window.close();	
    }
    
    
</script>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><bean:message key="hire.new.position"/></title>
	</head>
	<hrms:themes></hrms:themes>
	<body>
	<form name='f1'>
	
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
						<br>
						
						<fieldset align="center" style="width:90%;">
    							 <legend >
    							<SCRIPT language='javascript' >
    							if(info[1].length==0)
    							{
    								document.write(CODE_LENGTH_MOST_IS+info[0]+REPORT_CHAR);
    							}
    							else
    							{
    								document.write(INPUT_CODE_LENGTH_IS+info[0]+REPORT_CHAR);
    							}
    							</SCRIPT>
    							 
    							 
    							 </legend>
		                      			<table width="90%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
		                					
		                					
		                					
		                					<script language='javascript'>
		                						document.write("<td width='100%' height='80' style='padding-left:10px;'>&nbsp;"+POSITION_CODE+"&nbsp;<Input type='text' class='text4' name='posID' value='"+info[3]+"' style='width:250px;' ><br>&nbsp;"+POSITION_NAME+"&nbsp;<input maxlength='50' class='text4' type='text' name='posName' style='width:250px;margin-top:5px;'></td>")
		                					
		                					</script>
		                					
		                					
		                					
                     			
		                      				</tr>
		                      			</table>
		                      		</fieldset>
						
						
						
						
						
						
											
				</td></tr><tr>
				<td valign='bottom' align="center" height="35px;">
					<input type='button' value="<bean:message key="button.ok"/>"  class="mybutton" onclick='sub()' >
					<input type='button' value="<bean:message key="button.cancel"/>"  onclick='closeWindow()' class="mybutton" >
				
				</td>
			</tr>
		</table>
		
		</form>
	</body>
</html>
