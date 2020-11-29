<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.DataDictionary,com.hrms.hjsj.sys.FieldItem,com.hjsj.hrms.utils.ResourceFactory" %>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<script type="text/javascript" language="javascript">
<!--
function change()
 {	
    id_Factory_Form.action="/system/id_factory/seq_show.do?b_query=link&edition=${id_Factory_Form.edition}";
    id_Factory_Form.submit();
 }  

//-->
</script>
<%
	int i=0;
	
%>
<html:form action="/system/id_factory/seq_show">
		<table width="99%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
		  <THEAD>
			<tr >
           		 <logic:equal name="id_Factory_Form" property="sysorclient" value="2">
   	  	 				<td colspan="16" align="left" height="30px;" valign="top">            
          		 </logic:equal> 	 
           		 <logic:equal name="id_Factory_Form" property="sysorclient" value="1">
   	  	 				<td colspan="12" align="left" height="30px;" valign="top">            
          		 </logic:equal> 			   	  	 	
							<html:radio property="sysorclient" value="1" onclick="change();"/><bean:message key="id_factory.sys"/>			   	  	 	
							<html:radio property="sysorclient" value="2" onclick="change();"/><bean:message key="id_factory.client"/>
							&nbsp;
                            <html:text  property="searchflag" size="15" styleClass="text4"></html:text>
							<hrms:submit style="position:absolute" styleClass="mybutton" property="b_query"><bean:message key="button.query"/></hrms:submit>		   	  	 	
			   	  	 	</td>
			</tr>
			<TR>
				<logic:equal value="2" name="id_Factory_Form" property="sysorclient">
					<td align="center" class="TableRow" nowrap>
						<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>	
					</td>
				</logic:equal>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factroy.sequence_name" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.sequence_desc" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.minvalue" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.maxvalue" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.increase_order" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.loop_modal" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.c_rule" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.prefix" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.suffix" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.currentid" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.id_length" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.increment_o" />
				</td>
				<logic:equal name="id_Factory_Form" property="sysorclient" value="2">
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.prefix_field" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.prefix_field_len" />
				</td>
				<!-- <td align="center" class="TableRow" nowrap>
					<bean:message key="id_factory.byprefix" />
				</td> -->
				</logic:equal>
				<logic:equal value="2" name="id_Factory_Form" property="sysorclient">
					<td align="center" class="TableRow" nowrap>
						<bean:message key="label.edit" />
					</td>
				</logic:equal>
			</TR>
			</THEAD>
			<hrms:paginationdb id="element" name="id_Factory_Form" sql_str="id_Factory_Form.sql" table="" where_str="id_Factory_Form.where" columns="id_Factory_Form.column" order_by="" pagerows="${id_Factory_Form.pagerows}" page_id="pagination" indexes="indexes">
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
					<logic:equal value="2" name="id_Factory_Form" property="sysorclient">
						<td align="center" class="RecordRow" nowrap>
							<hrms:checkmultibox name="id_Factory_Form" property="pagination.select" value="true" indexes="indexes" />
						</td>
					</logic:equal>
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="sequence_name" />
					</td>	
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="sequence_desc" />
					</td>										
					<td align="right" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="minvalue" />
					</td>	
					<td align="right" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="maxvalue" />
					</td>	
					<td align="center" class="RecordRow" nowrap>
						<logic:equal value="0" name="element" property="increase_order">
						<bean:message key="id_factory.desc"/>
						</logic:equal>
						<logic:equal value="1" name="element" property="increase_order">
						<bean:message key="id_factory.asc"/>
						</logic:equal>
					</td>	
					<td align="center" class="RecordRow" nowrap>
						<logic:equal value="0" name="element" property="loop_mode">
						  <bean:message key="lable.zp_plan_detail.status0"/>
						</logic:equal>
						<logic:equal value="1" name="element" property="loop_mode">
						  <bean:message key="id_factory.loop_year"/>
						</logic:equal>
							
					</td>
					<td align="left" class="RecordRow" nowrap>
					<logic:equal value="1" name="element" property="c_rule">
						  &nbsp;<bean:message key="id_factory.c_rule.equal"/>
					</logic:equal>
					<logic:equal value="0" name="element" property="c_rule">
						  &nbsp;<bean:message key="id_factory.c_rule.unequal"/>
					</logic:equal>
							
					</td>					
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="prefix" />
					</td>	
					<td align="left" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="suffix" />
					</td>	
					<td align="right" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="currentid" />
					</td>	
					<td align="right" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="id_length" />
					</td>	
					<td align="right" class="RecordRow" nowrap>
							&nbsp;<bean:write name="element" property="increment_o" />
					</td>
					<logic:equal name="id_Factory_Form" property="sysorclient" value="2">
					<td align="left" class="RecordRow" nowrap>
						<%
							LazyDynaBean ldb = (LazyDynaBean)pageContext.getAttribute("element");
							String prefix_field = (String)ldb.get("prefix_field");
							FieldItem item = DataDictionary.getFieldItem(prefix_field);
							if(item!=null){
								prefix_field = item.getItemdesc();
						%>
						&nbsp;<%=prefix_field %>
						<%
							}
						 %>
							
					</td>
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="prefix_field_len" />							
					</td>
					<!-- <td align="left" class="RecordRow" nowrap>
						<%
							//String byprefix = (String)ldb.get("byprefix");
							//String byprefixview=ResourceFactory.getProperty("datesytle.no");
							//if(byprefix!=null&&"1".equalsIgnoreCase(byprefix)){
								//byprefixview=ResourceFactory.getProperty("datestyle.yes");
							//}
						%>
						<%//=byprefixview %>
							
					</td> -->
					</logic:equal>																														
					<logic:equal value="2" name="id_Factory_Form" property="sysorclient">
					<%
						LazyDynaBean bean = (LazyDynaBean)pageContext.getAttribute("element");
						String sequence_name = (String)bean.get("sequence_name");
					 %>
						<td align="center" class="RecordRow" nowrap>
						<a href="/system/id_factory/id_factoryupdateoradd.do?b_query=link&encryptParam=<%=PubFunc.encrypt("sequence_name="+sequence_name)%>"> <img src="/images/edit.gif" border="0"></a>
						</td>
					</logic:equal>
				</tr>
			</hrms:paginationdb>
		</table>
		<table width="100%" align="center" class="RecordRowP">
			<tr>
				<td align="left" class="tdFontcolor" nowrap>
					<hrms:paginationtag name="id_Factory_Form" pagerows="${id_Factory_Form.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
				</td>
				<td  align="right" nowrap class="tdFontcolor">
					<p align="right">
						<hrms:paginationdblink name="id_Factory_Form" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
			</tr>			

			
		</table>
<table  width="100%" align="center">
				<tr>
					<td align="center" height="35px;">					    

					    <logic:equal value="2" name="id_Factory_Form" property="sysorclient">
						<hrms:submit styleClass="mybutton" property="b_add">
							<bean:message key="button.insert" />
						</hrms:submit>
						<hrms:submit styleClass="mybutton" property="b_delete" onclick="return selectcheckeditem();">
							<bean:message key="button.delete" />
						</hrms:submit>
						</logic:equal>
						<logic:equal value="4" name="id_Factory_Form" property="edition">
						    <hrms:submit styleClass="mybutton" property="br_return">
								<bean:message key="button.return" />
							</hrms:submit>
						</logic:equal>
					</td>
				</tr>      
</table>		
</html:form>
<script>
function selectcheckeditem()
   {
	var nums=0;		
	for(var i=0;i<document.forms[0].elements.length;i++)
	{			
	   if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!="selbox"&&document.forms[0].elements[i].checked)
	   {		   			
		nums++;
		break;
	   }
        }
      if(nums>0){
      	return ifmsdel();
      	}
      	else
      	{
      		alert("请选择要删除项!");
      		return false;
      	}  
  }
</script>