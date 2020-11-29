<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.fixedDiv_self
{ 
	overflow:auto; 
	height:300 ; 
	width:450; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}

</style>
<script>
   function add(){   	  
       target_url="/performance/options/perKnowAdd.do?b_add=link`info=save"; 
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
       var config = {
           width:400,
           height:210,
           type:'2'
       }
       modalDialog.showModalDialogs(iframe_url,"perknowAddglWin",config,add_ok);
	}
	function add_ok(return_vo){
        if(return_vo==null)
            return;
        if(return_vo.flag=="true")
        {
            reflesh();
        }
    }
   function edit(knowId)
   {
	   var target_url="/performance/options/perKnowAdd.do?b_edit=link`knowId="+knowId+"`info=edit";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
       var config = {
           width:400,
           height:210,
           type:'2'
       }
       modalDialog.showModalDialogs(iframe_url,"perknowAddglWin",config,add_ok);
   }

	function checkdelete(){
			var str="";
			for(var i=0;i<document.perKnowForm.elements.length;i++)
			{
				if(document.perKnowForm.elements[i].type=="checkbox")
				{
					if(document.perKnowForm.elements[i].checked==true  && document.perKnowForm.elements[i].name!="selbox")
					{
						str+=document.perKnowForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert('<bean:message key="jx.paramset.selDel"/>');
				return;
			}else{
				if(confirm("确认删除了解程度？"))
    			{	
					perKnowForm.action="/performance/options/perKnowList.do?b_delete2=link&deletestr="+str; 
				 	perKnowForm.submit();
				}
			}
	  }
	  
	  
	function IfWindowClosed() {
		if (newwindow.closed == true) { 
			window.clearInterval(timer)
			perKnowForm.action="/performance/options/perKnowList.do?b_query2=link"
		    perKnowForm.submit();
		}
	}
	function toSorting(){
		var thecodeurl="/performance/options/perKnowSort.do?b_sort=link";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
        var config = {
            width:450,
            height:430,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,"toSortingWin",config,toStirng_sort_ok);

	}
	function toStirng_sort_ok(return_vo){
        if(return_vo!=null){
            reflesh();
        }
   }
	function reflesh(){		
		document.perKnowForm.action="/performance/options/perKnowList.do?b_query2=link";
	    document.perKnowForm.submit();
   }

</script>
<hrms:themes></hrms:themes>
<%
	int i=0;
%>
<html:form action="/performance/options/perKnowList">
	<table  border="0" align="center">
		<tr>
			<td>

				<div class="fixedDiv_self common_border_color">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
       <tr class="fixedHeaderTr">
         <td align="center" class="TableRow" nowrap width="10%" style="border-top:0px;border-left:0px;">
		    <input type="checkbox" name="selbox" onclick="batch_select(this, 'setlistform.select');">
         </td>         
         <td align="center" class="TableRow" nowrap  style="border-top:0px;">
		   <bean:message key="report.number"/>
	     </td>          
         <td align="center" class="TableRow" nowrap  style="border-top:0px;">
		      <bean:message key='column.name' />
	     </td>
	      <td align="center" class="TableRow" nowrap  style="border-top:0px;">
			 <bean:message key='kh.field.flag' />
         </td> 
	      <td align="center" class="TableRow" nowrap  style="border-top:0px;border-right:0px;">
			<bean:message key='lable.tz_template.edit' />
         </td>         
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="perKnowForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="1000" scope="session">
          <bean:define id="nid" name="element" property="string(know_id)"/>
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
        <td align="center" class="RecordRow" nowrap style="border-left:0px;border-top:0px;">
     		<hrms:checkmultibox name="perKnowForm" property="setlistform.select" value="true" indexes="indexes"/>
   	    </td>          
        <td align="left" style="border-top:0px;" class="RecordRow" nowrap>
            &nbsp; <bean:write name="element" property="string(know_id)" filter="true"/>
             <Input type='hidden' value='<bean:write name="element" property="string(know_id)" filter="true"/>'  name='knowId' />
              <Input type='hidden' value='<bean:write name="element" property="string(seq)" filter="true"/>'  name='seq' />
	    </td>        
        <td align="left" style="border-top:0px;" class="RecordRow" nowrap>
          &nbsp;   <bean:write name="element" property="string(name)" filter="true"/>
	    </td>
        <td align="left" style="border-top:0px;" class="RecordRow" nowrap>
       		<logic:equal name="element" property="string(status)" value="1">
		     &nbsp;	<bean:message key='kh.field.yx' />
		    </logic:equal>
		    <logic:equal name="element" property="string(status)" value="0">
		     &nbsp;	<bean:message key='kh.field.wx' />
			</logic:equal>
        </td>
	     <td align="center" class="RecordRow" nowrap style="border-right:0px;border-top:0px;">
			<a onclick="edit('<bean:write name="element" property="string(know_id)" filter="true"/>');"><img src="/images/edit.gif" border=0 style="cursor:hand;"></a>
		</td>        
       </tr>
    </hrms:extenditerate>
</table>
		</div>
	</td>
				</tr>
	</table>
<table  width="100%">
          <tr>
            <td  align="center">
 
         	<input type='button' class="mybutton" property="b_add"  onclick='add()' value='<bean:message key="button.insert"/>'  />

            <input type='button' class="mybutton" property="b_delete"  onclick='checkdelete()' value='<bean:message key="button.delete"/>'  />

         <input type="button"  value="<bean:message key='kq.item.change'/>" onclick="toSorting();" Class="mybutton">
            </td>
          </tr>          
</table>
</html:form>