var _fileIncluded_table=true;

function getRecordByCell(cell){
	return getRowByCell(cell).record;
}

function getTableRowByRecord(table, record){
	if (record){
		if (table._activeRow && table._activeRow.record==record) return table._activeRow;

		var row=table.rows[table.activeRowIndex+1];
		if (row && row.record==record) return row;

		var row=table.rows[table.activeRowIndex-1];
		if (row && row.record==record) return row;
	}

	for(var i=0; i<table.tBodies[0].rows.length; i++){
		var row=table.tBodies[0].rows[i];
		if (row.record==record) return row;
	}
}

function refreshTableRowData(row){
	for(var i=0; i<row.cells.length; i++){
		refreshElementValue(row.cells[i]);
	}
}

function getTableRowStyle(row){
	var table=getTableByRow(row);
	if (row.rowIndex % 2)
		return "row_odd";
	else
		return "row_even";
}

function refreshTableRowStyle(row){
	var table=getTableByRow(row);
	if (row==table.activeRow && isTrue(table.highlightSelection)){
		if (table.focused && !(_isDropDownPage || isTrue(table.isDropDownTable)))
			row.className="row_active";
		else
			row.className="row_selected";
	}
	else{
		row.className=getTableRowStyle(row);
	}
}

function refreshTableRowIndicate(row){
	var table=getTableByRow(row);
	if (!isTrue(table.showIndicator)) return;

	var cell=row.firstChild;
	if (table.activeRow==row){
		var record=row.record;
		if (record && (record.dataset.state=="insert" || record.dataset.state=="modify"))
			cell.innerHTML="<label style=\"font-family: Webdings; font-size: 7pt; color: red\"><</label>";
		else
			cell.innerHTML="<label style=\"font-family: Webdings; font-size: 7pt\">4</label>";
		cell.className="";
	}
	else{
		cell.innerHTML="";
		cell.className="indicate";
	}
}



function resetDataTableStyle(table, startIndex){
	var row;
	var maxIndex=checkTableCellIndex(table, 9999, 9999);
	for(var i=startIndex; i<=maxIndex[0]; i++){
		row=table.rows[i];
		refreshTableRowStyle(row);
	}
}


function datatable_getColWidth(table,col)
{
	if(!(table.tHead && table.tHead.rows[0]))
		return;
	if(!(table.tHead.rows[0].cells[col]))
		return 40;
	var col_width=table.tHead.rows[0].cells[col].offsetWidth;
	return col_width;
}
/**太慢*/
function datatable_lockColumn(table,col)
{
	var i=0,j=0;
	if(table._lockColumnIndex!=-1)
	{

		var rowh=table.tHead.rows[0];				
		for(j=0;j<table._lockColumnIndex;j++)
		{
			var cell=rowh.cells[j];
			cell.setAttribute("className","tablecell");
		}			

		for(var i=0; i<table.tBodies[0].rows.length; i++){
			var row=table.tBodies[0].rows[i];
			for(j=0;j<table._lockColumnIndex;j++)
			{
				var cell=row.cells[j];
				cell.setAttribute("className","tablecell");
			}
		}		
	}
	

	var rowh=table.tHead.rows[0];				
	for(j=0;j<col;j++)
	{
		var cell=rowh.cells[j];
		//cell.setAttribute("style","left:expression(document.getElementById('setpnl').scrollLeft);position:relative;z-index:10");
		cell.setAttribute("className","column_locked");
		//cell.style.left=expression(document.getElementById("setpnl")).scrollLeft;
	}			

	for(var i=0; i<table.tBodies[0].rows.length; i++){
		var row=table.tBodies[0].rows[i];
		for(j=0;j<col;j++)
		{
			var cell=row.cells[j];
			//cell.style.left=expression(document.getElementById("setpnl")).scrollLeft;
			//cell.setAttribute("style","left:expression(document.getElementById('setpnl').scrollLeft);position:relative;z-index:10");
			cell.setAttribute("className","column_locked");
		}
	}
	table._lockColumnIndex=col;
}

function datatable_resetColumnTitle(table,col,label)
{
	if(!(table.tHead && table.tHead.rows[0]))
		return;
	table.tHead.rows[0].cells[col].innerHTML=label;
}

function datatable_getActiveRow() {
	return this.activeRow;
}

