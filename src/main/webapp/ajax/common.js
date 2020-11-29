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
var _ShowSubDomainView=false;//初始加载模板子集，此参数为true时开始使用ext方式加载子集
var _showRenderFlag = false;//附件编辑窗口起始页面标识
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
			  
			  //20141018 dengcan  代码型指标支持模糊查找功能 
			  if (editor && !editor.readOnly){
				  var codesetid='';
				  var dropDown='';
				  var field=getElementField(editor); 
				  if(field&&field.codesetid!='0') //普通指标
				  {
					  codesetid=field.codesetid;
					  dropDown=field.dropDown;
				  }
				  else if(editor.codesetid&&editor.codesetid!='0') //模板子集指标
				  {
					  codesetid=editor.codesetid;
					  dropDown=editor.dropDown;
				  }
				  
				  if (codesetid!=''&&dropDown=='dropDownList'){
						 editor.onkeyup=function(aevent){
							 
							   if (!isDropdownBoxVisible()){
									if (editor)
									{ 
										editor.mhpp='1';  //20141018 dengcan  下拉框支持磨合匹配
										showDropDownBox(editor);
									}
								}
								else
								{
									 
									var e = aevent || window.event || arguments.callee.caller.arguments[0];
									if(e&&!(e.keyCode==40||e.keyCode==38))
									{ 
										hideDropDownBox();
										if (editor)
										{ 
											editor.mhpp='1';  //20141018 dengcan  下拉框支持磨合匹配
											showDropDownBox(editor);
										}
									} 
								}
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
					!compareText(editor.type, "checkbox")
					&&!compareText(editor.type, "textarea")) editor.select();
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
					//xuj add 2013-7-29屏蔽原有日期控件 
					if((activeElement.getAttribute("name")||activeElement.getAttribute("id"))&&(!activeElement.getAttribute("field")||activeElement.getAttribute("field").length==0)&&activeElement.getAttribute("extra")=="editor"&& activeElement.getAttribute("dropDown")=="dropDownDate"){
						break;
					}
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
//生成下框
function _document_onpropertychange(evt) {
	evt=evt?evt:(window.event?window.event:null);
	if (evt&&evt.propertyName=="activeElement"){
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
	 var key = window.event?e.keyCode:e.which;
	switch (key){
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
				eval("needPopup=isChild((e.target?e.target:e.srcElement),"+arrayHolder[j]+")");
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
function _control_onkeydown(evt) {

	evt=evt?evt:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
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
		if (evt.shiftKey)
			obj=getPriorTabElement(obj);
		else
			obj=getNextTabElement(obj);

		try
		{
			if (obj) obj.focus();
			evt.returnValue=false;
		}
		catch (e)
		{
			// do nothing
		}
	}

	element=evt.target?evt.target:evt.srcElement;
	 var key = window.event?evt.keyCode:evt.which;
	if (isDropdownBoxVisible()){
		if (_dropdown_window) _dropdown_window.processDropDownKeyDown(key);
		evt.returnValue=true;
	}
	else{
		var rowindex, colindex;
		switch (key) {
			//Tab
			case 9:{
				processTab(element);
				break;
			}
			//Enter
			case 13:{
				if (_processEnterAsTab && !compareText(element.tagName, "textarea") || evt.shiftKey || evt.ctrlKey || evt.altKey){
					var cell=getCell(element);
					if (cell && !evt.shiftKey){
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
				evt.returnValue=false;
				break;
			}
			//Left
			case 37:{
				var cell=getCell(element);
				if (cell){
					if ((evt.ctrlKey) || (evt.altKey)){
						var table=getTableByCell(cell);
						var rowIndex=getRowByCell(cell).rowIndex;
						var cellIndex=cell.cellIndex;
						cellIndex--;
						setFocusTableCell(table, rowIndex, cellIndex);
						evt.returnValue=false;
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
						evt.returnValue=false;
					}
				}
				break;
			}
			//Right
			case 39:{
				var cell=getCell(element);
				if (cell){
					if ((evt.ctrlKey) || (evt.altKey)){
						var table=getTableByCell(cell);
						var rowIndex=getRowByCell(cell).rowIndex;
						var cellIndex=cell.cellIndex;
						cellIndex++;
						setFocusTableCell(table, rowIndex, cellIndex);
						evt.returnValue=false;
					}
				}
				break;
			}
			//Down
			case 40:{
				if (evt.altKey){
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
							evt.returnValue=false;
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
				if (evt.ctrlKey){
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
							evt.returnValue=false;
						}
					}
				}
				break;
			}
			//Home
			case 36:{
				var cell=getCell(element);
				if (cell){
					if ((evt.ctrlKey) || (evt.altKey)){
						var row=getRowByCell(cell);
						setActiveTableCell(row, 0);
						evt.returnValue=false;
					}
				}
				break;
			}
			//End
			case 35:{
				var cell=getCell(element);
				if (cell){
					if ((evt.ctrlKey) || (evt.altKey)){
						var row=getRowByCell(cell);
						setActiveTableCell(row, 99999);
						evt.returnValue=false;
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
		
		//xuj write 2013-7-29 解决extra="editor" dropDown="dropDownDate"控件使用统一日期控件
			var _calendar = function(obj) {
				return function() {
					popUpCalendar(obj, obj, "`", "", "", "", true, true);
				}
			}
			
			var inputs = document.getElementsByTagName("input");
			for ( var i = 0; i < inputs.length; i++) {
				var obj = inputs[i];
				
				if((obj.getAttribute("name")||obj.getAttribute("id"))&&(!obj.getAttribute("field")||obj.getAttribute("field").length==0)&&obj.getAttribute("extra")=="editor"&& obj.getAttribute("dropDown")=="dropDownDate"){
					with (obj) {
						
						if (window.addEventListener) { // Mozilla, Netscape, Firefox
							//obj.setAttribute("style","");
							//obj.setAttribute("class","complex_border_color");
							addEventListener("click", function(e,obj) {
								if(bShow)// 引用多次initDocument 导致日期控件非ie浏览器无法加载
									return;
								popUpCalendar(this, this, "`", "", "", "", true, true);
							}, false);
						} else {// ie
							//obj.style.border='';
							//obj.setAttribute("className","complex_border_color");
							//obj.className="complex_border_color";
							if(!obj.hadattachEvent){//防止重复添加事件
								attachEvent('onclick', _calendar(obj));
								obj.hadattachEvent=true;
							}
						}
					}
				}
			}
	}finally
	{
		_document_loading=false;
	}
}

/*
 * 双击子集区域，弹出子集显示框  liuzy 20151013
 */
var subname='';
function showTemplateSubSets(subname,title,tabid)
{
    this.subname=subname;
    this.tabid=tabid;
    var subwinId=subname+"subwin";  
	var win1=Ext.getCmp(subwinId);
    win1.title=title;                           
    win1.show();  
    _showRenderFlag = true;
    win1.on("close",function(){	       //当窗口关闭时触发的事件
         var lastindex=subname.substr(subname.length-1,1);
         if(lastindex=='2'){           //当子集表以2为结尾时是变化后，需要将窗口中的数据放入页面的表格中
		    var storeId=subname+"_dataStore";    
		    var store = Ext.data.StoreManager.lookup(storeId);  //利用Ext.data.StoreManager的lookup()方法可以根据storeId得到对应的store
		    var panelname=subname+"1_tablePanel";  
		    var pagepanel=Ext.getCmp(panelname);
            pagepanel.store.removeAll();         //将panel中的store里面的records数据清空
            for (var i = 0; i < store.getCount(); i++) {
				var record = store.getAt(i);
				pagepanel.store.insert(i,record);  //panel的store利用insert方法添加record记录
			}
		}
		_showRenderFlag = false;
    });
}

/*
 * 初始加载模板子集信息，由于此方法需放在Ext.onReady(),所以需延迟加载
 */
function initTemplateSubSets(element)
{
	initTemplateSubSet(element);
	for (var i=0; i<element.children.length; i++)
	{
		initTemplateSubSets(element.children[i]);
	}
}

/*
 * 初始加载模板子集信息
 */
var tabid='';
function initTemplateSubSet(element){
	var _extra=element.getAttribute("extra");
	if (_extra){
		switch (_extra){
			case "panel":
			{
				if (!element.className) element.className=_extra;
				initElementDataset(element);				
				dataset=getElementDataset(element);
				var tablename=dataset.id;
				if(typeof(tablename)!="undefined" && tablename!=''){
				    var index=tablename.lastIndexOf('_');
				    this.tabid=tablename.substring(index+1,tablename.length);
				}
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
			    var tmp=value;
			    if(tmp=="")
				{
					element.innerText=tmp; 
				    element.insertAdjacentHTML("afterBegin", "&nbsp;");
			    }
			    else
			    {   
			        var field_name=element.getAttribute("field");
			        if(field_name.indexOf("t_")!=-1)//插入子集区域
			        {
			        	showSubDomainView(element,tmp);
			        }
			    }
				
				break;		
			}
			default:
			{
				if (!element.className &&_extra) element.className=_extra;
				break;
			}
		}

		element.window=window;
	
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
		        	if (_ShowSubDomainView){
		        		showSubDomainView(element,tmp);
		        	}
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
    var field_name=element.getAttribute("field");//子集名称。格式为t_a19_1
    var bread=true;//=true  变化前  =false  变化后
    /**分析是变化前还是变化后*/
    if (field_name!=null&&field_name.length>1){
	    if(field_name.substring(field_name.length-2,field_name.length).indexOf("_1")==-1)//如果是变化后的子集，则插入子集区域
	    	bread=false;
    }
	var xmlrec=getDecodeStr(xmlcontent);
		xmlrec=replaceAll(xmlrec,"＜","<");
		xmlrec=replaceAll(xmlrec,"＞",">");
		xmlrec=replaceAll(xmlrec,"＇","'");
		xmlrec=replaceAll(xmlrec,"＂",'"');
	var XMLDoc = XmlDocument.create();
	XMLDoc.async=false;
	xmlrec=replaceAll(xmlrec,"&","");
	if(!XMLDoc.loadXML(xmlrec))
	   return;
	var rootNode = XMLDoc.documentElement;///records那层节点
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
		var rwPriv=rootNode.getAttribute("rwPriv");//子集读写权限，用于控制子集中的三个按钮（新增，插入，删除）
		if(typeof(rwPriv)=="undefined" || rwPriv==null)
		{
			alert("生成子集的xml中缺少子集权限、指标权限、指标列宽这三个属性");
			///return;
		}
		if(typeof(sp_flag)!="undefined"&&sp_flag == "2")//如果是浏览打印进入的则不可编辑
			rwPriv = "1";
		var fieldsPriv=rootNode.getAttribute("fieldsPriv");
		var fieldsWidth=rootNode.getAttribute("fieldsWidth");
		var fieldsTitle=rootNode.getAttribute("fieldsTitle");
		var fieldsDefault=rootNode.getAttribute("fieldsDefault");
		//bug 41139 60锁子集没有按照设置的日期格式显示。
		var fieldslops=rootNode.getAttribute("slops");//日期列设置的格式
		var coloumType=rootNode.getAttribute("coloumType");//每列字段的类型
		var coloumCodeset=rootNode.getAttribute("coloumCodeset");//每列对应的代码类
		var coloumDecimalLength=rootNode.getAttribute("coloumDecimalLength");
		var coloumLength=rootNode.getAttribute("coloumLength");
		var subview=new SubSetView(rows,cols,fieldarr,div,recNodes,field_name,element,rwPriv,fieldsPriv,fieldsWidth,fieldsTitle,fieldsDefault,fieldslops,coloumType,coloumCodeset,coloumDecimalLength,coloumLength);
		subview.showViewByExt(bread);
	}
}

function SubSetView(row,col,column,elementdiv,recNodes,field_name,element,rwPriv,fieldsPriv,fieldsWidth,fieldsTitle,fieldsDefault,fieldslops,coloumType,coloumCodeset,coloumDecimalLength,coloumLength) {
	this._row=row;//记录条数
	this._col=col;//列数
	this._column=column;//指标列的字符串数组
	this._parent=elementdiv;//外层div的对象
	this._recNodes=recNodes;//各个record记录
	this._field_name=field_name;//子集的名字
	this._element=element;
	this._rwPriv=rwPriv;//子集的读写权限
	this._fieldsPrivArr=fieldsPriv.split(",");//每个指标的读写权限。如果子集是写，而某个指标是读，那么指标是只读模式
	this._fieldsWidthArr=fieldsWidth.split(",");//每个指标的列宽。以像素为单位
	this._fieldsTitleArr=fieldsTitle.split(",");//每个指标的自定义列头
	this._fieldsDefaultArr=fieldsDefault.split(",");//每个指标的默认取值
	this._fieldsPriv=fieldsPriv;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldsWidth=fieldsWidth;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldsTitle=fieldsTitle;//子集指标失去焦点时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldsDefault=fieldsDefault;//子集指标默认值时，重新组装xml时用到。（populateSubXml（）方法。）
	this._fieldslops=fieldslops;
	this._coloumType=coloumType;
	this._coloumDecimalLength=coloumDecimalLength;
	this._coloumLength=coloumLength;	
	this._coloumCodeset=coloumCodeset;
	this._fieldslopsArr=fieldslops.split(",");//日期格式
	this._coloumTypeArr=coloumType.split(",");//各列的类型
	this._coloumCodesetArr=coloumCodeset.split(",");//对应的代码类
	this._coloumDecimalLengthArr=coloumDecimalLength.split(",");
	this._coloumLengthArr=coloumLength.split(",");
	//this._activeRow=null;
	//this._activeRowIndex=1;	
    this._field_list=new Array();
    this._removedRecords=[];//已经删除的数据 wangrd 2015-04-08
    for(var i=0;i<this._col;i++)
    {
	  	var newFldObj=new Object();
			newFldObj.fldName=this._column[i];
			newFldObj.T=this._coloumTypeArr[i];
			newFldObj.flddesc=this._fieldsTitle.split(",")[i];
			newFldObj.fldPriv=this._fieldsPriv.split(",")[i];
			newFldObj.fldWidth=this._fieldsWidth.split(",")[i];
			newFldObj.defaultValue=this._fieldsDefault.split("`")[i];
			newFldObj.C=this._coloumCodesetArr[i];
			newFldObj.L=this._coloumLengthArr[i];
			newFldObj.D=this._coloumDecimalLengthArr[i];
			 this._field_list[i]=newFldObj;
	}   
}

/*
 * 返回新增的子集记录 空记录
 */
function get_Subset_NewRecord(param)
{
	var strRecord ={};
	strRecord["I9999"]='-1';
	strRecord["canEdit"]='true';
	for(var i=0;i<param.subSetView._col;i++)
	{	
		var value=param.subSetView._fieldsDefaultArr[i];
		var fmobj=param.subSetView._field_list[i];
		if(fmobj!=null && fmobj.C!="0"&&fmobj.fldName!='attach'){
			var showValue=param.subSetView.interpretCode(value,fmobj);  //添加默认值 liuzy 20160129
			value=value+"`"+showValue;
		}
		if(fmobj!=null && fmobj.T=='D'){
			value=replaceAll(value,".","-");
		}
		strRecord[param.subSetView._column[i]]=value;
		if(fmobj!=null && fmobj.T=='D'){
			strRecord[param.subSetView._column[i]+"_D"]=param.subSetView._fieldslopsArr[i];
		}
		
	}
	return strRecord;
}
/*
 * 新增子集记录
 */
function add_Subset_Record(param)
{
	var field_name=param.subSetView._field_name;
	var storeId=field_name+"_dataStore";    
    var store = Ext.data.StoreManager.lookup(storeId);
	var record =get_Subset_NewRecord(param);
	store.insert(store.getCount(),record);
	//param.subSetView.combineSubSetXml(store);
	
	sortstore(store);
}
/*
 * 插入子集记录
 */
function ins_Subset_Record(param)
{
	//获取当前选中第一条记录，目前点击行即选中
	var tablePanel=Ext.getCmp(param.subSetView._field_name+"_tablePanel");
	var store = tablePanel.getStore();
	var records=tablePanel.getSelectionModel().getSelection();
	var index=0;
	if(records.length>0){
		var record=records[0];
		index=store.indexOf(record);
	}
	
	var record =get_Subset_NewRecord(param);	
	store.insert(index,record);
	sortstore(store);
}

/*
 * 删除子集记录
 */
function del_Subset_Record(param)
{
	var tablePanel=Ext.getCmp(param.subSetView._field_name+"_tablePanel");
	var store = tablePanel.getStore();
	var records=tablePanel.getSelectionModel().getSelection();
	if(records.length<1){
		alert(PLEASE_SELECT_ONE_RECORD);
		//Ext.Msg.alert('提示信息',"请选择删除数据！");
		return;
	}
	if(!confirm("确定要删除吗？"))
		return;	
	store.remove(records);
	
	sortstore(store);
}

/*
 *  对store中的数据进行index赋值 liuzy 20151021
 */
function sortstore(store)
{
	for(var i=0;i<store.getCount();i++){
       var rec = store.getAt(i);
       rec.set('index',i);
    }
}

/*
 * 关闭子集显示 liuzy
 */
function close_Subset_Record(param,datas,store)
{
	var subwinId=this.subname+"subwin";  
	var subwin=Ext.getCmp(subwinId);
	subwin.close();
}
/*
 * 保存子集记录
 */
function save_Subset_Record(param)
{
    var storeId=this.subname+"_dataStore";    
    var store = Ext.data.StoreManager.lookup(storeId);  //利用Ext.data.StoreManager的lookup()方法可以根据storeId得到对应的store
    var panelname=this.subname+"1_tablePanel";  
    var pagepanel=Ext.getCmp(panelname);
    pagepanel.store.removeAll();         //将panel中的store里面的records数据清空
    for (var i = 0; i < store.getCount(); i++) {
		var record = store.getAt(i);
		record.commit();//50366 store插入数据后 commit
		pagepanel.store.insert(i,record);  //panel的store利用insert方法添加record记录
		record.commit()//50366 store插入数据后 commit 
	}

	_showRenderFlag = false;
	alert("保存成功！");
}
/*
 * 组装子集某条记录xml storeFlag:store格式的数据。
 */
SubSetView.prototype.getSubSetRecordXml=function(record,storeFlag)
{
	var recordXml="";
	var i9999="";
	var delState="";
	if(storeFlag){
		i9999=record.get("I9999");
		delState=record.get("delState");
	}
	else {
		i9999=record["I9999"];
		delState=record["delState"];
	}
   
    recordXml = "<record I9999=\""+i9999+"\"  ";
	if(delState=='D')//已删除的
		recordXml +="state=\"D\"";
	else
		recordXml +="state=\"\"";
	recordXml +=" >";	
	var values="";
	for(var j=0;j<this._col;j++)
    {	
		var columnFld=this._column[j];
		var value="";
		if(storeFlag)
			value=record.get(columnFld);
		else 
			value=record[columnFld];
		value = Ext.isDate(value)? Ext.Date.dateFormat(value, "Y-m-d") : value;
		if (value==null) value="";
		if (value==undefined||value=="undefined`") value="";
		//alert(value);
   		var fmobj=this._field_list[j];
   		if (fmobj){
   			if(fmobj.T=="A")
   			{
   				if (fmobj.C!="0"){
   					var valuearr=value.split("`");
   					if (valuearr.length>1){
   						value=valuearr[0];
   					}
   				}
   			}
   			else if(fmobj.T=="D"&&value!=null&&value!='')  
			{
			    var _date=new Date(value.replace(/-/g, "/"));
				if (isNaN(_date))
				{
					value="";
				}
			}  
   			else if(fmobj.T=="M")  
			{
			    
			}  
   		}
   		if (value==""){
   			value="&";
   		}
   		values=values+"`"+value;
	}
	values=values.substr(1,values.length-1);
	values=replaceAll(values,"<","〈");
	values=replaceAll(values,">","〉");
	recordXml += values;
	recordXml += "</record>";	
	return recordXml;
}

/*
 * 组装子集xml
 */
SubSetView.prototype.populateSubSetXml=function(store)
{
	var xml = "";
	xml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
	xml += "<records columns=\"" +this._column.join("`")+ "\"";
	xml += " rwPriv=\""+this._rwPriv+"\" fieldsPriv=\""+this._fieldsPriv+"\" fieldsWidth=\""+this._fieldsWidth+"\" fieldsTitle=\""+this._fieldsTitle+"\" fieldsDefault=\""+this._fieldsDefault+"\" ";
	xml +=" slops=\""+this._fieldslops+"\" coloumType=\""+this._coloumType+"\" coloumCodeset=\""+this._coloumCodeset+"\" coloumDecimalLength=\""+this._coloumDecimalLength+"\" coloumLength=\""+this._coloumLength+"\" >";
	var content="";
	//组装当前显示的记录
	for(var i=0;i<store.getCount();i++)	{
	    var record = store.getAt(i);
	    var recordXml=this.getSubSetRecordXml(record,true);	
	    content+=recordXml;
	}
	//组装刚删除i9999不等于-1的记录
	var removedStores=store.getRemovedRecords();
	for(var i=0;i<removedStores.length;i++)	{
		var record = removedStores[i];
	    var i9999=record.get("I9999");
		if (i9999!="-1"){//删除库中已存在的记录
			record.set("delState","D");
			var recordXml=this.getSubSetRecordXml(record,true);	
			content+=recordXml;
		}
	}
	//组装之前删除的记录
	for(var i=0;i<this._removedRecords.length;i++)	{
		var record = this._removedRecords[i];	
		var recordXml=this.getSubSetRecordXml(record,false);	
		content+=recordXml;
	}
	
	xml = xml+content+"</records>";
	return	xml;
}

/*
 * 保存子集xml到内存
 */
SubSetView.prototype.combineSubSetXml=function(store)
{
	var xml=this.populateSubSetXml(store);
	var dataset=getElementDataset(this._element);
	var record=dataset.getCurrent();
	if(!record)
	  return;
	var xml=getEncodeStr(xml);
	record.setValue(this._field_name,xml);
}

/*
 * 子集新增 删除触发事件
 */
SubSetView.prototype.subSetDataChanged = function(store,eOpts) {
	this.combineSubSetXml(store);	
}

/*
 * 子集更新数据触发事件
 */
SubSetView.prototype.subSetUpdate = function(store,record,operation,modifiedFieldNames,eOpts) {
	this.combineSubSetXml(store);
}

/*
 * 获取表格显示列
 */
SubSetView.prototype.getSubSetTableColumns = function(chgmode) {
	var tableColumns=[];
	for(var i=0;i<this._col;i++)
    {	
   		var currentPriv=this._fieldsPrivArr[i];
   		var currentWidth=this._fieldsWidthArr[i];
   		currentWidth=parseInt(currentWidth);
   		var currentTitle="";
   		if (this._fieldsTitleArr.length==this._col)
   		  currentTitle=this._fieldsTitleArr[i];
   		if(currentPriv=="0"){// 指标无权限 不显示
   			continue;
   		}
   		var bUseEditor=false;
   		var strformat="";
   		var strType="";
   		var strEditType="";
   		var strCodeSetid ="";
   		var strAlign="left";
   		var maxlength=100;
   		var editor;
   		var fmobj=this._field_list[i];
   		if (fmobj){
   			if(fmobj.T=="D")
   			{
   				bUseEditor=true;
   				strType="gridcolumn";
   				strEditType="datetimefield";
   				editor=Ext.create('SYSP.DateTimeField',{format:'Y-m-d'});
   				strAlign="right";
   				strformat="Y-m-d";
   				maxlength=fmobj.L;
   			}
   			else if(fmobj.T=="N"){
   				strType="numbercolumn";
   				strEditType="numberfield"
   				strAlign="right";
   				maxlength=fmobj.L+fmobj.D+1;
   			}
   			else if(fmobj.T=="M"){
   				strType="bigtextcolumn";
   				strEditType="bigtextfield";   				
   				maxlength=1000000;
   				if (fmobj.L!=0 && fmobj.L!=10 ){//设置备注长度大于50才控制。
   					maxlength=fmobj.L;
   				}
   			}
   			else {
   				strType="";// "textfield"
   				if (fmobj.C!="0"){
   					bUseEditor=true;
   					strType="codecolumn";
   					strCodeSetid=fmobj.C;
   					editor = Ext.create('SYSP.CodeTreeCombox',{codesetid:strCodeSetid});
   					if(fmobj.C!='@K'){
   						editor.onlySelectCodeset=false;//UM可以选择UN
   					}
   					strEditType="codecomboxfield";// 树 列表方式
   					maxlength=100;
   				}
   				else {
   						maxlength=fmobj.L;
   				}
   			}
   		};
	   		var columnObj={
	   				xtype:strType,
	   				text:currentTitle,
	   	   			width:currentWidth,
	   	   			dataIndex:this._column[i],
	   	   			editablevalidfunc:null,
	   	   			format:strformat,   		
	   	   			align:strAlign,
	   	   			hidden:currentTitle=='附件'?true:false//暂时隐藏子集附件
	   		};
			if(fmobj && fmobj.T=="D"){//日期显示格式与后台模板设置一致 lis 20160718
				columnObj.renderer = function(value, metaData, record, rowIndex, colIndex, store){
	   				if(value){
						var tempColIndex=colIndex;
						if(_showRenderFlag){
							tempColIndex=tempColIndex-1;
						}
	   					var index = this.columns[tempColIndex].dataIndex+"_D";
	   					var format = record.get(index);
	   					value = value.replace(/\./g,'-');
						var date= parseTemplateDate(value);
						value =formatTemplateDate(date,format);
	   				}
	   				return value;
	   			}
		   	}
   		if(fmobj && fmobj.T=="N"){//对数值型小数点位数设置 lis 20160728
   			var decimalWidth = ""
   			if(fmobj.D){
   				for(var j = 0; j < fmobj.D; j++){
   					decimalWidth += "0";
   				}
   			}
   			var format = "0";
   			if(decimalWidth != "")
   				format += "." + decimalWidth
   			columnObj.format = format;
   		}
   		/*if(currentTitle=='附件')
   		{
   			var rwPriv = this._rwPriv;
   		   columnObj.renderer=function(value, metaData, Record,a,b,c,tableView){
			        var index=metaData.recordIndex;
					var html = "<a href=javascript:showfiles('"+tableView.tablename+"','"+index+"','"+rwPriv+"');><img src='/images/file.png' border=0></a>";
					return html;
				};
   		}*/

   		if (currentPriv=="2"){
   			var editorObj={
   					xtype:strEditType,
   					maxLength:maxlength,
   					codesetid:strCodeSetid,
   					allowBlank:true,
   					maxValue:null,
   					format:strformat,
   					validator:null	
   			};
   			if(bUseEditor){
   				columnObj.editor=editor;
   			}
   			else if(currentTitle!='附件'){
   			    columnObj.editor=editorObj;
   			}
   		}
   		tableColumns.push(columnObj);
    }
	return tableColumns;
}

//当有文件附件时，可以点击打开，显示具体的文件 liuzy 20151017
showrender=function(value, metaData, Record,a,b,c,tableView){
        var index=metaData.recordIndex;
		var html = "<a href=javascript:showfiles('"+tableView.tablename+"','"+index+"');><img src='/images/file.png' border=0></a>";
		return html;
}

/*
 * 显示文件附件 liuzy 20151019
 */
function showfiles(tablename,index,_rwPriv){
       var storeId=tablename+"_dataStore";    
       var store = Ext.data.StoreManager.lookup(storeId);  //利用Ext.data.StoreManager的lookup()方法可以根据storeId得到对应的store
       var record = store.getAt(index);
       
       var edit = 0;
       if(_rwPriv=="2"){//有写权限
       	   if(_showRenderFlag==false){
       		edit=0;
	       }else{
	       		if (record.data.canEdit=="true"){
	      			edit=1;
	      		}
	       }
       }
       var value=record.data.attach;
       var list=new Array();
       var rootPath="/multimedia/template/template_"+this.tabid+"/";
       
      
       if(typeof(value)!='undefined' && value!='' && value!=null){
         var i= value.indexOf(',');
         if(i!=-1)
         {
            var lists=value.split(',');
            for(n=0; n<lists.length; n++ )   
            {  
               var listn=lists[n].split('|');
               var map = new HashMap();
               map.put('filename',listn[0]);
               map.put('path',listn[1]);
               map.put('localname',listn[2]);
               map.put('size',listn[3]);
               map.put('id',listn[4]);
               map.put('successed',true);
               list.push(map);
            }           
         }else{
               var listn=value.split('|');
               var map = new HashMap();
               map.put('filename',listn[0]);
               map.put('path',listn[1]);
               map.put('localname',listn[2]);
               map.put('size',listn[3]);
               map.put('id',listn[4]);
               map.put('successed',true);
               list.push(map);
         }
       }
       if(list.length>0){//已有上传文件
	       Ext.create("SYSF.FileUpLoad",{
				renderTo:Ext.getBody(),
				emptyText:"请输入文件路径或选择文件",
				upLoadType:2,
				uploadUrl:rootPath,
				isDownload:true,
				savePath:rootPath,
				fileList:list,
				isShowOrEdit:edit,
				fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx",
				buttonText:'上传',
				success:function(list){
				    if(list.length!=0){
						var valuestr='';
					    for(var m=0;m<list.length;m++){
							var filename = list[m].filename;  //编码后文件名
							var id = list[m].id;              //文件唯一标识      
							var localname=list[m].localname;  //原始文件名 
							var size = list[m].size.replace(" \r\n","");          //文件大小
							var path = list[m].path;          //文件上传路径
							var successed=list[m].successed;  //是否成功标识
							var text=filename+'|'+path+'|'+localname+'|'+size+'|'+id+'|'+m ;
	                        valuestr+=text+',';
						}
						valuestr=valuestr.substring(0,valuestr.length-1);
					    record.set('attach',valuestr);
				    }else{
				        record.set('attach','');
				    }
				},
				//回调方法，失败
	 			error:function(){
	  				Ext.MessageBox.show({  
						title : '文件上传',  
						msg : "文件上传失败 ！", 
						icon: Ext.MessageBox.INFO  
				    })
	 			},
				fileSizeLimit:'20MB',
				isDelete:true
		 }); 
	 }else{
		Ext.create("SYSF.FileUpLoad",{
			renderTo:Ext.getBody(),
			emptyText:"请输入文件路径或选择文件",
			upLoadType:2,
			uploadUrl:rootPath,
			isDownload:true,
			savePath:rootPath,
			isShowOrEdit:edit,
			fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx",
			buttonText:'上传',
			success:function(list){
				if(list.length!=0){
				    var valuestr='';
				    for(var m=0;m<list.length;m++){
						var filename = list[m].filename;  //编码后文件名
						var id = list[m].id;              //文件唯一标识      
						var localname=list[m].localname;  //原始文件名 
						var size = list[m].size.replace(" \r\n","");          //文件大小
						var path = list[m].path;          //文件上传路径
						var successed=list[m].successed;  //是否成功标识
						var text=filename+'|'+path+'|'+localname+'|'+size+'|'+id+'|'+m ;
                        valuestr+=text+',';
					}
					valuestr=valuestr.substring(0,valuestr.length-1);
				    record.set('attach',valuestr);
				}else{
				    record.set('attach','');
				}
			},
			//回调方法，失败
 			error:function(){
  				Ext.MessageBox.show({  
					title : '文件上传',  
					msg : "文件上传失败 ！", 
					icon: Ext.MessageBox.INFO  
			    })
 			},
			fileSizeLimit:'20MB',
			isDelete:true
		});
	}
}

/*
 * 获取表格显示data
 */
SubSetView.prototype.getSubSetTableData = function(chgmode) {
	var storeData=[];
    for (var i=0; i<this._recNodes.length; i++)
	{
		var record={};
    	var recNode = this._recNodes.item(i);
		var keyid = recNode.getAttribute("I9999");		
		var state=recNode.getAttribute("state");	
		if (state==null) state="";		
		
		record.I9999=keyid;
		record.delState=state;
		var value = recNode.text;
		var valuearr=value.split("`");
		if (!chgmode&& this._rwPriv=="2"){
			record.canEdit="true";
		}
		else {
			record.canEdit="false";
		}	
		for(var j=0;j<valuearr.length;j++)
		{
			var tmp=valuearr[j];
			//tmp=tmp.replace(/\r\n/g,"<BR>")  
		    //tmp=tmp.replace(/\n/g,"<BR>");  
			var fmobj=this._field_list[j];
			var slop=this._fieldslopsArr[j];
			var index = tmp.indexOf("type:");//兼容新人事异动有文件类型的情况
			if(index>0){
				tmp = tmp.substring(0,index-1);
			}
			if (fmobj){
				tmp=this.interpretCode(tmp,fmobj);
			  	if(fmobj.C!="0"&&tmp.length>0)	{  //这里的C为codeset
			  		tmp=valuearr[j]+"`"+tmp;
			  	}
				if(fmobj.T=="D"){
					record[this._column[j]+"_D"]=slop;
				}
			}
			record[this._column[j]]=tmp;
		}
		if (record.delState=="D")
			this._removedRecords.push(record);
		else
			storeData.push(record);
	}
	return storeData;
}

/*
 * 获取表格数据列
 */
SubSetView.prototype.getSubSetFields = function(chgmode) {
	var strfields ="'I9999','delState','canEdit'";
	for(var i=0;i<this._col;i++){	
		var fmobj=this._field_list[i];
		if(fmobj.T!=null&&fmobj.T=="D"){
			strfields=strfields+","+"'"+this._column[i]+"_D'";
		}
		strfields=strfields+","+"'"+this._column[i]+"'";
    }
	//strfields=strfields.substr(1);
	strfields="["+strfields+"]";
	var dataFields=Ext.decode(strfields);
	return dataFields;
}

/*
 * 获取表格工具栏
 */
SubSetView.prototype.getSubSetToolBars = function(chgmode) {
	var toolBar=null;
	if (!chgmode&& this._rwPriv=="2"){//变化后
		toolBar=[
		         {id:this._field_name+"_add",icon:null,text:"<font color='black'>新增</font>",cusBtn:"cusBtn",getdata:true,params:{subSetView:this},
		        	 fn:"add_Subset_Record",disabled:false}
		        ,{id:this._field_name+"_ins",icon:null,text:"<font color='black'>插入</font>",cusBtn:"cusBtn",getdata:true,params:{subSetView:this},
		        	 fn:"ins_Subset_Record",disabled:false}
		        ,{id:this._field_name+"_del",icon:null,text:"<font color='black'>删除</font>",cusBtn:"cusBtn",getdata:true,params:{subSetView:this},
		        	 fn:"del_Subset_Record",disabled:false}
		        ,{id:this._field_name+"_save",icon:null,text:"<font color='black'>保存</font>",cusBtn:"cusBtn",getdata:true,params:{subSetView:this},
		        	 fn:"save_Subset_Record",disabled:false}
		        ,{id:this._field_name+"_close",icon:null,text:"<font color='black'>关闭</font>",cusBtn:"cusBtn",getdata:true,params:{subSetView:this},
		        	 fn:"close_Subset_Record",disabled:false}
		]
	}
	return toolBar;
}

/*
 * 使用ext显示表格
 */
SubSetView.prototype.showViewByExt = function(chgmode,tablename) {
	
	 //清除已有窗口组件  liuzy 20151021
	 var subwinId=this._field_name+"subwin";  
	 var destroywin=Ext.getCmp(subwinId);
	 if(!Ext.isEmpty(destroywin)){
	     destroywin.destroy();
	 }
	//清除当前div的已有元素
	var tableDiv =Ext.getDom(this._field_name+'_div');
	if (tableDiv!=null && tableDiv !=undefined)
		tableDiv.innerHTML="";
	
	var bCanEdit=false; //表格可编辑
	var bCanSelect=false;//显示选择框
	if(!chgmode && this._rwPriv=="2") {
		bCanSelect=true;
	}		
	if(!chgmode && this._rwPriv=="2") {//非变化前子集即可编辑 无子集权限的不走此方法，由指标权限控制单元格是否可编辑
		bCanEdit=true;
	}
	
	//表格列头
	var tableColumns=this.getSubSetTableColumns(chgmode);
	//表格内容
	var storeData=this.getSubSetTableData(chgmode);	
	//表格数据列
	var dataFields=this.getSubSetFields(chgmode);	
	//工具栏
	var toolBar=this.getSubSetToolBars(chgmode);
	var me = this;
	//构建表格
	var configs={
	    	prefix:this._field_name+'1',
	    	pagesize:20,
	    	tdMaxHeight:-1,//iE8 多点几个人报错 bug9001
	    //	editable:bCanEdit,
	    	lockable:false,
	    //	selectable:bCanSelect,
	    //  customtools:toolBar,
	    	datafields:dataFields,
	    	beforeBuildComp:function(config){
	    	   var tablename = me._field_name;
			   config.tableConfig.viewConfig={tablename:tablename};
			},
	    //  viewConfig:{tablename:this._field_name},
	    	storedata:storeData,
	    	toolPosition:"bottom",
	    	tablecolumns:tableColumns
	};
	var tablegrid=new BuildTableObj(configs);
	var toolbar1=Ext.getCmp(this._field_name+"_toolbar");
	if (toolbar1){
		toolbar1.setHeight(48);
	}
	tablegrid.getMainPanel().render(this._field_name+'_div');

	//创建一个新的表格，在弹出窗口中显示 liuzy 20151021
	var configs1={
	        prefix:this._field_name,
	    	pagesize:20,
	    	tdMaxHeight:-1,//iE8 多点几个人报错 bug9001
	    	editable:bCanEdit,
	    	lockable:false,
	    	//selectable:bCanSelect,
	    	customtools:toolBar,
	    	datafields:dataFields,
	    	beforeBuildComp:function(config){
	    	   var tablename = me._field_name;
			   config.tableConfig.viewConfig={tablename:tablename};
			   config.tableConfig.selModel={selType:'checkboxmodel'};//由于新版表格控件对于选择框有修改导致此处无法使用,故在这里重写一下 20170711
			},
	    	//viewConfig:{tablename:this._field_name},
	    	storedata:storeData,
	    	forceFit:true,    //定义forceFit为自适应宽度，填充整个页面
	    	toolPosition:"bottom",
	    	tablecolumns:tableColumns
	};
	var tablegrid1=new BuildTableObj(configs1);
	var tableComp = tablegrid1.getMainPanel();
	//放置表格的panel
	var subpanel = Ext.widget('panel', {
	    id:this._field_name+'subset',
		buttonAlign : 'center',
		autoScroll:true,
		width:'100%',
		height:'100%',
	    //bodyPadding: 1,
	    border:false,
	//    forceFit:true,
	    layout:'fit',
	   
	    items:[tableComp]
	});
	var win1=Ext.widget("window",{
         id:this._field_name+'subwin',    //使用id属性唯一标示一个组件，但往往会引起错误，尽量少用
	    // title:title,  
         height:400,  
         width:800,
         layout:'fit',             //当窗口只有一个元素是，使用fit布局将整个窗口填充满
	     modal:true,               //遮罩效果
	     closeAction:'hide',		//当窗口关闭时，hide为隐藏窗口，destroy为销毁窗口  
         items: [subpanel]     
    }); 
	
		
	//数据变化触发事件
	var gridStore =tablegrid1.tablePanel.getStore();
	gridStore.on("datachanged","subSetDataChanged",this,{});
	gridStore.on("update","subSetUpdate",this,{});	
}


///点击子集的“增加”按钮
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
		var currentPriv=this._fieldsPrivArr[j];
		var currentWidth=this._fieldsWidthArr[j];
	   	currentWidth=parseInt(currentWidth);
		var td = tr.insertCell(tr.cells.length);
		if(currentPriv=="0")
		{
			td.className="hideclass";
		}
	  	var fmobj=this._field_list[j];
	  	if(currentPriv==2)//（即指标有写权限）
	  	{
	  		if(fmobj)
			{  
				if(fmobj.C=="0")
				{
					if(fmobj.T=="D")//日期型
						td.innerHTML="<input type=\"text\" dataType=\"date\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else if(fmobj.T=="N")//数值型
						td.innerHTML="<input type=\"text\" dataType=\"float\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else//字符型
						td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}
				else
				{
					if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				td.setAttribute("align","left");
			}
	  	} //currentPriv=2 结束
		else//currentPriv=1或0（即指标读或无权限）
		{
			if(fmobj)
			{  
				if(fmobj.C=="0")
				{
					if(fmobj.T=="D")//日期型
						td.innerHTML="<input type=\"text\" disabled=\"true\" dataType=\"date\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else if(fmobj.T=="N")//数值型
						td.innerHTML="<input type=\"text\" disabled=\"true\" dataType=\"float\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else//字符型
						td.innerHTML="<input type=\"text\"  disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}
				else
				{
					if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\"  disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				td.setAttribute("align","left");
			}
		} ////currentPriv=1或0 结束
	} //for循环 结束
	initElements(this._table);
    this.combineXml();		
}
//点击子集的“插入”按钮
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
		var currentPriv=this._fieldsPrivArr[j];
		var currentWidth=this._fieldsWidthArr[j];
	   	currentWidth=parseInt(currentWidth);
		var td = tr.insertCell(tr.cells.length);
		if(currentPriv=="0")//指标无权限，就让它隐藏
		{
			td.className="hideclass";
		}
	  	var fmobj=this._field_list[j];
	  	if(currentPriv=="2")//如果指标是写权限
	  	{
	  		if(fmobj)
			{  
				if(fmobj.C=="0")
				{
					if(fmobj.T=="D")
						td.innerHTML="<input type=\"text\" dataType=\"date\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else if(fmobj.T=="N")
						td.innerHTML="<input type=\"text\" dataType=\"float\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else 
						td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}
				else
				{
					if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\"  style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				td.setAttribute("align","left");
			}
	  	}//currentPriv=="2" 结束
		else////currentPriv=="1或0 指标是读或无权限
		{
			if(fmobj)
			{  
				if(fmobj.C=="0")
				{
					if(fmobj.T=="D")
						td.innerHTML="<input type=\"text\" disabled=\"true\" dataType=\"date\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else if(fmobj.T=="N")
						td.innerHTML="<input type=\"text\" disabled=\"true\" dataType=\"float\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else 
						td.innerHTML="<input type=\"text\" disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}
				else
				{
					if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\" disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					else
						td.innerHTML="<input type=\"text\" codesetid=\""+fmobj.C+"\" disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				}					
				td.setAttribute("align","left");				
			}
			else
			{
				td.innerHTML="<input type=\"text\" disabled=\"true\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
				td.setAttribute("align","left");
			}
		}//currentPriv=="1或0  结束
	}//for循环 结束
	initElements(this._table);
    this.combineXml();		
}

SubSetView.prototype.delRow   =function()
{
		//先判断是否选中了记录。
	    var isSelected="0";
		  for (var i=this._table.rows.length-1; i>0; i--)
	    {
	      var thetr = this._table.rows[i];
	      var thechkbox=thetr.cells[0].children[0];
	     	if(thechkbox.checked)
	     	{
	     		isSelected="1";
				break;
	     	}
	    }
	    if(isSelected=="0")
	    {
				alert(PLEASE_SELECT_ONE_RECORD);
				return;
	    }
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
	xml += "<records columns=\"" +this._column.join("`")+ "\"";
	xml += " rwPriv=\""+this._rwPriv+"\" fieldsPriv=\""+this._fieldsPriv+"\" fieldsWidth=\""+this._fieldsWidth+"\" fieldsTitle=\""+this._fieldsTitle+"\" fieldsDefault=\""+this._fieldsDefault+"\" ";
	xml +=" slops=\""+this._fieldslops+"\" coloumType=\""+this._coloumType+"\" coloumCodeset=\""+this._coloumCodeset+"\" coloumDecimalLength=\""+this._coloumDecimalLength+"\" coloumLength=\""+this._coloumLength+"\" >";
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
		{
			result=dmobj.V;
		}
		else if(codesetid=='UM'&&typeof (g_dm["_UN"+result])!='undefined')
		{
			result=g_dm["_UN"+result].V;
		}
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
    var isShowCheckbox="0";//=0:不显示选择框 =1：显示选择框
	if(this._rwPriv=="2")//写权限
    {
			isShowCheckbox="1";//写权限，则显示选择框和全选框。
    }
    if(!elem)//这个对象还不存在，那么创建一个新的
    {
      elem=document.createElement("table");
      elem.id=tableid;

      if(!chgmode)//变化后
      {
    	   //elem.onmouseout=subviewMouseout;
    	   //elem.onblur=subviewMouseout;
       	   elem.subview=this;
       }
	  // elem.style.width="100%";  
	  
	
	   /**设置标题*/
	   var tr = elem.insertRow(elem.rows.length);
	   try{
	    	_theme_root=hcm_tabset_root
	    }catch(e){}
	   tr.style.backgroundImage = "url("+_theme_root+"/button.gif)";
	   
	   tr.className="fixedHeaderTr";
	   
	   if(!chgmode)//变化后，全选按钮
	   {
		  	var td = tr.insertCell(tr.cells.length);
		  	td.innerHTML="<img src=\"/images/choose.gif\" title=\"全选\" onclick=\"select_chkall(this,'"+this._field_name+"')\">";
		  	td.setAttribute("align","center");
		  	if(isShowCheckbox=="0")//子集权限不是写
		  	{
		  		td.className="hideclass";
		  	}
	   }
	   var width=50;
	   for(var i=0;i<this._col;i++)//创建标题行
	   {
	   		var currentPriv=this._fieldsPrivArr[i];
	   		var currentWidth=this._fieldsWidthArr[i];
	   		currentWidth=parseInt(currentWidth);
	   		var currentTitle="";
	   		if (this._fieldsTitleArr.length==this._col)
	   		  currentTitle=this._fieldsTitleArr[i];
	   		var td = tr.insertCell(tr.cells.length);
	   		if(currentPriv=="0")//指标无权限
			{
				td.className="hideclass";
			}
			if (currentTitle!=""){
			  td.innerHTML=currentTitle;
			}
			else {
				var fmobj=this._field_list[i];
				if(fmobj)
				{
					td.innerHTML=fmobj.V;//fmobj.V是列的名字
				}				
				else
				{
					td.innerHTML=this._column[i];
				}
			}
			if(currentPriv=="1" || currentPriv=="2")//指标无权限
			{
				width+=currentWidth;
			}
			td.setAttribute("align","center");
	   }//for 结束
	   //设置标题 结束 
	   elem.style.width=width;//设置table的宽度。这个宽度是以pt为单位的   郭峰

	   
	   this._parent.appendChild(elem);//将table加入到div中
	   //增加按钮
	   if(!chgmode && this._rwPriv=="2")//变化后且子集有写权限
	   {
		var appbtn=document.createElement("button");
		appbtn.className="button";
		appbtn.innerText="新增";
		appbtn.style.color = "black";
		appbtn.subview=this;
		appbtn.onclick=appendClick;
		//appbtn.style.backgroundImage = "url("+_theme_root+"/button.gif)";
		this._parent.appendChild(appbtn);
		var insbtn=document.createElement("button");
		insbtn.className="button";
		insbtn.innerText="插入";
		insbtn.subview=this;
		insbtn.onclick=insClick;
		insbtn.style.color = "black";
		//insbtn.style.backgroundImage = "url("+_theme_root+"/button.gif)";
		this._parent.appendChild(insbtn);
		var delbtn=document.createElement("button");
		delbtn.className="button";
		delbtn.innerText="删除";
		delbtn.style.color = "black";
		delbtn.onclick=delClick;
		delbtn.subview=this;
		//delbtn.style.backgroundImage = "url("+_theme_root+"/button.gif)";
		this._parent.appendChild(delbtn);	   	
	   }
	   //增加按钮结束
	   	          
    } //创建table对象 结束
    else //如果table对象已经存在了
    {
		for (var i=elem.rows.length-1; i>0; i--)
	    {
	        var thetr = elem.rows[i];
	        var thechkbox=thetr.cells[0].children[0];
	        thetr.removeNode(true);       
	    }   	      	
    }
    
    
    /**以下开始输出子集内容*/
    
    
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
		  		var currentPriv=this._fieldsPrivArr[j];//指标权限
				var currentWidth=this._fieldsWidthArr[j];//指标列宽
	   		    if(currentPriv=="0")//如果没有权限，就让td隐藏
				{
					td.className="hideclass";
				}
				else if(currentPriv=="1" || currentPriv=="2")
				{
					td.setAttribute("width",currentWidth);
				}
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
			}//遍历某一行的所有列
		}//遍历所有行
	}///变化前 结束
	
	else//如果是变化后
	{
	    for (var i=0; i<this._recNodes.length; i++) 
		{
		    var tr = elem.insertRow(elem.rows.length);
		    tr.onmousedown=trMousedown;
		    tr.subview=this;
		    var td = tr.insertCell(tr.cells.length);

	  		td.innerHTML="<input type=\"checkbox\" name=\""+this._field_name+"_chk_"+i+"\">";
	  		td.setAttribute("align","center");
	  		if(isShowCheckbox=="0")//子集不是写权限，就不要选择框了。
		  	{
		  		td.className="hideclass";
		  	}
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
				var currentPriv=this._fieldsPrivArr[j];
				var currentWidth=this._fieldsWidthArr[j];
	   		    currentWidth=parseInt(currentWidth);
				var td = tr.insertCell(tr.cells.length);
				if(currentPriv=="0")//如果没有权限，就让td隐藏
				{
					td.className="hideclass";
				}
				//td.subview=this;
			  	var tmp=valuearr[j];
			  	var fmobj=this._field_list[j];
			  	if(currentPriv=="2")
			  	{
			  		if(fmobj)
				  	{
						tmp=this.interpretCode(tmp,fmobj);
						if(fmobj.C=="0")
						{
							if(fmobj.T=="D")
							  	td.innerHTML="<input type=\"text\"  dataType=\"date\" value=\""+tmp+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
							else if(fmobj.T=="N")
							  	td.innerHTML="<input type=\"text\" dataType=\"float\" value=\""+tmp+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
							else 
							  	td.innerHTML="<input type=\"text\"  value=\""+tmp+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
						}
						else
						{
									 
							if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
								td.innerHTML="<input type=\"text\" value=\""+tmp+"\" codesetid=\""+fmobj.C+"\" codevalue=\""+valuearr[j]+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
							else
								td.innerHTML="<input type=\"text\" value=\""+tmp+"\" codesetid=\""+fmobj.C+"\" codevalue=\""+valuearr[j]+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
						}
					  	td.setAttribute("align","left");
					  	/**为input设置title，字段内容过多时可以通过title预览 xcs update @2014-9-5**/
					  	var input=td.getElementsByTagName("input")[0];
					  	if(input){
					  		input.setAttribute("title",tmp);
					  	}
				  	}
				  	else	
				  	{  
					  	td.innerHTML="<input type=\"text\"  value=\""+valuearr[j]+"\" style=\"font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					  	td.setAttribute("align","left");
					  	/**为input设置title，字段内容过多时可以通过title预览 xcs update @2014-9-5**/
					  	var input=td.getElementsByTagName("input")[0];
					  	if(input){
					  		input.setAttribute("title",valuearr[j]);
					  	}
					  	
					}
			  	}//currentPriv=2 结束
				else//currentPriv=1或0
			  	{
			  		if(fmobj)
				  	{
						tmp=this.interpretCode(tmp,fmobj);
						if(fmobj.C=="0")
						{
							if(fmobj.T=="D")
								td.innerHTML="<input type=\"text\" disabled=\"true\" dataType=\"date\" value=\""+tmp+"\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;;width:"+currentWidth+";\" extra=\"editor\" dropDown=\"dropDownDate\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
							else if(fmobj.T=="N")
								td.innerHTML="<input type=\"text\" disabled=\"true\" dataType=\"float\" value=\""+tmp+"\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
							else 
								td.innerHTML="<input type=\"text\" disabled=\"true\" value=\""+tmp+"\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
						}
						else
						{
							if(fmobj.C=='UN'||fmobj.C=='UM'||fmobj.C=='@K')
								td.innerHTML="<input type=\"text\" disabled=\"true\" value=\""+tmp+"\" codesetid=\""+fmobj.C+"\" codevalue=\""+valuearr[j]+"\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropdownCode\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
							else
								td.innerHTML="<input type=\"text\" disabled=\"true\" value=\""+tmp+"\" codesetid=\""+fmobj.C+"\" codevalue=\""+valuearr[j]+"\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" dropDown=\"dropDownList\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
						}					
					  	td.setAttribute("align","left");
					  	/**为input设置title，字段内容过多时可以通过title预览 xcs update @2014-9-5**/
					  	var input=td.getElementsByTagName("input")[0];
					  	if(input){
					  		input.setAttribute("title",tmp);
					  	}
					  	
					}
				  	else
				  	{  
					  	td.innerHTML="<input type=\"text\" disabled=\"true\" value=\""+valuearr[j]+"\" style=\"background-color: whitesmoke;font-size:9pt;text-align:left;width:"+currentWidth+";\" extra=\"editor\" onblur=\"subviewMouseout('"+tableid+"',this);\">";
					  	td.setAttribute("align","left");
					  	/**为input设置title，字段内容过多时可以通过title预览xcs update @2014-9-5**/
					  	var input=td.getElementsByTagName("input")[0];
					  	if(input){
					  		input.setAttribute("title",valuearr[j]);
					  	}
					}
			  	}//currentPriv==1或0 结束		  				
			}//内层for循环（对每一行生成多个td） 结束
		}///外层for循环（遍历所有的记录） 结束
		initElements(this._table);		
	}//变化后 结束
	
}


function subviewMouseout(tableid,obj)
{
    var elem=document.getElementById(tableid);
    var codesetid=obj.getAttribute("codesetid");
    /**当光标离开这个input框时更改title内容 xcs update @2014-9-5**/
    obj.setAttribute("title",obj.value);
    if(codesetid){
    	if(codesetid!=""&codesetid!="0"){//解决代码类是获得codevalue而不是获得alue的问题，如果是删除代码类无法保存的问题
    		var objvalue=obj.value;
    		if(objvalue==""){
    			obj.setAttribute("codevalue", "");
    		}
    	}
    }
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
				
				if (s!=null&&typeof(s)=="string"&&s.replace(/[^\x00-\xff]/g,"**").length>30){
					if(s.match(/[^\x00-\xff]/g)!=null) 
						setElementValue(element, s.substring(0, 20) + "..."); 
					else
						setElementValue(element, s.substring(0, 30) + "..."); 
						
			 	    element.title=s;  
			 	    var field=getElementField(element);
			 	    if(field&&field.dataType=='clob'&&field.isReadOnly&&dataField=='appprocess')  //2013-09-03 dengc 如果数据采集控件的字段类型为clob，增加鼠标单击弹出窗口事件，用于显示字段内容描述 
			 	    { 
			 	    	element.detachEvent('onclick',showProcess);
				 			element.attachEvent('onclick',showProcess);  
			 	    }
			 	 
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


function showProcess(){ 
	 var _row=getRowByCell(_activeElement); 
	 var dw=380,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	 if (_row.record){
		 var arguments=_row.record.getString("appprocess");
	     var return_vo= window.showModalDialog("/gz/gz_accounting/sp_process.jsp",arguments, 
	     "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no;");
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


//-------------------------------------------------------
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
//--------------------------------------------------------
//chenmengqing added 20051203
var Prototype = {
  Version: '1.3.1',
  emptyFunction: function() {}
}

var Abstract = new Object();

Object.extend = function(destination, source) {
  for (property in source) {
    destination[property] = source[property];
  }
  return destination;
}
/* 2014-7-17  ajax模板，此方法与EXTJS4.2冲突，邓灿注释掉
Object.prototype.extend = function(object) {
  return Object.extend.apply(this, [this, object]);
}
*/
// 2016-01-25 原bind与EXTJS6 冲突，改成bindObj  guodd
Function.prototype.bindObj = function(object) {
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
//------------------??????------

var Field = {
  clear: function() {
    for (var i = 0; i < arguments.length; i++)
      $(arguments[i]).value = '';
  },

  focus: function(element) {
    $(element).focus();
  },
  
  present: function() {
    for (var i = 0; i < arguments.length; i++)
      if ($(arguments[i]).value == '') return false;
    return true;
  },
  
  select: function(element) {
    $(element).select();
  },
   
  activate: function(element) {
    $(element).focus();
    $(element).select();
  }
}
//-------------------------
var Form = {
  serialize: function(form) {
    var elements = Form.getElements($(form));
    var queryComponents = new Array();
    
    for (var i = 0; i < elements.length; i++) {
      var queryComponent = Form.Element.serialize(elements[i]);
      if (queryComponent)
        queryComponents.push(queryComponent);
    }
    
    return queryComponents.join('&');
  },
  
  getElements: function(form) {
    var form = $(form);

    var elements = new Array();

    for (tagName in Form.Element.Serializers) {
    	
      var tagElements = form.getElementsByTagName(tagName);

      for (var j = 0; j < tagElements.length; j++)
      {
      	//alert(tagElements[j].tagName+"="+tagElements[j].name);
        elements.push(tagElements[j]);
      }
    }
    return elements;
  },
  
  getInputs: function(form, typeName, name) {
    var form = $(form);
    var inputs = form.getElementsByTagName('input');
    
    if (!typeName && !name)
      return inputs;
      
    var matchingInputs = new Array();
    for (var i = 0; i < inputs.length; i++) {
      var input = inputs[i];
      if ((typeName && input.type != typeName) ||
          (name && input.name != name)) 
        continue;
      matchingInputs.push(input);
    }

    return matchingInputs;
  },

  disable: function(form) {
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      element.blur();
      element.disabled = 'true';
    }
  },

  enable: function(form) {
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      element.disabled = '';
    }
  },

  focusFirstElement: function(form) {
    //var form = $(form);
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      if (element.type != 'hidden' && !element.disabled) {
        Field.activate(element);
        break;
      }
    }
  },
  getFocusElement: function(form){
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      if (element.type != 'hidden' && !element.disabled) {
        if(element.selected)
          return element;
        break;
      }
    }
  },	
  reset: function(form) {
    $(form).reset();
  }
}
Form.Element = {
  serialize: function(element) {
	var $ = Form.Element.$;
    var element = $(element);
    var method = element.tagName.toLowerCase();
    var parameter = Form.Element.Serializers[method](element);
    
    if (parameter)
      return encodeURIComponent(parameter[0]) + '=' + 
        encodeURIComponent(parameter[1]);                   
  },
  
  getValue: function(element) {
	var $ = Form.Element.$;
    var element = $(element);
    if(element instanceof Array)
    {
    	var paraArr=new Array();
    	var parameter;
    	for(var i=0;i<element.length;i++)
    	{
    		var method = element[i].tagName.toLowerCase();
   		parameter= Form.Element.Serializers[method](element[i]);
    		if (parameter) 
      		    paraArr.push(parameter[1]);    	  
    	}
    	if(paraArr.join()==",")
    	  return ""
   	//return paraArr.join();
   	return paraArr;
    }
    else
    {
    	var method = element.tagName.toLowerCase();
   	var parameter = Form.Element.Serializers[method](element);
    
    	if (parameter) 
      	return parameter[1];
    }
  },
  
  $:function() {
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
  
}

Form.Element.Serializers = {
  input: function(element) {
    switch (element.type.toLowerCase()) {
      case 'submit':
      case 'hidden':
      case 'password':
      case 'text':
        return Form.Element.Serializers.textarea(element);
      case 'checkbox':  
      case 'radio':
        return Form.Element.Serializers.inputSelector(element);
    }
    return false;
  },

  inputSelector: function(element) {
    if (element.checked)
      return [element.name, element.value];
  },

  textarea: function(element) {
    return [element.name, element.value];
  },

  select: function(element) {
    var value = '';
    if (element.type == 'select-one') {
      var index = element.selectedIndex;
      if (index >= 0)
        value = element.options[index].value || element.options[index].text;
    } else {
      value = new Array();
      for (var i = 0; i < element.length; i++) {
        var opt = element.options[i];
        if (opt.selected)
          value.push(opt.value || opt.text);
      }
    }
    return [element.name, value];
  }
  
}

/*--------------------------------------------------------------------------*/
var $F = Form.Element.getValue;

/*--------------------------------------------------------------------------*/

if (!window.Element) {
  var Element = new Object();
}

Object.extend(Element, {
  toggle: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].style.display = (element[j].style.display == 'none' ? '' : 'none');
      }
      else
      	   element.style.display = (element.style.display == 'none' ? '' : 'none');
    }
  },
  allselect:function(){
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].checked= true;
      }
      else
      	   element.checked =true;
    }  	
  },
  unallselect:function(){
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].checked = false;
      }
      else
      	   element.checked= false;
    }  
  },
  readonly:function(){
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].disabled =true;
      }
      else      
           element.disabled =true;
    }  
  },
  hide: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].style.display ='none';
      }
      else      
           element.style.display = 'none';
    }
  },

  show: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].style.display ='';
      }
      else      
           element.style.display = '';
    }
  },

  remove: function(element) {
       element = $(element);
      if(element instanceof Array){
        for(var j=0;j<element.length;j++)
           element[j].parentNode.removeChild(element[j]);
      }
      else    
    	   element.parentNode.removeChild(element);
  },
   
  getHeight: function(element) {
    element = $(element);
    if(element instanceof Array)  
       return element[0].offsetHeight
    return element.offsetHeight; 
  },

  hasClassName: function(element, className) {
    element = $(element);
    if (!element)
      return;
    var a = element.className.split(' ');
    for (var i = 0; i < a.length; i++) {
      if (a[i] == className)
        return true;
    }
    return false;
  },

  addClassName: function(element, className) {
    element = $(element);
    Element.removeClassName(element, className);
    element.className += ' ' + className;
  },

  removeClassName: function(element, className) {
    element = $(element);
    if (!element)
      return;
    var newClassName = '';
    var a = element.className.split(' ');
    for (var i = 0; i < a.length; i++) {
      if (a[i] != className) {
        if (i > 0)
          newClassName += ' ';
        newClassName += a[i];
      }
    }
    element.className = newClassName;
  },
  
  // removes whitespace-only text node children
  cleanWhitespace: function(element) {
    var element = $(element);
    for (var i = 0; i < element.childNodes.length; i++) {
      var node = element.childNodes[i];
      if (node.nodeType == 3 && !/\S/.test(node.nodeValue)) 
        Element.remove(node);
    }
  }
});

