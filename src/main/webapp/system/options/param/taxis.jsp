<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
   function showLawbasefile(outparamters)
    {
       var lawbaselist=outparamters.getValue("lawbasefilelist");
       AjaxBind.bind(sysinfosortForm.tagorder,lawbaselist);
    }
   function SearchLawbasefile(base_id,basetype)
    {
       var hashvo=new ParameterSet();
       var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:showLawbasefile,functionId:'1012010006'},hashvo);
   }
  function save()
   {
   	/* Ext.window弹框使用form提交在firefox和chrome下有问题，会跟关闭window刷新界面请求冲突，导致有些请求会失效。此处用ajax提交*/
	   var options = document.getElementById("tagorder").options;
	   var orders = '';
	   for(var i=0;i<options.length;i++){
	   		orders+=options[i].value+",";
	   }
	   var hashvo=new ParameterSet();
	   hashvo.setValue("tagorder",orders);
	   var request=new Request({method:'post',asynchronous:false,parameters:null,onSuccess:function (out) {
			   winclose(true);
		   },functionId:'1012010007'},hashvo);

   }	
</script>
<html:form action="/system/param/sysinfosort" target="_self">
	<table width="390" border="0" cellspacing="0" align="center" cellpadding="0">
		<thead>
			<tr>
				<td align="left" class="TableRow" nowrap colspan="3">
					<bean:message key="lable.lawfile.adjustorder" />
					&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRowP" nowrap>
				<table>
					<tr>
						<td width="96%" align="center">
							<table width="100%">
								<tr>
									<td height="250" valign="top">
										<select id="tagorder" name="tagorder" multiple="multiple" style="height:250px;width:100%;font-size:9pt">
										</select>
									</td>
								</tr>
							</table>
						</td>
						<td width="4%" align="center">
							<html:button styleClass="mybutton" property="b_up" onclick="upItems($('tagorder'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down" onclick="downItems($('tagorder'));">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRowP" nowrap colspan="3"  style="height: 35px">
				<html:button styleClass="mybutton" property="cancel" onclick="save();">
					<bean:message key="button.ok" />
				</html:button>
				<html:button styleClass="mybutton" property="cancel" onclick="winclose();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
   SearchLawbasefile();
   
function winclose(beReload){
	if(parent.parent.Ext && parent.parent.Ext.getCmp('paixu')){
		parent.parent.Ext.getCmp('paixu').beReload = beReload;
		parent.parent.Ext.getCmp('paixu').close();
	}else{
		window.close();
	}
}   
</script>
