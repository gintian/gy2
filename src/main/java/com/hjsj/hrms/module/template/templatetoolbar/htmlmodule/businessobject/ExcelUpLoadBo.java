package com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.ExcelLayoutDao;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.dao.impl.ExcelLayoutDaoImpl;
import com.hjsj.hrms.module.template.templatetoolbar.htmlmodule.vo.UploadContant;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Mar 3, 2010 11:39:22 AM
 * </p>
 *
 * @author dengc
 * @version 5.0
 */
public class ExcelUpLoadBo {
    private Connection conn = null;
    /**模板号*/
	private int tabid=0;
    private UserView userview = null;
    /**获取dao*/
	private ExcelLayoutDao excelLayoutDao =null;

	private String moudle_id="1";
	//存放内容指标级联信息
	private Map<String, LazyDynaBean> relationMap=new HashMap<String, LazyDynaBean>();

	private TemplateUtilBo utilBo;
	private String idCardFielditem;//身份证号指标
    public String getMoudle_id() {
		return moudle_id;
	}
	public void setMoudle_id(String moudle_id) {
		this.moudle_id = moudle_id;
	}
    public ExcelUpLoadBo(int tabid, Connection con, UserView userview) {
        this.conn = con;
        this.tabid = tabid;
        this.userview = userview;
        this.excelLayoutDao=new ExcelLayoutDaoImpl(conn);
        this.utilBo = new TemplateUtilBo(this.conn,this.userview);
        this.idCardFielditem=getIdCardField();
    }
    /**
     * 获取到
     * @param form_file
     * @throws GeneralException
     */
	public JSONObject getLayoutByExcel(InputStream inputStream) throws GeneralException {
		Workbook wb = null;
        Sheet sheet = null;
        JSONObject json=new JSONObject();
        int totalnum=0;
		try {
            HashMap fieldsetmap = new HashMap();
            wb = WorkbookFactory.create(inputStream);
            int sheetsnum = wb.getNumberOfSheets();
    		json.put("module", this.moudle_id);//"1|2|3|4 //模块标识 "
            JSONArray pages_arr=new JSONArray();
    		int picNum=0;//目前仅支持单个图片
    		String pageTitle="";
    		 /** 输出单元格 */
            ArrayList celllist =getPageCell(-1);
            int page_row=this.getMaxPageId(tabid+"")+100;

            for (int i = 0; i < sheetsnum; i++) {
            	sheet = wb.getSheetAt(i);
                int rows = sheet.getPhysicalNumberOfRows();
                if(rows<1){
                	continue;
                }
                Row row = sheet.getRow(0);
            	if(row==null){
            		continue;
            	}
            	Cell cell0 = row.getCell(0);
            	if (cell0 != null) {
            		String val = getCellValue(cell0);
            		val=val.trim();
            		if(val.startsWith("pageid")){
            			String pageidv=val.substring(7);
            			if(pageidv.startsWith("A")){
            				int k=Integer.valueOf(pageidv.substring(1));
            				if(k>page_row){
            					page_row=k+1;
            				}
            			}
            		}
            	}
            }

            PoiExcelToHtmlUtil poiExcelToHtmlUtil=new PoiExcelToHtmlUtil();
    		poiExcelToHtmlUtil.init(sheet);
            for (int i = 0; i < sheetsnum; i++) {
            	JSONArray layout_arr=new JSONArray();
                String sheetname = wb.getSheetName(i);
                sheet = wb.getSheetAt(i);
                int rows = sheet.getPhysicalNumberOfRows();
                if(rows<1){
                	continue;
                }
                //获取每一页中最后一个不为空的列是第几个
                int maxNum = getMaxCols(sheet, rows);

                String pageid="";
                boolean pageidFlag=false;
                Row row1 = sheet.getRow(0);
                Cell cell0 = row1.getCell(0);
        		if (cell0 != null) {
        			String val = getCellValue(cell0);
        			val=val.trim();
        			if(val.startsWith("pageid")){
        				pageid=val.substring(7);
        				if(pageid.startsWith("A")){
        					pageidFlag=true;
        					pageid=pageid.substring(1);
        				}
        			}else{
        				pageid=(page_row+i)+"";
        				pageidFlag=true;
        			}
        		}
                pageTitle=sheetname;
                //初始化relatiaonMap字段
                initRelationMap((ArrayList)celllist.clone());
                JSONObject pages=new JSONObject();
                int isSetPage=0;//初始化，是否新增了一列 pageid:0
                /**遍历行*/
                for(int r=0;r<rows;r++){
                	Row row = sheet.getRow(r);
                	if(row==null){
                		continue;
                	}
            		JSONObject layout=new JSONObject();
                	JSONArray row_arr=new JSONArray();
                	int cnum=0;
                	//定义10个
                	int[] cellwidth=new int[maxNum+1];
                	int totalwidth=0;
                	boolean bl=false;//初始化
                	/**遍历 列 */
                	for (int c = 0; c <= maxNum; c++) {
                		Cell cell = row.getCell(c);
                		if (cell != null) {
                			String value = getCellValue(cell);
                			value=value.trim();
                			//if(value.startsWith("b0110_2"))
                			if(c==0&&r==0){
                				isSetPage=value.startsWith("pageid:A")?2:(value.startsWith("pageid:")?1:0);
                				//头一个是 pageid:开头的，撤销掉
                				if(isSetPage==1){
                					continue;
                				}
                			}
                			if(c==0){
                				//头一个是 pageid:开头的，撤销掉
                				if(isSetPage==1){
                					continue;
                				}
                			}
                			int columnWidth = 0;
                			Map locationMap=isMergedRegion(sheet, r, c,isSetPage);
                			int columnDifference=0;
                			if(locationMap!=null&&locationMap.containsKey("columnDifference")){
                				columnDifference=(int)locationMap.get("columnDifference");
                				int maxC=c+columnDifference;
                				for(int k=c;k<=maxC;k++){
                					columnWidth+=sheet.getColumnWidth(k);
                				}
                			}
                			if(StringUtils.isEmpty(value)||value.trim().length()<1){
                				//合并列里面的记录排除掉。
                    			if(locationMap!=null&&locationMap.containsKey("lastColumn")){
                    				int lastColumn=(int)locationMap.get("lastColumn");
                    				if(c<=lastColumn&&columnDifference>0){
                    					continue;
                    				}
                    			}
                				cnum++;
                				cellwidth[c]=columnWidth==0?2048:columnWidth;
                				totalwidth+=cellwidth[c];
                				Map map=new HashMap();
                				map.put("style", "width:" + cellwidth[c] / 256 * 200 / 20 + "pt;");
                    			createDescribeEditor(row_arr, "","",map);
                				continue;
                			}

                			//如果是模板设置说明文字， 则过滤掉。
                			if(value.equals(getTempletSetDesc())){
                				continue;
                			}
                			//0 left 1 center  2 right
                			int align=1;
                			HorizontalAlignment cellStall = cell.getCellStyle().getAlignmentEnum();
                			if(cellStall.compareTo(HorizontalAlignment.LEFT)==0){
                				align=0;
                			}else if(cellStall.compareTo(HorizontalAlignment.RIGHT)==0){
                				align=2;
                			}

                			StringBuffer sb=new StringBuffer();
							poiExcelToHtmlUtil.dealExcelFontStyle(wb, sheet, cell, sb);
							locationMap.put("style", sb.toString());

                			//获取批注信息
                			String cellComment = cell.getCellComment()==null?"":cell.getCellComment().getString().toString();
                			if(StringUtils.isNotEmpty(value)&&value.startsWith("collapse:")){
                				cnum++;
                				cellwidth[c]=columnWidth;
                				totalwidth+=columnWidth;
                				createCollapseEditor(row_arr, value.substring("collapse:".length()));
                				continue;
                			}else if(StringUtils.isNotEmpty(value)&&value.startsWith("describe:")){
                				cnum++;
                				cellwidth[c]=columnWidth;
                				totalwidth+=columnWidth;
                				createDescribeEditor(row_arr, value.substring("describe:".length()),cellComment,locationMap);
                				continue;
                				//describeBlank描述  无样式
                			}else if(StringUtils.isNotEmpty(value)&&value.startsWith("describe_blank:")){
                				cnum++;
                				cellwidth[c]=columnWidth;
                				totalwidth+=columnWidth;
                				createDescribeBlankEditor(row_arr, value.substring("describe_blank:".length()),cellComment,locationMap);
                				continue;
                				//ivider:分割线
                			}else if(StringUtils.isNotEmpty(value)&&value.startsWith("ivider:")){
                				cnum++;
                				cellwidth[c]=columnWidth;
                				totalwidth+=columnWidth;
                				createIviderEditor(row_arr, value.substring("ivider:".length()));
                				continue;
                			}else if(StringUtils.isNotEmpty(value)&&value.startsWith("option:")){
                				cnum++;
                				cellwidth[c]=columnWidth;
                				totalwidth+=columnWidth;
                				//审批意见 固定字符串
                				createOptionEditor(row_arr, "opinionContent",cellComment,locationMap);
                				continue;
                			}else if(StringUtils.isNotEmpty(value)&&value.startsWith("text:")){
                				cnum++;
                				cellwidth[c]=columnWidth;
                				totalwidth+=columnWidth;
                				HashMap map=new HashMap();
                				map.put("hz", value.substring("text:".length()));
                				map.put("align", align);
                				map.put("cellComment", cellComment);
                				//审批意见 固定字符串
                				createTextEditor(row_arr,map,locationMap);
                				continue;
                			}

                			String itemid=value;
                			String itemname="";
                			if(StringUtils.isNotEmpty(value)&&value.indexOf(":")!=-1){
                				itemid=value.substring(0, value.indexOf(":"));
                				itemname=value.substring(value.indexOf(":")+1);
                			}
                			boolean itemUseFlag=false;
                			boolean itemUseFlag2=false;
                			String itemid2="";
                			//校验【java_01】这种情况。
                			if(value.startsWith("【")&&value.endsWith("】")){
                				itemid2=value.substring(1,value.length()-1);
                			}
                			//校验传入的value值
                			for(int m=0;m<celllist.size();m++)
                			{
                				TemplateSet setcell=(TemplateSet)celllist.get(m);
                				String tabFldName = setcell.getTableFieldName();
                				if((itemid).equalsIgnoreCase(tabFldName))
                				{
                					itemUseFlag=true;
                					break;
                				}

                				if(StringUtils.isNotBlank(itemid2)&&(itemid2).equalsIgnoreCase(tabFldName))
                				{
                					itemUseFlag2=true;
                					break;
                				}
                			}
                			if(!itemUseFlag){
                				if(itemUseFlag2){
                					itemid=itemid2;
                					itemname="";
                				}else{
                					cnum++;
                    				cellwidth[c]=columnWidth;
                    				totalwidth+=columnWidth;
                    				HashMap map=new HashMap();
                    				map.put("hz", value);
                    				map.put("align", align);
                    				map.put("cellComment", cellComment);
                    				//审批意见 固定字符串
                    				createTextEditor(row_arr,map,locationMap);
                    				continue;
                				}
                			}
                			for(int m=0;m<celllist.size();m++)
                			{
                				TemplateSet setcell=(TemplateSet)celllist.get(m);
                				String fieldname  = setcell.getField_name();
                				String fieldType = setcell.getFlag();
                				String tabFldName = setcell.getTableFieldName();
                				int chgstate=setcell.getChgstate();
                				/**目前仅支持单个图片，且是头像图片*/
                				/**找到当前指标*/
            					if("F".equals(fieldType)&&(tabFldName).equalsIgnoreCase(itemid)){
                					cnum++;
                					totalnum++;
                					cellwidth[c]=columnWidth;
                					totalwidth+=columnWidth;
                					TemplateSet setcell2=(TemplateSet) setcell.clone();
                					setcell2.setHz(itemname.trim());
                					setcell2.setAlign(align);
                					createAttachmentEditor(row_arr,setcell2,cellComment,locationMap);
                					break;
                				}else if(setcell.isSubflag()){
                					if((itemid).equalsIgnoreCase(tabFldName)){
                						cnum++;
                						totalnum++;
                						cellwidth[c]=columnWidth;
                						totalwidth+=columnWidth;
                						TemplateSet setcell2=(TemplateSet) setcell.clone();
                						setcell2.setHz(itemname.trim());
                						setcell2.setAlign(align);
                    					/**创建输入框*/
                    					createEditor(row_arr,setcell2,cellComment,locationMap);
                    					break;
                					}
                				}else if((itemid).equalsIgnoreCase(tabFldName)){
                					cnum++;
                					totalnum++;
                					cellwidth[c]=columnWidth;
                					totalwidth+=columnWidth;
                					TemplateSet setcell2=(TemplateSet) setcell.clone();
                					setcell2.setHz(itemname.trim());
                					setcell2.setAlign(align);
                					/**创建输入框*/
                					createEditor(row_arr,setcell2,cellComment, locationMap);
                					break;
                				}

            					if("P".equals(fieldType)&&picNum==0) //picture
            					{
            						picNum++;
            						TemplateSet setcell2=(TemplateSet) setcell.clone();
            						setcell2.setAlign(align);
            						setcell2.setHz(itemname.trim());
            						createTitleImageEditor(json,setcell2);
            					}
                			}
                		}else{
                			if(isSetPage==1&&c==0){
                				continue;
                			}
                			cnum++;
            				cellwidth[c]=sheet.getColumnWidth(c);
            				cellwidth[c]=cellwidth[c]==0?2048:cellwidth[c];
            				totalwidth+=cellwidth[c];
            				Map map=new HashMap();
            				map.put("style", "width:" + cellwidth[c] / 256 * 200 / 20 + "pt;");
                			createDescribeEditor(row_arr, "","",map);
            				continue;
                		}
                	}
                	if(cnum<1){
                		continue;
                	}
                	layout.put("horizontal_id", "h"+pageid+(r+1));
            		layout.put("columns_num", cnum+"");
            		String columns_width="";
            		if(totalwidth!=0){
            			//遍历cellwidth
            			for(int j=0;j<maxNum+1;j++){
            				if(cellwidth[j]<=0){
            					continue;
            				}
            				DecimalFormat df=new DecimalFormat(".##");
            				columns_width+=df.format(((cellwidth[j]*100.00)/totalwidth))+"%,";
            			}
            		}
            		if(cnum==1){
            			//row_arr 取第一个
            			JSONObject objJson=(JSONObject) row_arr.get(0);
            			if(objJson.containsKey("type")){
            				switch((String)objJson.get("type"))
            			     {
            			        case UploadContant.type_avatar:
            			        case UploadContant.type_radio:
            			        case UploadContant.type_checkbox:
            			        case UploadContant.type_select:
            			        case UploadContant.type_dateTimePicker:
            			        case UploadContant.type_datePicker:
            			        case UploadContant.type_describe:
            			        case UploadContant.type_text:
            			        case UploadContant.type_input:
            			        	bl=true;
            			        	break;
            			     }
            			}
            		}
            		if(StringUtils.isNotBlank(columns_width)){
            			String colPre=columns_width.substring(0, columns_width.length()-1);
            			layout.put("columns_width", colPre);
            		}else{
            			layout.put("columns_width", "100%");
            		}
                	layout.put("content", row_arr);
                	/**教育经历 end*/
            		layout_arr.add(layout);
                }

                //添加spflag审批意见
//                if(spflag){
//                	JSONObject layout=new JSONObject();
//                	JSONArray row_arr=new JSONArray();
//
//                	createCollapseEditor(row_arr, "审批意见");
//                	layout.put("horizontal_id", "h"+pageid+(rows+1));
//            		layout.put("columns_num", 1+"");
//            		layout.put("columns_width", "100%");
//                	layout.put("content", row_arr);
//            		layout_arr.add(layout);
//
//            		layout.clear();
//            		row_arr.clear();
//            		//审批意见 固定字符串
//    				createOptionEditor(row_arr, "opinionContent");
//                	layout.put("horizontal_id", "h"+pageid+(rows+2));
//            		layout.put("columns_num", 1+"");
//            		layout.put("columns_width", "100%");
//                	layout.put("content", row_arr);
//            		layout_arr.add(layout);
//
//                	spflag=false;
//                }
                if(layout_arr.size()>0){
                	pages.put("page_desc", pageTitle);
                    pages.put("page_id", pageid);
                    pages.put("is_out_page", pageidFlag);
                    pages.put("required", "");//true|false, //页签内容是否为必填项
                    pages.put("fill_status", "");//ok|part|null, //填写情况  ok：都填完了、 part：只填了部分、null：没填
                	pages.put("layout", layout_arr);
                	pages_arr.add(pages);
                }
            }
            if(picNum==0){
            	createTitleImageEditor(json,null);
            }
            json.put("pages", pages_arr);
		}  catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(inputStream);
			try {
				wb.close();
				sheet=null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(totalnum<1){
			throw new GeneralException("请上传指定格式的Excle模板");
		}
		return json;
	}

	/**
	 * 获取模板设置说明文字 用于做匹配
	 * @return
	 */
	public String getTempletSetDesc(){
		return "模板设置说明：\nA区：\n   a0101_2:姓名，字符串型，展示为文本框。\n   a0107_1:性别，代码型，展示相关代码，为下拉列表。\n   a1025_2:聘任起始时间，日期型，展示日期型。\nB区：\n   collapse:教育背景，收缩分割线,教育背景为前台展示字符，可修改。\n   t_a04_2:教育背景，子集，展示为表格。\n   ivider: ，分割线。\nC区：\n   describe:任 现 职...，有样式的纯文本描述，自定义。\n   describe_blank:任 现 职...，去除样式的纯文本描述，自定义。\n   a01aa_1:备注,输入方式为简单编辑器，前台展示为文本框，输入方式为html编辑器，前台展示为富文本编辑器。";
	}
	/**
	 * 获取每一页中最后一个不为空的列是第几个
	 * @param sheet
	 * @param rows
	 * @return
	 */
	private int getMaxCols(Sheet sheet, int rows) {
		int maxNum=0;
		/**遍历行  获取最大值*/
		for(int r=0;r<rows;r++){
			Row row = sheet.getRow(r);
			if(row==null){
				continue;
			}
			//是获取最后一个不为空的列是第几个。
			int cols = row.getLastCellNum();
			int tempc=0;
			/**遍历 列 */
			for (int c = cols; c > 0; c--) {
				Cell cell = row.getCell(c);
				if (cell != null) {
					String value = getCellValue(cell);
					if(StringUtils.isNotEmpty(value)){
						tempc=c;
						break;
					}
				}
			}
			//赋值为最大行值。
			maxNum=maxNum<tempc?tempc:maxNum;
		}
		return maxNum;
	}

	/**
	 * 功能：验证某个单元格是否是合并单元格，以及该单元格所属的合并单元格的开始行、结束行、起始列以及结束列，
	 * @param sheet
	 * @param row
	 * @param column
	 * @param isSetPage
	 * @return
	 */
	private Map isMergedRegion(Sheet sheet,int row ,int column, int isSetPage) {
		Map map=new HashMap();
		int sheetMergeCount = sheet.getNumMergedRegions();
		for (int i = 0; i < sheetMergeCount; i++) {
			CellRangeAddress range = sheet.getMergedRegion(i);
			int firstColumn = range.getFirstColumn();
			int lastColumn = range.getLastColumn();
			int firstRow = range.getFirstRow();
			int lastRow = range.getLastRow();
			if(row >= firstRow && row <= lastRow){
				if(column >= firstColumn && column <= lastColumn){
					//开始行
					map.put("firstRow", (isSetPage==1||isSetPage==0)?firstRow+1:firstRow);
					//结束行
					map.put("lastRow", (isSetPage==1||isSetPage==0)?lastRow+1:lastRow);
					//开始列
					map.put("firstColumn", (isSetPage==2||isSetPage==0)?firstColumn+1:firstColumn);
					//结束列
					map.put("lastColumn", (isSetPage==2||isSetPage==0)?lastColumn+1:lastColumn);
					map.put("columnDifference", (lastColumn-firstColumn));
					return map;
				}
			}
		}
		//开始行
		map.put("firstRow", (isSetPage==1||isSetPage==0)?row+1:row);
		//结束行
		map.put("lastRow", (isSetPage==1||isSetPage==0)?row+1:row);
		//开始列
		map.put("firstColumn", (isSetPage==2||isSetPage==0)?column+1:column);
		//结束列
		map.put("lastColumn", (isSetPage==2||isSetPage==0)?column+1:column);
		//差距
		map.put("columnDifference", 0);
		return map;
	}
	private int getMaxPageId(String tabId) {
		return excelLayoutDao.getMaxPageId(tabId);
	}
	/**
	 * 功能：初始化 relationMap 字段
	 * @param celllist
	 * @return
	 */
	private HashMap<String, LazyDynaBean> initSubsetRelationMap(TemplateSet setBo) {
		if(setBo==null){
			return new HashMap<String, LazyDynaBean>();
		}
		HashMap relationFieldMap=new HashMap();
		ArrayList subfiledlist=setBo.getSubFieldList();
		if(subfiledlist!=null&&subfiledlist.size()>0){
			for(int j=0;j<subfiledlist.size();j++){
				SubField itemFiled=(SubField) subfiledlist.get(j);
				if(StringUtils.isNotBlank(itemFiled.getRelation_field())&&2==setBo.getChgstate()){
					relationFieldMap.put(itemFiled.getFieldname(), itemFiled);
				}
			}
		}
		HashMap<String, LazyDynaBean> subRelationMap=new HashMap<String, LazyDynaBean>();
        if(relationFieldMap.size()>0){
        	String pre=setBo.getTableFieldName()+"_";
			Iterator iterator = relationFieldMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry entry=(Entry)	iterator.next();
				SubField subsetBo = (SubField)entry.getValue();
				String relationField=subsetBo.getRelation_field();
				String fatherRelationField="";
				for(int i=0;i<subfiledlist.size();i++){
					LazyDynaBean fieldBean=null;
					SubField codeBo=(SubField)subfiledlist.get(i);
					String fieldfldName = codeBo.getFieldname();
					String childRelationField ="";
					if(relationField.equalsIgnoreCase(fieldfldName)){
						if(subRelationMap.containsKey(subsetBo.getFieldname())){
							fieldBean=subRelationMap.get(subsetBo.getFieldname());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}

						if(fieldBean==null){
							fieldBean=new LazyDynaBean();
						}

						if(StringUtils.isBlank(fatherRelationField)){
							fatherRelationField=pre+codeBo.getFieldname()+"`"+codeBo.getFieldname()+"&&"+codeBo.getFieldItem().getCodesetid();
						}else{
							fatherRelationField+=";"+pre+codeBo.getFieldname()+"`"+codeBo.getFieldname()+"&&"+codeBo.getFieldItem().getCodesetid();
						}
						fieldBean.set("fatherRelationField", fatherRelationField);
						subRelationMap.put(subsetBo.getFieldname(), fieldBean);

						//创建新的对象
						fieldBean=new LazyDynaBean();
						if(subRelationMap.containsKey(codeBo.getFieldname())){
							fieldBean=subRelationMap.get(codeBo.getFieldname());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}else{
							fatherRelationField="";
							childRelationField="";
						}
						if(StringUtils.isBlank(childRelationField)){
							childRelationField=pre+subsetBo.getFieldname()+"`"+subsetBo.getFieldname()+"&&"+subsetBo.getFieldItem().getCodesetid();
						}else{
							childRelationField=";"+pre+subsetBo.getFieldname()+"`"+subsetBo.getFieldname()+"&&"+subsetBo.getFieldItem().getCodesetid();
						}

						fieldBean.set("childRelationField", childRelationField);
						fieldBean.set("fatherRelationField", fatherRelationField);
						subRelationMap.put(codeBo.getFieldname(), fieldBean);
					}
				}
			}
        }
        return subRelationMap;
	}

