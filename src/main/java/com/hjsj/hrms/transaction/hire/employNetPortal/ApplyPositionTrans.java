package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.EmployNetPortalBo;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.ResumeFilterBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
/**
 * 申请职位
 * @author dengcan
 *
 */
public class ApplyPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		String info="2";   // 2: 申请成功   3:已超过了申请职位的最大数量3  4:简历资料必填项没填 
		RowSet search = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			//简历中心的bo类,通过这个类的方法得到新简历数目和所有简历数目
			PositionBo positionBo=new PositionBo(this.getFrameconn(),dao,this.userView);
			RecordVo zpDbNameVo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbname=zpDbNameVo.getString("str_value");
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.getFrameconn());
			String z0301=(String)this.getFormHM().get("z0301");
			z0301=SafeCode.decode(z0301);
			z0301=PubFunc.getReplaceStr(z0301);
			HashMap res = positionBo.isTimeOut(z0301);
			if(res.get("info") != null){ 
				if(res.get("isTimeOut") != null && (Boolean) res.get("isTimeOut")){
					if("before".equalsIgnoreCase((String)res.get("info")))
						info = "before"; 
					if("after".equalsIgnoreCase((String)res.get("info")))
						info = "after"; 
				}
			}else{
				String a0100 = (String)this.getFormHM().get("a0100");
				String posID=(String)this.getFormHM().get("posID");
				String userName=(String)this.getFormHM().get("userName");
				String person_type=(String)this.getFormHM().get("person_type");
				String returnType=(String)this.getFormHM().get("returnType");
				//招聘渠道
				String hireChannel = (String)this.getFormHM().get("hireChannel");
				String filter = (String)this.getFormHM().get("filter");
				
				a0100=PubFunc.getReplaceStr(a0100);
				posID=SafeCode.decode(posID);
				posID=PubFunc.getReplaceStr(posID);
				userName=PubFunc.getReplaceStr(userName);
				person_type=PubFunc.getReplaceStr(person_type);
				//校验结果
				boolean filterFlag = true;
				/**人员入职之后不允许继续申请!**/
				String isRzSql = "select a01.a0100 from "+dbname+"A01 a01 left join zp_pos_tache zpt on a01.a0100 = zpt.a0100 where  zpt.resume_flag = '1003' and a01.a0100 = ?";
				ArrayList<String> list = new ArrayList<String>();
				list.add(a0100);
				RowSet rs = dao.search(isRzSql,list);
				if(rs.next())
				{
					info="10";//该用户已入职，不允许继续申请!
					return;
				}
				rs.close();
				list.clear();
				/***该职位已申请之后不允许继续申请!***/
				StringBuffer buf=new StringBuffer();
				buf.append("select zp_pos_id from zp_pos_tache ");
				buf.append(" where a0100=? ");
				buf.append(" and nbase=? and zp_pos_id=?");
				list.add(a0100);
				list.add(dbname);
				list.add(z0301);
				this.frowset=dao.search(buf.toString(),list);
				if(this.frowset.next())
				{
					info="5";//此职位已申请过
					return;
				}
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
				HashMap _map=parameterXMLBo.getAttributeValues();
				String workExperience=employNetPortalBo.getWorkExperience();
				String isDefineWorkExperience=EmployNetPortalBo.isDefineWorkExperience;
				String value="";
				if("1".equals(isDefineWorkExperience))
				{
					String workExperience_item=(String)_map.get("workExperience");
					this.frowset=dao.search("select "+workExperience_item+" from "+dbname+"a01 where a0100='"+a0100+"'");
					if(this.frowset.next())
						value=this.frowset.getString(1)!=null?this.frowset.getString(1):"1";  //(String)this.getFormHM().get("workExperience");
						
				}
				//定义了工作经验参数，且注册选择的是校园  或 从校园招聘查看简历
    			//headHire、猎头招聘    01、校园招聘   02、社会招聘
    			if("1".equals(isDefineWorkExperience)&& "2".equals(value)||"headHire".equals(hireChannel)||"out".equalsIgnoreCase(hireChannel))
    				hireChannel = "02";
    			
				//校验职位筛选条件是否符合
				ResumeFilterBo filterBo = new ResumeFilterBo(this.frameconn, this.userView);
				ArrayList<String> ruleFilter = new ArrayList<String>();

    			String candidate_status = "";//应聘身份指标
    			String candidateValue  = "应聘身份";
    			String jobTybe  = "";
    	        //如果应聘身份指标启用，则先校验该职位渠道是否包含应聘者身份，如果包含继续校验该应聘者身份所有必填指标已填写
    	        if(_map.get("candidate_status")!=null) {
    	        	String candidateStatusId = (String)_map.get("candidate_status");
    	        	if(StringUtils.isNotEmpty(candidateStatusId)){
    	        		FieldItem fieldItem = DataDictionary.getFieldItem(candidateStatusId, "a01");
    	        		if(fieldItem != null)
    	        			candidateValue = fieldItem.getItemdesc();
    	        		fieldItem = DataDictionary.getFieldItem("z0385", "z03");
    	        		if(fieldItem != null)
    	        			jobTybe = fieldItem.getItemdesc();
    	        	}
    	        		
    	        	
    	        	if(StringUtils.isNotEmpty(candidateStatusId)&&!"#".equals(candidateStatusId)) {
    	        		list.clear();
    	        		list.add(a0100);
    					rs = dao.search("select "+candidateStatusId+" as candidateStatus from "+dbname+"A01 where A0100=?", list);
    					if(rs.next())
    						candidate_status = rs.getString("candidateStatus");
	    	        	//校验是否有选择应聘身份
	    	        	if(StringUtils.isEmpty(candidate_status)) {
	    					ruleFilter.add(candidateValue);
	    					this.formHM.put("ruleFilter", ruleFilter);
	    					this.formHM.put("jobTybe", jobTybe);
	    					return;
	    	        	}
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
	    	        	
	    	        	list.clear();
	    	        	//暂时只能通过长度来区分是否为加密数据
	    	            z0301 = z0301.length()>12 ? PubFunc.decrypt(z0301) : z0301;
	    	        	list.add(z0301);
	    	        	list.add(candidate_status);
	    	        	search = dao.search(sql.toString(),list);
	    	        	if(!search.next()) {
	    	        		ruleFilter.add(candidateValue);
	    	        		this.formHM.put("filterFlag", false);
	    	        		this.formHM.put("ruleFilter", ruleFilter);
	    	        		this.formHM.put("jobTybe", jobTybe);
	    	        		return;
	    	        	}
    	        	}
    	        }
    	        //校验前置岗位条件
	        	String checkApplyQualify = filterBo.checkApplyQualify(dbname, a0100, z0301);
	        	if(!"".equals(checkApplyQualify)) {
	        		this.formHM.put("filterFlag", false);
	        		this.formHM.put("checkApplyQualify", checkApplyQualify);
	        		return;
	        	}
				
    	       //判断简历资料必填项是否没填
				String isResumePerfection = isResumePerfection = employNetPortalBo.checkRequired(hireChannel,a0100);
				if(!"1".equals(isResumePerfection))
					info = "4";
				if(filterBo.getApplyControl(z0301)) {
					ArrayList<String> filterId = filterBo.getFilterId(z0301);
					if(filterId.size()>0) {
						ruleFilter = filterBo.ruleFilter(z0301, a0100, dbname, "apply");
						if(!a0100.equals(ruleFilter.get(0)))
							filterFlag = false;
					}
				}
				//返回校验参数
				if("true".equals(filter)) {
					if(!filterFlag)
						this.formHM.put("ruleFilter", ruleFilter);
					this.formHM.put("jobTybe", jobTybe);
					this.formHM.put("filterFlag", filterFlag);
					this.formHM.put("a0100", a0100);
					this.formHM.put("posID", posID);
					this.formHM.put("userName", userName);
					this.formHM.put("person_type", person_type);
					this.formHM.put("returnType", returnType);
					this.formHM.put("hireChannel", hireChannel);
				}else {//申请职位
					
					if(!"4".equals(info)&&filterFlag)
					{
						/**统计用户申请职位总数时，不计算申请的没有通过的职位*/
						com.hjsj.hrms.module.hire.businessobject.PositionBo pbo = new com.hjsj.hrms.module.hire.businessobject.PositionBo(frameconn, userView);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map = pbo.checkMaxcount(z0301, a0100);
						boolean flag = (boolean) map.get("flag");
						if(flag)
							{
								RecordVo vo = new RecordVo("zp_pos_tache");
								vo.setString("zp_pos_id",z0301);
								vo.setString("a0100",a0100);
								vo.setString("nbase",dbname);
								int sizeJobs = pbo.getSizeJobs(a0100,z0301);
								vo.setInt("thenumber",sizeJobs +1);
								vo.setDate("apply_date",Calendar.getInstance().getTime());
								vo.setDate("recdate",Calendar.getInstance().getTime());
								vo.setString("status","0");
								vo.setInt("relation_type",1);
								dao.addValueObject(vo);
								
								employNetPortalBo.addStatInfo(2,z0301);
								//老招聘发送邮件通知功能
								/*DemandCtrlParamXmlBo dbo = new DemandCtrlParamXmlBo(this.getFrameconn(),z0301);
								HashMap map = dbo.getAttributeValues("answer_mail");
								LazyDynaBean bean = (LazyDynaBean)map.get("answer_mail");
								if(bean!=null){
									String template_id = (String)bean.get("template");
									if(template_id!=null && template_id.trim().length()>0){
										String flag=(String)bean.get("flag");
										if(flag!=null && flag.equalsIgnoreCase("true")){
											AutoSendEMailBo bo = new AutoSendEMailBo(this.getFrameconn());
											bo.AutoSend(z0301,"",a0100,dbname,template_id);
										}
									}
								}*/
								//开始更新z03里面的数据,将新简历数目和所有简历数目更新一下
								positionBo.saveCandiatesNumber(z0301, 1);//1更新新简历数目
								positionBo.saveCandiatesNumber(z0301, 3);//3更新所有简历数目												       
								
								//更新当前的人员是否满足筛选状态
								ResumeFilterBo rbo = new ResumeFilterBo(this.frameconn, this.userView);
								ArrayList z03list = new ArrayList();
								z03list.add(z0301);
								rbo.updateSuitable(z03list, a0100);						        
								String sqlAccept_post="select accept_post from z03 where Z0301 =?"; 
								this.frowset=dao.search(sqlAccept_post,z03list);
								if (this.frowset.next()) {
								    String acceptPostSql = this.frowset.getString("accept_post");
								    //判断是否选择自动接受职位申请
								    if ("1".equalsIgnoreCase(acceptPostSql)) {
								        String sqlResume = "select suitable from  zp_pos_tache where A0100 ='" + a0100 + "' and ZP_POS_ID ='" + z0301 + "'";
								        this.frowset = dao.search(sqlResume);
								        if (this.frowset.next()) {
								            String suitable = this.frowset.getString("suitable");
								            //判断是否满足职位筛选
								            if ("1".equalsIgnoreCase(suitable)) {
								                //默认自动接受职位申请
								                ArrayList List = positionBo.applyPosition("1", a0100, userName, z0301, dbname, "1");
								                info = "6";
								            }
								        }
								    }
								}
								
							}else{
								info="3";
							}
						}
//					}
					
					this.getFormHM().put("userName", userName);
					this.getFormHM().put("returnType", returnType);
				}
				if("4".equals(info))
					info = isResumePerfection;
			}	
				
		}
		catch(Exception e)
		{
			info="4";
			e.printStackTrace();
		}
		finally
		{						
			this.getFormHM().put("info",info);
			
		}

	}
	
	private String getResource(String setlist,String id){
		String[] arr = setlist.split(",");
		for (int i = 0; i < arr.length; i++) {
			if("".equals(arr[i].trim()))
				continue;
			if(arr[i].indexOf(id+"[")!=-1)
				return arr[i];
		}
		return "";
	}

}
