
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/sys_maintence">
<br>
<br>

<hrms:funcpanel height="0" cols="3" width="400" cellspacing="5" title="sys.label.maintence" icon_height="40" icon_width="40">
	<hrms:funcitem url="/system/codemaintence/codetree.do?b_query=link" icon="/images/card_set.gif" label="sys.label.code" function_id="30016C"/>
	<hrms:funcitem url="/system/busimaintence/busimaintence.do?b_query=link" icon="/images/hmuster.gif" label="sys.label.busim" function_id="30016D"/>
	<hrms:funcitem url="/system/busimaintence/showrelatingcode.do?b_query=link&add_flag=0" icon="/images/mb.gif" label="相关代码类维护" function_id="30016F"/>
	
	<hrms:funcitem url="/general/operation/operationmaintence.do?b_query=link" icon="/images/mb.gif" label="sys.label.busiflow" function_id="30016E"/>

</hrms:funcpanel>

<!-- 
---horizontal
		<hrms:splitpanel height="400" width="100%" style="" orientation="horizontal">
			<hrms:splititem
				style="background-color: #CCCCCC;" height="100">
				In the first splititem we will place a cute search
				engine.
				<input type="text"></input>
				<button>Find</button>
			</hrms:splititem>
			<hrms:splitter></hrms:splitter>
			<hrms:splititem
				style="background-color: #FFDDDD;" >sasd
					<hrms:splitpanel height="500" width="100%" style="" orientation="vertical">
						<hrms:splititem
							style="background-color: #CCCCCC;" width="100">
							In the first splititem we will place a cute search
							engine.
							<input type="text"></input>
							<button>Find</button>
						</hrms:splititem>
						<hrms:splitter></hrms:splitter>
						<hrms:splititem
							style="background-color: #FFDDDD;" width="200">
							Second splititem
						</hrms:splititem>
						<hrms:splitter></hrms:splitter>
						<hrms:splititem
							style="background-color: #DDFFBB;">

						</hrms:splititem>
					</hrms:splitpanel>	

			</hrms:splititem>

		</hrms:splitpanel>
---vertical		
-->	

</html:form>
