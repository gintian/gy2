/*
 * Created on 2005-8-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title:SearchBaseOptionsTrans</p>
 * <p>Description:查询人才库参数</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SearchBaseOptionsTrans extends IBusiness {

	/**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
        strfields=strfields+",";
        int pos=0;
        StringTokenizer st = new StringTokenizer(strfields, ",");
        while (st.hasMoreTokens())
        {
            String fieldname=st.nextToken();
            list.add(fieldname);
        }
        return list;
    }
	public void execute() throws GeneralException {
		ArrayList subSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
		this.getFormHM().put("subSetList",subSetList);
		String[] fieldsetvalue = null;
		RecordVo vo= ConstantParamter.getRealConstantVo("ZP_DBNAME");
	    RecordVo rv= ConstantParamter.getRealConstantVo("ZP_SUBSET_LIST");
	    ArrayList list = new ArrayList();   
        if(rv != null && !"".equals(rv)){
        	String setlist=rv.getString("str_value");
        	ArrayList fieldlist=splitField(setlist);
        	fieldsetvalue = new String[fieldlist.size()];
        	for(int i=0;i<fieldlist.size();i++){
        		fieldsetvalue[i] = (String)fieldlist.get(i);
        	}
        	this.getFormHM().put("fieldsetvalue",fieldsetvalue);
        	this.getFormHM().put("userBase",vo.getString("str_value"));
        	
        }
	}

}
