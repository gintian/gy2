<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hrms.hjsj.sys.VersionControl,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hrms.struts.constant.WebConstant" %>
<html>
<head>
<style type="text/css"> 
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 0px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 3px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 3px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 2px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 2px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
#scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<hrms:themes />
<script type="text/javascript">
  function symbol2(cal)
{
    var computeFormula=document.getElementById("totalAppFormula");
    computeFormula.focus();
    var element = document.selection;
    if(element){
        var rge = element.createRange();
        if (rge!=null)
            rge.text=cal;

    }else{
        var start =computeFormula.selectionStart;
        computeFormula.value = computeFormula.value.substring(0, start) + cal + computeFormula.value.substring(start, computeFormula.value.length);
        computeFormula.setSelectionRange(start + cal.length, start + cal.length);
    }

}
  function closewindow()
  {
      if(window.showModalDialog) {
          parent.window.close();
      }else{
          window.open("about:blank","_top").close();
      }
  }
function addrelate(name,obj)
{
	var no = new Option();
	for(i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected)
		{
	    	no.value=obj.options[i].value;
	    	no.text=obj.options[i].text;
		}
	}
	var cal=no.value;
    var computeFormula=document.getElementById(name);
    computeFormula.focus();
    var element = document.selection;
    if(element){
        var rge = element.createRange();
        if (rge!=null)
            rge.text=cal;

    }else{
        var start =computeFormula.selectionStart;
        computeFormula.value = computeFormula.value.substring(0, start) + cal + computeFormula.value.substring(start, computeFormula.value.length);
        computeFormula.setSelectionRange(start + cal.length, start + cal.length);
    }
}
// 函数向导
function functionWizzard()
{
    var thecodeurl ="/org/funwd/function_Wizard.do?b_query=link`callBackFunc=openReturn"//&flag=1&checktemp=jixiaoguanli&planid="+planid; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    //var return_vo= window.showModalDialog(thecodeurl, "", 
    //          "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    
    
    var config = {
     	 width:420,
     	 height:430,
     	 type:'2',
     	 id:"function_Wizard"
    }

    modalDialog.showModalDialogs(iframe_url,"function_Wizard",config,openReturn);
}

function openReturn(return_vo){
	if(return_vo) {
        var formula_sys = document.getElementById("totalAppFormula");
        formula_sys.focus();
        var element = document.selection;
        if (element != null) {
            var rge = element.createRange();
            if (rge != null)
                rge.text = return_vo;
        } else {
            var start = formula_sys.selectionStart;
            formula_sys.value = formula_sys.value.substring(0, start) + return_vo + formula_sys.value.substring(start, formula_sys.value.length);
            formula_sys.setSelectionRange(start + return_vo.length, start + return_vo.length);
        }
    }
}

