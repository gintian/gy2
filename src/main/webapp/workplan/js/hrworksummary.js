var searchtype=Ext.getDom("searchtype").value;   //查询类型
var commonsearchtext=Ext.getDom("commonsearchtext").value;  //联合查询条件
var Const_QuickQueryHint="请输入姓名/拼音简称/部门/email";
/**
一键查询获取焦点
*/ 
function quickQueryTextFocus(obj){ 
    if (obj.value==Const_QuickQueryHint){
        obj.value=""; 
        obj.style.color="#000";
    
    } 
}
/**
一键查询失去焦点
*/
function quickQueryTextBlur(obj){ 
   if (obj.value==""){
       obj.value=Const_QuickQueryHint;  
       obj.style.color="#ccc";       
   }
}
//删除节点，浏览器兼容
function removeElement(element) {
	var parentElement = element.parentNode;
	if (parentElement) {
		parentElement.removeChild(element);
	}
}
// 隐藏，显示
function display(id, targetId) {
	var source = document.getElementById(id);
	var dp = source.style.display;
	if (dp == "none") {
		if (Ext.isDefined(targetId)) {
			var target = document.getElementById(targetId);
			var left = target.offsetLeft;
			var top = target.offsetTop;
			while (target = target.offsetParent) {
				left += target.offsetLeft;
				top += target.offsetTop;
			}

			source.style.position = "absolute";
			if ("typelist" == id) {
				source.style.left = left - 5 + "px";
				source.style.top = top + 30 + "px";
			} else if ("monthlist" == id) {
				source.style.left = left+20+"px";
				source.style.top = top+30+"px";  
			} else {
				source.style.left = left + "px";
				source.style.top = top + 25 + "px";
			}
		}
		source.style.display = "block";
	} else {
		source.style.display = "none";
	}

}


function showsearch() {
	if (Ext.query(".hj-wzm-cxgn")[0].style.display == "none") {
		Ext.query(".hj-wzm-shur img")[0].src = "/workplan/image/up.png";
		Ext.query(".hj-wzm-cxgn")[0].style.display = "block";
	} else {
		Ext.query(".hj-wzm-shur img")[0].src = "/workplan/image/down.png";
		Ext.query(".hj-wzm-cxgn")[0].style.display = "none";

	}
}

/**
联合查询
*/
function commonsearch()         
{ 
	searchtype="commonsearch";
	commonsearchtext=getcommonQueryText();
	Ext.getDom('pagenum').value = 1;
	queryTeamPerson("");
}
/**
获取普通查询条件
*/
function getcommonQueryText()
{
    var str="";
    var sels = Ext.query(".hj-zm-bumen");
    for(i=0;i<sels.length;i++)
    {
        if (sels[i].value==""){
          continue;
        }
        if (sels[i].name=="query_dept.viewvalue"){
          str=str+"`"+"e0122="+Ext.getDom("query_dept.value").value;
        }
        else {
            str=str+"`"+sels[i].name+"="+sels[i].value;
        }
    }
    return str;
}

