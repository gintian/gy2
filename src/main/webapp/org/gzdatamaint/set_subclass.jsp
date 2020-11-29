<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.org.gzdatamaint.GzDataMaintForm,java.util.*"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
	GzDataMaintForm gzDataMaintForm=(GzDataMaintForm)session.getAttribute("gzDataMaintForm"); 
	String returnflag=gzDataMaintForm.getReturnflag();
	String temp=request.getParameter("tempflag");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript"><!--
   function savefield()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");       
     if(vos.length!=0)
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		alert(SAME_SUBSET_RE_SELECT);
          		return false;
          	}
          }
        }       
     }
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;          
      code_fields[i]=valueS;
    }
    var gzflag = "${gzDataMaintForm.gzflag}";       
    hashvo.setValue("subclass_value",code_fields); 
    hashvo.setValue("gzflag",gzflag); 
    var request=new Request({method:'post',onSuccess:showSelect,functionId:'1602010234'},hashvo);
   }	
   function showSelect(outparamters)
   { 
   	 returnValue="aaaa";
	 window.close(); 
   }
   function savefieldOk()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");       
     if(vos.length!=0)
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		alert(SAME_SUBSET_RE_SELECT);
          		return false;
          	}
          }
        }       
     }
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;          
      code_fields[i]=valueS;
    }       
    var gzflag = "${gzDataMaintForm.gzflag}";       
    hashvo.setValue("subclass_value",code_fields); 
    hashvo.setValue("gzflag",gzflag);  
    var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'1602010234'},hashvo);
   }
    function showSelectOk(outparamters)
   { 
		alert(RELATED_SUBSET_SET_OK); 
   }
   function closeOk()
   { 
		returnValue="ssss";
		window.close();
   }
   function returnFirst(){
   		var gzflag = "${gzDataMaintForm.gzflag}";
   		if(gzflag=='2')
   			document.location= "/general/tipwizard/tipwizard.do?br_compensation=link";
   		else
   			document.location= "/general/tipwizard/tipwizard.do?br_Insurance=link";
	}	  
--></script>
<html:form action="/org/gzdatamaint/gzdatamaint">
<%if("1".equals(temp)){ %>
	<table width="530px;" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-top:0px;">
<%}else{ %>
	<table width="700" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-top:50px;">
<%} %>
		<tr>
			<td align="center" class="TableRow" nowrap colspan="3">
				<bean:message key="system.param.sysinfosort.selsubset" />
			</td>
		</tr>
		<tr>
			<td width="100%" align="center" nowrap>
				<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" >
					<tr>
						<td align="center" width="46%">
							<table align="center" width="100%">
								<tr>
									<td align="left" height="32" style="padding-left: 5px;">
										<bean:message key="system.param.sysinfosort.bsubset" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td align="center" style="padding-left: 5px;padding-bottom: 5px;">
										<hrms:optioncollection name="gzDataMaintForm"
											property="subclasslist" collection="list" />
										<html:select property="left_fields" size="10" multiple="true"
											style="height:230px;width:97%;font-size:9pt"
											ondblclick="additem('left_fields','right_fields');removeitem('left_fields');">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>

								</tr>

							</table>
						</td>

						<td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_addfield"
								onclick="additem('left_fields','right_fields');removeitem('left_fields');">
								<bean:message key="button.setfield.addfield" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_delfield"
								onclick="additem('right_fields','left_fields');removeitem('right_fields');">
								<bean:message key="button.setfield.delfield" />
							</html:button>
						</td>


						<td width="46%" align="center">


							<table width="100%">
								<tr>
									<td width="100%" align="left" height="32">
										<bean:message key="system.param.sysinfosort.ysubset" />
										&nbsp;&nbsp;
									</td>
								</tr>
								<tr>
									<td width="100%" align="left" style="padding-bottom: 5px;">
										<hrms:optioncollection name="gzDataMaintForm"
											property="selectsubclass" collection="selectedlist" />
										<html:select property="right_fields" size="10" multiple="true"
											style="height:230px;width:97%;font-size:9pt" styleId="right"
											ondblclick="additem('right_fields','left_fields');removeitem('right_fields');">
											<html:options collection="selectedlist" property="dataValue"
												labelProperty="dataName" />
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="center" style="padding-right: 5px;">
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
			<td align="center" class="RecordRow" style="padding-top: 2px;padding-bottom: 1px;height: 35px;" nowrap colspan="3">
				<logic:equal name="gzDataMaintForm" property="tagname" value="0">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick=" savefield();">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.close"/>' class="mybutton"
						onclick="closeOk();">
				</logic:equal>
				<logic:equal name="gzDataMaintForm" property="tagname" value="1">
					<input type="button" name="btnreturn"
						value='<bean:message key="button.ok"/>' class="mybutton"
						onclick=" savefieldOk();">
						<logic:equal value="2" name="gzDataMaintForm" property='gzflag'>
						 <hrms:tipwizardbutton flag="compensation" target="il_body" formname="gzDataMaintForm"/> 
						 </logic:equal>
						 <logic:notEqual value="2" name="gzDataMaintForm" property='gzflag'>
						 <hrms:tipwizardbutton flag="insurance" target="il_body" formname="gzDataMaintForm"/> 
						 </logic:notEqual>
						 
				</logic:equal>
			</td>
		</tr>
	</table>
</html:form>
