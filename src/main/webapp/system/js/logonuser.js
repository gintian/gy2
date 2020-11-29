  
 
  function add_group_ok(outparamters)
  {
     var currnode=Global.selectedItem;
     /*不允许在超级用户组下创建用户*/
     alert(currnode.uid);
     if(currnode.uid=="超级用户组")
        currnode=currnode.parent;
     var groupid=outparamters.getValue("groupid");
     var groupname=outparamters.getValue("groupname");
     var ungroupname=escape(groupname);
     if(currnode.load)
     {
	 	var tmp = new xtreeItem(groupname,groupname,"/system/security/assignpriv.do?b_query=link&a_flag=0&a_tab=funcpriv&a_roleid="+ungroupname,"mil_body",groupname,"/images/groups.gif","/system/logonuser/search_user_servlet?level0=1&groupid="+groupid+"&username="+ungroupname);
	 	currnode.add(tmp);
     }
     else
     	currnode.expand();
  }
  
  function add_group()
  {
     var currname=1;
     var title=prompt('<bean:message key="column.name"/>',"");
     if(title==null)
     	return;
     var currnode=Global.selectedItem;
     currname=currnode.text;
     var hashvo=new ParameterSet();
     hashvo.setValue("groupname",title);
     hashvo.setValue("currname",currname);	        
   　 var request=new Request({asynchronous:false,onSuccess:add_group_ok,functionId:'1010010036'},hashvo);        
  }
