package com.hjsj.hrms.transaction.performance.nworkplan.season;
/*主页面的显示处理类*/

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Calendar;
import java.util.HashMap;

public class SearchQuarterTrans extends IBusiness{

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String frompage = hm.get("frompage")==null?"":(String)hm.get("frompage");//从菜单进入时，frompage为1
		hm.remove("frompage");
		NewWorkPlanBo nwp = new NewWorkPlanBo(this.getFrameconn(),this.userView);
		String isdept = (String)this.getFormHM().get("isdept"); //入口 1 是个人进入 2 是部门进入
		String opt = (String)this.getFormHM().get("opt");      // 入口 1 从自己进入 2 从团队进入
		String type = (String)this.getFormHM().get("type");//1:季报 2：年报
		String year = "";
		String season = "";
		StringBuffer sb = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		StringBuffer where = new StringBuffer();
		StringBuffer sbp0100 = new StringBuffer("");//为了查出最新的p0100
		columns.append("file_id,name,ext,p0100");
		sb.append("select "+columns);
		where.append(" from per_diary_file where p0100 = ");
		if("1".equals(frompage)){//如果是从菜单进入，年和季显示当前
			year = Calendar.getInstance().get(Calendar.YEAR) + ""; 
			season = nwp.getSeasonByMonth(Calendar.getInstance().get(Calendar.MONTH)+1);
		}else{
			year = (String)this.getFormHM().get("year");
			season = (String)this.getFormHM().get("season");
		}
		
