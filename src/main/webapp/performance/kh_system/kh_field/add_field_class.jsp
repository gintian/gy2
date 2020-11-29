<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
var counter=0;
var psd="";
function savevalue(saveandcontinue)
{
  var pointname=document.khFieldForm.pointname.value;
  if(pointname==null||trim(pointname).length<=0)
  {
    alert("指标分类名称不能为空!");
    return;
  }
  
  // 判断指标分类名称长度是否超限(50) lium
  if (pointname.replace(/[[\u4E00-\u9FA5]/gm,"**").length > 50) {
	  alert("指标分类名称不能超过50个字符（一个中文视为2个字符）");
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
    var rdos = document.getElementsByName("scope");
    var scope = 0;
    for(var j = 0;j < rdos.length;j++){
			if(rdos[j].checked == true){
				scope = rdos[j].value;
			}
		}
     var hashvo=new ParameterSet();
	hashvo.setValue("saveandcontinue",saveandcontinue);
	hashvo.setValue("flag",flag);
	hashvo.setValue("parent_id",khFieldForm.parent_id.value);
	hashvo.setValue("pointname",getEncodeStr(pointname));
	hashvo.setValue("subsys_id",khFieldForm.subsys_id.value);
	hashvo.setValue("type",khFieldForm.type.value);
	hashvo.setValue("pointsetid",khFieldForm.pointsetid.value);
	hashvo.setValue("scope",scope);
    var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'9021001004'},hashvo);
     
 //khFieldForm.action="/performance/kh_system/kh_field/add_field_class.do?b_save=save&saveandcontinue="+saveandcontinue+"&flag="+flag;
// khFieldForm.submit();
}
function save_ok(outparameters)
{
  var saveandcontinue = outparameters.getValue("isClose");
  var pointname=outparameters.getValue("pointname");
  var pointsetid=outparameters.getValue("pointsetid");
  psd=pointsetid;
  var type=outparameters.getValue("type");
  var subsys_id=outparameters.getValue("subsys_id");
  if(saveandcontinue=="2")
  {
      document.khFieldForm.pointname.value="";
      var obj=document.getElementById("aa");
      for(var i=0;i<obj.options.length;i++)
      {
       if(i==obj.options.length-1)
        {
            obj.options[i].selected=true;
        }
      }
  }
  else
  {
      var obj = new Object();
      obj.pointsetname=pointname;
      obj.pointsetid=pointsetid;
      obj.type=type;
      obj.subsys_id=subsys_id;
      obj.refresh="2";
      parent.window.returnValue=obj;
      if(window.showModalDialog) {
          parent.window.close();;
      }else{
          parent.window.opener.window.addField_win_OK(obj);
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
      obj.pointsetid=psd;
      obj.subsys_id=khFieldForm.subsys_id.value;
   }
    parent.window.returnValue=obj;
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        parent.window.opener.window.addField_win_OK(obj);
        window.open("about:blank","_top").close();
    }
}
//-->
</script>
<html:form action="/performance/kh_system/kh_field/add_field_class">
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String bosFlag = userView.getBosflag();
	int top = 5;
	int left = 5;
	top = !"hcm".equalsIgnoreCase(bosFlag) ? 14 : top;
	left = !"hcm".equalsIgnoreCase(bosFlag) ? 5 : left;
%>
<table width="410" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:5px;margin-left:0px;">
<tr height="20">
			<td align='left' class='TableRow_lrt'>
			<logic:equal value="1" name="khFieldForm" property="type">
				<bean:message key="kh.field.new_class"/>
				</logic:equal>
				<logic:equal value="2" name="khFieldForm" property="type">
				<bean:message key="kh.field.edit_class"/>
				</logic:equal>
				
			</td>
		</tr>
		<tr>
			<td align="center" class="framestyle" height="120px">
			
			<table border="0" cellpadding="5" cellspacing="0" class="DetailTable" cellpadding="0">
			<tr height="35">
			<td align="right"><bean:message key="kh.field.name"/></td>
			<TD align="left">
			<input type="text" size="20px" value="<bean:write name="khFieldForm" property="pointname"/>" name="pointname" class="inputtext" style="width:152px;"/>
			</td>
			</tr>
			<tr height="35">
			<td align="right">
			<bean:message key="kh.field.flag_kb"/>&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="kh.field.flag_ks"/>
			</td>
			<td align="left">
			<html:select styleId="aa" name="khFieldForm" property="validflag" style="width:152px;">
								<html:option value="0"><bean:message key="kh.field.wx"/></html:option>
								<html:option value="1"><bean:message key="kh.field.yx"/></html:option>
							</html:select>
			</td>
			</tr>
			<tr height="35">
			<td>&nbsp;</td>
			<td >
			  <html:radio name="khFieldForm" property="scope" value="0">&nbsp;&nbsp;共享</html:radio>
			  <html:radio name="khFieldForm" property="scope"  value="1">&nbsp;&nbsp;私有</html:radio>
			  

			</td>
			</tr>
			</table>
</td>			
</tr>
<html:hidden name="khFieldForm" property="type"/>
<html:hidden name="khFieldForm" property="pointsetid"/>
<html:hidden name="khFieldForm" property="subsys_id"/>
<html:hidden name="khFieldForm" property="parent_id"/>
</table>
<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="center" style="padding-top:8px;">
<input type="button" class="mybutton" onclick="savevalue('1');" name="save" value="<bean:message key="button.save"/>"/>
<logic:equal value="1" name="khFieldForm" property="type">
   <input type="button" class="mybutton" onclick="savevalue('2');" name="sAndC" value="<bean:message key="button.savereturn"/>"/>
</logic:equal>
<input type="button" class="mybutton" onclick="closee('${khFieldForm.isrefresh}');" name="clo" value="<bean:message key="button.cancel"/>"/>
</td>
</tr>
</table>
</html:form>