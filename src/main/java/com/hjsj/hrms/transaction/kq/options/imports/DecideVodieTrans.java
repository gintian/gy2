package com.hjsj.hrms.transaction.kq.options.imports;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * <p>Title:判断统计指标、统计数据来源 是否设置</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 27, 2010:5:51:36 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class DecideVodieTrans extends IBusiness{

	public void execute() throws GeneralException {
		String it=(String)this.getFormHM().get("items");
		it = PubFunc.decrypt(it);
//		String it=(String)hm.get("akq_item");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
        RecordVo vo=new RecordVo("kq_item");
        String mse="1";
        String src="1";
		try
		{
			vo.setString("item_id",it);
   	        vo=dao.findByPrimaryKey(vo);
   	        String fielditemid=vo.getString("fielditemid");
   	        if("".equals(fielditemid)||fielditemid.length()<0)
   	        	mse="0";
   	        String sdata_src=vo.getString("sdata_src");
   	        if("".equals(sdata_src)||sdata_src.length()<0)
   	        	src="0";
   	        this.getFormHM().put("mse",mse);
   	        this.getFormHM().put("src",src);
   	        this.getFormHM().put("it",it);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
