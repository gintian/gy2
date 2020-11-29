package com.hjsj.hrms.module.kq.holiday.transaction;

import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.module.kq.util.KqUtil;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportLeaveTimeTypeDataTrans extends IBusiness{
    private int index = 0;
    private String errorMessage = ""; 
    private HashMap<String, String> keyMap = new HashMap<String, String>();
    private ArrayList<String> itemList = new ArrayList<String>(); 
    @Override
    public void execute() throws GeneralException {
        // TODO Auto-generated method stub
        try {
        	// 上传组件更新参数需解密
            String path = (String)this.getFormHM().get("path");
            path = PubFunc.decrypt(path);
            String fileName = (String)this.getFormHM().get("filename");
            fileName = PubFunc.decrypt(fileName);
            path = path + fileName;
            
            File uploadeFile = new File(path);
            
            if (!FileTypeUtil.isFileTypeEqual(uploadeFile)) {
                this.errorMessage = ResourceFactory.getProperty("error.common.upload.invalid");
                return;
            }
            
            StringBuffer msgJson = new StringBuffer();
            ArrayList<HashMap<String, String>> mapsList = readExcel(uploadeFile, msgJson);
            
            if(msgJson.length() > 0) {
            	msgJson.insert(0, "[");
            	if(msgJson.toString().endsWith(","))
            		msgJson.setLength(msgJson.length() - 1);
            	
            	msgJson.append("]");
            }
            
            this.getFormHM().put("mapsList", mapsList);
            this.getFormHM().put("itemList", this.itemList);
            this.getFormHM().put("msgJson", msgJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.getFormHM().put("errorMessage", this.errorMessage);
        }
    }

    /**
     * 读取excel数据放入集合对象
     * 
     * @param file
     * @param dao 
     * @param hashMap
     * @return
     * @throws Exception
     */
    private ArrayList<HashMap<String, String>> readExcel(File file, StringBuffer msgJson){

        ArrayList<HashMap<String, String>> mapsList = new ArrayList<HashMap<String, String>>();
        Workbook owb = null;
        Sheet osheet = null;
        InputStream ism = null;
        RowSet rs = null;
        try {
            ism = new FileInputStream(file);
            owb = WorkbookFactory.create(ism);
            osheet = owb.getSheetAt(0);
            Row orow = osheet.getRow(0);// 第一行标题
            if (orow == null) {
                this.errorMessage = ResourceFactory.getProperty("error.common.upload.invalid");
                return mapsList;
            }
            
            int cols = orow.getPhysicalNumberOfCells();// 总hang数
            int rows = osheet.getPhysicalNumberOfRows();// 总行数
            ArrayList<String> keyList = new ArrayList<String>();
            for (int c = 0; c < cols; c++) {// 遍历列
                String itemid = "";
                Cell cell = orow.getCell(c);
                if (cell != null) {
                    itemid = cell.getCellComment().getString().getString().toLowerCase();
                    String itemDesc = cell.getStringCellValue();
                    this.keyMap.put(itemid, itemDesc);
                    this.itemList.add(itemid);
                }
            }
            
            HashMap<String, String> map = KqPrivBo.getKqParameter(this.frameconn);
            String nbases = map.get("nbase");
            if(StringUtils.isEmpty(nbases)) {
            	this.errorMessage = ResourceFactory.getProperty("人员库为空！请确认考勤人员库是否已设置并且已授权给您！");
                return mapsList;
            }
            
            String[] dbnames = nbases.split(",");
            	
            ContentDAO dao = new ContentDAO(this.frameconn);
            HolidayBo bo = new HolidayBo(this.frameconn, this.userView);
            String value = "";
            String gNo = bo.getGNo();
            cols = this.itemList.size();
            for (int j = 1; j < rows; j++) {
                orow = osheet.getRow(j);
                if (orow == null)
                    continue;
                
                float q3305 = 0;
                float q3307 = 0;
                float q3309 = 0;
                String jobNumber = "";
                String nbase = "";
                String q3303 = "";
                // key=指标itemid value=值
                HashMap<String, String> valueMap = new HashMap<String, String>();
                for (int n = 0; n < cols; n++) {
                    String key = (String) itemList.get(n);
                    FieldItem item = DataDictionary.getFieldItem(key, "Q33");
                    if("jobNumber".equalsIgnoreCase(key)) {
                    	item = new FieldItem();
                    	item.setItemid(key);
                    	item.setItemtype("A");
                    	item.setCodesetid("0");
                    }
                    	
                    if (item == null)
                        continue;
                    
                    Cell cell = orow.getCell(n);
                    if (cell == null)
                        continue;
                    
                    String itemid = item.getItemid();
                    value = checkData(item, cell, msgJson, j);
                    // 时长数值类型由于模板的单位是小时 保存到库中以分钟为单位
                    if("q3305".equalsIgnoreCase(itemid)) {
                    	q3305 = Float.valueOf(value);
                    	value = String.valueOf(q3305*60);
                    }
                    else if("q3307".equalsIgnoreCase(itemid)) {
                    	q3307 = Float.valueOf(value);
                    	value = String.valueOf(q3307*60);                    	
                    }
                    else if("q3309".equalsIgnoreCase(itemid)) {
                    	q3309 = Float.valueOf(value);
                    	value = String.valueOf(q3309*60);                    	
                    }
                    else if("jobNumber".equalsIgnoreCase(itemid))
                        jobNumber = value;
                    else if("nbase".equalsIgnoreCase(itemid))
                        nbase  = value;
                    else if("q3303".equalsIgnoreCase(itemid))
                    	q3303  = value;
                    
                    if(!"jobNumber".equalsIgnoreCase(key))
                    	valueMap.put(key, value);
                    
                }
                // 如果工号为null 并且 整行数据也为null，那么视为记录结束，直接break
                if(StringUtils.isEmpty(jobNumber) && valueMap.isEmpty())
                	break;
                // 若工号为null 则给出提示信息
                if(StringUtils.isEmpty(jobNumber)) {
                	index++;
            		msgJson.append("{message:'");
            		msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中工号为空，数据不允许导入！");
            		msgJson.append("'},");
            		continue;
                }
                //校验工号对应的人员是否是多个，如果是数据不导入
                StringBuffer sql = new StringBuffer();
                sql.append("select count(1) count from (");
                ArrayList<String> valueList = new ArrayList<String>();
                for(int a = 0; a < dbnames.length; a++) {
                	String dbname = dbnames[a];
                	if(a > 0)
                		sql.append(" union ");
                	
                	sql.append("select 1 as person from ");
                	sql.append(dbname + "a01");
                	sql.append(" where " + gNo + " = ?");
                	valueList.add(jobNumber);
                }
                
                sql.append(") a");
                
                rs = dao.search(sql.toString(), valueList);
                if(rs.next()) {
                	int num = rs.getInt("count");
                	if(num > 1) {
                		index++;
                		msgJson.append("{message:'");
                		msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中工号（" 
                				+ jobNumber + "）对应的人员有两个或两个以上，数据不允许导入！");
                		msgJson.append("'},");
                		continue;
                	}
                }
                
                valueList.clear();
                valueList.add(nbase);
                String nbaseid = "";
                rs = dao.search("select pre from DBName where DBName=?", valueList);
                
                if(rs.next()) {
                	nbaseid = rs.getString("pre");
                	valueMap.put("nbase", nbaseid);
                } else {
                	index++;
                	msgJson.append("{message:'");
                	msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中的人员库（" 
                		+ nbase + "）不存在，数据不允许导入！");
                	msgJson.append("'},");
            		continue;
                }
                
                String a0100 = "";
                valueList.clear();
                valueList.add(jobNumber);
                rs = dao.search("select A0100,A0101,B0110,E0122,E01A1 from " + nbaseid + "A01 where " + gNo + " = ?", valueList);
                if(rs.next()) {
                	String a0101 = rs.getString("A0101");
                	if(!a0101.equalsIgnoreCase(valueMap.get("a0101"))) {
                		index++;
                		msgJson.append("{message:'");
                		msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中的"
                				+ keyMap.get("a0101") + "与系统中的不同，数据不允许导入！");
                		msgJson.append("'},");
                    		continue;
                	}
                	
                	valueMap.put("b0110", rs.getString("B0110"));
                	valueMap.put("e0122", rs.getString("E0122"));
                	valueMap.put("e01a1", rs.getString("E01A1"));
                	a0100 = rs.getString("A0100");
                	valueMap.put("a0100", a0100);
                } else {
                	index++;
                	msgJson.append("{message:'");
                	msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中的人员在人员库（" 
                    		+ nbase + "）中不存在，数据不允许导入！");
                	msgJson.append("'},");
                		continue;
                }
                //检测导入的数据在数据库中是否有同一日期的数据
                valueList.clear();
                valueList.add(a0100);
                valueList.add(nbaseid);
                valueList.add(q3303);
                StringBuffer sqlStr = new StringBuffer();
                sqlStr.append("select 1 from q33");
                sqlStr.append(" where a0100=?");
                sqlStr.append(" and nbase=?");
                sqlStr.append(" and q3303=?");
                rs = dao.search(sqlStr.toString(), valueList);
                if(rs.next()) {
                	index++;
                	msgJson.append("{message:'");
                	msgJson.append(index + ".&nbsp;第" + (j + 1) + "行在系统中已存在相同日期的数据，数据不允许导入！");
                	msgJson.append("'},");
                	continue;
                }
                
                //校验加班时长是否大于0，如果不大于0则整行数据不导入
                if(q3305 <= 0){
                	index++;
                	msgJson.append("{message:'");
                    msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中加班时长必须大于0，数据不允许导入！");
                    msgJson.append("'},");
                    continue;
                }
                //校验加班时长是否等于调休时长加可休时长，如果不等于则整行数据不导入
                if(q3305 != (q3307 + q3309)){
                	index++;
                	msgJson.append("{message:'");
                    msgJson.append(index + ".&nbsp;第" + (j + 1) + "行数据中加班时长不等于调休时长加可休时长，数据不允许导入！");
                    msgJson.append("'},");
                    continue;
                }
                
                mapsList.add(valueMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.errorMessage = ResourceFactory.getProperty("workbench.info.import.error.excel");
        } finally {
            PubFunc.closeIoResource(ism);
            PubFunc.closeIoResource(owb);
            PubFunc.closeDbObj(rs);
        }
        
        return mapsList;
    }
    /**
     * 校验导入的数据
     * @param item 对应的指标
     * @param cell 对应的单元格
     * @param json 提示信息
     * @param j 模板中的第几行
     * @return
     */
    public String checkData(FieldItem item, Cell cell, StringBuffer json, int j) {
        String value;
        DateFormat formater = new SimpleDateFormat("yyyy.MM.dd");
        if ("N".equals(item.getItemtype())) {
            value = String.valueOf(cell.getNumericCellValue());
            // 如果存在两个.则也提示为无效数值
            if ((value + " ").split("\\.").length > 2) {
            	index++;
            	json.append("{message:'");
                json.append(index + ".&nbsp;第" + (j + 1) + "行[" + this.keyMap.get(item.getItemid()) + "]为数值类型，而上传的值无效，上传的值为："
                        + value);
                json.append("'},");
                value = "```";
            } else {
                Pattern p = Pattern.compile("[+-]?[\\d.]*");
                Matcher m = p.matcher(value);
                if (!m.matches()) {
                	index++;
                	json.append("{message:'");
                    json.append(index + ".&nbsp;第" + (j + 1) + "行[" + this.keyMap.get(item.getItemid()) + "]为数值类型，而上传的值无效，上传的值为："
                            + value);
                    json.append("'},");
                    value = "```";
                } else {
                    if ("N".equals(item.getItemtype())) {
                        int dw = item.getDecimalwidth();
                        if (dw == 0) {
                            if (value.indexOf('.') != -1)
                                value = value.substring(0, value.indexOf('.'));
                        } else {
                            // 数值型指标长度限制去除
                            int il = item.getItemlength();
                            int intValueLength = 0;
                            if (value.indexOf('.') != -1) {
                                intValueLength = value.substring(0, value.indexOf('.')).length();
                                String dec = value.substring(value.indexOf('.') + 1);
                                if (dec.length() > dw)
                                    value = value.substring(0, value.indexOf('.') + dw + 1);
                            } else
                                intValueLength = value.length();
                            
                            if (intValueLength > il) {
                            	index++;
                            	json.append("{message:'");
                                json.append(index + ".&nbsp;第" + (j + 1) + "行"
                                        + this.keyMap.get(item.getItemid()) + "指标中值长度超过指标长度！");
                                json.append("'},");
                                value = "```";
                            }
                        }
                        
                        value = value.replaceAll("\\+", "");
                    }
                }
            }
        } else if ("D".equals(item.getItemtype())) {
            Date d = cell.getDateCellValue();
            try {
                value = formater.format(d);
            } catch (Exception e) {
            	index++;
            	json.append("{message:'");
                json.append(index + ".&nbsp;第" + (j + 1) + "行[" + this.keyMap.get(item.getItemid()) + "]指标为日期类型，而上传的值无效，上传的值为："
                        + cell.getStringCellValue());
                json.append("'},");
                
                value = "```";
                return value;
            }

            if (!"".equals(value)) {
                String tmp = KqUtil.checkdate(value);
                if ("false".equals(tmp)) {
                	index++;
                	json.append("{message:'");
                    json.append(index + ".&nbsp;第" + (j + 1) + "行[" + this.keyMap.get(item.getItemid()) + "]指标为日期类型，而上传的值无效，上传的值为："
                            + value);
                    json.append("'},");
                    
                    value = "```";
                } else 
                    value = tmp;
                
            }
        } 
        // q3303加班日期单独校验日期格式是否正确
        else if("q3303".equalsIgnoreCase(item.getItemid())) {
    		value = cell.getStringCellValue();
    		if (!"".equals(value)) {
                String tmp = KqUtil.checkdate(value);
                if ("false".equals(tmp)) {
                	index++;
                	json.append("{message:'");
                    json.append(index + ".&nbsp;第" + (j + 1) + "行[" + this.keyMap.get(item.getItemid()) + "]指标为日期类型，而上传的值无效，上传的值为："
                            + value);
                    json.append("'},");
                    
                    value = "```";
                } else 
                    value = tmp;
                
            }
    		value = value.replace("-", ".");
        } 
        else
        	value = StringUtils.isEmpty(cell.getStringCellValue()) ? "" : cell.getStringCellValue();

        return value;
    }
}
