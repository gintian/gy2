function _loadChildNode(evt, parentenodestr, childslist, grade, rootgrade,
		catalog_id, parameter) {
	var _childswidth;
	var rectobj = evt.target;
	var x = rectobj.getAttribute("x");
	var y = rectobj.getAttribute("y");
	var SvgDocument = evt.target.ownerDocument;
	var childsnodec = SvgDocument.getElementById(parentenodestr + "c");
	var isloadchildren = childsnodec.getAttribute("name");
	if (isloadchildren != null && isloadchildren.length > 23) {
		var isvisible = childsnodec.getAttribute("style");
		if (isvisible == "visibility:visible") {
			SetAllChildsHidden(SvgDocument, childsnodec);
			moveToOriginOnclickBottomRectLine(SvgDocument, parentenodestr, x,
					y, parameter);
			moveFormatNodesWhenHidden(SvgDocument, parentenodestr, parameter);
		} else {
			SetSunVisible(SvgDocument, childsnodec)
			SetAllChildsAlreadyVisible(SvgDocument, childsnodec);
			moveOnclickBottomRectLine(SvgDocument, parentenodestr, x, y,
					parameter);
			moveFormatNodesWhenVibile(SvgDocument, parentenodestr, parameter);
		}
	} else {
		if (childslist.length == 0) {
			clearOnclickBottomRectLine(SvgDocument, parentenodestr);
		} else {
			//alert(28);
			changewidthandheight(SvgDocument, childslist, grades, parameter);
			createChilds(SvgDocument, parentenodestr, childslist, rootgrade,
					catalog_id, x, y, grade, parameter);
			childsnodec.setAttribute("style", "visibility:visible");
			moveOnclickBottomRectLine(SvgDocument, parentenodestr, x, y,
					parameter);
			moveSiblingNode(SvgDocument, parentenodestr, childslist, parameter);
			moveAndChangeFatherNodeAndLine(SvgDocument, parentenodestr,
				childslist, parameter);
		}
	}
}

function moveFormatNodesWhenVibile(SvgDocument, parentenodestr, parameter) {
	if (parameter.getValue("graphaspect").toString() == 'true') {
		moveFormatFartherNodeWhenVibileupright(SvgDocument, parentenodestr,
				parameter);
		if (isHaveSilbingNode(parentenodestr))
			moveFormatSilbingNodeWhenVibileupright(SvgDocument, parentenodestr);
		if (isHaveParenteNode(parentenodestr))
			moveFormatParentesNodeWhenVibileupright(SvgDocument,
					parentenodestr, parameter);
	} else {
		moveFormatFartherNodeWhenVibilehorizontal(SvgDocument, parentenodestr,
				parameter);
		if (isHaveSilbingNode(parentenodestr))
			moveFormatSilbingNodeWhenVibilehorizontal(SvgDocument,
					parentenodestr);
		if (isHaveParenteNode(parentenodestr))
			moveFormatParentesNodeWhenVibilehorizontal(SvgDocument,
					parentenodestr, parameter);
	}

}
function moveFormatFartherNodeWhenVibileupright(SvgDocument, parentenodestr,
		parameter) {
	var _parentenode;
	var _parentenodename;
	if (isRootNode(parseInt(parentenodestr.substring(1, 3), 10), 0)) {
		var obj = parent.document.getElementById("svgmap");
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenodename = _parentenode.getAttribute("name").substring(16);
		var _viewbox = SvgDocument.getElementById("mainview");
		var viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 " + parseInt(viewVal[2], 10)
				+ " " + (parseInt(_parentenodename, 10) * 2 + 100));
		_parentenode.setAttribute("transform", "translate(0,"
				+ _parentenodename + ")");
		if ((parseInt(_parentenodename, 10) * 2 + 100 + parseInt(parameter
				.getValue("cellhspacewidth"), 10)) < 500)
			obj.height = 500;
		else
			// else
			// if(parseInt(_parentenodename,10)*2 +100
			// +parseInt(spacewidth,10)>16300)
			// obj.width=16300;
			// else
			obj.height = parseInt(_parentenodename, 10) * 2 + 100
					+ parseInt(parameter.getValue("cellhspacewidth"), 10);
	} else {
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenodename = _parentenode.getAttribute("name").substring(16);
		_parentenode.setAttribute("transform", "translate(0,"
				+ _parentenodename + ")");
	}
}
function moveFormatFartherNodeWhenVibilehorizontal(SvgDocument, parentenodestr,
		parameter) {
	var _parentenode;
	var _parentenodename;
	if (isRootNode(parseInt(parentenodestr.substring(1, 3), 10), 0)) {
		var obj = parent.document.getElementById("svgmap");
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenodename = _parentenode.getAttribute("name").substring(16);
		var _viewbox = SvgDocument.getElementById("mainview");
		var viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 "
				+ (parseInt(_parentenodename, 10) * 2 + 100) + " "
				+ parseInt(viewVal[3], 10));
		_parentenode.setAttribute("transform", "translate(" + _parentenodename
				+ ")");
		if ((parseInt(_parentenodename, 10) * 2 + 100 + parseInt(parameter
				.getValue("cellhspacewidth"), 10)) < 633)
			obj.width = 633;
		else if (parseInt(_parentenodename, 10) * 2 + 100
				+ parseInt(parameter.getValue("cellhspacewidth"), 10) > 16300)
			obj.width = 16300;
		else
			obj.width = parseInt(_parentenodename, 10) * 2 + 100
					+ parseInt(parameter.getValue("cellhspacewidth"), 10);
	} else {
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenodename = _parentenode.getAttribute("name").substring(16);
		_parentenode.setAttribute("transform", "translate(" + _parentenodename
				+ ")");
	}
}
function moveFormatSilbingNodeWhenVibileupright(SvgDocument, parentenodestr) {
	var _siblingnodeid;
	var _siblingnode;
	_siblingnodeid = getSiblingID(parentenodestr);
	_siblingnode = SvgDocument.getElementById(_siblingnodeid);
	_siblingnode.setAttribute("transform", "translate(0,"
			+ (parseInt(_siblingnode.getAttribute("name").substring(1), 10))
			+ ")");
}
function moveFormatSilbingNodeWhenVibilehorizontal(SvgDocument, parentenodestr) {
	var _siblingnodeid;
	var _siblingnode;
	_siblingnodeid = getSiblingID(parentenodestr);
	_siblingnode = SvgDocument.getElementById(_siblingnodeid);
	_siblingnode.setAttribute("transform", "translate("
			+ (parseInt(_siblingnode.getAttribute("name").substring(1), 10))
			+ ")");
}
function moveFormatParentesNodeWhenVibileupright(SvgDocument, parentenodestr,
		parameter) {
	var _parentelineid;
	var _parenteline;
	var _siblingid;
	var _siblingnode;
	var _parentenodeid;
	var _curode = SvgDocument.getElementById(parentenodestr + "p");
	_parentenodeid = _curode.getAttribute("name").substring(0, 16);
	var _movespace = parseInt(_curode.getAttribute("name").substring(16), 10);
	var _grades = getGrades(parentenodestr);
	for (var i = 0; i < _grades; i++) {
		_parentenode = SvgDocument.getElementById(_parentenodeid);
		_parentenode.setAttribute("transform",
				"translate(0,"
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) + _movespace) + ")");
		_parentenode.setAttribute("name",
				_parentenode.getAttribute("name").substring(0, 16)
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) + _movespace));

		_siblingid = getSiblingID(_parentenodeid).substring(0, 15);
		_siblingnode = SvgDocument.getElementById(_siblingid);
		_siblingnode.setAttribute("transform",
				"translate(0,"
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) + _movespace * 2) + ")");
		_siblingnode.setAttribute("name",
				_siblingnode.getAttribute("name").substring(0, 1)
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) + _movespace * 2));

		_parenteline = SvgDocument.getElementById(_parentenodeid.substring(0,
				15)
				+ "l");
		if (isFirstNode(parentenodestr)) {
			_parenteline.setAttribute("y1", (parseInt(_parenteline
					.getAttribute("y1"), 10) + _movespace));
		}
		if (isLastNode(parentenodestr)) {
			_parenteline.setAttribute("y2", (parseInt(_parenteline
					.getAttribute("y2"), 10) + _movespace));
		} else {
			_parenteline.setAttribute("y2", (parseInt(_parenteline
					.getAttribute("y2"), 10) + _movespace * 2));
		}
		parentenodestr = _parentenodeid;
		_parentenodeid = _parentenode.getAttribute("name").substring(0, 16);
	}
	visbilechangewidthandheight(SvgDocument, _movespace, parameter);
}
function moveFormatParentesNodeWhenVibilehorizontal(SvgDocument,
		parentenodestr, parameter) {
	var _parentelineid;
	var _parenteline;
	var _siblingid;
	var _siblingnode;
	var _parentenodeid;
	var _curode = SvgDocument.getElementById(parentenodestr + "p");
	_parentenodeid = _curode.getAttribute("name").substring(0, 16);
	var _movespace = parseInt(_curode.getAttribute("name").substring(16), 10);
	var _grades = getGrades(parentenodestr);
	for (var i = 0; i < _grades; i++) {
		_parentenode = SvgDocument.getElementById(_parentenodeid);
		_parentenode.setAttribute("transform",
				"translate("
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) + _movespace) + ")");
		_parentenode.setAttribute("name",
				_parentenode.getAttribute("name").substring(0, 16)
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) + _movespace));

		_siblingid = getSiblingID(_parentenodeid).substring(0, 15);
		_siblingnode = SvgDocument.getElementById(_siblingid);
		_siblingnode.setAttribute("transform",
				"translate("
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) + _movespace * 2) + ")");
		_siblingnode.setAttribute("name",
				_siblingnode.getAttribute("name").substring(0, 1)
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) + _movespace * 2));

		_parenteline = SvgDocument.getElementById(_parentenodeid.substring(0,
				15)
				+ "l");
		if (isFirstNode(parentenodestr)) {
			_parenteline.setAttribute("x1", (parseInt(_parenteline
					.getAttribute("x1"), 10) + _movespace));
		}
		if (isLastNode(parentenodestr)) {
			_parenteline.setAttribute("x2", (parseInt(_parenteline
					.getAttribute("x2"), 10) + _movespace));
		} else {
			_parenteline.setAttribute("x2", (parseInt(_parenteline
					.getAttribute("x2"), 10) + _movespace * 2));
		}
		parentenodestr = _parentenodeid;
		_parentenodeid = _parentenode.getAttribute("name").substring(0, 16);
	}
	visbilechangewidthandheight(SvgDocument, _movespace, parameter);
}
function visbilechangewidthandheight(SvgDocument, _movespace, parameter) {
	if (parameter.getValue("graphaspect").toString() == 'true') {
		visbilechangewidthandheightupright(SvgDocument, _movespace, parameter);
	} else {
		visbilechangewidthandheighthorizontal(SvgDocument, _movespace,
				parameter);
	}
}
function visbilechangewidthandheightupright(SvgDocument, _movespace, parameter) {
	var obj = parent.document.getElementById("svgmap");
	var _gchilds = SvgDocument.getElementById("gchildswidth");
	if (_gchilds != null) {
		_gchilds.setAttribute("name",
				_gchilds.getAttribute("name").substring(0, 1)
						+ (parseInt(_gchilds.getAttribute("name").substring(1),
								10) + parseInt(_movespace, 10) * 2));
		if (parseInt(_gchilds.getAttribute("name").substring(1), 10) > 500)
			// if(parseInt(_gchilds.getAttribute("name").substring(1),10)>16300)
			// obj.width=16300;
			// else
			obj.height = parseInt(_gchilds.getAttribute("name").substring(1),
					10)
					+ parseInt(parameter.getValue("cellhspacewidth"), 10);
		else
			obj.height = 500;
		var _viewbox = SvgDocument.getElementById("mainview");
		var _viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 " + _viewVal[2] + " "
				+ (parseInt(_viewVal[3], 10) + parseInt(_movespace, 10) * 2));
	}
}
function visbilechangewidthandheighthorizontal(SvgDocument, _movespace,
		parameter) {
	var obj = parent.document.getElementById("svgmap");
	var _gchilds = SvgDocument.getElementById("gchildswidth");
	if (_gchilds != null) {
		_gchilds.setAttribute("name",
				_gchilds.getAttribute("name").substring(0, 1)
						+ (parseInt(_gchilds.getAttribute("name").substring(1),
								10) + parseInt(_movespace, 10) * 2));
		if (parseInt(_gchilds.getAttribute("name").substring(1), 10) > 633)
			if (parseInt(_gchilds.getAttribute("name").substring(1), 10) > 16300)
				obj.width = 16300;
			else
				obj.width = parseInt(
						_gchilds.getAttribute("name").substring(1), 10)
						+ parseInt(parameter.getValue("cellhspacewidth"), 10);
		else
			obj.width = 633;
		var _viewbox = SvgDocument.getElementById("mainview");
		var _viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 "
				+ (parseInt(_viewVal[2], 10) + parseInt(_movespace, 10) * 2)
				+ " " + _viewVal[3]);
	}
}

