package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
/**
 * Excel排班导入
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Feb 15, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class InputExcelShiftTrans extends IBusiness {

    public void execute() throws GeneralException 
    {
        FormFile file = (FormFile) this.getFormHM().get("file");
        if (file == null)
            return;
        try {
            if(!FileTypeUtil.isFileTypeEqual(file))
            {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        String filename = file.getFileName();
        int indexInt = filename.lastIndexOf(".");
        String ext = filename.substring(indexInt + 1, filename.length());
        if (ext == null || ext.length() <= 0 || (!"xls".equals(ext) && !"xlsx".equals(ext)))
            throw GeneralExceptionHandler.Handle(new GeneralException("", "上传文件类型出错！", "", ""));

        KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, this.userView.getUserOrgId(), this
                .getFrameconn());
        String kq_cardno = kq_paramter.getCardno();
        if (kq_cardno == null || kq_cardno.length() <= 0)
            throw GeneralExceptionHandler.Handle(new GeneralException("", "没有定义卡号！", "", ""));

        String temp_Table = tempEmpClassTable();

        Workbook workbook = null;
        Sheet sheet = null;
        InputStream _in = null;
        try {
            _in = file.getInputStream();
            workbook = WorkbookFactory.create(_in);
        } catch (InvalidFormatException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            PubFunc.closeResource(workbook);
            PubFunc.closeIoResource(_in);
        }

        sheet = workbook.getSheetAt(0);

        int rowNum, cellNum;
        int i;
        Row row = null;
        Cell cell = null;

        rowNum = sheet.getLastRowNum();
        ArrayList q03z0_list = new ArrayList();
        ArrayList dataList = new ArrayList();
        //获取未封存最小月的第一天
        RegisterDate registerDate = new RegisterDate();
        ArrayList dateList = registerDate.getKqDayList(this.getFrameconn());
        String minDateStr = "";
        Date minDate = null;
        if(dateList!=null || dateList.size()>0){
        	minDateStr = (String) dateList.get(0);
        	minDate = DateUtils.getDate(minDateStr, "yyyy.MM.dd");
        }
        
        try{
            //判断“日期”单元格合并的最后位置
            int dateCellStartIndex = 3;
            int sheetMergeCount = sheet.getNumMergedRegions();
            
            for(i = 0 ; i < sheetMergeCount ; i++ ){
                CellRangeAddress ca = sheet.getMergedRegion(i);
                int firstColumn = ca.getFirstColumn();
                int lastColumn = ca.getLastColumn();
                int firstRow = ca.getFirstRow();
                int lastRow = ca.getLastRow();
                
                if(0 == firstRow && 0 == firstColumn){
                    dateCellStartIndex = lastColumn + 1;
                    break;
                }
            }
            
			for (i = 0; i <= rowNum; i++) {
				 row = sheet.getRow(i);
				 cellNum = row.getLastCellNum();
				 cellNum--; //兼容excel2007
				 
				 //读取日期行
				 if(i==0)
				 {
				    if(dateCellStartIndex > cellNum )
				        throw new GeneralException("请使用系统下载的模板");
				    
					for(int r=dateCellStartIndex; r<cellNum + 1; r++)
					{
						cell = row.getCell((short) r);
						if(cell==null)
							break;
						
						String q03z0 = cell.getStringCellValue();
						
						//zxj 20150925 增加日期校验
						if (q03z0 == null || "".equalsIgnoreCase(q03z0.trim()))
							throw new GeneralException("排班日期不能为空！请使用系统下载的模板，日期格式不可修改！");
						
						Date dQ03z0 = DateUtils.getDate(q03z0, "yyyy.MM.dd");
						if (dQ03z0 == null)
							dQ03z0 = DateUtils.getDate(q03z0, "yyyy-MM-dd");
						
						if (dQ03z0 == null)
							throw new GeneralException("排班日期格式不正确！请使用系统下载的模板，日期格式不可修改！");

						//linbz  20160721  已封存的排班日期不允许导入
						if(minDate != null)
							if(dQ03z0.before(minDate)){
								throw new GeneralException("排班日期"+q03z0+"已封存，不允许导入！");
							}
						
						q03z0 = DateUtils.format(dQ03z0, "yyyy.MM.dd");
						q03z0_list.add(q03z0.trim());
					}
					continue;
				 }
				 
				 if(i==1)
				 {
					 continue;
				 }
				 
				 cell = row.getCell((short) 0);
				 String str=cell.getStringCellValue();
				 if(str==null||str.length()<=0)
					 str="";
				 
				 if(str.indexOf("班次名称")!=-1&&str.indexOf("说明")!=-1)
				     break;
				 
				 for(int r=dateCellStartIndex;r<q03z0_list.size()+dateCellStartIndex;r++)
				 {
					 ArrayList list_o=new ArrayList();
					 cell = row.getCell((short) (dateCellStartIndex-3));
					 list_o.add(cell.getStringCellValue());
					 cell = row.getCell((short) (dateCellStartIndex-2));
					 list_o.add(cell.getStringCellValue());	
					 // 取第二行标题的物理列，因为第一行“日期”格有合并单元格，某些版本的文件会当成一列
					 if(r<=sheet.getRow(1).getPhysicalNumberOfCells())
					 {
						 cell = row.getCell((short)r);
						 // 36442 linbz 导入班次excel模板数据如果班次空白不填则默认为休息班
						 if(cell!=null && StringUtils.isNotEmpty(cell.toString())){
							 if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC)
							 {
								 list_o.add((int)cell.getNumericCellValue()+"");
							 }else
							 {
								 list_o.add(cell.getStringCellValue());
							 }
						 }else 
						   list_o.add("休息");
					 }else{
						 list_o.add("");						 
					 }
					 list_o.add(q03z0_list.get(r-dateCellStartIndex));  					 
					 dataList.add(list_o);	 
				 }
			}
			/**
			 * 校验excel文件中是否有重复数据，有则给出提示抛出异常
			 * 
			 */
			//重复卡号字段
			int num = 0;
			String cardStr = "";
			for (int k=0 ;k<dataList.size()-1;k++){       
	            for (int j=dataList.size()-1;j>k;j--){  
	            	ArrayList list_1=(ArrayList) dataList.get(k);
	            	String cardno1 = (String) list_1.get(1);
	            	String q03z01 = (String) list_1.get(3);
	            	
	            	ArrayList list_2=(ArrayList) dataList.get(j);
	            	String cardno2 = (String) list_2.get(1);
	            	String q03z02 = (String) list_2.get(3);
	            	
	            	if(cardno1.equalsIgnoreCase(cardno2) && q03z01.equalsIgnoreCase(q03z02)){
	            		if(StringUtils.isNotEmpty(cardno1) && !(","+cardStr).contains(","+cardno1+",")){
	            			cardStr += cardno1 + ",";
	            			num++;
	            			if(num%5==0 && num!=0)
	            				cardStr += "<br>";
	            		}
	            	}
	            }
			}
			if(StringUtils.isNotEmpty(cardStr)){
				throw new GeneralException("上传文件有重复数据！<br>重复的卡号是：<br>"+cardStr.substring(0, cardStr.length()-1));
			}
		} catch (GeneralException e) {
			throw GeneralExceptionHandler.Handle(e);
		} catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(new GeneralException("","上传文件错误！请使用系统下载的模板！","",""));
		}

        try {
            String sql = "insert into " + temp_Table + " (a0101,cardno,class_name,q03z0) values(?,?,?,?)";
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            dao.batchInsert(sql, dataList);
            //插入其他数据
            poiTempTable(temp_Table, kq_cardno);
            poiTempTableClassid(temp_Table);
            editEmpShit(temp_Table);
            KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn());
            kqUtilsClass.dropTable(temp_Table);
            this.formHM.put("checkClose", "close");            
        } catch (Exception e) {
            e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(new GeneralException("","上传文件错误！","",""));
        }
        
    }
    /**
     * 修改该临时表
     * @param temp_Table
     */
    private void poiTempTable(String temp_Table,String kq_cardno)
    {
        String destTab = temp_Table;//目标表
        String srcTab = "";//源表
        
        KqUtilsClass kqUtil = new KqUtilsClass(this.frameconn, this.userView);
        ArrayList list = null;
        try {
            list = kqUtil.getKqPreList();//this.userView.getPrivDbList();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        String nbase = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {
            for (int i = 0; i < list.size(); i++) {
                nbase = list.get(i).toString();
                srcTab = nbase + "A01";
                String strJoin = destTab + ".A0101=" + srcTab + ".A0101 and " + destTab + ".cardno=" + srcTab + "." + kq_cardno
                        + "";//关联串  xxx.field_name=yyyy.field_namex,....
                String strSet = destTab + ".a0100=" + srcTab + ".a0100`" + destTab + ".B0110=" + srcTab + ".B0110`" + destTab
                        + ".E0122=" + srcTab + ".E0122`" + destTab + ".E01A1=" + srcTab + ".E01A1`" + destTab + ".nbase='"
                        + nbase + "'";//更新串  xxx.field_name=yyyy.field_namex,....
                String strDWhere = destTab + ".a0100 is null";//destTab+".status='0'";//更新目标的表过滤条件
                String strSWhere = srcTab + ".a0100 in (select a0100 " + RegisterInitInfoData.getWhereINSql(userView, nbase) + ")";//源表的过滤条件  
                String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
                String othWhereSql = "";
                update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, othWhereSql);
                dao.update(update);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 修改临时表班次的
     * @param temp_Table
     */
    private void poiTempTableClassid(String temp_Table)
    {
        String destTab=temp_Table;//目标表
        String srcTab="kq_class";//源表
        String strJoin=destTab+".class_name="+srcTab+".name";
        String  strSet=destTab+".class_id="+srcTab+".class_id";
        String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,"","");            
        update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,"","");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            dao.update(update);
          //如果excel表格设置的班次为空，取消默认为休息班次的设置，直接删掉暂不考虑，
//            update="update "+destTab+" set class_id=0 where class_id is null";
//            dao.update(update);
            StringBuffer  deleteSQL=new StringBuffer();
			deleteSQL.append("delete from "+destTab);
			deleteSQL.append(" where class_id is null");
			ArrayList list=new ArrayList();
			dao.delete(deleteSQL.toString(), list);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 插入排班表
     * @param temp_Table
     */
    private void editEmpShit(String temp_Table)
    {
        String destTab="kq_employ_shift";//目标表
        String srcTab=temp_Table;//源表
        String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase and "+destTab+".q03z0="+srcTab+".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
        String  strSet=destTab+".class_id="+srcTab+".class_id`"+destTab+".a0101="+srcTab+".a0101";//更新串  xxx.field_name=yyyy.field_namex,....
//      String strDWhere=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//destTab+".status='0'";//更新目标的表过滤条件
        String strDWhere="";
        String strSWhere=" " + Sql_switcher.isnull(srcTab + ".cardno", "'##'") + "<>'##'";//源表的过滤条件  
        String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
//      String update="update "+destTab+" set "+strSet+" from "+srcTab+" left join "+destTab+" on "+strJoin;
        String othWhereSql="";
        update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql); 
        //System.out.println(update.toString());
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        try {           
            dao.update(update);
        } catch (Exception e) 
        {
            e.printStackTrace();
        }
        StringBuffer  insertSQL=new StringBuffer();
        insertSQL.append("INSERT INTO kq_employ_shift(nbase,A0100,A0101,B0110,E0122,E01A1,Q03Z0,class_id,status)");
        insertSQL.append(" SELECT a.nbase,a.A0100,a.A0101,a.B0110,a.E0122,a.E01A1,a.Q03Z0,a.class_id,0");
        insertSQL.append(" FROM "+temp_Table+" a ");
        insertSQL.append("WHERE NOT EXISTS(SELECT * FROM kq_employ_shift b");
        insertSQL.append(" WHERE a.A0100=b.A0100 and a.nbase=b.nbase and a.Q03Z0=b.Q03Z0 )");   
        insertSQL.append(" AND nbase IS NOT NULL");
        try {           
            ArrayList list=new ArrayList();     
            dao.insert(insertSQL.toString(),list);
        } catch (Exception e) {
            e.printStackTrace();            
        }
    }
    
    /**
     * 建立人员临时表
     * @param userView
     * @param conn
     * @return
     */
    private String tempEmpClassTable()
    {
        String table_name = "inputclass_temp_" + this.userView.getUserName();
        table_name = table_name.toLowerCase();
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
        DbWizard dbWizard = new DbWizard(this.getFrameconn());
        Table table = new Table(table_name);
        if (dbWizard.isExistTable(table_name, false)) {
            baseClassShift.dropTable(table_name);
        }
        
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("q03z0", "工作日期");
        temp.setDatatype(DataType.STRING);
        temp.setLength(20);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("B0110", "单位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("E0122", "部门");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("E01A1", "职位");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("A0101", "姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("class_id", "班次编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("class_name", "班次名称");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("flag", "标志");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("group_id", "组编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("cardno", "考勤卡号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("g_no", "工号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**重新加载数据模型*/

        DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
        dbmodel.reloadTableModel(table_name);
        
        return table_name;
    }
}
