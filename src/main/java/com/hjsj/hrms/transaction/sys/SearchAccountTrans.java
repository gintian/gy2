package com.hjsj.hrms.transaction.sys;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.CombineFactor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
/**
 * <p>Title:SearchAccountTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:May 12, 2005:2:58:15 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchAccountTrans extends IBusiness {
    /**取得自助用户登录名字段*/
    private String getLoginUserName()
    {
        /**登录参数表,登录用户指定不是username or userpassword*/
        String username=null;
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
        if(login_vo==null)
        {
            username="username";
        }
        else
        {
            String login_name = login_vo.getString("str_value").toLowerCase();
            int idx=login_name.indexOf(",");

            if(idx==-1)
            {
                username="username";
            }
            else
            {
                username=login_name.substring(0,idx);
                if("#".equals(username)|| "".equals(username))
                	username="username";
                else
                {
                	FieldItem item=DataDictionary.getFieldItem(username);
                	if(item==null|| "0".equalsIgnoreCase(item.getUseflag()))
                		username="username";
                }
            }
            
        }  
        return username;
    }
    
    /**求得登录用户的应用库列表*/
    private ArrayList getLoginBaseList()throws GeneralException
    {
        /**登录参数表*/
        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        String A01 = login_vo.getString("str_value");
        /**系统所有存在的数据库列表usr,oth,trs,ret*/
        StringBuffer strsql=new StringBuffer();
        strsql.append("select pre,dbname from dbname order by dbid");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList dblist=new ArrayList();
        try
        {
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                String dbpre=this.frowset.getString("pre");
                /**权限分析*/
	                if((A01.indexOf(dbpre)!=-1)&&(userView.isSuper_admin()||userView.hasTheDbName(dbpre)))
	                {
	                	CommonData vo=new CommonData(this.frowset.getString("pre"),this.frowset.getString("dbname"));
	                	dblist.add(vo);
	                }
            }
            /**认为是在职人员库*/
            if(dblist.size()==0)
            {
                /*CommonData vo=new CommonData("usr",ResourceFactory.getProperty("label.sys.userbase"));
                dblist.add(vo); */          
            	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("sys.account.dbpre.msg"))); 
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	      	throw GeneralExceptionHandler.Handle(sqle);                
        }
        return dblist;
    }
    
    private String getFirstDbase(ArrayList dblist)
    {
    	CommonData vo=(CommonData)dblist.get(0);
    	return vo.getDataValue();
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
    	
    	 try
         {
	    	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    	String uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
	    	if(uplevel==null||uplevel.length()==0)
	    		uplevel="0";
	        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	        String a_code=(String)hm.get("a_code");
	        StringBuffer cond_str=new StringBuffer();
	        String dbpre=(String)this.getFormHM().get("dbpre");
	        String a0101=(String)this.getFormHM().get("a0101");
	        String type = (String)hm.get("type");
	        hm.remove("type");
	        /**是否要快速定位*/
	        boolean bquick=true;
	        
	        String factor = (String)this.getFormHM().get("factor");
	        factor = SafeCode.decode(factor);
	        String expr= (String)this.getFormHM().get("expr");
	        expr = SafeCode.decode(expr);
	        String history= (String)this.getFormHM().get("history");
	        String likeflag= (String)this.getFormHM().get("likeflag");
	        /**相关代码类及代码值*/
       
	        if(a_code==null|| "".equals(a_code))
	        {
	           /**超级用户组下的用户,关联了三员角色时*/	
	           if(!this.userView.isBThreeUser())
	        	   a_code="UN";
	           else
	           {
	        	   a_code="UN-1";	   /**为空特殊处理*/     	   
	           }
	        }
	        ArrayList dblist=getLoginBaseList();
	        if(a0101==null|| "".equals(a0101))
	        	bquick=false;
	        if(dbpre==null|| "".equals(dbpre))
	        {
	        	dbpre=getFirstDbase(dblist);
	        }
	        String codesetid=a_code.substring(0,2);
	        String codevalue=a_code.substring(2);
	        StringBuffer  strexpr=new StringBuffer();
	        StringBuffer  strfactor=new StringBuffer();
	        if("UN".equals(codesetid))
	        {
	        	if(codevalue==null|| "".equals(codevalue))
	        	{
		            strfactor.append("B0110=`B0110=");
		            strfactor.append(codevalue);
		            strfactor.append("*`");
		            if(bquick)
		            {
		            	strfactor.append("A0101=");
		            	strfactor.append(a0101);
		            	strfactor.append("*`");
		            }
		            strexpr.append("(1+2)");	
		            if(bquick)
		            	strexpr.append("*3");	
	        	}
	        	else
	        	{
		            strfactor.append("B0110=");
		            strfactor.append(codevalue);
		            strfactor.append("*`");
		            if(bquick)
		            {
		            	strfactor.append("A0101=");
		            	strfactor.append(a0101);
		            	strfactor.append("*`");
		            }		            
		            strexpr.append("1");
		            if(bquick)
		            	strexpr.append("*2");			            
	        	}
	        }
	        else if("UM".equals(codesetid))
	        {
	            strfactor.append("E0122=");
	            strfactor.append(codevalue);
	            strfactor.append("*`");
	            if(bquick)
	            {
	            	strfactor.append("A0101=");
	            	strfactor.append(a0101);
	            	strfactor.append("*`");
	            }	            
	            strexpr.append("1"); 
	            if(bquick)
	            	strexpr.append("*2");		            
	        }
	        else if("@K".equals(codesetid))
	        {
	            strfactor.append("E01A1=");
	            strfactor.append(codevalue);
	            strfactor.append("*`");
	            if(bquick)
	            {
	            	strfactor.append("A0101=");
	            	strfactor.append(a0101);
	            	strfactor.append("*`");
	            }		            
	            strexpr.append("1"); 
	            if(bquick)
	            	strexpr.append("*2");		            
	        }
	        else
	        {
	           strfactor.append("B0110=*`B0110=`");
	            if(bquick)
	            {
	            	strfactor.append("A0101=");
	            	strfactor.append(a0101);
	            	strfactor.append("*`");
	            }		           
	           strexpr.append("(1+2)");
	           if(bquick)
	            	strexpr.append("*3");		           
	        }
	        
	        String strLexpr=strexpr.toString();
	        String strFactor=strfactor.toString();
	        if(factor!=null&&factor.length()>0&&expr!=null&&expr.length()>0){
		        String[] style=getCombinLexprFactor(strexpr.toString(),strfactor.toString(),expr,factor);
			    if(style!=null && style.length==2)
			    {
			    	strLexpr=style[0];
			    	strFactor=style[1];
			    }
	        }
	        ArrayList fieldlist=new ArrayList();
	        String strwhere="";
	        if(!userView.isSuper_admin())
	        {
	            strwhere=userView.getPrivSQLExpression(strLexpr+"|"+strFactor,dbpre,"1".equals(history),"1".equals(likeflag),true,fieldlist);
	        }
	        else
	        {
	            FactorList factorlist=new FactorList(strLexpr,strFactor,dbpre,"1".equals(history),"1".equals(likeflag),true,1,userView.getUserName());
	            strwhere= factorlist.getSqlExpression();        	
	        }
            cat.debug("strwhere="+strwhere);
            
            
            
	        /**查询条件*/
	        this.getFormHM().put("tablename",dbpre+"A01");
	        this.getFormHM().put("cond_str",PubFunc.encrypt(strwhere)/*cond_str.toString()*/);
	        RecordVo vo = new RecordVo("usra01");
	        /**条件列表*/
	        StringBuffer strsql=new StringBuffer();
	        strsql.append("select distinct a0000,");
	        strsql.append(dbpre);
	        strsql.append("a01.a0100 a0100");
	        if(vo.hasAttribute("e0122"))
	        	strsql.append(",b0110,e0122,e01a1,a0101 ");
	        else
	        	strsql.append(",b0110,e01a1,a0101 ");
	        /**去除掉重复姓名字段*/	        
	        if(!"A0101".equalsIgnoreCase(getLoginUserName()))
	        {	        
	        	strsql.append(",");
	        	strsql.append(getLoginUserName());
	        }
	        
	        DbNameBo dbnamebo = new DbNameBo(this.frameconn);
	        String lockfield = dbnamebo.getLogonLockField();
	        if(lockfield.length()==5){
	        	strsql.append(",");
	        	strsql.append(lockfield);
	        }

	        this.getFormHM().put("sql_str",strsql.toString());
	        /**字段列表*/
	        strsql.setLength(0);
	        if(vo.hasAttribute("e0122"))
	        	strsql.append("a0100,b0110,e0122,e01a1,a0101,");
	        else
	        	strsql.append("a0100,b0110,e01a1,a0101,");
	        /**去除掉重复姓名字段*/
	        if(!"A0101".equalsIgnoreCase(getLoginUserName()))
	        {
	        	strsql.append(getLoginUserName());
	        	strsql.append(",");   
	        }
	        if(lockfield.length()==5){
	        	strsql.append(lockfield);
	        	strsql.append(","); 
	        }
	        //用户解锁|锁定
	        if(type!=null&&type.length()>0&&("1".equals(type)|| "2".equals(type))){
	        	ArrayList selectedlist=(ArrayList)this.getFormHM().get("selectedaccount");
	        	ArrayList values = new ArrayList();
	        	ArrayList a0101s = new ArrayList();
	        	ArrayList a0100s = new ArrayList();
	        	ArrayList usernames = new ArrayList();
	        	
	        	for(int i=0;i<selectedlist.size();i++)
	    		{
	    			DynaBean bean=(DynaBean)selectedlist.get(i);
	    			String a0100 = (String)bean.get("a0100");	
	    			ArrayList tmplist = new ArrayList();
	    			tmplist.add(a0100);
	    			values.add(tmplist);
	    			a0100s.add(a0100);
	    			a0101s.add((String)bean.get("a0101"));
	    			usernames.add((String)bean.get(getLoginUserName()));
	    		}
	        	ContentDAO dao = new ContentDAO(this.frameconn);
	        	dao.batchUpdate("update "+dbpre+"A01 set "+lockfield+"="+type+" where a0100=?", values);
	        	if("1".equals(type))
	        		this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.account.lock").replace("{0}", a0101s.toString()));
	        	else if("2".equals(type)){
	                this.getFormHM().put("@eventlog", ResourceFactory.getProperty("log.account.unlock").replace("{0}", a0101s.toString()));
	        	}
	        	/**登录参数表*/
        		String username="username";
                RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
                if(login_vo==null){
                }else{
	                String login_name = login_vo.getString("str_value");
	                int idx=login_name.indexOf(",");
	                if(idx==-1){}else{
		                username=login_name.substring(0,idx);
	                }
                }
                ArrayList valuess = new ArrayList();
                for(int i=0;i<usernames.size();i++){
                	String usern = (String)usernames.get(i);
        			if("2".equals(type)){
        				ConstantParamter.setUserAttribute(usern, "locked_login", "0");
        				com.hrms.hjsj.sys.SecurityLock.clearCounter(usern);
        				ArrayList usernamess = new ArrayList();
        				usernamess.add(usern);
        				valuess.add(usernamess);
        			}else{
        				ConstantParamter.setUserAttribute(usern, "locked_login", "1");
        			}
        		}
                if("2".equals(type)){
                	UserObjectBo userbo = new UserObjectBo(this.frameconn);
                	userbo.updatePWDModTime(valuess);
                }
	        }
	        
	        this.getFormHM().put("columns",strsql.toString());
	        this.getFormHM().put("dblist",dblist);
	        this.getFormHM().put("loguser",getLoginUserName());
	        this.getFormHM().put("dbpre",dbpre);
	        this.getFormHM().put("uplevel", uplevel);
	        this.getFormHM().put("lockfield", lockfield.toLowerCase());
       }
	   catch(Exception ee)
	   {
		   ee.printStackTrace();
		   throw GeneralExceptionHandler.Handle(ee);  
	   }
    }

  //合并表达式
	public String[] getCombinLexprFactor(String lexpr,String factor,String seclexpr,String secfactor)
	{
		//xus 19/7/24 【50270】v7.6.1封版：帐号分配，高级设置成两个条件，并且选择多个值，查询报越界的错。
		//多条件分隔符与"或"关系分隔符都为“|”造成混淆。现用"{split^symbol}"替换或分隔符
		secfactor = secfactor.replace("|","{split^symbol}");
		String[] style=new String[2];
		ArrayList lexprFactor=new ArrayList();
		factor = PubFunc.keyWord_reback(factor);
		lexprFactor.add(lexpr + "|" + factor);
		lexprFactor.add(seclexpr + "|" + secfactor);
		CombineFactor combinefactor=new CombineFactor();
		String lexprFactorStr=combinefactor.getCombineFactorExpr(lexprFactor,0);
		StringTokenizer Stok = new StringTokenizer(lexprFactorStr, "|");
		if(Stok.hasMoreTokens())
		{
			style[0]=Stok.nextToken();
			style[1]=Stok.nextToken();
		}
		style[1] = style[1].replace("{split^symbol}","|");
		return style;
	}
}
