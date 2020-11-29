package com.hjsj.hrms.businessobject.train;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.utils.Sql_switcher;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

public class TrainBudgetBo {
	
	private Connection conn=null;
	private String budget;
	
	public TrainBudgetBo(){}
	public TrainBudgetBo(Connection con) {
		this.conn = con;
		this.budget = getTrainBudgetParam();
	}
	
	public String getTrainBudgetParam(){
		ConstantXml constantbo = new ConstantXml(this.conn,"TR_PARAM");
		return constantbo.getValue("train_budget");
	}
	
	/**
	 * 编辑培训计划剩余费用
	 * @param state 0=新增，1=修改
	 * @param planid 计划id
	 * @param plancost 该计划预计费用(-999999为计划新增或批准操作 默认等于预计总费用。删除=0且state=1)
	 * @param isclass 是否是操作培训班中关联的计划
	 */
	public void updateTrainPlanBudget(String state, String planid, double plancost, boolean isclass){
		ContentDAO dao = new ContentDAO(this.conn);
		String b0110 = "";
		String e0122 = "";
		String r2503 = "";//年度
		double changecost = 0;//变动费用
		double _budget = 0;//该计划剩余费用
		RowSet rs = null;
		String sql = "select b0110,e0122,r2503,r2506,"+budget+"  from r25 where r2501='"+planid+"'";
		try {
			rs = dao.search(sql.toString());
			if(rs.next()){
				b0110 = rs.getString("b0110");
				e0122 = rs.getString("e0122");
				r2503 = rs.getString("r2503");
				r2503 = r2503!=null ? "'"+r2503+"'" : r2503;
				double r2506 = rs.getDouble("r2506");
				String _b = rs.getString(budget);
				_budget = _b==null||_b.length()<1 ? r2506 : rs.getDouble(budget);
				plancost = plancost == -999999 ? r2506 : plancost;
				changecost = "1".equals(state)&&!isclass ? plancost-r2506 : plancost;
			}else {
                return;
            }
			
			while(true){
				if(isclass){
					if(changecost!=0f){//进行修改操作
						sql = "update r25 set " + budget + "=" +( _budget-changecost) + "where r2501='" + planid + "'";
						dao.update(sql);
					}
					if(_budget>=changecost&&_budget>=0) {
                        break;
                    }
					if(_budget>0) {
                        changecost = changecost-_budget;
                    }
					if(_budget<0&&changecost<0) {
                        changecost = _budget;
                    }
				}
				
				String code = e0122!=null&&e0122.length()>0?e0122:b0110;
				if(code==null||code.length()<1) {
                    break;
                }
				String[] _v = parentCode(code);
				if(_v==null) {
                    break;
                }
				sql = "select r2501,b0110,e0122,r2503,r2506,"+budget+"  from r25 where r2503="+r2503;
				if("UN".equalsIgnoreCase(_v[0])){
					sql += " and b0110='"+_v[1]+"' and (e0122 is null or e0122='')";
				}else if("UM".equalsIgnoreCase(_v[0])){
					sql += " and e0122='"+_v[1]+"'";
				}else {
                    break;
                }
				rs = dao.search(sql);
				if(rs.next()){
					planid = rs.getString("r2501");
					b0110 = rs.getString("b0110");
					e0122 = rs.getString("e0122");
					r2503 = rs.getString("r2503");
					r2503 = r2503!=null ? "'"+r2503+"'" : r2503;
					double r2506 = rs.getDouble("r2506");
					String _b = rs.getString(budget);
					_budget = _b==null||_b.length()<1 ? r2506 : rs.getDouble(budget);
					//changecost = "1".equals(state) ? changecost-r2506 : changecost;
				
					if(!isclass){
						if(changecost!=0f){//进行修改操作
							sql = "update r25 set " + budget + "=" +( _budget-changecost) + " where r2501='" + planid + "'";
							dao.update(sql);
						}
						if(_budget>=changecost&&_budget>=0) {
                            break;
                        }
						if(_budget>0) {
                            changecost = changecost-_budget;
                        }
						if(_budget<0&&changecost<0) {
                            changecost = _budget;
                        }
					}
				}else{
					if("UN".equalsIgnoreCase(_v[0])) {
                        b0110 = _v[1];
                    } else {
                        e0122 = _v[1];
                    }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 编辑培训班费用
	 * @param state 0=关联计划，1=撤销关联或删除培训班，2=修改培训班
	 * @param classid 培训班id
	 * @param cost 修改培训班预计费用
	 * @param _planid 修改计划id
	 */
	public void updateTrainBudget(String state,String classid,double cost,String _planid){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		double classcost = 0;//培训班费用
		double plancost = 0;//须修改计划变动费用
		String planid = "";
		StringBuffer sql = new StringBuffer();
		sql.append("select r3111,r3125 from r31 where r3101='"+classid+"'");
		try {
			rs = dao.search(sql.toString());
			if(rs.next()){
				classcost = rs.getDouble("r3111");
				planid = rs.getString("r3125");
			}else {
                return;
            }
			
			if("0".equals(state)){
				if(planid==null||planid.length()<1) {
                    return;
                }
				plancost = classcost;
				updateTrainPlanBudget("1", planid, plancost, true);
			}else if("1".equals(state)){
				if(planid==null||planid.length()<1) {
                    return;
                }
				plancost = 0 - classcost;
				updateTrainPlanBudget("1", planid, plancost, true);
			}else if("2".equals(state)){
				plancost = cost - classcost;
				if((planid!=null&&planid.length()>1)&&(_planid!=null&&planid.equals(_planid))&&plancost!=0){
					updateTrainPlanBudget("1", planid, plancost, true);
				}
				if((planid!=null&&planid.length()>1)&&(_planid!=null&&!planid.equals(_planid))){
					if(plancost!=0){
						updateTrainPlanBudget("1", planid, 0-classcost, true);
						updateTrainPlanBudget("1", _planid, cost, true);
					}else{
						updateTrainPlanBudget("1", planid, 0-classcost, true);
						updateTrainPlanBudget("1", _planid, classcost, true);
					}
				}
				if((planid!=null&&planid.length()>1)&&(_planid==null||_planid.length()<1)){
					updateTrainPlanBudget("1", planid, 0-classcost, true);
				}
				if((planid==null||planid.length()<1)&&(_planid!=null&&_planid.length()>1)){
					if(cost!=0) {
                        updateTrainPlanBudget("1", _planid, cost, true);
                    } else {
                        updateTrainPlanBudget("1", _planid, classcost, true);
                    }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 查看是否有下级计划
	 * @param planid
	 * @param year
	 * @param b0110
	 * @param e0122
	 * @return boolean
	 */
	public boolean isChildPlan(String planid,String year,String b0110,String e0122){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		boolean _b = false;
		if(b0110==null||b0110.length()<1) {
            return _b;
        }
		StringBuffer sql = new StringBuffer();
		if(e0122==null||e0122.length()<1) {
            sql.append("select 1 from r25 where r2503='"+year+"' and B0110 like '"+b0110+"%' and b0110<>'"+b0110+"'");
        } else {
            sql.append("select 1 from r25 where r2503='"+year+"' and e0122 like '"+e0122+"%' and e0122<>'"+e0122+"'");
        }
		try {
			rs = dao.search(sql.toString());
			if(rs.next()){
				_b = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return _b;
	}
	
	/**
	 * 查找上级部门或单位
	 * @param code
	 * @return [0]UN/UM   [1]parentId
	 */
	public String[] parentCode(String code){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		String[] _v = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select codesetid,codeitemid from organization");
		sql.append(" where (codesetid='UN' or codesetid='UM')");
		sql.append(" and " + Sql_switcher.dateValue(DateUtils.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date");
		sql.append(" and codeitemid=(select parentid from organization where codeitemid='"+code+"'  and codeitemid<>parentid)");
		try {
			rs = dao.search(sql.toString());
			if(rs.next()){
				_v = new String[2];
				_v[0] = rs.getString("codesetid");
				_v[1] = rs.getString("codeitemid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return _v;
	}
	
	public String getBudget() {
		return budget;
	}
	public void setBudget(String budget) {
		this.budget = budget;
	}
}