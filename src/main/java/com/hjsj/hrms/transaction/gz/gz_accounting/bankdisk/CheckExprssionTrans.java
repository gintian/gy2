package com.hjsj.hrms.transaction.gz.gz_accounting.bankdisk;

import com.hjsj.hrms.businessobject.gz.BankDiskSetBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckExprssionTrans extends IBusiness{

	public void execute() throws GeneralException {

		String info = "0";
		String type="";
		try
		{
			ArrayList arr=(ArrayList)this.getFormHM().get("arr");
			String expr=(String)this.getFormHM().get("expr");
			String sid=(String)this.getFormHM().get("sid");
			String fromflag=(String)this.getFormHM().get("fromflag");


			expr=PubFunc.keyWord_reback(expr);
			if(arr==null||expr==null|| "".equals(expr)){
					info="查询条件和因子表达式不能为空！";
			}else{
		    	 for(int i=0;i<arr.size();i++)
		         {
		            LazyDynaBean bean=(LazyDynaBean)arr.get(i);
	    	        bean.set("oper", PubFunc.keyWord_reback((String)bean.get("oper")));
	    	        bean.set("log",PubFunc.keyWord_reback((String)bean.get("log")));
	    	     }
    		    type=(String)this.getFormHM().get("type");
    		   
    		    StringBuffer sfactor=new StringBuffer();
	    	    StringBuffer sexpr=new StringBuffer(expr);
	    	    if("hire".equalsIgnoreCase(fromflag))
	    	    {
	    	    	combineFactor2(arr, sfactor, sexpr);   
    		        info=expressionvalidate(expr,arr.size(),sfactor.toString());
	    	    } else if("gz".equalsIgnoreCase(fromflag))
	    	    {
	    	    	 BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
	    	    	 SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(sid), this.userView);
	    	    	 String tableName=gzbo.getGz_tablename();
	   			     HashMap fieldItemMap=  bo.getFieldItemMap(Integer.parseInt(sid),this.userView);
	    	         info=this.combineFactor(arr, sfactor, expr, fieldItemMap, tableName);
	    	    }else if("history".equalsIgnoreCase(fromflag)){//history 表示为薪资历史数据分析进入
					BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn());
					HashMap fieldItemMap=  new HashMap();
					String[] sids=sid.split(",");
					for (String id : sids) {
						if(StringUtils.isNotBlank(id)){
							fieldItemMap.putAll( bo.getFieldItemMap(Integer.parseInt(id), this.userView));
						}
					}
					info=this.combineFactor(arr, sfactor, expr, fieldItemMap, "salaryarchive");
				}
			}
 	           
		}
		catch(Exception e)
		{
			info="1";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("info",info);
		this.getFormHM().put("type",type);
		
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
    		oper=PubFunc.keyWord_reback(oper);
    		String value=(String)bean.get("value");
    		if(value==null||value.length()<=0)
    			value="";
    		if(query_type!=null&& "1".equals(query_type))
    		{
    			String log=(String)bean.get("log");
    			log=PubFunc.keyWord_reback(log);
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
	 private String expressionvalidate(String expression,int factorsize,String sfactor) throws GeneralException
	 {
		 String str="0";
	    	  /**为了分析用*/
		    String type="1";
	        if(!isHaveExpression(expression,factorsize))
	            str=ResourceFactory.getProperty("errors.query.notexistfactor");
	        expression=expression.replaceAll("!","-");
	        TSyntax syntax=new TSyntax();
	        if(!syntax.Lexical(expression))
	            str=ResourceFactory.getProperty("errors.query.expression");
	        if(!syntax.DoWithProgram())
	            str=ResourceFactory.getProperty("errors.query.expression");
	        InfoUtils infoUtils=new InfoUtils();
	        if(!infoUtils.sqlCheckFactor(type, "Usr", expression, sfactor, userView, this.getFrameconn()))
	        {
	        	str=ResourceFactory.getProperty("errors.query.expression");
	        }
	        return str;
	  }
	 private String combineFactor(ArrayList factorlist,StringBuffer sfactor, String sexpr,HashMap fieldItemMap,String tableName) throws GeneralException
		{
			String str ="0";
			try
			{
				if(!isHaveExpression(sexpr,factorlist.size()))
		            str=ResourceFactory.getProperty("errors.query.notexistfactor");
				sexpr=sexpr.replaceAll("!","-");
		        TSyntax syntax=new TSyntax();
		        if(!syntax.Lexical(sexpr))
		            str=ResourceFactory.getProperty("errors.query.expression");
		        if(!syntax.DoWithProgram())
		            str=ResourceFactory.getProperty("errors.query.expression");
		        for(int i=0;i<factorlist.size();i++)
		        {
					LazyDynaBean bean=(LazyDynaBean)factorlist.get(i);
		    		if(bean==null)
		    			continue;
		    		String fieldname=(String)bean.get("fieldname");
		    		String oper=(String)bean.get("oper");
		    		oper=PubFunc.keyWord_reback(oper);
		    		String value=(String)bean.get("value");
		    		if(value==null||value.length()<=0)
		    			value="";
		    		/*if(query_type!=null&&query_type.equals("1"))
		    		{
		    			String log=(String)bean.get("log");
		    			log=PubFunc.keyWord_reback(log);
		    			if(i!=0)
		                    sexpr.append(log);
		    			sexpr.append(i+1);
		    		}        */  
		            
		            sfactor.append(fieldname.toUpperCase());
		            sfactor.append(oper);
		            sfactor.append(value);  
		            sfactor.append("`");            
		        }
		         FactorList factor_bo=new FactorList(sexpr.toString(),sfactor.toString().toUpperCase(),this.userView.getUserId(),fieldItemMap);
	  	         String strwhere=factor_bo.getSingleTableSqlExpression(tableName);
	  	        String sql="select 1 from "+tableName+" where 1=1 and ("+strwhere+")";
	            ContentDAO dao=new ContentDAO(this.getFrameconn());
	            boolean isCorrect=false;
	            try
	            {
	            	dao.search(sql);
	            	isCorrect=true;
	            }catch(Exception e)
	            {
	            	e.printStackTrace();
	            	//throw GeneralExceptionHandler.Handle(e);
	            }
	            if(!isCorrect)
	            	str=ResourceFactory.getProperty("errors.query.expression");
			}catch(Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return str;
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
//    				     	if(temp.length()>1)  //带括号就会出问题
//    						{
//							flag=true;
//							break;
//						}
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
