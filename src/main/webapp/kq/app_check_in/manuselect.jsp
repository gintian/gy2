<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/kq/kq.js"></script>
<%
	int i = 0;
%>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript">
	function checkRadio(){
		var len=document.appForm.elements.length;
		var i;
		for (i=0;i<len;i++)
			{
				if (document.appForm.elements[i].type=="checkbox")
				{
					if(document.appForm.elements[i].checked){
						if (document.appForm.elements[i].name != "selbox"){
							return true;
						}
					}
               	}
            }
       return false;
   }
   
   function change()
   {
      appForm.action="/kq/app_check_in/manuselect.do?b_query=link";
      appForm.submit();
   }
   function toallapp(table)
   {
    	parent.menuc.toggleCollapse(true);
    	window.location.href="/kq/app_check_in/all_app_data.do?b_search=link&root=0&jump=1";
   }
   function toaddapp()
   {
       	if(checkRadio()){
    		appForm.action="/kq/app_check_in/add_kqapp.do?b_query=link";
    		appForm.submit();
        }else{
        	alert("请选择申请人！");
        }
   }
   
    var checkflag = "false";

 function selAll()
  {
      var len=document.appForm.elements.length;
       var i;

    
  
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.appForm.elements[i].type=="checkbox")
            {
              document.appForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.appForm.elements[i].type=="checkbox")
          {
            document.appForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  }
	function selectToName(){
		var selectByName = document.getElementById("selectByName");
		var select_type = document.getElementById("select_type").value;
		appForm.action="/kq/app_check_in/manuselect.do?b_query=link&byname="+$URL.encode(getEncodeStr(selectByName.value))+ "&bytype=" +$URL.encode(select_type);
		appForm.submit();
	}
</script>
<html:form action="/kq/app_check_in/manuselect">
	<table width="80%" border="0" cellspacing="1" align="center"
		cellpadding="1">
		<tr>
			<logic:equal name="appForm" property="selectflag" value="0">
				<td align="left" nowrap>
					<span style="vertical-align: middle;">
					<hrms:optioncollection name="appForm" property="dblist"
						collection="list" />
					<html:select name="appForm" property="dbpre" size="1"
						onchange="change();">
						<html:options collection="list" property="dataValue"
							labelProperty="dataName" />
					</html:select>
					<html:select name="appForm" property="select_type"  size="1">
		            	<html:option value="0">姓名</html:option>                      
		           	 	<html:option value="1">工号</html:option>
		            	<html:option value="2">考勤卡号</html:option>
		          	</html:select></span>
					<input type="text" class="text4"  id="selectByName"> &nbsp;<span style="vertical-align: middle;"><input type="button" value="查询" class="mybutton" onclick="selectToName();"></span>
				</td>
			</logic:equal>
		</tr>
	</table>
	<table width="80%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-top: 2px">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
					<input type="checkbox" name="selbox"
						onclick="batch_select(this,'pagination.select');"
						title='<bean:message key="label.query.selectall"/>'>
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="B0110"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="E0122"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<logic:notEqual value="1" name="appForm" property="viewPost">
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="E01A1"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				</logic:notEqual>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="A0101"
						fielditem="fielditem" />
					<bean:write name="fielditem" property="dataValue" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<hrms:fieldtoname name="appForm" fieldname="gno"
						fielditem="fielditem" />
					工号
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap style="border-right: none;">
					<hrms:fieldtoname name="appForm" fieldname="cardno"
						fielditem="fielditem" />
					考勤卡号
					&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="appForm"
			sql_str="appForm.sql_str" table="" where_str="appForm.cond_str"
			columns="${appForm.columns}" order_by="appForm.cond_order"
			pagerows="${appForm.pagerows}" page_id="pagination" indexes="indexes">
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			
			<tr class="trDeep">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="appForm" property="pagination.select"
						value="true" indexes="indexes" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="UN" name="element" codevalue="b0110"
						codeitem="codeitem" scope="page" />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>

					<hrms:codetoname codeid="UM" name="element" codevalue="e0122"
						codeitem="codeitem" scope="page" uplevel="${appForm.uplevel}" />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				<logic:notEqual value="1" name="appForm" property="viewPost">
				<td align="left" class="RecordRow" nowrap>
					<hrms:codetoname codeid="@K" name="element" codevalue="e01a1"
						codeitem="codeitem" scope="page" />
					&nbsp;
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				</logic:notEqual>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="a0101" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="gno" filter="false" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" style="border-right: none;">
					&nbsp;
					<bean:write name="element" property="cardno" filter="false" />
					&nbsp;
				</td>
			</tr>
		</hrms:paginationdb>
		<tr>
			<td colspan="7">
				<table width="100%" align="center" class="RecordRowTop">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="appForm" pagerows="${appForm.pagerows}"
								property="pagination" scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="appForm" property="pagination"
									nameId="appForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>



	<table width="80%" align="center">
		<tr>
			<td align="center">
				<logic:equal name="appForm" property="selectflag" value="0">
					<html:button styleClass="mybutton" property="b_add"
						onclick="toaddapp();">
						<bean:message key="kq.self.apply" />
					</html:button>
					<logic:equal name="appForm" property="table" value="Q11">
						<html:button styleClass="mybutton" property="b_return"
							onclick="toallapp('Q11');">
							<bean:message key="button.return" />
						</html:button>
					</logic:equal>
					<logic:equal name="appForm" property="table" value="Q15">
						<html:button styleClass="mybutton" property="b_return"
							onclick="toallapp('Q15');">
							<bean:message key="button.return" />
						</html:button>
					</logic:equal>
					<logic:equal name="appForm" property="table" value="Q13">
						<html:button styleClass="mybutton" property="b_return"
							onclick="toallapp('Q13');">
							<bean:message key="button.return" />
						</html:button>
					</logic:equal>
				</logic:equal>
				<logic:equal name="appForm" property="selectflag" value="1">
					<!-- 
					<input type="button" name="alll"
						value='<bean:message key="label.query.selectall"/>'
						class="mybutton" onclick="selAll()">
						 -->
					<hrms:submit styleClass="mybutton" property="b_add">
						<bean:message key="kq.self.apply" />
					</hrms:submit>
					<hrms:submit styleClass="mybutton" property="br_pre">
						<bean:message key="button.return" />
					</hrms:submit>
				</logic:equal>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
hide_nbase_select('dbpre');
</script>