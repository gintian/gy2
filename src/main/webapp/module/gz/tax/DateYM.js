Ext.define('Date.DateYM', {
	extend : 'Ext.panel.Panel',
	xtype:"dateym",
	year:'',
	month:'',
	datechecked:undefined,
	scope:undefined,
	constructor:function(config) {
		date_me = this;
		date_me.scope = config.scope;
		date_me.datechecked = config.datechecked;
		date_me.init();
	},
	
	init:function(){
				//点击window外
		Ext.get(document).on('click', function (evt, el){
			if(date_me.win&&el.id!='dateymid'&&el.id!='timetitle'&&el.id!='monthtitle'&&el.id!='jt'&&el.id!='btnleft'&&el.id!='btnright'){
				date_me.win.close();
			}
		});
		
		Ext.util.CSS.swapStyleSheet("theme1","/module/gz/tax/DateYm.css");
		var myDate;
		//判断是否具有默认业务日期
		if(date_me.scope.datetime==''){
			myDate = new Date();
			date_me.year = myDate.getFullYear();
			date_me.month = parseInt(myDate.getMonth())+1;
		}
		else{
			var t=date_me.scope.datetime.split(".");
			myDate=new Date(t[0],t[1]);
			date_me.year = myDate.getFullYear();
			date_me.month = myDate.getMonth();
		}
		var html = "<a id = 'dateymid' href='javascript:date_me.click();' title='报税时间'><span id='timetitle'>"+date_me.year+"</span>年 <span id='monthtitle'>"+date_me.month+"</span>月 <img id='jt' src='/workplan/image/jiantou.png' /></a>";
		date_me.picker = new Ext.Panel({
			height : 18,
			border:false,
			width:85,
            html:html
		});
		return date_me.picker;
	},
	click:function(){
		if(date_me.win)
		{
			date_me.win.close();
		}
		var string = '';
		for(var i=1;i<13;i++){
			var mon = '';
			if(i<10){
				mon = "&nbsp;"+i;
			}else{
				mon = i;
			}
			string+="<li ><a href='###' onclick='date_me.selectPeriodMonth("+i+")'>"+mon+"月</a></li>";
		};
		var ye = Ext.getDom("timetitle").innerHTML
		date_me.win=Ext.create('Ext.window.Window',
        {
	       	id:'win',
	       	header:false,
	       	resizable:false,
	       	x:Ext.get("monthtitle").getX()-115,
	       	y:Ext.get("monthtitle").getY()+20,
            width:210,
            height:115,
            html:
               "<div class='hj-wzm-clock dropdownlist' style='margin-top:0px' id='monthlist' >" +
    			"<ul style='text-align:center'>" +
    			"<span style='color:#549FE3;'>" +
    			"<a  dropdownName='monthbox' href='javascript:date_me.yearchange(-1);'><img id='btnleft' dropdownName='monthbox' src='/workplan/image/left2.gif' /></a>" +
    			"<span id='myeartitle'>"+ye+"</span>年  " +
    			"<a  dropdownName='monthbox' href='javascript:date_me.yearchange(1);'><img id='btnright' dropdownName='monthbox' src='/workplan/image/right2.gif' /></a>" +
    			"</span></ul>" +
    			"<ul id='months'>" +string+	         			
    			"</ul></div>"
        });
		date_me.win.show();
	 },
	 //选择年份
     yearchange:function(ch){
		var year = Ext.getDom('myeartitle');
		year.innerHTML = Number(year.innerHTML)+ch;
     },
	 //选择月份
     selectPeriodMonth:function(month){
		var year=Ext.getDom("myeartitle").innerHTML;
		Ext.get("timetitle").setHtml(year);
		Ext.get("monthtitle").setHtml(month);
		Ext.getCmp('win').close();
		Ext.callback(date_me.datechecked,date_me.scope,[year,month]);
     }
});