
	
	function aaa(){
		if (aa) {
			var hashvo=new ParameterSet();
			hashvo.setValue("temp",aa);
			hashvo.setValue("trigger",trigger);  
			var request=new Request({method:'post',asynchronous:false,onSuccess:out,functionId:'1010040099'},hashvo);	
		}
	}	
	function out(outparamters){
		var miao=outparamters.getValue("temp1");
		var fen=outparamters.getValue("temp2");
		var shi=outparamters.getValue("temp3");
		var ri=outparamters.getValue("temp4");
		var yue=outparamters.getValue("temp5");
		var zhou=outparamters.getValue("temp6");
		var nian=outparamters.getValue("temp7");
		
		
		if((ri=="*"|| ri.indexOf("/") != -1) &&yue=="*" && zhou=="?" &&((shi.indexOf("/")==-1 &&fen.indexOf("/")==-1) ||(shi.indexOf("/")==-1 ||fen.indexOf("/")==-1)) ){
			var pls = document.getElementsByName('days');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='1'){
					pls[i].checked=true;
					break;
				}
			}	
			
			showMenu();
			if (ri.indexOf("/") == -1) {
				document.getElementById('every_cont_1_days').value=getTwo("1");
			} else {
				var ritemp = ri.split("/");
				document.getElementById('every_cont_1_days').value=getTwo(ritemp[1]);
			}
			
			if (shi.indexOf("/")==-1 &&fen.indexOf("/")==-1) {
				document.getElementById('every_pl_1_min').value=fen.substring(1);
				document.getElementById('every_pl_1_h').value=shi.substring(1);
				document.getElementById('every_pl_1_mm').value=miao.substring(1);
			} else {
				
				var pls = document.getElementsByName('pl');
			    for(var i=0;i<pls.length;i++){
					if(pls[i].value=='2'){
						pls[i].checked=true;
					}
			    }
			    
			document.getElementById('pl_1').disabled=true;
			var pl_2 = document.getElementsByName('pl_2');
			for(var j=0;j<pl_2.length;j++){
				pl_2[j].disabled=false;
			}
			if(shi.indexOf("/")!=-1){
				var shitemp = shi.split("/");
				var tt=document.getElementById('pl_2_type');
    			var options=tt.options;
				for(var i=0;i<options.length;i++){
					if(options[i].value=="2"){
						options[i].selected=true;
					}else{
						options[i].selected=false;
					}
				}
				document.getElementById('every_pl_2_num').value=getTwo(shitemp[1]);
				document.getElementById('every_pl_2_began_min').value=getTwo(fen);
				document.getElementById('every_pl_2_began_h').value=getTwo(shitemp[0].split("-")[0]);
				document.getElementById('every_pl_2_began_mm').value=getTwo(miao);
				document.getElementById('every_pl_2_end_h').value=getTwo(shitemp[0].split("-")[1]);
				select_month(2,"pl");
			}else if(fen.indexOf("/")!=-1){
				
				var fentemp = fen.split("/");
				var tt=document.getElementById('pl_2_type');
    			var options=tt.options;
				for(var i=0;i<options.length;i++){
					if(options[i].value=="1"){
						options[i].selected=true;
					}else{
						options[i].selected=false;
					}
				}
				document.getElementById('every_pl_2_num').value=getTwo(fentemp[1]);
				
				if(fentemp[0].indexOf("-")==-1){
				    document.getElementById('every_pl_2_began_min').value=getTwo(fentemp[0]);
				    document.getElementById('every_pl_2_end_min').value=getTwo(fentemp[0]);
				} else {
					document.getElementById('every_pl_2_began_min').value=getTwo(fentemp[0].split("-")[0]);
					document.getElementById('every_pl_2_end_min').value=getTwo(fentemp[0].split("-")[1]);
				}
				
				if(shi.indexOf("-")==-1)
					document.getElementById('every_pl_2_began_h').value = shi;
				else
				    document.getElementById('every_pl_2_began_h').value=getTwo(shi.split("-")[0]);
				
				document.getElementById('every_pl_2_began_mm').value=getTwo(miao);
				
				if(shi.indexOf("-")==-1)
					document.getElementById('every_pl_2_end_h').value = shi;
				else
				    document.getElementById('every_pl_2_end_h').value=getTwo(shi.split("-")[1]);
				
				select_month(2,"pl");
			}
				
				
				
				
				
			}
			
			
		} else {
		if(shi.indexOf("-")==-1&&shi.indexOf("*")==-1&&shi.indexOf("/")==-1
				&& fen.indexOf("-")==-1&&fen.indexOf("*")==-1&&fen.indexOf("/")==-1){
			var pls = document.getElementsByName('pl');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='1'){
					pls[i].checked=true;
				}
			}		
			document.getElementById('every_pl_1_min').value=fen.substring(1);
			document.getElementById('every_pl_1_h').value=shi.substring(1);
			document.getElementById('every_pl_1_mm').value=miao.substring(1);
		}else{
			var pls = document.getElementsByName('pl');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='2'){
					pls[i].checked=true;
				}
			}
			document.getElementById('pl_1').disabled=true;
			var pl_2 = document.getElementsByName('pl_2');
			for(var j=0;j<pl_2.length;j++){
				pl_2[j].disabled=false;
			}
			if(shi.indexOf("/")!=-1){
				var shitemp = shi.split("/");
				var tt=document.getElementById('pl_2_type');
    			var options=tt.options;
				for(var i=0;i<options.length;i++){
					if(options[i].value=="2"){
						options[i].selected=true;
					}else{
						options[i].selected=false;
					}
				}
				document.getElementById('every_pl_2_num').value=getTwo(shitemp[1]);
				document.getElementById('every_pl_2_began_min').value=getTwo(fen);
				document.getElementById('every_pl_2_began_h').value=getTwo(shitemp[0].split("-")[0]);
				document.getElementById('every_pl_2_began_mm').value=getTwo(miao);
				document.getElementById('every_pl_2_end_h').value=getTwo(shitemp[0].split("-")[1]);
				select_month(2,"pl");
			}else if(fen.indexOf("/")!=-1){
				var fentemp = fen.split("/");
				var tt=document.getElementById('pl_2_type');
    			var options=tt.options;
				for(var i=0;i<options.length;i++){
					if(options[i].value=="1"){
						options[i].selected=true;
					}else{
						options[i].selected=false;
					}
				}
				document.getElementById('every_pl_2_num').value=getTwo(fentemp[1]);
				
				if(fentemp[0].indexOf("-")==-1){
				    document.getElementById('every_pl_2_began_min').value=getTwo(fentemp[0]);
				    document.getElementById('every_pl_2_end_min').value=getTwo(fentemp[0]);
				} else {
					document.getElementById('every_pl_2_began_min').value=getTwo(fentemp[0].split("-")[0]);
				    document.getElementById('every_pl_2_end_min').value=getTwo(fentemp[0].split("-")[1]);
				}
				
				if(shi.indexOf("-")==-1) {
					document.getElementById('every_pl_2_began_h').value=getTwo(shi);
					document.getElementById('every_pl_2_end_h').value=getTwo(shi);
				} else {
				    document.getElementById('every_pl_2_began_h').value=getTwo(shi.split("-")[0]);
				    document.getElementById('every_pl_2_end_h').value=getTwo(shi.split("-")[1]);
				}
				document.getElementById('every_pl_2_began_mm').value=getTwo(miao);
				select_month(2,"pl");
			}
	

		}
		if(ri.indexOf("*")==-1&&ri.indexOf("?")==-1&&ri.indexOf("/")!=-1){
			var pls = document.getElementsByName('days');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='1'){
					pls[i].checked=true;
				}
			}	
			var ritemp = ri.split("/");
			document.getElementById('every_cont_1_days').value=getTwo(ritemp[1]);
			day=ritemp[0];
		}
		
		
		
		if(yue.indexOf("*")==-1&&yue.indexOf("?")==-1&&yue.indexOf("/")!=-1){
			var pls = document.getElementsByName('days');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='3'){
					pls[i].checked=true;
				}
			}
			showMenu(1);	
			var yuetemp = yue.split("/");
			if(zhou=="?"){
				if(ri.indexOf("W")!=-1){
					var pls1 = document.getElementsByName('month');
					for(var i=0;i<pls1.length;i++){
						if(pls1[i].value=='2'){
							pls1[i].checked=true;
							}
					}
					select_month(1,"month")
					var xx=document.getElementById('cont_3_2_month_num');
    				var options=xx.options;
					for(var j=0;j<options.length;j++){
						if(options[j].value==ri.substring(0,1)){
							options[j].selected=true;
						}else{
							options[j].selected=false;
						}
						}
					
					var tt=document.getElementById('cont_3_2_month_weekday');
    				var options=tt.options;
					for(var i=0;i<options.length;i++){
						if(options[i].value=="9"){
							options[i].selected=true;
						}else{
							options[i].selected=false;
						}
						}
					document.getElementById('every_cont_3_2_month_months').value=yuetemp[1];
				}else{
					document.getElementById('every_cont_3_1_month_day').value=getTwo(ri);
					document.getElementById('every_cont_3_1_month_day_month').value=getTwo(yuetemp[1]);
				}

			}else if(ri=="?"){
					var pls1 = document.getElementsByName('month');
					for(var i=0;i<pls1.length;i++){
						if(pls1[i].value=='2'){
							pls1[i].checked=true;
							}
					}
					select_month(1,"month")
				if(zhou.indexOf(",")!=-1){
					var aa ;
					if(zhou.indexOf("#")!=-1){
						aa = zhou.split(",")[0].split("#")[1];
					}else if(zhou.indexOf("L")!=-1){
						aa = "L";
					}				
					var xx=document.getElementById('cont_3_2_month_num');
    				var options=xx.options;
					for(var j=0;j<options.length;j++){
						if(options[j].value==aa){
							options[j].selected=true;
						}else{
							options[j].selected=false;
						}
						}
					
					var tt=document.getElementById('cont_3_2_month_weekday');
    				var options=tt.options;
					for(var i=0;i<options.length;i++){
						if(options[i].value=="10"){
							options[i].selected=true;
						}else{
							options[i].selected=false;
						}
						}
					if(yue.indexOf("/")!=-1){
						document.getElementById('every_cont_3_2_month_months').value=yuetemp[1];
					}
				}else{
					var zhoutemp = zhou.split("#");
					if(zhoutemp.length == 1)
						zhoutemp[1] = "L";
					var xx=document.getElementById('cont_3_2_month_num');
    				var options=xx.options;
					for(var j=0;j<options.length;j++){
						if(options[j].value==zhoutemp[1]){
							options[j].selected=true;
						}else{
							options[j].selected=false;
						}
						}
					
					var tt=document.getElementById('cont_3_2_month_weekday');
    				var options=tt.options;
					for(var i=0;i<options.length;i++){
						if(options[i].value==zhoutemp[0]){
							options[i].selected=true;
						}else{
							options[i].selected=false;
						}
						}
					document.getElementById('every_cont_3_2_month_months').value=yuetemp[1];
				}
			}
			
			month=yuetemp[0];
		}else if(yue=="*"&&zhou=="?"){
			var pls = document.getElementsByName('days');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='3'){
					pls[i].checked=true;
				}
			}
			showMenu(1);
			if(ri.indexOf("W")!=-1){
				var pls1 = document.getElementsByName('month');
				for(var i=0;i<pls1.length;i++){
					if(pls1[i].value=='2'){
						pls1[i].checked=true;
						}
					}
				select_month(1,"month")
				var xx=document.getElementById('cont_3_2_month_num');
    			var options=xx.options;
				for(var j=0;j<options.length;j++){
					if(options[j].value==ri.substring(0,1)){
						options[j].selected=true;
					}else{
						options[j].selected=false;
					}
					}
					
				var tt=document.getElementById('cont_3_2_month_weekday');
    			var options=tt.options;
				for(var i=0;i<options.length;i++){
					if(options[i].value=="9"){
						options[i].selected=true;
					}else{
						options[i].selected=false;
					}
					}
				document.getElementById('every_cont_3_2_month_months').value="1";
				}else{
					document.getElementById('every_cont_3_1_month_day').value=ri;
					document.getElementById('every_cont_3_1_month_day_month').value="1";
				}
		}else if(yue=="*"&&ri=="?"){
			if(zhou.indexOf("L")!=-1){// 复杂规则，按月时，最后一周 规则回显不对
				var pls = document.getElementsByName('days');
				for(var i=0;i<pls.length;i++){
					if(pls[i].value=='3'){
						pls[i].checked=true;
					}
				}
				showMenu(3);
				var pls1 = document.getElementsByName('month');
				for(var i=0;i<pls1.length;i++){
					if(pls1[i].value=='2'){
						pls1[i].checked=true;
					}
				}
				select_month(1,"month");
				document.getElementById('cont_3_2_month_num').value="L";
				document.getElementById('cont_3_2_month_weekday').value=zhou.substring(0,1);
				
			}else if(zhou.indexOf("#")==-1){
				var pls = document.getElementsByName('days');
				for(var i=0;i<pls.length;i++){
					if(pls[i].value=='2'){
						pls[i].checked=true;
					}
				}
				showMenu(1);
				var zhou1 = "1";
				var zhou2 = "1";

				if(zhou.indexOf(",")!=-1){
					if(zhou.indexOf("/")!=-1){
						var zhoutemp = zhou.split("/");
						zhou1 = zhoutemp[1];
						zhou2 = zhoutemp[0];
					}else{
						zhou2=zhou;
					}
				}else{
					if(zhou.indexOf("/")!=-1){
						var zhoutemp = zhou.split("/");
						zhou1 = zhoutemp[1];
						zhou2 = zhoutemp[0];
					}else{
						zhou2=zhou;
					}
				}
				document.getElementById('every_cont_2_week').value=zhou1;
				var pls = document.getElementsByName('cont_weeks');
				for(var i=0;i<pls.length;i++){
					for(var j=0;j<zhou2.split(",").length;j++){
						if(pls[i].value==zhou2.split(",")[j]){
							pls[i].checked=true;
							break;
						}else{
							pls[i].checked=false;
						}
					}
				}
			}else{
			var pls = document.getElementsByName('days');
			for(var i=0;i<pls.length;i++){
				if(pls[i].value=='3'){
					pls[i].checked=true;
				}
			}
			showMenu(1);
			var pls1 = document.getElementsByName('month');
					for(var i=0;i<pls1.length;i++){
						if(pls1[i].value=='2'){
							pls1[i].checked=true;
							}
					}
					
					select_month(1,"month")
				if(zhou.indexOf(",")!=-1){
					var aa ;
					if(zhou.indexOf("#")!=-1){
						aa = zhou.split(",")[0].split("#")[1];
					}else if(zhou.indexOf("L")!=-1){
						aa = "L";
					}				
					var xx=document.getElementById('cont_3_2_month_num');
    				var options=xx.options;
					for(var j=0;j<options.length;j++){
						if(options[j].value==aa){
							options[j].selected=true;
						}else{
							options[j].selected=false;
						}
						}
					
					var tt=document.getElementById('cont_3_2_month_weekday');
    				var options=tt.options;
					for(var i=0;i<options.length;i++){
						if(options[i].value=="10"){
							options[i].selected=true;
						}else{
							options[i].selected=false;
						}
						}
					if(yue.indexOf("/")!=-1){
						document.getElementById('every_cont_3_2_month_months').value=yuetemp[1];
					}
				}else{
					var zhoutemp = zhou.split("#");
					if(zhoutemp.length == 1)
						zhoutemp[1] = "L";
					var xx=document.getElementById('cont_3_2_month_num');
    				var options1=xx.options;
					for(var j=0;j<options1.length;j++){
						if(options1[j].value==zhoutemp[1]){
							options1[j].selected=true;
						}else{
							options1[j].selected=false;
						}
						}
					var tt=document.getElementById('cont_3_2_month_weekday');
    				var options=tt.options;
					for(var i=0;i<options.length;i++){
						if(options[i].value==zhoutemp[0]){
							options[i].selected=true;
						}else{
							options[i].selected=false;
						}
						}
				}
		}	
		}
		
	}
		if(nian>0){
			var pls1 = document.getElementsByName('lastendtime');
					for(var i=0;i<pls1.length;i++){
						if(pls1[i].value=='1'){
							pls1[i].checked=true;
							}
					}
					select_month(3,"lastendtime");
					if(yue>0||ri>0){
						month=yue;
						day=ri;
					}
					
					month = month + "";
					day = day + "";
					yue = yue + "";
					ri = ri + "";
					if (month&&month.indexOf("/") !=-1) {
						month = month.split("/")[0];
					}
					
					if (day&&day.indexOf("/") != -1) {
					
						day = day.split("/")[0];
					}
					
					var month1 = month;
					var day1 = day;
					
					if(yue&&yue.split("-")[1]>0) {
						month=yue.split("-")[0];
						month1 = yue.split("-")[1];
					}
					
					if (ri&&ri.split("-")[1]>0) {
						day=ri.split("-")[0];
						day1=ri.split("-")[1];
					}
					
					document.getElementById('begandate').value=year+"-"+month+"-"+day;
					document.getElementById('mxenddate').value=year+"-"+month1+"-"+day1;
		}else if(nian.split("-")[1]>0){
					var pls1 = document.getElementsByName('lastendtime');
					for(var i=0;i<pls1.length;i++){
						if(pls1[i].value=='1'){
							pls1[i].checked=true;
							}
					}
					select_month(3,"lastendtime");
					var month1 = month;
					var day1 = day;
					if(yue.split("-")[1]>0||ri.split("-")[1]>0){
						month=yue.split("-")[0];
						month1 = yue.split("-")[1];
						day=ri.split("-")[0];
						day1=ri.split("-")[1];
					}else{
						if(yue.indexOf("*")==-1&&yue.indexOf("?")==-1&&ri.indexOf("*")==-1&&ri.indexOf("?")==-1){
							month=yue;
							day=ri;
						}
					}
					document.getElementById('begandate').value=nian.split("-")[0]+"-"+month+"-"+day;
					document.getElementById('mxenddate').value=nian.split("-")[1]+"-"+month1+"-"+day1;
		}
	}
	function getTwo(str){
		if(str.length=="1"){
			return "0"+str;
		}else{
			return str;
		}
	}
	function bbb(){
		if (aa) {
			var hashvo=new ParameterSet();
			hashvo.setValue("temp",aa);
			hashvo.setValue("trigger",trigger);     
			var request=new Request({method:'post',asynchronous:false,onSuccess:outb,functionId:'1010040099'},hashvo);	
		}
	}	
	function outb(outparamters){
		var temp1=outparamters.getValue("temp1");
		var temp2=outparamters.getValue("temp2");
		var temp3=outparamters.getValue("temp3");
		var temp4=outparamters.getValue("temp4");
		var startdate = temp1.split(" ")[0];
		var starttime = temp1.split(" ")[1].split(":");
		var enddate = temp2.split(" ")[0];
		
		if(enddate=="0"){
			document.getElementById('enddate').value="";
			document.getElementById('start_h2').value="00";
			document.getElementById('start_m2').value="00";
			document.getElementById('start_mm2').value="00";
		}else{
		var endtime = temp2.split(" ")[1].split(":");
			document.getElementById('enddate').value=enddate;
			document.getElementById('start_h2').value=endtime[0];
			document.getElementById('start_m2').value=endtime[1];
			document.getElementById('start_mm2').value=endtime[2];
		}
		document.getElementById('issuedate').value=startdate;
		document.getElementById('start_h').value=starttime[0];
		document.getElementById('start_m').value=starttime[1];
		document.getElementById('start_mm').value=starttime[2];
		

		

		document.getElementById('state').value=temp3;
		document.getElementById('times').value=temp4;
	}