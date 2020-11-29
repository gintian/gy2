var historys=[]; //存历史记录用于返回上层部门
var task_type=""; //任务范围  0：所有   1：我负责的任务  2：我参与的任务
weeklysummary_me = undefined;
var humenmapInit = false; //人力地图初始化标志

//添加team历史
function addteamhistory(nbase,a0100,e01a1,flag)
{
	document.getElementById("returnId").style.display="none"; 
	var obj=new Object(); 
	obj.nbase=nbase; 
	obj.a0100=a0100; 
	obj.e01a1=e01a1; 
	obj.flag=flag; 
	obj.num=Ext.getDom('num_map').value; 
	historys.push(obj);
}
//添加sub_org历史
function addsuborghistory(e0122)
{
	var obj=new Object(); 
	obj.e0122=e0122; 
	obj.num=Ext.getDom('num_map').value; 
	historys.push(obj);
}

//清空历史
function cleanhistory()
{
	historys.length = 0;	
	document.getElementById("returnId").style.display="none"; 
}
//返回上级
function backSuper()
{
	document.getElementById("returnId").style.display="none"; 
	var objnum = historys.pop();
	var obj ;
	if(historys.length >0)
	obj=historys[historys.length-1];
	if( Ext.getDom('maptype').value == "teammap"){
	if(historys.length == 0)
	{
		inittype('teammap');
		initpersonmap('teammap','month','week','','','',objnum.num,'');
		queryTeamPerson('','','','');
	}else{
		inittype('teammap');
	    initpersonmap('teammap','month','week',obj.nbase,obj.a0100,obj.e01a1,objnum.num,obj.flag);
	    queryTeamPerson(obj.nbase,obj.a0100,'','');
	}
	}
	else if(Ext.getDom('maptype').value == "orgmap")
	{
		if(historys.length == 0)
		{
			inittype('orgmap');
			initpersonmap('orgmap','month','week','','','',objnum.num,'');
			queryTeamPerson('','','','');
		}else{
			inittype('orgmap');
		initpersonmap('orgmap','month','week','','',obj.e0122,objnum.num,'');
		queryTeamPerson('','',obj.e0122,'');
		}
	}
	
	
}
//删除节点，浏览器兼容
function removeElement(element){
         var parentElement = element.parentNode;
         if(parentElement){
                parentElement.removeChild(element);
         }
}
//隐藏，显示 
function display(id, targetId){
	var source = document.getElementById(id); 
	var dp = source.style.display;
	var isself = Ext.getDom('isself').value;   //是自己为me
	if(dp=="none"){
	    if (Ext.isDefined(targetId)) {
			var target = document.getElementById(targetId); 
			var left = target.offsetLeft;
			var top = target.offsetTop;
			while(target = target.offsetParent){
	        	left += target.offsetLeft;
	        	top += target.offsetTop;
	    	}
			
			source.style.position = "absolute";
			if("summarylist" == id)
			{
				if("me" == isself){
				    if (Ext.isEmpty(Ext.get('p0100').getValue())) {
				    	saveSummary(); // 自动保存 
				    }
				}
			}
			
			if ("typelist" == id) {
				source.style.left = left-5+"px";
				source.style.top = top+30+"px";
			} else if ("visibleList" == id) {
				source.style.left = left-13+"px";
				source.style.top = top+26+"px";
			} else if ("personlist" == id) {
				source.style.left = left+"px";
				source.style.top = top+27+"px";
				if("me" == isself){
				    if (Ext.isEmpty(Ext.get('p0100').getValue())) {
				    	saveSummary(); // 自动保存 
				    }
				}
			}
			else if ("selectmaplist" == id) {
				source.style.position = 'absolute';
				source.style.left = "0px";
				source.style.top = 48+"px"; 
			}else if ("durationlist" == id) {
				source.style.left = left+94+"px";
				source.style.top = top+"px"; 
			}else if ("monthlist" == id) {
				source.style.left = left+20+"px";
				source.style.top = top+30+"px";  
			} else {
				source.style.left = left+"px";
				source.style.top = top+30+"px"; 
			}
	    }
		source.style.display="block";
	} else{
		source.style.display="none";
	}
}

//选中 
function hover(obj)
{
	obj.style.background="#eee";
}
//移开
function unhover(obj){
	obj.style.background="#fff";
}

//显示人力地图
/*function initpersonmap(type,month,week,nbase,a0100,e01a1,pagenum)
{
	 var _month = document.getElementById(month).value;
 	 var _week = document.getElementById(week).value;
 	 document.getElementById("e01a1").value = e01a1;
 	 
 	 var num = document.getElementById('num_map').value;
 	 if(Number(pagenum)>0)
 	 {
 		num = pagenum;
 	 }
 	 var querytype = document.getElementById('type').value;
 	 
 	 //是否显示返回上级
 	// alert(historys.length);
 	if(historys.length > 0)
 		Ext.getDom('backSuperDiv').style.display = 'block';
 	else 
 		Ext.getDom('backSuperDiv').style.display = 'none';
 	
 	 var hashvo = new HashMap();
	hashvo.put("month",getEncodeStr(_month));
	hashvo.put("week",_week);
	hashvo.put("rownum",4);//几条
	hashvo.put("num",num);//第几页
	hashvo.put("type",type);
	hashvo.put("nbase",nbase);
	hashvo.put("a0100",a0100);
	hashvo.put("e01a1",e01a1);
	hashvo.put("querytype",querytype);
	hashvo.put("cycle", Ext.get('cycle').getValue());
	hashvo.put("year", Ext.get('year').getValue());
	var mapname = Ext.getDom('showselectmaplist');
	if("personmap" == type)
	{
		mapname.innerHTML="我关注的";
		document.getElementById("more_page").style.display="none"; 
		document.getElementById("person_page").style.display="block"; 
	}else if("personorgmap" == type)
	{
		mapname.innerHTML="我的部门";
		document.getElementById("more_page").style.display="none"; 
		document.getElementById("person_page").style.display="block"; 
	}else if("teammap" == type)
	{
		mapname.innerHTML="团队成员";
	
	}else if("orgmap" == type)
	{
		mapname.innerHTML="下属部门";
	}
	//document.getElementById("returnId").style.display="none"; 

	Ext.getDom('maptype').value=type;
	Ext.getDom('nbase').value=nbase;
	Ext.getDom('a0100').value=a0100;
	Rpc( {functionId : '9028000807',success : showmap}, hashvo);

}*/
/**
 * 
 * @param type
 * @param month
 * @param week
 * @param nbase
 * @param a0100
 * @param e01a1
 * @param pagenum
 * @param flag
 * @param isInit  是否是初始调用
 * @returns
 */
