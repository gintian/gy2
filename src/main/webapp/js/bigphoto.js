$(function(){
	var x = 10;
	var y = 20;
	var maxx=document.body.scrollWidth;
	var maxy=document.body.scrollHeight;
	if(($("img") instanceof Object)&&$("img").length&&$("img").length>0){
	$("img").mouseover(function(e){
		var topx=0,topy=0;
		topy=(e.pageY+y+300)>maxy?(e.pageY-300-y):(e.pageY+y);
		topx=(e.pageX+x+200)>maxx?(e.pageX-200-x):(e.pageX+x);
		topy=topy<0?0:topy;
		topx=topx<0?0:topx;
		var t=e.target?e.target:e.srcElement;
		if(t.src.indexOf("photo.jpg")==-1&&t.src.indexOf("go.gif")==-1){
			/* 照片墙鼠标滑过事件去掉多余的边框 xiaoyun 2014-6-17 start */
			// var tooltip = "<div id='tooltip'><img width='200px' height='300px'  src='"+ t.src +"'/></div>"; //创建 div 元素
			// var tooltip = "<img id='tooltip' width='212px' height='300px'  src='"+ t.src +"'/>"; 
			/* 图片按照比例缩放 xiaoyun 2014-7-3 start */
			 var img = new Image();
			 img.src=t.src;			 
			 var width = 200;
			 var height = 200*(img.height/img.width);
			 var tooltip = "<img id='tooltip' width='"+width+"px' height='"+height+"px' src='"+ t.src +"'/>";
			 /* 图片按照比例缩放 xiaoyun 2014-7-3 end */			 
			/* 照片墙鼠标滑过事件去掉多余的边框 xiaoyun 2014-6-17 end */
			$("body").append(tooltip);	//把它追加到文档中					
			$("#tooltip")
				.css({
					"top": topy + "px",
					"left": topx  + "px"
				}).show("fast");	  //设置x坐标和y坐标，并且显示
		}
	   
    }).mouseout(function(){	
		$("#tooltip").remove();	 //移除 
    }).mousemove(function(e){
    	var topx=0,topy=0;
		topy=(e.pageY+y+300)>maxy?(e.pageY-300-y):(e.pageY+y);
		topx=(e.pageX+x+200)>maxx?(e.pageX-200-x):(e.pageX+x);
		topy=topy<0?0:topy;
		topx=topx<0?0:topx;
		$("#tooltip")
			.css({
				"top": topy + "px",
				"left": topx  + "px"
			});
	});
	}
})