package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
/**
 * 
 * <p>Title:业务字典 修改 判断指标是否为开发商模式</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 1, 2009:10:13:50 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class IndexJudgeTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String msg = "1";
		String codeid = (String)this.getFormHM().get("codeid");
		String codename = (String)this.getFormHM().get("codename");
		String subsetid = (String)this.getFormHM().get("subsetid");
		boolean flag=true;
		if(this.checkupfieldname(codeid,codename,subsetid)){
			flag = false;
			msg=("所选指标不允许更改!");
		}
		this.getFormHM().put("msg", msg);
		
		this.getFormHM().put("codeid", codeid);
		this.getFormHM().put("codename", codename);
	}
	public boolean checkupfieldname(String id,String name,String subsetid)
	{
		boolean flag=false;
		String dev_flag=null;
		try {
			dev_flag = SystemConfig.getProperty("dev_flag");
			
		} catch (GeneralException e1) {
			
		}
		try{
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			if(dev_flag==null||dev_flag.length()<=0)
				  dev_flag="0";
			String sql ="select ownflag from t_hr_busifield  where fieldsetid='"+subsetid+"' and itemid='"+id+"'";
			RowSet rs = dao.search(sql);
			String s = "";
			while(rs.next())
			{
				s=rs.getString("ownflag");
				if("0".equalsIgnoreCase(dev_flag)&& "1".equalsIgnoreCase(s))
				{
					flag = true;
				}
			}
			
		}catch(Exception e)
		{
			
		}
			
		return flag;
	}
	
}