function datatable_getActiveRowIndex() {
	return this.activeRowIndex;
}

function datatable_getActiveCell() {
	return this.activeCell;
}

function datatable_getActiveCellIndex() {
	return this.activeCellIndex;
}

function datatable_isReadOnly() {
	return this.readOnly;
}

function datatable_setReadOnly(readOnly) {
	this.readOnly=readOnly;
}

function datatable_isEditable() {
	return this.editable;
}

function datatable_getMaxRow() {
	return this.maxRow;
}

function datatable_getDataset() {
	return this.dataset;
}

function datatable_getEditor() {
	return this.editor;
}

function cell_getField() {
	return this.field;
}

function initDataTable(table, resetColumns){

	function setElementAttribute(element, attr, value){
		if (getValidStr(element.getAttribute(attr))=="") element.setAttribute(attr, value);
	}

	table.refreshData=datatable_refreshData;
	table.isRecordSelected=datatable_isRecordSelected;
	table.selectRecord=datatable_selectRecord;
	table.unselectRecord=datatable_unselectRecord;
	table.resetColumnTitle=datatable_resetColumnTitle;
	table.getColWidth=datatable_getColWidth;
	table.lockColumn=datatable_lockColumn;
	table.activeRow=null;
	table.activeRowIndex=null;
	table._activeCellIndex=null;
	table.activeCellIndex=null;
	table._activeCell=null;
	table._activeCellIndex=null;
	table._lockColumnIndex=-1;
	
	table.getActiveRow=datatable_getActiveRow;
	table.getActiveRowIndex=datatable_getActiveRowIndex;
	table.getActiveCell=datatable_getActiveCell;
	table.getActiveCellIndex=datatable_getActiveCellIndex;
	table.isReadOnly=datatable_isReadOnly;
	table.setReadOnly=datatable_setReadOnly;
	table.isEditable=datatable_isEditable;
	table.getMaxRow=datatable_getMaxRow;
	table.getDataset=datatable_getDataset;
	table.getEditor=datatable_getEditor;
	
	if (_isDropDownPage || isTrue(table.isDropDownTable)) table.onclick=_dropdown_onclick;

	var dataset=getElementDataset(table);
	//var oCaption = table.createCaption()
	//oCaption.innerText="   共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+"   ";
	//oCaption.setAttribute("vAlign","bottom");
	//oCaption.setAttribute("align","left");

	if (resetColumns && dataset){
		var arrayField, arrayLabel=new Array();
		var fields=table.fields;
		if (fields){
			arrayField=fields.split(",");
			for(var i=0; i<arrayField.length; i++){
				index=arrayField[i].indexOf("=");
				if (index>=0){
					arrayLabel[i]=arrayField[i].substr(index+1);
					arrayField[i]=arrayField[i].substr(0, index);
				}
			}
		}
		else{
			arrayField=new Array();
			for(var i=0; i<dataset.fields.fieldCount; i++){
				arrayField[i]=dataset.fields[i].name;
			}
		}

		for (var i=table.children.length-1; i>=0; i--) table.children[i].removeNode(true);
		table.appendChild(document.createElement("<tbody></tbody>"));

		var row, cell;
		if (isTrue(table.showHeader)){
			row=table.createTHead().insertRow();
			for(var i=0; i<arrayField.length; i++){
				cell=row.insertCell();
				cell.name=arrayField[i];
				cell.field=arrayField[i];
				if (arrayLabel[i]) cell.label=arrayLabel[i];
			}
		}

		row=table.tBodies[0].insertRow();

		for(var i=0; i<arrayField.length; i++){
			cell=row.insertCell();
			cell.name=arrayField[i];
			cell.field=arrayField[i];
			cell.getField=cell_getField;
		}

		if (isTrue(table.showFooter)){
			row=table.createTFoot().insertRow();
			for(var i=0; i<arrayField.length; i++){
				cell=row.insertCell();
				cell.name=arrayField[i];
				cell.field=arrayField[i];
				if (arrayLabel[i]) cell.label=arrayLabel[i];
			}
		}
		delete arrayField;
	}

	var tHeadRow, tBodyRow, tFootRow;
	if (table.tHead && table.tHead.rows[0]) tHeadRow=table.tHead.rows[0];
	if (table.tFoot && table.tFoot.rows[0]) tFootRow=table.tFoot.rows[0];
	tBodyRow=table.tBodies[0].rows[0];

	if (tHeadRow) 
	{
		//tHeadRow.style.backgroundImage = "url("+_theme_root+"/hbutton.gif)";
		tHeadRow.height=25;
	}
	if (isTrue(table.showIndicator)) {
		table.minCellIndex=1;
		if (!tBodyRow.firstChild || (tBodyRow.firstChild && !tBodyRow.firstChild.isIndicate)){
			cell=tBodyRow.insertCell(0);
			cell.width="9px";
			cell.align="center";
			cell.isIndicate=true;
			cell.className="indicate";

			if (tHeadRow) {
				cell=tHeadRow.insertCell(0);
				cell.align="center";
			}

			if (tFootRow) {
				cell=tFootRow.insertCell(0);
				cell.align="center";
			}
		}
	}
	else{
		table.minCellIndex=0;
	}

	for(var i=table.minCellIndex; i<tBodyRow.children.length; i++) {
		var cell=tBodyRow.children[i];
		var name=cell.name;
		var dataField=cell.field;
		if (!dataField) dataField=name;
		cell.dataset=dataset;
		cell.field=dataField;

		var toolTip=cell.getAttribute("toolTip");
		cell.removeAttribute("toolTip");
		var field=null;
		if (dataset) field=dataset.getField(dataField);
		if (field && !toolTip) toolTip=field.toolTip;

		cell.id=table.id+"_"+dataField;

		cell.extra="tablecell";
		if (_isDropDownPage || isTrue(table.isDropDownTable)) cell.noWrap=true;

		if (name=="select"){
			cell.align="center";
			cell.vAlign="center";
			cell.innerHTML="<input type=checkbox onclick=\"return _table_checkbox_onclick();\" style=\"height:16\">";
			cell.readOnly=true;
		}
		else{
			if (field){
				if(field.dataType=="clob"){
					///setElementAttribute(cell, "width", 100);
				}
				setElementAttribute(cell, "readOnly", field.readOnly);
				setElementAttribute(cell, "dataType", field.dataType);
				setElementAttribute(cell, "align", field.align);
				setElementAttribute(cell, "vAlign", field.vAlign);
				setElementAttribute(cell, "codesetid", field.codesetid);
			}
			else{
				setElementAttribute(cell, "readOnly", true);
			}
		}

		if (getValidStr(cell.getAttribute("editorType"))==""){
			switch (cell.getAttribute("dataType")){
				case "date":;
				case "datetime":{
					cell.editorType="date";
					break;
				}
				case "boolean":{
					if (!cell.dropDown)	{
						cell.editorType="checkbox";
					}
					break;
				}
			}
		}
		fireUserEvent(getElementEventName(table, "onInitCell"), [table, cell, field]);
		initElement(cell);

		if (tHeadRow){
			var cell=tHeadRow.children[i];
			cell.id=table.id+"_header_"+dataField;
			cell.name=name;
			cell.field=dataField;			
			cell.extra="columnheader";
			cell.align="center";
			cell.getField=cell_getField;

			if (compareText(name, "select")){
				if (!cell.getAttribute("label")) cell.label="<font face=Marlett size=2>a</font>";
				cell.ondblclick=_table_select_onHeaderDblClick;
			}
			else{
				cell.title=toolTip;
			}			
			cell.dataset=dataset;
			initElement(cell);
		}

		if (tFootRow){
			var cell=tFootRow.children[i];
			cell.id=table.id+"_footer_"+dataField;
			cell.name=name;
			cell.field=dataField;
			cell.extra="columnfooter";
			cell.getField=cell_getField;

			if (compareText(name, "select")){
				if (!cell.getAttribute("label")) cell.label="<font face=Marlett size=2>a</font>";
				cell.align="center";		
			}
			else{
				if (field){
					setElementAttribute(cell, "align", field.align);
				}
				else {				
					cell.align="center";
				}
			}
			cell.dataset=dataset;
			initElement(cell);
		}
	}
	tBodyRow.extra="tablerow";
	table.repeatRow=tBodyRow.cloneNode(true);

	table.selectedRecords=new pArray();
	if (getInt(table.getAttribute("maxRow"))==0) table.maxRow=99999;
}

