var _fileIncluded_dataset=true;
var _maxAutoGenID=0;
var dm=new Array();

/********************************
 *????????ID??
 ********************************/
function _getAutoGenID()
{
	_maxAutoGenID++;
	return "__"+_maxAutoGenID;
}

function _downloadData(dataset, pageIndex){
	try
	{
		if (pageIndex)
			pageIndex = getValidStr(pageIndex);
		else
			pageIndex = "1";
			
		var pageSize = dataset.pageSize;
		if (pageSize)
			pageSize = getValidStr(pageSize);
		else
			pageSize = "0";
			
		var xml = "";
		xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		xml += "<rpc>";
		xml += "<datasetId>" + dataset.id + "</datasetId>";
		xml += "<datasetType>" + dataset.type + "</datasetType>";
		xml += "<datasetSql>" + getEncodeStr(dataset.sql) + "</datasetSql>";  //add at 20060110
		xml += "<funcId>"+dataset.funcId+"</funcId>";						  //add at 20061117	
		xml += "<keys>"+dataset.keys+"</keys>";
		xml += "<pageIndex>" + pageIndex + "</pageIndex>";
		xml += "<pageSize>" + pageSize + "</pageSize>";
		xml += "<parameters>" + __populateRPCParamXML(dataset.parameters()) + "</parameters>";
		xml += "<viewProperties>" + __populateRPCViewPropertiesXML() + "</viewProperties>";
		xml += "</rpc>";
		var loadtype="";
		if(dataset.flushByTrans)
			loadtype="byTrans";
		else
			loadtype="loadData";
		//alert(loadtype);
		var result = __remoteCall(_application_root+dataset.loadDataAction, loadtype/*"loadData"*/, xml, /*_showDialogOnLoadingData*/false);

		if (result) 
		{		

			var XMLRoot=result.documentElement;	
			__parseRPCViewProperties(XMLRoot);
			__parseRPCOutJavaScript(XMLRoot);			
			if (isTrue(XMLRoot.getAttribute("succeed"))){
				dataset.pageIndex=getInt(XMLRoot.selectSingleNode("pageIndex").text);
				dataset.pageCount=getInt(XMLRoot.selectSingleNode("pageCount").text);
				if(XMLRoot.selectSingleNode("rowCount")!=null)
					dataset.rowCount=getInt(XMLRoot.selectSingleNode("rowCount").text);
				return XMLRoot.selectSingleNode("records");
				
			}
			else{
				var error_text=XMLRoot.selectSingleNode("message").text;
				throw constErrDownLoadFailed+"\n"+constErrDescription+":"+error_text;
			}			
			delete XMLDoc;
		}
		else {			
			delete XMLDoc;
			throw constErrDownLoadFailed;
		}
	}
	catch(e){
		processException(e);
	}
}

function getDatasetByID(ID){
	for(var i=0; i<_array_dataset.length; i++){
		if (_array_dataset[i].id==ID) return _array_dataset[i];
	}

	var result=null;
	eval("if (typeof("+ID+")!=\"undefined\") result="+ID+";");
	return result;
}

function setElementDataset(element, dataset){
	var _dataset;
	if (typeof(dataset)=="string"){
		_dataset=getDatasetByID(dataset);
	}
	else{
		_dataset=dataset;
	}
	var old_dataset=element.getAttribute("dataset");

	if (old_dataset){
		var array=old_dataset.editors;
		if (array) pArray_ex_delete(array, element);
	}

	if (_dataset){
		var array=_dataset.editors;
		if (!array){
			array=new pArray();
			_dataset.editors=array;
		}
		pArray_ex_insert(array, element);
	}
	element.dataset=_dataset;
}

function _dataset_getField(fields, name){
	var field=null;
	if (typeof(name)=="number"){
		field=fields[name];
	}
	else if (typeof(name)=="string"){
		var fieldIndex=fields["_index_"+name.toLowerCase()];
		if (!isNaN(fieldIndex)) field=fields[fieldIndex];
	}
	return field;
}

function dataset_getField(name){
	var dataset=this;
	return _dataset_getField(dataset.fields, name);
}

function _dataset_getFieldCount() {
	var dataset=this;
	return dataset.fields.count();
}

function appendFromXml(dataset, root, init)
{
	if (!root) return;

	var current;
	if (root) 
	{
		var recordNodes=root.childNodes;
		for (var i=0; i<recordNodes.length; i++) 
		{
			var recordNode=recordNodes.item(i);
			var isCurrent=isTrue(recordNode.getAttribute("isCurrent"));
			var state=recordNode.getAttribute("state");
			var newData=recordNode.selectSingleNode("new");

			var record=newData.text.split(",");
			
			record.recordState=state;
			if (state=="new") 
			{
				record.recordState="insert";
			}

			var oldData=recordNode.selectSingleNode("old");
			if (oldData != null) {
				record.oldValueSaved=true;
				oldData=oldData.text.split(",");
				var fieldCount=dataset.fields.fieldCount;
				for(var j=0; j<fieldCount-1; j++){

					record[fieldCount+j]=record[j];
					record[fieldCount*2+j]=oldData[j];
				}
			}

			pArray_insert(dataset, "end", null, record);
			if (init) 
			   initRecord(record, dataset);
			
			if (isCurrent) {
				current=record;
			}
		}
	}
	return current;
}

function createDataset(ID){
	var dataset=new pArray();

	dataset.fields=new Array();
	dataset.fields.count=_field_count;

	dataset._parameters=new ParameterSet();
	dataset.updateItems=new Array();
	dataset.fields.fieldCount=0;
	dataset.addField=dataset_addField;
	dataset.pageSize=9999;
	dataset.pageCount=1;
	dataset.pageIndex=1;
	dataset.autoLoadPage=false;
	dataset.disableControlCount=0;
	dataset.disableEventCount=0;
	dataset.rowLock=false;
	dataset.rowLockField="sp_flag";
	dataset.rowLockValues=",07,01,";
	dataset.keys="";
	
	dataset.formname="";
	dataset._saveOldValue=record_saveOldValue;
	dataset._getValue=record_getValue;
	dataset._getString=record_getString;
	dataset._setValue=record_setValue;
	dataset._getOldValue=record_getOldValue;
	dataset._setOldValue=record_setOldValue;
	dataset._getPrevRecord=record_getPrevRecord;
	dataset._getNextRecord=record_getNextRecord;

	dataset.getId=dataset_getId;
	dataset.isFirst=dataset_isFirst;
	dataset.isLast=dataset_isLast;
	dataset.isAutoLoadPage=dataset_isAutoLoadPage;
	dataset.getDetailDatasets=dataset_getDetailDatasets;
	dataset.getDisableControlCount=dataset_getDisableControlCount;
	dataset.getDisableEventCount=dataset_getDisableEventCount;
	dataset.getEditors=dataset_getEditors;
	dataset.getActiveEditor=dataset_getActiveEditor;
	dataset.fieldSet=dataset_fieldSet;
	dataset.isModified=dataset_isModified;
	dataset.getPageCount=dataset_getPageCount;	
	dataset.getPageSize=dataset_getPageSize;
	dataset.getPageIndex=dataset_getPageIndex;
	dataset.getRowCount=dataset_getRowCount;
	dataset.setRowCount=dataset_setRowCount;
	dataset.getCurrent=dataset_getCurrent;
	dataset.setCurrent=dataset_setCurrent;
	dataset.getState=dataset_getState;
	dataset.getMasterDataset=dataset_getMasterDataset;
	dataset.getTag=dataset_getTag;
	dataset.setTag=dataset_setTag;
	dataset.getWindow=dataset_getWindow;
	
	dataset.getField=dataset_getField;
	dataset.getFieldCount=_dataset_getFieldCount;
	dataset.getValue=dataset_getValue;
	dataset.getString=dataset_getString;
	dataset.setValue=dataset_setValue;
	dataset.getCurValue=dataset_getCurValue;
	dataset.setCurValue=dataset_setCurValue;
	dataset.getOldValue=dataset_getOldValue;
	dataset.setOldValue=dataset_setOldValue;
	dataset.disableControls=dataset_disableControls;
	dataset.enableControls=dataset_enableControls;
	dataset.disableEvents=dataset_disableEvents;
	dataset.enableEvents=dataset_enableEvents;
	dataset.refreshControls=dataset_refreshControls;
	dataset.setRecord=dataset_setRecord;
	dataset.setReadOnly=dataset_setReadOnly;
	dataset.setFieldReadOnly=dataset_setFieldReadOnly;
	dataset.getFirstRecord=dataset_getFirstRecord;
	dataset.getLastRecord=dataset_getLastRecord;
	dataset.move=dataset_move;
	dataset.movePrev=dataset_movePrev;
	dataset.moveNext=dataset_moveNext;
	dataset.moveFirst=dataset_moveFirst;
	dataset.moveLast=dataset_moveLast;
	dataset.find=dataset_find;
	dataset.locate=dataset_locate;
	dataset.postRecord=dataset_postRecord;
	dataset.cancelRecord=dataset_cancelRecord;
	dataset.insertRecord=dataset_insertRecord;
	dataset.deleteRecord=dataset_deleteRecord;
	dataset.copyRecord=dataset_copyRecord;
	dataset.loadPage=dataset_loadPage;
	dataset.loadDetail=dataset_loadDetail;
	dataset.isPageLoaded=dataset_isPageLoaded;
	dataset.moveToPage=dataset_moveToPage;
	dataset.setMasterDataset=dataset_setMasterDataset;
	dataset.flushData=dataset_flushData;
	dataset.clearData=dataset_clearData;
	dataset.sort=dataset_sort;
	dataset.parameters=dataset_parameters;
	

	if (ID){
		dataset.id=ID;
		_array_dataset[_array_dataset.length]=dataset;
	}
	return dataset;
}

