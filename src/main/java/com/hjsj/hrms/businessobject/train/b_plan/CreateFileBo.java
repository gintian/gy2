package com.hjsj.hrms.businessobject.train.b_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class CreateFileBo {
	private Connection conn= null;
	public CreateFileBo(Connection conn){
		this.conn = conn;
	}
	public void createDir(String dirpath){
		File f= new File(dirpath);
		if(!f.exists()){
			f.mkdir();
		}
	}
	public void createFile(String path,String codesetid){
		ArrayList list = codeItemList(codesetid);
		for(int j=0;j<list.size();j++){
			String pathstr = (String)list.get(j);
			createDir(path+pathstr);
		}
		if("54".equals(codesetid)) {
            createFile(list,codesetid,path);
        }
	}
	public ArrayList codeItemList(String codesetid){
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		int layer = layer(dao,codesetid);
		
		for(int i=1;i<=layer;i++){
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select codeitemid,parentid from codeitem where ");
			sqlstr.append("codesetid='");
			sqlstr.append(codesetid);
			sqlstr.append("' and layer=");
			sqlstr.append(i);
			try {
				String codeitemid = "";
				String parentid = "";
				String pathstr  ="";
				String separator = System.getProperty("file.separator");
				RowSet rs = dao.search(sqlstr.toString());
				while(rs.next()){
					codeitemid = rs.getString(1);
					codeitemid=codeitemid!=null?codeitemid:"";
					if(codeitemid.trim().length()<1) {
                        continue;
                    }
					parentid = rs.getString(2);
					if(codeitemid.equalsIgnoreCase(parentid)){
						pathstr = separator+codeitemid+separator;
					}else{
						for(int j=0;j<list.size();j++){
							String path = (String)list.get(j);
							if(path.endsWith(separator+parentid+separator)){
								pathstr = path+codeitemid+separator;
								break;
							}
						}
					}
					list.add(pathstr);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	private int layer(ContentDAO dao,String codesetid){
		int layer=1;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select max(layer) as layer from ");
		sqlstr.append(" codeitem where codesetid='");
		sqlstr.append(codesetid);
		sqlstr.append("'");
		try {
			RowSet rs = dao.search(sqlstr.toString());
			if(rs.next()) {
                layer = rs.getInt(1);
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return layer;
	}
	private void createFile(ArrayList fieldlist,String codesetid,String path){
		//ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList valuelist = new ArrayList();

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select fileid,content,ext,");
		sqlstr.append("(select R0700 from R07 where R07.R0701=tr_res_file.R0701) as R0700");
		sqlstr.append(" from tr_res_file");

		InputStream in = null;
		FileOutputStream fout = null;
		File file = null;
		try {
			RowSet rs = dao.search(sqlstr.toString());
			String filename = "";
			String R0700="";
			String ext="";
			int fileid=0;
			String filepath = "";
			String separator = System.getProperty("file.separator");
			while(rs.next()){
				ArrayList pathlist = new ArrayList();
				try {
					fileid = rs.getInt(1);
					in = rs.getBinaryStream(2);
					ext=rs.getString(3);
					R0700=rs.getString(4);
					R0700=R0700!=null?R0700:"";
					filename = fileid+ext;
					if(R0700.trim().length()<1){
						filepath = path+separator+filename;
						pathlist.add("");
					}else{
						R0700 =separator+R0700+separator;
						for(int j=0;j<fieldlist.size();j++){
							String pathstr = (String)fieldlist.get(j);
							if(pathstr.endsWith(R0700)){
								filepath = path+separator+pathstr+filename;
								pathlist.add(separator+pathstr);
								break;
							}
						}
					}
					pathlist.add(fileid+"");
					valuelist.add(pathlist);
					file = new File(filepath);
					fout = new java.io.FileOutputStream(file);
					int len;
					if(in!=null){
						byte buf[] = new byte[1024];	
						while ((len = in.read(buf, 0, 1024)) != -1) {
							fout.write(buf, 0, len);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally {
					PubFunc.closeIoResource(fout);
					PubFunc.closeIoResource(in);
					PubFunc.closeIoResource(file);
				}
			}
			StringBuffer updatesql = new StringBuffer();
			updatesql.append("update tr_res_file set url=? where fileid=?");
			dao.batchUpdate(updatesql.toString(), valuelist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(fout);
			PubFunc.closeIoResource(in);
			PubFunc.closeIoResource(file);
		}
	}
	public boolean deletefile(String delpath){   
		try{   
			File file = new File(delpath); 
			String separator = System.getProperty("file.separator");
			if(!file.isDirectory()){   
				file.delete();   
			}else if(file.isDirectory()){   
				String[]   filelist   =   file.list();   
				for(int i = 0;i<filelist.length;i++){   
					File delfile = new File(delpath + separator + filelist[i]);   
					if(!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        deletefile(delpath + separator + filelist[i]);
                    }
				}   
				file.delete();   
			}   
		}  catch(Exception e){   
			
		}   
		return   true;   
	} 
	public String outFile(String codesetid,String fileid){
		String pathstr = new Date().getTime()+"";
		ContentDAO dao = new ContentDAO(this.conn);
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator");
		InputStream in = null;
		FileOutputStream fout = null;
		File file = null;
		StringBuffer sqlstr = new StringBuffer();
		if("54".equals(codesetid)){
			sqlstr.append("select content,ext");
			sqlstr.append(" from tr_res_file where fileid=");
			sqlstr.append(fileid);
		}else if("56".equals(codesetid)){
			sqlstr.append("select content,ext");
			sqlstr.append(" from per_diary_file where file_id=");
			sqlstr.append(fileid);
		}
		try {
			RowSet rs = dao.search(sqlstr.toString());
			if(rs.next()){
				in = rs.getBinaryStream(1);
				pathstr+=rs.getString(2);
				url +=pathstr;
				file = new File(url);
				fout = new java.io.FileOutputStream(file);
				int len;
				if(in!=null){
					byte buf[] = new byte[1024];	
					while ((len = in.read(buf, 0, 1024)) != -1) {
						fout.write(buf, 0, len);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(fout);
			PubFunc.closeIoResource(in);
			file = null;
		}
		return pathstr;
	}
	
	public String outFile(UserView userView,String codesetid,String fileid){
		String file_id = "";
		ContentDAO dao = new ContentDAO(this.conn);
		FileOutputStream fout = null;
		StringBuffer sqlstr = new StringBuffer();
		if("54".equals(codesetid)){
			sqlstr.append("select file_id");
			sqlstr.append(",name from tr_res_file where fileid=");
			sqlstr.append(fileid = PubFunc.decrypt(SafeCode.decode(fileid)));
		}else if("56".equals(codesetid)){
			sqlstr.append("select fileid");
			sqlstr.append(",name from per_diary_file where file_id=");
			sqlstr.append(fileid);
		}
		RowSet rs = null;
		try {
			rs = dao.search(sqlstr.toString());
			if(rs.next()){
				if("54".equals(codesetid)){
					file_id = rs.getString("file_id");
				}else if("56".equals(codesetid)){
					file_id = rs.getString("fileid");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		    PubFunc.closeIoResource(fout);
		    PubFunc.closeResource(rs);
		}
		
		return file_id;
	}
	public String downFile(UserView userView,String sqlstr,String titleitemid,String ext,String ole){
		String pathstr = "";
		ContentDAO dao = new ContentDAO(this.conn);
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator");
		InputStream in = null;
		FileOutputStream fout = null;
		File file = null;
		try {
			RowSet rs = dao.search(sqlstr);
			if(rs.next()){
				in = rs.getBinaryStream(ole);
				String tile = rs.getString(titleitemid.toUpperCase());
				tile = tile!=null&&tile.trim().length()>0?tile:PubFunc.getStrg();
				
				String extdoc = rs.getString(ext.toUpperCase());
				extdoc=extdoc!=null?extdoc:""; 
				pathstr+=tile+"_"+userView.getUserName()+extdoc;
				if(extdoc.trim().length()<1) {
                    pathstr = "no";
                }
				url +=pathstr;
				file = new File(url);
				fout = new java.io.FileOutputStream(file);
				int len;
				if(in!=null){
					byte buf[] = new byte[1024];	
					while ((len = in.read(buf, 0, 1024)) != -1) {
						fout.write(buf, 0, len);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			PubFunc.closeIoResource(fout);
			PubFunc.closeIoResource(in);
			
				file = null;
			
		}
		return pathstr;
	}
}
