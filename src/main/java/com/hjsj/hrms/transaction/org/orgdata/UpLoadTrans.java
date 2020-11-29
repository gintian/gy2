package com.hjsj.hrms.transaction.org.orgdata;

import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;

public class UpLoadTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		hm.remove("flag");
		if("load".equalsIgnoreCase(flag)){
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String b0110 = (String)hm.get("b0110");
			b0110=b0110!=null&&b0110.trim().length()>0?b0110:"";
			hm.remove("b0110");
			
			String i9999 = (String)hm.get("i9999");
			i9999=i9999!=null&&i9999.trim().length()>0?i9999:"";
			hm.remove("i9999");
			
			String dbname = (String)hm.get("dbname");
			dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
			hm.remove("dbname");
			
			String infor = (String)hm.get("infor");
			infor=infor!=null&&infor.trim().length()>0?infor:"";
			hm.remove("infor");
			
			String tablename = "B00";
			String itemid = "b0110";
			if("1".equals(infor)){
				tablename = dbname+"A00";
				itemid = "a0100";
			}else if("2".equals(infor)){
				tablename = "B00";
				itemid = "b0110";
			}else if("3".equals(infor)){
				tablename = "K00";
				itemid = "e01a1";
			}
			
			FormFile file=(FormFile)getFormHM().get("picturefile");
			String lfilename = file.getFileName();
			String filetxt = lfilename.substring(lfilename.lastIndexOf("."));//扩展名
			RecordVo vo = new RecordVo(tablename);
			vo.setString(itemid, b0110);
			vo.setString("i9999", i9999);
			try {
				vo = dao.findByPrimaryKey(vo);
				vo.setString("ext", filetxt);
				updateFile(file,vo,dao,tablename,itemid);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	 /** 文件存到数据库
	 * @param file
	 * @param attach_id
	 */
	private void updateFile(FormFile file,RecordVo a_vo,ContentDAO dao
			,String tablename,String itemid){
		try{
			switch(Sql_switcher.searchDbServer()){
				case Constant.ORACEL:
					break;
				default:
					byte[] data = file.getFileData();
					a_vo.setObject("ole",data);
				break;	
			}
			if(Sql_switcher.searchDbServer()==Constant.ORACEL){
				Blob blob = getOracleBlob(file,tablename,itemid,a_vo);
				a_vo.setObject("ole",blob);
				dao.updateValueObject(a_vo);
			}else{
				dao.updateValueObject(a_vo);
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * oracle得到blob字段
	 * @param file
	 * @param attach_id
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(FormFile file,String tablename,String itemid
			,RecordVo a_vo) throws FileNotFoundException, IOException {
		
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select ole from ");
		strSearch.append(tablename);
		strSearch.append(" where "+itemid+"='");
		strSearch.append(a_vo.getString(itemid));	
		strSearch.append("' and i9999='"+a_vo.getString("i9999")+"' FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update "+tablename);
		strInsert.append(" set content=EMPTY_BLOB()");
		strSearch.append(" where "+itemid+"='");
		strSearch.append(a_vo.getString(itemid));	
		strSearch.append("' and i9999='"+a_vo.getString("i9999")+"'");

	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    Blob blob = null;
	    InputStream is = null;
	    try{
	        is = file.getInputStream();
	        blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); 
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            PubFunc.closeIoResource(is);
        }
		return blob;
	}
}
