<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String showDb="0";
	if(request.getParameter("showDb")!=null)
		showDb=request.getParameter("showDb");
		
	String showSelfNode="0";
	if(request.getParameter("showSelfNode")!=null)
		showSelfNode=request.getParameter("showSelfNode");
		String level ="-1";
			if(request.getParameter("level")!=null)
		level=request.getParameter("level");	//该参数可以控制单选按钮，比如所属单位 
		String viewunit = "";
			if(request.getParameter("viewunit")!=null&&("0".equals(request.getParameter("viewunit"))||"1".equals(request.getParameter("viewunit"))))
		viewunit=request.getParameter("viewunit");	//该参数可以控制按操作单位或管理范围进行展现，1为操作单位，0为管理范围 

    String dbpre="usr";
     if(request.getParameter("dbpre")!=null)
    {
        dbpre=request.getParameter("dbpre");
    }
    String priv="1";
    if(request.getParameter("priv")!=null)
    {
        priv=request.getParameter("priv");
    }
    
    String tabid="";
    if(request.getParameter("tabid")!=null)  //人事异动模板id
    {
        tabid=request.getParameter("tabid");
    }
    
    String isfilter="0";
    if(request.getParameter("isfilter")!=null)
    {
        isfilter=request.getParameter("isfilter");
    }
    String nmodule = "";
    if(request.getParameter("nmodule")!=null)
    {
    	nmodule=request.getParameter("nmodule");
    }
    
    String dbtype="0";
     if(request.getParameter("dbtype")!=null)
    {
        dbtype=request.getParameter("dbtype");
    }

     String dbvalue="Usr";
     if(request.getParameter("dbvalue") != null){
    	 dbvalue = request.getParameter("dbvalue");
     }
     
     String planid="";
     if(request.getParameter("planid") != null){
    	 planid = request.getParameter("planid");
     }
     String opt="";
     if(request.getParameter("opt") != null){
    	 opt = request.getParameter("opt");
     }
     String object_id="";
     if(request.getParameter("object_id") != null){
    	 object_id = request.getParameter("object_id");
     }
     String callBackFunc = "";
     if(request.getParameter("callBackfunc")!=null){
         callBackFunc = request.getParameter("callBackfunc");
     }
     
     String defaultradiolevel = request.getParameter("defaultradiolevel");
     defaultradiolevel = StringUtils.isEmpty(defaultradiolevel) ? "0" : defaultradiolevel;
%>
<html>
<head>
<title>Insert title here</title>
</head>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<style>
div#treemenu {
 overflow:auto; 
width: 250px;
height: 304px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 
 border: inset 1px #C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid; 
}

</style>
<hrms:themes></hrms:themes>
<script language='javascript' >
var info=[];
if(window.showModalDialog){// [0]: tablename  [1]:nbase
	info=window.dialogArguments||parent.parent.dialogArguments||parent.window.opener.dialogArguments;
} else {
	info.push('<%=planid%>');
	info.push('<%=opt%>');
	info.push('<%=object_id%>');
}

<%
    if(request.getParameter("callbackFunc")!=null){
	    callBackFunc = request.getParameter("callbackFunc");
	}
