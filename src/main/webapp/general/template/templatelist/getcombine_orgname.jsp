<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.*,com.hrms.hjsj.sys.FieldItem,
				com.hrms.frame.dbstruct.Field"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm" %>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem" %>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>

<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	
	               	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			         Calendar calendar = Calendar.getInstance();
			         String date2 = sdf.format(calendar.getTime());
			         calendar.add(Calendar.DATE, -1);
					 String date = sdf.format(calendar.getTime());
	TemplateListForm templateListForm = (TemplateListForm)request.getSession().getAttribute("templateListForm");
 	ArrayList codeitemlist = templateListForm.getCodeitemlist();
 	//	int flag2=0;
 		String manager=userView.getManagePrivCodeValue();
 		String datefillable = templateListForm.getDatefillable();
 		String codedescfillable = templateListForm.getCodedescfillable();
 %>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<title></title>
	<script language="JavaScript">
	function closedialog()
	{
		if(!volidatestart()){
			return false;
		}
		if(!volidfill()){
		return false;
		}
		var info = "确定要合并组织机构吗？";
		<logic:equal name="templateListForm" property="infor_type" value="3">
		 info="确定要合并岗位吗？";
		</logic:equal>
		if(confirm(info)){
		   var hashvo=new ParameterSet();          
      	   hashvo.setValue("infor_type", "${templateListForm.infor_type}");
      	   hashvo.setValue("table_name", "${templateListForm.table_name}");
      	   hashvo.setValue("tarcodeitemdesc", document.templateListForm.combineorgname.value);
      	   hashvo.setValue("combinecodeitemid", document.templateListForm.combinecodeitemid.value);
      	   hashvo.setValue("end_date", document.templateListForm.end_date.value);
           var In_paramters="";
           var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:execombineok,functionId:'0570040046'},hashvo);
	   	 } 
	}
	 function execombineok(outparamters){
	 var url=outparamters.getValue("isrefresh");
	if(url!=""){
	alert(url);
	}else{
	 var temp_vo = new Object();
		   	  temp_vo.values = "true";
	 			window.returnValue=temp_vo;
	 			window.close();
		}
	 	
	 }
	function document.onkeydown()                //网页内按下回车触发
	{
        if(event.keyCode==13)
        {
            document.getElementById("b_ok").click();   
            return false;                               
        }
	}
	function volidfill(){
	var obj=$('combineorgname');
	if(obj.value==null||obj.value.trim().length<1)
	{ alert("合并后名称不能为空！");
	return false;
	}
	return true;
	}
	
	function showcorcode(obj){
		if(obj.value==obj.options[obj.options.length-1].value){
		var msg = "${templateListForm.msg}";
		if(msg!=null&&msg.length>1){
		alert(msg);
		obj.value=obj.options[0].value
		document.templateListForm.combinecodeitemid.value=obj.options[0].value;
		}else{
			//_corcode.style.display="block";
			// selectnew="1";
			 }
		}else{
		}
	}
	function volidatestart(){
		var obj=$('end_date');
		//alert(obj.value);
		if(obj.value=='')
		alert("有效日期不能为空！");
		var temp =<%=datefillable%>;
	if(temp!=null&&temp=="1"){
	if(obj.value==null||obj.value.length<1)
	{ alert("有效日期不能为空！");
	return false;
	}
	}
		var maxstartdate='<%=request.getParameter("maxstartdate") %>';
		//alert(maxstartdate);
		if(maxstartdate!=null&&maxstartdate!=''){
			var v=obj.value;
	           				if(v!=null&&v!=""){
	           					var tnew=(v).replace(/-/g, "/");
	           					var told=(maxstartdate).replace(/-/g, "/");
			   					var dnew=new Date(Date.parse(tnew));
			   					var dold=new Date(Date.parse(told));
			   					if(dnew<dold){
			   						alert("有效日期不能小于"+maxstartdate+"!");
			   						obj.focus();
			   						obj.value='<%=date %>'; 
			   						return false;
			   					}else{
			   						return true;
			   					}
	           				}
        }else{
        	return true;
        }
	}
	//ie兼容trim方法
	if(!String.prototype.trim) {
	    String.prototype.trim = function () {
	        return this.replace(/^\s+|\s+$/g,'');
	    };
	}
	</script>
	
<style>
<!--
.selectPre1 {
	position: absolute;
	left: 30%;
	top:30px;
}
.selectPre2 {
	position: absolute;
	left: 30%;
	top:70px;
}
.selectPre3 {
	position: absolute;
	left: 30%;
	top:80px;
}
.textbox {
	BACKGROUND-COLOR:#F8F8F8;border: 1pt solid #A8C4EC;width:250px
}
-->
</style>
<hrms:themes></hrms:themes>
<html:form action="/general/template/templatelist">
<input type="hidden" name="msgb0110" id="msgb0110" value="" />
<br>
        <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr align="center">
		<td valign="center" class="TableRow"  colspan="2">
		<logic:equal name="templateListForm" property="infor_type" value="2">
		 &nbsp;机构合并&nbsp;
		</logic:equal>
		<logic:equal name="templateListForm" property="infor_type" value="3">
		 &nbsp;岗位合并&nbsp;
		</logic:equal>
		 
		</td>
	 </tr> 
	 <tr><td class="framestyle3"><table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">
	  <tr height="10">
	  
            	<td>&nbsp;</td>
            </tr>
        	<tr height="30px">
                 <td align="right" width="35%" nowrap valign="center" style="padding-right: 5px;">
            	    有效日期
            	    </td><td align="left"  nowrap valign="center">
            	    <input type="text" name="end_date" size="20"   value="<%=date %>" class="text4" style="width:250px" extra="editor" dropDown="dropDownDate" onchange="javascript:if(!validate(this,'有效日期')) {this.focus(); this.value='<%=date %>'; }"/>
            	    <%if(datefillable!=null&&datefillable.equals("1")){ %>
            	    <font color='red'>*</font>
            	    <%} %>
                 </td>
              </tr> 
            <tr height="30px">
                 <td align="right"  nowrap valign="center" style="padding-right: 5px;">
            	    合并后编码
            	    </td><td align="left"  nowrap valign="center">
            	    <select name="combinecodeitemid" style="width:250px" onchange="showcorcode(this)">
            	    	<%for(int i=0;i<codeitemlist.size();i++){ 
            	    		CommonData comm = (CommonData)codeitemlist.get(i);
            	    	%>
            	    		<option value="<%=comm.getDataValue() %>"><%=comm.getDataName() %></option>
            	    	<%} %>
            	    </select>
                 </td>
              </tr>
              <tr height="30px">
                 <td align="right"  nowrap valign="center" style="padding-right: 5px;">
            	   <bean:message key="label.org.combinename"/>
            	    </td><td align="left"  nowrap valign="center">
            	    <input type="text" name="combineorgname" size="20" class="text4" style="width:250px" >
                   <%if(codedescfillable!=null&&codedescfillable.equals("1")){ %>
            	    <font color='red'>*</font>
            	    <%} %>
                 </td>
              </tr> 
              <tr height="8">
                 <td align="center"  nowrap valign="center" colspan="2">
            	    
                 </td>
              </tr> 
            
               <tr height="40">
                 <td align="center"  nowrap valign="center">
            	    
                 </td>
              </tr> 
              </table>
              </td>
              </tr>
          </table>       
     <table  width="100%" align="center">
          <tr>
            <td align="center" style="padding-top: 5xp;">
         	  <input id="b_ok"  type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="closedialog()">
	          <input type="button" name="br_return" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">
            </td>
          </tr>          
    </table>
</html:form >
 
