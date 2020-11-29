
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function openlink(url)
	{
    	var dbpre=$F("dbpre");
    	url=url+"&dbpre="+dbpre;
    	window.open(url,"_self","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
	}
	function newlink(url)
	{
    	var dbpre=$F("dbpre");
    	url=url+"&dbpre="+dbpre;
    	window.open(url,"_blank","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=yes,resizable=no","true");
	}
//-->
</script>
<style>
<!--
  body{
  	background-color:#DEEAF5;
  }
-->
</style>
<html:form action="/system/home">
<br>
<br>
<html:hidden name="homeForm" property="dbpre"/>
<hrms:viewpanel height="0" cols="2" win_type="1" cellspacing="20">
	<hrms:viewitem name="a" title="公告栏" content="" bcontent="true" index="${homeForm.boardcontentserial}" visible="${homeForm.boardcontentisvisible}" icon_url="/images/blank.png">
	   ${homeForm.boardcontent}
	</hrms:viewitem>
	<hrms:viewitem name="b" title="预警提示" content="" bcontent="true" index="${homeForm.warnserial}" visible="${homeForm.warnisvisible}" icon_url="/images/blank.png">
	   ${homeForm.warn}	
	</hrms:viewitem>
	<hrms:viewitem name="c" title="花名册" content="" bcontent="true" index="${homeForm.musterserial}" visible="${homeForm.musterisvisible}" icon_url="/images/blank.png">
		   ${homeForm.muster} 
	</hrms:viewitem>
	<hrms:viewitem name="d" title="常用查询" content="" bcontent="true" index="${homeForm.condserial}" visible="${homeForm.condisvisible}" icon_url="/images/blank.png">
		   ${homeForm.cond}
	</hrms:viewitem>
	<hrms:viewitem name="e" title="常用统计分析" content="" bcontent="true" index="${homeForm.statserial}" visible="${homeForm.statisvisible}" icon_url="/images/blank.png">
		  ${homeForm.stat}
	</hrms:viewitem>
	<hrms:viewitem name="f" title="常用登记表" content="" bcontent="true" index="${homeForm.ykcardserial}" visible="${homeForm.ykcardisvisible}" icon_url="/images/blank.png">
		   ${homeForm.ykcard}
	</hrms:viewitem>	
	<hrms:viewitem name="g" title="常用报表" content="" bcontent="true" index="${homeForm.reportserial}"  visible="${homeForm.reportisvisible}" icon_url="/images/blank.png">
		   ${homeForm.report}
	</hrms:viewitem>	
	<hrms:viewitem name="h" title="我的任务" content="" bcontent="true" index="${homeForm.matterserial}"  visible="${homeForm.matterisvisible}" icon_url="/images/blank.png">
		   ${homeForm.matter}
	</hrms:viewitem>
	<hrms:viewitem name="i" title="薪资审批" content="" bcontent="true" index="${homeForm.salaryserial}"  visible="${homeForm.salaryisvisible}" icon_url="/images/blank.png">
		   ${homeForm.salary}
	</hrms:viewitem>		
</hrms:viewpanel>
</html:form>
