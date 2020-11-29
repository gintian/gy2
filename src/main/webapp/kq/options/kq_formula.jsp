<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<style type="text/css"> 
.btn
{
	height: 25px;
	width: 24px;
}

#scroll_box {
    border: 1px solid #eee;
    height: 280px;    
    width: 270px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>

<script type="text/javascript">
var temp =new Array();
var chkid="-1";

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

function changeCodeValue(){
  	var item=document.getElementById("itemid").value;
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
  	var itemid = item.split(":");
    symbol('formula',itemid[1]);
	var in_paramters="itemid="+itemid[0];
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'15204110037'});
}

function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		AjaxBind.bind(kqFormulaForm.codesetid_arr,codelist);
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
function alertUseFlag(chid,opt){
	
    var hashvo=new ParameterSet();
    hashvo.setValue("opt",opt);
    hashvo.setValue("chkid",chid);
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
      var thecodeurl ="/kq/options/kq_check_formula.do?b_query=link"; 
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

function savemula(){
	var formula=document.getElementById("shry").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("formula",getEncodeStr(formula)); 
	var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'15204110036'},hashvo);	
}
function resultCheckExpr(outparamters){
	var flag = outparamters.getValue("flag");
	flag=getDecodeStr(flag);
	if(flag=='1'){
		alertUseFlag(chkid,'4');
	}else{
		alert(flag);
	}
}

function addformula(optType){
	 var thecodeurl ="/kq/options/kq_check_formula.do?b_edit=link`isClose=1`optType="+optType; 
	    if(optType=='2')
	    {
	         if(chkid=='-1')
	         {
	            alert(PLEASE_SELECT_EDITFORMULA+"！");
	            return;
	         }
	         thecodeurl+="`chkid="+chkid;
	    }
	    
	    if($URL)
	    	thecodeurl = $URL.encode(thecodeurl);
	    
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	    var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo!=null){
		      var obj=new Object();
		      obj.refresh=return_vo.refresh;
		      if(obj.refresh=='2')
		      {
	    	    var thecodeurl ="/kq/options/kq_check_formula.do?b_query=link";
	    	    window.location.href=thecodeurl;
	    	  }
	    }
}

function symbol(editor,strexpr){
	document.getElementById(editor).focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
}

function returnClose()
{
  var obj=new Object();
  obj.refresh='1';
  returnValue=obj;
  window.close();
}
</script>


