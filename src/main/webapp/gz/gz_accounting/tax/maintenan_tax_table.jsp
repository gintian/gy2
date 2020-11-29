<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView); 
int version = userView.getVersion();
 %>
<script language="javascript">

   function changeFieldSet(){
	var v = taxTableForm.salaryid.value;
  	var hashvo=new ParameterSet();

    hashvo.setValue("salaryid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
	parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'3020091002'},hashvo);				
  }
  
  function resultChangeFieldSet(outparamters){
  	//Element.hide('cid');
  	var fielditemlist=outparamters.getValue("gzmxprolist");
	AjaxBind.bind(taxTableForm.itemid,fielditemlist);
	
  }
 
  function sub(){
		var rightFiledIDs="";
		var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 returnValue=0;
//			 alert("请选择指标");
	    	 window.close();
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value.toUpperCase()==a_value.toUpperCase())
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			if(n>1)
			{
				alert(a_text+ITEM_NOT_RESET+"！");
				return;
			}
		}
		var hashVo=new ParameterSet();
		hashVo.setValue("maintenanfields",rightFiledIDs.substring(1));
		var dtid=document.getElementById("dtid");
		var dtidvalue="false";
		if(dtid)
		{
		   if(dtid.checked)
		      dtidvalue="true";
		}
		hashVo.setValue("deptid",dtidvalue);
    	var request=new Request({method:'post',asynchronous:false,functionId:'3020091012'},hashVo);	
   	    returnValue="refresh";
	    window.close();
	}

</script>
<html:form action="/gz/gz_accounting/tax/maintenan_tax_table">


<table width='540px;' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow_lrt" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow_lrt" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                     <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;           
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
			 		 <hrms:optioncollection name="taxTableForm" property="gzmxtypelist" collection="list" />
		             <html:select name="taxTableForm" property="salaryid" size="1" onchange="changeFieldSet()" style="width:100%">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                      <hrms:optioncollection name="taxTableForm" property="gzmxprolist" collection="list"/>
		              <html:select name="taxTableForm" size="10" property="itemid" multiple="multiple" ondblclick="additem('itemid','right_fields');" style="height:209px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('itemid','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                   <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 					  <hrms:optioncollection name="taxTableForm" property="rightlist" collection="list"/>
		              <html:select name="taxTableForm" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
 		     
                 </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
                  
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
            <%if(version>=50){ %>
            <tr>
            <td align="left" class="RecordRow">
              <html:checkbox styleId="dtid" property="deptid" name="taxTableForm" value="true"><bean:message key="gz.tax.lsdept"/></html:checkbox>
            </td>
            </tr>
            <%} %>
          <tr >
          <td align="center" nowrap  colspan="3" height="35px">
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	      
	       <html:button styleClass="mybutton" property="b_return" onclick="window.close();">
            		      <bean:message key="button.cancel"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>