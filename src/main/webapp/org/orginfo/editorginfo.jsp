<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date,com.hrms.frame.dao.RecordVo,java.util.Map"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	boolean version = false;
	String date ="";
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = sdf.format(new Date());
	}
//	int ver_flag=userView.getVersion_flag();
//	if(ver_flag==0)
//		version=false;
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
<script language="javascript">
function changenewkind()
{
             orgInformationForm.action="/org/orginfo/searchorglist.do?b_kind=link";
             orgInformationForm.submit();
}
function validatelen() 
{
   <logic:equal name="orgInformationForm" property="first" value="0">
     if(orgInformationForm.codeitemid.value.length!="${orgInformationForm.len}")
     {
       alert("<bean:message key="error.org.codelength"/>" + "${orgInformationForm.len}" + "!");
       return false;
     }
   </logic:equal>
	if(0=="${orgInformationForm.len}")
    {
      alert("当前组织机构代码 已达最大长度，无法新增下级机构！");
      return false;
    }
}
function submitsave(e)
{
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
   var  key = e.keyCode;
   //alert(key);
   if(!key||key==13 || key==0)
   {
      if(validatelen()==false)
      {
        return false;
      }
       document.orgInformationForm.target='_self';
       var reg=new RegExp("^[A-Z0-9]+$");
       if(!reg.exec(orgInformationForm.codeitemid.value)){ 
           alert(INPUT_CORRECT_ORG_CODE+"!");
           return false;
       }
       var codeitemdesc=orgInformationForm.codeitemdesc.value;
       if(codeitemdesc.indexOf("\‘")>-1||codeitemdesc.indexOf("\”")>-1||codeitemdesc.indexOf("\'")>-1||codeitemdesc.indexOf("\"")>-1||codeitemdesc.indexOf(" ")>-1)
      	{	
      		alert("机构名称不能包含空格或\’或\"或\’或\”");
      		return false;
      	}
       	var msgfillable="单位代码";
       	<logic:equal value="UM" name="orgInformationForm" property="codesetid">
       		msgfillable="部门代码";
        </logic:equal>
        <logic:equal value="@K" name="orgInformationForm" property="codesetid">
        	msgfillable="岗位代码";
        </logic:equal>
        <logic:equal value="@K" name="orgInformationForm" property="codesetid">
        	<logic:equal value="1" name="orgInformationForm" property="posfillable">
       			validate1('R','codeitemid',ORG_ORGINFO_ORGCODE,'R','codeitemdesc',ORG_ORGINFO_ORGCODENAME,'R','corcode',msgfillable);
      		</logic:equal>
      		<logic:notEqual value="1" name="orgInformationForm" property="posfillable">
       			validate1('R','codeitemid',ORG_ORGINFO_ORGCODE,'R','codeitemdesc',ORG_ORGINFO_ORGCODENAME);
      		</logic:notEqual>
      	</logic:equal>
      	<logic:notEqual value="@K" name="orgInformationForm" property="codesetid">
        	<logic:equal value="1" name="orgInformationForm" property="unitfillable">
       			validate1('R','codeitemid',ORG_ORGINFO_ORGCODE,'R','codeitemdesc',ORG_ORGINFO_ORGCODENAME,'R','corcode',msgfillable);
      		</logic:equal>
      		<logic:notEqual value="1" name="orgInformationForm" property="unitfillable">
       			validate1('R','codeitemid',ORG_ORGINFO_ORGCODE,'R','codeitemdesc',ORG_ORGINFO_ORGCODENAME);
      		</logic:notEqual>
      	</logic:notEqual>
      if(!checkdate())
      	return false;
      if(document.returnValue==true)
      {
      	var code = document.getElementsByName("codesetid");
  		if(code[0].value.length<=0)
  		{
  			alert(HAVE_NOT_PURVIEW);
  		}else{
        	orgInformationForm.action="/org/orginfo/searchorglist.do?b_save=link";
        	orgInformationForm.submit();  
        }
      }
   }
 
}

