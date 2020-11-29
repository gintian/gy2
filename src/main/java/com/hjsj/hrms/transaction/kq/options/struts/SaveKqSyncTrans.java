package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title:SaveKqSyncTrans
 * </p>
 * <p>
 * Description:保存考勤同步配制
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-12-22
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SaveKqSyncTrans extends IBusiness {

	public void execute() throws GeneralException {
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		// 获得更新类型
		String type = (String) map.get("type");
		// 没有更新类型时，默认为新增
		if (type == null || type.length() == 0) {
			type = "1";
		}
		DocumentSyncBo bo = new DocumentSyncBo(this.frameconn);
		String space = (String) this.getFormHM().get("syncxml_space");
		space = space == null ? "" : space;
		
		if ("1".equalsIgnoreCase(type)) {// 新增的保存
			List list = bo.getConnStrList();
			// 获得最新的id，在最后一个id加1
			String id = bo.getId();
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("syncxml_id", id);
			bean.set("syncxml_desc", this.getFormHM().get("syncxml_desc"));
			bean.set("syncxml_dbtype", this.getFormHM().get("syncxml_dbtype"));
			bean.set("syncxml_ip", this.getFormHM().get("syncxml_ip"));
			bean.set("syncxml_port", this.getFormHM().get("syncxml_port"));
			bean.set("syncxml_dbname", this.getFormHM().get("syncxml_dbname"));
			bean.set("syncxml_space", space);
			bean.set("syncxml_user", this.getFormHM().get("syncxml_user"));
			bean.set("syncxml_pwd", this.getFormHM().get("syncxml_pwd"));
			bean.set("syncxml_status", this.getFormHM().get("syncxml_status"));
			bean
					.set("syncxml_related", this.getFormHM().get(
							"syncxml_related"));
			bean
					.set("syncxml_options", this.getFormHM().get(
							"syncxml_options"));
			bean.set("syncxml_source", this.getFormHM().get("syncxml_source"));

			bo.save(bean);
		} else if ("2".equalsIgnoreCase(type)) { // 修改的保存
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("syncxml_id", this.getFormHM().get("syncxml_id"));
			bean.set("syncxml_desc", this.getFormHM().get("syncxml_desc"));
			bean.set("syncxml_dbtype", this.getFormHM().get("syncxml_dbtype"));
			bean.set("syncxml_ip", this.getFormHM().get("syncxml_ip"));
			bean.set("syncxml_port", this.getFormHM().get("syncxml_port"));
			bean.set("syncxml_dbname", this.getFormHM().get("syncxml_dbname"));
			bean.set("syncxml_space", space);
			bean.set("syncxml_user", this.getFormHM().get("syncxml_user"));
			bean.set("syncxml_pwd", this.getFormHM().get("syncxml_pwd"));
			bean.set("syncxml_status", this.getFormHM().get("syncxml_status"));
			bean
					.set("syncxml_related", this.getFormHM().get(
							"syncxml_related"));
			bean
					.set("syncxml_options", this.getFormHM().get(
							"syncxml_options"));
			bean.set("syncxml_source", this.getFormHM().get("syncxml_source"));
			bo.update(bean);
		}

	}

}
