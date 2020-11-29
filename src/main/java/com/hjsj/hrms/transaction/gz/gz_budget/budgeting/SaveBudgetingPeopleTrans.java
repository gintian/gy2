package com.hjsj.hrms.transaction.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budgeting.BudgetingBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaResBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveBudgetingPeopleTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
		String ysparam_newmonth_menu=((String)sysbo.getSysValueMap().get("ysparam_newmonth_menu")).toLowerCase();//获得新员工入职月份指标
		String ysparam_set=((String)sysbo.getSysValueMap().get("ysparam_set")).toLowerCase();
		String ysparam_idx_menu = ((String)sysbo.getSysValueMap().get("ysparam_idx_menu")).toLowerCase();
		ArrayList fieldList=new ArrayList();
		fieldList=(ArrayList) this.getFormHM().get("fieldList");
		String tab_id=(String)this.getFormHM().get("tab_id");
		String budget_id=(String)this.getFormHM().get("budget_id");
		BudgetingBo bo = new BudgetingBo(this.getFrameconn(),this.userView,true,"");
		String b0110=bo.getUnitcode();
		LazyDynaBean bean = new LazyDynaBean();
		String sql="select "+ysparam_newmonth_menu+" from "+ysparam_set+" where b0110='"+b0110+"' and "+ysparam_idx_menu+"="+ budget_id;
		RowSet rs=null;
		RowSet ros=null;
		RowSet ras=null;
		String max_sc010="";
		String newStaffA0101 = ResourceFactory.getProperty("gz.budget.newstaff");
		int k=0;
		try {
			String beginMonth="";
			try{
				rs=dao.search(sql);
				if(rs.next()){
					beginMonth=rs.getString(1);
				}
			}finally{
				if(rs!=null) rs.close();
			}
			
			for(int i=0;i<fieldList.size();i++){
				bean=(LazyDynaBean) fieldList.get(i);
				String codeitemid=(String) bean.get("codeitemid");
				String num=(String) bean.get("num");//页面所对应人员类别人数
				String ssl="select count(*) as A from sc01 where sc000='"+codeitemid+"' and b0110='"+b0110+"' and budget_id="+budget_id.toString()
				         + " and A0101='"+ newStaffA0101 +"' ";
				//String sbl="select max(sc010) from sc01 ";
				try{
					ros=dao.search(ssl);
					if(ros.next()){
						String str=ros.getString(1);
						k=Integer.parseInt(str);//数据库中对应人员类别人数
					}
				}finally{
					if(ros!=null) ros.close();
				}
				if(num==""){
				   num="0";
				}
				int j=Integer.parseInt(num);//	页面所对应人员类别人数
				if(j<k){
					String asl = "select sc010 from sc01 where sc000='"+ codeitemid+"' and b0110='"+b0110+"' and budget_id="+budget_id.toString() 
					           + " and A0101='"+newStaffA0101+"' order by sc010 desc";
					ras=dao.search(asl);
					try{
						for(int m=k-j;m>0;m--){
							if(ras.next()){
								max_sc010=ras.getString(1);
								String sol="delete from sc01 where sc010="+max_sc010;
								dao.update(sol);
							}
						}
					}finally{
						if (ras!=null) ras.close();
					}
				}
				if(j>k){
					for(int m=0;m<(j-k);m++){
						// int sc010=Integer.parseInt(max_sc010)+m+1;
						int sc010 = Integer.parseInt(new IDFactoryBean().getId("SC01.SC010", "", this.getFrameconn()));
				 		RecordVo vo=new RecordVo("sc01");
				 		vo.setInt("sc010", sc010);
				 		vo.setString("budget_id", budget_id);
				 		vo.setString("tab_id", tab_id);
				 		vo.setString("sc000", codeitemid);
				 		vo.setString("a0101", newStaffA0101);  // 新员工
				 		vo.setInt("a0000", BudgetFormulaResBo.NewStaffA0000);
				 		vo.setString("b0110", b0110);
				 		vo.setString("beginmonth", beginMonth);
				 		vo.setInt("endmonth", 12);
				 		dao.addValueObject(vo);
					}	
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
