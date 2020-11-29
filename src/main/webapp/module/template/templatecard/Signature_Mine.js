/**
 * add by hej
 * 签章生成(添加签章)
 */
function initSignature(setname,signaturename,PageID,solveflag){
	var DocumentID = "";
	var username = setname.split("_")[0];
	var GridNO = signaturename.split("S")[1];
	var fields=templateCard_me.fieldSet.fields;//如果是签章且没有写权限,直接返回false,不执行签章操作。
	for(var num=0;num<fields.length;num++){
		var field=fields[num];
		if(field.uniqueId==("fld_"+PageID+"_"+GridNO)){
			if(field.rwPriv==0||field.rwPriv==1){
				return false;
			}
			break;
		}
	}
	var imgUrlList = templateCard_me.imgUrlList;
	if(imgUrlList==""){
		Ext.showAlert(MB.LABEL.CURRENTUSERNOSIGNATURE);
		return false;
	}
	if(templateCard_me.signature_usb){//设置了启用锁
		if(websock&&ctrl!=null){//非ie浏览器
			checkClockForSignature(0,PageID,GridNO,signaturename,solveflag,username);
		}else{
			var PID = Arm_GetDongleInfo(0);
			if(PID!=""){
				Arm_Open();
			    //1、首先判断是不是发布的锁，不是，给出提示。
			    //读取0~2048位置存储的des加密的硬件id
				var PID_en = Arm_ReadData(0,16);
				//给PID_en解密 并判断PID_en与PID是否相同
				var map = new HashMap();
				map.put('type','1');//=0 加密 =1 解密
				map.put('data_en',PID_en);
				map.put('data',PID);
				Rpc({functionId:'MB00007006',async:false,success:function(form,action){
					var result = Ext.decode(form.responseText);
			    	var flag=result.succeed;
					if(flag==true){ 
						var data_de = result.data_de;
						if(data_de==PID){
						    //2、读取绑定信息“用户名称+硬件ID”加密
							var BaningID = Arm_ReadData(2048,128);
							var map = new HashMap();
							map.put('flag','2');
							map.put('BaningID',BaningID);
							Rpc({functionId:'MB00007001',async:false,success:function(form,action){
								var result = Ext.decode(form.responseText);
								templateCard_me.bandingflag = result.bandingflag;
								if(!templateCard_me.bandingflag){
									Ext.showAlert(MB.LABEL.CHECKNOBANDINGCURRENTUSERNOSIGNATURE,function(){
										Arm_Close();
										return false;
									});
								}else{
									addSignature(PageID,GridNO,signaturename,solveflag,username);
									//Arm_Close();
								}
							},scope:this},map);
						}else{
							Ext.showAlert(MB.LABEL.CHECKNOHJSOFTCLOCKNOSIGNATURE,function(){
								Arm_Close();
								return false;
							});
						}
			  		}else{
						Ext.showAlert(result.message,function(){
							Arm_Close();
							return false;
						});
					}
				},scope:this},map);
			}
		}
	}else
		addSignature(PageID,GridNO,signaturename,solveflag,username);
}
/**
 * 检测锁进而签章 非ie浏览器
 * @param PageID
 * @param GridNO
 * @param signaturename
 * @param solveflag
 * @param username
 * @returns
 */
function checkClockForSignature(clockflag,PageID,GridNO,signaturename,solveflag,username){
	ctrl.Arm_Enum(function(result, response){
		if (!result){
			Ext.showAlert("Arm_Enum error. " + response);
		}else{
    		var index = response;
    		if(index<0){
    			index=0;
    		}
    		if(index>1){
    			if(clockflag==1){
    				Ext.showAlert(MB.LABEL.NOMORECLOCKBANDING,function(){
    					return;
    				});
    			}else if(clockflag==0){
    				Ext.showAlert(MB.LABEL.CHECKMORECLOCK,function(){
    					return;
    				});
    			}
    		}
    		if(index==0){
    			Ext.showAlert(MB.LABEL.CHECKNOCLOCK,function(){
    				return;
    			});
    		}
    		DongleInfoNum = 2;	//硬件ID
    		if(index>0){
    			Index = index-1;
				ctrl.Arm_GetDongleInfo(function(result, response){
					if (!result){
						Ext.showAlert("Arm_GetDongleInfo error. " + response);
					}else{
		        		var PID = response;
		        		if(PID!=''){
		        			ctrl.Arm_Open(function(result, response){
		        				if (!result){
		        					Ext.showAlert("Arm_Open error. " + response);
		        				}else{
		        					ArmHandle = response;
		        					Offset = 0;
		        					ReadLength = 16;
		        					Handle = ArmHandle;
		        					ctrl.Arm_ReadData(function(result,response){
		        						if(!result){
		        							Ext.showAlert("Arm_ReadData error. " + response);
		        						}else{
		        							var PID_en = response;
		        							var map = new HashMap();
		            						map.put('type','1');//=0 加密 =1 解密
		            						map.put('data_en',PID_en);
		            						map.put('data',PID);
		            						Rpc({functionId:'MB00007006',async:false,success:function(form,action){
		            							var result = Ext.decode(form.responseText);
		            					    	var flag=result.succeed;
		            							if(flag==true){ 
		            								var data_de = result.data_de;
		            								if(data_de==PID){
		            								    //2、读取绑定信息“用户名称+硬件ID”加密
		            									Offset = 2048;
		            									ReadLength = 128;
		            									ctrl.Arm_ReadData(function(result,response){
		            										if(!result){
		            		        							Ext.showAlert("Arm_ReadData error. " + response);
		            		        						}else{
		            		        							var BaningID = response;
		            		        							var map = new HashMap();
				            									map.put('flag','2');
				            									map.put('BaningID',BaningID);
				            									Rpc({functionId:'MB00007001',async:false,success:function(form,action){
				            										var result = Ext.decode(form.responseText);
				            										templateCard_me.bandingflag = result.bandingflag;
				            										if(!templateCard_me.bandingflag){
				            											Ext.showAlert(MB.LABEL.CHECKNOBANDINGCURRENTUSERNOSIGNATURE,function(){
				            												//Arm_Close();
				            												return false;
				            											});
				            										}else{
				            											addSignature(PageID,GridNO,signaturename,solveflag,username);
				            											//Arm_Close();
				            										}
				            									},scope:this},map);
		            		        						}
		            									})
		            								}else{
		            									Ext.showAlert(MB.LABEL.CHECKNOHJSOFTCLOCKNOSIGNATURE,function(){
		            										return false;
		            										//Arm_Close();
		            									});
		            								}
		            					  		}else{
		            								Ext.showAlert(result.message,function(){
		            									return false;
		            									//Arm_Close();
		            								});
		            							}
		            						},scope:this},map);
		        						}
		        					})
		        				}
		        			})
		        		}
					}
				})	
    		}
		}
	})
}
/**
 * 添加签章
 * @param PageID
 * @param GridNO
 * @param signaturename
 * @param solveflag
 * @param username
 * @returns
 */
