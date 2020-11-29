package com.hjsj.hrms.module.recruitment.resumecenter.businessobject;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.card.businessobject.YkcardOutWord;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterSetBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.apache.tools.zip.ZipOutputStream;

import javax.sql.RowSet;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

public class PrintResumeBo {
	Connection conn;
    UserView userview;
    HashMap cardMap;//获取简历模板
    ArrayList list = new ArrayList();
    private String message = "";
    private String filetype = "";//导出文件类型默认pdf

	private Category cat = Category.getInstance(this.getClass());
    
    private HashMap<String, String> fileNameMap = new HashMap<String, String>();
    
    public void setFiletype(String filetype) {
    	this.filetype = filetype;
    }
    
    public PrintResumeBo(Connection conn, UserView userview){
    	 this.conn = conn;
         this.userview = userview;
         try {
        	this.cardMap = this.getResumeCardId();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
    }
	
	/**
     * 得到准考证模板id
     * 01 社会招聘简历模板
     * 02 校园招聘简历模板
     * @return
     * @throws GeneralException
     */
    public HashMap<String, String> getResumeCardId() throws GeneralException{
    	//获取准考证模板
    	ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn,"1");
    	ParameterSetBo parameterSetBo = new ParameterSetBo(this.conn);
    	HashMap map = xmlBo.getAttributeValues();
    	HashMap<String, String> cardMap = new HashMap<String, String>();
    	ArrayList hireObjList = parameterSetBo.getCodeValueList();//取得招聘对象集合
    	for(int i=0;i<hireObjList.size();i++)//将获取招聘对象改成自动获取，不是指定的社会招聘和校园招聘
		{
			LazyDynaBean abean = (LazyDynaBean)hireObjList.get(i);
			String key = "CARDTABLE_"+(String)abean.get("codeitemid");
			String cardCodeId = "#";
			if(map.get(key) != null && ((String)map.get(key)).trim().length() > 0 && !"03".equals((String)abean.get("codeitemid")) && !"03".equals((String)abean.get("codeitemid"))){
				cardCodeId = (String)map.get(key);
				list.add((String)abean.get("codeitemid"));
			}
			cardMap.put((String)abean.get("codeitemid"),cardCodeId);
		}
        return cardMap;
    }
    
    /**
     * 将多个文件压缩到压缩包中
     * @param inFileName  需要压缩文件的路径
     * @param outFileName  压缩包名称，需带后缀名
     * @throws Exception
     */
    public  void  inputFilesToZip(String[] inFileName,String outFileName)throws   Exception{
    	FileOutputStream  fos  =  null;
		BufferedOutputStream  bos   =  null;
		ZipOutputStream   zos = null;
		ParameterSetBo paramBo = new ParameterSetBo(this.conn);
		try{
			//压缩包文件流必须放在循环外部，这样压缩的文件才会在同一个压缩包里
			fos  =  new   FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outFileName);
			bos   =  new   BufferedOutputStream(fos,1024);
			zos = new   ZipOutputStream(bos);
			
			File file = null;
			for (int i = 0; i < inFileName.length; i++) {
				file = new File(inFileName[i]);
				
				//将文件压入压缩包
				if(file.isFile())
					paramBo.fileToZip(file,zos,true);  
				else if(file.isDirectory())
					paramBo.directoryToZip(file, zos);
				//将压入压缩包后的原文件（临时文件）删除
				file.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
    		PubFunc.closeResource(zos);
    		PubFunc.closeResource(bos);
    		PubFunc.closeResource(fos);
    	}
    }
    
