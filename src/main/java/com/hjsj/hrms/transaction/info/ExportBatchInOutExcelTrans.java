package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.CascadeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
/**
 * 人员批量导入模板导出
 * @author xujian
 *Apr 21, 2010
 */
public class ExportBatchInOutExcelTrans extends IBusiness {
	private int index = 0;
	private HSSFSheet codesetSheet=null;
	/**office版本类型，1:2007及以下版本，2：其他office以及wps版本*/
	private String officeType = "1";

	public void execute() throws GeneralException {
		String outName="";
		String items="";
		String fieldsetidlist="";
		String selectitemslist="";

		try{
			LazyDynaBean bean=(LazyDynaBean)this.getFormHM().get("obj");
			//zhangh 2019-11-20 获取用户选择的office版本
			this.officeType=(String)bean.get("officeType");
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

			if(selectitems.length()<1)
				throw new Exception(ResourceFactory.getProperty("workbench.info.selectitems.msg"));

			selectitems=selectitems.replaceAll("`", ",");
			String seconditems = (String)bean.get("seconditems");
			if(seconditems.length()>1)
				seconditems= seconditems.substring(1,seconditems.length());

			seconditems=seconditems.replaceAll("`", ",").toLowerCase();
			StringBuffer sbsql=new StringBuffer();
			String primarykey="a0101";//默认为姓名
			String codeid=(String)bean.get("codeid");//自定义唯一性指标  zhaogd 2013-11-27
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
			String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
			if(chkvalid==null)
				chkvalid="0";

			if(uniquenessvalid==null)
				chkvalid="0";

			if(uniquenessvalid==null)
				uniquenessvalid="";

			String chkcheck="",uniquenesscheck="";

			if("0".equalsIgnoreCase(chkvalid)|| "".equalsIgnoreCase(chkvalid)){
				chkcheck="";
			} else{
				chkcheck="checked";
			}

			if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
				uniquenesscheck="";
			} else{
				uniquenesscheck="checked";
			}

			if(chk==null)
				chk="";

			if(onlyname==null)
				onlyname = "";

			if(!("".equals(codeid))){
				primarykey=codeid;
			}else if(!("".equals(onlyname))&& "checked".equals(uniquenesscheck)){
				primarykey=onlyname;
			}else if(!("".equals(chk))&& "checked".equals(chkcheck)){
				primarykey=chk;
			}

			outName=this.creatExcel(sbsql.toString(),primarykey,selectitems,seconditems,fieldsetid,fsid);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			outName = PubFunc.encrypt(outName);
			this.getFormHM().put("outName", outName);
		}
	}

	private String creatExcel(String string, String primarykey, String selectitems, String seconditems, String fieldsetid, String fsid) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		String outName = this.userView.getUserName() + "_hr" + ".xls";
		try {
			if (fsid.length() < 1) {
				String ffid = "";
				String sss = "";
				String fid[] = (fieldsetid).split(",");
				String selects[] = (selectitems).split(":");
				for (int i = 0; i < fid.length; i++) {//如果有A01表，则A01排在首位
					if ("A01".equals(fid[i])) {
						for (int j = i; j > 0; j--) {
							ffid = fid[j];
							fid[j] = fid[j - 1];
							fid[j - 1] = ffid;
							sss = selects[j];
							selects[j] = selects[j - 1];
							selects[j - 1] = sss;
						}
					}
				}
				for (int i = 0; i < fid.length; i++) {
					fieldsetid = fid[i];
					selectitems = selects[i];
					String strs[] = (primarykey + selectitems).split(",");
					ArrayList list = new ArrayList();
					boolean hasName = false;//子集中当唯一标识不是姓名时，添加个姓名列    true就为添加   xupengyu
					for (int j = 0; j < strs.length; j++) {
						if (j == 0 && !"a0101".equalsIgnoreCase(primarykey) && !"A01".equalsIgnoreCase(fieldsetid)) {
							list.add((FieldItem) DataDictionary.getFieldItem("a0101"));
							hasName = true;
						}
						if (j != 0 && strs[j].equalsIgnoreCase(primarykey)) {
							continue;
						}
						FieldItem item = (FieldItem) DataDictionary.getFieldItem(strs[j]);
						list.add(item);
					}
					creatSheet(selectitems, seconditems, fieldsetid, hasName, list, wb);
				}
			} else {
				String strs[] = (primarykey + selectitems).split(",");
				ArrayList list = new ArrayList();
				boolean hasName = false;//子集中当唯一标识不是姓名时，添加个姓名列    true就为添加   xupengyu
				for (int j = 0; j < strs.length; j++) {
					if (j == 0 && !"a0101".equalsIgnoreCase(primarykey) && !"A01".equalsIgnoreCase(fieldsetid)) {
						list.add((FieldItem) DataDictionary.getFieldItem("a0101"));
						hasName = true;
					}
					if (j != 0 && strs[j].equalsIgnoreCase(primarykey)) {
						continue;
					}
					FieldItem item = (FieldItem) DataDictionary.getFieldItem(strs[j]);
					list.add(item);
				}
				creatSheet(selectitems, seconditems, fieldsetid, hasName, list, wb);
			}


			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(wb);
		}
		return outName;
	}

	private void creatSheet(String selectitems, String seconditems, String fieldsetid, boolean hasName, ArrayList list, HSSFWorkbook wb) throws Exception {
		String fieldsetdesc="";
		ContentDAO dao1 = new ContentDAO(this.frameconn);
		String sql = "select fieldsetdesc from fieldSet where fieldsetid='"+fieldsetid+"'";
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
		// 【12937】青牛北京软件有限公司 记录录入---批量下载--无法下载通讯费子集模板
		// 将 / 替换成_ sunm add 2015-9-18
		SheetName = SheetName.replaceAll("/", "_").replaceAll("／", "_");
		HSSFSheet sheet = wb.createSheet(SheetName);
		// sheet.setProtect(true);
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

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

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

		HSSFCellStyle styleF1 = dataStyle(wb);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		HSSFCellStyle styleF2 = dataStyle(wb);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		HSSFCellStyle styleF3 = dataStyle(wb);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		HSSFCellStyle styleF4 = dataStyle(wb);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		HSSFCellStyle styleF5 = dataStyle(wb);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

		//sheet.setColumnWidth((short) 0, (short) 1000);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
		HSSFPatriarch patr = sheet.createDrawingPatriarch();

		HSSFRow row =sheet.getRow(0);
		if(row==null){
			row=sheet.createRow(0);
		}
		HSSFCell cell = null;
		HSSFComment comm = null;



		ArrayList codeCols = new ArrayList();
		//zhangh 2019-10-29 codeSetList 存放codesetid,orderMap
		ArrayList codeSetList = new ArrayList();
		Map<String,Integer> orderMap = new HashMap<String,Integer>();
		for (int i = 0; i < list.size(); i++)
		{
			FieldItem field = (FieldItem) list.get(i);
			String fieldName = field.getItemid().toLowerCase();
			String fieldLabel = field.getItemdesc();
			//if (fieldName.equalsIgnoreCase("b0110") || fieldName.equalsIgnoreCase("e0122")||fieldName.equalsIgnoreCase("e01a1"))
			int w=field.getDisplaywidth();
			if(w==0){
				w=8;
			}
			if(w>186)
				w=186;
			sheet.setColumnWidth((i), w*350);
			cell=row.getCell(i);
			if(cell==null)
				cell=row.createCell(i);

			cell.setCellValue(cellStr(fieldLabel));
			cell.setCellStyle(style2);
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
			if(hasName){
				if(i==1){
					comm.setString(new HSSFRichTextString(fieldName+"`"+fieldsetid));
				}else if(seconditems.indexOf(fieldName)!=-1){
					comm.setString(new HSSFRichTextString(fieldName+"`foreignkey"));
				}else{
					comm.setString(new HSSFRichTextString(fieldName));
				}
			}else{
				if(i==0){
					comm.setString(new HSSFRichTextString(fieldName+"`"+fieldsetid));
				}else if(seconditems.indexOf(fieldName)!=-1){
					comm.setString(new HSSFRichTextString(fieldName+"`foreignkey"));
				}else{
					comm.setString(new HSSFRichTextString(fieldName));
				}
			}
			cell.setCellComment(comm);
			if ("A".equalsIgnoreCase(field.getItemtype())&&(field.getCodesetid()!=null&&!"".equals(field.getCodesetid())&&!"0".equals(field.getCodesetid()))){
				codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
				//zhangh 2019-11-4
				codeSetList.add(field.getCodesetid().toUpperCase());
				if("UN".equals(field.getCodesetid().toUpperCase())&&"B0110".equalsIgnoreCase(fieldName)){
					orderMap.put("UN",new Integer(i));
				}else if("UM".equals(field.getCodesetid().toUpperCase())&&"E0122".equalsIgnoreCase(fieldName)){
					orderMap.put("UM",new Integer(i));
				}else if("@K".equals(field.getCodesetid().toUpperCase())&&"E01A1".equalsIgnoreCase(fieldName)){
					orderMap.put("@K",new Integer(i));
				}
			}
		}



		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			int rowCount = 1;
			while(rowCount<1001)
			{
				row =sheet.getRow(rowCount);
				if(row==null){
					row=sheet.createRow(rowCount);
				}
				for (int i = 0; i < list.size(); i++)
				{
					FieldItem field = (FieldItem) list.get(i);
					String itemtype = field.getItemtype();
					int decwidth = field.getDecimalwidth();

					cell = row.getCell(i);
					if(cell==null)
						cell=row.createCell(i);
					if ("N".equals(itemtype))
					{
						if (decwidth == 0)
							cell.setCellStyle(styleN);
						else if (decwidth == 1)
							cell.setCellStyle(styleF1);
						else if (decwidth == 2)
							cell.setCellStyle(styleF2);
						else if (decwidth == 3)
							cell.setCellStyle(styleF3);
						else if (decwidth == 4)
							cell.setCellStyle(styleF4);
						// else if(decwidth==5)
						// cell.setCellStyle(styleF5);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue("");
					} else
					{
						cell.setCellStyle(style1);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					}

				}
				rowCount++;
			}
			rowCount--;

		/* guodd 2015-04-14   office 高版本 下拉数据不能从不同的sheet中获取了
		 * 	String codesetSheetName = "hjehr_codeset_"+index/255;
			if(codeCols.size()>0){
				if(index%255==0){//当sheet列数满255时，重新生成一个sheet
					codesetSheet = wb.createSheet(codesetSheetName);
				}
				wb.setSheetHidden(wb.getSheetIndex(codesetSheet), true);
				codesetSheet.setColumnWidth((index),0);
			}
			String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
*/
			codesetSheet = sheet;
			// 下拉数据放到最后，excel 最后一列是IV, 为方便计算，这里从HZ 开始到着输入，依次为 HZ、HY、HX......   guodd 2015-04-14
			String[] firstUpper = {"H","G","F","E","D","C","B","A"};
			String[] lettersUpper =
					{ "Z", "Y", "X", "W", "V", "U", "T", "S", "R", "Q", "P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A" };

			boolean finishFlag = false;
			boolean isHavePost = false;
			for (int n = 0; n < codeCols.size(); n++)
			{
				String codeCol = (String) codeCols.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				//zhangh 2019-10-29 解决导出单位、部门、岗位不级联的问题start
				//只有导出的指标中包含单位、部门指标时才做级联，岗位指标有没有都可以，有就级联上，没有就不处理
				if(codeSetList.contains("UN")&&codeSetList.contains("UM")){
					if("UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)|| "@K".equalsIgnoreCase(codesetid)){
						if(finishFlag){
							continue;
						}
						if(codeSetList.contains("@K")){
							//如果有岗位指标，那就把岗位也一起处理了
							isHavePost = true;
						}
						//获取权限范围内的单位列表
						List<String> orgNameList = CascadeUtil.getOrgNameList(this.userView,dao,"1","employee");
						//orderMap为空时，说明导出的指标没有单位、部门、岗位，不需要级联
						if(orgNameList!=null&&orgNameList.size()>0 &&orderMap != null && orderMap.size()>0){
							List<String> orgList = CascadeUtil.getOrgList(this.userView,dao,"1","employee");
							List<String> deptNameList = CascadeUtil.getDeptNameList(this.userView,dao,"1","employee");
							List<String> deptList = CascadeUtil.getDeptList(this.userView,dao,"1","employee");
							Map<String, List<String>> orgMap = CascadeUtil.getOrgMap(this.userView,dao,orgList,orgNameList,"1","employee");
							Map<String, List<String>> deptMap = CascadeUtil.getDeptMap(this.userView,dao,deptList,deptNameList,"1","employee");
							List<String> unitPostList = CascadeUtil.getUnitPost(userView, dao, "1", "employee");
							//创建一个隐藏sheet页，用于存在下拉框数据
							HSSFSheet HiddenSheet = wb.createSheet("hidden");
							wb.setSheetHidden(1, true); //将第二个用于存储下拉框数据的sheet隐藏
							//向隐藏页写入单位、部门、岗位信息信息
							CascadeUtil.initInfo(wb, HiddenSheet, orgNameList, orgMap,deptNameList,deptMap,isHavePost,"employee",unitPostList);
							//设置数据有效性
							if("A01".equalsIgnoreCase(fieldsetid) && orderMap != null && orderMap.size() > 0) {
							    CascadeUtil.initSheetValidation(sheet,orderMap,isHavePost,"employee",this.officeType);
							}
							
							finishFlag = true;
							continue;
						}
					}
				}
				//zhangh 2019-10-29 解决导出单位、部门、岗位不级联的问题end
				//int m = 0;
				int m = 2001;//初始行为2001行
				String columnIndex = firstUpper[index/26] + lettersUpper[index%26];//计算列的 列标识 guodd 2015-04-14
				int cellIndex = columnToIndex(columnIndex);  // 通过列标识 计算出列 index guodd 2015-04-14

				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();
				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "' and invalid='1' and " + Sql_switcher.sqlNow() + " between start_date and end_date");// and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
					this.frowset = dao.search(codeBuf.toString());
					if(this.frowset.next()){
						if(this.frowset.getInt(1)<500){//20160713 linbz 代码型中指标大于500的时候，就不再加载了
							codeBuf.setLength(0);
							codeBuf.append("select codesetid,codeitemid,codeitemdesc,childid"); 
							codeBuf.append(" from codeitem where codesetid='" + codesetid + "'");
							codeBuf.append(" and codeitemid=parentid");
							codeBuf.append(" and invalid='1'  and " + Sql_switcher.sqlNow()); 
							codeBuf.append(" between start_date and end_date order by a0000");
						}else{
							continue;
						}
					}
				} else
				{
					if (!"UN".equals(codesetid)){
						m=loadorg(codesetSheet,row,cell,cellIndex,m,dao,codesetid);
					}else if ("UN".equals(codesetid))
					{
						codeBuf.setLength(0);
						if(this.userView.isSuper_admin()){
							codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
									+ "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
						}else{
							String manpriv=this.userView.getManagePrivCode();
							String manprivv=this.userView.getManagePrivCodeValue();
							if(manprivv.length()>0) {
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
										+ "' and codeitemid like '"+manprivv+"%'");
								String hegihtPivWhere = getHeihtPivWhere(this.userView);
								if(StringUtils.isNotEmpty(hegihtPivWhere)) {
									codeBuf.append(hegihtPivWhere);
								}
								
								codeBuf.append(" and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
							} else if(manpriv.length()>=2)
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
										+ "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
							else
								codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where 1=2");
						}
					}
				}
				if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					this.frowset = dao.search(codeBuf.toString());
					while (this.frowset.next()) {
						row = codesetSheet.getRow(m + 0);
						if (row == null)
							row = codesetSheet.createRow(m + 0);
						
						cell = row.createCell((cellIndex));
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
							//如果代码项有子节点，则进行迭代写入excel表中
							if(!this.frowset.getString("codeitemid").equalsIgnoreCase(this.frowset.getString("childid"))) 
								m = m + createCell(codesetid, this.frowset.getString("codeitemid"), m, dao, cellIndex);
						}
						m++;
					}
				}
				if(m==2001){
					continue;
				}
				// 放到单独页签
				// sheet.setColumnWidth((208+index),0);
				String strFormula ="";
				/*if(index<=25){
					strFormula = codesetSheetName + "!$" + lettersUpper[index] + "$1:$" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
				}else if(index>25){
					strFormula = codesetSheetName + "!$" + lettersUpper[index/26-1] + lettersUpper[index%26]
					           + "$1:$" + lettersUpper[index/26-1] + lettersUpper[index%26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
				}*/
				//因为是倒着输入的，所以重新计算公式  guodd 2015-04-14
				strFormula = "$" + firstUpper[index/26] + lettersUpper[index%26]
						+ "$2001:$" + firstUpper[index/26] + lettersUpper[index%26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
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
	/**
	 * 通过列标识（A、B... AA等）计算列index（number）
	 * @author guodd 2015-04-14
	 * @param column，范围为A-IV，再大就超出excel范围了
	 * @return int
	 */
	int columnToIndex(String column) {
		if (!column.matches("[A-Z]+")) {
			try {
				throw new Exception("Invalid parameter");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int index = 0;
		char[] chars = column.toUpperCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			index += ((int) chars[i] - (int) 'A' + 1)
					* (int) Math.pow(26, chars.length - i - 1);
		}
		return index-1;
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

	private int loadorg(HSSFSheet sheet,HSSFRow row,HSSFCell cell,int index,int m,ContentDAO dao,String type) throws Exception {
		Statement st=null;
		ResultSet rs = null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try{
			st=this.frameconn.createStatement();
			String sql="";
			if(this.userView.isSuper_admin()){
				sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}else{
				String manpriv=this.userView.getManagePrivCode();
				String manprivv=this.userView.getManagePrivCodeValue();
				sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization";
				if(manprivv.length()>0) {
					sql += " where";
					String hegihtPivWhere = getHeihtPivWhere(this.userView);
					if(StringUtils.isNotEmpty(hegihtPivWhere)) {
						sql += " codeitemid like '"+manprivv+"%'";
						sql += hegihtPivWhere;
					} else 
						sql += " codeitemid='"+manprivv+"'";

					sql += " and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
				} else if(manpriv.length()>=2)
					sql += " where codeitemid=parentid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
				else
					sql += " where 1=2";
			}
			//rs = dao.search(sql);
			dbs.open(this.frameconn, sql);
			rs = st.executeQuery(sql);
			String codeitemid="";
			String childid="";
			String codeitemdesc="";
			int grade=0;
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				childid=rs.getString("childid");
				codeitemdesc=rs.getString("codeitemdesc");
				grade=rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				// cell = row.createCell((208 + index));
				// 放到单独页签
				cell = row.createCell((index));
				StringBuffer sb=new StringBuffer();
				sb.setLength(0);
				for(int i=1;i<grade;i++){
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString()+codeitemdesc+"("+codeitemid+")"));
				m++;
				if(!codeitemid.equals(childid))
					m=loadchild(sheet,row,cell,index,m,dao,codeitemid,type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(st);
		}
		return m;
	}

	private int loadchild(HSSFSheet sheet,HSSFRow row,HSSFCell cell,int index,int m,ContentDAO dao,String parentid,String type) throws Exception {
		ResultSet rs = null;
		Statement st=null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try{
			String sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='"+parentid+"'";
			st=this.frameconn.createStatement();
			String hegihtPivWhere = getHeihtPivWhere(this.userView);
			if(StringUtils.isNotEmpty(hegihtPivWhere)) {
				sql += hegihtPivWhere;
			}
			
			if("@K".equalsIgnoreCase(type)){
				sql +=" and parentid<>codeitemid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}else{
				sql +=" and codesetid<>'@K' and parentid<>codeitemid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}
			//rs = dao.search(sql);
			dbs.open(this.frameconn, sql);
			rs = st.executeQuery(sql);
			String codeitemid="";
			String childid="";
			String codeitemdesc="";
			int grade=0;
			while(rs.next()){
				codeitemid=rs.getString("codeitemid");
				childid=rs.getString("childid");
				codeitemdesc=rs.getString("codeitemdesc");
				grade=rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);

				cell = row.createCell((index));
				StringBuffer sb=new StringBuffer();
				sb.setLength(0);
				for(int i=1;i<grade;i++){
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString()+codeitemdesc+"("+codeitemid+")"));
				m++;
				if(!codeitemid.equals(childid))
					m=loadchild(sheet,row,cell,index,m,dao,codeitemid,type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(st);
		}
		return m;
	}

	/**
	 * 获取某代码项的子节点 
	 * @param codesetId
	 *            代码类编号
	 * @param parentId
	 *            某代码项编号
	 * @param rowIndex
	 *            Excel的行号
	 * @param dao
	 *            数据库链接
	 * @param cellIndex
	 *            Excel的列号
	 * @return
	 */
	private int createCell(String codesetId, String parentId, int rowIndex, ContentDAO dao, int cellIndex) {
		RowSet rs = null;
		int childSum = 0;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select codesetid,codeitemid,codeitemdesc,childid");
			sql.append(" from codeitem where codesetid='" + codesetId + "'");
			sql.append(" and parentid='" + parentId + "' and codeitemid<>'" + parentId + "'");
			sql.append(" and invalid='1'  and " + Sql_switcher.sqlNow());
			sql.append(" between start_date and end_date order by a0000");
			rs = dao.search(sql.toString());
			HSSFRow row = null;
			HSSFCell cell = null;
			while (rs.next()) {
				childSum++;
				row = codesetSheet.getRow(rowIndex + childSum);
				if (row == null)
					row = codesetSheet.createRow(rowIndex + childSum);

				cell = row.createCell((cellIndex));
				cell.setCellValue(new HSSFRichTextString(rs.getString("codeitemdesc")));
				String itemid = rs.getString("codeitemid");
				if (!rs.getString("codeitemid").equalsIgnoreCase(rs.getString("childid")))
					childSum = childSum + createCell(codesetId, itemid, rowIndex + childSum, dao, cellIndex);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}

		return childSum;
	}
	
	private static String getHeihtPivWhere(UserView userView) {
    	String highPrivWhere = "";
    	try {
    		String hegihtPiv = userView.getHighPrivExpression();
    		if(StringUtils.isNotEmpty(hegihtPiv)) {
    			highPrivWhere = userView.getPrivSQLExpression(hegihtPiv, "Usr", false, true, true, null);
    			highPrivWhere = highPrivWhere.substring(highPrivWhere.toLowerCase().indexOf("where") + 5);
    			highPrivWhere = highPrivWhere.replace("UsrA01.B0110", "codeitemid");
    			highPrivWhere = highPrivWhere.replace("UsrA01.E0122", "codeitemid");
    			highPrivWhere = " and (" + highPrivWhere + ")";
    		}
    	} catch (GeneralException e) {
    		e.printStackTrace();
    	}
    			
		return highPrivWhere;
	}
}
