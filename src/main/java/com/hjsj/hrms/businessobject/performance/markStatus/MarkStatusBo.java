package com.hjsj.hrms.businessobject.performance.markStatus;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.LazyDynaMap;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:MarkStatusBo.java</p>
 * <p>Description:展示打分状态</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 09:41:14</p>
 * @author JinChunhai
 * @version 1.0
 */

public class MarkStatusBo 
{
	private Connection conn=null;
	private RowSet frowset=null;
	private UserView userView=null;
	
	public MarkStatusBo(Connection conn)
	{
		this.conn=conn;
	}
	
	public MarkStatusBo(Connection conn,UserView auserView)
	{
		this.conn=conn;
		this.userView=auserView;
	}
	
	/**
	 * 得到考核计划列表
	 * @model //  0：绩效考核  1：民主评测
	 * @return
	 */
	public ArrayList getCheckPlanList(UserView userview,boolean isPerformanceManager,String model,String consoleType,String busitype)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list=new ArrayList();
		String sqlStr = "";
		try
		{
			ExamPlanBo bo = new ExamPlanBo(this.conn);
			StringBuffer sql=new StringBuffer("");
			sql.append("select plan_id,object_type,name,status,create_user,parameter_content,"+Sql_switcher.isnull("a0000", "999999")+" as norder ");
			sql.append(" from per_plan where status>=3 and status<>7 ");
			
		//	if(busitype==null || busitype.trim().length()<=0)
		//		busitype = "0";
			
			if(busitype!=null && busitype.trim().length()>0 && "0".equalsIgnoreCase(busitype))
			{
				sql.append(" and ( busitype is null or busitype='' or busitype = '" + busitype + "') ");
				sqlStr = " and ( busitype is null or busitype='' or busitype = '" + busitype + "') and status>=3 and status<>7 ";
			}else if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			{
				sql.append(" and busitype = '" + busitype + "' ");
				sqlStr = " and busitype = '" + busitype + "' and status>=3 and status<>7 ";
			}else
				sqlStr = " and status>=3 and status<>7 ";
			
			sql.append("order by norder asc,plan_id desc");
																	
	//		if(!isPerformanceManager&&model.equals("0"))  //如果不是绩效主管
	//			sql+=" and plan_id in (select distinct plan_id from per_mainbody where mainbody_id='"+userview.getUserId()+"') ";
			HashMap map = bo.getPlansByUserView(userview, sqlStr);
			this.frowset=dao.search(sql.toString());
			LoadXml loadXml=new LoadXml();
			AnalysePlanParameterBo appb=new AnalysePlanParameterBo(this.conn);
			Hashtable ht_table=appb.analyseParameterXml();
		//	String rightCtrlByPerObjType="true";
		//	if(ht_table!=null&&ht_table.get("rightCtrlByPerObjType")!=null)
		//		rightCtrlByPerObjType=(String)ht_table.get("rightCtrlByPerObjType");
			while(this.frowset.next())
			{
				int status=this.frowset.getInt("status");
				String desc="";
				if(status==3)
					desc="("+ResourceFactory.getProperty("lable.performance.status.issueed")+")";
				if(status==4)
					desc="("+ResourceFactory.getProperty("lable.performance.status.ongoing")+")";
				if(status==5)
					desc="("+ResourceFactory.getProperty("lable.performance.status.pause")+")";
				if(status==6)
					desc="("+ResourceFactory.getProperty("lable.performance.status.implement")+")";
				if(status==7)
					desc="("+ResourceFactory.getProperty("lable.performance.status.finished")+")";
				if(status==8)
					desc="("+ResourceFactory.getProperty("performance.plan.distribute")+")";
				String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
                String performanceType=loadXml.getPerformanceType(xmlContent);
                String object_type=this.frowset.getString("object_type");
                String create_user=this.frowset.getString("create_user");
                if(model.equals(performanceType))
                {
                	if(map.get(this.frowset.getString("plan_id"))==null)
                		continue;
                	if("1".equals(consoleType) && (busitype==null || busitype.trim().length()<=0 || "0".equalsIgnoreCase(busitype))) // 业务平台进入才限制
                	{
                    	if("2".equals(object_type))//人员计划
                    	{
                    		if((!userview.hasTheFunction("3260207")) && (!userview.getUserName().equalsIgnoreCase(create_user)))
                       			continue;
                    	}else{
                    		if((!userview.hasTheFunction("3260208")) && (!userview.getUserName().equalsIgnoreCase(create_user)))
                	    		continue;
                    	}
                	}
					CommonData aCommonData=new CommonData(PubFunc.encrypt(this.frowset.getString("plan_id")),this.frowset.getString("plan_id")+"."+this.frowset.getString("name")+desc);
					list.add(aCommonData);
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
		
	}
	
	/**
	 * 得到集合中第一条记录的id
	 * @param list
	 * @return
	 */
	public String getFirstRecordID(ArrayList list)
	{
		String ID="";
		if(list.size()>0)
		{
			CommonData aCommonData=(CommonData)list.get(0);
			ID=aCommonData.getDataValue();
		}
		return ID;
	}
	
	
	/**
	 * 取得计划下考核 主体/对象 的部门列表
	 * @param planid
	 * @param selectFashion
	 * @return
	 */
	public ArrayList getDepartMentList(String planid,String selectFashion)
	{
		ArrayList list=new ArrayList();
		try
		{
			if(planid!=null&&planid.length()>0)
			{
				int object_type=getObjectType(planid);
				String column="mainbody_id";
				if("2".equals(selectFashion))
					column="object_id";
				CommonData data=new CommonData("0",ResourceFactory.getProperty("hire.jp.pos.all"));
				list.add(data);
				
				String sql="";
				if(!("2".equals(selectFashion)&&(object_type==1||object_type==3))){
					sql="select distinct c.codeitemid,c.codeitemdesc  from  (select distinct "+column+" from per_mainbody where plan_id="+planid+") a,usra01 b,organization c "
						+" where a."+column+"=b.a0100 and b.e0122=c.codeitemid";
				}
				else
				{
					sql="select distinct c.codeitemid,c.codeitemdesc  from  (select distinct "+column+" from per_mainbody where plan_id="+planid+") a,organization c "
					+" where a."+column+"=c.codeitemid";
				}
				
				ContentDAO dao=new ContentDAO(this.conn);
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					data=new CommonData(this.frowset.getString("codeitemid"),this.frowset.getString("codeitemdesc"));
					list.add(data);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 取得 考核主体信息表记录 的说明原因信息
	 * @param planid
	 * @param objectid
	 * @param mainbodyID
	 * @return
	 */
	public String[]  getPerMainBodyDesc(String planid,String objectid,String mainbodyID,int objectType)
	{
		String desc="";
		String status="0";
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			String sql="select description,status from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'";
			if(objectType==1||objectType==3||objectType==4)
			{
				if(objectid.equals(mainbodyID))
					sql="select description,status from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id=(select mainbody_id from per_mainbody where  plan_id="+planid+" and object_id='"+objectid+"'  and body_id=-1)";
			}
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				desc=Sql_switcher.readMemo(this.frowset,"description");
				status=this.frowset.getString("status");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return new String[]{desc,status};
	}
	
	//填写说明原因
	public void SetDesc(String planid,String objectid,String mainbodyID,String desc,String username,String body_id)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			// desc=desc.replaceAll("@#@","\r\n");
			 RowSet rowSet=dao.search("select status from per_mainbody where plan_id="+planid+" and mainbody_id='"+mainbodyID+"'  and object_id='"+objectid+"'");
			 if(rowSet.next())
			 {
			//	dao.update("update per_mainbody set description='"+desc+"' where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'");
				String sql="update per_mainbody set description=? where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'";
				ArrayList list = new ArrayList();
				list.add(desc);
				dao.update(sql,list);
			 }
			 else   //主要针对目标管理
			 {
				   RecordVo mainbody_vo=getSelfVo(mainbodyID,"Usr");
					
					RecordVo vo=new RecordVo("per_mainbody");
					IDGenerator idg = new IDGenerator(2, this.conn);
					String id = idg.getId("per_mainbody.id");
					vo.setInt("id",Integer.parseInt(id));
					vo.setString("b0110", mainbody_vo.getString("b0110"));
					vo.setString("e0122", mainbody_vo.getString("e0122"));
					vo.setString("e01a1", mainbody_vo.getString("e01a1"));
					vo.setString("object_id",objectid);
					vo.setString("mainbody_id",mainbodyID);
					vo.setString("a0101",username);
					vo.setInt("body_id", Integer.parseInt(body_id));
					vo.setInt("plan_id", Integer.parseInt(planid));
					vo.setInt("status",0);
					vo.setString("description", desc);
					dao.addValueObject(vo);
				 
			 }
		//	 stmt.close();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public RecordVo getSelfVo(String object_id,String dbname)
	{
		RecordVo vo=new RecordVo(dbname.toLowerCase()+"a01");
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			vo.setString("a0100",object_id);
			vo=dao.findByPrimaryKey(vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	/**
	 * 保存或修改 考核主体信息表中记录的状态 和 说明原因
	 * @param planid
	 * @param objectid
	 * @param mainbodyID
	 * @param desc
	 * @param status
	 * @param operater 1:打分状态（考核对象）3：打分状态（主体）  2：打分
	 */
	public void saveOrUpdateDesc_status(String planid,String objectid,String mainbodyID,String desc,String status,String operater)
	{
		//ContentDAO dao=new ContentDAO(this.conn);
		DbSecurityImpl dbS = new DbSecurityImpl();
		PreparedStatement stmt=null;
		try
		{
			//desc=desc.replaceAll("@#@","\r\n");
			String sql="";
			if(!"4".equals(operater))
			{
				sql="update per_mainbody set description=?,status=? where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'";
				//dao.update("update per_mainbody set description='"+desc+"',status="+status+" where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'");
			}
			else
			{
				if("0".equals(status))
					sql="update per_mainbody set description=?  where plan_id="+planid+"  and mainbody_id='"+mainbodyID+"'";
				else
					sql="update per_mainbody set description=?,status=? where plan_id="+planid+"  and mainbody_id='"+mainbodyID+"'";
				
//				if(!this.userView.isSuper_admin()&&!this.userView.getGroupId().equals("1"))
				{
					RecordVo vo  = new RecordVo("per_plan");
					ContentDAO dao  = new ContentDAO(this.conn);
					vo.setInt("plan_id", Integer.parseInt(planid));
					vo=dao.findByPrimaryKey(vo);
					int object_type  = vo.getInt("object_type");
					
					// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
					String unit_id=userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
				    if(object_type==2)
			    	{
				    	if((!userView.isSuper_admin()) && (unit_id==null || "".equals(unit_id) || "UN".equalsIgnoreCase(unit_id)))
			    		{
			    	    	String priStrSql = InfoUtils.getWhereINSql(userView, "usr");
			    	    	if(priStrSql.length()>0)							
			    	    		sql+=" and per_mainbody.object_id in (select a0100 "+priStrSql+")";
			    		}
			    		else
			    		{
			    			if(unit_id.length()==3)
			    			{
			    				
			    			}
			    			else
			    			{
			    				String[] arr=unit_id.split("`");
			    				StringBuffer where=new StringBuffer();
			    				for(int j=0;j<arr.length;j++)
			    				{
			    					if(arr[j]==null|| "".equals(arr[j]))
			    						continue;
			    					String code=arr[j].substring(0,2);
			    					String value=arr[j].substring(2);
			    					if("UN".equalsIgnoreCase(code))
			    						where.append(" or UsrA01.b0110 like '"+value+"%'");
			    					else
			    						where.append(" or UsrA01.e0122 like '"+value+"%'");
			    				}
			    				sql+=" and per_mainbody.object_id in ( select a0100 from USRA01 where ("+where.toString().substring(3)+"))";
			    			}
			    		}
			    	}
	    			else
		    		{
	    				if((!userView.isSuper_admin()) && (unit_id==null || "".equals(unit_id) || "UN".equalsIgnoreCase(unit_id)))
			    		{
			    	    	String code=userView.getManagePrivCode();
			    	    	if(code==null|| "".equals(code))
			    	    		sql+=" 1=2 ";
			    	    	else
			    	    	{
			    	    		String value=userView.getManagePrivCodeValue();
			    	    		if("UN".equalsIgnoreCase(code))
			    	    		{
			    	    			sql+=" and per_mainbody.object_id like '"+(value==null?"":value)+"%'";
			    	    		}
			    	    	}
			    		}
			    		else
			    		{
			    			if(unit_id.length()==3)
			    			{
			    				
			    			}
			    			else
			    			{
			    				String[] arr=unit_id.split("`");
			    				StringBuffer where=new StringBuffer();
			    				for(int j=0;j<arr.length;j++)
			    				{
			    					if(arr[j]==null|| "".equals(arr[j]))
			    						continue;
			    					String code=arr[j].substring(0,2);
			    					String value=arr[j].substring(2);
			    					if("UN".equalsIgnoreCase(code))
			    						where.append(" or per_mainbody.object_id like '"+value+"%' ");//and a.codesetid='UN' 
			    					else
			    						where.append(" or per_mainbody.object_id like '"+value+"%'  ");//and a.codesetid='UM'
			    				}
			    				sql+=" and ("+where.toString().substring(3)+")";
			    			}
			    		}
					
		    		}
				}
			}
			 stmt=this.conn.prepareStatement(sql);
			 stmt.setString(1,desc);
			 if(!("4".equals(operater)&& "0".equals(status)))
				 stmt.setInt(2,Integer.parseInt(status));
			 // 打开Wallet
			 dbS.open(this.conn, sql);
			 stmt.execute();
			 
			 if("4".equals(status))
			 {
				 ContentDAO dao=new ContentDAO(this.conn);
				 dao.update("update per_mainbody set  know_id=null,whole_grade_id=null where plan_id="+planid+" and object_id='"+objectid+"' and mainbody_id='"+mainbodyID+"'");
			 }
			// stmt.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(stmt);
			try {
				// 关闭Wallet
				dbS.close(this.conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public int getObjectType(String plan_id)
	{
		int objectType=0;
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			if(plan_id!=null&&plan_id.length()>0)
			{
				this.frowset=dao.search("select object_type from per_plan where plan_id="+plan_id);
				if(this.frowset.next())
					objectType=this.frowset.getInt("object_type");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return objectType;
	}
	
	
	/**
	 * 取得计划 是否包含目标 和 报告的标记
	 * @param plan_id
	 * @return 0:无目标,无报告 1:有报告无目标 2有目标 有报告
	 */
	public String getPlanFlag(String plan_id)
	{
		String flag="0";
		try
		{
			if(plan_id==null|| "".equals(plan_id.trim()))
				return flag;
			ContentDAO dao=new ContentDAO(this.conn);
			int object_type=0;
			RowSet rowSet=dao.search("select object_type from per_plan where  plan_id="+plan_id);
			if(rowSet.next())
				object_type=rowSet.getInt("object_type");
			
		//	if(object_type==2)
			{
				
				LoadXml loadxml=new LoadXml(this.conn,plan_id);
				Hashtable htxml=new Hashtable();		
				htxml=loadxml.getDegreeWhole();
				String noteIdioGoal=((String)htxml.get("noteIdioGoal")).toLowerCase();	//显示个人目标
				String SummaryFlag=((String)htxml.get("SummaryFlag")).toLowerCase();
				if("true".equalsIgnoreCase(noteIdioGoal)|| "true".equalsIgnoreCase(SummaryFlag))
					flag="1";
				if("true".equalsIgnoreCase(noteIdioGoal)&& "true".equalsIgnoreCase(SummaryFlag))
					flag="2";
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 取得打分统计表信息集合
	 * @param planID  考核计划
	 * @param selectFashion  // 查询方式 1:按考核主体  2:考核对象
	 * @param department      //部门
	 * @param name            姓名
	 * @param isflag          0:无目标,无报告 1:有报告无目标 2有目标 有报告
	 * @return list
	 */
	public ArrayList getMarkStatusList(String planID,UserView userView,boolean isPerformanceManager,String selectFashion,String department,String name,String isflag)
	{    
		ArrayList list=new ArrayList();
		if(planID==null||planID.length()<=0)
			return list;
		ContentDAO dao=new ContentDAO(this.conn);
		RecordVo vo = getPlanVo(planID);
		int object_type = vo.getInt("object_type");
		try
		{
			
			ArrayList mainBodyIDList=new ArrayList();	
			StringBuffer sql=new StringBuffer("");
			String unit_id=userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			/**考核主体根据考核对象的范围来限制*/
			if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion))
			{
				sql.append("select distinct pm.mainbody_id a0100,pm.A0101,");
                sql.append(Sql_switcher.isnull("pm.b0110","''")+" b0110,");
                sql.append(Sql_switcher.isnull("pm.e0122","''")+" e0122,");
                sql.append(Sql_switcher.isnull("pm.e01a1","''")+" e01a1,");
                sql.append("UsrA01.a0000 a0000,"+Sql_switcher.isnull("pb.opt","0")+" opt  from per_mainbody pm");
				// 需要per_plan_body的opt字段，根据该值判断页面上显示“重新打分”还是“重新确认” by 刘蒙
				sql.append(" LEFT JOIN per_plan_body pb ON pm.body_id = pb.body_id AND pm.plan_id = pb.plan_id left join UsrA01 ");
				sql.append(" on pm.mainbody_id=Usra01.a0100 where pm.object_id in(select object_id from per_object po where plan_id="+planID+" ");
				if("1".equals(selectFashion))
					sql.append(getUserViewPrivWhere(userView)+") ");   // 考核主体根据考核对象的范围来限制 					
				else
				{					
					sql.append(getUserViewPrivMainWhere(userView)+") ");   // 预警考核主体根据考核主体的范围来限制 
					if(vo.getInt("method")==2)
						sql.append(" and pm.body_id in (select body_id from per_plan_body where plan_id="+planID+" and isgrade<>'1')  ");
				}
				sql.append(" and pm.plan_id="+planID+" ");
			}
			else
			{
				if(object_type==2){
					//xus 19/12/17 【56141】V77绩效自助：考评打分，打分状态，查询对象为考核对象，查询时后台报b0110无效
					sql.append("select distinct object_id a0100,UsrA01.A0101,UsrA01.b0110 b0110,UsrA01.e0122 e0122,UsrA01.e01a1 e01a1,UsrA01.a0000 a0000  from per_object,UsrA01 ");
					sql.append(" where per_object.object_id=UsrA01.a0100 and per_object.plan_id="+planID+" ");
					/**在绩效中，超级用户有操作单位，按操作单位走，没有操作单位按管理范围走，管理范围默认为全部*/
					if(unit_id==null|| "".equals(unit_id)|| "UN".equalsIgnoreCase(unit_id))//没有操作单位
					{
						if(userView.isSuper_admin())
						{
							String code=userView.getManagePrivCode();
							if(code!=null&&code.trim().length()>0)
							{
								String codevalue=userView.getManagePrivCodeValue();
								if("UN".equalsIgnoreCase(code))
								{
									sql.append(" and (");
									sql.append(" UsrA01.b0110 like '"+codevalue+"%'");
									if(codevalue==null|| "".equals(codevalue))
										sql.append(" or Usra01.b0110 is null ");
									sql.append(")");
								}
								else if("UM".equalsIgnoreCase(code))
								{
									sql.append(" and (");
									sql.append(" UsrA01.e0122 like '"+codevalue+"%'");
									if(codevalue==null|| "".equals(codevalue))
										sql.append(" or Usra01.e0122 is null ");
									sql.append(")");
								}
							}
							else
							{
								
							}
						}
						else
						{
							String priStrSql = InfoUtils.getWhereINSql(userView, "usr");
			    	    	sql.append(" and UsrA01.a0100 in (select a0100 "+priStrSql+")");
						}
					}else
					{
						if(unit_id.length()==3)
		    			{
		    				
		    			}
		    			else
		    			{
		    				String[] arr=unit_id.split("`");
		    				StringBuffer where=new StringBuffer();
		    				for(int j=0;j<arr.length;j++)
		    				{
		    					if(arr[j]==null|| "".equals(arr[j]))
		    						continue;
		    					String code=arr[j].substring(0,2);
		    					String value=arr[j].substring(2);
		    					if("UN".equalsIgnoreCase(code))
		    						where.append(" or UsrA01.b0110 like '"+value+"%'");
		    					else
		    						where.append(" or UsrA01.e0122 like '"+value+"%'");
		    				}
		    				sql.append(" and ("+where.toString().substring(3)+")");
		    			}
					}
				}
				else
				{
					sql.append("select distinct object_id a0100,a.codeitemdesc A0101 ");
					sql.append(" from per_object,organization a  where per_object.object_id=a.codeitemid and per_object.plan_id="+planID+" ");
					if(unit_id==null|| "".equals(unit_id)|| "UN".equalsIgnoreCase(unit_id))//没有操作单位
					{
						if(userView.isSuper_admin())
						{
							String code=userView.getManagePrivCode();
							if(code!=null&&code.trim().length()>0)
							{
								String codevalue=userView.getManagePrivCodeValue();
								if("UN".equalsIgnoreCase(code))
								{
									sql.append(" and ((a.codeitemid like '"+(codevalue==null?"":codevalue)+"%'");
			    	    			if(codevalue==null|| "".equals(codevalue))
			    	    			{
			    	    				sql.append(" or a.codeitemid is null or a.codeitemid=''");
			    	    			}
			    	    			sql.append("))");
								}
								else if("UM".equalsIgnoreCase(code))
								{
									sql.append(" and ((a.codeitemid like '"+(codevalue==null?"":codevalue)+"%'");
			    	    			if(codevalue==null|| "".equals(codevalue))
			    	    			{
			    	    				sql.append(" or a.codeitemid is null or a.codeitemid=''");
			    	    			}
			    	    			sql.append("))");
								}
							}
							else
							{
								
							}
						}
						else
						{
							String code=userView.getManagePrivCode();
							if(code!=null&&code.trim().length()>0)
							{
								String codevalue=userView.getManagePrivCodeValue();
								if("UN".equalsIgnoreCase(code))
								{
									sql.append(" and ((a.codeitemid like '"+(codevalue==null?"":codevalue)+"%'");
			    	    			if(codevalue==null|| "".equals(codevalue))
			    	    			{
			    	    				sql.append(" or a.codeitemid is null or a.codeitemid=''");
			    	    			}
			    	    			sql.append("))");
								}
								else if("UM".equalsIgnoreCase(code))
								{
									sql.append(" and ((a.codeitemid like '"+(codevalue==null?"":codevalue)+"%'");
			    	    			if(codevalue==null|| "".equals(codevalue))
			    	    			{
			    	    				sql.append(" or a.codeitemid is null or a.codeitemid=''");
			    	    			}
			    	    			sql.append("))");
								}
							}else
							{
								sql.append(" and 1=2 ");
							}
						}
					}else
					{
						if(unit_id.length()==3)
		    			{
		    				
		    			}
		    			else
		    			{
		    				String[] arr=unit_id.split("`");
		    				StringBuffer where=new StringBuffer();
		    				for(int j=0;j<arr.length;j++)
		    				{
		    					if(arr[j]==null|| "".equals(arr[j]))
		    						continue;
		    					String code=arr[j].substring(0,2);
		    					String value=arr[j].substring(2);
		    					if("UN".equalsIgnoreCase(code))
		    						where.append(" or a.codeitemid like '"+value+"%'");
		    					else
		    						where.append(" or a.codeitemid like '"+value+"%'");
		    				}
		    				sql.append(" and ("+where.toString().substring(3)+")");
		    			}
					}
				}
			}
			if(!("2".equals(selectFashion)&&(object_type==1||object_type==3||object_type==4))){
				if(!"0".equals(department))
				{
					if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion))
						sql.append(" and pm.e0122 like '"+department+"%'");
					else
			    		sql.append(" and UsrA01.e0122 like '"+department+"%'");
				}
				if(name.trim().length()>0)
				{
					if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion))
						sql.append(" and pm.A0101 like '%"+name.trim()+"%'");
					else
				    	sql.append(" and UsrA01.A0101 like '%"+name.trim()+"%'");
				}
				if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion))
					sql.append(" order by b0110,e0122,a0000");
				else
					sql.append(" order by b0110,e0122,a0000");
			}
			else {
				if(!"0".equals(department))
					sql.append(" and object_id like '"+department+"%'");
				sql.append(" order by a0100");
			}
			this.frowset=dao.search(sql.toString());
			HashMap map=new HashMap();
			while(this.frowset.next())
			{
				mainBodyIDList.add(this.frowset.getString("a0100"));
				HashMap tempMap=new HashMap();
				String a_href="<a href='/performance/markStatus/markStatusList.do?b_edit=edit&encryptParam="+PubFunc.encrypt("operater=4&planID="+planID+"&mainbodyID="+this.frowset.getString("a0100")+"")+"' >";
				if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion))
					tempMap.put("userName",a_href+this.frowset.getString("A0101")+"</a>");
				else
					tempMap.put("userName",this.frowset.getString("A0101"));
				if(!("2".equals(selectFashion)&&(object_type==1||object_type==3||object_type==4))){
					if(this.frowset.getString("e0122")!=null)
						tempMap.put("e0122",AdminCode.getCodeName("UM",this.frowset.getString("e0122")));
					else
						tempMap.put("e0122","");
				}
				if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion)) { // 按照考核主体查询的时候
					// per_plan_body.opt by 刘蒙
					int pbOpt = this.frowset.getInt("opt");
					tempMap.put("pbOpt", pbOpt == 1 ? "重新确认" : "重新打分");
				}
				map.put(this.frowset.getString("a0100"),tempMap);
			}
			
			String sql1="";
			if(object_type==2)
			{
				sql1 = "select m.*,ppb.isgrade,ppb.opt,a.a0101 object_name  from per_mainbody m"
						+ " left join per_plan_body ppb on ppb.plan_id = m.plan_id and ppb.body_id = m.body_id" // 是否打分需要isgrade和opt来判断 lium
						+ ",usra01 a where m.object_id=a.a0100 and m.plan_id="
						+ planID;
				sql.setLength(0);
				
				String base = "a";
				if("$1".equalsIgnoreCase(selectFashion))
					base = "m";				
		    	if(unit_id==null|| "".equals(unit_id)|| "UN".equalsIgnoreCase(unit_id))
		    	{
		    		if(userView.isSuper_admin())
		    		{
		    			String code=userView.getManagePrivCode();
						if(code!=null&&code.trim().length()>0)
						{
							String codevalue=userView.getManagePrivCodeValue();
							if("UN".equalsIgnoreCase(code))
							{
								sql.append(" and (("+base+".b0110 like '"+(codevalue==null?"":codevalue)+"%'");
		    	    			if(codevalue==null|| "".equals(codevalue))
		    	    			{
		    	    				sql.append(" or "+base+".b0110 is null or "+base+".b0110=''");
		    	    			}
		    	    			sql.append("))");
							}
							else if("UM".equalsIgnoreCase(code))
							{
								sql.append(" and (("+base+".e0122 like '"+(codevalue==null?"":codevalue)+"%'");
		    	    			if(codevalue==null|| "".equals(codevalue))
		    	    			{
		    	    				sql.append(" or "+base+".e0122 is null or "+base+".e0122=''");
		    	    			}
		    	    			sql.append("))");
							}
						}
						else
						{
							
						}
		    		}
		    		else
		    		{
		    			 String priStrSql = InfoUtils.getWhereINSql(userView, "usr");
				    	 sql.append(" and a.a0100 in (select a0100 "+priStrSql+")");
		    		}
		    	   
		    	}
		    	else
		    	{
		    		if(unit_id.length()==3)
		    		{
		    				
		    		}
		    		else
		    		{
		    			String[] arr=unit_id.split("`");
		    			StringBuffer where=new StringBuffer();
		    			for(int j=0;j<arr.length;j++)
		    			{
		    				if(arr[j]==null|| "".equals(arr[j]))
		    					continue;
		    				String code=arr[j].substring(0,2);
		    				String value=arr[j].substring(2);
		    				if("UN".equalsIgnoreCase(code))
		    					where.append(" or "+base+".b0110 like '"+value+"%'");
		    				else
		    					where.append(" or "+base+".e0122 like '"+value+"%'");
		    			}
		    			sql.append(" and ("+where.toString().substring(3)+")");
		    		}
		    	}
		    	
				sql1+=" "+sql.toString();
			}
			else
			{
				sql1="select m.*,ppb.isgrade,ppb.opt,a.codeitemdesc object_name  from per_mainbody m"
						+ " left join per_plan_body ppb on ppb.plan_id = m.plan_id and ppb.body_id = m.body_id"
						+ ",organization a where m.object_id=a.codeitemid  and m.plan_id="+planID;
				sql.setLength(0);
				
				String unitB0110 = "a.codeitemid";
				String unitE0122 = "a.codeitemid";
				if("$1".equalsIgnoreCase(selectFashion))
				{
					unitB0110 = "m.b0110";
					unitE0122 = "m.e0122";
				}
				
				if(unit_id==null|| "".equals(unit_id)|| "UN".equalsIgnoreCase(unit_id))
		    	{
		    	    String code=userView.getManagePrivCode();
		    	    if(code==null|| "".equals(code)){
		    	    	if(!userView.isSuper_admin())
		    	        	sql.append(" and 1=2 ");
		    	    }else
		    	    {
		    	    	String value=userView.getManagePrivCodeValue();
		    	    	if("UN".equalsIgnoreCase(code))
		    	    	{
		    	    		sql.append(" and (("+unitB0110+" like '"+(value==null?"":value)+"%'");
		    	    		if(value==null|| "".equals(value))
		    	    		{
		    	    			sql.append(" or "+unitB0110+" is null or "+unitB0110+"=''");
		    	    		}
		    	    		sql.append("))");
		    	    	}
		    	    	if("UM".equalsIgnoreCase(code))
		    	    	{
		    	    		sql.append(" and (("+unitE0122+" like '"+(value==null?"":value)+"%'");
		    	    		if(value==null|| "".equals(value))
		    	    		{
		    	    			sql.append(" or "+unitE0122+" is null or "+unitE0122+"=''");
		    	    		}
		    	    		sql.append("))");
		    	    	}
		    	    }
		    	}
		    	else
		    	{
		    		if(unit_id.length()==3)
		    		{
		    				
		    		}
		    		else
		    		{
		    			String[] arr=unit_id.split("`");
		    			StringBuffer where=new StringBuffer();
		    			for(int j=0;j<arr.length;j++)
		    			{
		    				if(arr[j]==null|| "".equals(arr[j]))
		    					continue;
		    				String code=arr[j].substring(0,2);
		    				String value=arr[j].substring(2);
		    				if("UN".equalsIgnoreCase(code))
		    					where.append(" or "+unitB0110+" like '"+value+"%'");
		    				else
		    					where.append(" or "+unitE0122+" like '"+value+"%'");
		    			}
		    			sql.append(" and ("+where.toString().substring(3)+")");
		    		}
		    	}
				sql1+=sql.toString();
			}
			this.frowset=dao.search(sql1);
			String columnName="object_name";
			if("2".equals(selectFashion))
				columnName="a0101";
			
			while(this.frowset.next())
			{
				String a0100="";
				if("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion))
					a0100=this.frowset.getString("mainbody_id");
				else 
					a0100=this.frowset.getString("object_id");
				HashMap tempMap=(HashMap)map.get(a0100);
				if(tempMap==null)
					continue;
				/**=0 or =null 没打分=1保存未提交，=2提交*/
				String  status=this.frowset.getString("status");
				if(status==null)
					status="0";
				
				// 根据上下文逻辑，不打分的主体status为4 lium
				int isgrade = frowset.getInt("isgrade"); // per_plan_body.isgrade(是否打分)
				int opt = frowset.getInt("opt"); // per_plan_body.opt(打分或确认标识)
                //不参与打分 或者 确认时，应为不打分主体 haosl 2019年7月17日
                status = (isgrade ==1 || opt == 1) ? "4" : status;
				
				String a_href="";
				String a_href_end="、";
				if(("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion)) && (("4".equals(status) || "7".equals(status))))
				{
					a_href_end="</a>、";
					a_href="<a href='/performance/markStatus/markStatusList.do?b_edit=edit&encryptParam="+PubFunc.encrypt("operater=1&status="+status+"&planID="+planID+"&objectID="+this.frowset.getString("object_id")+"&mainbodyID="+this.frowset.getString("mainbody_id")+"")+"' >";
				}
				else if(("1".equals(selectFashion) || "$1".equalsIgnoreCase(selectFashion)) && "0".equals(status))
				{
					a_href_end="</a>、";
					a_href="<a href='/performance/markStatus/markStatusList.do?b_edit=edit&encryptParam="+PubFunc.encrypt("operater=1&status=0&planID="+planID+"&objectID="+this.frowset.getString("object_id")+"&mainbodyID="+this.frowset.getString("mainbody_id")+"")+"' >";
				}
				if(tempMap.get(status)!=null)
				{
					
					String aName=(String)tempMap.get(status);
					
					if("7".equals(status)|| "4".equals(status)|| "0".equals(status))
						aName+=a_href+this.frowset.getString(columnName)+a_href_end;
					else
						aName+=this.frowset.getString(columnName)+"、";
					
					tempMap.put(status,aName);
				}
				else
				{
					if("7".equals(status)|| "4".equals(status)|| "0".equals(status))
						tempMap.put(status,a_href+this.frowset.getString(columnName)+a_href_end);
					else
						tempMap.put(status,this.frowset.getString(columnName)+"、");
				}
				map.put(a0100,tempMap);
			}
			
			
			for(Iterator t=mainBodyIDList.iterator();t.hasNext();)
			{
				String noMark="";//不打
				String marking="";
				String notMark="";//未打
				String marked="";
				
				String mainbody_id=(String)t.next();
				HashMap a_map=(HashMap)map.get(mainbody_id);
				LazyDynaMap dynaBean1 = new LazyDynaMap();
				String sss=(String)a_map.get("userName");
				dynaBean1.set("userName",(String)a_map.get("userName"));
				dynaBean1.set("e0122",a_map.get("e0122")==null?"":(String)a_map.get("e0122"));
				dynaBean1.set("object_id",PubFunc.encrypt(mainbody_id));
				dynaBean1.set("pbOpt", a_map.get("pbOpt")); // by 刘蒙
				//未打分
				noMark=(String)a_map.get("0");
				//正在打分
				marking=(String)a_map.get("1");
				if(marking==null)
					marking="";
				if(a_map.get("3")!=null&&((String)a_map.get("3")).length()>0) {
					marking += (String) a_map.get("3");//打分完成的也算正在打分的
				}
				if(a_map.get("8")!=null&&((String)a_map.get("8")).length()>0){
					marking += (String) a_map.get("8");//打分完成的也算正在打分的
				}
				//不打分
				{
					if(a_map.get("7")!=null)
						notMark+=(String)a_map.get("7");
					if(a_map.get("4")!=null)
						notMark+=(String)a_map.get("4");
				
				}
				//已打分
				marked=(String)a_map.get("2");
				if(noMark!=null)
				{
					dynaBean1.set("noMark",noMark);
				}
				else
				{
					dynaBean1.set("noMark","");
				}
				if(marking!=null)
				{
					dynaBean1.set("marking",marking);
				}
				else
				{
					dynaBean1.set("marking","");
				}
				if(notMark!=null)
				{
					dynaBean1.set("notMark",notMark);
				}
				else
				{
					dynaBean1.set("notMark","");
				}
				if(marked!=null)
				{
					dynaBean1.set("marked",marked);
				}
				else
				{
					dynaBean1.set("marked","");
				}
				//System.out.println(sss+"---"+noMark+"---"+marking+"---"+notMark+"---"+marked);
				if((noMark==null|| "".equals(noMark))&&(marking==null|| "".equals(marking))&&(notMark==null|| "".equals(notMark))&&(marked==null|| "".equals(marked)))
						continue;
				list.add(dynaBean1);
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		//插入目标 报告状态
		if(!"0".equals(isflag)&& "2".equals(selectFashion))
		{
			HashMap unA0100Map=getUnManager(planID);
			HashMap stateMap=getObjectGoal_SummaryState(planID,Integer.parseInt(isflag));
			LazyDynaMap dynaMap=null;
			for(int i=0;i<list.size();i++)
			{
				dynaMap=(LazyDynaMap)list.get(i);
				String object_id=(String)dynaMap.get("object_id");
				Pattern pattern = Pattern.compile("[0-9]*");
		        Matcher isNum = pattern.matcher(object_id);
		        if(!isNum.matches()) {
		        	object_id=PubFunc.decryption(object_id);
		        }
				if(object_type==1||object_type==3||object_type==4)
				{		
					if(unA0100Map.get(object_id)!=null)
						object_id=(String)unA0100Map.get(object_id);
				}
				
				if(stateMap.get("usr/"+object_id)!=null)
				{
					LazyDynaBean abean=(LazyDynaBean)stateMap.get("usr/"+object_id);
					if("1".equals(isflag))  //  0:无目标,无报告 1:有报告无目标 2有目标 有报告
					{
						if(abean.get("s")!=null)
							dynaMap.set("s",(String)abean.get("s"));
						else
							dynaMap.set("s","没填写");
					}
					else if("2".equals(isflag))
					{
						if(abean.get("s")!=null)
							dynaMap.set("s",(String)abean.get("s"));
						else
							dynaMap.set("s","没填写");
						if(abean.get("g")!=null)
							dynaMap.set("g",(String)abean.get("g"));
						else
							dynaMap.set("g","没填写");
					}
				}
				else
				{
					if("1".equals(isflag))  //  0:无目标,无报告 1:有报告无目标 2有目标 有报告
					{
						dynaMap.set("s","没填写");
					}
					else if("2".equals(isflag))
					{
						dynaMap.set("s","没填写");
						dynaMap.set("g","没填写");
					}
				}
			}
		}
		return list;
	}
	
	
	public HashMap getUnManager(String plan_id)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select mainbody_id,object_id from per_mainbody where plan_id="+plan_id+"  and body_id=-1   ");
			while(rowSet.next())
			{
				map.put(rowSet.getString("object_id"),rowSet.getString("mainbody_id"));
			//	a0100=rowSet.getString(1);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 取得计划下考核对象 绩效报告 , 绩效目标的状态
	 * @param plan_id
	 * @param flag   2:报告,目标  1:报告
	 * @return
	 */
	public HashMap getObjectGoal_SummaryState(String plan_id,int flag)
	{
		HashMap map=new HashMap();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			//绩效报告
			RowSet rowSet=dao.search("select distinct a0100,nbase,state from per_article where plan_id="+plan_id+" and Article_type=2 order by a0100,nbase,state desc");
			String a0100="";
			String nbase="";
			String state="0";  //0编辑  1提交  2批准  3驳回
			while(rowSet.next())
			{
				String a_a0100=rowSet.getString("a0100");
				String n_base=rowSet.getString("nbase");
				String a_state=rowSet.getString("state");
				if(a0100.length()==0)
				{
					a0100=a_a0100;
					state=a_state;
					nbase=n_base;
				}
				
				if(!(a_a0100.equalsIgnoreCase(a0100)&&n_base.equalsIgnoreCase(nbase)))
				{
					LazyDynaBean abean=new LazyDynaBean();
					getStateBean("s",state,abean);
					map.put(nbase.toLowerCase()+"/"+a0100,abean);
					
					a0100=a_a0100;
					state=a_state;
					nbase=n_base;
				}
			}
			LazyDynaBean abean=new LazyDynaBean();
			getStateBean("s",state,abean);
			map.put(nbase.toLowerCase()+"/"+a0100,abean);
			
			//绩效目标
			if(flag==2)
			{
				rowSet=dao.search("select distinct a0100,nbase,state from per_article where plan_id="+plan_id+" and Article_type=1 order by a0100,nbase,state desc");
				a0100="";
				nbase="";
				state="0";  //0编辑  1提交  2批准  3驳回
				while(rowSet.next())
				{
					String a_a0100=rowSet.getString("a0100");
					String n_base=rowSet.getString("nbase");
					String a_state=rowSet.getString("state");
					if(a0100.length()==0)
					{
						a0100=a_a0100;
						state=a_state;
						nbase=n_base;
					}
					
					if(!(a_a0100.equalsIgnoreCase(a0100)&&n_base.equalsIgnoreCase(nbase)))
					{
						LazyDynaBean a_bean=null;
						if(map.get(nbase.toLowerCase()+"/"+a0100)!=null)
							a_bean=(LazyDynaBean)map.get(nbase.toLowerCase()+"/"+a0100);
						else
							a_bean=new LazyDynaBean();
						getStateBean("g",state,a_bean);
						map.put(nbase.toLowerCase()+"/"+a0100,a_bean);
						
						a0100=a_a0100;
						state=a_state;
						nbase=n_base;
					}
				}
				LazyDynaBean a_bean=null;
				if(map.get(nbase.toLowerCase()+"/"+a0100)!=null)
					a_bean=(LazyDynaBean)map.get(nbase.toLowerCase()+"/"+a0100);
				else
					a_bean=new LazyDynaBean();
				getStateBean("g",state,a_bean);
				map.put(nbase.toLowerCase()+"/"+a0100,a_bean);
				
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	// flag  s:绩效报告  g:绩效目标
	public void getStateBean(String flag,String state,LazyDynaBean abean)
	{
		String desc="正编辑";
		if("0".equals(state))
			desc="正编辑";
		else if("1".equals(state))
			desc="已提交";
		else if("2".equals(state))
			desc="已批准";
		else if("3".equals(state))
			desc="已驳回";
		abean.set(flag,desc);
	}
	
	
	
	/**
	 * 得到指标说明文件的扩展名
	 * @param planid
	 * @return
	 */
	public String getIndexExplanExt(String planid)
	{
		String ext_name="";
		String sql="select file_ext from per_plan where plan_id="+planid;
	
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				ext_name=this.frowset.getString("file_ext");				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ext_name;
	}
	
	
	public void showIndexExplain(String planid,OutputStream output_stream)
	{
		
		String sql="select thefile,file_ext from per_plan where plan_id="+planid;
		//String sql="select ext,Ole from UsrA00 where A0100='00000003' and i9999=2 ";
		
		ResultSet rs = null;	
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			rs=dao.search(sql);
            if (rs.next()) {
            	byte[] buffer=rs.getBytes("thefile");                   
            	output_stream.write(buffer);
            	output_stream.flush();
            
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	PubFunc.closeResource(rs);
        	PubFunc.closeResource(output_stream);
        }	
	}
	public ArrayList getSubmitObject(String mainbodyid,String plan_id,UserView view)
    {
    	ArrayList list = new ArrayList();
    	try
    	{
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer();
    		sql.append("select a.object_id,a.a0101 from per_object a,per_mainbody b where b.mainbody_id='"+mainbodyid+"'");
    		sql.append(" and b.plan_id="+plan_id+" and b.status=2 and a.object_id=b.object_id and a.plan_id=b.plan_id");
    		RecordVo vo = new RecordVo("per_plan");
    		vo.setInt("plan_id", Integer.parseInt(plan_id));
    		vo=dao.findByPrimaryKey(vo);
    		int object_type=vo.getInt("object_type");
    		
    		// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
    		String unit_id=view.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
    		if(object_type==2)
    		{
//				if(!(view.isSuper_admin()||view.getGroupId().equals("1")))
		    	{
		    		if(!view.isSuper_admin()&&(unit_id==null || "".equals(unit_id.trim()) || "UN".equalsIgnoreCase(unit_id)))
		    		{
		    	    	String priStrSql = InfoUtils.getWhereINSql(view, "usr");
		    	    	if(priStrSql.length()>0)						
		    	    		sql.append(" and a.object_id in (select a0100 "+priStrSql+")");
		    		}
		    		else
		    		{
		    			if(unit_id.length()==3)
		    			{
		    				
		    			}
		    			else
		    			{
		    				String[] arr=unit_id.split("`");
		    				StringBuffer where=new StringBuffer();
		    				for(int j=0;j<arr.length;j++)
		    				{
		    					if(arr[j]==null|| "".equals(arr[j]))
		    						continue;
		    					String code=arr[j].substring(0,2);
		    					String value=arr[j].substring(2);
		    					if("UN".equalsIgnoreCase(code))
		    						where.append(" or a.b0110 like '"+value+"%'");
		    					else
		    						where.append(" or a.e0122 like '"+value+"%'");
		    				}
		    				if(where.toString().length()>0)
		    			    	sql.append(" and ("+where.toString().substring(3)+")");
		    			}
		    		}
		    	}
			
			}
			else
			{
//				if(!(view.isSuper_admin()||view.getGroupId().equals("1")))
		    	{
		    		if((!view.isSuper_admin()) && (unit_id==null || "".equals(unit_id) || "UN".equalsIgnoreCase(unit_id)))
		    		{
		    	    	String code=view.getManagePrivCode();
		    	    	if(code==null|| "".equals(code))
		    	    		sql.append(" 1=2 ");
		    	    	else
		    	    	{
		    	    		String value=view.getManagePrivCodeValue();
		    	    		if("UN".equalsIgnoreCase(code))
		    	    		{
		    	    			sql.append(" and ((a.object_id like '"+(value==null?"":value)+"%'");
		    	    			sql.append("))");
		    	    		}
		    	    		if("UM".equalsIgnoreCase(code))
		    	    		{
		    	    			sql.append(" and ((a.object_id like '"+(value==null?"":value)+"%'");
		    	    			sql.append("))");
		    	    		}
		    	    	}
		    		}
		    		else
		    		{
		    			if(unit_id.length()==3)
		    			{
		    				
		    			}
		    			else
		    			{
		    				String[] arr=unit_id.split("`");
		    				StringBuffer where=new StringBuffer();
		    				for(int j=0;j<arr.length;j++)
		    				{
		    					if(arr[j]==null|| "".equals(arr[j]))
		    						continue;
		    					String code=arr[j].substring(0,2);
		    					String value=arr[j].substring(2);
		    					if("UN".equalsIgnoreCase(code))
		    						where.append(" or a.object_id like '"+value+"%'");
		    					else
		    						where.append(" or a.object_id like '"+value+"%'");
		    				}
		    				sql.append(" and ("+where.toString().substring(3)+")");
		    			}
		    		}
		    	}
			}
    		RowSet rs = dao.search(sql.toString());
    		while(rs.next())
    		{
    			String object_id= rs.getString("object_id");
    			CommonData cd = new CommonData();
    			cd.setDataValue(plan_id+"/"+mainbodyid+"/"+object_id);
    			cd.setDataName(rs.getString("a0101"));
    			list.add(cd);
    		}
    	} 
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return list;
    }
	public void setStatus(String ids)
	{
		try
		{
			String[] arr = ids.split("`");
			ArrayList list = new ArrayList();
			ContentDAO  dao = new ContentDAO(this.conn);
			for(int i=0;i<arr.length;i++)
			{
				String temp=arr[i];
				if(temp==null|| "".equals(temp))
					continue;
				String[] sub_arr=temp.split("/"); // 354/00000049/00000028
				if(sub_arr.length==3)
				{
					String plan_id = sub_arr[0];
					String mainbody_id = sub_arr[1];
					String object_id = sub_arr[2];
					
					String sql = "update per_mainbody set status=1,score=null where plan_id="+plan_id+" and mainbody_id='"+mainbody_id+"'";
					sql+=" and object_id='"+object_id+"'";
					list.add(sql);

					// ###################################### 更新per_object开始 ######################################
					RowSet rs = null;
					StringBuffer poSql = new StringBuffer("SELECT id,score_process FROM per_object");
					poSql.append(" WHERE plan_id=?");
					poSql.append(" AND object_id=?");
					int po_pk = 0;
					String score_process = "";
					rs = dao.search(poSql.toString(), Arrays.asList(new Object[] { new Integer(plan_id), object_id }));
					if (rs.next()) {
						po_pk = rs.getInt("id");
						score_process = Sql_switcher.readMemo(rs, "score_process");
					}
					
					RecordVo po_vo = new RecordVo("per_object");
					po_vo.setInt("id", po_pk);
					po_vo = dao.findByPrimaryKey(po_vo);
					
					Map args = new HashMap();
					args.put("plan_id", plan_id);
					args.put("mainbody_id", mainbody_id);
					args.put("object_id", object_id);
					args.put("score_process", score_process);
					args.put("dao", dao);
					po_vo.setString("score_process", editScoreProcessXML(args));
					dao.updateValueObject(po_vo);
					// ###################################### 更新per_object结束 ######################################
					
					//重新打分后更新待办：需要重新打分的人重新收到待办 chent 20150928 start
					String receiver = "Usr" + mainbody_id;
					String ext_flag = "PERPF_" + plan_id;
					PendingTask pe = new PendingTask();
					RowSet rs1=dao.search("select distinct pending_title,pending_url,ext_flag from t_hr_pendingtask where pending_type='33' and ext_flag like '"+ext_flag+"%' and receiver='"+receiver+"'");
					while(rs1.next()){
						//改成创建新的代办任务，清除旧任务，以满足第三方代办 zhanghua 2018-10-18
						LazyDynaBean bean = new LazyDynaBean();
						bean.set("title", rs1.getString("pending_title"));
						bean.set("url", rs1.getString("pending_url"));
						bean.set("oper", "start");
						String flag = "4";
						if(("PERPF_" + plan_id+"_SELF").equals(rs1.getString("ext_flag"))) {
							flag="7";
						}
						LazyDynaBean _bean= PerformanceImplementBo.updatePendingTask(this.conn, this.userView, receiver,plan_id,bean,flag);
						if("add".equals(_bean.get("flag"))){
							pe.insertPending("PER"+_bean.get("pending_id"),"P",rs1.getString("pending_title"),this.userView.getDbname()+this.userView.getA0100(),receiver,rs1.getString("pending_url"),0,1,"重新打分",this.userView);
						}
						

					}

					//重新打分后更新待办：需要重新打分的人重新收到待办 chent 20150928 end
				}
			}
			dao.batchUpdate(list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * “重新打分”或“重新确认”时在score_process里追加“退回”记录
	 * @param args 用到的参数：plan_id object_id mainbody_id score_process
	 * @return 转换成字符串格式的xml
	 * @throws Exception 如果发生了数据库访问错误
	 * @author 刘蒙
	 * @since 2014-05-06
	 */
	public String editScoreProcessXML(Map args) throws Exception {
		String plan_id = (String) args.get("plan_id");
		String object_id = (String) args.get("object_id");
		String mainbody_id = (String) args.get("mainbody_id");
		String score_process = (String) args.get("score_process");
		
		// 得到将要被清除的主体名称,和其对应的opt(per_mainbody)
		ContentDAO dao = (ContentDAO) args.get("dao");
		RowSet rs = null;
		StringBuffer mbSQL = new StringBuffer("SELECT mb.A0101,pb.opt FROM per_mainbody mb ");
		mbSQL.append("LEFT JOIN per_plan_body pb ON pb.plan_id = mb.plan_id AND pb.body_id = mb.body_id ");
		mbSQL.append("WHERE mb.mainbody_id = ? AND mb.plan_id = ?");
		
		String mainbody_name = ""; // 被退回的主体名称
		int mainbody_opt = 0; // 主体对应的opt
		
		try {
			rs = dao.search(mbSQL.toString(), Arrays.asList(new Object[] { mainbody_id, plan_id }));
			if (rs.next()) {
				mainbody_name = rs.getString("A0101");
				mainbody_opt = rs.getInt("opt");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		
		Document doc = null;
		Element root = null;
		if (score_process != null && score_process.trim().length() > 0) {
			// 读取文档，如果存在的话
			doc = PubFunc.generateDom(score_process);
			root = doc.getRootElement();
		} else {
			// 创建xml文档
			root = new Element("root");
			doc = new Document(root);
		}
		
		// record节点
		Element record = new Element("record");
		root.addContent(record);
		// opt_object属性
		record.setAttribute("opt_object", object_id);
		// name属性
		record.setAttribute("name", AdminCode.getCodeName("UN",userView.getUserOrgId()) + "/" // 单位
				+ AdminCode.getCodeName("UM",userView.getUserDeptId()) + "/" // 部门
				+ AdminCode.getCodeName("@K",userView.getUserPosId()) + "/" // 岗位
				+ userView.getUserFullName()); // 用户名
		// date属性Admincode
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		record.setAttribute("date", sdf.format(new Date()));
		// status属性
		record.setAttribute("status", "1");
		// status_desc属性
		StringBuffer descSB = new StringBuffer("退回").append(mainbody_name);
		descSB.append(mainbody_opt == 0 ? " 的打分" : " 的确认意见");
		record.setAttribute("status_desc", descSB.toString());
		// reason属性
		record.setAttribute("reason", "");
		// report_to属性
		record.setAttribute("report_to", mainbody_name);

		// 格式化输出
		XMLOutputter outputter = new XMLOutputter();
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		outputter.setFormat(format);
		
		return outputter.outputString(doc);
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivMainWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  pm.b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  pm.e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and pm.b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and pm.e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and pm.e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and pm.b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  po.b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or  po.e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			}
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and po.b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and po.e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and po.e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and po.b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
	
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 */
	public RecordVo getPlanVo(String plan_id)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
	
	public String isNull(String str)
    {
		if (str == null || str.trim().length()<=0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
		    str = "";
		return str;
    }

}
