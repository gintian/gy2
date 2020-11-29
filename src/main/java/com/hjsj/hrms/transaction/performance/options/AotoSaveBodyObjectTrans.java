package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.*;

public class AotoSaveBodyObjectTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
	
		String planid = (String) this.getFormHM().get("plan_id");
		String object = "";
//		String flag = (String) hm.get("selType"); //1:手工选人 2:条件选人
//		hm.remove("selType");
		
		String accordByDepartment = "false";//条件选人 按部门匹配	
//		if(flag!=null && flag.equals("1"))
//			 accordByDepartment = "false";
//		else if(flag!=null && flag.equals("2"))
//		{
//			 accordByDepartment = (String) hm.get("accordByDepartment");
//		}
	//	hm.remove("accordByDepartment");
		
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(),this.userView);
	
		String str_sql = (String) this.getFormHM().get("str_sql");
//		if(flag!=null && flag.equals("2"))//条件选人 选择用户权限范围内的人 手工选人在选择时候就加以控制过了
//		{
//		}
			RecordVo vo=bo.getPerPlanVo(planid);
			int   object_type=vo.getInt("object_type");   //1部门 2：人员
			String plan_b0110=vo.getString("b0110");
			this.getFormHM().put("plan_b0110", plan_b0110);
			String queryA0100=(String)this.getFormHM().get("queryA0100");
		
			String whl = "" ;//根据用户权限先得到一个考核对象的范围
			String privWhl = bo.getPrivWhere(userView);
			whl+=privWhl;
			String orderSql=(String)this.getFormHM().get("orderSql");
			orderSql = SafeCode.decode(orderSql);
//			if(code!=null)
//			{
//				if(AdminCode.getCodeName("UN",code)!=null&&AdminCode.getCodeName("UN",code).length()>0)
//					whl+=" and b0110 like '"+code+"%'";
//				else if(AdminCode.getCodeName("UM",code)!=null&&AdminCode.getCodeName("UM",code).length()>0)
//					whl+=" and e0122 like '"+code+"%'";
//				
//			}
			HashMap organizationmap = new HashMap();
