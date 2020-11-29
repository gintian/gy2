package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * @Description:定义数据规范 第三步设置必填指标，显示已选指标的指标集
 * @author: zhiyh
 * @date: 2019年3月13日 上午9:30:48 
 * @version: 1.0
 */
public class ShowRequiredFieldsetTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String fileds=(String)this.getFormHM().get("fileds");
			fileds=fileds.substring(1, fileds.length());
			String[] filedArray =fileds.split(",");
			String filedsets="";
			for(int i=0;i<filedArray.length;i++) {
				if (i!=0) {
					filedsets+=",";
				}
				FieldItem item =DataDictionary.getFieldItem(filedArray[i]);
				if (null!=item) {
					filedsets+="'"+item.getFieldsetid()+"'";
				}
			}
			ArrayList list=new ArrayList();
			String sql = "select fieldsetid,customdesc from fieldset where fieldsetid in ("+filedsets+")";
			ContentDAO dao = new ContentDAO(frameconn);
			this.frowset=dao.search(sql);
			while (this.frowset.next()) {
				list.add(new CommonData(this.frowset.getString(1),this.frowset.getString(1)+":"+this.frowset.getString(2)));
			}
			this.getFormHM().put("list",list);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
