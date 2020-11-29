<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
function getfield(){
	var abkflag=busiMaintenceForm.abkfalg.value;
	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_input=link&query=query&group="+abkflag;
	busiMaintenceForm.submit();
	
}
function getitem(){
var abkflag=busiMaintenceForm.abkfalg.value;
var itemid=busiMaintenceForm.itemid.value;
	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_input=link&query=query&group="+abkflag+"&inputfield="+itemid;
	busiMaintenceForm.submit();
}
function backover(){
	busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_query=link";
	busiMaintenceForm.submit();
}
function input(type){
 var str="";
 	var dd=0;
	for(var i=0;i<document.busiMaintenceForm.elements.length;i++)
			{
				if(document.busiMaintenceForm.elements[i].type=="checkbox")
				{
					if(document.busiMaintenceForm.elements[i].name=="selbox")
						continue;
					if(document.busiMaintenceForm.elements[i].checked==true)
					{
						str+=document.getElementById("itemid"+dd).value+"/";
							
					}
					dd++;
				}
			}
  if(str.length==0)
  {
  		alert(PLEASE_SEL);
		return;
  }else{
  		var hashvo=new ParameterSet();
		hashvo.setValue("fieldsetid",'${busiMaintenceForm.fieldsetid }');
		hashvo.setValue("fielditemids",str);
		var request=new Request({method:'post',asynchronous:false,onSuccess:checkresult,functionId:'1010060026'},hashvo);
  		function checkresult(outparamters){
			var msg=outparamters.getValue("msg");
			if(msg=='ok'){
  				if(confirm("确定导入吗？"))
		  		{
		  			 var fieldid = document.getElementById("fieldid").value;
		     		busiMaintenceForm.action='/system/busimaintence/inputfield.do?b_input=link&fieldid='+fieldid+'&type='+type;
					// busiMaintenceForm.target="mil_body";
			 		busiMaintenceForm.submit();
					//	 window.close();
		  		}
	  		}else{
	  			alert(msg);
	  		}
  		}
  }
  
}
  if("${param.type}"=='save')
	{	
		var thevo=new Object();
		thevo.flag="true";
		// window.returnValue=thevo;
		// window.close();
		parent.parent.return_vo = thevo;
		winClose();
	}
//-->
function winClose() {
	if(parent.parent.Ext.getCmp('inputitem')){
        parent.parent.Ext.getCmp('inputitem').close();
	}
}
function changeTrColor(id)
 {
    var ob=document.getElementById("tb");
    var j=ob.rows.length;
    for(var i=0;i<j-2;i++)
    {
         var o="a_"+i;
         var obj=document.getElementById(o);
         if(o==id)
         {
           if(o!=null)
           {
               obj.className="selectedBackGroud";
           }
         }
         else
         {
           if(i%2==0)
           {
              if(o!=null)
              {
                obj.className="trShallow";
                
              }
           }
           else
           {
               if(o!=null)
               {
                  obj.className="trDeep";
               }
           }
         }
    }
 }
</script>

<html:form action="/system/busimaintence/inputfield">

