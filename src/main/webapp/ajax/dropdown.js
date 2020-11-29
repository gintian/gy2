var _fileIncluded_dropdown=true;

var _dropdown_parentwindow=null;
var _dropdown_parentbox=null;
var _dropdown_box=null;
var _dropdown_table=null;
var _dropdown_frame=null;
var _dropdown_dataset=null;
var _date_dropdown_box=null;

var _array_dropdown = new Array();

var _calendarControl=null;
var _calendarControl_p=null;
var _tmp_dataset_date=null;

function createDropDown(id) {
	var dropdown=new Object();
	dropdown.id=id;
	dropdown.clearCache=dropdown_clearCache;
	return dropdown;
}

function initDropDown(dropdown){
	_array_dropdown[_array_dropdown.length]=dropdown;
	if (dropdown.type=="dynamic") 
	{
		dropdown._parameters=new ParameterSet();
		dropdown.parameters=dropdown_parameters;
	}
}

function dropdown_parameters(){
	return this._parameters;
}

function dropdown_clearCache(){
	var dropdown=this;
	dropdown.dropdownbox=null;
}

function initDropDownBox(dropDownType){

	try{
		_isDropDownPage=true;
		if (typeof(_dropdown_succeed)!="undefined" && !isTrue(_dropdown_succeed)){
			throw getDecodeStr(_dropdown_error);
		}
		else{
			if (dropDownType=="dynamic"){
				if (typeof(datasetDropDown)!="undefined") _dropdown_dataset=datasetDropDown;
			}
			/*chenmengqing changed at 20060122 for dropdown codeselecttree 首层节点出现二次 */
			//initDocument();
			_initDropDownBox(dropDownType);
		}
		return true;
	}
	catch(e)
	{
		processException(e);
		hideDropDown();
		hideStatusLabel(window.parent);
		return false;
	}
}

function _initDropDownBox(dropDownType){
	_document_loading=true;

	switch (dropDownType){
		case "dynamic":{
			_dropdown_div.onkeydown=_dropdown_onkeydown;
		}

		case "custom":{
			_dropdown_parentwindow=window.parent;
			_dropdown_parentbox=_dropdown_parentwindow._dropdown_box;
			if (_dropdown_parentbox==null) return;

			_dropdown_parentwindow._dropdown_window=window;

			if (!_dropdown_parentbox || _dropdown_parentbox.style.visibility=="hidden") return;

			var editor=_dropdown_parentbox.editor;
			_dropdown_div.style.width=
				(_dropdown_parentbox.offsetWidth>editor.offsetWidth)?_dropdown_parentbox.offsetWidth:editor.offsetWidth;

			_dropdown_parentwindow.sizeDropDownBox();

			with (_dropdown_parentwindow._dropdown_frame)
			{
				width="100%";
				if (filters.blendTrans.status!=2) 
				{
					if (getIEVersion()<"5.5")
					{
						style.visibility="visible";
					}
					else{
						filters.blendTrans.apply();
						style.visibility="visible";
						filters.blendTrans.play();
					}
				}
			}

			hideStatusLabel(_dropdown_parentwindow);
			break;
		}

		case "date":{
			_dropdown_parentwindow=window;
			_dropdown_parentbox=_dropdown_parentwindow._dropdown_box;
			_dropdown_parentwindow._dropdown_window=window;
			sizeDropDownBox();
			if ((getIEVersion()>="5.5") &&
				_dropdown_parentbox.filters.blendTrans.status!=2)
				_dropdown_parentbox.filters.blendTrans.play();
			break;
		}

		default:{
			_dropdown_parentwindow=window;
			_dropdown_parentbox=_dropdown_parentwindow._dropdown_box;
			_dropdown_parentwindow._dropdown_window=window;
			_dropdown_dataset=getElementDataset(_dropdown_table);
			sizeDropDownBox();
			if ((getIEVersion()>="5.5") &&
				_dropdown_parentbox.filters.blendTrans.status!=2)
				_dropdown_parentbox.filters.blendTrans.play();
			break;
		}
	}

	_dropdown_parentbox.prepared=true;
	var editor=_dropdown_parentbox.editor;
	if (editor) dropDownLocate();
	_document_loading=false;
}

function sizeDropDownBox(){
	function _sizeDropDownBox(new_width, new_height){
		with (_dropdown_box){
			var editor=_dropdown_box.editor;
			var dropdown=_dropdown_box.dropDown;
			var maxHeight=parseInt(dropdown.height);
			if (isNaN(maxHeight) || maxHeight<20) maxHeight=220;

			var pos=getAbsPosition(editor, document.body);
			var _posLeft=pos[0];
			var _posTop=pos[1]+editor.offsetHeight+1;

			if (new_height>maxHeight &&
				!(dropdown.type=="dynamic" && getInt(dropdown.pageSize)>0)){
				new_height=maxHeight;
				new_width+=16;
				if (!(getIEVersion()<"5.5"))
					style.overflowY="scroll";
				else
					style.overflowY="visible";
			}
			else{
				style.overflowY="hidden";
			}

			var document_width=document.body.clientWidth + document.body.scrollLeft;
			var document_height=document.body.clientHeight + document.body.scrollTop;
			
			if (_posLeft+new_width>document_width && document_width>new_width) _posLeft=document_width-new_width-5;//xieguiquan 2011-7-22修改-5，防止点击代码型不停的闪动
			if (_posTop+new_height>document_height && pos[1]>new_height) _posTop=pos[1]-new_height-5;
			style.posLeft=_posLeft;
			style.posTop=_posTop;
			style.posHeight=new_height+4;
			if (Math.abs(new_width+4-style.posWidth)>4) style.posWidth=new_width+4;
			style.borderWidth="2px";
		}
	}

	if (!isDropdownBoxVisible()) return;

	try{
		var _width, _height;
		switch (_dropdown_box.dropDown.type){
			case "dynamic":;
			case "custom":{
				with (_dropdown_frame){
					_height=_dropdown_window._dropdown_div.offsetHeight;
					_width=_dropdown_window._dropdown_div.offsetWidth;
					style.posWidth=_width;
					style.posHeight=_height;
				}
				break;
			}

			case "date":{
				_width=CalendarTable.offsetWidth;
				_height=CalendarTable.offsetHeight;
				break;
			}

			default:{
				_width=_dropdown_table.offsetWidth;
				_height=_dropdown_table.offsetHeight;
				break;
			}
		}
		_sizeDropDownBox(_width, _height);
	}
	catch(e){
		//do nothing
	}
}

