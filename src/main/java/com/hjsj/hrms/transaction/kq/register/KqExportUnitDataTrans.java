package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ExportUnitTemplateTrans.java
 * </p>
 * <p>
 * Description:导出excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2016-09-13 13:00:00
 * </p>
 * 
 * @author linbz
 * @version 1.0
 * 
 */
public class KqExportUnitDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		try {
			
			 ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn());//实例化导出Excel工具类
			 excelUtil.setExportZero(false);
			 String sql = (String) this.userView.getHm().get("kq_sql_unit");
			 String colums = (String) this.getFormHM().get("colums");
			 String tablename = (String) this.getFormHM().get("tablename");
			
			 ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
			 ArrayList alllist= OrgRegister.newFieldItemList(fielditemlist);
			 String codesetid1="UN";
			 if(!userView.isSuper_admin()) 
	         {
			 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
				codesetid1="UM";	
	         }
			 if("Q09".equals(tablename)){
				 alllist=OrgRegister.newFieldItemListQ09(alllist,codesetid1);
			 }else if("Q07".equals(tablename)){
				 alllist=OrgRegister.newFieldItemListQ07(alllist,codesetid1);
			 }
			
			 ArrayList list = new ArrayList();
			 for(int j=0;j<alllist.size();j++){
				 FieldItem field = (FieldItem) alllist.get(j);
				 if (colums.indexOf(field.getItemid()) == -1) {
						continue;
				 	}
				 if(!field.isVisible()){
						continue;
					}
				 if("q03z3".equalsIgnoreCase(field.getItemid())|| "q03z5".equalsIgnoreCase(field.getItemid())|| "modtime".equalsIgnoreCase(field.getItemid())|| "modusername".equalsIgnoreCase(field.getItemid())){
						continue;
					}
				 list.add(field);
			 }
			 
			 ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
			 int colNum = 0;
			 HashMap headStyleMap = new HashMap();//表头样式设置
			 headStyleMap.put("columnWidth",3000);//表头宽度设置
			 headStyleMap.put("fillForegroundColor",HSSFColor.LIGHT_GREEN.index);
	        
			 HashMap colStyleMap = new HashMap();
			 colStyleMap.put("align",HorizontalAlignment.CENTER);
	        
			 for (int i = 0; i < list.size(); i++) {
					FieldItem field = (FieldItem) list.get(i);
					
					LazyDynaBean ldbean = new LazyDynaBean();
			        ldbean.set("itemid", field.getItemid());//列头代码
			        ldbean.set("comment", field.getItemid());//列头注释
			        ldbean.set("content", field.getItemdesc());//列头名称
			        ldbean.set("colType", field.getItemtype());//列数据类型
			        ldbean.set("codesetid", field.getCodesetid().toUpperCase());//代码类ID
			        ldbean.set("decwidth",  field.getDecimalwidth()+"");//列小数位数
			        ldbean.set("fromRowNum", 0);//单元格开始行
			        ldbean.set("toRowNum", 1);//单元格结束行
			        ldbean.set("fromColNum", colNum);//单元格开始行列
			        ldbean.set("toColNum", colNum);//单元格结束行列
					
			        ldbean.set("headStyleMap", headStyleMap);//表头样式
			        ldbean.set("colStyleMap", colStyleMap);//列样式
		            headList.add(ldbean);
		            colNum++;//下一列开始位置
				
			 }
			 ArrayList dataList = new ArrayList();
		
			 dataList = excelUtil.getExportData(headList, sql.toString());//得到数据列
		
			 HashMap map = new HashMap();
			 String tableName = ("Q09".equalsIgnoreCase(tablename)) ? ResourceFactory.getProperty("kq.init.depty").trim()
					 : ResourceFactory.getProperty("kq.init.dept").trim();
			 String fileName = "kq_" + this.userView.getUserName() +"_"+tableName+ ".xls";
			
			 excelUtil.exportExcel(fileName, tableName, null, headList, dataList, map, 1);//导出表格
			 fileName = PubFunc.encrypt(fileName);
			 getFormHM().put("outName", fileName);
		
        } catch(Exception e){
        	getFormHM().put("outName", "error");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e); 
		}	   
		
	}

}

