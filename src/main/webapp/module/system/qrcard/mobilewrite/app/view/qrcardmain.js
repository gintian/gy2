Ext.define("QRCard.view.qrcardmain",{
	extend:'Ext.Panel',
	config:{
		fullscreen:true,
		autoScroll : true,

	},
	initialize:function(){
		var me = this;
		var map = new HashMap();
		map.put('qrid',this.config.qrid);
		Rpc({functionId:'SYS0000002005',success:function(form){
			var result = Ext.decode(form.responseText);
			var items = [{
				xtype:'container',
				width:'100%',
				height:'100%',
				style:'background:url(/module/system/qrcard/images/background.png) no-repeat;background-size:100% 100%;',
				layout:{
					type:'vbox',
					align:'center',
					pack:'center'
				},
				items:[	
					{
					xtype:'container',
					html:result.qrData.detail_description,
					margin:'-11 0 0 0 ',
					width:'80%',
					scrollable: {
					    direction: 'vertical',
					    directionLock: true,
					    //隐藏滚动条样式  
				        indicators:false
					},
					height:'67%'
				},{
					xtype:'button',
					style:'background-color:#00A2FF;border-radius:3px 3px 3px 3px;',
					margin:'10 0 0 0',
					width:'45%',
					height:33,
					text:'<div width="100%" style="font-size:20px;color:#ffffff;text-align:center;">开始填写</div>',
					listeners:{
						tap:function(){
							var infoview = Ext.create('QRCard.view.qrcardinfo',{
								tabid:result.qrData.tab_id,
								a0100:me.config.a0100,
								org:result.qrData.org,
								base:result.qrData.base
							});
							Ext.Viewport.removeAll(true);
							Ext.Viewport.add(infoview);
						}
					}
				},{
					xtype:'component',
					width:'100%',
					height:10
				}]
		}]
			me.add(items);		
		}},map);
	}
});