package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GradeMembersEditTrans extends IBusiness {
	/**
	 * 30200710259
	 * <p>Title:PersonAndOpinionTrans.java</p>
	 * <p>Description>:目标卡多人评分保存删除评分人</p>
	 * <p>Company:HJSJ</p>
	 * <p>@author: zhanghua
	 */
	@Override
	public void execute() throws GeneralException {
		try{
			
			String type=(String)this.getFormHM().get("type");//type 1:添加评价人 2:删除评价人
			String plan_id=(String)this.getFormHM().get("plan_id");
			plan_id= PubFunc.decrypt(SafeCode.decode(plan_id));
			ContentDAO dao=new ContentDAO(this.frameconn);
			String strP0400=(String)this.getFormHM().get("strP0400");
			strP0400= PubFunc.decrypt(SafeCode.decode(strP0400));
			
			String content=(String)this.getFormHM().get("content");
			RowSet rs=null;
			if("1".equals(type) || "2".equals(type) ){//添加评价人 或者更新评价人
				
				
				String[] idsArray = content.split("'");
				
				
				HashMap<String, ArrayList<String>> map=new HashMap<String, ArrayList<String>>();
				for(String userid:idsArray)
				{
					if(StringUtils.isBlank(userid))
						continue;
					userid = PubFunc.decrypt(SafeCode.decode(userid));
					String nbase =userid.substring(0, 3);//人员库前缀
					if(map.containsKey(nbase)){
						ArrayList<String> list=(ArrayList<String>) map.get(nbase);
						list.add(userid.substring(3));
					}else{
						ArrayList<String> list=new ArrayList<String>();
						list.add(userid.substring(3));
						map.put(nbase, list);
					}
				}
				if("2".equals(type)){
					ObjectCardBo bo=new ObjectCardBo(getFrameconn(), getUserView(), plan_id);
				
					bo.UpdateGradeMembers(strP0400, map);
				}else{
					
					Iterator iter = map.entrySet().iterator();
					while(iter.hasNext()){
						Map.Entry entry = (Map.Entry) iter.next();
						String key = (String)entry.getKey();
						ArrayList<String> list = (ArrayList<String>)entry.getValue();
						
						StringBuffer strSql=new StringBuffer("insert into per_grade_members (P0400,NBASE,A0100,A0101,Create_user,Create_date) ");
						strSql.append(" select ?,?,n.a0100,n.a0101,'"+this.userView.getUserName()+"',"+Sql_switcher.sqlNow()+" from "+key+"A01 n ");
						strSql.append(" where n.a0100 in (");
						
						for(int i=0;i<list.size();i++){
							strSql.append("?,");
						}
						strSql.deleteCharAt(strSql.length()-1);
						strSql.append(")");
						
						ArrayList datalist=new ArrayList();
						datalist.add(strP0400);
						datalist.add(key);
						datalist.addAll(list);
						
						dao.update(strSql.toString(),datalist);
					
					}
				}
				String strSql="select nbase,A0100,A0101 from per_grade_members  where p0400='"+strP0400+"' order by Create_date";
				rs=dao.search(strSql);
				ArrayList<String> a0100List=new ArrayList<String>();
				StringBuffer strA0101=new StringBuffer();
				
				while(rs.next()){
					a0100List.add(PubFunc.encrypt(rs.getString("nbase")+rs.getString("A0100")));
					strA0101.append(rs.getString("A0101")+",");
				
				}
				if(strA0101.length()>0)
				strA0101.deleteCharAt(strA0101.length()-1);
				this.getFormHM().put("a0100List",a0100List);
				this.getFormHM().put("strA0101",strA0101.toString());
				
				
			}
//			else{
//				StringBuffer strSql=new StringBuffer();
//				strSql.append("insert into per_grade_members_Opinion(P0400,NBASE,A0100,Opinion,Create_date) values(?,?,?,?,");
//				strSql.append(Sql_switcher.sqlNow());
//				strSql.append(")");
//				
//				content=SafeCode.decode(content);
//				ArrayList list=new ArrayList();
//				list.add(strP0400);
//				list.add(this.userView.getDbname());
//				list.add(this.userView.getA0100());
//				list.add(content);
//				dao.update(strSql.toString(),list);
//				
//				strSql.setLength(0);
//				strSql.append("select usrA01.a0101,"+Sql_switcher.dateToChar("p.Create_date", "yyyy-mm-dd,hh24:mi:ss")+" as Create_date,opinion ");//暂时只支持在职库
//				strSql.append(" from per_grade_members_Opinion p inner join usrA01 on p.a0100=usrA01.a0100 where p0400=? order by Create_date ");
//				list=new ArrayList();
//				list.add(strP0400);
//				rs=dao.search(strSql.toString(),list);
//				ArrayList<LazyDynaBean> opinion=new ArrayList<LazyDynaBean>();
//				while(rs.next()){
//					LazyDynaBean bean=new LazyDynaBean();
//					bean.set("name", rs.getString("a0101"));
//					bean.set("time", rs.getString("Create_date"));
//					bean.set("value", rs.getString("opinion"));
//					list.add(bean);
//					
//				}
//				this.getFormHM().put("opinion", list);
//			}
			
			this.getFormHM().put("type", type);
			this.getFormHM().put("strP0400", PubFunc.encrypt(strP0400));
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
