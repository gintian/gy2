var _activeElement=null;
var _activeEditor=null;
var _forEditor=null;
var _activeTable=null;
var _dropdown_window=null;
var _isDropDownPage=false;

var _document_loading=false;
var _stored_element=null;
var _array_dataset=new Array();
var _tabset_list=new Array();
var _xml_list=new Array();
var _activeRowIndex=1;
var _skip_activeChanged=false;

/*
 *firefox ??????children
 */
if (typeof Node != 'undefined') 
{ 
  if (typeof Node.children == 'undefined')
  { 
    eval('Node.prototype.childrengetter = function() {return  this.childNodes;}'); 
  } 
} 

function isFileIncluded(fileId){
	var included=false;
	eval("included=(typeof(_fileIncluded_"+fileId+")!=\"undefined\")");
	return included;
}

function getPlatform(){
	return window.clientInformation.platform;
}

function getIEVersion(){
	var index=window.clientInformation.userAgent.indexOf("MSIE");
	if (index<0){
		return "";
	}
	else{
		return window.clientInformation.userAgent.substring(index+5, index+8);
	}
}

function getRowByCell(cell){
	return cell.parentElement;
}

function getTableByCell(cell){
	if (cell.table) return cell.table;
	var tbody=getRowByCell(cell).parentElement;
	if (tbody) return tbody.parentElement;
}

function getTableByRow(row){
	var tbody=row.parentElement;
	if (tbody) return tbody.parentElement;
}

function getElementEventName(element, eventName){
	var result="";
	if (element.extra!="dockeditor")
		result=element.id+"_"+eventName;
	else{
		var holder=element.editorHolder;
		if (holder) result=holder.id+"_"+eventName;
	}
	return result;
}

var _user_events = new Object();

function isUserEventDefined(function_name){
	if (function_name=="") return false;
	var eventInfo=_user_events[function_name];
	if (eventInfo==null) {
		eventInfo=new Object();
		_user_events[function_name]=eventInfo;
	 	var script="eventInfo.defined=(typeof("+function_name+")!=\"undefined\");" +
	 		"if (eventInfo.defined) eventInfo.handle=" + function_name + ";";
	 	eval(script);
	}
	return eventInfo.defined;
}

function fireUserEvent(function_name, param){
	var result;

	if (function_name=="") return;
	var eventInfo=_user_events[function_name];
	if (eventInfo==null) {
		if (!isUserEventDefined(function_name)) return;
		eventInfo=_user_events[function_name];
	}

	if (eventInfo!=null && eventInfo.defined) {
		result=eventInfo.handle(param[0], param[1], param[2], param[3]);
	}

	return result;
	
}

function processActiveElementChanged(activeElement){

	function isChildofTable(obj) {
		var result=null;
		var tmpObj;

		if (obj.getAttribute("extra")=="dockeditor")
			tmpObj=obj.editorHolder;
		else
			tmpObj=obj;

		if (tmpObj.getAttribute("extra")=="tablecell") result=getTableByCell(tmpObj);
		return result;
	}

	function set_activeEditor(editor){

		if (_activeEditor!=editor){
			if (_activeEditor){
				if (needUpdateEditor){
					if (_activeEditor.window==window)
						updateEditorInput(_activeEditor);
					else
						_activeEditor.window.updateEditorInput(_activeEditor);
				}
				if (typeof(hideDropDownBtn)!="undefined") hideDropDownBtn();

				switch (_activeEditor.getAttribute("extra")){
					case "editor":{
						_activeEditor.className="editor";
						break;
					}
					case "dockeditor":{
						hideDockEditor(_activeEditor);
						break;
					}
				}
				refreshElementValue(_activeEditor);
			}

			if (editor && !editor.readOnly){
				var field=getElementField(editor);

				if (editor.getAttribute("extra")=="editor" || editor.getAttribute("extra")=="dockeditor"){
					editor.className="editor_active";
					if (field){
						editor.dataType=field.dataType;
						editor.editorType=field.editorType;
					}

					if (!editor.getAttribute("dropDown") &&
						(editor.getAttribute("dataType")=="date" || editor.getAttribute("dataType")=="datetime")){
						editor.dropDown="dropDownDate";
					}

					if (editor.getAttribute("extra")=="editor" && field){
						editor.maxLength=(field.size>0)?field.size:2147483647;
						if (field.size > 100 && compareText(editor.tagName, "textarea") &&
							!editor.getAttribute("dropDown")){
							_stored_element=editor;
							editor.editorType="textarea";
							setTimeout("showDockEditor(_stored_element);", 0);
						}
					}
				}
				refreshElementValue(editor);
				if (typeof(showDropDownBtn)!="undefined"){
					showDropDownBtn(editor);
				}
				var _dropdown=getEditorDropDown(editor);
				if (_dropdown){
					editor.contentEditable=(!isTrue(_dropdown.fixed));
					if (typeof(showDropDownBtn)!="undefined"){
						if (_dropdown && isTrue(_dropdown.autoDropDown)) showDropDownBox(editor);
					}
				}
				else{
					editor.contentEditable=true;
				}

				if (!(_dropdown && isTrue(_dropdown.fixed)) &&
					!compareText(editor.type, "checkbox")) editor.select();
			}
		    if(editor)//chenmengqing added at 20061214 for template form
		    {	
		   		var dataset=getElementDataset(editor);
		   		if(dataset)
			   		dataset.activeeditor=editor;
		   	}
			_activeEditor=editor;
		}
	}

	function processElementBlur(){
		var doblur=(activeElement!=_activeEditor);

		if (_activeElement){
			if (typeof(_dropdown_btn)!="undefined" && _dropdown_btn){
				doblur=doblur && (_activeElement!=_dropdown_btn) &&
					(activeElement!=_dropdown_btn);
			}

			if (typeof(_dropdown_box)!="undefined" && _dropdown_box){
				var editor=_dropdown_box.editor;
				doblur=doblur && (activeElement!=editor) &&
					(!isChild(activeElement, _dropdown_box));
			}

			if (doblur){
				if (_activeEditor && _activeEditor.dropDownVisible){
					if (typeof(hideDropDownBox)!="undefined") hideDropDownBox();
					hideStatusLabel(window);
				}
				set_activeEditor(null);
			}
		}
		else{
			doblur=false;
		}

		if (activeElement==document.body && _skip_activeChanged){
			_skip_activeChanged=false;
			return;
		}
		if ((doblur || !_activeEditor)){
			var activeTable=isChildofTable(activeElement);
			if (_activeTable!=activeTable){
				if (_activeTable){
					_activeTable.focused=false;

					var row=_activeTable.activeRow;
					if (row) refreshTableRowStyle(row);

					var eventName=getElementEventName(_activeTable, "onBlur");
					fireUserEvent(eventName, [_activeTable]);
				}

				_activeTable=activeTable;

				if (_activeTable){
					_activeTable.focused=true;

					var row=_activeTable.activeRow;
					if (row) refreshTableRowStyle(row);

					var eventName=getElementEventName(_activeTable, "onFocus");
					fireUserEvent(eventName, [_activeTable]);
				}
			}
		}
	}

	try{
	if(navigator.appName.indexOf("Microsoft")!= -1){
	    if(event&&event.propertyName){
	    }else{
	    return;
	    } 
	    }
		_forEditor=activeElement;
		if (window.closed) return;
		if (activeElement==_activeElement) return;

		if (_activeElement){
			if (typeof(hideMenu)!="undefined"){
				if (_activeElement.getAttribute("extra")=="menuframe" ||
					_activeElement.getAttribute("extra")=="menuitem"){
					hideMenu();
				}
			}
		}

		if (activeElement){
			processElementBlur();

			switch (activeElement.getAttribute("extra")){
				case "tablecell":{
					var row=getRowByCell(activeElement);
					var table=getTableByRow(row);
					var dataset=getElementDataset(activeElement);

					table._activeRow=row;
					table._activeCell=activeElement;
					table._activeCellIndex=activeElement.cellIndex;
					if (row.record){
						if (dataset.window==window)
							_dataset_setRecord(dataset, row.record);
						else
							dataset.window._dataset_setRecord(dataset, row.record);
					}
					setActiveTableCell(row, activeElement.cellIndex);
					table._activeRow=null;
					break;
				}
				case "editor":;
				case "dockeditor":{
					set_activeEditor(activeElement);
					break;
				}
			}
		}
		_activeElement=activeElement;
		
	}
	catch(e){
		processException(e);
	}
}

