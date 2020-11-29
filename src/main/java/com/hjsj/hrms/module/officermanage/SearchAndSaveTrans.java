package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.module.officermanage.businessobject.CardViewService;
import com.hjsj.hrms.module.officermanage.businessobject.impl.CardViewServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/***
 * 干部任免表 查询保存交易类
 * @author Administrator
 *
 */
public class SearchAndSaveTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			
			String guidkey=(String)this.getFormHM().get("guidkey");
			String nbase=(String)this.getFormHM().get("nbase");
			String a0100=(String)this.getFormHM().get("A0100");
			String flag=(String)this.getFormHM().get("flag");
			CardViewService card=new CardViewServiceImpl(this.userView, this.frameconn);
			if(!"save".equals(flag)&&!"photo".equals(flag)) {
				HashMap<String,String> mainFieldMap=card.getMainFields();
				this.getFormHM().put("fieldMap", mainFieldMap);
			}
			if("search".equals(flag)) {
				LazyDynaBean bean=card.getOfficerData(guidkey, nbase, a0100);
				this.getFormHM().put("data", bean);
			}else if("photo".equals(flag)){
				String url=card.getPhotoPath(nbase, a0100,"url");
				this.getFormHM().put("photoUrl", url);
			}else if("search_person".equals(flag)){
				String url=(String)this.getFormHM().get("url");
				String refresh = "";
				String[] arry=url.split("&");
				for(int i=0;i<arry.length;i++) {
					if(arry[i].startsWith("nbase")) {
						nbase=arry[i].split("=")[1];
					}
					if(arry[i].startsWith("a0100")) {
						a0100=arry[i].split("=")[1];
						//判断a0100 是否是纯数字 如果不是则解密
						if(!StringUtils.isNumeric(a0100)) {
						    a0100 = PubFunc.decrypt(a0100);
						}
					}
					if(arry[i].startsWith("refresh")) {
					    refresh=arry[i].split("=")[1];
					}
				}
				if(StringUtils.isEmpty(nbase)||StringUtils.isEmpty(a0100)) {
					throw new Exception(ResourceFactory.getProperty("officer.errorURL"));
				}
				LazyDynaBean bean=card.getOfferData(nbase, a0100);
				this.getFormHM().put("data", bean);
				this.getFormHM().put("refresh", refresh);
			}else if("save".equals(flag)) {
				MorphDynaBean data_obj=(MorphDynaBean)this.getFormHM().get("data_obj");//干部任免表存储数据
				MorphDynaBean data=(MorphDynaBean)this.getFormHM().get("data");//人员id
				card.saveData(data_obj, data);
			}
			this.getFormHM().put("flag", true);
		} catch (Exception e) {
			this.getFormHM().put("flag", false);
			this.getFormHM().put("errMsg", e.getMessage());
			e.printStackTrace();
		}
	}

}