function resetDataTable(table){
	initDataTable(table, true);
	refreshTableData(table);
}

function _row_getRecord() {
	return this.record;
}

function refreshTableData(table, startRecord){
	var dataset=getElementDataset(table);
	if (!dataset) return;

	var count=0, maxRow=table.getAttribute("maxRow");;

	var _record=(startRecord)?startRecord:dataset.getFirstRecord();
	var count=0;
	while (_record && count<maxRow){
		if (count>(table.tBodies[0].rows.length-1)) insertTableRow(table, "end");
		row=table.tBodies[0].rows[count];
		refreshTableRowStyle(row);
		row.height=28;
		row.extra="tablerow";
		row.record=_record;

		for (var j=0; j<row.cells.length; j++){
			cell=row.cells[j];
			cell.vAlign="middle";
			refreshElementValue(cell);
		}
		count++;
		_record=_record.getNextRecord();
	}

	for(var i=table.tBodies[0].rows.length-1; i>=count; i--){
		var tmpRow=table.tBodies[0].rows[i];
		if (table.tBodies[0].rows.length!=1)
			deleteTableRow(tmpRow);
		else{
			tmpRow.record=null;
			for (var j=0; j<tmpRow.cells.length; j++){
				var cell=tmpRow.cells[j];
				if (cell.getAttribute("extra")=="tablecell") refreshElementValue(cell);
			}
		}
	}

	var row=getTableRowByRecord(table, dataset.record);
	if (row){
		setActiveTableRow(row);
	}
	else{
		setActiveTableRow(table.tBodies[0].rows[0]);
	}
	table.needRefresh=false;
	if(!isTrue(table.isDropDownTable))
	{
		//var oCaption = table.createCaption()
		//oCaption.innerHTML=" 共 "+dataset.getRowCount()+" 条 "+" 共 "+dataset.getPageCount()+" 页"+"   ";
	}

}