function initpersonmap(type,month,week,nbase,a0100,e01a1,pagenum,flag,isInit)
{
	 var _month = document.getElementById(month).value;
 	 var _week = document.getElementById(week).value;
 	 document.getElementById("e01a1").value = e01a1;
	 humenmapInit = isInit; 
 	 var num = document.getElementById('num_map').value;
 	 if(Number(pagenum)>0)
 	 {
 		num = pagenum;
 	 }
 	 var querytype = document.getElementById('type').value;
 	 
 	 //是否显示返回上级
 	if(historys.length > 0)
 		Ext.getDom('backSuperDiv').style.display = 'block';
 	else 
 		Ext.getDom('backSuperDiv').style.display = 'none';
 	
 	 var hashvo = new HashMap();
	hashvo.put("month",getEncodeStr(_month));
	hashvo.put("week",_week);
	hashvo.put("rownum",4);//几条
	hashvo.put("num",num);//第几页
	hashvo.put("type",type);
	hashvo.put("nbase",nbase);
	hashvo.put("a0100",a0100);
	hashvo.put("e01a1",e01a1);
	hashvo.put("flag",flag);
	hashvo.put("querytype",querytype);
	hashvo.put("cycle", Ext.get('cycle').getValue());
	hashvo.put("year", Ext.get('year').getValue());
	var mapname = Ext.getDom('showselectmaplist');
	if("personmap" == type)
	{
		mapname.innerHTML="我关注的";
		document.getElementById("more_page").style.display="none"; 
		document.getElementById("person_page").style.display="block"; 
	}else if("personorgmap" == type)
	{
		mapname.innerHTML="我的部门";
		document.getElementById("more_page").style.display="none"; 
		document.getElementById("person_page").style.display="block"; 
	}else if("teammap" == type)
	{
		mapname.innerHTML="团队成员";
	
	}else if("orgmap" == type)
	{
		mapname.innerHTML="下属部门";
	}
	//document.getElementById("returnId").style.display="none"; 

	Ext.getDom('maptype').value=type;
	Ext.getDom('nbase').value=nbase;
	Ext.getDom('a0100').value=a0100;
	Rpc( {functionId : '9028000807',success : showmap}, hashvo);

}
//人力地图翻页
function mapchangenum(va)
{
	var num=Number(Ext.getDom('num_map').value)+Number(va);
	var xshangEle = document.getElementById("xshangjpg");
	if(num >=2){
		if(xshangEle){
			xshangEle.style.display = "inline";
			xshangEle.style.margin = "0 0 0 50px";
		}
	}
	if(num == 1){
		if(xshangEle){
			xshangEle.style.display = "none";
		}
	}
	if(num < 1) return;
	Ext.getDom('num_map').value=num;
	
	if( Ext.getDom('maptype').value == "teammap"){		
		if(historys.length == 0)
		{
			initpersonmap(Ext.getDom('maptype').value,'month','week',Ext.getDom('nbase').value,Ext.getDom('a0100').value,Ext.getDom('e01a1').value,'0','');
		}else{				
			obj=historys[historys.length-1];
			initpersonmap(Ext.getDom('maptype').value,'month','week',obj.nbase,obj.a0100,obj.e01a1,'0',obj.flag);
		}
	}
	else 
	  initpersonmap(Ext.getDom('maptype').value,'month','week',Ext.getDom('nbase').value,Ext.getDom('a0100').value,Ext.getDom('e01a1').value,'0','');
}

function showmap(response) {
	var map = Ext.JSON.decode(response.responseText);
	var type = map.type;
	if("orgmap"==type){
		//后台用的计划的方法，返回格式不同，得调用另一个js加节点
		showorgmap(map);
		return;
	}
	var value = map.list;
	//人力地图下没有人员时，收起人力地图  haosl 20170616
	if(value.length==0 && humenmapInit)
		hideRightDiv();
	else
		showRightDiv();
	Ext.getDom('num_map').value=map.num;
	// 移除 数据 	
	var we = Ext.query("#personmapdiv dl");
	
	Ext.getDom("personmapdiv").style.height='';
	for ( var i = 0; i < we.length; i++) {
		removeElement(we[i]);
		//we[i].removeNode(true);
	}
	var xxiaEle = document.getElementById("xxiajpg");
	if(value == "" || value == null ||  value.length < 4){
		Ext.getDom("personmapdiv").style.height='400px';
		if(value == "" || value == null){
			xxiaEle.style.display = "none";
			return;
		}
	}
		var el= Ext.get('personmapdiv'); 
		var colarr; //列
		if(value.length < 8){
			if(xxiaEle){
				xxiaEle.style.display = "none";
			}
		}else{
			if(xxiaEle){
				xxiaEle.style.display = "inline";
				xxiaEle.style.margin = "0 0 0 50px";
			}
		}
		for ( var irow = 0; irow < value.length; irow++) {
			colarr = value[irow];
		if ("personmap" == type || "personorgmap" == type) {
			//Ext.getDom('maptype').value = "personmap";
			// Ext.getDom('nbase').value="";
			// Ext.getDom('a0100').value="";
			var innn = "";
			var showname = "";
			var delete_photo = "";
			if( typeof(colarr.p0900) != "undefined" && colarr.p0900 != "0")
			delete_photo ="onmouseout='photohide(this)' onmouseover='photoshow(this)'";
			if ("personmap" == type) {
				innn = "个人工作总结";
				showname = colarr.a0101;
				if ("2" == colarr.belong_type) {
					innn = "部门工作总结";
					showname = colarr.pos;
				}
			}else if("personorgmap" == type){
				showname = colarr.pos;
			}
		var b={ 
		tag: 'dl', 
		va: colarr[0],
		html: "<dt "+delete_photo+"><a href=\"javascript: showperdetail('"+colarr.a0100+"','"+colarr.nbase+"','"+colarr.a0101+"','"+colarr.E0122+"','"+colarr.belong_type+"');\"><img class='img-circle' title='"+colarr.a0101+"\n"+colarr.pos+"' src=\""+colarr.url+"\" /></a>" +
						  "<div style=\"position:absolute;display:none;z-index:666;width: 20px; height: 20px;margin-left:80px;margin-top:-55px;\"  onclick='if(confirm(\"确认不关注了吗?\")){deletephoto(this,\""+colarr.p0900+"\");}else{return false};' >"
						 +   "<img style=\"width: 20px; height: 20px;border:none;\" src=\"/workplan/image/remove.png\"/>"
						 + "</div></dt>"+
                        "<dd>"+showname+"</dd>"+
                        "<dd>"+innn+"</dd>"
			
		}; }
		else if("teammap"==type)
		{
			Ext.getDom('maptype').value="teammap";
			var str;
			var inn = "<dd>查看 <a href=\"javascript:inittype('teammap');addteamhistory('"+colarr.nbase+"','"+colarr.a0100+"','"+colarr.e01a1+"','"+colarr.flag+"'); initpersonmap('teammap','month','week','"+colarr.nbase+"','"+colarr.a0100+"','"+colarr.e01a1+"','1','"+colarr.flag+"');queryTeamPerson('"+colarr.nbase+"','"+colarr.a0100+"','"+colarr.e01a1+"','"+colarr.flag+"')\" >下属"+colarr.count+"人</a></dd>";
			if(colarr.count=="0")
			{
				inn="";
			}
			if(colarr.flag=="true"){
				 str="<dt><a href=\"javascript: showperdetail('"+colarr.a0100+"','"+colarr.nbase+"','"+colarr.a0101+"','','1');\"><img class='img-circle'  title='"+colarr.a0101+"\n"+colarr.org+"'  src=\""+colarr.url+"\" /></a></dt>"+
		         "<dd>"+colarr.a0101+"</dd>"+ inn
			}else{
				 str="<dt><img class='img-circle'  title='"+colarr.a0101+"\n"+colarr.org+"'  src=\""+colarr.url+"\" /></a></dt>"+
		         "<dd>"+colarr.a0101+"</dd>"+ inn
			}
		   
			var b={ 
					tag: 'dl', 
					va: colarr[0],
					html: str
					}; 
		}
		//追加 1 个子节点
		el.createChild(b);
		}
	
}

