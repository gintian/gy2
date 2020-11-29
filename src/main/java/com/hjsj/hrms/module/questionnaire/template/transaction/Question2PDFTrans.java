package com.hjsj.hrms.module.questionnaire.template.transaction;


import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

public class Question2PDFTrans extends IBusiness {

	Font fontTitle = FontFamilyType.getFont("微软雅黑", 0, 7);
	Font fontItem = FontFamilyType.getFont("微软雅黑", 0, 6);
	Image checkImg = null;
	Image unCheckImg = null;
	@Override
    public void execute() throws GeneralException {
		//this.getFormHM().put("qnid", "17");
		//this.getFormHM().put("mainObject", "Z0DuTtqmt3k@3HJD@");
		//this.getFormHM().put("subObject", "nSpgxEgByaCwgCMHwSuu9g@3HJD@@3HJD@");
		URL url = this.getClass().getResource("/");
		String path = url.getPath();
		Properties props=System.getProperties(); //系统属性
		String sysname = props.getProperty("os.name");
		if(sysname.startsWith("Win") && path.startsWith(File.separator)){
               path = path.substring(1, path.length());
		}
		try {
			String webpath = "";
			if(path.indexOf("WEB-INF") ==-1){
				webpath = SystemConfig.getServletContext().getResource("/").getFile();
			}else{
				webpath = path.substring(0, path.indexOf("WEB-INF"));
			}
			String imagePath = webpath+"module/system/questionnaire/images/";
			SearchPreviewTemplateTrans trans = new SearchPreviewTemplateTrans();
			trans.setFrameconn(this.frameconn);
			trans.setFrowset(this.frowset);
			trans.setFormHM(this.formHM);
			trans.setFrowset(this.frowset);
			trans.setUserView(this.userView);
			trans.setFstmt(this.fstmt);
			trans.execute();
			String jsonStr = (String)trans.getFormHM().get("jsonobject");
			JSONObject questionObj = JSONObject.fromObject(jsonStr);
		
			JSONArray quesList = (JSONArray)questionObj.get("questionList");
			checkImg = Image.getInstance(imagePath+"pdfcheck.jpg");
			checkImg.scaleAbsolute(10, 10);
			unCheckImg = Image.getInstance(imagePath+"pdfuncheck.jpg");
			unCheckImg.scaleAbsolute(10, 10);
			Document document = new Document(PageSize.A4); 
			File file = new File(System.getProperty("java.io.tmpdir"),userView.getUserName()+"_questions.pdf");
			//String filePath=System.getProperty("java.io.tmpdir")+"questions.pdf";
			//new FileOutputStream(new File(""));
			PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(file));
			document.open();
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100);
			PdfPCell title = new PdfPCell(new Paragraph(questionObj.getString("qnname"),fontTitle));
			title.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			title.setBorder(0);
			title.setPaddingBottom(15);
			table.addCell(title);
			
			String instruction = questionObj.getString("instruction");
			instruction = instruction.replaceAll("<[^>]+>", "");
			//html特殊字符转回
			instruction=StringEscapeUtils.unescapeHtml(instruction);
			PdfPCell msg = new PdfPCell(new Paragraph(instruction,fontItem));
			msg.setBorder(0);
			msg.setPaddingBottom(10);
			table.addCell(msg);
			
			PdfPCell line = new PdfPCell(new Paragraph(" ",fontItem));
			line.setBorder(0);
			line.enableBorderSide(1);
			line.setBorderWidth(new Float(1.5));
			table.addCell(line);
			
			int quesIndex = 1;
			for(int i=0;i<quesList.size();i++){
				JSONObject ques = quesList.getJSONObject(i);
				int questype = ques.getInt("typeid");
				PdfPTable itemTable = null;
				switch (questype) {
					case 1:
						itemTable = selectQues2Table(ques,quesIndex);
						 quesIndex++;
						 break;
					case 2:
						itemTable = selectQues2Table(ques,quesIndex);
						 quesIndex++;
						 break;
					case 3:
						itemTable = fillQues2Table(ques,quesIndex);
						 quesIndex++;
						 break;
					case 4:
						itemTable = fillQues2Table(ques,quesIndex);
						 quesIndex++;
						 break;
					case 5:
						itemTable = imgSelectQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 6:
						itemTable = imgSelectQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 7:
						itemTable = matrixSelectQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 8:
						itemTable = matrixSelectQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 9:
						itemTable = descriptionQues2Table(ques,quesIndex);
						break;
					case 11:
						itemTable = splitLine2Table(ques,quesIndex);
						break;
					case 12:
						itemTable = scoreQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 13:
						itemTable = scaleQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 14:
						itemTable = matrixScoreQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					case 15:
						itemTable = maxtrixScaleQues2Table(ques,quesIndex);
						quesIndex++;
						break;
					default:
						break;
				} 
				
				if(itemTable!=null){
					PdfPCell cell = new PdfPCell(itemTable);
					cell.setBorder(0);
					cell.setPaddingBottom(15);
					table.addCell(cell);
				}
			}
			document.add(table);
			document.close();
			
