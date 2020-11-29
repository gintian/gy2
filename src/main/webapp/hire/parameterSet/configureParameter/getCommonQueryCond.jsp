<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/js/constant.js"></script>
<script language="javascript">
var opt='${parameterForm2.optType}';
  function sub()
	{
		var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 returnValue=0;
	    	 window.close();
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
			rightFieldNames+=rightFields.options[i].text+"<br>";
			
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			
			if(n>1)
			{
				alert(a_text+FIELD_NOT_REPEAT);
				return;
			}
		}
		
		var infos=new Array();
		infos[0]=rightFiledIDs.substring(1);
		infos[1]=rightFieldNames;
		//alert(rightFiledIDs.substring(1));
		//alert(rightFieldNames);
   	    returnValue=infos;
   	    if(window.parent.me){//针对新招聘
	  		window.parent.me.setCallBack({returnValue:infos});
		    window.parent.Ext.getCmp('window').close();
   	   	}else
   	   		window.close();
	}
	
</script>
<html:form action="/hire/parameterSet/configureParameter/getCommonQueryCond">
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
<table width='530' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
            <logic:equal value="0" name="parameterForm2" property="optType">
		<bean:message key="hire.standing.querycondition"/>&nbsp;&nbsp;
		</logic:equal>
		   <logic:equal value="1" name="parameterForm2" property="optType">
		<bean:message key="hire.employActualize.resumeState"/>&nbsp;&nbsp;
		</logic:equal>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
   	   <td class="RecordRow" align="center" width="100%" nowrap>
   	   <table width="100%">
   	   <tr>
            <td width="100%" align="center" nowrap>
              <table>
                <tr>
                 <td align="center"  width="42%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="hire.alternative"/><logic:equal value="0" name="parameterForm2" property="optType"><bean:message key="kq.wizard.term"/></logic:equal>&nbsp;&nbsp;
                    </td>
                    </tr>
                   <tr>
                       <td align="center">
                     <hrms:optioncollection name="parameterForm2" property="commonQueryCondlist" collection="list"/>
		              <html:select name="parameterForm2" size="10" property="itemid" multiple="multiple" ondblclick="additem('itemid','right_fields');" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('itemid','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                <td width="42%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="lable.performance.selectedPerMainBody"/><logic:equal value="0" name="parameterForm2" property="optType"><bean:message key="kq.wizard.term"/></logic:equal>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     
 		        <hrms:optioncollection name="parameterForm2" property="selectedCommonQuery" collection="list"/>
		              <html:select name="parameterForm2" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
 		    
 		    
 		    
 		    
 		    
 		     
                 </td>
                  </tr>
                  </table>             
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
            </table>
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap style="padding-top:5px;padding-bottom:3px;">
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>
