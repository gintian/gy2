package com.hjsj.hrms.transaction.performance.data_collect;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* 
* 类名称：DelData_collectTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Aug 21, 2013 1:10:27 PM   
* 修改人：zhaoxg   
* 修改时间：Aug 21, 2013 1:10:27 PM   
* 修改备注：   删除
* @version    
*
 */
public class DelData_collectTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("data_collect_table");
		ArrayList list=(ArrayList)hm.get("data_collect_record");	
		DbSecurityImpl dbS = new DbSecurityImpl();
		boolean flag=false;
		try
		{
		    if(list!=null&&list.size()>0)
		    {
		    	for(int i=0;i<list.size();i++)
		    	{
		    		RecordVo vo=(RecordVo)list.get(i);
		    		String d=vo.getString("zt");
		    		if(d.trim().length()>0&&!"01".equals(d)&&!"07".equals(d))
		    		{
		    			flag=true;
		    			break;
		    		}
		    	}
		    	if(flag)
		    	{
		    		throw GeneralExceptionHandler.Handle(new Exception("只能删除起草或驳回的记录！"));
		    	}
		    	
		    	String sql="delete from "+name+" where a0100=? and i9999=?";
		    	try(
					PreparedStatement ps=this.getFrameconn().prepareStatement(sql);
				){
					for(int i=0;i<list.size();i++)
					{
						RecordVo vo=(RecordVo)list.get(i);
						ps.setString(1,vo.getString("a0100"));
						ps.setInt(2, vo.getInt("i9999"));
						ps.addBatch();
					}
					// 打开Wallet
					dbS.open(this.getFrameconn(), sql);
					ps.executeBatch();
				}
		    }
		}catch(Exception ex)
		{
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			try {
				// 关闭Wallet
				dbS.close(this.getFrameconn());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
