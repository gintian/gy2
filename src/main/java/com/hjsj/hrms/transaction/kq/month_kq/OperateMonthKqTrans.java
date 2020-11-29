package com.hjsj.hrms.transaction.kq.month_kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class OperateMonthKqTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		
		String isok = "操作失败!";
		String isone = "1";
		
		String nid = "";
		if(null != this.getFormHM().get("nid")){
			nid = this.getFormHM().get("nid").toString(); //单独报批、驳回、批准的人员ID
		}
		
		String type = this.getFormHM().get("type").toString();
		//操作的是哪年哪月的数据
		String year = this.getFormHM().get("years").toString();
		String month = this.getFormHM().get("months").toString();
		
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		//System.out.println(this.userView.getManagePrivCode() +":" + this.userView.getManagePrivCodeValue());
		String dbname = this.userView.getDbname();
		//System.out.println("dbname:"+dbname);
		String a0100 = this.userView.getA0100();
		
		String codeid = this.userView.getManagePrivCode();//当前登录人的管理范围
		String codeValue = this.userView.getManagePrivCodeValue();
		
		//ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		//String relation = constant.getNodeAttributeValue("/param/Kq_Parameters",
		//		"sp_relation");
		//oracle库换种方式取值
		String relation = bo.getParam1();
		ArrayList currUsersList = this.getCurrUsersById(relation);
		
		if("".equals(relation.trim()) || null == relation){
			isok = "请先设置审批关系!";
		}else{
		//if(!this.userView.isSuper_admin()){ //如果不是超级用户
			String userName = dbname + a0100; //USR + A0100
			String peopleName = "";
			if(!"".equals(userName.trim())){				
				peopleName = this.getUserName(this.userView.getDbname() + this.userView.getA0100()); //人名
			}
			//String currUser = this.getCurrUserById(); //其实这个没必要要 用currUsersList 循环遍历一样
			String currUser = "";
			// 此处加入单个报批 批准 驳回 
			if("1".equals(type)){ //报批
				if(null != this.getFormHM().get("manypeople")){
					MonthKqBean beans = new MonthKqBean();
					currUser = (String)this.getFormHM().get("manypeople");
					currUsersList.clear();
					beans.setCurrUser(currUser);
					currUsersList.add(beans);
					//currUsersList.add(currUser);
				}/*
				if(this.userView.hasTheFunction("3238110108")){ //如果有人员引入按钮的权限 则是系统考勤员进入 如果没有 则是多级报批
					String shows = peopleName + " " + sdf.format(new Date()) + " 报批";
					if(currUsersList.size() == 1){ //如果有一个审批人
						if("".equals(nid.trim())){ //多人报批
							if(this.getBpStatus(year, month, codeid, codeValue, "")){
								if(this.baoPi(a0100, userName, shows, currUser,dbname,year,month, codeid , codeValue,"")){
									isok = "报批成功!";
								}else{
									isok = "报批失败!";
								}
							}else{
								isok = "不能进行报批操作，" + year +"年"+month+"月中记录必须是起草或者驳回状态才可报批!";
							}
						}else{//单个报批
							String [] nids = nid.split(",");
							for(int i = 0 ; i < nids.length ; i ++){
								if(this.getBpStatus(year, month,codeid,codeValue,nids[i])){
									if(this.baoPi(a0100, userName, shows, currUser,dbname,year,month, codeid , codeValue,nids[i])){
										isok = "报批成功!";
									}else{
										isok = "报批失败!";
									}
								}else{
									isok = "不能进行报批操作，选中记录必须是起草或者驳回状态才可报批!";
								}
							}
						}
					}else{//多个审批人
						isone = "2";
						MonthKqBean beans = null;
						String codes = "";
						for(int i = 0; i < currUsersList.size() ; i ++){
							beans = (MonthKqBean)currUsersList.get(i);
							String code = beans.getCurrUser();
							codes += code +",";
						}
						this.getFormHM().put("codes", codes);
					}
					this.getFormHM().put("isone", isone);
				}else{ 	//没有人员引入按钮权限 默认为多级审批
					
				}*/
				//currUsersList.size() == 1 设置的审批关系中 有一个直接上级(当前审批人)
				//System.out.print(":"+currUsersList.size());
				if(currUsersList.size() ==1){	
					MonthKqBean beans = (MonthKqBean)currUsersList.get(0);//此处用循环中的 只有一个 取第一个的值
					currUser = beans.getCurrUser();
					String userNames = this.getUserName(currUser);
					String shows = peopleName + " " + sdf.format(new Date()) + "报批给 " + userNames + "\r\n" ;
					if("".equals(nid)){//多人报批 有一个审批人
						if(this.getBpStatus(year, month,codeid,codeValue,"")){
							if(this.baoPi(a0100, userName, shows, currUser,dbname,year,month, codeid , codeValue,"")){
								isok = "报批成功!";
							}else{
								isok = "报批失败!";
							}
						}else{
							if(this.userView.hasTheFunction("0AC020108")){
								isok = "不能进行报批操作，请选择起草或驳回的记录进行报批操作!";
							}else{
								isok = "不能进行报批操作，请选中已报批状态的记录进行报批操作或者您不是当前审批人!";
							}
						}
					}else{			//单个报批
						String [] nids = nid.split(",");
						for(int i = 0 ; i < nids.length ; i ++){
							if(this.getBpStatus(year, month,codeid,codeValue,nids[i])){
								if(this.baoPi(a0100, userName, shows, currUser,dbname,year,month, codeid , codeValue,nids[i])){
									isok = "报批成功!";
								}else{
									isok = "报批失败!";
								}
							}else{
								if(this.userView.hasTheFunction("0AC020108")){
									isok = "不能进行报批操作，请选择起草或驳回的记录进行报批操作!";
								}else{
									isok = "不能进行报批操作，请选中已报批状态的记录进行报批操作或者您不是当前审批人!";
								}
							}
						}
					}
				}else if(currUsersList.size() > 1){//如果一个人有多个审批人(多个上级)
					isone = "2";
					MonthKqBean beans = null;
					String codes = "";
					for(int i = 0; i < currUsersList.size() ; i ++){
						beans = (MonthKqBean)currUsersList.get(i);
						String code = beans.getCurrUser();
						codes += code +",";
					}
					this.getFormHM().put("codes", codes);
				}
				this.getFormHM().put("isone", isone);
			}else if("2".equals(type)){ //批准前是否应该加个判断 只能对已报批的进行批准操作
				String shows = peopleName + " " + sdf.format(new Date()) + " 批准";
				if("".equals(nid)){//多人批准
					if(this.getPzStatus(year, month,codeid,codeValue,"")){
						if(this.piZhun(codeid, currUser, shows, dbname,year,month,codeid,codeValue,"")){
							this.clearCurrUser(currUser,year,month,""); //批准完毕以后清空当前审批人
							isok = "批准成功!";
						}else{
							isok = "批准失败或者您不是当前审批人!";
						}
					}else{
						isok = "不能进行批准操作，请选中已报批的记录进行批准操作!";
					}
				}else{			//单人批准
					String [] nids = nid.split(",");
					for(int i = 0 ; i < nids.length ; i++){
						if(this.getPzStatus(year, month,codeid,codeValue,nids[i])){
							if(this.piZhun(codeid, currUser, shows, dbname,year,month,codeid,codeValue,nids[i])){
								this.clearCurrUser(currUser,year,month,nids[i]); //批准完毕以后清空当前审批人
								isok = "批准成功!";
							}else{
								isok = "批准失败或者您不是当前审批人!";
							}
						}else{
							isok = "不能进行批准操作，选中记录必须是已报批状态才可批准!";
						}
					}
				}
			}else if("3".equals(type)){//驳回
				String shows = peopleName + " " + sdf.format(new Date()) + " 驳回" + "\r\n";
				if("".equals(nid.trim())){//多人驳回
					if(this.getPzStatus(year, month,codeid,codeValue,"")){
						if(this.boHui(codeid, currUser, shows, dbname,year,month,codeid,codeValue,"")){
							isok = "驳回成功!";
						}else{
							isok = "驳回失败或者您不是当前审批人!";
						}
					}else{
						isok = "不能进行驳回操作，请选中已报批数据进行驳回!";
					}
				}else{					//单个驳回
					String [] nids = nid.split(",");
					for(int i = 0 ; i < nids.length ; i++){
						if(this.getPzStatus(year, month,codeid,codeValue,nids[i])){
							if(this.boHui(codeid, currUser, shows, dbname,year,month,codeid,codeValue,nids[i])){
								isok = "驳回成功!";
							}else{
								isok = "驳回失败或者您不是当前审批人!";
							}
						}else{
							isok = "不能进行驳回操作，选中记录必须是已报批状态才可驳回!";
						}
					}
				}
			}
		 }
	//	}
			this.getFormHM().put("isok", isok);
	}
	//报批
	public boolean baoPi(String codeid,String userName,String approcess,String currUser,String dbname,String year , String month, String codeid1 , String codeValue,String nid ){
		//String sql = "update q35 set status = '03' ,userflag = '"+userName+"',approcess = '"+approcess+"' where a0100 in (select a0100 from usra01 where e0122 like '%"+codeid+"%' ) or curr_user = '"+userName+"'";
		StringBuffer sb = new StringBuffer();
		String a_code = codeid1 + codeValue;
		if(a_code.trim().length() > 0){ //授权了
			sb.append("update q35 set status = '02' ,");
			//sb.append("userflag = '");
			//sb.append(userName+"',approcess = '");
			//sb.append(" approcess = approcess || '");
			sb.append("approcess = " + Sql_switcher.isnull(Sql_switcher.sqlToChar("approcess"),"''")+Sql_switcher.concat() + "'");
			sb.append(approcess+"',curr_user = '");
			sb.append(currUser + "' , ");
			//sb.append("appuser = appuser || '");//oracle ||   sql +
			sb.append("appuser = " + Sql_switcher.isnull(Sql_switcher.sqlToChar("appuser"),"''")+Sql_switcher.concat() + "'");
			sb.append(currUser +";'");
			//sb.append("' where (year(q35z0) = '");
			sb.append(" where " + Sql_switcher.year("q35z0") + "='");
			sb.append(year + "' and ");
			sb.append(Sql_switcher.month("q35z0") + "= '");
			//sb.append("month(q35z0) = '");
			sb.append(month + "'");
			if(!"".equals(nid)){//单个报批 得加上人员编码来判断是哪个人
				sb.append(" and a0100 = '");
				sb.append(nid + "'");
			}
			//sb.append(" and a0100 in (");
			//sb.append("select a0100 from ");
			//sb.append("usra01 where");
			//if(codeid1.equalsIgnoreCase("UN")){				
			//	sb.append(" b0110 like '%");
			//	sb.append(codeValue+"%') ");
			//}else if(codeid1.equalsIgnoreCase("UM")){
			//	sb.append(" e0122 like '" + codeValue + "%')");
			//}else if(codeid1.equalsIgnoreCase("@K")){
			//	sb.append(" e01a1 like '" + codeValue + "%')");
			//}
			sb.append(" and( curr_user = '");
			sb.append(this.userView.getDbname() + this.userView.getA0100() +"'"); //当前审批人等于当前登录用户
			sb.append(" or curr_user = '");
			sb.append(this.userView.getUserName() + "')");
		}
			
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			if(0 != dao.update(sb.toString())){				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	//批准
	public boolean piZhun(String codeid ,String currUser ,String approcess ,String dbname,String year , String month , String codeid1,String codeValue,String nid){
		StringBuffer sb = new StringBuffer();
		sb.append("update q35 set status = '03',");
		//sb.append("approcess = approcess + '"); //列的叠加 把审批流程叠加起来存入表中 便于查看审批过程
		sb.append("approcess = " + Sql_switcher.isnull(Sql_switcher.sqlToChar("approcess"),"''")+Sql_switcher.concat() + "'");
		sb.append(approcess + "'");
		/*sb.append(" where a0100 in ");
		sb.append(" (select a0100 from ");
		sb.append(dbname + "a01 where ");
		sb.append(" e0122 like '%");
		sb.append(codeid + "%') and ");
		sb.append("curr_user = '");
		sb.append(currUser);
		sb.append("'");*/
		//sb.append(" where (year(q35z0) = '");
		sb.append(" where " + Sql_switcher.year("q35z0") + "='");
		sb.append(year + "' and ");
		//sb.append("month(q35z0) = '");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "'");
		if(!"".equals(nid.trim())){
			sb.append(" and a0100 = '");
			sb.append(nid + "' ");
		}
		//sb.append(" and a0100 in (");
		//sb.append("select a0100 from ");
		//sb.append("usra01 where");
		//if(codeid1.equalsIgnoreCase("UN")){				
		//	sb.append(" b0110 like '%");
		//	sb.append(codeValue+"%') ");
		//}else if(codeid1.equalsIgnoreCase("UM")){
		//	sb.append(" e0122 like '" + codeValue + "%')");
		//}else if(codeid1.equalsIgnoreCase("@K")){
		//	sb.append(" e01a1 like '" + codeValue + "%')");
	//	}
	//	sb.append(" or curr_user = '");
		sb.append("and (curr_user = '");
		sb.append(this.userView.getDbname() + this.userView.getA0100() +"'");
		sb.append(" or curr_user = '");
		sb.append(this.userView.getUserName());
		sb.append("')");
		//sb.append(" and status not in ('03')");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(0 != dao.update(sb.toString())){		//如果被修改的记录大于0 说明修改成功		
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//驳回
	public boolean boHui(String codeid ,String currUser ,String approcess ,String dbname,String year , String month , String codeid1 , String codeValue,String nid){
		StringBuffer sb = new StringBuffer();
		sb.append("update q35 set status = '07',");
		//sb.append("approcess = approcess + '");
		sb.append("approcess = " + Sql_switcher.isnull(Sql_switcher.sqlToChar("approcess"),"''")+Sql_switcher.concat() + "'");
		sb.append(approcess + "'");
		sb.append(" ,curr_user = userflag");//驳回到初始报批人
		//sb.append(this.getCurrUserToBohui(year, month, nid) + "' ");
		/*sb.append(" where a0100 in ");
		sb.append(" (select a0100 from ");
		sb.append(dbname + "a01 where ");
		sb.append(" e0122 like '%");
		sb.append(codeid + "%') and ");
		sb.append("curr_user = '");
		sb.append(currUser);
		sb.append("'");*/
		//sb.append(" where (year(q35z0) = '");
		sb.append(" where " + Sql_switcher.year("q35z0") + "='");
		sb.append(year + "' and ");
		//sb.append("month(q35z0) = '");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "'");
		if(!"".equals(nid.trim())){
			sb.append(" and a0100 = '");
			sb.append(nid + "' ");
		}
		//sb.append(" and a0100 in (");
		//sb.append("select a0100 from ");
		//sb.append("usra01 where");
		//if(codeid1.equalsIgnoreCase("UN")){				
		//	sb.append(" b0110 like '%");
		//	sb.append(codeValue+"%') ");
		//}else if(codeid1.equalsIgnoreCase("UM")){
		//	sb.append(" e0122 like '" + codeValue + "%')");
		///}else if(codeid1.equalsIgnoreCase("@K")){
		//	sb.append(" e01a1 like '" + codeValue + "%')");
		//}
		//sb.append(" or curr_user = '");
		sb.append(" and (curr_user = '");
		//sb.append("Usr00000003'");
		sb.append(this.userView.getDbname() + this.userView.getA0100() +"'");
		sb.append(" or curr_user = '");
		sb.append(this.userView.getUserName() + "')");
		sb.append(" and status not in ('03')");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//System.out.println(sb.toString());
			if(0 != dao.update(sb.toString()) ){	//如果被修改的记录大于0 说明修改成功			
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//根据设置的审批关系得到当前审批人
	public String getCurrUserById(){
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		String currUser = "";
		//ConstantXml constant = new ConstantXml(this.frameconn, "kq_monthly");
		//String spRelation = constant.getNodeAttributeValue("/param/Kq_Parameters",
		//		"sp_relation");
		String spRelation = bo.getParam1();
		String sql = "select Mainbody_id from t_wf_mainbody where Relation_id = '"+spRelation+"' and sp_grade = '9'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				currUser = this.frowset.getString("mainbody_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currUser;
	}
	//通过设置的审批关系得到当前审批人 或许不只是一个 所以放入集合中
	public ArrayList getCurrUsersById(String relation){
		ArrayList list = new ArrayList();
		MonthKqBean beans = null;
		String sql = "select mainbody_id,a0101 from t_wf_mainbody where relation_id = '" + relation + "'";
		StringBuffer sb = new StringBuffer();
		sb.append("select mainbody_id ,a0101 from t_wf_mainbody ");
		sb.append(" where relation_id = '");
		sb.append(relation + "'");
		sb.append(" and sp_grade = '9' ");//直接上级
		sb.append(" and object_id = '");
		sb.append(this.userView.getDbname()+this.userView.getA0100() + "'");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
			//System.out.println(sql);
			while(this.frowset.next()){
				beans = new MonthKqBean();
				beans.setCurrUser(this.frowset.getString("mainbody_id"));
				beans.setItemdesc(this.frowset.getString("a0101"));
				list.add(beans);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	//批准完毕以后清空表中当前审批人
	public void clearCurrUser(String currUser,String year , String month,String nid){
		StringBuffer sb = new StringBuffer();
		sb.append("update q35 set curr_user = ''");
		sb.append(" where curr_user = '");
		sb.append(currUser);
		sb.append("'");
		//sb.append(" and year(q35z0) = '");
		sb.append(" and " + Sql_switcher.year("q35z0") + "='");
		//sb.append(year + "' and month(q35z0) = '");
		sb.append(year + "' and ");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "'");
		if(!"".equals(nid.trim())){
			sb.append(" and a0100 = '");
			sb.append(nid + " '");
		}
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//报批之前进行判断 当年月所有人是否全是起草或者驳回状态
	public boolean getBpStatus(String year ,String month,String codeid , String codeValue ,String nids){
		//String sql = "select * from q35 where year(q35z0) = '"+year+"' and month(q35z0) = '"+month+"' and status != '01' and status != '07'";
		StringBuffer sb = new StringBuffer();
		sb.append("select * from q35 ");
		//sb.append("where year(q35z0) = '");
		sb.append(" where ");
		sb.append(Sql_switcher.year("q35z0") + "='");
		sb.append(year + "' and ");
		//sb.append(year + "' and month(q35z0) = '");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "' and ");
		if(!"".equals(nids)){//单个报批 得加上人员编码来判断是哪个人
			sb.append(" a0100 = '");
			sb.append(nids + "' and ");
		}
		if(this.userView.hasTheFunction("0AC020108")){ //如果当前用户有人员引入功能权限 则报批时候判断起草、驳回数据 如
			sb.append("status not in( '01','07') ");
		}else{											//如果当前用户没有人员引入功能权限 则报批时候判断是否是已报批数据 只有已报批数据才可继续报批
			sb.append("(status not in( '02') ");
		}
		//sb.append("status not in( '01','07','02') ");
		sb.append(" and (a0100 in (");
		sb.append("select a0100 from ");
		sb.append("usra01 where");
		if("UN".equalsIgnoreCase(codeid)){
			sb.append(" b0110 like '%");
			sb.append(codeValue+"%') ");
		}else if("UM".equalsIgnoreCase(codeid)){
			sb.append(" e0122 like '" + codeValue + "%')");
		}else if("@K".equalsIgnoreCase(codeid)){
			sb.append(" e01a1 like '" + codeValue + "%')");
		}
		if(this.userView.hasTheFunction("0AC020108")){ //如果当前用户有人员引入功能权限 则报批时候判断起草、驳回数据 如
			sb.append(" or curr_user = '");
			sb.append(this.userView.getDbname() + this.userView.getA0100() +"')");
		}else{											//如果当前用户没有人员引入功能权限 则报批时候判断是否是已报批数据 只有已报批数据才可继续报批
			sb.append("  or curr_user = '");
			sb.append(this.userView.getDbname() + this.userView.getA0100() +"'))");
		}
		//sb.append(" or curr_user = '");
		//sb.append(this.userView.getDbname() + this.userView.getA0100() +"')");
	//	sb.append(" and status = '02'");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			//System.out.println(sb.toString());
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	//批准和驳回之前进行判断 当年月之中有没有不是已报批状态的记录
	public boolean getPzStatus(String year ,String month,String codeid,String codeValue,String nid){
		//String sql =  "select * from q35 where year(q35z0) = '"+year+"' and month(q35z0) = '"+month+"' and status != '02' ";
		StringBuffer sb = new StringBuffer();
		sb.append("select * from q35 ");
		//sb.append("where year(q35z0) = '");
		sb.append(" where " +Sql_switcher.year("q35z0") + "='");
		//sb.append(year + "' and month(q35z0) = '");
		sb.append(year + "' and ");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "' and ");
		if(!"".equals(nid)){//单个批准和驳回 得加上人员编码来判断是哪个人
			sb.append(" a0100 = '");
			sb.append(nid + "' and ");
		}
		sb.append("status not in( '02') ");
		sb.append(" and (a0100 in (");
		sb.append("select a0100 from ");
		sb.append("usra01 where");
		if("UN".equalsIgnoreCase(codeid)){
			sb.append(" b0110 like '%");
			sb.append(codeValue+"%') ");
		}else if("UM".equalsIgnoreCase(codeid)){
			sb.append(" e0122 like '" + codeValue + "%')");
		}else if("@K".equalsIgnoreCase(codeid)){
			sb.append(" e01a1 like '" + codeValue + "%')");
		}
		sb.append(" and curr_user = '");
		sb.append(this.userView.getDbname() + this.userView.getA0100() +"')");
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//驳回时候直接驳回到初始报批人
	public String getCurrUserToBohui(String year ,String month ,String a0100){
		String currUser = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" select userflag from q35");
		//sb.append(" where year(q35z0) = '");
		sb.append(" where " + Sql_switcher.year("q35z0") + "='");
		sb.append(year + "' and ");
		//sb.append(" and month(q35z0) = '");
		sb.append(Sql_switcher.month("q35z0") + "='");
		sb.append(month + "'");
		if(!"".equals(a0100.trim())){ //如果传进来的a0100 不是空的 则是单个驳回 如果是空的 则是批量驳回 取一个驳回人即可
			sb.append(" and a0100 = '");
			sb.append(a0100 + "' ");
		}
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){
				currUser = this.frowset.getString("userflag");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currUser;
	}
	
	public String getUserName(String a0100){
		String userName = "";
		String nbase = a0100.substring(0,3);
		String code = a0100.substring(3,a0100.length());
		String sql = "select a0101 from "+nbase+"a01 where a0100 = '"+code+"'";
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				userName = this.frowset.getString("a0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
	}
}
