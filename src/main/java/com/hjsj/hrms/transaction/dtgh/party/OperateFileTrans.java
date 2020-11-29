package com.hjsj.hrms.transaction.dtgh.party;

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
import java.util.Date;
import java.util.HashMap;

public class OperateFileTrans extends IBusiness {

        public void execute() throws GeneralException {
        	
        	
        		HashMap  reqParam = (HashMap)this.getFormHM().get("requestPamaHM"); 
        		String flag = reqParam.get("oper").toString();
        		String h0100 = this.getFormHM().get("codeitemid").toString();
        		String filename = this.getFormHM().get("filename").toString();
        		FormFile file = (FormFile)this.getFormHM().get("picturefile");
        		
        		try{
	        		if("del".equalsIgnoreCase(flag)){
	        			ContentDAO dao = new ContentDAO(this.frameconn);
	        			String sql = "delete h00 where h0100='"+h0100+"'";
	        			dao.update(sql);
	        		}else{
	        			
	        			savefile(file, h0100, flag, filename);
	        		}
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        			
            	
            	
        }
         
         
       public Blob getOracleBlob(String h0100,FormFile file,String flag) throws FileNotFoundException, IOException{
    	   
    	   String strInsert="";
    	   if("save".equalsIgnoreCase(flag))
    		   strInsert="insert into h00(h0100,i9999,ole) values('"+h0100+"','1',EMPTY_BLOB())";
    	   else
    	       strInsert = "update h00 set ole=EMPTY_BLOB() where h0100='"+h0100+"'";
    	   
    	   String strSearch = "select ole from h00 where h0100='"+h0100+"'";
    	   
    	   OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
    	   Blob blob = null;
    	   InputStream stream = null;
    	   try {
    		   stream = file.getInputStream();
    		   blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),stream); 
		   }finally{
				PubFunc.closeIoResource(stream);
		   }
   		   return blob;
    	   
       }
       
       public void savefile(FormFile file,String h0100,String flag,String filename) throws GeneralException{
    	   try {
           	String realname = file.getFileName();
           	String ext = realname.substring(realname.lastIndexOf("."));
				byte[] data=file.getFileData();
				ContentDAO dao = new ContentDAO(this.frameconn);
				Blob blob = null;
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			    {
					blob = getOracleBlob(h0100,file,flag);
//			    	dao.updateValueObject(a_vo);
			    }
				
				RecordVo vo = new RecordVo("h00");
				vo.setString("h0100", h0100);
				vo.setString("title", filename);
				vo.setString("i9999","1");
				vo.setString("flag", "K");
				vo.setString("ext",ext);
				vo.setString("createusername",userView.getUserName());
				vo.setDate("createtime",new Date());
				if(blob!=null)
					vo.setObject("ole", blob);
				else
					vo.setObject("ole", data);
				
				if(blob==null){
					if("save".equalsIgnoreCase(flag))
					 dao.addValueObject(vo);
					else
					 dao.updateValueObject(vo);
				}else{
					dao.updateValueObject(vo);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
       }
}
