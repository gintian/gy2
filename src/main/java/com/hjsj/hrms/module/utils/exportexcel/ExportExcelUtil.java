package com.hjsj.hrms.module.utils.exportexcel;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.*;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.imageio.ImageIO;
import javax.sql.RowSet;
import java.awt.Color;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *<p>Title:导出excel工具类</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2015-6-27:下午04:08:28</p> 
 *@author lis
 *@version 1.0
 */
public class ExportExcelUtil {
	private Connection conn=null;
	private HSSFWorkbook  wb = new HSSFWorkbook();
	private HSSFSheet sheet=null;
	private HashMap<String,Object> fontMap = new HashMap<String, Object>();
	private HashMap<String, Font> XSSFFontMap = new HashMap<String, Font>();
	private boolean protect = false;//是否启用锁定页面(需要配合单元格样式columnLocked同时使用)
	private boolean exportZero = true;//当是数值型指标时，是否导出0，默认导出
	private boolean convertToZero = true;//当是数值型指标时，是否转换成0，默认true

	private boolean isTotal=false;//是否启用合计，若启用则认为最后一行为合计行。 zhanghua 2017-7-11
	private short headRowHeight = -1;//表头行高
	private short rowHeight = -1;//数据列行高
	private UserView userView;
	private String fileName;
	private boolean isPrintSetup = false;//是否设置纸张参数
	private boolean landscape = false;//纸张显示默认纵向false/横向true
	private short paperSize = HSSFPrintSetup.A4_PAPERSIZE;//纸张大小默认4A
	private String waterRemarkContent;// 水印内容 若不为空则视为需要增加水印
	private String password = "";// 锁表密码
	
	
	public ExportExcelUtil(Connection con)
	{
		this.conn=con;
	}

	public ExportExcelUtil(Connection con, UserView userView)
	{
		this.conn=con;
		this.userView = userView;
	}
	
	public HSSFWorkbook getWb() {
		return wb;
	}

	public void setWb(HSSFWorkbook wb) {
		this.wb = wb;
	}

