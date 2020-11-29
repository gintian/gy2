	//标题栏字体处理
	function title_fw_s(obj) {
		var title_fs = obj.checked;
	
		if (title_fs) {
			if (document.pageOptionsForm.title_fu.checked) {
				document.pageOptionsForm.title_fw.style.textDecoration = 'underline line-through';
			} else {
				document.pageOptionsForm.title_fw.style.textDecoration = 'line-through';
			}
		} else {
			if (document.pageOptionsForm.title_fu.checked) {
				document.pageOptionsForm.title_fw.style.textDecoration = 'underline';
			} else {
				document.pageOptionsForm.title_fw.style.textDecoration = '';
			}
		}
	}
	function title_fw_u(obj) {
		var title_fu = obj.checked;
		if (title_fu) {
			if (document.pageOptionsForm.title_fs.checked) {
				document.pageOptionsForm.title_fw.style.textDecoration = 'underline line-through';
			} else {
				document.pageOptionsForm.title_fw.style.textDecoration = 'underline';
			}
		} else {
			if (document.pageOptionsForm.title_fs.checked) {
				document.pageOptionsForm.title_fw.style.textDecoration = 'line-through';
			} else {
				document.pageOptionsForm.title_fw.style.textDecoration = '';
			}
		}
	}
    function title_fw_i(obj)
   {
     var title_fi=obj.checked; 
     if(title_fi)
     {       
        document.pageOptionsForm.title_fw.style.fontStyle='italic';
     }else if(!title_fi)
     {
        document.pageOptionsForm.title_fw.style.fontStyle='';         
     }     
   }
   function title_fw_b(obj)
   {
     var title_fb=obj.checked;    
     if(title_fb)
     {       
        document.pageOptionsForm.title_fw.style.fontWeight='700';                
     }else if(!title_fb)
     {    
        document.pageOptionsForm.title_fw.style.fontWeight='400';         
     }     
   }
   function title_fw_n()
   {
     if((document.pageOptionsForm.title_fw_fn.selectedIndex!=-1)&&(document.pageOptionsForm.title_fw_fn.selectedIndex!=0)){
      document.pageOptionsForm.title_fw.style.fontFamily=document.pageOptionsForm.title_fw_fn.options[document.pageOptionsForm.title_fw_fn.selectedIndex].value;}
   }
   function title_fw_z()
   {
   	//liuy  修改常用花名册页面设置，字体下拉列表框不能选中第一个值（5px）  2014-10-8
   	//if((document.pageOptionsForm.title_fw_fz.selectedIndex!=-1)&&(document.pageOptionsForm.title_fw_fz.selectedIndex!=0))
     if((document.pageOptionsForm.title_fw_fz.selectedIndex!=-1)){
      document.pageOptionsForm.title_fw.style.fontSize=document.pageOptionsForm.title_fw_fz.options[document.pageOptionsForm.title_fw_fz.selectedIndex].value+"pt";}
   }
   function title_fw_c()
   {
     if(document.pageOptionsForm.title_fw_fc.value!=-1)
     {      
      document.pageOptionsForm.title_fw.style.color=document.pageOptionsForm.title_fw_fc.value;
      }
   }
   //表头内容处理
   
  //标题栏字体处理
   function head_fw_s(state,obj)
    {
	var head_fs = obj.checked;
	if (state != "0") {
		var eles = document.getElementsByTagName('textarea');
		if (head_fs) {
			if (document.pageOptionsForm.head_fu.checked) {
				for (var i = 0; i < eles.length; i++) {
					if (eles[i].id.indexOf('head') > -1) {
						eles[i].style.textDecoration = 'underline line-through';
					}
				}
			} else {
				document.pageOptionsForm.head_flw.style.textDecoration = 'line-through';
				document.pageOptionsForm.head_fmw.style.textDecoration = 'line-through';
				document.pageOptionsForm.head_frw.style.textDecoration = 'line-through';
			}
		} else {
			if (document.pageOptionsForm.head_fu.checked) {
				document.pageOptionsForm.head_flw.style.textDecoration = 'underline';
				document.pageOptionsForm.head_fmw.style.textDecoration = 'underline';
				document.pageOptionsForm.head_frw.style.textDecoration = 'underline';
			} else {
				document.pageOptionsForm.head_flw.style.textDecoration = '';
				document.pageOptionsForm.head_fmw.style.textDecoration = '';
				document.pageOptionsForm.head_frw.style.textDecoration = '';
			}
		}
	}

}
   function head_fw_u(state,obj)
   {
      var head_fu=obj.checked;
      if(state=="0")
      {
      	  if(head_fu)
          {       
           document.pageOptionsForm.head_fw.style.textDecorationUnderline='underline';
           
          }else
          {
            document.pageOptionsForm.head_fw.style.textDecorationUnderline=''; 
          }  
     }else{
        var eles = document.getElementsByTagName('textarea');
		if (head_fu) {
  			if (document.pageOptionsForm.head_fs.checked) {
  				for (var i = 0; i < eles.length; i++) {
  					if (eles[i].id.indexOf('head') > -1) {
  						eles[i].style.textDecoration = 'underline line-through';
  					}
  				}
  			} else {
  				for (var i = 0; i < eles.length; i++) {
  					if (eles[i].id.indexOf('head') > -1) {
  						eles[i].style.textDecoration = 'underline';
  					}
  				}
  			}
  		} else {
  			if (document.pageOptionsForm.head_fs.checked) {
  				for (var i = 0; i < eles.length; i++) {
  					if (eles[i].id.indexOf('head') > -1) {
  						eles[i].style.textDecoration = 'line-through';
  					}
  				}
  			} else {
  				for (var i = 0; i < eles.length; i++) {
  					if (eles[i].id.indexOf('head') > -1) {
  						eles[i].style.textDecoration = '';
  					}
  				}
  			}
  		}
     
     }
        
   }
   function head_fw_i(state,obj)
   {
      var head_fi=obj.checked;
      if(state=="0")
      {
      	 if(head_fi)
         {       
            document.pageOptionsForm.head_fw.style.fontStyle='italic';            
         }else
         {
            document.pageOptionsForm.head_fw.style.fontStyle='';            
         } 
      }else{
         if(head_fi)
         {       
            document.pageOptionsForm.head_flw.style.fontStyle='italic';
            document.pageOptionsForm.head_fmw.style.fontStyle='italic';
            document.pageOptionsForm.head_frw.style.fontStyle='italic';            
         }else
         {
            document.pageOptionsForm.head_flw.style.fontStyle=''; 
            document.pageOptionsForm.head_fmw.style.fontStyle=''; 
            document.pageOptionsForm.head_frw.style.fontStyle=''; 
         }
      }
         
   }
   function head_fw_b(state,obj)
   {
     var head_fb=obj.checked;
      if(state=="0")
      {
          if(head_fb)
         {       
           document.pageOptionsForm.head_fw.style.fontWeight='700';
         }else
         {    
            document.pageOptionsForm.head_fw.style.fontWeight='400'; 
         } 
     }else
     {
     	 if(head_fb)
         {       
           document.pageOptionsForm.head_flw.style.fontWeight='700';
           document.pageOptionsForm.head_fmw.style.fontWeight='700';
           document.pageOptionsForm.head_frw.style.fontWeight='700';
           
         }else
         {    
            document.pageOptionsForm.head_flw.style.fontWeight='400'; 
            document.pageOptionsForm.head_fmw.style.fontWeight='400'; 
            document.pageOptionsForm.head_frw.style.fontWeight='400'; 
         }
     }         
   }
   function head_fw_n(state)
   {
     if(state=="0")
     {
         if((document.pageOptionsForm.head_fw_fn.selectedIndex!=-1)&&(document.pageOptionsForm.head_fw_fn.selectedIndex!=0)){
         document.pageOptionsForm.head_fw.style.fontFamily=document.pageOptionsForm.head_fw_fn.options[document.pageOptionsForm.head_fw_fn.selectedIndex].value;}
     }else
     {
     	 if((document.pageOptionsForm.head_fw_fn.selectedIndex!=-1)&&(document.pageOptionsForm.head_fw_fn.selectedIndex!=0)){
           document.pageOptionsForm.head_flw.style.fontFamily=document.pageOptionsForm.head_fw_fn.options[document.pageOptionsForm.head_fw_fn.selectedIndex].value;
           document.pageOptionsForm.head_frw.style.fontFamily=document.pageOptionsForm.head_fw_fn.options[document.pageOptionsForm.head_fw_fn.selectedIndex].value;
           document.pageOptionsForm.head_fmw.style.fontFamily=document.pageOptionsForm.head_fw_fn.options[document.pageOptionsForm.head_fw_fn.selectedIndex].value;
         }
     }
    
   }
   function head_fw_z(state)
   {
   	if(state=="0")
   	{
   		 if((document.pageOptionsForm.head_fw_fz.selectedIndex!=-1)&&(document.pageOptionsForm.head_fw_fz.selectedIndex!=0)){
                  document.pageOptionsForm.head_fw.style.fontSize=document.pageOptionsForm.head_fw_fz.options[document.pageOptionsForm.head_fw_fz.selectedIndex].value+"pt";}
   	}else{
   	     if((document.pageOptionsForm.head_fw_fz.selectedIndex!=-1)){
                  document.pageOptionsForm.head_flw.style.fontSize=document.pageOptionsForm.head_fw_fz.options[document.pageOptionsForm.head_fw_fz.selectedIndex].value+"pt";
                  document.pageOptionsForm.head_fmw.style.fontSize=document.pageOptionsForm.head_fw_fz.options[document.pageOptionsForm.head_fw_fz.selectedIndex].value+"pt";
                  document.pageOptionsForm.head_frw.style.fontSize=document.pageOptionsForm.head_fw_fz.options[document.pageOptionsForm.head_fw_fz.selectedIndex].value+"pt";
             }
   	
   	}
    
   }
   function head_fw_c(state)
   {
     if(document.pageOptionsForm.head_fw_fc.value!=-1)
     {      
        document.pageOptionsForm.head_flw.style.color=document.pageOptionsForm.head_fw_fc.value;
        document.pageOptionsForm.head_frw.style.color=document.pageOptionsForm.head_fw_fc.value;
        document.pageOptionsForm.head_fmw.style.color=document.pageOptionsForm.head_fw_fc.value;
     }
   }
    //表尾内容处理    
   function tile_fw_s(state,obj)
    {
	var tile_fs = obj.checked;
	if (state != "0") {
		var eles = document.getElementsByTagName('textarea');
		if (tile_fs) {
			if (document.pageOptionsForm.tile_fu.checked) {
				for (var i = 0; i < eles.length; i++) {
					if (eles[i].id.indexOf('tile') > -1
							&& eles[i].id != 'title_fw') {
						eles[i].style.textDecoration = 'underline line-through';
					}
				}
			} else {
				for (var i = 0; i < eles.length; i++) {
					if (eles[i].id.indexOf('tile') > -1
							&& eles[i].id != 'title_fw') {
						eles[i].style.textDecoration = 'line-through';
					}
				}
			}
		} else {
			if (document.pageOptionsForm.tile_fu.checked) {
				for (var i = 0; i < eles.length; i++) {
					if (eles[i].id.indexOf('tile') > -1
							&& eles[i].id != 'title_fw') {
						eles[i].style.textDecoration = 'underline';
					}
				}
			} else {
				for (var i = 0; i < eles.length; i++) {
					if (eles[i].id.indexOf('tile') > -1
							&& eles[i].id != 'title_fw') {
						eles[i].style.textDecoration = '';
					}
				}
			}
		}
	}

}
   function tile_fw_u(state,obj)
   {
      var tile_fu=obj.checked;
      if(state=="0")
      {
      	  if(tile_fu)
          {       
           document.pageOptionsForm.tile_fw.style.textDecorationUnderline='underline';
          }else
          {
             document.pageOptionsForm.tile_fw.style.textDecorationUnderline=''; 
          }  
     }else{
    	var eles = document.getElementsByTagName('textarea');
 		if (tile_fu) {
   			if (document.pageOptionsForm.tile_fs.checked) {
   				for (var i = 0; i < eles.length; i++) {
   					if (eles[i].id.indexOf('tile') > -1 && eles[i].id!='title_fw') {
   						eles[i].style.textDecoration = 'underline line-through';
   					}
   				}
   			} else {
   				for (var i = 0; i < eles.length; i++) {
   					if (eles[i].id.indexOf('tile') > -1 && eles[i].id!='title_fw') {
   						eles[i].style.textDecoration = 'underline';
   					}
   				}
   			}
   		} else {
   			if (document.pageOptionsForm.tile_fs.checked) {
   				for (var i = 0; i < eles.length; i++) {
   					if (eles[i].id.indexOf('tile') > -1 && eles[i].id!='title_fw') {
   						eles[i].style.textDecoration = 'line-through';
   					}
   				}
   			} else {
   				for (var i = 0; i < eles.length; i++) {
   					if (eles[i].id.indexOf('tile') > -1 && eles[i].id!='title_fw') {
   						eles[i].style.textDecoration = '';
   					}
   				}
   			}
   		}
     
     }
        
   }
   function tile_fw_i(state,obj)
   {
      var tile_fi=obj.checked;	
      if(state=="0")
      {
      	 if(tile_fi)
         {       
            document.pageOptionsForm.tile_fw.style.fontStyle='italic';
          
         }else
         {
            document.pageOptionsForm.tile_fw.style.fontStyle=''; 
            
         } 
      }else{
         if(tile_fi)
         {       
            document.pageOptionsForm.tile_flw.style.fontStyle='italic';
            document.pageOptionsForm.tile_fmw.style.fontStyle='italic';
            document.pageOptionsForm.tile_frw.style.fontStyle='italic';
           
         }else
         {
            document.pageOptionsForm.tile_flw.style.fontStyle=''; 
            document.pageOptionsForm.tile_fmw.style.fontStyle=''; 
            document.pageOptionsForm.tile_frw.style.fontStyle=''; 
         }
      }
         
   }
   function tile_fw_b(state,obj)
   {
      var tile_fb=obj.checked;	
      if(state=="0")
      {
          if(tile_fb)
         {       
           document.pageOptionsForm.tile_fw.style.fontWeight='700';           
         }else
         {    
            document.pageOptionsForm.tile_fw.style.fontWeight='400';            
         } 
     }else
     {
     	 if(tile_fb)
         {       
           document.pageOptionsForm.tile_flw.style.fontWeight='700';
           document.pageOptionsForm.tile_fmw.style.fontWeight='700';
           document.pageOptionsForm.tile_frw.style.fontWeight='700';           
         }else
         {    
            document.pageOptionsForm.tile_flw.style.fontWeight='400'; 
            document.pageOptionsForm.tile_fmw.style.fontWeight='400'; 
            document.pageOptionsForm.tile_frw.style.fontWeight='400';            
          }
     }         
   }
   function tile_fw_n(state)
   {
     if(state=="0")
     {
         if((document.pageOptionsForm.tile_fw_fn.selectedIndex!=-1)&&(document.pageOptionsForm.tile_fw_fn.selectedIndex!=0)){
         document.pageOptionsForm.tile_fw.style.fontFamily=document.pageOptionsForm.tile_fw_fn.options[document.pageOptionsForm.tile_fw_fn.selectedIndex].value;}
     }else
     {
     	 if((document.pageOptionsForm.tile_fw_fn.selectedIndex!=-1)&&(document.pageOptionsForm.tile_fw_fn.selectedIndex!=0)){
           document.pageOptionsForm.tile_flw.style.fontFamily=document.pageOptionsForm.tile_fw_fn.options[document.pageOptionsForm.tile_fw_fn.selectedIndex].value;
           document.pageOptionsForm.tile_frw.style.fontFamily=document.pageOptionsForm.tile_fw_fn.options[document.pageOptionsForm.tile_fw_fn.selectedIndex].value;
           document.pageOptionsForm.tile_fmw.style.fontFamily=document.pageOptionsForm.tile_fw_fn.options[document.pageOptionsForm.tile_fw_fn.selectedIndex].value;
         }
     }
    
   }
   function tile_fw_z(state)
   {
   	if(state=="0")
   	{
   		 if((document.pageOptionsForm.tile_fw_fz.selectedIndex!=-1)&&(document.pageOptionsForm.tile_fw_fz.selectedIndex!=0)){
                  document.pageOptionsForm.tile_fw.style.fontSize=document.pageOptionsForm.tile_fw_fz.options[document.pageOptionsForm.tile_fw_fz.selectedIndex].value+"pt";}
   	}else{
   	     if((document.pageOptionsForm.tile_fw_fz.selectedIndex!=-1)){
                  document.pageOptionsForm.tile_flw.style.fontSize=document.pageOptionsForm.tile_fw_fz.options[document.pageOptionsForm.tile_fw_fz.selectedIndex].value+"pt";
                  document.pageOptionsForm.tile_fmw.style.fontSize=document.pageOptionsForm.tile_fw_fz.options[document.pageOptionsForm.tile_fw_fz.selectedIndex].value+"pt";
                  document.pageOptionsForm.tile_frw.style.fontSize=document.pageOptionsForm.tile_fw_fz.options[document.pageOptionsForm.tile_fw_fz.selectedIndex].value+"pt";
             }
   	
   	}
    
   }
   function tile_fw_c(state)
   {
     if(document.pageOptionsForm.tile_fw_fc.value!=-1)
     {      
        document.pageOptionsForm.tile_flw.style.color=document.pageOptionsForm.tile_fw_fc.value;
        document.pageOptionsForm.tile_frw.style.color=document.pageOptionsForm.tile_fw_fc.value;
        document.pageOptionsForm.tile_fmw.style.color=document.pageOptionsForm.tile_fw_fc.value;
     }
   }