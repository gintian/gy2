<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link> 
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>

<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
                 com.hrms.struts.taglib.CommonData,
			     org.apache.commons.beanutils.LazyDynaBean,
			     com.hrms.hjsj.sys.DataDictionary,com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo,
			     com.hrms.hjsj.sys.DataDictionary,
			     com.hrms.hjsj.sys.FieldItem,com.hjsj.hrms.utils.PubFunc,
			     java.util.*,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<html>
<head>			     
<%
    EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
	FieldItem item=DataDictionary.getFieldItem("a0101");
	int itemLength=20;
	if(item!=null)
	   itemLength=item.getItemlength();
	String isPrompt=employPortalForm.getIsPrompt();
	String person_type=PubFunc.getReplaceStr2(request.getParameter("person_type"));
	if(person_type==null)
	    person_type="0";
	 String aurl = (String)request.getServerName();
	 String port=request.getServerPort()+"";
	 String prl=request.getScheme();
	 String url_p=prl+"://"+aurl+":"+port;
	 String cardid = employPortalForm.getCardid();
	 
	 Calendar c = Calendar.getInstance();
   	 int hour = c.get(Calendar.HOUR_OF_DAY); 
   
     String passwordTransEncrypt = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt");
%>
<style type="text/css">
	.fontStyle{
		font-size: 12px;
	}
</style>
</head>

<script language='javascript' >
var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
var  isEnrol=0;
var idTypeValue = '<%=RecruitUtilsBo.getIdTypeValue() %>';
</script>
<body onload="registerok()" onKeyDown='pf_ChangeFocusRegister("${employPortalForm.isDefinitionActive}","${employPortalForm.cultureCodeItem}","${employPortalForm.isDefinitionCulture}","${employPortalForm.paramFlag}","${employPortalForm.blackField}","${employPortalForm.blackFieldDesc}","${employPortalForm.onlyNameDesc}","${employPortalForm.isDefineWorkExperience}","${employPortalForm.workExperienceDesc}","<%=person_type%>","<%=itemLength%>");'  >
<input type="hidden" id="hour" value="<%=hour %>"/>
<input type="hidden" id="pTransEncrypt" value="<%=passwordTransEncrypt %>"/>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					激活邮件发送中,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<div id="biaodan">


</div>
<html:form action="/hire/hireNetPortal/search_zp_position">
			<%
			
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			HashMap hm = employPortalForm.getFormHM();
			String channelName = (String)hm.get("channelName");
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			String hireChannel = employPortalForm.getHireChannel();
			ArrayList unitList=employPortalForm.getUnitList();
            	LazyDynaBean kbean=null;
            ArrayList kll=new ArrayList();
            int hi=0;
            String username=employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
            int lis=0;
            ArrayList boardlist=employPortalForm.getBoardlist();
            if(unitList!=null&&unitList.size()>0){
            	kbean=(LazyDynaBean)unitList.get(0);
            	for(int k=0;k<unitList.size();k++){
            		kbean=(LazyDynaBean)unitList.get(0);
            		kll=(ArrayList)kbean.get("list");
            		lis+=lis+kll.size()+1;
            	}
            	
            	
           		if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){
            			hi=lis*30+500;
	            		if(lis<=7){
	            			hi=7*30+500;
	            		}
            		
            	}else{
            		if(username.length()<=4){
	            		hi=lis*30+615;;
		            	if(lis<=7){
		            		hi=7*30+615;
		            	}
	            	}else{
		            	hi=lis*30+615;;
		            	if(lis<=7){
		            		hi=7*30+615;
		            	}
	            	}
	            	
            	}
            }else{
            	if(a0100==null||(a0100!=null&&a0100.trim().length()==0))
            		hi=7*30+500;
            	else{
            		hi=7*30+615;
            	}
            }
		    %>
