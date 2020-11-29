<%@ page contentType="text/html; charset=utf-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style>
<!--
.divTable {
	border: 1px solid #C4D8EE;
}

.TableRow {
	background-position: center left;
	font-size: 12px;
	BORDER-BOTTOM: #C4D8EE 1pt solid;
	BORDER-LEFT: #C4D8EE 0pt solid;
	BORDER-RIGHT: #C4D8EE 0pt solid;
	BORDER-TOP: #C4D8EE 0pt solid;
	height: 22px;
	font-weight: bold;
	valign: middle;
}
-->
</style>
<script type="text/javascript">
	function save() {
		var flag = 0;
		var msg = 0;
		if(document.getElementById("id").checked){
			if(document.getElementById("daan1").checked)
				flag = 1;
			if(document.getElementById("daan2").checked)
				flag = 2;
		}
		if(document.getElementById("selection").checked)
			msg = 1;
		var url=window.location.href;
		var urls=url.split("?");
		var cs= urls[1].split("&");;
		var r5300=cs[1].split("=");
		var imgurl=cs[2].split("=");
		
		var hashvo=new ParameterSet();
	    hashvo.setValue("r5300",r5300[1]);
	    hashvo.setValue("imgurl",imgurl[1]);
	    hashvo.setValue("flag",flag);
	    hashvo.setValue("msg",msg);
	    var request=new Request({method:'post',onSuccess:showWord,functionId:'202007000801'},hashvo);
	}
	function showWord(outparamters) {
		var outName=outparamters.getValue("outName");
		window.returnValue = outName;
		window.close();
	}
	function showselect(){
		if(document.getElementById("id").checked)
			document.getElementById("div").style.display="";
		else
			document.getElementById("div").style.display="none";
		
	}
</script>
<html:form action="/train/trainexam/paper/preview/paperspreview">
	<table width="100%" align="center" cellpadding="0" cellspacing="0"
		border="0">
		<tr>
			<td>
				<div class="fixedDiv3 complex_border_color" style="height: 156px;">
					<table width="100%" align="center" cellpadding="0" cellspacing="0"
						border="0">
						<tr>
							<td class="TableRow common_border_color" nowrap="nowrap" align="left">
								&nbsp;&nbsp;导出选项
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td>
								<table width="96%" align="center" cellpadding="0"
									cellspacing="0" border="0">
									<tr>
										<td>
											<table width="96%" align="center" cellpadding="0"
												cellspacing="0" border="0">
												<tr>
													<td align="left" >
														<input type="checkbox" name="selection" id="selection" value="1" checked="checked" />
														选择题每个选项单独占一行
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
									<tr>
										<td align="left">
											&nbsp;<input type="checkbox" id="id" name="check" onclick="javascript: showselect();"/>
											导出答案
										</td>
									</tr>
									<tr>
										<td align="left">
											<div id="div" style="display: none;">
												<table>
													<tr>
														<td align="center" width="35px">
															<input type="radio" name="daan" id="daan1" value="1"
																checked="checked" />
														</td>
														<td align="left">
															答案集中导出
														</td>
														<td align="center" width="35px">
															<input type="radio" name="daan" id="daan2" value="2" />
														</td>
														<td align="left">
															答案在每题后面
														</td>
													</tr>
												</table>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<table width="96%" align="center" cellpadding="0" cellspacing="0"
					border="0" style="margin-top: 5px;">
					<tr>
						<td align="center">
							<input type="button" name="b_modify" value='确定'
								onclick="save()" class="mybutton" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
