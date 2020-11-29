<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes />
<script language="JavaScript" src="../../../js/validate.js"></script>
<script language="JavaScript" src="../../../js/function.js"></script>
<script language="JavaScript">
  
	function showSetList(outparamters)
	{
		var setlist=outparamters.getValue("setlist");
		AjaxBind.bind(conditionQueryForm.setname,setlist);
		if($('setname').options.length>0)
		{
		  $('setname').options[0].selected=true;
		  //$('setname').fireEvent("onchange");
		  searchFieldList();//haosl 解决条件选择备选指标报不支持fireEvent();
		}
	}
	
	
	
	
	
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(conditionQueryForm.left_fields,fieldlist);
	}


	function searchFieldList()
	{
	  
	   var codeItemID=$F("setname");	
	   var In_paramters="codeID="+codeItemID;  
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'90100140009'});
	}
	
	function pre(v)
	{
		
		if(v==1)
			conditionQueryForm.action="/selfservice/performance/hquery_interface.do?br_return=return";
		else
			conditionQueryForm.action="/selfservice/performance/hquery_interface.do?br_return3=return";
		
		conditionQueryForm.submit();
	}
	
	
	function MusterInitData(infor)
	{
	   var pars="base="+infor;
   	   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:showSetList,functionId:'90100140013'});
	}
	
	function next()
	{
		var vos= document.getElementsByName('right_fields');  
  		if(vos[0].options.length==0)
  		{
  			alert(SEL_INDEX);
  			return;
  		}
		setselectitem('right_fields');
		conditionQueryForm.action="/selfservice/performance/hquery_interface.do?b_next=link";
		conditionQueryForm.submit();
	}
</script>

<style>

.RecoRowConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}

</style>

<html:form action="/selfservice/performance/hquery_interface">
<html:hidden property="plan_id"/>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
  <tr>  
    <td valign="top">  
    <br>
    <br>
    <br>   
     <table width="70%" border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="static.select"/>   &nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
        <td align="center" class="RecoRowConition common_border_color" nowrap>
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
                  	<select name="setname" size="1"  style="width:100%" onchange="searchFieldList();">    
			             <option value="1111">#</option>
			</select>
                  	
                  
                  
                  </td>
                 </tr>
                <tr>
                 <td align="center">
                 	<select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
                      </select>

                   </td>
                  </tr>
                 </table>
                </td>
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button>
	            <br>
	            <br>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button>	
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
                     <hrms:optioncollection name="conditionQueryForm" property="fieldlist" collection="selectedlist"/> 
     	             <html:select name="conditionQueryForm" property="right_fields"   multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
     	             		  <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
 		                 </html:select>
                   </td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecoRowConition common_border_color" nowrap  colspan="3" style="height:35px">
          	
          	 <html:button  styleClass="mybutton" property="br_return" onclick="pre(${conditionQueryForm.flag})" >
            		     <bean:message key="button.query.pre"/>
	         </html:button>
          
           	 <input type="button" name="bt_next"  value="<bean:message key="button.query.next"/>" class="mybutton" onclick="next()">  
	        <!--  
		 <hrms:submit styleClass="mybutton" property="b_next" onclick="setselectitem('right_fields');">
            		      <bean:message key="button.query.next"/>
	      	 </hrms:submit>   
	        -->     
         </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>
</html:form>
<script language="javascript">
   MusterInitData('1');
</script>