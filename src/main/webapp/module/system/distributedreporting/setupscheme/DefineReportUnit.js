/**
 * 定义上报单位JS
 */
Ext.define('SetupschemeUL.DefineReportUnit',{
	 //选择的单位存到orgList里面;
	 orgList:undefined,
	 //删除的单位存到orgList里面;
	 removeList:undefined,
	 //选择单位
	 selectionunit:undefined,
	 unitArray:undefined,
	 mask:undefined,
     constructor:function(config){//构造方法
 		thisDefineReportUnit=this;
 		mask=config.mask;
 		thisDefineReportUnit.init(config);
     },
	 init:function(config){
		Rpc({functionId:'SYS0000003028',async:false,success:function(form,action){
				var result = Ext.decode(form.responseText);
				//mask.close();
				unitArray=result.unitArray;
				thisDefineReportUnit.saveFilePath = result.saveFilePath;
		}},new HashMap());
		if(!Ext.getCmp('dataFirstWindow')){
			thisDefineReportUnit.creatWindow().show();
		}
	 },
	 creatWindow:function(){
		var selectionunit=thisDefineReportUnit.creatSelectionunit();
		var dataFirstWindow	=Ext.create('Ext.window.Window', {
		    id:'dataFirstWindow',
		    title: "<div align='left' style='font-size:14px;'>"+define_reporting_unit+"</div>",//style='background:#f3f3f3;font-size:14px;'
		    height: 380,
		    resizable : false,//禁止缩放
		    width:600,
		    modal:true,
		    layout: 'vbox',
		    buttonAlign: 'center',
			items: [selectionunit, {
				//文件存储路径
				xtype: 'textfield',
				id: 'savePath',
				allowBlank: false,
				value: thisDefineReportUnit.saveFilePath,
				margin: '10 0 0 12',
				height: 22,
				fieldLabel: "<span style='font-size:14px;'>" + saveurl + "</span>",
				labelAlign: 'right',
				labelWidth: 90
			}],
		    buttons:[{ 
			     text : sure,
			     height: 25,
			     handler:savaData
			    },{ 
			     text : cancel,
			     height: 25,
			     handler:function(){
			    	 dataFirstWindow.close();
			     }
			}]
		});
		function savaData(){
			var savePathCom = Ext.getCmp("savePath");
			var savePathComValue = savePathCom.getValue();
			if (!Ext.String.trim(savePathComValue)) {
				Ext.Msg.alert(createinput_tips, mustFillSavePath);
				return;
			}
			var promptinformation='';
	    	var checkcode = Ext.getCmp('orgtree').getChecked();
     		var checkArray = new Array();
     		var unitcodeArray = new Array();
     		for(var i=0;i<checkcode.length;i++){
     			var flag = false;
				var checkdata = checkcode[i].data;
				var id = checkdata.id;//guid
				var unitcode = checkdata.qtip;
				for(var j=unitArray.length-1;j>-1;j--){
					var jsonobject = unitArray[j]
					if(id==jsonobject.unitguid){
						flag =true;
						break;
					}
				}
				if(!flag){
					checkArray.push(id);
					unitcodeArray.push(unitcode);
				}
			}
     		var unitcodeList = unitcodeArray.join(",");
     		orgList=checkArray.join(",");
     		var removeArray =  new Array();
     		var removeCodeArray =  new Array();
     		for(var j=unitArray.length-1;j>-1;j--){
     			var jsonobject = unitArray[j];
     			var flag = false;
     			for(var i=0;i<checkcode.length;i++){
     				var checkdata = checkcode[i].data;
    				var id = checkdata.id;
    				if(id==jsonobject.unitguid){
    					flag = true;
    				}
     			}
     			if(!flag){
     				removeArray.push(jsonobject.unitguid);
     				removeCodeArray.push(jsonobject.unitcode);
     				if(promptinformation!=''){
     					promptinformation+=",";
     				}
     				promptinformation+=jsonobject.codeitemdesc;
     			}
     		}

     		var map = new HashMap();
     		if(removeArray.length>0){
     			Ext.Msg.confirm(hint_information,you_are_sure_to_cancel+promptinformation+is_it_reported,function(btn){
     				if(btn=="yes"){
     					removeList=removeArray.join(",");
    		     		map.put("orgList",orgList);
    		     		map.put("unitcodeList",unitcodeList);
    		     		map.put("removeList",removeList);
    		     		map.put("removeCodeList",removeCodeArray.join(","));
    		     		map.put("savePath",savePathComValue);
    					Rpc({functionId:'SYS0000003003',async:false,success:setup_scheme.reload},map);
     				}
     			});
     		}else if (checkArray.length > 0) {
				map.put("unitcodeList", unitcodeList);
				map.put("orgList", orgList);
				map.put("removeList", "");
				map.put("savePath", savePathComValue);
				Rpc({functionId: 'SYS0000003003', async: false, success: setup_scheme.reload}, map);
			} else {
				map.put("orgList", "");
				map.put("removeList", "");
				map.put("savePath", savePathComValue);
				Rpc({functionId: 'SYS0000003003', async: false, success: setup_scheme.reload}, map);
			}
	    };
		return dataFirstWindow;
	},
	//创建选择单位组件
	creatSelectionunit:function(){
	   //选择单位
		var selectionunit=Ext.create('Ext.panel.Panel', {
			 style: 'margin-top:10px',
			// height:350,
			 border:0,
			 //text: '请选择单位',
			 width:'99%',
			 items: [{html:"<p style='font-size:14px;'>"+selection_of_reporting_units+"</p>",
				      border:0,
				      style:'margin-left:18px;'
		     },{
		      xtype: 'panel',
		      id:'orgSelect',
		      style:'margin-left:18px;margin-top:10px;',
		      width:'95%',
		      height:220,
		     // autoScroll : true,//滚动条
		      bodyStyle : 'overflow-x:hidden; overflow-y:auto',
              containerScroll : true,
		      border:1
		     }]
		});
		var jsonUndata = undefined;
    	var map = new HashMap();
		//用户回写数据库中的数据
		Rpc({functionId:'SYS0000003002',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			jsonUndata = result.data;
		}},map);
		//机构树
		var codestore = Ext.create('Ext.data.TreeStore',{
			root: {
				// 根节点的文本
				id:'root',				
				expanded: true,
				children:jsonUndata
			}
        });
        var selector = Ext.widget('treepanel',{
            id:'orgtree',
            width:'100%',
            hight:450,
            autoScroll : true,//滚动条
            store:codestore,
            rootVisible: false,//是否显示根节点,
            border:false
        });
	    var orgPanel=Ext.getCmp('orgSelect');
		orgPanel.add(selector);
		return selectionunit;
	}
 });