//一键查询
function quicksearch()
{
	searchtype="quicksearch";
	Ext.getDom('pagenum').value = 1;
	queryTeamPerson("");
}
// 翻页
function changepagenum(num) {
	if (num == 0) {
		Ext.getDom('pagenum').value = 1;
	} else if (num == 2) {
		Ext.getDom('pagenum').value = Ext.getDom('lastpage').value;
	} else if (num == 3) {
		if (Number(Ext.getDom('gopage').value) > Number(Ext.getDom('lastpage').value)) {
			Ext.getDom('pagenum').value = Ext.getDom('lastpage').value;
		} else if (Number(Ext.getDom('gopage').value) == 0) {
			Ext.getDom('pagenum').value = 1;
		} else
			Ext.getDom('pagenum').value = Ext.getDom('gopage').value;
	} else {
		Ext.getDom('pagenum').value = Number(Ext.getDom('pagenum').value) + num;
		if( Number(Ext.getDom('pagenum').value) < 1)
			Ext.getDom('pagenum').value = 1;
		if( Number(Ext.getDom('pagenum').value) > Number(Ext.getDom('lastpage').value))
			Ext.getDom('pagenum').value = Ext.getDom('lastpage').value;
		
	}
	queryTeamPerson(Ext.get('stateSign').getValue());
}
function queryTeamPerson(stateSign) {

	fontblue();
	// 加载团队成员工作总结提交情况
	initShowTeamOrUnderling();

	var hashvo = new HashMap();
	hashvo.put("type0", Ext.get('type').getValue());
	hashvo.put("hr_pagesize", Ext.get('hr_pagesize').getValue()); // 每页几个
	// 判断，已提交，未提交，以打分,(判断是否切换的月报区间)
	if(("month" != stateSign) && "week" != stateSign){
		hashvo.put("stateSign", stateSign);
		Ext.getDom('stateSign').value = stateSign;
	}else{
		hashvo.put("monthOrWeek", stateSign);
	}
	//alert(stateSign);
	if(searchtype=="quicksearch") {
		var text = Ext.getDom("searchtext").value;
		// 检索条件获取到的如果是“请输入姓名/拼音简称/部门/email”，则置为空 chent 20180324 update
		if(text == Const_QuickQueryHint){
			text = '';
		} else {
			text = getEncodeStr(text);
		}
		hashvo.put("querypara", text);
	}
	if(searchtype=="commonsearch")
		hashvo.put("commonpara", getEncodeStr(commonsearchtext));
	hashvo.put("pagenum", Ext.get('pagenum').getValue()); // 当前页
	hashvo.put("cycle", Ext.get('cycle').getValue());
	hashvo.put("year", Ext.get('year').getValue());
	hashvo.put("month", Ext.get('month').getValue());
	hashvo.put("week", Ext.get('week').getValue());
	Rpc( {functionId : '9028000811',success : showPersonOK}, hashvo);
}

