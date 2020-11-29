package com.hjsj.hrms.transaction.general.statics;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Factor;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class BackQueryCondDesignTrans extends IBusiness{
	public BackQueryCondDesignTrans() {
        super();
    }
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
  
        String flisst=(String)this.getFormHM().get("infor_Flag");
        if(fields==null||fields.length==0)
        { 
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("errors.query.notexistfield"),"",""));
        }
        int j=0;
        StringBuffer strexpr=new StringBuffer();
        ArrayList list =new ArrayList();
        int nInform=1;
        try
        {
        	ArrayList factorlist=(ArrayList)this.getFormHM().get("factorlist");
            ArrayList dblist=userView.getPrivDbList();
    		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
    		dblist=dbvo.getDbNameVoList(dblist);
    		ArrayList lists=new ArrayList();
    		for(int i=0;i<dblist.size();i++)
    		{
    			CommonData vo=new CommonData();
    			RecordVo dbname=(RecordVo)dblist.get(i);
    			vo.setDataName(dbname.getString("dbname"));
    			vo.setDataValue(dbname.getString("pre"));
    			lists.add(vo);
    		}
    		this.getFormHM().put("dblist",lists);
    		this.getFormHM().put("flist",flisst);
            FieldItem item=null;
            for(int i=0;i<fields.length;i++)
            {
                String fieldname=fields[i];
                if(fieldname==null|| "".equals(fieldname))
                    continue;
                item=DataDictionary.getFieldItem(fieldname.toUpperCase());
                Factor factor=null;
                if(item!=null)
                {
                	if(factorlist!=null)
                	{
                		factor=findFactor(fieldname,factorlist,i);
                		if(factor!=null)
                		{
                			list.add(factor);
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
                    factor.setOper("=");
                    factor.setLog("*");
                    list.add(factor);
                    ++j;
                    strexpr.append(j);
                    strexpr.append("*");
                }                
            }
            if(strexpr.length()>0)
            	strexpr.setLength(strexpr.length()-1);
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
        finally
        {
            this.getFormHM().put("factorlist",list);
            this.getFormHM().put("mes","0");
            this.getFormHM().put("infor_Flag",flisst);
        }

    }

}
