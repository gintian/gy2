<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
  

  </head>
  <script language='javascript'> 

  	
	function up_o()
	{
	    var obj=eval("document.gztemplateSetForm.salarySetSort");
		if(optionSelected())
		{
			var index=getIndex();
			if(index>0)
			{
			    var a_option=obj.options[index];
			   
			    var   a_option0=document.createElement("OPTION");
			    a_option0.text=obj.options[index-1].text;
			    a_option0.value=  obj.options[index-1].value;
		
				obj.options[index-1].text=a_option.text;
				obj.options[index-1].value=a_option.value;
				obj.options[index].text=a_option0.text;
				obj.options[index].value=a_option0.value;
				obj.options[index-1].selected=true;
				obj.options[index].selected=false;
			}
		}
	}

	function down_o()
	{
		var obj=eval("document.gztemplateSetForm.salarySetSort");
		if(optionSelected())
		{
			var index=getIndex();
			if(index<obj.options.length-1)
			{
			    var a_option=obj.options[index];
			   
			    var   a_option0=document.createElement("OPTION");
			    a_option0.text=obj.options[index+1].text;
			    a_option0.value=  obj.options[index+1].value;
				obj.options[index+1].text=a_option.text;
				obj.options[index+1].value=a_option.value;
				obj.options[index].text=a_option0.text;
				obj.options[index].value=a_option0.value;
				obj.options[index+1].selected=true;
				obj.options[index].selected=false;
			}
		}
    }
    
    
    function getIndex()
    {
		var index=-1;
		var obj=eval("document.gztemplateSetForm.salarySetSort");
		for(var i=0;i<obj.options.length;i++)
    	{
    			if(obj.options[i].selected)
    			{
    				index=i;
    				 break;
    			 }
    	}
    	return index;
    }
    
    function optionSelected()
    {
    	var num=0;
    	var obj=eval("document.gztemplateSetForm.salarySetSort");
    	for(var i=0;i<obj.options.length;i++)
    	{
    		if(obj.options[i].selected)
    			num++;
    	}
    	if(num==0)
    	{
    		alert(GZ_TEMPLATESET_INFO41);
    		return false;
    	}
    	else if(num>1)
    	{
    		alert(GZ_TEMPLATESET_info42);
    		return false;
    	}
    	else
    	{
    		return true;
    	}
    }
    
    
    function sub()
    {
        var obj=eval("document.gztemplateSetForm.salarySetSort");
    	for(var i=0;i<obj.options.length;i++)
    	{
    		obj.options[i].selected=true;
    	}
    	document.gztemplateSetForm.action="/gz/templateset/gz_templatelist.do?b_sort=sort";
	    document.gztemplateSetForm.submit();
    }
    
    <%  
    	if(request.getParameter("b_sort")!=null&&request.getParameter("b_sort").equals("sort"))
    	{
    		out.println("returnValue='1';");
    		out.println("window.close();");
    	}
    %>
	
  </script>	  
  
  <body>
   
   <html:form action="/gz/templateset/gz_templatelist">
     <table width='100%' >
     <tr>
     <td colspan='2' > <logic:equal name="gztemplateSetForm" property="gz_module" value="0"><bean:message key="sys.res.gzset"/></logic:equal> <logic:equal name="gztemplateSetForm" property="gz_module" value="1">保险类别</logic:equal>:</td></tr>
     <tr><td width='90%' >
     <select name="salarySetSort" multiple="multiple"  style="height:209px;width:90%;font-size:9pt">
    	<logic:iterate  id="element" name="gztemplateSetForm" property="setlist2" >
    		<option value='<bean:write name="element" property="salaryid" filter="true"/>'><bean:write name="element" property="salaryid" filter="true"/>.<bean:write name="element" property="cname" filter="true"/></option>
    	
    	</logic:iterate>
     </select>
     </td>
     <td valign='center' >
     <Input type='button' onclick='up_o()'  class="mybutton" value=' <bean:message key="kq.shift.cycle.up"/> ' >
     <Br><Br>
     <Input type='button' onclick='down_o()'  class="mybutton" value=' <bean:message key="kq.shift.cycle.down"/> ' >
     
     </td>
     </tr>
     <tr><td colspan='2' align='center' >
     <Input type='button' onclick='sub()'  class="mybutton" value=' <bean:message key="reporttypelist.confirm"/> ' >
     &nbsp;&nbsp;
     <Input type='button' onclick='javascript:window.close()'  class="mybutton" value=' <bean:message key="button.cancel"/> ' >
     </td></tr>
     </table>
   	
   </html:form>
  </body>
</html>