function addSignature(PageID,GridNO,signaturename,solveflag,username){
	var signatureimg_id = "fld_"+PageID+"_"+GridNO+"_signid";
	var menuid = "fld_"+PageID+"_"+GridNO+"_menu";
	var signatureimg = document.getElementById(signatureimg_id); 
	if(signatureimg){
		return false;
	}
	var div = document.getElementById(signaturename);
	var height = parseInt(div.children[0].style.height+0);
	var width = parseInt(div.children[0].style.width+0);
	var left = parseInt(div.children[0].style.left+0);
	var top = parseInt(div.children[0].style.top+0);
	//拿到当前登录用户的签章
	var imgUrlList = templateCard_me.imgUrlList;
	if(imgUrlList==""){
		Ext.showAlert(MB.LABEL.CURRENTUSERNOSIGNATURE);
		return false;
	}
	var imgUrlarr = imgUrlList.split(",");
	if(imgUrlarr.length>=1){
		//需要弹窗选择签章
		var data = [];
		var isHavepass = false;
		var onlysignatureid = "";
		var onlyusername = "";
		var onlymarkpath = "";
		var onlyimgwidth = 0;
		var onlyimgheight = 0;
		var signaturemap = new HashMap();
		for(var i=0;i<imgUrlarr.length;i++){
			var imgurl = imgUrlarr[i];
			var imgarr = imgurl.split('`');
			var signatureid = imgarr[0];
			var password = imgarr[1];
			var markpath = imgarr[2];
			var markname = replaceAll(imgarr[3],"，",",");
			markname = replaceAll(markname,"＾","^");
			markname = replaceAll(markname,"～","~");
			markname = replaceAll(markname,"｀","`");
			var username_ = imgarr[4];
			var imgwidth = imgarr[5];
			var imgheight = imgarr[6];
			var record = {};
			var signaturerecord = [];
			signaturerecord.push(password);
			signaturerecord.push(markpath);
			signaturerecord.push(username_);
			signaturerecord.push(imgwidth);
			signaturerecord.push(imgheight);
			record.dataValue = signatureid;
			record.dataName = markname;
			data.push(record);
			signaturemap.put(signatureid,signaturerecord);
			if(imgUrlarr.length==1){
				if(password!=null&&password!='')
					isHavepass = true;
				onlysignatureid = signatureid;
				onlymarkpath = markpath;
				onlyusername = username;
				onlyimgwidth = imgwidth;
				onlyimgheight = imgheight;
			}else{
				if(password!=null&&password!=''&&!isHavepass)
					isHavepass = true;
			}
		}
		var store = Ext.create('Ext.data.Store', {
    	    fields: ['dataValue', 'dataName'],
    	    data : data
    	});
		var win = Ext.widget('window',{   
			title:MB.LABEL.SELECTSIGNATURE,  
			height:250,
			width:500,
			layout:'fit',
			modal:true,           
			closeAction:'destroy',
			resizable:false,
			items: [{xtype:'container',width:490,items:[
				{xtype:'combobox',
    	   			fieldLabel:MB.LABEL.USERSIGNATURE,
    	   			id:'usersignature',
    	   			labelAlign:'right',
    	   			store:store,
    	   			displayField:'dataName',
    	   			valueField:'dataValue',
    	   			labelSeparator :'',
    	   			margin:isHavepass?'40 0 5 60':'80 0 5 60',
    	   			editable:false,
    	   			queryMode:'local',
    	   			emptyText:MB.LABEL.PLEASESELECT,
    	   			labelWidth:110,
    	   			value:onlysignatureid,
    	   			width:300,
    	   			listeners:{
    	   				select:function(combo,record){
    	   					var value = combo.getValue();
    	   					var signaturerecord = signaturemap.get(value);
        					var password = signaturerecord[0];
        					if(password!=null&&password!=''){
        						Ext.getCmp('passwordcon').show();
        					}else if(password==''){
        						Ext.getCmp('passwordcon').hide();
        					}
    	   				}
    	   			}
    	   		},{xtype:'container',id:'passwordcon',layout:'hbox',hidden:isHavepass?false:true,items:[{
	    	   			xtype:'textfield',
	    	   			fieldLabel: MB.LABEL.SIGNATUREPASSWORD,
	    	   			margin:'30 0 0 60',
	        			id:'password',
	        			inputType:'password',
	        			labelAlign:'right',
	            		labelWidth:110,
	            		width:300,
	        			fieldStyle:'height:20px;',
	        			listeners:{
	        				blur:function(e,event,eOpts){
	        					var value = Ext.getCmp('usersignature').getValue();
	        					var signaturerecord = signaturemap.get(value);
	        					var password = signaturerecord[0];
	        					if(e.value!=''&&e.value!=password){
	        						Ext.getCmp('checkpassword').setText(MB.LABEL.SIGNATUREPASSWORDERROR);
	        					}else if(e.value==''&&password!=''){
	        						Ext.getCmp('checkpassword').setText(MB.LABEL.PLEASEWRITEPASSWORD);
	        					}else{
	        						Ext.getCmp('checkpassword').setText('');
	        					}
	        				}
	        			}
    	   			},{xtype:'label',margin:'30 0 0 5',id:'checkpassword',style:{color:'red'}}]
    	   		}/*,{xtype:'checkbox',margin:'20 0 0 122',boxLabel:MB.LABEL.REMEMBERPASSWORD,id:'checkbox_pass'}*/
			]}],
			buttonAlign:'center',
    		buttons:[{text:MB.LABEL.SURE,handler: function() {
    			var value = Ext.getCmp('usersignature').getValue();
				var signaturerecord = signaturemap.get(value);
				var password = signaturerecord[0];
				var markpath = signaturerecord[1];
				var username = signaturerecord[2];
				var imgwidth = signaturerecord[3];
				var imgheight = signaturerecord[4];
				if(Ext.getCmp('password').getValue()!=''&&Ext.getCmp('password').getValue()!=password){
					Ext.getCmp('checkpassword').setText(MB.LABEL.SIGNATUREPASSWORDERROR);
					return;
				}else if(Ext.getCmp('password').getValue()==''&&password!=''){
					Ext.getCmp('checkpassword').setText(MB.LABEL.PLEASEWRITEPASSWORD);
					return;
				}else{//密码正确
					/*//如果勾选记住密码
					var remberflag = Ext.getCmp('checkbox_pass').getValue();
					if(remberflag)
						templateCard_me.remembersignature += value;*/
					Ext.getCmp('checkpassword').setText('');
				}
				win.close();
				//生成图片
				var tabdivid = "tabdiv_tab_"+PageID;
				var tabdiv = document.getElementById(tabdivid);
		        var zindex = 3;
				createImg(signaturename,PageID,GridNO,markpath,(left+(width-imgwidth)/2)+'px',(top+(height-imgheight)/2)+'px',true,'false',imgwidth,imgheight,zindex,'0',username,value,solveflag);
    		}},{
		    	text:MB.LABEL.CANCEL,
		    	handler: function() {
		    		win.close();
			    }}]
		});
		if(imgUrlarr.length>1||(imgUrlarr.length==1&&isHavepass)){
			win.show();
		}
		else if(imgUrlarr.length==1&&!isHavepass){//没有密码
			//生成图片
			var tabdivid = "tabdiv_tab_"+PageID;
			var tabdiv = document.getElementById(tabdivid);
			var zindex = 3;
	        createImg(signaturename,PageID,GridNO,onlymarkpath,(left+(width-onlyimgwidth)/2)+'px',(top+(height-onlyimgheight)/2)+'px',true,'false',onlyimgwidth,onlyimgheight,zindex,'0',onlyusername,onlysignatureid,solveflag);
		}
	}else{
		
	}
}
/**
 * 批量添加签章
 * @param markpath
 * @returns
 */