		    this.getFormHM().clear();
		    
		    this.getFormHM().put("name",PubFunc.encrypt(userView.getUserName()+"_questions.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private PdfPTable selectQues2Table(JSONObject ques,int index) throws Exception{
		float[] widths = {0.05f,0.95f };
		PdfPTable table = new PdfPTable(widths);
		PdfPCell cell = getTitleCell(ques,3,index);
		table.addCell(cell);
		
		String answer = "";
		if(ques.containsKey("answer")){
			answer = ques.getString("answer");
			if(answer.indexOf("[")==0){
				answer = answer.replace("\"", "");
				answer = answer.replace("[", "").replace("]", "");
			}
			answer+=",";
		}
		JSONArray opts = (JSONArray)ques.get("optionList");
		for(int i=0;i<opts.size();i++){
			JSONObject opt = opts.getJSONObject(i);
			
			
			if(answer.indexOf(opt.getString("optid")+",")>-1){
				cell = new PdfPCell(checkImg);
			}else{
				cell = new PdfPCell(unCheckImg);
			}
			
			cell.setBorder(0);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(opt.getString("optname"),fontItem));
			cell.setBorder(0);
			cell.setPaddingBottom(7);
			table.addCell(cell);
			
		}
		
		return table;
	}
	
	private PdfPTable fillQues2Table(JSONObject ques,int index) throws Exception{
		float[] widths = {0.03f,0.97f};
		PdfPTable table = new PdfPTable(widths);;
		PdfPCell cell = getTitleCell(ques,2,index);
		table.addCell(cell);
		cell = new PdfPCell();
		cell.setBorder(0);
		table.addCell(cell);
		
	    JSONObject quesSet = ques.getJSONObject("set");
	    String type = quesSet.getString("inputtype");
		String width = quesSet.getString("inputwidth");
		float inputwidth = new Float(width);
		inputwidth = inputwidth>500?500:inputwidth;

		JSONArray opts = (JSONArray)ques.get("optionList");
		
		if(opts==null || opts.size()==0){
			String answer = " ";
			if(ques.containsKey("answer"))
				  answer = ques.getString("answer");
			PdfPTable line = new PdfPTable(1);
			line.setTotalWidth(new Float(inputwidth));
			line.setLockedWidth(true);
			line.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			cell = new PdfPCell(new Paragraph(answer,fontItem));
			cell.setPaddingBottom(5);
			if("5".equals(type)){
			  String height = quesSet.getString("inputheight");
			  cell.setMinimumHeight(new Float(height)*10);
			}else{
				cell.setBorder(0);
				cell.enableBorderSide(2);
			}
			
			line.addCell(cell);
			
			cell = new PdfPCell(line);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(0);
			table.addCell(cell);
			
			return table;
			
		}

		
		ArrayList cellList = new ArrayList();
		int length = 0;
		for(int i=0;i<opts.size();i++){
			JSONObject opt = opts.getJSONObject(i);
			String name = opt.getString("optname");
			cell = new PdfPCell(new Paragraph(name,fontItem));
			cell.setBorder(0);
			cellList.add(cell);
			
			if(length==200)
				continue;
			int realWidth = name.length()*6;
			if(realWidth==200){
				length=200;
			}else if(realWidth>length)
				length = realWidth;
		}
		
		JSONArray answer = null;
		if(ques.containsKey("answer"))
			  answer = ques.getJSONArray("answer");
		float[] colWidth = new float[2];
		colWidth[0] = length/210f;
		colWidth[1] = 0.9f;
		PdfPTable item = new PdfPTable(colWidth);
		for(int i=0;i<cellList.size();i++){
			item.addCell((PdfPCell)cellList.get(i));
			
			PdfPTable line = new PdfPTable(1);
			line.setTotalWidth(new Float(width));
			line.setLockedWidth(true);
			line.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			String value = " ";
			if(answer!=null)
				value = answer.getJSONObject(i).getString("optvalue");
			cell = new PdfPCell(new Paragraph(value,fontItem));
			cell.setPaddingBottom(5);
			cell.setBorder(0);
			cell.enableBorderSide(2);
			line.addCell(cell);
			
			cell = new PdfPCell(line);
			cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
			cell.setBorder(0);
			cell.setPaddingBottom(10);
			item.addCell(cell);
		}
		
		cell = new PdfPCell(item);
		cell.setBorder(0);
		table.addCell(cell);
		return table;
	}
	
