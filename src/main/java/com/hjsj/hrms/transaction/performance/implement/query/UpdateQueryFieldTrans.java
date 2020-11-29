package com.hjsj.hrms.transaction.performance.implement.query;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:UpdateQueryFieldTrans</p>
 * <p>Description:高级查询字段的选择的增减处理</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 20, 2005:2:06:41 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class UpdateQueryFieldTrans extends IBusiness {

    /**
     * 
     */
    public UpdateQueryFieldTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
    	
        ArrayList fieldlist=new ArrayList();  
        String codeID=(String)this.getFormHM().get("codeID");
       
        if(codeID==null||codeID.length()==0)
        {    
            return;
        }
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql="select itemid, fieldsetid,itemdesc from fielditem where fieldsetid='"+codeID+"' and useflag='1'";
        try
        {
        	
        	this.frowset=dao.search(sql);
        	boolean isK=false;
        	boolean isUm=false;
        	boolean isUn=false;
	        while(this.frowset.next())
	        {
	            String fieldID=this.frowset.getString("itemid");
	            String fieldName=this.frowset.getString("itemdesc");
	            
	            if("e0122".equalsIgnoreCase(fieldID))
	            	isUm=true;
	            if("e01a1".equalsIgnoreCase(fieldID))
	            	isK=true;
	            if("b0110".equalsIgnoreCase(fieldID))
	            	isUn=true;
	            CommonData dataobj = new CommonData(fieldID,fieldName);
	            fieldlist.add(dataobj);
	            
	        }
	        if("A01".equalsIgnoreCase(codeID))
	        {
	        	if(!isUn)
	        		fieldlist.add(new CommonData("B0110","单位名称"));
	        	if(!isUm)
	        		fieldlist.add(new CommonData("E0122","部门名称"));
	        	if(!isK)
	        		fieldlist.add(new CommonData("E01A1","职位名称"));
	        	
	        }
	        
	        
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally
		{
        	
			this.getFormHM().clear();
			this.getFormHM().put("fieldlist",fieldlist);
		}
    }

}
