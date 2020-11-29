package com.hjsj.hrms.transaction.sys.dbinit;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:查询指标交易类</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 2, 2008:4:52:21 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchFieldItemListTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer buf=new StringBuffer();
		String setid=(String)this.getFormHM().get("setid");
		try
		{
			//原来的参数拼加 改成使用预处理方式
			buf.append("select fieldsetid,itemid,useflag,itemtype,itemlength,itemdesc,decimalwidth,codesetid from fielditem where fieldsetid=? ");
			//【58039】要显示此指标，但不能修改 guodd 2020-02-14  /  54374 A01Z0是系统内置薪资用的停发标识，此处过滤掉，不允许删除和修改 guodd 2019-10-22
			//buf.append(" and itemid<>'A01Z0' ");
			buf.append(" order by displayid");
			ArrayList values = new ArrayList();
			values.add(setid);
			this.frowset=dao.search(buf.toString(),values);
			ArrayList list=new ArrayList();
			while(frowset.next())
			{
				RecordVo vo=new RecordVo("fielditem");
				vo.setString("fieldsetid", frowset.getString("fieldsetid"));
				vo.setString("itemid", frowset.getString("itemid"));
				vo.setString("useflag", frowset.getString("useflag"));
				vo.setString("itemtype", frowset.getString("itemtype"));
				vo.setString("itemdesc", frowset.getString("itemdesc"));
				vo.setString("useflag", frowset.getString("useflag"));
				vo.setString("codesetid", frowset.getString("codesetid"));
				vo.setInt("itemlength", frowset.getInt("itemlength"));		
				vo.setInt("decimalwidth", frowset.getInt("decimalwidth"));					
				list.add(vo);
			}
			this.frowset = dao.search("select useflag from fieldset where fieldsetid='"+setid+"'");
			if(this.frowset.next()){
				this.getFormHM().put("useflag",this.frowset.getString("useflag"));
			}
			this.getFormHM().put("list", list);
			this.getFormHM().put("setid",setid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