// 公式检查
function checkFormula()
{	
	var hashvo=new ParameterSet();
//	hashvo.setValue("type",'total_formula');
	hashvo.setValue("templateId",document.getElementById('templateId').value);
	hashvo.setValue("formula",getEncodeStr(document.getElementById('totalAppFormula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula,functionId:'9022000033'},hashvo);
}
function resultCheckFormula(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok" || info.length==0)
		alert("公式通过检查！");
	else if(info=="noHave")
		alert("未定义计算公式！");
	else
		alert(info);
}
function save(){
     var hashvo=new ParameterSet();
	hashvo.setValue("templateId",document.getElementById('templateId').value);
	hashvo.setValue("formula",getEncodeStr(document.getElementById('totalAppFormula').value));
	var request=new Request({method:'post',onSuccess:save_ok,functionId:'9022000033'},hashvo);
    
}
function save_ok(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok" || info=="noHave" || info.length==0){
        parent.window.returnValue = document.getElementById('totalAppFormula').value;
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.top.opener.calformula_window_ok(document.getElementById('totalAppFormula').value);
            window.open("about:blank","_top").close();
        }
	}
	else
		alert(info);
}
</script>
</head>
<body>
<html:form action="/performance/kh_plan/kh_params">
            
			<table width="620" height="300" border="0" align="center" style="padding-right: 27px;">
				<tr>
					<td>
					    <fieldset align="left" style="width:100%;">
						<table width="100%" height="300" border="0">
							<tr>
								<td align="left">
									<table style="width:590px;" border="0" align="center">
										<tr>
											<td valign='top'>
												<table width="100%" border="0">
													<tr>
														<td colspan="2" align="left">
															<html:textarea name="examPlanForm" property="totalAppFormula"
																styleId="totalAppFormula" style="width:100%;height:170px;"></html:textarea>
														</td>
														<html:hidden name="examPlanForm" property="templateId" styleId="templateId"/>
													</tr>													
													<tr>
														<td>
															<fieldset style="width: 339px;height:132px;">
																<legend>
																	<bean:message key="gz.formula.operational.symbol" />
																</legend>
																<table width="100%" border="0">
																	<tr>
																		<td>
																			<table width="100%" border="0">
																				<tr>
																					<td height="22">
																						<input type="button" value=" 0 "
																							onclick="symbol2('0');" class="smallbutton">

																						<input type="button" value=" 1 "
																							onclick="symbol2('1');" class="smallbutton">

																						<input type="button" value=" 2 "
																							onclick="symbol2('2');" class="smallbutton">

																						<input type="button" value=" 3 "
																							onclick="symbol2('3');" class="smallbutton">

																						<input type="button" value=" 4 "
																							onclick="symbol2('4');" class="smallbutton">

																						<input type="button" value=" ( "
																							onclick="symbol2('(');" class="smallbutton">
																							
																						<input type="button" value="如果"
																							onclick="symbol2('如果');" class="smallbutton">

																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value=" 5 "
																							onclick="symbol2('5');" class="smallbutton">

																						<input type="button" value=" 6 "
																							onclick="symbol2('6');" class="smallbutton">

																						<input type="button" value=" 7 "
																							onclick="symbol2('7');" class="smallbutton">

																						<input type="button" value=" 8 "
																							onclick="symbol2('8');" class="smallbutton">

																						<input type="button" value=" 9 "
																							onclick="symbol2('9');" class="smallbutton">
																						
																						<input type="button" value=" ) "
																							onclick="symbol2(')');" class="smallbutton">
																						
																						<input type="button" value="那么"
																							onclick="symbol2('那么');" class="smallbutton">
																					</td>
																				</tr>
																				<tr>
																					<td height="22">
																						<input type="button" value=" + "
																							onclick="symbol2('+');" class="smallbutton">

																						<input type="button" value=" - "
																							onclick="symbol2('-');" class="smallbutton">

																						<input type="button" value=" * "
																							onclick="symbol2('*');" class="smallbutton">

																						<input type="button" value=" / "
																							onclick="symbol2('/');" class="smallbutton">
																						
																						<input type="button" value=" = "
																							onclick="symbol2('=');" class="smallbutton">
																						
																						<input type="button" value=" . "
																							onclick="symbol2('.');" class="smallbutton">
																						
																						<input type="button" value="否则"
																							onclick="symbol2('否则');" class="smallbutton">	
																					</td>
																				</tr>
																				<tr>
																					<td height="22">																						
																						<input type="button" value=' > '
																							onclick="symbol2('>');" class="smallbutton">

																						<input type="button" value=' < '
																						 		onclick="symbol2('<');" class="smallbutton">
																						
																						<input type="button" value="< >"
																							onclick="symbol2('<>');" class="smallbutton">																						
																					
																						<input type="button" value="且"
																							onclick="symbol2('且');" class="smallbutton">

																						<input type="button" value="或"
																							onclick="symbol2('或');" class="smallbutton">

																						<input type="button" value="结束"
																							onclick="symbol2('结束');" class="smallbutton">

																						<input type="button" value="分情况"
																							onclick="symbol2('分情况');" class="smallbutton">
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
														<td align="left">
															<fieldset style="width: 238px;height:132px;">
																<legend>
																	<bean:message key='org.maip.reference.projects' />
																</legend>
																<table width="100%" border="0" height="110">
																	<tr height="10">
																		<td valign="top">
																			<table width="100%" border="0">
																				
																				<tr>
																					<td align="left" nowrap>
																						指标
																					</td>
																					<td>																																										
																						<html:select name="examPlanForm"
																							property="tem_point_id" size="1" style="width:168px"
																							onchange="addrelate('totalAppFormula',this);" >																							
																							<html:optionsCollection property="pointList" value="dataValue" label="dataName" />																						
																						</html:select>
																					</td>
																				</tr>
																			</table>
																		</td>
																	</tr>
																</table>
															</fieldset>
														</td>
													</tr>
													<tr>
														<td colspan="2" align="right">
															<input type='button' value='向导' class="mybutton"
																onclick="functionWizzard();" />
														
															<input type="button" name="formulaCheck"
																value="<bean:message key="performance.workdiary.check.formula"/>"
																class="mybutton" onclick="checkFormula();" style="position:relative;right: -5px;"/>																																																									
														</td>
													</tr>													
												</table>
											</td>
										</tr>
									</table>
								</td>								
							</tr>
						</table>
						</fieldset>
					</td>
				</tr>
			</table>
			<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
  		<tr style="height:35">			
			<td align="center" >
				<input type="button" name="sva" id="sva" class="mybutton" value="<bean:message key="button.ok"/>" onclick="save();"/>
				<input type="button" name="clo"  class="mybutton" value="<bean:message key="button.cancel"/>" onclick="closewindow();"/>
			</td>
		</tr>
	</table>
	
</html:form>
</body>
</html>
<script type="text/javascript">
var status = '${examPlanForm.status}';
var arguments=parent.dialogArguments;
if(!window.showModalDialog) {
    arguments=window.top.opener.totalAppFormula_arguments;
}
if(status!='0' && status!='5' && document.getElementById('sva')!=null){
   document.getElementById('sva').disabled=true;
}
if(arguments==undefined){
    arguments="";
}
document.getElementById('totalAppFormula').value = arguments;
if( !isCompatibleIE()){
    var inps = document.getElementsByTagName("input");
    for(var i=0;inps && i<inps.length;i++){
        if(inps[i].type=='button' && inps[i].className=='smallbutton'){
            inps[i].style.padding='0 8px 0 8px';
        }
    }
}
</script>