function init(map) {
	// Ext.getDom('belong_type').value=map.belong_type;
	Ext.getDom('type').value = map.type;
	Ext.getDom('week').value = map.week;
	Ext.getDom('weeknum').value = map.weeknum;
	Ext.getDom('pagenum').value = map.pagenum;
	Ext.getDom('cycle').value = map.cycle;
	Ext.getDom('year').value = map.year;
	Ext.getDom('month').value = map.month;
	Ext.getDom('weekstart').value = map.weekstart;
	Ext.getDom('weekend').value = map.weekend;
	Ext.getDom('yearListStr').value = map.yearList;
	Ext.getDom("summaryTypeJson").value = map.summaryTypeJson;
	Ext.get('yeartitle').setHtml(map.year);
	Ext.get('myeartitle').setHtml(map.year);
	Ext.get('timetitle').setHtml(map.year);
	Ext.get('monthtitle').setHtml(map.month);
	Ext.get('typetitle').setHtml(map.typetitle);
	var selvalue_b = Ext.getDom("cycle").value;
	initYears();
	// 除周报外，其它都不需要显示月份选择
	Ext.get("summarymonth").setDisplayed(1 == selvalue_b || 2 == selvalue_b);
	Ext.get("summaryyear").setDisplayed(1 != selvalue_b && 2 != selvalue_b);

	Ext.get('weeks').setDisplayed(1 == selvalue_b);
	Ext.get('quaters').setDisplayed(3 == selvalue_b);
	Ext.get('halfyears').setDisplayed(5 == selvalue_b);
	// Ext.get('months').setDisplayed(2==selvalue_b);
	Ext.get('typelist').setDisplayed(false);
	// Ext.get('typetitle').setHtml("工作周报");

}
function showPersonOK(response) {
	var value = response.responseText;
	var map = Ext.JSON.decode(value);
	if(!map.succeed){
		Ext.Msg.alert("提示信息",map.message,function(){
			document.body.innerHTML="";
		});
		return;
	}
	init(map);
	var summaryTypeJson = Ext.getDom("summaryTypeJson").value;
	loadSummaryTypeList(summaryTypeJson);
	var selvalue = Ext.getDom("cycle").value;
	if(selvalue!=1)
	    renderFillPeriod();
	if(map.DbNameMsg){
		Ext.Msg.alert('提示信息',map.DbNameMsg);
	}
	var value = map.list;
	// 我的团队，还是我的下属部门
	var type = map.type;
	// 向表格添加数据
	showTeamPeople(type, value);
	Ext.getDom('week').value = map.week;
	Ext.getDom('weekstart').value = map.weekstart;
	Ext.getDom('weekend').value = map.weekend;
	setSelWeekStyle(map.cycle, 0);
	Ext.getDom('page_now').innerHTML = "第" + map.pagenum + "页";
	Ext.getDom('page_count').innerHTML = "共" + map.totalPeopleNumber + "条";
	// 取大于等于 x，并且与它最接近的整数
	var linshival = Math.ceil(map.totalPeopleNumber/ Number(Ext.getDom('hr_pagesize').value));
	Ext.getDom('page_numcount').innerHTML = "共" + linshival + "页";
	Ext.getDom('lastpage').value = linshival; // 末页
	 
	if (map.pagenum == 1)// 第一页时隐藏‘首页’‘上一页’
	{
		Ext.query("[name=pageup]")[0].innerHTML = '首页';
		Ext.query("[name=pageup]")[1].innerHTML = '上页'	
	}
	else {
		Ext.query("[name=pageup]")[0].innerHTML = "<a  href='javascript:changepagenum(0)'>首页</a>";
		Ext.query("[name=pageup]")[1].innerHTML = "<a  href='javascript:changepagenum(-1)'>上页</a>";
	}

	if (map.pagenum == linshival || map.totalPeopleNumber==0)// 末页以及页面无记录时时隐藏‘末页’‘下一页’
	{
		Ext.query("[name=pagedown]")[0].innerHTML = '下页';
		Ext.query("[name=pagedown]")[1].innerHTML = '末页';
	}
	else {
		Ext.query("[name=pagedown]")[0].innerHTML = "<a  href='javascript:changepagenum(1)'>下页</a>";
		Ext.query("[name=pagedown]")[1].innerHTML = "<a  href='javascript:changepagenum(2)'>末页</a>";
	}
	
	if (map.stateSign == "sign") {
		if (type == "team") {
			Ext.getDom('totalNum').innerHTML = map.totalPeopleNumber+"人";//总人数
			Ext.getDom('scoreNum').innerHTML = map.scorePeopleNumber+"人";//已评
			Ext.getDom('ratified').innerHTML = map.approvePeopleNumber - map.scorePeopleNumber +"人";//未批
			Ext.getDom('p011503Num').innerHTML = map.approvePeopleNumber+"人";
			Ext.getDom('p011501Num').innerHTML = map.totalPeopleNumber- map.approvePeopleNumber+"人";
		} else if (type == "sub_org") {
            //总人数
			Ext.getDom('subOrgTotalNum').innerHTML = map.totalPeopleNumber+"人";
            //已评
			Ext.getDom('subOrgScore').innerHTML = map.scorePeopleNumber+"人";
            //已报
			Ext.getDom('subOrgP011503').innerHTML = map.approvePeopleNumber+"人";
            //未评
			Ext.getDom('subOrgP011502').innerHTML = map.approvePeopleNumber - map.scorePeopleNumber + "人";
            //未报
			Ext.getDom('subOrgP011501').innerHTML = map.totalPeopleNumber
			- map.approvePeopleNumber - map.scorePeopleNumber+"人";
		}
	}
	showSummaryDesc();
}