function SetSunVisible(SvgDocument, childsnodec) {
	var _childid;
	var _childnode;
	var _brotherid;
	childsnodec.setAttribute("style", "visibility:visible");
	childsnodec.setAttribute("name", childsnodec.getAttribute("name")
			.substring(0, 23)
			+ "visible");
}
function SetAllChildsAlreadyVisible(SvgDocument, childsnodec) {
	var _childid;
	var _childnode;
	var _brotherid;
	if(childsnodec.getAttribute("name")==null){
		childsnodec.setAttribute("name","");
	}
	if (childsnodec.getAttribute("name").substring(23) == "visible0"){
		childsnodec.setAttribute("style", "visibility:visible");
	}
	if (childsnodec.getAttribute("name").length >= 30
			&& childsnodec.getAttribute("name").substring(23, 30) == "visible") {
		if ((parseInt(childsnodec.getAttribute("name").substring(30), 10) - 1) >= 0){
			childsnodec.setAttribute("name", childsnodec.getAttribute("name")
					.substring(0, 30)
					+ (parseInt(childsnodec.getAttribute("name").substring(30),
							10) - 1));
		}
	}

	_childnode = SvgDocument.getElementById(childsnodec.getAttribute("name")
			.substring(0, 16));
	_childid = childsnodec.getAttribute("name").substring(0, 16);
	for (var i = 0; i < parseInt(_childid.substring(11, 15), 10); i++) {
		_brotherid = _childid.substring(0, 7)
				+ getFourCoding(parseInt(_childid.substring(7, 11), 10) + i)
				+ _childid.substring(11, 16);
		SetAllChildsAlreadyVisible(SvgDocument, SvgDocument
				.getElementById(_brotherid));
	}
}

function moveFormatNodesWhenHidden(SvgDocument, parentenodestr, parameter) {
	if (parameter.getValue("graphaspect").toString() == 'true') {
		moveFormatFartherNodeWhenHiddenupright(SvgDocument, parentenodestr);
		if (isHaveSilbingNode(parentenodestr))
			moveFormatSilbingNodeWhenHiddenupright(SvgDocument, parentenodestr);
		if (isHaveParenteNode(parentenodestr))
			moveFormatParentesNodeWhenHiddenupright(SvgDocument,
					parentenodestr, parameter);
	} else {
		moveFormatFartherNodeWhenHiddenhorizontal(SvgDocument, parentenodestr);
		if (isHaveSilbingNode(parentenodestr))
			moveFormatSilbingNodeWhenHiddenhorizontal(SvgDocument,
					parentenodestr);
		if (isHaveParenteNode(parentenodestr))
			moveFormatParentesNodeWhenHiddenhorizontal(SvgDocument,
					parentenodestr, parameter);
	}

}
function moveFormatFartherNodeWhenHiddenupright(SvgDocument, parentenodestr) {
	var _parentenode;
	if (isRootNode(parseInt(parentenodestr.substring(1, 3), 10), 0)) {
		var obj = parent.document.getElementById("svgmap");
		var _viewbox = SvgDocument.getElementById("mainview");
		var viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 " + parseInt(viewVal[2], 10)
				+ " 140");
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenode.setAttribute("transform", "translate(0)");
		obj.height = 500;
	} else {
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenode.setAttribute("transform", "translate(0)");
	}
}
function moveFormatFartherNodeWhenHiddenhorizontal(SvgDocument, parentenodestr) {
	var _parentenode;
	if (isRootNode(parseInt(parentenodestr.substring(1, 3), 10), 0)) {
		var obj = parent.document.getElementById("svgmap");
		var _viewbox = SvgDocument.getElementById("mainview");
		var viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 100 " + parseInt(viewVal[3], 10));
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenode.setAttribute("transform", "translate(0)");
		obj.width = 633;
	} else {
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		_parentenode.setAttribute("transform", "translate(0)");
	}
}
function moveFormatSilbingNodeWhenHiddenupright(SvgDocument, parentenodestr) {
	var _siblingnodeid;
	var _siblingnode;
	_siblingnodeid = getSiblingID(parentenodestr);
	_siblingnode = SvgDocument.getElementById(_siblingnodeid);
	_siblingnode.setAttribute("transform", "translate(0)");
}
function moveFormatSilbingNodeWhenHiddenhorizontal(SvgDocument, parentenodestr) {
	var _siblingnodeid;
	var _siblingnode;
	_siblingnodeid = getSiblingID(parentenodestr);
	_siblingnode = SvgDocument.getElementById(_siblingnodeid);
	_siblingnode.setAttribute("transform", "translate(0)");
}
function hiddenchangewidthandheight(SvgDocument, _movespace, parameter) {
	if (parameter.getValue("graphaspect").toString() == 'true') {
		hiddenchangewidthandheightupright(SvgDocument, _movespace, parameter);
	} else {
		hiddenchangewidthandheighthorizontal(SvgDocument, _movespace, parameter);
	}
}
function hiddenchangewidthandheightupright(SvgDocument, _movespace, parameter) {
	var obj = parent.document.getElementById("svgmap");
	var _gchilds = SvgDocument.getElementById("gchildswidth");
	if (_gchilds != null) {
		_gchilds.setAttribute("name",
				_gchilds.getAttribute("name").substring(0, 1)
						+ (parseInt(_gchilds.getAttribute("name").substring(1),
								10) - parseInt(_movespace, 10) * 2));
		if (parseInt(_gchilds.getAttribute("name").substring(1), 10) > 500)
			obj.height = parseInt(_gchilds.getAttribute("name").substring(1),
					10)
					+ parseInt(parameter.getValue("cellhspacewidth"), 10);
		else
			obj.height = 500;

		var _viewbox = SvgDocument.getElementById("mainview");
		var _viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 " + _viewVal[2] + " "
				+ (parseInt(_viewVal[3], 10) - parseInt(_movespace, 10) * 2));
	}
}
function hiddenchangewidthandheighthorizontal(SvgDocument, _movespace,
		parameter) {
	var obj = parent.document.getElementById("svgmap");
	var _gchilds = SvgDocument.getElementById("gchildswidth");
	if (_gchilds != null) {
		_gchilds.setAttribute("name",
				_gchilds.getAttribute("name").substring(0, 1)
						+ (parseInt(_gchilds.getAttribute("name").substring(1),
								10) - parseInt(_movespace, 10) * 2));
		if (parseInt(_gchilds.getAttribute("name").substring(1), 10) > 633)
			obj.width = parseInt(_gchilds.getAttribute("name").substring(1), 10)
					+ parseInt(parameter.getValue("cellhspacewidth"), 10);
		else
			obj.width = 633;

		var _viewbox = SvgDocument.getElementById("mainview");
		var _viewVal = _viewbox.getAttribute("viewBox").split(" ");
		_viewbox.setAttribute("viewBox", "0 0 "
				+ (parseInt(_viewVal[2], 10) - parseInt(_movespace, 10) * 2)
				+ " " + _viewVal[3]);
	}
}
function moveFormatParentesNodeWhenHiddenupright(SvgDocument, parentenodestr,
		parameter) {
	var _parentelineid;
	var _parenteline;
	var _siblingid;
	var _siblingnode;
	var _parentenodeid;
	var _curode = SvgDocument.getElementById(parentenodestr + "p");
	_parentenodeid = _curode.getAttribute("name").substring(0, 16);
	var _movespace = parseInt(_curode.getAttribute("name").substring(16), 10);
	var _grades = getGrades(parentenodestr);
	for (var i = 0; i < _grades; i++) {
		_parentenode = SvgDocument.getElementById(_parentenodeid);
		_parentenode.setAttribute("transform",
				"translate(0,"
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) - _movespace) + ")");
		_parentenode.setAttribute("name",
				_parentenode.getAttribute("name").substring(0, 16)
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) - _movespace));

		_siblingid = getSiblingID(_parentenodeid).substring(0, 15);
		_siblingnode = SvgDocument.getElementById(_siblingid);
		_siblingnode.setAttribute("transform",
				"translate(0,"
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) - _movespace * 2) + ")");
		_siblingnode.setAttribute("name",
				_siblingnode.getAttribute("name").substring(0, 1)
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) - _movespace * 2));

		_parenteline = SvgDocument.getElementById(_parentenodeid.substring(0,
				15)
				+ "l");
		if (isFirstNode(parentenodestr)) {
			_parenteline.setAttribute("y1", (parseInt(_parenteline
					.getAttribute("y1"), 10) - _movespace));
		}
		if (isLastNode(parentenodestr)) {
			_parenteline.setAttribute("y2", (parseInt(_parenteline
					.getAttribute("y2"), 10) - _movespace));
		} else {
			_parenteline.setAttribute("y2", (parseInt(_parenteline
					.getAttribute("y2"), 10) - _movespace * 2));
		}
		parentenodestr = _parentenodeid;
		_parentenodeid = _parentenode.getAttribute("name").substring(0, 16);
	}
	hiddenchangewidthandheight(SvgDocument, _movespace, parameter);
}
function moveFormatParentesNodeWhenHiddenhorizontal(SvgDocument,
		parentenodestr, parameter) {

	var _parentelineid;
	var _parenteline;
	var _siblingid;
	var _siblingnode;
	var _parentenodeid;
	var _curode = SvgDocument.getElementById(parentenodestr + "p");
	_parentenodeid = _curode.getAttribute("name").substring(0, 16);
	var _movespace = parseInt(_curode.getAttribute("name").substring(16), 10);
	var _grades = getGrades(parentenodestr);
	for (var i = 0; i < _grades; i++) {
		_parentenode = SvgDocument.getElementById(_parentenodeid);
		_parentenode.setAttribute("transform",
				"translate("
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) - _movespace) + ")");
		_parentenode.setAttribute("name",
				_parentenode.getAttribute("name").substring(0, 16)
						+ (parseInt(_parentenode.getAttribute("name")
								.substring(16), 10) - _movespace));

		_siblingid = getSiblingID(_parentenodeid).substring(0, 15);
		_siblingnode = SvgDocument.getElementById(_siblingid);
		_siblingnode.setAttribute("transform",
				"translate("
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) - _movespace * 2) + ")");
		_siblingnode.setAttribute("name",
				_siblingnode.getAttribute("name").substring(0, 1)
						+ (parseInt(_siblingnode.getAttribute("name")
								.substring(1), 10) - _movespace * 2));

		_parenteline = SvgDocument.getElementById(_parentenodeid.substring(0,
				15)
				+ "l");
		if (isFirstNode(parentenodestr)) {
			_parenteline.setAttribute("x1", (parseInt(_parenteline
					.getAttribute("x1"), 10) - _movespace));
		}
		if (isLastNode(parentenodestr)) {
			_parenteline.setAttribute("x2", (parseInt(_parenteline
					.getAttribute("x2"), 10) - _movespace));
		} else {
			_parenteline.setAttribute("x2", (parseInt(_parenteline
					.getAttribute("x2"), 10) - _movespace * 2));
		}
		parentenodestr = _parentenodeid;
		_parentenodeid = _parentenode.getAttribute("name").substring(0, 16);
	}
	hiddenchangewidthandheight(SvgDocument, _movespace, parameter);
}

function moveToOriginOnclickBottomRectLine(SvgDocument, parentenodestr, x, y,
		parameter) {
	var _node;
	_node = SvgDocument.getElementById("l" + parentenodestr.substring(1));
	if (parameter.getValue("graphaspect").toString() == 'true') {
		_node.setAttribute("x1", x);
		_node.setAttribute("x2", (parseInt(x, 10) + parseInt(parameter
				.getValue("rectwidth"), 10)));
	} else {
		_node.setAttribute("y1", y);
		_node.setAttribute("y2", (parseInt(y, 10) + parseInt(parameter
				.getValue("rectwidth"), 10)));
	}
}

function SetAllChildsHidden(SvgDocument, childsnodec) {
	var _childid;
	var _childnode;
	var _brotherid;
	if(childsnodec.getAttribute("name")==null){
		childsnodec.setAttribute("name","");
	}
	if (childsnodec.getAttribute("style") == "visibility:visible") {
		childsnodec.setAttribute("name", childsnodec.getAttribute("name")
				.substring(0, 23)
				+ "visible0");
	} else {
		if (childsnodec.getAttribute("name")!=null&&childsnodec.getAttribute("name").length >= 23
				&& childsnodec.getAttribute("name").substring(23, 30) == "visible") {
			childsnodec.setAttribute("name", childsnodec.getAttribute("name")
					.substring(0, 30)
					+ (parseInt(childsnodec.getAttribute("name").substring(30),
							10) + 1));
		} else {
			childsnodec.setAttribute("name", childsnodec.getAttribute("name")
					.substring(0, 23)
					+ "hidden");
		}
	}

	childsnodec.setAttribute("style", "visibility:hidden");
	_childnode = SvgDocument.getElementById(childsnodec.getAttribute("name")
			.substring(0, 16));
	_childid = childsnodec.getAttribute("name").substring(0, 16);
	for (var i = 0; i < parseInt(_childid.substring(11, 15), 10); i++) {
		_brotherid = _childid.substring(0, 7)
				+ getFourCoding(parseInt(_childid.substring(7, 11), 10) + i)
				+ _childid.substring(11, 16);
		SetAllChildsHidden(SvgDocument, SvgDocument.getElementById(_brotherid));
	}
}

