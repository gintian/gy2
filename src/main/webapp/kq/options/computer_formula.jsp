<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
 
  var temp =new Array();
  
  function showSetList(outparamters)
  {
	var setlist=outparamters.getValue("setlist");
	AjaxBind.bind(kqItemForm.setlist,setlist);
	var ss=outparamters.getValue("fieldlist");	
        if(ss!=null)
	{
          var i=0;
          for(i=0;i<ss.length;i++)
          {
             temp[i] = new Array(ss[i].label,ss[i].name);
          }
	}
  }
  function MusterInitData(infor)
  {
    var request=new Request({method:'post',asynchronous:false,onSuccess:showSetList,functionId:'15204110012'});
  }
  function insertTxt(strtxt)
  {
    if(strtxt==null)
    return ;
    var str = new Array();
	if(strtxt.indexOf(":") != -1){
	    str = strtxt.split(":");
	  	strtxt = str[1];
	}
    if((strtxt.toString()).indexOf("(")!=-1 && !"(" == (strtxt.toString()))
    	strtxt="["+strtxt+"]";
    if(strtxt=="<bean:message key="kq.formula.even"/>"||strtxt=="<bean:message key="kq.formula.if"/>"||strtxt=="<bean:message key="kq.formula.or"/>"||strtxt=="<bean:message key="kq.formula.fou"/>"||strtxt=="<bean:message key="kq.formula.end"/>"||strtxt=="<bean:message key="kq.formula.then"/>"||strtxt=="<bean:message key="kq.formula.not"/>")
     {
        var ddd=" "+strtxt+" ";
        var expr_editor=$('c_expr');
	        expr_editor.focus();
		  var element = document.selection;
		  if (element!=null) 
		  {
		  	var rge = element.createRange();
		   	if (rge!=null)	
		  	     rge.text=ddd;
		   }
     }else{
     
	  	var expr_editor=$('c_expr');
	        expr_editor.focus();
		  var element = document.selection;
		  if (element!=null) 
		  {
		  	var rge = element.createRange();
		   	if (rge!=null)	
		  	     rge.text=strtxt;
		  }
		 }
	 //document.getElementById("setlist").value = "";
  }
	
  function  fnOpen()  
  {  
      var  wName; 
      var thecodeurl ="/kq/options/functionwizard.do?b_query=link&salaryid=&tableid=&salarytemp="; 
      wName= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
      //wName=window.showModalDialog("formula_wizard.jsp",temp,'dialogLeft:455;dialogTop:200;dialogHeight:450px;dialogWidth:380px');   
      insertTxts(wName);
  }  
  
  function insertTxts(strtxt)
  {
    if(strtxt==null)
      return ;
   var ggg;
   if(strtxt=="<bean:message key="kq.wizard.ifa"/>")
   {
      var dgf=strtxt.replace(" ","\n");
      var dgg=dgf.replace(" ","\n");
      var ddd=dgg.replace(" ","\n");
      ggg=ddd.replace(" ","\n");
   }else if(strtxt=="<bean:message key="kq.wizard.thing"/>")
   {
       ggg="如果 Lexp1 那么 exp1"+"\n"+"如果 Lexp2 那么 exp2"+"\n"+"否则 expn..."+"\n"+"结束";
   }else{
    
      ggg=strtxt;  
   }
     var expr_editor=$('c_expr');
	        expr_editor.focus();
		  var element = document.selection;
		  if (element!=null) 
		  {
		  	var rge = element.createRange();
		   	if (rge!=null)	
		  	     rge.text=ggg;
		  }
  }
  
	function checkFormula(){
		var hashvo = new ParameterSet();
		hashvo.setValue("c_expr",getEncodeStr($F('c_expr')));
		var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckResult,functionId:'15204110021'},hashvo);
	}
	function showCheckResult(outparamters){
		var sige = outparamters.getValue("sige");
		var sigh = getDecodeStr(outparamters.getValue("sigh"));
		if("1" == sige)
		{
			document.getElementById("mess1").style.display = "block";
			document.getElementById("mess1").innerHTML = sigh;
			document.getElementById("mess2").style.display = "none";
		}
		if("2" == sige)
		{
			document.getElementById("mess2").style.display = "block";
			document.getElementById("mess1").style.display = "none";
		}
	}

	function changeCodeValue(){
	    var item=document.getElementsByName("setlist")[0].value;
	    if(item==null||item==undefined||item.length<1){
	        return;
	    }
	    var itemid = item.split(":");
	    symbol('c_expr',itemid[1]);
	    var in_paramters="itemid="+itemid[0];
	    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'15204110037'});
	}

	function showCodeFieldList(outparamters){
	    var codelist=outparamters.getValue("codelist");
	    if(codelist!=null&&codelist.length>1){
	        AjaxBind.bind(kqItemForm.codesetid_arr,codelist);
	        document.getElementById("codeview").style.display="block";
	    }
	    else
	    {
	       document.getElementById("codeview").style.display="none";
	    }
	} 

	function symbol(editor,strexpr){
	    document.getElementById(editor).focus();
	    var element = document.selection;
	    if (element!=null) {
	        var rge = element.createRange();
	        if (rge!=null)
	        {
		        if(strexpr.indexOf(")")>0 && strexpr.indexOf("(")>0)
		        strexpr='['+strexpr+']';     
	        rge.text=strexpr;
	        }
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
	        symbol('c_expr',"\""+codeid+"\"");
	    }
	} 
