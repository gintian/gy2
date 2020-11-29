/*
 * Created on 2005-7-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.sys;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;

/**
 * <p>Title:SearchLogListTrans</p>
 * <p>Description:查询日志列表，fr_txlog</p>
 * <p>Company:hjsj</p>
 * <p>create time:July 25, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchLogListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		StringBuffer strsql=new StringBuffer();
	    strsql.append("select * from fr_txlog");
	    strsql.append(this.getPrivSQL(userView, this.frameconn,"query"));
	    this.getFormHM().put("strsql",strsql.toString());

/*	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	      	  RecordVo vo=new RecordVo("fr_txlog");
	      	  vo.setString("sequenceno",this.getFrowset().getString("sequenceno"));  
	      	  vo.setString("id",this.getFrowset().getString("id"));
	          vo.setString("name",this.getFrowset().getString("name"));
	          vo.setString("description",this.getFrowset().getString("description"));
	          vo.setString("type",this.getFrowset().getString("type"));
	          vo.setString("commitor",this.getFrowset().getString("commitor"));
	          vo.setString("beginexectime",this.getFrowset().getString("beginexectime"));
	          vo.setString("endexectime",this.getFrowset().getString("endexectime"));
	          vo.setString("execstatus",this.getFrowset().getString("execstatus"));
	          vo.setString("remoteaddr",this.getFrowset().getString("remoteaddr"));
	          vo.setString("errormsg",this.getFrowset().getString("errormsg"));
	          list.add(vo);
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("queryLoglist",list);
	    }
*/
	}
	
	/**
     * 普通用户、系统管理员、安全保密员查看日志显示isthree值为0并且只显示归属单位内的记录（b0110 like “xx%” or b0110 is null），
     * 安全审计员查看日志显示isthree值为1|2|3并且只显示归属单位内的记录（b0110 like “xx%” or b0110 is null）  3:审计员|2:保密员|1:系统管理员
	 * 
	 * @param userView
	 * @param conn
	 * @param path 取值query|clear
	 * @return
	 */
	public String getPrivSQL(UserView userView,Connection conn,String path){
		StringBuffer strsql= new StringBuffer();
		if("su".equals(userView.getUserId()))//su啥都能干
			return strsql.append(" where 1=1").toString();
		if(userView.isBThreeUser()){
	    	//String unit_id=userView.getUnit_id();
	    		if(userView.haveTheRoleProperty("16")){//安全审计员
	    			strsql.setLength(0);
	    	    	//if(unit_id.length()>=2){
	    			if(userView.getUserOrgId().length()>0){
	    	    		//String fullPriv=PubFunc.getTopOrgDept(unit_id);
	    	    		//String priv = fullPriv.split("`")[0];
	    	    		strsql.append(" where isthree<>'0' and isthree is not null and (b0110 like '"+userView.getUserOrgId()+"%' or b0110 is null)");
	    	    		if("clear".equals(path)){//审计员不能删除审计员自己日志
	    	    			strsql.append(" and isthree<>'3'");
	    	    		}
	    	    	}
	    			else{//无范围的可以看到无范围的用户日志
	    	    		strsql.append(" where isthree<>'0' and isthree is not null and (b0110 is null or b0110='')");
	    	    		if("clear".equals(path)){//审计员不能删除审计员自己日志
	    	    			strsql.append(" and isthree<>'3'");
	    	    		}
	    	    	}
	    		}else{
	    			strsql.setLength(0);
	    	    	//if(unit_id.length()>=2){
	    			if(userView.getUserOrgId().length()>0){
	    	    		//String fullPriv=PubFunc.getTopOrgDept(unit_id);
	    	    		//String priv = fullPriv.split("`")[0];
	    	    		strsql.append(" where (isthree='0' or isthree is null) and (b0110 like '"+userView.getUserOrgId()+"%' or b0110 is null)");
	    	    		if("clear".equals(path)&&userView.haveTheRoleProperty("15")){//保密员不能删除保密员自己日志
	    	    			strsql.append(" and isthree<>'2'");
	    	    		}
	    	    	}
	    			//获取机构编号为空，去掉this  wangb 2018-11-05
	    			else if(userView.getUnit_id()!=null&&userView.getUnit_id().trim().length()>2){ //业务用户没关联自助用户时，取操作单位
	    				String priv = userView.getUnit_id().split("`")[0];
	    	    		String unitid="";
	    	    		StringBuffer tt=new StringBuffer("");
	    				if(userView.getUnit_id().length()==3)
	    				{
	    					unitid="";
	    					tt.append(" or 1=1 ");
	    				}
	    				else
	    				{
	    			    	unitid=userView.getUnit_id();
	    			    	String[] unit_arr = unitid.split("`");
	    			    	for(int i=0;i<unit_arr.length;i++)
	    			    	{
	    			    		if(unit_arr[i]==null|| "".equals(unit_arr[i]))
	    			    			continue;
	    			    		tt.append(" or b0110 like '"+unit_arr[i].substring(2)+"%' ");
	    			    	}
	    				}
	    				if(tt.length()==0)
	    					tt.append(" or 1=2 ");
	    				
	    				strsql.append(" where (isthree='0' or isthree is null) and ("+tt.substring(3)+" or b0110 is null)");
	    	    		if("clear".equals(path)&&userView.haveTheRoleProperty("15")){//保密员不能删除保密员自己日志
	    	    			strsql.append(" and isthree<>'2'");
	    	    		}
	    	    	}
	    			else{//无范围的可以看到无范围的用户日志
	    	    		//strsql.append(" where 1=2");
	    	    		strsql.append(" where (isthree='0' or isthree is null) and (b0110 is null or b0110='')");
	    	    		if("clear".equals(path)&&userView.haveTheRoleProperty("15")){//保密员不能删除保密员自己日志
	    	    			strsql.append(" and isthree<>'2'");
	    	    		}
	        		}
	    		}
	    }else{
	    	//if(userView.getStatus()==4){//自助
	    		if(userView.getUserOrgId().length()>0)
	    			strsql.append(" where (isthree='0' or isthree is null) and (b0110 like '"+userView.getUserOrgId()+"%' or b0110 is null)");
	    		else
	    			strsql.append(" where 1=2");
	    	/*}else{
	    		String unit_id=userView.getUnit_id();
	    		if(unit_id.length()>=2){
		    		String fullPriv=PubFunc.getTopOrgDept(unit_id);
		    		String priv = fullPriv.split("`")[0];
		    		strsql.append(" where (isthree='0' or isthree is null) and (b0110 like '"+priv.substring(2)+"%' or b0110 is null)");
		    	}else{
		    		strsql.append(" where 1=2");
	    		}
	    	}*/
	    }
		return strsql.toString();
	}
	
	
}
