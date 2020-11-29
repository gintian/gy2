package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
* 
* 类名称：AllBatchCopyTrans   
* 类描述：批量复制下级目标评分  
* 创建人：akuan   
* 创建时间：Aug 9, 2013 11:30:26 AM   
* 修改人：akuan   
* 修改时间：Aug 9, 2013 11:30:26 AM   
* 修改备注：   
* @version    
*
 */
public class AllBatchCopyTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String records=(String) this.getFormHM().get("records");//records   考核对象-计划号-状态 /.....
		String record[]=records.replaceAll("／", "/").split("/");
		ArrayList planList=new ArrayList();//所有计划号
		HashMap map=new HashMap();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String info="none";
		try{
			for(int i=0;i<record.length;i++){
			    String[] temp = record[i].split("-");
			    temp[0]=PubFunc.decrypt(temp[0].trim());
                temp[1]=PubFunc.decrypt(temp[1].trim());
                
				if (!planList.contains(temp[1]))
				{
					planList.add(temp[1]);
				} 
				if(map.get(temp[1])!=null){
					map.put((temp[1]), map.get((temp[1]))+"/"+temp[0]);//map(计划号,全部考核对象)
				}else{
					map.put((temp[1]), temp[0]);
				}
			}
			LoadXml loadxml=null;
			Hashtable params=null;
			String level="";//主体分类等级
			String objectIds="";//一个计划内所有考核对象
			ArrayList allMainBodyList=new ArrayList();
			String mainBodyId="";//已提交打分最近的主体id
			String plan_id="";//计划号
			String userId=this.userView.getA0100();//操作用户id
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			HashMap levlMap=this.getLevelMap(dao);
			String seq="";
			for(int i=0;i<planList.size();i++){
				loadxml= new LoadXml(this.getFrameconn(), (String) planList.get(i));
				params = loadxml.getDegreeWhole();
				boolean bool=true;//优先按顺序号
				if("True".equalsIgnoreCase((String) params.get("AllowSeeAllGrade"))){//按顺序号评分 允许查看比评分人顺序低的主体评分
					bool=false;
					objectIds=(String) map.get(planList.get(i));
					String objectId[]=objectIds.replaceAll("／", "/").split("/");//考核对象id
					plan_id=(String) planList.get(i);
					for(int j=0;j<objectId.length;j++){
						seq=this.getMainbodySeq(dao, plan_id, objectId[j], userId);
						if("0".equals(seq)){
							continue;
						}
						mainBodyId=this.getLowerSeqMainBodyId(dao, seq,objectId[j], plan_id);
					    this.copyLastMainbodyEvaluation(dao, plan_id, mainBodyId,  objectId[j], userId, idg);
					    info="ok";
					}
				}
				if(bool&&"True".equalsIgnoreCase((String) params.get("allowSeeLowerGrade"))){//允许查看下级对考核主体评分
					objectIds=(String) map.get(planList.get(i));
					String objectId[]=objectIds.replaceAll("／", "/").split("/");//考核对象id
					plan_id=(String) planList.get(i);
					for(int j=0;j<objectId.length;j++){
						level=this.getBody_id(dao, plan_id, objectId[j],userId,levlMap);
						allMainBodyList=this.getLowerGradeList(dao, Integer.parseInt(level), objectId[j],plan_id);
						if(allMainBodyList.size()==0)
							continue;
						mainBodyId=(String) allMainBodyList.get(allMainBodyList.size()-1);//取最近的考核主体
						this.copyLastMainbodyEvaluation(dao, plan_id, mainBodyId,  objectId[j], userId, idg);
						info="ok";
					}
				}else{
					continue;
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("info", info);
		}
	}
	/**
	 * 根据 计划号planId 对象objectId 获得当前用户的主体分类等级
	 * @param dao
	 * @param planId
	 * @param objectId
	 * @return
	 * @throws GeneralException
	 */
	public String getBody_id(ContentDAO dao,String planId,String objectId,String userId,HashMap levelMap) throws GeneralException{
		String body_id="";
		String level="";
		String sql="select body_id from per_mainbody where plan_id='"+planId+"' and object_id='"+objectId+"' and mainbody_id='"+userId+"'";
		try {
			
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				body_id=this.frowset.getString("body_id");
			}
			this.frowset.close();
			if(levelMap.get(body_id)!=null&&!"".equals((String)levelMap.get(body_id)))
				level=(String)levelMap.get(body_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return level;
	}
/**
 * 取得下级考核主体的信息列表
 * @param dao
 * @param body_id // 5:本人 0：上上级 1：直接上级 2：同级 -1：第三级领导, -2：第四级领导 ,3下级；4下下级
 * @param objectId
 * @param planId
 * @return list
 * @throws GeneralException 
 */
	public ArrayList getLowerGradeList(ContentDAO dao,int level, String objectId,String planId) throws GeneralException {
		ArrayList list = new ArrayList();
		try {
			if (level == 5)
				return list;
			String level_str = "";
			switch (level) {
			case 1:
				level_str = "5,2";
				break;
			case 0:
				level_str = "5,1,2";
				break;
			case -1:
				level_str = "5,1,0,2";
				break;
			case -2:
				level_str = "5,1,0,-1,2";
				break;
			}

			if (level_str.length() == 0)
				return list;
			StringBuffer sql = new StringBuffer("");
			sql.append("select pm.*,pms.name from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id ");
			sql.append(" and pm.plan_id=" + planId + " and pm.object_id='"
					+ objectId + "' and  ");
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				sql.append("  pms.level_o");
			else
				sql.append("  pms.level ");
			sql.append(" in (" + level_str + ")");
			String cloumn = "level";
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				cloumn = "level_o";
			sql.append(" order by " + cloumn + " desc ");
			this.frowset = dao.search(sql.toString());
			LazyDynaBean abean = null;
			while (this.frowset.next()) {
//				abean = new LazyDynaBean();
//				abean.set("a0100", rowSet.getString("mainbody_id"));
//				abean.set("a0101", rowSet.getString("a0101"));
//				abean.set("bodyname", rowSet.getString("name"));
				if (this.frowset.getString("status") == null|| !"2".equals(this.frowset.getString("status")))//status0:未打分1:正在编辑2:已提交3:不打分  筛选已提交的
					continue;
//				abean.set("status", rowSet.getString("status"));
				list.add(this.frowset.getString("mainbody_id"));
			}
			this.frowset.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
/**
 * 将最近的下级主体对考核对象评分复制到当前操作人对考核对象的评分
 * @param dao
 * @param plan_id
 * @param mainbody_id
 * @param object_id
 * @param userId
 * @param idg
 * @throws GeneralException
 */
	public void copyLastMainbodyEvaluation(ContentDAO dao,String plan_id,String mainbody_id,String object_id,String userId,IDGenerator idg) throws GeneralException{
		String sql="delete from per_target_evaluation where plan_id='"+plan_id+"' and mainbody_id='"+userId+"' and object_id='"+object_id+"'";
		try {
			dao.delete(sql, new ArrayList());
			sql="select * from per_target_evaluation where plan_id='"+plan_id+"' and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'";
			this.frowset=dao.search(sql);
			ResultSetMetaData rsmd=this.frowset.getMetaData();
			int nColumn=rsmd.getColumnCount();
			ArrayList insertList=new ArrayList();
			ArrayList contentList=new ArrayList();
			String columnValue="";
			String 	columnName="";
			String columnType="";
			String insertSql="insert into per_target_evaluation( ";
			String valueSql=" )values(";
			int n=0;
			while(this.frowset.next()){
				contentList=new ArrayList();
				for(int i=1;i<=nColumn;i++){
					columnName=rsmd.getColumnName(i);
					columnType=rsmd.getColumnTypeName(i);
					if("id".equalsIgnoreCase(columnName)){
						columnValue=idg.getId("per_target_evaluation.id");
					}else if("mainbody_id".equalsIgnoreCase(columnName)){
						columnValue=userId;
					}
					else{
						columnValue=this.frowset.getString(columnName);
					}
					if("numeric".equalsIgnoreCase(columnType)&&(columnValue==null|| "0E-8".equals(columnValue))){
						columnValue="0";
					}
					if(n==0){
				    	if(i!=nColumn){
					    	insertSql+=columnName+",";
					    	valueSql+="?,";
				    	}else{
				    		insertSql+=columnName;
					    	valueSql+="?)";
				    	}

				    }
	
						contentList.add(columnValue);

					
				}
				insertList.add(contentList);
				n++;
				
			}
			sql=insertSql+valueSql;
			dao.batchInsert(sql, insertList);
			
			//修改总分
			sql="update per_mainbody set status='1',score=(select score from per_mainbody where plan_id='"+plan_id+"' and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"') where plan_id='"+plan_id+"' and mainbody_id='"+userId+"' and object_id='"+object_id+"'";
			dao.update(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	} 
	/**
	 * 获得主体分类号、主体分类等级的map
	 * @param dao
	 * @return
	 */
	public HashMap getLevelMap(ContentDAO dao)
	{
		HashMap map = new HashMap();
		try
		{
			String sql = "select body_id ,";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				sql+=" level_o ";
			else
				sql+=" level ";
			sql+=" as lv from per_mainbodyset";
			RowSet rs  = dao.search(sql);
			while(rs.next())
			{
				map.put(rs.getString("body_id"),rs.getString("lv"));
			}
			rs.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 按顺序号评分 获得当前操作人考核主体顺序号
	 * @param dao
	 * @param plan_id
	 * @param object_id
	 * @param userId
	 * @return
	 */
	public String getMainbodySeq(ContentDAO dao,String plan_id,String object_id ,String userId ) {
		String seq="";
		try {
			String _str = "level";
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				_str = "level_o";

			this.frowset = dao
					.search("select pm.*,pms."
							+ _str
							+ " from per_mainbody pm left join per_mainbodyset pms  on pm.body_id=pms.body_id  where pm.plan_id="
							+plan_id
							+ " and pm.object_id='" + object_id
							+ "' and pm.mainbody_id='"
							+ userId + "'");
			if (this.frowset .next()) {
				seq=this.frowset.getString("seq");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seq;
	}
	/**
	 * 取得下级考核主体的信息列表
	 * 
	 * @param property
	 *            // 5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
	 * @return
	 */
	public String getLowerSeqMainBodyId(ContentDAO dao,String seq, String object_id, String plan_id) {
		String mainBodyId="";
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append("select pm.*,pms.name from per_mainbody pm left join per_mainbodyset pms on pm.body_id=pms.body_id where ");
			sql.append(" pm.plan_id=" + plan_id + " and pm.object_id='"
					+ object_id + "'");
			sql.append(" and pm.seq<" + seq);
			sql.append(" order by pm.seq desc");
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				mainBodyId=this.frowset.getString("mainbody_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mainBodyId;
	}
}