	/**
	 * 功能：初始化 relationMap 字段
	 * @param celllist
	 */
	private void initRelationMap(ArrayList celllist) {
		if(celllist.size()<=0){
			return;
		}
		HashMap relationFieldMap=new HashMap();

		for(int m=0;m<celllist.size();m++)
		{
			TemplateSet setBo=(TemplateSet)celllist.get(m);
			if(StringUtils.isEmpty(setBo.getCodeid())||"0".equals(setBo.getCodeid())){
				continue;
			}
            if(StringUtils.isNotBlank(setBo.getRelation_field())&&2==setBo.getChgstate()){
				relationFieldMap.put(setBo.getUniqueId(), setBo);
			}
		}

        if(relationFieldMap.size()>0){
			Iterator iterator = relationFieldMap.entrySet().iterator();
			while(iterator.hasNext()){
				Entry entry=(Entry)	iterator.next();
				TemplateSet setBo = (TemplateSet)entry.getValue();
				String relationField=setBo.getRelation_field();
				String uniqueId=(String)entry.getKey();
				String fatherRelationField="";
				for(int i=0;i<celllist.size();i++){
					LazyDynaBean fieldBean=null;
					TemplateSet codeBo=(TemplateSet)celllist.get(i);
					String fieldUniqueId = codeBo.getUniqueId();
					String fieldfldName = codeBo.getPageId()+"_"+codeBo.getGridno();
					String childRelationField ="";

					if((!uniqueId.equalsIgnoreCase(fieldUniqueId))&&relationField.equalsIgnoreCase(fieldfldName)){
						if(relationMap.containsKey(setBo.getUniqueId())){
							fieldBean=relationMap.get(setBo.getUniqueId());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}

						if(fieldBean==null){
							fieldBean=new LazyDynaBean();
						}

						if(StringUtils.isBlank(fatherRelationField)){
							fatherRelationField=codeBo.getTableFieldName()+"`"+codeBo.getTableFieldName()+"&&"+codeBo.getCodeid();
						}else if(fatherRelationField.indexOf("`"+codeBo.getTableFieldName()+"&&")!=-1){
							continue;
						}else{
							fatherRelationField+=";"+codeBo.getTableFieldName()+"`"+codeBo.getTableFieldName()+"&&"+codeBo.getCodeid();
						}
						fieldBean.set("fatherRelationField", fatherRelationField);
						this.relationMap.put(setBo.getUniqueId(), fieldBean);

						//创建新的对象
						fieldBean=new LazyDynaBean();
						if(this.relationMap.containsKey(codeBo.getUniqueId())){
							fieldBean=relationMap.get(codeBo.getUniqueId());
							childRelationField = (String) fieldBean.get("childRelationField");
							fatherRelationField=(String) fieldBean.get("fatherRelationField");
						}else{
							fatherRelationField="";
							childRelationField="";
						}
						if(StringUtils.isBlank(childRelationField)){
							childRelationField=setBo.getTableFieldName()+"`"+setBo.getTableFieldName()+"&&"+setBo.getCodeid();
						}else if(childRelationField.indexOf("`"+setBo.getTableFieldName()+"&&")!=-1){
							continue;
						}else{
							childRelationField+=";"+setBo.getTableFieldName()+"`"+setBo.getTableFieldName()+"&&"+setBo.getCodeid();
						}

						fieldBean.set("childRelationField", childRelationField);
						fieldBean.set("fatherRelationField", fatherRelationField);
						relationMap.put(codeBo.getUniqueId(), fieldBean);
					}
				}
			}
        }


	}
	/**
	 * Postil 批注
	 * @param td
	 * @param hz
	 */
	public void createPostilEditor(JSONArray td, TemplateSet setBo,String cellComment) {
		JSONObject text=new JSONObject();
		String hz=setBo.getHz();
		hz=hz.replaceAll("\\{", "");
		hz=hz.replaceAll("\\}", "");
		hz=hz.replaceAll("`", "");

		text.put("type", UploadContant.type_describe);
        text.put("content", hz);
        text.put("cellComment", cellComment);
        text.put("relation_id", setBo.getTableFieldName());
        td.add(text);
	}

