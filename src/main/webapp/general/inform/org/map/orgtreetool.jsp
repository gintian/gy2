<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag="";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
      bosflag=userView.getBosflag(); 
	}
	
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
%>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
</script>
<script type="text/javascript">
<!--
	function deleteCode(){
	
		var cur=parent.frames['mil_menu'].Global.selectedItem;
		//alert(cur.uid);
		if(cur.uid.indexOf("hroot")!=-1){
			if(confirm(DEL_INFO)){
				var hashvo=new ParameterSet();
      			hashvo.setValue("catalogid",cur.uid.substring(5,cur.uid.length)); 
      			var request=new Request({method:"post",asynchronous:false,onSuccess:showlist,functionId:"16010000034"},hashvo);
				//var in_paramters="catalogid="+cur.uid.substring(5,cur.uid.length);
	   	   		//var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showlist,functionId:'16010000034'});
				function showlist(outparamters){
				   var msg=outparamters.getValue("msg");
				   var catalog_id=outparamters.getValue("catalog_id");
				   if(msg==1){
						cur.remove();	
						if('${orgMapForm.isyfiles}' == '1'){
							orgMapForm.action="/general/inform/org/map/showyFilesOrgMap.do?code=&catalog_id="+catalog_id;
						}else{
							if(catalog_id != null && catalog_id.length>0){
	                            orgMapForm.action="/general/inform/org/map/searchhistoryorgmaps.do?code=&catalog_id="+catalog_id;
							}else{
								window.parent.parent.document.getElementsByName('il_body')[0].src='/general/inform/org/map/searchhistoryorgmap.do?b_search=link&busiPriv=1&catalog_id=';
								return;
							}
						}
						orgMapForm.target="mil_body";
						orgMapForm.submit();
						
					}else{
						alert("历史归档机构删除失败！");
					}
				}
			}
		}else{
			alert("请选择要删除的历史归档机构名称！");
		}
	}
//-->
</script>

<hrms:themes />
<body style="margin: 0px">
<html:form action="/general/inform/org/map/searchorgmap"> 
    <table width="100%" style="margin-left:0px;margin-top:0px;"  border="0" cellspacing="0"  align="center" cellpadding="0" >
    <tr align="left" class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left">
		<hrms:priv func_id="230520,0501030">
			<a href="###" onclick="deleteCode();"><img src="/images/del.gif" alt="删除历史归档机构" title="删除历史归档机构" border="0" align="middle"></a>  <!--add by xiegh 非ie浏览器alt无效  -->             
		</hrms:priv>
		</td>
	</tr>       
    </table>
</html:form>
</body>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>