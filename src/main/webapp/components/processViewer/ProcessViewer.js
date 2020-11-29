/**
 * 参数说明
 * processData[]审批过程数据[{"name":"fsk","time":"2020-03-03 17:21:41","opinion":"0","message":"请核实情况"}]。。。
 * name 左侧显示名称
 * time 时间
 * message 审批意见
 * opinion  图标  0：同意，1 ：退回
 */
Ext.Loader.loadScript({url:'/components/processViewer/css/approvalProcess.css',scope:this});
Ext.define('EHR.processViewer.ProcessViewer', {
	extend:'Ext.container.Container',
	config:{
		layout: {
	        type: 'vbox',
	        align: 'center'
	    },
	    scrollable: true,
	    bodyStyle: 'overflow-y:auto;overflow-x:hidden;',
	    processData:[],
	    width: 460,
	    height: 520,
	},
    initComponent:function(){
		this.callParent(arguments);
		this.initProcessData();
	},
  //判断是否存在审批数据
    initProcessData : function(){
    	var html="";
	    if (this.processData.length == 0) {//没有数据
	        html = "<p style='font-size: 14px;color: #5c5c5c;'>" + kq.dataAppeal.appProcessErr + "</p>";
	        var image = Ext.create("Ext.Img", {
	            width: 142,
	            height: 142,
	            align: 'center',
	            src: '/components/processViewer/images/notconfig.png'
	        });
	        var component = Ext.create("Ext.Component", {
	            html: html
	        });
	        this.add(image);
	        this.add(component);
	    } else {
	        //存在数据，组装审批过程html
	        html = this.getApproveHtml(this.processData);
	        var component = Ext.create("Ext.Component", {
	            html: html
	        });
	        this.add(component);
	    }
    },
	getApproveHtml: function (processData) {//组装审批过程html
        var html = "<div style='overflow-y:auto;height:480px;width:430px;'><table border='0' cellspacing='0' cellpadding='0' width=100% class='workflow-tuli'>";
        for (var z = 0; z < processData.length; z++) {
            var map = processData[z];
            var img = "";
            if (map.opinion != '') {
            	if (map.opinion == '0')
            		img = "<image width='40px' height='40px' src='/components/processViewer/images/agree.png' />";
            	else if(map.opinion == '1')
            		img = "<image width='40px' height='40px' src='/components/processViewer/images/disagree.png' />";
			}
            html += "<tr><td width='80px'></td><td width='12px' class='workflow-timeLine-shortline'></td><td></td></tr>"
                + "<tr><td width='45px;'><div class='workflow-approver'>";

            html += map.name;
            

            html += "</div></td>" +
                "<td width='12px' class='workflow-timeLine-longline'>" +
                "<div class='workflow-timeLine-point'></div>" +
                "</td>" +
                "<td style='padding-left:10px;padding-right:20px;'>" +
                "<div class='workflow-timeline-textarr'>" +
                "<div class='arrow'><em></em><span></span></div><table width='100%' style='font-size:1em;table-layout:fixed;word-break:break-all; word-wrap:break-word;'><tr><td valign='top' style='margin-left:5px;padding-top: 6px;'>" + map.time + "</td>" +
                "<td width='30px' valign='middle' rowspan='2'>" + img + "</td></tr><tr><td valign='top' style='word-break: break-word;width:270px' colspan='2'>" + map.message + "</td></tr></table>" +
                "</div>" +
                "</td></tr>"
                + "<tr><td ></td><td width='12px' class='workflow-timeLine-shortline'></td><td></td></tr>";
        }

        html += "</table></div>";
        return html;
    },
});