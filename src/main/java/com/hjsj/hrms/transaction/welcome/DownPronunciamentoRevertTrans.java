package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Statement;
public class DownPronunciamentoRevertTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
		String s=(String)this.getFormHM().get("id");
		String ext = (String)this.getFormHM().get("ext");
		InputStream inputstream = null;
		FileOutputStream fileOut =null;
		File file  = null;
		String name="downboard";
		DbSecurityImpl dbS = new DbSecurityImpl();
		Statement statement = null;
		try {
			System.gc();
			name="announce_"+s+"."+ext;
			file = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+name);
			if(!file.exists()){
				String sql = "select topic,ext,thefile from announce where id=" + s;			
				statement = this.frameconn.createStatement();
				dbS.open(this.frameconn, sql);
				this.frecset = statement.executeQuery(sql);
				inputstream = null;
				/**从数据库中取得数据*/
				if (frecset.next()) {
					//ext = frecset.getString("ext");
					//name=frecset.getString("topic");				
					inputstream = frecset.getBinaryStream("thefile");
				}
				else {
					return;
				}
				 fileOut = new FileOutputStream(file);
				 int len;
		            byte buf[] = new byte[1024];
					while ((len = inputstream.read(buf)) != -1) {
						fileOut.write(buf,0,len);
					}
					file = null;
                    fileOut.flush();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			try {
				// 关闭Wallet
				dbS.close(this.frameconn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PubFunc.closeResource(statement);
		    PubFunc.closeResource(inputstream);
		    PubFunc.closeResource(fileOut);
			this.getFormHM().put("outName", SafeCode.encode(name));
		}
	}

}
