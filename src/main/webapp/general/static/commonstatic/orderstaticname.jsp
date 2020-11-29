<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	//非IE浏览器获取标识  wangb 20180126
    String count = request.getParameter("count")==null? "":request.getParameter("count");
%>
<script language="javascript">
  function saveCode()
  {
     var hashvo=new ParameterSet();          
     var vos= document.getElementsByName("order_fields"); 
     if(vos==null || vos[0].length==0)
     {
  	   return; 
     }
     var codevo=vos[0];      
     var code_fields=new Array();        
     for(var i=0;i<codevo.options.length;i++)
     {
          var valueS=codevo.options[i].value;          
          code_fields[i]=valueS;
     }       
    
     hashvo.setValue("order_fields",code_fields); 
     hashvo.setValue("statid",${staticFieldForm.statid});    
            	
     var request=new Request({method:'post',onSuccess:returninfo,functionId:'11080204058'},hashvo);
     
   }
   function returninfo(outparamters)
   {
      var types=outparamters.getValue("types");          
      if(types=="ok")
      {
        alert("操作成功!");
        if(navigator.appName.indexOf("Microsoft")!= -1){
        	//window.returnValue="ok";
       		//window.close();
       		openReturn("ok",'<%=count%>');
       		openClose();
       }else{
       		//top.returnValue="ok";
       		//top.close();
       		openReturn("ok",'<%=count%>');
       		openClose();
       }
      }else
      {
        alert("操作失败");
      }
   }  
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10);
}
</style>
<%}else{ %>
<style>
.ListTable{
	width:expression(document.body.clientWidth-10);
	margin-top:10px;
}
</style>
<%} %>
<html:form action="/general/static/commonstatic/editstatic">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable" style="border-collapse: separate;"> <!-- modify by xiaoyun 2014-8-12 去掉下边的黑线 -->
		<thead>
			<tr>
				<td align="left" class="TableRow" style="border-bottom: none;" nowrap>
					<bean:message key="kq.item.change" />
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRow" style="border-bottom: none;" nowrap>
				<table width="100%">
					<tr>
						<td align="center" width="90%">
							<table align="center" width="100%">
								<tr>
									<td align="left">
										常用统计项
									</td>
								</tr>
								<tr>
									<td align="center">
										<html:select name="staticFieldForm" property="order_fields"
											multiple="multiple" size="10"
											style="height:230px;width:100%;font-size:9pt">
											<html:optionsCollection property="orderlist"
												value="dataValue" label="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="4%" align="center">
							<table border="0" cellspacing="0"  align="center" cellpadding="0">
		               			<tr>
		               				<td align="center">
							            <html:button styleClass="mybutton" property="b_up"
											onclick="upItem($('order_fields'));">
											<bean:message key="button.previous" />
										</html:button>
						            </td>
		               			</tr>
		               			<tr>
		               				<td height="30px"></td>
		               			</tr>
		               			<tr>
		               				<td align="center">
							           <html:button styleClass="mybutton" property="b_down"
											onclick="downItem($('order_fields'));">
											<bean:message key="button.next" />
										</html:button>	
		               				</td>
		               			</tr>
		               		</table>  
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap style="height: 35px">
				<input type="button" name="btnreturn" value='确定' class="mybutton"
					onclick=" saveCode();">
				<input type="button" name="tdf"
					value="<bean:message key="button.close"/>" class="mybutton"
					onclick="openClose();">
			</td>
		</tr>
	</table>
<script>
//兼容非IE浏览器
//关闭弹窗方法  wangb 20180126
function openClose(){
	if(getBrowseVersion()){//update by xiegh 用showdialog打开的窗口是没有top的  现在已经做了浏览器兼容  top.close()不需要保留
		top.close();
	}else{
		parent.window.close();
	}
}
//回调父页面方法 等同windowShowDialog 弹窗返回值    wangb 20180126
function openReturn(return_vo,type){
	if(getBrowseVersion()){
		parent.window.returnValue=return_vo;
	}else{
		parent.opener.openReturn(return_vo,'<%=count%>');
	}
}
</script>
</html:form>