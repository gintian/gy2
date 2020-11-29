package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.team.BaseClassShift;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * 输出摸版
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
 * Create time:Feb 3, 2008
 * </p>
 * 
 * @author sunxin
 * @version 4.0
 * 
 * <p>
 * 导出排班模板时，输出现有排班数据
 * </P>
 * @author zxj
 * @version 4.1
 * 
 * @modify zxj 2013-08-30
 * 精简重复代码，
 * 过滤暂停考勤人员数据，
 * 修正班组导出排班数据时nbase被转换成大写，导致无法导出的bug
 * 修正有多个未排班人员时，无法导出数据bug
 */
public class ExportExcelStencilTrans extends IBusiness {
    
    private String kqTypeWhr;

    public void execute() throws GeneralException {
        try {
            //暂停考勤人员条件
            kqTypeWhr = new KqUtilsClass(this.frameconn, this.userView).getKqTypeWhere(KqConstant.KqType.STOP, true);
            
            String a_code = (String) this.getFormHM().get("a_code");
            if (a_code != null && !"UN,UM,@K,GP,EP".contains(a_code.substring(0, 2)))
                a_code = PubFunc.decrypt(a_code);
            
            if ("GP".equalsIgnoreCase(a_code)) {
                a_code = RegisterInitInfoData.getKqPrivCode(userView) + RegisterInitInfoData.getKqPrivCodeValue(userView);
            }

            String nbase = (String) this.getFormHM().get("nbase");
            String start_date = (String) this.getFormHM().get("start_date");
            String end_date = (String) this.getFormHM().get("end_date");
            if (nbase == null || nbase.length() <= 0 || "0".equals(nbase))
                nbase = "all";

            ArrayList kq_dbase_list = new ArrayList();
            KqUtilsClass kqutilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
            String code = kqutilsClass.getCodeFormA_code(a_code);
            String kind = kqutilsClass.getKindFormA_code(a_code);

            if ("all".equals(nbase)) {
                if (code != null && code.length() > 0) {
                    if ("2".equals(kind)) {
                        kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(),
                                code);
                    } else {
                        String code_kind = RegisterInitInfoData.getDbB0100(code, kind, this.getFormHM(), this.userView, this
                                .getFrameconn());
                        kq_dbase_list = RegisterInitInfoData.getB0110Dase(this.getFormHM(), this.userView, this.getFrameconn(),
                                code_kind);
                    }
                } else {
                    kq_dbase_list = RegisterInitInfoData.getDase3(this.getFormHM(), this.userView, this.getFrameconn());
                }
            } else {
                kq_dbase_list.add(nbase);
            }

            ArrayList datelist = getDateList(start_date, end_date);
            if (datelist.size() > 256) 
            {
                throw new GeneralException(ResourceFactory.getProperty("kq.shift.export.excel.maxcols"));
            }
            KqParameter kq_paramter = new KqParameter(this.getFormHM(), this.userView, code, this.getFrameconn());
            String kq_cardno = kq_paramter.getCardno();
            String kq_gno = kq_paramter.getG_no();
            BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
            String t_table = baseClassShift.tempEmpClassTable();
            if (a_code != null && !"UN".equalsIgnoreCase(a_code)) {
                String codesetid = a_code.substring(0, 2);
                String codeitemid = a_code.substring(2);
                if ("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid)) {
                    shiftOrg(t_table, codesetid, codeitemid, kq_dbase_list, datelist, kq_cardno, kq_gno);
                } else if ("EP".equalsIgnoreCase(codesetid)) {
                    shift_employee(t_table, codesetid, codeitemid, nbase, datelist, kq_cardno, kq_gno);
                } else if ("GP".equalsIgnoreCase(codesetid)) {
                    shift_group(t_table, codesetid, codeitemid, datelist, kq_cardno, kq_gno, kq_dbase_list);
                }
            } else {
                ManagePrivCode managePrivCode = new ManagePrivCode(this.userView, this.getFrameconn());
                String codeitemid = managePrivCode.getPrivOrgId();
                // 初始进来值都为UN ,但是管理权限由可能是UM或者@K;修改
                String codesetid = RegisterInitInfoData.getKqPrivCode(userView);
                if (codeitemid != null && codeitemid.length() > 0) {
                    shiftOrg(t_table, codesetid, codeitemid, kq_dbase_list, datelist, kq_cardno, kq_gno);
                } else {
                    for (int i = 0; i < kq_dbase_list.size(); i++) {
                        String userbase = kq_dbase_list.get(i).toString();
                        String whereIN = RegisterInitInfoData.getWhereINSql(userView, userbase);
                        String whereB0110 = RegisterInitInfoData.selcet_OrgId(userbase, "b0110", whereIN);
                        ArrayList orgidb0110List = OrgRegister.getQrgE0122List(this.getFrameconn(), whereB0110, "b0110");
                        for (int t = 0; t < orgidb0110List.size(); t++) {
                            String b0110_one = orgidb0110List.get(t).toString();
                            nbase = RegisterInitInfoData.getOneB0110Dase(this.getFormHM(), this.userView, 
                                    userbase, b0110_one, this.getFrameconn());
                            if (nbase == null || nbase.length() <= 0) 
                                continue;
                            
                            ArrayList nlist = new ArrayList();
                            nlist.add(nbase);
                            shift_UNall(t_table, codesetid, b0110_one, nlist, datelist, kq_cardno, kq_gno); // 0017559bug
                        }
                    }
                }
            }
            
            String excelfile = "";
            if (t_table != null && t_table.length() > 0)
                excelfile = getExcelFieldname(t_table, datelist);
            //xiexd 2014.09.12加密文件名
            excelfile = PubFunc.encrypt(excelfile);
            this.getFormHM().put("excelfile", excelfile);
            baseClassShift.deleteTable(t_table);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    private void shiftOrg(String t_table, String codesetid, String codeitemid, ArrayList nbaselist, ArrayList date_list,
            String kq_card, String g_no) throws GeneralException {

        String org_str = "";
        if ("UN".equalsIgnoreCase(codesetid))
            org_str = "b0110";
        else if ("UM".equalsIgnoreCase(codesetid))
            org_str = "e0122";
        else
            org_str = "e01a1";

        //组织机构条件
        String sWhere = "and " + org_str + " LIKE '" + codeitemid + "%'";
        //考勤方式条件
        sWhere = sWhere + kqTypeWhr;

        //日期范围条件
        CommonData star_date = (CommonData) date_list.get(0);
        CommonData end_date = (CommonData) date_list.get(date_list.size() - 1);
        String whereD2 = "q03z0 BETWEEN '" + star_date.getDataName() + "' AND '" + end_date.getDataName() + "'";

        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
        for (int j = 0; j < nbaselist.size(); j++) {
            String nbase = nbaselist.get(j).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
            baseClassShift.synchronizationInitEmployee_Table(nbase, whereIN, whereD2, sWhere.substring(4));// 初始化
            baseClassShift.insrtTempEmpData(t_table, nbase, whereIN, sWhere, kq_card, g_no);
        }
    }

    // 全部的时候不用在查询孩子，在查出孩子插入的时候回出现 插入重复键错误
    private String shift_UNall(String t_table, String codesetid, String codeitemid, ArrayList nbaselist, ArrayList date_list,
            String kq_card, String g_no) throws GeneralException {
        String org_str = "b0110";
        
        //String sWhere = "and " + org_str + " LIKE '" + codeitemid + "%'";
        String sWhere = "and " + org_str + " = '" + codeitemid + "'";
        //考勤方式条件
        sWhere = sWhere + kqTypeWhr;
        
        CommonData star_date = (CommonData) date_list.get(0);
        CommonData end_date = (CommonData) date_list.get(date_list.size() - 1);
        String whereD2 = "q03z0 BETWEEN '" + star_date.getDataName() + "' AND '" + end_date.getDataName() + "'";
        
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
        for (int j = 0; j < nbaselist.size(); j++) {
            String nbase = nbaselist.get(j).toString();
            String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
            baseClassShift.synchronizationInitEmployee_Table(nbase, whereIN, whereD2, "");// 初始化
            baseClassShift.insrtTempEmpData(t_table, nbase, whereIN, sWhere, kq_card, g_no);
        }
        return t_table;
    }

    /**
     * 人员
     * 
     * @param codesetid
     * @param codeitemid
     * @param nbase
     * @param date_list
     * @param kq_card
     * @throws GeneralException
     */
    private String shift_employee(String t_table, String codesetid, String codeitemid, String nbase, ArrayList date_list,
            String kq_card, String g_no) throws GeneralException {
        if (nbase == null || nbase.length() <= 0)
            return "";
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
        String sWhere = "and a0100='" + codeitemid + "'";
        String whereIN = RegisterInitInfoData.getWhereINSql(this.userView, nbase);
        baseClassShift.insrtTempEmpData(t_table, nbase, whereIN, sWhere, kq_card, g_no);
        return t_table;

    }

    private String shift_group(String t_table, String codesetid, String codeitemid, ArrayList date_list, String kq_card,
            String g_no, ArrayList kq_dbase_list) throws GeneralException {
        BaseClassShift baseClassShift = new BaseClassShift(this.userView, this.getFrameconn());
        baseClassShift.synchronizationInitGtoupEmployee_Table(codeitemid);// 初始化
        insrtGroupTempData1(t_table, codeitemid, kq_card, g_no, kq_dbase_list);
        return t_table;

    }

    private void insrtGroupTempData1(String t_table, String group_id, String cardno, String g_no, ArrayList kq_dbase_list)
            throws GeneralException {
        StringBuffer insertSql = new StringBuffer();
        String srcTab = "kq_group_emp";// 源表
        String insetWhere = "";
        if (group_id != null && group_id.length() > 0) {
            insetWhere = "group_id='" + group_id + "'";
        } else {
            return;
        }
        
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        StringBuffer join = new StringBuffer();
        String expres = this.userView.getPrivExpression();
        for (Iterator it = kq_dbase_list.iterator(); it.hasNext();) {
            String nbase = (String) it.next();
            String whereIN = "";
            if (expres.length() > 0) {
                whereIN = userView.getPrivSQLExpression(expres, nbase, false, new ArrayList());
            } else {
                whereIN = getWhereINSql(userView, nbase);
            }
            
            if (!whereIN.toUpperCase().contains(" WHERE "))
                whereIN = whereIN + " WHERE 1=1 ";
            whereIN = whereIN + kqTypeWhr;
            
            if (join.length() > 0) {
                join.append(" UNION SELECT '" + nbase + "' nbase, A0000, A0100, B0110, E0122, E01A1, A0101," + cardno
                        + " cardno," + g_no + " g_no" + whereIN);
            } else {
                join.append("SELECT '" + nbase + "' nbase, A0000, A0100, B0110, E0122, E01A1, A0101," + cardno
                        + " cardno," + g_no + " g_no" + whereIN);
            }
        }
        //29392 linbz 在新建临时表时增加字段A0000 所以在添加数据的同时也要同步加上
        insertSql.append("INSERT INTO " + t_table + "(nbase, A0000, A0100, B0110, E0122, E01A1, A0101,cardno,g_no,group_id) ");
        insertSql.append("SELECT  A.nbase,A.A0000,A.A0100,A.B0110," + Sql_switcher.isnull("A.E0122", "''") + ", "
                + Sql_switcher.isnull("A.E01A1", "''") + ",A.A0101,cardno,g_no,group_id");
        insertSql.append(" FROM " + srcTab + " kq LEFT JOIN (" + join.toString()
                + ") A ON kq.A0100 = A.A0100 AND kq.nbase = A.nbase");
        insertSql.append(" WHERE " + insetWhere + " AND COALESCE(cardno,'0') <> '0'");
        try {
            dao.update(insertSql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**根据权限,生成select.IN中的查询串
    * @param code
    *        链接级别
    * @param userbase
    *        库前缀
    * @param cur_date
    *        考勤日期
    * @return 返回查询串
    * */
    private String getWhereINSql(UserView userView, String userbase) {
        String strwhere = "";
        if (!userView.isSuper_admin()) {
            String expr = "1";
            String factor = "";
            if ("UN".equals(userView.getManagePrivCode())) {
                factor = "B0110=";
                if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0) {
                    factor += userView.getManagePrivCodeValue();
                    factor += "%`";
                } else {
                    factor += "%`B0110=`";
                    expr = "1+2";
                }
            } else if ("UM".equals(userView.getManagePrivCode())) {
                factor = "E0122=";
                if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0) {
                    factor += userView.getManagePrivCodeValue();
                    factor += "%`";
                } else {
                    factor += "%`E0122=`";
                    expr = "1+2";
                }
            } else if ("@K".equals(userView.getManagePrivCode())) {
                factor = "E01A1=";
                if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0) {
                    factor += userView.getManagePrivCodeValue();
                    factor += "%`";
                } else {
                    factor += "%`E01A1=`";
                    expr = "1+2";
                }
            } else {
                expr = "1+2";
                factor = "B0110=";
                if (userView.getManagePrivCodeValue() != null && userView.getManagePrivCodeValue().length() > 0)
                    factor += userView.getManagePrivCodeValue();
                factor += "%`B0110=`";
            }
            ArrayList fieldlist = new ArrayList();
            try {

                /**表过式分析*/
                /**非超级用户且对人员库进行查询*/
                if (userView.getKqManageValue() != null && !"".equals(userView.getKqManageValue()))
                    strwhere = userView.getKqPrivSQLExpression("", userbase, fieldlist);
                else
                    strwhere = userView.getPrivSQLExpression(expr + "|" + factor, userbase, false, fieldlist);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            StringBuffer wheresql = new StringBuffer();
            wheresql.append(" from ");
            wheresql.append(userbase);
            wheresql.append("A01 ");
            strwhere = wheresql.toString();
        }

        return strwhere;
    }

    /**
     * 生成摸版
     * 
     * @param table_n
     * @param datelist
     * @return
     */
    private String getExcelFieldname(String table_n, ArrayList datelist) {
        // 60683 模板统一命名为： 登陆用户_相应信息
        String excel_filename = userView.getUserName() + "_" + ResourceFactory.getProperty("kq.init.shift").trim()+".xls";

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        short n = 0;
        /***/
        n = executeTableTitel(datelist, sheet, workbook);// 表头
        n = executeTableDate(table_n, n, sheet, workbook, datelist);// 人员信息
        short cells = (short) (3 + datelist.size());
        executeTableDate(cells, n, sheet, workbook);
        // 写下拉信息
        executeTableClassSelect(cells, n, sheet, workbook);
        try {
            String fileFullName = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + excel_filename;
            FileOutputStream fileOut = new FileOutputStream(fileFullName);
            workbook.write(fileOut);
            fileOut.close();
            sheet = null;
            workbook = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return excel_filename;
    }

    /**
     * 表格头
     * 
     * @param title
     * @param sheet
     * @param workbook
     * @return
     */
    private short executeTableTitel(ArrayList datelist, HSSFSheet sheet, HSSFWorkbook workbook) {
    	short n = 0;
    	try {
    		HSSFRow row = null;
    		HSSFCell csCell = null;
    		// 写标题
    		HSSFFont font = workbook.createFont();
    		font.setColor(HSSFFont.COLOR_NORMAL);
    		font.setBold(true);
    		HSSFCellStyle cellStyle = workbook.createCellStyle();
    		cellStyle.setFont(font);
    		cellStyle.setAlignment(HorizontalAlignment.RIGHT);
    		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    		row = sheet.createRow(n);
    		row.setHeight((short) (40 * 10));
    		csCell = row.createCell(Integer.parseInt(String.valueOf(0)));
    		csCell.setCellStyle(cellStyle);
    		
    		csCell.setCellValue("日期");
    		int a = 0;
    		int b = 0;
    		int c = 0;
    		int d = 3;
    		int b1 = b;
    		while (++b1 <= d) {
    			csCell = row.createCell(b1);
    		}
    		for (int a1 = a + 1; a1 <= c; a1++) {
    			row = sheet.createRow(a1);
    			b1 = b;
    			while (b1 <= d) {
    				csCell = row.createCell(b1);
    				b1++;
    			}
    		}
    		ExportExcelUtil.mergeCell(sheet, a, b, c, d);
    		for (int i = 0; i < datelist.size(); i++) {
    			csCell = row.createCell(i + 4);
    			cellStyle.setAlignment(HorizontalAlignment.CENTER);
    			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    			csCell.setCellStyle(cellStyle);
    			
    			CommonData vo = (CommonData) datelist.get(i);
    			csCell.setCellValue(vo.getDataName());
    			sheet.setColumnWidth(i + 4, 300 * 10);
    		}
    		n++;
    		row = sheet.createRow(n);
    		row.setHeight((short) (40 * 10));
    		
    		csCell = row.createCell(0);
    		cellStyle.setAlignment(HorizontalAlignment.LEFT);
    		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    		csCell.setCellStyle(cellStyle);
    		csCell.setCellValue("部门");
    		
    		csCell = row.createCell(1);
    		cellStyle.setAlignment(HorizontalAlignment.CENTER);
    		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    		csCell.setCellStyle(cellStyle);
    		csCell.setCellValue("姓名");
    		
    		csCell = row.createCell(2);
    		cellStyle.setAlignment(HorizontalAlignment.CENTER);
    		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    		csCell.setCellStyle(cellStyle);
    		csCell.setCellValue("卡号");
    		
    		csCell = row.createCell(3);
    		cellStyle.setAlignment(HorizontalAlignment.CENTER);
    		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    		csCell.setCellStyle(cellStyle);
    		csCell.setCellValue("工号");
    		
    		
    		for (int i = 0; i < datelist.size(); i++) {
    			csCell = row.createCell(i + 4);
    			cellStyle.setAlignment(HorizontalAlignment.CENTER);
    			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    			csCell.setCellStyle(cellStyle);
    			
    			CommonData vo = (CommonData) datelist.get(i);
    			csCell.setCellValue(vo.getDataValue());
    			sheet.setColumnWidth(i + 3, (300) * 10);
    		}
    		
    		n++;
    	}catch (Exception e) {
    		e.printStackTrace();
		}
        return n;
    }

    private short executeTableDate(String table_n, short n, HSSFSheet sheet, HSSFWorkbook workbook, ArrayList datelist) {
        if (datelist == null || datelist.size() == 0)
            return n;

        String datelistTab = "";
        try {
            datelistTab = this.createDateListTab(datelist);

            String startDate = ((CommonData) datelist.get(0)).getDataName();
            String endDate = ((CommonData) datelist.get(datelist.size() - 1)).getDataName();

            StringBuilder sql = new StringBuilder();
            sql.append("select * from (");
            sql.append("select A.Q03Z0,B.nbase,B.A0100,B.A0101,B.cardno,B.g_no,B.a0000,B.e0122,C.name");
            sql.append(" from kq_employ_shift A right join "); // inner join 改为 right join 解决未排班人员到处模板没有数据问题   13/6/25
            sql.append(" (select nbase,A0100,A0101,cardno,g_no,str_date,a0000,e0122,b0110,e01a1 from " + table_n + "," + datelistTab + ") B");
            sql.append(" on A.A0100=B.A0100 and A.nbase=B.nbase and A.Q03Z0=B.str_date");
            sql.append(" left join kq_class C");
            sql.append(" on A.class_id=C.class_id");
            sql.append(" where " + Sql_switcher.isnull("A.Q03Z0", "'" + startDate + "'") + ">=" + "'" + startDate + "' ");
            sql.append(" and " + Sql_switcher.isnull("A.Q03Z0", "'" + endDate + "'") + "<=" + "'" + endDate + "'");
            sql.append(") AA");
            sql.append(" where nbase IS NOT NULL AND a0100 IS NOT NULL");
            sql.append(" order by nbase,a0000,a0100,q03z0");

            HSSFRow row = null;
            HSSFCell csCell = null;
            HSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            String preNbase = "";
            String preA0100 = "";
            String nbase = "";
            String a0100 = "";
            String q03z0 = "";
            String shiftName = "";
            String e0122 = "";
            int num = 0;
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql.toString());

            while (this.frowset.next()) {

                nbase = this.frowset.getString("nbase");
                a0100 = this.frowset.getString("a0100");
                q03z0 = this.frowset.getString("q03z0");
                shiftName = this.frowset.getString("name");

                if (!preA0100.equals(a0100) || !preNbase.equals(nbase)) {
                    row = sheet.createRow(n);
                    row.setHeight((short) (40 * 10));
                    
                    csCell = row.createCell(0);
                    csCell.setCellStyle(cellStyle);
                    e0122 = this.frowset.getString("e0122");
                    csCell.setCellValue(AdminCode.getCodeName("UM", e0122));

                    csCell = row.createCell(1);
                    csCell.setCellStyle(cellStyle);
                    csCell.setCellValue(this.frowset.getString("a0101"));

                    csCell = row.createCell(2);
                    csCell.setCellStyle(cellStyle);
                    csCell.setCellValue(this.frowset.getString("cardno"));

                    csCell = row.createCell(3);
                    csCell.setCellStyle(cellStyle);
                    csCell.setCellValue(this.frowset.getString("g_no"));

                    preNbase = nbase;
                    preA0100 = a0100;

                    n++;
                    num = 0;
                }
                // 34900 如果日期为空 说明没有排班，则不需要考虑导出的内容，直接空单元格即可
                if (q03z0 == null) {
                	continue;
//                	q03z0 = ((CommonData) datelist.get(num)).getDataName();
                }
                // 已有排班的日期  会获取日期集合中的下标
                int colno = getIndexOfDateInList(datelist, q03z0);
                if (colno == -1)
                    continue;

                csCell = row.createCell(colno + 4);
                csCell.setCellStyle(cellStyle);
                csCell.setCellValue(shiftName);
                num++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbWizard dbWizard = new DbWizard(this.getFrameconn());
            if (dbWizard.isExistTable(datelistTab, false)) {
                dbWizard.dropTable(datelistTab);
            }
        }

        return n++;
    }

    private int getIndexOfDateInList(ArrayList datelist, String strDate) {
        if (datelist == null || datelist.size() == 0)
            return -1;

        for (int i = 0; i < datelist.size(); i++) {
            CommonData adate = (CommonData) datelist.get(i);
            if (strDate.equals(adate.getDataName())) {
                return i;
            }
        }

        return -1;
    }

    private String createDateListTab(ArrayList datelist) {
        String table_name = "t#" + userView.getUserName() + "_kq_dl";
        table_name = table_name.toLowerCase();
        DbWizard dbWizard = new DbWizard(this.getFrameconn());
        Table table = new Table(table_name);
        if (dbWizard.isExistTable(table_name, false)) {
            dbWizard.dropTable(table_name);
        }

        Field temp = new Field("str_date", "date");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.createTable(table);

            //添加数据到日期临时表
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            for (int i = 0; i < datelist.size(); i++) {
                String aDate = ((CommonData) datelist.get(i)).getDataName();
                String sql = "insert into " + table_name + " values('" + aDate + "')";
                dao.update(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return table_name;
    }

    /**
     * 表尾说明
     * 
     * @param cells
     * @param n
     * @param sheet
     * @param workbook
     */
    private void executeTableDate(short cells, short n, HSSFSheet sheet, HSSFWorkbook workbook) {
    	try {
    		HSSFRow row = null;
    		HSSFCell csCell = null;
    		HSSFCellStyle cellStyle = workbook.createCellStyle();
    		cellStyle.setAlignment(HorizontalAlignment.LEFT);
    		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    		HSSFFont font = workbook.createFont();
    		font.setBold(true);
    		row = sheet.createRow(n);
    		csCell = row.createCell(0);
    		cellStyle.setFont(font);
    		csCell.setCellStyle(cellStyle);
    		StringBuffer str = new StringBuffer();
    		str.append("班次名称：");
    		str.append(getClassName());
    		str.append("     ");
    		str.append("\r\n(说明:1、直接填写班次名称，空白不填则默认为休息班；2、需要检验导入的班次是否存在于班次表中,如果不存在则无法导入\r\n");
    		str.append("3、可以自己增加人员，但是人员姓名和卡号必须与数据库定义的一致；4、排班人员必须有对应的唯一卡号，卡号不能重复！)");
    		str.append("\r\n说明下面的班次请勿删除");
    		csCell.setCellValue(str.toString());
    		row.setHeight((short) (70 * 10));
    		int a = n;
    		short b = 0;
    		int c = n;
    		short d = cells;
    		int b1 = b;
    		while (++b1 <= d) {
    			csCell = row.createCell(b1);
    		}
    		for (int a1 = a + 1; a1 <= c; a1++) {
    			row = sheet.createRow(a1);
    			b1 = b;
    			while (b1 <= d) {
    				csCell = row.createCell(b1);
    				b1++;
    			}
    		}
    		
    		ExportExcelUtil.mergeCell(sheet, a, b, c, d);
    	} catch (Exception e) {
    		e.printStackTrace();
		}
    }

    /**
     * 得到班次名称
     * 
     * @return
     */
    private String getClassName() {
//        StringBuffer sql = new StringBuffer();
//        sql.append("select name from kq_class");
          StringBuffer str = new StringBuffer();
//        ContentDAO dao = new ContentDAO(this.getFrameconn());
//        try {
//            this.frowset = dao.search(sql.toString());
//            int i = 1;
        KqUtilsClass kqcl = new KqUtilsClass(this.frameconn,this.userView);
        ArrayList list = new ArrayList();
        list = kqcl.getKqClassListInPriv();
        LazyDynaBean ldb = new LazyDynaBean();
       
        try {
            for(int i=0;i<list.size();i++){
                ldb = (LazyDynaBean) list.get(i);
                str.append(i+1+ "、" +(String)ldb.get("name") + "; ");
            }
            if (str != null && str.length() > 0)
                str.setLength(str.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    private ArrayList getDateList(String start_date, String end_date) {
        KqUtilsClass kqUtilsClass = new KqUtilsClass();
        start_date = start_date.replaceAll("\\.", "-");
        end_date = end_date.replaceAll("\\.", "-");
        Date da1 = DateUtils.getDate(start_date, "yyyy-MM-dd");
        Date da2 = DateUtils.getDate(end_date, "yyyy-MM-dd");
        int num = RegisterDate.diffDate(da1, da2);
        ArrayList list = new ArrayList();
        CommonData vo = null;
        try {
            for (int m = 0; m <= num; m++) {
                String op_date_to = kqUtilsClass.getDateByAfter(da1, m);
                Date date = DateUtils.getDate(op_date_to, "yyyy.MM.dd");

                vo = new CommonData();
                vo.setDataName(op_date_to);

                String weekName = KqUtilsClass.getWeekName(date);
                vo.setDataValue(weekName);

                list.add(vo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private void executeTableClassSelect(short cells, short n, HSSFSheet sheet, HSSFWorkbook workbook) {
        HSSFRow row = null;
        HSSFCell csCell = null;
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        KqUtilsClass kqcl = new KqUtilsClass(this.frameconn,this.userView);
        ArrayList list = new ArrayList();
        list = kqcl.getKqClassListInPriv();
        LazyDynaBean ldb = new LazyDynaBean();
       
        try {
            for(int i=0;i<list.size();i++){
                ldb = (LazyDynaBean) list.get(i);
                String classValue = (String)ldb.get("classId");
           
                    row = sheet.createRow(m);
                    csCell = row.createCell(0);
                    csCell.setCellValue((String)ldb.get("name"));// 考勤班次号
                    m++;
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        String strFormula = "$A$" + s + ":$A$" + (m) + ""; // 表示AA列1-2行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(2, n, 4, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
    }

}
