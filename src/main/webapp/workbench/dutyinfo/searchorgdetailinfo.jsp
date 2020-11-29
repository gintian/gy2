<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.duty.DutyInfoForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo,com.hrms.frame.utility.AdminCode"%>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<%
	  int i=0;
%>
<script type="text/javascript">
<!--
 function edit1(code,i9999,edittype)
  {
     dutyInfoForm.action="/workbench/dutyinfo/searchdetailinfolist.do?b_edit=edit&code="+code+"&i9999="+i9999+"&edittype="+edittype;
     dutyInfoForm.submit();
  }
  function edit2(code,i9999,edittype)
  {
     dutyInfoForm.action="/workbench/dutyinfo/searchdetailinfolist.do?b_edit=edit&code="+code+"&i9999="+i9999+"&edittype="+edittype;
     dutyInfoForm.submit();
  }
  function exeReturn(returnStr,target)
  {
     dutyInfoForm.action=returnStr;
     dutyInfoForm.target=target;
     dutyInfoForm.submit();
  }
  	function clearstatus(){
		window.status="";
	}
	
	function upItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||_rowid<1){
		return;
	}
	var hashvo=new ParameterSet();  
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("fieldsetid", '${dutyInfoForm.setname}');
    hashvo.setValue("codeitemid", codeitemid);
    hashvo.setValue("i9999", i9999);
    hashvo.setValue("type", 'up');
    var request=new Request({method:'post',onSuccess:upItemview,functionId:'3409000022'},hashvo);
}

function upItemview(outparamters){
	var rowid=parseInt(outparamters.getValue("rowid"));
	var _rowid=rowid;
	var _table=$("tableid");
	var _row1=_table.rows[rowid];
	var _row2=_table.rows[rowid+1];
	var tempclass=_row1.className;
	_row1.className=_row2.className;
	_row2.className=tempclass;
	if(_table.rows.length<3||_rowid<=1){
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
		//alert(_cell1);
		//_cell1=_cell1.replace('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;','&nbsp');
		//_cell1=_cell1.replace('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;','');
		//_cell1=_cell1.replace('downItem','upItem').replace('down01','up01')+_cell1;
		//alert(_cell1);
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
		//alert(_cell2);
		//_cell2='&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;'+((_cell2.split('&nbsp;'))[2]);
		//alert(_cell2);
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	}else{
		var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
		var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
		_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
		_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	}
	if(_table.moveRow){
		_table.moveRow(_rowid+1,_rowid);
	}else{
		_table.childNodes[1].insertBefore(_row2,_row1);
	}
}
function downItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||(_rowid+2)==_table.rows.length){
		return;
	}
	var hashvo=new ParameterSet();          
   	hashvo.setValue("rowid", _rowid);          
    hashvo.setValue("fieldsetid", '${dutyInfoForm.setname}');
    hashvo.setValue("codeitemid", codeitemid);
    hashvo.setValue("i9999", i9999);
    hashvo.setValue("type", 'down');
    var request=new Request({method:'post',onSuccess:downItemview,functionId:'3409000022'},hashvo);
}

