<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			     com.hrms.hjsj.sys.DataDictionary,
			     com.hrms.hjsj.sys.FieldItem,com.hjsj.hrms.utils.PubFunc,
			     java.util.*,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<html>
<head>			     
<%
	FieldItem item=DataDictionary.getFieldItem("a0101");
	int itemLength=item.getItemlength();
	
	Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY); 
    
    String passwordTransEncrypt = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt");
%>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript">
var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
</script>
</head>
<body onKeyDown="return pf_ChangeFocusTHREE();" >
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
			EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
			HashMap hm = employPortalForm.getFormHM();
			String channelName = (String)hm.get("channelName");
			String isQueryCondition=employPortalForm.getIsQueryCondition();
			if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
				employPortalForm.setA0100("");
			}
			String a0100=employPortalForm.getA0100();
			String dbName=employPortalForm.getDbName();
			String zpUnitCode=employPortalForm.getZpUnitCode();
			String hireChannel=employPortalForm.getHireChannel();
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
				<div class="body" id='bd'>   	
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
								    <td>&nbsp;&nbsp;<div class="input_bg"><span>邮&nbsp;&nbsp; 箱</span><input class=s_input id=loginName onkeydown="KeyDown()"  name=loginName></div></td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>密&nbsp;&nbsp; 码</span><input class=s_input id=password type=password autocomplete="off" onkeydown="KeyDown()"				                 
						             name=password></div></td>
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
                </div>               
                <div class="right4"  id='rg'  >
                <h2>注册用户</h2>
               	  <div class="jj zw">
               	 	 <h2 style='width:679px;'><span>&nbsp;&nbsp;注册协议</span></h2>
		               	  <div class="nr">
		               	    <table width='99%' align="center"  border=0 cellSpacing=0 cellPadding=0 class="table">
					   			<tr>
									<td width=99%  class=''>	<br>							
									    <div style="overflow-y:auto;overflow-x:hidden;height:350px;width:677px;line-height:25px;">						
										${employPortalForm.licenseAgreement}
										</div>
									</td>
								 </tr>
							</table>
							<table>
								<TR>
			                        <TD colspan=2 align='center' class='hire_license_button' id='js'>
			                          	<img src='/images/hire/acceptth.gif' class='hire_license_image' id="js1" border='0' />
			                           		&nbsp;&nbsp;
			                           	<img src='/images/hire/bujsh.gif' border='0' class='hire_license_image' id="js2"/>			                          	
									</td>
								</tr>
							</table>
						</div>
					</div>
               	</div>
                <div class='footer' style='height:0px;'> &nbsp;&nbsp;</div>
            </div>
          </div>
      </div>
</html:form>
<script type="text/javascript">
window.setTimeout('changeSRC()',5000);   
</script>
</body>
</html>