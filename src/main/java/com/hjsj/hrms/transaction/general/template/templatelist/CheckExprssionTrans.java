package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckExprssionTrans extends IBusiness{

	public void execute() throws GeneralException {

		String info = "0";
		String type="";
		try
		{
		    type=(String)this.getFormHM().get("type");
			String expr=(String)this.getFormHM().get("expr");
			expr = PubFunc.keyWord_reback(expr);
			int size = Integer.parseInt((String)this.getFormHM().get("size"));
			
			  if(expr==null|| "".equals(expr))
	               info=ResourceFactory.getProperty("errors.query.notexistexpr");
	            /**为了分析用*/
	            if(!isHaveExpression(expr,size))
	                info=ResourceFactory.getProperty("errors.query.notexistfactor");
	            if(this.checkexpr(expr, size))
	            	info=ResourceFactory.getProperty("errors.query.expression");
	            expr=expr.replace('!','-');
	            TSyntax syntax=new TSyntax();
	            if(!syntax.Lexical(expr))
	                info=ResourceFactory.getProperty("errors.query.expression");
	            if(!syntax.DoWithProgram())
	                info=ResourceFactory.getProperty("errors.query.expression");
	           
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("info",info);
		this.getFormHM().put("type",type);
		
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
        //1*2*3*4*5
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

    public boolean checkexpr(String expr,int size)
    {
    	boolean flag=false;
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
//    						if(temp.length()>1)  //带括号就会出问题
//    						{
//    							flag=true;
//    							break;
//    						}
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