function dataset_getId() {
	return this.id;
}

function dataset_isFirst(){	
	return this._bof;
}

function dataset_isLast(){	
	return this._eof;
}

function dataset_isAutoLoadPage() {
	return this.autoLoadPage;
}

function dataset_getDetailDatasets() {
	return this.detailDatasets;
}

function dataset_getDisableControlCount() {
	return this.disableControlCount;
}

function dataset_getDisableEventCount() {
	return this.disableEventCount;
}

function dataset_getEditors() {
	return this.editors;
}

function dataset_getActiveEditor() {
	return this.activeeditor;
}

function dataset_fieldSet() {
	return this.fields;
}

function dataset_isModified() {
	return this.modified;
}

function dataset_getPageCount() {
	return this.pageCount;
}

function dataset_getPageSize() {
	return this.pageSize;
}

function dataset_getRowCount() {
	return this.rowCount;
}

function dataset_setRowCount() {
	return this.rowCount;
}



function dataset_getPageIndex() {
	return this.pageIndex;
}

function dataset_getCurrent() {
	return this.record;
}

function dataset_setCurrent(record) {
	record.dataset.setRecord(record);
}

function dataset_getState() {
	return this.state;
}

function dataset_getMasterDataset() {
	return this.masterDataset;
}

function dataset_getLoadDataAction() {
	return this.loadDataAction;
}

function dataset_setLoadDataAction(loadDataAction) {
	this.loadDataAction=loadDataAction;
}
	
function dataset_getTag() {
	return this.tag;
}

function dataset_setTag(tag) {
	this.tag=tag;
}

function dataset_getWindow() {
	return this.window;
}

function dataset_parameters(){
	return this._parameters;
}

function dataset_addField(fieldName, dataType){
	var dataset=this;

	try{
		if (getValidStr(fieldName)=="")
			throw constErrEmptyFieldName;

		if (dataset.prepared)
			throw constErrAddField;

		var field=new Object;
		var i=dataset.fields.length;
		dataset.fields["_index_"+fieldName.toLowerCase()]=i;
		dataset.fields[i]=field;
		dataset.fields.fieldCount++;
		field.index=i;
		field.dataset=dataset;
		field.fields=dataset.fields;
		
		field.name=fieldName;
		field.label=fieldName;
		field.fieldName=fieldName;
		field.dataType=dataType;
		field.toolTip="";
		field.codesetid="0";
	    field.level=1;
		switch (dataType){
			case "string":{
				field.editorType="text";
				field.align="left";
				field.vAlign="top";
				break;
			}

			case "byte":;
			case "short":;
			case "int":;
			case "long":;
			case "float":;
			case "double":;
			case "bigdecimal":{
				field.editorType="text";
				field.align="right";
				field.vAlign="top";
				break;
			}

			case "boolean":{
				field.editorType="checkbox";
				field.align="middle";
				field.vAlign="middle";
				break;
			}

			case "date":;
			case "time":;
			case "datetime":{
				field.editorType="text";
				field.align="left";
				field.vAlign="top";
				break;
			}

			default:{
				field.editorType="text";
				field.align="left";
				field.vAlign="top";
				break;
			}
		}
		
		field.getName=_field_getName;
		field.getLabel=_field_getLabel;
		field.getDataType=_field_getDataType;
		field.getEditorType=_field_getEditorType;
		field.isReadOnly=_field_isReadOnly;
		field.setReadOnly=_field_setReadOnly;
		field.getDefaultValue=_field_getDefaultValue;
		field.setDefaultValue=_field_setDefaultValue;
		field.isRequired=_field_isRequired;
		field.setRequired=_field_setRequired;
		field.getFormat=_field_getFormat;
		field.setFormat=_field_setFormat;
		field.isValueProtected=_field_isValueProtected;
		field.setValueProtected=_field_setValueProtected;
		field.isVisible=_field_isVisible;
		field.setVisible=_field_setVisible;
		field.getDropDown=_field_getDropDown;
		field.getTag=_field_getTag;
		field.setTag=_field_setTag;
		
		field.getToolTip=_field_getToolTip;
		field.setToolTip=_field_setToolTip;
		field.getAlign=_field_getAlign;
		field.setAlign=_field_setAlign;
		field.getVAlign=_field_getVAlign;
		field.setVAlign=_field_setVAlign;
		/**??????????*/
		field.getCodeSetId=_field_getCodeSetId;
		field.setCodeSetId=_field_setCodeSetId;
		field.level=_field_getLevel;
		
		return field;
	}
	catch(e){
		processException(e);
	}
}

function _field_getName() {
	return this.name;
}

function _field_getLevel() {
	return this.level;
}

function _field_getLabel() {
	return this.label;
}

function _field_getDataType() {
	return this.dataType;
}

function _field_getEditorType() {
	return this.editorType;
}

function _field_isReadOnly() {
	return this.readOnly;
}

function _field_setReadOnly(readOnly) {
	var dataset = this.dataset;
	dataset.setFieldReadOnly(this.name, readOnly);
}

function _field_getDefaultValue() {
	return this.defauleValue;
}

function _field_setDefaultValue(defauleValue) {
	this.defauleValue=defauleValue;
}

function _field_isRequired() {
	return this.required;
}

function _field_setRequired(required) {
	this.required=required;
}

function _field_getFormat() {
	return this.format;
}

function _field_setFormat(format) {
	this.format=format;
}

function _field_isValueProtected() {
	return this.valueProtected;
}

function _field_setValueProtected(valueProtected) {
	this.valueProtected=valueProtected;
}

function _field_isVisible() {
	return this.visible;
}

function _field_setVisible(visible)
{
	this.visible=visible;
}

function _field_getDropDown() {
	return this.dropDown;
}

function _field_getTag() {
	return this.tag;
}

function _field_setTag(tag) {
	this.tag = tag;
}

function _field_getToolTip() {
	return this.toolTip;
}

function _field_setToolTip(toolTip) {
	this.toolTip = toolTip;
}

function _field_getAlign() {
	return this.align;
}

function _field_setAlign(align) {
	this.align = align;
}

function _field_getVAlign() {
	return this.valign;
}

function _field_setVAlign(valign) {
	this.valign = valign;
}

function _field_getCodeSetId() {
	return this.codesetid;
}

function _field_setCodeSetId(codesetid) {
	this.codesetid = codesetid;
}

function _addUpdateItem(dataset) {
	var item=new Object();
	dataset.updateItems[dataset.updateItems.length]=item;
	return item;
}

function initFieldArray(dataset, fields){
	var fieldCount=fields.fieldCount;
	fields.dataset=dataset;

	for(var i=0; i<fieldCount; i++){
		if (dataset.id){
			if (fields[i].id && typeof(_element_property)!="undefined"){
				var root=_element_property[fields[i].id];
				if (root){
					var property_count=root.length;
					for(var j=0; j<property_count; j++)
						eval("fields[i]."+root[j].property+"=getDecodeStr(root[j].value)");
				}
			}
		}

		fields[fieldCount+i]=new Object;
		fields[fieldCount+i].name="_cur_"+fields[i].name;
		fields[fieldCount+i].dataType=fields[i].dataType;
		fields["_index__cur_"+fields[i].name.toLowerCase()]=fieldCount+i;
		fields[fieldCount*2+i]=new Object;
		fields[fieldCount*2+i].name="_old_"+fields[i].name;
		fields[fieldCount*2+i].dataType=fields[i].dataType;
		fields["_index__old_"+fields[i].name.toLowerCase()]=fieldCount*2+i;

		fields[i].readOnly=isTrue(fields[i].readOnly);
		fireDatasetEvent(dataset, "onInitField", [dataset, fields[i]]);
	}
}

function _record_getDataset() {
	return this.dataset;
}

function _record_getPageIndex() {
	return this.pageIndex;
}

function _record_getState() {
	return this.recordState;
}

function _record_setState(state) {
	this.recordState = state;
}

function initRecord(record, dataset){
	record.dataset=dataset;
	record.fields=dataset.fields;
	record.pageIndex=dataset.pageIndex;
	record.visible=true;

	record.saveOldValue=dataset._saveOldValue;
	record.getValue=dataset._getValue;
	record.getString=dataset._getString;
	record.setValue=dataset._setValue;
	record.getCurValue=dataset._getCurValue;
	record.setCurValue=dataset._setCurValue;
	record.getOldValue=dataset._getOldValue;
	record.setOldValue=dataset._setOldValue;
	record.getPrevRecord=dataset._getPrevRecord;
	record.getNextRecord=dataset._getNextRecord;
	
	record.getDataset=_record_getDataset;
	record.getPageIndex=_record_getPageIndex;
	record.getState=_record_getState;
	record.setState=_record_setState;
	
	for(var j=0; j<record.length-1; j++){
		if (record[j]!="") {
			switch (dataset.getField(j).dataType){
				case "string":{
					record[j]=filter_ValidStr(record[j]);//getDecodeStr(record[j]);
					break;
				}

				case "byte":;
				case "short":;
				case "int":;
				case "long":{
					record[j]=getInt(record[j]);
					break;
				}

				case "float":;
				case "double":;
				case "bigdecimal":{
					record[j]=getFloat(record[j]);
					break;
				}

				case "boolean":{
					record[j]=isTrue(record[j]);
					break;
				}

				case "date":;
				case "time":;
				case "datetime":{
					record[j]=new Date(getInt(record[j]));
					break;
				}

				default:{
					record[j]=filter_ValidStr(record[j]);  //getDecodeStr(record[j]);
					break;
				}
			}
		}
	}
	if (!record.oldValueSaved) 
	   record.saveOldValue();
}

