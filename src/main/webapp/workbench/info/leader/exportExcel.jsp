<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript">
 
 	// 选择备选指标
 	function changeInfor(){
 		// 执行初始化操作即可
 		initData2();
 	}
 	
	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");
                var resultlist=new Array();
                for(var i=0,n=0;i<fieldlist.length;i++)
                {
                   var obj=fieldlist[i];
                  
                      resultlist[n]=fieldlist[i];
                      n++;
                   
                }
		AjaxBind.bind(selfInfoForm.left_fields,resultlist);		
	}

function initData2()
{  
	var fieldsetid=$F("fieldsetid");
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'0201001062'},hashvo);
}
/**从后台取得相应的数据,初始化前台*/
function showSetList(outparamters)
{
		var setlist=outparamters.getValue("setlist");
		var dlist=outparamters.getValue("dlist");
		AjaxBind.bind(selfInfoForm.setlist,/*$('setlist')*/setlist);		
		
	   	while ($('left_fields').childNodes.length > 0) {
				$('left_fields').removeChild($('left_fields').childNodes[0]);
	   	}		
		if($('setlist').options.length>0)
		{
		  $('setlist').options[0].selected=true;
		  $('setlist').fireEvent("onchange");
		}
}
function additems(sourcebox_id,targetbox_id)
{
  var left_vo,right_vo,vos,voss,i;
  vos= document.getElementsByName(sourcebox_id);
  if(vos==null)
  	return false;
  left_vo=vos[0];
  var nofield=true;

  voss= document.getElementsByName(targetbox_id);  
  right_vo=voss[0];
  for(i=0;i<left_vo.options.length;i++)
  {
   if(left_vo.options[i].selected)
   {
    if(right_vo.options.length==0)
     {
        var no = new Option();
        no.value=left_vo.options[i].value;
        no.text=left_vo.options[i].text;
        right_vo.options[right_vo.options.length]=no;
     }else{
        for(var j=0;j<right_vo.options.length;j++)
        {
           if(right_vo.options[j].text==left_vo.options[i].text)
           {
               nofield=false; 
           }         
        }
          if(nofield)
           {
              var no = new Option();
              no.value=left_vo.options[i].value;
              no.text=left_vo.options[i].text;
              right_vo.options[right_vo.options.length]=no;
           }
           nofield=true;
    }
   }
 }
 return true;	  	
}

function selectitems(sourcebox_id){
	var vos,right_vo,i,items;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null || vos[0].length==0)
	{
		alert("请选择要导出的指标！");
		return;  	
	}
	
	right_vo=vos[0];  
	for(i=0;i<right_vo.options.length;i++)
	{
		if(i==0)
			items=right_vo.options[i].value;
		else
			items+=","+right_vo.options[i].value;
	}
	if(parent.parent.Ext)
		if(parent.parent.Ext.getCmp('importExcel')){//ext 弹窗返回数据方法  wangb 20190318
			var win = parent.parent.Ext.getCmp('importExcel');
			win.return_vo =items;
			win.close();
			return;		
		}
	window.returnValue=items;
	window.close();
}
function closed(){
	if(parent.parent.Ext){//关闭ext 弹窗方法   wangb 20190318
		if(parent.parent.Ext.getCmp('importExcel')){
			parent.parent.Ext.getCmp('importExcel').close();
			return;		
		}
	}

	window.close();
}
</script>
<html:form action="/workbench/info/leader/showinfodata">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td valign="top">
				<br>
				<table width="99%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="RecordRow">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap colspan="3">
								选择导出指标
								&nbsp;&nbsp;
							</td>
						</tr>
					</thead>
					<tr>
						<td width="100%" align="center" class="RecordRow" nowrap>
							<table>
								<tr>
									<td align="center" width="46%">
										<table align="center" width="100%">
											<tr>
												<td align="left">
													<bean:message key="static.target" />
													&nbsp;&nbsp;
												</td>
											</tr>
											 <tr >
   	                                           <td class="">
                                                  <hrms:optioncollection name="selfInfoForm" property="fieldSetDataList" collection="list" />
												  <html:select name="selfInfoForm" property="fieldsetid" indexed="fieldsetid" style="width:100%" onchange="changeInfor();">
														<html:options collection="list" property="dataValue" labelProperty="dataName"/>
												  </html:select>
   	                                            </td>
   	                                        </tr>
											<tr>
												<td align="center">
													<select name="left_fields" multiple="multiple"
														ondblclick="additems('left_fields','right_fields');"
														style="height: 200px; width: 100%; font-size: 9pt">
													</select>
												</td>
											</tr>
										</table>
									</td>
									<td width="8%" align="center">
										<html:button styleClass="mybutton" property="b_addfield"
											onclick="additems('left_fields','right_fields');">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<br>
										<br>
										<html:button styleClass="mybutton" property="b_delfield"
											onclick="removeitem('right_fields');">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="46%" align="center">
										<table width="100%">
											<tr>
												<td width="100%" align="left">
													<bean:message key="static.ytarget" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">
													<hrms:optioncollection name="selfInfoForm"
														property="rightlist" collection="list" />
													<html:select name="selfInfoForm" property="right_fields"
														multiple="multiple" size="10"
														ondblclick="removeitem('right_fields');"
														style="height:230px;width:100%;font-size:10pt">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>

												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr height="35">
			<td align="center" nowrap colspan="3">
				<input type="button" onclick="selectitems('right_fields');" value="<bean:message key='button.ok' />" class="mybutton"/>
				<input type="button" name="btnreturn" value="<bean:message key='button.cancel' />" onclick="closed();" class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
   initData2();
</script>