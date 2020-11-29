package com.hjsj.hrms.transaction.kq.feast_manage;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.ss.usermodel.*;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * Title:ImportYearHolidayTrans.java
 * </p>
 * <p>
 * Description:年假导入模板数据
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2013-06-03 13:00:00
 * </p>
 * 
 * @author yhj
 * @version 1.0
 * 
 * @author zxj
 * @version 1.1
 */
public class ImportHolidayTrans extends IBusiness {

    public void execute() throws GeneralException {
        String importMsg = "";
        
        FormFile form_file = (FormFile) getFormHM().get("file");
        try {
			if(!FileTypeUtil.isFileTypeEqual(form_file)){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String tablename = (String) hm.get("tablename");
        hm.remove("tablename");
        //拿到假期类型
        String holidayType = (String)hm.get("holidayType");
        hm.remove("holidayType");

        StringBuffer sql = new StringBuffer();
        sql.append("update " + tablename + " set ");
        int updateFidsCount = 0;// 将要更新的字段数目
        
        Workbook wb = null;
        Sheet sheet = null;
        InputStream stream = null;
        try {
        	stream = form_file.getInputStream();
            wb = WorkbookFactory.create(stream);
            sheet = wb.getSheetAt(0);
            //每次导入前先把消息清空
            this.getFormHM().put("importMsg", "");
            // 定义一个list用来存放哪些 指标可以修改
            LinkedHashMap map = new LinkedHashMap();
            Row row = sheet.getRow(0);
            if (row == null){
                //throw new GeneralException();
                importMsg = "请用导出的Excel模板来导入数据！";
                return;
            }

            int cols = row.getPhysicalNumberOfCells();
            int rows = sheet.getPhysicalNumberOfRows();
            StringBuffer codeBuf = new StringBuffer();
            ContentDAO dao = new ContentDAO(this.frameconn);
            HashMap dbMap = new HashMap();
            String dbSql = "select pre , dbname  from dbname";
            this.frowset = dao.search(dbSql);
            while (this.frowset.next()) {
                dbMap.put(this.frowset.getString("dbname"),
                        this.frowset.getString("pre"));
            }

            // 识别模板类型
            boolean priKeyColExist = false; // 固定模板“主键标识”列存在
            boolean q03z0ColExist = false; // 自定义模板“日期”列
            boolean gnoColExists = false; // 自定义模板“工号”列

            HashMap codeColMap = new HashMap();
            if (row != null) {

                if (cols < 1 || rows < 1) {
                    priKeyColExist = false;
                    q03z0ColExist = false;
                    gnoColExists = false;
                } else {
                    for (int i = 0; i < 2; i++) {
                        String value = "";
                        Cell cell = row.getCell((short) i);
                        if (cell != null) {
                            switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_FORMULA:
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                double y = cell.getNumericCellValue();
                                value = Double.toString(y);
                                break;
                            case Cell.CELL_TYPE_STRING:
                                value = cell.getStringCellValue();
                                break;
                            default:
                                value = "";
                            }
                        } else {
                            priKeyColExist = false;
                            q03z0ColExist = false;
                            gnoColExists = false;
                            break;
                        }

                        if (i == 0) {
                            if ("主键标识串".equalsIgnoreCase(value))
                                priKeyColExist = true;
                            else if ("日期".equalsIgnoreCase(value)) {
                                q03z0ColExist = true;
                                gnoColExists = true;
                            }
                        }
                        if (i == 1) {
                            if (!"工号".equalsIgnoreCase(value)
                                    && !priKeyColExist) {
                                gnoColExists = false;
                                
                            }

                        } else if ((1 == i) && (!"工号".equalsIgnoreCase(value))) {
                            gnoColExists = false;
                        }

                        if (!priKeyColExist && !q03z0ColExist && !gnoColExists)
                            break;
                    }
                }

                if (!priKeyColExist && !q03z0ColExist && !gnoColExists){
                    importMsg = "请用导出的Excel模板来导入数据！";
                    return;
                }

                // 固定模板
                short initCol = 2;
                if (priKeyColExist)
                    initCol = 1;
                
                for (short c = initCol; c < cols; c++) {
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
                            importMsg = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                            return;
                        }
                        
                        Comment myComment=cell.getCellComment();
                        //如果没有了批注
                        if(myComment==null){
                            importMsg = "标题行存在空标题！请用导出的Excel模板来导入数据！";
                            return;
                        }
                        

                        String field = cell.getCellComment().getString()
                                .toString().toLowerCase().trim();
                       //工号不从模版导入
                        if("f1".equals(field))
                        {
                        	continue;
                        }
                        String codesetid = DataDictionary.getFieldItem(field)
                                .getCodesetid();

                        if (!"0".equals(codesetid)) {
                            if (!"UM".equals(codesetid)
                                    && !"UN".equals(codesetid)
                                    && !"@K".equals(codesetid)) {
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='"
                                        + codesetid
                                        + "'  and codeitemid=childid  union all ");
                            } else {
                                codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='"
                                        + codesetid
                                        + "' and  codeitemid not in (select parentid from organization where codesetid='"
                                        + codesetid + "') union all ");
                            }
                        }
                        // 增加过滤A01指标，A01指标不能更改;更改
                        if ("b0110,e01a1,e0122,a0100,a0101,nbase".indexOf(field.toLowerCase()) == -1)// 单位 部门 姓名字段不更新
                        {
                            String itemtype = DataDictionary.getFieldItem(field).getItemtype();
                            boolean booindex = getindexA01(itemtype, field);
                            if (booindex) {
                                map.put(new Short(c), field + ":" + cell.getStringCellValue());
                                sql.append(field + "=?,");
                                updateFidsCount++;
                            }
                        }
                    } else
                        break;
                }
                