function initDataset(dataset)
{
	if(typeof(dataset.pageSize)!="undefined" && dataset.pageSize!=null && dataset.pageSize>500)
	{
		dataset.pageSize=500;
	}
		
	if (dataset.prepared) 
	   return;

	if (dataset.id && typeof(_element_property)!="undefined"){
		var root=_element_property[dataset.id];
		if (root){
			var property_count=root.length;
			for(var i=0; i<property_count; i++)
				eval("dataset."+root[i].property+"=getDecodeStr(root[i].value)");
		}
	}

	dataset.window=window;

	dataset._bof=true;
	dataset._eof=true;
	dataset.state="none";
	dataset.readOnly=isTrue(dataset.readOnly);
	dataset.sortFields="";
	dataset.loadedDetail=new Array();	
	dataset.loadedPage=new Array();
	if (dataset.pageIndex>0) dataset.loadedPage[dataset.pageIndex-1]=true;

	dataset.setReadOnly(isTrue(dataset.readOnly));
	initFieldArray(dataset, dataset.fields);

	eval("var isXmlExist=(typeof(__"+dataset.id+")==\"object\")");
	if (isXmlExist) {
		eval("var xmlIsland=__"+dataset.id);
		var current=appendFromXml(dataset, xmlIsland.documentElement, true);
		_xml_list[_xml_list.length]=xmlIsland;
	}
	dataset.prepared=true;

	if (current) {
		dataset.setRecord(current);
	}
	else 
	{
		if (dataset.pageIndex==1 || !dataset.autoLoadPage){
			dataset.moveFirst();
		}
		else {
			dataset.setRecord(dataset.getFirstRecord());
		}
	}
	
	fireDatasetEvent(dataset, "onCreate", [dataset]);

	if (!dataset.record) {
		if (dataset.insertOnEmpty && !dataset.readOnly) {
			dataset.insertRecord();
		}
	}
}

function isFieldEditable(dataset, field){
	if (field){
		var editable=!(dataset.readOnly || field.readOnly);
		if (dataset.record){
			var recordState=dataset.record.recordState;
			editable=(editable &&
				!((recordState=="none" || recordState=="modify") && field.valueProtected));
		}
	}
	else{
		var editable=true;
	}
	return editable;
}

function _dataset_setMasterDataset(dataset, masterDataset, masterKeyFields, detailKeyFields){
	if (dataset.masterDataset){
		var array=dataset.masterDataset.detailDatasets;
		if (array) pArray_ex_delete(array, dataset);
	}	

	if (typeof(masterDataset)=="string") masterDataset=getDatasetByID(masterDataset);
	dataset.masterDataset=masterDataset;
	if (masterDataset){
		var array=masterDataset.detailDatasets;
		if (!array){
			array=new pArray();
			masterDataset.detailDatasets=array;
		}
		pArray_ex_insert(array, dataset);
		
		dataset.references=new Array();
		var fields=masterKeyFields.split(",");
		for(var i=0; i<fields.length; i++){
			var field=masterDataset.getField(fields[i]);

			if (field){
				var reference=new Object();
				dataset.references[i]=reference;
				reference.masterField=field.name;
				reference.masterIndex=field.index;
			}
			else
				throw constErrCantFindMasterField.replace("%s", fields[i]);
		}

		var fields=detailKeyFields.split(",");
		for(var i=0; i<fields.length; i++){
			var field=dataset.getField(fields[i]);

			if (field){
				dataset.references[i].detailField=field.name;
				dataset.references[i].detailIndex=field.index;
			}
			else
				throw constErrCantFindDetailField.replace("%s", fields[i]);
		}
		delete fields;

		masterDataset.loadDetail();
	}
}

function dataset_setMasterDataset(masterDataset, masterKeyFields, detailKeyFields){
	var dataset=this;
	try{
		_dataset_setMasterDataset(dataset, masterDataset, masterKeyFields, detailKeyFields);
	}
	catch (e){
		processException(e);
	}
}

function _dataset_loadDetail(dataset){
	if (dataset.detailDatasets)
	{
		var unit=dataset.detailDatasets.firstUnit;
		while (unit && unit.data)
		{
			var detail_dataset=unit.data;
			if (dataset.record && dataset.record.recordState!="insert" &&
				dataset.record.recordState!="new"){
				try
				{
					validateDatasetCursor(detail_dataset);
					if (detail_dataset.loadAsNeeded && detail_dataset._bof && detail_dataset._eof) {
						var keycode_founded=false;

						if (dataset.record) 
						{
							var keycode="";
							for(var i=0; i<detail_dataset.references.length; i++){
								keycode+=dataset.record[detail_dataset.references[i].masterIndex];
							}


							for(var i=0; i<detail_dataset.loadedDetail.length; i++){
								if (detail_dataset.loadedDetail[i]==keycode){
									keycode_founded=true;
									break;
								}
							}
						}

						if (!keycode_founded){
							var dataset_inserted=false;
							var event_result=fireDatasetEvent(detail_dataset, "beforeLoadDetail", [detail_dataset, dataset]);
							if (event_result) throw event_result;

							if (detail_dataset.references.length>0) {
								var parameters = detail_dataset.parameters();
								for(var i=0; i<detail_dataset.references.length; i++){
									parameters.setValue(detail_dataset.references[i].detailField,
										dataset.getValue(detail_dataset.references[i].masterIndex));
								}

								var xmlNode=_downloadData(detail_dataset);
								if (xmlNode){
									appendFromXml(detail_dataset, xmlNode, true);
								}
								delete result;
							}

							detail_dataset.loadedDetail[detail_dataset.loadedDetail.length]=keycode;
						}
					}
				}
				catch (e){
					processException(e);
				}
			}


			detail_dataset.refreshControls();
			detail_dataset.moveFirst();
			unit=unit.nextUnit;
		}
	}
}

function dataset_loadDetail(){
	var dataset=this;
	try{
		_dataset_loadDetail(dataset);
	}
	catch (e){
		processException(e);
	}
}

function dataset_isPageLoaded(pageIndex){
	var dataset=this;
	return dataset.loadedPage[pageIndex-1];
}


function _dataset_loadPage(dataset, pageIndex){
	if (!dataset.autoLoadPage || pageIndex<1 || pageIndex>dataset.pageCount || dataset.isPageLoaded(pageIndex)) return;
	if (dataset.masterDataset) throw constErrLoadPageOnDetailDataset;
	if (dataset.sortFields) throw constErrLoadPageAfterSort;

	var xmlNode=_downloadData(dataset, pageIndex);
	if (xmlNode){
		var tmpArray=new pArray();
		tmpArray.fields=dataset.fields;

		appendFromXml(tmpArray, xmlNode);

		var record=tmpArray.lastUnit;
		while (record){
			initRecord(record, dataset);
			record.pageIndex=pageIndex;
			record=record.prevUnit;
		}

		var inserted=false;
		var record=dataset.lastUnit;
		while (record){
			if (record.pageIndex<pageIndex){
				pArray_insertArray(dataset, "after", record, tmpArray);
				inserted=true;
				break;
			}
			record=record.prevUnit;
		}
		if (!inserted) pArray_insertArray(dataset, "begin", null, tmpArray);
		delete tmpArray;

		dataset.loadedPage[pageIndex-1]=true;
		dataset.refreshControls();
	}
}

function dataset_loadPage(pageIndex){
	try{
		var dataset=this;
		_dataset_loadPage(dataset, pageIndex);
	}
	catch (e){
		processException(e);
	}
}

function _dataset_clearData(dataset){
	dataset.disableControls();
	try{
		if (dataset.loadedDetail) delete dataset.loadedDetail;
		if (dataset.loadedPage) delete dataset.loadedPage;		
		dataset.loadedDetail=new Array();
		dataset.loadedPage=new Array();
		if (dataset.pageIndex>0) dataset.loadedPage[dataset.pageIndex-1]=true;
		pArray_clear(dataset);
		dataset.setRecord(null);
	}
	finally{
		dataset.enableControls();
		dataset.refreshControls();
	}
}

function dataset_clearData(){
	try{
		var dataset=this;
		_dataset_clearData(dataset);
	}
	catch (e){
		processException(e);
	}
}

function freeDataset(dataset){
	if (dataset.detailDatasets) pArray_clear(dataset.detailDatasets);
	if (dataset.editors) pArray_clear(dataset.editors);
	delete dataset.references;
	pArray_clear(dataset.fields);
	dataset.clearData();
	delete dataset;
}

function _dataset_flushData(dataset, pageIndex){	
	var event_result=fireDatasetEvent(dataset, "beforeFlushData", [dataset]);
	if (event_result) throw event_result;

	dataset.disableControls();
	try{
		if (typeof(pageIndex)=="undefined") {
			pageIndex=dataset.pageIndex;
		}

		dataset.clearData();

		var xmlNode=_downloadData(dataset, pageIndex);

		if (xmlNode){
			appendFromXml(dataset, xmlNode, true);
		}

		delete result;
	}
	finally{
		dataset.setRecord(dataset.getFirstRecord());
		dataset.enableControls();
		dataset.refreshControls();
		dataset.loadDetail();

	}

	fireDatasetEvent(dataset, "afterFlushData", [dataset]);
}

function dataset_flushData(pageIndex){
	try{
		var dataset=this;
		_dataset_flushData(dataset, pageIndex);
	}
	catch (e){
		processException(e);
	}
}

