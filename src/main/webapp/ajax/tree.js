var _fileIncluded_tree=true;

var _rightclick_row=null;
var _stored_treeinfo=null;

function initTree(tree){
	tree.clearAllNodes=tree_clearAllNodes;
	tree.clearChildNodes=tree_clearChildNodes;
	tree.refreshNode=tree_refreshNode;
	tree.addNode=tree_addNode;
	tree.addDataNode=tree_addDataNode;
	tree.deleteNode=tree_deleteNode;
	tree.expandNode=tree_expandNode;
	tree.collapseNode=tree_collapseNode;
	tree.getCurrentNode=_tree_getCurrentNode;
	tree.setCurrentNode=_tree_setCurrentNode;
	tree.selectNode=tree_selectNode;
	tree.getTopNode=tree_getTopNode;

	tree.getTarget=tree_getTarget;
	tree.setTarget=tree_setTarget;
	tree.getContextMenuNode=tree_getContextMenuNode;

	for (var i=1; i<=8; i++){
		var tmpDataset=tree.getAttribute("dataset"+i);
		if (typeof(tmpDataset)!="undefined") eval("tree.dataset"+i+"="+tmpDataset+";");
	}

	tree.repeatrow=tree.rows[0].cloneNode(true);
	tree.deleteRow(0);

	tree.topNode=_createTreeNode(tree, null);
	eval("var isXmlExist=(typeof(__"+tree.id+")==\"object\")");
	if (isXmlExist) {

		eval("var xmlIsland=__"+tree.id);
		root=xmlIsland.documentElement;
		if (!root) return;

		initTreeNodes(tree, tree.topNode, root);
		_xml_list[_xml_list.length]=xmlIsland;
	}

	tree.expandNode(null);
	tree.setCurrentNode(null);
	if (_isDropDownPage) tree.onclick=_dropdown_onclick;
}

function tree_getTopNode() {
	return this.topNode;
}

function tree_getTarget() {
	return this.target;
}

function tree_setTarget(target) {
	this.target = target;
}

function tree_getContextMenuNode() {
	return this.contextMenuNode;
}

function _createTreeNode(tree, parentNode){
	var level=0;
	if (parentNode){
		level=parentNode.level+1;
	}

	var newNode=new Object();
	newNode.childNodes=new pArray();
	newNode.tree=tree;
	newNode.level=level;
	newNode.parentNode=parentNode;
	newNode.expanded=false;

	newNode.getTree=_node_getTree;
	
	newNode.setLabel=_node_setLabel;
	newNode.getLabel=_node_getLabel;
	
	newNode.setRecord=_node_setRecord;
	newNode.getRecord=_node_getRecord;
	
	newNode.setIcon=_node_setIcon;
	newNode.getIcon=_node_getIcon;
	
	newNode.setExpandedIcon=_node_setExpandedIcon;
	newNode.getExpandedIcon=_node_getExpandedIcon;
	
	newNode.setPath=_node_setPath;
	newNode.getPath=_node_getPath;
	
	newNode.setTag=_node_setTag;
	newNode.getTag=_node_getTag;
	
	newNode.isExpanded=_node_isExpanded;
	newNode.setCheckable=_node_setCheckable;
	
	newNode.isCheckable=_node_isCheckable;
	newNode.setChecked=_node_setChecked;
	
	newNode.isChecked=_node_isChecked;
	newNode.setHasChild=_node_setHasChild;
	newNode.isHasChild=_node_isHasChild;
	
	newNode.getLevel=_node_getLevel;
	newNode.getParent=_node_getParent;
	newNode.children=_node_children;

	var hasChild=getValidStr(tree.getAttribute("hasChild"+newNode.level));
	if (hasChild!=""){
		newNode.hasChild=isTrue(hasChild);
	}
	else{
		newNode.hasChild=true;
	}

	newNode.icon=tree.getAttribute("icon"+newNode.level);
	newNode.expandedIcon=tree.getAttribute("expandedIcon"+newNode.level);
	newNode.checkable=isTrue(tree.getAttribute("checkable"+newNode.level));
	newNode.checked=isTrue(tree.getAttribute("checked"+newNode.level));
	newNode._expanded=isTrue(tree.getAttribute("_expanded"+newNode.level));
	newNode.dataInited=false;
	return newNode;
}

