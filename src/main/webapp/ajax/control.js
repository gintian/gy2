var _fileIncluded_control=true;

function _dataPilot_getDataset() {
	return this.dataset;
}

function getLabel(dataset)
{
		var title=" <span style='padding-top:5px;height:20px' > 第 "+dataset.getPageIndex()+	" 页 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+" </span>";
	//	var title=" 第 "+dataset.getPageIndex()+	" 页 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页";  //dengcan 2012-3-3
		return title;
}
function createLabel(dataset,row)
{
	var cell=row.insertCell();
	cell.innerHTML=getLabel(dataset);
	cell.setAttribute("valign","bottom"); 
	return cell;
}

function moveToPage(dataset,gopages)
{
	var pageobj=document.getElementById(gopages);
	var pageindex=pageobj.value;
	if(getInt(pageindex)==0)
		return;
	dataset.moveToPage(pageindex);
	if(getInt(dataset.pageCount)>=pageindex){
		var datapilotm=document.getElementById('pilot'+dataset.id);//changxy 20161101 跳页 页面条不刷新 18323
		var title=" <span style='padding-top:5px;height:20px' > 第 "+pageindex+  " 页 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+" </span>";
		if(datapilotm&&datapilotm.celllabel){
    		datapilotm.celllabel.innerHTML=title;
    	}
	}
}
function createEveryPage(dataset,row)
{
			var formname=dataset.formname;//"accountingForm";//
			if(formname=="")
				return false;
			var cell=row.insertCell();	
			var num = dataset.pageSize;	
			var url="this.document."+formname+ ".submit()";
			var content=" &nbsp&nbsp&nbsp每页 <input class='text4' type='text' size='4' name='pagerows' value='";

			content=content+num;
			content=content+"' onkeypress='checkNumber(this,event)' onChange='checkNum(this)'>条<a href='javascript:"+url;
			content=content+"'>刷新</a> &nbsp&nbsp";
			content=content+"第<input class='text4' type='text' size='4' id='gopages' value='' ";
			content=content+"' onkeypress='checkNumber(this,event)'>页&nbsp";
			content=content+"<a href='javascript:moveToPage(";
			content=content+dataset.id;
			content=content+",\"gopages\")";
			content=content+"'><img src=\"/images/go.gif\" border=0 align=\"absmiddle\"></a>&nbsp&nbsp";				

			cell.innerHTML=content;
			return true;
	/*
	 *         String url = "this.document." + name + ".submit()";
		    buf.append(ResourceFactory.getProperty("label.every.page"));
	        buf.append("&nbsp<input type='text' size='4' name='pagerows' ");
	        buf.append(" value='");
	        buf.append(pagerows);
	        buf.append("' onkeypress='checkNumber(this,event)'>&nbsp");
	        buf.append("<a href=\"javascript:" + url);
	        buf.append("\">");
	        buf.append(ResourceFactory.getProperty("label.page.refresh"));
	        buf.append(" </a>&nbsp");
	        */
}
function checkNum(obj){
	var num = obj.value;
	if(num>500){
		obj.value=500;
	}
}
function _dataPilot_setLabel(dataset,pageindex)
{
		if(!this.bflag)
			return;
	//	var title="第 "+pageindex+	" 页 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+"   ";   //dengcan 2012-3-3
		var title=" <span style='padding-top:5px;height:20px' > 第 "+dataset.record.pageIndex+	" 页 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+" </span>";
		this.celllabel.innerHTML=title;
}

