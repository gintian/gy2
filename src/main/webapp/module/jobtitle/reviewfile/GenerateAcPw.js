/**
 * 上会材料-随机生成账号密码-弹出对话框
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 * 
 * */
Ext.define('GenerateAcPwURL.GenerateAcPw',{
	 idlist:'',
	 isSelectAll:'',
	 constructor:function(config){
		this.idlist = config.dataList;//获取前台选中或全选后反选的会议id与申请人id
		this.isSelectAll = config.isSelectAll;//是否是全选-1全选返回取消选中列信息；-0非全选返回选中值信息
     	this.init();
     },
     init:function(){
     	var me = this;
    	var win = Ext.create('Ext.window.Window',{
	  		title:'生成'+zc.reviewfile.step3showtext+'账号',
	 		width: 300, 
	        height: 150,
	        autoScroll:true,
	        modal: true,
	        border:false,
	       	buttonAlign:'center',
	       	layout:{
	       		type : 'vbox',
				align: 'center'
	       	},
	       	bodyStyle: 'background:#ffffff;',
	       	items:[{
	       		xtype:'textfield',
	   	        id:'input_step3',
	   	        fieldLabel:'账号数',
	   	        labelWidth:40,
	   	        labelPad:10,	
	            regex:/^\d+$/,
	            regexText: zc.label.innerNo,
	   	        padding:'20 10 0 10'}],
       	    bbar :[{xtype:'tbfill'},{
        		text:'确定',
        		margin:'0 10 0 0',
        		handler : function() {
        			Ext.MessageBox.wait("正在生成账号密码...", "等待");
        			var inputValue = Ext.getCmp("input_step3").getValue();
        			win.close();
        			var regex= /^\d+$/;
        			//校验
        			if((!regex.test(inputValue)&&inputValue!="")){
        				Ext.showAlert(zc.label.innerNoEro);
        				return ;
        			}
        			var map = new HashMap();
        			map.put("isSelectAll", me.isSelectAll);//是否全选
					map.put("idlist", me.idlist);//数据集
        			map.put("inputValue", inputValue);
        			
//        			//如果都为0，则报提示
//        			if(isAllZero){
//        				Ext.MessageBox.alert(zc.label.remind, "请输入数量并且不能全为0！");
//        				return ;
//        			}
        			
        			Rpc({functionId:'ZC00003005',async:true,success: function(form,action){
        				Ext.MessageBox.close();
        				var data = Ext.decode(form.responseText);
        				if(data.msg)
        					Ext.showAlert(data.msg);
    					reviewfile_me.loadTable();
        			}}, map);
				}
    		},{
	        	text:zc.label.cancel,
	        	listeners:{
	        		'click':function(){
	        			win.close();
	        		}
	        	}
       		},{
       			xtype:'tbfill'
       		}]
		});
		win.show();
    }
});