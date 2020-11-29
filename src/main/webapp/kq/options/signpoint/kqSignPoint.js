//右键菜单选项点击事件
function addMenuMarker(menuLocation){
	AddFlag = 0;//初始化菜单添加标记。
	var MenuMarker = new BMap.Marker(menuLocation);
	geocode.getLocation(menuLocation,function(res){ point_city = res.addressComponents.city; });
	var inputHTML = "<table width='240' border=0 ><tr><td nowrap='nowrap' align='right' style='padding-right:15px;'>考勤点名称&nbsp; <input type='text' id='kqname' class='textColorWrite' value='' ></td></tr>";
	inputHTML+="<tr><td align='right' style='padding-right:15px;'><button class=\"mybutton\" onclick=\"savePoint('"+menuLocation.lng+","+menuLocation.lat+"')\">添加移动考勤点</button></td></tr><table>";
	baiduMap.addOverlay(MenuMarker);
	var infoWindow = new BMap.InfoWindow(inputHTML);
	infoWindow.addEventListener('close',function(event){if(!AddFlag)baiduMap.removeOverlay(MenuMarker); /*如果执行保存了，就不删除地图标记了*/});
	MenuMarker.openInfoWindow(infoWindow);
	
}

function checkKeys(event){
	if(event && event.keyCode==13){
		search();
	}
}


//搜索
function search(){
	baiduMap.clearOverlays();
   var sInfo = document.getElementById("searchbox").value;
   if(sInfo.length <1)
      return;
   getSearchResult(sInfo);//更精确的搜索
   /*var local = new BMap.LocalSearch(baiduMap, {
	  renderOptions:{
		  map: baiduMap,
	      autoViewport: true,
		  selectFirstResult: false
	  },
	  onMarkersSet:markerEvent,
	  onSearchComplete:function(res){if(res.getNumPois()<1){alert("抱歉，未找到相关地点。\n请缩小搜索范围。");}}
	});
	local.search(sInfo);*/
}

var markerEvent = function(pois){
	if(privFlag != 1)
		return;
	for(var i=0;i<pois.length;i++){
		var mkr = pois[i].marker;
		mkr.addEventListener("click",markerClick);
	}
};

//复写搜索结果点击事件
function markerOverWrite(){
	 var mkrs = baiduMap.getOverlays();
	 for(var i = 0;i<mkrs.length;i++){
		 var obj = mkrs[i];
        if(obj instanceof BMap.Marker){
           obj.addEventListener("click",markerClick);
		}
	 }

}

function markerClick(event){
	var point =event.target.getPosition();
	var location = point.lng+","+point.lat;
	var title = event.target.getTitle();
	geocode.getLocation(point,function(res){ point_city = res.addressComponents.city; });
	var inputHTML = "<table width='240' border=0 ><tr><td nowrap='nowrap' align='right' style='padding-right:15px;'>考勤点名称&nbsp; <input type='text' id='kqname' class='textColorWrite' value='"+title+"'></td></tr>";
	inputHTML+="<tr><td align='right' style='padding-right:15px;'><button class=\"mybutton\" onclick=\"savePoint('"+location+"')\">添加移动考勤点</button></td></tr><table>";
	
	this.openInfoWindow(new BMap.InfoWindow(inputHTML));
}


//保存考勤点
function savePoint(location){
	var sign_point_name = document.getElementById("kqname").value;
	//var geocode = new BMap.Geocoder();
	//geocode.getLocation(new BMap.Point(116.404, 39.915),function(res){alert( res.addressComponents.city); });
	
	 //var city = escape(point_city);
	 //city = city.replace(/%/g,"\\");

	  if(sign_point_name.trim()==null||sign_point_name.trim()=="")
	  {
		  alert("考勤点名称不能为空！");
		  return;
	  }
	 var map = new HashMap();
	  map.put("sign_point_name",sign_point_name);
	  map.put("location",location);
	  map.put("city",point_city);
      Rpc({functionId:'151211001123',success:saveok},map); 
	
	
}