<html:form action="/kq/options/kq_check_formula.do?b_query=link">
<div class="" style="height: 100%;border: none">
<table width="100%" height="350" border="0" align="center">
<tr>
<td>  
<fieldset align="center" style="width:100%;">
<legend><bean:message key="label.gz.shformula"/></legend>
<table width="100%" height="350" border="0" align="center">
  <tr> 
    <td width="40%" height="350" align="center" style="border:none">
    <fieldset align="center" style="width:100%;height:410px;">
	<legend><bean:message key="gz.formula.list.table"/></legend> 
      <table width="100%" height="345" border="0">
        <tr > 
          <td height="280" align="center" valign="top"> 
          <div id="scroll_box">
            <table width="100%" border="0" class="ListTable1 common_border_color" style="border-collapse: collapse;border: none;">
              <tr> 
                <td width="15%" class="TableRow" style="border-top: none;border-left: none" align="center" nowrap><bean:message key="parttime.param.flag"/></td>
                <td width="65%" class="TableRow" style="border-top: none;border-right: none" align="center"><bean:message key="kq.shift.relief.name"/></td>
                </tr>
              <logic:iterate id="element" name="kqFormulaForm" property="kqFormulaList" indexId="index" offset="0">	
			   <tr> 
                <td width="15%" class="RecordRow" align="center"  style="border-left: none;" onclick="tr_backgroundcorlor('<bean:write name="element" property="chkid"/>')" nowrap>
                	<logic:equal name="element" property="validflag" value="1">
                		<input type="checkbox" id="<bean:write name="element" property="chkid"/>" name="<bean:write name="element" property="chkid"/>" value="1" onclick="alertUseFlag('<bean:write name="element" property="chkid"/>','1');" checked/>
                	</logic:equal>
                	<logic:notEqual name="element" property="validflag" value="1">
                		<input type="checkbox" id="<bean:write name="element" property="chkid"/>" name="<bean:write name="element" property="chkid"/>" value="0" onclick="alertUseFlag('<bean:write name="element" property="chkid"/>','1');"/>
                	</logic:notEqual>
                </td>
                <td width="65%" class="RecordRow" style="border-right: none;" onclick="tr_backgroundcorlor('<bean:write name="element" property="chkid"/>')" nowrap>&nbsp;<a href="javascript:addformula('2');"><bean:write name="element" property="name"/></a>&nbsp;</td>
              </tr>
			 </logic:iterate>
            </table>
            </div>
          </td>
        </tr>
        <tr>
          <td height="28" align="center"> 
            <input  type="button"  value="<bean:message key='button.new.add'/>" onclick="addformula('1')" Class="mybutton">
            <input  type="button"  value="<bean:message key='button.edit'/>" onclick="addformula('2');" Class="mybutton">
            <input type="button"  value="<bean:message key='button.delete'/>" onclick="delProject();" Class="mybutton">
          </td>
        </tr>
      </table> 
      </fieldset>
    </td>
    <td width="60%" align="center">
    <span id="expression">
    <table border="0" align="center">
    <tr><td>
    <fieldset align="center" style="width:100%;height:410px;">
	<legend><bean:message key="kq.wizard.expre"/></legend> 
		<table width="100%" border="0">
        	<tr> 
          		<td colspan="2" align="center"> 
            		<html:textarea name="kqFormulaForm" property="formula"  
            		  cols="75" rows="10" styleId="shry"></html:textarea> 
            	</td>
        	</tr>
        	<tr> 
          		<td height="21" colspan="2" align="right" style="padding-right: 0px;">
          			<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard('formula','kqrule');" Class="mybutton">  
            		<input type="button" value="<bean:message key='org.maip.formula.preservation'/>" onclick="savemula();" Class="mybutton">&nbsp; 
            	</td>
        	</tr>
        	<tr> 
          		<td width="52%" align="center"> 
          		 <fieldset  align="center" style="width:96%;height:140px;">
				 <legend><bean:message key='org.maip.reference.projects'/></legend> 
            		     <table width="100%" border="0">
            			<tr height="30px;">
            				<td>
            					<table width="100%"  border="0" >
              						<tr> 
                						<td height="30"><bean:message key="gz.formula.project"/>
											<hrms:optioncollection name="kqFormulaForm" property="fieldlist" collection="list"/>
											<html:select name="kqFormulaForm" property="itemid" onchange="changeCodeValue();" style="width:140">
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
                						<td height="30">
                						   <bean:message key="conlumn.codeitemid.caption"/>
											<select name="codesetid_arr" onchange="getCodesid();"  style="width:140;font-size:9pt">
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
          		<td width="48%" style="padding-right: 7px;">
          		<fieldset align="center" style="width:100%;">
				 <legend><bean:message key="gz.formula.operational.symbol"/></legend> 
					<table width="80%" border="0" align="center">
              			<tr> 
              				<td align="center">
              				<table width="100%" border="0">
              				<tr>
                				<td><input type="button"  value="0" onclick="symbol('formula',0);" class="smallbutton btn"></td>
                				<td><input type="button"  value="1" onclick="symbol('formula',1);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="2" onclick="symbol('formula',2);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="3" onclick="symbol('formula',3);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="4" onclick="symbol('formula',4);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="(" onclick="symbol('formula','(');" class="smallbutton btn"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.if'/>" onclick="symbol('formula','<bean:message key='gz.formula.if'/>');" class="smallbutton btn"  style="height: 25px;width: 52px;"></td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="5" onclick="symbol('formula',5);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="6" onclick="symbol('formula',6);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="7" onclick="symbol('formula',7);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="8" onclick="symbol('formula',8);" class="smallbutton btn"> </td>
                				<td><input type="button"  value="9" onclick="symbol('formula',9);" class="smallbutton btn"> </td>
                				<td><input type="button"  value=")" onclick="symbol('formula',')');" class="smallbutton btn"> </td>
                				<td colspan="2"><input type="button"  value="<bean:message key='gz.formula.else'/>" onclick="symbol('formula','<bean:message key='gz.formula.else'/>');" class="smallbutton btn"  style="height: 25px;width: 52px;"></td>
              				</tr>
              				<tr> 
                				<td><input type="button"  value="+" onclick="symbol('formula','+');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="-" onclick="symbol('formula','-');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="*" onclick="symbol('formula','*');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="/" onclick="symbol('formula','/');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="\" onclick="symbol('formula','\\');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="%" onclick="symbol('formula','%');" class="smallbutton btn"> </td>
               			 		<td><input type="button"  value="<bean:message key='general.mess.and'/>" onclick="symbol('formula','<bean:message key='general.mess.and'/>');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="<bean:message key='general.mess.or'/>" onclick="symbol('formula','<bean:message key='general.mess.or'/>');" class="smallbutton btn"> </td>
              				</tr>
              				<tr> 
               		 			<td><input type="button"  value="=" onclick="symbol('formula','=');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="&gt;" onclick="symbol('formula','&gt;');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="&lt;" onclick="symbol('formula','&lt;');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="&lt;&gt;" onclick="symbol('formula','&lt;&gt;');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="&lt;=" onclick="symbol('formula','&lt;=');"class="smallbutton btn"> </td>
                				<td><input type="button"  value="&gt;=" onclick="symbol('formula','&gt;=');"class="smallbutton btn"> </td>
                				<td><input type="button"  value="~" onclick="symbol('formula','~');" class="smallbutton btn"> </td>
                				<td><input type="button"  value="<bean:message key='kq.wizard.not'/>" onclick="symbol('formula','<bean:message key='kq.wizard.not'/>');" class="smallbutton btn"> </td>
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
<input type="button"  value="<bean:message key='button.ok'/>" onclick="returnClose();" Class="mybutton" style='margin-top:5px;'>
</center>
</div>
</html:form>
<script language="javascript">
   tr_backgroundcorlor('${kqFormulaForm.kqFormulaId}')
</script>