function _document_onpropertychange() {
	if (event&&event.propertyName=="activeElement"){
		var activeElementflag = true;
		if(typeof(document.activeElement)!="unknown"){
		if(navigator.appName.indexOf("Microsoft")!= -1){
		for(var elem in document.activeElement){
		activeElementflag=false;
		break;
		}
		if(activeElementflag)
		return ;
		}
		processActiveElementChanged(document.activeElement);
		}
		}
}

function _document_onkeydown(e){
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	switch (e.keyCode){
		case 123:{
			if (_enableClientDebug && e.altKey && e.ctrlKey && e.shiftKey){
				eval(window.prompt("DEBUG", ""));
			}
			break;
		}
	}

}

function _document_oncontextmenu(e){
	e=e?e:(window.event?window.event:null);
	e.returnValue=(!isTrue(_disableSystemContextMenu));
	if (typeof(_array_menu)=="undefined") return;
	for(var i=0; i<_array_menu.length; i++){
		var strHolder=_array_menu[i].popupContainer;
		if (getValidStr(strHolder)!=""){
			var arrayHolder=strHolder.split(",");
			for(var j=0; j<arrayHolder.length; j++){
				if (arrayHolder[j]=="") continue;
				var needPopup;
				eval("needPopup=isChild(event.srcElement,"+arrayHolder[j]+")");
				if (needPopup){
					showMenu(_array_menu[i]);
					event.returnValue=false;
					return;
				}
			}
		}
	}
}

function getPriorTabElement(obj){
	var i=obj.sourceIndex-1;
	var elementCount=document.all.length
	var tmpObj=null;
	while (i<elementCount){
		tmpObj=document.all[i];
		if (tmpObj!=obj)
		{
			switch (tmpObj.tagName.toLowerCase())
			{
			case "input":
			case "textarea":
			case "button":
				if (tmpObj.tabIndex!=-1 && !tmpObj.disabled && !isTrue(tmpObj.readOnly))
				{
					return tmpObj;
				}
			case "td":
				if (tmpObj.extra=="tablecell" && !isTrue(tmpObj.readOnly))
				{
					return tmpObj;
				}
			}
		}
		i--;
	}
}

