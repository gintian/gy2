package com.hjsj.hrms.module.hire.api.v1;

import com.hjsj.hrms.module.hire.businessobject.PositionBo;
import com.hjsj.hrms.module.hire.businessobject.ResumeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ApplyedPositionTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			String type = (String) this.getFormHM().get("type");
			String return_code = "success";
			String return_msg = "";
			PositionBo bo = new PositionBo(this.frameconn, this.userView);
			HashMap return_data = new HashMap();
			//获取已应聘职位信息
			if("search".equalsIgnoreCase(type)) {
				ArrayList column_info = bo.getApplyedPosFileds();
				LinkedHashMap<String, ArrayList> position_list = bo.getApplyedPositions(false);
				String deleteRecord = SystemConfig.getPropertyValue("zp_delete_must_set_record");
				return_data.put("deleteRecord", deleteRecord);
				return_data.put("column_info", column_info);
				return_data.put("position_list", position_list);
				this.getFormHM().put("return_data", return_data);
			//应聘职位
			} else if ("apply".equalsIgnoreCase(type)) {
				String pos_id = (String) this.getFormHM().get("pos_id");
				pos_id = PubFunc.decrypt(pos_id);
				return_code = bo.testPosition(pos_id);
				ResumeBo resumeBo = new ResumeBo(this.frameconn, this.userView);
				// 校验招聘渠道是否正确
				if ("success".equals(return_code)) {
					return_code = resumeBo.checkApplyCode(pos_id);
					if (!"success".equals(return_code)) {
						return_msg = "您的应聘身份与本职位要求不符！请前往“个人中心”修改左侧头像下方的“应聘身份”。";
					}
				}
				// 检验照片必填情况下照片是否上传
				if ("success".equals(return_code)) {
					if (StringUtils.equals(resumeBo.getIfMustUpload(), "1")
							&& StringUtils.equals("", resumeBo.getPhotoPath())) {
						return_code = "notHavePhoto";
					}
				}
				// 判断简历资料必填项是否没填
				if ("success".equals(return_code)) {
					// return_code：【加密后的A01】-【基本信息】[有必填项未填写||必须填写]
					return_code = resumeBo.checkRequired();
					if (!"success".equals(return_code)) {
						String[] split = return_code.split("-");
						return_msg = return_code.substring(return_code.indexOf("-") + 1);
						this.getFormHM().put("subset", split[0]);
					}
				}
				if ("success".equals(return_code)) {
					ArrayList<String> ruleFilter = bo.ruleFilter(pos_id);
					if (ruleFilter.size() > 0 && !this.userView.getA0100().equals(ruleFilter.get(0))) {
						return_code = "resumeRulefail";
						return_msg = "您的简历信息不符合本职位要求！";
					}
				}
				if ("success".equals(return_code)) {
					return_code = bo.applyPosition(pos_id);
				}

				if ("before".equals(return_code)) {
					return_msg = "尚未到该职位的报名起始日期，不允许继续申请！";
				} else if ("after".equals(return_code)) {
					return_msg = "已超过该职位的报名截止日期，不允许继续申请！";
				}  else if ("finished".equals(return_code)) {
					return_msg = "该职位已结束招聘，不允许继续申请！";
				}  else if ("paused".equals(return_code)) {
					return_msg = "该职位已暂停招聘，不允许继续申请！";
				} else if ("3".equals(return_code.split("`")[0])) {
					return_msg = "您已申请了" + return_code.split("`")[1] + "个职位， 达到了最大申请职位数，不能再申请！";
				} else if ("6".equals(return_code))
					return_msg = "您的岗位申请已被接受，请等待通知！";}else if("cancel".equalsIgnoreCase(type)) {
				String pos_id = (String) this.getFormHM().get("pos_id");
				pos_id = PubFunc.decrypt(pos_id);
				return_code = bo.cancelApply(pos_id);
				if("cannot".equals(return_code)) 
					return_msg = "该用户已入职，不允许继续操作职位！";
				else if("in_process".equals(return_code))
					return_msg = "您的职位申请正在处理中，不可以取消！";
				else if("success".equals(return_code))
					return_msg = "已成功取消应聘申请！";
				
			}else if("change".equalsIgnoreCase(type)) {
				ArrayList pos_info = (ArrayList) this.getFormHM().get("pos_info");
				return_code = bo.changeThenumber(pos_info);
			}else if("showhistory".equalsIgnoreCase(type)) {
				LinkedHashMap<String, ArrayList> position_list = bo.getApplyedPositions(true);
				return_data.put("position_list", position_list);
				this.getFormHM().put("return_data", return_data);
			}
			this.getFormHM().put("return_code", return_code);
			this.getFormHM().put("return_msg", return_msg);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