//			getOrganization(organizationmap);
			//自动匹配考核对象前，把原来非本人的考核对象全部删除。
			bo.aotoDelKhMainBody(""+planid);
		ArrayList	perObjectDataList=bo.getPerObjectDataList(planid,""+object_type,whl,orderSql,"");
		 ArrayList mainBodylist= this.getMainBodylist(planid);
		 HashMap  sampleMap = new HashMap();
		ArrayList factorList = bo.getFactor(planid, mainBodylist,object_type);
		bo.creatTempTable(this.userView.getUserName(), factorList,object_type);
		int  flag =0;//人员，组织
		//获得考核对象下的临时表中各指标对应的值
		HashMap compmap = new HashMap();
		bo.insertTempDate(this.userView.getUserName(), factorList, object_type,perObjectDataList,organizationmap,compmap,planid);
		//最后一步 自动分配考核对象，考核主体
		ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		YksjParser yp =null;
		
		
		HashSet aSet = (HashSet)factorList.get(0);
		 HashMap map =new HashMap();
		   for(Iterator t=aSet.iterator();t.hasNext();)
		    {
		    	String fieldname=((String)t.next()).trim();
		    	/* 判断该指标是否已被删除或还没构库 */
		    	FieldItem item=DataDictionary.getFieldItem(fieldname);	
		    	if(item==null)
				{
					continue;
				}
				if("0".equals(item.getUseflag()))
				{
					item.setUseflag("1");
				}
				
		    map.put(fieldname.toUpperCase(),item);
				
			}
		   String mainBodyType ="";//考核主体id
		   String  scope ="";
		   String b0110="";
		   String e0122="";
		   String tope0122="";
		   String sql="";
		   HashMap yksjParsermap= new HashMap();
		   FactorList factor_bo=null;
		   HashMap simplemap= new HashMap();
		   for(int j =0;j<mainBodylist.size();j++){

	    		 vo  = (RecordVo)mainBodylist.get(j);
	    		 mainBodyType = vo.getString("body_id");
	    		 String cond =vo.getString("cond");
					String cexpr =vo.getString("cexpr");
					scope = vo.getString("scope");
					if(cond==null||cond.trim().length()==0){
						
					}else{
					if(cexpr!=null&&cexpr.trim().length()>0){
						 factor_bo=new FactorList(cexpr,cond.toString().toUpperCase(),this.userView.getUserId(),map);
						   sql=factor_bo.getSingleTableSqlExpression("t#"+this.userView.getUserName()+"per_mainbody");
						   simplemap.put(mainBodyType, sql);
						
					}else{//高级带修改
						//addFactor2(aSet,bSet,cond,userView,object_type);
						
						
						yp = new YksjParser(userView ,alUsedFields,
								YksjParser.forNormal, YksjParser.LOGIC,YksjParser.forPerson , "Ht", "");
						yp.setCon(this.getFrameconn());
						yp.run_where(cond.trim());
						
					//	boolean b=yp.Verify_where(cond.trim());
						
						yksjParsermap.put(mainBodyType, yp);
					}
					}
	    		
	    	
		   }
		   sql ="";
		   StringBuffer sqlStr = new StringBuffer();
		   sqlStr.append("select a.* from ");
		    sqlStr.append("(select * from per_mainbodyset where status=1  and (body_type=0 or body_type is null))");
		    sqlStr.append(" a join per_plan_body b on a.body_id=b.body_id  and b.plan_id=");
		    sqlStr.append(planid);
		    sqlStr.append(" and b.body_id=5");
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    boolean pSelf= false;////本人的标志，有本人了，就不能再把自己以非本人的类别作为考核主体
		    try {
				this.frowset = dao.search(sqlStr.toString());
			    if(this.frowset.next())
			    	pSelf=true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		    
		    for (int i = 0; i < perObjectDataList.size(); i++)
		    {
		    	LazyDynaBean abean = (LazyDynaBean)perObjectDataList.get(i);
		    	object = (String)abean.get("object_id");
				 b0110 = 	(String)abean.get("b0110value");
				 e0122 = (String)abean.get("e0122value");
				
					 tope0122= (String)compmap.get(object+"khtopE0122");
				 
		    	for(int j =0;j<mainBodylist.size();j++){
		    		 vo  = (RecordVo)mainBodylist.get(j);
		    		 mainBodyType = vo.getString("body_id");
		    		 String cond =vo.getString("cond");
						String cexpr =vo.getString("cexpr");
						scope = vo.getString("scope");
						if(cond==null||cond.trim().length()==0){
							 continue;
						/*	 str_sql = " select * from  t#"+this.userView.getUserName()+"per_mainbody where 1=1 " ;
						 
							  if(!"5".equals(mainBodyType))
							  	bo.selMainBody2(str_sql, planid, mainBodyType, object,"false",pSelf);*/
						}else{
						if(cexpr!=null&&cexpr.trim().length()>0){	//简单条件	获得sql片段	
								if(simplemap!=null&&simplemap.get(mainBodyType)!=null)
							   sql=(String)simplemap.get(mainBodyType);
							  if(sql!=null&&sql.length()>0)
								  str_sql = " select * from  t#"+this.userView.getUserName()+"per_mainbody where "+sql ;
							  //加上主体范围
//							  if(scope!=null&&scope.length()>0&&!"5".equals(mainBodyType)){
//								  if(scope.equals("1"))
//									  str_sql=str_sql+" and B0110 like '"+b0110+"%' ";
//								  if(scope.equals("2"))
//									  str_sql=str_sql+" and E0122 like '"+tope0122+"%' ";
//								  if(scope.equals("3"))
//									  str_sql=str_sql+" and E0122 like '"+e0122+"%' ";
//							  }
							  if(!"5".equals(mainBodyType))
							  	bo.selMainBody2(str_sql, planid, mainBodyType, object,"false",pSelf);
						}else{//高级带修改
							//addFactor2(aSet,bSet,cond,userView,object_type);
							yp = (YksjParser)yksjParsermap.get(mainBodyType);
							HashMap map3=yp.getMapUsedFieldItems();  //获得设计到的所有指标			
							HashMap map2=yp.getBracketsFieldMap();  //获得中括号里的指标 {[性别]:FieldItem}
							Set keySet=map2.keySet();
							boolean flag2 = false;
							HashMap bracketsFieldValueMap = new HashMap();
				 		    for(Iterator t=keySet.iterator();t.hasNext();)
				 		    {
				 		    	String key=(String)t.next();
				 		    	FieldItem item =(FieldItem)map2.get(key);
				 		    	if(item!=null){
				 		    		if(object_type!=2){
				 		    			String str=item.getItemid().toUpperCase();
				 		    			if("e0122".equalsIgnoreCase(item.getItemid()))
				 		    				str="B0110";
				 		    			//处理日期型
				 		    			String value =""+compmap.get(object+str);
				 		    			if("D".equalsIgnoreCase(item.getItemtype())){
				 		    				value =value.trim().substring(0, 10);
				 		    			}
				 		    			bracketsFieldValueMap.put(key,""+value);
				 		    		}else{
				 		    			String value =""+compmap.get(object+item.getItemid().toUpperCase());
				 		    			if("D".equalsIgnoreCase(item.getItemtype())){
				 		    				value =value.trim().substring(0, 10);
				 		    			}
				 		    			bracketsFieldValueMap.put(key,value);
				 		    		
				 		    		}
				 		    	}
				 		    }
							try
							{
								yp.setBracketsFieldValueMap( bracketsFieldValueMap) ; //赛中括号里指标的值   {[性别]: 1}    日期型格式为：2009.01.01 
								yp.run_where(cond.trim()); 
								String where_str=yp.getSQL();
								  str_sql = " select * from  t#"+this.userView.getUserName()+"per_mainbody where "+where_str ;
								  //加上主体范围
//								  if(scope!=null&&scope.length()>0&&!"5".equals(mainBodyType)){
//									  if(scope.equals("1"))
//										  str_sql=str_sql+" and B0110 like '"+b0110+"%' ";
//									  if(scope.equals("2"))
//										  str_sql=str_sql+" and E0122 like '"+tope0122+"%' ";
//									  if(scope.equals("3"))
//										  str_sql=str_sql+" and E0122 like '"+e0122+"%' ";
//								  }
								  if(!"5".equals(mainBodyType))
									bo.selMainBody2(str_sql, planid, mainBodyType, object,"false",pSelf);  
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
							 
						}
						}
		    		
		    	}
				
		    }
//		    try {
//				this.frowset = dao.search(" select max(id) as id from per_mainbody ");
//				if(this.frowset.next()){
//			    	int id = this.frowset.getInt("id");
////			    	IDGenerator idg = new IDGenerator(2, this.getFrameconn());
////			    	idg.
////					String id = idg.getId("per_mainbody.id");
//			    	 vo = new RecordVo("id_factory");
//			    	 vo.setString("sequence_name", "per_mainbody.id");
//			    	 vo = dao.findByPrimaryKey(vo);
//			    	 vo.setInt("currentid", id);
//			    	 dao.updateValueObject(vo);
//			    }
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
			
			
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		    
		this.getFormHM().put("info", "ok");
    }
	 private ArrayList getMainBodylist(String plan_id)
	    {

			ArrayList list = new ArrayList();
		
			String strSql ="";
			    strSql = "Select P.cexpr cexpr1,P.cond cond1,P.scope scope1, B.* FROM per_plan_body P, per_mainbodyset B WHERE P.body_id = B.body_id and plan_id =" + plan_id + "  ORDER BY b.seq ";
			try
			{
		
			    ContentDAO dao = new ContentDAO(this.getFrameconn());
			    this.frowset = dao.search(strSql);
			    while (this.frowset.next())
			    {

			    	RecordVo vo = new RecordVo("per_mainbodyset");
			    	RecordVo vo2 = new RecordVo("per_plan_body");
					vo.setString("body_id", this.frowset.getString("body_id"));
					vo.setString("name", this.frowset.getString("name"));
					vo.setString("body_type", this.frowset.getString("body_type"));
					String  cond1 = Sql_switcher.readMemo(this.frowset, "cond1");
					String cond ="";
					String cexpr ="";
					String  scope ="";
					if(cond1==null||cond1.trim().length()==0){
						 cond = Sql_switcher.readMemo(this.frowset, "cond");
							 cexpr = Sql_switcher.readMemo(this.frowset, "cexpr");
						  scope = this.frowset.getString("scope");
					}else{
						 cond = Sql_switcher.readMemo(this.frowset, "cond1");
						 cexpr = Sql_switcher.readMemo(this.frowset, "cexpr1");
					  scope = this.frowset.getString("scope1");
					}
					cond = cond==null?"":cond;
					cexpr = cexpr==null?"":cexpr;
					vo.setString("cond", cond);
					vo.setString("cexpr", cexpr);	
					
					vo.setString("scope", scope);
			
					list.add(vo);
			    
			    }
			   
			} catch (Exception e)
			{
			    e.printStackTrace();
			}
		
			return list;
	    }
	 private void getOrganization(HashMap organizationmap){
		 ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset = dao.search("select * from organization ");
				while(this.frowset.next()){
					organizationmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemid"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}	
	 }
}