function datatable_refreshData(startRecord){
	refreshTableData(this, startRecord);
}

function getTableFirstRecord(table){
	if (table.tBodies[0].rows.length>0)
		return table.tBodies[0].rows[0].record;
	else
		return null;
}

function getTableLastRecord(table){
	var rowLength=table.tBodies[0].rows.length;
	if (rowLength>0)
		return table.tBodies[0].rows[rowLength-1].record;
	else
		return null;
}

function checkTableCellIndex(table, rowIndex, cellIndex){
	var r_rowIndex=rowIndex;
	var r_cellIndex=cellIndex;
	var minRowIndex=(table.tHead)?table.tHead.rows.length:0;
	minRowIndex=(minRowIndex<0)?0:minRowIndex;
	var maxRowIndex=(table.tBodies[0])?(minRowIndex+table.tBodies[0].rows.length-1):-1;
	var minCellIndex=table.minCellIndex;
	var maxCellIndex=table.tBodies[0].rows[0].cells.length-1;

	if ((!r_cellIndex)||(r_cellIndex<minCellIndex)) r_cellIndex=minCellIndex
	else if (r_cellIndex>maxCellIndex) r_cellIndex=maxCellIndex;
	if ((!r_rowIndex)||(r_rowIndex<minRowIndex)) r_rowIndex=minRowIndex
	else if (r_rowIndex>maxRowIndex) r_rowIndex=maxRowIndex;

	return ([r_rowIndex, r_cellIndex]);
}

function setActiveTableRow(row){
	var table=getTableByRow(row);
	var oldrow=table.activeRow;

	table.activeRow=row;
	table.activeRowIndex=row.rowIndex;

	if (oldrow){
		refreshTableRowStyle(oldrow);
		refreshTableRowIndicate(oldrow);
	}
	refreshTableRowStyle(row);
	refreshTableRowIndicate(row);

	var cellIndex=table._activeCellIndex;
	if (!cellIndex) cellIndex=table.activeCellIndex;

	setActiveTableCell(row, cellIndex);
	/**chenmengqing added 20080423*/
	var eventName=table.id+"_onRowClick";
	fireUserEvent(eventName, [table]);	
	/**end.*/
	table._activeCell=null;
	table._activeCellIndex=null;
}

