<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
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
    height: 300px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<hrms:themes />
<script language="javascript">
var chkid="-1";
function changeCodeValue(){
  	var item=document.getElementById("itemid").value;
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
  	//新建临时变量功能暂时不提供
  	//if(item=='newcreate'){
  		//settemp();
  		//return;
  //	}
  	var itemid = item.split(":");
    symbol('formula',itemid[1]);
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function settemp(){
	var salaryid=document.getElementById("salaryid").value;
	var thecodeurl = "/gz/gz_accounting/iframvartemp.jsp?state="+salaryid;
   	var return_vo= window.showModalDialog(thecodeurl,"window2",
   						"dialogWidth:750px;dialogHeight:530px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var hashvo=new ParameterSet();
    	hashvo.setValue("salaryid",salaryid);
    	hashvo.setValue("itemid",return_vo);
    	var request=new Request({method:'post',asynchronous:false,
     		onSuccess:setItemList,functionId:'3020060021'},hashvo);
    }
}
function setItemList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	var itemid = outparamters.getValue("itemid");
	if(itemlist.length>0){
		AjaxBind.bind(formulaForm.itemid,itemlist);
		document.getElementById("itemid").value=itemid;
		var arr = itemid.split(":");
		if(arr.length==2){
			symbol('formula',arr[1]);
		}
	}
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		AjaxBind.bind(gztemplateSetForm.codesetid_arr,codelist);
		document.getElementById("codeview").style.display="block";
	}
	else
	{
	   document.getElementById("codeview").style.display="none";
	}
} 
function getCodesid(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codesetid_arr");
	var codesetid_arr_vo = codesetid_arr[0];
	if(codesetid_arr==null){
		return;
	}else{
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
				continue;
			}
		}
		if(codeid==null||codeid==undefined||codeid.length<1){
  			return;
  		}
		symbol('formula',"\""+codeid+"\"");
	}
} 
function addFormula(optType){
	var salaryid="${gztemplateSetForm.salaryid}";
	var gz_module="${gztemplateSetForm.gz_module}";
    var thecodeurl ="/gz/templateset/spformula/sp_formula.do?b_edit=edit`isClose=1`salaryid="+salaryid+"`optType="+optType+"`gz_module="+gz_module; 
    if(optType=='2')
    {
         if(chkid=='-1')
         {
            alert(PLEASE_SELECT_EDITFORMULA+"！");
            return;
         }
         thecodeurl+="`chkid="+chkid;
    }
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo!=null){
	      var obj=new Object();
	      obj.refresh=return_vo.refresh;
	      if(obj.refresh=='2')
	      {
    	    var thecodeurl ="/gz/templateset/spformula/sp_formula.do?b_query=link&opt=1&salaryid="+salaryid+"&returnType=<%=request.getParameter("returnType")%>"+"&gz_module="+gz_module; 
    	    window.location.href=thecodeurl;
    	  }
    }
}
function savemula(){
	var formula=document.getElementById("shry").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("c_expr",getEncodeStr(formula));
	hashvo.setValue("salaryid","${gztemplateSetForm.salaryid}");
	hashvo.setValue("gz_module","${gztemplateSetForm.gz_module}");
	hashvo.setValue("itemid","");
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		alertUseFlag(chkid,'4');
	}else{
		if(info.length<4){
			var formula=document.getElementById("formula").value;
			alert(formula+" "+SYNTAX_ERROR+"!");
		}else{
			alert(getDecodeStr(info));
		}
	}
}
function returnup(){
   	document.location.href="/gz/templateset/gz_templatelist.do?b_query=link2";
}
function delProject(){
   if(chkid=="-1")
   {
      alert(CONFIRM_DELETE_ITEM+"！");
      return;
   }
	if(ifdel())
	{
	   alertUseFlag(chkid,'2');
	}
	else
	{
	  return;
	}
}
function alertUseFlag(chid,opt){
	
    var hashvo=new ParameterSet();
    hashvo.setValue("opt",opt);
    hashvo.setValue("chkid",chid);
    hashvo.setValue("salaryid","${gztemplateSetForm.salaryid}");
    if(opt=='1')
    {
       var obj=document.getElementById(chid);
       if(obj.checked)
       {
           hashvo.setValue("validflag","1");
       }
       else
       {
          hashvo.setValue("validflag","0");
       }
    }
    if(opt=='4')
    {
        var formula=document.getElementById("shry").value;
		hashvo.setValue("formula",getEncodeStr(formula));
    }
    var request=new Request({method:'post',asynchronous:false,onSuccess:config_ok,functionId:'3020070012'},hashvo); 
}
function config_ok(outparameters)
{
   var opt=outparameters.getValue("opt");
   if(opt=='2')
   {
      var salaryid=outparameters.getValue("salaryid");
      var gz_module="${gztemplateSetForm.gz_module}";
      var thecodeurl ="/gz/templateset/spformula/sp_formula.do?b_query=link&opt=0&salaryid="+salaryid+"&returnType=<%=request.getParameter("returnType")%>"+"&gz_module="+gz_module; 
      window.location.href=thecodeurl;
   }
   if(opt=='3')
   {
       var formula=outparameters.getValue("formula");
       document.getElementById("shry").value=getDecodeStr(formula);
   }
   if(opt=='4')
   {
      alert(SAVE_FORMULA_OK+"!");
   }
}
function tr_backgroundcorlor(chid)
{
    chkid=chid;
    var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	var cvalue = tablevos[i];
	    	var td = cvalue.parentNode.parentNode;
	    	td.style.backgroundColor = '';
		}
    }
    if(chid!='-1')
    {
    	var c = document.getElementById(chid);
    	if(c)
    	{
        	var tr = c.parentNode.parentNode;
        	tr.style.backgroundColor = '#FFF8D2' ;
        }
    }
    alertUseFlag(chkid,'3');
}
function returnClose()
{
  var obj=new Object();
  obj.refresh='1';
  returnValue=obj;
  window.close();
}
</script>
<html:form action="/gz/templateset/spformula/sp_formula">
<%if("hl".equals(hcmflag)){ %>
<br>
<table width="785" height="350" border="0" align="center">
<%}else{ %>
<table width="785" height="350" border="0" align="center" style="margin-top:-5px;margin-left:-2px;">
<%} %>
<tr>
<td>  
<html:hidden name="gztemplateSetForm" property="salaryid"/>
<html:hidden name="gztemplateSetForm" property="gz_module"/>
<fieldset align="center" style="width:100%;">
<legend><bean:message key="label.gz.shformula"/></legend>
<table width="100%" height="350" border="0" align="center">
  <tr> 
    <td width="40%" height="350" align="center">
    <fieldset align="center" style="width:100%;">
	<legend><bean:message key="gz.formula.list.table"/></legend> 
      <table width="100%" height="345" border="0">
        <tr > 
          <td height="280" align="center" valign="bottom"> 
          <div id="scroll_box" style="height: 96%">
            <table width="100%" border="0" cellspacing="0">
              <tr class="fixedHeaderTr"> 
                <td width="15%" class="TableRow" style="border-top:none;border-left:none;border-right:none;" align="center" nowrap><bean:message key="parttime.param.flag"/></td>
                <td width="65%" class="TableRow" style="border-top:none;border-right:none;" align="center"><bean:message key="kq.shift.relief.name"/></td>
                </tr>
              <logic:iterate id="element" name="gztemplateSetForm" property="spFormulaList" indexId="index" offset="0">	
			   <tr> 
                <td width="15%" class="RecordRow" style="border-top:none;border-left:none;border-right:none;" align="center" onclick="tr_backgroundcorlor('<bean:write name="element" property="chkid"/>')" nowrap>
                	<logic:equal name="element" property="validflag" value="1">
                		<input type="checkbox" id="<bean:write name="element" property="chkid"/>" name="<bean:write name="element" property="chkid"/>" value="1" onclick="alertUseFlag('<bean:write name="element" property="chkid"/>','1');" checked/>
                	</logic:equal>
                	<logic:notEqual name="element" property="validflag" value="1">
                		<input type="checkbox" id="<bean:write name="element" property="chkid"/>" name="<bean:write name="element" property="chkid"/>" value="0" onclick="alertUseFlag('<bean:write name="element" property="chkid"/>','1');"/>
                	</logic:notEqual>
                </td>
                <td width="65%" class="RecordRow" style="border-top:none;border-right:none;" onclick="tr_backgroundcorlor('<bean:write name="element" property="chkid"/>')" nowrap>&nbsp;<a href="javascript:addFormula('2');"><bean:write name="element" property="name"/></a>&nbsp;</td>
              </tr>
			 </logic:iterate>
            </table>
            </div>
          </td>
        </tr>
        <tr valign="top">
          <td height="28" align="center" valign="top"> 
            <input  type="button"  value="<bean:message key='button.new.add'/>" onclick="addFormula('1');" Class="mybutton"> &nbsp;&nbsp; 
            <input  type="button"  value="<bean:message key='button.edit'/>" onclick="addFormula('2');" Class="mybutton" style="margin-left: -15px"> &nbsp;&nbsp; 
            <input type="button"  value="<bean:message key='button.delete'/>" onclick="delProject();" Class="mybutton" style="margin-left: -15px">&nbsp;&nbsp;
          </td>
        </tr>
      </table> 
      </fieldset>
    </td>
    <td width="60%" align="center">
    <span id="expression">
    <table border="0" align="center">
    <tr><td>
    <fieldset align="center" style="width:100%;">
	<legend><bean:message key="kq.wizard.expre"/></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="gztemplateSetForm" property="formula"  cols="75" rows="9" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right">
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard('${gztemplateSetForm.salaryid}','formula');" Class="mybutton">  
            		<input type="button" value="<bean:message key='org.maip.formula.preservation'/>" onclick="savemula();" Class="mybutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		<td width="60%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height=125">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		<table width="100%" border="0" height="100">
            			<tr height="30">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30" align="right"><bean:message key="gz.formula.project"/>
											<hrms:optioncollection name="gztemplateSetForm" property="salaryItemList" collection="list"/>
											<html:select name="gztemplateSetForm" property="itemid" onchange="changeCodeValue();" style="width:190">
			 									<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
                 						</td>
              						</tr>
            					</table>
            				</td>
            			</tr>
            			<tr height="30">
            				<td>
            					<span id="codeview" style="display:none">
            					<table width="100%" border="0" >
              						<tr> 
                						<td height="30" align="right"><bean:message key="conlumn.codeitemid.caption"/>
											<select name="codesetid_arr" onchange="getCodesid();"  style="width:190">
             								</select>
                 						</td>
              						</tr>
            					</table>
            					</span>
            				</td>
            			</tr>
            		</table>
            		</fieldset>
          		</td>
          		<td width="40%">
          		<fieldset align="center" style="width:100%;">
				 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
					<table width="80%" border="0">
              			<tr> 
              				<td>
              				<table width="100%" border="0">
              				<tr>
                				<td><input type="button"  value="0" onclick="symbol('formula',0);" class="btn2 common_btn_bg"></td>
                				<td><input type="button"  value="1" onclick="symbol('formula',1);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="2" onclick="symbol('formula',2);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="3" onclick="symbol('formula',3);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="4" onclick="symbol('formula',4);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="(" onclick="symbol('formula','(');" class="btn2 common_btn_bg"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.if'/>" onclick="symbol('formula','<bean:message key='gz.formula.if'/>');" class="btn3 common_btn_bg"></td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="5" onclick="symbol('formula',5);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="6" onclick="symbol('formula',6);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="7" onclick="symbol('formula',7);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="8" onclick="symbol('formula',8);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="9" onclick="symbol('formula',9);" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value=")" onclick="symbol('formula',')');" class="btn2 common_btn_bg"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.else'/>" onclick="symbol('formula','<bean:message key='gz.formula.else'/>');" class="btn3 common_btn_bg"></td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="+" onclick="symbol('formula','+');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="-" onclick="symbol('formula','-');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="*" onclick="symbol('formula','*');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="/" onclick="symbol('formula','/');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="\" onclick="symbol('formula','\\');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="%" onclick="symbol('formula','%');" class="btn2 common_btn_bg"> </td>
               			 		<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('formula','<bean:message key='general.mess.and'/>');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
              				<tr> 
               		 			<td><input type="button"  value="=" onclick="symbol('formula','=');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;" onclick="symbol('formula','&gt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;" onclick="symbol('formula','&lt;');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;&gt;" onclick="symbol('formula','&lt;&gt;');" class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&lt;=" onclick="symbol('formula','&lt;=');"class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="&gt;=" onclick="symbol('formula','&gt;=');"class="btn1 common_btn_bg"> </td>
                				<td><input type="button"  value="~" onclick="symbol('formula','~');" class="btn2 common_btn_bg"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');" class="btn1 common_btn_bg"> </td>
              				</tr>
            			</table>
            			</td>
            		</tr>
            		</table>
            		</fieldset>
          		</td>
        	</tr>
      </table>
      </fieldset>
      </td></tr>
      </table>
      </span>
    </td>
  </tr>
</table>
</fieldset>
</td>
</tr>
</table>

<center>
<%if(request.getParameter("returnType").equals("0") && (request.getParameter("gz_module")==null || (request.getParameter("gz_module")!=null && !request.getParameter("gz_module").equals("3")))){ %>
<input type="button"  value="<bean:message key='button.return'/>" onclick="returnup();" Class="mybutton">
<%}else if(request.getParameter("returnType").equals("1")){ %>
<input type="button"  value="<bean:message key='button.ok'/>" onclick="returnClose();" Class="mybutton">
<%} %>
</center>
<script language="javascript">
tr_backgroundcorlor("${gztemplateSetForm.spFormulaId}");
</script>
</html:form>