function clearOnclickBottomRectLine(SvgDocument, parentenodestr) {
	var _lnode;
	var _rectnode;
	var _llnode;
	_lnode = SvgDocument.getElementById("l" + parentenodestr.substring(1));
	_llnode = SvgDocument.getElementById("ll" + parentenodestr.substring(1));
	_rectnode = SvgDocument.getElementById("c" + parentenodestr.substring(1));
	if (_lnode)
		_lnode.setAttribute("style", "visibility:hidden");
	if (_llnode)
		_llnode.setAttribute("style", "visibility:hidden");
	if (_rectnode)
		_rectnode.setAttribute("style", "visibility:hidden");
}
function moveParenteSiblingNode(SvgDocument, parentenodestr, childslist,
		parameter) {
	var _siblingid;
	var _siblingnode;
	_siblingid = getSiblingID(parentenodestr);
	_siblingnode = SvgDocument.getElementById(_siblingid);
	if (_siblingnode != null) {
		if (parameter.getValue("graphaspect").toString() == 'true') {
			_siblingnode
					.setAttribute(
							"transform",
							"translate(0,"
									+ (parseInt(_siblingnode
											.getAttribute("name").substring(1),
											10) + (parseInt(childslist.length,
											10) - 1)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))) + ")");
			_siblingnode
					.setAttribute(
							"name",
							_siblingnode.getAttribute("name").substring(0, 1)
									+ (parseInt(_siblingnode
											.getAttribute("name").substring(1),
											10) + (parseInt(childslist.length,
											10) - 1)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))));
		} else {
			_siblingnode
					.setAttribute(
							"transform",
							"translate("
									+ (parseInt(_siblingnode
											.getAttribute("name").substring(1),
											10) + (parseInt(childslist.length,
											10) - 1)
											* (parseInt(parameter
													.getValue("cellwidth"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))) + ")");
			_siblingnode
					.setAttribute(
							"name",
							_siblingnode.getAttribute("name").substring(0, 1)
									+ (parseInt(_siblingnode
											.getAttribute("name").substring(1),
											10) + (parseInt(childslist.length,
											10) - 1)
											* (parseInt(parameter
													.getValue("cellwidth"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))));
		}
	}

}