function canDropDown(editor){
	var field=getElementField(editor);
	var hasDropDown = ((field && field.dropDown) || editor.getAttribute("dropDown"));
	return (hasDropDown && !compareText(editor.type, "checkbox"));
}

function getDropDownBox(dropdown){
	var box=null;
	if (dropdown.cachable){
		box=dropdown.dropdownbox;
	}

	if (!box){
		box=document.createElement("<DIV class=\"dropdown_frame\" style=\"overflow-X: hidden; position: absolute; visibility: hidden; z-index: 10000\"></DIV>");
		document.body.appendChild(box);
		box.dropDown = dropdown;
	}	
	_dropdown_box=box;
}

function getDropDownBtn(){
	if  (typeof(_dropdown_btn)=="undefined"){
		obj=document.createElement("<INPUT class=\"dropdown_button\" id=_dropdown_btn type=button tabindex=-1 value=6 hidefocus=true"+
			" style=\"position: absolute; visibility: hidden; z-index: 9999\""+
			" LANGUAGE=javascript onmousedown=\"return _dropdown_btn_onmousedown(this)\" onfocus=\"return _dropdown_btn_onfocus(this)\">");
		obj.style.background = "url("+_theme_root+"/dropdown_button.gif)";
		document.body.appendChild(obj);
		return obj
	}
	else{
		return _dropdown_btn;
	}
}
/**B0110,E0122,E01A1,link update*/
function getParentIdString(dataset,field)
{
	var qrystr,crecord,pfield,parent_id,pfield_1;
	var ufieldname;
	if (dataset)
	{
		if (!dataset.record) 
			 dataset.insertRecord();
	}	
	else
		return "";
	crecord=dataset.record;
	
	ufieldname=field.name;

	if(ufieldname=="e0122_2")
	{
		pfield=dataset.getField("b0110_2");
		pfield_1=dataset.getField("b0110_1");
		if(!pfield&&!pfield_1)
		{
			return "";	
		}
		if(pfield)
			parent_id= crecord.getValue("b0110_2"); 
		else if(pfield_1)
			parent_id= crecord.getValue("b0110_1");
	}
	else if(ufieldname=="e01a1_2")
	{
		pfield=dataset.getField("e0122_2");
		if(!pfield)
		  return "";
		parent_id= crecord.getValue("e0122_2");  
		if(trim(parent_id).length==0)
		{
			pfield=dataset.getField("b0110_2");
			pfield_1=dataset.getField("b0110_1");
			if(!pfield&&!pfield_1)
			  return "";	
			if(pfield)
				parent_id= crecord.getValue("b0110_2"); 
			else if(pfield_1)
				parent_id= crecord.getValue("b0110_1");
		}
	}
	else if(ufieldname=="e01a1")
	{
		pfield=dataset.getField("e0122");
		if(!pfield)
		  return "";
		parent_id= crecord.getValue("e0122");   
	}	
	else if(ufieldname=="e0122")
	{
		pfield=dataset.getField("b0110");
		if(!pfield)
		  return "";	
		parent_id= crecord.getValue("b0110"); 
	}	
	else
		return "";
	qrystr="&parent_id="+parent_id;

	return qrystr;
}