// 总结默认选中周等标签的状态
function setSelWeekStyle(cycle, defautIndex) {
	var el = [];
	var em = [];
	if (1 == cycle) {
		el = Ext.query("#weeks a");
		var weeknum = Ext.getDom('weeknum').value;
		if (weeknum == "5") {
			Ext.get('weeks').setWidth(400);
			Ext.get('fiveweek').show();
		} else {
			Ext.get('weeks').setWidth(320);
			Ext.get('fiveweek').hide();
		}
		em = Ext.query("#months a");
	} else if (2 == cycle)
		em = Ext.query("#months a");
	else if (3 == cycle) {
		el = Ext.query("#quaters a");
	} else if (5 == cycle) {
		el = Ext.query("#halfyears a");
	} else if (100 == cycle) {
		el = Ext.query("#tasktypes a");
	}

	if (4 != cycle) {
		if (2 != cycle) {
			for ( var i = 0; i < el.length; i++) {
				el[i].className = "";
			}
		}
		if (2 == cycle || 1 == cycle) {
			for ( var i = 0; i < em.length; i++) {
				em[i].style.backgroundColor = "";
				em[i].style.color = "";
			}
		}

		var j = 0;

		if (cycle < 100) {
			if (2 == cycle || 1 == cycle) {
				j = Number(Ext.getDom("month").value) - 1;
				em[j].style.backgroundColor = "#549FE3";
				em[j].style.color = "#ffffff";
			}
			if (2 != cycle) {
				j = Number(Ext.getDom("week").value) - 1;
				el[j].className = "hj-wzm-or-a";
			}
		} else if (cycle == 100) {
			j = defautIndex;
			el[3 - j].className = "hj-wzm-zb-three-top-a";
		}
	}
}

function showTeamPeople(type, value) {
	// 移除 数据
	var we = Ext.query("#tab tr");
	for ( var i = 0; i < we.length; i++) {
		removeElement(we[i]);
	}

	if (value == "" || value == null)
		return;

	// 区别个人与部门
	var personOrDepartSign = "0";
	if (type == "team")
		personOrDepartSign = "0";
	else if (type == "sub_org")
		personOrDepartSign = "2";

	// 获取对象
	var elem = document.getElementById("tab");
	var colarr; // 列
	// 行 的循环
	for ( var irow = 0; irow < value.length; irow++) {
		colarr = value[irow];
		var tr = elem.insertRow(elem.rows.length);
		for ( var icol = 0; icol < 5; icol++) {
			var td = tr.insertCell(tr.cells.length);
			switch (icol) {
			case 0:
				td.width = "4%";
				td.className = "hj-wzm-tdzb-td";
				if (colarr.A0100 == "")
					td.innerHTML = "<a href='###' title='暂无头像'><img class='img-circle' src='"
							+ colarr.photoUrl + "' /></a>";
				else {
					if (type == "team")
						td.innerHTML = "<a href=\"javascript:showperdetail('"
								+ colarr.a0100 + "','" + colarr.nbase + "','"
								+ colarr.a0101 + "','','" + personOrDepartSign
								+ "')\"><img class='img-circle' src='"
								+ colarr.photoUrl + "' /></a>";
					else if (type == "sub_org")
						td.innerHTML = "<a href=\"javascript:showorgperdetail('"
								+ colarr.haveleader
								+ "','"
								+ colarr.nbaseA0100
								+ "','"+colarr.e0122desc+"','"
								+ colarr.a0101
								+ "','"
								+ colarr.e0122
								+ "')\"><img class='img-circle' src='"
								+ colarr.photoUrl + "' /></a>";

				}
				break;
			case 1:
				td.width = "16%";
				if (colarr.a0101 == "")
					td.innerHTML = "（暂无）";
				else {
					if (type == "team")
						td.innerHTML = "<a href=\"javascript:showperdetail('"
								+ colarr.a0100 + "','" + colarr.nbase + "','"
								+ colarr.a0101 + "','','" + personOrDepartSign
								+ "')\">" + colarr.a0101 + "</a>";
					else if (type == "sub_org")
						td.innerHTML = "<a href=\"javascript:showorgperdetail('"
								+ colarr.haveleader
								+ "','"
								+ colarr.nbaseA0100
								+ "','"+colarr.e0122desc+"','"
								+ colarr.a0101
								+ "','"
								+ colarr.e0122
								+ "')\">" + colarr.a0101 + "</a>";
				}
				break;
			case 2:
				td.width = "25%";
				if (type == "team")
					td.innerHTML = colarr.departName;
				else if (type == "sub_org") {
					td.innerHTML = colarr.e0122desc
							+ " <input type='hidden' name='departName' value=\""
							+ colarr.e0122desc + "\" > ";
				}
				break;
			case 3:
				td.width = "20%";
				if (colarr.p0115 == "02") {
					td.innerHTML = "已提交";
				} else if (colarr.p0115 == "03") {
					td.innerHTML = "已批准";
				} else
					td.innerHTML = "未提交";
				break;
			case 4:
				td.width = "35%";
				 if (colarr.p0115 == "02") {
						td.innerHTML = "<span name='startId'><a style='cursor:pointer;' onclick=\"fontgrey(this);sendEmail('"+ colarr.nbaseA0100+ "','one','publish','"+ colarr.e0122+ "','"+(colarr.p0100 || '')+"');\">提醒上级批准工作总结</a></span>";
					
				 }else if (colarr.score == "-1" && (colarr.p0115 == "03")){
					td.innerHTML = "<span name='startId'><a style='cursor:pointer;' onclick=\"fontgrey(this);sendEmail('"+ colarr.nbaseA0100+ "','one','contents','"+ colarr.e0122+ "','"+(colarr.p0100 || '')+"');\">提醒上级评价工作总结</a></span>";
				 }else if (colarr.score != "-1" && colarr.p0115 == "03") {
					td.innerHTML = "<span name='startId'></span>";
					_score.value = colarr.score;
					// 显示分数
					initstar(Ext.query("[name=startId]")[irow]);
				} else {
					td.innerHTML = "<span name='startId'><a style='cursor:pointer;' onclick=\"fontgrey(this);sendEmail('"+ colarr.nbaseA0100+ "','one','','"+ colarr.e0122+ "','"+(colarr.p0100 || '')+"');\">提醒写总结</a></span>";
				}
				break;
			}
		}
	}
}

