<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.frame.dao.RecordVo"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.org.OrgInformationForm,java.text.SimpleDateFormat"%>
				
<%
	  int i=0;
	    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String nowDate = sdf.format(new Date());
%>
  <logic:equal name="orgInformationForm" property="isrefresh" value="yes">
    <script language="javascript">
       //parent.mil_menu.location.reload();
   </script>
  </logic:equal>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">

   function openOrgTreeDialog()
   {
      
        orgInformationForm.action="/org/orginfo/savetransferorglist.do?b_save=link";
        orgInformationForm.submit();
        var thecodeurl="/org/orginfo/searchtransferorgtree.do?b_query=link"; 
        var popwin= window.showModelessDialog(thecodeurl, "theArr", 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
       
   }
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
   function deleterec()
   {
       var len=document.orgInformationForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.orgInformationForm.elements[i].type=="checkbox"&&document.orgInformationForm.elements[i].name!="box")
           {
              if(document.orgInformationForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert("没有选择记录！");
          return false;
       }
      //
      if(confirm("<bean:message key="label.org.isdeletedesc"/>"))
      {
      
        if(selectcheckeditem()!=null)
        {
           var hashvo=new ParameterSet();
           hashvo.setValue("orgcodeitemid", selectcheckeditem());
           var request=new Request({method:'post',onSuccess:deleteorg,functionId:'16010000022'},hashvo);
         }
      }
   }
   
  
   function selectcheckeditem()
   {
      	var a=0;
	var b=0;
	var selectid=new Array();
	var a_IDs=eval("document.forms[0].orgcodeitemid");	
	var nums=0;		
	for(var i=0;i<document.forms[0].elements.length;i++)
	{			
	   if(document.forms[0].elements[i].type=='checkbox'&&document.orgInformationForm.elements[i].name!="box")
	   {		   			
		nums++;
	   }
        }
	if(nums>1)
	{
	    for(var i=0;i<document.forms[0].elements.length;i++)
	    {			
		if(document.forms[0].elements[i].type=='checkbox'&&document.orgInformationForm.elements[i].name!="box")
		{	
		   if(document.forms[0].elements[i].checked==true)
		   {
			   selectid[a++]=a_IDs[b].value;						
		   }
		   b++;
		}
	    }
	}
	if(nums==1)
	{
	   for(var i=0;i<document.forms[0].elements.length;i++)
	   {			
	      if(document.forms[0].elements[i].type=='checkbox'&&document.orgInformationForm.elements[i].name!="box")
	      {	
		  if(document.forms[0].elements[i].checked==true)
		  {
			  selectid[a++]=a_IDs.value;						
		  }
	      }
	   }
	}
			
	if(selectid.length==0)
	{
		alert(REPORT_INFO9+"!");
		return ;
	}
	return selectid;	
   }  
   
   function combineorg()
   {
   		if(selectcheckedorg())
   		{
   			alert("您选择了虚拟机构，不允许此操作！");
   			return;
   		}
   		<%if(version){%>
   		if(candoit()){
   			alert(ORG_NEW_NOT_REVOKE);
   			return;
   		}
   		<%}%>
   	   var len=document.orgInformationForm.elements.length;
       var uu = 0;
       var a=0;
       var selectcodeitemids ="";
       var maxstartdate="";
       for (var i=0;i<len;i++)
       {
           if (document.orgInformationForm.elements[i].type=="checkbox"&&document.orgInformationForm.elements[i].name!="box")
           {
           		a++;
           		if($F(document.orgInformationForm.elements[i].name)=='true'){
           			uu += 1;
           			//alert(selectcodeitemids);
           			selectcodeitemids+=$F("orgcodeitemid"+a)+"`";
           			<%if(version){ %>
           			if(maxstartdate==""){
           				maxstartdate=$F("start_date"+a)
           			}else{
           				var tt=$F("start_date"+a);
           				if(tt!=null&&tt!=""){
           					var tnew=(tt).replace(/-/g, "/");
           					var told=(maxstartdate).replace(/-/g, "/");
		   					var dnew=new Date(Date.parse(tnew));
		   					var dold=new Date(Date.parse(told));
		   					if(dnew>dold){
		   						maxstartdate=tt;
		   					}
           				}
           			}
           			<%}%>
           		}
           }
       }
       if(uu<2)
       {
          alert(CHOICE_TWO_UNITE_NOTE_LEASTWAYS);
          return false;
       }
         orgInformationForm.action="/org/orginfo/searchorglist.do?b_initcombine=link&selectcodeitemids="+$URL.encode(getEncodeStr(selectcodeitemids.substring(0,selectcodeitemids.length-1)))+"&maxstartdate="+$URL.encode(maxstartdate);
         orgInformationForm.submit(); 
        
   }
   function transfer()
   {
   		if(selectcheckedorg())
   		{
   			alert("您选择了虚拟机构，不允许此操作！");
   			return;
   		}
   		
   		<%if(version){%>
   		if(candoit()){
   			alert(ORG_NEW_NOT_REVOKE);
   			return;
   		}
   		<%}%>
   	   var len=document.orgInformationForm.elements.length;
       var uu;
       var a=0;
       var maxstartdate="";
       for (var i=0;i<len;i++)
       {
           if (document.orgInformationForm.elements[i].type=="checkbox"&&document.orgInformationForm.elements[i].name!="box")
           {
           	a++;
              if(document.orgInformationForm.elements[i].checked==true)
              {
                uu="dd";
                <%if(version){ %>
           			if(maxstartdate==""){
           				maxstartdate=$F("start_date"+a)
           			}else{
           				var tt=$F("start_date"+a);
           				if(tt!=null&&tt!=""){
           					var tnew=(tt).replace(/-/g, "/");
           					var told=(maxstartdate).replace(/-/g, "/");
		   					var dnew=new Date(Date.parse(tnew));
		   					var dold=new Date(Date.parse(told));
		   					if(dnew>dold){
		   						maxstartdate=tt;
		   					}
           				}
           			}
           			<%}%>
                //break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(ORG_ORGINFO_INFO01);
          return false;
       }
       //alert(maxstartdate);
          orgInformationForm.action="/org/orginfo/searchorglist.do?b_transfer=link&maxstartdate="+maxstartdate;
          orgInformationForm.submit(); 
   }
   function deleter(isrefresh,code,transfercodeitemidall,issuperuser,manageprive,newid,firstnode,newcodeitemdesc,codeleng,index,isnewcombineorg)
   {
   		if(isrefresh=='delete')
   		{
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			if(currnode==null)
					return;
			if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase())
					currnode.childNodes[i].remove();
			}
		}
		if(isrefresh=='transfer')
   		{
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			var issuperuser = issuperuser;
   	 		var manageprive = manageprive;
   	 		//var new_id = newid;
			if(currnode==null)
					return;
			var oldnode = new Object();
			var root = currnode.root();
			var nodeid = "";
			a(root,transfercodeitemidall.toUpperCase());
			function a(root,name)
			{
				for(var z=0;z<=root.childNodes.length-1;z++){
					if(name==root.childNodes[z].uid){
						for(var q=0;q<=root.childNodes[z].childNodes.length-1;q++)
						{
							nodeid = root.childNodes[z].childNodes[q].uid;
						}
					}
					else
						a(root.childNodes[z],name);
				}
			}
			if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
					oldnode.uid = currnode.childNodes[i].uid;
					oldnode.text = currnode.childNodes[i].text;
					oldnode.action = currnode.childNodes[i].action;
					oldnode.title = currnode.childNodes[i].title;
					oldnode.imgurl = currnode.childNodes[i].imgurl;
					oldnode.Xml = currnode.childNodes[i].Xml;
					var codesetid = currnode.childNodes[i].uid.substring(0,2);
					currnode.childNodes[i].remove();
					var kind = '0';
			   	 	if(codesetid=='UM')
			   	 		kind='1';
			   	 	else if(codesetid=='UN')
			   	 		kind='2';
			   	 	var imgurl;
		   	 		if(codesetid=='UM')
		   	 			imgurl="/images/dept.gif";
		   	 		else if(codesetid=='UN')
		   	 			imgurl="/images/unit.gif";
					var action = "/org/orginfo/searchorglist.do?b_search=link&code="+newid+"&kind="+kind;
					var xml = "/common/org/loadtree?params=child&treetype=org&parentid="  + newid + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorglist.do&target=mil_body";
					if(codesetid!='@K'){
						if(currnode.load)
							parent.frames['mil_menu'].addtrs(transfercodeitemidall,codesetid+newid,oldnode.text,action,"mil_body",oldnode.title,imgurl,xml);
						else
							currnode.expand();
					}
				}
			}
		}
		if(isrefresh=='combineorg')
		{
			var currnode=parent.frames['mil_menu'].Global.selectedItem;
			var codesetid = firstnode.substring(0,2);
			var imgurl;
			var newnode = new Object();
   	 		if(codesetid=='UM')
   	 			imgurl="/images/dept.gif";
   	 		else if(codesetid=='UN')
   	 			imgurl="/images/unit.gif";
			if(currnode==null)
					return;
		    var removeNode = null;//记录合并到其中一个机构数据  wangb 30779  20170818 
			if(codesetid!='@K'){
				if(currnode.load)
					for(var i=0;i<=currnode.childNodes.length-1;i++){
						if(code.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
								removeNode = currnode.childNodes[i]; //记录合并到其中一个机构数据  wangb 30779  20170818
								currnode.childNodes[i].remove();
						}
					}
		    }
			if(isnewcombineorg=='yes'){
		   	 	var pt = currnode.getLastChild();
		   	 	if(pt.uid.indexOf(firstnode)==-1){
		   	 		//return;
			   	 	var uid = firstnode;
			   	 	var text = newcodeitemdesc;
			   	 	var title = newcodeitemdesc;
			   	 	var issuperuser = issuperuser;
			   	 	var manageprive = manageprive;
			   	 	var kind = '0';
			   	 	if(codesetid=='UM')
			   	 		kind='1';
			   	 	else if(codesetid=='UN')
			   	 		kind='2';
			   	 	var orgtype = "org";
			   	 	var action = "/org/orginfo/searchorglist.do?b_search=link&code="+firstnode.substring(2,firstnode.length)+"&kind="+kind+"&orgtype="+orgtype;
			   	 	var xml = "/common/org/loadtree?params=child&treetype=org&parentid="  + firstnode.substring(2,firstnode.length) + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorglist.do&target=mil_body";
			   	 	if(currnode==currnode.root()&&code.length>0)
			   	 		currnode = currnode.getFirstChild();
			   	 	if(currnode.load)
			   	 	{
			   	 		var imgurl;
			   	 		if(codesetid=='UM'){
			   	 			if('${orgInformationForm.vorganization}'=='0')
			   	 			{
			   	 				imgurl="/images/dept.gif";
			   	 			}else
			   	 			{
			   	 				imgurl="/images/vdept.gif";
			   	 			}
			   	 		}
			   	 		else if(codesetid=='UN'){
			   	 			if('${orgInformationForm.vorganization}'=='0')
			   	 			{
			   	 				imgurl="/images/unit.gif";
			   	 			}else
			   	 			{
			   	 				imgurl="/images/vroot.gif";
			   	 			}
			   	 		}
			   	 		//var tmp = new xtreeItem(uid,text,action,"mil_body",title,imgurl,xml);
			   	 		//currnode.expand();
			   	 		if(codesetid!='@K' && index==0)
			   	 			parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
		   	 		}else
		   	 			currnode.expand();
	   	 		}
			}else{
				/*for(var i=0;i<=currnode.childNodes.length-1;i++){
					//if(firstnode.toUpperCase()==currnode.childNodes[i].uid.toUpperCase()){
						newnode.uid = currnode.childNodes[i].uid;
						newnode.text = currnode.childNodes[i].text;
						newnode.action = currnode.childNodes[i].action;
						newnode.title = currnode.childNodes[i].title;
						newnode.xml = currnode.childNodes[i].Xml;
					}
				}*/
				if(index==0) //重新把合并为其中一个机构的数据 添加进来 wangb 20170818
					parent.frames['mil_menu'].add(removeNode.uid,newcodeitemdesc,removeNode.action,"mil_body",removeNode.title,imgurl,removeNode.Xml);
			}
			
		}
   }
   
   function treeReload(a_code,codesetid) {
	   var url = parent.frames['mil_menu'].location.href;
	   if(url.indexOf("?") > -1) {
		   if(url.indexOf("orgId=") < 0) {
			   url += "&orgId="+codesetid+a_code
		   } else {
			   var params = url.split("?")[1].split("&");
	           url = url.split("?")[0];
	           for (var i=0;i<params.length;i++) {
	               if(i == 0) {
	                   url += "?";
	               } else {
	                   url += "&";
	               }
	               
	               var param = params[i].split("=");
	               if(param[0] == "orgId"){
	                   url += "orgId=" + codesetid+a_code;
	               } else {
	                   url += params[i];
	               }
	           }
		   }
	   } else {
		   url += "?orgId="+codesetid+a_code
	   }
	   
	   
	   parent.frames['mil_menu'].location.href = url;
   }
   
   function save(isrefresh,a_code,codesetid,codeitemid,codeitemdesc,issuperuser,manageprive)
   {
   	 if(isrefresh=='save')
   	 {
   	 	var currnode=parent.frames['mil_menu'].Global.selectedItem;
   	 	if(currnode==null)
   	 		parent.frames['mil_menu'].location.reload();
   	 	
   	    currnode = currnode.root();
	   	 if(currnode) {
	   		var itemNode = getTreeItem(codesetid+a_code, currnode);
            if(itemNode) {
            	itemNode.select();
                currnode = itemNode;
            }
	     }
   	 	
   	 	var pt = currnode.getLastChild();
   	 	if(pt!=null&&pt.uid==codesetid+a_code+codeitemid)
   	 		return;
   	 	var uid = codesetid+a_code+codeitemid;
   	 	var text = codeitemdesc;
   	 	var title = codeitemdesc;
   	 	var issuperuser = issuperuser;
   	 	var manageprive = manageprive;
   	 	var kind = '0';
   	 	if(codesetid=='UM')
   	 		kind='1';
   	 	else if(codesetid=='UN')
   	 		kind='2';
   	 	var orgtype = "";
   	 	if('${orgInformationForm.vorganization}'=='0')
   	 	{
   	 		orgtype="org";
   	 	}else if('${orgInformationForm.vorganization}'=='1')
   	 	{
   	 		orgtype="vorg";
   	 	}
   	 	var action = "/org/orginfo/searchorglist.do?b_search=link&code="+a_code+codeitemid+"&kind="+kind+"&orgtype="+orgtype;
   	 	var xml = "/common/org/loadtree?params=child&treetype=org&parentid="  + a_code+codeitemid + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorglist.do&target=mil_body";
   	 	if(currnode.root().uid==currnode.uid&&a_code.length>0)
   	 		currnode = currnode.getFirstChild();
   	 	if(currnode.load)
   	 	{
   	 		var imgurl;
   	 		if(codesetid=='UM'){
   	 			if('${orgInformationForm.vorganization}'=='0')
   	 			{
   	 				imgurl="/images/dept.gif";
   	 			}else
   	 			{
   	 				imgurl="/images/vdept.gif";
   	 			}
   	 		}
   	 		else if(codesetid=='UN'){
   	 			if('${orgInformationForm.vorganization}'=='0')
   	 			{
   	 				imgurl="/images/unit.gif";
   	 			}else
   	 			{
   	 				imgurl="/images/vroot.gif";
   	 			}
   	 		}
   	 		if(codesetid!='@K')
   	 			parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
   	 	}else
   	 		currnode.expand();
   	 }
   }
   
   
   
   function update(isrefresh,codesetid,codeitemid,codeitemdesc)
   {
   	if(isrefresh=='update')
   	{
   		var currnode=parent.frames['mil_menu'].Global.selectedItem;
   		if(currnode.load)
			for(var i=0;i<=currnode.childNodes.length-1;i++){
				if((codesetid+codeitemid)==currnode.childNodes[i].uid)
					currnode.childNodes[i].setText(codeitemdesc);
   		}
   	}
   }
   
   function bolish()
   {
   		if(selectcheckedorg())
   		{
   			alert("您选择了虚拟机构，不允许此操作！");
   			return;
   		}
   		<%if(version){%>
   		if(candoit()){
   			alert(ORG_NEW_NOT_REVOKE);
   			return;
   		}
   		<%}%>
   	   var len=document.orgInformationForm.elements.length;
       var uu;
       var a=0;
       var maxstartdate='';
       for (var i=0;i<len;i++)
       {
       		
           if (document.orgInformationForm.elements[i].type=="checkbox"&&document.orgInformationForm.elements[i].name!="box")
           {
           	a++;
              if(document.orgInformationForm.elements[i].checked==true)
              {
                uu="dd";
               // break;
               <%if(version){ %>
           			if(maxstartdate==""){
           				maxstartdate=$F("start_date"+a)
           			}else{
           				var tt=$F("start_date"+a);
           				if(tt!=null&&tt!=""){
           					var tnew=(tt).replace(/-/g, "/");
           					var told=(maxstartdate).replace(/-/g, "/");
		   					var dnew=new Date(Date.parse(tnew));
		   					var dold=new Date(Date.parse(told));
		   					if(dnew>dold){
		   						maxstartdate=tt;
		   					}
           				}
           			}
           			<%}%>
               }
           }
       }
       if(uu!="dd")
       {
          alert(NOTING_SELECT);
          return false;
       }
       orgInformationForm.action="/org/orginfo/searchorglist.do?b_bolish=link&maxstartdate="+maxstartdate;
       orgInformationForm.submit();
   }
   function savemove(isrefresh)
   {
   		if(isrefresh=='move')
   		{
   			var currnode=parent.frames['mil_menu'].Global.selectedItem;
   			currnode.clearChildren();
   			currnode.collapse();
   			currnode.load = false;
   			currnode.expand();
   		}
   }
   
   function replacestr(url,v){
   		window.location.target='mil_body';
   		window.location.href=url+'&codeitemdesc='+$URL.encode(getEncodeStr(v));
   }
   
 //获取分类树上对应的节点
   function getTreeItem(itemid, currnode) {
	   if(currnode) {
           if(itemid==currnode.uid) {
               return currnode;
           }
           
           var itemCode = currnode.uid.substring(2);
           var selectCode = itemid.substring(2,itemCode.length + 2);
           if(selectCode != itemCode && "root" != currnode.uid){
                return false;
           } 
           
           if(!currnode.load){
               currnode.expand();
           }

           var childNode;
           for (var i = 0; i < currnode.childNodes.length; i++) {
               childNode = currnode.childNodes[i];
               if(childNode.uid == "Loading..."){
                   childNode = undefined;
                   continue;
               }
               
               childNode = getTreeItem(itemid, childNode);
               if(childNode)
                   break;
           }
           
           if(childNode)
               return childNode;
       }
   }