function showDropDownBox(_editor){

	try{
		if (!canDropDown(_editor)) return;
		if (!isDropdownBoxVisible()){
			var dropDownId=_editor.getAttribute("dropDown");

			if (!dropDownId) {
				var field=getElementField(_editor);
				if (field) dropDownId=field.dropDown;
			}

			eval("var dropdown=" + dropDownId);
			var eventName=getElementEventName(dropdown, "beforeOpen");
			var event_result=fireUserEvent(eventName, [dropdown]);
			if (event_result) throw event_result;

			getDropDownBox(dropdown);
			_dropdown_box.editor=_editor;
			_dropdown_box.prepared=false;
			if (_dropdown_box.filters.blendTrans.status==2) return;

			var dataset=getElementDataset(_editor);
			if (dataset){
				if (!dataset.record) dataset.insertRecord();
			}

			with (_dropdown_box){
				style.overflowY="hidden";
				switch (dropdown.type){
					case "dynamic":;
					case "custom":{
						style.visibility="visible";
						if (_editor.offsetWidth>128)
							style.width=editor.offsetWidth
						else
							style.width=128;
						break;
					}

					default:{
						if (filters.blendTrans.status!=2) {
							if (!(getIEVersion()<"5.5")) filters.blendTrans.apply();
							style.visibility="visible";
						}
						break;
					}
				}

				if (!_dropdown_box.cachable){
					switch (dropdown.type){
						case "dynamic":{
							showStatusLabel(window, constDownLoadingData, _editor);
							var _url=dropdown.path+"?__viewInstanceId="+__viewInstanceId+"&dropDown="+dropdown.id+
								"&visibleFields="+getValidStr(dropdown.visibleFields)+"&showColumnHeader="+((dropdown.showColumnHeader)?"true":"false")
								+"&filterParameter="+getValidStr(dropdown.filterParameter);
							
							var parameters=dropdown.parameters();
							for(var i=0; i<parameters.count(); i++){
								_url+="&paramName="+parameters.indexToName(i)+
									"&paramValue="+getEncodeStr(parameters.getValue(i))+
									"&paramDataType="+parameters.getDataType(i);
							}
							
							for(var i=0; i<ViewProperties.count(); i++){
								_url+="&procName="+ViewProperties.indexToName(i)+
									"&procValue="+getEncodeStr(ViewProperties.getValue(i))+
									"&procDataType="+ViewProperties.getDataType(i);
							}

							_dropdown_box.innerHTML="<IFRAME height=0 frameborder=0 marginheight=0 marginwidth=0 scrolling=no"+
								" src=\""+_application_root+_url+"\""+
								" style=\"position:_absolute; visibility:hidden; border-style: none\"></IFRAME>";
							_dropdown_frame=_dropdown_box.firstChild;
							break;
						}

						case "custom":{
							showStatusLabel(window, constDownLoadingData, _editor);
							/**chenmengqing added at 20060125 for selecting code value*/
							if(dropDownId=="dropdownCode")
							{ 
							    var paras="";
								if(typeof(field)=="undefined") //20130808 dengc  模板子集用到UM\UN\@K指标，下来需采用树状选择框
								{
									paras="?codesetid="+_editor.getAttribute("codesetid"); 
									var desc="单位";
									if(_editor.getAttribute("codesetid")=='UM')
										desc="部门";
									else if(_editor.getAttribute("codesetid")=='@K')
										desc="岗位";
									dropdown.writeFields=desc;
								} 
								else
								{
									paras="?codesetid="+field.codesetid+getParentIdString(dataset,field); 
									dropdown.writeFields=field.name;
								}
							    
							    //if(!_dropdown_frame)
							    //{
							    var dataset = getElementDataset(_editor);
							    if(dataset&&dataset.id&&dataset.id.toLowerCase().indexOf("templet_")!=-1){
							    paras+="&flag=templet";
							    }
							    	_dropdown_box.innerHTML="<IFRAME height=0 frameborder=0 marginheight=0 marginwidth=0 scrolling=no"+
									" src=\""+_application_root+dropdown.path+paras+"\""+
									" style=\"overflow: hidden; position:absolute; visibility:hidden; border-style: none\"></IFRAME>";
							   //}
							    //else
							   // {
							    //	var path=_application_root+dropdown.path+paras;
							    //	_dropdown_frame.setAttribute("src",path);
							    //}
							}
							else
							{

							    _dropdown_box.innerHTML="<IFRAME height=0 frameborder=0 marginheight=0 marginwidth=0 scrolling=no"+
								" src=\""+_application_root+dropdown.path+"\""+
								" style=\"overflow: hidden; position:absolute; visibility:hidden; border-style: none\"></IFRAME>";
							}
							_dropdown_frame=_dropdown_box.firstChild;

							break;
						}

						case "date":{
							createCalendar(_dropdown_box);
							_initDropDownBox(dropdown.type);
							_dropdown_box.onkeydown=_calendar_onkeydown;
							break;
						}

						default:{
							style.width=_editor.offsetWidth;
							createListTable(_dropdown_box,_editor);//cmq
							_dropdown_table.onkeydown=_dropdown_onkeydown;

							var _dataset;
							if (dropdown.type=="list"){
								_dataset=getDropDownItems(dropdown,_editor);
								//alert(dropdown.visibleFields);
								if (!dropdown.visibleFields){
									if (isTrue(dropdown.mapValue))
										dropdown.visibleFields="label";
									else
										dropdown.visibleFields="value";
								}
							}
							else{
								_dataset=dropdown.dataset;
								if (typeof(_dataset)=="string") _dataset=getDatasetByID(_dataset);
							}

							if (_dataset){
								setElementDataset(_dropdown_table, _dataset);
								_dropdown_table.fields=dropdown.visibleFields;
								_dropdown_table.highlightSelection=true;
								_dropdown_table.showHeader=dropdown.showColumnHeader;
								initElements(_dropdown_table);
								refreshTableData(_dropdown_table);
								//alert("s");
							}
							
							_initDropDownBox(dropdown.type);

							break;
						}
					}
				}
				else{
					switch (dropdown.type){
						case "dynamic":;
						case "custom":{
							_dropdown_frame=_dropdown_box.firstChild;
							dropdown.dropdown_window._initDropDownBox(dropdown.type);
							break;
						}

						default:{
							for (var i=0; i<_dropdown_box.children.length; i++){
								var obj=_dropdown_box.children[i];
								obj.style.visibility="visible";
								if (compareText(obj.getAttribute("extra"), "datatable")){									
									if (obj.needRefresh) {
										obj.refreshData();
									}			
								}
							}
							_dropdown_table=dropdown.dropdown_table;
							_initDropDownBox(dropdown.type);
							break;
						}
					}
				}
			}
			_editor.dropDownVisible=true;
			if  (typeof(_dropdown_btn)!="undefined") _dropdown_btn.value="5";
		}
	}
	catch(e){
		processException(e);
	}
}

function hideDropDownBox(){
	if (!_dropdown_box) return;
	if (isDropdownBoxVisible()){
		_skip_activeChanged=true;
		var editor=_dropdown_box.editor;
		var dropdown=_dropdown_box.dropDown;
		if (_dropdown_box.prepared && dropdown.cachable){
			dropdown.dropdown_box=_dropdown_box;
			_dropdown_box.cachable=true;
			switch (dropdown.type){
				case "list":;
				case "dataset":{
					dropdown.dropdown_table=_dropdown_table;
					break;
				}
				case "dynamic":;
				case "custom":{
					dropdown.dropdown_window=_dropdown_window;
					_dropdown_frame.removeNode(true);
					break;
				}
			}

			for (var i=0; i<_dropdown_box.children.length; i++){
				_dropdown_box.children[i].style.visibility="hidden";
			}
			_dropdown_box.style.visibility="hidden";
			_dropdown_window=null;
		}
		else{
			_dropdown_box.editor=null;
			switch (_dropdown_box.dropDown.type){
				case "list":
				case "dataset":{
					setElementDataset(_dropdown_table, null);
					break;
				}
				case "dynamic":;
				case "custom":{
					if (typeof(_dropdown_frame)!="undefined"){
						_dropdown_frame.style.visibility="hidden";
						_dropdown_frame.removeNode(true);
						//document.body.removeNode(_dropdown_frame.parent);
						/*
						_dropdown_frame.document.write("");
						_dropdown_frame.document.clear();
						document.body.removeNode(_dropdown_frame);
						CollectGarbage();*/
					}
					break;
				}
			}
			_dropdown_window=null;

			for (var i=0; i<_dropdown_box.children.length; i++){
				_dropdown_box.children[i].style.visibility="hidden"
			}
			_dropdown_box.style.visibility="hidden";
			_dropdown_box.removeNode(true);
			_dropdown_box=null;
		}

		editor.dropDownVisible=false;
		if  (typeof(_dropdown_btn)!="undefined") _dropdown_btn.value="6";
	}
}