function showTableByAjax(stateSign) {
	Ext.getDom('pagenum').value = 1;
	//Ext.getDom('plan_status').value = "";
	//statusvalue('','全部总结');
	queryTeamPerson(stateSign);
}

// 加载信息
function initShowTeamOrUnderling() {

	if (Ext.getDom('type').value == "team") {
		document.getElementById("myTeam").style.display = "inline";
		document.getElementById("mySub_org").style.display = "none";

	} else {
		document.getElementById("myTeam").style.display = "none";
		document.getElementById("mySub_org").style.display = "inline";

	}
}
/*
 * 重新查询
 */
function resetHR()
{
	var sels = Ext.query(".hj-zm-bumen");
	statusvalue('','全部总结');
	for(i=0;i<sels.length;i++)
    {
        sels[i].value="";
        if (sels[i].name=="query_dept.viewvalue"){
         Ext.getDom("query_dept.value").value="";
        }
        
    }
    commonsearch();
}

//加减年
function yearchange(va) {
	var year = Ext.getDom('myeartitle');
	year.innerHTML = Number(year.innerHTML) + va;
	Ext.getDom('year').value = year.innerHTML;
}

//跳转
function showperdetail(a0100,nbase,a0101,e0122,belong_type)
{
	var cycle = Ext.getDom("cycle").value;// 总结类型，周报、月报、季报、半年报、年报
	var year = Ext.getDom("year").value;
	var month = Ext.get('month').getValue();
	var week = Ext.get('week').getValue();
	var searchpara = '';
	if(searchtype=="quicksearch")
		searchpara = "&querypara="+$URL.encode(Ext.getDom("searchtext").value);
	if(searchtype=="commonsearch")
		searchpara = "&commonpara="+ $URL.encode(commonsearchtext);
	var returnurl =	"/workplan/work_summary_track.do?br_query=link&type=team&searchtype="+searchtype+searchpara;
		
    var url ="/workplan/work_summary.do?b_query=link&type=person&belong_type=0&ishr=true&cycle="+cycle+"&year="+year
    +"&month="+month+"&week="+week+"&nbase="+nbase+"&a0100="+a0100+"&e0122="+e0122+"&returnurl="+$URL.encode(getEncodeStr(returnurl));
    location.href =url;
}

