package com.hjsj.hrms.module.jobtitle.reviewfile.transaction;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.ReviewMeetingBo;
import com.hjsj.hrms.module.jobtitle.utils.JobtitleUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:ResultsArchivingTrans </p>
 * <p>Description: 上会材料-结果归档交易类</p>
 * <p>Company: hjsj</p> 
 * <p>create time: 2016-3-4</p>
 * @author liuy
 * @version 1.0
 */
@SuppressWarnings("serial")
public class ResultsArchivingTrans extends IBusiness {

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		String msg = "";//提示信息
		String type = (String)this.getFormHM().get("type");//1:未全部完成评审，直接归档  0||空需要判断
		ContentDAO dao = new ContentDAO(this.frameconn);
		ReviewFileBo bo = new ReviewFileBo(this.frameconn,userView);
		//从静态变量里读取评审结果归档方案内容
		HashMap hashMap = bo.getResultsArchivingConfig(JobtitleUtil.ZC_REVIEWARCHIVE_STR);	//haosl
		if(hashMap.size()==0){
			msg = "未配置评审结果归档方案，请及时联系管理员！";
			this.getFormHM().put("msg", msg);
			return;
		}
		
 	    //结果归档自动将评审会议状态修改为"结束"
		String w0301 = (String)this.getFormHM().get("w0301");
		if(w0301.indexOf("_")!=-1){
			w0301=w0301.substring(w0301.indexOf("_")+1);
			w0301 = PubFunc.decrypt(w0301);//评审会议编号
			RowSet rs = null;
			try {
				StringBuffer sql = new StringBuffer();
				if(!"1".equals(type)){//非1的时候需要判断
					ReviewMeetingBo meetingBo = new ReviewMeetingBo(this.frameconn, this.userView);
					HashMap<String, Boolean> enableSteps = new HashMap<String, Boolean>();//meetingBo.getEnableSteps(w0301, "", "");//获得会议启用的阶段
					sql.append("select W0511,W0303 from W05 left join W03 on w05.W0301 = w03.W0301");
					sql.append(" where W03.W0301='"+ w0301 +"'");
					
					//haosl 根据启用了那些阶段，判断是否完成投票 20170613 start
					String stepSql = "";
					if(enableSteps.get("step1")){//评委会
						stepSql+=Sql_switcher.isnull("W0559", "''") +"='' or"; 
					}
					if(enableSteps.get("step2"))//学科组
						stepSql+=" "+Sql_switcher.isnull("W0557", "''") +"='' or";
					if(enableSteps.get("step3"))//同行专家
						stepSql+=" "+ Sql_switcher.isnull("W0533", "''") +"='' or"; 
					if(enableSteps.get("step4"))//二级单位
						stepSql+=" "+ Sql_switcher.isnull("W0569", "''") +"='' or"; 
					
					if(stepSql.length()>0){
						stepSql = stepSql.substring(0, stepSql.length()-2);
						sql.append(" and ("+stepSql+")");
					}
					//haosl 根据启用了那些阶段，判断是否完成投票 20170613 end
					
					rs = dao.search(sql.toString());
					if(rs.next()){
						int i=1;
						msg=rs.getString("W0303") +"："+rs.getString("W0511");
						while(rs.next()){
							if(i<=5)
								msg +="、"+ rs.getString("W0511");
							i++;
						}
						msg+="等"+i+"人的职称申报未完成评审！是否继续归档？";
						this.getFormHM().put("msg", msg);
						this.getFormHM().put("type", "1");
						return;
					}
				}
				List<String> list = bo.getResultsArchivingList(JobtitleUtil.ZC_REVIEWARCHIVE_STR);//结果归档所有属性名称
				List<String> list2 = new ArrayList<String>();	//存放查询的字段
				for(int i=1;i<list.size();i++){
					String name = (String)hashMap.get(list.get(i));
					if(StringUtils.isNotEmpty(name)){//判断是否设置对应目的指标
						String itemid = list.get(i);
						list2.add(itemid);
					}
				}
				sql.setLength(0);//置空sql对象
				sql.append("select distinct W0503 from W05 left join W03 on w05.W0301=w03.W0301");
				rs=dao.search(sql.toString());//查询人员库
				ArrayList dbList = new ArrayList();
				while(rs.next())
					dbList.add(rs.getString("W0503"));
					if (dbList.size()>0) {//循环人员库
						for(int i=0; i<dbList.size(); i++){
							String tableName = dbList.get(i)+ (String)hashMap.get("fieldset");
							sql.setLength(0);//置空sql对象
							sql.append(" select * ");
//							if(select_sql.length()>0)
//								sql.append(","+select_sql.substring(0,select_sql.length()-1));
							sql.append(" from W05");
							sql.append(" left join W03 on w05.W0301 = w03.W0301 ");
							sql.append(" where W0503='"+ dbList.get(i) +"'  and W03.W0301='"+ w0301 +"'");
							rs = dao.search(sql.toString());
							while(rs.next()){//循环人员
								sql.setLength(0);//置空sql对象
								RecordVo vo = new RecordVo(tableName);

								// 如果是有本年的归档信息就更新，没有才插入 chent 20170603 start
								boolean insertOrUpdate = true;
								int i9999 = 0;
								String now_year_whereStr = "where a0100='"+ rs.getString("w0505") +"' and " + Sql_switcher.diffYears("createtime", Sql_switcher.sqlNow())+"=0";
								int max_now_year_i9999 = DbNameBo.getPrimaryKey(tableName, "i9999", now_year_whereStr, this.frameconn);//子集中本年的最大编号
								if(max_now_year_i9999 == 1){//本年的最大编号是1，说明还没有，需要插入。
									String whereStr = "where a0100='"+ rs.getString("w0505") +"'";
									i9999 = DbNameBo.getPrimaryKey(tableName, "i9999", whereStr, this.frameconn);//子集中最大编号
								} else {//本年的最大编号大于1,说存在记录。要更新
									insertOrUpdate = false;
									i9999 = max_now_year_i9999-1;
								}
								// 如果是有本年的归档信息就更新，没有才插入 chent 20170603 end
								vo.setString("a0100", rs.getString("w0505"));
								vo.setInt("i9999", i9999);
								vo.setDate("createtime", new Date());
								vo.setString("createusername", userView.getUserName());
								for(int j=0; j<list2.size() ;j++){//循环目的指标  	0是fieldset 1开始是子集的指标项 
									String field = (String)hashMap.get(list2.get(j)).toString().toLowerCase();
									if(!StringUtils.isEmpty(field)){//有可能不配置目的指标
										if("W0309".equalsIgnoreCase(list2.get(j))||"W0311".equalsIgnoreCase(list2.get(j)))
											vo.setDate(field, rs.getDate(list2.get(j)));
										else{
											String value = "";
											if("attendance_1".equalsIgnoreCase(list2.get(j))){//attendance_1:参会人数（评委会）
												int w0549 = rs.getInt("w0549");
												int w0551 = rs.getInt("w0551");
												int w0553 = rs.getInt("w0553");
												value = String.valueOf(w0549+w0551+w0553);
												
											} else if("attendance_2".equalsIgnoreCase(list2.get(j))) {// attendance_2：:参会人数（学科组）
												int w0543 = rs.getInt("w0543");
												int w0545 = rs.getInt("w0545");
												int w0547 = rs.getInt("w0547");
												value = String.valueOf(w0543+w0545+w0547);
												
											} else if("attendance_3".equalsIgnoreCase(list2.get(j))) {// attendance_3：:参会人数（同行专家）
												int w0527 = rs.getInt("w0527");
												int w0529 = rs.getInt("w0529");
												int w0531 = rs.getInt("w0531");
												value = String.valueOf(w0527+w0529+w0531);
												
											} else if("attendance_4".equalsIgnoreCase(list2.get(j))) {// attendance_4：:参会人数（二级单位）
												int w0563 = rs.getInt("w0563");
												int w0565 = rs.getInt("w0565");
												int w0567 = rs.getInt("w0567");
												value = String.valueOf(w0563+w0565+w0567);
												
											} else {
												value = rs.getString(list2.get(j));
											}
											vo.setString(field, value);
										}
									}
								}
								if(insertOrUpdate){
									dao.addValueObject(vo);
								} else {
									dao.updateValueObject(vo);
								}
							}
						}
						dao.update("update W03 set W0321 = '06' where W0301 = '"+ w0301 +"'");
						this.getFormHM().put("msg", "归档成功！");
					}
			} catch (Exception e) {
				this.getFormHM().put("msg", "归档失败！");
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}finally{
				if(rs!=null)
					PubFunc.closeResource(rs);
			}
		}else{		
			this.getFormHM().put("msg", "请选择一个会议再进行结果归档！");
			return;
		}
	}

}
