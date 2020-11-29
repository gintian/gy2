package com.hjsj.hrms.utils.components.signaturefile.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.signaturefile.businessobject.SignatureFileBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignatureFileTrans extends IBusiness {
	@Override
	public void execute() throws GeneralException {
		try {
			SignatureFileBo bo=new SignatureFileBo(this.frameconn, this.userView);
			String flag = (String) this.getFormHM().get("flag");
			if ("checkSignature".equals(flag)) {
				String currentUser = (String) this.getFormHM().get("currentUser");
				HashMap map=bo.checkSignature(currentUser);
				if(map.containsKey("type")) {
					if((Boolean)map.get("type")) {
						this.getFormHM().put("signature_usb", (Boolean)map.get("signature_usb"));
						this.getFormHM().put("currentUser", (String)map.get("currentUser"));
						this.getFormHM().put("type", true);
					}else {
						this.getFormHM().put("type", false);
					}
				} else {
					this.getFormHM().put("type", false);
				}
			} else if ("comparePID".equals(flag)) {
				String data = (String) this.getFormHM().get("data");
				String data_en = (String) this.getFormHM().get("data_en");
				boolean status=bo.comparePID(data, data_en);
				this.getFormHM().put("flag", status);
			} else if ("baningID".equals(flag)) {
				String baningID = (String) this.getFormHM().get("BaningID");
				String currentUser = (String) this.getFormHM().get("currentUser");
				HashMap map=bo.checkBaning(baningID, currentUser);
				if(map!=null) {
					this.getFormHM().put("flag", map.get("flag"));
					this.getFormHM().put("checkpassword",(Boolean)map.get("checkPassWord"));
					if(map.containsKey("data")) {
						this.getFormHM().put("data", map.get("data"));
					}
				}
			}else if("checkpassword".equals(flag)) {
				String password=(String)this.getFormHM().get("value");
				String currentUser=(String)this.getFormHM().get("currentUser");
				String markID=(String)this.getFormHM().get("MarkID");
				boolean isGetMarkID=(Boolean)this.getFormHM().get("isGetMarkID");
				List<DynaBean> list=list=bo.checkPassword(false,isGetMarkID,password, currentUser,markID);
				this.getFormHM().put("photolist", list);
				this.getFormHM().put("flag", true);
			}else if("getphoto".equals(flag)) {
				String password=(String)this.getFormHM().get("value");
				String currentUser=(String)this.getFormHM().get("currentUser");
				String markID=(String)this.getFormHM().get("MarkID");
				boolean isGetMarkID=(Boolean)this.getFormHM().get("isGetMarkID");
				List<DynaBean> bean_list=bo.checkPassword(true,isGetMarkID,password, currentUser,markID);
				this.getFormHM().put("photolist", bean_list);
				if(bean_list!=null&&bean_list.size()>0) {
					this.getFormHM().put("flag", true);
				}else {
					this.getFormHM().put("flag", false);
				}
			} else if ("onlyCheckPassword".equals(flag)) {
				String currentUser = (String) this.getFormHM().get("currentUser");
				if (StringUtils.isEmpty(currentUser)) {
					if (this.userView.getStatus() == 4) {
						currentUser = PubFunc.encrypt(this.userView.getDbname() + this.userView.getA0100());
					} else {
						currentUser = PubFunc.encrypt(this.userView.getUserName());
					}
					this.getFormHM().put("currentUser", currentUser);
				}
				ArrayList list_value = new ArrayList();
				list_value.add(PubFunc.decrypt(currentUser));
				List list = ExecuteSQL.executePreMyQuery("select password,ext_param,signatureID from signature where username=?", list_value,
						this.frameconn);
				if (list != null && list.size() > 0) {
					LazyDynaBean bean = (LazyDynaBean) list.get(0);
					if (StringUtils.isNotEmpty((String) bean.get("password"))) {
						this.getFormHM().put("checkpassword", true);
					}
					String ext_param = (String)bean.get("ext_param");
					if(StringUtils.isNotEmpty(ext_param)) {
						this.getFormHM().put("data", bo.getSignatureImg("",ext_param, true));
					}
					this.getFormHM().put("signatureID", (String)bean.get("signatureid"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("flag", false);
			this.getFormHM().put("errorMsg", e.getMessage());
			
		}
	}
}