	public void createDescribeEditor(JSONArray td, String hz,String cellComment, Map locationMap) {
		JSONObject text=new JSONObject();
		text.put("type", UploadContant.type_describe);
        text.put("content", hz);
        //添加Excel批注
        text.put("cellComment", cellComment);
        if(locationMap!=null){
        	//循环转换
        	Iterator it =locationMap.entrySet().iterator();
        	while (it.hasNext()) {
        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
        		text.put(entry.getKey(), entry.getValue());
        	}
        }
//        text.put("hidden", false);
        td.add(text);
	}
	/**
	 * 功能：describe描述  无样式
	 * @param td
	 * @param hz
	 * @param locationMap
	 */
	public void createDescribeBlankEditor(JSONArray td, String hz,String cellComment, Map locationMap) {
		JSONObject text=new JSONObject();
		text.put("type", UploadContant.type_describe_blank);
        text.put("content", hz);
        //添加Excel批注
        text.put("cellComment", cellComment);
//        text.put("hidden", false);
        if(locationMap!=null){
        	//循环转换
        	Iterator it =locationMap.entrySet().iterator();
        	while (it.hasNext()) {
        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
        		text.put(entry.getKey(), entry.getValue());
        	}
        }
        td.add(text);
	}
	/**
	 * 功能：增加审批意见
	 * @param td
	 * @param locationMap
	 * @param hz
	 */
	public void createOptionEditor(JSONArray td, String element_id,String cellComment, Map locationMap) {
		JSONObject text=new JSONObject();
		text.put("type", UploadContant.type_opinion);
        text.put("element_id", element_id);
        //添加Excel批注
        text.put("cellComment", cellComment);
//        text.put("hidden", false);
        if(locationMap!=null){
        	//循环转换
        	Iterator it =locationMap.entrySet().iterator();
        	while (it.hasNext()) {
        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
        		text.put(entry.getKey(), entry.getValue());
        	}
        }
        td.add(text);
	}
	/**
	 * 生成唯一值
	 * @return
	 */
	public String getUniqueOne(){
		UUID uuid=UUID.randomUUID();
        String uuidStr=uuid.toString();
        return uuidStr;
	}
	private void createTitleImageEditor(JSONObject json, TemplateSet setBo) {
		JSONObject avatar_obj=new JSONObject();
		avatar_obj.put("avatar_id", "p1");
		avatar_obj.put("label", "照片");
		avatar_obj.put("file_name", "");//file_name:"xxxxx.bmp", //用户上传的文件名
		avatar_obj.put("file_path", "");//file_path:"subdomain\template_201\T386\T427", //文件路径
		if(setBo==null){
			avatar_obj.put("required",false);
			avatar_obj.put("readonly",true);
			avatar_obj.put("relation_id",getUniqueOne());//数据库中的字段名称
			json.put("avatar", avatar_obj);
			return;
		}
		if(setBo.isYneed())
		{
			avatar_obj.put("required",true);
		}else{
			avatar_obj.put("required",false);
		}

		if(setBo.getChgstate()==2){
			avatar_obj.put("readonly",false);
		}else{
			avatar_obj.put("readonly",true);
		}
		setElementPublicProperty(avatar_obj,setBo);
		json.put("avatar", avatar_obj);
	}


