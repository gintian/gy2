package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchAppiaryBodyTrans.java</p>
 * <p>Description>:查找报批的直接领导</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:jul 26, 2012 12:52:06 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchAppiaryBodyTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		ArrayList list = new ArrayList();
		String nbase = (String)this.getFormHM().get("nbase");
		String a0100 = (String)this.getFormHM().get("a0100");
		String sp_relation = (String)this.getFormHM().get("sp_relation"); // 审批关系
		String flag = (String)this.getFormHM().get("flag");
		String sp_level = (String)this.getFormHM().get("sp_level");
	//	WorkdiarySelStr selStr = new WorkdiarySelStr();
		ArrayList listldb = getSuperiorUser(sp_level,nbase, a0100, sp_relation, new ContentDAO(this.getFrameconn()));
		for (int i = 0; i < listldb.size(); i++) 
		{
			LazyDynaBean ldb=(LazyDynaBean) listldb.get(i);
			list.add(ldb.get("a0100")+":"+ldb.get("a0101"));
		}
		this.getFormHM().put("outname", list);
		this.getFormHM().put("flag", flag);
	}

	/**
	 * 得到直接上级姓名
	 * @param a0100
	 * @param dao
	 * @return
	 */
	public ArrayList getSuperiorUser(String sp_level,String nbase,String a0100,String sp_relation,ContentDAO dao) throws GeneralException 
	{
		String sql="";
		String uvNbase = this.getUserView().getDbname();
		String uvA0100 = this.getUserView().getA0100();
		String uvA0101 = this.getUserView().getUserName();
		RowSet rs = null;
		ArrayList list = new ArrayList();
		WorkPlanViewBo bo = new WorkPlanViewBo(this.getUserView(),this.getFrameconn());
		try 
		{
			RecordVo vo = new RecordVo(nbase+"a01");
			vo.setString("a0100", a0100);
			vo = dao.findByPrimaryKey(vo);
			String b0110 = vo.getString("b0110");
			String e0122 = vo.getString("e0122");
			String e01a1 = vo.getString("e01a1");
		// 参数设置中设置了审批关系就按设置的走，否则按之前的日志（考核关系）走
		if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
		{
			if(sp_level!=null && "1".equals(sp_level)){
				sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
				if(!bo.isHaveResults(sql,dao)){
					//先判断岗位
					sql=bo.getSuperSql(9, sp_relation, e01a1, "", "");
					//再判断部门
					if(!bo.isHaveResults(sql,dao)){
					  sql=bo.getSuperSql(9, sp_relation, "", e0122, "");
					}
					//再判断单位
					if(!bo.isHaveResults(sql,dao)){
					  sql=bo.getSuperSql(9, sp_relation, "", "", b0110);
					}
				}
			}else{
				//当前是用户
				if((nbase+a0100).equals(uvNbase+uvA0100)){
					//判断是否有直接领导，2级领导，3级领导，4级领导
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"9")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"10")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '10' and relation_id = '"+ sp_relation +"' ";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"11")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '11' and relation_id = '"+ sp_relation +"' ";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
					}
					//报批给岗位，或部门，或单位
					if("".equals(sql.trim())){
						//先判断岗位
						for (int i = 9; i <= 12; i++) {
							sql=bo.getSuperSql(i, sp_relation, e01a1, "", "");
							if(bo.isHaveResults(sql,dao))
								break;
						}
						//再判断部门
						if(!bo.isHaveResults(sql,dao)){
							for (int i = 9; i <= 12; i++) {
								sql=bo.getSuperSql(i, sp_relation, "", e0122, "");
								if(bo.isHaveResults(sql,dao))
									break;
							}
						}
						//再判断单位
						if(!bo.isHaveResults(sql,dao)){
							for (int i = 9; i <= 12; i++) {
								sql=bo.getSuperSql(i, sp_relation, "", "", b0110);
								if(bo.isHaveResults(sql,dao))
									break;
							}
						}
					}
				}
				//当前是直接领导
				if("9".equals(bo.whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"10")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '10' and relation_id = '"+ sp_relation +"' ";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"11")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '11' and relation_id = '"+ sp_relation +"' ";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
					}
				}
				//当前是二级领导
				if("10".equals(bo.whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"11")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '11' and relation_id = '"+ sp_relation +"' ";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
					}
				}
				//当前是三级领导
				if("11".equals(bo.whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"12")){
						sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+nbase+a0100+"' and sp_grade = '12' and relation_id = '"+ sp_relation +"' ";
					}
				}
				//判断当前用户是其岗位，部门还是单位领导，并且判断是哪级领导
				if("".equals(sql.trim())){
					String[] strArray = bo.getInfo(uvNbase,uvA0100,nbase,a0100,dao,sp_relation);
					if(strArray!=null){
						String objectid = strArray[0];
						int level = Integer.parseInt(strArray[1]);
						for (int i = level; i <= 12; i++) {
							sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+objectid+"' and sp_grade = '"+(i+1)+"' and relation_id = '"+ sp_relation +"' ";
							if(bo.isHaveResults(sql,dao)){
								break;
							}
						}
					}
				}
			}