</script>
<script   language="JavaScript">   
  var   r; 
  function ss() {        
    
     execScript("r=msgbox('<bean:message key="label.org.isdelpersonorg"/>',3,'提示')","vbscript"); //返回值必须是全局变量   
     alert(r);
  }
   function deleteorg(outparamters)
   {
     var checkperson=outparamters.getValue("checkperson"); 
     var orgitem=outparamters.getValue("orgitem");
     if(checkperson=="true")
     {
       execScript("r=msgbox('<bean:message key="label.org.isdelpersonorg"/>',3,'提示')","vbscript"); //返回值必须是全局变量  
       if(r==6)//点击是
        {
          orgInformationForm.action="/org/orginfo/searchorglist.do?b_delete=del&delpersonorg=t";
          orgInformationForm.submit();        
        }
        else if(r==7)//点击否
        {
          	orgInformationForm.action="/org/orginfo/searchorglist.do?b_delete=del&delpersonorg=f";
        	orgInformationForm.submit(); 
        }
        else//取消
        {
        return false;
        }     
     }
     else
     {
        orgInformationForm.action="/org/orginfo/searchorglist.do?b_delete=del&delpersonorg=f";
        orgInformationForm.submit(); 
     }     
   }
    function getSelectedOrg()
	{
	 /*operuser中用户名*/
	 var oper_id=window.dialogArguments;	
     var theurl="/org/orginfo/searchorglist.do?br_vieworgtree=link";
  	 //ie弹窗 改为Ext弹窗 兼容多浏览器   wangb 20190306
     var win = Ext.create('Ext.window.Window',{
		id:'exportorgtree',
		title:'<bean:message key="button.output.data"/>',
		width:320,
		height:470,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+theurl+'"></iframe>',
		renderTo:Ext.getBody()
	 });
    
	}
	function getInputOrg()
	{
	 /*operuser中用户名*/
	 var oper_id=window.dialogArguments;	
     var theurl="/org/orginfo/searchorglist.do?br_inputorgtree=link";
	 var win = Ext.create('Ext.window.Window',{
			id:'importorgtree',
			title:'<bean:message key="button.input.data"/>',
			width:325,
			height:470,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+theurl+'"></iframe>',
			renderTo:Ext.getBody()
		 });
 	 
	}
	function importTreeReturn(return_vo){
		if(return_vo)
 		{
 			orgInformationForm.action = "/org/orginfo/searchorgtree.do?b_query=link&code=&query=&idordesc=";
 			orgInformationForm.target="il_body";
 			orgInformationForm.submit();
 		}
	}
	
		function candoit()
	{
		var org_type=eval("document.forms[0].start_date");
		var b=0;
		var selectid=new Array();
		var nums=0;		
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
			if(document.forms[0].elements[i].type=='checkbox'&& document.forms[0].elements[i].name!="box")
			{		   			
				nums++;
			}
		}
		if(nums>1)
		{
			for(var i=0;i<document.forms[0].elements.length;i++)
	    	{			
				if(document.forms[0].elements[i].type=='checkbox'&& document.forms[0].elements[i].name!="box")
				{	
					if(document.forms[0].elements[i].checked==true)
		   			{
		   				var v=org_type[b].value;
		   				if(v!=null&&v!=''){
		   					var tt=v.replace(/-/g, "/");
		   					 var b_date=new Date(Date.parse(tt+' 23:59:59')); 
		   					 if(b_date>=(new Date())){
		   					 	return true;
		   					 }  
		   				}					
		   			}
		   			b++;
				}
	    	}
		}
		if(nums==1)
		{
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&& document.forms[0].elements[i].name!="box")
				{	
					if(document.forms[0].elements[i].checked==true)
					{
						var v=org_type.value;
		   				if(v!=null&&v!=''){
		   					var tt=v.replace(/-/g, "/");
		   					var b_date=new Date(Date.parse(tt+' 23:59:59'));
		   					 if(b_date>=(new Date())){
		   					 	return true;
		   					 }  
		   				}						
					}
				}
			}
		}
		return false;
	}
	
	function selectcheckedorg()
	{
		var org_type=eval("document.forms[0].org_type");
		var b=0;
		var selectid=new Array();
		var nums=0;		
		for(var i=0;i<document.forms[0].elements.length;i++)
		{			
			if(document.forms[0].elements[i].type=='checkbox'&& document.forms[0].elements[i].name!="box")
			{		   			
				nums++;
			}
		}
		if(nums>1)
		{
			for(var i=0;i<document.forms[0].elements.length;i++)
	    	{			
				if(document.forms[0].elements[i].type=='checkbox'&& document.forms[0].elements[i].name!="box")
				{	
					if(document.forms[0].elements[i].checked==true)
		   			{
						if(org_type[b].value=='vorg')
							return true;						
		   			}
		   			b++;
				}
	    	}
		}
		if(nums==1)
		{
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&& document.forms[0].elements[i].name!="box")
				{	
					if(document.forms[0].elements[i].checked==true)
					{
						if(org_type.value=='vorg')
							return true;						
					}
				}
			}
		}
		return false;
	}
	
	//全选
	function selectAll() {
		var checkAll = document.getElementById("checkAll");
		if (checkAll.checked == true) {//选中
			box = document.getElementsByTagName("input");
			for (i = 0; i < box.length; i ++) {
				if (box[i].type=="checkbox") {
					box[i].checked=true;
				}
			}	
		} else {//不选中
			box = document.getElementsByTagName("input");
			for (i = 0; i < box.length; i ++) {
				if (box[i].type=="checkbox") {
					box[i].checked=false;
				}
			}
		}
	}
	function query(){
		var idordesc=document.getElementById("idordesc").value;
		orgInformationForm.action = "/org/orginfo/searchorglist.do?b_search=link&query=1&idordesc="+$URL.encode(getEncodeStr(idordesc));
     	orgInformationForm.submit();
	}
	
	//防止刷新重复提交，屏蔽右键菜单
	document.body.oncontextmenu=function(){return false;};
  </script>
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchorglist">
<div style="width:99%;padding-left:5px;padding-right:5px;">
<table>
<tr><td><span style="vertical-align: top;" >
<bean:message key="train.quesType.type_name"/> &nbsp;
<input type="text" id="idordesc" class="TEXT4" />&nbsp;
<input type="button" class="mybutton" value="查询" onclick="query();" /></span>
</td> 
<logic:notEqual value="<%=nowDate %>" name="orgInformationForm" property="backdate">
  <td style="padding-left:50px;"><bean:message key="label.historytime"/>:${orgInformationForm.backdate }</td>
