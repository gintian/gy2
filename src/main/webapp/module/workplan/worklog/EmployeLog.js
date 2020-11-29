/***
 * 员工日志
 * 
 */
 Ext.define('WorkLogUL.EmployeLog',{
         constructor:function(config){
         Ext.apply(this,config);
        employeLog_me=this;
        this.getRes();
       },getRes:function(){
       	    var map=new HashMap();
       	      Rpc({functionId:'WP40000001',async:false,success:employeLog_me.createPanel,scope:employeLog_me},map);
       },createPanel:function(res){
    	   var params=Ext.decode(res.responseText);
    	   employeLog_me.uplevel = params.uplevel; 
    	   
       	if (!Ext.util.CSS.getRule('#timefiled div')) {
            Ext.util.CSS.createStyleSheet("#timefiled div{border-color:#ffffff;}", "card_css");
        }
        if (!Ext.util.CSS.getRule('#timefiled .x-form-text-default')) {
            Ext.util.CSS.createStyleSheet("#timefiled .x-form-text-default{color:#1B4A98;}", "card_css");
        }
            var config=params.tableConfig;
            employeLog_me.tableObj=new BuildTableObj(config);
          
            employeLog_me.tableObj.insertItem({
            	xtype:'panel',
            	margin:'10 0 0 2',
                //width:100,
                height:25,
                layout:{
                  type:'hbox'  
                
                },
                border:0,
                items:[{
                	xtype:'panel',
                	border:0,
                	layout:{
                	   type:'vbox',
                	   align:'left'
                	},
                	items:[
                	   {
                	   xtype: 'textfield',
                	   id:'timefiled',
                	   readOnly:true,
                	   labelWidth:0,
                       width:80
                	   }
                	   
                	]
                },{
                          xtype:'image',
                          margin:'3 0 0 0',
                           style:'cursor:pointer;',
                          src:"/images/new_module/expand.png",
                          listeners:{
                          	click:{
                          	     element:'el',
                          	     fn:function(){
                          	     	employeLog_me.getMonth();
                          	     }
                          	}
                          }
                       }]
            },0);
         Ext.getCmp('timefiled').setValue(params.date.substring(0,4)+"年"+(params.date.substring(5,7))+"月");
       },getMonth:function(){
       	    var y=Ext.getCmp('timefiled').getY()+Ext.getCmp('timefiled').getHeight();
             var month= Ext.widget('panel',{
                           width:179,
                           height:197,
                           x:0,
                           y:y,
                           floating:true,
                           items:[
                           {
                           xtype: 'monthpicker',
                           value: new Date(),
                           onSelect: function(res) {
                           	      var map=new HashMap();
                           	      var month = ((res.value[0]+1)+"");
                           	      month = month.length==1?"0"+month:month;//haosl 20170208 统一显示格式为yyyyMM
                           	      
                           	      Ext.getCmp('timefiled').setValue(res.value[1]+"年"+month+"月");
                           	      map.put("date",res.value[1]+"年"+month+"月");
                           	      Rpc({functionId:'WP40000002',async:false,success:function(res){
                           	              employeLog_me.tableObj.tablePanel.getStore().reload();         
                           	      },scope:employeLog_me},map);
                            },
                            listeners: {
                                okclick: 'onSelect',
                                cancelclick: function () {
                                     month.hide();
                                }
                            }
                           }
                           ],
                           listeners:{
                                 mouseout: {
                                                element: 'el', 
                                                fn: function(){ month.hide(); }
                                            },
                                 mouseover: {
                                                element: 'el', 
                                                fn: function(){ month.show(); }
                                            }        
                           }
                           
                       
           }).show();
           return month;
       },showWorkLog:function(rowname,cell,record,rowIndex,colIndex){
            var html="<a href='javascript:employeLog_me.showLogWindow(\""+record.data.nbase_e+"\",\""+record.data.a0100_e+"\",\""+rowname+"\")'>"+rowname+"</a>";
         return html;
       },showLogWindow:function(nbase,a0100,rowname){
       	    var year=Ext.getCmp('timefiled').getValue().split('年')[0];
       	    var month=Ext.getCmp('timefiled').getValue().split('年')[1].split('月')[0];
       	 
       	    var nowMonth=year+'-'+month;
       	    Ext.create('Ext.window.Window',{
       	        title:rowname+'的工作日志',
       	        width:Ext.getBody().getWidth(),
       	        height:Ext.getBody().getHeight(),
       	        floating:true,
       	        modal:true,
       	        html:' <iframe scrolling="auto" frameborder="0" width="100%" height="100%" src="../../../module/workplan/worklog/WorkLog.html?nbase='+nbase+'&a0100='+a0100+'&employeflag=1&nowMonth='+nowMonth+'"></iframe>'
       	        
       	    }).show();
       	     
       },showUplevelCode: function (value,c,record) {
    	   	var value = "";
    	   	var e0122 = record.data.e0122;
    	   	if(Ext.isEmpty(e0122))
    	   		return value;
    	   	
    	   	var map = new HashMap();
   			map.put("e0122", e0122);
   			map.put("uplevel", employeLog_me.uplevel);
   			Rpc({functionId:'WP40000003',async:false,success:function (response){
   					var map	 = Ext.decode(response.responseText);
   					value = map.codeDesc;
   				}
   			},map);
   			
   			return value;
       },exportWorkLog:function (){
    	   	 var myMask = new Ext.LoadMask({
    	   			 msg: '正在导出......',
    	   			 target: employeLog_me.tableObj.getMainPanel()
    	   		 });
    	   	 myMask.show();
    	   	
    	    var map = new HashMap();
    	   	var displaycolumns = Ext.getCmp("employlog_tablePanel").getColumnManager().getColumns();
			var outputcolumns = new Array();
			var column;
			var level = 1;
			for(var i in displaycolumns){
				column =  displaycolumns[i];
				
				if(column.dataIndex && column.dataIndex.length>0 && !column.hidden){
				    var ups = [];
				    employeLog_me.getUpColumnText(column,ups);
				    level = ups.length+1>level?ups.length+1:level;
					outputcolumns.push({columnid:column.dataIndex,width:column.width,operationData:column.operationData?column.operationData:[],ups:ups});
				}
			}

			var selectPersons = "";
			var selectDate = Ext.getCmp("employlog_tablePanel").getView().getSelectionModel().getSelection();
			for (var i=0;i<selectDate.length;i++){
				selectPersons += selectDate[i].data.nbase_e + ":" + selectDate[i].data.a0100_e + ",";
			}
			
			map.put("outputcolumns",outputcolumns);
			map.put("headLevel",level);
			map.put("selectPersons",selectPersons);
  			Rpc({functionId:'WP40000004',success:function (response){
  					myMask.destroy();
  					var map	 = Ext.decode(response.responseText);
					//zhangh 2020-3-5 下载改为使用VFS
					var outName=map.filename;
					outName = decode(outName);
					var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
  				}
  			},map);
       },getUpColumnText:function(column,ups){
	     if(column.ownerCt.xtype=='gridcolumn'){
	         ups.push(column.ownerCt.text+'`'+column.ownerCt.id);
	         employeLog_me.getUpColumnText(column.ownerCt,ups);
	     }
	}
 })