function moveAndChangeGrandfatherNodes(SvgDocument, parentenodestr, childslist,
		parameter) {
	var _parenteline;
	var _parentenode;
	var _parentelineid;
	var _grades = getGrades(parentenodestr);
	if (parameter.getValue("graphaspect").toString() == 'true') {
		for (var i = 0; i < _grades + 1; i++) {
			_parentenode = SvgDocument.getElementById(parentenodestr + "p");
			if (_parentenode != null) {
				_parentelineid = _parentenode.getAttribute("name").substring(0,
						15);
				_parenteline = SvgDocument.getElementById(_parentelineid + "l");
				if (_parenteline != null) {
					if (isFirstNode(parentenodestr)) {
						if (!isRootNode(_grades, i)
								&& isHaveSilbingNode(parentenodestr)
								&& !(_parentelineid == parentenodestr && parentenodestr == "g00000000000000")) {
							_parenteline
									.setAttribute(
											"y1",
											parseInt(_parenteline
													.getAttribute("y1"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
							_parenteline
									.setAttribute(
											"y2",
											parseInt(_parenteline
													.getAttribute("y2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						}
						_parentenode
								.setAttribute(
										"name",
										_parentenode.getAttribute("name")
												.substring(0, 16)
												+ (parseInt(_parentenode
														.getAttribute("name")
														.substring(16), 10) + (parseInt(
														childslist.length, 10) - 1)
														* (parseInt(
																parameter
																		.getValue("cellheight"),
																10) + parseInt(
																parameter
																		.getValue("cellhspacewidth"),
																10)) / 2));
						_parentenode.setAttribute("transform", "translate(0,"
								+ _parentenode.getAttribute("name")
										.substring(16) + ")");
						moveParenteSiblingNode(SvgDocument, parentenodestr,
								childslist, parameter);
					} else if (isLastNode(parentenodestr)) {
						if (!isRootNode(_grades, i)
								&& isHaveSilbingNode(parentenodestr)
								&& !(_parentelineid == parentenodestr && parentenodestr == "g00000000000000")) {
							_parenteline
									.setAttribute(
											"y2",
											parseInt(_parenteline
													.getAttribute("y2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
						}
						_parentenode
								.setAttribute(
										"name",
										_parentenode.getAttribute("name")
												.substring(0, 16)
												+ (parseInt(_parentenode
														.getAttribute("name")
														.substring(16), 10) + (parseInt(
														childslist.length, 10) - 1)
														* (parseInt(
																parameter
																		.getValue("cellheight"),
																10) + parseInt(
																parameter
																		.getValue("cellhspacewidth"),
																10)) / 2));
						_parentenode.setAttribute("transform", "translate(0,"
								+ _parentenode.getAttribute("name")
										.substring(16) + ")");
						moveParenteSiblingNode(SvgDocument, parentenodestr,
								childslist, parameter);
					} else {
						if (!isRootNode(_grades, i)
								&& isHaveSilbingNode(parentenodestr)
								&& !(_parentelineid == parentenodestr && parentenodestr == "g00000000000000")) {
							_parenteline
									.setAttribute(
											"y2",
											parseInt(_parenteline
													.getAttribute("y2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						}
						_parentenode
								.setAttribute(
										"name",
										_parentenode.getAttribute("name")
												.substring(0, 16)
												+ (parseInt(_parentenode
														.getAttribute("name")
														.substring(16), 10) + (parseInt(
														childslist.length, 10) - 1)
														* (parseInt(
																parameter
																		.getValue("cellheight"),
																10) + parseInt(
																parameter
																		.getValue("cellhspacewidth"),
																10)) / 2));
						_parentenode.setAttribute("transform", "translate(0,"
								+ _parentenode.getAttribute("name")
										.substring(16) + ")");
						moveParenteSiblingNode(SvgDocument, parentenodestr,
								childslist, parameter);
					}
				}
			}
			parentenodestr = _parentelineid;
		}
	} else {
		for (var i = 0; i < _grades + 1; i++) {
			_parentenode = SvgDocument.getElementById(parentenodestr + "p");
			if (_parentenode != null) {
				_parentelineid = _parentenode.getAttribute("name").substring(0,
						15);
				_parenteline = SvgDocument.getElementById(_parentelineid + "l");
				if (_parenteline != null) {
					if (isFirstNode(parentenodestr)) {
						if (!isRootNode(_grades, i)
								&& isHaveSilbingNode(parentenodestr)
								&& !(_parentelineid == parentenodestr && parentenodestr == "g00000000000000")) {
							_parenteline
									.setAttribute(
											"x1",
											parseInt(_parenteline
													.getAttribute("x1"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
							_parenteline
									.setAttribute(
											"x2",
											parseInt(_parenteline
													.getAttribute("x2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						}
						_parentenode
								.setAttribute(
										"name",
										_parentenode.getAttribute("name")
												.substring(0, 16)
												+ (parseInt(_parentenode
														.getAttribute("name")
														.substring(16), 10) + (parseInt(
														childslist.length, 10) - 1)
														* (parseInt(
																parameter
																		.getValue("cellwidth"),
																10) + parseInt(
																parameter
																		.getValue("cellhspacewidth"),
																10)) / 2));
						_parentenode.setAttribute("transform", "translate("
								+ _parentenode.getAttribute("name")
										.substring(16) + ")");
						moveParenteSiblingNode(SvgDocument, parentenodestr,
								childslist, parameter);
					} else if (isLastNode(parentenodestr)) {
						if (!isRootNode(_grades, i)
								&& isHaveSilbingNode(parentenodestr)
								&& !(_parentelineid == parentenodestr && parentenodestr == "g00000000000000")) {
							_parenteline
									.setAttribute(
											"x2",
											parseInt(_parenteline
													.getAttribute("x2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
						}
						_parentenode
								.setAttribute(
										"name",
										_parentenode.getAttribute("name")
												.substring(0, 16)
												+ (parseInt(_parentenode
														.getAttribute("name")
														.substring(16), 10) + (parseInt(
														childslist.length, 10) - 1)
														* (parseInt(
																parameter
																		.getValue("cellwidth"),
																10) + parseInt(
																parameter
																		.getValue("cellhspacewidth"),
																10)) / 2));
						_parentenode.setAttribute("transform", "translate("
								+ _parentenode.getAttribute("name")
										.substring(16) + ")");
						moveParenteSiblingNode(SvgDocument, parentenodestr,
								childslist, parameter);
					} else {
						if (!isRootNode(_grades, i)
								&& isHaveSilbingNode(parentenodestr)
								&& !(_parentelineid == parentenodestr && parentenodestr == "g00000000000000")) {
							_parenteline
									.setAttribute(
											"x2",
											parseInt(_parenteline
													.getAttribute("x2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						}
						_parentenode
								.setAttribute(
										"name",
										_parentenode.getAttribute("name")
												.substring(0, 16)
												+ (parseInt(_parentenode
														.getAttribute("name")
														.substring(16), 10) + (parseInt(
														childslist.length, 10) - 1)
														* (parseInt(
																parameter
																		.getValue("cellwidth"),
																10) + parseInt(
																parameter
																		.getValue("cellhspacewidth"),
																10)) / 2));
						_parentenode.setAttribute("transform", "translate("
								+ _parentenode.getAttribute("name")
										.substring(16) + ")");
						moveParenteSiblingNode(SvgDocument, parentenodestr,
								childslist, parameter);
					}
				}
			}
			parentenodestr = _parentelineid;
		}
	}
}
function moveAndChangeFatherNodeAndLine(SvgDocument, parentenodestr,
		childslist, parameter) {
	var _parenteline;
	var _parentenode;
	var _parentelineid;
	if (parameter.getValue("graphaspect").toString() == 'true') {
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		if (_parentenode != null) {
			_parentelineid = _parentenode.getAttribute("name").substring(0, 15);
			_parenteline = SvgDocument.getElementById(_parentelineid + "l");
			if (_parenteline != null) {
				if (!isRootNode(getGrades(parentenodestr), 0)) {
					_parentenode
							.setAttribute(
									"transform",
									"translate(0,"
											+ (parseInt(childslist.length, 10) - 1)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10)) / 2 + ")");
					_parentenode
							.setAttribute(
									"name",
									_parentenode.getAttribute("name")
											.substring(0, 16)
											+ (parseInt(childslist.length, 10) - 1)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10)) / 2);
					if (isHaveSilbingNode(parentenodestr))
						if (isFirstNode(parentenodestr)) {
							_parenteline
									.setAttribute(
											"y1",
											parseInt(_parenteline
													.getAttribute("y1"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
							_parenteline
									.setAttribute(
											"y2",
											parseInt(_parenteline
													.getAttribute("y2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						} else if (isLastNode(parentenodestr)) {
							_parenteline
									.setAttribute(
											"y2",
											parseInt(_parenteline
													.getAttribute("y2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
						} else {
							_parenteline
									.setAttribute(
											"y2",
											parseInt(_parenteline
													.getAttribute("y2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellheight"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						}
				}
			}
		}
		moveAndChangeGrandfatherNodes(SvgDocument, _parentelineid, childslist,
				parameter)
	} else {
		_parentenode = SvgDocument.getElementById(parentenodestr + "p");
		if (_parentenode != null) {
			_parentelineid = _parentenode.getAttribute("name").substring(0, 15);
			_parenteline = SvgDocument.getElementById(_parentelineid + "l");
			if (_parenteline != null) {
				if (!isRootNode(getGrades(parentenodestr), 0)) {
					_parentenode
							.setAttribute(
									"transform",
									"translate("
											+ (parseInt(childslist.length, 10) - 1)
											* (parseInt(parameter
													.getValue("cellwidth"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10)) / 2 + ")");
					_parentenode
							.setAttribute(
									"name",
									_parentenode.getAttribute("name")
											.substring(0, 16)
											+ (parseInt(childslist.length, 10) - 1)
											* (parseInt(parameter
													.getValue("cellwidth"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10)) / 2);
					if (isHaveSilbingNode(parentenodestr))
						if (isFirstNode(parentenodestr)) {
							_parenteline
									.setAttribute(
											"x1",
											parseInt(_parenteline
													.getAttribute("x1"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
							_parenteline
									.setAttribute(
											"x2",
											parseInt(_parenteline
													.getAttribute("x2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						} else if (isLastNode(parentenodestr)) {
							_parenteline
									.setAttribute(
											"x2",
											parseInt(_parenteline
													.getAttribute("x2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)) / 2);
						} else {
							_parenteline
									.setAttribute(
											"x2",
											parseInt(_parenteline
													.getAttribute("x2"), 10)
													+ (parseInt(
															childslist.length,
															10) - 1)
													* (parseInt(
															parameter
																	.getValue("cellwidth"),
															10) + parseInt(
															parameter
																	.getValue("cellhspacewidth"),
															10)));
						}
				}
			}
		}
		moveAndChangeGrandfatherNodes(SvgDocument, _parentelineid, childslist,
				parameter)
	}
}

function moveSiblingNode(SvgDocument, parentenodestr, childslist, parameter) {
	var _siblingid;
	var _siblingnode;
	var _siblingnodep;
	_siblingid = getSiblingID(parentenodestr);
	_siblingnode = SvgDocument.getElementById(_siblingid);
	if (_siblingnode != null) {
		if (parameter.getValue("graphaspect").toString() == 'true') {
			_siblingnode
					.setAttribute(
							"transform",
							"translate(0,"
									+ (parseInt(childslist.length, 10) - 1)
									* (parseInt(parameter
											.getValue("cellheight"), 10) + parseInt(
											parameter
													.getValue("cellhspacewidth"),
											10)) + ")");
			_siblingnode
					.setAttribute(
							"name",
							"x"
									+ (parseInt(childslist.length, 10) - 1)
									* (parseInt(parameter
											.getValue("cellheight"), 10) + parseInt(
											parameter
													.getValue("cellhspacewidth"),
											10)));
		} else {
			_siblingnode
					.setAttribute(
							"transform",
							"translate("
									+ (parseInt(childslist.length, 10) - 1)
									* (parseInt(
											parameter.getValue("cellwidth"), 10) + parseInt(
											parameter
													.getValue("cellhspacewidth"),
											10)) + ")");
			_siblingnode
					.setAttribute(
							"name",
							"x"
									+ (parseInt(childslist.length, 10) - 1)
									* (parseInt(
											parameter.getValue("cellwidth"), 10) + parseInt(
											parameter
													.getValue("cellhspacewidth"),
											10)));
		}
	}
}

function moveOnclickBottomRectLine(SvgDocument, parentenodestr, x, y, parameter) {
	var _node;
	if (parameter.getValue("graphaspect").toString() == 'true') {
		_node = SvgDocument.getElementById("l" + parentenodestr.substring(1));
		_node
				.setAttribute("x1", (parseInt(parameter.getValue("rectwidth"),
						10) + parseInt(x, 10)));
		_node.setAttribute("x2", (parseInt(parameter.getValue("rectwidth"), 10)
				+ parseInt(x, 10) + parseInt(parameter
				.getValue("cellvspacewidth"), 10)));
	} else {
		_node = SvgDocument.getElementById("l" + parentenodestr.substring(1));
		_node
				.setAttribute("y1", (parseInt(parameter.getValue("rectwidth"),
						10) + parseInt(y, 10)));
		_node.setAttribute("y2", (parseInt(parameter.getValue("rectwidth"), 10)
				+ parseInt(y, 10) + parseInt(parameter
				.getValue("cellvspacewidth"), 10)));
	}
}

function createChilds(SvgDocument, parentenodestr, childslist, rootgrade,
		catalog_id, x, y, grade, parameter) {
	if (parameter.getValue("graphaspect").toString() == 'true') {
		createChildsisupright(SvgDocument, parentenodestr, childslist, grade,
				rootgrade, catalog_id, x, y, parameter);
	} else {
		createChildshorizontal(SvgDocument, parentenodestr, childslist, grade,
				rootgrade, catalog_id, x, y, parameter);
	}
}
function createChildshorizontal(SvgDocument, parentenodestr, childslist, grade,
		rootgrade, catalog_id, x, y, parameter) {
	var _childgid;
	var _childgpid;
	var _childgcid;
	var _childg;
	var _childgp;
	var _childgc;
	var _childtopline;
	var _rectnode;
	var _childnextnode;
	var _childnextnodeid;
	var _childgctemp;
	var _codeitemobj;
	var _isperson;
	var _graph3dright;
	var _graph3dtop;
	var _topy1;
	var parentenodec = SvgDocument.getElementById(parentenodestr + "c");

	createchildnodeline(SvgDocument, parentenodestr, parentenodec,
			childslist.length, x, y, parameter)
	_childgctemp = parentenodec;
	for (var i = 0; i < parseInt(childslist.length, 10); i++) {

		_codeitemobj = childslist[i];

		_childgid = getChildGID(parentenodestr, grade, i + 1, childslist.length);

		var _childg = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childg.setAttribute("id", _childgid);
		_childg.setAttribute("name", "x"
				+ (parseInt(childslist.length, 10) - 1)
				* (parseInt(parameter.getValue("cellwidth"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10)));
		parentenodec.appendChild(_childg);

		_childgpid = getChildGPID(parentenodestr, grade, i + 1,
				childslist.length);
		_childgp = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childgp.setAttribute("id", _childgpid);

		_childgp.setAttribute("name", parentenodestr
				+ "p"
				+ (parseInt(childslist.length, 10) - 1)
				* (parseInt(parameter.getValue("cellwidth"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10)) / 2);
		_childg.appendChild(_childgp);
		if (parameter.getValue("graph3d").toString() == 'true')
			_topy1 = (parseInt(y, 10)
					+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
					parameter.getValue("cellvspacewidth"), 10)
					* 2)
					- parseInt(parameter.getValue("cellheight"), 10) / 12;
		else
			_topy1 = (parseInt(y, 10)
					+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
					parameter.getValue("cellvspacewidth"), 10)
					* 2);

		_childtopline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
		_childtopline.setAttribute("x1",
				(i
						* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
								.getValue("cellhspacewidth"), 10))
						+ parseInt(x, 10) + parseInt(parameter
						.getValue("rectwidth"), 10)
						/ 2));
		_childtopline.setAttribute("y1", (parseInt(y, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
				parameter.getValue("cellvspacewidth"), 10)));
		_childtopline.setAttribute("x2",
				(i
						* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
								.getValue("cellhspacewidth"), 10))
						+ parseInt(x, 10) + parseInt(parameter
						.getValue("rectwidth"), 10)
						/ 2));
		_childtopline.setAttribute("y2", _topy1+5);
		_childtopline.setAttribute("style", "stroke:#000000;stroke-width:"
				+ parameter.getValue("celllinestrokewidth")+"px");
		_childgp.appendChild(_childtopline);

		_rectnode = createNode(SvgDocument, i, _codeitemobj, x, y, parameter);
		_graph3dright = createNodeGraph3dright(SvgDocument, i, _codeitemobj, x,
				y, parameter);
		_graph3dtop = createNodeGraph3dtop(SvgDocument, i, _codeitemobj, x, y,
				parameter);
		createNodeText(SvgDocument, _rectnode, i, _codeitemobj, _childgp, x, y,
				parameter, _graph3dright, _graph3dtop);

		if (_codeitemobj.childid != _codeitemobj.codeitemid) {
			_isperson = false;
			createBottomRect(SvgDocument, _childgp, _childgid, _codeitemobj, i,
					x, y, grade, rootgrade, catalog_id, parameter, _isperson);

		} else if (_codeitemobj.personcount > 0
				&& parameter.getValue("isshowpersonname").toString() == 'true') {
			_isperson = true;
			createBottomRect(SvgDocument, _childgp, _childgid, _codeitemobj, i,
					x, y, grade, rootgrade, catalog_id, parameter, _isperson);
		}

		_childgcid = getChildGCID(parentenodestr, grade, i + 1,
				childslist.length);
		if (i == 0)
			_childgctemp.setAttribute("name", _childgcid + "sun"
					+ getFourCoding(childslist.length) + "visible");
		_childgc = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childgc.setAttribute("id", _childgcid);
		_childg.appendChild(_childgc);

		_childnextnodeid = getChildGID(parentenodestr, grade, i + 2,
				childslist.length);
		_childnextnode = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childnextnode.setAttribute("id", _childnextnodeid);
		_childnextnode.setAttribute("name", "x0");
		_childg.appendChild(_childnextnode);

		parentenodec = _childnextnode;
	}
}
function createNode(SvgDocument, i, _codeitemobj, x, y, parameter) {
	var _rectnode;
	_rectnode = SvgDocument.createElementNS("http://www.w3.org/2000/svg","rect");
	_rectnode.setAttribute("x",
			(i
					* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(x, 10) - (parseInt(parameter
					.getValue("cellwidth"), 10)
					/ 2 - parseInt(parameter.getValue("rectwidth"), 10) / 2)));
	_rectnode.setAttribute("y", (parseInt(y, 10)
			+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
			parameter.getValue("cellvspacewidth"), 10)
			* 2));
	_rectnode.setAttribute("width", parameter.getValue("cellwidth"));
	_rectnode.setAttribute("height", parameter.getValue("cellheight"));
	_rectnode.setAttribute("style", "fill:"
			+ getCellColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid)
			+ ";stroke:"
			+ getCellFrameColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid) + ";stroke-width:"
			+ parameter.getValue("celllinestrokewidth")+"px");

	return _rectnode;
}
function createNodeGraph3dtop(SvgDocument, i, _codeitemobj, x, y, parameter) {
	var _graph3dtop;
	var _x = i
			* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10))
			+ parseInt(x, 10)
			- (parseInt(parameter.getValue("cellwidth"), 10) / 2 - parseInt(
					parameter.getValue("rectwidth"), 10)
					/ 2);
	var _y = parseInt(y, 10) + parseInt(parameter.getValue("rectwidth"), 10)
			+ parseInt(parameter.getValue("cellvspacewidth"), 10) * 2;
	_graph3dtop = SvgDocument.createElementNS("http://www.w3.org/2000/svg","path");
	var _xl = parseInt(parameter.getValue("cellheight"), 10) / 6;
	_graph3dtop.setAttribute("d", "M " + _x + "," + _y + " h "
			+ parseInt(parameter.getValue("cellwidth")) + " l " + _xl + ",-" + _xl
			+ " h -" + parseInt(parameter.getValue("cellwidth")) + " l -" + _xl + ","
			+ _xl + " z");

	_graph3dtop.setAttribute("style", "fill:"
			+ getShadowColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid));
	_graph3dtop.setAttribute("fill-opacity", "0.5");
	return _graph3dtop;
}
function createNodeGraph3dright(SvgDocument, i, _codeitemobj, x, y, parameter) {
	var _graph3dright;
	var _x = i
			* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10))
			+ parseInt(x, 10)
			+ (parseInt(parameter.getValue("cellwidth"), 10) / 2 + parseInt(
					parameter.getValue("rectwidth"), 10)
					/ 2);
	var _y = parseInt(y, 10) + parseInt(parameter.getValue("rectwidth"), 10)
			+ parseInt(parameter.getValue("cellvspacewidth"), 10) * 2;
	_graph3dright = SvgDocument.createElementNS("http://www.w3.org/2000/svg","path");
	var _xl = parseInt(parameter.getValue("cellheight"), 10) / 6;
	_graph3dright.setAttribute("d", "M " + _x + "," + _y + " v "
			+ parseInt(parameter.getValue("cellheight")) + " l " + _xl + ",-" + _xl
			+ " v -" + parseInt(parameter.getValue("cellheight")) + " l -" + _xl + ","
			+ _xl + " z");

	_graph3dright.setAttribute("style", "fill:"
			+ getShadowColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid));
	_graph3dright.setAttribute("fill-opacity", "0.5");
	return _graph3dright;
}
function createNodeText(SvgDocument, _rectnode, i, _codeitemobj, _childgp, x,
		y, parameter, _graph3dright, _graph3dtop) {
	var _text;
	var _colscount;
	var _wordcount;
	var _tspan;
	var _vlink;
	var hreflink;
	_text = SvgDocument.createElementNS("http://www.w3.org/2000/svg","text");
	_text
			.setAttribute("x",
					(i
							* (parseInt(parameter.getValue("cellwidth")) + parseInt(
									parameter.getValue("cellhspacewidth"), 10))
							+ parseInt(x, 10) - (parameter
							.getValue("cellwidth")
							/ 2 - 11)));
	_text.setAttribute("y", (parseInt(y, 10)
			+ parseInt(parameter.getValue("rectwidth"), 10) / 2 * 3 + parseInt(
			parameter.getValue("cellvspacewidth"), 10)
			* 2));

	// _wordcount=OneLineWordCount(parameter.getValue("cellwidth"));
	// _colscount=parseInt(_codeitemobj.codeitemdesc.length/_wordcount,10);
	/*
	 * var
	 * text_Arr=oneLineWordCountSunx2(parameter.getValue("cellwidth"),parameter.getValue("fontsize"),parameter.getValue("cellheight"),_codeitemobj.codeitemdesc.length);
	 * if(text_Arr!=null&&text_Arr.length>0) { fize=text_Arr[0];
	 * _wordcount=text_Arr[1]; _colscount=text_Arr[2]; }else { //
	 * _wordcount=OneLineWordCount(parameter.getValue("cellwidth"));
	 * _wordcount=oneLineWordCountSunx(parameter.getValue("cellwidth"),parameter.getValue("fontsize"));
	 * _colscount=parseInt(_codeitemobj.codeitemdesc.length/_wordcount,10);
	 * fize=fizeWordSunx(_colscount,parameter.getValue("cellheight"),parameter.getValue("fontsize"));
	 * //alert(fize+"---"+_wordcount+"---"+_colscount); } if(_colscount>0) {
	 * for(var j=0;j<_colscount+1;j++) { var
	 * dy=parseInt(parameter.getValue("cellheight",10))/(_colscount*4);
	 * if(j==_colscount) { _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc.substring((j)*_wordcount,_codeitemobj.codeitemdesc.length)));
	 * _tspan.setAttribute("dy",fize+3);
	 * _tspan.setAttribute("x",(i*(parameter.getValue("cellwidth")
	 * +parseInt(parameter.getValue("cellhspacewidth"),10)) +
	 * parseInt(x,10)-(parameter.getValue("cellwidth")/2-11)));
	 * _text.appendChild(_tspan); }else if(j==0) {
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount)));
	 * _tspan.setAttribute("dy",dy);
	 * _tspan.setAttribute("x",(i*(parameter.getValue("cellwidth")
	 * +parseInt(parameter.getValue("cellhspacewidth"),10)) +
	 * parseInt(x,10)-(parameter.getValue("cellwidth")/2-11)));
	 * _text.appendChild(_tspan); }else {
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount)));
	 * _tspan.setAttribute("dy",fize+3);
	 * _tspan.setAttribute("x",(i*(parameter.getValue("cellwidth")
	 * +parseInt(parameter.getValue("cellhspacewidth"),10)) +
	 * parseInt(x,10)-(parameter.getValue("cellwidth")/2-11)));
	 * _text.appendChild(_tspan); } } }else {
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc));
	 * _tspan.setAttribute("dy","15");
	 * _tspan.setAttribute("x",(i*(parameter.getValue("cellwidth")
	 * +parseInt(parameter.getValue("cellhspacewidth"),10)) +
	 * parseInt(x,10)-(parameter.getValue("cellwidth")/2-11)));
	 * _text.appendChild(_tspan);
	 *  }
	 */
	var strObject = oneTableTextLineSvg(parseInt(parameter.getValue("cellwidth")),
			parseInt(parameter.getValue("cellheight")), _codeitemobj.codeitemdesc,
			parseInt(parameter.getValue("fontsize")));
	fize = strObject.fize;
	var _textArray = strObject.strArray;
	_colscount = 0;
	if (_textArray != null && _textArray.length > 0)
		_colscount = _textArray.length;
	if (_colscount > 1) {
		for (var j = 0; j < _textArray.length; j++) {
			var dy = fize + 2;
			if (j == 0)
				dy = parseInt(parameter.getValue("cellheight", 10))
						/ (_colscount * 4) + 1;
			_tspan = SvgDocument.createElementNS("http://www.w3.org/2000/svg","tspan");
			_tspan.appendChild(SvgDocument.createTextNode(_textArray[j]));
			_tspan.setAttribute("dy", dy);
			_tspan.setAttribute("x",
					(i
							* (parseInt(parameter.getValue("cellwidth")) + parseInt(
									parameter.getValue("cellhspacewidth"), 10))
							+ parseInt(x, 10) - (parameter
							.getValue("cellwidth")
							/ 2 - 11)));
			_text.appendChild(_tspan);
		}
	} else {
		_tspan = SvgDocument.createElementNS("http://www.w3.org/2000/svg","tspan");
		_tspan.appendChild(SvgDocument
				.createTextNode(_codeitemobj.codeitemdesc));
		_tspan.setAttribute("dy", parseInt(parameter.getValue("cellheight"))
				/ 2);
		_tspan.setAttribute("x",
				(i
						* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
								.getValue("cellhspacewidth"), 10))
						+ parseInt(x, 10) - (parameter.getValue("cellwidth")
						/ 2 - 11)));
		_text.appendChild(_tspan);
	}
	// if(parameter.getValue("isshowpersonconut")==true)
	// {
	// _tspan=SvgDocument.createElement("tspan");
	// _tspan.appendChild(SvgDocument.createTextNode("(" +
	// _codeitemobj.personcount + "??)"));
	// _tspan.setAttribute("dy","15");
	// _tspan.setAttribute("x",(i*(parameter.getValue("cellwidth")
	// +parseInt(parameter.getValue("cellhspacewidth"),10)) +
	// parseInt(x,10)-(parameter.getValue("cellwidth")/2-11)));
	// _text.appendChild(_tspan);
	// }
	_vlink = SvgDocument.createElementNS("http://www.w3.org/2000/svg", "a");
	if (_codeitemobj.infokind == "org") {
		if (_codeitemobj.codesetid == "@K") {
			hreflink = "/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id="
					+ _codeitemobj.codeitemid
					+ "&infokind=3&dbname="
					+ parameter.getValue("dbnames");
		}

		else {
			hreflink = "/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id="
					+ _codeitemobj.codeitemid
					+ "&infokind=2&dbname="
					+ parameter.getValue("dbnames");
		}
	} else if (_codeitemobj.infokind == "vorg") {
		hreflink = "/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id="
				+ _codeitemobj.codeitemid
				+ "&infokind=2&dbname="
				+ parameter.getValue("dbnames")
				+ "&orgtype="
				+ _codeitemobj.infokind;
	} else {
        hreflink = "/workbench/browse/showselfinfo.do?b_search=link&userbase="+parameter.getValue("dbnames")+"&a0100="+_codeitemobj.codeitemid+"&flag=notself&returnvalue=1000000";
	}
	_text.setAttribute("style", "fill:"
			+ getFontColor(parameter.getValue("fontcolor"),
					_codeitemobj.codesetid) + ";font-size:"
			+ parameter.getValue("fontsize") + "px;");
	_text.setAttribute("font-family", getFontfamily(parameter
			.getValue("fontfamily")));
	_text.setAttribute("font-style", getFontstyle(parameter
			.getValue("fontstyle")));
	_text.setAttribute("font-weight", getFontweight(parameter
			.getValue("fontstyle")));
	_vlink.setAttribute("target", "_blank");
	if (hreflink != null && hreflink != "undefined" && hreflink != ""){
		_vlink.setAttribute("onclick", "javascript:viewmess('" + hreflink
				+ "');");
		_vlink.setAttributeNS("http://www.w3.org/1999/xlink","xlink:href",hreflink);
	}
	_childgp.appendChild(_vlink);
	_vlink.appendChild(_rectnode);
	if (parameter.getValue("graph3d").toString() == 'true') {
		_vlink.appendChild(_graph3dright);
		_vlink.appendChild(_graph3dtop);
	}
	_vlink.appendChild(_text);
}
function createBottomRect(SvgDocument, _childgp, _childgid, _codeitemobj, i, x,
		y, grade, rootgrade, catalog_id, parameter, _isperson) {
	var _childbuttomline;
	var _childmiddleline;
	var _rectsmallnode;

	_childbuttomline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
	_childbuttomline.setAttribute("x1",
			(i
					* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(x, 10) + parseInt(parameter
					.getValue("rectwidth"), 10)
					/ 2));
	_childbuttomline.setAttribute("y1", (parseInt(y, 10) + (parseInt(parameter
			.getValue("cellvspacewidth"), 10)
			* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 + parseInt(parameter
			.getValue("cellheight")))));
	_childbuttomline.setAttribute("x2",
			(i
					* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(x, 10) + parseInt(parameter
					.getValue("rectwidth"), 10)
					/ 2));
	_childbuttomline.setAttribute("y2",
			(parseInt(y, 10) + (parseInt(parameter.getValue("rectwidth"), 10)
					* 2 + parseInt(parameter.getValue("cellvspacewidth"), 10)
					/ 2 * 3 + parseInt(parameter.getValue("cellheight")))));
	_childbuttomline.setAttribute("style", "stroke:#000000;stroke-width:"
			+ parameter.getValue("celllinestrokewidth")+"px");
	_childbuttomline.setAttribute("id", "l" + _childgid.substring(1));
	_childbuttomline.setAttribute("fill-opacity", "0");
	_childgp.appendChild(_childbuttomline);

	_childmiddleline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
	_childmiddleline.setAttribute("x1", (i
			* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10)) + parseInt(x, 10)));
	_childmiddleline.setAttribute("y1", (parseInt(y, 10) + (parseInt(parameter
			.getValue("cellvspacewidth"), 10)
			* 2 + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(parameter
			.getValue("cellheight")))));
	_childmiddleline.setAttribute("x2",
			(i
					* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(x, 10) + parseInt(parameter
					.getValue("rectwidth"), 10)));
	_childmiddleline.setAttribute("y2", (parseInt(y, 10) + (parseInt(parameter
			.getValue("cellvspacewidth"), 10)
			* 2 + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(parameter
			.getValue("cellheight")))));
	_childmiddleline.setAttribute("style", "stroke:#000000;stroke-width:"
			+ parameter.getValue("celllinestrokewidth")+"px");
	_childmiddleline.setAttribute("id", "ll" + _childgid.substring(1));
	_childmiddleline.setAttribute("fill-opacity", "0");
	_childgp.appendChild(_childmiddleline);

	_rectsmallnode = SvgDocument.createElementNS("http://www.w3.org/2000/svg","rect");
	_rectsmallnode.setAttribute("x", (i
			* (parseInt(parameter.getValue("cellwidth")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10)) + parseInt(x, 10)));
	_rectsmallnode.setAttribute("y", (parseInt(y, 10) + (parseInt(parameter
			.getValue("cellvspacewidth"), 10)
			* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 + parseInt(parameter
			.getValue("cellheight")))));
	_rectsmallnode.setAttribute("width", parameter.getValue("rectwidth"));
	_rectsmallnode.setAttribute("height", parameter.getValue("rectwidth"));
	// _rectsmallnode.setAttribute("style","fill:" +
	// getCellColor(parameter.getValue("cellcolor"),_codeitemobj.codesetid) +
	// ";stroke:" +
	// getCellFrameColor(parameter.getValue("cellcolor"),_codeitemobj.codesetid)
	// + ";stroke-width:" + + parameter.getValue("celllinestrokewidth"));

	_rectsmallnode.setAttribute("style", "fill:#FF0000;stroke:"
			+ getCellFrameColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid) + ";stroke-width:"
			+ +parameter.getValue("celllinestrokewidth")+"px");

	_rectsmallnode.setAttribute("fill-opacity", "0.5");
	_rectsmallnode.setAttribute("id", "c" + _childgid.substring(1));
	_childgp.appendChild(_rectsmallnode);

	if (_isperson == false) {
		_rectsmallnode.setAttribute("onclick", "loadChildNode(evt,'"
				+ _childgid + "','" + _codeitemobj.codeitemid + "','"
				+ _codeitemobj.codesetid + "'," + rootgrade + ",'" + catalog_id
				+ "','" + parameter.getValue("dbname") + "',"
				+ parameter.getValue("graphaspect") + ")");
	} else {
		_rectsmallnode.setAttribute("onclick", "loadPersonNode(evt,'"
				+ _childgid + "','" + _codeitemobj.codeitemid + "','"
				+ _codeitemobj.codesetid + "'," + grade + ",'" + catalog_id
				+ "','" + parameter.getValue("dbname") + "',"
				+ parameter.getValue("graphaspect") + ")");
	}

}
function createNodeTextisupright(SvgDocument, _rectnode, i, _codeitemobj,
		_childgp, x, y, parameter, _graph3dright, _graph3dtop) {
	var _text;
	var _colscount;
	var _wordcount;
	var _tspan;
	var _vlink;
	var fize;
	var hreflink;
	_text = SvgDocument.createElementNS("http://www.w3.org/2000/svg","text");
	_text.setAttribute("x", (parseInt(x, 10)
			+ parseInt(parameter.getValue("rectwidth"), 10) / 2 * 3 + parseInt(
			parameter.getValue("cellvspacewidth"), 10)
			* 2));
	_text
			.setAttribute("y",
					(i
							* (parseInt(parameter.getValue("cellheight")) + parseInt(
									parameter.getValue("cellhspacewidth"), 10))
							+ parseInt(y, 10) - (parseInt(parameter
							.getValue("cellheight"))
							/ 2 - 11)));
	// alert(_text.getAttribute("y"))

	/*
	 * var
	 * text_Arr=oneLineWordCountSunx2(parameter.getValue("cellwidth"),parameter.getValue("fontsize"),parameter.getValue("cellheight"),_codeitemobj.codeitemdesc.length);
	 * if(text_Arr!=null&&text_Arr.length>0) { fize=text_Arr[0];
	 * _wordcount=text_Arr[1]; _colscount=text_Arr[2]; }else { //
	 * _wordcount=OneLineWordCount(parameter.getValue("cellwidth"));
	 * _wordcount=oneLineWordCountSunx(parameter.getValue("cellwidth"),parameter.getValue("fontsize"));
	 * _colscount=parseInt(_codeitemobj.codeitemdesc.length/_wordcount,10);
	 * fize=fizeWordSunx(_colscount,parameter.getValue("cellheight"),parameter.getValue("fontsize"));
	 * //alert(fize+"---"+_wordcount+"---"+_colscount); } if(_colscount>0) {
	 * for(var j=0;j<_colscount+1;j++) { if(j==_colscount) {
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc.substring((j)*_wordcount,_codeitemobj.codeitemdesc.length)));
	 * _tspan.setAttribute("dy",fize+2);
	 * _tspan.setAttribute("x",(parseInt(x,10)+
	 * parseInt(parameter.getValue("rectwidth"),10)/2*3 +
	 * parseInt(parameter.getValue("cellvspacewidth"),10)*2));
	 * _text.appendChild(_tspan); }else if(j==0) { var
	 * dy=parseInt(parameter.getValue("cellheight",10))/(_colscount*4);
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount)));
	 * _tspan.setAttribute("dy",dy); _tspan.setAttribute("x",(parseInt(x,10)+
	 * parseInt(parameter.getValue("rectwidth"),10)/2*3 +
	 * parseInt(parameter.getValue("cellvspacewidth"),10)*2));
	 * _text.appendChild(_tspan); }else {
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc.substring(j*_wordcount,(j+1)*_wordcount)));
	 * _tspan.setAttribute("dy",fize+2);
	 * _tspan.setAttribute("x",(parseInt(x,10)+
	 * parseInt(parameter.getValue("rectwidth"),10)/2*3 +
	 * parseInt(parameter.getValue("cellvspacewidth"),10)*2));
	 * _text.appendChild(_tspan); } } }else {
	 * _tspan=SvgDocument.createElement("tspan");
	 * _tspan.appendChild(SvgDocument.createTextNode(_codeitemobj.codeitemdesc));
	 * _tspan.setAttribute("dy","15"); _tspan.setAttribute("x",(parseInt(x,10)+
	 * parseInt(parameter.getValue("rectwidth"),10)/2*3 +
	 * parseInt(parameter.getValue("cellvspacewidth"),10)*2));
	 * _text.appendChild(_tspan); }
	 */
	var strObject = oneTableTextLineSvg(parseInt(parameter.getValue("cellwidth")),
			parseInt(parameter.getValue("cellheight")), _codeitemobj.codeitemdesc,
			parseInt(parameter.getValue("fontsize")));
	fize = strObject.fize;
	var _textArray = strObject.strArray;
	_colscount = 0;
	if (_textArray != null && _textArray.length > 0)
		_colscount = _textArray.length;
	if (_colscount > 1) {
		for (var j = 0; j < _textArray.length; j++) {
			var dy = fize + 2;
			if (j == 0)
				dy = parseInt(parameter.getValue("cellheight", 10))
						/ (_colscount * 4) + 1;
			_tspan = SvgDocument.createElementNS("http://www.w3.org/2000/svg","tspan");
			_tspan.appendChild(SvgDocument.createTextNode(_textArray[j]));
			_tspan.setAttribute("dy", dy);
			_tspan.setAttribute("x",
					(parseInt(x, 10)
							+ parseInt(parameter.getValue("rectwidth"), 10) / 2
							* 3 + parseInt(parameter
							.getValue("cellvspacewidth"), 10)
							* 2));
			_text.appendChild(_tspan);
		}
	} else {
		_tspan = SvgDocument.createElementNS("http://www.w3.org/2000/svg","tspan");
		_tspan.appendChild(SvgDocument
				.createTextNode(_codeitemobj.codeitemdesc));
		_tspan.setAttribute("dy", parseInt(parameter.getValue("cellheight"))
				/ 2);
		_tspan
				.setAttribute("x",
						(parseInt(x, 10)
								+ parseInt(parameter.getValue("rectwidth"), 10)
								/ 2 * 3 + parseInt(parameter
								.getValue("cellvspacewidth"), 10)
								* 2));
		_text.appendChild(_tspan);
	}
	_vlink = SvgDocument.createElementNS("http://www.w3.org/2000/svg", "a");

	if (_codeitemobj.infokind == "org") {
		if (_codeitemobj.codesetid == "@K") {
			hreflink = "/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id="
					+ _codeitemobj.codeitemid
					+ "&infokind=3&dbname="
					+ parameter.getValue("dbnames");
		} else {

			hreflink = "/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id="
					+ _codeitemobj.codeitemid
					+ "&infokind=2&dbname="
					+ parameter.getValue("dbnames");
		}
	} else if (_codeitemobj.infokind == "vorg") {
		
		hreflink = "/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id="
				+ _codeitemobj.codeitemid
				+ "&infokind=2&dbname="
				+ parameter.getValue("dbnames")
				+ "&orgtype="
				+ _codeitemobj.infokind;
		
		
	} else {
		hreflink = "/workbench/browse/showselfinfo.do?b_search=link&userbase="+parameter.getValue("dbnames")+"&a0100="+_codeitemobj.codeitemid+"&flag=notself&returnvalue=1000000";
		
	}
	_text.setAttribute("style", "fill:"
			+ getFontColor(parameter.getValue("fontcolor"),
					_codeitemobj.codesetid) + ";font-size:" + fize + "px;");
	_text.setAttribute("font-family", getFontfamily(parameter
			.getValue("fontfamily")));
	_text.setAttribute("font-style", getFontstyle(parameter
			.getValue("fontstyle")));
	_text.setAttribute("font-weight", getFontweight(parameter
			.getValue("fontstyle")));
	_vlink.setAttribute("target", "_blank");
	if (hreflink != null && hreflink != "undefined" && hreflink != ""){
		_vlink.setAttribute("onclick", "javascript:viewmess('" + hreflink
				+ "');");
	}
	_vlink.setAttributeNS("http://www.w3.org/1999/xlink","xlink:href",hreflink);
	_childgp.appendChild(_vlink);
	_vlink.appendChild(_rectnode);
	if (parameter.getValue("graph3d").toString() == 'true') {
		_vlink.appendChild(_graph3dright);
		_vlink.appendChild(_graph3dtop);
	}
	_vlink.appendChild(_text);
}
function createBottomRectisupright(SvgDocument, _childgp, _childgid,
		_codeitemobj, i, x, y, grade, rootgrade, catalog_id, parameter,
		_isperson) {
	var _childbuttomline;
	var _childmiddleline;
	var _rectsmallnode;
	var _buttomlinex1;
	var _buttomlinex2;
	var _middlelinex1;
	var _middlelinex2;
	var _smallnodex;
	if (parameter.getValue("graph3d").toString() == 'true') {
		_buttomlinex1 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 + parseInt(parameter
				.getValue("cellwidth"))))
				+ parseInt(parameter.getValue("cellheight"), 10) / 8;
		_buttomlinex2 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 * 3 + parseInt(parameter
				.getValue("cellwidth"))))
				+ parseInt(parameter.getValue("cellheight"), 10) / 8;
		_middlelinex1 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(parameter
				.getValue("cellwidth"))))
				+ parseInt(parameter.getValue("cellheight"), 10) / 8;
		_middlelinex2 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(parameter
				.getValue("cellwidth"))))
				+ parseInt(parameter.getValue("cellheight"), 10) / 8;
		_smallnodex = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 + parseInt(parameter
				.getValue("cellwidth"))))
				+ parseInt(parameter.getValue("cellheight"), 10) / 8;
	} else {
		_buttomlinex1 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 + parseInt(parameter
				.getValue("cellwidth"))));
		_buttomlinex2 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 * 3 + parseInt(parameter
				.getValue("cellwidth"))));
		_middlelinex1 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(parameter
				.getValue("cellwidth"))));
		_middlelinex2 = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(parameter
				.getValue("cellwidth"))));
		_smallnodex = (parseInt(x, 10) + (parseInt(parameter
				.getValue("cellvspacewidth"), 10)
				* 2 + parseInt(parameter.getValue("rectwidth"), 10) / 2 + parseInt(parameter
				.getValue("cellwidth"))));
	}
	_childbuttomline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
	_childbuttomline.setAttribute("x1", _buttomlinex1);
	_childbuttomline.setAttribute("y1",
			(i
					* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(y, 10) + parseInt(parameter
					.getValue("rectwidth"), 10)
					/ 2));
	_childbuttomline.setAttribute("x2", _buttomlinex2);
	_childbuttomline.setAttribute("y2",
			(i
					* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(y, 10) + parseInt(parameter
					.getValue("rectwidth"), 10)
					/ 2));
	_childbuttomline.setAttribute("style", "stroke:#000000;stroke-width:"
			+ parameter.getValue("celllinestrokewidth")+"px");
	_childbuttomline.setAttribute("id", "l" + _childgid.substring(1));
	_childgp.appendChild(_childbuttomline);

	_childmiddleline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
	_childmiddleline.setAttribute("x1", _middlelinex1);
	_childmiddleline.setAttribute("y1", (i
			* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10)) + parseInt(y, 10)));
	_childmiddleline.setAttribute("x2", _middlelinex2);
	_childmiddleline.setAttribute("y2",
			(i
					* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
							.getValue("cellhspacewidth"), 10))
					+ parseInt(y, 10) + parseInt(parameter
					.getValue("rectwidth"), 10)));
	_childmiddleline.setAttribute("style", "stroke:#000000;stroke-width:"
			+ parameter.getValue("celllinestrokewidth")+"px");
	_childmiddleline.setAttribute("id", "ll" + _childgid.substring(1));
	_childgp.appendChild(_childmiddleline);

	_rectsmallnode = SvgDocument.createElementNS("http://www.w3.org/2000/svg","rect");
	_rectsmallnode.setAttribute("x", _smallnodex);
	_rectsmallnode.setAttribute("y", (i
			* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10)) + parseInt(y, 10)));
	_rectsmallnode.setAttribute("width", parameter.getValue("rectwidth"));
	_rectsmallnode.setAttribute("height", parameter.getValue("rectwidth"));
	// _rectsmallnode.setAttribute("style","fill:" +
	// getCellColor(parameter.getValue("cellcolor"),_codeitemobj.codesetid) +
	// ";stroke:" +
	// getCellFrameColor(parameter.getValue("cellcolor"),_codeitemobj.codesetid)
	// + ";stroke-width:" + parameter.getValue("celllinestrokewidth"));

	_rectsmallnode.setAttribute("style", "fill:#FF0000;stroke:"
			+ getCellFrameColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid) + ";stroke-width:"
			+ parameter.getValue("celllinestrokewidth")+"px");
	_rectsmallnode.setAttribute("fill-opacity", "0.5");
	_rectsmallnode.setAttribute("id", "c" + _childgid.substring(1));
	if (_isperson == false) {
		_rectsmallnode.setAttribute("onclick", "loadChildNode(evt,'"
				+ _childgid + "','" + _codeitemobj.codeitemid + "','"
				+ _codeitemobj.codesetid + "'," + rootgrade + ",'" + catalog_id
				+ "','" + parameter.getValue("dbname") + "',"
				+ parameter.getValue("graphaspect") + ")");
	} else {
		_rectsmallnode.setAttribute("onclick", "loadPersonNode(evt,'"
				+ _childgid + "','" + _codeitemobj.codeitemid + "','"
				+ _codeitemobj.codesetid + "'," + grade + ",'" + catalog_id
				+ "','" + parameter.getValue("dbname") + "',"
				+ parameter.getValue("graphaspect") + ")");
	}

	_childgp.appendChild(_rectsmallnode);
}
function createChildsisupright(SvgDocument, parentenodestr, childslist, grade,
		rootgrade, catalog_id, x, y, parameter) {
	var _childgid;
	var _childgpid;
	var _childgcid;
	var _childg;
	var _childgp;
	var _childgc;
	var _childtopline;
	var _rectnode;
	var _childnextnode;
	var _childnextnodeid;
	var _childgctemp;
	var _codeitemobj;
	var _graph3dright;
	var _graph3dtop;
	var _topy1;
	var parentenodec = SvgDocument.getElementById(parentenodestr + "c");
	createchildnodeline(SvgDocument, parentenodestr, parentenodec,
			childslist.length, x, y, parameter);
	_childgctemp = parentenodec;
	for (var i = 0; i < parseInt(childslist.length, 10); i++) {

		_codeitemobj = childslist[i];
		_childgid = getChildGID(parentenodestr, grade, i + 1, childslist.length);
		var _childg = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childg.setAttribute("id", _childgid);
		_childg.setAttribute("name", "x"
				+ (parseInt(childslist.length, 10) - 1)
				* (parseInt(parameter.getValue("cellheight"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10)));
		parentenodec.appendChild(_childg);

		_childgpid = getChildGPID(parentenodestr, grade, i + 1,
				childslist.length);
		_childgp = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childgp.setAttribute("id", _childgpid);

		_childgp.setAttribute("name", parentenodestr
				+ "p"
				+ (parseInt(childslist.length, 10) - 1)
				* (parseInt(parameter.getValue("cellheight"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10)) / 2);
		_childg.appendChild(_childgp);

		_childtopline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
		_childtopline.setAttribute("x1", (parseInt(x, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
				parameter.getValue("cellvspacewidth"), 10)));
		_childtopline.setAttribute("y1",
				(i
						* (parseInt(parameter.getValue("cellheight")) + parseInt(
								parameter.getValue("cellhspacewidth"), 10))
						+ parseInt(y, 10) + parseInt(parameter
						.getValue("rectwidth"), 10)
						/ 2));
		_childtopline.setAttribute("x2", (parseInt(x, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
				parameter.getValue("cellvspacewidth"), 10)
				* 2));
		_childtopline.setAttribute("y2", (i
				* (parseInt(parameter.getValue("cellheight"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10))
				+ parseInt(y, 10) + parseInt(parameter.getValue("rectwidth"),
				10)
				/ 2));
		_childtopline.setAttribute("style", "stroke:#000000;stroke-width:"
				+ parseInt(parameter.getValue("celllinestrokewidth"))+"px");
		_childgp.appendChild(_childtopline);

		_rectnode = SvgDocument.createElementNS("http://www.w3.org/2000/svg","rect");
		_rectnode.setAttribute("x", (parseInt(x, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
				parameter.getValue("cellvspacewidth"), 10)
				* 2));
		_rectnode
				.setAttribute("y",
						(i
								* (parseInt(parameter.getValue("cellheight")) + parseInt(
										parameter.getValue("cellhspacewidth"),
										10)) + parseInt(y, 10) - (parseInt(
								parameter.getValue("cellheight"), 10)
								/ 2 - parseInt(parameter.getValue("rectwidth"),
								10)
								/ 2)));
		_rectnode.setAttribute("width", parameter.getValue("cellwidth"));
		_rectnode.setAttribute("height", parameter.getValue("cellheight"));
		_rectnode.setAttribute("style", "fill:"
				+ getCellColor(parameter.getValue("cellcolor"),
						_codeitemobj.codesetid)
				+ ";stroke:"
				+ getCellFrameColor(parameter.getValue("cellcolor"),
						_codeitemobj.codesetid) + ";stroke-width:"
				+ parseInt(parameter.getValue("celllinestrokewidth"))+"px");
		_graph3dright = createNodeGraph3drightisupright(SvgDocument, i,
				_codeitemobj, x, y, parameter);
		_graph3dtop = createNodeGraph3dtopisupright(SvgDocument, i,
				_codeitemobj, x, y, parameter);
		createNodeTextisupright(SvgDocument, _rectnode, i, _codeitemobj,
				_childgp, x, y, parameter, _graph3dright, _graph3dtop);
		if (_codeitemobj.childid != _codeitemobj.codeitemid) {
			_isperson = false;
			createBottomRectisupright(SvgDocument, _childgp, _childgid,
					_codeitemobj, i, x, y, grade, rootgrade, catalog_id,
					parameter, _isperson);

		} else if (parseInt(_codeitemobj.personcount) > 0
				&& parameter.getValue("isshowpersonname").toString() == 'true') {
			_isperson = true;
			createBottomRectisupright(SvgDocument, _childgp, _childgid,
					_codeitemobj, i, x, y, grade, rootgrade, catalog_id,
					parameter, _isperson);
		}

		_childgcid = getChildGCID(parentenodestr, grade, i + 1,
				childslist.length);
		if (i == 0)
			_childgctemp.setAttribute("name", _childgcid + "sun"
					+ getFourCoding(childslist.length) + "visible");
		_childgc = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childgc.setAttribute("id", _childgcid);
		_childg.appendChild(_childgc);

		_childnextnodeid = getChildGID(parentenodestr, grade, i + 2,
				childslist.length);
		_childnextnode = SvgDocument.createElementNS("http://www.w3.org/2000/svg","g");
		_childnextnode.setAttribute("id", _childnextnodeid);
		_childnextnode.setAttribute("name", "x0");
		_childg.appendChild(_childnextnode);

		parentenodec = _childnextnode;
	}
}
function createNodeGraph3dtopisupright(SvgDocument, i, _codeitemobj, x, y,
		parameter) {
	var _graph3dtop;
	var _x = (parseInt(x, 10) + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
			parameter.getValue("cellvspacewidth"), 10)
			* 2);
	var _y = (i
			* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10)) + parseInt(y, 10) - (parseInt(
			parameter.getValue("cellheight"), 10)
			/ 2 - parseInt(parameter.getValue("rectwidth"), 10) / 2));
	_graph3dtop = SvgDocument.createElementNS("http://www.w3.org/2000/svg","path");
	var _xl = parseInt(parameter.getValue("cellheight"), 10) / 6;
	_graph3dtop.setAttribute("d", "M " + _x + "," + _y + " h "
			+ parameter.getValue("cellwidth") + " l " + _xl + ",-" + _xl
			+ " h -" + parameter.getValue("cellwidth") + " l -" + _xl + ","
			+ _xl + " z");

	_graph3dtop.setAttribute("style", "fill:"
			+ getShadowColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid));
	_graph3dtop.setAttribute("fill-opacity", "0.5");
	return _graph3dtop;
}
function createNodeGraph3drightisupright(SvgDocument, i, _codeitemobj, x, y,
		parameter) {
	var _graph3dright;
	var _x = (parseInt(x, 10) + parseInt(parameter.getValue("rectwidth"), 10) + parseInt(
			parameter.getValue("cellvspacewidth"), 10)
			* 2)
			+ parseInt(parameter.getValue("cellwidth"), 10);
	var _y = (i
			* (parseInt(parameter.getValue("cellheight")) + parseInt(parameter
					.getValue("cellhspacewidth"), 10)) + parseInt(y, 10) - (parseInt(
			parameter.getValue("cellheight"), 10)
			/ 2 - parseInt(parameter.getValue("rectwidth"), 10) / 2));
	_graph3dright = SvgDocument.createElementNS("http://www.w3.org/2000/svg","path");
	var _xl = parseInt(parameter.getValue("cellheight"), 10) / 6;
	_graph3dright.setAttribute("d", "M " + _x + "," + _y + " v "
			+ parameter.getValue("cellheight") + " l " + _xl + ",-" + _xl
			+ " v -" + parameter.getValue("cellheight") + " l -" + _xl + ","
			+ _xl + " z");

	_graph3dright.setAttribute("style", "fill:"
			+ getShadowColor(parameter.getValue("cellcolor"),
					_codeitemobj.codesetid));
	_graph3dright.setAttribute("fill-opacity", "0.5");
	return _graph3dright;
}

function createchildnodeline(SvgDocument, parentenodestr, parentenodec,
		childslistlength, x, y, parameter) {
	var childline = SvgDocument.createElementNS("http://www.w3.org/2000/svg","line");
	if (parameter.getValue("graphaspect").toString() == 'true') {
		childline.setAttribute("x1", parseInt(x, 10)
				+ parseInt(parameter.getValue("cellvspacewidth"), 10)
				+ parseInt(parameter.getValue("rectwidth"), 10));
		childline.setAttribute("y1", parseInt(y, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10) / 2);
		childline.setAttribute("x2", parseInt(x, 10)
				+ parseInt(parameter.getValue("cellvspacewidth"), 10)
				+ parseInt(parameter.getValue("rectwidth"), 10));
		childline.setAttribute("y2", parseInt(y, 10)
				+ parseInt(parameter.getValue("rectwidth"))
				/ 2
				+ (parseInt(childslistlength, 10) - 1)
				* (parseInt(parameter.getValue("cellheight"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10)));
	} else {
		childline.setAttribute("x1", parseInt(x, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10) / 2);
		childline.setAttribute("y1", parseInt(y, 10)
				+ parseInt(parameter.getValue("cellvspacewidth"), 10)
				+ parseInt(parameter.getValue("rectwidth"), 10));
		childline.setAttribute("x2", parseInt(x, 10)
				+ parseInt(parameter.getValue("rectwidth"), 10)
				/ 2
				+ (parseInt(childslistlength, 10) - 1)
				* (parseInt(parameter.getValue("cellwidth"), 10) + parseInt(
						parameter.getValue("cellhspacewidth"), 10)));
		childline.setAttribute("y2", parseInt(y, 10)
				+ parseInt(parameter.getValue("cellvspacewidth"), 10)
				+ parseInt(parameter.getValue("rectwidth"), 10));
	}
	childline.setAttribute("style", "stroke:#000000;stroke-width:"
			+ parseInt(parameter.getValue("celllinestrokewidth"))+"px");
	childline.setAttribute("id", parentenodestr + "l");
	parentenodec.appendChild(childline);
}

function changewidthandheightupright(SvgDocument, childslist, grades, parameter) {
	var _gchilds = SvgDocument.getElementById("gchildswidth");
	if (_gchilds != null)
		_gchilds.setAttribute("name",
				_gchilds.getAttribute("name").substring(0, 1)
						+ (parseFloat(_gchilds.getAttribute("name")
								.substring(1), 10) + parseFloat(
								childslist.length - 1, 10)
								* (parseInt(parameter.getValue("cellheight"),
										10) + parseInt(parameter
										.getValue("cellhspacewidth"), 10))));
	var _viewbox = SvgDocument.getElementById("mainview");
	var viewVal = _viewbox.getAttribute("viewBox").split(" ");
	if (parameter.getValue("graph3d").toString() == 'true') {
		if (parseInt(grades, 10)
				* (parseInt(parameter.getValue("cellheight"), 10) / 6
						+ parseInt(parameter.getValue("cellwidth"), 10)
						+ parseInt(parameter.getValue("cellvspacewidth"), 10)
						* 2 + parseInt(parameter.getValue("rectwidth"), 10))
				+ parseInt(parameter.getValue("cellheight"), 10) / 4 > parseInt(
				viewVal[2], 10))
			_viewbox
					.setAttribute(
							"viewBox",
							"0 0 "
									+ (parseInt(viewVal[2], 10)
											+ ((parseInt(parameter
													.getValue("cellwidth"), 10)
													+ parseInt(
															parameter
																	.getValue("cellvspacewidth"),
															10) * 2 + parseInt(
													parameter
															.getValue("rectwidth"),
													10))) + parseInt(parameter
											.getValue("cellheight"), 10)
											/ 6)
									+ " "
									+ (parseFloat(viewVal[3], 10) + parseFloat(
											childslist.length - 1, 10)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))));
		else
			_viewbox
					.setAttribute(
							"viewBox",
							"0 0 "
									+ parseFloat(viewVal[2], 10)
									+ " "
									+ (parseInt(viewVal[3], 10) + parseFloat(
											childslist.length - 1, 10)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))));
	} else {
		if ((parseInt(grades, 10)+1)
				* (parseInt(parameter.getValue("cellwidth"), 10)
						+ parseInt(parameter.getValue("cellvspacewidth"), 10)
						* 2 + parseInt(parameter.getValue("rectwidth"), 10)) > parseInt(
				viewVal[2], 10))
			_viewbox.setAttribute("viewBox", "0 0 "
					+ (parseInt(viewVal[2], 10) + (parseInt(parameter
							.getValue("cellwidth"), 10)
							+ parseInt(parameter.getValue("cellvspacewidth"),
									10) * 2 + 10 + parseInt(parameter
							.getValue("rectwidth"), 10)))
					+ " "
					+ (parseFloat(viewVal[3], 10) + parseFloat(
							childslist.length - 1, 10)
							* (parseInt(parameter.getValue("cellheight"), 10)
									+ 10 + parseInt(parameter
									.getValue("cellhspacewidth"), 10))));
		else
			_viewbox
					.setAttribute(
							"viewBox",
							"0 0 "
									+ (parseFloat(viewVal[2], 10) + 10)
									+ " "
									+ (10 + parseInt(viewVal[3], 10) + parseFloat(
											childslist.length - 1, 10)
											* (parseInt(parameter
													.getValue("cellheight"), 10) + parseInt(
													parameter
															.getValue("cellhspacewidth"),
													10))));
	}
	//alert(2375);
	adjustembedsize(_gchilds.getAttribute("name").substring(1), grades,
			parameter);
}
function changewidthandheighthorizontal(SvgDocument, childslist, grades,
		parameter) {
	var _gchilds = SvgDocument.getElementById("gchildswidth");
	if (_gchilds != null)
		_gchilds
				.setAttribute(
						"name",
						_gchilds.getAttribute("name").substring(0, 1)
								+ (parseFloat(_gchilds.getAttribute("name")
										.substring(1), 10) + parseFloat(
										childslist.length - 1, 10)
										* (parseInt(parameter
												.getValue("cellwidth"), 10) + parseInt(
												parameter
														.getValue("cellhspacewidth"),
												10))));
	var _viewbox = SvgDocument.getElementById("mainview");
	var viewVal = _viewbox.getAttribute("viewBox").split(" ");

	if (parseInt(grades, 10)
			* (parseInt(parameter.getValue("cellheight"), 10)
					+ parseInt(parameter.getValue("cellvspacewidth"), 10) * 2
					+ parseInt(parameter.getValue("rectwidth"), 10) + 20) > parseInt(
			viewVal[3], 10))
		_viewbox
				.setAttribute(
						"viewBox",
						"0 0 "
								+ (parseFloat(viewVal[2], 10) + 10 + parseFloat(
										childslist.length - 1, 10)
										* (parseInt(parameter
												.getValue("cellwidth"), 10) + parseInt(
												parameter
														.getValue("cellhspacewidth"),
												10)))
								+ " "
								+ (parseInt(viewVal[3], 10) + 10 + (parseInt(
										parameter.getValue("cellheight"), 10)
										+ parseInt(parameter
												.getValue("cellvspacewidth"),
												10) * 2 + parseInt(parameter
										.getValue("rectwidth"), 10))));
	else
		_viewbox
				.setAttribute(
						"viewBox",
						"0 0 "
								+ (parseFloat(viewVal[2], 10) + 10 + parseFloat(
										childslist.length - 1, 10)
										* (parseInt(parameter
												.getValue("cellwidth"), 10) + parseInt(
												parameter
														.getValue("cellhspacewidth"),
												10))) + " "
								+ (parseInt(viewVal[3], 10) + 10));
	//alert(2434);
	adjustembedsize(_gchilds.getAttribute("name").substring(1), grades,
			parameter);
}
function changewidthandheight(SvgDocument, childslist, grades, parameter) {
	if (parameter.getValue("graphaspect").toString() == 'true') {
		//alert(2440);
		changewidthandheightupright(SvgDocument, childslist, grades, parameter);
	} else {
		changewidthandheighthorizontal(SvgDocument, childslist, grades,
				parameter);
	}
}