</logic:notEqual>
</tr></table>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
             <td align="center" class="TableRow" nowrap>
                <input type="checkbox" name="box" id="checkAll" onclick="batch_select(this,'organizationForm.select');" title='<bean:message key="button.all.select"/>'/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.codeitemid.caption"/>&nbsp;
             </td>
				   <%--2020-06-17 贵银 加机构编码 start --%>
			   <td align="center" class="TableRow" nowrap>
				   机构/岗位编码&nbsp;
			   </td>
				   <%--2020-06-17 贵银 加机构编码 end --%>
             <%if(version){ %>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.codeitemid.start_date"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.codeitemid.end_date"/>&nbsp;
             </td> 
             <%} %>                
             <td align="center" class="TableRow" nowrap>
		  <bean:message key="label.edit"/>            	
             </td>        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="orgInformationForm" property="organizationForm.list" indexes="indexes"  pagination="organizationForm.pagination" pageCount="${orgInformationForm.pagerows}" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="orgInformationForm" property="organizationForm.select" value="true" indexes="indexes" />&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>  
           		<%
           		RecordVo vo = (RecordVo)pageContext.getAttribute("element");
           		String end_date = vo.getString("end_date");
           		if(sdf.parse(sdf.format(new Date())).compareTo(sdf.parse(end_date))<=0){ %>          
            	<logic:equal name="element" property="string(codesetid)" value="UN">
            		<logic:equal name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/vroot.gif" border=0>
            		</logic:equal>
            		<logic:notEqual name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/unit.gif" border=0>
            		</logic:notEqual>
            	</logic:equal>
            	<logic:equal name="element" property="string(codesetid)" value="UM">
            		<logic:equal name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/vdept.gif" border=0>
            		</logic:equal>
            		<logic:notEqual name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/dept.gif" border=0>
            		</logic:notEqual>
            	</logic:equal>
            	<logic:equal name="element" property="string(codesetid)" value="@K">
            		<logic:equal name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/vpos_l.gif" border=0>
            		</logic:equal>
            		<logic:notEqual name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/pos_l.gif" border=0>
            		</logic:notEqual>
            	</logic:equal>
            <%}else{ %>
                <logic:equal name="element" property="string(codesetid)" value="UN">
            		<logic:equal name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/b_vroot.gif" border=0>
            		</logic:equal>
            		<logic:notEqual name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/b_unit.gif" border=0>
            		</logic:notEqual>
            	</logic:equal>
            	<logic:equal name="element" property="string(codesetid)" value="UM">
            		<logic:equal name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/b_vdept.gif" border=0>
            		</logic:equal>
            		<logic:notEqual name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/b_dept.gif" border=0>
            		</logic:notEqual>
            	</logic:equal>
            	<logic:equal name="element" property="string(codesetid)" value="@K">
            		<logic:equal name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/b_vpos_1.gif" border=0>
            		</logic:equal>
            		<logic:notEqual name="element" property="string(flag)" value="vorg">
            			&nbsp;<img src="/images/b_pos_1.gif" border=0>
            		</logic:notEqual>
            	</logic:equal>
            <%} %>     
                <bean:write  name="element" property="string(codeitemdesc)" filter="true"/>&nbsp;
            </td>
             <td align="left" class="RecordRow" nowrap>     
               <input type="hidden" name="orgcodeitemid" value="<bean:write  name="element" property="string(codeitemid)" filter="true"/>">           
               <INPUT type="hidden" name="org_type" value="<bean:write name="element" property="string(flag)" filter="true"/>">
                <input type="hidden" name="orgcodeitemid<%=i %>" value="<bean:write  name="element" property="string(codeitemid)" filter="true"/>:<bean:write  name="element" property="string(codeitemdesc)" filter="true"/>:<bean:write  name="element" property="string(codesetid)" filter="true"/>">
                &nbsp;<bean:write  name="element" property="string(codeitemid)" filter="true"/>&nbsp;
            </td>
				  <%--2020-06-17 贵银 加机构编码 start --%>
			  <td align="left" class="RecordRow" nowrap>
				  &nbsp;<bean:write  name="element" property="string(corcode)" filter="true"/>&nbsp;
			  </td>
				  <%--2020-06-17 贵银 加机构编码 end --%>
            <%if(version){ %>
            	<td align="left" class="RecordRow" nowrap>   
            	<INPUT type="hidden" name="start_date" value="<bean:write name="element" property="string(start_date)" filter="true"/>"> 
            	<INPUT type="hidden" name="start_date<%=i %>" value="<bean:write name="element" property="string(start_date)" filter="true"/>">   
               &nbsp;<bean:write  name="element" property="string(start_date)" filter="true"/>&nbsp;
            	</td>
            	<td align="left" class="RecordRow" nowrap>     
               &nbsp;<bean:write  name="element" property="string(end_date)" filter="true"/>&nbsp;
            	</td>
            <%} %>
           <td align="center" class="RecordRow" nowrap>
             <hrms:priv func_id="230503">
              <a onclick='replacestr("/org/orginfo/searchorglist.do?b_updates=link&codeitemid=<bean:write  name="element" property="string(codeitemid)" filter="true"/>&codesetid=<bean:write  name="element" property="string(codesetid)" filter="true"/>&isorg=<bean:write  name="element" property="string(flag)" filter="true"/>","<bean:write  name="element" property="string(codeitemdesc)" filter="true"/>");' href="###"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	         </hrms:priv>
	   </td>
                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="middle" class="tdFontcolor">
		   <hrms:paginationtag name="orgInformationForm" pagerows="${orgInformationForm.pagerows}" property="organizationForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="orgInformationForm" property="organizationForm.pagination"
				nameId="organizationForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="100%" align="left" border=0 cellpadding="0" cellspacing="0">
  <tr>
   <td align="left"  nowrap colspan="4" height="30px" style="padding-top: 3px">
     <hrms:priv func_id="230502">  
       <logic:equal name="orgInformationForm" property="orgtype" value="vorg">
       	   <logic:empty  name="orgInformationForm" property="virtualOrgSet" >
       		<hrms:submit styleClass="mybutton"  property="b_add" disabled="true">
                  <bean:message key="button.insert"/>
		    </hrms:submit> 
	       </logic:empty>
	       <logic:notEmpty  name="orgInformationForm" property="virtualOrgSet">
	       		<hrms:submit styleClass="mybutton"  property="b_add" >
	                  <bean:message key="button.insert"/>
			   </hrms:submit> 
	       </logic:notEmpty> 
       </logic:equal>
       <logic:notEqual name="orgInformationForm" property="orgtype" value="vorg">
          <logic:equal name="orgInformationForm" property="addFuncFlag" value="0">
             <hrms:submit styleClass="mybutton"  property="b_add" disabled="true">
                  <bean:message key="button.insert"/>
		   </hrms:submit>
          </logic:equal>
          <logic:notEqual name="orgInformationForm" property="addFuncFlag" value="0">
            <hrms:submit styleClass="mybutton"  property="b_add">
                  <bean:message key="button.insert"/>
	   		</hrms:submit>
          </logic:notEqual>
       		 
       </logic:notEqual>
    </hrms:priv>   
    <hrms:priv func_id="2305012">  
	         <input type="button" name="addbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick='deleterec()' > 
	</hrms:priv>
	 <hrms:priv func_id="230504"> 
	         <logic:equal name="orgInformationForm" property="orgtype" value="vorg">
	         	<input type="button" name="combinebutton"  value="<bean:message key="button.combine"/>" class="mybutton" onclick='combineorg()' disabled>
	         </logic:equal>
	         <logic:equal name="orgInformationForm" property="query" value="1">
	         	<input type="button" name="combinebutton"  value="<bean:message key="button.combine"/>" class="mybutton" onclick='combineorg()' disabled>
	         </logic:equal>
	         <logic:notEqual name="orgInformationForm" property="orgtype" value="vorg">
	         		<logic:notEqual name="orgInformationForm" property="query" value="1">
	         			<input type="button" name="combinebutton"  value="<bean:message key="button.combine"/>" class="mybutton" onclick='combineorg()'>  
	         		</logic:notEqual>
	         </logic:notEqual>
	 </hrms:priv>
	 <hrms:priv func_id="230505"> 
	         <logic:equal name="orgInformationForm" property="orgtype" value="vorg">
	         	<input type="button" name="combinebutton"  value="<bean:message key="button.transfer"/>" class="mybutton" onclick='transfer()' disabled>
	         </logic:equal>
	         <logic:notEqual name="orgInformationForm" property="orgtype" value="vorg">
	         	<input type="button" name="combinebutton"  value="<bean:message key="button.transfer"/>" class="mybutton" onclick='transfer()'>  
	         </logic:notEqual>
	 </hrms:priv>
	 <hrms:priv func_id="230506"> 
	         <logic:equal name="orgInformationForm" property="orgtype" value="vorg">
	         	<input type="button" name="addbutton"  value="<bean:message key="button.abolish"/>" class="mybutton" onclick='bolish()' disabled>  
	         </logic:equal>
	         <logic:notEqual name="orgInformationForm" property="orgtype" value="vorg">
	         	<input type="button" name="addbutton"  value="<bean:message key="button.abolish"/>" class="mybutton" onclick='bolish()'>  
	         </logic:notEqual>
	  </hrms:priv> 
           <!-- <hrms:submit styleClass="mybutton"  property="b_previous">
                  <bean:message key="button.previous"/>
	   </hrms:submit> 
	    <hrms:submit styleClass="mybutton"  property="b_movenext">
                  <bean:message key="button.next"/>
	   </hrms:submit> -->
	 <hrms:priv func_id="230507"> 
	         	<hrms:submit styleClass="mybutton"  property="b_move">
                  <bean:message key="button.movenextpre"/>
	   		</hrms:submit> 
	 </hrms:priv>
	 <hrms:priv func_id="230508"> 
	   <hrms:submit styleClass="mybutton"  property="br_org_pigeonhole">
                  <bean:message key="button.org_pigeonhole"/>
	   </hrms:submit> 
	 </hrms:priv>
	 <hrms:priv func_id="230509"> 
	   <html:button styleClass="mybutton" property="b_cancel" onclick="getSelectedOrg();">
            		<bean:message key="button.output.data"/>
	   </html:button>
	 </hrms:priv>
	 <hrms:priv func_id="2305010"> 
	   <html:button styleClass="mybutton" property="b_cancel" onclick="getInputOrg();">
            		 <bean:message key="button.input.data"/>  		
	   </html:button>
	 </hrms:priv>
	 <hrms:priv func_id="2305011"> 
	   <hrms:submit styleClass="mybutton"  property="b_layer">
                  <bean:message key="org.orginfo.resetorg"/>
	   </hrms:submit>
	 </hrms:priv>
	   <hrms:tipwizardbutton flag="org" target="il_body" formname="orgInformationForm"/>  
        </td>	  
      </tr>
