package com.hjsj.hrms.module.gz.salaryaccounting.updisk.transaction;

import com.hjsj.hrms.businessobject.gz.SalaryReportBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.gz.salaryaccounting.updisk.businessobject.BankDiskSetBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * 
 * 项目名称：hcm7.x 类名称：BankDiskExportFileTrans 类描述：银行报盘交易类 创建人：sunming 创建时间：2015-9-7
 * 
 * @version
 */

public class BankDiskExportFileTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			String salaryid = (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String tableName = "";
			String bank_id = (String) this.getFormHM().get("bankid");
			/**model=0 薪资发放中的银行报盘，model=1薪资审批中的银行报盘**/
			String model =(String)this.getFormHM().get("model");
			String bankName=this.getFormHM().get("bankName").toString();
			String bosdate = "";
			String boscount = "";
			String spSQL = "";
			//model=0 薪资发放中的银行报盘，model=1薪资审批中的银行报盘
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this
					.getFrameconn(), Integer.valueOf(salaryid),
					this.userView);
			if ("0".equals(model)) {
				tableName = gzbo.getGz_tablename();
			} else {
				tableName = "salaryhistory";
				bosdate = (String) this.getFormHM().get("bosdate");
				boscount = (String) this.getFormHM().get("boscount");
				SalaryReportBo srb = new SalaryReportBo(this.getFrameconn(),
						salaryid, "");
				spSQL = srb.getSpSQL(this.getUserView(), boscount, bosdate,
						model);
			}
			/*******************************************************************
			 * 报盘导出6种方式 :0制表符分隔的文本文件1空格分隔的文本文件 2无分隔的文本文件 4|分隔的文本文件 5逗号分隔的文本文件
			 * 3Excel
			 ******************************************************************/
			String fileType = (String) this.getFormHM().get("fileType");
			
			
			String type = "";
			if ("0".equalsIgnoreCase(fileType))// 制表符分隔的文本文件
			{
				type = "\t";
			} else if ("1".equalsIgnoreCase(fileType))// 空格分隔的文本文件
			{
				type = " ";
			} else if ("2".equalsIgnoreCase(fileType))// 无分隔文本文件
			{
				type = "";
			} else if ("4".equalsIgnoreCase(fileType))// |
			{
				type = "|";
			} else {
				type = ",";
			}
			BankDiskSetBo bbo = new BankDiskSetBo(this.getFrameconn(), Integer
					.parseInt(salaryid), this.userView);
			HashMap map = bbo.getCheckAndFormat(bank_id);
			// 在表头或表尾设置信息: bankCheck的值,0:不加,1:在首行,2:在末行.
			String bankCheck = (String) map.get("bankcheck");
			//表头内容
			String format = (String) map.get("bankformat");
			if (bankCheck == null||StringUtils.isBlank(format)) {
				bankCheck = "0";
			}
			String a0100s = bbo.getA0100s(model,spSQL,tableName);
			// 拼接column
			ArrayList columns = bbo.getFieldList(bank_id);
			ArrayList<ColumnsInfo> columnsList = new ArrayList<ColumnsInfo>();
			columnsList = bbo.toColumnsInfo(columns);
			// 得到银行模板的格式
			HashMap hm = bbo.getFormatMap(columns, bank_id);
			// 拼接sql
			String midtable="t#"+this.userView.getUserName()+"_gz";
			String sql = "select * from "+midtable;


			TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get(tableName+bank_id);
			sql+= " "+tableCache.getSortSql();// 取得oder by

			ArrayList dataList = bbo.getFilterResult(sql, columns, columnsList);
			// 导出excel
			if ("3".equals(fileType)) {
				ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();// 封装excel表头数据
				for (int i = 0; i < columns.size(); i++) {
					FieldItem fielditem = (FieldItem) columns.get(i);
					LazyDynaBean bean = new LazyDynaBean();
					String itemid = fielditem.getItemid();
					if("aid".equalsIgnoreCase(itemid)){
						continue;
					}
					String itemdesc = fielditem.getItemdesc();
					String codesetid = fielditem.getCodesetid();
					String itemtype = fielditem.getItemtype();
					//String decwidth = String.valueOf(fielditem.getDecimalwidth());
					bean.set("content", itemdesc);// 列头名称
					bean.set("itemid", itemid);// 列头代码
					bean.set("codesetid", "0");// 列头代码  全部以字符原样导出 2016-10-11 zhanghua
					if("N".equalsIgnoreCase(fielditem.getItemtype())){
						if(hm.get(itemid)!=null&&StringUtils.isNotBlank((String)hm.get(itemid))){//现在对于所有有格式的数据以文本形式输出，这样不会出现带格式的数值型数据一些输出格式错误等  31628 31899
							bean.set("colType", "A");
						}else {
							bean.set("colType", "N");
							if(!"a00z1".equalsIgnoreCase(itemid)&&!"a00z3".equalsIgnoreCase(itemid))
								bean.set("decwidth",String.valueOf( fielditem.getDecimalwidth()));
						}
					}else
						bean.set("colType", "A");// 该列数据类型
					//bean.set("codesetid", codesetid);// 列头代码
					//bean.set("decwidth", decwidth);// 列小数点后面位数
					//bean.set("colType", itemtype);// 该列数据类型
						
					headList.add(bean);
				}
				
				String fileName = "";
				ExportExcelUtil excelUtil = new ExportExcelUtil(this.getFrameconn(),this.userView);
				Date currentTime = new Date();
			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
			    String dateString = formatter.format(currentTime);

				fileName =  dateString+bankName+"_"+this.userView.getUserName()+ ".xls";
				/**行首行末位置标识**/
				ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
				//处理行首行末标识
				String[] strArr=format.split("`");
				int count = 0;
				/**excel数据头部列从第几行开始**/
				int headStartRowNum = 0;
				if("1".equals(bankCheck)){
					count=0;
					headStartRowNum = 1;
				}else{
					count = dataList.size()+1;
					headStartRowNum = 0;
				}
				
				if(!"0".equals(bankCheck)){
					/**mergedCellBean样式map**/
					HashMap mergedCellStyleMap = new HashMap();
					mergedCellStyleMap.put("fontSize", 10);
					
					String stemp="";
					//插入银行标志行
					for(int i=0;i<strArr.length;i++){
						StringBuffer str=new StringBuffer();
//						str=bbo.setFormat(str, strArr[i], (int) bbo.getCount("sp_flag", a0100s, model, boscount, bosdate)
//								, hm, "", tableName, a0100s, model, boscount, bosdate); 
						str=bbo.setFormat(str, strArr[i], dataList.size() 
								, hm, "", tableName, a0100s, model, boscount, bosdate);//改为总行数直接从数据集取得 zhanghua 2017-6-7
						stemp+=str.toString();
					}
					LazyDynaBean mergedCellBean = new LazyDynaBean();
					mergedCellBean.set("content", stemp);
					mergedCellBean.set("fromRowNum", count);
					mergedCellBean.set("toRowNum", count);
					mergedCellBean.set("fromColNum", 0);
					mergedCellBean.set("toColNum", 0);
					mergedCellBean.set("mergedCellStyleMap", mergedCellStyleMap);
					mergedCellList.add(mergedCellBean);
				}
				// 导出excel
				excelUtil.exportExcelBySql(fileName,"1",mergedCellList, headList,sql, null,headStartRowNum);
				this.getFormHM().put("fileName",
						SafeCode.encode(PubFunc.encrypt(fileName)));
			} else {
				String outName = "BankDiskFile_" + PubFunc.getStrg() + ".txt";
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outName);
				try {
					StringBuffer data_str = new StringBuffer();
					
					if ("1".equalsIgnoreCase(bankCheck))// first
					{
						data_str.append(bbo.setFormat(format, dataList,
								 type,hm, a0100s, model,
								boscount, bosdate));
					}

					data_str.append(bbo.getDataStringBuffer(columns, dataList,
							type));

					if ("2".equalsIgnoreCase(bankCheck))// last
					{
						data_str.append(bbo.setFormat(format, dataList,
								type, hm,a0100s, model,
								boscount, bosdate));
					}
					fileOut.write(data_str.toString().getBytes());
					outName = SafeCode.encode(PubFunc.encrypt(outName));
					this.getFormHM().put("fileName",outName);
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				} finally {
					PubFunc.closeResource(fileOut);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
}