var Toggle = new Object();
Toggle.display = Element.toggle;
//---------------------------------
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
/**
 * 格式化日期格式
 * date:日期类型
 * format 日期格式 人事异动专用格式
 */
function formatTemplateDate(date, format) {
     format = format + "";
    var yy = date.getFullYear() + "";//获得完整年份，1988  
    var mm = date.getMonth() + 1;
    var dd = date.getDate();
   // var dd = date.getDay();//星期
    var hh = date.getHours();
    var mi = date.getMinutes();
    var ss = date.getSeconds();
    var strResult="";
   /* if (yy<100){//1900到2000年
        yy=1900+parseInt(yy);
    }*/
    var disformat= parseInt(format);
    switch (disformat) {
        case 0: // 1991.12.3  注意：因为这种格式是默认格式 都是按1991.12.03显示。
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
        case 1: // 91.12.3
            strResult=yy.substring(2);
            strResult=strResult+"."+getTwoDigitNumber(mm)+"."+dd;
            break;
        case 2:// 1991.2
            strResult=yy+"."+mm;
            break;
        case 3:// 1992.02
            strResult= yy+"."+getTwoDigitNumber(mm);
            break;
        case 4:// 92.2
            strResult=yy.substring(2);
            strResult=strResult+"."+mm;
            break;
        case 5:// 98.02
            strResult=yy.substring(2);
            strResult=strResult+"."+getTwoDigitNumber(mm);
            break;
        case 6:// 一九九一年一月二日
            strResult= exchangYearToCn(yy)+"年"+exchangMonthToCn(mm)+"月"+exchangDayToCn(dd)+"日";
            break;
        case 7:// 一九九一年一月
            strResult= exchangYearToCn(yy)+"年"+exchangMonthToCn(mm)+"月";
            break;
        case 8:// 1991年1月2日
            strResult= yy+"年"+mm+"月"+dd+"日";
            break;
        case 9:// 1991年1月
            strResult= yy+"年"+mm+"月";
            break;
        case 10:// 91年1月2日
            strResult=yy.substring(2);
            strResult=strResult+"年"+mm+"月"+dd+"日";
            break;
        case 11:// 91年1月
            strResult=yy.substring(2);
            strResult=strResult+"年"+mm+"月";
            break;
        case 12:// 年龄
        	var date=new Date();
        	var year=date.getFullYear();
        	var month=date.getMonth()+1;
        	var day=date.getDate();
        	var result=year-parseInt(yy,10);
        	if(month<parseInt(mm,10))
        	{
        		result=result-1>0?result-1:0;
        	}
        	else
        	{
        		if(month==parseInt(mm,10))
        		{
        			if(day<parseInt(dd,10))
        			{
        				result=result-1>0?result-1:0;
        			}
        		}
        	}
        	strResult=result;
            break;
        case 13:// 1991（年）
            strResult=yy+"";
            break;
        case 14:// 1 （月）
            strResult=mm+"";
            break;
        case 15:// 23 （日）
            strResult=dd+"";
            break;
        case 16:// 1999年02月
            strResult= yy+"年"+getTwoDigitNumber(mm)+"月";
            break;
        case 17:// 1999年02月03日
            strResult= yy+"年"+getTwoDigitNumber(mm)+"月"+getTwoDigitNumber(dd)+"日";
            break;
        case 18:// 1992.02.01
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
        default:
            strResult= yy+"."+getTwoDigitNumber(mm)+"."+getTwoDigitNumber(dd);
            break;
    }
  return strResult;
}

