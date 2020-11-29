<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.ImplementForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 	 
	}
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
	ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");	
	ArrayList objectList=implementForm.getPerObjectDataListform().getList();
	ArrayList lastRelaPlans = implementForm.getLastRelaPlans();
	String planStatus=implementForm.getPlanStatus();
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes />
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
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
    function testIsKhObj(nodeid)
    {
        for(var n=0;n<object_ids.length;n++)
        {
            if(nodeid==object_ids[n])
                return true;
        }
        return false;
    }
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
<script language="JavaScript">


function pf_ChangeFocus() 
{
   key = window.event.keyCode;

   if ( key==0xD && event.srcElement.tagName!='TEXTAREA'&& event.srcElement.type!='file') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
 
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}
document.oncontextmenu= function ()
{ 
  return　false; 
}

	var object_ids = new Array();
	var m=0;
	<%for(int i=0;i<objectList.size();i++){
		LazyDynaBean abean = (LazyDynaBean)objectList.get(i);
		String object_id = (String)abean.get("object_id");
	%>
		object_ids[m]='<%=object_id%>';
		m++;
	<%}%>
	
	function importLastTargetCard1(aplanid){
		var currnode=Global.selectedItem;
    	if(currnode==null)
    		return;   
    	var theCodeId=currnode.uid; //得到选中结点
    	if(theCodeId=='root')    	
    		theCodeId=obj.uid; 		   
		theCodeId=theCodeId.split('`')[3]+theCodeId.split('`')[0];
		window.tempObj = {};
        window.tempObj.objList = new  Array();
		if(theCodeId.indexOf("p")!=-1)
		{
            window.tempObj.objList[0]=theCodeId.substring(1);
            getObjectLit_ok();
		}else{
			var right_fields='';
			var opt = 2;
			var infos=new Array();
			infos[0]=aplanid;
			infos[1]=opt;
			infos[2]='${implementForm.planid}';
			
	   		var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`dispPlanName=1`callBackfunc=getObjectLit_ok";
			var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
			var dialogWidth=620;
			var dialogHeight=480;
            if(!window.showModalDialog){
                dialogHeight=480;
                window.dialogArguments = infos;
            }
            var config = {
                width:dialogWidth,
                height:dialogHeight,
                dialogArguments:infos,
                type:"2"
            }
            modalDialog.showModalDialogs(iframe_url,infos, config,getObjectLit_ok);
		}

	}
	function getObjectLit_ok(returnvo) {
	    if (returnvo){
            window.tempObj.objList=returnvo;
        }
        var objList_array = [];
        for(var i=0;i<window.tempObj.objList.length;i++){
            objList_array[i]=window.tempObj.objList[i];
        }
        var hashvo=new ParameterSet();
        hashvo.setValue("planid_past",'${implementForm.planid}');
        hashvo.setValue("objList",objList_array);
        hashvo.setValue("opt",'38');
        var request=new Request({method:'post',asynchronous:false,onSuccess:function(outparameters){
            var planid_copy = outparameters.getValue("planid_copy");
            importLastTargetCard(planid_copy);
        },functionId:'9023000003'},hashvo);
    }
	function importLastTargetCard(planid_copy)
	{
		if(!planid_copy || planid_copy == ""){
			alert('上期目标卡无合适数据引入!');
			return;
		}else{
		
			var currnode=Global.selectedItem;
	    	if(currnode==null)
	    		return;   
	    	var theCodeId=currnode.uid; //得到选中结点
	    	if(theCodeId=='root')    	
	    		theCodeId=obj.uid; 		   
			theCodeId=theCodeId.split('`')[3]+theCodeId.split('`')[0];
			var objList = new  Array();
			var aplanid=planid_copy;
			if(theCodeId.indexOf("p")!=-1)
			{
				objList[0]=theCodeId.substring(1);
                importLastTargetCard_ok(objList);
			}else{
				var right_fields='';
				var opt = 2;
				var infos=new Array();
				infos[0]=aplanid;
				infos[1]=opt;
				infos[2]="${implementForm.planid}";
		
		   		var strurl="/performance/handSel.do?b_query=link`planid="+aplanid+"`opt="+opt+"`dispPlanName=1`callBackfunc=importLastTargetCard_ok";
				var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+$URL.encode(strurl);
                var dialogWidth=600;
                var dialogHeight=480;
                if(!window.showModalDialog){
                    dialogWidth = 620;
                    dialogHeight=480;
                }
                var config = {
                    width:dialogWidth,
                    height:dialogHeight,
                    type:"2"
                }
                modalDialog.showModalDialogs(iframe_url,infos, config,importLastTargetCard_ok);
			}
		}		
	}
	function importLastTargetCard_ok(objList){
        if(objList==null)
            return;

        if(objList.length>0)
        {
            if(confirm('确认引入上期目标卡吗？此操作会将当前目标卡中的数据清除！'))
            {

            }else
                return;

            var objList_array = new  Array();
            for(var i=0;i<objList.length;i++){
                objList_array[i]=objList[i];
            }
            var hashvo=new ParameterSet();
            hashvo.setValue("planid_past",'${implementForm.planid}');
            hashvo.setValue("planid_copy",aplanid);
            hashvo.setValue("objList",objList_array);
            hashvo.setValue("objCode",theCodeId);
            hashvo.setValue("opt",'24');
            var request=new Request({method:'post',asynchronous:false,onSuccess:refreshPage,functionId:'9023000003'},hashvo);
        }
    }
	function refreshPage(outparameters)
	{   
		var currnode=Global.selectedItem;
    	if(currnode==null)
    	    return;   		
    	    
    	var currnode=Global.selectedItem;
    	if(currnode==null)
    	    return;   
    	var theCodeId=currnode.uid; //得到选中结点
    	if(theCodeId=='root')    	
    		theCodeId=obj.uid; 
    	var objCode = outparameters.getValue("objCode");
		parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid="+objCode;
	}
