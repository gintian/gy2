package com.hjsj.hrms.transaction.gz.gz_budget.budget_rule.definition;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Date;

public class AddBudgetDefTrans extends IBusiness{

	public void execute() throws GeneralException {
		String Create_date = PubFunc.getStringDate("yyyy-MM-dd"); //获取当前时间
		String end_date = "9999-12-31";//结束时间
		String isExistName = "0";//判断是否重名  0 未重名 1 已重名
		String isAdd = (String)this.getFormHM().get("isAdd");//判断是增加还是修改 
		String budgetTab_id = "";
		if("0".equals(isAdd) || "2".equals(isAdd)){//如果是重命名，还需要获得id号
			budgetTab_id = SafeCode.decode((String)this.getFormHM().get("budgetTab_id"));
		}
		String name = SafeCode.decode((String)this.getFormHM().get("name")).trim();
		name= name.replaceAll("#", "").replaceAll("&", "");
		try{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer sb = new StringBuffer();
			sb.append("select * from gz_budget_tab where tab_name='"+name+"'");
			this.frowset = dao.search(sb.toString());
			if(this.frowset.next()){//如果有记录，说明新增时增加了一个重复的，或重命名时命名了一个重复的
				isExistName = "1";
				isAdd = "0";//控制着显示第几页
			}else{//如果没有重名的，那么就插入新记录或修改记录
				if("1".equals(isAdd)){//如果是新增
					StringBuffer sbsql = new StringBuffer();
					sbsql.append("select MAX(tab_id)+1 tab_id,MAX(seq)+1 seq from gz_budget_tab");
					this.frowset = dao.search(sbsql.toString());
					if(this.frowset.next()){
						int tab_id = this.frowset.getInt("tab_id");
						int seq = this.frowset.getInt("seq");
						RecordVo vo=new RecordVo("gz_budget_tab");
						vo.setInt("tab_id", tab_id);
						vo.setString("tab_name", name);
						vo.setInt("tab_type", 4);
						vo.setInt("seq", seq);
						vo.setInt("analyseflag", 0);
						vo.setInt("bpflag", 0);
						vo.setInt("validflag", 0);
						vo.setDate("start_date", Create_date);
						vo.setDate("end_date", end_date);
						dao.addValueObject(vo);
					}
				}
				else if("2".equals(isAdd)){//另存为
					RowSet rs=null;
					StringBuffer sbsql = new StringBuffer();
					sbsql.append("select tab_type from gz_budget_tab where tab_id ="+budgetTab_id+" AND tab_type in (1,2,3)");
					this.frowset = dao.search(sbsql.toString());
					if(this.frowset.next()){
						isExistName="2";
					}
					else {
						sbsql.setLength(0);
						sbsql.append("select MAX(tab_id)+1 tab_id,MAX(seq)+1 seq from gz_budget_tab");
						this.frowset = dao.search(sbsql.toString());
						if(this.frowset.next()){
							int tab_id = this.frowset.getInt("tab_id");
							int seq = this.frowset.getInt("seq");
							String sql="insert into gz_budget_tab (tab_id,tab_name,tab_type,budgetgroup,codesetid,"
							          +"analyseflag,bpflag,tabcode,validflag,end_date,extattr,start_date,seq ) "
								      + "select "+ String.valueOf(tab_id)+",'"+name+"',"+"tab_type,budgetgroup,codesetid,"
							          +"analyseflag,bpflag,tabcode,validflag,end_date,extattr,"
							          + Sql_switcher.dateValue(DateStyle.dateformat(new Date(),"yyyy-MM-dd"))
							          +","+ String.valueOf(seq)+"  from gz_budget_tab where tab_id ="+budgetTab_id ;
							dao.update(sql);
							
							sbsql.setLength(0);
							sbsql.append("select * from gz_budget_formula where tab_id ="+budgetTab_id);
							rs= dao.search(sbsql.toString());
							while(rs.next()){
								String formula_id = String.valueOf(rs.getInt("formula_id"));
								sbsql.setLength(0);
								sbsql.append("select MAX(formula_id)+1 formula_id,MAX(seq)+1 seq from gz_budget_formula");
								this.frowset = dao.search(sbsql.toString());
								if(this.frowset.next()){
									int newformula_id =this.frowset.getInt("formula_id");
									int newformulaseq = this.frowset.getInt("seq");
									
									String sql1="insert into gz_budget_formula (formula_id,tab_id,formulaname,formuladcrp,formulatype,"
								          +"destflag,extattr,seq ) "
									      + "select "+ String.valueOf(newformula_id)+","+String.valueOf(tab_id)+",formulaname,formuladcrp,formulatype,"
								          +"destflag,extattr"
								          +","+ String.valueOf(newformulaseq)+"  from gz_budget_formula where formula_id ="+formula_id;
								    dao.update(sql1);
								}
							}
						
						}
					}
					isAdd="1";
				}
				else{//如果是修改
					StringBuffer sb_update = new StringBuffer();
					sb_update.append("update gz_budget_tab set tab_name='"+name+"' where tab_id="+budgetTab_id);
					dao.update(sb_update.toString());
				}
			}
			
			this.getFormHM().put("isAdd", isAdd);
			this.getFormHM().put("isExistName", isExistName);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
