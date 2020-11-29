package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * 北京高法专版需求，总裁桌面挂查看文档
 * @author Administrator
 *
 */
public class QueryBiDocumentDisplayTrans extends IBusiness {

	public void execute() throws GeneralException {
		String url="";
		ArrayList yearlist = new ArrayList();
		String year="";
		String orgname="";
		String msg="ok";
		try{
			String tmppath=SafeCode.decode((String)this.getFormHM().get("tmppath"));
			String dirname=(String)this.getFormHM().get("dirname");
			String orgid=(String)this.getFormHM().get("orgid");
			orgid=orgid==null?"":orgid;
			/*String year=(String)this.getFormHM().get("year");
			year=year==null?"":year;*/
			CommonData cd = null;//new CommonData("","");
			//yearlist.add(cd);
			/*cd = new CommonData("2012","2012");
			yearlist.add(cd);
			cd = new CommonData("2011","2011");
			yearlist.add(cd);
			cd = new CommonData("2010","2010");
			yearlist.add(cd);*/
			StringBuffer path= new StringBuffer();
			path.append(tmppath+File.separator+dirname);
			TreeSet set = new TreeSet();
			if(orgid.length()>0){//选中机构进来
				path.append(File.separator+orgid);
				File f = new File(path.toString());
				if(f.exists()){
					File[] containfiles=f.listFiles();
					for(int i=0;i<containfiles.length;i++){
						File file=containfiles[i];
						if(file.isFile()){
							String filename=file.getName();
							set.add(filename);
						}
					}
				}else{
					msg=ResourceFactory.getProperty("bi.document.display.notexists");
					//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("bi.document.display.notexists"),"",""));
				}
			}else{//导航菜单点击进来
				File f = new File(path.toString());
				TreeSet orgset = new TreeSet();
				if(f.exists()){//包含的机构目录
					File[] containfiles=f.listFiles();
					for(int i=0;i<containfiles.length;i++){
						File file=containfiles[i];
						if(file.isDirectory()){
							//String filename=file.getName();
							orgset.add(file);
						}
					}
				}else{
					//msg=ResourceFactory.getProperty("bi.document.display.notexists");
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("bi.document.display.notexists"),"",""));
				}
				if(orgset.size()==0){
					//msg=ResourceFactory.getProperty("bi.document.display.notexists");
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("bi.document.display.notexists"),"",""));
				}
				if(!this.userView.isSuper_admin()){
					if(this.userView.getManagePrivCode().startsWith("UN")){
						String priv=this.userView.getManagePrivCodeValue();
						for(Iterator i=orgset.iterator();i.hasNext();){
								File file=(File)i.next();
								String filename = file.getName();
								File[] yearfiles=file.listFiles();
								if(filename.indexOf(priv)!=-1&&yearfiles.length>0){
									String tmporgid=file.getName();
									orgname=AdminCode.getCodeName("UN", tmporgid);
									if(orgname.length()>0){
										orgid=tmporgid;
										for(int n=0;n<yearfiles.length;n++){
											File yearfile=yearfiles[n];
											if(yearfile.isFile()){
												String yearname=yearfile.getName();
												set.add(yearname);
											}
										}
										break;
									}
								}
						}
						if(set.size()==0){
								//msg=ResourceFactory.getProperty("bi.document.display.notprivexists");
								throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("bi.document.display.notprivexists"),"",""));
						}
					}else{
						//msg=ResourceFactory.getProperty("bi.document.display.notprivexists");
						throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("bi.document.display.notprivexists"),"",""));
					}
				}else{
					for(Iterator i=orgset.iterator();i.hasNext();){
						File file=(File)i.next();
						File[] yearfiles=file.listFiles();
						if(yearfiles.length>0){
							String tmporgid=file.getName();
							orgname=AdminCode.getCodeName("UN", tmporgid);
							if(orgname.length()>0){
								orgid=tmporgid;
								for(int n=0;n<yearfiles.length;n++){
									File yearfile=yearfiles[n];
									if(yearfile.isFile()){
										String filename=yearfile.getName();
										set.add(filename);
									}
								}
								break;
							}
						}
					}
				}
			}
			if(set.size()==0)
				if(orgid.length()>0)
					msg=ResourceFactory.getProperty("bi.document.display.notexists");
				else
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("bi.document.display.notexists"),"",""));
			ArrayList filenames=new ArrayList();
			for(Iterator i=set.iterator();i.hasNext();){
				String filename=(String)i.next();
				filenames.add(filename);
			}
			for(int i=filenames.size()-1;i>=0;i--){
				String filename=(String)filenames.get(i);
				if(i==filenames.size()-1){
					url=File.separator+"bjgaofa"+File.separator+dirname+File.separator+orgid+File.separator+filename;
					year=filename;
				}
				cd=new CommonData(filename,filename.substring(0,filename.indexOf(".")));
				yearlist.add(cd);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("url", SafeCode.encode(url));
			if(yearlist.size()==0)
				yearlist.add(new CommonData("",""));
			this.getFormHM().put("yearlist", yearlist);
			this.getFormHM().put("year", year);
			this.getFormHM().put("orgname", orgname);
		}
		
	}

}
