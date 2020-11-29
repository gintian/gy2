package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.options.JWhichUtil;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class AddResumeInfoTrans extends IBusiness {

	@SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		InputStream inputStream=null;
		BufferedReader reader=null;
		InputStream in =null;
		String byA0100 = "";
		try
		{
			    String flag=(String)this.getFormHM().get("flag");                // 1:保存并添加  0：修改 2:保存走下一步 3下一步；4上一步
				String i9999=(String)this.getFormHM().get("i9999");
			    String currentSetID=(String)this.getFormHM().get("currentSetID");
			    String extendFile="";//图片后缀名
				int currentSetid = Integer.parseInt(currentSetID);
				ArrayList fieldSetList=(ArrayList)this.getFormHM().get("fieldSetList");
				HashMap requestMap = (HashMap)this.getFormHM().get("requestPamaHM");
				LazyDynaBean abean=null;
				abean=(LazyDynaBean)fieldSetList.get(currentSetid); 
				ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
				HashMap map=xmlBo.getAttributeValues();
				String isAttach="0";
				if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
					isAttach=(String)map.get("attach");
				EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn(),isAttach); 
				//如果是猎头招聘的话,由于在进入基本信息时没有向A01里面添加简历所对应人员的信息A0100,所以应从新添加一个
				String a0100 = (String) this.getFormHM().get("a0100");
				ArrayList resumeFieldList=(ArrayList)this.getFormHM().get("resumeFieldList");
				String dbName = employNetPortalBo.getZpkdbName();
				a0100=PubFunc.getReplaceStr(a0100);
				if(employNetPortalBo.isOnlyChecked()&& "a01".equalsIgnoreCase((String)abean.get("fieldSetId")))
				{
					boolean checkOnly_Field = checkOnly_Field(resumeFieldList,dbName,a0100);
					if(!checkOnly_Field)
						throw new GeneralException("该人员已存在，不能重复录入！");
					
				}
				
				
				String id_type="";
				String idTypeValue="";
				String idCardValue="";
				String cardId="";
				
				if(map.get("id_type")!=null&&((String)map.get("id_type")).length()>0)
					 id_type=(String)map.get("id_type");
				
				RecordVo vo = ConstantParamter.getConstantVo("ZP_ONLY_FIELD");
	            if (vo != null)
	            	cardId = vo.getString("str_value").toUpperCase();
				
	            if (StringUtils.isNotEmpty(cardId))
	            	cardId = cardId.substring(4);
	            
				for(int i = 0 ; i < resumeFieldList.size() ; i++) {
					LazyDynaBean item = (LazyDynaBean) resumeFieldList.get(i);
					String itemid = (String) item.get("itemid");
					if(StringUtils.isNotEmpty(itemid) && itemid.equalsIgnoreCase(id_type))
						idTypeValue = (String) item.get("value");
					
					if(StringUtils.isNotEmpty(itemid) && itemid.equalsIgnoreCase(cardId))
						idCardValue = (String) item.get("value");
					
					//验证是否有效身份证号
					if(StringUtils.isEmpty(id_type) && StringUtils.isEmpty(cardId)) {
						Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
				        String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
				        if(StringUtils.isNotEmpty(itemid) && itemid.equalsIgnoreCase(chk)){
				        	idCardValue = (String) item.get("value");
					        //验证是否有效身份证号
							if(!PubFunc.idCardValidate(idCardValue)) 
								throw new GeneralException(ResourceFactory.getProperty("hire.out.cardid.verification"));
				        }
					}
				}
				
				//验证是否有效身份证号
				if(StringUtils.isNotEmpty(id_type) && StringUtils.isNotEmpty(cardId) &&  RecruitUtilsBo.getIdTypeValue().equals(idTypeValue)&&!PubFunc.idCardValidate(idCardValue)) {
					throw new GeneralException(ResourceFactory.getProperty("hire.out.cardid.verification"));
				}
				
				
				if("headHire".equals(this.userView.getUserId()))
					employNetPortalBo.setHireChannel("headHire");
				if("headHire".equals(a0100)){//通过猎头招聘进来的,新增简历,从数据库中新取一个a0100
					if("headHire".equals(a0100)&&!"a01".equalsIgnoreCase((String)abean.get("fieldSetId"))){//如果是猎头招聘进来新增简历,并且当前要保存的不是基本信息的话,提示让其先维护基本信息
						throw new GeneralException(ResourceFactory.getProperty("hire.out.headhunter.preserve.basicInformation"));
					}
					if("2".equals(flag)){
						a0100=DbNameBo.insertMainSetA0100(dbName+"A01",this.getFrameconn());
						this.getFormHM().put("a0100", a0100);
						addA01MoreInfor(a0100,dbName+"A01");
					}
					
				}
				byA0100 = a0100;
				
				FormFile form_file = (FormFile) getFormHM().get("file");
				if(form_file!=null&&form_file.getFileSize()>0){	
			   	 	String fname=form_file.getFileName();
			   	 	int indexInt=fname.lastIndexOf(".");
			   	 	extendFile=fname.substring(indexInt+1,fname.length());
					inputStream= form_file.getInputStream();
	                int fsize = form_file.getFileSize();// 获取图片大小
                    if((fsize>512*1024 || fsize==0)&&!"3".equals(flag)){
                    	 throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("hire.upload.photomsg")));
                    }
					/**校验图片是否绑定webshell、shell、cmd_shell、操作系统相关命令等关键字  url ***/

		            StringBuffer sb = new StringBuffer();
		            String str="";
		            Properties props = new Properties();
		            String rootPath=JWhichUtil.getResourceFilePath("ShellKeyWord.properties")==null?"":JWhichUtil.getResourceFilePath("ShellKeyWord.properties");
		            if(rootPath==null|| "".equals(rootPath)){
		            	//throw GeneralExceptionHandler.Handle(new Exception("请配置ShellKeyWord.properties文件!"));
		            }else{
			            reader = new BufferedReader(new InputStreamReader(inputStream),10*1024*1024); 
			            while((str=reader.readLine())!=null){
			            	sb.append( str+"\n");
			            }
		                in = new BufferedInputStream (new FileInputStream(rootPath));
		                props.load(in);
		                Enumeration en = props.propertyNames();
		                while (en.hasMoreElements()) {
		                  String key = (String) en.nextElement();
		                  if(sb.toString().indexOf(key)!=-1){
		                	  throw new GeneralException("您上传的是非法图片!");
		                  }
		                }
		            }

		            InputStream stream = null;
		            String fileType="";
		            try{
		            	stream = form_file.getInputStream();
		            	fileType=EmployNetPortalBo.getFileTypeByHead(form_file.getInputStream());//通过图片头信息获取图片类型
		            }finally{
		            	PubFunc.closeIoResource(stream);
		            }
		            //判断文件后缀和文件格式是否是照片格式
		            if(!isPhotoFormat(fileType) || !isPhotoFormat(extendFile) )
		            		 throw new GeneralException(ResourceFactory.getProperty("hire.upload.photo.format"));
				
		        }
				String answerSet="";
				if(map!=null&&map.get("answerSet")!=null)
					answerSet=(String)map.get("answerSet");

				//保存子集信息
				if(!"3".equals(flag)&&!"4".equals(flag))
				{
			    	
			    	if(map!=null&&((String)abean.get("fieldSetId")).equalsIgnoreCase(answerSet)){
			    		employNetPortalBo.addResumeSetInfo(dbName,resumeFieldList,(String)abean.get("fieldSetId"),a0100,flag,i9999,true);
					}else{
						employNetPortalBo.addResumeSetInfo(dbName,resumeFieldList,(String)abean.get("fieldSetId"),a0100,flag,i9999,false);
					}
				}
			    //保存照片
				if("A01".equals((String)abean.get("fieldSetId"))&&form_file!=null&&form_file.getFileData().length>0&&form_file.getFileData().length<524288)
				{
					if(!"3".equals(flag)&&!"4".equals(flag))
					{
				    	employNetPortalBo.insertPic(form_file,a0100,dbName,(String)this.getFormHM().get("txtEmail"));
				    	this.getFormHM().put("isPhoto","1");
					}
				    
				}
				form_file=null;
				if("3".equals(flag))
					flag="2";
				if("2".equals(flag))
				{
					if((currentSetid+1)<fieldSetList.size())
						currentSetID=String.valueOf((currentSetid+1));
					else if("1".equals(isAttach))
						currentSetID="-1";
				}
				if("-1".equals(currentSetID))
					this.getFormHM().put("opt", "2");
				if("4".equals(flag))
				{
					if((currentSetid-1)>=0)
						currentSetID=String.valueOf((currentSetid-1));
				}
				if(i9999!=null&&!"0".equals(i9999))
					flag="0";
				this.getFormHM().put("currentSetID",currentSetID);			
				this.getFormHM().remove("file");
				
				//保存显示下一页简历信息
				//if(flag.equals("2"))
				{
					String workExperience=employNetPortalBo.getWorkExperience();
					String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
					String value="";
					if("1".equals(isDefineWorkExperience))
						value=(String)this.getFormHM().get("workExperience");
					
					String writeable =(String)this.getFormHM().get("writeable");
					//String a0100=(String)this.getFormHM().get("a0100");
					a0100=PubFunc.getReplaceStr(a0100);
					byA0100 = a0100;
					//招聘渠道
					String hireChannel = (String) this.getFormHM().get("hireChannel");
					
					//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
					//headHire、猎头招聘    01、校园招聘   02、社会招聘
					if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
						hireChannel = "02";
					
					ContentDAO dao = new ContentDAO(this.getFrameconn());
					String candidate_status_itemId="#";//应聘身份指标
					if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
						candidate_status_itemId=(String)map.get("candidate_status");
					//如果应聘身份指标参数有值，则注册时必须填写应聘身份
					if(!"#".equals(candidate_status_itemId)) {
						hireChannel = employNetPortalBo.getCandidateStatus(candidate_status_itemId, a0100);
						String channelName = employNetPortalBo.getChannelName(candidate_status_itemId,a0100,"");
						this.getFormHM().put("channelName", channelName);
					}
					ArrayList list=employNetPortalBo.getZpFieldList();
					
					list=employNetPortalBo.getSetByWorkExprience(hireChannel);
					this.getFormHM().put("hireChannel",hireChannel);
					
					String isOnlyChecked="0";
					String onlyField="";
					if(employNetPortalBo.isOnlyChecked())
					{
						isOnlyChecked="1";
						onlyField=EmployNetPortalBo.isOnlyChecked;
					}
					
					String resumeStateFieldIds="";
					if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
						resumeStateFieldIds=(String)map.get("resume_state");
					String status=employNetPortalBo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
					String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
					canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
					if(canPrintAdmissionCardStatus.indexOf((","+status+",")) > -1)
						employNetPortalBo.setVisiablSeqField(true);
					
					isAttach = employNetPortalBo.getIsAttach(map, hireChannel, isAttach);
					this.getFormHM().put("isAttach",isAttach);
					this.getFormHM().put("onlyField",onlyField);
					this.getFormHM().put("isOnlyCheck", isOnlyChecked);
					this.getFormHM().put("writeable",writeable);
					this.getFormHM().put("fieldSetList",(ArrayList)list.get(0));
					this.getFormHM().put("fieldMap",(HashMap)list.get(1));
					//设置必填的子集
					this.getFormHM().put("fieldSetMustList",(ArrayList)list.get(4));
					int currentSetIndex=Integer.parseInt(currentSetID);
					if(!"-1".equals(currentSetID))
					{
				    	LazyDynaBean a_bean=(LazyDynaBean)((ArrayList)list.get(0)).get(currentSetIndex);
				    	String setid=(String)a_bean.get("fieldSetId");
				    	ArrayList showFieldList=employNetPortalBo.getShowFieldList(setid,(HashMap)list.get(2),(HashMap)list.get(1),1);  //取得简历子集 列表需显示的 列指标 集合
				    	if(showFieldList==null||showFieldList.size()==0)
				    	{
				    		//当应聘简历子集只设置了基本信息的时候
				    		if("A01".equalsIgnoreCase(setid))
				    			setid="A00";
				    		i9999=employNetPortalBo.getI9999(setid, a0100, dbName)+"";
				    	}
				    	ArrayList showFieldDataList=employNetPortalBo.getShowFieldDataList(showFieldList,a0100,setid,dbName);//前台显示内容
				    	this.getFormHM().put("showFieldDataList",showFieldDataList);
				    	this.getFormHM().put("showFieldList",showFieldList);
			    		if(map!=null&&setid.equalsIgnoreCase(answerSet)){
				    		 resumeFieldList=employNetPortalBo.getResumeFieldList2((ArrayList)list.get(0),(HashMap)list.get(2),currentSetIndex,(HashMap)list.get(1),a0100,dbName);	
				     	}
				    	else{
						if(showFieldList==null||showFieldList.size()==0)
				    	    resumeFieldList=employNetPortalBo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),currentSetIndex,(HashMap)list.get(1),a0100,dbName,i9999);
						else
							  resumeFieldList=employNetPortalBo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),currentSetIndex,(HashMap)list.get(1),a0100,dbName,"0");
			    		}
			  	    	this.getFormHM().put("resumeFieldList",resumeFieldList);
				    	if(showFieldList==null||showFieldList.size()==0)
					    	this.getFormHM().put("i9999",i9999);
				    	else
				    		this.getFormHM().put("i9999","0");
					}else{
							//处理在最后一个子集点击下一步到简历时未取到已上传的文件
							HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
							String opt=(String)hm.get("opt");
							
							EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),"1");
							ArrayList uploadFileList = new ArrayList();
					    	if(opt!=null&&!"2".equals(opt))
					    		uploadFileList = bo.getUploadFileList(a0100, dbName);
					    	else if(opt!=null&& "2".equals(opt)){//下一个自己是简历附件
					    		ResumeFileBo filebo = new ResumeFileBo(this.getFrameconn(), this.userView);
								uploadFileList = filebo.getFiles(dbName, a0100, "1");
					    	}
					    	ArrayList mediaList = bo.getMediaSortList();
					    	ArrayList attachCodeSet = bo.getAttachCodeset(map, hireChannel);
					    	this.getFormHM().put("attachCodeSet",attachCodeSet);
					    	this.getFormHM().put("mediaList",mediaList);
						    this.getFormHM().put("uploadFileList", uploadFileList);
						    this.getFormHM().put("opt","2");
						    this.getFormHM().put("currentSetID","-1");
					}
					String editableField=SystemConfig.getPropertyValue("hire_editable_field");
					String isHaveEditableField="0";
					HashMap editableMap=new HashMap();
					if(editableField!=null&&!"".equals(editableField))
					{
						isHaveEditableField="1";
						String arr[]=editableField.split(",");
						for(int i=0;i<arr.length;i++)
						{
							if(arr[i]==null|| "".equals(arr[i]))
								continue;
							editableMap.put(arr[i].toLowerCase(), "1");
						}
					}
					String isResumePerfection="1";
		            if(requestMap.get("finished")!=null&& "1".equals((String)requestMap.get("finished")))
		            {
		            	ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
		    			HashMap _map=parameterXMLBo.getAttributeValues();
		    			if("1".equals(isDefineWorkExperience))
		    			{
		    			//	value=(String)this.getFormHM().get("workExperience");
		    				String workExperience_item=(String)_map.get("workExperience");
		    				this.frowset=dao.search("select "+workExperience_item+" from "+dbName+"a01 where a0100='"+a0100+"'");
		    				if(this.frowset.next())
		    					value=this.frowset.getString(1)!=null?this.frowset.getString(1):"1";  //(String)this.getFormHM().get("workExperience");
		    			
		    			}
		    			
		    			//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
		    			//headHire、猎头招聘    01、校园招聘   02、社会招聘
		    			if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
		    				hireChannel = "02";
		    			
		    			//判断简历资料必填项是否没填
						isResumePerfection = employNetPortalBo.checkRequired(hireChannel,a0100);
		    			requestMap.remove("finished");
		            }
		            this.getFormHM().put("isResumePerfection", isResumePerfection);
					this.getFormHM().put("editableMap", editableMap);
					this.getFormHM().put("isHaveEditableField", isHaveEditableField);
					this.getFormHM().put("workExperience", value);
					this.getFormHM().put("answerSet", answerSet);
						
				}
				
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);  
        }finally{
        	ResumeFilterBo rbo = new ResumeFilterBo(frameconn, userView);
        	rbo.updateByA0100(byA0100);
        	PubFunc.closeResource(inputStream);
        	PubFunc.closeResource(reader);
        	PubFunc.closeResource(in);
        }  

	 }

	/**
	* @Title: addA01MoreInfor
	* @Description: 为新增的简历人员增加创建人员和创建时间
	* @param a0100 新增的人员的a0100
	* @param tableName 招聘人才库 
	* @throws
	*/
	
	private void addA01MoreInfor(String a0100, String tableName) {
		RecordVo vo=new RecordVo(tableName);
		vo.setString("a0100",a0100);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try{
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null){
				vo.setString("createusername", this.userView.getUserName());
		    	vo.setDate("createtime",Calendar.getInstance().getTime());
		    	//设置人员状态
		    	ParameterXMLBo bo=new ParameterXMLBo(this.getFrameconn(),"1");
		    	HashMap map=bo.getAttributeValues();
		    	if(map!=null&&map.get("resume_state")!=null)
		    		vo.setString(((String)map.get("resume_state")).toLowerCase(),"10");
	    		dao.updateValueObject(vo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	} 
	
	private boolean checkOnly_Field(ArrayList<LazyDynaBean> list ,String dbname, String a0100){
		String onlyField = EmployNetPortalBo.isOnlyChecked.toLowerCase();
		StringBuffer whereSql = new StringBuffer(" where 1=1 ");
		for(int i = 0;i<list.size();i++){
			LazyDynaBean item = list.get(i);
			String itemid = (String) item.get("itemid");
			if((onlyField+",").indexOf(itemid.toLowerCase()+",")>-1)
				whereSql.append(" and upper("+itemid +")='"+((String) item.get("value")).toUpperCase()+"'");
			
		}
		//没有唯一性指标时不校验
		if(StringUtils.isEmpty(onlyField)||" where 1=1 ".equals(whereSql))
			return true;
		String sql = "select a0100,"+onlyField+" from "+dbname+"A01 "+whereSql;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frecset = dao.search(sql);
			if(this.frecset.next()&&!this.frecset.getString("a0100").equals(a0100))
				return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//判断照片格式是否属于jpg、jpeg、png、gif或bmp其中之一
	private boolean isPhotoFormat(String fileType){
			boolean flag = false;
			if(StringUtils.isNotEmpty(fileType) && ("jpg".equalsIgnoreCase(fileType)|| "gif".equalsIgnoreCase(fileType)|| "bmp".equalsIgnoreCase(fileType)|| "png".equalsIgnoreCase(fileType)|| "jpeg".equalsIgnoreCase(fileType))){
				 flag = true;
			}
			return flag;
	}
	
}