function initTreeNodes(tree, parentNode, xmlNode){
	var childXmlNodes = xmlNode.childNodes;
	for (var i=0; i<childXmlNodes.length; i++) {
		var childXmlNode = childXmlNodes.item(i);
		var newNode=_createTreeNode(tree, parentNode);
		newNode.label=childXmlNode.getAttribute("label");
		newNode.icon=childXmlNode.getAttribute("icon");
		newNode.expandedIcon=childXmlNode.getAttribute("expandedIcon");
		newNode.hasChild=isTrue(childXmlNode.getAttribute("hasChild"));
		newNode.path=childXmlNode.getAttribute("path");
		newNode.checkable=isTrue(childXmlNode.getAttribute("checkable"));
		newNode.checked=isTrue(childXmlNode.getAttribute("checked"));
		newNode._expanded=isTrue(childXmlNode.getAttribute("_expanded"));
		newNode.tag=childXmlNode.getAttribute("tag");
		_insertTreeNode(tree, parentNode, newNode);

		initTreeNodes(tree, newNode, childXmlNode);
	}
}

function _node_getTree() {
	return this.tree;
}

function _node_setLabel(label) {
	this.label = label;
}

function _node_getLabel() {
	return this.label;
}

function _node_setRecord(record) {
	this.record = record;
}

function _node_getRecord() {
	return this.record;
}

function _node_setIcon(icon) {
	this.icon = icon;
}

function _node_getIcon() {
	return this.icon;
}

function _node_setExpandedIcon(icon) {
	this.expandedIcon = icon;
}

function _node_getExpandedIcon() {
	return this.expandedIcon;
}

function _node_setPath(path) {
	this.path = path;
}

function _node_getPath() {
	return this.path;
}

function _node_setTag(tag) {
	this.tag = tag;
}

function _node_getTag() {
	return this.tag;
}

function _node_isExpanded() {
	return this.expanded;
}

function _node_setCheckable(checkable) {
	this.checkable = checkable;
}

function _node_isCheckable() {
	return this.checkable;
}

function _node_setChecked(checked) {
	if (this.checked != checked) {
		this.checked = checked;		
		refreshTreeNode(this);
	}
}

function _node_isChecked() {
	return this.checked;
}

function _node_isHasChild() {
	return this.hasChild;
}

function _node_setHasChild(hasChild) {
	if (this.hasChild != hasChild) {
		this.hasChild = hasChild;		
		refreshTreeNode(this);
	}
}

function _node_getLevel() {
	return this.level;
}

function _node_getParent() {
	return this.parentNode;
}

function _node_children() {
	return this.childNodes;
}

function getTreeNodeStyle(row){
	if (row.rowIndex % 2)
		return "row_odd";
	else
		return "row_even";
}

function refreshTreeNodeColor(row){
	var tree=getTableByRow(row);

	var currentNode=tree.currentNode;

	if (currentNode && currentNode.row==row){
		row.className="row_selected";
	}
	else{
		row.className=getTreeNodeStyle(row);
	}
}

function refreshTreeColor(tree, startIndex){
	var row;
	var maxIndex=tree.rows.length-1;
	for(var i=startIndex; i<=maxIndex; i++){
		row=tree.rows[i];
		refreshTreeNodeColor(row);
	}
}

function tree_clearAllNodes(tree){
	tree.clearChildNodes(tree);

	tree.expandNode();
	tree.setCurrentNode(tree, null);
}

function refreshTreeNode(node){
	var row=node.row;
	if (row){
		for(var i=0; i<row.cells.length; i++){
			refreshElementValue(row.cells[i]);
		}
	}
}

function tree_refreshNode(node){
	refreshTreeNode(node);
}

