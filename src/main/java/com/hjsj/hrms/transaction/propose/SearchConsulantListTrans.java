/*
 * Created on 2005-5-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.propose;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 * 咨询列表查询
 */
public class SearchConsulantListTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	  public void execute() throws GeneralException {
	  	
	  	this.getFormHM().put("userAdmin",Boolean.toString(userView.isSuper_admin()));
	    StringBuffer strsql=new StringBuffer();
	    String sql = getSearchSQL();	    
	    strsql.append(sql);
	    strsql.append(" order by createtime desc");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    ArrayList list=new ArrayList();
	    try
	    {
	      this.frowset = dao.search(strsql.toString());
	      while(this.frowset.next())
	      {
	          	RecordVo vo=new RecordVo("consultation");
	          	vo.setString("id",this.frowset.getString("id"));
	          	/**匿名*/
	          	String temp=this.frowset.getString("createuser");
	            vo.setString("createuser",temp);
	           	vo.setDate("createtime",PubFunc.FormatDate(frowset.getDate("createtime")));
	           	temp = Sql_switcher.readMemo(frowset,"ccontent");
	           	if (temp.length()>20)
	           	   temp = temp.substring(0, 20) + "...";
	           	vo.setString("ccontent",temp);
	            vo.setString("replyuser",frowset.getString("replyuser"));
	            temp=PubFunc.FormatDate(this.frowset.getDate("replytime"));
	            vo.setDate("replytime",temp);
	            
	            temp=Sql_switcher.readMemo(frowset,"rcontent");
		        vo.setString("rcontent",temp.substring(0,temp.length()>20?20:temp.length())+"...");
		        list.add(vo);
	      }
	    }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("consulantlist",list);
	    }
    }

	/**
	 * @return
	 */
	private String getSearchSQL() {
		String sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation";
	    if(this.userView.isSuper_admin())
	    {
	    	  sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation";
	    }
	    else
	    {
	    	String a0100whe="or ((e0122='"+this.userView.getUserDeptId()+"' or e0122 is null)  " +
	    			"and (b0110='"+this.userView.getUserOrgId()+"' or b0110 is null) " +
	    					"and (e01a1='"+this.userView.getUserPosId()+"' or e01a1 is null) " +
	    							"and createuser='"+this.userView.getUserFullName()+"')";
	    	if("UN".equals(this.userView.getManagePrivCode().toString().trim()) &&!this.userView.isBhighPriv()&& "".equals(this.userView.getManagePrivCodeValue().toString().trim()))
	    	{
	    		
	    		sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation";
	    	}
	    	else if("UN".equals(this.userView.getManagePrivCode().toString().trim())&&this.userView.isBhighPriv())
	    	{
	    		sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where b0110 like '"+this.userView.getUserOrgId()+"%' "+a0100whe;
	    	}
	    	else if("UM".equals(this.userView.getManagePrivCode().toString().trim())&&this.userView.isBhighPriv())
	    	{
	    		sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where E0122 like '"+this.userView.getUserDeptId()+"%' "+a0100whe;
	    		
	    	}else if("UN".equals(this.userView.getManagePrivCode().toString().trim())&&!"".equals(this.userView.getManagePrivCodeValue().toString().trim()))
	    	{
	    		sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where B0110 like  '"+this.userView.getManagePrivCodeValue()+"%' "+a0100whe;
	    	}else if("UM".equals(this.userView.getManagePrivCode().toString().trim())&&!"".equals(this.userView.getManagePrivCodeValue().toString().trim()))
	    	{
	    		sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where E0122 like  '"+this.userView.getManagePrivCodeValue()+"%' "+a0100whe;
	    	}	     
	    	else
	    	{
	    		sql="select id,createuser,createtime,ccontent,replyuser,replytime,rcontent,B0110,E0122,E01A1 from consultation where  createuser =  '"+this.userView.getUserFullName()+"' "+a0100whe;
	    	}	    
	    }
		return sql;
	}

}
