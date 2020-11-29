/**
 * 填报期间范围
 * haosl
 */
Ext.define('WorkPlanConfigUL.PeriodRange',{
	extend:"Ext.panel.Panel",
	title:wp.param.periodrange,
	bodyStyle:"border-width:1px 1px 0 0",
	scrollable:true, 
	height:520,
	unselectRange:undefined,
	constructor:function(){
		wp_periodRange = this;
		wp_periodRange.callParent(arguments);
		wp_periodRange.init();
	},
	init:function(){
		wp_periodRange.crateTable();
		//刷新页面
		wp_periodRange.on('render',wp_periodRange.validateText,wp_periodRange);
		wp_periodRange.on('render',wp_periodRange.createBtns,wp_periodRange);
		wp_periodRange.on('afterrender',wp_periodRange.refreshPlan,wp_periodRange);
	},
	/** 创建表格 **/
	crateTable:function(){
		var html ="<form id='periodRangeForm'>";
		html += "<table class='tab'>" +
					"<tr class='wp_head' style='background-color:"+workPlanConfig.headerColor+";'>" +
					"<td colspan='2' width='170px'>计划与总结类型</td>" +
					"<td width='70px'>启用</td><td width='220px'>填报期限</td>" +
					"<td width='230px'>填报周期</td></tr>";//表头
		//年度计划和总结
		html+="<tr class='wp_data'>" +
			      "<td rowspan='2'>年度</td>" +
			      "<td>年度计划</td>" +
			      "<td><input id='p0' class='plansumy' type='checkbox' name='p0' value='' /></td>" +
				  "<td>上年末<input id='p0pre' type='text' value='30' name='p0pre'/>天 至 本年初<input id='p0now' value='30' type='text' name='p0now'/>天</td>" +
				  "<td rowspan='2'></td>" +
			  "</tr>" +
			  "<tr class='wp_data'>" +
			  	"<td>年度总结</td><td><input id='s0' class='plansumy' type='checkbox' name='s0' value=''/></td>" +
				"<td>上年末<input id='s0pre' type='text' name='s0pre' value='15'/>天 至 本年初<input id='s0now' type='text' name='s0now' value='30'/>天</td>" +
			  "</tr>";
		//半年计划和总结
		html+="<tr class='wp_data'>" +
					"<td rowspan='2'>半年</td><td>半年计划</td>" +
					"<td><input id='p1' class='plansumy' type='checkbox' name='p1' value='' onclick='wp_periodRange.setRange(this,\"p1\")'/></td>" +
					"<td>上期末<input id='p1pre' type='text' name='p1pre' value='15'/>天 至 本期前<input id='p1now' type='text' name='p1now' value='15'/>天</td>" +
					"<td rowspan='2'>" +
						"<div class='cheackb' id='helfyear'>"+
							"<input id='helfyear1' type='checkbox' name='p1cycle' value='1'/><label for='helfyear1' style='margin-right:5px'>上半年</label>" +
							"<input id='helfyear2' type='checkbox' name='p1cycle' value='2'/><label for='helfyear2'>下半年</label>" +
					"</td>" +
				"</tr>" +                                         
				"<tr class='wp_data'><td>半年总结</td><td><input id='s1' class='plansumy' type='checkbox' name='s1' value='' onclick='wp_periodRange.setRange(this,\"p1\")'/></td>" +
				"<td>上期末<input id='s1pre' type='text' name='s1pre' value='15'/>天 至 本期前<input id='s1now' type='text' name='s1now' value='15'/>天</td>" +
				"</tr>";
		//季度计划和总结
		html+="<tr class='wp_data'>" +
				"<td rowspan='2'>季度</td><td>季度计划</td><td><input class='plansumy' id='p2' type='checkbox' name='p2' value=''/ onclick='wp_periodRange.setRange(this,\"p2\")'></td>" +
				"<td>上季末<input id='p2pre' type='text' name='p2pre' value='15'/>天 至 本季前<input id='p2now' type='text' name='p2now' value='15'/>天</td>" +
					"<td rowspan='2'>" +
						"<div id='jidu' class='cheackb'>"+
							"<input id='jidu1' type='checkbox' name='p2cycle' value='1'/><label for='jidu1' style='margin-right:5px'>一季度</label>" +
							"<input id='jidu2' type='checkbox' name='p2cycle' value='2'/><label for='jidu2'>二季度</label>" +
							"<input id='jidu3' type='checkbox' name='p2cycle' value='3'/><label for='jidu3' style='margin-right:5px'>三季度</label>" +
							"<input id='jidu4' type='checkbox' name='p2cycle' value='4'/><label for='jidu4'>四季度</label>" +
						"</div>"+
					"</td>" +
			  "</tr>" +
			  "<tr class='wp_data'>" +
				  "<td>季度总结</td><td><input id='s2' class='plansumy' type='checkbox' name='s2' value='' onclick='wp_periodRange.setRange(this,\"p2\")'/></td>" +
				  "<td>上季末<input id='s2pre' type='text' name='s2pre' value='15'/>天 至 本季前<input id='s2now' type='text' name='s2now' value='15'/>天</td>" +
			  "</tr>";
		//月度计划和总结
		html+="<tr class='wp_data'>" +
				"<td rowspan='2'>月度</td><td>月度计划</td><td><input id='p3' class='plansumy' type='checkbox' name='p3' onclick='wp_periodRange.setRange(this,\"p3\")'/></td>" +
				"<td>上月末<input id='p3pre' type='text' name='p3pre' value='5'/>天 至 本月前<input id='p3now' type='text' name='p3now' value='5'/>天</td>" +
					"<td rowspan='2'>" +
						"<div id='yuedu' class='cheackb'>"+
							"<input id='moth1' type='checkbox' name='p3cycle' value='1'/><label for='moth1'>一月</label>" +
							"<input id='moth2' type='checkbox' name='p3cycle' value='2'/><label for='moth2'>二月</label>" +
							"<input id='moth3' type='checkbox' name='p3cycle' value='3'/><label for='moth3'>三月</label>" +
							"<input id='moth4' type='checkbox' name='p3cycle' value='4'/><label for='moth4'>四月</label>" +
							"<input id='moth5' type='checkbox' name='p3cycle' value='5'/><label for='moth5'>五月</label>" +
							"<input id='moth6' type='checkbox' name='p3cycle' value='6'/><label for='moth6'>六月</label>" +
							"<input id='moth7' type='checkbox' name='p3cycle' value='7'/><label for='moth7'>七月</label>" +
							"<input id='moth8' type='checkbox' name='p3cycle' value='8'/><label for='moth8'>八月</label>" +
							"<input id='moth9' type='checkbox' name='p3cycle' value='9'/><label for='moth9'>九月</label>" +
							"<input id='moth10' type='checkbox' name='p3cycle' value='10'/><label for='moth10'>十月</label>" +
							"<input id='moth11' type='checkbox' name='p3cycle' value='11'/><label for='moth11'>十一</label>" +
							"<input id='moth12' type='checkbox' name='p3cycle' value='12'/><label for='moth12'>十二</label>" +
						"</div>"+       
					"</td>" +
			  "</tr>" +
			  "<tr class='wp_data'>" +
				  "<td>月度总结</td><td><input id='s3' class='plansumy' type='checkbox' name='s3' value='' onclick='wp_periodRange.setRange(this,\"p3\")'/></td>" +
				  "<td>上月末<input id='s3pre' type='text' name='s3pre' value='5'/>天 至 本月前<input id='s3now' type='text' name='s3now' value='5'/>天</td>" +
			  "</tr>";
		//周计划和总结
		html+="<tr class='wp_data'>" +
			      "<td rowspan='2'>周</td><td>周计划</td><td><input id='p4' class='plansumy' type='checkbox' name='p4' value=''/></td>" +
				  "<td>上周末<input id='p4pre' type='text' name='p4pre' value='3'/>天 至 本周前<input id='p4now' type='text' name='p4now' value='1'/>天</td>" +
				  "<td rowspan='2'></td>" +
			  "</tr>" +
			  "<tr class='wp_data'>" +
			  	"<td>周总结</td><td><input id='s4' class='plansumy' type='checkbox' name='s4' value=''/></td>" +
				"<td>上周末<input id='s4pre' type='text' name='s4pre' value='3'/>天 至 本周前<input id='s4now' type='text' name='s4now' value='1'/>天</td>" +
			  "</tr>";
		//日志
		html+="<tr class='wp_data'>" +
				"<td>日志</>"+
			  	"<td>工作日志</td><td><input id='p5' class='plansumy' type='checkbox' name='p5' value=''/></td>" +
				"<td>超过当日<input id='p5now' type='text' name='p5now' value='3'/>天内可以填写 </td><td></td>" +
			  "</tr>";
		html += "</table></form>";	
		html += "<div style='width:100%;text-align:center;'><div style='display:inline;' id='button11'></div><div style='display:inline;'  id='button22'></div></div>";
		wp_periodRange.html = html;
	},
	/**获得表单数据**/
	getFormMap:function(){
		var plansumy = Ext.query("input[class=plansumy]")//计划和总结是否启用
		var list = new Array();
		var isSetplan = false;//是否设置了方案
		for(i in plansumy){
			var el = plansumy[i];
			if(el.checked){
				isSetplan = true;
				var hm = new HashMap();
				hm.put("id",el.id);
				if(el.id!="p5"){
					var pre = document.getElementById(el.id+"pre").value;
					if(!pre || pre==""){
						wp_periodRange.unselectRange = el.parentNode.previousSibling.innerText;
						return "1";//区间参数未设置不能提交
					}
					hm.put(el.id+"pre",pre);
				}
				var now = document.getElementById(el.id+"now").value;
				if(!now || now==""){
					wp_periodRange.unselectRange = el.parentNode.previousSibling.innerText;
					return "1";//区间参数未设置时不能提交
				}
				var cycleName ="p"+(el.id+"cycle").substring(1);
				var cycles = Ext.query("input[name="+cycleName+"]");//模糊查询
			
				hm.put(el.id+"now",now);
				var str = "";
				if(cycles && cycles.length>0){
					var b = false;
					Ext.Array.each(cycles,function(cycleEl,index){
						if(!cycleEl.checked){
							return;
						}else{
							b = true;
							str+=cycleEl.value+",";
						}
					})
					if(!b){
						//未勾选填报周期的项
						wp_periodRange.unselectRange = el.parentNode.previousSibling.innerText
						return '2';//勾选方案时，未勾选填报周期，则不能提交
					}
				}
				if(str!=""){
					str=str.substring(0,str.length-1);
					hm.put(el.id+"cycle",str);
				}
				list.push(hm);
			}
		}; 
		if(!isSetplan)
			return '0';//未设置方案  无需保存 
		return list;
	},
	/** 校验输入框的值是否正确 */
	validateText:function(){
		//文本框控制只能输入1-100的正整数
		var textfields = Ext.query("#periodRangeForm input[type=text]");
		Ext.Array.each(textfields,function(tf){
			tf.onkeyup=function(){this.value=this.value.replace(/\D/g,'');}
			tf.onafterpaste=function(){this.value=this.value.replace(/\D/g,'');}
			tf.onblur = function(){
                if(isNaN(this.value) || parseInt(this.value)>400 || parseInt(this.value)<1){
                    this.value = "";
                    Ext.showAlert("请输入1-400的整数！");
                }
					
			}
		});
		
	},
	createBtns:function(){
		Ext.widget('button', {
			id:'saveBtn',
			text:'保存',
			hidden:false,
			width:80,
			renderTo:'button11',
			handler:function(){
				var data = wp_periodRange.getFormMap();
				if(data=='0'){
					Ext.showAlert("请勾选要启用的计划与总结类型！");
					return;//没有设置，不需要保存
				}else if(data=='1' && wp_periodRange.unselectRange){
					Ext.showAlert("【"+wp_periodRange.unselectRange+"】 未填写完整的填报期限！");
					return;
				}
				else if(data=='2' && wp_periodRange.unselectRange){
					Ext.showAlert("【"+wp_periodRange.unselectRange+"】未勾选填报周期！");
					return;
				}
				var map = new HashMap();
				map.put("opt","2")//保存
				map.put("list",data);
				Rpc({functionId:'WP20000002',async:false,success:function(form,action){
					
					var result = Ext.decode(form.responseText);
					if(result.sucflag)
						 msg='保存成功！'
					else
						msg=result.message;
					wp_periodRange.refreshPlan();
					Ext.showAlert(msg);
				}},map);
			}
		});
		Ext.widget('button', {
					
			id:'cancelBtn',
			text:'重置',
			hidden:false,
			renderTo:'button22',
			margin:'0 0 0 10',
			width:80,
			handler:function(){
				//取消修改
				Ext.showConfirm("是否取消修改？", function(id){
					if(id=='yes')
						wp_periodRange.cancelSetting();
				});
			}
		});
	},
	/**
	 * 刷新参数配置页面，回显数据
	 */
	refreshPlan:function(){
		wp_periodRange.setDefaultConfig();//重置
		var map = new HashMap();
		map.put("opt","1");
		Rpc({functionId:'WP20000002',async:false,success:function(form,action){
			var data = Ext.decode(form.responseText).data;
			for(var i in data){
				var map = data[i];
				for(var key in map){
					var planSummy  = document.querySelector("#"+key);
					planSummy.checked  = true;//选中方案中的数据
					var pre = map[key].pre;
					var now = map[key].now;
					var cycle = map[key].cycle;
					if(pre && pre.length>0){
						var preEl = document.querySelector("#"+key+"pre");
						if(preEl)
							preEl.value=pre;
					}
					if(now && now.length>0){
						var nowEl = document.querySelector("#"+key+"now");
						if(nowEl)
							nowEl.value=now;
					}
					cycle = ","+cycle+",";
					var key_tmp = "p"+key.substring(1,key.length);
					var cycleEls = Ext.query("input[name="+key_tmp+"cycle]");
					for(var c in cycleEls){
						var cycleEl = cycleEls[c];
						if(cycleEl && cycle.indexOf(','+cycleEl.value+',')>-1 &&!cycleEl.checked)
							cycleEl.checked = true;
					}
				}
			}
		}},map);
	},
	/** 清空设置 **/
	setDefaultConfig:function(){
		var chbEls = Ext.query("input[type=checkbox]");
		for(var x in chbEls){
			chbEls[x].checked = false;
		}
		var textEl = Ext.query("input[type=text]");
		for(var y in textEl){
			textEl.value = "";
		}
	},
	/** 勾选启用计划与总结时默认勾选填报周期 **/
	setRange:function(self,key){
		var chbEls = Ext.query("input[name="+key+"cycle]");
		if(self.id == key){//计划
			var id = key.substring(1,key.length);
			var summy = document.querySelector("#s"+id);
			if(self.checked && !summy.checked){
				for(var x in chbEls){
					chbEls[x].checked = true;
				}
			}
		}else{//总结
			var plan = document.querySelector("#"+key);
			if(self.checked && !plan.checked){
				for(var x in chbEls){
					chbEls[x].checked = true;
				}
			}
		}	
	},
	/** 重置 **/
	cancelSetting:function(){
	    var map = new HashMap();
	    map.put("opt","3");
        Rpc({functionId:'WP20000002',async:false,success:function(){
            var rightPanel = Ext.getCmp('rightPanel');
            rightPanel.removeAll(); //清空右侧页面布局
            rightPanel.add(workPlanConfig.createPeriodrange())
        }},map);
	}
})