function initDataPilot(dataPilot){
	dataPilot.getDataset=_dataPilot_getDataset;
	dataPilot.setLabel=_dataPilot_setLabel;
	var dataset=getElementDataset(dataPilot);
	if (!dataPilot.getAttribute("pageSize")){
		if (dataset) dataPilot.pageSize=dataset.pageSize;
	}
	var pageSize=dataPilot.getAttribute("pageSize");

	for(var i=0; i<dataPilot.tBodies[0].rows.length; i++){
		var row=dataPilot.tBodies[0].rows[i];
		row.removeNode(true);
	}
	var bflag=false;
	var buttons_str=getValidStr(dataPilot.getAttribute("buttons"));
	if (buttons_str=="" || compareText(buttons_str, "default"))
		buttons_str="movefirst,prevpage,moveprev,movenext,nextpage,movelast,appendrecord,deleterecord,cancelrecord,postrecord";
	else if (compareText(buttons_str, "readonly"))
		buttons_str="movefirst,prevpage,moveprev,movenext,nextpage,movelast";
	else if(compareText(buttons_str, "bottom"))
	{
		buttons_str="movefirst,prevpage,nextpage,movelast";
		bflag=true;
	}
		
	buttons_str=buttons_str.toLowerCase();
	var buttons=buttons_str.split(",");

	var row=dataPilot.tBodies[0].insertRow();
	row.align="center";
	/**第x页,如果导航条在下面，则加*/
	var nadd=0;
	if(bflag)
	{
		dataPilot.celllabel=createLabel(dataset,row);
		nadd++;
		if(createEveryPage(dataset,row))
			nadd++;
	}
	dataPilot.bflag=bflag;
	dataPilot.ncols=nadd;
	
	for(var i=0; i<buttons.length; i++){ 
		if(buttons[i]=="saverecord")//for editable save
		  continue;
		//if(bflag)
		//	btn=document.createElement("<input type=button style=\"background-color:transparent;border:0\" hideFocus=true style=\"height: 22px\">");
		//else
		//{
			btn=document.createElement("<input type=button class=button hideFocus=true style=\"height: 22px\">");
			//btn.style.backgroundImage = "url("+_theme_root+"/button.gif)";		
			btn.onmouseover=_button_onmouseover;
			btn.onmouseout=_button_onmouseout;
		//}
		btn.tabIndex=-1;
		btn.onclick=_datapilot_onclick;
		btn.dataset=dataPilot.getAttribute("dataset");
		btn.buttonType=buttons[i];
		btn.datapiolt=dataPilot;

		switch(buttons[i]){
			case "movefirst":{
//				btn.value="首页";
				btn.style.fontFamily="Webdings";
				btn.value="9";
			   btn.title=constDatasetMoveFirst;
				btn.style.width=30;
				break;
			}
			case "prevpage":{
//				btn.value="上页";
				btn.style.fontFamily="Webdings";
				btn.value="7";
			   btn.title=constDatasetPrevPage;
				btn.style.width=30;
				break;
			}
			case "moveprev":{
//				btn.value="上条";
				btn.value="3";
				btn.style.fontFamily="Webdings";
				btn.title=constDatasetMovePrev;
				btn.style.width=30;
				break;
			}
			case "movenext":{
				//btn.value="下条";
				btn.style.fontFamily="Webdings";
				btn.value="4";
				btn.title=constDatasetMoveNext;
				btn.style.width=30;
				break;
			}
			case "nextpage":{
				//btn.value="下页";	
				btn.style.fontFamily="Webdings";
				btn.value="8";
				btn.title=constDatasetNextPage;
				btn.style.width=30;
				break;
			}
			case "movelast":{
				btn.style.fontFamily="Webdings";
				btn.value=":";
				//btn.value="末页";	
				btn.title=constDatasetMoveLast;
				btn.style.width=30;
				break;
			}
			case "insertrecord":{
				btn.value=constBtnInsertRecord;
				btn.title=constDatasetInsertRecord;
				btn.style.width=45;
				break;
			}
			case "appendrecord":{
				btn.value=constBtnAppendRecord;
				btn.title=constDatasetAppendRecord;
				btn.style.width=45;
				break;
			}
			case "deleterecord":{
				btn.value=constBtnDeleteRecord;
				btn.title=constDatasetDeleteRecord;
				btn.style.width=45;
				break;
			}
			case "editrecord":{
				btn.value=constBtnEditRecord;
				btn.title=constDatasetEditRecord;
				btn.style.width=45;
				break;
			}
			case "cancelrecord":{
				btn.value=constBtnCancelRecord;;
				btn.title=constDatasetCancelRecord;
				btn.style.width=45;
				break;
			}
			case "postrecord":{
				btn.value=constBtnPostRecord;
				btn.title=constDatasetPostRecord;
				btn.style.width=45;
				break;
			}
		}
		btn.id=dataPilot.id+"_"+btn.buttonType;
		row.insertCell().appendChild(btn);
	}

	refreshDataPilot(dataPilot);
}

function setDataPilotButtons(dataPilot, buttons){
	dataPilot.buttons=buttons;
	initDataPilot(dataPilot);
}

