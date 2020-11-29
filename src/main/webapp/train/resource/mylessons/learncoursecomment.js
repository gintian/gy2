function LearnCouseCommentList(courseid, flag, sign) {
	if ("1" == sign) {
		Ext.getDom("divlist" + flag).innerHTML = "";
	}

	var map = new HashMap();
	map.put("courseid", courseid + "");
	map.put("flag", flag);
	Rpc({
		functionId : '2020030201',
		success : CourseSucce
	}, map);

}

function CourseSucce(response) {
	var value = response.responseText;
	var map = Ext.decode(value);
	if (map.commentList == null)
		return;

	for ( var i = 0; i < map.commentList.length; i++) {
		AddList(map.commentList[i], map.flag);
	}
}

function AddList(map, flag) {
	var dh = Ext.DomHelper;
	var divparam = {
		tag : 'div',
		name : 'divf'
	};
	var div = dh.createDom(divparam);
	dh.applyStyles(div,"margin:5px 5px 0 5px;width:270px;padding-bottom:10px;border-bottom:1px #D5D5D5 dashed; overflow:hidden;");
	var div_bottom = dh.createDom({
		tag : 'div'
	});
	dh.applyStyles(div_bottom, "text-align:left;");
	var div_bottom_top = dh.createDom({
		tag : 'div'
	});
	var divclear = dh.createDom({
		tag : 'div'
	});
	dh.applyStyles(divclear, "clear:both");
	div.appendChild(divclear);

	if ("0" == flag) {
		var div_top = dh.createDom({
			tag : 'div'
		});
		dh.applyStyles(div_top, "float:left; height:30px;");
		var innerHTML = "<img height='30px' width='30px'  class='img-circle' src='"
				+ map[1] + "'/>";
		var imgpart = {
			tag : 'div',
			html : innerHTML
		};

		dh.append(div_top, imgpart);

		div.appendChild(div_top);
	}

	var html = "";
	if ("0" == flag) {
		html += "<table width='230px' cellpadding='0' cellspacing='0' style='padding-left: 3px;margin-top: -3px;' ><tr><td  class='fontstyle' >" + map[0]
				+ "</td>";
		html += "<td  class='fontstyle' >" + map[3] + "</td>";
		if(opt!="sss" && map[2]=="yes" ){
			html += "<td  class='fontstyle'  style='text-align:right;'><a href=\"javascript:;\" onclick=\"Dele('"
				+ map[5]
				+ "','"
				+ flag
				+ "','"
				+ map[6]
				+ "','"
				+ map[2]
				+ "')\">删除</a></td></tr>";
		}
		html += "<tr><td colspan='3' style=\"word-wrap:break-word;word-break:break-all;\">"
				+ map[4].replace(/\n/g, "<br>") + "</td>";
	} else {
		html += "<table width='260px' cellpadding='0' cellspacing='0'><tr><td  class='fontstyle' >" + map[0]
				+ "</td>";
		html += "<td  class='fontstyle'  style='width:\"20%\";text-align:right;'><a href=\"javascript:;\" onclick=\"Dele('"
				+ map[2]
				+ "','"
				+ flag
				+ "','"
				+ map[3]
				+ "')\">删除</a></td></tr>";
		html += "<tr><td colspan='2' style=\"word-wrap:break-word;word-break:break-all;\">"
				+ map[1].replace(/\n/g, "<br>") + "</td>";// \r\n

	}

	html += "</tr></table>";
	var bottom = {
		tag : 'div',
		html : html
	};
	dh.append(div_bottom_top, bottom);

	div_bottom.appendChild(div_bottom_top);
	div.appendChild(div_bottom);
	Ext.getDom("divlist" + flag).appendChild(div);

}

function LearnCouseCommentInput(courseid, flag) {
	var title = "保存";
	if (flag == 0)
		title = "发表";
	
	var frmLogin = Ext.create('Ext.form.FormPanel', {
	    width: '100%',
	    bodyPadding: '10 10 0 10',
	    border:1,
	    items: [{
	        xtype: 'textareafield',
	        emptyText: "请在此输入文字",
	        grow: true,
	        name: "comment" + flag,
	        fieldLabel: false,
	        height:125,
	        anchor    : '100%'
	    }],
		buttons : [ {
			text : title,
			margin : '0 10 5 5',
			handler : function() {
				var commenttext = frmLogin.getForm().findField("comment" + flag).getValue().toString();
				if (commenttext == null || commenttext == "")
					return;
				
				var map = new HashMap();
				map.put("comment", commenttext);
				map.put("courseid", courseid + "");
				map.put("flag", flag);
				Rpc({
					functionId : '2020030202',
					success : SuccSave
				}, map);

			}
		} ],
		renderTo : "top" + flag
	});

}

function SuccSave(response) {
	var sign = "1";
	var value = response.responseText;
	var map = Ext.decode(value);
	Ext.query("[name='comment" + map.flag+"']")[0].value="";
	LearnCouseCommentList(map.courseid, map.flag, sign);
}

function Dele(id, flag, courseid, charge) {
	if ("no" == charge) {
		alert("此项非本人写入，不能删除");
		return;
	}
	var map = new HashMap();
	map.put("id", id);
	map.put("flag", flag);
	map.put("courseid", courseid + "");
	Rpc({
		functionId : '2020030203',
		success : SuccDele
	}, map);
}

function SuccDele(response) {
	var sign = "1";
	var value = response.responseText;
	var map = Ext.decode(value);
	LearnCouseCommentList(map.courseid, map.flag, sign);
}

function Company(courseid, flag) {
	var dh = Ext.DomHelper;
	if ("0" == flag)
		Ext.getDom("coursecomments").innerHTML = "";
	else
		Ext.getDom("coursenotes").innerHTML = "";

	if ("me" == opt) {
		var id = 'top' + flag;
		var top = {
			tag : 'div',
			id : id
		};
		var top = dh.createDom(top);
		dh.applyStyles(top,"width:100%;height:185px;padding-bottom:3px;border-bottom:1px #c5c5c5 solid;");
		if ("0" == flag)
			Ext.getDom("coursecomments").appendChild(top);
		else
			Ext.getDom("coursenotes").appendChild(top);
	}

	var id = 'divlist' + flag;
	var top = {
		tag : 'div',
		id : id
	};
	var divlist = dh.createDom(top);
	if ("me" == opt)
		dh.applyStyles(divlist, "width:100%;height:70%;overflow:auto;");

	else
		dh.applyStyles(divlist, "width:100%;height:100%;overflow:auto;");
	if ("0" == flag)
		Ext.getDom("coursecomments").appendChild(divlist);
	else
		Ext.getDom("coursenotes").appendChild(divlist);

	if ("me" == opt)
		LearnCouseCommentInput(courseid, flag);

	LearnCouseCommentList(courseid, flag, '');
}
