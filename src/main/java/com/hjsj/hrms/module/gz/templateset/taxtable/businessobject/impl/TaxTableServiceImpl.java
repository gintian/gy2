package com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.impl;

import com.hjsj.hrms.module.gz.templateset.taxtable.businessobject.ITaxTableService;
import com.hjsj.hrms.module.gz.templateset.taxtable.dao.ITaxTableDao;
import com.hjsj.hrms.module.gz.templateset.taxtable.dao.impl.TaxTableDaoImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @Description: 税率表业务接口实现类
 * @Author manjg
 * @Date 2019/12/3 15:31
 * @Version V1.0
 **/
public class TaxTableServiceImpl implements ITaxTableService {

    /**
     * 数据库连接对象
     */
    private Connection conn;
    /**
     * 用户信息对象
     */
    private UserView userView;
    /**
     * 税率表数据接口对象
     */
    private ITaxTableDao taxTableDao;
    /** 日志对象 */
    private static Category log = Category.getInstance(TaxTableServiceImpl.class.getName());
    //导出所用xml
    private String xml= "";
    //导出所用doc
    private Document doc = null;

    public TaxTableServiceImpl(Connection conn, UserView userView){
        this.conn = conn;
        this.userView = userView;
        this.taxTableDao = new TaxTableDaoImpl(conn);
    }

