<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
<% 
if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("close")){
	out.print("per_callback();");
	out.print("per_close();");
}
%>

 function save(){
 	var name = document.getElementById("name").value;	
	if(ltrim(rtrim(name)) == ""){
		alert("<bean:message key='jx.paramset.info1'/>");
		return;
	}
 	perKnowForm.action="/performance/options/perKnowAdd.do?b_save=link&opt=close"; 
 	perKnowForm.target="_self";
 	perKnowForm.submit();
 }
 
 function per_callback() {
	 var thevo=new Object();
	 thevo.flag="true";
	 if(window.showModalDialog){
         parent.window.returnValue=thevo;
	 }else {
	     if(parent.parent.perknow_ok)
 		    parent.parent.perknow_ok(thevo);
	     else if(parent.opener.add_ok)
             parent.opener.add_ok(thevo);
	 } 
 }
 
 function per_close() {
	if(!window.showModalDialog){
	    if(parent.parent.Ext){
            var win = parent.parent.Ext.getCmp('perknow_win');
            if(win) {
                win.close();
            }
        }else{
            parent.window.close();
        }
  	}else{
        parent.window.close();
    }
 }
	 
</script>
<html:form action="/performance/options/perKnowAdd">
<table border="0" cellspacing="0" align="center" cellpadding="2">

			<tr>
						<td align="center" nowrap>
							<fieldset align="center" style="width:360px;">
							<legend align="center" style="text-align: center;">
									了解程度维护
							</legend>
						
		<table border="0" cellspacing="2" align="left" cellpadding="5">
			<html:hidden name="perKnowForm" styleId="knowId" property="perknowvo.string(know_id)"/>
			<tr>
				<td align="right" nowrap valign="left">
					 &nbsp;<bean:message key='column.name' />
				</td>
				<td align="left" nowrap valign="left">
					<html:text name="perKnowForm" styleId="name" property="perknowvo.string(name)" styleClass="inputtext"/>
				</td>
			</tr>
			<tr>
				<td align="right" nowrap valign="left">
					&nbsp;<bean:message key='kh.field.flag' />
				</td>
				<td align="left" nowrap valign="left">
					<html:select name="perKnowForm" property="perknowvo.string(status)" size="1">
						<html:option value="1">
							<bean:message key='kh.field.yx' />
						</html:option>
						<html:option value="0">
							<bean:message key='kh.field.wx' />
						</html:option>
					</html:select>
				</td>
			</tr>
		</table>
	</fieldset>
							</td>
			</tr>
					</table>
		<table border="0" cellspacing="0" align="center" cellpadding="2">
			<tr>
				<td align="center" colspan="2">
					<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />
					<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="per_close();">  
				</td>
			</tr>
		</table>
	
<script type="text/javascript">
    //非IE下去掉fiedset中框线的标题。 haosl 2019年6月21日
    if(!getBrowseVersion()){
        var legends = document.getElementsByTagName("legend");
        if(legends && legends.length>0){
            legends[0].style.display='none';
        }
    }
</script>
</html:form>
