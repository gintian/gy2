package com.hjsj.hrms.module.gz.salaryaccounting.inout.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.inout.businessobject.SalaryInOutBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class importDataExportTrans extends IBusiness {
	@Override
    public void execute() throws GeneralException {
		try {
			ArrayList<String> tempTableList1=(ArrayList<String>)this.getFormHM().get("tempTableList");//临时表列对应关系
			String salaryid=(String)this.getFormHM().get("salaryid");//当前薪资id
			salaryid=PubFunc.decrypt(SafeCode.decode(salaryid));
			String tempTableName=(String)this.getFormHM().get("tempTableName");
			tempTableName=PubFunc.decrypt(SafeCode.decode(tempTableName));
			SalaryInOutBo inOutBo = new SalaryInOutBo(this.getFrameconn(), Integer.valueOf(salaryid), this.userView);
			ArrayList<String> tempTableList=new ArrayList<String>();
			String lastStr="";
			for(int i=0;i<tempTableList1.size();i++){//排除默认字段
				String str=tempTableList1.get(i);
				String [] stritem=str.split("=");
				if(stritem.length>1){
					if(!"temp_Nbase".equalsIgnoreCase(stritem[0])&&!"temp_A0100".equalsIgnoreCase(stritem[0])&&!"temp_A0000".equalsIgnoreCase(stritem[0])
							&&!"temp_B0110".equalsIgnoreCase(stritem[0])&&!"temp_E0122".equalsIgnoreCase(stritem[0])&&!"temp_A0101".equalsIgnoreCase(stritem[0])
							&&!"temp_A01Z0".equalsIgnoreCase(stritem[0])&&!lastStr.equals(stritem[0])){
						tempTableList.add(str);
						lastStr=stritem[0];
					}
				}
			}
			
			HashMap<String,LazyDynaBean> headMap=new HashMap<String, LazyDynaBean>();
			ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
			LazyDynaBean columsBean =  new LazyDynaBean();
			columsBean.set("content", "状态");
			columsBean.set("itemid", "updateflag");// 列头代码
			columsBean.set("colType", "A");// 该列数据类型
			columsBean.set("codesetid", "");
			HashMap headStyleMap = new HashMap();
			headStyleMap.put("isFontBold", true);
			headStyleMap.put("columnWidth", 5000);
			columsBean.set("headStyleMap", headStyleMap);
			headList.add(columsBean);
			for(String str:tempTableList){
				String [] stritem=str.split("=");
				columsBean = new LazyDynaBean();
				columsBean.set("content", stritem[0]);// 列头名称
				columsBean.set("itemid", stritem[2]);// 临时表列头代码  系统代码为stritem[1]
				
				if(!stritem[1].startsWith("f_useless")&&!"A00Z0".equalsIgnoreCase(stritem[1])&&!"A00Z1".equalsIgnoreCase(stritem[1])
						&&!"A00Z2".equalsIgnoreCase(stritem[1])&&!"A00Z3".equalsIgnoreCase(stritem[1])){//未设置对应关系的列以f_useless开头
					
					FieldItem field=DataDictionary.getFieldItem(stritem[1]);
					String codesetid=field.getCodesetid();
					if("0".equals(codesetid))
						codesetid="";
					columsBean.set("codesetid",codesetid );// 列头代码
					//columsBean.set("colType", field.getItemtype());// 该列数据类型
					columsBean.set("colType","A");
					columsBean.set("decwidth", String.valueOf(field.getDecimalwidth()));
				}
				else{
					columsBean.set("codesetid", "");// 列头代码
					columsBean.set("colType", "A");// 该列数据类型
				}
				
				if(!stritem[1].startsWith("f_useless")){
					
					headMap.put(stritem[1].toLowerCase(), columsBean);
				}
				
				//columsBean.set("decwidth", columnsInfo.getDecimalWidth()+"");// 列小数点后面位数
				headStyleMap = new HashMap();
				headStyleMap.put("columnWidth", 5000);
				headStyleMap.put("isFontBold", true);
				columsBean.set("headStyleMap", headStyleMap);
				headList.add(columsBean);
			}
			String sql="";
			
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn(),this.userView);
			
			ArrayList datalist=new ArrayList();
			// 导出excel 
			String fileName=this.userView.getUserName()+"_导入文件检查结果.xls";
			//excelUtil.exportExcelBySql(fileName, "检验数据", null, headList, sql, null, 0);
			
			sql=inOutBo.getCheckExportSql(tempTableList,tempTableName,headMap,"1");
			datalist=excelUtil.getExportData(headList, sql);
			inOutBo.createExcelSheet( headList, datalist,"更新数据");

			sql=inOutBo.getCheckExportSql(tempTableList,tempTableName,headMap,"2");
			datalist=excelUtil.getExportData(headList, sql);
			inOutBo.createExcelSheet( headList, datalist,"新增数据");
			
			sql=inOutBo.getCheckExportSql(tempTableList,tempTableName,headMap,"0");
			datalist=excelUtil.getExportData(headList, sql);
			inOutBo.createExcelSheet( headList, datalist,"问题数据");
			inOutBo.doSendExcel(fileName);
			
			this.getFormHM().put("fileName",SafeCode.encode(PubFunc.encrypt(fileName)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
