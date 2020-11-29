
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_self.tax.SelfTaxForm"%>
<%
int i = 0;
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script> 

<script type="text/javascript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>

<script type="text/javascript" language="javascript">

	function showanian(){
		var anian=$('anian');
		var ataxdate=$('ataxdate');
		var atimefield=$('atimefield');
		anian.style.display="block";
		ataxdate.style.display="none";
		atimefield.style.display="none";
	}
	
	function showataxdate(){
		var anian=$('anian');
		var ataxdate=$('ataxdate');
		var atimefield=$('atimefield');
		anian.style.display="none";
		ataxdate.style.display="block";
		atimefield.style.display="none";
	}
	
	function showatimefield(){
		var anian=$('anian');
		var ataxdate=$('ataxdate');
		var atimefield=$('atimefield');
		anian.style.display="none";
		ataxdate.style.display="none";
		atimefield.style.display="block";
	}
	
	function shownone(){
		anian.style.display="none";
		ataxdate.style.display="none";
		atimefield.style.display="none";
	}

	function querytf() {
	    var startime = selfTaxForm.startime.value;
	    var endtime = selfTaxForm.endtime.value;
	    // 指向第一页
	    this.document.selfTaxForm.paginationDbAction.value = 'First';
	    selfTaxForm.action = "/gz/gz_self/tax/selftaxshow.do?b_query=link&action=query&timefield=s&startime=" + startime + "&endtime=" + endtime;
	    selfTaxForm.submit();
	}
	
	function seletetyear() {
	    var nian = selfTaxForm.nian.value;
	    if (nian == 0) return;
	    // 指向第一页
	    this.document.selfTaxForm.paginationDbAction.value = 'First';
	    selfTaxForm.action = "/gz/gz_self/tax/selftaxshow.do?b_query=link&timefield=s&action=query";
	    selfTaxForm.submit();
	}
	
	function seletetaxdate() {
	    var nian = selfTaxForm.tax_date.value;
	    if (nian == 0) return;
	    // 指向第一页
	    this.document.selfTaxForm.paginationDbAction.value = 'First';
	    selfTaxForm.action = "/gz/gz_self/tax/selftaxshow.do?b_query=link&action=query";
	    selfTaxForm.submit();
	}
	
	function showall() {
	    // 指向第一页
	    this.document.selfTaxForm.paginationDbAction.value = 'First';
	    selfTaxForm.action = "/gz/gz_self/tax/selftaxshow.do?b_query=link";
	    selfTaxForm.submit();
	}

	function adv_format(value,num)   //四舍五入
    {
    var a_str = formatnumber(value,num);
    var a_int = parseFloat(a_str);
    if (value.toString().length>a_str.length)
        {
        var b_str = value.toString().substring(a_str.length,a_str.length+1)
        var b_int = parseFloat(b_str);
        if (b_int<5)
            {
            return a_str
            }
        else
            {
            var bonus_str,bonus_int;
            if (num==0)
                {
                bonus_int = 1;
                }
            else
                {
                bonus_str = "0."
                for (var i=1; i<num; i++)
                    bonus_str+="0";
                bonus_str+="1";
                bonus_int = parseFloat(bonus_str);
                }
            a_str = formatnumber(a_int + bonus_int, num)
            }
        }
        return a_str;
    }

	function formatnumber(value,num)    //直接去尾
    {
    var a,b,c,i
    a = value.toString();
    b = a.indexOf('.');
    c = a.length;
    if (num==0)
        {
        if (b!=-1)
            a = a.substring(0,b);
        }
    else
        {
        if (b==-1)
            {
            a = a + ".";
            for (i=1;i<=num;i++)
                a = a + "0";
            }
        else
            {
            a = a.substring(0,b+num+1);
            for (i=c;i<=b+num;i++)
                a = a + "0";
            }
        }
    return a;
    }