/**
 * 汉化数字转化到阿拉伯数字
 * @param {} cnNum
 * @return {}
 */
function exchangCnToNum(cnNum){
  var strResult="";
  var value = cnNum+"";
  var strLength =  value.length;//汉字‘十’是分割点，长度是1时，’十‘是‘10’，长度是2，‘十一’是‘11’
  for (var i = 0; i < value.length; i++) {
      switch (value.charAt(i)) {
      case '〇':
          strResult=strResult+"0";
          break;
      case '一':
          strResult=strResult+"1";
          break;
      case '二':
          strResult=strResult+"2";
          break;
      case '三':
          strResult=strResult+"3";
          break;
      case '四':
          strResult=strResult+"4";
          break;
      case '五':
          strResult=strResult+"5";
          break;
      case '六':
          strResult=strResult+"6";
          break;
      case '七':
          strResult=strResult+"7";
          break;
      case '八':
          strResult=strResult+"8";
          break;
      case '九':
          strResult=strResult+"9";
          break;
    case '十':
    		if(strLength == 1)
          		strResult=strResult+"10";
      		else if(strLength == 2)
          		strResult=strResult+"1";
          break;
      case '零':
          strResult=strResult+"0";
          break;
      }
  }
  return strResult;
}

function exchangYearToCn(year){
  var strResult="";
  var value = year+"";
  for (var i = 0; i < value.length; i++) {
      switch (value.charAt(i)) {
      case '1':
          strResult=strResult+"一";
          break;
      case '2':
          strResult=strResult+"二";
          break;
      case '3':
          strResult=strResult+"三";
          break;
      case '4':
          strResult=strResult+"四";
          break;
      case '5':
          strResult=strResult+"五";
          break;
      case '6':
          strResult=strResult+"六";
          break;
      case '7':
          strResult=strResult+"七";
          break;
      case '8':
          strResult=strResult+"八";
          break;
      case '9':
          strResult=strResult+"九";
          break;
      case '0':
          strResult=strResult+"零";
          break;
      }
  }
  return strResult;
}


