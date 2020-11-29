var BusiParam = new Object();
BusiParam.ChannelPrivMap = new HashMap();

/**
 * 
 */
BusiParam.indexAd = 0;
BusiParam.addEvent = function(btn,id){
    if("add"+id+"A2"==btn.id){//选角色
        Ext.require('EHR.rolepicker.RolePicker', function(){          
            Ext.create('EHR.rolepicker.RolePicker',
                    {callBackFunc:function(cm){
                      var elem1 = Ext.getDom("add"+id);
                      var elem2 = Ext.getDom("add"+id+"A1");
                      BusiParam.character = new Array();//角色
                      
                      for(var int2=0;int2<cm.length;int2++){
                          var c = cm[int2];
                          c.photo = "/images/role.png";
                          c.name = c.role_name;
                          role_id = c.role_id_e;
                          var name = c.name;
                          mapRow =BusiParam.ChannelPrivMap.get(id);
                          if( mapRow && mapRow.get("role_id")){
                          var role_id1 = mapRow.get("role_id");
                          roleId = role_id1.split(",");
                          if (roleId.contains(role_id)) {
                              continue;
                            }
                          }
                          
                          BusiParam.character.push(role_id);
                          BusiParam.indexAd++;
                          if(c.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
                              c.name = BusiParam.cut_str(c.name,3);
                          }
                          
                          var elem3 = document.createElement("div");
                          elem3.name="addDiv";
                          elem3.className="hj-nmd-dl2";
                          var divid ="divs"+BusiParam.indexAd;
                          elem3.innerHTML='<dl onmouseover="BusiParam.toRemove(\''+divid+'\')" onmouseleave="BusiParam.toChan(\''+divid+'\')"><dt title="'+name+'"><img src="'+c.photo+'" /><img id="'+divid+'" class="deletePic" onclick="BusiParam.toDelet(this,\''+id+'\',\''+role_id+'\',2)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'+c.name+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+role_id+'"/>';
                          elem1.insertBefore(elem3,elem2);
                      }
                      
                      var roleId;
                      roleId = BusiParam.character.join(",");
                      
                      if(BusiParam.ChannelPrivMap.get(id)){
                          mapRow =BusiParam.ChannelPrivMap.get(id);
                          if(mapRow.get("role_id")){
                              var role_id =  mapRow.get("role_id");
                              role_id =role_id + ","+roleId;
                              mapRow.put("role_id", role_id);
                          }else{
                              mapRow.put("role_id", roleId);
                          }
                          
                      }else{
                          var mapRow = new HashMap();
                          mapRow.put("role_id", roleId);
                      }
                      BusiParam.ChannelPrivMap.put(id, mapRow);
                    },
                    multiple:true});
        },this);
    }else if("add"+id+"A3"==btn.id){//选用户
        var picker = new PersonPicker({
            multiple: true,
            isSelfUser:false,//是否选择自助用户
            selfUserIsExceptMe:false,//业务用户时是否排除自己默认排除
            isMiddle:true,//是否居中显示
            isPrivExpression:false,//是否启用人员范围（含高级条件）
            text:'添加用户',
            deprecate: BusiParam.hadInfoType2,
            callback: function (cm) {
                var elem1 = Ext.getDom("add"+id);
                var elem2 = Ext.getDom("add"+id+"A1");
                
                BusiParam.user = new Array();
                for(var int2=0;int2<cm.length;int2++){
                    var c = cm[int2];
                    BusiParam.indexAd++;
                    mapRow =BusiParam.ChannelPrivMap.get(id);
                    if(mapRow && mapRow.get("user_name")){
                    var user_name = mapRow.get("user_name");
                    userName = user_name.split(",");
                    if (userName.contains(c.id)) {
                         continue;
                      }
                    }
                    
                    BusiParam.user.push(c.id);
                    var name =c.name;
                    if(c.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
                        name = BusiParam.cut_str(c.name,3);
                    }
                    
                    var elem3 = document.createElement("div");
                    elem3.name="addDiv";
                    elem3.className="hj-nmd-dl";
                    var divid ="divs"+BusiParam.indexAd;
                    elem3.innerHTML='<dl onmouseover="BusiParam.toRemove(\''+divid+'\')" onmouseleave="BusiParam.toChan(\''+divid+'\')"><dt title="'+c.name+'"><img class="img-circle" src="'+c.photo+'" /><img id="'+divid+'" class="deletePic" onclick="BusiParam.toDelet(this,\''+id+'\',\''+c.name+'\',3)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'+name+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+c.id+'"/>';
                    elem1.insertBefore(elem3,elem2);
                }
                var userb;
                userb = BusiParam.user.join(",");
                if(BusiParam.ChannelPrivMap.get(id)){
                    mapRow =BusiParam.ChannelPrivMap.get(id);
                    if( mapRow.get("user_name")){
                        var user_name =  mapRow.get("user_name");
                        user_name =user_name + ","+userb;
                        mapRow.put("user_name", user_name);
                    }else{
                        mapRow.put("user_name", userb);
                    }
                    
                }else{
                    var mapRow = new HashMap();
                    mapRow.put("user_name", userb);
                }
                BusiParam.ChannelPrivMap.put(id, mapRow);
            }
        
        }, btn);
        picker.open();
        
    }else if("add"+id+"A1"==btn.id){//选人
        var picker = new PersonPicker({
            multiple: true,
      //      selfUserIsExceptMe:false,
            deprecate: BusiParam.hadInfoType3,
            isPrivExpression:false,
            callback: function (cm) {
                    var elem1 = Ext.getDom("add"+id);
                    var elem2 = Ext.getDom("add"+id+"A1");
                    BusiParam.member = new Array();//成员
                    for(var int2=0;int2<cm.length;int2++){
                        var c = cm[int2];
                        var name = c.name;
                        BusiParam.indexAd++;
                        mapRow =BusiParam.ChannelPrivMap.get(id);
                        if(mapRow && mapRow.get("emp_id")){
                        var emp_id = mapRow.get("emp_id");
                        empId = emp_id.split(",");
                        if (empId.contains(c.id)) {
                            continue;
                          }
                        }
                        if(c.name.replace(/[\u4E00-\u9FA5]/g,'aa').length>6){
                            c.name = BusiParam.cut_str(c.name,3);
                        }
                        
                        BusiParam.member.push(c.id);
                        var elem3 = document.createElement("div");
                        elem3.name="addDiv";
                        elem3.className="hj-nmd-dl";
                        var divid ="divs"+BusiParam.indexAd;
                        elem3.innerHTML='<dl onmouseover="BusiParam.toRemove(\''+divid+'\')" onmouseleave="BusiParam.toChan(\''+divid+'\')"><dt title="'+name+'"><img class="img-circle" src="'+c.photo+'" /><img id="'+divid+'" class="deletePic" onclick="BusiParam.toDelet(this,\''+id+'\',\''+c.id+'\',1)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>'+c.name+'</dd></dl><input name="ponsMemberId" type="hidden" value="'+c.id+'"/>';
                        elem1.insertBefore(elem3,elem2);
                    }
                    
                    var members;
                    members = BusiParam.member.join(",");
                    if(BusiParam.ChannelPrivMap.get(id)){
                        var mapRow = BusiParam.ChannelPrivMap.get(id)
                        mapRow =BusiParam.ChannelPrivMap.get(id);
                        if( mapRow.get("emp_id")){
                            var emp_id =  mapRow.get("emp_id");
                            emp_id =emp_id + ","+members;
                            mapRow.put("emp_id", emp_id);
                            
                        }else{
                            mapRow.put("emp_id", members);
                        }
                      
                    }else {
                        var mapRow = new HashMap();
                        mapRow.put("emp_id", members);
                    }
                   
                    BusiParam.ChannelPrivMap.put(id, mapRow);
            }
        }, btn);
        picker.open();
    }
};


