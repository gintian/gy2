package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowResumeFieldListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HttpSession session=(HttpSession)this.getFormHM().get("session");
			if(this.userView==null)
				this.userView = (UserView) session.getAttribute(WebConstant.userView);
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			OtherParam param=new OtherParam(this.getFrameconn());
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.getFrameconn(),"1");
			HashMap map=xmlBo.getAttributeValues();
			String isAttach="0";
			String idType="";
			if(map.get("attach")!=null&&((String)map.get("attach")).length()>0)
				isAttach=(String)map.get("attach");
			
			if(map.get("id_type")!=null&&((String)map.get("id_type")).length()>0)
				idType=(String)map.get("id_type");
			
			String idTypeValue=(String)hm.get("idTypeValue");
			EmployNetPortalBo bo=new EmployNetPortalBo(this.getFrameconn(),isAttach);
            Map setmap=param.serachAtrr("/param/formual[@name='bycardno']");
            if(setmap!=null){
            	/**得到系统的身份证指标**/
            	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
            	String onlyname = bo.getOnly_field();
                String cardid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");
                String valid = setmap.get("valid").toString().toLowerCase();
                //如果启用了按身份证号计算年龄，性别，出生日期，并且设置了唯一性指标是身份证，那么基本信息年龄，性别，出生日期不允许修改
                if("true".equalsIgnoreCase(valid)&&StringUtils.isNotEmpty(cardid)&&RecruitUtilsBo.getIdTypeValue().equals(idTypeValue)) {
                    String birthdayName = setmap.get("birthday").toString().toLowerCase();
                    String ageName = setmap.get("age").toString().toLowerCase();
                    String axName = setmap.get("ax").toString().toLowerCase();
                    this.getFormHM().put("birthdayName",birthdayName);
                    this.getFormHM().put("ageName",ageName);
                    this.getFormHM().put("axName",axName);
                } else {
                    this.getFormHM().put("birthdayName", "");
                    this.getFormHM().put("ageName", "");
                    this.getFormHM().put("axName", "");
                }
            }
          
			String  setID=(String)hm.get("setID");
			if(StringUtils.isNotEmpty(setID) && !"0".equals(setID) && !"-1".equals(setID))
			    setID = PubFunc.decrypt(setID);
			
			String opt=(String)hm.get("opt");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String  userid=(String)hm.get("userid");
			String  i9999=(String)hm.get("i9999");
			
			hm.remove("userid");
			hm.remove("i9999");
			String a0100=(String)this.getFormHM().get("a0100");
			a0100=PubFunc.getReplaceStr(a0100);
			if(this.userView.getHm().get("isHeadhunter")!=null&&"1".equals((String)this.userView.getHm().get("isHeadhunter"))&&hm.get("a0100")!=null){
				a0100 = PubFunc.decrypt((String)hm.get("a0100"));
				this.getFormHM().put("a0100", a0100);
			}
			//zxj 20141209 直接将userid替换成a0100,避免url中userid被篡改，导致越权看到别人的数据
			//userid=PubFunc.getReplaceStr(userid);
			userid = a0100;
			
			i9999=PubFunc.getReplaceStr(i9999);
			setID=PubFunc.getReplaceStr(setID);
			String login=(String)hm.get("login");
			String  person_type_field="";
			String userName="";
			String person_type="";
			String field="";
			String isDefinitionActive=(String)this.getFormHM().get("isDefinitionActive");
			if(isDefinitionActive==null)
				isDefinitionActive="2";
			
			if(map!=null&&map.get("person_type")!=null)
				person_type_field=((String)map.get("person_type")).toLowerCase();
			
			String dbName = bo.getZpkdbName();
			String  isPhoto=this.getIsPhoto(a0100, dbName);
			if(login!=null&& "2".equalsIgnoreCase(login)){
				
				String loginName=(String)this.getFormHM().get("loginName");
				String password=(String)this.getFormHM().get("password");
				StringBuffer sql=new StringBuffer();
				sql.append("select a0101, a0100,"+person_type_field);
				if("1".equals(isDefinitionActive))
				{
					field=(String)map.get("active_field");
					sql.append(","+field);
				}
				String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
				String workExperience=bo.getWorkExperience();
				if("1".equals(isDefineWorkExperience))
				{
					sql.append(","+workExperience);
				}
				String value="";
				sql.append(" from "+dbName+"a01 where userName='"+PubFunc.getStr(loginName)+"' and UserPassword='"+PubFunc.getStr(password)+"'");
				this.frowset=dao.search(sql.toString());
			    if(this.frowset.next())
			    {
			    	userName=this.frowset.getString("a0101");
			    	a0100=this.frowset.getString("a0100");
			    	person_type=this.frowset.getString(person_type_field);
			    	
			    	if("1".equals(isDefinitionActive))
			    	{
			    		this.getFormHM().put("activeValue", this.frowset.getString(field)==null?"1":this.frowset.getString(field));
			    	}
			    	if("1".equals(isDefineWorkExperience))
			    	{
			    		value=this.frowset.getString(workExperience)==null?"":this.frowset.getString(workExperience);
			    	}
			    }
			    this.getFormHM().put("person_type",person_type);
			    this.getFormHM().put("userName",userName);
				this.getFormHM().put("dbName",dbName);
				this.getFormHM().put("a0100",a0100);
				this.getFormHM().put("workExperience", value);
			}
			
			String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
            String value="";
            if("1".equals(isDefineWorkExperience))
                value=(String)this.getFormHM().get("workExperience");
            
            //招聘渠道
            String hireChannel = (String) this.getFormHM().get("hireChannel");
            //定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
            //headHire、猎头招聘    01、校园招聘   02、社会招聘
            if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
                hireChannel = "02";
            
            String candidate_status_itemId="#";//应聘身份指标
			if(StringUtils.isNotEmpty((String)map.get("candidate_status")))
				candidate_status_itemId=(String)map.get("candidate_status");
			//如果应聘身份指标参数有值，则注册时必须填写应聘身份
			if(!"#".equals(candidate_status_itemId)) {
				hireChannel = bo.getCandidateStatus(candidate_status_itemId, a0100);
				String channelName = bo.getChannelName(candidate_status_itemId,a0100,"");
				this.getFormHM().put("channelName", channelName);
			}else {
				if(StringUtils.isEmpty(hireChannel) || "0".equals(hireChannel))
					hireChannel = bo.getHireChannelFromTable();
				String channelName = bo.getChannelName(hireChannel);
				this.getFormHM().put("channelName", channelName);
			}
            
			if(opt!=null&& "2".equals(opt)) {
				ResumeFileBo resumeBo = new ResumeFileBo(this.getFrameconn(), this.userView);
				ArrayList uploadFileList = resumeBo.getFiles(dbName, a0100, "1");
				ArrayList list = bo.getAttachCodeset(map, hireChannel);
				//如果启用了上传文件分类，则对已上传文件进行排序
				String attach_codeset = (String) map.get("attachCodeset");
				if(StringUtils.isNotEmpty(attach_codeset)&&!"#".equals(attach_codeset))
					uploadFileList = bo.sortFileList(list,uploadFileList);
				this.getFormHM().put("attachCodeSet", list);
			    this.getFormHM().put("uploadFileList", uploadFileList);
			    this.getFormHM().put("opt",opt);
			    this.getFormHM().put("currentSetID","-1");
			}
			
			if(userid==null||(userid!=null&&userid.trim().length()==0))
			{
				i9999="0";
				userid="0";
			}
			/**session超时*/
			if(dbName==null|| "".equalsIgnoreCase(dbName))
			{
				return;//..
			}
			
			String isUpPhoto="0";   //是否必须上传照片
			String isExp="0"; //是否显示指标描述
		
		
			if(map.get("photo")!=null&&((String)map.get("photo")).length()>0)
				isUpPhoto=(String)map.get("photo");
			if(map.get("explaination")!=null&&((String)map.get("explaination")).length()>0)
				isExp=(String)map.get("explaination");
			
			ArrayList list=bo.getZpFieldList();
			list=bo.getSetByWorkExprience(hireChannel);
			this.getFormHM().put("hireChannel",hireChannel);
			
			String resumeStateFieldIds="";
			if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
				resumeStateFieldIds=(String)map.get("resume_state");
			FieldItem fieldItem = DataDictionary.getFieldItem(resumeStateFieldIds, "A01");
			if(fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
				String status=bo.getStatus(a0100, dbName, resumeStateFieldIds, dao);
				String canPrintAdmissionCardStatus=SystemConfig.getPropertyValue("canPrintAdmissionCardStatus");
				canPrintAdmissionCardStatus=","+canPrintAdmissionCardStatus+",";
				if(canPrintAdmissionCardStatus.indexOf((","+status+","))>-1)
					bo.setVisiablSeqField(true);
			}
			
			//获取我的简历页面需要展现的第一个子集      chenxg   2016-05-10
			if("0".equalsIgnoreCase(setID)) {
				ArrayList<LazyDynaBean> fieldsetList = (ArrayList<LazyDynaBean>)list.get(0);
				LazyDynaBean fieldBean = (LazyDynaBean) fieldsetList.get(0);
	            setID = (String) fieldBean.get("fieldSetId");
			}
			//唯一指标
			String onlyFieldSet = "A01,A0A";
			if(!"0".equals(setID)&& !onlyFieldSet.contains(setID) && !"2".equals(opt)) {
				ArrayList showFieldList=bo.getShowFieldList(setID,(HashMap)list.get(2),(HashMap)list.get(1),1);  //取得简历子集 列表需显示的 列指标 集合
				if(showFieldList==null||showFieldList.size()==0)
				{
					i9999=bo.getI9999(setID, a0100, dbName)+"";
					userid=a0100;
				}
				ArrayList showFieldDataList=bo.getShowFieldDataList(showFieldList,a0100,setID,dbName);
				this.getFormHM().put("showFieldDataList",showFieldDataList);
				this.getFormHM().put("showFieldList",showFieldList);
			} else 
				userid=a0100;
			
			this.getFormHM().put("fieldSetList",(ArrayList)list.get(0));
			this.getFormHM().put("fieldMap",(HashMap)list.get(1));
			this.getFormHM().put("isUpPhoto",isUpPhoto);
			this.getFormHM().put("isExp",isExp);
			isAttach = bo.getIsAttach(map, hireChannel, isAttach);
			this.getFormHM().put("isAttach",isAttach);
			this.getFormHM().put("opt",opt);
			int currentSetIndex=0;
			if(!"0".equals(setID)) {
				for(int i=0;i<((ArrayList)list.get(0)).size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)((ArrayList)list.get(0)).get(i);
					String setid=(String)abean.get("fieldSetId");
					if(setid.equalsIgnoreCase(setID))
					{
						currentSetIndex=i;
						break;
					}
				}
			}
			
			if(this.getFormHM().get("emailColumn")==null||((String)this.getFormHM().get("emailColumn")).trim().length()==0)
			{
				String emailColumn=ConstantParamter.getEmailField().toLowerCase();
				this.getFormHM().put("emailColumn",emailColumn);
			}
			
			if(StringUtils.isEmpty(opt) || !"2".equals(opt))
			    this.getFormHM().put("currentSetID",String.valueOf(currentSetIndex));
			/*if(i9999.equals("0")&&currentSetIndex==(((ArrayList)list.get(0)).size()-1))
			{
				i9999="1";
				userid=a0100;
				
			}*/
			String answerSet="";
			if(map!=null&&map.get("answerSet")!=null)
				answerSet=(String)map.get("answerSet");
			if(map!=null&&setID.equalsIgnoreCase(answerSet)){
				ArrayList resumeFieldList=bo.getResumeFieldList2((ArrayList)list.get(0),(HashMap)list.get(2),currentSetIndex,(HashMap)list.get(1),a0100,dbName);
				this.getFormHM().put("resumeFieldList",resumeFieldList);
			}else{
				ArrayList resumeFieldList=bo.getResumeFieldList((ArrayList)list.get(0),(HashMap)list.get(2),currentSetIndex,(HashMap)list.get(1),userid,dbName,i9999);
				this.getFormHM().put("resumeFieldList",resumeFieldList);
			}
			//设置必填的子集
			ArrayList  fieldSetMustList = new ArrayList(); 
			fieldSetMustList = (ArrayList)list.get(4);
			this.getFormHM().put("fieldSetMustList",fieldSetMustList);
            
			if(i9999==null||(i9999!=null&& "0".equals(i9999)))
				this.getFormHM().put("i9999","0");
			else
				this.getFormHM().put("i9999",i9999);
			/**简历状态不再基于这个修改
			String resume_state="";
			if(map.get("resume_state")!=null&&((String)map.get("resume_state")).length()>0)
			{
				resume_state=(String)map.get("resume_state");
			}
			if(resume_state==null||resume_state.equals(""))
				throw GeneralExceptionHandler.Handle(new Exception("系统运行错误，请联系系统管理员！"));
			**/
			String writeable=bo.getWriteable(dao, a0100, dbName);
			
			/**简历是否可修改*/
			this.getFormHM().put("writeable", writeable);
			String isOnlyChecked="0";
			String onlyField="";
			if(bo.isOnlyChecked())
			{
				isOnlyChecked="1";
				onlyField=EmployNetPortalBo.isOnlyChecked;
			}
			this.getFormHM().put("onlyField",onlyField);
			this.getFormHM().put("isOnlyCheck", isOnlyChecked);
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
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");
			String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");
		    String onlyname = bo.getOnly_field();
			String paramFlag="1";
			if(onlyname!=null&&!"".equals(onlyname))
				paramFlag="2";
			if(blacklist_field==null|| "".equals(blacklist_field))
				this.getFormHM().put("blackField", "");
			else
			{
				if("2".equals(paramFlag))
				{
					if(onlyname.equalsIgnoreCase(blacklist_field))
					{
						paramFlag="3";
					}
				}
				this.getFormHM().put("blackField", blacklist_field);
			}
			if(blacklist_per==null|| "".equals(blacklist_per))
				this.getFormHM().put("blackNbase", "");
			else
				this.getFormHM().put("blackNbase",blacklist_per);
			
			this.getFormHM().put("editableMap", editableMap);
			this.getFormHM().put("isHaveEditableField", isHaveEditableField);
			this.getFormHM().put("workExperience", value);
			this.getFormHM().put("answerSet", answerSet);
			this.getFormHM().put("onlyName", onlyname);
			this.getFormHM().put("paramFlag", paramFlag);
			this.getFormHM().put("isPhoto", isPhoto);
			this.getFormHM().put("id_type", idType);
			
			String maxFileSize = "";
			if(map != null && map.get("maxFileSize") != null)
			    maxFileSize = (String) map.get("maxFileSize");
			
			maxFileSize = StringUtils.isEmpty(maxFileSize) || "0".equalsIgnoreCase(maxFileSize) ? "10" : maxFileSize;
			this.getFormHM().put("maxFileSize", maxFileSize);
			/**清除无用内容 防止内存溢出**/
//			this.getFormHM().put("posFieldList", null);
//			this.getFormHM().put("zpPosList", null);
//			this.getFormHM().put("unitPosMap", null);
//			this.getFormHM().put("unitIntroduce", null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	public String getIsPhoto(String a0100,String dbName)
	{
		String isPhoto="0";
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select a0100 from "+dbName+"A00 where a0100='"+a0100+"' and LOWER(Flag)= 'p'");
			if(this.frowset.next())
				isPhoto="1";
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isPhoto;
	}
	

}
