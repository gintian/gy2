<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.ChangeInfoForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 java.util.*"%>
<%
	int i=0;
	ChangeInfoForm changeInfoForm=(ChangeInfoForm)session.getAttribute("changeInfoForm");
	String salaryid=changeInfoForm.getSalaryid();
	ArrayList changeTabList=changeInfoForm.getChangeTabList();
	
%>
<script language="javascript"><!--
function getCount(salaryid)
{
    var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("ajax","ajax"); 
	var request=new Request({method:'post',asynchronous:true,onSuccess:setvalid,functionId:'3020072000'},hashvo);
}
	function setvalid(outparameters)
	{
	    var addcount=outparameters.getValue("addcount");
        var delcount=outparameters.getValue("delcount");
        var chgcount=outparameters.getValue("chgcount");
        var salaryid=outparameters.getValue("salaryid");
        var tfcount=outparameters.getValue("tfcount");
	    var altDesc="总共";
	    if(addcount!='0')
	    {
	       altDesc+=" 增加 "+addcount+" 人,";
	    }
	    if(delcount!='0')
	    {
	       altDesc+=" 减少 "+delcount+" 人,";
	    }
	    if(chgcount!='0')
	    {
	       altDesc+=" 信息变动 "+chgcount+" 人,";
	    }
	    if(tfcount!='0')
	    {
	       altDesc+="  停发 "+tfcount+"  人";
	    }
	    altDesc+="是否继续?";
	    if(altDesc=="总共是否继续?"){
	    	altDesc = "没有选中人员";
	    }
	    if(confirm(altDesc))
	    {
	    	document.changeInfoForm.b_ok.disabled=true;
	        var waitInfo=eval("wait");			
	        waitInfo.style.display="block";
            var hashvo=new ParameterSet();
		    hashvo.setValue("salaryid",salaryid);  
	     	var request=new Request({asynchronous:true,onSuccess:isSuccess,functionId:'3020072007'},hashvo); 
	     }
	}
	function isSuccess()
	{
		var waitInfo=eval("wait");			
	    waitInfo.style.display="none";
		returnValue=1;
		window.close();
	}
	function getCountX(salaryid)
   {
      var hashvo=new ParameterSet();
	  hashvo.setValue("salaryid",salaryid);
	  hashvo.setValue("ajax","ajax"); 
	  var request=new Request({method:'post',asynchronous:true,onSuccess:sub,functionId:'3020072000'},hashvo);
   }
	function sub(outparameters)
	{
	    var addcount=outparameters.getValue("addcount");
        var delcount=outparameters.getValue("delcount");
        var chgcount=outparameters.getValue("chgcount");
        var salaryid=outparameters.getValue("salaryid");
        var tfcount=outparameters.getValue("tfcount");
	    var altDesc="总共";
	     if(addcount!='0')
	    {
	       altDesc+=" 增加 "+addcount+" 人,";
	    }
	    if(delcount!='0')
	    {
	       altDesc+=" 减少 "+delcount+" 人,";
	    }
	    if(chgcount!='0')
	    {
	       altDesc+=" 信息变动 "+chgcount+" 人,";
	    }
	    if(tfcount!='0')
	    {
	       altDesc+="  停发 "+tfcount+"  人";
	    }
	    altDesc+="是否继续?";
	    if(confirm(altDesc))
	    {
	    	document.changeInfoForm.b_ok.disabled=true;
	    	var waitInfo=eval("wait");			
	        waitInfo.style.display="block";
	    	changeInfoForm.action="/gz/gz_accounting/change_list.do?b_ok=ok";
	    	changeInfoForm.submit();
	    }
		
	}
	
function backSetList(flow_flag,gz_module)
{
   document.location="/gz/gz_accounting/gz_set_list.do?b_query=link&flow_flag="+flow_flag+"&gz_module="+gz_module;
}	
	
	
-->
</script>
<hrms:themes />
<html:form action="/gz/gz_accounting/change_list">

<div id='wait' style='position:absolute;top:70;left:350;display:none;'>
		<table border="1" width="400" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="classdata.isnow.wiat"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"   direction="right" width="300" scrollamount="5" scrolldelay="10" >
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



<table width='100%'><tr><td width='100%' >
<hrms:tabset name="pageset" width="100%" height="500" type="true"> 
	<% for(int j=0;j<changeTabList.size();j++){
			LazyDynaBean abean=(LazyDynaBean)changeTabList.get(j);	
			String label=(String)abean.get("label");
			String url=(String)abean.get("url");
	 		String name="tab"+(j+1);
	 %>
	  <hrms:tab name="<%=name%>" label="<%=label%>" visible="true" url="<%=url%>" >
      </hrms:tab>	
	 <% } %>

</hrms:tabset>
</td>
</tr>
</table>
<table  width="100%" align="center">
          <tr>
            <td align="center">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
             <logic:equal name="changeInfoForm" property="fromflag" value="0">
         	<!-- 
         	  <html:submit styleClass="mybutton" property="b_ok">
            		<bean:message key="button.ok"/>
	 	      </html:submit>
	 	      -->
	 	      <html:button styleClass="mybutton" property="b_ok" onclick="getCountX('${changeInfoForm.salaryid}');">
            		<bean:message key="button.ok"/>
	 	      </html:button>  
	 	      <input type="button" class="mybutton" name="bb" value="<bean:message key="button.return"/>" onclick="backSetList('<%=request.getParameter("flow_flag")%>','<%=request.getParameter("gz_module")%>');"/>
	         </logic:equal> 
	         <logic:equal name="changeInfoForm" property="fromflag" value="1">
	         <html:button styleClass="mybutton" property="b_ok" onclick="getCount('${changeInfoForm.salaryid}');">
            	<bean:message key="button.ok"/>
	 	      </html:button>     
	 	      
	 	      <html:button styleClass="mybutton" property="b_back" onclick="javascript:window.close();">
             	<bean:message key="button.close"/>
	 	      </html:button> 
	         </logic:equal>   
	         
	         
	         
            </td>
          </tr>          
</table>
</html:form>
