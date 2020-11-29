/**
*人事异动机构选择window hej 2016-4-6
*
*参数说明  
* 定义map 例如
* map = {
		codesetidstr:codesetidstr,  代码id组合；不能为空 格式：UN,UM,@K 组合显示 三个元素也可以单个出现 
	                         		判断机构数显示形式 按照@K-UM-UN的顺序 然后在判断参数有那几个 就代表可以选择与其同级的代码
							  		参数定义-- UN,UM,@K
							         	   -- UN,@K
								           -- UM,@K  
									       -- @K
									       -- 以上四种参数组合显示的机构数形式与UN,UM,@K相同
									       -- UN,UM
									       -- UM
									       -- 以上两种参数组合显示的机构数形式与UN,UM相同
									       -- UN
		codesource:codesource, 代码生成器类名称,比较复杂的或自定义的可以使用此属性，如果有此属性，下面属性都不需要了
		nmodule:nmodule, 业务模块id
		ctrltype:ctrltype, 
							过滤类型
					 			    如果codesetid 为机构（UN、UM、@K）
					 			          0： 不控制 ；1：管理范围； 2：操作单位； 3：业务范围（如果是此值则 业务模块号必须设置：nmodule参数）
					 			          默认值为1
					 			    如果是普通代码类 
					 		          0：不过滤，其他任意值（包括""）代表需要过滤（有效或在有效日期），默认过滤
		parentid:parentid, 根节点id，如果设置此值，将加载此id的下级节点
		searchtext:searchtext,
		multiple:multiple  false为单选，true为多选
		//issavelevel: issavelevel 是否只能选择与codesetid同级代码 false true  //暂时不用
		isencrypt:isencrypt  是否加密节点id false true
		confirmtype:confirmtype 选择确认的方式 0：直接点击节点确认选择直接关闭窗口（只适应单选），1：选择完毕后点击确认按钮关闭窗口（适应单选和多选）
		height:height
		width:width
		title:title 窗体标题
}
**/
Ext.Loader.setPath("EHR","/components");
Ext.define('EHR.orgTreePicker.OrgTreePicker', {
    extend: 'Ext.window.Window',
    requires:["EHR.extWidget.proxy.TransactionProxy"],
    xtype:'orgtreepicker',
    /**
   		构造函数
    **/
    constructor:function(config){
        var me = this;
		this.codesetidstr = config.map.codesetidstr;
		this.codesource = config.map.codesource;
		this.nmodule = config.map.nmodule;
		this.ctrltype = config.map.ctrltype;
		this.parentid = config.map.parentid;
		this.searchtext = config.map.searchtext;
		this.multiple = config.map.multiple;
		this.callbackfunc = config.map.callbackfunc;
		this.isencrypt = config.map.isencrypt;
		this.confirmtype = config.map.confirmtype;
		this.height = config.map.height;
		this.width = config.map.width;
		this.title = config.map.title;
		this.expandTop = config.map.expandTop;//添加默认展开顶级节点参数
		this.limitSelectNum = config.map.limitSelectNum;//限制选择的个数(参数为数值型)，不传或者为0时不限制
		this.checkedcodeids = config.map.checkedcodeids;//设置多选时，用于回显选中的id
		this.init();
	},
	init:function(){
	    var me = this;
	    me.checklist = [];
	    me.selectnum = 0;
	    var orgTitle = '';
	    var height = 400;
	    var width = 350;
	    var codesetid = '';
	    me.addindexof();
	    if(me.codesetidstr.indexOf('@K')!=-1){
	    	codesetid = '@K';
	    }
	    else if(me.codesetidstr.indexOf('UM')!=-1&&me.codesetidstr.indexOf('@K')==-1){
	    	codesetid = 'UM';
	    }
	    else if(me.codesetidstr.indexOf('UN')!=-1&&me.codesetidstr.indexOf('UM')==-1&&me.codesetidstr.indexOf('@K')==-1){
	    	codesetid = 'UN';
	    }
		var codestore = Ext.create('Ext.data.TreeStore',{
	    	fields: ['text','id','codesetid'], 
	        proxy: Ext.create("EHR.extWidget.proxy.TransactionProxy",{   
	            extraParams:{
	            	codesetid  : codesetid,
	            	codesource : me.codesource,
	            	nmodule    : me.nmodule,
	            	ctrltype   : me.ctrltype,
	            	parentid   : me.parentid,
	            	searchtext : encodeURI(me.searchtext),
	            	multiple   : me.multiple,
	            	isencrypt  : me.isencrypt,
	            	expandTop  : me.expandTop,
	            	checkedcodeids : me.checkedcodeids
	            },
	            functionId:'ZJ100000131'
	        })
	    });
		var selector = Ext.widget('treepanel',{
		    id:'orgtree',
	    	width:300,
	    	store:codestore,
	    	rootVisible: false,
			listeners:{
				//选中树节点事件
				itemclick:function(a,record){
					//bug 44342 选中的组织单元应该有反显
					var list=Ext.query(".selected",true,"orgtree");
					for(var i=0;i<list.length;i++){
						var node=list[i];
					    if(!!node.className.match( new RegExp( "(\\s|^)selected(\\s|$)") )){
					    	node.className = node.className.replace( new RegExp( "(\\s|^)selected(\\s|$)" ), " " );
					    }
					}
					var list=Ext.query(".x-grid-item-selected",true,"orgtree");
					for(var i=0;i<list.length;i++){
						var node=list[i];
						if(!!!node.className.match( new RegExp( "(\\s|^)selected(\\s|$)") )){
							node.className += " selected"; 
					    }
					}
					
					var codesetidarr = me.codesetidstr.split(',');
					if(codesetidarr.indexOf(record.data.codesetid)==-1){
						return;
					}
					var text  = record.data.text;
					var id = record.data.id;
					var codesetid = record.data.codesetid;
					var checked = record.data.checked;
					
					if(me.confirmtype=='0'&&me.multiple==false){//单选以及点击节点直接提交
					    var map = new HashMap();
						map.put('id',id);
						map.put('text',text);
						map.put('codesetid',codesetid);
						me.checklist.splice(0,me.checklist.length);
						me.checklist.push(map);
						Ext.callback(me.callbackfunc, null,[me.checklist]);
               			this.ownerCt.close();
					}
				},
				itemcollapse:function(ele){
					//bug 44342 选中的组织单元应该有反显
					var list=Ext.query(".selected",true,"orgtree");
					for(var i=0;i<list.length;i++){
						var node=list[i];
					    if(!!node.className.match( new RegExp( "(\\s|^)selected(\\s|$)") )){
					    	node.className = node.className.replace( new RegExp( "(\\s|^)selected(\\s|$)" ), " " );
					    }
					}
					var list=Ext.query(".x-grid-item-selected",true,"orgtree");
					for(var i=0;i<list.length;i++){
						var node=list[i];
						if(!!!node.className.match( new RegExp( "(\\s|^)selected(\\s|$)") )){
							node.className += " selected"; 
					    }
					}
				},
				itemexpand:function(ele){
					//bug 44342 选中的组织单元应该有反显
					var list=Ext.query(".selected",true,"orgtree");
					for(var i=0;i<list.length;i++){
						var node=list[i];
					    if(!!node.className.match( new RegExp( "(\\s|^)selected(\\s|$)") )){
					    	node.className = node.className.replace( new RegExp( "(\\s|^)selected(\\s|$)" ), " " );
					    }
					}
					var list=Ext.query(".x-grid-item-selected",true,"orgtree");
					for(var i=0;i<list.length;i++){
						var node=list[i];
						if(!!!node.className.match( new RegExp( "(\\s|^)selected(\\s|$)") )){
							node.className += " selected"; 
					    }
					}
				},
				beforecheckchange:function( node, checked, e, eOpts ){
					if(me.confirmtype=='1'&&me.multiple==true&&me.limitSelectNum&&me.limitSelectNum>0){
						if(me.selectnum==0&&me.checkedcodeids){
							var idarr = me.checkedcodeids.split("`");
							for(var i=0;i<idarr.length;i++){
								var codeid = idarr[i];
								if(!Ext.isEmpty(codeid)){
									me.selectnum++;
								}
							}
						}
						if(!checked)
							me.selectnum++;
						else
							me.selectnum--;
						if(me.selectnum>me.limitSelectNum){
							var orgtree = Ext.getCmp('orgtree');
							Ext.showAlert('最多只能选择'+me.limitSelectNum+'个！');
							me.selectnum--;
							return false;
						}else
							return true;
					}
				}
			}
	    });
	    
	    if(me.title!=undefined&&me.title!=''){
	    	orgTitle = me.title;
	    }else{
	    	if(me.codesetid=='UN'||me.codesetid=='UM'||me.codesetid == undefined){
	    		orgTitle = '选择机构';
		    }else{
		    	orgTitle = '选择岗位';
		    }
	    }
	    
	    if(me.height!=undefined&&me.height!=''){
	    	height = me.height;
	    }
	    if(me.width!=undefined&&me.width!=''){
	    	width = me.width;
	    }
	    
		var orgWin = Ext.widget('window',{
			width:width,
            height:height,
            resizable:false,
            title:orgTitle,
		    modal:true,
		    closeAction:'destroy',
		    border: false,
		    plain:true,
            layout:'fit',
			items:[selector],
			buttons:[{xtype:'tbfill'},
	                    {text:'确定',handler:function(){
	                    if(me.confirmtype=='1'){
	                    	var codesetidarr = me.codesetidstr.split(',');
	                    	if(me.multiple==true){
	                    		var checkcode = selector.getChecked();
	                    		if(checkcode.length==0){
                					Ext.Msg.alert('提示信息','您没有选择任何组织机构树中单位、部门、岗位节点！');
                					return;
                				}
	                			for(var i=0;i<checkcode.length;i++){
									var checkdata = checkcode[i].data;
									var code=checkdata.id;
									var textValue = checkdata.text;
									var codesetid = checkdata.codesetid;
									if(codesetidarr.indexOf(codesetid)!=-1){
										me.checklist.push({id:code,text:textValue,codesetid:codesetid});
									}
								}		
                			}else{
                				var checkcode = selector.getSelection();
                				if(checkcode.length==0){
                					Ext.Msg.alert('提示信息','您没有选择任何组织机构树中单位、部门、岗位节点！');
                					return;
                				}
                				var checkdata = checkcode[0].data;
								var code=checkdata.id;
								var textValue = checkdata.text;
								var codesetid = checkdata.codesetid;
								if(codesetidarr.indexOf(codesetid)!=-1){
									me.checklist.push({id:code,text:textValue,codesetid:codesetid});
								}
                			}
	                    }else{
	                    	var checkcode = selector.getSelection();
               				if(checkcode.length==0){
               					Ext.Msg.alert('提示信息','您没有选择任何组织机构树中单位、部门、岗位节点！');
               					return;
               				}
	                    }
              			
						Ext.callback(me.callbackfunc, null,[me.checklist]);
               			orgWin.close();
               	}},{text:'关闭',handler:function(){orgWin.close();}},{xtype:'tbfill'}]
		}).show();
	},
	 //解决ie数组indexof报错问题
	addindexof:function(){
		if (!Array.prototype.indexOf)
			{
			  Array.prototype.indexOf = function(elt /*, from*/)
			  {
			    var len = this.length >>> 0;
			    var from = Number(arguments[1]) || 0;
			    from = (from < 0)
			         ? Math.ceil(from)
			         : Math.floor(from);
			    if (from < 0)
			      from += len;
			    for (; from < len; from++)
			    {
			      if (from in this &&
			          this[from] === elt)
			        return from;
			    }
			    return -1;
			  };
			}
	}
});