</table>
</div>
</html:form>
<script language="javascript">
var value_s="";
var newid_s="";
var codelength = "";
var index = "";
<%
	OrgInformationForm oif = (OrgInformationForm)session.getAttribute("orgInformationForm");
	ArrayList codelist = oif.getCodelist();
	ArrayList newidlist = oif.getNewidlist();
	if(codelist!=null&&codelist.size()>0){
	%>
		codelength = "<%=codelist.size()-1%>";
		<%
			for(int y=0;y<codelist.size();y++){
		%>
				value_s="<%=codelist.get(y)%>";
				index = "<%=y%>";
		<%
				if(newidlist!=null&&newidlist.size()>0){
		%>
					newid_s = "<%=newidlist.get(y)%>";
				<%}%>
				deleter('<bean:write name="orgInformationForm" property="isrefresh" filter="true"/>',value_s,'<bean:write name="orgInformationForm" property="transfercodeitemidall" filter="true"/>','<bean:write name="orgInformationForm" property="issuperuser" filter="true"/>','<bean:write name="orgInformationForm" property="manageprive" filter="true"/>',newid_s,'<bean:write name="orgInformationForm" property="firstNodeCode" filter="true"/>','<bean:write name="orgInformationForm" property="combinetext" filter="true"/>',codelength,index,'<bean:write name="orgInformationForm" property="isnewcombineorg" filter="true"/>');
		<%  
		}
		
		if(codelist!=null)
			codelist.clear();
		if(newidlist!=null)
			newidlist.clear();
		}
	%>
	save('<bean:write name="orgInformationForm" property="isrefresh" filter="true"/>','<bean:write name="orgInformationForm" property="code" filter="true"/>','<bean:write name="orgInformationForm" property="codesetid" filter="true"/>','<bean:write name="orgInformationForm" property="codeitemid" filter="true"/>','<bean:write name="orgInformationForm" property="codeitemdesc" filter="true"/>','<bean:write name="orgInformationForm" property="issuperuser" filter="true"/>','<bean:write name="orgInformationForm" property="manageprive" filter="true"/>');
	update('<bean:write name="orgInformationForm" property="isrefresh" filter="true"/>','<bean:write name="orgInformationForm" property="codesetid" filter="true"/>','<bean:write name="orgInformationForm" property="codeitemid" filter="true"/>','<bean:write name="orgInformationForm" property="codeitemdesc" filter="true"/>');
	savemove('<bean:write name="orgInformationForm" property="isrefresh" filter="true"/>');
	if('<bean:write name="orgInformationForm" property="isrefresh" filter="true"/>' == "reload"){
	    treeReload('<bean:write name="orgInformationForm" property="code" filter="true"/>','<bean:write name="orgInformationForm" property="codesetid" filter="true"/>');
	}
		
</script>
<div id='wait' style='position:absolute;top:250;left:80;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					正在刷新数据字典，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
<script>
	//兼容非ie浏览器 样式修改  wangbs 20190315
	if(!getBrowseVersion()){
        var idordescInput = document.getElementById("idordesc");
        idordescInput.style.marginTop = "-4px";
	}else{//兼容ie浏览器 样式修改  xuanz 20190929
		var idordescInput = document.getElementById("idordesc");
        idordescInput.style.marginTop = "-3px";
	}
</script>