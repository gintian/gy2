package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AuditCollectTrainDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		String code = "";
		String outName = "";
		String classname="";
		String ids = (String)this.getFormHM().get("ids");
		String vflag = (String) this.getFormHM().get("vflag");
		// 管理范围
		TrainCourseBo tbo = new TrainCourseBo(this.userView);
		code = tbo.getUnitIdByBusi();

		TrainClassBo bo = new TrainClassBo(this.getFrameconn());
		String msg = "";// 0：没有指定审核公式 no：审核通过 否则 审核不通过 导出审核报告

		String checkinfor = "";
		boolean flag = true;
		String[] cid = null;
		String ids1 = "";
		if (ids != null && ids.length() > 0 && ids.indexOf(":") != -1) {
			ids1 = ids;
			cid = ids.split(",");
			ids="";
			for (int i = 0; i < cid.length; i++) {
				String[] cids = cid[i].split(":");
				ids += cids[0] + ",";
			}
		}
		
		ArrayList formulaList = bo.getPxTrainFormulaList();
		if (formulaList == null || formulaList.size() == 0) {
			if(ids1!=null&&ids1.length()>0&&ids1.indexOf(":")!=-1&& "1".equalsIgnoreCase(vflag))
				ids=ids1;
			this.getFormHM().put("msg", "0");
			this.getFormHM().put("ids", ids);
			if (vflag != null && vflag.length() > 0)
				this.getFormHM().put("vflag", vflag);
			return;
		}
		if(ids!=null&&ids.length()>0)
			ids=ids.substring(0, ids.length()-1);
		try {
			ArrayList midVariableList;
			ArrayList varlist = new ArrayList();
			midVariableList = bo.getTrainMidVariableList(formulaList);
			varlist.addAll(midVariableList);

			StringBuffer sql = new StringBuffer();
			ResultSet rs = null;
			ResultSet rscn = null;
			ContentDAO dao = new ContentDAO(this.frameconn);
			String csql = "select itemdesc from t_hr_busifield where fieldsetid='R31' and itemid='R3130'";
			rscn = dao.search(csql);
			if(rscn.next())
				classname = rscn.getString("itemdesc");
			try{
				if(rscn!=null)
					rscn.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
			

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = null;
			for (int i = 0; i < formulaList.size(); i++) {
				LazyDynaBean bean = (LazyDynaBean)formulaList.get(i);
				String formula=(String)bean.get("formula");
				String formulaname=(String)bean.get("name");
	            String information=(String)bean.get("information");
				if(formula==null|| "".equals(formula))
					continue;
				HSSFRow row=null;
				HSSFCell csCell=null;
				HSSFCellStyle titlestyle = bo.style(workbook,0);
				HSSFCellStyle centerstyle = bo.style(workbook,1);
				HSSFCellStyle cloumnstyle=bo.style(workbook,2);
				HSSFCellStyle bordernone=bo.style(workbook,3);
				HSSFCellStyle bordertop=bo.style(workbook,4);
				centerstyle.setWrapText(true);
				short rows=0;
			
				YksjParser yp=null;
				int x=1;
				int y=0;

				yp = new YksjParser(userView, varlist, YksjParser.forNormal, YksjParser.LOGIC, YksjParser.forPerson, "", "");
				yp.setCon(this.frameconn);
				boolean b = yp.Verify_where(formula.trim());
				if (!b) {
					checkinfor = formulaname + ResourceFactory.getProperty("workdiary.message.review.failure") + "!\n\n";
					checkinfor += yp.getStrError();
					throw GeneralExceptionHandler.Handle(new Exception(checkinfor));
				}
				yp.setVerify(false);
				yp.run(formula.trim());
				String wherestr = yp.getSQL();// 公式的结果
				
				String sqlwhere = "";
				if (code != null && code.length() > 0 && code.indexOf("UN`") == -1) {
					String[] b0110 = code.split("`");
					for (int j = 0; j < b0110.length; j++) {
						if(b0110[j].indexOf("UN")!=-1)
							sqlwhere = "b0110 like '" + b0110[j].substring(2) + "%' or";
						else
							sqlwhere = "b0110 like '" + b0110[j] + "%' or";
					}
					sqlwhere = sqlwhere.substring(0, sqlwhere.length() - 3);
				} else if (code == null || code.length() < 1 || code.indexOf("UN`") != -1) {
					sqlwhere = "1=1";
				}
				
				//20170420 linbz  7637 导出增加举办单位，举办部门，负责人。
				sql.append("select r3106,r3101,r3130,r1.codeitemdesc unitName,r2.codeitemdesc departName from (");
				
				sql.append("select r3130,b0110,e0122,r3106,r3101 ");
				sql.append(" from r31");
				sql.append(" where " + wherestr);
				sql.append(" and (" + sqlwhere + ") and r3127<>'06'");
				if(ids!=null&&ids.length()>0){
					sql.append(" and r3101 in ("+ids+")");
				}
				
				sql.append(" ) a ");
				sql.append(" left join organization r1 on r1.codeitemid = a.b0110 ");
	            sql.append(" left join organization r2 on r2.codeitemid = a.e0122 ");
	            
				sql.append(" order by r3101");
				try {
					rs = dao.search(sql.toString());
				} catch (SQLException e) {
					e.printStackTrace();
					throw new SQLException(formulaname + "公式定义错误！");
				}
				while(rs.next())
				{
					if(y==0)
					{
						sheet = workbook.createSheet((i+1)+"");
						row=sheet.createRow(rows);
						csCell =row.createCell((short)0);
						HSSFRichTextString  titlecontext = new HSSFRichTextString(ResourceFactory.getProperty("train.job.shresult"));
						csCell.setCellStyle(titlestyle);
						csCell.setCellValue(titlecontext);
						ExportExcelUtil.mergeCell(sheet, 0, (short)0,0, (short)4);
						rows++;
						row=sheet.createRow(rows);
						HSSFRichTextString  context = new HSSFRichTextString((i+1)+":"+formulaname);
						csCell =row.createCell((short)0);
						csCell.setCellValue(context);
						csCell.setCellStyle(bordernone);
						ExportExcelUtil.mergeCell(sheet, rows, (short)0,rows, (short)4);
						rows++;
						row=sheet.createRow(rows);
						HSSFRichTextString  text = new HSSFRichTextString(ResourceFactory.getProperty("workdiary.message.message")+"："+information);
						csCell =row.createCell((short)0);
						csCell.setCellValue(text);
						csCell.setCellStyle(bordertop);
						ExportExcelUtil.mergeCell(sheet, rows, (short)0,rows, (short)4);
						rows++;
						row=sheet.createRow(rows);
						HSSFRichTextString  one = new HSSFRichTextString(ResourceFactory.getProperty("train.job.serial"));
						csCell =row.createCell((short)0);
						csCell.setCellStyle(cloumnstyle);
						csCell.setCellValue(one);
						HSSFRichTextString  four = new HSSFRichTextString(classname);
						csCell =row.createCell((short)1);
						csCell.setCellStyle(cloumnstyle);
						csCell.setCellValue(four);
						HSSFRichTextString  b0110str = new HSSFRichTextString("举办单位");
						csCell =row.createCell((short)2);
						csCell.setCellStyle(cloumnstyle);
						csCell.setCellValue(b0110str);
						HSSFRichTextString  e0122str = new HSSFRichTextString("举办部门");
						csCell =row.createCell((short)3);
						csCell.setCellStyle(cloumnstyle);
						csCell.setCellValue(e0122str);
						HSSFRichTextString  r3106str = new HSSFRichTextString("负责人");
						csCell =row.createCell((short)4);
						csCell.setCellStyle(cloumnstyle);
						csCell.setCellValue(r3106str);
						rows++;
					}
					flag = false;
					row=sheet.createRow(rows);
					HSSFRichTextString  on = new HSSFRichTextString(x+"");
					csCell =row.createCell((short)0);
					csCell.setCellStyle(centerstyle);
					csCell.setCellValue(on);
					HSSFRichTextString  fou = new HSSFRichTextString(rs.getString("r3130")==null?"":rs.getString("r3130"));
					csCell =row.createCell((short)1);
					csCell.setCellStyle(centerstyle);
					csCell.setCellValue(fou);
					HSSFRichTextString  b0110Value = new HSSFRichTextString(rs.getString("unitName")==null?"":rs.getString("unitName"));
					csCell =row.createCell((short)2);
					csCell.setCellStyle(centerstyle);
					csCell.setCellValue(b0110Value);
					HSSFRichTextString  e0122Value = new HSSFRichTextString(rs.getString("departName")==null?"":rs.getString("departName"));
					csCell =row.createCell((short)3);
					csCell.setCellStyle(centerstyle);
					csCell.setCellValue(e0122Value);
					HSSFRichTextString  r3106Value = new HSSFRichTextString(rs.getString("r3106")==null?"":rs.getString("r3106"));
					csCell =row.createCell((short)4);
					csCell.setCellStyle(centerstyle);
					csCell.setCellValue(r3106Value);
					rows++;
					x++;
					y++;
				}
				sql.setLength(0);
				
				if(sheet!=null)
				{
		    		for (int j = 0; j <=6; j++)
		    		{
		    			if(j == 0){
		    				sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)2000);
		    			}else{
		    				sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)6000);
		    			}
		    		}
		    		for (int j = 0; j <=rows; j++)
		    		{
		    			row=sheet.getRow(j);
		    			if(row==null)
		    	    	    row = sheet.createRow(j);
		    		    row.setHeight((short) 400);
		    		}
		     	}
			}
			
			outName += ResourceFactory.getProperty("train.info.class.nocheck") + "_" + this.userView.getUserName() + "_train.xls";
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+
					System.getProperty("file.separator")+outName);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			if (flag) {
				msg = "no";
			} else {
				msg = "yes";
			}
			if (rs != null)
				rs.close();
		} catch (GeneralException e) {
			throw GeneralExceptionHandler.Handle(e);
		} catch (SQLException e) {
			throw GeneralExceptionHandler.Handle(e);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(ids1!=null&&ids1.length()>0&&ids1.indexOf(":")!=-1&& "1".equalsIgnoreCase(vflag)){
			ids=ids1;
		}
		if(vflag!=null&&vflag.length()>0)
			this.getFormHM().put("vflag", vflag);
		this.getFormHM().put("fileName", PubFunc.encrypt(outName));
		this.getFormHM().put("msg", msg);
		this.getFormHM().put("ids", ids);
	}

}
