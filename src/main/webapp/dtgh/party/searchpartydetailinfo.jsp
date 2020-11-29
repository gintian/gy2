<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.dtgh.party.PartyBusinessForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<%
	  int i=0;
%>

<script language="javascript">
  function deletes()
  {
     var len=document.partyBusinessForm.elements.length;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
           if (document.partyBusinessForm.elements[i].type=="checkbox"&&document.partyBusinessForm.elements[i].name!="sel")
            {
              if( document.partyBusinessForm.elements[i].checked==true)
                isCorrect=true;
            }
     }
    if(!isCorrect)
    {
          alert("请选择记录！");
          return false;
     }
     if(confirm("确认要删除记录？"))
     {
          partyBusinessForm.action = "/dtgh/party/searchpartybusinesslist.do?b_delete_sub=link";
          partyBusinessForm.submit();
     }
  }
  function edit(i9999,edittype)
  {
     partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_add_sub=edit&i9999="+i9999+"&subtype="+edittype;
     partyBusinessForm.submit();
  }
  function exeReturn(returnStr,target)
{
  //target_url=returnStr;
 // window.open(target_url,target); 
   partyBusinessForm.action=returnStr;
   partyBusinessForm.target=target;
   partyBusinessForm.submit();
}
function add(){
   partyBusinessForm.action="/dtgh/party/searchpartybusinesslist.do?b_add_sub=link&subtype=add";
   partyBusinessForm.submit();
}
   function winhref(url)
{
   if(url=="")
      return false;
   partyBusinessForm.action=url;
   partyBusinessForm.submit();
} 

function upItem(rowid,codeitemid,i9999){
	var _table=$("tableid");
	var _rowid=parseInt(rowid);
	if(_table.rows.length<3||_rowid<1){
		return;
	}
	var hashvo=new ParameterSet();  
	hashvo.setValue("rowid", _rowid);      
    hashvo.setValue("fieldsetid", '${partyBusinessForm.fieldsetid}');
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
    hashvo.setValue("fieldsetid", '${partyBusinessForm.fieldsetid}');
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
    // _table.moveRow(_rowid+1,_rowid+2);
}
function sub1(o)
{
	partyBusinessForm.action="/dtgh/party/searchpartydetailinfo.do";
	partyBusinessForm.submit(); 	
}
</script>
<html:form action="/dtgh/party/searchpartydetailinfo">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" id="tableid">
           <tr>
             <td align="center" class="TableRow" nowrap>
               <input type="checkbox" name="sel" onclick="batch_select(this,'partyBusinessForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
             </td>
             <td align="center" class="TableRow_2rows" nowrap>
              插入&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
             </td>    
            <logic:iterate id="element"    name="partyBusinessForm"  property="infofieldlist"> 
              <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc"/>&nbsp; 
              </td>
              
             </logic:iterate>
          	 <td align="center" class="TableRow" nowrap>
				<bean:message key="label.zp_exam.sort"/>             	
			</td> 	        	        	        
           </tr>
           <%
           		PartyBusinessForm partyBusinessForm=(PartyBusinessForm)session.getAttribute("partyBusinessForm");
           		String fieldsetid=partyBusinessForm.getFieldsetid();
           		String key=fieldsetid.substring(0,1).toLowerCase()+"0100";
           		int len = partyBusinessForm.getList().size();
            %>
          <hrms:extenditerate  id="element" name="partyBusinessForm" property="partyBusinessForm.list" indexes="indexes"  pagination="partyBusinessForm.pagination" pageCount="${partyBusinessForm.pagerows}" scope="session">
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
            <td align="center" class="RecordRow" width="40" nowrap>
               <hrms:checkmultibox name="partyBusinessForm" property="partyBusinessForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>  
            <td align="center" class="RecordRow" width="40" nowrap>
            	<a href="###" onclick="winhref('/dtgh/party/searchpartybusinesslist.do?b_add_sub=link&subtype=insert&i9999=<bean:write  name="element" property="string(i9999)" filter="true"/>');"><img src="/images/goto_input.gif" border=0></a>
	      </td>
              <td align="center" class="RecordRow" width="40" nowrap>
            	<a href="javascript:edit('<bean:write  name="element" property="string(i9999)" filter="true"/>','edit');"><img src="/images/edit.gif" border=0></a>
	      </td>    
            <logic:iterate id="info"    name="partyBusinessForm"  property="infofieldlist">            
              <logic:equal  name="info" property="itemtype" value="M"> 
               <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %> 
               <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${partyBusinessForm.fieldsetid}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
                  <td align="left" class="RecordRow" ${tiptext} nowrap>               
                   ${showtext}&nbsp;
                  </td> 
              </logic:equal> 
              <logic:notEqual  name="info" property="itemtype" value="M"> 
                    <logic:notEqual  name="info" property="itemtype" value="N">               
                      <td align="left" class="RecordRow" nowrap >      
                    </logic:notEqual>
                    <logic:equal  name="info" property="itemtype" value="N">               
                      <td align="right" class="RecordRow" nowrap >        
                    </logic:equal>          
                   <bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;
                   </td>
              </logic:notEqual>
           </logic:iterate> 
                 <td align="left" class="RecordRow" width="60" nowrap >
                 	<%if(i!=1){ %>
					&nbsp;<a href="javaScript:upItem('${indexes }','<%=vo.getString(key) %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
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
					&nbsp;<a href="javaScript:downItem('${indexes }','<%=vo.getString(key) %>','<bean:write  name="element" property="string(i9999)" filter="true"/>')">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
					<%} %>
				</td>           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         <table  width="100%" class="RecordRowP">
		<tr>
		    <td>
		    	<bean:message key="label.page.serial" />
				<bean:write name="partyBusinessForm" property="partyBusinessForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="partyBusinessForm" property="partyBusinessForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="partyBusinessForm" property="partyBusinessForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />&nbsp;&nbsp;	
					 每页显示<html:text property="pagerows" name="partyBusinessForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:sub1(0);">刷新</a>
			</td>
	        <td  align="right" nowrap>
		          <p align="right"><hrms:paginationlink name="partyBusinessForm" property="partyBusinessForm.pagination"
				nameId="partyBusinessForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="left">
          <tr>
            <td align="left">
               	<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.insert" />" onclick="javascript:add();">          	
	 	<input type="button" name="b_delete" class="mybutton" value="<bean:message key="button.delete" />" onclick="javascript:deletes();">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/dtgh/party/searchpartybusinesslist.do?b_query=link','mil_body')">                 
  
            </td>
          </tr>          
 </table>
</table>

</html:form>
