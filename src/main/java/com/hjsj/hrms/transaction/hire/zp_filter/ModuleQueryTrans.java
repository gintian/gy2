
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:ModuleQueryTrans</p>
 * <p>Description:模板查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 13, 2005:4:43:44 PM</p>
 * @author fengxin
 * @version 1.0
 * 
 */ 
public class ModuleQueryTrans extends IBusiness {

	  /**
     * 
     */
    public ModuleQueryTrans() {
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
        s_str_date=s_str_date.replace('.','-');
        e_str_date=e_str_date.replace('.','-');
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
			                strfactor.append(item.getValue().trim());
			                strfactor.append("*`");	   
	                    }
	                    else
	                    {
			                strfactor.append(item.getItemid().toUpperCase());
			                strfactor.append("=");
			                strfactor.append(item.getValue().trim());
			                strfactor.append("`");	  	                        
	                    }
	            }
	            else
	            {
	                strfactor.append(item.getItemid().toUpperCase());
	                strfactor.append("=");
	                strfactor.append(item.getValue().trim());
	                strfactor.append("`");
	            }
	            ++j;	            
            }
        }//for i loop end.
        cat.debug("factor="+strfactor.toString());
        cat.debug("expression="+strexpr.toString());
        ArrayList fieldlist=new ArrayList(); 
        //System.out.println("factor="+strfactor.toString());
        FactorList factorlist=new FactorList(strexpr.toString(),strfactor.toString(),dbpre,false,true,true,Integer.parseInt(strInfr),userView.getUserId());
        return factorlist.getSqlExpression();

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
        /**
         * 查询符合条件的人员列表
         */
        ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
        String dbpre = (String)this.getFormHM().get("dbpre");
        String like=(String)this.getFormHM().get("like");
        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
		String userbase = rv.getString("str_value");
        
        String strInfkind=(String)hm.get("inforkind");
        if(strInfkind==null|| "".equals(strInfkind))
            strInfkind="1";
        if(list==null)
            return;
        if(like==null|| "".equals(like))
            like="0";
        String substrwhere=combine_SQL(list,like,dbpre,strInfkind);
        int num = substrwhere.lastIndexOf("WHERE");
        StringBuffer strwhere = new StringBuffer();
        if(num == -1){
        	strwhere.append(substrwhere);
        	strwhere.append(" where "+userbase+"A01.a0100 not in (select a0100 from zp_pos_tache)");
        }else{
        	strwhere.append(substrwhere.substring(0,num+5));
        	strwhere.append("(");
        	strwhere.append(substrwhere.substring(num+5,substrwhere.length()));
        	strwhere.append(")");
        	strwhere.append(" and "+userbase+"A01.a0100 not in (select a0100 from zp_pos_tache)");
        }
        ArrayList resultlist=privFieldList(list,"1");
        /**同样根据是否填定查询值，取得查询指标*/
        String fields=query_Field(resultlist);
        StringBuffer strsql=new StringBuffer();
        strsql.append("select distinct a0000,");
        strsql.append(dbpre);
        strsql.append("a01.a0100 "); 
        strsql.append(",a0101 ");
        if(fields==null|| "".equals(fields))
        {
            strsql.append(" ");
        }
        else
        {
        	strsql.append(",");
        	strsql.append(fields);        	
        }
        strsql.append(" ");    
        this.getFormHM().put("cond_sql",strsql.toString());
        if(fields==null|| "".equals(fields))
        	fields="a0100,a0101,";        	
        else
        	fields="a0100,a0101,"+fields+",";
        this.getFormHM().put("columns",fields);
        this.getFormHM().put("strwhere",strwhere.toString());
        this.getFormHM().put("resultlist",resultlist);
    }
    /**
     * 
     * @param fieldlist
     * @param flag 1:人员2：单位3：职位
     * @return
     */
	private ArrayList privFieldList(ArrayList fieldlist,String flag) {
		ArrayList list=new ArrayList();
		/**权限分析*/
		for(int j=0;j<fieldlist.size();j++)
		{
			FieldItem fielditem=(FieldItem)fieldlist.get(j);
			String fieldname=fielditem.getItemid();
			/**为空则不用显示*/
            if(fielditem.getValue()==null|| "".equals(fielditem.getValue()))
                continue; 			
			if("a0101".indexOf(fieldname)!=-1)
				continue;
			fielditem.setPriv_status(Integer.parseInt(userView.analyseFieldPriv(fieldname)));
			list.add(fielditem);
		}
		return list;
	}  
}