function tree_addNode(parentNode, label, tag, mode, node){
	return _addTreeNode(this, parentNode, label, tag, null, mode, node);
}

function tree_addDataNode(parentNode, record){
	return _addTreeNode(this, parentNode, "", null, record);
}

function _addTreeNode(tree, parentNode, label, tag, record, mode, node){
	var newNode=_createTreeNode(tree, parentNode);
	newNode.label=label;
	newNode.tag=tag;
	newNode.record=record;

	if (record)
	{
		var labelField=tree.getAttribute("labelField"+newNode.level);
		if (labelField)
			newNode.label=record.getString(labelField);
		else
			newNode.label=record.getString(0);

		var iconField=tree.getAttribute("iconField"+newNode.level);
		if (iconField)
			newNode.icon=record.getString(iconField);
		else
			newNode.icon=tree.getAttribute("icon"+newNode.level);

		var expandedIconField=tree.getAttribute("expandedIconField"+newNode.level);
		if (expandedIconField)
			newNode.expandedIcon=record.getString(expandedIconField);
		else
			newNode.expandedIcon=tree.getAttribute("expandedIcon"+newNode.level);
	}

	_insertTreeNode(tree, parentNode, newNode, mode, node);
	return newNode;
}

function _insertTreeNode(tree, parentNode, newNode, mode, node) {

	function getSlideNext(node){
		if (node){
			var result=node.nextUnit;
			if (!result) result=getSlideNext(node.parentNode);
			return result;
		}
	}
	
	var nodes=parentNode.childNodes;
	var newRow;
	if (parentNode.expanded && parentNode.row){
		var _mode, _node;
		switch (mode){
			case "begin":{
				_node=nodes.firstUnit;
				if (_node){
					_mode="before";
				}
				else{
					_mode="after";
					if (node) _node=node.parentNode;
				}
				break;
			}
			case "before":{
				_node=node;
				_mode="before";
				break;
			}
			case "after":{
				_node=node.nextUnit;
				if (_node){
					_mode="before";
				}
				else{
					_node=getSlideNext(node.parentNode);
					if (_node){
						_mode="before";
					}
					else{
						_mode="end";
					}
				}
				break;
			}
			default:{
				_node=getSlideNext(parentNode);
				if (_node){
					_mode="before";
				}
				else{
					_mode="end";
				}
				break;
			}
		}

		if (!_node){
			_mode="end";
		}
		else{
			var row=_node.row;
		}

		newRow=tree.repeatrow.cloneNode(true);
		switch (_mode){
			case "begin":{
				tree.tBodies[0].insertAdjacentElement("afterBegin", newRow);
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
				tree.tBodies[0].insertAdjacentElement("beforeEnd", newRow);
				break;
			}
		}
		newRow.cells[0].node=newNode;
		newNode.row=newRow;
	}

	pArray_insert(nodes, mode, node, newNode);

	parentNode.hasChild=true;
	refreshTreeNode(parentNode);

	if (tree.currentNode==null) tree.setCurrentNode(newNode);

	var eventName=getElementEventName(tree, "onInitTreeNode");
	fireUserEvent(eventName, [tree, newNode]);

	refreshTreeNode(newNode);
	if (newRow) refreshTreeColor(tree, newRow.rowIndex);
}

function tree_deleteNode(node){
	var tree=this;
	var parentNode=node.parentNode;
	var nodes=parentNode.childNodes;
	tree.collapseNode(node);
	pArray_delete(nodes, node);
	if (node.row){
		var rowIndex=node.row.rowIndex;
		node.row.removeNode(true);
		refreshTreeColor(tree, rowIndex);
		node.row=null;
	}

	if (node==tree.contextMenuNode) tree.contextMenuNode=null;
	if (node==tree.currentNode){
		if (node.level>1){
			tree.setCurrentNode(null);
		}
		else {
			tree.setCurrentNode(parentNode);
		}
	}

	parentNode.hasChild=(parentNode.childNodes.length>0);
	refreshTreeNode(parentNode);
}

