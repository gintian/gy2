package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
*    
* 
* 类名称：ImportDemandZipTrans   
* 类描述： 导入压缩的zip招聘需求  
* 创建人：akuan   
* 创建时间：Aug 5, 2013 9:21:03 AM   
* 修改人：akuan   
* 修改时间：Aug 5, 2013 9:21:03 AM   
* 修改备注：   
* @version    
*
 */
public class ImportDemandZipTrans extends IBusiness {

    public void execute() throws GeneralException {
        InputStream inputStream = null;
        FormFile form_file = (FormFile) getFormHM().get("file");
        String name = form_file.getFileName();
        String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + name;
        Workbook workbook = null;
        Sheet sheet = null;
        OutputStream outputStream = null;
        ZipInputStream is = null;
        String n = "^-?\\d+$";//匹配整数
        String d = "^\\d{4}-(0\\d|1[0-2])-([0-2]\\d|3[01])( ([01]\\d|2[0-3])\\:[0-5]\\d\\:[0-5]\\d)?$"; //匹配时间
        try {
            /**安全平台改造,防止文件上传漏洞**/
            boolean isFileTypeEqual = FileTypeUtil.isFileTypeEqual(form_file);
            if (!isFileTypeEqual) {
                throw new GeneralException(ResourceFactory.getProperty("error.fileuploaderror"));
            }
            
            /*首先上传zip压缩包至服务器端*/
            inputStream = form_file.getInputStream();
            outputStream = new FileOutputStream(filePath);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            /*其次从服务器端获得zipfile对象解密并解析*/

            ZipFile zipFile = new ZipFile(filePath); // 根据路径取得需要解压的Zip文件   
            if (zipFile.isEncrypted()) { // 判断文件是否加码   
                zipFile.setPassword("hjsj2013"); // 密码为hjsj2013   
            }
            
            List fileHeaderList = zipFile.getFileHeaders();
            if (fileHeaderList == null || fileHeaderList.size() > 1) {
                throw new GeneralException("请用下载的zip压缩包来导入数据!");
            }
            
            for (int i = 0; i < 1 /*fileHeaderList.size()*/; i++) { //此处只用一个模板
                FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
                is = zipFile.getInputStream(fileHeader);
                try {
                    workbook = WorkbookFactory.create(is);
                } catch(Exception e) {
                    //if ("Your InputStream was neither an OLE2 stream, nor an OOXML stream".equalsIgnoreCase(e.getMessage()))
                        throw new GeneralException("请用下载的zip压缩包来导入数据!");
                    //else
                     //   throw e;
                }
                
                String sheetname = "";
                sheetname = workbook.getSheetName(0);
                if (!"招聘需求表HJSJ".equalsIgnoreCase(sheetname)) {
                    throw new GeneralException("请用下载的zip压缩包来导入数据!");
                }
                
                sheet = workbook.getSheet("招聘需求表HJSJ");
                Row row = sheet.getRow(0);//第一行
                if (row == null)
                    throw new GeneralException("请用下载的zip压缩包来导入数据!");
                
                int cols = row.getPhysicalNumberOfCells();//获得导入excel对应模板的列数
                int rows = sheet.getPhysicalNumberOfRows();//获得导入excel对应模板的行数
                if (cols < 1 || rows < 1)
                    throw new GeneralException("请用下载的zip压缩包来导入数据!");

                Cell cell = null;
                String value = "";//各列名
                String id = "";//表头各批注
                ArrayList idList = new ArrayList();//按列顺序的字段集合
                ArrayList contentList = new ArrayList();//导入数据内容  格式：contentList/map
                HashMap map = new HashMap();//各行 字段/值 集合
                for (short c = 0; c < cols; c++) {
                    cell = row.getCell(c);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_FORMULA:
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            double y = cell.getNumericCellValue();
                            value = Double.toString(y);
                            id = cell.getCellComment().getString().toString().toLowerCase();
                            break;
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            id = cell.getCellComment().getString().toString().toLowerCase();
                            break;
                        default:
                            value = cell.getStringCellValue();
                            id = cell.getCellComment().getString().toString().toLowerCase();
                        }
                        if (c == 0) {
                            if (!"z0301".equalsIgnoreCase(id))//用工申请序号 第一列是否为用工申请序号
                                throw new GeneralException("请用下载的zip压缩包来导入数据!");
                        }
                        idList.add(id);
                    }
                }

                /*遍历excel内容行*/

                for (short r = 1; r < rows; r++) {
                    row = sheet.getRow(r);
                    map = new HashMap();
                    for (short cc = 0; cc < cols; cc++) {
                        cell = row.getCell(cc);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_FORMULA:
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                //						    double y = cell.getNumericCellValue();
                                //						    value = Double.toString(y);
                                int y = (int) cell.getNumericCellValue();
                                value = String.valueOf(y);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            default:
                                value = cell.getStringCellValue();
                            }
                            map.put(idList.get(cc), value);
                        }

                    }

