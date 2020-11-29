package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 
 * <p>Title:找到未构库子集的指标</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 26, 2009:3:30:36 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class OpenSubsetIndexTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String id = (String)this.getFormHM().get("id");
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		if(fieldsetid==null||fieldsetid=="")
			return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql = "select itemid,itemdesc,useflag from t_hr_busifield  where fieldsetid='"+fieldsetid+"' order by displayid";
		try{
			CommonData da=null; 
			ArrayList left= new ArrayList();
			ArrayList right=new ArrayList();
			String itemid = "";
			String itemdesc = "";
			String useflag = "";
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				itemid = this.frowset.getString("itemid");
				itemdesc = this.frowset.getString("itemdesc");
				useflag = this.frowset.getString("useflag");
				da=new CommonData();
				if(!"1".equalsIgnoreCase(useflag))
				{
					da.setDataName(itemdesc);
					da.setDataValue(itemid);
					left.add(da);
				}else {
					da.setDataName(itemdesc);
					da.setDataValue(itemid);
					right.add(da);
				}
			}
			this.getFormHM().put("leftlist",left);
			this.getFormHM().put("rightlist",right);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
