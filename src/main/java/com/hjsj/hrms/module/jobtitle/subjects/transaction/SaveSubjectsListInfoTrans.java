package com.hjsj.hrms.module.jobtitle.subjects.transaction;

import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.module.jobtitle.subjects.businessobject.SubjectsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * 职称评审_学科组_保存
 * @createtime Nov 23, 2015 9:07:55 AM
 * @author chent
 *
 */
public class SaveSubjectsListInfoTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList datalist=(ArrayList) this.getFormHM().get("savedata");
			StringBuffer sql = new StringBuffer();		
			StringBuffer str = new  StringBuffer();
			ArrayList valueList = new ArrayList();

			ArrayList<HashMap> outExpert_Add=new ArrayList<HashMap>();
			ArrayList<HashMap> w01Expert_Update=new ArrayList<HashMap>();

			String needToSave = ",group_id_e,w0101_e,start_date,end_date,flag,";//只需要保存
			sql.append("update zc_subjectgroup_experts set ");
			for(int i=0;i<datalist.size();i++){
				StringBuffer where1 = new  StringBuffer();
				StringBuffer where2 = new  StringBuffer();
				ArrayList wherelist1 = new ArrayList();
				ArrayList wherelist2 = new ArrayList();
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);

				if("1`是".equalsIgnoreCase(map.get("w0111").toString())) {//新增外部专家
					if(map.containsKey("changestate")&& "add".equalsIgnoreCase(map.get("changestate").toString())) {
						outExpert_Add.add(map);
						continue;
					}
				}

				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				java.sql.Date startDate = null;
				java.sql.Date endDate = null;//haosl 20170523
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if(needToSave.indexOf(","+key.toString().toLowerCase()+",") != -1){//只需要保存
						if("group_id_e".equalsIgnoreCase(key.toString())) {//学科组编号
							where1.append(" group_id");
							where1.append("=?");
							wherelist1.add(PubFunc.decrypt(val.toString()));
							
						} else if("w0101_e".equalsIgnoreCase(key.toString())){//专家编号
							where2.append(" expertid");
							where2.append("=?");
							wherelist2.add(PubFunc.decrypt(val.toString()));
							
						}
						else {
							if(i == 0){
								str.append(",");
								str.append(key.toString());
								str.append(" =?");
							}
							//haosl 20170523  保存前对开始和结束日期进行校验
							if("start_date".equalsIgnoreCase(key.toString())){
								if(val != null && StringUtils.isNotEmpty(val.toString())){
									startDate = new java.sql.Date(DateUtils.getSqlDate(val.toString(),"yyyy-MM-dd").getTime());
								}
								list.add(startDate);
							} else if("end_date".equalsIgnoreCase(key.toString())){
								if(val != null && StringUtils.isNotEmpty(val.toString())){
									endDate = new java.sql.Date(DateUtils.getSqlDate(val.toString(),"yyyy-MM-dd").getTime());
								}
								list.add(endDate);
							}else if("flag".equalsIgnoreCase(key.toString())){
								//防止前台传过来参数为null时报错
								list.add(val==null||val.toString().indexOf("`")<=0?"":val.toString().split("`")[0]);
								
							} 
						}
					}
					
				}
				//haosl 20160826  校验开始日期不能大于结束日期
				if(startDate!=null && endDate!=null && startDate.after(endDate)){
					this.getFormHM().put("hinttext","终止日期不能小于起始日期");
					return;
				}
				//如果更新w01表，在学科组中的字段是flag，前台查的时候给的字段不是w0109，而是flag，这里仅涉及保存,改一下影响不大
				map.put("w0109", map.get("flag"));
				w01Expert_Update.add(map);//将需要更新的信息加入待更新数组

				list.addAll(wherelist1);
				list.addAll(wherelist2);
				valueList.add(list);
				if(i == 0){
					sql.append(str.substring(1));
					sql.append(" where ");
					sql.append(where1);
					sql.append(" and ");
					sql.append(where2);
				}		
			}
			dao.batchUpdate(sql.toString(), valueList);



			if(outExpert_Add.size()>0) {
				this.addOutExpert(dao, outExpert_Add);

			}

			if(w01Expert_Update.size()>0)
				this.updateW01Expert(dao,w01Expert_Update);

		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.getFormHM().put("hinttext",e.toString());
		}
	}

	/**
	 * 问卷模板是否修改,修改则创建计划
	 * @param key
	 * @param val
	 * @return
	 */
	private boolean isChangeValue(ArrayList datalist ) {
		
//		String key = keyO.toString();
//		String val = valO==null||valO.toString().length()==0?valO.toString():valO.toString().split("`")[0];
		String w0501 = "";
		String w0539 = "";
		String w0541 = "";
		for(int i=0;i<datalist.size();i++){
			DynaBean bean = (DynaBean) datalist.get(i);
			HashMap map = PubFunc.DynaBean2Map(bean);
			Iterator iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if("w0501".equalsIgnoreCase(key.toString())){
					w0501 = val.toString();
				} else if("W0539".equalsIgnoreCase(key.toString())) {
					w0539 = val==null||val.toString().length()==0?val.toString():val.toString().split("`")[0];
				} else if("W0541".equalsIgnoreCase(key.toString())) {
					w0539 = val==null||val.toString().length()==0?val.toString():val.toString().split("`")[0];
				}
			}
		}
		
		
		return false;
	}
	/**
	 * 更新w01 专家库信息
	 *
	 * @param dao
	 * @param outExpert 专家信息串
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 11:50 2018/4/25
	 */
	private void updateW01Expert(ContentDAO dao, ArrayList<HashMap> outExpert) throws GeneralException {
		try {
			ArrayList fieldList = DataDictionary.getFieldList("w01", Constant.USED_FIELD_SET);
			ExpertsBo bo = new ExpertsBo(this.frameconn, this.userView);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			ArrayList updFieldList = bo.getIdlist();//要修改的字段
			ArrayList<RecordVo> volist = new ArrayList<RecordVo>();

			for (int i = 0; i < outExpert.size(); i++) {
				HashMap updatemap = outExpert.get(i);
				RecordVo resultVo = new RecordVo("w01");
				for (int j = 0; j < updFieldList.size(); j++) {
					String fieldid = String.valueOf(updFieldList.get(j));//字段名
					if (!updatemap.containsKey(fieldid) && !"w0101".equalsIgnoreCase(fieldid)) {
						continue;
					}
					Object obj = updatemap.get(fieldid);
					if ("w0101".equalsIgnoreCase(fieldid)) {
						String w0101 = PubFunc.decrypt((String) updatemap.get("w0101_e"));
						resultVo.setString("w0101", w0101);
					} else if ("b0110".equals(fieldid)) {
						String fieldvalue = obj.toString();
						if (fieldvalue != null && fieldvalue.toString().contains("`")) {
							int index = fieldvalue.indexOf("`");
							fieldvalue = fieldvalue.substring(0, index);
							resultVo.setString(fieldid, fieldvalue);
						} else {
							resultVo.setString(fieldid, obj.toString());
						}
					} else {
						for (int k = 0; k < fieldList.size(); k++) {
							FieldItem item = (FieldItem) fieldList.get(k);
							String itemid = item.getItemid();//字段id
							String itemtype = item.getItemtype();//字段类型


							if (itemid.equals(fieldid)) {

								if ("w0109".equalsIgnoreCase(itemid)) {//如果是可聘任标识，则同步到评委会和学科组的专家的聘任标识 haosl 20160831
									if (obj != null) {
										String w0101 = PubFunc.decrypt((String) updatemap.get("w0101_e"));
										int index = obj.toString().indexOf("`");
										if (index > 0) {//有效字符串 如："1`是"
											String flag = obj.toString().substring(0, index);
											bo.syscExpertFlag(w0101, flag);//同步聘任标识
										}
									}
								}

								if ("D".equals(itemtype)) {
									if (obj == null || "".equals(obj)) {
										Date fieldvalue = null;
										resultVo.setDate(fieldid, fieldvalue);
									} else {
										Date fieldvalue = null;
										try {
											fieldvalue = sdf.parse(String.valueOf(obj));
											resultVo.setDate(fieldid, fieldvalue);
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								} else if ("N".equals(itemtype)) {
									if ("".equals(obj) || obj == null) {
										String fieldvalue = null;
										resultVo.setNumber(fieldid, fieldvalue);
									} else {
										resultVo.setNumber(fieldid, obj.toString());
									}
								} else if ("A".equals(itemtype)) {
									String fieldvalue = obj.toString();
									if (fieldvalue != null && fieldvalue.toString().contains("`")) {
										int index = fieldvalue.indexOf("`");
										fieldvalue = fieldvalue.substring(0, index);
										resultVo.setString(fieldid, fieldvalue);
									} else {
										resultVo.setString(fieldid, obj.toString());
									}
								}
							}
						}
					}
				}
				resultVo.setDate("modify_time", new Date());
//					resultVo.setString("create_user", this.userView.getUserName());
//					resultVo.setString("create_fullname", this.userView.getUserFullName());
//					resultVo.setString("b0110", this.userView.getUnitIdByBusi("9"));
				volist.add(resultVo);
			}
			dao.updateValueObject(volist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 新增外部专家
	 *
	 * @param dao
	 * @param outExpert
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 11:51 2018/4/25
	 */
	private void addOutExpert(ContentDAO dao, ArrayList<HashMap> outExpert) throws GeneralException {
		try {
			ArrayList fieldList = DataDictionary.getFieldList("w01", Constant.USED_FIELD_SET);
			ExpertsBo bo = new ExpertsBo(this.frameconn, this.userView);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			ArrayList updFieldList = bo.getIdlist();//要修改的字段
			ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
			ArrayList<String> personidList = new ArrayList<String>();
			String group_id = "";//评审会议id

			for (int i = 0; i < outExpert.size(); i++) {
				HashMap addmap = outExpert.get(i);

				if (StringUtils.isBlank(group_id))
					group_id = (String) addmap.get("group_id_e");


				RecordVo resultVo = new RecordVo("w01");
				for (int j = 0; j < updFieldList.size(); j++) {
					String fieldid = String.valueOf(updFieldList.get(j));//字段名
					if (!addmap.containsKey(fieldid)) {
						continue;
					}
					Object obj = addmap.get(fieldid);
					if ("b0110".equals(fieldid)) {
						String fieldvalue = obj.toString();
						if (fieldvalue != null && fieldvalue.toString().contains("`")) {
							int index = fieldvalue.indexOf("`");
							fieldvalue = fieldvalue.substring(0, index);
							resultVo.setString(fieldid, fieldvalue);
						} else {
							resultVo.setString(fieldid, obj.toString());
						}
					} else {
						for (int k = 0; k < fieldList.size(); k++) {
							FieldItem item = (FieldItem) fieldList.get(k);
							String itemid = item.getItemid();//字段id
							String itemtype = item.getItemtype();//字段类型
							if (itemid.equals(fieldid)) {
								if ("D".equals(itemtype)) {
									if (obj == null || "".equals(obj)) {
										Date fieldvalue = null;
										resultVo.setDate(fieldid, fieldvalue);
									} else {
										Date fieldvalue = null;
										try {
											fieldvalue = sdf.parse(String.valueOf(obj));
											resultVo.setDate(fieldid, fieldvalue);
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								} else if ("N".equals(itemtype)) {
									if ("".equals(obj) || obj == null) {
										String fieldvalue = null;
										resultVo.setNumber(fieldid, fieldvalue);
									} else {
										resultVo.setNumber(fieldid, obj.toString());
									}
								} else if ("A".equals(itemtype)) {
									String fieldvalue = obj.toString();
									if (fieldvalue != null && fieldvalue.toString().contains("`")) {
										int index = fieldvalue.indexOf("`");
										fieldvalue = fieldvalue.substring(0, index);
										resultVo.setString(fieldid, fieldvalue);
									} else {
										resultVo.setString(fieldid, obj.toString());
									}
								}
							}
						}
					}
				}
				IDGenerator idg = new IDGenerator(2, this.getFrameconn());
				String w0101 = idg.getId("W01.W0101");

				personidList.add(PubFunc.encrypt(String.valueOf(Integer.valueOf(w0101))));
				resultVo.setString("w0101", String.valueOf(Integer.valueOf(w0101)));
				resultVo.setDate("create_time", new Date());
				resultVo.setString("create_user", this.userView.getUserName());
				resultVo.setString("create_fullname", this.userView.getUserFullName());

				volist.add(resultVo);
			}
			dao.addValueObject(volist);

			SubjectsBo subjectsBo = new SubjectsBo(this.getFrameconn(), this.userView);// 工具类


			subjectsBo.createSubjectsPerson(PubFunc.decrypt(group_id), personidList);


		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
