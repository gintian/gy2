<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="../../../../js/validate.js"></script>
<script language="javascript">
  function save()
  {
      var dblist=new Array();
      if(!getBrowseVersion() || getBrowseVersion() == 10){
          var vos= document.getElementsByName("left_fields")[0];
          for(var i=0;i<vos.options.length;i++){
              dblist[i] = vos.options[i].value;
          }
	  }else{
          var vos= document.getElementsByName("left_fields");
          for(var i=0;i<vos.left_fields.length;i++){
          	dblist[i] = vos.left_fields[i].value;
          }
	  }
      // var vos= document.getElementsByName("left_fields");
      // var dblist=new Array();
      // for(var i=0;i<vos.left_fields.length;i++){
      // 	dblist[i] = vos.left_fields[i].value;
      // }
      var hashvo=new ParameterSet(); 
      hashvo.setValue("dbnamelist",dblist);
      var request=new Request({method:'post',onSuccess:save_ok,functionId:'1020010201'},hashvo);
  }	
  
  function save_ok(outparamters)
  {
  	// window.returnValue=new Object();
  	// window.close();
      parent.parent.return_vo = new Object();
      winClose();
  }
  function winClose() {
      if(parent.parent.Ext.getCmp('adjust_order')){
          parent.parent.Ext.getCmp('adjust_order').close();
      }
  }
</script>
<html:form action="/system/dbinit/changebase">
	<table width="586" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="left" class="TableRow" nowrap colspan="3">
					<bean:message key="lable.lawfile.adjustorder" />
					&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRowP" colspan="3" nowrap>
				<table>
					<tr>
						<td width="56%" align="left">
							<table width="100%">
								<tr>
									<td height="250" align="left" valign="top">
										<hrms:optioncollection name="dbaseForm" property="dbnamelist" collection="list"/> 
					     	              <html:select property="left_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt">
					                        <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
					                     </html:select> 
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="right">
							<html:button styleClass="mybutton" property="b_up" onclick="upItem($('left_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<html:button styleClass="mybutton" property="b_down" onclick="downItem($('left_fields'));" style="margin-top:30px;">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRowP" nowrap colspan="3" style="height: 35px">
				<input type="button" name="btnreturn" value='<bean:message key="button.ok" />' class="mybutton" onclick=" save()">
				<%--<html:button styleClass="mybutton" property="cancel" onclick="window.close();">--%>
					<%--<bean:message key="button.cancel" />--%>
				<%--</html:button>--%>
					<html:button styleClass="mybutton" property="cancel" onclick="winClose();">
					<bean:message key="button.cancel" />
					</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion()){//非ie浏览器样式调整 wangb 20190521 bug 48156
	var table = document.getElementsByClassName('RecordRowP')[0].getElementsByTagName('table')[0]
	table.setAttribute('width','100%');
	
}

</script>