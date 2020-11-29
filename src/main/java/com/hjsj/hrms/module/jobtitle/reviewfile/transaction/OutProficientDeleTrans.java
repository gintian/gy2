package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.OutProficientBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title: OutProficientDeleTrans
 * </p>
 * <p>
 * Description: 上会材料-单独生成账号密码 -删除按钮
 * </p>
 * <p>
 * Company: hjsj
 * </p>
 * <p>
 * create time: 2015-9-11 下午2:07:49
 * </p>
 * 
 * @author liuyang
 * @version 1.0
 */
public class OutProficientDeleTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		ContentDAO dao = new ContentDAO(this.getFrameconn());

		try {
			String content = "";//账号
			String w0501 = "";
			String w0301 = "";
			String type = "3";//外部鉴定专家
			
			ArrayList datalist = (ArrayList) this.getFormHM().get("deletedata");
			for (int i = 0; i < datalist.size(); i++) {
				MorphDynaBean mdb = (MorphDynaBean) datalist.get(i);
				String username = (String) mdb.get("username");
				if (StringUtils.isEmpty(w0501) && StringUtils.isEmpty(w0301)) {
					w0501 = PubFunc.decrypt((String) mdb.get("w0501_e"));
					w0301 = PubFunc.decrypt((String) mdb.get("w0301_e"));
				}
				content += ("'"+username+"',");
			}
			HashMap hm = this.getFormHM();

			/** 删除方法 */
			OutProficientBo ofbo = new OutProficientBo(this.getFrameconn());
			String base = ofbo.dele(w0301, w0501, content, type);
			hm.put("base", base);
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