</script>
<html:form action="/gz/gz_self/tax/selftaxshow">
	<%
		SelfTaxForm stf = (SelfTaxForm) session.getAttribute("selfTaxForm");
		int len = stf.getFieldlist().size() + 1;
		UserView uv = stf.getUserView();
		String b0100 = uv.getUserOrgId();
		String a0101 = uv.getUserFullName();
		String e0122 = uv.getUserDeptId();
		b0100 = AdminCode.getCodeName("UN", b0100);
		e0122 = AdminCode.getCodeName("UM", e0122);
		if (e0122 == null) {
			e0122 = "";
		}
	%>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" >
		<tr>
			<td align="left" width="300" nowrap>
				<logic:equal value="0" name="selfTaxForm" property="ymd">
					<input type="radio" name="r" value="4" onclick="showall();" /><bean:message key="hire.jp.pos.all"/>
	
		<input type="radio" name="r" value="1" onclick="showanian();" />
					<bean:message key="id_factory.loop_year" />
					<input type="radio" name="r" value="2" onclick="showataxdate();" /><bean:message key="system.setstate.m"/>
		<input type="radio" name="r" value="3" checked="true"
						onclick="showatimefield();" />
					<bean:message key="kq.datewidth" />
				</logic:equal>
				<logic:equal value="1" name="selfTaxForm" property="ymd">
					<input type="radio" name="r" value="4" onclick="showall();" /><bean:message key="hire.jp.pos.all"/>
	
		<input type="radio" name="r" value="1" checked="true"
						onclick="showanian();" />
					<bean:message key="id_factory.loop_year" />
					<input type="radio" name="r" value="2" onclick="showataxdate();" /><bean:message key="system.setstate.m"/>
		<input type="radio" name="r" value="3" onclick="showatimefield();" />
					<bean:message key="kq.datewidth" />
				</logic:equal>
				<logic:equal value="2" name="selfTaxForm" property="ymd">
					<input type="radio" name="r" value="4" onclick="showall();" /><bean:message key="hire.jp.pos.all"/>
	
		<input type="radio" name="r" value="1" onclick="showanian();" />
					<bean:message key="id_factory.loop_year" />
					<input type="radio" name="r" value="2" checked="true"
						onclick="showataxdate();" /><bean:message key="system.setstate.m"/>
		<input type="radio" name="r" value="3" onclick="showatimefield();" />
					<bean:message key="kq.datewidth" />
				</logic:equal>
				<logic:equal value="3" name="selfTaxForm" property="ymd">
					<input type="radio" name="r" value="4" checked="true"
						onclick="showall();" /><bean:message key="hire.jp.pos.all"/>
	
		<input type="radio" name="r" value="1" onclick="showanian();" />
					<bean:message key="id_factory.loop_year" />
					<input type="radio" name="r" value="2" onclick="showataxdate();" /><bean:message key="system.setstate.m"/>
		<input type="radio" name="r" value="3" onclick="showatimefield();" />
					<bean:message key="kq.datewidth" />
				</logic:equal>
			</td>
			<td>
				<div id="anian" style="display=none">
					<bean:write name="selfTaxForm" property="selstrnian" filter="false" />
				</div>
				<div id="ataxdate" style="display=none">
					<bean:write name="selfTaxForm" property="selstrtaxdate"
						filter="false" />
				</div>
				<div id="atimefield" style="display=none">
					<bean:message key="label.from" />
					<input type="text" name="startime" extra="editor"
						style="width:100px;font-size:10pt;text-align:left;" id="editor1"
						dropDown="dropDownDate" class="inputtext" value="${selfTaxForm.startime}">
					<bean:message key="kq.init.tand" />
					<input type="text" name="endtime" extra="editor"
						style="width:100px;font-size:10pt;text-align:left;" id="editor1"
						dropDown="dropDownDate" class="inputtext" value="${selfTaxForm.endtime}">
					&nbsp;&nbsp;
					<BUTTON name="tfquery" class="mybutton" onclick="querytf();">
						<bean:message key="button.query" />
					</BUTTON>
				</div>
			</td>
		</tr>
		<tr><td height="5px"></td></tr>
	</table>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable common_border_color" style="border-right: 1px solid;">
		<tr>

			<logic:iterate id="info" name="selfTaxForm" property="fieldlist">
				<bean:define id="sss" name="info" property="itemid" />
				<logic:equal value="a0101" name="info" property="itemid">
					<td align="center" class="TableRow" nowrap>
						<bean:write name="info" property="itemdesc" />
					</td>
				</logic:equal>
				<logic:notEqual value="a0101" name="info" property="itemid">
					<td align="center" class="TableRow" nowrap>
						<bean:write name="info" property="itemdesc" />
					</td>
				</logic:notEqual>
			</logic:iterate>
		</TR>
		<hrms:paginationdb id="element" name="selfTaxForm"
			sql_str="selfTaxForm.sql" table="" where_str="selfTaxForm.where"
			columns="selfTaxForm.column" order_by="selfTaxForm.orderby"
			pagerows="15" page_id="pagination" indexes="indexes">
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

				<logic:iterate id="info" name="selfTaxForm" property="fieldlist">
					<bean:define id="fid" name="info" property="itemid" />
					<bean:define id="ftype" name="info" property="itemtype" />
					<bean:define id="fsetid" name="info" property="fieldsetid" />
					<bean:define id="codesetid" name="info" property="codesetid" />
					<bean:define id="nnn" name="element" property="${fid}" />

					<logic:equal value="N" name="ftype">
						<td align="right" class="RecordRow" nowrap>

							<script type="text/javascript" language="javascript">
		var nns="<%=nnn%>";
		if(nns!=""&&nns.length>0){
		var numbers=parseFloat(nns);
		var s=numbers.toFixed(2);
		document.write(s+"&nbsp;&nbsp;");
		}
		</script>
					</logic:equal>
					<logic:equal value="D" name="ftype">
						<td align="left" class="RecordRow" nowrap>
							<script type="text/javascript" language="javascript">
				var datestr='<bean:write name="element" property="${fid}"/>';
				if(datestr!=null&&datestr.length>10){
					datestr=datestr.substring(0,10);
				}
				document.write("&nbsp;&nbsp;"+datestr);
			</script>
			</logic:equal>
					<logic:notEqual value="N" name="ftype">
						<logic:notEqual value="D" name="ftype">
							<logic:equal value="a0101" name="fid">
								<td align="left" class="RecordRow" nowrap>
							</logic:equal>
							<logic:notEqual value="a0101" name="fid">
								<td align="left" class="RecordRow" nowrap>
							</logic:notEqual>
							<logic:equal value="taxmode" name="fid">
								<bean:define id="fidvalue" name="element" property="taxmode" />
								<hrms:codetoname codeid="${selfTaxForm.modeset}" name="element"
									codevalue="${fid}" codeitem="codeitem" scope="page" />
								&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
			</logic:equal>
							<logic:notEqual value="taxmode" name="fid">
								<logic:equal value="0" name="codesetid">
									<hrms:codetoname codeid="${fsetid}" name="element"
										codevalue="${fid}" codeitem="codeitem" scope="page" />
									<bean:write name="codeitem" property="codename" />&nbsp;
           		</logic:equal>
								<logic:notEqual value="0" name="codesetid">
									<hrms:codetoname codeid="${codesetid}" name="element"
										codevalue="${fid}" codeitem="codeitem" scope="page" />
									&nbsp;&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
           		</logic:notEqual>
								<logic:equal value="" name="codeitem" property="codename">
									<bean:write name="element" property="${fid}" />
								</logic:equal>
							</logic:notEqual>
						</logic:notEqual>
					</logic:notEqual>
					<logic:equal value="a0101" name="fid">
			&nbsp;&nbsp;&nbsp;
		</logic:equal>
					</td>
				</logic:iterate>
			</tr>
		</hrms:paginationdb>
		<tr>
			<td colspan="<%=len%>" align="left" class="RecordRow" nowrap>
				<TABLE>
					<tr>
						<td>
							<bean:message key="gz.gz_acounting.total"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						</td>
						<td>
							<bean:message key="workdiary.message.income.tax.total"/>：
							<bean:define id="sumsdss" name="selfTaxForm" property="sumsds" />
							<script type="text/javascript" language="javascript">
		var nns="<%=sumsdss%>";
		if(nns!=""&&nns.length>0){
		var numbers=parseFloat(nns);
		var s=numbers.toFixed(2);
		document.write(s);
		}
		</script>
							&nbsp;&nbsp;
						</td>
						<TD>
							<bean:message key="workdiary.message.tax.income.tax.Mtotal"/>：
							<bean:define id="absd" name="selfTaxForm" property="sumynse" />
							<script type="text/javascript" language="javascript">
		var ssu="<%=absd%>";
		if(ssu!=""&&ssu.length>0){
		var numbersv=parseFloat(ssu);
		var sy=numbersv.toFixed(2);
		document.write(sy);
		}
		</script>
						</TD>

					</tr>
				</TABLE>
			</td>
		</tr>
	</table>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="selfTaxForm" property="pagination"
						nameId="browseRegisterForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>
	</table>
	<html:hidden name="selfTaxForm" property="ymd" />

</html:form>
<script type="text/javascript" language="javascript">
	var ymds = selfTaxForm.ymd.value;
	if (ymds == 0) {
	    showatimefield();
	} else if (ymds == 1) {
	    showanian();
	} else if (ymds == 2) {
	    showataxdate();
	} else if (ymds == 3) {
	    shownone();
	}
</script>