%>
</script>
<script type="text/javascript"><!--
 
 function selectupdown(e){
	var keycode;
    if(navigator.appName == "Microsoft Internet Explorer"){
    	keycode = window.event.keyCode;
        
    }else{
        keycode = e.which;  
    }
    e = window.event || e;
    if(keycode==13){
        var objid,i;
           var obj=$('contenttype');           
           for(i=0;i<obj.options.length;i++)
           {
              if(obj.options[i].selected)
                objid=obj.options[i].value;
           }       
           if(objid)
           {
               if(objid.length>3){
            	   okSelect();  
               }else{
                    //在人员库回车键不做任何操作
                   if(e.returnValue){
                       e.returnValue = false ;
                   }
                   if(e.preventDefault ){
                       e.preventDefault();
                   }
                   return false;
                }
           }
    }
    
  } 

	
	 //添加人员
	function okSelect()
	{
		var obj=document.getElementById("date_box");
        for(var i=0;i<obj.options.length;i++)
	    {
	        if(obj.options[i].selected)
	        {
		          var desc = obj.options[i].value;
		          if(typeof(desc)=="undefined" || desc == null || desc.length < 1){
		        	  continue;
				  }
				 var descvalue='0';
				 var descpre='Usr';
				 if(desc.length > 3){
				     descvalue=desc.substring(3,desc.length);
				     descpre=desc.substring(0,3);
				 }
				 
	             var temp=obj.options[i].text;
	             var tit;
	             if(temp.indexOf("/")!=-1)
	             {
                     //tit=temp.split("/")[1];// 不能无条件取下标为1的节点，有可能为“产品研发部/研发二部/陈”，那样就取到了“研发二部”。chent 20160701 delete
	             	 var tempArray = temp.split("/");
                     tit=temp.split("/")[tempArray.length-1];//取姓名（最后一组值） chent 20160701 add
	             }else{
	                 tit=temp;
	             }
                var isExist=0;
		    	for(var j=0;j<document.handSelForm.right_fields.options.length;j++)
		    	{
		    		if(document.handSelForm.right_fields.options[j].value==descvalue)
		    		{	isExist=1;
		    			break;
		    		}
		    	}
		    	if(isExist==1)
		    		continue;
		        var no = new Option();	
		        no.value=descvalue;
		    	//添加当前所选人应用库名，如Usr,Oth,Ret
		    	no.dbName=descpre;
		    	no.text=tit;
		    	document.handSelForm.right_fields.options[document.handSelForm.right_fields.options.length]=no;
		     }
		 }
	}


 var optionNum=0;
  function updown(e){
     e = window.event||e;
	var keycode;
    if(navigator.appName == "Microsoft Internet Explorer"){
         keycode = e.keyCode;
    }else{
         keycode = e.which;  
    }
    
    var elem2 = document.getElementsByName("contenttype");
    var optionLength = elem2[0].childNodes.length;
    
    if(optionLength>0){
	    switch (keycode) {
	    case 38://up键
        	optionNum--;
            if(optionNum<0)
            	optionNum=optionLength-1;
            elem2[0].value = elem2[0].childNodes[optionNum].value;
	        break;
	
	    case 40://down键
        	optionNum++;
            if(optionNum>=optionLength) 
            	optionNum=0;
            elem2[0].value = elem2[0].childNodes[optionNum].value;
	        break;

	    case 13://回车键
            if (event.preventDefault){
                event.preventDefault();
            }
            else {
                event.returnValue = false;
            }
            break;
	    }
    }
} 

	function showDateSelectBox(e,srcobj)
   {
       e = window.event || e;
		var keycode;
		var temp = document.getElementsByName("contenttype");
		if(navigator.appName == "Microsoft Internet Explorer"){
	        keycode = event.keyCode;  

	    }else{
	        keycode = e.which;  
	    }	
		if(keycode==38||keycode==40||keycode==13){ //当上下回车的时候不进交易类
			return;
		}
		optionNum = -1;
		if( temp[0].options[0]){
	        temp[0].value = temp[0].options[0].value;  
	    }
		
   		if($F('objName')=="")
   		{
   			Element.hide('date_panel');
   			return false ;
   		}
      date_desc=document.getElementById(srcobj);
      Element.show('date_panel');
      var pos=getAbsPosition(date_desc);
	  with($('date_panel'))
	  {
        style.position="absolute";
		style.left=pos[0]+"px";
		style.top=(pos[1]-date_desc.offsetHeight+42)+"px";
		style.width=((date_desc.offsetWidth<20)?150:date_desc.offsetWidth+1)+"px";
      }
      var hashVo = new ParameterSet();
      hashVo.setValue("name",getEncodeStr(document.handSelForm.objName.value));
	  hashVo.setValue("dbpre","<%=dbpre%>");
	  hashVo.setValue("showDb","<%=showDb%>");
	  hashVo.setValue("priv","<%=priv%>");
      hashVo.setValue("showSelfNode","<%=showSelfNode%>");
	  hashVo.setValue("dbtype","<%=dbtype%>");
	  hashVo.setValue("viewunit","<%=viewunit%>");
	  hashVo.setValue("nmodule","<%=nmodule%>");
	  hashVo.setValue("isfilter","<%=isfilter%>");
	  hashVo.setValue("tabid","<%=tabid%>");
	  hashVo.setValue("SYS_FILTER_FACTOR","<%=""%>");
	  hashVo.setValue("flag","4");
	  hashVo.setValue("dbvalue","<%=dbvalue%>");
      var request=new Request({method:'post',onSuccess:shownamelist,functionId:'3020071012'},hashVo);
   }
   function shownamelist(outparamters)
   {
   		var namelist=outparamters.getValue("namelist");
   		var str_value=outparamters.getValue("str_value");
		if(namelist.length==0){
			Element.hide('date_panel');
		}
		else{
			AjaxBind.bind(handSelForm.contenttype,namelist);
			if(typeof(str_value)!="undefined"&&str_value.length>0)
			{ 
				var objs=str_value.split("`");
				var _objs= new Array();
				for(var i=0;i<objs.length;i++)
				{
					var temp=objs[i].split("~");
					_objs["_"+temp[0]]=temp[1];
				}
				
				var obj=document.getElementsByName("contenttype");
				for(var i=0;i<obj[0].options.length;i++)
	  			{
	  				var _value=obj[0].options[i].value;
	  				var desc=_objs["_"+_value];
	  				if(typeof(desc)!="undefined")
	  				{
	  					obj[0].options[i].title=desc;
	  				}else{
	  					obj[0].options[i].disabled=true;
			  		}
	  			}
			}
		}
   }