function batchSignature(markpath){
	var map = new HashMap();
	map.put("object_id" , templateCard_me.object_id);
	map.put("cur_task_id" , templateCard_me.cur_task_id);
	map.put("cur_ins_id" , templateCard_me.cur_ins_id);
	map.put("tab_id",templateMain_me.templPropety.tab_id); 
	map.put("task_id",templateMain_me.templPropety.task_id);
	map.put("infor_type",templateMain_me.templPropety.infor_type);
	map.put("module_id",templateMain_me.templPropety.module_id);
	map.put("allNum",templateTool_me.getTotalCount());
	map.put("signxml",templateCard_me.signXml);
	map.put("markpath",markpath);
	Rpc({functionId:'MB00007005',async:false,success:function(form,action){
		var result = Ext.decode(form.responseText);
		if(!result.succeed){
			var message = result.message;
			if(message&&message.indexOf("拆分审批")!=-1){
				templateTool_me.checkSpllit(message);
			}
		}else{
			
		}
	}},map);
}
/**
 * 创建签章图片 一个区域只能签盖一个章
 * @param signaturename
 * @param PageID
 * @param GridNO
 * @param markpath
 * @param pointx
 * @param pointy
 * @param moveflag
 * @param isdelneedpass
 * @param imgwidth
 * @param imgheight
 * @param zindex
 * @param savetodiskflag
 * @param flag
 * @param username
 * @param signatureid
 * @param solveflag
 * @returns
 */
