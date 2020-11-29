/*
 * Created on 2005-9-7
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_sumup;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchZpjobDetailsTrans</p>
 * <p>Description:查询招聘活动明细</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 18, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchZpjobDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo rv=(RecordVo)this.getFormHM().get("zpSumupvo");
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        ArrayList infoList = new ArrayList();
        try
        {
        	try{
            	String sql = "select b.name,a.real_invite_amount,a.description from zp_job a , zp_resource b where zp_job_id = '"+rv.getString("zp_job_id")+"' and a.resource_id = b.resource_id";
                this.frowset = dao.search(sql);
                while(this.frowset.next()){
                	this.getFormHM().put("resource_id_name",this.frowset.getString("name"));
                	rv.setString("description",this.frowset.getString("description"));
                	rv.setString("real_invite_amount",this.frowset.getString("real_invite_amount"));
                  }
            }catch(SQLException sqle)
            {
        	      sqle.printStackTrace();
      	      throw GeneralExceptionHandler.Handle(sqle);            
            }
            String sql = "select * from zp_job_details where zp_job_id = '"+rv.getString("zp_job_id")+"'";
            this.frowset = dao.search(sql);
            while(this.frowset.next()){
            	RecordVo vo=new RecordVo("zp_job_details");
            	vo.setString("detail_id",this.getFrowset().getString("detail_id"));
            	vo.setString("detailname",this.getFrowset().getString("detailname"));
            	vo.setString("charge",this.getFrowset().getString("charge"));
            	vo.setString("realcharge",this.getFrowset().getString("realcharge"));
            	list.add(vo);
            }
            for(int i=0;i<list.size();i++){
            	RecordVo vo=(RecordVo)list.get(i);
            	infoList.add(i,vo.getString("realcharge"));;
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("zpSumupDetailslist",list);
            this.getFormHM().put("infoList",infoList);
            this.getFormHM().put("zpSumupvo",rv);
        }

	}

}
