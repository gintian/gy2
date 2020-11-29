package com.hjsj.hrms.transaction.general.query.common;


import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 常用查询
 * create time at:Aug 10, 20064:26:28 PM
 * @author chenmengqing
 * @version 4.0
 */
public class CheckQueryCond2Trans extends IBusiness {

	/**
	 * @param factorlist
	 * @param sfactor
	 * @param sexpr
	 */
	private void combineFactor(ArrayList factorlist, StringBuffer sfactor, StringBuffer sexpr) {
		for(int i=0;i<factorlist.size();i++)
        {
            Factor factor=(Factor)factorlist.get(i);
            if(i!=0)
            {
            	factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
            	sexpr.append(factor.getLog());
            }
                
            sexpr.append(i+1);
            sfactor.append(factor.getFieldname().toUpperCase());
            sfactor.append(factor.getOper());
            sfactor.append(factor.getValue());  
            sfactor.append("`");            
        }
	}
	private void combineFactor2(ArrayList factorlist, StringBuffer sfactor, StringBuffer sexpr) {
		String query_type=(String)this.getFormHM().get("query_type");
		for(int i=0;i<factorlist.size();i++)
        {
			LazyDynaBean bean=(LazyDynaBean)factorlist.get(i);
    		if(bean==null)
    			continue;
    		String fieldname=(String)bean.get("fieldname");
    		String oper=(String)bean.get("oper");
    		String value=(String)bean.get("value");
    		if(value==null||value.length()<=0)
    			value="";
    		if(query_type!=null&& "1".equals(query_type))
    		{
    			String log=(String)bean.get("log");
    			if(i!=0)
                    sexpr.append(log);
    			sexpr.append(i+1);
    		}          
            
            sfactor.append(fieldname.toUpperCase());
            sfactor.append(oper);
            sfactor.append(value);  
            sfactor.append("`");            
        }
	}
	public void execute() throws GeneralException {
       // ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
		ArrayList arr=(ArrayList)this.getFormHM().get("arr");
		if(arr==null)
				throw GeneralExceptionHandler.Handle(new GeneralException("统计条件不能为空！"));
		//String factor=getFactor(factorlist);
        String expression=(String)this.getFormHM().get("expression");
        boolean bflag=false;
        if(!(expression==null||expression.length()==0))
        {
        	bflag=true;
        }
        
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
        //combineFactor2(factorlist, sfactor, sexpr); 
        combineFactor2(arr, sfactor, sexpr);   
        expressionvalidate(expression,arr.size(),sfactor.toString());
        String expr=null;
        if(!bflag)
        	expr=sexpr.toString()+"|"+sfactor.toString();
        else
        	expr=expression+"|"+sfactor.toString();
        this.getFormHM().put("expr",expr);
	}
	
	/**
	 * 组合条件
	 * @param arrList
	 * @return
	 */
    private String getFactor(ArrayList arrList)
    {
    	StringBuffer factor=new StringBuffer();
    	if(arrList==null)
    		return "";
    	for(int i=0;i<arrList.size();i++)
    	{
    		LazyDynaBean bean=(LazyDynaBean)arrList.get(i);
    		if(bean==null)
    			continue;
    		String fieldname=(String)bean.get("fieldname");
    		String oper=(String)bean.get("oper");
    		String value=(String)bean.get("value");
    		if(value==null||value.length()<=0)
    			value="Null";
    		factor.append(fieldname);
    		factor.append(oper);
    		factor.append(value);
    		factor.append("`");
    	}
    	if(factor.length()>0)
    		factor.setLength(factor.length()-1);
    	return factor.toString();
    }
	 private void expressionvalidate(String expression,int factorsize,String sfactor) throws GeneralException
	 {
	    	  /**为了分析用*/
		    String type=(String)this.getFormHM().get("type");
	        if(!isHaveExpression(expression,factorsize))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
	        expression=expression.replaceAll("!","-");
	        TSyntax syntax=new TSyntax();
	        if(!syntax.Lexical(expression))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	        if(!syntax.DoWithProgram())
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	        InfoUtils infoUtils=new InfoUtils();
	        if(!infoUtils.sqlCheckFactor(type, "Usr", expression, sfactor, userView, this.getFrameconn()))
	        {
	        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	        };
	  }
	 /**
	     * 分析表达式的合法式
	     * @param expression
	     * @param nmax　最大表达式因子号
	     * @return
	     */
	    private boolean isHaveExpression(String expression,int nmax)
	    {
	        boolean bflag=true;
	        String strlastno="";
	        int ncurr=0;
	        for(int i=0;i<expression.length();i++)
	        {
	          char v =expression.charAt(i);
	          if(((i+1)!=expression.length())&&(v>='0'&&v<='9'))
	          {
	            strlastno=strlastno+v;
	          }
	          else
	          {
	            if(v>='0'&&v<='9')
	            {
	              strlastno=strlastno+v;
	            }
	            if(!"".equals(strlastno))
	            {
	              ncurr=Integer.parseInt(strlastno);
	              if(ncurr>nmax)
	              {
	                  bflag=false;
	                  break;
	              }
	            }
	            strlastno="";
	          }
	        }        
	        return bflag;
	    }
}