		if("1".equals(opt)){  //如果是自己进入 进入人员信息
			where.append("(");
			where.append("select p0100 from p01 where ");
			if("1".equals(isdept)){//如果是个人
				if("1".equals(type)){	   //入口是季报
					if(!nwp.isChuZhang(this.userView.getA0100())){//如果不是处长，就用a0100.因为如果是处长，再用a0100，如果再换处长，就什么看不见了
						where.append(" a0100 = '");
						where.append(this.userView.getA0100() + "' and ");
						where.append(" nbase = '" + this.userView.getDbname() + "' and ");
					}
					where.append(Sql_switcher.year("p0104") + "=" + year);
					where.append(" and state = 3 and time = " + season );
				}else if("2".equals(type)){//入口是年报
					if(!nwp.isChuZhang(this.userView.getA0100())){
						where.append(" a0100 = '");
						where.append(this.userView.getA0100() + "' and ");
						where.append(" nbase = '" + this.userView.getDbname() + "' and ");
					}
					where.append(Sql_switcher.year("p0104") + "=" + year);
					where.append(" and state = 4 ");
				}
				if(nwp.isChuZhang(this.userView.getA0100())){// 如果是处长 则找belong_type = 1 和部门是我的 或者 当前人是我 belong_type = 0 
					where.append(" and ((belong_type = 1 and e0122='" + this.userView.getUserDeptId() + "')");
//					where.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
//					where.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0)");
					where.append(")");//and条件的结束
				}else{
					where.append(" and (belong_type = 0 or belong_type = null)");
				}
			}else if("2".equals(isdept)){//如果是部门
				where.append(" belong_type = 2 and ");
				where.append("e0122 = '" + nwp.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				where.append("' and ");
				where.append(Sql_switcher.year("p0104") + "=" + year);
				if("1".equals(type)){
					where.append(" and state = 3 ");
					where.append(" and time = " + season);
				}else if("2".equals(type)){
					where.append(" and state = 4");
				}
			}
			where.append(")");
		}else if("2".equals(opt)){     //团队总结进入(领导进入)
			String p0100 = "";
			HashMap hm2 = (HashMap) this.getFormHM().get("requestPamaHM");	
			p0100 = (String)hm2.get("p0100");
			hm2.remove("p0100");
			if(p0100==null || "".equals(p0100)){//切换了年或季，需要重查p0100
				where.append("(");
				where.append("select p0100 from p01 where p0100=");
				p0100 = (String)this.getFormHM().get("p0100");//上次从团队进入传入的p0100
				String[] str = new String[4];//0:a0100 1:nbase 2:belong_type 3:e0122
				str = nwp.getDetailByP0100(p0100);
				
				String belong_type = (String)this.getFormHM().get("belong_type");//从团队那里接受过来的参数
				if("0".equals(belong_type)){
					sbp0100.append("select p0100 from p01 where a0100='"+str[0]+"' and nbase='"+str[1]+"' and (belong_type=0 or belong_type=null)");
					where.append("(select p0100 from p01 where a0100='"+str[0]+"' and nbase='"+str[1]+"' and (belong_type=0 or belong_type=null)");
				}else if("1".equals(belong_type)){
					sbp0100.append("select p0100 from p01 where belong_type=1 and e0122='"+str[2]+"'");
					where.append("(select p0100 from p01 where belong_type=1 and e0122='"+str[2]+"'");
				}else if("2".equals(belong_type)){
					sbp0100.append("select p0100 from p01 where belong_type=1 and e0122='"+str[2]+"'");
					where.append("(select p0100 from p01 where belong_type=2 and e0122='"+nwp.getParentDeptIdByUser(str[0], str[1])+"'");
				}
				String tmpyear = (String)this.getFormHM().get("year");
				where.append(" and "+Sql_switcher.year("p0104") + "=" + tmpyear);
				sbp0100.append(" and "+Sql_switcher.year("p0104") + "=" + tmpyear);
				if("1".equals(type)){	   //入口是季报
					String tmpseason = (String)this.getFormHM().get("season");
					where.append(" and state=3 and time ="+tmpseason);
					sbp0100.append(" and state=3 and time ="+tmpseason);
				}else if("2".equals(type)){//入口是年报
					where.append(" and state=4");
					sbp0100.append(" and state=4");
				}
				where.append("))");
			}else{//如果是第一次进入，还没有切换年和季度，那么年和季度需要自己通过传递过来的p0100进行查找
				String tempstate = "3";
				if("2".equals(type)){//如果是年报
					tempstate = "4";
				}
				String[] strarray = nwp.getYearAndSeason(p0100,tempstate);
				year = strarray[0];
				season = strarray[1];
				where.append(p0100);
			}
		}
		
		//入口入口是季报，还需要起始月和结束月
		if("1".equals(type)){	   //入口是季报
			String months = nwp.getMonthsBySeason(season);
			if(!"".equals(months)){
				String [] month = months.split(",");
				this.getFormHM().put("startMonth", month[0]);
				this.getFormHM().put("endMonth", month[1]);
			}
		}
		
		/*为了控制提交后不能删除，不能上传 只有从自己页面进入的时候才有这个控制*/
		String isCommitOk = "0";
		if("1".equals(opt)){//只有从自己页面进入的时候才有此控制
			StringBuffer sbquery = new StringBuffer("");
			sbquery.append("select p0115 from p01 where p0100 in (select p0100 "+where.toString()+")");//有且仅能查出一条数据来
			RowSet rs = null;
			try{
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				rs = dao.search(sbquery.toString());
				if(rs.next()){
					String p0115 = rs.getString("p0115");
					if("02".equals(p0115)){
						isCommitOk = "1";
					}
				}
				if(sbp0100.length()>0){
					rs = dao.search(sbp0100.toString());
					if(rs.next()){
						String tempp0100 = rs.getString("p0100")==null?"":rs.getString("p0100");
						this.getFormHM().put("p0100", tempp0100);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		this.getFormHM().put("sql", sb.toString());
		this.getFormHM().put("where", where.toString());
		this.getFormHM().put("cols", columns.toString());
		this.getFormHM().put("year", year);
		if(!"".equals(season)){//年报时，season可能为空
			this.getFormHM().put("season", season);
		}
		this.getFormHM().put("isTooBig", "0");
		this.getFormHM().put("isCommitOk", isCommitOk);
	}
	
}