function refreshDataPilot(dataPilot){

	function refreshButton(btn, enable){
			btn.disabled=!enable;
			refreshButtonColor(btn);
			/*
			if (isTrue(btn.getAttribute("down"))){
				btn.style.color = "white";
			}
			else{
				btn.style.color = "#1B4A98";
			}	
			*/		
	}

	var dataset=getElementDataset(dataPilot);

	var row=dataPilot.rows[0];
	//第一列为标题　第x页
	var istart=0;
	if(dataPilot.bflag)
		istart=dataPilot.ncols;
	for(var i=istart; i<row.cells.length; i++){
		var btn=row.cells[i].children[0];
		switch(btn.buttonType){
			case "movefirst":;
			case "moveprev":{
				refreshButton(btn, (dataset && !dataset.isFirst()));
				break;
			}
			case "prevpage":{
				refreshButton(btn, (dataset && dataset.record && dataset.record.pageIndex>1));
				break;
			}
			case "movenext":;
			case "movelast":{
				refreshButton(btn, (dataset && !dataset.isLast()));
				break;
			}
			case "nextpage":{
				refreshButton(btn, (dataset && dataset.record && dataset.record.pageIndex<dataset.pageCount));
				break;
			}
			case "insertrecord":;
			case "appendrecord":{
				refreshButton(btn, (dataset && !dataset.readOnly));
				break;
			}
			case "editrecord":{
				refreshButton(btn, (dataset && !(dataset.isFirst() && dataset.isLast()) && !dataset.readOnly));
				break;
			}
			case "deleterecord":{
				refreshButton(btn, (dataset && !(dataset.isFirst() && dataset.isLast()) && !dataset.readOnly));
				break;
			}
			case "cancelrecord":;
			case "postrecord":{
				refreshButton(btn, (dataset && (dataset.state=="insert" || dataset.state=="modify") && !dataset.readOnly));
				break;
			}
		}

		fireUserEvent(getElementEventName(dataPilot, "onRefreshButton"), [dataPilot, btn, btn.buttonType]);
	}
}

function _datapilot_onclick(evt){
	//xuj update 2013-7-23
	evt=evt?evt:(window.event?window.event:null);
	var button=evt.target?evt.target:evt.srcElement;
	if (button.disabled) return;
	var datapiolt=button.datapiolt;
	var dataset=getElementDataset(datapiolt);

	var eventName=getElementEventName(datapiolt, "onButtonClick");
	if (isUserEventDefined(eventName)){
			var event_result=fireUserEvent(eventName, [datapiolt, button, button.buttonType]);
			if (!event_result) return;
	}

	var pageSize=datapiolt.getAttribute("pageSize");
   
	switch(button.buttonType){
		case "movefirst":{
			dataset.moveFirst();
			datapiolt.setLabel(dataset,1);			
			break;
		}
		case "prevpage":{
			var pageIndex=(dataset.record)?getInt(dataset.record.pageIndex)-1:1;//changxy 20161109 dataset.record.pageIndex 类型为字符型 无法计算
			dataset.moveToPage(pageIndex);
			datapiolt.setLabel(dataset,pageIndex);
			break;
		}
		case "moveprev":{
			dataset.movePrev();
			break;
		}
		case "movenext":{
			dataset.moveNext();
			break;
		}
		case "nextpage":{
			var pageIndex=(dataset.record)?getInt(dataset.record.pageIndex)+1:1;//【18323】 changxy  20161109 dataset.record.pageIndex 类型为字符型 无法计算
			dataset.moveToPage(pageIndex);
			datapiolt.setLabel(dataset,pageIndex);
			break;
		}
		case "movelast":{
			dataset.moveLast();
			var pageindex=dataset.getPageIndex();
			datapiolt.setLabel(dataset,pageindex);
			break;
		}
		case "insertrecord":{
			dataset.insertRecord("before");
			break;
		}
		case "appendrecord":{
			dataset.insertRecord("end");
			break;
		}
		case "editrecord":{
			dataset_setState(dataset, "modify");
			break;
		}
		case "deleterecord":{
			if (isTrue(datapiolt.getAttribute("confirmDelete"))){
					if (confirm(constDatasetDeleteRecord)) dataset.deleteRecord();
			}
			else
					dataset.deleteRecord();
			break;
		}
		case "cancelrecord":{
			if (isTrue(datapiolt.getAttribute("confirmCancel"))){
					if (confirm(constDatasetCancelRecord)) dataset.cancelRecord();
			}
			else
					dataset.cancelRecord();
			break;
		}
		case "postrecord":{
			dataset.postRecord();
			break;
		}
	}
}

