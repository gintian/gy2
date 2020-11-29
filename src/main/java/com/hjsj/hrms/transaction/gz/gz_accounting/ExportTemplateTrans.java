package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * <p>
 * Title:ExportExcelTrans.java
 * </p>
 * <p>
 * Description:薪资导出模板
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-06-08 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 */
public class ExportTemplateTrans extends IBusiness
{

	public void execute() throws GeneralException
	{
		try
		{
			String salaryid = (String) this.getFormHM().get("salaryid");
			String sqlStr = (String) this.getFormHM().get("sqlStr");
			sqlStr=PubFunc.decrypt(SafeCode.decode(sqlStr));
			String itemFilterId = (String) this.getFormHM().get("itemid");
			String pro_field = (String) this.getFormHM().get("proright_str");
			//如果用户没有当前薪资类别的资源权限   20140915  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			HashMap map = new HashMap();
			HashMap calcuItemMap = new HashMap();
			ArrayList list = new ArrayList();
			/** 薪资类别 */
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
	
			String  royalty_valid=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"valid");
			String royalty_relation_fields=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.ROYALTIES,"relation_fields");
			
			
			// 仅数值型和字符和备注类型，另外需要去除系统项，无权限的指标
			String sql = "select itemid,itemdesc,Itemtype,Codesetid,Initflag from salaryset " + "where salaryid=" + salaryid
					+ " and Itemtype in ('A','N','M')  and (Initflag<>3 or (Initflag=3 and itemid in ('B0110','E0122','A0101')))";
		 	if("1".equals(royalty_valid)&&royalty_valid.trim().length()>0)  //提成工资，关联指标必须输出
				sql = "select itemid,itemdesc,Itemtype,Codesetid,Initflag from salaryset " + "where salaryid=" + salaryid
				+ " and Itemtype in ('A','N','M','D')  and (Initflag<>3 or (Initflag=3 and itemid in ('B0110','E0122','A0101')))";
		 
			ContentDAO dao = new ContentDAO(this.frameconn);
			String salaryName = "";// 工资类别名
			String itemFilterName = "";// 项目过滤的名称
	
