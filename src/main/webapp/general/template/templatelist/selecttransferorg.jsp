<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateListForm" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		            Calendar calendar = Calendar.getInstance();
		            String date2 = sdf.format(calendar.getTime());
		            calendar.add(Calendar.DATE, -1);
					String date = sdf.format(calendar.getTime());
					TemplateListForm templateListForm = (TemplateListForm)request.getSession().getAttribute("templateListForm");
// 	ArrayList codeitemlist = templateListForm.getCodeitemlist();
 %>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>
<script language="javascript">	
   function openOrgTreeDialog()
   {
        var thecodeurl="/org/orginfo/searchtransferorgtree.do?b_query=link"; 
        var oldobj=templateListForm.tarorgname;
        var hiddenobj=templateListForm.transfercodeitemid;
            var theArr=new Array(oldobj,hiddenobj); 
        var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   }
   function volidatestart(){
		var obj=$('end_date');
		if(obj.value=='')
		alert("有效日期不能为空！");
		//alert(obj.value);
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
		function closedialog()
	{
	if(!volidatestart()){
			return false;
		}
	
	 	var length=templateListForm.transfercodeitemid.value.length;
	var info = "确定要划转组织机构吗？";
		<logic:equal name="templateListForm" property="infor_type" value="3">
		 info="确定要划转岗位吗？";
		</logic:equal> 	
 	if(length==0){
 		alert("请选择目标机构名！");
	 	return false;
	 	}
	 if(confirm(info)){
	 			
	 			var hashvo=new ParameterSet();			
				hashvo.setValue("transfercodeitemid",templateListForm.transfercodeitemid.value);
				hashvo.setValue("infor_type","${templateListForm.infor_type}");
				hashvo.setValue("end_date",templateListForm.end_date.value);
				hashvo.setValue("table_name","${templateListForm.table_name}");
	
				var request=new Request({method:'post',asynchronous:false,onSuccess:resultinfo,functionId:'0570040048'},hashvo);
	}else{
		return false;
	}
	}
	function  resultinfo(outparamters)
   {
	var url=outparamters.getValue("resultinfor");
	if(url!=""){
	alert(url);
	}else{
		   	  var temp_vo = new Object();
		   	  temp_vo.values = "true";
	 			window.returnValue=temp_vo;
	 			window.close();
	 	}
   }	
 	function show(obj){
		obj.style.display="none";
		var _div=document.getElementById("changehis");
		var _span=document.getElementById("hid");
		_div.style.display="block";
		_span.style.display="block";
	}
	function hid(obj){
		obj.style.display="none";
		var _div=document.getElementById("changehis");
		var _span=document.getElementById("show");
		_div.style.display="none";
		_span.style.display="block";
	}
   </script>
<html:form action="/general/template/templatelist">
<br>
<table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr align="center">
		<td valign="center" class="TableRow"  colspan="2">
		<logic:equal name="templateListForm" property="infor_type" value="2">
		 &nbsp;<bean:message key="label.org.selecttarorg"/>&nbsp;
		</logic:equal>
		<logic:equal name="templateListForm" property="infor_type" value="3">
		 &nbsp;<bean:message key="label.duty.selecttarorg"/>&nbsp;
		</logic:equal> 	
		  
		</td>
	 </tr> 
	 <tr><td class="framestyle3"><table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">
	  <tr height="10">
            	<td>&nbsp;</td>
            </tr>
        	<tr  height="30px">
                 <td align="right" nowrap valign="center" width="35%" style="padding-right: 5px;">
            	    有效日期
            	    </td><td align="left"  nowrap valign="center">
            	    <input type="text" name="end_date" value="<%=date %>" maxlength="50" class="text4" style="width:250px" extra="editor" dropDown="dropDownDate" onchange="if(!validate(this,'有效日期')) {this.focus(); this.value='<%=date %>'; }"/>                                            
                 </td>
              </tr> 
           <tr  height="30px">
                 <td align="right"  nowrap valign="center" style="padding-right: 5px;">
            	     <html:hidden name="templateListForm" property="transfercodeitemid"/> 
                  目标机构名
            	    </td><td align="left"  nowrap valign="center">
                <html:text name="templateListForm" property="tarorgname" readonly="false" styleClass="text4" style="width:250px"/> 
                 <img  src="/images/code.gif" align="absmiddle" onclick='javascript:openOrgTreeDialog();'/>  
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
            <td align="center" style="padding-top: 5px;">
         	
	     <input id="b_ok"  type="button" name="b_ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="closedialog()">
	     <input type="button" name="b_return" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">
            </td>
          </tr>          
    </table>
</html:form>