function downItemview(outparamters){
	var rowid=parseInt(outparamters.getValue("rowid"));
	var _rowid=rowid;
	var _table=$("tableid");
	var _row1=_table.rows[rowid+1];
	var _row2=_table.rows[rowid+2];
	var tempclass=_row1.className;
	_row1.className=_row2.className;
	_row2.className=tempclass;
	var _cell1=_row1.cells[_row1.cells.length-1].innerHTML;
	var _cell2=_row2.cells[_row2.cells.length-1].innerHTML;
	_row1.cells[_row1.cells.length-1].innerHTML=_cell2;
	_row2.cells[_row2.cells.length-1].innerHTML=_cell1;
	if(_table.moveRow){
		_table.moveRow(_rowid+1,_rowid+2);
	}else{
		_table.childNodes[1].insertBefore(_row2,_row1);
	}
}
//-->
</script>
<html:form action="/workbench/dutyinfo/searchdetailinfolist">
<html:hidden name="dutyInfoForm" property="setname" />
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" id="tableid">
           <tr>
             <td align="center" class="TableRow"  width='30' nowrap>
             <input type="checkbox" name="selbox" onclick="batch_select(this,'orgInfoForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
             </td>
             <logic:equal name="dutyInfoForm" property="setprv" value="2">
              <td align="center" class="TableRow"  width='40' nowrap>
		  <bean:message key="button.new.insert"/>            	
              </td>     
             </logic:equal>	
             <logic:equal name="dutyInfoForm" property="setprv" value="2">
              <td align="center" class="TableRow"  width='40' nowrap>
		  <bean:message key="label.edit"/>            	
              </td>     
             </logic:equal>	
             <logic:equal name="dutyInfoForm" property="setprv" value="3">
              <td align="center" class="TableRow"  width='40' nowrap>
		  <bean:message key="label.edit"/>            	
              </td>   
              </logic:equal>	
            <logic:iterate id="element"    name="dutyInfoForm"  property="infofieldlist"> 
                <logic:match value="z0" name="element" property="itemid"><!-- 年月标示 -->
              		<td align="center"  class="TableRow" width='90' nowrap>
              			<bean:write  name="element" property="itemdesc"/>&nbsp; 
              		</td>
              	</logic:match>
              	<logic:match value="z1" name="element" property="itemid"><!-- 次数 -->
              		<td align="center" class="TableRow" width='40' nowrap>
              			<bean:write  name="element" property="itemdesc"/>&nbsp; 
              		</td>
              	</logic:match>
              	<logic:notMatch value="z0" name="element" property="itemid">
              		<logic:notMatch value="z1" name="element" property="itemid">
	              		<td align="center" class="TableRow" nowrap>
	              			<bean:write  name="element" property="itemdesc"/>&nbsp; 
	              		</td>
              		</logic:notMatch>
              	</logic:notMatch>
             </logic:iterate>  
              <td align="center" class="TableRow" nowrap>
				<bean:message key="label.zp_exam.sort"/>             	
			</td>  
             	        	        	        
           </tr>
   	  
   	  <%
           		DutyInfoForm dutyInfoForm=(DutyInfoForm)session.getAttribute("dutyInfoForm");
           		int len = dutyInfoForm.getOrgInfoForm().getList().size();
            %>
          <hrms:extenditerate id="element" name="dutyInfoForm" property="orgInfoForm.list" indexes="indexes"  pagination="orgInfoForm.pagination" pageCount="${dutyInfoForm.pagerows}" scope="session">
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
          RecordVo vo=(RecordVo)element;       
          %>  
            <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="dutyInfoForm" property="orgInfoForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>
            <logic:equal name="dutyInfoForm" property="setprv" value="2">
                <td align="center" class="RecordRow" nowrap>
            	   <a href="javascript:edit1('${dutyInfoForm.code}','<bean:write  name="element" property="string(i9999)" filter="true"/>','insert');"><img src="/images/goto_input.gif" border=0></a>
	         </td>
              </logic:equal>
            <logic:equal name="dutyInfoForm" property="setprv" value="2">
                <td align="center" class="RecordRow" nowrap>
            	   <a href="javascript:edit1('${dutyInfoForm.code}','<bean:write  name="element" property="string(i9999)" filter="true"/>','update');"><img src="/images/edit.gif" border=0></a>
	         </td>
              </logic:equal>	
              <logic:equal name="dutyInfoForm" property="setprv" value="3">
                <td align="center" class="RecordRow" nowrap>
            	   <a href="javascript:edit2('${dutyInfoForm.code}','<bean:write  name="element" property="string(i9999)" filter="true"/>','update');"><img src="/images/edit.gif" border=0></a>
	         </td>
              </logic:equal>
               
            <logic:iterate id="info"    name="dutyInfoForm"  property="infofieldlist">   
             <logic:equal  name="info" property="itemtype" value="M">          
               <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %> 
               <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${orgInfoForm.setname}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
                  <td align="left" class="RecordRow" ${tiptext} nowrap>               
                   ${showtext}&nbsp;
              </td>
              </logic:equal>
              <logic:notEqual  name="info" property="itemtype" value="M"> 
                 <logic:notEqual  name="info" property="itemtype" value="N">               
                      <td align="left" class="RecordRow" nowrap>        
                    </logic:notEqual>
                    <logic:equal  name="info" property="itemtype" value="N">               
                      <td align="right" class="RecordRow" nowrap>        
                    </logic:equal> 
                   <bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;
                 </td>
              </logic:notEqual>
             </logic:iterate> 
                <td align="left" class="RecordRow" width="55" nowrap>
                 	<%if(i!=1){ %>
					&nbsp;<a href="javaScript:upItem('${indexes }','<%=vo.getString("e01a1") %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%}else{ %>
						<script type="text/javascript">
		           			if(isIE6()){
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}else{
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}
		           	    </script>
					<%} %>
				    <%if(len==i){ %>
				    	<script type="text/javascript">
		           			if(isIE6()){
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}else{
		           				document.write("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		           			}
		           	    </script>
				    <%}else{ %>
					&nbsp;<a href="javaScript:downItem('${indexes }','<%=vo.getString("e01a1") %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
					<%} %>
				</td>             	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		         <!-- 为了可以调节页面显示的条数，使用下面的方法，这里注掉 guodd 2014-3-26   <bean:message key="label.page.serial"/>
					<bean:write name="dutyInfoForm" property="orgInfoForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="dutyInfoForm" property="orgInfoForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="dutyInfoForm" property="orgInfoForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
					--> 
					<hrms:paginationtag name="dutyInfoForm"
								pagerows="${dutyInfoForm.pagerows}" property="orgInfoForm.pagination"
								 refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="dutyInfoForm" property="orgInfoForm.pagination"
				nameId="orgInfoForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="100%" align="left">
          <tr>
            <td align="left">
            <html:hidden name="dutyInfoForm" property="edittype" value="new"/>
        <logic:equal name="dutyInfoForm" property="setprv" value="2">
         <hrms:submit styleClass="mybutton" property="b_edit">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>  
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>  
	 	<logic:equal name="dutyInfoForm" property="returnvalue" value="scanduty">
	 	   <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${dutyInfoForm.return_codeid}','nil_body')">
	 	</logic:equal>
	 	 <logic:equal name="dutyInfoForm" property="returnvalue" value="orgpre">
         	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="clearstatus(),exeReturn('/org/orgpre/postable.do?b_query=link&b0110=${dutyInfoForm.b0110 }&setid=${dutyInfoForm.setid}&a_code=${dutyInfoForm.a_code}&infor=${dutyInfoForm.infor}&unit_type=${dutyInfoForm.unit_type}&nextlevel=${dutyInfoForm.nextlevel}','mmil_body')">                 
         </logic:equal>
	  </logic:equal>
	  <logic:equal name="dutyInfoForm" property="returnvalue" value="75"><!--预警-->
	  		<logic:equal value="bi" name="userView" property="bosflag">
         		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','i_body')">                 
         	</logic:equal>
         	<logic:notEqual value="bi" name="userView" property="bosflag">
         		<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/system/warn/result_manager.do?b_query=link','il_body')">                 
         	</logic:notEqual>
         </logic:equal>
	 	 <!--<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>-->  
            </td>
          </tr>          
 </table>
</html:form>
<%
String codeitemid=request.getParameter("codeitemid");
if(codeitemid!=null&&codeitemid.length()>0){
String name=AdminCode.getCodeName("@K",codeitemid);
%>
<script type="text/javascript">
<!--
	//window.status="当前岗位:<%=name %>";
//-->
</script>
<%
}
%>