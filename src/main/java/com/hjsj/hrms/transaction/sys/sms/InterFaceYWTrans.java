package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.SmsYWInterfaceBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:InterFaceYWTrans</p>
 * <p>Description:查询接收短信接口</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-05-25</p>
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class InterFaceYWTrans extends IBusiness {

	public void execute() throws GeneralException {	
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		
		String opt = "";
		if (map == null) {
			opt =  (String) this.getFormHM().get("opt");
		} else {
			opt = (String) map.get("opt");
		}
		
		SmsYWInterfaceBo bo = new SmsYWInterfaceBo(this.frameconn);
		if ("select".equalsIgnoreCase(opt)) {// 查询			
			ArrayList list = bo.getList();
			
			this.getFormHM().put("ywList", list);
		} else if ("addSave".equalsIgnoreCase(opt)) {// 增加的保存
			String isUpdate = (String) map.get("isUpdate");
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("code", this.getFormHM().get("ywCode"));
			bean.set("desc", this.getFormHM().get("ywDesc"));
			bean.set("status", this.getFormHM().get("ywStatus"));
			bean.set("classes", this.getFormHM().get("ywClasses"));
			if ("1".equalsIgnoreCase(isUpdate)) {// 更新
				bo.updateByCode(bean, (String) this.getFormHM().get("ywCode"));
			} else { // 添加
				bo.add(bean);
			}
			this.getFormHM().put("ywCode", "");
			this.getFormHM().put("ywDesc", "");
			this.getFormHM().put("ywStatus", "");
			this.getFormHM().put("ywClasses", "");
			
		} else if ("addLink".equalsIgnoreCase(opt)) {// 增加
			this.getFormHM().put("isUpdate", "0");
			this.getFormHM().put("ywCode", "");
			this.getFormHM().put("ywDesc", "");
			this.getFormHM().put("ywStatus", "");
			this.getFormHM().put("ywClasses", "");
		} else  if ("able".equalsIgnoreCase(opt)) {// 启用、未启用
			String status = (String) map.get("status");
			String codes = (String) map.get("codes");
			codes = SafeCode.decode(codes);
			String[] code = codes.split(",");
			ArrayList list = new ArrayList();
			for (int i = 0; i < code.length; i++) {
				LazyDynaBean bean = bo.getBeanByCode(code[i]);
				bean.set("status", status);
				list.add(bean);				
			}
			
			bo.batchUpdateByCode(list);
			
		} else  if ("delete".equalsIgnoreCase(opt)) {// 删除
			String codes = (String) map.get("codes");
			codes = SafeCode.decode(codes);
			String[] code = codes.split(",");
			bo.deleteByCode(code);
		} else  if ("updateLink".equalsIgnoreCase(opt)) {// 更新查询
			String code = (String) map.get("code");
			code = SafeCode.decode(code);
			LazyDynaBean bean = bo.getBeanByCode(code);
			
			this.getFormHM().put("isUpdate", "1");
			this.getFormHM().put("ywCode", code);
			this.getFormHM().put("ywDesc", (String) bean.get("desc"));
			this.getFormHM().put("ywStatus", (String) bean.get("status"));
			this.getFormHM().put("ywClasses", (String) bean.get("classes"));
		} else if ("check".equalsIgnoreCase(opt)) {
			String code = (String) this.getFormHM().get("code");
			boolean exist = bo.isExistCode(code);
			if (exist) {
				this.getFormHM().put("exist", "1");
			} else {
				this.getFormHM().put("exist", "0");
			}
		}
	}
}
