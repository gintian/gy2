<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
	function to_return()
	{
		hrSyncForm.action="/sys/export/SearchHrSyncSet.do?b_query=link";
		hrSyncForm.submit();
	}
function addfield()
{	
	var target_url;
	var type = "${hrSyncForm.type}";
	if(type=="A")
		target_url="/sys/export/SearchHrSyncFiled.do?b_query=link";
	else if(type=="B")
		target_url="/sys/export/SearchHrSyncFiled.do?b_orgfield=link";
	else
	    target_url="/sys/export/SearchHrSyncFiled.do?b_postfield=link";  
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=540,dh=420,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,1, 
         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null)
    {
    	window.location.href="/sys/export/SearchHrSyncFiled.do?b_search=link&type="+"${hrSyncForm.type}";;
    }
    */
    //改用ext 弹窗显示  wangb 20190320
    var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请选择',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo)
    				{
    					window.location.href="/sys/export/SearchHrSyncFiled.do?b_search=link&type="+"${hrSyncForm.type}";;
    				}
				}
			}
	}); 
}
function edit(field)
{
	var dw=250,dh=200,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var orgname=showModalDialog('/system/export/editname.jsp',null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no"); 
    if(orgname!=null && orgname.length>0)
    {
      hrSyncForm.action="/sys/export/SearchHrSyncFiled.do?b_editname=link&field="+field+"&editname="+getEncodeStr(orgname);
      hrSyncForm.submit(); 
    }
    */
    //改用ext 弹窗显示  wangb 20190321
    var url = '/system/export/editname.jsp';
    var win = Ext.create('Ext.window.Window',{
			id:'edit_field',
			title:'请输入',
			width:dw+20,
			height:dh+40,
			resizable:false,
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.orgname && this.orgname!=null && this.orgname.length>0)
    				{
      					hrSyncForm.action="/sys/export/SearchHrSyncFiled.do?b_editname=link&field="+field+"&editname="+getEncodeStr(this.orgname);
      					hrSyncForm.submit(); 
    				}
				}
			}
		 });  
}
function dele()
{
	var len=document.hrSyncForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.hrSyncForm.elements[i].type=="checkbox")
           {
              if(document.hrSyncForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert("请选择要删除的记录！");
          return false;
       }
}
document.onkeypress=function()                //网页内按下回车触发
{
        if(event.keyCode==13)
       		return false;
}
</script>
<html:form action="/sys/export/SearchHrSyncFiled">
<% int i=0;%>

<table width="65%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<thead>
		<tr>
			<td align="center" class="TableRow" nowrap>
				<input type="checkbox" name="selbox" onclick="batch_select(this,'setListForm.select');" title='<bean:message key="label.query.selectall"/>'>
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="gz.templateset.itemcode"/>名称
			</td>
			<td align="center" class="TableRow" nowrap>
				<bean:message key="gz.templateset.itemcode"/>
			</td>            	        	        	        
			<td align="center" class="TableRow" nowrap>
				<bean:message key="kq.duration.define"/><bean:message key="gz.templateset.itemcode"/>&nbsp;&nbsp;
			</td> 
			<td align="center" class="TableRow" nowrap>
				<bean:message key="label.edit"/>&nbsp;&nbsp;
			</td> 
		</tr>
	</thead>
	<hrms:extenditerate id="element" name="hrSyncForm" property="setListForm.list" indexes="indexes"  pagination="setListForm.pagination" pageCount="25" scope="session">
	   	 	 	<%
	   	 	 		
	   	 	 		if(i%2==0){
	   	 	 	%>
	   	 	 		<tr class="trDeep">
	   	 	 	<%
	   	 	 		}else{
	   	 	 	%>
	   	 	 		<tr class="trShallow">
	   	 	 	<%
	   	 	 		}
	   	 	 		i++;
	   	 	 	%>
				   		<td align="center" class="RecordRow" nowrap>
							<hrms:checkmultibox name="hrSyncForm" property="setListForm.select" value="true" indexes="indexes" />
						</td>
						<td align="center" class="RecordRow" width="25%" >
				   			<bean:write name="element" property="fieldname" filter="false"/>&nbsp;
					    </td>
				   		<td align="center" class="RecordRow" width="25%" >
				   			<bean:write name="element" property="field" filter="false"/>&nbsp;
					    </td>
					    <td align="left" class="RecordRow" width="40%">
				   			&nbsp;<bean:write name="element" property="tofield" filter="false"/>&nbsp;
					    </td>
					    <td align="center" class="RecordRow" width="10%">
				   			<img src="/images/edit.gif" onclick="edit('<bean:write name="element" property="field" filter="false"/>')" style="cursor:hand"/>
					    </td>            
					</tr> 
				</hrms:extenditerate>
</table>
<table  width="65%" align="center" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor" ><bean:message key="label.page.serial"/>
			<bean:write name="hrSyncForm" property="setListForm.pagination.current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="hrSyncForm" property="setListForm.pagination.count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="hrSyncForm" property="setListForm.pagination.pages" filter="true" />
			<bean:message key="label.page.page"/>
		</td>
		<td  align="right" nowrap class="tdFontcolor">
			<p align="right">
				<hrms:paginationlink name="hrSyncForm" property="setListForm.pagination"
					nameId="setListForm" propertyId="setlistsProperty">
				</hrms:paginationlink>
			</p>
		</td>
	</tr>
</table>
<table  width="65%" align="center">
	<tr>
		<td align="center" height="35px;">
			<input type="button" name="btnreturn" value='增加' class="mybutton" onclick=" addfield()">
			<hrms:submit styleClass="mybutton" property="b_del" onclick="return dele()">
            		<bean:message key="button.delete"/>
	 	     </hrms:submit>
			<input type="button" name="btnreturn" value='<bean:message key="button.return"/>' class="mybutton" onclick=" to_return()">
		</td>
	</tr>
</table>
</html:form>