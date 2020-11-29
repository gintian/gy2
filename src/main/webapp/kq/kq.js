/*
 * 类似登记表的查询
 */
function common_query_comroww(infor,query_type,row_num)
{
   
    var dw=600,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    var strurl="/kq/options/sign_point/select_query_fields.do?b_init=link`type="+infor+"`query_type="+query_type+"`row_num="+row_num;
    if($URL)
    	strurl = $URL.encode(strurl);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
    var objlist =window.showModalDialog(iframe_url,null,"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth=600px;dialogHeight=450px;resizable=yes;status=no;");  
    /*传回来的对象类型发生变化啦 instanceof Array*/
    if(objlist)
    {
        var objarr=new Array();
        for(var i=0;i<objlist.length;i++)
            objarr[i]=objlist[i];
    }
    return objarr;
}
/**
 * 获取考勤的节假日、公休日、倒休日在日期控件上显示
 * @return
 */
function getKqCalendarVar()
{
 	var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
}

function setkqcalendar(outparamters)
{
 	weeks = outparamters.getValue("weeks");  
 	feasts = outparamters.getValue("feasts"); 
 	turn_dates = outparamters.getValue("turn_dates"); 
 	week_dates = outparamters.getValue("week_dates");
 	kq_duration = outparamters.getValue("kq_duration");
}

/**
 * 将日期格式yyyy-MM-dd转换为yyyy.MM.dd
 * @param obj
 * @return
 */
function rep_dateValue(obj)
{
    var d_value = obj.value;
    if(d_value != "")
    {
    	d_value = d_value.replace("-",".");
    	d_value = d_value.replace("-",".");
    	obj.value = d_value;
    }
}
/**
 * 按格式截取日期
 * @param obj
 * @param len
 * @return
 */
function rep_dateValue(obj, len)
{
	var d_value = obj.value;
    if(d_value != "")
    {
    	d_value = d_value.replace("-",".");
    	d_value = d_value.replace("-",".");
    	d_value = d_value.substring(0, len);
    	obj.value = d_value;
    }
}
/**
 * 保存当前页面的日期值
 * @param obj
 * @return
 */
function saveCurrDateValue(obj)
{
	var d_value = obj.value;
	var otherObj = $('dateValue');
	otherObj.value = d_value;
}

/**
 * 判断选择的日期是否在考勤期间之内,如果不在则还原之前的日期值并给予提示
 * @param obj
 * @param kqduration
 * @return
 */
function restoreDateValue(obj,kqduration){
    var durationArr = kq_duration.split("`");
    var kq_start = durationArr[0];
    var kq_end = durationArr[1];
    kq_start = kq_start.replace(/\./g, "/");
    kq_end = kq_end.replace(/\./g, "/");
    var KqStartDate = new Date(Date.parse(kq_start.replace(/-/g, "/"))); //考勤开始日期
    var KqEndDate = new Date(Date.parse(kq_end.replace(/-/g, "/")));//考勤结束日期
    
    var d_value = obj.value;
    if(d_value != "")
    {
    	d_value = d_value.replace(/\./g, "/");
        var date_value = new Date(Date.parse(d_value.replace(/-/g, "/"))); //当前选择的日期
        if(date_value.getTime() > KqEndDate.getTime() || date_value.getTime() < KqStartDate.getTime()){
        	var otherObj = $('dateValue').value;
        	obj.value = otherObj;
        	kq_duration = kq_duration.replace("`"," - ");
        	alert("请选择考勤期间（"+kq_duration+"）范围内的日期！");
        }
    }
}

/**
 * 隐藏人员库
 *  * @return
 */
function hide_nbase_select(pre){
	var obj = document.getElementById(pre);
	if(obj != null && obj.options.length == 1){
		obj.style.display = "none";
	}
}

/**
 * 生成一个Ext时间选择组件,页面中需引入extjs
 * @param elementId 时间组件id
 * @param renderToElementId 时间组件放置到的dom元素id(一般为一个div)
 * @param times 时间点08:30
 */
function createTimeField(elementId,renderToElementId,times) {
	var timefield = Ext.getCmp(elementId);
	if(timefield)
		timefield.destroy();
	
	timefield = Ext.create("Ext.form.field.Time",{
	  id : elementId,
	  format: 'H:i',
	  fieldLabel: false,
	  width : 70,
	  formatText: 'HH:mm',
	  minValue: '00:00',
	  maxValue: '23:59',
	  margin : '0 0 0 0',
	  increment: 5,
	  anchor: '100%',
	  invalidText: '{0}请输入正确的时间！',
	  altFormats : "g:ia|g:iA|g:i a|g:i A|h:i|g:i|H:i|ga|ha|gA|h a|g a|g A|gi|hi|Hi|gia|hia|g|H|gi a|hi a|giA|hiA|gi A|hi A",
	  listConfig : {
          maxHeight : 160
      },
	  renderTo: renderToElementId 
	});
	if(Ext.isEmpty(times)){
		times = "08:00";
	}
	timefield.setValue(new Date("2017/01/01 "+times+":00"));
}

/**
 * 时间轴刷卡提示信息
 *  #迟到提示语
 *	kq_late_hint=迟到毁一生
 *	#早退
 *	kq_early_hint=早退毁一生
 *	#旷工
 *	kq_absent_hint=旷工毁一生
 *	#正常上班
 *	kq_normal_hint=时间达人
 *	#下班晚走
 *	kq_leave_late_hint=工作狂人
 */
function getKqCardTimeInfo(flag) {
	
	if(!Ext.isEmpty(flag)){
		if(flag == 'late'){
			return "  迟到毁一生";
		}else if(flag == 'early'){
			return "  早退毁一生";
		}else if(flag == 'absent'){
			return "  旷工毁一生";
		}else if(flag == 'normal'){
			return "  时间达人";
		}else if(flag == 'leave_late'){
			return "  工作狂人";
		}else{
			return "";
		}
	}else{
		return "";
	}
}

/**
 * 基本班次、考勤规则、考勤期间三个页面中间的树页面在edge会有滚动条
 * @param elementId
 */
function tabWidthForEdge(elementId) 
{
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
    var isEdge = userAgent.indexOf("Edge") > -1 //判断是否IE的Edge浏览器 
    if(isEdge) 
        document.getElementById(elementId).width = "100%";
}






