package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetResumeTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try {
			String type = (String) this.getFormHM().get("type");
			String subset = (String) this.getFormHM().get("subset");
			String isSubsetEncrypt = (String)this.getFormHM().get("isSubsetEncrypt");
			String isGetCode = (String)this.getFormHM().get("isGetCode");
			String isWzp = (String)this.formHM.get("isWzp");
			ResumeBo resumeBo = new ResumeBo(this.frameconn,this.userView);
			Map<String,Map<String,List>> codeData = null;
			EmployNetPortalBo bo=new EmployNetPortalBo(this.frameconn);
			HashMap status = resumeBo.getCandidateStatus();
			//返回应聘身份指标
			//获取应聘身份指标的描述,方便前台展现
			String candidateStatusid = (String) this.userView.getHm().get("applyCode");
			String codeitemdesc = AdminCode.getCodeName("35", candidateStatusid);
			this.getFormHM().put("applyCode",candidateStatusid);
			this.getFormHM().put("applyCodeDesc",codeitemdesc);
			this.getFormHM().put("Candidate",status);
			HashMap<String,Object> return_data = new HashMap<String,Object>();
			if("search".equalsIgnoreCase(type)) {
				ArrayList field_set_list = resumeBo.getResumeFieldSetList(candidateStatusid);
				HashMap<String, Object> field_list = resumeBo.getResumeFieldList(candidateStatusid);
				ArrayList uploadFileList = resumeBo.getOthFiles();
				String writeable = resumeBo.getWriteable();
				//照片路径
				String photoPath = resumeBo.getPhotoPath();
				//最大上传照片大小 单位：k 
				String photo_size = "512";
				//最大上传附件大小 单位：M 
				String max_filesize = resumeBo.getMaxFileSize();
				//是否配置简历附件
				String attach = resumeBo.getResumeFileState();
				//最大子集附件大小 M
				String subFileMaxSize = "10";
				//照片是否必须上传
				String must_upload = resumeBo.getIfMustUpload();
				//系统设置的邮箱指标
				String email_item = ConstantParamter.getEmailField();
				//系统设置的电话指标
				String phone_item = ConstantParamter.getMobilePhoneField();
				//简历附件子集是否是必填
				boolean mustFlag = false;
				ParameterXMLBo xmlBo = new ParameterXMLBo(this.getFrameconn(),"1");
			    HashMap xmlMap = xmlBo.getAttributeValues();
			    ArrayList attach_codeList = bo.getAttachCodeset(xmlMap, candidateStatusid);
			    String attach_codeset = (String) xmlMap.get("attachCodeset");
			    boolean isAttachCodeset = false;//是否开启了简历附件分类功能
			    if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset) && !StringUtils.equals("true",isWzp)) {
				    //isWzp 增加判定，微招聘暂时不开启简历分类功能
			        isAttachCodeset = true;
			        uploadFileList = resumeBo.sortFileList(attach_codeList,uploadFileList);
			        resumeBo.getAttachCodeSetState(attach_codeList,uploadFileList);
			        mustFlag = bo.getMustCodeset(xmlMap, candidateStatusid);
			    }
			        
			    String answerSet="";//开放问答子集
			    if(xmlMap.get("answerSet")!=null&&((String)xmlMap.get("answerSet")).length()>0) {
			        answerSet=(String)xmlMap.get("answerSet");
			    }
			    if(StringUtils.equals(isGetCode, "1")) {//获取代码型指标所有代码项
			       codeData = resumeBo.getCodeData(candidateStatusid);
			    }
				return_data.put("writeable", writeable);
				return_data.put("photo", photoPath);
				return_data.put("photo_size", photo_size);
				return_data.put("max_filesize", max_filesize);
				return_data.put("subFileMaxSize", subFileMaxSize);
				return_data.put("must_upload", must_upload);
				return_data.put("email_item", email_item);
				return_data.put("phone_item", phone_item);
				return_data.put("field_set_list", field_set_list);
				return_data.put("field_list", field_list);
				return_data.put("uploadFileList", uploadFileList);
				return_data.put("answerSet", answerSet);
				return_data.put("codeData", codeData);
				return_data.put("attach", attach);
				return_data.put("attachCodeSet", attach_codeList);
				return_data.put("isAttachCodeset", isAttachCodeset);
				return_data.put("mustFlag", mustFlag);
				//用于直接跳转到某个子集
				if(StringUtils.isNotEmpty(subset)) {
					ArrayList list = new ArrayList();
					String setid = "";
					if("false".equals(isSubsetEncrypt)) {
					    setid = subset;
					}else {
					    setid = PubFunc.decrypt(subset);
					}
					for (Object obj : field_set_list) {
						LazyDynaBean bean = (LazyDynaBean) obj;
						if(setid.equals(bean.get("fieldsetid"))) {
							list.add(obj);
							break;
						}
					}
					return_data.put("field_set_list", list);
					HashMap map = new HashMap();
					map.put(setid, field_list.get(setid));
					return_data.put("field_list", map);
				}
			}else if("checkMust".equals(type)){
			    Map mustList = resumeBo.checkMust();
			    this.getFormHM().put("mustList", mustList);
			}else if("serarchAttachCodesetFile".equals(type)) {//查询简历附件分类文件
			    ArrayList uploadFileList = resumeBo.getOthFiles();
			    ParameterXMLBo xmlBo = new ParameterXMLBo(this.frameconn,"1");
                HashMap xmlMap = xmlBo.getAttributeValues();
			    ArrayList attach_codeList = bo.getAttachCodeset(xmlMap, candidateStatusid);
			    if(attach_codeList!=null&&attach_codeList.size()>0)
			    	uploadFileList = resumeBo.sortFileList(attach_codeList,uploadFileList);
                resumeBo.getAttachCodeSetState(attach_codeList,uploadFileList);
                this.getFormHM().put("uploadFileList", uploadFileList);
                this.getFormHM().put("attachCodeSet", attach_codeList);
			}
			this.getFormHM().put("return_data", return_data);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