	/**
	 * @author lis
	 * @date 2015-6-25
	 * @param headList
		 * 参数说明：
		 * headStyleMap：列头单元格样式，key的可选值：align(value值参考：HorizontalAlignment.CENTER),border(value值参考：HSSFCellStyle.BORDER_THIN),borderColor(value值参考：HSSFColor.BLACK.index));
	     * comment:列头注释(可选)；
	     * colType：该列单元格类型，A：字符型,N：数值型,D：日期型,M备注型；
	     * dateFormat：时间格式化(是日期型时)；
	     * colStyleMap:该列的样式(参考headStyleMap)；
	     * decwidth：小数点位数(是数值型时)；
	     * content：列头单元格对应内容；
	     * itemid：列头单元格对应指标代码；
	     * specialType：配置了该参数，会在单位部门名称前加代码id 例：1002:XX大学；"1"代表启用codesetid：代码类代号(如果该列是代码型,所填数据是代码,如果是代码名称则未空)；
	     * total:合计列标记 1为需要合计 否则不用合计 zhanghua 2017-7-11
	     * specialType：配置了该参数，会在单位部门名称前加代码id 例：1002:XX大学；"1"代表启用
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public ArrayList getExportData(ArrayList<LazyDynaBean> headList, String sql) throws SQLException {
        ContentDAO dao=new ContentDAO(this.conn);
        LazyDynaBean rowDataBean = null;
        LazyDynaBean dataBean = null;
        LazyDynaBean bean = new LazyDynaBean();
        Timestamp d = null;
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        String itemid = "";
        String itemtype = "";
        String codesetid = "";
        int decwidth = 0;
        String dateFormat = "";
        String specialType = "";//薪资下载模板特殊标识 sunjian 2017-7-5（暂时只有薪资使用该参数）
        SimpleDateFormat df = null;
        // 20180207 linbz 若SQL为空，应返回空list
        if(StringUtils.isBlank(sql))
            return dataList;
        
        RowSet rowSet = null;
        
        LazyDynaBean totalBean=new LazyDynaBean();
        try
        {
                rowSet=dao.search(sql);
                
                Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
                if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
                    display_e0122="0";      
                
                while (rowSet.next()) {
                    rowDataBean = new LazyDynaBean();
                    for (int i = 0; i < headList.size(); i++) {
                        dataBean=new LazyDynaBean();
                        bean = (LazyDynaBean) headList.get(i);
                        itemid = (String) bean.get("itemid");
                        itemtype = (String) bean.get("colType");
                        codesetid = (String) bean.get("codesetid");// 代码类id
                        specialType = (String) bean.get("specialType");
                        if(bean.get("decwidth") != null)
                            decwidth = Integer.parseInt((String)bean.get("decwidth"));
                        dateFormat = (String) bean.get("dateFormat");
                        if (StringUtils.isEmpty(codesetid))
                            codesetid = "0";
                        
                        if ("D".equals(itemtype)) {
                            //日期型
                            if (StringUtils.isEmpty(dateFormat))
                                df = new SimpleDateFormat("yyyy-MM-dd");
                            else
                                df = new SimpleDateFormat(dateFormat);
                            d = null;
                            d = rowSet.getTimestamp(itemid);
                            if (d != null)
                                dataBean.set("content", df.format(d));
                            else
                                dataBean.set("content", "");
                            rowDataBean.set(itemid, dataBean);
                        } else if ("M".equals(itemtype)) {
                            //是备注型
                            dataBean.set("content", Sql_switcher.readMemo(rowSet,itemid));
                            rowDataBean.set(itemid, dataBean);
                        } else if ("A".equals(itemtype) && !"0".equals(codesetid)) {
                            // 是代码类
                            String value = rowSet.getString(itemid);
                            //该指标的parentid
                            String pcodeitem = "";
                            if(StringUtils.isNotBlank(value))
                            {
                                if("um".equalsIgnoreCase(codesetid))//此处加此判断是为了适应潍柴的特殊情况，潍柴会在部门字段里面保存单位的代码值  
                                {
                                    String theUM="";                    
                                    if(Integer.parseInt(display_e0122)==0) {
                                        theUM=AdminCode.getCodeName("UM",value);
                                        if(StringUtils.isBlank(theUM))
                                            theUM=AdminCode.getCodeName("UN",value);
                                    }else
                                    {
                                        CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
                                        if(item!=null)
                                        {
                                            theUM=item.getCodename();
                                            //部门的时候判断是否需要显示单位，如果设置了参数为显示2级部门，且theCodename也显示了二级部门则不添加单位，否则添加单位
                                            //暂时只有薪资下载模板用到该参数specialType,配置了该参数，会在单位部门名称前加代码id 例：1002:XX大学
                                            if("1".equalsIgnoreCase(specialType) && !"e0122".equalsIgnoreCase(itemid)) {
                                                if(!"UN".equals(codesetid) && theUM.split(AdminCode.dept_seq).length-1 != Integer.parseInt(display_e0122)) {
                                                    pcodeitem = item.getPcodeitem();
                                                    item = AdminCode.getCode("UN",pcodeitem,Integer.parseInt(display_e0122));
                                                    theUM = item.getCodename() + "-" + theUM;
                                                }
                                                theUM = value + ":" + theUM;
                                            }else {
                                                theUM = StringUtils.isBlank(theUM)?AdminCode.getCode("UN",value,Integer.parseInt(display_e0122)).getCodeitem(): theUM;
                                            }
                                        }
                                        else
                                        {
                                            theUM=AdminCode.getCodeName("UM",value);
                                            if(StringUtils.isBlank(theUM))
                                                theUM=AdminCode.getCodeName("UN",value);
                                        }
                                    }
                                    dataBean.set("content",theUM);
                                }else{
                                    String content = "";
                                    //如果设置了display_e0122按display_e0122走 sunjian 2017-07-01
                                    if("UN".equals(codesetid)){
                                        CodeItem item=AdminCode.getCode("UN",value);
                                        if(item!=null)
                                            content=item.getCodename();
                                        else 
                                            content = AdminCode.getCodeName("UN", value);
                                        if(StringUtils.isBlank(content))
                                            content = AdminCode.getCodeName("UM", value);
                                    }else
                                        content = AdminCode.getCodeName(codesetid, value); 
                                    //暂时只有薪资下载模板用到该参数specialType,配置了该参数，会在单位部门名称前加代码id 例：1002:XX大学
                                    if("1".equalsIgnoreCase(specialType)) {
                                        if(!"b0110".equalsIgnoreCase(itemid) && "UN".equals(codesetid))
                                            content = value + ":" + content;
                                    }
                                    dataBean.set("content", content);
                                }
                            } else {
                                dataBean.set("content", "");
                            }
                            rowDataBean.set(itemid, dataBean);
                        } else if ("N".equals(itemtype)) {
                            //数字型
                            if (rowSet.getString(itemid) != null) {
                                if("rank_num".equals(itemid))
                                    dataBean.set("content", "999999".equals(PubFunc.round(rowSet.getString(itemid), decwidth))?"":PubFunc.round(rowSet.getString(itemid), decwidth));
                                else
                                    dataBean.set("content", PubFunc.round(rowSet.getString(itemid), decwidth));
                                
                                //计算数字合计行
                                if(bean.get("total")!=null&& "1".equals((String)bean.get("total"))){
                                    if(totalBean.get(itemid)!=null){
                                        LazyDynaBean b=(LazyDynaBean)totalBean.get(itemid);
                                        double sum=Double.parseDouble(b.get("content").toString())+ Double.parseDouble(PubFunc.round(rowSet.getString(itemid), decwidth));
                                        b.set("content",String.valueOf(sum));
                                        totalBean.set(itemid, b);
                                    }else{
                                        LazyDynaBean b=new LazyDynaBean();
                                        b.set("content", PubFunc.round(rowSet.getString(itemid), decwidth));
                                        totalBean.set(itemid, b);
                                    }
                                }
                                
                            } else {
                                if(bean.get("total")!=null&& "1".equals((String)bean.get("total"))) {//没有数据也插入一个为0的合计
                                    if(totalBean.get(itemid)==null){
                                        LazyDynaBean b=new LazyDynaBean();
                                        b.set("content", PubFunc.round("0", decwidth));
                                        totalBean.set(itemid, b);
                                    }
                                }

                                dataBean.set("content", "");
                            }
                            rowDataBean.set(itemid, dataBean);
                        } else {
                            if (rowSet.getString(itemid) != null)
                                dataBean.set("content", rowSet.getString(itemid));
                            else
                                dataBean.set("content", "");
                            rowDataBean.set(itemid, dataBean);
                        }
                    }
                    dataList.add(rowDataBean);
                }
                if(totalBean.getMap().size()>0){//添加合计行数据
                    this.setTotal(true);//合计行标记
                    dataList.add(totalBean);
                }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            PubFunc.closeDbObj(rowSet);
        }
        return dataList;
    }
	/**
	 * @Title: exportExcelBySql 
	 * @Description: TODO(使用sql语句导出excel，详细情况请参考方法exportExcel) 
	 * @param sheetName:导出excel文件的sheet的名称，可选;
	 * @param mergedCellList 合并单元格的数据集合
	 * @param headList 导出excel头部列
	 * @param sql 导出数据的查询语句
	 * @param dropDownMap 下拉数据
	 * @param headStartRowNum excel数据头部列从第几行开始
	 * @throws SQLException
	 * @throws IOException
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-8-20 上午09:16:00
	 */
	public String exportExcelBySql(String fileName,String sheetName,ArrayList<LazyDynaBean> mergedCellList,
			ArrayList<LazyDynaBean> headList,String sql,HashMap dropDownMap,int headStartRowNum) throws SQLException, IOException, GeneralException {
		RowSet rs = null;
		try {
		    int count = 0;
		    if(StringUtils.isNotEmpty(sql)) {
		        ContentDAO dao = new ContentDAO(this.conn);
		        rs = dao.search(sql);
		        rs.last();
		        count = rs.getRow();
		        
		        if((count * headList.size()) > 70000000) {
		            if(this.userView != null)
		                this.userView.getHm().put("msg", "导出数据过大，请分批导出！");
		            else
		                throw new GeneralException("", "导出数据过大，请分批导出！", "", "");
		            
		        } else if(this.userView != null)
		            this.userView.getHm().put("msg", "ok");
		        
		        if(this.userView != null) {
		        	this.userView.getHm().put("totalRows", count);   
		        	this.userView.getHm().put("exportRows", 0);     
		        }
		    }
			//超过10000000个单元格或列数超过255列采用xlsx格式导出
			this.fileName = fileName;
			if((count * headList.size()) > 10000000 || count > 120000 || headList.size() > 255 || this.fileName.toLowerCase().endsWith("xlsx")) {
                if(!this.fileName.toLowerCase().endsWith("xlsx"))
                    this.fileName += "x";
                
				this.createXSSFExcelSheet(this.fileName, sheetName, headList, sql, mergedCellList, dropDownMap, headStartRowNum);
			} else {
				ArrayList dataList = this.getExportData(headList, sql);
				this.exportExcel(fileName, sheetName, mergedCellList, headList, dataList, dropDownMap, headStartRowNum);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		return this.fileName;
	}
	/**
	 * 生成sheet
	 * @author lis
	 * @date 2015-6-25
	 * @param sheetName:导出excel文件的sheet的名称，可选;
	 * @param mergedCellList:合并单元格集合，可选;
		 * 集合中单个数据bean的参数说明：
		 * content：title值(必选);
		 * fromRowNum：合并单元格从第几行开始
		 * toRowNum：合并单元格到地几行结束
		 * fromColNum：合并单元格从第几列开始
		 * toColNum：合并单元格到第几列结束
		 * mergedCellStyleMap:bean样式map,可选值：
		 	* key:columnWidth,value:（列宽，数字，为short型）
		 	* key:align,value：(值参考:HorizontalAlignment.CENTER);单元格内容靠左或右或中
		 	* key:border,value:(值参考：HSSFCellStyle.BORDER_THIN);单元格边界
		 	* key:borderColor,value:(值参考：HSSFColor.BLACK.index);单元格边界颜色
		 	* key:fillPattern,value:(值参考：HSSFCellStyle.SOLID_FOREGROUND);单元格填充模式
		 	* key:fillForegroundColor,value:(值参考：HSSFColor.GREY_25_PERCENT.index);单元格前景色
		 	* key:fontName,value:(值参考：宋体);字体名称
		 	* key:fontSize,value:(值参考：15);字体大小
	 * @param headList:必选;
	 	 * headList中的数据类型：LazyDynaBean
		 * LazyDynaBean的参数说明：
		 * 
		 * 以下四个参数是可选，要么所有LazyDynaBean都有，要么都没有
		 * fromRowNum：合并单元格从那行开始
		 * toRowNum：合并单元格到哪行结束
		 * fromColNum：合并单元格从哪列开始
		 * toColNum：合并单元格从哪列结束
		 * 
		 * headStyleMap：列头单元格样式(参考titleBean);
	     * comment:列头注释(可选);
	     * colType：该列单元格类型，A：字符型,N：数值型,D：日期型,M备注型;
	     * dateFormat：时间格式化(是日期型时);
	     * colStyleMap:该列的样式(参考titleBean);
	     * decwidth：小数点位数(是数值型时);
	     * content：列头单元格对应内容;
	     * itemid：列头单元格对应指标代码;
	     * codesetid：代码类代号(如果该列是代码型,所填数据是代码,如果是代码名称则未空);
	     * columnHidden:该列是否隐藏;
	     * columnLocked:该列是否锁定，如果锁定，则导出的excel表格大部分功能不能使用，只能简单的编辑单元格字体大小、颜色等;
	 * @param dataList:必选;
		 * 参数说明：
		 * ArrayList里可选数据是dataList(List)或rowDataBean(LazyDynaBean),
		 * 如果是List则默认是排好序的数据,
		 * 如果是rowDataBean，数据格式为：rowDataBean.set(列头ID，dataBean(LazyDynaBean)); 
		 * dataBean可选参数：content、fromRowNum、toRowNum、fromColNum、toColNum
		 * 可通过setTotal方法启用合计，若启用合计则认为dataList最后一行为合计行 zhanghua 2017-7-11
	 * @param dropDownMap:下拉框数据，可选;
		 * 参数说明：
		 * key:列头名称，表示要在该列添加下拉框,
		 * value:ArrayList,要添加的数据list;
	 * @throws IOException 
	 * @throws GeneralException 
	 */
	public void exportExcel(String sheetName,ArrayList<LazyDynaBean> mergedCellList,
			ArrayList<LazyDynaBean> headList, ArrayList dataList, HashMap dropDownMap, int headStartRowNum)
			throws IOException, GeneralException {

		try {
			int page = 1;
			String excel_rows = SystemConfig.getPropertyValue("excel_rows");
            excel_rows = excel_rows != null && excel_rows.trim().length() > 0 ? excel_rows : "30000";
			int nrows = Integer.parseInt(excel_rows);
            nrows = nrows > 60000 ? 60000 : nrows; 
			LazyDynaBean rowDataList = null;
			LazyDynaBean dataBean = null;
			ArrayList dataArrayList = null;
			LazyDynaBean headBean = null;
			HSSFCell cell = null;
			HSSFRichTextString richTextString = null;
			HSSFDataFormat df = wb.createDataFormat();
			int rowNum = 0;
			HashMap dropDownDataTemp = new HashMap();
			HSSFRow row = null;
			// 如果没有数据则只输出表头
			if (dataList.size() == 0) {
				// 43565 linbz 20190115 sheetName与另一种方式createXSSFExcelSheet保持一致
				if (StringUtils.isBlank(sheetName))
					sheet = wb.createSheet("第"+ page +"页");
				else
					sheet = wb.createSheet(sheetName + "第"+ page +"页");// haosl 20161012
																				// 解决导出数据超过20000条时，不能导出的问题
				if (mergedCellList != null)
					addMergedCell(mergedCellList, headList.size());
				// 设置表格列标题
				setHead(dropDownMap, dropDownDataTemp, headList, headStartRowNum);
			}

			HashMap colStyle_Map = new HashMap();
			HSSFCellStyle style = null;
			HSSFCellStyle style_old = null;
			HashMap colStyleMap_old = new HashMap();
			for (int i = 0; i < dataList.size(); i++) {
				if (i == 0 || (i != 1 && i % nrows == 1)) {
					rowNum = 0;
					if (StringUtils.isBlank(sheetName))
						sheet = wb.createSheet("第"+ page +"页");
					else
						sheet = wb.createSheet(sheetName + "第"+ page +"页");// haosl 20161012
                    																// 解决导出数据超过20000条时，不能导出的问题
					page++;

					if (mergedCellList != null)
						addMergedCell(mergedCellList, headList.size());
					// 设置表格列标题
					setHead(dropDownMap, dropDownDataTemp, headList, headStartRowNum);
					rowNum = headStartRowNum + 1;

					if (this.protect)// 是否锁定为只读
						sheet.protectSheet(this.getPassword());// 保护当前页，只读
				}
				Boolean data_total = false;
				boolean isArrayList = false;
				if (dataList.get(i) instanceof ArrayList)// 判断数据是List还是LazyDynaBean
					isArrayList = true;
				if (isArrayList) {
					dataArrayList = (ArrayList) dataList.get(i);// 第i行数据
					if (this.isTotal && i == dataList.size() - 1)// 若启用合计，则认为最后一行为合计行
						data_total = true;
				} else {
					rowDataList = (LazyDynaBean) dataList.get(i);// 第i行数据
					if (this.isTotal && i == dataList.size() - 1)// 若启用合计，则认为最后一行为合计行
						data_total = true;
				}

				String content = "";
				int fromRowNum = rowNum;// 合并单元格从第几行开始
				int toRowNum = rowNum;// 合并单元格到地几行结束
				int fromColNum = 0;// 合并单元格从第几列开始
				int toColNum = 0;// 合并单元格到第几列结束

				row = sheet.getRow(rowNum);
				if (row == null)
					row = sheet.createRow(rowNum);

				// 给该行赋值
				for (int columnIndex = 0; columnIndex < headList.size(); columnIndex++) {
					headBean = (LazyDynaBean) headList.get(columnIndex);
					String headItemid = (String) headBean.get("itemid");// 列标题代码
					String type = (String) headBean.get("colType");// 该列的类型，D：日期，N：数字，A：字符

					int deciwidth = headBean.get("decwidth") == null ? 0
							: Integer.parseInt((String) headBean.get("decwidth"));// 小数点位数

					// 当前列是否锁定
					boolean columnLocked = headBean.get("columnLocked") == null ? false
							: (Boolean) headBean.get("columnLocked");

					if (isArrayList) {
						content = (String) dataArrayList.get(columnIndex);
						fromRowNum = rowNum;
						toRowNum = rowNum;
						fromColNum = toColNum;
					} else {
						if (rowDataList.get(headItemid) == null) {
							dataBean = new LazyDynaBean();
						} else
							dataBean = (LazyDynaBean) rowDataList.get(headItemid);
						
						content = (String) dataBean.get("content");

						if (dataBean.get("fromRowNum") != null && dataBean.get("toRowNum") != null
								&& dataBean.get("fromColNum") != null && dataBean.get("toColNum") != null) {
							fromRowNum = (Integer) dataBean.get("fromRowNum");
							toRowNum = (Integer) dataBean.get("toRowNum");
							fromColNum = (Integer) dataBean.get("fromColNum");
							toColNum = (Integer) dataBean.get("toColNum");
						} else {
							fromRowNum = rowNum;
							toRowNum = rowNum;
							fromColNum = toColNum;
						}
					}

					if (this.rowHeight != -1) {
						row.setHeight(this.rowHeight);
					}
					
					cell = row.getCell(fromColNum);
					if (cell == null)
						cell = row.createCell(fromColNum);
					HashMap colStyleMap = (HashMap) headBean.get("colStyleMap");
					
					//防止每一个格子都创建一个样式，否则高版本的情况下回缺线
					if (colStyle_Map.get(headItemid + "_" + columnIndex) != null) {
						style = (HSSFCellStyle) colStyle_Map.get(headItemid + "_" + columnIndex);
					}else {
						//如果map中对应的列有格式了，那么对应的列都会再上面取存入的格式进行输出，不用再次创建了
						boolean flag = compareMap2(colStyleMap_old,colStyleMap);
						//这个也只是，如果上一次和这次的样式一样，那么也不用新建了，直接用上一次旧的，这里存入每次的进行比较，意义不大
						colStyleMap_old = colStyleMap==null?null:(HashMap) colStyleMap.clone();
						if ("N".equals(type)) {
							if(flag && style_old != null) {
								style = style_old;
							}else {
								style = getStyle(colStyleMap, "");
								style_old = style;
							}
							int scale = deciwidth;
							StringBuffer buf = new StringBuffer();
							for (int k = 0; k < scale; k++) {
								buf.append("0");
							}
							
							String format = "";
							if(scale>0&&(colStyleMap== null||!colStyleMap.containsKey("dataFormat"))) {// 没有数据格式则设置默认格式
								format = "0." + buf.toString() + "_ ";
								style.setDataFormat(df.getFormat(format));
							}

							if (colStyleMap == null || !colStyleMap.containsKey("align"))
								style.setAlignment(HorizontalAlignment.RIGHT);

							style.setLocked(columnLocked);
						} else {
							style = getStyle(colStyleMap, "");
							// 没有数据格式则设置默认格式 ,并且为文本数据时，为列设置文本类型的样式 haosl 2017-07-26
							// 修改原因:如果使用默认格式，导出Excel后，数据前面有零的话Excel会自动把零去掉
							if (colStyleMap == null && "A".equals(headBean.get("colType"))) {
								style.setDataFormat(df.getFormat("@"));
							}
						}
						
						colStyle_Map.put(headItemid + "_" + columnIndex, style);
					}

					if ("N".equals(type)) {
						if (StringUtils.isBlank(content) && !convertToZero) {
							cell.setCellStyle(style);
							cell.setCellValue("");
						} else {
							if (StringUtils.isBlank(content) && data_total) {// 如果是空的 且是合计行 则直接写入空值
								cell.setCellValue("");
							} else {
								if (StringUtils.isBlank(content))
									content = "0";

								cell.setCellStyle(style);
								BigDecimal bd = new BigDecimal(content);
								bd = bd.setScale(deciwidth, BigDecimal.ROUND_HALF_UP);
								if (!exportZero && bd.doubleValue() == 0) {
									cell.setCellValue("");
								} else {
									//此处导致了导出excel复制粘贴到另一个excel中过大，但是如果修改成了toString,
									//问题会好，但是不能合计了【39853】【19783】参考这两个，暂时不动
									cell.setCellValue(bd.doubleValue());
								}
							}
						}
					} else {
						richTextString = new HSSFRichTextString(content);
						style.setLocked(columnLocked);
						cell.setCellStyle(style);
						cell.setCellValue(richTextString);
					}
					
					//xus 18/11/16 单个单元格添加颜色样式  fillForegroundColor：必须为 IndexedColors 中存在的颜色 
					if(dataBean != null && dataBean.get("singleCellStyle") != null){
						HashMap styleMap = (HashMap)dataBean.get("singleCellStyle");
						HSSFCellStyle style_ = getColorStyle(styleMap);
						if(style_!=null)
							cell.setCellStyle(style_);
					}
					
					if (data_total) {// 如果是总计行 则设置为灰色 zhanghua 2017-7-3
						HSSFCellStyle totalStyle = wb.createCellStyle();
						totalStyle.cloneStyleFrom(style);
						totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
						totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						cell.setCellStyle(totalStyle);
					}
					// 合并单元格
					if (fromRowNum != toRowNum || fromColNum != toColNum) {
						mergeCell(sheet, fromRowNum, (short) fromColNum, toRowNum, (short) toColNum);
					}
					
					toColNum++;
				}
				// 添加附件列
				if (isArrayList)
					setHyperLink(wb, row, headList, dataArrayList);
				else
					setHyperLink(wb, row, headList, rowDataList);

				rowNum++;
				if(this.userView != null)
		            this.userView.getHm().put("exportRows", i);     
			}

			rowNum--;
			setDropDownData(dropDownDataTemp, headStartRowNum + 1, rowNum);
			// 设置纸张参数
			if(isPrintSetup) {
				HSSFPrintSetup printSetup = sheet.getPrintSetup();
				printSetup.setPaperSize(paperSize);
				printSetup.setLandscape(landscape);
				// 设置边距
				sheet.setMargin(HSSFSheet.TopMargin, (double)0.3);
				sheet.setMargin(HSSFSheet.BottomMargin, (double)0.3);
				sheet.setMargin(HSSFSheet.LeftMargin, (double)0.3);
				sheet.setMargin(HSSFSheet.RightMargin, (double)0.1);
			}
			/**
			 * 输出水印
			 */
			if(StringUtils.isNotBlank(waterRemarkContent)) {
				String path = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "waterRemark.png";
				createWaterMark(waterRemarkContent, path);
				// 个数暂时固定 如有需要再另行调整
				putWaterRemarkToExcel(sheet, path, 2, 5, 8, 5, 2, 5, 5, 5);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			// dataList.clear();
			System.gc();
		}
	}
	/**
     * 添加附件列与附件列中对应的超链接，
     * 注：
     *     1.需要生成附件列的功能覆写此方法，在覆写的方法中处理业务
     *     2.此文件中此方法不允许修改
     * @param wb 
     * @param row  需要生成附件单元格对应的数据行
     * @param headList 列头
     * @param data 当前行对应的数据
     * @throws GeneralException
     */
    public void setHyperLink(HSSFWorkbook wb, HSSFRow row, ArrayList<LazyDynaBean> headList, 
            Object data) throws GeneralException {
        
    }
    
    /**
     * 添加附件列与附件列中对应的超链接，
     * 注：
     *     1.需要生成附件列的功能覆写此方法，在覆写的方法中处理业务
     *     2.此文件中此方法不允许修改
     * @param wb 
     * @param row  需要生成附件单元格对应的数据行
     * @param headList 列头
     * @param data 当前行对应的数据
     * @throws GeneralException
     */
    public void setSXSSFHyperLink(SXSSFWorkbook wb, Row row, ArrayList<LazyDynaBean> headList, 
            Object data) throws GeneralException {
        
    }
    /**
     * 兼容旧版poi合并单元格功能调用此方法
     * @param sheet
     * @param firstRow
     * @param firstCol
     * @param lastRow
     * @param lastCol
     * @throws GeneralException
     */
    public static void mergeCell(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol) throws GeneralException {
    	try {
    		if(firstRow < lastRow || firstCol < lastCol)
    			sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(e);
		}
        
    }
    
    /**
     * 生成sheet
     * @param headList
     * @param fields
     * @param out
     * @param styles
     * @param execMethod
     * @param params
     * @throws Exception
     */
    private void createXSSFExcelSheet(String fileName, String sheetName, ArrayList<LazyDynaBean> headList,
    		String sql, ArrayList<LazyDynaBean> mergedCellList, HashMap dropDownMap, int headStartRowNum) throws Exception {
        RowSet rs = null;
        // 在内存当中保持 100 行 , 超过的数据放到硬盘中
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		SXSSFSheet sheet = null;
		//  sql2000=8    sql2005=9    sql2008=10    sql2012=11
		int version = 8;
		DbWizard db = new DbWizard(this.conn);
		int row_num_old = 0;//记录下拉框，因为在内存当中保持 100 行 , 超过的数据放到硬盘中，这样如果超过100行在最后创建下拉框，会读不到第一个row
		//sql2000中使用到的临时表表名
		UUID uuid = UUID.randomUUID();
		String guid = uuid.toString().toUpperCase(); 
		String tempTableName = "Temp_" + guid.split("-")[0] + "Excel";
        try {
        	if(db.isExistTable(tempTableName, false))
            	db.dropTable(tempTableName);
        	
        	DatabaseMetaData dataBase = this.conn.getMetaData();
        	if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
				version = dataBase.getDatabaseMajorVersion();
			}

            Row row = null;
            String excel_rows = SystemConfig.getPropertyValue("excel_rows");
            excel_rows = excel_rows != null && excel_rows.trim().length() > 0 ? excel_rows : "30000";
            int nrows = Integer.parseInt(excel_rows);
            nrows = nrows > 60000 ? 60000 : nrows; 
            
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setColor(HSSFFont.COLOR_NORMAL);
            font.setBold(true);
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setRightBorderColor(IndexedColors.BLACK.index);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setTopBorderColor(IndexedColors.BLACK.index);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setWrapText(false);
            
            int pageRow = 20;
            int fieldCount = headList.size();
            if (fieldCount <= 30)
                pageRow = 20000;
            else if (fieldCount > 30 && fieldCount <= 50)
                pageRow = 10000;
            else if (fieldCount > 50 && fieldCount <= 100)
                pageRow = 2000;
            else if (fieldCount > 100 && fieldCount <= 200)
                pageRow = 2000;
            else if (fieldCount > 200)
                pageRow = 1000;
            
            pageRow = pageRow > 5000 ? 5000 : pageRow;
            int exportRows = 0;
            // 查询起始页
            int pageIndex = 1;
            // 是否是最后一页，循环条件
            Boolean isEnd = true;
            ContentDAO dao = new ContentDAO(this.conn);
            
            //没有排序时，默认按查询结果显示的顺序生成行号
            String orderBy = "order by (select 0)";
            if(Sql_switcher.searchDbServer() == Constant.ORACEL)
                orderBy = "order by (select 0 from dual)";
            
            if(sql.toLowerCase().indexOf("order by") > -1) {
                orderBy = sql.substring(sql.toLowerCase().lastIndexOf("order by"));
                sql = sql.substring(0, sql.toLowerCase().lastIndexOf("order by"));
            }
            
            String pagination = sql.replaceFirst("select", "select ROW_NUMBER() over (" +orderBy + ") as rn,");
            if(db.isExistTable(tempTableName, false))
            	db.dropTable(tempTableName);
            
            if(Sql_switcher.dbflag == Constant.MSSQL && version < 9) {
            	String strSql = sql.replaceFirst("select", "select IDENTITY(INT,1,1) as rn,");
            	strSql = strSql.replaceFirst("from", "into " + tempTableName + " from");
            	strSql = strSql + orderBy;
            	dao.update(strSql);
            }
            
            HashMap dropDownDataTemp = new HashMap();
            int rowNum = 0;
            StringBuffer buf = new StringBuffer();
            DataFormat df = wb.createDataFormat();
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
            while (isEnd) {// 开始分页查询
            	
            	buf.setLength(0);
                if(Sql_switcher.dbflag == Constant.MSSQL && version < 9) {
                	buf.append("select * from " + tempTableName);
                	buf.append(" where rn>" + ((pageIndex-1) * pageRow));
                	buf.append(" and rn<=" + (pageIndex * pageRow));
                	buf.append(" order by rn");
                } else {
                	buf.append("select * from (");
                	buf.append(pagination);
                	buf.append(") temp");
                	buf.append(" where rn>" + ((pageIndex-1) * pageRow));
                	buf.append(" and rn<=" + (pageIndex * pageRow));
                	buf.append(" order by rn");
                }
                
                HashMap cellStyleMap = new HashMap();
                int fromRowNum = rowNum;// 合并单元格从第几行开始
				int toRowNum = rowNum;// 合并单元格到地几行结束
				int fromColNum = 0;// 合并单元格从第几列开始
				int toColNum = 0;// 合并单元格到第几列结束
                // 查询数据
                rs = dao.search(buf.toString());
                int pageRowNum = 0;
                while (rs.next()) {
                	pageRowNum++;
                	int m = exportRows%nrows;
                    if(m==0){
                        if(rowNum != 0) {
                            for(int j = 0; j <=headList.size(); j++) 
                                sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)6000);
                        }
                        
                        if(StringUtils.isEmpty(sheetName))
                        	sheet = wb.createSheet("第"+ (exportRows / nrows + 1) +"页");
                        else
                        	sheet = wb.createSheet(sheetName+ "第"+(exportRows / nrows + 1) +"页");

                        if (this.protect)// 是否锁定为只读
    						sheet.protectSheet(this.getPassword());// 保护当前页，只读
                        
                        rowNum = 0;
                    }
                    
                    if(exportRows == 0 || m == 0){
                        if (mergedCellList != null)
                        	addXSSFMergedCell(wb,sheet,mergedCellList, headList.size());
                        
                        setXSSFHead(wb, sheet, dropDownMap, dropDownDataTemp, headList, headStartRowNum);
                        rowNum = headStartRowNum + 1;
                    }
                    
                    fromRowNum = rowNum;// 合并单元格从第几行开始
    				toRowNum = rowNum;// 合并单元格到地几行结束
    				fromColNum = 0;// 合并单元格从第几列开始
    				toColNum = 0;// 合并单元格到第几列结束
    				
                    // 写方法-------
                    row = sheet.createRow(rowNum);
                    row.setHeight((short) 400);
                    for (int i = 0; i < headList.size(); i++) {
                    	LazyDynaBean headBean = headList.get(i);
                    	// 列标题代码
    					String headItemid = (String) headBean.get("itemid");
    					// 该列的类型，D：日期，N：数字，A：字符
    					String type = (String) headBean.get("colType");
    					// 代码类id
    					String codesetid = headBean.get("codesetid")==null?"":(String) headBean.get("codesetid");
    					// 小数点位数
    					int deciwidth = headBean.get("decwidth") == null ? 0
    							: Integer.parseInt((String) headBean.get("decwidth"));
    					// 当前列是否锁定
    					boolean columnLocked = headBean.get("columnLocked") == null ? false
    							: (Boolean) headBean.get("columnLocked");
    					//数据格式
    					String dateFormat = (String) headBean.get("dateFormat");
    					fromRowNum = rowNum;
    					toRowNum = rowNum;
    					fromColNum = toColNum;

    					if (this.rowHeight != -1) {
    						row.setHeight(this.rowHeight);
    					}
    					
    					Cell cell = row.getCell(fromColNum);
    					CellStyle style = null;
    					if (cell == null)
    						cell = row.createCell(fromColNum);
    					HashMap colStyleMap = (HashMap) headBean.get("colStyleMap");

    					if (cellStyleMap.get(headItemid + "_" + i) != null)
    						style = (CellStyle) cellStyleMap.get(headItemid + "_" + i);
    					else {
    						if ("N".equals(type)) {
    							style = getXSSFStyle(wb, colStyleMap, "");
    							int scale = deciwidth;
    							StringBuffer formatStyle = new StringBuffer();
    							for (int k = 0; k < scale; k++) {
    								formatStyle.append("0");
    							}
    							
    							String format = "";
    							if (scale > 0 && colStyleMap == null) {// 没有数据格式则设置默认格式
    								format = "0." + formatStyle.toString() + "_ ";
    								style.setDataFormat(df.getFormat(format));
    							}

    							if (colStyleMap == null || colStyleMap.get("align") == null)
    								style.setAlignment(HorizontalAlignment.RIGHT);

    							style.setLocked(columnLocked);
    						} else {
    							style = getXSSFStyle(wb, colStyleMap, "");
    							// 没有数据格式则设置默认格式 ,并且为文本数据时，为列设置文本类型的样式 haosl 2017-07-26
    							// 修改原因:如果使用默认格式，导出Excel后，数据前面有零的话Excel会自动把零去掉
    							if (colStyleMap == null && "A".equals(headBean.get("colType"))) {
    								style.setDataFormat(df.getFormat("@"));
    							}
    						}

    						cellStyleMap.put(headItemid + "_" + i, style);
    					}

    					if ("N".equals(type)) {
    						String value = rs.getString(headItemid);
    						if (StringUtils.isEmpty(value) && !convertToZero) {
    							cell.setCellStyle(style);
    							cell.setCellValue("");
    						} else {
    							if (StringUtils.isEmpty(value))
    								value = "0";
    							
    							cell.setCellStyle(style);
    							BigDecimal bd = new BigDecimal(value);
    							bd = bd.setScale(deciwidth, BigDecimal.ROUND_HALF_UP);
    							if (!exportZero && bd.doubleValue() == 0)
    								cell.setCellValue("");
    							else
    								cell.setCellValue(bd.doubleValue());
    							
    						}
    					} else if ("D".equals(type)) {
    						Timestamp aDate = rs.getTimestamp(headItemid);
    						style.setLocked(columnLocked);
    						cell.setCellStyle(style);
    						cell.setCellValue(formatDate(aDate, dateFormat));
    					} else {
    						String value = rs.getString(headItemid);
    						if(StringUtils.isNotBlank(codesetid) && !"0".equalsIgnoreCase(codesetid)) {
    							String itemDesc = AdminCode.getCodeName(codesetid, value);
    							if("UM".equalsIgnoreCase(codesetid)) {
    								CodeItem item=AdminCode.getCode("UM",value,Integer.parseInt(display_e0122));
    								if(item != null)
    									itemDesc = item.getCodename();
    								
    								if(StringUtils.isEmpty(itemDesc))
    									itemDesc = AdminCode.getCodeName("UN", value);
    							}
    							
    							value = itemDesc;
    						}
    						
    						cell.setCellValue(value);
    						style.setLocked(columnLocked);
    						cell.setCellStyle(style);
    					}
    					//用sql语句查询时，暂时用不到合计行  此处先注释掉
//    					if (data_total) {// 如果是总计行 则设置为灰色 zhanghua 2017-7-3
//    						CellStyle totalStyle = wb.createCellStyle();
//    						totalStyle.cloneStyleFrom(style);
//    						totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//    						totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//    						cell.setCellStyle(totalStyle);
//    					}
    					// 合并单元格
    					if (fromRowNum != toRowNum || fromColNum != toColNum) {
    						mergeCell(sheet, fromRowNum, (short) fromColNum, toRowNum, (short) toColNum);
    					}
    					
    					toColNum++;
    				}
    				// 添加附件列
                    setSXSSFHyperLink(wb, row, headList, rs);
    				
                    
                	rowNum++;
                    exportRows++;
                    if(this.userView != null)
                    	userView.getHm().put("exportRows", exportRows);
                    
                    //因为每100行，就给存到硬盘中，无法读取，这样只能在这100行存入之前，先getRow,创建下拉框所需要的内容到相应的位置，最后创建下拉框的引用，对应关系
                    if(rowNum%100 == 0) {
                    	setDropDownData_xlxs(dropDownDataTemp,  sheet,rowNum, row_num_old, "0");
                    	row_num_old = rowNum;
                    }
                    	
                }

                pageIndex++;
                if (pageRowNum < pageRow)
                    isEnd = false;

            }
            
            if(rowNum == 0) {
            	if(StringUtils.isEmpty(sheetName))
                	sheet = wb.createSheet(sheetName + "第"+(exportRows / nrows + 1)+"页");
                else
                	sheet = wb.createSheet(sheetName + "第"+(exportRows / nrows + 1)+"页");

            	if (this.protect)// 是否锁定为只读
					sheet.protectSheet(this.getPassword());// 保护当前页，只读
            	
                row=sheet.createRow(rowNum);
                row.setHeight((short) 400);
                if (mergedCellList != null)
					addMergedCell(mergedCellList, headList.size());
                
                setXSSFHead(wb, sheet, dropDownMap, dropDownDataTemp, headList, headStartRowNum);
            }
            
            //如果下拉列远大于行数，如A下拉有500行，B下拉有10行，500行创建完之后sheet中getRow只能获得400到500，这样在创建B列时，第一行不再sheet中，再创建就会报错
            int count = row_num_old;
            //找到长度最长的，然后每100行执行一次
            for (Iterator it = dropDownDataTemp.keySet().iterator(); it.hasNext();) {
            	int key = (Integer) it.next();// 第key列
				ArrayList arrayList = (ArrayList) dropDownDataTemp.get(key);
				int count_temp = arrayList.size();
				if(count_temp > count) {
					count = count_temp;
				}
            }
            
            while((count-row_num_old) > 100) {
            	rowNum = row_num_old + 100;
            	setDropDownData_xlxs(dropDownDataTemp, sheet, rowNum, row_num_old, "0");
            	row_num_old = row_num_old + 100;
            }
            //因为每100行，就给存到硬盘中，无法读取，这样只能在这100行存入之前，先getRow,创建下拉框所需要的内容到相应的位置，最后创建下拉框的引用，对应关系
            //无论最后是什么样的，都创建100行吧，效率上影响不大
            setDropDownData_xlxs(dropDownDataTemp, sheet, row_num_old+100, row_num_old, "1");
            //创建对应关系
            setDropDownData_xlxs_create(dropDownDataTemp, headStartRowNum + 1, rowNum, sheet);
            FileOutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + fileName);
            wb.write(out);
            out.close();
            wb.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            db.dropTable(tempTableName);
        }
    }
    /**
     * 按指标定义的长度格式化日期时间数据
     * 
     * @param aDate
     * @param dateFormat
     * @return
     * @author zhaoxj
     */
    private String formatDate(Timestamp aDate, String dateFormat) {
    	if (aDate == null)
            return "";
        
        if(StringUtils.isBlank(dateFormat))
        	dateFormat = "yyyy-MM-dd";
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(aDate);
    }
	/**
	 * @Title: exportExcel 
	 * @Description: TODO(导出excel，可以自定义sheet的名称) 
	 * @param fileName
	 * @param sheetName
	 * @param mergedCellList
	 * @param headList
	 * @param dataList
	 * @param dropDownMap
	 * @param headStartRowNum
	 * @throws IOException
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-21 上午09:16:02
	 */
	public void exportExcel(String fileName,String sheetName,ArrayList<LazyDynaBean> mergedCellList,
			ArrayList<LazyDynaBean> headList,ArrayList dataList,HashMap dropDownMap,int headStartRowNum) throws IOException, GeneralException {
		try {
			this.exportExcel(sheetName, mergedCellList, headList, dataList, dropDownMap, headStartRowNum);
			this.exportExcel(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 * 导出多个sheet的excel
	 * @param fileName
	 * @param sheetName
	 * @param mergedCellList
	 * @param headList
	 * @param dataList
	 * @param dropDownMap
	 * @param headStartRowNum
	 * @throws IOException
	 * @throws GeneralException
	 */
	public void exportExcelWithMultiSheets(String fileName,ArrayList<LazyDynaBean> mergedCellList,
			ArrayList<LazyDynaBean> headList,ArrayList dataList,HashMap dropDownMap,int headStartRowNum) throws IOException, GeneralException {
		try {
			this.exportExcelWithMultiSheets(mergedCellList, headList, dataList, dropDownMap, headStartRowNum);
			this.exportExcel(fileName);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		
	}
	/**
	 *  
	 * @param sheetName
	 * @param mergedCellList
	 * @param headList
	 * @param dataList
	 * @param dropDownMap
	 * @param headStartRowNum
	 * @throws IOException 
	 * @throws GeneralException 
	 */
	public void exportExcelWithMultiSheets(ArrayList<LazyDynaBean> mergedCellList,
			ArrayList headList, ArrayList dataList, HashMap dropDownMap, int headStartRowNum) throws GeneralException, IOException {
		for(int i=0;i<headList.size();i++) {
			String sheetName = "";
			ArrayList headlist = new ArrayList();
			ArrayList datalist = new ArrayList();
			HashMap headmap = (HashMap) headList.get(i);
			HashMap datamap = (HashMap) dataList.get(i);
			for(Object key : headmap.keySet()){
			   sheetName = (String)key;
			   headlist = (ArrayList)headmap.get(key);
			   datalist = (ArrayList)datamap.get(key);	
			}
			this.exportExcel(sheetName, mergedCellList, headlist, datalist, dropDownMap, headStartRowNum);
		}
	}	
	/**
	 * @Title: exportExcel 
	 * @Description: TODO(导出excel) 
	 * @param fileName 导出excel文件名
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-20 上午10:13:41
	 */
	public void exportExcel(String fileName) throws GeneralException{
		FileOutputStream fileOut = null;
		try {
			String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
			fileOut = new FileOutputStream(url);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeIoResource(fileOut);
			wb = null;
		}
		 
	}
	
	/**
	 * @Title: executeCell 
	 * @Description: TODO(设置单元格) 
	 * @param fromRowNum 单元格从哪行开始
	 * @param fromColNum 单元格从哪列开始
	 * @param toRowNum 单元格从哪行结束
	 * @param toColNum  单元格从哪列结束
	 * @param content 单元格内容
	 * @param cellStyle 单元格样式
	 * @param comment 单元格批注
	 * @param patr 
	 * @author lis   
	 * @date 2015-8-21 上午10:29:36
	 */
	public void executeCell(int fromRowNum, int fromColNum, int toRowNum, int toColNum, String content,HSSFCellStyle cellStyle,String comment,HSSFPatriarch patr) {

		HSSFComment comm = null;
		//取得第fromRowNum行
		HSSFRow row = sheet.getRow(fromRowNum);
		if(row==null)
			row = sheet.createRow(fromRowNum);
		//linbz
		if(this.headRowHeight != -1){
			 row.setHeight(this.headRowHeight);
		}
		//取得fromColNum列的单元格
		HSSFCell cell = row.getCell(fromColNum);
		if(cell==null)
			cell = row.createCell(fromColNum);
		//设置该单元格样式
		cell.setCellStyle(cellStyle);
		//给该单元格赋值
		cell.setCellValue(new HSSFRichTextString(content));
		if(StringUtils.isNotBlank(comment)){//当注释不为空时
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (fromColNum + 1), 0, (short) (fromColNum + 2), 1));
			comm.setString(new HSSFRichTextString(comment));
			cell.setCellComment(comm);
		}
		
		int fromColNum1 = fromColNum;
		while (++fromColNum1 <= toColNum) {
			cell = row.getCell(fromColNum1);
			if(cell==null)
				cell = row.createCell(fromColNum1);
			cell.setCellStyle(cellStyle);
		}
		for (int fromRowNum1 = fromRowNum + 1; fromRowNum1 <= toRowNum; fromRowNum1++) {
			row = sheet.getRow(fromRowNum1);
			if(row==null)
				row = sheet.createRow(fromRowNum1);
			fromColNum1 = fromColNum;
			while (fromColNum1 <= toColNum) {
				cell = row.getCell(fromColNum1);
				if(cell==null)
					cell = row.createCell(fromColNum1);
				cell.setCellStyle(cellStyle);
				fromColNum1++;
			}
		}
		//合并单元格
		if(toRowNum > fromRowNum || toColNum > fromColNum) {
			CellRangeAddress cra=new CellRangeAddress(fromRowNum, toRowNum, fromColNum, toColNum);
			 sheet.addMergedRegion(cra);
			 RegionUtil.setBorderBottom(BorderStyle.THIN, cra, sheet);
			 RegionUtil.setBorderTop(BorderStyle.THIN, cra, sheet);
			 RegionUtil.setBorderLeft(BorderStyle.THIN, cra, sheet);
			 RegionUtil.setBorderRight(BorderStyle.THIN, cra, sheet);
		}
				
	}
	/**
	 * @Title: executeXSSFCell 
	 * @Description: TODO(设置单元格) 
	 * @param fromRowNum 单元格从哪行开始
	 * @param fromColNum 单元格从哪列开始
	 * @param toRowNum 单元格从哪行结束
	 * @param toColNum  单元格从哪列结束
	 * @param content 单元格内容
	 * @param cellStyle 单元格样式
	 * @param comment 单元格批注
	 * @param patr 
	 */
	public void executeXSSFCell(SXSSFSheet sheet, int fromRowNum, int fromColNum, int toRowNum, int toColNum,
			String content,	CellStyle cellStyle,String comment,SXSSFDrawing patr) {
		
		Comment comm = null;
		//取得第fromRowNum行
		Row row = sheet.getRow(fromRowNum);
		if(row==null)
			row = sheet.createRow(fromRowNum);
		//linbz
		if(this.headRowHeight != -1){
			row.setHeight(this.headRowHeight);
		}
		//取得fromColNum列的单元格
		Cell cell = row.getCell(fromColNum);
		if(cell==null)
			cell = row.createCell(fromColNum);
		//设置该单元格样式
		cell.setCellStyle(cellStyle);
		//给该单元格赋值
		cell.setCellValue(content);
		if(StringUtils.isNotEmpty(comment)){//当注释不为空时
			comm = patr.createCellComment(new XSSFClientAnchor(0, 0, 0, 1, (short) fromColNum , 0, (short) (fromColNum + 1), 1));
			comm.setString(new XSSFRichTextString(comment));
			cell.setCellComment(comm);
		}
		
		int fromColNum1 = fromColNum;
		while (++fromColNum1 <= toColNum) {
			cell = row.getCell(fromColNum1);
			if(cell==null)
				cell = row.createCell(fromColNum1);
			cell.setCellStyle(cellStyle);
		}
		for (int fromRowNum1 = fromRowNum + 1; fromRowNum1 <= toRowNum; fromRowNum1++) {
			row = sheet.getRow(fromRowNum1);
			if(row==null)
				row = sheet.createRow(fromRowNum1);
			fromColNum1 = fromColNum;
			while (fromColNum1 <= toColNum) {
				cell = row.getCell(fromColNum1);
				if(cell==null)
					cell = row.createCell(fromColNum1);
				cell.setCellStyle(cellStyle);
				fromColNum1++;
			}
		}
		//合并单元格
		if(toRowNum > fromRowNum || toColNum > fromColNum)
			sheet.addMergedRegion(new CellRangeAddress(fromRowNum, toRowNum, (short) fromColNum, (short)toColNum));
	}
	
	/**
	 * @Title: addMergedCell 
	 * @Description: TODO(合并单元格) 
	 * @param mergedCellList 要合并单元格的数据集合
	 * @param wb excel
	 * @param sheet  excel表格
	 * @param headSize excel表头列的个数
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-11 下午05:37:35
	 */
	private void addMergedCell(ArrayList<LazyDynaBean> mergedCellList,int headSize) throws GeneralException{
		try {
			LazyDynaBean cellBean = null;
			int fromRowNum = 0;//合并单元格从第几行开始
			int toRowNum = 0;//合并单元格到地几行结束
			int fromColNum = 0;//合并单元格从第几列开始
			int toColNum = 0;//合并单元格到第几列结束
			//生成title的样式
			HSSFCellStyle defaultStyle = this.getStyle(null,"mergedCell");//不锁列默认样式
			HSSFCellStyle defaultStyleLock = this.getStyle(null,"mergedCell");//加上锁列的默认样式
			defaultStyleLock.setLocked(true);
			for(int i=0;i < mergedCellList.size();i++){
					cellBean = mergedCellList.get(i);
                    String itemid = (String) cellBean.get("itemid");// 代码类id
                    String content = cellBean.get("content")==null?"":(String) cellBean.get("content");
					if (cellBean != null) {
						boolean columnLocked = cellBean.get("columnLocked") == null ? false : (Boolean)cellBean.get("columnLocked");//当前列是否锁定
						HSSFCellStyle titleStyle = null;
						//获取title的样式map
						HashMap titleStyleMap = (HashMap) cellBean.get("mergedCellStyleMap");
						if(titleStyleMap!=null) {
							titleStyle = this.getStyle(titleStyleMap, "mergedCell");
							titleStyle.setLocked(columnLocked);//当前单元格是否只读
						}else {
                            //特殊处理考勤日明细页面导出Excel 周六日列头颜色改为绿色
                            Boolean isZhoumo = cellBean.get("isZhoumo")==null?false:(Boolean) cellBean.get("isZhoumo");// 代码类id
						    if(isZhoumo){
                                String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
                                HSSFFont font = null;
                                if(fontMap.get(fontName+"_10_true_color") == null){
                                    font = fonts(fontName,(short)10,true);
                                    HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                                    palette.setColorAtIndex(HSSFColor.GREEN.index,
                                            (byte) Integer.parseInt("2d", 16),
                                            (byte) Integer.parseInt("c0", 16),
                                            (byte) Integer.parseInt("2d", 16)
                                    );
                                    font.setColor(HSSFColor.GREEN.index);
                                    fontMap.put(fontName+"_10_true_color",font);
                                }else{
                                    font = (HSSFFont) fontMap.get(fontName+"_10_true_color");
                                }
                                titleStyle = wb.createCellStyle();
                                titleStyle.cloneStyleFrom(defaultStyle);
                                titleStyle.setFont(font);
                            }else{
                                //没有自定义样式时使用默认创建的样式，避免重复创建相同的样式
                                if(columnLocked)
                                    titleStyle = defaultStyleLock;
                                else
                                    titleStyle = defaultStyle;
                            }
						}
						fromRowNum =  cellBean.get("fromRowNum")==null?0:(Integer)cellBean.get("fromRowNum");
						fromColNum = cellBean.get("fromColNum")==null?0:(Integer)cellBean.get("fromColNum");
						toRowNum = cellBean.get("toRowNum")==null?0:(Integer)cellBean.get("toRowNum");
						toColNum = cellBean.get("toColNum")==null?headSize-1:(Integer)cellBean.get("toColNum");
						// 校验是否是图片（考勤导出明细汇总签章图片用到）
						if(cellBean.get("isPhoto")!=null && (Boolean)cellBean.get("isPhoto")) {
							byte[] photo_bytes = (byte[])cellBean.get("photo_bytes");
							if(photo_bytes!=null && photo_bytes.length>0) {
								/**
								 * 处理图片大小
								 * 
								File file = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"_photo"+".jpg");
						        if (!file.exists()) {  
							    	// 生成jpeg图片
							        FileOutputStream out = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"_photo"+".jpg");
							        out.write(photo_bytes);
							        out.flush();
							        out.close();
							    }
							    // 原图大小
						        double imgwidth = 0;
						        double imgheight = 0; 
						        File tempFile = new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+"_photo"+".jpg");
								if(tempFile.exists()) {
								    BufferedImage sourceImg =ImageIO.read(new FileInputStream(tempFile));
								    imgwidth = sourceImg.getWidth();
								    imgheight = sourceImg.getHeight();
								}
								// 生成图片
								int pictureIdx = wb.addPicture(photo_bytes, Workbook.PICTURE_TYPE_JPEG);
								CreationHelper helper = wb.getCreationHelper();
								// Create the drawing patriarch.  This is the top level container for all shapes. 
							    Drawing drawing = sheet.createDrawingPatriarch();
							    //add a picture shape
							    ClientAnchor anchor = helper.createClientAnchor();
							    //set top-left corner of the picture,
							    //subsequent call of Picture#resize() will operate relative to it
							    anchor.setCol1((short)fromColNum);anchor.setRow1(fromRowNum);
//							    anchor.setCol2((short)toColNum);anchor.setRow2(toRowNum);
//							    anchor.setDx1(0);
//							    anchor.setDx2(0);
							    anchor.setDx1(220);
							    anchor.setDx2(220);
							    anchor.setDy1(-350);
							    anchor.setDy2(-350);
							    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
							    Picture pict = drawing.createPicture(anchor, pictureIdx);
							    //auto-size picture relative to its top-left corner
							    // 计算单元格的长宽
							    //sheet.getRow(0).getColumnIndex(); cell = row.getCell(fromColNum);
							    // 400+237	sheet.getRow(0).getHeight() + sheet.getRow(1).getHeight() 
							    //  excel单元格高度是以点单位，1点=2像素; POI中Height的单位是1/20个点，故设置单元的等比例高度如下
							    double cellHeight = 750/20 * 2;
//							    double cellWidth = sheet.getColumnWidthInPixels(sheet.getRow(0).getColumnIndex());
//							    double cellHeight = cell.getRow().getHeightInPoints()/72*96;
							    //imgwidth/220 , imgheight/80//imgwidth , imgheight
							    //根据合并的列数来计算比例数  5个单元格80
							    int multiple = 100;
							    int diffNum = toColNum - fromColNum;
							    if(diffNum > 5) {
							    	multiple = 70;
							    }else if(diffNum == 5) {
							    	multiple = 80;
							    }else if(diffNum == 4) {
							    	multiple = 95;
							    }else if(diffNum == 2) {
							    	multiple = 180;
							    }else if(diffNum == 1) {
							    	multiple = 220;
							    }
							    //pict.resize(400/multiple, 2.37);
							    pict.resize(imgwidth/multiple, imgheight/100);
							    **/
								// 暂时 按照铺满单元格处理签章图片
								HSSFPatriarch patriarch = sheet.createDrawingPatriarch();//1023, 255,
								HSSFClientAnchor anchor = new HSSFClientAnchor( 0, 0, 255, 255, (short)fromColNum, fromRowNum, (short)toColNum, toRowNum);
					     	    anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
								patriarch.createPicture(anchor,this.wb.addPicture(photo_bytes, HSSFWorkbook.PICTURE_TYPE_JPEG));	
							}else {
								this.executeCell(fromRowNum, fromColNum, toRowNum, toColNum, content,titleStyle,null,null);
							}
						}else {
							this.executeCell(fromRowNum, fromColNum, toRowNum, toColNum, content,titleStyle,null,null);
						}
					}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * @Title: addMergedCell 
	 * @Description: TODO(合并单元格) 
	 * @param mergedCellList 要合并单元格的数据集合
	 * @param wb excel
	 * @param sheet  excel表格
	 * @param headSize excel表头列的个数
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-11 下午05:37:35
	 */
	private void addXSSFMergedCell(SXSSFWorkbook wb,SXSSFSheet sheet, ArrayList<LazyDynaBean> mergedCellList,int headSize) throws GeneralException{
		try {
			LazyDynaBean cellBean = null;
			int fromRowNum = 0;//合并单元格从第几行开始
			int toRowNum = 0;//合并单元格到地几行结束
			int fromColNum = 0;//合并单元格从第几列开始
			int toColNum = 0;//合并单元格到第几列结束
			//生成title的样式
			CellStyle defaultStyle = this.getXSSFStyle(wb, null,"mergedCell");//不锁列默认样式
			CellStyle defaultStyleLock = this.getXSSFStyle(wb, null,"mergedCell");//加上锁列的默认样式
			defaultStyleLock.setLocked(true);
			for(int i=0;i < mergedCellList.size();i++){
				cellBean = mergedCellList.get(i);
				String itemid = (String) cellBean.get("itemid");// 代码类id
				String content = cellBean.get("content")==null?"":(String) cellBean.get("content");
				if (cellBean != null) {
					boolean columnLocked = cellBean.get("columnLocked") == null ? false : (Boolean)cellBean.get("columnLocked");//当前列是否锁定
					CellStyle titleStyle = null;
					//获取title的样式map
					HashMap titleStyleMap = (HashMap) cellBean.get("mergedCellStyleMap");
					if(titleStyleMap!=null) {
						titleStyle = this.getStyle(titleStyleMap, "mergedCell");
						titleStyle.setLocked(columnLocked);//当前单元格是否只读
					}else {
						//特殊处理考勤日明细页面导出Excel 周六日列头颜色改为绿色
						Boolean isZhoumo = cellBean.get("isZhoumo")==null?false:(Boolean) cellBean.get("isZhoumo");// 代码类id
						if(isZhoumo){
							String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
							Font font = null;
							if(fontMap.get(fontName+"_10_true_color") == null){
								font = fonts(fontName,(short)10,true);
								font.setColor(HSSFColor.GREEN.index);
								fontMap.put(fontName+"_10_true_color",font);
							}else{
								font = (Font) fontMap.get(fontName+"_10_true_color");
							}
							titleStyle = wb.createCellStyle();
							titleStyle.cloneStyleFrom(defaultStyle);
							titleStyle.setFont(font);
						}else{
							//没有自定义样式时使用默认创建的样式，避免重复创建相同的样式
							if(columnLocked)
								titleStyle = defaultStyleLock;
							else
								titleStyle = defaultStyle;
						}
					}
					fromRowNum =  cellBean.get("fromRowNum")==null?0:(Integer)cellBean.get("fromRowNum");
					fromColNum = cellBean.get("fromColNum")==null?0:(Integer)cellBean.get("fromColNum");
					toRowNum = cellBean.get("toRowNum")==null?0:(Integer)cellBean.get("toRowNum");
					toColNum = cellBean.get("toColNum")==null?headSize-1:(Integer)cellBean.get("toColNum");
					// 校验是否是图片（考勤导出明细汇总签章图片用到）
					if(cellBean.get("isPhoto")!=null && (Boolean)cellBean.get("isPhoto")) {
						byte[] photo_bytes = (byte[])cellBean.get("photo_bytes");
						if(photo_bytes!=null && photo_bytes.length>0) {
							// 暂时 按照铺满单元格处理签章图片
//							SXSSFDrawing patriarch = sheet.createDrawingPatriarch();//1023, 255,
//							HSSFClientAnchor anchor = new ClientAnchor( 0, 0, 255, 255, (short)fromColNum, fromRowNum, (short)toColNum, toRowNum);
//							anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);
//							patriarch.createPicture(anchor,this.wb.addPicture(photo_bytes, SXSSFWorkbook.PICTURE_TYPE_JPEG));	
						}else {
							this.executeXSSFCell(sheet, fromRowNum, fromColNum, toRowNum, toColNum, content,titleStyle,null,null);
						}
					}else {
						this.executeXSSFCell(sheet, fromRowNum, fromColNum, toRowNum, toColNum, content,titleStyle,null,null);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * @deprecated:设置表格列标题
	 * @author lis
	 * @date 2015-6-27
	 * @param wb
	 * @param sheet
	 * @param headSize
	 * @param rowNum
	 * @param dropDownMap
	 * @param dropDownDataTemp
	 * @param headList
	 * @throws GeneralException 
	 */
	private int setHead(HashMap dropDownMap,HashMap dropDownDataTemp,ArrayList<LazyDynaBean> headList,int headStartRowNum) throws GeneralException{
		int rowNum = headStartRowNum;
		try {
			int headSize = headList.size();
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			int fromRowNum = 0;
			int toRowNum = 0;
			int fromColNum = 0;
			int toColNum = 0;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isBlank(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";
			//获得该单元格默认样式，没有自定义样式时无需重复创建
			HSSFCellStyle defaultStyle = this.getStyle(null,"head");//不锁列的默认样式
			HSSFCellStyle defaultStyleLock = this.getStyle(null,"head");//锁列的默认样式
			defaultStyleLock.setLocked(true);
			HashMap<String,HSSFCellStyle> titleStyleMap=new HashMap<String,HSSFCellStyle>();
			for (int columnIndex = 0; columnIndex < headSize; columnIndex++) {
				LazyDynaBean headBean = (LazyDynaBean) headList.get(columnIndex);
				String type=(String)headBean.get("colType");//该列的样式
				String itemid = (String) headBean.get("itemid");// 代码类id
				String codesetid = (String) headBean.get("codesetid");// 代码类id
				String content = (String) headBean.get("content");//当前列名标题
				String comment = (String) headBean.get("comment");//当前列名标题的注释
				boolean columnLocked = headBean.get("columnLocked") == null ? false : (Boolean)headBean.get("columnLocked");//当前列是否锁定
				boolean columnHidden = headBean.get("columnHidden") == null ? false : (Boolean)headBean.get("columnHidden");//当前列是否隐藏
				sheet.setColumnHidden(columnIndex, columnHidden);
				
				if(headBean.get("fromRowNum")!=null && headBean.get("toRowNum")!=null && headBean.get("fromColNum")!=null && headBean.get("toColNum")!=null){
					fromRowNum = (Integer)headBean.get("fromRowNum");
					toRowNum = (Integer)headBean.get("toRowNum");
					fromColNum = (Integer)headBean.get("fromColNum");
					toColNum = (Integer)headBean.get("toColNum");
				}else{
					fromRowNum = headStartRowNum;
					toRowNum = headStartRowNum;
					fromColNum = toColNum;
				}
				
				if(dropDownMap != null && dropDownMap.containsKey(itemid))
					dropDownDataTemp.put(columnIndex, dropDownMap.get(itemid));
				HashMap headStyleMap = (HashMap) headBean.get("headStyleMap");//当前列名标题样式
				short columnWidth = 0;
				HSSFCellStyle headStyle = null;
				if(headStyleMap != null){
					Integer columnWidthTemp = (Integer)headStyleMap.get("columnWidth");
					columnWidth = Short.valueOf(columnWidthTemp.toString());
					//获得该单元格自定义样式
					headStyle = this.getStyle(headStyleMap, "head");
					headStyle.setLocked(columnLocked);
				}else {
				    if(headBean.get("columnWidth")!=null) {
                        Integer columnWidthTemp = (Integer)headBean.get("columnWidth");
                        columnWidth = Short.valueOf(columnWidthTemp.toString());
                    }
					//特殊处理考勤日明细页面导出Excel 周六日列头颜色改为绿色
                    if(itemid != null && itemid.toUpperCase().startsWith("Q35")
                            && StringUtils.isNumericSpace(itemid.substring(3))
                            && Integer.parseInt(itemid.substring(3))>=1
                            && Integer.parseInt(itemid.substring(3))<=31
                            && (content.equalsIgnoreCase(ResourceFactory.getProperty("kq.date.column.zliu"))
                            || content.equalsIgnoreCase(ResourceFactory.getProperty("kq.date.column.zri")))){
                        String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
                        HSSFFont font = null;
                        if(fontMap.get(fontName+"_10_true_color") == null){
                            font = fonts(fontName,(short)10,true);
                            HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                            palette.setColorAtIndex(HSSFColor.GREEN.index,
                                    (byte) Integer.parseInt("2d", 16),
                                    (byte) Integer.parseInt("c0", 16),
                                    (byte) Integer.parseInt("2d", 16)
                            );
                            font.setColor(HSSFColor.GREEN.index);
                            fontMap.put(fontName+"_10_true_color",font);
                        }else{
                            font = (HSSFFont) fontMap.get(fontName+"_10_true_color");
                        }
                        headStyle = wb.createCellStyle();
                        headStyle.cloneStyleFrom(defaultStyle);
                        headStyle.setFont(font);
                    }else{
                        //没有自定义样式时使用默认创建的样式，避免重复创建相同的样式
                        if(columnLocked)
                            headStyle = defaultStyleLock;
                        else
                            headStyle = defaultStyle;
                    }
				}
				if(columnWidth == 0){
					if ("D".equals(type)) {
						columnWidth = 4000;
					}else if("N".equals(type)){
						columnWidth = 3000;
					}else if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid)|| "@k".equalsIgnoreCase(codesetid))
					{
						if(!"0".equalsIgnoreCase(display_e0122) && !"@k".equalsIgnoreCase(codesetid))
							columnWidth = 7000;
						else
							columnWidth = 5000;
					}else{
						columnWidth = 3500;
					}
					//对于显示多层级的部门单位，加大宽度
				}
				//设置单元格长度
				sheet.setColumnWidth((short) (fromColNum), columnWidth); 
				//获得该单元格样式
				if(titleStyleMap.get(type)==null) {
					headStyle = this.getStyle(headStyleMap,"head");
					titleStyleMap.put(type, headStyle);
				}else {
					headStyle=titleStyleMap.get(type);
				}
				headStyle.setLocked(columnLocked);
				this.executeCell(fromRowNum, fromColNum, toRowNum, toColNum, content, headStyle ,comment,patr);
				toColNum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return rowNum;
	}
	/**
	 * @deprecated:设置表格列标题
	 * @param sheet
	 * @param headSize
	 * @param rowNum
	 * @param dropDownMap
	 * @param dropDownDataTemp
	 * @param headList
	 * @throws GeneralException 
	 */
	private int setXSSFHead(SXSSFWorkbook wb, SXSSFSheet sheet,HashMap dropDownMap,HashMap dropDownDataTemp,ArrayList<LazyDynaBean> headList,
			int headStartRowNum) throws GeneralException{
		int rowNum = headStartRowNum;
		try {
			int headSize = headList.size();
			SXSSFDrawing patr = sheet.createDrawingPatriarch();
			int fromRowNum = 0;
			int toRowNum = 0;
			int fromColNum = 0;
			int toColNum = 0;
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(StringUtils.isEmpty(display_e0122)|| "00".equals(display_e0122))
				display_e0122="0";	
			
			for (int columnIndex = 0; columnIndex < headSize; columnIndex++) {
				LazyDynaBean headBean = (LazyDynaBean) headList.get(columnIndex);
				String type=(String)headBean.get("colType");//该列的样式
				String itemid = (String) headBean.get("itemid");// 代码类id
				String codesetid = (String) headBean.get("codesetid");// 代码类id
				String content = (String) headBean.get("content");//当前列名标题
				String comment = (String) headBean.get("comment");//当前列名标题的注释
				
				boolean columnLocked = headBean.get("columnLocked") == null ? false : (Boolean)headBean.get("columnLocked");//当前列是否锁定
				boolean columnHidden = headBean.get("columnHidden") == null ? false : (Boolean)headBean.get("columnHidden");//当前列是否隐藏
				sheet.setColumnHidden(columnIndex, columnHidden);
				
				if(headBean.get("fromRowNum")!=null && headBean.get("toRowNum")!=null && headBean.get("fromColNum")!=null && headBean.get("toColNum")!=null){
					fromRowNum = (Integer)headBean.get("fromRowNum");
					toRowNum = (Integer)headBean.get("toRowNum");
					fromColNum = (Integer)headBean.get("fromColNum");
					toColNum = (Integer)headBean.get("toColNum");
				}else{
					fromRowNum = headStartRowNum;
					toRowNum = headStartRowNum;
					fromColNum = toColNum;
				}
				
				if(dropDownMap != null && dropDownMap.containsKey(itemid))
					dropDownDataTemp.put(columnIndex, dropDownMap.get(itemid));
				CellStyle headStyle = null;
				HashMap headStyleMap = (HashMap) headBean.get("headStyleMap");//当前列名标题样式
				short columnWidth = 0;
				if(headStyleMap != null){
					Integer columnWidthTemp = (Integer)headStyleMap.get("columnWidth");
					columnWidth = Short.valueOf(columnWidthTemp.toString());
				}
				if(columnWidth == 0){
					if ("D".equals(type)) {
						columnWidth = 4000;
					}else if("N".equals(type)){
						columnWidth = 3000;
					}else if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid)|| "@k".equalsIgnoreCase(codesetid))
					{
						if(!"0".equalsIgnoreCase(display_e0122) && !"@k".equalsIgnoreCase(codesetid))
							columnWidth = 7000;
						else
							columnWidth = 5000;
					}else{
						columnWidth = 3500;
					}
					//对于显示多层级的部门单位，加大宽度
				}
				//设置单元格长度
				sheet.setColumnWidth((short) (fromColNum), columnWidth); 
				//获得该单元格样式
				headStyle = this.getXSSFStyle(wb,headStyleMap,"head");
				headStyle.setLocked(columnLocked);
				this.executeXSSFCell(sheet, fromRowNum, fromColNum, toRowNum, toColNum, content, headStyle ,comment,patr);
				toColNum++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return rowNum;
	}
	/**
	 * @deprecated 填写下拉框数据
	 * @author lis
	 * @date 2015-6-27
	 * @param dropDownDataTemp
	 * @param sheet
	 * @param titleRowNum
	 * @param rowNum
	 */
	private void setDropDownData(HashMap dropDownDataTemp,int titleRowNum,int rowNum){
		 int index = 0;
		 HSSFCell cell = null;
		 String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			
		 int div = 0;
		 int mod = 0;	
		 //为指定的列添加下拉框
		 for(Iterator it = dropDownDataTemp.keySet().iterator();it.hasNext();) {
			 int key = (Integer)it.next();//第key列 
			 ArrayList arrayList = (ArrayList) dropDownDataTemp.get(key);
			 int m = 0;
			 for(int i=0;i<arrayList.size();i++){
				 HSSFRow row = sheet.getRow(m + 0);
					if (row == null)
						row = sheet.createRow(m + 0);
					cell = row.createCell((short) (182 + index));
						cell.setCellValue(new HSSFRichTextString((String)arrayList.get(i)));
					m++;
			 }
			 if(m==0)
					m=2; 
				sheet.setColumnWidth((short) (182 + index), (short) 0);
				div = index/26;
				mod = index%26;
				String strFormula = "$" +lettersUpper[6+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[6+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
				//导出excel文件时，在单元格中设置下拉菜单，当rowNum<1时，默认把下拉菜单放到1001行以后。
				if(rowNum < 1)
				    rowNum = 1000;
				
				CellRangeAddressList addressList = new CellRangeAddressList(titleRowNum, rowNum, key, key);
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);

				index++;
		 }
	}
	
	/**
	 * 填写下拉框数据到对应的位置
	 * @author sunj
	 * @date 2019-4-24
	 * @param dropDownDataTemp
	 * @param sheet
	 * @param row_num_new
	 * @param row_num_old
	 * @param type 0:每100行插入，1：最后一次插入
	 */
	private void setDropDownData_xlxs(HashMap dropDownDataTemp, SXSSFSheet sheet,int row_num_new, int row_num_old, String type){
		try {
			int index = 0;
			SXSSFCell cell = null;

			// 为指定的列添加下拉框
			for (Iterator it = dropDownDataTemp.keySet().iterator(); it.hasNext();) {
				int key = (Integer) it.next();// 第key列
				ArrayList arrayList = (ArrayList) dropDownDataTemp.get(key);
				int size = 0;
				///因为每100行，就给存到硬盘中，无法读取，这样只能在这100行存入之前，先getRow,创建下拉框所需要的内容到相应的位置，最后创建下拉框的引用，对应关系
				//如果在最后一次，下列表的长度超出了数据的长度，这样，等于总的长度就行
				if("0".equals(type)) {
					size = row_num_new < arrayList.size()?row_num_new:arrayList.size();
				}else {
					size = arrayList.size();
				}
				int i;
				//如果该下拉框没有相应的长度了，不需要再写内容进去
				if(row_num_old >= arrayList.size()) {
					index++;
					continue;
				}
				//写内容到cell里面
				for (i = row_num_old; i < size; i++) {
					SXSSFRow row = sheet.getRow(i + 0);
					if (row == null)
						row = sheet.createRow(i + 0);
					cell = row.createCell((short) (182 + index));
					cell.setCellValue((String) arrayList.get(i));
				}
				
				sheet.setColumnWidth((short) (182 + index), (short) 0);
				index++;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 最后写创建对应关系
	 * @author sunj
	 * @date 2019-4-24
	 * @param dropDownDataTemp
	 * @param titleRowNum 写入的开始行
	 * @param rowNum
	 * @param sheet
	 */
	private void setDropDownData_xlxs_create(HashMap dropDownDataTemp,int titleRowNum,int rowNum,SXSSFSheet sheet){
		try {
			int index = 0;
			String[] lettersUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
					"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

			int div = 0;
			int mod = 0;
			// 为指定的列添加下拉框
			for (Iterator it = dropDownDataTemp.keySet().iterator(); it.hasNext();) {
				int key = (Integer) it.next();// 第key列
				ArrayList arrayList = (ArrayList) dropDownDataTemp.get(key);
				int m = 0;
				for (int i = 0; i < arrayList.size(); i++) {
					m++;
				}
				if (m == 0)
					m = 2;
				div = index / 26;
				mod = index % 26;
				String strFormula = "$" + lettersUpper[6 + div] + lettersUpper[mod] + "$1:$" + lettersUpper[6 + div] + lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
				// 导出excel文件时，在单元格中设置下拉菜单，当rowNum<1时，默认把下拉菜单放到1001行以后。
				if (rowNum < 1)
					rowNum = 1000;
				//需要创建下拉框指定区域第几列
				CellRangeAddressList addressList = new CellRangeAddressList(titleRowNum, rowNum, key, key);
				//将数据来源即下拉框的内容区域strFormula（$GA$1:$GA$5），对应到相应的需要创建该下拉框指定区域第几列addressList
				DataValidationHelper dvHelper = sheet.getDataValidationHelper();
				XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createFormulaListConstraint(strFormula);
				XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint,addressList);
				sheet.addValidationData(validation);
				index++;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置excel的字体
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	private HSSFFont fonts(String fonts, int size, boolean bolderWeight) {
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		font.setBold(bolderWeight);// 加粗
		return font;
	}
	
	/**
	 * 设置excel的字体
	 * @param workbook
	 * @param fonts
	 * @param size
	 * @return
	 */
	private Font XSSFFonts(SXSSFWorkbook wb, String fonts, int size, boolean bolderWeight) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) size);
		font.setFontName(fonts);
		font.setBold(bolderWeight);// 加粗
		return font;
	}

    /**
     * 获得列颜色
     *
     * @param styleMap
     * @return
     */
    private HashMap cellcolorMap = new HashMap();

    private HSSFCellStyle getColorStyle(HashMap styleMap) {

        String color = "";
        String type = "font";
        if (styleMap.containsKey("fillForegroundColor")) {
            type = "fillForeground";
            //16进制颜色编码
            String fillForegroundColor = String.valueOf(styleMap.get("fillForegroundColor"));
            color = fillForegroundColor;
        } else if (styleMap.containsKey("fontColor")) {
            //16进制颜色编码
            String fontColor = String.valueOf(styleMap.get("fontColor"));
            color = fontColor;
        }
        if (cellcolorMap.containsKey(type + "_" + color)) {
            return (HSSFCellStyle) cellcolorMap.get(type + "_" + color);
        } else {
            HSSFCellStyle cellStyle = wb.createCellStyle();
            if (!StringUtils.isEmpty(color)) {
                HSSFPalette palette = ((HSSFWorkbook) wb).getCustomPalette();
                HSSFColor hssfColor = palette.findSimilarColor(
                        (byte) Integer.parseInt(color.substring(0, 2), 16),
                        (byte) Integer.parseInt(color.substring(2, 4), 16),
                        (byte) Integer.parseInt(color.substring(4, 6), 16)
                );
                if (hssfColor != null) {
                    if ("font".equals(type)) {
                        HSSFFont font = wb.createFont();
                        font.setColor(hssfColor.getIndex());
                        cellStyle.setFont(font);
                    } else {
                        HSSFFont font = wb.createFont();
                        font.setColor(IndexedColors.WHITE.getIndex());
                        cellStyle.setFont(font);
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        cellStyle.setFillForegroundColor(hssfColor.getIndex());
                    }
                }
            }
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderTop(BorderStyle.THIN);
            if (styleMap.containsKey("align")) {
                String align = String.valueOf(styleMap.get("align"));
                if ("left".equals(align)) {
                    cellStyle.setAlignment(HorizontalAlignment.LEFT);
                } else if ("right".equals(align)) {
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                } else {
                    cellStyle.setAlignment(HorizontalAlignment.CENTER);
                }
            }
            cellcolorMap.put(type + "_" + color, cellStyle);
            return cellStyle;
        }
    }

    /**
	 * 
	 * @author lis
	 * @date 2015-6-25
	 * @param wb
	 * @param styleMap
	 * @param type
	 * @return
	 */
	private HSSFCellStyle getStyle(HashMap styleMap, String type) {
		
		HSSFCellStyle a_style= wb.createCellStyle();
		a_style.setWrapText(true);// 自动换行
		short border = (short) 1;
		short borderColor = IndexedColors.BLACK.index;
		HorizontalAlignment align = HorizontalAlignment.CENTER;
		FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
		short fillForegroundColor = IndexedColors.WHITE.index;
		String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
		int fontSize = 0;// 字体大小
		boolean fontBoldWeight = false;
		boolean isFontBold = false;// 是否加粗

		if (styleMap != null) {// 设置了单元格样式
			if (styleMap.get("border") != null)
				border = (Short) styleMap.get("border");// 值为-1时则改样式不设置
			if (styleMap.get("borderColor") != null)
				borderColor = (Short) styleMap.get("borderColor");
			if (styleMap.get("align") != null) {
				Object horizontAlign = styleMap.get("align");
				if (horizontAlign instanceof Short) {
					short alignMent = (Short) horizontAlign;
					if (alignMent == 1)
						align = HorizontalAlignment.LEFT;
					else if (alignMent == 3)
						align = HorizontalAlignment.RIGHT;
				} else
					align = (HorizontalAlignment) styleMap.get("align");

			}
			if (styleMap.get("fillForegroundColor") != null)
				fillForegroundColor = (Short) styleMap.get("fillForegroundColor");
			
			if (styleMap.get("fillPattern") != null)
				fillPattern = (FillPatternType) styleMap.get("fillPattern");
			
			if (styleMap.get("fontName") != null)
				fontName = (String) styleMap.get("fontName");
			
			if (styleMap.get("fontSize") != null)
				fontSize = (Integer) styleMap.get("fontSize");
			
			if (styleMap.get("isFontBold") != null)
				isFontBold = (Boolean) styleMap.get("isFontBold");
			// 设置数据自定义格式
			if (styleMap.get("dataFormat") != null && StringUtils.isNotBlank((String) styleMap.get("dataFormat"))) {
				HSSFDataFormat format = wb.createDataFormat();
				a_style.setDataFormat(format.getFormat((String) styleMap.get("dataFormat")));
			}
		} else {
			// 没有设置单元格样式  默认头部字体是加粗
			if ("head".equals(type)
					|| "mergedCell".equals(type)) {
				isFontBold = true;
			}
		}

		HSSFFont fonttitle = null;
		if ("head".equals(type)) {
			a_style.setFillPattern(fillPattern);
			a_style.setFillForegroundColor(fillForegroundColor);
			if (fontSize == 0)
				fontSize = 10;
			if (isFontBold)
				fontBoldWeight = true;
			// 设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(fontBoldWeight);

			if (fontMap.get(fontKey.toString()) == null) {
				fonttitle = fonts(fontName, fontSize, fontBoldWeight);
				fontMap.put(fontKey.toString(), fonttitle);
			} else
				fonttitle = (HSSFFont) fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		} else if ("mergedCell".equals(type)) {
			if (border != -1) {
				a_style.setFillPattern(fillPattern);
				a_style.setFillForegroundColor(fillForegroundColor);
			}
			if (fontSize == 0)
				fontSize = 10;
			if (isFontBold)
				fontBoldWeight = true;
			// 设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(isFontBold);
			if (fontMap.get(fontKey.toString()) == null) {
				fonttitle = fonts(fontName, fontSize, fontBoldWeight);
				fontMap.put(fontKey.toString(), fonttitle);
			} else
				fonttitle = (HSSFFont) fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		} else {
			if (align == HorizontalAlignment.CENTER&&(styleMap == null||styleMap.get("align") ==null))
				align = HorizontalAlignment.LEFT;

			if (fontSize == 0)
				fontSize = 10;
			if (isFontBold)
				fontBoldWeight = true;
			// 设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(isFontBold);
			if (fontMap.get(fontKey.toString()) == null) {
				fonttitle = fonts(fontName, fontSize, fontBoldWeight);
				fontMap.put(fontKey.toString(), fonttitle);
			} else
				fonttitle = (HSSFFont) fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		}

		if (border != -1) {
			a_style.setBorderBottom(BorderStyle.valueOf(border));
			a_style.setBottomBorderColor(borderColor);
			a_style.setBorderLeft(BorderStyle.valueOf(border));
			a_style.setLeftBorderColor(borderColor);
			a_style.setBorderRight(BorderStyle.valueOf(border));
			a_style.setRightBorderColor(borderColor);
			a_style.setBorderTop(BorderStyle.valueOf(border));
			a_style.setTopBorderColor(borderColor);
		}
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		a_style.setAlignment(align);
		a_style.setLocked(false);
		return a_style;
	}
	/**
	 * 
	 * @author lis
	 * @date 2015-6-25
	 * @param wb
	 * @param styleMap
	 * @param type
	 * @return
	 */
	private CellStyle getXSSFStyle(SXSSFWorkbook wb, HashMap styleMap, String type) {
		CellStyle a_style = wb.createCellStyle();
		a_style.setWrapText(true);// 自动换行
		short border = (short) 1;
		short borderColor = IndexedColors.BLACK.index;
		HorizontalAlignment align = HorizontalAlignment.CENTER;
		FillPatternType fillPattern = FillPatternType.SOLID_FOREGROUND;
		short fillForegroundColor = IndexedColors.WHITE.index;
		String fontName = ResourceFactory.getProperty("gz.gz_acounting.m.font");
		int fontSize = 0;// 字体大小
		boolean fontBoldWeight = false;
		boolean isFontBold = false;// 是否加粗
		if (styleMap != null) {// 设置了单元格样式
			if (styleMap.get("border") != null)
				border = (Short) styleMap.get("border");// 值为-1时则改样式不设置
			if (styleMap.get("borderColor") != null)
				borderColor = (Short) styleMap.get("borderColor");
			if (styleMap.get("align") != null) {
				Object horizontAlign = styleMap.get("align");
				if (horizontAlign instanceof Short) {
					short alignMent = (Short) horizontAlign;
					if (alignMent == 1)
						align = HorizontalAlignment.LEFT;
					else if (alignMent == 3)
						align = HorizontalAlignment.RIGHT;
				} else
					align = (HorizontalAlignment) styleMap.get("align");
				
			}
			if (styleMap.get("fillForegroundColor") != null)
				fillForegroundColor = (Short) styleMap.get("fillForegroundColor");
			
			if (styleMap.get("fillPattern") != null)
				fillPattern = (FillPatternType) styleMap.get("fillPattern");
			
			if (styleMap.get("fontName") != null)
				fontName = (String) styleMap.get("fontName");
			
			if (styleMap.get("fontSize") != null)
				fontSize = (Integer) styleMap.get("fontSize");
			
			if (styleMap.get("isFontBold") != null)
				isFontBold = (Boolean) styleMap.get("isFontBold");
			// 设置数据自定义格式
			if (styleMap.get("dataFormat") != null && StringUtils.isNotBlank((String) styleMap.get("dataFormat"))) {
				DataFormat format = wb.createDataFormat();
				a_style.setDataFormat(format.getFormat((String) styleMap.get("dataFormat")));
			}
		} else {
			// 没有设置单元格样式  默认头部字体是加粗
			if ("head".equals(type)) {
				isFontBold = true;
			}
		}
		
		Font fonttitle = null;
		if ("head".equals(type)) {
			a_style.setFillPattern(fillPattern);
			a_style.setFillForegroundColor(fillForegroundColor);
			if (fontSize == 0)
				fontSize = 10;
			
			// 设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(true);
			
			if (XSSFFontMap.get(fontKey.toString()) == null) {
				fonttitle = XSSFFonts(wb, fontName, fontSize, true);
				XSSFFontMap.put(fontKey.toString(), fonttitle);
			} else
				fonttitle = XSSFFontMap.get(fontKey.toString());
			
			a_style.setFont(fonttitle);
		} else if ("mergedCell".equals(type)) {
			if (border != -1) {
				a_style.setFillPattern(fillPattern);
				a_style.setFillForegroundColor(fillForegroundColor);
			}
			if (fontSize == 0)
				fontSize = 15;
			if (isFontBold)
				fontBoldWeight = true;
			// 设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(isFontBold);
			if (XSSFFontMap.get(fontKey.toString()) == null) {
				fonttitle = XSSFFonts(wb, fontName, fontSize, fontBoldWeight);
				XSSFFontMap.put(fontKey.toString(), fonttitle);
			} else
				fonttitle = (Font) fontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		} else {
			if (align == HorizontalAlignment.CENTER)
				align = HorizontalAlignment.LEFT;
			
			if (fontSize == 0)
				fontSize = 10;
			if (isFontBold)
				fontBoldWeight = true;
			// 设置字体
			StringBuffer fontKey = new StringBuffer(fontName);
			fontKey.append("_");
			fontKey.append(fontSize);
			fontKey.append("_");
			fontKey.append(isFontBold);
			if (XSSFFontMap.get(fontKey.toString()) == null) {
				fonttitle = XSSFFonts(wb, fontName, fontSize, fontBoldWeight);
				XSSFFontMap.put(fontKey.toString(), fonttitle);
			} else
				fonttitle = XSSFFontMap.get(fontKey.toString());
			a_style.setFont(fonttitle);
		}
		
		if (border != -1) {
			a_style.setBorderBottom(BorderStyle.valueOf(border));
			a_style.setBottomBorderColor(borderColor);
			a_style.setBorderLeft(BorderStyle.valueOf(border));
			a_style.setLeftBorderColor(borderColor);
			a_style.setBorderRight(BorderStyle.valueOf(border));
			a_style.setRightBorderColor(borderColor);
			a_style.setBorderTop(BorderStyle.valueOf(border));
			a_style.setTopBorderColor(borderColor);
		}
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		a_style.setAlignment(align);
		a_style.setLocked(false);
		return a_style;
	}
	
	/**
	 * @author lis
	 * @Description: 根据ColumnsInfo导出excel
	 * @date 2015-11-3
	 * @param fileName
	 * @param sheetName
	 * @param mergedCellList
	 * @param columns
	 * @param sql
	 * @param dropDownMap
	 * @param headStartRowNum
	 * @throws SQLException
	 * @throws IOException
	 * @throws GeneralException
	 */
	public void exportExcelByColum(String fileName,String sheetName,ArrayList<LazyDynaBean> mergedCellList,
			ArrayList<ColumnsInfo> columns,String sql,HashMap dropDownMap,int headStartRowNum) throws SQLException, IOException, GeneralException {
		RowSet rs = null;
		try {
			ArrayList<LazyDynaBean> headList = this.getHeadListByColum(columns);
			int count = 0;
			if(StringUtils.isNotEmpty(sql)) {
			    ContentDAO dao = new ContentDAO(this.conn);
			    String countSql = "";
			    if(sql.indexOf("order by") > -1)
			        countSql = "select count(1) as cn from (" + sql.substring(0, sql.indexOf("order by")) + ") temp";
			    else
			        countSql = "select count(1) as cn from (" + sql + ") temp";
			    
			    rs = dao.search(countSql);
			    if(rs.next()) {
			        count = rs.getInt("cn");
			        if(this.userView != null) {
			            this.userView.getHm().put("totalRows", count);   
			            this.userView.getHm().put("exportRows", 0);     
			        }
			    }
			    
			    if((count * headList.size()) > 70000000) {
			        if(this.userView != null)
			            this.userView.getHm().put("msg", "导出数据过大，请分批导出！");
			        else
			            throw new GeneralException("", "导出数据过大，请分批导出！", "", "");
			        
			    } else if(this.userView != null)
			        this.userView.getHm().put("msg", "ok");
			    
			}
			//超过10000000个单元格或列数超过255列采用xlsx格式导出
			this.fileName = fileName;
			if((count * headList.size()) > 10000000 || count > 120000|| headList.size() > 255) {
			    if(!this.fileName.toLowerCase().endsWith("xlsx"))
			        this.fileName += "x";
			    
				this.createXSSFExcelSheet(fileName, sheetName, headList, sql, mergedCellList, dropDownMap, headStartRowNum);
			} else {
				ArrayList dataList = this.getExportData(headList, sql);
				this.exportExcel(fileName, sheetName, mergedCellList, headList, dataList, dropDownMap, headStartRowNum);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
	}
	
	/**
	 * @author lis
	 * @Description: 根据ColumnsInfo信息得到导出excel的头部数据列表
	 * @date 2015-11-3
	 * @param columnTmps 表格控件列
	 * @return
	 */
	public ArrayList<LazyDynaBean> getHeadListByColum(ArrayList<ColumnsInfo> columns){
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();// 封装excel表头数据
		try {
			LazyDynaBean columsBean = null;
			
			for(ColumnsInfo columnsInfo:columns){
				columsBean = new LazyDynaBean();
				columsBean.set("content", columnsInfo.getColumnDesc());// 列头名称
				columsBean.set("itemid", columnsInfo.getColumnId());// 列头代码
				columsBean.set("codesetid", columnsInfo.getCodesetId());// 列头代码
				columsBean.set("colType", columnsInfo.getColumnType());// 该列数据类型
				columsBean.set("decwidth", columnsInfo.getDecimalWidth()+"");// 列小数点后面位数
				HashMap headStyleMap = new HashMap();
				headStyleMap.put("columnWidth", columnsInfo.getColumnWidth()*40);
				headStyleMap.put("isFontBold", true);
				columsBean.set("headStyleMap", headStyleMap);
				headList.add(columsBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return headList;
	}
	public boolean compareMap2(HashMap old_data,HashMap new_data){
		boolean flag = true;
		try {
	        if(old_data==null || old_data.size() == 0 || new_data == null || new_data.size() == 0) {
	        	return false;
	        }
	        Iterator iter1 = old_data.entrySet().iterator();
	        while(iter1.hasNext()){
	        	Map.Entry entry = (Map.Entry) iter1.next();
	        	Object key = entry.getKey();
	        	Object val = entry.getValue();
	            String m1value = val == null?"":String.valueOf(val);
	            String m2value =  new_data.get(key)==null?"":String.valueOf(new_data.get(key));
	                  	
	            if (!m1value.equals(m2value)) {//若两个map中相同key对应的value不相等
	            	flag = false;
	            	break;
	            }
	        }
		}catch(Exception e) {
			e.printStackTrace();
		}
		return flag;
    }
	
	/**
	 * 为Excel打上水印工具函数
     * 请自行确保参数值，以保证水印图片之间不会覆盖。
     * 在计算水印的位置的时候，并没有考虑到单元格合并的情况，请注意
	 * @param sheet	需要打水印的Excel
	 * @param waterRemarkPath	水印图片路径 目前只支持png格式的图片
	 * @param startXCol	水印起始列
	 * @param startYRow	水印起始行
	 * @param betweenXCol	水印横向之间间隔多少列
	 * @param betweenYRow	水印纵向之间间隔多少行
	 * @param XCount	横向共有水印多少个
	 * @param YCount	纵向共有水印多少个
	 * @param waterRemarkWidth	水印图片宽度为多少列
	 * @param waterRemarkHeight	水印图片高度为多少行
	 * @throws IOException
	 * @author linbz
	 * @date 2020年3月26日下午1:17:52
	 */
    public void putWaterRemarkToExcel(Sheet sheet, String waterRemarkPath, int startXCol, int startYRow
    		, int betweenXCol, int betweenYRow, int XCount, int YCount
    		, int waterRemarkWidth, int waterRemarkHeight) throws IOException{
        // 校验传入的水印图片格式
        if(!waterRemarkPath.endsWith("png") && !waterRemarkPath.endsWith("PNG")){
            throw new RuntimeException("向Excel上面打印水印，目前支持png格式的图片。");
        }
		ByteArrayOutputStream byteArrayOut = null;
		InputStream imageIn = null;
		try {
			// 加载图片
			byteArrayOut = new ByteArrayOutputStream();
			imageIn = new FileInputStream(waterRemarkPath);
        // 直接读本地路径 classes下
        //InputStream imageIn = Thread.currentThread().getContextClassLoader().getResourceAsStream(waterRemarkPath);
        if(null == imageIn || imageIn.available() < 1){
            throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(1)。");
        }
        BufferedImage bufferImg = ImageIO.read(imageIn);
        if(null == bufferImg) {
            throw new RuntimeException("向Excel上面打印水印，读取水印图片失败(2)。");
        }
        ImageIO.write(bufferImg,"png",byteArrayOut);
        // 获取共有多少行
        int lastRowNum = sheet.getLastRowNum();
        /**
         * startYRow 超过5行的就从第5行，否则，第3行
         */
        if(lastRowNum < 5) {
        	startYRow = 2;
        }
        /**
         * 获取纵向共有水印多少个
         * 如果需要固定行 那么注释掉即可
         */
        try {
            YCount = (lastRowNum-31)/(betweenYRow); 
            if(YCount < 5){
                YCount = 5;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            YCount = 50;
        }
        // 纵向可以多几个 暂时不需要减
        // YCount = YCount-2;
        
        // 开始打水印
        Drawing drawing = sheet.createDrawingPatriarch();
        // 按照共需打印多少行水印进行循环
        for (int yCount = 0; yCount < YCount; yCount++) {
            // 按照每行需要打印多少个水印进行循环
            for (int xCount = 0; xCount < XCount; xCount++) {
                // 创建水印图片位置
                int xIndexInteger = startXCol + (xCount * waterRemarkWidth) + (xCount * betweenXCol);
                int yIndexInteger = startYRow + (yCount * waterRemarkHeight) + (yCount * betweenYRow);
                /*
                 * 参数定义：
                 * 第一个参数是（x轴的开始节点）； 
                 * 第二个参数是（是y轴的开始节点）； 
                 * 第三个参数是（是x轴的结束节点）；
                 * 第四个参数是（是y轴的结束节点）； 
                 * 第五个参数是（是从Excel的第几列开始插入图片，从0开始计数）；
                 * 第六个参数是（是从excel的第几行开始插入图片，从0开始计数）； 
                 * 第七个参数是（图片宽度，共多少列）；
                 * 第八个参数是（图片高度，共多少行）；
                 */
                ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, xIndexInteger,
                        yIndexInteger, xIndexInteger+waterRemarkWidth, yIndexInteger+waterRemarkHeight);
                
                Picture pic = drawing.createPicture(anchor,
                        wb.addPicture(byteArrayOut.toByteArray(), Workbook.PICTURE_TYPE_PNG));
                pic.resize();
            }
        }
		} finally {
			PubFunc.closeResource(byteArrayOut);
			PubFunc.closeResource(imageIn);
		}
	}
    /**
     * 生成水印图片
     * @param waterRemarkContent	水印文字
     * @param waterRemarkPath		水印图片路径 只支持png
     * @throws IOException
     * @author linbz
     * @date 2020年3月26日下午1:15:17
     */
    public void createWaterMark(String waterRemarkContent, String waterRemarkPath) throws IOException{
    	
        Integer width = 300;
        Integer height = 120;
        int len = waterRemarkContent.length();
        // 默认设置该图片大小为 10个内容长度  如果超出则适当调整图片大小
        if(len > 10) {
        	width = 300 + (len-10) * 20;
        	height = 120 + (len-10) * 10;
        }
        // 获取bufferedImage对象
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        String fontType = "宋体";
        Integer fontStyle = 0;
        Integer fontSize = 30;
        java.awt.Font font = new java.awt.Font (fontType, fontStyle, fontSize);
        // 获取Graphics2d对象
        Graphics2D g2d = image.createGraphics();
        image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        g2d.dispose();
        g2d = image.createGraphics();
        // 设置字体颜色和透明度
        g2d.setColor(new Color(0, 0, 0, 60)); 
        // 设置字体
        g2d.setStroke(new BasicStroke(1));
        // 设置字体类型  加粗 大小
        g2d.setFont(font);
        // 设置倾斜度
        g2d.rotate(Math.toRadians(-10), (double) image.getWidth() / 3, (double) image.getHeight() / 3);
        
        FontRenderContext context = g2d.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(waterRemarkContent, context);
        double x = (width - bounds.getWidth()) / 2;
        double y = (height - bounds.getHeight()) / 2;
        double ascent = -bounds.getY();
        double baseY = y + ascent;
        // 写入水印文字原定高度过小，所以累计写水印，增加高度
        g2d.drawString(waterRemarkContent, (int)x, (int)baseY);
        // 设置透明度
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        // 释放对象
        g2d.dispose();
        
        ImageIO.write(image, "png", new File(waterRemarkPath));
    }
	public boolean isTotal() {
		return isTotal;
	}
	/**
	 * 是否启用合计，若启用则认为最后一行为合计行
	 * @param isTotal
	 * @author zhanghua
	 * @date 2017年7月11日 上午11:26:09
	 */
	public void setTotal(boolean isTotal) {
		this.isTotal = isTotal;
	}
	public boolean getProtect() {
		return protect;
	}

	public void setProtect(boolean protect) {
		this.protect = protect;
	}

	public HSSFSheet getSheet() {
		return sheet;
	}

	public void setSheet(HSSFSheet sheet) {
		this.sheet = sheet;
	}
	public short getRowHeight() {
		return rowHeight;
	}
	
	public void setRowHeight(short height) {
		this.rowHeight = height;
	}
	
	public short getHeadRowHeight() {
		return headRowHeight;
	}
	
	public void setHeadRowHeight(short height) {
		this.headRowHeight = height;
	}

	public void setExportZero(boolean exportZero) {
		this.exportZero = exportZero;
	}

	public boolean getExportZero() {
		return exportZero;
	}
	public boolean isConvertToZero() {
		return convertToZero;
	}
	/**
	 * 设置是否当是数值型指标时，转换成0，默认true
	 * @param convertToZero
	 */
	public void setConvertToZero(boolean convertToZero) {
		this.convertToZero = convertToZero;
	}
	
	public String getFileName() {
		return this.fileName;
	}

	public boolean isPrintSetup() {
		return isPrintSetup;
	}

	public void setPrintSetup(boolean isPrintSetup) {
		this.isPrintSetup = isPrintSetup;
	}

	public boolean isLandscape() {
		return landscape;
	}

	public void setLandscape(boolean landscape) {
		this.landscape = landscape;
	}

	public short getPaperSize() {
		return paperSize;
	}

	public void setPaperSize(short paperSize) {
		this.paperSize = paperSize;
	}
	
	public String getWaterRemarkContent() {
		return waterRemarkContent;
	}
	/**
	 * 设置水印内容
	 * @param waterRemarkContent
	 * @author linbz
	 * @date 2020年3月27日上午10:15:53
	 */
	public void setWaterRemarkContent(String waterRemarkContent) {
		this.waterRemarkContent = waterRemarkContent;
	}

	public String getPassword() {
		return password;
	}
	/**
	 * 设置锁表密码
	 * @param password
	 * @author linbz
	 * @date 2020年4月28日下午4:24:07
	 */
	public void setPassword(String password) {
		this.password = StringUtils.isBlank(password) ? "" : password;
	}
}