	/**
	    * @Title: getPageCell
	    * @Description: 返回指定页的所有单元格celllist
	    * @param @param tabId
	    * @param @param pagenum
	    * @param @return
	    * @return ArrayList
	    */
	    public ArrayList getPageCell(int pageNum) {

	    	return utilBo.getPageCell(this.tabid,pageNum);
	    }


	    /**
		 * 如果为null返回“”字符串
		 * @param value
		 * @return
		 */
		private static String nullToSpace(String value)
		{
			if(value==null)
				return "";
			else
				return value;
		}

		/**
		 * 创建输入框
		 * @param td
		 * @param locationMap
		 * @param cellComment:Excel批注信息
		 * @throws GeneralException
		 */
		private void createEditor(JSONArray td,TemplateSet setBo,String cellComment, Map locationMap) throws GeneralException
		{
			if(setBo.getFlag()==null|| "".equalsIgnoreCase(setBo.getFlag()))
				setBo.setFlag("H");
			String flag=setBo.getFlag();
			String field_type=setBo.getField_type();
			String field_name = setBo.getField_name(); //xgq 电子签章
			if("H".equals(flag)){//汉字描述 不考虑
			}else if("A".equals(flag)|| "B".equals(flag)|| "K".equals(flag)){ //指标
				if(((setBo.getHismode()==2||setBo.getHismode()==3||(setBo.getHismode()==4))&&setBo.getChgstate()==1)||(setBo.isSubflag())){
					/**条件1：序号定位| 2：(条件序号&&!最近第&&最初第)&&变化前) 3：子集（subflag=true）**/
			      if(!setBo.isSubflag()) {
			    	  if(((setBo.getHismode()==2 || setBo.getHismode()==4) && (setBo.getMode()==1
	                      || setBo.getMode()==3))||setBo.getHismode()==3){
		                //序号定位 、条件定位的最近第、 最初第因为是一条记录  按普通当前记录显示方式显示 wangrd 20151026
		                 FieldItem fldItem = DataDictionary.getFieldItem(field_name,setBo.getSetname());
		                 if (fldItem!=null && "M".equalsIgnoreCase(fldItem.getItemtype())){
		                     createTextAreaEditor(td, setBo,cellComment,locationMap);
		                 }else if(fldItem.getItemlength()>=255&&"0".equals(setBo.getCodeid())&&"A".equals(setBo.getField_type())&&"A".equals(setBo.getFlag())){
		                	 createTextAreaEditor(td, setBo,cellComment,locationMap);
		                 }
		                 else {
		                     createInputEditor(td,setBo,cellComment,locationMap);
		                 }
		              }else {
		            	  createInputEditor(td,setBo,cellComment,locationMap);
		              }
			      }else {
	            	  createDivPanel(td,setBo,cellComment,locationMap);
			      }
			  }
			  else
			  {
				  if("D".equalsIgnoreCase(field_type))
				  {
						createDatePickerEditor(td,setBo,cellComment,locationMap);
				  }
				  else if("N".equalsIgnoreCase(field_type))
				  {
						createInputEditor(td,setBo,cellComment,locationMap);
				  }
				  else if("M".equalsIgnoreCase(field_type))
				  {
					  createTextAreaEditor(td,setBo,cellComment,locationMap);
				  }
				  else if("A".equalsIgnoreCase(field_type))
				  {
					createInputEditor(td,setBo,cellComment,locationMap);
				  }
			  }
			}
			else if("P".equals(flag)) //picture
			{
				createImageEditor(td,setBo,cellComment);
			}
			else if("F".equals(flag)) //attachment
			{
				createAttachmentEditor(td,setBo,cellComment,locationMap);
			}
			else if("V".equals(flag))//临时变量
			{
				if("A".equals(setBo.getField_type())){
					RecordVo varVo= setBo.getVarVo();
					if(varVo.getInt("fldlen")>=255&&varVo.getInt("ntype")==2){
						createTextAreaEditor(td, setBo,cellComment,locationMap);
					}else{
						createInputEditor(td,setBo,cellComment,locationMap);
					}
				}else if("D".equals(setBo.getField_type())){
					createDatePickerEditor(td,setBo,cellComment,locationMap);
				}else{
					createInputEditor(td,setBo,cellComment,locationMap);
				}
			}
			else if("C".equals(flag))//计算公式 wangrd 2013-12-30
			{
			    createInputEditor(td,setBo,cellComment,locationMap);
			}
		}

