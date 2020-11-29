 var returnVo ;
 var obj;
function setFild(dbname,orgcode,itemid,itemvalue)
{
	 var theurl="/ht/ctstatic/setFlds.do?b_query=link";
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
	 
	 var userAgent = navigator.userAgent;
	var browserVersion = parseFloat(navigator.appVersion);
	var browserName = navigator.appName;
		var reg = new RegExp("MSIE (\\d+)\\.\\d+");
		reg.test(userAgent);
		var strIE = "MSIE|" + RegExp["$1"];
		
	if (getBrowseVersion()) {//IE浏览器
		if (strIE == "MSIE|6") {
			returnVo = window.showModalDialog(iframe_url, 'htset_win', 
		      				"dialogWidth:600px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");
		} else{
			returnVo = window.showModalDialog(iframe_url, 'htset_win', 
		      				"dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
		}
		if(!returnVo)
			return false;	   
		if(returnVo.flag=="true")
		{    
			stAnalysisForm.action="/ht/ctstatic/ht_static_detail.do?b_query=link&dbname="+dbname+"&a_code="+orgcode+"&itemid="+itemid+"&itemvalue="+itemvalue;
			stAnalysisForm.submit();
		}  
	}else{
		obj= parent.frames['center_iframe'][1].contentWindow;
		if(obj.Ext.getCmp("selectFild")){
			obj.Ext.getCmp("selectFild").close(); //防止再次点击
	    }
		obj.Ext.create('Ext.window.Window',{
			id:'selectFild',
			title:'',
			width:600,
			height:550,
			resizable:false,
			modal:true,
			autoScroll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff;" frameborder="0" scrolling=no height="100%" width="100%" src="'+iframe_url+'"></iframe>',
			renderTo:obj.Ext.getBody(),
			listeners:{
				'close':function(){
					if (returnVo) {
						if(returnVo.flag== 'true')
						{
							stAnalysisForm.action="/ht/ctstatic/ht_static_detail.do?b_query=link&dbname="+dbname+"&a_code="+orgcode+"&itemid="+itemid+"&itemvalue="+itemvalue;
							stAnalysisForm.submit();
						}
					}
				}
			}
			
		});
	}
 			
}
function saveSetFlds()
{
	setselectitem('right_fields');
	var	vos=document.getElementsByName("right_fields")[0];
	if(vos==null || vos.length==0){
		  document.getElementsByName("right_fields")[0].options.add(new Option("", "NULL"));
		  document.getElementsByName("right_fields")[0].options[0].selected=true;

	}
	stAnalysisForm.action="/ht/ctstatic/setFlds.do?br_save=link";	
	stAnalysisForm.submit();
	var thevo=new Object();
	thevo.flag="true";
	 if(getBrowseVersion()){
		 	window.returnValue=thevo;
			window.close();
	    }else{
	    		parent.parent.returnVo=thevo;
	            var selectFild = parent.parent.Ext.getCmp("selectFild");
	            if(selectFild){
	            	selectFild.close();
	            }
	    }
	
	
}
function exportExcel(cols)
{
	var val = document.getElementById("cols");
	var hashvo=new ParameterSet();
	hashvo.setValue("cols",val.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'3400030012'},hashvo);
}
function showfile(outparamters)
{
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");
}

// 返回，返回到统计页面
function goback(code) {
	stAnalysisForm.action="/ht/ctstatic/ctanalysis.do?b_query=link&a_code="+code;	
	stAnalysisForm.submit();	
}