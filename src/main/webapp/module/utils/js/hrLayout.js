var HrLayout = {};
HrLayout.pageid = 0;
// 用于区分页面加载的区域是否含有字幕区域，ture：含有 | false：不含有
HrLayout.flag = true;
// 查询区域信息
HrLayout.load = function() {
	var url =  window.location.search;
	var map = new HashMap();
	map.put("urlpath", url);

	Rpc( {
		functionId : '1010010101',
		success : HrLayout.searchSucc
	}, map);
}
// 加载区域信息
HrLayout.searchSucc = function(response) {
	var map = Ext.decode(response.responseText);
	var list = map.panellist;
	HrLayout.parms = map.parms;
	HrLayout.pageid = map.pageid;
	HrLayout.layoutTC(list);

}
// 创建分区并判断区域是否含有子区域
HrLayout.addPanels = function(list) {
	for ( var i = 0; i < list.length; i++) {
		var panelParm = list[i];
		if (0 != panelParm.layout) {
			HrLayout.searchChild(panelParm.regionid, panelParm.layout);
		} else {
			HrLayout.addHtml(panelParm);
		}
	}
}
// 查询子区域信息
HrLayout.searchChild = function(id, layout) {
	var map = new HashMap();
	map.put("parentid", id);
	map.put("pageid", HrLayout.pageid);
	map.put("layout", layout);

	Rpc( {
		functionId : '1010010101',
		success : HrLayout.searchChlidSucc
	}, map);
}
// 加载区域的子区域
HrLayout.searchChlidSucc = function(response) {
	var map = Ext.decode(response.responseText);
	var list = map.panellist;
	var layout = map.layout;

	if (list == null || list.length < 1)
		return;

	if (0 != layout) {
		if (1 == layout)
			HrLayout.layoutCenter(list);
		else if (2 == layout)
			HrLayout.layoutLC(list);
		else if (3 == layout)
			HrLayout.layoutLCR(list);
		else if (4 == layout)
			HrLayout.layoutTC(list);
		else if (5 == layout)
			HrLayout.layoutTCB(list);
	}

}
// 创建一个panel
HrLayout.layoutCenter = function(Parm) {
	HrLayout.createPanels(Parm, [ 'center' ]);
}
// 创建两列panel
HrLayout.layoutLC = function(Parm) {
	HrLayout.createPanels(Parm, [ 'west', 'center' ]);
}
// 创建三列panel
HrLayout.layoutLCR = function(Parm) {
	HrLayout.createPanels(Parm, [ 'west', 'center', 'east' ]);
}
// 创建两行panel
HrLayout.layoutTC = function(Parm) {
	HrLayout.createPanels(Parm, [ 'center', 'south' ]);
	if (HrLayout.flag == true)
		HrLayout.flag = false;
}
// 创建三行panel
HrLayout.layoutTCB = function(Parm) {
	HrLayout.createPanels(Parm, [ 'north', 'center', 'south' ]);
}
// 创建多个panel
HrLayout.createPanels = function(layout, region) {

	for ( var i = 0; i < layout.length; i++) {
		if (i > 2)
			break;

		var panelParm = layout[i];
		var panel = HrLayout.panels(panelParm, region[i]);
		if (panelParm.parentRegionid != panelParm.regionid) {
			Ext.getCmp('panel' + panelParm.parentRegionid).add(panel);
		} else {
			HrLayout.mainPanel.add(panel);
		}
	}

	HrLayout.addPanels(layout);
}
// 创建panel
HrLayout.panels = function(panelParm, region) {

	var height = panelParm.height;
	var width = panelParm.width + "%";

	var config = {
		xtype : 'panel',
		region : region,
		layout : 'border',
		id : 'panel' + panelParm.regionid,
		width : width,
		border : false,
		align : 'center',
		bodyStyle : 'background:#F3F3F3;'
	};
	// 判断加载的区域是否是字幕，若是，则字幕区域的高度固定为30px
	if (HrLayout.flag) {
		if ('center' != region)
			config.height = 30;
	} else
		config.height = panelParm.height + "%";

	var title = panelParm.name;
	if (!HrLayout.flag && title != null && title.length > 0) {
		var jumpUrl = panelParm.jumpUrl;
		if (jumpUrl != null && jumpUrl.length > 0)
			title = "<a href='" + jumpUrl + "'>" + title + "</a>";

		config.title = "<img src='/images/icon.png' style='margin-right: 5px;' align='absmiddle'>"
				+ title;
	}

	var panel = new Ext.Panel(config);
	return panel;
}
// 加载区域内容
HrLayout.addHtml = function(panelParm) {
	var panel = Ext.getCmp('panel' + panelParm.regionid).getTargetEl();
	if (Ext.isEmpty(panel))
		return;

	var html = "";
	if (HrLayout.flag) {
		var content = panelParm.content;
		// 如果字幕的内容为空，则字幕区域不显示
		if (content == null || content.length < 1)
			Ext.getCmp('panel' + panelParm.regionid).hide();

		html = "<div styl='height:30px;width:100%;line-height: 30px;vertical-align: middle;'><iframe border='none' src='"
				+ content + "' id='iframe'" + panelParm.regionid
				+ "' frameborder='0' marginwidth='0' marginheight='0' width='100%' height='30'></iframe></div>";
	} else {
		if (panelParm.content != null && panelParm.content.length > 0)
			html = "<iframe border='none' src='"
					+ panelParm.content + HrLayout.parms + "&pageid="+HrLayout.pageid+"' id='iframe" + panelParm.regionid
					+ "' frameborder='0' marginwidth='0' marginheight='0' width='100%' height='100%'></iframe>";
	}

	if (html == null || html.length < 1)
		return;

	panel.setHTML(html);
}
