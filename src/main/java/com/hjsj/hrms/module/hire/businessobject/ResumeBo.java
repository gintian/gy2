package com.hjsj.hrms.module.hire.businessobject;

import cfca.sadk.control.sip.api.SIPDecryptionBuilder;
import cfca.sadk.control.sip.api.SIPDecryptor;
import com.hjsj.hrms.businessobject.hire.AutoSendEMailBo;
import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.businessobject.infor.multimedia.MultiMediaBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.resumecenter.businessobject.ResumeFileBo;
import com.hjsj.hrms.module.recruitment.util.RecruitUtilsBo;
import com.hjsj.hrms.module.system.distributedreporting.businessobject.FileUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.emailtemplate.businessobject.TemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.sql.RowSet;
import java.io.*;
import java.security.MessageDigest;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class ResumeBo {
	private Connection conn;
	private ContentDAO dao;
	private UserView userview;
	private String mainguid; //人员guidkey
	private String dbName;//招聘库
	
	public String getDbName() {
		return dbName;
	}
	
	public void setUserview(UserView userview) {
		this.userview = userview;
	}
	
	public ResumeBo(Connection conn) {
		this.conn = conn;
		this.dao = new ContentDAO(conn);
		this.getZpkdbName();
	}
	
	public ResumeBo(Connection conn, UserView userview) {
		this(conn);
		this.userview = userview;
	}
	
	// 得到招聘应用库
    public String getZpkdbName(){
        if (StringUtils.isEmpty(dbName)) {
            try {
                RecordVo vo = ConstantParamter.getConstantVo("ZP_DBNAME");
                if (vo == null)
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));
                dbName = vo.getString("str_value");
                if (StringUtils.isEmpty(dbName))
                    throw GeneralExceptionHandler.Handle(new Exception("后台参数没有设置应聘人才库"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dbName;
    }
    
    /**
     * 账号注册信息校验
     * @param params
     * @return
     */
    public String registerCheck(Map<String, String> params) {
    	String return_code = "success";
    	RowSet rs = null;
    	try {
    	    String equalFlag = params.get("equalFlag");
    	    String blackValue = params.get("blackValue");
    	    String idTypeValue = params.get("idTypeValue");
    		String password = params.get("password");
    		return_code = this.validateRules(password,true);
    		if(!"success".equals(return_code))
    			return return_code;
			HashMap<String, String> onlyName = this.getOnlyName();
			String only_name = "";
			String onlyValue = "";
			if(onlyName!=null&&!onlyName.isEmpty()) {
				only_name = onlyName.get("itemid");
				onlyValue = params.get("only_name");
				if(StringUtils.isEmpty(onlyValue)) {
				    return "唯一性指标不能为空！";
				}
				FieldItem fieldItem = DataDictionary.getFieldItem(only_name,"A01");
				if(fieldItem == null||!"1".equals(fieldItem.getUseflag())) {
				    return "唯一性指标未构库，请联系管理员！";
				}
				//验证是否有效身份证号
				if(RecruitUtilsBo.getIdTypeValue().equals(idTypeValue)&&!PubFunc.idCardValidate(onlyValue)) {
					return "请输入正确的身份证号！";
				}
	            return_code = checkBlackList(equalFlag,blackValue,onlyValue);
	            if(!"success".equals(return_code)) {
	                return return_code;
	            }
				ArrayList<String> value = new ArrayList<String>();
				value.add(onlyValue.toUpperCase());
				rs = dao.search("select 1 from "+dbName+"A01 where UPPER("+only_name+")=?",value);
				if(rs.next()) {
				    return fieldItem.getItemdesc()+"重复，不允许注册！";
				}
			}
			String username = params.get("email");
			ArrayList<String> value = new ArrayList<String>();
			value.add(username);
			rs=dao.search("select * from "+dbName+"A01 where userName=?",value);
			if(rs.next()) {
			    return "邮件地址已存在，请重新填写！";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {
			PubFunc.closeResource(rs);
		}
		return return_code;
    }
    /**
     * 检验是否被列在黑名单
     * @param equalFlag 唯一性指标与黑名单指标 =3相同,=2不同
     * @param blackValue
     * @param onlyValue
     * @return
     */
    private String checkBlackList(String equalFlag,String blackValue,String onlyValue) {
        String return_code = "success";
        RowSet rs = null;
        try {
            rs=dao.search("select str_value from constant where constant='ZP_DBNAME'");
            if(rs.next()){
                dbName=rs.getString("str_value");
            }else {
                /*后台参数没有设置应聘人才库！*/
                return "noneDbName";
            }
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
            String blacklist_per=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"base");//黑名单人才库
            String blacklist_field=sysbo.getAttributeValues(Sys_Oth_Parameter.BLACKLIST,"field");//黑名单指标
            EmployNetPortalBo bo = new EmployNetPortalBo(conn);
            /**黑名单检查*/
            if(!"3".equals(equalFlag)&&StringUtils.isNotEmpty(blacklist_field)&&StringUtils.isNotEmpty(blacklist_per)){
                if(bo.isBlackPerson(blacklist_field, blacklist_per, blackValue)){
                    return_code="inBlack";
                }
            }
            if("3".equals(equalFlag)&&StringUtils.isNotEmpty(blacklist_field)&&StringUtils.isNotEmpty(blacklist_per)){
                String onlyname = bo.getOnly_field();
                if(bo.isBlackPerson(onlyname, blacklist_per, onlyValue)){
                    return_code="inBlack";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        } finally {
            PubFunc.closeResource(rs);
        }
        return return_code;
    }

    /**
	 * 校验密码是否符合规则
	 * @param new_pw
	 * @return
	 * @throws GeneralException
	 */
	public String validateRules(String new_pw,boolean isRegiste) throws GeneralException {
		String return_msg = "success";
		try {
			if(StringUtils.isEmpty(new_pw))
				return "密码不能为空！";
			
			String complexPassword="";
			String passwordMinLength="";
			String passwordMaxLength="";
			ParameterXMLBo bo2=new ParameterXMLBo(this.conn);
			HashMap map=bo2.getAttributeValues();
			if(map.get("complexPassword")!=null&&((String)map.get("complexPassword")).length()>0)
				complexPassword=(String)map.get("complexPassword");
			if(map.get("passwordMinLength")!=null&&((String)map.get("passwordMinLength")).length()>0)
				passwordMinLength=(String)map.get("passwordMinLength");
			if(map.get("passwordMaxLength")!=null&&((String)map.get("passwordMaxLength")).length()>0)
				passwordMaxLength=(String)map.get("passwordMaxLength");
			//简单密码
			if(!"1".equals(complexPassword)) {
				passwordMinLength = "6";
				passwordMaxLength = "8";
			}
			
			//注册账号时如果系统参数设置加密，需要解密使用明文校验规则
			if(isRegiste) {
				new_pw = this.handlerPassword(new_pw,"decrypt");
			}
			
			if(new_pw.length()<Integer.parseInt(passwordMinLength)||new_pw.length()>Integer.parseInt(passwordMaxLength)){
				return "密码长度应为"+passwordMinLength+"-"+passwordMaxLength+"位！"; 
			}
			/**验证密码是否符合规则**/
			if("1".equals(complexPassword)){
				int numasc = 0;
				int charasc = 0;
				int otherasc = 0;
				for(int i=0;i<new_pw.length();i++){
					byte[] by=new_pw.substring(i,i+1).getBytes();
					if(by[0]>= 48 && by[0]<= 57){
						numasc+=1;
					}
		            if ((by[0] >= 65 && by[0] <= 90)||(by[0] >= 97 && by[0] <= 122)) {
		                charasc += 1;
		            } 
		            if ((by[0] >= 33 && by[0] <= 42)||by[0] == 64 ||by[0] == 94 ||by[0] == 126 ) {
		                otherasc += 1;
		            }
		        }
		        if(numasc==0)  {
		        	return_msg="密码必须含有数字！";
		        }else if(charasc==0){
		        	return_msg="密码必须含有字母！";
		        }else if(otherasc==0){
		        	return_msg="密码必须含有特殊字符(%$#@!~^&*()')";
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
		return return_msg;
	}
    
    /**
     * 账号注册
     * @param params
     * @param guidkey 
     * @return
     * @throws GeneralException
     */
    public String register(Map<String, String> params, ArrayList<String> list) throws GeneralException {
        String return_code = "success";
		RowSet search = null;
		String table = dbName+"A01";
		String a0100=DbNameBo.insertMainSetA0100(table,this.conn);
		try{
			ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn,"1");
			HashMap map=xmlBo.getAttributeValues();
			HashMap<String, String> onlyName = this.getOnlyName();
			String only_name = "";
			String onlyValue = "";
			if(onlyName!=null&&!onlyName.isEmpty()) {
				only_name = onlyName.get("itemid");
				onlyValue = params.get("only_name");
				if(StringUtils.isEmpty(onlyValue))
					return "唯一性指标不能为空";
			}
			
			String idTypeValue = params.get("idTypeValue");
			String username = params.get("email");
			String password = params.get("password");
			String realname = params.get("realname");
			String candidate_status = params.get("applycode");
			String localUrl = params.get("localUrl");
			String emailColumn=ConstantParamter.getEmailField().toLowerCase();
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			OtherParam param=new OtherParam(this.conn);
			Map setmap=param.serachAtrr("/param/formual[@name='bycardno']");
			String cardid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "1", "name");
			String valid = StringUtils.EMPTY;
			if(setmap != null) {
				valid = setmap.get("valid").toString().toLowerCase();
			}
			RecordVo vo=new RecordVo(table);
			vo.setString("a0100",a0100);
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null) {
				if(onlyName!=null&&!onlyName.isEmpty()) {
				    vo.setString(only_name,onlyValue);
				}
				if(map.get("id_type")!=null&&!"#".equals(map.get("id_type"))) {
					FieldItem fieldItem = DataDictionary.getFieldItem((String)map.get("id_type"), "A01");
					if(fieldItem!=null&&"1".equals(fieldItem.getUseflag()))
						vo.setString((String)map.get("id_type"), idTypeValue);
				}
	    		//注册简历时添加guidkey
	    		String sql = "select guidkey from "+table+" where a0100="+a0100;
				search = dao.search(sql);
				String guidkey = "";
				if(search.next()){
					if(StringUtils.isEmpty(search.getString("guidkey"))){
						UUID uuid = UUID.randomUUID();
						guidkey = uuid.toString().toUpperCase(); 
						vo.setString("guidkey", guidkey);
					}else
						guidkey = search.getString("guidkey");
					list.add(guidkey);
					list.add(a0100);
				}
				//如果启用了按身份证号计算年龄，性别，出生日期
				if(/*RecruitUtilsBo.getIdTypeValue().equals(idTypeValue)&&*/PubFunc.idCardValidate(onlyValue)&&"true".equalsIgnoreCase(valid)) {
					RecruitUtilsBo Calculation = new RecruitUtilsBo();
					String birthdayName = setmap.get("birthday").toString().toLowerCase();
					if(StringUtils.isNotEmpty(birthdayName)) {
						FieldItem birthItem = DataDictionary.getFieldItem(birthdayName, "a01");
						SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
						String birthDay = Calculation.getBirthDay(onlyValue);
						Timestamp date = new Timestamp(sdf.parse(birthDay).getTime());
						if(birthItem!=null&&"1".equals(birthItem.getUseflag()))
							vo.setDate(birthdayName, date);
					}
						String ageName = setmap.get("age").toString().toLowerCase();
					if(StringUtils.isNotEmpty(ageName)) {
						FieldItem ageItem = DataDictionary.getFieldItem(ageName, "a01");
						String age = Calculation.getAge(onlyValue);
						if(ageItem!=null&&"1".equals(ageItem.getUseflag()))
							vo.setString(ageName,age);
					}
						String axName = setmap.get("ax").toString().toLowerCase();
					if(StringUtils.isNotEmpty(axName)) {
						FieldItem axItem = DataDictionary.getFieldItem(axName, "a01");	
						String sex = Calculation.getSex(onlyValue);            
						if(axItem!=null&&"1".equals(axItem.getUseflag()))
							vo.setString(axName,sex);
					}
				}
				vo.setString("username",username);
				vo.setString("a0101",realname);
				vo.setString("userpassword",password);
		    	vo.setDate("createtime",Calendar.getInstance().getTime());
		    	if(emailColumn!=null&&emailColumn.length()>1) {
		    	    vo.setString(emailColumn,username);
		    	}
		    	//设置人员状态
		    	DbWizard dbWizard = new DbWizard(this.conn);
		    	
		    	String candidate_status_itemId = (String)map.get("candidate_status");
		    	if(!"#".equals(candidate_status_itemId)&&StringUtils.isNotEmpty(candidate_status_itemId))
		    		if(dbWizard.isExistField(table, candidate_status_itemId, false))//判断是否已构库，第一个参数表名，第二个字段名，第三个参数false不抛异常
		    			vo.setString(candidate_status_itemId,candidate_status);
		    	
		    	if(map!=null && map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0){
                    String acountBeActived=(String)map.get("acountBeActived");
                    if("1".equals(acountBeActived)) {
                        vo.setString("state", "0");
                    }
                }
	    		dao.updateValueObject(vo);
	    		String acountBeActived=(String)map.get("acountBeActived");
                if("1".equals(acountBeActived)){
                	sendActiveEmail(a0100, username, realname, localUrl);
                }
			}
		}catch(Exception e){
			e.printStackTrace();
			try {
				//注册发生异常删掉已生成信息
				RecordVo vo=new RecordVo(dbName+"A01");
				vo.setString("a0100",a0100);
				dao.deleteValueObject(vo);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return "fail";
		}finally{
			PubFunc.closeResource(search);
		}
		return return_code;
	}

	/**
	 * 发送激活邮件
	 * @param a0100
	 * @param username
	 * @param realname
	 * @param localUrl
	 * @throws Exception
	 */
	public void sendActiveEmail(String a0100, String username, String realname, String urladdr) throws Exception {
			//发送激活帐号邮件
		    String why=SystemConfig.getPropertyValue("masterName");
		    why = why==null?"":why;
		    String str=why;
		    EMailBo emb = new EMailBo(this.conn,true,"");
		    AutoSendEMailBo autoSendEMailBo = new AutoSendEMailBo(this.conn);
		    String from_addr=autoSendEMailBo.getFromAddr();
		    String title=str+"招聘网帐号激活邮件";
		    StringBuffer context = new StringBuffer();
		    Calendar calendar = Calendar.getInstance(); //发送激活邮件的时间
		    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    String activeDate = format.format(calendar.getTime());
		    activeDate=PubFunc.encryption(activeDate);
		    context.append(realname+"&nbsp;&nbsp;您好:\r\n");
		    context.append("您在"+str+"招聘网的帐号已经注册成功，请点击下面链接激活该帐号：<br><br>");
		    context.append("<a style='color:#1aa3ff' href=\""+urladdr+"?activeid="+PubFunc.encrypt(a0100)+"&activeDate="+activeDate+"\"");
		    context.append(" target=\"_blank\">激活帐号</a><br><br>");
		    emb.sendEmail(title,context.toString(),"",from_addr,username);
	}

	/**
	 * 根据邮箱获取登录网址
	 * @param username
	 * @return
	 */
	public String getMailBoxLoginAddress(String emailAddress) {
		String address = "";
		if(StringUtils.isNotBlank(emailAddress)&&emailAddress.split("@").length>1) {
			String[] split = emailAddress.split("@");
			String add = split[1];
			if ((add.toLowerCase()).contains("qq.com")) {
				address = "https://mail.qq.com/cgi-bin/loginpage";
			} else if ((add.toLowerCase()).contains("hjsoft.com")) {
				address = "http://exmail.qq.com/login";
			} else if ((add.toLowerCase()).contains("163.com")) {
				address = "http://mail.163.com/";
			} else if ((add.toLowerCase()).contains("sina.com")) {
				address = "http://mail.sina.com.cn/";
			} else if ((add.toLowerCase()).contains("126.com")) {
				address = "http://www.126.com/";
			} else if((add.toLowerCase()).contains("gmail.com")) {
				address = "http://mail.google.com/";
			}
		}
		return address;
	}
	
	/**
	 * 根据应聘简历子集设置获取简历子集列表
	 * @return
	 */
	public ArrayList getResumeFieldSetList(String hireChannel) {
		EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
		ArrayList paramsInfo = bo.getSetByWorkExprience(hireChannel);
		String deleteRecord = SystemConfig.getPropertyValue("zp_delete_must_set_record");
		if(paramsInfo==null)
			return new ArrayList();
		//该渠道所有子集参数
		ArrayList<LazyDynaBean> setlist = (ArrayList) paramsInfo.get(0);
		//存放必填子集参数
		ArrayList<String> mustlist = (ArrayList) paramsInfo.get(4);
		ArrayList<LazyDynaBean> field_set_list = new ArrayList<LazyDynaBean>();
		for(int i = 0;i<setlist.size();i++) {
			LazyDynaBean obj = setlist.get(i);
			LazyDynaBean bean = new LazyDynaBean();
			String fieldSetId = (String) obj.get("fieldSetId");
			FieldSet fs = DataDictionary.getFieldSetVo(fieldSetId);
			String must = "0";
			for (String setid : mustlist) {
				if(fieldSetId.equalsIgnoreCase(setid))
					must = "1";
			}
			bean.set("fieldsetid", fieldSetId.toUpperCase());
			bean.set("fieldsetdesc", fs.getFieldsetdesc());
			bean.set("displayname", (String) obj.get("fieldSetDesc"));
			bean.set("must", must);
			//支持参数大小写
			if("false".equalsIgnoreCase(deleteRecord))
				bean.set("deleteRecord", "false");
			else
				bean.set("deleteRecord", deleteRecord);
			bean.set("file_flag", fs.getMultimedia_file_flag()==null?"0":fs.getMultimedia_file_flag());
			bean.set("explain",fs.getExplain());
			field_set_list.add(bean);
		}
		return field_set_list;
		
	}
	/**
	 * 根据应聘简历指标设置获取简历指标列表
	 * @return
	 * @throws GeneralException 
	 */
	public HashMap<String, Object> getResumeFieldList(String hireChannel) throws GeneralException {
		RecruitUtilsBo recruitUtilsBo = new RecruitUtilsBo();
		EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
		ArrayList paramsInfo = bo.getSetByWorkExprience(hireChannel);
		HashMap<String, Object> field_list = new HashMap<String, Object>();
		ArrayList<LazyDynaBean> setlist = (ArrayList) paramsInfo.get(0);
		LinkedHashMap fieldMap = (LinkedHashMap) paramsInfo.get(1);
		HashMap<String, HashMap> fieldNameSetMap = (HashMap<String, HashMap>) paramsInfo.get(3);
		//存放指标是否必填参数
		HashMap<String, HashMap> fieldSetMap=(HashMap)paramsInfo.get(2);
		HashMap allOnlyName = this.getAllOnlyName();
		boolean isVisibleExplaination = this.isVisibleExplaination();
		ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
		HashMap params=parameterXMLBo.getAttributeValues();
		String id_type="";
		if(params!=null)
			id_type = (String)params.get("id_type");
		
		for(int i = 0;i<setlist.size();i++) {
			LazyDynaBean obj = setlist.get(i);
			String fieldSetId = (String) obj.get("fieldSetId");
			ArrayList<String> fielditemList = (ArrayList) fieldMap.get(fieldSetId);
			HashMap fieldSet = fieldSetMap.get(fieldSetId.toLowerCase());
			ArrayList<HashMap<String, Object>> fieldlist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, String> fieldNameMap = (HashMap<String, String>) fieldNameSetMap.get(fieldSetId.toLowerCase());
			for (String itemId : fielditemList) {
				String displayname = "";
				if(fieldNameMap!=null&&fieldNameMap.get(itemId)!=null)
					displayname = fieldNameMap.get(itemId);
				String str = (String) fieldSet.get(itemId.toLowerCase());
				String only_field = allOnlyName.get(itemId)==null?"0":"1";
				FieldItem fieldItem = DataDictionary.getFieldItem(itemId, fieldSetId);
				if(fieldItem == null || !"1".equals(fieldItem.getUseflag())) {
					System.out.println("有简历指标不存在，请重新保存应聘简历指标参数！");
					continue;
				}
				String codesetid = fieldItem.getCodesetid();
				String layer = "0";
				//是否只能选末级 0：否
				String leaf_only = "0";
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemid", itemId.toUpperCase());
				map.put("itemdesc", StringUtils.isEmpty(displayname)?fieldItem.getItemdesc():displayname);
				map.put("itemtype", fieldItem.getItemtype());
				map.put("codesetid", codesetid);
				if(codesetid!=null&&!"0".equals(codesetid)) {
					layer = this.getCodeSetLayer(codesetid);
					leaf_only = this.getSelectFlag(codesetid);
				}
				map.put("leaf_only", leaf_only);
				map.put("layer", layer);
				map.put("itemlength", fieldItem.getItemlength());
				map.put("decimal", fieldItem.getDecimalwidth());
				map.put("displayname", displayname);
				map.put("only_field", only_field);
				if("D".equalsIgnoreCase(fieldItem.getItemtype())) {
					String format = recruitUtilsBo.getDateFormat(itemId);
					map.put("format", format);
				}
				//是否为必填项 1：是 0：否
				map.put("must", str.split("#")[1]);
				if(itemId.equalsIgnoreCase(id_type))
					map.put("must", "1");
				if(isVisibleExplaination) {
				    map.put("explain", fieldItem.getExplain());
				}else {
				    map.put("explain","");
				}
				fieldlist.add(map);
			}
			HashMap<String, Object> fieldDataMap = this.getFieldDataList(fieldlist, this.userview.getA0100(), fieldSetId);
			if(fieldDataMap!=null) {
				ArrayList fieldDataList = (ArrayList) fieldDataMap.get("datalist");
					HashMap<String, ArrayList> uploadFileList = this.getUploadFileList(fieldSetId);
					for(int n = 0; n<fieldDataList.size();n++) {
						ArrayList datalist = (ArrayList) fieldDataList.get(n);
						String i9999 = "0";
						if(!"a01".equalsIgnoreCase(fieldSetId)) {
    						HashMap<String, String> bean = (HashMap<String, String>) datalist.get(0);
    						i9999 = bean.get("i9999");
					    }
						ArrayList arrayList = uploadFileList.get(i9999);
						HashMap map = new HashMap();
						map.put("file_list", arrayList);
						datalist.add(map);
				}
			}
			
			field_list.put(fieldSetId, fieldDataMap);
		}
		return field_list;
		
	}
	
    /**
     * 取得 子集 信息集合
     * @param fieldList
     * @param a0100
     * @param setid
     * @return
     */
    public HashMap<String, Object> getFieldDataList(ArrayList<HashMap<String, Object>> fieldList, String a0100, String setid) {
    	RecruitUtilsBo recruitUtilsBo = new RecruitUtilsBo();
    	HashMap<String, Object> dataMap = new HashMap<String, Object>();
        ArrayList list = new ArrayList();
        if (fieldList.size() == 0)
            return null;
        
        RowSet rs = null;
        try {
        	rs = dao.search("select * from " + dbName + setid + " where a0100='" + a0100 + "'");
            while (rs.next()) {
            	ArrayList<HashMap<String, Object>> datalist = new ArrayList<HashMap<String, Object>>();
            	HashMap<String, Object> a_bean = new HashMap<String, Object>();
                if (!"a01".equalsIgnoreCase(setid))
                    a_bean.put("i9999", rs.getString("i9999"));
                for (int i = 0; i < fieldList.size(); i++) {
                	HashMap<String, Object> map = fieldList.get(i);
                	HashMap<String, Object> abean = (HashMap<String, Object>) map.clone();
                    String itemid = (String) abean.get("itemid");
                    String itemtype = (String) abean.get("itemtype");
                    String codesetid = (String) abean.get("codesetid");
                    int itemlength = ((Integer) abean.get("itemlength")).intValue();
                    String value = "";
                    if ("A".equals(itemtype)) {
                        if (rs.getString(itemid) != null) {
                            if ("0".equals(codesetid)) {
                                value = rs.getString(itemid);
                            } else {
                            	abean.put("codeitemid", rs.getString(itemid));
                            	abean.put("fullvalue", getParentCodes(codesetid, rs.getString(itemid)));
                                value = AdminCode.getCodeName(codesetid, rs.getString(itemid));
                            }
                        }
                    } else if ("M".equals(itemtype)) {
                        value = Sql_switcher.readMemo(rs, itemid);
                        value = value.replaceAll("\r\n", "<br>");
                        //value = value.replaceAll(" ", "&nbsp;&nbsp;");
                    } else if ("D".equals(itemtype)) {
                        if (rs.getDate(itemid) != null) {
                        	String format = recruitUtilsBo.getDateFormat(itemid);
                            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                        	abean.put("format", format);
                            value = dateFormat.format(rs.getDate(itemid));
                            //value = value.substring(0, format.length());
                        }
                    } else if ("N".equals(itemtype)) {
                        if (rs.getString(itemid) != null) {
                            value = rs.getString(itemid);
                        }
                    }
                    abean.put("value", value);
                    datalist.add(abean);
                }
                datalist.add(0, a_bean);
                list.add(datalist);
            }
            if(list.size()==0) {
            	dataMap.put("record", "no");
            	list.add(fieldList);
            }else 
            	dataMap.put("record", "yes");
            
            dataMap.put("datalist", list);
            	
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return dataMap;
    }
	
	/**
	 * 获取已上传简历附件
	 * @return
	 */
	public HashMap<String, ArrayList> getUploadFileList(String setid) {
		RowSet rs = null;
		HashMap<String, ArrayList> fileMap = new HashMap<String, ArrayList>();
		try {
            String guid = "";
            String orderFiled = "";
			DbWizard db = new DbWizard(conn);
			String tablename = dbName+setid;
			if(!db.isExistField(tablename, "GuidKey",false)) {
				Table table = new Table(tablename);
				FieldItem item=new FieldItem();
				item.setItemtype("A");
				item.setItemid("GUIDKEY");
				item.setItemlength(40);
				table.addField(item);
				db.addColumns(table);
			}
			StringBuffer sql = new StringBuffer();
			if(!"A01".equalsIgnoreCase(setid)) {
			    sql.append("select A.I9999,hr.* ");
			    guid = "childguid";
			    orderFiled = "I9999";
			}else {
			    sql.append("select hr.* ");
			    guid = "mainguid";
                orderFiled = "id";
			}
			sql.append(" from hr_multimedia_file hr,"+tablename+" A ");
            sql.append(" where GUIDKEY=");
            sql.append(guid);
            if("A01".equalsIgnoreCase(setid)) {
                sql.append(" and (");
                sql.append(Sql_switcher.isnull("childguid", "'#'")).append("='#'");
                if(Sql_switcher.searchDbServer()==1) {//sqlserver 要再处理一下空格
                    sql.append(" or childguid=''");
                }
                sql.append(")");
            }
            sql.append(" and a.A0100='"+this.userview.getA0100()+"'");
            sql.append("  order by ");
            sql.append(orderFiled);
			rs = dao.search(sql.toString());
			while(rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				String i9999 = "0";//主集i9999为0
				if(!"A01".equalsIgnoreCase(setid)) {
				     i9999 = rs.getString("i9999");
				}
				String file_id = rs.getString("id");
				String title = rs.getString("topic");
				String file_name = rs.getString("filename");
				String real_name = rs.getString("srcfilename");
				String path = rs.getString("path");
				String file_type = rs.getString("ext");
				bean.set("file_id", PubFunc.encrypt(file_id));
				bean.set("title", title);
				bean.set("file_name", PubFunc.encrypt(file_name));
				bean.set("encrypt_file_name", PubFunc.encrypt(file_name));
				bean.set("real_name", real_name);
				bean.set("file_type", file_type);
				bean.set("path", path);
				ArrayList list = new ArrayList();
				if(fileMap.get(i9999)!=null)
					list = fileMap.get(i9999);
				
				list.add(bean);
				fileMap.put(i9999, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
		
		return fileMap;
		
	}
	
	/**
	 * 删除简历数据
	 * @param params
	 */
	public void deleteResume(HashMap<String, ArrayList> params) {
		for(Map.Entry<String,ArrayList> entry:params.entrySet()){    
		    String setId =  entry.getKey();
		    ArrayList<MorphDynaBean> values = entry.getValue();
		    for(int i = 0; i<values.size(); i++){
				MorphDynaBean object = values.get(i);
				HashMap<String,String> map = PubFunc.DynaBean2Map(object);
				String i9999 = map.get("i9999");
				deleteResumeInfo(setId, i9999);
		    }
		}
	}
	
	/**
	 * 删除简历信息
	 * @param i9999 
	 * @param setid 
	 */
	public void deleteResumeInfo(String setid, String i9999) {
		try {
			String a0100 = userview.getA0100();
			ArrayList<String> values = new ArrayList<String>();
			values.add(a0100);
			values.add(i9999);
			dao.delete("delete from " + dbName + setid + " where a0100=? and i9999=?", values);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	/**
	 * 获取应聘身份指标
	 * @return
	 */
	public HashMap getCandidateStatus() {
		HashMap status = new HashMap();
		try {
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			String candidate_status="#";//应聘身份指标
			if(map.get("candidate_status")!=null) {
				candidate_status=(String)map.get("candidate_status");
				if(StringUtils.isNotEmpty(candidate_status)&&!"#".equals(candidate_status)) {
					FieldItem fieldItem = DataDictionary.getFieldItem(candidate_status,"A01");
					if(fieldItem==null)
						throw GeneralExceptionHandler.Handle(new Exception("应聘身份指标不存在"));
					
					if(!"1".equals(fieldItem.getUseflag()))
						throw GeneralExceptionHandler.Handle(new Exception("应聘身份指标未构库"));
					
					status.put("itemid",candidate_status);
					status.put("itemdesc",fieldItem.getItemdesc());
					status.put("itemtype",fieldItem.getItemtype());
					status.put("codesetid",fieldItem.getCodesetid());
					status.put("itemlength",fieldItem.getItemlength());
					status.put("decimal",fieldItem.getDecimalwidth());
					status.put("explain",fieldItem.getExplain());
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * 获取唯一性指标
	 * @return
	 */
	public HashMap getOnlyName() {
		HashMap map = new HashMap();
		EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
		String onlyname = bo.getOnly_field();
		RowSet search = null;
		try {
			if(StringUtils.isNotEmpty(onlyname)) {
				FieldItem item = DataDictionary.getFieldItem(onlyname.toLowerCase());
				ArrayList<CodeItem> codeItemList = AdminCode.getCodeItemList("35");
				map.put("itemid", item.getItemid());
				map.put("itemdesc", item.getItemdesc());
				//获取显示名称
				for (CodeItem obj : codeItemList) {
					ArrayList paramsInfo = bo.getSetByWorkExprience(obj.getCodeitem());
					if(paramsInfo!=null&&paramsInfo.size()>0) {
						HashMap<String, HashMap> fieldNameSetMap = (HashMap<String, HashMap>) paramsInfo.get(3);
						HashMap<String, String> fieldNameMap = (HashMap<String, String>) fieldNameSetMap.get("a01");
						if(item.getItemid()!=null&&fieldNameMap!=null&&fieldNameMap.get(item.getItemid())!=null) {
							map.put("itemdesc", fieldNameMap.get(item.getItemid()));
							break;
						}
					}
				}
				map.put("itemtype", item.getItemtype());
				map.put("codesetid",item.getFieldsetid());
				map.put("itemlength",item.getItemlength());
				map.put("decimal",item.getDecimalwidth());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(search);
		}
		return map;
	}
	
	/**
	 * 获取唯一性指标
	 * 只取第一个
	 * @return
	 */
	public HashMap getAllOnlyName() {
		HashMap onlyMap = new HashMap();
		try {
			RecordVo vo = new RecordVo("constant");
	        vo.setString("constant", "ZP_ONLY_FIELD");
			if (dao.isExistRecordVo(vo)) {
				vo = dao.findByPrimaryKey(vo);
				String only_field_str = vo.getString("str_value");
				if (only_field_str != null && only_field_str.trim().length() > 0) {
					String[] arr = only_field_str.split(",");
					for (int i = 0; i < arr.length; i++) {
						if (arr[i] == null || "".equals(arr[i]))
						    continue;
						onlyMap.put(arr[i].substring(4), "1");
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return onlyMap;
	}
	
	/**
     * 根据后台设置禁止修改的简历状态判断该简历是否可修改
     * 1是，0否
     */
    public String getWriteable() {   
	    /** 默认为可以修改 */
	    String writeable = "1";
	    RowSet rs = null;
	    RowSet rowSet = null;
	    boolean flag = false;
	    try {
	    	// 根据简历所报职位的状态和有效结束日期和招聘流程中此环节“是否允许修改简历”的状态控制简历修改
	    	StringBuffer sql = new StringBuffer();
			sql.append("select Z0101,Z0331 from Z03 Z, zp_pos_tache P,zp_flow_status  F  ");
			sql.append(" where P.resume_flag=F.status ");
			sql.append(" and Z.Z0301 = P.ZP_POS_ID ");
			sql.append(" and P.link_id=F.link_id  ");
			sql.append(" and (f.resume_modify=0 or ");
			sql.append(" ( f.resume_modify is null  ");
			sql.append(" and ( P.resume_flag not in ('0105','0106','0205','0206','0306','0307', ");
			sql.append(" '0308','0406','0407','0408','0506','0507','0508','0603','0604','0703','0704','0805','0806','1003','1004','1005'))))  ");
			sql.append(" and a0100= '").append(userview.getA0100()).append("'");
			sql.append(" and p.nbase= '").append(dbName).append("'");
			sql.append(" and Z.Z0319 <>'06'  ");
			rs = dao.search(sql.toString());
			
			while(rs.next()) {
	        	String z0101 = rs.getString("Z0101");
	        	Timestamp z0331Time = rs.getTimestamp("Z0331");
	            Date nowTime = new Date();
	            int result = z0331Time.compareTo(nowTime);
	            if (result > 0) 
	            {
	            	if(StringUtils.isNotEmpty(z0101)){
	            		sql = new StringBuffer();
	        			sql.append("select Z0109 from Z01 ");
	        			sql.append(" where Z0129 <>'06'");
	        			sql.append(" and  Z0101 ='").append(z0101).append("'");
	        			rowSet= dao.search(sql.toString());
	        			if(rowSet.next()){
	        				Timestamp Z0109 = rowSet.getTimestamp("Z0109");
	        				if(Z0109 != null) {
	        					result = Z0109.compareTo(nowTime);
		                        if(result > 0) {
		                        	/** 不可修改 */
		                            writeable = "0";
		                            break;
		                        }
	        				}
	                        
	        			}
	            	}else{
	            		/** 不可修改 */
	                    writeable = "0";
	                    break;
	            	}
	            }
	        }
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        PubFunc.closeDbObj(rs);
	        PubFunc.closeDbObj(rowSet);
	    }
	    return writeable;
    }
    
    /**
     * 最大上传附件大小
     * @return
     */
    public String getMaxFileSize() {
    	String maxFileSize = "";
    	try {
	    	ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map = parameterXMLBo.getAttributeValues();
			if(map != null && map.get("maxFileSize") != null)
				maxFileSize = (String) map.get("maxFileSize");
			
			maxFileSize = maxFileSize == null || "0".equalsIgnoreCase(maxFileSize) ? "10" : maxFileSize;
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
		return maxFileSize;
    }
    
    /**
     * 照片是否必须上传
     * isUpPhoto="1"必须上传
     * @return
     */
    public String getIfMustUpload() {
    	String isUpPhoto = "0";
    	try {
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map = parameterXMLBo.getAttributeValues();
			if(map.get("photo")!=null&&((String)map.get("photo")).length()>0)
				isUpPhoto=(String)map.get("photo");
		} catch (GeneralException e) {
			e.printStackTrace();
		}
    	
    	return isUpPhoto;
    }
    
    /**
     * 获取照片路径
     * @return
     */
    public String getPhotoPath() {
    	String a0100 = userview.getA0100();
		
		StringBuffer photourl = new StringBuffer();
		try{
			if ("A0100".equals(a0100))
				a0100 = "";

			String fileid = ""; 
			if (StringUtils.isNotEmpty(a0100)) {
				fileid = this.createPhotoFile(dbName + "A00",	a0100, "P");
			}
			if (!"".equals(fileid)) {
				photourl.append("/servlet/vfsservlet?fileid=");
				photourl.append(fileid);
			} 
		}catch(Exception ex){
		}
		return photourl.toString();
    }
    
    /**
     * 根据人员库前缀和人员编码生成其对应的文件
     * 
     * @param userTable
     *            应用库 usra01
     * @param userNumber
     *            0000001 ,a0100
     * @param flag
     *            'P'照片
     * @return
     * @throws Exception
     */
    public String createPhotoFile(String userTable, String userNumber, String flag){
        String fileid="";
        ResultSet rs = null;
        try {
            StringBuffer strsql = new StringBuffer();
            strsql.append("select ext,Ole,fileid from ");
            strsql.append(userTable);
            strsql.append(" where A0100='");
            strsql.append(userNumber);
            strsql.append("' and Flag='");
            strsql.append(flag);
            strsql.append("'");
           
            rs=dao.search(strsql.toString());
            
            if (rs.next()) {
            	fileid = rs.getString("fileid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
        }
        return fileid;
    }
    
    /**
     * 校验唯一性指标值是否已存在
     * @param params
     * @return
     */
    public String checkOnlyValue(HashMap<String, ArrayList> params) {
    	String return_code = "success";
    	RowSet rs = null;
    	try {
    		HashMap onlyName = this.getAllOnlyName();
    		//唯一性指标没有设置的时候不需要校验值
    		if(onlyName.isEmpty())
    			return return_code;
	    	ArrayList<MorphDynaBean> value = params.get("A01")!=null?params.get("A01"):params.get("a01");
	    	HashMap item = this.getOnlyName();
	    	String itemid = (String) item.get("itemid");
	    	String itemdesc = (String) item.get("itemdesc");
	    	if(value!=null&&value.size()>0&&item!=null&&StringUtils.isNotEmpty(itemid)) {
				MorphDynaBean object = value.get(0);
				HashMap<String,String> map = PubFunc.DynaBean2Map(object);
				String onlyValue = map.get(itemid.toUpperCase());
				if(StringUtils.isNotBlank(onlyValue)) {
					StringBuffer sql = new StringBuffer("select 1 from "+dbName+"A01 ");
					sql.append( " where "+itemid+"=? ");
					sql.append(" and a0100<>?");
					ArrayList<String> param = new ArrayList<String>();
					param.add(onlyValue);
					param.add(this.userview.getA0100());
					rs = dao.search(sql.toString(), param);
					if(rs.next())
						return_code = itemdesc+"指标已存在请重新填写！";
				}else {
					return_code = itemdesc+"指标不能为空";
				}
	    	}
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return "fail";
    	} finally {
			PubFunc.closeResource(rs);
		}
		return return_code;
    }
    
    /**
     * 所有要修改或者增加的简历信息
     * @param params
     * @param type update修改数据，add 增加数据
     */
    public HashMap<String,Object> addResumeInfo(HashMap<String, ArrayList> params, String type) {
    	String return_code = "success";
    	HashMap<String,Object> i9999_map = new HashMap<String,Object>();
    	for(Map.Entry<String,ArrayList> entry:params.entrySet()){    
		    String setId =  entry.getKey();
		    ArrayList<MorphDynaBean> values = entry.getValue();
		    return_code = getRecordVo(setId,values,type,i9999_map);
		    if("fail".equals(return_code))
		    	break;
		}
    	i9999_map.put("return_code", return_code);
    	return i9999_map;
    }
    
    public String getRecordVo(String setId, ArrayList<MorphDynaBean> values,String type, HashMap<String, Object> i9999_map) {
    	String return_code = "success";
    	RecruitUtilsBo recruitUtilsBo = new RecruitUtilsBo();
    	HashMap<String,Object> imap = new HashMap<String,Object>();
    	String tableName = this.dbName.toLowerCase() + setId;
    	RecordVo vo = new RecordVo(tableName);
    	RowSet rs = null; 
    	try {
			String a0100 = this.userview.getA0100();
			vo.setString("a0100", a0100);
			if("A01".equalsIgnoreCase(setId)) {
				vo = dao.findByPrimaryKey(vo);
			}
			for(int i = 0; i<values.size(); i++){
				MorphDynaBean object = values.get(i);
				HashMap<String,String> map = PubFunc.DynaBean2Map(object);
				if(!"A01".equalsIgnoreCase(setId)) {
					String temp = map.get("i9999");
					int i9999 = Integer.parseInt(temp);
					if(i9999<0) {
						i9999 = 1;
						rs = dao.search("select max(i9999) i9999 from " + tableName + " where a0100='" + a0100 + "'");
						if(rs.next()){
							if(rs.getObject("i9999")!=null){
								i9999=rs.getInt("i9999")+1;
							}
						}
					}
					vo.setInt("i9999", i9999);
					//不再用type区分是更新还是新增操作
					if(Integer.parseInt(temp)<0) {
						dao.addValueObject(vo);
						imap.put(temp, i9999);
					}
				}
				for(Map.Entry<String,String> obj:map.entrySet()){
					String itemid =  obj.getKey().toLowerCase();
					if("i9999".equalsIgnoreCase(itemid))
						continue;
					String itemvalue = "";
					if(obj.getValue() != null)
						itemvalue = String.valueOf(obj.getValue());
					FieldItem item = DataDictionary.getFieldItem(itemid,setId);
					int decimalwidth = item.getDecimalwidth();
					String itemtype = item.getItemtype();
					if("D".equalsIgnoreCase(itemtype)) {
						Date date = null;
						if(StringUtils.isNotEmpty(itemvalue)){
							String format = recruitUtilsBo.getDateFormat(itemid);
							date = DateUtils.getDate(itemvalue, format);
						}

						vo.setDate(itemid, date);
					}else if("N".equalsIgnoreCase(itemtype)) {
						if(StringUtils.isEmpty(itemvalue))
							itemvalue="0";
						if(decimalwidth==0)
							vo.setInt(itemid, Integer.valueOf(itemvalue));
						else
							vo.setDouble(itemid, Double.valueOf(itemvalue));
					}
					else
						vo.setString(itemid, itemvalue);
				}
				
				dao.updateValueObject(vo);
			}
			i9999_map.put(setId, imap);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return "fail";
    	} finally {
    		PubFunc.closeResource(rs);
    	}
		return return_code;
    }
    /**
     * 将base64字符解码成输入流
     * @param base64Code
     * @throws Exception
     */
    public InputStream decoderBase64ToInputStream(String base64Code) throws Exception {
    	byte[] buffer = Base64.decodeBase64(base64Code);
		InputStream inputStream = new ByteArrayInputStream(buffer);
		return inputStream;
    }
    
    /**
     * 将base64字符解码保存文件
     * @param base64Code
     * @throws Exception
     */
    public File decoderBase64File(String base64Code, String path) throws Exception {
		File file = null;
        FileOutputStream out = null;
        BufferedOutputStream bos = null;
        try {
            // 解码，然后将字节转换为文件
            file = new File(path);
            if (!file.exists())
                file.createNewFile();
            byte[] bytes =Base64.decodeBase64(base64Code);// 将字符串转换为byte数组
            out = new FileOutputStream(file);
            bos = new BufferedOutputStream(out);
            bos.write(bytes);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            PubFunc.closeIoResource(bos);
            PubFunc.closeIoResource(out);
        }
        return file;
    }
    
    private Blob getOracleBlob(InputStream inputStream, String userbase, String userid, String recid) throws FileNotFoundException, IOException {
		try {
			StringBuffer strSearch=new StringBuffer();
			strSearch.append("select ole from ");
			strSearch.append(userbase);
			strSearch.append("a00 where a0100='");
			strSearch.append(userid);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			 
			StringBuffer strInsert=new StringBuffer();
			strInsert.append("update  ");
			strInsert.append(userbase);
			strInsert.append("a00 set ole=EMPTY_BLOB() where a0100='");
			strInsert.append(userid);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
			OracleBlobUtils blobutils=new OracleBlobUtils(conn);
			Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),inputStream);
			return blob;
		} finally {
			PubFunc.closeIoResource(inputStream);
		}
	}
    
    /**
     * 保存照片
     * @param fileName 文件名
     * @param inputStream 文件输入流
     * @param i9999
     * @return
     */
    public String savePhoto(String fileName, InputStream inputStream, String i9999) {
    	String return_code = "success";
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	try {
			String a0100 = this.userview.getA0100();
			String username = this.userview.getUserName();
			deleteDAO(a0100, dbName);
			String title = fileName.substring(0,fileName.lastIndexOf("."));
			String ext = fileName.substring(fileName.lastIndexOf("."));
			Date date = new Date();
			Timestamp create_time = new Timestamp(date.getTime());
			RecordVo tempvo=new RecordVo(dbName+"A00");
			tempvo.setString("a0100", a0100);
			tempvo.setString("i9999", i9999);
			tempvo.setString("title", title);
			tempvo.setString("flag", "P");
			tempvo.setString("ext", ext);
			tempvo.setDate("createtime",create_time);
			tempvo.setString("createusername", username);
			int len = 0;
			byte[] temp = new byte[1024];
			while ((len = inputStream.read(temp)) != -1) {
				out.write(temp, 0, len);
			}
			//输入流读过一次就读不到了，从outStream里重新取一下
			inputStream = new ByteArrayInputStream(out.toByteArray());
			String fileid = addFile(fileName, new ByteArrayInputStream(out.toByteArray()));
			tempvo.setString("fileid", fileid);
			switch (Sql_switcher.searchDbServer()) {
			case Constant.ORACEL:
				break;
			default:
				byte[] buffer = out.toByteArray();
				tempvo.setObject("ole", buffer);
				break;
			}
			dao.addValueObject(tempvo);
			//oracle 库保存文件要先转成blob
			if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
				RecordVo updatevo = new RecordVo(dbName + "A00");
				updatevo.setString("a0100", a0100);
				updatevo.setString("i9999", i9999);
				Blob blob = getOracleBlob(inputStream, dbName , a0100, i9999);
				if (blob != null) {
					updatevo.setObject("ole", blob);
					dao.updateValueObject(updatevo);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {
			PubFunc.closeIoResource(out);
			PubFunc.closeIoResource(inputStream);
		}
    	return return_code;
	}
    
    /**
     * 保存照片
     * @param filename
     * @param file_base64
     * @param i9999
     * @return
     */
    @Deprecated
    public String savePhoto(String filename,String file_base64,String i9999) {
    	InputStream inputStream = null;
		try {
			inputStream = decoderBase64ToInputStream(file_base64);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return this.savePhoto(filename, inputStream, i9999);
    }
    
    /**
     * 保存新照片之前把旧的删掉
     * @param A0100
     * @param userbase
     * @throws GeneralException
     */
    private void deleteDAO(String A0100, String userbase) throws GeneralException {

        try {
        	String fileId = this.createPhotoFile(dbName + "A00", A0100, "P");
			if (StringUtils.isNotBlank(fileId)) {
				VfsService.deleteFile(userview.getUserName(), fileId);
			}
	        StringBuffer deletesql = new StringBuffer();
	        deletesql.append("delete from ");
	        deletesql.append(userbase);
	        deletesql.append("a00 where a0100='");
	        deletesql.append(A0100);
	        deletesql.append("' and flag='P'");
        	dao.delete(deletesql.toString(),new ArrayList());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 保存子集附件
     * @param filename
     * @param file_base64
     * @param setId
     * @param i9999
     * @return
     */
    public String saveMultimediaFile(String filename,String file_base64, String setId, String i9999) {
    	String return_code = "success";
    	try {
			String a0100 = this.userview.getA0100();
			String filetitle = filename.substring(0,filename.lastIndexOf("."));
			String filetype = filename.substring(filename.lastIndexOf("."));
			String fileName = UUID.randomUUID().toString();
			String childguid = "";
			String path = System.getProperty("java.io.tmpdir")+File.separator+fileName+filetype;
			File file = this.decoderBase64File(file_base64, path);
			MultiMediaBo mbo = new MultiMediaBo(conn, userview);
			mbo.setA0100(a0100);
			mbo.setI9999(Integer.valueOf(i9999));
			mbo.setNbase(dbName);
			mbo.setSetId(setId);
			HashMap<String, String> allMap = new HashMap<String, String>();
			if(StringUtils.isEmpty(mainguid))
				mainguid = getGuidKey(dbName+"A01","",false);
			if(!"A01".equalsIgnoreCase(setId)) {
			    childguid = getGuidKey(dbName+setId,i9999,true);
			    allMap.put("childguid", childguid);
			}
			allMap.put("mainguid", mainguid);
			allMap.put("nbase", dbName);
			allMap.put("a0100", a0100);
			//指子集附件类型中的“文件”
			allMap.put("filetype", "F");
			allMap.put("filetitle", filetitle);
			allMap.put("description", "");
			mbo.saveMultimediaFile(allMap, file);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
    	return return_code;
    }
    
    /**
     * 获取其他简历附件
     * @return
     */
    public ArrayList getOthFiles() {
		String a0100 = this.userview.getA0100();
		ResumeFileBo bo = new ResumeFileBo(this.conn, this.userview);
		ArrayList<LazyDynaBean> uploadFileList = bo.getFiles(dbName, a0100, "1");
		ArrayList<LazyDynaBean> files = new ArrayList<LazyDynaBean>();
		for (LazyDynaBean obj : uploadFileList) {
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("file_id", obj.get("id"));
			// 20191123 zxj 没办法，为了兼容，外部使用的地方file_name和fileName都有
			bean.set("file_name", obj.get("fileName"));
			bean.set("fileName", obj.get("fileName"));
			bean.set("encrypt_file_name", PubFunc.encrypt((String)obj.get("path")));
			bean.set("file_type", obj.get("fileType"));
			bean.set("path", obj.get("path"));
			files.add(bean);
		}
		return files;
	}
    
    /**
     * @param tablename
     * @param i9999
     * @param bMain 是否主集
     * @return
     */
    private String getGuidKey(String tablename,String i9999,boolean bMain)
    {
        String guid="";
        RowSet frowset=null;
        String a0100 = this.userview.getA0100();
        try{
            StringBuffer sb = new StringBuffer();
            StringBuffer sWhere  = new StringBuffer();
            
            sWhere.append(" where a0100 ='");
            sWhere.append(a0100);
            sWhere.append("'");
            if(bMain) {
            	sWhere.append(" and i9999 =");
            	sWhere.append(i9999); 
            }
            
            sb.append("select GUIDKEY from ");
            sb.append(tablename);     
            sb.append(sWhere.toString());   
            
            frowset = dao.search(sb.toString());
            if (frowset.next()) {
                guid = frowset.getString("guidkey");
                if (StringUtils.isEmpty(guid)){
                    UUID uuid = UUID.randomUUID();
                    String tmpid = uuid.toString(); 
                    StringBuffer stmp = new StringBuffer();
                    stmp.append("update  ");
                    stmp.append(tablename);   
                    stmp.append(" set GUIDKEY ='");
                    stmp.append(tmpid.toUpperCase());
                    stmp.append("'");                    
                    stmp.append(sWhere.toString());
                    stmp.append(" and guidkey is null ");   
                    dao.update(stmp.toString());                

                    frowset = dao.search(sb.toString());
                    if (frowset.next()) {
                        guid = frowset.getString("guidkey");             
                    }
                }
            }
        }
        catch (Exception e ){
           e.printStackTrace();             
        } finally {
        	PubFunc.closeResource(frowset);
        }
        return guid;
     }

    /**
	 * 上传简历子集附件
	 * @param params
	 * @return
	 */
	public ArrayList uploadFiles(ArrayList params) {
		String return_code = "success";
		//上传失败得文件
		ArrayList<String> fail_name = new ArrayList<String>();
		for(int i=0; i<params.size(); i++){
			MorphDynaBean bean = (MorphDynaBean) params.get(i);
			HashMap<String,ArrayList> map = PubFunc.DynaBean2Map(bean);
			for(Map.Entry<String,ArrayList> entry:map.entrySet()){    
			    String setId = entry.getKey();
			    ArrayList<MorphDynaBean> values = entry.getValue();
			    for(int n = 0; n<values.size(); n++){
			    	MorphDynaBean object = values.get(n);
					HashMap<String,String> file = PubFunc.DynaBean2Map(object);
					String filename = file.get("filename");
					String encode = file.get("encode");//采用上传文件名加密的方式，如果有加密标识则进行解密，解决中文乱码的问题
					if(StringUtils.equals(encode,"true")){
                        filename = SafeCode.decode(filename);
                    }
					String filetype = file.get("filetype");
					String i9999 = file.get("i9999");
					String file_base64 = file.get("file");
					filename += "."+filetype;
					file_base64 = file_base64.substring(file_base64.indexOf(",")+1);
			    	return_code = this.saveMultimediaFile(filename, file_base64, setId , i9999);
			    	if("fail".equals(return_code)) {
			    		fail_name.add(filename);
			    	}
			    }
			}
		}
		return fail_name;
	}
	
	/**
	 * 保存其他简历附件
	 * @return
	 */
	@Deprecated
	public ArrayList<String> uploadOthFiles(ArrayList params) {
		String return_code = "success";
		//保存上传失败得文件名
		ArrayList<String> fail_name = new ArrayList<String>();
		for(int i=0; i<params.size(); i++){
			MorphDynaBean bean = (MorphDynaBean) params.get(i);
			HashMap<String,String> file = PubFunc.DynaBean2Map(bean);
			String filename = file.get("filename");
			String encode = file.get("encode");//是否加密,采取加密方式解决上传中文乱码的问题
			if(StringUtils.equals("true",encode)){
                filename = SafeCode.decode(filename);
            }
			String filetype = file.get("filetype");
			String file_base64 = file.get("file");
			filename += "."+filetype;
			file_base64 = file_base64.substring(file_base64.indexOf(",")+1);
	    	return_code = this.uploadOthFiles(filename, file_base64);
	    	if("fail".equals(return_code)) {
	    		fail_name.add(filename);
	    	}
		}
		return fail_name;
	}
	
	/**
	 * 保存其他简历附件
	 * @param filename
	 * @param file_base64
	 * @return
	 */
	@Deprecated
	public String uploadOthFiles(String filename, String file_base64){
		String return_code = "success";
		String a0100 = this.userview.getA0100();
		File tempDir = null;
		String filetype = filename.substring(filename.lastIndexOf("."));
		try {
			String path = getPath();
    		//创建目录
            tempDir = new File(path);
            if (!tempDir.exists()) {
                tempDir.mkdirs();
            }
			String name = getFileName(path,filename);
			path = path+name;
			//保存文件
			this.decoderBase64File(file_base64, path);
			//增加文件记录
			saveAttachment(a0100,"00" ,dbName, name,name,"");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {
			PubFunc.closeResource(tempDir);
		}
		return return_code;
	}
	
	/**
	 * 保存文件
	 * @param filename
	 * @param input
	 * @return
	 */
	public String uploadOthFiles(String filename, InputStream input){
		String return_code = "success";
		String a0100 = this.userview.getA0100();
		String filetype = filename.substring(filename.lastIndexOf("."));
		try {
			String fileid = addFile(filename, input);
			//增加文件记录
			saveAttachment(a0100,"00" ,dbName, fileid,filename,"");
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {
			PubFunc.closeResource(input);
		}
		return return_code;
	}

	private String addFile(String filename, InputStream input) throws Exception {
		//vfs保存文件start
		String username = userview.getUserName();
		//文件类型
		VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
		//所属模块
		VfsModulesEnum vfsModulesEnum = VfsModulesEnum.ZP;
		// 文件所属类型
		VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
		String guidkey = getGuidKey(dbName+"A01","",false);
		//文件扩展标识 可以通过扩展标识拿到这个人的所有简历附件
		String fileTag = "zp_"+guidkey;
		//文件加密id
		String fileid = "";
		boolean isTempFile = false;
		fileid = VfsService.addFile(username, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, guidkey, input,
				filename, fileTag, isTempFile);
		return fileid;
	}
	
	/**
     * 保存简历分类附件
     * @return
     */
    public String uploadAttachCodeSetFiles(String filename, InputStream input, String attachCodeItemid) {
    	String return_code = "success";
    	try {
    		String a0100 = this.userview.getA0100();
    		String filetype = filename.substring(filename.lastIndexOf(".")+1);
			ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn,"1");
			HashMap xmlMap = xmlBo.getAttributeValues();
			String candidateStatusid = (String) this.userview.getHm().get("applyCode");
			EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
			ArrayList attachCodeSet =  bo.getAttachCodeset(xmlMap, candidateStatusid);
			for(int j=0;j<attachCodeSet.size();j++) {
                HashMap tempHashMap = (HashMap) attachCodeSet.get(j);
                if(tempHashMap.get("itemId").equals(attachCodeItemid)) {
                    filename = (String)tempHashMap.get("itemDesc");
                }
            }
			checkFileName("", filename, a0100, this.dbName);
			filename = StringUtils.isEmpty(filename)?filename:filename+"."+filetype;
			uploadOthFiles(filename, input);
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		}
    	
		return return_code;
    }

	/**
	 * 获得文件路径（不包括文件名）
	 * @return
	 * @throws GeneralException
	 */
    @Deprecated
	private String getPath() throws GeneralException {
		if(StringUtils.isEmpty(mainguid))
			mainguid = getGuidKey(dbName+"A01","",false);
		TemplateBo tb = new TemplateBo(this.conn, dao, this.userview);
		String rootPath = tb.getRootDir();
		String path = rootPath+"doc"+File.separator+"resume"+File.separator+this.getGuidDir(mainguid)+File.separator+mainguid+File.separator;
		return path;
	}
	
	/**
	 * 插入文件记录
	 * @param a0100
	 * @param nbase
	 * @param fileid 文件加密id
	 * @throws GeneralException
	 */
	private void saveAttachment(String a0100,String nodeid ,String nbase, String fileid,String fileName,String linkid)
			throws GeneralException {
		String create_user = this.userview.getUserName();//登录名 
		String create_fullname = this.userview.getUserFullName();
        create_fullname = StringUtils.isEmpty(create_fullname) ? create_user : create_fullname;//用户全名为空则为登录名
        
        IDGenerator idg = new IDGenerator(2, this.conn);
        String id = idg.getId("zp_attachment.id");//参数从系统管理-应用管理-参数设置-序号维护中获取
        String guidkey = this.getGuidKey(dbName+"A01","",false);
        String guidDir = this.getGuidDir(guidkey);
		
		RecordVo vo = new RecordVo("zp_attachment");
		/**
		 * 处理文件重命名后文件名小于4的文件后缀名,避免保存为格式为ppt(1)类似的格式
		 */
		String ext = fileName.substring(fileName.lastIndexOf(".")+1);
		if(ext.indexOf("(")!=-1)
			ext = ext.substring(0, ext.indexOf("("));
		
		vo.setString("id", id);
		vo.setString("node_id", nodeid);
		vo.setString("guidkey", guidkey);
		vo.setString("path", fileid);
		vo.setString("file_name", fileName);
		vo.setString("file_name_old", fileName);
		vo.setString("ext", ext);
		vo.setDate("create_time", new java.sql.Date(new Date().getTime()));
		vo.setString("create_user", create_user);
		vo.setString("create_fullname", create_fullname);
		if(StringUtils.isNotEmpty(linkid))
			vo.setString("link_id", linkid);
			
		
		ContentDAO dao = new ContentDAO(this.conn);
		dao.addValueObject(vo);
	}
	
	/**
	 * 根据guid生成文件上传路径
	 * @param guid
	 * @return
	 */
	private String getGuidDir(String guid){
		StringBuffer dir = new StringBuffer();
		
		int iHash = Math.abs(guid.hashCode());
		dir.append("P").append(String.format("%04d", iHash/1000000%500));
		dir.append(File.separator);
		dir.append("P").append(String.format("%04d", iHash/1000%500));
		return dir.toString();
	}
	
	/**
	 * 当文件夹下已有名为name的文件，获取新文件名
	 * 将文件名按照name，name(1),name(2)…命名
	 * @Title: getFileName   
	 * @Description: 
	 * @param path 文件夹完整绝对路径
	 * @param name 文件名
	 * @return 
	 * @return filename 新文件名
	 */
	public String getFileName(String path,String name){
		File filedir = new File(path);
		String filename = name;
		if (filedir.exists()) {
			File[] filelist = filedir.listFiles();
			for (int i = 0; i < filelist.length+1; i++) {
				File file = new File(path+File.separator+filename);
				if(!file.exists())
					break;
				else
					filename = name.substring(0, name.lastIndexOf("."))+"("+(i+1)+")"+name.substring(name.lastIndexOf("."));
			}

		}
		return filename;
	}
	
	/**
	 * 删除简历子集附件
	 * 将保存信息和附件同时删掉
	 * @param deleteFileList
	 * @return
	 */
	public String deleteFile(ArrayList<String> deleteFileList) {
		String return_code = "success";
		RowSet rs = null;
		StringBuffer whesql = new StringBuffer();
		ArrayList<String> values = new ArrayList<String>();
		String selsql = "select path from hr_multimedia_file ";
		String delsql = "delete from hr_multimedia_file ";
		try {
			whesql.append(" where id in( ");
			for (String file_id : deleteFileList) {
				whesql.append("?,");
				values.add(PubFunc.decrypt(file_id));
			}
			whesql.setLength(whesql.length()-1);
			whesql.append(")");
			rs = dao.search(selsql+whesql.toString(),values);
			while(rs.next()) {
				String path = rs.getString("path");
				VfsService.deleteFile(userview.getUserName(), path);
			}
			dao.delete(delsql+whesql.toString(), values);
		} catch (Exception e) {
			//删除文件抛异常后将关联信息删掉
			try {
				dao.delete(delsql+whesql.toString(), values);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		} finally {
			PubFunc.closeResource(rs);
		}
		return return_code;
	}
	
	/**
	 * 校验应聘身份是否符合
	 * @param z0301
	 * @return
	 */
	public String checkApplyCode(String z0301) {
		String return_code = "success";
		RowSet search = null;
		try {
			//应聘身份指标
			String candidate_status = (String) this.userview.getHm().get("applyCode");
			if(StringUtils.isNotBlank(candidate_status)) {
			    //校验当前职位渠道是否包含应聘者应聘身份
			    StringBuffer sql = new StringBuffer();
			    sql.append("select 1 from Z03 ");
			    sql.append(" where Z0301=? ");
			    sql.append(" and (Z0336=? ");
			    FieldItem fieldItem = DataDictionary.getFieldItem("z0384", "z03");
			    if(fieldItem!=null && "1".equals(fieldItem.getUseflag())) {
			        if (Sql_switcher.searchDbServer() == Constant.MSSQL)
			            sql.append("  or ','+Z0384+',' like '%,"+candidate_status+",%'");
			        else
			            sql.append("  or ','||Z0384||',' like '%,"+candidate_status+",%'");
			    }
			    sql.append(")");
			    ArrayList<String> list = new ArrayList<String>();
			    list.add(z0301);
			    list.add(candidate_status);
			    search = dao.search(sql.toString(),list);
			    if(!search.next()) {
			        return_code = "applyCodeFail";
			    }
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "fail";
		}
		return return_code;
	}
	
	/**
	 * 所有子集全部检查是否有必填项未填
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String checkRequired() throws GeneralException {
		RowSet rs = null;
		String isResumePerfection="success";
		try {
			String a0100 = this.userview.getA0100();
			EmployNetPortalBo embo=new EmployNetPortalBo(this.conn);
			//应聘身份指标
			String candidateStatusid = (String) this.userview.getHm().get("applyCode");
			ArrayList<String> values = new ArrayList<String>();
			values.add(a0100);
			ParameterXMLBo parameterXMLBo = new ParameterXMLBo(this.conn);
			HashMap map = parameterXMLBo.getAttributeValues();
			//渠道参数信息
			ArrayList paramsInfo = embo.getSetByWorkExprience(candidateStatusid);
			ArrayList list = (ArrayList)paramsInfo.get(0);
			HashMap fieldMap=(HashMap)paramsInfo.get(1);
	        HashMap fieldSetMap=(HashMap)paramsInfo.get(2);
	        ArrayList<String> mustList = (ArrayList)paramsInfo.get(4);
			StringBuffer whl=new StringBuffer("");
			String setstr = "1";
	        /**所有子集全部检查是否有必填项未填*/
	        for(int i=0;i<list.size();i++)
	        {
	        	setstr = "1";
	            LazyDynaBean bean = (LazyDynaBean)list.get(i);
	            String key=(String)bean.get("fieldSetId");
	            HashMap fieldExtendMap=(HashMap)fieldSetMap.get(key.toLowerCase());
	            ArrayList fieldList=(ArrayList)fieldMap.get(key.toUpperCase()) == null?(ArrayList)fieldMap.get(key.toLowerCase()):(ArrayList)fieldMap.get(key.toUpperCase());
	            
	            whl.setLength(0);
	            for(Iterator t=fieldList.iterator();t.hasNext();)
	            {
	                String itemid=(String)t.next();
	                if(StringUtils.isNotEmpty(itemid))
	                	itemid = itemid.split("#")[0];
	                FieldItem fieldItem = DataDictionary.getFieldItem(itemid, key);
                    if(fieldItem == null || !"1".equals(fieldItem.getUseflag()))
    					continue;
	                String temp=(String)fieldExtendMap.get(itemid.toLowerCase());
	                if(temp==null)
	                    temp=(String)fieldExtendMap.get(itemid.toUpperCase());
	                String[] temps=temp.split("#");
	                if("1".equals(temps[1])) {
	                	whl.append(" or "+itemid+" is null ");
	                	if("A".equalsIgnoreCase(fieldItem.getItemtype()))
	                		whl.append(" or "+itemid+"='' ");
	                }
	                
	            }
	            for (String setId : mustList){
	            	if(key.equalsIgnoreCase(setId))
	            		setstr = "2";
	            }
	            
	            if(whl.length()>0)
	            {
	                
					rs=dao.search("select * from "+dbName+key+" where a0100='"+a0100+"' and ( "+whl.substring(3)+" )");
					
	                if(rs.next())
	                    isResumePerfection ="0";
	                
	            }
                rs=dao.search("select * from "+dbName+key+" where a0100='"+a0100+"'");
	    		boolean flag=true;
	    		if(rs.next())
	    			flag=false;
	    		
	    		if("1".equals(setstr))//当子集为可选，指标为必选时，根据子集进行确定当前子集无记录可通过验证
	    			flag=false;
	    		
	    		if(flag&&"2".equals(setstr))
	    		{
	    			isResumePerfection = PubFunc.encrypt(key)+"-"+bean.get("fieldSetDesc")+"必须填写，";
	    			break;
	    		} else if(!flag&&"0".equals(isResumePerfection)){
	    			isResumePerfection = PubFunc.encrypt(key)+"-"+bean.get("fieldSetDesc")+"有必填指标未填写，";
	    			break;
	    		}
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return isResumePerfection;
	}
	
	/**
	 * 修改应聘身份指标
	 * @param itemid
	 * @param value
	 * @return
	 */
	public String changeCandidate(String itemid,String value) {
		String return_code = "success";
		String tableName = this.dbName.toLowerCase() + "a01";
    	RecordVo vo = new RecordVo(tableName);
    	RowSet rs = null; 
    	try {
			String a0100 = this.userview.getA0100();
			vo.setString("a0100", a0100);
			vo.setString(itemid.toLowerCase(), value);
			dao.updateValueObject(vo);
    	} catch(Exception e) {
    		e.printStackTrace();
    		return "fail";
    	}
		return return_code;
	}
	
	
	/**
	 * 删除简历附件
	 * 将保存信息和附件同时删掉
	 * @param deleteFileList
	 * @return
	 * @throws GeneralException 
	 */
	public String deleteOthFile(ArrayList<String> deleteFileList) throws GeneralException {
		String return_code = "success";
		RowSet rs = null;
		StringBuffer whesql = new StringBuffer();
		ArrayList<String> values = new ArrayList<String>();
		String selsql = "select path,file_name,file_name_old from zp_attachment ";
		String delsql = "delete from zp_attachment ";
		try {
			whesql.append(" where id in( ");
			for (String file_id : deleteFileList) {
				whesql.append("?,");
				values.add(PubFunc.decrypt(file_id));
			}
			whesql.setLength(whesql.length()-1);
			whesql.append(")");
			rs = dao.search(selsql+whesql.toString(),values);
			while(rs.next()) {
				String path = rs.getString("path");
				VfsService.deleteFile(userview.getUserName(), path);
			}
			dao.delete(delsql+whesql.toString(), values);
		} catch (Exception e) {
			//删除文件抛异常后将关联信息删掉
			try {
				dao.delete(delsql+whesql.toString(), values);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
		} finally {
			PubFunc.closeResource(rs);
		}
		return return_code;
	}
	
	/**
	    * 获取代码层级
	     * @param codesetid
	     * @return
	     */
	    public String getCodeSetLayer(String codeSetid) {
	       RowSet rs = null;
	       String layer = "0";
	       try {
	       		//单位和部门返回层级只为了多层显示，所以直接返回2
				if("UN".equalsIgnoreCase(codeSetid)||"UM".equalsIgnoreCase(codeSetid)){
					return "2";
				}
	           String sql = "select 1 from codeitem where codesetid = ?";
	           String codesql = "select MAX(layer) as layer from codeitem where codesetid = ? and invalid=1";//只查询有效的等级
	           ArrayList codeSetId = new ArrayList();
	           codeSetId.add(codeSetid);
	           rs = dao.search(sql,codeSetId);
	           if(rs.next()) {
	               sql = "select 1 from codeitem where layer is null and codesetid = ?";
	               rs = dao.search(sql, codeSetId);
	               if(rs.next()) //如果代码类里有layer为null的，重置代码层级
	                   this.updateLayer(codeSetid);
	               
	               rs = dao.search(codesql, codeSetId);
	               if(rs.next()){
	                   String temp = rs.getString("layer");
	                   if(temp != null)
	                       layer = temp;
	                   
	               }
	           }
	       } catch (Exception e) {
	           e.printStackTrace();
	       } finally {
	           PubFunc.closeResource(rs);
	       }
	       return layer;
	   }
	   
	   /**
	    * 重置代码层级
	     * @param codesetid
	     */
	    public void updateLayer(String codeSetid) {
	           try{
	               String sql = " update codeitem set layer=null where codesetid='"+codeSetid+"'";
	                dao.update(sql);
	                sql = " update codeitem set layer = 1 where codesetid='"+codeSetid+"' and parentid=codeitemid";
	                dao.update(sql);
	                sql = " update codeitem set layer=(select layer from codeitem c1 where c1.codesetid='"+codeSetid+"' and c1.codeitemid=codeitem.parentid)+1 where codesetid='"+codeSetid+"' and layer is null ";
	                int i=1;
	                while(i>0){
	                    i = dao.update(sql);
	                }
	           }catch(Exception e){
	               e.printStackTrace();
	           }
	      }
	    
		/**
		 * 校验密码是否一致
		 * @param loginName
		 * @param password
		 * @return
		 */
		public ArrayList<String> pwValidate(String loginName, String password) {
		    Des des = new Des();
            String return_code = "error_pw";
            ArrayList<String> list = new ArrayList<String>();
            list.add(return_code);
            RowSet rs = null;
            try {
                String pwSql = "select a0100,userpassword,state from "+dbName+"A01 where username=?";
                ArrayList<String> pwSqlValues = new ArrayList<String>();
                pwSqlValues.add(loginName);
                rs = dao.search(pwSql, pwSqlValues);
                if(rs.next()) {
                    String userpassword = rs.getString("userpassword");
                    String state = rs.getString("state");
                    RecordVo encryVo = ConstantParamter.getRealConstantVo("EncryPwd",this.conn);
                    String pwDesFlag = "0";//0:帐号密码不加密
                    if(encryVo!=null) {
                        pwDesFlag = encryVo.getString("str_value");
                    }
                    String realPassWord = "";
                    if("1".equals(pwDesFlag)) {
                    	realPassWord = des.DecryPwdStr(userpassword);
                    }
                    if(StringUtils.isNotEmpty(userpassword)) {
                    	//兼容系统设置的认证用户名密码指标跟招聘库密码指标不一致的情况
                        if((userpassword).equals(password)||(realPassWord).equals(password)) {
                            return_code = "success";
                            list.add(rs.getString("a0100"));
                            list.add(rs.getString("userpassword"));
                            ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn,"1");
                			HashMap map=xmlBo.getAttributeValues();
                			if(map!=null && map.get("acountBeActived")!=null&&((String)map.get("acountBeActived")).length()>0){
                                String acountBeActived=(String)map.get("acountBeActived");
                                //未启用激活校验不判断
                            	if("1".equals(acountBeActived)&&state!=null&&!"1".equals(state)) {
                            		return_code = "not_active";
                            	}
                            }
                            list.set(0, return_code);
                        }
                    }
                }else {
                    list.set(0, "error_account");
                }
            } catch (Exception e) {
                e.printStackTrace();
                list.set(0, "fail");
                return list;
            }
            return list;
		}
		/**
		 * 登录校验
		 * @param loginName
		 * @param password
		 * @param application
		 * @return
		 */
		public ArrayList<String> loginValidate(String loginName, String password,ServletContext application) {
			String return_code = "error_pw";
			ArrayList<String> list = new ArrayList<String>();
			list.add(return_code);
			RowSet rs = null;
			try {
			    list = pwValidate(loginName,password);//校验密码
			    return_code = list.get(0);
			    if("error_account".equals(return_code)) {
			        return_code="用户名或密码错误！";
			        list.set(0, return_code);
			        return list;
			    }
			    
			    ParameterXMLBo xmlBo=new ParameterXMLBo(this.conn,"1");
		        HashMap map=xmlBo.getAttributeValues();
		        String failedTime="3";//最大登录失败次数
		        if(map.get("failedTime")!=null&&((String)map.get("failedTime")).length()>0) {
		            failedTime=(String)map.get("failedTime");
		        }
		        String unlockTime="60";//解锁时间间隔
		        if(map.get("unlockTime")!=null&&((String)map.get("unlockTime")).length()>0) {
		            unlockTime=(String)map.get("unlockTime");
		        }
                Calendar calendar = Calendar.getInstance();
                
                boolean haveData = false;//默认没有记录
                int i=0;//连续输错几次，初始化0
                if(!"not_active".equals(return_code)&&Integer.parseInt(failedTime)>0){
                    ArrayList<String> sqlValues = new ArrayList<String>();
                    String sql = "select a0100,a0101,state from "+dbName+"A01 where lower(username)=lower(?) and userpassword=?";
                    sqlValues.add(loginName);
                    if("success".equals(return_code)) {
                        sqlValues.add(list.get(2));
                    }else {
                        sqlValues.add(password);
                    }
                    rs = dao.search(sql, sqlValues);
                    haveData = rs.next();
                    
                    String value = (String) application.getAttribute(loginName);//获得用户的登入失败信息
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //超过解锁时间间隔 清除信息
                    if(value!=null&&!"".equals(value)){
                        String[] str=value.split("`");
                        Date date1=sdf.parse(str[1]);//账号锁定时间或最后一次输错的时间
                        Date date2=sdf.parse(sdf.format(calendar.getTime()));
                        long intervalMinute = date2.getTime() - date1.getTime();
                        int j=(int) (intervalMinute / (60*1000));
                        if(j>=Integer.parseInt(unlockTime)){
                            application.removeAttribute(loginName);
                        }
                    }
                    //密码输入错误
                    if("error_pw".equals(return_code)&&!haveData){
                        value=(String) application.getAttribute(loginName);//在此重新赋值 是因为超过时间间隔会清空信息
                        if(value==null||value==""){
                            application.setAttribute(loginName, 1+"`"+sdf.format(calendar.getTime()));
                            return_code="用户名或密码输入错误,您已经输错"+(i+1)+"次,连续输错"+failedTime+"次后账号将被锁定!";
                            list.set(0, return_code);
                        }else{
                            String[] str=value.split("`");
                            if(Integer.parseInt(str[0])<Integer.parseInt(failedTime)){
                                i=Integer.parseInt(str[0])+1;
                                str[1]=sdf.format(calendar.getTime());
                                application.setAttribute(loginName, i+"`"+str[1]);
                                if(i==Integer.parseInt(failedTime)){
                                    lockValidate(failedTime,sdf,str,calendar,unlockTime,return_code,list);
                                }else{
                                    return_code="用户名或密码输入错误,您已经输错"+i+"次，连续输错"+failedTime+"次后账号将被锁定!";
                                    list.set(0, return_code);
                                }
                            }else{
                                lockValidate(failedTime,sdf,str,calendar,unlockTime,return_code,list);
                            }
                        }
                    }else if("success".equals(return_code)&&haveData){//密码输入正确
                        value=(String) application.getAttribute(loginName);
                        if(value==null||value==""){
                            
                        }else{
                            String[] str=value.split("`");
                            if(Integer.parseInt(str[0])<Integer.parseInt(failedTime)){//在限制次数之内  密码输入正确  移除错误信息
                                application.removeAttribute(loginName);
                            }else{//在限制次数之外  密码输入正确  也将提示错误信息  不能进入系统
                                lockValidate(failedTime,sdf,str,calendar,unlockTime,return_code,list);
                            }
                        }
                    }
                }
            } catch (Exception e) {
				e.printStackTrace();
				list.set(0, "fail");
				return list;
			}
			return list;
		}
		/**
		 * 锁定校验
		 * @param failedTime
		 * @param sdf
		 * @param str
		 * @param calendar
		 * @param unlockTime
		 * @param return_code
		 * @param list
		 */
        private void lockValidate(String failedTime,SimpleDateFormat sdf,String[] str,Calendar calendar,
                String unlockTime,String return_code,ArrayList list) {
            try {
                Date date1 = sdf.parse(str[1]);//账号锁定时间或最后一次输错的时间
                Date date2=sdf.parse(sdf.format(calendar.getTime()));
                long intervalMinute = date2.getTime() - date1.getTime();
                int j=(int) (intervalMinute / (60*1000));//现在距离锁定过了多少分钟
                int k=Integer.parseInt(unlockTime)-j;//还剩多少分钟解锁
                if(k>0){
                    return_code="用户名或密码已连续输错"+failedTime+"次,账号已被锁定,请"+k+"分钟后再试!";
                    list.set(0, return_code);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
		 * md5加密
		 * @param str
		 * @return
		 */
		private String getMD5(String str){
			
			MessageDigest messageDigest = null;
			try {
				messageDigest = MessageDigest.getInstance("MD5");
				messageDigest.reset();
				messageDigest.update(str.getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte[] byteArray = messageDigest.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
			return md5StrBuff.toString();
		}
		
		/**
		 * 获取个人最新文件id
		 * @param table 表名
		 * @return
		 */
		public Map getFileId(String table) {
			RowSet rs = null;
			String guidkey = "";
			String temp = ""; 
			String filename="";
			String encrypt_file_name ="";
			Map<String,String> param = new HashMap<String,String>();
            ArrayList<String> values = new ArrayList<String>();
			if("hr_multimedia_file".equals(table)) {
			    values.add(dbName);
			    guidkey = "mainguid";
			    temp = "nbase";
			    filename = "filename";
			}else if("zp_attachment".equals(table)) {
			    values.add("00");
			    guidkey = "guidkey";
			    temp = "node_id";
			    filename = "file_name";
			}
			values.add(this.userview.getA0100());
			String fileId = "";
			try {
				StringBuffer sql = new StringBuffer();
				sql.append("select id,");
				sql.append(filename);
				sql.append(" from ");
				sql.append(table).append(" where id=(");
				sql.append("select MAX(id)");
				sql.append(" from ");
				sql.append(table);
				sql.append(" where ");
				sql.append(temp);
				sql.append("=?");
				sql.append(" and ");
				sql.append(guidkey);
				sql.append("=(select GUIDKEY from ");
				sql.append(dbName);
				sql.append("A01 where A0100=?))");
				rs = dao.search(sql.toString(), values);
				if(rs.next()) {
				    fileId = rs.getString(1);
				    encrypt_file_name = PubFunc.encrypt(rs.getString(filename));
				    param.put("fileId",fileId);
				    param.put("encrypt_file_name",encrypt_file_name);
				    
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeResource(rs);
			}
			return param;
		}
		
		private String getParentCodes(String codeSetId, String codeItemId) {
			String parents = "";
			
			ArrayList<String> codes = new ArrayList<String>();
			CodeItem item = AdminCode.getCode(codeSetId, codeItemId);
			if(item != null)
			  codes.add(codeItemId);
			
			while(item!=null && !item.getCodeitem().equals(item.getPcodeitem())) {
				item = AdminCode.getCode(codeSetId, item.getPcodeitem());
				if(item != null)
					codes.add(item.getCodeitem());				
			}
			
			for(int i=codes.size()-1; i>=0; i--) {
				if (!"".equals(parents)) {
					parents += ",";
				}
				parents += "'" + codes.get(i) + "'";
			}
			
			if(!"".equals(parents))
			    return  "[" + parents + "]";
			else
				return "[]";
			
		}
		
		/**
		 * 代码是否只能选最底层 0：否
		 * @param codesetid
		 * @return
		 */
		private String getSelectFlag(String codesetid) {
	    	String leaf_only = "0";
	    	RowSet rs = null;
	    	try {
	    		ArrayList valuelist = new ArrayList();
	    		valuelist.add(codesetid);
				String sql =" select leaf_node from codeset where codesetid = ?";
				rs = dao.search(sql,valuelist);
				if(rs.next())
					leaf_only = rs.getInt("leaf_node")+"";
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				PubFunc.closeDbObj(rs);
			}
			return leaf_only;
		}
		  public Map downloadSetFile(String mediaid) {
		        String return_code = "success";
		        mediaid = PubFunc.decrypt(mediaid);
		        MultiMediaBo multiMediaBo = new MultiMediaBo(this.conn,this.userview);             
		        String filename = "";
		        Map map = new HashMap();
		        try {
		            filename = multiMediaBo.downloadFile(mediaid);
		            File file = new File(filename);
	                if (!file.exists()) {
	                    throw new GeneralException("未找到文件(" + filename + ")!");
	                }
	                String srcfilename =multiMediaBo.getDestFileName();
	                //【8581】员工管理-信息维护-记录录入-点击附件（下载，空白页面） jingq upd 2015.04.08
	                filename = SafeCode.encode(PubFunc.encryption(filename));
	                srcfilename = SafeCode.encode(srcfilename);
	                map.put("filename", filename);
	                map.put("srcfilename", srcfilename);
	                map.put("return_code", "success");
		        } catch (GeneralException e) {
		            e.printStackTrace();
		        }
		        return map;
		    }
		  /**
			 * 获取应聘身份
			 * @param candidate_status_itemId 应聘身份指标
			 * @param a0100 
			 * @return
			 */
		  public String getApplyCode(String a0100) {
			  EmployNetPortalBo bo = new EmployNetPortalBo(this.conn);
			  HashMap status = this.getCandidateStatus();
			  String candidate_status_itemId = (String) status.get("itemid");
			  String applyCode = bo.getCandidateStatus(candidate_status_itemId , a0100);
			  return applyCode;
		  }
		  /**
         * 检验所有子集的必填，用于新招聘子集列表完成情况的判断
         */
        public Map checkMust() {
            Map needMustMap = new HashMap();//返回给前台的子集状态list
            RowSet rs = null;
            String isResumePerfection="1";//表示子集记录中必填项已完成
            try {
                String a0100 = this.userview.getA0100();
                EmployNetPortalBo embo=new EmployNetPortalBo(this.conn);
                //应聘身份指标
                String candidateStatusid = (String) this.userview.getHm().get("applyCode");
                //渠道参数信息
                ArrayList paramsInfo = embo.getSetByWorkExprience(candidateStatusid);
                if(paramsInfo==null)
                	return needMustMap;
                //[{"fieldSetId":"A01","fieldSetDesc":"基本信息"},...]
                ArrayList showSetList = (ArrayList)paramsInfo.get(0);
                //{{"A01":[A0101,A0111#出生日期,A01AX#最高学历毕业日期]},...}
                HashMap fieldMap=(HashMap)paramsInfo.get(1);
                //{"A01":{"A0101":"0#1","A0111":"0#1",...},...}
                HashMap fieldSetMap=(HashMap)paramsInfo.get(2);
                //["A01","A04"]
                ArrayList<String> mustList = (ArrayList)paramsInfo.get(4);
                StringBuffer sqlConditions = new StringBuffer("");//sql语句拼接条件
                String mustSet = "1";//子集是否必填 1：非必填  2：必填
                /**所有子集全部检查是否有必填项未填*/
                for(int i=0;i<showSetList.size();i++){
                    mustSet = "1";
                    isResumePerfection = "1";
                    LazyDynaBean bean = (LazyDynaBean)showSetList.get(i);//{"fieldSetId":"A01","fieldSetDesc":"基本信息"}
                    String key=(String)bean.get("fieldSetId");
                    HashMap fieldExtendMap=(HashMap)fieldSetMap.get(key.toLowerCase());//{"A0101":"0#1","A0111":"0#1",...}
                    
                    //[A0101,A0111#出生日期,A01AX#最高学历毕业日期]
                    ArrayList fieldList=(ArrayList)fieldMap.get(key.toUpperCase()) == null?(ArrayList)fieldMap.get(key.toLowerCase()):(ArrayList)fieldMap.get(key.toUpperCase());
                    
                    sqlConditions.setLength(0);
                    for(Iterator t=fieldList.iterator();t.hasNext();){
                        String itemid=(String)t.next();
                        if(StringUtils.isNotEmpty(itemid)) {
                            itemid = itemid.split("#")[0];//A01AX#最高学历毕业日期  取#前面的itemid
                            FieldItem fieldItem = DataDictionary.getFieldItem(itemid, key);
                            if(fieldItem == null || !"1".equals(fieldItem.getUseflag()))
            					continue;
                            String temp=(String)fieldExtendMap.get(itemid.toLowerCase());//"0#1"表示指标是否必填
                            if(temp==null) {
                                temp=(String)fieldExtendMap.get(itemid.toUpperCase());
                            }
                            String[] temps=temp.split("#");
                            if("1".equals(temps[1])) {//temps[1]==1:指标必填
                                sqlConditions.append(" or "+itemid+" is null ");
                            }
                        }
                    }
                    for (String setId : mustList){
                        if(key.equalsIgnoreCase(setId)) {
                            mustSet = "2";
                            break;
                        }
                    }
                    if(sqlConditions.length()>0){//表示该子集有必填的指标  故去库中查询是否有未填的必填指标
                        rs=dao.search("select * from "+dbName+key+" where a0100='"+a0100+"' and ( "+sqlConditions.substring(3)+" )");
                        if(rs.next()) {
                            isResumePerfection ="0";
                        }
                    }
                    rs=dao.search("select count(*) from "+dbName+key+" where a0100='"+a0100+"'");
                    boolean haveData=false;//代表当前子集是否有记录,默认false:没有记录
                    if(rs.next()) {//如果查出来  代表是有子集有记录
                        int recordCount = rs.getInt(1);
                        if(recordCount>0) {
                            haveData=true;
                        }
                    }
                    boolean isMustSet = true;//当前子集是否是必填
                    if("1".equals(mustSet)) {//当子集为可选，指标为必选时，根据子集进行确定当前子集无记录可通过验证
                        isMustSet=false;
                    }
                    if(!isMustSet&&!haveData) {//无记录  子集非必填不存入needMustMap（空白状态）只有子集有记录且记录中必填指标均已填写，才通过验证（绿色对号）
                    }else if(haveData&&"1".equals(isResumePerfection)) {
                        needMustMap.put(key, "1");
                    }else {
                        needMustMap.put(key, "0");
                    }
//                    if((!haveData&&isMustSet)||(haveData&&"0".equals(isResumePerfection))){//有必填子集没有记录  或者   有记录但是有未填的必填指标
//                        needMustMap.put(key, "0");
//                    }else if(isMustSet&&haveData&&"1".equals(isResumePerfection)) {//必填子集 有记录  必填都已完成
//                        needMustMap.put(key, "1");
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                PubFunc.closeResource(rs);
            }
            return needMustMap;
        }
        /*
         * 是否显示指标解释
         */
        public boolean isVisibleExplaination() {
            ParameterXMLBo bo=new ParameterXMLBo(this.conn);
            boolean isVisible = false;
            try {
                HashMap map=bo.getAttributeValues();
                if(map.get("explaination")!=null&&((String)map.get("explaination")).length()>0) {
                   String explaination=(String)map.get("explaination");
                   if("1".equals(explaination)) {
                       isVisible = true;
                   }
                }
            } catch (GeneralException e) {
                e.printStackTrace();
            }
            return isVisible;
        }
        /**
         * 注册时邮箱框失焦  校验邮箱是否存在
         * @param intputEmail
         */
        public String checkEmail(String inputEmail) {
            String return_code = "success";
            RowSet rs = null;
            try {
                ArrayList<String> value = new ArrayList<String>();
                value.add(inputEmail);
                rs=dao.search("select * from "+dbName+"A01 where userName=?",value);
                if(rs.next()) {
                    return_code = "emailRepeat";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "fail";
            } finally {
                PubFunc.closeResource(rs);
            }
            return return_code;
        }
        /**
         * 更新帐号状态
         */
        public HashMap refreshState(String ativeid) {
            RowSet rs = null;
            String username="";
            String password="";
            String state="";
            String return_code = "success";
            HashMap returnMap = new HashMap();
            try {
                ArrayList<String> value = new ArrayList<String>();
                value.add(ativeid);
                rs=dao.search("select username,userpassword,state from "+dbName+"A01 where a0100=?", value);
                while(rs.next()){
                    username = rs.getString("username");
                    password = rs.getString("userpassword");
                    state = rs.getString("state");
                }
                if("1".equals(state)){
                    return_code = "actived";
                }else {
                    dao.update("update "+dbName+"A01 set state='1' where a0100=?", value);
                }
                returnMap.put("return_code", return_code);
                returnMap.put("username", username);
                returnMap.put("password", password);
            } catch (Exception e) {
                e.printStackTrace();
                return_code = "fail";
                return returnMap;
            } finally {
                PubFunc.closeResource(rs);
            }
            return returnMap;
        }
        /**
         * 获取简历指标集合中的代码类指标
         * @return
         */
        public Map getCodeData(String hireChannel) {
            EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
            ArrayList paramsInfo = bo.getSetByWorkExprience(hireChannel);
            HashMap<String, Object> field_list = new HashMap<String, Object>();
            ArrayList<LazyDynaBean> setlist = (ArrayList) paramsInfo.get(0);
            LinkedHashMap fieldMap = (LinkedHashMap) paramsInfo.get(1);
            //存放指标是否必填参数
            HashMap<String, HashMap> fieldSetMap=(HashMap)paramsInfo.get(2);
            Map<String,Map<String,List>> codeData = new HashMap<String,Map<String,List>>();
            for(int i = 0;i<setlist.size();i++) {
                LazyDynaBean obj = setlist.get(i);
                String fieldSetId = (String) obj.get("fieldSetId");
                ArrayList<String> fielditemList = (ArrayList) fieldMap.get(fieldSetId);
                HashMap fieldSet = fieldSetMap.get(fieldSetId.toLowerCase());
                for (String itemId : fielditemList) {
                    itemId = itemId.split("#")[0];
                    FieldItem fieldItem = DataDictionary.getFieldItem(itemId, fieldSetId);
    				if(fieldItem == null || !"1".equals(fieldItem.getUseflag())) {
    					System.out.println("有简历指标不存在，请重新保存应聘简历指标参数！");
    					continue;
    				}
                    String codesetid = fieldItem.getCodesetid();
                    //是否只能选末级 0：否
                    if(codesetid!=null&&!"0".equals(codesetid)) {
                        if(!"UN".equalsIgnoreCase(codesetid) && !"UM".equalsIgnoreCase(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
                            ArrayList treeItems = fastGetCodeItems(codesetid);
                            Map<String,List> codeChildren = new HashMap<String,List>();
                            codeChildren.put("children",treeItems);
                            codeData.put(codesetid,codeChildren);
                        }
                    }
                }
            }
            return codeData;
        }
        /**
         * 极速加载代码模式
         * @param codeSetId
         * @return
         */
        private ArrayList fastGetCodeItems(String codeSetId) {
            ArrayList nodes = new ArrayList();
            
            ArrayList codeList = AdminCode.getCodeItemList(codeSetId);
            boolean isRecHistoryCode = AdminCode.isRecHistoryCode(codeSetId);
            //使用A0000进行排序
            Collections.sort(codeList, new Comparator<CodeItem>() {
                @Override
                public int compare(CodeItem o1, CodeItem o2) {
                    int diff = o1.getA0000() - o2.getA0000();
                    if (diff > 0) {
                        return 1;
                    }else if (diff < 0) {
                        return -1;
                    }
                    return 0; //相等为0
                }
            });
            for (int i=0; i<codeList.size(); i++) {
                CodeItem code = (CodeItem)codeList.get(i);
                
                // 寻找根节点
                if (!code.getCodeitem().equalsIgnoreCase(code.getPcodeitem())) 
                    continue;
                boolean isInvalid = validCode(isRecHistoryCode, code);
                if(!isInvalid) {
                    continue;
                }
                HashMap treeitem = new HashMap();
                String itemid = code.getCodeitem().trim();
                treeitem.put("value", itemid);
                treeitem.put("label", code.getCodename());
                treeitem.put("leaf", Boolean.TRUE);
                
                // 递归加载子节点
                ArrayList childNodes = getChildCode(codeList, code.getCodeitem(),isRecHistoryCode);
                if (childNodes != null && childNodes.size()>0) {
                    treeitem.put("loading", Boolean.FALSE);
                    treeitem.put("leaf", Boolean.FALSE);
                    treeitem.put("children", childNodes);
                }
            
                nodes.add(treeitem);    
            }
            
            return nodes;
        }
        /**
         * 获取孩子结点的code
         * @param codeList 当前代码类所有的代码项
         * @param parentId 父节点
         * @param isRecHistoryCode 是否是记录历史代码类
         * @return 孩子节点集合
         */
        private ArrayList getChildCode(ArrayList codeList, String parentId, boolean isRecHistoryCode) {
            ArrayList nodes = new ArrayList();
            
            for (int i=0; i<codeList.size(); i++) {
                CodeItem item = (CodeItem)codeList.get(i);
                // 根节点跳过
                if (item.getCodeitem().equalsIgnoreCase(parentId)) 
                    continue;
                // 非parentId的子节点跳过
                if (!item.getPcodeitem().equalsIgnoreCase(parentId)) 
                    continue;
                boolean isInvalid = validCode(isRecHistoryCode, item);
                if(!isInvalid) {
                    continue;
                }
                HashMap treeitem = new HashMap();
                String itemid = item.getCodeitem().trim();
                treeitem.put("value", itemid);
                treeitem.put("label", item.getCodename());
                treeitem.put("leaf", Boolean.TRUE);
//                ArrayList childNodes = getChildCode(codeList, item.getCodeitem());
//                if (childNodes != null && childNodes.size()>0) {
//                    treeitem.put("leaf", Boolean.FALSE);
//                    treeitem.put("loading", Boolean.FALSE);
//                    treeitem.put("children", childNodes);
//                }
                
                if (isHaveLeaf(codeList, item.getCodeitem(),isRecHistoryCode)) {
                    treeitem.put("leaf", Boolean.FALSE);
                    treeitem.put("loading", Boolean.FALSE);
                    treeitem.put("children", new ArrayList());
                }
                
                nodes.add(treeitem);
            }
            
            return nodes;
        }
        /**
         * 当前节点是否还有子节点
         * @param codeList 
         * @param parentId 当前节点
         * @param isRecHistoryCode 是否记录历史
         * @return true 有子节点  false 没有子节点
         */
        private boolean isHaveLeaf(ArrayList codeList, String parentId, boolean isRecHistoryCode) {
            int count = 0;
            boolean isHaveleaf = false;//默认没有子节点
            for (int i=0; i<codeList.size(); i++) {
                CodeItem item = (CodeItem)codeList.get(i);
                // 根节点跳过
                if (item.getCodeitem().equalsIgnoreCase(parentId)) 
                    continue;
                // 非parentId的子节点跳过
                if (!item.getPcodeitem().equalsIgnoreCase(parentId)) 
                    continue;
                boolean isInvalid = validCode(isRecHistoryCode, item);
                if(!isInvalid) {
                    continue;
                }
                count ++;
            }
            if(count>0) {
                isHaveleaf = true;
            }
            return isHaveleaf;
        }
        /**
         * 是否配置了上传简历附件
         * @return 1配置 0未配置
         */
        public String getResumeFileState() {
            String state = "";
            try {
                ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
                HashMap map = parameterXMLBo.getAttributeValues();
                if(map != null && map.get("attach") != null)
                    state = (String) map.get("attach");
                
                state = state == null? "0" : state;
            } catch (GeneralException e) {
                e.printStackTrace();
            }
            
            return state;
        }
        /**
         * 判定代码指标是否有效
         * @param isRecHistoryCode 是否记录历史记录
         * @param code 代码值
         * @return isInvalid 指标是否有效
         */
        private boolean validCode(boolean isRecHistoryCode,CodeItem code) {
            boolean isInvalid = true;//默认代码有效
          //进行代码过滤，去掉无效的代码
            if(!isRecHistoryCode) {//如果不是记录历史的代码类,直接根据invalid
                int invalid = code.getInvalid();
                if(invalid==0) {//等于0是无效代码
                    isInvalid = false;
                }
            }else {
                Date endDate = code.getEndDate();//结束日期
                Date startDate = code.getStartDate();//开始日期
                Date now = new Date();
                if(now.getTime()>endDate.getTime()||now.getTime()<startDate.getTime()) {//大于结束日期小于开始日期
                    isInvalid = false;
                }
            }
            return isInvalid;
        }
        /**
         * 对已上传文件进行排序
         * @param list
         * @param uploadFileList
         * @return
         */
        public ArrayList sortFileList(ArrayList<HashMap> list, ArrayList<LazyDynaBean> uploadFileList) {
            //已修改的附件分类
            ArrayList<LazyDynaBean> fileList = new ArrayList<LazyDynaBean>();
            //当前附件附件分类
            ArrayList<LazyDynaBean> fileCodeset = new ArrayList<LazyDynaBean>();
            String itemDesc = "";
            String fileName = "";
            String itemId = "";
            for (HashMap codeMap : list) {
                itemDesc = (String) codeMap.get("itemDesc");
                itemId = (String) codeMap.get("itemId");
                LazyDynaBean bean = null;
                for(int i = uploadFileList.size()-1; i>=0; i--) {
                    bean = uploadFileList.get(i);
                    fileName = (String) bean.get("file_name");
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    if(itemDesc.equalsIgnoreCase(fileName)) {
                        fileCodeset.add(bean);
                        uploadFileList.remove(i);
                    }
                }
            }
            fileCodeset.addAll(fileList);
            fileCodeset.addAll(uploadFileList);
            return fileCodeset;
        }
        /**
         * 保存简历分类附件
         * @return
         */
        @Deprecated
        public ArrayList<String> uploadAttachCodeSetFiles(ArrayList params) {
            ArrayList<String> fail_name = new ArrayList<String>();
            try {
                String return_code = "success";
                ParameterXMLBo xmlBo = new ParameterXMLBo(this.conn,"1");
                HashMap xmlMap = xmlBo.getAttributeValues();
                String candidateStatusid = (String) this.userview.getHm().get("applyCode");
                EmployNetPortalBo bo=new EmployNetPortalBo(this.conn);
                ArrayList attachCodeSet =  bo.getAttachCodeset(xmlMap, candidateStatusid);
               
                String a0100 = this.userview.getA0100();
                for(int i=0; i<params.size(); i++){
                    MorphDynaBean bean = (MorphDynaBean) params.get(i);
                    HashMap<String,String> file = PubFunc.DynaBean2Map(bean);
                    String filename = file.get("filename");
                    String filetype = file.get("filetype");
                    String file_base64 = file.get("file");
                    String attachCodeItemid = file.get("attachCodeItemid");
                    for(int j=0;j<attachCodeSet.size();j++) {
                        HashMap tempHashMap = (HashMap) attachCodeSet.get(j);
                        if(tempHashMap.get("itemId").equals(attachCodeItemid)) {
                            filename = (String)tempHashMap.get("itemDesc");
                        }
                    }
                    checkFileName(this.getPath(), filename, a0100, this.dbName);
                    filename += "."+filetype;
                    file_base64 = file_base64.substring(file_base64.indexOf(",")+1);
                    return_code = this.uploadOthFiles(filename, file_base64);
                    if("fail".equals(return_code)) {
                        fail_name.add(filename);
                    }
                }
            } catch (Exception e ) {
                e.printStackTrace();
            } 
            //保存上传失败得文件名
            return fail_name;
        }
        /**
         * 当文件夹下已有名为name的文件则删掉原来的
         * @Title: getFileName   
         * @Description: 
         * @param path 文件夹完整绝对路径
         * @param name 文件名
         * @param a0100 
         * @param nbase 
         * @return 
         * @return String 新文件名
         * @throws SQLException 
         */
        public void checkFileName(String path,String name, String a0100, String nbase) throws SQLException{
        	String guidkey = getGuidKey(dbName+"A01", "", false);
        	//文件扩展标识 可以通过扩展标识拿到这个人的所有简历附件
    		String fileTag = "zp_"+guidkey;
    		try {
    			StringBuffer sql = new StringBuffer();
    			ArrayList value = new ArrayList();
				List<VfsFileEntity> fileEntityGroup = VfsService.getFileEntityGroup(fileTag, VfsModulesEnum.ZP);
				//附件分类中的文件名相同则删掉原来的文件
				for (VfsFileEntity vfsFileEntity : fileEntityGroup) {
					if(StringUtils.isNotEmpty(name)&&name.equals(vfsFileEntity.getName())) {
						String fileid = vfsFileEntity.getFileid();
						VfsService.deleteFile(userview.getUserName(), vfsFileEntity.getFileid());
	                    value.clear();
	                    sql.setLength(0);
	                    sql.append("delete from zp_attachment ");
	                    sql.append(" where guidkey=? ");
	                    sql.append(" and path=? ");
	                    value.add(guidkey);
	                    value.add(fileid);
	                    dao.update(sql.toString(),value);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        /**
         * @param attach_codeList
         * @param uploadFileList
         */
    public void getAttachCodeSetState(List<HashMap> attach_codeList, List<LazyDynaBean> uploadFileList) {
        for (HashMap codeMap : attach_codeList) {
            codeMap.put("hasFile", false);
            for (LazyDynaBean fileBean : uploadFileList) {
                String fileName = "";
                fileName = (String) fileBean.get("file_name");
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.contains(fileName, (String) codeMap.get("itemDesc"))) {
                    codeMap.put("hasFile", true);
                }
            }
        }
    }
    /**
     * 根据系统参数设置对密码处理
     * @param password
     */
    public String handlerPassword(String password,String type) {
		RecordVo recordVo = ConstantParamter.getRealConstantVo("EncryPwd");
		if(recordVo != null) {
			//判断系统参数是否设置账号加密
			String isEncryPwd = recordVo.getString("str_value"); 
			if("1".equals(isEncryPwd)) {
				if("encrypt".equalsIgnoreCase(type))
					password = new Des().EncryPwdStr(password); //加密
				else if("decrypt".equalsIgnoreCase(type))
					password = new Des().DecryPwdStr(password); //解密
			}
		}
    	return password;
    }
    
    /**
     * 获取姓名
     * @param a0100
     * @return
     */
    public String getRealName(String a0100) {
    	RowSet rs = null;
    	String a0101 = "";
    	try {
    		ArrayList<String> value = new ArrayList<String>();
    		value.add(a0100);
    		rs = dao.search("select a0101 from "+dbName+"A01 where a0100=?",value);
    		if(rs.next())
    			a0101 = rs.getString("a0101");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
		}
    	return a0101;
    }

	/**
	 * 前端密码控件解密
	 *
	 * @param encryptedClientRandom
	 * @param password
	 * @param serverRandom
	 * @return
	 */
	public String decodeCFCA(String encryptedClientRandom, String password, String serverRandom) {
		String oldStr = "";
		try {
			String sm2PfxFile = SystemConfig.getPropertyValue("sm2PfxFile");
			if (!FileUtil.fileExistence(sm2PfxFile,"sm2Encrypt.sm2")) {
				return oldStr;
			}
			sm2PfxFile = sm2PfxFile  + File.separator + "sm2Encrypt.sm2";
			final SIPDecryptor decryptor = SIPDecryptionBuilder.sm2().config(sm2PfxFile, "111111");
			oldStr = decryptor.decrypt(serverRandom, encryptedClientRandom, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return oldStr;
	}

}
