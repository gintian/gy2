//wangjl
Ext.define('ManProjectHoursUL.ManProjectHours',{
	constructor:function(config) {
		manproject_me = this;
		manproject_me.init();
		var str = '';
	},
	// 初始化函数
	init:function() {
		Ext.util.CSS.removeStyleSheet('treegridImg');
		Ext.util.CSS.removeStyleSheet('gridCell');
		var map = new HashMap();
		map.put("init", "init")
	    Rpc({functionId:'PM00000301',async:false,success:manproject_me.getTableOK},map);
	},
	// 加载表单
	getTableOK:function(form,action){
		var result = Ext.decode(form.responseText);
		var conditions=result.tableConfig;
		var obj = Ext.decode(conditions);
		templatelistObj = new BuildTableObj(obj);
		var string = '';
		for(var i=1;i<13;i++){
			var mon = '';
			if(i<10){
				mon = "&nbsp;"+i;
			}else{
				mon = i;
			}
			if(result.month==i){
				string+="<li ><a href='###' onclick='manproject_me.selectPeriodMonth("+i+")' style='background-color:rgb(84, 159, 227)'>"+mon+"月</a></li>";
			}else{
				string+="<li ><a href='###' onclick='manproject_me.selectPeriodMonth("+i+")'>"+mon+"月</a></li>";
			}
		}
		manproject_me.str = string;
		
		var manProjectHourItem = templatelistObj.getMainPanel();
		
		var window = Ext.getCmp('windowid');
		if(window)
			window.close();
		
		window = new Ext.window.Window({
			maximized : true,
			header: false,
			padding:'0 1 1 0',
			border : false,
			id : 'windowid',
			closable : false,
			autoScroll : true,
			items:[manProjectHourItem],
			layout :'fit',
			listeners: {
				afterrender : function () {
					document.getElementById("timetitle").innerHTML=result.year;
					document.getElementById("monthtitle").innerHTML=result.month;
				}
			}
		});
			
		window.show();
	},
	click:function(){
		var win = Ext.getCmp('win');
		if(win)
			win.close();
		
		win = Ext.create('Ext.window.Window', {
			id : 'win',
			header : false,
			resizable : false,
			x : Ext.get("monthtitle").getX() - 105,
			y : Ext.get("monthtitle").getY() + 20,
			width : 210,
			height : 115,
			html : "<div class='hj-wzm-clock dropdownlist'  id='monthlist'  >"
				+ "<ul style='text-align:center'>"
				+ "<span style='color:#549FE3;'>"
				+ "<a dropdownName='monthbox' href='javascript:manproject_me.yearchange(-1);'><img dropdownName='monthbox' id='changeYear' src='/workplan/image/left2.gif' /></a>"
				+ "<span id='myeartitle'>"
				+ document
				.getElementById("timetitle").innerHTML
				+ "</span>年  "
				+ "<a  dropdownName='monthbox' href='javascript:manproject_me.yearchange(1);'><img dropdownName='monthbox' id='yearChange' src='/workplan/image/right2.gif' /></a>"
				+ "</span></ul>"
				+ "<ul id='months'>"
				+ manproject_me.str
				+ "</ul></div>"
		});
		
		win.show();
		
		// 点击window外
		Ext.getBody().addListener('click', function(evt, el) {
			if (!win.hidden && "monthtitle" != el.id && "timetitle" != el.id && "asd" != el.id && "changeYear" != el.id && "yearChange" != el.id)
				win.close();
		});
		
	},
		
	//选择年份
	yearchange:function(ch){
		 var year = Ext.getDom('myeartitle');
		 year.innerHTML = Number(year.innerHTML)+ch;
	},
	//选择月份
	selectPeriodMonth:function(month){
		var map = new HashMap();
		var year=Ext.getDom("myeartitle").innerHTML;
		map.put("year", year);
		map.put("month", month+"");
		Ext.getCmp('win').close();
		Rpc({functionId:'PM00000301',async:false,success:function(form,action){Ext.getCmp('manproject_tablePanel').getStore().reload();
		var result = Ext.decode(form.responseText);
		Ext.get("timetitle").setHtml(result.year);
		Ext.get("monthtitle").setHtml(result.month);
		var string = '';
		for(var i=1;i<13;i++){
			var mon = '';
			if(i<10){
				mon = "&nbsp;"+i;
			}else{
				mon = i;
			}
			if(result.month==i){
				string+="<li ><a href='###' onclick='manproject_me.selectPeriodMonth("+i+")' style='background-color:rgb(84, 159, 227)'>"+mon+"月</a></li>";
			}else{
				string+="<li ><a href='###' onclick='manproject_me.selectPeriodMonth("+i+")'>"+mon+"月</a></li>";
			}
		}
		manproject_me.str = string;
		}},map);
	},
	achieveMent:function(){
//		templatelistObj.getMainPanel().destroy();
//		projectManage.init();
		var window = Ext.getCmp('windowid');
		if(window)
			window.close();
	},
	schemeSaveCallback:function(){
		templatelistObj.getMainPanel().destroy();
		manproject_me.init();
	}
});