function queryTeamPerson(nbase,a0100,e01a1s,flag){
	Ext.getDom('flag').value = flag;
	Ext.getDom('e01a1s').value = e01a1s;
	// 加载团队成员工作总结提交情况
	initShowTeamOrUnderling();
	
	// 获取人力地图中的人员编号
	Ext.getDom('user_nbase').value=nbase;
	Ext.getDom('user_a0100').value=a0100;
	
	var hashvo = new HashMap();
	hashvo.put("nbase",nbase); 
	hashvo.put("a0100",a0100);
	hashvo.put("flag",flag);
	hashvo.put("e01a1s", e01a1s);
	hashvo.put("pagenum",Ext.get('pagenum').getValue());
	hashvo.put("type0",Ext.get('type').getValue());
	hashvo.put("cycle", Ext.get('cycle').getValue());
	hashvo.put("year", Ext.get('year').getValue());
	hashvo.put("month", Ext.get('month').getValue());
	hashvo.put("week", Ext.get('week').getValue());
	// 判断，已提交，未提交，以打分
	hashvo.put("stateSign","");

	Rpc({functionId : '9028000805',success : showPersonOK}, hashvo);
}
function showPersonOK(response){
	
	var value = response.responseText;
	var map = Ext.JSON.decode(value);

	var value = map.list;

	// 我的团队，还是我的下属部门
	var type = map.type;
	// 向表格添加数据 
	showTeamPeople(type,value);
	Ext.getDom('week').value=map.week;
	Ext.getDom('weekstart').value = map.weekstart;
	Ext.getDom('weekend').value = map.weekend;
	setSelWeekStyle(Ext.get('cycle').getValue(),0);
	if (type == "team") {
		Ext.getDom('totalNum').innerHTML = map.totalPeopleNumber+"人";
		Ext.getDom('scoreNum').innerHTML = map.scorePeopleNumber+"人";//已批
		Ext.getDom('p011503Num').innerHTML = map.approvePeopleNumber+"人";//已报
		Ext.getDom('p011501Num').innerHTML = map.totalPeopleNumber-map.approvePeopleNumber+"人";//未报
		Ext.getDom('notApproveNum').innerHTML = map.notApprovePeopleNumber+"人";//未批
	} else if (type == "sub_org") {
		Ext.getDom('subOrgTotalNum').innerHTML = map.totalPeopleNumber+"人";
		Ext.getDom('subOrgScore').innerHTML = map.scorePeopleNumber+"人";//已批
		Ext.getDom('subOrgP011503').innerHTML = map.approvePeopleNumber+"人";//已报
		Ext.getDom('subOrgP011501').innerHTML = map.totalPeopleNumber-map.approvePeopleNumber+"人";//未报
		Ext.getDom('subNotApproveNum').innerHTML = map.notApprovePeopleNumber+"人";//未批
	}
	showSummaryDesc();
	document.getElementById("more_page").style.display="block"; 
	document.getElementById("person_page").style.display="none";
}

function showTeamPeople(type,value){
	// 移除 数据 	
	var we = Ext.query("#tab tr");
	for ( var i = 0; i < we.length; i++) {
		removeElement(we[i]);
	}

	if (value == "" || value == null)
		return;
	
	// 区别个人与部门
	var personOrDepartSign = "0";
	if(type == "team")
		personOrDepartSign = "0";
	else if(type == "sub_org")
		personOrDepartSign = "2";
	
	//获取对象 
	var elem = document.getElementById("tab");
	var colarr; //列  
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
				if(colarr.A0100 == "")
					td.innerHTML = "<a href='###' title='暂无头像'><img class='img-circle' src='" + colarr.photoUrl +  "' /></a>";
				else{
					if(type == "team")
					    td.innerHTML = "<a href=\"javascript:showperdetail('"+colarr.a0100+"','"+colarr.nbase+"','"+colarr.a0101+"','','"+personOrDepartSign+"')\"><img class='img-circle' src='" + colarr.photoUrl +  "' /></a>";
					else if(type == "sub_org")
						td.innerHTML = "<a href=\"javascript:showorgperdetail('"+colarr.haveleader+"','"+colarr.nbaseA0100+"','"+colarr.e0122desc+"','"+colarr.a0101+"','"+colarr.e0122+"')\"><img class='img-circle' src='" + colarr.photoUrl +  "' /></a>";
					
				}
				break;
			case 1:
				td.width = "16%";
				if(colarr.a0101 == "")
					td.innerHTML = "（暂无）";
				else{
					if(type == "team")
						td.innerHTML = "<a href=\"javascript:showperdetail('"+colarr.a0100+"','"+colarr.nbase+"','"+colarr.a0101+"','','"+personOrDepartSign+"')\">" + colarr.a0101 + "</a>";
					else if(type == "sub_org")
						td.innerHTML = "<a href=\"javascript:showorgperdetail('"+colarr.haveleader+"','"+colarr.nbaseA0100+"','"+colarr.e0122desc+"','"+colarr.a0101+"','"+colarr.e0122+"')\">" + colarr.a0101 + "</a>";
				}
				break;
			case 2:
				td.width = "25%";
				if(type == "team")
					td.innerHTML = colarr.departName;
				else if(type == "sub_org"){
					td.innerHTML =  colarr.e0122desc
					 			 + " <input type='hidden' name='departName' value=\""+ colarr.e0122desc + "\" > ";
				}
				break;
			case 3:
				td.width = "20%";
				if (colarr.p0115 == "02") {
					td.innerHTML = "已提交";
				} else if (colarr.p0115 == "03") {
					td.innerHTML = "已批准";
				} else if (colarr.p0115 == "07") {
                    td.innerHTML = "已退回";
                } else
					td.innerHTML = "未提交";
				break;
			case 4:
				td.width = "35%";

				if (colarr.score == "-1" && colarr.p0115 != "01")
				{
					if(type == "team")
					td.innerHTML = "<span name='startId'><a href=\"javascript:showperdetail('"+colarr.a0100+"','"+colarr.nbase+"','"+colarr.a0101+"','','"+personOrDepartSign+"')\">待评价</a></span>";
					else if(type == "sub_org")
					td.innerHTML = "<span name='startId'><a href=\"javascript:showorgperdetail('"+colarr.haveleader+"','"+colarr.nbaseA0100+"','"+colarr.e0122desc+"','"+colarr.a0101+"','"+colarr.e0122+"')\">待评价</a></span>";
				}
					else if (colarr.score != "-1" && colarr.p0115 == "03"){
					td.innerHTML ="<span name='startId'></span>";
					_score.value = colarr.score;
					//显示分数
					initstar(Ext.query("[name=startId]")[irow]);
				}else {
					if(type == "team"){
					td.innerHTML = "<span name='startId'><a style='cursor:pointer;' onclick=\"fontgrey(this);sendEmail('"+ colarr.nbaseA0100 + "','one','','');\">提醒</a></span>"
								 + " <input type='hidden' name='hdnA0100' value=\""+ colarr.nbaseA0100 + "\" > ";
					} else if(type == "sub_org"){
					td.innerHTML = "<span name='startId'><a style='cursor:pointer;' onclick=\"fontgrey(this);sendEmail('"+ colarr.nbaseA0100 + "','one','','"+ colarr.e0122 + "');\">提醒</a></span>"
					+ " <input type='hidden' name='hdnA0100' value=\""+ colarr.nbaseA0100 + "\" > "
					+ " <input type='hidden' name='hdnE0122' value=\""+ colarr.e0122 + "\" > ";
					}
				}
				break;
			}
		}
	}
}