BusiParam.indexAd = 0;
BusiParam.show = function(ChannelMap) {
    for (var key in ChannelMap) {
        var mapRow = new HashMap();
        if (ChannelMap[key]["emp_id"]) {
            mapRow.put("emp_id", ChannelMap[key]["emp_id"]);
            var strs = ChannelMap[key]["emp_id"].split(","); 
            var strs2 = ChannelMap[key]["a0101_name"].split(",");
            var strs3 = ChannelMap[key]["photos_id"].split(","); 
            var elem1 = Ext.getDom("add" + key);
            var elem2 = Ext.getDom("add" + key + "A1");
            for (i = 0; i < strs.length; i++) {
                var id = key;
                var name = strs2[i];
                if (strs2[i].replace(/[\u4E00-\u9FA5]/g, 'aa').length > 6) {
                    strs2[i] = BusiParam.cut_str(strs2[i], 3);
                }

                photo = strs3[i];
                var elem3 = document.createElement("div");
                elem3.name = "addDiv";
                elem3.className = "hj-nmd-dl";
                BusiParam.indexAd++;
                var divid = "divs" + BusiParam.indexAd;
                elem3.innerHTML = '<dl onmouseover="BusiParam.toRemove(\'' + divid + '\')" onmouseleave="BusiParam.toChan(\'' + divid + '\')"><dt title="' + name + '"><img class="img-circle" src="' + photo + '" /><img id="' + divid + '" class="deletePic" onclick="BusiParam.toDelet(this,\'' + key + '\',\'' + strs[i] + '\',1)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>' + strs2[i] + '</dd></dl><input name="ponsMemberId" type="hidden" value="' + strs[i] + '"/>';
                if (elem2 != null) {
                    elem1.insertBefore(elem3, elem2);
                } else if (elem1 != null) {
                    elem1.appendChild(elem3);
                } 
            }
        }

        if (ChannelMap[key]["user_name"]) {
            mapRow.put("user_name", ChannelMap[key]["user_name"]);
            var strs = ChannelMap[key]["user_name"].split(","); 
            var strNames = ChannelMap[key]["full_name"].split(",");
            var elem1 = Ext.getDom("add" + key);
            var elem2 = Ext.getDom("add" + key + "A1");
            for (i = 0; i < strs.length; i++) {
                var name = strNames[i];
                var id = key;
                if (strNames[i].replace(/[\u4E00-\u9FA5]/g, 'aa').length > 6) {
                    name = BusiParam.cut_str(strNames[i], 3);
                }

                photo = "/components/personPicker/image/male.png";
                var elem3 = document.createElement("div");
                elem3.name = "addDiv";
                elem3.className = "hj-nmd-dl";
                BusiParam.indexAd++;
                var divid = "divs" + BusiParam.indexAd;
                elem3.innerHTML = '<dl onmouseover="BusiParam.toRemove(\'' + divid + '\')" onmouseleave="BusiParam.toChan(\'' + divid + '\')"><dt title="' + name + '"><img class="img-circle" src="' + photo + '" /><img id="' + divid + '" class="deletePic" onclick="BusiParam.toDelet(this,\'' + key + '\',\'' + strs[i] + '\',3)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>' + name + '</dd></dl><input name="ponsMemberId" type="hidden" value="' + strs[i] + '"/>';
                if (elem2 != null) {
                    elem1.insertBefore(elem3, elem2);
                } else if (elem1 != null) {
                    elem1.appendChild(elem3);
                }
            }
        }

        if (ChannelMap[key]["role_id"]) {
            mapRow.put("role_id", ChannelMap[key]["role_id"]);
            var strs = ChannelMap[key]["role_id"].split(","); 
            var strs2 = ChannelMap[key]["role_name"].split(","); 
            var elem1 = Ext.getDom("add" + key);
            var elem2 = Ext.getDom("add" + key + "A1");
            for (i = 0; i < strs.length; i++) {
                var id = key;
                var name = strs2[i];
                if (strs2[i].replace(/[\u4E00-\u9FA5]/g, 'aa').length > 6) {
                    strs2[i] = BusiParam.cut_str(strs2[i], 3);
                }

                photo = "/images/role.png";
                var elem3 = document.createElement("div");
                elem3.name = "addDiv";
                elem3.className = "hj-nmd-dl2";
                BusiParam.indexAd++;
                var divid = "divs" + BusiParam.indexAd;
                elem3.innerHTML = '<dl onmouseover="BusiParam.toRemove(\'' + divid + '\')" onmouseleave="BusiParam.toChan(\'' + divid + '\')"><dt title="' + name + '"><img src="' + photo + '" /><img id="' + divid + '" class="deletePic" onclick="BusiParam.toDelet(this,\'' + key + '\',\'' + strs[i] + '\',2)" class="img-middle" style="width: 20px;display: none; height: 20px;" src="/workplan/image/remove.png" complete="complete"></img></dt><dd>' + strs2[i] + '</dd></dl><input name="ponsMemberId" type="hidden" value="' + strs[i] + '"/>';
                if (elem2 != null) {
                    elem1.insertBefore(elem3, elem2);
                } else if (elem1 != null) {
                    elem1.appendChild(elem3);
                }
            }
        }
        BusiParam.ChannelPrivMap.put(key, mapRow);
    }

}