		/**
		 * 创建收缩分割线
		 * @param td
		 */
		private void createCollapseEditor(JSONArray td,String hz) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_collapse);
			//每个面板的唯一性标识
			text.put("name",getUniqueOne());//单元格唯一键值
//			text.put("relation_id",hz);//数据库中的字段名称
	        text.put("title", hz);
	        //是否为手风琴模式
	        text.put("accordion", false);
	        td.add(text);
		}

		/**
		 * 创建收缩分割线
		 * @param td
		 */
		private void createIviderEditor(JSONArray td,String content) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_divider);
			text.put("element_id",getUniqueOne());//单元格唯一键值
			//设置分割线方向  horizontal| vertical
			text.put("direction","horizontal");//horizontal| vertical （横向|纵向）
			text.put("content", content);
			text.put("contentPosition", "center");
	        td.add(text);
		}

		/**
		 * 创建历史记录输出面板
		 * @param td
		 * @param locationMap
		 * @throws GeneralException
		 */
		private void createDivPanel(JSONArray td,TemplateSet setBo,String cellComment, Map locationMap) throws GeneralException {
			String setname=setBo.getSetname();
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_table);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", "");
	        //label 如果没有 ，就设置label_hz
	        text.put("label_hz", hz);
	        text.put("label_align", "left");//默认放置左边
	        //最大输入长度
	        text.put("maxlength", "20");
	        text.put("disabled", false);//是否禁用
	        text.put("hidden", false);////是否可见
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否显示序号
			text.put("serial_flag", true);
			//是否有审核列 无’0’ 读 ‘1’写 ‘2’
			text.put("isverify", "2");
			//最多支持录入行数
			text.put("maxnum", "100");
			//表格高度
			text.put("height", setBo.getRheight());//当前单元格高度
			text.put("maxheight", setBo.getRheight());//当前单元格高度
			//审核列下拉选项，只在isverify为’1’和’2’时有，其数据格式可传 [{verify_desc:'通过',verify_code:'2'},{verify_desc:'不通过', verify_code:'1'}]
			text.put("verify_list", "[{verify_desc:'通过',verify_code:'2'},{verify_desc:'不通过', verify_code:'1'}]");
			//表格头部工具栏  数据格式 [{button_id:'b1',button_desc:'批量审核',function_id:'XXXXXXXXX',button_type:''}]
			String buttons=getSubSetButtons(setBo);
			text.put("buttons", buttons);
			//添加Excel批注
			text.put("cellComment", cellComment);
	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }

			String subFields=setBo.getSubFields();
			HashMap<String, LazyDynaBean> subRelationMap=initSubsetRelationMap(setBo);
			ArrayList subfiledlist=setBo.getSubFieldList();
			if(subfiledlist!=null&&subfiledlist.size()>0){
				JSONArray subitems=new JSONArray();
				for(int j=0;j<subfiledlist.size();j++){
					SubField itemFiled=(SubField) subfiledlist.get(j);
					String itemid=itemFiled.getFieldname();
					if("attach".equals(itemid)){
						createAttachmentEditor(subitems,itemFiled,setBo,cellComment);
						continue;
					}
					FieldItem fielditem=itemFiled.getFieldItem();
					JSONObject subitem=new JSONObject();
					subitem.put("type", UploadContant.type_input);
					fielditem=fielditem==null?DataDictionary.getFieldItem(itemid):fielditem;
					if(fielditem==null){
						throw new GeneralException(hz+"子集中"+itemFiled.getTitle()+"指标已被删除，请重新定义模板！");
					}
					//添加指标解释
					String itemmemo=fielditem.getExplain();
					if(itemmemo==null)
						itemmemo="";
					itemmemo = itemmemo.replaceAll("\r\n", "\n");
			        itemmemo = itemmemo.replaceAll("\n", "<br>");
			        if("<br>".equals(itemmemo.trim()))
			            itemmemo="";
			        //设置指标解释
			        subitem.put("itemmemo", itemmemo);

					if(fielditem.getItemlength()>=255&&"0".equals(fielditem.getCodesetid())&&"A".equals(fielditem.getItemtype())){//大于255的字符型指标看做大文本处理
						subitem.put("type", UploadContant.type_textarea);
					}
					String itemdesc=itemFiled.getTitle();
			        subitem.put("label", itemdesc);
					subitem.put("column_id", itemid);
					subitem.put("element_id",setBo.getTableFieldName()+"_"+itemid);//单元格唯一键值 列是唯一的
					if (!"0".equals(fielditem.getCodesetid())){
						subitem.put("codesetid",fielditem.getCodesetid());
						//代码型指标关联的联动指标
						subitem.put("relation_field",itemid);
						subitem.put("type",UploadContant.type_select);
						int lay_num=this.excelLayoutDao.getLayerByCodesetid(fielditem.getCodesetid());
						subitem.put("lay", lay_num);
						if(subRelationMap.containsKey(itemid)){
							//需要关联模板的elementid`relationid
							LazyDynaBean subfieldBean = subRelationMap.get(itemid);
							subitem.put("linked_child_elementid_rationid",subfieldBean.get("childRelationField")==null?"":subfieldBean.get("childRelationField"));
							subitem.put("linked_parent_elementid_rationid",subfieldBean.get("fatherRelationField")==null?"":subfieldBean.get("fatherRelationField"));
						}
						//lay>1 选择任意一级选项
						subitem.put("checkStrictly", false);
				        if("UM".equalsIgnoreCase(fielditem.getCodesetid())||"UN".equalsIgnoreCase(fielditem.getCodesetid())){
				        	subitem.put("checkStrictly", true);
				        }else{
				        	//获取末端代码：
				        	subitem.put("checkStrictly", this.excelLayoutDao.getLeafCode(fielditem.getCodesetid()));
				        }

					}else{
						subitem.put("codesetid","");
						subitem.put("relation_field", "");
					}
			        //
			        subitem.put("hidden",false);
					if(setBo.isYneed())
					{
						subitem.put("required",true);
					}else{
						subitem.put("required",false);
					}
					if(setBo.getChgstate()==2){
						subitem.put("readonly",false);
					}else{
						subitem.put("readonly",true);
					}
					if("true".equalsIgnoreCase(itemFiled.getHis_readonly())){
						subitem.put("his_readonly",true);
					}else{
						subitem.put("his_readonly",false);
					}
					String va=itemFiled.getValign();
					String[] valign=getHValign(Integer.parseInt(va));
			        String hAlign= valign[1];
					subitem.put("valign", hAlign);
					//子集指标添加最大长度
					subitem.put("maxlength", fielditem.getItemlength());
					String a=itemFiled.getAlign();
					if(("N").equalsIgnoreCase(fielditem.getItemtype())){
						String[] align=getHValign(Integer.parseInt(a));
						subitem.put("align", align[0]);
						int dec=fielditem.getDecimalwidth();
		                if(Integer.valueOf(itemFiled.getSlop())<fielditem.getDecimalwidth()) {
		                	dec = Integer.valueOf(itemFiled.getSlop());
		                }
						String format_pattern="*";//表示整数
						if(dec>0){
							format_pattern+=".";
							for(int d=0;d<dec;d++){
								format_pattern+="?";
							}
						}
						// *.* 表示浮点型  *.??? 表示3位小数   * 表示整数    空表示非数值型输入框
						subitem.put("number_format",format_pattern);
						//数值型 formattype:'number'
						subitem.put("formattype","number");
					}else{
						//除数值全部居左
						subitem.put("align", "left");
					}

					subitem.put("default_value",itemFiled.getDefaultvalue());
					subitem.put("fixed", false);
//					subitem.put("placeholder", "请输入"+itemdesc);
//					//0,1,2   无，看，写   默认为无
//					subitem.put("postil_flag", "0");
//					subitem.put("postil_msg", "");
					subitem.put("width",itemFiled.getTitleWidth());
					if (("D".equals(fielditem.getItemtype()))){
						if(fielditem.getItemlength()>10){ //日期格式支持小时：分钟
							subitem.put("format",itemFiled.getSlop());
							subitem.put("type",UploadContant.type_datePicker);
						}
						else{
							subitem.put("format",itemFiled.getSlop());
							subitem.put("type",UploadContant.type_datePicker);
						}

						//后台接受日期的格式
						String value_format="yyyy-MM-dd";
						switch(fielditem.getItemlength()){
						case 16:
							value_format="yyyy-MM-dd HH:mm";
							break;
						case 18:
							value_format="yyyy-MM-dd HH:mm:ss";
							break;
						default:
							value_format="yyyy-MM-dd";
							break;
						}
						subitem.put("value_format", value_format);
					}
					/**添加该节点*/
					subitems.add(subitem);
				}
				text.put("columns", subitems);
			}
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 功能：获取当前子集buttons
		 * "[{button_id: 'b1',button_desc: '新增',function_id: 'XXXXXXXXX',button_type: 'add'}
		 * ,{button_id: 'b2',button_desc: '删除',function_id: 'XXXXXXXXX',button_type: 'del'}]"
		 *
		 * @param setBo
		 * @return
		 */
		private String getSubSetButtons(TemplateSet setBo) {
			JSONArray btnArr=new JSONArray();

			//button按钮设定
			//变化前子集 只保留刷新按钮
			//变化后子集 显示所有子集按钮
			boolean btnPre=false;
			if(2==setBo.getChgstate()){
				btnPre=true;
			}

			//新增
			JSONObject btn=new JSONObject();
			if(btnPre){
				btn.put("button_id", "b1");
				btn.put("button_desc", "新增");
				//暂定 ZC00006310
				btn.put("function_id", "ZC00006310");
				btn.put("button_type", "add");
				btnArr.add(btn);
				//插入
				btn=new JSONObject();
				btn.put("button_id", "b2");
				btn.put("button_desc", "插入");
				btn.put("function_id", "ZC00006311");
				btn.put("button_type", "insert");
				btnArr.add(btn);

				//删除
				btn=new JSONObject();
				btn.put("button_id", "b3");
				btn.put("button_desc", "删除");
				btn.put("function_id", "ZC00006312");
				btn.put("button_type", "delete");
				btnArr.add(btn);

				//导入
				btn=new JSONObject();
				btn.put("button_id", "b4");
				btn.put("button_desc", "导入");
				btn.put("function_id", "ZC00006306");
				btn.put("button_type", "import");
				btnArr.add(btn);

				//置顶
				btn=new JSONObject();
				btn.put("button_id", "b5");
				btn.put("button_desc", "置顶");
				btn.put("function_id", "ZC00006313");
				btn.put("button_type", "top");
				btnArr.add(btn);
			}


			return btnArr.toString();
		}
		/**
		 * 创建输出框INPUT
		 * @param td
		 * cellComment：Excel批注信息
		 */
		private void createInputEditor(JSONArray td,TemplateSet setBo,String cellComment,Map locationMap) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			int dec=0;

			String itemmemo="";
			if(fielditem==null){
				//临时变量
				text.put("maxlength", "100");
				dec=setBo.getDisformat();
				//用于标识临时变量
				text.put("isVar", setBo.isSpecialItem()?false:true);
				//设置指标解释
		        text.put("itemmemo", itemmemo);
			}else{
				text.put("maxlength", fielditem.getItemlength());
				dec=fielditem.getDecimalwidth();
                if(setBo.getDisformat()<fielditem.getDecimalwidth()) {
                	dec = setBo.getDisformat();
                }
				//用于标识临时变量
				text.put("isVar", false);

				itemmemo=fielditem.getExplain();
				if(itemmemo==null)
					itemmemo="";
				itemmemo = itemmemo.replaceAll("\r\n", "\n");
		        itemmemo = itemmemo.replaceAll("\n", "<br>");
		        if("<br>".equals(itemmemo.trim()))
		            itemmemo="";
		        //设置指标解释
		        text.put("itemmemo", itemmemo);
			}
			//添加Excel批注
			text.put("cellComment", cellComment);
	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }

			//如果是代码选项
			if(!"0".equals(setBo.getCodeid())){
				createSelectEditor(td, setBo,cellComment,locationMap);
				return;
			}
			if(("N").equalsIgnoreCase(setBo.getField_type())){

				String format_pattern="*";//表示整数
				if(dec>0){
					format_pattern+=".";
					for(int d=0;d<dec;d++){
						format_pattern+="?";
					}
				}
				// *.* 表示浮点型  *.??? 表示3位小数   * 表示整数    空表示非数值型输入框
				text.put("number_format",format_pattern);
				//数值型 formattype:'number'
				text.put("formattype","number");
			}else{
				text.put("number_format","");
				if(field_name.equalsIgnoreCase(this.idCardFielditem)){
					//身份证号指标 标识。
					text.put("formattype","idCard");
				}
			}

			text.put("type", UploadContant.type_input);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边

	        text.put("minlength", 0);
	        String[] align=getHValign(setBo.getAlign());
	        String hAlign= align[0];
	        text.put("align", hAlign);
			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}

			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			/***
			 * 是否显示输入数字 默认false
			 */
			text.put("showWordLimit",false);
			//输入框占位文本
			text.put("placeholder", "");
			//是否禁用
			text.put("disabled", false);
			//是否可以清空选项
			text.put("clearable", true);
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");

			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建富文本编辑器  类似html编辑器
		 * @param td
		 */
		private void createEditorEditor(JSONArray td,TemplateSet setBo,String cellComment,Map locationMap) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_editor);
			setElementPublicProperty(text,setBo);

			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			//添加指标解释
			String itemmemo=fielditem.getExplain();
			if(itemmemo==null)
				itemmemo="";
			itemmemo = itemmemo.replaceAll("\r\n", "\n");
	        itemmemo = itemmemo.replaceAll("\n", "<br>");
	        if("<br>".equals(itemmemo.trim()))
	            itemmemo="";
	        //设置指标解释
	        text.put("itemmemo", itemmemo);
	        //添加Excel批注
	        text.put("cellComment", cellComment);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", hz);
	        text.put("label_align", "center");//默认放置左边