function dataset_moveToPage(pageIndex){
	try{
		var dataset=this;
		if (!dataset.isPageLoaded(pageIndex)) _dataset_loadPage(dataset, pageIndex);

		var record=dataset.getFirstRecord();
		while (record){
			if (record.pageIndex>=pageIndex){
				_dataset_setRecord(dataset, record);
				break;
			}
			record=record.getNextRecord();
		}
	}
	catch (e){
		processException(e);
	}
}

function record_saveOldValue(){
	var record=this;

	var fieldCount=record.fields.fieldCount;
	for(var i=0; i<fieldCount; i++){
		record[fieldCount+i]=record[i];
		record[fieldCount*2+i]=record[i];
	}
}

function _dataset_sort(dataset, fields){

	function quickSort(_array, _fields, _low, _high){

		function compareRecord(record, _mid_data){
			if (_fields.length>0){
				var value1, value2;
				for (var i=0; i<_fields.length; i++){
					if (_field[i].ascend){
						value1=1;
						value2=-1;
					}
					else{
						value1=-1;
						value2=1;
					}

					if (record.getValue(_fields[i].index)>_mid_data[i]){
						return value1;
					}
					else if (record.getValue(_fields[i].index)<_mid_data[i]){
						return value2;
					}
				}
			}
			else{
				if (record.recordno>_mid_data[0]){
					return 1;
				}
				else if (record.recordno<_mid_data[0]){
					return -1;
				}
			}
			return 0;
		}

		var low=_low;
		var high=_high;
		var mid=getInt((low+high)/2);
		var mid_data=new Array();

		if (_fields.length>0){
			for (var i=0; i<_fields.length; i++)
				mid_data[i]=_array[mid].getValue(_fields[i].index);
		}
		else{
			mid_data[0]=_array[mid].recordno;
		}

		do {
			while (compareRecord(_array[low], mid_data)<0) low++;
			while (compareRecord(_array[high], mid_data)>0) high--;

			if (low<=high){
				var tmp=_array[low];
				_array[low]=_array[high];
				_array[high]=tmp;

				low++;
				high--;
			}
		}while (low<=high)

		if (high>_low) quickSort(_array, _fields, _low, high);
		if (_high>low) quickSort(_array, _fields, low, _high);
	}

	var _field=new Array();
	if (fields){
		var fields_array=fields.split(",");
		for (var i=0; i<fields_array.length; i++){
			_field[i]=new Object();
			_field[i].ascend=true;

			var firstchar=fields_array[i].substring(0, 1);
			var fieldName;
			if (firstchar=="+" || firstchar=="-"){
				if (firstchar=="-") _field[i].ascend=false;
				fieldName=fields_array[i].substring(1, fields_array[i].length);
			}
			else{
				fieldName=fields_array[i];
			}

			for (var j=0; j<dataset.fields.fieldCount; j++){
				if (compareText(fieldName, dataset.fields[j].name)){
					_field[i].index=j;
					break;
				}
			}
		}
	}

	function customSort(_array, _low, _high){

		function compareRecord(record1, record2){
			var event_name=getElementEventName(dataset, "onCompareRecord");
			if (isUserEventDefined(event_name)){
				return fireUserEvent(event_name, [record1.dataset, record1, record2]);
			}
		}

		var low=_low;
		var high=_high;
		var mid_record=_array[getInt((low+high)/2)];

		do {
			while (compareRecord(_array[low], mid_record)<0) low++;
			while (compareRecord(_array[high], mid_record)>0) high--;

			if (low<=high){
				var tmp=_array[low];
				_array[low]=_array[high];
				_array[high]=tmp;

				low++;
				high--;
			}
		}while (low<=high)

		if (high>_low) customSort(_array, _low, high);
		if (_high>low) customSort(_array, low, _high);
	}

	var _field=new Array();
	if (fields){
		if (fields!="#custom"){
			var fields_array=fields.split(",");
			for (var i=0; i<fields_array.length; i++){
				_field[i]=new Object();
				_field[i].ascend=true;

				var firstchar=fields_array[i].substring(0, 1);
				var fieldName;
				if (firstchar=="+" || firstchar=="-"){
					if (firstchar=="-") _field[i].ascend=false;
					fieldName=fields_array[i].substring(1, fields_array[i].length);
				}
				else{
					fieldName=fields_array[i];
				}

				for (var j=0; j<dataset.fields.fieldCount; j++){
					if (compareText(fieldName, dataset.fields[j].name)){
						_field[i].index=j;
						break;
					}
				}
			}
		}
	}

	if (!dataset.firstUnit) return;

	var tmp_array=new Array();
	try{
		var record=dataset.firstUnit;
		var i=0;
		while (record){
			tmp_array[i++]=record;
			if (!dataset.sortFields) record.recordno=i;
			record=record.nextUnit;
		}

		dataset.sortFields=fields;
		if (fields!="#custom"){
			quickSort(tmp_array, _field, 0, tmp_array.length-1);
		}
		else{
			customSort(tmp_array, 0, tmp_array.length-1);
		}

		dataset.firstUnit=null;
		dataset.lastUnit=null;
		for (var i=0; i<tmp_array.length; i++){
			pArray_insert(dataset, "end", null, tmp_array[i]);
		}

		dataset.refreshControls();
	}
	finally{
		delete tmp_array;
		for (var i=0; i<_field.length; i++) delete _field[i];
		delete _field;
	}
}

function dataset_sort(fields){
	try{
		var dataset=this;
		_dataset_sort(dataset, fields);
	}
	catch (e){
		processException(e);
	}
}

function dataset_setReadOnly(readOnly){
	var dataset=this;
	dataset.readOnly=readOnly;

	_broadcastDatasetMsg(_notifyDatasetStateChanged, dataset);
}

function dataset_setFieldReadOnly(fieldName, readOnly){
	var dataset=this;
	var field=dataset.getField(fieldName);
	if (field){
		field.readOnly=readOnly;
		_broadcastFieldMsg(_notifyFieldStateChanged, dataset, dataset.record, field);
	}
	else
		throw constErrCantFindField.replace("%s", dataset.id+"."+fieldName);
}

function fireDatasetEvent(dataset, eventName, param){
	if (dataset.disableEventCount>0) return;
	var result;
	result=fireUserEvent(getElementEventName(dataset, eventName), param);
	return result;
}

function dataset_isRecordValid(record){
	if (!record)
		return false;
	else{
		var result=(record.recordState!="delete" && record.recordState!="discard" && record.visible);
		var dataset=record.dataset;
		var masterDataset=dataset.masterDataset;
		if (result){
			if (masterDataset){
				if (!masterDataset.record) return false;
				for(var i=0; i<dataset.references.length; i++){
					if (masterDataset.getCurValue(dataset.references[i].masterIndex)!=
						record[dataset.references[i].detailIndex]){
							result=false;
							break;
					}
				}
			}

			if (dataset.filtered && !(record==dataset.record && dataset.state!="none")){
				var event_name=getElementEventName(dataset, "onFilterRecord");
				if (isUserEventDefined(event_name)){
					if (!fireUserEvent(event_name, [dataset, record])) result=false;
				}
			}
		}
		return result;
	}
}

function dataset_setBofnEof(dataset, BofValue, EofValue){
	if (dataset._bof!=BofValue || dataset._eof!=EofValue){
		dataset._bof=BofValue;
		dataset._eof=EofValue;
		_broadcastDatasetMsg(_notifyDatasetStateChanged, dataset, dataset.record);
	}
}

function _do_dataset_setRecord(dataset, record){
	if (dataset.record!=record){
		if (dataset.record){
			_dataset_postRecord(dataset);
		}

		if (dataset.detailDatasets){
			var unit=dataset.detailDatasets.firstUnit;
			while (unit){
				var detailDataset=unit.data;
				_dataset_postRecord(detailDataset);
				unit=unit.nextUnit;
			}
		}

		var event_result=fireDatasetEvent(dataset, "beforeScroll", [dataset]);
		if (event_result) throw event_result;

		dataset.record=record;
		dataset.modified=false;

		if (dataset.disableControlCount<1){
			dataset.loadDetail();
		}

		fireDatasetEvent(dataset, "afterScroll", [dataset]);
		_broadcastDatasetMsg(_notifyDatasetStateChanged, dataset, record);
		_broadcastDatasetMsg(_notifyDatasetCursorChanged, dataset, record);
	}
}

function _dataset_setRecord(dataset, record){
	_do_dataset_setRecord(dataset, record);
	if (record){
		dataset_setBofnEof(dataset, false, false);
		dataset_setBofnEof(dataset, false, false);
	}
}

function dataset_setRecord(record){
	try{
		_dataset_setRecord(this, record);
	}
	catch(e){
		processException(e);
	}
}

function validateDatasetCursor(dataset){
	var down_found=false, up_found=false;
	var curRecord=(dataset.record)?dataset.record:dataset.firstUnit;

	var record=curRecord;
	while (record){
		if (dataset_isRecordValid(record)){
			_do_dataset_setRecord(dataset, record);
			up_found=true;
			break;
		}
		record=_record_getPrevRecord(record);
	}

	var record=curRecord;
	while (record){
		if (dataset_isRecordValid(record)){
			_do_dataset_setRecord(dataset, record);
			down_found=true;
			break;
		}
		record=_record_getNextRecord(record);
	}

	if (!up_found && !down_found)
		_do_dataset_setRecord(dataset, null);

	dataset_setBofnEof(dataset, (!up_found), (!down_found));
}

