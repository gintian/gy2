
//插入指标确定
function gzemail_chooseFieldOk(values,value)
{
   if(values != '') {
	   var templateId = Ext.getCmp('tempalteId').getValue();
	   var arr = values.split("`");
	   var fieldtitle=arr[1];
	   var array = arr[0].split(":");
	   var fieldcontent = array[1];
	   var fieldsetid=array[0];
	   var strtxt='';
	    if(array[0]=="undefined"){
		   strtxt="$sys:"+fieldtitle+"$";
		}else{
			   strtxt="$"+fieldid+":"+fieldtitle+"$";
			   
		}
	    if(isIE()){
	   	 if(value[1]=="contentId"){
			 var wobj = Ext.getCmp("contentId");
			 wobj.focus();
			 var orValue=wobj.getValue();
			 if(!isCompatibleIE()){
				//IE 非兼容模式
				wobj.setValue(orValue.substring(0,orValue.length-4)+trim(strtxt));
				if(orValue.substring(orValue.length-13)=="<P>&nbsp;</P>"){
					wobj.setValue(orValue.substring(0,orValue.length-13)+trim(strtxt));
				}else if(orValue.substring(orValue.length-4)=="</P>"){
					wobj.setValue(orValue.substring(0,orValue.length-4)+trim(strtxt)+"</P>");
				}else {
					wobj.setValue(orValue.substring(0)+trim(strtxt));
				}
			 }else{
				var rge = range;//通过替换光标保证位置正确
				if (rge!=null&&rge.offsetLeft!=0)//用户点击过计算公式部分 offsetLeft==0说明该对象未被渲染到当前页面上
				{ 
					rge.text=strtxt;
					rge.select();
				}
				else
				{//用户未点击过计算公式部分需要创建一个range。让用户选择的选项在开头插入
					var element = document.selection;
					if (element!=null) {
						var rge = element.createRange();
						if (rge!=null)	
						{ 
							rge.text=strtxt;
							rge.select();
						}
					}
				}
				range=rge;
		 	}
	   	 }
		   if(value[1]=="emailName"){
		  	 var wobj = Ext.getCmp("emailName");
			 wobj.focus();
			 var orValue=wobj.getValue();
		     wobj.setValue(orValue.substring(0,orValue.length)+trim(strtxt));
		   }
	   }
		if(!isIE()){
			if(value[1]=="emailName"){
				var wobj = Ext.getCmp('emailName');
				var orValue=wobj.getValue();
				wobj.setValue(orValue.substring(0,value[0])+strtxt+orValue.substring(value[0],orValue.length));
			}
			if(value[1]=="contentId"){
				var wobj = Ext.getCmp('contentId');
				wobj.focus();
				wobj.insertAtCursor(strtxt);
			}
		}
		if(array[0].length<1){
			return;
		}
		var map = new HashMap();
		map.put("fieldsetid",fieldsetid);
		map.put("fieldtitle",fieldtitle);
		map.put("fieldcontent",trim(fieldcontent));
		Rpc({functionId : 'ZP0000002347',success : Global.toInsertField}, map);
   }
}
Global.toInsertField=function(response){
	var templateId=Ext.getCmp('tempalteId').getValue();
	var value = response.responseText;
	var map = Ext.decode(value);
  	var fieldtitle = map.fieldtitle;
  	var fieldcontent = map.fieldcontent;
  	var fieldtype = map.fieldtype;
  	var codeset = map.codeset;
  	var fieldset = map.fieldset;
  	var fieldlen = map.fieldlen;
  	var ndec = map.ndec;
    var dateformat="0";
    var nflag="0";
     var arrValue = ""
     var  setobj= new Array();
          setobj[0]=fieldtitle;
          setobj[1]=fieldcontent;
          setobj[2]=fieldtype;
          setobj[3]=fieldlen;
          setobj[4]=ndec;
          setobj[5]=dateformat;
          setobj[6]=codeset;
          setobj[7]=nflag;
          setobj[8]=fieldset;
          setobj[8]=fieldid;
          var strtxt="$sys:"+fieldtitle+"$";
          var strtxt2="$"+fieldid+":"+fieldtitle+"$";
   			   
          
          var wobj = Ext.getCmp("contentId");
	 	  wobj.focus();
	 	  var txt =wobj.getValue();
	 	  
	 	 if(txt.indexOf(strtxt) != -1 ||  txt.indexOf(strtxt2) != -1 ){
	 		  formula_array[parseInt(fieldid)] = setobj;
	 		 arrValue =templateId+"`"+fieldid+"`"+fieldtitle+"`"+fieldtype+"`"+fieldcontent+"`"+dateformat+"`"+fieldlen+"`"+ndec+"`"+codeset+"`"+fieldset+"`"+nflag;
	 		 email_array.push(getEncodeStr(arrValue));
	 		 fieldid=parseInt(fieldid)+1; 
	 	  }
          
          
   
	 arrValue=undefined;
	 setobj=undefined;
}