--></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<body>
<html:form action="/performance/handSel">
	<html:hidden name="handSelForm" property="aplanid"/>
<table width='600px;' style="margin-left:-6px;">
<tr>
	<td></td>
	<td>
	<fieldset align="center" style="width:255px">
    		<legend ><bean:message key="lable.performance.preparePerMainBody"/></legend>    		
    		<table width='100%'  >
    		<logic:notEqual name="handSelForm" property="opt" value="1">
    			<logic:equal name="handSelForm" property="object_type" value="2">
    				<tr><td>
    					 <bean:message key="columns.archive.name"/>&nbsp;
    					 <input type='text' name='objName'  size='20' id="objName" onkeyup="showDateSelectBox(event,'objName')" onkeydown="updown(event);" class="text4">
    		 			</td>
    		 			<td width='30%'>
    		 			 <span>
    		 			 <a href='javascript:query()' > <img  src="/images/code.gif"  border=0 style="position:relative;top:2px;"/></a>
    		 			 </span>
    				</td></tr>    		
    		   </logic:equal>	
    	   </logic:notEqual>
    	   	<logic:equal name="handSelForm" property="opt" value="1">
    	   		<tr><td>
    					 <bean:message key="columns.archive.name"/>:&nbsp;<html:text name="handSelForm" property="objName" size='20' styleClass="text4"/>
    		 			</td>
    		 			<td width='30%'>
    		 			<span>
    		 			 <a href='javascript:query()' > <img  src="/images/code.gif"  border=0 style="position:relative;top:2px;"/></a>
    		 		    </span>
    				</td></tr>    		
    	    </logic:equal>	
    		<tr>    		
    			<td colspan='2' align='left' >
		     		<div id="treemenu"></div> 
		     	</td>
		     </tr>		     
		     
		     </table>
		                  
	</fieldset>
	</td>
	<td align="center">
	
	 
	            
	            <input type='button' class='mybutton' value="<bean:message key="button.setfield.addfield"/>"  onclick="addMen();" style="margin-bottom: 30px;"/>
	            
	            <input type='button' class='mybutton' value="<bean:message key="button.setfield.delfield"/>"  onclick="delMen();" style="margin-top:5px;"/>
	       
	</td>
	<td>
	<fieldset align="center" style="width:255px">
    		<legend ><bean:message key="lable.performance.selectedMenPerMainBody"/></legend>
			<table><tr><td>
			
 
            
            <logic:notEqual name="handSelForm" property="opt" value="1">
    			<logic:equal name="handSelForm" property="object_type" value="2">
    			   <select name="right_fields"  multiple="multiple" size="10"   style="height:330px;width:250px;font-size:9pt">   
            		</select>  
    		   </logic:equal>	
    		   <logic:notEqual name="handSelForm" property="object_type" value="2">
    		      		<select name="right_fields"  multiple="multiple" size="10"   style="height:300px;width:250px;font-size:9pt">   
            			</select> 
    		   </logic:notEqual>
    	   </logic:notEqual>
    	   	<logic:equal name="handSelForm" property="opt" value="1">
    	   	   <select name="right_fields"  multiple="multiple" size="10"   style="height:330px;width:250px;font-size:9pt">   
            		</select>  
    	    </logic:equal>	
            
            
                       
            </td></tr></table>
		    
		                 
	</fieldset>
	</td>

