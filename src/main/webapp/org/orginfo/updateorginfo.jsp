<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.frame.dao.RecordVo,java.util.Map"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
//    int ver_flag=userView.getVersion_flag();
//    if(ver_flag==0)
//        version=false;
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	RecordVo vo = new RecordVo("organization");
		  Map lenmap = vo.getAttrLens();
		  String codeitemdesclen = (String)lenmap.get("codeitemdesc");
%>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<script type="text/javascript">
<!--
	function checkdate(){
		<%
			if(version){
         %>
         if(document.returnValue==true){
		var start_date=document.getElementsByName("start_date")[0].value;
		var end_date=document.getElementsByName("end_date")[0].value;
		start_date=new Date(Date.parse(start_date.replace(/-/g, "/")));
		end_date=new Date(Date.parse(end_date.replace(/-/g, "/")));
		var cur_date=new Date();
		var cur_date=new Date(cur_date.getFullYear(),cur_date.getMonth(),cur_date.getDate());
		if(cur_date<start_date){
			alert("有效日期起不能大于当前日期!");
			document.returnValue=false;
		}
		if(start_date>end_date){
			alert("有效日期起不能大于有效日期止!");
			document.returnValue=false;
		}
		return true;
		}
		<%}else{%>
			 if(document.returnValue==true)
			document.returnValue=true;
		<%}%>
	}
	function back()
{
	orgInformationForm.action = "/org/orginfo/searchorglist.do?b_return=link";
	orgInformationForm.submit();
}
function checkcorcode(){
	if(document.returnValue==true){
	var msgfillable="单位代码";
       	<logic:equal value="UM" name="orgInformationForm" property="codesetid">
       		msgfillable="部门代码";
        </logic:equal>
        <logic:equal value="@K" name="orgInformationForm" property="codesetid">
        	msgfillable="岗位代码";
        </logic:equal>
        <logic:equal value="@K" name="orgInformationForm" property="codesetid">
        	<logic:equal value="1" name="orgInformationForm" property="posfillable">
       			validate1('R','corcode',msgfillable);
      		</logic:equal>
      	</logic:equal>
      	<logic:notEqual value="@K" name="orgInformationForm" property="codesetid">
        	<logic:equal value="1" name="orgInformationForm" property="unitfillable">
       			validate1('R','corcode',msgfillable);
      		</logic:equal>
      	</logic:notEqual>
      	}
}
//-->
</script>
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchorglist"> 
<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="30">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.org.maintenance"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   -->
       		<td align="left" colspan="1" class="TableRow">&nbsp;<bean:message key="label.org.maintenance"/>&nbsp;</td>           	             	                  	                  	      
  </tr>  
   <tr>
      <td colspan="4" class="framestyle3" width="100%" align="center">
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"> 
             <tr  align="right" class="list3">
               <td  class="RecordRowHr" width="38%" >
                  &nbsp;<bean:message key="label.org.curcode"/>
                </td>
               <td align="left"  class="RecordRowHr">
                 <html:text   name="orgInformationForm" property="codeitemid"  readonly="true" styleClass="textColorRead" maxlength="${orgInformationForm.len}"/>
               </td>
             </tr> 
             <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                   <logic:equal value="UN" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.un"/><bean:message key="conlumn.codeitemdesc.caption"/>
                   </logic:equal>
                   <logic:equal value="UM" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.um"/><bean:message key="conlumn.codeitemdesc.caption"/>
                   </logic:equal>
                   <logic:equal value="@K" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.kk"/>
                   </logic:equal>
                </td>
               <td align="left" class="RecordRowHr">
                  <html:text   name="orgInformationForm" property="codeitemdesc"  styleClass="textColorWrite" maxlength="<%=codeitemdesclen %>"/>
               	  <font color="red">*</font>
               </td>
             </tr> 
             <%
             	//版本号大于等于50才显示这些功能
             	//xuj 2009-10-30 在organazition（vorganization虚拟机构）表中增加“有效日期起”、“有效日期止”两个字段
             	if(version){
              %>
             <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                   &nbsp;<bean:message key="conlumn.codeitemid.start_date"/>
                </td>
               <td align="left"  class="RecordRowHr"> 
               <input type="text"  name="start_date" value="${orgInformationForm.start_date }" maxlength="50" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='${orgInformationForm.start_date }'; }"/>
               </td>
             </tr>
             <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                   &nbsp;<bean:message key="conlumn.codeitemid.end_date"/>
                </td>
               <td align="left"  class="RecordRowHr"> 
                  <input type="text" name="end_date" value="${orgInformationForm.end_date }" maxlength="50" class="textColorWrite" style="BACKGROUND-COLOR:#F8F8F8;width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='${orgInformationForm.end_date }'; }"/>
               </td>
             </tr>
             <%} %>
              <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                   <logic:equal value="UN" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.un"/><bean:message key="kh.field.code"/>
                   </logic:equal>
                   <logic:equal value="UM" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.um"/><bean:message key="kh.field.code"/>
                   </logic:equal>
                   <logic:equal value="@K" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.kk"/><bean:message key="kh.field.code"/>
                   </logic:equal>
                </td>
               <td align="left"  class="RecordRowHr"> 
                  <html:text   name="orgInformationForm" property="corcode"  styleClass="textColorWrite" maxlength="50"/>
               		<logic:equal value="@K" name="orgInformationForm" property="codesetid">
               			<logic:equal value="1" name="orgInformationForm" property="posfillable">
                   			<font color="red">*</font>
                   		</logic:equal>
                   </logic:equal>
                   <logic:notEqual value="@K" name="orgInformationForm" property="codesetid">
               			<logic:equal value="1" name="orgInformationForm" property="unitfillable">
                   			<font color="red">*</font>
                   		</logic:equal>
                   </logic:notEqual>
               </td>
             </tr> 
             
          </table>
       </td>
   </tr>   
  </table>
  <table width="600" align="center"> 
  	<tr align="center">
                <td>
                   <hrms:submit styleClass="mybutton"  property="b_update" onclick="document.orgInformationForm.target='_self';validate1('R','codeitemdesc','机构代码名称');checkdate();checkcorcode();return document.returnValue;">
                     	 <bean:message key="button.save"/></hrms:submit>
	               <input type="button" name="returnbutton" value="<bean:message key="button.return"/>" class="mybutton" onclick='back();'>
                </td>
            </tr>
  </table>
</html:form>
