package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>
 * Title:SetParamTrans
 * </p>
 * <p>
 * Description:查询参数设置及保存参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-05-30 14:19:00
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SetParamTrans extends IBusiness {

	public void execute() throws GeneralException {
		// 获得链接后的参数
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		// 操作类型，search为查询，save为保存
		String opt = (String) hm.get("opt");

		ConstantXml constant = new ConstantXml(this.frameconn, "TR_PARAM");
		if ("search".equalsIgnoreCase(opt)) {// 查询

			// diy课程分类
			String diyType = constant.getNodeAttributeValue(
					"/param/diy_course", "codeitemid");
			// 热门课程
			String hotCount = constant.getNodeAttributeValue(
					"/param/hot_course", "top");
			String diyTypeName = AdminCode.getCodeName("55", diyType);

			// 返回数据
			this.getFormHM().put("diyType", diyType);
			this.getFormHM().put("hotCount", hotCount);
			this.getFormHM().put("diyTypeName", diyTypeName);
			
			// 保存状态，0为不保存，1为保存成功，2为保存失败
			this.getFormHM().put("saveStatus", "0");
		} else if ("save".equalsIgnoreCase(opt)) {// 保存
			// diy课程类型
			String diyType = (String) this.getFormHM().get("diyType");
			// diy课程类型
			String hotCount = (String) this.getFormHM().get("hotCount");
			if (hotCount == null || "".equals(hotCount.trim())) {
				hotCount = String.valueOf(0);
			}
			try {
				// 保存diy课程分类
				constant.setAttributeValue("/param/diy_course", "codeitemid",
						diyType);
				// 保存热门课程设置
				constant
						.setAttributeValue("/param/hot_course", "top", hotCount);

				// 保存
				constant.saveStrValue();
			} catch (Exception e) {
				e.printStackTrace();
				this.getFormHM().put("saveStatus", "2");
			}

			this.getFormHM().put("diyType", diyType);
			this.getFormHM().put("hotCount", hotCount);
			this.getFormHM().put("saveStatus", "1");

		}

	}

}
