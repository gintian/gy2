
function editOrder(orderid) {
	var theurl="/hire/demandPlan/hireOrder.do?b_edit=link`orderid="+orderid;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	var returnVo= window.showModalDialog(iframe_url, 'order_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	if(returnVo==null)
		return;
	if(returnVo.flag=="true")
		searchOrder();
}
function searchOrder() {
	var startTime = document.getElementById("editor1").value;
	var endTime = document.getElementById("editor2").value;
	document.getElementById("startDate").value = startTime;
	document.getElementById("endDate").value = endTime;
	hireOrderForm.action = "/hire/demandPlan/hireOrder.do?b_query=link";
	hireOrderForm.submit();
}
function showQueryValue(flag, theCodeValue) {
	Element.hide("datapnl");
	Element.hide("datapn2");
	Element.hide("datapn3");
	Element.hide("datapn4");
	Element.hide("querybutton");
	if (flag == 1) {
		setNull("editor1");
		setNull("editor2");
		setNull("startNum");
		setNull("endNum");
		setNull("codeValue");
		setNull("queryValue");
	}
	var theVal = hireOrderForm.queryItem.value;
	if (theVal == "") {
		return;
	}
	var valArray = theVal.split(":");
	var fieldtype = valArray[2];
	var fieldid = valArray[0];
	var codesetid = valArray[1];
	if (fieldtype == "D") {
		Element.show("datapnl");
	} else {
		if (fieldtype == "N") {
			Element.show("datapn4");
		} else {
			if (codesetid == "0" || codesetid == "") {
				Element.show("datapn2");
			} else {
				Element.show("datapn3");
				searchCodeItemList(codesetid);
				if (flag == 0) {
					document.getElementById("codeValue").value = theCodeValue;
				}
				if (flag == 1) {
					document.getElementById("codeValue").value = "all";
				}
			}
		}
	}
	Element.show("querybutton");
}
function showFieldList(outparamters) {
	var fieldlist = outparamters.getValue("codeitems");
	AjaxBind.bind($("codevalue"), fieldlist);
}
function searchCodeItemList(codeset) {
	var hashvo = new ParameterSet();
	hashvo.setValue("codeset", codeset);
	var request = new Request({method:"post", asynchronous:false, onSuccess:showFieldList, functionId:"3000000224"}, hashvo);
}
function setNull(theName) {
	if (theName == "codeValue") {
		document.getElementById(theName).value = "all";
	} else {
		document.getElementById(theName).value = "";
	}
}
function assignOrder() {
	
	var str = "";
	var tablename = "z04";
	var table = $("table" + tablename);
	var dataset = table.getDataset();
	var record = dataset.getFirstRecord();
	while (record) {
		if (record.getValue("select")) {
		     if(record.getValue("z0410")=='1')
		     {
		        alert(HIRE_ORDER_INFO3);
		        return;
		     }
			str += "," + record.getValue("Z0400");
		}
		record = record.getNextRecord();
	}
	if (str == "") {
		alert(HIRE_ORDER_INFO2);
		return;
	}
	
	var title,content,objecttype,assignObjTye;
	 var thecodeurl="/hire/demandPlan/positionDemand/positionDemandTree.do?b_moresp2=search`intype=2"; 
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	 var xx=window.showModalDialog(iframe_url,null, 
		        "dialogWidth:350px; dialogHeight:200px;resizable:yes;center:yes;scroll:yes;status:no");		   
        
     if(xx==null)      
           return;
      
     if(xx.content==null||xx.content=='')
     {
         alert(HIRE_ORDER_INFO1);
         return;
     }
     title=xx.title;
     content=xx.content;
     objecttype=xx.objecttype;	
	 assignObjTye=xx.assignObjTye;
	/*
	var a0100,a0101;
	var return_vo=select_org_emp_dialog("1","2","0","1","0","0");  
	if(return_vo)
	{	
		a0100=return_vo.content.split(",");	
		a0101=return_vo.title.split(",");	
	}
	else
	{
	  return;
	}
	if(a0100=='')
	{
		alert(HIRE_ORDER_INFO1);
		return;
	}
	*/
	var startTime = document.getElementById("editor1").value;
	var endTime = document.getElementById("editor2").value;
    hireOrderForm.startDate.value= startTime;
	hireOrderForm.endDate.value = endTime;
	hireOrderForm.paramStr.value=str.substring(1);
	hireOrderForm.action = "/hire/demandPlan/hireOrder.do?b_assignOrder=link&objecttype="+objecttype+"&zh="+getEncodeStr(content)+"&name="+getEncodeStr(title)+"&assignObjFld="+assignObjTye;
	hireOrderForm.submit();
}
function editSave()
{
	hireOrderForm.action = "/hire/demandPlan/hireOrder.do?b_save=link";
	hireOrderForm.submit();
	var thevo=new Object();
	thevo.flag="true";
	window.returnValue=thevo;
	window.close();
}
function delOrder()
{
	var str = "";
	var tablename = "z04";
	var table = $("table" + tablename);
	var dataset = table.getDataset();
	var record = dataset.getFirstRecord();
	while (record) {
		if (record.getValue("select")) {
			str += "," + record.getValue("Z0400");
		}
		record = record.getNextRecord();
	}
	if (str == "") {
		alert(HIRE_ORDER_INFO2);
		return;
	}
	hireOrderForm.action="/hire/demandPlan/hireOrder.do?b_del=link&z0400="+str.substring(1);
    hireOrderForm.submit();
}