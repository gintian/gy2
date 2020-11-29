/**
 * 添加试卷 LiWeichao 2011-10-25 10:15:01
 */
package com.hjsj.hrms.transaction.train.trainexam.paper;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddExamPaperTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5300 = (String) hm.get("r5300");
		r5300 = PubFunc.decrypt(SafeCode.decode(r5300));
		String r5311 = "";
		hm.remove("r5300");
		List itemList = new ArrayList();
		this.getFormHM().put("r5300", "");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			if (r5300 != null && r5300.trim().length() > 0) {// 修改
				RecordVo vo = new RecordVo("r53");
				vo.setString("r5300", r5300);
				vo = dao.findByPrimaryKey(vo);
				this.getFormHM().put("r5300", r5300);
				r5311 = vo.getString("r5311");
				if(!this.userView.isSuper_admin()&&vo.getString("b0110")!=null&&vo.getString("b0110").length()>0){//是否是上级判断
					TrainCourseBo tbo = new TrainCourseBo(this.userView,this.frameconn);
					int lee = tbo.isUserParent(vo.getString("b0110"));
					if(lee==2||lee==-1)
						r5311 = "04";//因为页面用的判断是 状态为04不可编辑 为方便这里是上级也给它个不可编辑的状态
				}
				
				List fieldList = DataDictionary.getFieldList("R53",
						Constant.USED_FIELD_SET);
				FieldItemView fieldItemView = null;
				FieldItem fieldItem = null;
				for (int i = 0; i < fieldList.size(); i++) {
					fieldItemView = new FieldItemView();
					fieldItem = (FieldItem) fieldList.get(i);
					if ("R5300".equalsIgnoreCase(fieldItem.getItemid())
							|| "create_time".equalsIgnoreCase(fieldItem.getItemid())
							|| "create_user".equalsIgnoreCase(fieldItem.getItemid())
							|| "R5306".equalsIgnoreCase(fieldItem.getItemid())) {
						continue;
					} else if ("R5311".equalsIgnoreCase(fieldItem.getItemid())) {
						fieldItemView.setValue(vo.getString(fieldItem
								.getItemid()));
						if(vo.getString(fieldItem.getItemid())!=null&&vo.getString(fieldItem.getItemid()).length()>0){
							RecordVo recordVo = new RecordVo("codeitem");
							recordVo.setString("codeitemid", vo
									.getString(fieldItem.getItemid()));
							recordVo.setString("codesetid", "23");
							try
							{
								recordVo = dao.findByPrimaryKey(recordVo);
							}catch (Exception e) {
								e.fillInStackTrace();
							}
							String temp = recordVo.getString("codeitemdesc");
							fieldItemView.setViewvalue(temp);
						}else
							fieldItemView.setViewvalue("");
						
						fieldItemView.setAuditingFormula(fieldItem
								.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem
								.getAuditingInformation());
						fieldItemView.setCodesetid("23");
						fieldItemView.setDecimalwidth(fieldItem
								.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem
								.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView
								.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setRowflag(String.valueOf(fieldList
								.size() - 1)); // 在struts用来表示换行的变量
						fieldItemView.setFillable(fieldItem.isFillable());
						fieldItemView.setVisible(false);
					} else {
						fieldItemView.setValue(vo.getString(fieldItem
								.getItemid()));
						if (!"0".equals(fieldItem.getCodesetid())) {
							if ("un".equalsIgnoreCase(fieldItem.getCodesetid())) {
								RecordVo voCodeItem = new RecordVo(
										"organization");
								voCodeItem.setString("codesetid", fieldItem
										.getCodesetid());
								voCodeItem.setString("codeitemid", vo
										.getString(fieldItem.getItemid()));
								if(vo.getString(fieldItem.getItemid())!=null&&vo.getString(fieldItem.getItemid()).length()>0){
									voCodeItem = dao
									.findByPrimaryKey(voCodeItem);
									fieldItemView.setViewvalue(voCodeItem
											.getString("codeitemdesc"));
								}else
									fieldItemView.setViewvalue("");
							} else {

								RecordVo voCodeItem = new RecordVo("codeitem");
								voCodeItem.setString("codesetid", fieldItem.getCodesetid());
								voCodeItem.setString("codeitemid", vo
										.getString(fieldItem.getItemid()));
								if(vo.getString(fieldItem.getItemid())!=null&&vo.getString(fieldItem.getItemid()).length()>0){
									voCodeItem = dao.findByPrimaryKey(voCodeItem);
									fieldItemView.setViewvalue(voCodeItem.getString("codeitemdesc"));
								}else
									fieldItemView.setViewvalue("");
								
							}
						}
						fieldItemView.setAuditingFormula(fieldItem
								.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem
								.getAuditingInformation());
						fieldItemView.setCodesetid(fieldItem.getCodesetid());
						fieldItemView.setDecimalwidth(fieldItem
								.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem
								.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView
								.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setRowflag(String.valueOf(fieldList
								.size() - 1)); // 在struts用来表示换行的变量
						fieldItemView.setFillable(fieldItem.isFillable());
					}
					itemList.add(fieldItemView.clone());
				}

			} else {// 新增
				List fieldList = DataDictionary.getFieldList("R53",
						Constant.USED_FIELD_SET);
				FieldItemView fieldItemView = null;
				FieldItem fieldItem = null;
				for (int i = 0; i < fieldList.size(); i++) {
					fieldItemView = new FieldItemView();
					fieldItem = (FieldItem) fieldList.get(i);
					if ("R5300".equalsIgnoreCase(fieldItem.getItemid())
							|| "create_time".equalsIgnoreCase(fieldItem.getItemid())
							|| "create_user".equalsIgnoreCase(fieldItem.getItemid())
							|| "R5306".equalsIgnoreCase(fieldItem.getItemid())) {
						continue;
					} else if ("R5311".equalsIgnoreCase(fieldItem.getItemid())) {
						fieldItemView.setValue("01");
						fieldItemView.setViewvalue("起草");
						fieldItemView.setAuditingFormula(fieldItem
								.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem
								.getAuditingInformation());
						fieldItemView.setCodesetid("23");
						fieldItemView.setDecimalwidth(fieldItem
								.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem
								.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView
								.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setRowflag(String.valueOf(fieldList
								.size() - 1)); // 在struts用来表示换行的变量
						fieldItemView.setFillable(fieldItem.isFillable());
						fieldItemView.setVisible(false);
					} else {
						fieldItemView.setAuditingFormula(fieldItem
								.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem
								.getAuditingInformation());
						fieldItemView.setCodesetid(fieldItem.getCodesetid());
						fieldItemView.setDecimalwidth(fieldItem
								.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem
								.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView
								.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setRowflag(String.valueOf(fieldList
								.size() - 1)); // 在struts用来表示换行的变量
						fieldItemView.setFillable(fieldItem.isFillable());
					}
					itemList.add(fieldItemView.clone());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("itemlist", itemList);
			this.getFormHM().put("r5300", SafeCode.encode(PubFunc.encrypt(r5300)));
			this.getFormHM().put("r5311", r5311);
		}
		/**新增课件控制显示部门 业务用户先判断操作单位 无单位判断管理范围 liwc*/
		String temp="";
//		if(!userView.isSuper_admin()){
//			if(userView.getStatus()==4)
//				temp=this.getUserView().getManagePrivCodeValue();
//			else{
//				String codeall = userView.getUnit_id();
//				if(codeall!=null&&codeall.length()>2)
//					temp=codeall;//.split("`")[0].substring(2);
//				if("".equals(temp))
//					temp=this.getUserView().getManagePrivCodeValue();
//			}
//		}else
//			temp=this.getUserView().getManagePrivCodeValue();
		if(!userView.isSuper_admin()){
			TrainCourseBo bo =  new TrainCourseBo(this.userView);
			temp=bo.getUnitIdByBusi();//this.userView.getUnitIdByBusi("6");
		}
		this.getFormHM().put("orgparentcode",temp);
	}
}
