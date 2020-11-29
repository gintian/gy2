package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.module.template.templatecard.businessobject.AttachmentBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 项目名称 ：ehr
 * 类名称：SaveAttachmentTrans
 * 类描述：保存附件
 * 创建人： lis
 * 创建时间：2016-5-26
 */
public class SaveAttachmentTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM();
		
		String ins_id = (String) this.getFormHM().get("ins_id");
		/*if (ins_id != null && !"0".equals(ins_id)) {// 如果ins_id不为0,判断ins_id是否在后台存储,否则的话不允许上传
			HashMap cardAttachMap = (HashMap) this.userView.getHm().get("cardAttachMap");
			if (cardAttachMap != null && !cardAttachMap.containsKey(ins_id)) {
				throw new GeneralException(ResourceFactory.getProperty("no_permission_ins_id"));
			}
		}*/
		
		
		try {
			String tabid = (String) this.getFormHM().get("tabid");
			// 过滤完毕
			String infor_type = (String) hm.get("infor_type");
			String fileValues = (String) hm.get("fileValues");
			String object_id = (String)hm.get("object_id")==null?"":(String)hm.get("object_id");
			String moduleId = (String)hm.get("module_id")==null?"":(String)hm.get("module_id");
			object_id = PubFunc.decrypt(SafeCode.decode(object_id));
			String attachmenttype = (String) hm.get("attachmenttype");
			
			AttachmentBo attachmentBo = new AttachmentBo(userView, frameconn,tabid);
			attachmentBo.saveAttachment(ins_id, fileValues, object_id, infor_type, attachmenttype,true,moduleId);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
