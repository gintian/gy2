package com.hjsj.hrms.transaction.general.operation;
/**修改审批模式这个页面  数据查询和数据更新都用该类。if(reqhm.containsKey("tabid"))把它们分隔开*/

import com.hjsj.hrms.businessobject.general.operation.TwfdefineBo;
import com.hjsj.hrms.businessobject.general.operation.TwfnodeBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateApproveWayTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		RecordVo template_tableVo =new RecordVo("template_table");//固定模板用到的表
		RecordVo t_wf_defineVo=new RecordVo("t_wf_define");//自定义模板用到的表
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		TwfdefineBo twf=new TwfdefineBo();
		TwfnodeBo twn=new TwfnodeBo();
		String bsp_flag="0";//=0:不需要审批  =1:需要审批 
		if(reqhm.containsKey("tabid")){//进行数据查询
			//reqhm传来tabid,usertype
			String tabid=(String) reqhm.get("tabid");
			String usertype=(String)reqhm.get("usertype");
			String email="false";//是否需要发邮件。该变量保存在/params/notes下的email中
			String sms="false";
			if("0".equals(usertype)){//如果是固定模板
				template_tableVo.setInt("tabid",(new Integer(tabid)).intValue());
			
				try {
					template_tableVo=dao.findByPrimaryKey(template_tableVo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operatin.find.fail"),"",""));

				}
				TemplateTableBo tt=new TemplateTableBo(this.getFrameconn(),template_tableVo,this.getUserView());
				int operationtype = tt.getOperationtype();//业务类型
				hm.put("operationtype", ""+operationtype);
				hm.put("infor_type", String.valueOf(tt.getInfor_type()));//得到模板类型 1：人员 2：单位 3：岗位  郭峰
				if(tt.isBsp_flag()){
					bsp_flag="1";
				}
				if(tt.isBemail()){
					email="0";
				}
				if(tt.isBsms()){
					sms="0";
				}
				template_tableVo.setInt("sp_flag",tt.getSp_mode());//这里把审批模式赋值给是否需要审批，并没有错。但不排除有错。因为前台是这样用的：<html:radio name="operationForm" property="template_tableVo.string(sp_flag)" value="0" disabled="" onclick="approvetypef2();">自动流转模式</html:radio>
				hm.put("template_tableVo",template_tableVo);
				hm.put("sp_flag",""+tt.getSp_mode());//在form中，sp_flag是审批模式（手工模式或自动流转）。它是ctrl_para参数里的一个属性
				hm.put("reject_type", tt.getReject_type());
				try {
					String sp_flag = template_tableVo.getString("sp_flag");//=0：自动流转 =1：手工指派  注意，变量的意思不是是否需要审批
					String ctrl_para=tt.getctrl_para(sp_flag,email,sms,usertype);
					if(sp_flag!=null&& "1".equals(sp_flag)){//如果是手工指派
						String endusertype =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","endusertype");
						String enduser =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","enduser");
						hm.put("enduservalue",enduser);
						hm.put("endusertype",endusertype);
						if(endusertype!=null&&endusertype.trim().length()>0){
							enduser =twf.getBusinessPerson(enduser,endusertype,this.getFrameconn());
							hm.put("enduser",enduser); 
							
						}else{
							hm.put("enduser","");
						}
					
					}
					String code_leader="";
					String relation_id =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","relation_id");
					hm.put("relation_id",relation_id);
					String def_flow_self =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","def_flow_self");
					hm.put("def_flow_self",def_flow_self);
					String mode=twf.getctrl_paraAttributeValue(ctrl_para,"split_data","mode");
					String no_sp_yj=twf.getctrl_paraAttributeValue(ctrl_para, "sp_flag", "no_sp_yj");
					String sync_archive_data=twf.getctrl_paraAttributeValue(ctrl_para, "sp_flag", "sync_archive_data");
					String out_type=twf.getctrl_paraAttributeValue(ctrl_para, "out", "type");
					if(mode==null){
						code_leader="-1";
					}else{
						if("groupfield".equalsIgnoreCase(mode)){
							code_leader="0";	
						}else if("superior".equalsIgnoreCase(mode)){
							code_leader="1";
						}else{
							code_leader="-1";
						}
					}
					if(no_sp_yj==null){//默认给予未选中
					    no_sp_yj="0";
					}
					if(sync_archive_data==null){//默认给予未选中
					    sync_archive_data="0";
					}
					if(StringUtils.isEmpty(out_type)) {
						out_type="1";
					}
					hm.put("out_type", out_type);
					hm.put("no_sp_yj", no_sp_yj);
					hm.put("sync_archive_data", sync_archive_data);
					Object codeitemid_buffer="";
					Object layer_buffer="";
					String fields =twf.getctrl_paraAttributeValue(ctrl_para,"split_data","fields");
					if(fields!=null&&!"".equals(fields)){
						String fields_group[]=fields.split(",");
						String fields_i;
						for(int i=0;i<fields_group.length;i++){
							fields_i=fields_group[i];
							if(i<fields_group.length-1){
								if(fields_i.indexOf("(")!=-1){
									codeitemid_buffer=codeitemid_buffer+fields_i.substring(0, fields_i.indexOf("("))+",";
									layer_buffer=layer_buffer+fields_i.substring(fields_i.indexOf("(")+1, fields_i.indexOf(")"))+",";
									}else{
									codeitemid_buffer=codeitemid_buffer+fields_i+",";
									layer_buffer=layer_buffer+"kong"+",";
									}
							}else{
								if(fields_i.indexOf("(")!=-1){
									codeitemid_buffer=codeitemid_buffer+fields_i.substring(0, fields_i.indexOf("("));
									layer_buffer=layer_buffer+fields_i.substring(fields_i.indexOf("(")+1, fields_i.indexOf(")"));
									}else{
									codeitemid_buffer=codeitemid_buffer+fields_i;
									layer_buffer=layer_buffer+"kong";
									}
							}
						}
					}	
					hm.put("code_leader", code_leader);
					if("0".equalsIgnoreCase(code_leader)){
						hm.put("codeitemid_buffer", codeitemid_buffer);
						hm.put("layer_buffer", layer_buffer);
					}else{
						hm.put("codeitemid_buffer", "");
						hm.put("layer_buffer", "");
					}

					String template_bos =twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_bos");
					String template_sp =twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_sp");
					String template_staff =twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_staff");
					String email_staff =twf.getctrl_paraAttributeValue(ctrl_para,"notes","email_staff");
					String notice_initiator=twf.getctrl_paraAttributeValue(ctrl_para,"notes","notice_initiator");//得到是否通知到发起人 true：通知 false：不通知
					String template_initiator=twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_initiator");//通知发起人时的模版
					if("0".equals(sms)|| "0".equals(email)){
						hm.put("sp_bos_flag","1"); 
					}else{
						hm.put("sp_bos_flag","0"); 
					}
					if(email_staff!=null&& "true".equals(email_staff)){
						email_staff ="0";
					}else{
						email_staff ="false";
					}
					hm.put("email_staff", email_staff);
					hm.put("template_bos",template_bos); 
					hm.put("template_sp",template_sp); 
					hm.put("template_staff",template_staff); 
					hm.put("template_spList",twf.getEmail_name(this.getFrameconn()));
					hm.put("tabid",tabid);
					hm.put("notice_initiator", notice_initiator);
					hm.put("template_initiator", template_initiator);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{//如果是自定义表单
				t_wf_defineVo.setInt("tabid",(new Integer(tabid)).intValue());
				TemplateTableBo tt=new TemplateTableBo(this.getFrameconn(),(new Integer(tabid)).intValue(),this.getUserView());
				int operationtype = tt.getOperationtype();
				hm.put("operationtype", ""+operationtype);
				try {
					t_wf_defineVo=dao.findByPrimaryKey(t_wf_defineVo);
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operatin.find.fail"),"",""));
				}
//				TemplateTableBo tt=new TemplateTableBo(this.getFrameconn(),t_wf_defineVo,this.getUserView());
				try {
					twf.paraxml(t_wf_defineVo.getString("ctrl_para"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("operation.paraxml.fail"),"",""));
				} 
				if("1".equals(t_wf_defineVo.getString("sp_flag"))){
					bsp_flag="1";
				}
				if(twf.getEmail()!=null&& "true".equals(twf.getEmail())){
					email="0";
				}else{
					email="false";
				}
				if(twf.getSms()!=null&& "true".equals(twf.getSms())){
					sms="0";
				}else{
					sms="false";
				}
				t_wf_defineVo.setString("sp_flag",twf.getSp_flag());
				hm.put("t_wf_defineVo",t_wf_defineVo);
				hm.put("sp_flag",t_wf_defineVo.getString("sp_flag"));
				try {
					String sp_flag = twf.getSp_flag();
					String ctrl_para=t_wf_defineVo.getString("ctrl_para");
					if(ctrl_para.length()<1){
						ctrl_para=twf.getctrl_para(sp_flag,email,sms);
					}else{
						ctrl_para=twf.updatetwfctrl_para(ctrl_para,sp_flag,email,sms);
					}
					String endusertype =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","endusertype");
					String enduser =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","enduser");
					String relation_id =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","relation_id");
					hm.put("relation_id",relation_id); 
                    String def_flow_self =twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","def_flow_self");
                    hm.put("def_flow_self",def_flow_self);
					hm.put("enduservalue",enduser);
					hm.put("endusertype",endusertype);
					if(endusertype!=null&&endusertype.trim().length()>0){
						enduser =twf.getBusinessPerson(enduser,endusertype,this.getFrameconn());
						hm.put("enduser",enduser); 
						
					}else{
						hm.put("enduser","");
					}
					String template_bos =twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_bos");
					String template_sp =twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_sp");
					String template_staff =twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_staff");
					String email_staff =twf.getctrl_paraAttributeValue(ctrl_para,"notes","email_staff");
					String notice_initiator=twf.getctrl_paraAttributeValue(ctrl_para,"notes","notice_initiator");//得到是否通知到发起人 true：通知 false：不通知
                    String template_initiator=twf.getctrl_paraAttributeValue(ctrl_para,"notes","template_initiator");//通知发起人时的模版
					if("0".equals(sms)|| "0".equals(email)){
						hm.put("sp_bos_flag","1"); 
					}else{
						hm.put("sp_bos_flag","0"); 
					}
					if(email_staff!=null&& "true".equals(email_staff)){
						email_staff ="0";
					}else{
						email_staff ="false";
					}
					hm.put("email_staff", email_staff);
					hm.put("template_bos",template_bos); 
					hm.put("template_sp",template_sp); 
					hm.put("template_staff",template_staff); 
					hm.put("template_spList",twf.getEmail_name(this.getFrameconn()));
					hm.put("notice_initiator", notice_initiator);
                    hm.put("template_initiator", template_initiator);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			ArrayList list=new ArrayList();
			CommonData dt=new CommonData("","未指定");
			list.add(dt);
			 dt=new CommonData("0","用户");
			list.add(dt);
			 dt=new CommonData("1","人员");
			list.add(dt);
			hm.put("enduserList",list);
			hm.put("sms",sms);
			hm.put("email",email);
			hm.put("bsp_flag",bsp_flag);	
			hm.put("usertype",usertype);
			reqhm.remove("tabid");
			
			//获得审批关系list
			ArrayList relationList = new ArrayList();
			try {
				dt=new CommonData("","");
				relationList.add(dt);
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				
				if(dbWizard.isExistTable("t_wf_relation", false)){
				this.frowset = dao.search(" select * from t_wf_relation where validflag='1' order by  seq  ");
				while(this.frowset.next()){
					 dt=new CommonData(this.frowset.getString("relation_id"),this.frowset.getString("cname"));
					 relationList.add(dt);
				}
				}
				
				
				String superField=getPS_SUPERIOR_value();
				if(superField.length()>0)
				{
					dt=new CommonData("gwgx",ResourceFactory.getProperty("general.template.nodedefine.gwhbgx"));
					relationList.add(dt);
				} 
				hm.put("relationList",relationList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{//进行数据更新
			Object codeitemid_buffer=(Object) hm.get("codeitemid_buffer");
			Object layer_buffer=(Object) hm.get("layer_buffer");
			hm.put("codeitemid_buffer", codeitemid_buffer);
			hm.put("layer_buffer", layer_buffer);
			String codeitemid[]=codeitemid_buffer.toString().split(",");
			String layer[]=layer_buffer.toString().split(",");
			String fields="";
			for(int i=0;i<codeitemid.length;i++){
				if(i<codeitemid.length-1){
					if(!"kong".equalsIgnoreCase(layer[i])){
					fields=fields+codeitemid[i]+"("+layer[i]+")," ;
					}else{
				    fields=fields+codeitemid[i]+",";
					}
				}else{
					if(!"kong".equalsIgnoreCase(layer[i])){
					fields=fields+codeitemid[i]+"("+layer[i]+")" ;
					}else{
				    fields=fields+codeitemid[i] ;
					}
				}
			}
			String code_leader=(String) hm.get("code_leader");
			String sms=(String) hm.get("sms");
			String email=(String) hm.get("email");
			String reject_type = (String)hm.get("reject_type");
			String  template_sp=(String) hm.get("template_sp");
			String  template_bos=(String) hm.get("template_bos");
			bsp_flag=(String)hm.get("bsp_flag");
			if ("0".equals(bsp_flag)){//不需要审批 清空拆单模式 2015-04-09
				code_leader="-1";
			}
			String usertype=(String)hm.get("usertype");
			String email_staff=(String) hm.get("email_staff");
			String template_staff=(String) hm.get("template_staff");
			HashMap map = (HashMap)this.getFormHM().get("spflagmap");
			String operationtype =(String) hm.get("operationtype");
			String notice_initiator=(String) hm.get("notice_initiator");//得到是否通知到发起人 true：通知 false：不通知
            String template_initiator=(String) hm.get("template_initiator");//通知发起人时的模版
            String no_sp_yj=(String) hm.get("no_sp_yj");//审批时不填写审批意见 1：不填写 0：填写
            String sync_archive_data=(String) hm.get("sync_archive_data");//审批时不填写审批意见 1：不填写 0：填写
            String out_type=(String)hm.get("out_type");//输出格式 1分页导出、 2连续导出 
            if(StringUtils.isEmpty(out_type)) {
            	out_type="1";
            }
			if("0".equals(usertype)){//如果是固定表单
				template_tableVo=(RecordVo) hm.get("template_tableVo");
				TemplateTableBo tt=new TemplateTableBo(this.getFrameconn(),template_tableVo,this.getUserView());
				String sp_flag=template_tableVo.getString("sp_flag");
				try {
					if("on".equals(sms)){
						sms="true";
					}else{
						sms="false";
					}
					if("on".equals(email)){
						email="true";
					}else{
						email="false";
					}
					if("on".equals(email_staff)){
						email_staff="true";
					}else{
						email_staff="false";
					}
					if("0".equals(bsp_flag)){//如果不需要审批
						reject_type = "1";
					}
					if("on".equals(notice_initiator)){
					    notice_initiator="true";
					}else{
					    notice_initiator="false";
					}
					if(no_sp_yj!=null&& "on".equals(no_sp_yj)){
					    no_sp_yj="1";
					}else{
					    no_sp_yj="0";
					}
					if(sync_archive_data!=null&& "on".equals(sync_archive_data)){
					    sync_archive_data="1";
					}else{
					    sync_archive_data="0";
					}
					map.put(""+template_tableVo.getInt("tabid"), ""+sp_flag);
					this.getFormHM().put("spflagmap", map);
					String ctrl_para=tt.getctrl_para(sp_flag,email,sms,usertype);
					ctrl_para=tt.getctrl_para_codediff(sp_flag,email,sms,usertype,fields,code_leader);//加<split_data>
					String relation_id=(String)hm.get("relation_id");
					ctrl_para =twf.updatectrl_paraAttributevalue(relation_id, "sp_flag", "relation_id", usertype, ctrl_para);
					String def_flow_self=(String)hm.get("def_flow_self");
					if ("0".equals(bsp_flag) || (!"1".equals(sp_flag))) def_flow_self="0";//非审批模式置0
					ctrl_para =twf.updatectrl_paraAttributevalue(def_flow_self, "sp_flag", "def_flow_self", usertype, ctrl_para);
					
					ctrl_para=twf.updatectrl_paraAttributevalue(out_type, "out", "type", usertype, ctrl_para);
					
					//更新驳回方式 <rejectFlag>1</rejectFlag>
					ctrl_para =twf.updatectrl_paraValue(reject_type,ctrl_para);
					if(sp_flag!=null&& "1".equals(sp_flag)){//如果是手工指派
						String enduservalue=(String)hm.get("enduservalue");
						ctrl_para =twf.updatectrl_paraAttributevalue(enduservalue, "sp_flag", "enduser", usertype, ctrl_para);
						String endusertype=(String)hm.get("endusertype");
						ctrl_para =twf.updatectrl_paraAttributevalue(endusertype, "sp_flag", "endusertype", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue(no_sp_yj, "sp_flag", "no_sp_yj", usertype, ctrl_para);
					}else{//如果是自动流转
						ctrl_para =twf.updatectrl_paraAttributevalue("", "sp_flag", "enduser", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue("", "sp_flag", "endusertype", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue(no_sp_yj, "sp_flag", "no_sp_yj", usertype, ctrl_para);
					}
					ctrl_para =twf.updatectrl_paraAttributevalue(sync_archive_data, "sp_flag", "sync_archive_data", usertype, ctrl_para);
					if("true".equals(email)|| "true".equals(sms)){
						ctrl_para =twf.updatectrl_paraAttributevalue(template_sp, "notes", "template_sp", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue(template_bos, "notes", "template_bos", usertype, ctrl_para);
						if(operationtype!=null&&(!"0".equals(operationtype)&&!"5".equals(operationtype))){
						ctrl_para =twf.updatectrl_paraAttributevalue(email_staff, "notes", "email_staff", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue(template_staff, "notes", "template_staff", usertype, ctrl_para);
						}
						ctrl_para=twf.updatectrl_paraAttributevalue(notice_initiator,"notes", "notice_initiator", usertype, ctrl_para);
						ctrl_para=twf.updatectrl_paraAttributevalue(template_initiator,"notes", "template_initiator", usertype, ctrl_para);
					}
					if("0".equals(bsp_flag)){
						sp_flag="0";
					//	twn.delnode(this.getFrameconn(),template_tableVo.getString("tabid"));
					}else{
						if(tt.getSp_mode()==0&& "1".equals(sp_flag)){
							twn.delnode(this.frameconn, template_tableVo.getString("tabid"));
							template_tableVo.setString("wf_chart", null);
						}
						sp_flag="1";
						twn.insertnode(dao,this.getFrameconn(),template_tableVo.getString("tabid"));
					}
					template_tableVo.setString("sp_flag",sp_flag);
					template_tableVo.setString("ctrl_para",ctrl_para);
					dao.updateValueObject(template_tableVo);
					//将templatetablevo加入到缓存
					TemplateStaticDataBo.setTemplateHashMap(template_tableVo.getString("tabid"), template_tableVo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("t_template.update.way.fail"),"",""));
				}
			}else{//如果是自定义表单
				t_wf_defineVo=(RecordVo) hm.get("t_wf_defineVo");
				String sp_flag=t_wf_defineVo.getString("sp_flag");
				map.put(""+template_tableVo.getInt("tabid"), ""+sp_flag);
				this.getFormHM().put("spflagmap", map);
				try {
					if("on".equals(sms)){
						sms="true";
					}else{
						sms="false";
					}
					if("on".equals(email)){
						email="true";
					}else{
						email="false";
					}
					if("on".equals(email_staff)){
						email_staff="true";
					}else{
						email_staff="false";
					}
					if("on".equals(notice_initiator)){//是否在流程结束时通知发起人
                        notice_initiator="true";
                    }else{
                        notice_initiator="false";
                    }
					if(no_sp_yj!=null&& "on".equals(no_sp_yj)){
                        no_sp_yj="1";
                    }else{
                        no_sp_yj="0";
                    }
					if(sync_archive_data!=null&& "on".equals(sync_archive_data)){
					    sync_archive_data="1";
					}else{
					    sync_archive_data="0";
					}
					String ctrl_para=t_wf_defineVo.getString("ctrl_para");
					String value = twf.getctrl_paraAttributeValue(ctrl_para,"sp_flag","mode");
					
					if(ctrl_para.length()<1){
						ctrl_para=twf.getctrl_para(sp_flag,email,sms);
					}else{
						ctrl_para=twf.updatetwfctrl_para(ctrl_para,sp_flag,email,sms);
					}
					String relation_id=(String)hm.get("relation_id");
					ctrl_para =twf.updatectrl_paraAttributevalue(relation_id, "sp_flag", "relation_id", usertype, ctrl_para);					
                    String def_flow_self=(String)hm.get("def_flow_self");
                    if ("0".equals(bsp_flag) || (!"1".equals(sp_flag))) def_flow_self="0";
                    ctrl_para =twf.updatectrl_paraAttributevalue(def_flow_self, "sp_flag", "def_flow_self", usertype, ctrl_para);
					
					if(sp_flag!=null&& "1".equals(sp_flag)){
						String enduservalue=(String)hm.get("enduservalue");
						ctrl_para =twf.updatectrl_paraAttributevalue(enduservalue, "sp_flag", "enduser", usertype, ctrl_para);
						String endusertype=(String)hm.get("endusertype");
						ctrl_para =twf.updatectrl_paraAttributevalue(endusertype, "sp_flag", "endusertype", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue(no_sp_yj, "sp_flag", "no_sp_yj", usertype, ctrl_para);
						}else{
							ctrl_para =twf.updatectrl_paraAttributevalue("", "sp_flag", "enduser", usertype, ctrl_para);
							ctrl_para =twf.updatectrl_paraAttributevalue("", "sp_flag", "endusertype", usertype, ctrl_para);
							ctrl_para =twf.updatectrl_paraAttributevalue(no_sp_yj, "sp_flag", "no_sp_yj", usertype, ctrl_para);
						}
					ctrl_para =twf.updatectrl_paraAttributevalue(sync_archive_data, "sp_flag", "sync_archive_data", usertype, ctrl_para);
					if("true".equals(email)|| "true".equals(sms)){
						ctrl_para =twf.updatectrl_paraAttributevalue(template_sp, "notes", "template_sp", usertype, ctrl_para);
						ctrl_para =twf.updatectrl_paraAttributevalue(template_bos, "notes", "template_bos", usertype, ctrl_para);
						if(operationtype!=null&&(!"0".equals(operationtype)&&!"5".equals(operationtype))){
						    ctrl_para =twf.updatectrl_paraAttributevalue(email_staff, "notes", "email_staff", usertype, ctrl_para);
						    ctrl_para =twf.updatectrl_paraAttributevalue(template_staff, "notes", "template_staff", usertype, ctrl_para);
						}
						ctrl_para=twf.updatectrl_paraAttributevalue(notice_initiator,"notes", "notice_initiator", usertype, ctrl_para);
                        ctrl_para=twf.updatectrl_paraAttributevalue(template_initiator,"notes", "template_initiator", usertype, ctrl_para);
					}
					if("0".equals(bsp_flag)){
						sp_flag="0";
					//	twn.delnode(this.getFrameconn(),t_wf_defineVo.getString("tabid"));
					}else{
						if(value!=null||value.trim().length()!=0){
							if("0".equals(value)&& "1".equals(sp_flag)){
								twn.delnode(this.frameconn, template_tableVo.getString("tabid"));
							}
						}
						sp_flag="1";
						twn.insertnode(dao,this.getFrameconn(),t_wf_defineVo.getString("tabid"));
					}
					t_wf_defineVo.setString("sp_flag",sp_flag);
					t_wf_defineVo.setString("ctrl_para",ctrl_para);
					
					dao.updateValueObject(t_wf_defineVo);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("t_template.update.way.fail"),"",""));
				}
			}
			
		}

	}
	
	
	/**
	 * 获得 汇报关系中 直接上级指标
	 * @return
	 */
	public String getPS_SUPERIOR_value()
	{
		String fieldItem="";
		RecordVo vo=ConstantParamter.getConstantVo("PS_SUPERIOR");
        if(vo==null)
        	return fieldItem;
        String param=vo.getString("str_value");
        if(param==null|| "".equals(param)|| "#".equals(param))
        	return fieldItem;
		fieldItem=param;
		return fieldItem;
	}

}
