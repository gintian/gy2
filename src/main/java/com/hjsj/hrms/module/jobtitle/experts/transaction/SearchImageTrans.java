package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
* <p>Title:SearchImageTrans </p>
* <p>Description: 查找图片以及修改图片名称</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Nov 30, 2015 9:22:08 AM
 */
public class SearchImageTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		String w0101 = (String)this.getFormHM().get("w0101");//专家编号
		String a0100 = (String)this.getFormHM().get("a0100");//
		String nbase = (String)this.getFormHM().get("nbase");//
		String quality = (String)this.getFormHM().get("quality");//
		String flag = (String)this.getFormHM().get("flag");//1为通过路径查找图片，2通过人员库前缀和人员编号
		ConstantXml constantXml = new ConstantXml(this.getFrameconn(),"FILEPATH_PARAM");
        String fileRootPath = constantXml.getNodeAttributeValue("/filepath", "rootpath");
        String truthpath = fileRootPath+File.separator+"multimedia"+File.separator+"jobtitle"+File.separator+"qualifications"+File.separator+"expert_photo"+File.separator;
		if("1".equals(flag)){
			//20/3/11 xus vfs改造
			//1、通过w0101获取fileid
			String sql = " select fileid from w01 where w0101 = ? ";
			ArrayList values = new ArrayList();
			values.add(w0101);
			ContentDAO dao = new ContentDAO(this.frameconn);
			String fileid = "";
			try {
				//外部专家
				if(StringUtils.isNotBlank(w0101)){
					this.frowset = dao.search(sql,values);
					if(this.frowset.next()) {
						if(this.frowset.getObject("fileid") != null) {
							fileid = this.frowset.getString("fileid");
							flag = "1";
						}
					}
				}
				
				//内部专家
				if(StringUtils.isNotBlank(a0100) && StringUtils.isNotBlank(nbase)) {
					sql = " select fileid from "+nbase+"a00 where a0100 = ? and flag = 'P' ";
					values = new ArrayList();
					values.add(a0100);
					this.frowset = dao.search(sql,values);
					if(this.frowset.next()) {
						if(StringUtils.isNotEmpty(this.frowset.getString("fileid"))) {
							fileid = this.frowset.getString("fileid");
							flag = "2";
						}
					}
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			
//			
//			String imagename = "";
//			//通过专家编号查找专家对应的图片
//	        File f = new File(truthpath);
//	        if(f.exists()){
//	        	 File s[] = f.listFiles();
////	 	        if(s==null){
////	 	        	this.getFormHM().put("checkflag", false);
////	 	        }else{
//	 	        	for(int i=0;i<s.length;i++) {
//	 		        	String name = s[i].getName();
//	 			        int index = name.indexOf(".");
//	 			        String fnameString = name.substring(0,index);
//	 			        if(fnameString.equals(w0101)){
//	 			        	imagename = name;
//	 			        }
//	 		        }
////	 	        }
//	        }
//	        String pubRealPath = "";
//	        String filePath = "";
//	        String filename = w0101+".jpg";
//	        PhotoImgBo photoImgBo = new PhotoImgBo(this.frameconn);// 图片工具类
//	        if(!photoImgBo.isExistdir(truthpath)&&StringUtils.isNotEmpty(a0100)){//判断文件路径是否存在
//	        	try {
//	        		String tempdir = System.getProperty("java.io.tmpdir");//取临时目录
//					filename = ServletUtilities.createPhotoFile(nbase + "A00", a0100, "P", null);//生成的文件名
//					//当人员没有照片时，回传空的文件路径，防止照片显示出有X号  20170620
//					if(StringUtils.isNotBlank(filename)){
//						filePath = PubFunc.encryption(tempdir);
//						pubRealPath = PubFunc.encryption(filePath);
//						flag = "1";//1为通过路径查找图片
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//	        }else {				
//	        	if(!imagename.equals("")){
//	        		filePath = PubFunc.encryption(truthpath);
//	        		pubRealPath = PubFunc.encryption(truthpath+imagename);
//	        	}else{
//	        		filePath = PubFunc.encryption(truthpath);
//	        		pubRealPath = PubFunc.encryption(truthpath+filename);
//	        	}
//	        	if(a0100!=null)
//	        		flag = "2";//2通过人员库前缀和人员编号
//			}
//	        if(a0100!=null){
//	        	a0100 = PubFunc.encrypt(a0100);
//	        }
//	        if(nbase!=null){
//	        	nbase = PubFunc.encrypt(nbase);
//	        }
//	        if(quality!=null){
//	        	quality = PubFunc.encrypt(quality);
//	        }
//	        Sys_Infom_Parameter sys_Infom_Parameter=new Sys_Infom_Parameter(this.getFrameconn(),"INFOM");
//	        String fileSizeLimit = sys_Infom_Parameter.getValue(Sys_Infom_Parameter.PHOTO,"MaxSize")+"KB";
//	        if("-1KB".equals(fileSizeLimit))
//	        	fileSizeLimit="";
//	        this.getFormHM().put("fileSizeLimit", fileSizeLimit);
//	        this.getFormHM().put("pubRealPath", pubRealPath);
//	        this.getFormHM().put("filePath", filePath);
			this.getFormHM().put("fileid", fileid);
	        this.getFormHM().put("a0100", a0100);
	        this.getFormHM().put("nbase", nbase);
	        this.getFormHM().put("quality", quality);
	        this.getFormHM().put("flag", flag);
		}
	}
}
