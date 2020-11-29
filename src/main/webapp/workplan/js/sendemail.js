/**
 *  发送邮件，提示信息 
 *  
 * @param nbaseA0100 人员编号 ：nbase+A0100
 * @param num 发送类型，单发还是群发 
 * @param remindType 总结或评价 
 * @return
 */
function sendEmail(nbaseA0100,num ,remindType,e0122,p0100) {

	var str_a0100 = "";
	var saveDepartName = "";
	var hashvo = new HashMap();
	var hr='HR';

	if (num == "one") {// 单个
		if('' == nbaseA0100){
			//str_a0100 = Ext.getDom("nbaseA0100").value;	
			alert("该部门没有负责人，邮件不能发送！");
			return;
		}else if(nbaseA0100=="person")
		{
			str_a0100 = Ext.getDom("nbaseA0100").value;
		}
		else
			str_a0100 = nbaseA0100;
		if(str_a0100 == ""){
			alert("请选择邮件通知对象 ！ ");
			return;
		}
		

	} else if (num == "more") { // 群发（仅适用于我的团队的群发）
		// 获取未提交人员的 a0100 
		str_a0100 ="HR";
		if(searchtype=="quicksearch")
			hashvo.put("querypara", getEncodeStr(Ext.getDom("searchtext").value));
		if(searchtype=="commonsearch")
			hashvo.put("commonpara", getEncodeStr(commonsearchtext));
	}
	if('' == remindType)
		hr='';
	hashvo.put("a0100", str_a0100);
    hashvo.put("type",Ext.getDom('type').value);
    hashvo.put("remindType",remindType);
    hashvo.put("cycle", Ext.get('cycle').getValue());
    hashvo.put("year", Ext.get('year').getValue());
    hashvo.put("month", Ext.get('month').getValue());
    hashvo.put("week", Ext.get('week').getValue());
    hashvo.put("e0122", e0122);
    hashvo.put("p0100", Ext.isEmpty(p0100)?"":p0100);
    hashvo.put("isHR", hr);
	//if (confirm("您是否要发送邮件提醒？")) {
		Rpc( {
		functionId : '9028000804',
		success : sendOK
	}, hashvo);
	//}

}
function sendOK(response) {
	var value = response.responseText;
	var map = Ext.JSON.decode(value);

//	if (map.msg == null)
//		alert("发送失败！");
//	else {
//	}
}