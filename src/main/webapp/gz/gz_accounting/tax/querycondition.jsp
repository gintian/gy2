<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
   
  function sub()
	{
		var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=$('conright_fields')
		if(rightFields.options.length==0)
		{
			alert(GENERAL_SELECT_ITEMNAME+"！");
			return;
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
			
			
		}
		var querystr = rightFiledIDs.substring(1);
		document.getElementById("condtionsql").value = null;
		var strurl="/gz/gz_accounting/tax/setcondition.do?b_query=link`querystr="+querystr;
   			var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
   			if(isIE6() ){
   			var value = window.showModalDialog(iframe_url,arguments,"dialogWidth=540px;dialogHeight=450px;status:no");	
   			}else{
   			var value = window.showModalDialog(iframe_url,arguments,"dialogWidth=540px;dialogHeight=420px;status:no");	
   			}	
		
		if(value!=null)       
		{
			window.returnValue = value;	        
			window.close();
		}

	}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
</script>
<html:form action="/gz/gz_accounting/tax/querycondition">

<table width='440px' border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
				<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="99%" align="center" class="RecordRow" nowrap colspan="3">
              <table>
                <tr>
                 <td align="center"  width="45%">
                   <table align="center" width="99%">
                    <tr>
                    <td align="left">
                     	<bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;           
                    </td>
                    </tr>
                   <tr>
                   <td align="center">
	                    <hrms:optioncollection name="taxTableForm" property="congzmxprolist" collection="list"/>
		                <html:select name="taxTableForm" size="10" property="conitemid" multiple="multiple" ondblclick="additem('conitemid','conright_fields');" style="height:230px;width:100%;font-size:9pt">
		                <html:options collection="list" property="name" labelProperty="label"/>
		        	    </html:select>		
                   </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                    <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('conitemid','conright_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           		</html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('conright_fields');">
            		<bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                <td width="45%" align="center">
                 <table width="99%">
                  <tr>
                  <td width="99%" align="left">
                   	   <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="99%" align="left">
		              <html:select name="taxTableForm" size="10" property="conright_fields" multiple="multiple" ondblclick="removeitem('conright_fields');" style="height:230px;width:100%;font-size:9pt">
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
          <tr>
          <td align="center" class="RecordRow" nowrap style="padding-top: 2px;" colspan="3">
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            	   <bean:message key="button.query.next"/>
	      	  </html:button> 	       
          </td>
          </tr>   
</table>
<html:hidden name="taxTableForm" property="condtionsql" />
</html:form>