//查看个人总结
function showperdetail(a0100,nbase,a0101,e0122,belong_type)
{	
	document.getElementById("more_page").style.display="none"; 
	document.getElementById("person_page").style.display="block"; 
	
	var teamSign = Ext.get('type').getValue();
	if( teamSign == "team" ){
		document.getElementById("returnId").style.display="inline"; 
	}
	var map = new HashMap();
    map.put("belong_type",belong_type);
    map.put("maptype",Ext.getDom('maptype').value);
    map.put("cycle", Ext.get('cycle').getValue());
    map.put("year", Ext.get('year').getValue());
    map.put("month", Ext.get('month').getValue());
    map.put("week", Ext.get('week').getValue());
    map.put("a0100",a0100);
    map.put("nbase",nbase);
    map.put("a0101",a0101);
    map.put("e0122",e0122);
    if(historys.length >0 ){
		// 查询团队成员
		var obj = historys[historys.length-1];
		map.put("hisa0100",obj.a0100);
	    map.put("hisnbase",obj.nbase);
	    map.put("hise01a1",obj.e01a1);
	    map.put("hisflag",obj.flag);
	}else
	{
		map.put("hisa0100","");
	    map.put("hisnbase","");
	    map.put("hise01a1","");
	    map.put("hisflag","");
	}
    Rpc({functionId:'9028000809',success:queryOK},map); 
		
}
function showorgperdetail(haveleader,objid,e0122desc,a0101,e0122)
{
	if("" == objid){
		return;
	}
	document.getElementById("more_page").style.display="none"; 
	document.getElementById("person_page").style.display="block";
	var teamSign = Ext.get('type').getValue();
	if( teamSign == "sub_org" ){
		document.getElementById("returnId").style.display="inline"; 
	}
	if(a0101 != null && a0101 !=""){
		a0101="("+a0101+")";
	}
	Ext.query('[name=showdeptdesc]')[0].innerHTML = e0122desc;
	var map = new HashMap();
    map.put("belong_type","2");
    map.put("maptype",Ext.getDom('maptype').value);
    map.put("cycle", Ext.get('cycle').getValue());
    map.put("year", Ext.get('year').getValue());
    map.put("month", Ext.get('month').getValue());
    map.put("week", Ext.get('week').getValue());
    map.put("nbaseA0100",objid);
    map.put("haveleader",haveleader);
    map.put("a0101",a0101);
    map.put("e0122",e0122);
    Rpc({functionId:'9028000809',success:queryOK},map); 

}



//下属部门map
function showorgmap(map){
	var worklist = Ext.JSON.decode(map.info).orgmap;
	var strhtml="";	
	if(worklist.length > 0)
	{
		Ext.getDom('num_map').value=worklist[0].cur_page;
	}else{
		if(humenmapInit){
			hideRightDiv();
		}
	}
	Ext.getDom("personmapdiv").style.height='';
	if(worklist == "" || worklist == null ||  worklist.length < 4){
		Ext.getDom("personmapdiv").style.height='400px';
		if(worklist == "" || worklist == null)
		return;
	}
	for ( var i = 0; i < worklist.length; i++) {
		// 下属部门
		strhtml = strhtml + "<dl><dt>";
		if (worklist[i].isleader == "true") {
				strhtml = strhtml + "<a href=\"javascript: showorgperdetail('"+worklist[i].haveleader+"','"+worklist[i].objectid+"','"+worklist[i].e0122desc+"','"+worklist[i].name.split(' ')[0]+"','"+worklist[i].e0122+"');\" >";
		} else {
			strhtml = strhtml + "<a>"
		}
		strhtml = strhtml + "<img class='img-circle' title='"+worklist[i].name.replace(' ', '\n')+"' src='" + worklist[i].imagepath
				+ "'/></a></dt>";

		strhtml = strhtml + "<dd>" + worklist[i].e0122desc + "</dd>"
		
		if (worklist[i].subpeople != "") {
			strhtml = strhtml
					+ "<dd>查看 <a href=\"javascript:inittype('orgmap');addsuborghistory('"+worklist[i].e01a1+"');initpersonmap('orgmap','month','week','','','"+worklist[i].e01a1+"','1');queryTeamPerson('"+worklist[i].objectid+"','','','')\" >" + worklist[i].subpeople
					+ "</a></dd>"
		}

		strhtml = strhtml + "</dl>";
	}
	Ext.getDom("personmapdiv").innerHTML = strhtml;
	
}
/*function searchperson(obj)
{
	var p0100 = document.getElementById("p0100").value;
	var hashvo = new HashMap();
	var querytype = document.getElementById('type').value;
	hashvo.put("para",getEncodeStr(obj.value));
	hashvo.put("p0100",p0100);
	hashvo.put("rownum",10);
	hashvo.put("num",1);
	hashvo.put("type","searchperson");
	hashvo.put("querytype",querytype);
	hashvo.put("e0122",Ext.getDom('e0122').value);
	
	Rpc( {functionId : '9028000807',success : getsearchperson}, hashvo);
	
}

function getsearchperson(response) {
	var map = Ext.JSON.decode(response.responseText);
	var value = map.list;
	// 移除 数据 	
	var we = Ext.query("#personul li");
	for ( var i = 0; i < we.length; i++) {
		removeElement(we[i]);
		//we[i].removeNode(true);
	}
	
	if (value == "" || value == null)
		return;
	
	var el= Ext.get('personul'); 
	var colarr; //列
	for ( var irow = 0; irow < value.length; irow++) {
		colarr = value[irow];
		var org = colarr[4];
		var org1 = "";
		if(org.length > 11)
			org = org.substring(0,10)+"...";
		org1 = "("+org+")";
		var b={ 
			 tag: 'li', 
			 va: colarr[0],
			html: "<div class='clearfix' style='margin-top:10px;'>"
				 +   "<span style='float:left;'><img class='img-circle' style='margin-top: 5px' width='32px' height='32px' src='" + colarr[3] + "'/></span>"
				 +   "<div class='smember_list_info' style='margin-top: 5px; margin-left:10px;'>"
				 +       "<div class='smember_list_item_name' title='"+colarr[4]+"'>"+colarr[1]+org1+"</div>"
				 +       "<div class='smember_list_item_email' style='margin-top:2px;'>"+colarr[2]+"</div>"
				 +   "</div>"
				 +"</div>" 
		}; 
		//追加 1 个子节点
		el.createChild(b);
	}
	
	var el=Ext.query("#personul li");
	for(var i=0 ;i < el.length; i++)
 	{
		 el[i].onmouseover = function() {hover(this)};
		 el[i].onmouseout = function() {unhover(this)};
		 el[i].onclick = function() {addperson(this)};
 	}
}*/

