package com.hjsj.hrms.module.certificate.dashboard.transaction;

import com.hjsj.hrms.module.certificate.dashboard.businessobject.CertificateDashboardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @Title:        CertificateDashboardTrans.java
 * @Description:  证书管理门户交易类
 * @Company:      hjsj     
 * @Create time:  2018-5-23 上午11:02:23
 * @author        linbz
 * @version       1.0
 */
public class CertificateDashboardTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
        try{
        	// =0 门户初始，=manager 管理者门户，=employee 借阅人门户
        	String opt = (String) this.getFormHM().get("opt");
        	String flag = (String) this.getFormHM().get("flag");
            
            // =0初始门户
            if("0".equals(opt)) {
            	// 如果有档案管理权限，则走管理门户=manager，否则走借阅人门户=employee
            	String roleType = this.userView.hasTheFunction("40003") ? "manager" : "employee";
            	this.getFormHM().put("roleType", roleType);
            	// 回传自助用户 与 业务用户 
        		this.getFormHM().put("userStatus", this.userView.getStatus());
            }
            // =manager 管理者门户
            else if("manager".equalsIgnoreCase(opt)) {
            	CertificateDashboardBo cerBo = new CertificateDashboardBo(this.frameconn, this.userView);
            	// 获取证书总数
            	if(flag.contains(",all,") || flag.contains(",total,")){
            		
            		if(!cerBo.checkCerConfig()) {
            			this.getFormHM().put("cerFlag", "1");
            			return;
            		}
            		HashMap map = cerBo.getTotalNum();
            		// 若返回map为空 或 null 则说明证书参数未配置完善，直接返回给出提示重新配置参数
            		if(!(null == map || map.isEmpty())) {
            			
            			this.getFormHM().put("certTotalNum", (String)map.get("certTotalNum"));
            			this.getFormHM().put("empCertTotalNum", (String)map.get("empCertTotalNum"));
            			this.getFormHM().put("empTotalNum", (String)map.get("empTotalNum"));
            		}
            		// 获取证书子集指标对应关系
            		HashMap cerMap = cerBo.getCerFieldsetid();
            		this.getFormHM().put("cerFieldsetid", cerMap);
            	}
            	// 显示所有证书
            	if (flag.contains(",allCers,")) {
            		
            		String subModuleId = "allCers_01";
            		String config = cerBo.getTableConfigCers(subModuleId, "");
            		
    	            this.getFormHM().put("tableConfig", config.toString());
            	}
            	// 获取逾期未还数
            	if (flag.contains(",all,") || flag.contains(",overdue,")) {
            		
            		ArrayList dataList = cerBo.getOverdueNum();
            		this.getFormHM().put("overdueData", dataList);
            	}
            	// 获取逾期未还数据表格
            	if (flag.contains(",overdueCers,")) {
            		
            		String subModuleId = "overdueCers_01";
            		String config = cerBo.getTableConfigOverdueCers(subModuleId, "");
            		this.getFormHM().put("overdueTableConfig", config.toString());
            	}
            	// 提醒逾期未还数据记录操作
				if (flag.contains(",remindCer,")) {
					
		            HashMap map = new HashMap();
		            map.put("nbase", (String) this.getFormHM().get("nbase"));
		            map.put("A0100", (String) this.getFormHM().get("A0100"));
		            map.put("A0101", (String) this.getFormHM().get("A0101"));
		            map.put("borrowDate", (String) this.getFormHM().get("borrowDate"));
		            map.put("returnDate", (String) this.getFormHM().get("returnDate"));
		            map.put("cerName", (String) this.getFormHM().get("cerName"));
		            map.put("cerPerName", (String) this.getFormHM().get("cerPerName"));
		            
		            String msg = cerBo.sendRemindCer(map);
		            this.getFormHM().put("msg", msg);
				}
            	// 获取待办
				if (flag.contains(",all,") || flag.contains(",dealt,")) {
					ArrayList list = cerBo.getDealtNum();
            		this.getFormHM().put("dealtData", list);
				}
				// 待办借阅单条详细信息
				if (flag.contains(",borrow,")) {
					
					String nbase = (String) this.getFormHM().get("nbase");
		        	String A0100 = (String) this.getFormHM().get("A0100");
		            String borrowDate = (String) this.getFormHM().get("borrowDate");
		            String returnDate = (String) this.getFormHM().get("returnDate");
		            String borrowCause = (String) this.getFormHM().get("borrowCause");
		            
		            ArrayList list = cerBo.borrowWinData(nbase, A0100, borrowDate, returnDate, borrowCause);
		            this.getFormHM().put("borrowCertificateData", (ArrayList)list.get(0));
		            this.getFormHM().put("fieldItems", (ArrayList)list.get(1));
		            String userInfo = cerBo.getUserInfo(nbase, A0100);
		            this.getFormHM().put("userInfo", userInfo);
				}
				// 审批待办
				if (flag.contains(",borwApp,")) {
					
		            HashMap map = new HashMap();
		            map.put("nbase", (String) this.getFormHM().get("nbase"));
		            map.put("A0100", (String) this.getFormHM().get("A0100"));
		            map.put("borrowDate", (String) this.getFormHM().get("borrowDate"));
		            map.put("returnDate", (String) this.getFormHM().get("returnDate"));
		            map.put("borrowCause", (String) this.getFormHM().get("borrowCause"));
		            map.put("approveFlag", (String) this.getFormHM().get("approveFlag"));
		            map.put("appOpinValue", (String) this.getFormHM().get("appOpinValue"));
		            
		            String  falg = cerBo.borwApproveData(map);
		            this.getFormHM().put("appflag", falg);
		            // 回调刷新待办内容
		            ArrayList list = cerBo.getDealtNum();
            		this.getFormHM().put("dealtData", list);
				}
				// 证书分布情况
            	if (flag.contains(",all,") || flag.contains(",cerdist,")) {
            		ArrayList dataList = cerBo.getCurveSituation("");
            		this.getFormHM().put("cerdistData", dataList);
            	}
            	// 证书分布情况穿透进去
            	if (flag.contains(",clickItem,")) {
            		String childItem = (String) this.getFormHM().get("childItem");
            		
            		ArrayList list = cerBo.clickChildItem(childItem);
            		String typeflag = (String)list.get(0);
            		this.getFormHM().put("typeflag", typeflag);
            		if("1".equals(typeflag))
            			this.getFormHM().put("datalist", (ArrayList)list.get(1));
            		else if("2".equals(typeflag))
            			this.getFormHM().put("datalist", (String)list.get(1));
            	}
            	// 借阅证书分布情况
            	if (flag.contains(",borrowBution,")) {
            		String borflag = (String) this.getFormHM().get("borflag");
            		
            		ArrayList list = cerBo.borrowBution(borflag);
            		this.getFormHM().put("borrowButionData", list);
            	}
            	// 借阅证书分布情况穿透进去
            	if (flag.contains(",borrowType,")) {
            		String childItem = (String) this.getFormHM().get("childItem");
            		String borflag = (String) this.getFormHM().get("borflag");
            		
            		ArrayList list = cerBo.clickChildBorrowBution(childItem, borflag);
            		String typeflag = (String)list.get(0);
            		this.getFormHM().put("typeflag", typeflag);
            		if("1".equals(typeflag))
            			this.getFormHM().put("datalist", (ArrayList)list.get(1));
            		else if("2".equals(typeflag))
            			this.getFormHM().put("datalist", (String)list.get(1));
            	}
            	// 证书到期情况统计
            	if (flag.contains(",exprieBution,")) {
            		
            		ArrayList list = cerBo.exprieBution();
            		this.getFormHM().put("exprieButionData", list);
            	}
            	// 证书到期情况统计穿透进去
            	if (flag.contains(",exprieType,")) {
            		String childFlag = (String) this.getFormHM().get("childItem");
            		
            		String tableConfig = cerBo.clickChildExprieBution(childFlag);
            		this.getFormHM().put("exprieTypeButionData", tableConfig);
            	}
            }
            // =employee 借阅人门户
            else if("employee".equalsIgnoreCase(opt)) {
            	CertificateDashboardBo cerBo = new CertificateDashboardBo(this.frameconn, this.userView);
            	
            	// 获取证书总数
            	if(flag.contains(",all,")){
            		
            		if(!cerBo.checkCerConfig()) {
            			this.getFormHM().put("cerFlag", "1");
            			return;
            		}
            		ArrayList certSubetMsg = cerBo.getCertSubetMsg();
            		this.getFormHM().put("certSubetMsg", certSubetMsg);
            		
            		// 获取证书子集指标对应关系
            		HashMap cerMap = cerBo.getCerFieldsetid();
            		this.getFormHM().put("cerFieldsetid", cerMap);
            		
            		String userInfo = cerBo.getUserInfo(PubFunc.encryption(this.userView.getDbname()), PubFunc.encryption(this.userView.getA0100()));
		            this.getFormHM().put("userInfo", userInfo);
		            this.getFormHM().put("userFullName", this.userView.getUserFullName());
		            // 处理其他指标集合
		            this.getFormHM().put("fieldItems", cerBo.getfieldSetList());
            	}
            	if(flag.contains(",borrow,")){
            		
            		String certNOItemIdValue = (String) this.getFormHM().get("certNOItemIdValue");
            		ArrayList certBorrowSubetMsg = cerBo.getCertBorrowSubetMsg(certNOItemIdValue);
            		this.getFormHM().put("certBorrowSubetMsg", certBorrowSubetMsg);
            		
            	}
            	// 加载借阅证书列表
            	if(flag.contains(",borrowCerts,")){
            		// =1显示全部证书  =2显示可借阅证书
            		String canBowFlag = (String) this.getFormHM().get("canBowFlag");
            		
     	            String subModuleId = "borrowCerts_01";
    	            String config = cerBo.getTableConfigForDiff(subModuleId, canBowFlag);
    	            this.getFormHM().put("tableConfig", config.toString());
    	        }
            	// 自助用户已借阅证书列表
            	if(flag.contains(",alreadyBorrowCert,")){
            		
            		String config = cerBo.getAlreadyBorrowCertTableConfig("alreadyBorrowCert_01");
    	            this.getFormHM().put("tableConfig", config.toString());
    	        }
            	// 借阅证书  提交操作
            	if(flag.contains(",commit,")){
            		// 借阅日期
            		String browDate = (String) this.getFormHM().get("browDate");
            		// 预计归还日期
            		String retunDate = (String) this.getFormHM().get("retunDate");
            		// 借阅原因
            		String browReason = (String) this.getFormHM().get("browReason");
            		// 借阅的证书集合
            		ArrayList browStoreData = (ArrayList) this.getFormHM().get("browStoreData");
            		// 借阅子集维护的其他指标集合
            		ArrayList fieldsData = (ArrayList) this.getFormHM().get("fieldsData");
            		// 提交操作
            		cerBo.addBrowRecords(browStoreData, browDate, retunDate, browReason, fieldsData);
    	        }
            	
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        
    }

}
