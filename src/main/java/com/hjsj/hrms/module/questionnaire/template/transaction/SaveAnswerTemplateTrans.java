package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * <p>Title: SaveAnswerTemplateTrans </p>
 * <p>Description: 问卷答案保存</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-9-26 下午2:12:58</p>
 * @author hej
 * @version 1.0
 */
public class SaveAnswerTemplateTrans extends IBusiness {

	private Random rand =  new Random();
	@Override
    public void execute() throws GeneralException{
		String cip = (String)this.getFormHM().get("cip");
		String mainObject = (String)this.getFormHM().get("mainObject");
		String subObject = (String)this.getFormHM().get("subObject");
		String flag = (String)this.getFormHM().get("flag");//保存标识 1、保存 2.交卷
		mainObject = mainObject==null?"":mainObject;
		subObject = subObject==null?"":subObject;
		ContentDAO dao = new ContentDAO(frameconn);
		//是否需要删除本人已经答过的答案
		boolean cleanData = false;
		if("".equals(mainObject)){
			if(userView!=null){
				cleanData = true;
				mainObject = userView.getA0100();
			}
			if("".equals(mainObject)){
				mainObject = cip+System.currentTimeMillis()+rand.nextInt(1000);
			}
		}else{
			cleanData = true;
			mainObject = PubFunc.decrypt(mainObject);
			subObject = PubFunc.decrypt(subObject);
		}
		String qnid = (String)this.getFormHM().get("qnid");
		String planIdStr = (String)this.getFormHM().get("planid");
		if(!"".equals(planIdStr)||planIdStr!=null){
			planIdStr = PubFunc.decrypt(planIdStr);
		}
		planIdStr=Integer.toString(Integer.parseInt(planIdStr));   // changxy  planIdStr 解密后变为‘0000000xxx’ 
		int planId  = Integer.parseInt(planIdStr);
		//保存或提交时校验是否已经结束问卷 结束则取消保存或交卷
		//String sql = "select qnId from qn_plan where status='1' and planId='"+planid+"'";
		ArrayList list=new ArrayList();
		list.add(planId);
		try {
			RowSet rs=dao.search("select qnId from qn_plan where status='1' and planId=?", list);
			if(!rs.next()) {//保存时问卷是结束状态，数据不保存到数据库，直接返回
				return ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		
		int autocloseselected = Integer.parseInt((String)this.getFormHM().get("autocloseselected"));
		String autoclosevalue = (String)this.getFormHM().get("autoclosevalue");
		int closeCount = autoclosevalue==null||autoclosevalue.length()<1?0:Integer.parseInt(autoclosevalue);

		//答案数据
		DynaBean answer = (DynaBean)this.getFormHM().get("answer");
		//转Map方便操作
		HashMap answerMap = PubFunc.DynaBean2Map(answer);
		//zhangh 2020-1-20 作废的问卷，不再计入收集的份数
		if("2".equals(flag)&&answerMap!=null&&answerMap.size()>0){
			TemplateBo bo  = new TemplateBo();
			//更新收集份数，返回是否继续保存数据
			boolean continueSave = bo.updateRecoveryCount(autocloseselected, planId, closeCount, mainObject, userView, qnid, this.frameconn);
			//如果不继续，说明收集份数打到限制最大，自动关闭问卷，停止插入数据
			if(!continueSave)
				return;
		}
		DbWizard db = new DbWizard(this.frameconn);
		String sql;
		ArrayList valueList = new ArrayList();
		valueList.add(planIdStr);
		valueList.add(mainObject);
		if(StringUtils.isBlank(subObject)){
			subObject = " ";
		}
		valueList.add(subObject);

		try{
			//zhangh 有答案数据时才进行保存和提交操作，否则按作废处理
			if(answerMap.size()>0){
				//保存常规题(非矩阵题)
				String tableName = "qn_"+qnid+"_data";
				if(db.isExistTable("qn_"+qnid+"_data",false)){
					//删除重复数据
					if(cleanData){
						//zhangh 2020-1-17 【56366】V77问卷调查：oracle 图表分析和原始数据中的数据不一致
						//oracle数据库中，空字符和null是一回事，但是查询的时候要写字段is null
						sql = "delete "+tableName+" where planid=? and mainObject=? and " +Sql_switcher.isnull("subObject","' '") + "=? ";
						dao.delete(sql, valueList);
					}

					//保存试题
					RecordVo vo = new RecordVo(tableName);
					vo.setString("dataid", SaveAnswerTemplateTrans.getQuesAnswerNextDataId(tableName,dao));
					vo.setString("planid", planIdStr);
					vo.setString("mainobject", mainObject);
					vo.setString("subobject", subObject);
					vo.setString("cip", cip);
					vo.setInt("status", Integer.parseInt(flag));
					//answerMap中的key对应数据库的字段，直接放进去就行
					Object[] key = answerMap.keySet().toArray();
					for(int i=0;i<key.length;i++){
						Object value =  answerMap.get(key[i]);
						//如果value是List，说明是矩阵题，先跳过
						if((value instanceof List))
							continue;
						vo.setObject(key[i].toString().toLowerCase(),value.toString());
						answerMap.remove(key[i]);
					}

					dao.updateValueObject(vo);
				}

				//保存矩阵题数据
				tableName = "qn_matrix_"+qnid+"_data";
				if(answerMap.size()>0 && db.isExistTable(tableName,false)){
					//删除重复数据
					if(cleanData){
						sql = "delete "+tableName+" where planid=? and mainObject=? and subObject=? ";
						dao.delete(sql, valueList);
					}

					List matrixList = new ArrayList();
					Iterator ite = answerMap.keySet().iterator();
					while(ite.hasNext()){
						ArrayList matrixAnswer = (ArrayList)answerMap.get(ite.next());
						for(int i=0;i<matrixAnswer.size();i++){
							HashMap map = PubFunc.DynaBean2Map((DynaBean)matrixAnswer.get(i));
							//矩阵量表 后台score是str类型，转成数值
							if(map.containsKey("score"))
								map.put("score", new BigDecimal(map.get("score").toString()).setScale(2, BigDecimal.ROUND_HALF_UP));
							RecordVo resultVo = new RecordVo(tableName);
							resultVo.setString("planid", planIdStr);
							resultVo.setString("mainobject", mainObject);
							resultVo.setString("subobject", subObject);
							resultVo.setString("cip", cip);
							resultVo.setInt("status", Integer.parseInt(flag));
							for(Object key:map.keySet()){
								resultVo.setObject(key.toString().toLowerCase(),map.get(key));
							}
							matrixList.add(resultVo);
						}

					}

					dao.addValueObject(matrixList);
				}
			}

			//更新代办信息
			String receiver = "Usr"+mainObject;
			/*sql错误导致更新失败。另外添加旧程序兼容，Ext_flag存的是计划id，原来程序是 000000001格式，新程序是1，导致有时会找不到。  guodd 2019-06-05*/
			//zhangh 2020-1-15 【56151】V77问卷调查：收集配置，分享给内部人员，内部人员在系统首页的热点调查中答题后，仍然有这条数据
			//应该是预编译时将Ext_flag转成int时报错
			String pendingsql = "update t_hr_pendingtask set lasttime=?,pending_status=?  where receiver=? and "+Sql_switcher.isnull("Ext_flag","'0'")+"=? and pending_type=? ";
			PreparedStatement preSta = null;
			try{
				preSta = this.frameconn.prepareStatement(pendingsql);
				preSta.setDate(1, new java.sql.Date(System.currentTimeMillis()));
				if("1".equals(flag)){//保存
					preSta.setString(2,"3");
				}else{//提交
					preSta.setString(2,"1");
				}
				preSta.setString(3,receiver);
				preSta.setString(4,planIdStr);
				preSta.setString(5,"80");
				preSta.executeUpdate();
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(preSta);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	
	static synchronized public String getQuesAnswerNextDataId(String sequence_name,ContentDAO dao){
		
			ResultSet  rset = null;
			int dataid=0;
			try{
				
				String sql  ="";
				switch(Sql_switcher.searchDbServer()){
					 case Constant.MSSQL:
						 sql = "select max(CAST(dataid AS int)) as max from "+sequence_name;
						  break;
					 case Constant.ORACEL:
						 sql = "select max(to_number(dataid)) as max from "+sequence_name;
						  break;
				}
				
				rset=dao.search(sql);
					if(rset.next()){
						if(rset.getString("max")==null||"".equals(rset.getString("max"))){
							dataid = dataid+1;
						}else{
							dataid = Integer.parseInt(rset.getString("max"))+1;
						}	
					}
					
					sql = "insert into "+sequence_name+"(dataid) values ('"+dataid+"') ";
					dao.update(sql);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(rset);
			}
			return String.valueOf(dataid);
	}
	
}