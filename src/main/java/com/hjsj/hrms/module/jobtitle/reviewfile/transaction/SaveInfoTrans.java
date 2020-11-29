package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.*;
/**
 * 上会材料  保存
 * @createtime August 24, 2015 9:07:55 PM
 * @author chent
 *
 */
public class SaveInfoTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rs = null;
		try
		{
			String groupIdStr = "";// 更新的学科组id。判断当前更新的数据中是否更新了学科组，如果重新分配了学科组则在保存后要做账号重新分配
			String w0301 = "";
			/** 数据保存 */
			ArrayList datalist=(ArrayList) this.getFormHM().get("savedata");
			StringBuffer sql = new StringBuffer();		
			StringBuffer str = new  StringBuffer();
			StringBuffer where = new  StringBuffer();
			ArrayList valueList = new ArrayList();
			String noeditStr = ",type,changestate,record_internalid,id,meetingname,checkproficient,professiongroup,agreeproportion," +
					"w0513,w0515,w0511,w0507,w0509,w0503_safe_e,w0501_safe_e,w0505,w0505_safe_e,w0301,w0321,nbasea0100_e,nbasea0100_1_e,w0301_safe_e,imgpath," +
					"subjectsagree,committeeagree,proficientagree,mvp_data_key,w0525,collegeagree,committeename,w0535_,w0536_,w0537_,w0517,w0521,w0571,group_1,group_2,group_3,group_4,";// 不可编辑指标
			sql.append("update w05 set ");
			for(int i=0;i<datalist.size();i++){
				DynaBean bean = (DynaBean) datalist.get(i);
				HashMap map = PubFunc.DynaBean2Map(bean);
				if(i==0){
					String w0301_safe_e = (String)map.get("w0301_safe_e");
					w0301 = PubFunc.decrypt(w0301_safe_e);
				}
				Iterator iter = map.entrySet().iterator();
				ArrayList<Object> list = new ArrayList();
				ArrayList wherelist = new ArrayList();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if(noeditStr.indexOf(","+key.toString().toLowerCase()+",")!=-1){//这几个字段不会被页面编辑
						continue;
					}
					
					if("w0501".equalsIgnoreCase(key.toString())){
						where.append(" "+key.toString());
						where.append("=?");
						wherelist.add(val.toString());
					} else if("group_id".equalsIgnoreCase(key.toString()) || "W0539".equalsIgnoreCase(key.toString()) || "W0541".equalsIgnoreCase(key.toString())){
						if(i == 0){
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
						}
						//不为空的话取值，为空保存空
						String value = null;
						if(val != null && val.toString().length() > 0){
							value = val.toString().split("`")[0];
						}
						list.add(value);
						
//						if("W0539".equalsIgnoreCase(key.toString()) || "W0541".equalsIgnoreCase(key.toString())){//分配了问卷，则置为非导入数据
//							if(!"0".equals(value)){
//								isImportData = false;
//							}
//						}
						
						if("group_id".equalsIgnoreCase(key.toString()) && val != null && val.toString().length() > 0){
							groupIdStr = groupIdStr + "'"+val.toString()+"',";
						}
					} else{
						if(i == 0){
							str.append(",");
							str.append(key.toString());
							str.append(" =?");
						}
						FieldItem fiem = (FieldItem)DataDictionary.getFieldItem(key.toString());
						if(fiem!=null && fiem.getCodesetid() != null && !"0".equals(fiem.getCodesetid()) && val.toString() != null && val.toString().length()>0){
							try{//有可不选，则值为"`","`"的话插入""
								list.add(val==null||val.toString().length()==0?val.toString():val.toString().split("`")[0]);
							}catch(Exception e){
								list.add("");
							}
						}else if(fiem!=null&& "D".equals(fiem.getItemtype())){
							Date date = null;
							if(val!=null && StringUtils.isNotBlank(val.toString())){
								val = val.toString().replaceAll("-","\\.");
							    date = DateUtils.getSqlDate(val.toString(),"yyyy.MM.dd");
							}
							list.add(date);
						}else if(fiem!=null&& "N".equals(fiem.getItemtype())){
							if(fiem.getDecimalwidth()==0){
								list.add(val==null || val.toString().length()==0?0:Integer.parseInt(val.toString()));
							}else{
								String value = PubFunc.DoFormatDecimal(val==null||val.toString().length()==0?"":val.toString(), fiem.getDecimalwidth());
								list.add(value.length()==0?null:PubFunc.parseDouble(value));
							}
						}else{
							list.add(val==null||val.toString().length()==0?"":val.toString());
						}
					}
					
				}
//				if(!isImportData){//非导入数据
//					list.add("0000");
//				}
				list.addAll(wherelist);
				valueList.add(list);
				if(i == 0){
//					if(!isImportData){//非导入数据
//						str.append(",");
//						str.append("w0525");
//						str.append(" =?");
//					}
					
					sql.append(str.substring(1));
					sql.append(" where ");
					sql.append(where);
				}
			}
			dao.batchUpdate(sql.toString(), valueList);
			
			ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
			if(StringUtils.isNotEmpty(w0301)){
				reviewFileBo.asyncPersonNum(w0301,2);// 同步学科组人数
				reviewFileBo.asyncStatus(w0301);//设置评审状态
			}
			this.getFormHM().put("msg","保存成功！");
		}
		catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
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
}