function exchangMonthToCn(month){
  var strResult="";
  switch (month) {
     case 1:
         strResult="一";
         break;
     case 2:
         strResult="二";
         break;
     case 3:
         strResult="三";
         break;
     case 4:
         strResult="四";
         break;
     case 5:
         strResult="五";
         break;
     case 6:
         strResult="六";
         break;
     case 7:
         strResult="七";
         break;
     case 8:
         strResult="八";
         break;
     case 9:
         strResult="九";
         break;
     case 10:
         strResult="十";
         break;
     case 11:
         strResult="十一";
         break;
     case 12:
         strResult="十二";
         break;
  }
  return strResult;
}

function exchangDayToCn(month){
  var strResult="";
  switch (month) {
     case 1:
         strResult="一";
         break;
     case 2:
         strResult="二";
         break;
     case 3:
         strResult="三";
         break;
     case 4:
         strResult="四";
         break;
     case 5:
         strResult="五";
         break;
     case 6:
         strResult="六";
         break;
     case 7:
         strResult="七";
         break;
     case 8:
         strResult="八";
         break;
     case 9:
         strResult="九";
         break;
     case 10:
         strResult="十";
         break;
     case 11:
         strResult="十一";
         break;
     case 12:
         strResult="十二";
         break;
     case 13:
         strResult="十三";
         break;
     case 14:
         strResult="十四";
         break;
     case 15:
         strResult="十五";
         break;
     case 16:
         strResult="十六";
         break;
     case 17:
         strResult="十七";
         break;
     case 18:
         strResult="十八";
         break;
     case 19:
         strResult="十九";
         break;
     case 20:
         strResult="二十";
         break;
     case 21:
         strResult="十一";
         break;
     case 22:
         strResult="十二";
         break; 
     case 23:
         strResult="二三";
         break;
     case 24:
         strResult="二四";
         break;
     case 25:
         strResult="二五";
         break;
     case 26:
         strResult="二六";
         break;
     case 27:
         strResult="二七";
         break;
     case 28:
         strResult="二八";
         break;
     case 29:
         strResult="二九";
         break;
     case 30:
         strResult="三十";
         break;
     case 31:
         strResult="三一";
         break;
  }
  return strResult;

}
/**
 * 解析日期
 */
