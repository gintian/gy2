<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hjsj.hrms.actionform.sys.cms.ChannelForm"%>

<link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />
<script type="text/javascript" src="../../ext/ext-all.js" ></script>
<script type="text/javascript" src="../../ext/ext-lang-zh_CN.js" ></script> 
<script type="text/javascript" src="../../ext/rpc_command.js"></script>
<script type="text/javascript">
//<!--
function getChannelInfo()
	{
       var channelvo=new Object();
       channelvo.parent_id=document.channelForm.parent_id.value;
       var temp="";
       for(var i=0;i<document.channelForm.right_fields.options.length;i++){
         temp += document.channelForm.right_fields.options[i].value+"/";
         }
       channelvo.right_fields = temp;
       channelvo.isTop = document.channelForm.isTop.value;
       if(Ext.isChrome)
       {
	       window.opener.change_sort_return(channelvo);  
 	   }else{
	       window.returnValue=channelvo;
 	   }
	   window.close();		
	}

//-->
</script>
<html:form action="/sys/cms/channelMoveList">

<div id="first" style="filter:alpha(Opacity=100);display=block;">
<table width="510px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td class="TableRow" align="left" nowrap>
		<bean:message key="button.change_channel_sort"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	  <td class="RecordRow">
   	  <table>
   	  <tr>
   	  <td>
   	            <html:select name="channelForm" property="right_fields" multiple="multiple" size="10" style="height:230px;width:100%;font-size:9pt">
                      <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                    </html:select>   	     
   	   </td>
   	  
   	    <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
             </td>      
   	  </tr>
   	  </table>
   	  </td>
   	  </tr>
          <tr>
          <td align="center"  nowrap  colspan="3" style="padding-top:5px;">
              <html:button styleClass="mybutton" property="b_savemove" onclick="getChannelInfo();">
            		      <bean:message key="button.save"/>
	      </html:button> 	       
          </td>
          </tr> 
          <tr> <td><html:hidden name="channelForm" property="parent_id"/></td>
               <td><html:hidden name="channelForm" property="isTop"/></td></tr>  
</table>
</div>
</html:form>