function saveok(response){
	var value=response.responseText;
	var map=Ext.decode(value);
	if(map.saveRs == 'succeed'){
		var location = map.location.split(",");
		var pt = new BMap.Point(location[0], location[1]);
		var circle = new BMap.Circle(pt,pointRadius);
		   circle.setFillColor("#87CEFA");
		   circle.setStrokeWeight(2);
		   baiduMap.addOverlay(circle);
		   
		   var pid = map.pid;
		   var sign_point_name = document.getElementById("kqname").value;
		   parent.frames['mil_menu'].addTreeNode(pid,sign_point_name,point_city);
		   
		   
		   
		   AddFlag = 1;
		  
	}else if(map.saveRs == 'noPriv'){
		alert("无权限操作！");
	}
	
	 var mkrs = baiduMap.getOverlays();
		

		for(var i = 0;i<mkrs.length;i++){
			 var obj = mkrs[i];
			
	        if(obj instanceof BMap.Marker){
				obj.closeInfoWindow();

			}
		 }
 
}


function searchPerson(){
	   var db_arr='${kqSignPointForm.nbaseStr}'.split(",");
	   var persons=null;
	   persons=common_query_comrow("1",db_arr,"1",'5');
	   if(typeof(persons)!= "undefined"){
		      var map = new HashMap();
			  map.put("nbaseStr",db_arr);
			  map.put("persons",persons);
		      Rpc({functionId:'151211001127',success:setPersonList},map); 
	   }
}

function setPersonList(response){
	var value=response.responseText;
	var map=Ext.decode(value);
	var obj = document.getElementById("persons");
	parent.frames['mil_menu'].personArray = map.personList;
	bindSelectBox(obj,/*$('personlist')*/map.personList);
}

function bindSelectBox(elem,value)
{
		while (elem.childNodes.length > 0) {
			elem.removeChild(elem.childNodes[0]);
		}
		// bind data
		
		var indexOpt = document.createElement("OPTION");
		indexOpt.value="";
		indexOpt.text="";
		elem.options.add(indexOpt);
		
		for (var i = 0; i < value.length; i++) 
		{
			var option = document.createElement("OPTION");
			var data = value[i];
			if (data == null || typeof(data) == "undefined") {
				option.value = "";
				option.text = "";
			}
			if (typeof(data) != 'object') {
				option.value = data;
				option.text = data;
			} else {
				option.value = data.dataValue;
				option.text = data.dataName;	
			}
			elem.options.add(option);
		}
}

function checkPersonPoint(selectBox){
	var optionObj = selectBox.options[selectBox.selectedIndex];
	parent.frames['mil_menu'].personObj = [optionObj.text,optionObj.value];
	 kqSignPointForm.action="/kq/options/sign_point/setsign_point.do?b_showpoint=link&selectedA0100="+optionObj.value;
	 kqSignPointForm.submit();
}




//删除考勤点时调用
function cleanOverlays(){
	baiduMap.clearOverlays();
}

function initPointMarker(pid,location,isAdd,markerTitle){
	 var location = location.split(",");
	   var pt = new BMap.Point(location[0], location[1]);
	   
	   var marker;
	   if(selectedA0100.indexOf("`")>-1){
		   if(isAdd == '1'){
			   marker = new BMap.Marker(pt,{icon:icon});
			   marker.setIcon(icon);
		   }else{
			   marker = new BMap.Marker(pt);
			   marker.addEventListener("click",function(){ 
				   var selectBox = document.getElementById("date_box");
				   var personObj = parent.frames['mil_menu'].personObj;
				   if(confirm("添加 "+personObj[0]+" 到此考勤点吗？")){
					   addPerson2Point(pid,personObj[1],this);
					  
				   }
					   
			   });
		   }
		   
	   }else{
		   marker = new BMap.Marker(pt); 
	   }
	   marker.setTitle(markerTitle);
	   var circle = new BMap.Circle(pt,pointRadius);
	   circle.setFillColor("#87CEFA");
	   circle.setStrokeWeight(2);
	   baiduMap.addOverlay(circle);
	   baiduMap.addOverlay(marker);
}