//			if(setBo.isYneed())
//			{
//				text.put("required",true);
//			}else{
//				text.put("required",false);
//			}
	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }

			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");

			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建Link文字链接
		 * @param td
		 */
		private void createLinkEditor(JSONArray td,TemplateSet setBo) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_link);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //是否下划线
	        text.put("underline", false);
	        //输入框显示的值
	        text.put("value", "");
	        //原生 href 属性
	        text.put("href", "");
	        /**
	         * 图标类名(可不传，文字前面的图片，图片必须为图片库中如：el-icon-edit)
	         */
	        text.put("icon","");
			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否禁用
			text.put("disabled", false);
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}



		/**
		 * 创建Radio 输入框
		 * @param td
		 */
		private void createRadioEditor(JSONArray td,TemplateSet setBo) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_radio);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        text.put("show_type", UploadContant.radio_show_type_normal);
	        String codes = getDMInfo(fielditem.getCodesetid());
	        text.put("radio_values", codes);
	        text.put("value", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 根据代码号，获取json字符串形式的 值，value
		 * 如：
		 * {
		 * 'bj':'北京',
		 * 'sh':'上海',
		 * 'sz':'深圳'
		 * }
		 * @param codesetid
		 * @return
		 */
		private String getDMInfo(String codesetid) {
			if(StringUtils.isEmpty(codesetid)||"0".equals(codesetid)){
				return "";
			}
	        ArrayList<CodeItem> codeitems = AdminCode.getCodeItemList(codesetid);
	        JSONObject codes=new JSONObject();
	        for(int k=0;k<codeitems.size();k++){
	        	CodeItem c=codeitems.get(k);
	        	codes.put(c.getCodeitem(), c.getCodename());
	        }
			return codes.toString();
		}

		/**
		 * 创建Checkbox多选框
		 * @param td
		 */
		private void createCheckboxEditor(JSONArray td,TemplateSet setBo) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_checkbox);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
	        String codes = getDMInfo(fielditem.getCodesetid());
	        text.put("checkbox_values", codes);

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //勾选的最小数量为0，最大数量为10
	        text.put("min", 0);
	        text.put("max", 100);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建Select选择器
		 * @param td
		 */
		private void createSelectEditor(JSONArray td,TemplateSet setBo,String cellComment,Map locationMap) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			JSONObject text=new JSONObject();
			String itemmemo="";
			if(fielditem==null){
				//用于标识临时变量
				text.put("isVar", setBo.isSpecialItem()?false:true);
				//设置指标解释
		        text.put("itemmemo", itemmemo);
			}else{
				//用于标识临时变量
				text.put("isVar", false);
				itemmemo=fielditem.getExplain();
				if(itemmemo==null)
					itemmemo="";
				itemmemo = itemmemo.replaceAll("\r\n", "\n");
		        itemmemo = itemmemo.replaceAll("\n", "<br>");
		        if("<br>".equals(itemmemo.trim()))
		            itemmemo="";
		        //设置指标解释
		        text.put("itemmemo", itemmemo);
			}

	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }

			//添加Excel批注
			text.put("cellComment", cellComment);
			text.put("type", UploadContant.type_select);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        int lay_num=this.excelLayoutDao.getLayerByCodesetid(setBo.getCodeid());
	        text.put("lay", lay_num);

	        text.put("codesetid",setBo.getCodeid());
	        //输入框仅显示最后一级
	        text.put("show_all_levels", false);
	        //lay>1 选择任意一级选项
	        text.put("checkStrictly", false);
	        if("UM".equalsIgnoreCase(setBo.getCodeid())||"UN".equalsIgnoreCase(setBo.getCodeid())){
	        	text.put("checkStrictly", true);
	        }else{
	        	//获取末端代码：
	        	text.put("checkStrictly", this.excelLayoutDao.getLeafCode(setBo.getCodeid()));
	        }
	        text.put("placeholder", "");
	        //是否多选
	        text.put("multiple", false);
	        //是否可搜索
	        text.put("filterable", true);
	        //搜索条件无匹配时显示的文字
	        text.put("noMatchText", "无匹配数据");
	        //选项为空时显示的文字
	        text.put("noDataText", "无数据");
	        //是否可以清空选项
	        text.put("clearable", true);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			//需要关联模板的elementid`relationid
			if(relationMap.containsKey(setBo.getUniqueId())){
				LazyDynaBean fieldBean = relationMap.get(setBo.getUniqueId());
				text.put("linked_child_elementid_rationid",fieldBean.get("childRelationField")==null?"":fieldBean.get("childRelationField"));
				text.put("linked_parent_elementid_rationid",fieldBean.get("fatherRelationField")==null?"":fieldBean.get("fatherRelationField"));
			}
			//根据 relation
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 创建Select选择器
		 * @param td
		 */
		private void createTimePickerEditor(JSONArray td,TemplateSet setBo,String cellComment) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_dateTimePicker);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");

	        // 两种展现形式  1.单个 normal 2 区间 range  人事异动 不存在区间
	        text.put("show_type", "normal");

	        // 两种展现形式  1.间隔 space 2.任意 anyTime
	        text.put("time_show_type", "anyTime");
	        //输入框显示的值
	        text.put("value", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //show_type==1 输入框占位文本
	        text.put("placeholder", "");
	        //show_type==2 开始输入框占位文本
	        text.put("start_placeholder", "");
	        //show_type==2  结束输入框占位文本
	        text.put("end_placeholder", "");
	        //time_show_type==1  开始时间
	        text.put("start", "09:00");
	      //time_show_type==1  结束时间
	        text.put("end", "18:00");
	      //time_show_type==1  间隔时间
	        text.put("step", "00:30");
	        //time_show_type==2 可选时间段，例如'18:30:00 - 20:30:00'或者传入数组['09:30:00 - 12:00:00', '14:30:00 - 18:30:00']
	        text.put("selectableRange", "");
	        //time_show_type==2 时间格式化  小时：HH，  分：mm，  秒：ss， AM/PM：A
	        text.put("format", "");
	        //选择范围时的分隔符
	        text.put("range_separator", "-");
	        //自定义头部图标的类名
	        text.put("prefix_icon", "el-icon-time");
	        //搜索条件无匹配时显示的文字
	        text.put("clear_icon", "el-icon-circle-close");
	        //是否可以清空选项
	        text.put("clearable", true);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
	        if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
	        text.put("editable", false);
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 创建Select选择器
		 * @param td
		 * @param locationMap
		 */
		private void createDatePickerEditor(JSONArray td,TemplateSet setBo,String cellComment, Map locationMap) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);

			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_datePicker);
			setElementPublicProperty(text,setBo);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");

			int disformat=setBo.getDisformat();
			text.put("format", disformat+"");
			//后台接受日期的格式
			String value_format="yyyy-MM-dd";
			int itemLen=0;
			if(fielditem==null){
				//判断当前指标是否时特殊指标
				if(setBo.isSpecialItem()){
					//用于标识临时变量
					text.put("isVar", false);
				}else{
					RecordVo varVo=setBo.getVarVo();
					itemLen=varVo.getInt("fldlen");
					//用于标识临时变量
					text.put("isVar", true);
				}
				//设置指标解释
		        text.put("itemmemo", "");
			}else{
				itemLen=fielditem.getItemlength();
				text.put("isVar", false);
				//添加指标解释
				String itemmemo=fielditem.getExplain();
				if(itemmemo==null)
					itemmemo="";
				itemmemo = itemmemo.replaceAll("\r\n", "\n");
		        itemmemo = itemmemo.replaceAll("\n", "<br>");
		        if("<br>".equals(itemmemo.trim()))
		            itemmemo="";
		        //设置指标解释
		        text.put("itemmemo", itemmemo);
			}
			//添加Excel批注
			text.put("cellComment", cellComment);
			switch(itemLen){
			case 16:
				value_format="yyyy-MM-dd HH:mm";
				break;
			case 18:
				value_format="yyyy-MM-dd HH:mm:ss";
				break;
			default:
				value_format="yyyy-MM-dd";
				break;
			}
			text.put("value_format", value_format);
	        /**
	         * 两种展现形式
	         * 1.year 年
	         * 2.month 月
	         * 3.date 日期
	         * 4.week 周
	         * 5.datetime 日期时间
	         * 6.datetimeRange 日期时间范围
	         * 7.dateRange 日期范围
	         * 8. monthRange 月范围
	         */
	        if(itemLen>10){ //日期格式支持小时：分钟
	        	text.put("show_type", "datetime");
//	        	text.put("format", "yyyy-MM-dd HH:mm");
	        }
	        else{
	        	text.put("show_type", "date");
//	        	text.put("format", "yyyy-MM-dd");
	        }
	        //输入框显示的值
	        text.put("value", "");

	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //show_type==1 输入框占位文本
	        text.put("placeholder", "");
	        //show_type==2 开始输入框占位文本
	        text.put("start_placeholder", "");
	        //show_type==2  结束输入框占位文本
	        text.put("end_placeholder", "");
	        //time_show_type==1  开始时间
	        text.put("align", "left");
	      //选择范围时的分隔符
	        text.put("range_separator", "-");
	      //date_show_type=week   周起始日
	        text.put("firstDayOfWeek", "7");
	        //自定义头部图标的类名
	        text.put("prefix_icon", "el-icon-time");
	        //搜索条件无匹配时显示的文字
	        text.put("clear_icon", "el-icon-circle-close");
	        //是否可以清空选项
	        text.put("clearable", true);
	        //是否禁用
			text.put("disabled", false);
	        if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
	        if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
	        text.put("editable", false);
			//是否可见
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			//syl0 批注里没有
			text.put("postil_msg", "");
			text.put("postil_username", "");
	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }
			/**添加该节点*/
			td.add(text);
		}
		/**
		 * 创建Text 文本框
		 * @param td
		 */
		private void createTextEditor(JSONArray td,HashMap map,Map locationMap) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_text);
			text.put("element_id",getUniqueOne());//单元格唯一键值

	        String hz=(String) map.get("hz");
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
			text.put("content", hz);
	        text.put("label", "");
	        text.put("label_align", "");
	        text.put("align", getHValign((int)map.get("align"))[0]);
			//是否清空
			text.put("hidden", false);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			text.put("postil_msg", "");
			text.put("postil_username", "");
	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }
			/**添加该节点*/
			td.add(text);
		}


		/**
		 * 创建文本输入框
		 * @param td
		 * @param locationMap
		 */
		private void createTextAreaEditor(JSONArray td,TemplateSet setBo,String cellComment, Map locationMap) {
			String field_name=setBo.getField_name().toLowerCase();
			FieldItem fielditem=DataDictionary.getFieldItem(field_name);
			if(fielditem==null) {
				fielditem=new FieldItem();
				fielditem.setItemid(field_name);
				fielditem.setInputtype(0);
			}
			//指标的文本编辑类型(大文本) //0普通编辑器 1 富文本编辑器
			if(1==fielditem.getInputtype()){
				//走富文本编辑器
				createEditorEditor(td, setBo,cellComment,locationMap);
				return;
			}
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_textarea);
			setElementPublicProperty(text,setBo);

	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }

			//添加指标解释
			String itemmemo=fielditem.getExplain();
			if(itemmemo==null)
				itemmemo="";
			itemmemo = itemmemo.replaceAll("\r\n", "\n");
	        itemmemo = itemmemo.replaceAll("\n", "<br>");
	        if("<br>".equals(itemmemo.trim()))
	            itemmemo="";
	        //设置指标解释
	        text.put("itemmemo", itemmemo);
	        //添加Excel批注
	        text.put("cellComment", cellComment);

	        String hz=setBo.getHz();
	        hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
			text.put("value", "");
	        text.put("label", hz);
	        text.put("label_align", "left");//默认放置左边
	        //大文本字符长度为10的话，传值为空，不限制长度
	        text.put("maxlength", (fielditem.getItemlength()==10||fielditem.getItemlength()==0)?"":fielditem.getItemlength());
	        text.put("minlength", 0);
	        /***
	         * 是否显示输入数字 默认false
	         */
	        text.put("showWordLimit",false);
	        //输入框占位文本
	        text.put("placeholder", "");
	        //输入框行数
			text.put("rows", UploadContant.textarea_rows);
	        //自适应内容高度，可传入对象，如：{ minRows: 2, maxRows: 6 }
			text.put("autosize", false);
			//是否禁用
			text.put("disabled", false);

			if(setBo.getChgstate()==2){
				text.put("readonly",false);
			}else{
				text.put("readonly",true);
			}
			//是否可以清空选项
			text.put("clearable", true);
			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//是否可见
			text.put("hidden", false);

			String[] align=getHValign(setBo.getAlign());
	        String hAlign= align[0];
	        text.put("align", hAlign);

	      //0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");
			/**syl0 是否有批注 没有这些内容*/
			text.put("postil_msg", "");
			text.put("postil_username", "");
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 照片
		 * @param td
		 */
		private void createImageEditor(JSONArray td,TemplateSet setBo,String cellComment) {
//			Element text=new Element("input");
//			text.setAttribute("type","image");
//			text.setAttribute("src","/images/photo.jpg");
//			setElementPublicProperty(text,setBo);
//			StringBuffer style=new StringBuffer();
//			style.append("height:");
//			style.append(setBo.getRheight()-7);
//			style.append("px;");
//			style.append("width:");
//			style.append(setBo.getRwidth()-5);
//			style.append("px;");
//			text.setAttribute("style",style.toString());
//			text.setAttribute("extra","photo");
			/**业务类型为0时，才需要上传照片*/


		}
		/**
		 * 附件
		 * @param td
		 */
		private void createAttachmentEditor(JSONArray td, SubField subfile,TemplateSet setBo,String cellComment) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_upload);
			text.put("label", "附件");
