<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.ChangeInfoForm,com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.utility.AdminCode,com.hrms.frame.utility.CodeItem,com.hrms.struts.constant.WebConstant,
com.hrms.struts.valueobject.UserView"%>
<%
	int i=0;
	int t=0;
	ChangeInfoForm changeInfoForm = (ChangeInfoForm)session.getAttribute("changeInfoForm");
	FieldItem item = changeInfoForm.getOnlyitem();
	String dise=changeInfoForm.getDisplayE0122();
	ArrayList add_delList=changeInfoForm.getAdd_delList();
	if(add_delList.size()>0){
		t=add_delList.size();
	}
	if(item!=null)
	{
		t++;
	}
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>
<script type="text/javascript" >
<!--
    function isSuccess(outparamters)
    {
	  
    }
	function setvalid(obj,dbname,a0100)
	{
		var flag;
		if(obj.checked)
		  flag="1";
		else
		  flag="0";
        var hashvo=new ParameterSet();
        hashvo.setValue("dbname",dbname);
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("flag",flag);	   
		hashvo.setValue("chgtype",'add');   
		hashvo.setValue("salaryid",'${changeInfoForm.salaryid}');  
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'3020072008'},hashvo); 
	}
	
	
	function exports()
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("salaryid",'${changeInfoForm.salaryid}');    
		hashvo.setValue("opt","add");
	   	var request=new Request({asynchronous:false,onSuccess:showfile,functionId:'3020072009'},hashvo); 
	}
	
	
	function showfile(outparamters)
	{
		var fileName=outparamters.getValue("fileName"); 
		fileName = getDecodeStr(fileName);
		var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true","excel");
	}
	function allSelect()
	{
	   changeInfoForm.action="/gz/gz_accounting/addman.do?b_all=all";
	   changeInfoForm.submit();
	}
	function allClear()
	{
	   changeInfoForm.action="/gz/gz_accounting/addman.do?b_clear=clear";
	   changeInfoForm.submit();
	}
