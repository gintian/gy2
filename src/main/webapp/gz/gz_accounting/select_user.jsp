<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
  
</head>
 
<style type="text/css">
	
#scroll_box {
	           border: 1px solid #ccc;
	           height: 250px;    
	           width: 100%;            
	           overflow: auto;            
	           margin: 0em 0;
	       }
	  
	  
</style>
<body>


  <html:form action="/gz/gz_accounting/gz_table">
    
    <table width='100%' >
    <tr>
    <td colspan='2' > &nbsp;</td>
    </tr>
    <tr>
    <td> &nbsp;</td>
    <td width='100%'>
    
    							 
    				 <div id="scroll_box">	 
    
    			 <table width="100%" border="0" cellspacing="0" style="margin-top:0"  align="center" cellpadding="0" class="ListTable">
							   	  <thead>
							   	  <tr>
							   	  <td align="center" width='20%' class="TableRow" nowrap >
								  <bean:message key="column.select"/>
								  </td>
								  <td align="center" width='40%' class="TableRow" nowrap >
								  用户组
								  </td>
								  <td align="center" width='40%' class="TableRow" nowrap >
								  用户名
								  </td>
    							  </tr>
    							  </thead>
    							  
    							  <script language='javascript'>
	   
	   							var info=dialogArguments;
	   						 
	   							var temps=getDecodeStr(info[0]).split("`");
	   							for(var i=0;i<temps.length;i++)
	   							{
	   								var _temps=temps[i].split("##");
		   							document.write("<tr class='trShallow'   >  <td align='center' class='RecordRow' nowrap>");
		   							document.write("<input type='radio' name='user' value='"+_temps[2]+"' /> </td>  ");  
		   							document.write("<td align='left' class='RecordRow' nowrap>&nbsp;");
		   							document.write(_temps[0]+" </td>  ");  
		   							document.write("<td align='left' class='RecordRow' nowrap>&nbsp;");
		   							document.write(_temps[1]+" </td> </tr> ");    
	   
	   							}
	   							 </script>
    							  
    							  
    							  
    			  </table>
    
     				</div>      		
		  
		</td></tr>
		<tr><td align='center' height='20' colspan='2' >
         &nbsp;
         <br>
    	<input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="kq.formula.true"/>'  />
    	<input type='button'  class="mybutton"  onclick='javascript:window.close()' value='<bean:message key="lable.content_channel.cancel"/>'  />
	    </td>  </tr>
    </table>
</html:form>

 <script language='javascript'>

	function enter()
	{
		var _value="";
		var objs=document.getElementsByName("user");
		for(var i=0;i<objs.length;i++)
		{
			if(objs[i].checked)
				_value=objs[i].value;
			
		}
		if(_value=="")
		{
			alert("请选择审批用户!");
			return;
		}
		returnValue=_value;
	    window.close();
	}

  </script>
</body>
</html>