function parseTemplateDate(str) {
    if (typeof str == 'string') {
        var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
        if (results && results.length > 3){
            return new Date(parseInt(results[1],10), parseInt(results[2],10) - 1, parseInt(results[3],10));
        }
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
        if (results && results.length > 6){
            return new Date(parseInt(results[1],10), parseInt(results[2],10) - 1, parseInt(results[3],10), 
                parseInt(results[4],10), parseInt(results[5],10), parseInt(results[6],10));
        }
       results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,9}) *$/);
        if (results && results.length > 7){
            return new Date(parseInt(results[1],10), parseInt(results[2],10) - 1, parseInt(results[3],10), 
            parseInt(results[4],10), parseInt(results[5],10), parseInt(results[6],10), 
            parseInt(results[7],10));
        }
        //匹配格式：2016-03-02 10:30
        results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2})\s+(\d{1,2}):(\d{1,2})*$/);
        var year = parseInt(results[1],10);
        var month = parseint(results[2],10)-1;
        var day = parseint(results[3],10);
        var hour = parseint(results[4],10);
        var min = parseint(results[5],10);
        var date = new Date(year,month,day,hour,min); 
        if (results && results.length > 5){
            return date;
        }
    }
    return null;
}

function getTwoDigitNumber(value) { 
   var strResult=value;
   if(value<10){
        strResult="0"+value
   }
   return strResult;
}   