//-->
</script>
<html:form action="/gz/gz_accounting/addman">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
   <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<logic:equal value="0" name="changeInfoForm" property="checkall">
		<html:checkbox property="checkall" name="changeInfoForm" value="1" onclick="allSelect();"></html:checkbox>
		</logic:equal>
		<logic:equal value="1" name="changeInfoForm" property="checkall">
		<html:checkbox property="checkall" name="changeInfoForm" value="1" onclick="allClear();" ></html:checkbox>
		</logic:equal>
            </td>    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.dbase"/>&nbsp;
            </td>                        
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="changeInfoForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="changeInfoForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td> 
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="changeInfoForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	    </td>
	    	     <%if(add_delList.size()>0){ %>
	     
	     	<%for(int j=0;j<add_delList.size();j++){ %>
	     	<td align="center" class="TableRow" nowrap>
	     	<%FieldItem _item =(FieldItem)add_delList.get(j); %>
	     	<%=_item.getItemdesc() %>
	     	</td>
	     	<%} %>
	     	
	     <%} %>
	    <%
	       if(item!=null)
	       {
	         %>
	          <td align="center" class="TableRow" nowrap>
             <%=item.getItemdesc()%>       	
	          </td>
	         <%
	       }
	     %>

		   </tr>
      </thead>
      <hrms:paginationdb id="element" name="changeInfoForm" sql_str="changeInfoForm.strsql" table="" where_str="changeInfoForm.strwhere" columns="changeInfoForm.columns" order_by=" order by DBNAME,A0000,b0110,e0122" page_id="pagination" pagerows="15" distinct="">
	       <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
            <logic:equal name="element" property="state" value="1">
				<input type="checkbox" name="chk" value="1"   checked  onclick ="setvalid(this,'<bean:write name="element" property="dbname" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"> 
		    </logic:equal>
            <logic:equal name="element" property="state" value="0" >
				<input type="checkbox" name="chk" value="0" onclick ="setvalid(this,'<bean:write name="element" property="dbname" filter="true"/>','<bean:write name="element" property="a0100" filter="true"/>');"> 
		    </logic:equal>		    
  	       </td> 	
            <td align="left" class="RecordRow" nowrap>&nbsp;
                <hrms:codetoname codeid="@@" name="element" codevalue="dbname" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>  	       
            <td align="left" class="RecordRow" nowrap>&nbsp;
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>   
	     <%
	       LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	       String e0122="";
	       if(abean.get("e0122")!=null)
	              e0122=(String)abean.get("e0122");
	       String desc=AdminCode.getCode("UM",e0122,Integer.parseInt(dise))!=null?AdminCode.getCode("UM",e0122,Integer.parseInt(dise)).getCodename():AdminCode.getCodeName("UM",e0122);
	       %>         
            <td align="left" class="RecordRow" nowrap>&nbsp;
          	<%=desc%>&nbsp;            
	    </td>

            <td align="left" class="RecordRow" nowrap>&nbsp;
                 <bean:write name="element" property="a0101" filter="true"/>&nbsp;
	    </td>
	    	    <%if(add_delList.size()>0){%>
	     
	     	<%for(int j=0;j<add_delList.size();j++){ %>

	     	<%FieldItem _item =(FieldItem)add_delList.get(j); %>
	     	<%if(_item.getItemtype().equalsIgnoreCase("a")&&!_item.getCodesetid().equals("0"))
	        {
	         %>
	              <td align="left" class="RecordRow" nowrap>&nbsp;
             	<hrms:codetoname codeid="<%=_item.getCodesetid() %>" name="element" codevalue="<%=_item.getItemid()%>" codeitem="codeitem" scope="page"/>  	      
              	<bean:write name="codeitem" property="codename" />&nbsp;            
	           </td>
	         <%
	        }
	        else
	        {
	           String align="left";
	           if(_item.getItemtype().equalsIgnoreCase("N"))
	              align="right";
	         %>
	            <td align="<%=align%>" class="RecordRow" nowrap>&nbsp;
                 <bean:write name="element" property="<%=_item.getItemid()%>" filter="false"/>&nbsp;
	            </td>
	         <%
	        } %>

	     	<%} %>
	     	
	     <%} %>
	    <%
	       if(item!=null)
	       {
	        if(item.getItemtype().equalsIgnoreCase("a")&&!item.getCodesetid().equals("0"))
	        {
	         %>
	              <td align="left" class="RecordRow" nowrap>&nbsp;
             	<hrms:codetoname codeid="<%=item.getCodesetid() %>" name="element" codevalue="<%=item.getItemid()%>" codeitem="codeitem" scope="page"/>  	      
              	<bean:write name="codeitem" property="codename" />&nbsp;            
	           </td>
	         <%
	        }
	        else
	        {
	           String align="left";
	           if(item.getItemtype().equalsIgnoreCase("N"))
	              align="right";
	         %>
	            <td align="<%=align%>" class="RecordRow" nowrap>&nbsp;
                 <bean:write name="element" property="<%=item.getItemid()%>" filter="true"/>
	            </td>
	         <%
	        }
	       }
	     %>

     	</tr>
        </hrms:paginationdb>
        <tr>
        	<td colspan="<%=(t+5)%>">
				<table  width="100%" align="center" class="RecordRowP">
						<tr>
						    <td valign="bottom" class="tdFontcolor" >
						            <bean:message key="label.page.serial"/>
									<bean:write name="pagination" property="current" filter="true" />
									<bean:message key="label.page.sum"/>
									<bean:write name="pagination" property="count" filter="true" />
									<bean:message key="label.page.row"/>
									<bean:write name="pagination" property="pages" filter="true" />
									<bean:message key="label.page.page"/>
							</td>
					        <td  align="right" nowrap class="tdFontcolor">
						          <p align="right"><hrms:paginationdblink name="changeInfoForm" property="pagination" nameId="changeInfoForm" scope="page">
								</hrms:paginationdblink>
							</td>
						</tr>
				</table> 
        	</td>
        </tr>
         <tr>
        	<td colspan="<%=(t+5)%>">
				<table  width="100%" align="center">
			          <tr>
			            <td align="left">
				 	      <html:button styleClass="mybutton" property="b_export" onclick="exports()">
			            		<bean:message key="button.export"/>
				 	      </html:button> 
				 	     	      
			            </td>
			          </tr>          
				</table>
        	</td>
        </tr>
   </table>     

<!-- modify by xiaoyun 2014-9-24 
<table  width="<%=(t+5)*150 %>px" align="center" class="RecordRowP">
 -->

<!-- modify by xiaoyun 2014-9-24 start
<table  width="<%=(t+5)*150 %>px" align="center">
 -->


</html:form>
