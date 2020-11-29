/**
 * 
 */
package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-3:17:21:06</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveGQueryCondTrans extends IBusiness {

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
	
    private int getMaxCondId(){
    	int nmax=0;
    	String strsql="select max(id) as nmax from lexpr";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		this.frowset=dao.search(strsql);
    		if(this.frowset.next())
    			nmax=this.frowset.getInt("nmax");
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    	return nmax+1;
    }
    
	public void execute() throws GeneralException {
		try
		{
			String condname=(String)this.getFormHM().get("condname");
			String condid=(String)this.getFormHM().get("condid");
	        String like=(String)this.getFormHM().get("like");
	        if(like==null|| "".equals(like))
	            like="0";
	        String likevalue=(String)this.getFormHM().get("likevalue");
	        if(likevalue==null|| "".equals(likevalue))
	        	likevalue="0";
	        String history=(String)this.getFormHM().get("history");
	        if(history==null|| "".equals(history))
	            history="0"; 
	        String historysave=(String)this.getFormHM().get("historysave"); //历史纪录查询保存后的
	        if(historysave==null|| "".equals(historysave))
	        	historysave="0";
	        String type=(String)this.getFormHM().get("type");
	        if(type==null|| "".equals(type))
	        	type="1";        
			if(condid==null|| "".equals(condid))
				condid= Integer.toString(getMaxCondId());
			String categories = (String)this.getFormHM().get("categories");
			categories = categories==null?"":categories;
			
	        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
	        String expression=(String)this.getFormHM().get("expression");
	        expression=PubFunc.keyWord_reback(expression);
	        StringBuffer sfactor=new StringBuffer();
	        StringBuffer sexpr=new StringBuffer();
	        rebackKeyword(factorlist);
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
	            if("M".equals(factor.getFieldtype()))
	            {
	            	throw new GeneralException(factor.getHz()+" "+ResourceFactory.getProperty("error.query.factor"));
	            }
	            if("1".equals(like)&&("A".equals(factor.getFieldtype())))
	                sfactor.append("*");
	            sfactor.append(factor.getValue());  
	            /**对字符型指标有模糊*/
	            if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
	                    sfactor.append("*");
	            sfactor.append("`");            
	        }
	        
	        /**通用查询时，表达式因子按用户填写进行分析处理*/
	        sexpr.setLength(0);
	        if(expression==null|| "".equals(expression))
	           throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistexpr"),"",""));
	        /**为了分析用*/
	        if(!isHaveExpression(expression,factorlist.size()))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
	        expression=expression.replaceAll("!","-");
	        TSyntax syntax=new TSyntax();
	        if(!syntax.Lexical(expression))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	        if(!syntax.DoWithProgram())
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
	        sexpr.append(expression);
	        /**通用查询结束**/
	        cat.debug("save_expr="+sexpr.toString());
	        cat.debug("save_factor="+sfactor.toString());
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			RecordVo vo=new RecordVo("lexpr");
			vo.setInt("id",Integer.parseInt(condid));
			
			/* 解决新加通用查询排序问题 xiaoyun 2014-4-29 start */
			/**delete*/
			//dao.deleteValueObject(vo); 
			// 判断该查询是否在数据库中存在
			boolean isExist = dao.isExistRecordVo(vo);
			if(!isExist) {
				this.frowset = dao.search("select max(norder) norder from lexpr");
				if(this.frowset.next()) {
					vo.setInt("norder", (this.frowset.getInt("norder")+1));
				}
			}
			/* 解决新加通用查询排序问题 xiaoyun 2014-4-29 end */
			vo.setString("name",condname);
			vo.setString("lexpr",sexpr.toString());
			vo.setString("factor",sfactor.toString());
			vo.setString("type",type);
			vo.setString("moduleflag","10000000000000000000");
//			vo.setString("history",history);    原来的
//			vo.setString("fuzzyflag","0");			默认：模糊跟历史查询都为 0；不管用户是否勾选
			vo.setString("history",historysave);
			vo.setString("fuzzyflag",likevalue);	
			vo.setString("categories",categories);	
			/* 解决新加通用查询排序问题 xiaoyun 2014-4-29 start */
			// 如果不存在，执行插入操作
			if(!isExist) {
				dao.addValueObject(vo);
			} else { // 如果存在，那么更新
				dao.updateValueObject(vo);
			}
			/* 解决新加通用查询排序问题 xiaoyun 2014-4-29 end */
			/**创建时保存*/
			UserObjectBo user_bo=new UserObjectBo(this.getFrameconn());
			user_bo.saveResource(vo.getString("id"),this.userView,IResourceConstant.LEXPR);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	
	private void rebackKeyword(ArrayList list){
		for(int i=0;i<list.size();i++){
			Factor factor = (Factor)list.get(i);
			String hz = factor.getHz();
			String oper = factor.getOper();
			String log = factor.getLog();
			String value = factor.getValue();
			String hzvalue = factor.getHzvalue();
			hz = PubFunc.hireKeyWord_filter_reback(hz);
			oper = PubFunc.hireKeyWord_filter_reback(oper);
			log = PubFunc.hireKeyWord_filter_reback(log);
			value = PubFunc.hireKeyWord_filter_reback(value);
			hzvalue = PubFunc.hireKeyWord_filter_reback(hzvalue);
			factor.setHz(hz);
			factor.setOper(oper);
			factor.setLog(log);
			factor.setValue(value);
			factor.setHzvalue(hzvalue);
		}
	}
}
