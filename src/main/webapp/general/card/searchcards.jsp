<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%



    // 在标题栏显示当前用户和日期 2004-5-10 
    String url="";
    String userName = null;
    String userFullName=null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String fields="";
	String tables="";
	String bosflag="";
	 String themes="default";
	String isselfinfo="0";
	if(userView != null){
	  css_url=userView.getCssurl();
	bosflag=userView.getBosflag();    
	if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
      CardTagParamForm cardTagParamForm=(CardTagParamForm)session.getAttribute("cardTagParamForm");    
      String userpriv=cardTagParamForm.getUserpriv();
      userName=userView.getUserName();
      userFullName=userView.getUserFullName();
      url=userView.getBosflag().toLowerCase();
      if(!userView.isSuper_admin())
      {
           if(userpriv!=null&&userpriv.equals("selfinfo"))
	       {
	           fields=userView.getEmp_fieldpriv().toString();
	           tables=userView.getEmp_tablepriv().toString();
	           isselfinfo="1";
	       }else
	       {
	           fields=userView.getFieldpriv().toString();
	           tables=userView.getTablepriv().toString();
	       }
	       if(fields==null||fields.length()<=0)
	         fields=",";
	       if(tables==null||tables.length()<=0)
	         tables=",";  
      }
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
	String superUser="0";
	if(userView.isSuper_admin())
	  superUser="1";
	  EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
%>
<%String aurl = (String)request.getServerName();
  String port=request.getServerPort()+"";  
  String url_p=SystemConfig.getServerURL(request);
  
  String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
	/****
	打印预览：2304003,2604003,0315003,250120703
	导出pdf：2304001,2504001,2604001,0315001,250120701
	导出word：2304002,2504002,2604002,0315002,250120702
	*/
	String printFuncId="2304003,2604003,0315003,250120703";
	String pdfFuncId="2304001,2504001,2604001,0315001,250120701";
	String wordFuncId="2304002,2504002,2604002,0315002,250120702";
	boolean printFlag=false;
	boolean pdfFlag=false;
	boolean wordFlag=false;
	for(int i=0;i<printFuncId.split(",").length;i++){
		if(userView.hasTheFunction(printFuncId.split(",")[i])){
			printFlag=true;
			break;
		}
	}
	for(int i=0;i<pdfFuncId.split(",").length;i++){
		if(userView.hasTheFunction(pdfFuncId.split(",")[i])){
			pdfFlag=true;
			break;
		}
	}
	for(int i=0;i<wordFuncId.split(",").length;i++){
		if(userView.hasTheFunction(wordFuncId.split(",")[i])){
			wordFlag=true;
			break;
		}
	}
	
	
%>
<script type='text/javascript' src='../../../module/utils/js/template.js'></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
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
<script language="javascript" src="/js/dict.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style>
body{  

	font-size: 12px;
	margin:4 0 0 4;
}
</style>

<script language="javascript">
	var isIE=false;
	if(!!window.ActiveXObject || "ActiveXObject" in window)
			isIE=true;

    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

<script language="javascript">

function change()
{
    var id=$F('personlists');
    var userbase=$F('userbase');
   // cardTagParamForm.action="/general/card/searchcard.do?b_showcard=link&a0100=" + id+"&inforkind=${cardTagParamForm.inforkind }";
   // cardTagParamForm.target="tbi_body";
    var showCard=document.getElementById("showCard");
    showCard.style.height=(parent.document.body.clientHeight-45)+"px";
    showCard.innerHTML="<iframe style=\"z-index: -1\" width=\"100%\" height=\"95%\" src=\"/general/card/searchcard.do?b_showcard=link&a0100="+id+"&userbase="+userbase+"&inforkind=${cardTagParamForm.inforkind }\" frameborder=\"no\" name=\"tbi_body\" scrolling=\"auto\" ></iframe>";
}
function getPersonlist(outparamters)
{
  var personlist=outparamters.getValue("personlist");
 if(personlist)
  AjaxBind.bind(cardTagParamForm.personlists,/*$('personlist')*/personlist);
 // change();
}
function searchall()
{
   var hashvo=new ParameterSet();
   if("${cardTagParamForm.inforkind}"=="1")
       hashvo.setValue("dbname",cardTagParamForm.userbase.value);
   hashvo.setValue("inforkind",${cardTagParamForm.inforkind});
   hashvo.setValue("plan_id","${cardTagParamForm.plan_id}");
   var request=new Request({method:'post',onSuccess:getPersonlist,functionId:'07020100007'},hashvo);
}
function searchdbname()
{
   var hashvo=new ParameterSet();
   if(${cardTagParamForm.inforkind}=="1")
       hashvo.setValue("dbname",cardTagParamForm.userbase.value);
   hashvo.setValue("inforkind",${cardTagParamForm.inforkind});
   hashvo.setValue("plan_id","${cardTagParamForm.plan_id}");
   var request=new Request({method:'post',onSuccess:getchangedbnamePersonlist,functionId:'07020100007'},hashvo);
}
function getchangedbnamePersonlist(outparamters)
{
  var personlist=outparamters.getValue("personlist"); 
  AjaxBind.bind(cardTagParamForm.personlists,/*$('personlist')*/personlist);
  //change();
}

function card_query_comrow(infor,dbpre_arr,query_type,row_num)
{
   var strdb="";
   if(dbpre_arr)
     strdb=dbpre_arr.toString();
     var dw=600,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
   var strurl="/general/query/common/select_query_fields.do?b_init=link`type="+infor+"`show_dbpre="+strdb+"`query_type="+query_type+"`row_num="+row_num;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
   if(isIE6()){
   		dw += 10;
        dh += 10;
   }
   var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
   var isOpera = userAgent.indexOf("Opera") > -1;
   var objlist;
   
   if(userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera){
	   objlist=window.showModalDialog(iframe_url,null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth="+dw+"px;dialogHeight="+dh+"px;resizable=yes;status=no;");  
   	   openReturn(objlist);
   }
   else{
	   window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=780px,height=500px');
   }
}

function openReturn(objlist){
	 /*传回来的对象类型发生变化啦 instanceof Array*/
	   if(objlist)
	   {
	     var objarr=new Array();
	     for(var i=0;i<objlist.length;i++)
	         objarr[i]=objlist[i];
	     
		   var hashvo=new ParameterSet();
		   
		   if(${cardTagParamForm.inforkind}=="1"){
			      hashvo.setValue("dbname",cardTagParamForm.userbase.value);
			   }
		   
		     hashvo.setValue("persons",objarr);
		     hashvo.setValue("inforkind",${cardTagParamForm.inforkind});
		     var request=new Request({method:'post',onSuccess:getchangedbnamePersonlist,functionId:'07020100006'},hashvo);
	   }

}

function search()
{
   var db_arr=new Array();
   var persons=null;
   if(${cardTagParamForm.inforkind}=="1"){
      db_arr[0]=cardTagParamForm.userbase.value;
      card_query_comrow("${cardTagParamForm.inforkind}",db_arr,"1",'500');
   }else{
     if(${cardTagParamForm.inforkind}==4){
       card_query_comrow("3",db_arr,"1",'500');
     }else if(${cardTagParamForm.inforkind}==6){
       card_query_comrow("9",db_arr,"1",'500');
     }else{
        card_query_comrow("${cardTagParamForm.inforkind}",db_arr,"1",'500');
     }
   }   
/*    if(persons!=null && persons.length>0){
     hashvo.setValue("persons",persons);
     hashvo.setValue("inforkind",${cardTagParamForm.inforkind});
     var request=new Request({method:'post',onSuccess:getchangedbnamePersonlist,functionId:'07020100006'},hashvo);
   } */
}
</script>
<script language="javascript">

function returnhome(ver,tar)
{
    if(ver=="5")
    {
    	if(tar=="hcm"){
    	cardTagParamForm.action="/templates/index/hcm_portal.do?b_query=link";
    	}else{
       cardTagParamForm.action="/templates/index/portal.do?b_query=link";
    	}
       cardTagParamForm.target="il_body";
       cardTagParamForm.submit();
    }else
    {
       cardTagParamForm.action="/system/home.do?b_query=link";
       if(tar=="hl4")
           cardTagParamForm.target="il_body";
       else
           cardTagParamForm.target="i_body";
       cardTagParamForm.submit();
    }
    
}

function excecuteWord(flag,fileFlag)
{
        var tab_id="${cardTagParamForm.tabid}";        
        if(tab_id==null||tab_id.length<=0||tab_id=="-1")
        {
           alert("请选择登记表！");
           return false;
        }
        var hashvo=new HashMap();
        var id=$F('personlists');
        if(flag=='all'||flag=='1'){
	        var element=$('personlists');
	        var nids=new Array();
	        for(var i = 0; i < element.childNodes.length; i++) {
	             var node = element.childNodes[i];
	             nids.push(node.value);
	         }  
	        if(element.childNodes.length>0) {     
	          hashvo.put("nid",nids);
	        }else
	        { 
	          nids.push("");
	          hashvo.put("nid",nids);
	        }
        }else{
	        hashvo.put("nid",id);
        }
        hashvo.put("fileFlag",fileFlag);
        hashvo.put("flag",flag);
        hashvo.put("cardid","${cardTagParamForm.tabid}");
        if(${cardTagParamForm.cardparam.queryflagtype}==1)
        {
           hashvo.put("cyear","${cardTagParamForm.cardparam.cyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==3)
        {
           hashvo.put("cyear","${cardTagParamForm.cardparam.csyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==4)
        {
           hashvo.put("cyear","${cardTagParamForm.cardparam.csyear}");
        }        
        hashvo.put("userpriv","noinfo");
        hashvo.put("istype","1");        
        hashvo.put("cmonth","${cardTagParamForm.cardparam.cmonth}");
        hashvo.put("season","${cardTagParamForm.cardparam.season}");
        hashvo.put("ctimes","${cardTagParamForm.cardparam.ctimes}");
        hashvo.put("cdatestart","${cardTagParamForm.cardparam.cdatestart}");
	    hashvo.put("cdateend","${cardTagParamForm.cardparam.cdateend}");
	    hashvo.put("infokind","${cardTagParamForm.inforkind}");
	    hashvo.put("plan_id","${cardTagParamForm.plan_id}");
	    hashvo.put("querytype","${cardTagParamForm.cardparam.queryflagtype}");
	    <logic:equal name="cardTagParamForm" property="inforkind" value="1">	
	       hashvo.put("userbase",cardTagParamForm.userbase.value);
        </logic:equal>
        <logic:equal name="cardTagParamForm" property="inforkind" value="5">
	      hashvo.put("userbase","Usr");
        </logic:equal>
        <logic:notEqual name="cardTagParamForm" property="inforkind" value="1">
	       <logic:notEqual name="cardTagParamForm" property="inforkind" value="5">
	         hashvo.put("userbase","BK");
	       </logic:notEqual>
        </logic:notEqual>

      	showWait(true,fileFlag);
        Rpc({functionId:'07020100026',async:true,success:showWord,scope:this},hashvo);  
	
}
function showWait(flag,fileFlag){
	 if(flag){
	 	if(fileFlag=='pdf'){
		 Ext.MessageBox.wait("正在执行导出PDF操作，请稍候...", "等待");
	 	}else{
	 	 Ext.MessageBox.wait("正在执行导出WORD操作，请稍候...", "等待");
	 	}
	 }
	 else
		Ext.MessageBox.close(); 	 
	}

function showWord(outparamters)
{
	var res=Ext.decode(outparamters.responseText);// 
	showWait(false);
	if(res.succeed){
		if(!res.errorMsg){
		   	var url=res.url;
			url=decode(url)
		    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
		}else{
			Ext.showAlert(res.errorMsg);
		}
	}else{
		Ext.showAlert(res.message);
	}
}

function printView()
{
   var target_url="/general/card/searchcard.do?b_print=link&userbase=${cardTagParamForm.userbase}&infokind=${cardTagParamForm.inforkind}&tabid=${cardTagParamForm.tabid}";
   var scrheigh=window.screen.availHeight;  //屏幕可用工作区高度 
   var scewidth=window.screen.availWidth; //屏幕可用工作区宽度   
   window.open(target_url,'tbi_body',"width="+scewidth+",height="+scrheigh+""); //tbi_body
}

function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;
      var DBType="${cardTagParamForm.dbType}";
      var UserName="<%=userName%>";   
      var obj = document.getElementById('CardPreview1');
      if(obj==null)
      {
         return false;
      }   
      var superUser="<%=superUser%>";
      var menuPriv="<%=fields%>";
      var tablePriv="<%=tables%>";
      var isselfinfo="<%=isselfinfo%>";
      obj.SetSelfInfo(isselfinfo); 
      obj.SetSuperUser(superUser); 
      obj.SetUserMenuPriv(menuPriv);
      obj.SetUserTablePriv(tablePriv);
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("<%=userFullName%>");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
      return true;
}
function printCard(outparamters)
{
  var personlist=outparamters.getValue("personlist");  
  var inforkind="${cardTagParamForm.inforkind}";    
  var tab_id="${cardTagParamForm.tabid}"; 
  if(tab_id==null||tab_id.length<=0||tab_id=="-1")
  {
           alert("请选择登记表！");
           return false;
  }  
  var obj = document.getElementById('CardPreview1');  
  if(obj==null)
  {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
  }
  obj.SetCardID("${cardTagParamForm.tabid}");
  obj.SetDataFlag("${cardTagParamForm.dataFlag}");
  var inforkind="${cardTagParamForm.inforkind}";
  if(inforkind=="1")
    obj.SetNBASE(cardTagParamForm.userbase.value);
  else
    obj.SetNBASE("${cardTagParamForm.userbase}");   
  obj.ClearObjs();  
  for(var i=0;i<personlist.length;i++)
  {     
     obj.AddObjId(personlist[i].dataName);
  }
  try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
  obj.ShowCardModal();
}
function showPrintCard()
{
   if(!AxManager.setup("axContainer", "CardPreview1", 0, 0, showPrintCard, AxManager.cardpkgName))
       return false;
   if(!initCard())
       return;
   var hashvo=new ParameterSet();
   var inforkind="${cardTagParamForm.inforkind}"; 
   if(inforkind=="1")
   hashvo.setValue("dbname",cardTagParamForm.userbase.value);
   else
     hashvo.setValue("dbname","${cardTagParamForm.userbase}");   
   hashvo.setValue("inforkind","${cardTagParamForm.inforkind}");
   hashvo.setValue("plan_id","${cardTagParamForm.plan_id}"); 
   var request=new Request({method:'post',onSuccess:printCard,functionId:'07020100077'},hashvo);
}
function returnH(url,target)
{
    cardTagParamForm.action=url;
    cardTagParamForm.target=target;
    cardTagParamForm.submit();
}

//xyy 20141226处理input键盘想下事件 让上下能选select中的option
var optionNum = -1;  //记录向上或向下的的位置
function updown(e){
	var keycode;
	if(navigator.appName == "Microsoft Internet Explorer"){
		 keycode = event.keyCode;  
    }else{
    	 keycode = e.which;  
    }
	if(keycode==13){
		 this.comSearch();
	}
	      
}  
  function comSearch(){
	var hashvo=new ParameterSet()
	if(${cardTagParamForm.inforkind}=="1"){
		hashvo.setValue("dbname",cardTagParamForm.userbase.value);
	}
	  hashvo.setValue("inforkind",${cardTagParamForm.inforkind});
	  hashvo.setValue("comSearch",1);
	  var a0101=document.getElementById("a0101").value;//取值
	  a0101=a0101;
	  hashvo.setValue("A0101",a0101);
      var request=new Request({method:'post',onSuccess:getchangedbnamePersonlist,functionId:'07020100006'},hashvo);
	}

var hcmflag="<%=url%>";
var printFlag=<%=printFlag%>;
var pdfFlag=<%=pdfFlag%>;
var wordFlag=<%=wordFlag%>;

Ext.onReady(function(){
	var inforkind="${cardTagParamForm.inforkind}";
	var returnvalue="${cardTagParamForm.returnvalue}";
	var home="${cardTagParamForm.home}";
	var searchBtn=Ext.create('Ext.button.Button',{
							width:50,
					        height:'90%',
					        text:'查询',
					        handler:function(){
					        	search();
					        }
	});
	
	var showPrintBtn=Ext.create('Ext.button.Button',{
							width:80,
					        height:'90%',
					        text:'打印预览',
					        handler:function(){
					        	showPrintCard();					        	
					        }
	});
	
	
	var childPdfMenu=Ext.create('Ext.menu.Menu',{
							width: 170,
							plain: true,
						    floating: true,
						    items:[
						    	{
						    		text:(inforkind=='2'||inforkind=='4'||inforkind=='6')?'一机构一文档':'一人一文档',
						    		handler:function(){excecuteWord("1","pdf");}
						    	},{
						    		text:(inforkind=='2'||inforkind=='4'||inforkind=='6')?'多机构一文档':'多人一文档',
						    		handler:function(){excecuteWord("all","pdf");}
						    	}
						    	
						    ]
	});

	var pdfMenu=Ext.create('Ext.menu.Menu',{
						width: 170,
						plain: true,
					    floating: true,
					    items:[
					    		{
					    			text:((inforkind=='2'||inforkind=='4'||inforkind=='6')?'当前机构':'当前人员')+'生成PDF',
					    			handler:function(){excecuteWord("false","pdf");}
					    		},
					    		{
					    			text:((inforkind=='2'||inforkind=='4'||inforkind=='6')?'全部机构':'全部人员')+'生成PDF',
					    			menu:childPdfMenu
					    		}
					    	  ]
	});
	
	var childWordMenu=Ext.create('Ext.menu.Menu',{
							width: 170,
							plain: true,
						    floating: true,
						    items:[
						    	{
						    		text:(inforkind=='2'||inforkind=='4'||inforkind=='6')?'一机构一文档':'一人一文档',
						    		handler:function(){excecuteWord("1","word");}
						    	},{
						    		text:(inforkind=='2'||inforkind=='4'||inforkind=='6')?'多机构一文档':'多人一文档',
						    		handler:function(){excecuteWord("all","word");}
						    	}
						    	
						    ]
	});
	
	var wordMenu=Ext.create('Ext.menu.Menu',{
							width: 170,
							plain: true,
						    floating: true,
						    items:[
						    		{
						    			text:((inforkind=='2'||inforkind=='4'||inforkind=='6')?'当前机构':'当前人员')+'生成WORD',
						    			handler:function(){excecuteWord("false","word");}
						    		},
						    		{
						    			text:((inforkind=='2'||inforkind=='4'||inforkind=='6')?'全部机构':'全部人员')+'生成WORD',
						    			menu:childWordMenu
						    		}
						    	  ]
	});
	
	var wordBtn=Ext.create('Ext.button.Button',{
							width:100,
					        height:'90%',
					        text:'生成WORD',
					        menu:wordMenu
	});
	var pdfBtn=Ext.create('Ext.button.Button',{
							width:80,
					        height:'90%',
					        text:'生成PDF',
					        menu:pdfMenu
	});
	
	var returnBtn=Ext.create('Ext.button.Button',{
							width:50,
					        height:'90%',
					        text:'返回'
	});
	
	var toolbar=Ext.create('Ext.toolbar.Toolbar',{
							 renderTo:'menuItems',
							 border:0,
							 width:400,	
							 height:35
	});
	
	if(inforkind!='5'){
		toolbar.add(searchBtn);
		toolbar.add('-');
		if(inforkind=='2'||inforkind=='4'||inforkind=='1'||inforkind=='6'){
			if(Ext.isIE){
				if(printFlag){
					toolbar.add(showPrintBtn);
					toolbar.add('-');
				}
			}
			if(pdfFlag){
				toolbar.add(pdfBtn);
				toolbar.add('-');
			}
			if(wordFlag){
				toolbar.add(wordBtn);
				toolbar.add('-');
			}
			
		}
		
	}else{
		if(printFlag){
			toolbar.add(showPrintBtn);
			toolbar.add('-');
		}
		if(pdfFlag){
			toolbar.add({xtype:'button',width:80,height:'90%',text:'生成PDF',handler:function(){excecuteWord("false","pdf");}})
			toolbar.add('-');
		}
		
	}
	
	if(returnvalue=='dxts'){
		returnBtn.setHandler(function(){hrbreturn('selfinfo','il_body','cardTagParamForm');},this);
		toolbar.add(returnBtn);
		toolbar.add('-');
	}else if(returnvalue=='dxt'){
		if(inforkind=='1'){//
			returnBtn.setHandler(function(){hrbreturn('emp','il_body','cardTagParamForm');},this);
			toolbar.add(returnBtn);
			toolbar.add('-');
		}else if(inforkind=='4'){//
			returnBtn.setHandler(function(){hrbreturn('org','il_body','cardTagParamForm');},this);
			toolbar.add(returnBtn);
			toolbar.add('-');
		}
	}else if(returnvalue=='home5'){
		if(hcmflag=='hcm'){
			returnBtn.setHandler(function(){returnH('/templates/index/hcm_portal.do?b_query=link','il_body');},this);
		}else{
			returnBtn.setHandler(function(){returnH('/templates/index/portal.do?b_query=link','il_body');},this);
		}
			toolbar.add(returnBtn);
			toolbar.add('-');
	}
	if(home=='1'){//returnH('/system/home.do?b_query=link','il_body');
		returnBtn.setHandler(function(){returnH('/system/home.do?b_query=link','il_body');},this);
		toolbar.add(returnBtn);
		toolbar.add('-');
	}
});
function resizeDiV(){
	var height=parent.document.body.clientHeight;
    var width=document.body.clientWidth;//resizeDiV
    document.getElementById('bodyID').style.height=height+'px';
    document.getElementById('bodyID').style.width=width+'px';
	
}
</script>
<html:form action="/general/card/searchshowcard">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<body id="bodyID" onresize="resizeDiV();" onload="resizeDiV()" style="width: 100%;height: 100%">
<div  style="width: 100%;height:45px; border-bottom:1px solid #C5C5C5">
<table width="90%" height="40px"  align="left"  border="0" cellspacing="1"  align="center" cellpadding="1" <%if("Firefox".equals(browser)){ %>style="margin-top: 3px;margin-bottom: 3px"<%}else{ %>style="margin-top:5px;margin-bottom: 3px"<%} %>>
  <tr>
  <logic:equal name="cardTagParamForm" property="inforkind" value="1">
  <hrms:importgeneraldata showColumn="dbname" valueColumn="pre" flag="false" paraValue="" 
                 sql="cardTagParamForm.dbcond" collection="list" scope="page"/>
                 <bean:size id="length" name="list" scope="page"/>
   <td align="left" nowrap  <logic:lessThan value="2" name="length">style="display: none"</logic:lessThan> style="width:10% " >  
              <bean:message key="label.query.dbpre"/>
                  <html:select name="cardTagParamForm" property="userbase" onchange="javascript:searchdbname()">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  </html:select> 
         </td>
  </logic:equal>
  <logic:equal name="cardTagParamForm" property="inforkind" value="1">
  		 <td align="left" nowrap style="width:8% " >
  		 	 <select name="personlists"  style="width:expression_r((this.offsetWidth>80)?'auto':'80')"   onchange="change()">
  		 	 	<option value="1111"></option>
	         </select> 
  		  </td>
  </logic:equal>
  <logic:notEqual name="cardTagParamForm" property="inforkind" value="1">
  <td align="left" nowrap width="180">  
    <logic:equal name="cardTagParamForm" property="inforkind" value="4">                   
               <select name="personlists"  onchange="change()"> 
    </logic:equal>
    <logic:notEqual name="cardTagParamForm" property="inforkind" value="4">                   
               <select name="personlists"  onchange="change()"> 
    </logic:notEqual>
		        <option value="1111"></option>
               </select> 
  </td>
 </logic:notEqual>
  <td align="left" nowrap width="495">    
  		<div id="menuItems"></div>
        </td>
        <td align="right" nowrap >
        	<logic:equal name="cardTagParamForm" property="inforkind" value="1">     
   	  	 	<bean:message key="label.title.name"/>
   	  	 	<input type="text" name="a0101" value=""  style="width:100px;font-size:10pt;text-align:left" id="a0101" onkeydown="updown(event);" >
   	  	 	</logic:equal>
   	  	 	<logic:equal name="cardTagParamForm" property="inforkind" value="2">     
   	  	 	<bean:message key="general.inform.org.organizationName"/>
   	  	 	<input type="text" name="a0101" value=""  class="" style="width:80px;font-size:10pt;text-align:left" id="a0101"  onkeydown="updown(event);" >
   	  	 	</logic:equal>
   	  	 	<logic:equal name="cardTagParamForm" property="inforkind" value="4">     
   	  	 	<bean:message key="kq.shift.employee.e01a1"/>
   	  	 	<input type="text" name="a0101" value=""  class="" style="width:80px;font-size:10pt;text-align:left" id="a0101"  onkeydown="updown(event);" >
   	  	 	</logic:equal>
   	  	 	<logic:equal name="cardTagParamForm" property="inforkind" value="6">     
   	  	 	<bean:message key="kq.shift.employee.e01a1"/>
   	  	 	<input type="text" name="a0101" value=""  class="" style="width:80px;font-size:10pt;text-align:left;" id="a0101"  onkeydown="updown(event);" >
   	  	 	</logic:equal>
        </td>
   </tr>
  </table>
 </div>
 <div  style="width: 100%;height: 97%" id="showCard">
</div>
 </body>
 <div id="axContainer" style="display:none" > 
 <script language="javascript">
 searchall();
 change();

</script>
</html:form>
