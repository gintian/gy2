package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Map;

/**
 * <p>
 * Title:AddKqSyncTrans
 * </p>
 * <p>
 * Description:添加考勤同步配制
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
public class AddKqSyncTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		// 获得更新类型
		String type = (String) map.get("type");
		// 没有更新类型时，默认为新增
		if (type == null || type.length() == 0) {
			type = "1";
		}
		DocumentSyncBo bo = new DocumentSyncBo(this.frameconn);
		// 获得主集指标
		ArrayList list = bo.getA01File();
		// 获得数据库类型
		ArrayList typeList = bo.getDBType();
		if ("1".equalsIgnoreCase(type)) {//  新增
			this.getFormHM().put("syncxml_id", "");
			this.getFormHM().put("syncxml_desc", "");
			this.getFormHM().put("syncxml_dbtype", "");
			this.getFormHM().put("syncxml_ip", "");
			this.getFormHM().put("syncxml_port", "");
			this.getFormHM().put("syncxml_dbname", "");
			this.getFormHM().put("syncxml_space", "");
			this.getFormHM().put("syncxml_user", "");
			this.getFormHM().put("syncxml_pwd", "");
			this.getFormHM().put("syncxml_status", "");
			this.getFormHM().put("syncxml_related", "");
			this.getFormHM().put("syncxml_options", "");
			this.getFormHM().put("syncxml_source", "");
			
		} else if ("3".equalsIgnoreCase(type)) { // 删除
			String ids = (String) map.get("ids");
			bo.delete(ids);
		} else if ("2".equalsIgnoreCase(type)) { // 修改
			String ids = (String) map.get("ids");
			LazyDynaBean bean = (LazyDynaBean)bo.getConnStrList(ids);
			this.getFormHM().put("syncxml_id", bean.get("syncxml_id"));
			this.getFormHM().put("syncxml_desc", bean.get("syncxml_desc"));
			this.getFormHM().put("syncxml_dbtype", bean.get("syncxml_dbtype"));
			this.getFormHM().put("syncxml_ip", bean.get("syncxml_ip"));
			this.getFormHM().put("syncxml_port", bean.get("syncxml_port"));
			this.getFormHM().put("syncxml_dbname", bean.get("syncxml_dbname"));
			this.getFormHM().put("syncxml_space", bean.get("syncxml_space"));
			this.getFormHM().put("syncxml_user", bean.get("syncxml_user"));
			this.getFormHM().put("syncxml_pwd", bean.get("syncxml_pwd"));
			this.getFormHM().put("syncxml_status", bean.get("syncxml_status"));
			this.getFormHM().put("syncxml_related", bean.get("syncxml_related"));
			this.getFormHM().put("syncxml_options", bean.get("syncxml_options"));
			this.getFormHM().put("syncxml_source", bean.get("syncxml_source"));
		} else if ("4".equalsIgnoreCase(type) || "5".equalsIgnoreCase(type)) { // 4发布，5暂停
			String ids = (String) map.get("ids");
			String[] id = ids.split(",");
			ArrayList idlist = new ArrayList();
			for (int i = 0; i < id.length; i++) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("syncxml_id", id[i]);
				if ("4".equalsIgnoreCase(type)) {
					bean.set("syncxml_status", "1");
				} else {
					bean.set("syncxml_status", "5");
				}
				
				idlist.add(bean);
			}
			
			bo.batchupdate(idlist);
		}
		
		// 更新类型
		this.getFormHM().put("type", type);
		this.getFormHM().put("syncxml_related_list", list);
		this.getFormHM().put("syncxml_dbtype_list", typeList);
		
	}
	 
}
