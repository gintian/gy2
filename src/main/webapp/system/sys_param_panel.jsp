
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/sys_param_panel">
<br>
<br>
<br>

<hrms:funcpanel height="0" cols="3" width="400" cellspacing="5" title="sys.label.param" icon_height="40" icon_width="40">
	<hrms:funcitem url="/system/options/org_param.do?b_search=link&edition=4&tableset=" icon="/images/jgbm.gif" label="sys.label.b0110" function_id="30015D"/>
	<hrms:funcitem url="/system/options/duty_param.do?b_search=link&edition=4&tableset=" icon="/images/jgbm.gif" label="sys.label.e01a1" function_id="30015E"/>
	<hrms:funcitem url="/system/options/info_param.do?edition=4" icon="/images/card.gif" label="system.options.personlistfieldset" function_id="30015C"/>
	<hrms:funcitem url="/system/options/party_param.do?edition=4&tableset=Y01" icon="/images/jgbm.gif" label="sys.label.party" function_id="30015F"/>
	<hrms:funcitem url="/system/options/corps_param.do?edition=4&tableset=V01" icon="/images/jgbm.gif" label="sys.label.corps" function_id="30015G"/>	
	<hrms:funcitem url="/system/warn/config_manager.do?b_query=link&edition=4" icon="/images/jgbm.gif" label="sys.label.warnset" function_id="080802,300151"/>
	<hrms:funcitem url="/system/id_factory/seq_show.do?b_query=link&edition=4" icon="/images/mc.gif" label="序号维护" function_id="300156"/>
    <hrms:funcitem url="/hire/zp_options/pos_template.do?b_search=link&edition=4" icon="/images/card.gif" label="sys.label.poscardset" function_id="300153"/>
	<hrms:funcitem url="/system/options/param/sys_param.do?b_query=link&edition=4" icon="/images/jgbm.gif" label="sys.label.sysparam" function_id="30015B"/>        
</hrms:funcpanel>
	<!-- 
	<hrms:funcitem url="/system/options/query_template.do?b_query=link" icon="/images/query_set.gif" label="sys.label.queryset" function_id="300150,070101"/>
	<hrms:funcitem url="/system/options/cardconstantset.do?b_cardset=set" icon="/images/salary_set.gif" label="sys.label.salarycardset" function_id="070201,300152"/>
 -->
</html:form>
