<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,java.io.File"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<script type="text/javascript" src="../../module/utils/js/template.js"></script>
<script type="text/javascript">
function getChannelInfo()
	{
       var channelvo=new Object();	
       var name = document.channelForm.name.value;
       name = name.replace(/ /g,"");
       if(name=="")
       {
    	  Ext.showAlert("频道名称不能为空！");return;
       }
       channelvo.name=document.channelForm.name.value;
       channelvo.function_id=document.channelForm.function_id.value;
       if(document.channelForm.visible.checked ==true)
         channelvo.visible="0";
        else{
         channelvo.visible= "1";
       }
       channelvo.visible_type=document.channelForm.visible_type.value; 
       channelvo.icon_url=document.channelForm.icon_url.value;
       channelvo.channel_id=document.channelForm.channel_id;
       channelvo.menu_width=document.channelForm.menu_width.value;
       channelvo.icon_width=document.channelForm.icon_width.value;
       channelvo.icon_height=document.channelForm.icon_height.value;
       channelvo.parent_id=${channelForm.parent_id};
     	Ext.Ajax.request({
			url : '/servlet/ImgPathServlet?img_url='+channelvo.icon_url,
			async:false,
			success : function(response) {
			   var  data = response.responseText;
			    if(data!=null && data != ""){
			       /// Ext.MessageBox.alert("提示",data);
			         alert(data);
			    }
			   
			
			}
		});
       if(Ext.isIE)
       {
		   window.returnValue=channelvo;
  		}else{
	       var flag = document.getElementById("flag").value;
	       if(flag==null||flag==""){
		   		window.opener.add_channel_return(channelvo);
	       }else{
	         	window.opener.edit_channel_return(channelvo);
	       }
  	  	}
	   window.close();		
	}
	
	function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
}
</script>

<body onload="setTPinput()">
<html:form action="/sys/cms/addOrEditChannel">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<br/>
<%
}
%><input type="hidden" value="<bean:write name="channelForm" property="name"/>" id="flag"/>
      <table width="510" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;margin-left:0px;">
          <tr height="20">
       		<td class="TableRow" align="left" style='border-bottom:0px;'>
       		<bean:message key="lable.content.channel"/>&nbsp;
       		</td>              	      
          </tr> 
          <tr>
            <td class="framestyle" align="center">
               <table border="0" cellpmoding="0" cellspacing="2"  cellpadding="0">     
                      <tr>
                	      <td align="right" nowrap ><bean:message key="lable.channel.name"/>:</td>
                	      <td align="left" nowrap >
                	      	<html:text name="channelForm" property="name" size="20" maxlength="250"/>
                          </td>
                      </tr>
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="lable.channel.visible_type"/>:</td>
                	      <td align="left" nowrap >
			                 <html:select name="channelForm" property="visible_type" size="1">
				        	        <html:option value="0">平铺</html:option>
				        	        <html:option value="1">菜单</html:option>
				             </html:select>	
		    	          </td>
                          <html:hidden name="channelForm" property="channel_id"/>
                          <html:hidden name="channelForm" property="parent_id"/>
                      </tr>
                      <tr class="list3">
                	      <td align="right"  nowrap>
                                <bean:message key="lable.channel.menu_width"/>:
                          </td>
                	      <td align="left" nowrap >
                	      	<html:text name="channelForm" property="menu_width" size="20" maxlength="10"/>    	      
                          </td>                          
                      </tr>                       
                      <tr class="list3">
                	      <td align="right"  nowrap>
                                <bean:message key="lable.channel.icon_url"/>:
                          </td>
                	      <td align="left" nowrap >
                	      	<html:text name="channelForm" property="icon_url" size="20" maxlength="250"/>    	      
                          </td>                          
                      </tr>  
                      <tr class="list3">
                	      <td align="right"  nowrap>
                                <bean:message key="lable.channel.icon_width"/>:
                          </td>
                	      <td align="left" nowrap >
                	      	<html:text name="channelForm" property="icon_width" size="20" maxlength="10"/>    	      
                          </td>                          
                      </tr>  
                      <tr class="list3">
                	      <td align="right"  nowrap>
                                <bean:message key="lable.channel.icon_height"/>:
                          </td>
                	      <td align="left" nowrap >
                	      	<html:text name="channelForm" property="icon_height" size="20" maxlength="10"/>    	      
                          </td>                          
                      </tr>                                             
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="lable.channel.hide"/>:</td>
                	      <td align="left" nowrap >
                	      	<html:checkbox name="channelForm" property="visible" value="0"/>    	      
                          </td>
                      </tr>                         
                      <tr class="list3">
                	      <td align="right" nowrap ><bean:message key="lable.channel.function_id"/>:</td>
                	      <td align="left" nowrap >
                	      	<html:text name="channelForm" property="function_id" size="20" maxlength="20"/>    	      
                          </td>
                      </tr>                                                                   
                 </table>     
              </td>
          </tr>                                                   
          <tr class="list3">
            <td align="center" colspan="2" style="padding-top:5px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getChannelInfo();">
            		<bean:message key="button.save"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>
          </tr>          
      </table>
</html:form>
</body>