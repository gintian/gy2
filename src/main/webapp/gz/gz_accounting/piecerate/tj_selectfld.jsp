<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<script type="text/javascript">
<!--
var value="";
function queryOptions()
{
   var key=window.event.keyCode;
   var obj=document.getElementById("itemid");
   var idvalue=document.getElementById("queryvalue").value;
    if(idvalue=='')
       return;
    if(value!=idvalue)
    {
       if(key!=8&&key!=46)
       {
         for(var i=0;i<obj.options.length;i++)
         {
            if(obj.options[i].value.toUpperCase().indexOf(idvalue.toUpperCase())!=-1||obj.options[i].text.toUpperCase().indexOf(idvalue.toUpperCase())!=-1)
            {
               obj.options[i].selected=true;
            }
            else
            {
               obj.options[i].selected=false;
            }
          }
        }
      value=idvalue;
    }
    
   
}

function refreshLeftFiled11()
{
	document.pieceRateTjDefineForm.action="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_selectfld=link";
  	document.pieceRateTjDefineForm.submit();

}
    
function refreshLeftField()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("flag","getLeftFieldlist");
	hashvo.setValue("setId",pieceRateTjDefineForm.setId.value);
	var request=new Request({asynchronous:false,onSuccess:sucessRefreshLeftField,functionId:'3020091066'},hashvo);	

}

function sucessRefreshLeftField(outparamters)
{	
	var itemlist=outparamters.getValue("leftFieldList");
	if ((itemlist !=null)) {
	  AjaxBind.bind(pieceRateTjDefineForm.left_fields,itemlist);	
    }

}

function nextStep()
{
    var rightFiledIDs="";
   var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 return;
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
		}
	pieceRateTjDefineForm.rightFields.value=rightFiledIDs.substring(1);
	pieceRateTjDefineForm.action="/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_setGroup=link";
	pieceRateTjDefineForm.submit();
}
//-->
</script>
<base id="mybase" target="_self">
<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">
	<table width="590px;" border="0" cellspacing="1" align="center"
		cellpadding="1" class="ListTable">
		<THEAD>
			<tr>
				<td class="TableRow_lrt">
					<bean:message key="label.query.selectfield" />
				</td>
			</tr>
		</THEAD>
		<tr>
			<td class="RecordRow">
				<table width="100%">
					<tr>
						<td width="40%">
							<table width="100%" border="0" cellspacing="0" align="center"
								cellpadding="0">
								<tr nowarp>
									<td width="100%" align="left">
										<bean:message key="gz.bankdisk.preparefield" />
										<hrms:optioncollection name="pieceRateTjDefineForm"
											property="setList" collection="list" />
										<html:select name="pieceRateTjDefineForm" property="setId"
											onchange="refreshLeftField();" style="width:140">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
								<tr>
									<td align="center">
										<hrms:optioncollection name="pieceRateTjDefineForm"
											property="leftFieldList" collection="list" />
										<html:select name="pieceRateTjDefineForm" size="10"
											property="left_fields" multiple="multiple"
											ondblclick="additem('left_fields','right_fields');"
											style="height:250px;width:100%;font-size:9pt">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="10%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="additem('left_fields','right_fields');">
								<bean:message key="button.setfield.addfield" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="removeitem('right_fields');">
								<bean:message key="button.setfield.delfield" />
							</html:button>
						</td>
						<td width="40%">
							<table width="100%" border="0" cellspacing="0" align="center"
								cellpadding="0">
								<tr>
									<td width="100%" align="left">
										<bean:message key="gz.bankdisk.selectedfield" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left">
										<hrms:optioncollection name="pieceRateTjDefineForm"
											property="selectedFieldList" collection="list" />
										<html:select name="pieceRateTjDefineForm" size="10"
											property="right_fields" multiple="multiple"
											ondblclick="removeitem('right_fields');"
											style="height:250px;width:100%;font-size:9pt">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>

						<td width="10%" align="center">
								<html:button styleClass="mybutton" property="b_up"
									onclick="upItem($('right_fields'));">
									<bean:message key="button.previous" />
								</html:button>			
								<br>
								<br>
								<html:button styleClass="mybutton" property="b_down"
									onclick="downItem($('right_fields'));">
									<bean:message key="button.next" />
								</html:button>
	
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="RecordRowP" nowrap align="center" style="height: 35px;">
				<input type="button" name="query" class="mybutton"
					value="<bean:message key="gz.bankdisk.nextstep"/>"
					onclick="nextStep();">
				<input type="button" name="cancel"
					value="<bean:message key="button.close"/>" class="mybutton"
					onclick="window.close()" />
				<input type="hidden" name="rightFields" value="">
			</td>
		</tr>
	</table>
</html:form>