<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
	<script language="javascript" src="/ajax/common.js"></script>
<HTML>
<SCRIPT LANGUAGE="JavaScript">
	var  dd=window.dialogArguments;  
  var mm=Array();
  var kk=Array();
  var c=0;
  for(var c=0;c<dd.length;c++)
 	{
   	var s=dd[c].toString();
 	  mm[c]=s.substr(0,1);
 	  kk[c]=s.substr(2,s.length-1);
 	}
 	  //var  showGetMsg="接收到父窗口的信s息为:"  +  dd;  
 // document.write(showGetMsg); 
 function  sendFromChild()  
   {  
     if(selId.value=="<bean:message key="kq.wizard.round"/>")        
        window.returnValue =selId.value+"("+tests.value+","+vvv.value+")"; 
    else if(selId.value=="<bean:message key="kq.wizard.lbunch"/>"||selId.value=="<bean:message key="kq.wizard.rbunch"/>")
        window.returnValue  =selId.value+"("+tests.value+","+ccc.value+")";
    else if(selId.value=="<bean:message key="kq.wizard.zbunch"/>")
        window.returnValue =selId.value+"("+tests.value+","+ccc.value+","+vvv.value+")"; 
    else if(selId.value=="<bean:message key="kq.wizard.max"/>"||selId.value=="<bean:message key="kq.wizard.min"/>")
        window.returnValue =selId.value+"("+tests.value+","+stest.value+")"; 
   else if(selId.value=="<bean:message key="kq.wizard.qname"/>")
        window.returnValue ="取 "+stest.value+" "+vvv.value+" 条记录"; 
   else if(selId.value=="<bean:message key="kq.wizard.stas"/>")
        window.returnValue =selId.value+"满足"+ccc.value+stest.value; 
    else if(selId.value=="<bean:message key="kq.wizard.thing"/>"||selId.value=="<bean:message key="kq.wizard.ifa"/>"||selName.value=="5"||selName.value=="6"||selName.value=="7"||selName.value=="8"||selName.value=="9")
         window.returnValue=selId.value;
     else
       window.returnValue  =selId.value+"("+tests.value+ccc.value+vvv.value+")";  
       window.close();  
   } 