//添加关注人:fuj
function addFollower(btn){
//	PersonPicker(btn)
//	.setCallback(function(c) {
//		var staffId=c.id;
//		addperson(staffId);
//	}).open();
	//原来的循环是有几个关注人就跟后台交互几次,改成获取全部关注人的id,用^连接,只需与后台交互一次(wusy)
    if(!privCheck())
        return;
	var picker = new PersonPicker({
		multiple: true,
		text: "添加关注人",
		isPrivExpression:false,//不启用高级权限
		callback: function (c) {
			var staffids = "";
			for (var i = 0; i < c.length; i++) {
				staffids += c[i].id + "^";
			}
			addperson(staffids);
		}
	}, btn);
	picker.open();
}

//ajax添加关注人 
function addperson(objectId){
	
	var p0100 = document.getElementById("p0100").value;
	if(p0100 == "")
	{
		Ext.Msg.alert("提示信息","总结还未提交，不能添加关注人！");
		return;
	}
	var hashvo = new HashMap(); 
	hashvo.put("objectId",objectId);
	hashvo.put("type","addperson");
	hashvo.put("p0100",p0100);
	
	Ext.get('personlist').setDisplayed(false);
	Rpc( {functionId : '9028000807',success : initpersonlist}, hashvo);
}

//显示新添加的人 
function initpersonlist(response)
{
	var map = Ext.JSON.decode(response.responseText);
	var list = map.list;
	for (var irow = 0; irow < list.length; irow++) {
		value = list[irow];
		//display('personlist');
		//Ext.query('#personlist input')[0].value = '输入姓名/拼音简码/email...';
		var el = Ext.get('photolist');
		var b = {
			tag: 'div',
			html: "<dl>" +
			"<dt onmouseout='photohide(this)' onmouseover='photoshow(this)'>" +
			"<img class='img-circle' src=\"" +
			value.url +
			"\" />" +
			"<div style=\"position:absolute;display:none;z-index:666;width: 20px; height: 20px;margin-left:26px;margin-top:-40px;\"  onclick='deletephoto(this,\"" +
			value.p0900 +
			"\")' >" +
			"<img style=\"width: 20px; height: 20px;\" src=\"/workplan/image/remove.png\"/>" +
			"</div>" +
			"</dt>" +
			"<dd>" +
			value.a0101 +
			"</dd>" +
			"</dl>"
		};
		//追加 1 个子节点
		el.createChild(b);
	}
}

//加载关注人 
function initphotolist()
{
	var p0100 = document.getElementById("p0100").value;
	var hashvo = new HashMap();
	hashvo.put("type","photolist");
	hashvo.put("p0100",p0100);
	Rpc( {functionId : '9028000807',success : getphoto}, hashvo);
}
function getphoto(response)
{
	// 移除 数据 	
	var we = Ext.query("#photolist dl");
	for ( var i = 0; i < we.length; i++) {
		removeElement(we[i]);
	}
	var map = Ext.JSON.decode(response.responseText);
	var value = map.list;
	var delete_photo ="";
	if("me" == Ext.getDom('isself').value || ("other" == Ext.getDom('isself').value &&  Ext.getDom('maptype').value != "personmap"))
		delete_photo ="onmouseout='photohide(this)' onmouseover='photoshow(this)'";
	var el= Ext.get('photolist'); 
	for ( var irow = 0; irow < value.length; irow++) {
		colarr = value[irow];
		var b={ 
			tag: 'div', 
			width: 32,
			html: "<dl>"
				 + "<dt "+delete_photo+">"
				 +   "<img class='img-circle' src=\""+colarr.url+"\" />"
				 + "<div style=\"position:absolute;display:none;z-index:666;width: 20px; height: 20px;margin-left:26px;margin-top:-40px;\"  onclick='deletephoto(this,\""+colarr.p0900+"\")' >"
				 +   "<img style=\"width: 20px; height: 20px;\" src=\"/workplan/image/remove.png\"/>"
				 + "</div>"
			     + "</dt>"
				 + "<dd>"+colarr.a0101+"</dd>" 
				 + "</dl>"
		}; 
		//追加 1 个子节点
		el.createChild(b);
	}
}

function photoshow(obj)
{
	obj.getElementsByTagName("div")[0].style.display='block';
	//obj.parentNode.style.marginTop='16px';
}

function photohide(obj)
{
	obj.getElementsByTagName("div")[0].style.display='none';
	//obj.parentNode.style.marginTop='20px';
}

function deletephoto(obj,id)
{
	//删除节点
	//obj.parentNode.parentNode.removeNode(true);
	removeElement(obj.parentNode.parentNode);
	var hashvo = new HashMap();
	hashvo.put("type","photodelete");
	hashvo.put("p0900",id);
	Rpc( {functionId : '9028000807'}, hashvo);
}

//汇总周总结
function collectSummary(cycle,collecttype)
{
    if(!privCheck())
        return;
	var year = Ext.getDom("year").value;
	var month = Ext.get('month').getValue();
	var belong_type = Ext.getDom('belong_type').value;
	var thisWorkSummary = Ext.getDom('thisWorkSummary').value;
	var nextWorkSummary = Ext.getDom('nextWorkSummary').value;
	var cyclenow = Ext.getDom("cycle").value;
	var e0122 = Ext.getDom('e0122').value;
	var e01a1 = Ext.getDom('b01ps').value;
	var week = Ext.get('week').getValue();

	var workTaskMap = new HashMap();
	workTaskMap.put("cyclenow", cyclenow); // 当前的类型类型，周报、月报、季报、半年报、年报
	workTaskMap.put("cycle", cycle); // 汇总的总结类型，周报、月报、季报、半年报、年报
	workTaskMap.put("year", year);
	workTaskMap.put("e0122", e0122);
	workTaskMap.put("e01a1", e01a1);
	workTaskMap.put("month", month);
	workTaskMap.put("week", week);
	workTaskMap.put("collecttype", collecttype);
	workTaskMap.put("belong_type", belong_type);
	
	workTaskMap.put("thisWorkSummary", thisWorkSummary);
	workTaskMap.put("nextWorkSummary", nextWorkSummary);
	Rpc( {functionId : '9028000810',success : getWorkSummary}, workTaskMap);
}
function getWorkSummary(response){
	var map = Ext.JSON.decode(response.responseText);
	
	var warning =map.warn;
	var flag = true ;
	if("warnperson" == warning)
	{
		if(!confirm("您的直接下属超过10人，系统只汇总前10人的工作总结，你确认要汇总吗？"))
		{
			flag =false ;
		}
	}else if("warnorg" == warning)
	{
		if(!confirm("您的直接下属部门超过10个，系统只汇总前10个部门的工作总结，你确认要汇总吗？"))
		{
			flag =false ;
		}
	}
	if(flag)
	{	Ext.getDom('thisWorkSummary').value = map.thisPlanTaskList;  
	Ext.getDom('nextWorkSummary').value = map.nextPlanTaskList;}
	adapt.adaptTextareaHeight();
}

