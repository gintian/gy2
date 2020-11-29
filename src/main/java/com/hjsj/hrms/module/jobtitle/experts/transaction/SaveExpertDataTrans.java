package com.hjsj.hrms.module.jobtitle.experts.transaction;

import com.hjsj.hrms.module.jobtitle.experts.businessobject.ExpertsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 
* <p>Title:SaveExpertDataTrans </p>
* <p>Description: 专家保存</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Nov 26, 2015 9:21:35 AM
 */
public class SaveExpertDataTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		HashMap hm=this.getFormHM();
		ArrayList addlist=(ArrayList)hm.get("addrecord"); //新增的数据
		ArrayList updatelist=(ArrayList)hm.get("updaterecord"); //修改的数据
		
		ArrayList idarr = (ArrayList)hm.get("idarr");//表格的字段
		
		ExpertsBo bo = new ExpertsBo(this.frameconn,this.userView);
		
		ArrayList updFieldList=bo.getIdlist();//要修改的字段
		
		ArrayList fieldList=DataDictionary.getFieldList("w01",Constant.USED_FIELD_SET);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		//循环新增和修改的数据
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(addlist.size()>0){//新增外部专家
				ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
				for(int i=0;i<addlist.size();i++){
					MorphDynaBean bean=(MorphDynaBean)addlist.get(i);
					HashMap addmap = PubFunc.DynaBean2Map(bean);
					RecordVo resultVo = new RecordVo("w01");
					for(int j=0;j<updFieldList.size();j++){
						String fieldid = String.valueOf(updFieldList.get(j));//字段名
						if(!addmap.containsKey(fieldid)){
							continue;
						}
						Object obj = bean.get(fieldid);
						if("b0110".equals(fieldid)){
							String fieldvalue = obj.toString();
							if(fieldvalue!=null&&fieldvalue.toString().contains("`")){
								int index = fieldvalue.indexOf("`");
								fieldvalue = fieldvalue.substring(0,index);
								resultVo.setString(fieldid, fieldvalue);
							}else{
								resultVo.setString(fieldid, obj.toString());
							}
						}else{
							for(int k=0;k<fieldList.size();k++){
								FieldItem item=(FieldItem)fieldList.get(k);	
								String itemid=item.getItemid();//字段id
								String itemtype=item.getItemtype();//字段类型
								if(itemid.equals(fieldid)){
									if("D".equals(itemtype)){
										if(obj==null|| "".equals(obj)){
											Date fieldvalue = null;
											resultVo.setDate(fieldid, fieldvalue);
										}else{
											Date fieldvalue = null;
											try {
											fieldvalue = sdf.parse(String.valueOf(obj));
											resultVo.setDate(fieldid, fieldvalue);
											} catch (ParseException e) {
												e.printStackTrace();
											}
										}
									}else if("N".equals(itemtype)){
										if("".equals(obj)||obj==null){
											String fieldvalue = null;
											resultVo.setNumber(fieldid, fieldvalue);
										}else{
											resultVo.setNumber(fieldid, obj.toString());
										}
									}else if("A".equals(itemtype)){
										String fieldvalue = obj.toString();
										if(fieldvalue!=null&&fieldvalue.toString().contains("`")){
											int index = fieldvalue.indexOf("`");
											fieldvalue = fieldvalue.substring(0,index);
											resultVo.setString(fieldid, fieldvalue);
										}else{
											resultVo.setString(fieldid, obj.toString());
										}
									}
								}
							}
						}
					}
					IDGenerator idg = new IDGenerator(2,this.getFrameconn());
					String w0101 = idg.getId("W01.W0101");
					resultVo.setString("w0101", String.valueOf(Integer.valueOf(w0101)));
					resultVo.setDate("create_time", new Date());
					resultVo.setString("create_user", this.userView.getUserName());
					resultVo.setString("create_fullname", this.userView.getUserFullName());
//					String b0110 = this.userView.getUnitIdByBusi("9");
//					if(b0110.equals("UN`")){
//						resultVo.setString("b0110", "");
//					}else{
//						int index = b0110.indexOf("`");
//						b0110 = b0110.substring(2,index);
//						resultVo.setString("b0110", b0110);
//					}
					volist.add(resultVo);
				}	
				dao.addValueObject(volist);
			}
			
			if(updatelist.size()>0){//修改保存（外部）
				ArrayList<RecordVo> volist = new ArrayList<RecordVo>();
				for(int i=0;i<updatelist.size();i++){
					MorphDynaBean bean=(MorphDynaBean)updatelist.get(i);
					HashMap updatemap = PubFunc.DynaBean2Map(bean);
					RecordVo resultVo = new RecordVo("w01");
					for(int j=0;j<updFieldList.size();j++){
						String fieldid = String.valueOf(updFieldList.get(j));//字段名
						if(!updatemap.containsKey(fieldid)){
							continue;
						}
						Object obj = bean.get(fieldid);
						if("b0110".equals(fieldid)){
							String fieldvalue = obj.toString();
							if(fieldvalue!=null&&fieldvalue.toString().contains("`")){
								int index = fieldvalue.indexOf("`");
								fieldvalue = fieldvalue.substring(0,index);
								resultVo.setString(fieldid, fieldvalue);
							}else{
								resultVo.setString(fieldid, obj.toString());
							}
						}else{
							for(int k=0;k<fieldList.size();k++){
								FieldItem item=(FieldItem)fieldList.get(k);	
								String itemid=item.getItemid();//字段id
								String itemtype=item.getItemtype();//字段类型
								if(itemid.equals(fieldid)){
									
									if("w0109".equalsIgnoreCase(itemid)){//如果是可聘任标识，则同步到评委会和学科组的专家的聘任标识 haosl 20160831
										if(obj!=null){
											String w0101 = (String)bean.get("w0101");
											int index = obj.toString().indexOf("`");
											if(index>0){//有效字符串 如："1`是"
												String flag = obj.toString().substring(0,index);
												bo.syscExpertFlag(w0101,flag);//同步聘任标识
											}
										}
									}
									
									if("D".equals(itemtype)){
										if(obj==null|| "".equals(obj)){
											Date fieldvalue = null;
											resultVo.setDate(fieldid, fieldvalue);
										}else{
											Date fieldvalue = null;
											try {
											fieldvalue = sdf.parse(String.valueOf(obj));
											resultVo.setDate(fieldid, fieldvalue);
											} catch (ParseException e) {
												e.printStackTrace();
											}
										}
									}else if("N".equals(itemtype)){
										if("".equals(obj)||obj==null){
											String fieldvalue = null;
											resultVo.setNumber(fieldid, fieldvalue);
										}else{
											resultVo.setNumber(fieldid, obj.toString());
										}
									}else if("A".equals(itemtype)){
										String fieldvalue = obj.toString();
										if(fieldvalue!=null&&fieldvalue.toString().contains("`")){
											int index = fieldvalue.indexOf("`");
											fieldvalue = fieldvalue.substring(0,index);
											resultVo.setString(fieldid, fieldvalue);
										}else{
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
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
}
