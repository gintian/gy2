package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;


/**
 * <p>
 * Title:DownloadCustomReportTrans
 * </p>
 * <p>
 * Description:下载自定制报表信息
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-4-13
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class DownloadCustomReportTrans extends IBusiness {

	private String ext;
	private String xmlContent;
	private String filename;
	private InputStream in;
	public void execute() throws GeneralException {
		
		// 文件的扩展名
		String type=(String)this.getFormHM().get("type");
		// 自定制报表id
		String id=(String)this.getFormHM().get("id");
		this.selectFile(id, type);
		
		// 文件名称
		String excel_filename = "";
		// 文件扩展名
		if(this.ext.indexOf(".")==-1)
	    {
	    	excel_filename = filename+"_"+this.getUserView().getUserName() +"."+this.ext;

	    }else
	    	excel_filename = filename+"_"+this.getUserView().getUserName() +""+this.ext;
		
		// 文件输出
		FileOutputStream fileOut = null;
		OutputStreamWriter writer = null;
		BufferedWriter wr = null;
		try {
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			writer = new OutputStreamWriter(fileOut);
			wr = new BufferedWriter(writer);
			if ("xml".equalsIgnoreCase(type)) {
				wr.write(xmlContent);
			} else {
				byte []bt = new byte[1024];
				int read = 0;
				while ((read = in.read(bt)) != -1) {
					fileOut.write(bt, 0, read);
				}
			}
			excel_filename = PubFunc.encrypt(excel_filename);//先加密再转码
			this.getFormHM().put("filename", excel_filename);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			try {
				if (wr != null) {
					wr.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (fileOut != null) {
					fileOut.close();
				}
				if (in != null) {
					in.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	/**
	 * 查询文件
	 * @param tabid 表id
	 * @param type 文件类型，xml和非xml
	 */
	public void selectFile(String tabid, String type) {
		// 获得数据库需要的类型
		int id = Integer.parseInt(tabid);
		// sql 语句
		String sql = "select name,Sqlfile,Templatefile,ext " +
				"from t_custom_report  where id='" + id + "'";
		
		RowSet rs = null;    	
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.frameconn);
    		rs = dao.search(sql);
    		if(rs.next())
    		{
    			if ("xml".equalsIgnoreCase(type)) {
    				String xmlStr = Sql_switcher.readMemo(rs, "Sqlfile");
					xmlStr= xmlStr.replace("encoding=\"GB2312\"", "encoding=\"UTF-8\"").replace("encoding=\"gb2312\"", "encoding=\"UTF-8\"");
					xmlStr= xmlStr.replace("encoding=\"GBK\"", "encoding=\"UTF-8\"").replace("encoding=\"gbk\"", "encoding=\"UTF-8\"");
					this.xmlContent = xmlStr;
    				this.ext = ".xml";
    			} else {
    				this.in = rs.getBinaryStream("Templatefile");
    				this.ext=rs.getString("ext")!=null?rs.getString("ext"):"";
    			}
    			
    			this.filename=rs.getString("name");
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
    	}
	}
	

}
