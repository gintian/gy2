<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.dtgh.party.person.PersonForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    boolean version = false;
    String css_url="/css/css1.css";
    String bosflag="";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  bosflag=userView.getBosflag(); 
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  
       if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	  }	 	 
	}
	
	PersonForm personForm = (PersonForm)session.getAttribute("personForm");
	String param = personForm.getParam();
	boolean showTab = true;
	if("Y".equals(param)){
		if((personForm.getParty()==null||personForm.getParty().length()==0)&&(personForm.getPreparty()==null||personForm.getPreparty().length()==0)&&(personForm.getImportant()==null||personForm.getImportant().length()==0)&&(personForm.getActive()==null||personForm.getActive().length()==0)&&(personForm.getApplication()==null||personForm.getApplication().length()==0)&&(personForm.getPerson()==null||personForm.getPerson().length()==0)){
			showTab = false;
		}
	}else if("V".equals(param)){
		if((personForm.getMember()==null||personForm.getMember().length()==0)&&(personForm.getPerson()==null||personForm.getPerson().length()==0)){
			showTab = false;
		}
	}
	int i=0;
%>
 
<script language="javascript">
function gointo(param){
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	if(currnode.uid=='root'){
		alert("请您先选择非根节点的组织单元节点！");
		return;
	}
	personForm.action="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics="+param;
	//personForm.target="il_body";
	personForm.submit();
}
</script>
<html:form action="/dtgh/party/person/searchbusinesslist">
<% if(showTab){%><!-- 【7098】党团管理（页面上有缺线的现象）jingq upd 2015.02.03 -->
<hrms:tabset name="pageset" width="100%" height="99%" type="true" align="center" >   	
  <logic:equal value="Y" name="personForm" property="param">

	  <logic:notEmpty name="personForm" property="party">
	  	<hrms:priv func_id="350120">
	  	  <hrms:tab function_id="" name="tab1" label="党员" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=party&tabIndex="+i %>'>
	      </hrms:tab>
	      <%i++; %>	
	    </hrms:priv>
	  </logic:notEmpty>
	  
	  <logic:notEmpty name="personForm" property="preparty">
	  <hrms:priv func_id="350121">
		  <hrms:tab function_id="" name="tab2" label="预备党员" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=preparty&tabIndex="+i %>'>
	      </hrms:tab>
	      <%i++; %>	
	      </hrms:priv>
	  </logic:notEmpty>

	  <logic:notEmpty name="personForm" property="important">
	  <hrms:priv func_id="350122">
	  	<hrms:tab function_id="" name="tab3" label="重点发展对象" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=important&tabIndex="+i %>'>
	  	</hrms:tab> 
	  	<%i++; %>	
	  	</hrms:priv>
	  </logic:notEmpty>
	 
		<logic:notEmpty name="personForm" property="active">
		<hrms:priv func_id="350123">
			  <hrms:tab function_id="" name="tab4" label="入党积极分子" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=active&tabIndex="+i %>'>
			  </hrms:tab>
			  <%i++; %>	
			  </hrms:priv>
		</logic:notEmpty>
	
	  <logic:notEmpty name="personForm" property="application">
	  <hrms:priv func_id="350124">
		   <hrms:tab function_id="" name="tab5" label="申请入党人员" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=application&tabIndex="+i %>'>     
		   </hrms:tab> 
		   <%i++; %>	
		   </hrms:priv>
	  </logic:notEmpty>
	
	  <logic:notEmpty name="personForm" property="person">
	  <hrms:priv func_id="350125">
		  <hrms:tab function_id="" name="tab6" label="群众" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=person&tabIndex="+i %>'> 
		  </hrms:tab> 
		  <%i++; %>	
		  </hrms:priv>
	  </logic:notEmpty>
  </logic:equal>
  
  <logic:equal value="V" name="personForm" property="param">
	  <logic:notEmpty name="personForm" property="member">
	  <hrms:priv func_id="350220">
	   <hrms:tab function_id="" name="tab1" label="团员" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=member&tabIndex="+i %>'>     
	   </hrms:tab> 
	   <%i++; %>	
	   </hrms:priv>
	  </logic:notEmpty>
	
	  <logic:notEmpty name="personForm" property="person">
	  <hrms:priv func_id="350221">
	 	<hrms:tab function_id="" name="tab2" label="群众" visible="true" url='<%="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics=person&tabIndex="+i %>'> 
	    </hrms:tab> 
	    <%i++; %>	
	    </hrms:priv>
	  </logic:notEmpty>
  </logic:equal>

</hrms:tabset>
<%} else{%>
	<script>
		alert("请在参数设置中设置相应的政治面貌!");
	</script>
<%} %>
</html:form>
<script type="text/javascript">
<!--
	pageset.tabIndex=${personForm.tabIndex};
//-->
</script>
