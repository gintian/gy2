package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hjsj.hrms.transaction.kq.month_kq.MonthKqBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class OperateSquarterTrans extends IBusiness{

	public void execute() throws GeneralException {
		String opt = (String)this.getFormHM().get("opt");
		String type = (String)this.getFormHM().get("type");
		String isok = "报批失败!";
		String year = (String)this.getFormHM().get("year");
		String relation = "5";
		this.getFormHM().put("shows", "");
		this.getFormHM().remove("shows");
		String isdept = (String)this.getFormHM().get("isdept"); //进入部门还是进入个人
		
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn,this.userView); 
		ArrayList list = bo.getCurrUsersById(relation);
		if("".equals(relation.trim())){
			isok = "请先设置审批关系!";
		}else{	
			if("1".equals(isdept)){
				String isone = "1";
				String userName = "";
				String user = "";
				if("1".equals(opt)){
					if("1".equals(type)){ //季报报批
						if(null != this.getFormHM().get("manypeople")){
							MonthKqBean beans = new MonthKqBean();
							String currUser = (String)this.getFormHM().get("manypeople");
							list.clear();
							beans.setCurrUser(currUser);
							beans.setItemdesc(bo.getUserNameByCode((String)this.getFormHM().get("manypeople")));
							list.add(beans);
							//currUsersList.add(currUser);
						} 
						String season = (String)this.getFormHM().get("season");
						if(list.size() == 1){     //如果有一个当前审批人
							if(bo.isBaoPi(Integer.parseInt(year), Integer.parseInt(season) , type , opt , "",isdept)){
								MonthKqBean bean = (MonthKqBean)list.get(0);
								if(!"".equals(bean.getCurrUser())){
									user = bean.getCurrUser();
									userName = bo.getUsername(user);
								}
								if(bo.BaoPi(Integer.parseInt(year), Integer.parseInt(season),user,userName,type,opt,"",isdept)){
									isok = "报批成功!";
								}else{
									isok = "报批失败!";
								}
							}else{
								isok = "只能对起草、驳回状态的季报进行报批操作!";
							}
						}else if(list.size() > 1){//如果当前登录用户有多个当前审批人 
							isone = "2";
							MonthKqBean beans = null;
							String codes = "";
							for(int i = 0; i < list.size() ; i ++){
								beans = (MonthKqBean)list.get(i);
								String code = beans.getCurrUser();
								codes += code +",";
							}
							this.getFormHM().put("codes", codes);
						}
					}else if("2".equals(type)){ //年报报批
						if(null != this.getFormHM().get("manypeople")){
							MonthKqBean beans = new MonthKqBean();
							String currUser = (String)this.getFormHM().get("manypeople");
							list.clear();
							beans.setCurrUser(currUser);
							beans.setItemdesc(bo.getUserNameByCode((String)this.getFormHM().get("manypeople")));
							list.add(beans);
							//currUsersList.add(currUser);
						} 
						if(list.size() == 1){   //一个当前审批人(直接上级)
							if(bo.isBaoPi(Integer.parseInt(year), 0, type ,opt , "",isdept)){
								MonthKqBean bean = (MonthKqBean)list.get(0);
								if(!"".equals(bean.getCurrUser())){
									user = bean.getCurrUser();
									userName = bo.getUsername(user);
								}
								if(bo.BaoPi(Integer.parseInt(year), 0, user, userName, type,opt,"",isdept)){
									isok = "报批成功!";
								}else{
									isok = "报批失败!";
								}
							}else{
								isok = "只能对起草、驳回状态的年报进行报批操作!";
							}
						}else if(list.size() > 1){//多个当前审批人(多个直接上级)
							isone = "2";
							MonthKqBean beans = null;
							String codes = "";
							for(int i = 0; i < list.size() ; i ++){
								beans = (MonthKqBean)list.get(i);
								String code = beans.getCurrUser();
								codes += code +",";
							}
							this.getFormHM().put("codes", codes);
						}
					}
				}else if("2".equals(opt)){ //从团队总结中进入 没有报批(主要是因为下级看上级 无法报批) 此方法暂时不用 以后确定不用了可以去掉 减少代码量
					//String a0100 = (String)this.getFormHM().get("a0100");
					//String nbase = (String)this.getFormHM().get("nbase");
					String p0100 = (String)this.getFormHM().get("p0100");
					if("1".equals(type)){ //季报报批
						if(null != this.getFormHM().get("manypeople")){
							MonthKqBean beans = new MonthKqBean();
							String currUser = (String)this.getFormHM().get("manypeople");
							list.clear();
							beans.setCurrUser(currUser);
							beans.setItemdesc(bo.getUserNameByCode((String)this.getFormHM().get("manypeople")));
							list.add(beans);
							//currUsersList.add(currUser);
						} 
						String season = (String)this.getFormHM().get("season");
						if(list.size() == 1){     //如果有一个当前审批人
							if(bo.isBaoPi(Integer.parseInt(year), Integer.parseInt(season) , type , opt , p0100,"1")){
								MonthKqBean bean = (MonthKqBean)list.get(0);
								if(!"".equals(bean.getCurrUser())){
									user = bean.getCurrUser();
									userName = bo.getUsername(user);
								}
								if(bo.BaoPi(Integer.parseInt(year), Integer.parseInt(season),bean.getItemdesc(),userName,type,opt,p0100,"1")){
									isok = "报批成功!";
								}else{
									isok = "报批失败!";
								}
							}else{
								isok = "只能对起草、驳回状态的季报进行报批操作!";
							}
						}else if(list.size() > 1){//如果当前登录用户有多个当前审批人 
							isone = "2";
							MonthKqBean beans = null;
							String codes = "";
							for(int i = 0; i < list.size() ; i ++){
								beans = (MonthKqBean)list.get(i);
								String code = beans.getCurrUser();
								codes += code +",";
							}
							this.getFormHM().put("codes", codes);
						}
					}else if("2".equals(type)){ //年报报批
						if(null != this.getFormHM().get("manypeople")){
							MonthKqBean beans = new MonthKqBean();
							String currUser = (String)this.getFormHM().get("manypeople");
							list.clear();
							beans.setCurrUser(currUser);
							beans.setItemdesc(bo.getUserNameByCode((String)this.getFormHM().get("manypeople")));
							list.add(beans);
							//currUsersList.add(currUser);
						} 
						if(list.size() == 1){   //一个当前审批人(直接上级)
							if(bo.isBaoPi(Integer.parseInt(year), 0, type ,opt , p0100,"1")){
								MonthKqBean bean = (MonthKqBean)list.get(0);
								if(!"".equals(bean.getCurrUser())){
									user = bean.getCurrUser();
									userName = bo.getUsername(user);
								}
								if(bo.BaoPi(Integer.parseInt(year), 0, bean.getItemdesc(), userName, type,opt,p0100,"1")){
									isok = "报批成功!";
								}else{
									isok = "报批失败!";
								}
							}else{
								isok = "只能对起草、驳回状态的年报进行报批操作!";
							}
						}else if(list.size() > 1){//多个当前审批人(多个直接上级)
							isone = "2";
							MonthKqBean beans = null;
							String codes = "";
							for(int i = 0; i < list.size() ; i ++){
								beans = (MonthKqBean)list.get(i);
								String code = beans.getCurrUser();
								codes += code +",";
							}
							this.getFormHM().put("codes", codes);
						}
					}
				}
				this.getFormHM().put("isone", isone);
			}else if("2".equals(isdept)){
				int season = 0;
				if("1".equals(type)){
					season = Integer.parseInt((String)this.getFormHM().get("season"));
				}
				if(bo.isBaoPi(Integer.parseInt(year), season, type ,opt , "",isdept)){
					
					if(bo.BaoPi(Integer.parseInt(year), season, "", "", type,opt,"",isdept)){
						isok = "报批成功!";
					}else{
						isok = "报批失败!";
					}
				}else{
					isok = "只能对起草、驳回状态的年报进行报批操作!";
				}
				this.getFormHM().put("isone", "1");
			}
			}
			String shows = (String)this.getFormHM().get("shows");
			if(!"".equals(shows)){
				this.getFormHM().put("shows", "");
			}
			this.getFormHM().put("isok", isok);
	}
}