function getNextTabElement(obj){
	var i=obj.sourceIndex+1;
	var elementCount=document.all.length
	var tmpObj=null;
	while (i<elementCount){
		tmpObj=document.all[i];
		if (tmpObj!=obj)
		{
			switch (tmpObj.tagName.toLowerCase())
			{
			case "input":
			case "textarea":
			case "button":
				if (tmpObj.tabIndex!=-1 && !tmpObj.disabled && !isTrue(tmpObj.readOnly))
				{
					return tmpObj;
				}
			case "td":
				if (tmpObj.extra=="tablecell" && !isTrue(tmpObj.readOnly))
				{
					return tmpObj;
				}
			}
		}
		i++;
	}
}
function _control_onkeydown() {

	function getCell(element){
		if (element.getAttribute("extra")=="tablecell")
			return element;
		else if (element.in_table)
			return element.editorHolder;
	}

	function processTab(element){
		var obj=null;
		if (element.extra=="dockeditor"){
			obj=element.editorHolder;
		}
		else{
			obj=element;
		}
		if (!obj) return;
		if (event.shiftKey)
			obj=getPriorTabElement(obj);
		else
			obj=getNextTabElement(obj);

		try
		{
			if (obj) obj.focus();
			event.returnValue=false;
		}
		catch (e)
		{
			// do nothing
		}
	}

	element=event.srcElement;
	if (isDropdownBoxVisible()){
		if (_dropdown_window) _dropdown_window.processDropDownKeyDown(event.keyCode);
		event.returnValue=true;
	}
	else{
		var rowindex, colindex;
		switch (event.keyCode) {
			//Tab
			case 9:{
				processTab(element);
				break;
			}
			//Enter
			case 13:{
				if (_processEnterAsTab && !compareText(element.tagName, "textarea") || event.shiftKey || event.ctrlKey || event.altKey){
					var cell=getCell(element);
					if (cell && !event.shiftKey){
						var row=getRowByCell(cell);
						var table=getTableByRow(row);
						var maxIndex=checkTableCellIndex(table, 9999, 9999);
						if (row.rowIndex==maxIndex[0] && cell.cellIndex==maxIndex[1] && !isTrue(table.getAttribute("readOnly"))){
							var dataset=getElementDataset(element);
							dataset.insertRecord("end");
							//dataset.modified=false;
							setActiveTableCell(table.activeRow, 0);
						}
						else{
							processTab(element);
						}
					}
					else{
						processTab(element);
					}
				}
				break;
			}
			//ESC
			case 27:{
				if (!element.modified){
					var dataset=getElementDataset(element);
					if (!dataset || dataset.state=="none") break;

					var cell=getCell(element);
					var table=getTableByCell(cell);
					if (cell && !isTrue(table.getAttribute("readOnly"))){
						if (isTrue(table.getAttribute("confirmCancel"))){
							if (confirm(constDatasetConfirmCancel)){
								dataset.cancelRecord();
							}
						}
						else{
							dataset.cancelRecord();
						}
					}
				}
				else{
					setElementValue(element, element.oldValue);
				}
				event.returnValue=false;
				break;
			}
			//Left
			case 37:{
				var cell=getCell(element);
				if (cell){
					if ((event.ctrlKey) || (event.altKey)){
						var table=getTableByCell(cell);
						var rowIndex=getRowByCell(cell).rowIndex;
						var cellIndex=cell.cellIndex;
						cellIndex--;
						setFocusTableCell(table, rowIndex, cellIndex);
						event.returnValue=false;
					}
				}
				break;
			}
			//Up
			case 38:{
				var cell=getCell(element);
				if (cell){
					var dataset=getElementDataset(element);
					if (dataset){
						dataset.movePrev();
						event.returnValue=false;
					}
				}
				break;
			}
			//Right
			case 39:{
				var cell=getCell(element);
				if (cell){
					if ((event.ctrlKey) || (event.altKey)){
						var table=getTableByCell(cell);
						var rowIndex=getRowByCell(cell).rowIndex;
						var cellIndex=cell.cellIndex;
						cellIndex++;
						setFocusTableCell(table, rowIndex, cellIndex);
						event.returnValue=false;
					}
				}
				break;
			}
			//Down
			case 40:{
				if (event.altKey){
					showDropDownBox(element);
				}
				else{
					var cell=getCell(element);
					if (cell){
						var table=getTableByCell(cell);
						var dataset=getElementDataset(element);
						if (dataset){
							dataset.moveNext();
							if (dataset.isLast() && !isTrue(table.getAttribute("readOnly")) && !isTrue(dataset.readOnly)){
								dataset.insertRecord("end");
								//dataset.modified=false;
							}
							event.returnValue=false;
						}
					}
				}
				break;
			}
			//Insert
			case 45:{
				var cell=getCell(element);
				if (cell && !isTrue(getTableByCell(cell).getAttribute("readOnly"))){
					var dataset=getElementDataset(element);
					if (!isTrue(dataset.readOnly)){
						dataset.insertRecord("before");
						//dataset.modified=false;
					}
				}
				break;
			}
			//Delete
			case 46:{
				if (event.ctrlKey){
					var cell=getCell(element);
					if (cell){
						var table=getTableByCell(cell);
						if (!isTrue(table.getAttribute("readOnly"))){
							var dataset=getElementDataset(element);
							if (!isTrue(dataset.readOnly)){
								if (isTrue(table.getAttribute("confirmDelete"))){
									if (confirm(constDatasetConfirmDelete)){
										dataset.deleteRecord();
									}
								}
								else{
									dataset.deleteRecord();
								}
							}
							event.returnValue=false;
						}
					}
				}
				break;
			}
			//Home
			case 36:{
				var cell=getCell(element);
				if (cell){
					if ((event.ctrlKey) || (event.altKey)){
						var row=getRowByCell(cell);
						setActiveTableCell(row, 0);
						event.returnValue=false;
					}
				}
				break;
			}
			//End
			case 35:{
				var cell=getCell(element);
				if (cell){
					if ((event.ctrlKey) || (event.altKey)){
						var row=getRowByCell(cell);
						setActiveTableCell(row, 99999);
						event.returnValue=false;
					}
				}
				break;
			}
			//Page Up
			case 33:{
				var cell=getCell(element);
				if (cell && !isTrue(getTableByCell(cell).getAttribute("readOnly"))){
					var dataset=getElementDataset(element);
					var pageIndex=(dataset.record)?dataset.record.pageIndex-1:1;
					dataset.moveToPage(pageIndex);
				}
				break;
			}
			//Page Down
			case 34:{
				var cell=getCell(element);
				if (cell && !isTrue(getTableByCell(cell).getAttribute("readOnly"))){
					var dataset=getElementDataset(element);
					var pageIndex=(dataset.record)?dataset.record.pageIndex+1:1;
					dataset.moveToPage(pageIndex);
				}
				break;
			}
			//F2
			case 113:;
			//F7
			case 118:{
				showDropDownBox(element);
				break;
			}
		}
	}
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

function isChild(obj, parentObj) {
	var tmpObj=obj;
	var result=false;
	if (parentObj) {
		while (tmpObj) {
			if (tmpObj==parentObj){
				result=true;
				break;
			}
			tmpObj=tmpObj.parentElement;
		}
	}
	return result;
}

function initElementDataset(element){
	var dataset=element.getAttribute("dataset");
	if (dataset) setElementDataset(element, dataset);
}

function _element_getId() {
	return this.id;
}

function _element_getDataset() {
	return this.dataset;
}

function _element_getField() {
	return this.field;
}

function _element_getTag() {
	return this.tag;
}

function _element_setTag(tag) {
	this.tag=tag;
}


function initElement(element){
	var initChildren=true;
	if(navigator.appName.indexOf("Microsoft")!= -1){
	for(var elem in element){
	initChildren=false;
	break;
	}
	if(initChildren)
	return true;
	else
	initChildren=true;
	}
	var _extra=element.getAttribute("extra");
	if (_extra){
		element.getId=_element_getId;
		element.getTag=_element_getTag;
		element.setTag=_element_setTag;
	
		switch (_extra){
			case "fieldlabel":{				
				element.getDataset=_element_getDataset;
				element.getField=_element_getField;
		
				if (!element.className) element.className=_extra;

				var dataset;
				var _dataset=element.getAttribute("dataset");
				if (typeof(_dataset)=="string"){
					dataset=getDatasetByID(_dataset);
				}
				else{
					dataset=_dataset;
				}
				element.dataset=dataset;
				refreshElementValue(element);
				initChildren=false;
				break;
			}
			case "columnheader":{
				if (!element.className) element.className=_extra;
				element.noWrap=true;
				element.onclick=_table_head_onclick;
				element.onmouseover=_table_head_onmouseover;
				element.onmouseout=_table_head_onmouseout;
				refreshElementValue(element);
				initChildren=false;
				break;
			}
			case "columnfooter":{
				if (!element.className) element.className=_extra;
				refreshElementValue(element);
				initChildren=false;
				break;
			}
			case "datalabel":{				
				element.getDataset=_element_getDataset;
				element.getField=_element_getField;
				
				if (!element.className) element.className=_extra;
				initElementDataset(element);
				initChildren=false;
				break;
			}
			case "panel":
				element.getDataset=_element_getDataset;
				element.getField=_element_getField;
				if (!element.className) element.className=_extra;
				initElementDataset(element);
				refreshElementValue(element);
				break;
			case "editor":				
				element.getDataset=_element_getDataset;
				element.getField=_element_getField;
				
				if (!element.className) element.className=_extra;

				initEditor(element);
				initChildren=false;
				break;
			case "dockeditor":{				
				element.getDataset=_element_getDataset;
				element.getField=_element_getField;
				
				if (!element.className) element.className="editor_active";

				initEditor(element);
				initChildren=false;
				break;
			}
			case "datatable":{				
				element.getDataset=_element_getDataset;
				
				if (_isDropDownPage || isTrue(element.isDropDownTable)){
					if (!element.className) element.className="dropdowntable";
				}
				else{
					if (!element.className) element.className="datatable";
				}

				initElementDataset(element);
				initDataTable(element, !isTrue(element.getAttribute("skipRebuild")));
				element.onkeydown=_control_onkeydown;
				break;
			}
			case "tablecell":{
				element.getField=_element_getField;
				
				if (!element.className)
					element.className=_extra;
				initChildren=false;
				break;
			}
			case "datapilot":{		
				element.getDataset=_element_getDataset;
				
				if (!element.className) element.className=_extra;
				initElementDataset(element);
				initDataPilot(element);
				break;
			}			
			case "pagepilot":
			{		
				element.getDataset=_element_getDataset;
				
				if (!element.className) element.className=_extra;
				initElementDataset(element);
				_initPagePilot(element);
				initChildren=false;
				break;
			}
			case "menubar":{
				if (!element.className) element.className=_extra;
				initMenuBar(element);
				break;
			}
			case "button":{
				if (!element.className) element.className=_extra;

				initButton(element);
				initChildren=false;
				break;
			}
			case "tree":
			{
				if (!element.className) element.className=_extra;
				initTree(element);
				initChildren=false;
				break;
			}
			case "tabset":
			{
				if (!element.className) element.className=_extra;
				initTabSet(element);
				initChildren=false;
				break;
			}
			default:
			{
				if (!element.className &&_extra) element.className=_extra;
				break;
			}
		}

		element.window=window;
		fireUserEvent("document_onInitElement", [element, _extra]);
	}
	return initChildren;
}

function initElements(element){	
	
	if (compareText(element.getAttribute("extra"), "tabset"))
	{
		_tabset_list[_tabset_list.length]=element;
	}
	else
	{
		if (!initElement(element)) 
		   return;
	}
	for (var i=0; i<element.children.length; i++)
	{
		initElements(element.children[i]);
	}
}

function uninitElement(element)
{
	var _extra=element.getAttribute("extra");
	switch (_extra){
	    case "panel":;
		case "datalabel":;
		case "editor":;
		case "dockeditor":;
		case "datatable":;
		case "tablecell":;
		case "pagepilot":;
		case "datapilot":{
			if (typeof(setElementDataset)!="undefined") setElementDataset(element, null);
			if (typeof(element.window)!="undefined") element.window=null;
			break;
		}
	}
}

function uninitElements(element){
	for(var i=0; i<_array_dataset.length; i++){
		var dataset=_array_dataset[i];
		if (dataset.window==window) dataset.setMasterDataset(null);
		dataset.window=null;
	}

	if (!element) element=document.body;	
	if (element) {
		for (var i=0; i<element.children.length; i++){
			uninitElements(element.children[i]);
		}
		uninitElement(element);
	}
	
	_dropdown_window=null;
	_dropdown_box=null;
	_dropdown_table=null;
	_dropdown_frame=null;
	_dropdown_dataset=null;
	_date_dropdown_box=null;
	if (_isDropDownPage) {
		if (_dropdown_parentbox) {
			_dropdown_parentbox.editor=null;			
			_dropdown_parentbox.dropDown=null;
		}
		_dropdown_parentwindow=null;
		_dropdown_parentbox=null;
	}
}

function _window_onunload() {
	fireUserEvent("page_onUnload", []);
	uninitElements();
}

function _finishInitializtion(){

	for (var i=0; i<_xml_list.length; i++){
		_xml_list[i].removeNode(true);
	}

	for (var i=0; i<_tabset_list.length; i++){
		initElement(_tabset_list[i]);
	}
	document.body.style.visibility="visible";
}

function initDocument()
{
/*

	if (getIEVersion()<"5.0")
	{
		alert(constErrUnsupportBrowser);
	}
*/
	_document_loading=true;
	try
	{	
		fireUserEvent("page_beforeInit", []);
		
		with (document)
		{			
			if (typeof(_setElementsProperties)!="undefined") 
			   _setElementsProperties();

			for(var i=0; i<_array_dataset.length; i++)
			{
				var dataset=_array_dataset[i];
				if (dataset.masterDataset)
				{
					dataset.setMasterDataset(dataset.masterDataset, dataset.masterKeyFields, dataset.detailKeyFields);
				}
				var event_name=getElementEventName(dataset, "onFilterRecord");
				dataset.filtered=isUserEventDefined(event_name);
			}
			//alert("111");
			//var obj=body;
			//alert(obj.getAttribute("sss"));
			initElements(body);
			//alert("222");
			for(var i=0; i<_array_dataset.length; i++)
			{
				var dataset=_array_dataset[i];
				dataset.refreshControls();
			}

			//setTimeout("_finishInitializtion()", 0);
			_finishInitializtion();

			language="javascript";
			onpropertychange=_document_onpropertychange;
			onkeydown=_document_onkeydown;
			oncontextmenu=_document_oncontextmenu;
		}
		if (!window.onunload) window.onunload=_window_onunload;

		if (typeof(sizeDockEditor)!="undefined") setInterval("adjustControlsSize();", 300);

		setTimeout("if (typeof(document.activeElement)!=\"unknown\") processActiveElementChanged(document.activeElement);", 0);
		
		fireUserEvent("page_afterInit", []);
		fireUserEvent("page_onLoad", []);
	}
	finally
	{
		_document_loading=false;
	}
}

var _ad_box=null;
var _ad_interval=50;
var _ad_count=_ad_interval;


function adjustControlsSize(){
	if (typeof(sizeDockEditor)!="undefined"){
		sizeDockEditor();
		if (typeof(sizeDropDownBtn)!="undefined" && _activeEditor) sizeDropDownBtn(_activeEditor);
		if (typeof(sizeDropDownBox)!="undefined") sizeDropDownBox();
	}
}

function getElementDataset(element){
	switch (element.getAttribute("extra")){
		case "tablecell":{
			return element.dataset;
			break;
		}
		case "tablerow":{
			return element.record.dataset;
			break;
		}
		case "dockeditor":{
			var holder=element.editorHolder;
			if (holder){
				return getElementDataset(holder);
			}
			break;
		}
		default:{
			return element.getAttribute("dataset");
			break;
		}
	}
}

function getElementField(element){
	var dataset=getElementDataset(element);
	if (!dataset) return;

	return dataset.getField(element.getAttribute("field"));
}

function getElementValue(element){
	var eventName=getElementEventName(element, "onGetValue");
	if (isUserEventDefined(eventName)){
		var event_result=fireUserEvent(eventName, [element]);
		return event_result;
	}

	switch (element.getAttribute("extra")){
		case "editor":;
		case "dockeditor":{
			switch (element.type.toLowerCase()){
				case "checkbox":{
					return element.checked;
					break;
				}
				default:{
					var result=element.value;
					var _dropdown=getEditorDropDown(element);
					if (_dropdown){
						if (_dropdown.type=="list" && isTrue(_dropdown.mapValue)){
							var items=getDropDownItems(_dropdown);
							if (items){
								var item=items.find(["label"], [element.value]);
								if (item) result=item.getString("value");
							}
						}
					}	
					return result;
					break;
				}
			}
			break;
		}

		default:{
			return element.value;
			break;
		}
	}
}

function setElementValue(element, value){

	function getEditorValue(element, value){
		var result;

		switch (typeof(value)) {			
			case "string":
			case "boolean":
				result=getValidStr(value);
				var _dropdown=getEditorDropDown(element);

				if (_dropdown){
					if (_dropdown.type=="list" && isTrue(_dropdown.mapValue)){
						result="";
						var items=getDropDownItems(_dropdown);
						if (items){
							var item=items.find(["value"], [value]);
							if (item) result=item.getString("label");
						}
					}
				}

				break;
			case "object":
				switch (element.dataType) {
					case "date":
						result=formatDate(value, "yyyy-MM-dd");
						break;
					case "time":
						result=formatDate(value, "hh:mm:ss");
						break;
					case "datetime":
						result=formatDate(value, "yyyy-MM-dd hh:mm:ss");
						break;
					default:
						result=value;
						break;
				}
				break;
			case "number":
				result=value;
				break;
			default:
				result=value;
				break;
		}
		return result;
	}

	switch (element.getAttribute("extra")){
		case "fieldlabel":{
			var eventName=getElementEventName(element, "onRefresh");
			if (isUserEventDefined(eventName)){
				if (!fireUserEvent(eventName, [element, value])) break;
			}
			element.innerHTML=value;
			break;
		}

		case "datalabel":{
			if (element.oldValue==value) return;
			element.oldValue=value;

			var eventName=getElementEventName(element, "onRefresh");
			if (isUserEventDefined(eventName)){
				if (!fireUserEvent(eventName, [element, value])) break;
			}
			
			element.innerText=value;
			break;
		}
		case "panel": //chenmengqing added at 20061118
		    //alert(getEditorValue(element, value));
		    var tmp=getEditorValue(element, value);
		    if(tmp=="")
			{
				element.innerText=tmp; //dengcan 20100119
			    element.insertAdjacentHTML("afterBegin", "&nbsp;");
		    }
		    else
		    {   
		        var field_name=element.getAttribute("field");
		        if(field_name.indexOf("t_")!=-1)//插入子集区域
		        {
		        	showSubDomainView(element,tmp);
		        }
		        else
		    		element.innerText=tmp;
		    }
			break;
		case "editor":;
		case "dockeditor":{

			if (element.oldValue==value && !element.modified) return;
			var eventName=getElementEventName(element, "onSetValue");
			if (isUserEventDefined(eventName)){
				if (!fireUserEvent(eventName, [element, value])) break;
			}

			
			element.keyValue=value;
			switch (element.type.toLowerCase()){
				case "checkbox":{
					element.checked=isTrue(value);
					break;
				}
				case "image":  //chenmengqing added at 20061117
					if(value=="blank")
						element.src="/images/photo.jpg"; 
					else
						element.src=getEditorValue(element, value);

					break;
				default:{
					element.value=getEditorValue(element, value);
					element.keyValue=value;
					break;
				}
			}
			break;
		}

		case "columnheader":{
			var table=getTableByCell(element);
			var eventName=table.id + "_" + element.name + "_onHeaderRefresh";
			if (isUserEventDefined(eventName)){
				if (!fireUserEvent(eventName, [element, value])) break;
			}
			element.innerHTML=value;
			break;
		}

		case "columnfooter":{
			var table=getTableByCell(element);
			var eventName=table.id + "_" + element.name + "_onFooterRefresh";
			if (isUserEventDefined(eventName)){
				if (!fireUserEvent(eventName, [element, value])) break;
			}
			element.innerHTML=value;
			break;
		}

		case "tablecell":{
			var table=getTableByCell(element);
			var eventName=table.id + "_" + element.name + "_onRefresh";
			if (isUserEventDefined(eventName)){
				var record=getRecordByCell(element);
				if (!fireUserEvent(eventName, [element, value, record])) break;
			}

			if (element.getAttribute("name")=="select") {
				var record=getRecordByCell(element);
				if (record) {
					if (isTrue(record.getValue("select"))){
						element.innerHTML="<input type=checkbox checked onclick=\"return _table_checkbox_onclick();\" style=\"height:16\">";
					}
					else {
						element.innerHTML="<input type=checkbox onclick=\"return _table_checkbox_onclick();\" style=\"height:16\">";
					}
				}
			}
			else {
				var tmpHTML;
				switch (element.getAttribute("editorType")){
					case "checkbox":{
						if (isTrue(value)){
							tmpHTML="<font face=Marlett size=2>a</font>";
						}
						else{
							tmpHTML="<font face=Webdings size=1 color=silver>c</font>";
						}
						element.innerHTML=tmpHTML;
						break;
					}
					default:{
						tmpHTML=getEditorValue(element, value);
						if (tmpHTML=="") tmpHTML=" ";
						element.innerText=tmpHTML;
					}
				}
			}
			break;
		}
		case "treenode":{
			var node=element.node;
			var canceled=false;
			var eventName=getElementEventName(getTableByCell(element), "onRefresh");
			if (isUserEventDefined(eventName)){
				canceled=(!fireUserEvent(eventName, [element, value, node]));
			}
			if (!canceled) element.innerHTML=value;

			if (node.checkable){
				tmpHTML="<input type=\"checkbox\" "+((node.checked)?"checked":"")+
					" onclick=\"return _tree_checkbox_onClick();\">";
				element.insertAdjacentHTML("afterBegin", tmpHTML);
				element.firstChild.node=node;
			}

			var tmpHTML="";

			if (node.icon){
				if (node.hasChild && node.expanded && node.expandedIcon)
					tmpHTML="<img src=\""+node.expandedIcon+"\" class=\"icon\">";
				else
					tmpHTML="<img src=\""+node.icon+"\" class=\"icon\">";
				element.insertAdjacentHTML("afterBegin", tmpHTML);
			}


			var record=node.data;
			var button;
			if (node.hasChild){
				var button_img=(node.expanded)?"collapse.gif":"expand.gif";
				button=document.createElement("<img id=_button_expand hideFocus=true class=\"expandbutton\" src=\""+_theme_root+"/"+button_img+"\""+
					" language=javascript onclick=\"return _tree_expendclick(this);\">");

				button.treenode=element;
				element.insertAdjacentElement("afterBegin", button);
			}
			else{
				element.insertAdjacentHTML("afterBegin", "<img id=_button_expand hideFocus=true class=\"expandbutton\" src=\""+_theme_root+"/nochild.gif\">");
			}

			tmpHTML="";
			element.button=button;
			for(var i=1; i<node.level; i++){
				tmpHTML+="&nbsp;&nbsp;&nbsp;&nbsp;"
			}
			element.insertAdjacentHTML("afterBegin", tmpHTML);
			break;
		}
		default:{
			element.value=value;
		}
	}
}
/**显示插入子集内容*/
function showSubDomainView(element,xmlcontent)
{
    var field_name=element.getAttribute("field");
    var bread=true;
    /**分析是变化前还是变化后*/
    if (field_name!=null&&field_name.length>1){
    if(field_name.substring(field_name.length-2,field_name.length).indexOf("_1")==-1)//插入子集区域
    	bread=false;
    }
	var xmlrec=getDecodeStr(xmlcontent);
	var XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
	xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(xmlrec))
	   return;
	var rootNode = XMLDoc.documentElement;  
	if(rootNode)
	{
		var divid=field_name+"_div";
		var div=document.getElementById(divid);
		if(!div)
		{
			div=document.createElement("div");
			div.style.width="100%";
			div.style.height="100%";
			div.style.overflow="auto";
			div.className="fixedDiv";
			div.id=divid;
			element.appendChild(div);
		}
		var fields=rootNode.getAttribute("columns");
		var fieldarr=fields.split("`");
		var cols=fieldarr.length;
		var recNodes= rootNode.childNodes;
		var rows=recNodes.length;
		var subview=new SubSetView(rows,cols,fieldarr,div,recNodes,field_name,element);
		subview.showView(bread);
	}
}