// 自动汇总，本周工作总结，下周工作计划
function collectWorkPlan(){
    if(!privCheck())
        return;
	var thisWorkSummary = Ext.getDom('thisWorkSummary').value;
	var nextWorkSummary = Ext.getDom('nextWorkSummary').value;
	var p0100 = Ext.getDom('p0100').value;
	
	var nullSign = 15;
	
	var cycle = Ext.getDom("cycle").value;
	var year = Ext.getDom("year").value;
	var month = Ext.get('month').getValue();
	var week = Ext.get('week').getValue();
	var type = Ext.get('type').getValue();
	
	var workTaskMap = new HashMap();
	workTaskMap.put("planTaskContent", nullSign + "");
	workTaskMap.put("cycle", cycle); // 总结类型，周报、月报、季报、半年报、年报
	workTaskMap.put("selectedYear", year);
	workTaskMap.put("selectMonth", month);
	workTaskMap.put("week", week);
	workTaskMap.put("type", type);
	workTaskMap.put("e0122", Ext.getDom('e0122').value);
	workTaskMap.put("e01a1", Ext.getDom('b01ps').value);
	
	workTaskMap.put("p0100", p0100);
	workTaskMap.put("thisWorkSummary", thisWorkSummary);
	workTaskMap.put("nextWorkSummary", nextWorkSummary);
	
	Rpc( {functionId : '9028000808',success : getWorkTask}, workTaskMap);
}
function getWorkTask(response){
	var map = Ext.JSON.decode(response.responseText);
	
	
	// nullSignValue值为 = 7:两个都赋值；=4；给下周计划赋值；=3；给本周总结赋值
	var nullSignValue = map.nullSign;
	
	if(nullSignValue >= 4){
		Ext.getDom('nextWorkSummary').value = map.nextPlanTaskList; //"一、测试数据：下周的工作总结1\n二、测试数据：下周的工作总结2\n三、测试数据：下周的工作总结3"; 
		nullSignValue = nullSignValue -4;
	}
	if(nullSignValue >= 3)
		Ext.getDom('thisWorkSummary').value = map.thisPlanTaskList; //"一、测试数据：本周的工作总结1\n二、测试数据：本周的工作总结2\n三、测试数据：本周的工作总结3";// 
	adapt.adaptTextareaHeight();
}

/* 根据时间查询出，我的所有任务/我负责的任务/我参与的任务/我委托的任务
 * 
 * scope 任务范围  0：所有   1：我负责的任务  2：我参与的任务
 * 
 */
function getMyWorkTaskList(scope){
	task_type = scope; //任务范围 
	setSelWeekStyle(100,scope);
	
	var cycle = Ext.getDom("cycle").value;// 总结类型，周报、月报、季报、半年报、年报
	var year = Ext.getDom("year").value;
	var month = Ext.get('month').getValue();
	var week = Ext.get('week').getValue();
	var type = Ext.get('type').getValue();
	
	var user_a0100 = Ext.get('user_a0100').getValue();
	var user_nbase = Ext.get('user_nbase').getValue();
	
	var myWorkTaskMap = new HashMap();
	myWorkTaskMap.put("cycle", cycle); 
	myWorkTaskMap.put("selectedYear", year);
	myWorkTaskMap.put("selectMonth", month);
	myWorkTaskMap.put("week", week);
	myWorkTaskMap.put("type", type);
	myWorkTaskMap.put("scope", scope);
	myWorkTaskMap.put("a0100", user_a0100);
	myWorkTaskMap.put("nbase", user_nbase);
	myWorkTaskMap.put("e0122", Ext.getDom('e0122').value);
	Rpc( {functionId : '9028000808',success : returnMyTask}, myWorkTaskMap);
	
}

function openPlanTask(event,p0700,p0800,objectid,p0723){ 
	
	var cycle = Ext.getDom("cycle").value;// 总结类型，周报、月报、季报、半年报、年报
	var year = Ext.getDom("year").value;
	var month = Ext.get('month').getValue();
	var week = Ext.get('week').getValue();
	var type = Ext.get('type').getValue();
	var e0122= Ext.getDom('e0122').value;
	var user_a0100 = Ext.get('user_a0100').getValue();
	var user_nbase = Ext.get('user_nbase').getValue();
	
    var returnurl ="/workplan/work_summary.do?b_query=link&type="+type+"&cycle="+cycle+"&year="+year
    +"&month="+month+"&week="+week+"&nbase="+user_nbase+"&a0100="+user_a0100+"&e0122="+e0122;  
    returnurl= getEncodeStr(returnurl); 
    
    var url="/workplan/plan_task.do?br_task=link&p0700="+p0700
        +"&p0800="+p0800+"&objectid="+objectid+"&p0723="+p0723
        +"&returnurl="+returnurl;
    
    // 任务改由弹出小窗口代替打开新页面
    var taskFrame = document.getElementById("taskFrame");
    var leftArrow = document.getElementById("leftArrow");
    var iframe_task = window.frames["iframe_task"];
    iframe_task.location.href = url;
    
    // 页面滚动的高度
    var QUIRKS = document.compatMode == "BackCompat" ? true : false; // 怪异模式(BackCompat)
    var BODY = QUIRKS ? document.body : document.documentElement;
    var scrollTop = BODY.scrollTop;

    // 定位左箭头
    var e = event || window.event;
    var _top = e.clientY || e.offsetY || e.pageY;
    leftArrow.style.top = (_top + scrollTop - 7) + "px";
    leftArrow.style.display = "block";
    taskFrame.style.top = "0px";
    taskFrame.style.display = "block";
 }

 
function returnMyTask(response){
	var map = Ext.JSON.decode(response.responseText);
	
	var myWorkTaskMap = map.myWorkTaskList;
	
	// 移除 数据 	
	var tableList = Ext.query("#taskdetail tr");
	for ( var i = 0; i < tableList.length; i++) {
		removeElement(tableList[i]);
	}
	//获取对象 
	var elem = document.getElementById("planTab");   // 浏览器的兼容性
	
	if(myWorkTaskMap == ""){
		var tr_null = elem.insertRow(elem.rows.length); 
		var td_null = tr_null.insertCell(tr_null.cells.length);
		td_null.align = "center";
		td_null.innerHTML = "未找到相关任务！";
		return;
	}

	/*有值      行 的循环 
	 * colarr.p0801 	任务标题
	 * colarr.p0813		任务开始时间
	 * colarr.p0815		任务结束时间
	 * colarr.p0831		任务完成情况
	 * 
	 * 问题：显示是列是写固定了的，目前不可以动态
	 */ 
	for ( var irow = 0; irow < myWorkTaskMap.length; irow++) {
		var colarr = myWorkTaskMap[irow]; //列  
		var tr = elem.insertRow(elem.rows.length); 
		
		for ( var icol = 0; icol < 4; icol++) {
			var td = tr.insertCell(tr.cells.length);
			var showTaskName = "";
			switch (icol) {
			case 0:
				td.className = "hj-wzm-tdzb-td";
				td.width = "3%";
				td.innerHTML = "";
				break;
			case 1:
				td.width = "50%";
				// 显示指定长度 
				showTaskName = Ext.util.Format.ellipsis(colarr.p0801, 38, false);
				var levelnum = (colarr._level-1)*10; //按等级缩进
				td.className = "hj-wzm-tdzb-a";
				td.innerHTML = "<a onclick='openPlanTask(event,\""+colarr.link_p0700+"\",\""
				     +colarr.link_p0800+"\",\""+colarr.link_objectid+"\",\""+colarr.link_p0723+"\")' style='margin-left:"+levelnum+"px' title='"+ colarr.p0801 +"'>"+showTaskName+"</a>";
				break;
			case 2:
				td.width = "34%";

				td.innerHTML = colarr.timeHorizon;
				
				if(colarr.p0815 == "")
					break;
				
				var d = new Date();
				var strDate = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
				// 两个时间差
				var dateSubtract = daysBetween(colarr.p0815,strDate);
				if(dateSubtract < 0 && colarr.p0835 != 100)
					td.innerHTML += "<font color='red'>（逾期" + Math.abs(dateSubtract) + "天）</font>";
				
				break;
			case 3:
				td.width = "13%";
				
				if (colarr.p0835 == 100)
					td.innerHTML = "已完成";
				else if (Ext.isEmpty(colarr.p0835) || colarr.p0835 == 0)
					td.innerHTML = "0%";
				else
					td.innerHTML = colarr.p0835 +"%";
				break;
			}
		}
	}
}

