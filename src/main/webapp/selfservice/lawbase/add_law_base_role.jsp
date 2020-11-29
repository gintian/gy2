<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.lawbase.LawBaseForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.fixedHeaderTr{
 	border:1px solid #C4D8EE;
}
.myleft
{
	border-left: none;
}
.mytop
{
	border-top: none;
}
.myright
{
 	border-right:none; 
}
</style>
<script language="javascript">
	function saveAssignRole()
	{
	    /**如果这样做的话，有的机器不提交b_o_save参数为空*/
        lawbaseForm.action="/selfservice/lawbase/add_law_base_role.do?b_save=link";
		lawbaseForm.submit();
	}
	 function read_sp_result()
   {
      <%
        LawBaseForm lawbaseForm=(LawBaseForm)session.getAttribute("lawbaseForm");
        String sp_result= lawbaseForm.getSp_result();         
        if(sp_result!=null&&sp_result.length()>0&&!sp_result.equals("xxx"))
        {
       %>
        //alert("<%=sp_result%>"); 
        <%         
        }  
        lawbaseForm.setSp_result(""); 
        session.setAttribute("lawbaseForm",lawbaseForm);     
      %>                
   }
   function selectAll()
   {
   		var len = document.lawbaseForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.lawbaseForm.elements[i].type=="checkbox")
   				document.lawbaseForm.elements[i].checked=true;
   		}
   }
   function deleteAll()
   {
   		var len = document.lawbaseForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.lawbaseForm.elements[i].type=="checkbox")
   				document.lawbaseForm.elements[i].checked=false;
   		}
   }
</script>
<base target="_self"/>
<html:form action="/selfservice/lawbase/add_law_base_role">
<html:hidden name="lawbaseForm" property="base_id" styleClass="text"/>
<html:hidden name="lawbaseForm" property="closeFlag"/>
<div class="fixedDiv2"> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr class="fixedHeaderTr">
            <td align="center" class="TableRow myleft mytop" nowrap width="5%" >
		 <input type=checkbox name=selbox onclick=batch_select(this,'roleListForm.select'); title=<bean:message key='label.query.selectall' />  width=35>&nbsp;
            </td>           
            <td align="center" class="TableRow myleft mytop" nowrap width="25%">
		&nbsp;<bean:message key="column.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow myleft mytop myright" nowrap width="70%" style="border-right: none;">
		&nbsp;<bean:message key="column.desc"/>&nbsp;
	    </td>
   		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="lawbaseForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="${lawbaseForm.pagerows}" scope="session">
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
            <td align="center" class="RecordRow myleft mytop"  style="border-left: none;" nowrap>
                 <logic:equal name="element" property="string(valid)" value="1">
     		   <hrms:checkmultibox name="lawbaseForm" property="roleListForm.select" value="false" indexes="indexes"/>&nbsp;
            	 </logic:equal>  
                 <logic:equal name="element" property="string(valid)" value="0">
     		   <hrms:checkmultibox name="lawbaseForm" property="roleListForm.select" value="true" indexes="indexes"/>&nbsp;
            	 </logic:equal>             	    		   
	    </td>            
            <td align="left" class="RecordRow myleft mytop" nowrap>
                   <bean:write name="element" property="string(role_name)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow myleft mytop myright" style="border-right: none;" style="word-break:break-all;">
                    <bean:write  name="element" property="string(role_desc)" filter="false"/>&nbsp;
            </td>
          </tr>
        </hrms:extenditerate>
        
</table>
</div>
<div class="fixedDiv3"> 
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    	<hrms:paginationtag name="lawbaseForm" pagerows="${lawbaseForm.pagerows}" property="roleListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="lawbaseForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
<table  width="70%" align="center">
          <tr>
            <td align="center">	           
           <html:button styleId="save" styleClass="mybutton" property="b_save" onclick="saveRole()">
            	   	<bean:message key="button.save"/>
  	       </html:button> 
  	      
           <html:button styleId="close" styleClass="mybutton" property="b_o_close" onclick="window.close();">
            	   	<bean:message key="button.close"/>
  	       </html:button>

                   	           	          	   	 	  
            </td>
          </tr>          
</table>

<div id='wait' style='position:absolute;top:200;left:100;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
      <tr>

        <td class="td_style" height="24">正在保存,请稍候...</td>

      </tr>
      <tr>
        <td style="font-size:12px;line-height:200%" align=center>
          <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
            <table cellspacing="1" cellpadding="0">
              <tr height=8>
                <td bgcolor=#3399FF width=8></td>
                    <td></td>
                    <td bgcolor=#3399FF width=8></td>
                    <td></td>
                    <td bgcolor=#3399FF width=8></td>
                    <td></td>
                    <td bgcolor=#3399FF width=8></td>
                    <td></td>
               </tr>
             </table>
          </marquee>
        </td>
     </tr>
   </table>
</div>

</html:form>
<script language="javascript">
  read_sp_result();
  if(document.getElementById("closeFlag")){
  	if(document.getElementById("closeFlag").value=="1")
  		window.close();
  }
  
  function saveRole(){
		document.getElementById("wait").style.display="block";
		document.getElementById("save").disabled="true";
		document.getElementById("close").disabled="true";
		lawbaseForm.action="/selfservice/lawbase/add_law_base_role.do?b_save=link";
		lawbaseForm.submit();
		
  }
</script>