function addPerson2Point(pid,a0100,marker){
	var map = new HashMap();
	  map.put("pid",pid);
	  map.put("a0100",a0100);
   Rpc({functionId:'151211001128',success:function(response){
   	var value=response.responseText;
   	var map=Ext.decode(value);
   	if(map.saveRs == 'succeed'){
   		marker.setIcon(icon);
   		alert('添加成功！');
   	}else{
   		alert('添加失败！');
   	}
   	
   }},map); 
   
}








function showDateSelectBox(srcobj)
{
		
   var div = document.getElementById("date_panel");
   div.style.display = 'block';
   var pos=getAbsPosition(srcobj);
	  with(div)
	  {
        style.position="absolute";
		style.posLeft=pos[0];
		style.posTop=pos[1]-srcobj.offsetHeight+42;
		style.width=(srcobj.offsetWidth<20)?150:srcobj.offsetWidth+1;
   }
	  var map = new HashMap();
	  map.put("personId",srcobj.value);
      Rpc({functionId:'151211001127',success:setPersonBox},map); 
}

function setPersonBox(response){
	var value=response.responseText;
	var map=Ext.decode(value);
	var obj = document.getElementById("date_box");
	//parent.frames['mil_menu'].personArray = map.personList;
	bindSelectBox(obj,/*$('personlist')*/map.personList);
}

function getAbsPosition(obj, offsetObj){
	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;
	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x += tmpObj.offsetLeft - tmpObj.scrollLeft + tmpObj.clientLeft;
		y += tmpObj.offsetTop - tmpObj.scrollTop + tmpObj.clientTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
}

function getSearchResult(searchWord){
	var map = new HashMap();
	  map.put("searchWord",searchWord);
    Rpc({functionId:'151211001138',success:handleSearchResult},map); 
}

function handleSearchResult(res){
	var value = res.responseText;
	var map = Ext.decode(value);
	var mess = map.mess;
	if(mess!='ok'){
		alert(mess);
		return;
	}
		
	
	var pointListStr = map.pointList;
	var pointList = Ext.decode(pointListStr);
	var center;
	for(var i=0;i<pointList.length;i++){
		var point = pointList[i];
		var pt = new BMap.Point(point.lng,point.lat);
		if(i==0)center=pt;
		var marker = new BMap.Marker(pt);
	        marker.setTitle(point.name);
	        marker.addEventListener("click",markerClick);
	        baiduMap.addOverlay(marker);
	}
	
	baiduMap.centerAndZoom(center,16);
}


var myValue;
function initSearchBox(){
	var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
		    {"input" : "searchbox"
		    ,"location" : baiduMap
		});
	ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
		var str = "";
		    var _value = e.fromitem.value;
		    var value = "";
		    if (e.fromitem.index > -1) {
		        value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		    }    
		    str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
		    
		    value = "";
		    if (e.toitem.index > -1) {
		        _value = e.toitem.value;
		        value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		    }    
		    str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
		    document.getElementById("searchResultPanel").innerHTML = str;
	}); 

	
	ac.addEventListener("onconfirm", function(e) {    //鼠标点击下拉列表后的事件
		var _value = e.item.value;
		    myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		    document.getElementById("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
		    
		    setPlace();
	});

}

function setPlace(){
	baiduMap.clearOverlays();    //清除地图上所有覆盖物
    function myFun(){
        var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
        baiduMap.centerAndZoom(pp, 18);
        var marker = new BMap.Marker(pp);
        var searchWord = document.getElementById("searchBox").value;
        marker.setTitle(searchWord);
        marker.addEventListener("click",markerClick);
        baiduMap.addOverlay(marker);    //添加标注
    }
    var local = new BMap.LocalSearch(baiduMap, { //智能搜索
      onSearchComplete: myFun
    });
    local.search(myValue);
}