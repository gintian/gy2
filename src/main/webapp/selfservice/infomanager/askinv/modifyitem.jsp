<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
function sub()
 {
    var fill=document.getElementById("fill");
    var sl=document.getElementById("sl");
    if(!fill.checked)
    { 
       fill.value="0";
       fill.checked=true;
    }
    if(sl)
    {
       if(sl.checked)
       {
           var max=document.getElementById("max");
           var min=document.getElementById("min");
           var myReg =/^(\d+)$/;
           if(trim(max.value)!=''&&!myReg.test(max.value))
           {
                alert("最大选择数量请输入数字！");
                return false;
           }
           if(trim(min.value)!=''&&!myReg.test(min.value))
           {
                alert("最小选择数量请输入数字！");
                return false;
           }
           if(max.value*1<min.value*1)
           {
           
             alert("最大选择数量不能小于最小选择数量！");
             return false;
           }
       }
    }
    if(sl)
    {
       if(!sl.checked)
       {
          sl.value="0";
          sl.checked=true;
          document.getElementById("max").value="";
          document.getElementById("min").value="";
       }
    }
    return true;
 
 }
 function isVisible(type)
 {
    if(type=='2')
    {
    var trl=document.getElementById("trl");
    if(trl)
     {
        trl.style.display="block";
     }
    }
    else
    {
         var trl=document.getElementById("trl");
         var sl=document.getElementById("sl");
         if(trl)
         {
           trl.style.display="none";
           var vrl = document.getElementById("vrl");
           vrl.style.display="none";
           var minvrl = document.getElementById("minvrl");
           minvrl.style.display="none";
         }
          if(sl)
            sl.checked=false;
    }
    
    }
    function visibleValue(obj)
    {
       var vrl = document.getElementById("vrl");
       if(obj.checked)
          vrl.style.display="block";
       else
          vrl.style.display="none";
       var minvrl = document.getElementById("minvrl");
       if(obj.checked)
          minvrl.style.display="block";
       else
          minvrl.style.display="none";
         
    }
    function check(){
        var pointName = document.getElementById("pointName").value;
        if(pointName.length>100){
            alert(POINT_NAME_LENGTH);
            return false;
        }
        return true;
    }
//-->
</script>
<html:form action="/selfservice/infomanager/askinv/additem">
	<table width="80%" border="0" cellspacing="0" style="margin-top:6px;" align="center" cellpadding="0" >
	<tr>
		<td align="center" valign="center" nowrap colspan="10" class="educationtitle">
			<img src="/images/shimv.gif">&nbsp;<bean:message key="conlumn.investigate_item.maintopic"/>&nbsp;<img src="/images/shimv1.gif">
		</td>
	</tr>
	</table>
      <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="30">
       		<td align="left" colspan="2" class="TableRow"><bean:message key="lable.investigate_item.repair"/>&nbsp;</td>
          </tr> 
                     <tr ><td align="right" width="40%" nowrap valign="center"><bean:message key="conlumn.investigate.questionName"/></td>
                     <td>
                      <bean:write name="itemForm" property="content" filter="true"/>&nbsp;
                                        
                     </td> </tr>   

                      <tr >
                	      <td align="right" nowrap valign="center"> <bean:message key="conlumn.investigate.question"/></td>
                	      <td align="left"  nowrap>
                	      	<html:text styleId="pointName" name="itemForm" property="itemvo.string(name)" size="20" maxlength="250" styleClass="text4" style="width:400px;"/>
                          </td>
                      </tr> 
                      <tr >
                	      <td align="right" nowrap valign="center"><bean:message key="lable.investigate_item.type"/></td>
                	      <td align="left"  nowrap>
                	        <html:radio disabled="true" onclick="isVisible('0')" name="itemForm" property="itemvo.string(status)" value="0"/><bean:message key="lable.investigate_item.singlesubject"/>
                	        <html:radio disabled="true" onclick="isVisible('1')" name="itemForm" property="itemvo.string(status)" value="1"/><bean:message key="lable.investigate_item.asked"/>
                	        <html:radio disabled="true" onclick="isVisible('2')" name="itemForm" property="itemvo.string(status)" value="2"/><bean:message key="lable.investigate_item.multinselected"/>
                              </td>
                      </tr> 
                       <tr >
                	      <td align="right" nowrap valign="center"> <bean:message key="lable.investigate_item.fillflag"/></td>
                	      <td align="left"  nowrap>
							<html:checkbox name="itemForm" styleId="fill" property="itemvo.string(fillflag)" value="1"/>
						  </td>
                      </tr>
                       <logic:equal value="2" name="itemForm" property="itemvo.string(status)">
                      <tr id="trl" style="display=block">
                          <td align="right" nowrap valign="center"> 是否控制选择项目数量</td>
                          <td align="left"  nowrap>
                            <html:checkbox onclick="visibleValue(this)" name="itemForm" property="itemvo.string(selects)" styleId="sl" value="1"/>
                          </td>
                      </tr> 
                      </logic:equal>
                       <logic:notEqual value="2" name="itemForm" property="itemvo.string(status)">
                        <tr id="trl" style="display=none">
                          <td align="right" nowrap valign="center"> 是否控制选择项目数量</td>
                          <td align="left"  nowrap>
                            <html:checkbox onclick="visibleValue(this)" name="itemForm" property="itemvo.string(selects)" styleId="sl" value="1"/>
                          </td>
                      </tr> 
                       </logic:notEqual> 
                       <logic:equal value="1" name="itemForm" property="itemvo.string(selects)">
                       <tr  id="vrl" style="display=block">
                          <td align="right" nowrap valign="center">最大选择数量
                          </td>
                          <td align="left"  nowrap>
                            <html:text styleId="max" name="itemForm" property="itemvo.string(maxvalue)" size="4"/>
                          </td>
                      </tr>
                      <tr  id="minvrl" style="display=block">
                          <td align="right" nowrap valign="center">最小选择数量
                          </td>
                          <td align="left"  nowrap>
                             <html:text styleId="min" name="itemForm" property="itemvo.string(minvalue)" size="4"/>
                          </td>
                      </tr>  
                       </logic:equal>
                        <logic:equal value="0" name="itemForm" property="itemvo.string(selects)">
                       <tr  id="vrl" style="display=none">
                          <td align="right" nowrap valign="center">最大选择数量
                          </td>
                          <td align="left"  nowrap>
                            <html:text styleId="max" name="itemForm" property="itemvo.string(maxvalue)" size="4"/>
                          </td>
                      </tr>
                      <tr  id="minvrl" style="display=none">
                          <td align="right" nowrap valign="center">最小选择数量
                          </td>
                          <td align="left"  nowrap>
                             <html:text styleId="min" name="itemForm" property="itemvo.string(minvalue)" size="4"/>
                          </td>
                      </tr>  
                       </logic:equal>
                       
                      <tr>
                      	<td colspan="2" align="center" style="height:35px;">
         				<hrms:submit styleClass="mybutton" property="b_save" onclick="document.itemForm.target='_self';validate( 'R','itemvo.string(name)','项目名称');return (document.returnValue && check() && sub() && ifqrbc());">
            				<bean:message key="button.save"/>
	 					</hrms:submit>
						<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
                        </td>
                      </tr>
                  
         
      </table>
	