</script>
<html:form action="/kq/options/computer_formula">
	<table border="0px" cellpadding="0px" cellspacing="0px" width="700"
		align="center">
		<tr>
			<td>
				<br />
			</td>
		</tr>
		<tr>
			<td>
				<fieldset height="100%">
					<legend>
						<logic:equal name="kqItemForm" property="expr_flag" value="day">
							<bean:message key="kq.item.day.count" />
						</logic:equal>
						<logic:equal name="kqItemForm" property="expr_flag" value="mo">
							<bean:message key="kq.item.mo.count" />
						</logic:equal>
					</legend>
					<table border="0" cellspacing="1" width="100%" height="420px;" align="left"
						cellpadding="0">
						<tr>
							<td width="100%" colspan="2" align="center">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<font color="red" id="mess2" style="display:none"> 
									<bean:message key="kq.formula.tcheck"/>
								</font>
								<font color="red" id="mess1" style="display:none">
								</font>
								<logic:notEqual name="kqItemForm" property="sige" value="2">
								</logic:notEqual>
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center">
								<html:textarea name="kqItemForm" property="c_expr" style="height:250px;width:98%;font-size:9pt" />
							</td>
						</tr>
						<tr>
							<td align="right" valign="middle" colspan="2">
								<html:hidden name="kqItemForm" property="expr_flag" />
								<input type="button" name="Submit"
									value="<bean:message key="kq.formula.function"/>"
									class="mybutton" onClick="fnOpen();">
								<input type="button" name="check" 
									value="<bean:message key="performance.workdiary.check.formula" />"
									class="mybutton" onclick="checkFormula();"/>
								<hrms:submit styleClass="mybutton" property="b_save">
									<bean:message key="org.maip.formula.preservation" />
								</hrms:submit>&nbsp;&nbsp;
							</td>
						</tr>
						<tr >
							<td align="center" valign="top" height="90px;" width="40%">
								<fieldset style="height: 100%;width:95%;vertical-align: center;vertical-align: middle;" >
								<legend><bean:message key='org.maip.reference.projects'/></legend>
							<table style="margin: 5px; ">
							   <tr>
							    <td>
								    <table weidth="100%" border="0">
								      <tr>
								        <td width="30%" align="right">
										<bean:message key="gz.formula.project"/>
										</td>
										<td>
										<select name="setlist" onchange="changeCodeValue();" size="1" style="width:140;font-size:9pt">
										</select>
										</td>
									  </tr>
									</table>
									<span id="codeview" style="display:none">
                                        <table width="100%" border="0" >
                                            <tr> 
                                                <td width="30%" align="right">
                                                    <bean:message key="conlumn.codeitemid.caption"/>
                                                </td>
                                                <td>
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

							<td valign="top" align="center" height="92px;" style="margin-bottom: 5px;">
								<fieldset style="height: 100%;width:98%;text-align: center; center;vertical-align: middle;">
								<legend>运算符号</legend>
									<table width="100%" align="center">
										<tr align="center">
										<td>
										<input type="button" name="Submit4" value="0" class="smallbutton"
											onclick="insertTxt(this.value);"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit42" value="1"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit43" value="2"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit44" value="3"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit45" value="4"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit46" value="("
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit477" value="="
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4763" value="&gt;="
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4753"
											value="<bean:message key="kq.formula.not"/>" class="smallbutton"
											onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4764" value="~"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit46"
											value="<bean:message key="kq.wizard.thing"/>"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 13%; font-size: 9pt">
										</td>
										</tr>
										<tr>
										<td align="center">
										<input type="button" name="Submit47" value="5"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit472" value="6"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit473" value="7"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit474" value="8"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit475" value="9"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit476" value=")"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4722" value="&gt;"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4754" value="&lt;="
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4752"
											value="<bean:message key="kq.formula.even"/>"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit46"
											value="<bean:message key="kq.formula.if"/>" class="smallbutton"
											onclick="insertTxt(this.value)"
											style="height: 20px; width: 10%; font-size: 9pt">
										<input type="button" name="Submit4764"
											value="<bean:message key="kq.formula.fou"/>" class="smallbutton"
											onclick="insertTxt(this.value)"
											style="height: 20px; width: 10%; font-size: 9pt">
										</td>
										</tr>
										<tr>
										<td align="center">
										<input type="button" name="Submit47" value="+"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit472" value="-"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit473" value="*"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit474" value="/"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit475" value="\"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit476" value="%"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4732" value="&lt;" class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4742" value="&lt;>"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit4762"
											value="<bean:message key="kq.formula.or"/>" class="smallbutton"
											onclick="insertTxt(this.value)"
											style="height: 20px; width: 7%; font-size: 9pt">
										<input type="button" name="Submit46"
											value="<bean:message key="kq.formula.then"/>"
											class="smallbutton" onclick="insertTxt(this.value)"
											style="height: 20px; width: 10%; font-size: 9pt">
										<input type="button" name="Submit4764"
											value="<bean:message key="kq.formula.end"/>" class="smallbutton"
											onclick="insertTxt(this.value)"
											style="height: 20px; width: 10%; font-size: 9pt">
										</td></tr></table>
								</fieldset>
							</td>
						</tr>
						<tr height="5">
							<td height="5" colspan="3" align="center">
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td>
				<br />
			</td>
		</tr>
		<tr>
			<td align="center">
				<table>
					<tr>
						<td align="center">
							<input type="button" value="<bean:message key="button.return"/>" class="mybutton" onclick="history.back();"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
   MusterInitData();
</script>