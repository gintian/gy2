package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:SimpleQueryTrans</p>
 * <p>Description:简单查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 06, 2005:10:34:40 AM</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SimpleQueryTrans extends IBusiness {

    /**
     * 
     */
    public SimpleQueryTrans() {
        super();
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
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
        String expression=(String)this.getFormHM().get("expression");
        String pos_id = (String)this.getFormHM().get("pos_id_value");
        if(factorlist==null)
            return;
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
        StringBuffer pos_cond=new StringBuffer();
        /**合成通用的表达式*/
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
         /*   if((factor.getFieldtype().equals("A")||factor.getFieldtype().equals("M")))
                sfactor.append("*");   */       
            sfactor.append(factor.getValue()); 
            if(factor.getValue().trim().length()>=1)
            {
            	if(("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
            		sfactor.append("*");
            }
            sfactor.append("`");            
        }
        sexpr.setLength(0);
        if(expression==null|| "".equals(expression))
           throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
        /**为了分析用*/
        if(!isHaveExpression(expression,factorlist.size()))
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
        expression=expression.replace('!','-');
        TSyntax syntax=new TSyntax();
        if(!syntax.Lexical(expression))
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
        if(!syntax.DoWithProgram())
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
        sexpr.append(expression);
        pos_cond.append(sexpr);
        pos_cond.append("|");
        pos_cond.append(sfactor);
        /**
         * 更新数据库表organization的pos_cond的字段
         */
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        try
        {
        	ArrayList list = new ArrayList();
        	String sql="update organization set pos_cond ='"+pos_cond+"' where codeitemid ='"+pos_id+"'";
        	dao.update(sql,list);
        }
        catch(Exception sqle)
        {
       	     sqle.printStackTrace();
    	     throw GeneralExceptionHandler.Handle(sqle);            
        }
    } 
}