function SubSetView(row,col,column,elementdiv,recNodes,field_name,element) {
	this._row=row;
	this._col=col;
	this._column=column;
	this._parent=elementdiv;
	this._recNodes=recNodes;
	this._field_name=field_name;
	this._element=element;
	//this._activeRow=null;
	//this._activeRowIndex=1;	
    this._field_list=new Array();
    for(var i=0;i<this._col;i++)
    {
		var indexname="_"+this._column[i].toUpperCase();
		if(!(typeof(g_fm[indexname])=="undefined"||g_fm[indexname]==null))
			  this._field_list[i]=g_fm[indexname];
		else
			  this._field_list[i]=null; 
	}   
}

SubSetView.prototype.appendRow   =function()
{
    var tr = this._table.insertRow(this._table.rows.length);
    _activeRowIndex=this._table.rows.length-1;
    tr.onmousedown=trMousedown;
	tr.subview=this;
	var    tableid=this._table.getAttribute("id");
    var td = tr.insertCell(tr.cells.length);
 	td.innerHTML="<input type=\"checkbox\" name=\""+this._field_name+"_chk_"+this._table.rows.length+"\">";
 	td.setAttribute("align","center");		    
	tr.setAttribute("I9999","-1");
	for(var j=0;j<this._col;j++)
	{
  		var td = tr.insertCell(tr.cells.length);
  		var fmobj=this._field_list[j];
		if(fmobj)
		{  
					if(fmobj.C=="0")
					{
					    if(fmobj.T=="D")
					  		td.innerHTML="<input type=\"text\" dataType=\"date\" style=\"font-size:9pt;text-align:left;width:100px\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					    else if(fmobj.T=="N")
					  		td.innerHTML="<input type=\"text\" dataType=\"float\" style=\"font-size:9pt;text-align:left;width:100px\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					    else 
					  		td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					}
					else
					{
					  		td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  style=\"font-size:9pt;text-align:left\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					}					
			  		td.setAttribute("align","left");				
		}
		else
		{
			td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
			td.setAttribute("align","left");
		}
	}
	initElements(this._table);
    this.combineXml();		
}

SubSetView.prototype.insRow   =function()
{

	if(_activeRowIndex>this._table.rows.length)
	{
	  _activeRowIndex=1;
	}
	//alert(this._activeRowIndex);

    var tr = this._table.insertRow(_activeRowIndex);//index,
	var    tableid=this._table.getAttribute("id");
    
    tr.onmousedown=trMousedown;
	tr.subview=this;
    var td = tr.insertCell(tr.cells.length);
 	td.innerHTML="<input type=\"checkbox\" name=\""+this._field_name+"_chk_"+this._table.rows.length+"\">";
 	td.setAttribute("align","center");		    
	tr.setAttribute("I9999","-1");
	for(var j=0;j<this._col;j++)
	{
  		var td = tr.insertCell(tr.cells.length);
  		var fmobj=this._field_list[j];
		if(fmobj)
		{  
					if(fmobj.C=="0")
					{
					    if(fmobj.T=="D")
					  		td.innerHTML="<input type=\"text\" dataType=\"date\" style=\"font-size:9pt;text-align:left;width:100px\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					    else if(fmobj.T=="N")
					  		td.innerHTML="<input type=\"text\" dataType=\"float\" style=\"font-size:9pt;text-align:left;width:100px\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					    else 
					  		td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					}
					else
					{
					  		td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  style=\"font-size:9pt;text-align:left\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					}					
			  		td.setAttribute("align","left");				
		}
		else
		{
			td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
			td.setAttribute("align","left");
		}
	}
	initElements(this._table);
    this.combineXml();		
}

SubSetView.prototype.delRow   =function()
{
	  if(!confirm("确定要删除吗？"))
	     return;	
      for (var i=this._table.rows.length-1; i>0; i--)
      {
        var thetr = this._table.rows[i];
        var thechkbox=thetr.cells[0].children[0];
       	if(!thechkbox.checked)
        		continue;
        if(thetr.getAttribute('i9999')==-1)		//只可对非档案库里的子集记录作此操作  dengcan 2011-3-11
	        thetr.removeNode(true);       
	    else if(thetr.getAttribute('i9999')!=-1)
	    	thetr.style.display='none';
      }	  
      this.combineXml();	
}

function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
}


/*
<?xml version="1.0" encoding="GB2312"?>
<records columns="a0405`a0410`a0415">
    <record I9999="1">21`020201`</record>
    <record I9999="2">11`020205`</record>
</records>
*/
SubSetView.prototype.populateSubXml=function()
{
	var xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
	xml += "<records columns=\"" +this._column.join("`")+ "\">";
	var content="";
	for(var i=1;i<this._table.rows.length;i++)
	{
	    var thetr = this._table.rows[i];
	    var i9999=thetr.getAttribute("I9999");
		content += "<record I9999=\""+i9999+"\"  ";
		if(thetr.style.display=='none')
			content +="state=\"D\"";
		else
			content +="state=\"\"";
		content +=" >";
		var values="";
		var tmp="";
		for(var j=1;j<thetr.cells.length;j++)
		{
			var inputobj=thetr.cells[j].children[0];
			var dataType=inputobj.getAttribute("dataType");
  			var codesetid=inputobj.getAttribute("codesetid");
			if(codesetid!=null&&codesetid!="0")
			{  			
			    var codevalue=inputobj.getAttribute("codevalue");
			    if(codevalue==null||codevalue=="")
			       codevalue="&";
				values=values+codevalue+"`";	
			}
			else
			{
				tmp=inputobj.value;
				if(tmp=="")
				   tmp="&"; 
				if(dataType!=null&&dataType=="date"&&tmp!='&')   //邓灿修改  2011/2/17  子集下的日期指标输入不符合格式的数据，需自动清除
				{
				    var _date=new Date(tmp.replace(/-/g, "/"));
					if (isNaN(_date))
					{
						inputobj.value="";
						tmp="&";
					}
				}   
				values=values+tmp+"`";
			}
		}
		values=values.substr(0,values.length-1);
		values=replaceAll(values,"<","〈");
		values=replaceAll(values,">","〉");
		content += values;
		content += "</record>";	
	}
	xml = xml+content+"</records>";
	return	xml;
}

SubSetView.prototype.interpretCode=function(value,fieldobj)
{
  	var codesetid=fieldobj.C;
  	var result=value;
  	if(codesetid!="0"&&value.length>0)
  	{
  		var indexname="_"+codesetid+result;
		var dmobj=g_dm[indexname];
		if(!(dmobj=="undefined"||dmobj==null))
  			result=dmobj.V;
  	}	
  	return result
}

SubSetView.prototype.combineXml=function()
{

	var xml=this.populateSubXml();
	var dataset=getElementDataset(this._element);

	var record=dataset.getCurrent();
	if(!record)
	  return;
	var xml=getEncodeStr(xml);

	record.setValue(this._field_name,xml);

}


SubSetView.prototype.showView = function(chgmode) {

    var tableid=this._field_name+"_table";
    var elem=document.getElementById(tableid);

    if(!elem)
    {
      elem=document.createElement("table");
      elem.id=tableid;

      if(!chgmode)
      {
    	   //elem.onmouseout=subviewMouseout;
    	   //elem.onblur=subviewMouseout;
       	   elem.subview=this;
       }
	  // elem.style.width="100%";  
	 
	   /**head*/
	   var tr = elem.insertRow(elem.rows.length);	
	   tr.style.backgroundImage = "url("+_theme_root+"/button.gif)";
	   tr.className="fixedHeaderTr";
	   if(!chgmode)
	   {
		  	var td = tr.insertCell(tr.cells.length);
		  	td.innerHTML="<img src=\"/images/choose.gif\" title=\"全选\" onclick=\"select_chkall(this,'"+this._field_name+"')\">";
		  	td.setAttribute("align","center");
	   }
	   var width=50;	
	   for(var i=0;i<this._col;i++)
	   {
		  	var td = tr.insertCell(tr.cells.length);
			var fmobj=this._field_list[i];
			if(fmobj){
				td.innerHTML=fmobj.V;
				if(fmobj.C=="0")
					{
					    if(fmobj.T=="D")
					    width+=100;
					    else if(fmobj.T=="N")
					     width+=100;
					    else
					     width+=150;
					 }else
					 width+=150;
				}
			else{
				td.innerHTML=this._column[i];
				width+=150;
				}
		  	td.setAttribute("align","center");
	   }
	    if(this._col>5){
	    elem.style.width="100%";  
	   }else{
	   elem.style.width=width;  
	   }
	   
	   this._parent.appendChild(elem);
	   
	   if(!chgmode)
	   {
		var appbtn=document.createElement("button");
		appbtn.className="button";
		appbtn.innerText="新增";
		appbtn.style.color = "black";
		appbtn.subview=this;
		appbtn.onclick=appendClick;
		appbtn.style.backgroundImage = "url("+_theme_root+"/button.gif)";
		this._parent.appendChild(appbtn);
		var insbtn=document.createElement("button");
		insbtn.className="button";
		insbtn.innerText="插入";
		insbtn.subview=this;
		insbtn.onclick=insClick;
		insbtn.style.color = "black";
		insbtn.style.backgroundImage = "url("+_theme_root+"/button.gif)";
		this._parent.appendChild(insbtn);
		var delbtn=document.createElement("button");
		delbtn.className="button";
		delbtn.innerText="删除";
		delbtn.style.color = "black";
		delbtn.onclick=delClick;
		delbtn.subview=this;
		delbtn.style.backgroundImage = "url("+_theme_root+"/button.gif)";
		this._parent.appendChild(delbtn);	   	
	   }	          
    }
    else
    {
      for (var i=elem.rows.length-1; i>0; i--)
      {
        var thetr = elem.rows[i];
        var thechkbox=thetr.cells[0].children[0];
        thetr.removeNode(true);       
      }	      	
    }
    this._table=elem;	
	/**data domain*/
	if(chgmode)//only read
	{
	    for (var i=0; i<this._recNodes.length; i++) 
		{
		    var tr = elem.insertRow(elem.rows.length);	
			var recNode = this._recNodes.item(i);
			var keyid = recNode.getAttribute("I9999");
			var value = recNode.text;
			var valuearr=value.split("`");
			for(var j=0;j<valuearr.length;j++)
			{
		  		var td = tr.insertCell(tr.cells.length);
		  		var tmp=valuearr[j];
		  		var fmobj=this._field_list[j];
		  		if(fmobj)
		  		{
					td.innerHTML=this.interpretCode(tmp,fmobj);
					if(fmobj.T=="A"||fmobj.T=="M")
		  				td.setAttribute("align","left");
		  			else if (fmobj.T=="D"){
			  			td.setAttribute("align","center");
			  			td.setAttribute("noWrap","true");
		  			}
		  			else
		  				td.setAttribute("align","right");			
		  		}
		  		else	
		  		{  
			  		td.innerHTML=valuearr[j];
		  			td.setAttribute("align","center");
			  	}
			}
		}
	}
	else
	{
	    for (var i=0; i<this._recNodes.length; i++) 
		{
		    var tr = elem.insertRow(elem.rows.length);
		    tr.onmousedown=trMousedown;
		    tr.subview=this;
		    var td = tr.insertCell(tr.cells.length);

	  		td.innerHTML="<input type=\"checkbox\" name=\""+this._field_name+"_chk_"+i+"\">";
	  		td.setAttribute("align","center");	
			var recNode = this._recNodes.item(i);
			var keyid = recNode.getAttribute("I9999");
			var state=recNode.getAttribute("state");
			tr.setAttribute("I9999",keyid);
			 
			if(state&&state=='D') //deleted值为1，表示该记录被删除啦 dengcan 2011-3-11
			{
				tr.style.display='none';
			}
			
			var value = recNode.text;
			var valuearr=value.split("`");
			for(var j=0;j<valuearr.length;j++)
			{
		  		var td = tr.insertCell(tr.cells.length);
			    //td.subview=this;
		  		var tmp=valuearr[j];
		  		var fmobj=this._field_list[j];
		  		if(fmobj)
		  		{
					tmp=this.interpretCode(tmp,fmobj);
					if(fmobj.C=="0")
					{
					    if(fmobj.T=="D")
					  		td.innerHTML="<input type=\"text\"  dataType=\"date\" value=\""+tmp+"\" style=\"font-size:9pt;text-align:left;width:100px\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					    else if(fmobj.T=="N")
					  		td.innerHTML="<input type=\"text\" dataType=\"float\" value=\""+tmp+"\" style=\"font-size:9pt;text-align:left;width:100px\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					    else 
					  		td.innerHTML="<input type=\"text\"  value=\""+tmp+"\" style=\"font-size:9pt;text-align:left\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					}
					else
					{
					  		td.innerHTML="<input type=\"text\" value=\""+tmp+"\" codesetid=\""+fmobj.C+"\" codevalue=\""+valuearr[j]+"\" style=\"font-size:9pt;text-align:left\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					}					
			  		td.setAttribute("align","left");		
		  		}
		  		else	
		  		{  
			  		td.innerHTML="<input type=\"text\"  value=\""+valuearr[j]+"\" style=\"font-size:9pt;text-align:left\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
			  		td.setAttribute("align","left");
			  	}		  		
			}
		}
		initElements(this._table);		
	}
	
}

function subviewMouseout(tableid,obj)
{
    var elem=document.getElementById(tableid);

	var xml=elem.subview.populateSubXml();
	var dataset=getElementDataset(elem.subview._element);

	var record=dataset.getCurrent();
	if(!record)
	  return;
	var xml=getEncodeStr(xml);

	record.setValue(elem.subview._field_name,xml);
}

function trMousedown()
{
	/*this.subview.*/_activeRowIndex=this.sectionRowIndex;

	//alert(this.sectionRowIndex);
}

function delClick()
{
	this.subview.delRow();
}

function appendClick()
{
	this.subview.appendRow();
}

function insClick()
{
	this.subview.insRow();
}

function select_chkall(obj,name)
{
  	if(obj.title=="全选")
  	{
  	  setChkState(1,name);
  	  obj.title="取消全选";
  	}
  	else
  	{
  	  setChkState(2,name);
  	  obj.title="全选";
  	}
}
  
function setChkState(flag,name)
{
      var chklist,objname,i,typeanme;
      chklist=document.getElementsByTagName('INPUT');
      if(!chklist)
        return;
	  for(i=0;i<chklist.length;i++)
	  {
	     typeanme=chklist[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;	       
	     objname=chklist[i].name;
	     if(objname.indexOf(name)==-1)
	        continue;
	     if(flag=="1")
  	       chklist[i].checked=true;
  	     else  
  	       chklist[i].checked=false;
	  }   
}    
  
function refreshElementValue(element){
	var dataset;

	var _extra=element.getAttribute("extra");
	switch (_extra){
		case "fieldlabel":{
			var label=element.getAttribute("field");
			var field=getElementField(element);
			if (field){
				label=field.label;
				if (field.required && !field.readOnly && !field.dataset.readOnly){
						label="<font color=red>*</font>"+label;
				}
			}
			setElementValue(element, label);
			break;
		}

		case "columnheader": {
			var label=getValidStr(element.getAttribute("label"));
			var field=getElementField(element);
			if (!label){
				if (field){
					label=field.label;
				}
				else{
					label=getValidStr(element.getAttribute("name"));
				}
			}
			if (!label){
				label=element.getAttribute("name");
			}

			if (field){
				if (field.required && !field.readOnly && !field.dataset.readOnly){
						label="<font color=red>*</font>"+label;
				}
			}

			setElementValue(element, label);
			break;
		}

		case "columnfooter": {
			break;
		}

		case "tablecell":{
			var row=getRowByCell(element);
			var record=row.record;
			var dataField=element.getAttribute("field");

			if (record){
				var s=record.getString(dataField);
				if (s!=null&&s.length>30){
					setElementValue(element, s.substring(0, 30) + "...");
					element.title=s;
				}
				else{
					setElementValue(element, s);
					element.title="";
				}
			}
			else
				setElementValue(element, "");
			break;
		}

		case "treenode":{
			var node=element.node;

			if (node)
				setElementValue(element, node.label);
			else
				setElementValue(element, "");
			break;
		}
		case "panel": //

			dataset=getElementDataset(element);
			var value="";
			if (dataset){
				var fieldName=element.getAttribute("field");
				if (fieldName) {
						value=dataset.getString(fieldName);
				}			
				
				setElementValue(element, value);
			}
			element.oldValue=getElementValue(element);
			element.modified=false;
			break;
		default:{
			dataset=getElementDataset(element);
			var value="";
			if (dataset){
				var fieldName=element.getAttribute("field");
				if (fieldName) {
					if ((_extra=="editor" || _extra=="dockeditor") && _forEditor==element) {
						value=dataset.getValue(fieldName);
					}
					else {
						value=dataset.getString(fieldName);
					}
				}
				setElementValue(element, value);
			}

			element.oldValue=getElementValue(element);
			element.modified=false;
			break;
		}
	}
}

function getStatusLabel(text){
	if (typeof(_status_label)=="undefined"){
		document.body.insertAdjacentHTML("beforeEnd", "<DIV id=_status_label nowrap style=\"position: absolute; visibility: hidden;"+
			" padding-left: 16px; padding-right: 16px; height: 22px; font-size: 9pt; background-color: #ffffcc; border: 1 solid silver; padding-top:3; z-index: 10000;  filter:alpha(opacity=80)\"></DIV>");
	}
	_status_label.innerHTML=text;
}
function showStatusLabel(parent_window, text, control){
	parent_window.getStatusLabel(text);
	parent_window._status_label.style.visibility="visible";
	if (control){
		var pos=getAbsPosition(control);
		locateStatusLabel(pos[0]+(control.offsetWidth-_status_label.offsetWidth)/2, pos[1]+control.offsetHeight+1);
	}
	else{
		parent_window._status_label.style.posLeft=(document.body.clientWidth - _status_label.offsetWidth) / 2;
		parent_window._status_label.style.posTop=(document.body.clientHeight - _status_label.offsetHeight) / 2;
		parent_window.document.onmousemove=null;
	}

}

function hideStatusLabel(parent_window){
	if (!parent_window.closed && parent_window._status_label){
		parent_window.document.onmousemove=null;
		parent_window._status_label.style.visibility="hidden";
	}
}

function locateStatusLabel(x, y){
	if (x==0 && y==0) return;

	var posX=document.body.clientWidth + document.body.scrollLeft - _status_label.offsetWidth;
	var posY=document.body.clientHeight + document.body.scrollTop - _status_label.offsetHeight;
	posX=(x<posX)?x:posX;
	posY=(y<posY)?y:posY;

	_status_label.style.posLeft=posX + 1;
	_status_label.style.posTop=posY + 1;
}

function isDropdownBoxVisible(){
  if (typeof(_dropdown_box)!="undefined" && _dropdown_box)
          return (_dropdown_box.style.visibility=="visible")
  else
          return false;
}

// ParameterSet
function ParameterSet() {
	this._parameters = new Array();
}

// Methods
ParameterSet.prototype._addParameter = function(name) {
	parameter = new Object();
	parameter.dataType = "string";		
	parameter.name = name;		
	var property = "__" + name.toLowerCase();
	var _parameters = this._parameters;
	_parameters[property] = parameter;
	_parameters[_parameters.length] = parameter;
	return parameter;
}

ParameterSet.prototype._getParameter = function(name) {
	var _parameters = this._parameters;
	if (typeof(name) == "number"){
		var index = getInt(name);
		var parameter = _parameters[index];
		return parameter;
	}
	else{
		var property = "__" + name.toLowerCase();
		var parameter = _parameters[property];		
		return parameter;
	}
}

ParameterSet.prototype.count = function() {
	return this._parameters.length;
}

ParameterSet.prototype.indexToName = function(index) {
	var parameter = this._getParameter(index);
	if (parameter) {
		return parameter.name;
	}
}

ParameterSet.prototype.setValue = function(name, value) {
	var parameter = this._getParameter(name);
	if (!parameter && typeof(name) != "number") {
		parameter = this._addParameter(name);
	}
	if (parameter){
		parameter.value = value;
	}
}

ParameterSet.prototype.getValue = function(name) {	
	var parameter = this._getParameter(name);
	if (parameter) {
		return parameter.value;
	}
}

ParameterSet.prototype.setDataType = function(name, dataType) {
	var parameter = this._getParameter(name);
	if (!parameter && typeof(name) != "number") {
		parameter = this._addParameter(name);
	}
	if (parameter){
		parameter.dataType = dataType;
	}
}

ParameterSet.prototype.getDataType = function(name) {	
	var parameter = this._getParameter(name);
	if (parameter) {
		return parameter.dataType;
	}
}

ParameterSet.prototype.clearAll=function()
{
	delete this._parameters;
	this._parameters = new Array();	
	/*
	for(var i=0;i<this._parameters.length;i++)
	{
	  this._parameters[i]=null;
	}
	this._parameters.length=0;
	*/
	
}

var Prototype = {
  Version: '1.3.1',
  emptyFunction: function() {}
}

/**var Abstract = new Object();

Object.extend = function(destination, source) {
  for (property in source) {
    destination[property] = source[property];
  }
  return destination;
}

Object.prototype.extend = function(object) {
  return Object.extend.apply(this, [this, object]);
}**/


Function.prototype.bind = function(object) {
  var __method = this;
  return function() {
    __method.apply(object, arguments);
  }
}

Function.prototype.bindAsEventListener = function(object) {
  var __method = this;
  return function(event) {
    __method.call(object, event || window.event);
  }
}

if (!Array.prototype.push) {
  Array.prototype.push = function() {
		var startLength = this.length;
		for (var i = 0; i < arguments.length; i++)
      this[startLength + i] = arguments[i];
	  return this.length;
  }
}

if (!Function.prototype.apply) {
  Function.prototype.apply = function(object, parameters) {
    var parameterStrings = new Array();
    if (!object)     object = window;
    if (!parameters) parameters = new Array();
    
    for (var i = 0; i < parameters.length; i++)
      parameterStrings[i] = 'parameters[' + i + ']';
    
    object.__apply__ = this;
    var result = eval('object.__apply__(' + 
      parameterStrings.join(', ') + ')');
    object.__apply__ = null;
    
    return result;
  }
}



//--------------------------form????
function $() {
  var elements = new Array();
  var tmp=null;
  for (var i = 0; i < arguments.length; i++) 
  {
    var element = arguments[i];
    if (typeof element == 'string')
    {
      tmp=document.getElementsByName(element);
      if(tmp==null||tmp.length<1)
      {
         element=document.getElementById(element);
         if(element!=null){
         	elements.push(element);
         }
      }
      else
      {
      	 element=tmp;
      	 
    	 for(var j=0;j<element.length;j++)
       	    elements.push(element[j]);      
      }
      //element=document.getElementsByName(element);
    }
  }
  if(elements.length==1)
     return elements[0];
  return elements;
}








function AjaxBind() {
}

AjaxBind.prototype.bind=function(elem,value)
{		

	switch(elem.tagName) {
		case "INPUT": 
			switch (elem.type.toLowerCase())
			{
				case "text": ;
				case "hidden": ;
				case "password": 
				      this.bindText(elem, value); 
				      break;
				case "checkbox":;
				case "radio": 
				      this.bindRadioOrCheckbox(elem, value); 
				      break;
			}
			break;
		case "TEXTAREA":
			this.bindText(elem, value);
			break; 
		case "TABLE": 
			this.bindTable(elem, value);
			break; 
		case "SELECT": 
			this.bindSelect(elem, value);
			break; 
		case "DIV":
		case "SPAN":
		case "TD":		
			elem.innerHTML = value;
			break;
	}
}

AjaxBind.prototype.reportError=function(elem, value, msg)
{
     throw "Data bind failed: "+msg;	
}

AjaxBind.prototype.bindText=function(elem,value)
{
    elem.value = value;	
}

AjaxBind.prototype.bindRadioOrCheckbox=function(elem,value)
{
	var ret = false;
	switch (typeof(value)) 
	{
		case 'boolean': ret = value; break;
		case 'string': ret = (value == "1" || value == "true" || value == "yes"); break;
		case 'number': ret = (parseInt(value) == 1); break;
		default: ret = false;
	}
	elem.checked = ret;	
}

AjaxBind.prototype.bindSelect=function(elem,value)
{
		if (typeof(value) != "object" || value.constructor != Array) {
			this.reportError(elem,value,"Array Type Needed for binding select!");
		}
		// delete all the nodes.
		while (elem.childNodes.length > 0) {
			elem.removeChild(elem.childNodes[0]);
		}
		// bind data
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

AjaxBind.prototype.bindTable=function(elem,value)
{
	var colarr;
	var even=0;
	for (var irow = 0; irow < value.length; irow++) 
	{
	  colarr=value[irow];
	  var tr = elem.insertRow(elem.rows.length);
	  for(var icol = 0; icol < colarr.length; icol++)
	  {
	  	var td = tr.insertCell(tr.cells.length);
	  	td.innerHTML=colarr[icol];
	  	td.className="RecordRow";
	  }
	  ++even;
	  if(even%2==0)
	  {
		//tr.setAttribute("class","trShallow1");
		tr.className=	 "trShallow"; 	
	  }
	  else
	  {
		//tr.setAttribute("class","trDeep1");	
		tr.className=	 "trDeep"; 	  
	  }
	}
}
//-------------------??????
var AjaxBind=new AjaxBind();