function createImg(signaturename,PageID,GridNO,markpath,pointx,pointy,moveflag,isdelneedpass,imgwidth,imgheight,zindex,flag,username,signatureid,solveflag){
	var ie = Ext.isIE;
	var meizz;
	var signaturediv = document.getElementById(signaturename);
	var tabdivid = "tabdiv_tab_"+PageID;
	var div = document.getElementById(tabdivid);
	var id = "fld_"+PageID+"_"+GridNO+"_signid";
	var signatureimg = document.getElementById(id); 
	if(signatureimg){
	}else{
		var img;
		if(ie){
			img = document.createElement("img");
			img.setAttribute('style' , "left:"+pointx+";top:"+pointy+";width:"+imgwidth+"px;height:"+imgheight+"px;position:absolute;z-index:"+zindex+";");
			if(flag=='1'){//回显
				var src = 'data:image/png;base64,'+markpath;
				img.setAttribute('src',src);
				img.setAttribute('id',id);
			    addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath,solveflag,flag);
			}else{
				/*if(templateCard_me.signature_usb){
					//从锁中读取文件
					if(websock&&ctrl!=null){//非ie浏览器
						//从锁中读取文件
						if(savetodiskflag!=undefined&&savetodiskflag!=''){
							var markpath_ = "";
							var diskflagarr = savetodiskflag.split("_");
							var firstnum = parseInt(diskflagarr[0]);
							var lastnum = parseInt(diskflagarr[0])+parseInt(diskflagarr[1]);
							readClockFile(firstnum,lastnum,markpath_,img,div,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,solveflag,flag);
						}
					}else{
						if(savetodiskflag!=undefined&&savetodiskflag!=''){
							var markpath_ = "";
							var diskflagarr = savetodiskflag.split("_");
							var j = 0;
							for(var i=parseInt(diskflagarr[0]);i<parseInt(diskflagarr[0])+parseInt(diskflagarr[1]);i++){
								var FileID = "000"+i;
								var ReadLength = 4096;
								var FileOffset = 0;
								var filedata = Arm_ReadFile(FileID,ReadLength,FileOffset);
								markpath_+=b.decode(b.decode(filedata));
								j++;
							}
							markpath = markpath_;
							var src = 'data:image/png;base64,'+markpath;
							img.setAttribute('src',src);
							img.setAttribute('id',id);
						    addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath,solveflag,flag);
						}
					}
				}else{*/
					var src = 'data:image/png;base64,'+markpath;
				    img.setAttribute('src',src);
				    img.setAttribute('id',id);
				    addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath,solveflag,flag);
				//}
			}
		}else{
			img=new Image();
			img.style = "left:"+pointx+";top:"+pointy+";width:"+imgwidth+"px;height:"+imgheight+"px;position:absolute;z-index:"+zindex+";";
			if(flag=='0'){
				/*if(templateCard_me.signature_usb&&templateCard_me.isSaveToDisk=='0'){//勾选了
					//从锁中读取文件
					if(websock&&ctrl!=null){//非ie浏览器
						//从锁中读取文件
						if(savetodiskflag!=undefined&&savetodiskflag!=''){
							var markpath_ = "";
							var diskflagarr = savetodiskflag.split("_");
							var firstnum = parseInt(diskflagarr[0]);
							var lastnum = parseInt(diskflagarr[0])+parseInt(diskflagarr[1]);
							readClockFile(firstnum,lastnum,markpath_,img,div,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,solveflag,flag);
						}
					}
				}else{*/
					var src = 'data:image/png;base64,'+markpath;
					img.src=src;
					img.id=id;
					addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath,solveflag,flag);
				//}
			}else{//回显
				var src = 'data:image/png;base64,'+markpath;
				img.src=src;
				img.id=id;
				addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath,solveflag,flag);
			}
		}
	}
}
/**
 * 给图片添加事件
 * @param div
 * @param img
 * @param moveflag
 * @param PageID
 * @param GridNO
 * @param signaturename
 * @param isdelneedpass
 * @param id
 * @param imgwidth
 * @param imgheight
 * @param zindex
 * @param username
 * @param signatureid
 * @param markpath
 * @param solveflag
 * @param flag
 * @returns
 */
function addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath,solveflag,flag){
	var imgs = div.children[1].getElementsByTagName("img");//找寻所有img
	if(imgs.length>0){
		var num_ = 0;
		for(var i=0;i<imgs.length;i++){
        	var id_ = imgs[i].id;
        	if(id_.indexOf('_signid')!=-1){
        		num_++;
        	}else
        		continue;
        }
		if(num_>0){
			var imgid = imgs[num_-1].id;
			var next = document.getElementById(imgid).nextSibling;
			div.children[1].insertBefore(img,next);
		}else
			div.children[1].insertBefore(img,div.children[1].children[0]);
	}else
		div.children[1].insertBefore(img,div.children[1].children[0]);
	var edgetop = parseInt(div.children[1].style.top+0);
	var edgeleft = parseInt(div.children[1].style.left+0);
	var edgeheight = parseInt(div.children[1].style.height+0);
	var edgewidth = parseInt(div.children[1].style.width+0);
	/*鼠标将图片在相应的div自由拖动*/
	var nn6 = document.getElementById && !document.all;
	var isdrag = false;
	var y,x;
	var oDragObj;
	img.onmousedown = function (e) {
	    var oDragHandle = nn6 ? e.target : event.srcElement;
	    if (oDragHandle.id == id&&moveflag) {
	        isdrag = true;
	        oDragObj = oDragHandle;
	        nTY = parseInt(oDragObj.style.top + 0);
	        y = nn6 ? e.clientY : event.clientY;
	        nTX = parseInt(oDragObj.style.left + 0);
	        x = nn6 ? e.clientX : event.clientX;
	        img.onmousemove = function (e) {
	        	var el = nn6 ? e.target : event.srcElement;
	            if (isdrag&&el.id==id) {
	            	var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
	                var delimg = document.getElementById(delid);
	                if(delimg)
	                	div.children[1].removeChild(delimg);
	            	var imgtop = (nn6 ? nTY + e.clientY - y : nTY + event.clientY - y);
	            	var imgleft = (nn6 ? nTX + e.clientX - x : nTX + event.clientX - x);
	            	if(imgtop<=edgetop){
	            		imgtop=edgetop;
	            	}
	            	if(imgtop>=edgetop+edgeheight-imgheight){
	            		imgtop=edgetop+edgeheight-imgheight;
	            	}
	            	if(imgleft<=edgeleft){
	            		imgleft=edgeleft;
	            	}
	            	if(imgleft>=edgeleft+edgewidth-imgwidth){
	            		imgleft=edgeleft+edgewidth-imgwidth;
	            	}
	                oDragObj.style.top = imgtop + "px";
	                oDragObj.style.left = imgleft + "px";
	                return false;
	            }
	        };
	        return false;
	    }
	};
	img.onmouseup = function(e){
		isdrag=false;
    };
    img.onmouseover = function(e){
    	//鼠标滑过添加右上角删除图标
    	var oDragHandle = nn6 ? e.target : event.srcElement;
    	if (oDragHandle.id == id&&moveflag&&!isdrag) {
    		//meizz = setTimeout(function(){
    			var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
    			oDragObj = oDragHandle;
    			var width = parseInt(document.getElementById(id).width);
    			var height = parseInt(document.getElementById(id).height);
    			nTY = parseInt(oDragObj.style.top + 0);
    		    nTX = parseInt(oDragObj.style.left + 0);
    		    var img_del;
    		    if(Ext.isIE){
    		    	img_del = document.createElement("img");
    		    	img_del.setAttribute('style' , "left:"+(nTX+width-9)+"px;top:"+(nTY-9)+"px;position:absolute;width:18px;height:18px;z-index:"+zindex+";cursor:pointer;");
    				var delsrc = "/components/homewidget/images/del.png";
    				img_del.setAttribute('src',delsrc);
    				img_del.setAttribute('id',delid);
    				img_del.setAttribute('title',MB.LABEL.CANCELSIGNATURE);
    			}else{
    		        img_del=new Image();
    		        img_del.style = "left:"+(nTX+width-9)+"px;top:"+(nTY-9)+"px;position:absolute;width:18px;height:18px;z-index:"+zindex+";cursor:pointer;";
    		    	var delsrc = "/components/homewidget/images/del.png";
    		    	img_del.src=delsrc;
    		    	img_del.id=delid;
    		    	img_del.title=MB.LABEL.CANCELSIGNATURE;
    			}
    		    if(document.getElementById(delid)){
    		    	div.children[1].removeChild(document.getElementById(delid));
    		    }else{
    		    	var imgs_ = div.children[1].getElementsByTagName("img");//找寻所有img
    		    	var num1 = 0;
    				for(var j=0;j<imgs_.length;j++){
    		        	var id_ = imgs_[j].id;
    		        	if(id_.indexOf('_signid')!=-1){
    		        		num1++;
    		        	}else
    		        		continue;
    		        }
    				if(num1>0){
    					var imgid_ = imgs_[num1-1].id;
    					var next_ = document.getElementById(imgid_).nextSibling;
    					div.children[1].insertBefore(img_del,next_);
    				}
    		    }
    		    	
    		    img_del.onclick = function(e){
    				DelSignature(signaturename,PageID,GridNO,isdelneedpass);
    		    }
    		    img_del.onmouseout = function(e){
    		    	var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
    		    	if(document.getElementById(delid)){
    		    		div.children[1].removeChild(document.getElementById(delid));
    		        }
    		    }
    		//}, 500); 
    	}
    }
    img.onmouseout = function(e){
    	//clearTimeout(meizz);
    	var el = nn6 ? e.toElement : event.toElement;
    	if(nn6&&e.toElement==undefined)
    		el = e.relatedTarget;//火狐
    	var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
    	if(document.getElementById(delid)){
    		if(el!=undefined&&el.id!=delid){
    			div.children[1].removeChild(document.getElementById(delid));
    		}
        }
    }
    if(flag!='1'){
    	var menuid = "fld_"+PageID+"_"+GridNO+"_menu";
        addXml(PageID,GridNO,username,signatureid,zindex,markpath);
    	if(solveflag=='2'){
    		batchSignature(markpath);
    	}
    }
	if(Ext.getCmp(menuid)){
		Ext.getCmp(menuid).close();
	}
}
/**
 * 读取锁中数据
 * @param firstnum
 * @param lastnum
 * @param markpath_
 * @param img
 * @param div
 * @param moveflag
 * @param PageID
 * @param GridNO
 * @param signaturename
 * @param isdelneedpass
 * @param id
 * @param imgwidth
 * @param imgheight
 * @param zindex
 * @param username
 * @param signatureid
 * @param solveflag
 * @returns
 */
function readClockFile(firstnum,lastnum,markpath_,img,div,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,solveflag){
	var rtn = "";
	FileID = '000'+firstnum;
	ReadLength = 4096;
	FileOffset = 0;
	Handle = ArmHandle;
	ctrl.Arm_ReadFile(function(result, response){
		if(!result){
			Ext.showAlert("Arm_ReadFile error. " + response);
		}else{
			rtn = response;
			markpath_+=b.decode(b.decode(rtn));
			if(firstnum==lastnum-1){
				var src = 'data:image/png;base64,'+markpath_;
				img.setAttribute('src',src);
				img.id=id;
				addFunction(div,img,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,markpath_,solveflag);
				return;
			}
			FileID = '000'+(firstnum+1);
			readClockFile(firstnum+1,lastnum,markpath_,img,div,moveflag,PageID,GridNO,signaturename,isdelneedpass,id,imgwidth,imgheight,zindex,username,signatureid,solveflag);
		}
	})
}
/**
 * 撤销签章
 * @param signaturename
 * @param PageID
 * @param GridNO
 * @param isdelneedpass
 * @returns
 */
