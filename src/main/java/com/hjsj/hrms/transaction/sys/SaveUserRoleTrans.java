package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * <p>Title:SaveUserRoleTrans</p>
 * <p>Description:保存角色关系对应表,t_sys_staff_in_role</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:2:31:14 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveUserRoleTrans extends IBusiness {

    /**
     * 
     */
    public SaveUserRoleTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    private String getFirstDbase(ArrayList dblist)
    {
    	RecordVo vo=(RecordVo)dblist.get(0);
    	return vo.getString("pre");
    }       
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList list=(ArrayList)this.getFormHM().get("selectedlist");
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        if(list==null)
            return ;
        String user_id=(String)this.getFormHM().get("user_id");
        String dbpre=(String)this.getFormHM().get("dbpre");
        DbNameBo dbNameBo=new DbNameBo(this.getFrameconn());
        ArrayList dblist=dbNameBo.getAllLoginDbNameList();
        if(dblist==null)
        {
        	 throw GeneralExceptionHandler.Handle(new GeneralException("没有定义认证人员库！")); 
        }
        if(dbpre==null|| "".equals(dbpre))
        {
        	dbpre=getFirstDbase(dblist);
        }         
        //dbpre=dbpre.toUpperCase();
        String userflag=(String)hm.get("a_userflag"); 
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        
        //判断选中角色是否有三员角色特征角色，非超级用户不允许授权三员角色特征角色
        /*  2015-05-27 guodd 取消限制 [工作项 9884]  世纪金源：想在普通用户组下的用户身上授权“三员角色特征”的角色  
        if("0".equals(userflag)){
        	boolean isHThree = false;
        	for(int i=0;i<list.size();i++)
            {
                String role_property =((RecordVo)list.get(i)).getString("role_property");
                if("0".equals(role_property)||"15".equals(role_property)||"16".equals(role_property)){
                	isHThree = true;
                	break;
                }
            }
        	if(isHThree){
        		String sql  = "select username from operuser where username='"+user_id+"' and groupid=1 and roleid=0";
        		try {
					this.frowset = dao.search(sql);
					if(!this.frowset.next()){
						throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("sys.operuser.signrole.msg"))); 
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
        */
        //自助用户、快速角色分配不允许授权三员角色特征角色
        if("1".equals(userflag)||"2".equals(userflag)){
        	boolean isHThree = false;
        	for(int i=0;i<list.size();i++)
            {
                String role_property =((RecordVo)list.get(i)).getString("role_property");
                if("0".equals(role_property)||"15".equals(role_property)||"16".equals(role_property)){
                	isHThree = true;
                	break;
                }
            }
        	if(isHThree){
				throw GeneralExceptionHandler.Handle(new GeneralException(ResourceFactory.getProperty("sys.operuser.signrole.msg"))); 
        	}
        }
        
        if(user_id.indexOf(",")!=-1&& "1".equals(userflag)){//账号批量授权角色
        	user_id = user_id.substring(1);
        	StringBuffer strsql=new StringBuffer();
	        strsql.append("delete from t_sys_staff_in_role where staff_id=? and status=? and role_id=?");
	        String[] prea0100s = user_id.split(",");
	        ArrayList values = new ArrayList();
	        ArrayList listvo=null;
	        for(int i=0;i<prea0100s.length;i++){
		        	for(int n=0;n<list.size();n++)
		            {
				        ArrayList params=new ArrayList();
				        params.add(prea0100s[i]);
				        params.add(userflag);
				        params.add(((RecordVo)list.get(n)).getString("role_id"));
				        values.add(params);
		            }
	        }
	        try
	        {
	            /**
	             * 先删除以前的记录
	             */
	            dao.batchUpdate(strsql.toString(),values);
	            listvo=new ArrayList();
	            for(int i=0;i<prea0100s.length;i++){
	            	
		            for(int n=0;n<list.size();n++)
		            {
		                RecordVo vo=new RecordVo("t_sys_staff_in_role");
		                vo.setString("staff_id",prea0100s[i]);
		                vo.setString("role_id",((RecordVo)list.get(n)).getString("role_id"));
		                vo.setString("status",userflag);
		                listvo.add(vo);
		            }
		        }
	            dao.addValueObject(listvo);
	            this.getFormHM().put("@eventlog", this.getEventLog(userflag, user_id));
	        }
	       
	        catch(SQLException sqle)
	        {
	            sqle.printStackTrace();
	  	        throw GeneralExceptionHandler.Handle(sqle);            
	        }
	        catch(Exception ex)
			{
	        	ex.printStackTrace();
	  	        throw GeneralExceptionHandler.Handle(ex);        
			}
	        finally
	        {
	        	values=null;
	            listvo=null;
	        }
        }else{
	        StringBuffer strsql=new StringBuffer();
	        strsql.append("delete from t_sys_staff_in_role where staff_id=? and status=?");
	        ArrayList listvo=null;
	        	ArrayList params=new ArrayList();
		        if("1".equals(userflag)){
		        	if(user_id.length()==11){
		        		params.add(user_id);
		        	}else
		        		params.add(dbpre+user_id);
		        }else
		        	params.add(user_id);
		        params.add(userflag);
	        try
	        {
	            /**
	             * 先删除以前的记录
	             */
	            dao.update(strsql.toString(),params);
	            listvo=new ArrayList();
	            for(int i=0;i<list.size();i++)
	            {
	                RecordVo vo=new RecordVo("t_sys_staff_in_role");
	                if("1".equals(userflag)){
	                	if(user_id.length()==11){
	                		vo.setString("staff_id",user_id);
	    	        	}else
	    	        		vo.setString("staff_id",dbpre+user_id);
	                }else
	                	vo.setString("staff_id",user_id);
	                vo.setString("role_id",((RecordVo)list.get(i)).getString("role_id"));
	                vo.setString("status",userflag);
	                listvo.add(vo);
	            }
	            dao.addValueObject(listvo);
	            if("1".equals(userflag)){
	            	if(user_id.length()==11){
	            		this.getFormHM().put("@eventlog", this.getEventLog(userflag, user_id));
	            	}else{
	            		this.getFormHM().put("@eventlog", this.getEventLog(userflag, dbpre+user_id));
	            	}
	            }else{
	            	this.getFormHM().put("@eventlog", this.getEventLog(userflag, user_id));
	            }
	        }
	       
	        catch(SQLException sqle)
	        {
	            sqle.printStackTrace();
	  	        throw GeneralExceptionHandler.Handle(sqle);            
	        }
	        catch(Exception ex)
			{
	        	ex.printStackTrace();
	  	        throw GeneralExceptionHandler.Handle(ex);        
			}
	        finally
	        {
	        	params=null;
	            listvo=null;
	        }
        }

    }

    private String getEventLog(String userflag,String user_id){
    	StringBuffer mess = new StringBuffer(ResourceFactory.getProperty("log.asign.dui"));
    	try{
	    	ContentDAO dao = new ContentDAO(this.frameconn);
	        if("1".equals(userflag)){//自助
	    		mess.append(ResourceFactory.getProperty("label.role.detail.name.1"));
	    		if(user_id.length()>11){
	    			 String[] prea0100s = user_id.split(",");
	    			 StringBuffer wherea0100=new StringBuffer();
	    			 String dbpre = prea0100s[0].substring(0,3);
	    		     for(int i=0;i<prea0100s.length;i++){
	    			        wherea0100.append(" a0100='"+prea0100s[i].substring(3)+"' or ");
	    		    }
	    		     wherea0100.append(" 1=2 ");
		    		this.frowset = dao.search("select a0101 from "+dbpre+"A01 where ("+wherea0100+")");
		    		mess.append("[");
		    		while(this.frowset.next())
		    			mess.append(this.frowset.getString("a0101")+",");
		    		mess.append("]");
	    		}else{
	    			this.frowset = dao.search("select a0101 from "+user_id.substring(0, 3)+"A01 where a0100='"+user_id.substring(3)+"'");
		    		if(this.frowset.next())
		    			mess.append("["+this.frowset.getString("a0101")+"]");
	    		}
	        }else if("0".equals(userflag)){//业务
	    		mess.append(ResourceFactory.getProperty("label.role.detail.name.0"));
	    		mess.append("["+user_id+"]");
	        }else{//机构
	    		mess.append(ResourceFactory.getProperty("tree.orgroot.orgdesc"));
	    		this.frowset = dao.search("select codeitemdesc from organization where codeitemid='"+user_id+"'");
	    		if(this.frowset.next())
	    			mess.append("["+this.frowset.getString("codeitemdesc")+"("+user_id+")]");
	    	}
	        mess.append(ResourceFactory.getProperty("label.role.assign"));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        return mess.toString();
    }
}
