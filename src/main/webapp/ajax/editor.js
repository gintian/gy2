
var _fileIncluded_editor = true;
var needUpdateEditor = true;
function _editor_getKeyValue() {
	return this.keyValue;
}
function _editor_getDataType() {
	return this.dataType;
}
function _editor_isReadOnly() {
	return isTrue(this._readOnly);
}
function _editor_getDropDown() {
	return this.dropDown;
}
function initEditor(editor) {
	editor.getKeyValue = _editor_getKeyValue;
	editor.getDataType = _editor_getDataType;
	editor.isReadOnly = _editor_isReadOnly;
	editor.getDropDown = _editor_getDropDown;
	initElementDataset(editor);
	with (editor) {
		if (tagName.toLowerCase() == "input" && compareText(type, "checkbox")) {
			style.borderColor = "window";
			onclick = _checkbox_onclick;
		}
		language = "javascript";
		onkeydown = _control_onkeydown;
		onkeypress = _editor_onkeypress;
		onpropertychange = _editor_onpropertychange;
	}
	if (editor.getAttribute("initValue")) {
		setElementValue(editor, getDecodeStr(editor.getAttribute("initValue")));
	}
	var field = getElementField(editor);
	editor.title = getDecodeStr(editor.getAttribute("title"));
	if (field && !editor.title) {
		editor.title = field.toolTip;
	}
	editor.setReadOnly = editor_setReadOnly;
}
function _editor_setReadOnly(editor, _readOnly) {
	with (editor) {
		if (_readOnly) {
			editor.readOnly = true;
			//陈总让改样式 暂时屏蔽 2015-09-30
			//style.color = "dimgray";
			//style.backgroundColor = "whitesmoke";
			style.border=0;
		} else {
			editor.readOnly = false;
			style.color = "black";
			style.backgroundColor = "";
		}
	}
}
function editor_setReadOnly(_readOnly) {
	_editor_setReadOnly(this, _readOnly);
}
function isDataEditorEditable(editor, dataset) {
	var editable = !isTrue(editor._readOnly);
	if (dataset) {
		var field = getElementField(editor);
		editable = editable && isFieldEditable(dataset, field);
	}
	return editable;
}
/*??????????,??????????????*/
function setDmValue(record, _editor) {
	var dropDownId = _editor.getAttribute("dropDown");
	if (dropDownId == "dropdownCode") {
		return;
	}
	if (record == null) {
		return;
	}
	var codesetid = record.getValue("codesetid");
	var codeitemid = record.getValue("codeitemid");
	var codeitemdesc = record.getValue("codeitemdesc");
	if (codesetid == "0") {
		return;
	}
	if (dm["_" + codesetid + codeitemid] == null) {
		var dmitem = new Object();
		dmitem.id = codesetid;
		dmitem.value = codeitemid;
		dmitem.name = codeitemdesc;
		dm[dm.length] = dmitem;
		dm["_" + codesetid + codeitemid] = dm[dm.length - 1];
	}
}
function processDropDownSelected(editor, record) {
	var _dropdown = getEditorDropDown(editor);
	if (!_dropdown) {
		return;
	}
	var eventName = getElementEventName(_dropdown, "onSelect");
	needAbort = (isUserEventDefined(eventName) && !fireUserEvent(eventName, [_dropdown, record, editor]));
	if (needAbort) {
		return;
	}
	switch (_dropdown.type) {
	  case "date":
		if (record) {
			var value = record.getValue("value");
			switch (editor.dataType) {
			  case "simpledate"://xuj add 2010-9-3
				var itemlength = editor.itemlength;
				if (itemlength == 4) {
					value = formatDate(value, "yyyy");
				} else {
					if (itemlength == 7) {
						value = formatDate(value, "yyyy.MM");
					} else {
						value = formatDate(value, "yyyy.MM.dd");
					}
				}
				break;
			  case "date":
				value = formatDate(value, "yyyy-MM-dd");
				break;
			  case "time":
				value = formatDate(value, "hh:mm:ss");
				break;
			  case "datetime":
				value = formatDate(value, "yyyy-MM-dd hh:mm:ss");
				break;
			  default:
				value = formatDate(value, "yyyy-MM-dd");
				break;
			}
			setElementValue(editor, value);
		}
		break;
	  case "list": //chenmengqing added 20080204
		var value = record.getValue("value");
		value = trimStr(value);
		var label = record.getValue("label");
		editor.setAttribute("codevalue", label);
		var fieldname = editor.getField();
		if (fieldname) {
			setElementValue(editor, label);
		} else {
			setElementValue(editor, value);
		}
		break;
	  default:
		var fieldmaps = new Array();
		if (_dropdown.writeFields) {
			var readFields = _dropdown.readFields.split(",");
			var writeFields = _dropdown.writeFields.split(",");
			for (var i = 0; i < writeFields.length; i++) {
				fieldmaps[i] = new Object();
				fieldmaps[i].writeField = writeFields[i];
				if (readFields[i]) {
					fieldmaps[i].readField = readFields[i];
				} else {
					fieldmaps[i].readField = writeFields[i];
				}
			}
		}
		var dataField = editor.getAttribute("field");
		var dataset = getElementDataset(editor);
		/**??????????*/

			//setDmValue(record,editor);			
		if (dataset) {
			if (fieldmaps.length > 0) {
				for (var i = 0; i < fieldmaps.length; i++) {
					if (record) {
						dataset.setValue(fieldmaps[i].writeField, record.getValue(fieldmaps[i].readField));
					} else {
						dataset.setValue(fieldmaps[i].writeField, "");
					}
				}
			} else {
				if (record) {
					if (_dropdown.type == "list") {
						setElementValue(editor, record.getValue("value"));
					} else {
						if (dataField) {
							dataset.setValue(dataField, record.getValue(dataField));
						}
					}
				} else {
					if (_dropdown.type == "list") {
						setElementValue(editor, "");
					} else {
						if (dataField) {
							dataset.setValue(dataField, "");
						}
					}
				}
			}
		} else {
			if (record) {
				if (fieldmaps.length > 0) {
					editor.value = record.getValue(fieldmaps[0].readField);
				} else {
					if (_dropdown.type == "list") {
						setElementValue(editor, record.getValue("value"));
					} else {
						if (dataField) {
							editor.value = record.getValue(dataField);
						} else {
							editor.value = record.getValue(0);
						}
					}
				}
			} else {
				if (fieldmaps.length > 0) {
					editor.value = "";
				} else {
					if (_dropdown.type == "list") {
						setElementValue(editor, "");
					} else {
						editor.value = "";
					}
				}
			}
		}
	}
	editor.dropDownSelectedValue = editor.value;
}
function validEditorInput(editor) {
	var _dropdown = getEditorDropDown(editor);
	if (!editor.value || (_dropdown && _dropdown.type == "list" && isTrue(_dropdown.mapValue))) {
		return;
	}
	switch (editor.getAttribute("dataType")) {
	  case "byte":
	  case "short":
	  case "int":
	  case "long":
		if (isNaN(parseInt(editor.value))) {
			throw constErrTypeInt.replace("%s", editor.value);
		}
		break;
	  case "float":
	  case "double":
	  case "bigdecimal":
		if (isNaN(parseFloat(editor.value))) {
			throw constErrTypeNumber.replace("%s", editor.value);
		}
		break;
	  case "date":
	  case "datetime":
		var _date = new Date(editor.value.replace(/-/g, "/"));
		if (isNaN(_date)) {
			throw constErrTypeDate.replace("%s", editor.value);
		}
		break;
	  case "time":
		var _date = new Date("1900/01/01 " + editor.value);
		if (isNaN(_date)) {
			throw constErrTypeDate.replace("%s", editor.value);
		}
		break;
	}
}
function updateEditorInput(editor) {
	try {
		if (window.closed) {
			return;
		}
		editor.modified = (getElementValue(editor) != editor.oldValue);
		if (editor.modified) {
			validEditorInput(editor);
			var dataset = getElementDataset(editor);
			var editorValue = getElementValue(editor);
			var dataField = editor.getAttribute("field");
			var eventName = getElementEventName(editor, "onUpdate");
			var event_result = fireUserEvent(eventName, [editor]);
			if (event_result) {
				throw event_result;
			}
			var _dropdown = getEditorDropDown(editor);
			var _codesetid=editor.getAttribute("codesetid");
			var dataType = editor.getAttribute("dataType");
			var noAjaxTree=true;
			//人事异动模板子集列表涉及单位、部门、岗位代码型指标，需采用ajax树状代码选择框  dengc 2013-08-09 
			if(_dropdown&&_dropdown.type=="custom"&&editor.getAttribute("dropDown")=="dropdownCode"&&(_codesetid=='UN'||_codesetid=='UM'||_codesetid=='@K')&& !isTrue(_dropdown.fixed))
			{  
				editor.setAttribute("codevalue",editorValue );
				if(typeof (g_dm["_"+_codesetid+editorValue])=='undefined')
				{
					if(_codesetid=='UM'&&typeof (g_dm["_UN"+editorValue])!='undefined')
					{
						editor.setAttribute("value", g_dm["_UN"+editorValue].V);
						noAjaxTree=false;
					}	
					else
					{
						editor.setAttribute("value", "");
						editor.setAttribute("codevalue", "");
					}
				}
				else
				{
					editor.setAttribute("value", g_dm["_"+_codesetid+editorValue].V);  
					noAjaxTree=false;
				}
			}
			
			if (noAjaxTree&&_dropdown && editor.dropDownSelectedValue != editor.value && !isTrue(_dropdown.fixed)) {
				if (editor.value != "") {
					var notInList = false;
					switch (_dropdown.type) {
					  case "list":
						var items = getDropDownItems(_dropdown, editor);
						if (items) {
							notInList = (items.find(["value"], [editorValue]));
							if (notInList != null) {
								var value = notInList.getValue("value");
								var label = notInList.getValue("label");
								editor.setAttribute("codevalue", label);
							} else {
								editor.setAttribute("value", "");
								editor.setAttribute("codevalue", "");
							}
						}
						break;
					  case "dataset":
						var tmp_dataset = _dropdown.dataset;
						if (tmp_dataset) {
							if (typeof (tmp_dataset) == "string") {
								tmp_dataset = getDatasetByID(tmp_dataset);
							}
							if (tmp_dataset) {
								var dataField = _dropdown.dataField;
								if (!dataField) {
									dataField = editor.getAttribute("field");
								}
								if (dataField) {
									var record = tmp_dataset.find([dataField], [editor.value]);
									notInList = (record == null);
									if (!notInList) {
										processDropDownSelected(editor, record);
									}
								}
							}
						}
						break;
					}
					if (notInList && isTrue(_dropdown.checkInput)) {
						throw constErrOutOfDropDownList;
					}
				} else {
					switch (_dropdown.type) {
					  case "dynamic":
					  case "custom":
						processDropDownSelected(editor, null);
						break;
					}
				}
			}
			editor.dropDownSelectedValue = editor.value;
			if (dataset && dataset.record) {
				if(dataType==null||dataType!='clob')
					editorValue = trimStr(editorValue);
				else
				    editorValue = rtrimStr(editorValue);//大文本只去掉右边空格   hej 20170622
				if (dataset.window == window) {
					_record_setValue(dataset.record, dataField, editorValue);
				} else {
					dataset.window._record_setValue(dataset.record, dataField, editorValue);
				}
			}
		}
	}
	catch (e) {
		processException(e);
		refreshElementValue(editor);
		editor.focus();
		throw "abort";
	}
}
function processEditorValueChanged(editor) {
	var dataset = getElementDataset(editor);
	if (dataset) {
		var value = editor.value;
		if (!dataset.record && editor.value != "") {
			dataset.insertRecord("end");
			if (dataset.state == "insert") {
				editor.value = value;
				editor.oldValue = "";
			}
		}
		if (dataset.id && dataset.id.toLowerCase().indexOf("templet_") != -1) {
			var _dropdown = editor.getAttribute("dropDown");
			if (!_dropdown) {
				var field = getElementField(editor);
				if (field) {
					_dropdown = field.dropDown;
				}
			}
			if (_dropdown != "dropdownCode") {
				var codesetid = null;
				var fieldname = editor.getField();
				var valuexgq = editor.getAttribute("valuexgq");
				var xgqflag = "";
				var xgqoldvalueflag = "";
				var xgqcodeid = "";
				if (fieldname) {
					var dataset = editor.getDataset();
					var field = dataset.getField(fieldname);
					codesetid = field.getCodeSetId();
				} else {
					codesetid = editor.getAttribute("codesetid");
				}
				for (var i = 0; i < g_dm.length; i++) {
					var codeitem = g_dm[i].ID;
					nlen = g_dm[i].L * 2;
					if (codeitem.substring(0, 2) == codesetid) {
						if (codeitem.substring(2, codeitem.length) == value) {
							xgqcodeid = "1";
						}
						if ( g_dm[i].V == value) {
						if(codeitem.substring(2, codeitem.length) == valuexgq )
							xgqflag = "1";
							else
							xgqflag = "";
						}
						if (codeitem.substring(2, codeitem.length) == editor.oldValue && g_dm[i].V != value) {
							xgqoldvalueflag = "1";
						}
					}
				}
				if (editor.value != ""  && xgqflag == "" && codesetid != "0" && codesetid != "orgType") {
					editor.setAttribute("valuexgq", value);
					if (editor.oldValue == null || editor.oldValue == "" || xgqoldvalueflag == "1") {
						if (xgqcodeid == "") {
						//	//不明白这段程序意思，但造成多层级代码如果某代码名相同会要求重复选择的问题 dengcan 2012-11-29
						/*
							hideDropDownBox();
							editor.setAttribute("hidenxgq", "1");
							showDropDownBox(editor);	
						*/
						editor.setAttribute("hidenxgq", "");
						 hideDropDownBox();
						
						} else {
							editor.setAttribute("hidenxgq", "");
							hideDropDownBox();
						}
					} else {
						editor.setAttribute("hidenxgq", "");
						hideDropDownBox();
					}
				} else {
					//alert("DD"+editor.value+":"+valuexgq+"DD");
					editor.setAttribute("hiden", "");
					hideDropDownBox();
				}
			}
		}
	}
	editor.modified = (getElementValue(editor) != editor.oldValue);
	if (editor.dropDownVisible && _dropdown_window) {
		_dropdown_window.dropDownLocate();
	}
}
function _editor_onpropertychange(e) {
	 e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	if (e.propertyName == "value") {
		//xuj update 2013-7-23
		var editor=e.target?e.target:e.srcElement;
		//var editor = event.srcElement;
		if (_activeEditor == editor) {
			processEditorValueChanged(editor);
		}
	}
}
function _checkbox_onclick(e) {
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	processEditorValueChanged(e.target?e.target:e.srcElement);
}
function _editor_onkeypress(e) {
	//xuj update 2013-7-23
	e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	var button=e.target?e.target:e.srcElement;
	if (button.readOnly || !button.contentEditable) {
		e.returnValue = false;
		return;
	}
	var result = true;
	var key = window.event?e.keyCode:e.which;
	switch (button.getAttribute("dataType")) {
	  case "byte":
	  case "short":
	  case "int":
	  case "long":
		result = (isNumericLogic(key) && (key == 45 || (key >= 48 && key <= 57)));
		break;
	  case "float":
	  case "double":
	  case "bigdecimal":
		result = (isNumericLogic(key) && (key == 45 || key == 46 || (key >= 48 && key <= 57)));
			//alert(editor.value);
		break;
	  case "date":
	  case "time":
	  case "datetime":
		result = ((key == 47 || key == 58 || key == 32 || key == 45 || (key >= 48 && key <= 57)));
		break;
	}
	e.returnValue = result;
}
/**int float ,chenmengqing add*/
function isNumericLogic(keyCode) {
	if (keyCode == 46) {
		/*.*/
	}
	return true;
	if (typeof (editor) == "undefined") {
		return true;
	}
	var dataField = getElementField(editor);
	if (!dataField) {
		return true;
	}
	var datatype = editor.getAttribute("dataType");
	var len = dataField.length;
	var value = editor.value;
	var bflag = true;
	var element = document.selection;
	var selvalue = "";
	if (element != null) {
		var rge = element.createRange();
		if (rge != null) {
			selvalue = rge.text;
		}
	}
	switch (datatype) {
	  case "byte":
	  case "short":
	  case "int":
	  case "long":
		var slen = selvalue.length;
		if (value.length - slen > len - 1) {
			bflag = false;
		}
		break;
	  case "float":
	  case "double":
	  case "bigdecimal":
		var idx = value.indexOf(".");
		if (idx == -1) {
			var slen = selvalue.length;
			if (value.length - slen > len - 1) {
				bflag = false;
			}
		}
		break;
	}
	return bflag;
}
function getEditorDropDown(editor) {
	var _dropdown = editor.getAttribute("dropDown");
	if (!_dropdown) {
		var field = getElementField(editor);
		if (field) {
			_dropdown = field.dropDown;
		}
	}
	if (_dropdown) {
		eval("var _dropdown=" + _dropdown);
	}
	return _dropdown;
}
function sizeDockEditor(editor) {
	var _editor = (editor) ? editor : _activeEditor;
	if (!_editor) {
		return;
	}
	var holder = _editor.editorHolder;
	if (!holder) {
		return;
	}
	var pos = getAbsPosition(holder);
	with (_editor) {
		if (compareText(_editor.tagName, "textarea")) {
			style.posLeft = pos[0] - 1;
			style.posTop = pos[1] - 1;
			style.width = (holder.offsetWidth < 150) ? 150 : holder.offsetWidth + 1;
			style.height = (holder.offsetHeight < 150) ? 150 : holder.offsetHeight + 1;
		} else {
			if (!compareText(type, "checkbox")) {
				style.posLeft = pos[0] - 1;
				style.posTop = pos[1] - 1;
				style.width = holder.offsetWidth + 1;
				style.height = holder.offsetHeight + 1;
			} else {
				style.posLeft = pos[0];
				style.posTop = pos[1];
				style.width = holder.clientWidth;
				style.height = holder.clientHeight;
				if (offsetWidth > 18) {
					style.borderLeftWidth = (offsetWidth - 18) / 2;
					style.borderRightWidth = (offsetWidth - 18) / 2;
				}
			}
		}
	}
}
function showDockEditor(holder) {
	try {
		if (isTrue(holder.getAttribute("readOnly"))) {
			throw "abort";
		}
		var _dataset = getElementDataset(holder);
		if (!isDataEditorEditable(holder, _dataset)) {
			throw "abort";
		}
		editor = getDockEditor(holder);
		if (editor.editorHolder == holder) {
			return;
		}
		with (editor) {
			if (style.visiblity != "visible") {
				editor.editorHolder = holder;
				editor.dataType = holder.getAttribute("dataType");
				editor.editorType = holder.getAttribute("editorType");
				editor.field = holder.getAttribute("field");
				editor.dropDown = holder.getAttribute("dropDown");
				if (compareText(holder.tagName, "td")) {
					var table = getTableByCell(holder);
					if (table) {
						table.editor = editor;
					}
					editor.in_table = true;
				} else {
					editor.in_table = false;
				}
				setElementDataset(editor, _dataset);
				sizeDockEditor(editor);
				style.visibility = "visible";
			}
			editor.focus();
		}
	}
	catch (e) {
		processException(e);
	}
}
function hideDockEditor(editor) {
	if (editor.style.visibility == "visible") {
		_skip_activeChanged = true;
		editor.style.visibility = "hidden";
		setElementDataset(editor, null);
		var holder = editor.editorHolder;
		if (holder) {
			if (compareText(holder.tagName, "td")) {
				var table = getTableByCell(holder);
				if (table) {
					table.editor = null;
				}
			}
			editor.editorHolder = null;
		}
	}
}
function getDockEditor(holder) {
	var result = null;
	var editorType = holder.getAttribute("editorType");
	switch (editorType) {
	  case "textarea":
		if (typeof (_table_textarea) == "undefined") {
			result = document.createElement("<TEXTAREA id=_table_textarea extra=dockeditor tabindex=-1" + " style=\"position: absolute; visibility: hidden\"></TEXTAREA>");
			initElement(result);
			document.body.appendChild(result);
		} else {
			result = _table_textarea;
		}
		break;
	  case "checkbox":
		if (typeof (_table_checkbox) == "undefined") {
			result = document.createElement("<INPUT id=_table_checkbox type=checkbox hidefocus=false extra=dockeditor tabindex=-1" + " style=\"position: absolute; visibility: hidden; background-color: window;\">");
			initElement(result);
			document.body.appendChild(result);
		} else {
			result = _table_checkbox;
		}
		break;
	  default:
		if (typeof (_table_texteditor) == "undefined") {
			result = document.createElement("<INPUT id=_table_texteditor extra=dockeditor tabindex=-1" + " style=\"position: absolute; visibility: hidden\">");
			initElement(result);
			document.body.appendChild(result);
		} else {
			result = _table_texteditor;
		}
		break;
	}
	return result;
}
function setFocusTableCell(table, rowIndex, cellIndex) {
	_rowIndex = rowIndex;
	_cellIndex = cellIndex;
	if (_rowIndex == -1) {
		_rowIndex = table.activeRowIndex;
	}
	if (_cellIndex == -1) {
		_cellIndex = table.activeCellIndex;
	}
	var index = checkTableCellIndex(table, _rowIndex, _cellIndex);
	table.rows[index[0]].cells[index[1]].focus();
}
function isEmptyRow(row) {
	function getTableRowState(row) {
		var record = row.record;
		if (record) {
			return record.recordstate;
		} else {
			return "";
		}
	}
	return (getTableRowState(row) == "new" && !getTableRowModified(row));
}

