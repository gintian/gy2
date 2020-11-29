<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/ht/param/ht_param_menu">
<hrms:tabset name="cardset" width="96%" height="96%" type="true"> 
		  <hrms:tab name="menu1" label="label.dbase" visible="true" function_id="" url="/ht/param/ht_param_db.do?b_query=link&menuid=1">
	      </hrms:tab>
		 <hrms:tab name="menu2" label="ht.param.empindex" visible="true" function_id="" url="/ht/param/ht_param_empindex.do?b_query=link&menuid=2">
	      </hrms:tab>	
	      <hrms:tab name="menu3" label="ht.param.htset" visible="true" function_id="" url="/ht/param/ht_param_htset.do?b_query=link&menuid=3">
	      </hrms:tab>	
</hrms:tabset>
<logic:equal value="dxt" name="contractParamForm" property="returnvalue">
  <div style="position:relative; width:50px; margin-top:550px!important; margin-top:5px;left:50%;margin-left:-0px; ">
    <html:button styleClass="mybutton" property="bc_btn1" onclick="hrbreturn('contract','il_body','contractParamForm');">
               返回
    </html:button>
  </div>
</logic:equal>
</html:form>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script>
	//48910 非IE浏览器出纵向滚动条 guodd 2019-06-17
	if(!getBrowseVersion()){
		document.body.style.marginTop="4px"
	}
</script>