function dataset_setState(dataset, state){
	dataset.state=state;

	_broadcastDatasetMsg(_notifyDatasetStateChanged, dataset, dataset.record);
	fireDatasetEvent(dataset, "onStateChanged", [dataset]);
}

function _field_count() {
	return this.fieldCount;
}

function _record_getValue(record, fieldName){
	var dataset=record.dataset;
	var fields=record.fields;
	var fieldIndex=-1;
	var value;

	if (typeof(fieldName)=="number"){
		fieldIndex=fieldName;
	}
	else if (typeof(fieldName)=="string"){
		fieldIndex=fields["_index_"+fieldName.toLowerCase()];
	}

	var field=fields[fieldIndex];
	if (typeof(field)=="undefined"){
		throw constErrCantFindField.replace("%s", record.dataset.id+"."+fieldName);
	}

	value=record[fieldIndex];
	if (typeof(value)=="undefined" || value==null || (typeof(value)=="number" && isNaN(value))) {
		value="";
	}

	var eventName=getElementEventName(dataset, "onGetValue");
	if (isUserEventDefined(eventName)){
			value=fireUserEvent(eventName, [dataset, field, value]);
	}

	return value;
}

function record_getValue(fieldName){
	try{
		return _record_getValue(this, fieldName);
	}
	catch(e){
		processException(e);
	}
}
/**????????????????????????,????*/
function record_getString(fieldName){
	var record=this, field, value="";
	var field=record.dataset.getField(fieldName);
	//var idx=0;
	var tmp="";
	var parent_1="",parent_1v="";
	var parent_2="",parent_2v="";
	if (field){
		value=record.getValue(fieldName);
		if (value!="") {
			switch (field.dataType){
				case "string":{
					value=getValidStr(value);
					if(field.codesetid!="0"&&value!="")
					{
						/*?????????????????????? for examples 1&??*/
					  	//idx=value.indexOf("&");
					  	//value=value.substring(idx+1, value.length);
					  	if(field.codesetid=="UN")
					  	{
					  		tmp="_"+field.codesetid+value;
					  		if(!g_dm[tmp])
					  		  tmp="_UM"+value;					  		  					  	
					  	}
					  	else if (field.codesetid=="@@")
					  	{
					  		tmp="_"+field.codesetid+value.toLowerCase();
					  		if(!g_dm[tmp])
					  		  tmp="_@@"+value;					  		  					  	
					  	}					  	
					  	else if (field.codesetid=="UM")
					  	{
					  		tmp="_"+field.codesetid+value;
					  		if(!g_dm[tmp])
					  		  tmp="_UN"+value;	
					  		else
					  		{
					  			/**get up two level,效率?*/
								/*
					  			parent_1=getUpDeptId(field.codesetid+value);
					  			parent_2=getUpDeptId(field.codesetid+parent_1);
					  			parent_1="_"+field.codesetid+parent_1;
					  	        if(!(g_dm[parent_1]=="undefined"||g_dm[parent_1]==null))
					  	        {
					  	        	parent_1v=g_dm[parent_1].V;
					  	        }
					  	        parent_2="_"+field.codesetid+parent_2;
					  	        if(!(g_dm[parent_2]=="undefined"||g_dm[parent_2]==null))
					  	        {
					  	        	parent_2v=g_dm[parent_2].V;
					  	        }  	        
								*/
								if(field.level>0)
								{
								
								//	parent_1=getUpDeptDesc(field.codesetid+value,field.level);
									if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
									{	
										if(!(g_dm[tmp].P=="undefined"||g_dm[tmp].P==null||g_dm[tmp].P.length==0))
											parent_1=g_dm[tmp].P;
										else
											parent_1=g_dm[tmp].V;
									}
								}
					  		}	  		  					  	
					  	}
					  	else
					  		tmp="_"+field.codesetid+value;
					  		
					    if(field.codesetid=="orgType") //邓灿  组织异动模块组织类型用到
					    {
					    	if(value=="UM")
					    		value="部门";
					    	else if(value=="UN")
					    		value="单位";
					    	else
					    		value="";
					    }
					    else
					    {
						  	if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
						  	{
						  	   if(field.codesetid=="UM"&&tmp.substring(0,3)!='_UN'&&field.level>0)
						  	   {
						  	   	 //value=g_dm[tmp].V;
								   value=parent_1;  	   	 
						  	   }
						  	   else
						  	   	  value=g_dm[tmp].V;
						  	}
						  	else
						  		value=""; 
						 }
					}
					break;
				}

				case "byte":;
				case "short":;
				case "int":;
				case "long":{
					if (!isNaN(value)) value=value+"";

					break;
				}
				case "float":;
				case "double":;
				case "bigdecimal":{					
					var format=field.getFormat();
					if (!format) format="#,##.##";
					if (!isNaN(value)) value=formatFloat(value, format);
					break;
				}

				case "date":{
					var format=field.getFormat();
					if (!format) format="yyyy-MM-dd";
					//alert(value);
					//value=value+1;
					value=formatDate(value, format);
					break;
				}
				
				case "time":{
					var format=field.getFormat();
					if (!format) format="hh:mm:ss";
					value=formatDate(value, format);
					break;
				}
				
				case "datetime":{
					var format=field.getFormat();
					if (!format) format="yyyy-MM-dd hh:mm:ss";
					value=formatDate(value, format);
					break;
				}
				/*
				case "blob":
				    alert("blob");
					break;*/
				case "boolean":;
				default:{
					value=getValidStr(value);
					break;
				}
			}
		}
	}
	return value;
}





function _record_setValue(record, fieldName, value){
	
	 
	if(typeof(infor_type)!="undefined"&&(infor_type=='2'||(infor_type=='3'&&operationtype=='8'))&&typeof(autoInsertRecord)!="undefined"&&autoInsertRecord==0) //dengcan 组织异动合并单元特殊的字段不允许修改
	{ 
			if(typeof(operationtype)!="undefined"&&(operationtype=='8'||operationtype=='9'))
			{
					if(typeof(priv_obj)!="undefined"&&priv_obj.length>0)
					{ 
						if(fieldName=='codesetid_2'||fieldName=='codeitemdesc_2'||fieldName=='corcode_2'||fieldName=='parentid_2'||fieldName=='start_date_2')
						{
							var key='B0110';
							if(infor_type=='3')
								key='E01A1';
							var isEdit=0;
							for(var k=0;k<priv_obj.length;k++)
							{
								if(priv_obj[k]==record.getString(key))
									isEdit=1;	 
									
								 if(fieldName=='start_date_2')
								  { 
								  			autoInsertRecord=1;
								  			var key='B0110';
											if(infor_type=='3')
												key='E01A1';
											_dataset=record.dataset; 
											var self_key_value=record.getString(key);  
											var record2=_dataset.getFirstRecord();	
											while (record2) 
										   { 
												 var key_value=record2.getString(key); 
												 if(typeof(group_arr[key_value])!="undefined")
												 { 
													if(self_key_value!=key_value&&group_arr[key_value].split('`')[1]==self_key_value)
										 			{    
															record2.setValue('start_date_2',value);
															record2.recordState="modify";
																		
													}
												 }
												record2=record2.getNextRecord();
											} 
											autoInsertRecord=0;
								  } 	
									
									
							}
							if(record.getString(key).charAt(0)!='B'&&fieldName!='codeitemdesc_2'&&fieldName!='start_date_2'&&fieldName!='corcode_2')
								isEdit=0;
							if(isEdit==0)
							{
								return;
							}
						}
					}
					else if(typeof(priv_obj)!="undefined"&&priv_obj.length==0)
					{
						if(fieldName=='codesetid_2'||fieldName=='codeitemdesc_2'||fieldName=='corcode_2'||fieldName=='parentid_2'||fieldName=='start_date_2')
						{
						//	if(record.getString(key).charAt(0)!='B'&&fieldName!='codeitemdesc_2'&&fieldName!='start_date_2'&&fieldName!='corcode_2')
							isEdit=0;
							if(isEdit==0)
							{
								return;
							}
						}
					} 
			}
	} 
	
	var dataset=record.dataset;
	var fields=record.fields;
	var fieldIndex=-1;
	if (typeof(fieldName)=="number"){
		fieldIndex=fieldName;
	}
	else if (typeof(fieldName)=="string"){
		fieldIndex=fields["_index_"+fieldName.toLowerCase()];
	}

	if (typeof(fields[fieldIndex])=="undefined"){
		throw constErrCantFindField.replace("%s", record.dataset.id+"."+fieldName);
	}

	var field=fields[fieldIndex];

	switch (field.dataType)
	{
		case "byte":;
		case "short":;
		case "int":;
		case "long":
			if (typeof(value)!="number") {
				value = getInt(value);
			}
			break;
		
		case "float":;
		case "double":;
		case "bigdecimal":
			if (typeof(value)!="number") {
				value = getFloat(value);
			}
			break;
				
		case "date":;
		case "date":;
		case "datetime":
			if (typeof(value)!="object") {
				value=getValidStr(value);
				value=new dataset.window.Date(value.replace(/-/g, "/"));
			}
			else {				
				value=new dataset.window.Date(value.getTime());
			}
			if (isNaN(value)) value="";
			break;
		case "time":
			if (typeof(value)!="object") {
				value=getValidStr(value);
				value=new dataset.window.Date("1970/01/01 " + value);
			}
			else {				
				value=new dataset.window.Date(value.getTime());
			}
			if (isNaN(value)) value="";
			break;
		case "boolean":
			value=isTrue(value);
			break;
		case "string":
			if(field.codesetid!="0"&&value!="")
			{
				if(field.codesetid=='orgType')
				{
					if(value!='UM'&&value!='UN')
						value="";
				}
				else
				{
				  	if(field.codesetid=="UN")
				  	{
				  		tmp="_"+field.codesetid+value;
				  		if(!g_dm[tmp])
				  		  tmp="_UM"+value;					  		  					  	
				  	}
				  	else if (field.codesetid=="@@")
				  	{
				  		tmp="_"+field.codesetid+value.toLowerCase();
				  		if(!g_dm[tmp])
				  		  tmp="_@@"+value;					  		  					  	
				  	}					  	
				  	else if (field.codesetid=="UM")
				  	{
				  		tmp="_"+field.codesetid+value;
				  		if(!g_dm[tmp])
				  		  tmp="_UN"+value;					  		  					  	
				  	}
				  	else
				  		tmp="_"+field.codesetid+value;
				  	if((g_dm[tmp]=="undefined"||g_dm[tmp]==null))
				  		value=""; 
				  }
			}
			break;
	}

	var event_result=fireDatasetEvent(dataset, "beforeChange", [dataset, field, value]);
	if (event_result) throw event_result;

	var eventName=getElementEventName(dataset, "onSetValue");

	if (isUserEventDefined(eventName)){
			value=fireUserEvent(eventName, [dataset, field, value]);
	}


	record[fieldIndex]=value;
	dataset.modified=true;

	fireDatasetEvent(dataset, "afterChange", [dataset, field]);

	if (dataset.state=="none") dataset_setState(dataset, "modify");
	_broadcastFieldMsg(_notifyFieldDataChanged, dataset, record, field);

}

