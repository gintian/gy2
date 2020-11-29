<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<script language="javascript">  
   function Is() {
	    var agent   = navigator.userAgent.toLowerCase();
	    this.major  = parseInt(navigator.appVersion);
	    this.minor  = parseFloat(navigator.appVersion);
	    this.ns     = ((agent.indexOf('mozilla')   != -1) &&
	                  (agent.indexOf('spoofer')    == -1) &&
	                  (agent.indexOf('compatible') == -1) &&
	                  (agent.indexOf('opera')      == -1) &&
	                  (agent.indexOf('webtv')      == -1));
	    this.ns2    = (this.ns && (this.major      ==  2));
	    this.ns3    = (this.ns && (this.major      ==  3));
	    this.ns4    = (this.ns && (this.major      ==  4));
	    this.ns6    = (this.ns && (this.major      >=  5));
	    this.ie     = (agent.indexOf("msie")       != -1);
	    this.ie3    = (this.ie && (this.major      <   4));
	    this.ie4    = (this.ie && (this.major      ==  4) &&
	                  (agent.indexOf("msie 5.0")   == -1));
	    this.ie5    = (this.ie && (this.major      ==  4) &&
	                  (agent.indexOf("msie 5.0")   != -1));
	    this.ieX    = (this.ie && !this.ie3 && !this.ie4);
	}
   function fist(){
		var is = new Is();
		var available_width = 750;
		var available_height = 600; 
		
		var image_width = 750;
		var image_height = 120;	
		
	    if(is.ns4||is.ns6) {
	        available_width=innerWidth;
	        available_height=innerHeight;
	    } else if(is.ie4||is.ie5||is.ieX) {
	        available_width=document.body.clientWidth;
	        available_height=document.body.clientHeight;
	    }
		
	    if(is.ie4 ||is.ie5||is.ns6|| is.ns4||is.ieX) {
	    	
	    	available_width = available_width*0.9;
	    	available_height = available_height*0.9;
	    	
	    	available_width = ""+available_width;
	    	available_height =""+available_height;
	    	
	    	if(available_width.indexOf('.')!=-1){
	    		available_width = available_width.substring(0,available_width.indexOf('.'));
	    	}
	    	if(available_height.indexOf('.')!=-1){
	    		available_height = available_height.substring(0,available_height.indexOf('.'));
	    	}
	    	
			image_width = (available_width);
			image_height = (available_height);

		}
		
		var wid = image_width;
		var hei = image_height/2+50;
		//页面重定向
		location.href="/general/static/history/searchstaticdata.do?b_statFrame=link&w="+wid+"&h="+hei;
		
	}	
	fist();	
</script>