			try
			{
				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					String itemid = this.frowset.getString("itemid");
					map.put(itemid.toLowerCase(), this.frowset.getString("itemdesc"));
				}
				sql = "select itemname from salaryformula  where salaryid=" + salaryid;
				// 公式计算项
				this.frowset = dao.search(sql);
				while (this.frowset.next())
				{
					String itemid = this.frowset.getString("itemname");
					calcuItemMap.put(itemid.toLowerCase(), itemid);
				}
				sql = "select cname from salarytemplate where salaryid=" + salaryid;
				this.frowset = dao.search(sql);
				if (this.frowset.next())
					salaryName = this.frowset.getString(1);
				if (!"all".equalsIgnoreCase(itemFilterId) && !"new".equalsIgnoreCase(itemFilterId)&&itemFilterId.trim().length()>0)
				{
					sql = "select chz from gzitem_filter where id=" + itemFilterId;
					this.frowset = dao.search(sql);
					if (this.frowset.next())
						itemFilterName = this.frowset.getString(1);
				}
			} catch (SQLException e1)
			{
				e1.printStackTrace();
			}
	
			
			/** 项目过滤 */
			ArrayList fieldlist = gzbo.getFieldlist();
			ArrayList allFieldList=(ArrayList)fieldlist.clone();
			if (!"all".equalsIgnoreCase(itemFilterId)&&itemFilterId.trim().length()>0&&!"new".equalsIgnoreCase(itemFilterId))
				fieldlist = gzbo.filterItemList(fieldlist, itemFilterId);
	
			if (!(pro_field == null || "".equalsIgnoreCase(pro_field)))
			{
				fieldlist = gzbo.filterItemsList(gzbo.getFieldlist(), pro_field);
			} else if ("new".equalsIgnoreCase(itemFilterId) && "".equalsIgnoreCase(pro_field))
			{
				fieldlist = gzbo.getFieldlist();
			}
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			boolean _flag=true;//判断是否该显示唯一性指标（如果隐藏了唯一指标  那么导出模版我直接写死导出就带这列  如果帐套里面就没有这个指标  那么我把主键导出来） zhaoxg 2013-11-26
			for(int j =0;j<fieldlist.size();j++){
				Field field = (Field)fieldlist.get(j);
				if(onlyname.equalsIgnoreCase(field.getName())){
					_flag=false;
					continue;
				}
			}
			// start xieguiquan 2010-08-26
			RecordVo vo =gzbo.getRealConstantVo(this.getFrameconn(),Integer.parseInt(salaryid));
			if(vo!=null&&vo.getString("lprogram")!=null&&vo.getString("lprogram").indexOf("hidden_items")!=-1
				&&vo.getString("lprogram").indexOf("hidden_item")!=-1){
				//System.out.println(vo.getString("lprogram"));
		    		Document doc;
					try {
						doc = PubFunc.generateDom(vo.getString("lprogram"));
				    String _str=",SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,A00Z0,A00Z1,NBASE,";
		    		Element root = doc.getRootElement();
					Element hidden_items = root.getChild("hidden_items");
					if(hidden_items!=null){
						List list2 = hidden_items.getChildren();
						if(list2.size()>0){
							for(int i =0;i<list2.size();i++){
								Element temp = (Element)list2.get(i);
								if(temp.getAttributeValue("user_name")!=null&&temp.getAttributeValue("user_name").toString().equalsIgnoreCase(this.userView.getUserName()))
									{
									String str = temp.getText();//隐藏指标
									if(str.length()>0){
									str = ","+str+",";
									ArrayList alist = new ArrayList();
									for(int j =0;j<fieldlist.size();j++){
										Field field = (Field)fieldlist.get(j);
										Field cfield = (Field)field.clone();
										if(onlyname.equalsIgnoreCase(field.getName())){
											cfield.setVisible(true);
											alist.add(cfield);
											_flag=false;
											continue;
										}
										if(str.indexOf(","+field.getName()+",")!=-1){
											cfield.setVisible(false);
											
										}else{
											if("a01z0".equalsIgnoreCase(field.getName())){//初试化隐藏
												if(gzbo.getCtrlparam()!=null)
												{
													String a01z0Flag=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有  
													if(a01z0Flag==null|| "0".equals(a01z0Flag))
													{
	
													}
													else
														cfield.setVisible(true);
												}
												else
													cfield.setVisible(true);
											}
											else if("add_flag".equalsIgnoreCase(field.getName())){
												//追加
											}else if("a0000".equalsIgnoreCase(field.getName())|| "a0100".equalsIgnoreCase(field.getName())){
												//系统默认不显示指标
											}
											else if(_str.indexOf(","+field.getName().toUpperCase()+",")==-1&& "0".equalsIgnoreCase(this.userView.analyseFieldPriv(field.getName())))
											{
												 
											}
											else
											cfield.setVisible(true);
										}
										alist.add(cfield);
									}
									if(alist.size()>0)
										fieldlist =alist;
									}
									break;
									}
								
								
							}
						}
					}
					} catch (JDOMException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
	
			//end xieguiquan
			//dengcan 北京移动自动隐藏 审批状态和过程
			if(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
			{
			 	for(int i=0;i<fieldlist.size();i++)
				{
					Field field=(Field)fieldlist.get(i);
				 	if("sp_flag".equalsIgnoreCase(field.getName())|| "appprocess".equalsIgnoreCase(field.getName()))
				 		field.setVisible(false);
				} 
			}
			// export_limits:设置可以导出的只读指标项
			HashMap readOnlyFldsCanExport = new HashMap();
			String export_limits = SystemConfig.getPropertyValue("export_limits");
			if (export_limits != null)
			{
				String[] readOnlyFlds = export_limits.split(",");
				for (int m = 0; m < readOnlyFlds.length; m++)
				{
					String temp = readOnlyFlds[m].trim();
					if (temp.length() > 0)
						readOnlyFldsCanExport.put(temp.toUpperCase(), "");
				}
			}
	
			for (int i = 0; i < fieldlist.size(); i++)
			{
				Field field = (Field) fieldlist.get(i);
				// 仅数值型和字符(包括代码指标) 备注型 去除系统项(保留单位 部门 姓名可选)
				if (map.get(field.getName().toLowerCase()) == null)
					continue;
				
				FieldItem item=DataDictionary.getFieldItem(field.getName());
				if(item!=null)
				{
					String pri = this.userView.analyseFieldPriv(field.getName());
					if ("0".equals(pri))// 没有权限
						continue;
				}
				if (SystemConfig.getPropertyValue("excel_template_limit") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_limit")))
				{
					// 去除公式计算项了
					if (calcuItemMap.get(field.getName().toLowerCase()) != null)
						continue;
	
					// 去除只读项
					if(item!=null)
					{
						String pri = this.userView.analyseFieldPriv(field.getName());
						if ("1".equals(pri))// 只读
						{
							if (readOnlyFldsCanExport.size() > 0)
							{
								if (readOnlyFldsCanExport.get(field.getName().toUpperCase()) == null)// 不属于允许导出的只读项
									continue;
							} else
								// 没有设置允许导出的只读项
								continue;
						}
					}
				}
				if ("new".equalsIgnoreCase(itemFilterId) && !(pro_field == null || "".equalsIgnoreCase(pro_field)))
				{
					if (pro_field.toLowerCase().indexOf(field.getName().toLowerCase()) == -1)
						continue;
				}
				// xieguiquan 去除 隐藏项
				if(!field.isVisible())
					continue;
				list.add(field);
			}
			
			
			 
			if("1".equals(royalty_valid)&&royalty_valid.trim().length()>0)  //提成工资，关联指标必须输出
			{
				 
				StringBuffer buf=new StringBuffer(",");
				for (int i = 0; i < list.size(); i++)
				{
					Field field = (Field) list.get(i);
					buf.append(field.getName().toLowerCase()+",");
				}
				String[] temps=royalty_relation_fields.toLowerCase().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].trim().length()>0)
					{
						if(buf.indexOf(","+temps[i]+",")==-1)
						{
							
							for(int j=0;j<allFieldList.size();j++)
							{
								Field field = (Field) allFieldList.get(j);
								if(field.getName().toLowerCase().equals(temps[i]))
								{
									list.add(field);
									break;
								}
							}
						}
					}
				}
			}
	
			HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
			HSSFSheet sheet = wb.createSheet();
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
			
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = null;
			HSSFComment comm = null;		
			
			HashMap styleMap = new HashMap();
			styleMap.put("0",styleN);
			styleMap.put("1",styleF1);
			styleMap.put("2",styleF2);
			styleMap.put("3",styleF3);
			styleMap.put("4",styleF4);
			styleMap.put("5",styleF5);
	
			String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
			if(uniquenessvalid==null|| "0".equals(uniquenessvalid))
				onlyname="";
	
			//北京移动 下载模板不要下载主键标识串
			if(!(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))&&(onlyname==null||onlyname.trim().length()==0||_flag)&&!"1".equals(royalty_valid))
			{
				sheet.setColumnWidth(0, (short) 1000);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
				cell = row.createCell(0);
				cell.setCellValue(cellStr("主键标识串"));
				cell.setCellStyle(styleCol0_title);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 1, 0, (short) 2, 1));
				comm.setString(new HSSFRichTextString("主键标识串"));
				cell.setCellComment(comm);
			}
			
			if("1".equals(royalty_valid)) //提成工资
			{
				if(onlyname.length()==0)
					throw new GeneralException("系统没有设置唯一性指标!");
				boolean flag=false;
				for (int i = 0; i < list.size(); i++)
				{
					Field field = (Field) list.get(i);
					if(field.getName().equalsIgnoreCase(onlyname))
						flag=true;
				}
				if(!flag)
					throw new GeneralException("薪资类别没有设置唯一性指标项目!");
				
			}
			
			
			/** 单位部门姓名原来是固定列，现在可以让用户选择 */
			// cell = row.createCell((short) 1);
			//		
			// cell.setCellStyle(style2);
			// String fieldExplain = DataDictionary.getFieldItem("b0110").getExplain();
			// String fieldLabel1="单位名称";
			// if(SystemConfig.getPropertyValue("excel_template_desc")!=null && SystemConfig.getPropertyValue("excel_template_desc").equalsIgnoreCase("true") && fieldExplain!=null &&
			// fieldExplain.trim().length()>0)
			// {
			// fieldLabel1+="\r\n如："+fieldExplain;
			// sheet.setColumnWidth((short) (1), (short)5000);
			// }
			// cell.setCellValue(cellStr(fieldLabel1));
			// 注释
			// HSSFComment comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 2, 0, (short) 3, 1));
			// comm.setString(new HSSFRichTextString("b0110"));
			// cell.setCellComment(comm);
			//
			// cell = row.createCell((short) 2);
			//	
			// cell.setCellStyle(style2);
			// fieldExplain = DataDictionary.getFieldItem("e0122").getExplain();
			// fieldLabel1="部门";
			// if(SystemConfig.getPropertyValue("excel_template_desc")!=null && SystemConfig.getPropertyValue("excel_template_desc").equalsIgnoreCase("true") && fieldExplain!=null &&
			// fieldExplain.trim().length()>0)
			// {
			// fieldLabel1+="\r\n如："+fieldExplain;
			// sheet.setColumnWidth((short) (2), (short)5000);
			// }
			// cell.setCellValue(cellStr(fieldLabel1));
			// comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 3, 0, (short) 4, 1));
			// comm.setString(new HSSFRichTextString("e0122"));
			// cell.setCellComment(comm);
			//
			// cell = row.createCell((short) 3);
			//	
			// fieldExplain = DataDictionary.getFieldItem("a0101").getExplain();
			// fieldLabel1="姓名";
			// if(SystemConfig.getPropertyValue("excel_template_desc")!=null && SystemConfig.getPropertyValue("excel_template_desc").equalsIgnoreCase("true") && fieldExplain!=null &&
			// fieldExplain.trim().length()>0)
			// {
			// fieldLabel1+="\r\n如："+fieldExplain;
			// sheet.setColumnWidth((short) (3), (short)5000);
			// }
			// cell.setCellValue(cellStr(fieldLabel1));
			// cell.setCellStyle(style2);
			// comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 4, 0, (short) 5, 1));
			// comm.setString(new HSSFRichTextString("a0101"));
			// cell.setCellComment(comm);
			String fieldExplain = "";
	
			ArrayList codeCols = new ArrayList();
			HashMap codeSize=new HashMap();
			for (int i = 0; i < list.size(); i++)
			{
				Field field = (Field) list.get(i);
				String fieldName = field.getName().toLowerCase();
				String fieldLabel = field.getLabel();
	
				int colIndex = i;
				if(!(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))&&(onlyname==null||onlyname.trim().length()==0||_flag)&&!"1".equals(royalty_valid))
					colIndex = i+1;			
				
				fieldExplain = DataDictionary.getFieldItem(fieldName).getExplain();
				if (SystemConfig.getPropertyValue("excel_template_desc") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("excel_template_desc")) && fieldExplain != null
						&& fieldExplain.trim().length() > 0)
				{
					fieldLabel += "\r\n如：" + fieldExplain;
					sheet.setColumnWidth((short) (colIndex), (short) 5000);
				}
				if ("UN".equalsIgnoreCase(field.getCodesetid()) || "UM".equalsIgnoreCase(field.getCodesetid())|| "@k".equalsIgnoreCase(field.getCodesetid()))
					sheet.setColumnWidth((short) (colIndex), (short) 5000);
	
				cell = row.createCell((short) (colIndex));
	
				cell.setCellValue(cellStr(fieldLabel));
				cell.setCellStyle(style2);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (colIndex + 1), 0, (short) (colIndex + 2), 1));
				comm.setString(new HSSFRichTextString(fieldName));
				cell.setCellComment(comm);
				if (!"0".equals(field.getCodesetid()))
				{
					int size=0;
					if(codeSize.get(field.getCodesetid().toLowerCase())!=null)
						size=Integer.parseInt((String)codeSize.get(field.getCodesetid().toLowerCase()));
					else
					{
						size=getCodeSize(field,field.getCodesetid(),dao);
						codeSize.put(field.getCodesetid().toLowerCase(), size+"");
					}
					if(size<100) //代码项个数大于50 或 没有写权限都不生成代码数据  50有点少 改成100 zhaoxg update 2017-02-14
						codeCols.add(field.getCodesetid() + ":" + new Integer(colIndex).toString());
				}
			}
	
			sqlStr =PubFunc.keyWord_reback(SafeCode.decode(sqlStr));
	        
			try
			{
				int rowCount = 1;
				this.frowset = dao.search(sqlStr);
				while (this.frowset.next())
				{
					String nASE = this.frowset.getString("NBASE");
					String a0100 = this.frowset.getString("A0100");
					String a00Z0 = this.frowset.getDate("A00Z0").toString();
					String a00Z1 = this.frowset.getString("A00Z1");
					String b0110 = this.frowset.getString("b0110");
					String e0122 = this.frowset.getString("e0122");
					String a0101 = this.frowset.getString("a0101");
	
					
					/*
					if(royalty_valid.equals("1")&&royalty_valid.trim().length()>0)  //提成工资，关联指标必须输出
					{
						String[] temps=royalty_relation_fields.trim().split(",");
						for(int i=0;i<temps.length;i++)
						{
							if(temps[i].trim().length()>0)
							{
								boolean isValue=true;
								if(this.frowset.getString(temps[i])==null)
								{
									isValue=false;
								}
								else
								{
									String value=this.frowset.getString(temps[i]);
									if(value.trim().length()==0)
										isValue=false;
								}
								if(!isValue)
								{
									throw new GeneralException(a0101+"["+DataDictionary.getFieldItem(temps[i]).getItemdesc()+"]指标没有数据，导出模板失败！");
								}
							}
						}
						
					}
					*/
					
					
					String flag = nASE + "|" + a0100 + "|" + a00Z0 + "|" + a00Z1;
					b0110 = b0110 != null ? AdminCode.getCodeName("UN", b0110) : "";
					e0122 = e0122 != null ? AdminCode.getCodeName("UM", e0122) : "";
					row = sheet.createRow(rowCount++);
	
				
					if(!(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))&&(onlyname==null||onlyname.trim().length()==0||_flag)&&!"1".equals(royalty_valid))
					{
						cell = row.createCell(0);
						cell.setCellValue(cellStr(flag));
						cell.setCellStyle(styleCol0);
					}			
	
					// cell = row.createCell((short) 1);
					//		
					// cell.setCellValue(cellStr(b0110));
					// cell.setCellStyle(style1);
					//
					// cell = row.createCell((short) 2);
					//		
					// cell.setCellValue(cellStr(e0122));
					// cell.setCellStyle(style1);
					//
					// cell = row.createCell((short) 3);
					//		
					// cell.setCellValue(cellStr(a0101));
					// cell.setCellStyle(style1);
					SimpleDateFormat dateformat=new SimpleDateFormat("yyyy-MM-dd");
					for (int i = 0; i < list.size(); i++)
					{
						Field field = (Field) list.get(i);
						String fieldName = field.getName().toLowerCase();
						String itemtype = DataDictionary.getFieldItem(fieldName).getItemtype();
						int decwidth = DataDictionary.getFieldItem(fieldName).getDecimalwidth();
						String codesetid = DataDictionary.getFieldItem(fieldName).getCodesetid();
	
						String pri = this.userView.analyseFieldPriv(fieldName);
						if ("1".equals(pri))// 只读
						{
	
						}
						if(!(SystemConfig.getPropertyValue("clientName")!=null&& "bjyd".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))&&(onlyname==null||onlyname.trim().length()==0||_flag)&&!"1".equals(royalty_valid))
							cell = row.createCell(1 + i);
						else
							cell = row.createCell(i);
						if ("N".equals(itemtype))
						{
							if(styleMap.get(decwidth+"")!=null)
							{
								HSSFCellStyle style = (HSSFCellStyle)styleMap.get(decwidth+"");
								cell.setCellStyle(style);
							}else{
								HSSFCellStyle style = dataStyle(wb);
								style.setAlignment(HorizontalAlignment.RIGHT);
								style.setWrapText(true);
								HSSFDataFormat hdf = wb.createDataFormat();
								style.setDataFormat(hdf.getFormat(decimalwidth(decwidth)));
								cell.setCellStyle(style);
								styleMap.put(decwidth+"", style);
							}
							if(this.frowset.getString(fieldName)!=null){
								BigDecimal bd = new BigDecimal(this.frowset.getString(fieldName));
								BigDecimal bd2=bd.setScale(decwidth, bd.ROUND_HALF_UP);
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								cell.setCellValue(bd2.doubleValue());
								}
						} 
						else if ("D".equals(itemtype))
						{
							if(this.frowset.getDate(fieldName)==null)
							{
								cell.setCellValue(new HSSFRichTextString(""));
							}
							else
							{
								cell.setCellValue(new HSSFRichTextString(dateformat.format(this.frowset.getDate(fieldName))));
							}
							cell.setCellStyle(style1);
						}
						else
						{
							String value = this.frowset.getString(fieldName);
							if (value != null)
							{
								String codevalue = value;
								if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
								{
									if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))
										value = AdminCode.getCode(codesetid, codevalue) != null ? AdminCode.getCode(codesetid, codevalue).getCodename() : "";
								}else
								{
									if (codevalue.trim().length() > 0 && codesetid != null && codesetid.trim().length() > 0 && !"0".equals(codesetid))
									{
										if("UM".equalsIgnoreCase(codesetid))
										{
											if(AdminCode.getCode(codesetid, codevalue)!=null)
											{
												value = AdminCode.getCode(codesetid, codevalue) != null ? codevalue+":"+AdminCode.getCode(codesetid, codevalue).getCodename() : "";
											}
											else
												value = AdminCode.getCode("UN", codevalue) != null ? codevalue+":"+AdminCode.getCode("UN", codevalue).getCodename() : "";
											
											
										}
										else
											value = AdminCode.getCode(codesetid, codevalue) != null ? codevalue+":"+AdminCode.getCode(codesetid, codevalue).getCodename() : "";
									}
								}						
								cell.setCellValue(new HSSFRichTextString(value));
							}
							cell.setCellStyle(style1);
						}
	
					}
				}
				rowCount--;
				int index = 0;
				String[] lettersUpper =
				{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
				
				int div = 0;
				int mod = 0;			
				for (int n = 0; n < codeCols.size(); n++)
				{
					String codeCol = (String) codeCols.get(n);
					String[] temp = codeCol.split(":");
					String codesetid = temp[0];
					int codeCol1 = Integer.valueOf(temp[1]).intValue();
					StringBuffer codeBuf = new StringBuffer();
					if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
					{
						codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");
					} else
					{
						if (!"UN".equals(codesetid))
						{
							if("UM".equalsIgnoreCase(codesetid))
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where ( codesetid='UM' OR codesetid='UN' ) "
										+" order by codeitemid");
							else
								codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
									+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
						}
						else if ("UN".equals(codesetid))
						{
							codeBuf.append("select count(*) from organization where codesetid='UN'");
							this.frowset = dao.search(codeBuf.toString());
							if (this.frowset.next())
								if (this.frowset.getInt(1) == 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN'");
								} else if (this.frowset.getInt(1) > 1)
								{
									codeBuf.setLength(0);
									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='"+codesetid+"'");
//									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='" + codesetid
//											+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
//									codeBuf.append(" union all ");
//									codeBuf.append("select codesetid,codeitemid,codeitemdesc from organization where codesetid='UN' and  codeitemid=parentid and childid in (select codeitemid from organization where codesetid!='UN')");
//									
								}
						}
					}
	
					this.frowset = dao.search(codeBuf.toString());
	
					int m = 0;
					while (this.frowset.next())
					{
						row = sheet.getRow(m + 0);
						if (row == null)
							row = sheet.createRow(m + 0);
						cell = row.createCell((short) (208 + index));
						if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
							cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
						else
							cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemid")+":"+this.frowset.getString("codeitemdesc")));
						m++;
					}
					if(m==0)
						m=2;
					sheet.setColumnWidth((short) (208 + index), (short) 0);
	//				String strFormula = "$H" + lettersUpper[index] + "$1:$H" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
					div = index/26;
					mod = index%26;
					String strFormula = "$" +lettersUpper[7+div]+ lettersUpper[mod] + "$1:$"+lettersUpper[7+div]+  lettersUpper[mod] + "$" + Integer.toString(m); // 表示BA列1-m行作为下拉列表来源数据
				 
					CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);
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
	
			String outName =this.userView.getUserName()+"_";
			outName += PubFunc.getStrg() + ".xls";
	
			try
			{
				FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
				wb.write(fileOut);
				fileOut.close();
			} catch (Exception e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		//	outName = outName.replace(".xls", "#");
			/* 自助平台-员工信息-数据上报-下载模版 报错 xiaoyun 2014-10-23 start */
			//getFormHM().put("outName", SafeCode.decode(PubFunc.encrypt(outName)));
			getFormHM().put("outName", SafeCode.encode(PubFunc.encrypt(outName)));
			/* 自助平台-员工信息-数据上报-下载模版 报错 xiaoyun 2014-10-23 end */
			sheet = null;
			wb = null;
		}
		catch (Exception ex)
		{
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	
	/**
	 * 获得代码项个数
	 * @param field
	 * @param codesetid
	 * @param dao
	 * @return
	 */
	private int getCodeSize(Field field,String codesetid,ContentDAO dao)
	{
		
		
			int size=0;
			FieldItem item=DataDictionary.getFieldItem(field.getName());
			if(item!=null)
			{
				String pri = this.userView.analyseFieldPriv(field.getName());
				if (!"2".equals(pri))// 没有写权限
					return 10000;
			}
			
			
			StringBuffer codeBuf=new StringBuffer("");
			if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
			{
				codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'");
			} else
			{
				if (!"UN".equals(codesetid))
				{
					if("UM".equalsIgnoreCase(codesetid))
						codeBuf.append("select count(*) from organization where ( codesetid='UM' OR codesetid='UN' ) ");
					else
						codeBuf.append("select count(*) from organization where codesetid='" + codesetid
							+ "' and  codeitemid not in (select parentid from organization where codesetid='" + codesetid + "')");
				}
				else if ("UN".equals(codesetid))
				{
					codeBuf.append("select count(*) from organization where codesetid='UN'");
				}
			}
			try
			{
				this.frowset=dao.search(codeBuf.toString());
				if(this.frowset.next())
					size=this.frowset.getInt(1);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return size;
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

}