</script>

<html:form action="/performance/implement/performanceImplement" > 
<div onresize="resize_table()" style="width:100%;">
<table id="table_menu" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="height:90%;width:100%;position:absolute;left:0px;top:0px;">
  <tr align="left" class="toolbar" style="padding-left:2px;">  
    <td valign="middle">&nbsp;
				<%if(planStatus.equals("8") || planStatus.equals("3")  || planStatus.equals("5")){ %>
				<a href="javascript:copyTargetCard();" > <img src="/images/copy.gif" border="0" align="middle" id='copyTargetCard' title="<bean:message key='jx.implement.copyTargetCard' />" /></a>
				 	 <!-- <img src="/images/past.gif"  id='pastBt'  alt="<bean:message key='jx.implement.pastRule' />" disabled onClick="pastRule();"/> -->
				<a href="javascript:pastTargetCard();" >
					<!-- 控制粘贴按钮是否可以点击 -->
					<input type="hidden" id="isCanClick" value="0"/>
					<img src="/images/past.gif" border="0" align="middle" title="<bean:message key='jx.implement.pastTargetCard' />"/>
				</a>
				<a href="javascript:importLastTargetCard1('${implementForm.planid}');" > <img src="/images/import.gif" border="0" align="middle" id='inputTargetCard' title="<bean:message key='jx.implement.target_card_set.importLastTargetCard' />" /></a>
				<%} %>   
			</td>
		</tr>
	<tr>  
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	 
	</tr>
</table>	
</div>
<SCRIPT LANGUAGE=javascript>
//flag:-1 从人员库开始显示 0从顶层机构开始显示
	var m_sXMLFile	= "/performance/kh_plan/handImportObjs.jsp?flag=0&id=0&nbase=Usr&planid=${implementForm.planid}&opt=7";
	actionUrl="/performance/implement/kh_mainbody/set_dyna_main_rank/welcome.html";//点击根节点显示空白页面
	var newwindow;
	var root=new xtreeItem("root","组织机构",actionUrl,"mil_body","组织机构","/images/root.gif",m_sXMLFile);
	Global.defaultInput=1;
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));	
	//root.openURL();
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	var obj=root;
	/*var i=0;
	var ori_text="";
	while(obj.getFirstChild()&&ori_text!=obj.getFirstChild().text)
	{
		ori_text=obj.getFirstChild().text;
		i++;
		obj.getFirstChild().expand();
		var a_obj=obj.getFirstChild();
		obj=a_obj;
		if(i==8)
			break;
	}
*/
	var findNode=false;
	var orgLink = "${implementForm.objInfo}";
	var temps=orgLink.split("/");
	for(var i=temps.length-1;i>=0;i--)
	{
		obj.expand();
		for(var j=0;j<obj.childNodes.length;j++)
		{
			if(obj.childNodes[j].text==temps[i])
			{
				obj=obj.childNodes[j];
				if(obj && obj.uid) {
					var codeitemid = obj.uid.split("`");
					if(codeitemid[0]=='${implementForm.object_id}')				
						findNode = true;
					break;
				}
			}
		}
	}
	obj.expand();	
	if(findNode==false)
	{
/*		var Node=false;
		for(var i=temps.length-1;i>=0;i--)
		{
			obj.expand();					
			for(var j=0;j<obj.childNodes.length;j++)
			{
//				alert(obj.childNodes[j].text);
//				alert(temps[i]);
			
				if(obj.childNodes[j].text==temps[i])
				{
					obj=obj.childNodes[j];
					var codeitemid = obj.uid.split("`");
					
//					alert(codeitemid);
//					alert('${implementForm.object_id}');
					
					if(codeitemid[0]=='${implementForm.object_id}')				
					{
						var codeid = codeitemid[3]+codeitemid[0];
						selectedClass("treeItem-text-"+obj.id);
						parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid="+codeid;
						Node = true;
						break;
					}					
				}
			}
			if(Node==true)
				break;
		}
*/	
		var flag = true;//有对象就定位到该对象上面，否则定位到第一个   zhaoxg add 2014-12-31
		for(var j=0;j<obj.childNodes.length;j++)
		{
			var tempobj=obj.childNodes[j];
			if(tempobj)
			{	
				if(tempobj && tempobj.uid) {
					var codeitemid = tempobj.uid.split("`");
					if(codeitemid[0]=='${implementForm.object_id}')
					{
						flag = false;
						var codeid = codeitemid[3]+codeitemid[0];
						selectedClass("treeItem-text-"+tempobj.id);
						parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid="+codeid;
					}
				}
			}
		}
		if(flag){
			var tempobj=obj.childNodes[0];
			if(tempobj && tempobj.uid) {
				var codeitemid = tempobj.uid.split("`");
				var codeid = codeitemid[3]+codeitemid[0];
				selectedClass("treeItem-text-"+tempobj.id);
				parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid="+codeid;
			}
		}
	}else
	{
		if(obj && obj.uid) {
			var codeitemid = obj.uid.split("`");
			var codeid = codeitemid[3]+codeitemid[0];
			selectedClass("treeItem-text-"+obj.id);
			parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid="+codeid;
		}
	}




