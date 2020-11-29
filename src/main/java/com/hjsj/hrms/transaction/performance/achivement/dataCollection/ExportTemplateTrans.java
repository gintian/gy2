package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**  
 *<p>Title:ExportExcelTrans.java</p>
 *<p>Description:数据采集导出模板</p>
 *<p>Company:hjsj</p>
 *<p>create time:2009-03-02 13:00:00</p>
 *@author JinChunhai
 *@version 5.0
 */

public class ExportTemplateTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		String planID = (String) this.getFormHM().get("planID");
		String determine = (String) this.getFormHM().get("determine");
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(), planID,"",this.userView);
		String planName = bo.getPlanCnName();
		
		   // 创建新的Excel 工作簿
		String outName="";
		FileOutputStream fileOut = null;
		HSSFWorkbook workbook= null;
		try
		{
			workbook= new HSSFWorkbook();
			ArrayList points = bo.getPointList(planID);
			LinkedHashMap pointsMap = new LinkedHashMap();
			StringBuffer str = new StringBuffer();
			for(int i=0;i<points.size();i++)
			{
				CommonData temp = (CommonData)points.get(i);
				String pointId = temp.getDataValue();
				String pointName = temp.getDataName();
				DataCollectBo nowBo = new DataCollectBo(this.getFrameconn(), planID,pointId,this.userView);
				ArrayList list = new ArrayList();

				String type = nowBo.getTypeOfPoint1(pointId);
				if ("0".equals(type) || "".equals(type))// 基本指标
					list.add("实际值^realValue:"+pointId);
				else
				{
					HashMap itemsMap = nowBo.getItemsMap();
					ArrayList list1=  nowBo.getAllItems();
					for(int j=0;j<list1.size();j++)
					{
						LazyDynaBean abean = (LazyDynaBean)list1.get(j);
						String itemid = (String)abean.get("item");
						list.add(itemsMap.get(itemid)+"^"+itemid+":"+pointId);
					}
				}
				pointsMap.put(pointName+"^"+pointId, list);
				if(i==0){
					str.append(pointId);
				}else{
					str.append(","+pointId);
				}
			}

			//  两种下载模板样式   JinChunhai  2011.03.30
			bo.setPoints(str.toString());//把具体指标塞进去，用于sql语句中in里面的内容   zhaoxg add 2014-9-17
			if("true".equalsIgnoreCase(determine))
				bo.downloadTemplate_special(workbook,planName,pointsMap);	
			else
				bo.downloadTemplate(workbook,planName,pointsMap);	
			
			outName=planName+"_"+this.userView.getUserName()+".xls";
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +outName);
			workbook.write(fileOut);

		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e); 
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
//		outName=outName.replace(".xls","#");
		outName = PubFunc.encrypt(outName);
		outName = SafeCode.encode(outName);
		this.getFormHM().put("outName",outName);

    }
}
