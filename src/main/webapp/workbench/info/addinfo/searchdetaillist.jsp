<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<%
	  int i=0;
%>
<script language="javascript">
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
    function multimediahref(setprv,dbname,a0100,i9999){
        var result=false;
        if(setprv==2)
        {
            result=true;
        }else{
            result=false;
        }
        var thecodeurl =""; 
        var return_vo=null;
        var setname = "${selfInfoForm.setname}";
        var dw=800,dh=500,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
        thecodeurl="/general/inform/multimedia/multimedia_tree.do?b_query=link&setid="+setname+"&a0100="+a0100+"&nbase="+dbname+"&i9999="+i9999+"&dbflag=A&canedit="+result;
         if(getBrowseVersion()){//update by xiegh on date 20180316 bug35665
                return_vo= window.showModalDialog(thecodeurl, "", 
                "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:800px; dialogHeight:500px;resizable:no;center:yes;scroll:no;status:no");
         }else{
            var iTop = (window.screen.availHeight - 30 - dh) / 2;  //获得窗口的垂直位置
            var iLeft = (window.screen.availWidth - 10 - dw) / 2; //获得窗口的水平位置 
            window.open(thecodeurl,"","width="+dw+",height="+dh+",resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
         }
    }
</script>
<hrms:themes />
<!-- 【5338】员工管理：快速录入和记录录入中人员子集页面布局不一致，建议优化下  jingq upd 2014.11.25 -->
<style>
.TableRow_left{
	BORDER-TOP: 0pt solid;
}
</style>
<html:form action="/workbench/info/addinfo/add">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top: 8px;">
<tr>
	<td align="left"  nowrap>
        (&nbsp;<bean:message key="label.title.org"/>: <bean:write  name="selfInfoForm" property="b0110" filter="true"/>&nbsp;
        <bean:message key="label.title.dept"/>: <bean:write  name="selfInfoForm" property="e0122" filter="true"/>&nbsp;
        <bean:message key="label.title.name"/>: <bean:write  name="selfInfoForm" property="a0101" filter="true"/>&nbsp;)
     </td>
</tr>
<tr>
  <td>
  	<div id="dataBox" class="fixedDiv2" >
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <logic:notEqual value="${selfInfoForm.setname}" name="selfInfoForm" property="virAxx">
             <td align="center" class="TableRow_top" nowrap>
              <%--<bean:message key="column.select"/>
             --%>
             	<input type="checkbox" name="selbox" onclick="batch_select(this,'selfInfoForm.select');" title='<bean:message key="label.query.selectall"/>'>
             </td>
             <td align="center" class="TableRow_left" nowrap>
              插入
             </td>
             <logic:equal name="selfInfoForm" property="setprv" value="2"> 
               <td align="center" class="TableRow_left" nowrap>
		  			<bean:message key="label.edit"/>            	
               </td> 
             </logic:equal> 
             <logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
                  <td width="5%" align="center" class="TableRow_left" nowrap>
                    <bean:message key="conlumn.resource_list.name"/>                
                  </td> 
              </logic:equal>
             </logic:notEqual>  
            <logic:iterate id="element"    name="selfInfoForm"  property="infoFieldList"> 
              <td align="center" class="TableRow_left" nowrap>
                   <bean:write  name="element" property="itemdesc"/> 
              </td>
             </logic:iterate>  
              		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="selfInfoForm" property="selfInfoForm.list" indexes="indexes"  pagination="selfInfoForm.pagination" pageCount="${selfInfoForm.pagerows}" scope="session">
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
           RecordVo vo=(RecordVo)element;  
          %>  
           <logic:notEqual value="${selfInfoForm.setname}" name="selfInfoForm" property="virAxx">
            <td align="center" class="RecordRow_right" nowrap>
               <hrms:checkmultibox name="selfInfoForm" property="selfInfoForm.select" value="true" indexes="indexes"/>
            </td>
            <td align="center" class="RecordRow" nowrap>
            	<a href="/workbench/info/addinfo/add.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999=<bean:write  name="element" property="string(i9999)" filter="true"/>&actiontype=new&insert=1"  target="mil_body"><img src="/images/goto_input.gif" border=0></a>
	      </td>
	      <logic:equal name="selfInfoForm" property="setprv" value="2">
	      <td align="center" class="RecordRow" nowrap>
            	<a href="/workbench/info/addinfo/add.do?b_edit=edit&a0100=${selfInfoForm.a0100}&i9999=<bean:write  name="element" property="string(i9999)" filter="true"/>&actiontype=update"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	      </td>
	      </logic:equal>
	      <logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
                  <td align="center" class="RecordRow" nowrap>
                    <a href="###"  onclick="multimediahref('${selfInfoForm.setprv}','${selfInfoForm.userbase}','${selfInfoForm.a0100}','<bean:write  name="element" property="string(i9999)" filter="true"/>');"><img src="/images/muli_view.gif" border=0></a>    
                  </td> 
              </logic:equal>
	      </logic:notEqual>
            <logic:iterate id="info"    name="selfInfoForm"  property="infoFieldList">  
                      
                <logic:notEqual  name="info" property="itemtype" value="N">    
                  <logic:notEqual  name="info" property="itemtype" value="M">                 
                  <td align="left" class="RecordRow_left" nowrap> 
                  	<bean:write  name="element" property="string(${info.itemid})" filter="true"/>  
                  </td>     
                  </logic:notEqual>
                </logic:notEqual>
                <logic:equal  name="info" property="itemtype" value="N">               
                <td align="right" class="RecordRow_left" nowrap>        
                  	<bean:write  name="element" property="string(${info.itemid})" filter="true"/>
              	</td>
              </logic:equal>
              <logic:equal  name="info" property="itemtype" value="M">    
                <%
                 FieldItem item=(FieldItem)pageContext.getAttribute("info");
                 String tx=vo.getString(item.getItemid());
               %>          
                <hrms:showitemmemo showtext="showtext" itemtype="M" setname="${selfInfoForm.setname}" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>
                <td align="left" class="RecordRow_left" ${tiptext} nowrap>   
                
                 ${showtext}
               </td>  
              </logic:equal> 
             </logic:iterate>                	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
     </table>
     </div>
  </td>
</tr>
<tr>
  <td>
  	<div style="height:0;border:0" class="fixedDiv2" >
    <table  width="100%" align="center" class="RecordRowP">
		<tr><%-- 【5338】员工管理：快速录入和记录录入中人员子集页面布局不一致，建议优化下  jingq upd 2014.11.25
		--%>
		<td class="tdFontcolor">
			    <hrms:paginationtag name="selfInfoForm" pagerows="${selfInfoForm.pagerows}" property="selfInfoForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
				</td>
		               <td align="right" nowrap class="tdFontcolor">
			          <p align="right"><hrms:paginationlink name="selfInfoForm" property="selfInfoForm.pagination"
					nameId="selfInfoForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	</div>
  </td>
</tr>
</table>

<!-- 【5338】员工管理：快速录入和记录录入中人员子集页面布局不一致，建议优化下  jingq upd 2014.11.25 -->
<table cellspacing="0" cellpadding="0" width="70%" align="left">
          <tr>
            <td align="left" height="35px;">            
               <html:hidden name="selfInfoForm" property="tolastpageflagsub"/>
                  <html:hidden name="selfInfoForm" property="tolastpageflag"/>
               <html:hidden name="selfInfoForm" property="actiontype" value="new"/>
              <logic:equal name="selfInfoForm" property="setprv" value="2">
               <logic:notEqual name="selfInfoForm" property="a0100" value="su">
                <logic:notEqual name="selfInfoForm" property="a0100" value="A0100">
                 <logic:notEqual value="${selfInfoForm.setname}" name="selfInfoForm" property="virAxx">
               	   <hrms:submit styleClass="mybutton" property="b_edit">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>  
         	   <hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdelinfo()">
            		 <bean:message key="button.delete"/>
	 	   </hrms:submit> 
	 	   </logic:notEqual> 
	       </logic:notEqual>
	      </logic:notEqual>
	     </logic:equal>	 	
            </td>
          </tr>          
 </table>
</html:form>
<logic:notEmpty name="selfInfoForm" property="msg">
<script language='javascript'>
alert('<bean:write  name="selfInfoForm" property="msg"/>');
</script>
</logic:notEmpty> 