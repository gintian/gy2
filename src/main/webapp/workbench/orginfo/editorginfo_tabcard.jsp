<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.org.OrgInfoForm,java.util.*,com.hrms.hjsj.sys.FieldSet" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.hjsj.sys.DataDictionary,com.hrms.frame.utility.AdminDb"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.ConstantXml,java.sql.Connection"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
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
	
%>
<%
       OrgInfoForm orgInfoForm=(OrgInfoForm)session.getAttribute("orgInfoForm");
       ArrayList infoSetList = (ArrayList)orgInfoForm.getInfoSetList();
       String kind=orgInfoForm.getKind();
       String code = orgInfoForm.getCode();
       String return_code=orgInfoForm.getReturn_codeid();
       String returnvalue=request.getParameter("returnvalue");
		if("75".equals(returnvalue)){
			orgInfoForm.setLeader("org");
			Connection conn =null;
			try{
			conn = AdminDb.getConnection();
			ConstantXml xml = new ConstantXml(conn,"ORG_LEADER_STRUCT");
				String org_m = xml.getValue("org_m");
				org_m=org_m==null?"":org_m;
				orgInfoForm.setOrg_m(org_m);
				String org_c = xml.getValue("org_c");
				org_c = org_c==null?"":org_c;
				orgInfoForm.setOrg_c(org_c);
				}catch(Exception e){}finally{
					if(conn!=null){
						conn.close();
					}
				}
		}
       String leader = orgInfoForm.getLeader();
       if("leader".equals(leader)){
       	String org_m=orgInfoForm.getOrg_m();
       	String org_c=orgInfoForm.getOrg_c();
		
       	infoSetList=new ArrayList();
       	FieldSet set = DataDictionary.getFieldSetVo(org_m);
       	if(set!=null)
       		infoSetList.add(set);
       	String tmp[]=org_c.split(",");
       	for(int i=0;i<tmp.length;i++){
       		String setid=tmp[i];
       		if(setid.length()==3){
       			set = DataDictionary.getFieldSetVo(setid);
		       	if(set!=null)
		       		infoSetList.add(set);
       		}
       	}
       }
       String isself = request.getParameter("isself");
       isself = isself==null?"":isself;
 %>
 
<script language="javascript">
function exeReturn(returnStr,target)
{
  target_url=returnStr;
  window.open(target_url,target); 
}
</script>
<html:form action="/workbench/orginfo/editorginfodata" style="margin-left:-2px;">
<script language="javascript">
	if(!getBrowseVersion()){ //非IE浏览器兼容性问题  wangb 20171115
		var td = document.getElementsByName('orgInfoForm')[0].parentNode;//获取当前表单 上级td节点
		td.style.overflow='';//超出范围不管
	}
</script>