function setChange(obj)
{
var selValue = obj.options[obj.selectedIndex].value;
var i=0;
var r=-1;
if (selValue=="1")
 {
 
    tavl.style.display='';
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.formula.int"/>","<bean:message key="kq.formula.int"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.round"/>","<bean:message key="kq.wizard.round"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.ssqr"/>","<bean:message key="kq.wizard.ssqr"/>");
    selId[3]=new  Option("<bean:message key="kq.wizard.ffjy"/>","<bean:message key="kq.wizard.ffjy"/>");
    selId[4]=new  Option("<bean:message key="kq.wizard.ffjj"/>","<bean:message key="kq.wizard.ffjj"/>");
    
    tests.length=0;
    
     for(var f=0;f<mm.length;f++)
     {
     if(mm[f]=="N")
     {
       r++;
       tests[r]=new Option(kk[f],kk[f]);
     }
    }
    $('xxx').innerHTML="<bean:message key="kq.formula.counts"/>";
    $('xxy').innerHTML="<bean:message key="kq.formula.countt"/>";
    
    
    ggg.style.display='none';
    hhh.style.display='none';
    aaa.style.display='';
    bbb.style.display='none';
       
  }
 if (selValue=="2")
 {
    tavl.style.display='';
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.qnull"/>","<bean:message key="kq.wizard.qnull"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.lnull"/>","<bean:message key="kq.wizard.lnull"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.rnull"/>","<bean:message key="kq.wizard.rnull"/>");
    selId[3]=new  Option("<bean:message key="kq.wizard.zbunch"/>","<bean:message key="kq.wizard.zbunch"/>");
    selId[4]=new  Option("<bean:message key="kq.wizard.bunchl"/>","<bean:message key="kq.wizard.bunchl"/>");
    selId[5]=new  Option("<bean:message key="kq.wizard.lbunch"/>","<bean:message key="kq.wizard.lbunch"/>");
    selId[6]=new  Option("<bean:message key="kq.wizard.rbunch"/>","<bean:message key="kq.wizard.rbunch"/>");
    
    tests.length=0
     
      $('xxx').innerHTML="<bean:message key="kq.formula.character"/>";
      $('xxy').innerHTML="<bean:message key="kq.wizard.zfbd"/>";
    
    for(var f=0;f<mm.length;f++)
     {
       if(mm[f]=="A")
       {
           r++;
           tests[r]=new Option(kk[f],kk[f]);
        }
     }
    
     aaa.style.display='';
     ggg.style.display='none';
     hhh.style.display='none';
     bbb.style.display='none';
  }
  if (selValue=="3")
 {
   tavl.style.display='';
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.year"/>","<bean:message key="kq.wizard.year"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.month"/>","<bean:message key="kq.wizard.month"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.day"/>","<bean:message key="kq.wizard.day"/>");
    selId[3]=new  Option("<bean:message key="kq.wizard.quarter"/>","<bean:message key="kq.wizard.quarter"/>");
    selId[4]=new  Option("<bean:message key="kq.wizard.week"/>","<bean:message key="kq.wizard.week"/>");
    selId[5]=new  Option("<bean:message key="kq.wizard.weeks"/>","<bean:message key="kq.wizard.weeks"/>");
    selId[6]=new  Option("<bean:message key="kq.wizard.today"/>","<bean:message key="kq.wizard.today"/>");
    selId[7]=new  Option("<bean:message key="kq.wizard.bweek"/>","<bean:message key="kq.wizard.bweek"/>");
    selId[9]=new  Option("<bean:message key="kq.wizard.bquarter"/>","<bean:message key="kq.wizard.bquarter"/>");
    selId[8]=new  Option("<bean:message key="kq.wizard.bmonth"/>","<bean:message key="kq.wizard.bmonth"/>");
    selId[10]=new  Option("<bean:message key="kq.wizard.byear"/>","<bean:message key="kq.wizard.byear"/>");
    selId[11]=new  Option("<bean:message key="kq.wizard.edate"/>","<bean:message key="kq.wizard.edate"/>");
    selId[12]=new  Option("<bean:message key="kq.wizard.age"/>","<bean:message key="kq.wizard.age"/>");
    selId[13]=new  Option("<bean:message key="kq.wizard.gage"/>","<bean:message key="kq.wizard.gage"/>");
    selId[14]=new  Option("<bean:message key="kq.wizard.tmonth"/>","<bean:message key="kq.wizard.tmonth"/>");
    selId[15]=new  Option("<bean:message key="kq.wizard.years"/>","<bean:message key="kq.wizard.years"/>");
    selId[16]=new  Option("<bean:message key="kq.wizard.months"/>","<bean:message key="kq.wizard.months"/>");
    selId[17]=new  Option("<bean:message key="kq.wizard.days"/>","<bean:message key="kq.wizard.days"/>");
    selId[18]=new  Option("<bean:message key="kq.wizard.quarters"/>","<bean:message key="kq.wizard.quarters"/>");
    selId[19]=new  Option("<bean:message key="kq.wizard.weekss"/>","<bean:message key="kq.wizard.weekss"/>");
    selId[20]=new  Option("<bean:message key="kq.wizard.ayear"/>","<bean:message key="kq.wizard.ayear"/>");
    selId[21]=new  Option("<bean:message key="kq.wizard.amonth"/>","<bean:message key="kq.wizard.amonth"/>");
    selId[22]=new  Option("<bean:message key="kq.wizard.aday"/>","<bean:message key="kq.wizard.aday"/>");
    selId[23]=new  Option("<bean:message key="kq.wizard.aquarter"/>","<bean:message key="kq.wizard.aquarter"/>");
    selId[24]=new  Option("<bean:message key="kq.wizard.aweek"/>","<bean:message key="kq.wizard.aweek"/>");
    tests.length=0;
    
    for(var f=0;f<mm.length;f++)
    {
      if(mm[f]=="D")
       {
          r++;
         tests[r]=new Option(kk[f],kk[f]);
       }
     }
    
    $('xxx').innerHTML="<bean:message key="kq.wizard.riqi"/>";
    $('xxy').innerHTML="<bean:message key="kq.wizard.riqi"/>";
      
    aaa.style.display='';
    ggg.style.display='none';
    hhh.style.display='none';
    bbb.style.display='none';
 }
 if (selValue=="4")
 {
   tavl.style.display='';
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.ifa"/>","<bean:message key="kq.wizard.ifa"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.thing"/>","<bean:message key="kq.wizard.thing"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.max"/>","<bean:message key="kq.wizard.max"/>");
    selId[3]=new  Option("<bean:message key="kq.wizard.min"/>","<bean:message key="kq.wizard.min"/>");
    selId[4]=new  Option("<bean:message key="kq.wizard.qname"/>","<bean:message key="kq.wizard.qname"/>");
    selId[5]=new  Option("<bean:message key="kq.wizard.stas"/>","<bean:message key="kq.wizard.stas"/>");
    
    aaa.style.display='none';
    ggg.style.display='none';
    hhh.style.display='none';
    bbb.style.display='none';
 }
 if (selValue=="5")
 {
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.true"/>","<bean:message key="kq.wizard.true"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.flase"/>","<bean:message key="kq.wizard.flase"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.null"/>","<bean:message key="kq.wizard.null"/>");
    
    tests.length=0;
    ggg.style.display='none';
    hhh.style.display='none';
    aaa.style.display='none';
    bbb.style.display='none';
 }
 if (selValue=="6")
 {
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.even"/>","<bean:message key="kq.wizard.even"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.and"/>","<bean:message key="kq.wizard.and"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.not"/>","<bean:message key="kq.wizard.not"/>");
    
   ggg.style.display='none';
   hhh.style.display='none';
   aaa.style.display='none';
   bbb.style.display='none';
 }
  if (selValue=="7")
 {
   tavl.style.display='none';
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.add"/>","<bean:message key="kq.wizard.add"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.dec"/>","<bean:message key="kq.wizard.dec"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.mul"/>","<bean:message key="kq.wizard.mul"/>");
    selId[3]=new  Option("<bean:message key="kq.wizard.divide"/>","<bean:message key="kq.wizard.divide"/>");
    selId[4]=new  Option("<bean:message key="kq.wizard.divs"/>","<bean:message key="kq.wizard.divs"/>");
    selId[5]=new  Option("<bean:message key="kq.wizard.div"/>","<bean:message key="kq.wizard.div"/>");
    selId[6]=new  Option("<bean:message key="kq.wizard.over"/>","<bean:message key="kq.wizard.over"/>");
    selId[7]=new  Option("<bean:message key="kq.wizard.mod"/>","<bean:message key="kq.wizard.mod"/>");
    
    aaa.style.display='none';
    ggg.style.display='none';
    hhh.style.display='none';
    bbb.style.display='none';
 }
  if (selValue=="8")
 {
   tavl.style.display='none';
    selId.length=0;
    selId[0]=new  Option("=","=");
    selId[1]=new  Option(">",">");
    selId[2]=new  Option(">=",">=");
    selId[3]=new  Option("<","<");
    selId[4]=new  Option("<=","<=");
    selId[5]=new  Option("<>","<>");
    selId[6]=new  Option("LIKE","LIKE");
    
    ggg.style.display='none';
    hhh.style.display='none';
    aaa.style.display='none';
    bbb.style.display='none';
 }
  if (selValue=="9")
 {
   tavl.style.display='none';
    selId.length=0;
    selId[0]=new  Option("()","()");
    selId[1]=new  Option("[]","[]");
    selId[2]=new  Option("{}","{}");
    selId[3]=new  Option("//","//");
    
    ggg.style.display='none';
    hhh.style.display='none';
    aaa.style.display='none';
    bbb.style.display='none';
 }
  if (selValue=="10")
 {
   tavl.style.display='none';
    selId.length=0;
    selId[0]=new  Option("<bean:message key="kq.wizard.ctod"/>","<bean:message key="kq.wizard.ctod"/>");
    selId[1]=new  Option("<bean:message key="kq.wizard.cton"/>","<bean:message key="kq.wizard.cton"/>");
    selId[2]=new  Option("<bean:message key="kq.wizard.dtoc"/>","<bean:message key="kq.wizard.dtoc"/>");
    selId[3]=new  Option("<bean:message key="kq.wizard.ntoc"/>","<bean:message key="kq.wizard.ntoc"/>");
    selId[4]=new  Option("<bean:message key="kq.wizard.ctom"/>","<bean:message key="kq.wizard.ctom"/>");
    selId[5]=new  Option("<bean:message key="kq.wizard.ctos"/>","<bean:message key="kq.wizard.ctos"/>");
    
    tests.length=0;
    aaa.style.display='';
    ggg.style.display='none';
    bbb.style.display='none';
    
 }
} 
  function test()
  {
   var r=-1;
    if($('selId').value=="<bean:message key="kq.formula.int"/>")
    {
      tavl.style.display='';
      ggg.style.display='none';
      hhh.style.display='none';
      aaa.style.display='';
      bbb.style.display='none';
      for(var f=0;f<mm.length;f++)
     {
      if(mm[f]=="N")
      {
         r++;
         tests[r]=new Option(kk[f],kk[f]);
      }
    }
      
  	   $('ddd').innerHTML="Int(192.99)";
  	   $('fff').innerHTML="192";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.round"/>")
  	{
  	   tavl.style.display='';
  	   ggg.style.display='none';
       hhh.style.display='';
       aaa.style.display='';
       bbb.style.display='none';
  	   $('ddd').innerHTML="Round(192.355,2)";
  	   $('fff').innerHTML="192.36";
  	   
  	  for(var f=0;f<mm.length;f++)
      {
       if(mm[f]=="N")
       {
          r++;
          tests[r]=new Option(kk[f],kk[f]);
       }
     }
  	     
         hhh.style.display='';
  	     $('zzz').innerHTML="<bean:message key="kq.wizard.decimal"/>";
          vvv.style.display='';
  	}
    if($('selId').value=="<bean:message key="kq.wizard.ssqr"/>")
  	{
      	tavl.style.display='';
      	ggg.style.display='none';
        hhh.style.display='none';
        aaa.style.display='';
        bbb.style.display='none';
  	   $('ddd').innerHTML="SANQI(192.33)";
  	   $('fff').innerHTML="192.3";
  	    hhh.style.display='none';
  	   for(var f=0;f<mm.length;f++)
      {
        if(mm[f]=="N")
        {
           r++;
          tests[r]=new Option(kk[f],kk[f]);
       }
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ffjy"/>")
  	{
  	
  	    tavl.style.display='';
  	    ggg.style.display='none';
        hhh.style.display='none';
       aaa.style.display='';
       bbb.style.display='none';
  	   $('ddd').innerHTML="YUAN(192.05)";
  	   $('fff').innerHTML="193";
  	   hhh.style.display='none';
  	 for(var f=0;f<mm.length;f++)
     {
       if(mm[f]=="N")
       {
          r++;
          tests[r]=new Option(kk[f],kk[f]);
       }
     }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ffjj"/>")
  	{
  	   tavl.style.display='';
  	   ggg.style.display='none';
       hhh.style.display='none';
       aaa.style.display='';
       bbb.style.display='none';
  	   $('ddd').innerHTML="JIAO(192.35)";
  	   $('fff').innerHTML="192.4";
  	   hhh.style.display='none';
  	 for(var f=0;f<mm.length;f++)
     {
       if(mm[f]=="N")
       {
         r++;
         tests[r]=new Option(kk[f],kk[f]);
      }
    }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.qnull"/>")
  	{
  	   $('ddd').innerHTML="Trim(' ABC ')";
  	   $('fff').innerHTML="ABC";
  	   ggg.style.display='none';
       hhh.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.lnull"/>")
  	{
  	   $('ddd').innerHTML="LTrim(' ABC ')";
  	   $('fff').innerHTML="'ABC '";
  	   ggg.style.display='none';
       hhh.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.rnull"/>")
  	{
  	   $('ddd').innerHTML="RTrim(' ABC ')";
  	   $('fff').innerHTML="' ABC'";
  	   ggg.style.display='none';
       hhh.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.zbunch"/>")
  	{
  	   $('ddd').innerHTML="SubStr('welcome',3,7)";
  	   $('fff').innerHTML="come";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.target"/>";
       $('xyy').innerHTML="";
       $('yyy').innerHTML="";
       $('yzz').innerHTML="";
       $('zzz').innerHTML="<bean:message key="kq.wizard.long"/>";
  	    ggg.style.display='';
  	    hhh.style.display='';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.bunchl"/>")
  	{
  	   $('ddd').innerHTML="Len('welcome')";
  	   $('fff').innerHTML="7";
  	   ggg.style.display='none';
       hhh.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.lbunch"/>")
  	{
  	   $('ddd').innerHTML="Left('Welcome',2)";
  	   $('fff').innerHTML="we";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.target"/>";
       $('xyy').innerHTML="";
       $('yyy').innerHTML="<bean:message key="kq.wizard.long"/>";
  	    ggg.style.display='';
  	    hhh.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.rbunch"/>")
  	{
  	   $('ddd').innerHTML="Right('Welcome',4)";
  	   $('fff').innerHTML="come";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.target"/>";
       $('xyy').innerHTML="";
       $('yyy').innerHTML="<bean:message key="kq.wizard.long"/>";
  	    ggg.style.display='';
  	    hhh.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.year"/>")
  	{
  	   $('ddd').innerHTML="Year('1982.09.08')";
  	   $('fff').innerHTML="1982";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.month"/>")
  	{
  	   $('ddd').innerHTML="Month('1982.09.08')";
  	   $('fff').innerHTML="09";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.day"/>")
  	{
  	   $('ddd').innerHTML="Day('1982.09.08')";
  	   $('fff').innerHTML="08";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.week"/>")
  	{
  	   $('ddd').innerHTML="Week('2001.01.08')";
  	   $('fff').innerHTML="2";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.quarter"/>")
  	{
  	   $('ddd').innerHTML="Quarter('1982.09.08')";
  	   $('fff').innerHTML="3";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.weeks"/>")
  	{
  	   $('ddd').innerHTML="WeekDay('1992.01.09')";
  	   $('fff').innerHTML="4";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.today"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.rdate"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.bweek"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.rweek"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.bmonth"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.rmonth"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.byear"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ryear"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.edate"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.endd"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.age"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.suma"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.gage"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.aone"/>";
  	   ggg.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.months"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.tmonths"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
       $('yyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.years"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.tyear"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
       $('yyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.days"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.tday"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
       $('yyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.quarters"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.tquarter"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
       $('yyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.weekss"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.tweek"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
       $('yyy').innerHTML="<bean:message key="kq.wizard.riqi"/>2";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ayear"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.riqi"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
       $('yyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.amonth"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.riqi"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
       $('yyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.aday"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.riqi"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
       $('yyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.aquarter"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.riqi"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
       $('yyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.aweek"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.riqi"/>";
  	   ggg.style.display='';
  	   $('xyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
       $('yyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ifa"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ifone"/>";
  	   ggg.style.display='none';
  	   hhh.style.display='none';
  	   aaa.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.thing"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ifmore"/>";
  	   ggg.style.display='none';
  	   hhh.style.display='none';
  	   aaa.style.display='none';
  	   bbb.style.display='none';
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.max"/>")
  	{
  	   bbb.style.display='';
  	   aaa.style.display='';
  	   ggg.style.display='none';
  	   $('xxx').innerHTML="<bean:message key="kq.wizard.zifu"/>";
       $('xxy').innerHTML="<bean:message key="kq.wizard.expre"/>1";
       $('bbx').innerHTML="<bean:message key="kq.wizard.zifu"/>";
       $('bby').innerHTML="<bean:message key="kq.wizard.expre"/>2";
  	   hhh.style.display='none';
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="MAX";
  	   tests.length=0;
  	   //alert(kk[2]);
  	   for(var f=0;f<mm.length;f++)
       {

          r++;
         tests[r]=new Option(kk[f],kk[f]);
         stest[r]=new Option(kk[f],kk[f]);
         
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.min"/>")
  	{
  	   ggg.style.display='none';
  	   hhh.style.display='none';
  	   aaa.style.display='';
  	   bbb.style.display='';
  	   $('xxx').innerHTML="<bean:message key="kq.wizard.zifu"/>";
       $('xxy').innerHTML="<bean:message key="kq.wizard.expre"/>1";
       $('bby').innerHTML="<bean:message key="kq.wizard.zifu"/>";
       $('bby').innerHTML="<bean:message key="kq.wizard.expre"/>2";
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="MIN";
  	   
  	 for(var f=0;f<mm.length;f++)
      {
          r++;
          tests[r]=new Option(kk[f],kk[f]);
          stest[r]=new Option(kk[f],kk[f]);
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.qname"/>")
  	{
  	   aaa.style.display='';
  	   ggg.style.display='none';
  	   hhh.style.display='';
  	   bbb.style.display='';
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.log"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.target"/>";
       $('xyy').innerHTML="";
       $('yyy').innerHTML="<bean:message key="kq.wizard.integer"/>";
       $('bbx').innerHTML="";
       $('bby').innerHTML="<bean:message key="kq.wizard.direct"/>";
       tests.length=0;
  	   for(var f=0;f<mm.length;f++)
       {
           r++;
          tests[r]=new Option(kk[f],kk[f]);
      }
      stest[0]=new Option("最初第","最初第");
      stest[1]=new Option("最近第","最近第");
      
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.stas"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.sstut"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.target"/>";
       $('xyy').innerHTML="";
       $('yyy').innerHTML="<bean:message key="kq.wizard.term"/>";
       $('bbx').innerHTML="";
       $('bby').innerHTML="<bean:message key="kq.wizard.wise"/>";
  	   ggg.style.display='';
  	   aaa.style.display='';
  	   hhh.style.display='none';
  	   bbb.style.display='';
  	   for(var f=0;f<mm.length;f++)
      {
           r++;
           tests[r]=new Option(kk[f],kk[f]);
      }
      stest[0]=new Option("的总和","的总和");
      stest[1]=new Option("的平均值","的平均值");
      stest[2]=new Option("的个数","的个数");
      stest[3]=new Option("的最大值","的最大值");
      stest[4]=new Option("的最小值","的最小值");
      stest[5]=new Option("的最近第一条记录","的最近第一条记录");
      stest[6]=new Option("的最初第一条记录","的最初第一条记录");
    
  	}
  	if($('selId').value=="LIKE")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.contain"/>";
  	}
  	if($('selId').value=="[]")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.contain"/>";
  	}
  	if($('selId').value=="{}")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.variable"/>";
  	}
  	if($('selId').value=="//")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.note"/>";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ctod"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ctod"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.zfbd"/>";
       tests.length=0;
  	 for(var f=0;f<mm.length;f++)
     {
      if(mm[f]=="A")
       {
          r++;
         tests[r]=new Option(kk[f],kk[f]);
       }
     }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.cton"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.cton"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.zfbd"/>";
       tests.length=0;
    	for(var f=0;f<mm.length;f++)
      {
       if(mm[f]=="A")
        {
           r++;
          tests[r]=new Option(kk[f],kk[f]);
        }
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.dtoc"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.dtoc"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.riqi"/>";
       tests.length=0;
  	   for(var f=0;f<mm.length;f++)
      {
       if(mm[f]=="D")
       {
          r++;
         tests[r]=new Option(kk[f],kk[f]);
       }
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ntoc"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ntoc"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.formula.countt"/>";
  	   tests.length=0;
     for(var f=0;f<mm.length;f++)
     {
      if(mm[f]=="D")
       {
          r++;
         tests[r]=new Option(kk[f],kk[f]);
       }
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ctom"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ssign"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.zfbd"/>";
       tests.length=0;
      for(var f=0;f<mm.length;f++)
      {
       if(mm[f]=="A")
        {
           r++;
          tests[r]=new Option(kk[f],kk[f]);
        }
      }
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ctos"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ctos"/>";
  	   $('xxx').innerHTML="";
       $('xxy').innerHTML="<bean:message key="kq.wizard.zfbd"/>";
       tests.length=0;
      for(var f=0;f<mm.length;f++)
      {
       if(mm[f]=="A")
        {
           r++;
          tests[r]=new Option(kk[f],kk[f]);
        }
      }
  	}
  	if("<bean:message key="kq.wizard.ctod"/>"==$('selId').value)
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.riqi"/>";
  	}
  	if("<bean:message key="kq.wizard.dtoc"/>"==$('selId').value)
  	{
  	   $('ddd').innerHTML="NTOC('#1990.09.08#')";
  	   $('fff').innerHTML="1990.09.08";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ntoc"/>")
  	{
  	   $('ddd').innerHTML="ITOC(1988.02)";
  	   $('fff').innerHTML="1988.02";
  	}
  	if($('selId').value=="<bean:message key="kq.wizard.ctos"/>")
  	{
  	   $('ddd').innerHTML="";
  	   $('fff').innerHTML="<bean:message key="kq.wizard.ctos"/>";
  	}
  	
  }
</SCRIPT>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">


  <fieldset align="center"  style="width:62%;">
  		<legend><bean:message key="kq.formula.function"/></legend>

<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" >
    <tr> 
      <td align="left" colspan=2"" nowrap>
	 <bean:message key="kq.formula.funtions"/> 
 <select NAME="selName" id="selName" onchange="setChange(this)">
    <option value="1"><bean:message key="kq.formula.number"/></option>
    <option value="2"><bean:message key="kq.formula.char"/></option>
    <option value="3"><bean:message key="kq.formula.date"/></option>
    <option value="4"><bean:message key="kq.wizard.buding"/></option>
    <option value="5"><bean:message key="kq.wizard.chlang"/></option>
    <option value="6"><bean:message key="kq.wizard.boolen"/></option>
    <option value="7"><bean:message key="kq.wizard.number"/></option>
    <option value="8"><bean:message key="kq.wizard.option"/></option>
    <option value="10"><bean:message key="kq.wizard.switch"/></option>
    <option value="9"><bean:message key="kq.wizard.other"/></option>
 </select>
<SELECT NAME="selId" id="selId" onchange="test();">
	<option value="<bean:message key="kq.formula.int"/>"><bean:message key="kq.formula.int"/></option>
	<option value="<bean:message key="kq.wizard.round"/>"><bean:message key="kq.wizard.round"/></option>
	<option value="<bean:message key="kq.wizard.ssqr"/>"><bean:message key="kq.wizard.ssqr"/></option>
	<option value="<bean:message key="kq.wizard.ffjy"/>"><bean:message key="kq.wizard.ffjy"/></option>
	<option value="<bean:message key="kq.wizard.ffjj"/>"><bean:message key="kq.wizard.ffjj"/></option>
	
</SELECT>	  
	  </td>	  
    </tr>
  </table>

	 <div id="tavl" style="DISPLAY: none">
  <table  width="60%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
    <tr> 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.formula.paramn"/></td>	  
      <td align="center" class="TableRow" nowrap><bean:message key="kq.formula.paramt"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.formula.parameter"/></td>
    </tr>
    <tr class="trShallow" id="aaa"> 
      <td align="left" class="RecordRow" nowrap id="xxx"><bean:message key="kq.formula.counts"/></td>
      <td align="left" class="RecordRow" nowrap id="xxy"><bean:message key="kq.formula.countt"/></td>
      <td align="left" class="RecordRow" nowrap >
      <SELECT NAME="tests">
      	<option value="">&nbsp;&nbsp;</option>
       </SELECT>	
       </td>
    </tr>
     <tr class="trShallow" id="bbb"> 
      <td align="left" class="RecordRow" nowrap id="bbx"><bean:message key="kq.formula.counts"/></td>
      <td align="left" class="RecordRow" nowrap id="bby"><bean:message key="kq.formula.countt"/></td>
      <td align="left" class="RecordRow" nowrap >
      <SELECT NAME="stest">
      	<option value="">&nbsp;&nbsp;</option>
       </SELECT>	
       </td>
    </tr>
    <tr class="trDeep" id="ggg"> 
      <td align="left" class="RecordRow" nowrap id="xyy" ><bean:message key="kq.formula.character"/></td>
      <td align="left" class="RecordRow" nowrap id="yyy" ><bean:message key="kq.formula.charat"/></td>
      <td align="left" class="RecordRow" nowrap >
      <input type="text" name="ccc" value="" size="10" maxlength="8"></td>
    </tr>
    <tr class="trDeep" id="hhh"> 
      <td align="left" class="RecordRow" nowrap id="yzz"></td>
      <td align="left" class="RecordRow" nowrap id="zzz"><bean:message key="kq.wizard.integer"/></td>
      <td align="left" class="RecordRow" nowrap >
      <input type="text" name="vvv" value="" size="10" maxlength="2"></td>
    </tr>

  </table>
</div>
 </fieldset>  

<table width="60%" align="center" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
  <tr align="left">  
    <td valign="top">
     <fieldset>
	    <legend><bean:message key="kq.formula.explain"/></legend>
	   <br>
	   <bean:message key="kq.wizard.example"/>
	   <div id="ddd">
      </div>	
	  <br>
	   <bean:message key="kq.formula.backv"/>
	  <div id="fff">
     </div>	
  </fieldset>		      
    </td>
  </tr>   
  <tr align="center">  
    <td valign="top">
	   <input type="button" name="Submit3" value="<bean:message key="kq.formula.true"/>" class="mybutton" onclick="sendFromChild();">    	
	   <input type="button" name="Submit3" value="<bean:message key="button.return"/>" class="mybutton" onclick="window.close();">        
    </td>
  </tr>    
</table>

<BODY>
</HTML>