function adjustembedsize(childswidth, grades, parameter) {
	if (parameter.getValue("graphaspect").toString()  == 'true') {
		//alert("2447");
		adjustembedsizeisupright(childswidth, grades, parameter);
	} else {
		adjustembedsizehorizontal(childswidth, grades, parameter);
	}

}
function adjustembedsizehorizontal(childswidth, grades, parameter) {
	var obj = parent.document.getElementById("svgmap");
	if (parseInt(childswidth, 10) > parseInt(obj.width, 10)
			&& parseInt(childswidth, 10) <= 16300) {
		obj.width = parseInt(childswidth, 10)
				+ parseInt(parameter.getValue("cellhspacewidth"), 10);
	} else {
		if (parseInt(childswidth, 10) > 16300)
			obj.width = 16300;
	}
	if (parseInt(grades, 10)
			* (parseInt(parameter.getValue("cellheight"), 10)
					+ parseInt(parameter.getValue("cellvspacewidth"), 10) * 2
					+ parseInt(parameter.getValue("rectwidth"), 10) + 20) > parseInt(
			obj.height, 10))
		obj.height = parseInt(obj.height)
				+ (parseInt(parameter.getValue("cellheight"), 10)
						+ parseInt(parameter.getValue("cellvspacewidth"), 10)
						* 2 + parseInt(parameter.getValue("rectwidth"), 10));
}
function adjustembedsizeisupright(childswidth, grades, parameter) {
	var obj =parent.document.getElementById("svgmap");
	if (parseInt(childswidth, 10) > parseInt(obj.height, 10)) {
		obj.height = parseInt(childswidth, 10)
				+ parseInt(parameter.getValue("cellhspacewidth"), 10);
	}
	if (parameter.getValue("graph3d").toString() == 'true') {
		if (parseInt(parameter.getValue("cellwidth"), 10)
				/ 4
				+ parseInt(grades, 10)
				* (parseInt(parameter.getValue("cellwidth"), 10) / 6
						+ parseInt(parameter.getValue("cellwidth"), 10)
						+ parseInt(parameter.getValue("cellvspacewidth"), 10)
						* 2 + parseInt(parameter.getValue("rectwidth"), 10)) > parseInt(
				obj.width, 10))
			obj.width = 74
					+ parseInt(parameter.getValue("cellwidth"), 10)
					/ 6
					+ parseInt(grades, 10)
					* (parseInt(parameter.getValue("cellwidth"), 10)
							/ 6
							+ parseInt(parameter.getValue("cellwidth"), 10)
							+ parseInt(parameter.getValue("cellvspacewidth"),
									10) * 2 + parseInt(parameter
							.getValue("rectwidth"), 10));// (parseInt(parameter.getValue("cellwidth"),10)+
															// parseInt(parameter.getValue("cellvspacewidth"),10)*2
															// +
															// parseInt(parameter.getValue("rectwidth"),10))
															// +
															// parseInt(parameter.getValue("cellheight"),10)/6;
	} else {
		if ((parseInt(grades, 10)+1)
				* (parseInt(parameter.getValue("cellwidth"), 10)
						+ parseInt(parameter.getValue("cellvspacewidth"), 10)
						* 2 + parseInt(parameter.getValue("rectwidth"), 10)) > parseInt(
				obj.width, 10))
			obj.width = parseInt(obj.width)
					+ (parseInt(parameter.getValue("cellwidth"), 10)
							+ parseInt(parameter.getValue("cellvspacewidth"),
									10) * 2 + parseInt(parameter
							.getValue("rectwidth"), 10));
	}
}
function getChildGCID(parentenodestr, grade, curchild, childs) {
	var parentserialnumber = parentenodestr.substring(
			parentenodestr.length - 8, parentenodestr.length - 4);
	var gpid = "g" + getGrade(grade) + parentserialnumber
			+ getFourCoding(curchild) + getFourCoding(childs) + "c";
	return gpid;
}
function getChildGPID(parentenodestr, grade, curchild, childs) {
	var parentserialnumber = parentenodestr.substring(
			parentenodestr.length - 8, parentenodestr.length - 4);
	var gpid = "g" + getGrade(grade) + parentserialnumber
			+ getFourCoding(curchild) + getFourCoding(childs) + "p";
	return gpid;
}
function getChildGID(parentenodestr, grade, curchild, childs) {
	var parentserialnumber = parentenodestr.substring(
			parentenodestr.length - 8, parentenodestr.length - 4);
	var gid = "g" + getGrade(grade) + parentserialnumber
			+ getFourCoding(curchild) + getFourCoding(childs);
	return gid;
}
function getFourCoding(coding) {
	if (parseInt(coding) > 999)
		return coding;
	else if (parseInt(coding) > 99)
		return "0" + coding;
	else if (parseInt(coding) > 9)
		return "00" + coding;
	else
		return "000" + coding;
}
function getGrade(grade) {
	if (parseInt(grade) > 9) {
		return grade;
	} else {
		return "0" + grade;
	}
}
function OneLineWordCount(width) {
	return parseInt(width, 10) * 6 / 80;
}
function oneLineWordCountSunx(width, fize) {
	var iWidth = parseInt(width, 10);
	var iFize = parseInt(fize, 10);
	var count = parseInt(iWidth / (iFize + 2), 10);

	return count;
}
function fizeWordSunx(row, height, fize) {
	var constant = 6;
	var afontSize = parseInt(fize, 10);
	var iHeight = parseInt(height, 10);
	var iRow = parseInt(row, 10);
	while (true) {
		if ((afontSize + constant) * iRow <= iHeight)
			break;
		else
			afontSize--;
	}
	return afontSize;
}
function oneLineWordCountSunx2(width, fize, height, length) {
	var ifontSize = parseInt(fize, 10);
	var iHeight = parseInt(height, 10);
	var iWidth = parseInt(width, 10);
	var iLength = parseInt(length, 10);
	var _wordcount = parseInt(iWidth / (ifontSize + 2), 10);;
	var s_wordcount = _wordcount;
	var _colscount = parseInt(iLength / _wordcount, 10);
	var s_colscount = _colscount;
	var constant = 7;
	var i = 0;
	var r = 0
	var isCorrect = false;
	while ((ifontSize + constant) * _colscount > iHeight) {
		isCorrect = true;
		// alert((ifontSize+constant)*_colscount+"---"+iHeight);
		// alert(ifontSize+"---"+_wordcount+"---"+_colscount);
		ifontSize--;
		i++
		r++;
		if (r >= 3) {
			_wordcount++;
			_colscount = parseInt(iLength / _wordcount, 10);
			r = 0;
		}
		if (ifontSize <= 0) {
			isCorrect = false;
			break;
		}
	}
	// alert((ifontSize+constant)*_colscount+"---"+iHeight);
	// alert(ifontSize+"---"+_wordcount+"---"+_colscount);
	var myArray = new Array();
	if (isCorrect) {
		myArray[0] = ifontSize;
		myArray[1] = _wordcount;
		myArray[2] = _colscount;
	}
	return myArray;
}
function getSiblingID(parentenodestr) {
	return parentenodestr.substring(0, 7)
			+ getFourCoding(parseInt(parentenodestr.substring(7, 11), 10) + 1)
			+ parentenodestr.substring(11);
}

