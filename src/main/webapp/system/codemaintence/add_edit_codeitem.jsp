<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script type="text/javascript">
<!--
	function getCodeitemInfo()
	{	
       var codeitemvo=new Object();	
       var maxlen='<bean:write name="codeMaintenceForm" property="len"/>';
       var maxint=parseInt(maxlen);
       var parentid=$F('codeitemvo.string(parentid)');
       codeitemvo.codesetid=$F('codeitemvo.string(codesetid)');
       var codeid=$F('codeitemvo.string(codeitemid)');
       if(codeid==null){
       	return null;
       }
       if(codeid.length<maxint){
       
       		alert("<bean:message key="codemaintence.code.length.reqire"/>"+maxlen+'<bean:message key="codemaintence.code.wei"/>');
       		return;
       }
       if(codeid=="")
       {
         alert("<bean:message key="codemaintence.codeitem.id.error"/>");
         return;
       }
       if(!(IsLetter(codeid)||IsInteger(codeid)))
       {
       	alert("<bean:message key="codemaintence.codeset.inputerror"/>");
       	return;
       }
       var tempcodeitem=parentid+codeid;
       if(parentid.length==30){
        alert('<bean:message key="codemaintence.code.length.upsize"/>');
        window.close();
       }
       if(tempcodeitem.length>30){
       alert('<bean:message key="codemaintence.code.length.limited"/>'+(30-parentid.length)+'<bean:message key="codemaintence.code.wei"/>');
           return;
       }
       
       codeitemvo.codeitemid=parentid+codeid;
       
       codeitemvo.codeitemdesc=$F('codeitemvo.string(codeitemdesc)');
       if(codeitemvo.codeitemdesc==""){
      	 alert("<bean:message key="codemaintence.codeitem.desc.error"/>");
         return;
       }      
       codeitemvo.parentid=$F('codeitemvo.string(parentid)');      
       if(parentid==""){      
       codeitemvo.parentid=$F('codeitemvo.string(codeitemid)');
       }       
       codeitemvo.childid=parentid+codeid; 
	   window.returnValue=codeitemvo;
	   window.close();		
	}
	
function IsLetter(str)      
{      
        if(str.length!=0){     
        reg=/^[a-zA-Z]+$/;      
        if(!reg.test(str)){     
            return false;   
        }     
        } 
        return true;    
}      
      
function IsInteger(str)      
{        
        if(str.length!=0){     
        reg=/^[-+]?\d*$/;      
        if(!reg.test(str)){     
            return false; 
        }     
        }
        return true;     
}      
function estop()
{
	return event.keyCode!=34&&event.keyCode!=39;
}
//-->
</script>

<html:form action="/system/codemaintence/add_edit_codeitem">
	<br>
	<br>
	<table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!--<td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				&nbsp;
				<logic:equal value="0" name="codeMaintenceForm" property="vflag">
					<bean:message key="codemaintence.codeitem.add" />
				</logic:equal>
				<logic:equal value="1" name="codeMaintenceForm" property="vflag">
					<bean:message key="codemaintence.codeitem.update" />
				</logic:equal>
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="350"></td>  -->
			<td align=center class="TableRow">
				&nbsp;
				<logic:equal value="0" name="codeMaintenceForm" property="vflag">
					<bean:message key="codemaintence.codeitem.add" />
				</logic:equal>
				<logic:equal value="1" name="codeMaintenceForm" property="vflag">
					<bean:message key="codemaintence.codeitem.update" />
				</logic:equal>
				&nbsp;
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
			<br/>
				<table border="0" cellpmoding="0" cellspacing="2" class="DetailTable" cellpadding="0">
					<tr class="list3">

						<td align="right" nowrap>
							<bean:message key="codemaintence.codeitem.id" />
							:
						</td>
						<td align="left" nowrap>
							<logic:equal value="1" name="codeMaintenceForm" property="vflag">
								<html:text name="codeMaintenceForm" property="codeitemvo.string(codeitemid)" maxlength="25" size="20" styleClass="text" disabled="true"></html:text>

							</logic:equal>
							<logic:equal value="0" name="codeMaintenceForm" property="vflag">
								<logic:present name="codeMaintenceForm" property="len">
									<input type=text name="codeitemvo.string(codeitemid)" maxlength='<bean:write name="codeMaintenceForm" property="len"/>' size="20" Class="text" />
								(<bean:write name="codeMaintenceForm" property="len"/>
							<bean:message key="codemaintence.code.wei"/>)
								</logic:present>
								<logic:notPresent name="codeMaintenceForm" property="len">
									<html:text name="codeMaintenceForm" property="codeitemvo.string(codeitemid)" maxlength="25" size="20" styleClass="text"></html:text>
								</logic:notPresent>
							</logic:equal>
						</td>
						</tr>
						<tr>
						<td align="right" nowrap>
							<bean:message key="codemaintence.codeitem.desc" />
							:
						</td>

						<td align="left" nowrap>
							<html:text name="codeMaintenceForm" property="codeitemvo.string(codeitemdesc)" maxlength="25" size="20" styleClass="text" onkeypress="event.returnValue=estop(this)"></html:text>

						</td>
					</tr>

					<html:hidden name="codeMaintenceForm" property="codeitemvo.string(codesetid)" />
					<html:hidden name="codeMaintenceForm" property="codeitemvo.string(parentid)" />
					<html:hidden name="codeMaintenceForm" property="codeitemvo.string(childid)" />


				</table>
				<br/>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" style="height:35px;">
				<html:button styleClass="mybutton" property="b_save" onclick="getCodeitemInfo();">
					<bean:message key="button.save" />
				</html:button>
				<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
					<bean:message key="button.close" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>

