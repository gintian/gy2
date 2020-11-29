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
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>
 * Title:SendVoucherDataTrans
 * </p>
 * <p>
 * Description:发送凭证数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2012-09-06
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SendVoucherDataTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{

		// 错误消息
		String erroStr = "发送成功！";
		String pzIds = (String) this.getFormHM().get("pzIds");
		// 月份
		String timeInfo = (String) this.getFormHM().get("timeInfo");
		String dbilltimes = (String)this.getFormHM().get("dbilltimes"); // 发放次数
		pzIds = SafeCode.decode(pzIds);

		try 
		{
			GzVoucherSendBo sendBo = new GzVoucherSendBo(this.frameconn, userView);
			String[] options = sendBo.getOptions();
			//兼容老的代码 如果未设置sendtype =1 则走老的处理方法
			if(options ==null || !"1".equals(options[0])){
				if (pzIds != null && pzIds.length() > 0) 
				{
					String[] pzId = pzIds.split(",");
					GzVoucherBo bo = new GzVoucherBo(this.getFrameconn(), this.getUserView());
					// 所有凭证
					ArrayList voucherList = bo.getVoucherList();
	
					String ipStr = SystemConfig.getPropertyValue("nxyp_pz_address_url");
					
					// 发送财务凭证标记 JinChunhai 2014.05.20
					String ncFlag = SystemConfig.getPropertyValue("ncFlag");
	
					int j = 0;
					for (int i = 0; i < pzId.length; i++) 
					{
						LazyDynaBean abean = bo.getVoucherBean(pzId[i], voucherList);
						ArrayList headList = bo.getVoucherItems(abean);
						ArrayList dataList = bo.getvoucherInfoList2("0", timeInfo,pzId[i], headList, "", dbilltimes);
						ArrayList dataList2 = bo.getvoucherInfoList2("3", timeInfo,pzId[i], headList, "", dbilltimes);
						if (dataList2 != null) {
							dataList.addAll(dataList2);
						}
						
						if (dataList.size() > 0) 
						{
							j++;
							
							if(ncFlag!=null && ncFlag.trim().length()>0 && "NC".equalsIgnoreCase(ncFlag)) // 用友NC
							{
								HashMap dataMap = bo.getNcvoucherInfoList(timeInfo,pzId[i],headList);
								
								ArrayList ncdataList = new ArrayList();
								Iterator itor = dataMap.keySet().iterator();  
								while(itor.hasNext())  
								{  
									String key = (String)itor.next();  
									ncdataList = (ArrayList)dataMap.get(key);  
									
									erroStr = bo.sendDataByNC(ncdataList);
								} 														
							}
							else if(ncFlag!=null && ncFlag.trim().length()>0 && "qlyh".equalsIgnoreCase(ncFlag)) // 齐鲁银行
							{
								// 生成指定格式文件并上传到 Ftp 服务器 
								erroStr = bo.creatFileToFtp(dataList);
								
							}
							else
							{
								erroStr = bo.sendDataBySAP(ipStr,"urn:sap-com:document:sap:soap:functions:mc-style","ZHrCreateAccDoc", "EtAccDoc", dataList);
							}
							
						} else 
						{
							
						}
					}
	
					if (j == 0) {
						erroStr = "没有需要发送的凭证记录！";
					}
					this.getFormHM().put("erroStr", SafeCode.encode(erroStr));
				} 
				else 
				{
					this.getFormHM().put("erroStr", SafeCode.encode("没有未导记录！"));
				}
			}else{
				erroStr = sendBo.sendMessages(pzIds, timeInfo);
				this.getFormHM().put("erroStr", SafeCode.encode(erroStr));
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

}