<%
	int i=0;
%>
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">

   	  <thead>
   	 
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'topicForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
               <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_item.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_item.fillflag"/>&nbsp;
	    </td>
           
	   	    	  <td align="center" class="TableRow" nowrap>
		是否控制项目选择数量&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		最大选择数量&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		最小选择数量&nbsp;
	    </td>    	    
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	      <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.questionItem"/>&nbsp;
	    </td>
           	    	    		        	        	        
           </tr>
   	  </thead>
   	  
          <hrms:extenditerate id="element" name="itemForm" property="itemForm.list" indexes="indexes"  pagination="itemForm.pagination" pageCount="10" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="itemForm" property="itemForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
                    
            <td align="left" class="RecordRow" nowrap width="200" style="word-break:break-all">
                  <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
            
                <td align="center" class="RecordRow" nowrap>
                  <bean:write name="element" property="string(fillflag)" filter="true"/>&nbsp;
	    </td>
            <td align="center" class="RecordRow" nowrap>
	         <bean:write name="element" property="string(selects)" filter="true"/>&nbsp;
	        </td>
	         <td align="center" class="RecordRow" nowrap>
	          <bean:write name="element" property="string(maxvalue)" filter="true"/>&nbsp;
	        </td>
	         <td align="center" class="RecordRow" nowrap>
	          <bean:write name="element" property="string(minvalue)" filter="true"/>&nbsp;
	        </td>
	        <%
	        	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	        	String itemid = vo.getString("itemid");
	        	String name = vo.getString("name");
	         %>
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/additem.do?b_query=link&encryptParam=<%=PubFunc.encrypt("itemid="+itemid)%>"><img src="/images/edit.gif" border=0></a>
	    </td>
	      <td align="center" class="RecordRow" nowrap>
	      	<logic:notEqual name="element" property="string(status)" value="1">
            	<a href="/selfservice/infomanager/askinv/addoutline.do?b_addquery=link&encryptParam=<%=PubFunc.encrypt("itemid="+itemid+"&itemName="+name)%>" ><img src="/images/edit.gif" border=0></a>
	    	</logic:notEqual>
	    </td>
           	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
					<bean:write name="itemForm" property="itemForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="itemForm" property="itemForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="itemForm" property="itemForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="itemForm" property="itemForm.pagination"
				nameId="itemForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="document.itemForm.target='_self';validate( 'R','itemvo.string(id)','项目名称');return (document.returnValue && ifdel());">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_addquery">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
        
            </td>
           
          </tr>          
</table>

</html:form>

