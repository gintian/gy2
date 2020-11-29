package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-6-8:9:13:19</p>
 * @author luangaojiong
 * @version 1.0
 * 
 */
public class DeleteLawBaseTrans extends IBusiness {

	/* 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	ArrayList lawidlst=new ArrayList();
	ArrayList lawfathidlst=new ArrayList();
	
	
	public void execute() throws GeneralException {
		String a_base_id = (String)this.getFormHM().get("a_base_id");
		a_base_id = PubFunc.decrypt(SafeCode.decode(a_base_id));
        if(a_base_id==null|| "".equals(a_base_id)){
        	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
            a_base_id=(String)hm.get("a_base_id");
        }
        String basetype = (String)this.getFormHM().get("basetype");
        if(a_base_id==null || "".equals(a_base_id))
        {
        	return;
        }
        if("1".equalsIgnoreCase(basetype))
		{
			if (!userView.isHaveResource(IResourceConstant.LAWRULE, a_base_id))
				return;
		}
		if("5".equalsIgnoreCase(basetype))
		{
			if (!userView.isHaveResource(IResourceConstant.DOCTYPE, a_base_id))
				return;
		}
		if("4".equalsIgnoreCase(basetype))
		{
			if (!userView.isHaveResource(IResourceConstant.KNOWTYPE, a_base_id))
				return;
		}
       /* if (!userView.isHaveResource(IResourceConstant.LAWRULE, a_base_id))
			return;*/
		DbSecurityImpl dbS = new DbSecurityImpl();
		Connection con=this.getFrameconn();
		Statement st=null;
		ResultSet rs=null;
        try
		{
        	 ContentDAO dao=new ContentDAO(this.getFrameconn());
           	 ArrayList list=new ArrayList();
           	 list.add(a_base_id);
           	 //删除该目录
              dao.delete("delete from law_base_struct where base_id=?",list);
              dao.delete("delete from law_base_file where base_id=?",list);
             /*
              * 
              *
              *删除子目录
             */
             st=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);

             int flag=0;
            do
             {   
            	String sql="SELECT base_id FROM law_base_struct WHERE up_base_id not in (select base_id from law_base_struct)";
            	dbS.open(con, sql);
             	rs=st.executeQuery(sql);
             	ArrayList lst=new ArrayList();
             	while(rs.next())
             	{
             		lst.add(rs.getString("base_id"));             		
             	}
             	if(lst.size()>0)
             	{
             		flag=1;
             	}
             	else
             	{
             		flag=0;
             	}
             	if(rs!=null)
             	rs.close();
             	if(flag!=0)
             	{
             		String sqlstr="delete from law_base_struct where base_id in( SELECT base_id FROM law_base_struct WHERE up_base_id not in (select base_id from law_base_struct))";
             		dbS.open(con, sqlstr);
             		st.executeUpdate(sqlstr);
             		sqlstr="delete from law_base_file where base_id in( SELECT base_id FROM law_base_struct WHERE up_base_id not in (select base_id from law_base_struct))";
             		dbS.open(con, sqlstr);
             		st.executeUpdate(sqlstr);
             		continue;
             	}             	            	
             }while(flag==1);
            
		}
        catch(Exception ex)
		{
        	
		}finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(st);
			try {
				// 关闭Wallet
				dbS.close(con);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}
	 
	

}
