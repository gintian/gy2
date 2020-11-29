<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
function sub(){
	var rightFiledIDs="";
	var rightFields=$('sort_fields');
	if(rightFields.options.length==0){
		returnValue=0;
	    window.close();
	}
	for(var i=0;i<rightFields.options.length;i++){
		rightFiledIDs+=","+rightFields.options[i].value;
		var a_value=rightFields.options[i].value;
		var n=0;
		var a_text="";
		for(var j=0;j<rightFields.options.length;j++){
			if(rightFields.options[j].value==a_value){
				n++;
				a_text=rightFields.options[j].text;
			}
		}
		if(n>1){
			alert(a_text+ITEM_NOT_RESET+"！");
			return;
		}
	}
	var setname = "${subsetConfsetForm.subset}";
	var sortfieldstr = rightFiledIDs.substring(1);
	var hashvo=new ParameterSet();
	hashvo.setValue("sortitem",sortfieldstr);  
	hashvo.setValue("setid",setname); 
	hashvo.setValue("flag","sort");  
	var request=new Request({asynchronous:false,functionId:'1602010225'},hashvo); 
	returnValue=rightFiledIDs.substring(1);
	window.close();	
}

</script>

<html:form action="/org/autostatic/confset/view_hide">
<table width='98%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		&nbsp;<bean:message key="infor.menu.sortitem"/>&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="92%" align="center" nowrap><div class="common_border_color" style="border:1px solid;border-top: none;">
              <table>
                <tr>               
                <td width="46%" align="center">
                 <table width="100%">               
                  <tr>
                  <td width="100%" align="left">
 		     
 		        <hrms:optioncollection name="subsetConfsetForm" property="sortfieldlist" collection="list"/>
		              <html:select name="subsetConfsetForm" size="10" property="sort_fields" multiple="multiple" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
 		     
                 </td>
                  </tr>
                  </table>             
                </td>
               <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <html:button style="margin-top:30px;" styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                               
                </tr>
              </table> </div>            
            </td>
            </tr>
</table>
<div style="margin-top: 5px;" align="center">
	<html:button styleClass="mybutton" property="b_next" onclick="sub();">
            	<bean:message key="reporttypelist.confirm"/></html:button> 
    <html:button styleClass="mybutton" property="b_cancel" onclick="window.close();">
        		<bean:message key="button.cancel"/></html:button>    
</div>
</html:form>