function checkdate(){
		<%
			if(version){
         %>
		var start_date=document.getElementsByName("start_date")[0].value;
		var end_date=document.getElementsByName("end_date")[0].value;
		start_date=new Date(Date.parse(start_date.replace(/-/g, "/")));
		end_date=new Date(Date.parse(end_date.replace(/-/g, "/")));
		var cur_date=new Date('<%=date%>'.replace(/-/g, "/"));
		//var cur_date=new Date(cur_date.getFullYear(),cur_date.getMonth(),cur_date.getDate());
		if(cur_date<start_date){
			alert("有效日期起不能大于当前日期!");
			return false;
		}
		if(start_date>end_date){
			alert("有效日期起不能大于有效日期止!");
			return false;
		}
		return true;
		<%}else{%>
			return true;
		<%}%>
	}
function validatecode(obj){
	var v = obj.value;
	var reg=new RegExp("^[A-Z0-9]+$");
	for(var i=0;i<v.length;i++){
		var c=v.substr(i,1);
		if(!reg.test(c)){
			alert("系统代码只能为大写字母或数字!");
			obj.value="";
			obj.focus();
			break;
		}
	}
}

function back()
{
	orgInformationForm.action = "/org/orginfo/searchorglist.do?b_return=link";
	orgInformationForm.submit();
}
function estop(e)
{
	//alert(e);
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	if(navigator.appName.indexOf("Microsoft")!= -1)
		return e.keyCode!=34&&e.keyCode!=39;
	else
		return e.which!=34&&e.which!=39;
}

</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchorglist" onsubmit="return validatelen()"> 

