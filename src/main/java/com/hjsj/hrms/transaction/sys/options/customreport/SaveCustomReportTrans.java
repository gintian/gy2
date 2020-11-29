package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;

/**
 * <p>
 * Title:SaveCustomReportTrans
 * </p>
 * <p>
 * Description:保存自定制报表
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-3-8
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class SaveCustomReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		RecordVo vo = new RecordVo("t_custom_report");
		ContentDAO dao = new ContentDAO(this.frameconn);
		
		// 是否是更新操作，0为添加，1为更新
		String isEdit = (String) this.getFormHM().get("isEdit");
		try {
			// 获得保存的id
			String id = "";
			if (isEdit != null && "1".equals(isEdit)) {
				id = (String) this.getFormHM().get("id");
				vo.setInt("id", Integer.parseInt(id));
			} else {
				String sql = "select max(id) from t_custom_report";
				this.frowset = dao.search(sql);
				if (frowset.next()) {
					id = String.valueOf((frowset.getInt(1)) + 1);
				} else {
					id = "1";
				}
				vo.setInt("id", Integer.parseInt(id));
			}
			Map map = (Map) this.getFormHM().get("requestPamaHM");
			
			// 修改前，先查出该条记录
			// 获得模块id
			String businessModuleValue = (String) this.getFormHM().get(
					"businessModuleValue");
			vo.setString("moduleid", businessModuleValue);
			
			// 获得报表类型
			Integer reportType = (Integer) this.getFormHM().get("reportType");
			vo.setInt("report_type", reportType.intValue());
			this.getFormHM().remove("reportType");
			
			// 获得关联的表id
			if (reportType.intValue() != 0 && reportType.intValue()!= 4) {
				Integer link_tabid = (Integer) this.getFormHM().get(
						"link_tabid");
				vo.setInt("link_tabid", link_tabid.intValue());
				this.getFormHM().remove("link_tabid");
			} else {
				// 自定制表关联tableid为0
				vo.setInt("link_tabid", 0);
			}
			
			// 获得报表名称
			String name = (String) this.getFormHM().get("name");
			String regx = "[`~!#$%^&*()+{}|\\:\"<>\\-=,\\?；，？！@%']+";
			name = name.replaceAll(regx, "");
			vo.setString("name", name);
			this.getFormHM().remove("name");
			
			// 获得报表描述
			String description = (String) this.getFormHM().get("description");
			vo.setString("description", description);
			this.getFormHM().remove("description");
			
			// 获得发布状态
			String flag = (String) map.get("flag");
			if (flag == null || flag.length() <= 0) {
				flag = "0";
			}
			vo.setInt("flag", Integer.parseInt(flag));
			this.getFormHM().remove("flag");

			// vo.setInt("id", id);
			// 自定义报表获得模板和sql文件
			if (reportType.intValue() == 0 || reportType.intValue() == 4) {
				// 获得报表模板
				FormFile templateFile = (FormFile) this.getFormHM().get(
						"templateFile");
				boolean templateAccept = FileTypeUtil.isFileTypeEqual(templateFile);
				// 获得sql条件文件
				FormFile sqlFile = (FormFile) this.getFormHM().get("sqlFile");
				String sqlString = "";
				boolean sqlAccept = true;
				if (sqlFile != null && sqlFile.getFileSize() != 0) {
//					vo.setString("sqlfile", this.getStringFromFile(sqlFile));
					sqlString = this.getStringFromFile(sqlFile);
					sqlAccept = FileTypeUtil.isFileTypeEqual(sqlFile);
				}
				if(templateAccept&&sqlAccept){
					insertDAO(vo, templateFile,sqlString, dao, isEdit, id);
				} else {
					throw GeneralExceptionHandler.Handle(new Throwable(ResourceFactory.getProperty("error.fileuploaderror")));
				}
				
			} else {
				String updatesql = "";
				if (isEdit != null && "1".equals(isEdit)) {
					switch (Sql_switcher.searchDbServer()) {
					case Constant.ORACEL:
						updatesql = "update t_custom_report set templatefile=EMPTY_BLOB(),sqlfile=EMPTY_CLOB() where id='"
								+ vo.getInt("id") + "'";

						break;
					default:
						byte[] tdata = new byte[1];
						vo.setObject("templatefile", tdata);
						break;
					}
//					vo.setString("sqlfile", "");
					
					if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
						vo.setString("ext", "");
						dao.updateValueObject(vo);
						dao.update(updatesql);
					} else {
						vo.setString("ext", "");
						dao.updateValueObject(vo);
					}
				} else {
					dao.addValueObject(vo);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	/**
	 * 通过底层函数进行文件保存
	 * 
	 * @param vo
	 * @param templateFile
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile templateFile, String sqlStr, ContentDAO dao,
			String isEdit, String id) throws GeneralException {
		boolean bflag = true;

		PreparedStatement pstmt = null;
		try {
			int reportType = vo.getInt("report_type");
			if (reportType == 0 || reportType == 4) {
				if ((templateFile != null && templateFile.getFileSize() != 0)) {
					// 报表模板的扩展名
					String templateFileName = templateFile.getFileName();
					int indexInt = templateFileName.lastIndexOf(".");
					String ext = templateFileName.substring(indexInt,
							templateFileName.length());
					vo.setString("ext", ext);
					/** blob字段保存,数据库中差异 */
					switch (Sql_switcher.searchDbServer()) {
					case Constant.ORACEL:
						// Blob blob = getOracleBlob(
						// templateFile,codesetid,recid);
//						 vo.setObject("templatefile",blob);
						break;
					default:
						byte[] tdata = templateFile.getFileData();
						vo.setObject("templatefile", tdata);
						if (sqlStr.length() > 0) {
							vo.setString("sqlfile", sqlStr);
						}
						break;
					}
				} else {
					//vo.setString("ext", "");
					if ((sqlStr.length() > 0 && Sql_switcher.searchDbServer() == Constant.MSSQL)|| (reportType != 0 && reportType!=4)) {
						vo.setString("sqlfile", sqlStr);
					}
				}
			}
			if (isEdit != null && "1".equals(isEdit)) {
				dao.updateValueObject(vo);
			} else {
				dao.addValueObject(vo);
			}

			// 查询
			if (bflag && Sql_switcher.searchDbServer() == Constant.ORACEL) {
				RecordVo updatevo = new RecordVo("t_custom_report");
				// updatevo.setInt("id", vo.getInt("id"));
				if (templateFile != null && templateFile.getFileSize() != 0 || reportType != 0) {
					Blob blob = getOracleBlob(templateFile, Integer.parseInt(id));
					vo.setObject("templatefile", blob);
				}
				
				if (sqlStr.length() > 0 || (reportType != 0 && reportType != 4)) {
					Clob clob = this.readClob(updatevo, "sqlfile", sqlStr, "id='"+id+"'");
					clob=OracleBlobUtils.convertDruidToOracle(clob);
					vo.setObject("sqlfile", clob);
				}
				
				dao.updateValueObject(vo);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException ee) {
				ee.printStackTrace();
			}
		}
	}

	/**
	 * @param file
	 * @param id
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file, int id)
			throws FileNotFoundException, IOException {
		StringBuffer strSearch = new StringBuffer();
		strSearch.append("select templatefile from ");
		strSearch.append("t_custom_report where id='");
		strSearch.append(id);
		strSearch.append("'");
		strSearch.append(" FOR UPDATE");

		StringBuffer strInsert = new StringBuffer();
		strInsert.append("update  ");
		strInsert
				.append("t_custom_report set templatefile=EMPTY_BLOB() where id='");
		strInsert.append(id);
		strInsert.append("'");
		Blob blob = null;
		InputStream in = null;
		try {
			OracleBlobUtils blobutils = new OracleBlobUtils(this.frameconn);
			in = file.getInputStream();
			blob = blobutils.readBlob(strSearch.toString(), strInsert
				.toString(), in); // readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            PubFunc.closeIoResource(in);
        }
		return blob;
	}

	private String getStringFromFile(FormFile file) {
		String xmlStr = "";
		InputStream input = null;
		try {
			/*不限制上传格式，上传后强制转为UTF-8*/
			input = file.getInputStream();
			SAXBuilder builder  = new SAXBuilder();
			Document doc = builder.build(input);
			XMLOutputter outPutter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outPutter.setFormat(format);
			xmlStr = outPutter.outputString(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
		    PubFunc.closeResource(input);
		}

		return xmlStr;
	}
	
	public Clob readClob(RecordVo vo,String fieldname,String content,String strWhere)
	{
		PreparedStatement stmt2=null;
		ResultSet rs=null;
		Clob clob=null;

		String stab=vo.getModelName();
		StringBuffer buf=new StringBuffer();
		buf.append("select ");
		buf.append(fieldname);
		buf.append(" from ");
		buf.append(stab);
		buf.append(" where ");
		buf.append(strWhere);
		buf.append(" for update");		
		
		StringBuffer insbuf=new StringBuffer();
		insbuf.append("update ");
		insbuf.append(stab);
		insbuf.append(" set ");
		insbuf.append(fieldname);
		insbuf.append("=? where ");
		insbuf.append(strWhere);
		
		try 
		{	 ContentDAO dao  = new ContentDAO(this.frameconn);

			dao.update("update " +stab+ " set " + fieldname+"=empty_clob() where " + strWhere);
			
			OracleBlobUtils blobutils = new OracleBlobUtils(this.frameconn);
		clob = blobutils.readClob(buf.toString(), insbuf.toString(), content);
//			conn = AdminDb.getConnection();
//			stmt=
////			for(int i=0;i<list.size();i++)
////			{
////				stmt.setObject(i+1,vo.getObject(list.get(i).toString()));
////			}
//			stmt.executeUpdate();
//			
//			stmt2=
////			for(int i=0;i<list.size();i++)
////			{
////				stmt.setObject(i+1,vo.getObject(list.get(i).toString()));
////			}
//			rs = stmt2.executeQuery();
//			if(rs.next())
//			{
//				clob = (java.sql.Clob) rs .getClob(1);
//				clob.setString(1, content);
//			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
				if(stmt2!=null)
					stmt2.close();
			}
			catch(Exception ggg)
			{
				ggg.printStackTrace();
			}
		}
		return clob;		
	}

}
