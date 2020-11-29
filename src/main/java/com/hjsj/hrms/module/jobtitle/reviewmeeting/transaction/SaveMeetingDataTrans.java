package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * <p>Title:SaveMeetingDataTrans </p>
 * <p>Description: 评审会议数据保存类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2015-12-31</p>
 * @author liuy
 * @version 1.0
 * 
 */
@SuppressWarnings("serial")
public class SaveMeetingDataTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {

		ReviewMeetingBo bo = new ReviewMeetingBo(this.frameconn,this.userView);
		ArrayList addlist=(ArrayList)this.getFormHM().get("addrecord"); //新增的数据
		ArrayList updatelist=(ArrayList)this.getFormHM().get("updaterecord"); //修改的数据
		ArrayList updFieldList=bo.getIdlist();//要修改的字段
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		try{
			//循环新增的数据
			if(addlist.size()>0){//新增评审会议
				for(int i=0;i<addlist.size();i++){
					HashMap bean = PubFunc.DynaBean2Map((MorphDynaBean)addlist.get(i));
					RecordVo resultVo = new RecordVo("W03");
					for(int j=0;j<updFieldList.size();j++){
						String fieldid = String.valueOf(updFieldList.get(j));//字段名
						if("w0301".equals(fieldid)//新增不用循环处理会议编号
								||"w0315".equalsIgnoreCase(fieldid)//评委会参会人数不处理
								||"w0323".equalsIgnoreCase(fieldid)
								||"sub_committee_id".equalsIgnoreCase(fieldid)//二级单位不在此处更新
								||"committee_id".equalsIgnoreCase(fieldid))//高评委不在此处更新
							continue;
						try {							
							if(bean.get(fieldid)==null)
								continue;
						} catch (Exception e) {
							continue;
						}
						if(bean.get(fieldid) instanceof String){							
							String fieldvalue = (String)bean.get(fieldid);
							if("w0307".equals(fieldid)){
								if(!fieldvalue.matches("[0-9]+")){//届次为非正整数是赋为0
									fieldvalue="0";
								}
							}
							String dateType = bo.getDateType(fieldid);//得到日期格式
							if (!"".equals(dateType)&&StringUtils.isNotEmpty(fieldvalue)){//处理日期型指标
								resultVo.setDate(fieldid, DateUtils.getSqlDate(fieldvalue,dateType));
							}else if("b0110".equalsIgnoreCase(fieldid)){//所属机构
								String value = "";
								//当没有维护所属机构的时候才根据业务范围保存所属机构  haosl 2017-07-13  start
								if(StringUtils.isNotEmpty(fieldvalue)) {
									value = fieldvalue.split("`").length==0?"":fieldvalue.split("`")[0];//haosl 20160812
								}else{
									String b0110 = this.userView.getUnitIdByBusi("9");
									if("UN`".equals(b0110))
										value = "";
									else{
										int index = b0110.indexOf("`");
										value = b0110.substring(2,index);
									}
								}
									//当没有维护所属机构的时候才根据业务范围保存所属机构  haosl 2017-07-13  end
								resultVo.setString(fieldid, value);
							}else{
								if ("".equals(fieldvalue))//空值数据库里面插入null
									resultVo.setString(fieldid, null);
								else
									resultVo.setString(fieldid, fieldvalue);
							}
						}else if(bean.get(fieldid) instanceof Integer){
							int fieldvalue = (Integer)bean.get(fieldid);
							resultVo.setInt(fieldid, fieldvalue);
						}else if(bean.get(fieldid) instanceof Double){
							double fieldvalue = (Double)bean.get(fieldid);
							resultVo.setDouble(fieldid, fieldvalue);
						}else if(bean.get(fieldid)==null){
							resultVo.setString(fieldid, null);
						}
					}
					IDGenerator idg = new IDGenerator(2,this.getFrameconn());//序号生成器
					String w0301 = idg.getId("W03.W0301");//序号生成器生成w0301
					resultVo.setString("w0301", w0301);
					resultVo.setString("w0321", "01");//新增的评审会议默认为起草状态
					resultVo.setDate("create_time", new Date());
					resultVo.setString("create_user", this.userView.getUserName());
					resultVo.setString("create_fullname", this.userView.getUserFullName());
					
					String committee_id = bean.containsKey("committee_id")?(String)bean.get("committee_id"):"";
					String sub_committee_id = bean.containsKey("sub_committee_id")?(String)bean.get("sub_committee_id"):"";
					this.getFormHM().put("w0301", w0301);
					this.getFormHM().put("w0321", "01`起草");
					this.getFormHM().put("committee_id",committee_id);
					dao.addValueObject(resultVo);
					bo.saveCommitteePesons(PubFunc.encrypt(w0301), committee_id, sub_committee_id);
				}
				
				
			}
			//循环修改的数据
			if(updatelist.size()>0){//修改评审会议
				for(int i=0;i<updatelist.size();i++){
					HashMap bean = PubFunc.DynaBean2Map((MorphDynaBean)updatelist.get(i));
					RecordVo resultVo = new RecordVo("W03");
					for(int j=0;j<updFieldList.size();j++){
						String fieldid = String.valueOf(updFieldList.get(j));//字段名
						if("w0315".equalsIgnoreCase(fieldid)//评委会参会人数不处理
									||"w0323".equalsIgnoreCase(fieldid)//二级单位参会人数不处理
									||"sub_committee_id".equalsIgnoreCase(fieldid)//二级单位不在此处更新
									||"committee_id".equalsIgnoreCase(fieldid))//高评委不在此处更新
							continue;
						if("w0301".equals(fieldid))//加密数据取的时候加"_e"
							fieldid = fieldid + "_e";
						try {							
							if(bean.get(fieldid)==null)
								continue;
						} catch (Exception e) {
							continue;
						}
						if(bean.get(fieldid) instanceof String){
							String fieldvalue = (String)bean.get(fieldid);
							if("w0301_e".equals(fieldid)){
								fieldvalue = PubFunc.decrypt(fieldvalue);
								fieldid = "w0301";
							}
							if("w0307".equals(fieldid)){
								if(!fieldvalue.matches("[0-9]+")){//届次为非正整数是赋为0
									fieldvalue="0";
								}
							}
							String dateType = bo.getDateType(fieldid);//得到日期格式
							if (!"".equals(dateType)&&StringUtils.isNotEmpty(fieldvalue)){//处理日期型指标
								resultVo.setDate(fieldid, DateUtils.getSqlDate(fieldvalue,dateType));
							}else if("b0110".equalsIgnoreCase(fieldid)){//所属机构
								String value = "";
								if(StringUtils.isNotEmpty(fieldvalue)) {
									value = fieldvalue.split("`").length==0?"":fieldvalue.split("`")[0];//haosl 20160812
								}
								resultVo.setString(fieldid, value);
							}else{
								if ("".equals(fieldvalue))//空值数据库里面插入null
									resultVo.setString(fieldid, null);
								else
									resultVo.setString(fieldid, fieldvalue);
							}
						}else if(bean.get(fieldid) instanceof Integer){
							int fieldvalue = (Integer)bean.get(fieldid);
							resultVo.setInt(fieldid, fieldvalue);
						}else if(bean.get(fieldid) instanceof Double){
							double fieldvalue = (Double)bean.get(fieldid);
							resultVo.setDouble(fieldid, fieldvalue);
						}else if(bean.get(fieldid)==null){
							resultVo.setString(fieldid, null);
						}
						
					}
					//保存评委会或二级单位
					String committee_id = bean.containsKey("committee_id")?(String)bean.get("committee_id"):"";
					String sub_committee_id = bean.containsKey("sub_committee_id")?(String)bean.get("sub_committee_id"):"";
					String w0301 = (String)bean.get("w0301_e");
					dao.updateValueObject(resultVo);
					bo.saveCommitteePesons(w0301, committee_id, sub_committee_id);
				}	
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try {
				if(rs!=null)
					PubFunc.closeResource(rs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