                    contentList.add(map);
                }

                /*以目标数据库为准 分析数据 插入数据*/
                ResultSet rs = null;
                ContentDAO dao = new ContentDAO(this.frameconn);
                rs = dao.search("select * from z03");
                ResultSetMetaData md = rs.getMetaData();
                int nColumn = md.getColumnCount();
                ArrayList fieldlist = new ArrayList();
                String fieldName = "";
                FieldItem item;
                String insertSqlKeys = "insert into z03 (";
                String insertSqlValues = " values (";
                for (int k = 1; k <= nColumn; k++) {
                    fieldName = md.getColumnName(k);
                    if (k != nColumn) {
                        insertSqlKeys += fieldName + ",";
                        insertSqlValues += "?,";
                    } else {
                        insertSqlKeys += fieldName + ")";
                        insertSqlValues += "?)";
                    }

                    item = DataDictionary.getFieldItem(fieldName);
                    if (item == null) {//非指标字段
                        item = new FieldItem();
                        item.setItemid(fieldName);
                        item.setItemdesc(fieldName);
                        item.setFieldsetid("");
                        item.setItemtype("M");
                    }
                    fieldlist.add(item);

                }
                map = new HashMap();
                ArrayList insertList = new ArrayList();//最终要导入的数据
                ArrayList valueList = new ArrayList();
                ArrayList detailList = new ArrayList();//导入数据失败原因
                String itemId = "";
                String itemDesc = "";
                String itemType = "";
                String itemValue = "";
                String detail = "";
                String codeName = "";
                Date dd = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.sql.Date date = new java.sql.Date(dd.getTime());
                IDGenerator idg = new IDGenerator(2, this.getFrameconn());
                for (int p = 0; p < contentList.size(); p++) {
                    map = (HashMap) contentList.get(p);
                    valueList = new ArrayList();
                    for (int z = 0; z < fieldlist.size(); z++) {
                        item = (FieldItem) fieldlist.get(z);
                        itemId = item.getItemid().toLowerCase();
                        itemDesc = item.getItemdesc();
                        itemType = item.getItemtype();
                        itemValue = (String) map.get(itemId) == null ? "" : (String) map.get(itemId);
                        if ("z0301".equalsIgnoreCase(itemId)) {//重新在目标数据库中生成主键用工申请序号
                            itemValue = idg.getId("Z03.Z0301");
                        }
                        if (item.isFillable()) {//是否必填
                            if (itemValue == null || "".equals(itemValue)) {
                                detail = "'" + itemDesc + "'为必填项，而源数据对应项为空!";
                                detailList.add(getErrorInfor(map, detail));
                                valueList.clear();
                                break;
                            }

                        }

                        if ("z0101".equalsIgnoreCase(itemId)) //dengcan 20140818  将招聘需求计划ID置空
                            itemValue = "";

                        if ("A".equalsIgnoreCase(itemType)) {
                            if ("0".equals(item.getCodesetid())) {//非代码型

                            } else {//判断代码性指标是否有对应代码值
                                if (itemValue != null && !"".equals(itemValue) && !":".equals(itemValue)
                                        && !"null:".equals(itemValue)) {
                                    itemValue = itemValue.split(":")[0];
                                    codeName = AdminCode.getCode(item.getCodesetid(), itemValue) != null ? AdminCode.getCode(
                                            item.getCodesetid(), itemValue).getCodename() : "";
                                    if (codeName == null || "".equals(codeName)) {
                                        detail = "'" + itemDesc + "'为代码型指标，而源数据对应项没有符合的代码!";
                                        detailList.add(getErrorInfor(map, detail));
                                        valueList.clear();
                                        break;
                                    }
                                }

                            }
                        } else if ("D".equalsIgnoreCase(itemType)) {
                            if (itemValue != null && !"".equals(itemValue)) {
                                Pattern pattern = Pattern.compile(d);
                                Matcher m = pattern.matcher(itemValue.trim());
                                if (!m.matches()) {
                                    detail = "'" + itemDesc + "'应该为日期，而源数据对应项不是或格式不对!";
                                    detailList.add(getErrorInfor(map, detail));
                                    valueList.clear();
                                    break;
                                }
                                dd = sdf.parse(itemValue);
                                date = new java.sql.Date(dd.getTime());
                            } else {
                                date = null;
                            }
                        } else if ("N".equalsIgnoreCase(itemType)) {
                            if ("".equals(itemValue.trim()))
                                itemValue = null;
                            if (itemValue != null && !"".equals(itemValue)) {
                                Pattern pattern = Pattern.compile(n);
                                Matcher m = pattern.matcher(itemValue.trim());
                                if (!m.matches()) {
                                    detail = "'" + itemDesc + "'应该为整数，而源数据对应项不是!";
                                    detailList.add(getErrorInfor(map, detail));
                                    valueList.clear();
                                    break;
                                }
                            }
                        } else if ("M".equalsIgnoreCase(itemType)) {

                        }
                        if ("D".equalsIgnoreCase(itemType)) {
                            valueList.add(date);
                        } else {
                            valueList.add(itemValue);
                        }

                    }
                    if (valueList.size() != 0)
                        insertList.add(valueList);
                }

                /*导入数据**/
                String insertSql = insertSqlKeys + insertSqlValues;//导入sql语句
                //批量新增
                dao.batchInsert(insertSql, insertList);
                /*向服务器端输出失败的导入数据信息*/
                String infor = "本次导入数据共" + contentList.size() + "条，成功了" + (contentList.size() - detailList.size()) + "条，失败了"
                        + detailList.size() + "条。";
                String logTextName = "";
                if (detailList.size() > 0) {
                    logTextName = WriteImortDetail(detailList, contentList.size(), this.userView.getA0100());
                    //安全平台改造,防止任意文件下载漏洞,将文件名字加密
                    logTextName = PubFunc.encrypt(logTextName);
                }
                this.getFormHM().put("logTextName", logTextName);
                this.getFormHM().put("infor", infor);
                /*删除压缩包*/
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeResource(workbook);
            PubFunc.closeResource(inputStream);
            PubFunc.closeResource(outputStream);
            PubFunc.closeResource(is);
        }
    }

    /**
     *  获得数据导入失败原因
     * @param map
     * @param detail1
     * @return
     */
    public String getErrorInfor(HashMap map, String detail1) {
        String detail = "";
        String detail0 = "";
        String danwei = "";
        String bumen = "";
        String gangwei = "";
        String zhuanye = "";
        String qudao = "";
        if (map.get("z0321") != null && !"".equals(map.get("z0321")) && !":".equals(map.get("z0321"))) {
            if (map.get("z0321").toString().split(":").length > 1)
                danwei = map.get("z0321").toString().split(":")[1];
        }
        if (map.get("z0325") != null && !"".equals(map.get("z0325")) && !":".equals(map.get("z0325"))) {
            if (map.get("z0325").toString().split(":").length > 1)
                bumen = map.get("z0325").toString().split(":")[1];
        }
        if (map.get("z0311") != null && !"".equals(map.get("z0311")) && !":".equals(map.get("z0311"))) {
            if (map.get("z0311").toString().split(":").length > 1)
                gangwei = map.get("z0311").toString().split(":")[1];
        }

        zhuanye = (String) map.get("z0338");
        if (map.get("z0336") != null && !"".equals(map.get("z0336")) && !":".equals(map.get("z0336")))
            qudao = map.get("z0336").toString().split(":")[0];

        if ("01".equalsIgnoreCase(qudao)) {//校园招聘
            detail0 = subString("需求单位：" + danwei, 40) + subString("需求部门:" + bumen, 40) + subString("需求专业:" + zhuanye, 40)
                    + " 此条数据导入失败，因为";
        } else {
            detail0 = subString("需求单位：" + danwei, 40) + subString("需求部门:" + bumen, 40) + subString("需求岗位:" + gangwei, 40)
                    + " 此条数据导入失败，因为";
        }
        detail = detail0 + detail1;
        return detail;
    }

    /**
     * 记录导入信息
    * @throws GeneralException 
     */
    public String WriteImortDetail(ArrayList detailList, int total, String a0100) throws GeneralException {
        BufferedWriter output = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            String outname = "";
            outname = "HireImportLog" + a0100 + ".txt";
            String pathFile = System.getProperty("java.io.tmpdir");
            pathFile += System.getProperty("file.separator") + outname;

            File file = new File(pathFile);
            //		 if (file.exists()) { 
            //		 file.delete();           
            //		 }                          
            //		 file.createNewFile();                               
            output = new BufferedWriter(new FileWriter(file));
            output.write(date + "\n");
            output.write("本次导入数据共" + total + "条，成功了" + (total - detailList.size()) + "条，失败了" + detailList.size() + "条。\n");
            for (int i = 0; i < detailList.size(); i++) {
                output.write(subString(String.valueOf(i + 1) + "、", 6) + String.valueOf(detailList.get(i)) + "\n");
            }
            output.close();
            return outname;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeIoResource(output);
        }
    }

    /**
     * 统一长度
     * @param str
     * @return
     */
    public static String subString(String str, int n) {

        int count = 0;
        char[] chs = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            count += (chs[i] > 0xff) ? 2 : 1;
        }

        if (str.length() < n) {
            for (int i = 0; i < n - count; i++) {
                str += " ";
            }
        }
        return str;
    }
}
