package com.hjsj.hrms.transaction.general.inform.informcheck;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class PrintWordTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String infor=(String)this.getFormHM().get("infor");
			infor=infor!=null&&infor.trim().length()>0?infor:"1";
			
		String dbname=(String)this.getFormHM().get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		
		String itemid=(String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		
	    ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		
		ArrayList itemlist = itemList(infor,itemid);
		
		String tabename=dbname+"a01";
		
		String checkinfor = "";
	
		String outname="exportChangeInfo.xls";
		FileOutputStream fileOut = null;
		try {    
			ContentDAO dao  = new ContentDAO(this.frameconn);
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			
			HSSFCellStyle titlestyle = style(workbook,0);
			HSSFCellStyle centerstyle = style(workbook,1);
			centerstyle.setWrapText(true);
			
			int inforgroup = YksjParser.forPerson;
			if("2".equals(infor)){
				tabename=dbname;
				inforgroup = YksjParser.forUnit;
			}else if("3".equals(infor)){
				tabename=dbname;
				inforgroup = YksjParser.forPosition;
			}

			int n=0;
			for(int i=0;i<itemlist.size();i++){
				ArrayList list = (ArrayList)itemlist.get(i);
				if(list.size()<4)
					continue;
				
				String formula=(String)list.get(2);
				formula=formula!=null?formula:"";
				
				String formulaname=(String)list.get(3);
				formulaname=formulaname!=null?formulaname:"";
				
				if(formula.trim().length()<1)
					continue;

				YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forSearch, YksjParser.LOGIC
						,inforgroup,"Ht",dbname);
				yp.setCon(this.getFrameconn());
				boolean b = yp.Verify_where(formula.trim());
				if (!b) {
					checkinfor =formulaname+ResourceFactory.getProperty("workdiary.message.review.failure")+"!\n\n";
					checkinfor += yp.getStrError();
				} 
				
				yp.run(formula.trim());
//				String privsql =this.userView.getPrivSQLExpression(formula.trim(),dbname,false,alUsedFields);
				ArrayList usrlist = yp.getUsedSets();
				String privsql = " from "+yp.getTempTableName();
//				for(int j=0;j<usrlist.size();j++){
//					if(j!=0)
//						privsql+=",";
//					privsql+=dbname+usrlist.get(j);
//				}
				
				String wherestr = yp.getSQL();//公式的结果
				HashMap hashmap = yp.getMapUsedFieldItems();
				Iterator it =hashmap.values().iterator();
				StringBuffer buf = new StringBuffer();
				buf.append("select ");
				if("1".equals(infor)){
					buf.append("(select org.codeitemdesc from organization org,"+tabename);
					buf.append(" where org.codeitemid="+tabename+".B0110 and "+tabename+".a0100=a.A0100) as B0110,");

					buf.append("(select org.codeitemdesc from organization org,"+tabename);
					buf.append(" where org.codeitemid="+tabename+".E0122 and "+tabename+".a0100=a.A0100) as E0122,");

					buf.append("(select org.codeitemdesc from organization org,"+tabename);
					buf.append(" where org.codeitemid="+tabename+".E01A1 and "+tabename+".a0100=a.A0100) as E01A1");

					buf.append(",(select A0101 from "+tabename+" where "+tabename+".a0100=a.A0100) as A0101");
				}else if("2".equals(infor)){
					buf.append("(select org.codeitemdesc from organization org,"+tabename);
					buf.append(" where org.codeitemid="+tabename+".B0110 and "+tabename+".B0110=a.B0110) as B0110");
				}else if("3".equals(infor)){
					buf.append("(select org.codeitemdesc from organization org,"+tabename);
					buf.append(" where org.codeitemid="+tabename+".E01A1 and "+tabename+".E01A1=a.E01A1) as E01A1");
				}
				ArrayList itemarr = new ArrayList();
				while(it.hasNext()){
					FieldItem item = (FieldItem)it.next();
					buf.append(",");
					buf.append(item.getItemid());
					itemarr.add(item);
				}
				buf.append(privsql+" a where ");
				buf.append(wherestr);
				if(!this.userView.isSuper_admin()){
					String codeWhere = codeWhere(dbname,infor);
					if(codeWhere!=null&&codeWhere.trim().length()>0)
						buf.append(" and "+codeWhere);
				}
				this.frowset=dao.search(buf.toString());
				int j=0;
				while(this.frowset.next()){
					if(j<1){
						ExportExcelUtil.mergeCell(sheet, (short)n, (short)0,(short)n, (short)3);
						row = sheet.createRow((short)n);
						csCell =row.createCell((short)0);
						HSSFRichTextString  titlecontext = new HSSFRichTextString((String)list.get(3));
						csCell.setCellValue(titlecontext);
						csCell.setCellStyle(titlestyle);
						n++;
						ExportExcelUtil.mergeCell(sheet, 0, (short)0,0, (short)0);
						HSSFRichTextString context = null;
						if("1".equals(infor)){
							sheet.setColumnWidth((short)0,(short)5000);
							sheet.setColumnWidth((short)1,(short)5000);
							sheet.setColumnWidth((short)2,(short)5000);
							sheet.setColumnWidth((short)3,(short)5000);
							sheet.setColumnWidth((short)4,(short)5000);
							row = sheet.createRow((short)n);
							csCell =row.createCell((short)0);	
							context = new HSSFRichTextString(ResourceFactory.getProperty("hrms.b0110"));
							csCell.setCellValue(context);
							csCell.setCellStyle(centerstyle);

							csCell =row.createCell((short)1);
							context = new HSSFRichTextString(ResourceFactory.getProperty("label.commend.um"));
							csCell.setCellValue(context);
							csCell.setCellStyle(centerstyle);

							csCell =row.createCell((short)2);
							context = new HSSFRichTextString(ResourceFactory.getProperty("kjg.title.job"));
							csCell.setCellValue(context);
							csCell.setCellStyle(centerstyle);

							csCell =row.createCell((short)3);
							context = new HSSFRichTextString(ResourceFactory.getProperty("columns.archive.name"));
							csCell.setCellValue(context);
							csCell.setCellStyle(centerstyle);
							for(int m=0;m<itemarr.size();m++){
								FieldItem item = (FieldItem)itemarr.get(m);
								csCell =row.createCell((short)(m+4));
								context = new HSSFRichTextString(item.getItemdesc());
								csCell.setCellValue(context);
								csCell.setCellStyle(centerstyle);
							}
						}else if("2".equals(infor)){
							sheet.setColumnWidth((short)0,(short)5000);
							sheet.setColumnWidth((short)1,(short)5000);
							row = sheet.createRow((short)n);
							csCell =row.createCell((short)0);	
							context = new HSSFRichTextString(ResourceFactory.getProperty("label.commend.um"));
							csCell.setCellValue(context);
							csCell.setCellStyle(centerstyle);
							for(int m=0;m<itemarr.size();m++){
								FieldItem item = (FieldItem)itemarr.get(m);
								csCell =row.createCell((short)(m+1));
								context = new HSSFRichTextString(item.getItemdesc());
								csCell.setCellValue(context);
								csCell.setCellStyle(centerstyle);
							}
						}else if("3".equals(infor)){
							sheet.setColumnWidth((short)0,(short)5000);
							sheet.setColumnWidth((short)1,(short)5000);
							row = sheet.createRow((short)n);
							csCell =row.createCell((short)0);	
							context = new HSSFRichTextString(ResourceFactory.getProperty("kjg.title.job"));
							csCell.setCellValue(context);
							csCell.setCellStyle(centerstyle);
							for(int m=0;m<itemarr.size();m++){
								FieldItem item = (FieldItem)itemarr.get(m);
								csCell =row.createCell((short)(m+1));
								context = new HSSFRichTextString(item.getItemdesc());
								csCell.setCellValue(context);
								csCell.setCellStyle(centerstyle);
							}
						}
					}
					n++;
				
					row = sheet.createRow((short)n);
					HSSFRichTextString context = null;
					if("1".equals(infor)){
						csCell =row.createCell((short)0);
						context = new HSSFRichTextString(this.frowset.getString("B0110"));
						csCell.setCellValue(context);
						csCell.setCellStyle(centerstyle);

						csCell =row.createCell((short)1);
						context = new HSSFRichTextString(this.frowset.getString("E0122"));
						csCell.setCellValue(context);
						csCell.setCellStyle(centerstyle);

						csCell =row.createCell((short)2);
						context = new HSSFRichTextString(this.frowset.getString("E01A1"));
						csCell.setCellValue(context);
						csCell.setCellStyle(centerstyle);

						csCell =row.createCell((short)3);
						context = new HSSFRichTextString(this.frowset.getString("A0101"));
						csCell.setCellValue(context);
						csCell.setCellStyle(centerstyle);
					}else if("2".equals(infor)){
						csCell =row.createCell((short)0);
						context = new HSSFRichTextString(this.frowset.getString("B0110"));
						csCell.setCellValue(context);
						csCell.setCellStyle(centerstyle);
					}else if("3".equals(infor)){
						csCell =row.createCell((short)0);
						context = new HSSFRichTextString(this.frowset.getString("E01A1"));
						csCell.setCellValue(context);
						csCell.setCellStyle(centerstyle);
					}
					
					for(int m=0;m<itemarr.size();m++){
						FieldItem item = (FieldItem)itemarr.get(m);
						if("1".equals(infor))
							csCell =row.createCell((short)(m+4));
						else if("2".equals(infor))
							csCell =row.createCell((short)(m+1));
						else if("3".equals(infor))
							csCell =row.createCell((short)(m+1));
						if(item.isCode()){
							context = new HSSFRichTextString(AdminCode.getCodeName(item.getCodesetid(),this.frowset.getString(item.getItemid())));
							csCell.setCellValue(context);
						}else{
							if("D".equalsIgnoreCase(item.getItemtype())){
								String datevalue = PubFunc.FormatDate(this.frowset.getDate(item.getItemid()));
								if(datevalue!=null&&datevalue.trim().length()>9){
									datevalue=datevalue.substring(0,10);
								}
								context = new HSSFRichTextString(datevalue);
								csCell.setCellValue(context);
							}else{
								context = new HSSFRichTextString(this.frowset.getString(item.getItemid()));
								csCell.setCellValue(context);
							}
						}
						csCell.setCellStyle(centerstyle);
					}
					j++;
				}
				n+=2;
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
		}catch(Exception e){
			outname="no";
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
		}
		
//		this.getFormHM().put("outName",SafeCode.encode(PubFunc.encrypt(outname)));
		//20/3/17 xus vfs改造
		this.getFormHM().put("outName",PubFunc.encrypt(outname));
		this.getFormHM().put("info",SafeCode.encode(checkinfor));
	}
	/**
     * 设置excel表格效果
     * @param styles 设置不同的效果
     * @param workbook 新建的表格
     */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		
		
		switch(styles){
		
		case 0:
				HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),15);
				fonttitle.setBold(true);//加粗 
				style.setFont(fonttitle);
				style.setAlignment(HorizontalAlignment.LEFT );
		        break;			
		case 1:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);               	
				break;		
		case 4:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			  break;
		default:		
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	  
				break;
		}
		return style;
	}
	/**
     * 设置excel字体效果
     * @param fonts 设置不同的字体
     * @param size 设置字体的大小
     * @param workbook 新建的表格
     */
	public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size){
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
	private ArrayList itemList(String infor,String item){
		ArrayList itemlist = new ArrayList();
		ContentDAO dao  = new ContentDAO(this.frameconn);
		
		String[] itemarr = item.split(",");

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select Expression,fieldsetid,itemid,itemtype,AuditingInformation,AuditingFormula from fielditem where fieldsetid in(");
		if("1".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'A%' and useflag='1'");
		}else if("2".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'B%' and useflag='1'");
		}else if("3".equals(infor)){
			sqlstr.append("select fieldsetid from fieldset where fieldsetid like 'K%' and useflag='1'");
		}
		sqlstr.append(") and (AuditingInformation is not null or AuditingFormula is not null)");

		try {
			this.frowset = dao.search(sqlstr.toString());
			while(this.frowset.next()){
				String expression = this.frowset.getString("Expression");
				expression=expression!=null&&expression.trim().length()>0?expression:"";
				
				String AuditingInformation = this.frowset.getString("AuditingInformation");
				AuditingInformation=AuditingInformation!=null&&AuditingInformation.trim().length()>0?AuditingInformation:"";
				
				String AuditingFormula = this.frowset.getString("AuditingFormula");
				AuditingFormula=AuditingFormula!=null&&AuditingFormula.trim().length()>0?AuditingFormula:"";
				
				if(expression.length()<1&&(AuditingInformation.length()>0||AuditingFormula.length()>0)){
					ArrayList list = new ArrayList();
					String itemid = this.frowset.getString("itemid");

					for(int i=0;i<itemarr.length;i++){
						if(itemarr[i].equalsIgnoreCase(itemid)){
							list.add(itemid);
							list.add(this.frowset.getString("itemtype"));
							list.add(AuditingFormula);
							list.add(AuditingInformation);
							list.add(this.frowset.getString("fieldsetid"));
							itemlist.add(list);
						}
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemlist;
	}
	private String codeWhere(String dbname,String infor){
		StringBuffer sexpr=new StringBuffer();
		StringBuffer sfactor=new StringBuffer();
		String codesetid=this.userView.getManagePrivCode();
		String value=this.userView.getManagePrivCodeValue();
		
		/**过滤条件*/
		String strwhere="";
		StringBuffer buf = new StringBuffer();
		ArrayList fieldlist = new ArrayList();
		if("1".equals(infor)){
			if("UN".equalsIgnoreCase(codesetid)){
				sexpr.append("B0110=");
				sexpr.append(value);
				sexpr.append("*`B0110=`");
				sfactor.append("1+2");
			}else if("UM".equalsIgnoreCase(codesetid)){
				sexpr.append("E0122=");
				sexpr.append(value);
				sexpr.append("*`");
				sfactor.append("1");
			}else if("@K".equalsIgnoreCase(codesetid)){
				sexpr.append("E01A1=");
				sexpr.append(value);
				sexpr.append("*`");
				sfactor.append("1");
			}
			FieldItem fielditem = DataDictionary.getFieldItem("B0110");
			fieldlist.add(fielditem);
			fielditem = DataDictionary.getFieldItem("E0122");
			fieldlist.add(fielditem);
			fielditem = DataDictionary.getFieldItem("E01A1");
			fieldlist.add(fielditem);
			buf.append(" a.A0100 in(select A0100 ");
		}else if("2".equals(infor)){
			sexpr.append("B0110=");
			sexpr.append(value);
			sexpr.append("*`B0110=`");
			sfactor.append("1+2");
			FieldItem fielditem = DataDictionary.getFieldItem("B0110");
			fieldlist.add(fielditem);
			buf.append(" a.B0110 in(select B0110 ");
			dbname="";
		}else if("3".equals(infor)){
			sexpr.append("E01A1=");
			sexpr.append(value);
			sexpr.append("*`");
			sfactor.append("1");
			buf.append(" a.E01A1 in(select E01A1 ");
			FieldItem fielditem = DataDictionary.getFieldItem("E01A1");
			fieldlist.add(fielditem);
			dbname="";
		}
		
		
		try {
			strwhere = userView.getPrivSQLExpression(sfactor.toString()+"|"+sexpr.toString(),dbname,false,true,fieldlist);
			if("2".equals(infor)){
				strwhere=strwhere.replaceAll("A01", "B01");
			}else if("3".equals(infor)){
				strwhere=strwhere.replaceAll("A01", "K01");
				strwhere = strwhere.replaceAll("B0110", "E01A1");
			}
			
			buf.append(strwhere);
			buf.append(")");
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=1;
		if("A".equalsIgnoreCase(type)){
			temp=YksjParser.STRVALUE;
		}else if("D".equalsIgnoreCase(type)){
			temp=YksjParser.DATEVALUE;
		}else if("N".equalsIgnoreCase(type)){
			temp=YksjParser.FLOAT;
		}else if("L".equalsIgnoreCase(type)){
			temp=YksjParser.LOGIC;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}
}
