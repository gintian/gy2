<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">	
   function getBusinessTemplate(param)
{
	var obj=(eval("document.pparameterForm."+param));
      var select_id=obj.value;
      var t_url="/dtgh/party/person/party_parameter.do?b_template=link&type=1&dr=1&select_id="+select_id;
	  var return_vo= window.showModalDialog(t_url,'rr',"dialogWidth:356px; dialogHeight:446px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");
        if(!return_vo)
            return false;
         else
          {
             if(return_vo.flag=="true")
             {
                var afield=eval('fieldIds'+param);   
                var name=return_vo.title;
                var ids=return_vo.content;
                var a_name=name.split(",");
                var a_ids=ids.split(",");
                var bnames="&nbsp;";
                var ids="";
                for(var i=0;i<a_ids.length;i++)
                {
                   if(a_ids[i]==null||a_ids[i]=='')
                   continue;
                   bnames+=a_ids[i]+":"+a_name[i]+"<br>";
                   ids+=","+a_ids[i];
                }
                if(ids.length>0)
                    ids=ids.substring(1);
                 if(bnames=='')
                     bnames="&nbsp;&nbsp;";
                 afield.innerHTML=bnames;
                 obj.value=ids;
             }
             
          }      
}
</script>

<style>
  div {height: 25px;}
</style>
<html:form action="/dtgh/party/person/party_parameter">
<logic:equal value="party" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="add"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="out"/>
</logic:equal>
<logic:equal value="preparty" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="add"/>
	<html:hidden name="pparameterForm" property="up"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="out"/>
</logic:equal>
<logic:equal value="important" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="add"/>
	<html:hidden name="pparameterForm" property="up"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="out"/>
</logic:equal>
<logic:equal value="active" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="add"/>
	<html:hidden name="pparameterForm" property="up"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="out"/>
</logic:equal>
<logic:equal value="application" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="add"/>
	<html:hidden name="pparameterForm" property="up"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="out"/>
</logic:equal>
<logic:equal value="member" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="add"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="out"/>
</logic:equal>
<logic:equal value="person" name="pparameterForm" property="param">
	<html:hidden name="pparameterForm" property="up"/>
	<html:hidden name="pparameterForm" property="leave"/>
	<html:hidden name="pparameterForm" property="resumeparty"/>
	<html:hidden name="pparameterForm" property="iin"/>
	<html:hidden name="pparameterForm" property="resumemember"/>