function isDropDownBtnVisible(){
	if  (typeof(_dropdown_btn)!="undefined")
		return (_dropdown_btn.style.visibility=="visible")
	else
		return false;
}

function sizeDropDownBtn(_editor){
	if (!isDropDownBtnVisible()) return;
	with (_dropdown_btn){
		var pos=getAbsPosition(_editor);

		style.height=_editor.offsetHeight-2;
		style.width=16;
		style.posLeft=pos[0]+_editor.offsetWidth-offsetWidth-1;
		style.posTop=pos[1]+1;
	}
}

function showDropDownBtn(_editor){
	if (!canDropDown(_editor)) return;
	getDropDownBtn();
	if (typeof(_dropdown_btn)=="undefined") return;

	with (_dropdown_btn){
		if (!isDropDownBtnVisible()){
			setAttribute("editor", _editor);
			style.visibility="visible";
			sizeDropDownBtn(_editor);

			var oldWidth=_editor.offsetWidth;
			_editor.style.borderRightWidth=18;
			_editor.style.width=oldWidth;
		}
	}
}

function hideDropDownBtn(){
	if  (typeof(_dropdown_btn)=="undefined") return;

	if (isDropDownBtnVisible()){
		var editor=_dropdown_btn.editor;
		if (editor){
			var oldWidth=editor.offsetWidth;
			editor.style.borderRightWidth=1;
			editor.style.width=oldWidth;
		}
		_dropdown_btn.style.visibility="hidden";
		_dropdown_btn.editor=null;
	}
}

function _dropdown_btn_onmousedown(button){
	var obj=button.editor;
	obj.mhpp='0';  //20141018 dengcan  下拉框不支持磨合匹配
	if (!isDropdownBoxVisible()){
		if (obj) showDropDownBox(obj);
	}
	else
		hideDropDownBox();
}

function _dropdown_btn_onfocus(button){
	var obj=button.editor;
	if (obj) obj.focus();
}

function createListTable(parent_element,_editor){
	/*_dropdown_table=document.createElement("<table extra=datatable isDropDownTable=true readOnly=true width=100% "+
		" cellspacing=0 cellpadding=2 rules=all></table>");
	if (parent_element)
		parent_element.appendChild(_dropdown_table);
	else
		document.body.appendChild(_dropdown_table);
	*/ //cmq 20080203
	_dropdown_table=document.createElement("<table extra=datatable isDropDownTable=true readOnly=true width=100% "+
		" cellspacing=0 cellpadding=2 rules=all></table>");

	if (parent_element)
	{
		parent_element.appendChild(_dropdown_table);
	}
	else
		document.body.appendChild(_dropdown_table);	
}

function dropDownLocate(){
	var editor=_dropdown_parentbox.editor;
	var dropdown=_dropdown_parentbox.dropDown;
	switch (dropdown.type){
		case "date":{
			var _date=new Date(editor.value);
			if (!isNaN(_date)) setCalendarDate(_date);
			break;
		}
		default:{
			if (_dropdown_dataset){
				var fieldName;

				if (dropdown.type=="list"){
					fieldName=(isTrue(dropdown.mapValue))?"label":"value";
				}
				else{
					fieldName=dropdown.field;
					if (!fieldName) fieldName=editor.getAttribute("field");
				}

				var value=editor.value;
				var record=_dropdown_dataset.locate(fieldName, value);
				if (record) _dropdown_dataset.setRecord(record);
			}
			break;
		}
	}
}

function hideDropDown() {
	var editor=_dropdown_parentbox.editor;
	_dropdown_parentwindow.hideDropDownBox();
	if(editor){
	editor.focus();
	}
}

function _standard_dropdown_keyDown(keycode){
	switch(keycode){
		//PageUp
		case 33:{
			if (_dropdown_dataset){
				var pageIndex=(_dropdown_dataset.record)?_dropdown_dataset.record.pageIndex-1:1;
				_dropdown_dataset.moveToPage(pageIndex);
			}
			break;
		}
		//PageDown
		case 34:{
			if (_dropdown_dataset){
				var pageIndex=(_dropdown_dataset.record)?_dropdown_dataset.record.pageIndex+1:1;
				_dropdown_dataset.moveToPage(pageIndex);
			}
			break;
		}
		//Up
		case 38:{
			if (_dropdown_dataset){
				_dropdown_dataset.movePrev();
			}
			break;
		}
		//Down
		case 40:{
			if (_dropdown_dataset){
				_dropdown_dataset.moveNext();
			}
			break;
		}
	}
}

function processDropDownKeyDown(keycode) {
	switch(keycode){
		//Enter
		case 13:{
			dropDownSelected();
			break;
		}
		//ESC
		case 27:{
			hideDropDown();
			break;
		}
		//F2
		case 113:{
			hideDropDown();
			break;
		}
		//F7
		case 118:{
			hideDropDown();
			break;
		}
		default:{
			switch (_dropdown_parentbox.dropDown.type){
				case "list":
				case "dataset":
				case "dynamic":{
					_standard_dropdown_keyDown(keycode);
					break;
				}
				case "date":{
					_calendar_onkeydown();
					break;
				}
				default:{
					if (typeof(dropDown_onKeyDown)!="undefined") dropDown_onKeyDown(keycode);
					break;
				}
			}
		}
	}
}

function dropDownSelected(){
	var record;
	switch (_dropdown_parentbox.dropDown.type){
		case "list":
		case "dataset":
		case "dynamic":{
			if (_dropdown_dataset) record=_dropdown_dataset.record;
			break;
		}
		case "date":{
			_tmp_dataset_date=createDataset("_tmp_dataset_date");
			_tmp_dataset_date.addField("value");
			initDataset(_tmp_dataset_date);
			_tmp_dataset_date.insertRecord();
			_tmp_dataset_date.setValue("value", new Date(_calendarControl.year, _calendarControl.month, _calendarControl.day));
			_tmp_dataset_date.postRecord();
			record=_tmp_dataset_date.record;
			break;
		}
		default:{
			record=fireUserEvent("page_onGetDropDownRecord", []);
			break;
		}
	}

	if (record){
		_dropdown_parentwindow.processDropDownSelected(_dropdown_parentbox.editor, record, false);
		hideDropDown();
	}
	if (_tmp_dataset_date) freeDataset(_tmp_dataset_date);
}

