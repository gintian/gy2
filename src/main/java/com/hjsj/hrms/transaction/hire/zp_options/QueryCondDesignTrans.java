/*
 * Created on 2005-9-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

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
 * <p>Description:查询指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
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
	        String strexprsave=(String)this.getFormHM().get("expression");
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        if(fields==null||fields.length==0)
	        {
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfield"),"",""));
	        }
	        int j=0;
	        StringBuffer strexpr=new StringBuffer();
	        ArrayList list =new ArrayList();
	        /**信息类型定义default=1（人员类型）*/
	        int nInform=1;
	        try
	        {
	            /**保存的因子对象列表*/
	        	ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
	        	
	            /**应用库过滤前缀符号*/
	            ArrayList dblist=userView.getPrivDbList();
	            StringBuffer cond=new StringBuffer();
	            cond.append("select pre,dbname from dbname where pre in (");
	            for(int i=0;i<dblist.size();i++)
	            {
	                if(i!=0)
	                    cond.append(",");
	                cond.append("'");
	                cond.append((String)dblist.get(i));
	                cond.append("'");
	            }
	            if(dblist.size()==0)
	                cond.append("''");
	            cond.append(")");
	            /**应用库前缀过滤条件*/
	            this.getFormHM().put("dbcond",cond.toString());
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
	                	/**已定义的因子再现*/
	                	if(factorlist!=null)
	                	{
	                		factor=findFactor(fieldname,factorlist,i);
	                		if(factor!=null)
	                		{
	                			list.add(factor);
	                			continue;
	                		}
	                	}
	                    factor=new Factor(nInform);
	                    factor.setCodeid(item.getCodesetid());
	                    factor.setFieldname(item.getItemid());
	                    factor.setHz(item.getItemdesc());
	                    factor.setFieldtype(item.getItemtype());
	                    factor.setItemlen(item.getItemlength());
	                    factor.setItemdecimal(item.getDecimalwidth());
	                    factor.setOper("=");//default
	                    factor.setLog("*");//default
	                    list.add(factor);
	                    ++j;
	                    strexpr.append(j);
	                    strexpr.append("*");
	                }                
	            }
	            if(strexpr.length()>0)
	            	strexpr.setLength(strexpr.length()-1);
	        }
	        catch(Exception ee)
	        {
	        	ee.printStackTrace();
	        	throw GeneralExceptionHandler.Handle(ee);
	        }
	        finally
	        {
	            this.getFormHM().put("factorlist",list);
	            if(!(strexprsave==null|| "".equals(strexprsave))){
	            	this.getFormHM().put("expression",strexprsave);  
	            }
	            else{
	            	this.getFormHM().put("expression",strexpr.toString());
	            }
	        }

	}

}