<table width="600" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="30">
       		<!-- td width=10 valign="top" class="tableft"></td>
       		<td width=140 align=center class="tabcenter">&nbsp;<bean:message key="label.org.maintenance"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   -->
       		<td align="left" colspan="1" class="TableRow">&nbsp;<bean:message key="label.org.maintenance"/>&nbsp;</td>           	             	                  	      
  </tr>  
   <tr>
      <td colspan="4" class="framestyle3" width="100%" align="center">
           <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center"> 
             <tr  align="center" class="list3">
              <td colspan="2"  class="RecordRowHr">
                 <div align="center"><bean:write name="orgInformationForm" property="labelmessage"  filter="true"/></div>
              </td>
             </tr>
             <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                  <bean:message key="label.org.type_org"/>
                </td>
                <td align="left"  class="RecordRowHr">
                <logic:equal name="orgInformationForm" property="kind" value="1">
                   <html:select name="orgInformationForm" property="codesetid" size="1" onchange="changenewkind()">
                   <hrms:priv func_id="23054">
                       <html:option value="UM"><bean:message key="label.codeitemid.um"/></html:option>
                   </hrms:priv>
	                   <hrms:priv func_id="23055">
	                       <html:option value="@K"><bean:message key="hmuster.label.post"/></html:option>
	                   </hrms:priv>
                   </html:select>
                </logic:equal>
                <logic:equal name="orgInformationForm" property="kind" value="2">
                       <html:select name="orgInformationForm" property="codesetid" size="1" onchange="changenewkind()">
                       	 <hrms:priv func_id="23053">
                         	<html:option value="UN"><bean:message key="label.codeitemid.un"/></html:option>
                         </hrms:priv>
                         <hrms:priv func_id="23054">
                         	<html:option value="UM"><bean:message key="label.codeitemid.um"/></html:option>
                         </hrms:priv>
                         <logic:notEmpty name="orgInformationForm" property="code">
	                         <hrms:priv func_id="23055">
	                       		<html:option value="@K"><bean:message key="hmuster.label.post"/></html:option>
	                   		 </hrms:priv>
                   		 </logic:notEmpty>
                       </html:select>               
                </logic:equal>
                </td>
             </tr> 
             <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                  <bean:message key="label.org.superiorcode"/>
                </td>
                <td align="left"  class="RecordRowHr">
                   <html:text   name="orgInformationForm" property="code" readonly="true" styleClass="textColorRead"/>
                </td>
             </tr> 
             <tr  align="right" class="list3">
               <td  class="RecordRowHr">
                  &nbsp;<bean:message key="label.org.curcode"/>
                </td>
               <td align="left"  class="RecordRowHr">
                 <html:text   name="orgInformationForm" property="codeitemid"  styleClass="textColorWrite" maxlength="${orgInformationForm.len}" onblur="validatecode(this)"/>
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
                   		&nbsp;<bean:message key="label.codeitemid.kk"/>&nbsp;
                   </logic:equal>
                </td>
               <td align="left"  class="RecordRowHr"> 
                  <html:text onkeypress="return estop(event);" name="orgInformationForm" property="codeitemdesc"  styleClass="textColorWrite" maxlength="<%=codeitemdesclen %>"  />
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
               <input type="text" name="start_date" value="<%=date %>" maxlength="50" class="text4" style="BACKGROUND-COLOR:#F8F8F8;width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期起')) {this.focus(); this.value='<%=date %>'; }"/>
               </td>
             </tr>
             <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                   &nbsp;<bean:message key="conlumn.codeitemid.end_date"/>
                </td>
               <td align="left"  class="RecordRowHr"> 
                  <input type="text" name="end_date" value="9999-12-31" maxlength="50" class="text4" style="BACKGROUND-COLOR:#F8F8F8;width:200px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期止')) {this.focus(); this.value='9999-12-31'; }"/>
               </td>
             </tr>
             <%} %>
              <tr  align="right" class="list3">
                <td  class="RecordRowHr">
                   <logic:equal value="UN" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.un"/><bean:message key="kh.field.code"/>&nbsp;
                   </logic:equal>
                   <logic:equal value="UM" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.um"/><bean:message key="kh.field.code"/>&nbsp;
                   </logic:equal>
                   <logic:equal value="@K" name="orgInformationForm" property="codesetid">
                   		&nbsp;<bean:message key="label.codeitemid.kk"/><bean:message key="kh.field.code"/>&nbsp;
                   </logic:equal>
                </td>
               <td align="left"  class="RecordRowHr"> 
                  <html:text  onkeypress="return estop(event);" name="orgInformationForm" property="corcode"  styleClass="textColorWrite" maxlength="50"/>
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
             <logic:notEqual name="orgInformationForm" property="orgtype" value="vorg">
             <logic:equal name="orgInformationForm" property="vflag" value="1">
	             <TR align="right" class="list3">
	             	<TD  class="RecordRowHr">
	             		<html:radio name="orgInformationForm" property="vorganization" value="0" >正式机构</html:radio>
	             	</TD>
	             	<TD align="left"  class="RecordRowHr">
	             		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="orgInformationForm" property="vorganization" value="1" >虚拟机构</html:radio>
	             	</TD>
	             </TR>
             </logic:equal>
             </logic:notEqual>
             
             <logic:equal name="orgInformationForm" property="orgtype" value="vorg">
	             <TR align="right" class="list3">
	             	<TD  class="RecordRowHr">
	             	</TD>
	             	<TD align="left"  class="RecordRowHr">
	             		<html:hidden name="orgInformationForm" property="vorganization" value="1" />
	             	</TD>
	             </TR>
             </logic:equal>
          </table>
       </td>
   </tr>   
  </table>
  <table width="600" align="center">
  	<tr  align="center">
                <td >
                 <input type="button" Class="mybutton" name="b_save"  value="<bean:message key='addunitinfo.reportunit.save'/>" onClick="submitsave(event)" onkeydown="submitsave(event);" />                 
	         <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick='back();'>
                </td>
            </tr>
  </table>
</html:form>