function _dropdown_onkeydown(){
	processDropDownKeyDown(event.keyCode);
}

function _dropdown_onclick(){
	dropDownSelected();
}

function getDropDownItems(dropdown,_editor){
	/*
	var items=dropdown._items;
	if (!items){
		eval("var isXmlExist=(typeof(__"+dropdown.id+")==\"object\")");
		if (isXmlExist) {
			eval("var xmlIsland=__"+dropdown.id);
			items=createDataset();
			items.addField("value");
			items.addField("label");
			
			root=xmlIsland.documentElement;

			if (root) {
				var itemNodes = root.childNodes;
				for (var i=0; i<itemNodes.length; i++) {
					var itemNode = itemNodes.item(i);
					items.insertRecord();
					items.setValue("value", itemNode.getAttribute("value"));
					items.setValue("label", itemNode.getAttribute("label"));
				}
			}		
			initDataset(items);
			dropdown._items=items;

			_xml_list[_xml_list.length]=xmlIsland;
		}		
	}
	*/
	var items=dropdown._items;
	items=null;
	items=createDataset();
	items.addField("value");
	items.addField("label");
	var codesetid=null;
	var fieldname=_editor.getField();
	if(fieldname)
	{
		var dataset=_editor.getDataset();
		var field=dataset.getField(fieldname);
		codesetid=field.getCodeSetId();
		//dropdown.writeFields=fieldname;
	}
	else
		codesetid=_editor.getAttribute("codesetid");
	//var codevalue=_editor.getAttribute("codevalue");
	var rows=0;
	var pad="                                     ";
	var nlen=0;
	var now = new Date();
	var year = now.getFullYear().toString(); 
	var month = now.getMonth()+1; 
	var date = now.getDate(); 
	var nowTime=year*10000+month*100+date;
	if(codesetid=='orgType')  //邓灿  组织异动模块组织类型用到
	{
		items.insertRecord();
		items.setValue("label","UN");
		items.setValue("value", "单位");
		items.insertRecord();
		items.setValue("label","UM");
		items.setValue("value", "部门");
	}
	else
	{
		for(var i=0;i<g_dm.length;i++)
		{
			var codeitem=g_dm[i].ID;
			nlen=g_dm[i].L*2;
			//if(codeitem.indexOf(codesetid)!=-1)
			if(codeitem.substring(0,2)==codesetid)
			{
				if((g_dm[i].V).indexOf(_editor.value)<0){
				var hidenxgq=_editor.getAttribute("hidenxgq");
				if(hidenxgq=="1")
				continue;
				}
				items.insertRecord();
				if(codesetid=="UN"||codesetid=="UM"){//单位和部门按照有效时间范围判断，zhaoxg add 2014-1-24
					if(parseInt(nowTime)>=parseInt(g_dm[i].S) && parseInt(nowTime)<=parseInt(g_dm[i].E)){
						if(g_dm[i].L!=1)
							items.setValue("value",pad.substring(0,nlen)+g_dm[i].V);
						else
							items.setValue("value",g_dm[i].V);
							items.setValue("label", codeitem.substring(2));
							++rows;
					}
				}else{
					 
					//20141018 dengcan  代码型指标支持模糊查找功能 
				 	if(_editor.mhpp&&_editor.mhpp=='1'&&g_dm[i].V.indexOf(_editor.value)==-1&&codeitem.substring(2).indexOf(_editor.value)==-1)
				 			continue;
					
					if(g_dm[i].VF=="1"){//1为按时间范围走  0为按有效或无效的方式
						if(parseInt(nowTime)>=parseInt(g_dm[i].S) && parseInt(nowTime)<=parseInt(g_dm[i].E)){
							if(g_dm[i].L!=1)
								items.setValue("value",pad.substring(0,nlen)+g_dm[i].V);
							else
								items.setValue("value",g_dm[i].V);
								items.setValue("label", codeitem.substring(2));
								++rows;
						}
					}else{
						if(g_dm[i].I=="1"){//有效的
							if(parseInt(nowTime)>=parseInt(g_dm[i].S) && parseInt(nowTime)<=parseInt(g_dm[i].E)){
								if(g_dm[i].L!=1)
									items.setValue("value",pad.substring(0,nlen)+g_dm[i].V);
								else
									items.setValue("value",g_dm[i].V);
									items.setValue("label", codeitem.substring(2));
									++rows;
							}
						}
					}
				}

			}
		}
	}
	items.rowCount=rows;
	initDataset(items);
	dropdown._items=items;
	return items;
}


function initDropDownItems(dropdown){
	getDropDownItems(dropdown);
}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

var _calendar_days;

function _calendar_year_onpropertychange(){
	try{
		if (!_calender_year.processing && event.propertyName=="value"){
			if (_calender_year.value.length==4){
				_calender_year.processing=true;
				changeCalendarDate(getInt(_calender_year.value), _calendarControl.month);
				_calender_year.processing=false;
			}
		}
	}catch(err){
		//alert(err);
	}
}

function _calendar_month_onpropertychange(){
	if (!_calender_month.processing && _activeElement==_calender_month && event.propertyName=="value"){
		if (_calender_month.value.length>0){
			_calender_month.processing=true;
			changeCalendarDate(_calendarControl.year, getInt(_calender_month.value-1));
			_calender_month.processing=false;
		}
	}
}