	private PdfPTable imgSelectQues2Table(JSONObject ques,int index) throws Exception{
		float[] widths = {0.03f,0.2f,0.2f,0.2f,0.2f,0.2f};
		PdfPTable table = new PdfPTable(widths);
		JSONArray opts = (JSONArray)ques.get("optionList");
		PdfPCell cell = getTitleCell(ques,6,index);
		table.addCell(cell);
		int itemIndex = 1;
		float[] itemTW = {0.15f,0.85f};
		
		String answer = "";
		if(ques.containsKey("answer")){
			answer = ques.getString("answer");
			if(answer.indexOf("[")==0){
				answer = answer.replace("\"", "");
				answer = answer.replace("[", "").replace("]", "");
			}
			answer+=",";
		}
		for(int i=0;i<opts.size();i++){
			if(itemIndex==1){
				cell = new PdfPCell();
			    cell.setBorder(0);
				table.addCell(cell);
			}
			JSONObject opt = opts.getJSONObject(i);
			
			
			PdfPTable it = new PdfPTable(itemTW);
			String imgUrl = opt.getString("imgurl");

			InputStream is = null;
			try {
				//xus 20/4/20 vfs改造  我的问卷，点击右上的按钮切换成列表显示，预览，导出PDF，跳转到空白界面，后台报错
				is = VfsService.getFile(imgUrl);
				byte[] buffer = null;
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buff = new byte[100];
                int rc = 0;
                while ((rc = is.read(buff, 0, 100)) > 0) {
                    byteArrayOutputStream.write(buff, 0, rc);
                }
                buffer = byteArrayOutputStream.toByteArray();
				
				Image img = Image.getInstance(buffer);
				img.scaleAbsolute(75, 100);
				cell = new PdfPCell(img);
				cell.setColspan(2);
				cell.setBorder(0);
				it.addCell(cell);
				if(answer.indexOf(opt.getString("optid")+",")>-1){
					cell = new PdfPCell(checkImg);
				}else{
					cell = new PdfPCell(unCheckImg);
				}
				//cell = new PdfPCell(unCheckImg);
				cell.setBorder(1);
				cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				it.addCell(cell);
				cell = new PdfPCell(new Paragraph(opt.getString("optname"),fontItem));
				cell.setBorder(0);
				it.addCell(cell);
				
				cell = new PdfPCell(it);
				cell.setBorder(0);
				cell.setPaddingBottom(10);
				cell.setPaddingRight(10);
				table.addCell(cell);
				itemIndex++;
				if(itemIndex==6){
					itemIndex=1;
				}
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				PubFunc.closeIoResource(is);
			}
		}
		for(;itemIndex<6;itemIndex++){
			cell = new PdfPCell();
			cell.setBorder(0);
			table.addCell(cell);
		}
		
		return table;
	}
	
