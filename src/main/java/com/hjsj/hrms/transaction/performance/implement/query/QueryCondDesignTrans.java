package com.hjsj.hrms.transaction.performance.implement.query;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:QueryCondDesignTrans</p>
 * <p>Description:查询条件定义</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:9:23:20 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class QueryCondDesignTrans extends IBusiness {

    /**
     * 
     */
    public QueryCondDesignTrans() {
        super();
    }
    /**
     * 查找是否存在相同的因子对象
     * @param name
     * @param list
     * @return
     */
    private Factor findFactor(String name,ArrayList list,int index)
    {
    	Factor factor=null;
    	for(int i=0;i<list.size();i++)
    	{
    		factor=(Factor)list.get(i);
    		if(name.equalsIgnoreCase(factor.getFieldname())&&(i==index))
    			break;
    		factor=null;
    	}
    	return factor;
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        String[] fields=(String[])this.getFormHM().get("right_fields");
        if(fields==null||fields.length==0)
        {
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfield"),"",""));
        }
       
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list =new ArrayList();
       
        try
        {
        	ArrayList factorlist=new ArrayList();

            /**定义条件项*/
            FieldItem item=null;
            for(int i=0;i<fields.length;i++)
            {
                String fieldname=fields[i];
                if(fieldname==null|| "".equals(fieldname))
                    continue;
                item=DataDictionary.getFieldItem(fieldname.toUpperCase());
                Factor factor=null;
                if(item!=null)
                {
                	
                    factor=new Factor(1);
                    factor.setCodeid(item.getCodesetid());
                    factor.setFieldname(item.getItemid());
                    factor.setHz(item.getItemdesc());
                    factor.setFieldtype(item.getItemtype());
                    factor.setItemlen(item.getItemlength());
                    factor.setItemdecimal(item.getDecimalwidth());
                    factor.setOper("=");//default
                    factor.setLog("*");//default
                    list.add(factor);
                  
                }                
            }
           
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            this.getFormHM().put("factorlist",list);
          
        }

    }

}