function _expandTreeNode(node){
	var tree=node.tree;
	if (!node.dataInited){
		eval("var child_dataset=tree.getAttribute(\"dataset"+(node.level+1)+"\");");
		if (child_dataset){
			if (node.level>0){
				eval("var dataset=tree.getAttribute(\"dataset"+node.level+"\");");
				if (dataset) {
					dataset.setRecord(node.record);
				}
			}
			var record=child_dataset.getFirstRecord();
			while (record){
				tree.addDataNode(node, record);
				record=record.getNextRecord();
			}
		}
		node.dataInited=true;
	}

	var nodes=node.childNodes;
	if (nodes.length>0) {
		var row=node.row;

		var _node=nodes.firstUnit;
		while (_node && (row || node.level==0)){
			var newRow=tree.repeatrow.cloneNode(true);
			if (row)
				row.insertAdjacentElement("afterEnd", newRow);
			else
				tree.tBodies[0].insertAdjacentElement("afterBegin", newRow);

			newRow.cells[0].node=_node;
			_node.row=newRow;
			refreshTreeNode(_node);

			row=newRow;
			_node=_node.nextUnit;
		}

		_node=nodes.firstUnit;
		while (_node){
			if (_node.expanded || _node._expanded) _expandTreeNode(_node);
			_node=_node.nextUnit;
		}
	}
	node.expanded=true;

	node.hasChild=(node.childNodes.length>0);
	if (node.row){
		refreshTreeColor(node.tree, node.row.rowIndex);
		refreshTreeNode(node);
	}
}

function tree_expandNode(node){
	var tree=this;
	if (!node) node=tree.topNode;
	try{
		if (node.expanded) return;
		var eventName=getElementEventName(tree, "beforeExpandNode");
		var event_result=fireUserEvent(eventName, [tree, node]);

		if (event_result) throw event_result;
		_expandTreeNode(node);
		var eventName=getElementEventName(tree, "afterExpandNode");
		fireUserEvent(eventName, [tree, node]);
	}
	catch (e){
		processException(e);
	}
}

function _collapseTreeNode(node){
	if (node.childNodes.length<1) return;

	var _node=node.childNodes.firstUnit;
	while (_node){
		if (_node==_node.tree.currentNode){
			_node.tree.currentNode=null;
		}

		_collapseTreeNode(_node);
		if (_node.row) _node.row.removeNode(true);
		_node.row=null;
		_node=_node.nextUnit;
	}
}

function tree_collapseNode(node){
	try{
		var tree=this;
		if (!node) return;
		if (!node.expanded) return;

		var eventName=getElementEventName(tree, "beforeCollapseNode");
		var event_result=fireUserEvent(eventName, [tree, node]);
		if (event_result) throw event_result;

		_collapseTreeNode(node);

		if (tree.currentNode==null){
			tree.setCurrentNode(node);
		}

		var eventName=getElementEventName(tree, "afterCollapseNode");
		fireUserEvent(eventName, [tree, node]);

		node.expanded=false;
		refreshTreeColor(node.tree, node.row.rowIndex);
		refreshTreeNode(node);
	}
	catch (e){
		processException(e);
	}
}

function tree_clearChildNodes(node){
	function deleteNodes(nodes){
		var unit=nodes.firstUnit;
		var _unit;
		while (unit){
			_unit=unit;
			unit=unit.nextUnit;
			tree.deleteNode(_unit);
		}
	}

	var tree=this;
	if (!node){
		node = tree.topNode;
	}	
	deleteNodes(node.childNodes);
	node.expanded=false;
}

function TreeNodeClick(tree, node){
	if (node && node.expanded){
		tree.collapseNode(node);
	}
	else{
		tree.expandNode(node);
	}
}

function processNodeChanged(){
	var tree=_stored_treeinfo[0];
	var node=_stored_treeinfo[1];
	var eventName=getElementEventName(tree, "afterNodeChange");
	if (isUserEventDefined(eventName))
		fireUserEvent(eventName, _stored_treeinfo);

	if (getValidStr(node.path)!=""){
		open(node.path, tree.getAttribute("target"));
	}
}

