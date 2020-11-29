package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchUserRoleTrans</p>
 * <p>Description:查询用户角色关系对应表，t_sys_staff_in_role</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:2:29:15 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchUserRoleTrans extends IBusiness {

    /**
     * 
     */
    public SearchUserRoleTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    private HashMap getUserRole(String user_id,String dbpre,String user_flag)throws GeneralException
    {
        HashMap hm=new HashMap();

        StringBuffer strsql=new StringBuffer();
        strsql.append("select role_id from t_sys_staff_in_role where staff_id='");
        if("1".equals(user_flag))
        	strsql.append(dbpre+user_id);
        else
        	strsql.append(user_id);
        strsql.append("' and status=");
        strsql.append(user_flag);

        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                String role_id=this.getFrowset().getString("role_id");
                hm.put(role_id,role_id);
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);            
        }
        return hm;
    }
    
    /***
     * 分析登录用户是否拥有此角色
     * @param role_id
     * @return
     */
    private boolean isHaveRole(String role_id)
    {
      if(userView.isSuper_admin()&&(!userView.isBThreeUser()))
    	return true;
      /**登录用户拥有的角色*/
      boolean flag=false;
      ArrayList rolelist=userView.getRolelist();
      for(int i=0;i<rolelist.size();i++)
      {
    	  if(role_id.equals(rolelist.get(i)))
    	  {
    		  flag=true;
    		  break;
    	  }
      }
      return flag;
    }
    
    private String getFirstDbase(ArrayList dblist)
    {
    	if(dblist==null||dblist.size()==0)
    		return "";
    	RecordVo vo=(RecordVo)dblist.get(0);
    	return vo.getString("pre");
    }         

    public void execute() throws GeneralException {
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String user_id="0";
        user_id=(String)this.getFormHM().get("a_roleid");
        if(user_id==null|| "".equals(user_id))
            return;
        String userflag=(String)hm.get("a_userflag");  
        String ret_ctrl = (String)hm.get("ret_ctrl");
        String dbpre=(String)this.getFormHM().get("dbpre");
        DbNameBo dbNameBo=new DbNameBo(this.getFrameconn());
        ArrayList dblist=dbNameBo.getAllLoginDbNameList();
        if((dbpre==null|| "".equals(dbpre)))
        {
        	dbpre=getFirstDbase(dblist);
        }  
        ArrayList list=new ArrayList();
        try
	    {
        	StringBuffer strsql=new StringBuffer();
        	 ContentDAO dao=new ContentDAO(this.getFrameconn());
        /*//只能为业务非su超级用户授权三员角色
        
       
        strsql.append("select * from operuser where groupid=1 and roleid=0 and username='"+user_id+"'");
        this.frecset=dao.search(strsql.toString());
        boolean issuperadmin=false;//是否是业务超级用户
        if(this.frecset.next())
        	issuperadmin= true;*/
        //dbpre=dbpre.toUpperCase();  
        /**
         * 查询些用户拥有的角色
         */
        HashMap rolehm=getUserRole(user_id,dbpre,userflag);
        
	    //strsql.setLength(0);
	    strsql.append("select role_id,role_name,role_desc,role_property,valid,status from t_sys_role where valid=1");
	    strsql.append(" order by norder");
	    
	    
	    
	      this.frowset = dao.search(strsql.toString());

	      ArrayList otherRoleArr = new ArrayList();
	      while(this.frowset.next())
	      {
	          
	          String roleId = this.getFrowset().getString("role_id");
	          boolean hasRole = isHaveRole(this.getFrowset().getString("role_id"));
	          boolean otherRole = rolehm.containsKey(roleId);
	          /**有此角色,才能赋角色给别人,解决分布式授权机机制*/
	          /**当没有此角色 但是 被授权人有此角色时，可以看到，但不可以编辑。
	           * 防止两个人都可以对1个人授权角色时，互相冲掉数据 bug【14953】 guodd 2016-07-01*/
	          if(!hasRole && !otherRole)
	        	  	continue;
	          
	        	  RecordVo vo=new RecordVo("T_SYS_ROLE");
	          vo.setString("role_id",roleId);
	          
	          if("su".equals(user_id)||!(this.userView.isSuper_admin()&& "su".equals(this.userView.getUserName()))){//不能为超级用户su授权三员角色||非超级用户su不能为其他用户授权三员角色（及时此用户有三员角色） xuj 2010-9-27
	        	  int role_property = this.frowset.getInt("role_property");
	        	  if(role_property==0||role_property==15||role_property==16)
	        		  continue;
	          }
	          vo.setString("role_name",this.getFrowset().getString("role_name"));
	          vo.setString("role_desc",PubFunc.toHtml(this.getFrowset().getString("role_desc")));
	          vo.setString("role_property",this.getFrowset().getString("role_property"));
	          if(rolehm.get(this.getFrowset().getString("role_id"))==null)
	              vo.setString("valid","0");	              
	          else
	              vo.setString("valid","1");
	          vo.setString("status",this.getFrowset().getString("status"));
	          /**页面判断为2时，不可编辑。将此记录放到最后 bug【14953】 guodd 2016-07-01*/
	          if(!hasRole){
	        	  	vo.setString("valid","2");
	        	  	otherRoleArr.add(vo);
	        	  	continue;
	          }
	          list.add(vo);
	      }
	      
	      list.addAll(otherRoleArr);
	      if("batch".equals(user_id)&&"0".equals(ret_ctrl)){
	    	  StringBuffer tmpsb = new StringBuffer();
	    	  ArrayList selectedaccount=(ArrayList)this.getFormHM().get("selectedaccount");
	    	  for(int i=0;i<selectedaccount.size();i++){
	    		  DynaBean dbean = (DynaBean)selectedaccount.get(i);
	    		  tmpsb.append(","+dbpre+dbean.get("a0100"));
	    	  }
	    	  user_id=tmpsb.toString();
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("rolelist",list);
	        this.getFormHM().put("user_id",user_id);
	        this.getFormHM().put("userflag",userflag);
	    }

    }

}