<html:hidden name="employPortalForm" property="isDefinitionActive"/>
<input type="hidden" name="url_addr" value="<%=url_p%>"/>	
		<div class="body">   	
			 <div class="tcenter" id='tc'>
				<div class="center_bg" id='cms_pnl'>
					<div class="left">       		
                	<div class="login">
                    	<h2>&nbsp;&nbsp;&nbsp;&nbsp;用户登录</h2>
                        <div class="dl">
								<table width="197" border="0" cellspacing="0" cellpadding="0">
								  <tr>
								    <td>&nbsp;</td>
								  </tr>
								  <tr>
								    <td>&nbsp;&nbsp;<div class="input_bg"><span>邮&nbsp;&nbsp; 箱</span><input class=s_input id=loginName onkeydown="KeyDown()" name=loginName></div></td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>密&nbsp;&nbsp; 码</span><input class=s_input id=password type=password  onkeydown="KeyDown()"				                 
						             name=password autocomplete="off"></div></td>
								  </tr>
								 <tr>
								    <td>
								    	<div class="input_bg"><span>验证码</span><input class="s_input" id="validatecode" type="text" onkeydown="KeyDown()" value="" name="validatecode" /></div>
								    </td>
								  </tr>
								  
								  <tr>
								    <td align="right" style="padding:2px 0 2px 0;border-bottom:dotted 1px #c6c6c6;">
								    <img align="absMiddle" src="/servlet/vaildataCode?channel=0&codelen=4" id="vaildataCode">
								    <img align="absMiddle" src="/images/refresh.png" height="15" width="15" title="换一张" onclick="validataCodeReload()">
								    <img align="absMiddle" style="cursor:hand;" src="/images/hire/dl.gif" title="登录"  onclick='hireloginvalidate(0);'/>&nbsp;</td>
								  </tr>
								  <tr>
								    <td><span>
								    <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:T_BUTTON();'>注册</a>
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:TR_BUTTON();'>注册</a>
						                  </logic:equal>
						                <logic:equal value="1" name="employPortalForm" property="acountBeActived">
						                    |<a href="javascript:hireloginvalidate(1);">激活</a>						        
						                  </logic:equal>
											|<a href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>忘记密码</a>
						             	  <logic:equal value="true" name="employPortalForm" property="accountFlag">
											|<a href='javascript:getZpAccounts();'>忘记帐号</a>
										  </logic:equal>
						                </span>
						             </td>
								  </tr>
								</table>
	                        </div>
	                   	 </div>
                    <div class="muen">
                   		 <h2>&nbsp;&nbsp;&nbsp;&nbsp;招聘单位</h2>
         				<logic:iterate id="unit" name="employPortalForm" property="unitList" indexId="index">
                    		<logic:equal value="<%=zpUnitCode%>" name="unit" property="codeitemid">
                    		 <div class="firstDiv"><table><tr><td align="left" valign="middle"><img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a></td></tr></table></div>
                    		<ul class="col">
				                    <logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
				                    			<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a   class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:equal>
				                    			<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:notEqual>
			                    </logic:iterate>
                  			</ul>
                    		</logic:equal>
                    		<logic:notEqual value="<%=zpUnitCode%>" name="unit" property="codeitemid">
                    		 <div class="firstDiv"><table><tr><td align="left" valign="middle"><img src="/images/tree_collapse.gif" border="0" id="<bean:write name="unit" property="id_img"/>" style="cursor:hand" onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");'/><a href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font class="firstFont"><bean:write name="unit" property="codeitemdesc"/></font> </a></td></tr></table></div>
                    		 <ul class="col">
				                    <logic:iterate id="UnitSub" name="unit" property="list" indexId="indexid">
				                    			<logic:equal value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a   class="one" title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:equal>
				                    			<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub" property="codeitemid">
				                    				<li id="<bean:write name="UnitSub" property="id_r"/>"><a title='<bean:write name="UnitSub" property="altdesc"/>' href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write name="UnitSub" property="codeitemdesc"/></a></li>
				                    			</logic:notEqual>
			                    </logic:iterate>
                  			</ul>
                    		</logic:notEqual>                    		
                    	</logic:iterate>                    
                    </div>
                     <div class="promt">
                     ${employPortalForm.promptContent}
                     </div>
                </div>
                <div class="right4"  id='rg'  >
                <h2>注册用户</h2>
		              <div class="jj zw">
		                	<div class="nr">
								<TABLE cellSpacing=0 cellPadding=0 width="100%"  align=center border=0>
		                          <TBODY>		                          
		                            <tr><TD height="30px" align='left'>(加<font class=ff>*</font>号为必填项，注册成功后邮箱和姓名不可修改)</TD></tr>		                          
		                          </TBODY></TABLE>
		                        <table width="0" border="0" cellspacing="0" cellpadding="0" class="table">
		                          <TBODY>
		                          <tr>
								    <td align="right" width='35%'>&nbsp;</td>
								    <td  width='25%'>&nbsp;</td>
								    <td  width='40%'>
								    </td>
								  </tr>
		                          <TR>
		                            <td class="reg_item_title_td"> <font class="fontStyle"><bean:message key="hire.register.email"/>：</font> </TD>
		                           	<td align='left' class='nowrap'>
		                           		<div class='input_bg1 reg_input_div'>
		                           			<input  id=txtEmail name=txtEmail class="reg_input">
		                           			<label class="reg_item_must">*</label>
		                             	 </div>
		                              	
		                            </TD>
		                            <td class='register_td'>
		                           	  <label id='t1' class="reg_item_must"></label>
								    </td>
		                          </TR>

		                          <logic:notEqual name="employPortalForm" property="complexPassword" value="1"><!--不使用复杂密码  -->
		                           <TR>
		                             <td class="reg_item_title_td"> <font class="fontStyle"><bean:message key="label.login.password"/>：</font></TD>
		                           	 <td align='left' class='nowrap'>
			                           	 <div class='input_bg1 reg_input_div'>
			                           		 <input class="reg_input" id=pwd1 type=password name=pwd1 autocomplete="off">
			                           		 <label class="reg_item_must">*</label>
			                             </div>
		                          
		                            </TD>
		                            <td class='register_td'>
		                              <label id='t2' class="reg_item_must"></label>
		                            </td>
		                          </TR>
			                          <TR>
			                            <td class="reg_item_title_td"> <font class="fontStyle" ><bean:message key="hire.zp_persondb.okpassword"/>：</font></TD>
			                            <td align='left' class='nowrap'>
				                            <div class='input_bg1 reg_input_div'>
				                            	<input class="reg_input" id="pwd2" type="password"  name="pwd2" autocomplete="off">
                     				  	        <label class="reg_item_must">*</label>
				                             </div>

			                            </td>
			                             <td align='left' class='nowrap'>
			                            &nbsp; &nbsp;密码长度为<font>6-8</font>位
			                            </td>
			                           </TR>

		                            </logic:notEqual>
		                            <logic:equal name="employPortalForm" property="complexPassword" value="1"><!-- 使用复杂密码 -->
			                           <TR>
			                             <td class="reg_item_title_td"> <font class="fontStyle"><bean:message key="label.login.password"/>：</font></TD>
			                           	 <td align='left' class='nowrap'>
				                           	 <div class='input_bg1 reg_input_div'>
				                           		 <input class="reg_input" id="pwd1" type="password" name="pwd1" autocomplete="off">
				                           		 <label class="reg_item_must">*</label>
				                             </div>
			                            
			                            </TD>
			                            <td class='register_td'>
			                              <label id='t2' class="reg_item_must"></label>
			                            </td>
			                          </TR>
	              		                <TR>
	              		               	 	<td class="reg_item_title_td"> <font class="fontStyle"><bean:message key="hire.zp_persondb.okpassword"/>：</font></TD>
			                            	<td align='left' class='nowrap'>
				                            <div class='input_bg1 reg_input_div'>
				                            	<input class="reg_input" id='pwd2'  type='password'  name=pwd2 autocomplete="off">
				                            	<label class="reg_item_must">*</label>
				                             </div>
				                            
				                             </td>
				                             <td class='register_td'>
			                             	&nbsp;&nbsp;<bean:write name="employPortalForm" property="passwordMinLength"/>-<bean:write name="employPortalForm" property="passwordMaxLength"/>位的字母、数字、特殊字符组合
			                            	</td>
			                           </TR>
		
		                            </logic:equal>
		                             <logic:notEqual name="employPortalForm" property="onlyName" value="A0101">
			                            <TR>
				                             <td class="reg_item_title_td"> <font class="fontStyle"><bean:message key="hire.user.name"/>：</font></TD>
				                           	 <td align='left' class='nowrap'>
				                           	 	<div class='input_bg1 reg_input_div'>
				                           		 <input class="reg_input" id=txtName name=txtName maxLength="<%=itemLength%>" >
				                           		 <label class="reg_item_must">*</label>
				                              	</div>
				                             	
				                              	
				                              </TD>
				                              <td class='register_td'>
				                             	<label id='t3' class="reg_item_must"></label>
				                              </td>
				                        </TR>
			                        </logic:notEqual>
		                             <logic:notEqual value="3" name="employPortalForm" property="paramFlag">
		                              	<logic:notEqual value="-1" name="employPortalForm" property="blackField">		                              
			                              <TR>
			                                 <td class="reg_item_title_td"><font class="fontStyle"><bean:write name="employPortalForm" property="blackFieldDesc"/>：</font></TD>
			                               	<td align='left' class='nowrap'>
				                               	<div class='input_bg1 reg_input_div'>
				                               		<input class="reg_input" id=blackValue name=blackFieldValue maxLength="${employPortalForm.blackFieldSize}">
				                               		<label class="reg_item_must">*</label>
				                                </div>
			                                	
			                                </TD>
			                                <td class='register_td'>
				                              <label id='t5' class="reg_item_must"></label>
				                            </td>
			                              </TR>
		                              	</logic:notEqual>
		                             </logic:notEqual>	
		                
		                  			<%ArrayList list2 = employPortalForm.getId_type_List(); 
		                  			  	String dataName  = "";
		                  			  	String codeId= employPortalForm.getCodeId();
		                              	if(list2!=null && list2.size()>0){
		                              		for(int i = 0 ; i < list2.size() ; i++) {
		                              			 CommonData data = (CommonData)list2.get(i);
		                              			 String dataValue  =  data.getDataValue();
		                              			
		                              			 if(dataValue.equalsIgnoreCase(RecruitUtilsBo.getIdTypeValue()))
		                              				 dataName  =  data.getDataName();
		                              		}
		                              %>             
		                             <TR>
										<td class="reg_item_title_td"><font class="fontStyle">${employPortalForm.id_type_desc}：</font></td>
										<TD class='tdValue' >
										<div class='input_bg1 reg_input_div' style='width:auto;'>
											<input type='hidden' name='id_type' id='id_type' value="<%=RecruitUtilsBo.getIdTypeValue() %>"  />
											<input  id='idType_view' type='text' name='idType_view' style="width:167px;" value="<%=dataName%>" />
											<img src="/images/hire/xia.gif"  plugin="codeselector" isHideTip = "true" codesetid="<%=codeId%>" inputname='idType_view' valuename='id_type' multiple='false' onlySelectCodeset='true' align="absmiddle"/>	 	                             
									 	</div>
										</TD>
										<td><FONT color='#ff0000'>*&nbsp;<label id='candidatehint2' class="reg_item_must"></label></td>
										</TR>
		                             	<%} %>
		                             	                              
		                             <html:hidden property="paramFlag" name="employPortalForm"/>
		                             <logic:notEqual value="1" name="employPortalForm" property="paramFlag">
		                               <TR>
		                                 <td class="reg_item_title_td"> <font class="fontStyle"><bean:write name="employPortalForm" property="onlyNameDesc"/>：</font></TD>
		                              	 <td align='left' class='nowrap'>
			                              	 <div class='input_bg1 reg_input_div'>
			                              	 <%--	将证件号设置为唯一性指标	--%>
			                              		 <input  id="onlyv" name="onlyValue" maxLength="${employPortalForm.onlySize}">
			                              		 <label class="reg_item_must">*</label>
			                               	 </div>
		                                 </TD>
		                               	 <td class='register_td'>
			                             	<label id='t7' class="reg_item_must"></label>
			                             </td>
		                              </TR>
		                              </logic:notEqual>
		                              <html:hidden name="employPortalForm" property="isDefinitionActive"/>
		                              <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
			                              <TR>
				                           	 <td class="reg_item_title_td"> <font class="fontStyle">限制浏览单位：</font></TD>
				                             <td align='left' class='nowrap'>
					                             <div class='input_bg1 reg_input_div'>
					                            	 <input type="text" style="margin-bottom:19px" class="textbox" name="belongUnithName_view" readOnly>		                     					                             
				                            			<INPUT type="hidden"  id="belongUnit" name="belongUnit" value="">
				                            			<INPUT type="hidden"  id="belongUnit_value" name="belongUnithName_value" value="">
				                             	        <label class="reg_item_must">*</label> 
				                              	 </FONT>
				                              	 <img style="margin-left:-23px" class="img-middle" src="/images/hire/xia.gif" plugin="codeselector" codesetid="UN" ctrltype="0" inputname="belongUnithName_view" afterfunc="dealRes()"/>
				                               	 </div>
			                              	 </TD>
			                              	 <td class='register_td'>
				                             	<label id='t4' class="reg_item_must"></label>
				                           	 </td>
			                               </TR>
		                               </logic:equal>
		                               <logic:equal value="1" name="employPortalForm" property="isDefinitionCulture">		                              
			                               <TR>
			                           		  <td class="reg_item_title_td"> <font class="fontStyle"><bean:message key="hire.culture.type"/>：</font></TD>
			                           			 <TD class='tdValue' >
			                            		 	<hrms:optioncollection name="employPortalForm" property="cultureList" collection="list" />
													 	<html:select  name="employPortalForm" property="hiddenCode" size="1" >
							            				 <html:options collection="list" property="dataValue" labelProperty="dataName"/>
							        					</html:select>		                             
			                           				    <label class="reg_item_must">*</label>
			                            	  	</TD>
			                            	  <td>
			                            	  &nbsp;
			                            	  </td>
			                              </TR>
		                              </logic:equal>
		                              <%ArrayList list = employPortalForm.getCandidate_status_List(); 
		                              	if(list!=null && list.size()>0){
		                              %>
										<TR>
										<td class="reg_item_title_td"><font class="fontStyle">${employPortalForm.candidate_status_desc}：</font></td>
										<TD class='tdValue' >
										<div class='input_bg1 reg_input_div' style='width:auto;'>
											<input type='hidden' name='candidate_status' id='candidate_status'  />
											<input  id='candidateStatus_view' type='text' name='candidateStatus_view' style="width:167px;" />
											<img src="/images/hire/xia.gif"  plugin="codeselector" isHideTip = "true" codesetid='35' inputname='candidateStatus_view' valuename='candidate_status' multiple='false' onlySelectCodeset='true' align="absmiddle"/>	 	                             
									 	</div>
										</TD>
										<td><FONT color='#ff0000'>*&nbsp;<label id='candidatehint' class="reg_item_must"></label></td>
										</TR>
										<%} %>
		                              <html:hidden name="employPortalForm" property="isDefineWorkExperience"/>
		                              <logic:equal value="1" name="employPortalForm" property="isDefineWorkExperience">
			                               <TR>
					                            <TD class="tdTitle"> <font class="fontStyle">${employPortalForm.workExperienceDesc}：</font></TD>
					                            <TD  class='nowrap'>
						                           <logic:iterate id="element" property="workExperienceCodeList" name="employPortalForm" indexId="index">
						                           		<input type="radio" name="workExperience" value="<bean:write name="element" property="codeitemid"/>"/><bean:write name="element" property="codeitemdesc"/>		                           
						                    		</logic:iterate>(应届毕业生请选此项)<FONT color=#ff0000>*</FONT>	                             		                             		                              		
			                              		</TD>
					                             <td class='register_td'>
						                             
						                            <label id='t6' class='reg_item_must'></label>
					                             </td>
			                              	</TR>
		                              </logic:equal>  
		                              <!-- 
		                               <TR>
		                            <TD class=tdTitle>
		                           <input type="checkbox" name="remenber" value="1" id="remenberme"/><font class='FontStyle'>记住我</font>&nbsp;&nbsp;<a href='javascript:getPasswordZP("<%=dbName%>","username","userpassword");'>忘记密码？</a></TD><td class=tdTitle>&nbsp;</td>
		                            </TR>
		                            --> 
		                            <tr>
		                            <td>
		                            &nbsp; &nbsp; &nbsp;
		                            </td></tr>
		                          </TBODY>
		                        </TABLE>	
		                        <TABLE cellSpacing=0 cellPadding=0 width="100%"  align=center border=0>
		                          <TBODY>		                          
		                            <tr>
			                            <TD style="TEXT-ALIGN:center;background-color:#FFFFFF;" colspan="3" >
			                            <br>
			                            	<IMG  onclick='subRegister("${employPortalForm.isDefinitionActive}","${employPortalForm.cultureCodeItem}","${employPortalForm.isDefinitionCulture}","${employPortalForm.paramFlag}","${employPortalForm.blackField}","${employPortalForm.blackFieldDesc}","${employPortalForm.onlyNameDesc}","${employPortalForm.isDefineWorkExperience}",
			                            	"${employPortalForm.workExperienceDesc}","<%=person_type%>","<%=itemLength%>","${employPortalForm.complexPassword}","${employPortalForm.passwordMinLength}","${employPortalForm.passwordMaxLength}","<%=(list!=null && list.size()>0) %>","${employPortalForm.candidate_status_desc}","<%=(list2!=null && list2.size()>0) %>","${employPortalForm.id_type_desc}");' src="/images/hire/sub.gif" border=0 style="cursor:hand"/>		                        
			                            </TD>
		                            </tr>		                          
		                          </TBODY></TABLE>	                       
							</div>
						</div>			
               		</div>
               		<div class='footer' style='height:0px;'> &nbsp;&nbsp;</div>
               	 </div>               	 	
               </div>
            </div>
</html:form>
<script type="text/javascript">
function registerok(){
<%if(request.getParameter("active")!=null&&request.getParameter("active").equals("1")){%>
  alert("帐号注册成功，已将激活帐号邮件发送到注册邮箱，请到注册邮箱中激活帐号(1小时内有效)！");
  document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_register=register";
  document.employPortalForm.submit();
  <%}%>
}
function dealRes(){
	document.getElementById("belongUnit").value = document.getElementById("belongUnit_value").value;
}
</script>
</body>
</html>
