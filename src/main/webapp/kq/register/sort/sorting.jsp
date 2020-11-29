<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.kq.register.DailyRegisterForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/kq/register/sort/sorting.js"></script>
<style type="text/css"> 
#dis_sort_table {
           border: 1px solid #C4D8EE;
           height: 230px;    
           width: 230px;            
           overflow: auto;            
           /*margin: 1em 1;*/
}
</style>
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
	
//-->
</script>
<html:form action="/kq/register/daily_registerdata_sort">
<div class="fixedDiv2" style="height: 100%;border: none">
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
				&nbsp;<bean:message key="label.sort.selectfield"/>&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table border="0">
              	<tr valign="bottom">
              		<td align="left" valign="bottom">
        	             <bean:message key="selfservice.query.queryfield"/>     
                    </td>
                    <td align="left" valign="bottom">   
                    </td>
                  	<td width="100%" align="left"  valign="bottom">
                   		<bean:message key="selfservice.query.queryfieldselected"/>
                  	</td>
                  	<td width="100%" align="left" valign="bottom" >
                  	</td>
              	</tr>
                <tr>
                 <td align="center" valign="center"  width="44%">
                   <table align="center" width="100%">
                   <logic:equal name="dailyRegisterForm" property="checkflag" value="1"> 
                   <tr>
                       <td align="center">
 		       		 		<html:select name="dailyRegisterForm" property="fieldid" styleId="fieldid" onchange="changeField();"  style="width:160;font-size:9pt">
			 					<html:optionsCollection property="fieldlist" value="dataValue" label="dataName" />
							</html:select>  
                       </td>
                    </tr>        
                   <tr>
                       <td align="center">
 		       		 	<hrms:optioncollection name="dailyRegisterForm" property="itemlist" collection="list"/>
		              	<html:select name="dailyRegisterForm" property="itemid" multiple="multiple" ondblclick="addfield();removeitem('itemid');" style="height:210px;width:100%;font-size:9pt">
		              	<html:options collection="list" property="name" labelProperty="label"/>
		        		</html:select>	
                       </td>
                    </tr>
                    </logic:equal>
                    <logic:notEqual name="dailyRegisterForm" property="checkflag" value="1">               
	                    <tr>
	                       <td align="center">
	 		       		 		<html:select name="dailyRegisterForm" property="itemid" multiple="multiple" ondblclick="addfield();removeitem('itemid');"  style="height:230px;width:100%;font-size:9pt">
				 					<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
								</html:select> 
	                       </td>
	                    </tr>
                    </logic:notEqual>
                   </table>
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="addfield();removeitem('itemid');">
            		     <bean:message key="button.setfield.addfield"/> 
		           </html:button >
	           <br>
	           <br>
		           <html:button  styleClass="mybutton" property="b_delfield" onclick="deletefield();">
            		     <bean:message key="button.setfield.delfield"/>    
	    	       </html:button >	     
                </td>         
                <td width="44%" align="center" >
                 	<table align="center"  width="100%" border="0">
                  	<tr>
                  	<td width="100%"  >	                  
				       <div id="dis_sort_table" class="common_border_color"  >
				       	<table width="60%" border="0">
				       		<tr>
				       			<td class="TableRow" width="10%" align="left" style="border-left:0px;border-color:red;">&nbsp;</td>
				       			<td class="TableRow" width="65%" align="center"><bean:message key="field.label"/></td>
				       			<td class="TableRow" width="25%" align="center" style="border-right:0px;"><bean:message key="label.query.baseDesc"/></td>
				       		</tr>
				       	</table>
				       </div>    
                 	</td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
                    <html:button  styleClass="mybutton" property="b_up" onclick="upSort();">
            		     <bean:message key="button.previous"/> 
	           		</html:button >
	           <br>
	           <br>
	           		<html:button  styleClass="mybutton" property="b_down" onclick="downSort();">
            		     <bean:message key="button.next"/>    
	           		</html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" style="height:35px;">
          <html:button styleClass="mybutton" property="b_defOrder" onclick="defOrder()">
            		      默认排序
	      		</html:button>
          			
                <html:button styleClass="mybutton" property="b_temporder" onclick="sub()">
                	临时排序
	      		</html:button>
	      			
	       		<html:button styleClass="mybutton" property="b_return" onclick="window.close();">
            		      <bean:message key="button.cancel"/>
	      		</html:button> 	       
          </td>
          </tr>   
</table>
</div>
<input type="hidden" name="sortitemid" id="sortitemid"> 
<html:hidden name="dailyRegisterForm" property="sortitem" styleId="sortitem"/>
<logic:equal name="dailyRegisterForm" property="checkflag" value="1">
<script language="javascript">
defField();
changeField();
</script>
</logic:equal>
<logic:notEqual name="dailyRegisterForm" property="checkflag" value="1">
<script language="javascript">
defField();
</script>
</logic:notEqual>
</html:form>


