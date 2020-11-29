package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OtherSaveRoleTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String other_name=(String)this.getFormHM().get("other_name");
		ArrayList rolelist=(ArrayList)this.getFormHM().get("selectedlist");
		String old_role_id="";
		if(rolelist!=null)
         {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
         	RecordVo vo=(RecordVo)rolelist.get(0);         	
         	old_role_id=vo.getString("role_id");
         	IDGenerator idg=new IDGenerator(2,this.getFrameconn());
            String id=idg.getId("T_SYS_ROLE.ROLE_ID");
            StringBuffer sql=new StringBuffer();
            try
            {
            	//vo查出的数据会将回车空格等变成html代码(\n >> <br>),另存用recordvo插入 role_desc 会出现html代码，改为用sql插入
            //	vo.setString("role_id", id);
            	//vo.setString("role_name", other_name);
            	sql.append(" insert into t_sys_role(role_id,role_name,role_desc,role_property,valid,status,norder) ");
            	sql.append("select ?,?,role_desc,role_property,valid,status,(select max(norder) from t_sys_role) norder from t_sys_role where role_id=?");
            	ArrayList values = new ArrayList();
            	values.add(id);
            	values.add(other_name);
            	values.add(vo.getString("role_id"));
            	dao.update(sql.toString(), values);
            	//dao.addValueObject(vo);
            	/**注意其它相关表中的信息,t_sys_staff_in_role,t_sys_function_priv*/
            
            	/*sql.append("insert into t_sys_staff_in_role(staff_id,role_id,status)");
            	sql.append(" select staff_id,'"+id+"',status from t_sys_staff_in_role");
            	sql.append(" where role_id='"+old_role_id+"'");
            	dao.insert(sql.toString(),new ArrayList());*/
            	sql.delete(0, sql.length());
            	sql.append("insert into t_sys_function_priv(id,status,functionpriv,condpriv,");
            	sql.append("dbpriv,tablepriv,fieldpriv,managepriv,cardpriv,namelistpriv,reportsortpriv,");
            	sql.append("warnpriv,mediapriv,templatepriv,salarysetpriv,rulePriv)");
            	sql.append(" select '"+id+"',status,functionpriv,condpriv,");
            	sql.append("dbpriv,tablepriv,fieldpriv,managepriv,cardpriv,namelistpriv,reportsortpriv,");
            	sql.append("warnpriv,mediapriv,templatepriv,salarysetpriv,rulePriv from t_sys_function_priv");
            	sql.append(" where id='"+old_role_id+"' and status=1");
            	//System.out.println(sql.toString());
            	dao.insert(sql.toString(),new ArrayList());//xuj 2010-3-11 李群叫改（刘红梅需求）把角色‘另存为’功能改为只复制一个空角色，不复制任何权限
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
            }catch(Exception e)
            {
            	e.printStackTrace();
            }
         }   
		
		
	}

}