function createCalendar(parent_element){

	function calendar(){
	 	var today=new Date()
	 	this.todayDay=today.getDate();
		this.todayMonth=today.getMonth();
		this.todayYear=today.getFullYear();
	 	this.activeCellIndex=0;
	}

	if (typeof(CalendarTable)=="object") {
		CalendarTable.removeNode(true);
	}

	_calendar_days=new Array(constSunday, constMonday, constTuesday, constWednesday, constThursday, constFriday, constSaturday);
	_calendarControl=new calendar();

	var tmpHTML="<iframe style=\"position:absolute;height:100%;width:100%; z-index: -1\" ></iframe>";
	tmpHTML+="<TABLE id=\"CalendarTable\" class=\"calendar\" width=200px cellspacing=0 cellpadding=1 rule=all>";
	tmpHTML+="<TR class=\"title\" valign=top><TD>";
	tmpHTML+="<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=0>";
	tmpHTML+="<TR><TD align=right>";
	tmpHTML+="<INPUT type=button extra=button value=3 title=\""+constLastYear+"\" style=\"FONT-SIZE:8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.year-1,_calendarControl.month)\">";
	tmpHTML+="</TD><TD width=1>";
	tmpHTML+="<INPUT id=\"_calender_year\" type=text class=editor size=4 maxlength=4 onpropertychange=\"return _calendar_year_onpropertychange()\">";
	tmpHTML+="</TD><TD align=left width=20px>";
	tmpHTML+="<INPUT type=button extra=button value=4 title=\""+constNextYear+"\" style=\"FONT-SIZE:8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.year+1,_calendarControl.month)\">";
	tmpHTML+="</TD>";
	tmpHTML+="<TD align=right width=20px>";
	tmpHTML+="<INPUT type=button extra=button value=3 title=\""+constLastMonth+"\" style=\"FONT-SIZE:8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.preYear,_calendarControl.preMonth)\">";
	tmpHTML+="</TD><TD width=1>";
	tmpHTML+="<INPUT id=\"_calender_month\" type=text class=editor size=2 maxlength=2 onpropertychange=\"return _calendar_month_onpropertychange()\">";
	tmpHTML+="</TD><TD align=left>";
	tmpHTML+="<INPUT type=button extra=button value=4 title=\""+constNextMonth+"\" style=\"FONT-SIZE: 8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.nextYear,_calendarControl.nextMonth)\">";
	tmpHTML+="</TD></TR>";
	tmpHTML+="</TABLE></TD></TR>";

	tmpHTML+="<TR><TD>";
	tmpHTML+="<TABLE border=1 bordercolor=silver id=\"calendarData\" HEIGHT=100% WIDTH=100% CELLSPACING=0 CELLPADDING=0 style=\"BORDER-COLLAPSE: collapse\"";
	tmpHTML+="onclick=\"_calendar_cell_onclick(event.srcElement)\">";
	tmpHTML+="<TR height=20px style=\"background-image: url("+_theme_root+"/table_header.gif)\">";
	for (var i=0;i<=6;i++){
		tmpHTML+="<TD align=center>"+_calendar_days[i]+"</TD>";
	}
	tmpHTML+="</TR>";
	for(var i=0;i<=5;i++){
		tmpHTML+="<TR>";
		for(var j=0;j<=6;j++){
			tmpHTML+="<TD align=center></TD>";
		}
		tmpHTML+="</TR>";
	}
	tmpHTML+="</TABLE></TD></TR>";

	tmpHTML+="<TR class=\"footer\"><TD align=right>";
	tmpHTML+="<INPUT extra=button type=button id=\"button_today\" value=\""+constToday+" "+_calendarControl.todayYear+"-"+(_calendarControl.todayMonth+1)+"-"+_calendarControl.todayDay+"\" onclick=\"_calendar_today_onclick()\"";
	tmpHTML+="</TD></TR></TABLE>";
	if (parent_element)
		parent_element.innerHTML=tmpHTML;
	else
		document.body.innerHTML=tmpHTML;

	initElements(CalendarTable);
	changeCalendarDate(_calendarControl.todayYear,_calendarControl.todayMonth,_calendarControl.todayDay)
}


	
function createCalendarPanel(parent_element){
	function calendar(){
	 	var today=new Date();
	 	this.todayDay=today.getDate();
		this.todayMonth=today.getMonth();
		this.todayYear=today.getFullYear();
	 	this.activeCellIndex=0;
	}
	if (typeof(CalendarTable)=="object") {
		CalendarTable.removeNode(true);
	}

	_calendar_days=new Array(constSunday, constMonday, constTuesday, constWednesday, constThursday, constFriday, constSaturday);
	_calendarControl=new calendar();
	_calendarControl_p=new calendar();
	var tmpHTML="";
	tmpHTML+="<TABLE id=\"CalendarTable\" class=\"calendar\" width=200px cellspacing=0 cellpadding=1 rule=all>";
	tmpHTML+="<TR class=\"title\" valign=top><TD>";
	tmpHTML+="<TABLE WIDTH=100% CELLSPACING=1 CELLPADDING=0>";
	tmpHTML+="<TR><TD align=right>";
	tmpHTML+="<INPUT type=button extra=button value=3 title=\""+constLastYear+"\" style=\"FONT-SIZE:8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.year-1,_calendarControl.month)\">";
	tmpHTML+="</TD><TD width=1>";
	tmpHTML+="<INPUT id=\"_calender_year\" type=text class=editor size=4 maxlength=4 onpropertychange=\"return _calendar_year_onpropertychange()\">";
	tmpHTML+="</TD><TD align=left width=20px>";
	tmpHTML+="<INPUT type=button extra=button value=4 title=\""+constNextYear+"\" style=\"FONT-SIZE:8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.year+1,_calendarControl.month)\">";
	tmpHTML+="</TD>";
	tmpHTML+="<TD align=right width=20px>";
	tmpHTML+="<INPUT type=button extra=button value=3 title=\""+constLastMonth+"\" style=\"FONT-SIZE:8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.preYear,_calendarControl.preMonth)\">";
	tmpHTML+="</TD><TD width=1>";
	tmpHTML+="<INPUT id=\"_calender_month\" type=text class=editor size=2 maxlength=2 onpropertychange=\"return _calendar_month_onpropertychange()\">";
	tmpHTML+="</TD><TD align=left>";
	tmpHTML+="<INPUT type=button extra=button value=4 title=\""+constNextMonth+"\" style=\"FONT-SIZE: 8;FONT-FAMILY:webdings;WIDTH:18px;HEIGHT:20px\" onclick=\"changeCalendarDate(_calendarControl.nextYear,_calendarControl.nextMonth)\">";
	tmpHTML+="</TD></TR>";
	tmpHTML+="</TABLE></TD></TR>";

	tmpHTML+="<TR><TD>";
	tmpHTML+="<TABLE border=1 bordercolor=silver id=\"calendarData\" HEIGHT=100% WIDTH=100% CELLSPACING=0 CELLPADDING=0 style=\"BORDER-COLLAPSE: collapse\"";
	//xuj update ie fire兼容
	tmpHTML+="onclick=\"_calendarPanel_cell_onclick(event.target?event.target:event.srcElement)\">";
	tmpHTML+="<TR height=20px style=\"background-image: url("+_theme_root+"/table_header.gif)\">";
	for (var i=0;i<=6;i++){
		tmpHTML+="<TD align=center>"+_calendar_days[i]+"</TD>";
	}
	tmpHTML+="</TR>";
	for(var i=0;i<=5;i++){
		tmpHTML+="<TR>";
		for(var j=0;j<=6;j++){
			tmpHTML+="<TD align=center></TD>";
		}
		tmpHTML+="</TR>";
	}
	tmpHTML+="</TABLE></TD></TR>";

	tmpHTML+="<TR class=\"footer\"><TD align=right>";
	tmpHTML+="<INPUT extra=button type=button id=\"button_today\" value=\""+constToday+" "+_calendarControl.todayYear+"-"+(_calendarControl.todayMonth+1)+"-"+_calendarControl.todayDay+"\" onclick=\"_calendarPanel_today_onclick()\"";
	tmpHTML+="</TD></TR></TABLE>";
	if (parent_element)
		parent_element.innerHTML=tmpHTML;
	else
		document.body.innerHTML=tmpHTML;
    _calender_month=$('_calender_month');
    _calender_year=$('_calender_year');
	changeCalendarDate(_calendarControl.todayYear,_calendarControl.todayMonth,_calendarControl.todayDay)
}