function DelSignature(signaturename,PageID,GridNO,isdelneedpass){
	var signaturediv = document.getElementById(signaturename);
	var signaturetds = signaturediv.getElementsByTagName("td");
	var signxml = templateCard_me.signXml;
	if(signxml.length>0){
		var num = 0;
		var tabdivid = "tabdiv_tab_"+PageID;
		signxml = replaceXml(signxml);
		var XMLDoc = loadXMLString(signxml);
		XMLDoc.async = false;
		var rootNode = XMLDoc.documentElement;
		var recordnode = rootNode.childNodes;
		var m = recordnode.length;
		for(var i=0;i<m;i++){
			if(recordnode[i].nodeType==1){
				var cid = recordnode[i].getAttribute("DocuemntID");
				if(cid!=""&&cid!="BJCA"&&cid==PageID+"_"+GridNO){
					var item = recordnode[i].childNodes;
					if(isdelneedpass!='false'){
						var win = Ext.widget('window',{   
							title:MB.LABEL.CANCELSIGNATURE,  
							height:250,
							width:500,
							layout:'fit',
							modal:true,           
							closeAction:'destroy',
							resizable:false,
							items: [{xtype:'container',width:490,items:[
								{xtype:'container',id:'passwordcon_del',layout:'hbox',items:[{
					    	   			xtype:'textfield',
					    	   			fieldLabel: MB.LABEL.SIGNATUREPASSWORD,
					    	   			margin:'80 0 0 60',
					        			id:'password_del',
					        			inputType:'password',
					        			labelAlign:'right',
					            		labelWidth:110,
					            		width:300,
					        			fieldStyle:'height:20px;',
					        			listeners:{
					        				blur:function(e,event,eOpts){
					        					var password = isdelneedpass;
					        					if(e.value!=''&&e.value!=password){
					        						Ext.getCmp('checkpassword_del').setText(MB.LABEL.SIGNATUREPASSWORDERROR);
					        					}else if(e.value==''){
					        						Ext.getCmp('checkpassword_del').setText(MB.LABEL.SIGNATUREPASSWORDERROR);
					        					}else{
					        						Ext.getCmp('checkpassword_del').setText('');
					        					}
					        				}
					        			}
				    	   			},{xtype:'label',margin:'80 0 0 5',id:'checkpassword_del',style:{color:'red'}}]
				    	   		}
							]}],
							buttonAlign:'center',
				    		buttons:[{text:MB.LABEL.SURE,handler: function() {
								var password = isdelneedpass;
								if(Ext.getCmp('password_del').getValue()!=''&&Ext.getCmp('password_del').getValue()!=password){
									Ext.getCmp('checkpassword_del').setText(MB.LABEL.SIGNATUREPASSWORDERROR);
									return;
								}else if(Ext.getCmp('password_del').getValue()==''){
									Ext.getCmp('checkpassword_del').setText(MB.LABEL.SIGNATUREPASSWORDERROR);
									return;
								}else{
									Ext.getCmp('checkpassword_del').setText('');
								}
								win.close();
								var div = document.getElementById(tabdivid);
								var id = "fld_"+PageID+"_"+GridNO+"_signid";
								var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
								var img = document.getElementById(id);
								var img_del = document.getElementById(delid);
								if(img)
									div.children[1].removeChild(img);
								if(img_del)
									div.children[1].removeChild(img_del);
								revokeSignature(PageID,GridNO);
				    			rootNode.removeChild(recordnode[i]);
				    			templateCard_me.signXml = XMLtoString(XMLDoc);
				    			//自动保存签章xml数据  暂时先这样
				    			templateTool_me.save('true','true');
				    		}},{
						    	text:MB.LABEL.CANCEL,
						    	handler: function() {
						    		win.close();
							    }}]
						});
						win.show();
					}else{
						var div = document.getElementById(tabdivid);
						var id = "fld_"+PageID+"_"+GridNO+"_signid";
						var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
						var img = document.getElementById(id);
						var img_del = document.getElementById(delid);
						if(img)
							div.children[1].removeChild(img);
						if(img_del)
							div.children[1].removeChild(img_del);
						revokeSignature(PageID,GridNO);
		    			rootNode.removeChild(recordnode[i]);
		    			templateCard_me.signXml = XMLtoString(XMLDoc);
		    			//自动保存签章xml数据  暂时先这样
		    			templateTool_me.save('true','true');
					}
					//m=m-1;
					break;
				}
			}
		}
	}
}
/**
 * 将签章数据从htmlsignature表中删除
 * @param PageID
 * @param GridNO
 * @returns
 */
function revokeSignature(PageID,GridNO){
	var map = new HashMap();
	map.put("tab_id",templateMain_me.templPropety.tab_id); 
	map.put("PageID",PageID);
	map.put("GridNO",GridNO);
	map.put("signxml",templateCard_me.signXml);
	Rpc({functionId:'MB00007008',async:false,success:function(form,action){
		var result = Ext.decode(form.responseText);
	}},map);
}
/**
 * 添加签章生成xml
 * @param PageID
 * @param GridNO
 * @param username
 * @param signatureid
 * @param zindex
 * @param markpath
 * @returns
 */