function _tabset_getTarget() {
	return this.targetFrame;
}

function _tabset_setTarget(targetFrame) {
	this.targetFrame = targetFrame;
}

function _tabset_getSelectedTab() {
	return this.selectTab
}

function _tabset_setSelectedTab(tab) {
	if (typeof(tab)=="number"){
		setActiveTabIndex(this, tab);
	}
	else if (typeof(tab)=="string"){
		setActiveTab(this, tab);
	}
	else {
		_setActiveTab(this, tab);
	}
}

function _tab_getName() {
	return this.name
}

function _tab_getLabel() {
	return this.label
}

function _tab_setLabel(label) {
	this.label = label;
}


function _tab_getPath() {
	return this.path
}

function _tab_setPath(path) {
	this.path = path;
}

function _tab_isVisible() {
	return this.visible
}

function _tab_setVisible(visible) {
	this.visible = visible;
}

function _tab_getTag() {
	return this.tag
}

function _tab_setTag(tag) {
	this.tag = tag;
}

function initTabSet(tabset){
	tabset._tabs=_tabset_tabs;
	tabset.getTarget=_tabset_getTarget;
	tabset.setTarget=_tabset_setTarget;
        tabset.getSelectedTab=_tabset_getSelectedTab;
        tabset.setSelectedTab=_tabset_setSelectedTab;
    try{
    	_theme_root=hcm_tabset_root
    }catch(e){}
    
    _theme_root = _theme_root.replace(/(^\s*)|(\s*$)/g, "");//去除两边的空格
    
    if(_theme_root.lastIndexOf("/")==_theme_root.length-1)//add by xiegh 20171218 bug33439
    
		tabset._imagePrefix = _theme_root + "tabset/" + tabset.tabPlacement + "_";
		
	else
	
		tabset._imagePrefix = _theme_root + "/tabset/" + tabset.tabPlacement + "_";
		
	//var parentDiv=tabset.parentElement;
	var parentDiv=tabset.parentNode;//xuj update ff、gg不支持parentElement
	parentDiv.style.width=parentDiv.offsetWidth;
	var tabs=new Array();
	eval("var isXmlExist=(typeof(__"+tabset.id+")==\"object\")");
	if (isXmlExist) {
		eval("var xmlIsland=__"+tabset.id);
		root=xmlIsland.documentElement;

		if (root) {
			var tabNodes = root.childNodes;
			for (var i=0; i<tabNodes.length; i++) {
				var tabNode = tabNodes.item(i);
				var tab=new Object();
				tab.name=tabNode.getAttribute("name");
				tab.label=tabNode.getAttribute("label");
				tab.path=tabNode.getAttribute("path");
				tab.visible=isTrue(tabNode.getAttribute("visible"));
				tab.tag=isTrue(tabNode.getAttribute("tag"));
				
				tab.getName=_tab_getName;
				tab.getLabel=_tab_getLabel;
				tab.setLabel=_tab_setLabel;
				tab.getPath=_tab_getPath;
				tab.setPath=_tab_setPath;
				tab.isVisible=_tab_isVisible;
				tab.setVisible=_tab_setVisible;
				tab.getTag=_tab_getTag;
				tab.setTag=_tab_setTag;

				tabs[tabs.length]=tab;
			}
		}		
		_xml_list[_xml_list.length]=xmlIsland;
	}
	for(var i=0; i<tabset.tBodies[0].rows.length; i++){
		var row=tabset.tBodies[0].rows[i];
		row.removeNode(true);
	}
	var row=tabset.tBodies[0].insertRow();
	var cell=row.insertCell();
	cell.firstCell=true;
	cell.innerHTML="<img src=\""+tabset._imagePrefix+"start_tab.gif\">";
	
	var label, tabname, index;
	for(i=0; i<tabs.length; i++){
		var tab=tabs[i];
		if (!tab.visible) continue;

		cell=row.insertCell();
		cell.background=tabset._imagePrefix+"tab_button.gif";
		cell._tabIndex=i;
		tabname=tab.name;
		cell.tabName=tabname;
		cell.label=tab.label;
		cell.path=tab.path;

		btn=document.createElement("<DIV hideFocus=true nowrap class=tab></DIV>");
		btn.innerText=tab.label;
		btn._tabIndex=-1;

		btn.onclick=_tabset_onclick;
		btn.onmouseover=_tabset_onmouseover;
		btn.onmouseout=_tabset_onmouseout;
		btn.tab=cell;
		cell.appendChild(btn);

		cell=row.insertCell();
		if (i!=tabs.length-1){
			cell.innerHTML="<img src=\""+tabset._imagePrefix+"tab.gif\">";
		}
		else{
			cell.lastCell=true;
			cell.innerHTML="<img src=\""+tabset._imagePrefix+"end_tab.gif\">";
		}

		eval("var tabsetBody=_body_"+tabset.id+";");
		eval("if (typeof(" + tabset.id+"_"+tab.name + ")!=\"undefined\") var tabDiv="+tabset.id+"_"+tab.name+";");

		if (typeof(tabDiv)!="undefined") {
			tabDiv.extra="tab";
			tabDiv.style.visibility="hidden";
			_setChildTableVisibility(tabDiv, "hidden");			
			tabDiv.style.overflow="auto";
			tabDiv.style.position="absolute";
			tabDiv.style.left=0;
			tabDiv.style.top=0;
			/*chenmengqing added if for showModalDialog*/
			if(tabsetBody.clientWidth>4)
			{
				tabDiv.style.width=tabsetBody.clientWidth-4;
				tabDiv.style.height=tabsetBody.clientHeight-4;
			}

			tabDiv.style.margin=2;
		}
		
	}

	cell=row.insertCell();
	cell.width="100%";
	cell.background=tabset._imagePrefix+"tab_blank.gif";

	setActiveTabIndex(tabset, getInt(tabset.getAttribute("tabIndex")));
	//_theme_root路径最后面中多了一个"/"符号，图片才不显示 33695 wangb 20180103
	if(_theme_root.lastIndexOf("/") == _theme_root.length-1)
		_theme_root = _theme_root.substring(0,_theme_root.length-1);
	if (tabset.offsetWidth > parentDiv.clientWidth) {
		var buttonPane=document.createElement("<div style=\"width:30; cursor:hand; z-index:1000\"></div>");
		buttonPane.innerHTML="<img width=\"15\" height=\"15\" src=\""+_theme_root+"/tabset/scroll_button2.gif\" "+
			"onmousedown=\"_tabpane_"+tabset.id+".scrollLeft=_tabpane_"+tabset.id+".scrollLeft-50\">"+
			"<img width=\"15\" height=\"15\" src=\""+_theme_root+"/tabset/scroll_button1.gif\" "+
			"onmousedown=\"_tabpane_"+tabset.id+".scrollLeft=_tabpane_"+tabset.id+".scrollLeft+50\">";
		buttonPane.style.position="absolute";
		eval("var pos=getAbsPosition(_tabpane_"+tabset.id+");");
		buttonPane.style.left=pos[0];
		buttonPane.style.top=pos[1] + 4;
		eval("_tabdiv_"+tabset.id+".appendChild(buttonPane);");
	}

	eval("var tabsetPane=_tabsetpane_"+tabset.id+";");
	tabsetPane.tabPane=parentDiv;
	tabsetPane.onresize=tabSet_onResize;
}

