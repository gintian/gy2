<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
	function checkRadio(){
		var len=document.outsyncFrom.elements.length;
		var n=0;
		var id="";
		var i;
		for (i=0;i<len;i++)
		{
			if (document.outsyncFrom.elements[i].type=="checkbox")
			{
				if(document.outsyncFrom.elements[i].checked){
					n++;
					id=document.outsyncFrom.elements[i].value;
				}
            }
        }
        if(n<1)
        {
           alert("请选择发送系统！");
           return false;
        }else  if(n>1)
        {
           alert("只能选择一个");
           return false;
        } 
        return id;      
    }	
	function send()
	{
	  var id=checkRadio();
	  if(id)
	  {
	     var thevo=new Object();
	     thevo.id=id;
	     thevo.flag="true";
	     
	     if(parent.Ext && parent.Ext.getCmp('handwork_send')){//ext 弹窗返回数据  wangb 20190320
			parent.Ext.getCmp('handwork_send').return_vo = thevo;
		 }else{
	         window.returnValue=thevo;
	     }
         //window.close();
         winclose();
	  }
	    
	  
	}
	//关闭弹窗    wangb 20190320
	function winclose(){
		if(parent.Ext && parent.Ext.getCmp('handwork_send')){
			parent.Ext.getCmp('handwork_send').close();
			return;
		}
		window.close();
	}
//-->
</script>
<html:form action="/system/outsync/outsynclist.do">
	<table width="400" border="0" cellpadding="0" cellspacing="0"
		align="center">
		<tr>
			<td>
				<table width="100%" border="0" cellpadding="0" cellspacing="0"
					class="ListTable">
					<tr>
						<td align="center" class="TableRow" nowrap width=50 style="border-bottom:0px;">
							选择							
						</td>
						<td align="center" class="TableRow" nowrap width=80 style="border-bottom:0px;">
							<bean:message key="label.code" />
						</td>
						<td align="center" class="TableRow" nowrap style="border-bottom:0px;">
							<bean:message key="column.name" />
						</td>						
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<div style="height:250px;overflow-y:scroll;overflow-x:hidden;padding:0px;" class="RecordRow">
				<table width="100%" border="0" cellpadding="1" cellspacing="0"
					class="ListTable">
					<hrms:paginationdb id="element" name="outsyncFrom"
						sql_str="select sys_id,sys_name" where_str="from t_sys_outsync where state=1"
						columns="sys_id,sys_name" page_id="pagination" pagerows="10"
						indexes="indexes">
						<tr>
							<td align="center" class="RecordRow" nowrap width=50 style="border-width:0px 1px 1px 0px">
								<input type="checkbox" name="selbox"
								value='<bean:write name="element" property="sys_id" />'>
							</td>
							<td align="center" class="RecordRow" nowrap width=80 style="border-width:0px 1px 1px 1px">
								<bean:write name="element" property="sys_id" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" style="border-width:0px 0px 1px 1px">
								<div style="width:170px;">
								<bean:write name="element" property="sys_name" />
								</div>
							</td>							
						</tr>						
					</hrms:paginationdb>
				</table>
				</div>
			</td>
		</tr>
		<tr>
			<td class="RecordRowP">
				<table width="70%" align="right">
					<tr>						
						<td align="right" nowrap class="tdFontcolor">
							<hrms:paginationdblink name="outsyncFrom" property="pagination"
								nameId="outsyncFrom" scope="page">
							</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" valign="bottom" height="35px">
				<html:button styleClass="mybutton" property="b_add" onclick="send()">
确定
				</html:button>
				<html:button styleClass="mybutton" property="b_del" onclick='winclose();'>
					关闭
				</html:button>
				
			</td>
		</tr>
	</table>
</html:form>