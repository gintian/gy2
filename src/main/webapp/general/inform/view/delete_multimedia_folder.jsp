<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
 	function init()
	{ 
		var rightFields=$('multimedia');
		if(rightFields.options.length>0)
		{
			rightFields.options[0].selected=true;
		}
	}
	function sub()
	{
		var rightFields=$('multimedia');
		var num=0;
		var a_value="";
		for(var i=0;i<rightFields.options.length;i++)
		{
			if(rightFields.options[i].selected==true)
			{
				num++;
				a_value=rightFields.options[i].value;
			}
		}
		if(num>1)
		{
			alert(EVERY_SELECT_FIELD);
			return ;
		}	
		if(num==1)
		{
			var multimediaflag = "${mInformForm.multimediaflag}";
			var dbname = "${mInformForm.dbname}";
			var kind = "${mInformForm.kind}";
			var hashvo=new ParameterSet();
			hashvo.setValue("dbname",dbname);  
			hashvo.setValue("kind",kind);  
    		hashvo.setValue("multimediaflag",multimediaflag);
			hashvo.setValue("tomultimediaflag",a_value);  
   			var request=new Request({asynchronous:false,functionId:'1010094009'},hashvo); 			
//			var thecodeurl = "/general/inform/emp/view/delete_multimedia_folder.do?b_query=link&tomultimediaflag="+a_value;
// 			var return_vo = window.showModalDialog(thecodeurl, "", "dialogWidth:450px; dialogHeight:400px;resizable:yes;center:yes;scroll:yes;status:yes");
	    	window.close();
		}
		
	}
</script>
	
<html:form action="/general/inform/emp/view/to_delete_multimedia_folder">
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" >
 		<thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
				<bean:message key="general.mediainfo.folder.move"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	   </thead>
   	   <tr>
       <td width="100%" align="center"  nowrap>
           <hrms:optioncollection name="mInformForm" property="multimedialist" collection="list"/>
              <html:select name="mInformForm" size="10" property="multimedia" multiple="multiple" style="height:265;width:100%;font-size:9pt">
              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
              </html:select>	           
      </td>
      </tr>
       <tr>
       <td width="100%" align="center"  nowrap>
            <html:button styleClass="mybutton" property="b_deletefolder" onclick="sub();">
		  		<bean:message key="button.ok"/>
			</html:button>	            
      </td>
      </tr>
          
</table>
<html:hidden name="mInformForm" property="multimediaflag"/>
</html:form>
<script language="javascript">
 	init();
</script>