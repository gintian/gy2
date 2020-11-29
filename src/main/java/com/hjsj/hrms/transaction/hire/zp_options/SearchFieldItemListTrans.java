/*
 * Created on 2005-10-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchFieldItemListTrans extends IBusiness {

	/**根据传过的的指标串，分解成对应的指标对象*/
    private ArrayList splitField(String strfields)
    {
        ArrayList list=new ArrayList();
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
		String[] fielditemvalue = null;
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String fieldsetid=(String)hm.get("a_id");
        String fieldsetidvalue=(String)this.getFormHM().get("fieldsetid");        
        RecordVo vo= ConstantParamter.getRealConstantVo("ZP_FIELD_LIST");
        if(vo != null && !"".equals(vo)){
        	String fieldlist=vo.getString("str_value");
        	int fieldindex = -1;
        	if(fieldsetid != null && !"".equals(fieldsetid)){
        		fieldindex = fieldlist.indexOf(fieldsetid);
        	}else{
        		fieldindex = fieldlist.indexOf(fieldsetidvalue);
        	}
        	if(fieldindex != -1){
        		String substr = fieldlist.substring(fieldindex,fieldlist.length());
        		int subindex = substr.indexOf("},");
        		String midstr = fieldlist.substring(fieldindex+4,fieldindex+subindex);
        		ArrayList field=splitField(midstr);
        		fielditemvalue = new String[field.size()];
        		for(int i=0;i<field.size();i++){
        			fielditemvalue[i] = (String)field.get(i);
            	}
        	}
        }
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        String sql = "select itemid,itemdesc from fielditem where fieldsetid = '"+fieldsetid+"'";
        try{
           this.frowset = dao.search(sql);       
            while(this.frowset.next()){
           	  HashMap hmp = new HashMap();
           	  hmp.put("itemid",this.getFrowset().getString("itemid"));
           	  hmp.put("itemdesc",this.getFrowset().getString("itemdesc"));
           	  list.add(hmp);	  
           }
        }catch(SQLException sqle){
        	sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);
        }finally{
        	this.getFormHM().put("fieldItemList",list);
        	this.getFormHM().put("fielditemvalue",fielditemvalue);
        	this.getFormHM().put("fieldsetid",fieldsetid);
        }
        
	}

}
