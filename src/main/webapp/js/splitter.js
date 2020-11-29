var iBoundMax = 1024;
var iBoundMin = 1;
var bMouseDown = false;
var rowidx=0;
var cellidx=0;
var dragtable;
var orientation;
var ox=0;
var oy=0;
var oh=0;
var dv=0;

function event_init(obj) {
	if (!obj) 
		return;
	var hv=obj.getAttribute("orientation");
	if(!hv)
		return;
	obj.attachEvent("onmousedown",MouseDown);		
	obj.attachEvent("onmouseup",MouseUp);	
}

function MouseUp(e)
{
	var dragobjs=document.getElementsByTagName("td");
	for(var i=0;i<dragobjs.length;i++)
	{
		var tmp=dragobjs[i];
		if(!tmp.getAttribute("orientation"))
			continue;
		tmp.detachEvent ("onmousemove", MouseMove);
		tmp.releaseCapture();   		
	}
	bMouseDown = false;
}

function MouseDown(e)
{				
	var oSlider = e.srcElement;	
	var hv=oSlider.getAttribute("orientation");
	if(hv)
	{
		dragtable=oSlider.offsetParent;
		var tr=oSlider.parentElement ;
		rowidx=tr.rowIndex;
		cellidx=oSlider.cellIndex;
		orientation=hv;
		if(orientation=="horizontal")
		{
			ox=window.event.clientX;
			oy=window.event.clientY;
			oh=parseInt(dragtable.rows[rowidx-1].height);
		}
		bMouseDown = true;
		oSlider.setCapture();
		oSlider.attachEvent ("onmousemove", MouseMove);		
	}	

}

function MouseMove(e)
{
	if(orientation=="vertical")
	{
		var offsetLeft = 0;
		var tempTd = dragtable.rows[rowidx].cells[cellidx-1];
		while (tempTd.offsetParent != null) 
		{
			offsetLeft += tempTd.offsetLeft;
			tempTd = tempTd.offsetParent;
		}		
		var iNewX = window.event.clientX - offsetLeft - 8;	
		if (!dragtable.contains(window.event.srcElement)) 
			iNewX -= offsetLeft + window.document.body.scrollLeft;	
		if (iNewX > iBoundMax) 
			iNewX = iBoundMax;
		if (iNewX < iBoundMin) 
		{
			iNewX = iBoundMin;		
		}
		dragtable.rows[rowidx].cells[cellidx-1].width=iNewX;
	}
	if(orientation=="horizontal")
	{
		var zc=(window.event.clientY-oy);
		var iNewY=zc+oh;	
		if (iNewY > iBoundMax) 
			iNewY = iBoundMax;
		if (iNewY < iBoundMin) 
		{
			iNewY = iBoundMin;		
		}
		dragtable.rows[rowidx-1].height=iNewY;
	}	
}

function doclose()
{
	if(orientation=="horizontal")
	{
		var vv=dragtable.rows[rowidx-1].height;
		if(vv>iBoundMin)
		{
			dv=dragtable.rows[rowidx-1].height;
			dragtable.rows[rowidx-1].height=iBoundMin;
		}
		else
		{
			dragtable.rows[rowidx-1].height=dv;
		}
	}
	if(orientation=="vertical")
	{
		var vv=dragtable.rows[rowidx].cells[cellidx-1].width;
		if(vv>iBoundMin)
		{
			dv=dragtable.rows[rowidx].cells[cellidx-1].width;
			dragtable.rows[rowidx].cells[cellidx-1].width=iBoundMin;
		}
		else
		{
			dragtable.rows[rowidx].cells[cellidx-1].width=dv;
		}
	}	
}