	private PdfPTable matrixSelectQues2Table(JSONObject ques,int index) throws Exception{
		PdfPTable contable = new PdfPTable(1);
		PdfPCell cell = getTitleCell(ques,null,index);
		contable.addCell(cell);
		
		JSONArray levs = (JSONArray)ques.get("levelList");
		float[] width = new float[levs.size()+1];
		PdfPTable table = new PdfPTable(levs.size()+1);
		
		cell = new PdfPCell();
		table.addCell(cell);
		for(int i=0;i<levs.size();i++){
		    JSONObject lev = levs.getJSONObject(i);
		    cell = new PdfPCell(new Paragraph(lev.getString("optname"),fontItem));
		    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    cell.setPaddingBottom(7);
			table.addCell(cell);
		}
		JSONArray answer = new JSONArray();
		if(ques.containsKey("answer")){
			answer = ques.getJSONArray("answer");
		}
		JSONArray opts = (JSONArray)ques.get("optionList");
		for(int i=0;i<opts.size();i++){
			JSONObject opt = opts.getJSONObject(i);
			cell = new PdfPCell(new Paragraph(opt.getString("optname"),fontItem));
			cell.setPaddingBottom(7);
			table.addCell(cell);
			for(int j=0;j<levs.size();j++){
				cell = null;
				for(int k=0;k<answer.size();k++){
					JSONObject value = answer.getJSONObject(k);
					if(!opt.getString("optid").equals(value.getString("optid")))
						continue;
					if(levs.getJSONObject(j).getString("optid").equals(value.getString("optvalue"))){
						cell = new PdfPCell(checkImg);
						answer.remove(k);
					}
					break;
				}
				if(cell==null)
					cell = new PdfPCell(unCheckImg);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
		}
		
		cell = new PdfPCell(table);
		cell.setBorder(0);
		cell.setPaddingLeft(15);
		contable.addCell(cell);
		return contable;
	}
	
	private PdfPTable descriptionQues2Table(JSONObject ques,int index) throws Exception{
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell(new Paragraph(ques.getString("name"),fontTitle));
		cell.setBorder(0);
		table.addCell(cell);
		return table;
	}
	
	private PdfPTable splitLine2Table(JSONObject ques,int index) throws Exception{
		PdfPTable table = new PdfPTable(1);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setCellEvent(new CustomCell());
		table.addCell(cell);
		return table;
	}
	
	private PdfPTable scoreQues2Table(JSONObject ques,int index) throws Exception{
		PdfPTable table = new PdfPTable(new float[]{0.03f,0.1f,0.87f});
		
		PdfPCell cell = getTitleCell(ques,3,index);
		table.addCell(cell);
		
		cell = new PdfPCell();
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		
		String anwser = "";
		if(ques.containsKey("answer"))
			anwser = ques.getJSONArray("answer").getJSONObject(0).getString("score");
		
		//{"minscore":"1","extrainput":"false","middledesc":"中","rightdesc":"高","maxscore":"100","leftdesc":"低","required":"false","skip":"true"}
		cell = new PdfPCell(new Paragraph(anwser,fontItem));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(4);
		cell.setCellEvent(new PdfPCellEvent() {
			@Override
            public void cellLayout(PdfPCell cell, Rectangle pos, PdfContentByte[] arg2) {
				PdfContentByte cb = arg2[PdfPTable.LINECANVAS];
			    cb.saveState();
			    cb.setLineWidth(0.5f);
			    //下划线
			    cb.moveTo(pos.left(), pos.bottom());
			    cb.lineTo(pos.left()+50, pos.bottom());
			    
			    cb.stroke();
			    cb.restoreState();
			}
		});
		table.addCell(cell);
		
		JSONObject quesSet = ques.getJSONObject("set");
		cell = new PdfPCell(new Paragraph("*分值范围为"+quesSet.getString("minscore")+"("+quesSet.getString("leftdesc")+
				")~"+quesSet.getString("maxscore")+"("+quesSet.getString("rightdesc")+")",fontItem));
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPadding(4);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		return table;
	}
	
	private PdfPTable scaleQues2Table(JSONObject ques,int index) throws Exception{
		float[] widths = {0.05f,0.95f };
		PdfPTable table = new PdfPTable(widths);
		PdfPCell cell = getTitleCell(ques,3,index);
		table.addCell(cell);
		JSONObject quesSet = ques.getJSONObject("set");
		JSONArray levs = quesSet.getJSONArray("levels");
		
		String answer = "";
		if(ques.containsKey("answer"))
			answer = ques.getJSONArray("answer").getJSONObject(0).getString("score");
		for(int i=0;i<levs.size();i++){
			JSONObject opt = levs.getJSONObject(i);
			if(answer.equals(opt.getString("score")))
				cell = new PdfPCell(checkImg);
			else
				cell = new PdfPCell(unCheckImg);
			cell.setBorder(0);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			
			String score = "";
			 if(quesSet.containsKey("showscore") && "true".equals(quesSet.getString("showscore")))
		    		score = "("+opt.getString("score")+"分)";
			cell = new PdfPCell(new Paragraph(opt.getString("text")+score,fontItem));
			cell.setBorder(0);
			cell.setPaddingBottom(7);
			table.addCell(cell);
			
		}
		
		return table;
	}
	
	private PdfPTable matrixScoreQues2Table(JSONObject ques,int index) throws Exception{
		PdfPTable table = new PdfPTable(new float[]{0.03f,0.2f,0.77f});
		
		PdfPCell cell = getTitleCell(ques,3,index);
		table.addCell(cell);
		PdfPCellEvent cev = new PdfPCellEvent() {
			@Override
            public void cellLayout(PdfPCell cell, Rectangle pos, PdfContentByte[] arg2) {
				PdfContentByte cb = arg2[PdfPTable.LINECANVAS];
			    cb.saveState();
			    cb.setLineWidth(0.5f);
			    //下划线
			    cb.moveTo(pos.left(), pos.bottom()+1);
			    cb.lineTo(pos.left()+50, pos.bottom()+1);
			    
			    cb.stroke();
			    cb.restoreState();
			}
		};
		
		JSONArray opts = ques.getJSONArray("optionList");
		JSONArray answer = null;
		if(ques.containsKey("answer"))
			answer = ques.getJSONArray("answer");
		for(int i=0;i<opts.size();i++){
			JSONObject opt = opts.getJSONObject(i);
			cell = new PdfPCell();
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
			
			cell = new PdfPCell(new Paragraph(opt.getString("optname"),fontItem));
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
			
			String value = "";
			if(answer!=null)
				value = answer.getJSONObject(i).getString("score");
			cell = new PdfPCell(new Paragraph(value,fontItem));
			cell.setBorder(Rectangle.NO_BORDER);
			cell.setPaddingBottom(5);
			cell.setCellEvent(cev);
			table.addCell(cell);
		}
		cell = new PdfPCell();
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
		JSONObject quesSet = ques.getJSONObject("set");
		cell = new PdfPCell(new Paragraph("*分值范围为"+quesSet.getString("minscore")+"("+quesSet.getString("leftdesc")+
				")~"+quesSet.getString("maxscore")+"("+quesSet.getString("rightdesc")+")",fontItem));
		cell.setColspan(2);
		cell.setBorder(0);
		cell.setPaddingTop(10);
		table.addCell(cell);
		return table;
	}
	
	
	private PdfPTable maxtrixScaleQues2Table(JSONObject ques,int index) throws Exception{
		PdfPTable contable = new PdfPTable(1);
		PdfPCell cell = getTitleCell(ques,null,index);
		contable.addCell(cell);
		
		JSONObject quesSet = ques.getJSONObject("set");
		JSONArray levs = (JSONArray)quesSet.get("levels");
		PdfPTable table = new PdfPTable(levs.size()+1);
		
		cell = new PdfPCell();
		table.addCell(cell);
		for(int i=0;i<levs.size();i++){
		    JSONObject lev = levs.getJSONObject(i);
		    String score = "";
		    if(quesSet.containsKey("showscore") && "true".equals(quesSet.getString("showscore")))
		    		score = "("+lev.getString("score")+"分)";
		    cell = new PdfPCell(new Paragraph(lev.getString("text")+score,fontItem));
		    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		    cell.setPaddingBottom(7);
			table.addCell(cell);
		}
		
		JSONArray opts = (JSONArray)ques.get("optionList");
		JSONArray answer = null;
		if(ques.containsKey("answer"))
			answer = ques.getJSONArray("answer");
		for(int i=0;i<opts.size();i++){
			JSONObject opt = opts.getJSONObject(i);
			cell = new PdfPCell(new Paragraph(opt.getString("optname"),fontItem));
			cell.setPaddingBottom(7);
			table.addCell(cell);
			for(int j=0;j<levs.size();j++){
				String value = "0";
				if(answer!=null)
					value = answer.getJSONObject(i).getString("score");
				if(Float.parseFloat(value)==Float.parseFloat(levs.getJSONObject(j).getString("score")))
					cell = new PdfPCell(checkImg);
				else
					cell = new PdfPCell(unCheckImg);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
		}
		
		cell = new PdfPCell(table);
		cell.setBorder(0);
		cell.setPaddingLeft(15);
		contable.addCell(cell);
		return contable;
		
		
	}
	
	
	private PdfPCell getTitleCell(JSONObject ques,Integer colspan,int index){
		PdfPCell cell = new PdfPCell(new Paragraph(index+". "+ques.getString("name"),fontTitle));
		cell.setBorder(0);
		cell.setPaddingBottom(10);
		if(colspan!=null)
		 cell.setColspan(colspan);
		return cell;
	}
	
	class CustomCell implements PdfPCellEvent {

		@Override
        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
		    PdfContentByte cb = canvases[PdfPTable.LINECANVAS];
		    cb.saveState();
		    cb.setLineWidth(1);
		    cb.setLineDash(new float[] { 5.0f, 5.0f }, 0);
		    cb.moveTo(position.left(), position.bottom());
		    cb.lineTo(position.right(), position.bottom());
		    cb.stroke();
		    cb.restoreState();
		}

	}
}
