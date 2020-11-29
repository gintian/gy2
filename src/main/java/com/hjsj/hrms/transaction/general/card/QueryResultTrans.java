 /*
 * Created on 2006-2-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.general.card;

 import com.hrms.frame.dao.ContentDAO;
 import com.hrms.frame.utility.DateStyle;
 import com.hrms.hjsj.sys.FieldItem;
 import com.hrms.hjsj.utils.FactorList;
 import com.hrms.struts.exception.GeneralException;
 import com.hrms.struts.exception.GeneralExceptionHandler;
 import com.hrms.struts.facade.transaction.IBusiness;
 import com.hrms.struts.taglib.CommonData;

 import java.util.ArrayList;
 import java.util.Date;
 import java.util.HashMap;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class QueryResultTrans extends IBusiness {

    /**
     * 
     */
    public QueryResultTrans() {
        super();
        // TODO Auto-generated constructor stub
    }
    /**分析字符串是否为数值型*/
    private boolean isnumber(String strvalue)
    {
        boolean bflag=true;
        try
        {
            Float.parseFloat(strvalue);
        }
        catch(NumberFormatException ne)
        {
            bflag=false;
        }
        return bflag;
    }
    /**分析日期型字段*/
    private void analyFieldDate(FieldItem item,StringBuffer strexpr,StringBuffer strfactor,int pos)
    {
        String s_str_date=item.getValue();
        String e_str_date=item.getViewvalue();
        s_str_date=s_str_date.replaceAll("\\.","-");
        e_str_date=e_str_date.replaceAll("\\.","-");
       
        Date s_date=DateStyle.parseDate(s_str_date);
        Date e_date=DateStyle.parseDate(e_str_date);
        
      
        /**起始日期及终止日期格式全对*/
        if(s_date!=null&&e_date!=null)
        {
            if(strexpr.length()==0)
            {
              strexpr.append(pos);
              strexpr.append("*");
              strexpr.append(pos+1);
            
            }
            else
            {
                strexpr.append("*(");                
                strexpr.append(pos);
                strexpr.append("*");
                strexpr.append(pos+1);  
                strexpr.append(")");
            }
            strfactor.append(item.getItemid().toUpperCase());
            strfactor.append(">=");
            strfactor.append(item.getValue());
            strfactor.append("`");
            strfactor.append(item.getItemid().toUpperCase());
            strfactor.append("<=");
            strfactor.append(item.getViewvalue()); 
            strfactor.append("`");            
        }
        else if (isnumber(s_str_date) && isnumber(e_str_date))
        {
            if(strexpr.length()==0)
            {
              strexpr.append(pos);
              strexpr.append("*");
              strexpr.append(pos+1);
            
            }
            else
            {
                strexpr.append("*(");                
                strexpr.append(pos);
                strexpr.append("*");
                strexpr.append(pos+1);  
                strexpr.append(")");
            }
            strfactor.append(item.getItemid().toUpperCase());
            strfactor.append(">=$YRS[");
            strfactor.append(item.getValue());
            strfactor.append("]`");
            strfactor.append(item.getItemid().toUpperCase());
            strfactor.append("<=$YRS[");
            strfactor.append(item.getViewvalue()); 
            strfactor.append("]`");               
        }
        else
        {
            if(strexpr.length()==0)
            {
              strexpr.append(pos);
            }
            else
            {
                strexpr.append("*");                
                strexpr.append(pos);
            }
            strfactor.append(item.getItemid().toUpperCase());
            strfactor.append("=");
            strfactor.append("`");
        }
        
    }
    
    /**组合查询SQL*/
    private String combine_SQL(ArrayList list,String like,String dbpre,String strInfr) throws GeneralException
    {
        int j=1;
        StringBuffer strexpr=new StringBuffer();
        StringBuffer strfactor=new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            /**如果值未填的话，default是否为不查*/
            if(item.getValue()==null|| "".equals(item.getValue()))
                continue;
            
            if("D".equals(item.getItemtype()))
            {
                analyFieldDate(item,strexpr,strfactor,j);
                j=j+2;
            }
            else
            {
	            /**组合表达式串*/
	            if(j==1)
	            {
	                strexpr.append(j);
	            }
	            else
	            {
	                strexpr.append("*");
	                strexpr.append(j);                
	            }
	            
	            if("A".equals(item.getItemtype())|| "M".equals(item.getItemtype()))
	            {
	                    if("1".equals(like))
	                    {
			                strfactor.append(item.getItemid().toUpperCase());
			                strfactor.append("=*");
			                strfactor.append(item.getValue());
			                strfactor.append("*`");	   
	                    }
	                    else
	                    {
			                strfactor.append(item.getItemid().toUpperCase());
			                strfactor.append("=");
			                strfactor.append(item.getValue());
			                strfactor.append("`");	  	                        
	                    }
	            }
	            else
	            {
	                strfactor.append(item.getItemid().toUpperCase());
	                strfactor.append("=");
	                strfactor.append(item.getValue());
	                strfactor.append("`");
	            }
	            ++j;	            
            }
        }//for i loop end.
        cat.debug("factor="+strfactor.toString());
        cat.debug("expression="+strexpr.toString());
        ArrayList fieldlist=new ArrayList();
        if(!userView.isSuper_admin())
        {
            String strpriv=userView.getPrivSQLExpression(strexpr.toString()+"|"+strfactor.toString(),dbpre,true,fieldlist);
            cat.debug("priv_strsql="+strpriv);
            return strpriv;
        }
        else
        {
        	FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,true,true,Integer.parseInt(strInfr),"zhangyi");
            //fieldlist=factorlist.getFieldList();
            return factorlist.getSqlExpression();
        }
    }
    /**
     * 根据输入的查询条件取得查询值
     * @param list
     * @return
     */
    private String query_Field(ArrayList list)
    {
        StringBuffer strfields=new StringBuffer();
        int j=0;
        for(int i=0;i<list.size();i++)
        {
            FieldItem item=(FieldItem)list.get(i);
            if(item.getValue()==null|| "".equals(item.getValue()))
                continue;            
            if(j!=0)
                strfields.append(",");
            ++j;
            strfields.append(item.getItemid());
        }
        return strfields.toString();
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {

        ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
        String like=(String)this.getFormHM().get("like");
        String dbpre=(String)this.getFormHM().get("userbase");
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        
        String strInfkind=(String)hm.get("inforkind");
        if(strInfkind==null|| "".equals(strInfkind))
            strInfkind="1";
        if(list==null)
            return;
        if(like==null|| "".equals(like))
            like="0";
        String strwhere=combine_SQL(list,like,dbpre,strInfkind);
        cat.debug("Module query's where="+strwhere);
        cat.debug("like="+like);
        StringBuffer sql=new StringBuffer();
        sql.append("select a0100,a0101 ");
        sql.append(strwhere);
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList personlist=new ArrayList();
        try
		{
        	this.frowset=dao.search(sql.toString());
        	String a0100=null;
        	while(this.frowset.next())
        	{
        		if(a0100==null)
        			a0100=this.frowset.getString("a0100");
        		 CommonData dataobj = new CommonData(this.frowset.getString("a0100"),this.frowset.getString("a0101"));
        		 personlist.add(dataobj);
        	}
        	cat.debug("----personlist.size()---->" + personlist.size());
        	this.getFormHM().put("personlist",personlist);
        	this.getFormHM().put("a0100",a0100);
        	
        }catch(Exception e)
		{
        	e.printStackTrace();
        	throw GeneralExceptionHandler.Handle(e);
        }
        
    }
  
}
