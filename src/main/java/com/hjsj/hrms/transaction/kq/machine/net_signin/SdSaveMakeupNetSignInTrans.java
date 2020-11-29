package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
/**
 * 上岛签到 补签 保存
 * @author Owner
 *wangyao
 */
public class SdSaveMakeupNetSignInTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String nbase=(String)this.getFormHM().get("nbase"); //库
		String a0100=(String)this.getFormHM().get("a0100"); //人员编号
		String sdmakeup_date = (String)this.getFormHM().get("sdmakeup_date"); //时间
		String sdjudge = (String)this.getFormHM().get("sdjudge"); //1 =签到 0=取消
		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
		
		sdmakeup_date=sdmakeup_date.replaceAll("-","\\.");
		boolean isCorrect=false;
		boolean isCorrect2=false;
		String mess="";
		isCorrect=getq03null(nbase,a0100,sdmakeup_date,sdjudge);
		if(isCorrect)
		{
			isCorrect2=setsd(nbase,a0100,sdmakeup_date,sdjudge,sdao_count_field);
			if(isCorrect2)
			{
				if("0".equalsIgnoreCase(sdjudge))
				{
					mess="成功取消上岛签到!";
				}else
				{
					mess="上岛补签到申请成功!";
				}
			}else
			{
				if("0".equalsIgnoreCase(sdjudge))
				{
					mess="上岛补签退申请失败!";
				}else
				{
					mess="上岛补签到申请失败!";
				}
			}
		}else
		{
			if("0".equalsIgnoreCase(sdjudge))
			{
				mess="上岛补签退申请失败!";
			}else
			{
				mess="上岛补签到申请失败!";
			}
		}
		this.getFormHM().put("mess",mess);
	}
	public boolean getq03null(String nbase,String a0100,String sdmakeup_date,String sdjudge)
	{
		boolean isCorrect=true;
		String dd=null;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet=null;
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("select a0100 from q03 where nbase='"+nbase+"' and a0100='"+a0100+"' and q03z0='"+sdmakeup_date+"'");
			rowSet = dao.search(sql.toString());
			if(rowSet.next())
			{
				dd=rowSet.getString("a0100");
			}
			if(dd==null||dd.length()<0)
			{
				isCorrect=false;
			}
		}catch(Exception e)
		{
			isCorrect=false;
			e.printStackTrace();
		}finally{
			if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return isCorrect;
	}
	public boolean setsd(String nbase,String a0100,String sdmakeup_date,String sdjudge,String sdao_count_field)
	{
		boolean isCorrect2=true;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet=null;
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("update q03 set "+sdao_count_field+"='"+sdjudge+"' where q03z0='"+sdmakeup_date+"' and ");
			sql.append("a0100='"+a0100+"' and nbase='"+nbase+"'");
			dao.update(sql.toString());
		}catch(Exception e)
		{
			isCorrect2=false;
			e.printStackTrace();
		}finally{
			if(rowSet!=null)
				try {
					rowSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return isCorrect2;
	}
}
