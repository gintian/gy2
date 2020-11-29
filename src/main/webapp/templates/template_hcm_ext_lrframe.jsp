<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.utils.PubFunc"%>
<%  
UserView userView=(UserView)session.getAttribute(WebConstant.userView);

String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
//主题皮肤
String themes = "default";
if(userView != null){ 
    themes = SysParamBo.getSysParamValue("THEMES",userView.getUserName());   
 }
//模块菜单menu_id值
String menu_id = request.getParameter("menu_id");
menu_id = menu_id==null?"":menu_id;
//模块菜单树iframe的target，暂时无用
String menu_target = request.getParameter("menu_target");
menu_target = menu_target==null?"":menu_target;
//内容区域center的请求url，默认登录进来显示的主面板，读取的url为screen-definition.xml的
String center_url = request.getParameter("center_url");
center_url = center_url==null?"":center_url;
center_url = PubFunc.decrypt(center_url);
center_url=center_url.replaceAll("`","&");
//内容区域iframe的target值
String center_target = request.getParameter("center_target");
center_target = center_target==null?"":center_target;
//模块菜单名称
String name = request.getParameter("menu_name");
name = name==null?" ":name;
String allname = request.getParameter("allname");
if(allname!=null&&allname.length()>0){
	name = PubFunc.splitString(allname,18);
	if(allname.equals(name)){
		   allname="";
	}
}
String first_center_url = request.getParameter("first_center_url");
first_center_url = first_center_url==null?"":first_center_url;
first_center_url = PubFunc.decrypt(first_center_url);
first_center_url=first_center_url.replaceAll("`","&");

String first_center_target = request.getParameter("first_center_target");
first_center_target = first_center_target==null?"":first_center_target;

String mod_id = request.getParameter("module");

%>
<html>
    <%@ page contentType="text/html; charset=UTF-8"%>
    <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>   
    <%@ taglib uri="/tags/struts-html" prefix="html" %>
    <head>
        <title>Simple Tasks</title>
        <link rel="stylesheet" type="text/css" href="../../ext/resources/css/ext-all.css" />

        <!--link rel="stylesheet" type="text/css" href="../../ext/resources/css/slate.css" / -->
        <script type="text/javascript" src="../../ext/ext-all.gzjs"></script>
        <script type="text/javascript" src="../../ext/rpc_command.js"></script>    
        <!-- .x-tool{overflow:hidden;width:15px;height:15px;float:right;cursor:pointer;background:transparent url(/images/tool-sprites.gif) no-repeat;margin-left:2px;} -->     
<link rel="stylesheet" type="text/css" href="/css/hcm/themes/<%=themes %>/menu.css" />
<style type="text/css">
#ext-gen25{margin-top:-1px; margin-bottom:2px;}
#iframebox{width:100%;height:100%;overflow:auto;-webkit-overflow-scrolling:touch;}/*for ipad iframe scroll*/

.safebox-close{
	float:right;
	cursor:pointer;
	margin-top:11px;
	margin-right:10px;
	background:url(/ext/ext6/resources/images/tools/tool-sprites.gif) 0 0 no-repeat;
}
.safebox-close-over{
	background-position:-15px 0;
}
</style>

</head>
<body>
<html:form action="/templates/index/mainpanel">
  <input type="hidden" id="cs_app" value="${sysForm.cs_app_str}" name="cs">
      <hrms:hcmmenu menu_id="<%=menu_id %>" target="" max_menu="5" themes="<%=themes %>" name="mbobj" />
  <a href="" style="display:none" id="hostname">wizard</a>  
</html:form>
<script type="text/javascript">
//防止打开多个页面造成数据混乱 guodd 2015-12-18
window.document.oncontextmenu = function(){return false;};
    var viewport;
    /**空白图片*/
	Ext.BLANK_IMAGE_URL="/images/s.gif";


    var curr_module,curr_pnl,curr_node;
    /**cmq changed at 20121111 for ipad iframe scroll*/
    var il_body='<iframe src="<%=center_url %>" name="<%=center_target %>" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
    <% if(center_url.length()==0){%>
    il_body='<iframe src="<hrms:insert parameter="HtmlBody" />" name="il_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ';
    <%}%>
    if(navigator.userAgent.match(/iPad|iPhone/i)) 
        il_body='<div id="iframebox">'+il_body+'</div>';
       