/*
	if(obj)
	{	var codeitemid = obj.uid.split("`");
	    var codeid = codeitemid[3]+codeitemid[0];
		selectedClass("treeItem-text-"+obj.id);
		parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_query=link&planid=${implementForm.planid}&codeid="+codeid;
	}
	*/
	var copyObjTypeid,copyObjTypeDesc,pastObjTypeid,pastObjTypeDesc;
	function copyTargetCard()
	{	
    	var currnode=Global.selectedItem;
    	if(currnode==null)
    	    return;   

    	copyObjTypeid=currnode.uid; 
    	copyObjTypeDesc=currnode.text;
    	if(copyObjTypeid=='root')
    	{
    		copyObjTypeid=obj.uid; 
    		copyObjTypeDesc=obj.text;
    	}
    	
    	//检查是否是考核对象范围内的节点   
    	if(testIsKhObj(copyObjTypeid.split('`')[0])==false)
    	{
    		alert(copyObjTypeDesc+' 不是考核对象,请重新选择！');
    		return;
    	}
		document.getElementById("isCanClick").value='1'
	}
	function pastTargetCard()
	{
		if(document.getElementById("isCanClick").value==='0')
			return;
		var currnode=Global.selectedItem;
    	if(currnode==null)
    	    return;   
    	pastObjTypeid=currnode.uid; 
    	pastObjTypeDesc=currnode.text;
    	if(pastObjTypeid=='root')
    	{
    		pastObjTypeid=obj.uid; 
    		pastObjTypeDesc=obj.text;
    	}

    	//检查是否是考核对象范围内的节点   
    	if(testIsKhObj(pastObjTypeid.split('`')[0])==false)
    	{
    		alert(pastObjTypeDesc+' 不是考核对象,请重新选择！');
    		return;
    	}
    	var pastObjId = pastObjTypeid.split('`')[0];
    	
    	if(confirm('确认将['+copyObjTypeDesc+']的目标卡指标/任务粘帖给['+pastObjTypeDesc+']吗？'))
    	{
    		var delOldTarget = 0;
    		if(confirm('确认清除目标卡中原有的指标/任务？'))
    			delOldTarget = 1;
    		parent.mil_body.location="/performance/implement/performanceImplement/targetCardSet.do?b_pastPoint=link&planid=${implementForm.planid}&delOldTarget="+delOldTarget+"&pastObjId="+pastObjTypeid.split('`')[0]+"&copyObjId="+copyObjTypeid.split('`')[0]+"&codeid="+pastObjTypeid.split('`')[3]+pastObjTypeid.split('`')[0];
    	}
	}
</SCRIPT>
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
  //xus 19/12/16 【55716 】绩效管理：考核实施，目标卡制定，不展开单位后有留白
  document.body.style.height='99%';
  function resize_table() {
	  //如果是ie下，菜单随着拖动会有一块空白，这里算出iframe的宽度，对应的调整
	  if(getBrowseVersion())
	  	document.getElementById("table_menu").style.width = parent.document.getElementById("center_iframe").clientWidth + "px";
  }
  resize_table();
</script>
</html:form>