function addXml(PageID,GridNO,username,signatureid,zindex,markpath){
	var id = "fld_"+PageID+"_"+GridNO+"_signid";
	var signXml = templateCard_me.signXml;
	if(templateCard_me.signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "<record DocuemntID=\""+PageID+"_"+GridNO+"\">";
		signXml += " </record>";
		signXml += " </params>";
	}
	var pointx = document.getElementById(id).style.left;
	var pointy = document.getElementById(id).style.top;
	signXml = replaceXml(signXml);
	var XMLDoc = loadXMLString(signXml);  
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;
	try{
		if(rootNode){
			var recordnode = rootNode.childNodes;
			var ishave = false;
			var theno = 0;
			for(var i=0;i<recordnode.length;i++){
				if(recordnode[i].nodeType==1){
					if(recordnode[i].getAttribute("DocuemntID")==PageID+"_"+GridNO){
						ishave = true;
						theno = i;
						break;
					}
				}
			}
			var SignatureHtmlID = "";
			if(ishave){
				var itemlist = recordnode[i].childNodes;
				if(itemlist.length>0){
					recordnode[i].removeChild(itemlist[0]);
				}
				var newNode = XMLDoc.createElement("item");
				var time = new Date();
			    var lg = time.getTime();
			    SignatureHtmlID = lg;
				newNode.setAttribute('UserName',username);
				newNode.setAttribute('SignatureID',signatureid);
				newNode.setAttribute('SignatureHtmlID',SignatureHtmlID+"");
				newNode.setAttribute('PageID',""+PageID);
				newNode.setAttribute('GridNO',""+GridNO);
				newNode.setAttribute('node_id',""+templateCard_me.nodeId);
				newNode.setAttribute('pointx',""+pointx);
				newNode.setAttribute('pointy',""+pointy);
				newNode.setAttribute('zindex',""+zindex);
				recordnode[theno].appendChild(newNode);
			}else{
				var record = XMLDoc.createElement("record");
				record.setAttribute('DocuemntID',PageID+"_"+GridNO);
				var newNode = XMLDoc.createElement("item");
				var time = new Date();
			    var lg = time.getTime();
			    SignatureHtmlID = lg;
				newNode.setAttribute('UserName',username);
				newNode.setAttribute('SignatureID',signatureid);
				newNode.setAttribute('SignatureHtmlID',SignatureHtmlID+"");
				newNode.setAttribute('PageID',""+PageID);
				newNode.setAttribute('GridNO',""+GridNO);
				newNode.setAttribute('node_id',""+templateCard_me.nodeId);
				newNode.setAttribute('pointx',""+pointx);
				newNode.setAttribute('pointy',""+pointy);
				newNode.setAttribute('zindex',""+zindex);
				record.appendChild(newNode);
				rootNode.appendChild(record);
			}
			savetoHtmlSignature(SignatureHtmlID+"",PageID+"_"+GridNO,markpath,signatureid);
		}
		templateCard_me.signXml = XMLtoString(XMLDoc);
		//自动保存签章xml数据  暂时先这样
		templateTool_me.save('true','true');
	}
	catch(e){
		//Ext.showAlert(e.message);
	}
}
/**
 * 将数据保存到HtmlSignature表
 * @param signaturehtmlid
 * @param documentid
 * @param markpath
 * @returns
 */
function savetoHtmlSignature(signaturehtmlid,documentid,markpath,signatureid){
	var map = new HashMap();
	map.put('signaturehtmlid',signaturehtmlid);
	map.put('documentid',documentid);
	map.put('markpath',markpath);
	map.put('tabid',templateMain_me.templPropety.tab_id);
	map.put('signatureid',signatureid);
	Rpc({functionId:'MB00007007',async:false,success:function(form,action){},scope:this},map);
}
function replaceXml(signXml){
	signXml=replaceAll(signXml,"＜","<");
	signXml=replaceAll(signXml,"＞",">");
	signXml=replaceAll(signXml,"＇","'");
	signXml=replaceAll(signXml,"＂",'"');
	signXml=replaceAll(signXml,"&","");
	return signXml;
}
/**
 * 保存前修改xml
 * @returns
 */
function updateDocumentid(){
    var signstr ="";
    var signXml = templateCard_me.signXml;
	if(templateCard_me.signXml==''){
		signXml += "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
		signXml += "<params>";
		signXml += "</params>";
	}
	signXml = replaceXml(signXml);
	var XMLDoc = loadXMLString(signXml);  
	XMLDoc.async=false;
	var rootNode = XMLDoc.documentElement;
	var recordnode = rootNode.childNodes;
	for(var i=0;i<recordnode.length;i++){
		if(recordnode[i].nodeType==1){
			var DocuemntID = recordnode[i].getAttribute("DocuemntID");
			if(DocuemntID!='BJCA'){
				var id = "fld_"+DocuemntID+"_signid";
				var item = recordnode[i].childNodes;
				if(document.getElementById(id)){
					var  m = item.length;
					for(var j=0;j<m;j++){
						var pointx = document.getElementById(id).style.left;
						var pointy = document.getElementById(id).style.top;
						if(item[j].nodeType==1){
						    if((item[j].getAttribute("PageID")+"_"+item[j].getAttribute("GridNO"))==DocuemntID){
							     item[j].getAttributeNode("pointx").value=pointx;
							     item[j].getAttributeNode("pointy").value=pointy;
						    }
						}
					}
				}
			}
		}
	}
	templateCard_me.signXml = XMLtoString(XMLDoc);
}
/**
 * 签章生成（回显签章）
 * @param signxml
 * @param pageid
 * @returns
 */
