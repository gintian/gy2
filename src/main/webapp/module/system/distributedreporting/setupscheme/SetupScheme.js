/**
 * 设置接收方案主JS
 */
Ext.define('SetupschemeUL.SetupScheme',{
	setup_scheme:'',
	rightGrid:undefined,
	rightTreePanel:undefined,
	leftTreePanel:undefined,
	tableObj:undefined,
	requires:['SetupschemeUL.CreateImportSchema','SetupschemeUL.SetMatch'],
	constructor:function(config) {
		setup_scheme=this;
		this.timerTask = null;
        setup_scheme.init();
	},
	init:function(){
		 var map = new HashMap();
		 Rpc({functionId:'SYS0000003001',success: function(form,action){
				var result = Ext.decode(form.responseText);
				if(result.succeed){
					setup_scheme.createTableOK(result,form,action);
				}else{
					Ext.MessageBox.alert(hint_information,result.message);
				}
		}},map);
	},
	createTableOK:function(result,form,action){
		Ext.util.CSS.createStyleSheet(".scheme-selected-cls{text-decoration:underline;margin-left:10px;margin-right:10px;}","underline");
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		tableObj = new BuildTableObj(obj);
	},
	//状态render
	schemeState:function(value,metaData,Record){
		var value = "";
		var state = Record.data.state;
		if(1==state){
			value = enable;//启用
		}else if(0==state){
			value = not_enable;//不启用
		}else{
			value = donotdefined;//未定义
		}
		return value;
	},
	//上报方式render
	reporttype:function(value,metaData,Record){
		var reporttype = Record.data.reporttype;
		var value = "";
		if('0'==reporttype){
			value = '手工接收';
		}else if ('1'==reporttype){
			value = '中间库';
		}else if ('2'==reporttype){
			value = 'FTP';
		}else if ('3'==reporttype){
			value = 'Webservice';
		}
		return value;
	},
	//选择上报负责人render
	reportleader:function(value,metaData,Record){
		var reportleader = Record.data.reportleader;
		var schemeid = Record.data.schemeid;
	  	var unitcode = Record.data.unitcode;
	  	var rowindx = metaData.rowIndex;
	  	var html = "";
	  	if(reportleader==''){
	  		reportleader ="请选择负责人";
	  		html += "<a id ='selectleader"+schemeid+"'  href=javascript:setup_scheme.selectleader('"+unitcode+"','"+rowindx+"','"+schemeid+"');>"+reportleader+"</a>";
	  	}else{
	  		html+="<div style='width:100%;height:100%;line-height:normal !important;cursor:pointer;' " +
			" onmouseover='setup_scheme.itemMouseOver(this,true,"+schemeid+")' onmouseout='setup_scheme.itemMouseOut(this,true,"+schemeid+")'>";
			html+="<span class='listitemcls'  style='text-align:left;color:#186B14;'>";
			html += "<a id ='selectleader"+schemeid+"'  href=javascript:setup_scheme.selectleader('"+unitcode+"','"+rowindx+"','"+schemeid+"');>"+reportleader+"</a>";
			html += "<img onmouseover='setup_scheme.itemMouseOver(this,false,566)' onmouseout='setup_scheme.itemMouseOut(this,false,123)' onclick='setup_scheme.deleteleader("+schemeid+")' id='imgdel"+schemeid+"' src='/workplan/image/remove.png' style='display:none;width:15px;height:15px;cursor:pointer;position:absolute;top:0px;right:0px;'/>";
			html+="</span></div>";
	  	}
	  	return html;
	},
	//删除上报负责人
	deleteleader:function(schemeid){
		Ext.Msg.confirm ( hint_information , whether_delete_it , function(buttonid,value,opt){
			if(buttonid=='no'){
				 return;
			}else if(buttonid=='yes'){
				 var map = new HashMap();
		    	 map.put('type','delLeader');
		    	 map.put('schemeid',getEncodeStr(schemeid));
				 Rpc({functionId:'SYS0000003038',success: function(form,action){
						var result = Ext.decode(form.responseText);
						if(result.succeed){
							setup_scheme.reload();
						}
				}},map);
			}
		} , this);
	},
	//显示图片
    itemMouseOver:function(itemSpan,delay,schemeid){
        if(delay){
        	setup_scheme.timerTask=setTimeout (function(){
	            var img = Ext.getDom("imgdel"+schemeid);
	            if(img){
	                img.style.display = "inline";
	            }
        	},0);
        }else{
            itemSpan.style.display = "inline";
        }
    },
    //隐藏图片
    itemMouseOut:function(itemSpan,delay,schemeid){
        if(delay){
            var img = Ext.getDom("imgdel"+schemeid);
            if(img){
                img.style.display = "none";
            }
            if(setup_scheme.timerTask){
                clearTimeout(setup_scheme.timerTask);
            }
        }else{
            itemSpan.style.display = "none";
        }


    },
	//操作列render
	lineOperation : function(value, metaData, Record){
	  	var schemeid = Record.data.schemeid;
	  	var unitcode = Record.data.unitcode;
	  	var rowindx = metaData.rowIndex;
	  	var html = "<a href=javascript:SetupschemeGlobal.createImportSchema('"+schemeid+"','"+rowindx+"');>上报方式</a>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
	    html += "<a href=javascript:SetupschemeGlobal.exportScheme('"+unitcode+"','"+rowindx+"','"+schemeid+"');>导出方案</a>";
	  	return html;
	},
	reload:function(res){
		if(res){
			var resultData = Ext.decode(res.responseText);
			if (resultData.saveFilePathMsg) {
				Ext.Msg.alert(setmatch_tips, eval(resultData.saveFilePathMsg));
			}else{
				Ext.getCmp('dataFirstWindow').close();
				Ext.getCmp('setupscheme001_tablePanel').getStore().reload();
			}
		}else{
			Ext.getCmp('setupscheme001_tablePanel').getStore().reload();
		}
		//window.location.href="/module/system/distributedreporting/setupscheme/setupscheme.html";
	},
	
	/**
	 * 定义上报单位
	 * @returns
	 */
	defineReportingUnit:function(){
		//var mask = setup_scheme.createMask('正在加载单位数据请稍后...',Ext.getCmp('setupscheme001_tablePanel'));
		
		//var mask=Ext.MessageBox.wait("正在加载单位数据请稍后...", "定义上报单位");
		//mask:mask
		Ext.require('SetupschemeUL.DefineReportUnit',function(){
			Ext.create("SetupschemeUL.DefineReportUnit",{})
		},this)
	},
	createMask: function (msg,target) {
	        var myMask = new Ext.LoadMask({
	            msg: msg,
	            target: target
	        }).show();
	        return myMask;
    },
	/**
	 * 定义数据标准
	 * @returns
	 */
	defineDataSpecification:function(){
		Ext.require('SetupschemeUL.DefiningDataStandards', function(){
			Ext.create("SetupschemeUL.DefiningDataStandards", {});
		},this);
	},
     /**
      * 上报方式
      * @author caoqy
      */
	createImportSchema : function(schemeid,rowindx) {
		Ext.create('SetupschemeUL.CreateImportSchema',{schemeid:schemeid,rowindx:rowindx});
	},
	/**
	 * 设置对应关系
	 * 集分式已放弃该方法
	 * @author
	 */
	setMatching : function(unitcode,rowindx,schemeid){
		Ext.create('SetupschemeUL.SetMatch',{rowindx:rowindx,schemeid:schemeid,unitcodeid:unitcode});
	},
	/**
	 * 选择上报负责人
	 */
	selectleader:function(unitcode,rowindx,schemeid){
		var f = document.getElementById("selectleader"+schemeid);
		var staffId = "";
		var picker = new PersonPicker({
            multiple : false,
            isSelfUser:false,
            deprecate : "",
            selfUserIsExceptMe:false,
            callback : function(o) {
            	 var username = o.userName;
            	 /*var name = o.name;
            	 if(name!=username&&name){
            		 leader = name;
            	 }else{
            		 leader = username;
            	 }*/
            	 leader = username;
            	 var map = new HashMap();
            	 map.put('type','saveLeader');
            	 map.put('leader',leader);
            	 map.put('schemeid',schemeid);
        		 Rpc({functionId:'SYS0000003038',success: function(form,action){
        				var result = Ext.decode(form.responseText);
        				if(result.succeed){
        					setup_scheme.reload();
        				}
        		}},map);
            }
        }, f);
		picker.open();
	},
	/**
	 * 导出方案
	 */
	exportScheme:function(unitcode,rowindx,schemeid){
		var map = new HashMap();
		map.put("schemeid",schemeid);// 编号id
		map.put("type","exportScheme");//导出方案
		map.put("unitcode",unitcode);
		Rpc({functionId : 'SYS0000003004',async:false,success:function(form) {
				var result = Ext.decode(form.responseText);
				if(!result.flag){
					Ext.Msg.alert(setmatch_tips, createinput_before_tips);// 请先定义上报方式
					return;
				}
				if (!result.filePath) {
					Ext.Msg.alert(setmatch_tips, goSetPath);// '请前往“定义上报信息”处，设置文件存储路径！';
					return;
				}
				if(result.succeed){
					window.open("/servlet/vfsservlet?fileid=" + result.filename + "&fromjavafolder=true");
				}
			}
		}, map);
	},
	/**
	 * 定义校验规则
	 */
	validateRules:function(){
		Ext.require('SetupschemeUL.DefineValidateRules',function(){
			Ext.create('SetupschemeUL.DefineValidateRules',{});
		},this)
	}
});
