package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchIpAddrTrans</p>
 * <p>Description:查询ip地址,ip_address</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 6, 2005:5:42:54 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchIpAddrListTrans extends IBusiness {

    /**
     * 
     */
    public SearchIpAddrListTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        
        StringBuffer strsql=new StringBuffer();
        strsql.append("select id, ip_addr,valid,description from ip_address");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList iplist=new ArrayList();
        ArrayList ipaddrlist=new ArrayList();
        ArrayList ipaddrlist_v=new ArrayList();
        try
        {
            this.frowset=dao.search(strsql.toString());
           
            while(this.frowset.next())
            {
               RecordVo vo=new RecordVo("ip_address");
               vo.setString("id",this.getFrowset().getString("id"));
               vo.setString("ip_addr",this.getFrowset().getString("ip_addr"));
               vo.setString("description",this.getFrowset().getString("description"));
               vo.setString("valid",this.getFrowset().getString("valid"));
               String valid=this.getFrowset().getString("valid");
               if(valid==null)
            	   valid="0";
               if("1".equalsIgnoreCase(valid))
            	   ipaddrlist.add(this.getFrowset().getString("ip_addr"));
               if("0".equalsIgnoreCase(valid))
            	   ipaddrlist_v.add(this.getFrowset().getString("ip_addr"));
               iplist.add(vo);
            }
        }
        catch(SQLException sqle)
        {
            sqle.printStackTrace();
  	        throw GeneralExceptionHandler.Handle(sqle);              
        }
        finally
        {
            this.getFormHM().put("iplist",iplist);
            this.getFormHM().put("addrlist", ipaddrlist);
            this.getFormHM().put("addrlist_v", ipaddrlist_v);
        }

    }
    

}
