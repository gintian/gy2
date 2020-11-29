/**
 * 
 */
var Utils = new Object();
//获取时间控件的日期格式
Utils.getFormat = function(itemLength){
		if(itemLength ==4){
			dateFormat ="Y";
		}else if(itemLength ==7){
			dateFormat ="Y-m";
		}else if(itemLength ==10){
			dateFormat ="Y-m-d";
		}else if(itemLength ==16){
			dateFormat ="Y-m-d H:i";
		}else if(itemLength >=18){
			dateFormat ="Y-m-d H:i:s";
		}
		return dateFormat;
	};
