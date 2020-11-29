package com.hjsj.hrms.transaction.query;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:QueryCondDesignTrans</p>
 * <p>Description:查询条件定义</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 21, 2005:9:23:20 AM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class QueryCondDesignTrans extends IBusiness {

    /**
     * 
     */
    public QueryCondDesignTrans() {
        super();
    }
    /**
     * 查找是否存在相同的因子对象
     * @param name
     * @param list
     * @return
     */
    private Factor findFactor(String name,ArrayList list,int index)
    {
    	Factor factor=null;
    	for(int i=0;i<list.size();i++)
    	{
    		factor=(Factor)list.get(i);
    		if(name.equalsIgnoreCase(factor.getFieldname())&&(i==index))
    			break;
    		factor=null;
    	}
    	return factor;
    }
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        String[] fields=(String[])this.getFormHM().get("right_fields");
        
        String strexprsave=(String)this.getFormHM().get("expression");
        if(fields==null||fields.length==0)
        {
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfield"),"",""));
        }
        int j=0;
        StringBuffer strexpr=new StringBuffer();
        ArrayList<Factor> list =new ArrayList<Factor>();
        /**信息类型定义default=1（人员类型）*/
        int nInform=1;
        try
        {
            /**保存的因子对象列表*/
        	ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
            /**应用库过滤前缀符号*/
            ArrayList dblist=userView.getPrivDbList();
            StringBuffer cond=new StringBuffer();
            cond.append("select pre,dbname from dbname where pre in (");
            for(int i=0;i<dblist.size();i++)
            {
                if(i!=0)
                    cond.append(",");
                cond.append("'");
                cond.append((String)dblist.get(i));
                cond.append("'");
            }
            if(dblist.size()==0)
                cond.append("''");
            cond.append(")");
            cond.append(" order by dbid");
            /**应用库前缀过滤条件*/
            this.getFormHM().put("dbcond",cond.toString());
            /**定义条件项*/
            FieldItem item=null;
            for(int i=0;i<fields.length;i++)
            {
                String fieldname=fields[i];
                if(fieldname==null|| "".equals(fieldname))
                    continue;
                cat.debug("field_name="+fieldname);
                item=DataDictionary.getFieldItem(fieldname.toUpperCase());
                Factor factor=null;
                if(item!=null)
                {
                	/**已定义的因子再现*/
                	if(factorlist!=null)
                	{
                		factor=findFactor(fieldname,factorlist,i);
                		if(factor!=null)
                		{
                			list.add(factor);
                		    ++j;
                            strexpr.append(j);
                            strexpr.append("*");
                			continue;
                		}
                	}
                    factor=new Factor(nInform);
                    factor.setCodeid(item.getCodesetid());
                    factor.setFieldname(item.getItemid());
                    factor.setHz(item.getItemdesc());
                    factor.setFieldtype(item.getItemtype());
                    factor.setItemlen(item.getItemlength());
                    factor.setItemdecimal(item.getDecimalwidth());
                    factor.setOper("=");//default
                    factor.setLog("*");//default
                    list.add(factor);
                    ++j;
                    strexpr.append(j);
                    strexpr.append("*");
                }                
            }
            
            if(strexpr.length()>0)
            	strexpr.setLength(strexpr.length()-1);
            
            ArrayList<String> partList = new ArrayList<String>();
    		//启用标识
    		partList.add("flag");
    		//兼职单位标识
    		partList.add("unit");
    		//兼职子集
    		partList.add("setid");
    		String flag = "false";
    		String part_unit = "";
			Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
			HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME, partList);
			if (map != null && map.size() != 0) {
				if (map.get("flag") != null && ((String) map.get("flag")).trim().length() > 0)
					flag = (String) map.get("flag");
				if (flag != null && "true".equalsIgnoreCase(flag)) {
					if (map.get("unit") != null && ((String) map.get("unit")).trim().length() > 0)
						part_unit = (String) map.get("unit");
				}
			}
        	this.getFormHM().put("part_unit", part_unit.toLowerCase());
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            this.getFormHM().put("factorlist",list);
            if(!(strexprsave==null|| "".equals(strexprsave)))  //chenmengqing changed at 20070601
            	this.getFormHM().put("expression",strexprsave);            	
            else
            	this.getFormHM().put("expression",strexpr.toString());
        }

    }

}
