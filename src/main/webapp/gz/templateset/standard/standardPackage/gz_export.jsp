<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
  
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
  
  function outPut()
  {
  		var ids="";
  		for(var i=0;i<document.gzStandardPackageForm.elements.length;i++)
  		{
  			if(document.gzStandardPackageForm.elements[i].type=='checkbox')
  			{
  				if(document.gzStandardPackageForm.elements[i].checked==true)
  				{
  					/* 将#号分隔符改为与逗号（因为后台是使用逗号来分割的） xiaoyun 2014-9-20 start */
  					//ids=ids+"#"+document.gzStandardPackageForm.elements[i].value;
  					ids=ids+","+document.gzStandardPackageForm.elements[i].value;
  					/* 将#号分隔符改为与逗号（因为后台是使用逗号来分割的） xiaoyun 2014-9-20 end */
  				} 				
  			}
  		}
  		
  		if(ids.length==0)
  			return;
  		
  		var waitInfo=eval("wait");
		waitInfo.style.display="block";	
  			
  		var In_paramters="standard="+ids.substring(1); 
		var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'3020010120'});			
  	
  }
  
  function returnInfo(outparamters)
  {
  		var waitInfo=eval("wait");
		waitInfo.style.display="none";
		
		var outName = outparamters.getValue("outName");
		outName = getDecodeStr(outName);
		window.location.target = "_blank";
		window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
  }
  
  
  function goback()
  {
  		document.gzStandardPackageForm.action="/gz/templateset/standard/standardPackage.do?b_query=link";
  		document.gzStandardPackageForm.submit();
  }
  
  </script>
  
  <body>
   <html:form action="/gz/templateset/standard/standardPackage"><Br>
   	<table width="432" border="0" cellspacing="0" cellpadding="0" class="framestyle" align="center">
	  <tr>
	    <td width="430" height="22" class="TableRow" style="border-top: none;border-left: none;border-right: none;"><bean:message key="label.gz.importGzStandard"/></td>
	  </tr>
	  <tr height=460>
	    <td align="center">
	    <Br>
	    	<% int i=1;
	    	   int j=1;
	    	 %>
	    	<div id="scroll_box">
		    <table width="310" border="0" align="center" cellpadding="0" cellspacing="0" class="ListTable" style="border-top: none;">
		    	<logic:iterate id="element" name="gzStandardPackageForm" property="gzStandardPackageInfo"  >
		    		<logic:equal name="element" property="flag" value="0" >
		    			<tr>
					        <td colspan="2" width="90%" class="TableRow" style="border-top: none;">&nbsp;<%=i%>&nbsp;<bean:write name="element" property="name" filter="true"/></td>
					    	<% 
					    	i++;
					    	j=1;
					    	 %>
					    </tr>
		    		</logic:equal>
		    		<logic:equal name="element" property="flag" value="1" >

		    			<tr>
				        	<td align="center" width="40" class="RecordRow">
				          <input type="checkbox" name="importStandardIds" value="<bean:write name="element" property="id" filter="true"/>" />
				         
				          </td>
				          <td align="left" class="RecordRow">
				         <%=j%>&nbsp;【<bean:write name="element" property="id" filter="true"/>】<bean:write name="element" property="name" filter="true"/>
				           <% j++; %>
				            
				            </td>
				        </tr>

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
	    </td>
	  </tr>
	  <tr>
	    <td>
	  <div id='wait' style='position:absolute;top:150;left:300;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					正在导出数据，请稍候......
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
	      <input name="Submit22" type="button" class="mybutton" value="<bean:message key="sys.export.derived"/>"  onclick="outPut()" />
	      <input name="Submit22" type="button" class="mybutton" value="<bean:message key="reportcheck.return"/>"  onclick="goback()" />
	      <Br>
	      <br>
	    </div></td>
	  </tr>
	</table>
   
   
   </html:form>  
  </body>
</html>
