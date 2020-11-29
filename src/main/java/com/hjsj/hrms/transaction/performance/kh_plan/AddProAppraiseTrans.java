package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:AddProAppraiseTrans.java</p>
 * <p>Description:新增描述性评议项</p>
 * <p>Company:hjsj</p>
 * <p>create time:2014-08-16 14:13:19</p>
 * @author JinChunhai
 * @version 7.0 
 */

public class AddProAppraiseTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
    	String plan_id = (String) hm.get("plan_id");
    	String addDescription = (String) hm.get("addDescription");
    	addDescription = SafeCode.decode(addDescription);
    	
		ExamPlanBo bo = new ExamPlanBo(plan_id,this.frameconn);
		String tempTable = "t#des_review";
		RowSet rowSet = null;
    	try
	    {
    		ContentDAO dao = new ContentDAO(this.getFrameconn());

			/**增加一条记录*/
			int maxid = 0;
			int maxseq = 0;
	        rowSet = dao.search("select max(id) id,max(seq) seq from " + tempTable);
	        while(rowSet.next())
	        {
	        	String id = rowSet.getString("id");
	        	if((id!=null) && (id.trim().length()>0))
	        		maxid=Integer.parseInt(id);	
	        	
	        	String seq = rowSet.getString("seq");
	        	if((seq!=null) && (seq.trim().length()>0))
	        		maxseq=Integer.parseInt(seq);	
	        }
	        ++maxid;
	        ++maxseq;
			
			ArrayList list = new ArrayList();						
			StringBuffer strSql = new StringBuffer();
			strSql.append("insert into ");
			strSql.append(tempTable);
			strSql.append("(id,seq,value,num1) values (?,?,?,?) ");
				    
			list.add(String.valueOf(maxid));
			list.add(String.valueOf(maxseq));
			list.add(addDescription);
			list.add(String.valueOf(maxseq));
		    
		    dao.insert(strSql.toString(), list);
		    
		    // 保存描述性评议项设置
		    bo.saveHighSet(tempTable);
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		}finally
		{		
			ArrayList list = bo.getTempData(tempTable);
		    this.getFormHM().put("extproList",list);	
		    this.getFormHM().put("addDescription","");
		}		

    }

}
