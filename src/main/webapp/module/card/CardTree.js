/***
 * 登记表加载登记表树形结构
 * */
Ext.define("Card.CardTree",{
	tabid:undefined,
	multi_cards:undefined,
	inforkind:undefined,
	treeData:undefined,
	treeScope:undefined,
	treePanel:undefined,
	constructor:function(config){
		cardTree_me=this;
		Ext.apply(this,config);
		var map=new HashMap();
		map.put("tabid",cardTree_me.tabid);
		map.put("multi_cards",cardTree_me.multi_cards);
		map.put("inforkind",cardTree_me.inforkind);
		
		if(cardGlobalBeanDefault.a0100){
			map.put("a0100",cardGlobalBeanDefault.a0100);
		}
		map.put("flag","tree");
		map.put("zp_flag",searchCard_me.cardFormProty.zp_flag);
		Rpc({functionId:'CARD0000001',async:false,success:function(res){
	    	   var rs=Ext.decode(res.responseText);
	    	   if(!rs.flagType){
	    		   Ext.showAlert(rs.errMsg);
	    	   }else{
	    		   cardTree_me.treeData=rs.tableTree;
		    	   cardTree_me.createTreePanel();
	    	   }
	       		},scope:searchCard_me},map);
		
	},createTreePanel:function(){
		var treeStore = Ext.create('Ext.data.TreeStore', {
			root: {
				// 根节点的文本
				id:'root',				
				text:'登记表',
				expanded: true,
				icon:rootPath+'/images/add_all.gif',
				children:cardTree_me.treeData
			}
			});

		this.treePanel=Ext.create('Ext.tree.Panel', {//左侧登记表树
			useArrows: false,
			store: treeStore, // 指定该树所使用的TreeStore
			rootVisible: true, // 指定根节点可见
            width: 350,
            split:true,
      		collapseMode:'mini',                 
            border:true,
            collapsible: true, 
            style:'backgroundColor:white', 
            bodyStyle:"",//border-color:"+templatenavigation.headerColor,
            header:false,
            listeners:{
            	'itemclick':function(view,record,item,index){
            		if(record.get("id") == "root")
       					return;
       			    if (record.get("isCategory")!=null && record.get("isCategory")=="1"){
       			         return;
       			    }
       			 //登记表树选择后 获取登记表名称 
       			 Ext.getCmp('treeText').setValue(record.data.text.split(':')[1]);
       			 searchCard_me.tabMark='';
       			 cardTree_me.treeScope.changeTabid(record.id,true);
       			 if(Ext.getCmp("card_treePanel")){
       				Ext.getCmp("card_treePanel").setHidden(true);
       			 }
            	}
            }
		   
		});
		if(cardTree_me.treeData&&cardTree_me.treeData.length==1){
			this.treePanel.setHidden(true)
		}
	},getTreePanel:function(){
		return this.treePanel;
	},getFirstCard:function(){
		if(this.treePanel.getStore().getRoot().data.children){
			var firstRoot=this.treePanel.getStore().getRoot().data.children[0];
			if(firstRoot.children){
				return firstRoot.children[0].text
			}else{
				return firstRoot.text;
			}
		}else{
			return '0';
		}
		
	}
});