function record_setValue(fieldName, value){
	try{
		_record_setValue(this, fieldName, value);
	}
	catch(e){
		processException(e);
	}
}

function record_getCurValue(fieldName){
	var record=this;
	if (typeof(fieldName)=="number"){
		return record.getValue(fieldName+record.fields.fieldCount);
	}
	else{
		return record.getValue("_cur_"+fieldName.toLowerCase());
	}
}

function record_setCurValue(fieldName, value){
	var record=this;
	if (typeof(fieldName)=="number"){
		record.setValue(fieldName+record.fields.fieldCount, value);
	}
	else{
		record.setValue("_cur_"+fieldName.toLowerCase(), value);
	}
}

function record_getOldValue(fieldName){
	var record=this;
	if (typeof(fieldName)=="number"){
		return record.getValue(fieldName+record.fields.fieldCount*2);
	}
	else{
		return record.getValue("_old_"+fieldName.toLowerCase());
	}
}

function record_setOldValue(fieldName, value){
	var record=this;
	if (typeof(fieldName)=="number"){
		record.setValue(fieldName+record.fields.fieldCount*2, value);
	}
	else{
		record.setValue("_old_"+fieldName.toLowerCase(), value);
	}
}

function dataset_getValue(fieldName){
	var dataset=this;
	if (dataset.record)
		return dataset.record.getValue(fieldName);
	else
		return "";
}

function dataset_getString(fieldName){
	var dataset=this;
	if (dataset.record)
		return dataset.record.getString(fieldName);
	else
		return "";
}

function dataset_setValue(fieldName, value){
	try{
		var dataset=this;
		if (dataset.record)
			dataset.record.setValue(fieldName, value);
		else
			throw constErrNoCurrentRecord;
	}
	catch(e){
		processException(e);
	}
}

function dataset_getCurValue(fieldName){
	var dataset=this;
	if (typeof(fieldName)=="number"){
		return dataset.getValue(fieldName+dataset.fields.fieldCount);
	}
	else{
		return dataset.getValue("_cur_"+fieldName);
	}
}

function dataset_setCurValue(fieldName, value){
	var dataset=this;
	if (typeof(fieldName)=="number"){
		dataset.setValue(fieldName+dataset.fields.fieldCount, value);
	}
	else{
		dataset.setValue("_cur_"+fieldName, value);
	}
}

function dataset_getOldValue(fieldName){
	var dataset=this;
	if (typeof(fieldName)=="number"){
		return dataset.getValue(fieldName+dataset.fields.fieldCount*2);
	}
	else{
		return dataset.getValue("_old_"+fieldName);
	}
}

function dataset_setOldValue(fieldName, value){
	var dataset=this;
	if (typeof(fieldName)=="number"){
		dataset.setValue(fieldName+dataset.fields.fieldCount*2, value);
	}
	else{
		dataset.setValue("_old_"+fieldName, value);
	}
}

function _record_getPrevRecord(record){
	var _record=record;
	while (_record){
		_record=_record.prevUnit;
		if (dataset_isRecordValid(_record)) return _record;
	}
}

function record_getPrevRecord(){
	return _record_getPrevRecord(this);
}

function _record_getNextRecord(record){
	var _record=record;
	while (_record){
		_record=_record.nextUnit;
		if (dataset_isRecordValid(_record)) return _record;
	}
}

function record_getNextRecord(){
	return _record_getNextRecord(this);
}

function dataset_disableControls(){
	var dataset=this;
	dataset.disableControlCount=dataset.disableControlCount+1;
}

function dataset_enableControls(){
	var dataset=this;
	dataset.disableControlCount=(dataset.disableControlCount>0)?dataset.disableControlCount-1:0;
	dataset.refreshControls();

}

function dataset_disableEvents(){
	var dataset=this;
	dataset.disableEventCount=dataset.disableEventCount+1;
}

function dataset_enableEvents(){
	var dataset=this;
	dataset.disableEventCount=(dataset.disableEventCount>0)?dataset.disableEventCount-1:0;
}

function dataset_refreshControls(){
	var dataset=this;
	validateDatasetCursor(dataset);
	dataset.loadDetail();
	_broadcastDatasetMsg(_notifyDatasetRefresh, dataset);
}

function _dataset_move(dataset, count){
	var _record=dataset.record;
	if (!_record) _record=dataset.getFirstRecord();
	if (!_record) return;
	var record=_record;

	if (count>0){
		var old_pageIndex=record.pageIndex
		var _eof=false;
		for(var i=0; i<count; i++){
			var pageIndex=0;

			_record=record.getNextRecord();
			if (!_record || (_record && _record.pageIndex!=old_pageIndex)){
				if (old_pageIndex<dataset.pageCount){
					if (!dataset.isPageLoaded(old_pageIndex+1)){
						if ((i+dataset.pageSize<count) && (old_pageIndex+1<dataset.pageCount)){
							i+=dataset.pageSize-1;
							_record=record;
						}
						else{
							_dataset_loadPage(dataset, old_pageIndex+1);
							_record=record.getNextRecord();
						}
					}
				}
				old_pageIndex++;
			}

			if (_record){
				record=_record;
			}
			else{
				_eof=true;
				break;
			}
		}
		select_general_record(dataset,record);
		dataset_setBofnEof(dataset, (!dataset_isRecordValid(dataset.record)), _eof);
	}
	else{
		var old_pageIndex=record.pageIndex
		var _bof=false;
		for(var i=count; i<0; i++){
			var pageIndex=0;

			_record=record.getPrevRecord();
			if (!_record || (_record && _record.pageIndex!=old_pageIndex)){
				if (old_pageIndex>1){
					if (!dataset.isPageLoaded(old_pageIndex-1)){
						if ((i+dataset.pageSize<0) && (old_pageIndex>1)){
							i+=dataset.pageSize-1;
							_record=record;
						}
						else{
							_dataset_loadPage(dataset, old_pageIndex-1);
							_record=record.getPrevRecord();
						}
					}
				}
				old_pageIndex--;
			}

			if (_record){
				record=_record;
			}
			else{
				_bof=true;
				break;
			}
		}
		select_general_record(dataset,record);
		dataset_setBofnEof(dataset, _bof, (!dataset_isRecordValid(dataset.record)));
	}

	if (record) _do_dataset_setRecord(dataset, record);
}

function dataset_move(count){
	var dataset=this;
	try{
		_dataset_move(dataset, count);
	}
	catch(e){
		processException(e);
	}
}

function dataset_movePrev(){
	var dataset=this;
	try{
		_dataset_move(dataset, -1);
	}
	catch(e){
		processException(e);
	}
}

function dataset_moveNext(){
	var dataset=this;
	try{
		_dataset_move(dataset, 1);
	}
	catch(e){
		processException(e);
	}
}

function _dataset_getFirstRecord(dataset){
	var record=dataset.firstUnit;
	if (record && !dataset_isRecordValid(record)) record=record.getNextRecord();
	return record;
}

function dataset_getFirstRecord(){
	return _dataset_getFirstRecord(this);
}
function select_general_record(dataset,record2){//xieguiquan 2011-07-15
	if(dataset.id&&dataset.id.toLowerCase().indexOf("templet_")!=-1&&record2){
		var a0100="";
		var basepre="";
		//先判断属性是否存在 wangrd 2015-06-25
		if (record2.hasOwnProperty('a0100')) 
		  a0100=record2.getValue("a0100");
		if (record2.hasOwnProperty('basepre')) 
    	   basepre=record2.getValue("basepre");
    	locaterec(basepre,a0100);
    }
}
function dataset_moveFirst(){
	var dataset=this;
	try{
		if (!dataset.isPageLoaded(1)) _dataset_loadPage(dataset, 1);
		_do_dataset_setRecord(dataset, dataset.getFirstRecord());
		select_general_record(dataset,dataset.getFirstRecord());
		dataset_setBofnEof(dataset, true, (!dataset_isRecordValid(dataset.record)));
	
	}
	catch(e){
		processException(e);
	}
}

