package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:UserSearchTrans</p>
 * <p>Description:用户查询交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 23, 2005:2:19:46 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class UserSearchTrans extends IBusiness {

    /**
     * 
     */
    public UserSearchTrans() {
        super();
        // TODO Auto-generated constructor stub
    }

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        StringBuffer strsql=new StringBuffer();

        strsql.append("select username,fullname from operuser where groupid<>1");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList userlist=new ArrayList();
        try
        {
            this.frowset=dao.search(strsql.toString());
            while(this.frowset.next())
            {
                RecordVo vo=new RecordVo("operuser");
                vo.setString("username",this.frowset.getString("username"));
                vo.setString("fullname",this.frowset.getString("fullname"));
                userlist.add(vo);
            }
        }
        catch(SQLException sqle)
        {
            throw GeneralExceptionHandler.Handle(sqle);
        }
        finally
        {
            this.getFormHM().put("userlist",userlist);
        }

    }

}