    /**
     * 根据应聘人员申请岗位判断应聘渠道，从而取对应的预览简历登记表
     * 
     * @Title: getResumeTemplateId
     * @Description:
     * @param dao
     * @param hireParams
     * @param a0100
     * @return
     * @throws Exception 
     */
    public String getResumeTemplateId(String a0100) throws GeneralException {
    	ContentDAO dao = new ContentDAO(this.conn);
        String templateId = "#";
        String message = "";
        String z0336 = "02";// z0336应聘渠道
        RowSet rs = null;
        try {
        	ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn,"1");
        	HashMap map = xmlBo.getAttributeValues();
        	ParameterSetBo parameterSetBo = new ParameterSetBo(this.conn);
        	ArrayList<LazyDynaBean> hireObjList = parameterSetBo.getCodeValueList();//取得招聘对象集合
            StringBuffer sql = new StringBuffer();
            String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
				String a_dbName = "";
                if (vo == null || StringUtils.isEmpty(vo.getString("str_value")))
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));
                a_dbName = vo.getString("str_value");
                
				sql.append("select "+candidate_status_itemId+" as hireChannel from "+a_dbName+"A01 where A0100=?");
				sql.append(" and " + Sql_switcher.isnull(candidate_status_itemId, "'#'") +"<>'#'");
			}
			
			ArrayList sqlParam = new ArrayList();
			sqlParam.add(a0100);
			
			if(sql.length() > 0)
			    rs = dao.search(sql.toString(), sqlParam);
			
			if (sql.length() > 0 && rs.next()) {
			    z0336 = rs.getString("hireChannel");
			    templateId = getTemplateId(z0336);
			    if ("#".equals(templateId)){
			        for(int i = 0; i < hireObjList.size(); i++) {
			            if(z0336.equalsIgnoreCase((String) hireObjList.get(i).get("codeitemid"))) {
			                this.message = "未设置" + (String) hireObjList.get(i).get("codeitemdesc") + "模板！";
			                this.cat.error(this.message);
			                message  = this.message;
			                if (!"".equals(message)) 
			                    throw new Exception(message);
			            }
			        }
			    }
			    
			    return templateId;
			}
			
			sql.setLength(0);
			sql.append("select z0336 as hireChannel from Z03,zp_pos_tache");
            sql.append(" where Z03.Z0301=zp_pos_tache.ZP_POS_ID");
            sql.append(" and a0100=? order by Thenumber");
			rs = dao.search(sql.toString(), sqlParam);
			if (rs.next()) {
			    z0336 = rs.getString("hireChannel");
			    templateId = getTemplateId(z0336);
			    if ("#".equals(templateId)){
			        for(int i = 0; i < hireObjList.size(); i++) {
			            if(z0336.equalsIgnoreCase((String) hireObjList.get(i).get("codeitemid"))) {
			                this.message = "未设置" + (String) hireObjList.get(i).get("codeitemdesc") + "模板！";
                            this.cat.error(this.message);
                            message  = this.message;
			            }
			        }
			    }
			    
			} else{
			    //查看是否设置模板，没有给出提示
			    for(int i = 0; i < hireObjList.size(); i++) {
			        templateId = getTemplateId((String) hireObjList.get(i).get("codeitemid"));
			        if(!"#".equals(templateId)) 
			            break;
			        else if(i == hireObjList.size() - 1)
			            message = "请设置招聘模板！";
			    }
			}
            
            if (!"".equals(message)) 
                throw new Exception(message);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return templateId;
    }
    
    /**
     * 根据根据应聘职位，从而取对应的预览简历登记表
     * 
     * @Title: getResumeTemplateId
     * @Description:
     * @param dao
     * @param hireParams
     * @param a0100
     * @return
     * @throws Exception 
     */
    public String getResumeTemplateId(String a0100 ,String zp_pos_id) throws GeneralException {
    	ContentDAO dao = new ContentDAO(this.conn);
        String templateId = "#";
        String message = "";
        String z0336 = "02";// z0336应聘渠道
        RowSet rs = null;
        try {
        	ParameterSetBo parameterSetBo = new ParameterSetBo(this.conn);
        	ArrayList<LazyDynaBean> hireObjList = parameterSetBo.getCodeValueList();//取得招聘对象集合
            StringBuffer sql = new StringBuffer("select z0336 from Z03 where Z0301=?");

            ArrayList sqlParam = new ArrayList();
            sqlParam.add(zp_pos_id);

            rs = dao.search(sql.toString(), sqlParam);
            if (rs.next()) {
                z0336 = rs.getString("z0336");
                templateId = getTemplateId(z0336);
                if ("#".equals(templateId)) {
                	for(int i = 0; i < hireObjList.size(); i++) {
                		if(z0336.equalsIgnoreCase((String) hireObjList.get(i).get("codeitemid"))) {
                			message = "未设置" + (String) hireObjList.get(i).get("codeitemdesc") + "模板！";
                			break;
                		}
                	}
                }
            }else{
            	 //查看是否设置模板，没有给出提示
            	 for(int i = 0; i < hireObjList.size(); i++) {
            		 templateId = getTemplateId((String) hireObjList.get(i).get("codeitemid"));
            		 if(!"#".equals(templateId)) 
            			 break;
            		 else if(i == hireObjList.size() - 1)
            			 message = "请设置招聘模板！";
            	 }
            	 //未申请职位时，先调用社会模板 02，若未设置，则调用校园模板 01，若也未设置，则给出提示信息“请设置简历模板！”
            	 /*templateId = getTemplateId("02");
            	 if ("#".equals(templateId)){
            		 templateId = getTemplateId("01");
                	 if ("#".equals(templateId)){
                		 message = "请设置社会或校园招聘模板！";
                	 }
            	 }*/
            }
            
            if (!"".equals(message)) 
                throw new GeneralException("", message, "", "");
            
        } catch (GeneralException e) {
        	throw e;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return templateId;
    }
    /**
     * 根据应聘应聘渠道，取对应的预览简历登记表号
     * 
     * @Title: getTemplateId
     * @Description:
     * @param z0336
     * @return templateId
     */
    private String getTemplateId(String z0336){
    	String templateId = "#";
    	templateId = (String) this.cardMap.get(z0336);
   	 	templateId = PubFunc.hireKeyWord_filter_reback(templateId);
   	 	if (null == templateId || "".equals(templateId) || "#".equals(templateId)){
   	 		templateId = "#";
   	 	}
		return templateId;
    }
    /**
     * 导入简历登记表
     * @param a0100s 人员编号
     * @param z0301 批次编号
     * @param nbase 招聘人员库
     * @param nbase 是否生成压缩文件， =0：不生成；=1：生成
     * @return
     * @throws GeneralException
     */
	public String printResume(String a0100s, String z0301, String nbase, int flag) throws GeneralException {
		String fileName = "";
		try {
			String[] a0100 = a0100s.split(",");
			EmployNetPortalBo bo = new EmployNetPortalBo(conn);
			YkcardOutWord ykcardPdf=new YkcardOutWord(this.userview, conn);
			HashMap<String, ArrayList<String>> pdfNames = new HashMap<String, ArrayList<String>>();
			ArrayList<String> list = new ArrayList<String>();
			String cardid = "#";
			String candidateStatusItemId="#";//应聘身份指标
			ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
            HashMap map = parameterXMLBo.getAttributeValues();
            if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
                candidateStatusItemId = (String)map.get("candidate_status");
            
			//z0301有值时，为候选人导出登记表，没有值时，为简历中心导出登录表
			if ((StringUtils.isEmpty(candidateStatusItemId) || "#".equals(candidateStatusItemId)) 
			        && StringUtils.isNotEmpty(z0301))
				cardid = this.getResumeTemplateId("", z0301);
			
			for (int i = 0; i < a0100.length; i++) {
				if ((StringUtils.isNotEmpty(candidateStatusItemId) && !"#".equals(candidateStatusItemId))
				        || StringUtils.isEmpty(z0301)) {
					
					cardid = bo.getResumeTemplateId(PubFunc.decrypt(a0100[i]));
					if("-1".equals(cardid)) {
						this.message = "简历中心导出登记表：没有设置简历登记表!";
			            this.cat.error(this.message);
			            message  = this.message;
			            if (!"".equals(message)) 
			                throw new Exception(message);
					}
				}
				
				list = pdfNames.get(cardid);
				if (list == null)
					list = new ArrayList<String>();
				
				list.add(PubFunc.decrypt(a0100[i]));
				pdfNames.put(cardid, list);
			}
			
			if(0 == flag)
			    ykcardPdf.setIsZipFile(1);
			else
			    ykcardPdf.setIsZipFile(0);
			if("word".equals(filetype))
				fileName = ykcardPdf.outWordYkcard(pdfNames, "0", "1", PubFunc.decrypt(nbase), "zpselfinfo", "1", "1",0);
			else
				fileName = ykcardPdf.outWordYkcard(pdfNames, "0", "1", PubFunc.decrypt(nbase), "zpselfinfo", "1", "1",YkcardOutWord.FILETYPE_PDF);
			fileNameMap = ykcardPdf.getFileNameMap();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		return fileName;
	}
	
	public HashMap<String, String> getFileNameMap(){
	    return fileNameMap;
	}

    public String getMessage() {
        return message;
    }

}
