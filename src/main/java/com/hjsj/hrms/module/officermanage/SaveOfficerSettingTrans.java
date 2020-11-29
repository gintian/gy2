package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.module.officermanage.businessobject.OfficerConfigService;
import com.hjsj.hrms.module.officermanage.businessobject.impl.OfficerConfigServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class SaveOfficerSettingTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String flag = (String) this.getFormHM().get("flag");
		OfficerConfigService configService=new OfficerConfigServiceImpl(this.userView,this.frameconn);
		try {
			if ("save".equals(flag)) {
				String postSet = (String) this.getFormHM().get("postSet");
				String postOrg = (String) this.getFormHM().get("postOrg");
				String postState = (String) this.getFormHM().get("postState");
				String jobname=(String)this.getFormHM().get("jobname");
				String nbase = (String) this.getFormHM().get("nbase");
				String filterExpr = (String) this.getFormHM().get("filterExpr");
				HashMap<String, Object> mainfieldMap = PubFunc.DynaBean2Map((DynaBean) this.getFormHM().get("mainfields"));// 干部关键信息表
				ArrayList<MorphDynaBean> customlist = (ArrayList) this.getFormHM().get("customfields");// 自定义信息
				HashMap map=new HashMap();
				map.put("postSet", postSet);
				map.put("postOrg", postOrg);
				map.put("postState", postState);
				map.put("jobname", jobname);
				map.put("nbase", nbase);
				map.put("filterExpr", filterExpr);
				map.put("mainfields", mainfieldMap);
				map.put("customfields", customlist);
				configService.saveSetting(map);
			} else if ("fieldItem".equals(flag)) {
				String setId = (String) this.getFormHM().get("setId");
				if(StringUtils.isNotEmpty(setId)) {
					ArrayList list=configService.getFieldItemList(setId);
					this.getFormHM().put("fieldSet", JSONArray.fromObject(list.get(0)).toString());
					this.getFormHM().put("bWList", JSONArray.fromObject(list.get(1)).toString());
					this.getFormHM().put("fieldItem", JSONArray.fromObject(list.get(2)).toString());
				}else {
					this.getFormHM().put("fieldSet", null);
					this.getFormHM().put("bWList", null);
					this.getFormHM().put("fieldItem",null);
				}
			} else {
				HashMap map = configService.getOfficerConstant();
				if (map != null) {
					this.getFormHM().put("office_constant", map);
				}
				this.getFormHM().put("property", configService.getDbListFieldSetList());
			}
			this.getFormHM().put("typeFlag", true);
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("typeFlag", false);
			this.getFormHM().put("msg", e.getMessage());
		}
	}

}
