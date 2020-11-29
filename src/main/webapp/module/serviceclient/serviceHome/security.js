ServiceClientSecurity = function(){

	var securityObj = {
		requestTime:undefined,
		resetTime:function(){
			this.requestTime = new Date();
		},
		timeOutChecker:function(){
			if(!this.requestTime)
				return;
			if(new Date().getTime()-this.requestTime.getTime()>(1000*60*5)){
//				window.location.href=securityObj.url ;
				Ext.Ajax.request({
					//注销地址
					url: '/servler/sys/logout',
					params:{flag:14},
					success: function() {
		            //注销成功刷新界面
		            window.location.reload();
		            }
				});
			}
				
		},
		start:function(){
			setInterval(this.timeOutChecker.bind(this),5000);
		}
	};
	
	Ext.Ajax.on('requestcomplete',function(){
		securityObj.resetTime();
	});
	
	return securityObj;
}();