<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page
	import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.query.QueryInterfaceForm"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
	String ver = (String) request.getParameter("ver");
	String returnvalue = (String) request.getParameter("returnvalue");
	ver = ver != null && ver.length() > 0 ? ver : "";
	String userName = null;
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag = "";
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		bosflag = userView.getBosflag();
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");          
	}
%>
<script language="javascript">
	/*通用查询及简单查询
	 *queryType查询类型 =1简单查询 =2通用查询
	 */
	function openSimpleQueryDialog(infor_type){
		var obj=document.getElementById('curr_id');
		var selidx=getSelectedIndex(obj);
		if(selidx==-1)
		  return;
		var cond_id=obj.options[selidx].value;  
	    var hashvo=new ParameterSet();
	    hashvo.setValue("curr_id",cond_id);
	    hashvo.setValue("infor_type",infor_type);
   	    var request=new Request({method:'post',asynchronous:false,onSuccess:savecond,functionId:'0202001030'},hashvo);
	}	
	
	function savecond(outparamters)
	{
	  	var info,queryType,dbPre,oldExpress;	
	  	var curr_id=outparamters.getValue("curr_id");
	  	extcurr_id=curr_id;
	  	var lexpr=outparamters.getValue("lexpr");
		info=outparamters.getValue("infor_type");
		var isEncode = true;//加密参数值 isEncode wangb
		dbPre="Usr";
		queryType="2";
		var strExpression = generalExpressionDialog(info,dbPre,queryType,lexpr,'','',isEncode);//方法出添加一参数, 表示是否加密 路径   wangb 20180308
		if(!strExpression){
			strExpression="";
		}else{
	    	var hashvo=new ParameterSet();
	    	hashvo.setValue("curr_id",curr_id);
	    	hashvo.setValue("lexpr",strExpression);
   	   	 	var request=new Request({method:'post',asynchronous:false,functionId:'0202001031'},hashvo);
		}	  
	}
	/*非IE浏览器弹窗调用方法    wangb 20180125*/
	var extcurr_id;
	function extOpenStrExpression(strExpression){
		Ext.getCmp('selectpoint_win').close();
		if(!strExpression){
			strExpression="";
		}else{
	    	var hashvo=new ParameterSet();
	    	hashvo.setValue("curr_id",extcurr_id);
	    	hashvo.setValue("lexpr",strExpression);
   	   	 	var request=new Request({method:'post',asynchronous:false,functionId:'0202001031'},hashvo);
		}
	}
	
	function blackMaint(checkflag,target)
	{
	   if(checkflag=='2'){
	    	queryInterfaceForm.action="/templates/index/portal.do?b_query=link";
	    	queryInterfaceForm.submit(); 
    	}else{
		   queryInterfaceForm.action="/system/home.do?b_query=link";
		   queryInterfaceForm.target=target;
		   queryInterfaceForm.submit(); 
	    }
    }
    function blackMaintVer(checkflag){
    	if(checkflag=='hcm'){
    		queryInterfaceForm.action="/templates/index/hcm_portal.do?b_query=link";
	    	queryInterfaceForm.submit();
    	}else{
    		queryInterfaceForm.action="/templates/index/portal.do?b_query=link";
	    	queryInterfaceForm.submit();
    	}
    }
    function taxisbyid()
    {
         var curr_vos= document.getElementsByName('curr_id');
         var curr_vo=curr_vos[0];  
          var idx=getSelectedIndex(curr_vo);
		   if(idx==-1)
		     return;
		  
         var curr_value="";  
         var selects=new Array();      
         for(var i=0;i<curr_vo.options.length;i++)
         {
              if(curr_vo.options[i].selected)
                curr_value=curr_vo.options[i].value ;
                selects[i]=curr_vo.options[i].value ;         
         } 
         
         var hashvo=new ParameterSet();
	     hashvo.setValue("selects",selects);
	     if(document.getElementById("hidcategoriesselect"))
	     	hashvo.setValue("categories",document.getElementById("hidcategoriesselect").value);
	     else
	     	hashvo.setValue("categories","");
	     hashvo.setValue("type","${queryInterfaceForm.type}");
	      hashvo.setValue("curr_value",curr_value);
         var request=new Request({method:'post',onSuccess:showSelect,asynchronous:false,functionId:'0202001032'},hashvo);
    }
	function showSelect(outparamters)
	{
	   var condlist=outparamters.getValue("condlist");	   
	   var curr_value=outparamters.getValue("curr_value");
	   while ($('curr_id').childNodes.length > 0) {
				$('curr_id').removeChild($('curr_id').childNodes[0]);
	   	}		
	   AjaxBind.bind(queryInterfaceForm.curr_id,condlist);
	   var curr_vos= document.getElementsByName('curr_id');
       var curr_vo=curr_vos[0];  
        for(var i=0;i<curr_vo.options.length;i++)
         {
              if(curr_vo.options[i].value==curr_value){
                curr_vo.options[i].selected=true;     
              	break;
              }                   
         }
	}
	
	function changeCond(categories){
	var ids="";
	var texts="";
	var conds=queryInterfaceForm.curr_id.options;
	//for(var i=conds.length-1;i>=0;i--){
		//if(conds[i].selected){
			//ids+="','"+conds[i].value
			//texts+="，"+conds[i].text;
		//}
	//}
	//alert(ids);
	if(ids.length>0){
		if(categories.length==0){
			if(!confirm("确认要将["+texts.substring(1)+"]删除分类吗")){
				ids="";
			}
		}else{
			if(!confirm("确认要将["+texts.substring(1)+"]移动到"+categories+"分类下吗")){
				ids="";
			}
		}
	}
		var hashVo=new ParameterSet();
		hashVo.setValue("ids",ids);
	    hashVo.setValue("categories",categories);
	    hashVo.setValue("type","${queryInterfaceForm.type}");
	    var request=new Request({method:'post',asynchronous:false,onSuccess:changeCondOk,functionId:'0202001072'},hashVo);
		function changeCondOk(outparameters){
	   		var condlist=outparameters.getValue("condlist");
			AjaxBind.bind(queryInterfaceForm.curr_id,condlist);
		}
}
window.onload = function(){
	var hidcategoriesselect = document.getElementById("hidcategoriesselect");
	if(hidcategoriesselect)
		hidcategoriesselect.attachEvent?hidcategoriesselect.attachEvent("onpropertychange",function(){changeCond(hidcategoriesselect.value);}):hidcategoriesselect.addEventListener("click",function(){changeCond(hidcategoriesselect.value);},false);
}