function _tree_getCurrentNode() {
	return this.currentNode;
}

function _tree_setCurrentNode(node){
	try{
		var tree=this;
		if (!node) node=tree.topNode.childNodes.firstUnit;

		var eventName=getElementEventName(tree, "beforeNodeChange");
		if (isUserEventDefined(eventName)){
			var event_result=fireUserEvent(eventName, [tree, node]);
			if (event_result) throw event_result;
		}

		var old_node=tree.currentNode;
		var old_row, row;
		if (old_node) old_row=old_node.row;
		if (node) row=node.row;

		if (old_row!=row){
			tree.currentNode=node;
			if (old_row) refreshTreeNodeColor(old_row);
			if (row) refreshTreeNodeColor(row);

			clearTimeout(tree.timeout_id);
			_stored_treeinfo=[tree, node];
			tree.timeout_id=setTimeout("processNodeChanged();", 400);
		}
	}
	catch (e){
		processException(e);
	}
}

function resetRightClickRow(){
	try{
		if (_rightclick_row){
			var tree=getTableByRow(_rightclick_row);
			tree.contextMenuNode=null;
			refreshTreeNodeColor(_rightclick_row);
		}
	}
	catch (e){
		//do nothing
	}
	finally{
		_rightclick_row=null;
	}
}

function _tree_expendclick(button){
	var cell=button.treenode;
	var row=getRowByCell(cell);
	var node=row.cells[0].node;
	var tree=getTableByRow(row);

	TreeNodeClick(tree, node);
	event.cancelBubble=true;
}

function _tree_onmousedown(row){
	if (event.srcElement.id=="_button_expand") return;

	var tree=getTableByRow(row);
	var currentNode=tree.currentNode;
	var node=row.cells[0].node;

	if (event.button==2){
		tree.contextMenuNode=node;
		row.className="row_rightclick";

		try{
			if (_rightclick_row) refreshTreeNodeColor(_rightclick_row);
		}
		catch (e){
			//do nothing
		}
		_rightclick_row=row;
		setTimeout("resetRightClickRow()", 1000);
	}
	else{
		tree.setCurrentNode(node);
	}
}

function processTreeKeyDown(tree, keycode){

	function getCurrentNode(){
		var node=tree.currentNode;
		if (!node){
			var nodes=tree.topNode.childNodes;
			node=nodes.firstUnit;
		}
		return node;
	}

	switch (keycode){
		//Left
		case 37:{
			var node=getCurrentNode();
			if (node && node.hasChild && node.expanded){
				TreeNodeClick(tree, node);
			}
			break;
		}
		//Up
		case 38:{
			var node=getCurrentNode();
			var rowIndex=node.row.rowIndex;
			if (rowIndex>0){
				tree.setCurrentNode(tree.rows[rowIndex-1].cells[0].node);
			}
			break;
		}
		//Right
		case 39:{
			var node=getCurrentNode();
			if (node && node.hasChild && !node.expanded){
				TreeNodeClick(tree, node);
			}
			break;
		}
		//Down
		case 40:{
			var node=getCurrentNode();
			var rowIndex=node.row.rowIndex;
			if (rowIndex+1<tree.rows.length){
				tree.setCurrentNode(tree.rows[rowIndex+1].cells[0].node);
			}
			break;
		}
	}
}

function _tree_onkeydown(tree){
	processTreeKeyDown(tree, event.keyCode);
}

function _tree_checkbox_onClick(){
	var node=event.srcElement.node;
	var tree=node.tree;
	if (node) tree.selectNode(node, !node.checked);
}

function _selectTreeNode(tree, node, checked) {
	if (node.checked != checked) {
		node.checked = checked;
		tree.refreshNode(node);

		var eventName=getElementEventName(tree, "onCheckStateChanged");
		fireUserEvent(eventName, [tree, node]);
	}
}

function tree_selectNode(node, checked) {
	_selectTreeNode(this, node, checked);
}