    /**
     * 获取税率表表格组件内容
     * @return
     * @throws GeneralException
     */
    @Override
    public String getTaxTableConfig() throws GeneralException {
    	String taxTableConfig = "";
        try {
			ArrayList<ColumnsInfo> columnsInfoList = new ArrayList<ColumnsInfo>();

			ColumnsInfo taxIdInfo = getColumnsInfo("taxid", "taxid","A",60,"0");
			taxIdInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			columnsInfoList.add(taxIdInfo);

			ColumnsInfo desInfo = getColumnsInfo("description",ResourceFactory.getProperty("column.name"),"A",300,"0");
			desInfo.setRendererFunc("TaxTable_me.rendDescFunc");
			columnsInfoList.add(desInfo);

			ColumnsInfo k_baseInfo = getColumnsInfo("k_base", ResourceFactory.getProperty("gz.self.tax.basedata"),"N",180,"0");
			columnsInfoList.add(k_baseInfo);

			ColumnsInfo paramInfo = getColumnsInfo("param",ResourceFactory.getProperty("gz.columns.taxmode"),"A",180,"46");
			columnsInfoList.add(paramInfo);

			ArrayList buttonList = getButtonList();
			String subModuleId = "taxTable";
			ITaxTableDao taxDao = new TaxTableDaoImpl(this.conn);
			List<DynaBean> oldBeanList = taxDao.listTaxTables();
			List<LazyDynaBean> newBeanList = new ArrayList<LazyDynaBean>();
			for (DynaBean lazyDynaBean : oldBeanList) {
				LazyDynaBean newBean = new LazyDynaBean();
				String taxid = lazyDynaBean.get("taxid").toString();
				String description = lazyDynaBean.get("description").toString();
				String k_base = lazyDynaBean.get("k_base").toString();
				String param = lazyDynaBean.get("param").toString();
				param = getCodeitemIdFromXml(param);
				newBean.set("taxid", taxid);
				newBean.set("description", description);
				newBean.set("k_base", k_base);
				newBean.set("param", param);
				newBeanList.add(newBean);
			}

			TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsInfoList, subModuleId, userView, conn);
			builder.setTitle(ResourceFactory.getProperty("gz.formula.scale"));
			builder.setDataList((ArrayList) newBeanList);
			builder.setSelectable(true);
			builder.setColumnFilter(false);
			builder.setSetScheme(false);
			builder.setEditable(true);
			builder.setPageTool(false);
			builder.setPageSize(newBeanList.size());
			builder.setTableTools(buttonList);

			taxTableConfig = builder.createExtTableConfig();
		} catch (Exception e) {
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.getAllTaxTableError",e);
			throw new GeneralException("gz.taxTableHomePage.msg.getAllTaxTableError");
		}
        return taxTableConfig;
    }
	/**
	 * 列头ColumnsInfo对象初始化
	 * @param columnId id
	 * @param columnDesc 名称
	 * @param columnDesc 显示列宽
	 * @param codeSetId 代码集编号，如果不是代码指标传入"0"
	 * @return
	 */
	private static ColumnsInfo getColumnsInfo(String columnId, String columnDesc,String columnType,int columnWidth,String codeSetId){

		ColumnsInfo columnsInfo = new ColumnsInfo();
		try {
			columnsInfo.setColumnId(columnId);
			columnsInfo.setColumnDesc(columnDesc);
			columnsInfo.setColumnType(columnType);// 类型N|M|A|D
			columnsInfo.setColumnWidth(columnWidth);//显示列宽
			if (StringUtils.equalsIgnoreCase(columnType, "A")) {
				columnsInfo.setCodesetId(codeSetId);
				columnsInfo.setColumnLength(50);
			}else if (StringUtils.equalsIgnoreCase(columnType, "N")) {
				columnsInfo.setColumnLength(11);
				columnsInfo.setDecimalWidth(2);
				columnsInfo.setTextAlign("right");
			}else {
				columnsInfo.setTextAlign("left");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.packageColunmsError",e);
		}
		return columnsInfo;
	}
//    /***
//     * 按钮列表
//     * @param userView
//     * @return
//     */
    private ArrayList getButtonList() {
        ArrayList<ButtonInfo> buttonList = new ArrayList<ButtonInfo>();
        try {
        	ButtonInfo addButton = new ButtonInfo(ResourceFactory.getProperty("menu.gz.new"), "TaxTable_me.addNewRecordFunc");
        	ButtonInfo deleteButton = new ButtonInfo(ResourceFactory.getProperty("menu.gz.delete"), "TaxTable_me.deleteRecordFunc");
        	ButtonInfo saveButton = new ButtonInfo(ResourceFactory.getProperty("lable.func.main.save"), "TaxTable_me.saveFunc");
        	ButtonInfo importButton = new ButtonInfo(ResourceFactory.getProperty("menu.gz.import"), "TaxTable_me.importFunc");
        	ButtonInfo exportButton = new ButtonInfo(ResourceFactory.getProperty("menu.gz.export"), "TaxTable_me.exportFunc");
        	assemblyPrivButton(CREATE_FUNC_ID,buttonList,addButton);
        	assemblyPrivButton(DELETE_FUNC_ID,buttonList,deleteButton);
        	buttonList.add(saveButton);
        	assemblyPrivButton(EXPORT_IMPORT_FUNC_ID,buttonList,exportButton);
        	assemblyPrivButton(EXPORT_IMPORT_FUNC_ID,buttonList,importButton);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.getButtonsError",e);
		}
        return buttonList;
    }

    /***
     * 从xml字符串中取到代码号
     * @param xml xml字符串
     * @return
     */
    private String getCodeitemIdFromXml(String xml) {

    	Document doc=null;
    	String codeIdString = "";
    	try {
    		if (StringUtils.isNotBlank(xml)){
				doc = PubFunc.generateDom(xml);
				Element root = doc.getRootElement();
				codeIdString = root.getAttributeValue("TaxModeCode");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.getCodeitemError",e);
		}
    	return codeIdString;
    }

    /**
     * 税率表保存
     * @param beanList 税率表方案记录集合
     * @throws GeneralException
     */
    @Override
    public void saveTaxTable(List<DynaBean> beanList) throws GeneralException {
    	List<RecordVo> updataVoList = new ArrayList<RecordVo>();
    	List<DynaBean> BeansList = new ArrayList<DynaBean>();
    	ITaxTableDao dao = new TaxTableDaoImpl(this.conn);
    	try {
			for (DynaBean dynaBean : beanList) {
				RecordVo updatedVo = new RecordVo("gz_tax_rate");
				String taxid_old = dynaBean.get("taxid").toString();
				String description = dynaBean.get("description").toString();
				description = PubFunc.hireKeyWord_filter(description);
				String k_base = dynaBean.get("k_base").toString();
				String taxModeCode = dynaBean.get("taxModeCode").toString();
				int taxid = Integer.parseInt(PubFunc.decrypt(taxid_old));
				beanList = dao.listTaxTables(PubFunc.decrypt(taxid_old));
				updatedVo.setObject("taxid",taxid);
				updatedVo.setObject("description",description);
				updatedVo.setObject("k_base",k_base);
				updatedVo.setObject("param",getXmlfromCode((String)(beanList.get(0).get("param")), taxModeCode));
				updataVoList.add(updatedVo);
			}
			dao.updateTaxTable(updataVoList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.saveTaxTableError",e);
			throw new GeneralException("gz.taxTableHomePage.msg.saveTaxTableError");
		}
    }

    /***
     * 在XML中添加代码
     * @param code xml字符串
     * @return
     */
    private String getXmlfromCode(String xml,String code) throws GeneralException {
    	StringBuffer xmls = new StringBuffer();
    	Document doc=null;
    	String codeIdString = "";
    	try {
    		doc = PubFunc.generateDom(xml);
    		Element root = doc.getRootElement();
    		root.setAttribute("TaxModeCode",code);
		    XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
   	     	format.setEncoding("UTF-8");
   	     	outputter.setFormat(format);
   	        xmls.setLength(0);
   	        xmls.append(outputter.outputString(doc));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("hrms/module/gz/gz_resource_zh_CN.js->getXmlError",e);
			throw new GeneralException("gz.taxTableHomePage.msg.saveTaxTableError");
		}
    	return xmls.toString();
    }

    /**
     * 删除税率方案记录
     * @param ids 税率表方案id
     * @throws GeneralException
     */
    @Override
    public void deleteTaxTable(String ids) throws GeneralException {
    	String[] paramArr = ids.split(",");
		List<String> paramList = Arrays.asList(paramArr);
		List<Integer> deCryptParamList = new ArrayList<Integer>();
		List<DynaBean> usedFormula = new ArrayList<DynaBean>();
		List<DynaBean> taxTableDetails = new ArrayList<DynaBean>();
		List<RecordVo> deleteHomeVoList = new ArrayList<RecordVo>();
		List<RecordVo> deleteDetilVoList = new ArrayList<RecordVo>();
		int taxid;
		StringBuffer idsBuffer = new StringBuffer();
		try {
			//组装需要删除的税率表信息
			for (String param : paramList) {
				RecordVo homePageVo = new RecordVo("gz_tax_rate");
				taxid = Integer.parseInt(PubFunc.decrypt(param));
				idsBuffer.append(taxid).append(",");
				deCryptParamList.add(taxid);
				homePageVo.setObject("taxid", taxid);
				deleteHomeVoList.add(homePageVo);
			}
			idsBuffer.deleteCharAt(idsBuffer.length()-1);
			ITaxTableDao dao = new TaxTableDaoImpl(this.conn);
			//检查是否有被占用的税率表
			usedFormula = dao.isHaveTaxTableTosalaryformula(deCryptParamList);
			if (usedFormula.size() == 0) {
				//没有被占用的税率表，根据taxid查询明细信息
				taxTableDetails = dao.listTaxTableDetails(idsBuffer.toString());
				for (DynaBean beans : taxTableDetails) {
					RecordVo detilVo = new RecordVo("gz_taxrate_item");
					detilVo.setObject("taxid", PubFunc.decrypt((String)beans.get("taxid")));
					detilVo.setObject("taxitem", PubFunc.decrypt((String)beans.get("taxitem")));
					deleteDetilVoList.add(detilVo);
				}
				//同时删除税率表和明细表
				dao.deleteTaxTable(deleteHomeVoList);
				dao.deleteTaxTableDetail(deleteDetilVoList);
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.deleteTaxTableError",e);
			throw new GeneralException("gz.taxTableHomePage.msg.deleteTaxTableError");
		}

		if(usedFormula.size() != 0) {
			String fileName = writeUsedFormula(usedFormula);
			log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.haveUsedFormulaError");
			throw new GeneralException("gz.taxTableHomePage.msg.haveUsedFormulaError,"+fileName);
		}
    }
    /***
     * 将被占用的公式信息输出到TXT中保存下来
     * @param result 被占用公式的信息
     * @return 加密过的文件名
     */
     private String writeUsedFormula(List<DynaBean> result) {
         String fileName = userView.getUserName()+ResourceFactory.getProperty("gz.export.tax.usedfilename")+".txt";
         BufferedWriter out = null;
         String filePath = System.getProperty("java.io.tmpdir");
		 if (!filePath.endsWith("\\")) {
			 filePath = filePath + "\\";
		 }
         filePath = filePath.replace("\\", File.separator).replace("/", File.separator);
         String enCodeName = PubFunc.encrypt(fileName);
         FileOutputStream fileOutput = null;
         OutputStreamWriter outputStream = null;
         try {
             if (result != null && !result.isEmpty() && StringUtils.isNotEmpty(fileName)) {
                 File pathFile = new File(filePath);
                 if (!pathFile.exists()) {
                	 pathFile.mkdirs();
                 }
                 String relFilePath = filePath + fileName;
                 File file = new File(relFilePath);
                 if (!file.exists()) {
                     file.createNewFile();
                 }
                 fileOutput = new FileOutputStream(file);
                 outputStream = new OutputStreamWriter(fileOutput, "GBK");
                 out = new BufferedWriter(outputStream);
                 for (DynaBean dynaBean : result) {
					out.write(ResourceFactory.getProperty("gz.export.tax.taxtableename")+dynaBean.get("description").toString()+" ");
					out.write(ResourceFactory.getProperty("gz.export.tax.salaryname")+dynaBean.get("cname").toString()+" ");
					out.write(ResourceFactory.getProperty("gz.export.tax.formuladesc")+dynaBean.get("hzname").toString()+" ");
					out.newLine();
				 }
             }
         } catch (Exception e) {
             e.printStackTrace();
             log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.exportUsedMsgError",e);
         } finally {
             PubFunc.closeIoResource(fileOutput);
             PubFunc.closeIoResource(outputStream);
             PubFunc.closeIoResource(out);
         }
         return enCodeName;
     }
    /**
     * 导出税率表方案
     * @param ids 税率表方案id
     * @return
     * @throws GeneralException
     */
    @Override
    public String exportTaxTable(String ids) throws GeneralException {
    	String ExcelName = null;
    	FileOutputStream fileOut = null;
    	HSSFWorkbook workbook= new HSSFWorkbook();//创建excel对象
    	try {
    		String[] taxid = ids.split(",");
			if(taxid.length==1) {
				String description = null;
				List<DynaBean> list = taxTableDao.listTaxTables(ids);//根据id获取税率表
				for (DynaBean dynaBean : list) {
					description = (String) dynaBean.get("description");
				}
				description = description.replace(",", "，");
				description = description.replace("\\", "＼");
				description = description.replace(":", "：");
				ExcelName = userView.getUserName()+"_"+description+".xls";//返回  用户名_税率表名称.xls
			}else {
				ExcelName = userView.getUserName()+"_"+ ResourceFactory.getProperty("gz.export.tax.name") +".xls";// 返回 用户名_税率表方案.xls
			}
			HashMap taxmap = getTaxExcelHead();//设置税率表的excel表头
			//第一个sheet 税率表
			HSSFSheet sheet1 = workbook.createSheet();//创建excel中sheet对象
			workbook.setSheetName(0,ResourceFactory.getProperty("gz.formula.scale"));//设置sheet的名字
			HSSFRow row = null;//行
			HSSFCell cell=null;//单元格
			HSSFComment comment=null;
			int n=0;
			n=setTaxHead(n,taxmap,workbook,sheet1,ExcelName);
			ArrayList taxinfoList = getTaxTableInfo(ids);
			setTaxData(n,taxinfoList,workbook,sheet1,row,cell);
			// 必须在单元格设值以后进行
            // 设置为根据内容自动调整列宽
            for (int k = 0; k < taxmap.size(); k++) {
                sheet1.autoSizeColumn(k);
            }
            setSizeColumn(sheet1, taxmap.size());
			//第二个sheet 税率表明细
			HSSFSheet sheet2 = workbook.createSheet();
			workbook.setSheetName(1,ResourceFactory.getProperty("gz.columns.slmx"));
			HashMap taxDetailMap= getTaxDetailExcelHead();
			short k=0;
			k=setTaxDetailHead(k,taxDetailMap,workbook,sheet2,ExcelName);
			ArrayList detailList = getTaxDetailTableList(ids);
			String[][] data_arr=getDetailData(detailList);
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT );
			HSSFCellStyle cellStyle2= workbook.createCellStyle();
			cellStyle2.setFont(font);
			cellStyle2.setAlignment(HorizontalAlignment.LEFT );
			for(short j=0;j<data_arr.length;j++){
				row= sheet2.createRow(k);
				for(short h=0;h<data_arr[j].length;h++){
					cell=row.createCell(h);
					if(h==7) {
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						cell.setCellStyle(cellStyle2);
					} else {
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellStyle(cellStyle);
					}
					cell.setCellValue(data_arr[j][h]);
				}
				k++;
			}
			for (int v = 0; v < taxDetailMap.size(); v++) {
				 sheet2.autoSizeColumn(k);
            }
			setSizeColumn(sheet2, taxDetailMap.size());
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+ExcelName);
			workbook.write(fileOut);
			fileOut.close();
			sheet1=null;
			sheet2=null;
			ExcelName = PubFunc.encrypt(ExcelName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.exportTaxTableError",e);
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}finally {
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeIoResource(workbook);
		}
		return ExcelName;
    }

    /**
     * 导入税率表方案
     * @param fileId 文件加密id
     * @throws GeneralException
     */
    @Override
    public List<Map> importTaxTable(String fileId) throws GeneralException {
    	List<Map> repeat= null;
    	HSSFWorkbook wb = null;
		try {
			InputStream is = VfsService.getFile(fileId);
			wb = new HSSFWorkbook(is);
			if(wb.getNumberOfSheets()<2) {
				throw new GeneralException("gz.taxTable.msg.importTaxTableSheetError");
			}
			HSSFSheet sheet = wb.getSheetAt(0);     //读取sheet 0
			HSSFSheet sheet2 = wb.getSheetAt(1);     //读取sheet 1
			String checkResult = importCheck(sheet, sheet2);
			if(checkResult!=null) {
				return repeat;
			}
			List<Integer> repeatid = importTaxTableSave(sheet);//保存税率表，返回重复taxid的集合
			importTaxTableSaveItem(sheet2, repeatid);//保存明细表
			repeat = importTaxTableReturn(repeatid, sheet, sheet2);//组装taxid重复的数据集合
		} catch (Exception e) {
			e.printStackTrace();
			log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableOneError"+e);
			throw new GeneralException("gz.taxTable.msg.importTaxTableOneError");
		} finally {
			PubFunc.closeIoResource(wb);
		}
		return repeat;
    }
    /**
     * 校验导入数据
     * @param sheet
     * @param sheet2
     * @return
     * @throws GeneralException
     */
    private String importCheck(HSSFSheet sheet,HSSFSheet sheet2) throws GeneralException {
    	String errorMsg = null;
    	try {
    		if(sheet.toString() == null && sheet2.toString() == null) {
    			errorMsg = "gz.taxTable.msg.importTaxTableSheetNullError";
    			throw new GeneralException(errorMsg);
    		}else {
    			int firstRowIndex = sheet.getFirstRowNum();   //列名
    			HSSFRow row = sheet.getRow(firstRowIndex);
    			HSSFRow row2 = sheet2.getRow(firstRowIndex);
    			int firstCellNum = row.getFirstCellNum();
    			int firstCellNum2 = row2.getFirstCellNum();
    			int lastCellNum = row.getLastCellNum();
    			int lastCellNum2 = row2.getLastCellNum();
    			if(lastCellNum < 4) {
    				errorMsg = "gz.taxTable.msg.importTaxTableTaxRowError";
    				throw new GeneralException(errorMsg);
    			}else {
    				String cell1 = row.getCell(firstCellNum).toString();
    				String cell2 = row.getCell(firstCellNum+1).toString();
    				String cell3 = row.getCell(firstCellNum+2).toString();
    				String cell4 = row.getCell(firstCellNum+3).toString();
    				if(!ResourceFactory.getProperty("gz.columns.type").equals(cell1)||!ResourceFactory.getProperty("gz.columns.taxname").equals(cell2)||
    						!ResourceFactory.getProperty("gz.columns.basedata").equals(cell3)||!ResourceFactory.getProperty("gz.columns.taxmode").equals(cell4)) {
    					errorMsg = "gz.taxTable.msg.importTaxTableTaxRowError";
    					throw new GeneralException(errorMsg);
    				}
    			}
    			if(lastCellNum2 < 9) {
    				errorMsg = "gz.taxTable.msg.importTaxTableItemRowError";
    				throw new GeneralException(errorMsg);
    			}else {
    				String cellItem1 = row2.getCell(firstCellNum2).toString();
    				String cellItem2 = row2.getCell(firstCellNum2+1).toString();
    				String cellItem3 = row2.getCell(firstCellNum2+2).toString();
    				String cellItem4 = row2.getCell(firstCellNum2+3).toString();
    				String cellItem5 = row2.getCell(firstCellNum2+4).toString();
    				String cellItem6 = row2.getCell(firstCellNum2+5).toString();
    				String cellItem7 = row2.getCell(firstCellNum2+6).toString();
    				String cellItem8 = row2.getCell(firstCellNum2+7).toString();
    				String cellItem9 = row2.getCell(firstCellNum2+8).toString();
    				if(!ResourceFactory.getProperty("gz.columns.type").equals(cellItem1)||!ResourceFactory.getProperty("gz.columns.slbh").equals(cellItem2)||
    						!ResourceFactory.getProperty("gz.columns.ynsd_dowm").equals(cellItem3)||!ResourceFactory.getProperty("gz.columns.ynsd_up").equals(cellItem4)||
    						!ResourceFactory.getProperty("gz.columns.sl").equals(cellItem5)||!ResourceFactory.getProperty("gz.columns.sskcs").equals(cellItem6)||
    						!ResourceFactory.getProperty("gz.columns.taxflag").equals(cellItem7)||!ResourceFactory.getProperty("label.description").equals(cellItem8)||
    						!ResourceFactory.getProperty("gz.columns.kc_base").equals(cellItem9)) {
    					errorMsg = "gz.taxTable.msg.importTaxTableItemRowError";
    					throw new GeneralException(errorMsg);
    				}
    			}
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    		errorMsg = "gz.taxTable.msg.importTaxTableFormatError";
			throw new GeneralException(errorMsg);
    	}
    	return errorMsg;
    }
    /**
     * 导入 添加taxid未重复的数据
     * @param sheet 税率表对象
     * @return 重复id集合
     * @throws GeneralException
     */
    private List<Integer> importTaxTableSave(HSSFSheet sheet) throws GeneralException {
    	List<Integer> repeatid = new ArrayList();//重复id集合
    	try {
    		//税率表
    		int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
            int lastRowIndex = sheet.getLastRowNum();
            List<RecordVo> list = new ArrayList();//添加到数据库的list
            List<DynaBean> taxTables = taxTableDao.listTaxTables();//获取数据库中所有的taxid
            for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                HSSFRow row = sheet.getRow(rIndex);
                if (row != null) {
                    int firstCellNum = row.getFirstCellNum();
                    //获取每列的taxid
                	HSSFCell cell = row.getCell(firstCellNum);
                	String[] query = cell.toString().split("\\.");//表格中可能为小数
                	if(!"".equals(query[0])&&query != null) {
                		int taxid = Integer.parseInt(query[0]);
                		int i = 0;//统计重复id
                		for (DynaBean dynaBean : taxTables) {//遍历taxid与excel中的taxid做比较，不相同插入到数据库中
                			String id = (String) dynaBean.get("taxid");
                			id = PubFunc.decrypt(id);
                			int int_id = Integer.parseInt(id);
                			if(query[0].length()>0) {
                				if(int_id == taxid) {
                					i++;
                				}
                			}
                		}
                		if(i>0) {//此id重复
                			repeatid.add(taxid);
                		}else {
                			int lastCellNum = row.getLastCellNum();
                			RecordVo vo = new RecordVo("gz_tax_rate");
                			for (int cIndex = firstCellNum; cIndex < lastCellNum; cIndex++) {   //遍历列
                				HSSFCell cellAll = row.getCell(cIndex);
                				if(cIndex==firstCellNum) {
                					String[] idQuery = cellAll.toString().split("\\.");//表格中可能为小数
                					int id = Integer.parseInt(idQuery[0]);
                					vo.setInt("taxid", id);
                				}
                				if(cIndex==firstCellNum+1) {
                					vo.setString("description", cellAll.toString());
                				}
                				if(cIndex==firstCellNum+2) {
                					vo.setDouble("k_base", Double.parseDouble(cellAll.toString()));
                				}
                				if(cIndex==firstCellNum+3) {
                					String param = cellAll.toString();
                					//汉字转代码数字
                					param = codeItemdescToItemid("46", param);
                					//组装xml
                					param = getImportXmlfromCode(param);
                					vo.setString("param", param);
                				}
                			}
                			list.add(vo);
                		}
                	}
                }
            }
            if(list.size()>0) {
            	taxTableDao.insertTaxTable(list);
            	list.clear();
            }
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableOneAddError"+e);
			throw new GeneralException("gz.taxTable.msg.importTaxTableOneAddError");
    	}
    	return repeatid;
    }
    /**
     * 导入 新增明细表
     * @param sheet2 明细表
     * @param repeatid 重复id添加到此列表
     * @return
     * @throws GeneralException
     */
    private void importTaxTableSaveItem(HSSFSheet sheet2,List<Integer> repeatid) throws GeneralException {
    	try {
    		String itemMaxIdString = taxTableDao.getMaxId("gz_taxrate_item");
    		int itemMaxId = Integer.parseInt(itemMaxIdString);
    		//明细表
        	List<RecordVo> list = new ArrayList();//添加到数据库的list
    		int firstRowIndex1 = sheet2.getFirstRowNum()+1;   //第一行是列名，所以不读
            int lastRowIndex1 = sheet2.getLastRowNum();
            for(int rIndex = firstRowIndex1; rIndex <= lastRowIndex1; rIndex++) {   //遍历行
                HSSFRow row = sheet2.getRow(rIndex);
                if (row != null) {
                    int firstCellNum = row.getFirstCellNum();
                    //获取每列的taxid
                	HSSFCell cell = row.getCell(firstCellNum);
                	String[] query = cell.toString().split("\\.");//表格中可能为小数
                	if(!"".equals(query[0])&&query != null) {
                		int taxid = Integer.parseInt(query[0]);
                		int i = 0;//统计重复id
                		for (Integer int_id : repeatid) {//遍历taxid与excel中的taxid做比较，不相同插入到数据库中
                			if(int_id == taxid) {
                				i++;
                			}
                		}
                		if(i==0){
                			int lastCellNum = row.getLastCellNum();
                			RecordVo vo = new RecordVo("gz_taxrate_item");
                			for (int cIndex = firstCellNum; cIndex < lastCellNum; cIndex++) {   //遍历列
                				HSSFCell cellAll = row.getCell(cIndex);
                				if(cIndex==firstCellNum) {
                					String[] idQuery = cellAll.toString().split("\\.");//表格中可能为小数
                					int id = Integer.parseInt(idQuery[0]);
                					vo.setInt("taxid", id);
                				}
                				if(cIndex==firstCellNum+1) {
                					/*String[] taxitemQuery = cellAll.toString().split("\\.");//表格中可能为小数
                					int taxitem = Integer.parseInt(taxitemQuery[0]);
                					vo.setInt("taxitem", taxitem);*/
                					itemMaxId++;
                		    		vo.setInt("taxitem", itemMaxId);
                				}
                				if(cIndex==firstCellNum+2) {
                					vo.setDouble("ynse_down", Double.parseDouble(cellAll.toString()));
                				}
                				if(cIndex==firstCellNum+3) {
                					vo.setDouble("ynse_up", Double.parseDouble(cellAll.toString()));
                				}
                				if(cIndex==firstCellNum+4) {
                					vo.setDouble("sl", Double.parseDouble(cellAll.toString()));
                				}
                				if(cIndex==firstCellNum+5) {
                					vo.setDouble("sskcs", Double.parseDouble(cellAll.toString()));
                				}
                				if(cIndex==firstCellNum+6) {
                					String flag = cellAll.toString();
                					flag = getFlag(flag);
                					vo.setInt("flag", Integer.parseInt(flag));
                				}
                				if(cIndex==firstCellNum+7) {
                					vo.setString("description", cellAll.toString());
                				}
                				if(cIndex==firstCellNum+8) {
                					vo.setDouble("kc_base", Double.parseDouble(cellAll.toString()));
                				}
                			}
                			list.add(vo);
                		}
                	}
                }
            }
            if(list.size()>0) {
            	taxTableDao.insertTaxTableDetail(list);
            }
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableOneAddItemError"+e);
			throw new GeneralException("gz.taxTable.msg.importTaxTableOneAddItemError");
    	}
    }

    /**
     * 导入 组装返回的数据
     * @param repeatid 重复taxid的集合
     * @param sheet 税率表对象
     * @param sheet2 明细表对象
     * @return taxid重复的数据集合
     * @throws GeneralException
     */
    private List<Map> importTaxTableReturn(List<Integer> repeatid,HSSFSheet sheet,HSSFSheet sheet2) throws GeneralException  {
    	List<Map> repeat = new ArrayList();//重复集合
    	try {
    		int firstRowIndex = sheet.getFirstRowNum()+1;   //第一行是列名，所以不读
            int lastRowIndex = sheet.getLastRowNum();
    		for(int rIndex = firstRowIndex; rIndex <= lastRowIndex; rIndex++) {   //遍历行
                HSSFRow row = sheet.getRow(rIndex);
                if (row != null) {
                    int firstCellNum = row.getFirstCellNum();
                    //获取每列的taxid
                	HSSFCell cell = row.getCell(firstCellNum);
                	String[] query = cell.toString().split("\\.");//表格中可能为小数
                	if(!"".equals(query[0])&&query != null) {
                		int taxid = Integer.parseInt(query[0]);
                		int count = 0;//统计重复id
                		for (Integer int_id : repeatid) {//遍历重复id
                			if(int_id == taxid) {
                				count++;
                			}
                		}
                		if(count>0){
                			int lastCellNum = row.getLastCellNum();
                			Map map = new HashMap();
                			for (int cIndex = firstCellNum; cIndex < lastCellNum+1; cIndex++) {   //遍历列
                				if(!(cIndex==firstCellNum+4)) {
                					HSSFCell cellAll = row.getCell(cIndex);
                					if(cIndex==firstCellNum) {
                						String[] idQuery = cellAll.toString().split("\\.");//表格中可能为小数
                						String id = PubFunc.encrypt(idQuery[0]);
                						map.put("taxid", id);
                					}
                					if(cIndex==firstCellNum+1) {
                						map.put("description", cellAll.toString());
                					}
                					if(cIndex==firstCellNum+2) {
                						map.put("k_base", cellAll.toString());
                					}
                					if(cIndex==firstCellNum+3) {
                						map.put("taxModeCode", cellAll.toString());
                					}
                				}else {
                					map = getImportItemMap(sheet2, repeatid, taxid, map);
                				}
                			}
                			repeat.add(map);
                		}
                	}
                }
            }
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableOneAssembleError"+e);
			throw new GeneralException("gz.taxTable.msg.importTaxTableOneAssembleError");
    	}
    	return repeat;
    }
    /**
     * 导入 组装明细表重复数据
     * @param sheet2 明细表对象
     * @param repeatid 重复taxid集合
     * @param taxid
     * @param map 要组装的Map
     * @return
     */
    private Map getImportItemMap(HSSFSheet sheet2,List<Integer> repeatid,int taxid,Map map) throws GeneralException {
    	try {
    		int firstRowIndex1 = sheet2.getFirstRowNum()+1;   //第一行是列名，所以不读
            int lastRowIndex1 = sheet2.getLastRowNum();
    		List mxlist = new ArrayList();
    		for(int rIndex1 = firstRowIndex1; rIndex1 <= lastRowIndex1; rIndex1++) {   //遍历行
                HSSFRow row1 = sheet2.getRow(rIndex1);
                int mxcount=0;
                if (row1 != null) {
                    //获取每列的taxid
                    int mxfirstCellNum = row1.getFirstCellNum();
                	HSSFCell cell1 = row1.getCell(mxfirstCellNum);
                	String[] query1 = cell1.toString().split("\\.");//表格中可能为小数
                	if(!"".equals(query1[0])&&query1 != null) {
                		int taxid1 = Integer.parseInt(query1[0]);
                		if(taxid1 == taxid) {
                			int mxlastCellNum = row1.getLastCellNum();
                			Map mxmap = new HashMap();
                			for (int mxIndex = mxfirstCellNum; mxIndex < mxlastCellNum; mxIndex++) {   //遍历列
                				HSSFCell cellAll = row1.getCell(mxIndex);
                				if(mxIndex==mxfirstCellNum) {
                					String[] idQuery = cellAll.toString().split("\\.");//表格中可能为小数
                					String id = PubFunc.encrypt(idQuery[0]);
                					mxmap.put("taxid", id);
                				}
                				if(mxIndex==mxfirstCellNum+1) {
                					String[] idQuery = cellAll.toString().split("\\.");//表格中可能为小数
                					String taxitem = PubFunc.encrypt(idQuery[0]);
                					mxmap.put("taxitem", taxitem);
                				}
                				if(mxIndex==mxfirstCellNum+2) {
                					mxmap.put("ynse_down", cellAll.toString());
                				}
                				if(mxIndex==mxfirstCellNum+3) {
                					mxmap.put("ynse_up", cellAll.toString());
                				}
                				if(mxIndex==mxfirstCellNum+4) {
                					mxmap.put("sl", cellAll.toString());
                				}
                				if(mxIndex==mxfirstCellNum+5) {
                					mxmap.put("sskcs", cellAll.toString());
                				}
                				if(mxIndex==mxfirstCellNum+6) {
                					String flag = cellAll.toString();
                					flag = getFlag(flag);
                					mxmap.put("flag", flag);
                				}
                				if(mxIndex==mxfirstCellNum+7) {
                					mxmap.put("description", cellAll.toString());
                				}
                				if(mxIndex==mxfirstCellNum+8) {
                					mxmap.put("kc_base", cellAll.toString());
                				}
                			}
                			mxlist.add(mxmap);
                		}
                	}
                }
    		}
    		map.put("items", mxlist);
	    }catch(Exception e) {
			e.printStackTrace();
			log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableOneAssembleItemError"+e);
			throw new GeneralException("gz.taxTable.msg.importTaxTableOneAssembleItemError");
		}

		return map;
    }

    /**
     * 导入税率表方案 第二次进入
     * @param taxList 导入数据列表
     * @throws GeneralException
     */
    @Override
    public void importTaxTable(List<MorphDynaBean> taxList) throws GeneralException {
		for (MorphDynaBean map : taxList) {
			String type = (String) map.get("type");
			String id = PubFunc.decrypt((String) map.get("taxid"));
			String description = (String) map.get("description");
			String k_baseString = (String) map.get("k_base");
			Double k_base = Double.parseDouble(k_baseString);
			String param = (String) map.get("taxModeCode");
			//汉字转代码数字
			param = codeItemdescToItemid("46", param);
			//组装xml
			param = getImportXmlfromCode(param);
			List<MorphDynaBean> items = (List) map.get("items");
			if("1".equals(type)) {//追加
				importAdd(description, k_base, param, items);
			}else {//覆盖
				importCover(id, description, k_base, param, items);
			}
		}
    }
    /**
     * 导入第二次 追加操作
     * @param description description字段
     * @param k_base k_base字段
     * @param param param字段
     * @param items 明细表集合
     * @throws GeneralException
     */
    private void importAdd(String description,Double k_base,String param,List<MorphDynaBean> items) throws GeneralException {
    	try {
    		//获取税率表和明细表的最大id
    		String maxIdString = taxTableDao.getMaxId("gz_tax_rate");
    		int maxId = Integer.parseInt(maxIdString);
    		String itemMaxIdString = taxTableDao.getMaxId("gz_taxrate_item");
    		int itemMaxId = Integer.parseInt(itemMaxIdString);
    		//税率表
    		List insertList = new ArrayList();
    		RecordVo insertvo = new RecordVo("gz_tax_rate");
    		insertvo.setInt("taxid", maxId+1);
    		insertvo.setString("description", description);
    		insertvo.setDouble("k_base", k_base);
    		insertvo.setString("param", param);
    		insertList.add(insertvo);
    		//明细表
    		List itemInsertList = new ArrayList();
    		int count = 1;
    		for (MorphDynaBean itemmap : items) {
    			int taxitem = itemMaxId+count;
    			Double ynse_dowm = Double.parseDouble((String) itemmap.get("ynse_down"));
    			Double ynse_up = Double.parseDouble((String) itemmap.get("ynse_up"));
    			Double sl = Double.parseDouble((String) itemmap.get("sl"));
    			Double sskcs = Double.parseDouble((String) itemmap.get("sskcs"));
    			String flagString = (String) itemmap.get("flag");
    			int flag = (int)Double.parseDouble(flagString);
    			String mxdescription = (String) itemmap.get("description");
    			Double kc_base = Double.parseDouble((String) itemmap.get("kc_base"));
    			RecordVo itemvo = new RecordVo("gz_taxrate_item");
    			itemvo.setInt("taxitem",taxitem);
    			itemvo.setInt("taxid",maxId+1);
    			itemvo.setDouble("ynse_down", ynse_dowm);
    			itemvo.setDouble("ynse_up", ynse_up);
    			itemvo.setDouble("sl", sl);
    			itemvo.setDouble("sskcs", sskcs);
    			itemvo.setInt("flag", flag);
    			itemvo.setString("description", mxdescription);
    			itemvo.setDouble("kc_base", kc_base);
    			itemInsertList.add(itemvo);
    			count ++;
    		}
    		taxTableDao.insertTaxTable(insertList);
    		taxTableDao.insertTaxTable(itemInsertList);
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableTwoAddError"+e);
    		throw new GeneralException("gz.taxTable.msg.importTaxTableTwoAddError");
    	}
    }
    /**
     * 导入第二次 覆盖操作
     * @param description description字段
     * @param k_base k_base字段
     * @param param param字段
     * @param items 明细表集合
     * @throws GeneralException
     */
    private void importCover(String id,String description,Double k_base,String param,List<MorphDynaBean> items) throws GeneralException {
    	try {
    		//税率表
    		int taxid = Integer.parseInt(id);
			List updateList = new ArrayList();
			List delList = new ArrayList();
			RecordVo vo = new RecordVo("gz_tax_rate");
			vo.setInt("taxid", taxid);
			delList.add(vo);
			vo.setString("description", description);
			vo.setDouble("k_base", k_base);
			vo.setString("param", param);
			updateList.add(vo);
			//明细表
			String itemMaxIdString = taxTableDao.getMaxId("gz_taxrate_item");
    		int itemMaxId = Integer.parseInt(itemMaxIdString);
			List<DynaBean> list = taxTableDao.listTaxTableDetails(id);//获取之前的明细表数据
			List itemDeleteList = new ArrayList();
			for(DynaBean bean : list) {
				RecordVo taxidvo = new RecordVo("gz_taxrate_item");
				taxidvo.setInt("taxid", taxid);
				String itemidString = (String) bean.get("taxitem");
				int itemid = Integer.parseInt(PubFunc.decrypt(itemidString));
				taxidvo.setInt("taxitem", itemid);
				itemDeleteList.add(taxidvo);
			}
			List itemUpdateList = new ArrayList();

			for (MorphDynaBean itemmap : items) {
				//String taxitemString = (String) itemmap.get("taxitem");
				//int taxitem = Integer.parseInt(PubFunc.decrypt(taxitemString));
				Double ynse_dowm = Double.parseDouble((String) itemmap.get("ynse_down"));
				Double ynse_up = Double.parseDouble((String) itemmap.get("ynse_up"));
				Double sl = Double.parseDouble((String) itemmap.get("sl"));
				Double sskcs = Double.parseDouble((String) itemmap.get("sskcs"));
				String flagString = (String) itemmap.get("flag");
				int flag = (int)Double.parseDouble(flagString);
				String mxdescription = (String) itemmap.get("description");
				Double kc_base = Double.parseDouble((String) itemmap.get("kc_base"));
				RecordVo itemvo = new RecordVo("gz_taxrate_item");
				itemvo.setInt("taxid",taxid);
				//itemvo.setInt("taxitem",taxitem);
				itemMaxId++;
				itemvo.setInt("taxitem",itemMaxId);
				itemvo.setDouble("ynse_down", ynse_dowm);
				itemvo.setDouble("ynse_up", ynse_up);
				itemvo.setDouble("sl", sl);
				itemvo.setDouble("sskcs", sskcs);
				itemvo.setInt("flag", flag);
				itemvo.setString("description", mxdescription);
				itemvo.setDouble("kc_base", kc_base);
				itemUpdateList.add(itemvo);
			}
			taxTableDao.deleteTaxTable(delList);//先删除之前的，再增加
			taxTableDao.insertTaxTable(updateList);
			taxTableDao.deleteTaxTableDetail(itemDeleteList);
			taxTableDao.insertTaxTableDetail(itemUpdateList);
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.importTaxTableTwoCoverError"+e);
    		throw new GeneralException("gz.taxTable.msg.importTaxTableTwoCoverError");
    	}
    }
    /**
     * 获取税率明细表表格组件内容
     * @return
     * @throws GeneralException
     */
    @Override
    public String getTaxTableDetailConfig(String taxId) throws GeneralException {
        String taxTableDetailConfig = "";
        try {
            ArrayList<ButtonInfo> bottonList = new ArrayList<ButtonInfo>();
            ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
            //项目序号
            ColumnsInfo taxitem = getColumnsInfo("taxitem", ResourceFactory.getProperty("gz.columns.taxitem"),"N",50,"0");
			taxitem.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
            columnTmp.add(taxitem);
            //税率表ID
            ColumnsInfo taxid = getColumnsInfo("taxid", ResourceFactory.getProperty("system.taxTable.taxid"),"A",20,"0");
            taxid.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
            columnTmp.add(taxid);
            //级数
            ColumnsInfo series = getColumnsInfo("series",ResourceFactory.getProperty("gz.columns.series"),"N",50,"0");
            series.setRendererFunc("taxTableDetail.addImgFunc");
			series.setEditableValidFunc("taxTableDetail.edit");
            columnTmp.add(series);
            //应纳税所得额下限
            ColumnsInfo ynse_down = getColumnsInfo("ynse_down",ResourceFactory.getProperty("gz.columns.ynsd_dowm"),"N",120,"0");
			ynse_down.setValidFunc("taxTableDetail.dataValidFunc");
            ynse_down.setAllowBlank(false);
            columnTmp.add(ynse_down);
            //应纳税所得额上限
            ColumnsInfo ynse_up = getColumnsInfo("ynse_up",ResourceFactory.getProperty("gz.columns.ynsd_up"),"N",120,"0");
			ynse_up.setValidFunc("taxTableDetail.dataValidFunc");
            ynse_up.setAllowBlank(false);
            columnTmp.add(ynse_up);
            //税率
            ColumnsInfo sl = getColumnsInfo("sl",ResourceFactory.getProperty("gz.self.tax.sl"),"N",70,"0");
            sl.setValidFunc("taxTableDetail.dataSlValidFunc");
			sl.setAllowBlank(false);
            columnTmp.add(sl);
            //封闭标志
            ColumnsInfo flag = getColumnsInfo("flag",ResourceFactory.getProperty("gz.columns.taxflag"),"A",85,"0");
            flag.setRendererFunc("taxTableDetail.flagRenderFun");
            columnTmp.add(flag);
            //速算扣除数
            ColumnsInfo sskcs = getColumnsInfo("sskcs",ResourceFactory.getProperty("gz.self.tax.sskcs"),"N",150,"0");
			sskcs.setValidFunc("taxTableDetail.dataValidFunc");
            columnTmp.add(sskcs);
            //扣除基数
            ColumnsInfo kc_base = getColumnsInfo("kc_base",ResourceFactory.getProperty("gz.columns.kc_base"),"N",150,"0");
			kc_base.setValidFunc("taxTableDetail.dataValidFunc");
			kc_base.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
            columnTmp.add(kc_base);
            //说明
            ColumnsInfo description = getColumnsInfo("description",ResourceFactory.getProperty("kq.kq_rest.shuoming"),"A",220,"0");
			description.setColumnLength(250);
            columnTmp.add(description);
            //操作
            ColumnsInfo handle = getColumnsInfo("handle",ResourceFactory.getProperty("reportcyclelist.option"),"H",70,"0");
            handle.setRendererFunc("taxTableDetail.deleteFunc");
            handle.setEditableValidFunc("taxTableDetail.edit");
            handle.setTextAlign("center");
            columnTmp.add(handle);
            List<DynaBean> taxDetailList = taxTableDao.listTaxTableDetails(taxId);
			List<LazyDynaBean> taxDetailDataList = new ArrayList<LazyDynaBean>();
			int count = 1;
			for(DynaBean lazyDynaBean : taxDetailList){
				LazyDynaBean taxDetailData = new LazyDynaBean();
				taxDetailData.set("taxitem", PubFunc.decrypt((String) lazyDynaBean.get("taxitem")));
				taxDetailData.set("taxid", lazyDynaBean.get("taxid"));
				taxDetailData.set("series", count);
				taxDetailData.set("ynse_down", (String) lazyDynaBean.get("ynse_down")=="" ? "0":(String) lazyDynaBean.get("ynse_down"));
				taxDetailData.set("ynse_up", (String) lazyDynaBean.get("ynse_up")=="" ? "0":(String) lazyDynaBean.get("ynse_up"));
				taxDetailData.set("sl", (String) lazyDynaBean.get("sl")=="" ? "0":(String) lazyDynaBean.get("sl"));
				taxDetailData.set("sskcs", (String) lazyDynaBean.get("sskcs")=="" ? "0":(String) lazyDynaBean.get("sskcs"));
				taxDetailData.set("flag", (String) lazyDynaBean.get("flag")=="" ? "0":(String) lazyDynaBean.get("flag"));
				taxDetailData.set("kc_base", (String) lazyDynaBean.get("kc_base")=="" ? "0":(String) lazyDynaBean.get("kc_base"));
				taxDetailData.set("description", (String) lazyDynaBean.get("description"));
				taxDetailDataList.add(taxDetailData);
				count++;
			}
            TableConfigBuilder builder = new TableConfigBuilder("taxTableDetail",columnTmp,"taxTableDetail",this.userView,this.conn);
			builder.setDataList((ArrayList) taxDetailDataList);
            builder.setEditable(true);
            builder.setSetScheme(false);
            builder.setPageTool(false);
            builder.setTableTools(bottonList);
            taxTableDetailConfig = builder.createExtTableConfig();
        }catch (GeneralException e){
            e.printStackTrace();
            //初始化数据出错！
            throw new GeneralException(ResourceFactory.getProperty("kq.date.error.tablemsg"));
        }
        return taxTableDetailConfig;
    }

	/**
	 * 税率明细方案保存
	 * @param taxList 税率表方案记录map
	 * @param taxDetailList 税率表明细方案记录map
	 * @throws GeneralException
	 */
    @Override
    public String saveTaxTableDetail(List taxList,List taxDetailList) throws GeneralException {
		ArrayList recordTaxVoList = new ArrayList();
		ArrayList recordTaxDetailVoList = new ArrayList();
		ArrayList recordUpdateTaxDetailVoList = new ArrayList();
		String taxid = "";
		try {
			HashMap taxMap  = PubFunc.DynaBean2Map((MorphDynaBean)taxList.get(0));
			taxid = PubFunc.decrypt((String) taxMap.get("taxid"));
			if(StringUtils.isBlank(taxid)){//新增税率表
				taxid = taxTableDao.getMaxId("gz_tax_rate");
				String param = getImportXmlfromCode((String) taxMap.get("taxModeCode"));
				taxid = Integer.parseInt(taxid)+1+"";
				RecordVo taxVo = new RecordVo("gz_tax_rate");
				taxVo.setString("taxid",taxid);
				taxVo.setString("description", (String) taxMap.get("description"));
				taxVo.setString("k_base", (String) taxMap.get("k_base"));
				taxVo.setString("param",param);
				recordTaxVoList.add(taxVo);
			}
			for (int i = 0; i < taxDetailList.size(); i++) {
				HashMap map  = PubFunc.DynaBean2Map((MorphDynaBean)taxDetailList.get(i));
				String taxitem = (String) map.get("taxitem");
				RecordVo vo = new RecordVo("gz_taxrate_item");
				vo.setString("ynse_down", (String) map.get("ynse_down"));
				vo.setString("ynse_up", (String)map.get("ynse_up"));
				vo.setString("sl", (String) map.get("sl"));
				vo.setString("sskcs", (String) map.get("sskcs"));
				vo.setString("flag", (String) map.get("flag"));
				vo.setString("kc_base", (String) map.get("kc_base"));
				vo.setString("description", (String)map.get("description"));
				vo.setString("taxid",taxid);
				if(StringUtils.isBlank(taxitem)){
					taxitem = getMaxTaxitem("gz_taxrate_item");
					int count = Integer.parseInt(taxitem)+1+i;
					vo.setInt("taxitem",count);
					recordTaxDetailVoList.add(vo);
				}else{
					vo.setString("taxitem",taxitem);
					recordUpdateTaxDetailVoList.add(vo);
				}
			}
			taxTableDao.insertTaxTable(recordTaxVoList);
			taxTableDao.insertTaxTableDetail(recordTaxDetailVoList);
			taxTableDao.updateTaxTableDetail(recordUpdateTaxDetailVoList);
		}catch (GeneralException e){
			e.printStackTrace();
			throw new GeneralException("gz.taxTableHomePage.msg.saveTaxTableError");
		}
		return taxid;
    }


    /**
     * 删除税率表明细方案
     * @param id 税率表明细方案id
     * @throws GeneralException
     */
    @Override
    public void deleteTaxTableDetail(String id) throws GeneralException {
        try {
            List<RecordVo> voList = new ArrayList<RecordVo>();
            RecordVo voTaxDetail = null;
            String taxid = id.split("`")[0];
            String taxitem = id.split("`")[1];
            voTaxDetail = new RecordVo("gz_taxrate_item");
            voTaxDetail.setString("taxid",taxid);
            voTaxDetail.setString("taxitem",taxitem);
            voList.add(voTaxDetail);
            taxTableDao.deleteTaxTableDetail(voList);
        }catch (GeneralException e){
            e.printStackTrace();
			throw new GeneralException(ResourceFactory.getProperty("system.sdparameter.deleteHintFail"));
        }
    }

    /**
     * 获取计税方式代码项集合
     * @return 计税方式代码项集合
     * @throws GeneralException
     */
    @Override
    public List getTaxModeCodeItem() throws GeneralException {
        List taxModeCodeItem = this.taxTableDao.listTaxModeCodeItem();
        return taxModeCodeItem;
    }

	/**
	 * 检验税率表首页操作权限（编辑）
	 * @param funcid 权限ID
	 * @return
	 * @throws GeneralException
	 */
	@Override
	public boolean isHaveOperationPriv(String funcid) throws GeneralException {
		boolean isHavePriv = false;
		try {
			if (userView.isSuper_admin() || userView.hasTheFunction(funcid)) {
				isHavePriv = true;
			}
		}catch (Exception e){
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.isHaveOperationPrivError");
		}

		return isHavePriv;
	}

	/**
     * 获取最大的taxitem
     * @param taxName  税率表名
     * @return taxitem  最大taxitem值
     * */
    private String getMaxTaxitem(String taxName) throws GeneralException {
//        String taxitem = taxTableDao.getMaxTaxitemId(taxid);
		String taxitem = taxTableDao.getMaxId(taxName);
        return taxitem;
    }

    /**
     * 获取当前登录用户有权限的功能按钮
     *
     * @param funcid     功能按钮权限号
     * @param buttonList 功能按钮权集合
     * @param button     按钮对象
     */
    private void assemblyPrivButton(String funcid, List buttonList, Object button) {
        boolean isHasButton = false;
        if (userView.isSuper_admin() || userView.hasTheFunction(funcid)) {
            isHasButton = true;
        }
        if (isHasButton) {
            buttonList.add(button);
        }
    }

    /**
     * 导出取doc
     * @throws GeneralException
     */
    private void init() throws GeneralException{
		byte[] b = xml.getBytes();
		InputStream ip = new ByteArrayInputStream(b);
		try {
			doc = PubFunc.generateDom(ip);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
	}

    /**
	 * 导出组装税率表
	 * @param n 行
	 * @param map 表头
	 * @param workbook 表格整体
	 * @param sheet sheet表名
	 * @param ExcelName excel表名
	 * @return
	 */
	private int setTaxHead(int n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet,String ExcelName) throws GeneralException
	{
		int i=n;
		try
		{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFPatriarch patr=sheet.createDrawingPatriarch();
			//第一列 税率表型号
			HSSFComment comment1 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 6, 0));
			comment1.setString(new HSSFRichTextString("taxid"));
			comment1.setAuthor(ExcelName);
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER );
			row=sheet.createRow(i);
			csCell=row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
		    csCell.setCellComment(comment1);
			csCell.setCellValue((String)map.get("taxid"));
			//第二列 税率名称
			HSSFComment comment2 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 3, (short) 6, 1));
			comment2.setString(new HSSFRichTextString("description"));
			comment2.setAuthor(ExcelName);
			csCell=row.createCell((short)1);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment2);
			csCell.setCellValue((String)map.get("description"));
			//第三列 基本减除费用
			HSSFComment comment3 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 4, (short) 6, 2));
			comment3.setString(new HSSFRichTextString("k_base"));
			comment3.setAuthor(ExcelName);
			csCell=row.createCell((short)2);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment3);
			csCell.setCellValue((String)map.get("k_base"));
			//第四列 计税方式
			HSSFComment comment4 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 5, (short) 6, 3));
			comment4.setString(new HSSFRichTextString("param"));
			comment4.setAuthor(ExcelName);
			csCell=row.createCell((short)3);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment4);
			csCell.setCellValue((String)map.get("param"));
            i++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
		return i;
	}

	/**
     * 导出设置税率表的excel表头
     */
    private HashMap getTaxExcelHead() throws GeneralException
    {
    	HashMap map = new HashMap();
    	try
    	{
    		map.put("taxid",ResourceFactory.getProperty("gz.columns.type"));
    		map.put("description",ResourceFactory.getProperty("gz.columns.taxname"));
    		map.put("k_base",ResourceFactory.getProperty("gz.columns.basedata"));
    		map.put("param",ResourceFactory.getProperty("gz.columns.taxmode"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
    	}
    	return map;
    }
    /**
     * 导出设置税率明细表的excel表头
     * @return
     */
    private HashMap getTaxDetailExcelHead() throws GeneralException {
    	HashMap map = new HashMap();
    	try
    	{
    		map.put("taxid",ResourceFactory.getProperty("gz.columns.type"));
    		map.put("taxitem",ResourceFactory.getProperty("gz.columns.slbh"));
    		map.put("ynse_down",ResourceFactory.getProperty("gz.columns.ynsd_dowm"));
    		map.put("ynse_up",ResourceFactory.getProperty("gz.columns.ynsd_up"));
    		map.put("sl",ResourceFactory.getProperty("gz.columns.sl"));
    		map.put("sskcs",ResourceFactory.getProperty("gz.columns.sskcs"));
    		map.put("flag",ResourceFactory.getProperty("gz.columns.taxflag"));
    		map.put("description",ResourceFactory.getProperty("label.description"));
    		map.put("kc_base",ResourceFactory.getProperty("gz.columns.kc_base"));
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
    	}
    	return map;
    }

    /**
	 * 导出取得税率表信息
	 * @param taxid id
	 * @return
	 */
	private ArrayList getTaxTableInfo(String taxid) throws GeneralException{
		ArrayList list = new ArrayList();
		try
		{
			List<DynaBean> values = taxTableDao.listTaxTables(taxid);
			HashMap taxmap=getAllValues(taxid);
	    	for (DynaBean DynaBean : values) {
	    		String id = (String) DynaBean.get("taxid");
	    		id = PubFunc.decrypt(id);
	    		String description = (String) DynaBean.get("description");
	    		String k_base = (String) DynaBean.get("k_base");

                LazyDynaBean bean = new LazyDynaBean();
                bean.set("description",description);
                bean.set("k_base",getXS(String.valueOf(k_base),2));
    			if(taxmap !=null && taxmap.size()!=0) {
    				bean.set("param",AdminCode.getCodeName("46",(String)taxmap.get(id)));
    			}
    			else {
    				bean.set("param","");
    			}
                bean.set("taxid",id);
                list.add(bean);
    		}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
		return list;
	}
    /**
     * 导出保证列数据格式，小数点后两位
     * @param str 列数据
     * @param scale 小数点后几位
     * @return
     */
	private String getXS(String str,int scale){
    	if(str==null|| "null".equalsIgnoreCase(str)|| "".equals(str)) {
    		str="0.00";
    	}
    	BigDecimal m=new BigDecimal(str);
    	BigDecimal one = new BigDecimal("1");
    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
    }
	/**
	 *  导出获取 taxid和xml中param的对应
	 * @param taxid
	 * @return
	 * @throws GeneralException
	 */
	private HashMap getAllValues(String taxid) throws GeneralException{
		HashMap map = new HashMap();
		String[] id_Arr=taxid.split(",");
        for(int i=0;i<id_Arr.length;i++){
	        map.put(id_Arr[i],getParamValue(id_Arr[i]));
        }
        return map;
	}
	/**
	 *  导出获取 taxid和xml中param的对应,执行
	 * @param id
	 * @return
	 * @throws GeneralException
	 */
	private String getParamValue(String id) throws GeneralException {
		List<DynaBean> list = taxTableDao.listTaxTables(id);
		for (DynaBean dynaBean : list) {
			xml = (String) dynaBean.get("param");
		}
		String ctrl_str = "";
		this.init();
		try {
			XPath xpath = XPath.newInstance("/param");
			Element param = (Element)xpath.selectSingleNode(this.doc);
			if(param != null) {
				ctrl_str = param.getAttributeValue("TaxModeCode");
			}
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
		return ctrl_str;
	}
	/**
	 * 导出设置表格数据
	 * @param n
	 * @param infoList
	 * @param workbook
	 * @param sheet
	 * @param row
	 * @param cell
	 * @throws GeneralException
	 */
	private void setTaxData(int n,ArrayList infoList,HSSFWorkbook workbook,HSSFSheet sheet,HSSFRow row,HSSFCell cell) throws GeneralException
	{
		try
		{
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(false);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.RIGHT );
			HSSFFont font2 = workbook.createFont();
			font2.setColor(HSSFFont.COLOR_NORMAL);
			font2.setBold(false);
			HSSFCellStyle cellStyle2= workbook.createCellStyle();
			cellStyle2.setFont(font);
			cellStyle2.setAlignment(HorizontalAlignment.LEFT );
			for(int i=0;i<infoList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)infoList.get(i);
        		row = sheet.createRow(n);
        		cell=row.createCell((short)0);
        		cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
        		cell.setCellStyle(cellStyle);
        		cell.setCellValue((String)bean.get("taxid"));
        	   	cell=row.createCell((short)1);
	        	cell.setCellType(HSSFCell.CELL_TYPE_STRING);
	        	cell.setCellStyle(cellStyle2);
        		cell.setCellValue((String)bean.get("description"));
	        	cell=row.createCell((short)2);
	        	cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	        	cell.setCellStyle(cellStyle);
	        	cell.setCellValue((String)bean.get("k_base"));
	        	cell=row.createCell((short)3);
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        		cell.setCellStyle(cellStyle2);
	        	cell.setCellValue((String)bean.get("param"));
	        	n++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
	}
	/**
	 * 导出设置明细表数据
	 * @param n 循环行数
	 * @param map 要导出的数据
	 * @param workbook excel表总空间
	 * @param sheet sheet表名
	 * @param commentname excel名
	 * @return 数据总行数
	 */
	private short setTaxDetailHead(short n,HashMap map,HSSFWorkbook workbook,HSSFSheet sheet,String commentname) throws GeneralException{
		short i=n;
		try
		{
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFPatriarch patr=sheet.createDrawingPatriarch();
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER );
			row=sheet.createRow(i);
			HSSFComment comment0 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 2, (short) 6, 0));
			comment0.setString(new HSSFRichTextString("taxid"));
			comment0.setAuthor(commentname);
			csCell=row.createCell((short)0);
			csCell.setCellStyle(cellStyle);
			csCell.setCellComment(comment0);
			csCell.setCellValue((String)map.get("taxid"));
			csCell=row.createCell((short)1);
			HSSFComment comment1 = patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 3, (short) 6,1));
			comment1.setString(new HSSFRichTextString("taxitem"));
			comment1.setAuthor(commentname);
			csCell.setCellComment(comment1);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("taxitem"));
			csCell=row.createCell((short)2);
			HSSFComment comment2= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 4, (short) 6,2));
			comment2.setString(new HSSFRichTextString("ynse_down"));
			comment2.setAuthor(commentname);
			csCell.setCellComment(comment2);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("ynse_down"));
			csCell=row.createCell((short)3);
			HSSFComment comment3= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 5, (short) 6,3));
			comment3.setString(new HSSFRichTextString("ynse_up"));
			comment3.setAuthor(commentname);
			csCell.setCellComment(comment3);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("ynse_up"));
			csCell=row.createCell((short)4);
			HSSFComment comment4= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 6, (short) 6,4));
			comment4.setString(new HSSFRichTextString("sl"));
			comment4.setAuthor(commentname);
			csCell.setCellComment(comment4);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("sl"));
			csCell=row.createCell((short)5);
			HSSFComment comment5= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 7, (short) 6,5));
			comment5.setString(new HSSFRichTextString("sskcs"));
			comment5.setAuthor(commentname);
			csCell.setCellComment(comment5);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("sskcs"));
			csCell=row.createCell((short)6);
			HSSFComment comment6= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4,8, (short) 6,6));
			comment6.setString(new HSSFRichTextString("flag"));
			comment6.setAuthor(commentname);
			csCell.setCellComment(comment6);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("flag"));
			csCell=row.createCell((short)7);
			HSSFComment comment7= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 9, (short) 6,7));
			comment7.setString(new HSSFRichTextString("description"));
			comment7.setAuthor(commentname);
			csCell.setCellComment(comment7);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("description"));
			csCell=row.createCell((short)8);
			HSSFComment comment8= patr.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short)4, 10, (short) 6,8));
			comment8.setString(new HSSFRichTextString("kc_base"));
			comment8.setAuthor(commentname);
			csCell.setCellComment(comment8);
			csCell.setCellStyle(cellStyle);
			csCell.setCellValue((String)map.get("kc_base"));
            i++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
		return i;
	}
	/**
	 * 导出获取税率表明细数据
	 * @param ids
	 * @return 明细表数据集合
	 * @throws GeneralException
	 */
	private ArrayList getTaxDetailTableList(String ids) throws GeneralException{
		ArrayList list = new ArrayList();
    	try{
    		String[] idsArr = ids.split(",");
    		List<DynaBean> itemList = new ArrayList();
    		for (String id : idsArr){
    			List<DynaBean> listById = taxTableDao.listTaxTableDetails(id);
    			itemList.addAll(listById);
            }
    		DecimalFormat myformat1 = new DecimalFormat("########.###");
    		for (DynaBean DynaBean : itemList) {
    			String taxitem = (String) DynaBean.get("taxitem");
    			String taxid = (String) DynaBean.get("taxid");
    			taxitem = PubFunc.decrypt(taxitem);
    			taxid = PubFunc.decrypt(taxid);
	    		String ynse_down = (String) DynaBean.get("ynse_down");
	    		String ynse_up = (String) DynaBean.get("ynse_up");
	    		String sl = (String) DynaBean.get("sl");
	    		String sskcs = (String) DynaBean.get("sskcs");
	    		String flag = (String) DynaBean.get("flag");
	    		String description = (String) DynaBean.get("description");
	    		String kc_base = (String) DynaBean.get("kc_base");

    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("taxitem",taxitem);
    			bean.set("ynse_down",getXS(ynse_down,2));
    			bean.set("ynse_up",getXS(ynse_up,2));
    			bean.set("sl",sl==null?"0":myformat1.format(Double.parseDouble(sl)));
    			bean.set("sskcs",getXS(sskcs,2));
    			bean.set("flag",flag);
    			bean.set("description",description==null?"":description);
    			bean.set("kc_base",getXS(kc_base,2));
    			bean.set("taxid",taxid);
    			list.add(bean);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
    	}
    	return list;
	}
	/**
	 * 导出组装明细表的二维数组
	 * @param list 明细表数据
	 * @return 二维数组
	 * @throws GeneralException
	 */
	private String[][] getDetailData(ArrayList list) throws GeneralException{
		String[][] arr= new String[list.size()][9];
		try
		{
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)list.get(i);
				arr[i][0]=(String)bean.get("taxid");
				arr[i][1]=(String)bean.get("taxitem");
				arr[i][2]=(String)bean.get("ynse_down");
				arr[i][3]=(String)bean.get("ynse_up");
				arr[i][4]=(String)bean.get("sl");
				arr[i][5]=(String)bean.get("sskcs");
				arr[i][6]=(String)bean.get("flag");
				arr[i][7]=(String)bean.get("description");
				arr[i][8]=(String)bean.get("kc_base");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new GeneralException("gz.taxTable.msg.exportTaxTableError");
		}
		return arr;
	}

	/**
	 * 根据代码描述返回代码值
	 * @param codesetid 代码类ID
	 * @param codeItemdesc 代码项描述
	 * @return coedItemid 代码项ID
	 */
    private String codeItemdescToItemid(String codesetid,String codeItemdesc){
		String codeItemid = "";
		ArrayList<CodeItem> codeitemList = AdminCode.getCodeItemList(codesetid);
		for (CodeItem codeItem : codeitemList) {
			String codeDesc = codeItem.getCodename();
			if (StringUtils.equalsIgnoreCase(codeDesc.trim(), codeItemdesc.trim())) {
				codeItemid = codeItem.getCodeitem();
				break;
			}
		}

		return codeItemid;
	}

	/**
	 * 判断对象是否为空
	 * @param object 需要判断的对象
	 * @return
	 */
	private static boolean isEmpty(Object object) {
		return (object == null || "".equals(object));
	}
	/***
     * 导入时在XML中添加代码
     * @param code xml字符串
     * @return
     */
    private String getImportXmlfromCode(String code) throws GeneralException {

    	String xml = "<?xml version=\"1.0\" encoding=\"GB2312\"?><param />";
    	StringBuffer xmls = new StringBuffer();
    	Document doc=null;
    	String codeIdString = "";
    	try {
    		doc = PubFunc.generateDom(xml);
    		Element root = doc.getRootElement();
    		root.setAttribute("TaxModeCode",code);
		    XMLOutputter outputter = new XMLOutputter();
		    Format format=Format.getPrettyFormat();
   	     	format.setEncoding("UTF-8");
   	     	outputter.setFormat(format);
   	        xmls.setLength(0);
   	        xmls.append(outputter.outputString(doc));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTable.msg.codeToXmlError",e);
			throw new GeneralException("gz.taxTable.msg.codeToXmlError");
		}
    	return xmls.toString();
    }

    /**
     * 汉字可自适应列宽
     * @param sheet
     * @param size
     */
    private void setSizeColumn(HSSFSheet sheet, int size) {
        for (int columnNum = 0; columnNum < size; columnNum++) {
            int columnWidth = sheet.getColumnWidth(columnNum) / 256;
            for (int rowNum = 0; rowNum < sheet.getLastRowNum()+1; rowNum++) {
            	HSSFRow currentRow;
                //当前行未被使用过
                if (sheet.getRow(rowNum) == null) {
                    currentRow = sheet.createRow(rowNum);
                } else {
                    currentRow = sheet.getRow(rowNum);
                }

                if (currentRow.getCell(columnNum) != null) {
                	HSSFCell currentCell = currentRow.getCell(columnNum);
                    if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        int length = currentCell.getStringCellValue().getBytes().length;
                        if (columnWidth < length) {
                            columnWidth = length;
                        }
                    }
                }
            }
            sheet.setColumnWidth(columnNum, columnWidth * 256);
        }
    }
    /**
     * 兼容导入的flag列为汉字
     * @param flag
     * @return
     */
    private String getFlag(String flag) {
    	String ceiling = ResourceFactory.getProperty("jx.param.upmargin");
		String floor = ResourceFactory.getProperty("jx.param.downmargin");
		if(flag.equals(ceiling)) {
			flag = "0";
		}
		if(flag.equals(floor)) {
			flag = "1";
		}
		return flag;
    }

	/**
	 * 刷新税率明细表表格数据
	 * @param taxId 税率表ID
	 * @throws GeneralException
	 */
	@Override
	public void refsTableData(String taxId) throws GeneralException {
    	try {
			TableDataConfigCache configCache = (TableDataConfigCache) this.userView.getHm().get("taxTableDetail");
			List<DynaBean> taxDetailList = taxTableDao.listTaxTableDetails(taxId);
			List<LazyDynaBean> taxDetailDataList = new ArrayList<LazyDynaBean>();
			int count = 1;
			for(DynaBean lazyDynaBean : taxDetailList){
				LazyDynaBean taxDetailData = new LazyDynaBean();
				taxDetailData.set("taxitem", PubFunc.decrypt((String) lazyDynaBean.get("taxitem")));
				taxDetailData.set("taxid", lazyDynaBean.get("taxid"));
				taxDetailData.set("series", count);
				taxDetailData.set("ynse_down", (String) lazyDynaBean.get("ynse_down")=="" ? "0":(String) lazyDynaBean.get("ynse_down"));
				taxDetailData.set("ynse_up", (String) lazyDynaBean.get("ynse_up")=="" ? "0":(String) lazyDynaBean.get("ynse_up"));
				taxDetailData.set("sl", (String) lazyDynaBean.get("sl")=="" ? "0":(String) lazyDynaBean.get("sl"));
				taxDetailData.set("sskcs", (String) lazyDynaBean.get("sskcs")=="" ? "0":(String) lazyDynaBean.get("sskcs"));
				taxDetailData.set("flag", (String) lazyDynaBean.get("flag")=="" ? "0":(String) lazyDynaBean.get("flag"));
				taxDetailData.set("kc_base", (String) lazyDynaBean.get("kc_base")=="" ? "0":(String) lazyDynaBean.get("kc_base"));
				taxDetailData.set("description", (String) lazyDynaBean.get("description"));
				taxDetailDataList.add(taxDetailData);
				count++;
			}
			configCache.setTableData((ArrayList) taxDetailDataList);
			this.userView.getHm().put("taxTableDetail", configCache);
		}catch (Exception e){
    		e.printStackTrace();
			log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTableDetail.msg.reloadDataError",e);
			throw new GeneralException("gz.taxTableDetail.msg.reloadDataError");
		}

	}
	/**
	 * 删除税率明细表
	 * @param ids
	 */
	@Override
	public void deleteTaxTableDetail(ArrayList<String> ids) throws GeneralException{
		try {
			if(ids.size() != 0){
				for (String id : ids){
					String taxid = PubFunc.decrypt(id.split("`")[0]);
					String taxitem = id.split("`")[1];
					id = taxid + "`" + taxitem;
					this.deleteTaxTableDetail(id);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			log.error("/module/gz/gz_resource_zh_CN.js ---> gz.taxTableHomePage.msg.deleteTaxTableError",e);
			throw new GeneralException("gz.taxTableHomePage.msg.deleteTaxTableError");
		}
	}
}
