<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    String bosflag = "";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
		  css_url=userView.getCssurl();
		  if(css_url==null||css_url.equals(""))
		  	 css_url="/css/css1.css";
	          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >"); 
		  bosflag = userView.getBosflag();
	      /*xuj added at 2014-4-18 for hcm themes*/
	    themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName()); 
		}
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
%>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--
	function backDate(){
		//var backdate=showModalDialog('/org/orginfo/searchorgtree.do?b_backdate=link','_blank','dialogHeight:370px;dialogWidth:400px;center:yes;help:no;resizable:no;status:no;');
/* 		if(getBrowseVersion()!=10 && getBrowseVersion()){//此时代表ie兼容模式
            var top = (window.screen.availHeight - 30 - 600) / 2;//获得窗口的垂直位置;
            var left = (window.screen.availWidth - 10 - 800) / 2; //获得窗口的水平位置;
            //兼容非IE浏览器 弹窗改用open  wangb 20171122
            open('/org/orginfo/searchorgtree.do?b_backdate_new=link', '_blank', 'height=370px,width=400px,resizable=no,status=no,top=' + top + ',left=' + left);
		}else{
            var obj;  //兼容多浏览器改用Ext window弹窗  start wangz 2019-03-07
            var theUrl = '/org/orginfo/searchorgtree.do?b_backdate_new=link';
            if (getBrowseVersion()) {
                obj = parent.frames[2];
            } else {
                obj = parent.frames['center_iframe'][1].contentWindow;
            }
            var win = obj.Ext.create('Ext.window.Window', {
                title: '历史节点查询',
                id: 'searchorgtree',
                width: 400,
                height: 370,
                resizable: false,
                modal: true,
                autoScoll: false,
                autoShow: true,
                autoDestory: true,
                html: '<iframe style="background-color: #fff;" frameborder="0" scrolling="NO" height="100%" width="100%" src="' + theUrl + '"></iframe>',
                renderTo: obj.Ext.getBody(),
                listeners: {
                    'close': function () {
                        if(this.backdate){
                            var backdate = this.backdate;
                            if(backdate&&backdate.length>9) {
                                if('${orgMapForm.isyfiles}' == '1'){
                                    orgMapForm.action="/general/inform/org/map/searchOrgTree.do?b_search=link&backdate="+backdate+"&code=";
                                }else{
                                    orgMapForm.action="/general/inform/org/map/searchorgmap.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";

                                }
                                orgMapForm.target="il_body";
                                orgMapForm.submit();
                            }
                        }
                    }
                }

            })
            //兼容多浏览器改用Ext window弹窗  end wangz 2019-03-07
		} */
		
		var iTop = (window.screen.height-30-350)/2; //获得窗口的垂直位置;
	  	var iLeft = (window.screen.width-10-400)/2;  //获得窗口的水平位置;
	  	window.open('/org/orginfo/searchorgtree.do?b_backdate_new=link','','height=300, width=400,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');

	}
	//open弹窗回调方法
	function openHistoryReturn(backdate){
        if(backdate&&backdate.length>9) {
            if('${orgMapForm.isyfiles}' == '1'){
                orgMapForm.action="/general/inform/org/map/searchOrgTree.do?b_search=link&backdate="+backdate+"&code=";
            }else{
                orgMapForm.action="/general/inform/org/map/searchorgmap.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";

            }
            orgMapForm.target="il_body";
            orgMapForm.submit();
        }
	}
	//弹窗调用父窗口方法  wangb 20171122
	function click_ok(backdate){
		if(backdate&&backdate.length>9) {
			if('${orgMapForm.isyfiles}' == '1'){
				orgMapForm.action="/general/inform/org/map/searchOrgTree.do?b_search=link&backdate="+backdate+"&code=";
			}else{
			    orgMapForm.action="/general/inform/org/map/searchorgmap.do?b_search=link&treetype=vorg&backdate="+backdate+"&code=";
			    
			}
			orgMapForm.target="il_body";
			orgMapForm.submit();
		}else
			return false;
	}
	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
	
	function viewhide(){
		var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
		var uid = currnode.uid;
		if("root"==uid){
			return;
		}
		var icon=currnode.icon;
		var text=currnode.text;
		var msg="";
		var flag="0";
		var orgtype="";
		var imgurl="";
		if(icon.indexOf("_h")==-1){
			msg="确认要在机构图中隐藏["+text+"]?";
			flag=1;
			imgurl=icon.replace(".gif","_h.gif");
		}else{
			msg="确认要在机构图中显示["+text+"]?";
			imgurl=icon.replace("_h","");
		}
		if(icon.indexOf("v")!=-1)
			orgtype="v";
		if(confirm(msg)){
			var hashvo=new ParameterSet();
			hashvo.setValue("codeitemid",uid.substring(2));
			hashvo.setValue("flag",flag);
			hashvo.setValue("orgtype",orgtype);
			var request=new Request({method:'post',asynchronous:false,parameters:'',onSuccess:viewhide_ok,functionId:'0405050032'},hashvo);
			function viewhide_ok(outparamters){
				var msg=outparamters.getValue("msg");
				if("ok"==msg){
					currnode.setIcon(imgurl);
					if(currnode.load){
						while(currnode.childNodes.length){
							//alert(currnode.childNodes[0].uid);
							currnode.childNodes[0].remove();
						}
						currnode.load=true;
						currnode.loadChildren();
						currnode.reload(1);
					}
					currnode.openURL();
				}
			}
		}
	}
//-->
</script>

<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
	a {margin-right: 5px;}
-->
</style>
<body  border="0" cellspacing="0"  cellpadding="0">
<html:form action="/general/inform/org/map/searchorgmap"> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
    	<%if(version){ %>
    	<tr align="left"  class="toolbar" style="padding-left: 10px">
		<td valign="middle" align="left">
			<hrms:priv module_id="" func_id="0501021,230511">
				<a id="history" href="###" onclick="return backDate();">
				   <img src="/images/quick_query.gif" alt="历史时点查询" title="历史时点查询" border="0" align="middle">
				</a>    
			</hrms:priv>
			<hrms:priv module_id="" func_id="0501020,230510">
				<a href="###" onclick="return viewhide();">
				  <img src="/images/viewhide.gif" alt="机构图显示或隐藏当前组织单元" title="机构图显示或隐藏当前组织单元"  border="0" align="middle">
				</a>    
			</hrms:priv>
		</td>
		</tr>  
    	<%} %>
    </table>
</html:form>
</body>
<script language="javascript">
	<%--getBrowseVersion()方法改动后，ie11返回值是10 wangbs 2019年3月12日12:02:05--%>
	if(!getBrowseVersion() || getBrowseVersion()==10){// 非IE浏览器兼容样式  a 标签添加id属性   wangb 20171122
		   var a = document.getElementById('history');
		   a.style.marginLeft='10px';
	}
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