function search(){
	if(!validate('RS','dbpre','人员库','RS','curr_id','常用条件'))
		return false;
	
	var searchButton = document.getElementById("searchButton");
	if(searchButton)
		searchButton.disabled = true;
	
	var deleteButton = document.getElementById("deleteButton");
	if(deleteButton)
		deleteButton.disabled = true;
	
	var editButton = document.getElementById("editButton");
	if(editButton)
		editButton.disabled = true;
	
	var returnButton = document.getElementById("returnButton");
	if(returnButton)
		returnButton.disabled = true;
	
	var resetButton = document.getElementById("resetButton");
	if(resetButton)
		resetButton.disabled = true;
	
	queryInterfaceForm.action="/workbench/query/gquery_interface.do?b_query=link";
	queryInterfaceForm.submit();
}
</script>
<hrms:themes />
<html:form action="/workbench/query/gquery_interface">
	<%
		if ("hcm".equals(bosflag)) {
	%>
	<table width="700" border="0" cellpadding="0" cellspacing="0"
		align="center">
		<%
			} else {
		%>
		<table width="700" border="0" cellpadding="0" cellspacing="0"
			align="center" style="margin-top: 10px">
			<%
				}
			%>
			<tr height="20">
				<td align="left" class="TableRow_lrt">
					<bean:message key="label.query.gquery" />
				</td>
			</tr>
			<tr>
				<td class="framestyle">
					<table width="90%" border="0" cellpmoding="0" cellspacing="0"
						class="DetailTable" cellpadding="0" align="center">
						<tr><td colspan="2" height="5px"></td></tr>
						<logic:equal name="queryInterfaceForm" property="type" value="1">
							<logic:notEmpty name="queryInterfaceForm" property="catelist">
								<tr>
									<td style="height: 30px;" width="25%" align="right" nowrap class="tdFontcolor">
										<bean:message key="kh.field.name"/>
									</td>
									<td align="left" nowrap>
										<html:select name="queryInterfaceForm" property='categories'
											styleId="hidcategoriesselect" style="width:300px;">
											<!-- onchange="changeCond(this.value)" 改用js注册事件  解决问题点击重置按钮分类选择中的值回不到原来 LiWeichao -->
											<option value=""></option>
											<html:optionsCollection property="catelist" value="dataValue"
												label="dataName" />
										</html:select>
									</td>
								</tr>
							</logic:notEmpty>
							<hrms:importgeneraldata showColumn="dbname" valueColumn="pre"
								flag="false" paraValue="" sql="queryInterfaceForm.dbcond"
								collection="list" scope="page" />
							<bean:size id="length" name="list" scope="page" />
							<tr
								<logic:lessThan value="2" name="length">style="display: none"</logic:lessThan>>
								<td align="right" width="25%" nowrap class="tdFontcolor">
									<bean:message key="label.query.dbpre"/>
								</td>
								<td align="left" nowrap>
									<html:select name="queryInterfaceForm" property="dbpre"
										size="1" style="width:300px;">
										<!-- <html:option value="#"><bean:message key="label.select.dot"/></html:option> -->
										<html:options collection="list" property="dataValue"
											labelProperty="dataName" />
										<html:option value="All">全部人员库</html:option>
									</html:select>
									&nbsp;
								</td>
							</tr>
						</logic:equal>
						<tr>
							<td width="25%"></td>
							<td align="left" class="" nowrap>
								<table align="left" border="0" cellpmoding="0" cellspacing="0"
									cellpadding="0">
									<tr>
										<td>
											<!-- <span style="height: 59px; width: 100px; border: #F0F0F0 1pt solid;"> -->
												<html:select name="queryInterfaceForm" styleId="curr_id"
													property="curr_id" size="1" multiple="false"
													style="height:259px;width:300px;margin-right:-1px;margin-bottom:-1px;">
													<html:optionsCollection property="condlist" value="id"
														label="name" />
												</html:select> 
											<!-- </span> -->
										</td>
										<td height="5px" width="5px">
										</td>
										<td align="center">
											<hrms:priv func_id="2601006,0303012">
												<table border="0" cellpadding="0" cellspacing="0"
													align="center">
													<tr>
														<td>
															<html:button styleClass="mybutton" property="b_up"
																onclick="upItem($('curr_id'));taxisbyid();">
																<bean:message key="button.previous" />
															</html:button>
														</td>
													</tr>
													<tr>
														<td height="30px"></td>
													</tr>
													<tr>
														<td>
															<html:button styleClass="mybutton" property="b_down"
																onclick="downItem($('curr_id'));taxisbyid();">
																<bean:message key="button.next" />
															</html:button>
														</td>
													</tr>
												</table>
											</hrms:priv>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td></td>
							<td align="left" nowrap class="tdFontcolor">
								<html:checkbox name="queryInterfaceForm" property="history"
									value="1">
									<bean:message key="label.query.history" />
								</html:checkbox>
								<logic:equal name="userView" property="status" value="0">
									<html:checkbox name="queryInterfaceForm" property="result"
										value="1">
										<bean:message key="label.query.second" />
									</html:checkbox>
								</logic:equal>
								<html:checkbox name="queryInterfaceForm" property="like"
									value="1">
									<bean:message key="label.query.like" />
								</html:checkbox>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr class="list3">
				<td align="center" colspan="2" height="5px">
				</td>
			</tr>
			<tr class="list3">
				<td colspan="4" align="center">
					<logic:equal name="queryInterfaceForm" property="type" value="2">
						<html:radio name="queryInterfaceForm" property="qobj" value="1">
							<bean:message key="label.query.dept" />
						</html:radio>
						<html:radio name="queryInterfaceForm" property="qobj" value="2">
							<bean:message key="label.query.org" />
						</html:radio>
						<html:radio name="queryInterfaceForm" property="qobj" value="0">
							<bean:message key="label.query.all" />
						</html:radio>
					</logic:equal>
					<html:button styleClass="mybutton" styleId="searchButton" property="b_query"
						onclick="search();">
						<bean:message key="button.query" />
					</html:button>
					<hrms:priv func_id="2601001,0303010">
						<logic:equal name="queryInterfaceForm" property="type" value="1">
							<hrms:submit styleClass="mybutton" styleId="deleteButton" property="b_delete"
								function_id="2601001,0303010"
								onclick="document.queryInterfaceForm.target='_self';return (validate('RS','curr_id','常用条件')&&ifmsdel());document.returnValue;">
								<bean:message key="button.delete" />
							</hrms:submit>
						</logic:equal>
					</hrms:priv>
					<hrms:priv func_id="2301001">
						<logic:equal name="queryInterfaceForm" property="type" value="2">
							<hrms:submit styleClass="mybutton" styleId="deleteButton" property="b_delete"
								function_id="2301001"
								onclick="document.queryInterfaceForm.target='_self';return (validate('RS','curr_id','常用条件')&&ifmsdel());document.returnValue;">
								<bean:message key="button.delete" />
							</hrms:submit>
						</logic:equal>
					</hrms:priv>
					<hrms:priv func_id="2501001">
						<logic:equal name="queryInterfaceForm" property="type" value="3">
							<hrms:submit styleClass="mybutton" property="b_delete" styleId="deleteButton"
								function_id="2501001"
								onclick="document.queryInterfaceForm.target='_self';return (validate('RS','curr_id','常用条件')&&ifmsdel());document.returnValue;">
								<bean:message key="button.delete" />
							</hrms:submit>
						</logic:equal>
					</hrms:priv>
					<logic:equal name="queryInterfaceForm" property="type" value="1">
						<hrms:priv func_id="2601005,0303011">
							<html:button styleClass="mybutton" property="b_edit" styleId="editButton"
								onclick="openSimpleQueryDialog('${queryInterfaceForm.type}');">
								<bean:message key="button.edit" />
							</html:button>
						</hrms:priv>
					</logic:equal>
					<logic:equal name="queryInterfaceForm" property="type" value="2">
						<hrms:priv func_id="2301004">
							<html:button styleClass="mybutton" property="b_edit" styleId="editButton"
								onclick="openSimpleQueryDialog('${queryInterfaceForm.type}');">
								<bean:message key="button.edit" />
							</html:button>
						</hrms:priv>
					</logic:equal>
					<logic:equal name="queryInterfaceForm" property="type" value="3">
						<hrms:priv func_id="2501004">
							<html:button styleClass="mybutton" property="b_edit" styleId="editButton"
								onclick="openSimpleQueryDialog('${queryInterfaceForm.type}');">
								<bean:message key="button.edit" />
							</html:button>
						</hrms:priv>
					</logic:equal>
					<html:reset styleClass="mybutton" styleId="resetButton">
						<bean:message key="button.clear" />
					</html:reset>
					<%
						request.setAttribute("bosflag", bosflag);
							if (ver != null && ver.equals("5")) {
					%>
					<input type="hidden" name="ver" value="<%=ver%>">
					<logic:equal name="queryInterfaceForm" property="home" value="5">
						<html:button styleClass="mybutton" property="bc_btn1" styleId="returnButton"
							onclick="blackMaintVer('${bosflag}');">
							<bean:message key="button.return" />
						</html:button>
					</logic:equal>
					<logic:equal name="queryInterfaceForm" property="home" value="6">
						<html:button styleClass="mybutton" property="bc_btn1" styleId="returnButton"
							onclick="blackMaintVer('${bosflag}');">
							<bean:message key="button.return" />
						</html:button>
					</logic:equal>
					<logic:equal name="queryInterfaceForm" property="home" value="0">
						<html:button styleClass="mybutton" property="bc_btn1" styleId="returnButton"
							onclick="window.location.replace('/workbench/query/query_interface.do?b_query=link&a_inforkind=1&home=0');">
							<bean:message key="button.return" />
						</html:button>
					</logic:equal>
					<%
						} else {
					%>
					<logic:notEqual name="queryInterfaceForm" property="home" value="3">
						<logic:notEqual name="queryInterfaceForm" property="home"
							value="dxt">
							<%
								if (bosflag != null && bosflag.equals("ul")) {
							%>
							<html:button styleClass="mybutton" property="bc_btn1" styleId="returnButton"
								onclick="blackMaint('1','i_body');">
								<bean:message key="button.return" />
							</html:button>
							<%
								} else if (!"bi".equals(bosflag)) {
							%>
							<!-- 暂时用于解决总裁桌面配置了常用查询填出结果页面不出现返回按钮 -->
							<html:button styleClass="mybutton" property="bc_btn1" styleId="returnButton"
								onclick="blackMaint('1','il_body');">
								<bean:message key="button.return" />
							</html:button>
							<%
								}
							%>
						</logic:notEqual>
					</logic:notEqual>
					<%
						}
					%>
					<logic:equal name="queryInterfaceForm" property="home" value="dxt">
						<hrms:tipwizardbutton flag="emp" target="il_body"
							formname="queryInterfaceForm" />
					</logic:equal>
				</td>
			</tr>
		</table>
		</html:form>