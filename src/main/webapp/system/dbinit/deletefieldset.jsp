<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
  function save()
  {
  	var hashvo=new ParameterSet();          
	var vos= document.getElementsByName("fieldsetlist")[0];
    var code_fields=new Array();        
	var index = 0;
    for(var i=0;i<vos.length;i++)
    {
	    if(vos.options[i].selected)
	    {
	    	var valueS=vos.options[i].value; 
	    	code_fields[index]=valueS;         
	    	index++;
	    }
    }
    if(index==0)
    {
    	alert("请选择要删除的子集");
    	return;
    }
    if(confirm("<bean:message key="gz.acount.determined.del" />")){
	    hashvo.setValue("infor",'${dbinitForm.infor}');
	    hashvo.setValue("fieldsetlist",code_fields); 
	    var request=new Request({method:'post',onSuccess:save_ok,functionId:'1020010145'},hashvo);
	}
   }
   function save_ok(outparamters)
   {
   		var thevo=new Object();
	 	// window.returnValue=thevo;
	 	// window.close();
	   parent.parent.return_vo = thevo;
	   winClose();
   }
   function MusterInitData()
	{
	   var hashvo=new ParameterSet();
	   hashvo.setValue("infor",'${dbinitForm.infor}');
   	   var request=new Request({method:'post',onSuccess:showSetList,functionId:'1020010144'},hashvo);
	}
	
	/**从后台取得相应的数据,初始化前台*/
	function showSetList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldsetlist");
		AjaxBind.bind(dbinitForm.fieldsetlist,fieldlist);
	}
	function winClose() {
		if(parent.parent.Ext.getCmp('deletefieldset')){
            parent.parent.Ext.getCmp('deletefieldset').close();
		}
    }
</script>
<html:form action="/system/dbinit/inforlist">
	<table width="530" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="left" class="TableRow" nowrap >
					<bean:message key="button.delete" /><bean:message key="label.zp_options.subset" />
					&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td align="center" class="RecordRow" nowrap>
				<table>
					<tr>
						<td width="300" align="center">
							<html:select styleId="right" name="dbinitForm" property="fieldsetlist" multiple="multiple" size="10"  style="height:230px;width:100%;font-size:9pt">
 							</html:select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap  style="height: 35px">
				<input type="button" name="btnreturn" value='<bean:message key="button.delete" />' class="mybutton" onclick=" save();">
				<%--<html:button styleClass="mybutton" property="cancel" onclick="window.close();">--%>
					<%--<bean:message key="button.cancel" />--%>
				<%--</html:button>	--%>
				<html:button styleClass="mybutton" property="cancel" onclick="winClose();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
  	MusterInitData();
</script>