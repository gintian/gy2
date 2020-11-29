package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.recruitment.position.businessobject.PositionBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class AjustOrderTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String opt = (String)this.getFormHM().get("opt");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String from = (String) this.getFormHM().get("from");
			String a0100 = (String)this.getFormHM().get("a0100");

			StringBuffer sql = new StringBuffer();
			ArrayList sqlParams = new ArrayList();
			
			RecordVo zpDbNameVo = ConstantParamter.getConstantVo("ZP_DBNAME");
			String nbase = zpDbNameVo.getString("str_value");
			
			//人员入职之后不允许继续操作职位!
		    sql.append("select * from ").append(nbase).append("A01 a01 left join zp_pos_tache zpt");
		    sql.append(" on a01.a0100=zpt.a0100");
		    sql.append(" where zpt.resume_flag='0903'");
		    sql.append(" and a01.a0100=?");
		    sqlParams.add(a0100);
		    this.frowset = dao.search(sql.toString(), sqlParams);
		    if(this.frowset.next()){
		        //该用户已入职，不允许继续操作职位
		    	this.getFormHM().put("info","该用户已入职，不允许继续操作职位！");
				return;
		    }
		    
		    //调整志愿顺序
		    if("order".equals(opt)) {
				String value = (String)this.getFormHM().get("value");
				a0100 = (String)this.getFormHM().get("a0100");
				if(null == from){
					a0100 = PubFunc.getReplaceStr(a0100);
					value = PubFunc.getReplaceStr(value);
				}
				
				sql.setLength(0);
				sql.append("update zp_pos_tache");
				sql.append(" set thenumber=?");
				sql.append(" where zp_pos_id=?");
				sql.append(" and a0100=?");				
				
				String[] value_str = value.split("#");
				for(int i=0; i<value_str.length; i++){
					if(value_str[i].trim().length() == 0)
						continue;
					
					String[] temp = value_str[i].split("/");
					
					sqlParams.clear();
					sqlParams.add(temp[1]);
					sqlParams.add(temp[0]);
					sqlParams.add(a0100);
					dao.update(sql.toString(), sqlParams);
				}
				this.getFormHM().put("info","修改成功！");
			} else if("del".equals(opt)) {
			    //取消应聘（删除志愿）
				ParameterXMLBo xmlBo = new ParameterXMLBo(this.getFrameconn(),"1");
				HashMap map = xmlBo.getAttributeValues();
				String resume_state = "";
				if(map.get("resume_state")!=null)
					resume_state = (String)map.get("resume_state");
				
				if("".equals(resume_state))
					throw (new Exception());
				
				a0100 = (String)this.getFormHM().get("a0100");
				String zp_pos_id = (String)this.getFormHM().get("zp_pos_id");
				if(null == from){
					a0100 = PubFunc.decrypt(a0100);
					zp_pos_id = PubFunc.decrypt(zp_pos_id);
				}
				
				//老招聘
//				this.frowset=dao.search("select * from "+dbname+"a01 where a0100='"+a0100+"' and ( "+resume_state+"='10' or "+resume_state+"='13' ) ");
				
				/*
				 * 新招聘
				 * 原未接收申请的、各环节的已淘汰和已终止的申请允许在外网已应聘职位中删除
				 * 现人大要求
				 */
				
				sql.setLength(0);
                sql.append("select * from zp_pos_tache");
                sql.append(" where a0100=?");
                sql.append(" and nbase=?");
                sql.append(" and zp_pos_id=?");
                sql.append(" and (resume_flag is null or resume_flag='')");
                /*人大需求：原未接收申请的、各环节的已淘汰和已终止的申请允许在外网已应聘职位中删除
                sql.append(" or resume_flag in ('0105','0106','0205','0206','0306','0307','0308',");
                sql.append("'0406','0407','0408','0506','0507','0508','0603','0604',");
                sql.append("'0703','0704','0805','0806','1004','1005')");
                */
                sqlParams.clear();
                sqlParams.add(a0100);
                sqlParams.add(nbase);
                sqlParams.add(zp_pos_id);
                
				this.frowset = dao.search(sql.toString(), sqlParams);
				if(this.frowset.next()) {
				    //后续志愿号减一
				    sql.setLength(0);
	                sql.append("update zp_pos_tache");
	                sql.append(" set thenumber=thenumber-1");
	                sql.append(" where a0100=?");
	                sql.append(" and thenumber>(select thenumber from zp_pos_tache");
	                sql.append(" where a0100=?");
	                sql.append(" and zp_pos_id=?)");
	                sql.append(" and nbase=?");
	                
	                sqlParams.clear();
	                sqlParams.add(a0100);
	                sqlParams.add(a0100);
	                sqlParams.add(zp_pos_id);
	                sqlParams.add(nbase);
					dao.update(sql.toString(), sqlParams);
					
					//删除申请记录
					sql.setLength(0);
	                sql.append("delete from zp_pos_tache");
	                sql.append(" where a0100=?");
	                sql.append(" and zp_pos_id=?");
	                sql.append(" and nbase=?");
	                
	                sqlParams.clear();
	                sqlParams.add(a0100);
	                sqlParams.add(zp_pos_id);
	                sqlParams.add(nbase);
					dao.delete(sql.toString(), sqlParams);
					
					this.getFormHM().put("info","已成功取消应聘申请！");
				} else {
					this.getFormHM().put("info","您的职位申请正在处理中，不可以取消！");
				}
				
				PositionBo pobo = new PositionBo(this.getFrameconn(),dao,null);
				pobo.saveCandiatesNumber(zp_pos_id, 1);
				pobo.saveCandiatesNumber(zp_pos_id, 2);
				pobo.saveCandiatesNumber(zp_pos_id, 3);
				
			}
		}
		catch(Exception e)
		{
			this.getFormHM().put("info","系统忙...，请稍后再试！");
			e.printStackTrace();
		}

	}

}