                if (codeBuf.length() > 0) {
                    codeBuf.setLength(codeBuf.length() - " union all ".length());
                    try {
                        RowSet rs = dao.search(codeBuf.toString());
                        while (rs.next())
                            codeColMap.put(rs.getString("codesetid") + "a04v2u"
                                    + rs.getString("codeitemdesc"),
                                    rs.getString("codeitemid"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                
                sql.setLength(sql.length() - 1);
                // 人员库 人员编号 年度 来标识 每行记录
                sql.append(" where NBASE=? and A0100=? and Q1709=? and Q1701=?");

                if (this.userView.isAdmin()) {
                    sql.append(" and 1=1 ");
                } else {
                    // 要控制人员范围
                    String a_code = RegisterInitInfoData.getKqPrivCode(this.userView)
                            + RegisterInitInfoData.getKqPrivCodeValue(this.userView);
                    
                    if (a_code.length() >= 2) {
                        String codesetid = a_code.substring(0, 2);
                        String value = a_code.substring(2);
                        if ("UN".equalsIgnoreCase(codesetid)) {
                            sql.append(" and (B0110 like '");
                            sql.append(value);
                            sql.append("%'");
                            
                            if ("".equalsIgnoreCase(value))
                                sql.append(" or B0110 is null");
                            
                            sql.append(")");
                        } else if ("UM".equalsIgnoreCase(codesetid)) {
                            sql.append(" and E0122 like '");
                            sql.append(value);
                            sql.append("%'");
                        }
                    } else if (a_code.trim().length() == 0)// 没有管理权限
                        sql.append(" and 1=2 ");
                }

            }
            
            // 定义更新了的记录数
            int updateCount = 0;
            ArrayList infoList = new ArrayList();//存放记录集合
            for (int j = 1; j < rows; j++) {
                row = sheet.getRow(j);
                if (row == null)
                    continue;
                
                // 固定模板取nbase\a0100\q03z0值
                if (priKeyColExist) {
                    Cell flagCol = row.getCell((short) 0);
                    if (flagCol == null)
                        continue;
                    
                    switch (flagCol.getCellType()) {
                        case Cell.CELL_TYPE_BLANK:
                            importMsg = "主键标识串列存在空数据，导入数据失败！";
                            return;
                        case Cell.CELL_TYPE_STRING:
                            if (flagCol.getRichStringCellValue().toString().trim().length() == 0){
                                importMsg = "主键标识串列存在空数据，导入数据失败！";
                                return;
                            }
                            
                            break;
                    }

                    String whichDb = "";
                    String userNo = "";

                    ArrayList importValue = new ArrayList();

                    String[] primaryKey = row.getCell(0).getStringCellValue().trim().split("\\|");
                    //人员库
                    whichDb = primaryKey[0];
                    //人员编号
                    userNo = primaryKey[1];
                    //高级授权
                    KqParameter kqParameter = new KqParameter(userView, "", this.getFrameconn());
            		HashMap paraMap = kqParameter.getKqParamterMap();
            		String g_no= (String) paraMap.get("g_no");
                    boolean empA01 = this.getEmpA01(whichDb,userNo, g_no, dao);
					if(empA01){
						importMsg = "第" + (j+1) + "行工号不正确或无该人员权限，无法导入！";
						return;
					}
                    
                    //记录假期类型
                    String q1709="";
                    //拿到年度
                    String q1701="";
                    Iterator it = map.entrySet().iterator(); 
                    while (it.hasNext()) { 
                        Map.Entry entry = (Map.Entry) it.next();
                        //那个Excel中第几列可以导入更新
                        Short columnIndex = (Short)entry.getKey(); 
                        String mapValue = (String)entry.getValue(); 
                        int c=columnIndex.intValue();
                        
                        //把map里面的value分割成批注和描述
                        String[] titleKey=mapValue.split(":");
                        String fielditem=titleKey[0];
                        String dataType=DataDictionary.getFieldItem(fielditem).getItemtype();
                        String fieldCodeSetId=DataDictionary.getFieldItem(fielditem).getCodesetid();
                        int decwidth = DataDictionary.getFieldItem(fielditem).getDecimalwidth();
                
                        Cell thecell = row.getCell(c);
                        //拿到年度
                        
                        if("q1701".equalsIgnoreCase(fielditem)){
                            String thecellValue = thecell.getStringCellValue();
                            importValue.add(thecellValue);
                            q1701 = thecellValue;
                            continue;
                        }
                        
                        switch(thecell.getCellType()){
                            //如果是空白单元格
                            case Cell.CELL_TYPE_BLANK:
                                if("N".equalsIgnoreCase(dataType)){
                                    //如果数值型的字段没填，则默认填0.0
                                        importValue.add(new Double(0.0));
                                        break;
                                }else if("D".equalsIgnoreCase(dataType)||"A".equalsIgnoreCase(dataType)){
                                    importValue.add(null);
                                    break;
                                }
                                
                            //如果是数值型单元格
                            case Cell.CELL_TYPE_NUMERIC:
                                double thecellValue1= thecell.getNumericCellValue();
                                //如果是数值型的字段
                                if("N".equalsIgnoreCase(dataType)){
                                    //去匹配是否符合要求
                                    if(isDataType(decwidth,dataType,thecellValue1+"")){
                                        String value = Double.toString(thecellValue1);
                                        value = PubFunc.round(value, decwidth);
                                        importValue.add(new Double((PubFunc.round(value, decwidth))));
                                        break;
                                    }else{
                                        importMsg = "第"+(j+1)+"行，第"+c+"列，日期格式错误！";
                                        return;
                                    }
                                }
                            
                            case Cell.CELL_TYPE_STRING:
                                String thecellValue2=thecell.getStringCellValue();
                                if(thecellValue2 == null || "".equals(thecellValue2)){
                                    importValue.add(null);
                                    break;
                                }
                                    
                                //判断是字符型字段
                                if("A".equalsIgnoreCase(dataType)){
                                    
                                    //如果是代码型的字段 codesetid不为0，不为null
                                    if(!("0".equalsIgnoreCase(fieldCodeSetId))&&fieldCodeSetId!=null&&!("".equalsIgnoreCase(fieldCodeSetId))){
                                        //则根据代码的描述去拿对应的编码
                                        String codeitem = getCodeByDesc(fieldCodeSetId, thecellValue2);
                                        //判断是否有该代码
                                        if(codeitem==null){
                                            importMsg = "第"+(j+1)+"行，第"+c+"列，该代码名称在数据字典中找不到对于代码！";
                                            return;
                                        }
                                        
                                        importValue.add(codeitem);
                                        if("q1709".equalsIgnoreCase(fielditem)){
                                            q1709=codeitem;
                                        }
                                        break;
                                        
                                    }else{
                                        //如果是普通字符型字段就直接添加
                                        importValue.add(thecellValue2);
                                        break;
                                    }
                                    
                                }
                            
                                //如果是日期
                                if("D".equalsIgnoreCase(dataType)){
                                    //去判断日期格式是否正确
                                    if(isDataType(decwidth,dataType,thecellValue2)){
                                        thecellValue2=thecellValue2.replaceAll("/", "-");
                                        //如果是起始时间则加上00:00:00
                                        if("q17z1".equalsIgnoreCase(fielditem)){
                                            thecellValue2=thecellValue2+" 00:00:00";
                                        }
                                        //如果是终止时间则加上23:59:59
                                        if("q17z3".equalsIgnoreCase(fielditem)){
                                            thecellValue2=thecellValue2+" 23:59:59";
                                        }
                                        
                                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(thecellValue2); 
                                        java.sql.Timestamp stp = new java.sql.Timestamp(date.getTime());
                                        importValue.add(stp);
                                        break;
                                    }else{
                                        importMsg = "第"+(j+1)+"行，第"+c+"列日期格式错误！";
                                        return;
                                    }
                                    
                                }
                            
                                if("N".equalsIgnoreCase(dataType)){
                                    //如果数值型的字段没填，则默认填0.0
                                    if(thecellValue2==null||"".equalsIgnoreCase(thecellValue2)){
                                        importValue.add(new Double(0.0));
                                        break;
                                    }
                                    //去匹配是否符合要求
                                    else if(isDataType(decwidth,dataType,thecellValue2)){
                                        thecellValue2 = PubFunc.round(thecellValue2, decwidth);
                                        importValue.add(new Double((PubFunc.round(thecellValue2, decwidth))));
                                        break;
                                    }else{
                                        importMsg = "第"+(j+1)+"行，第"+c+"列数值格式错误！";
                                        return;
                                    }
                                    
                                }
                        }
                    }
                    
                    //添加判断  假期类型不能为空
                    if("".equalsIgnoreCase(q1709)||q1709==null){
                        importMsg = "第"+(j+1)+"行，假期类型不能为空！";
                        return;
                    }
                    
                    //判断导入的假期类型是否与选中类型按钮的假期形同，如果不同，则提示
                    if(!holidayType.equalsIgnoreCase(q1709)){
                        importMsg = "第"+(j+1)+"行，假期类型与当前导入类型不一致！";
                        return;
                    }
                    
                    //添加判断  年度不能为空
                    if("".equalsIgnoreCase(q1701)||q1701==null){
                        importMsg = "第"+(j+1)+"行，年度不能为空！";
                        return;
                    }
                    
                    // 添加人员库
                    importValue.add(whichDb);
                    // 添加人员编号
                    importValue.add(userNo);
                    // 添加假期类型 q1709
                    importValue.add(q1709);
                    // 添加哪一个年度
                    importValue.add(q1701);
                    infoList.add(importValue);
                        updateCount++;
                    
                }
            }
            dao.batchUpdate(sql.toString(), infoList);
            importMsg = "成功导入" + updateCount + "条记录";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeIoResource(wb);
            PubFunc.closeIoResource(stream);
            this.getFormHM().put("importMsg", importMsg);  
        }
    }

    /**
	 * @param whichDb
	 * @param userNo
	 * @param gNo
	 * @param dao
	 * @return
	 * @author szk
	 * 2014-5-13下午03:53:34
	 */
	private boolean getEmpA01(String whichDb, String userNo, String gNo,
			ContentDAO dao)
	{
        try {
			StringBuffer sb = new StringBuffer();
			sb.append("select a0100 from " + whichDb + "A01");
			sb.append(" where a0100=?");
			if (this.userView.isAdmin()) {
				sb.append(" and 1=1 ");
			} else {
				// 要控制人员范围
				String whereIn = RegisterInitInfoData.getWhereINSql(userView,whichDb);
				sb.append(" and a0100 in (");
				sb.append(" select a0100 " + whereIn + ")");
			}
			
			ArrayList sqlParam = new ArrayList();
			sqlParam.add(userNo);
			this.frowset = dao.search(sb.toString(), sqlParam);
			if (this.frowset.next()) 
			{
				return false;
			}
			
		}  catch (SQLException e) {
			e.printStackTrace();
		}  finally{
			KqUtilsClass.closeDBResource(frowset);
		}
		return true;
	}

	/**
     * 判断Q03中那些指标是从A01主集中取得的
     * 
     * @param itemtype
     * @param itemid
     * @return
     */
    public boolean getindexA01(String itemtype, String itemid) {
        boolean field = true;
        itemtype = itemtype.toUpperCase();
        itemid = itemid.toUpperCase();

        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());

            String sql = "select itemid from fielditem where fieldsetid='A01' and itemid='"
                    + itemid + "' and itemtype='" + itemtype + "'";
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String itemi = rs.getString("itemid");
                if (!"A0101".equals(itemi) && !"E0122".equals(itemi)) {
                    if (itemi != null && itemi.length() > 0) {
                        field = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return field;
    }

    /**
     * 判断 值类型是否与 要求的类型一致
     * 
     * @param columnBean
     * @param itemid
     * @param value
     * @return
     */
    public boolean isDataType(int decwidth, String itemtype, String value) {

        boolean flag = true;
        if ("N".equals(itemtype)) {
            if (decwidth == 0) {
                flag = value.matches("^[+-]?[\\d]+$");
            } else {
                flag = value.matches("^[+-]?[\\d]*[.]?[\\d]+");
            }

        } else if ("D".equals(itemtype)) {
            flag = value.matches("[0-9]{4}[#-.][0-9]{2}[#-.][0-9]{2}");
        }
        return flag;
    }


    private String getCodeByDesc(String codesetId, String codeDesc){
        String sql="select codeitemid from codeitem where codesetid='" + codesetId + "' and codeitemdesc='"+codeDesc+"'";
        RowSet rs = null;
        try{
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
}
