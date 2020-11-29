package com.hjsj.hrms.utils.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.utility.AdminDb;
import com.hrms.virtualfilesystem.service.VfsService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2005-5-11:17:10:02
 * </p>
 * 
 * @author Administrator
 * @version 1.0
 * 
 */
public class CreateTempFile {
	private String getTempFilePath() throws Exception {
		String tempDirName = System.getProperty("java.io.tmpdir");
		if (tempDirName == null) {
			throw new RuntimeException("Temporary directory system property (tempfile.path) is null.");
		}

		File tempDir = new File(tempDirName);
		if (!tempDir.exists()) {
			tempDir.mkdirs();
		}
		return tempDir.getPath();
	}

	protected void registerTempFileForDeletion(File tempFile, HttpSession session) {
		if (session != null) {
			TempfileDeleter tempDeleter = (TempfileDeleter) session.getAttribute("TempfileDeleter");
			if (tempDeleter == null) {
				tempDeleter = new TempfileDeleter();
				session.setAttribute("TempfileDeleter", tempDeleter);
			}
			tempDeleter.addTempFile(tempFile.getName());
		} else {
			System.out.println("Session is null - temp file will not be deleted");
		}
	}

	public String createPicture(String userTable, String userNumber, String flag, HttpSession session, Connection conn)
	        throws Exception {
		String error;
		File tempFile = null;
		String temppath = getTempFilePath();
		ResultSet rs = null;
		InputStream in = null;
		try {
			StringBuffer strsql = new StringBuffer();
			strsql.append("select fileid from ");
			strsql.append(userTable);
			strsql.append(" where A0100='");
			strsql.append(userNumber);
			strsql.append("' and Flag='");
			strsql.append(flag);
			strsql.append("'");
			rs = new ExecuteSQL().execQuery(strsql.toString(), conn);
			strsql.delete(0, strsql.length());
			if (rs != null && rs.next()) {
				String fileid = rs.getString("fileid");
				in = VfsService.getFile(fileid);
				String prefix = "ID" + userNumber;
				java.io.FileOutputStream fout = null;
				try {
					tempFile = File.createTempFile(prefix, ".jpeg", new File(temppath));
					if (tempFile.exists()) {
						tempFile.delete();
					}
					fout = new java.io.FileOutputStream(tempFile);
					int len;
					byte buf[] = new byte[1024];
					while ((len = in.read(buf, 0, 1024)) != -1) {
						fout.write(buf, 0, len);
					}
				} finally {
					PubFunc.closeIoResource(fout);
				}
				if (session != null) {
					registerTempFileForDeletion(tempFile, session);
				}
			}
		} catch (SQLException cnfe) {
			error = "SQLException:Exception in freeConn() ";
			throw new SQLException(error);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(in);
		}
		String name = null;
		if (tempFile != null)
			name = "/temp/" + tempFile.getName();
		
		tempFile = null;
		return name;
	}

	public String createPicture(String userTable, String userNumber, String flag, HttpSession session)
	        throws Exception {
		Connection conn = null;
		String name = null;
		try {
			conn = AdminDb.getConnection();
			name = createPicture(userTable, userNumber, flag, session, conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(conn);
		}

		return name;
	}
}