function _dataset_getLastRecord(dataset){
	var record=dataset.lastUnit;
	select_general_record(dataset,record);
	if (!dataset_isRecordValid(record) && record) record=record.getPrevRecord();
	return record;
}

function dataset_getLastRecord(){
	return _dataset_getLastRecord(this);
}

function dataset_moveLast(){
	var dataset=this;

	try{
		if (!dataset.isPageLoaded(dataset.pageCount)) _dataset_loadPage(dataset, dataset.pageCount);
		_do_dataset_setRecord(dataset, dataset.getLastRecord());
		dataset_setBofnEof(dataset, (!dataset_isRecordValid(dataset.record)), true);
	}
	catch(e){
		processException(e);
	}
}

function dataset_find(fieldNames, values, startRecord){

	function isMatching(fieldNames, values, record){
		var result=true;
		for (var j=0; j<fieldNames.length && j<values.length; j++){
			if (!compareText(record.getString(fieldNames[j]), values[j])){
				result=false;
				break;
			}
		}
		return result;
	}

	if (!fieldNames || !values) return false;

	var dataset=this;
	if (!dataset.record) return;
	if (isMatching(fieldNames, values, dataset.record)) return dataset.record;

	var record=(startRecord)?startRecord:dataset.getFirstRecord();
	while (record){
		if (isMatching(fieldNames, values, record)) return record;
		record=record.getNextRecord();
	}
}

function dataset_locate(fieldName, value, startRecord){

	function isMatching(fieldName, value, record){
		var tmpValue=record.getString(fieldName);
		return (tmpValue && compareText(tmpValue.substr(0, len), value));
	}

	if (!value) return false;

	var dataset=this;
	if (!dataset.record) return;
	if (isMatching(fieldName, value, dataset.record)) return dataset.record;

	var len=value.length;
	var record=(startRecord)?startRecord:dataset.getFirstRecord();
	while (record){
		if (isMatching(fieldName, value, record)) return record;
		record=record.getNextRecord();
	}
}

function _dataset_insertRecord(dataset, mode){
	_dataset_postRecord(dataset);

	var event_result=fireDatasetEvent(dataset, "beforeInsert", [dataset, mode]);
	if (event_result) throw event_result;

	var pageIndex=(dataset.record)?dataset.record.pageIndex:1;

	var newRecord=new Array();
	pArray_insert(dataset, mode, dataset.record, newRecord);
	initRecord(newRecord, dataset);

	switch (mode){
		case "begin":{
			newRecord.pageIndex=1;
			break;
		}
		case "end":{
			newRecord.pageIndex=dataset.pageCount;
			break;
		}
		default:{
			newRecord.pageIndex=pageIndex;
			break;
		}
	}

	newRecord.recordState="new";
	newRecord.recordno=9999;

	var _masterDataset=dataset.masterDataset;
	if (_masterDataset){
		if (_masterDataset.record){
			for(var i=0; i<dataset.references.length; i++){
				var fieldIndex=dataset.references[i].masterIndex;
				if (_masterDataset.getString(fieldIndex)==""){
					var field=_masterDataset.getField(fieldIndex);
					switch (field.dataType) {
					case "string":
						_masterDataset.setValue(fieldIndex, _getAutoGenID());
						break;
					case "byte":;
					case "short":;
					case "int":;
					case "long":;
					case "float":;
					case "double":;
					case "bigdecimal":;
						var maxnum=0;
						var record=_masterDataset.firstUnit;
						while (record){
							if (record.getValue(fieldIndex)>maxnum) {
								maxnum=record.getValue(fieldIndex);
							}
							record=record.nextUnit;
						}
						_masterDataset.setValue(fieldIndex, maxnum+1);
						break;
					}
				}
			}
			_dataset_postRecord(_masterDataset);

			for(var i=0; i<dataset.references.length; i++){
				var reference=dataset.references[i];
				newRecord[reference.detailIndex]=
					_masterDataset.getCurValue(reference.masterIndex);
			}
		}
		else{
			throw constErrNoMasterRecord;
		}
	}

	dataset_setState(dataset, "insert");
	_broadcastDatasetMsg(_notifyDatasetInsert, dataset, dataset.record, [mode, newRecord]);
	_dataset_setRecord(dataset, newRecord);

	var fieldCount=dataset.fields.fieldCount;
	for (var i=0; i<fieldCount; i++) {
		var field=dataset.fields[i];
		var defaultValue=getValidStr(field.defaultValue);
		if (defaultValue!=""){
			newRecord.setValue(i, defaultValue);
		}
		else if (field.dataType=="boolean") {
			newRecord.setValue(i, false);
		}
		if (field.autoGenId){
			newRecord.setValue(i, _getAutoGenID());
		}
	}

	fireDatasetEvent(dataset, "afterInsert", [dataset, mode]);
	dataset.modified=false;
}

function dataset_insertRecord(mode){
	try{
		_dataset_insertRecord(this, mode);
	}
	catch(e){
		processException(e);
	}
}

function _dataset_deleteRecord(dataset){
	if (!dataset.record) return;

/*	if (dataset.detailDatasets){
		var unit=dataset.detailDatasets.firstUnit;
		while (unit && unit.data){
			var detail_dataset=unit.data;
			if (detail_dataset.references.length>0) {
				_dataset_postRecord(detail_dataset);
				detail_dataset.moveFirst();
				while (!detail_dataset._eof){
					detail_dataset.deleteRecord();
				}
			}
			detail_dataset.refreshControls();
			unit=unit.nextUnit;
		}
	}
*/

	needUpdateEditor=false;
	try{
		if (dataset.record.recordState=="new" || dataset.record.recordState=="insert"){
			var event_result=fireDatasetEvent(dataset, "beforeDelete", [dataset]);
			if (event_result) throw event_result;

			dataset.record.recordState="discard";
		}
		else{
			var event_result=fireDatasetEvent(dataset, "beforeDelete", [dataset]);
			if (event_result) throw event_result;

			dataset.record.recordState="delete";
			_changeMasterRecordState(dataset);
		}

		dataset.modified=false;

		fireDatasetEvent(dataset, "afterDelete", [dataset]);
		dataset_setState(dataset, "none");

		_broadcastDatasetMsg(_notifyDatasetDelete, dataset, dataset.record);
		validateDatasetCursor(dataset);
	}
	finally{
		needUpdateEditor=true;
	}
}

function dataset_deleteRecord(){
	try{
		_dataset_deleteRecord(this);
	}
	catch(e){
		processException(e);
	}
}

function _dataset_postRecord(dataset){

	if (!dataset.record) return;
	if (!dataset_isRecordValid(dataset.record)) return;

	_broadcastDatasetMsg(_notifyDatasetBeforeUpdate, dataset, dataset.record);

	if (dataset.modified){
		var fieldCount=dataset.fields.fieldCount;
		for (var i=0; i<fieldCount; i++){
			if (!isTrue(dataset.fields[i].readOnly) && isTrue(dataset.fields[i].required) &&
				dataset.getString(i)==""){
				throw constErrFieldValueRequired.replace("%s", dataset.fields[i].label);
			}
		}

		var event_result=fireDatasetEvent(dataset, "beforePost", [dataset]);
		if (event_result) throw event_result;

		var detaildatasets=new Array();
		if (dataset.detailDatasets){
			var unit=dataset.detailDatasets.firstUnit;
			while (unit && unit.data){
				var detail_dataset=unit.data;
				if (detail_dataset.references.length>0) {
					var disableCount=detail_dataset.disableControlCount;
					detail_dataset.disableControlCount=1;
					try{
						var changed=false;
						_dataset_postRecord(detail_dataset);
						detail_dataset.moveFirst();
						while (!detail_dataset._eof){
							for(var i=0; i<detail_dataset.references.length; i++){
								var detailIndex=detail_dataset.references[i].detailIndex;
								var masterIndex=detail_dataset.references[i].masterIndex;
								if (detail_dataset.getValue(detailIndex)!=dataset.getValue(masterIndex)){
									detail_dataset.setValue(detailIndex, dataset.getValue(masterIndex));
									changed=true;
								}
							}
							_dataset_postRecord(detail_dataset);

							detail_dataset.moveNext();
						}
					}
					finally{
						detail_dataset.disableControlCount=disableCount;
					}
					
					if (changed){
						detaildatasets[detaildatasets.length]=detail_dataset;
					}
				}
				unit=unit.nextUnit;
			}
		}

		switch (dataset.record.recordState){
			case "none":{
				dataset.record.recordState="modify";
				_changeMasterRecordState(dataset);
				break;
			}
			case "new":{
				dataset.record.recordState="insert";
				_changeMasterRecordState(dataset);
				break;
			}
		}

		for (var i=0; i<fieldCount; i++){
			dataset.record[fieldCount+i]=dataset.record[i];
		}
		dataset.modified=false;

		fireDatasetEvent(dataset, "afterPost", [dataset]);
		dataset_setState(dataset, "none");
		
		for (var i=0;i<detaildatasets.length;i++){
			detail_dataset.refreshControls();
			validateDatasetCursor(detail_dataset);
		}
	}
	else{
		if (dataset.record.recordState=="new"){
			dataset.record.recordState="discard";
			dataset_setState(dataset, "none");
			_broadcastDatasetMsg(_notifyDatasetDelete, dataset, dataset.record);
			validateDatasetCursor(dataset);
		}
	}
}

function dataset_postRecord(){
	try{
		_dataset_postRecord(this);
		return true;
	}
	catch(e){
		processException(e);
		return false;
	}
}

