package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.PointCtrlXmlBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ExportExcelTrans.java</p>  
 * <p>Description:数据采集导出Excel</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-12-11 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExportExcelTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		String planID = (String) this.getFormHM().get("planID");
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(), planID,this.userView);
		String planName = bo.getPlanCnName();
		try(
			HSSFWorkbook workbook= new HSSFWorkbook();   // 创建新的Excel 工作簿
		) {
			ArrayList points = bo.getPointList(planID);
//		for(int i=0;i<points.size();i++)
//		{
//		    CommonData temp = (CommonData)points.get(i);
//		    String pointId = temp.getDataValue();
//		    String pointName = temp.getDataName();
//		    DataCollectBo nowBo = new DataCollectBo(this.getFrameconn(), planID,pointId,this.userView);
//		    nowBo.createExcel(workbook,pointName,planName);	  
//		}
			//xieguiquan start
			DataCollectBo nowBo = new DataCollectBo(this.getFrameconn());
			ArrayList list0 = new ArrayList();
			ArrayList list1 = new ArrayList();
			ArrayList list2 = new ArrayList();
			ArrayList list3 = new ArrayList();
			ArrayList list4 = new ArrayList();
			ArrayList list5 = new ArrayList();
			for (int i = 0; i < points.size(); i++) {
				CommonData temp = (CommonData) points.get(i);
				String pointId = temp.getDataValue();
				String pointName = temp.getDataName();
				String pointctrl = nowBo.getPointctrl1(pointId);
				HashMap map = PointCtrlXmlBo.getAttributeValues(pointctrl);
				nowBo.setPoint(pointId);
				String type = nowBo.getTypeOfPoint();
				if ("0".equals(type) || "".equals(type))// 基本指标
				{
					if (map != null && map.get("computeRule") != null && "0".equals(map.get("computeRule"))) {
						LazyDynaBean myBean = new LazyDynaBean();
						myBean.set("sheetname", "录分规则");
						myBean.set("pointId", pointId);
						myBean.set("pointName", pointName);
						myBean.set("type", type);
						myBean.set("computeRule", "0");
						list0.add(myBean);
					}
					if (map != null && map.get("computeRule") != null && "1".equals(map.get("computeRule"))) {
						LazyDynaBean myBean = new LazyDynaBean();
						myBean.set("sheetname", "简单、分段计算规则");
						myBean.set("pointId", pointId);
						myBean.set("pointName", pointName);
						myBean.set("type", type);
						myBean.set("computeRule", "1");
						list1.add(myBean);
					}
					if (map != null && map.get("computeRule") != null && "2".equals(map.get("computeRule"))) {
						LazyDynaBean myBean = new LazyDynaBean();
						myBean.set("sheetname", "简单、分段计算规则");
						myBean.set("pointId", pointId);
						myBean.set("pointName", pointName);
						myBean.set("type", type);
						myBean.set("computeRule", "2");
						list1.add(myBean);
					}
					if (map != null && map.get("computeRule") != null && "3".equals(map.get("computeRule"))) {
						LazyDynaBean myBean = new LazyDynaBean();
						myBean.set("sheetname", "排名计算规则");
						myBean.set("pointId", pointId);
						myBean.set("pointName", pointName);
						myBean.set("type", type);
						myBean.set("computeRule", "3");
						list3.add(myBean);
					}

				} else if ("1".equals(type)) {
					LazyDynaBean myBean = new LazyDynaBean();
					myBean.set("sheetname", "加分规则");
					myBean.set("pointId", pointId);
					myBean.set("pointName", pointName);
					myBean.set("type", type);
					myBean.set("computeRule", "-1");
					list4.add(myBean);
				} else if ("2".equals(type)) {
					LazyDynaBean myBean = new LazyDynaBean();
					myBean.set("sheetname", "扣分规则");
					myBean.set("pointId", pointId);
					myBean.set("pointName", pointName);
					myBean.set("type", type);
					myBean.set("computeRule", "-1");
					list5.add(myBean);
				}
			}
			if (list0.size() > 0) {
				nowBo.createExcelByXGQ(workbook, this.getFrameconn(), planID, planName, list0, this.userView);
			}
			if (list1.size() > 0) {
				nowBo.createExcelByXGQ(workbook, this.getFrameconn(), planID, planName, list1, this.userView);
			}
//			if(list2.size()>0){
//			    nowBo.createExcelByXGQ(workbook,this.getFrameconn(), planID,planName,list2,this.userView);	
//		}
			if (list3.size() > 0) {
				nowBo.createExcelByXGQ(workbook, this.getFrameconn(), planID, planName, list3, this.userView);
			}
			if (list4.size() > 0) {
				nowBo.createExcelByXGQ(workbook, this.getFrameconn(), planID, planName, list4, this.userView);
			}
			if (list5.size() > 0) {
				nowBo.createExcelByXGQ(workbook, this.getFrameconn(), planID, planName, list5, this.userView);
			}
			//end
			String outName = planName + "_" + this.userView.getUserName() + ".xls";
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
				workbook.write(fileOut);
				fileOut.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			} finally {
				PubFunc.closeResource(fileOut);
			}
//		outName=outName.replace(".xls","#");
			outName = PubFunc.encrypt(outName);
			//xus 20/4/30 vfs 改造
//		outName = SafeCode.encode(outName);
			this.getFormHM().put("outName", outName);
		} catch (IOException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
