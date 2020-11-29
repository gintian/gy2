/*
 * Created on 2005-9-13
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title:QueryInterfaceTrans</p>
 * <p>Description:查询接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 12, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class QueryInterfaceTrans extends IBusiness {

	 /**
     * 
     */
    public QueryInterfaceTrans() {
    }
    
    /**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            /** for examples A01.A0405*/
            String fieldname=st.nextToken();
            pos=fieldname.indexOf(".");
            fieldname=fieldname.substring(pos+1);
            
            FieldItem item=DataDictionary.getFieldItem(fieldname);
            if(item==null)
                cat.debug("not find fielditem=["+fieldname+"]");
            list.add(item);
        }
        return list;
    }
    
    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
	public void execute() throws GeneralException {
		    RecordVo vo= ConstantParamter.getRealConstantVo("ZP_QUERY_TEMPLATE");
		    RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
	        if(vo!=null)
	        {
	            String strfields=vo.getString("str_value");
	            String dbpre=rv.getString("str_value");
	            ArrayList fieldlist=splitField(strfields);
	            this.getFormHM().put("fieldlist",fieldlist);
	            this.getFormHM().put("dbpre",dbpre);
	        }
	}

}
