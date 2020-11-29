package com.hjsj.hrms.module.template.signature.transaction;

import com.hjsj.hrms.interfaces.decryptor.Des;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;



public class EnOrDeSignatureTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String type = (String) this.getFormHM().get("type");
		String data = (String) this.getFormHM().get("data");
		String data_en = (String) this.getFormHM().get("data_en");

		Des des = new Des();
		try {
			if("1".equals(type)) {
				String username = (String) this.getFormHM().get("username");
				String bandingid = (String) this.getFormHM().get("bandingid");
				if(StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(bandingid)) {
					bandingid = Base64.encodeBase64String(bandingid.getBytes());
					username = PubFunc.decrypt(username);
				}
				//解密
				//1、首先将字符串base64解开
				byte[] databytes = Base64.decodeBase64(data_en);
				//调用des的解密方法
				String data_de = des.DecryStr(databytes, "hjsj");
				String username_b = "";
				if(StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(bandingid)) {
					int index = bandingid.indexOf(data_de);
					if(index>0)
						username_b = bandingid.substring(0,index);
				}
					
				this.getFormHM().put("data_de",data_de);
				this.getFormHM().put("username",username);
				this.getFormHM().put("username_e",PubFunc.encrypt(username));
				this.getFormHM().put("username_b",username_b);
			}else if ("0".equals(type)) {
				//加密
				//调用des的加密方法
				byte [] databytes = des.EncryStr(data, "hjsj");
				//调用BASE64Encoder
				String dataen = Base64.encodeBase64String(databytes);
				this.getFormHM().put("data_en",dataen);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
