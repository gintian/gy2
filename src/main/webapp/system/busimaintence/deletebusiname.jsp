<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
function saveSort(setd){ 
	var left_vo,vos;
	vos= document.getElementsByName(setd);
	  if(vos==null)
	  		return false;
	  left_vo=vos[0];
	  var set="";
	  var num=0;
	  	for(i=0;i<left_vo.options.length;i++)
	  		{
	  	 		if(left_vo.options[i].selected)
		    		{
		    			set+="/"+left_vo.options[i].value;
		    			num++;
		    			
		    		}
	  		}
	  		if(num==0)
	  		{
	  		 alert(KJG_YWZD_INFO9); 
	  		 return;     
	  		}
	  		if(confirm(KJG_YWZD_INFO10)){
	  			// var userType = document.getElementById("userType").value;
	  			var userType = document.getElementsByName("userType")[0].value;
	  			var id = document.getElementsByName("id")[0].value;
	  			// var id = document.getElementById("id").value;
	  			var hashvo=new ParameterSet();
	  			hashvo.setValue("set",set.substring(1));
	  			hashvo.setValue("userType",userType);
	  			hashvo.setValue("id",id);
	  			var request=new Request({method:'post',asynchronous:false,onSuccess:showOut,functionId:'1010061024'},hashvo);
	  		}	
}
function showOut(){
	//busiMaintenceForm.action="/system/busimaintence/showbusiname.do?b_delete=link";
    //busiMaintenceForm.submit();
    // window.returnValue="aaaa";
    // window.close();
	parent.parent.return_vo = 'aa';
	winClose()
}
function winClose() {
	if(parent.parent.Ext.getCmp('voider')){
        parent.parent.Ext.getCmp('voider').close();
	}
}
</script>
<base target="_self">
<html:form action="/system/busimaintence/showbusiname">
<center>
<table width="390" height="300" border="0" align="center" cellpadding="0" cellspacing="0">
	<tr>
		<td>
			<html:hidden name="busiMaintenceForm" property="userType"/>
			<html:hidden name="busiMaintenceForm" property="id"/>
		</td>
	</tr>
  <tr> 
    <td>
    	<fieldset style="width:100%;height:300">
    	<legend><bean:message key='kjg.title.deledb'/></legend>
    	<table width="100%"  border="0" align="center">
    	<tr>
    	<tr>
    		<td width="100%">
    			<html:select disabled="true" name="busiMaintenceForm" property="setname" size="1"  style="height:240px;width:100%;font-size:10pt">
                         <html:optionsCollection property="subsyslist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    	</tr>
    	<tr>
    		<td width="100%">
    			<html:select name="busiMaintenceForm" property="busfields" multiple="multiple" style="height:209px;width:100%;font-size:9pt" >
                           <html:optionsCollection property="busitablelist" value="dataValue" label="dataName"/>
                      </html:select>
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
  <tr>
    	<td align="center" height="35px;">
    	<input type="button" value="<bean:message key='button.delete'/>" onclick=saveSort('busfields') Class="mybutton">
    	<%--<input type="button" value="<bean:message key='button.close'/>" onclick="window.close();" Class="mybutton"></td>--%>
    	<input type="button" value="<bean:message key='button.close'/>" onclick="winClose();" Class="mybutton"></td>
  </tr>
</table>
</center>
</html:form>
<script language="JavaScript">
	if(!getBrowseVersion() || getBrowseVersion() == '10'){
	    document.getElementsByName('setname')[0].style.height = '30px';
	}
</script>