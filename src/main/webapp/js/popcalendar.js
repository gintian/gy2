//	written	by Tan Ling	Wee	on 2 Dec 2001
//	last updated 23 June 2002
	var isShowTime = true;
	var isShowMinute=true;
	if(typeof vLangue == 'undefined')
		vLangue = 1;
	if(typeof vWeekManagement == 'undefined')
		vWeekManagement = 1;
	var _top,left=-1;	
	
	
	var	fixedX = -1;				// x position (-1 if to appear below control)
	var	fixedY = -1;					// y position (-1 if to appear below control)
	var startAt = parseFloat(vWeekManagement);   // 0 - sunday ; 1 - monday
	var showWeekNumber = 1;			// 0 - don't show; 1 - show
	var showToday = 1;				// 0 - don't show; 1 - show
	var imgDir = "/js/img/";					// directory for images ... e.g. var imgDir="/img/"
    var outObject;
	var gotoString = "Go To Current Month";
	var todayString = "今天";
	var weekString = "";//公休日
	var feastString = "";//年假
	var turn_dateString="";//工作日倒休
	var week_dateString="";//公休日倒休
	var scrollLeftMessage = "Click to scroll to previous month. Hold mouse button to scroll automatically.";
	var scrollRightMessage = "Click to scroll to next month. Hold mouse button to scroll automatically.";
	var selectMonthMessage = "Click to select a month.";
	var selectYearMessage = "Click to select a year.";
	var selectDateMessage = "Select [date] as date."; // do not replace [date], it will be replaced by date.
	var altCloseCalendar = "Close the Calendar";
	var	monthName =	new	Array("01月","02月","03月","04月","05月","06月","07月","08月","09月","10月","11月","12月");
	var sweek_Hit="休息日";
	var sfeast_Hit="节假日";
	var swork_Hit="工作日";
	var sturn_Hit="倒休日";
	var dayName = new Array	("日","一","二","三","四","五","六");
	var arrTemp = dayName.slice(startAt,7);
	dayName = arrTemp.concat(dayName.slice(0,startAt));
	var fullDayName;	
	if (vLangue==0) //FRENCH
		{
		gotoString = "Aller au mois en cours";
		todayString = "Aujourd'hui :&nbsp;";
		weekString = "Sem";
		scrollLeftMessage = "Cliquer pour le mois prent. Tenir enfonc?pour doulement automatique.";
		//alert("scrollLeftMessage---"+scrollLeftMessage);
		scrollRightMessage = "Cliquer pour le mois suivant. Tenir enfonc?pour doulement automatique.";
		//alert("scrollRightMessage---"+scrollRightMessage);
		selectMonthMessage = "Cliquer pour choisir un mois.";
		selectYearMessage = "Clicquer pour choisir une ann.";
		//alert("selectYearMessage---"+scrollLeftMessage);
		selectDateMessage = "Choisir [date] comme date."; // do not replace [date], it will be replaced by date.
		altCloseCalendar = "Fermer le calendrier";
		monthName =	new	Array("Janvier","Frier","Mars","Avril","Mai","Juin","Juillet","Ao","Septembre","Octobre","Novembre","Dembre");
		dayName = new Array	("Dim","Lun","Mar","Mer","Jeu","Ven","Sam");
		fullDayName = new Array	("dimanche","lundi","mardi","mercredi","jeudi","vendredi","samedi");
		
		arrTemp = dayName.slice(startAt,7);
		dayName = arrTemp.concat(dayName.slice(0,startAt));
		
		arrTemp = fullDayName.slice(startAt,7);
		fullDayName = arrTemp.concat(fullDayName.slice(0,startAt));
		}
	
	var	crossobj, crossMonthObj, crossYearObj, monthSelected, yearSelected, dateSelected, omonthSelected, oyearSelected, odateSelected, monthConstructed, yearConstructed, intervalID1, intervalID2, timeoutID1, timeoutID2, ctlToPlaceValue, ctlNow, dateFormat, nStartingYear;
	var	bPageLoaded=false;
	var	ie=document.all;
	var	domflag=document.getElementById;
	var	ns4=document.layers;
	var	today =	new	Date();
	var	dateNow	 = today.getDate();
	var	monthNow = today.getMonth();
	var	yearNow	 = today.getFullYear();
	var	imgsrc = new Array("drop1.gif","drop2.gif","left1.gif","left2.gif","right1.gif","right2.gif");
	var	img	= new Array();
	var bShow = false;
    /* hides <select> and <applet> objects (for IE only) */
    function hideElement( elmID, overDiv )
    {
      if( ie )
      {
        for( i = 0; i < document.all.tags( elmID ).length; i++ )
        {
          obj = document.all.tags( elmID )[i];
          if( !obj || !obj.offsetParent )
          {
            continue;
          }
      
          // Find the element's offsetTop and offsetLeft relative to the BODY tag.
          objLeft   = obj.offsetLeft;
          objTop    = obj.offsetTop;
          objParent = obj.offsetParent;
          
          while( objParent.tagName.toUpperCase() != "BODY" )
          {
            objLeft  += objParent.offsetLeft;
            objTop   += objParent.offsetTop;
            objParent = objParent.offsetParent;
          }
      
          objHeight = obj.offsetHeight;
          objWidth = obj.offsetWidth;
      
          if(( overDiv.offsetLeft + overDiv.offsetWidth ) <= objLeft );
          else if(( overDiv.offsetTop + overDiv.offsetHeight ) <= objTop );
          else if( overDiv.offsetTop >= ( objTop + objHeight ));
          else if( overDiv.offsetLeft >= ( objLeft + objWidth ));
          else
          {
            obj.style.visibility = "hidden";
          }
        }
      }
    }
     
    /*
    * unhides <select> and <applet> objects (for IE only)
    */
    function showElement( elmID )
    {
      if( ie )
      {
        for( i = 0; i < document.all.tags( elmID ).length; i++ )
        {
          obj = document.all.tags( elmID )[i];
          
          if( !obj || !obj.offsetParent )
          {
            continue;
          }
        
          obj.style.visibility = "";
        }
      }
    }
	function HolidayRec (d, m, y, desc)
	{
		this.d = d;
		this.m = m;
		this.y = y;
		this.desc = desc;
	}
	var HolidaysCounter = 0;
	var Holidays = new Array();
	function addHoliday (d, m, y, desc)
	{
		Holidays[HolidaysCounter++] = new HolidayRec ( d, m, y, desc );
	}
	if (domflag&&!document.getElementById("calendar"))
	{
		for	(i=0;i<imgsrc.length;i++)
		{
			img[i] = new Image;
			img[i].src = imgDir + imgsrc[i];
		}                                          
		document.write ("<div id='calendar' style='z-index: 999; position: absolute; visibility: hidden; left: 0px; top: 0px;background:#FFF' onClick='bShow=true'>" +
							"<table	width="+((showWeekNumber==1)?161:140)+" style='font-family:arial;font-size:11px;border-width:1;border-style:solid;border-color:#F2EDED;' bgcolor='#ffffff'>" +//改动边框颜色#a0a0a0  整个日期控件背景颜色#ffffff  #F5F5FA border-color:#b0d2f4
							"<tr bgcolor='#DEDEDE'>" +//改动头部颜色#0000aa   #5aa2f4  #F2EDED
							"<td>" +
								"<table width='"+((showWeekNumber==1)?161:140)+"'>" +
								"<tr>" +
									"<td style='padding:0px;font-family:arial; font-size:11px;' nowrap>" +
										"<font color='#ffffff'><B><span id='caption'></span></B></font>" +
									"</td>" +
									"<td align=right>" +
										//"<a href='javascript:hideCalendar()'><IMG SRC='"+imgDir+"close.gif' name=close WIDTH='15' HEIGHT='13' BORDER='0' ALT='" + altCloseCalendar + "'></a>" +
									"</td>" +
								"</tr>" +
								"</table>" +
							"</td>" +
							"</tr>" +
							"<tr>" +
							"<td style='padding:0px' bgcolor='#ffffff'>" +//日期主体背景颜色#ffffff
								"<span id='_content'></span>" +
							"</td>" +
							"</tr>");
			
		if (showToday==1)
		{
			document.write ("<tr bgcolor='#ffffff' >" + //时分秒部位颜色#f0f0f0
							"<td style='padding:1px' align=center  valign=middle>" +
								"<span style='float:left;'>" +
								"<iframe name=stime id=stime src='/js/timeS.htm' frameborder=0 width=90 height=22 scrolling=no allowtransparency=true></iframe>" +
								"</span>" +
								"<span id='lblToday' style=\" line-height:22px;\">" +
								"</span>" +
							"</td>" +
							"</tr>");
		}
			
			document.write ("</table>" +
						"</div>" +//控件面板显示结束
						//年月下拉层
						"<div id='selectMonth' style='z-index:+999;position:absolute;visibility:hidden;;left:0px;top:0px;'></div>" +
						"<div id='selectYear' style='z-index:+999;position:absolute;visibility:hidden;left:0px;top:0px;'></div>");
	}
	var	styleAnchor="text-decoration:none;color:#28497C;font-size:11px;";
	var	styleLightBorder="border-style:solid;border-width:1px;border-color:#a0a0a0;";
	function swapImage(srcImg, destImg){
		if (ie)	{ 
			document.getElementById(srcImg).setAttribute("src",imgDir + destImg); 
		}
	}
	function init()	{
		if (!ns4)
		{
			if (!ie&&yearNow<1900) { 
				yearNow += 1900;	
			}
			crossobj=(domflag)?document.getElementById("calendar").style : ie? document.all.calendar : document.calendar;
			hideCalendar();
			crossMonthObj=(domflag)?document.getElementById("selectMonth").style : ie? document.all.selectMonth	: document.selectMonth;
			crossYearObj=(domflag)?document.getElementById("selectYear").style : ie? document.all.selectYear : document.selectYear;
			monthConstructed=false;
			yearConstructed=false;
			if (showToday==1)
			{
				if (vLangue)//日期选择框下面显示了今天的日期后chrome浏览器会换行
					document.getElementById("lblToday").innerHTML =	" <a style='text-decoration:none;color:black;font-size:9px;' href='javascript:dateSelected=dateNow;monthSelected=monthNow;yearSelected=yearNow;closeCalendar();'>"+todayString+ /*yearNow + "." + monthName[monthNow].substring(0,2)+"."+ padZero(dateNow)+*/"</a>";
					//document.getElementById("lblToday").innerHTML =	" <a onmousemove='window.status=\""+gotoString+"\"' onmouseout='window.status=\"\"' title='"+gotoString+"' style='text-decoration:none;color:black;font-size:11px;' href='javascript:dateSelected=dateNow;monthSelected=monthNow;yearSelected=yearNow;closeCalendar();'>"+todayString + "周"+dayName[firstdayofweek(today.getDay())]+", " + yearNow + "年" + monthName[monthNow].substring(0,3)+ padZero(dateNow)+ "日</a>";
				else
					;//document.getElementById("lblToday").innerHTML =	" <a onmousemove='window.status=\""+gotoString+"\"' onmouseout='window.status=\"\"' title='"+gotoString+"' style='text-decoration:none;color:black;font-size:11px;' href='javascript:dateSelected=dateNow;monthSelected=monthNow;yearSelected=yearNow;closeCalendar();'>"+todayString + fullDayName[firstdayofweek(today.getDay())]+" le " + ((dateNow==1)?"1<sup>er</sup>":dateNow) + " " + monthName[monthNow].toLowerCase()	+ "	" +	yearNow	+ "</a>";
			}
			//
			var sHTML1="<span id='spanLeft'	style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer' onmouseover='swapImage(\"changeLeft\",\"left2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+scrollLeftMessage+"\"' onclick='javascript:decMonth()' onmouseout='clearInterval(intervalID1);swapImage(\"changeLeft\",\"left1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartDecMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'>&nbsp<IMG id='changeLeft' SRC='"+imgDir+"left1.gif' width=10 height=11 BORDER=0>&nbsp</span>&nbsp;";
			sHTML1+="<span id='spanRight' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer'	onmouseover='swapImage(\"changeRight\",\"right2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+scrollRightMessage+"\"' onmouseout='clearInterval(intervalID1);swapImage(\"changeRight\",\"right1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='incMonth()' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartIncMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'>&nbsp<IMG id='changeRight' SRC='"+imgDir+"right1.gif'	width=10 height=11 BORDER=0>&nbsp</span>&nbsp";
			sHTML1+="<span id='spanMonth' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer'	onmouseover='swapImage(\"changeMonth\",\"drop2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+selectMonthMessage+"\"' onmouseout='swapImage(\"changeMonth\",\"drop1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='popUpMonth()'></span>&nbsp;";
			sHTML1+="<span id='spanYear' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer' onmouseover='swapImage(\"changeYear\",\"drop2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+selectYearMessage+"\"'	onmouseout='swapImage(\"changeYear\",\"drop1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"'	onclick='popUpYear()'></span>&nbsp;";
			//xmsh 2014-1-4
			sHTML1="<span id='spanLeft'	style='cursor:pointer' onmouseover='window.status=\""+scrollLeftMessage+"\"' onclick='javascript:decMonth()' onmouseout='clearInterval(intervalID1);window.status=\"\"' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartDecMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'><img id='changeLeft' width='22' height='19' border='0' src='"+imgDir+"left3.png' style='float:left;'></span>";
			sHTML1+="<span id='spanRight' style='cursor:pointer;'	onmouseover='window.status=\""+scrollRightMessage+"\"' onmouseout='clearInterval(intervalID1);window.status=\"\"' onclick='incMonth()' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartIncMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'><img id='changeRight' width='22' height='19' border='0' src='"+imgDir+"right3.png' style='float:right;'></span>";
			sHTML1+="&nbsp;&nbsp;&nbsp;&nbsp;";
			sHTML1+="<span id='spanYear' style='cursor:pointer;text-decoration:none; font-size:15px;' onmouseover='this.style.borderColor=\"#88AAFF\";window.status=\""+selectYearMessage+"\"'	onmouseout='this.style.borderColor=\"#3366FF\";window.status=\"\"'	onclick='popUpYear()'></span>";
			sHTML1+="<span style='cursor:pointer;text-decoration:none; font-size:15px;'>-</span>";
			sHTML1+="<span id='spanMonth' style='cursor:pointer;text-decoration:none; font-size:15px;'	onmouseover='this.style.borderColor=\"#88AAFF\";window.status=\""+selectMonthMessage+"\"' onmouseout='this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='popUpMonth()'></span>";
			document.getElementById("caption").innerHTML  =	sHTML1;
			bPageLoaded=true
		}
	}
	function firstdayofweek(day)
	{
		day -= startAt;
		if (day < 0){
			day = 7 + day;
		}
		return day;
	}
	function hideCalendar()	{
		//alert("hideCalendar");
		if(crossobj){
			crossobj.visibility="hidden";
		}
		if (crossMonthObj != null){
			crossMonthObj.visibility="hidden";
		}
		if (crossYearObj !=	null){
			crossYearObj.visibility="hidden";
		}
        showElement( 'SELECT' );
		showElement( 'APPLET' );
		
	}
	function padZero(num) {
		return (num	< 10)? '0' + num : num ;
	}
	function constructDate(d,m,y)
	{
		sTmp = dateFormat;
		sTmp = sTmp.replace	("dd","<e>");
		sTmp = sTmp.replace	("d","<d>");
		sTmp = sTmp.replace	("<e>",padZero(d));
		sTmp = sTmp.replace	("<d>",d);
		sTmp = sTmp.replace	("mmm","<o>");
		sTmp = sTmp.replace	("mm","<n>");
		sTmp = sTmp.replace	("m","<m>");
		sTmp = sTmp.replace	("<m>",m+1);
		sTmp = sTmp.replace	("<n>",padZero(m+1));
		sTmp = sTmp.replace	("<o>",monthName[m]);
		if (isShowTime)
		{
			if(isShowMinute)
				sTmp=sTmp.replace ("yyyy",y)+" "+stime.m.getTime();
			else
				sTmp=sTmp.replace ("yyyy",y)+" "+stime.m.getTimeMinute();
		}else{
			sTmp=sTmp.replace ("yyyy",y);
		}
		/**xuj 2013-7-30 add 拓展采用extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"时间格式 */
		if(ctlToPlaceValue.getAttribute("extra")=="editor"){
			var datetype = ctlToPlaceValue.getAttribute("dataType");
			
			if(datetype==null)
				datetype="date";
			switch (datetype) {
			  case "simpledate"://xuj add 2010-9-3
				var itemlength = ctlToPlaceValue.getAttribute("itemlength");
				if (itemlength == 4) {
					sTmp=sTmp.substring(0,4);
				} else if (itemlength == 7){
					sTmp=sTmp.substring(0,7);
				} else if(itemlength == 10) {
					sTmp=sTmp.substring(0,10);
				} else if(itemlength == 16) {
					sTmp=sTmp.substring(0,16);
				}else if(itemlength == 18) {
				}
				break;
			  case "date":
				  sTmp=sTmp.substring(0,10);
				break;
			  case "time":
				  sTmp=sTmp.substring(11);
				break;
			  case "datetime":
				break;
			  default:
				  sTmp=sTmp.substring(0,10);
				break;
			}
		}
		return sTmp;
	}
	function closeCalendar() {
		var	sTmp;
		hideCalendar();
		ctlToPlaceValue.value =	constructDate(dateSelected,monthSelected,yearSelected);
		 
		if(document.all) ///ie浏览器
	    { 
	       outObject.fireEvent('ondblclick');
		   outObject.fireEvent('onchange');///激活 onchange事件  使面试安排时间自动保存  zzk
		}
		else{
			try{
				var  evt=document.createEvent('HTMLEvents');            
				evt.initEvent('dblclick',true,true);            
				outObject.dispatchEvent(evt);
				var  evt1=document.createEvent('HTMLEvents');            
				evt1.initEvent('change',true,true);  ///激活 onchange事件  使面试安排时间自动保存  注意Firefox onchange事件触发用‘change’ zzk 
				outObject.dispatchEvent(evt1);
			}catch(e){
			
			}
		}
	}
	/*** Month Pulldown	***/
	function StartDecMonth()
	{
		intervalID1=setInterval("decMonth()",80);
	}
	function StartIncMonth()
	{
		intervalID1=setInterval("incMonth()",80);
	}
	function incMonth () {
		monthSelected++;
		if (monthSelected>11) {
			monthSelected=0;
			yearSelected++;
		}
		constructCalendar();
	}
	function decMonth () {
		monthSelected--
		if (monthSelected<0) {
			monthSelected=11
			yearSelected--
		}
		constructCalendar()
	}
	function constructMonth() {
		popDownYear()
		if (!monthConstructed) {
			sHTML =	""
			for	(i=0; i<12;	i++) {
				sName =	monthName[i];
				if (i==monthSelected){
					sName =	"<B>" +	sName +	"</B>"
				}
				sHTML += "<tr><td id='m" + i + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='monthConstructed=false;monthSelected=" + i + ";constructCalendar();popDownMonth();event.cancelBubble=true;'>&nbsp;" + sName + "&nbsp;</td></tr>"
			}
			document.getElementById("selectMonth").innerHTML = "<table width=70	style='font-family:arial; font-size:11px; border-width:1; border-style:solid; border-color:#a0a0a0;' bgcolor='#FFFFDD' cellspacing=0 onmouseover='clearTimeout(timeoutID1)'	onmouseout='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"popDownMonth()\",100);event.cancelBubble=true'>" +	sHTML +	"</table>"
			monthConstructed=true
		}
	}
	function popUpMonth() {
		constructMonth()
		crossMonthObj.visibility = (domflag||ie)? "visible"	: "show"
		crossMonthObj.left = (parseInt(crossobj.left) + document.getElementById("spanMonth").offsetLeft)+"px";
		crossMonthObj.top =	parseInt(crossobj.top) + 26+"px";
		hideElement( 'SELECT', document.getElementById("selectMonth") );
		hideElement( 'APPLET', document.getElementById("selectMonth") );			
	}
	function popDownMonth()	{
		crossMonthObj.visibility= "hidden"
	}
	/*** Year Pulldown ***/
	function incYear() {
		if((6+nStartingYear)==9999){
			bShow=true;
			return;
		}
			
		for	(i=0; i<7; i++){
			newYear	= (i+nStartingYear)+1
			if (newYear==yearSelected)
			{ txtYear =	"&nbsp;<B>"	+ newYear +	"</B>&nbsp;" }
			else
			{ txtYear =	"&nbsp;" + newYear + "&nbsp;" }
			document.getElementById("y"+i).innerHTML = txtYear
		}
		nStartingYear ++;
		bShow=true
	}
	function decYear() {
		for	(i=0; i<7; i++){
			newYear	= (i+nStartingYear)-1
			if (newYear==yearSelected)
			{ txtYear =	"&nbsp;<B>"	+ newYear +	"</B>&nbsp;" }
			else
			{ txtYear =	"&nbsp;" + newYear + "&nbsp;" }
			document.getElementById("y"+i).innerHTML = txtYear
		}
		nStartingYear --;
		bShow=true
	}
	function selectYear(nYear) {
		yearSelected=parseInt(nYear+nStartingYear);
		yearConstructed=false;
		constructCalendar();
		popDownYear();
	}
	function constructYear() {
		popDownMonth()
		sHTML =	""
		//if (!yearConstructed) 
		{
			sHTML =	"<tr><td align='center'	onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='clearInterval(intervalID1);this.style.backgroundColor=\"\"' style='cursor:pointer'	onmousedown='clearInterval(intervalID1);intervalID1=setInterval(\"decYear()\",30)' onmouseup='clearInterval(intervalID1)'>-</td></tr>"
			j =	0;
			var nEndingyear;
			if(yearSelected+3>9999){
				nEndingyear=9999;
				nStartingYear=nEndingyear-6;
			}else{
				nStartingYear =	yearSelected-3;
				nEndingyear=yearSelected+3;
			}
			for	(i=nStartingYear; i<=nEndingyear; i++) {
				sName =	i;
				if (i==yearSelected){
					sName =	"<B>" +	sName +	"</B>"
				}
				sHTML += "<tr><td id='y" + j + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='selectYear("+j+");event.cancelBubble=true'>&nbsp;" + sName + "&nbsp;</td></tr>"
				j ++;
			}
			sHTML += "<tr><td align='center' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='clearInterval(intervalID2);this.style.backgroundColor=\"\"' style='cursor:pointer' onmousedown='clearInterval(intervalID2);intervalID2=setInterval(\"incYear()\",30)'	onmouseup='clearInterval(intervalID2)'>+</td></tr>"
			document.getElementById("selectYear").innerHTML	= "<table width=44 style='font-family:arial; font-size:11px; border-width:1; border-style:solid; border-color:#a0a0a0;'	bgcolor='#FFFFDD' onmouseover='clearTimeout(timeoutID2)' onmouseout='clearTimeout(timeoutID2);timeoutID2=setTimeout(\"popDownYear()\",100)' cellspacing=0>"	+ sHTML	+ "</table>"
			yearConstructed	= true
		}
	}
	function popDownYear() {
		clearInterval(intervalID1)
		clearTimeout(timeoutID1)
		clearInterval(intervalID2)
		clearTimeout(timeoutID2)
		crossYearObj.visibility= "hidden"
	}
	function popUpYear() {
		var	leftOffset
		constructYear();
		crossYearObj.visibility	= (domflag||ie)? "visible" : "show"
		leftOffset = parseInt(crossobj.left) + document.getElementById("spanYear").offsetLeft;
		if (ie)
		{
			leftOffset += 6
		}
		crossYearObj.left =	leftOffset+"px";
		crossYearObj.top = parseInt(crossobj.top) +	26+"px";
	}
	/*** calendar ***/
   function WeekNbr(n) {
      // Algorithm used:
      // From Klaus Tondering's Calendar document (The Authority/Guru)
      // hhtp://www.tondering.dk/claus/calendar.html
      // a = (14-month) / 12
      // y = year + 4800 - a
      // m = month + 12a - 3
      // J = day + (153m + 2) / 5 + 365y + y / 4 - y / 100 + y / 400 - 32045
      // d4 = (J + 31741 - (J mod 7)) mod 146097 mod 36524 mod 1461
      // L = d4 / 1460
      // d1 = ((d4 - L) mod 365) + L
      // WeekNumber = d1 / 7 + 1
 
      year = n.getFullYear();
      month = n.getMonth() + 1;
	  /*
      if (startAt == 0) {
         day = n.getDate() + 1;
      }
      else {
         day = n.getDate();
      }*/
	  day = n.getDate() + 1-startAt;
 
      a = Math.floor((14-month) / 12);
      y = year + 4800 - a;
      m = month + 12 * a - 3;
      b = Math.floor(y/4) - Math.floor(y/100) + Math.floor(y/400);
      J = day + Math.floor((153 * m + 2) / 5) + 365 * y + b - 32045;
      d4 = (((J + 31741 - (J % 7)) % 146097) % 36524) % 1461;
      L = Math.floor(d4 / 1460);
      d1 = ((d4 - L) % 365) + L;
      week = Math.floor(d1/7) + 1;
 
      return week;
   }
	function constructCalendar () {
		var aNumDays = Array (31,0,31,30,31,30,31,31,30,31,30,31);
		var dateMessage;
		var	startDate =	new	Date (yearSelected,monthSelected,1);
		var endDate;
		if (monthSelected==1)
		{
			endDate	= new Date (yearSelected,monthSelected+1,1);
			endDate	= new Date (endDate	- (24*60*60*1000));
			numDaysInMonth = endDate.getDate();
		}
		else
		{
			numDaysInMonth = aNumDays[monthSelected];
		}
		datePointer	= 0
		//dayPointer = startDate.getDay()
		dayPointer = firstdayofweek(startDate.getDay());
		/*
		switch (startAt)
			{
			case (0): dayPointer = dayPointer
			break;
			case (1): dayPointer--
			break;
			case (6): dayPointer++
			break;
			}	
			*/
		//dayPointer = startDate.getDay()// - startAt
		
		if (dayPointer<0)
		{
			//dayPointer = 6
		}
		var sHTML =	"<table	 border=0 style='font-size:10px;' cellpadding='0' cellspacing='0'><tr>";//日期天背景颜色
		
		for	(i=0; i<7; i++)	{
			var str_i=(i+1)+"";			
			if(weekString.indexOf(str_i)!=-1)
			{
				if(i==6){
					//周末
					sHTML += "<td width='23' align='center' valign=middle style='height:14px;font-size:12px;color:#000000 Author=meizz;cursor:default;background-color:#F2EDED;'>"+ dayName[i]+"</td>";//周末栏颜色#FF9999  #C4D4EB
				}else{
					//周末
					sHTML += "<td width='23' align='center' valign=middle style='height:14px;font-size:12px;color:#000000 Author=meizz;cursor:default;background-color:#F2EDED; border-right:2px solid #FFF;'>"+ dayName[i]+"</td>";//周末栏颜色#FF9999
				}
			}else{
				if(i==6){
					//工作日
			        sHTML += "<td width='23' align='center' valign=middle style='height:14px;font-size:12px;color:#e7f1fe Author=meizz;cursor:default;background-color:#FCF9F9;'><font color='#254B7C'>"+ dayName[i]+"</font></td>";//工作日栏颜色#99CCCC  #e7f2ff
				}else{
					//工作日
			        sHTML += "<td width='23' align='center' valign=middle style='height:14px;font-size:12px;color:#e7f1fe Author=meizz;cursor:default;background-color:#FCF9F9; border-right:1px solid #FFF;'><font color='#254B7C'>"+ dayName[i]+"</font></td>";//工作日栏颜色#99CCCC
				}
			}
		}
		sHTML +="</tr><tr>";
		
		
		for	( var i=1; i<=dayPointer;i++ )
		{
			sHTML += "<td valign=\"middle\" height=\"14\" align=\"center\" style=\"background-color:#fff; border-right:1px solid #F2EDED;border-top:1px solid #F2EDED;\" >&nbsp;</td>";//线#eaeaea
		}     
	
		for	( datePointer=1; datePointer<=numDaysInMonth; datePointer++ )
		{
			dayPointer++;
			if ((dayPointer+startAt) % 7 == startAt) { 
				sHTML += "<td valign=\"middle\" height=\"14\" align=\"center\" style=\"background-color:#fff; border-top:1px solid #F2EDED;\">";
			}else{
				sHTML += "<td valign=\"middle\" height=\"14\" align=\"center\" style=\"background-color:#fff; border-top:1px solid #F2EDED;border-right:1px solid #F2EDED;\">";
			}
			sStyle=styleAnchor;
			if ((datePointer==odateSelected) &&	(monthSelected==omonthSelected)	&& (yearSelected==oyearSelected)){ 
				sStyle+=styleLightBorder; 
			}
			var sHint = "";
			for (k=0;k<HolidaysCounter;k++)
			{
				if ((parseInt(Holidays[k].d)==datePointer)&&(parseInt(Holidays[k].m)==(monthSelected+1)))
				{
					if ((parseInt(Holidays[k].y)==0)||((parseInt(Holidays[k].y)==yearSelected)&&(parseInt(Holidays[k].y)!=0)))
					{
						sStyle+="background-color:#FFDDDD;";
						sHint+=sHint==""?Holidays[k].desc:"\n"+Holidays[k].desc;
					}
				}
			}
			var regexp= /\"/g;
			sHint=sHint.replace(regexp,"&quot;");
			if(weekString=="`,")
				swork_Hit="";
                        
			dateMessage = "onmousemove='window.status=\""+selectDateMessage.replace("[date]",constructDate(datePointer,monthSelected,yearSelected))+"\"' onmouseout='window.status=\"\"' ";
            var now_week= getweekStr(yearSelected,monthSelected,datePointer);
                        
			if ((datePointer==dateNow)&&(monthSelected==monthNow)&&(yearSelected==yearNow))
			{ 
				
			    if(getfeastStr(yearSelected,monthSelected,datePointer))
			    {
			    	sHTML += "<b><a "+dateMessage+" title=\"" + sfeast_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer+";closeCalendar(); return false;' href='javascript:void(0);'><font color=#ff0000>&nbsp;" + datePointer + "</font>&nbsp;</a></b>";
			    }else
			    {
			        sHTML += "<b><a "+dateMessage+" title=\"" + sHint + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer+";closeCalendar(); return false;' href='javascript:void(0);'><font color=#ff0000>&nbsp;" + datePointer + "</font>&nbsp;</a></b>";
			    }
			}
			else if	(weekString.indexOf(now_week)!=-1)//休息日
			{
				//优先判断是否是节假日
				if(getfeastStr(yearSelected,monthSelected,datePointer))
                {
                    sHTML += "<a "+dateMessage+" title=\"" + sfeast_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;<font color=#3333FF>" + datePointer + "</font>&nbsp;</a>"; 
                }else {
					if(!getWeek_date(yearSelected,monthSelected,datePointer))
					{
			            sHTML += "<a "+dateMessage+" title=\"" + sweek_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;<font color=#909090>" + datePointer + "</font>&nbsp;</a>"; 	
		            }
			        else
			        {
			        	sHTML += "<a "+dateMessage+" title=\"" + swork_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;" + datePointer + "&nbsp;</a>"; 
			        }
                }
				 
			}
			else				
			{ 
			    if(getfeastStr(yearSelected,monthSelected,datePointer))
			    {
			    	if(!getWeek_date(yearSelected,monthSelected,datePointer))
			    	{
			    	     sHTML += "<a "+dateMessage+" title=\"" + sfeast_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;<font color=#3333FF>" + datePointer + "</font>&nbsp;</a>"; 
			    	}else
			    	{
			    	     sHTML += "<a "+dateMessage+" title=\"" + swork_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;" + datePointer + "&nbsp;</a>"; 
			    	}
			    	
			    
			    }else
			    {
			        if(getTurn_date(yearSelected,monthSelected,datePointer))
			        {
			            sHTML += "<a "+dateMessage+" title=\"" + sturn_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;<font color=#909090>" + datePointer + "</font>&nbsp;</a>"; 
			        }else
			        {
			            sHTML += "<a "+dateMessage+" title=\"" + swork_Hit + "\" style='"+sStyle+"' onclick='javascript:dateSelected="+datePointer + ";closeCalendar(); return false;' href='javascript:void(0);'>&nbsp;" + datePointer + "&nbsp;</a>"; 
			        }
			        
			    }			
			}
			sHTML += "";
			if ((dayPointer+startAt) % 7 == startAt) { 
				sHTML += "</tr><tr>"; 
				
			}
		}
		if((dayPointer % 7)>0){
			for(var i=0;i<(7-(dayPointer % 7));i++){
				if(i==(6-(dayPointer % 7))){
					sHTML += "<td valign=\"middle\" height=\"14\" align=\"center\" style=\"background-color:#fff; border-top:1px solid #F2EDED;\" >&nbsp;</td>";
				}else{
					sHTML += "<td valign=\"middle\" height=\"14\" align=\"center\" style=\"background-color:#fff; border-top:1px solid #F2EDED;border-right:1px solid #F2EDED;\" >&nbsp;</td>";
				}
			}
		}     
		document.getElementById("_content").innerHTML   = sHTML;
		//document.getElementById("spanMonth").innerHTML = "&nbsp;" +	monthName[monthSelected] + "&nbsp;<IMG id='changeMonth' SRC='"+imgDir+"drop1.gif' WIDTH='12' HEIGHT='10' BORDER=0>";
		//document.getElementById("spanYear").innerHTML =	"&nbsp;" + yearSelected	+ "&nbsp;<IMG id='changeYear' SRC='"+imgDir+"drop1.gif' WIDTH='12' HEIGHT='10' BORDER=0>";
		document.getElementById("spanMonth").innerHTML = "&nbsp;" +	monthName[monthSelected].replace('月','') +"&nbsp;";
		document.getElementById("spanYear").innerHTML =	"&nbsp;" + yearSelected	+ "&nbsp;";
	}
	function popUpCalendar(ctl,ctl2,weeks,feastS,turn_date,week_date,showTime,ShowMinute) {		
		outObject=ctl;
		weekString=weeks+",";		
		feastString=feastS;
		var format="yyyy-mm-dd";
		/**xuj 2013-7-30 add 拓展采用extra="editor" itemlength=${element.itemlength } dataType="simpledate" dropDown="dropDownDate"时间格式 */
		if(ctl.getAttribute("extra")=="editor"){
			var datetype = ctl.getAttribute("dataType");
			if(datetype=="simpledate")
				format="yyyy.mm.dd";
		}
		turn_dateString=turn_date;
		week_dateString=week_date;
		if(showTime!=null&&showTime==false)
			isShowTime=false;
		if(showTime!=null&&showTime==true)
			isShowTime=true;
	    if(ShowMinute!=null&&ShowMinute==false)
	        isShowMinute=false;
	    if(ShowMinute!=null&&ShowMinute==true)
			isShowMinute=true;
		var	leftpos = left;
		var	toppos = _top;		
		if (isNaN(left))
			leftpos = -235; //-208
			
		if (isNaN(_top))
			toppos = 0;
			
		if (bPageLoaded)
		{
			//alert(crossobj.visibility);
			if ( crossobj.visibility ==	"hidden" ) {
				ctlToPlaceValue	= ctl2;
				dateFormat=format;
				var formatChar = " ";
				var aFormat	= dateFormat.split(formatChar);
				if (aFormat.length<3)
				{
					formatChar = "/";
					aFormat	= dateFormat.split(formatChar);
					if (aFormat.length<3)
					{
						formatChar = ".";
						aFormat	= dateFormat.split(formatChar);
						if (aFormat.length<3)
						{
							formatChar = "-";
							aFormat	= dateFormat.split(formatChar);
							if (aFormat.length<3)
							{
								// invalid date	format
								formatChar="";
							}
						}
					}
				}
        
				var tokensChanged =	0;
				if ( formatChar	!= "" )
				{
					// use user's date
					var aData =	ctl2.value.split(formatChar);
					for	(i=0;i<3;i++)
					{
						if ((aFormat[i]=="d") || (aFormat[i]=="dd"))
						{
							dateSelected = parseInt(aData[i], 10);
							tokensChanged ++;
						}
						else if	((aFormat[i]=="m") || (aFormat[i]=="mm"))
						{
							monthSelected =	parseInt(aData[i], 10) - 1;
							tokensChanged ++;
						}
						else if	(aFormat[i]=="yyyy")
						{
							yearSelected = parseInt(aData[i], 10);
							tokensChanged ++;
						}
						else if	(aFormat[i]=="mmm")
						{
							for	(j=0; j<12;	j++)
							{
								if (aData[i]==monthName[j])
								{
									monthSelected=j;
									tokensChanged ++;
								}
							}
						}
					}
				}
				if ((tokensChanged!=3)||isNaN(dateSelected)||isNaN(monthSelected)||isNaN(yearSelected))
				{
					dateSelected = dateNow;
					monthSelected =	monthNow;
					yearSelected = yearNow;
				}
				odateSelected=dateSelected;
				omonthSelected=monthSelected;
				oyearSelected=yearSelected;
				var aTag = ctl;
				do {
					aTag = aTag.offsetParent;
					leftpos	+= aTag.offsetLeft;
					toppos += aTag.offsetTop;
				} while(aTag.tagName!="BODY");
				crossobj.left =(fixedX==-1 ? ctl.offsetLeft	+ leftpos :	fixedX)+"px";
				var bodywidth=document.body.clientWidth;//窗口可见区域大小
				var absowidth=crossobj.left;
				var surplusWidth=parseInt(bodywidth)-parseInt(absowidth);
				//针对考勤日明细,当element在div中,并且div出现水平滚动条,并且实际crossobj.left>clientWidth  wangmj 2013.12.11 
				var sLeft = 0;
				var calendarWidth = 170;
				if(document.getElementById("tbl-container")){
					sLeft = document.getElementById("tbl-container").scrollLeft;//滚动条隐藏的页面的宽度
					crossobj.left = parseInt(absowidth) - parseInt(sLeft);
					absowidth = crossobj.left;
					surplusWidth=parseInt(bodywidth)-parseInt(absowidth);
					if(surplusWidth<calendarWidth && surplusWidth >= 0)
						crossobj.left=parseInt(absowidth)-(calendarWidth-surplusWidth);//把显示层显示到窗口内
				}		
				else if(surplusWidth<calendarWidth && surplusWidth >= 0)
				{
					//日期框左对齐空间不足的情况像右偏移不足的像素
				  crossobj.left=parseInt(absowidth)-(calendarWidth-surplusWidth);//把显示层显示到窗口内
				}
				//针对考勤日明细,当element在div中,并且div出现垂直滚动条,并且实际crossobj.top>clientHeight  wangmj 2013.12.11 
				crossobj.top =(fixedY==-1 ?	ctl.offsetTop +	toppos + ctl.offsetHeight +	2 :	fixedY) + "px";
				var bodyhegiht=document.body.clientHeight;//窗口可见区域高度
				var absoheight = crossobj.top;
				var minusHeight = parseInt(bodyhegiht)-parseInt(absoheight);
				
				var sTop=0;//当维护的指标全部放在一个div中，并且div出现了滚动条，此时出现的时间选择控件的位置是不对的，解决方法是把外层div的id值写成Wdiv，这样就可以了 lzw 2012-05-09
				if(document.getElementById("Wdiv")){
					sTop=document.getElementById("Wdiv").scrollTop;
					crossobj.top = parseInt(absoheight)-parseInt(sTop);
				}
				else if(document.getElementById("tbl-container")){//针对考勤日明细
					sTop=document.getElementById("tbl-container").scrollTop;
					crossobj.top = parseInt(absoheight)-parseInt(sTop);
				}
				
				var _scrollHeight=document.body.scrollHeight;
				var _clientHeight=document.body.clientHeight;
				var bodyHeight=_scrollHeight>_clientHeight?_scrollHeight:_clientHeight;//document.body.clientHeight;//窗口可见区域大小
				//alert(window.screen.availHeight+" "+document.body.scrollHeight+" "+document.body.clientHeight);
				var absotop=crossobj.top;
				var surplusTop=parseInt(bodyHeight)-parseInt(absotop);
				if(surplusTop<202)//日期控件的最大高度
				{
					var crossobj_top=parseInt(absotop)-(184+ctl.offsetHeight);
					if(crossobj_top>0){
					  crossobj.top=crossobj_top+"px";//把显示层显示到窗口内
					}
				}
				
				constructCalendar (1, monthSelected, yearSelected);
				crossobj.visibility=(domflag||ie)? "visible" : "show";
				
				//IE6则隐藏和控件有交集的select和applet wangmj
				var browser=navigator.appName; 
				if("Microsoft Internet Explorer" == browser){
					var b_version=navigator.appVersion;
					var version = b_version.split(";");
					var trim_Version=version[1].replace(/[ ]/g,""); 
					if("MSIE6.0" == trim_Version){
						hideElement( 'SELECT', document.getElementById("calendar") );
						hideElement( 'APPLET', document.getElementById("calendar") );			
					}
				}
				bShow = true;
			}
			else
			{
				hideCalendar();
				if (ctlNow!=ctl) {
					//popUpCalendar(ctl, ctl2, format,feastS,turn_date,week_date);
					popUpCalendar(ctl,ctl2,weeks,feastS,turn_date,week_date,showTime,ShowMinute) 
				}
			}
			ctlNow = ctl;			
		}
		
		if(document.getElementById("Wdiv")){
			document.getElementById("Wdiv").onscroll = function hidecal2 () { 		
				if (!bShow)
				{
					hideCalendar();
				}
				bShow = false;
			}
		}
	}
	document.onkeypress = function hidecal1 (evt) { 
		evt=evt?evt:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
	    var key = window.event?evt.keyCode:evt.which;
		if (key==27) 
		{
			hideCalendar();
		}
	}
	document.onclick = function hidecal2 () { 		
		if (!bShow)
		{
			hideCalendar();
		}
		bShow = false;
	}
	if(ie)
	{
		init();
	}
	else
	{
		window.onload=init();
	}
	//判断星期几
	function getweekStr(year,month,day)
	{
	   var firstDate = new Date(year,month,day);	   	   
	   var r_week=firstDate.getDay();
	   if(r_week==0)
	   {
	     r_week=7;
	   }
	   return r_week;
	}
	//判断节假日
	function getfeastStr(year,month,day)
	{
	  var thisDate = new Date(year,month,day);
	  month=padZero(month+1);
	  day=padZero(day);
	  var cur_m_d=month+"."+day;
	  var cur_y_m_d=year+"."+month+"."+day;
	  //alert(feastString+"---"+cur_m_d);
	  var isCorrect=false;
	  if(feastString.indexOf(cur_m_d)!=-1)
	  {
	          var lr=feastString.lastIndexOf("`");
	          if(lr!=feastString.length)
		  {
			  feastString=feastString+"`";
		  }
		  var i=0;
		  var r=0;	
		  var t=0;
		  var list_ary=new Array();
		  if(feastString.indexOf("`")!=-1)
		  {
			  while(i!=-1)
			  {		  
			     i=feastString.indexOf("`",r);			   
			     if(i!=-1)
			     {
			       var str=feastString.substring(r,i);			       	
			       list_ary[t]=str;
			       t++;
			     }		   
			     r=i+1;
			  }
		  }else
		  {
			  list_ary[t]=feastString;
		  }		  
		  for(var y=0;y<list_ary.length;y++)
		  {
			  var date=list_ary[y];
			  if(date.indexOf(cur_m_d)!=-1)
			  {
				  if(date.length>cur_m_d.length)
				  {
					  if(date.indexOf(cur_y_m_d)!=-1)
					  {
						  isCorrect=true;
					  }else
					  {
						  isCorrect=false;
					  }
				  }else
				  {
					  isCorrect=true;
					  
				  }
			  }
		  }
	   }
	  return isCorrect;
       }
       //判断公休日倒休
       //公休日
       function getWeek_date(year,month,day)
       {
       	  var cur_week=getweekStr(year,month,day);
          month=padZero(month+1);
	  day=padZero(day);
          var cur_y_m_d=year+"."+month+"."+day;               
          if(week_dateString.indexOf(cur_y_m_d)!=-1)
          {
             if(weekString.indexOf(cur_week)!=-1)
             {
             	return true;
             }else
                return false;
          }else
          {
             return false;
          }	
       }
       //工作日
        function getTurn_date(year,month,day)
       {
           month=padZero(month+1);
           day=padZero(day);
          var cur_y_m_d=year+"."+month+"."+day; 
          if(turn_dateString.indexOf(cur_y_m_d)!=-1)
          {
             return true;
          }else
          {
             return false;
          }	
       }
