package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SaveItemComputerTrans extends IBusiness {

	public void execute() throws GeneralException {
	    try {
    		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");   
    		String items=(String)hm.get("akq_item");
    		String c_expr=(String)this.getFormHM().get("c_expr");
    		c_expr = com.hjsj.hrms.utils.PubFunc.hireKeyWord_filter_reback(c_expr);
    		this.getFormHM().put("c_expr", c_expr);
    		String expr_flag=(String)this.getFormHM().get("expr_flag");
    		if(expr_flag==null||expr_flag.length()<=0)
    			return;
    		
    		if(c_expr==null|| "".equals(c_expr))
    			c_expr="";
    		else
    		{
    		     ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET); 
    		     YksjParser yp = new YksjParser(
    			 getUserView()//Trans交易类子类中可以直接获取userView
    			 ,fielditemlist
    			  ,YksjParser.forNormal
    			 ,YksjParser.INT//此处需要调用者知道该公式的数据类型
    			 ,YksjParser.forPerson
    			 ,"USR","");
    		   yp.setCon(this.getFrameconn());
    		   if(!yp.Verify(c_expr.trim()) ){//校验不通过
    			   String strErrorMsg = ResourceFactory.getProperty("errors.query.expression") + "<br>" + yp.getStrError();
    			   throw new GeneralException("", strErrorMsg, "", "");	
    		   }
    		}
    		saveC_expr(items,c_expr,expr_flag);
	    } catch (Exception e) {
	        throw GeneralExceptionHandler.Handle(e);
	    }
	}
	/**
	 * 保存考勤规则公式
	 * @param items
	 * @param c_expr
	 * @param expr_flag
	 * @throws GeneralException
	 */
    public void saveC_expr(String items,String c_expr,String expr_flag)throws GeneralException
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	String sql="select c_expr  from kq_item where item_id='"+items+"'";
    	String old_expr="";
    	String rest="";
        try
        {
        	this.frowset=dao.search(sql);
        	if(this.frowset.next())
        	{
        		old_expr=Sql_switcher.readMemo(this.frowset,"c_expr");
        	}else
        	{
        		return;
        	}
        	if("day".equals(expr_flag))//日计算公式
        	{
        		if(old_expr==null||old_expr.length()<=0)
        		{
        			rest=c_expr+"^";
        		}else
        		{
        			int s=old_expr.indexOf("^");
        			if(s!=-1)
        			{
        				rest=c_expr+"^"+old_expr.substring(s+1);
        			}else
        			{
        				rest=c_expr+"^";
        			}
        		}
        	}else if("mo".equals(expr_flag))//月计算公式
        	{
        		if(old_expr==null||old_expr.length()<=0)
        		{
        			rest="^"+c_expr;
        		}else
        		{
        			int s=old_expr.indexOf("^");
        			if(s!=-1)
        			{
        				rest=old_expr.substring(0,s)+"^"+c_expr;
        			}else
        			{
        				rest=c_expr+"^";
        			}
        		}
        	}
        	StringBuffer st=new StringBuffer();
//        	st.append("update kq_item set c_expr='");
//      	    st.append(rest);
//      	    st.append("' where item_id='");
//      	    st.append(items.toString());
//      	    st.append("'");
//      	    dao.update(st.toString());
        	st.append("update kq_item set c_expr=?");
      	    st.append(" where item_id=?");
      	    ArrayList listt = new ArrayList();
      	    listt.add(rest);
      	    listt.add(items.toString());
      	    dao.update(st.toString(), listt);
        }catch(Exception e)
        {
        	throw GeneralExceptionHandler.Handle(e);
        }
    	
    }
}
