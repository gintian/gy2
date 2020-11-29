package com.hjsj.hrms.transaction.train.trainexam.paper.preview;

import com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;
import org.jdom.xpath.XPath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 培训考试试卷预览导出word 注：导出时暂不支持导出表格
 * create time:2014-1-09
 * @author chenxg
 * 
 */
public class ExportPapersWordTrans extends IBusiness {

	public void execute() throws GeneralException {
		String r5300 = (String) this.getFormHM().get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String imgurl = (String) this.getFormHM().get("imgurl");
		imgurl = PubFunc.keyWord_reback(SafeCode.decode(imgurl));
		imgurl = imgurl.replace("&quot;", "/");
		String flag = (String) this.getFormHM().get("flag");
		String msg = (String) this.getFormHM().get("msg");
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String typeid = "";
		String title = "";
		String examtime = "";
		String examscore = "";
		String examdescribe = "";
		OutputStream out = null;
		int i = 0;
		int k = 1;
		try {
			String sqlstr = "select r5301,r5303,r5304,r5305 from r53 where r5300=" + r5300;
			this.frowset = dao.search(sqlstr);
			if (this.frowset.next()) {
				title = this.frowset.getString("r5301");
				examtime = this.frowset.getString("r5305");
				examscore = this.frowset.getString("r5304");
				examdescribe = this.frowset.getString("r5303");
				examdescribe = examdescribe != null && examdescribe.length() > 1 ? examdescribe : "";
			}
			String outName = this.userView.getUserName() + "_train.doc";
			// 设置纸张大小
			Document document = new Document(PageSize.A4);
			out = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + outName);
			// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
			RtfWriter2.getInstance(document, out);
			document.open();
			// 设置中文字体
			BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
					"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
			// 标题字体风格
			Font titleFont = new Font(bfChinese, 12, Font.BOLD);
			// 正文字体风格
			Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
			Paragraph title1 = new Paragraph(title, titleFont);
			// 设置标题格式对齐方式
			title1.setAlignment(Element.ALIGN_CENTER);
			document.add(title1);

			Paragraph timeAndScore = new Paragraph("考试时间：" + examtime
					+ "分钟   总分：" + examscore, contextFont);
			timeAndScore.setAlignment(Element.ALIGN_RIGHT);
			//设置行间距  12f为单倍行间距
			timeAndScore.setLeading((float)18);
			document.add(timeAndScore);

			Paragraph explain = new Paragraph(examdescribe, contextFont);
			explain.setAlignment(Element.ALIGN_LEFT);
			explain.setLeading((float)18);
			// 离上一段落（标题）空的行数
			explain.setSpacingBefore(1);
			// 设置第一行空的列数
			explain.setFirstLineIndent(20);
			document.add(explain);

			Paragraph context = null;
			QuestionesBo bo =new QuestionesBo();

			sql.append("select r5205,r5207,r5213,t.type_id");
			if ("2".equalsIgnoreCase(flag))
				sql.append(",r5208,r5209");
			sql.append(" from tr_exam_paper t left join r52 r on r.r5200=t.r5200");
			sql.append(" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300=" + r5300);
			sql.append(" where t.r5300=" + r5300);
			sql.append(" order by q.norder ,t.norder");

			this.frowset = dao.search(sql.toString());
			while (this.frowset.next()) {
				String r5208 = "";
				String r5209 = "";
				String type_id = this.frowset.getString("type_id");
				String r5213 = this.frowset.getString("r5213");
				r5213 = PubFunc.nullToStr(r5213);
				String r5205 = this.frowset.getString("r5205");
				String r5207 = this.frowset.getString("r5207");
				if ("2".equalsIgnoreCase(flag)) {
					r5208 = this.frowset.getString("r5208");
					r5209 = this.frowset.getString("r5209");
				}
				
				if (!typeid.equalsIgnoreCase(type_id)) {
					typeid = type_id;
					i++;
					k = 1;
					String qTitle = QuestionesBo.getTitle(type_id.toString(), SafeCode.encode(PubFunc.encrypt(r5300.toString())), i + "");
					context = new Paragraph(qTitle, contextFont);
					context.setAlignment(Element.ALIGN_LEFT);
					context.setLeading((float)18);
					document.add(context);
				}
				
				r5205 = bo.brToStr(r5205);
				if (r5205 != null && r5205.length() > 0
						&& (r5205.indexOf("<img") > -1 || r5205.indexOf("<IMG") > -1)) {
					context = new Paragraph("第" + k + "题(" + r5213 + "分)",
							contextFont);
					context.setAlignment(Element.ALIGN_LEFT);
					context.setLeading((float)18);
					document.add(context);
					setPicture(r5205, document, imgurl);
				} else {
					r5205 = PubFunc.nullToStr(r5205);
					String questiones = "第" + k + "题(" + r5213 + "分)"
							+ QuestionesBo.toHtml(QuestionesBo.html2Text(r5205));
					context = new Paragraph(questiones, contextFont);
					context.setAlignment(Element.ALIGN_LEFT);
					context.setLeading((float)18);
					document.add(context);
				}

				if ("5".equalsIgnoreCase(typeid) || "6".equalsIgnoreCase(typeid)) {
					context = new Paragraph("\n", contextFont);
					context.setAlignment(Element.ALIGN_LEFT);
					context.setLeading((float)18);
					context.setSpacingBefore(5);
					document.add(context);
				}

				if ("1".equalsIgnoreCase(typeid) || "2".equalsIgnoreCase(typeid)) {
					String selection = getStrSelection(r5207);
					String[] selections = null;
					if(selection!=null && selection.length() > 0)
					    selections = selection.split("`~&~`");
					else{
					    context = new Paragraph(QuestionesBo.toHtml(""), contextFont);
                        context.setAlignment(Element.ALIGN_LEFT);
                        context.setLeading((float)18);
                        document.add(context);
                        k++;
                        continue;
					}
					
					int f = 0;
					String ns = "";
					String nss = "";
					float sum = (float) 0.0;
					
					int vsg = 0;
					for(int n=0; n<selections.length;n++){
						String nselections = selections[n].replace("`:`",":");
						byte[] nsb = nselections.getBytes();
						sum = (float) (nsb.length / 2.0 * 3.55);
						if(sum>35.5&&sum<=85.2)
							vsg = 1;
						
						if(sum>85.2 || nselections.indexOf("<img") > -1 || nselections.indexOf("<IMG") > -1){
							vsg = 2;
							break;
						}
					}
					
					for (int m = 0; m < selections.length; m++) {
						String selectionss = selections[m];
						if(selectionss.length()==4)
							selectionss += "&nbsp;";
						String[] nselections = selectionss.split("`:`");
						nselections[1] = bo.brToStr(nselections[1]);
						if (nselections[1] != null && nselections[1].length() > 0
								&& (nselections[1].indexOf("<img") > -1 || nselections[1].indexOf("<IMG") > -1)) {
							if (nss.length() > 0 && nss != null) {
								context = new Paragraph(QuestionesBo.toHtml(nss), contextFont);
								context.setAlignment(Element.ALIGN_LEFT);
								context.setLeading((float) 18);
								document.add(context);
								f = 0;
								nss = "";
								sum = (float) 0.0;
								nss = "";
							}
							setPicture(nselections[0] + "：" + nselections[1], document, imgurl);
						} else {
							nselections[1] = PubFunc.nullToStr(nselections[1]);
							ns = nselections[0] + "：" + QuestionesBo.toHtml(QuestionesBo.html2Text(nselections[1]));
							if ("0".equalsIgnoreCase(msg) && vsg != 2) {

								nss += ns;
								f++;
								if ((vsg == 1 && f == 2) || (vsg == 0 && f==4)) {
									context = new Paragraph(QuestionesBo.toHtml(nss), contextFont);
									context.setAlignment(Element.ALIGN_LEFT);
									context.setLeading((float) 18);
									document.add(context);
									f = 0;
									nss = "";
								} else {
									if (f > 0) {
										nss += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
									}
								}
							}else{
								context = new Paragraph(QuestionesBo.toHtml(ns), contextFont);
								context.setAlignment(Element.ALIGN_LEFT);
								context.setLeading((float)18);
								document.add(context);
							}
						}
					}
					
					if(f!=0){
						context = new Paragraph(QuestionesBo.toHtml(nss), contextFont);
						context.setAlignment(Element.ALIGN_LEFT);
						context.setLeading((float)18);
						document.add(context);
					}

				}
				
				if ("3".equalsIgnoreCase(typeid)) {
					String ns = "A: 对         B: 错";
					context = new Paragraph(ns, contextFont);
					context.setAlignment(Element.ALIGN_LEFT);
					context.setLeading((float)18);
					document.add(context);
				}
				
				if ("2".equalsIgnoreCase(flag)) {
					r5208 = bo.brToStr(r5208);
					if (r5208 != null && r5208.length() > 0) {
						if (r5208 != null && r5208.length() > 0 && (r5208.indexOf("<img") > -1 || r5208.indexOf("<IMG") > -1)) {
							context = new Paragraph("正确答案：", contextFont);
							context.setAlignment(Element.ALIGN_LEFT);
							context.setLeading((float)18);
							document.add(context);
							setPicture(r5208, document, imgurl);
						} else {
							r5208 = PubFunc.nullToStr(r5208);
							context = new Paragraph("正确答案：" + QuestionesBo.toHtml(QuestionesBo.html2Text(r5208)), contextFont);
							context.setAlignment(Element.ALIGN_LEFT);
							context.setLeading((float)18);
							document.add(context);
						}
					} else {
						String answer = "正确答案：" + QuestionesBo.toHtml(QuestionesBo.html2Text(r5209));
						context = new Paragraph(answer, contextFont);
						context.setAlignment(Element.ALIGN_LEFT);
						context.setLeading((float)18);
						document.add(context);
					}

				}
				k++;
			}
			// 导出答案
			if ("1".equalsIgnoreCase(flag)) {
				sql.delete(0, sql.length());
				sql.append("select r5208,r5209,t.type_id");
				sql.append(" from tr_exam_paper t left join r52 r on r.r5200=t.r5200");
				sql.append(" left join tr_exam_question_type q on q.type_id=t.type_id and q.r5300="
								+ r5300);
				sql.append(" where t.r5300=" + r5300);
				sql.append(" order by q.norder ,t.norder");

				typeid = "";
				i = 0;
				k = 1;
				document.newPage();
				this.frowset = dao.search(sql.toString());
				String nanswer = "";
				float sum = (float) 0.0;
				int f = 0;
				while (this.frowset.next()) {
					String type_id = this.frowset.getString("type_id");
					String r5208 = this.frowset.getString("r5208");
					String r5209 = this.frowset.getString("r5209");
					r5208 = bo.brToStr(r5208);
					if (!typeid.equalsIgnoreCase(type_id)) {

						if ("1".equalsIgnoreCase(typeid) || "2".equalsIgnoreCase(typeid) || "3".equalsIgnoreCase(typeid)
								|| "4".equalsIgnoreCase(typeid)) {
							context = new Paragraph(QuestionesBo.toHtml(nanswer), contextFont);
							context.setAlignment(Element.ALIGN_LEFT);
							context.setLeading((float)18);
							document.add(context);
							nanswer = "";
							f = 0;
							sum = (float) 0.0;
						}
						typeid = type_id;
						i++;
						k = 1;
						context = new Paragraph(QuestionesBo.getTitle(type_id.toString(), SafeCode.encode(PubFunc.encrypt(r5300.toString())), i + ""), contextFont);
						context.setAlignment(Element.ALIGN_LEFT);
						context.setLeading((float)18);
						document.add(context);
					}
					if (r5208 != null && r5208.length() > 0) {
						if (r5208 != null && r5208.length() > 0 && r5208.indexOf("<img") > -1
								|| r5208.indexOf("<IMG") > -1) {
							context = new Paragraph("第" + k + "题：", contextFont);
							context.setAlignment(Element.ALIGN_LEFT);
							context.setLeading((float)18);
							document.add(context);
							setPicture(r5208, document, imgurl);
						} else {
							r5208 = PubFunc.nullToStr(r5208);
							r5208 = "第" + k + "题：" + QuestionesBo.toHtml(QuestionesBo.html2Text(r5208));
							if ("1".equalsIgnoreCase(typeid) || "2".equalsIgnoreCase(typeid) || "3".equalsIgnoreCase(typeid)
									|| "4".equalsIgnoreCase(typeid)) {
								byte[] r5208s = r5208.getBytes();
								sum = (float) (sum + ((r5208s.length / 2.0) + 2) * 3.55);
								
								if (sum >= 156.0) {
									context = new Paragraph(QuestionesBo.toHtml(nanswer), contextFont);
									context.setAlignment(Element.ALIGN_LEFT);
									context.setLeading((float)18);
									document.add(context);
									f = 1;
									nanswer = "";
									sum = (float) 0.0;
									nanswer = r5208;
								} else {
									if (f > 0) {
										nanswer += "&nbsp;&nbsp;&nbsp;&nbsp;";
									}
									
									if (f == 0) {
										nanswer = r5208;
										f = 1;
									} else{
										nanswer += r5208;
										f++;
									}
								}
							} else {
								context = new Paragraph(r5208, contextFont);
								context.setAlignment(Element.ALIGN_LEFT);
								context.setLeading((float)18);
								document.add(context);
							}
						}
					} else {
						String answer = "第" + k + "题：" + QuestionesBo.toHtml(QuestionesBo.html2Text(r5209));
						
						byte[] answers = answer.getBytes();
						sum = (float) (sum + ((answers.length / 2.0) + 2) * 3.55);
						if (sum >= 156.0) {
							context = new Paragraph(QuestionesBo.toHtml(nanswer), contextFont);
							context.setAlignment(Element.ALIGN_LEFT);
							context.setLeading((float)18);
							document.add(context);
							f = 1;
							nanswer = "";
							sum = (float) 0.0;
							nanswer = answer;
						} else {
							if (f > 0) {
								nanswer += "&nbsp;&nbsp;&nbsp;&nbsp;";
							}
							
							if (f == 0) {
								nanswer = answer;
								f = 1;
							} else {
								nanswer += answer;
								f++;
							}
						}
					}
					k++;
				}
				if (nanswer.length() > 0 && nanswer != null) {
					context = new Paragraph(QuestionesBo.toHtml(nanswer), contextFont);
					context.setAlignment(Element.ALIGN_LEFT);
					context.setLeading((float)18);
					document.add(context);
				}
			}
			document.close();
			this.formHM.put("outName", PubFunc.encrypt(outName));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeIoResource(out);
		}
		
	}

	/**
	 * 获得选项的字符串
	 * 
	 * @param xml
	 * @return
	 * @throws GeneralException
	 */
	public String getStrSelection(String xml) throws GeneralException {

		StringBuffer selection = new StringBuffer();
		if (xml == null || xml.length() <= 0) {
			return "";
		}
		try {
			org.jdom.Document doc = PubFunc.generateDom(xml);
			String str_path = "/Params/item";
			XPath xpath = XPath.newInstance(str_path);
			List list = xpath.selectNodes(doc);

			for (int i = 0; i < list.size(); i++) {
				org.jdom.Element el = (org.jdom.Element) list.get(i);
				String id = el.getAttributeValue("id");
				String content = el.getText();
				content=PubFunc.nullToStr(content);
				if (i != 0) {
					selection.append("`~&~`");
				}
				selection.append(id);
				selection.append("`:`");
				selection.append(content);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return selection.toString();
	}

	/**
	 * word中添加图片
	 * 
	 * @param r5205
	 * @param p
	 * @param doc
	 * @param imgFile
	 * @throws Exception
	 */
	public void setPicture(String r5205, Document document, String imgFile)
			throws Exception {
		String content = "";
		int height = 0;
		int width = 0;
		if (imgFile.indexOf("/UserFiles") > -1)
			imgFile = imgFile.substring(0, imgFile.indexOf("/UserFiles"));
		BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		while (r5205.length() > 0) {
			if (r5205.indexOf("<img") > -1 || r5205.indexOf("<IMG") > -1) {
				String imgsrc = "";
				if (r5205.indexOf("<img") > -1) {
					content = r5205.substring(0, r5205.indexOf("<img", 0));
					String img = r5205.substring(r5205.indexOf("<img", 0),
							r5205.indexOf("/>", 0) + 2);
					r5205 = r5205.substring(r5205.indexOf("/>", 0) + 2, r5205
							.length());
					imgsrc = match(img, "img", "src");
				} else if (r5205.indexOf("<IMG") > -1) {
					content = r5205.substring(0, r5205.indexOf("<IMG", 0));
					String img = r5205.substring(r5205.indexOf("<IMG", 0),
							r5205.indexOf(">", 0) + 1);
					r5205 = r5205.substring(r5205.indexOf(">", 0) + 1, r5205
							.length());
					imgsrc = match(img, "IMG", "src");
				}
				imgFile += imgsrc;
				String name = imgsrc.substring(imgsrc.lastIndexOf("/") + 1, imgsrc.length());
				try {
					File imgfile = new File(imgFile);
					BufferedImage buff = ImageIO.read(imgfile);
					height = buff.getHeight();
					width = buff.getWidth();
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.trainexam.image")
											+ name + ResourceFactory.getProperty("train.trainexam.image.notexist")));
				}
				if (height == 0)
					height = 200;
				if (width == 0)
					width = 190;
				Paragraph context = new Paragraph(QuestionesBo.toHtml(QuestionesBo.html2Text(content)), contextFont);
				context.setAlignment(Element.ALIGN_LEFT);
				context.setLeading((float)18);
				document.add(context);
				// 添加图片
				try {
					Image img = Image.getInstance(imgFile);
					img.setAbsolutePosition(0, 0);
					img.setAlignment(Image.RIGHT);// 设置图片显示位置
					img.scaleAbsolute(width, height);// 直接设定显示尺寸
					document.add(img);
				} catch (Exception e) {
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new Exception(
							ResourceFactory.getProperty("train.trainexam.image.form")));
				}
			} else {
				Paragraph context = new Paragraph(QuestionesBo
						.toHtml(QuestionesBo.html2Text(r5205)), contextFont);
				context.setAlignment(Element.ALIGN_LEFT);
				context.setLeading((float)18);
				document.add(context);
				r5205 = "";
			}
		}
		Paragraph context = new Paragraph("", contextFont);
		context.setAlignment(Element.ALIGN_LEFT);
		context.setLeading((float)18);
		document.add(context);
	}

	/**
	 * 获取图片的路径
	 * 
	 * @param source
	 * @param element
	 * @param attr
	 * @return
	 */
	public String match(String source, String element, String attr) {
		String result = "";
		String reg = "<" + element + "[^<>]*?\\s" + attr
				+ "=['\"]?(.*?)['\"].*?>";
		Matcher m = Pattern.compile(reg).matcher(source);
		if (m.find()) {
			result = m.group(1);
		}

		if (result.startsWith("http") || result.startsWith("HTTP")) {
			int n = 0;
			String[] results = result.split("");
			for (int i = 0; i < results.length; i++) {
				if ("/".equalsIgnoreCase(results[i])) {
					n++;
				}
				if (n == 3) {
					n = i;
					break;
				}
			}
			result = result.substring(n - 1, result.length());
		}
		if (result.indexOf("%20") > -1)
			result = result.replaceAll("%20", " ");
		return result;
	}
}
