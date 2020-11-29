package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.performance.WarnNoscoreBo;
import com.hjsj.hrms.interfaces.sys.warn.IConstant;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchTotalWithUserView extends IBusiness implements IConstant
{

	public void execute() throws GeneralException 
	{
		Connection conn = null;
		try
		{
			conn = AdminDb.getConnection();
			ArrayList warnList = new ArrayList();
			
			// 绩效考核预警  JinChunhai 2012.05.21
			ArrayList planList = new ArrayList();
			HashMap roleMap = new HashMap();
			ArrayList roleList = this.userView.getRolelist();
			if(!this.userView.isSuper_admin() && roleList!=null && roleList.size()>0)
			{
				for (int i = 0; i < roleList.size(); i++) 
				{
					String role = (String) roleList.get(i);					
					roleMap.put(role, "role");					
				}
				
				WarnNoscoreBo wbo = new WarnNoscoreBo(conn,this.userView);
				planList = wbo.getWarnPlanList(roleMap,"");
			}
			for( int i=0; i<planList.size(); i++)
			{
				CommonData cData = (CommonData)planList.get(i);
				warnList.add(cData);
			}
			
			
			ScanTotal st = new ScanTotal( getUserView() );
			ArrayList alTotal = st.execute();
			for( int i=0; i<alTotal.size(); i++)
			{
				CommonData cData = (CommonData)alTotal.get(i);
				warnList.add(cData);
			}
			
			getFormHM().put(Key_FormMap_UserView_Result, warnList);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try
			{
				if (conn != null)
					conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		
	}
	
}