//			try 
//			{
//				if(sql.trim().equals("")){
//					if(!isHaveResults(sql,dao)){
//						//先找所属岗位是否定义审批关系，再找所属部门，再找所属单位
//						if(sp_level!=null && sp_level.equals("1")){
//							sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '@K"+e01a1+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
//							if(!isHaveResults(sql,dao)){
//								sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = 'UM"+e0122+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
//							}
//							if(!isHaveResults(sql,dao)){
//								sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = 'UN"+b0110+"' and sp_grade = '9' and relation_id = '"+ sp_relation +"' ";
//							}
//						}else{
//							//当前是用户
//							if((nbase+a0100).equals(uvNbase+uvA0100)){
//								    boolean breakflag = false;
//									for (int j = 1; j <=3; j++) {
//                                        if(breakflag)
//                                        	break;
//										String temp = null;
//										String fieldtemp = null;
//										if(j==1){
//											temp="@K";
//											fieldtemp = e01a1;
//										}
//										if(j==2){
//											temp="UM";
//											fieldtemp = e0122;
//										}
//										if(j==3){
//											temp="UN";
//											fieldtemp = b0110;
//										}
//										for (int i = 9; i <= 12; i++) {
//											sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+temp+fieldtemp+"' and sp_grade = '"+i+"' and relation_id = '"+ sp_relation +"' ";
//											if(isHaveResults(sql,dao)){
//												breakflag = true;
//												break;
//											}
//								        }
//								    }
//							}else{
//								//当前是用户岗位or部门or单位的直接领导or2级or3级pr4级领导
//								String[] strArray = getInfo(uvNbase,uvA0100,nbase,a0100,dao,sp_relation);
//								if(strArray!=null){
//									String objectid = strArray[0];
//									int level = Integer.parseInt(strArray[1]);
//									for (int i = level; i <= 12; i++) {
//										sql="select object_id,mainbody_id,a0101 from t_wf_mainbody where object_id = '"+objectid+"' and sp_grade = '"+(i+1)+"' and relation_id = '"+ sp_relation +"' ";
//										if(isHaveResults(sql,dao)){
//											break;
//										}
//									}
//								}
//							}
//							
//						}
//					}
//				}
//			} catch (Exception e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}else
		{	
			if(sp_level!=null && "1".equals(sp_level)){
				sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					sql+=" level_o";
				else
					sql+=" level ";
				//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
				String body_id="1";
				sql+="="+body_id+"  and pmb.object_id='"+a0100+"'";
			}else{
				//当前是用户
				if((nbase+a0100).equals(uvNbase+uvA0100)){
					//判断是否有直接领导
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"1")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '1'  and pmb.object_id='"+a0100+"'";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"0")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '0'  and pmb.object_id='"+a0100+"'";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-1")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '-1'  and pmb.object_id='"+a0100+"'";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '-2'  and pmb.object_id='"+a0100+"'";
					}
				}
				//当前是直接领导
				if("9".equals(bo.whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"0")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '0'  and pmb.object_id='"+a0100+"'";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-1")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '-1'  and pmb.object_id='"+a0100+"'";
					}else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '-2'  and pmb.object_id='"+a0100+"'";
					}
				}
				//当前是二级领导
				if("10".equals(bo.whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-1")){
					sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						sql+=" level_o";
					else
						sql+=" level ";
					//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
					sql+="= '-1'  and pmb.object_id='"+a0100+"'";
				  }else if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
					sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						sql+=" level_o";
					else
						sql+=" level ";
					//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
					sql+="= '-2'  and pmb.object_id='"+a0100+"'";
				  }
			    }
				//当前是三级领导
				if("11".equals(bo.whichCurrentLevel(uvNbase,uvA0100,nbase,a0100,dao,sp_relation))){
					if(bo.isHaveLeader(nbase,a0100,sp_relation,dao,"-2")){
						sql="select pmb.*,pmbs.name from per_mainbody_std pmb,per_mainbodyset pmbs where pmb.body_id=pmbs.body_id  and ";		
						if(Sql_switcher.searchDbServer()==Constant.ORACEL)
							sql+=" level_o";
						else
							sql+=" level ";
						//body_id=5:本人 -2：第四级领导 ,-1：第三级领导,0：主管领导,1：直接上级
						sql+="= '-2'  and pmb.object_id='"+a0100+"'";
					}
				}
			}
		}
		
			if(!"".equals(sql.trim())){
				rs = dao.search(sql);
				while(rs.next())
				{
					LazyDynaBean bean=new LazyDynaBean();
					bean.set("a0101", rs.getString("a0101"));
					if(sp_relation!=null && sp_relation.trim().length()>0 && !"null".equalsIgnoreCase(sp_relation))
						bean.set("a0100", rs.getString("mainbody_id"));
					else
						bean.set("a0100", "Usr"+rs.getString("mainbody_id"));
					list.add(bean);
				}
			}
		} catch (SQLException e) 
		{
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			if(rs!=null)
			{
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}	
	
	
}