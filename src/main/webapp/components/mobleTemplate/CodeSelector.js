Ext.define("EHR.mobleTemplate.CodeSelector",{
   extend:'Ext.form.Panel',
   xtype:'codeselector',
   
   config:{
   	  cls:'x-codeselector',
   	  scrollable:true,
   	  currentid:'',
      codesetid:'',
      ctrltype:'',
      nmodule:'',
      levelName:'',
      searchtext:'',
      multi:false,
      autoLoad:false,
      onlySelectCodeset:false,
      clearIcon:false,
      ui:'select'
   },
   initialize:function(){
      //if(this.getAutoLoad())
      	this.loadData();
   },
   loadData:function(node){
	    if(node && node.config.leaf/*node.noData*/)//没有子节点
	       return;
        if(node && node.expanded){
           node.parent.child('#childBox').hide();
           node.expanded = false;
           node.child('#toggerBtn').removeCls('x-codeselector-expand');
           return;
        }
        if(node && node.loaded){
        		node.parent.child('#childBox').show();
           	node.expanded = true;
           	node.child('#toggerBtn').addCls('x-codeselector-expand');
           	return;
        }
        if(node)
        	node.child('#toggerBtn').addCls('x-codeselector-expand');
        this.currentNode = node;
        this.setMasked({xtype:'loadmask',message:'加载中...',showAnimation:'slideIn'});
   		var vo = new HashMap();
        vo.put('codesetid',this.getCodesetid());
        vo.put('ctrltype',this.getCtrltype());
        vo.put('nmodule',this.getNmodule());
        vo.put('searchtext',this.getSearchtext());
        vo.put('node','root');
        vo.put('showLevelDept',true);//xus 18/3/14 部门显示多层级
        vo.put('currentid',this.getCurrentid());
        if(node)
           vo.put('node',node.config.orgCode); 
   		Rpc({functionId:'ZJ100000131',success:this.insertItem,scope:this},vo);
   },
   insertItem:function(response){
    	this.setMasked(false);
   		this.setSearchtext('');
        var result = Ext.decode(response.responseText);
        var child = result.children;
        var level = 0;
        var container = this;
        if(this.currentNode){
        		this.currentNode.expanded = true;
        		this.currentNode.loaded = true;
            level = this.currentNode.config.level+1;
            container = this.currentNode.parent.child('#childBox');
            if(!child || child.length == 0){//没有子节点
            	this.currentNode.noData = true;
            	this.currentNode.child('#toggerBtn').removeCls('x-codeselector-expand');
            }
        }
        var items = [];
        var me = this;
        for(var i=0;i<child.length;i++){
        		//currentid 有值情况下，模糊查询没有控制权限范围内的机构  wangb 20191112
        		if(this.getCurrentid() && child[i].id.substring(0,this.getCurrentid().length) != this.getCurrentid()){
        			continue;
        		}
        		items.push({
        		    xtype:'container',items:[{
        		    		xtype:'container',docked:'top',
        		    		layout:'hbox',
        		    		padding:'0 0 0 5',
        		    		orgCode:child[i].id,
        		    		level:level,
        		    		leaf:child[i].leaf,
        		    		expanded:false,
        		    		listeners:{
        		    		    element: 'element',
        		    		    tap:function(){
        		    		    		me.loadData(this);
        		    		    }
        		    		},
        		    		items:[{xtype:'component',width:(level)*15},{
        		    			xtype:'button',
        		    			itemId:'toggerBtn',
        		    			level:level,
        		    			orgCode:child[i].id,
        		    			cls:child[i].leaf? '':'x-codeselector-collapse', //没有下级节点时，不要箭头
        		    			border:0, //去掉边线
        		    			margin:'5 0 0 0',
        		    			width:30,height:30//,expanded:false,
        		    			//handler:this.loadData,scope:this
        		    		},{
        		    			xtype:this.getMulti()?'checkboxfield':'radiofield',
        		    			name:'codeselector',
        		    			value:child[i].id+'`'+child[i].text+'`'+child[i].levelName,
        		    			codesetid:child[i].codesetid,
        		    			flex:1,
        		    			label:child[i].text,
        		    			labelWidth:'75%',
        		    			labelCls:'x-codeselector-label',
        		    			labelWrap:true,
        		    			itemId:'dataSelect',
        		    			dataSelect:false,
        		    			selectable:child[i].selectable,
        		    			listeners:{
        		    				check:function(box){
        		    					var dataSelects = me.query('#dataSelect');
        		    					for(var i = 0 ; i < dataSelects.length; i++){
        		    						if(!dataSelects[i].config.dataSelect)
        		    							continue;
        		    						dataSelects[i].setStyle({'background-color':'transparent'});
        		    						dataSelects[i].config.dataSelect = false;
        		    					}
        		    					box.setStyle('background-color:#FFF9EC;');
        		    					box.config.dataSelect = true;
        		    					if((this.getOnlySelectCodeset() && this.getCodesetid()!=box.config.codesetid)||box.config.selectable=='false'){
        		    						box.uncheck();
        		    					}
        		    				},
        		    				scope:me
        		    			}
        		    		}]
        		    	},{xtype:'container',itemId:'childBox'}]
        		});
        }
        		
   		container.add(items);
   		this.setMasked(false);
   }


});