<table width="590" border="0" cellspacing="0"  cellpadding="0" class="ListTable">
<tr>
<td nowrap="nowrap">
<input type="hidden" name="ss"/>
	<table  width="100%" align="left" border="0" id="tb" cellspacing="0"  cellpadding="0" class="ListTable">
	
	
	<tr style="height: 35px">
	<td class="RecordRow" colspan="7" style="height: 35px">
	<bean:write name="busiMaintenceForm" property="fieldsel" filter="false"/>&nbsp;
	<bean:write name="busiMaintenceForm" property="itemsel" filter="false"/>
	<html:hidden styleId="fieldid" name="busiMaintenceForm" property="fieldsetid"/>
	</td>
	</tr>
		<TR>
				<td class="TableRow" align="center">
				<input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kjg.title.indexname"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="kjg.title.zhibiaotaihao"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="label.org.type_org"/>
				</td>
				<td class="TableRow" align="center">
					<bean:message key="report.parse.len"/>
				</td>
				<td  class="TableRow" align="center">
					<bean:message key="kq.wizard.decimal"/>
				</td>
				<td  class="TableRow" align="center">
					<bean:message key="kq.item.code"/>
				</td>
			</TR>
	
		<bean:define id="dev_flag" name="busiMaintenceForm" property="userType"/>
		<%int i=0;%>
		<%--busiMaintenceForm.fielditemcount 查询导入指标总数 一页显示不分页  wangb 20180511 --%>
		<hrms:paginationdb id="element" name="busiMaintenceForm" sql_str="busiMaintenceForm.bsql" table="" where_str="busiMaintenceForm.bwhere" columns="busiMaintenceForm.bcolumn" order_by="busiMaintenceForm.borderby" pagerows="${busiMaintenceForm.fielditemcount}" page_id="pagination" indexes="indexes">
			
			<%
          		if(i%2==0)
          		{
          	%>
          <tr class="trShallow" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
          	<%}
          		else
          	{%>
          <tr class="trDeep" id="<%="a_"+i%>" onclick="changeTrColor('<%="a_"+i%>');">
          	<%}%>
		<td align="center" class="RecordRow" nowrap>
			<bean:define id="fielditemid" name="element" property="itemid"/>
			<input type='hidden' id="itemid<%=i %>" value="${fielditemid}"/>
			<hrms:checkmultibox name="busiMaintenceForm" property="pagination.select" value="true" indexes="indexes"/>
		</td>
		<td align="center" class="RecordRow" nowrap>
			<bean:write name="element" property="itemdesc"/>
		</td>
		<td align="center" class="RecordRow" nowrap>
			<bean:write name="element" property="itemid"/>
		</td>
		<td align="center" class="RecordRow" nowrap>
		<logic:equal value="A" name="element" property="itemtype">
		<logic:equal value="0" name="element" property="codesetid">
		<bean:message key="kq.formula.charat"/>
		</logic:equal>
		<logic:notEqual value="0" name="element" property="codesetid">
		<bean:message key="system.item.cdtype"/>
		</logic:notEqual>
		</logic:equal>
		<logic:equal value="D" name="element" property="itemtype">
		<bean:message key="system.item.dtype"/>
		</logic:equal>	
		<logic:equal value="N" name="element" property="itemtype">
		<bean:message key="system.item.ntype"/>
		</logic:equal>	
		<logic:equal value="M" name="element" property="itemtype">
		<bean:message key="system.item.mtype"/>
		</logic:equal>	
		</td>
		<td align="center" class="RecordRow" nowrap>
			<bean:write name="element" property="itemlength"/>
		</td>
		<td align="center" class="RecordRow" nowrap>
		<logic:notEqual value="0" name="element" property="decimalwidth">
			<bean:write name="element" property="decimalwidth"/>
		</logic:notEqual>	
		</td>
		<td align="center" class="RecordRow" nowrap>
		<logic:notEqual value="0" name="element" property="codesetid">
			<bean:write name="element" property="codesetid"/>
		</logic:notEqual>		
		</td>
		</tr>
		<% i++; %>
		</hrms:paginationdb>
	</table>
	</td>
	</tr>
	<TR>
	<td>
	<input type="hidden" name="displayid" value="${displayid}"/>
	<input type="hidden" name="fieldsetid" value="${fieldsetid}"/>
		</td>
		</tr>
		<tr>
			<td align="center" style="height: 35px">
					<button name='back' class="mybutton" onclick="input('save')">
					<bean:message key="menu.gz.import" />
					</button>
					&nbsp;
					<%--<button name='back' class="mybutton" onclick='window.close();'>--%>
					<%--<bean:message key="button.cancel" />--%>
					<%--</button>--%>
				<button name='back' class="mybutton" onclick='winClose();'>
					<bean:message key="button.cancel" />
				</button>
				
			</td>
		</tr>
		</table>
	
		

</html:form>
<script>
</script>




