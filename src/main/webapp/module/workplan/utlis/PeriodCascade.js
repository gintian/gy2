/**
 * OKR  类型周期下拉级联
 * linbz
 */
Ext.define('PeriodCascadeUL.PeriodCascade',{
	
	constructor:function(config) {
		periodCascade_me = this;
		
		periodCascade_me.type = config.type;//计划=0，
		periodCascade_me.Periodyear = config.Periodyear;//当前年
		periodCascade_me.Periodhalf = "1";//半年
		periodCascade_me.Periodquar = "1";//季度
		periodCascade_me.Periodmonth = config.Periodmonth;//当前月
		periodCascade_me.Periodweek = config.Periodweek;//当前周
		periodCascade_me.Periodtype = config.Periodtype;//当前类型
		periodCascade_me.params = config.params;//参数
		periodCascade_me.yearlist = undefined;//年度集合
		periodCascade_me.monthslist = undefined;
		periodCascade_me.success = config.success;//回调函数
		
		periodCascade_me.initParam();
		periodCascade_me.init();
		var str = '';
	},
	/**
	 * 初始参数
	 */
	initParam:function (){
		periodCascade_me.yearlist = new Array();
		//年份规则，前4年，后2年
        for(var j=-4;j<3;j++){
            var yearmap =  new HashMap();
            var date = Ext.Date.format(Ext.Date.add(new Date(), Ext.Date.YEAR, j), "Y");
            
            yearmap.put("id", date);
            yearmap.put("info", date+"年度");
            periodCascade_me.yearlist.push(yearmap);
        }
        
        var paramlist = new Array();
        for(var p in periodCascade_me.params){
            var parammap =  new HashMap();
            var obj = periodCascade_me.params[p];
            if(obj['p0'] != undefined ){
                parammap.put("flag","1");
                parammap.put("info","年计划");
                parammap.put("values","");
                
                paramlist.push(parammap);
            }else if(obj['p1'] != undefined ){
                parammap.put("flag","2");
                parammap.put("info","半年计划");
                parammap.put("values",obj['p1'].cycle);
                
                paramlist.push(parammap);
            }else if(obj['p2'] != undefined ){
                parammap.put("flag","3");
                parammap.put("info","季度计划");
                parammap.put("values",obj['p2'].cycle);
                
                paramlist.push(parammap);
            }else if(obj['p3'] != undefined ){
                parammap.put("flag","4");
                parammap.put("info","月计划");
                parammap.put("values",obj['p3'].cycle);
                paramlist.push(parammap);
                periodCascade_me.monthslist = obj['p3'].cycle;
            }else if(obj['p4'] != undefined ){
                parammap.put("flag","5");
                parammap.put("info","周计划");
                parammap.put("values","");
                
                paramlist.push(parammap);
            }else{
                continue;
            }
        }
        periodCascade_me.paramlist = paramlist;
//      console.log(paramlist);
        
        periodCascade_me.yearbo = true;
        periodCascade_me.halfbo = true;
        periodCascade_me.quabo = true;
        periodCascade_me.monthbo = true;
        periodCascade_me.weekbo = true;
        //年1，半年2，季度3，月4，周5，
        var type = periodCascade_me.Periodtype;
        if(type=="1" || type=="2" ||type=="3" ){
        
            periodCascade_me.yearbo = false;
            if(type=="2"){
                periodCascade_me.halfbo=false;
                periodCascade_me.Periodhalf = periodCascade_me.Periodmonth;
            }
            if(type=="3"){
                periodCascade_me.quabo=false;
                periodCascade_me.Periodquar = periodCascade_me.Periodmonth;
            }
        }else if(type=="4" || type=="5" ){
        
            periodCascade_me.monthbo = false;
            if(Ext.isEmpty(periodCascade_me.Periodmonth) && !Ext.isEmpty(periodCascade_me.monthslist))periodCascade_me.Periodmonth=periodCascade_me.monthslist[0];
            if(type=="5")periodCascade_me.weekbo=false;
        }
	
	},
	// 初始化函数
	init:function() {
		
		this.periodtbar1 = Ext.create("Ext.toolbar.Toolbar", {
                    height:25,
                    autoWidth : true,
                    padding:'0 2 0 2',
                    border:false,
                    items:[
                    {
                        xtype:'label',
                        id:'typeid',
                        margin: '0 6 0 6',
                        html:"<div id = 'copyTaskTypeid'  ><a id = 'typeAid' href='javascript:periodCascade_me.dropdownDatas(\"type\",\"copyTaskTypeid\");' ><span id='typetitleid'>"
                            + periodCascade_me.selectType("type", periodCascade_me.Periodtype)
                            + "</span> <img  src='/workplan/image/jiantou.png' /></a></div>"
                    },
                    {
                        xtype:'label',
                        id:'yearlbid',
                        hidden:periodCascade_me.yearbo,
                        margin: '0 0 0 10',
                        html:"<div id = 'yearid'  ><a  href='javascript:periodCascade_me.dropdownDatas(1,\"yearid\");' ><span id='yeartitleid'>"
                            + periodCascade_me.Periodyear
                            + "年</span> <img  src='/workplan/image/jiantou.png' /></a></div>"
                    },
                    {
                        xtype:'label',
                        id:'timeid',
                        hidden:periodCascade_me.monthbo,
                        margin: '0 0 0 10',
                        html:"<a id = 'asd' href='javascript:periodCascade_me.clickTaskMonth();' ><span id='monthYtitle'>"
                            + periodCascade_me.Periodyear
                            + "</span>年<span id='monthtitle'>"
                            + periodCascade_me.Periodmonth
                            + "</span>月 <img src='/workplan/image/jiantou.png' /></a>"
                    },
                    {
                        xtype:'label',
                        id:'halflbid',
                        margin: '0 0 0 10',
                        hidden:periodCascade_me.halfbo,
                        html:"<div id = 'halfid'  ><a  href='javascript:periodCascade_me.dropdownDatas(2,\"halfid\");' ><span id='halftitleid'>"
                            + periodCascade_me.selectType("2", periodCascade_me.Periodhalf)
                            + "</span> <img  src='/workplan/image/jiantou.png' /></a></div>"
                    },
                    {
                        xtype:'label',
                        id:'quelbid',
                        hidden:periodCascade_me.quabo,
                        margin: '0 0 0 10',
                        html:"<div id = 'queid'  ><a  href='javascript:periodCascade_me.dropdownDatas(3,\"queid\");' ><span id='quetitleid'>"
                            + periodCascade_me.selectType("3", periodCascade_me.Periodquar)
                            + "</span> <img  src='/workplan/image/jiantou.png' /></a></div>"
                    },
                    {
                        xtype:'label',
                        id:'weeklbid',
                        hidden:periodCascade_me.weekbo,
                        margin: '0 10 0 10',
                        html:"<div id = 'weekid'  ><a  href='javascript:periodCascade_me.dropdownDatas(5,\"weekid\");' ><span id='weektitleid'>"
                            + periodCascade_me.selectType("5", periodCascade_me.Periodweek)
                            + "</span> <img  src='/workplan/image/jiantou.png' /></a></div>"
                    }]
                }); 
	},
	
	/**
     * 下拉数据点击事件
     * @param {} flag 年1，半年2，季度3，月4，周5 ，类型type，
     * @param {} id  数值
     */
	selectPeriodTask:function(flag, id){

		var strValue = "";
		var typeflag = "0";
		if("type" == flag){
			//若是切换类型，则给出标识
			typeflag = "1";
    		var value = periodCascade_me.selectType("type", id);
    		document.getElementById('typetitleid').innerHTML = value;
    		periodCascade_me.Periodtype = id;
    		
            if(id=="1"){
                strValue = periodCascade_me.Periodmonth;
            }
            if(id=="2"){
                strValue = periodCascade_me.Periodhalf;
            }
            if(id=="3"){
                strValue = periodCascade_me.Periodquar;
            }
        	    
            if(id=="4"){
                strValue = periodCascade_me.Periodmonth;
            }
            if(id=="5"){
                strValue = periodCascade_me.Periodmonth;
            }
		}else if("1" == flag){
		    periodCascade_me.Periodyear = id;
		    strValue = periodCascade_me.Periodmonth;
		}else if("2" == flag){
            periodCascade_me.Periodhalf = id;
            strValue = id;
        }else if("3" == flag){
            periodCascade_me.Periodquar = id;
            strValue = id;
        }else if("4" == flag){
        
        }else if("5" == flag){
            periodCascade_me.Periodweek = id;
            strValue = periodCascade_me.Periodmonth;
        }else{
        
        }
        //回调
        periodCascade_me.success(typeflag, periodCascade_me.Periodtype, periodCascade_me.Periodyear, strValue, periodCascade_me.Periodweek);
	},
	/**
	 * 下拉事件
	 * @param {} flag 年1，半年2，季度3，月4，周5 ，类型type，
	 * @param {} showid  展现的下拉窗口位置id
	 */
	dropdownDatas:function(flag, showid){

        var strlihtml1 = '<li> <a style="width:100px;line-height:28px;display:block;float:left;margin-left:20px;cursor:pointer" id="';
        var strlihtml2 = '" onclick="' + 'periodCascade_me.selectPeriodTask(' 
        var strlihtml3 = ')' + '" href="javascript:void(0)">'
        var strlihtml4 = '</a> </li>';
        
        var strhtml = "";
        var height = 0 ;
        if("2" == flag || "3" == flag || "type" == flag){
    		var list = periodCascade_me.paramlist;
    		for(var i=0;i<list.length;i++){
    			if("type" == flag){
    				var info = list[i].get("info");
    				var id = list[i].get("flag");
    				if(!Ext.isEmpty(info) && !Ext.isEmpty(id)){
    					strhtml = strhtml 
                                + strlihtml1
                                + id
                                + strlihtml2 
                                + "\'"+flag+"\',\'"+id+ "\'"
                                + strlihtml3
                                + info
                                + strlihtml4;
                        height = height + 30;
    					
    				}
    			}else if("2" == flag && list[i].get("flag")=="2"){//半年计划
    				var values = list[i].get("values");
    				if(!Ext.isEmpty(values)){
                    	var valuelist = values.split(",");
                    	for(var j=0;j<valuelist.length;j++){
                    		if(!Ext.isEmpty(valuelist[j])){
                        	   var str = (valuelist[j]=="1")?"上半年":"下半年";
                        	   strhtml = strhtml 
                                    + strlihtml1
                                    + valuelist[j]
                                    + strlihtml2 
                                    + flag+","+valuelist[j]
                                    + strlihtml3
                                    + str
                                    + strlihtml4;
                                height = height + 30;
                    		}
                    	}
                    }
    			}else if("3" == flag && list[i].get("flag")=="3"){//季度计划
    				var values = list[i].get("values");
                    if(!Ext.isEmpty(values)){
                        var valuelist = values.split(",");
                        for(var j=0;j<valuelist.length;j++){
                            if(!Ext.isEmpty(valuelist[j])){
                               var str = "";
                               if(valuelist[j]=="1")str="第一季度";
                               if(valuelist[j]=="2")str="第二季度";
                               if(valuelist[j]=="3")str="第三季度";
                               if(valuelist[j]=="4")str="第四季度";
                               if(str=='')continue;
                               strhtml = strhtml 
                                    + strlihtml1
                                    + valuelist[j]
                                    + strlihtml2 
                                    + flag+","+valuelist[j]
                                    + strlihtml3
                                    + str
                                    + strlihtml4;
                                height = height + 30;
                            }
                        }
                    }
    			}
    		}
		}
		
		if("1" == flag){
            for(var i=0;i<periodCascade_me.yearlist.length;i++){
                var parammap =  new HashMap();
                var id = periodCascade_me.yearlist[i].get("id");
                var info = periodCascade_me.yearlist[i].get("info");
                strhtml = strhtml 
                        + strlihtml1
                        + id
                        + strlihtml2 
                        + "\'"+flag+"\',\'"+id+ "\'"
                        + strlihtml3
                        + info
                        + strlihtml4;
                height = height + 30;
            }
      }else if("5"==flag){
      	    var weeknum =  periodCascade_me.getWeeks(periodCascade_me.Periodyear, periodCascade_me.Periodmonth);
      	    for(var i=1;i<weeknum+1;i++){
      	    	var str = "";
      	    	if(i==1)str="一";
      	    	if(i==2)str="二";
      	    	if(i==3)str="三";
      	    	if(i==4)str="四";
      	    	if(i==5)str="五";
      	    	if(str=='')continue;
      	    	strhtml = strhtml 
                        + strlihtml1
                        + i
                        + strlihtml2 
                        + flag+","+i
                        + strlihtml3
                        + "第"+str+"周"
                        + strlihtml4;
                height = height + 30;
      	    }
      }
      if(strhtml.length>0){
            strhtml = "<ul >" + strhtml + "</ul>"
            periodCascade_me.clickDataTask(strhtml, height, showid);
       }
//            console.log(strhtml);
	},
	
	
    clickDataTask : function (strhtml, heightvalue, showid){

        var win = Ext.getCmp('win');
        if(win)
            win.close();
        
        win = Ext.create('Ext.window.Window', {
            id : 'win',
            header : false,
            resizable : false,
            x : Ext.get(showid).getX() - 10,
            y : Ext.get(showid).getY() + 20,
            width : 100,
            height : heightvalue,
            html : "<div >"
                    + strhtml+"</div>"
        });
        win.show();
        
        // 点击window外
        Ext.getBody().addListener('click', function(evt, el) {
            if (!win.hidden && showid != el.id )//&& "yearTitleid" != el.id
                win.close();
        });
    },
   /**
    * 获取该月份有几周（从第一个周一起）
    * @param {} year
    * @param {} month
    * @return {}
    */
   getWeeks : function(year,month){
		var weeknum=4;
		var map = new HashMap();
		map.put("periodYear",year)
		map.put("periodMonth",month)
		map.put("oprType","getWeekNum")
		Rpc({functionId:'9028000702',async:false,success:function(form){
			 var result = Ext.decode(form.responseText); 
			 weeknum = result.weeknum;
		}},map);
		return weeknum;
	},
    selectPeriodMonth : function(month){
        
    	var year=Ext.getDom("myeartitle").innerHTML;
    	periodCascade_me.Periodmonth = month;
    
    	periodCascade_me.success("0", periodCascade_me.Periodtype, periodCascade_me.Periodyear, periodCascade_me.Periodmonth,periodCascade_me.Periodweek);
    },
    //  月份下拉
    clickTaskMonth : function (){
        var string = '';
        for(var i=1;i<13;i++){
            var mon = '';
            if(i<10){
                mon = "&nbsp;"+i;
            }else{
                mon = i;
            }
            if((","+periodCascade_me.monthslist+",").indexOf(','+i+",") == -1)
                continue;
            if(periodCascade_me.Periodmonth==i){
                string+="<li ><a href='###' onclick='periodCascade_me.selectPeriodMonth("+i+")' style='background-color:rgb(84, 159, 227)'>"+mon+"月</a></li>";
            }else{
                string+="<li ><a href='###' onclick='periodCascade_me.selectPeriodMonth("+i+")'>"+mon+"月</a></li>";
            }
        }
        
        var win = Ext.getCmp('win');
        if(win)
            win.close();
    
        win = Ext.create('Ext.window.Window', {
            id : 'win',
            header : false,
            resizable : false,
            x : Ext.get("asd").getX() - 65,
            y : Ext.get("asd").getY() + 20,
            width : 204,
            height : 115,
            padding:0,
            html : "<div class='hj-wzm-clock dropdownlist' style='top:0px;left:0px;padding:0 0 0 0;border: 0px;text-align:center;'  id='monthlist'  >"
                + "<ul style='text-align:center'>"
                + "<span style='color:#549FE3;'>"
                + "<a dropdownName='monthbox' href='javascript:periodCascade_me.cyearchange(-1);'><img dropdownName='monthbox' id='changeYear' src='/workplan/image/left2.gif' /></a>"
                + " <span id='yeartitles'>"
                + periodCascade_me.Periodyear
                + "</span>年  "
                + "<a  dropdownName='monthbox' href='javascript:periodCascade_me.cyearchange(1);'><img dropdownName='monthbox' id='yearChange' src='/workplan/image/right2.gif' /></a>"
                + "</span></ul>"
                + "<ul id='months'>"
                + string
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
    cyearchange : function (ch){
         var year = Ext.getDom('yeartitles');
         var yearvalue = Number(year.innerHTML)+ch;
         year.innerHTML = yearvalue;
         periodCascade_me.Periodyear = yearvalue; 
    },
    
	selectType : function (flag, id){
		
		if(Ext.isEmpty(id) || null==id || "null"==id){
		      id = "1";
		}
        var value = "";
        if("type" == flag){
            if(id=="1")value = "年计划";
            if(id=="2")value = "半年计划";
            if(id=="3")value = "季度计划";
            if(id=="4")value = "月计划";
            if(id=="5")value = "周计划";
        }else if("1" == flag){
            value = id + "年";
        }else if("2" == flag){
            if(id=="1")value = "上半年";
            if(id=="2")value = "下半年";
        }else if("3" == flag){
        	if(id=="1")value = "第一季度";
            if(id=="2")value = "第二季度";
            if(id=="3")value = "第三季度";
            if(id=="4")value = "第四季度";
        }else if("4" == flag){
        
        }else if("5" == flag){
        	value = "第一周";
            if(id=="2")value = "第二周";
            if(id=="3")value = "第三周";
            if(id=="4")value = "第四周";
            if(id=="5")value = "第五周";
        }else{
        
        }
        
        return value;
    },
    
    getSelector:function(){
        return this.periodtbar1;
    },
    
    getDropDownWin:function(){
    	return Ext.getCmp("win");
    }
	
});