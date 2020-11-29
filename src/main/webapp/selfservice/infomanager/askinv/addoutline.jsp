<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>
<style>
table.ftable { background:#FFFFFF; border-collapse:collapse; font-size:12px;  /*color:#666633;*/}
table.ftable td { border:1px solid #C4D8EE; height:22px; padding:0 5px; overflow: hidden; text-overflow:ellipsis; word-break: normal;  }
table.ftable th {border:1px solid #C4D8EE;height:22px;/*color:#336699*/;line-height:22px; padding:0 5px;  background-color:#f4f7f7;}
</style>
<hrms:themes></hrms:themes>
<script type="text/javascript">
function check(){
	var pointName = document.getElementById("pointName").value;
	if(pointName.length>100) {
		alert(ITEM_NAME_LENGTH);
		return false;
	}
	return true;
}
</script>
<hrms:themes />
<html:form action="/selfservice/infomanager/askinv/addoutline">
<table  width="70%" border="0" cellspacing="0" style="margin-top:6px;" align="center" cellpadding="0"> <tr><td align="center" valign="center" nowrap colspan="10"  class="educationtitle">
<img src="/images/shimv.gif">&nbsp;<bean:message key="conlumn.investigate_point.maintopic"/>&nbsp;<img src="/images/shimv1.gif"></td></tr></table>
      <table width="70%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
         <tr height="30">
       		<td align="left" colspan="2" style="height:30px;" class="TableRow" ><bean:message key="lable.investigate_point.repair"/>&nbsp;</td>
          </tr> 
                     <tr class="list3">
                     <td align="right" width="40%"  valign="center"><bean:message key="conlumn.investigate_item.name"/></td>
                     <td width="200">
                      <bean:write name="outlineForm" property="itemName" filter="true"/>&nbsp;
                     
                     </td> </tr>   
                     
                      <tr class="list3">
                	      <td align="right" nowrap valign="center"> <bean:message key="conlumn.investigate_point.name"/></td>
                	      <td align="left"  nowrap>
                	      	<html:text styleId="pointName" name="outlineForm" property="outlinevo.string(name)" styleClass="text4" style="width:400px;"/>
                          </td>
                      </tr> 
                      
                      
                       <tr>
                       <td align="right" nowrap valign="center"> <bean:message key="conlumn.investigate_point.status"/></td>
                      <td align="left"  nowrap>
                      <html:radio name="outlineForm" property="outlinevo.string(status)" value="1"/><bean:message key="datestyle.yes"/>
                      <html:radio name="outlineForm" property="outlinevo.string(status)" value="0"/><bean:message key="datesytle.no"/>   
                     </td>
                      </tr>
                      <tr>
                      <td align="right" nowrap valign="center"><bean:message key="conlumn.investigate_point.describe"/></td>
                      <td align="left"  nowrap>
                      <html:radio name="outlineForm" property="outlinevo.string(describestatus)" value="1"/><bean:message key="datestyle.yes"/>
                      <html:radio name="outlineForm" property="outlinevo.string(describestatus)" value="0"/><bean:message key="datesytle.no"/>   
                     </td>
                      </tr>
                 
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px;">
         	<hrms:submit styleClass="mybutton" property="b_saveadd" onclick="document.outlineForm.target='_self';validate( 'R','outlinevo.string(name)','要点名称');return (document.returnValue && check() && ifqrbc());">

            		<bean:message key="button.save"/>
	 	</hrms:submit>  
		<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>	 	   
            </td>
          </tr>                                                      
	        
      </table>
<br>

<%
	int i=0;
%>
  
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">

   	  <thead>
   	  
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'outlineForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_point.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_point.status"/>&nbsp;
	    </td>
                      	   	    	    	    
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	     
           	    	    		        	        	        
           </tr>
   	  </thead>
   	  
          <hrms:extenditerate id="element" name="outlineForm" property="outlineForm.list" indexes="indexes"  pagination="outlineForm.pagination" pageCount="10" scope="session">
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
            <td align="center" class="RecordRow" nowrap width="20">
     		   <hrms:checkmultibox name="outlineForm" property="outlineForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            
            <td align="left" class="RecordRow" nowrap width="300" style="word-break:break-all">
                  <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <logic:equal name="element" property="string(status)" value="1">
	    	   <bean:message key="datestyle.yes"/>
	    	   </logic:equal>
	    	   <logic:equal name="element" property="string(status)" value="0">
	    	   <bean:message key="datesytle.no"/>
	    	   </logic:equal>&nbsp;
	    </td>
            <%
            	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
            	String pointid = vo.getString("pointid");
            	String status = vo.getString("status");
             %>            	    	    
           
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/addoutline.do?b_query=link&encryptParam=<%=PubFunc.encrypt("pointid="+pointid+"&status="+status)%>"><img src="/images/edit.gif" border=0></a>
	    </td>
	     
           	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
					<bean:write name="outlineForm" property="outlineForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="outlineForm" property="outlineForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="outlineForm" property="outlineForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="outlineForm" property="outlineForm.pagination"
				nameId="outlineForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	
         	<hrms:submit styleClass="mybutton" property="b_delete">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
        
            </td>
           
          </tr>          
</table>

</html:form>

