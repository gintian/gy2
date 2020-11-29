$URL=function(){
	return{
		encode:function(str){
			return encodeURIComponent(str);
		},
		decode:function(str){
			return decodeURIComponent(str);
		}
	};
}();