package com.hjsj.hrms.transaction.gz.gz_accounting.voucher;

import com.hjsj.hrms.businessobject.gz.GzVoucherBo;
import com.hjsj.hrms.businessobject.gz.GzVoucherSendBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * <p>Title:SendCwMessageTrans</p>
 * <p>Description:发送凭证数据(标准接口)</p>
 * <p>Company:hjsj</p>
 * <p>create time:2013-06-06</p>
 * @author jinjiawei
 * @version 1.0 
 */
public class SendCwMessageTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		// 错误消息
		String erroStr = "报送成功！";
		String pzIds = (String) this.getFormHM().get("pzIds");
		// 月份
		String timeInfo = (String) this.getFormHM().get("timeInfo");
		String dbilltimes = (String)this.getFormHM().get("dbilltimes"); // 发放次数
		pzIds = SafeCode.decode(pzIds);
		try 
		{
			GzVoucherSendBo sendBo = new GzVoucherSendBo(this.frameconn, userView);
			String[] options = sendBo.getOptions();
			if("1".equals(options[0])){
				if (pzIds != null && pzIds.length() > 0) {
					if (pzIds != null && pzIds.length() > 0) {
						String[] arrPzIds = pzIds.split(",");
						for (int i = 0; i < arrPzIds.length; i++) {
							String pzId= arrPzIds[i];
							erroStr = sendBo.sendMessages(pzIds, timeInfo);
							if (erroStr.length()<1)
								erroStr ="报送成功！";
						}
						this.getFormHM().put("erroStr", SafeCode.encode(erroStr));
					}
				} else {
					this.getFormHM().put("erroStr", SafeCode.encode("没有需要报送的凭证种类！"));
				}
			}
			else {
				if (pzIds != null && pzIds.length() > 0) 
				{
					String[] pzId = pzIds.split(",");
					GzVoucherBo bo = new GzVoucherBo(this.getFrameconn(), this.getUserView());
					// 所有凭证
					ArrayList voucherList = bo.getVoucherList();
				//	String ipStr = SystemConfig.getPropertyValue("nxyp_pz_address_url");
					
					//是否走标准的推送财务凭证数据接口
					//String isCwBz = SystemConfig.getPropertyValue("isCwBz");
					String cwpz = SystemConfig.getPropertyValue("CwPz");
					int j = 0;
					for (int i = 0; i < pzId.length; i++) 
					{
						LazyDynaBean abean = bo.getVoucherBean(pzId[i], voucherList);
						ArrayList headList = bo.getVoucherItems(abean);
						ArrayList dataList = bo.getvoucherInfoList2("0", timeInfo,pzId[i], headList, "", dbilltimes);
						if(!"hkBank".equalsIgnoreCase(cwpz))
						{
							ArrayList dataList2 = bo.getvoucherInfoList2("3", timeInfo,pzId[i], headList, "", dbilltimes);
							if (dataList2 != null) {
								dataList.addAll(dataList2);
							}
						}
						if (dataList.size() > 0) 
						{
							j++;
							erroStr = bo.sendPzMessage(dataList,pzId[i]);
						} else {
							
						}
					}

					if (j == 0) {
						erroStr = "没有需要报送的凭证记录！";
					}
					this.getFormHM().put("erroStr", SafeCode.encode(erroStr));
				} else {
					this.getFormHM().put("erroStr", SafeCode.encode("没有起草记录！"));
				}	
				
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	
	}
	
}
