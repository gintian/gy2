<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
  com.hjsj.hrms.actionform.sys.PrivForm form=(com.hjsj.hrms.actionform.sys.PrivForm)session.getAttribute("privForm");
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);  
  String codeid="@K";//userView.getManagePrivCode();
  String codevalue=userView.getManagePrivCodeValue();
  if(userView.isSuper_admin()) 
  {
     codevalue="ALL";  
     codeid="@K";
  }
%>
<%
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<style>
	.table tr  td{ border:1px #ff000 solid}
</style>
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
		<link rel="stylesheet"
			href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css"
			rel="stylesheet" type="text/css" />
		<link href="/module/recruitment/css/style.css" rel="stylesheet"
			type="text/css" />
		<script language="JavaScript"
			src="/components/tableFactory/tableFactory.js"></script>
<script language="JavaScript">
	//this.status ="招聘管理 / 应聘人才库";
       var Panel = "";
	function setCurrent(tab)
	{
		var nodes,currnode;
		currnode=document.getElementById("current");
		if(currnode==null)
		   return;
		currnode.id="";
		nodes=tab.parent;
		if(nodes==null)
		   return;
		nodes.id="current";
		
	}

      
      /* 
      *树形菜单控制,功能菜单
      */
     function show(div_id)
      {
      	var oDiv;
      	oDiv=document.getElementById(div_id);
      	if(oDiv==null)
      	  return;
      	for(var i=0;i<oDiv.childNodes.length;i++)
      	{
      		if(oDiv.childNodes[i].tagName=='DIV')
      		{
      		   if(oDiv.childNodes[i].style.display=="none")
      		     oDiv.childNodes[i].style.display="block";
      		   else
      		     oDiv.childNodes[i].style.display="none";	  
      		}
      	}		
      }
       /**function getUrlParam(param) {
		 var params = Ext.urlDecode(location.search.substring(1));
		 return param ? params[param] : params;
		} */
		function loadTitle(){
			var globalname = '${baseOptionsReForm.current_tab}';
			if(globalname=="dbpriv"){
				document.getElementById('titleId').innerHTML="请设置招聘人员库";
			}
			if(globalname=="tablepriv"){
				document.getElementById('titleId').innerHTML ='请设置应聘简历子集';
			}
			if(globalname=="fieldpriv"){
				document.getElementById('titleId').innerHTML="请设置应聘简历指标";
			}
		}
		 
      /**
      *组装表权限或字段权限串，最后通过document.baseOptionsReForm.field_set_str
      *隐藏域传到后台．
      */
      function combinePrivString()
      {
      	var tablevos,thecontent,tmp,tablename,tabname,displayName,tableName;
      	thecontent="";
      	tabname=document.baseOptionsReForm.current_tab.value;
      	
      	if(tabname=="tablepriv")
      	{
      		tablevos = document.getElementsByTagName("input");
      		for ( var i = 0; i < tablevos.length; i++) {
      			tablename = document.getElementsByName("func"+i);
      			var displayObj = document.getElementById("displayName"+i);
      			if(displayObj != null && typeof(displayObj) != "undefined")
      				displayName = displayObj.value;
      				
      			var tdOcj = document.getElementById("tableName"+i);
      			if(tdOcj != null && typeof(tdOcj) != "undefined")
      				tableName = tdOcj.innerHTML;
  				
				if(tablename.length>0){
					tabname = "";
					for ( var j = 0; j < tablename.length; j++) {
						if(tablename[j].type=="checkbox"){
							var value = tablename[j].value;
							tmp = value.substring(0,3);//A01
							if(displayName != tableName)
								tmp += "#" + displayName;
							
							var str = value.substring(3,value.length-1);//01
							var index = value.substring(value.length-1);//1
							if(tabname.indexOf(tmp)==-1){//第一个checkbox
								tabname = tmp+"[";
								if(tablename[j].checked)
									tabname += str+"#1";
								else
									tabname += "-1#1#";
							} else {
								if(tabname.indexOf("-1#1#")!=-1){//第二个checkbox且第一个未选中
									if(tablename[j].checked)
										tabname = tmp+"["+str+"#2`";
									else
										tabname = tmp+"[-1#1`";
								} else {
									if(tabname.indexOf(str+"#")!=-1){
										if(tabname.indexOf("`")!=-1){//第四个checkbox且第三个已选中
											if(tablename[j].checked)
												tabname = tabname.substring(0,tabname.indexOf("`")+1)+str+"#2";
										} else {//第二个checkbox且第一个已选中
											if(tablename[j].checked)
												tabname = tmp+"["+str+"#2`";
											else
												tabname += "`";
										}
									} else {
										if(tabname.indexOf("`")==tabname.length-1){//第三个checkbox
											if(tablename[j].checked)
												tabname += str+"#1";
											else
												tabname += "-1#1";
										} else {//第四个checkbox且第三个未选中
											if(tablename[j].checked)
												tabname = tabname.substring(0,tabname.indexOf("`")+1)+str+"#2";
										}
									}
								}
							}
						}
					}
					tabname += "]";
					thecontent += ","+tabname;
				} else {
					break;
				}
			}
      		thecontent=","+thecontent;
      		var fieldSetContent=thecontent.replace(/\,/g,"");///子集为空 会造成数组下标越界
      		if(fieldSetContent.length==0){
      			Ext.Msg.alert("提示信息","请选择子集！");
      			return;
      		}
      		document.baseOptionsReForm.field_set_str.value=thecontent; 
      	}
      	else if(tabname=="fieldpriv")
      	{
      		var constent="";
      		var constent_show="";
      		var constent_must="";
      		var constent_only="";
          	var display_name="";
      		var field_vo=eval("document.baseOptionsReForm.func");
      		var show_field_vo=eval("document.baseOptionsReForm.func_show");
      		if(field_vo!=null)
      		{
	      		var must_field_vo=eval("document.baseOptionsReForm.func_must");
	      		var only_field_vo=eval("document.baseOptionsReForm.func_onlys");
	      		var itemDisplayName = eval("document.baseOptionsReForm.itemDisplayName");
	      		var itemName = document.getElementsByName("itemName");
	      		for(var i=0;i<field_vo.length;i++) {
      				var checked = false;
      				//校验是否是主集
		      		if(Ext.String.startsWith(field_vo[i].value,"A01",true))
			      		checked = true;
		      		//必填指标或前台子集列表显示指标校验是否是有效指标
	      			if((must_field_vo[i].checked || (show_field_vo[i] && show_field_vo[i].checked)) && !field_vo[i].checked) {
	      				Ext.Msg.alert("提示信息",MUST_AND_VISIBLE_FIELD_BE_IN_AVAILABLEIN+"!");
	      				return;
	      			}
	      			//唯一性校验指标校验是否是有效指标
	      			if(checked && only_field_vo[i].checked) {
		      			if(!field_vo[i].checked||!must_field_vo[i].checked) {
		      			     Ext.Msg.alert("提示信息",ONLY_MUST_AVAILABLE_VISIBLE_FIELD+"！");
		      			     return;
		      			}
	      			}
	      			//唯一性校验指标
		   			if(only_field_vo[i] && only_field_vo[i].checked)
		   			    constent_only=constent_only+only_field_vo[i].value+",";
	      			//前台子集列表显示指标
	      			if(!checked && show_field_vo[i] && show_field_vo[i].checked)
	      				constent_show=constent_show+show_field_vo[i].value+',';
	      			//有效指标
	      			if(field_vo[i].checked)
	      				constent=constent+field_vo[i].value+',';
	      			//必填指标
	      			if(must_field_vo[i].checked)
	      				constent_must=constent_must+must_field_vo[i].value+',';
					//显示名称
	      			if(field_vo[i].checked && itemDisplayName[i]){
		      			if(itemDisplayName[i].value == itemName[i].value)
		      				display_name += "###" + "," ;
		      			else
		      				display_name += itemDisplayName[i].value + ",";

			      	}
	      		}
	      		
	      		var fieldContent=constent.replace(/\,/g,"");///指标不能为空
	      		if(fieldContent.length==0){
	      			Ext.Msg.alert("提示信息","请选择有效指标！");
	      			return;
	      		}

	      		document.baseOptionsReForm.field_set_str.value=constent; 
	      		document.baseOptionsReForm.show_field_str.value=constent_show;
	      		document.baseOptionsReForm.mustFill_field_str.value=constent_must;
	      		if(constent_only.length>0)
	      			constent_only=constent_only.substring(0,constent_only.length-1);
      			
	      		document.baseOptionsReForm.func_only.value=constent_only;
	      		document.baseOptionsReForm.itemDisplayNames.value=display_name;
	      	}
      	}
      	
      	document.baseOptionsReForm.action="/recruitment/baseoptions/basesetfield.do?b_save=save";
	    document.baseOptionsReForm.submit();
      	
      }   
    function initOnly()
    {
         var str="${baseOptionsReForm.func_only}";
         if(str=='')
            return;
         var arr=str.split(",");
         var only_field_vo=eval("document.baseOptionsReForm.func_onlys");
         for(var i=0;i<only_field_vo.length;i++)
         {
             for(var j=0;j<arr.length;j++)
             {
                 if(arr[j]==only_field_vo[i].value)
                 {
                      only_field_vo[i].checked=true;
                 }
             }
         }
    }
    function resetValue()
    {
      // zhangcq  2016/7/19 子集显示名称重置
       if (document.getElementById('titleId').innerHTML == "请设置应聘简历子集")
       {
          var tab = document.getElementById("tab") ;
          	for(var i=0;i<tab.rows.length;i++ )
          	 {
          	    	var tdOcj = document.getElementById("tableName"+i);
      			    if(tdOcj != null && typeof(tdOcj) != "undefined")
      			    {
      			      document.getElementById("displayName"+i).value = tdOcj.innerHTML;
      				}
          	}
          	
            for(var i=0;i<document.forms[0].elements.length;i++)
            {
               
             if(document.forms[0].elements[i].type=='radio'||document.forms[0].elements[i].type=='checkbox')
             {
            	 if(document.forms[0].elements[i].disabled!=true)
                 	document.forms[0].elements[i].checked=false;
             }
             if(document.forms[0].elements[i].type=='text'  ) 
              {    
                 if(document.forms[0].elements[i+1].disabled!=true)
                 	  document.forms[0].elements[i].disabled=true;
                     
              }
              
             }
          }
        if (document.getElementById('titleId').innerHTML == "请设置应聘简历指标")
        {
              for(var i=0;i<document.forms[0].elements.length;i++)
              {
             if(document.forms[0].elements[i].type=='radio'||document.forms[0].elements[i].type=='checkbox')
             {
            	 if(document.forms[0].elements[i].disabled!=true)
                 	document.forms[0].elements[i].checked=false;
             }
             if(document.forms[0].elements[i].type=='text'  ) 
              {    
                 if(document.forms[0].elements[i+1].disabled!=true)
                 	  document.forms[0].elements[i].disabled=true;
                     
                    document.forms[0].elements[i].value = document.forms[0].elements[i-1].value
              }
              
             }
        }
         
     } 
    function allSelect(obj)
    {
        var arr=document.getElementsByName("func");
        if(arr)
        {
           for(var i=0;i<arr.length;i++)
           {
              if(obj.checked)
                 arr[i].checked=true;
              else
                 arr[i].checked=false;
           }
        }
    }		
    
    Ext.onReady(function(){
    	Panel = new Ext.Panel({      
             xtype:'panel',
             id:"main",
 	    	 title:"<div style='float:left' id='titleId'>请设置招聘人员库</div><div style='float:right;padding-right:10px;' id='titilPanel' style='font-weight:normal'><a href='javascript:void(0);' onclick='combinePrivString();' >保存</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript:void(0);' onclick='resetValue();' >重置</a></div>",
 	    	 html:"<div id='contentDiv'></div>",
 	    	 region:"center",
 	    	 renderTo: 'allparams',
 	    	 border:false
    	 });

        Ext.widget('viewport',{
            layout:'border',
            padding:"0 5 0 5",            
            style:'backgroundColor:white',
            items:[Panel]
        });

        Ext.getDom('contentDiv').appendChild(Ext.getDom("allparams"));
        
        var view_panel = Ext.getCmp('main');
        view_panel.setAutoScroll(true);
        var winHeight=parent.document.body.clientHeight;
        view_panel.setHeight(winHeight);
        loadTitle();
        
       Ext.getDom("allparams").style.display = "block";
    });
    
    function setCheck(obj){
    	var value = obj.value;
    	var items = document.getElementsByName(obj.name);
    	for ( var i = 0; i < items.length; i++) {
			var item = items[i];
			if(item.value==(value.substring(0,value.length-1)+"1")&&obj.checked){//选择必填后自动勾选可选，选中的时候将value中-改为0
				item.checked = true;
			}
		}
    }
    
    function isFuncMust(id) {
        if (document.getElementById("func_must" + id).checked == true) {
            document.getElementById("func" + id).checked = true;
        }else if (document.getElementById("func_must" + id).checked == false) {
        	if (document.getElementById("func_onlys" + id)) 
                document.getElementById("func_onlys" + id).checked = false;
        }
    	this.isDisable(id);
        
    }
    
    function isFuncOnly(id) {
        if (document.getElementById("func_onlys" + id).checked == true) {
            if (document.getElementById("func_must" + id)) 
            	document.getElementById("func_must" + id).checked = true
            	
            document.getElementById("func" + id).checked = true;
        }
        
    	this.isDisable(id);
        
    }
    
    function isFuncShow (id) {
        if (document.getElementById("func_show" + id).checked == true) {
            document.getElementById("func" + id).checked = true;
        }
        
    	this.isDisable(id);
    }
    
    function isDisable(id) {
        if (document.getElementById("func" + id).checked == false) {
            if (document.getElementById("func_show" + id)) 
                document.getElementById("func_show" + id).checked = false;

            if (document.getElementById("func_onlys" + id)) 
                document.getElementById("func_onlys" + id).checked = false;

            document.getElementById("func_must" + id).checked = false;
        } 
        
		var obj = document.getElementById(id);
		if(obj == null || typeof(obj) == 'undefined')
			return;
		
		var disabled = obj.disabled;
		if(document.getElementById("func" + id).checked == true)
			obj.disabled = '';
		else
			obj.disabled = 'disabled';
    }

    function isTableDisable (id) {
		var obj = document.getElementById("displayName" + id);
		if(obj == null || typeof(obj) == 'undefined')
			return;

		var disabled = true;
		var items = document.getElementsByName("func" + id);
		for (var i = 0; i < items.length; i++) {
			var item = items[i];
			if(item && item.checked) {
				disabled = false;
				break;
			}
		}
		
		if(!disabled && (obj.disabled ||'disabled'==obj.disabled))
			obj.disabled = '';
		else if(disabled && (!obj.disabled ||''==obj.disabled))
			obj.disabled = 'disabled';
    }
</script>
<body>
<div id="allparams" style="padding-top:10px;display:none;cursor:pointer;">
<html:form action="/recruitment/baseoptions/basesetfield">
  <!--保存计算过的需要递交的子集或指标内容 -->
  <html:hidden name="baseOptionsReForm" property="show_field_str"/>
  <html:hidden name="baseOptionsReForm" property="mustFill_field_str"/>
  <html:hidden name="baseOptionsReForm" property="func_only"/>
  <html:hidden name="baseOptionsReForm" property="field_set_str"/>
  <html:hidden name="baseOptionsReForm" property="current_tab"/>
  <html:hidden name="baseOptionsReForm" property="org"/>
  <html:hidden name="baseOptionsReForm" property="itemDisplayNames"/>
  <%
    if(bosflag!=null&&!bosflag.equals("hcm")){
  %>
  <br>
  <%
  }
  %>
<bean:write  name='baseOptionsReForm' property='script_str' filter='false'/>
</html:form>
</div>
</body>
<script language="JavaScript">
initOnly();
</script>

