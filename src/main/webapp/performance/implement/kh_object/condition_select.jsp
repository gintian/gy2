 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<style>
<%
	String datebase=request.getParameter("datebase");
	String showdb = request.getParameter("showdb");
	String callBackFunc = "";
    if(request.getParameter("callbackfunc")!=null){
        callBackFunc = request.getParameter("callbackfunc");
    }else if(request.getParameter("callBackFunc")!=null){
        callBackFunc = request.getParameter("callBackFunc");
    }else if(request.getParameter("callbackFunc")!=null){
        callBackFunc = request.getParameter("callbackFunc");
    }
%>
.RecoRowConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
.RecoRowConition1
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
 function change(value)
{
 	var hashvo=new ParameterSet();
 	hashvo.setValue("tableName",value);
	var request=new Request({asynchronous:false,
     		onSuccess:changesItem,functionId:'9023000101'},hashvo);
}
function changesItem(outparamters)
{
	var fieldList=outparamters.getValue("fieldList");
	AjaxBind.bind(implementForm.left_fields,fieldList);		
}
function next()
{
	var items = document.implementForm.right_fields.options; 
	if(items.length==0)
	{
		alert(GENERAL_SELECT_ITEMNAME+"!");
		return;
	}
  var isHistory='0';
  for(i=0;i<items.length;i++)
  {
	var tableName = items[i].value.split('<@>');
	if(tableName[4]!='A01')
		isHistory='1';  
  }
  	setselectitem('right_fields');
	implementForm.action='/performance/implement/kh_object/select_next.do?b_next=link&isHistory='+isHistory+'&db=${param.db}&showdb=<%=showdb %>&datebase=<%=datebase %>&callBackFunc=<%=callBackFunc%>';
	implementForm.submit();
	if(!window.showModalDialog) {
        window.resizeTo(600, 530);
    }
}

//覆盖validate.js中的同名方法，将"对象"改为"指标" lium
function removeitem(sourcebox_id) {
	var vos, right_vo, i;
	var isCorrect = false;
	vos= document.getElementsByName(sourcebox_id);
	if(vos==null) {return false;}
	right_vo = vos[0];
	for(i = right_vo.options.length - 1; i >= 0; i--) {
		if(right_vo.options[i].selected) {
			right_vo.options.remove(i);
			isCorrect = true;
	  }
	}
	if(!isCorrect) {
		alert("请选择需要操作的指标！");
		return false;
	}
	return true;	  	
}
</script> 
<html:form action="/performance/implement/kh_object/condition_select">

  
<table width="540px" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable complex_border_color">
   	  <thead>
           <tr>
	            <td align="center" class="TableRow common_border_color" nowrap >
					<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
	            </td>
       	        	        	        
           </tr>
   	  </thead>
   	  <%if(datebase!=null&&datebase.equals("1")&&!"0".equals(showdb)){ %>
            <tr>
	               <td align="left" class="RecoRowConition1 common_border_color" nowrap>
	               	&nbsp;&nbsp;人员库
				     	<hrms:optioncollection name="implementForm" property="dblist" collection="list" />
				             <html:select name="implementForm" property="dbpre" size="1" >
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select> 
                   </td>     
       	        	        	        
           </tr>
       <%} %>    	 
   	   <tr>
            <td width="100%" align="center" class="RecoRowConition common_border_color" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                     <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                    <td align="center">
					 <html:select name="implementForm" property="setname" size="1"  onchange="change(this.value);" style="width:100%">
           				<html:optionsCollection property="tablelist" value="dataValue" label="dataName"/>
    				 </html:select>
                    </td>
                    </tr>
                   <tr>
                    <td align="center">
                      <html:select name="implementForm" property="left_fields" multiple="true" style="height:216px;width:100%;font-size:9pt" ondblclick="additem('left_fields','right_fields');">
                           <html:optionsCollection property="leftlist" value="dataValue" label="dataName"/>
                      </html:select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                
                <td width="46%" align="center">
                 
                 
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="implementForm" property="right_fields" size="10" multiple="true" style="height:247px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');">
     	             	<html:optionsCollection property="rightlist" value="dataValue" label="dataName"/>
                     </html:select>   
                  </td>  
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
	          <td align="center" class="RecoRowConition common_border_color" nowrap  style="height:35px">                           	                    
	       			 <input type='button'  class="mybutton"  value='<bean:message key="button.query.next"/>' onclick='next()' />
	          </td>
          </tr>
</table>
</html:form>
 