package com.hjsj.hrms.transaction.kq.machine.net_signin;

import com.hjsj.hrms.businessobject.kq.kqself.NetSignIn;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.HashMap;
/**
 * 上岛签到补签 补签信息写入到 Q03表里的某一个字段里
 * wangyao
 * @author Owner
 *
 */
public class SdMakeupNetSignInTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String nbase =(String)hm.get("dbsign");  //库前缀
		String a0100 = (String)hm.get("a0100sign"); //人员编号
		NetSignIn netSignIn=new NetSignIn();
		String sdmakeup_date=netSignIn.getWork_date();  //得到当前系统时间
		String sdao_count_field=SystemConfig.getPropertyValue("sdao_count_field"); //得到上岛标识 对应的字段
		String sdjudge = getsdjudge(nbase,a0100,sdmakeup_date,sdao_count_field);
		
		this.getFormHM().put("sdmakeup_date",sdmakeup_date);
		this.getFormHM().put("sdjudge",sdjudge);
		this.getFormHM().put("dbsign",nbase);
		this.getFormHM().put("a0100sign",a0100);
	}
	/**
	 * judge=1 签到 judge=0 取消
	 * @param nbase
	 * @param a0100
	 * @param sdmakeup_date
	 * @param sdao_count_field
	 * @return
	 */
	public String getsdjudge(String nbase,String a0100,String sdmakeup_date,String sdao_count_field)
	{
		String judge=null;
		String chusi=null;
		sdmakeup_date = sdmakeup_date.replaceAll("-","\\."); //开始
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet rowSet=null;
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("select "+sdao_count_field+" as sdzhi from q03 where nbase='"+nbase+"' and ");
			sql.append("a0100='"+a0100+"' and q03z0='"+sdmakeup_date+"' ");
			rowSet = dao.search(sql.toString());
			if(rowSet.next())
			{
				chusi=rowSet.getString("sdzhi");
			}
//			if(chusi.equalsIgnoreCase("0")||chusi.length()<0)
//			{
				judge="1";
//			}else 
//			{
//				judge="0";
//			}
		}catch(Exception e)
		{
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
		return judge;
	}
}