function setActiveTableCell(row, cellIndex){
	var table=getTableByRow(row);
	var index=checkTableCellIndex(table, row.rowIndex, cellIndex);
	cell=row.cells[index[1]];
	var oldcell=table.activeCell;

	if (oldcell!=cell && table.focused){
		if (_activeEditor && _activeEditor.getAttribute("extra")=="dockeditor"){
			hideDockEditor(_activeEditor);
		}
	}


	var table_holder=table.parentElement;
	if (table_holder.scrollWidth>table_holder.offsetWidth || table_holder.scrollHeight>table_holder.offsetHeight){
		var pos=getAbsPosition(cell, table_holder);

		if (pos[0]<table_holder.scrollLeft)
			table_holder.scrollLeft=pos[0];
		else if ((pos[0]+cell.offsetWidth)>(table_holder.scrollLeft+table_holder.offsetWidth))
			table_holder.scrollLeft=pos[0]+cell.offsetWidth-table_holder.offsetWidth;

		if (pos[1]<table_holder.scrollTop)
			table_holder.scrollTop=pos[1];
		else if ((pos[1]+cell.offsetHeight)>(table_holder.scrollTop+table_holder.offsetHeight))
			table_holder.scrollTop=pos[1]+cell.offsetHeight-table_holder.offsetHeight;
	}

	if (table.focused){

		if (!isTrue(table.getAttribute("readOnly"))&& (!isRowReadOnly(table)) && isTrue(table.getAttribute("editable")) && cell.getAttribute("field")){
			_stored_element=cell;
			setTimeout("showDockEditor(_stored_element);", 0);
		}
		
	}

	table.activeCell=cell;
	table.activeCellIndex=cell.cellIndex;
	return true;
}
/*
chenmengqing added at 20071027 for row readonly state
*/
function isRowReadOnly(table)
{
	var dataset=table.getDataset();
	if(!dataset.rowLock)
		return false;
    var record=dataset.getCurrent();
    if(!record)
      return false;
    var bflag=true;
    /**查找审批状态字段*/
    var field=dataset.getField(/*"sp_flag"*/dataset.rowLockField);
    if(!field)
    	return false;
    var value=record.getValue(/*"sp_flag"*/dataset.rowLockField);
    var idx=dataset.rowLockValues.indexOf(","+value+",");
    //if(value=="07"||value=="01")
    if(idx!=-1)
       bflag=false;
    return bflag;
}

function deleteTableRow(row) {
	var table=getTableByRow(row);
	with (table){
		if (table.activeRow==row){
			setAttribute("activeRow", null);
			setAttribute("activeCell", null);
		}
		var rowIndex=row.rowIndex;
		row.removeNode(true);
		if (!_document_loading) resetDataTableStyle(table, rowIndex);
	}
}

function insertTableRow(table, mode, row, empty) {
	if (!row) row=table.tBodies[0].rows[0];

	var newRow=table.repeatRow.cloneNode(!empty);
	switch (mode){
		case "begin":{
			table.tBodies[0].insertAdjacentElement("afterBegin", newRow);
			break;
		}
		case "before":{
			row.insertAdjacentElement("beforeBegin", newRow);
			break;
		}
		case "after":{
			row.insertAdjacentElement("afterEnd", newRow);
			break;
		}
		default:{
			table.tBodies[0].insertAdjacentElement("beforeEnd", newRow);
			break;
		}
	}

	for (var i=0; i<row.cells.length; i++){
		var cell=row.cells[i];
		cell.getEditorType=_cell_getEditorType;
		cell.getDropDown=_cell_getDropDown;
	}
	if (!_document_loading) resetDataTableStyle(table, newRow.rowIndex);
	return newRow;
}

function _cell_getEditorType() {
	return this.editorType;
}

function _cell_getDropDown() {
	return this.dropDown;
}

function refreshTableRecord(row){
	refreshTableRowData(row);
}

function deleteTableRecord(row) {
	var table=getTableByRow(row);
	var editor=table.editor;
	if (editor) hideDockEditor(editor);

	if (table.tBodies[0].rows.length>1){
		var nextRow=table.tBodies[0].rows[row.rowIndex+1];
		deleteTableRow(row);
		if (nextRow) refreshTableRowData(nextRow);
	}
	else{
		row.record=null;
		for (var i=0; i<row.cells.length; i++){
			refreshElementValue(row.cells[i]);
		}
	}
}