function isHaveSilbingNode(parentenodestr) {
	if (parseInt(parentenodestr.substring(11, 15), 10) > 1)
		return true;
	else
		return false;
}
function isLastNode(parentenodestr) {
	if (parseInt(parentenodestr.substring(7, 11), 10) == parseInt(
			parentenodestr.substring(11, 15), 10))
		return true;
	else
		return false;
}
function isFirstNode(parentenodestr) {
	if (parseInt(parentenodestr.substring(7, 11), 10) == 1)
		return true;
	else
		return false;
}
function isRootNode(_grades, root) {
	if (_grades == root)
		return true;
	else
		return false;
}
function getGrades(parentenodestr) {
	var _gradesresult;
	if (parentenodestr.substring(1, 2) == "0")
		_gradesresult = parseInt(parentenodestr.substring(2, 3), 10);
	else
		_gradesresult = parseInt(parentenodestr.substring(1, 3), 10);
	return _gradesresult;
}
function isHaveParenteNode(parentenodestr) {
	if (parseInt(parentenodestr.substring(1, 3), 10) > 0)
		return true;
	else
		return false;
}
function getCellFrameColor(orgcolor, infokind) {
	return "#000000";
}
function getCellColor(orgcolor, infokind) {
	if (infokind == "UN")
		return orgcolor;
	else if (infokind == "UM")
		return orgcolor;
	else if (infokind == "@K")
		return orgcolor;
	else
		return orgcolor;
}
function getFontColor(fontcolor, infokind) {
	return fontcolor;
}
function getFontfamily(fontfamily) {
	if (fontfamily == "song") {
		return "";
	} else if (fontfamily == "kaiti") {
		return "KaiTi_GB2312";
	} else if (fontfamily == "lishu") {
		return "LiSu";
	} else if (fontfamily == "youyuan") {
		return "YouYuan";
	} else
		return "";
}
function getFontstyle(fontstyle) {
	if (fontstyle == "italic") {
		return "italic";
	} else if (fontstyle == "italicthick") {
		return "italic";
	} else {
		return ""
	}
}
function getFontweight(fontstyle) {
	if (fontstyle == "thick") {
		return "bold";
	} else if (fontstyle == "italicthick") {
		return "bold";
	} else {
		return ""
	}
}
function getShadowColor(orgcolor, infokind) {
	if (infokind == "UN")
		return orgcolor;
	else if (infokind == "UM")
		return orgcolor;
	else if (infokind == "@K")
		return orgcolor;
	else
		return orgcolor;
}
function oneTableTextLineSvg(width, height, str, fize) {
	var strArray = getTextEachLines(str, fize, width);
	var nLine = 0;
	if (strArray != null && strArray.length > 0)
		nLine = strArray.length;
	var nChieght = fize + (fize / 3);
	var fCell = height / nChieght;
	if (nLine > fCell) {
		while (nLine > fCell) {
			fize = fize - 1;
			if (fize <= 0)
				break;
			strArray = getTextEachLines(str, fize, width);
			if (strArray != null && strArray.length > 0)
				nLine = strArray.length;
			else
				break;
			nChieght = fize + (fize / 3);
			fCell = height / nChieght;
		}
	}
	var thevo = new Object();
	thevo.strArray = strArray;
	thevo.fize = fize;
	return thevo;

}
function getTextEachLines(str, fize, width) {
	var strArray = new Array();
	if (str == null || str == "") {
		return strArray;
	}
	var nStart = 0;
	var result = 0;
	var iHzlen = 0;
	var c;
	var charLen = 0;
	var bHz = false;
	var i = 0;
	for (i = 0; i < str.length; i++) {
		c = str.charAt(i);
		if (!reCHZ(c)) {
			charLen++;
			bHz = false;
		} else {
			iHzlen++;
			charLen = charLen + 2;
			bHz = true;
		}
		if (charLen * fize - iHzlen * (fize / 2) > width) {
			if (bHz) {
				strArray[result] = str.substring(nStart, i);
				nStart = i;
				result++;
			} else {
				strArray[result] = str.substring(nStart, i - 1);
				nStart = i - 1;
				result++;
			}
			charLen = 0;
			iHzlen = 0;
		}

	}
	if (nStart <= str.length) {
		strArray[result] = str.substring(nStart, i);
		result++;
	}
	return strArray;
}
function reCHZ(c) {
	var isCorrect = false;
	var reg = /[\u4E00-\u9FA5]/g;
	if (reg.test(c)) {
		isCorrect = true;;
	} else {
		isCorrect = false;
	}
	return isCorrect;
}