</tr>
<%
if(request.getParameter("busitype")!=null && request.getParameter("busitype").trim().length()>0 && request.getParameter("busitype").equals("1"))
{
	if((request.getParameter("opt").equals("1") && userView.hasTheFunction("3603021511")) || (request.getParameter("opt").equals("9") && userView.hasTheFunction("3603021511"))) {%>
	<tr>
		<td colspan='4' align=left >	            
		   <input type="checkbox" id='accordPrivBox' checked onclick='accordPriv(this.checked)' style="margin-left:5px;"/> <bean:message key='jx.implement.accordPriv'/>    
		</td>
	</tr>
<%}
}else{ 
	if((request.getParameter("opt").equals("1") && userView.hasTheFunction("3260301160101")) || (request.getParameter("opt").equals("9") && userView.hasTheFunction("3260607090101"))) {%>
	<tr>
		<td colspan='4' align=left >	            
		   <input type="checkbox" id='accordPrivBox' checked onclick='accordPriv(this.checked)' style="margin-left:5px;"/> <bean:message key='jx.implement.accordPriv'/>    
		</td>
	</tr>
	<%} else if(request.getParameter("opt").equals("12")&& (!userView.isSuper_admin())&& (1==2)) {//暂时屏蔽 wangrd%>
	<hrms:priv func_id="3300103001,3310103001,270153001,0C3483001,3203001,3212601,3702601,3712601,3722601,3722601,32401013001,32501013001,01073101,230672501,2311022501">  
		<tr>
			<td colspan='4' align=left >	            
			   <input type="checkbox" id='accordPrivBox' checked onclick='accordPriv(this.checked)' style="margin-left:5px;"/> <bean:message key='jx.implement.accordPriv'/>    
			</td>
		</tr>
	</hrms:priv>
	<%}else if("1".equals(request.getParameter("flag0"))&& userView.hasTheFunction("9A51070201")){ %>
		<tr>
			<td colspan='4' align=left >	            
			   <input type="checkbox" id='accordPrivBox' checked onclick='accordPriv(this.checked)' style="margin-left:5px;"/> <bean:message key='jx.implement.accordPriv'/>    
			</td>
		</tr>
<%} }%>
<tr>
	<td colspan='4' align="center" >             
	      	<input type='button' class='mybutton' value="<bean:message key='button.ok'/>" onclick="sub();" />    
	        <input type='button' class='mybutton' value="<bean:message key='button.cancel'/>" onclick="closeWin();" />            
	</td>
</tr>

</table>
    <div id="date_panel" style="display:none;">
        <select id="date_box" name="contenttype" onkeydown="selectupdown(event);" onblur="Element.hide('date_panel');"  multiple="multiple"  style="width:254" size="10" ondblclick="okSelect();">
        </select>
	</div>