//部门跳转
function showorgperdetail(haveleader,objid,e0122desc,a0101,e0122)
{
	var cycle = Ext.getDom("cycle").value;// 总结类型，周报、月报、季报、半年报、年报
	var year = Ext.getDom("year").value;
	var month = Ext.get('month').getValue();
	var week = Ext.get('week').getValue();
	var searchpara = '';
	if(searchtype=="quicksearch")
		searchpara = "&querypara="+ $URL.encode(Ext.getDom("searchtext").value);
	if(searchtype=="commonsearch")
		searchpara = "&commonpara="+ $URL.encode(commonsearchtext);
	var returnurl =	"/workplan/work_summary_track.do?br_query=link&type=sub_org&searchtype="+searchtype+searchpara;
	
    var url ="/workplan/work_summary.do?b_query=link&type=org&belong_type=2&ishr=true&cycle="+cycle+"&year="+year
    +"&month="+month+"&week="+week+"&a0101="+$URL.encode(getEncodeStr(a0101))+"&nbaseA0100="+objid+"&deptdesc="+$URL.encode(e0122desc)+"&e0122="+e0122+"&returnurl="+$URL.encode(getEncodeStr(returnurl));
    location.href =url;
}

/**
 * 链接灰色
 * */
function fontgrey(obj)
{
	obj.style.color="#838383";
}
/**
 * 链接蓝色
 * */
function fontblue()
{
	var cccc = Ext.query(".hj-zm-tixing a");
	for ( var irow = 0; irow < cccc.length; irow++) {
		cccc[irow].style.color="#549FE3";
	}
}

/**
 * 根据区间类型动态显示填报周期
 */
function renderFillPeriod(){
   var jsonValue = Ext.getDom("summaryTypeJson").value;
   if(!jsonValue ||jsonValue.length==0)
       return;
   var summaryTypeJson = Ext.decode(jsonValue);
   
   var months = Ext.query("#months li",false);
   var halfyears = Ext.query("#halfyears a",false);
   var quaters = Ext.query("#quaters a",false);
  
   for(var i in summaryTypeJson){
       var s3 = summaryTypeJson[i].s3//月总结 配置
       var s2 = summaryTypeJson[i].s2//季度总结 配置
       var s1 = summaryTypeJson[i].s1//半年总结 配置
       //月报
       if(s3 && s3.cycle){
          var cycle = ","+s3.cycle+",";
          for(var j=0;j<months.length;j++){
              if(cycle.indexOf(","+(j+1)+",")<0)
                 months[j].setDisplayed(false);
          }
       }
       //季度总结
       if(s2 && s2.cycle){
           var cycle = ","+s2.cycle+",";
           for(var j=0;j<quaters.length;j++){
              if(cycle.indexOf(","+(j+1)+",")<0)
                  quaters[j].setDisplayed(false);
           }
       }
       //半年总结
       if(s1 && s1.cycle){
          var cycle = ","+s1.cycle+",";
          for(var j=0;j<halfyears.length;j++){
              if(cycle.indexOf(","+(j+1)+",")<0)
                  halfyears[j].setDisplayed(false);
          }
       }
   }
}
/**
 * 显示查看的人的已启用的总结类型列表
 */
function loadSummaryTypeList(summaryTypeJson){
    if(!summaryTypeJson || summaryTypeJson.length==0)
        return;
    var summaryTypeArr = Ext.decode(summaryTypeJson);
    var typeList = Ext.getDom("typelist");
    var ul = typeList.getElementsByTagName('ul')[0];
    var li = '';
    for(var i in summaryTypeArr){
        var item = summaryTypeArr[i];
        if(item.s4)
            li+='<li ><a href="###" onclick="hidemonth(0,\'工作周报\',1)" >工作周报</a></li>';
        else if(item.s3)
         li+= '<li ><a href="###" onclick="hidemonth(0,\'工作月报\',2)" >工作月报</a></li>';
        else if(item.s2)    
            li+= '<li ><a href="###" onclick="hidemonth(0,\'季度总结\',3)" >季度总结</a></li>';
        else if(item.s1)    
            li+= '<li ><a href="###" onclick="hidemonth(0,\'半年总结\',5)" >半年总结</a></li>';
        else if(item.s0)    
            li+= '<li ><a href="###" onclick="hidemonth(0,\'年度总结\',4)" >年度总结</a></li>';
   }
    if(li.length>0)
        ul.innerHTML = li;
}