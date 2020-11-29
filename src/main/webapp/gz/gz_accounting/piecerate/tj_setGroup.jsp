<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script type="text/javascript">
<!--

function setgroupfld(){
	if(!this.pieceRateTjDefineForm.useGroup.checked){
		alert("请选择启用分组汇总！");
		return;
	}
    var thecodeurl ="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setGroupFld=link&groupFlds="+ pieceRateTjDefineForm.groupFlds.value; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo!=null)
	{	
	  var itemid = return_vo;  
	  pieceRateTjDefineForm.groupFlds.value=itemid;

    }
}

function setsummaryfld(){
	if(!this.pieceRateTjDefineForm.useGroup.checked){
		alert("请选择启用分组汇总！");
		return;
	}
    var thecodeurl ="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setSummaryFld=link&summaryFlds="+ pieceRateTjDefineForm.summaryFlds.value
                  +"&groupFlds="+ pieceRateTjDefineForm.groupFlds.value; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo!=null)
	{	
	  var itemid = return_vo;  
	  pieceRateTjDefineForm.summaryFlds.value=itemid;

    }
}

function setTjWhere(){
    var thecodeurl ="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setCondition=link&tjWhere="+ getEncodeStr(pieceRateTjDefineForm.tjWhere.value);
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:330px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null)
	{	
	  var itemid = return_vo;  
	  pieceRateTjDefineForm.tjWhere.value=getDecodeStr(itemid);

    }
}

function nextStep()
{
    var useGroup="0";
    if (document.getElementById("useGroup").checked) { 
      useGroup ="1";}
   // pieceRateTjDefineForm.useGroup.value=useGroup;
	pieceRateTjDefineForm.action="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setOrderFld=link&useGroup="+useGroup;
	pieceRateTjDefineForm.submit();
}

//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">
<html:hidden name="pieceRateTjDefineForm" property="groupFlds" />
<html:hidden name="pieceRateTjDefineForm" property="summaryFlds" />

	<fieldset  style="width: 90%; height: 80%" align="center" >
	<legend>定义分组汇总及条件</legend>
		<table width="95%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
			<tr>
				<td  nowrap align="left" style="height:35px">
                   <html:checkbox property="useGroup" name="pieceRateTjDefineForm" value="1" >
                      	是否启用分组汇总
                   </html:checkbox>	
                 </td>
                 <td  nowrap align="left" style="height:35px">  
		           <input type="button" name="btnGroup" value="分组指标" class="mybutton" onclick="setgroupfld()"/>
		           <input type="button" name="bthSum" value="汇总指标及方式" class="mybutton" onclick="setsummaryfld();"/> 
				</td>
			</tr>
			
			<tr> 
          		<td colspan="2" style="align:left;"> 
            		<html:textarea name="pieceRateTjDefineForm" property="tjWhere"  onclick=""  cols="58" rows="14" styleId="shry"></html:textarea> 
            	</td>
            	<td  valign ="top" style="align:left;"> 
            		<input type="button" name="bthSet" value="设置" class="mybutton" onclick="setTjWhere();"/>
            	</td>
        	</tr>
			<tr height="20px">
				<td colspan="4" align="center">
					<hrms:submit styleClass="mybutton" property="br_reselectfld">
					  <bean:message key="button.query.pre"/>
					</hrms:submit>  					         
					<input type="button" name="query" class="mybutton" value="<bean:message key="gz.bankdisk.nextstep"/>" onclick="nextStep();">
	
				</td>
			</tr>
		</table>
</fieldset>						
</html:form>