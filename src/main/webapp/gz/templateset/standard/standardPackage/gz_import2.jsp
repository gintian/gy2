<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.templateset.standard.standardPackage.GzStandardPackageForm,
			     java.util.*"%>
<html>
  <head>
  <%
				GzStandardPackageForm gzStandardPackageForm=(GzStandardPackageForm)session.getAttribute("gzStandardPackageForm");
				ArrayList gzStandardPackageInfo=gzStandardPackageForm.getGzStandardPackageInfo();
  %>
  
  </head>
  <style type="text/css"> 
.gray {	
	background:  #00FFFF;
	font-size: 12px;
	color: #000000;
}
#scroll_box {
    border: 1px solid #eee;
    height: 450px;    
    width: 350px;            
    overflow: auto;            
    margin: 1em 0;
}
.btn {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 1px;
 PADDING-RIGHT: 1px;
 FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 BORDER-BOTTOM: #C0C0C0 1px solid
}
</style>
  
  <script langugae='javascript' >
  
  function selectAll()
  {
  	   for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox')
  			{
  				document.gzStandardPackageForm.elements[i].checked=true
  			}
  		}
  
  }
  
  function clearSelect()
  {
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox')
  			{
  				document.gzStandardPackageForm.elements[i].checked=false
  			}
  		}
  }
  
  function importFile(flag)
  {
  		var num=0;
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox')
  			{
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					num++;
  				} 				
  			}
  		}
  		if(num==0)
  		{
  			alert(GZ_ACCOUNTING_SELECTIMPORTSTANDARD);
  			return;
  		}
  		var desc="";
  		if(flag==1)
  		{
  			desc=GZ_ACCOUNTING_COVERIMPORT;
  		}
  		else if(flag==2)
  		{
  			desc=GZ_ACCOUNTING_ADDITIONIMPORT;
  		}
  		if(confirm(GZ_ACCOUNTING_ENTERRUN+desc+GZ_ACCOUNTING_OPERATE+"？"))
  		{

  			var waitInfo=eval("wait");
			waitInfo.style.display="block";
  			
  			document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_import=import&flag="+flag;
  			document.gzStandardPackageForm.submit();
  		}
  }
  
  </script>
  
  <body>
   <html:form action="/gz/templateset/standard/standardPackage"><Br>
   	<table width="432" border="0" cellspacing="0" cellpadding="0" class="framestyle" align="center">
	  <tr>
	    <td width="430" height="22" class="TableRow" style="border-top: none;border-right: none;border-left: none;"><bean:message key="label.gz.selectImportSalaryTable"/></td>
	  </tr>
	  <tr>
	    <td align="center" >
	    <Br>
	    	<%
	    		if(gzStandardPackageInfo==null||gzStandardPackageInfo.size()==0)
	    		{
	    			out.print("&nbsp;&nbsp;&nbsp;&nbsp;无可导入数据！！！<Br><br>&nbsp;");
	    		
	    		}
	    		else
	    		{
	    		
	    	   int i=1;
	    	   int j=1;
	    	 %>
	    	<div id="scroll_box">
		    <table width="300" border="0" align="center" cellpadding="0" cellspacing="0" class="ListTable" style="border-top: none;">
		    	<logic:iterate id="element" name="gzStandardPackageForm" property="gzStandardPackageInfo"  >
		    		<logic:equal name="element" property="flag" value="0" >
		    			<tr>
					        <td width="255" class="TableRow" style="border-top: none;">&nbsp;<%=i%>:<bean:write name="element" property="name" filter="true"/></td>
					    	<% 
					    	i++;
					    	j=1;
					    	 %>
					    </tr>
		    		</logic:equal>
		    		<logic:equal name="element" property="flag" value="1" >
		    			<tr>
				        	<td align="center" width="40" class="RecordRow" style="border-top: none;">
				          <input type="checkbox" name="importStandardIds" value="<bean:write name="element" property="id" filter="true"/>" />
				         
				          </td>
				          <td align="left" class="RecordRow" style="border-top: none;">
				         <%=j%>:【<bean:write name="element" property="id" filter="true"/>】<bean:write name="element" property="name" filter="true"/>
				           
				            
				            </td>
				        </tr>
				         <% j++; %>
		    		</logic:equal>
		    	</logic:iterate>
		      <tr>
		        <td>&nbsp;</td>
		      </tr>
		      <tr>
		        <td>&nbsp;</td>
		      </tr>
		    </table>
		    </div>
		    <%
		    }
		    %>
	    
	    </td>
	  </tr>
	  <%
	  if(gzStandardPackageInfo!=null&&gzStandardPackageInfo.size()!=0){
	  %>
	  <tr>
	    <td>
	    <div id='wait' style='position:absolute;top:150;left:300;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					正在导入数据，请稍候......
				</td>
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
	    <div align="center">
	      <input name="Button" type="button" class="mybutton" value="<bean:message key="label.query.selectall"/>" onclick='selectAll()' />
	      <input name="Submit2" type="button" class="mybutton" value="<bean:message key="label.query.clearall"/>"  onclick='clearSelect()' />
	      <input name="Submit22" type="button" class="mybutton" value="<bean:message key="button.overimport"/>"  onclick="importFile(1)" />
	      <input name="Submit22" type="button" class="mybutton" value="<bean:message key="button.additionimport"/>"  onclick="importFile(2)" />
	      <Br>&nbsp;
	    </div>		
</td>
	  </tr>
	  <%  }  %>
	</table>

	
	<%
	if(gzStandardPackageInfo==null||gzStandardPackageInfo.size()==0)
   	{
   	%>
   		<table width="432" border="0" cellspacing="0" cellpadding="0" align="center">
	  <tr>
	    <td width="430" height="22" align='left' style="padding-top: 3px;">
	    	<input name="Button" type="button" class="mybutton" value="<bean:message key="kq.search_feast.back"/>" onclick='javascript:history.go(-1)' />
	    
	    </td>
	  </tr>
   </table>
   	<% } %>
</html:form>
  </body>
</html>

