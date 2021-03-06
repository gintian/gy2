package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 培训班/培训课程导入模板导出
 */
public class ExportClassInOutExcelTrans extends IBusiness {
	private Connection conn=null;
	private int index = 0;
	private HSSFSheet codesetSheet=null;
	public ExportClassInOutExcelTrans() {
		this.conn = this.frameconn;
	}

	public void execute() throws GeneralException {
		String outName="";
		String items="";
		String fieldsetidlist="";
		String selectitemslist="";
		
		try{
			LazyDynaBean bean=(LazyDynaBean)this.getFormHM().get("obj");
			String fieldsetid=(String)bean.get("fieldsetid");
			String fsid=fieldsetid;
			String selectitems = (String)bean.get("selectitems");
			if(fsid.length()<1){//多信息集时，将信息中的fieldsetid和selectitems分隔开
				items = (String)bean.get("selectitems");
				String ims[] =(items).split("#");
				int i = ims.length;
				for(int j=1;j<i;j++){
					String is[] = (ims[j]).split(":");
					fieldsetidlist=fieldsetidlist+is[0]+",";
					selectitemslist=selectitemslist+is[1]+":";
				}
				fieldsetid=fieldsetidlist;
				selectitems=selectitemslist;
			}
			if(selectitems.length()<1){
				throw new Exception(ResourceFactory.getProperty("workbench.info.selectitems.msg"));
			}
			selectitems=selectitems.replaceAll("`", ",");
			StringBuffer sbsql=new StringBuffer();
			String primarykey="r3130";//模板中默认添加指标
			outName=this.creatExcel(sbsql.toString(),primarykey,selectitems,fieldsetid,fsid);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * 创建Excel 工作簿
	 * @param string
	 * @param primarykey
	 * @param selectitems
	 * @param fieldsetid
	 * @param fsid
	 * @return
	 * @throws Exception
	 */
	private String creatExcel(String string, String primarykey, String selectitems, String fieldsetid, String fsid) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		if(fsid.length()<1){
			String ffid = "";
			String sss = "";
			String fid[] =(fieldsetid).split(",");
			String selects[] =(selectitems).split(":");
			for(int i=0;i<fid.length;i++){//如果有R31表，则R31排在首位
				if("R31".equals(fid[i])){
					for(int j =i;j>0;j--){
						ffid = fid[j];
						fid[j] = fid[j-1];
						fid[j-1] = ffid;
						sss = selects[j];
						selects[j]=selects[j-1];
						selects[j-1] = sss;
					}
				}
			}
			for(int i=0;i<fid.length;i++){
				fieldsetid = fid[i];
				selectitems = selects[i];
				String strs[]=(primarykey+selectitems).split(",");
				ArrayList list = new ArrayList();
				ArrayList fieldList = new ArrayList();
				fieldList = dataList(fieldsetid);
				for (int j = 0; j < strs.length; j++) {
					for (int n = 0; n < fieldList.size(); n++) {
						FieldItem fi = (FieldItem) fieldList.get(n);
						if (j == 0 && n == 0) {
							list.add((FieldItem) DataDictionary.getFieldItem("r3130"));
							if("R31".equalsIgnoreCase(fieldsetid)){
								list.add((FieldItem) DataDictionary.getFieldItem("b0110","r31"));
							}
						}
						if ((j != 0 && strs[j].equalsIgnoreCase(primarykey))|| "r3130".equalsIgnoreCase(fi.getItemid())) {
							continue;
						}

						if (strs[j].equals(fi.getItemid()))
							list.add(fi);
					}
				}
				creatSheet(selectitems,fieldsetid,list,wb);
			}
		}else{
			String strs[]=(primarykey+selectitems).split(",");
			ArrayList list = new ArrayList();
			ArrayList fieldList = new ArrayList();
			fieldList = dataList(fieldsetid);
			for (int j = 0; j < strs.length; j++) {
				for (int n = 0; n < fieldList.size(); n++) {
					FieldItem fi = (FieldItem) fieldList.get(n);
					if (j == 0 && n == 0) {
						list.add((FieldItem) DataDictionary.getFieldItem("r3130"));
						if("R31".equalsIgnoreCase(fieldsetid)){
							list.add((FieldItem) DataDictionary.getFieldItem("b0110","r31"));
						}
					}
					if ((j != 0 && strs[j].equalsIgnoreCase(primarykey))|| "r3130".equalsIgnoreCase(fi.getItemid())) {
						continue;
					}
					if (strs[j].equals(fi.getItemid()))
						list.add(fi);
				}
			}
			creatSheet(selectitems,fieldsetid,list,wb);
		}
		String outName = this.userView.getUserName() + "_train.xls";
		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
		}
		wb = null;
		return outName;
	}
	/**
	 * 创建Excel 工作表
	 * @param selectitems
	 * @param fieldsetid
	 * @param list
	 * @param wb
	 * @throws Exception
	 */
	private void creatSheet(String selectitems, String fieldsetid, ArrayList list, HSSFWorkbook wb) throws Exception {
		String fieldsetdesc="";
		ContentDAO dao1 = new ContentDAO(this.frameconn);
		String sql = "select fieldsetdesc from t_hr_busiTable where fieldsetid='"+fieldsetid+"'";
		RowSet rs;
		try {
			rs = dao1.search(sql);
			if(rs.next()){
				fieldsetdesc=rs.getString("fieldsetdesc");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String SheetName = fieldsetdesc+"("+fieldsetid+")"; 
		HSSFSheet sheet = wb.createSheet(SheetName);
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font2);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setBottomBorderColor((short) 8);
		style2.setLeftBorderColor((short) 8);
		style2.setRightBorderColor((short) 8);
		style2.setTopBorderColor((short) 8);
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBottomBorderColor((short) 8);
		style1.setLeftBorderColor((short) 8);
		style1.setRightBorderColor((short) 8);
		style1.setTopBorderColor((short) 8);
		style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
		styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
		styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		
		HSSFRow row =sheet.getRow(0);
		if(row==null){
			row=sheet.createRow(0);
		}
		HSSFCell cell = null;
		HSSFComment comm = null;

		ArrayList codeCols = new ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
			FieldItem field = (FieldItem) list.get(i);
			String fieldName = field.getItemid().toLowerCase();
			String fieldLabel = field.getItemdesc();
			
			sheet.setColumnWidth((i), 3500);
			cell=row.getCell(i);
			if(cell==null)
				cell=row.createCell(i);

			if("b0110".equalsIgnoreCase(fieldName) || i == 0 || field.isFillable())
				fieldLabel=fieldLabel+"*";
			cell.setCellValue(cellStr(fieldLabel));
			cell.setCellStyle(style2);
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
			if(i==0){
				comm.setString(new HSSFRichTextString(fieldName+"`"+fieldsetid));
			}else{
				comm.setString(new HSSFRichTextString(fieldName));
			}
			
			cell.setCellComment(comm);
			if ("A".equalsIgnoreCase(field.getItemtype())&&(field.getCodesetid()!=null&&!"".equals(field.getCodesetid())&&!"0".equals(field.getCodesetid()))){
				if("r4117".equalsIgnoreCase(field.getItemid()) || "r4118".equalsIgnoreCase(field.getItemid()))
					continue;
				else 
					codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
			}
		}
		
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			int rowCount = 1;
			ArrayList styleList = new ArrayList();
			while(rowCount<1001)
			{
				row =sheet.getRow(rowCount);
				if(row==null){
					row=sheet.createRow(rowCount);
				}
				for (int i = 0; i < list.size(); i++)
				{
					HSSFCellStyle style = null;
					FieldItem field = (FieldItem) list.get(i);
					String itemtype = field.getItemtype();
					int decwidth = field.getDecimalwidth();
					
					if(rowCount == 1){
						if("N".equals(itemtype))
							style = styleN(decwidth, wb);
						styleList.add(style);
					}
					cell = row.getCell(i);
					if(cell==null)
						cell=row.createCell(i);
					if ("N".equals(itemtype)) {
						cell.setCellStyle((HSSFCellStyle)styleList.get(i));
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue("");
					} else {
						cell.setCellStyle(style1);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					}

				}
				rowCount++;
			}
			rowCount--;
			
			String codesetSheetName = "hjehr_codeset_"+index/255; 
			if(codeCols.size()>0){
				if(index%255==0){//当sheet列数满255时，重新生成一个sheet
					codesetSheet = wb.createSheet(codesetSheetName);
				}
				wb.setSheetHidden(wb.getSheetIndex(codesetSheet), true);
				codesetSheet.setColumnWidth((index),0);
			}
			String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			for (int n = 0; n < codeCols.size(); n++)
			{
				int m = 0;
				String codeCol = (String) codeCols.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();
				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'");
					
					this.frowset = dao.search(codeBuf.toString());
					if(this.frowset.next()){
						if(this.frowset.getInt(1)<200){
							codeBuf.setLength(0);
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "' order by codeitemid");
						}else{
							continue;
						}
					}
				} else
				{//生成下拉列表
					if (!"UN".equals(codesetid)){
						m=loadorg(codesetSheet,row,cell,index,m,dao,codesetid);
					}else if ("UN".equals(codesetid))
					{
						codeBuf.setLength(0);
						if(this.userView.isSuper_admin()){
							codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
									+ "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
						}else{
							TrainCourseBo bo = new TrainCourseBo(this.userView);
							String manpriv = bo.getUnitIdByBusi();
							String code = "";
							StringBuffer tmpstr=new StringBuffer();
							if (manpriv != null && manpriv.trim().length() > 2 && manpriv.indexOf("UN`")==-1) {
								String[] tmp = manpriv.split("`");
								StringBuffer e0122s=new StringBuffer();
								for(int i=0;i<tmp.length;i++){
									code = tmp[i];
									if ("UN".equalsIgnoreCase(code.substring(0, 2))){
									    if(i>0)
	                                        tmpstr.append(" or ");
									
										tmpstr.append("codeitemid like '" + code.substring(2, code.length()) + "%'");
									}else{
									    e0122s.append("'"+code.substring(2, code.length())+"',");
									}
								}
								
								if(tmpstr == null || tmpstr.length() < 1)
								    tmpstr.append("codeitemid in(" + getB0110(e0122s.subSequence(0, e0122s.length()-1).toString()) + ")");
								
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
										+ "' and ("+tmpstr+") and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by grade,a0000,codeitemid");
							}else if(manpriv.indexOf("UN`")!=-1)
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
										+ "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by grade,a0000,codeitemid");
							else
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
						}
					}
				}
				
				if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					
					this.frowset = dao.search(codeBuf.toString());
					while (this.frowset.next())
					{
						row = codesetSheet.getRow(m + 0);
						if (row == null)
							row = codesetSheet.createRow(m + 0);
						
						cell = row.createCell((index));
						
						if("UN".equals(codesetid)){
							int grade=this.frowset.getInt("grade");
							StringBuffer sb=new StringBuffer();
							sb.setLength(0);
							for(int i=1;i<grade;i++){
								sb.append("  ");
							}
							cell.setCellValue(new HSSFRichTextString(sb.toString()+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")"));
						}else{
							cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
						}
						m++;
					}
				}
				if(m==0){
					continue;
				}
				// 放到单独页签
				String strFormula ="";
				if(index<=25){
					strFormula = codesetSheetName + "!$" + lettersUpper[index] + "$1:$" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
				}else if(index>25){
					strFormula = codesetSheetName + "!$" + lettersUpper[index/26-1] + lettersUpper[index%26] 
					           + "$1:$" + lettersUpper[index/26-1] + lettersUpper[index%26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
				}
				CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);//rowCount
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);
				index++;
			}

		} catch (SQLException e1)
		{
			e1.printStackTrace();
		}
		
	}

	public HSSFRichTextString cellStr(String context)
	{

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public String decimalwidth(int len)
	{

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
			decimal.append(".");
		for (int i = 0; i < len; i++)
		{
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}
	/**
	 * 单元格样式
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
	{

		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBottomBorderColor((short) 8);
		style.setLeftBorderColor((short) 8);
		style.setRightBorderColor((short) 8);
		style.setTopBorderColor((short) 8);
		return style;
	}
	/**
	 * 生成下拉列表
	 * @param sheet
	 * @param row
	 * @param cell
	 * @param index
	 * @param m
	 * @param dao
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private int loadorg(HSSFSheet sheet,HSSFRow row,HSSFCell cell,int index,int m,ContentDAO dao,String type) throws Exception {
		Statement st=null;
		ResultSet rs = null;
		try{
			String sql="";
			if(this.userView.isSuper_admin()){
				sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codesetid<>'@K' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}else{
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				String manpriv = bo.getUnitIdByBusi();
				String code = "";
				StringBuffer tmpstr=new StringBuffer();
				if (manpriv != null && manpriv.trim().length() > 2 && manpriv.indexOf("UN`")==-1) {
					String[] tmp = manpriv.split("`");
					for(int i=0;i<tmp.length;i++){
						code = tmp[i];
						if(i>0)
							tmpstr.append(" or ");
						if ("UN".equalsIgnoreCase(code.substring(0, 2)) || "UM".equalsIgnoreCase(code.substring(0, 2)))
							tmpstr.append("codeitemid like '" + code.substring(2, code.length()) + "%'");
					}
					sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where ("+tmpstr+") and codesetid<>'@K' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
				} else if(manpriv.indexOf("UN`")!=-1)
					sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codesetid<>'@K' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
				else
					sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where 1=2";
			}
			rs=dao.search(sql);
			String codeitemid="";
			String codeitemdesc="";
			int grade=0;
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				codeitemdesc=rs.getString("codeitemdesc");
				grade=rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				// 放到单独页签
				cell = row.createCell((index));
				StringBuffer sb=new StringBuffer();
				sb.setLength(0);
				for(int i=1;i<grade;i++){
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString()+codeitemdesc+"("+codeitemid+")"));
				m++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				rs.close();
			}
			if(st!=null){
				st.close();
			}
		}
		return m;
	}
	/**
	 * 获取业务字典中的指标
	 * @param fieldsetid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList dataList(String fieldsetid) throws GeneralException{
		ArrayList list = new ArrayList();
		list = DataDictionary.getFieldList(fieldsetid, 1);
		return list;
	}
	/**
	 * 数值类型指标的单元格格式
	 * @param i
	 * @param wb
	 * @return
	 */
	public HSSFCellStyle styleN(int i, HSSFWorkbook wb) {
		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.CENTER);
		styleN.setVerticalAlignment(VerticalAlignment.CENTER); 
		styleN.setWrapText(true);
		styleN.setBorderBottom(BorderStyle.THIN);
		styleN.setBorderLeft(BorderStyle.THIN);
		styleN.setBorderRight(BorderStyle.THIN);
		styleN.setBorderTop(BorderStyle.THIN);
		styleN.setBottomBorderColor((short) 8);
		styleN.setLeftBorderColor((short) 8);
		styleN.setRightBorderColor((short) 8);
		styleN.setTopBorderColor((short) 8);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(i)));
		return styleN;
	}
	/**
	 * 获取部门的所属单位
	 * @param e0122s
	 * @return
	 */
    private String getB0110(String e0122s) {
        String b0110 = "";
        try {
            String[] e0122 = e0122s.split(",");
            for (int i = 0; i < e0122.length; i++) {

                if (e0122[i] == null || e0122[i].trim().length() < 1)
                    continue;
                
                List savePos = getStationPos(e0122[i]);
                for (int n = 0; n < savePos.size(); n++) {
                    StationPosView posview = (StationPosView) savePos.get(n);
                    if (!"b0110".equalsIgnoreCase(posview.getItem()))
                        continue;

                    b0110 += "'" + posview.getItemvalue() + "',";
                    break;
                }
            }

            if(b0110 !=null && b0110.length() > 0)
                b0110 = b0110.substring(0, b0110.length()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b0110;
    }
	/**
	 * 获取部门的所属单位
	 * @param code
	 * @return
	 */
    private ArrayList getStationPos(String code) {
        ArrayList poslist = new ArrayList();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean isdep = false;
        boolean isorg = false;
        StringBuffer strsql = new StringBuffer();
        try {
            String pre = "UM";
            conn = this.getFrameconn();
            ContentDAO db = new ContentDAO(conn);
            while (!"UN".equalsIgnoreCase(pre)) {
                if (strsql != null && strsql.length() > 0)
                    strsql.delete(0, strsql.length());

                strsql.append("select * from organization");
                strsql.append(" where codeitemid=");
                strsql.append(code);
                rs = db.search(strsql.toString()); // 执行当前查询的sql语句
                if (rs.next()) {
                    StationPosView posview = new StationPosView();
                    pre = rs.getString("codesetid");
                    if ("UM".equalsIgnoreCase(pre)) {
                        if (isdep == false) {
                            posview.setItem("e0122");
                            posview.setItemvalue(rs.getString("codeitemid"));
                            posview.setItemviewvalue(rs.getString("codeitemdesc"));
                            isdep = true;
                            poslist.add(posview);
                        }
                    } else if ("UN".equalsIgnoreCase(pre)) {
                        if (isorg == false) {
                            posview.setItem("b0110");
                            posview.setItemvalue(rs.getString("codeitemid"));
                            posview.setItemviewvalue(rs.getString("codeitemdesc"));
                            isorg = true;
                            poslist.add(posview);
                        }
                    }

                    code = "'"+rs.getString("parentid")+"'";
                }
            }  
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
            PubFunc.closeResource(stmt);
        }

        return poslist;
    }
}