</html:form>
</body>
<script language='javascript' >	
	//flag:-1 从人员库开始显示 0从顶层机构开始显示
	var m_sXMLFile;
	<% 
	 
	String approvalRelation = request.getParameter("approvalRelation");
	String flag0= request.getParameter("flag0");
	if(null!=approvalRelation&&approvalRelation.equals("1")){
	%>
		m_sXMLFile	="/system/load_tree?dbpre=null&isfilter=0&target=null&flag=1&dbtype=0&priv=1&loadtype=1&first=1&lv=0&viewunit=&nextlevel=0&chitemid=&orgcode=&umlayer=0&privtype=&nmodule=&cascadingctrl=0&params=codeitemid%3Dparentid&id=UN&showDbName=1&showDb=1&cascadingctrl=0&isAddAction=true&approvalRelationDefine=1";
	<%
	}else if(flag0!=null&&flag0.equals("1")){//审批关系 loadtype 传0 不然部门下也会出现人 20160528 bug18491
	%>
		m_sXMLFile	="/system/load_tree?dbpre=null&isfilter=0&target=null&flag=1&dbtype=0&priv=1&loadtype=0&first=1&lv=0&viewunit=&nextlevel=0&chitemid=&orgcode=&umlayer=0&privtype=&nmodule=&cascadingctrl=0&params=codeitemid%3Dparentid&id=UN&showDbName=1&showDb=1&cascadingctrl=0&isAddAction=true&approvalRelationDefine=1";
	<%
	}else if(flag0!=null&&flag0.equals("2")){//人事异动 自定义审批流程 选人 wangrd 2012-12-19 
		%>
			m_sXMLFile	="/performance/kh_plan/handImportObjs.jsp?flag=-1&id=0&planid=&opt=12";
		<%
	}
	
	else
	{
	%>
	 
	m_sXMLFile	='/performance/kh_plan/handImportObjs.jsp?flag=0&id=0&nbase=Usr&planid='+info[0]+"&opt="+info[1];
	 
	<% } %>
	//var m_sXMLFile	=/performance/kh_plan/handImportObjs.jsp?flag=0&id=0&nbase=Usr,&planid='+info[0]+"&opt="+info[1]	
   //var m_sXMLFile	= "/performance/kh_plan/handImportObjs.jsp?flag=0&idzz=0&nbase=Usr,&planid="+info[0]+"&opt="+info[1];	
    if(info[1]==5 || info[1]==8)
   	 	m_sXMLFile+="&khObjCopyed="+info[2];
	if(info[1]==2)
   	 	m_sXMLFile+="&oldPlan_id="+info[2];
	/* 【6300】工具箱：审批关系，指定审批主体时，手工选人窗口，左侧机构树都带有超链接
	 * servlet中是从session中取的action，此处传入参数，用于判断是否清空session中的action
	 * jingq add 2014.12.25
	 */
   	<% if("9".equals(opt)&&"1".equals(flag0)&&approvalRelation==null){%>
   		m_sXMLFile+="&opt=9";
   	<%}%>
	var newwindow;
	var root=new xtreeItem("root","组织机构","","mil_body","组织机构","/images/root.gif",m_sXMLFile);
	Global.defaultInput=1;
	Global.showroot=false;
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	function accordPriv(theChecked)
	{		 
		document.getElementById("treemenu").innerHTML='';	
		Global={
			id:0,
			getId:function(){return this.id++;},
			all:[],
			selectedItem:null,
			defaultText:"",
			defaultAction:"javascript:void(0)",
			defaultTarget:"_blank",
			closeAction:"",         //关闭事件
			checkvalue:",",      //选中的值,可以根据选中的值，置checkbox radio的状态
			defaultInput:0,      //是否为0无,1　checkbox 2=radio default 无	
			showroot:true,     //根节点是否显示 checkbox,radio
			drag:false,
			showorg:0,//   =1为组织机构树,=0为其它树
			defaultchecklevel:0,   //showorg=1时  =all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现
			/**check 级联选中*/
			cascade:false,	
			defaultradiolevel:<%=defaultradiolevel%>, //0=all出现单选框,1=部门以下都出现,2=职位以下,3=只有人员出现
			setDrag:function(bool){this.drag=bool;},
			isIE:navigator.appName.indexOf("Microsoft")!= -1	
		}
		Global.defaultInput=1;
		Global.showroot=false;
		if (info[1]==12){	
		 // m_sXMLFile	="/performance/kh_plan/handImportObjs.jsp?flag=-1&id=0&planid=&opt=12";	
		  m_sXMLFile= "/performance/kh_plan/handImportObjs.jsp?flag=-1&id=0&planid=&opt=12";	
		}
		else {
			
		<%	 if(flag0!=null&&flag0.equals("1")){ %>
		  m_sXMLFile= "/performance/kh_plan/handImportObjs.jsp?flag=-1&id=0&planid=&opt=13";
		<%   } else { %>
		
		  m_sXMLFile= "/performance/kh_plan/handImportObjs.jsp?flag=0&id=0&nbase=Usr,&planid="+info[0]+"&opt="+info[1];	
		<%   } %>
		}
    	if(info[1]==1 || info[1]==9 )
   	 		m_sXMLFile+="&accordPriv="+theChecked;
   	 	else if ( info[1]==12)
   	 		m_sXMLFile+="&accordPriv=false";
   	 	root.allClear();	
		root=new xtreeItem("root","组织结构","","mil_body","组织结构","/images/add_all.gif",m_sXMLFile);		
		root.setup(document.getElementById("treemenu"));	
	}
	function closeWin(){
		if(window.showModalDialog){
			parent.window.close();
		}else{
		    if (parent.parent.selectWinClose){
                parent.parent.selectWinClose();
            }else{
                parent.window.close();
            }
		}
	}
	function sub()
	{
		var objlist=new Array(); 		        
		if(document.handSelForm.right_fields.options.length==0)
		{
			alert(KH_NOTSELECT_NULL);
			return;
		}
		 vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		  	//var temp = right_vo.options[i].value.split('`');
		  	//objlist.push(temp[0]);
		  	//传送人员编号和人员库名形式如：0000001`Oth
		  	//判断数据是否是在职人员，如果不是一定是审批关系 定义按钮界面的出发请求，如果是则走原来的流程
		  	/*if("Usr"==right_vo.options[i].dbName){
		  		objlist.push(right_vo.options[i].value);	
		  	}else{
		  		objlist.push(right_vo.options[i].value+'`'+right_vo.options[i].dbName);
		  		}
		  	*/
		  	
		  	<%if(flag0!=null&&flag0.equals("1")){%>
		  		objlist.push(right_vo.options[i].dbName+right_vo.options[i].value);
	 	    <%} else if(flag0!=null&&flag0.equals("2")){%>
		  		objlist.push(right_vo.options[i].dbName+right_vo.options[i].value);	 
		  	<%}else{%>
			  	if("Usr"==right_vo.options[i].dbName){
			  		objlist.push(right_vo.options[i].value);	
			  	}else{
			  		objlist.push(right_vo.options[i].value+'`'+right_vo.options[i].dbName);
			  		}
		  	
		  	<%}%>
		
		  	
		  }
		  if(window.showModalDialog){
			  parent.window.returnValue=objlist;
		  }else{
		      if (parent.parent.select_ok){
                  parent.parent.select_ok(objlist);
              }else{
            	  <%
            	       if(callBackFunc != null && callBackFunc.length() > 0) {//bug 46472 组织机构无法显示
            	   %>
                    if (parent.parent.<%=callBackFunc%>) {
                        eval(parent.parent.<%=callBackFunc%>)(objlist);
                    } else if (parent.window.opener.<%=callBackFunc%>) {
                        eval(parent.window.opener.<%=callBackFunc%>)(objlist);
                    }
                  <%}%>
                }

              }
		  closeWin();
	}

	
	//删除人员
	function delMen()
	{
		  vos= document.getElementsByName("right_fields");
		  if(vos==null)
		  	return false;
		  right_vo=vos[0];
		  for(i=right_vo.options.length-1;i>=0;i--)
		  {
		    if(right_vo.options[i].selected)
		    {
		    	//alert(i);
				right_vo.options.remove(i);
		    }
		  }
	}
	
    //添加人员
	function addMen()
	{
		if(root.getSelected()=="")
		{
				alert(KH_NOTSELECT_NULL);
				return;
		}
			
		var temp_str=root.getSelected();
		temp_str=getDecodeStr(temp_str);
		var temps=temp_str.split(",");
		if (info[1]==12 || "3" == '<%=defaultradiolevel%>'){//==12 是自定义审批流程选择人员调用。 update hej 20180305
			for(var i=0;i<temps.length;i++)
			{
				if(temps[i].length>0&&temps[i].split("`")[temps[i].split("`").length-1]!='p')
				{
					alert("只能选择人员信息!");
					return;
				}
			}
		}
		for(i=0;i<temps.length;i++)
		{
		    if(temps[i].length>0)
		    {
		    	var isExist=0;
		    	for(var j=0;j<document.handSelForm.right_fields.options.length;j++)
		    	{
		    		if(document.handSelForm.right_fields.options[j].value==temps[i].split("`")[0])
		    		{	isExist=1;
		    			break;
		    		}
		    	}
		    	if(isExist==1)
		    		continue;
		        var no = new Option();	
		    	no.value=temps[i].split("`")[0];
		    	//添加当前所选人应用库名，如Usr,Oth,Ret
		    	no.dbName=temps[i].split("`")[1];
		    	no.text=temps[i].split("`")[2];
		    	document.handSelForm.right_fields.options[document.handSelForm.right_fields.options.length]=no;
		    }
		}
	}
	
	
	/** 查询 */
	function query()
	{
		if(document.handSelForm.objName.value.length==0)
		{
			alert("请输入姓名信息!");
			return;
		}
		 var hashVo=new ParameterSet();
		 hashVo.setValue("name",getEncodeStr(document.handSelForm.objName.value));
		 hashVo.setValue("dbname",'Usr');
		 hashVo.setValue("planid",info[0]);
		 hashVo.setValue("opt",info[1]);
		 if(info[1]==5 || info[1]==8)
			hashVo.setValue("khObjCopyed",info[2]);
		 if(info[1]==2)
			hashVo.setValue("oldPlan_id",info[2]);			
		 else if(info[1]==1 || info[1]==9 )
		 {
		 	 if(document.getElementById('accordPrivBox')!=null)
			 	hashVo.setValue("accordPriv",document.getElementById('accordPrivBox').checked+'');	
			 else
			 	hashVo.setValue("accordPriv",'false');	
		 }   	
		 else if( info[1]==12){//默认为未选中
		   hashVo.setValue("accordPriv",'false');
		 }		 
			 
		 var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'9024000028'},hashVo);			
	}
	
	
	function return_ok(outparameters)
	{
		var orgLinks = outparameters.getValue("orgLinks");
		if(orgLinks.length==0)
		{
			alert("待选对象中没有找到"+document.handSelForm.objName.value+"!");	
		}
		else
		{
			var findNode = false;
			var maxcount=orgLinks.length;
			if(maxcount>40)
				maxcount=40;
			root.collapseAll();
			for(var k=0;k<maxcount;k++)
			{
				var orgLink = orgLinks[k];
				var temps=orgLink.split("/");
				var obj=root;
				for(var i=temps.length;i>=0;i--)
				{
					obj.expand();
					for(var j=0;j<obj.childNodes.length;j++)
					{
						if(obj.childNodes[j].text==temps[i])
						{
							obj=obj.childNodes[j];
							findNode = true;
							break;
						}
					}
				}
				obj.expand();				
			}
			if(findNode == false)	
				alert("待选对象中没有找到"+document.handSelForm.objName.value+"!");	
		}
	}
	
	
	function goback()
	{
        closeWin();
	}
</script>


</html>