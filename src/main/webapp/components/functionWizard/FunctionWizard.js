/**
*	调用方法：		
			Ext.require('EHR.functionWizard.FunctionWizard',function(){
				Ext.create("EHR.functionWizard.FunctionWizard",{keyid:id,opt:"1",mode:'xzgl_jsgs',callbackfunc:'thisScope.getfunctionWizard'});
			})
*	参数说明：keyid:主键标识  薪资模块为薪资类别号
			opt: 1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
			mode:“xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
			callbackfunc：回调函数，按“完成”按钮把具体内容返还给调用页面并触发一个自定义的方法
**/
Ext.define('EHR.functionWizard.FunctionWizard',{
	   requires:["EHR.extWidget.proxy.TransactionProxy"],
       constructor:function(config){
	       	functionWizardScope = this;
	       	functionWizardScope.keyid = config.keyid; 
	       	functionWizardScope.opt = config.opt;
	       	functionWizardScope.mode = config.mode;
	       	functionWizardScope.callbackfunc = config.callbackfunc;
	       	functionWizardScope.type = config.type;
	       	functionWizardScope.inforType = config.inforType;
	       	functionWizardScope.id = "";
	       	functionWizardScope.node = "";
	       	functionWizardScope.win1 = "";
	       	functionWizardScope.win2 = "";
	       	functionWizardScope.init();

       },
       init:function(){
             var treeStore = Ext.create('Ext.data.TreeStore', {
				proxy:{
			    	type: 'transaction',
			        functionId:'ZJ100000101',
			        extraParams:{
			        		opt:functionWizardScope.opt,//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			        		mode:functionWizardScope.mode,//“xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
			        		inforType:functionWizardScope.inforType,//人员1、单位2、岗位3
			        		type:functionWizardScope.type//临时变量 type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
			        },
			        reader: {
			            type: 'json',
			            root: 'data'         	
			        }
				},
				root: {
					// 根节点的文本
					id:'root',				
					text:'函数',
					expanded: true,
					icon:'/images/ac.gif'
				}
			});
			var tree = Ext.create('Ext.tree.Panel', {
				// 不使用Vista风格的箭头代表节点的展开/折叠状态
				useArrows: false,
				//id:'treePanel',
				height:300,
				width:330,
				store: treeStore, // 指定该树所使用的TreeStore
				rootVisible: true, // 指定根节点可见
				listeners:{
        			'itemclick':function(view,record,item,index){
        				functionWizardScope.node = record.get("id");
						functionWizardScope.setHtml(record.get("id"));
						var height = Ext.getCmp('explain').getHeight();
						functionWizardScope.win1.setHeight(350+height);
        			}
        		}
			});
			var buttons = Ext.create('Ext.panel.Panel',{
   				layout:'column',
	        	border:false,
	        	columnWidth:1,
	        	width:50,
	        	items:[{
	        		xtype:'button',
	        		columnWidth:1,
	        		text:'下步',
	        		id:'next',
	        		disabled:true,
	        		//style:'margin-top:20px',
	        		listeners:{
	        			'click':function(view,record,item,index){
							functionWizardScope.nextStep();
							var height = Ext.getCmp('explain').getHeight();//取部门值说明过长，导致不显示问题
							functionWizardScope.win2.setHeight(350+height);
	        			}
	        		}
	        	},{
	        		xtype:'button',
	        		columnWidth:1,
	        		style:'margin-top:20px',
	        		text:'上步',
	        		id:'up',
	        		disabled:true,
	        		listeners:{
	        			'click':function(){
							
	        			}
	        		}
	        	},{
	        		xtype:'button',
	        		columnWidth:1,
	        		style:'margin-top:20px',
	        		text:'完成',
	        		id:'okok',
	        		disabled:true,
	        		listeners:{
	        			'click':function(){
							functionWizardScope.completed(functionWizardScope.callbackfunc);
	        			}
	        		}
	        	}]
			});
			functionWizardScope.win1 = Ext.widget("window",{
		          title:'请选择函数，按【下步】设置函数需要的参数',  
		          height:500,  
		          width:400,
		          layout:'fit',
				  modal:true,
				  closeAction:'destroy',
				  resizable:false,
				  items: [{
					  	xtype:'panel',
					  	layout:'border',
					  	bodyStyle: 'background:#ffffff;',
			         	border:false,
			         	items:[
				            { region: "center",border:false,items:tree},
				            { region: "east",border:false,items:buttons},
				            { region: "south",xtype:'box',id:'explain',border:false,minHeight:150,html:''}
			            ]
		          }]
		    });
		    functionWizardScope.win1.show();
       },
       setHtml:function(id){
			switch (id){
				case "0":
					Ext.getCmp("explain").setHtml("说明：数值函数");
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "N_num0":
					Ext.getCmp("explain").setHtml("说明：INT(数值表达式)<br>Int(129.11) <br>返回结果为：129");
					functionWizardScope.value = "取整";
					functionWizardScope.id = id;
					functionWizardScope.explained="取整(数值表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "N_num1_4":
					Ext.getCmp("explain").setHtml("说明：FUNCMOD(数值表达式,数值表达式)<br>取余数(10, 3) <br>返回结果为: 1");
					functionWizardScope.value = "取余数";
					functionWizardScope.id = id;
					functionWizardScope.explained="取余数(数值表达式,数值表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "N_num2_2":
					Ext.getCmp("explain").setHtml("说明：ROUND(数值表达式,保留小数点位数)<br>Round(129.166,2) <br>返回结果为：129.17");
					functionWizardScope.value = "四舍五入";
					functionWizardScope.id = id;
					functionWizardScope.explained="四舍五入(数值表达式,保留小数点位数)";
					functionWizardScope.decimalname="整数";
					functionWizardScope.haveButtons(id);
					break;
				case "N_num3":
					Ext.getCmp("explain").setHtml("说明：SANQI;三舍七入,其他为0.5<br>ANQI(129.34) <br>返回结果为：129.5");
					functionWizardScope.value = "三舍七入";
					functionWizardScope.id = id;
					functionWizardScope.explained="三舍七入(数值表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "N_num4_2":
					Ext.getCmp("explain").setHtml("说明：YUAN(数值表达式,[进元参数]),进元参数可选,默认为1<br>YUAN(129.01) <br>返回结果为：130<br>YUAN(129.09, 10) <br>返回结果为：129<br>YUAN(129.10, 10) <br>返回结果为：130");
					functionWizardScope.value = "逢分进元";
					functionWizardScope.id = id;
					functionWizardScope.decimalname = "进元参数(分)";
					functionWizardScope.explained="逢分进元(数值表达式,[进元参数])";
					functionWizardScope.haveButtons(id);
					break;
				case "N_num5":
					Ext.getCmp("explain").setHtml("说明：JIAO(129.11) \n返回结果为：129.2<br>JIAO(129.11) <br>返回结果为：129.2");
					functionWizardScope.value = "逢分进角";
					functionWizardScope.id = id;
					functionWizardScope.explained="逢分进角(数值表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "NN_num6":
					Ext.getCmp("explain").setHtml("说明：求底数的n次方<br>幂(3,2) <br>返回结果为：9<br>注意: 在进行N次方根的计算时,第二个参数可用小数或分数(1/n),在用分数时,应使用类似1.0/n的格式;<br>小数或者分数的N次方根据需要保留精度，例：保留两位小数 幂(3.02,2);小数或者分数次方根时 幂(4,2.00/3)") ;
					functionWizardScope.value = "幂(,)";
					functionWizardScope.id = id;
					functionWizardScope.explained="幂(数值表达式,数值表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "1":
					Ext.getCmp("explain").setHtml("说明：字符串函数");
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "A_str0":
					Ext.getCmp("explain").setHtml('说明：Trim(字符串表达式)<br>Trim("&nbsp;&nbsp;AbcD&nbsp;&nbsp;") <br>返回结果为："AbcD"');
					functionWizardScope.value = "去空格";
					functionWizardScope.id = id;
					functionWizardScope.explained="去空格(字符串表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "A_str1":
					Ext.getCmp("explain").setHtml('说明：LTRIM(字符串表达式)<br>LTrim("&nbsp;&nbsp;AbcD&nbsp;&nbsp;") <br>返回结果为："AbcD&nbsp;&nbsp;"');
					functionWizardScope.value = "去左空格";
					functionWizardScope.id = id;
					functionWizardScope.explained="去左空格(字符串表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "A_str2":
					Ext.getCmp("explain").setHtml('说明：RTRIM(字符串表达式)<br>RTrim("&nbsp;&nbsp;AbcD&nbsp;&nbsp;") <br>返回结果为："&nbsp;&nbsp;AbcD"');
					functionWizardScope.value = "去右空格";
					functionWizardScope.id = id;
					functionWizardScope.explained="去右空格(字符串表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "A_str3_2_2":
					Ext.getCmp("explain").setHtml('说明：SUBSTR(字符串表达式,起始位,长度)<br>SubStr("AykchrbcD",2,5) <br>返回结果为："ykchr"');
					functionWizardScope.value = "子串";
					functionWizardScope.id = id;
					functionWizardScope.decimalname = "起始位";
					functionWizardScope.explained="子串(字符串表达式,整数,整数)";
					functionWizardScope.haveButtons(id);
					break;
				case "A_str4":
					Ext.getCmp("explain").setHtml('说明：LEN(字符串表达式)<br>LEN("ykchrbcD") <br>返回结果为：8');
					functionWizardScope.value = "串长";
					functionWizardScope.id = id;
					functionWizardScope.explained="串长(字符串表达式)";
					functionWizardScope.haveButtons(id);
					break;
				case "A_str5_2":
					Ext.getCmp("explain").setHtml('说明：LEFT(字符串表达式,长度)<br>Left("ykchrbcD",5) <br>返回结果为："ykchr"');
					functionWizardScope.value = "左串";
					functionWizardScope.id = id;
					functionWizardScope.explained="左串(字符串表达式,长度)";
					functionWizardScope.haveButtons(id);
					break;
				case "A_str6_2":
					Ext.getCmp("explain").setHtml('说明：RIGHT(字符串表达式,长度)<br>Right("Abcykchr",5) <br>返回结果为："ykchr"');
					functionWizardScope.value = "右串";
					functionWizardScope.id = id;
					functionWizardScope.explained="右串(字符串表达式,长度)";
					functionWizardScope.haveButtons(id);
					break;
				case "V_vols7_2":
					Ext.getCmp("explain").setHtml('说明：求登录用户名1(简写)|2(全称)<br>参数只能是1或者2，<br>1表示返回登录用户名，<br>2表示返回登录用户全名');
					functionWizardScope.value = "登录用户名";
					functionWizardScope.id = id;
					functionWizardScope.explained="登录用户名(1|2)";
					functionWizardScope.decimalname = "参数";
					functionWizardScope.haveButtons(id);
					break;
				case "V_vols8_9":
					Ext.getCmp("explain").setHtml('当前登录用户的操作单位,一般直接用于IN操作符后, 如:<br>如果 所在单位 IN 本单位() 那么 ...<br>参数是业务类别，可以是1-3:<br>0:默认，系统操作单位<br>1:工资发放<br>2:工资总额<br>3:所得税');
					functionWizardScope.value = "本单位";
					functionWizardScope.id = id;
					functionWizardScope.explained="";
					functionWizardScope.haveButtons(id);
					break;
				case "V_vols8_10":
					Ext.getCmp("explain").setHtml('说明：<br>&nbsp;&nbsp;取兼职信息()：不显示兼职单位、部门信息<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;主任(兼职)，党委书记(-)<br>&nbsp;&nbsp;取兼职信息(单位部门):显示兼职单位、部门信息，<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;北京分公司\分公司领导\主任(兼职)，集团总部\党委书记(-)<br>&nbsp;&nbsp;取兼职信息(单位):显示兼职单位信息<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;北京分公司\主任(兼职)，集团总部\党委书记(-)<br>&nbsp;&nbsp;取兼职信息(部门):显示兼职部门信息<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;分公司领导\主任(兼职)，党委书记(-)');
					functionWizardScope.value = "取兼职信息";
					functionWizardScope.id = id;
					functionWizardScope.explained="";
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol9_3":
					Ext.getCmp("explain").setHtml('说明：<br>&nbsp;&nbsp;上一级代码(指标名称,[代码类])：如代码类不填写，则默认使用指标名称关联的代码类<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;上一级代码(部门):取当前部门的上一级机构代码 <br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;上一级代码(部门,"UM"):取当前部门的上一级机构代码');
					functionWizardScope.value = "上一级代码";
					functionWizardScope.id = id;
					functionWizardScope.explained="";
					functionWizardScope.conditionsdesc = "代码类";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.haveButtons(id); 
					break;
				case "2":
					Ext.getCmp("explain").setHtml('说明：日期函数 ');
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "D_data0":
					Ext.getCmp("explain").setHtml('说明：YEAR(日期)：取日期的年<br>Year(#1992.10.10#) <br>返回结果为：1992');
					functionWizardScope.value = "年";
					functionWizardScope.id = id;
					functionWizardScope.explained="年(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.haveButtons(id);
					break;
				case "D_data1":
					Ext.getCmp("explain").setHtml('说明：MONTH(日期)：取日期的月<br>Month(#1992.10.13#) <br>返回结果为：10');
					functionWizardScope.value = "月";
					functionWizardScope.id = id;
					functionWizardScope.explained="月(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.haveButtons(id);
					break;
				case "D_data2":
					Ext.getCmp("explain").setHtml('说明：DAY(日期)：取日期的日<br>Day(#1992.10.11#) <br>返回结果为：11');
					functionWizardScope.value = "日";
					functionWizardScope.id = id;
					functionWizardScope.explained="日(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.haveButtons(id);
					break;
				case "D_data3":
					Ext.getCmp("explain").setHtml('说明：QUARTER(日期)：取日期的季度<br>QUARTER(#1992.10.11#) <br>返回结果为：4');
					functionWizardScope.value = "季度";
					functionWizardScope.id = id;
					functionWizardScope.explained="季度(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.haveButtons(id);
					break;
				case "D_data4":
					Ext.getCmp("explain").setHtml('说明：WEEK(日期)：该日期为本年的第几周<br>Week(#1992.1.9#) <br>返回结果为：2');
					functionWizardScope.value = "周";
					functionWizardScope.id = id;
					functionWizardScope.explained="周(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.haveButtons(id);
					break;
				case "D_data5":
					Ext.getCmp("explain").setHtml('说明：WEEKDAY(日期)：该日期为星期几<br>WEEKDAY(#1992.1.9#) <br>返回结果为：4');
					functionWizardScope.value = "星期";
					functionWizardScope.id = id;
					functionWizardScope.explained="星期(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data6":
					Ext.getCmp("explain").setHtml('说明：TODAY 或 TODAY()<br>Today <br>返回结果为：#2007.06.26#');
					functionWizardScope.value = "今天";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data7":
					Ext.getCmp("explain").setHtml('说明：TOWEEK<br>TOWEEK <br>返回结果为：当前系统时间的周数');
					functionWizardScope.value = "本周";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data8":
					Ext.getCmp("explain").setHtml('说明：TOMONTH<br>TOMONTH <br>返回结果为：当前系统时间的月份');
					functionWizardScope.value = "本月()";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data9":
					Ext.getCmp("explain").setHtml('说明：TOQUARTER<br>TOQUARTER <br>返回结果为：当前系统时间的季度数');
					functionWizardScope.value = "本季度()";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data10":
					Ext.getCmp("explain").setHtml('说明：TOYEAR<br>TOYEAR <br>返回结果为：当前系统时间的年份');
					functionWizardScope.value = "今年()";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data11":
					Ext.getCmp("explain").setHtml('说明：APPDATE<br>APPDATE() <br>返回结果为：用户设置的计算截止日期');
					functionWizardScope.value = "截止日期()";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data12":
					Ext.getCmp("explain").setHtml('说明：AGE(日期)：计算到日的年龄<br>Age(#1992.7.12#) <br>返回结果为：计算到日的年龄');
					functionWizardScope.value = "年龄";
					functionWizardScope.explained="年龄(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data13":
					Ext.getCmp("explain").setHtml('说明：WORKAGE(日期)：年份相减加1<br>WorkAge(#1992.7.12#) <br>返回结果为：年份相减加1');
					functionWizardScope.value = "工龄";
					functionWizardScope.explained="工龄(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data14":
					Ext.getCmp("explain").setHtml('说明：WMONTHAGE(日期)：计算到月的年龄<br>WMONTHAGE(#1992.7.12#) <br>返回结果为：计算到月的年龄');
					functionWizardScope.value = "到月年龄";
					functionWizardScope.explained="到月年龄(日期)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data15_1":
					Ext.getCmp("explain").setHtml('说明：YEARS(日期1,日期2)：从日期2到日期1的年数<br>Years(#2002.10.10#,#1992.7.12#) <br>返回结果为：两日期之间年数');
					functionWizardScope.value = "年数";
					functionWizardScope.explained="年数(日期1,日期2)";
					functionWizardScope.datesubset="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
					functionWizardScope.datesubset1="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data16_1":
					Ext.getCmp("explain").setHtml('说明：MONTHS(日期1,日期2)：从日期2到日期1的月数<br>Months(#2002.10.10#,#1992.7.12#) <br>返回结果为：两日期之间月数');
					functionWizardScope.value = "月数";
					functionWizardScope.explained="月数(日期1,日期2)";
					functionWizardScope.datesubset="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
					functionWizardScope.datesubset1="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data17_1":
					Ext.getCmp("explain").setHtml('说明：DAYS(日期1,日期2)：从日期2到日期1的天数<br>Days(#2002.10.10#,#1992.7.12#) <br>返回结果为：两日期之间天数');
					functionWizardScope.value = "天数";
					functionWizardScope.explained="天数(日期1,日期2)";
					functionWizardScope.datesubset="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
					functionWizardScope.datesubset1="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data18_1":
					Ext.getCmp("explain").setHtml('说明：QUARTERS(日期1,日期2)：从日期1到日期2的季度数<br>QUARTERs(#1992.7.12#,#2002.10.10#) <br>返回结果为：两日期之间季度数');
					functionWizardScope.value = "季度数";
					functionWizardScope.explained="季度数(日期1,日期2)";
					functionWizardScope.datesubset="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
					functionWizardScope.datesubset1="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data19_1":
					Ext.getCmp("explain").setHtml('说明：WEEKS(日期1,日期2)：从日期1到日期2的周数<br>Weeks(#1992.7.12#,#2002.10.10#) <br>返回结果为：两日期之间周数');
					functionWizardScope.value = "周数";
					functionWizardScope.explained="周数(日期1,日期2)";
					functionWizardScope.datesubset="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
					functionWizardScope.datesubset1="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data20_2":
					Ext.getCmp("explain").setHtml('说明：AddYear(日期,整数)<br>AddYear(#1992.7.12#,2) <br>返回结果为：日期');
					functionWizardScope.value = "增加年数";
					functionWizardScope.explained="增加年数(日期,整数)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.decimalname="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data21_2":
					Ext.getCmp("explain").setHtml('说明：AddMonth(日期,整数)<br>AddMonth(#1992.7.12#,21) <br>返回结果为：日期');
					functionWizardScope.value = "增加月数";
					functionWizardScope.explained="增加月数(日期,整数)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.decimalname="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data22_2":
					Ext.getCmp("explain").setHtml('说明：AddDay(日期,整数)<br>AddDay(#1992.7.12#,200) <br>返回结果为：日期');
					functionWizardScope.value = "增加天数";
					functionWizardScope.explained="增加天数(日期,整数)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.decimalname="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data23_2":
					Ext.getCmp("explain").setHtml('说明：AddQUARTER(日期,整数)<br>AddQUARTER(#1992.7.12#,200) <br>返回结果为：日期');
					functionWizardScope.value = "增加季度数";
					functionWizardScope.explained="增加季度数(日期,整数)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.decimalname="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data24_2":
					Ext.getCmp("explain").setHtml('说明：AddWeek(日期,整数)<br>AddWeek(#1992.7.12#,12) <br>返回结果为：日期');
					functionWizardScope.value = "增加周数";
					functionWizardScope.explained="增加周数(日期,整数)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.decimalname="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "DD_data25":
					Ext.getCmp("explain").setHtml("返回结果为：归属日期(['A00Z0'|'A00Z2'])<br>如果设置了参数 A00Z0归属日期 或 A00Z2发放日期，则取薪资账套中第一条记录的归属日期或者发放日期的值，否则取当前薪资发放的业务日期<br>说明：归属日期只能用于薪资发放和保险缴费核算业务中");
					functionWizardScope.value = "归属日期()";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data26_1_7":
					var expstr = "返回指定日期范围内的工作日天数.<br>参数说明:<br>";
					expstr+="日期参数: 日期型指标、日期数值、截止日期和归属日期<br>";
					expstr+='节假日标志: 可选参数，"含节假日"或"不含节假日"(不';
					expstr+='含引号)。默认为"不含节假日"。';
					expstr+="含节假日表示节假日为工作日。";
					expstr+="公休日、节假日及倒休在考勤模块中设置";
					Ext.getCmp("explain").setHtml('说明：WorkDays(日期1,日期2,节假日标志)<br>'+expstr);
					functionWizardScope.value = "工作日";
					functionWizardScope.explained="工作日(日期,日期[,含节假日|不含节假日])";
					functionWizardScope.datesubset="日期1";
					functionWizardScope.datesubset1="日期2";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data27_3":
					var strtext = "子集中符合条件的日期指标的月份数<br>";
					strtext+="第一个参数必须为子集的日期指标，第二个参数为条件<br>";
					strtext+="返回指标所在子集中,符合条件的所有日期值的年月份个数";
					Ext.getCmp("explain").setHtml('说明：GroupMonths(日期指标,条件表达式)<br>'+strtext);
					functionWizardScope.value = "统计月数";
					functionWizardScope.explained="统计月数(日期指标,条件表达式)";
					functionWizardScope.datesubset="日期指标  ";
					functionWizardScope.conditionsdesc="条件";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "D_data28_1_10_10":
					var expstr = "参数说明:<br>";
					expstr+="（1）表达式中只能用这两个时间或其它常量<br>（2）开始时间和结束时间有重复，则只算一次<br>例如：统计时间(任职时间,免职时间,职级=\"03\",月数(如果 为空(免职时间) 那么 今天 否则 免职时间 结束,任职时间))<br>返回值：任03职级的月数，如果同时兼职，相同月数只算一次";
					Ext.getCmp("explain").setHtml('说明：统计时间(开始时间, 结束时间, 条件, 表达式())<br>'+expstr);
					functionWizardScope.value = "统计时间";
					functionWizardScope.explained="统计时间(开始时间, 结束时间, 条件, 表达式())";
					functionWizardScope.datesubset="开始日期";
					functionWizardScope.datesubset1="结束日期";
					functionWizardScope.conditionssubset="表&nbsp;达&nbsp;式";
					functionWizardScope.conditionsdesc="条件";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "3":
					Ext.getCmp("explain").setHtml('说明：转换函数');
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "T_str0_22":
					Ext.getCmp("explain").setHtml('说明：CTOD(字符串表达式)<br>CTOD("1992.7.2"，"YYYY-MM-DD") <br>返回结果为：日期');
					functionWizardScope.value = "字符转日期";
					functionWizardScope.explained="字符转换日期(字符串表达式，格式)";
					functionWizardScope.strsubset="字符串表达式";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_str1":
					Ext.getCmp("explain").setHtml('说明：CTOI(字符串表达式)<br>CTOI("12922.01") <br>返回结果为：12922.01');
					functionWizardScope.value = "字符转数值";
					functionWizardScope.explained="字符转换数值(字符串表达式)";
					functionWizardScope.strsubset="字符串表达式";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_data2":
					Ext.getCmp("explain").setHtml('说明：DTOC(日期，格式)<br>DTOC(#1991.10.10#，"YYYY-MM-DD") <br>返回结果为："1991.10.10"');
					functionWizardScope.value = "日期转字符";
					functionWizardScope.explained="日期转换字符(日期，格式)";
					functionWizardScope.datesubset="日期表达式  ";
					functionWizardScope.datesubsettype="格式  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_num3":
					Ext.getCmp("explain").setHtml('说明：ITOC(数值表达式)<br>ITOC(129.02) <br>返回结果为："129.02"');
					functionWizardScope.value = "数值转字符";
					functionWizardScope.explained="数值转换字符(数值表达式)";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_vol7_2":
					Ext.getCmp("explain").setHtml('说明：NumConversion(指标名称,1|2|3)<br>“编号”指标值为“第0003号”<br>数字转汉字(编号,1)<br>返回结果为：第○○○三号<br>数字转汉字(编号,2)<br>返回结果为：第零零零叁号<br>“基本工资”指标值为“1234.56”<br>数字转汉字(编号,3)<br>返回结果为：壹仟贰佰叁拾肆元伍角陆分');
					functionWizardScope.value = "数字转汉字";
					functionWizardScope.explained="数字转汉字(指标,参数)";
					functionWizardScope.decimalname="参&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_code4":
					Ext.getCmp("explain").setHtml('说明：CTON(指标名称)<br>CTON(性别) <br>返回结果为：字符');
					functionWizardScope.value = "代码转名称";
					functionWizardScope.explained="代码转名称(指标名称)";
					functionWizardScope.strsubset="指标名称";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_num4_2":
					Ext.getCmp("explain").setHtml('说明：NUMTOCODE(指标名称,长度)<br>返回结果为：指标整数部分，不足指定长度前面补0');
					functionWizardScope.value = "数值转代码";
					functionWizardScope.explained="数值转代码(指标名称, 4)";
					functionWizardScope.numtitle="指标名称";
					functionWizardScope.decimalname="长度";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "TT_tra5":
					Ext.getCmp("explain").setHtml('说明：代码转换符号');
					functionWizardScope.value = "~";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "T_item6_5":
					Ext.getCmp("explain").setHtml('说明：CTON2(表达式,代码类,[,显示层级,分隔符])<br>CTON2(性别,"AX") <br>如果性别为"1"<br>&nbsp;&nbsp;返回结果为：男<br>如果性别为"2"<br>&nbsp;&nbsp;返回结果为：女<br><br>CTON2(所学专业, "AI", 3, ".")<br>&nbsp;&nbsp;返回结果为: 经济学.经济学类.统计学');
					functionWizardScope.value = "代码转名称2";
					functionWizardScope.explained="代码转名称2(表达式,代码类[,显示层级,分隔符])";
					functionWizardScope.itemidname="表达式";
					functionWizardScope.codeMaxname="代码类";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "4":
					Ext.getCmp("explain").setHtml('说明：类型不定函数');
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "VV_vol0":
					Ext.getCmp("explain").setHtml('说明：IIF<Lexp1> THEN<exp1> ELSE<exp1> END<br>如果 学历="01" 那么 100 <br>否则 200 <br>结束');
					functionWizardScope.value = "如果 <Lexp1> 那么 <exp1> \n否则 <exp1>  \n结束";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "VV_vol1":
					Ext.getCmp("explain").setHtml('说明：CASEIIF Lexp1 THEN exp1 IIF Lexp2 THEN exp2[ELSE expn]结束<br>分情况<br>如果 学历="01" 那么 100 <br>如果 学历="02" 那么 200 <br>如果 学历="03" 那么 300 <br>否则 400 <br>结束');
					functionWizardScope.value = "分情况 \n如果 Lexp1 那么 exp1 \n如果 Lexp2 那么 exp2 \n否则 expn... \n结束";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol2_6":
					Ext.getCmp("explain").setHtml('说明：GETMAX(exp1,exp2)<br>GetMax(192,22) <br>值为192');
					functionWizardScope.value = "较大值";
					functionWizardScope.explained = "较大值(exp1,exp2)";
					functionWizardScope.datastrcssaname = "表 达 式1  ";
					functionWizardScope.datastrcss1name = "表 达 式2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol3_6":
					Ext.getCmp("explain").setHtml('说明：GETMIN(exp1,exp2)<br>GetMin(192,22) <br>值为22');
					functionWizardScope.value = "较小值";
					functionWizardScope.explained = "较小值(exp1,exp2)";
					functionWizardScope.datastrcssaname = "表 达 式1  ";
					functionWizardScope.datastrcss1name = "表 达 式2  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol4_2_1":
					Ext.getCmp("explain").setHtml('说明：GET(指标名称，整数，方向)<br>取 学历 最近第 1 条记录');
					functionWizardScope.value = "取";
					functionWizardScope.explained = "取 指标名称 [最近第|最初第] 整数 条记录";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.decimalname = "整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vsub7_3_3":
					Ext.getCmp("explain").setHtml('说明：<br>统计表单子集 保险发放的 失业保险个人缴费 满足 月(A94z0)=月(归属日期) 且 年(A94z0)=年(归属日期) 的总和');
					functionWizardScope.value = "统计表单子集";
					functionWizardScope.explained = "统计 四则运算表达式 满足 条件 [的总和..";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.conditionsdesc="条件";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol5_3_3":
					Ext.getCmp("explain").setHtml('说明：SELECT(四则运算表达式,条件,方式)<br>统计 失业保险个人缴费+失业保险个人补缴  满足 月(A94z0)=月(归属日期) 且 年(A94z0)=年(归属日期) 的最近第一条记录');
					functionWizardScope.value = "统计";
					functionWizardScope.explained = "统计 四则运算表达式 满足 条件 [第一条记录..";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.conditionsdesc="条件";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol6":
					Ext.getCmp("explain").setHtml('说明：指标 IS NULL<br>例子: 为空(独生子女费)<br>独生子女费 IS NULL');
					functionWizardScope.value = "为空";
					functionWizardScope.explained = "为空(指标)";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol1_8_9_1_1":
					var exprestr = "返回对应的结果值。<br>部分参数说明:<br>分组指标: ";
					exprestr+="代码型指标<br>分组级数：需要分组统计到的代码层级. 默认为0，";
					exprestr+="表示直接按代码值分组范围：可设为当前列表、当前人员库。默认为当前列表";
					Ext.getCmp("explain").setHtml('说明：分组汇总(汇总指标,汇总方式,分组指标[,分组级数[,当前列表|当前人员库[,条件]]])<br>'+exprestr);
					functionWizardScope.value = "分组汇总";
					functionWizardScope.explained = "分组汇总(汇总指标,个数|平均数|总和|最大值|最小值,分组指标[,分组级数[,当前列表|当前人员库[,条件]]])";
					functionWizardScope.datastrcssaname = "汇总指标";
					functionWizardScope.itemidname = "分组指标";
					functionWizardScope.condname = "方&nbsp;&nbsp;&nbsp;&nbsp;式";
					functionWizardScope.rangeidname = "范&nbsp;&nbsp;&nbsp;&nbsp;围";
					functionWizardScope.conditionsdesc = "条件";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol9":
					var exprestr = "在公式中使用单位子集指标时, 一般是取得人员所在单位的指标<br>有时, 需要取人员所在部门的指标, 此时可用本函数<br><br>";
					exprestr+="注意: 在一个公式中, 不能既取单位的值,又取部门的值. 当一个公式使用了\"取部门值\"时, 所有单位子集指标都是取对应部门的值 ";
					Ext.getCmp("explain").setHtml('说明：GetE0122Value(指标名称)<br>'+exprestr);
					functionWizardScope.value = "取部门值";
					functionWizardScope.explained = "取部门值(指标名称)";
					functionWizardScope.datastrcssaname = "关联指标  ";
					functionWizardScope.datastrcss1name = "结果指标  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_volu9_20":
					var exprestr = "返回值：关联指标跟单位信息集中单位名称进行关联，取关联上的单位对应的结果指标内容，当此函数只有一个参数时，返回人员所在部门在单位信息集中对应的指标内容<br>";
					//exprestr+="举例：“张三”，异动模板中变化前部门为“运输部”，变化后部门为“能源与环保”<br>取部门值(所属行业)<br>";
					//	exprestr+="返回值：张三所在部门“运输部”的所属行业，如果张三部门为空，返回张三所在单位的所属行业<br> ";
					exprestr+="取部门值(变化后部门, 所属行业)<br>返回值：张三变化后部门“能源与环保”部的所属行业<br>";
					exprestr+="注意：公式取单位信息集指标时，一般取单位对应的指标内容；有时需要取部门对应的指标内容，可用本函数。";
					Ext.getCmp("explain").setHtml('说明：(1)GetE0122Value(结果指标)<br>(2)GetE0122Value(关联指标,结果指标)<br>(3)GetE0122Value(关联指标,结果指标,条件)<br>'+exprestr);
					functionWizardScope.value = "取部门值";
					functionWizardScope.explained = "&nbsp;取部门值(关联指标,结果指标,条件)";
					functionWizardScope.datastrcssaname = "关联指标  ";
					functionWizardScope.datastrcss2name = "结果指标  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_volp7_20":
					var exprestr = "返回值：关联指标跟岗位信息集中的岗位名称进行关联，取关联上的岗位对应的指标内容<br>举例：“张三”从“检修计划管理”岗（岗位属性为空）调到“机构点检员”（岗位属性为高温），那么人事异动业务中需要计算出此人变化前特殊工种标识以及变化后特殊工种标识。<br>取岗位值(拟岗位名称,岗位属性)<br>返回：张三变化后岗位的岗位属性，即高温";
					Ext.getCmp("explain").setHtml('说明：	(1)取岗位值(结果指标)<br>(2)取岗位值(关联指标,结果指标)<br>(3)取岗位值(关联指标,结果指标,条件)<br>'+exprestr);
					functionWizardScope.value = "取岗位值";
					functionWizardScope.explained = "&nbsp;取岗位值(关联指标,结果指标,条件)";
					functionWizardScope.datastrcssaname = "关联指标  ";
					functionWizardScope.datastrcss2name = "结果指标  ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vols6_3_11_10_10":
					Ext.getCmp("explain").setHtml("说明：<br> 执行存储过程(过程名：参数1,参数2,参数3,...)参数中可用\"当前表\"<br> 举例：<br>  参数为数字型： 10<br>  参数为字符型： 'AAAAAA'<br>  参数为日期型： #2001.01.02#");
					functionWizardScope.value = "执行存储过程";
					functionWizardScope.explained = "执行存储过程(过程名：参数1,参数2,参数3,...)";
					functionWizardScope.conditionsdesc = "过程名";
					functionWizardScope.conditionssubset1 = "参数1";
					functionWizardScope.conditionssubset = "参数2";
					functionWizardScope.conditionssubset3 = "参数3";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_vol9_100":
					Ext.getCmp("explain").setHtml("说明：对象指标(指标名称)");
					functionWizardScope.value = "对象指标";
					functionWizardScope.explained = "对象指标(指标名称)";
					functionWizardScope.datastrcssaname = "指标名称";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "V_volq1":
					Ext.getCmp("explain").setHtml("说明：取自于(变量名)");
					functionWizardScope.value = "取自于";
					functionWizardScope.explained = "取自于(变量名)";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "A_vol9_6_2_10_2":
					var exprestr = "说明：例子：序号(单据编号，部门,2,\"序号名称\",1|0)<br>返回值：单据编号生成规则=部门前2位编码+A0104指标按序号维护中规则生成的编号<br>"+
					"参数:1|0 按截取内容分类编号<br>1  表示按部门分组，每个部门人员的单据编号都从1开始连续编号<br>0  表示所有人的单据编号，按编号规则连续生成"+
					 "例如：<br>部门	姓名	单据编号（1）	单据编号（0）<br>01	张三	01001		01001<br>01	李四	01002		01002<br>02	王五	02001		02003<br><br>注意"+
					 "<br>（1）需到序号维护中分别定义：序号名称，序号名称_部门1前2位，序号名称_部门2前2位<br>（2）序号名称不能关联指标，必须是自定义的一个任意名称 ";  
					Ext.getCmp("explain").setHtml("序号(目标指标，参考值指标,n,\"序号名称\",1|0)"+exprestr);
					functionWizardScope.value = "序号";
					functionWizardScope.explained = "序号(单据编号，部门,n,\"序号名称\",1|0)";
					functionWizardScope.datastrcssaname = "目标指标";
					functionWizardScope.datastrcss1name = "参考值指标";
					functionWizardScope.conditionssubset = "序号字符串";
					functionWizardScope.initiationnumid = "截取长度";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "5":
					Ext.getCmp("explain").setHtml('说明：常量');
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "CC_con0":
					Ext.getCmp("explain").setHtml("说明：逻辑常量(TRUE)");
					functionWizardScope.value = "真";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "CC_con1":
					Ext.getCmp("explain").setHtml("说明：逻辑常量(FALSE)");
					functionWizardScope.value = "假";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "CC_con2":
					Ext.getCmp("explain").setHtml("说明：空(日期)");
					functionWizardScope.value = "空";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "CC_con3":
					Ext.getCmp("explain").setHtml("说明：#2000.3.22#表示2000年3月22日");
					functionWizardScope.value = "#2000.3.22#";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "CC_con4":
					Ext.getCmp("explain").setHtml("说明：#3.22#表示3月22日");
					functionWizardScope.value = "#3.22#";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "CC_con5":
					Ext.getCmp("explain").setHtml("说明：“张三”表示姓名为张三");
					functionWizardScope.value = "张三";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "6":
					Ext.getCmp("explain").setHtml('说明：逻辑操作符');
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "LL_log0":
					Ext.getCmp("explain").setHtml("说明：AND");
					functionWizardScope.value = "且";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "LL_log1":
					Ext.getCmp("explain").setHtml("说明：OR");
					functionWizardScope.value = "或";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "LL_log2":
					Ext.getCmp("explain").setHtml("说明：NOT");
					functionWizardScope.value = "非";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "7":
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "OO_opr0":
					Ext.getCmp("explain").setHtml("说明：加");
					functionWizardScope.value = "+";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr1":
					Ext.getCmp("explain").setHtml("说明：减");
					functionWizardScope.value = "-";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr2":
					Ext.getCmp("explain").setHtml("说明：乘");
					functionWizardScope.value = "*";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr3":
					Ext.getCmp("explain").setHtml("说明：除");
					functionWizardScope.value = "/";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr4":
					Ext.getCmp("explain").setHtml("说明：整除");
					functionWizardScope.value = "\\";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr5":
					Ext.getCmp("explain").setHtml("说明：整除");
					functionWizardScope.value = "DIV";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr6":
					Ext.getCmp("explain").setHtml("说明：求余");
					functionWizardScope.value = "%";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "OO_opr7":
					Ext.getCmp("explain").setHtml("说明：求余");
					functionWizardScope.value = "MOD";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "8":
					Ext.getCmp("explain").setHtml("说明：关系运算符");
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "RR_rel0":
					Ext.getCmp("explain").setHtml("说明：等于");
					functionWizardScope.value = "=";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel1":
					Ext.getCmp("explain").setHtml("说明：大于");
					functionWizardScope.value = ">";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel2":
					Ext.getCmp("explain").setHtml("说明：大于等于");
					functionWizardScope.value = ">=";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel3":
					Ext.getCmp("explain").setHtml("说明：小于");
					functionWizardScope.value = "<";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel4":
					Ext.getCmp("explain").setHtml("说明：小于等于");
					functionWizardScope.value = "<=";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel5":
					Ext.getCmp("explain").setHtml("说明：不等于");
					functionWizardScope.value = "<>";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel6":
					Ext.getCmp("explain").setHtml("说明：包含");
					functionWizardScope.value = "LIKE";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "RR_rel7":
					var exprestr = "IN操作符语法: IN (操作数列表)|本单位<br>操作数列表可以有两种, 字符串列表和整数列表, 字符串用双引号限定, 列表中操作数之间用,分隔.<br>";
					exprestr+="示例: 如果 指标名称 IN (\"01\",\"02\",\"03\",\"05\",\"08\") 那么 ....<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp 如果 指标名称 IN (1,2,3,8) 那么 .... ";
					Ext.getCmp("explain").setHtml("说明：在...内<br>"+exprestr);
					functionWizardScope.value = "IN()";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "9":
					Ext.getCmp("explain").setHtml("说明：其他");
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "EE_oth0":
					Ext.getCmp("explain").setHtml("说明：括号");
					functionWizardScope.value = "(  )";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "EE_oth1":
					Ext.getCmp("explain").setHtml("说明：中括号[表示指标]");
					functionWizardScope.value = "[  ] ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "EE_oth2":
					Ext.getCmp("explain").setHtml("说明：大括号[临时变量]");
					functionWizardScope.value = "{  } ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "EE_oth3":
					Ext.getCmp("explain").setHtml("说明：注释标识");
					functionWizardScope.value = "// ";
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "10":
					Ext.getCmp("explain").setHtml("说明：工资函数");
					functionWizardScope.value = "";
					functionWizardScope.id = id;
					Ext.getCmp("next").disable();
					Ext.getCmp("up").disable();
					Ext.getCmp("okok").disable();
					break;
				case "S_stan0":
					Ext.getCmp("explain").setHtml("说明：ExecuteStandard(标准号,横一,横二,纵一,纵二)<br>执行标准(标准号,横一,横二,纵一,纵二)<br>例子：执行标准(12, 档次, 空, 工资级别, 空)<br>返回值：找出工资级别、工资档次对应的结果值（级别工资）");
					functionWizardScope.value = "执行标准";
					functionWizardScope.standname = '标准表';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_sthl1":
					Ext.getCmp("explain").setHtml("说明：NearByHigh(标准号,纵向指标,结果指标)<br>例子：就近就高(12, 工资级别, 级别工资)<br>只支持代码型指标建立的，且只有一个横向指标及一个纵向指标的标准!");
					functionWizardScope.value = "就近就高";
					functionWizardScope.standhlname = '标准表';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_sthl2":
					Ext.getCmp("explain").setHtml("说明：NearByLow(标准号,纵向指标,结果指标)<br>例子：就近就低(12, 工资级别, 级别工资)<br>只支持代码型指标建立的，且只有一个横向指标及一个纵向指标的标准!");
					functionWizardScope.value = "就近就低";
					functionWizardScope.standhlname = '标准表';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_tztd1":
					Ext.getCmp("explain").setHtml("说明：就近套级套档(标准表号,结果指标,取横|纵向指标标识)  1:横向指标 2：纵向指标<br>例子：就近套级套档(12,基本工资,1)<br>只支持代码型指标建立的，且只有一个横向指标及一个纵向指标的标准!");
					functionWizardScope.value = "就近套级套档";
					functionWizardScope.standhlname = '标准表';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_item3_2_4":
					Ext.getCmp("explain").setHtml('说明：前一个代码(代码指标,增量,极值代码)<br>前一个代码(工资级别, 1, "03")<br>工资级别指标代码向前递一级，但不会超过"03"所代表级别<br>如"10"变为"09", "07"变为"06""');
					functionWizardScope.value = "前一个代码";
					functionWizardScope.itemidname = '前一个代码';
					functionWizardScope.decimalname = '增量';
					functionWizardScope.codeMaxname = '极值代码';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_item4_2_4":
					Ext.getCmp("explain").setHtml('说明：后一个代码(代码指标,增量,极值代码)<br>后一个代码(工资级别, 1, "13")<br>工资级别指标代码向后递一级，但不会超过"13"所代表级别<br>如"08"变为"09", "07"变为"08"');
					functionWizardScope.value = "后一个代码";
					functionWizardScope.itemidname = '后一个代码';
					functionWizardScope.decimalname = '增量';
					functionWizardScope.codeMaxname = '极值代码';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_item5_7_4_5":
					Ext.getCmp("explain").setHtml('说明：代码调整(代码指标,增量指标,极大值代码,极小值代码)<br>代码调整(工资级别, 调整级数, "15", "03")<br>代码调整(“05”,2, "15", "03") 返回值：07<br>代码调整(“05”,-2, "15", "03") 返回值：03');
					functionWizardScope.value = "代码调整";
					functionWizardScope.itemidname = '代码调整';
					functionWizardScope.incrementalItemname = '增量指标';
					functionWizardScope.codeMaxname = '极大值代码';
					functionWizardScope.codeMinname = '极小值代码';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar5":
					Ext.getCmp("explain").setHtml('说明：基于一个子集的历史记录，计算满足条件的表达式exp1累计之和<br>例子：分段计算(exp1)<br>&nbsp;&nbsp;基于子集 A02 满足 开始时间>=#1998.7.1#计算满足条件的∑(exp1)');
					functionWizardScope.value = '前一个代码(现所学专业,0,"0101分段计算(exp1) 基于子集 ... [满足 ...]") ';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar6":
				
				
					var exprestr = '说明：分段计算的扩展，是基于两个子集的历史记录，满足条件的历史<br>例子：分段计算2(A07,开始时间, 结束时间,<br>'
												+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exp1,"",<br>'
												+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A10,聘任起始时间, 聘任终止时间,'
												+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exp2,"",<br>'
												+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exp3,"#2004.10.1", "")<br>'
												+'计算2004.10.1后　∑(exp3*MAX(exp1,exp2))';
					var value = '分段计算2(子集1, 开始时间1, 结束时间1, exp1, "",\n '
										+'子集2, 开始时间2, 结束时间2, exp2, "",\n'
										+'expl3, "", "")';
					Ext.getCmp("explain").setHtml(exprestr);
					functionWizardScope.value = value;
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar7":
					var exprestr = '说明：历史记录最初指标值(返回指标，条件) <br>例子：历史记录最初指标值(任职时间,  职务名称="03")<br>'
										+'返回指标必须是子集指标<br>'
										+'返回子集中最近符合条件的记录连续上溯到第一条记录的指标值';
					Ext.getCmp("explain").setHtml(exprestr);
					functionWizardScope.value = '历史记录最初指标值(,)';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar8":
					var exprestr = '说明：历史记录最初指标值(返回指标,条件)<br>例子：历史记录最初指标值(任职时间,  职务名称="03")<br>'
										+'返回指标必须是子集指标<br>'
										+'返回子集中最近符合条件的记录连续上溯到第一条记录的指标值';
					Ext.getCmp("explain").setHtml(exprestr);
					functionWizardScope.value = '历史记录最初指标值(,)';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar9":
					var exprestr = '说明：上一个历史记录指标值(返回指标,条件)<br>例子：上一个历史记录指标值(职务名称,  职务名称="03")<br>'
										+'返回指标必须是子集指标<br>'
										+'返回子集中最近符合条件的上一个不符条件的指标值';
					Ext.getCmp("explain").setHtml(exprestr);
					functionWizardScope.value = '上一个历史记录指标值(,)';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar10":
					Ext.getCmp("explain").setHtml('说明：取上月实发工资人数（薪资类别号，归属单位指标，条件）<br>例子:取上月实发工资人数("1,4,9",单位名称,发薪表示="0")<br>');
					functionWizardScope.value = '取上月实发工资人数(,,)';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "SS_sar11":
					Ext.getCmp("explain").setHtml('计算满足条件的∑(exp1)<br>例子:分段计算(exp1)<br>基于子集  A02 满足  开始时间>=#1998.07.01#');
					functionWizardScope.value = '分段计算(exp1) 基于子集 ... [满足 ...]';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "S_pert1":
					Ext.getCmp("explain").setHtml('说明：取专项附加额(类型，取值范围)<br>'
											+ '类型：子女教育|住房租金|住房贷款利息|赡养老人|继续教育<br>'
											+ '取值范围：当月|累计<br>'
											+ '例子1：取专项附加额(住房租金，当月)：当月应当抵扣的住房租金金额<br>'
											+ '例子2：取专项附加额(住房租金，累计)：截止到当月本年应当累计抵扣的住房租金金额<br><br>'
											+ '仅能用于薪资发放和保险业务中');
					functionWizardScope.value = '取专项附加额';
					functionWizardScope.itemname = '类型';
					functionWizardScope.statisRange = '取值范围';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "K_item0_21_12_15_16": 
					Ext.getCmp("explain").setHtml('说明：可休天数(请假类型，请假起始时间，请假结束时间，1|0，0|1|2)'
							                    + '<br>包含在途标志: 1：包含；0：不包含'
							                    + '<br>天数范围: 0:总可休天数1:当年可休天数2：上年结余可休天数'
							                    + '<br>例子:可休天数(拟请假类型,拟请假起始时间,拟请假结束时间,1,0)<br>');
					functionWizardScope.value = '可休天数';
					functionWizardScope.itemidname = '请假类型';
					functionWizardScope.datesubset = '起始时间';
					functionWizardScope.datesubset1 = '结束时间';
					functionWizardScope.isTransitName = '参数1';
					functionWizardScope.isContainsLastName = '参数2';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "K_item1_21_12_16":
					Ext.getCmp("explain").setHtml('说明：已休天数（请假类型，请假起始时间，请假结束时间，2|1|0）'
											+ '<br>0:总已休天数1:当年已休天数2：上年结余已休天数'
											+ '<br>例子:已休天数(拟请假类型,拟请假起始时间,拟请假结束时间,0)<br>');
					functionWizardScope.value = '已休天数';
					functionWizardScope.itemidname = '请假类型';
					functionWizardScope.datesubset = '起始时间';
					functionWizardScope.datesubset1 = '结束时间';
					functionWizardScope.isContainsLastName = '参数';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
				case "K_item2_21_12":
					Ext.getCmp("explain").setHtml('说明：申请时长（类型，起始时间，结束时间）<br>例子:申请时长(拟类型,拟起始时间,拟结束时间)<br>');
					functionWizardScope.value = '申请时长';
					functionWizardScope.itemidname = '类型';
					functionWizardScope.datesubset = '起始时间';
					functionWizardScope.datesubset1 = '结束时间';
					functionWizardScope.id = id;
					functionWizardScope.haveButtons(id);
					break;
			}
		},
		haveButtons:function(id){
			if(id.length>0){
				var array = id.split("_");
				if(array.length>0){
					if(array[0].length==2){
						Ext.getCmp("next").disable();
						Ext.getCmp("up").disable();
						Ext.getCmp("okok").enable();
					}else{
						Ext.getCmp("next").enable();
						Ext.getCmp("up").disable();
						Ext.getCmp("okok").disable();
					}
				}
			}
		},
		nextStep:function(){
			var id = functionWizardScope.node;
			var win2array = new Array();
			if(id!=null&&id.length>1){
				var array = id.split("_");
				var start1="";
				var start2="";
				var start3="";
				var start4="";
				var start5="";
				var start6="";
				if(array.length>0){
					start1=array[0];
				}
				if(array.length>1){
					start2=array[1];
				}
				if(array.length>2){
					start3=array[2];
				}
				if(array.length>3){
					start4=array[3];
				}
				if(array.length>4){
					start5=array[4];
				}
				if(array.length>5){
					start6=array[5];
				}
				var subset = "";
				if(start2.length>0){
					if(start2.length==6){
						subset = start2.substring(0,start2.length-2);
					}else{
						subset = start2.substring(0,start2.length-1);
					}
					switch (subset){
						case 'str':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","strexpression_arr");//(flag,subset,id,name)
							win2array[win2array.length]=functionWizardScope.initexpression(subset,"字符表达式","strexpression_arr","A");
							break;
						case 'code':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","strexpression_arr");
							win2array[win2array.length]=functionWizardScope.initexpression(subset,"字符表达式","strexpression_arr","A");
							break;
						case 'num':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","numexpression1_arr,numexpression2_arr,dateexpression1_arr,dateexpression2_arr");
							win2array[win2array.length]=functionWizardScope.initexpression(subset,"数值表达式","numexpression1_arr","N");
							break;
						case 'data':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","dateexpression1_arr,dateexpression2_arr,datetype_arr");
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datesubset,"dateexpression1_arr","D");
							if(start1=="T")
								win2array[win2array.length]=functionWizardScope.initComboBox("datetype_arr",functionWizardScope.datesubset);
							break;
						case 'vol':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","datestr_arr,strid_arr,itemid_arr");
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datastrcssaname,"datestr_arr","V");
							break;
						case 'vsub'://统计表单数据
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","datestr_arr,strid_arr,itemid_arr");
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datastrcssaname,"datestr_arr","V");
							break;
						case 'item':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","itemid_arr,incrementalItem_arr,dateexpression1_arr,dateexpression2_arr");
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.itemidname,"itemid_arr","item");
							break;
						case 'stan':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"standid",functionWizardScope.standname,"hfactor_arr,vfactor_arr,s_hfactor_arr,s_vfactor_arr");
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"横一","hfactor_arr","item",true);
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"纵一","vfactor_arr","item",true);
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"横二","s_hfactor_arr","item",true);
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"纵二","s_vfactor_arr","item",true);
							break;
						case 'sthl':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"standhlid",functionWizardScope.standhlname,"vfactor_arr,s_vfactor_arr,item");
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"纵向指标","vfactor_arr","item",false);
							//就近函数只需要纵向指标即可，不需要纵向子指标的，纵向子指标计算出错
							//win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"纵向指标","s_vfactor_arr","item",false);
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"结果指标","item","item",false);
							break;
						case 'tztd':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"standhlid",functionWizardScope.standhlname,"item");
							win2array[win2array.length]=functionWizardScope.initFactorCode(subset,"结果指标","item","item",false);
							break;	
						case 'strs':
							break;
						case 'vols':
							break;
						case 'volu':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","datestr_arr");
							win2array[win2array.length]=functionWizardScope.initexpression("datastrcss",functionWizardScope.datastrcssaname,"datestr_arr","V");
							win2array[win2array.length]=functionWizardScope.initFieldset("1",subset,"fieldnameunit","子集","strid2_arr");
							break;
						case 'volp':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"fieldname","子集","datestr_arr");
							win2array[win2array.length]=functionWizardScope.initexpression("datastrcss",functionWizardScope.datastrcssaname,"datestr_arr","V");
							win2array[win2array.length]=functionWizardScope.initFieldset("1",subset,"fieldsetlistpos","子集","strid2_arr");
							break; 
						case 'volq':
							win2array[win2array.length]=functionWizardScope.initFieldset("0",subset,"variablename","临时变量","");
							break; 
						case 'pert':
							win2array[win2array.length]=functionWizardScope.initComboBox("personTax",functionWizardScope.itemname);
							win2array[win2array.length]=functionWizardScope.initComboBox("perTaxRange",functionWizardScope.statisRange);
							break; 
					}
				}
				if(start3.length>0){
					switch (start3){
						case '1':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datesubset1,"dateexpression2_arr","D");
							break;
						case '2':
							win2array[win2array.length]=functionWizardScope.inittext("decimal",functionWizardScope.decimalname,"numberfield");
							break;
						case '3':
							win2array[win2array.length]=functionWizardScope.inittext("conditions",functionWizardScope.conditionsdesc,"textfield");
							break;
						case '4':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,"数值表达式","numexpression2_arr","N");
							break;
						case '5':
							win2array[win2array.length]=functionWizardScope.initCode(subset,functionWizardScope.codeMaxname,"code_maxarr","item");
							break;
						case '6':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datastrcss1name,"strid_arr","V");
							break;
						case '7':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.incrementalItemname,"incrementalItem_arr","I");
							break;
						case '8':
							win2array[win2array.length]=functionWizardScope.initComboBox("statid",functionWizardScope.condname);
							break;
						case '9':
							win2array[win2array.length]=functionWizardScope.initComboBox("templates","业务模板");
							break;
						case '10':
							win2array[win2array.length]=functionWizardScope.initComboBox("partTimeJob_select","参数设置");
							break;
						case '20':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datastrcss2name,"strid2_arr","V");
							break;
						case '21':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datesubset,"dateexpression1_arr","D");
							break;
						case '22':	
							win2array[win2array.length]=functionWizardScope.initComboBox("datetype_arr",functionWizardScope.datesubset);
							break;
					}
				}
				if(start4.length>0){
					switch (start4){
						case '1':
							win2array[win2array.length]=functionWizardScope.initComboBox("direction","方向");
							break;
						case '2':
							win2array[win2array.length]=functionWizardScope.inittext("initiation","长度","numberfield");
							break;
						case '3':
							win2array[win2array.length]=functionWizardScope.initComboBox("way","方式");
							break;
						case '4':
							win2array[win2array.length]=functionWizardScope.initCode(subset,functionWizardScope.codeMaxname,"code_maxarr","C");
							break;
						case '5':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.codeMinname,"code_minarr","C");
							break;
						case '6':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.strsubset,"strexpression_arr","A");
							break;
						case '7':
							win2array[win2array.length]=functionWizardScope.initComboBox("hdayslogo","节假日标志");
							break;
						case '9':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.itemidname,"itemid_arr","item");
							break;
						case '10':
							win2array[win2array.length]=functionWizardScope.inittext("conditions",functionWizardScope.conditionsdesc,"textfield");
							break;
						case '11':
							win2array[win2array.length]=functionWizardScope.inittext("conditions1",functionWizardScope.conditionssubset1,"textfield");
							break;
						case '12':
							win2array[win2array.length]=functionWizardScope.initexpression(subset,functionWizardScope.datesubset1,"dateexpression2_arr","D");
							break;
					}
				}
				if(start5.length>0){
					switch (start5){
						case '1':
							win2array[win2array.length]=functionWizardScope.initComboBox("rangeid",functionWizardScope.rangeidname);
							break;
						case '5':
							win2array[win2array.length]=functionWizardScope.initCode(subset,functionWizardScope.codeMinname,"code_minarr","C");
							break;
						case '10':
							win2array[win2array.length]=functionWizardScope.inittext("conditions2",functionWizardScope.conditionssubset,"textfield");
							break;
						case '15':
							win2array[win2array.length]=functionWizardScope.initComboBox("isTransit",functionWizardScope.isTransitName,"textfield");
							break;
						case '16':
							win2array[win2array.length]=functionWizardScope.initComboBox("isContainsLast",functionWizardScope.isContainsLastName,"已休");
							break;
					}
				}
				if(start6.length>0){
					switch (start6){
						case '1':
							win2array[win2array.length]=functionWizardScope.inittext("conditions",functionWizardScope.conditionsdesc,"textfield");
							break;
						case '2':
							win2array[win2array.length]=functionWizardScope.initComboBox("sortingnum","按截取内容分类编号");
							break;
						case '10':
							win2array[win2array.length]=functionWizardScope.inittext("conditions3",functionWizardScope.conditionssubset3,"textfield");
							break;
						case '16':
							win2array[win2array.length]=functionWizardScope.initComboBox("isContainsLast",functionWizardScope.isContainsLastName,"可休");
							break;
					}
				}
				functionWizardScope.showWin2(win2array);
			}
		},
		initFieldset:function(flag,subset,id,name,ids){//子集下拉框 flag:0：表示下一步后第一层子集 1：后面个性的子集下拉框  subset:树节点  id：当前组件id  name：当前组件名称  ids：联动组件id（可多个，逗号分隔）
			var fieldsetStore = Ext.create('Ext.data.Store',
			{
				fields:['name','id'],
				proxy:{
			    	type: 'transaction',
			        functionId:'ZJ100000102',
			        extraParams:{
			        		opt:functionWizardScope.opt,//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			        		keyid:functionWizardScope.keyid,
			        		flag:flag,
			        		subset:subset,
			        		type:functionWizardScope.type//入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他    区别临时变量时opt无法分别出是什么
			        },
			        reader: {
			            type: 'json',
			            root: 'data'         	
			        }
				},
				autoLoad: true
			});
			var fieldsetlist = Ext.create('Ext.form.ComboBox', {
	   			fieldLabel:name,
	   			id:id,
	   			store:fieldsetStore,
	   			displayField:'name',
	   			editable:false,
	   			valueField:'id',
	   			queryMode:'local',
	   			labelAlign:'right',
	   			labelWidth:70,
	   			labelSeparator:false,
	   			width:300,
	   			style:'margin-top:10px',
//   				matchFieldWidth:false,
				listeners:{
	   				select:function(combo,records){
						var fieldsetid = combo.getValue();
	   					var arr = ids.split(",");
	   					for(var i=0;i<arr.length;i++){
	   						if(Ext.getCmp(arr[i])==null)
	   							continue;
	   						var obj =  Ext.getCmp(arr[i]);
	   						obj.reset();
	   						var standid = Ext.getCmp("standhlid");
	   						if(!standid){
	   							standid = Ext.getCmp("standid");
	   						}
	   						if(arr[i]=='hfactor_arr'||arr[i]=='vfactor_arr'||arr[i]=='s_hfactor_arr'||arr[i]=='s_vfactor_arr'||arr[i]=='item'){
	   							 obj.getStore().load({
									params:{
										standid:standid.getValue(),
										opt:functionWizardScope.opt,//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
			       						keyid:functionWizardScope.keyid
									},
									callback: function(record, option, succes){
										if(record.length>1){
//											obj.show(); 
										}else{
//											obj.hide(); 
										}
									}
								});
	   						}else{
	   							Ext.getCmp(arr[i]).getStore().load({
									params:{
										fieldsetid:fieldsetid,
										opt:functionWizardScope.opt,//1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
										mode:functionWizardScope.mode,//“xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
										functionid:functionWizardScope.id,
			       						keyid:functionWizardScope.keyid,
			       						vtemptype:functionWizardScope.type//type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
									}
								});
	   						}
	   					}
					}
				}
			});
			return fieldsetlist;
		},
		initexpression:function(subset,expressionName,id,type){//表达式下拉框subset:向导树节点 expressionName名称，id主键，type表示哪类的下拉框 A:字符型 N：数值型 D：日期型 V：全类型 item：代码型 I：整形
			var expressionStore = Ext.create('Ext.data.Store',
			{
				fields:['name','id'],
				proxy:{
				    	type: 'transaction',
				        functionId:'ZJ100000103',
				        extraParams:{
				        		subset:subset,
				        		type:type
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				}
			});
			var fieldsetlist = Ext.create('Ext.form.ComboBox', {//表达式所用的下拉框
	   			fieldLabel:expressionName,
	   			id:id,
	   			store:expressionStore,
	   			displayField:'name',
	   			editable:false,
	   			valueField:'id',
	   			queryMode:'local',
	   			labelAlign:'right',
	   			labelWidth:70,
	   			labelSeparator:false,
	   			width:300,
	   			style:'margin-top:10px',
				listeners:{
	   				select:function(combo,records){
	   					//代码调整选择增量指标的时候不需要重新赋极大和极小值指标
		   				if(id != 'incrementalItem_arr') {
		   					var codearr = "code_maxarr,code_minarr";
		   					var arr = codearr.split(",");
							for(var i=0;i<arr.length;i++){
								var itemid=combo.getValue();
								var _itemid = itemid.split(":");
								if(_itemid.length==2){
									if(Ext.getCmp(arr[i])==null)
			   							continue;
			   						Ext.getCmp(arr[i]).reset();
									Ext.getCmp(arr[i]).getStore().load({
										params:{
											fieldsetid:functionWizardScope.getCodesid("fieldname"),
											itemid:_itemid[0].split("_")[0],
											tempid:functionWizardScope.id,
											keyid:functionWizardScope.keyid
										}
									});
								}
							}
						}
	   				}
				}
			});
			return fieldsetlist;
		},
		initCode:function(subset,expressionName,id,type){
			var expressionStore = Ext.create('Ext.data.Store',
			{
				fields:['name','id'],
				proxy:{
				    	type: 'transaction',
				        functionId:'ZJ100000104',
				        extraParams:{
				        		subset:subset,
				        		type:type
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				}
			});
			var fieldsetlist = Ext.create('Ext.form.ComboBox', {
	   			fieldLabel:expressionName,
	   			id:id,
	   			store:expressionStore,
	   			displayField:'name',
	   			editable:false,
	   			valueField:'id',
	   			queryMode:'local',
	   			labelAlign:'right',
	   			labelWidth:70,
	   			labelSeparator:false,
	   			width:300,
	   			style:'margin-top:10px',
				listeners:{
	   				select:function(combo,records){

					}
				}
			});
			return fieldsetlist;
		},
		initFactorCode:function(subset,expressionName,id,type,hidden){
			var expressionStore = Ext.create('Ext.data.Store',
			{
				fields:['name','id'],
				proxy:{
						type: 'transaction',
				        async:false,
				        actionMethods:{
							read:'post'
						},
				        functionId:'ZJ100000105',
				        extraParams:{
				        		subset:subset,
				        		type:type,
				        		keyid:functionWizardScope.keyid,
				        		vtemptype:functionWizardScope.type,//type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
				        		factorId:id
				        },
				        reader: {
				            type: 'json',
				            root: 'data'         	
				        }
				},
				listeners:{  
	    			load : function( store, records, successful, operation){
	    			    if(store.data.length == 0)
	    			    	Ext.getCmp(id).setHidden(true);
	    			    else
	    			    	Ext.getCmp(id).setHidden(false);
	    			}
	    	    },
	    	    autoLoad: true
			});
			var fieldsetlist = Ext.create('Ext.form.ComboBox', {
	   			fieldLabel:expressionName,
	   			id:id,
	   			store:expressionStore,
	   			displayField:'name',
	   			editable:false,
	   			valueField:'id',
	   			queryMode:'local',
	   			labelAlign:'right',
	   			labelWidth:70,
	   			hidden:hidden,
	   			labelSeparator:false,
	   			width:300,
	   			style:'margin-top:10px',
				listeners:{
	   				select:function(combo,records){

					}
				}
			});
			return fieldsetlist;
		},
		inittext:function(id,name,flag){
			if(!name)
				name="&nbsp;";
			if(name == "进元参数(分)") {//逢分进元函数做特殊
				value="1";
			}else {
				value="0";
			}
			var text = {
			        xtype: 'numberfield',
			        anchor: '100%',
			        id:id,
			        name: id,
			        fieldLabel: name,
			        labelWidth:70,
			        labelSeparator:false,
	   				width:300,
	   				labelAlign:'right',
	   				style:'margin-top:10px',
			        value: value,
			        maxValue: 99
			    };
			if(flag=="textfield"){
				text = {
			        xtype: 'textfield',
			        anchor: '100%',
			        id:id,
			        name: id,
			        fieldLabel: name,
			        labelSeparator:false,
			        labelAlign:'right',
			        style:'margin-top:10px',
			        labelWidth:70,
	   				width:300
				};
			}
			return text;
		},
		initComboBox:function(id,expressionName,params){
			var optionStore = Ext.create('Ext.data.Store', {
			    fields: ['id', 'name'],
			    data : [
			    	{"id":"0", "name":"默认操作单位"},
			        {"id":"1", "name":"工资发放"},
			        {"id":"2", "name":"工资总额"},
			        {"id":"3", "name":"所得税"}
			    ]
			});
			if(id=="partTimeJob_select"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	{"id":"empty", "name":""},
				        {"id":"单位部门", "name":"单位部门"},
				        {"id":"单位", "name":"单位"},
				        {"id":"部门", "name":"部门"}
				    ]
				});
			}else if(id=="direction"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"最初第", "name":"最初第"},
				        {"id":"最近第", "name":"最近第"}
				    ]
				});
			}else if(id=="way"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"的总和", "name":"的总和"},
				        {"id":"的平均值", "name":"的平均值"},
				        {"id":"的个数", "name":"的个数"},
				        {"id":"的最大值", "name":"的最大值"},
				        {"id":"的最小值", "name":"的最小值"},
				        {"id":"的最初第一条记录", "name":"的最初第一条记录"},
				        {"id":"的最近第一条记录", "name":"的最近第一条记录"}
				    ]
				});
			}else if(id=="hdayslogo"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"不含节假日", "name":"不含节假日"},
				        {"id":"含节假日", "name":"含节假日"}
				    ]
				});
			}else if(id=="sortingnum"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"0", "name":"否"},
				        {"id":"1", "name":"是"}
				    ]
				});
			}else if(id=="statid"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"个数", "name":"个数"},
				        {"id":"总和", "name":"总和"},
				        {"id":"最小值", "name":"最小值"},
				        {"id":"最大值", "name":"最大值"},
				        {"id":"平均值", "name":"平均值"}
				    ]
				});
			}else if(id=="rangeid"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"当前列表", "name":"当前列表"},
				        {"id":"当前人员库", "name":"当前人员库"}
				    ]
				});
			}else if(id=="datetype_arr"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"empty", "name":""},
				        {"id":"'YYYY-MM-DD':'YYYY-MM-DD'", "name":"'YYYY-MM-DD'"},
				        {"id":"'YYYY-MM-DD HH24':'YYYY-MM-DD HH24'", "name":"'YYYY-MM-DD HH24'"},
				        {"id":"'YYYY-MM-DD HH24@MI':'YYYY-MM-DD HH24:MI'", "name":"'YYYY-MM-DD HH24:MI'"},
				        {"id":"'YYYY-MM-DD HH24@MI@SS':'YYYY-MM-DD HH24:MI:SS'", "name":"'YYYY-MM-DD HH24:MI:SS'"}
				    ]
				});
			}else if(id=="isTransit"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	{"id":"0", "name":"0：不包含在途的单据"},
				        {"id":"1", "name":"1：包含在途的单据"}
				    ]
				});
			}else if(id=="personTax"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	//{"id":"所有专项的合计", "name":"所有专项的合计"},
				    	//{"id":"不含大病、继续教育其它专项的合计", "name":"不含大病、继续教育其它专项的合计"},
				    	{"id":common.label.znjy, "name":common.label.znjy},
				    	{"id":common.label.jxjy, "name":common.label.jxjy},
				    	{"id":common.label.zfzj, "name":common.label.zfzj},
				    	{"id":common.label.zfdk, "name":common.label.zfdk},
				    	//{"id":"大病医疗", "name":"大病医疗"},
				        {"id":common.label.sylr, "name":common.label.sylr}
				    ]
				});
			}else if(id=="perTaxRange"){
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	{"id":"当月", "name":"当月"},
				    	{"id":"累计", "name":"累计"}
				    ]
				});
			}else if(id=="isContainsLast"){				
				optionStore = Ext.create('Ext.data.Store', {
				    fields: ['id', 'name'],
				    data : [
				    	{"id":"0", "name":"0：总" + params + "天数"},
				        {"id":"1", "name":"1：当年" + params + "天数"},
				        {"id":"2", "name":"2：上年结余" + params + "天数"}
				    ]
				});
			}
			var fieldsetlist = Ext.create('Ext.form.ComboBox', {
	   			fieldLabel:expressionName,
	   			id:id,
	   			store:optionStore,
	   			displayField:'name',
	   			editable:false,
	   			valueField:'id',
	   			queryMode:'local',
	   			labelAlign:'right',
	   			labelWidth:70,
	   			width:300,
	   			style:'margin-top:10px',
				listeners:{
	   				select:function(combo,records){
						
					}
				},
				tpl:Ext.create('Ext.XTemplate',
					          '<tpl for=".">',
					          '<div class="x-boundlist-item" style="height:21px">{name}</div>',
					          '</tpl>'
					)
			});
			return fieldsetlist;
		},
		showWin2:function(array){//下步 窗口展现

			var itempanel = Ext.create('Ext.panel.Panel', {
				border:false,
				items:[{
			 	   	xtype:'fieldset',
			        title:functionWizardScope.explained,
			        layout:'column',
					height:300,
					width:330,
			   		items:array
		   		}]
		   	});
		   	var buttons2 = Ext.create('Ext.panel.Panel',{
   				layout:'column',
	        	border:false,
	        	columnWidth:1,
	        	width:50,
	        	items:[{
	        		xtype:'button',
	        		columnWidth:1,
	        		text:'下步',
	        		id:'next2',
	        		disabled:true,
	        		style:'margin-top:20px',
	        		listeners:{
	        			'click':function(){
							functionWizardScope.nextStep();
	        			}
	        		}
	        	},{
	        		xtype:'button',
	        		columnWidth:1,
	        		style:'margin-top:20px',
	        		text:'上步',
	        		id:'up2',
//	        		disabled:true,
	        		listeners:{
	        			'click':function(){
							functionWizardScope.upStep();
	        			}
	        		}
	        	},{
	        		xtype:'button',
	        		columnWidth:1,
	        		style:'margin-top:20px',
	        		text:'完成',
	        		id:'okok2',
//	        		disabled:true,
	        		listeners:{
	        			'click':function(){
							functionWizardScope.completed(functionWizardScope.callbackfunc);
	        			}
	        		}
	        	}]
			});

			functionWizardScope.win2 = Ext.widget("window",{
	          title:'请设置函数参数，按【完成】结束函数向导',
	          height:500,  
	          width:400,
	          layout:'fit',
			  modal:true,
			  resizable:false,
			  closeAction:'destroy',
	       		listeners:{
	       			'beforeclose':function(){
						functionWizardScope.win1.close();
	       			}
	       		},
			  items: [{
				  	xtype:'panel',
				  	layout:'border',
				  	bodyStyle: 'background:#ffffff;',
		         	border:false,
		         	items:[
			            { region: "center",border:false,items:itempanel},
			            { region: "east",border:false,items:buttons2},
			            { region: "south",xtype:'box',border:false,minHeight:150,html:Ext.getCmp("explain").html}
		            ]
	          }]
		    });        
		    functionWizardScope.win2.show();
		    functionWizardScope.win1.hide();
		},
		upStep:function(){
			if(!Ext.isEmpty(functionWizardScope.win2)){//由于窗口都是用的隐藏，所以判断是否存在，存在先销毁
				functionWizardScope.win2.destroy();
			}
//			functionWizardScope.explained = "";
			functionWizardScope.win1.show();
		    //functionWizardScope.win2.hide();
		},
		completed:function(callbackfunc){
			var formula = functionWizardScope.value;
			var id = functionWizardScope.node;
			var array = id.split("_");
			if(array[0].length==2){
				if(callbackfunc)
					Ext.callback(eval(callbackfunc),null,[formula]);
			}else if(id=="K_item0_21_12_15_16"||id=="K_item1_21_12_16"||id=="K_item2_21_12")
			{
				var returns = formula;
				var code_value=functionWizardScope.getCodesid("itemid_arr");
				var date1_value=functionWizardScope.getCodesid("dateexpression1_arr");
				var date2_value=functionWizardScope.getCodesid("dateexpression2_arr");
				var returnValue = "";
				if(id=="K_item0_21_12_15_16") {//对于考勤函数，只有可修天数有是否包含在途的年假参数
					var isTransit=functionWizardScope.getCodesid("isTransit");
					var isContainsLast=functionWizardScope.getCodesid("isContainsLast");
					returnValue = formula+"("+code_value+","+date1_value+","+date2_value+","+isTransit+","+isContainsLast+")";
				}else if(id==="K_item1_21_12_16"){
					var isContainsLast=functionWizardScope.getCodesid("isContainsLast");
					returnValue = formula+"("+code_value+","+date1_value+","+date2_value+","+isContainsLast+")";
				}else {
					returnValue = formula+"("+code_value+","+date1_value+","+date2_value+")";
				}
				if(callbackfunc)
					Ext.callback(eval(callbackfunc),null,[returnValue]);
			}else if(id=="V_vol4_2_1"){
				var returns = formula;
				var dateexpression1 = functionWizardScope.getCodesid("datestr_arr");
				var direction = functionWizardScope.getCodesid("direction");
				var decimal = functionWizardScope.getCodesid("decimal");
				 if(dateexpression1.length>0){
					returns += " " +dateexpression1+" ";
				 }
				 if(direction.length>0){
					returns += " " +direction+" ";
				 }
				 if(decimal.toString().length>0){
					returns += " " +decimal+" ";
				 }else{
					returns += " 0 ";
				 }
				var returnValue = returns + "条记录";
				if(callbackfunc)
					Ext.callback(eval(callbackfunc),null,[returnValue]);
			}else if(id=="V_vol5_3_3"){
				var returns = formula;
				var datestr = functionWizardScope.getCodesid("datestr_arr");
				var conditions = functionWizardScope.getCodesid("conditions");
				var way = functionWizardScope.getCodesid("way");
				if(datestr!=null&&datestr.length>0)
					returns += " " +datestr+" ";
				returns+= " 满足 ";
				if(conditions.length>0){
					returns+= " "+conditions+" ";
				}
				if(way.length>0){
					returns+= " "+way+" ";
				}
				var returnValue = returns
				if(callbackfunc)
					Ext.callback(eval(callbackfunc),null,[returnValue]);
			}else if(id=="V_vsub7_3_3"){
				var returns = formula;
				var fieldname = Ext.getCmp("fieldname").rawValue;
				var index =fieldname.indexOf("-");
				fieldname = fieldname.substring(index+1);
				var datestr = functionWizardScope.getCodesid("datestr_arr");
				var conditions = functionWizardScope.getCodesid("conditions");
				var way = functionWizardScope.getCodesid("way");
				var items = Ext.getCmp("fieldname").getStore().data.items;
			    var num = 0;
			    for(var i = 0; i < items.length; i++) {
			       var name = items[i].data.name.split("-")[1];
			       if(name == fieldname) {
			         num++;
			       }
			    }
			    if(num > 1) {
			       Ext.showAlert("不能选择名称相同的子集！");
			       return false;
			    }
				if(datestr!=null&&datestr.length>0)
					returns += " " + fieldname + "的 " + datestr+" ";
				returns+= " 满足 ";
				if(conditions.length>0){
					returns+= " "+conditions+" ";
				}
				if(way.length>0){
					returns+= " "+way+" ";
				}
				var returnValue = returns
				if(callbackfunc)
					Ext.callback(eval(callbackfunc),null,[returnValue]);
			}else{
				var attribute = functionWizardScope.defvalue();
				var returnValue = formula+"("+attribute+")";
				if(callbackfunc)
					Ext.callback(eval(callbackfunc),null,[returnValue]);
			}
			if(functionWizardScope.win1)
				functionWizardScope.win1.close();
			if(functionWizardScope.win2)
				functionWizardScope.win2.close();
		},
		defvalue:function(){
			var atvalue="";
			var id = functionWizardScope.node;
			if(id!=null&&id.length>1){
				var array = id.split("_");
				var start1="";
				var start2="";
				var start3="";
				var start4="";
				var start5="";
				var start6="";
				
				if(array.length>0){
					start1=array[0];
				}
				if(array.length>1){
					start2=array[1];
				}
				if(array.length>2){
					start3=array[2];
				}
				if(array.length>3){
					start4=array[3];
				}
				if(array.length>4){
					start5=array[4];
				}
				if(array.length>5){
					start6=array[5];
				}
				if(start2.length>0){
					var subset = "";
					if(start2.length==6){
						subset = start2.substring(0,start2.length-2);
					}else{
						subset = start2.substring(0,start2.length-1);
					}
					switch (subset){
						case 'str':
							var strexpression=functionWizardScope.getCodesid("strexpression_arr");
							if(strexpression!=null&&strexpression.length>0){
								atvalue+=strexpression;
							}
							break;
						case 'code':
							var strexpression=functionWizardScope.getCodesid("strexpression_arr");
							if(strexpression!=null&&strexpression.length>0){
								atvalue+=strexpression;
							}
							break;
						case 'num':
							var numexpression1=functionWizardScope.getCodesid("numexpression1_arr");
							if(numexpression1!=null&&numexpression1.length>0){
								atvalue+=numexpression1;
							}
							break;
						case 'data':
							var datetype_item = null;
							var dateexpression1=functionWizardScope.getCodesid("dateexpression1_arr");
							if(dateexpression1!=null&&dateexpression1.length>0){
								atvalue+=dateexpression1;
							}
							if(start1=="T"){//hej update 20160627
								datetype_item=functionWizardScope.getCodesid("datetype_arr");
							}
							if(datetype_item!=null&&datetype_item.length>0){
								atvalue+=","+datetype_item.replace(/@/g,":");
							}
							break;
						case 'vol':
							var datestr=functionWizardScope.getCodesid("datestr_arr");
							if(datestr!=null&&datestr.length>0){
								atvalue+=datestr;
							}
							break;
						case 'item':
							var itemid=functionWizardScope.getCodesid("itemid_arr");
							if(itemid!=null&&itemid.length>0){
								atvalue+=itemid;
							}
							break;
						case 'stan':
							var standid=Ext.getCmp("standid").getValue();
							var hfactor=Ext.getCmp("hfactor_arr").getValue();
							var vfactor=Ext.getCmp("vfactor_arr").getValue();
							var s_hfactor=Ext.getCmp("s_hfactor_arr").getValue();
							var s_vfactor=Ext.getCmp("s_vfactor_arr").getValue();
							if(standid!=null&&standid.length>0){
								var itemarr = standid.split(":"); 
								if(itemarr.length==7){
									atvalue+=itemarr[6];
								}
								if(hfactor!=null&&hfactor.length>0){
									var hfactorarr = hfactor.split(":");
									if(hfactorarr.length==2){
										atvalue+=','+hfactorarr[1];
									}else{
										atvalue+=",''";
									}
								}else{
									atvalue+=',空';
								}
								
								if(s_hfactor!=null&&s_hfactor.length>0){
									var s_hfactorarr = s_hfactor.split(":");
									if(s_hfactorarr.length==2){
										atvalue+=','+s_hfactorarr[1];
									}else{
										atvalue+=",''";
									}
								}else{
									atvalue+=',空';
								}
								if(vfactor!=null&&vfactor.length>0){
									var vfactorarr = vfactor.split(":");
									if(vfactorarr.length==2){
										atvalue+=','+vfactorarr[1];
									}else{
										atvalue+=",''";
									}
								}else{
									atvalue+=',空';
								}
								if(s_vfactor!=null&&s_vfactor.length>0){
									var s_vfactorarr = s_vfactor.split(":");
									if(s_vfactorarr.length==2){
										atvalue+=','+s_vfactorarr[1];
									}else{
										atvalue+=',空';
									}
								}else{
									atvalue+=',空';
								}
							}
							break;
						case 'tztd':
							var standhlid=Ext.getCmp("standhlid").getValue();
							var item=functionWizardScope.getCodesid("item");
							if(standhlid!=null&&standhlid.length>0){
								var itemarr = standhlid.split(":"); 
								if(itemarr.length==7){
									atvalue+=itemarr[6];
								}
								atvalue+=",";
								if(item!=null&&item.length>0){
									var itemarr = item.split(":");
									if(itemarr.length==2){
										atvalue+=itemarr[1];
									}else {
										atvalue+=item;
									}
								}
								atvalue+=",";
							}
							break;
						case 'sthl':
							var standhlid=Ext.getCmp("standhlid").getValue();
							var vfactor=functionWizardScope.getCodesid("vfactor_arr");
							//var s_vfactor=functionWizardScope.getCodesid("s_vfactor_arr");
							var item=functionWizardScope.getCodesid("item");
							if(standhlid.length>0){
								var itemarr = standhlid.split(":"); 
								if(itemarr.length==7){
									atvalue+=itemarr[6];
								}
								if(vfactor.length>0){
									var vfactorarr = vfactor.split(":");
									if(vfactorarr.length==2){
										atvalue+=','+vfactorarr[1];
									}else{
										atvalue+=','+vfactor;
									}
								}else{
									atvalue+=',';
								}
								//就近函数只需要纵向指标即可，不需要纵向子指标的，纵向子指标计算出错
								/*if(s_vfactor.length>0){
									var s_vfactorarr = s_vfactor.split(":");
									if(s_vfactorarr.length==2){
										atvalue+=','+s_vfactorarr[1];
									}else{
										atvalue+=','+s_vfactor;
									}
								}else{*/
									if(vfactor.length<1){
										atvalue+=',';
									}
								//}
								if(item!=null&&item.length>0){
									var itemarr = item.split(":");
									if(itemarr.length==2){
										atvalue+=','+itemarr[1];
									}else{
										atvalue+=','+item;
									}
								}else{
									atvalue+=',';
								}
							}
							break;
						case 'strs':
							break;
						case 'vols':
							break;
						case 'volu':
							var datestr=functionWizardScope.getCodesid("datestr_arr");
							if(datestr!=null&&datestr.length>0){
								atvalue+=datestr;
							}
							break;
						case 'volp':
							var datestr=functionWizardScope.getCodesid("datestr_arr");
							if(datestr!=null&&datestr.length>0){
								atvalue+=datestr;
							}
							break;
						case 'volq':
							var variablename=functionWizardScope.getCodesid("variablename");
							if(variablename!=null&&variablename.length>0){
								atvalue+=variablename;
							}
							break;
						case 'pert':
							var variablename=functionWizardScope.getCodesid("personTax");
							if(variablename!=null&&variablename.length>0){
								atvalue+=variablename;
							}
							atvalue+=",";
							var variablename=functionWizardScope.getCodesid("perTaxRange");
							if(variablename!=null&&variablename.length>0){
								atvalue+=variablename;
							}
							break;
					}
				}
				if(start3.length>0){
				switch (start3){
						case '1':
							var dateexpression2=functionWizardScope.getCodesid("dateexpression2_arr");
							if(dateexpression2!=null&&dateexpression2.length>0){
								atvalue+=","+dateexpression2;
							}else{
								atvalue+=",";
							}
							break;
						case '2':
							var decimal=functionWizardScope.getCodesid("decimal");
							if(id=="V_vols7_2"){
								atvalue+=decimal;
							}else{
								atvalue+=","+decimal;
							}
							break;
						case '3':
							var conditions=functionWizardScope.getCodesid("conditions");
							if(id=="V_vols6_3_11_10_10"){
								if(conditions!=null&&conditions.length>0){
									atvalue+=conditions;
								}else{
									atvalue+="";
								}
							}else if(id=="V_vol9_3"){
								if(conditions!=null&&conditions.length>0){
									atvalue+=',"'+conditions+'"';
								}
							}
							else{
								if(conditions!=null&&conditions.length>0){
									atvalue+=","+conditions;
								}else{
									atvalue+=",";
								}
							}
							break;
						case '4':
							var numexpression2=functionWizardScope.getCodesid("numexpression2_arr");
							if(numexpression2!=null&&numexpression2.length>0){
								atvalue+=","+numexpression2;
							}else{
								atvalue+=",";
							}
							break;
						case '5':
							var codemax=functionWizardScope.getCodesid("code_maxarr");
							if(codemax!=null&&codemax.length>0){
								atvalue+=',"'+codemax+'"';
							}else{
								atvalue+=',""';
							}
							break;
						case '6':
							var strid=functionWizardScope.getCodesid("strid_arr");
							if(strid!=null&&strid.length>0){
								atvalue+=","+strid;
							}else{
								atvalue+=",";
							}
							break;
						case '7':
							var incrementalItemid=functionWizardScope.getCodesid("incrementalItem_arr");
							if(incrementalItemid!=null&&incrementalItemid.length>0){
								atvalue+=","+incrementalItemid;
							}else{
								atvalue+=",";
							}
							break;
						case '8':
							var statid=functionWizardScope.getCodesid("statid");
							if(statid!=null&&statid.length>0){
								atvalue+=","+statid;
							}else{
								atvalue+=",";
							}
							break;
						case '9':
							var template=functionWizardScope.getCodesid("templates");
							if(template!=null&&template.length>0){
								atvalue+=""+template;
							}else{
								atvalue+="";
							}
							break;
						case '10': 
							var template=functionWizardScope.getCodesid("partTimeJob_select");
							if(template!=null&&template.length>0){
								atvalue+='"'+template+'"';
							}else{
								atvalue+="";
							}
							break;	
						case '20':
							var strid2=functionWizardScope.getCodesid("strid2_arr");
							if(id=="V_volu9_20"){
								var datestr=functionWizardScope.getCodesid("datestr_arr");
								if(datestr!=null&&datestr.length>0){
									if(strid2!=null&&strid2.length>0){
										atvalue+=","+strid2;
									}
								}else{
									if(strid2!=null&&strid2.length>0){
										atvalue+=","+strid2;
									}else{
										atvalue+=",";
									}
								}
							
							}else{
							if(strid2!=null&&strid2.length>0){
								atvalue+=","+strid2;
							}else{
								atvalue+=",";
							}
							}
							
							break;
						case '22':
							var datetype_item=functionWizardScope.getCodesid("datetype_arr");
							if(datetype_item!=null && datetype_item.length>0){
								atvalue+=","+datetype_item.replace(/@/g,":");
							}
							break;
					}
				}
				if(start4.length>0){
					switch (start4){
						case '1':
							var direction=functionWizardScope.getCodesid("direction");
							if(direction!=null){
								atvalue+=","+direction;
							}else{
								atvalue+=",";
							}
							break;
						case '2':
							var initiation=functionWizardScope.getCodesid("initiation");
							if(initiation!=null){
								atvalue+=","+initiation;
							}else{
								atvalue+=",";
							}
							break;
						case '3':
							var way=functionWizardScope.getCodesid("way");
							if(way!=null&&way.length>0){
								atvalue+=","+way;
							}else{
								atvalue+=",";
							}
							break;
						case '4':
							var codemax=functionWizardScope.getCodesid("code_maxarr");
							if(codemax!=null&&codemax.length>0){
								atvalue+=',"'+codemax+'"';
							}else{
								atvalue+=',""';
							}
							break;
						case '5':
							var codemin=functionWizardScope.getCodesid("code_minarr");
							if(codemin!=null&&codemin.length>0){
								atvalue+=',"'+codemin+'"';
							}else{
								atvalue+=',""';
							}
							break;
						case '6':
							var strexpression_item=functionWizardScope.getCodesid("strexpression_arr");
							if(strexpression_item!=null&&strexpression_item.length>0){
								atvalue+=','+strexpression_item;
							}else{
								atvalue+=',""';
							}
							break;
						case '7':
							var hdayslogo=functionWizardScope.getCodesid("hdayslogo");
							if(hdayslogo!=null&&hdayslogo.length>0){
								atvalue+=','+hdayslogo;
							}else{
								atvalue+=',""';
							}
							break;
						case '9':
							var itemid_item=functionWizardScope.getCodesid("itemid_arr");
							if(itemid_item!=null&&itemid_item.length>0){
								atvalue+=','+itemid_item;
							}else{
								atvalue+=',';
							}
							break;
						case '10':
							var itemid_item=functionWizardScope.getCodesid("conditions");
							if(itemid_item!=null&&itemid_item.length>0){
								atvalue+=','+itemid_item;
							}else{
								atvalue+=',';
							}
							break;
						case '11':
							var itemid_item=functionWizardScope.getCodesid("conditions1");
							if(id=="V_vols6_3_11_10_10"){
								var conditions=functionWizardScope.getCodesid("conditions");
								if(conditions!=null&&conditions.length>0){
									var itemid_item2=functionWizardScope.getCodesid("conditions2");
									var itemid_item3=functionWizardScope.getCodesid("conditions3");
									if((itemid_item!=null&&itemid_item.length>0)||(itemid_item2!=null&&itemid_item2.length>0)||(itemid_item3!=null&&itemid_item3.length>0)){
										atvalue+=':'+itemid_item;
									}
								}else{
									atvalue+="";
								}
							}else{
								if(itemid_item!=null&&itemid_item.length>0){
									atvalue+=','+itemid_item;
								}else{
									atvalue+=',';
								}
							}
							break;
					}
				}
				if(id=="V_vol1_8_9_1_1"){
					atvalue+=',0';
				}
				if(start5.length>0){
					switch (start5){
						case '1':
							var rangeid=functionWizardScope.getCodesid("rangeid");
							if(rangeid!=null&&rangeid.length>0){
								atvalue+=','+rangeid;
							}else{
								if(id=="V_vol1_8_9_1_1"){
									atvalue+=',0';
								}else
									atvalue+=',""';
							}
							break;
						case '5':
							var codemin=functionWizardScope.getCodesid("code_minarr");
							if(codemin!=null&&codemin.length>0){
								atvalue+=',"'+codemin+'"';
							}else{
								atvalue+=',""';
							}
							break;
						case '10':
							var itemid_item=functionWizardScope.getCodesid("conditions2");
							if(id=="A_vol9_6_2_10_2"){
								if(itemid_item!=null&&itemid_item.length>0){
									atvalue+=','+"\""+itemid_item+"\"";
								}else{
									atvalue+=',\"\"';
								}
								break;
							}
							if(id=="V_vols6_3_11_10_10"){
								var conditions=functionWizardScope.getCodesid("conditions");
								if(conditions!=null&&conditions.length>0){
									var itemid_item1=functionWizardScope.getCodesid("conditions1");
									var itemid_item3=functionWizardScope.getCodesid("conditions3");
									if((itemid_item!=null&&itemid_item.length>0)||(itemid_item1!=null&&itemid_item1.length>0)||(itemid_item3!=null&&itemid_item3.length>0)){
										atvalue+=','+itemid_item;
									}
								}else{
									atvalue+="";
								}
								break;
							}
							if(itemid_item!=null&&itemid_item.length>0){
								atvalue+=','+itemid_item;
							}else{
								atvalue+=',';
							}
							break;
					}
				}
				if(start6.length>0){
					switch (start6){
						case '1':
							var conditions=functionWizardScope.getCodesid("conditions");
							if(conditions!=null&&conditions.length>0){
								atvalue+=','+conditions;
							}else{
								if(id=="V_vol1_8_9_1_1"){
									atvalue+=',1=1';
								}else{
									atvalue+=',""';
								}
							}
							break;
						case '2':
							var sortingnum=functionWizardScope.getCodesid("sortingnum");
							if(sortingnum!=null&&sortingnum.length>0){
								atvalue+=','+sortingnum;
							}else{
								atvalue+=',0';
							}
							break;
						case'10':
							var itemid_item=functionWizardScope.getCodesid("conditions3");
							if(id=="V_vols6_3_11_10_10"){
								var conditions=Ext.getCmp("conditions").getValue();
								if(conditions!=null&&conditions.length>0){
									var itemid_item2=Ext.getCmp("conditions2").getValue();
									var itemid_item1=Ext.getCmp("conditions1").getValue();
									if((itemid_item!=null&&itemid_item.length>0)||(itemid_item2!=null&&itemid_item2.length>0)||(itemid_item1!=null&&itemid_item1.length>0)){
										atvalue+=','+itemid_item;
									}
								}else{
									atvalue+="";
								}
								break; 
							}
							break;
					}
				}
			}
			return atvalue;
		},
		getCodesid:function(code_arr){
			var codeid="";
			var codesetid_arr = Ext.getCmp(code_arr).getValue();
			if(codesetid_arr==null||codesetid_arr=='empty'){
				return "";
			}else{
				codeid = codesetid_arr;
			}
			if(Ext.isNumber(codeid))
				return codeid;
			else
				return functionWizardScope.toggleText(codeid);
		},
		toggleText:function(itemvalue){
			var itemarr = itemvalue.split(":");
			if(itemarr.length==2){
				return itemarr[1];
			}else{
				return itemarr[0];
			}
		}
});