
Ext.define('SetupschemeUL.MusterManage',{
	musterManage:'',
	moduleID : '0',//模块号，=0：员工管理；=1：组织机构；参照t_hr_subsys中内容，如果t_hr_subsys没有则按顺序添加；默认为0。
    musterType : '1',//花名册类型；=1：人员花名册；=2：单位花名册；=3：岗位花名册；=4：基准岗位花名册；默认为“1”。
	tableObj:undefined,
	currentPage:'1',
	musterManageStyleid:undefined,
	priv:undefined,//登陆用的权限单位 UN表示超级用户     XXXX,XXXX| XXXX,XXXX 本机单位及下级单位 | 上级单位
	constructor:function(config) {
		musterManage=this;
		musterManageStyleid = config.musterManageStyleid;
		moduleID=config.moduleID==undefined?'0':config.moduleID;
		musterType=config.musterType==undefined?'1':config.musterType;
		currentPage=config.currentPage==undefined?'1':config.currentPage;
		musterManage.menuFocus = true; //分类按钮获得焦点状态
		musterManage.musterFlag = -1;//标志分类面板的显示和隐藏状态  -1 0 隐藏 1 显示
		musterManage.edit = 0;
		musterManage.init();
	},
	init:function(){
		 var map = new HashMap();
		 map.put("moduleID",moduleID);
		 map.put("musterType",musterType);
		 map.put("musterManageStyleid",musterManageStyleid);
		 map.put("currentPage",currentPage);
		 Rpc({functionId:'MM01010001',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					musterManage.createTableOK(result,form,action);
				}else{
					Ext.showAlert(result.message);
				}
		 }},map);
	},
	createTableOK:function(result,form,action){
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
		var conditions=result.tableConfig;
		priv = result.priv;
		var obj = Ext.decode(conditions);
		obj.beforeBuildComp = function(grid) {
			   grid.tableConfig.selModel = Ext.create("Ext.selection.CheckboxModel", {
	                mode: "multi",//multi,simple,single；默认为多选multi
	                checkOnly: true,//如果值为true，则只用点击checkbox列才能选中此条记录
	                enableKeyNav: true
	            })
	    }
		obj.openColumnQuery = true;
		tableObj = new BuildTableObj(obj);
		Ext.getCmp('musterManage001_tablePanel').addListener('beforeselect', beforeselect);
		function beforeselect (grid, record, index, eOpts){
		  	var B0110 = record.data.b0110.split("`")[0];
		  	var flag = false;
	  		if(priv.indexOf("|")!=-1&&""!=priv){//表示既不是超级用户也有上级单位
	  			var array=priv.split("|");
	  			for(var i=0;i<array[0].split(",").length;i++){
	  				var privid = array[0].split(",")[i];
	  				if(""!=privid&&privid==B0110){
	  					flag = true;
	  				}else if(""!=privid&&B0110.indexOf(privid)==0){
	  					flag = true;
	  				}
	  			}
	  		}else if(priv=="UN"){
	  			flag = true;
	  		}
	  		if(!flag){
	  			Ext.showAlert(hint_NoPower);
	  		}
		  	return flag;
		}
	},
	loadStore:function(){
		Ext.getCmp('musterManage001_tablePanel').getStore().reload();
	},
	reloadStore:function(){
		window.location.href="/module/muster/mustermanage/MusterManage.html?musterType="+musterType+"&moduleID="+moduleID+"";
	},
	musterClass:function(){	
		if(musterManage.menuFocus == false){
			if(Ext.getCmp('win')){
				Ext.getCmp('win').destroy();
				musterManage.menuFocus = true;
				Ext.getCmp('style_button').un('blur',musterManage.musterClose);
			}
		}else{
			Ext.require('SetupschemeUL.MusterClass', function(){
				Ext.create("SetupschemeUL.MusterClass", {
					musterType:musterType,
					moduleID:moduleID
				});				
			});
			musterManage.menuFocus = false;		
		}
		
	},
	musterClose:function(){	
		if(Ext.getCmp('win')){	
			//musterManage.musterFlag = 1;
			Ext.getCmp('win').focus(true);
			Ext.get('musterManage001_tablePanel').on('click',function(){
				Ext.getCmp('style_button').focus(true);
				if(musterManage.edit == 1){
					Ext.getCmp('win').un('focusleave',musterClass.focusleave);// 移除监听事件
					Ext.getCmp('win').destroy();
					musterManage.menuFocus = true;
					musterManage.edit = 0;
				}				
			})
		}	
		Ext.getCmp('style_button').un('blur',musterManage.musterClose);	
		musterManage.menuFocus = false;
		if(musterManage.musterFlag == 0){
			if(Ext.getCmp('win')){
				Ext.getCmp('win').destroy();
				musterManage.menuFocus = true;
				Ext.getCmp('style_button').un('blur',musterManage.musterClose);
			}
		}	
		//musterManage.musterFlag = 0;
	},
	/**
	 * 花名册名称超链接
	 */
	hznameShow:function(value,metaData,Record){
		var hzname = Record.data.hzname;
		var tabid = Record.data.tabid;
		var html  = "<a href=javascript:musterManage.showMuster('"+tabid+"');>"+hzname+"</a>";//'"+tabid+"'
		return html;
	},
	/**
	 * 创建时间的渲染
	 */
	createdateShow:function(value,metaData,Record){
		var createdate = Record.data.create_date;
		return createdate.substring(0,10);
	},
	/**
	 * 新增花名册
	 */
	addMuster : function(){
		Ext.require('SetupschemeUL.AddorEditMuster', function(){
			Ext.create("SetupschemeUL.AddorEditMuster", {musterType:musterType,moduleID:moduleID,operateFlag:0});
		});
	},
	/**
	 * 浏览花名册 
	 */
	showMuster:function(tabid){
		var store= tableObj.tablePanel.getStore();
		var currentPage=store.currentPage;
		var address = "/module/muster/showmuster/ShowMuster.html?musterType="+musterType+"&moduleID="+moduleID+"&tabid="+tabid+"";
		if(musterManageStyleid!=undefined){
			address+="&musterManageStyleid="+musterManageStyleid+"";
		}
		address+="&currentPage="+currentPage;
		window.location.href=address;
		
	},
	/**
	 * 删除花名册
	 */
	delMuster:function(){
		if (tableObj.tablePanel.getSelection().length == 0) {
            Ext.showAlert(hint_NoSelectRow);
            return;
        }
		var delArray = new Array();
	        Ext.each(tableObj.tablePanel.getSelection(), function (recard) {
	            delArray.push(recard.data.tabid);
	        });
	        var tabid = delArray.join(',');
	        musterManage.delMusterByid(tabid);
	},
	delMusterByid:function(tabid){
		Ext.Msg.confirm(hint_information,hint_deleteMsg,function(btn){
			if(btn=='yes'){
				var map = new HashMap();
				map.put("tabid",getEncodeStr(tabid));
				map.put("flag","del");
				Rpc({functionId:'MM01010002',success:function(){
					musterManage.loadStore();
				}},map);
			}
		});
	},
	/**
	 * 编辑花名册
	 */
	editMuster:function(tabid){
		Ext.require('SetupschemeUL.AddorEditMuster', function(){
			Ext.create("SetupschemeUL.AddorEditMuster", {musterType:musterType,moduleID:moduleID,operateFlag:1,tabid:tabid});
		});
	},
	
   /**
   * 操作列监听
   */
   lineOperation : function(value, metaData, Record){
	  	var tabid = Record.data.tabid;
	  	var B0110 = Record.data.b0110.split("`")[0];
	  	var html = "&nbsp&nbsp&nbsp<a href=javascript:musterManage.showMuster('"+tabid+"');>"+muster_open+"</a>";
	  	if(priv.indexOf("|")!=-1&&""!=priv){//表示既不是超级用户也有上级单位
  			var array=priv.split("|");
  			var flag = false;
  			for(var i=0;i<array[0].split(",").length;i++){
  				var privid = array[0].split(",")[i];
  				if(privid!=""&&privid==B0110){//权限内的单位可以编辑删除
  					if(flag){
  						break;
  					}
  					flag = true;
  					html += "&nbsp&nbsp&nbsp<a href=javascript:musterManage.editMuster('"+tabid+"');>"+muster_edit+"</a>";
  	  			    html += "&nbsp&nbsp&nbsp<a href=javascript:musterManage.delMusterByid('"+tabid+"');>"+muster_delete+"</a>";
  				}else if(privid!=""&&B0110.indexOf(privid)==0){//下级单位也可以编辑删除
  					if(flag){
  						break;
  					}
  					flag = true;
  					html += "&nbsp&nbsp&nbsp<a href=javascript:musterManage.editMuster('"+tabid+"');>"+muster_edit+"</a>";
  	  			    html += "&nbsp&nbsp&nbsp<a href=javascript:musterManage.delMusterByid('"+tabid+"');>"+muster_delete+"</a>";
  				}
  			}
  		}else if("UN"==priv){
  			html += "&nbsp&nbsp&nbsp<a href=javascript:musterManage.editMuster('"+tabid+"');>"+muster_edit+"</a>";
		    html += "&nbsp&nbsp&nbsp<a href=javascript:musterManage.delMusterByid('"+tabid+"');>"+muster_delete+"</a>";
  		}
	  	return html;
   }
});
