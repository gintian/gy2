<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">

	
	
	/**显示指标*/
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(employActualizeForm.left_fields,fieldlist);
	}


				
	/**查询指标*/
	function searchFieldList()
	{
	
	   var tablename=$F('dbpre');
	   var in_paramters="tablename="+tablename;
   	   var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'3000000143'});
	}
	
		
	function next()
	{
		
		var rightFields=$('rightFields')
		if(rightFields.options.length==0)
		{
			alert(GENERAL_SELECT_ITEMNAME+"");
			return;
		}
		setselectitem('rightFields');
		employActualizeForm.action="/hire/employActualize/personnelFilter/personnelFilterTree.do?b_setCondition=b";
		employActualizeForm.submit();
		
	}
	
	
	
	/**填充花名册指标和排序指标*/
	function filloutData()
	{
	    setselectitem('rightFields');
	    setselectitem('sort_rightFields');		
	}
	
	/**初化数据*/
	function MusterInitData()
	{
		searchFieldList()
	}
	
	function goback()
	{
		employActualizeForm.action="/hire/employActualize/personnelFilter/personnelFilterTree.do?b_query=${employActualizeForm.linkDesc}&model=4";
		employActualizeForm.submit();
	}
	
</script>
<html:form action="/hire/employActualize/personnelFilter/personnelFilterTree">
<Br><br>

<table width='65%' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
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
                      		<hrms:optioncollection name="employActualizeForm" property="fieldSetList" collection="list" />
					             <html:select name="employActualizeForm" property="dbpre" size="1"   onchange="searchFieldList();"  style="width:100%"   >
					             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
					             </html:select>	
                                             
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','rightFields');" style="height:209px;width:100%;font-size:9pt">
                         </select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','rightFields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('rightFields');">
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
 		     
 		     <select name="rightFields" multiple="multiple" size="10" ondblclick="removeitem('rightFields');" style="height:230px;width:100%;font-size:9pt">
 		     	  		<logic:iterate id="element" name="employActualizeForm" property="selectedFieldList"  offset="0"> 
	      					<option value='<bean:write name="element" property="itemid"   filter="false"/>%%<bean:write name="element" property="itemdesc"   filter="false"/>%%<bean:write name="element" property="itemtype"   filter="false"/>%%<bean:write name="element" property="itemsetid"   filter="false"/>%%<bean:write name="element" property="fieldsetid"   filter="false"/>' ><bean:write name="element" property="itemdesc"   filter="false"/></option>
            			</logic:iterate>               
 		     </select>
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('rightFields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('rightFields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
          	   <html:button styleClass="mybutton" property="b_next" onclick="goback()">
            		     <bean:message key="static.back"/>
	      	  </html:button> 	
              <html:button styleClass="mybutton" property="b_next" onclick="next()">
            		      <bean:message key="static.next"/>
	      	  </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>
<script language="javascript">
   MusterInitData();
</script>