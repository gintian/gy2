package com.hjsj.hrms.businessobject.general.template.tasklist;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.general.template.workflow.SendMessageBo;
import com.hjsj.hrms.businessobject.general.template.workflow.WF_Node;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class TaskListRoleActorInfoBo {
    private Connection conn=null; 
    private UserView userView;
    public TaskListRoleActorInfoBo(Connection con,UserView _userview)
    {
        this.conn=con; 
        this.userView=_userview;
    }
    

    
    /**   
     * @Title: getRoleActorHtml   
     * @Description:获取前台显示用的html   
     * @param @param tab_id
     * @param @param task_id
     * @param @return 
     * @return HashMap 
     * @author:wangrd   
     * @throws   
    */
    public HashMap getRoleActorHtml(String tab_id,String task_id)
    {
        HashMap returnMap=new HashMap();
        String approvePeople="";
        String approveContent="";
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            String sql="select * from t_wf_task where task_id = ?";
            ArrayList paramList = new ArrayList();
            paramList.add(Integer.valueOf(task_id));
            RowSet rset =dao.search(sql, paramList);
            if (rset.next()){
                String ins_id=rset.getString("ins_id");
                String role_id=rset.getString("actorid");
                String node_id=rset.getString("node_id");
                String actor_type=rset.getString("actor_type");
                LazyDynaBean paramBean = new LazyDynaBean();
                paramBean.set("tab_id", tab_id);
                paramBean.set("task_id", task_id);
                paramBean.set("ins_id", ins_id);
                paramBean.set("role_id", role_id);
                paramBean.set("node_id", node_id);
                paramBean.set("actor_type", actor_type);
                ArrayList list = new ArrayList();
                if("5".equals(actor_type)) {//本人
                	list = this.getListWithMyself(tab_id,task_id);
                }else {
	                //审批人
	                //ArrayList list = getRoleActorList(paramBean);
	                //以下参考代码WF_NODE
	                SendMessageBo sendMessageBo=new SendMessageBo(this.conn,this.userView);
	                sendMessageBo.setTask_id(task_id);
	                sendMessageBo.setIns_id(ins_id);
	                //sendMessageBo.setSp_flag("2");
	                TemplateTableBo tableBo=new TemplateTableBo(conn,Integer.parseInt(tab_id),this.userView);
	                WF_Node wf_Node=new WF_Node(tableBo,this.conn);
	                ArrayList alist=sendMessageBo.findUserListByRoleId(role_id,
	                        sendMessageBo.getTemplate_emailAddress(),
	                        sendMessageBo.getTemplate__set()); 
	                HashMap map = new HashMap();
	                ArrayList userlist =wf_Node.getUserflag(""+node_id,""+task_id,map,"");
	                for(int i=0;i<alist.size();i++)
	                {
	                    LazyDynaBean abean=(LazyDynaBean)alist.get(i); 
	                    boolean flag =false;//没有符合条件的单据 则都不显示 wangrd 2015-05-15
	                    if(map.isEmpty()&&userlist.size()>0){
	                         flag = getUserflag2(""+node_id,""+task_id,abean,userlist);
	                    }
	                    else if (!map.isEmpty()) {
	                    	flag=true;
	                    }
	                    if(!flag) {
                            continue;
                        }
	                  
	                    list.add(abean);
	                }
	                //处理特殊角色 直管领导等 不需要了 屏蔽
	                // setObjectidBySpecialRole(tableBo, list,paramBean);
                }
                //转换需输出信息
                list =transferRoleActorList(list, paramBean);
            
               //往前台输出html信息
                StringBuffer tableHtml= new StringBuffer();
                for (int i=0;i<list.size();i++){
                   LazyDynaBean bean =  (LazyDynaBean)list.get(i); 
                   String userDesc= (String)bean.get("userDesc");
                   String isDealing= (String)bean.get("isDealing");
                   if (i==10&&!"5".equals(actor_type)){
                       tableHtml.append("<tr height='25'>");
                       tableHtml.append("<td align='left' colspan='2' >等共计"
                               +String.valueOf(list.size())+"人</td>");
                       tableHtml.append("</tr>");
                       break;
                   } 
                   if (i % 2==0){//每行显示两人
                       tableHtml.append("<tr height='25'>"); 
                       tableHtml.append("<td align='left' valign='top'  >");
                   }
                   else {
                       tableHtml.append("<td align='left' valign='top' style='padding-left:10px' >");
                   }
                   
                   if ("0".equals(isDealing)){//正在处理
                       tableHtml.append("<IMG  align='left'  src='/images/edit.gif'>");
                   }
                   else if ("1".equals(isDealing)){//已处理
                       tableHtml.append("<IMG  align='left'  src='/images/cc1.gif'>"); 
                   }
                   tableHtml.append(userDesc);
                   tableHtml.append("&nbsp;");
                   tableHtml.append("</td> ");
                   
                   if (i % 2==1){//每行显示两人
                       if (i>0){
                           tableHtml.append("</tr>");   
                       }
                   }
                }              
                approvePeople="<table border='0'  cellspacing='0' style='font-size:12px;' cellpadding='0' width='100%' >";
                approvePeople=approvePeople+tableHtml.toString();
                approvePeople=approvePeople+"</table>";
              //审批意见
                approveContent="<table border='0'  cellspacing='0'    cellpadding='0' width='100%' >";
                approveContent=approveContent+"<tr height='20'> ";
                approveContent=approveContent+" <td align='left'  >";
                approveContent= approveContent+Sql_switcher.readMemo(rset,"content").replace("\n", "<br>").replace(" ", "&nbsp;");
                approveContent=approveContent+" </td>";
                approveContent=approveContent+" </tr>";
                approveContent=approveContent+" </table> ";                
                       
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        returnMap.put("approvePeople", approvePeople);
        returnMap.put("approveContent", approveContent);
        return returnMap;
        
    }
    
    
    private ArrayList getListWithMyself(String tab_id, String task_id) {
    	RowSet rowSet = null;
    	ArrayList list = new ArrayList();
    	try {
    		ContentDAO dao=new ContentDAO(this.conn);
			ArrayList valueList = new ArrayList();
			StringBuffer strsql = new StringBuffer("");
			strsql .append("select templet.BasePre"+Sql_switcher.concat()+"templet.a0100 a0100,twb.state,1 status,templet.e0122_1  from ");
			strsql.append("t_wf_task_objlink TWB,templet_"+tab_id+" templet "); 
			strsql.append(" where TWB.seqnum=templet.seqnum and TWB.ins_id=templet.ins_id ");
			strsql.append(" and  Twb.task_id=?  ");
			valueList.add(new Integer(task_id));
	
			rowSet = dao.search(strsql.toString(),valueList);
			while(rowSet.next()){
				LazyDynaBean lady = new LazyDynaBean();
				String a0100 = rowSet.getString("a0100");
				String state = rowSet.getString("state");
				String status = rowSet.getString("status");
				String e0122_1 = rowSet.getString("e0122_1");
				lady.set("a0100", a0100);
				lady.set("state", state);
				lady.set("status", status);
				lady.set("e0122", e0122_1);
				list.add(lady);
			}
    	}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return list;
	}



	public boolean getUserflag2(String node_id,String task_id,LazyDynaBean a_bean,ArrayList list){
        boolean flag = false;
        String scope_field="";
        String containUnderOrg="0";
        RowSet rowSet2=null;
        ContentDAO dao=new ContentDAO(this.conn);
        try {
            LazyDynaBean userbean = (LazyDynaBean)list.get(0);
            scope_field =(String)userbean.get("scope_field");
            containUnderOrg=(String)userbean.get("containUnderOrg");
            if(scope_field.length()>0)
            {
                    String username =""+a_bean.get("username");
                    String password =""+a_bean.get("password");
                    String operOrg=null;
                    //暂时不启用
                    //operOrg =(String)a_bean.get("templateMangerPirv");
                	String userOrgId ="";//单位
					String userDeptId ="";//部门
					String A0100="";
                	//if (operOrg==null){
                		UserView userView = new UserView(username,password, this.conn);
                        userView.canLogin(false);
                        operOrg = userView.getUnitIdByBusi("8"); // 操作单位 5: 绩效管理  6：培训管理 7：招聘管理  8:业务模板
						userDeptId= userView.getUserDeptId();
						userOrgId= userView.getUserOrgId();
						A0100= userView.getA0100();
					/*}
					else {	
						userOrgId= (String)a_bean.get("b0110");
						if (userOrgId==null) userOrgId="";
						userDeptId= (String)a_bean.get("e0122");
						if (userDeptId==null) userDeptId="";
						A0100= (String)a_bean.get("a0100");
						
					}*/
					
				    ArrayList paramlist = new ArrayList();
				    String role_property="";//角色特征
				    String actor_id = "";
                	StringBuffer sb = new StringBuffer("select t1.role_property,t.actorid from t_wf_task t,t_sys_role t1 where t1.role_id=t.actorid and t.task_id=?");
                	paramlist.add(task_id);
                	rowSet2 = dao.search(sb.toString(), paramlist);
                    if(rowSet2.next()){
                    	role_property = rowSet2.getString("role_property");
                    	actor_id = rowSet2.getString("actorid");
                    }
                    if (actor_id!=null && actor_id.length()>0){
	                    if ("1".equals(role_property)){//部门领导
							String e0122=userView.getUserDeptId();
							if (e0122!=null &&e0122.length()>0){
								//operOrg="UN"+e0122;//不知道为什么要写成UN
								operOrg="UM"+e0122;//改成UM，应该是对的 20170930
							}
							else {
								operOrg="";
							}
						}
						else if ("6".equals(role_property)){//单位领导
							String b0110=userView.getUserOrgId();
							if (b0110!=null &&b0110.length()>0){
								operOrg="UN"+b0110;
							}
							else {
								operOrg="";
							}
						}
                    }
					
                     for(int j=0;j<list.size();j++){
                            userbean = (LazyDynaBean)list.get(j);
                            String user = ""+userbean.get("username");
                            String value=""+userbean.get("scope_fieldvalue");
                            {
                             
                                if("UN`".equalsIgnoreCase(operOrg))
                                {
                                    return true;
                                }
                                String codesetid="";  
                                if(scope_field.toUpperCase().indexOf("SUBMIT")!=-1)
                                {
                                    String field_value=""+userbean.get("field_value");
                                    if(field_value.length()>0)
                                    {
                                        if(scope_field.toUpperCase().indexOf("E0122")!=-1) {
                                            codesetid="UM";
                                        } else if(scope_field.toUpperCase().indexOf("B0110")!=-1) {
                                            codesetid="UN";
                                        }
                                    }
                                    else {
                                        return false;
                                    }
                                }
                                else if ("parentid_2".equals(scope_field)|| "parentid_1".equals(scope_field)){//岗位 与单位 取得上级组织机构的代码类
                                	CodeItem codeItem = AdminCode.getCode("UN", value);
                                	codesetid="UN";
                                	if (codeItem==null){
                                		codesetid="UM";
                                	}
                                }
                                else
                                {
                                    String[] temps=scope_field.split("_");
                                    String itemid=temps[0].toLowerCase();  
                                    FieldItem _item=DataDictionary.getFieldItem(itemid);
                                    codesetid=_item.getCodesetid();
                                }
                                
                                if(operOrg!=null && operOrg.length() > 3)
                                {
                                    boolean tempflag = false;
                                    String[] temp = operOrg.split("`");
                                    for (int i = 0; i < temp.length; i++) {
                        
                                        if("1".equals(containUnderOrg)) //包含下级机构
                                        {
                                            tempflag= true;
                                            if(value.toLowerCase().startsWith(temp[i].substring(2).toLowerCase())){ 
                                                return true;
                                            }
                                            
                                        }
                                        else
                                        {
                                            if ("UN".equalsIgnoreCase(codesetid)&& "UN".equalsIgnoreCase(temp[i].substring(0, 2))){
                                                tempflag= true;
                                                if(value.toLowerCase().equals(temp[i].substring(2).toLowerCase())){
                                                    return true;
                                                    
                                                }
                                            }
                                                
                                            else if ("UM".equalsIgnoreCase(codesetid)&& "UM".equalsIgnoreCase(temp[i].substring(0, 2))){
                                                tempflag= true;
                                                if(value.toLowerCase().startsWith(temp[i].substring(2).toLowerCase())){ 
                                                    return true;
                                                }
                                            }
                                        }
                                        
                                    }
                                    
                                    if(!tempflag)
                                    {
                                        if("UN".equalsIgnoreCase(codesetid))
                                        {
                                            if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                            {
                                                String orgid=userOrgId.toLowerCase();
                                                if(value.toLowerCase().equals(orgid)||("1".equals(containUnderOrg)&&value.toLowerCase().startsWith(orgid))){
                                                    return true;
                                                }
                                            }
                                        }
                                        else if ("UM".equalsIgnoreCase(codesetid))
                                        {
                                            if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                            {
                                                String orgid=userDeptId.toLowerCase();
                                                if(value.toLowerCase().startsWith(orgid)){  
                                                    return true;
                                                }
                                            }
                                    }
                                    
                                }
                            }
                            else
                            {
                                    if("UN".equalsIgnoreCase(codesetid))
                                    {
                                        if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                        {
                                            String orgid=userOrgId.toLowerCase();
                                            if(value.toLowerCase().equals(orgid)||("1".equals(containUnderOrg)&&value.toLowerCase().startsWith(orgid))){
                                                return true;
                                            }
                                        }
                                    }
                                    else if ("UM".equalsIgnoreCase(codesetid))
                                    {
                                        if(A0100!=null&&A0100.trim().length()>0) //自助用户没定义管理范围，按所在单位、部门控制  2014-04-01 dengcan
                                        {
                                            if(value.toLowerCase().startsWith(userDeptId.toLowerCase())){ 
                                                return true;
                                            }
                                        }
                                }
                            
                            }
                    
                            }   
                        }
            }else{
                return true;
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rowSet2);
        }
        return false;
    }
    /**   
     * @Title: getUsrGroupName   
     * @Description:获取用户组名    
     * @param @param groupId
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getUsrGroupName(String username) {
        String a0101="";
        if (username==null){
            return "";
        }
        RowSet rset=null;
        ContentDAO dao=new ContentDAO(this.conn);
        String strsql="select groupname from UserGroup where groupid in "
            +"(select groupid from operuser where username='"+username+"')";
        try{
            rset=dao.search(strsql);
            if (rset.next()){
                a0101= rset.getString("groupname");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
           PubFunc.closeDbObj(rset);
        }
        return a0101;
    } 
    
    /**   
     * @Title: getUsrFullName   
     * @Description:获取业务用户的全称    
     * @param @param username
     * @param @return 
     * @return String 
     * @author:wangrd   
     * @throws   
    */
    public String getUsrFullName(String username) {
        if (username==null){
            return "";
        }
        String a0101=username;
        RowSet rset=null;
        ContentDAO dao=new ContentDAO(this.conn);
        try{
            rset=dao.search("select a0100,nbase from operuser where username='"+username+"'");
            if(rset.next())
            {
                String _a0100=rset.getString("a0100");
                String _nbase=rset.getString("nbase");
                if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
                {
                    rset=dao.search("select a0101 from "+_nbase+"a01 where a0100='"+_a0100+"' ");
                    if(rset.next())
                    {
                        a0101=rset.getString(1);
                    }
                }
                
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            PubFunc.closeDbObj(rset);
        }
        return a0101;
    } 
    
    
    /**   
     * @Title: getRoleActorList   
     * @Description: 将源信息转换为需要输出的信息   
     * @param @param slist
     * @param @return 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    public ArrayList transferRoleActorList(ArrayList sList,LazyDynaBean paramBean)
    {
        ArrayList list=new ArrayList();
        RowSet rowSet=null;
        try
        {
            ArrayList unDealList=new ArrayList();
            ContentDAO dao=new ContentDAO(this.conn);
            String tab_id=(String)paramBean.get("tab_id");
            String task_id=(String)paramBean.get("task_id");
            String actor_type=(String)paramBean.get("actor_type");
            HashMap dealingUserMap = new HashMap();
            if(!"5".equals(actor_type)) {
                dealingUserMap =getIsDealingTaskUser(tab_id,task_id);
            }
            for (int i=0;i<sList.size();i++){
                LazyDynaBean sBean = (LazyDynaBean)sList.get(i);
                String username=(String)sBean.get("username");
                String userfullname=(String)sBean.get("userfullname");
                String status=(String)sBean.get("status");
                String a0100=(String)sBean.get("a0100");
                String e0122=(String)sBean.get("e0122");
                String state=(String)sBean.get("state");
                String userDesc="";
                String deptDesc="";
               // if (sList.size()<10){//多于10个不显示 提高一点效率
                    if ("1".equals(status)){//自助
                        String dbpre=a0100.substring(0,3);                    
                        a0100=a0100.substring(3);
                        if (userfullname!=null && userfullname.length()>0){
                            deptDesc=AdminCode.getCodeName("UM", e0122);
                        }
                        else {
                            rowSet=dao.search("select * from "+dbpre+"A01 where a0100='"
                                    +a0100+"'");
                            if (rowSet.next()){
                                String e01221=rowSet.getString("e0122");
                                if (e01221!=null) {
                                    deptDesc=AdminCode.getCodeName("UM", e01221);
                                }
                                userfullname=rowSet.getString("a0101");
                            }
                            
                        }
                        
                        userDesc= userfullname+"("+deptDesc+")";
                    }
                    else {
                        deptDesc= getUsrGroupName(username);
                        String _name=getUsrFullName(username);
                        userfullname="".equals(_name)?userfullname:_name;
                        userDesc=userfullname+"("+deptDesc+")";
                        
                    }
               // }
                LazyDynaBean bean =  new LazyDynaBean();
                bean.set("userDesc", userDesc); 
                if("5".equals(actor_type)) {
                	if("1".equals(state)) {
                		bean.set("isDealing", state);
	                    list.add(bean);
                	}else {
                		bean.set("isDealing", "");  
	                    unDealList.add(bean);
                	}
                }else {
	                if (dealingUserMap.containsKey(username)){
	                    bean.set("isDealing", dealingUserMap.get(username));
	                    list.add(bean);
	                }
	                else {
	                    bean.set("isDealing", "");  
	                    unDealList.add(bean);
	                }
                } 
            }    
            //liuyz角色查询当前审批人，增加已被取消相关角色但是已经锁定任务的人信息显示 20170527 begin
            if(list.size()==0)
            {
            	Set keySet = dealingUserMap.keySet();
            	Iterator it = keySet.iterator();  
            	String userNameKey="";
            	DbNameBo dbbo=new DbNameBo(this.conn);
            	userNameKey=dbbo.getLogonUserNameField();
            	ArrayList dblist = dbbo.getAllLoginDbNameList();
            	while (it.hasNext()) {
	            	String key = (String) it.next();
            		String dbpre="";
            		String deptDesc="";
            		String userfullname="";
            		String userDesc="";
            		//先遍历自助用户认证库
            		for(int dbNum=0;dbNum<dblist.size();dbNum++)
            		{
            			RecordVo dbName=(RecordVo) dblist.get(dbNum);
            			dbpre=dbName.getString("pre");
            			rowSet=dao.search("select * from "+dbpre+"A01 where "+userNameKey+"='"+key+"'");
						if (rowSet.next()){
						    String e01221=rowSet.getString("e0122");
							if (e01221!=null) {
                                deptDesc=AdminCode.getCodeName("UM", e01221);
                            }
							userfullname=rowSet.getString("a0101");
							userDesc= userfullname+"("+deptDesc+")";
							break;
						}
            		}
            		//如果遍历完认证库userDesc依旧为空说明可能是业务用户，去业务库查询。
            		if(userDesc.trim().length()<=0)
            		{
            			deptDesc= getUsrGroupName(key);
                        String _name=getUsrFullName(key);
                        userfullname="".equals(_name)?userfullname:_name;
                        userDesc=userfullname+"("+deptDesc+")";
            		}
            		//如果业务和认证中都没有就不显示了，不过依旧可能在非认证库。
            		if(userDesc.trim().length()>0)
            		{
	            		LazyDynaBean bean =  new LazyDynaBean();
	            		bean.set("userDesc", userDesc);                    
	                    bean.set("isDealing", dealingUserMap.get(key.toString()));
	                    boolean added=false;//记录是否已经增加到了列表中
	                    //判断是否已经有重复人员出现在列表中，例如自助业务都有此角色，自助用户抢单了，自助又被取消了相关角色，不去重会显示两个相同人名（个人觉得应该去重）。
	                    for (int i=0;i<unDealList.size();i++){
	                        LazyDynaBean sBean = (LazyDynaBean)unDealList.get(i);
	                        if(bean.get("userDesc").equals(sBean.get("userDesc")))
	                        {
	                        	sBean.set("isDealing", dealingUserMap.get(key.toString()));
	                        	added=true;
	                        	break;
	                        }
	                    }  
	                    if(!added) {
                            list.add(bean);
                        }
            		}
            	}
            }
          //liuyz角色查询当前审批人，增加已被取消相关角色但是已经锁定任务的人信息显示 20170527 end
            for (int i=0;i<unDealList.size();i++){
                LazyDynaBean sBean = (LazyDynaBean)unDealList.get(i);
                list.add(sBean);
            }  
              
        }
        catch(Exception e)
        {
            e.printStackTrace();
        
        } finally {
            PubFunc.closeDbObj(rowSet);
         }
        return list;
        
    }
    
    
 

    /**   
     * @Title: isDealingTask   
     * @Description:获取正在处理单据的人，    
     * @param @param tab_id
     * @param @param task_id
     * @param @return 
     * @return boolean 
     * @author:wangrd   
     * @throws   
    */
    public HashMap getIsDealingTaskUser(String tab_id,String task_id)
    {
        HashMap map = new HashMap();      
        try{
            ContentDAO dao=new ContentDAO(this.conn);
            String sql0="select twt.* from  t_wf_task_objlink twt,templet_"+tab_id+" tt where twt.seqnum=tt.seqnum and twt.ins_id=tt.ins_id  ";
            sql0+=" and twt.task_id="+task_id;
          
            RowSet rowSet2=dao.search(sql0);
            while (rowSet2.next())
            {
                String _username=rowSet2.getString("username");
                if (_username!=null && _username.length()>0){
                    map.put(_username, rowSet2.getString("state"));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
       
        return  map;
    }
    
    
    /**
     * 获得报批人所在的单位  或 部门
     * @param task_id
     * @param orgFlag UN:单位  UM：部门
     * @return
     */
    /*
    private String getSubmitTaskInfo(String task_id,String orgFlag)
    {
        String info="";
        ContentDAO dao=new ContentDAO(this.conn);
        String fielditem="e0122";
        if(orgFlag.equalsIgnoreCase("UN"))
            fielditem="b0110";
        else if(orgFlag.equalsIgnoreCase("UM"))
            fielditem="e0122";
        else if(orgFlag.equalsIgnoreCase("@K"))
            fielditem="e01a1";
        else if(orgFlag.equalsIgnoreCase("username"))
            fielditem="username"; 
        else if(orgFlag.equalsIgnoreCase("a0100"))
            fielditem="a0100"; 
            
        RowSet rset=null;
        try
        {
            int ins_id=0;
            int node_id=0;
            String state="";
            String a0100="";//报批人的人员编号
            rset=dao.search("select ins_id,state,node_id from t_wf_task where task_id="+task_id);
            if(rset.next())
            {
                node_id=rset.getInt("node_id");
                ins_id=rset.getInt("ins_id");
                state=rset.getString("state");
            }
            if(state.equals("07"))  //驳回
            { 
                rset=dao.search("select a0100_1 from t_wf_task where node_id="+node_id+" and ins_id="+ins_id+" and state='08' and "+Sql_switcher.isnull("bs_flag","'1'")+"='1' and task_state='5' order by task_id desc");
            }
            else
                rset=dao.search("select a0100_1 from t_wf_task where task_id="+task_id); 
            if(rset.next())
                a0100=rset.getString(1);
           
            if(a0100!=null&&a0100.trim().length()>0)
            {
                if ("username".equals(fielditem)){
                    return a0100;
                }
                if(a0100.length()>3)
                {
                    String dbpre=a0100.substring(0,3);
                    boolean flag=false;
                    ArrayList dblist=DataDictionary.getDbpreList();
                    for(int i=0;i<dblist.size();i++)
                    {
                        if(((String)dblist.get(i)).equalsIgnoreCase(dbpre))
                            flag=true;
                    }
                    if(flag)
                    {
                        rset=dao.search("select "+fielditem+" from "+dbpre+"a01 where a0100='"+a0100.substring(3)+"' ");
                        if(rset.next())
                        {
                            info=rset.getString(1);
                        }
                    } 
                }
                
                if(info.length()==0)
                {
                    rset=dao.search("select a0100,nbase from operuser where username='"+a0100+"'");
                    if(rset.next())
                    {
                        String _a0100=rset.getString("a0100");
                        String _nbase=rset.getString("nbase");
                        if(_a0100!=null&&_a0100.length()>0&&_nbase!=null&&_nbase.length()>0)
                        {
                            rset=dao.search("select "+fielditem+" from "+_nbase+"a01 where a0100='"+_a0100+"' ");
                            if(rset.next())
                            {
                                info=rset.getString(1);
                            }
                        }
                        
                    }
                    
                }
                
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(rset!=null)
                    rset.close();
            }
            catch(Exception e)
            {
                
            }
        }
        return info;
    }
    */
    
    
    
    /**
     * 
     *角色属性“直接领导”、“主管领导”，“第三级领导”、“第四级领导”、“全部领导”，属性值各自为“9，10，11，12，13
     */
    /*//可参考getSpecialRoleMap
    public void setObjectidBySpecialRole(TemplateTableBo tableBo,
            ArrayList existsList,LazyDynaBean paramBean)
    {
        String task_id=(String)paramBean.get("task_id");
        String role_id=(String)paramBean.get("role_id");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        ArrayList specialList = new ArrayList();
        try {
            // 是否是特殊角色
            rowSet = dao.search("select * from t_sys_role where role_id='" + role_id + "'");
            int role_property = 0;
            if (rowSet.next())
                role_property = rowSet.getInt("role_property");
            if (role_property == 9 || role_property == 10 || role_property == 11 || role_property == 12 || role_property == 13) {
            
            } else {//不是特殊角色 返回
                return;
            }

            int sp_mode = tableBo.getSp_mode();
            String tab_id = String.valueOf(tableBo.getTabid());
            if (tableBo.getRelation_id() == null || tableBo.getRelation_id().length() == 0) {
                return; // throw new GeneralException("该业务流程没有定义审批关系!");
            }
            //模拟报批人身份登录
            String userName = getSubmitTaskInfo(task_id, "username");
            UserView _userView = new UserView(userName, this.conn);
            if(!_userView.canLogin(false)){
                _userView=this.userView;
            }
            
            WorkflowBo wbo = new WorkflowBo(this.conn, Integer.parseInt(tab_id), _userView);
            if (sp_mode == 1) {
                specialList= getObjectidBySpecialRole1(tableBo,wbo,role_property,task_id);
            } else { // 如果是自动流转
                specialList= getObjectidBySpecialRole0(tableBo,wbo,role_property,task_id);
            }
            //过滤已存在的
            for(int j=0;j<specialList.size();j++)
            {
                LazyDynaBean a_bean=(LazyDynaBean)specialList.get(j);
                String a100=(String)a_bean.get("a100");
                //是否已存在 已存在的不处理
                boolean bExist=false;
                for (int i=0;i<existsList.size();i++){
                    a_bean=(LazyDynaBean)existsList.get(i);
                    if("1".equals((String)a_bean.get("status"))){
                      if  (a100.equalsIgnoreCase((String)a_bean.get("a0100"))) {
                          bExist=true;
                          break;
                      }
                    }
                }
                String status=(String)a_bean.get("status");
                if (!bExist){
                    a_bean=new LazyDynaBean();
                    a_bean.set("a0100",a100);
                    a_bean.set("status",status);
                    existsList.add(a_bean);
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
        }
    }
    */
    /**   
     * @Title: getObjectidBySpecialRole1   
     * @Description: 手工审批 获取特殊角色成员   
     * @param @param tableBo
     * @param @param wbo
     * @param @param role_property
     * @param @param task_id
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    /*
    public ArrayList getObjectidBySpecialRole1(TemplateTableBo tableBo,WorkflowBo wbo,int role_property,
            String task_id) throws GeneralException
    {
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rowSet = null;
        ArrayList specialList = new ArrayList();
        try {
            RecordVo t_wf_relationVo = new RecordVo("t_wf_relation");
            if (tableBo.getRelation_id().equalsIgnoreCase("gwgx")) // 标准岗位关系
            {
                if (role_property == 13) {
                    return specialList; // throw new
                    // GeneralException(ResourceFactory.getProperty("general.template.workflowbo.info7")+"!");
                }
                String e01a1 = getSubmitTaskInfo(task_id, "@K");
                if ("".equals(e01a1)) {
                    return specialList;
                }
                LazyDynaBean _abean = new LazyDynaBean();
                _abean.set("type", "@K");
                _abean.set("value", e01a1);// 取报批人的岗位
                _abean.set("from_nodeid", "reportNode");
                ArrayList tempList = wbo.getSuperPos_userList(_abean, "human", String.valueOf(role_property));
                for (int i = 0; i < tempList.size(); i++) {
                    LazyDynaBean abean = (LazyDynaBean) tempList.get(i);
                    LazyDynaBean newBean = new LazyDynaBean();
                    newBean.set("a0100", (String) abean.get("mainbodyid"));
                    newBean.set("status", "1");
                    specialList.add(newBean);
                }
            } else {
                t_wf_relationVo.setInt("relation_id", Integer.parseInt(tableBo.getRelation_id()));
                t_wf_relationVo = dao.findByPrimaryKey(t_wf_relationVo);
                String sql = "";
                if (t_wf_relationVo.getString("actor_type").equals("1")) // 自助用户
                {
                    String userName = getSubmitTaskInfo(task_id, "username");
                    sql = "select * from t_wf_mainbody where Relation_id=" + tableBo.getRelation_id() + "  and lower(Object_id)='" + userName.toLowerCase() + "'";
                } else if (t_wf_relationVo.getString("actor_type").equals("4")) // 业务用户
                {
                    String userName = getSubmitTaskInfo(task_id, "username");
                    sql = "select *  from t_wf_mainbody where Relation_id=" + tableBo.getRelation_id() + "  and lower(Object_id)='" + userName.toLowerCase() + "'";
                }
                if (role_property != 13)
                    sql += " and SP_GRADE=" + role_property + " ";
                else
                    sql += " and SP_GRADE in (9,10,11,12) ";
                rowSet = dao.search(sql);
                while (rowSet.next()) {
                    LazyDynaBean newBean = new LazyDynaBean();
                    String a0101 = rowSet.getString("a0101");
                    String mainbodyid = rowSet.getString("mainbody_id");
                    if (t_wf_relationVo.getString("actor_type").equals("4")) {
                        newBean.set("status", "0");
                    } else {
                        newBean.set("status", "1");
                    }
                    newBean.set("a0100", mainbodyid);
                    specialList.add(newBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // throw GeneralExceptionHandler.Handle(e);
        }
        return specialList;
    }
*/
    

    /**   
     * @Title: getObjectidBySpecialRole0   
     * @Description:  自动审批 获取特殊角色成员     
     * @param @param tableBo
     * @param @param wbo
     * @param @param role_property
     * @param @param task_id
     * @param @return
     * @param @throws GeneralException 
     * @return ArrayList 
     * @author:wangrd   
     * @throws   
    */
    /*
    public ArrayList getObjectidBySpecialRole0(TemplateTableBo tableBo,WorkflowBo wbo,int role_property,String task_id) throws GeneralException
    {
        ArrayList specialList = new ArrayList();
        RowSet rowSet = null;
        try
        {
            int ins_id=0;
            int node_id=-1;
            String selfapply="";
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search("select ins_id from t_wf_task where task_id=" + task_id);
            if (rowSet.next()){
                ins_id = rowSet.getInt("ins_id");
                node_id = rowSet.getInt("ins_id");
            }
            
            RecordVo t_wf_relationVo = new RecordVo("t_wf_relation");
            t_wf_relationVo = dao.findByPrimaryKey(t_wf_relationVo);
            if (t_wf_relationVo.getString("actor_type").equals("1")) // 自助用户
            {
                selfapply="1";
                tableBo.setBEmploy(true);
            } else if (t_wf_relationVo.getString("actor_type").equals("4")) // 业务用户
            {
                
            }
    
            WF_Node wf_node=getWFNode(node_id);
            String ext_param=wf_node.getExt_param();
            LazyDynaBean bean=wbo.getFromNodeid_role(ext_param,ins_id,dao,Integer.parseInt(task_id),selfapply,"");
            if(tableBo.getRelation_id().equalsIgnoreCase("gwgx")) // 标准岗位关系
            {
                String e01a1=(String)bean.get("value");
                if(e01a1==null||e01a1.trim().length()==0)
                {
                    return specialList;
                } 
                if(role_property==13)
                {
                    return specialList ;
                } 
                LazyDynaBean _abean = new LazyDynaBean();
                _abean.set("type", "@K");
                _abean.set("value", e01a1);
                _abean.set("from_nodeid", "reportNode");
                ArrayList tempList = wbo.getSuperPos_userList(_abean, "human", String.valueOf(role_property));
                for (int i = 0; i < tempList.size(); i++) {
                    LazyDynaBean abean = (LazyDynaBean) tempList.get(i);
                    LazyDynaBean newBean = new LazyDynaBean();
                    newBean.set("a0100", (String) abean.get("mainbodyid"));
                    newBean.set("status", "1");
                    specialList.add(newBean);
                }
            }
            else
            {
                HashMap a_map=wbo.getSuperSql(role_property,tableBo.getRelation_id(),bean);
                if(a_map.size()==0) 
                    return specialList;
                   
                String sql=(String)a_map.get("sql");                    
                sql+=" order by SP_GRADE" ;
                
                rowSet=dao.search(sql); 
                while(rowSet.next())
                {
                    LazyDynaBean newBean=new LazyDynaBean();
                    String actor_type=rowSet.getString("actor_type"); //1:HUMAN 4:业务用户 
                    newBean.set("actor_type",actor_type);
                    if(actor_type.equals("1")) //自助
                    {
                        newBean.set("userfullname",rowSet.getString("a0101"));
                        newBean.set("e0122",rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"");
                        newBean.set("status", "1");  
                    }
                    else
                    {
                        newBean.set("status", "0");   
                    }
                    newBean.set("a0100",rowSet.getString("Mainbody_id"));
                    specialList.add(newBean);
                }
            }      
        }
        catch(Exception e)
        {
       
        }
       
        return specialList;
    }
*/
    

    /**   
     * @Title: getWFNode   
     * @Description:获取流程号Vo    
     * @param @param node_id
     * @param @return
     * @param @throws GeneralException 
     * @return WF_Node 
     * @author:wangrd   
     * @throws   
    */
    /*
    public WF_Node getWFNode(int node_id)throws GeneralException
    {
        WF_Node wf_node=new WF_Node(this.conn);     
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo node_vo=new RecordVo("t_wf_node");
            node_vo.setInt("node_id",node_id);
            node_vo=dao.findByPrimaryKey(node_vo);
            wf_node.setNode_id(node_vo.getInt("node_id"));
            wf_node.setNodename(node_vo.getString("nodename"));
            wf_node.setNodetype( Integer.parseInt(node_vo.getString("nodetype")));
            wf_node.setTabid(node_vo.getString("tabid"));
            wf_node.setExt_param(node_vo.getString("ext_param"));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            //throw GeneralExceptionHandler.Handle(ex);
        }
        return wf_node;
    }
    */
    
}
