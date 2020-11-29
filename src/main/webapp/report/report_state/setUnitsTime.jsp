<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%
	int i=0;
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="../../ajax/constant.js"></script>
<script language="javascript" src="../../ajax/basic.js"></script>
<script language="javascript" src="../../ajax/common.js"></script>
<script language="javascript" src="../../ajax/control.js"></script>
<script language="javascript" src="../../ajax/dataset.js"></script>
<script language="javascript" src="../../ajax/editor.js"></script>
<script language="javascript" src="../../ajax/dropdown.js"></script>
<script language="javascript" src="../../ajax/table.js"></script>
<script language="javascript" src="../../ajax/menu.js"></script>
<script language="javascript" src="../../ajax/tree.js"></script>
<script language="javascript" src="../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../ajax/command.js"></script>
<script language="javascript" src="../../ajax/format.js"></script>
<script type="text/javascript" src="../../js/validateDate.js"></script>
<script language="javascript" src="../../js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="../../js/popcalendar.js"></script>
<script type="text/javascript">
<%--注释影响程序正常运行的无用代码 wangbs 20190318--%>
// var info=dialogArguments;
 function saveTime(){
	var formd = "yyyy-mm-dd";
	var obj=document.getElementsByName("adddate");
	var valueandtime="";
		for(var i=0;i<obj.length;i++){
			var type=obj[i].id;
			var rltype;
			if(type.indexOf("o")!=-1){
				rltype=type.substr(1);
			}
			var value=obj[i].value;
			if(value.length!=0){
							var myReg =/^(-?\d+)(\.\d+)?$/
							 if(IsOverStrLength(value,10))
							 {
								 alert(value+"不是正确格式！正确日期格式为："+formd+" ！");
								 return ;
							 }
							 else
							 {
							 	if(trim(value).length!=10)
							 	{
							 		 alert(value+"不是正确格式！正确日期格式为："+formd+" ！");
									 return ;
							 	}
								var year=value.substring(0,4);
								var month=value.substring(5,7);
								var day=value.substring(8,10);
								if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
							 	{
									 alert(value+"不是正确格式！正确日期格式为："+formd+" ！");
									 return;
							 	}
							 	/*if(year<1900||year>2100)
							 	{
							 		 alert(itemdesc+" 年范围为1900~2100！");
									 return false;
							 	}*/
							 	
							 	if(!isValidDate(day, month, year))
							 	{
									 alert(value+"错误，无效时间！");
									 return ;
							 	}
							 }
				var pattern = /^[1-9]\d{3}-((0[1-9]{1})|(1[0-2]{1}))-((0[1-9]{1})|([1-2]{1}\d{1})|(3[0-1]{1}))$/;
				if(!pattern.test(value)){
					alert(value+"不是正确格式！正确日期格式为："+formd);
					return;
				}else{
					valueandtime+=rltype+"`"+value+";"
				}
			}else{
				valueandtime+=rltype+"`"+value+";"
			}
		}
	var hashvo=new ParameterSet();
	hashvo.setValue("valueAndTime",valueandtime);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnOk,functionId:'0305000213'},hashvo);		
	}
	function returnOk(outparamters){
		var info=outparamters.getValue("ok");
		if(info=='ok'){
		    //兼容ie 谷歌 wangbs 20190318
		    if(parent.parent.Ext){
		        var operateTarget = parent.parent.Ext.getCmp("setTimeWin");
                if(operateTarget){
                    operateTarget.returnValue = "ok";
                    operateTarget.close();
				}else{
                    window.returnValue="ok";
                    window.close();
                }
			}else{
                window.returnValue="ok";
                window.close();
            }
		}
	}
	function cancel(){
        //兼容ie 谷歌 wangbs 20190318
        if(parent.parent.Ext){
            var operateTarget = parent.parent.Ext.getCmp("setTimeWin");
            if(operateTarget){
                operateTarget.returnValue = "false";
                operateTarget.close();
            }else{
                window.returnValue="false";
                window.close();
            }
        }else{
            window.returnValue="false";
            window.close();
        }
	}
</script>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">

			<form name="reportStateForm" method="post" action="">
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.tsortname" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sortid" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="reporttypelist.sort" />
							</td>
							
							<td align="center" class="TableRow" nowrap>
								<bean:message key="column.sys.belongTime" />
							</td>
							
						</tr>
					</thead>

					<hrms:extenditerate id="element" name="reportStateForm" property="reportTypeList.list" indexes="indexes" pagination="reportTypeList.pagination" pageCount="15" scope="session">
					          <%
					          
					     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
					     		String type=(String)abean.get("tsortid");
					          if(i%2==0)
					          {
					          %>
					          <tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");' >
					          <%}
					          else
					          {%>
					          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'  >
					          <%
					          }
					          i++;          
					          %>  
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="name" filter="false" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" nowrap>
								<bean:write name="element" property="tsortid" filter="false" />
								&nbsp;
							</td>
							<td align="center" class="RecordRow" nowrap>
								<bean:write name="element" property="fontname" filter="false" />
								&nbsp;
							</td>
							<td align="left" class="RecordRow" nowrap>
								<logic:equal value="true" name="element" property="hasTime" >
							<input type="text" name="adddate" size="14" style="width:200px"  value='<bean:write name="element" property="time" />' style="border: 1pt solid;width:150px"  extra="editor"  id='o<%=type %>'  dropDown="dropDownDate"/>
								</logic:equal>
							</td>
						</tr>
					</hrms:extenditerate>

				</table>

				<table width="100%"   class='RecordRowP' align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<bean:message key="label.page.serial"/>
							<bean:write name="reportStateForm" property="reportTypeList.pagination.current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="reportStateForm" property="reportTypeList.pagination.count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="reportStateForm" property="reportTypeList.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationlink name="reportStateForm" property="reportTypeList.pagination" nameId="reportTypeList" >
								</hrms:paginationlink>
						</td>
					</tr>
				</table>
				
				
				<table width="75%" align="center">
					<tr>
						<td align="center" nowrap colspan="4">
						<input type="button" name="b_save" class="mybutton" value="<bean:message key="button.save"/>" onclick="saveTime();"/>
						<input type="button" name="b_save" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="cancel();"/>
						</td>
					</tr>
				</table>
			</form>

		</td>
	</tr>
	
</table>