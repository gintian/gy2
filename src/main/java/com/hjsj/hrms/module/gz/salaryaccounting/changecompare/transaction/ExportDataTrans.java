package com.hjsj.hrms.module.gz.salaryaccounting.changecompare.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.changecompare.businessobject.ChangeCompareBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 薪资发放_变动比对_导出
 * @createtime July 02, 2015 9:07:55 PM
 * @author chent
 *
 */
public class ExportDataTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		
		// 薪资类别编号
		String tableType=(String)this.getFormHM().get("tableType"); // 表区分
		String salaryid=(String)this.getFormHM().get("salaryid"); // 薪资类别号
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		
		try {
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn,this.userView);//导出工具类
			ChangeCompareBo changeCompareBo = new ChangeCompareBo(this.getFrameconn(), this.userView);// 变动比对工具类
			// 临时表名
			String tablename = changeCompareBo.getTableName(tableType);
			// 不需要的项目
			String exceptStr = ",state,a0100,a0000,a01z0,";
			
			/** 获取Excel文件名 */
			String fileName = changeCompareBo.getExpFileName(tableType);
			
			/** 获取文件中sheet名 */
			String sheetName = changeCompareBo.getExpSheetName(tableType);
			
			/** 获取合并单元格格式 */
			ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
			if("info".equals(tableType)){//信息变动人员
				mergedCellList = changeCompareBo.getExpMergedCellList(tablename, exceptStr);
			}else{
				mergedCellList = null;
			}
			
			/** 获取Excel列头 */
			ArrayList<LazyDynaBean> excelHeadList = new ArrayList<LazyDynaBean>();// 封装excel表头数据
			if("info".equals(tableType)){//信息变动人员
				excelHeadList = changeCompareBo.getExpInfoHeadList(tablename, exceptStr);
			}else{//新增、减少、停发
				excelHeadList = changeCompareBo.getExpHeadList(tablename, exceptStr);
			}
			
			/** 获取Excel查询语句 */
			String sql = changeCompareBo.getExpSql(tablename);//包括人员库转化Usr=>在职人员库
			
			/** excel的起始行 */
			int startIndex = 0;
			if("info".equals(tableType)){//信息变动人员
				startIndex = 1;
			}
			
			/** 导出excel */
			excelUtil.exportExcelBySql(fileName, sheetName, mergedCellList, excelHeadList, sql, null, startIndex);
			
			this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
			
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
