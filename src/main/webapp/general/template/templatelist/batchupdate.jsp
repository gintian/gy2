
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script>
<script type="text/javascript" src="/js/function.js"></script>  
 <%
	int i=0;
 UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 String bosflag= userView.getBosflag();
	
%>
<script language="javascript">
//	var whl=dialogArguments;
	

	function set_refitem(objname)
	{
	  var itemdesc="";
	  var vo=$(objname);
	  for(i=vo.options.length-1;i>=0;i--)
	  {
	    if(vo.options[i].selected)
	    {
			itemdesc=vo.options[i].text
			break;
	    }
	  }	
 	 symbol('formula',itemdesc);
	}

	function set_codeitem(objname)
	{
	  var itemdesc="";
	  var vo=$(objname);
	  for(i=vo.options.length-1;i>=0;i--)
	  {
	    if(vo.options[i].selected)
	    {
			itemdesc=vo.options[i].value
			break;
	    }
	  }	
 	  symbol('formula',"\""+itemdesc+"\"");
	}	
	
	function getCodeValue()
	{
  		var item=document.getElementById("itemid").value;
		var in_paramters="itemid="+item;
   		var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
	}
	
	function showCodeFieldList(outparamters)
	{
		var codelist=outparamters.getValue("codelist");
		if(codelist.length>1)
		{
			Element.show('coderow');
//			AjaxBind.bind(templateListForm.codeitem,codelist);
		}
		else
		{
			Element.hide('coderow');
		}	
}	
function function_Wizard(tableid,formula){

    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_search=link&salaryid=&tableid="+tableid; 
    var return_vo= window.showModalDialog(thecodeurl,"", 
              "dialogWidth:400px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
		symbol(formula,return_vo);
	}	
}
function run_batch_update(tableid){
   var param=new Object();
			    param.itemid=document.getElementById("itemid").value;
			    param.formula=document.templateListForm.formula.value;
			    param.conditions=document.templateListForm.conditions.value;
//			    if(document.templateListForm.selchecked.checked)
//			    param.selchecked="1";
//			    else
//			    param.selchecked="0";
                var isOnlySelected=0;
				var obj=document.getElementsByName("selchecked");
				    if(obj!=null)
				    {
				        for(var i=0;i<obj.length;i++){
				          if(obj[i].checked){
				             isOnlySelected=obj[i].value; 
				          }
				        }
			    }
                param.selchecked=isOnlySelected;
			    window.returnValue=param;
				window.close();
}
function condiTions(tableid,conditions){
	conditions=document.templateListForm.conditions.value;
    var thecodeurl ="/general/salarychange/calculating_conditions.do?b_query=link&tableid="+tableid+"&conditions="+conditions; 
    var dialogWidth="520px";
    var dialogHeight="450px";
    if (isIE6()){
    	dialogWidth="560px";
    	dialogHeight="480px";
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogWidth+";resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
		document.getElementById("conditions").value=return_vo;   
	}    
	}
</script>
<hrms:themes></hrms:themes>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<html:form action="/general/template/batchupdate"> 
<input type="hidden" name="conditions">


	<div id='wait' style='position:absolute;top:40;left:40;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="org.autostatic.mainp.calculation.wait"/>
				</td>
			</tr>
			<tr>
			
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10" >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
  </div>
 <%
     if (!"hcm".equals(bosflag)) {
 %>
<br>
<%
    }
%>
<fieldset align="center"  style="width:490px"  class="batchupdate_margin">
	<legend>
		<bean:message key="menu.gz.batch.update" />
	</legend>
	
	<table align="center" width="100%">
		<tr>
			<td width="70%">
				<table width="100%" border="0" cellspacing="0" align="left"
					cellpadding="2">
					<tr>
						<td align="right">
							<bean:message key="label.rs.gzitem" />
						</td>
						<td>
							<html:select name="templateListForm" property="itemid" size="1"
								onchange="getCodeValue();">
								<html:optionsCollection property="targitemlist"
									value="dataValue" label="dataName" />
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="right" valign="top">
							<bean:message key="label.gz.update.src" />
						</td>
						<td>
							<html:textarea name="templateListForm" property="formula"
								cols="42" rows="15"
								onclick="this.pos=document.selection.createRange();">
							</html:textarea>
						</td>
					</tr>
					<tr>
						<td align="right" valign="top"></td>
						<td>
							<!--  <input type="checkbox" name="selchecked">只修改勾选数据 -->
							<input type="radio" name="selchecked" id="all" value="0" />
							全部记录
							<input type="radio" name="selchecked" id="onlysel" value="1"
								checked />
							选中记录
						</td>

					</tr>
					<tr>
						<td align="right">
							<bean:message key="org.maip.reference.projects" />
						</td>
						<td>
							<html:select name="templateListForm" property="ref_itemid"
								size="1" onchange="set_refitem('ref_itemid');"
								style="width:180;font-size:9pt">
								<html:optionsCollection property="ref_itemlist"
									value="dataValue" label="dataName" />
							</html:select>
						</td>
					</tr>
					<tr id="coderow">
						<td align="right">
							<bean:message key="conlumn.codeitemid.caption" />
						</td>
						<td>
							<select name="codeitem" onchange="set_codeitem('codeitem');"
								style="width: 180; font-size: 9pt">
							</select>
						</td>
					</tr>
					<tr align="right" valign="top">
						<td>
							<bean:message key="kq.set.card.explain" />
							:
						</td>
						<td align="left">
							<bean:message key="label.gz.descript" />
						</td>
					</tr>
				</table>
			</td>

			<td width="30%" valign="top">
			    <table style="margin-top:28px;">
			     <tr>
			         <td>
				        <button name="btn_cond" Class="mybutton" onclick="condiTions('${templateListForm.tabid}','${templateListForm.conditions}');">
	                       <bean:message key="button.cond" />
	                    </button>
			         </td>
			    </tr>
			    <tr>
			         <td>
			           <button name="btn_wizard" Class="mybutton" onclick="function_Wizard('${templateListForm.tabid}','formula')"  style="margin-top: 15px;">
                            <bean:message key="button.wizard" />
                       </button>   
			         </td>
			     </tr>
			    </table>
			</td>
		</tr>
	</table>
</fieldset>
<table style="width:490px;" cellspacing="0" cellpadding="0">
    <tr>
        <td align="center" height="10">
           <button id="btn_ok" Class="mybutton" onclick="run_batch_update('${templateListForm.tabid}')" style="margin-top:5px;">
            <bean:message key="button.ok" />
           </button> 
           <button name="cancel" Class="mybutton" onclick="window.close();" style="margin-top:5px;">
            <bean:message key="button.cancel" />
           </button>   
        </td>
    </tr>
</table>

	<script language="javascript">
	var list = '${templateListForm.targitemlist}';
	if(list.length>2){
	
	}else{
	var bt=document.getElementById("btn_ok");
	bt.disabled="disabled";
	}
	Element.hide('coderow');
</script>
</html:form>


  