BusiParam.toDelet=function(elem,id,deletValue,attributes){
    Ext.Msg.confirm(PROMPT_INFORMATION,DETERMINE_DELETE_MEMBER,function(btn){
        if(btn=="yes"){ 
            var addtdelem = Ext.getDom("add"+id);
            var b =elem.parentNode.parentNode.parentNode;
                var arrNode = b.childNodes;
                var value ="";
                mapRow =BusiParam.ChannelPrivMap.get(id);
                if(attributes == 1){
                    var emp_id = mapRow.get("emp_id");
                    empId = emp_id.split(",");
                    empId.remove(deletValue);
                    mapRow.put("emp_id", empId.join(","));
                }else if(attributes == 2){
                    var role_id = mapRow.get("role_id");
                    roleId = role_id.split(",");
                    roleId.remove(deletValue);
                    mapRow.put("role_id", roleId.join(","));
                }else if(attributes == 3){
                    var user_name = mapRow.get("user_name");
                    userName = user_name.split(",");
                    userName.remove(deletValue);
                    mapRow.put("user_name", userName.join(","));
                }
                BusiParam.ChannelPrivMap.put(id, mapRow);
                for ( var int = 0; int < arrNode.length; int++) {
                    if(arrNode[int].tagName=="INPUT"){
                        value= arrNode[int].value;
                        break;
                    }
                }
            addtdelem.removeChild(b);
        }
    });
};

BusiParam.toRemove=function(par){
	var a =Ext.getDom(par);
	a.style.display="";
};

BusiParam.toChan=function(par) {
	var a =Ext.getDom(par);
	a.style.display="none";
};


//截取6个字节长度的字符串
BusiParam.cut_str = function (str, len){
    var char_length = 0;
    for (var i = 0; i < str.length; i++){
        var son_str = str.charAt(i);
        encodeURI(son_str).length > 2 ? char_length += 1 : char_length += 0.5;
        if (char_length >= len){
            var sub_len = char_length == len ? i+1 : i;
            return str.substr(0, sub_len);
        }
    }
};