function initSignatureXml(signxml,pageid){
	if(signxml.length>0){
		//得到签章相关用户得签章数据
		templateCard_me.userSignatureList+='';
		var map = new HashMap();
		map.put('flag','1');
		map.put('signxml',signxml);
		map.put('tabid',templateMain_me.templPropety.tab_id);
		Rpc({functionId:'MB00007001',async:false,success:function(form,action){
			var result = Ext.decode(form.responseText);
			templateCard_me.userSignatureList = getDecodeStr(result.userSignatureList);
		},scope:this},map);
		signxml = replaceXml(signxml);
		var XMLDoc = loadXMLString(signxml);
		XMLDoc.async = false;
		var rootNode = XMLDoc.documentElement;
		var recordnode = rootNode.childNodes;
		templateCard_me.notShowSignatureIDs+='';//记录无权限的签章
		templateCard_me.readOnlySignatureIDs+='';//记录只读权限的签章
		for(var i=0;i<recordnode.length;i++){
			if(recordnode[i].nodeType==1){
				var cid = recordnode[i].getAttribute("DocuemntID");
				if(cid!=""&&cid!="BJCA"){
					var item = recordnode[i].childNodes;
					var isShow = false;
					for(var j=0;j<item.length;j++){
						var isHaveRwPriv=true;
						var isReadOnly=false;
						if(item[j].nodeType==1){
							var pid = item[j].getAttribute("PageID");
							var gridno = item[j].getAttribute("GridNO");
							var username = item[j].getAttribute("UserName");
							var SignatureID = item[j].getAttribute("SignatureID");
							var pointx = item[j].getAttribute("pointx");
							var pointy = item[j].getAttribute("pointy");
							var zindex = item[j].getAttribute("zindex");
							var fields=templateCard_me.fieldSet.fields;
							for(var num=0;num<fields.length;num++){
								var field=fields[num];
								if(field.uniqueId==("fld_"+pid+"_"+gridno)){
									if(field.rwPriv==0)
										isHaveRwPriv=false;
									else if(field.rwPriv==1)
										isReadOnly=true;
									break;
								}
							}
							if(templateCard_me.signatureMap.get(pid+"_"+gridno)){}
							else
								templateCard_me.signatureMap.put(pid+"_"+gridno,username);
							if(pageid==pid){
								if(!isHaveRwPriv){
									templateCard_me.notShowSignatureIDs+=','+SignatureID;
								}
								if(isReadOnly||(templateCard_me.currentUser!=username&&isHaveRwPriv)){
									templateCard_me.readOnlySignatureIDs+=','+SignatureID;
								}
								if(!isShow){
									isShow = true;
									templateCard_me.showSignatureId += SignatureID+',';
								}
								var signaturename = "signature"+pid+"S"+gridno;
								ShowSignature(signaturename,pid,gridno,SignatureID,pointx,pointy,templateCard_me.userSignatureList,zindex);
							}
						}
					}
				}
			}
		}
	}
}
/**
 * 回显签章
 * @param signaturename
 * @param PageID
 * @param GridNO
 * @param SignatureID
 * @param pointx
 * @param pointy
 * @param userSignatureList
 * @param zindex
 * @returns
 */
function ShowSignature(signaturename,PageID,GridNO,SignatureID,pointx,pointy,userSignatureList,zindex){
	var div = document.getElementById(signaturename);
	if(div==null||div==undefined){
		return;
	}
	var tds = div.getElementsByTagName("td");
	templateCard_me.notShowSignatureIDs+=',';//记录无权限的签章
	templateCard_me.readOnlySignatureIDs+=',';//记录只读权限的签章
	var markpath = "";
	var password = "";
	var imgwidth = "";
	var imgheight = "";
	var savetodiskflag = "";
	var username = "";
	var imgUrlarr = userSignatureList.split(",");
	for(var i=0;i<imgUrlarr.length;i++){
		var imgurl = imgUrlarr[i];
		var imgarr = imgurl.split('`');
		var signatureid = imgarr[0];
		var password_ = imgarr[1];
		var markpath_ = imgarr[2];
		var username_ = imgarr[4];
		var imgwidth_ = imgarr[5];
		var imgheight_ = imgarr[6];
		if(signatureid==SignatureID){
			markpath = markpath_;
			password = password_;
			imgwidth = imgwidth_;
			imgheight = imgheight_;
			username = username_;
			break;
		}
	}
    if(templateCard_me.notShowSignatureIDs.indexOf(SignatureID+",")>-1){//无权限的签章不显示
    	tds[0].title = "";
    }
	else if(templateCard_me.readOnlySignatureIDs.indexOf(SignatureID+",")>-1){//只读权限的签章设置不允许移动。
		if(markpath!=""){
			createImg(signaturename,PageID,GridNO,markpath,pointx,pointy,false,'false',imgwidth,imgheight,zindex,'1',username,SignatureID,'1'); //生成图片
			tds[0].title = "";
		}
    }else{
    	var isdelneedpass = 'false';
    	if(password!=''){
    		isdelneedpass = password;
    	}
    	if(markpath!=""){
    		createImg(signaturename,PageID,GridNO,markpath,pointx,pointy,true,isdelneedpass,imgwidth,imgheight,zindex,'1',username,SignatureID,'1');
    		tds[0].title = "";
    	}
    }
}
/**
 * 隐藏签章
 * @returns
 */
function HideSignature(){
	var signxml = templateCard_me.signXml;
	if(signxml.length>0){
		signxml = replaceXml(signxml);
		var XMLDoc = loadXMLString(signxml);
		XMLDoc.async = false;
		var rootNode = XMLDoc.documentElement;
		var recordnode = rootNode.childNodes;
		for(var i=0;i<recordnode.length;i++){
			if(recordnode[i].nodeType==1){
				var cid = recordnode[i].getAttribute("DocuemntID");
				if(cid!=""&&cid!="BJCA"){
					var item = recordnode[i].childNodes;
					for(var j=0;j<item.length;j++){
						if(item[j].nodeType==1){
							var PageID = item[j].getAttribute("PageID");
							var GridNO = item[j].getAttribute("GridNO");
							var id = "fld_"+PageID+"_"+GridNO+"_signid";
							var delid = "fld_"+PageID+"_"+GridNO+"_signid_del";
							var tabdivid = "tabdiv_tab_"+PageID;
							var div = document.getElementById(tabdivid);
							var img = document.getElementById(id);
							var img_del = document.getElementById(delid);
							if(img)
								div.children[1].removeChild(img);
							if(img_del)
								div.children[1].removeChild(img_del);
						}
					}
				}
			}
		}
	}
}