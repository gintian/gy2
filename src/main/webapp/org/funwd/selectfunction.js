function saveCalculation(){
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    targetobj = parent.document.getElementById("calculation") ;
	hiddenobj = parent.document.getElementById("formula") ;
	var id = currnode.uid;
	
	switch (id){
		case "0":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：数值函数";
			break;
		case "N_num0":
			targetobj.innerHTML = "Int(129.11) <br>返回结果为：129";
			hiddenobj.value = "取整";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：INT(数值表达式)";
			parent.document.getElementById("explained").innerHTML="取整(数值表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "N_num1_4":
			targetobj.innerHTML = "取余数(10, 3) <br>返回结果为: 1";
			hiddenobj.value = "取余数";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：FUNCMOD(数值表达式,数值表达式)";
			parent.document.getElementById("explained").innerHTML="取余数(数值表达式,数值表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "N_num2_2":
			targetobj.innerHTML = "Round(129.166,2) <br>返回结果为：129.17";
			hiddenobj.value = "四舍五入";
			idstart(id); 
			parent.document.getElementById("decimalname").innerHTML="整数  ";
			parent.document.getElementById("note").innerHTML="说明：ROUND(数值表达式,保留小数点位数)";
			parent.document.getElementById("explained").innerHTML="四舍五入(数值表达式,保留小数点位数)";
			parent.document.getElementById("id").value=id;
			break;
		case "N_num3":
			targetobj.innerHTML = "SANQI(129.34) <br>返回结果为：129.5";
			hiddenobj.value = "三舍七入";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：SANQI;三舍七入,其他为0.5";
			parent.document.getElementById("explained").innerHTML="三舍七入(数值表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "N_num4_2":
			targetobj.innerHTML = "YUAN(129.01) <br>返回结果为：130<br>YUAN(129.09, 10) <br>返回结果为：129<br>YUAN(129.10, 10) <br>返回结果为：130";
			hiddenobj.value = "逢分进元";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：YUAN(数值表达式,[进元参数]),进元参数可选,默认为1";
			parent.document.getElementById("explained").innerHTML="逢分进元(数值表达式,[进元参数])";
			parent.document.getElementById("id").value=id;
			parent.document.getElementById("decimal").value='1';
			break;
		case "N_num5":
			targetobj.innerHTML = 'JIAO(129.11) <br>返回结果为：129.2"';
			hiddenobj.value = "逢分进角";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：JIAO(129.11) \n返回结果为：129.2";
			parent.document.getElementById("explained").innerHTML="逢分进角(数值表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "NN_num6":
			targetobj.innerHTML = '幂(3,2) <br>返回结果为：9<br>注意: 在进行N次方根的计算时,第二个参数可用小数或分数(1/n),在用分数时,应使用类似1.0/n的格式';
			hiddenobj.value = "幂(,)";
			idstart(id);
			parent.document.getElementById("decimalname").innerHTML="次方";
			parent.document.getElementById("note").innerHTML="说明：求底数的n次方";
			parent.document.getElementById("explained").innerHTML="幂(数值表达式,数值表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "1":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：字符串函数";
			break;
		case "A_str0":
			targetobj.innerHTML = 'Trim("&nbsp;&nbsp;AbcD&nbsp;&nbsp;") <br>返回结果为："AbcD"';
			hiddenobj.value = "去空格";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("note").innerHTML="说明：Trim(字符串表达式)";
			parent.document.getElementById("explained").innerHTML="去空格(字符串表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "A_str1":
			targetobj.innerHTML = 'LTrim("&nbsp;&nbsp;AbcD&nbsp;&nbsp;") <br>返回结果为："AbcD&nbsp;&nbsp;"';
			hiddenobj.value = "去左空格";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("note").innerHTML="说明：LTRIM(字符串表达式)";
			parent.document.getElementById("explained").innerHTML="去左空格(字符串表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "A_str2":
			targetobj.innerHTML = 'RTrim("&nbsp;&nbsp;AbcD&nbsp;&nbsp;") <br>返回结果为："&nbsp;&nbsp;AbcD"';
			hiddenobj.value = "去右空格";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("note").innerHTML="说明：RTRIM(字符串表达式)";
			parent.document.getElementById("explained").innerHTML="去右空格(字符串表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "A_str3_2_2":
			targetobj.innerHTML = 'SubStr("AykchrbcD",2,5) <br>返回结果为："ykchr"';
			hiddenobj.value = "子串";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("decimalname").innerHTML="起&nbsp;&nbsp;&nbsp;&nbsp;始&nbsp;&nbsp;&nbsp;&nbsp;位  ";
			parent.document.getElementById("note").innerHTML="说明：SUBSTR(字符串表达式,起始位,长度)";
			parent.document.getElementById("explained").innerHTML="子串(字符串表达式,整数,整数)";
			parent.document.getElementById("id").value=id;
			break;
		case "A_str4":
			targetobj.innerHTML = 'LEN("ykchrbcD") <br>返回结果为：8';
			hiddenobj.value = "串长";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("note").innerHTML="说明：LEN(字符串表达式)";
			parent.document.getElementById("explained").innerHTML="串长(字符串表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "A_str5_2":
			targetobj.innerHTML = 'Left("ykchrbcD",5) <br>返回结果为："ykchr"';
			hiddenobj.value = "左串";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("decimalname").innerHTML="长&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;度";
			parent.document.getElementById("note").innerHTML="说明：LEFT(字符串表达式,长度)";
			parent.document.getElementById("explained").innerHTML="左串(字符串表达式,长度)";
			parent.document.getElementById("id").value=id;
			break;
		case "A_str6_2":
			targetobj.innerHTML = 'Right("Abcykchr",5) <br>返回结果为："ykchr"';
			hiddenobj.value = "右串";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("decimalname").innerHTML="长&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;度";
			parent.document.getElementById("note").innerHTML="说明：RIGHT(字符串表达式,长度)";
			parent.document.getElementById("explained").innerHTML="右串(字符串表达式,长度)";
			parent.document.getElementById("id").value=id;
			break;
		case "V_vols7_2":
			targetobj.innerHTML = '';
			hiddenobj.value = "登录用户名";
			idstart(id);
			parent.document.getElementById("decimalname").innerHTML="参数：";
			parent.document.getElementById("note").innerHTML="说明：求登录用户名1(简写)|2(全称)<br>参数只能是1或者2，<br>1表示返回登录用户名，<br>2表示返回登录用户全名";
			parent.document.getElementById("explained").innerHTML="登录用户名(1|2)";
			parent.document.getElementById("id").value=id;
			break;
			case "V_vols8_9":
			targetobj.innerHTML = '';
			hiddenobj.value = "本单位";
			idstart(id);
			parent.document.getElementById("note").innerHTML="当前登录用户的操作单位,一般直接用于IN操作符后, 如:<br>如果 所在单位 IN 本单位() 那么 ...<br>参数是业务类别，可以是1-3:<br>0:默认，系统操作单位<br>1:工资发放<br>2工资总额<br>3:所得税";
			parent.document.getElementById("explained").innerHTML="";
			parent.document.getElementById("id").value=id;
			parent.document.getElementById("templatename").innerHTML="业务分类";
			break;
			case "V_vols8_10":
			targetobj.innerHTML = '';
			hiddenobj.value = "取兼职信息";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：<br>&nbsp;&nbsp;取兼职信息()：不显示兼职单位、部门信息<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;主任(兼职)，党委书记(-)<br>&nbsp;&nbsp;取兼职信息(单位部门):显示兼职单位、部门信息，<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;北京分公司\分公司领导\主任(兼职)，集团总部\党委书记(-)<br>&nbsp;&nbsp;取兼职信息(单位):显示兼职单位信息<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;北京分公司\主任(兼职)，集团总部\党委书记(-)<br>&nbsp;&nbsp;取兼职信息(部门):显示兼职部门信息<br>&nbsp;&nbsp;举例：<br>&nbsp;&nbsp;分公司领导\主任(兼职)，党委书记(-)";
			parent.document.getElementById("explained").innerHTML="";
			parent.document.getElementById("id").value=id; 
			break;
		case "2":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：日期函数 ";
			break;
		case "D_data0":
			targetobj.innerHTML = 'Year(#1992.10.10#) <br>返回结果为：1992';
			hiddenobj.value = "年";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：YEAR(日期)：取日期的年";
			parent.document.getElementById("explained").innerHTML="年(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data1":
			targetobj.innerHTML = 'Month(#1992.10.13#) <br>返回结果为：10';
			hiddenobj.value = "月";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：MONTH(日期)：取日期的月";
			parent.document.getElementById("explained").innerHTML="月(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data2":
			targetobj.innerHTML = 'Day(#1992.10.11#) <br>返回结果为：11';
			hiddenobj.value = "日";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：DAY(日期)：取日期的日";
			parent.document.getElementById("explained").innerHTML="日(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data3":
			targetobj.innerHTML = 'QUARTER(#1992.10.11#) <br>返回结果为：4';
			hiddenobj.value = "季度";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：QUARTER(日期)：取日期的季度";
			parent.document.getElementById("explained").innerHTML="季度(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data4":
			targetobj.innerHTML = 'Week(#1992.1.9#) <br>返回结果为：2';
			hiddenobj.value = "周";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：WEEK(日期)：该日期为本年的第几周";
			parent.document.getElementById("explained").innerHTML="周(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data5":
			targetobj.innerHTML = 'WEEKDAY(#1992.1.9#) <br>返回结果为：4';
			hiddenobj.value = "星期";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：WEEKDAY(日期)：该日期为星期几";
			parent.document.getElementById("explained").innerHTML="星期(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data6":
			targetobj.innerHTML = 'Today <br>返回结果为：#2007.06.26#';
			hiddenobj.value = "今天";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：TODAY 或 TODAY()";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data7":
			targetobj.innerHTML = 'TOWEEK <br>返回结果为：当前系统时间的周数';
			hiddenobj.value = "本周";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：TOWEEK";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data8":
			targetobj.innerHTML = 'TOMONTH <br>返回结果为：当前系统时间的月份';
			hiddenobj.value = "本月()";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：TOMONTH";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data9":
			targetobj.innerHTML = 'TOQUARTER <br>返回结果为：当前系统时间的季度数';
			hiddenobj.value = "本季度()";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：TOQUARTER";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data10":
			targetobj.innerHTML = 'TOYEAR <br>返回结果为：当前系统时间的年份';
			hiddenobj.value = "今年()";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：TOYEAR";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data11":
			targetobj.innerHTML = 'APPDATE() <br>返回结果为：用户设置的计算截止日期';
			hiddenobj.value = "截止日期()";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：APPDATE";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data11":
			targetobj.innerHTML = 'APPDATE() <br>返回结果为：用户设置的计算截止日期';
			hiddenobj.value = "截止日期()";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：APPDATE";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data12":
			targetobj.innerHTML = 'Age(#1992.7.12#) <br>返回结果为：计算到日的年龄';
			hiddenobj.value = "年龄";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：AGE(日期)：计算到日的年龄";
			parent.document.getElementById("explained").innerHTML="年龄(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data13":
			targetobj.innerHTML = 'WorkAge(#1992.7.12#) <br>返回结果为：年份相减加1';
			hiddenobj.value = "工龄";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：WORKAGE(日期)：年份相减加1";
			parent.document.getElementById("explained").innerHTML="工龄(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data14":
			targetobj.innerHTML = 'WMONTHAGE(#1992.7.12#) <br>返回结果为：计算到月的年龄';
			hiddenobj.value = "到月年龄";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式";
			parent.document.getElementById("note").innerHTML="说明：WMONTHAGE(日期)：计算到月的年龄";
			parent.document.getElementById("explained").innerHTML="到月年龄(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data15_1":
			targetobj.innerHTML = 'Years(#2002.10.10#,#1992.7.12#) <br>返回结果为：两日期之间年数';
			hiddenobj.value = "年数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
			parent.document.getElementById("datesubset1").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
			parent.document.getElementById("note").innerHTML="说明：YEARS(日期1,日期2)：从日期2到日期1的年数";
			parent.document.getElementById("explained").innerHTML="年数(日期1,日期2)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data16_1":
			targetobj.innerHTML = 'Months(#2002.10.10#,#1992.7.12#) <br>返回结果为：两日期之间月数';
			hiddenobj.value = "月数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
			parent.document.getElementById("datesubset1").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
			parent.document.getElementById("note").innerHTML="说明：MONTHS(日期1,日期2)：从日期2到日期1的月数";
			parent.document.getElementById("explained").innerHTML="月数(日期1,日期2)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data17_1":
			targetobj.innerHTML = 'Days(#2002.10.10#,#1992.7.12#) <br>返回结果为：两日期之间天数';
			hiddenobj.value = "天数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
			parent.document.getElementById("datesubset1").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
			parent.document.getElementById("note").innerHTML="说明：DAYS(日期1,日期2)：从日期2到日期1的天数";
			parent.document.getElementById("explained").innerHTML="天数(日期1,日期2)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data18_1":
			targetobj.innerHTML = 'QUARTERs(#1992.7.12#,#2002.10.10#) <br>返回结果为：两日期之间季度数';
			hiddenobj.value = "季度数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
			parent.document.getElementById("datesubset1").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
			parent.document.getElementById("note").innerHTML="说明：QUARTERS(日期1,日期2)：从日期1到日期2的季度数";
			parent.document.getElementById("explained").innerHTML="季度数(日期1,日期2)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data19_1":
			targetobj.innerHTML = 'Weeks(#1992.7.12#,#2002.10.10#) <br>返回结果为：两日期之间周数';
			hiddenobj.value = "周数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期1  ";
			parent.document.getElementById("datesubset1").innerHTML="日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期2  ";
			parent.document.getElementById("note").innerHTML="说明：WEEKS(日期1,日期2)：从日期1到日期2的周数";
			parent.document.getElementById("explained").innerHTML="周数(日期1,日期2)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data20_2":
			targetobj.innerHTML = 'AddYear(#1992.7.12#,2) <br>返回结果为：日期';
			hiddenobj.value = "增加年数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("decimalname").innerHTML="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
			parent.document.getElementById("note").innerHTML="说明：AddYear(日期,整数)";
			parent.document.getElementById("explained").innerHTML="增加年数(日期,整数)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data21_2":
			targetobj.innerHTML = 'AddMonth(#1992.7.12#,21) <br>返回结果为：日期';
			hiddenobj.value = "增加月数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("decimalname").innerHTML="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
			parent.document.getElementById("note").innerHTML="说明：AddMonth(日期,整数)";
			parent.document.getElementById("explained").innerHTML="增加月数(日期,整数)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data22_2":
			targetobj.innerHTML = 'AddDay(#1992.7.12#,200) <br>返回结果为：日期';
			hiddenobj.value = "增加天数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("decimalname").innerHTML="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
			parent.document.getElementById("note").innerHTML="说明：AddDay(日期,整数)";
			parent.document.getElementById("explained").innerHTML="增加天数(日期,整数)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data23_2":
			targetobj.innerHTML = 'AddQUARTER(#1992.7.12#,200) <br>返回结果为：日期';
			hiddenobj.value = "增加季度数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("decimalname").innerHTML="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
			parent.document.getElementById("note").innerHTML="说明：AddQUARTER(日期,整数)";
			parent.document.getElementById("explained").innerHTML="增加季度数(日期,整数)";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data24_2":
			targetobj.innerHTML = 'AddWeek(#1992.7.12#,12) <br>返回结果为：日期';
			hiddenobj.value = "增加周数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("decimalname").innerHTML="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
			parent.document.getElementById("note").innerHTML="说明：AddWeek(日期,整数)";
			parent.document.getElementById("explained").innerHTML="增加周数(日期,整数)";
			parent.document.getElementById("id").value=id;
			break;
		case "DD_data25":
			targetobj.innerHTML = '返回结果为：当前工资发放的归属日期';
			hiddenobj.value = "归属日期()";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：归属日期";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data26_1_7":
			var expstr = "返回指定日期范围内的工作日天数.<br>参数说明:<br>";
			expstr+="日期参数: 日期型指标、日期数值、截止日期和归属日期<br>";
			expstr+='节假日标志: 可选参数，"含节假日"或"不含节假日"(不';
			expstr+='含引号)。默认为"不含节假日"。';
			expstr+="含节假日表示节假日为工作日。";
			expstr+="公休日、节假日及倒休在考勤模块中设置";
			targetobj.innerHTML = expstr;
			hiddenobj.value = "工作日";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期1";
			parent.document.getElementById("datesubset1").innerHTML="日期2";
			parent.document.getElementById("note").innerHTML="说明：WorkDays(日期1,日期2,节假日标志)";
			parent.document.getElementById("explained").innerHTML="工作日(日期,日期[,含节假日|不含节假日])";
			parent.document.getElementById("id").value=id;
			break;
		case "D_data27_3":
			var strtext = "子集中符合条件的日期指标的月份数<br>";
			strtext+="第一个参数必须为子集的日期指标，第二个参数为条件<br>";
			strtext+="返回指标所在子集中,符合条件的所有日期值的年月份个数";
			targetobj.innerHTML = strtext;
			hiddenobj.value = "统计月数";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期指标  ";
			parent.document.getElementById("note").innerHTML="说明：GroupMonths(日期指标,条件表达试)";
			parent.document.getElementById("explained").innerHTML="统计月数(日期指标,条件表达试)";
			parent.document.getElementById("id").value=id;
			break;
			case "D_data28_1_10_10":
			var expstr = "参数说明:<br>";
			expstr+="（1）表达式中只能用这两个时间或其它常量<br>（2）开始时间和结束时间有重复，则只算一次<br>例如：统计时间(任职时间,免职时间,职级=\"03\",月数(如果 为空(免职时间) 那么 今天 否则 免职时间 结束,任职时间))<br>返回值：任03职级的月数，如果同时兼职，相同月数只算一次";
			targetobj.innerHTML = expstr;
			hiddenobj.value = "统计时间";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="开始日期";
			parent.document.getElementById("datesubset1").innerHTML="结束日期";
			parent.document.getElementById("conditionssubset").innerHTML="表&nbsp;达&nbsp;式";
			parent.document.getElementById("note").innerHTML="说明：统计时间(开始时间, 结束时间, 条件, 表达式())";
			parent.document.getElementById("explained").innerHTML="统计时间(开始时间, 结束时间, 条件, 表达式())";
			parent.document.getElementById("id").value=id;
			break;
		case "3":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：转换函数";
			break;
		case "T_str0":
			targetobj.innerHTML = 'CTOD("1992.7.2") <br>返回结果为：日期';
			hiddenobj.value = "字符转日期";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("note").innerHTML="说明：CTOD(字符串表达式)";
			parent.document.getElementById("explained").innerHTML="字符转换日期(字符串表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "T_str1":
			targetobj.innerHTML = 'CTOI("12922.01") <br>返回结果为：12922.01';
			hiddenobj.value = "字符转数值";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="字符串表达式";
			parent.document.getElementById("note").innerHTML="说明：CTOI(字符串表达式)";
			parent.document.getElementById("explained").innerHTML="字符转换数值(字符串表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "T_data2":
			targetobj.innerHTML = 'DTOC(#1991.10.10#) <br>返回结果为："1991.10.10"';
			hiddenobj.value = "日期转字符";
			idstart(id);
			parent.document.getElementById("datesubset").innerHTML="日期表达式  ";
			parent.document.getElementById("note").innerHTML="说明：DTOC(日期)";
			parent.document.getElementById("explained").innerHTML="日期转换字符(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "T_num3":
			targetobj.innerHTML = 'ITOC(129.02) <br>返回结果为："129.02"';
			hiddenobj.value = "数值转字符";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：ITOC(数值表达式)";
			parent.document.getElementById("explained").innerHTML="数值转换字符(数值表达式)";
			parent.document.getElementById("id").value=id;
			break;
		case "T_vol7_2":
			targetobj.innerHTML = '“编号”指标值为“第0003号”<br>数字转汉字(编号,1)<br>返回结果为：第○○○三号<br>数字转汉字(编号,2)<br>返回结果为：第零零零叁号<br>“基本工资”指标值为“1234.56”<br>数字转汉字(编号,3)<br>返回结果为：壹仟贰佰叁拾肆元伍角陆分';
			hiddenobj.value = "数字转汉字";
			idstart(id);
			//parent.document.getElementById("strsubset").innerHTML="指标名称";
			parent.document.getElementById("decimalname").innerHTML="参&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数";
			parent.document.getElementById("note").innerHTML="说明：NumConversion(指标名称,1|2|3)";
			parent.document.getElementById("explained").innerHTML="数字转汉字(指标,参数)";
			parent.document.getElementById("id").value=id;
			break;
		case "T_code4":
			targetobj.innerHTML = 'CTON(性别) <br>返回结果为：数符';
			hiddenobj.value = "代码转名称";
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="指标名称";
			parent.document.getElementById("note").innerHTML="说明：CTON(指标名称)";
			parent.document.getElementById("explained").innerHTML="代码转名称(指标名称)";
			parent.document.getElementById("id").value=id;
			break;
		case "T_num4_2":
			targetobj.innerHTML = '返回结果为：指标整数部分，不足指定长度前面补0';
			hiddenobj.value = "数值转代码";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：NUMTOCODE(指标名称,长度)";
			parent.document.getElementById("explained").innerHTML="数值转代码(指标名称, 4)";
			parent.document.getElementById("decimalname").innerHTML="长度";
	//		parent.document.getElementById("numtitle").innerHTML="指标名称";
			parent.document.getElementById("id").value=id;
			break;
		case "TT_tra5":
			targetobj.innerHTML = "";
			hiddenobj.value = "~";
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：代码转换符号";
			parent.document.getElementById("id").value=id;
			break;
		case "T_item6_5":
			targetobj.innerHTML = 'CTON2(性别,"AX") <br>如果性别为"1"<br>&nbsp;&nbsp;返回结果为：男<br>如果性别为"2"<br>&nbsp;&nbsp;返回结果为：女';
			hiddenobj.value = "代码转名称2";
			idstart(id);
			parent.document.getElementById("itemidname").innerHTML="表达式";
			parent.document.getElementById("codeMaxname").innerHTML="代码类";
			parent.document.getElementById("note").innerHTML="说明：CTON2(表达式,代码类)";
			parent.document.getElementById("explained").innerHTML="代码转名称2(表达式,代码类)";
			parent.document.getElementById("id").value=id;
			break;
		case "4":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：类型不定函数";
			break;
		case "VV_vol0":
			targetobj.innerHTML = '如果 学历="01" 那么 100 <br>否则 200 <br>结束';
			hiddenobj.value = '如果 <Lexp1> 那么 <exp1> \n否则 <exp1>  \n结束';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：IIF<Lexp1> THEN<exp1> ELSE<exp1> END";
			parent.document.getElementById("id").value=id;
			break;
		case "VV_vol1":
			targetobj.innerHTML = '分情况<br>如果 学历="01" 那么 100 <br>如果 学历="02" 那么 200 <br>如果 学历="03" 那么 300 <br>否则 400 <br>结束';
			hiddenobj.value = '分情况 \n如果 Lexp1 那么 exp1 \n如果 Lexp2 那么 exp2 \n否则 expn... \n结束';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：CASEIIF Lexp1 THEN exp1 IIF Lexp2 THEN exp2[ELSE expn]结束";
			parent.document.getElementById("id").value=id;
			break;
		case "V_vol2_6":
			targetobj.innerHTML = 'GetMax(192,22) <br>值为192';
			hiddenobj.value = "较大值";
			idstart(id);
			parent.document.getElementById("datastrcssaname").innerHTML="表 达 式1  ";
			parent.document.getElementById("datastrcss1name").innerHTML="表 达 式2  ";
			parent.document.getElementById("note").innerHTML="说明：GETMAX(exp1,exp2)";
			parent.document.getElementById("explained").innerHTML="较大值(exp1,exp2)";
			parent.document.getElementById("id").value=id;
			break;
		case "V_vol3_6":
			targetobj.innerHTML = 'GetMin(192,22) <br>值为22';
			hiddenobj.value = "较小值";
			idstart(id);
			parent.document.getElementById("datastrcssaname").innerHTML="表 达 式1  ";
			parent.document.getElementById("datastrcss1name").innerHTML="表 达 式2  ";
			parent.document.getElementById("note").innerHTML="说明：GETMIN(exp1,exp2)";
			parent.document.getElementById("explained").innerHTML="较小值(exp1,exp2)";
			parent.document.getElementById("id").value=id;
			break;
		case "V_vol4_2_1":
			targetobj.innerHTML = '取 学历 最近第 1 条记录';
			hiddenobj.value = '取';
			idstart(id);
			parent.document.getElementById("datastrcssaname").innerHTML="指标名称";
			parent.document.getElementById("decimalname").innerHTML="整&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数  ";
			parent.document.getElementById("note").innerHTML="说明：GET(指标名称，整数，方向)";
			parent.document.getElementById("explained").innerHTML="取 指标名称 [最近第|最初第] 整数 条记录";
			parent.document.getElementById("id").value=id;
			break;
		case "V_vol5_3_3":
			targetobj.innerHTML = '统计 职务工资 满足 Year(记账日期)=2002 的总和';
			hiddenobj.value = '统计';
			idstart(id);
			parent.document.getElementById("datastrcssaname").innerHTML="指标名称";
			parent.document.getElementById("note").innerHTML="说明：SELECT(指标名称,条件,方式)";
			parent.document.getElementById("explained").innerHTML="统计 指标名称 满足 条件 [第一条记录..";
			parent.document.getElementById("id").value=id;
			break;
		case "V_vol6":
			targetobj.innerHTML = '例子: 为空(独生子女费)<br>独生子女费 IS NULL';
			hiddenobj.value = '为空';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：指标 IS NULL";
			parent.document.getElementById("explained").innerHTML="为空(指标)";
			parent.document.getElementById("id").value=id;
			break;
		case "V_volc1_8_9_1_1":
			var exprestr = "返回对应的结果值。<br>部分参数说明:<br>分组指标: ";
			exprestr+="代码型指标<br>分组级数：需要分组统计到的代码层级. 默认为0，";
			exprestr+="表示直接按代码值分组范围：可设为当前列表、当前人员库。默认为当前列表";
			targetobj.innerHTML = exprestr;
			hiddenobj.value = '分组汇总';
			idstart(id);
			parent.document.getElementById("strsubset").innerHTML="分组指标";
			parent.document.getElementById("itemidname").innerHTML="汇总指标";
			parent.document.getElementById("condname").innerHTML="方&nbsp;&nbsp;&nbsp;&nbsp;式";
			parent.document.getElementById("rangeidname").innerHTML="范&nbsp;&nbsp;&nbsp;&nbsp;围";
			parent.document.getElementById("note").innerHTML="说明：分组汇总(汇总指标,汇总方式,分组指标[,分组级数[,当前列表|当前人员库[,条件]]])";
			parent.document.getElementById("explained").innerHTML="分组汇总(汇总指标,个数|平均数|总和|最大值|最小值,分组指标[,分组级数[,当前列表|当前人员库[,条件]]])";
			parent.document.getElementById("id").value=id;
			break;
			case "V_vol9":
			var exprestr = "在公式中使用单位子集指标时, 一般是取得人员所在单位的指标<br>有时, 需要取人员所在部门的指标, 此时可用本函数<br><br>";
			exprestr+="注意: 在一个公式中, 不能既取单位的值,又取部门的值. 当一个公式使用了\"取部门值\"时, 所有单位子集指标都是取对应部门的值 ";
			targetobj.innerHTML = exprestr;
			hiddenobj.value = '取部门值';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：GetE0122Value(指标名称)";
			parent.document.getElementById("explained").innerHTML="取部门值(指标名称)";
			parent.document.getElementById("id").value=id;
			break;
			case "V_volu9_20":
			var exprestr = "返回值：关联指标跟单位信息集中单位名称进行关联，取关联上的单位对应的结果指标内容，当此函数只有一个参数时，返回人员所在部门在单位信息集中对应的指标内容<br>";
			exprestr+="举例：“张三”，异动模板中变化前部门为“运输部”，变化后部门为“能源与环保”<br>取部门值(所属行业)<br>";
				exprestr+="返回：张三所在部门“运输部”的所属行业，如果张三部门为空，返回张三所在单位的所属行业<br> ";
			exprestr+="取部门值(变化后部门, 所属行业)<br>返回：张三变化后部门“能源与环保”部的所属行业<br>";
			exprestr+="注意：公式取单位信息集指标时，一般取单位对应的指标内容；有时需要取部门对应的指标内容，可用本函数。";
			targetobj.innerHTML = exprestr;
			hiddenobj.value = '取部门值';
			idstart(id);
			parent.document.getElementById("datastrcssaname").innerHTML="关联指标  ";
			parent.document.getElementById("datastrcss2name").innerHTML="结果指标  ";
			parent.document.getElementById("note").innerHTML="说明：(1)GetE0122Value(结果指标)<br>(2)GetE0122Value(关联指标,结果指标)";
			parent.document.getElementById("explained").innerHTML="&nbsp;取部门值(关联指标,结果指标)<br>&nbsp;取部门值(结果指标)";
			parent.document.getElementById("id").value=id;
			break;
			case "V_volp7_20":
			var exprestr = "返回值：关联指标跟岗位信息集中的岗位名称进行关联，取关联上的岗位对应的指标内容<br>举例：“张三”从“检修计划管理”岗（岗位属性为空）调到“机构点检员”（岗位属性为高温），那么人事异动业务中需要计算出此人变化前特殊工种标识以及变化后特殊工种标识。<br>取岗位值(拟岗位名称,岗位属性)<br>返回：张三变化后岗位的岗位属性，即高温";

			targetobj.innerHTML = exprestr;
			hiddenobj.value = '取岗位值';
			idstart(id);
			parent.document.getElementById("datastrcssaname").innerHTML="关联指标  ";
			parent.document.getElementById("datastrcss2name").innerHTML="结果指标  ";
			parent.document.getElementById("note").innerHTML="说明：	取岗位值(关联指标,结果指标)";
			parent.document.getElementById("explained").innerHTML="&nbsp;取岗位值(关联指标,结果指标)";
			parent.document.getElementById("id").value=id;
			break;
			case "V_vols6_3_11_10_10":
			var exprestr = "";   
			targetobj.innerHTML = exprestr;
			hiddenobj.value = '执行存储过程';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：<br> 执行存储过程(过程名：参数1,参数2,参数3,...)参数中可用\"当前表\"<br> 举例：<br>  参数为数字型： 10<br>  参数为字符型： 'AAAAAA'<br>  参数为日期型： #2001.01.02#";
			parent.document.getElementById("explained").innerHTML="执行存储过程(过程名：参数1,参数2,参数3,...)";
			parent.document.getElementById("id").value=id;
			parent.document.getElementById("conditionssubset0").innerHTML="过程名";
			parent.document.getElementById("conditionssubset1").innerHTML="参数1";
			parent.document.getElementById("conditionssubset").innerHTML="参数2";
			parent.document.getElementById("conditionssubset3").innerHTML="参数3";
			break;
			case "A_vol9_6_2_10_2":
			var exprestr = "说明：例子：序号(单据编号，部门,2,\"序号名称\",1|0)<br>返回值：单据编号生成规则=部门前2位编码+A0104指标按序号维护中规则生成的编号<br>"+
			"参数:1|0 按截取内容分类编号<br>1  表示按部门分组，每个部门人员的单据编号都从1开始连续编号<br>0  表示所有人的单据编号，按编号规则连续生成"+
			 "例如：<br>部门	姓名	单据编号（1）	单据编号（0）<br>01	张三	01001		01001<br>01	李四	01002		01002<br>02	王五	02001		02003<br><br>注意"+
			 "<br>（1）需到序号维护中分别定义：序号名称，序号名称_部门1前2位，序号名称_部门2前2位<br>（2）序号名称不能关联指标，必须是自定义的一个任意名称 ";   
			targetobj.innerHTML = exprestr;
			hiddenobj.value = '序号';
			idstart(id);
			parent.document.getElementById("note").innerHTML="序号(目标指标，参考值指标,n,\"序号名称\",1|0)";
			parent.document.getElementById("explained").innerHTML="序号(单据编号，部门,n,\"序号名称\",1|0)";
			parent.document.getElementById("id").value=id;
			parent.document.getElementById("datastrcssaname").innerHTML="目标指标";
			parent.document.getElementById("datastrcss1name").innerHTML="参考值指标";
			parent.document.getElementById("conditionssubset").innerHTML="序号字符串";
			parent.document.getElementById("initiationnumid").innerHTML="截取长度";
			
			break;
		case "5":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：常量";
			break;
		case "CC_con0":
			targetobj.innerHTML = '';
			hiddenobj.value = '真';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：逻辑常量(TRUE)";
			parent.document.getElementById("id").value=id;
			break;
		case "CC_con1":
			targetobj.innerHTML = '';
			hiddenobj.value = '假';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：逻辑常量(FALSE)";
			parent.document.getElementById("id").value=id;
			break;
		case "CC_con2":
			targetobj.innerHTML = '';
			hiddenobj.value = '空';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：空(日期)";
			parent.document.getElementById("id").value=id;
			break;
		case "CC_con3":
			targetobj.innerHTML = '';
			hiddenobj.value = '#2000.3.22#';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：#2000.3.22#表示2000年3月22日";
			parent.document.getElementById("id").value=id;
			break;
		case "CC_con4":
			targetobj.innerHTML = '';
			hiddenobj.value = '#3.22#';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：#3.22#表示3月22日";
			parent.document.getElementById("id").value=id;
			break;
		case "CC_con5":
			targetobj.innerHTML = '';
			hiddenobj.value = '"张三"';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：“张三”表示姓名为张三";
			parent.document.getElementById("id").value=id;
			break;
		case "6":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：逻辑操作符";
			break;
		case "LL_log0":
			targetobj.innerHTML = '';
			hiddenobj.value = '且';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：AND";
			parent.document.getElementById("id").value=id;
			break;
		case "LL_log1":
			targetobj.innerHTML = '';
			hiddenobj.value = '或';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：OR";
			parent.document.getElementById("id").value=id;
			break;
		case "LL_log2":
			targetobj.innerHTML = '';
			hiddenobj.value = '非';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：NOT";
			parent.document.getElementById("id").value=id;
			break;
		case "7":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML ="";
			break;
		case "OO_opr0":
			targetobj.innerHTML = '';
			hiddenobj.value = '+';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：加";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr1":
			targetobj.innerHTML = '';
			hiddenobj.value = '-';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：减";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr2":
			targetobj.innerHTML = '';
			hiddenobj.value = '*';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：乘";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr3":
			targetobj.innerHTML = '';
			hiddenobj.value = '/';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：除";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr4":
			targetobj.innerHTML = '';
			hiddenobj.value = '\\';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：整除";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr5":
			targetobj.innerHTML = '';
			hiddenobj.value = 'DIV';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：整除";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr6":
			targetobj.innerHTML = '';
			hiddenobj.value = '%';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：求余";
			parent.document.getElementById("id").value=id;
			break;
		case "OO_opr7":
			targetobj.innerHTML = '';
			hiddenobj.value = 'MOD';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：求余";
			parent.document.getElementById("id").value=id;
			break;
		case "8":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：关系运算符";
			break;
		case "RR_rel0":
			targetobj.innerHTML = '';
			hiddenobj.value = '=';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：等于";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel1":
			targetobj.innerHTML = '';
			hiddenobj.value = '>';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：大于";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel2":
			targetobj.innerHTML = '';
			hiddenobj.value = '>=';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：大于等于";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel3":
			targetobj.innerHTML = '';
			hiddenobj.value = '<';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：小于";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel4":
			targetobj.innerHTML = '';
			hiddenobj.value = '<=';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：小于等于";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel5":
			targetobj.innerHTML = '';
			hiddenobj.value = '<>';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：不等于";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel6":
			targetobj.innerHTML = '';
			hiddenobj.value = 'LIKE';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：包含";
			parent.document.getElementById("id").value=id;
			break;
		case "RR_rel7":
			hiddenobj.value = 'IN()';
			idstart(id);
			var exprestr = "IN操作符语法: IN (操作数列表)|本单位<br>操作数列表可以有两种, 字符串列表和整数列表, 字符串用双引号限定, 列表中操作数之间用,分隔.<br>";
			exprestr+="示例: 如果 指标名称 IN (\"01\",\"02\",\"03\",\"05\",\"08\") 那么 ....<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp 如果 指标名称 IN (1,2,3,8) 那么 .... ";
			targetobj.innerHTML = exprestr;
			parent.document.getElementById("note").innerHTML="说明：在...内";
			parent.document.getElementById("id").value=id;
			break;
		case "9":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：其他";
			break;
		case "EE_oth0":
			targetobj.innerHTML = '';
			hiddenobj.value = '(  )';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：括号";
			parent.document.getElementById("id").value=id;
			break;
		case "EE_oth1":
			targetobj.innerHTML = '';
			hiddenobj.value = '[  ] ';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：中括号[表示指标]";
			parent.document.getElementById("id").value=id;
			break;
		case "EE_oth2":
			targetobj.innerHTML = '';
			hiddenobj.value = '{  } ';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：大括号[临时变量]";
			parent.document.getElementById("id").value=id;
			break;
		case "EE_oth3":
			targetobj.innerHTML = '';
			hiddenobj.value = '// ';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：注释标识";
			parent.document.getElementById("id").value=id;
			break;
		case "10":
			togglesParent("stepDark");
			hidesParent("stepBrilliant");
			targetobj.innerHTML="";
			parent.document.getElementById("note").innerHTML="说明：工资函数";
			break;
		case "S_stan0":
			targetobj.innerHTML = '执行标准(标准号,横一,横二,纵一,纵二)<br>例子：执行标准(12, 档次, 空, 工资级别, 空)<br>返回值：找出工资级别、工资档次对应的结果值（级别工资）';
			hiddenobj.value = '执行标准';
			idstart(id);
			parent.document.getElementById("standname").innerHTML='标准表';
			parent.document.getElementById("note").innerHTML="说明：ExecuteStandard(标准号,横一,横二,纵一,纵二)";
			parent.document.getElementById("id").value=id;
			break;
		case "S_sthl1":
			targetobj.innerHTML = '例子：就近就高(12, 工资级别, 级别工资)<br>只支持代码型指标建立的，且只有一个横向指标及一个纵向指标的标准!';
			hiddenobj.value = '就近就高';
			idstart(id);
			parent.document.getElementById("standhlname").innerHTML='标准表';
			parent.document.getElementById("note").innerHTML="说明：NearByHigh(标准号,纵向指标,结果指标)";
			parent.document.getElementById("id").value=id;
			break;
		case "S_sthl2":
			targetobj.innerHTML = '例子：就近就低(12, 工资级别, 级别工资)<br>只支持代码型指标建立的，且只有一个横向指标及一个纵向指标的标准!';
			hiddenobj.value = '就近就低';
			idstart(id);
			parent.document.getElementById("standhlname").innerHTML='标准表';
			parent.document.getElementById("note").innerHTML="说明：NearByLow(标准号,纵向指标,结果指标)";
			parent.document.getElementById("id").value=id;
			break;
		case "S_item3_2_4":
			targetobj.innerHTML = '前一个代码(工资级别, 1, "03")<br>工资级别指标代码向前递一级，但不会超过"03"所代表级别<br>如"10"变为"09", "07"变为"06"';
			hiddenobj.value = '前一个代码';
			idstart(id);
			parent.document.getElementById("itemidname").innerHTML='前一个代码';
			parent.document.getElementById("decimalname").innerHTML='增量';
			parent.document.getElementById("codeMaxname").innerHTML='极值代码';
			parent.document.getElementById("note").innerHTML="说明：前一个代码(代码指标,增量,极值代码)";
			parent.document.getElementById("id").value=id;
			break;
		case "S_item4_2_4":
			targetobj.innerHTML = '后一个代码(工资级别, 1, "13")<br>工资级别指标代码向后递一级，但不会超过"13"所代表级别<br>如"08"变为"09", "07"变为"08"';
			hiddenobj.value = '后一个代码';
			idstart(id);
			parent.document.getElementById("itemidname").innerHTML='后一个代码';
			parent.document.getElementById("decimalname").innerHTML='增量';
			parent.document.getElementById("codeMaxname").innerHTML='极值代码';
			parent.document.getElementById("note").innerHTML="说明：后一个代码(代码指标,增量,极值代码)";
			parent.document.getElementById("id").value=id;
			break;
		case "S_item5_7_4_5":
			targetobj.innerHTML = '代码调整(工资级别, 调整级数, "03", "15")<br>代码调整(“05”,2, "03", "15") 返回值：07<br>代码调整(“05”,-2,"03","15") 返回值：03';
			hiddenobj.value = '代码调整';
			idstart(id);
			parent.document.getElementById("itemidname").innerHTML='代码调整';
			parent.document.getElementById("incrementalItemname").innerHTML='增量指标';
			parent.document.getElementById("codeMaxname").innerHTML='极大值代码';
			parent.document.getElementById("codeMinname").innerHTML='极小值代码';
			parent.document.getElementById("note").innerHTML="说明：代码调整(代码指标,增量指标,极大值代码,极小值代码)";
			parent.document.getElementById("id").value=id;
			break;
		case "SS_sar5":
			targetobj.innerHTML = '例子：分段计算(exp1)<br>&nbsp;&nbsp;基于子集 A02 满足 开始时间>=#1998.7.1#计算满足条件的∑(exp1)';
			hiddenobj.value = '前一个代码(现所学专业,0,"0101分段计算(exp1) 基于子集 ... [满足 ...]") ';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：基于一个子集的历史记录，计算满足条件的表达式exp1累计之和";
			parent.document.getElementById("id").value=id;
			break;
		case "SS_sar6":
			targetobj.innerHTML = '例子：分段计算2(A07,开始时间, 结束时间,<br>'
										+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exp1,"",<br>'
										+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;A10,聘任起始时间, 聘任终止时间,'
										+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exp2,"",<br>'
										+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;exp3,"#2004.10.1", "")<br>'
										+'计算2004.10.1后　∑(exp3*MAX(exp1,exp2))';
			hiddenobj.value = '分段计算2(子集1, 开始时间1, 结束时间1, exp1, "",\n '
								+'子集2, 开始时间2, 结束时间2, exp2, "",\n'
								+'expl3, "", "")';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：分段计算的扩展，是基于两个子集的历史记录，满足条件的历史";
			parent.document.getElementById("id").value=id;
			break;
		case "SS_sar7":
			targetobj.innerHTML = '例子：历史记录最初指标值(任职时间,  职务名称="03")<br>'
								+'返回指标必须是子集指标<br>'
								+'返回子集中最近符合条件的记录连续上溯到第一条记录的指标值';
			hiddenobj.value = '历史记录最初指标值(,)';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：历史记录最初指标值(返回指标，条件) ";
			parent.document.getElementById("id").value=id;
			break;
		case "SS_sar8":
			targetobj.innerHTML = '例子：历史记录最初指标值(任职时间,  职务名称="03")<br>'
								+'返回指标必须是子集指标<br>'
								+'返回子集中最近符合条件的记录连续上溯到第一条记录的指标值';
			hiddenobj.value = '历史记录最初指标值(,)';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：历史记录最初指标值(返回指标,条件)";
			parent.document.getElementById("id").value=id;
			break;
		case "SS_sar9":
			targetobj.innerHTML = '例子：上一个历史记录指标值(职务名称,  职务名称="03")<br>'
								+'返回指标必须是子集指标<br>'
								+'返回子集中最近符合条件的上一个不符条件的指标值';
			hiddenobj.value = '上一个历史记录指标值(,)';
			idstart(id);
			parent.document.getElementById("note").innerHTML="说明：上一个历史记录指标值(返回指标,条件)";
			parent.document.getElementById("id").value=id;
			break;
		
	} 	  
    simulateClick(targetobj);
}

