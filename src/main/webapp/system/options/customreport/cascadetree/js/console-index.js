// 全局路径
if(typeof(glbRootPath) != "undefined"){
	basePath = glbRootPath;
}

// 导航树
NavTree = function(){
	var nav;
	var navEditor;
	var leafMenu;
	var dirMenu;
	var loader;
	var root;
	var removeFlag = false;
	var titleChangeFlag = false;
	var nodeSelected;
	var mgr;
	return {
		init : function(){
			/*if(!mgr){
				Ext.Msg.alert("警告提示","请先通过NavTree.setMgr()设置mgr");
				return;
			}*/
			if(!loader){//树加载器初始化
					loader = new Ext.tree.TreeLoader({
						baseAttrs: { uiProvider: Ext.tree.TreeCheckNodeUI },
						url : basePath + 'servlet/sys/option/customreport/tree' //定义数据的来源
					});
				
				loader.on('beforeload', function(treeloader, node) {//定义加载前需要进行的处理
				treeloader.baseParams = {//定义传回后台的参数
					id : node.id,//点击的节点的id
					codeset : codeset,
					method : 'tree',
					priv : privs,
					level : level
				};
				}, this);
			}
			if(!root){
				root = new Ext.tree.AsyncTreeNode({//定义根节点
					id : nid,//取jsp页面上定义的id值
					text : rootText,//取jsp页面上定义的text值
					icon : '/images/add_all.gif'
				});			
			}
			if(!nav){
				nav = new Ext.tree.TreePanel({
					//title : "左部导航",
					width : width,
					height: height,
					autoScroll : true,
					animate : false,//展开节点时是否显示动态效果
					loader : loader,//数据加载器
					root : root,//根节点
					singleExpand : false,//设置是否一次只展开树中的一个节点
					//enableDD : true,//设置是否能对节点进行拖拽
					checkModel: checkmodel,   //对树的级联多选,'cascade'(同时选父和子);'parentCascade'(选父);'childCascade'(选子) 'single'单选
        			onlyLeafCheckable: false,//对树所有结点都可选
					listeners : {//定义监听事件
						'dblclick' : function(node, event) {//双击节点
							if (node.isLeaf()) {//当节点是叶子节点时的处理
								//alert(node.id+" : "+node.text);
							}
						},
						'load' : function (node,event) {
							var selectedstr = ","+selectedId+","; 
							if (selectedstr.indexOf(","+node.id+",") != -1) {
								
								//node.getUI().check(true);
								
								node.getUI().checkbox.checked = true;
						
								var childNodes = node.childNodes;
								for (i = 0; i < childNodes.length; i++) {
									var nod = childNodes[i];
									if (selectedstr.indexOf(","+nod.id+",") != -1) {
										nod.attributes.checked = true;
									}
								}
							} else {
								var childNodes = node.childNodes;
								for (i = 0; i < childNodes.length; i++) {
									var nod = childNodes[i];
									if (selectedstr.indexOf(","+nod.id+",") != -1) {
										
										nod.attributes.checked = true;
										
									}
								}
							}
						}
					}/*,
					tools:[{
						id:'refresh',//根据id的不同会出现不同的按钮
						handler:function(){
							var tree = Ext.getCmp('extExample-tree-checkboxTree');
							tree.root.reload();//让根节点重新加载
							tree.body.mask('数据加载中……', 'x-mask-loading');//给tree的body加上蒙版
							tree.root.expand(true,false,function(){
							tree.body.unmask();//全部展开之后让蒙版消失
							});
						}
					}]*/
				});
				// 添加右键菜单
				//nav.on("contextmenu", this.showTreeMenu);
				nav.on("check",this.treeCheckAction,this);
				if (typeof width != "undefined") {
					nav.width=width;
				}
			}
			root.expand(true,true);
			//root.expand(false,true);
			
		},
		setMgr : function(manager){
			mgr = manager;
		},
		getMgr : function(){
			return mgr;
		},
		show : function(){
			nav.render(Ext.getBody());
			nav.getRootNode().toggle();
			
		},
		treeCheckAction : function(node,checked){
			/*var tree = Ext.getCmp(nid);
			var ids = tree.getChecked('id');
			var texts = tree.getChecked('text');
			var valueid = document.getElementById("valueid");
			var valuename = document.getElementById("valuename");
			valueid.value=ids;
			valuename.value = texts;*/
			//alert(str);
		}
	}
}();


	