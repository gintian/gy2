/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Aug 10, 20064:26:28 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SaveQueryCondTrans extends IBusiness {

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
	
	public void execute() throws GeneralException {
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");	
        String expression=(String)this.getFormHM().get("expression");
        boolean bflag=false;
        if(!(expression==null||expression.length()==0))
        {
        	bflag=true;
        }
        
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
        combineFactor(factorlist, sfactor, sexpr);   
        expressionvalidate(expression,factorlist,sfactor.toString());
        String expr=null;
        if(!bflag)
        	expr=sexpr.toString()+"|"+sfactor.toString();
        else
        	expr=expression+"|"+sfactor.toString();
        this.getFormHM().put("expr",expr);
	}
	 private void expressionvalidate(String expression,ArrayList factorlist,String sfactor) throws GeneralException
	 {
	    	  /**为了分析用*/
		    String type=(String)this.getFormHM().get("type");
	        if(!isHaveExpression(expression,factorlist.size()))
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