function _dataset_cancelRecord(dataset){
	if (!dataset.record) return;

	needUpdateEditor=false;
	try{
		if (dataset.record.recordState=="new"){
			var event_result=fireDatasetEvent(dataset, "beforeCancel", [dataset]);
			if (event_result) throw event_result;

			dataset.record.recordState="discard";

			fireDatasetEvent(dataset, "afterCancel", [dataset]);

			dataset_setState(dataset, "none");
			_broadcastDatasetMsg(_notifyDatasetDelete, dataset, dataset.record);
			validateDatasetCursor(dataset);
		}
		else if (dataset.modified){
			var event_result=fireDatasetEvent(dataset, "beforeCancel", [dataset]);
			if (event_result) throw event_result;

			var fieldCount=dataset.fields.fieldCount;
			for (var i=0; i<fieldCount; i++){
				dataset.record[i]=dataset.record[fieldCount+i];
			}
			dataset.modified=false;

			fireDatasetEvent(dataset, "afterCancel", [dataset]);

			dataset_setState(dataset, "none");
			_broadcastDatasetMsg(_notifyDatasetRefreshRecord, dataset, dataset.record);
		}
	}
	finally{
		needUpdateEditor=true;
	}
}

function dataset_cancelRecord(){
	try{
		_dataset_cancelRecord(this);
	}
	catch(e){
		processException(e);
	}
}

function _dataset_copyRecord(dataset, record, fieldMap){	
	if (fieldMap){
		var fieldmaps=new Array();
		var fields=fieldMap.split(";");
		var field1="", field2="";
		for(var i=0; i<fields.length; i++){
			fieldmaps[i]=new Object();
			var index=fields[i].indexOf("=");
			if (index>=0){
				field1=fields[i].substr(0, index);
				field2=fields[i].substr(index+1);
			}
			else{
				field1=fields[i];
				field2=fields[i];
			}
			
			var value=record.getValue(field2);
			if (typeof(value)!="undefined") dataset.setValue(field1, value);
		}
	}
	else{
		for(var i=0; i<dataset.fields.fieldCount; i++){
			var fieldName=dataset.getField(i).name;
			var field=record.dataset.getField(fieldName);
			if (field) {
				var value=record.getValue(fieldName);
				if (typeof(value)!="undefined") dataset.setValue(fieldName, value);
			}
		}
	}
}

function dataset_copyRecord(record, fieldMap){
	var dataset=this;
	_dataset_copyRecord(dataset, record, fieldMap);
}

function _broadcastDatasetMsg(proc, dataset, record, reserved){
	if (dataset.disableControlCount>0) return;
	var pArray=dataset.editors;
	if (pArray){
		var unit=pArray.firstUnit;
		while (unit && unit.data){
			proc(unit.data, dataset, record, reserved);
			unit=unit.nextUnit;
		}
	}
}

function _broadcastFieldMsg(proc, dataset, record, field, reserved){
	if (dataset.disableControlCount>0) return;
	var pArray=dataset.editors;
	if (pArray){
		var unit=pArray.firstUnit;
		while (unit && unit.data){
			proc(unit.data, dataset, record, field, reserved);
			unit=unit.nextUnit;
		}
	}
}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

function _notifyDatasetCursorChanged(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "datatable":{
			if (!record) break;

			var maxRow=element.getAttribute("maxRow");
			if (element.tBodies[0].rows.length>=maxRow){
				var needRefresh=true;
				var firstRecord=_window.getTableFirstRecord(element);
				var lastRecord=_window.getTableLastRecord(element);

				var _record=firstRecord;
				while (_record){
					if (_record==record){
						needRefresh=false;
						break;
					}

					if (_record==lastRecord) break;
					_record=_record.nextUnit;
				}

				if (needRefresh){
					var counter=maxRow;
					var tmpRecord=record;

					for(var i=0; i<counter; i++){
						tmpRecord=tmpRecord.getNextRecord();
						if (!tmpRecord) break;
					}

					var startRecord=record;
					tmpRecord=record;
					counter=maxRow-i-1;
					for(var i=0; i<counter; i++){
						tmpRecord=tmpRecord.getPrevRecord();
						if (tmpRecord)
							startRecord=tmpRecord;
						else
							break;
					}

					_window.refreshTableData(element, startRecord);
				}
			}

			var row=_window.getTableRowByRecord(element, record);
			if (row){
				_window.setActiveTableRow(row);
			}
			break;
		}
		case "datalabel":{
			_window.refreshElementValue(element);
			break;
		}
		case "panel":
			_window.refreshElementValue(element);
			element.isUserInput=false;
			break;
		case "editor":;
		case "dockeditor":{
			_window.refreshElementValue(element);
			element.isUserInput=false;
			break;
		}
		case "datapilot":{
			_window.refreshDataPilot(element);
			break;
		}
		case "pagepilot":{
			_window.refreshPagePilot(element);
			break;
		}
	}
}

function _notifyDatasetBeforeUpdate(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "dockeditor":{
			_window.updateEditorInput(element);
			break;
		}
	}
}

function _notifyDatasetStateChanged(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "editor":;
		case "dockeditor":{
			var field=_window.getElementField(element);
			element.setReadOnly(!isFieldEditable(dataset, field));
			break;
		}
		case "datapilot":{
			_window.refreshDataPilot(element);
			break;
		}
		case "datatable":{
			if (element.activeRow) _window.refreshTableRowIndicate(element.activeRow);
			break;
		}
	}
}

function _notifyDatasetInsert(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "datatable":{
			var row;
			if (record) row=_window.getTableRowByRecord(element, record);

			_window.insertTableRecord(element, reserved[0], row, reserved[1]);
			if (element.tBodies[0].rows.length>element.getAttribute("maxRow")){
				var lastRecord=_window.getTableLastRecord(element);
				if (lastRecord!=reserved[1]){
					_window.deleteTableRecord(element.tBodies[0].rows[element.tBodies[0].rows.length-1]);
				}
				else{
					_window.deleteTableRecord(element.tBodies[0].rows[0]);
				}
			}
			break;
		}
	}
}

function _notifyDatasetDelete(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "datatable":{
			if (record){
				var row=_window.getTableRowByRecord(element, record);
				if (row){
					if (element.tBodies[0].rows.length<=element.getAttribute("maxRow")){
						var firstRecord=_window.getTableFirstRecord(element);
						var lastRecord=_window.getTableLastRecord(element);
						if (firstRecord){
							var _record=lastRecord.getNextRecord();
							if (_record){
								_window.insertTableRecord(element, "end", row, _record);
							}
							else{
								var _record=firstRecord.getPrevRecord();
								if (_record) _window.insertTableRecord(element, "begin", row, _record);
							}
						}
					}

					_window.deleteTableRecord(row);
				}
			}
			break;
		}
	}
}

function _notifyDatasetRefreshRecord(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "datatable":{
			if (record){
				var row=_window.getTableRowByRecord(element, record);
				if (row) _window.refreshTableRecord(row);
			}
			break;
		}
		case "datalabel":;
		case "editor":;
		case "dockeditor":{
			_window.refreshElementValue(element);
			element.isUserInput=false;
			break;
		}
	}

	if (_window.isFileIncluded("editor")) _window.sizeDockEditor();
}

function _notifyDatasetRefresh(element, dataset, record, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "datatable":{
			if (!compareText(element.style.visibility, "hidden")) {
				refreshTableData(element);
			}
			else {
				element.needRefresh=true;
			}
			break;
		}
		case "datalabel":;
		case "editor":;
		case "dockeditor":{
			_window.refreshElementValue(element);
			element.isUserInput=false;
			break;
		}
		case "datapilot":{
			_window.refreshDataPilot(element);
			break;
		}
		case "pagepilot":{
			_window.refreshPagePilot(element);
			break;
		}
	}
	_notifyDatasetStateChanged(element, dataset, record, reserved);

	if (_window.isFileIncluded("editor")) _window.sizeDockEditor();
}

function _notifyFieldDataChanged(element, dataset, record, field, reserved){
	var _window=element.window;
	switch (element.getAttribute("extra")){
		case "datatable":{
			var row=_window.getTableRowByRecord(element, record);
			if(row)
			{
				for(var i=0; i<row.cells.length; i++){
					var cell=row.cells[i];
					if (compareText(cell.getAttribute("field"), field.name)){
						_window.refreshElementValue(cell);
					}
				}
			}
			break;
		}
		case "editor":;
		case "dockeditor":{
			if (compareText(element.getAttribute("field"), field.name)){
				_window.refreshElementValue(element);
				element.isUserInput=false;
			}
			break;
		}
		case "datalabel":{
			if (compareText(element.getAttribute("field"), field.name)){
				_window.refreshElementValue(element);
			}
			break;
		}
	}

	if (_window.isFileIncluded("editor")) 
	   _window.sizeDockEditor();
}

function _notifyFieldStateChanged(element, dataset, record, field, reserved)
{
	switch (element.getAttribute("extra"))
	{
		case "editor":;
		case "dockeditor":{
			var elmtField=getElementField(element);
			if (elmtField==field) {
				element.setReadOnly(!isFieldEditable(dataset, field));
			}
			break;
		}
	}
}

/*-----------------------------------------------------------------------------------------------------------------------------------------------------------------*/

function _resetRecordState(record){
	record.saveOldValue();
	if (record.recordState=="delete") {
		record.recordState="discard";
	}
	else if (record.recordState!="discard") {
		record.recordState="none";
	}
}

//--------------------------------------------------

function _changeMasterRecordState(dataset){
	var masterDataset=dataset.masterDataset;
	if (masterDataset) {
		if (masterDataset.record.recordState=="none") {
			masterDataset.record.recordState="modify";
			_changeMasterRecordState(masterDataset);
		}
	}	
}
