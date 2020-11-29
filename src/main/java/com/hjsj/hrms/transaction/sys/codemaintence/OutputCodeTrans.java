package com.hjsj.hrms.transaction.sys.codemaintence;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class OutputCodeTrans extends IBusiness {
	Random random = new Random();
	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String codesetid = ((String) this.getFormHM().get("uid"));
		codesetid=PubFunc.keyWord_reback(codesetid);
		codesetid=codesetid.replaceAll("&nbsp;", "").trim();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='"
				+ codesetid + "'";
		try {
			this.frowset = dao.search(sql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		try {
			if (!this.frowset.next()) {
				Exception ex = new Exception("很抱歉！此代码类下无代码项，不能导出！");
				throw GeneralExceptionHandler.Handle(ex);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		RecordVo codesetvo = this.getCodesetvo(codesetid);
		String path = System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator");
		try {
			String filename = this.getCodeitem(dao, codesetvo, path);
			//xus 20/4/18 vfs改造
			filename = PubFunc.encrypt(filename.replace("\\", "/"));
//			filename = SafeCode.encode(PubFunc.encrypt(filename.replace("\\", "/")));
			this.getFormHM().put("fileid", filename);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public String getCodeitem(ContentDAO dao, RecordVo codesetvo, String path)
			throws SQLException, IOException {
		ArrayList codlist = new ArrayList();
		int rint = random.nextInt(10000);
		Writer fw = new FileWriter(path + "/"
				+ codesetvo.getString("codesetid") + "_" + rint + ".cod");
		BufferedWriter bw = null;
		try{
			bw=new BufferedWriter(fw);
			bw.write("指标体系");
			bw.newLine();
			bw.newLine();
			bw.write("BEGIN");
			bw.newLine();
			bw.newLine();
			bw.write(codesetvo.getString("codesetid") + " "
					+ codesetvo.getString("maxlength") + " "
					+ codesetvo.getString("codesetdesc") + "`"
					+ codesetvo.getString("status") + "`"
					+ codesetvo.getString("validateflag"));
			bw.newLine();
			bw.newLine();
			//代码体系，导出代码类，   jingq upd 2014.12.11
			//String sql = "select codeitemid,codeitemdesc from codeitem where codesetid='"
			String sql = "select codeitemid,codeitemdesc,corcode,invalid,start_date,end_date from codeitem where codesetid='"
					+ codesetvo.getString("codesetid") + "' order by codeitemid,a0000";
			RowSet rs = dao.search(sql);
			int codeitemmaxlen = 0;
			while (rs.next()) {
				String[] coditemarr = new String[6];
				coditemarr[0] = rs.getString("codeitemid");
				coditemarr[1] = rs.getString("codeitemdesc");
				//bs导出与cs导出相同    jingq add 2014.12.12
				coditemarr[2] = PubFunc.nullToStr(rs.getString("corcode")).trim();
				coditemarr[3] = rs.getString("invalid")!=null?rs.getString("invalid"):"0";
				//【6080】Oracle库导出代码类，报错   jingq upd 2014.12.17
				Date start_date = rs.getDate("start_date");
				Date end_date = rs.getDate("end_date");
				coditemarr[4] = PubFunc.DoFormatSecDate(start_date==null?"":start_date.toString());
				coditemarr[5] = PubFunc.DoFormatSecDate(end_date==null?"":end_date.toString());
				if (codeitemmaxlen < coditemarr[0].length()) {
					codeitemmaxlen = coditemarr[0].length();
				}
				codlist.add(coditemarr);
			}
			for (int i = 0; i < codlist.size(); i++) {
				String[] arrcodeitem = (String[]) codlist.get(i);
				StringBuffer sb = new StringBuffer();
				sb.append(arrcodeitem[0]);
				for (int j = arrcodeitem[0].length(); j < codeitemmaxlen; j++) {
					sb.append(" ");
				}
				sb.append("        ");
				bw.write(sb.toString() + arrcodeitem[1]+"`"+arrcodeitem[2]+"`"+arrcodeitem[3]+"`"+arrcodeitem[4]+"`"+arrcodeitem[5]);
				bw.newLine();
			}
			bw.flush();
			bw.close();
			fw.close();
		 } catch (Exception e) {
			e.printStackTrace();
		 }
		 finally
         {
                 PubFunc.closeIoResource(bw);
         }
		//xus 20/4/18 vfs改造
//		return path + "\\" + codesetvo.getString("codesetid") + "_" + rint + ".cod";
		return codesetvo.getString("codesetid") + "_" + rint + ".cod";
	}

	public RecordVo getCodesetvo(String codesetid) throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RecordVo codesetvo = new RecordVo("codeset");
		codesetvo.setString("codesetid", codesetid);
		try {
			codesetvo = dao.findByPrimaryKey(codesetvo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return codesetvo;

	}
}
