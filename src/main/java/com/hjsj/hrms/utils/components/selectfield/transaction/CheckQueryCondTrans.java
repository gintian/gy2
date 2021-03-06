package com.hjsj.hrms.utils.components.selectfield.transaction;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 常用查询
 * create time at:Aug 10, 20064:26:28 PM
 * @author chenmengqing
 * @version 4.0
 */
public class CheckQueryCondTrans extends IBusiness { 

	public void execute() throws GeneralException {
		try {
			ArrayList arr=(ArrayList)this.getFormHM().get("arr");
			Boolean needValidate=(Boolean)this.getFormHM().get("needValidate");//是否需要校验
			if(arr==null)
				throw GeneralExceptionHandler.Handle(new GeneralException("统计条件不能为空！"));
			String expression=(String)this.getFormHM().get("expression");
			expression = SafeCode.decode(expression);
			expression=PubFunc.keyWord_reback(expression);
			StringBuffer sfactor=new StringBuffer();
			/*for(int i=0;i<arr.size();i++)
        {
        	MorphDynaBean bean=(MorphDynaBean)arr.get(i);
        	bean.set("oper", PubFunc.keyWord_reback((String)bean.get("oper")));
        	bean.set("log",PubFunc.keyWord_reback((String)bean.get("log")));
        }*/
			combineFactor(arr, sfactor);   
			if(needValidate.booleanValue()){
				if(StringUtils.isBlank(expression))
					throw GeneralExceptionHandler.Handle(new Exception("因式表达式不能为空！"));
				expressionvalidate(expression,arr.size(),sfactor.toString());
				String expr=null;
				expr=expression+"|"+sfactor.toString();
				this.getFormHM().put("expr",SafeCode.encode(expr));
			}else{
				this.getFormHM().put("cexpr",SafeCode.encode(expression));
				this.getFormHM().put("condStr",SafeCode.encode(sfactor.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @param factorlist
	 * @param sfactor
	 * @param sexpr
	 */
	private void combineFactor(ArrayList factorlist, StringBuffer sfactor) {
		for(int i=0;i<factorlist.size();i++)
        {
			MorphDynaBean bean=(MorphDynaBean)factorlist.get(i);
    		if(bean==null)
    			continue;
    		String fieldname=(String)bean.get("fieldname");
    		String oper=(String)bean.get("oper");
    		oper=PubFunc.keyWord_reback(oper);
    		String value=(String)bean.get("value");
    		if(StringUtils.isBlank(value))
    			value="";
    		else{
    			String[] values = value.split("`");
    			if(values.length > 0){
    				value = values[0];
    			}else
    				value="";
    		}
            
            sfactor.append(fieldname.toUpperCase());
            sfactor.append(oper);
            sfactor.append(value);  
            sfactor.append("`");            
        }
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
    
    /**
     * @author lis
     * @Description: 校验表达式
     * @date 2016-2-16
     * @param expression
     * @param factorsize
     * @param sfactor
     * @throws GeneralException
     */
	 private void expressionvalidate(String expression,int factorsize,String sfactor) throws GeneralException
	 {
	    	  /**为了分析用*/
		    String type=(String)this.getFormHM().get("type");
	        if(!isHaveExpression(expression,factorsize))//分析表达式的合法式
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