Ext.onReady(function()
{
	Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter2'});
            var hcmcenter = new Ext.Panel({
                title: '',
                border:false,
                region:'center',
                html: il_body,
                cls:'empty'
            });


	window.treeStore = Ext.create('Ext.data.TreeStore', {
		fields:fields,
		// 定义根节点
		root: {
			// 根节点的文本
			text: '根节点',
			expanded: true,
			// 指定根节点包含的所有子节点
			children:mbobj
		}
	});
	var hcmtree = Ext.create('Ext.tree.Panel', {
		// 不使用Vista风格的箭头代表节点的展开/折叠状态
		useArrows: false,
		region:'west',
		title: '<div onclick=\"toNavigation();\" title=\"<%=allname %>\" style=\"line-height:18px;cursor:pointer;\"><%=name %></div>',				
		width:182,
		margins:'0 0 0 0',
        minSize: 175,
        maxSize: 400,               
        border:true,
        //菜单收缩样式
        placeholder:{
        		width:17,height:30,border:false,bodyCls:'x-menu-placeholder',
        		items:{
        			xtype:'image',height:16,width:16,cls:'x-menu-placeholder-bar',
        			listeners:{
        				render:function(){
        					var me = this;
        					this.getEl().on('click',function(){
        						this.ownerCt.placeholderFor.expand();
        					},me);
        				}
        			}
        		}
		},
        scroll:'vertical',
        collapsible: true,
        split:true,
		layoutConfig:{
            animate:true
        },
		store: treeStore, // 指定该树所使用的TreeStore
		rootVisible: false, // 指定根节点可见
		listeners: {expand: handle,collapse:function(){
			
				Ext.util.CSS.createStyleSheet(".mysplitter2{background: url(/images/hcm/themes/<%=themes %>/menu_split_bg2.gif) repeat-y scroll 0px 10px rgb(255, 255, 255);}");
			},
			'itemclick':function(view, record, item){
	            if(record.get('leaf'))
	                return;
	            if(record.get('expanded'))
	                this.collapseNode(record);
	            else
					this.expandNode(record);
			},
			'beforeitemclick': function(view, record, item, index, e,obj){
				var target_to_use = record.data.targetToUse;
				var url_to_use = record.data.urlToUse;
				var isCollapse=record.data.hide;
				var validateType = record.data.validateType;
				
				if(record.isExpanded())
					return true;
				
				if("1"==validateType){
					check(record,target_to_use,url_to_use);
					return false;
				}else if("2"==validateType || "1,2"==validateType){
					smscheck(record,target_to_use,url_to_use,("1,2"==validateType));
					return false;
				}
				return true;
				/*
				if("2"==validateType || "1,2"==validateType){
					var thecodeurl ='/general/sys/validate/secondValidate.do?b_init=link';
					var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:yes');
					if(return_vo==null)
						return false;
					
					var content=return_vo.content;
					var inputcode=return_vo.inputcode;
					if(content!=inputcode){
						alert("校验码不正确!");
						return false;
					}
					
					if("1,2"==validateType)
						return check(isCollapse,target_to_use,url_to_use);
						
					if('true'==isCollapse)
						accordion.toggleCollapse(false);

					if(target_to_use != null && target_to_use != "")
						eval("document."+target_to_use+".location='"+url_to_use+"'");
						
				}
				*/
			}
		}
	});
	
	/*展开菜单时进行并发点数控制校验*/
    hcmtree.on("itemexpand",function(node){
	    	if(node.data.mod_id && node.data.mod_id.length>0){
	            curr_pnl=this;
	            curr_node=node;
	            var map = new HashMap();
	            map.put("module",parseInt(node.data.mod_id));
	            map.put("auth_lock","true");
	            Rpc({functionId:'1010010206',
	            	success:function(response){
	            		var value=response.responseText;
	                var map=Ext.decode(value);
	                
	                if(map.succeed==false)
	                {
		                	alert(map.message); 
	                		if(map.message=="会话超时,请重新登录!")
	                    {
	                        var newwin=window.open(window.location,"_top","toolbar=no,location=0,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
	                        window.opener=null;
	                        self.close();
	                    }
	                    
	                		setTimeout(function(){ node.collapse();},500);
	                }
	            	
	            }},map);
	    }
    });
hcmtree.on("expandnode",function(node){ 
     if(node.attributes["mod_id"]!=undefined)
     {
             curr_pnl=this;
             curr_node=node;
             var map = new HashMap();
             map.put("module",node.attributes["mod_id"]);
             map.put("auth_lock","true");
             Rpc({functionId:'1010010206',success:authorize},map);
     } 
    });
            var root = hcmtree.store.getRootNode( ).childNodes[0];   
            /*模块菜单进入    校验二次密码验证  wangb 20190516*/
            if((root && root.data.validateType) || (!root.isLeaf() && root.childNodes.length>0 && root.childNodes[0].data.validateType)){// 文件夹 或  菜单 设置了 二次密码验证  跳转的地址和对应iframe name 值
            	var framename = "<%=center_target %>";
            	framename = framename? framename:'il_body'; 
            	var src = "<%=center_target %>"? "<%=center_url %>":"<hrms:insert parameter="HtmlBody" />";
            	hcmcenter = new Ext.Panel({
                	title: '',
                	border:false,
                	region:'center',
                	html: '<iframe src="" name="'+framename+'" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ',
                	cls:'empty'
            	});
            	
            	viewport = new Ext.Viewport({
                	layout:'border',                   
                	items:[hcmtree,hcmcenter]
            	});
            	
            	if(root && root.childNodes.length>1 && root.childNodes[0].data.validateType){
            		root = root.childNodes[0];
            	}
            	if("1"==root.data.validateType){
					check(root,root.data.targetToUse,src);
				}else if("2"==root.data.validateType || "1,2"==root.data.validateType){
					smscheck(root,root.data.targetToUse,src,("1,2"==root.data.validateType));
				}
            }else{
            	viewport = new Ext.Viewport({
                	layout:'border',                   
                	items:[hcmtree,hcmcenter]
            	});
            }
            /**展开第一个菜单*/
            root = hcmtree.store.getRootNode( ).childNodes[0];  
            while(!root.isLeaf() && root.childNodes.length>0 && !root.data.validateType){//菜单文件夹没有设置二次密码验证 才能展开  wangb 20190516
            		root.expand();
            		root = root.childNodes[0];
            }
           // hcmtree.expandAll();
}); 

function check(node,target_to_use,url_to_use){
	var xpoint = document.body.clientWidth/2-100;
	Ext.widget('panel',{
		floating:true,
		id:'passchecker',
		header:{
				xtype:'container',height:37,style:'background:white;color:black;line-height:37px',
				items:[{
					xtype:'component',html:'安全验证',style:'float:left;'
				},{
					xtype:'component',height:15,width:15,cls:'safebox-close actionbutton',overCls:'safebox-close-over'
				}]
		},
		bodyStyle:'border:1px solid #c5c5c5;border-width:1 0 0 0;',
		width:300,
		height:200,
		modal:true,
		layout:'fit',
		items:{
			xtype:'container',
			padding:'16 0 0 50',
			items:[{
  				xtype:'label',
  				text:'请输入您的密码：'
  			},{
  				xtype:'textfield',margin:'10 0 0 0',width:200,inputType:'password'
  			},{
  				xtype:'box',margin:'10 0 0 0',width:200,
  				html:'提示：请输入您的登录密码，校验认证通过以后，才能进入该模块。'
  			},{xtype:'box',height:20},{
  				xtype:'box',width:46,cls:'actionbutton',
  				style:'border:1px solid #c5c5c5;background:#f1f1f1;cursor:pointer;padding:3px 10px 3px 10px;margin-left:70',
  				html:'确定'
  			}]
		},
		listeners:{
			click:{
				element:'el',
				delegate:'div .actionbutton',
				fn:function(e){
					var box = Ext.getCmp('passchecker');
					if(e.target.className.indexOf('safebox-close')>-1){
						box.destroy();
						return;	
					}
					var password = box.query('textfield')[0].getValue();
					var vo = new HashMap();
					vo.put("password",password);
					vo.put("transType","pwdcheck");
					Rpc({functionId:'0202011021',success:function(response){
						var backparam = Ext.decode(response.responseText);
						if(backparam.result){
							box.destroy();
							if(!node.isLeaf())
								node.expand();
							if(target_to_use != null && target_to_use != "")
								window.open(url_to_use,target_to_use);
						}else{
							alert("密码输入不正确！");
						}
					}},vo);
				}
			}
		}
	}).showAt(xpoint,100);
	return false;
	/*
	var thecodeurl ='/general/sys/validate/validatePassword.jsp'; 
	var return_vo= window.showModalDialog(thecodeurl, '', 'dialogWidth:470px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:yes');
	if(return_vo==null)
		return false;
	
		var content=return_vo.content;
		var inputcode=return_vo.inputcode;
		if(content!=inputcode){
			alert("密码不正确!");
			return false;
		}
		
		if(isCollapse=='true')
			accordion.toggleCollapse(false);

		if(target_to_use != null && target_to_use != "")
			eval("document."+target_to_use+".location='"+url_to_use+"'");
		*/
}
function smscheck(node,target_to_use,url_to_use,checkPassword){
	var phone;
	var delaytime;
	var vo = new HashMap();
	vo.put("transType","getPhone");
	Rpc({functionId:'0202011021',async:false,success:function(response){
		var backparam = Ext.decode(response.responseText);
	  	if(backparam.result){
	  		phone = backparam.phone;
	  		delaytime = backparam.delaytime;
	  	}else{
	  		alert(backparam.msg);
	  	}
	}},vo);
	
	if(!phone || phone.length<1)
		return;
	var xpoint = document.body.clientWidth/2-100;
	Ext.widget('panel',{
		floating:true,
		id:'passchecker',
		header:{
				xtype:'container',height:37,style:'background:white;color:black;line-height:37px',
				items:[{
					xtype:'component',html:'安全验证',style:'float:left;'
				},{
					xtype:'component',height:15,width:15,cls:'safebox-close actionbutton',overCls:'safebox-close-over'
				}]
		},
		bodyStyle:'border:1px solid #c5c5c5;border-width:1 0 0 0;',
		width:400,
		height:checkPassword?360:300,
		modal:true,
		layout:'fit',
		items:{
			xtype:'container',
			padding:'16 0 0 20',
			items:[{
 				xtype:'label',
 				text:'手机号码: '+phone
 			},{
 				xtype:'container',layout:'hbox',height:50,
 				items:[{
 					xtype:'textfield',itemId:'codebox',margin:'10 0 10 0',fieldLabel:'验证码',labelWidth:56,width:120,labelAlign:'right'
 				},{
 					xtype:'button',width:70,margin:'10 0 0 10',text:'发送验证码',
 					handler:function(btn){
 						//发送验证码
 						var vo = new HashMap();
 						vo.put("phoneNumber",phone);
 						Rpc({functionId:'0202011020',success:function(response){
   	  					var backparam = Ext.decode(response.responseText);
   	  					//发送失败，提示
   	  					if(backparam.error==1)
						{
							alert(backparam.content);
							return;
						}
						
						var codebox = btn.ownerCt.child('#codebox');
						codebox.realcode = backparam.content;
						//发送成功，隐藏发送按钮，显示倒计时
   	  					btn.setText('重新发送');
   	  					btn.setVisible(false);
   	  					var timerBox = btn.ownerCt.child('#timer');
   	  					timerBox.setVisible(true);
   	  					timerBox.update(delaytime+'s');
   	  					timerBox.delaytime = delaytime;
   	  					//开始倒计时
   	  					var forcode = setInterval(function(){
   	  						timerBox.delaytime--;
   	  						timerBox.update(timerBox.delaytime+'s');
   	  						//当倒计时为0时，隐藏倒计时，显示重新发送按钮
   	  						if(timerBox.delaytime==0){
   	  							window.clearInterval(forcode);
   	  							btn.setVisible(true);
   	  							timerBox.setVisible(false);
   	  						}
   	  					},1000);
   	  				}},vo);
 					}
 				},{
 					xtype:'box',margin:'14 0 0 10',hidden:true,itemId:'timer'
 				}]
 			},
 			checkPassword?{xtype:'textfield',inputType:'password',itemId:'pwdbox',margin:'10 0 0 0',fieldLabel:'密码',labelWidth:56,width:200,labelAlign:'right'}:undefined,
 			{
 				xtype:'component',margin:'0 0 0 0',width:360,
 				html:'<table booder="0"><tr><td align="left" Colspan="2" style="font-size:12px;">提示：</td></tr>'+
	           '<tr> <td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td  align="left" style="font-size:12px;">1.请确认上述手机号码是否为接收短信验证码的正确号码。如有误，请不要点击“获取验证码”，并尽快联系您的人力资源主管进行信息更正。</td></tr>'+
	           '<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left" style="font-size:12px;">2.点击获取验证码后，短信可能由于网路等原因有所延迟，如果您在'+delaytime+'秒内手机没有收到短信验证码，请重新获取。</td></tr>'+
	           (checkPassword?'<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left" style="font-size:12px;">3.请输入您的登录密码</td></tr>':'')+
                  '<tr><td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left" style="font-size:12px;">'+
                  (checkPassword?'4':'3')+'.验证通过后才能进入该模块。</td></tr>'+
	   		   '</table>'
 			},{
  				xtype:'box',width:46,cls:'actionbutton',
  				style:'border:1px solid #c5c5c5;background:#f1f1f1;cursor:pointer;padding:3px 10px 3px 10px;margin-left:150',
  				html:'确定'
  			}]
		
		},
		listeners:{
			click:{
				element:'el',
				delegate:'div .actionbutton',
				fn:function(e){
					var box = Ext.getCmp('passchecker');
					if(e.target.className.indexOf('safebox-close')>-1){
						box.destroy();
						return;	
					}
					var codefield = box.queryById('codebox');
					var smscode = codefield.getValue();
	  				var realcode = codefield.realcode;
	  				if(smscode!=realcode){
	  					alert('验证码错误');
	  					return;
	  				}
	  				
	  				var pwdfield = box.queryById('pwdbox');
	  				if(pwdfield){
	  					var vo = new HashMap();
	  					vo.put("password",pwdfield.getValue());
	  					vo.put("transType","pwdcheck");
	  					Rpc({functionId:'0202011021',success:function(response){
    	  					var backparam = Ext.decode(response.responseText);
    	  					if(backparam.result){
    	  						box.destroy();
    	  						if(!node.isLeaf())
    								node.expand();
    	  						if(target_to_use != null && target_to_use != "")
    								window.open(url_to_use,target_to_use);
    	  					}else{
    	  						alert("密码错误！");
    	  					}
    	  					
    	  				}},vo);
	  				
	  				}else{
	  					box.destroy();
	  					if(isCollapse=='true')
	  						accordion.toggleCollapse(false);
	  					if(target_to_use != null && target_to_use != "")
							window.open(url_to_use,target_to_use);
	  				}
				}
			}
		}
	}).showAt(xpoint,100);
}
//eHR-V7点击左侧树最顶端的title右侧能显示出来导航图。  jingq add 2014.07.17
function toNavigation(){
	
	var map = new HashMap();
    map.put("module",'<%=mod_id%>');
    map.put("auth_lock","true");
    Rpc({functionId:'1010010206',success:function(response){
    	var value=response.responseText;
    	var map=Ext.decode(value);
        if(map.succeed==false)
        {
            alert(map.message); 
            return;
        }
        
        var href='<%=center_url%>';
    	var target='<%=center_target%>';
    	if(href==""){
    		href = "<%=first_center_url%>";
    		target="<%=first_center_target%>";
    	}
    	sysForm.action=href;
    	sysForm.target=target;
    	sysForm.submit();
    }},map); 
	
	return;
	
	
}   

function getModuleNode(name)
{
  var node,mobj;
  for(var i=0;i<modulemenu.length;i++)
  {
     var mobj=modulemenu[i];
     if(mobj.text==name)
     {
         node=mobj;
         break;
     }
  }
  return node;       
}



function handle(pnl)
{
	Ext.util.CSS.createStyleSheet(".mysplitter2{background: url(/images/hcm/themes/<%=themes %>/menu_split_bg2.gif) repeat-y scroll -1px 10px rgb(255, 255, 255);}");
  var name=pnl.id;
  var title=pnl.title;
  var node=getModuleNode(name);  
  curr_module=node;
  curr_pnl=pnl;
  if(node.mod_id)
  {
     var map = new HashMap();
     map.put("module",node.mod_id);
     map.put("auth_lock","true");
     Rpc({functionId:'1010010206',success:authorize},map); 
  }
}    
function authorize(response)
{
     var value=response.responseText;
     var map=Ext.util.JSON.decode(value);
     if(map.succeed==false)
     {
         alert(map.message); 
         if(curr_pnl.id=='自助服务'||curr_pnl.id=='绩效自助')
         {
             curr_node.collapse(true);
         }
         else
         {
             curr_pnl.collapse(true);
         }
         /**20110725*/
         if(map.message=="会话超时,请重新登录!")
         {
             var newwin=window.open(window.location,"_top","toolbar=no,location=0,directories=0,fullscreen=0,status=no,menubar=no,scrollbars=no,resizable=no");
             window.opener=null;
             self.close();
         }               
     }
     else
     {
          var href,target;
          if(curr_module)
          {
              href=curr_module.href;
              target=curr_module.hrefTarget; 
              if(href)
              {
                 window.open(href,target);
              }      
          }  
     }
}


</script>  
</body>
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script>
</html>