package com.hjsj.hrms.businessobject.propose;

import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

public class ProposeBo{
	UserView userView;
          
	
	
	/**
	 * 求查询内容的SQL语句
	 * @return
	 */
	public String getSearchSQL(UserView userView,String start_date,String end_date)throws GeneralException  {
		this.userView=userView;
		StringBuffer sql=new StringBuffer("select id ,	createuser,	createtime,	annymous ,scontent,replyuser,replytime,rcontent,ext,flag,B0110,E0122,E01A1,bread  from suggest where (");
	    if(this.userView.isSuper_admin())
	    {
	         sql.append(" 1=1 ");
	    }
	    else if(!this.userView.isSuper_admin())
	    {
	    	if(this.userView.hasTheFunction("110501"))			
	    	{
	    		String a0100whe="or ((e0122='"+this.userView.getUserDeptId()+"'  or e0122 is null)" +
	    				" and (b0110='"+this.userView.getUserOrgId()+"' or b0110 is null) " +
	    						"and (e01a1='"+this.userView.getUserPosId()+"'  or e01a1 is null) " +
	    								"and createuser='"+this.userView.getUserFullName()+"')  ";
		    	if("UN".equals(this.userView.getManagePrivCode().toString().trim()) &&!this.userView.isBhighPriv()&& "".equals(this.userView.getManagePrivCodeValue().toString().trim()))
		    	{
		    		 sql.append(" 1=1 ");
		    	}
		    	else if("UN".equals(this.userView.getManagePrivCode().toString().trim())&&this.userView.isBhighPriv())
		    	{
		    		sql.append("  (B0110 like  '"+this.userView.getUserOrgId()+"%') or ( flag=1)"+ " "+a0100whe);
		    		
		    	}
		    	else if("UM".equals(this.userView.getManagePrivCode().toString().trim())&&this.userView.isBhighPriv())
		    	{
		    		sql.append("  (E0122 like  '"+this.userView.getUserDeptId()+"%') or (flag=1)"+ " "+a0100whe);
		    		
		    	}else if("UN".equals(this.userView.getManagePrivCode().toString().trim())&&!"".equals(this.userView.getManagePrivCodeValue().toString().trim()))
		    	{
		    		sql.append("  (B0110 like  '"+this.userView.getManagePrivCodeValue()+"%') or ( flag=1)"+ " "+a0100whe);
		    	}else if("UM".equals(this.userView.getManagePrivCode().toString().trim())&&!"".equals(this.userView.getManagePrivCodeValue().toString().trim()))
		    	{
		    		sql.append("  (E0122 like  '"+this.userView.getManagePrivCodeValue()+"%') or ( flag=1)"+ " "+a0100whe);
		    	}	     
		    	else
		    	{
		    		sql.append(" (createuser = '"+this.userView.getUserFullName()+"' ) or (flag=1)"+ " "+a0100whe);
		    	}
	    	}else
	    	{
	    		sql.append(" (createuser = '"+this.userView.getUserFullName()+"' ) or (flag=1)");
	    	}
	    	
	    }   
	    
	    sql.append(")and (createtime between  ");
	    sql.append(Sql_switcher.dateValue(start_date + " 00:00:00"));
	    sql.append(" and ");
	    sql.append(Sql_switcher.dateValue(end_date + " 23:59:59"));
	    sql.append(") order by createtime desc");
		return sql.toString();
	}
}
