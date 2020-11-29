<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page
	import="com.hrms.struts.valueobject.UserView,com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	//在标题栏显示当前用户和日期 2004-5-10 
	String css_url="/css/css1.css";
	String bosflag="";
	String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	      //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
		  bosflag=userView.getBosflag(); 
		  /*xuj added at 2014-4-18 for hcm themes*/
	    themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
	String manager = userView.getManagePrivCodeValue();
	if (manager.length() > 0)
	{
		manager += "~" + AdminCode.getCodeName(userView.getManagePrivCode(), userView.getManagePrivCodeValue());
	}
	String plan_b0110 = request.getParameter("b0110");
	String b0110_name = "";
	/*	if(plan_b0110!=null)
	 {
	 if(!plan_b0110.equalsIgnoreCase("hjsj"))
	 {
	 b0110_name=AdminCode.getCodeName("UN", plan_b0110);
	 manager=plan_b0110+"~"+b0110_name;
	 }else
	 {
	 b0110_name=AdminCode.getCodeName("UN", "01");	
	 manager="01~"+b0110_name;
	 }	
	 }*/
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript">

	var infor,managerstr,dbpre_arr;
	infor="${handworkSelectForm.infor}";
	managerstr="<%=manager%>";
/*	managerstr="${handworkSelectForm.managerstr}";
       由于树控件不够完善，所以将managerstr设为空值
*/
	dbpre_arr="${handworkSelectForm.dbpre_arr}";

	function showFieldList(outparamters)
	{
		var fieldlist=outparamters.getValue("fieldlist");	
		AjaxBind.bind(handworkSelectForm.left_fields,fieldlist);
	}

	function searchFieldList()
	{		
	   var hashvo=new ParameterSet();	
	   var codeItemID=document.getElementsByName("codeitem.value");
	   var obj=codeItemID[0];
	   var In_paramters="dbpre_arr="+dbpre_arr;  	 	  
	   hashvo.setValue("codeid",infor);
	   hashvo.setValue("codeItemID",obj.value);
   	   var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'0202001014'},hashvo);
	}

	function savecode()
   	{		
   		var objlist=new Array(); 	 	
   	 	for(var i=0;i<handworkSelectForm.right_fields.options.length;i++)
   	 	{
   	 		objlist.push(handworkSelectForm.right_fields.options[i].value); 			
   	 	}	   	 	
   	   	returnValue=objlist;
	        window.close();	
    	  	
   	}
   	
   	
   	function additem2(sourcebox_id,targetbox_id)
	{
	  var left_vo,right_vo,vos,i;
	  vos= document.getElementsByName(sourcebox_id);
	
	  if(vos==null)
	  	return false;
	  left_vo=vos[0];
	  vos= document.getElementsByName(targetbox_id);  
	  if(vos==null)
	  	return false;
	  right_vo=vos[0];
	  for(i=0;i<left_vo.options.length;i++)
	  {
	    if(left_vo.options[i].selected)
	    {
	    	var isExist=0;
	    	for(var j=0;j<right_vo.options.length;j++)
	    	{
	    		if(right_vo.options[j].value==left_vo.options[i].value)
	    			isExist=1;
	    	}
	    	if(isExist==0)
	    	{
		        var no = new Option();
		    	no.value=left_vo.options[i].value;
		    	no.text=left_vo.options[i].text;
		    	right_vo.options[right_vo.options.length]=no;
		    }
	    }
	  }
   	}
   	
   	
</script>


<html:form action="/general/query/handworkSelect">

	<table width="90%" align="center" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<td height='35px'>

			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable">
					<tr>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="lable.performance.handworkselect" />
						</td>
					</tr>
					<tr>
						<td width="100%" align="center" class="RecordRow" nowrap>
							<table>
								<tr>
									<td align="center" width="46%">
										<table align="center" width="100%">
											<tr>
												<td align="left">
													<bean:message key="lable.performance.preparePerMainBody" />
													<bean:message key="lable.performance.object" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td align="center">

													<input type="hidden" name="posparentcode" value="01">
													<input type="hidden" name="codeitem.value" value="01">
													<input type="text" name='codeitem.viewvalue' readonly
														onChange="searchFieldList()" class="text6" size="25" />
													&nbsp;
													<script language="JavaScript">                   
                     		if(infor=="1")
                     		{
                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("@K","codeitem.viewvalue","'+managerstr+'","s");\'   /> ');
                     		}
                     		else if(infor=="2")
                     		{
                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("UN","codeitem.viewvalue","'+managerstr+'","s");\'   /> ');
                     		}
                     		else if(infor=="3")
                     		{
                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("UM","codeitem.viewvalue","'+managerstr+'","s");\'   /> ');
                     		} 
                     		else if(infor=="4")
                     		{
                     			document.write('<img  src="/images/code.gif" onclick=\'javascript:openInputCodeDialogOrg_handwork("UM","codeitem.viewvalue","'+managerstr+'","s");\'   /> ');
                     		}                     		
                        </script>

												</td>
											</tr>
											<tr>
												<td align="center">
													<select name="left_fields" multiple="multiple"
														ondblclick="additem2('left_fields','right_fields');"
														style="height:209px;width:100%;font-size:9pt">
													</select>
												</td>
											</tr>
										</table>
									</td>

									<td width="8%" align="center">
										<html:button styleClass="mybutton" property="b_addfield"
											onclick="additem2('left_fields','right_fields');">
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

													<bean:message key="lable.performance.selectedPerMainBody" />
													<bean:message key="lable.performance.object" />
													&nbsp;&nbsp;
												</td>
											</tr>
											<tr>
												<td width="100%" align="left">

													<select name="right_fields" multiple="multiple" size="10"
														ondblclick="removeitem('right_fields');"
														style="height:230px;width:100%;font-size:9pt">
													</select>

												</td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="center" class="RecordRow" 
							style="height:35px">
							<html:button styleClass="mybutton" property="b_save"
								onclick="savecode()">
								<bean:message key="button.ok" />
							</html:button>

					<input type="button"
							value="<bean:message key='button.cancel'/>"
							onclick="window.close();" Class="mybutton" />
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

</html:form>
