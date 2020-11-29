var Obj='' ;
var index=10000;//z-index; 
var pwX=100;
var phY=200;
var cyx=1;
document.onmouseup=MUp;
document.onmousemove=MMove ;
function MDown(Object){ 
	Obj=Object.id ;
	document.all(Obj).setCapture() ;
	pwX=event.x-document.all(Obj).style.pixelLeft; 
	phY=event.y-document.all(Obj).style.pixelTop; 
} 

function MMove(){ 
	if(Obj!=''){
	 	if(event.x-pwX>0)
			document.all(Obj).style.left=event.x-pwX;
		else{
			document.all(Obj).style.left=0;
		}
		if(event.y-phY>0)
			document.all(Obj).style.top=event.y-phY; 
		else
			document.all(Obj).style.top=0; 
	} 
} 

function MUp(){ 
	if(Obj!=''){ 
		document.all(Obj).releaseCapture(); 
		Obj=''; 
	} 
} 
function getFocus(obj) { 
	if(obj.style.zIndex!=index) { 
		index = index + 2; 
		var idx = index; 
		obj.style.zIndex=idx;  
	} 
} 
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
}
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function dt(){
	hides("movDiv");
}