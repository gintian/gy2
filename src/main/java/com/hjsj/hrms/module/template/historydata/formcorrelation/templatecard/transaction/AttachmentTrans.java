package com.hjsj.hrms.module.template.historydata.formcorrelation.templatecard.transaction;

import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.TemplateDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 显示附件（个人or公共）
* @Title: AttachmentTrans
* @Description:
* @author: hej
* @date 2019年11月20日 下午4:31:00
* @version
 */
public class AttachmentTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		HashMap hm= this.getFormHM();
		String tabid=(String)hm.get("tabid");
		String record_id = (String)hm.get("record_id");
		String archive_year = (String)hm.get("archive_year");
		String archive_id = (String)hm.get("archive_id");
		String attachmenttype = (String)hm.get("attachmenttype");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			ArrayList list = new ArrayList();
			this.getFormHM().put("showRefreshBtn", false);
			TemplateDataBo templateDataBo = new TemplateDataBo(this.frameconn, this.userView, Integer.parseInt(tabid), archive_id);
			HashMap dataMap = templateDataBo.analysisJson2Map(record_id, archive_year);
			if(dataMap.containsKey("t_wf_file_"+attachmenttype)) {
				ArrayList fileList = (ArrayList) dataMap.get("t_wf_file_"+attachmenttype);
				for(int i=0;i<fileList.size();i++){
					HashMap fileMap = (HashMap) fileList.get(i);
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("file_id", SafeCode.encode(PubFunc.encrypt((String)fileMap.get("file_id"))));
					bean.set("attachmentname", (String)fileMap.get("name"));
					bean.set("sortname", (String)fileMap.get("sortname"));
					bean.set("ext", (String)fileMap.get("ext"));
					bean.set("ins_id", (String)fileMap.get("ins_id"));
					String d_create = (String)fileMap.get("create_time");
					Date date = format.parse(d_create);
					String create_time=DateUtils.format(date,"yyyy.MM.dd");
					bean.set("create_time", create_time);
					String name = (String)fileMap.get("fullname");
					String user_name = (String)fileMap.get("create_user");
					if(StringUtils.isBlank(name))
						name = user_name;
					bean.set("fullname", name);
					bean.set("candelete", "0");
					list.add(bean);
				}
			}
			this.getFormHM().put("attachmentList", list);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
