package com.hjsj.hrms.transaction.performance.implement.query;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Factor;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 考核表分发条件选择
 * 
 * @author: JinChunhai
 */

public class searchQueryObjectTrans extends IBusiness 
{

	public searchQueryObjectTrans() 
	{
		super();
		// TODO Auto-generated constructor stub
	}

	public void execute() throws GeneralException 
	{
		
		try
		{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
		if(factorlist==null)
            return;
		String plan_id=(String)this.getFormHM().get("plan_id");
		String accordByDepartment = (String)this.getFormHM().get("accordByDepartment");		
		String history=(String)this.getFormHM().get("history");
        String like=(String)this.getFormHM().get("like");
        String clear=(String)this.getFormHM().get("clear");  //是否删除当前计划已有对象
        if(clear==null|| "".equals(clear))
        	clear="0";
        if(history==null|| "".equals(history))
            history="0";
        if(like==null|| "".equals(like))
            like="0";
        
		String flag=(String)this.getFormHM().get("flag");
		String objectType=(String)this.getFormHM().get("objectType");
		StringBuffer e0122Whl=new StringBuffer();
        if("2".equals(flag)&& "2".equals(objectType))//人员计划条件选择主体
        {
            if(accordByDepartment==null|| "".equals(accordByDepartment))
            	accordByDepartment="0";
            if("1".equals(accordByDepartment))
            {
            	 String object_id = (String)this.getFormHM().get("object_id");
                 
                 StringBuffer sqlStr = new StringBuffer();
                 sqlStr.append( "select codeitemid,parentid,codesetid from organization where upper(codesetid) ='UM' or upper(codesetid)='UN' order by codesetid ");
     	    	this.frowset = dao.search(sqlStr.toString());
     	    	HashMap codesetMap = new HashMap();
     	    	HashMap parentMap = new HashMap();
     	 		while (this.frowset.next())
     	 		{
     	 			codesetMap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codesetid"));
     	 			parentMap.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
     	 		}  
     	 		
     		    sqlStr.setLength(0);
     		    sqlStr.append("select e0122 from per_object where plan_id="+plan_id+" and object_id='"+object_id+"'");
     		    this.frowset  = dao.search(sqlStr.toString());
     		    
     		   	if(this.frowset.next())
     		   	{
     		   		String e0122 = this.frowset.getString(1);
     		   		e0122Whl.append(",'"+e0122+"'");
     		   		
     		   		while(parentMap.get(e0122)!=null && "UM".equalsIgnoreCase((String)codesetMap.get((String)parentMap.get(e0122))))
     		   		{
     		   			e0122 = (String)parentMap.get(e0122);
     		   			e0122Whl.append(",'"+e0122+"'");
     		   		}
     		   	}
            }	   	
        }else
        {
        	accordByDepartment="0";
        }
        
        StringBuffer sfactor=new StringBuffer();
        StringBuffer sexpr=new StringBuffer();
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
            sfactor.append(PubFunc.keyWord_reback(factor.getOper()));

            if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
                sfactor.append("*");            
            sfactor.append(factor.getValue());  
            /**对字符型指标有模糊*/
            if("1".equals(like)&&("A".equals(factor.getFieldtype())|| "M".equals(factor.getFieldtype())))
                    sfactor.append("*");
            sfactor.append("`");  
          
        }
        
        String strwhere="";
        ArrayList fieldlist=new ArrayList();
        boolean bhis=false;
        boolean blike=false;
        if("1".equals(history))
        	bhis=true;
        if("1".equals(like))
        	blike=true;
        
//        if(!userView.isSuper_admin())
//        {       	
//            strwhere=userView.getPrivSQLExpression(sexpr.toString()+"|"+sfactor.toString(),"Usr",bhis,fieldlist);
//        }
//        else
//        {
        	FactorList factorslist=new FactorList(sexpr.toString(),sfactor.toString(),"Usr",bhis ,blike,true,1,"zhangyi");
            fieldlist=factorslist.getFieldList();
            strwhere=factorslist.getSqlExpression();  
            
            PerformanceImplementBo bo1=new PerformanceImplementBo(this.getFrameconn());
    	    String whl = bo1.getPrivWhere(userView);//根据用户权限先得到一个考核对象的范围
    	    strwhere+=whl;
            
//        }
    	    if(e0122Whl.length()>0)
    	    	 strwhere+=" and e0122 in ("+e0122Whl.substring(1)+")";
        
        this.getFormHM().put("str_sql","select distinct UsrA01.A0100 A0100,B0110,E0122,E01A1,A0101,A0000 ");
        this.getFormHM().put("str_whl",strwhere.toString());
		}
    	catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
	
	
}
