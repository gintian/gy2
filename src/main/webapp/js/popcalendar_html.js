//	written	by Tan Ling	Wee	on 2 Dec 2001//	last updated 23 June 2002//	email :	fuushikaden@yahoo.com	var isShowTime = true;	var isShowMinute=true;	if(typeof vLangue == 'undefined')		vLangue = 1	if(typeof vWeekManagement == 'undefined')		vWeekManagement = 1	var top,left=-1;				var	fixedX = -1					// x position (-1 if to appear below control)	var	fixedY = -1					// y position (-1 if to appear below control)	var startAt = parseFloat(vWeekManagement)   // 0 - sunday ; 1 - monday	var showWeekNumber = 1			// 0 - don't show; 1 - show	var showToday = 1				// 0 - don't show; 1 - show	var imgDir = "/js/img/"					// directory for images ... e.g. var imgDir="/img/"        var outObject;	var gotoString = "Go To Current Month"	var todayString = "今天"	var weekString = ""//公休日	var feastString = ""//年假	var turn_dateString="";//工作日倒休	var week_dateString="";//公休日倒休	var scrollLeftMessage = "Click to scroll to previous month. Hold mouse button to scroll automatically."	var scrollRightMessage = "Click to scroll to next month. Hold mouse button to scroll automatically."	var selectMonthMessage = "Click to select a month."	var selectYearMessage = "Click to select a year."	var selectDateMessage = "Select [date] as date." // do not replace [date], it will be replaced by date.	var altCloseCalendar = "Close the Calendar"	var	monthName =	new	Array("01月","02月","03月","04月","05月","06月","07月","08月","09月","10月","11月","12月")	var sweek_Hit="休息日";	var sfeast_Hit="节假日";	var swork_Hit="工作日";	var sturn_Hit="倒休日";	dayName = new Array	("日","一","二","三","四","五","六")	arrTemp = dayName.slice(startAt,7)	dayName = arrTemp.concat(dayName.slice(0,startAt))	var _popcalendar_p=null;	var popcalendar_date="";		if (vLangue==0) //FRENCH		{		gotoString = "Aller au mois en cours"		todayString = "Aujourd'hui :&nbsp;"		weekString = "Sem"		scrollLeftMessage = "Cliquer pour le mois pr閏閐ent. Tenir enfonc?pour d閞oulement automatique."		//alert("scrollLeftMessage---"+scrollLeftMessage);		scrollRightMessage = "Cliquer pour le mois suivant. Tenir enfonc?pour d閞oulement automatique."		//alert("scrollRightMessage---"+scrollRightMessage);		selectMonthMessage = "Cliquer pour choisir un mois."		selectYearMessage = "Clicquer pour choisir une ann閑."		//alert("selectYearMessage---"+scrollLeftMessage);		selectDateMessage = "Choisir [date] comme date." // do not replace [date], it will be replaced by date.		altCloseCalendar = "Fermer le calendrier"		monthName =	new	Array("Janvier","F関rier","Mars","Avril","Mai","Juin","Juillet","Ao鹴","Septembre","Octobre","Novembre","D閏embre")		dayName = new Array	("Dim","Lun","Mar","Mer","Jeu","Ven","Sam")		fullDayName = new Array	("dimanche","lundi","mardi","mercredi","jeudi","vendredi","samedi")				arrTemp = dayName.slice(startAt,7)		dayName = arrTemp.concat(dayName.slice(0,startAt))				arrTemp = fullDayName.slice(startAt,7)		fullDayName = arrTemp.concat(fullDayName.slice(0,startAt))		}		var	crossobj, crossMonthObj, crossYearObj, monthSelected, yearSelected, dateSelected, omonthSelected, oyearSelected, odateSelected, monthConstructed, yearConstructed, intervalID1, intervalID2, timeoutID1, timeoutID2, ctlToPlaceValue, ctlNow, dateFormat, nStartingYear	var	bPageLoaded=false	var	ie=document.all	var	dom=document.getElementById	var	ns4=document.layers	var	today =	new	Date()	var	dateNow	 = today.getDate()	var	monthNow = today.getMonth()	var	yearNow	 = today.getYear()	var	imgsrc = new Array("drop1.gif","drop2.gif","left1.gif","left2.gif","right1.gif","right2.gif")	var	img	= new Array()	var bShow = false;    /* hides <select> and <applet> objects (for IE only) */    function hideElement( elmID, overDiv )    {      if( ie )      {        for( i = 0; i < document.all.tags( elmID ).length; i++ )        {          obj = document.all.tags( elmID )[i];          if( !obj || !obj.offsetParent )          {            continue;          }                // Find the element's offsetTop and offsetLeft relative to the BODY tag.          objLeft   = obj.offsetLeft;          objTop    = obj.offsetTop;          objParent = obj.offsetParent;                    while( objParent.tagName.toUpperCase() != "BODY" )          {            objLeft  += objParent.offsetLeft;            objTop   += objParent.offsetTop;            objParent = objParent.offsetParent;          }                objHeight = obj.offsetHeight;          objWidth = obj.offsetWidth;                if(( overDiv.offsetLeft + overDiv.offsetWidth ) <= objLeft );          else if(( overDiv.offsetTop + overDiv.offsetHeight ) <= objTop );          else if( overDiv.offsetTop >= ( objTop + objHeight ));          else if( overDiv.offsetLeft >= ( objLeft + objWidth ));          else          {            obj.style.visibility = "hidden";          }        }      }    }         /*    * unhides <select> and <applet> objects (for IE only)    */    function showElement( elmID )    {      if( ie )      {        for( i = 0; i < document.all.tags( elmID ).length; i++ )        {          obj = document.all.tags( elmID )[i];                    if( !obj || !obj.offsetParent )          {            continue;          }                  obj.style.visibility = "";        }      }    }	function HolidayRec (d, m, y, desc)	{		this.d = d		this.m = m		this.y = y		this.desc = desc	}	var HolidaysCounter = 0	var Holidays = new Array()	function addHoliday (d, m, y, desc)	{		Holidays[HolidaysCounter++] = new HolidayRec ( d, m, y, desc )	}	if (dom&&!document.getElementById("calendar"))	{		for	(i=0;i<imgsrc.length;i++)		{			img[i] = new Image			img[i].src = imgDir + imgsrc[i]		}		document.write ("<div onclick='bShow=true' id='calendar'	style='z-index:+999;position:absolute;visibility:hidden;'><table	width="+((showWeekNumber==1)?250:220)+" style='font-family:arial;font-size:11px;border-width:1;border-style:solid;border-color:#a0a0a0;font-family:arial; font-size:11px}' bgcolor='#ffffff'><tr bgcolor='#0000aa'><td><table width='"+((showWeekNumber==1)?248:218)+"'><tr><td style='padding:2px;font-family:arial; font-size:11px;'><font color='#ffffff'><B><span id='caption'></span></B></font></td><td align=right></td></tr></table></td></tr><tr><td style='padding:5px' bgcolor=#ffffff><span id='_content'></span></td></tr>")					if (showToday==1)		{			document.write ("<tr bgcolor=#f0f0f0><td style='padding:1px' align=center  valign=middle><span style='float:left;'><iframe name=stime id=stime src='/js/timeS.htm' frameborder=0 width=90 height=20 scrolling=no allowtransparency=true></iframe></span> <span id='lblToday'></span></td></tr>")		}					document.write ("</table></div><div id='selectMonth' style='z-index:+999;position:absolute;visibility:hidden;'></div><div id='selectYear' style='z-index:+999;position:absolute;visibility:hidden;'>");	        document.write("</div>");	}	var	styleAnchor="text-decoration:none;color:black;"	var	styleLightBorder="border-style:solid;border-width:1px;border-color:#a0a0a0;"	function swapImage(srcImg, destImg){		if (ie)	{ document.getElementById(srcImg).setAttribute("src",imgDir + destImg) }	}	function init()	{		if (!ns4)		{			if (!ie) { yearNow += 1900	}			crossobj=(dom)?document.getElementById("calendar").style : ie? document.all.calendar : document.calendar			crossMonthObj=(dom)?document.getElementById("selectMonth").style : ie? document.all.selectMonth	: document.selectMonth                        crossYearObj=(dom)?document.getElementById("selectYear").style : ie? document.all.selectYear : document.selectYear			monthConstructed=false;			yearConstructed=false;			if (showToday==1)			{				if (vLangue)					document.getElementById("lblToday").innerHTML =	todayString + " <a style='"+styleAnchor+"' href='javascript:monthSelected=monthNow;yearSelected=yearNow;constructCalendar();'>"+ "周" +　dayName[firstdayofweek(today.getDay())]+", " + yearNow + "年" + monthName[monthNow].substring(0,3) +	 (dateNow>9?dateNow:("0"+dateNow))	+ "日</a>"				else					document.getElementById("lblToday").innerHTML =	todayString + " <a style='"+styleAnchor+"' href='javascript:monthSelected=monthNow;yearSelected=yearNow;constructCalendar();'>"+fullDayName[firstdayofweek(today.getDay())]+" le " + ((dateNow==1)?"1<sup>er</sup>":dateNow) + " " + monthName[monthNow].toLowerCase()	+ "	" +	yearNow	+ "</a>"			}			sHTML1="<span id='spanLeft'	style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer' onmouseover='swapImage(\"changeLeft\",\"left2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+scrollLeftMessage+"\"' onclick='javascript:decMonth()' onmouseout='clearInterval(intervalID1);swapImage(\"changeLeft\",\"left1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartDecMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'>&nbsp<IMG id='changeLeft' SRC='"+imgDir+"left1.gif' width=10 height=11 BORDER=0>&nbsp</span>&nbsp;"			sHTML1+="<span id='spanRight' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer'	onmouseover='swapImage(\"changeRight\",\"right2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+scrollRightMessage+"\"' onmouseout='clearInterval(intervalID1);swapImage(\"changeRight\",\"right1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='incMonth()' onmousedown='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"StartIncMonth()\",500)'	onmouseup='clearTimeout(timeoutID1);clearInterval(intervalID1)'>&nbsp<IMG id='changeRight' SRC='"+imgDir+"right1.gif'	width=10 height=11 BORDER=0>&nbsp</span>&nbsp"			sHTML1+="<span id='spanMonth' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer'	onmouseover='swapImage(\"changeMonth\",\"drop2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+selectMonthMessage+"\"' onmouseout='swapImage(\"changeMonth\",\"drop1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"' onclick='popUpMonth()'></span>&nbsp;"			sHTML1+="<span id='spanYear' style='border-style:solid;border-width:1;border-color:#3366FF;cursor:pointer' onmouseover='swapImage(\"changeYear\",\"drop2.gif\");this.style.borderColor=\"#88AAFF\";window.status=\""+selectYearMessage+"\"'	onmouseout='swapImage(\"changeYear\",\"drop1.gif\");this.style.borderColor=\"#3366FF\";window.status=\"\"'	onclick='popUpYear()'></span>&nbsp;"						document.getElementById("caption").innerHTML  =	sHTML1			bPageLoaded=true		}	}	function firstdayofweek(day)	{	day -= startAt	if (day < 0){day = 7 + day}	return day	}	function hideCalendar()	{	}	function padZero(num) {		return (num	< 10)? '0' + num : num ;	}	function constructDate(d,m,y)	{		sTmp = dateFormat		sTmp = sTmp.replace	("dd","<e>")		sTmp = sTmp.replace	("d","<d>")		sTmp = sTmp.replace	("<e>",padZero(d))		sTmp = sTmp.replace	("<d>",d)		sTmp = sTmp.replace	("mmm","<o>")		sTmp = sTmp.replace	("mm","<n>")		sTmp = sTmp.replace	("m","<m>")		sTmp = sTmp.replace	("<m>",m+1)		sTmp = sTmp.replace	("<n>",padZero(m+1))		sTmp = sTmp.replace	("<o>",monthName[m])		return sTmp.replace ("yyyy",y)			}	/*** Month Pulldown	***/	function StartDecMonth()	{		intervalID1=setInterval("decMonth()",80)	}	function StartIncMonth()	{		intervalID1=setInterval("incMonth()",80)	}	function incMonth () {		monthSelected++		if (monthSelected>11) {			monthSelected=0			yearSelected++		}		constructCalendar()	}	function decMonth () {		monthSelected--		if (monthSelected<0) {			monthSelected=11			yearSelected--		}		constructCalendar()	}	function constructMonth() {		popDownYear()		if (!monthConstructed) {			sHTML =	""			for	(i=0; i<12;	i++) {				sName =	monthName[i];				if (i==monthSelected){					sName =	"<B>" +	sName +	"</B>"				}				sHTML += "<tr><td id='m" + i + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='monthConstructed=false;monthSelected=" + i + ";constructCalendar();popDownMonth();event.cancelBubble=true;'>&nbsp;" + sName + "&nbsp;</td></tr>"			}			document.getElementById("selectMonth").innerHTML = "<table width=70	style='font-family:arial; font-size:11px; border-width:1; border-style:solid; border-color:#a0a0a0;' bgcolor='#FFFFDD' cellspacing=0 onmouseover='clearTimeout(timeoutID1)'	onmouseout='clearTimeout(timeoutID1);timeoutID1=setTimeout(\"popDownMonth()\",100);event.cancelBubble=true'>" +	sHTML +	"</table>"			monthConstructed=true		}	}	function popUpMonth() {		constructMonth()		crossMonthObj.visibility = (dom||ie)? "visible"	: "show"		crossMonthObj.left = parseInt(crossobj.left) + 50		crossMonthObj.top =	parseInt(crossobj.top) + 26		hideElement( 'SELECT', document.getElementById("selectMonth") );		hideElement( 'APPLET', document.getElementById("selectMonth") );				}	function popDownMonth()	{		crossMonthObj.visibility= "hidden"	}	/*** Year Pulldown ***/	function incYear() {		for	(i=0; i<7; i++){			newYear	= (i+nStartingYear)+1			if (newYear==yearSelected)			{ txtYear =	"&nbsp;<B>"	+ newYear +	"</B>&nbsp;" }			else			{ txtYear =	"&nbsp;" + newYear + "&nbsp;" }			document.getElementById("y"+i).innerHTML = txtYear		}		nStartingYear ++;		bShow=true	}	function decYear() {		for	(i=0; i<7; i++){			newYear	= (i+nStartingYear)-1			if (newYear==yearSelected)			{ txtYear =	"&nbsp;<B>"	+ newYear +	"</B>&nbsp;" }			else			{ txtYear =	"&nbsp;" + newYear + "&nbsp;" }			document.getElementById("y"+i).innerHTML = txtYear		}		nStartingYear --;		bShow=true	}	function selectYear(nYear) {		yearSelected=parseInt(nYear+nStartingYear);		yearConstructed=false;		constructCalendar();		popDownYear();	}	function constructYear() {		popDownMonth()		sHTML =	""		if (!yearConstructed) {			sHTML =	"<tr><td align='center'	onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='clearInterval(intervalID1);this.style.backgroundColor=\"\"' style='cursor:pointer'	onmousedown='clearInterval(intervalID1);intervalID1=setInterval(\"decYear()\",30)' onmouseup='clearInterval(intervalID1)'>-</td></tr>"			j =	0			nStartingYear =	yearSelected-3			for	(i=(yearSelected-3); i<=(yearSelected+3); i++) {				sName =	i;				if (i==yearSelected){					sName =	"<B>" +	sName +	"</B>"				}				sHTML += "<tr><td id='y" + j + "' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='this.style.backgroundColor=\"\"' style='cursor:pointer' onclick='selectYear("+j+");event.cancelBubble=true'>&nbsp;" + sName + "&nbsp;</td></tr>"				j ++;			}			sHTML += "<tr><td align='center' onmouseover='this.style.backgroundColor=\"#FFCC99\"' onmouseout='clearInterval(intervalID2);this.style.backgroundColor=\"\"' style='cursor:pointer' onmousedown='clearInterval(intervalID2);intervalID2=setInterval(\"incYear()\",30)'	onmouseup='clearInterval(intervalID2)'>+</td></tr>"			document.getElementById("selectYear").innerHTML	= "<table width=44 style='font-family:arial; font-size:11px; border-width:1; border-style:solid; border-color:#a0a0a0;'	bgcolor='#FFFFDD' onmouseover='clearTimeout(timeoutID2)' onmouseout='clearTimeout(timeoutID2);timeoutID2=setTimeout(\"popDownYear()\",100)' cellspacing=0>"	+ sHTML	+ "</table>"			yearConstructed	= true		}	}	function popDownYear() {		clearInterval(intervalID1)		clearTimeout(timeoutID1)		clearInterval(intervalID2)		clearTimeout(timeoutID2)		crossYearObj.visibility= "hidden"	}	function popUpYear() {		var	leftOffset		constructYear()		crossYearObj.visibility	= (dom||ie)? "visible" : "show"		leftOffset = parseInt(crossobj.left) + document.getElementById("spanYear").offsetLeft		if (ie)		{			leftOffset += 6		}		crossYearObj.left =	leftOffset		crossYearObj.top = parseInt(crossobj.top) +	26	}	/*** calendar ***/   function WeekNbr(n) {      // Algorithm used:      // From Klaus Tondering's Calendar document (The Authority/Guru)      // hhtp://www.tondering.dk/claus/calendar.html      // a = (14-month) / 12      // y = year + 4800 - a      // m = month + 12a - 3      // J = day + (153m + 2) / 5 + 365y + y / 4 - y / 100 + y / 400 - 32045      // d4 = (J + 31741 - (J mod 7)) mod 146097 mod 36524 mod 1461      // L = d4 / 1460      // d1 = ((d4 - L) mod 365) + L      // WeekNumber = d1 / 7 + 1       year = n.getFullYear();      month = n.getMonth() + 1;	  /*      if (startAt == 0) {         day = n.getDate() + 1;      }      else {         day = n.getDate();      }*/	  day = n.getDate() + 1-startAt;       a = Math.floor((14-month) / 12);      y = year + 4800 - a;      m = month + 12 * a - 3;      b = Math.floor(y/4) - Math.floor(y/100) + Math.floor(y/400);      J = day + Math.floor((153 * m + 2) / 5) + 365 * y + b - 32045;      d4 = (((J + 31741 - (J % 7)) % 146097) % 36524) % 1461;      L = Math.floor(d4 / 1460);      d1 = ((d4 - L) % 365) + L;      week = Math.floor(d1/7) + 1;       return week;   }	function constructCalendar () {		var aNumDays = Array (31,0,31,30,31,30,31,31,30,31,30,31)		var dateMessage		var	startDate =	new	Date (yearSelected,monthSelected,1)		var endDate		if (monthSelected==1)		{			endDate	= new Date (yearSelected,monthSelected+1,1);			endDate	= new Date (endDate	- (24*60*60*1000));			numDaysInMonth = endDate.getDate()		}		else		{			numDaysInMonth = aNumDays[monthSelected];		}		datePointer	= 0		//dayPointer = startDate.getDay()		dayPointer = firstdayofweek(startDate.getDay())		/*		switch (startAt)			{			case (0): dayPointer = dayPointer			break;			case (1): dayPointer--			break;			case (6): dayPointer++			break;			}				*/		//dayPointer = startDate.getDay()// - startAt				if (dayPointer<0)		{			//dayPointer = 6		}		sHTML =	"<table	 border=0 style='font-size:10px;' cellpadding='0' cellspacing='1' bgColor='#EAEAEA'><tr>"				for	(i=0; i<7; i++)	{			var str_i=(i+1)+"";						if(weekString.indexOf(str_i)!=-1)			{				sHTML += "<td width='33' align='center' valign=middle style='height:15;font-size:9pt;color:#000000 Author=meizz;cursor:default;background-color:#95B7F3;'>"+ dayName[i]+"</td>"			 }else		        {		        	sHTML += "<td width='33' align='center' valign=middle style='height:15;font-size:9pt;color:#000000 Author=meizz;cursor:default;background-color:#95B7F3;'><font color='#ffffff'>"+ dayName[i]+"</font></td>"			}		}		sHTML +="</tr><tr>"						for	( var i=1; i<=dayPointer;i++ )		{			sHTML += "<td>&nbsp;</td>"		}			for	( datePointer=1; datePointer<=numDaysInMonth; datePointer++ )		{			dayPointer++;			sHTML += "<td align=center valign=middle height='18' style='background-color:#ffffff;'>"			sStyle=styleAnchor			if ((datePointer==odateSelected) &&	(monthSelected==omonthSelected)	&& (yearSelected==oyearSelected))			{ sStyle+=styleLightBorder }			sHint = ""			for (k=0;k<HolidaysCounter;k++)			{				if ((parseInt(Holidays[k].d)==datePointer)&&(parseInt(Holidays[k].m)==(monthSelected+1)))				{					if ((parseInt(Holidays[k].y)==0)||((parseInt(Holidays[k].y)==yearSelected)&&(parseInt(Holidays[k].y)!=0)))					{						sStyle+="background-color:#FFDDDD;"						sHint+=sHint==""?Holidays[k].desc:"\n"+Holidays[k].desc					}				}			}			var regexp= /\"/g			sHint=sHint.replace(regexp,"&quot;")                        			dateMessage = "onmousemove='window.status=\""+selectDateMessage.replace("[date]",constructDate(datePointer,monthSelected,yearSelected))+"\"' onmouseout='window.status=\"\"' "                        var now_week= getweekStr(yearSelected,monthSelected,datePointer);                        			if ((datePointer==dateNow)&&(monthSelected==monthNow)&&(yearSelected==yearNow))			{ 							    if(getfeastStr(yearSelected,monthSelected,datePointer))			    {			    	sHTML += "<b><a "+dateMessage+" title=\"" + sfeast_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'><font color=#ff0000>&nbsp;" + datePointer + "</font>&nbsp;</a></b>"			    }else			    {			        sHTML += "<b><a "+dateMessage+" title=\"" + sHint + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'><font color=#ff0000>&nbsp;" + datePointer + "</font>&nbsp;</a></b>"			    }			}			else if	(weekString.indexOf(now_week)!=-1)//休息日			{				if(!getWeek_date(yearSelected,monthSelected,datePointer))				{					if(getfeastStr(yearSelected,monthSelected,datePointer))			                 {			                     sHTML += "<a "+dateMessage+" title=\"" + sfeast_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;<font color=#3333FF>" + datePointer + "</font>&nbsp;</a>" 			                 }else			                 {			                   sHTML += "<a "+dateMessage+" title=\"" + sweek_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;<font color=#909090>" + datePointer + "</font>&nbsp;</a>" 				                 }								        }else			        {			        	sHTML += "<a "+dateMessage+" title=\"" + swork_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;" + datePointer + "&nbsp;</a>" 			        }				 			}			else							{ 			    if(getfeastStr(yearSelected,monthSelected,datePointer))			    {			    	if(!getWeek_date(yearSelected,monthSelected,datePointer))			    	{			    	     sHTML += "<a "+dateMessage+" title=\"" + sfeast_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;<font color=#3333FF>" + datePointer + "</font>&nbsp;</a>" 			    	}else			    	{			    	     sHTML += "<a "+dateMessage+" title=\"" + swork_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;" + datePointer + "&nbsp;</a>" 			    	}			    				    			    }else			    {			        if(getTurn_date(yearSelected,monthSelected,datePointer))			        {			            sHTML += "<a "+dateMessage+" title=\"" + sturn_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;<font color=#909090>" + datePointer + "</font>&nbsp;</a>" 			        }else			        {			            sHTML += "<a "+dateMessage+" title=\"" + swork_Hit + "\" style='"+sStyle+"' href='#' onclick='selectdate(" + datePointer + ");'>&nbsp;" + datePointer + "&nbsp;</a>" 			        }			        			    }						}			sHTML += ""			if ((dayPointer+startAt) % 7 == startAt) { 				sHTML += "</tr><tr>" 							}		}                		document.getElementById("_content").innerHTML   = sHTML		document.getElementById("spanMonth").innerHTML = "&nbsp;" +	monthName[monthSelected] + "&nbsp;<IMG id='changeMonth' SRC='"+imgDir+"drop1.gif' WIDTH='12' HEIGHT='10' BORDER=0>"		document.getElementById("spanYear").innerHTML =	"&nbsp;" + yearSelected	+ "&nbsp;<IMG id='changeYear' SRC='"+imgDir+"drop1.gif' WIDTH='12' HEIGHT='10' BORDER=0>"	}	function popUpCalendar(ctl,ctl2,weeks,feastS,turn_date,week_date,showTime,ShowMinute,lefth,toph) {				outObject=ctl;				weekString=weeks+",";				feastString=feastS;		var format="yyyy-mm-dd";		turn_dateString=turn_date;		week_dateString=week_date;			_popcalendar_p=new calendar();			if(showTime!=null&&showTime==false)			isShowTime=false;	        if(ShowMinute!=null&&ShowMinute==false)	           isShowMinute=false;		var	leftpos = left		var	toppos = top				if (isNaN(left))			leftpos = -235 //-208					if (isNaN(top))			toppos = 0		if (bPageLoaded)		{			//alert(crossobj.visibility);			if ( crossobj.visibility ==	"hidden" ) {				ctlToPlaceValue	= ctl2				dateFormat=format;				formatChar = " "				aFormat	= dateFormat.split(formatChar)				if (aFormat.length<3)				{					formatChar = "/"					aFormat	= dateFormat.split(formatChar)					if (aFormat.length<3)					{						formatChar = "."						aFormat	= dateFormat.split(formatChar)						if (aFormat.length<3)						{							formatChar = "-"							aFormat	= dateFormat.split(formatChar)							if (aFormat.length<3)							{								// invalid date	format								formatChar=""							}						}					}				}				tokensChanged =	0				if ( formatChar	!= "" )				{					// use user's date					var aData =	ctl2.value.split(formatChar);					for	(i=0;i<3;i++)					{						if ((aFormat[i]=="d") || (aFormat[i]=="dd"))						{							dateSelected = parseInt(aData[i], 10);							tokensChanged ++;						}						else if	((aFormat[i]=="m") || (aFormat[i]=="mm"))						{							monthSelected =	parseInt(aData[i], 10) - 1;							tokensChanged ++;						}						else if	(aFormat[i]=="yyyy")						{							yearSelected = parseInt(aData[i], 10);							tokensChanged ++;						}						else if	(aFormat[i]=="mmm")						{							for	(j=0; j<12;	j++)							{								if (aData[i]==monthName[j])								{									monthSelected=j;									tokensChanged ++;								}							}						}					}				}				if ((tokensChanged!=3)||isNaN(dateSelected)||isNaN(monthSelected)||isNaN(yearSelected))				{					dateSelected = dateNow					monthSelected =	monthNow					yearSelected = yearNow				}				odateSelected=dateSelected				omonthSelected=monthSelected				oyearSelected=yearSelected				aTag = ctl                                leftpos=lefth;                                toppos=toph;				crossobj.left =	fixedX==-1 ? ctl.offsetLeft	+ leftpos :	fixedX				crossobj.top = fixedY==-1 ?	ctl.offsetTop +	toppos + ctl.offsetHeight +	2 :	fixedY				constructCalendar (1, monthSelected, yearSelected);				crossobj.visibility=(dom||ie)? "visible" : "show"				hideElement( 'SELECT', document.getElementById("calendar") );				hideElement( 'APPLET', document.getElementById("calendar") );					bShow = true;			}			else			{				hideCalendar()				if (ctlNow!=ctl) {popUpCalendar(ctl, format,feastS,turn_date,week_date)}			}			ctlNow = ctl					}	}	if(ie)	{		init()	}	else	{		window.onload=init	}	//判断星期几	function getweekStr(year,month,day)	{	   var firstDate = new Date(year,month,day);	   	   	   var r_week=firstDate.getDay();	   if(r_week==0)	   {	     r_week=7;	   }	   return r_week;	}	//判断节假日	function getfeastStr(year,month,day)	{	  var thisDate = new Date(year,month,day);	  month=padZero(month+1);	  day=padZero(day);	  var cur_m_d=month+"."+day;	  var cur_y_m_d=year+"."+month+"."+day;	  //alert(feastString+"---"+cur_m_d);	  var isCorrect=false;	  if(feastString.indexOf(cur_m_d)!=-1)	  {	          var lr=feastString.lastIndexOf("`");	          if(lr!=feastString.length)		  {			  feastString=feastString+"`";		  }		  var i=0;		  var r=0;			  var t=0;		  var list_ary=new Array();		  if(feastString.indexOf("`")!=-1)		  {			  while(i!=-1)			  {		  			     i=feastString.indexOf("`",r);			   			     if(i!=-1)			     {			       var str=feastString.substring(r,i);			       				       list_ary[t]=str;			       t++;			     }		   			     r=i+1;			  }		  }else		  {			  list_ary[t]=feastString;		  }		  		  for(var y=0;y<list_ary.length;y++)		  {			  var date=list_ary[y];			  if(date.indexOf(cur_m_d)!=-1)			  {				  if(date.length>cur_m_d.length)				  {					  if(date.indexOf(cur_y_m_d)!=-1)					  {						  isCorrect=true;					  }else					  {						  isCorrect=false;					  }				  }else				  {					  isCorrect=true;					  				  }			  }		  }	   }	  return isCorrect;       }       //判断公休日倒休       //公休日       function getWeek_date(year,month,day)       {          month=padZero(month+1);	  day=padZero(day);          var cur_y_m_d=year+"."+month+"."+day;           // alert(week_dateString+"---"+cur_y_m_d);                   if(week_dateString.indexOf(cur_y_m_d)!=-1)          {             return true;          }else          {             return false;          }	       }       //工作日        function getTurn_date(year,month,day)       {           month=padZero(month+1);	  day=padZero(day);          var cur_y_m_d=year+"."+month+"."+day;                    if(turn_dateString.indexOf(cur_y_m_d)!=-1)          {             return true;          }else          {             return false;          }	       }       function selectdate(day)       {       	  _popcalendar_p.todayYear=yearSelected;	      _popcalendar_p.todayMonth=monthSelected+1;	      _popcalendar_p.todayDay=day;       }       function calendar(){ 	var today=new Date()	 	this.todayDay=today.getDate();		this.todayMonth=today.getMonth();		this.todayYear=today.getFullYear();	 	this.activeCellIndex=0;      }