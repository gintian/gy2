/**
*薪资报表弹窗
*zhaoxg 2016-4-13
*/
Ext.define('SalaryReport.CreateWindow',{
    constructor:function(config){
    	winScope = this;
    	var vs = Ext.getBody().getViewSize();
  		winScope.win = Ext.widget("window",{
	          title:config.title,  
	          height:config.height?'100%':vs.height,  
	          width:config.width?'100%':vs.width,
	          layout:'fit',
	          scrollable:false,
	          id:'win',
			  modal:true,
			  closeAction:'destroy',
			  items: [{
				  	xtype:'panel',
				  	layout:'fit',
				  	bodyStyle: 'background:#ffffff;',
		         	border:false,
		         	scrollable:false,//iframe 去除左侧 上方的边框
		         	html:'<iframe name="childFrame" id="childFrame" height="100%" width="100%" scrolling="no" frameborder="0" style="border-top-width: 0px; border-left-width: 0px" src='+config.url+'></iframe>'
	          }]
	    });
//		win.setSize(vs.width, vs.height);  
	    winScope.win.show();
    },
    closeWin:function(){
    	winScope.win.close();
    }
})
