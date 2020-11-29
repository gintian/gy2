/**
 * 
 */
package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class ValidateExpressionTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		  ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");  //cmq removed it 
		  isUnbindfactor(factorlist);
          String expression=(String)this.getFormHM().get("expression");
          expression=PubFunc.keyWord_reback(expression);
          String like=(String)this.getFormHM().get("like");
          String historysave=(String)this.getFormHM().get("history");
          expressionvalidate(expression,factorlist);
          this.getFormHM().put("likevalue", like);
          this.getFormHM().put("historysave", historysave);
	}
	    private void expressionvalidate(String expression,ArrayList factorlist) throws GeneralException
	    {
	    	  /**为了分析用*/
	        if(!isHaveExpression(expression,factorlist.size()))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
	        expression=expression.replaceAll("!","-");
	        TSyntax syntax=new TSyntax();
	        if(!syntax.Lexical(expression))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	        if(!syntax.DoWithProgram())
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
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
	    private boolean isUnbindfactor(ArrayList factorlist)
	    {
	        boolean bflag=true;
	        ArrayList list=new ArrayList();
	        if(factorlist==null||factorlist.size()<=0)
	        	return false;
	        for(int i=0;i<factorlist.size();i++)
	        {
	        	 Factor factor=(Factor)factorlist.get(i);
	             factor.setLog(PubFunc.keyWord_reback(factor.getLog()));
	             factor.setOper(PubFunc.keyWord_reback(factor.getOper()));
	             list.add(factor);
	        }
	        this.getFormHM().put("factorlist",list);
	        return bflag;
	    }
}
