Ext.define('DashboardURL.CertificateDashboard',{
	
	constructor:function(config) {
		dashboard_me = this;
		// manager:管理员；employee:员工
		dashboard_me.roleType = config.roleType;
		
		this.init();
	},
	// 初始化函数
	init: function() {
		
		var map = new HashMap();
		// manager:管理员；employee:员工
		map.put("opt", "0");
	    Rpc({functionId:'CF01030001',success:function(form){
	    	
	    	var result = Ext.decode(form.responseText);
			dashboard_me.roleType = result.roleType;
	    	
			if("manager" == dashboard_me.roleType){
				map = new HashMap();
				map.put("roleType",dashboard_me.roleType);
				
				Ext.require('DashboardURL.ManagerDashboard',function(){
					var manager = Ext.create("DashboardURL.ManagerDashboard", map);
				});
			}else if("employee" == dashboard_me.roleType){
				// 若既没有档案管理权限，又不是自助用户  则门户给出提示
				if(4 != result.userStatus){
					Ext.showAlert("您没有证书档案管理权限！");
					return;
				}
				map = new HashMap();
				map.put("roleType",dashboard_me.roleType);
				
				Ext.require('DashboardURL.EmployeeDashboard',function(){
					var employee = Ext.create("DashboardURL.EmployeeDashboard",map);
				});
			}
	    }},map);
	}
	
});