function simulateClick(el) {
	var evt;
	if (document.createEvent) {
		evt = document.createEvent("MouseEvents");
		evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
		el.dispatchEvent(evt);
	} else if (el.fireEvent) {
		el.fireEvent('onclick');
	}
}

function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function togglesParent(targetId){
	if (parent.document.getElementById(targetId)){
		target = parent.document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hidesParent(targetId){
	if (parent.document.getElementById(targetId)){
		target = parent.document.getElementById(targetId);
		target.style.display = "none";
	}
}
function nextStep(){
    toggles("stepDark");
	hides("stepBrilliant");
	hides("darkReturnStep");
	toggles("brilliantReturnStep");
	hides("darkCompleted");
	toggles("brilliantCompleted");
	hides("selectifram");
	toggles("selectformula");
}
function returnStep(){
    hides("stepDark");
	toggles("stepBrilliant");
	toggles("darkReturnStep");
	hides("brilliantReturnStep");
	toggles("darkCompleted");
	hides("brilliantCompleted");
	hides("directioncss");
	hides("conditionscss");
	hides("datastrcss");
	hides("datastrcss1");
	hides("condstrview");
	
	hides("waycss");
	hides("templates");
	hides("conditionscss2");
	hides("conditionscss1");
	hides("conditionscss3");
	hides("datastrcss2");
	hides("fieldsetunitview");
	hides("fieldsetposview");
	hides("rangeidview");
	
	toggles("selectifram");
	hides("selectformula");
	hides("subsetstr");
	hides("subsetnum1");
	hides("subsetnum2");
	hides("subsetdate1");
	hides("subsetdate2");
	hides("decimalpoint");
	hides("initiationnum");
	hides("codeMax");
	hides("codeMin");
	hides("itemidSelect");
	hides("stand");
	hides("standHfactor");
	hides("standVfactor");
	hides("standS_hfactor");
	hides("standS_vfactor");
	hides("standhHighLow");
	hides("standItem");
	valueToNull();
}
function returnStepParent(){
    hidesParent("stepDark");
	togglesParent("stepBrilliant");
	togglesParent("darkReturnStep");
	hidesParent("brilliantReturnStep");
	togglesParent("darkCompleted");
	hidesParent("brilliantCompleted");
}
function idstart(id){
	if(id.length>0){
		var array = id.split("_");
		if(array.length>0){
			if(array[0].length==2){
				togglesParent("stepDark");
				togglesParent("brilliantCompleted");
				hidesParent("stepBrilliant");
				hidesParent("darkCompleted");
			}else{
				togglesParent("stepBrilliant");
				togglesParent("darkCompleted");
				hidesParent("stepDark");
				hidesParent("brilliantCompleted");
			}
		}
	}
}

function completed(){
	var formula = document.getElementById("formula").value;
	var id = document.getElementById("id").value;
	var array = id.split("_");
	var attribute = defvalue();
	if(array[0].length==2){
		window.returnValue = formula;
	}else if(id=="V_data4_2_1"){
		var returns = formula;
		var dateexpression1=document.getElementById("dateexpression1").value;
		var direction=document.getElementById("direction").value;
		var decimal=document.getElementById("decimal").value;
		 if(dateexpression1.length>0){
		 	var itemarr = dateexpression1.split(":"); 
			if(itemarr.length==2){
				returns += " " +itemarr[1]+" ";
			}
		 }
		 if(direction.length>0){
			returns += " " +direction+" ";
		 }
		 if(decimal.length>0){
			returns += " " +decimal+" ";
		 }else{
			returns += " 0 ";
		 }
		
		window.returnValue = returns + "条记录";
		
	}else if(id=="V_vol5_3_3"){
		var returns = formula;
		var datestr=document.getElementById("datestr").value;
		var conditions=document.getElementById("conditions").value;
		var way=document.getElementById("way").value;
		if(datestr.length>0){
			var itemarr = datestr.split(":"); 
			if(itemarr.length==2){
				returns += " " +itemarr[1]+" ";
			}
		}
		returns+= " 满足 ";
		if(conditions.length>0){
			returns+= " "+conditions+" ";
		}
		if(way.length>0){
			returns+= " "+way+" ";
		}
		window.returnValue = returns

	}else{
		 window.returnValue = formula+"("+attribute+")";
	}
	window.close();
}
function subsetfunction(){
	var id = document.getElementById("id").value;
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
					toggles("subsetstr");
					break;
				case 'code':
					toggles("subsetstr");
					break;
				case 'num':
					toggles("subsetnum1");
					break;
				case 'data':
					toggles("subsetdate1");
					break;
				case 'vol':
					toggles("datastrcss");
					break;
				case 'volc':
					toggles("itemidSelect");
					break;
				case 'item':
					toggles("itemidSelect");
					break;
				case 'stan':
					toggles("stand");
					break;
				case 'sthl':
					toggles("standhHighLow");
					break;
				case 'strs':
//					toggles("templates");
					break;
				case 'vols':
//					toggles("templates");
					break;
				case 'volu':
//					toggles("fieldsetview");
//					toggles("fieldsetunitview");
//					toggles("datastrcss");
					break;
				case 'volp':
//					toggles("fieldsetview");
//					toggles("fieldsetposview");
//					toggles("datastrcss");
					break;
			}
		}
		if(start3.length>0){
		switch (start3){
				case '1':
					toggles("subsetdate2");
					break;
				case '2':
					toggles("decimalpoint");
					break;
				case '3':
					toggles("conditionscss");
					break;
				case '4':
					toggles("subsetnum2");
					break;
				case '5':
					toggles("codeMax");
					break;
				case '6':
					toggles("datastrcss1");
					break;
				case '8':
					toggles("condstrview");
					break;
				case '9':
					toggles("templates");	
					break;
				case '10':
					toggles("partTimeJob");	
					break;
				case '20':
					toggles("datastrcss2");
					break;
			}
		}
		if(start4.length>0){
			switch (start4){
				case '1':
					toggles("directioncss");
					break;
				case '2':
					toggles("initiationnum");
					break;
				case '3':
					toggles("waycss");
					break;
				case '4':
					toggles("codeMax");
					break;
				case '5':
					toggles("codeMin");
					break;
				case '9':
					toggles("subsetstr");
					break;
				case '11':
					toggles("conditionscss1");
					break;
			}
		}
		if(start5.length>0){
			switch (start5){
				case '1':
					toggles("rangeidview");
					break;
				case '5':
					toggles("codeMin");
					break;
				case '10':
					toggles("conditionscss2");
					break;
			
		}
	}
	if(start6.length>0){
			switch (start6){
				case '1':
					toggles("conditionscss");
					break;
				case '10':
					toggles("conditionscss3");
					break;
			}
		}
}
}
function defvalue(){
	var atvalue="";
	var id = document.getElementById("id").value;
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
					var strexpression=document.getElementsByName("strexpression")[0].value;
					if(strexpression!=null&&strexpression.length>0){
						var itemarr = strexpression.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}
					}
					break;
				case 'code':
					var strexpression=document.getElementsByName("strexpression")[0].value;
					if(strexpression!=null&&strexpression.length>0){
						var itemarr = strexpression.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}
					}
					break;
				case 'num':
					var numexpression1=document.getElementsByName("numexpression1")[0].value;
					if(numexpression1!=null&&numexpression1.length>0){
						var itemarr = numexpression1.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}
					}
					break;
				case 'data':
					var dateexpression1=document.getElementsByName("dateexpression1")[0].value;
					if(dateexpression1!=null&&dateexpression1.length>0){
						var itemarr = dateexpression1.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}
					}
					break;
				case 'vol':
					var datestr=document.getElementsByName("datestr")[0].value;
					if(datestr!=null&&datestr.length>0){
						var itemarr = datestr.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}
					}
					break;
				case 'volc':
					var itemid=document.getElementById("itemid").value;
					if(itemid!=null&&itemid.length>0){
						var itemarr = itemid.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}else{
						atvalue+=itemarr[0];
						}
					}
					break;
				case 'item':
					var itemid=document.getElementById("itemid").value;
					if(itemid!=null&&itemid.length>0){
						var itemarr = itemid.split(":"); 
						if(itemarr.length==2){
							atvalue+=itemarr[1];
						}
					}
					break;
				case 'stan':
					var standid=document.getElementById("standid").value;
					var hfactor=getCodesid("hfactor_arr");
					var vfactor=getCodesid("vfactor_arr");
					var s_hfactor=getCodesid("s_hfactor_arr");
					var s_vfactor=getCodesid("s_vfactor_arr");
					if(standid.length>0){
						var itemarr = standid.split(":"); 
						if(itemarr.length==7){
							atvalue+=itemarr[6];
						}
						if(hfactor.length>0){
							var hfactorarr = hfactor.split(":");
							if(hfactorarr.length==2){
								atvalue+=','+hfactorarr[1];
							}else{
								atvalue+=",''";
							}
						}else{
							atvalue+=',空';
						}
						
						if(s_hfactor.length>0){
							var s_hfactorarr = s_hfactor.split(":");
							if(s_hfactorarr.length==2){
								atvalue+=','+s_hfactorarr[1];
							}else{
								atvalue+=",''";
							}
						}else{
							atvalue+=',空';
						}
						if(vfactor.length>0){
							var vfactorarr = vfactor.split(":");
							if(vfactorarr.length==2){
								atvalue+=','+vfactorarr[1];
							}else{
								atvalue+=",''";
							}
						}else{
							atvalue+=',空';
						}
						if(s_vfactor.length>0){
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
				case 'strs':
//					var template=document.getElementById("template").value;
//					if(template!=null&&template.length>0){
//						atvalue+=""+template;
//					}else{
//						atvalue+="";
//					}
					break;
				case 'vols':
//					var template=document.getElementById("template").value;
//					if(template!=null&&template.length>0){
//						atvalue+=""+template;
//					}else{
//						atvalue+="";
//					}
					break;
				case 'sthl':
					var standhlid=document.getElementById("standhlid").value;
					var vfactor=getCodesid("vfactor_arr");
					var s_vfactor=getCodesid("s_vfactor_arr");
					var item=getCodesid("item");
					if(standhlid.length>0){
						var itemarr = standhlid.split(":"); 
						if(itemarr.length==7){
							atvalue+=itemarr[6];
						}
						if(vfactor.length>0){
							var vfactorarr = vfactor.split(":");
							if(vfactorarr.length==2){
								atvalue+=','+vfactorarr[1];
							}
						}
						if(s_vfactor.length>0){
							var s_vfactorarr = s_vfactor.split(":");
							if(s_vfactorarr.length==2){
								atvalue+=','+s_vfactorarr[1];
							}else{
								atvalue+=',';
							}
						}else{
							if(vfactor.length<1){
								atvalue+=',';
							}
						}
						if(item!=null&&item.length>0){
							var itemarr = item.split(":");
							if(itemarr.length==2){
								atvalue+=','+itemarr[1];
							}
						}else{
							atvalue+=',';
						}
					}
					break;
				case 'volu':
					if(document.getElementById("datestr_item"))
					{ 	
						var datestr=document.getElementById("datestr_item").value;
						if(datestr!=null&&datestr.length>0){
							atvalue+=datestr;
						}
					}
					break;
				case 'volp':
					if(document.getElementById("datestr_item"))
					{
						var datestr=document.getElementById("datestr_item").value;
						if(datestr!=null&&datestr.length>0){
							atvalue+=datestr;
						}
					}
					break;
			}
		}
		
		if(start3.length>0){
		switch (start3){
				case '1':
					var dateexpression2=document.getElementById("dateexpression2").value;
					if(dateexpression2!=null&&dateexpression2.length>0){
						var itemarr = dateexpression2.split(":"); 
						if(itemarr.length==2){
							atvalue+=","+itemarr[1];
						}else{
							atvalue+=",";
						}
					}else{
						atvalue+=",";
					}
					break;
				case '2':
					var decimal=document.getElementById("decimal").value;
					if(id=="V_vols7_2"){
					if(decimal!=null&&decimal.length>0){
						atvalue+=decimal;
					}else{
						atvalue+="";
					}
					}else{
					if(decimal!=null&&decimal.length>0){
						atvalue+=","+decimal;
					}else{
						atvalue+=",";
					}
					}
					break;
				case '3':
					var conditions=document.getElementById("conditions").value;
					if(id=="V_vols6_3_11_10_10"){
					if(conditions!=null&&conditions.length>0){
						atvalue+=conditions;
					}else{
						atvalue+="";
					}
					break;
					}
					if(conditions!=null&&conditions.length>0){
						atvalue+=","+conditions;
					}else{
						atvalue+=",";
					}
					break;
				case '4':
					var numexpression2=document.getElementById("numexpression2").value;
					if(numexpression2!=null&&numexpression2.length>0){
						var itemarr = numexpression2.split(":"); 
						if(itemarr.length==2){
							atvalue+=","+itemarr[1];
						}else{
							atvalue+=",";
						}
					}else{
						atvalue+=",";
					}
					break;
				case '5':
					var codemax=getCodesid("code_maxarr");
					if(codemax!=null&&codemax.length>0){
						atvalue+=',"'+codemax+'"';
					}else{
						atvalue+=',""';
					}
					break;
				case '6':
					var strid=document.getElementById("strid").value;
					if(strid!=null&&strid.length>0){
						var itemarr = strid.split(":"); 
						if(itemarr.length==2){
							atvalue+=","+itemarr[1];
						}else{
							atvalue+=",";
						}
					}else{
						atvalue+=",";
					}
					break;
				case '7':
					var incrementalItemid=document.getElementById("incrementalItem_item").value;
					if(incrementalItemid!=null&&incrementalItemid.length>0){
						atvalue+=","+incrementalItemid;
					}else{
						atvalue+=",";
					}
					break;
				case '8':
					var statid=document.getElementById("statid").value;
					if(statid!=null&&statid.length>0){
						atvalue+=","+statid;
					}else{
						atvalue+=",";
					}
					break;
				case '9':
					var template=document.getElementById("template").value;
					if(template!=null&&template.length>0){
						atvalue+=""+template;
					}else{
						atvalue+="";
					}
					break;
				case '10': 
					var template=document.getElementById("partTimeJob_select").value;
					if(template!=null&&template.length>0){
						atvalue+='"'+template+'"';
					}else{
						atvalue+="";
					}
					break;	
				case '20':
					var strid2=document.getElementById("strid2_item").value;
					if(id=="V_volu9_20"){
						var datestr="";
						if(document.getElementById("datestr_item"))
						{ 
							datestr=document.getElementById("datestr_item").value;
						}
						if(datestr!=null&&datestr.length>0){
							if(strid2!=null&&strid2.length>0){
								atvalue+=","+strid2;
							}else{
								atvalue+=",";
							}	
						}else{
						if(strid2!=null&&strid2.length>0){
							atvalue+=strid2;
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
			}
			
		}
		if(start4.length>0){
			switch (start4){
				case '1':
					var direction=document.getElementById("direction").value;
					if(direction!=null&&direction.length>0){
						atvalue+=","+direction;
					}else{
						atvalue+=",";
					}
					break;
				case '2':
					var initiation=document.getElementById("initiation").value;
					if(initiation!=null&&initiation.length>0){
						atvalue+=","+initiation;
					}else{
						atvalue+=",";
					}
					break;
				case '3':
					var way=document.getElementById("way").value;
					if(way!=null&&way.length>0){
						atvalue+=","+way;
					}else{
						atvalue+=",";
					}
					break;
				case '4':
					var codemax=getCodesid("code_maxarr");
					if(codemax!=null&&codemax.length>0){
						atvalue+=',"'+codemax+'"';
					}else{
						atvalue+=',""';
					}
					break;
				case '5':
					var codemin=getCodesid("code_minarr");
					if(codemin!=null&&codemin.length>0){
						atvalue+=',"'+codemin+'"';
					}else{
						atvalue+=',""';
					}
					break;
				case '9':
					var strexpression=document.getElementById("strexpression").value;
					if(strexpression!=null&&strexpression.length>0){
						var strexpressionr = strexpression.split(":"); 
						if(strexpressionr.length==2){
							atvalue+=','+strexpressionr[1];
						}else{
						atvalue+=','+strexpressionr[0];
						}
					}else{
						atvalue+=',';
					}
					break;
					case '11':
					var itemid_item=document.getElementById("conditions1").value;
					if(id=="V_vols6_3_11_10_10"){
					var conditions=document.getElementById("conditions").value;
					if(conditions!=null&&conditions.length>0){
					var itemid_item2=document.getElementById("conditions2").value;
					var itemid_item3=document.getElementById("conditions3").value;
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
		if(id=="V_volc1_8_9_1_1"){
						atvalue+=',0';
						}
						
		if(start5.length>0){
			switch (start5){
				case '1':
					var rangeid=document.getElementById("rangeid").value;
					if(rangeid!=null&&rangeid.length>0){
						atvalue+=','+rangeid;
					}else{
					if(id=="V_volc1_8_9_1_1"){
						atvalue+=',0';
						}else
						atvalue+=',""';
					}
					break;
				case '5':
					var codemin=getCodesid("code_minarr");
					if(codemin!=null&&codemin.length>0){
						atvalue+=',"'+codemin+'"';
					}else{
						atvalue+=',""';
					}
					break;
				case '10':
					var itemid_item=document.getElementById("conditions2").value;
					if(id=="V_vols6_3_11_10_10"){
					var conditions=document.getElementById("conditions").value;
					if(conditions!=null&&conditions.length>0){
						var itemid_item1=document.getElementById("conditions1").value;
					var itemid_item3=document.getElementById("conditions3").value;
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
					var conditions=document.getElementById("conditions").value;
					if(conditions!=null&&conditions.length>0){
						atvalue+=','+conditions;
					}else{
						if(id=="V_volc1_8_9_1_1"){
							atvalue+=',1=1';
						}else{
							atvalue+=',""';
						}
					}
					break;
			case'10':
					var itemid_item=document.getElementById("conditions3").value;
						if(id=="V_vols6_3_11_10_10"){
					var conditions=document.getElementById("conditions").value;
					if(conditions!=null&&conditions.length>0){
						var itemid_item2=document.getElementById("conditions2").value;
					var itemid_item1=document.getElementById("conditions1").value;
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
}


function upadd(valueadd){
	var addValue  = document.getElementById(valueadd).value;
	if(addValue.length<1){
		addValue = 0;
	}
    document.getElementById(valueadd).value = parseInt(addValue)+1
}
function downcut(valuecut){
	var cutValue  = document.getElementById(valuecut).value;
	if(cutValue.length<1){
		cutValue = 0;
	}
	 if(cutValue.length>0&&parseInt(cutValue)<=1){
	if(id=="N_num4_2"){
		document.getElementById(valuecut).value = 1;
		return;
		}
	}
    document.getElementById(valuecut).value = parseInt(cutValue)-1
}
function attributeValue(){
	var atvalue="";
	var strexpression=document.getElementById("strexpression").value;
	var numexpression1=document.getElementById("numexpression1").value;
	var numexpression2=document.getElementById("numexpression2").value;
	var dateexpression1=document.getElementById("dateexpression1").value;
	var dateexpression2=document.getElementById("dateexpression2").value;
	var decimal=document.getElementById("decimal").value;
	var initiation=document.getElementById("initiation").value;
	var direction=document.getElementById("direction").value;
	var itemid=document.getElementById("itemid").value;
	var datestr=document.getElementById("datestr").value;
	var standhlid=document.getElementById("standhlid").value;
	var strid=document.getElementById("strid").value;
	var codemax=getCodesid("code_maxarr");
	var codemin=getCodesid("code_minarr");
	
	var standid=document.getElementById("standid").value;
	var hfactor=getCodesid("hfactor_arr");
	var vfactor=getCodesid("vfactor_arr");
	var s_hfactor=getCodesid("s_hfactor_arr");
	var s_vfactor=getCodesid("s_vfactor_arr");
	var item=getCodesid("item");
	
	if(standid.length>0){
		var itemarr = standid.split(":"); 
		if(itemarr.length==7){
			atvalue+=itemarr[6];
		}
		if(hfactor.length>0){
			var hfactorarr = hfactor.split(":");
			if(hfactorarr.length==2){
				atvalue+=','+hfactorarr[1];
			}else{
				atvalue+=",''";
			}
		}else{
			atvalue+=',空';
		}
		if(vfactor.length>0){
			var vfactorarr = vfactor.split(":");
			if(vfactorarr.length==2){
				atvalue+=','+vfactorarr[1];
			}else{
				atvalue+=",''";
			}
		}else{
			atvalue+=',空';
		}
		if(s_hfactor.length>0){
			var s_hfactorarr = s_hfactor.split(":");
			if(s_hfactorarr.length==2){
				atvalue+=','+s_hfactorarr[1];
			}else{
				atvalue+=",''";
			}
		}else{
			atvalue+=',空';
		}
		if(s_vfactor.length>0){
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
	if(standhlid.length>0){
		var itemarr = standhlid.split(":"); 
		if(itemarr.length==7){
			atvalue+=itemarr[6];
		}
		if(vfactor.length>0){
			var vfactorarr = vfactor.split(":");
			if(vfactorarr.length==2){
				atvalue+=','+vfactorarr[1];
			}
		}
		if(s_vfactor.length>0){
			var s_vfactorarr = s_vfactor.split(":");
			if(s_vfactorarr.length==2){
				atvalue+=','+s_vfactorarr[1];
			}else{
				atvalue+=',';
			}
		}else{
			if(vfactor.length<1){
				atvalue+=',';
			}
		}
		if(item.length>0){
			var itemarr = item.split(":");
			if(itemarr.length==2){
				atvalue+=','+itemarr[1];
			}
		}else{
			atvalue+=',';
		}
	}
	if(strexpression.length>0){
		var itemarr = strexpression.split(":"); 
		if(itemarr.length==2){
			atvalue+=itemarr[1];
		}
	}
	if(datestr.length>0){
		var itemarr = datestr.split(":"); 
		if(itemarr.length==2){
			atvalue+=itemarr[1];
		}
	}
	if(numexpression1.length>0){
		var itemarr = numexpression1.split(":"); 
		if(itemarr.length==2){
			atvalue+=itemarr[1];
		}
	}
	if(itemid.length>0){
		var itemarr = itemid.split(":"); 
		if(itemarr.length==2){
			atvalue+=itemarr[1];
		}
	}
	if(numexpression2.length>0){
		var itemarr = numexpression2.split(":"); 
		if(itemarr.length==2){
			atvalue+=","+itemarr[1];
		}
	}
	if(dateexpression1.length>0){
		var itemarr = dateexpression1.split(":"); 
		if(itemarr.length==2){
			atvalue+=itemarr[1];
		}
	}
	if(dateexpression2.length>0){
		var itemarr = dateexpression2.split(":"); 
		if(itemarr.length==2){
			atvalue+=","+itemarr[1];
		}
	}
	if(direction.length>0){
		atvalue+=","+direction;
	}
	if(decimal.length>0){
		atvalue+=","+decimal;
	}
	if(initiation.length>0){
		atvalue+=","+initiation;
	}
	if(codemax.length>0){
		atvalue+=',"'+codemax+'"';
	}
	if(codemin.length>0){
		atvalue+=',"'+codemin+'"';
	}
	if(strid.length>0){
		var itemarr = strid.split(":"); 
		if(itemarr.length==2){
			atvalue+=","+itemarr[1];
		}
	}
	return atvalue;
}
function getCodesid(code_arr){
	var codeid="";
	var codesetid_arr= document.getElementsByName(code_arr);
	if(codesetid_arr==null){
		return "";
	}else{
		var codesetid_arr_vo = codesetid_arr[0];
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
			}
		}
	}
	return codeid;
}
function arrToNull(code_arr){
	var codeid="";
	var codesetid_arr= document.getElementsByName(code_arr);
	if(codesetid_arr==null){
		return "";
	}else{
		var codesetid_arr_vo = codesetid_arr[0];
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codesetid_arr_vo.options[i].selected=false;
			}
		}
	}
}
function changeCodeValue(){
   var itemid=document.getElementById("itemid").value;
   var arr = itemid.split(":");
   if(arr.length==2){
   		var in_paramters="itemid="+arr[0];
  	 	var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
   }
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	
	if(codelist!=null&&codelist.length>1){
		AjaxBind.bind(projectForm.code_maxarr,codelist);
		if(document.getElementById("codeMin").style.display == "block"){
			AjaxBind.bind(projectForm.code_minarr,codelist);
		}
	}
}
function showFactor(outparamters){
	hides("standHfactor");
	hides("standVfactor");
	hides("standS_hfactor");
	hides("standS_vfactor");
	var hfactorlsit=outparamters.getValue("hfactorlsit");
	var vfactorlsit=outparamters.getValue("vfactorlsit");
	var s_hfactorlsit=outparamters.getValue("s_hfactorlsit");
	var s_vfactorlsit=outparamters.getValue("s_vfactorlsit");
	var str=outparamters.getValue("str");
	
	arrToNull("hfactor_arr");
	arrToNull("vfactor_arr");
	arrToNull("s_hfactor_arr");
	arrToNull("s_vfactor_arr");
	parent.document.getElementById("calculation").innerHTML=str;
	if(hfactorlsit!=null&&hfactorlsit.length>0){
		toggles("standHfactor");
		parent.document.getElementById("hfactorname").innerHTML='横一';
		AjaxBind.bind(projectForm.hfactor_arr,hfactorlsit);
	}
	if(vfactorlsit!=null&&vfactorlsit.length>0){
		toggles("standVfactor");
		parent.document.getElementById("vfactorname").innerHTML='纵一';
		AjaxBind.bind(projectForm.vfactor_arr,vfactorlsit);
	}
	if(s_hfactorlsit!=null&&s_hfactorlsit.length>0){
		toggles("standS_hfactor");
		parent.document.getElementById("s_hfactorname").innerHTML='横二';
		AjaxBind.bind(projectForm.s_hfactor_arr,s_hfactorlsit);
	}
	if(s_vfactorlsit!=null&&s_vfactorlsit.length>0){
		toggles("standS_vfactor");
		parent.document.getElementById("s_vfactorname").innerHTML='纵二';
		AjaxBind.bind(projectForm.s_vfactor_arr,s_vfactorlsit);
	}
}
function standSelect(){
	 var standid=document.getElementById("standid").value;
	 var codearr=document.getElementById("codearr").value;
	 if(standid.length>0){
	    var hashvo=new ParameterSet();
	    hashvo.setValue("standid",standid);
	    hashvo.setValue("codearr",codearr);
	    var request=new Request({asynchronous:false,
     		onSuccess:showFactor,functionId:'3020050012'},hashvo);
     }
}
function changeItemValue(){
   var standhlid=document.getElementById("standhlid").value;
   var codearr=document.getElementById("codearr").value;
   var strarr=document.getElementById("strarr").value;
   if(standhlid.length>0){
	    var hashvo=new ParameterSet();
	    hashvo.setValue("standid",standhlid);
	    hashvo.setValue("codearr",codearr);
	     hashvo.setValue("strarr",strarr);
	    var request=new Request({asynchronous:false,
     		onSuccess:showItemFactor,functionId:'3020050012'},hashvo);
    }
}
function showItemFactor(outparamters){
	hides("standVfactor");
	hides("standS_vfactor");
	hides("standItem");
	var vfactorlsit=outparamters.getValue("vfactorlsit");
	var s_vfactorlsit=outparamters.getValue("s_vfactorlsit");
	var itemidlist=outparamters.getValue("itemidlist");
	var hlstr=outparamters.getValue("hlstr");
	
	arrToNull("vfactor_arr");
	arrToNull("s_vfactor_arr");
	arrToNull("item");
	parent.document.getElementById("calculation").innerHTML=hlstr;
	
	toggles("standVfactor");
	parent.document.getElementById("vfactorname").innerHTML='纵向指标';
	AjaxBind.bind(projectForm.vfactor_arr,vfactorlsit);
	if(s_vfactorlsit!=null&&s_vfactorlsit.length>0){
		hides("standVfactor");
		toggles("standS_vfactor");
		parent.document.getElementById("s_vfactorname").innerHTML='纵向指标';
		AjaxBind.bind(projectForm.s_vfactor_arr,s_vfactorlsit);
	}
	if(itemidlist!=null&&itemidlist.length>0){
		toggles("standItem");
		parent.document.getElementById("itemname").innerHTML='结果指标';
		AjaxBind.bind(projectForm.item,itemidlist);
	}
}

function toggleSelect(itemtoArr){
	toggles(itemtoArr+"_arr");
	hides(itemtoArr+"_item");
	document.getElementById(itemtoArr+"_arr").focus();
}
function toggleText(arrtoItem){
	hides(arrtoItem+"_arr");
	toggles(arrtoItem+"_item");
	var itemvalue = document.getElementById(arrtoItem+"_arr").value;
	var itemarr = itemvalue.split(":");
	if(itemarr.length==2){
		document.getElementById(arrtoItem+"_item").value=itemarr[1];
	}else{
		document.getElementById(arrtoItem+"_item").value="";
	}
}


function valueToNull(){
    document.getElementsByName("strexpression")[0].value='';
	document.getElementsByName("numexpression1")[0].value='';
	document.getElementsByName("numexpression2")[0].value='';
	document.getElementsByName("dateexpression1")[0].value='';
	document.getElementsByName("dateexpression2")[0].value='';
	document.getElementsByName("decimal")[0].value='';
	document.getElementsByName("initiation")[0].value='';
	document.getElementsByName("direction")[0].value='';
	document.getElementsByName("itemid")[0].value='';
	document.getElementsByName("datestr")[0].value='';
	document.getElementsByName("standhlid")[0].value='';
	document.getElementsByName("strid")[0].value='';
	arrToNull("code_maxarr");
	arrToNull("code_minarr");

    document.getElementsByName("standid")[0].value='';
	arrToNull("hfactor_arr");
	arrToNull("vfactor_arr");
	arrToNull("s_hfactor_arr");
	arrToNull("s_vfactor_arr");
	arrToNull("item");
}