//			String hz=subfile.getTitle();
//			hz=hz.replaceAll("\\{", "");
//			hz=hz.replaceAll("\\}", "");
//			hz=hz.replaceAll("`", "");
//			if(StringUtils.isNotEmpty(hz)){
//				text.put("label", hz);
//			}
			text.put("label_align", "left");
			text.put("hidden", false);
			text.put("align", "center");
			text.put("element_id",getUniqueOne());//单元格唯一键值
			text.put("relation_id",setBo.getSetname()+"attach_"+setBo.getChgstate());//数据库中的字段名称
			String itemid=subfile.getFieldname();
			if(StringUtils.isEmpty(itemid))
				text.put("column_id", "attach");
			else
				text.put("column_id", itemid);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");//是否有批注
			text.put("postil_msg", "");  //批注信息

			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//附件61063 ZCSB：（Oracle库），批次管理，岗位聘用批次，流程设计表单授权中，附件无法设置写权限，见附件。
			text.put("readonly",false);
			/**
			 * value:[{
						   "real_file_name":"xxxxx.pdf", //存储文件名
						   "file_name":"xxxxx.pdf", //用户上传的文件名
						   "file_path":"subdomain\template_201\T386\T427", //文件路径
					     },。。。]
			 */
			/**添加该节点*/
			td.add(text);
		}

		/**
		 * 附件
		 * @param td
		 */
		private void createAttachmentEditor(JSONArray td, TemplateSet setBo,String cellComment,Map locationMap) {
			JSONObject text=new JSONObject();
			text.put("type", UploadContant.type_upload);
			text.put("label", "附件");
			String hz=setBo.getHz();
			hz=hz.replaceAll("\\{", "");
			hz=hz.replaceAll("\\}", "");
			hz=hz.replaceAll("`", "");
			if(StringUtils.isNotEmpty(hz)){
				text.put("label", hz);
			}
	        if(locationMap!=null){
	        	//循环转换
	        	Iterator it =locationMap.entrySet().iterator();
	        	while (it.hasNext()) {
	        		Entry<String, Object> entry = (Entry<String, Object>) it.next();
	        		text.put(entry.getKey(), entry.getValue());
	        	}
	        }
			//添加Excel批注
			text.put("cellComment", cellComment);
			text.put("label_align", "left");
			text.put("hidden", false);
			text.put("align", "center");
			setElementPublicProperty(text,setBo);
			//0,1,2   无，看，写   默认为无
			text.put("postil_flag", "0");//是否有批注
			text.put("postil_msg", "");  //批注信息

			if(setBo.isYneed())
			{
				text.put("required",true);
			}else{
				text.put("required",false);
			}
			//附件61063 ZCSB：（Oracle库），批次管理，岗位聘用批次，流程设计表单授权中，附件无法设置写权限，见附件。
			text.put("readonly",false);
			/**
			 * value:[{
						   "real_file_name":"xxxxx.pdf", //存储文件名
						   "file_name":"xxxxx.pdf", //用户上传的文件名
						   "file_path":"subdomain\template_201\T386\T427", //文件路径
					     },。。。]
			 */
			/**添加该节点*/
			td.add(text);
		}
		/**
		* @Title: setElementPublicProperty
		* @Description: 设置元素的公用属性，id field_name 用于前台显示数据及修改数据
		* @param @param td
		* @return void
		*/
		private void setElementPublicProperty(JSONObject text,TemplateSet setBo) {
			text.put("element_id",setBo.getTableFieldName());//单元格唯一键值
			text.put("relation_id",setBo.getTableFieldName());//数据库中的字段名称
//			text.put("fieldsetid","dataset_"+setBo.getPageId());//数据结构对象的Id
//			text.put("recordsetid","dataset_"+setBo.getPageId());//数据对象的Id
		}

		/**
		 * 排列方式
		 * @param ali
		 * @return
		 */
		protected String[] getHValign(int ali) {
			String[] align = new String[2];
			switch(ali)
			{
			case 0:
				align[0] = "left";
				align[1] = "top";
				break;
			case 1:
				align[0] = "center";
				align[1] = "top";
				break;
			case 2:
				align[0] = "right";
				align[1] = "top";
				break;
			case 3:
				align[0] = "left";
				align[1] = "bottom";
				break;
			case 4:
				align[0] = "center";
				align[1] = "bottom";
				break;
			case 5:
				align[0] = "right";
				align[1] = "bottom";
				break;
			case 6:
				align[0] = "left";
				align[1] = "middle";
				break;
			case 7:
				align[0] = "center";
				align[1] = "middle";
				break;
			case 8:
				align[0] = "right";
				align[1] = "middle";
				break;
			}
			return align;
		}

		/**
		 * 得到主集中存放身份证号的字段
		 * @return
		 */
		private String getIdCardField()
		{
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String field=sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
			return field;
		}



		/**
		 * 获取表格单元格Cell内容
		 * @param cell
		 * @return
		 */
		private static String getCellValue(Cell cell) {
			String result = new String();
			if(cell==null){
				return result;
			}
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:// 数字类型
				if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
					SimpleDateFormat sdf = null;
					if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
						sdf = new SimpleDateFormat("HH:mm");
					} else {// 日期
						sdf = new SimpleDateFormat("yyyy-MM-dd");
					}
					Date date = cell.getDateCellValue();
					result = sdf.format(date);
				} else if (cell.getCellStyle().getDataFormat() == 58) {
					// 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = DateUtil
							.getJavaDate(value);
					result = sdf.format(date);
				} else {
					double value = cell.getNumericCellValue();
					CellStyle style = cell.getCellStyle();
					DecimalFormat format = new DecimalFormat();
					String temp = style.getDataFormatString();
	// 单元格设置成常规
					if ("General".equals(temp)) {
						format.applyPattern("#");
					}
					result = format.format(value);
				}
				break;
			case Cell.CELL_TYPE_STRING:// String类型
				result = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BLANK:
				result = "";
				break;
			default:
				result = "";
				break;
			}
			return result;
		}

}