</logic:equal>
<br>
<table width="70%" border="0" cellpadding="0" cellspacing="0" align="center">
      <tr align="center">
		<td valign="center" class="TableRow"  colspan="2" style="border-bottom:0px;">
		<logic:equal value="party" name="pparameterForm" property="param">
		  <bean:message key="dtgh.party.setup.bus.party"/>
		</logic:equal>
		<logic:equal value="preparty" name="pparameterForm" property="param">
		  <bean:message key="dtgh.party.setup.bus.preparty"/>
		</logic:equal>
		<logic:equal value="important" name="pparameterForm" property="param">
		  <bean:message key="dtgh.party.setup.bus.important"/>
		</logic:equal>
		<logic:equal value="active" name="pparameterForm" property="param">
		  <bean:message key="dtgh.party.setup.bus.active"/>
		</logic:equal>
		<logic:equal value="application" name="pparameterForm" property="param">
		  <bean:message key="dtgh.party.setup.bus.application"/>
		</logic:equal>
		<logic:equal value="member" name="pparameterForm" property="param">
		 <bean:message key="dtgh.party.setup.bus.member"/>
		</logic:equal>
		<logic:equal value="person" name="pparameterForm" property="param">
		  <bean:message key="dtgh.party.setup.bus.person"/>
		</logic:equal>
		</td>
	 </tr> 
	 <tr>
	 <td class="framestyle">
	 <table width="100%" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" align="center">
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
     		</tr>
     		<br>
     		<br>
     		<logic:equal value="party" name="pparameterForm" property="param">
     		<hrms:priv func_id="350410000">
           <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="button.insert"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsadd' style="top:inherit">
		            	&nbsp;${pparameterForm.addview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("add");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410001">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.leaveparty"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsleave'  style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410002">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.in"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410003">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.out"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsout'  style="top:inherit">
		            	&nbsp;${pparameterForm.outview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("out");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
		   <logic:equal value="preparty" name="pparameterForm" property="param">
		   <hrms:priv func_id="350410010">
            <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="button.insert"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsadd' style="top:inherit">
		            	&nbsp;${pparameterForm.addview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("add");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
		    <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410011">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.uppreparty"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsup'  style="top:inherit">
		            	&nbsp;${pparameterForm.upview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("up");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr>
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410012">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.leave"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsleave'  style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410013">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.in"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410014">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.out"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsout'  style="top:inherit">
		            	&nbsp;${pparameterForm.outview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("out");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
		   <logic:equal value="important" name="pparameterForm" property="param">
		   <hrms:priv func_id="350410020">
            <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="button.insert"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsadd' style="top:inherit">
		            	&nbsp;${pparameterForm.addview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("add");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
		    <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410021">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.upimportant"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsup'  style="top:inherit">
		            	&nbsp;${pparameterForm.upview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("up");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr>
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410022">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.leave"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsleave'  style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410023">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.in"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410024">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.out"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsout'  style="top:inherit">
		            	&nbsp;${pparameterForm.outview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("out");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
		   <logic:equal value="active" name="pparameterForm" property="param">
		   <hrms:priv func_id="350410030">
            <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="button.insert"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsadd' style="top:inherit">
		            	&nbsp;${pparameterForm.addview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("add");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
		    <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410031">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.upactive"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsup'  style="top:inherit">
		            	&nbsp;${pparameterForm.upview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("up");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr>
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410032">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.leave"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsleave'  style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410033">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.in"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410034">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.out"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsout'  style="top:inherit">
		            	&nbsp;${pparameterForm.outview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("out");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
		   <logic:equal value="application" name="pparameterForm" property="param">
		   <hrms:priv func_id="350410040">
            <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="button.insert"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsadd' style="top:inherit">
		            	&nbsp;${pparameterForm.addview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("add");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
		    <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410041">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.upapplication"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsup'  style="top:inherit">
		            	&nbsp;${pparameterForm.upview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("up");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr>
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410042">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.leave"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsleave'  style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410043">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.in"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410044">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.out"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsout'  style="top:inherit">
		            	&nbsp;${pparameterForm.outview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("out");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
		   <logic:equal value="member" name="pparameterForm" property="param">
		   <hrms:priv func_id="350410050">
            <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="button.insert"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsadd' style="top:inherit">
		            	&nbsp;${pparameterForm.addview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("add");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410051">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.leavemember"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsleave'  style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410052">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.in"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410053">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.out"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsout'  style="top:inherit">
		            	&nbsp;${pparameterForm.outview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("out");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
		   <logic:equal value="person" name="pparameterForm" property="param">
		   <hrms:priv func_id="350410060">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.upperson"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsup'  style="top:inherit">
		            	&nbsp;${pparameterForm.upview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("up");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr>
            <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410061">
            <tr> 
		      <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.leave"/>&nbsp; </td>
			  <td  class="RecordRow"  align='left' width='400'>
						<div id='fieldIdsleave' style="top:inherit">
		            	&nbsp;${pparameterForm.leaveview }
		            	</div>
		      </td>
		      <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("leave");'><bean:message key="button.orgmapset"/></button>	
		      </td>
		    </tr> 
		    <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
            </tr>
            </hrms:priv>
            <hrms:priv func_id="350410062">
            <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap>
		            	<bean:message key="dtgh.party.setup.bus.resumeparty"/>&nbsp; 
		            </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsresumeparty'  style="top:inherit">
		            	&nbsp;${pparameterForm.resumepartyview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("resumeparty");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410063">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.iin"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsiin'  style="top:inherit">
		            	&nbsp;${pparameterForm.iinview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("iin");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   <tr height="2">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr>
           </hrms:priv>
           <hrms:priv func_id="350410064">
		   <tr> 
		            <td align="right" valign="middle" height="40" width='200' nowrap><bean:message key="dtgh.party.setup.bus.resumemember"/>&nbsp; </td>
					<td  class="RecordRow"  align='left' width='400'>	
						<div id='fieldIdsresumemember'  style="top:inherit">
		            	&nbsp;${pparameterForm.resumememberview }
		            	</div>
		            </td>
		            <td  valign='middle' >  
		            	&nbsp;&nbsp;<button name="" class="mybutton" onclick='getBusinessTemplate("resumemember");'><bean:message key="button.orgmapset"/></button>	
		            </td>
		   </tr> 
		   </hrms:priv>
		   </logic:equal>
           <tr height="40">
                 <td align="center"  nowrap valign="center" colspan="3"></td>
           </tr> 
      </table>
      </td>
      </tr>
</table>       
     <table  width="100%" align="center" style="margin-top: 5px;">
          <tr>
            <td align="center">
         	  <hrms:submit styleClass="mybutton"  property="b_save_bus" onclick="">
                  <bean:message key="button.ok"/>
	     </hrms:submit> 
	     <input type="submit" name="b_query" value="<bean:message key="button.return"/>" class="mybutton" onclick="">
           &nbsp; &nbsp; &nbsp; &nbsp;
            </td>
          </tr>          
    </table>
</html:form>