function tabSet_onResize(evt) {
	evt=evt?evt:(window.event?window.event:null);
	//var tabsetPane=event.srcElement;
	//xuj update 2013-7-23
	var tabsetPane=evt.target?evt.target:evt.srcElement;
	var tabPane=tabsetPane.tabPane;
	tabPane.style.width=tabsetPane.offsetWidth;
}

function _tabset_tabs() {
	return this._tabs;
}

function setTabs(tabset, tabs){
	tabset._tabs=tabs;
	initTabSet(tabset);
}

function _setChildTableVisibility(element, vis){


	for (var i=0; i<element.children.length; i++){
		var obj=element.children[i];
		if (compareText(obj.getAttribute("extra"), "datatable")){
			obj.style.visibility=vis;
			if (!compareText(vis, "hidden") && obj.needRefresh) {
				obj.refreshData();
			}			
		}
		_setChildTableVisibility(obj, vis);
	}
}

function _setActiveTab(cell){
	try{
		var row=getRowByCell(cell);
		var tabset=getTableByRow(row);
		var selectCell=tabset.selectTab;

		if (selectCell==cell) return;
		var oldName=(selectCell)?selectCell.tabName:"";
		var newName=cell.tabName;

		var eventName=getElementEventName(tabset, "beforeTabChange");
		var event_result=fireUserEvent(eventName, [tabset, oldName, newName]);
		if (event_result) throw event_result;

		eval("var tabsetBody=_body_"+tabset.id+";");
		if (selectCell){
			var prevCell=row.cells[selectCell.cellIndex-1];
			var nextCell=row.cells[selectCell.cellIndex+1];

			selectCell.background=tabset._imagePrefix+"tab_button.gif";

			if (prevCell.firstCell)
				prevCell.firstChild.src=tabset._imagePrefix+"start_tab.gif";
			else
				prevCell.firstChild.src=tabset._imagePrefix+"tab.gif";

			if (nextCell.lastCell)
				nextCell.firstChild.src=tabset._imagePrefix+"end_tab.gif";
			else
				nextCell.firstChild.src=tabset._imagePrefix+"tab.gif";

			var tab=null;
			eval("if (typeof(" + tabset.id+"_"+oldName + ")!=\"undefined\") tab="+tabset.id+"_"+oldName+";");
			tab=document.getElementById(tabset.id+"_"+oldName);
			if (tab) {
				_stored_element=tab;
				//_setChildTableVisibility(tab, "hidden");
				//document.body.appendChild(tab);
				var s="_stored_element.style.position=\"absolute\";"+
					"_stored_element.style.visibility=\"hidden\";";
				setTimeout(s, 0);
			}
		}

		var prevCell=row.cells[cell.cellIndex-1];
		var nextCell=row.cells[cell.cellIndex+1];

		cell.background=tabset._imagePrefix+"active_tab_button.gif";

		if (prevCell.firstCell)
			prevCell.firstChild.src=tabset._imagePrefix+"active_start_tab.gif";
		else
			prevCell.firstChild.src=tabset._imagePrefix+"active_tab1.gif";

		if (nextCell.lastCell)
			nextCell.firstChild.src=tabset._imagePrefix+"active_end_tab.gif";
		else
			nextCell.firstChild.src=tabset._imagePrefix+"active_tab2.gif";

		var tab=null;
		//eval("if (typeof(" + tabset.id+"_"+newName + ")!=\"undefined\") tab="+tabset.id+"_"+newName+";");
		tab=document.getElementById(tabset.id+"_"+newName);
		if (tab) {
			//tabsetBody.appendChild(tab); //checkbox radio 怎不灵，切换页签时,chenmengqing begin
			tab.style.position="absolute";
			var pos=getAbsPosition(tabsetBody);
			tab.style.posLeft=pos[0];
			tab.style.posTop=pos[1];	//end.
			tab.style.visibility="";
			//_setChildTableVisibility(tab, ""); //chenmengqing changed at 0720
		}

		tabset.selectTab=cell;
		tabset.selectName=cell.tabName;
		tabset.selectIndex=cell._tabIndex;

		if (cell.path){
			//20030920 TabSet
			//var url=cell.path+((cell.path.indexOf("?")>=0)?"&":"?");
			//url+=tabset.id+"_tabIndex="+cell._tabIndex;
			/*at 0715 tabset.target->detail*/
		//	open(cell.path, "detail");  20150504 dengcan iframe通过open打开，QQ管家会误认为是广告拦截 处理
			document.getElementById("detail").src=cell.path;
		}

		var eventName=getElementEventName(tabset, "afterTabChange");
		fireUserEvent(eventName, [tabset, oldName, newName]);
	}
	catch(e){
		processException(e);
	}
}