<% if(infoSetList!=null&&infoSetList.size()>0)
   {%>
<script language="javascript">
var editType = "";
var b00Code = "";
Ext.onReady(function(){
	Ext.widget('viewport',{
 		layout:'fit',
 		items:{
 			xtype:'tabpanel',
 			id:'cardTabPanel',
 			margin:'10 0 0 0',
 			listeners:{
 				beforetabchange:function( tabPanel, newCard, oldCard, eOpts){
 					var newCardId = newCard.config.id;
 					if("B00" == newCardId) {
 						var B00Tab = Ext.getCmp("B00");
 						var html = B00Tab.html;
						if(b00Code)
 							html = html.replace("&a0100=<%=code%>&", "&a0100="+ b00Code + "&");
						
 						if(html) {
 							if(html.indexOf("&editType=") > -1)
 								html = html.replace("&editType=new", "&editType="+editType);
 							else
 								html = html.replace("&isself=0", "&isself=0&editType="+editType);
 						}
 						
 						B00Tab.html = html;
 					}
 					
 					if(oldCard&&oldCard.config){
 						var id=oldCard.config.id;
 						if(id=='A01'||id=='B01'||id=='K01'){
 							 if(document.getElementsByName(id)){
 								//console.log(document.getElementsByName("B01")[1].contentWindow);
 								var childFrames=document.getElementsByName("B01");
 								var childFrame;
 								for(var i=0;i<childFrames.length;i++){
 									if(childFrames[i].contentWindow&&typeof childFrames[i].contentWindow.OrgDataIsChange==='function'){
 										childFrame=childFrames[i];
 									}
 								}
								if(childFrame&&typeof childFrame.contentWindow.OrgDataIsChange==='function'){
									if(childFrame.contentWindow.OrgDataIsChange()){
										var r=confirm("信息已修改请保存数据，否则可能会导致数据丢失。确认跳转？");
			 							if(r==false){
			 								return false;
			 							}
									}
								} 								
 							} 

 						}
 					}
				},
				tabchange: function(tabPanel, newCard, oldCard){
					var cardId = newCard.config.id;
					var src = document.getElementById("iframe_" + cardId).src;
					if(src.indexOf("&dateId") > -1)
						src = src.substring(0, src.indexOf("&dateId"));
					
					document.getElementById("iframe_" + cardId).src = src + "&dateId=" + new Date();
               }
 			}
 		}
 	});
	


  <logic:equal value="leader" name="orgInfoForm" property="leader" >
<%
      for(int i=0;i<infoSetList.size();i++)
      {
          FieldSet set = (FieldSet)infoSetList.get(i);
          String setid=set.getFieldsetid();
          String setfesc=set.getCustomdesc();
          String url="";
          if(set.getFieldsetid().equalsIgnoreCase("B01"))
          {
             url="/workbench/orginfo/editorginfodata.do?b_query=link&setname="+setid+"&treetype=org";
          }
           else if(set.getFieldsetid().equalsIgnoreCase("B00")){
           	session.setAttribute("code","U"+return_code);
             url="/general/inform/emp/view/multimedia_tree.do?b_query=link&isvisible=1&kind="+kind+"&a0100="+code+"&multimediaflag=&isself="+isself;
           }
           else
           {
               url="/workbench/orginfo/searchdetailinfolist.do?b_search=link&setname="+setid+"&treetype=org";
           }
            if(set.getFieldsetid().equalsIgnoreCase("B01"))
          {
  %>
      <%}
         else if(set.getFieldsetid().equalsIgnoreCase("B00")){
        %>	
      <%}else{ %>
      Ext.getCmp("cardTabPanel").add({
			title:"<%=setfesc%>",
			id:"<%=setid.toUpperCase()%>",
			html:'<iframe src="<%=url%>" name="<%=setid.toUpperCase()%>" id="iframe_<%=setid.toUpperCase()%>" width="100%" height="100%" frameborder=0 />'
		});	
	 <%--  <hrms:tab name='<%="tab"+i%>' label="<%=setfesc%>"  visible="true" url="<%=url%>">
      </hrms:tab> --%>
      
      <%
            }
        }
    
       %>	
	
  </logic:equal>
   <logic:notEqual value="leader" name="orgInfoForm" property="leader">
<%
		String org_c=(orgInfoForm.getOrg_m()+","+orgInfoForm.getOrg_c()).toUpperCase();
      for(int i=0;i<infoSetList.size();i++)
      {
          FieldSet set = (FieldSet)infoSetList.get(i);
          String setid=set.getFieldsetid();
          if(org_c.indexOf(setid.toUpperCase())!=-1)
          	continue;
          String setfesc=set.getCustomdesc();
          String url="";
          if(set.getFieldsetid().equalsIgnoreCase("B01"))
          {
             url="/workbench/orginfo/editorginfodata.do?b_query=link&setname="+setid+"&treetype=org";
          }
          else if(set.getFieldsetid().equalsIgnoreCase("B00")){
           	 session.setAttribute("code","U"+return_code);
             url="/general/inform/emp/view/multimedia_tree.do?b_query=link&isvisible=1&kind="+kind+"&a0100="+code+"&multimediaflag=&isself="+isself;
          }
          else
          {
             url="/workbench/orginfo/searchdetailinfolist.do?b_search=link&setname="+setid+"&treetype=org";
          }
            if(set.getFieldsetid().equalsIgnoreCase("B01"))
          {
  %>
  	Ext.getCmp("cardTabPanel").add({
		title:"<%=setfesc%>",
		id:"<%=setid.toUpperCase()%>",
		html:'<iframe src="<%=url%>" name="<%=setid.toUpperCase()%>" id="iframe_<%=setid.toUpperCase()%>" width="100%" height="100%" frameborder=0 />'
	});	
	  <%-- <hrms:tab name='<%="tab"+i%>' label="<%=setfesc%>" visible="true" url="<%=url%>">
      </hrms:tab> --%>
      <%}
         else if(set.getFieldsetid().equalsIgnoreCase("B00")){
        %>	
    <hrms:priv func_id="2306010,05010401">     
	    Ext.getCmp("cardTabPanel").add({
			title:"<%=setfesc%>",
			id:"<%=setid.toUpperCase()%>",
			html:'<iframe src="<%=url%>" name="<%=setid.toUpperCase()%>" id="iframe_<%=setid.toUpperCase()%>" width="100%" height="100%" frameborder=0 />'
		});	
	<%--   <hrms:tab name='<%="tab"+i%>' label="<%=setfesc%>" visible="true" url="<%=url%>"> 
      </hrms:tab>	 --%>
    </hrms:priv>
      <%}else{ %>
      Ext.getCmp("cardTabPanel").add({
			title:"<%=setfesc%>",
			id:"<%=setid.toUpperCase()%>",
			html:'<iframe src="<%=url%>" name="<%=setid.toUpperCase()%>" id="iframe_<%=setid.toUpperCase()%>" width="100%" height="100%" frameborder=0 />'
		});	
	 <%--  <hrms:tab name='<%="tab"+i%>' label="<%=setfesc%>"  visible="true" url="<%=url%>">
      </hrms:tab> --%>
      
      <%
            }
        }
    
       %>	
</logic:notEqual>
Ext.getCmp("cardTabPanel").setActiveTab(0);
});
</script>
<%} %>
<%if(bosflag!=null&&bosflag.equals("hl")) {%>
<logic:equal name="orgInfoForm" value="dxt" property="returnvalue1">
<input type="button" name="b_delete" value='<bean:message key="button.return"/>' class="mybutton" onclick="hrbreturn('org','2','orgInfoForm');"><!-- hrbreturn('org','il_body','orgInfoForm --> 
</logic:equal>
<%} %>  
</html:form>
<%if("Gecko".equals(userView.getBrower())){ %>
<script type="text/javascript">
<!--
	document.forms[0].style.height=document.body.clientHeight;
	document.forms[0].style.width=document.body.clientWidth;
//-->
</script> 
<%}%>
