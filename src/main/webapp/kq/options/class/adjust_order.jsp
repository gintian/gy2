<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
   function showLawbasefile(outparamters)
    {
       var kqlist=outparamters.getValue("kqlist");
       AjaxBind.bind(kqclassForm.kqlist,kqlist);
    }
   function SearchLawbasefile()
    {
       var hashvo=new ParameterSet();
       var request=new Request({asynchronous:false,onSuccess:showLawbasefile,functionId:'15211000006'},hashvo);
   }
  function save()
   {
      setselectitem('kqlist');
      var vos= document.getElementById('kqlist');
      var kqlist = new Array();
      for(i=0;i<vos.options.length;i++){
		kqlist[i]=(vos.options[i].value)
      }  
      var hashvo=new ParameterSet();
      hashvo.setValue("kqlist",kqlist);
      var request=new Request({asynchronous:false,onSuccess:reResults,functionId:'15211000007'},hashvo);
   }
   function reResults(outparamters){
   		alert("完成排序");
   		window.close();
   }
</script>
<html:form action="/kq/options/class/kq_class_data">
	<div class="fixedDiv3">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap >
					<bean:message key="lable.lawfile.adjustorder" />
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRow" nowrap>
				<table>
					<tr>
						<td width="46%" align="center">
							<table width="100%">
								<tr>
									<td height="250" valign="top">
										<select name="kqlist" multiple="multiple" style="height:250px;width:100%;font-size:9pt">
										</select>
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_up" onclick="upItem($('kqlist'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down" onclick="downItem($('kqlist'));">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap style="height:35px;border: none">
				<html:button styleClass="mybutton" property="b_saveorder" onclick="save();">
					<bean:message key="button.ok" />
				</html:button>
				<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
	</div>
</html:form>
<script language="javascript">
   SearchLawbasefile();
</script>
