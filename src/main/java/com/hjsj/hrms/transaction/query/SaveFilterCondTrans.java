package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.businessobject.ykcard.TSyntax;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveFilterCondTrans</p>
 * <p>Description:用于系统管理中的人员授权的高级条件设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 24, 2005:4:49:42 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SaveFilterCondTrans extends IBusiness {

    /**
     * 
     */
    public SaveFilterCondTrans() {
        super();
        // TODO Auto-generated constructor stub
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
    
    private void saveCondPriv(String role_id,String user_flag,String cond)
    {
        RecordVo vo=new RecordVo("t_sys_function_priv");
        vo.setString("id",role_id);
        vo.setString("condpriv",cond);
        vo.setString("status",user_flag/*GeneralConstant.ROLE*/);
        cat.debug("role_vo="+vo.toString());	
        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
        sysbo.save();         
    }
    private ArrayList getSavedFactorList(String flag,String a_id)
    {
    	ArrayList list=new ArrayList();
    	ArrayList factorslist=null;
    	ArrayList factorlist=new ArrayList();
    	StringBuffer strsql=new StringBuffer();
    	String strcond=null;
        ContentDAO dao=new ContentDAO(this.getFrameconn());    	
    	strsql.append("select condpriv from t_sys_function_priv where id=? and status=?");
    	list.add(a_id);
    	list.add(flag);
    	try
    	{
    		cat.debug("sql_priv="+strsql.toString());
    		this.frowset=dao.search(strsql.toString(),list);
    		if(this.frowset.next())
    			strcond=this.frowset.getString("condpriv");

    		if(!(strcond==null|| "".equals(strcond)))
    		{
    			int idx=0;
    			idx=strcond.indexOf("|");
    			String sfactor=strcond.substring(0,idx);
    			String sexpr=strcond.substring(idx+1);
        		cat.debug("condpriv_factor="+sfactor);    	
        		cat.debug("condpriv_expr="+sexpr);           		
    			factorslist=new FactorList(sexpr,sfactor,userView.getUserName());
//    			for(int j=0;j<factorslist.size();j++)
//    			{
//    				Factor vo=(Factor)factorslist.get(j);
//    				cat.debug("factor_vo="+vo.toString());
//    			}
    			/**返回到前台页面*/
                this.getFormHM().put("factorlist",factorslist);
                this.getFormHM().put("expression",sexpr);      			
    			//factorlist.addAll(factorslist);
    		}
    	}
    	catch(SQLException sqle)
    	{
    		sqle.printStackTrace();
    	}
		return factorslist;    	
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        /**取得人员标识和角色键值*/
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
        String user_flag=(String)hm.get("a_flag");
        String user_id=(String)this.getFormHM().get("role_id");
        //String user_id=(String)hm.get("a_roleid");
        cat.debug("user_id="+user_id);
        cat.debug("user_flag="+user_flag);
        if(user_id==null|| "".equals(user_id))
            return;
        //如果为空，则default为角色
        if(user_flag==null|| "".equals(user_flag))
            user_flag=GeneralConstant.ROLE;       
        ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
        String expression=(String)this.getFormHM().get("expression");
        expression=PubFunc.keyWord_reback(expression);
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
        /**合成通用的表达式*/
        for(int i=0;i<factorlist.size();i++)
        {
            Factor factor=(Factor)factorlist.get(i);
            sfactor.append(factor.getFieldname().toUpperCase());
            sfactor.append(PubFunc.keyWord_reback(factor.getOper()));
            sfactor.append(factor.getValue());  
            sfactor.append("`");        
        }
        if(factorlist==null||factorlist.size()==0)
        {
            saveCondPriv(user_id,user_flag,""); 
            return;
        }
        if(expression==null|| "".equals(expression))
        {
            saveCondPriv(user_id,user_flag,"");
            return;
        }
        /**为了分析用*/
        if(!isHaveExpression(expression,factorlist.size()))
        {
            //saveCondPriv(user_id,user_flag,"");            
        	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfactor"),"",""));
        }
        expression=expression.replaceAll("!","-");
        TSyntax syntax=new TSyntax();
        if(!syntax.Lexical(expression))
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
        if(!syntax.DoWithProgram())
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.expression"),"",""));
        sexpr.append(expression);
        sfactor.append("|");
        sfactor.append(sexpr);
        
        cat.debug("filtercond="+sfactor.toString().replaceAll("-","!"));        
        saveCondPriv(user_id,user_flag,PubFunc.keyWord_reback(sfactor.toString()).replaceAll("-","!"));
        getSavedFactorList(user_flag,user_id);
    }

}
