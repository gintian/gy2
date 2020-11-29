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
	String count =request.getParameter("count");
	count = count==null? "":count;
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
     var request=new Request({method:'post',onSuccess:returninfo,functionId:'11080204095'},hashvo);
     
   }
   function returninfo(outparamters)
   {
      var types=outparamters.getValue("types");          
      if(types=="ok")
      {
        alert("操作成功!");
        if(getBrowseVersion()){
        	window.returnValue="ok";
       		window.close();
       }else{
       		parent.opener.openReturn("ok",'<%=count%>');
       		top.close();
       }
      }else
      {
        alert("操作失败");
      }
   }
  function customUpItem(){
      var vos= document.getElementsByName("order_fields")[0];
      upItems(vos);
  }
  function customDownItem(){
      var vos= document.getElementsByName("order_fields")[0];
      downItems(vos);
  }
</script>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.ListTable {
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-20);
}
</style>
<%}else{ %>
<style>
.ListTable {
	width:expression(document.body.clientWidth-10);
	height:expression(document.body.clientHeight-20);
}
</style>
<%} %>
<html:form action="/general/static/commonstatic/editstatic/history">
	<table width="390" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="left" class="TableRow" nowrap>
					<bean:message key="kq.item.change" />
				</td>
			</tr>
		</thead>
		<tr>
			<td width="390" align="center" class="RecordRow" nowrap>
				<table border="0" cellspacing="0" align="center"
		cellpadding="0">
					<tr>
						<td align="center" width="90%">
							<table align="center" width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0">
								<tr>
									<td align="left">
										统计项目&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center">
										<html:select name="staticFieldForm" property="order_fields"
											multiple="multiple" size="10"
											style="height:290px;width:100%;font-size:9pt">
											<html:optionsCollection property="orderlist"
												value="dataValue" label="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="4%" align="center">
							<table align="center" width="100%" border="0" cellspacing="0" align="center" id="right_table"
			cellpadding="0">
                                <%--wangbs 按钮的点击事件直接调的validate.js的upItems方法，参数$('order_fields')在非兼容模式下无效--%>
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_up"
											onclick="customUpItem();">
											<bean:message key="button.previous" />
										</html:button>
									</td>
								</tr>
								<tr>
									<td height="28px">
									</td>
								</tr>
								<tr>
									<td align="center">
										<html:button styleClass="mybutton" property="b_down"
											onclick="customDownItem();">
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
					onclick="top.close();">
			</td>
		</tr>
	</table>
</html:form>
<script>
    if(getBrowseVersion()==10){ //非IE浏览器兼容性   wangb 20180127
         //统计项排序页面样式修改  wangbs 2019年3月6日18:12:03
         var outForm = document.getElementsByTagName("form")[0];
         var outTable3 = outForm.getElementsByTagName("table")[3];
         outTable3.style.marginLeft = "5px";
    }
    if(!getBrowseVersion() || getBrowseVersion() == 10){
    	var right_table = document.getElementById('right_table');
    	right_table.style.marginLeft='6px';
    }
</script>