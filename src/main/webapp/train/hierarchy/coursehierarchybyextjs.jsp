<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<link rel="stylesheet" href="/train/resource/mylessons/learncourse.css"
	type="text/css"></link>
	<script language='JavaScript' src='../../../components/extWidget/proxy/TransactionProxy.js'></script>
<style>
.fontstyle {
	font-family: "微软雅黑";
	font-size: 14px;
	font-weight: bold;
	padding: 0;
	margin: 0;
}

.divcss {
	height:75px;
	overflow:hidden;
}

.divcss a:LINK {
	color: #000000;
}

.divcss a:hover {
	color: #1b4a98;
}
.divcss a:active {
	color: #000000;
}
.divcss a:visited {
	color: #000000;
}

</style>
<html>
	<body>
		<div id='panel'></div>
	</body>
	<script type="text/javascript">
	var dh = Ext.DomHelper;
	var store = new Ext.create('Ext.data.Store', {
	    storeId: "FontColorStore",
	    pageSize:10,
	    fields:["r5000","r5003","r5004","r5009","r5012","r5039","r5016","lesson_from","hot","count","id","imageurl","r5039Name","lessondesc"],
	    proxy:{
			type:'transaction',
		    functionId:'2020030204',
		    extraParams:{
		    	a_code:'${courseHierarchyForm.a_code}'
			},
			reader:{
			   type:'json',
               root:'data',
               totalProperty:'totalCount'
			}
		}
	});

	Ext.onReady(function() {
		var pagingToolbar = new Ext.PagingToolbar( {
			emptyMsg : "没有数据",
			displayInfo : true,
			height: 30,
			cls:'tbar-bg',
			displayMsg : "显示{0}-{1}条，共{2}条",
			store : store
		});

		var north = new Ext.Panel( {
			xtype : 'panel',
			id : 'north',
			height:40,
			html : '<div id="searchlesson" style="padding-top:10px;width:100%;height:40px;padding-left:5px;border-bottom:1px #D5D5D5 dashed;"></div>',
			region : 'north',
			border : 1
		});
		
		var Panel = new Ext.Panel( {
			xtype : 'panel',
			id : 'center',
			html : '<div id="lesson" style="width: 100%;height: 100%;padding: 0;"></div>',
			region : 'center',
			border : 1
		});

		var south = new Ext.Panel( {
			xtype : 'panel',
			id : 'south',
			height:28,
			region : 'south',
			border : 1,
			bbar:pagingToolbar
		});

		new Ext.Viewport( {
			layout : "border",
			items : [ north, Panel, south ]
		});

		var centerDiv = dh.createDom({tag:'div',id:'showlesson',bbar : pagingToolbar});
		dh.applyStyles(centerDiv,"margin:0 5px 0 5px;width:100%;height:100%;padding-bottom:10px;border-bottom:1px #D5D5D5 dashed; overflow:auto;");
			
		Ext.getDom("lesson").appendChild(centerDiv);

		addText(store);
		storeLoad(store, '');
	});

	function addText(store){
		Ext.create('Ext.panel.Panel', {
            width: 315,
            height: 25,
            layout:'column',
            border: false,
            renderTo: 'searchlesson',
            items: [{
                id: 'boxtext',
                xtype: 'textfield',
                columnWidth: .85,
                emptyText:"输入课程名称、讲师姓名、课程简介",
                fieldCls: 'searchbox',
                listeners: {
                    specialkey: function(field, e){
                        if (e.getKey() == e.ENTER) {
                        	var search = Ext.getCmp('boxtext').getValue(); //获取文本框值
							search = getEncodeStr(search);
                    		storeLoad(store, search);
                        }
                    }
                }
            },{
            	xtype: 'button',
            	text: '查询',
            	margin: '0 0 0 5',
            	handler: function () {
            		var search = Ext.getCmp('boxtext').getValue(); //获取文本框值
					search = getEncodeStr(search);
            		storeLoad(store, search);
                }
             }]
        });
	}

	function storeLoad(store, search){
		store.on("beforeload",function(){
			Ext.apply(store.proxy.extraParams,{search: search});
		}); //传递参数
		
		store.on('load',function(s,array){
			document.getElementById("showlesson").innerHTML="";
			Ext.each(array,adds);
		});
		store.load();
	}
	
	function adds(record){
		var r5000 = record.get('r5000');
		var r5004 = record.get('r5004');
		var r5003 = record.get('r5003');
		var r5009 = record.get('r5009');
		var r5012 = record.get('r5012');
		var r5016 = record.get('r5016');
		var r5039 = record.get('r5039');
		var lessonFrom = record.get('lesson_from');
		var hot = record.get('hot');
		var count = record.get('count');
		var id = record.get('id');
		var imageurl = record.get('imageurl');
		var r5039Name = record.get("r5039Name");
		var lessondesc = record.get("lessondesc");

		var div = dh.createDom({tag:'div',name:'divf'});
		dh.applyStyles(div,"width:100%;height:130px;padding:5px;border-bottom:1px #D5D5D5 dashed; overflow:hidden;");

		var div_left = dh.createDom({tag:'div'});
		dh.applyStyles(div_left,"float:left;width:240px;height:120px;");

		var div_right = dh.createDom({tag:'div'});
		dh.applyStyles(div_right,"text-align:left;margin-left:245px;");
		
		var divclear = dh.createDom({tag:'div'});
		dh.applyStyles(divclear,"clear:both");
		div.appendChild(divclear);

		var flag = 0;
		if (lessonFrom != null && lessonFrom.length > 0)
			flag = 1
	
		var leftHTML = "<a href='###' onclick=\"learn('"+r5000+"','"+r5004+"',"+flag+");\"><img height='120px' width='240px' src='"+imageurl+"'/></a>";
		dh.insertHtml('afterBegin', div_left, leftHTML);
		div.appendChild(div_left);

		var html = "<table width='100%' height='100%'><tr><td style='padding:0 10px 0 0;margin:0px;'><p class='fontstyle' style='float:left'>";
		html += "<a href='###' style='font-size: 14px;' onclick=\"learn('"+r5000+"','"+r5004+"',"+flag+");\">" + r5003 + "</a></p>";
		if ("1"==hot)
			html += "<img height='14px' width='24px' src='/images/hot.png'/>";

		if(r5039!=null && r5039.length > 0)
			html += "<p class='fontstyle' style='float:right;color: #747474;'>【" +r5039Name + "："+r5039+"】</p>";

		html += "</td></tr>";
		html += "<tr><td height='75px' valign='top' style='color:#747474;padding:0 10px 0 0;margin:0px;max-height:75px;'>";
		html += "<div class='divcss'>";
		if (lessondesc.length > 200)
			html += "<a href='###' onclick='showLessondesc(\"" + r5003 + "\",\"" + r5012 + "\")'>" + lessondesc + "</a>";
		else
			html += lessondesc;
		
		html += "<div>";
		html += "</td></tr><tr><td style='padding:0 10px 0 0;margin:0px;'><p style='padding: 0;margin: 0;float:left'>";
		if ("1"==r5016 && (lessonFrom == null || lessonFrom.length < 1))
			html += "<a href='###' onclick=\"courseselect('"+r5000+"','','"+lessonFrom+"');\">加入我的课程</a>";
		else if ("1" == lessonFrom && (id != null && id.length > 0))
			html += "<a href='###' onclick=\"courseselect('','"+id+"','"+lessonFrom+"');\" >从我的课程撤销</a>";
		else
			html += "&nbsp;";
			
		html += "</p><div style='color:#747474;padding: 0;width:170px;margin: 0;float:right'>";

		if (r5009 !=null && r5009 > 0)
			html += "<span style='width:70px;'><img align='absmiddle' height='15' width='15' style='margin:0 3px 0 0;' src='/images/time.png'/>"+r5009+"课时 ";

		html += "</span><span style='width:100px;float:right'><img align='absmiddle' height='15' width='15' style='margin:0 3px 0 20px;' src='/images/students.jpg'/>"+count+"人";
		html += "</span></div></td></tr></table>";
			
		var spec = {
			    tag: 'div',
			    html: html
			};
		
		dh.append(div_right,  spec);
		div.appendChild(div_right);
			
		Ext.getDom("showlesson").appendChild(div);
	}

	function courseselect(r5000,id,lessonFrom){
///zhangcq 2016/5/4 修改提示信息
		if("1"==lessonFrom){
			Ext.Msg.confirm('提示信息', '您的学习 笔记、学习进度以及考试记录会被同时置空，是否继续？', function(btn){
	            if (btn == 'yes'){   
	            	select(r5000,id)
	            }
	        });
		} else {
			select(r5000,id)
		}
	}
	
    function select(r5000,id){
        var map = new HashMap();
    	map.put("r5000",r5000);
    	map.put("id",id);
    	Rpc({functionId:'2020031012',success:onRefresh},map);
    }
        
	function onRefresh(response){
		var map = Ext.decode(response.responseText);
		if(!map)
			return;
		
		store.on('load',function(s,array){
			document.getElementById("showlesson").innerHTML="";
			Ext.each(array,adds);
		});
		
		store.load();
	}
	
	function learn(courseid,classes,flag) {
		var url = "/train/resource/mylessons/learncoursebyextjs.jsp?";
		if(flag == 0)
			url += "opt=sss`piv=1`classes="+classes+"`lesson=" + courseid;
		else
			url += "opt=me`classes="+classes+"`lesson=" + courseid+"`lessonState=ing";
		var fram = "/train/resource/mylessons/learniframe.jsp?src="+$URL.encode(url);
		window.open(fram,'','fullscreen=no,left=0,top=0,width='+ (screen.availWidth - 10) +',height='+ (screen.availHeight-50) +',scrollbars,resizable=no,toolbar=no,location=no,status=no,menubar=no');
	}

	function showLessondesc(name,msg){

		 new Ext.Window({  
	            title : name+'简介',
	            closable : true,  
	            border : false,  
	            width : 400,  
	            height : 250,  
	            layout : 'border',  
	            autoScroll : true,
	            html : '<div>'+msg+'<div>'
	        }).show();  
	}
	
</script>
</html>