function setActiveTab(table, tabname){
	if (!tabname) return;
	for(var i=0; i<table.cells.length; i++){
		if (table.cells[i].tabName==tabname){
			_setActiveTab(table.cells[i]);
			break;
		}
	}
}

function setActiveTabIndex(table, index){
	for(var i=0; i<table.cells.length; i++){
		if (table.cells[i]._tabIndex==index){
			_setActiveTab(table.cells[i]);
			break;
		}
	}
}

function _tabset_onclick(evt){
	//xuj update 2013-7-23
	evt=evt?evt:(window.event?window.event:null);
	var tabsetPane=evt.target?evt.target:evt.srcElement;
	var tab=tabsetPane.tab;
	_setActiveTab(tab);
}

function _tabset_onmouseover(evt){
	//xuj update 2013-7-23
	evt=evt?evt:(window.event?window.event:null);
	var tabsetPane=evt.target?evt.target:evt.srcElement;
	tabsetPane.style.color="blue";
	tabsetPane.style.textDecorationUnderline=true;
}

function _tabset_onmouseout(evt){
	//xuj update 2013-7-23
	evt=evt?evt:(window.event?window.event:null);
	var tabsetPane=evt.target?evt.target:evt.srcElement;
	tabsetPane.style.color="black";
	tabsetPane.style.textDecorationUnderline=false;
}

