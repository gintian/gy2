<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
  function sub()
	{
		var rightFiledIDs="";
		var rightFields=$('sort_fields')
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
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			if(n>1)
			{
				alert(a_text+"指标不能重复！");
				return;
			}
		}
		var hashVo=new ParameterSet();
		hashVo.setValue("sortfields",rightFiledIDs.substring(1));
    	var request=new Request({method:'post',asynchronous:false,functionId:'3020091013'},hashVo);	
   	    returnValue="refresh";
	    window.close();
	}
</script>

<html:form action="/gz/gz_accounting/tax/sort_tax_table">

<table width='290px' border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap >
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>               
                <td width="90%" align="center">
                 <table width="100%">               
                  <tr>
                  <td width="100%" align="left">
 		     
 		        <hrms:optioncollection name="taxTableForm" property="alltaxmxfieldlist" collection="list"/>
		              <html:select name="taxTableForm" size="10" property="sort_fields" multiple="multiple" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="name" labelProperty="label"/>
		        </html:select>	
 		     
                 </td>
                  </tr>
                  </table>             
                </td>
               <td width="10%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" nowrap height="35px" >
           
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="button.ok"/>
	      </html:button> 	
	       <html:button styleClass="mybutton" property="b_return" onclick="window.close();">
            		      <bean:message key="button.cancel"/>
	      </html:button>        
          </td>
          </tr>   
</table>
</html:form>