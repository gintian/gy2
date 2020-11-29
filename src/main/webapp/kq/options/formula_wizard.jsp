<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/ajax/common.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<HTML>
	<SCRIPT LANGUAGE="JavaScript"><!--
	
		//系统指标类型(A,D,N,M)
		//传入数据格式为 A 姓名,N 年龄,D 出生日期 
		var  dataInfo = window.dialogArguments;//数据集合  		
		var fieldTypeArray = Array(); //指标类型标识集合
		var fieldArray = Array(); //指标集合
		
		for(var i=0;i<dataInfo.length;i++){
			var temp =dataInfo[i].toString();
			fieldTypeArray[i]=temp.substr(0,1);
			fieldArray[i]=temp.substr(2,temp.length-1);
		}
		
		//设置参数选择框数据集合
		function setOption(obj , flag){
			obj.length=0;
			var n =-1;
			
			if(flag == "A"){//字符型
				for(var i =0;  i < fieldTypeArray.length; i++){
					if(fieldTypeArray[i] == "A"){
						n++;
						obj[n] = new Option(fieldArray[i],fieldArray[i]);
					}
				}
			}else if(flag == "D"){//日期型
				for(var i =0;  i < fieldTypeArray.length; i++){
					if(fieldTypeArray[i] == "D"){
						n++;
						obj[n] = new Option(fieldArray[i],fieldArray[i]);
					}
				}
			}else if(flag == "N"){ //数值型
				for(var i =0;  i < fieldTypeArray.length; i++){
					if(fieldTypeArray[i] == "N"){
						n++;
						obj[n] = new Option(fieldArray[i],fieldArray[i]);
					}
				}
			}else if(flag == "M"){//备注型
				for(var i =0;  i < fieldTypeArray.length; i++){
					if(fieldTypeArray[i] == "M"){
						n++;
						obj[n] = new Option(fieldArray[i],fieldArray[i]);
					}
				}
			}else if(flag == "ALL"){//所有
				for(var i =0;  i < fieldTypeArray.length; i++){
					n++;
					obj[n] = new Option(fieldArray[i],fieldArray[i]);
				}
			}
		
		}
		
		//改变函数类型后的联动反应
		function changeFunctionType(obj){
		
			// 数值 1 字符 2 日期 3 不定型 4 常量 5 逻辑 6 算术 7 关系 8 转换 10 其他 9
			var funTypeValue = obj.options[obj.selectedIndex].value;//选中的函数类型
			
			if(funTypeValue == "1"){//数值
			
				fun.length=0;
				fun[0]=new  Option("N 取整(数值表达式)","<bean:message key="kq.formula.int"/>");
				fun[1]=new  Option("N 取余数(数值表达式,数值表达式)","取余数");				
				fun[2]=new  Option("N 四舍五入(数值表达式,整数)","<bean:message key="kq.wizard.round"/>");
				fun[3]=new  Option("N 三舍七入(数值表达式)","<bean:message key="kq.wizard.ssqr"/>");
				fun[4]=new  Option("N 逢分进元(数值表达式)","<bean:message key="kq.wizard.ffjy"/>");
				fun[5]=new  Option("N 逢分进角(数值表达式)","<bean:message key="kq.wizard.ffjj"/>");
				
				changeFunction();
				
				
			}else if(funTypeValue=="2"){//字符
			
				fun.length=0;
				fun[0]=new  Option("A 去空格(字符串表达式)","<bean:message key="kq.wizard.qnull"/>");
				fun[1]=new  Option("A 去左空格(字符串表达式)","<bean:message key="kq.wizard.lnull"/>");
				fun[2]=new  Option("A 去右空格(字符串表达式)","<bean:message key="kq.wizard.rnull"/>");
				fun[3]=new  Option("A 子串(字符串表达式,整数,整数)","<bean:message key="kq.wizard.zbunch"/>");
				fun[4]=new  Option("N 串长(字符串表达式)","<bean:message key="kq.wizard.bunchl"/>");
				fun[5]=new  Option("A 左串(字符串表达式,整数)","<bean:message key="kq.wizard.lbunch"/>");
				fun[6]=new  Option("A 右串(字符串表达式,整数)","<bean:message key="kq.wizard.rbunch"/>");
				
				changeFunction();
			
			
			}else if(funTypeValue=="3"){//日期
			
				fun.length=0;
				fun[0]=new  Option("N 年(日期)","<bean:message key="kq.wizard.year"/>");
				fun[1]=new  Option("N 月(日期)","<bean:message key="kq.wizard.month"/>");
				fun[2]=new  Option("N 日(日期)","<bean:message key="kq.wizard.day"/>");
				fun[3]=new  Option("N 季度(日期)","<bean:message key="kq.wizard.quarter"/>");
				fun[4]=new  Option("N 周(日期)","<bean:message key="kq.wizard.week"/>");
				fun[5]=new  Option("N 星期(日期)","<bean:message key="kq.wizard.weeks"/>");
				fun[6]=new  Option("D 今天","<bean:message key="kq.wizard.today"/>");
				fun[7]=new  Option("N 本周 或 本周()","<bean:message key="kq.wizard.bweek"/>");
				fun[9]=new  Option("N 本月 或 本月()","<bean:message key="kq.wizard.bquarter"/>");
				fun[8]=new  Option("N 本季度 或 本季度()","<bean:message key="kq.wizard.bmonth"/>");
				fun[10]=new  Option("N 今年 或 今年()","<bean:message key="kq.wizard.byear"/>");
				fun[11]=new  Option("D 截止日期 或 截止日期()","<bean:message key="kq.wizard.edate"/>");
				fun[12]=new  Option("N 年龄(日期)","<bean:message key="kq.wizard.age"/>");
				fun[13]=new  Option("N 工龄(日期)","<bean:message key="kq.wizard.gage"/>");
				fun[14]=new  Option("N 到月年龄(日期)","<bean:message key="kq.wizard.tmonth"/>");
				fun[15]=new  Option("N 年数(日期1,日期2)","<bean:message key="kq.wizard.years"/>");
				fun[16]=new  Option("N 月数(日期1,日期2)","<bean:message key="kq.wizard.months"/>");
				fun[17]=new  Option("N 天数(日期1,日期2)","<bean:message key="kq.wizard.days"/>");
				fun[18]=new  Option("N 季度数(日期1,日期2)","<bean:message key="kq.wizard.quarters"/>");
				fun[19]=new  Option("N 周数(日期1,日期2)","<bean:message key="kq.wizard.weekss"/>");
				fun[20]=new  Option("D 增加年数(日期,整数)","<bean:message key="kq.wizard.ayear"/>");
				fun[21]=new  Option("D 增加月数(日期,整数)","<bean:message key="kq.wizard.amonth"/>");
				fun[22]=new  Option("D 增加天数(日期,整数)","<bean:message key="kq.wizard.aday"/>");
				fun[23]=new  Option("D 增加季度数(日期,整数)","<bean:message key="kq.wizard.aquarter"/>");
				fun[24]=new  Option("D 增加周数(日期,整数)","<bean:message key="kq.wizard.aweek"/>");
				
				changeFunction();
				
			}else if(funTypeValue=="4"){//不定型
			
				fun.length=0;
				fun[0]=new  Option("如果 那么 否则 结束","<bean:message key="kq.wizard.ifa"/>");
				fun[1]=new  Option("分情况 如果 Lexp1 那么 exp1 如果 Lexp2 那么 exp2 [否则 expn] 结束","<bean:message key="kq.wizard.thing"/>");
				fun[2]=new  Option("较大值(exp1,exp2)","<bean:message key="kq.wizard.max"/>");
				fun[3]=new  Option("较小值(exp1,exp2)","<bean:message key="kq.wizard.min"/>");
				fun[4]=new  Option("取 指标名称 [最近第|最初第] 整数 条记录","<bean:message key="kq.wizard.qname"/>");
				fun[5]=new  Option("统计 指标名称 满足 条件 [的最初第一条记录|的最近第一条记录|的最大值|的最小值|的总和|的平均值|的个数]","<bean:message key="kq.wizard.stas"/>");
				
				changeFunction();
				
			}else if(funTypeValue=="5"){//常量
			
				fun.length=0;
				fun[0]=new  Option("真","<bean:message key="kq.wizard.true"/>");
				fun[1]=new  Option("假","<bean:message key="kq.wizard.flase"/>");
				fun[2]=new  Option("空(日期)","<bean:message key="kq.wizard.null"/>");
				fun[3]=new  Option("#2000.3.22#表示2000年3月22日","#2000.3.22#");
				fun[4]=new  Option("#3.22#本年3月22日","#3.22#");
				fun[5]=new  Option("\"张三\"表示姓名为张三","\"张三\"");
				
				changeFunction();
				
			}else if(funTypeValue=="6"){//逻辑
			
				fun.length=0;
				fun[0]=new  Option("<bean:message key="kq.wizard.even"/>","<bean:message key="kq.wizard.even"/>");
				fun[1]=new  Option("<bean:message key="kq.wizard.and"/>","<bean:message key="kq.wizard.and"/>");
				fun[2]=new  Option("<bean:message key="kq.wizard.not"/>","<bean:message key="kq.wizard.not"/>");
				
				changeFunction();
				
			}else if(funTypeValue=="7"){//算术
				
				fun.length=0;
				fun[0]=new  Option("<bean:message key="kq.wizard.add"/>","<bean:message key="kq.wizard.add"/>");
				fun[1]=new  Option("<bean:message key="kq.wizard.dec"/>","<bean:message key="kq.wizard.dec"/>");
				fun[2]=new  Option("<bean:message key="kq.wizard.mul"/>","<bean:message key="kq.wizard.mul"/>");
				fun[3]=new  Option("<bean:message key="kq.wizard.divide"/>","<bean:message key="kq.wizard.divide"/>");
				//fun[4]=new  Option("<bean:message key="kq.wizard.divs"/>","<bean:message key="kq.wizard.divs"/>");
				fun[4]=new  Option("\\(整除)","\\");
				fun[5]=new  Option("<bean:message key="kq.wizard.div"/>","<bean:message key="kq.wizard.div"/>");
				fun[6]=new  Option("<bean:message key="kq.wizard.over"/>","<bean:message key="kq.wizard.over"/>");
				fun[7]=new  Option("MOD(求余)","<bean:message key="kq.wizard.mod"/>");
				
				changeFunction();
				
			}else if(funTypeValue=="8"){//关系
			
				fun.length=0;
				
				fun[0]=new  Option("=(等于)","=");
				fun[1]=new  Option(">(大于)",">");
				fun[2]=new  Option(">=(大于等于)",">=");
				fun[3]=new  Option("<(小于)","<");
				fun[4]=new  Option("<=(小于等于)","<=");
				fun[5]=new  Option("<>(不等于)","<>");
				fun[6]=new  Option("LIKE(包含)","LIKE");
				
				changeFunction();
				
			}else if(funTypeValue=="9"){//其他
				
				fun.length=0;
				
				fun[0]=new  Option("( )括号","( )");
				fun[1]=new  Option("[ ]中括号","[ ]");
				fun[2]=new  Option("{ }大括号","{ }");
				fun[3]=new  Option("//注释标识","//");
				
				changeFunction();
				
			}else if(funTypeValue=="10"){//转换
			
				fun.length=0;
				fun[0]=new  Option("D 字符转日期(字符串表达式)","<bean:message key="kq.wizard.ctod"/>");
				fun[1]=new  Option("N 字符转数值(字符串表达式)","<bean:message key="kq.wizard.cton"/>");
				fun[2]=new  Option("A 日期转字符(日期)","<bean:message key="kq.wizard.dtoc"/>");
				fun[3]=new  Option("A 数值转字符(数值表达式)","<bean:message key="kq.wizard.ntoc"/>");
				fun[4]=new  Option("A 代码转名称(指标代码)","<bean:message key="kq.wizard.ctom"/>");
				fun[5]=new  Option("A ~代码指标 ","<bean:message key="kq.wizard.ctos"/>");
				
				changeFunction();
				
			}
		} 
		
		//改变函数后的联动反应
		function changeFunction(){
			var funValue = ${"fun"}.value;
			
			if(funValue == ""){
				funValue = "<bean:message key="kq.formula.int"/>";
			}
			
			//alert(funValue);
			
			if(funValue == "<bean:message key="kq.formula.int"/>"){//取整
			
				$("param_funinfo").innerHTML="取整(数值表达式)";
				$("help_funinfo").innerHTML="说明:INT(数值表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="数值表达式";
			
				setOption($('expr1_option_1'),'N');
				
				$('help_fundesc').innerHTML="Int(129.11)<br>返回结果:129"
				
			}else if(funValue == "取余数"){//取余数
			
				$("param_funinfo").innerHTML="取余数(数值表达式,数值表达式)";
				$("help_funinfo").innerHTML="说明:FUNCMOD(数值表达式,数值表达式)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr2_text','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="数值表达式";
				$('expr2_desc').innerHTML="数值表达式";
				
				setOption($('expr1_option_1'),'N');
				setOption($('expr2_option_1'),'N');
				
				$('help_fundesc').innerHTML="取余数(10,3)<br>返回结果为:1";
				
			}else if(funValue == "<bean:message key="kq.wizard.round"/>"){//四舍五入
			
				$("param_funinfo").innerHTML="四舍五入(数值表达式,整数)";
				$("help_funinfo").innerHTML="说明:ROUND(数值表达式,保留小数点位数)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_option');

				$('expr1_desc').innerHTML="数值表达式";
				$('expr2_desc').innerHTML="小数点位数";
				
				setOption($('expr1_option_1'),'N');
				
				$('help_fundesc').innerHTML="Round(129.166,2)<br>返回结果为:129.17";
				
			}else if(funValue == "<bean:message key="kq.wizard.ssqr"/>"){//三舍七入
			
				$("param_funinfo").innerHTML="三舍七入(数值表达式)";
				$("help_funinfo").innerHTML="说明:SANQI:三舍七入,其他为0.5";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="数值表达式";
			
				setOption($('expr1_option_1'),'N');
				
				$('help_fundesc').innerHTML="SANQI(129.34)<br>返回结果为:129.5";
				
			}else if(funValue == "<bean:message key="kq.wizard.ffjy"/>"){//逢分进元
			
				$("param_funinfo").innerHTML="逢分进元(数值表达式)";
				$("help_funinfo").innerHTML="说明:YUAN(数值表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="数值表达式";
			
				setOption($('expr1_option_1'),'N');
				
				$('help_fundesc').innerHTML="YUAN(129.02)<br>返回结果为:130";
				
			}else if(funValue == "<bean:message key="kq.wizard.ffjj"/>"){//逢分进角
			
				$("param_funinfo").innerHTML="逢分进角(数值表达式)";
				$("help_funinfo").innerHTML="说明:JIAO(数值表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="数值表达式";
			
				setOption($('expr1_option_1'),'N');
				
				$('help_fundesc').innerHTML="JIAO(129.11)<br>返回结果为:129.2";
				
			}else if(funValue == "<bean:message key="kq.wizard.qnull"/>"){//去空格
			
				$("param_funinfo").innerHTML="去空格(字符串表达式)";
				$("help_funinfo").innerHTML="说明:TRIM(字符串表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
			
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="Trim(\"   AbcD   \")<br>返回结果为:\"AbcD\"";
				
			}else if(funValue == "<bean:message key="kq.wizard.lnull"/>"){//去左空格
			
				$("param_funinfo").innerHTML="去左空格(字符串表达式)";
				$("help_funinfo").innerHTML="说明:LTRIM(字符串表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
			
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="LTrim((\"   AbcD   \")<br>返回结果为:\"AbcD   \")";
				
			}else if(funValue == "<bean:message key="kq.wizard.rnull"/>"){//去右空格
			
				$("param_funinfo").innerHTML="去右空格(字符串表达式)";
				$("help_funinfo").innerHTML="说明:RTRIM(字符串表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
			
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="RTrim(\"   AbcD   \")<br>返回结果为:\"   AbcD\")";
				
			}else if(funValue == "<bean:message key="kq.wizard.zbunch"/>"){//子串
			
				$("param_funinfo").innerHTML="子串(字符串表达式,整数,整数)";
				$("help_funinfo").innerHTML="说明:SUBSTR(字符串表达式,起始位,长度)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text','expr3_desc','expr3_text');
				Element.hide('expr1_text','expr2_option','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
				$('expr2_desc').innerHTML="起  始  位";
				$('expr3_desc').innerHTML="长      度";
				
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="SubStr(\"AykchrbcD\",2,5)<br>返回结果为：\"ykchr\"";
				
			}else if(funValue == "<bean:message key="kq.wizard.bunchl"/>"){//串长
			
				$("param_funinfo").innerHTML="串长(字符串表达式)";
				$("help_funinfo").innerHTML="说明:LEN(字符串表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
			
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="LEN(\"ykchrbcD\")<br>返回结果为：8";
				
			}else if(funValue == "<bean:message key="kq.wizard.lbunch"/>"){//左串
			
				$("param_funinfo").innerHTML="左串(字符串表达式,整数)";
				$("help_funinfo").innerHTML="说明:LEFT(字符串表达式,长度)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr2_option','expr3_option','expr3_desc','expr3_text');

				$('expr1_desc').innerHTML="字符表达式";
				$('expr2_desc').innerHTML="长      度";
				
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="Left(\"ykchrbcD\",5)<br>返回结果为：\"ykchr\"";
				
			}else if(funValue == "<bean:message key="kq.wizard.rbunch"/>"){//右串

				$("param_funinfo").innerHTML="右串(字符串表达式,整数)";
				$("help_funinfo").innerHTML="说明:RIGHT(字符串表达式,长度)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr2_option','expr3_option','expr3_desc','expr3_text');

				$('expr1_desc').innerHTML="字符表达式";
				$('expr2_desc').innerHTML="长      度";
				
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="Right(\"Abcykchr\",5)<br>返回结果为：\"ykchr\"";
				
			}else if(funValue == "<bean:message key="kq.wizard.year"/>"){//年
			
				$("param_funinfo").innerHTML="年(日期)";
				$("help_funinfo").innerHTML="说明:YEAR(日期):取日期的年";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="Year(#1992.10.10#)<br>返回结果为：1992";
				
			}else if(funValue == "<bean:message key="kq.wizard.month"/>"){//月
			
				$("param_funinfo").innerHTML="月(日期)";
				$("help_funinfo").innerHTML="说明:MONTH(日期):取日期的月";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="Month(#1992.10.13#)<br>返回结果为：10";
				
			}else if(funValue == "<bean:message key="kq.wizard.day"/>"){//日
			
				$("param_funinfo").innerHTML="日(日期)";
				$("help_funinfo").innerHTML="说明:DAY(日期):取日期的日";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="Day(#1992.10.11#)<br>返回结果为：11";
				
			}else if(funValue == "<bean:message key="kq.wizard.quarter"/>"){//季度
			
				$("param_funinfo").innerHTML="季度(日期)";
				$("help_funinfo").innerHTML="说明:QUARTER(日期):取日期的季度";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="QUARTER(#1992.10.11#)<br>返回结果为：4";
				
			}else if(funValue == "<bean:message key="kq.wizard.week"/>"){//周
			
				$("param_funinfo").innerHTML="周(日期)";
				$("help_funinfo").innerHTML="说明:WEEK(日期):该日期为本年的第几周";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="Week(#1992.1.9#)<br>返回结果为：2";
				
			}else if(funValue == "<bean:message key="kq.wizard.weeks"/>"){//星期
			
				$("param_funinfo").innerHTML="星期(日期)";
				$("help_funinfo").innerHTML="说明:WEEKDAY(日期):该日期为星期几";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="WEEKDAY(#1992.1.9#)<br>返回结果为：4";
				
			}else if(funValue == "<bean:message key="kq.wizard.today"/>"){//今天
			
				$("param_funinfo").innerHTML="今天";
				$("help_funinfo").innerHTML="说明:TODAY 或 TODAY()";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="Today<br>返回结果为：#2007.03.09#";
				
			}else if(funValue == "<bean:message key="kq.wizard.bweek"/>"){//本周 或 本周()
			
				$("param_funinfo").innerHTML="本周 或 本周()";
				$("help_funinfo").innerHTML="说明:TOWEEK";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="TOWEEK<br>返回结果为：当前系统时间的周数";
				
			}else if(funValue == "<bean:message key="kq.wizard.bquarter"/>"){//本月 或 本月()
			
				$("param_funinfo").innerHTML="本月 或 本月()";
				$("help_funinfo").innerHTML="说明:TOMONTH()";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="TOMONTH<br>返回结果为：当前系统时间的月份";
				
			}else if(funValue == "<bean:message key="kq.wizard.bmonth"/>"){//本季度 或 本季度()
			
				$("param_funinfo").innerHTML="本季度 或 本季度()";
				$("help_funinfo").innerHTML="说明:TOQUARTER()";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="TOQUARTER<br>返回结果为：当前系统时间的季度数";
				
			}else if(funValue == "<bean:message key="kq.wizard.byear"/>"){//今年 或 今年()
			
				$("param_funinfo").innerHTML="今年 或 今年()";
				$("help_funinfo").innerHTML="说明:TOYEAR()";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="TOYEAR<br>返回结果为：当前系统时间的年份";
				
			}else if(funValue == "<bean:message key="kq.wizard.edate"/>"){//截止日期 或 截止日期()
			
				$("param_funinfo").innerHTML="截止日期 或 截止日期()";
				$("help_funinfo").innerHTML="说明:APPDATE";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="APPDATE()<br>返回结果为：用户设置的计算截止日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.age"/>"){//年龄
			
				$("param_funinfo").innerHTML="年龄(日期)";
				$("help_funinfo").innerHTML="说明:AGE(日期):计算到日的年龄";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="Age(#1992.7.12#)<br>返回结果为：计算到日的年龄";
				
			}else if(funValue == "<bean:message key="kq.wizard.gage"/>"){//工龄
			
				$("param_funinfo").innerHTML="工龄(日期)";
				$("help_funinfo").innerHTML="说明:WORKAGE(日期):年份相减加1";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="WorkAge(#1992.7.12#)<br>返回结果为：年份相减加1";
				
			}else if(funValue == "<bean:message key="kq.wizard.tmonth"/>"){//到月年龄
			
				$("param_funinfo").innerHTML="到月年龄(日期)";
				$("help_funinfo").innerHTML="说明:WMONTHAGE(日期):计算到月的年龄";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="WMONTHAGE(#1992.7.12#)<br>返回结果为：计算到月的年龄";
				
			}else if(funValue == "<bean:message key="kq.wizard.years"/>"){//年数
			
				$("param_funinfo").innerHTML="年数(日期1,日期2)";
				$("help_funinfo").innerHTML="说明:YEARS(日期1,日期2):从日期2到日期1的年数";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr2_text','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="日期表达式";
				
				setOption($('expr1_option_1'),'D');
				setOption($('expr2_option_1'),'D');
				
				$('help_fundesc').innerHTML="Years(#1992.7.12#,#2002.10.10#)<br>返回结果为：两日期之间年数";
				
			}else if(funValue == "<bean:message key="kq.wizard.months"/>"){//月数
			
				$("param_funinfo").innerHTML="月数(日期)";
				$("help_funinfo").innerHTML="说明:MONTHS(日期1,日期2):从日期2到日期1月数";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr2_text','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="日期表达式";
				
				setOption($('expr1_option_1'),'D');
				setOption($('expr2_option_1'),'D');
				
				$('help_fundesc').innerHTML="Months(#1992.7.12#,#2002.10.10#)<br>返回结果为：两日期之间月数";
				
			}else if(funValue == "<bean:message key="kq.wizard.days"/>"){//天数
			
				$("param_funinfo").innerHTML="天数(日期1,日期2)";
				$("help_funinfo").innerHTML="说明:DAYS(日期1,日期2):从日期2到日期1天数";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr2_text','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="日期表达式";
				
				setOption($('expr1_option_1'),'D');
				setOption($('expr2_option_1'),'D');
				
				$('help_fundesc').innerHTML="Days(#1992.7.12#,#2002.10.10#)<br>返回结果为：两日期之间天数";
				
			}else if(funValue == "<bean:message key="kq.wizard.quarters"/>"){//季度数
			
				$("param_funinfo").innerHTML="季度数(日期1,日期2)";
				$("help_funinfo").innerHTML="说明:QUARTER(日期1,日期2):从日期2到日期1季度数";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr2_text','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="日期表达式";
				
				setOption($('expr1_option_1'),'D');
				setOption($('expr2_option_1'),'D');
				
				$('help_fundesc').innerHTML="QUARTERs(#1992.7.12#,#2002.10.10#)<br>返回结果为：两日期之间季度数";
				
			}else if(funValue == "<bean:message key="kq.wizard.weekss"/>"){//周数
			
				$("param_funinfo").innerHTML="周数(日期1,日期2)";
				$("help_funinfo").innerHTML="说明:WEEKS(日期1,日期2):从日期2到日期1周数";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr2_text','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="日期表达式";
				
				setOption($('expr1_option_1'),'D');
				setOption($('expr2_option_1'),'D');
				
				$('help_fundesc').innerHTML="Weeks(#1992.7.12#,#2002.10.10#)<br>返回结果为：两日期之间周数";
				
			}else if(funValue == "<bean:message key="kq.wizard.ayear"/>"){//增加年数
			
				$("param_funinfo").innerHTML="增加年数(日期,整数)";
				$("help_funinfo").innerHTML="说明:AddYear(日期,整数)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="整     数";
				
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="AddYear(#1992.7.12#,2)<br>返回结果为：日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.amonth"/>"){//增加月数
			
				$("param_funinfo").innerHTML="增加月数(日期,整数)";
				$("help_funinfo").innerHTML="说明:AddMonth(日期,整数)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="整     数";
				
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="AddMonth(#1992.7.12#,21)<br>返回结果为：日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.aday"/>"){//增加天数
			
				$("param_funinfo").innerHTML="增加天数(日期,整数)";
				$("help_funinfo").innerHTML="说明:AddDay(日期,整数)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="整     数";
				
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="AddDay(#1992.7.12#,200)<br>返回结果为：日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.aquarter"/>"){//增加季度数
			
				$("param_funinfo").innerHTML="增加季度数(日期,整数)";
				$("help_funinfo").innerHTML="说明:AddQuarter(日期,整数)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="整     数";
				
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="AddQUARTER(#1992.7.12#,200)<br>返回结果为：日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.aweek"/>"){//增加周数
							
				$("param_funinfo").innerHTML="增加周数(日期,整数)";
				$("help_funinfo").innerHTML="说明:AddWeek(日期,整数)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_option');

				$('expr1_desc').innerHTML="日期表达式";
				$('expr2_desc').innerHTML="整     数";
				
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="AddWeek(#1992.7.12#,12)<br>返回结果为：日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.ifa"/>"){//如果 那么 否则 结束
			
				$("param_funinfo").innerHTML="如果 那么 否则 结束";
				$("help_funinfo").innerHTML="说明:IIF <Lexp1> THEN <exp1> ELSE <exp1> END";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="如果 学历=\"01\" 那么 100<br>否则 200<br>结束";
				
			}else if(funValue == "<bean:message key="kq.wizard.thing"/>"){//分情况 
			
				$("param_funinfo").innerHTML="分情况 如果 Lexp1 那么 exp1 如果 Lexp2 那么 exp2 [否则 expn] 结束";
				$("help_funinfo").innerHTML="说明:CASE IIF Lexp1 THEN exp1 ... [ELSE expn] END";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				
				$('help_fundesc').innerHTML="分情况<br>如果 学历=\"01\" 那么 100<br>如果 学历=\"02\" 那么 200<br>如果 学历=\"03\" 那么 300<br>否则 400<br>结束";
				
			}else if(funValue == "<bean:message key="kq.wizard.max"/>"){//较大值
			
				$("param_funinfo").innerHTML="较大值(exp1,exp2)";
				$("help_funinfo").innerHTML="说明:GETMAX(exp1,exp2)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_text');

				$('expr1_desc').innerHTML="表达式1";
				$('expr2_desc').innerHTML="表达式2";
				
				setOption($('expr1_option_1'),'ALL');
				setOption($('expr2_option_1'),'ALL');
				
				$('help_fundesc').innerHTML="GetMax(192,22)";
				
			}else if(funValue == "<bean:message key="kq.wizard.min"/>"){//较小值
			
				$("param_funinfo").innerHTML="较小值(exp1,exp2)";
				$("help_funinfo").innerHTML="说明:GETMIN(exp1,exp2)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_option');
				Element.hide('expr1_text','expr3_desc','expr3_text','expr3_option','expr2_text');

				$('expr1_desc').innerHTML="表达式1";
				$('expr2_desc').innerHTML="表达式2";
				
				setOption($('expr1_option_1'),'ALL');
				setOption($('expr2_option_1'),'ALL');
				
				$('help_fundesc').innerHTML="GetMin(192,22)";
				
			}else if(funValue == "<bean:message key="kq.wizard.qname"/>"){//取 指标名称 [最近第|最初第] 整数 条记录
			
				$("param_funinfo").innerHTML="取 指标名称 [最近第|最初第] 整数 条记录";
				$("help_funinfo").innerHTML="说明:GET(指标名称,整数,方向)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr2_text','expr3_desc','expr3_option');
				Element.hide('expr1_text','expr3_text','expr2_option');

				$('expr1_desc').innerHTML="指标名称";
				$('expr2_desc').innerHTML="整    数";
				$('expr3_desc').innerHTML="方    向";
				
				setOption($('expr1_option_1'),'ALL');
				
				var obj = ${'expr3_option_1'};
				obj.length=0;
				obj[0] = new Option("最初第","最初第");
				obj[1] = new Option("最近第","最近第");
				
				$('help_fundesc').innerHTML="取 学历 最近第 1 条记录";
				
			}else if(funValue == "<bean:message key="kq.wizard.stas"/>"){//统计

				$("param_funinfo").innerHTML="统计 指标名称 满足 条件 [的最初第一条记录|的最近第一条记录|的最大值|的最小值|的总和|的平均值|的个数]";
				$("help_funinfo").innerHTML="说明:SELECT(指标名称,条件,方式)";
				
				Element.show('expr1_desc','expr1_option','expr2_desc','expr3_desc','expr3_option','expr2_text');
				Element.hide('expr1_text','expr3_text','expr2_option_1');

				$('expr1_desc').innerHTML="指标名称";
				$('expr2_desc').innerHTML="条    件";
				$('expr3_desc').innerHTML="方    式";
				
				setOption($('expr1_option_1'),'ALL');
				setOption($('expr2_option_1'),'ALL');
				
				var obj = ${'expr3_option_1'};
				obj.length=0;
				obj[0]=new Option("的总和","的总和");
				obj[1]=new Option("的平均值","的平均值");
				obj[2]=new Option("的个数","的个数");
				obj[3]=new Option("的最大值","的最大值");
				obj[4]=new Option("的最小值","的最小值");
				obj[5]=new Option("的最近第一条记录","的最近第一条记录");
				obj[6]=new Option("的最初第一条记录","的最初第一条记录");
				
				$('help_fundesc').innerHTML="统计 职务工资 满足 Year(记账日期)=2002 的总和";
				
			}else if(funValue == "<bean:message key="kq.wizard.true"/>"){//真
			
				$("param_funinfo").innerHTML="真";
				$("help_funinfo").innerHTML="逻辑常量(TRUE)";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.flase"/>"){//假
			
				$("param_funinfo").innerHTML="假";
				$("help_funinfo").innerHTML="说明:逻辑常量(FALSE)";
			
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.null"/>"){//空(日期)
			
				$("param_funinfo").innerHTML="空(日期)";
				$("help_funinfo").innerHTML="说明:NULL";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "#2000.3.22#"){//#2000.3.22#
			
				$("param_funinfo").innerHTML="#2000.3.22#";
				$("help_funinfo").innerHTML="说明:表示2000年3月22日";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');				
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "#3.22#"){//#3.22#
			
				$("param_funinfo").innerHTML="#3.22#";
				$("help_funinfo").innerHTML="本年3月22日";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";	
			}else if(funValue == "\"张三\""){//张三

				$("param_funinfo").innerHTML="\"张三\"";
				$("help_funinfo").innerHTML="说明:表示姓名为张三";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.even"/>"){//且
			
				$("param_funinfo").innerHTML="且";
				$("help_funinfo").innerHTML="说明:AND";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.and"/>"){//或
			
				$("param_funinfo").innerHTML="或";
				$("help_funinfo").innerHTML="说明:OR";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.not"/>"){//非
				
				$("param_funinfo").innerHTML="非";
				$("help_funinfo").innerHTML="说明:NOT";
			
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.add"/>"){//+
			
				$("param_funinfo").innerHTML="+(加)";
				$("help_funinfo").innerHTML="说明:加";
			
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');	
				$('help_fundesc').innerHTML="&nbsp;";			
			}else if(funValue == "<bean:message key="kq.wizard.dec"/>"){//-
			
				$("param_funinfo").innerHTML="-(减)";
				$("help_funinfo").innerHTML="说明:减";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";				
			}else if(funValue == "<bean:message key="kq.wizard.mul"/>"){//*
			
				$("param_funinfo").innerHTML="*(乘)";
				$("help_funinfo").innerHTML="说明:乘";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";				
			}else if(funValue == "<bean:message key="kq.wizard.divide"/>"){// /
			
				$("param_funinfo").innerHTML="/(除)";
				$("help_funinfo").innerHTML="说明:除";
				
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "\\"){//  \
			
				$("param_funinfo").innerHTML="\\(整除)";
				$("help_funinfo").innerHTML="说明:整除";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.div"/>"){  // DIV
			
				$("param_funinfo").innerHTML="DIV(整除)";
				$("help_funinfo").innerHTML="说明:整除";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.over"/>"){ // % 
			
				$("param_funinfo").innerHTML="%(求余)";
				$("help_funinfo").innerHTML="说明:求余";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.mod"/>"){ // MOD

				$("param_funinfo").innerHTML="MOD(求余)";
				$("help_funinfo").innerHTML="说明:求余";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "="){
			
				$("param_funinfo").innerHTML="=(等于)";
				$("help_funinfo").innerHTML="说明:等于";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == ">"){
			
				$("param_funinfo").innerHTML=">(大于)";
				$("help_funinfo").innerHTML="大于";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == ">="){
			
				$("param_funinfo").innerHTML=">=(大于等于)";
				$("help_funinfo").innerHTML="说明:大于等于";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<"){
			
				$("param_funinfo").innerHTML="<(小于)";
				$("help_funinfo").innerHTML="说明:小于";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<="){
			
				$("param_funinfo").innerHTML="<=(小于等于)";
				$("help_funinfo").innerHTML="说明:小于等于";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<>"){
			
				$("param_funinfo").innerHTML="<>(不等于)";
				$("help_funinfo").innerHTML="说明:不等于";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "LIKE"){
				
				$("param_funinfo").innerHTML="LIKE(包含)";
				$("help_funinfo").innerHTML="说明:包含";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "( )"){
			
				$("param_funinfo").innerHTML="( )括号";
				$("help_funinfo").innerHTML="说明:括号";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "[ ]"){
			
				$("param_funinfo").innerHTML="[ ]中括号";
				$("help_funinfo").innerHTML="说明:中括号(表示指标)";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "{ }"){
			
				$("param_funinfo").innerHTML="{ }大括号";
				$("help_funinfo").innerHTML="说明:大括号(临时变量)";
				Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "//"){ // 注释标识

				$("param_funinfo").innerHTML="//注释标识";
				$("help_funinfo").innerHTML="说明:注释标识";
			    Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}else if(funValue == "<bean:message key="kq.wizard.ctod"/>"){//字符转日期
			
				$("param_funinfo").innerHTML="字符转日期(字符串表达式)";
				$("help_funinfo").innerHTML="说明:CTOD(字符串表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
			
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="CTOD(\"1992.7.12\")<br>返回结果为：日期";
				
			}else if(funValue == "<bean:message key="kq.wizard.cton"/>"){//字符转数值
			
				$("param_funinfo").innerHTML="字符转数值(字符串表达式)";
				$("help_funinfo").innerHTML="说明:CTOI(字符串表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="字符表达式";
			
				setOption($('expr1_option_1'),'A');
				
				$('help_fundesc').innerHTML="CTOI(\"12922.01\")<br>返回结果为：12922.01";
				
			}else if(funValue == "<bean:message key="kq.wizard.dtoc"/>"){//日期转字符
			
				$("param_funinfo").innerHTML="日期转字符(日期)";
				$("help_funinfo").innerHTML="说明:DTOC(日期)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="日期表达式";
			
				setOption($('expr1_option_1'),'D');
				
				$('help_fundesc').innerHTML="DTOC(#1991.10.10#)<br>返回结果为：\"1991.10.10\"";
				
			}else if(funValue == "<bean:message key="kq.wizard.ntoc"/>"){//数值转字符
			
				$("param_funinfo").innerHTML="数值转字符(数值表达式)";
				$("help_funinfo").innerHTML="说明:ITOC(数值表达式)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="数值表达式";
			
				setOption($('expr1_option_1'),'N');
				
				$('help_fundesc').innerHTML="ITOC(129.02)<br>返回结果为：\"129.02\"";
				
			}else if(funValue == "<bean:message key="kq.wizard.ctom"/>"){//代码转名称 
			
				$("param_funinfo").innerHTML="代码转名称(指标名称)";
				$("help_funinfo").innerHTML="说明:CTON(指标名称)";
				
				Element.show('expr1_desc','expr1_option');
				Element.hide('expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');

				$('expr1_desc').innerHTML="指标名称";
			
				setOption($('expr1_option_1'),'A'); //字符型 and 代码型
				
				$('help_fundesc').innerHTML="CTON(性别)<br>返回结果为：数符";
				
			}else if(funValue == "<bean:message key="kq.wizard.ctos"/>"){//~代码指标
			
				$("param_funinfo").innerHTML="~代码指标";
				$("help_funinfo").innerHTML="说明:代码转换符号";
				
			    Element.hide('expr1_desc','expr1_option','expr1_text','expr2_desc','expr2_text','expr2_option','expr3_desc','expr3_text','expr3_option');
				$('help_fundesc').innerHTML="&nbsp;";
			}
			
		}
		
		function sendFromChild(){
			
			var funValue = ${"fun"}.value;
			
			if(funValue == ""){
				funValue = "<bean:message key="kq.formula.int"/>";
			}
			if(funValue == "<bean:message key="kq.formula.int"/>"){//取整
				window.returnValue  = '取整('+$('expr1_option_1').value+')';				
			}else if(funValue == "取余数"){//取余数
				window.returnValue  = '取余数('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.round"/>"){//四舍五入
				window.returnValue  = '四舍五入('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.ssqr"/>"){//三舍七入
				window.returnValue  = '三舍七入('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.ffjy"/>"){//逢分进元
				window.returnValue  = '逢分进元('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.ffjj"/>"){//逢分进角
				window.returnValue  = '逢分进角('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.qnull"/>"){//去空格
				window.returnValue  = '去空格('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.lnull"/>"){//去左空格
				window.returnValue  = '去左空格('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.rnull"/>"){//去右空格
				window.returnValue  = '去右空格('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.zbunch"/>"){//子串
				window.returnValue  = '子串('+$('expr1_option_1').value+','+$('expr2_text_1').value+','+$('expr3_text_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.bunchl"/>"){//串长
				window.returnValue  = '串长('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.lbunch"/>"){//左串
				window.returnValue  = '左串('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.rbunch"/>"){//右串
				window.returnValue  = '右串('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.year"/>"){//年
				window.returnValue  = '年('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.month"/>"){//月
				window.returnValue  = '月('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.day"/>"){//日
				window.returnValue  = '日('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.quarter"/>"){//季度
				window.returnValue  = '季度('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.week"/>"){//周
				window.returnValue  = '周('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.weeks"/>"){//星期
				window.returnValue  = '星期('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.today"/>"){//今天
				window.returnValue  = '今天';
			}else if(funValue == "<bean:message key="kq.wizard.bweek"/>"){//本周 或 本周()
				window.returnValue  = '本周';
			}else if(funValue == "<bean:message key="kq.wizard.bquarter"/>"){//本月 或 本月()
				window.returnValue  = '本月()';
			}else if(funValue == "<bean:message key="kq.wizard.bmonth"/>"){//本季度 或 本季度()
				window.returnValue  = '本季度()';
			}else if(funValue == "<bean:message key="kq.wizard.byear"/>"){//今年 或 今年()
				window.returnValue  = '今年()';
			}else if(funValue == "<bean:message key="kq.wizard.edate"/>"){//截止日期 或 截止日期()
				window.returnValue  = '截止日期()';
			}else if(funValue == "<bean:message key="kq.wizard.age"/>"){//年龄
				window.returnValue  = '年龄('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.gage"/>"){//工龄
				window.returnValue  = '工龄('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.tmonth"/>"){//到月年龄
				window.returnValue  = '到月年龄('+$('expr1_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.years"/>"){//年数
				window.returnValue  = '年数('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.months"/>"){//月数
				window.returnValue  = '月数('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.days"/>"){//天数
				window.returnValue  = '天数('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.quarters"/>"){//季度数
				window.returnValue  = '季度数('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.weekss"/>"){//周数
				window.returnValue  = '周数('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.ayear"/>"){//增加年数
				window.returnValue  = '增加年数('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.amonth"/>"){//增加月数
				window.returnValue  = '增加月数('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.aday"/>"){//增加天数
				window.returnValue  = '增加天数('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.aquarter"/>"){//增加季度数
				window.returnValue  = '增加季度数('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.aweek"/>"){//增加周数
				window.returnValue  = '增加周数('+$('expr1_option_1').value+','+$('expr2_text_1').value+')';							
			}else if(funValue == "<bean:message key="kq.wizard.ifa"/>"){//如果 那么 否则 结束
				window.returnValue  = '如果 <Lexp1> 那么 <exp1> \n否则 <exp1>  \n结束';
			}else if(funValue == "<bean:message key="kq.wizard.thing"/>"){//分情况 
				window.returnValue  = '分情况 \n如果 Lexp1 那么 exp1 \n如果 Lexp2 那么 exp2 \n否则 expn...\n结束';
			}else if(funValue == "<bean:message key="kq.wizard.max"/>"){//较大值
				window.returnValue  = '较大值('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.min"/>"){//较小值
				window.returnValue  = '较小值('+$('expr1_option_1').value+','+$('expr2_option_1').value+')';	
			}else if(funValue == "<bean:message key="kq.wizard.qname"/>"){//取 指标名称 [最近第|最初第] 整数 条记录
				window.returnValue  = '取 '+$('expr1_option_1').value+' '+$('expr3_option_1').value+' '+$('expr2_text_1').value+' 条记录';	
			}else if(funValue == "<bean:message key="kq.wizard.stas"/>"){//统计
				window.returnValue  = '统计 '+$('expr1_option_1').value+'  满足 '+$('expr2_text_1').value+' '+$('expr3_option_1').value;	
			}else if(funValue == "<bean:message key="kq.wizard.true"/>"){//真
				window.returnValue  = '真';
			}else if(funValue == "<bean:message key="kq.wizard.flase"/>"){//假
				window.returnValue  = '假';				
			}else if(funValue == "<bean:message key="kq.wizard.null"/>"){//空(日期)
				window.returnValue  = '空';				
			}else if(funValue == "#2000.3.22#"){//#2000.3.22#
				window.returnValue  = '#2000.3.22#';				
			}else if(funValue == "#3.22#"){//#3.22#
				window.returnValue  = '#3.22#';			
			}else if(funValue == "\"张三\""){//张三
				window.returnValue  = '\"张三\"';				
			}else if(funValue == "<bean:message key="kq.wizard.even"/>"){//且
				window.returnValue  = ' 且 ';				
			}else if(funValue == "<bean:message key="kq.wizard.and"/>"){//或
				window.returnValue  = ' 或 ';				
			}else if(funValue == "<bean:message key="kq.wizard.not"/>"){//非
				window.returnValue  = ' 非 ';				
			}else if(funValue == "<bean:message key="kq.wizard.add"/>"){//+
				window.returnValue  = ' + ';				
			}else if(funValue == "<bean:message key="kq.wizard.dec"/>"){//-
				window.returnValue  = ' - ';					
			}else if(funValue == "<bean:message key="kq.wizard.mul"/>"){//*
				window.returnValue  = ' * ';							
			}else if(funValue == "<bean:message key="kq.wizard.divide"/>"){// /
				window.returnValue  = ' / ';				
			}else if(funValue == "\\"){//  \
				window.returnValue  = ' \ ';				
			}else if(funValue == "<bean:message key="kq.wizard.div"/>"){  // DIV
				window.returnValue  = ' DIV ';				
			}else if(funValue == "<bean:message key="kq.wizard.over"/>"){ // % 
				window.returnValue  = ' % ';				
			}else if(funValue == "<bean:message key="kq.wizard.mod"/>"){ // MOD
				window.returnValue  = ' MOD ';			
			}else if(funValue == "="){
				window.returnValue  = ' = ';					
			}else if(funValue == ">"){
				window.returnValue  = ' > ';					
			}else if(funValue == ">="){
				window.returnValue  = ' >= ';					
			}else if(funValue == "<"){
				window.returnValue  = ' < ';					
			}else if(funValue == "<="){
				window.returnValue  = ' <= ';				
			}else if(funValue == "<>"){
				window.returnValue  = ' <> ';					
			}else if(funValue == "LIKE"){
				window.returnValue  = ' LIKE ';					
			}else if(funValue == "( )"){
				window.returnValue  = ' ( ) ';					
			}else if(funValue == "[ ]"){
				window.returnValue  = ' [ ] ';						
			}else if(funValue == "{ }"){
				window.returnValue  = ' { } ';						
			}else if(funValue == "//"){ // 注释标识
				window.returnValue  = ' // ';					
			}else if(funValue == "<bean:message key="kq.wizard.ctod"/>"){//字符转日期
				window.returnValue  = '字符转日期('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.cton"/>"){//字符转数值
				window.returnValue  = '字符转数值('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.dtoc"/>"){//日期转字符
				window.returnValue  = '日期转字符('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.ntoc"/>"){//数值转字符
				window.returnValue  = '数值转字符('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.ctom"/>"){//代码转名称
				window.returnValue  = '代码转名称('+$('expr1_option_1').value+')';
			}else if(funValue == "<bean:message key="kq.wizard.ctos"/>"){//~代码指标
				window.returnValue  = '~';
			}else{
				window.returnValue  = '';
			}
			window.close(); 
		}
		
		//初始化(默认为数值函数 取整(数值型表达式))
		function init(){
			
			Element.hide('expr1_text');
			Element.hide('expr2_desc','expr2_option','expr2_text');
			Element.hide('expr3_option','expr3_text','expr3_desc');
			
			Element.show('expr1_option');
			
			$('expr1_desc').innerHTML="数值表达式";
			
			setOption($('expr1_option_1'),'N');
			
			$('help_fundesc').innerHTML="Int(129.11)<br>返回结果:129"
		}
	-->
	</SCRIPT>
	<body>
		<<div  class="fixedDiv2" style="height: 100%;border: none">
		<table border="0" align="center" width="100%">
			<tr>
				<td>
					<fieldset>
						<legend>
							<bean:message key="kq.formula.function" />
						</legend>
						<table width="90%" border=0 align="center">
							<tr>
								<td>
									<bean:message key="kq.formula.funtions" />						
									<select NAME="functionType" id="functionType" onchange="changeFunctionType(this)"><!-- 函数类型 -->
										<option value="1">
											N&nbsp;
											<bean:message key="kq.formula.number" />
											<!-- 数值函数 -->
										</option>
										<option value="2">
											A&nbsp;
											<bean:message key="kq.formula.char" />
											<!-- 字符函数 -->
										</option>
										<option value="3">
											D&nbsp;
											<bean:message key="kq.formula.date" />
											<!-- 日期函数 -->
										</option>
										<option value="10">
											<bean:message key="kq.wizard.switch" />
											<!-- 转换函数 -->
										</option>
										<option value="4">
											<bean:message key="kq.wizard.buding" />
											<!-- 不定型函数 -->
										</option>
										<option value="5">
											<bean:message key="kq.wizard.chlang" />
											<!-- 常量 -->
										</option>
										<option value="6">
											<bean:message key="kq.wizard.boolen" />
											<!-- 逻辑操作符 -->
										</option>
										<option value="7">
											<bean:message key="kq.wizard.number" />
											<!-- 算术运算 -->
										</option>
										<option value="8">
											<bean:message key="kq.wizard.option" />
											<!-- 关系运算 -->
										</option>
										
										<option value="9">
											<bean:message key="kq.wizard.other" />
											<!-- 其他 -->
										</option>
									</select>						
									<SELECT NAME="fun" id="fun" onchange="changeFunction();" style="width:180"><!-- 特定一类函数(默认为数值类型)  -->
										<option value="<bean:message key="kq.formula.int" />">
											N 取整(数值表达式)
										</option>
										<option value="取余数">
											N 取余数(数值表达式,数值表达式)
										</option>
										<option value="<bean:message key="kq.wizard.round" />">
											N 四舍五入(数值表达式,整数)
										</option>
										<option value="<bean:message key="kq.wizard.ssqr" />">
											N 三舍七入(数值表达式)
										</option>
										<option value="<bean:message key="kq.wizard.ffjy" />">
											N 逢分进元(数值表达式)
										</option>
										<option value="<bean:message key="kq.wizard.ffjj" />">
											N 逢分进角(数值表达式)
										</option>
									</SELECT>
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr><tr>	
				<td>
					<!--参数设置区域 函数中最多有三个参数 参数为 输入数值 和 下列选择(文本框 下拉列表) -->
					<fieldset>
						<legend>
							参数设置:
						</legend>
						<table width="90%" border=0 align="center">
							<tr>
								<td colspan="2">
									<div id="param_funinfo">取整(数值表达式)</div>
								</td>
							</tr>
							<tr>
								<td><div id="expr1_desc"></div></td>
								<td>
									<div id="expr1_text">
										<input type="text" id="expr1_text_1" name="expr1_text_1" value="" size="10" maxlength="2">
									</div>
									<div id="expr1_option">
										<select name="expr1_option_1" id="expr1_option_1">
												<option value="">&nbsp;&nbsp;</option>
										</select>
									</div>
								</td>
							</tr>
							<tr>
								<td><div id="expr2_desc"></div></td>
								<td>
									<div id="expr2_text">
										<input type="text" id="expr2_text_1" name="expr2_text_1" value="" size="10" maxlength="2">
									</div>
									<div id="expr2_option">
										<select name="expr2_option_1" id="expr2_option_1">
												<option value="">&nbsp;&nbsp;</option>
										</select>
									</div>
								</td>
							</tr>
							<tr>
								<td><div id="expr3_desc"></div></td>
								<td>
									<div id="expr3_text">
										<input type="text" id="expr3_text_1" name="expr3_text_1" value="" size="10" maxlength="2">
									</div>
									<div id="expr3_option">
										<select name="expr3_option_1" id="expr3_option_1">
												<option value="">&nbsp;&nbsp;</option>
										</select>
									</div>								
								</td>
							</tr>
						</table>
					</fieldset>
				</td>
			</tr><tr>		
				<td>
					<!-- 帮助说明区域 -->
					<fieldset>
						<legend>
							<div id="help_funinfo">说明:INT(数值型表达式)</div>
						</legend>
						<table width="90%" border=0 align="center">
							
							<tr>
								<td><div id="help_fundesc"></div></td>
							</tr>
						</table>
					</fieldset>
				</td>
				
			</tr>
		</table>
		<table width="90%" align="center">
			<tr align="center">
				<td valign="top" align="center">
					<input type="button" name="Submit3" value="<bean:message key="kq.formula.true"/>" class="mybutton" onclick="sendFromChild();">
					<input type="button" name="Submit3" value="<bean:message key="button.return"/>" class="mybutton" onclick="window.close();">
				</td>
			</tr>
		</table>
		</div>
	<BODY>
	<SCRIPT LANGUAGE="JavaScript">
		init();
	</SCRIPT>
</HTML>

