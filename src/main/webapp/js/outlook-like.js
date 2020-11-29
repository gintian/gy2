//#BACE7D
//#ebf5d6

function showitem(id,name,option)
{
	return ("<br><span class=a9px-height150><a href='"+id+"' class=a1><img src='../images/"+name+"' border=0>&nbsp;"+option+"</a></span><br>")
}


function switchoutlookBar(number)
{       
	var i = outlookbar.opentitle;
        outlookbar.opentitle=number;
	var id1,id2,id1b,id2b
	if (number!=i && outlooksmoothstat==0){
	if (number!=-1)
		{
			if (i==-1)
				{
				id2="blankdiv";
				id2b="blankdiv";}
			else{
				id2="outlookdiv"+i;
				id2b="outlookdivin"+i;
				}
			id1="outlookdiv"+number
			id1b="outlookdivin"+number
                        document.all("outlooktitle"+number).className="show2"
                        if(i!=-1)document.all("outlooktitle"+i).className="show1"
			smoothout(id1,id2,id1b,id2b,0);
		}
	else
		{
			document.all("blankdiv").style.display="";
			document.all("blankdiv").style.height="100%";
			document.all("outlookdiv"+i).style.display="none";
			document.all("outlookdiv"+i).style.height="0%";
			document.all("outlooktitle"+i).style.border="0px solid navy";
			document.all("outlooktitle"+i).style.background="#000000";
			document.all("outlooktitle"+i).style.color="black";
			document.all("outlooktitle"+i).style.textalign="center";
		}
	}
			
}

function smoothout(id1,id2,id1b,id2b,stat)
{                
	if(stat==0){
		tempinnertext1=document.all(id1b).innerHTML;
		tempinnertext2=document.all(id2b).innerHTML;
		outlooksmoothstat=1;
		document.all(id1b).style.overflow="hidden";
		document.all(id2b).style.overflow="hidden";
		document.all(id1).style.height="0%";
		document.all(id1).style.display="";
		setTimeout("smoothout('"+id1+"','"+id2+"','"+id1b+"','"+id2b+"',"+outlookbar.inc+")",outlookbar.timedalay);
	}
	else
	{
		stat+=outlookbar.inc;
                if (stat>100)stat=100;
		document.all(id1).style.height=stat+"%";
		document.all(id2).style.height=(100-stat)+"%";
		if (stat<100) 
			setTimeout("smoothout('"+id1+"','"+id2+"','"+id1b+"','"+id2b+"',"+stat+")",outlookbar.timedalay);
		else
			{
                        document.all(id1b).innerHTML=tempinnertext1;
			document.all(id2b).innerHTML=tempinnertext2;
                        outlooksmoothstat=0;
			//document.all(id1b).style.overflow="auto";
			document.all(id2).style.display="none";
			}
	}
}





function theitem(intitle,instate,inkey)
{
	this.state=instate;
	this.otherclass=" nowrap ";
	this.key=inkey;
	this.title=intitle;
}

function addtitle(intitle)
{
	outlookbar.itemlist[outlookbar.titlelist.length]=new Array();
	outlookbar.titlelist[outlookbar.titlelist.length]=new theitem(intitle,1,0);
/*	if (outlookbar.titlelist.length != (outlookbar.starttitle+1)) 
		outlookbar.titlelist[outlookbar.titlelist.length-1].otherclass=" nowrap align=center style='cursor:hand;background-color:#bace7d;color:white;height:5;border:1 solid navy' ";
	else
		outlookbar.titlelist[outlookbar.titlelist.length-1].otherclass=" nowrap align=center style='cursor:hand;background-color:#C1E0FF;color:#bace7d;height:5;border:1 solid white' ";
*/	return(outlookbar.titlelist.length-1);
}

function additem(intitle,parentid,inkey)
{
	if (parentid>=0 && parentid<=outlookbar.titlelist.length)
	{
		outlookbar.itemlist[parentid][outlookbar.itemlist[parentid].length]=new theitem(intitle,2,inkey);
		outlookbar.itemlist[parentid][outlookbar.itemlist[parentid].length-1].otherclass=" nowrap align=left style='backgroundColor:blue;height:5' ";
		return(outlookbar.itemlist[parentid].length-1);
	}
	else
		additem=-1;
}

function outlook()
{
	this.titlelist=new Array();
	this.itemlist=new Array();
	this.divstyle="style='height:100%;width:100%;overflow:auto' align=center";
	this.otherclass="border=0 cellspacing='0' cellpadding='0' style='height:100%;width:100%'valign=middle align=center ";
	this.addtitle=addtitle;
	this.additem=additem;
        this.starttitle=-1;
	this.opentitle=this.starttitle;
	this.reflesh=outreflesh;
	this.timedelay=100;
	this.inc=10;
	
}

function outreflesh()
{
	document.all("outLookBarDiv").innerHTML=outlookbar.getOutLine();
}

function locatefold(foldname)
{
	for (var i=0;i<outlookbar.titlelist.length;i++)
		if(foldname==outlookbar.titlelist[i].title)
			{
				 outlookbar.starttitle=i;
				 outlookbar.opentitle=i;
			}
	
}
function makevisible(cur,which)
{
	if (which==0)
		cur.filters.alpha.opacity=100
	else
		cur.filters.alpha.opacity=80
}

function getCookie(name)
{
	var cname = name + "=";               
	var dc = document.cookie;
	if (dc.length > 0) 
	{              
		begin = dc.indexOf(cname);
		if (begin != -1) {           
			begin += cname.length;
			end = dc.indexOf(";", begin);
			if (end == -1) end = dc.length;
				return unescape(dc.substring(begin, end));        
		}
	}
	return "";
}
var outlookbar=new outlook();
var tempinnertext1,tempinnertext2,outlooksmoothstat
outlooksmoothstat = 0;
