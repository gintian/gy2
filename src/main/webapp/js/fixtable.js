// JavaScript Document
function FixTable(TableID, FixColumnNumber, width, height) {
	//alert(width);
	if ($("#" + TableID + "_tableLayout").length != 0) {
		$("#" + TableID + "_tableLayout").before($("#" + TableID));
		$("#" + TableID + "_tableLayout").empty();
	} else {
		$("#" + TableID).after("<div id='" + TableID
				+ "_tableLayout' style='overflow:hidden;height:" + height
				+ "px; width:" + width + "px;' class='fixedtab dataTable'></div>");//zgd 2014-7-23 添加了class='fixedtab dataTable' 修改信息浏览界面样式
	}
	$('<div id="' + TableID + '_tableFix"></div>' + '<div id="' + TableID
			+ '_tableHead" style="width:'+(width+17)+'px;" class="fixedtab"></div>' + '<div id="' + TableID
			+ '_tableColumn"></div>' + '<div id="' + TableID
			//+ '_tableData"></div>').appendTo("#" + TableID + "_tableLayout");
			//当无数据时按既定高度显示列表 xuj update 2012-12-20
			+ '_tableData" style="height:'+height+'px;width:'+(width)+'px" class="fixedtab"></div>').appendTo("#" + TableID + "_tableLayout");
	var oldtable = $("#" + TableID);
	var tableFixClone = oldtable.clone(true);
	tableFixClone.attr("id", TableID + "_tableFixClone");
	$("#" + TableID + "_tableFix").append(tableFixClone);
	var tableHeadClone = oldtable.clone(true);
	tableHeadClone.attr("id", TableID + "_tableHeadClone");
	$("#" + TableID + "_tableHead").append(tableHeadClone);
	var tableColumnClone = oldtable.clone(true);
	tableColumnClone.attr("id", TableID + "_tableColumnClone");
	$("#" + TableID + "_tableColumn").append(tableColumnClone);
	$("#" + TableID + "_tableData").append(oldtable);
	$("#" + TableID + "_tableLayout table").each(function() {
		$(this).css("margin", "0");
	});
	var HeadHeight = $("#" + TableID + "_tableHead thead").height();
	HeadHeight += 2;
	$("#" + TableID + "_tableHead").css("height", HeadHeight);
	$("#" + TableID + "_tableFix").css("height", HeadHeight);
	var ColumnsWidth = 0;
	var ColumnsNumber = 0;
	$("#" + TableID + "_tableColumn tr:last td:lt(" + FixColumnNumber + ")")
			.each(function() {
				ColumnsWidth += $(this).outerWidth(true);
				ColumnsNumber++;
			});
	ColumnsWidth += 2;
	if ($.browser.msie) {
		switch ($.browser.version) {
			case "7.0" :
				if (ColumnsNumber >= 3)
					ColumnsWidth--;
				break;
			case "8.0" :
				if (ColumnsNumber >= 2)
					ColumnsWidth--;
				break;
		}
	}
	$("#" + TableID + "_tableColumn").css("width", ColumnsWidth);
	$("#" + TableID + "_tableFix").css("width", ColumnsWidth);
	$("#" + TableID + "_tableData").scroll(function() {
		$("#" + TableID + "_tableHead").scrollLeft($("#" + TableID
				+ "_tableData").scrollLeft());
		$("#" + TableID + "_tableColumn").scrollTop($("#" + TableID
				+ "_tableData").scrollTop());
	});
	$("#" + TableID + "_tableFix").css({
		"overflow" : "hidden",
		"position" : "relative",
		"z-index" : "50",
		"background-color" : "#FFFFFF"
	});
	$("#" + TableID + "_tableHead").css({
		"overflow" : "hidden",
		"width" : width - 17,
		"position" : "relative",
		"z-index" : "45",
		"background-color" : "#FFFFFF"
	});
	$("#" + TableID + "_tableColumn").css({
		"overflow" : "hidden",
		"height" : height - 17,
		"position" : "relative",
		"z-index" : "40",
		"background-color" : "#FFFFFF"
	});
	//alert($("#" + TableID).width());
	//alert(width);
	if(width>=$("#" + TableID).width()){////根据显示宽度踢出横向滚动条xuj update 2012-12-20
		$("#" + TableID + "_tableData").css({
			"overflow-y" : "scroll",
			"overflow-x" : "hidden",//无此设置非ie浏览器会有横向滚动条xuj update 2012-12-20
			"width" : width,
			"height" : height,
			"position" : "relative",
			"z-index" : "35"
		});
		$("#" + TableID + "_tableColumn").css({
			"display" : "none"
		});
		$("#" + TableID + "_tableFix").css({
			"display" : "none"
		});
	}else{
		$("#" + TableID + "_tableData").css({
			"overflow" : "scroll",
			"width" : width,
			"height" : height,
			"position" : "relative",
			"z-index" : "35"
		});
	}
	/*if ($("#" + TableID + "_tableHead").width() > $("#" + TableID
			+ "_tableFix table").width()) {
		$("#" + TableID + "_tableHead").css("width",
				$("#" + TableID + "_tableFix table").width());
		$("#" + TableID + "_tableData").css("width",
				$("#" + TableID + "_tableFix table").width() + 17);
	}*/
	if ($("#" + TableID + "_tableColumn").height() > $("#" + TableID
			+ "_tableColumn table").height()) {
		$("#" + TableID + "_tableColumn").css("height",
				$("#" + TableID + "_tableColumn table").height());
		//当无数据时按设定高度显示xuj update 2012-12-20
		//$("#" + TableID + "_tableData").css("height",
				//$("#" + TableID + "_tableColumn table").height() + 17);
		//显示左边线
		$("#" + TableID + "_tableData").css({
			"border-left":"1pt solid",
			"border-bottom":"0pt solid",
			"table-layout":"fixed"
		});
		$("#" + TableID + "_tableHead").css({
			"border-left":"1pt solid"
		});
	}
	$("#" + TableID + "_tableFix").offset($("#" + TableID + "_tableLayout")
			.offset());
	$("#" + TableID + "_tableHead").offset($("#" + TableID + "_tableLayout")
			.offset());
	$("#" + TableID + "_tableColumn").offset($("#" + TableID + "_tableLayout")
			.offset());
	$("#" + TableID + "_tableData").offset($("#" + TableID + "_tableLayout")
			.offset());
}
