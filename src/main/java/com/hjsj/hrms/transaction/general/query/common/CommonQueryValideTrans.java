/**
 * 
 */
package com.hjsj.hrms.transaction.general.query.common;

import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:CommonQueryTrans</p>
 * <p>Description:简单及通用查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-4-28:14:07:04</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class CommonQueryValideTrans extends IBusiness {

	
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



  
	
	public void execute() throws GeneralException {
	try
	{
		String info ="";	
		String size=(String)this.getFormHM().get("size");
        String query_type=(String)this.getFormHM().get("query_type");
        String expression=(String)this.getFormHM().get("expression");
        /**查询类型，简单查询或通用查询*/
        if(query_type==null|| "".equals(query_type))
            query_type="1";
       
      
        
    
        /**通用查询时，表达式因子按用户填写进行分析处理*/
     
        if("2".equals(query_type))
        {
            if(expression==null|| "".equals(expression))
            	info =ResourceFactory.getProperty("errors.query.notexistexpr");
            /**为了分析用*/
            if(!isHaveExpression(expression,Integer.parseInt(size)))
            	info =ResourceFactory.getProperty("errors.query.notexistfactor");
            if(this.checkexpr(expression,Integer.parseInt(size)))
            	info=ResourceFactory.getProperty("errors.query.expression");
            expression=expression.replaceAll("!","-");
            TSyntax syntax=new TSyntax();
            if(!syntax.Lexical(expression))
            	info =ResourceFactory.getProperty("errors.query.expression");
            if(!syntax.DoWithProgram())
            	info =ResourceFactory.getProperty("errors.query.expression");
        }
        
        /**通用查询结束**/
     
      this.getFormHM().put("info", info);
        
        
       
	}
	catch(Exception ex)
	{
		ex.printStackTrace();
		throw GeneralExceptionHandler.Handle(ex);
	}
	}

	   public boolean checkexpr(String expr,int size)
	    {
			int temp1=0;
	    	int temp2=0;
	    	for(int i=0;i<expr.length();i++){
	    		if("(".equals(""+expr.charAt(i))){
	    			temp1++;
	    		}
	    		if(")".equals(""+expr.charAt(i))){
	    			temp2++;
	    		}
	    	}
	    	boolean flag=false;
	    	try
	    	{
	    		if(expr.endsWith("+")||expr.endsWith("*")||(expr.endsWith("!")))
	    		{
	    			flag=true;
	    			return flag;
	    		}
	    		else if(expr.startsWith("+")||expr.startsWith("*"))
	    		{
	    			flag=true;
	    			return flag;
	    		}
	    		else if(expr.indexOf("+*")!=-1||expr.indexOf("*+")!=-1||expr.indexOf("*-")!=-1||expr.indexOf("-*")!=-1||expr.indexOf("-+")!=-1||expr.indexOf("+-")!=-1)
	    		{
	    			flag=true;
	    			return flag;
	    		}
	    		else if(temp1!=temp2)
	    		{
	    			flag=true;
	    			return flag;
	    		}
	    		else
	    		{
	    			int j=1;
	    			for(int i=1;i<=size;i++)
	    			{
	    				j=i+1;
	    				if(j>size)
	    					break; 
	    				int h=expr.indexOf(i+"");
	    				if(i>=10)
	    					h=expr.indexOf(i+"")+1;
	    				String temp=expr.substring(h+1,expr.indexOf(j+""));
	    				if(temp==null||temp.length()==0)
	    				{
	    					flag=true;
	    					break;
	    				}
	    				else
	    				{
	    					/**有逻辑非运算符*/
	    					if(temp.indexOf("!")!=-1)
	    					{
	    						if(temp.indexOf("+")==-1&&temp.indexOf("*")==-1)
	    						{
	    							flag = true;
	    							break;
	    						}
	    						else
	    						{
	    							if(temp.indexOf("+")!=-1)
	    							{
	    								if(temp.indexOf("+")>temp.indexOf("!"))
	    								{
	    									flag=true;
	    									break;
	    								}
	    							}
	    							if(temp.indexOf("*")!=-1)
	    							{
	    								if(temp.indexOf("*")>temp.indexOf("!"))
	    								{
	    									flag=true;
	    									break;
	    								}
	    							}
	    						}
	    					}
	    					else
	    					{
//	    						if(temp.length()>1)  //带括号就会出问题
//	    						{
//	    							flag=true;
//	    							break;
//	    						}
	    					}
	    				}
	    				
	    				
	    			}
	    		}
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return flag;
	    }
   
	
}
