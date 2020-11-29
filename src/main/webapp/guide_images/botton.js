
window.onload = function() {
	  
var Ocenter =  document.getElementById("center");
var Oreset =  document.getElementById("reset");
Ocenter.style.backgroundPosition = "0 0";
Oreset.style.backgroundPosition = "-79px 0";
Ocenter.onmouseover = function() {
 Ocenter.style.backgroundPosition = "0 -33px";
}
Ocenter.onmouseout = function() {
Ocenter.style.backgroundPosition = "0 0";
		}
Oreset.onmouseover = function() {
Oreset.style.backgroundPosition = "-79px -33px";
}
Oreset.onmouseout = function() {
Oreset.style.backgroundPosition = "-79px 0";
	}
	  
}