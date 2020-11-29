<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.hire.EmployResumeBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,com.hrms.hjsj.sys.Des"%>


<%
    int i = 0;
    EmployResumeForm employResumeForm = (EmployResumeForm) session.getAttribute("employResumeForm");
    String personType = employResumeForm.getPersonType();
    String columns = "a0100,a0101";
    //	if(personType.equals("0"))
    //		columns+=",zp_pos_id";
    String resumeState = employResumeForm.getResumeState();
    String dbname = employResumeForm.getDbname();
    String z0301 = employResumeForm.getZ0301();
    String sql_view = employResumeForm.getSql_view();
    while(sql_view.indexOf(dbname+"A01.a0101") != -1){
        sql_view=sql_view.replace(dbname+"A01.a0101", "'zpoh'");
    }
    String sql = "select distinct " + dbname + "A01.a0100," + dbname + "A01.a0101";
    String order = " order by  " + dbname + "A01.a0100";
%> 
<script language='javascript'>
	 function resumeBrowse(a0100,dbname,zp_pos_id)
  	 {
   		
   		window.open("/hire/employNetPortal/search_zp_position.do?b_resumeBrowse=browse&flag=photo&dbName="+dbname+"&a0100="+a0100+"&zp_pos_id="+zp_pos_id+"&personType=${employResumeForm.personType}","_blank");
   	 }
   	 
   	 
   	 function goback()
   	 {
   	 		<%if (personType.equals("0")) {%>
       		
       		window.open("/hire/employActualize/employResume.do?b_query=link&personType=0&operate=back&employType=${employResumeForm.employType}","_self");
       		<%} else if (personType.equals("1")) {%>
       		window.location="/hire/employActualize/employResume.do?b_query=link&personType=1&employType=${employResumeForm.employType}";
       		<%} else if (personType.equals("4")) {%>
       		window.location="/hire/employActualize/employResume.do?b_query=link&personType=4&employType=${employResumeForm.employType}";
       		<%}%>   
   	 }

</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/employResumePhoto">

<Br>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <hrms:paginationdb id="element" name="employResumeForm" sql_str="<%=sql %>" table="" where_str="${employResumeForm.str_whl}" columns="<%=columns%>" pagerows="20" order_by="<%=order %>" page_id="pagination" keys="">
          <%
              if (i % 4 == 0) {
          %>
          <tr>
          <%
              }
                      LazyDynaBean abean = (LazyDynaBean) pageContext.getAttribute("element");
                      Des des = new Des();
                      String a0100 = (String) abean.get("a0100");
                //      String zp_pos_id = (String) abean.get("zp_pos_id");
                      String dbName = employResumeForm.getDbname();
                      String Name = employResumeForm.getName();
                      a0100 = PubFunc.encryption(a0100);
                    //  zp_pos_id = PubFunc.encryption(zp_pos_id);
                      dbName = PubFunc.encryption(dbName);
                      request.setAttribute("name", a0100);
                     // request.setAttribute("zpid", zp_pos_id);
                      request.setAttribute("dbName", dbName);
          %>             
          <td align="center" NOWRAP>
          <%
		    String posHtml = "";
		    String posTile = "";
		    String posid = "";
        	if(!"4".equalsIgnoreCase(personType)){
	          	String position = EmployResumeBo.getPosition(a0100, personType, resumeState, dbname,z0301);
		        if(position.indexOf("`")!=-1){
		          	String[] positions = position.split("`");
		          	posHtml = positions[0];
		          	posTile = positions[1];
		          	posid = positions[2];
		        } else {
		            posHtml = position;
		        }
		        request.setAttribute("posid", posid);
        	}
          %>
					<ul class="photos">
						<li>

							<logic:equal name="employResumeForm" property="personType" value="0">
								<hrms:ole name="element"  photoWall="true" dbpre="employResumeForm.dbname" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="resumeBrowse('${name}','${dbName}','${posid}')" />
								<div class="detail">
									<p><a href='javascript:resumeBrowse("<%=a0100%>","<%=dbName%>","<%=posid%>")'>
										<bean:write name="element" property="a0101" filter="true" />
									</a></p>
									<p class="linehg" style="margin-left:36px;" <%if(posTile!=null&&posTile.length()>0){%>title="<%=posTile %>"<% } %> >
										<%=posHtml %>
									</p>
								</div>
							</logic:equal>
							<logic:notEqual name="employResumeForm" property="personType" value="0">
								<hrms:ole name="element"  photoWall="true" dbpre="employResumeForm.dbname" href="###" target="nil_body" a0100="a0100" div="" scope="page" height="120" width="85" onclick="resumeBrowse('${name}','${dbName}','-1')" />
								<div class="detail">
									<p><a href='javascript:resumeBrowse("<%=a0100%>","<%=dbName%>","-1")'>
										<bean:write name="element" property="a0101" filter="true" />
								    </a></p>
									<p class="linehg" style="margin-left:36px;" <%if(posTile!=null&&posTile.length()>0){%>title="<%=posTile %>"<% } %> >
										<%=posHtml %>
									</p>
								</div>
							</logic:notEqual>
						</li>
					</ul>
		  </td> 
          <%
               if ((i + 1) % 4 == 0) {
           %>
          </tr>
          <%
              }
                      i++;
          %>         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true"/>
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true"/>
					<bean:message key="hmuster.label.paper"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="employResumeForm" property="pagination" nameId="employResumeForm" scope="page">
				</hrms:paginationdblink></p>
		       </td>
		</tr>
</table>
<table  width="80%" align="center">
          <tr>
            <td align="center">       
         	   <Input type='button' value=<bean:message key="button.return"/> onclick='goback()' class="mybutton"  />
            </td>
          </tr>          
</table>
</html:form>
