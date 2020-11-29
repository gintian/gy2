<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">

var count=0;
var pobj;
var stype = "";//代表保存还是提交
var body = "${performanceInterviewForm.body}";
var status = "${performanceInterviewForm.status}";
function sub(status)
{
   stype = status;
   if(count>0)
      return;
   count++;
   var clo="1";
   if(status=='0')
       clo="3";
   var hashvo=new ParameterSet();
   hashvo.setValue("interview",getEncodeStr(document.getElementsByName('interview')[0].value));
   hashvo.setValue("id",'${performanceInterviewForm.id}');
   hashvo.setValue("objectid",'${performanceInterviewForm.objectid}');
   hashvo.setValue("plan_id",'${performanceInterviewForm.plan_id}');
   hashvo.setValue("status",status);
   hashvo.setValue("isClose",clo);
   var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'9028000404'},hashvo);
   
}
function returnInfo(outparamters) {
	var num = outparamters.getValue("num");
	var id = outparamters.getValue("id");
	performanceInterviewForm.action="/performance/interview/search_interview_list.do?b_edit=edit&id="+id;
	performanceInterviewForm.submit();
	if(stype == 0) {
		if(num > 0) {
			alert("保存成功!");
		}else {
			alert("保存失败!");
		}
	}
}
function clo(isclose)
{
  if(isclose=='1')
  {
	//【44838】IE11，自助服务/绩效自助/考核沟通/面谈记录，保存提交后，再点面谈记录，刚才的信息显示不出来，刷新不对。只有整个退出再点进来才能正确显示。  ,如果是提交的状态下，不会出现保存和提交的按钮的
	if(body == "0" && (status == "0" || status == "1")) {//body==0和status==0的时候是有保存和提交按钮的，这种情况需要重新赋一下id的值
	    var obj=new Object();
	    obj.id="${performanceInterviewForm.id}";
	    //returnValue=obj;
	    pobj.openValue(obj);//调用父页面的openValue方法  wangb 20171206
	}
    //window.close();
	if(!window.showModalDialog && parent.parent.Ext){
	    var win = parent.parent.Ext.getCmp('perInterview_win');
	 	if(win) {
	  		win.close();
	 	}
	}else{
        parent.window.close();//关闭弹窗
    }

  }
  else
  {
    if(isclose=='3'&&pobj)
    {
       document.getElementById("pif").value="${performanceInterviewForm.id}";
       pobj.document.getElementById("idvalue").value="${performanceInterviewForm.id}";
      //alert(pobj.document.getElementById("idvalue").value);
    }
  }
}
//-->
</script>
<html:form action="/performance/interview/search_interview_list">

 <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
 <logic:equal value="0" name="performanceInterviewForm" property="body">
 <tr>
 <td align="left">
 <fieldset>
 <legend><font style="font-weight:bold" size='2'>提示</font></legend>
 <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
 <tr><td align="left">您可以从以下几个维度记录谈话的内容：</td></tr>
 <tr><td align="left">1:可取之处;</td></tr>
 <tr><td align="left">2:有待改善之处;</td></tr>
 <tr><td align="left">3:其他评语;</td></tr>
 <tr><td align="left">4:培训发展建议;</td></tr>
</table>
</fieldset>
 </td>
 </tr><tr><td>&nbsp;</td></tr>
 </logic:equal>
	   <tr>
	   <html:hidden property="objectid" name="performanceInterviewForm"/>
       <html:hidden property="plan_id" name="performanceInterviewForm"/>
      <html:hidden property="id" name="performanceInterviewForm"/>
	   <td align="left" width="640">
      <table width="640" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<td align='left' class="TableRow_lrt">
       		<font style="font-weight:bold" size='2'>谈话记录</font>
       		</td>              	      
          </tr> 
          <tr>
            <td class="framestyle">
            
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   	 		 		
		 		<tr>		 			
		 			<td width="100%"  align='center' >
		 			<br>
		 			<logic:equal value="0" name="performanceInterviewForm" property="body">
		 			<html:textarea property="interview" name="performanceInterviewForm"  rows='22' cols='85'></html:textarea>
		 			</logic:equal>
		 			<logic:equal value="1" name="performanceInterviewForm" property="body">
		 				<html:textarea property="interview" name="performanceInterviewForm"  rows='22' cols='85'></html:textarea>
		 			</logic:equal>
		 			</td>		 		
		 		</tr>				                                                   
      </table>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td align="center" style="padding:5px;">

<html:hidden name="performanceInterviewForm" property="id" styleId="pif"/>
<logic:equal value="0" name="performanceInterviewForm" property="body">
<logic:equal value="0" name="performanceInterviewForm" property="status">
<input type="button" class="mybutton" name="oo" value="<bean:message key="button.save"/>" onclick="sub('0');"/>
<input type="button" class="mybutton" name="smt" value="提交" onclick="sub('1');"/>
</logic:equal>
</logic:equal>
<input type="button" class="mybutton" name="oo" value="<bean:message key="button.close"/>" onclick="clo('1');"/> <!--window.close() open 弹窗关闭不了 wangb 20171206  -->
</td>
</tr>
</table>
<script type="text/javascript">
//pobj=window.dialogArguments;
pobj = parent.window.opener;//open 弹窗获取 父页面   iframe 显示  需加 parent  wangb 20171206  
clo("<%=request.getParameter("isClose")%>");
if(!getBrowseVersion()){//非IE浏览器下  样式兼容性 wangb 20180109
	var interview = document.getElementsByName('interview')[0];//调整textarea 宽度 不显示滚动条
	interview.style.width = '82%';
}else{ //IE浏览器下 fieldset 里 提示信息太靠左  bug 36682 wangb 20180417
	if(body == "0") {//只有在body==0的时候会显示fieldset，
		var firsttable = document.getElementsByTagName('fieldset')[0].getElementsByTagName('table')[0];
		firsttable.style.margin='5px 0px 10px 10px';
	}
}
</script>
</html:form>