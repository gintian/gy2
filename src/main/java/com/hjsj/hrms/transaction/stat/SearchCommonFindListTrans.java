package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

public class SearchCommonFindListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String statId=(String)this.getFormHM().get("statid");
		String istwostat=(String)this.getFormHM().get("istwostat");
        StringBuffer strsql=new StringBuffer();
        strsql.append("select id,name,type from lexpr where type='");//
        strsql.append("1");
        strsql.append("' order by norder");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list =new ArrayList();
        try
        {
           
           /**常用查询条件列表*/
            this.frowset=dao.search(strsql.toString());
            int i=1;
            while(this.frowset.next())
            {
                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
                DynaBean vo=new LazyDynaBean();
                vo.set("id",this.frowset.getString("id"));
                vo.set("name",i+"."+this.getFrowset().getString("name"));
                vo.set("type",this.frowset.getString("type"));
                list.add(vo);
                ++i;
            }
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
            String stat_id=sysbo.getValue(Sys_Oth_Parameter.STAT_ID);
            getCondStatlist();
            this.getFormHM().put("default_stat_id",stat_id);
            this.getFormHM().put("preresult", "");
    	    this.getFormHM().put("result", "");
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("condlist",list);
        }
    }
	/**
     * 得到常用统计
     *
     */
    public void getCondStatlist()
    {
    	StringBuffer strsql=new StringBuffer();
        strsql.append("select id,name,type from lexpr where type='");//
        strsql.append("1");
        strsql.append("' order by id");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list =new ArrayList();
        try
        {
           
           /**常用查询条件列表*/
            this.frowset=dao.search(strsql.toString());
            
            while(this.frowset.next())
            {
                if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
                CommonData da=new CommonData();
                da.setDataValue(this.getFrowset().getString("id"));
                da.setDataName(this.getFrowset().getString("name"));
                list.add(da);
            }
        }
        catch(SQLException sqle)
        {
  	      sqle.printStackTrace();	                
        }
        finally
        {
            this.getFormHM().put("statlist",list);
        }
    }
}