/**
 * 获取两个时间的差值（时间格式 YYYY--MM--dd）
 * 
 * @param DateOne 区间的最后时间
 * @param DateNow 当前时间
 * @return
 */
function daysBetween(DateOne,DateNow)  
{   
    var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-'));  
    var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1);  
    var OneYear = DateOne.substring(0,DateOne.indexOf ('-'));  
  
    var TwoMonth = DateNow.substring(5,DateNow.lastIndexOf ('-'));  
    var TwoDay = DateNow.substring(DateNow.length,DateNow.lastIndexOf ('-')+1);  
    var TwoYear = DateNow.substring(0,DateNow.indexOf ('-'));  
  
    var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000);   
    return cha;  //Math.abs(cha)
}  
//初始化 map地图下拉
function initselectmaplist()
{
	var querytype = document.getElementById('type').value;
	var hashvo = new HashMap();
	 var _month = document.getElementById('month').value;
 	 var _week = document.getElementById('week').value;
 	 var hashvo = new HashMap();
	hashvo.put("month",getEncodeStr(_month));
	hashvo.put("week",_week);
	hashvo.put("cycle", Ext.get('cycle').getValue());
	hashvo.put("year", Ext.get('year').getValue());
	hashvo.put("type","initselectmaplist");
	hashvo.put("querytype",querytype);
	Rpc( {functionId : '9028000807',success : getinitselectmaplist}, hashvo);
}
function getinitselectmaplist(response)
{
	var add =Ext.query("#selectmaplist ul")[0];
	add.innerHTML="";
	var map = Ext.JSON.decode(response.responseText);
	var querytype = map.querytype;
		var html="";
		//if("true" == map.isperson)
		//{
		//}
		var showMapArrow  = false;
		if("true" == map.isorg)
		{
			showMapArrow = true;
			html=html+"<li><a href=\"javascript:cleanhistory();inittype('personorgmap');initpersonmap('personorgmap','month','week','','','','1','');showperdetail('','','','','2')\" >我的部门</a></li>";
		}
		if("true" == map.isteam)
		{
			showMapArrow = true;
			html=html+"<li><a href='###'  onclick=\"cleanhistory();inittype('teammap');initpersonmap('teammap','month','week','','','','1','');queryTeamPerson('','','','')\">团队成员</a></li>";
		}
		
		if("true" == map.issuborg)
		{
			showMapArrow = true;
			html=html+"<li><a href='###' onclick=\"cleanhistory();inittype('orgmap');initpersonmap('orgmap','month','week','','','','1','');queryTeamPerson('','','','')\" >下属部门</a></li>";
		}
		if(!showMapArrow)//只有关注人时箭头不显示   lis 20160628
			document.getElementById("maparrow").style.display = "none";
		if("person" == querytype){
			html=html+"<li><a href=\"javascript: cleanhistory();inittype('personmap');initpersonmap('personmap','month','week','','','','1','');showperdetail('','','','','0')\" >我关注的</a></li>";
		}
		add.innerHTML=html;
	
}
function inittype(type)
{
	if("personmap" == type)
	{
		Ext.getDom("type").value="person";
	}else if("personorgmap" == type)
	{
		Ext.getDom("type").value="org";
	}else if("teammap" == type)
	{
		Ext.getDom("type").value="team";
	
	}else if("orgmap" == type)
	{
		Ext.getDom("type").value="sub_org";
	}	
}

//总结默认选中周等标签的状态
function setSelWeekStyle(cycle,defautIndex) {
    var el=[];
    var em=[];
    var tasktype = false;
    if(document.getElementById('tasktypes'))
    {
        var type = document.getElementById('tasktypes').style.display;
        type=="none"?tasktype=false:tasktype=true;
    }
    if (1 == cycle) {
        el = Ext.query("#weeks a");
        var weeknum = Ext.getDom('weeknum').value;
        if( weeknum =="5") {
        	Ext.get('weeks').setWidth(400);
            Ext.get('fiveweek').show();
        } else {
        	Ext.get('weeks').setWidth(320);
            Ext.get('fiveweek').hide();
        }
        em = Ext.query("#months a");
    }
    else if (2 == cycle) 
    	em = Ext.query("#months a");
    else if (3 == cycle) {
        el = Ext.query("#quaters a");
    } else if (5 == cycle) {
        el = Ext.query("#halfyears a"); 
    } else if (100 == cycle && tasktype) {
    	el = Ext.query("#tasktypes a");
    }
    
    if (4 != cycle) {
    	if(2 != cycle){
    		for(var i=0 ;i < el.length; i++){
        		el[i].className="";
    		}
        }
    	if(2 == cycle || 1 == cycle)
        {
        	for(var i=0 ;i < em.length; i++){
        		em[i].style.backgroundColor="";
        		em[i].style.color="";
        	}
        }
        
        var j = 0;
       
        if (cycle<100) {
        	if (2 == cycle || 1 == cycle) {
            	j = Number(Ext.getDom("month").value)-1;
            	em[j].style.backgroundColor="#549FE3";
            	em[j].style.color="#ffffff";
            }
        	if(2!=cycle){
            j = Number(Ext.getDom("week").value)-1;
            el[j].className="hj-wzm-or-a";
            }
        }
        else if (cycle == 100 && tasktype) {
        	j = defautIndex;
        	el[3-j].className="hj-wzm-zb-three-top-a";
        }
    }
}

/**
 *  发送邮件，提示信息 
 *  
 * @param nbaseA0100 人员编号 ：nbase+A0100
 * @param num 发送类型，单发还是群发 
 * @param remindType 总结或评价 
 * @return
 */