function _button_getCommand() {
	return this.command;
}

function _button_setCommand(command) {
	this.command = command;
}

function _button_getMenu() {
	return this.menu;
}

function _button_setMenu(menu) {
	this.menu = menu;
}

function _button_isDown() {
	return this.down;
}

function _button_setDown(down) {
	setButtonDown(this, down);
}

function initButton(button) {
	button.getCommand=_button_getCommand;
	button.setCommand=_button_setCommand;
	button.getMenu=_button_getMenu;
	button.setMenu=_button_setMenu;	
	button.isDown=_button_isDown;
	button.setDown=_button_setDown;
	
	button.hideFocus=true;
	setButtonDown(button, button.getAttribute("down"))
	if (!button.onmousedown) {
		button.onmousedown=_button_onmousedown;
	}
	if (!button.onmouseup) {
		button.onmouseup=_button_onmouseup;
	}
	if (!button.onmouseover) {
	 button.onmouseover=_button_onmouseover;
	}
	if (!button.onmouseout) {
		button.onmouseout=_button_onmouseout;
	}
	if (!button.onclick) {
		button.onclick=_button_onclick;
	}
	button.title = getDecodeStr(button.toolTip);
}

function refreshButtonColor(button){
	if (isTrue(button.getAttribute("down"))){
		button.className="button_down";
		button.style.color = "white";
		//button.style.backgroundImage = "url("+_theme_root+"/button.gif)";
	}
	else{
		button.className="button";
		button.style.color = "black";
		//button.style.backgroundImage = "url("+_theme_root+"/button.gif)";
	}
}

function setButtonDown(button, down){
	button.down=isTrue(down);
	refreshButtonColor(button);
}

function _button_onmousedown(evt){
	//xuj update 2013-7-23
	evt=evt?evt:(window.event?window.event:null);
	var button=evt.target?evt.target:evt.srcElement;
	//var button=event.srcElement;
	fireUserEvent(getElementEventName(button, "onMouseDown"), [button]);
	var menu=button.getAttribute("menu");

	if (typeof(menu)=="string" && menu!=""){
		eval("menu="+menu);
		button.menu=menu;
	}

	if (menu){
		showButtonMenu(menu, button);
	}
}

function _button_onmouseup(evt){
	//var button=event.srcElement;
	evt=evt?evt:(window.event?window.event:null);
	var button=evt.target?evt.target:evt.srcElement;
	if (isTrue(button.getAttribute("allowPushDown"))){
		var down=button.getAttribute("down");
		setButtonDown(button, !down);
	}
	fireUserEvent(getElementEventName(button, "onMouseUp"), [button]);
}

function _button_onmouseover(evt){
	try{
		//var button=event.srcElement;
		//xuj update 2013-7-23
		evt=evt?evt:(window.event?window.event:null);
		var button=evt.target?evt.target:evt.srcElement;
		if (button.disabled || button.down) return;
		//button.style.backgroundImage="url()";
		fireUserEvent(getElementEventName(button, "onMouseEnter"), [button]);
	}
	catch(e){
		//do nothing
	}
}

function _button_onmouseout(evt){
	try{
		//var button=event.srcElement;
		//xuj update 2013-7-23
		evt=evt?evt:(window.event?window.event:null);
		var button=evt.target?evt.target:evt.srcElement;
		if (button.disabled) return;
		refreshButtonColor(button);
		fireUserEvent(getElementEventName(button, "onMouseLeave"), [button]);
	}
	catch(e){
		//do nothing
	}
}

function _button_onclick(evt)
{
	//var button=event.srcElement;
	//xuj update 2013-7-23
	evt=evt?evt:(window.event?window.event:null);
	var button=evt.target?evt.target:evt.srcElement;
	if (button.command)
	{

		eval("var command = " + button.command);
		var hint=command.getHint();
		if(hint&&hint!="")
		{
			if(confirm(hint))
			  command.execute();
		}
		else
			command.execute();
	}
	else
	{
		fireUserEvent(getElementEventName(button, "onClick"), [button]);
	}
}