function insertTableRecord(table, mode, row, record) {
	if (!row) row=table.tBodies[0].rows[0];

	var newRow;
	if (!row.getAttribute("record")){
		newRow=row;
	}
	else{
		newRow=insertTableRow(table, mode, row);
	}

	newRow.record=record;
	for (var i=0; i<newRow.cells.length; i++){
		refreshElementValue(newRow.cells[i]);
	}
	return newRow;
}

function _getCheckboxByRecord(table, record){
	var cells=document.body.all(table.id+"_select");
	if (cells){
		for (var i=0; i<cells.length; i++) {
			var row=getRowByCell(cells[i]);
			if (row.record==record){
				var checkbox=cells[i].firstChild;
				return checkbox;
			}
		}
	}
}

function isRecordSelected(table, record){
	return record.getValue("select");
}

function datatable_isRecordSelected(record){
	return isRecordSelected(this, record);
}

function selectRecord(table, record){
	if (record)	{
		record.setValue("select", true);
	}
}

function datatable_selectRecord(record){
	selectRecord(this, record);
}

function unselectRecord(table, record){
	if (record)	{
		record.setValue("select", false);
	}
}

function datatable_unselectRecord(record){
	unselectRecord(this, record);
}

function _table_head_onmouseover(){
	var cell=this;
	if (compareText(cell.name, "select") ||
		isTrue(cell.headerClickable) || isTrue(cell.sortable)){
		cell.style.backgroundImage = "url()";
	}
}

function _table_head_onmouseout(){
	var cell=this;
	if (compareText(cell.name, "select") ||
		isTrue(cell.headerClickable) || isTrue(cell.sortable)){
		//cell.style.backgroundImage = "url("+_theme_root+"/hbutton.gif)";
	}
}

function _table_head_onclick(){
	var cell=this;
	var table=getTableByCell(cell);

	if (compareText(cell.name, "select")) {
		_table_select_onHeaderClick(table, cell);
		return;
	}

	if (isTrue(cell.headerClickable) ){
		var field=getElementField(cell);
		var eventName=table.id+"_onHeaderClick";
		fireUserEvent(eventName, [table, cell, field]);
	}

	if (isTrue(cell.sortable)){
		var dataset=getElementDataset(table);
		if (dataset){
			var sortfield;
			if (!event.altKey){
				sortfield=cell.getAttribute("field");
				var ascend=true;
				if (compareText(dataset.sortFields.substr(0, sortfield.length), sortfield)){
					sortfield="-"+sortfield;
					ascend=false;
				}
				showStatusLabel(window, "<FONT face=Marlett><B>"+((ascend)?"5":"6")+"</B></FONT>", cell);
			}
			else{
				sortfield="";
				showStatusLabel(window, constCancelSort, cell);
			}

			_stored_element=dataset;
			setTimeout("_stored_element.sort(\""+sortfield+"\")", 100);
			setTimeout("hideStatusLabel(window)",  500);
		}
	}
}

function _table_checkbox_onclick(){
	var row=getRowByCell(event.srcElement.parentElement);
	var record=row.getAttribute("record");
	if (!record) event.returnValue=false;

	if (event.srcElement.checked)
		selectRecord(getTableByRow(row), record);
	else
		unselectRecord(getTableByRow(row), record);
}

function _table_select_onHeaderClick(table, cell) {
	var dataset=getElementDataset(table);
	if (!dataset) return;
	//dataset.disableControls();  chenmengqing added at 20080415 for tianhong
	try{
		var record=dataset.getFirstRecord();
		while (record){
			if (isRecordSelected(table, record)) {
				unselectRecord(table, record);
			}
			else{
				selectRecord(table, record);
			}
			record=record.getNextRecord();
		}
	}
	finally{
		//dataset.enableControls();
	}
}

function _table_select_onHeaderDblClick() {
	var cell=this;
	var table=getTableByCell(cell);
	
	var dataset=getElementDataset(table);
	if (!dataset) return;

	dataset.disableControls();
	try{
		var record=dataset.getFirstRecord();
		while (record){
			selectRecord(table, record);
			record=record.getNextRecord();
		}
	}
	finally{
		dataset.enableControls();
	}
}