function _calendarPanel_cell_onclick(cell){
	setCalendarActiveCell(cell);
}
function _calendarPanel_today_onclick(){
	changeCalendarDate(_calendarControl.todayYear,_calendarControl.todayMonth,_calendarControl.todayDay)
	var index=_calendarControl.todayDay+_calendarControl.startday-1;
	//xuj update ie fire兼容
	//setCalendarActiveCell(calendarData.cells[index+7]);
	setCalendarActiveCell(getCalendarDataCell((index+7)));
}

function setCalendarDate(date){
	changeCalendarDate(date.getFullYear(),date.getMonth(),date.getDate());
}

function changeCalendarDate(year, month, day){
	if (_calendarControl.year==year && _calendarControl.month==month && (!day || _calendarControl.day==day)) return;

	if (_calendarControl.year!=year || _calendarControl.month!=month){
		_calendarControl.year=year;
		_calendarControl.month=month;

		if (month==0){
			 _calendarControl.preMonth=11
			 _calendarControl.preYear=_calendarControl.year-1
		}else{
			 _calendarControl.preMonth=_calendarControl.month-1
			 _calendarControl.preYear=_calendarControl.year
		}
		if (month==11){
			_calendarControl.nextMonth=0
			_calendarControl.nextYear=_calendarControl.year+1
		}else{
			_calendarControl.nextMonth=_calendarControl.month+1
			_calendarControl.nextYear=_calendarControl.year

		}
		_calendarControl.startday=(new Date(year,month,1)).getDay()
		if (_calendarControl.startday==0) _calendarControl.startday=7
		var curNumdays=getNumberOfDays(_calendarControl.month,_calendarControl.year)
		var preNumdays=getNumberOfDays(_calendarControl.preMonth,_calendarControl.preYear)
		var nextNumdays=getNumberOfDays(_calendarControl.nextMonth,_calendarControl.nextYear)
		var startDate=preNumdays-_calendarControl.startday+1
		var endDate=42-curNumdays-_calendarControl.startday

		_calender_month.value=(_calendarControl.month+1);
		//_calender_year.innerText=_calendarControl.year
		_calender_year.value=_calendarControl.year

		var datenum=0;
		for (var i=startDate;i<=preNumdays;i++){
			//xuj update ie fire兼容
			//var cell = calendarData.cells[datenum+7];
			var cell = getCalendarDataCell(datenum+7);
			cell.monthAttribute="pre";
			cell.className="cell_trailing";
			//cell.innerText=i;  //xuj update ie fire兼容
			cell.innerHTML=i;
			datenum++;
		}
		for (var i=1;i<=curNumdays;i++){
			//xuj update ie fire兼容
			//var cell = calendarData.cells[datenum+7];
			var cell = getCalendarDataCell(datenum+7);
			cell.monthAttribute="cur";
			if (datenum != _calendarControl.activeCellIndex){
				cell.className="cell_day";
			}
			//cell.innerText=i; //xuj update ie fire兼容
			cell.innerHTML=i;
			datenum++;
		}
		for (var i=1;i<=endDate;i++){
			//xuj update ie fire兼容
			//var cell = calendarData.cells[datenum+7];
			var cell = getCalendarDataCell(datenum+7);
			cell.monthAttribute="next";
			cell.className="cell_trailing";
			//cell.innerText=i; //xuj update ie fire兼容
			cell.innerHTML=i;
			datenum++;
		}
	}

	if (day) _calendarControl.day=day;
	//xuj update ie fire兼容
	//setCalendarActiveCell(document.getElementById("calendarData").cells[_calendarControl.day+_calendarControl.startday-1+7]);
	//xuj add 2015-2-5  解决如现在系统时间是2015.01.30号，当在时间控件上月份向右按钮点一下，结果跨过2月直接蹦到3月份的2号上了，不对
	var actionCellnum = (_calendarControl.day);
	if(actionCellnum>curNumdays)
		actionCellnum = curNumdays; 
	setCalendarActiveCell(getCalendarDataCell(actionCellnum+_calendarControl.startday-1+7));
	/**cmq begin*/
	if(_calendarControl_p)
	{
		_calendarControl_p.todayYear=_calendarControl.year;
		_calendarControl_p.todayMonth=_calender_month.value;
		_calendarControl_p.todayDay=_calendarControl.day;
	}
	/**cmq end.*/
}

