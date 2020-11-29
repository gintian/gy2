<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>
<%
	int i=0;
%>
<style>
<!--
.RecordRow {
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 0pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
    font-size: 12px;
    height:22px;
}
.RecordRowLast {
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
    font-size: 12px;
    height:22px;
}
.TableRow {
    background-position : center left;
    font-size: 12px;  
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 0pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    height:22px;
    font-weight: bold;
    background-color:#f4f7f7;   
    /*
    color:#336699;
    */
    valign:middle;
}

.TableRowLast {
    background-position : center left;
    font-size: 12px;  
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    height:22px;
    font-weight: bold;
    background-color:#f4f7f7;   
    /*
    color:#336699;
    */
    valign:middle;
}
.RecordRowP {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    margin-top:-1px;
    height:22;
}
-->
</style>
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
        if(pointName.length>100) {
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
	 <td align="center" valign="center" nowrap colspan="10" class="educationtitle ">
	 <img src="/images/shimv.gif">&nbsp;<bean:message key="conlumn.investigate_item.maintopic"/>&nbsp;
	 <img src="/images/shimv1.gif">
	 </td>
	 </tr>
	</table>
    <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align=left colspan="2" class="TableRow"><bean:message key="lable.investigate_item.repair"/></td><!-- 调查项目维护 -->
          </tr> 
                     <tr>
                      <td align="right" width="40%" nowrap valign="center"><bean:message key="conlumn.investigate.questionName"/></td>
                      <td>
                      <bean:write name="itemForm" property="content" filter="true"/>
                      </td> 
                     </tr>   

                      <tr >
                	      <td align="right" nowrap valign="center"><bean:message key="conlumn.investigate.question"/></td>
                	      <td align="left"  nowrap>
                	      	<html:text styleId="pointName" name="itemForm" property="itemvo.string(name)" style="width:400px;" maxlength="250" styleClass="text4"/>
                          </td>
                      </tr> 
                      <tr >
                	      <td align="right" nowrap valign="center"><bean:message key="lable.investigate_item.type"/></td>
                	      <td align="left"  nowrap>
							<html:radio onclick="isVisible('0')" name="itemForm" property="itemvo.string(status)" value="0"/><bean:message key="lable.investigate_item.singlesubject"/>
							<html:radio onclick="isVisible('1')" name="itemForm" property="itemvo.string(status)" value="1"/><bean:message key="lable.investigate_item.asked"/>
							<html:radio onclick="isVisible('2')" name="itemForm" property="itemvo.string(status)" value="2"/><bean:message key="lable.investigate_item.multinselected"/>
						  </td>
                      </tr> 
                      <tr >
                	      <td align="right" nowrap valign="center"><bean:message key="lable.investigate_item.fillflag"/></td>
                	      <td align="left"  nowrap>
							<html:checkbox name="itemForm" property="itemvo.string(fillflag)" styleId="fill" value="1"/>
						  </td>
                      </tr> 
                      <tr  id="trl" style="display:none">
                	      <td align="right"nowrap valign="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 是否控制选择项目数量</td>
                	      <td align="left"  nowrap>
							<html:checkbox onclick="visibleValue(this)" name="itemForm" property="itemvo.string(selects)" styleId="sl" value="1"/>
						  </td>
                      </tr> 
                      <tr id="vrl" style="display:none">
                	      <td align="right" nowrap valign="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 最大选择数量
                	      </td>
                	      <td align="left"  nowrap>
                	       <html:text styleId="max" name="itemForm" property="itemvo.string(maxvalue)" size="4"/>
						  </td>
                      </tr> 
                       <tr id="minvrl" style="display:none">
                          <td align="right" nowrap valign="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 最小选择数量
                          </td>
                          <td align="left"  nowrap>
                            <html:text styleId="min" name="itemForm" property="itemvo.string(minvalue)" size="4"/>
                          </td>
                      </tr> 
                      <tr >
                      	<td colspan="2" align="center" style="height:35px;">
 	<hrms:submit styleClass="mybutton" property="b_saveadd" onclick="document.itemForm.target='_self';validate('R','itemvo.string(name)','项目名称','RD','first_date.','发布日期','RD','second_date.','结束日期','RI','itemvo.string(days)','调查天数');return (document.returnValue && check() && sub() && ifqrbc());">

            		<bean:message key="button.save"/>
	 	</hrms:submit>
		<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
                      	
                      	</td>
                      </tr>                      
      </table>
    
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ftable complex_border_color">

   	  <thead>
   	  
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'itemForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
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
	      <td align="center" class="TableRow" nowrap style="border-right:none;">
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
            <td align="center" nowrap>
     		   <hrms:checkmultibox name="itemForm" property="itemForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
                    
            <td align="left" nowrap width="200" style="word-break:break-all">
                  <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
                   <td align="center" nowrap>
                  <bean:write name="element" property="string(fillflag)" filter="true"/>&nbsp;
	    </td>  
	        <td align="center" nowrap>
	         <bean:write name="element" property="string(selects)" filter="true"/>&nbsp;
	        </td>
	         <td align="center" nowrap>
	          <bean:write name="element" property="string(maxvalue)" filter="true"/>&nbsp;
	        </td>
	         <td align="center" nowrap>
	          <bean:write name="element" property="string(minvalue)" filter="true"/>&nbsp;
	        </td>
	        <%
	        	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	        	String itemid = vo.getString("itemid");
	         %>
            <td align="center" nowrap>
            	<a href="/selfservice/infomanager/askinv/additem.do?b_query=link&encryptParam=<%=PubFunc.encrypt("itemid="+itemid)%>"><img src="/images/edit.gif" border=0></a>
	    </td>
	      <td align="center" nowrap style="border-right:none;">
	      	<logic:notEqual name="element" property="string(status)" value="1">
            	<a href="/selfservice/infomanager/askinv/addoutline.do?b_addquery=link&encryptParam=<%=PubFunc.encrypt("itemid="+itemid)%>" >
            	<img src="/images/edit.gif" border=0>
            	</a>
	    	</logic:notEqual>
	    	<logic:equal name="element" property="string(status)" value="1">
               &nbsp;
            </logic:equal>
	    </td>
           	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
<tr>
  <td align="center" colspan="9">
     <table  width="100%" align="center">
		<tr>
		    <td align="left" nowrap style="border:none;">
				<bean:message key="label.page.serial" />
					<bean:write name="itemForm" property="itemForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="itemForm" property="itemForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="itemForm" property="itemForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
	        <td  align="right" nowrap style="border:none;">
		          <p align="right"><hrms:paginationlink name="itemForm" property="itemForm.pagination"
				nameId="itemForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
  </td>
</tr>
</table>

       

<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="document.itemForm.target='_self';validate( 'R','itemvo.string(id)','项目名称');return (document.returnValue && ifdel());">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	
	 	<hrms:submit styleClass="mybutton" property="br_return" onclick="JavaScript:history.back();">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
        
            </td>
           
          </tr>          
</table>

</html:form>

