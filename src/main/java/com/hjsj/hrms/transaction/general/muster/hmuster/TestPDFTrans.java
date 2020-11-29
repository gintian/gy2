package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

// 导入　ＰＯＩ库类

public class TestPDFTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		// 定义页面
		PageSize Pg = new PageSize();
		// 生成ＰＤＦ文档实例
		Document document = new Document(Pg.A4);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream("c:\\Chap0101.pdf");
			// 载入中文字库
			BaseFont bfChinese = BaseFont.createFont("c:\\windows\\fonts\\FZSTK.TTF",
					BaseFont.MACROMAN, BaseFont.NOT_EMBEDDED);
			// 输入文档实例
			PdfWriter.getInstance(document,
					fileOutputStream);
			document.open();
			/*
			 * 生成表头 读取文档 350.XLS
			 */
			//WillsRecor Re = new WillsRecor("c:\\350.xls");
			// 定义第一页的数据列表．
			//ArrayList ReL;
			// 读取一页
			//ReL = Re.Next();
			// 如果读取列表中有数据，则生成一页内容．
			//while (!ReL.isEmpty()) {
				// 定义页头
				Paragraph Title = new Paragraph();
				// 设置页面格式
				Title.setSpacingBefore(8);
				Title.setSpacingAfter(2);
				Title.setAlignment(1);
				// 定义标题
				Title.add(new Chunk("南华大学2004级学生成绩登记册",
						new com.lowagie.text.Font(bfChinese, 20,
								com.lowagie.text.Font.BOLD)));

				document.add(new Paragraph("大学体育4", new com.lowagie.text.Font(
						bfChinese, 14, com.lowagie.text.Font.BOLD)));
				// 将标题输出到ＰＤＦ文档
				document.add(Title);

				Paragraph aa = new Paragraph("2005/2006学年第一学期",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.NORMAL));
				aa.setAlignment(1);
				document.add(aa);
				// 加入任课教师
				aa = new Paragraph(" 任课教师：", new com.lowagie.text.Font(
						bfChinese, 9, com.lowagie.text.Font.NORMAL));
				aa.setAlignment(0);
				document.add(aa);
				/*
				 * 开始生成表格,首先是表头．
				 */
				Table table = new Table(15);
				// 定义表格的单元格宽度
				int WidthE[] = { 5, 3, 7, 4, 3, 3, 3, 2, 5, 3, 7, 4, 3, 3, 3 };
				// 设置表格的格式
				table.setWidth(100);
				table.setWidths(WidthE);
				table.setBorder(1);
				table.setPadding(0);
				table.setSpacing(0);
				table.setDefaultHorizontalAlignment(1);
				// 加入表头项
				table.addCell(new Cell(new Chunk("学院",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("专业",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("学号",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("姓名",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("性别",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell("　　"));
				table.addCell(new Cell("　　"));

				// 加入中间空白列，用来将表格间隔成两个小表格
				Cell Blank = new Cell("");
				Blank.setRowspan(1);
				Blank.setBorderWidth(0);
				table.addCell(Blank);

				table.addCell(new Cell(new Chunk("学院",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("专业",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("学号",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("姓名",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell(new Chunk("性别",
						new com.lowagie.text.Font(bfChinese, 10,
								com.lowagie.text.Font.BOLD))));
				table.addCell(new Cell("　　"));
				table.addCell(new Cell("　　"));

				// table.addCell(new Cell(new Chunk("20014210101", new
				// com.lowagie.text.Font(bfChinese, 10,
				// com.lowagie.text.Font.BOLD))));
				// 加入数据到表体．

				/*(for (int i = 0; i < 30; i++) {
					if (i < Re.Num) {
						Record Wills = (Record) ReL.get(i);
						table.addCell(new Cell(new Chunk(Wills.Sco,
								new com.lowagie.text.Font(bfChinese, 10,
										com.lowagie.text.Font.BOLD))));
						table.addCell(new Cell(new Chunk(Wills.SMajor
								.substring(3, 5), new com.lowagie.text.Font(
								bfChinese, 10, com.lowagie.text.Font.BOLD))));
						table.addCell(new Cell(new Chunk(Wills.SSum,
								new com.lowagie.text.Font(bfChinese, 10,
										com.lowagie.text.Font.BOLD))));
						table.addCell(new Cell(new Chunk(Wills.SName,
								new com.lowagie.text.Font(bfChinese, 10,
										com.lowagie.text.Font.BOLD))));
						table.addCell(new Cell(new Chunk("  ",
								new com.lowagie.text.Font(bfChinese, 10,
										com.lowagie.text.Font.BOLD))));
						table.addCell(new Cell("　　"));
						table.addCell(new Cell("　　"));
						if (i + 30 < Re.Num) {
							Wills = (Record) ReL.get(i + 30);
							table.addCell(Blank);
							table.addCell(new Cell(new Chunk(Wills.Sco,
									new com.lowagie.text.Font(bfChinese, 10,
											com.lowagie.text.Font.BOLD))));
							table.addCell(new Cell(new Chunk(Wills.SMajor
									.substring(3, 5),
									new com.lowagie.text.Font(bfChinese, 10,
											com.lowagie.text.Font.BOLD))));
							table.addCell(new Cell(new Chunk(Wills.SSum,
									new com.lowagie.text.Font(bfChinese, 10,
											com.lowagie.text.Font.BOLD))));
							table.addCell(new Cell(new Chunk(Wills.SName,
									new com.lowagie.text.Font(bfChinese, 10,
											com.lowagie.text.Font.BOLD))));
							table.addCell(new Cell(new Chunk("  ",
									new com.lowagie.text.Font(bfChinese, 10,
											com.lowagie.text.Font.BOLD))));
							table.addCell(new Cell("　　"));
							table.addCell(new Cell("　　"));
						} else if (i + 30 >= Re.Num) {
							Cell Blank1 = new Cell("");
							Blank1.setColspan(8);
							Blank1.setBorderWidth(0);
							table.addCell(Blank1);
						}
					}
					// 表格
				}*/
				// 输出表格到ＰＤＦ
				document.add(table);
				aa = new Paragraph(
						"　　本册为学生成绩登记原始记录册，由教研室任课教师登记一式三份，于考完后四天内送教务科"
								+ "、学生所在院系各一份，教研室自留一份．不及格成绩用红笔记录；考查课程按优(90~)，良(80~)，中(70~，及"
								+ "格(60~)和不及格记载；空白说明原因．",
						new com.lowagie.text.Font(bfChinese, 9,
								com.lowagie.text.Font.NORMAL));
				aa.setAlignment(0);
				aa.setIndentationRight(260);
				document.add(aa);
				// 页尾
				// 读取下一页的内容
				//ReL = Re.Next();
				// 添加新的一页
				//document.newPage();
			//}
		} catch (DocumentException de) {
			System.err.println(de.getMessage());
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}finally{
			PubFunc.closeIoResource(fileOutputStream);
		}
		document.close();

		/*
		 * try { Executable.printDocumentSilent("Chap0101.pdf"); } catch
		 * (IOException ioe) { System.err.println(ioe.getMessage()); }
		 */
		System.out.println("生成ＰＤＦ成功！");
	}

}

class PrintStudent {

	/**
	 * @param args
	 * @throws IOException
	 * @throws DocumentException
	 */

	public static void main(String[] args) {
		/*try {
			new TestPDFTrans().execute();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
//		 Document document = new Document();        
//	        try {
//	            PdfWriter.getInstance(document, new FileOutputStream("c:\\cc.pdf"));
//	            
//	            // step 3: we open the document
//	            document.open();
//	            
//	            // step 4: we add content to the document
//	            //楷体字
//	            BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\simkai.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            Font font = new Font(bfComic, 14);
//	            String text1 = "IDENTITY_H";
//	            document.add(new Paragraph(text1, font));
//	            
//	            bfComic = BaseFont.createFont("c:\\windows\\fonts\\simkai.ttf", BaseFont.IDENTITY_V, BaseFont.NOT_EMBEDDED);
//	            font = new Font(bfComic, 14);
//	            text1 = "IDENTITY_V";
//	            document.add(new Paragraph(text1, font));
//	            
//	            bfComic = BaseFont.createFont("c:\\windows\\fonts\\simkai.ttf", BaseFont.IDENTITY_V, BaseFont.NOT_EMBEDDED);
//	            font = new Font(bfComic, 14);
//	            text1 = "IDENTITY_V";
//	            document.add(new Paragraph(text1, font));
//	            //方正舒体
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\FZSTK.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //方正姚体
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\FZYTK.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //仿宋体
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\SIMFANG.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //黑体
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\SIMHEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //华文彩云
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\STCAIYUN.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //华文仿宋
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\STFANGSO.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //华文细黑
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\STXIHEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //华文新魏
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\STXINWEI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //华文行楷
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\STXINGKA.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //华文中宋
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\STZHONGS.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //隶书
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\SIMLI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //宋体&新宋体    (这种字体的输出不了.有问题)
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\SIMSUN.TTC", BaseFont.NOT_EMBEDDED, BaseFont.NOT_EMBEDDED);
//	            //宋体-方正超大字符集
//	            //BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\SURSONG.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//	            //幼圆
//	            BaseFont bfComic = BaseFont.createFont("c:\\windows\\fonts\\SIMYOU.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//
//	            Font font = new Font(bfComic, 14);
//	            String text1 = " 幼圆幼圆幼圆  This is the quite popular True Type font (繁體字測試VS简体字测试) ==>"+new java.util.Date();
//	            document.add(new Paragraph(text1, font));
//	        }
//	        catch(DocumentException de) {
//	            System.err.println(de.getMessage());
//	        }
//	        catch(IOException ioe) {
//	            System.err.println(ioe.getMessage());
//	        }        
//	        // step 5: we close the document
//	        document.close();
//	        System.out.println(">>> Export : "+"D:\\ChinesePDF005__.pdf");

		try{
		Document document = new Document();
	   // PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("c:\\2.pdf"));
	    document.open();
	    String text = "我爱你仙!";
	    BaseFont bf = BaseFont.createFont("c:/windows/fonts/arialuni.ttf", BaseFont.IDENTITY_H,
	        BaseFont.EMBEDDED);
	    document.add(new Paragraph(text, new com.lowagie.text.Font(bf, 12)));
	    
	    

	    document.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}

class WillsRecor {
	private ArrayList List;

	public int Num = 0;

	private static int CurrentRecordNum = 1;

	private static String SNum = "";

	private static int INum = 0;

	private InputStream myxls;

	private HSSFWorkbook wb;

	private HSSFSheet sheet;

	private HSSFRow row;

	private HSSFCell cell;

	public WillsRecor(String file) throws IOException {
		myxls = new FileInputStream(file);
		wb = new HSSFWorkbook(myxls);
		sheet = wb.getSheetAt(0); // 第一个工作表

	}

	public ArrayList Next() {

		/*
		 * 下面是主体了，我们要读取 Excel 表格 第一步，将 SNum 和 INum 设为空和 0，并将 List 设为空;
		 * 第二步，从当前记录中取一条记录，将其值放入 SNum 和 INum 中; 第三步，读取记录到 List 中.
		 */
		List = new ArrayList();
		// System.out.println(CurrentRecordNum);
		Num = 0;
		while (CurrentRecordNum <= sheet.getLastRowNum()) {

			row = sheet.getRow(CurrentRecordNum);
			cell = row.getCell(0);

			if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				if (cell.getStringCellValue().equals(SNum) && Num != 0) {
					Add();
					// System.out.println(List.get(0).toString());
					/* 添加到对象 */
				} else if (Num == 0) {
					SNum = cell.getStringCellValue();
					Add();
					// System.out.print("In Cell");
				} else
					break;
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				if (cell.getColumnIndex() == INum) {
					/* 添加到对象 */
				} else {
					INum = cell.getColumnIndex();
					break;
				}
			} else {
				System.out.println("空");
			}
			CurrentRecordNum++;
		}
		System.out.println(Num);
		return List;
	}

	public void Add() {
		try {
			Record aa = new Record(row.getCell(0).getStringCellValue(),
					row.getCell(8).getStringCellValue(), row.getCell(
							7).getStringCellValue(), row.getCell(
							1).getStringCellValue(), row.getCell(
							6).getStringCellValue(), row.getCell(
							0).getStringCellValue());
			List.add(aa);
			Num++;
		} catch (Exception e) {
			System.out.print(e.toString());
		}
	}
}

class Record {
	public String ClassN;

	public String Sco;

	public String SMajor;

	public String SSum;

	public String SName;

	public String ISex;

	public Record(String ClassN, String Sco, String SMajor, String SSum,
			String SName, String ISex) {
		this.ClassN = ClassN;
		this.Sco = Sco;
		this.SMajor = SMajor;
		this.SSum = SSum;
		this.SName = SName;

	}

	public String getSco() {
		return Sco;
	}

	public String toString() {
		return SSum;
	}

}
