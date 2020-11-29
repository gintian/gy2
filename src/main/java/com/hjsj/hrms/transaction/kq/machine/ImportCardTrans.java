package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *<p>Title:ImportCardTrans.java</p> 
 *<p>Description:导入刷卡记录</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:20140429</p> 
 *@author szk
 *@version 1.0
 */
public class ImportCardTrans extends IBusiness {

    public void execute() throws GeneralException {
        String error_message = "";
        FormFile file = (FormFile) this.getFormHM().get("file");
        String table = "kq_originality_data";
        try {
            if(!FileTypeUtil.isFileTypeEqual(file)){
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        StringBuffer sql = new StringBuffer();
        sql.append("insert into " + table + " ( ");
        
        int updateFidsCount = 0;// 将要更新的字段数目
        int cardIndex= 0;
        DbWizard dWizard = new DbWizard(frameconn);
        KqParameter kqParameter = new KqParameter(userView, "", this.getFrameconn());
        HashMap paraMap = kqParameter.getKqParamterMap();
        String g_no= (String) paraMap.get("g_no");
        String card_on = kqParameter.getCardno();
        if (card_on == null || card_on.length() <= 0) 
        {
            error_message = "请在参数设置里设置考勤卡号对应指标！";
            return;
        }
        HashMap empA01 = new HashMap();
        Workbook wb = null;
        Sheet sheet = null;
        try {
            InputStream inputStream=null;
            try{
                    inputStream=file.getInputStream();
                    wb = WorkbookFactory.create(inputStream);
                    sheet = wb.getSheetAt(0);
            }
            finally{
                PubFunc.closeResource(wb);
                PubFunc.closeResource(inputStream);   
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        try {
            //每次导入前先把消息清空
            this.getFormHM().put("error_message", "");
            // 定义一个list用来存放哪些 指标可以修改
            //HashMap map = new HashMap();
            ArrayList map = new ArrayList();
            Row row = sheet.getRow(0);//第二行
            if (row == null){
                error_message = "请用导出的Excel模板来导入数据！";
                return;
            }

            int cols = row.getPhysicalNumberOfCells();
            int rows = sheet.getPhysicalNumberOfRows();
            
            ContentDAO dao = new ContentDAO(this.frameconn);

            // 取表头

            if (cols < 1 || rows < 1) {
                
            } else {
                for (short c = 0; c < cols; c++) {
                    Cell cell = row.getCell(c);
                    if (cell != null) {
                        String title = "";
                        switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_FORMULA:
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            double y = cell.getNumericCellValue();
                            title = Double.toString(y);
                            break;
                        case Cell.CELL_TYPE_STRING:
                            title = cell.getStringCellValue();
                            break;
                        default:
                            title = "";
                        }

                        if ("".equals(title.trim())){
                            error_message = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                            return;
                        }
                        
                        Comment myComment=cell.getCellComment();
                        //如果没有了批注
                        if(myComment==null){
                            error_message = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                            return;
                        }
                        

                        String field = cell.getCellComment().getString().toString().toLowerCase().trim();
                        // 如果有工号  工号不校验  直接跳出
                        if (g_no.equalsIgnoreCase(field)) 
                            continue;
                        // 校验卡号
                        if ("card_no".equalsIgnoreCase(field)) 
                            cardIndex = c;
                            
                        if(!dWizard.isExistField(table, field.toUpperCase(), false))
                        {
                            error_message = "字段“" + field + "”未构库或不存在！";
                            return;
                        }
                        //增加过滤A01指标，A01指标不能更改;更改
                        if (("b0110,e01a1,e0122,a0100,a0101,nbase").indexOf(field.toLowerCase()) == -1)// 单位 部门 姓名字段不更新
                        {
                            boolean booindex = getindexA01(field);
                            if (booindex) {
                                CommonData commonData = new CommonData(String.valueOf(c) , field + ":" + cell.getStringCellValue());
                                //map.put(new Short(c), field + ":" + cell.getStringCellValue());
                                map.add(commonData);
                                sql.append(field + ", ");
                                updateFidsCount++;
                            }
                        }
                    }else 
                    {
                        error_message = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                        return;
                    }
                }
                sql.append("a0100, a0101, nbase, b0110, e0122, e01a1 ");
                updateFidsCount = updateFidsCount +6;
                sql.append(") values (");
                for (int i = 0; i < updateFidsCount; i++) 
                {
                    sql.append("?, ");
                }
                sql.setLength(sql.length()-2);
                sql.append(")");


            }
            
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            timeFormat.setLenient(false);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            
            // 开始取数据
            ArrayList infoList = new ArrayList();//存放记录集合
            ArrayList dellist = new ArrayList();
            int updateCount = 0;
            for (int j = 1; j < rows; j++) {
                row = sheet.getRow(j);
                if (row == null)
                    continue;
                boolean isnull = true;
                for (int i = 0; i < cols; i++) 
                {
                    Cell oneCell = row.getCell(i);
                    if (oneCell != null && oneCell.getCellType() != Cell.CELL_TYPE_BLANK){
                        isnull = false;
                        break;
                    }
                }
                if(isnull)
                    continue;
                Cell cardCell = row.getCell(cardIndex);
                if (cardCell == null)
                {
                    error_message = "第" + (j+1) + "行考勤卡号为空，无法导入该数据！";
                    return;
                }
                else
                {
                    if (cardCell.getCellType() == Cell.CELL_TYPE_NUMERIC) 
                        cardCell.setCellType(Cell.CELL_TYPE_STRING);
                    empA01 = this.getEmpA01(cardCell.getStringCellValue(), card_on, dao);
                    if(empA01 == null){
                        error_message = "第" + (j+1) + "行考勤卡号不正确或无该人员权限，无法导入该数据！";
                        return;
                    }
                }
                
                
                ArrayList importValue = new ArrayList();//一条记录的集合插入用
                ArrayList delValue = new ArrayList();//一条记录的集合删除用
                
                for (int i = 0; i < map.size(); i++) 
                {
                    
                    CommonData commonData = (CommonData) map.get(i);
                    String columnIndex = commonData.getDataValue();//(Short)entry.getKey(); 
                    String mapValue = commonData.getDataName();//(String)entry.getValue(); 
                    int c = Integer.parseInt(columnIndex);
                    
                    //把map里面的value分割成批注和描述
                    String[] titleKey=mapValue.split(":");
                    String fielditem=titleKey[0];
                    Cell thecell = row.getCell(c);
                    
                    if ("oper_time".equalsIgnoreCase(fielditem))
                    {
                        //linbz 27802 如果获取的操作时间为null 直接添加当前时间,由于下面去重时list比较所以时间设置为一致的00:00
                        String nowValue = dateFormat.format(new Date()) +" 00:00";
                        Date nowDate = OperateDate.strToDate(nowValue, "yyyy-MM-dd HH:mm");
                        java.sql.Timestamp nowstp = new java.sql.Timestamp(nowDate.getTime());
                        if(null == thecell){
                             importValue.add(nowstp);
                        }else{
                             String thecellValue1= thecell.getStringCellValue();
                             if(thecellValue1==null ||  "".equals(thecellValue1.trim()))
                             {
                                importValue.add(nowstp);
                             }else {
                                if(thecellValue1.length()<=10){
                                    thecellValue1 = thecellValue1+" 00:00";
                                }
                                Date operDate = OperateDate.strToDate(thecellValue1, "yyyy-MM-dd HH:mm");
                                java.sql.Timestamp stp = new java.sql.Timestamp(operDate.getTime());
                                importValue.add(stp);
                            }
                        }
                    }
                    else
                    {
                        //刷卡表都为varchar
                        String thecellValue2="";
                        if (thecell != null && thecell.getCellType() != Cell.CELL_TYPE_BLANK){
                            if(thecell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                                if(thecell.getDateCellValue() instanceof Date 
                                        && ("work_time".equalsIgnoreCase(fielditem) || "work_date".equalsIgnoreCase(fielditem))){
                                    try {
                                        Date dateStr = thecell.getDateCellValue();
                                        if ("work_time".equalsIgnoreCase(fielditem))
                                            thecellValue2 = timeFormat.format(dateStr);
                                        else {
                                            thecellValue2 = dateFormat.format(dateStr);
                                        }
                                    } catch (Exception e) {
                                        error_message = "第"+(j+1)+"行打卡日期或时间数据错误，无法导入该数据！";
                                        return;
                                    }
                                }else
                                    thecellValue2 = thecell.getNumericCellValue()+"";
                            }else {
                                thecellValue2=thecell.getStringCellValue();
                                try {
                                    if ("work_time".equalsIgnoreCase(fielditem)) {
                                        timeFormat.parse(thecellValue2);
                                        thecellValue2 = timeFormat.format(DateUtils.getTimestamp(thecellValue2, "HH:mm"));
                                    }
                                    else if("work_date".equalsIgnoreCase(fielditem)) {
                                        thecellValue2 = thecellValue2.replaceAll("\\.", "-");
                                        dateFormat.parse(thecellValue2);;
                                        thecellValue2 = dateFormat.format(DateUtils.getDate(thecellValue2, "yyyy-MM-dd"));
                                    }
                                } catch (Exception e) {
                                    error_message = "第"+(j+1)+"行打卡日期或时间数据错误，无法导入该数据！";
                                    return;
                                }
                            }
                        }
                            
                        //判断是字符型字段
                            if("sp_flag".equalsIgnoreCase(fielditem)){
                                if(thecellValue2 == null || thecellValue2.trim().length() <= 0){
                                    error_message = "第"+(j+1)+"行审批状态为空，无法导入该数据！";
                                    return;
                                }
                                //则根据代码的描述去拿对应的编码
                                String codeitem = getCodeByDesc(thecellValue2);
                                //判断是否有该代码
                                if(codeitem==null){
                                    error_message = "第"+(j+1)+"行，第"+(c+1)+"列，该代码名称在数据字典中找不到对应代码！";
                                    return;
                                }
                                importValue.add(codeitem);
                            } else if("inout_flag".equalsIgnoreCase(fielditem)){
                                if(thecellValue2 == null || thecellValue2.trim().length() <= 0){
                                    error_message = "第"+(j+1)+"行进出标志为空，无法导入该数据！";
                                    return;
                                }
                                if ("不限".equals(thecellValue2))
                                {
                                    importValue.add("0");
                                }
                                else if ("进".equals(thecellValue2))
                                {
                                    importValue.add("1");
                                }
                                else if("出".equals(thecellValue2))
                                {
                                    importValue.add("-1");
                                }else{
                                    error_message = "第"+(j+1)+"行进出标志数据错误，无法导入该数据！";
                                    return;
                                }
                            } else if("iscommon".equalsIgnoreCase(fielditem))
                            {
                                if ("否".equals(thecellValue2))
                                {
                                    importValue.add("0");
                                }
                                else 
                                {
                                    importValue.add("1");
                                }
                            }
                            else{
                                if ("work_date".equalsIgnoreCase(fielditem))
                                {
                                    if (thecellValue2==null || thecellValue2.trim().length()==0)
                                    {
                                            error_message = "第" + (j+1) + "行刷卡日期为空，无法导入该数据！";
                                            return;
                                    }
                                    else
                                    {
                                    delValue.add(thecellValue2.replaceAll("-", "\\."));
                                    
                                    }
                                }
                                if ("work_time".equalsIgnoreCase(fielditem))
                                {
                                    if (thecellValue2==null || thecellValue2.trim().length()==0)
                                    {
                                            error_message = "第" + (j+1) + "行刷卡时间为空，无法导入该数据！";
                                            return;
                                    }
                                    else
                                    {
                                    delValue.add(thecellValue2);
                                    
                                    }
                                }
                                //如果是普通字符型字段就直接添加
                                if (thecell == null) 
                                {
                                    importValue.add(null);
                                    continue;
                                }
                                if("work_date".equalsIgnoreCase(fielditem) && (thecellValue2!=null || thecellValue2.trim().length()>0)){
                                    importValue.add(thecellValue2.replaceAll("-", "\\."));
                                }else{
                                    importValue.add(thecellValue2);
                                }
                                
                            }
                    }
                }
                
                importValue.add((String)empA01.get("a0100"));//人员编号
                importValue.add((String)empA01.get("a0101"));
                importValue.add((String)empA01.get("nbase"));
                importValue.add((String)empA01.get("b0110"));
                importValue.add((String)empA01.get("e0122"));
                importValue.add((String)empA01.get("e01a1"));
                infoList.add(importValue);
                
                delValue.add((String)empA01.get("a0100"));//人员编号
                delValue.add((String)empA01.get("nbase"));
                
                dellist.add(delValue);
                updateCount++;
            }
            dao.batchUpdate("delete from kq_originality_data  where work_date=? and work_time=? and a0100=? and nbase=?", dellist);
            //linbz 在导入之前，先去重 （a0100,nbase,work_date,work_time四个主键都相等时去重）
            for (int i=0 ;i<infoList.size()-1;i++){       
                for (int j=infoList.size()-1;j>i;j--){  
                    ArrayList infoList1 = (ArrayList) infoList.get(i);
                    
                    ArrayList infoList2 = (ArrayList) infoList.get(j);
                    if(infoList1.containsAll(infoList2)){       
                        infoList.remove(j); 
                     }        
                  }        
            }
            dao.batchInsert(sql.toString(), infoList) ;
            error_message = "成功导入" + infoList.size() + "条记录！";
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            this.getFormHM().put("error_message", error_message);  
        }
    }
    
    
    /**
     * 判断那些指标是从A01主集中取得的
     * 
     * @param itemtype
     * @param itemid
     * @return
     */
    private boolean getindexA01(String itemid) {
        boolean field = true;
        itemid = itemid.toUpperCase();

        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            String sql = "select itemid from fielditem where fieldsetid='A01' and itemid='"
                    + itemid + "'";
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String itemi = rs.getString("itemid");
                if (itemi != null && itemi.length() > 0) {
                    field = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return field;
    }

    private String getCodeByDesc(String codeDesc){
        String sql="select codeitemid from codeitem where codeitemdesc='"+codeDesc+"'";
        RowSet rs = null;
        try{
            if ("待批".equals(codeDesc)||"已报批".equals(codeDesc))
            {
                 sql="select codeitemid from codeitem where codeitemdesc='待批' or codeitemdesc='已报批'";
            }
            if ("批准".equals(codeDesc)||"已批".equals(codeDesc))
            {
                 sql="select codeitemid from codeitem where codeitemdesc='批准' or codeitemdesc='已批'";
            }
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs=dao.search(sql);
            if(rs.next()){
                String codeitemid=rs.getString("codeitemid");
                return codeitemid;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return null;
        
    }
    /**
     * 根据卡号获取人员基本信息
     * @param card_on 卡号
     * @param field 卡号字段
     * @param dao dao
     * @return
     */
    private HashMap getEmpA01(String card_on, String field, ContentDAO dao){
        HashMap list = null;
        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.frameconn,userView);
        try {
            ArrayList nbaseList = kqUtilsClass.getKqPreList();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < nbaseList.size(); i++) 
            {
                sb.setLength(0);
                sb.append("select a0100,a0101,b0110,e0122,e01a1,"+field+" from " + nbaseList.get(i) + "A01");
                sb.append(" where " + field + "= '" + card_on + "'");
                if (this.userView.isAdmin()) {
                    sb.append(" and 1=1 ");
                } else {
                    // 要控制人员范围
                    String whereIn = RegisterInitInfoData.getWhereINSql(userView, (String)nbaseList.get(i));
                    sb.append(" and a0100 in (");
                    sb.append(" select a0100 " + whereIn + ")");
                }
                this.frowset = dao.search(sb.toString());
                if (this.frowset.next()) 
                {
                    list = new HashMap();
                    list.put("nbase", nbaseList.get(i));
                    list.put("a0100", this.frowset.getString(1));
                    list.put("a0101", this.frowset.getString(2));
                    list.put("b0110", this.frowset.getString(3));
                    list.put("e0122", this.frowset.getString(4));
                    list.put("e01a1", this.frowset.getString(5));
                    list.put(field, card_on);
                    return list;
                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