//xuj add 2013-7-23解决ie firefox获取table表格兼容问题
function getCalendarDataCell(index){
	
	var rowindex=Math.floor(index/7);
	var cellindex=index%7;
	//alert(rowindex+" "+cellindex);
	//alert(document.getElementById("calendarData").rows.length);
	//alert(document.getElementById("calendarData").rows.item(0).cells.length);
	//alert(document.getElementById("calendarData").rows[rowindex]);
	return document.getElementById("calendarData").rows[rowindex].cells[cellindex];
}

function setCalendarActiveCell(cell){

	function setActiveCell(cellIndex){
		//xuj update ie fire兼容
		//var cell = calendarData.cells[_calendarControl.activeCellIndex+7];
		var cell = getCalendarDataCell((_calendarControl.activeCellIndex+7));
		if (cell.monthAttribute=="cur"){
			cell.className="cell_day";
		}
		else{
			cell.className="cell_trailing";
		}
		//xuj update ie fire兼容
		//var cell = calendarData.cells[cellIndex+7];
		var cell = getCalendarDataCell((cellIndex+7));
		cell.className="cell_selected";
		_calendarControl.activeCellIndex=cellIndex;
	}

	if (cell.tagName.toLowerCase()!="td") return;
	var _activeCellIndex=cell.parentElement.rowIndex*7+cell.cellIndex-7;

	with(_calendarControl){
		if (activeCellIndex==_activeCellIndex) return;

		var monthAttribute=cell.monthAttribute;
		switch (monthAttribute){
			case "pre":{
				changeCalendarDate(preYear,preMonth,getNumberOfDays(preMonth,preYear)-startday+_activeCellIndex+1);
				setActiveCell(startday+day-1);
				break
			}
			case "cur":{
				changeCalendarDate(year,month,_activeCellIndex-startday+1);
				setActiveCell(_activeCellIndex);
				break
			}
			case "next":{
				changeCalendarDate(nextYear,nextMonth,_activeCellIndex-getNumberOfDays(month,year)-startday+1);
				setActiveCell(startday+day-1);
				break
			}
		}
	}
}

function _calendar_cell_onclick(cell){
	setCalendarActiveCell(cell);
	dropDownSelected();
}

function _calendar_onkeydown(){

	switch(event.keyCode){		
		case 33:{//PgUp
			if (event.ctrlKey){
				changeCalendarDate(_calendarControl.year-1,_calendarControl.month)
			}else{
				changeCalendarDate(_calendarControl.preYear,_calendarControl.preMonth)
			}
			break
		}
		case 34:{//PgDn
			if (event.ctrlKey){
				 changeCalendarDate(_calendarControl.year+1,_calendarControl.month)
			}else{
				 changeCalendarDate(_calendarControl.nextYear,_calendarControl.nextMonth)
			}
			break
		}
		case 35:{//End
		    	var index=getNumberOfDays(_calendarControl.month,_calendarControl.year) +_calendarControl.startday-1
			//xuj update ie fire兼容
		    	//setCalendarActiveCell(calendarData.cells[index+7+7])
			setCalendarActiveCell(getCalendarDataCell((index+7+7)));
			break
		}
		case 36:{//Home
			//xuj update ie fire兼容
			//setCalendarActiveCell(calendarData.cells[_calendarControl.startday+7+7])
			setCalendarActiveCell(getCalendarDataCell((_calendarControl.startday+7+7)))
			break
		}
		case 37:{//<--
			var index=_calendarControl.activeCellIndex-1;
			if (index<0) index=0;
			//xuj update ie fire兼容
			//setCalendarActiveCell(calendarData.cells[index+7])
			setCalendarActiveCell(getCalendarDataCell((index+7)))
			break
		}
		case 38:{//?0?7?0?3?0?4?0?5?0?1·
			if (_calendarControl.activeCellIndex<14){
				var day=getNumberOfDays(_calendarControl.preMonth,_calendarControl.preYear)+_calendarControl.day-7;
				setCalendarDate(new Date(_calendarControl.preYear, _calendarControl.preMonth, day));
			}
			else{
				var index=_calendarControl.activeCellIndex-7;
				//xuj update ie fire兼容
				//setCalendarActiveCell(calendarData.cells[index+7]);
				setCalendarActiveCell(getCalendarDataCell((index+7)));
			}
			break
		}
		case 39:{//-->
			var index=_calendarControl.activeCellIndex+1;
			if (index>=document.getElementById("calendarData").rows.item(0).cells.length-7) index=document.getElementById("calendarData").rows.item(0).cells.length-8;
			//xuj update ie fire兼容
			//setCalendarActiveCell(calendarData.cells[index+7])
			setCalendarActiveCell(getCalendarDataCell((index+7)))
			break
		}
		case 40:{//
			if (_calendarControl.activeCellIndex>41){
				var day=7-(getNumberOfDays(_calendarControl.month,_calendarControl.year)-_calendarControl.day);
				setCalendarDate(new Date(_calendarControl.nextYear, _calendarControl.nextMonth, day));
			}
			else{
				var index=_calendarControl.activeCellIndex+7;
				//xuj update ie fire兼容
				//setCalendarActiveCell(calendarData.cells[index+7]);
				setCalendarActiveCell(getCalendarDataCell((index+7)));
			}
			break
		}
	}
}

function _calendar_today_onclick(){
	changeCalendarDate(_calendarControl.todayYear,_calendarControl.todayMonth,_calendarControl.todayDay)
	var index=_calendarControl.todayDay+_calendarControl.startday-1;
	//xuj update ie fire兼容
	//setCalendarActiveCell(document.getElementById("calendarData").cells[index+7]);
	setCalendarActiveCell(getCalendarDataCell((index+7)));
	dropDownSelected();
}

function getNumberOfDays(month,year){
	var numDays=new Array(31,28,31,30,31,30,31,31,30,31,30,31)
	n=numDays[month]
	if (month==1 && (year%4==0 && year%100!=0 || year%400==0)) n++
	return n
}