function sendEmail(nbaseA0100,num ,remindType,e0122) {

	var str_a0100 = "";
	var saveDepartName = "";

	if (num == "one") {// 单个
		 if(nbaseA0100=="person")
		{
			str_a0100 = Ext.getDom("nbaseA0100").value;
			if(Ext.getDom("maptype").value=="orgmap")
				e0122 = Ext.getDom('e0122').value;	
		}
		else
			str_a0100 = nbaseA0100;

	} else if (num == "more") { // 群发（仅适用于我的团队的群发）
		// 获取未提交人员的 a0100 
		var a0100s = Ext.query('[name=hdnA0100]');
		
		var e0122s ;
		if(Ext.getDom("maptype").value=="orgmap")
		e0122s = Ext.query('[name=hdnE0122]');
		
		var departNames = Ext.query('[name=departName]');
		
		for ( var i = 0; i < a0100s.length; i++) {
			if(a0100s[i].value == ""){
				saveDepartName += departNames[i].value + ","
				continue;
			}
			str_a0100 += a0100s[i].value + ",";
			if(Ext.getDom("maptype").value=="orgmap")
			e0122 += e0122s[i].value + ",";
		}
	}

	if(str_a0100 == ""){
		if(Ext.getDom("type").value=="org")
			Ext.Msg.alert("提示信息","该部门没有负责人，邮件不能发送！");
//		else
//		alert("请选择邮件通知对象 ！ ");
		return;
	}
	
	if(saveDepartName != ""){
		if (!confirm(saveDepartName + "没有负责人，确定要给其他人发送邮件？")){
			return;
		}
	}
	
	var hashvo = new HashMap();
	hashvo.put("a0100", str_a0100);
    hashvo.put("type",Ext.getDom('type').value);
    hashvo.put("remindType",remindType);
    hashvo.put("cycle", Ext.get('cycle').getValue());
    hashvo.put("year", Ext.get('year').getValue());
    hashvo.put("month", Ext.get('month').getValue());
    hashvo.put("week", Ext.get('week').getValue());
    hashvo.put("e0122", e0122);
    hashvo.put("p0100", Ext.get('p0100').getValue());
    
	//if (confirm("您是否要发送邮件提醒？")) {
		Rpc( {
			functionId : '9028000804',
			success : sendOK
		}, hashvo);
	//}

}
function sendOK(response) {
	var value = response.responseText;
	var map = Ext.JSON.decode(value);
//成功与否都不提示
//	if (map.msg == null)
//		alert("发送失败！");
//	else {
//	}
}


// 返回
function returnBefore(){
	
	document.getElementById("returnId").style.display="none"; 
	var teamSign = Ext.get('type').getValue();
	if(teamSign == "person"){
		Ext.getDom("type").value = "team";
	}else if(teamSign == "org"){
		Ext.getDom("type").value = "sub_org";
	}
	document.getElementById("more_page").style.display="block"; 
	document.getElementById("person_page").style.display="none"; 
	initTeamPerson();
}


/*****我团队js*******我下属部门js***************************/

//加载信息 
function initShowTeamOrUnderling(){

	var sign = Ext.getDom('type').value;
	if(sign == "team"){
		//链接蓝色
		Ext.query('[name=fontbluet]')[0].style.color="#549FE3";
		document.getElementById("myTeam").style.display="block";
		document.getElementById("mySub_org").style.display="none";
	}else if(sign == "sub_org"){
		document.getElementById("myTeam").style.display="none";
		//链接蓝色
		Ext.getDom('fontblueo').style.color="#549FE3";
		document.getElementById("mySub_org").style.display="block";

	}
}

/*Ajax 查询团队成员  
 * 
 *stateSign 状态标志，（以提交，未提交，以打分） 为空时 就不按这个条件查询  
 *
 *stateSign 为空时查询全部 
 *stateSign 值  以提交=p011503,未提交=p011501,以打分 =score
 *与后台判断相关，慎重改动  
 */
function showTableByAjax(stateSign) {
	
	document.getElementById("more_page").style.display="block"; 
	document.getElementById("person_page").style.display="none"; 
	// 加载团队成员工作总结提交情况
	initShowTeamOrUnderling();
	
	var nbase = Ext.get("user_nbase").getValue();
	var a0100 = Ext.get("user_a0100").getValue();
	
	var hashvo = new HashMap();
	hashvo.put("nbase",nbase);
	hashvo.put("a0100", a0100);
	
	hashvo.put("flag",Ext.getDom('flag').value);//当flag=false时，是说明从人力地图的缺编岗位进来的
	hashvo.put("e01a1s",Ext.getDom('e01a1s').value);
	
	hashvo.put("type0",Ext.getDom('type').value);
	hashvo.put("cycle", Ext.get('cycle').getValue());
	hashvo.put("year", Ext.get('year').getValue());
	hashvo.put("month", Ext.get('month').getValue());
	hashvo.put("week", Ext.get('week').getValue());
	// 判断，已提交，未提交，以打分
	hashvo.put("stateSign",stateSign);

	Rpc({functionId : '9028000805',success : getOK}, hashvo);
}
function getOK(response) {

	var value = response.responseText;
	var map = Ext.JSON.decode(value);

	var value = map.list;
	// 我的团队，还是我的下属部门
	var type = map.type;
	// 显示团队 / 下属部门
	showTeamPeople(type,value);

	Ext.getDom('week').value=map.week;
	Ext.getDom('weeknum').value=map.weeknum;
	if (map.stateSign == "sign") {
		if (type == "team") {
			Ext.getDom('totalNum').innerHTML = map.totalPeopleNumber+"人";
			Ext.getDom('scoreNum').innerHTML = map.scorePeopleNumber+"人";//已批
			Ext.getDom('p011503Num').innerHTML = map.approvePeopleNumber+"人";//已报
			Ext.getDom('p011501Num').innerHTML = map.totalPeopleNumber-map.approvePeopleNumber+"人";//未报
			Ext.getDom('notApproveNum').innerHTML = map.notApprovePeopleNumber+"人";//未批
		} else if (type == "sub_org") {
			Ext.getDom('subOrgTotalNum').innerHTML = map.totalPeopleNumber+"人";
			Ext.getDom('subOrgScore').innerHTML = map.scorePeopleNumber+"人";//已批
			Ext.getDom('subOrgP011503').innerHTML = map.approvePeopleNumber+"人";//已报
			Ext.getDom('subOrgP011501').innerHTML = map.totalPeopleNumber-map.approvePeopleNumber+"人";//未报
			Ext.getDom('subNotApproveNum').innerHTML = map.notApprovePeopleNumber+"人";//未批
		}
	}
}

//加减年
function yearchange(va)
{
	var year = Ext.getDom('myeartitle');
	year.innerHTML = Number(year.innerHTML)+va;
	Ext.getDom('year').value = year.innerHTML;
}

//链接灰色
function fontgrey(obj)
{
	obj.style.color="#838383";
}


//隐藏右侧人力地图
function hideRightDiv(){
	document.getElementById("rightDiv").style.display = "none";
	document.getElementById("leftDiv").style.marginRight = "20px";
	document.getElementById("showRightDiv").style.display = "inline";
	if(weeklysummary_me && weeklysummary_me.mainpanel){
		var contentWidth = weeklysummary_me.getZjWidth();
		weeklysummary_me.mainpanel.setWidth(contentWidth);
}
}
//显示右侧人力地图
function showRightDiv(){
	document.getElementById("rightDiv").style.display = "inline";
	document.getElementById("leftDiv").style.marginRight = "164px";
	document.getElementById("showRightDiv").style.display = "none";
	if(weeklysummary_me && weeklysummary_me.mainpanel){
			var contentWidth = weeklysummary_me.getZjWidth();
			weeklysummary_me.mainpanel.setWidth(contentWidth);
	}
}


