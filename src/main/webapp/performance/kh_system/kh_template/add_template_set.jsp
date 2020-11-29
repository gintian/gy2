<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<script type="text/javascript">
<!--
var counter=0;
var psd="";
function savevalue(saveandcontinue)
{
  var fname=document.khTemplateForm.fname.value;
  if(fname==null||trim(fname).length<=0)
  {
    alert("模版分类名称不能为空!");
    return;
  }
  var obj=document.getElementById("aa");
  var flag="";
  counter++;
  for(var i=0;i<obj.options.length;i++)
  {
    if(obj.options[i].selected)
    {
       flag=obj.options[i].value;
    }
  }
  var scope = "";
  var rdos = document.getElementsByTagName("input");
  for(var i=0; i<rdos.length; i++){
	if(rdos[i].type=="radio" && rdos[i].checked){
		scope = rdos[i].value;
	}
  }
    var hashvo=new ParameterSet();
	hashvo.setValue("saveandcontinue",saveandcontinue);
	hashvo.setValue("scope",scope);
	hashvo.setValue("flag",flag);
	hashvo.setValue("parentid",khTemplateForm.parentid.value);
	hashvo.setValue("fname",getEncodeStr(fname));
	hashvo.setValue("subsys_id",khTemplateForm.subsys_id.value);
	hashvo.setValue("type",khTemplateForm.type.value);
	hashvo.setValue("templatesetid",khTemplateForm.templatesetid.value);
    var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9021001027'},hashvo);
}
function save_ok(outparameters)
{
  var saveandcontinue = outparameters.getValue("isClose");	
  var fname=outparameters.getValue("fname");
  var templatesetid=outparameters.getValue("templatesetid");
  psd=templatesetid;
  var type=outparameters.getValue("type");
  var subsys_id=outparameters.getValue("subsys_id");
  if(saveandcontinue=="2")
  {
      document.khTemplateForm.fname.value="";
     /*  var obj=document.getElementById("aa");  //xuj update 2015-1-9  浙江景兴纸业股份有限公司提，连续新增标示不用置为无效
      for(var i=0;i<obj.options.length;i++)
      {
       if(i==obj.options.length-1)
        {
            obj.options[i].selected=true;
        }
      } */
  }
  else
  {
      var obj = new Object();
      obj.fname=fname;
      obj.templatesetid=templatesetid;
      obj.type=type;
      obj.subsys_id=subsys_id;
      obj.refresh="2";
      parent.window.returnValue=obj;
      if(window.showModalDialog) {
          parent.window.close();;
      }else{
          parent.window.opener.window.addTemplateSet_Ok(obj);
          window.open("about:blank","_top").close();
      }
  }
}
function closee(refresh)
{
   var obj = new Object();
   if(counter==0)
      obj.refresh = "1";
   else
   {
      obj.refresh = "2";
      obj.templatesetid=psd;
      obj.subsys_id=khTemplateForm.subsys_id.value;
   }
    parent.window.returnValue=obj;
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        parent.window.opener.window.addTemplateSet_Ok(obj);
        window.open("about:blank","_top").close();
    }
}
//-->
</script>
<html:form action="/performance/kh_system/kh_template/add_template_set">
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosFlag = userView.getBosflag();
	int top = 5;
	int left = 5;
	top = !"hcm".equalsIgnoreCase(bosFlag) ? 14 : top;
	left = !"hcm".equalsIgnoreCase(bosFlag) ? 5 : left;
%>
<table width="500" border="0" cellspacing="0" style="margin-left:0px;" align="center" cellpadding="0">
<tr height="20">
			<td align='left' class='TableRow_lrt'>
			<logic:equal value="0" name="khTemplateForm" property="type">
				<bean:message key="label.kh.new.tmplatefen"/>
				</logic:equal>
				<logic:equal value="1" name="khTemplateForm" property="type">
				修改<bean:message key="lable.kh.template"/>
				</logic:equal>
				
			</td>
		</tr>
		<tr>
			<td align="center" class="framestyle" height="120px">
			
			<table border="0" cellpadding="5" cellspacing="0" class="DetailTable" cellpadding="0">
			<tr height="35">
			<td align="right">分类名称</td>
			<TD align="left">
			<input type="text"  style="width:152px;" value="<bean:write name="khTemplateForm" property="fname"/>" name="fname" class="inputtext"/>
			</td>
			</tr>
			<tr height="35">
			<td align="right">
			<bean:message key="kh.field.flag_kb"/>&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kh.field.flag_ks"/>
			</td>
			<td align="left">
			<html:select styleId="aa" name="khTemplateForm" property="fvalidflag" style="width:152px;">
			                    <html:option value="1"><bean:message key="kh.field.yx"/></html:option>
								<html:option value="0"><bean:message key="kh.field.wx"/></html:option>
							</html:select>
			</td>
			</tr>
			<tr height="35">
			<td>&nbsp;</td>
			<td >
			  <html:radio property="scope" value="0">&nbsp;&nbsp;共享</html:radio>
			  <html:radio property="scope" value="1">&nbsp;&nbsp;私有</html:radio>
			</td>
			</tr>
			</table>
</td>			
</tr>
<html:hidden name="khTemplateForm" property="type"/>
<html:hidden name="khTemplateForm" property="templatesetid"/>
<html:hidden name="khTemplateForm" property="subsys_id"/>
<html:hidden name="khTemplateForm" property="parentid"/>
</table>
<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0">
<tr>
<td align="center" height="35px">
<input type="button" class="mybutton" onclick="savevalue('1');" name="save" value="<bean:message key="button.save"/>"/>
<logic:equal value="0" name="khTemplateForm" property="type">
   <input type="button" class="mybutton" onclick="savevalue('2');" name="sAndC" value="<bean:message key="button.savereturn"/>"/>
</logic:equal>
<input type="button" class="mybutton" onclick="closee('${khTemplateForm.isrefresh}');" name="clo" value="<bean:message key="button.cancel"/>"/>
</td>
</tr>
</table>
</html:form>