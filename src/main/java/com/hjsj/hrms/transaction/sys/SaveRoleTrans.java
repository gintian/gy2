package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.transaction.sys.warn.DomainTool;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Map;
/**
 * @author chenmengqing
 */
public class SaveRoleTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        RecordVo vo=(RecordVo)this.getFormHM().get("rolevo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        ContentDAO dao=new ContentDAO(this.getFrameconn());  
        try
        {        
	        if("1".equals(flag))
	        {
	            /**新建角色，进行保存处理*/
	            IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	            String id=idg.getId("T_SYS_ROLE.ROLE_ID");
	            vo.setString("role_id",id);
	            //vo.setString("role_property","-1");
	            vo.setString("valid","1");
	            vo.setString("status","1");
	            vo.setInt("norder", this.getMaxOrder());
	            cat.debug("NewRoleVo="+vo.toString());           
	            dao.addValueObject(vo);
	            DomainTool domainTool = new DomainTool();
	            Map roleHM = domainTool.getRoleMap();
	            roleHM.put("RL"+id, vo.getString("role_name"));
	            /**非超级用户*/
	            if(!userView.isSuper_admin()||userView.isBThreeUser())
	            {
	                RecordVo role_vo=new RecordVo("t_sys_staff_in_role");

	                role_vo.setString("role_id",id);
	                if(userView.getStatus()==0)
	                {
		                role_vo.setString("staff_id",userView.getUserId());	                	
	                	role_vo.setString("status","0");
	                }
	                else
	                {
		                role_vo.setString("staff_id",userView.getDbname()+userView.getUserId());	 	                	
	                	role_vo.setString("status","1");
	                }
	                dao.addValueObject(role_vo);
	                /**用户增加角色*/
	                userView.getRolelist().add(id);
	            }
	            this.getFormHM().put("oqname", "");
		    	this.getFormHM().put("oqroleproperty", "");
		    	this.getFormHM().put("@eventlog", ResourceFactory.getProperty("button.insert")+ResourceFactory.getProperty("label.sys.warn.domain.role")+vo.getString("role_name")+"["+vo.getString("role_id")+"]");
	        }
	        else 
	        {
		        /**
		         * 点编辑链接后，进行保存处理
		         */
	            cat.debug("RoleVo="+vo.toString());
	            String role_property=vo.getString("role_property");
	            String role_id=vo.getString("role_id");
	            dao.updateValueObject(vo);
	            /**清空条件*/
	            clearManageCond(Integer.parseInt(role_property),role_id);
	            this.getFormHM().put("@eventlog", ResourceFactory.getProperty("button.edit")+ResourceFactory.getProperty("label.sys.warn.domain.role")+vo.getString("role_name")+"["+vo.getString("role_id")+"]");
	        }
        }
        catch(SQLException sqle)
        {
    	     sqle.printStackTrace();
    	     throw GeneralExceptionHandler.Handle(sqle);            
        }        
    }
    
    /**
     * 角色属性变更时，需要相应地清空管理范围及高级条件
     * @param flag
     * @param role_id
     * @throws GeneralException
     */
    private void clearManageCond(int flag,String role_id)throws GeneralException
    {
    	if(!(flag==1||flag==5||flag==6||flag==7))
    		return;
    	StringBuffer buf=new StringBuffer();
    	buf.append("update t_sys_function_priv set condpriv='',managepriv='' where id='");
    	buf.append(role_id);
    	buf.append("' and status=1");
    	try
    	{
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		dao.update(buf.toString());
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		throw GeneralExceptionHandler.Handle(ex);
    	}
    }
    
    private int getMaxOrder(){
    	int num = 1;
    	try
    	{
    		
    		ContentDAO dao=new ContentDAO(this.getFrameconn());
    		this.frowset = dao.search("select max(norder) num from t_sys_role");
    		if(frowset.next()){
    			num = this.frowset.getInt("num");
    		}
    		num++;
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return num;
    }
}
