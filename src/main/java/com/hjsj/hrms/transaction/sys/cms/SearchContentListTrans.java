package com.hjsj.hrms.transaction.sys.cms;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;


public class SearchContentListTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			HashMap hm = (HashMap)this.getFormHM();
			String channel_id = (String)hm.get("channel_id");
			Integer.parseInt(channel_id);

			StringBuffer buf = new StringBuffer();
			buf.append("select * from t_cms_content  where channel_id = ?");
			buf.append(" order by content_sort");

			ArrayList sqlParams = new ArrayList();
			sqlParams.add(channel_id);

			ArrayList list = new ArrayList(); 
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(buf.toString(), sqlParams);
			while(this.frowset.next()){
				DynaBean bean = new LazyDynaBean();
				bean.set("content_id",this.frowset.getString("content_id"));
				bean.set("title",this.frowset.getString("title"));
				bean.set("content",this.frowset.getString("content")==null?"":this.frowset.getString("content"));
				bean.set("out_url",this.frowset.getString("out_url")==null?"":this.frowset.getString("out_url"));
				bean.set("params",this.frowset.getString("params")==null?"":this.frowset.getString("params"));
				bean.set("news_date",PubFunc.FormatDate(this.frowset.getDate("news_date"), "yyyy.MM.dd"));
				bean.set("create_user",this.frowset.getString("create_user")==null?"":this.frowset.getString("create_user"));
				bean.set("state",this.frowset.getString("state"));
				bean.set("content_type", this.frowset.getString("content_type"));
				bean.set("visible", this.frowset.getString("visible"));
				list.add(bean);
			}
			this.getFormHM().put("list",list);
			this.getFormHM().put("channel_id",channel_id);
		} catch (NumberFormatException e) {
			throw new GeneralException("传入的channel_id值错误！");
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
