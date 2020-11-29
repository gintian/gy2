package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 防止用户修改链接参数横向越权问题
 * @author Administrator
 *
 */
public class CheckPrivSafeBo {

	private Connection conn;
	private UserView userView;
	
	/**
	 * @param conn 注：此类未对传入connection做关闭操作
	 * @param userView
	 */
	public CheckPrivSafeBo(Connection conn,UserView userView){
		this.conn=conn;
		this.userView =userView;
	}
	/**
	 * 验证管理范围，如果越权则返回实有的管理范围
	 * @param orgid
	 * @param moudle 业务模块号，无则传空字符串
	 * @return
	 */
	public String checkOrg(String orgid,String moudle){
		if(this.userView.isSuper_admin()) {
            return orgid;
        }
		orgid=orgid==null?"":orgid;
		moudle=moudle==null?"":moudle;
		boolean flag=orgid.startsWith("UN")||orgid.startsWith("UM")||orgid.startsWith("@K");
		String codesetid="";
		String codeitemid="";
		if(flag){
			codesetid=orgid.substring(0,2);
			codeitemid=orgid.substring(2);
		}
		if(moudle.length()>0){
			String busi = userView.getUnitIdByBusi(moudle);
			if (busi.length() > 0) {
				busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi);
			}else{
				busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
			}
			if(busi.length()>=2){
				if(busi.indexOf("`")!=-1){
					if(!"UN`".equals(busi)){
						StringBuffer sb = new StringBuffer();
						String[] tmps=busi.split("`");
						boolean b=false;
						String priv="";
						for(int i=0;i<tmps.length;i++){
							String a_code=tmps[i];
							if(a_code.length()>2){
								if(i==0){
									if(flag) {
                                        priv=a_code;
                                    } else {
                                        priv=a_code.substring(2);
                                    }
								}
								sb.append("','"+a_code.substring(2));
								if(flag){
									if(codeitemid.startsWith(a_code.substring(2))){
										b=true;
									}
								}else{
									if(orgid.startsWith(a_code.substring(2))){
										b=true;
									}
								}
							}
						}
						if(!b){
							orgid=priv;
						}
					}
				}else{
					if(flag){
						if(!codeitemid.startsWith(busi.substring(2))){
							orgid=busi;
						}
					}else{
						if(!orgid.startsWith(busi.substring(2))){
							orgid=busi.substring(2);
						}
					}
				}
			}else{
				orgid="";
			}
		}else{
			String manapriv = this.userView.getManagePrivCodeValue();
			if(manapriv.length()==0){
				if(this.userView.getManagePrivCode().length()<2) {
                    orgid="";
                }
			}else{
				if(flag){
					if(!codeitemid.startsWith(manapriv)){
						orgid=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
					}
				}else{
					if(orgid!=null && orgid.length()>0){
						if(manapriv!=null && manapriv.length()>0){
							if(manapriv.startsWith(orgid)){
								orgid=manapriv;
							}else if(orgid.startsWith(manapriv)){
								
							}else{
								orgid="1|";
							}					
						}
										
					}
				}
			}
		}
		return orgid;
	}
	
	/**
	 * 验证人员库越权，则返回第一个权限人员库
	 * @param dbpre
	 * @return
	 */
	public String checkDb(String dbpre){
		if(this.userView.isSuper_admin()) {
            return dbpre;
        }
		dbpre=dbpre==null?"":dbpre;
		if(this.userView.getPrivDbList().size()>0){
			String privdbs=this.userView.getPrivDbList().toString().toUpperCase();
			if(privdbs.indexOf(dbpre.toUpperCase())==-1){
				dbpre=(String)this.userView.getPrivDbList().get(0);
			}
		}else{
			dbpre="";
		}
		return dbpre;
	}
	
	/***
	 * 验证人员编号越权，则返回第一个权限内人员编号
	 * @param manapriv 传空字符串则按权限范围走，否则按此范围走(传非空字符串前提用checkOrg方法验证过，否则建议传空字符串)
	 * @param dbpre 传空字符串则按权限人员库走，否则按此人员库走(传非空字符串前提用checkDb方法验证过，否则建议传空字符串)
	 * @param a0100
	 * @param moudle
	 * @return
	 */
	public String checkA0100(String manapriv,String dbpre,String a0100,String moudle){
		if(this.userView.isSuper_admin()) {
            return a0100;
        }
		
		manapriv=manapriv==null?"":manapriv;
		dbpre=dbpre==null?"":dbpre;
		moudle=moudle==null?"":moudle;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList pridblist = null;
		ArrayList sqlParams = new ArrayList();
		try{
			//检查是否有 单位部门都为空的人，如果有直接返回a0100 
			if(dbpre.length()==0){
				pridblist = this.userView.getPrivDbList();
				if(pridblist.size()==0) {
                    return "";
                } else{
					for(int i=pridblist.size()-1;i>=0;i--){
						String pre=(String)pridblist.get(i);
						String sql="select '1' from "+pre+"a01 where a0100=? and (B0110='' or B0110 is null) and (E0122='' or E0122 is null)";
                        sqlParams.clear();
                        sqlParams.add(a0100);
						rs = dao.search(sql, sqlParams);
						if(rs.next()){
							return a0100;
						}
							
					}
				}
			}else{
			    String sql="select '1' from "+dbpre+"a01 where a0100=? and (B0110='' or B0110 is null) and (E0122='' or E0122 is null) ";
                sqlParams.clear();
                sqlParams.add(a0100);
				rs = dao.search(sql, sqlParams);
				if(rs.next()){
					return a0100;
				}
			}
			
			if(moudle.length()==0){
				if(manapriv.length()==0){
					manapriv=this.userView.getManagePrivCodeValue();
					if(this.userView.getManagePrivCode().length()>=2) {
                        manapriv=manapriv.length()==0?"`":manapriv;
                    }
				}else{
					manapriv=manapriv.replaceAll("UN", "").replaceAll("UM", "").replaceAll("@K", "");
				}
				manapriv=manapriv.length()==0?"#":manapriv;
				manapriv= "`".equals(manapriv)?"":manapriv;
				if(dbpre.length()==0){
					//ArrayList dblist = this.userView.getPrivDbList();
					if(pridblist.size()==0){
						a0100="";
					}else{
						String tmpa0100="";
						boolean flag = false;
						for(int i=pridblist.size()-1;i>=0;i--){
							String pre=(String)pridblist.get(i);
							tmpa0100 = getCheckA0100(pre, manapriv, a0100);
							if(StringUtils.isNotEmpty(tmpa0100)) {
                                flag = true;
                                break;
							}
						}
						
						if(StringUtils.isEmpty(tmpa0100)) {
						    String nbase = (String) pridblist.get(pridblist.size() - 1);
						    tmpa0100 = getCheckA0100(nbase, manapriv, "");
						}
						
						if(!flag) {
                            a0100=this.isPartPerson(manapriv, pridblist, a0100, tmpa0100);
                        } else {
                            a0100 = tmpa0100;
                        }
					}
				}else{
					String tmpa0100="";
					boolean flag = false;
					tmpa0100 = getCheckA0100(dbpre, manapriv, a0100);
					if(StringUtils.isNotEmpty(tmpa0100)) {
                        flag = true;
                    }
					
                    if(StringUtils.isEmpty(tmpa0100)) {
                        tmpa0100 = getCheckA0100(dbpre, manapriv, "");
                    }
                    
					if(!flag){
						ArrayList<String> dblist = new ArrayList<String>();
						dblist.add(dbpre);
						a0100=this.isPartPerson(manapriv, dblist, a0100, tmpa0100);
					}else {
                        a0100=tmpa0100;
                    }
				}
			}else{
				if(manapriv.length()==0){
					String busi = userView.getUnitIdByBusi(moudle);
					if (busi.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
					busi=busi.replaceAll("UN", "").replaceAll("UM", "").replaceAll("@K", "");
					
					if(busi.length()>=2){
						busi=busi.replaceAll("UN", "").replaceAll("UM", "").replaceAll("@K", "");
					}else{
						busi="###";
					}
					manapriv=busi;
				}else{
					manapriv=manapriv.replaceAll("UN", "").replaceAll("UM", "").replaceAll("@K", "");
					//manapriv=manapriv.length()==0?"#":manapriv;
					manapriv= "`".equals(manapriv)?"":manapriv;
				}
				StringBuffer sb = new StringBuffer();
				String[] tmps=manapriv.split("`");
				for(int i=0;i<tmps.length;i++){
					String a_code=tmps[i];
					sb.append(" or "+Sql_switcher.isnull("b0110", "' '")+" like '"+a_code+"%' or "+Sql_switcher.isnull("e0122", "' '")+" like '"+a_code+"%' or "+Sql_switcher.isnull("e01a1", "' '")+" like '"+a_code+"%'");
				}
				if(dbpre.length()==0){
					ArrayList dblist = this.userView.getPrivDbList();
					if(dblist.size()==0){
						a0100="";
					}else{
						String tmpa0100="";
						boolean flag = false;
						for(int i=dblist.size()-1;i>=0;i--){
							String pre=(String)dblist.get(i);
							String sql="select a0100 from "+pre+"a01 where 1=2 "+sb.toString();
							rs =dao.search(sql);
							int index=0;
							while(rs.next()){
								if(index==0&&i==0){
									tmpa0100=rs.getString("a0100");
									index++;
								}
								if(rs.getString("a0100").equals(a0100)){
									tmpa0100=a0100;
									flag = true;
									break;
								}
							}
							/*if(tmpa0100.length()!=0)
								break;*/
						}
						if(!flag) {
                            a0100=this.isPartPerson(manapriv, dblist, a0100, tmpa0100);
                        } else {
                            a0100 = tmpa0100;
                        }
					}
				}else{
					String tmpa0100="";
					String sql="select a0100 from "+dbpre+"a01 where 1=2 "+sb.toString();
					rs =dao.search(sql);
					int index=0;
					boolean flag = false;
					while(rs.next()){
						if(index==0){
							tmpa0100=rs.getString("a0100");
							index++;
						}
						if(rs.getString("a0100").equals(a0100)){
							tmpa0100=a0100;
							flag = true;
							break;
						}
					}
					if(!flag){
						ArrayList dblist = new ArrayList();
						dblist.add(dbpre);
						a0100=this.isPartPerson(manapriv, dblist, a0100, tmpa0100);
					}else {
                        a0100=tmpa0100;
                    }
				}
			}
		}catch(Exception e){
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
		return a0100;
	}
	
	/**
     * 获取权限内的人员编号
     * @param nbase 人员库
     * @param manapriv 权限范围代码
     * @param a0100 人员编号
     * @return
     */
    private String getCheckA0100(String nbase, String manapriv, String a0100) {
        RowSet rs = null;
        String tempAa0100 = "";
        try {
            if(StringUtils.isEmpty(nbase)) {
                return "";
            }
            
            StringBuffer sql = new StringBuffer();
            sql.append("select a0100 from " + nbase + "a01");
            sql.append(" where 1=1");
            if(StringUtils.isNotEmpty(manapriv)) {
                sql.append(" and (" + Sql_switcher.isnull("b0110", "' '") + " like ?");
                sql.append(" or " + Sql_switcher.isnull("e0122", "' '") + " like ?");
                sql.append(" or " + Sql_switcher.isnull("e01a1", "' '") + " like ?)");
            }
            
            if(StringUtils.isNotEmpty(a0100)) {
                sql.append(" and a0100=?");
            }
            
            sql.append(" order by a0000");
            ArrayList<String> sqlParams = new ArrayList<String>();
            if(StringUtils.isNotEmpty(manapriv)) {
            	sqlParams.add(manapriv + "%");
            	sqlParams.add(manapriv + "%");
            	sqlParams.add(manapriv + "%");
            }
            
            if(StringUtils.isNotEmpty(a0100)) {
                sqlParams.add(a0100);
            }
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs =dao.search(sql.toString(), sqlParams);
            if(rs.next()) {
                tempAa0100 = rs.getString("a0100");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return tempAa0100;
    }
    
	private String isPartPerson(String manapriv,ArrayList dblist,String a0100,String tmpa0100){
		ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");//兼职单位
		list.add("setid");//兼职子集
		list.add("appoint");//兼职标识
		list.add("pos");//兼职职务
		list.add("dept");//兼职部门
		list.add("order");//兼职排序
		list.add("format");//兼职显示格式
		String part_setid="";
		String part_unit="";
		String appoint=" ";
		String flag="";
		String part_pos="";
		String part_dept="";
		String part_order="";
		String part_format="";
		RowSet rs = null;
		try{
			//兼职处理
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
	    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
	    	if(map!=null&& map.size()!=0){
				if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0) {
                    flag=(String)map.get("flag");
                }
				if(flag!=null&& "true".equalsIgnoreCase(flag))
				{
					if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0) {
                        part_unit=(String)map.get("unit");
                    }
					if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0) {
                        part_setid=(String)map.get("setid");
                    }
					if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0) {
                        appoint=(String)map.get("appoint");
                    }
					if(map.get("pos")!=null && ((String)map.get("pos")).trim().length()>0) {
                        part_pos=(String)map.get("pos");
                    }
					if(map.get("dept")!=null && ((String)map.get("dept")).trim().length()>0) {
                        part_dept=(String)map.get("dept");
                    }
					if(map.get("order")!=null && ((String)map.get("order")).trim().length()>0) {
                        part_order=(String)map.get("order");
                    }
					if(map.get("format")!=null && ((String)map.get("format")).trim().length()>0) {
                        part_format=(String)map.get("format");
                    }
				}		
			}
	    	boolean isreturn=false;
			if(!"true".equalsIgnoreCase(flag)) {
                isreturn=true;
            }
			if(part_setid==null||part_setid.trim().length()<=0) {
                isreturn=true;
            }
	    	if(a0100==null||a0100.trim().length()<=0) {
                isreturn=true;
            }
	    	if(dblist.size()==0) {
                isreturn=true;
            }
	    	if(part_pos==null||part_pos.trim().length()<=0) {
                isreturn=true;
            }
	    	if(appoint==null||appoint.trim().length()<=0) {
                isreturn=true;
            }
	    	FieldItem fielitem=DataDictionary.getFieldItem(part_pos);
	    	if(fielitem==null) {
                isreturn=true;
            }
	    	if(!isreturn){
		    	ContentDAO dao=new ContentDAO(conn);
		    	StringBuffer sql=new StringBuffer();
		    	String ta="";
		    	String[] tmps =manapriv.split("`");
		    	for(int i=dblist.size()-1;i>=0;i--){
		    		String dbpre = (String)dblist.get(i);
			       sql.append("select a0100 from "+dbpre+part_setid);
			       sql.append(" where "+appoint+"='0' and (1=2");
			       if(part_unit!=null&&part_unit.length()>0){
			    	   for(int n=0;n<tmps.length;n++){
			    		   if(tmps[n].length()>0) {
                               sql.append(" or "+part_unit+" like '"+tmps[n]+"%'");
                           }
			    	   }
			       }
			       if(part_dept!=null&&part_dept.length()>0){
			    	   for(int n=0;n<tmps.length;n++){
			    		   if(tmps[n].length()>0) {
                               sql.append(" or "+part_dept+" like '"+tmps[n]+"%'");
                           }
			    	   }
			       }
			       sql.append(")");
				   rs=dao.search(sql.toString());
				   while(rs.next()){
					   String a = rs.getString("a0100");
					   if(a0100.equals(a)){
						   ta=a;
						   break;
					   }
				   }
				   if(ta.length()!=0) {
                       break;
                   }
		    	}
		    	if(ta.length()==0){
					a0100=tmpa0100;
				}
	    	}else{
	    	    a0100=tmpa0100;
	    	}
		}catch(Exception e){
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
		return a0100;
	}
	
	/**
	 * 查看有无此资源权限
	 * @param type
	 * @param res_id
	 * @return
	 */
	public String checkResource(int type,String res_id){
		if(this.userView.isSuper_admin()) {
            return res_id;
        }
		if(!this.userView.isHaveResource(type, res_id)){
			/*String res_ids=this.userView.getResourceString(type);
			String[] tmps=res_ids.split(",");
			if(tmps.length>0){
				res_id=tmps[0];
			}else*/
				res_id="-1";
		}
		return res_id;
	}
	/**
     * 查看有无此子集权限(包含分类授权的校验)
     * @param fieldset
     * @param domain 1A 2B 3K
     * @return
     */
	public String checkFieldSet(String nbase,String fieldsetid, String a0100, int domain, ContentDAO dao){
	    if(this.userView.isSuper_admin()) {
            return fieldsetid;
        }
	    
	    boolean flag = false;
	    InfoUtils infoUtils=new InfoUtils();
	    String sub_type=infoUtils.getOneselfFenleiType(nbase, a0100, "", dao);//人员分类
	    ArrayList privFieldSets = new ArrayList();
	    if(sub_type!=null&&sub_type.length()>0) {
	        //得到分类授权子集
	        privFieldSets=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
	        if(privFieldSets==null||privFieldSets.size()<=0) {
                privFieldSets=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
            }
	        
	        FieldSet fieldset = null;
	        for (int i = 0; i < privFieldSets.size(); i++) {
	            fieldset=(FieldSet)privFieldSets.get(i);
	            if(fieldsetid.equals(fieldset.getFieldsetid())) {
	                flag = true;
	                break;
	            }
	        }
	        
	        if(!flag) {
	            if(privFieldSets != null && privFieldSets.size() > 0) {
                    fieldsetid = ((FieldSet)privFieldSets.get(0)).getFieldsetid();
                } else {
                    fieldsetid = "";
                }
	        }
	        
	    } else if("0".equals(this.userView.analyseTablePriv(fieldsetid))){
            privFieldSets = this.userView.getPrivFieldSetList(domain);  //获得所有权限的子集
            FieldSet fieldset = null;
            if(privFieldSets != null && privFieldSets.size()>0){
                fieldset=(FieldSet)privFieldSets.get(0);
                fieldsetid = fieldset.getFieldsetid();
            }else{
                fieldsetid="";
            }
        }
        
        return fieldsetid;
    }
	/**
	 * 查看有无此子集权限
	 * @param fieldset
	 * @param domain 1A 2B 3K
	 * @return
	 */
	public String checkFieldSet(String fieldsetid,int domain){
		if(this.userView.isSuper_admin()) {
            return fieldsetid;
        }
		
		if("0".equals(this.userView.analyseTablePriv(fieldsetid))){
			ArrayList privFieldSets=this.userView.getPrivFieldSetList(domain);
			FieldSet fieldset = null;
			if(privFieldSets.size()>1){
				fieldset=(FieldSet)privFieldSets.get(0);
				fieldsetid = fieldset.getFieldsetid();
			}else{
				fieldsetid="";
			}
		}
		return fieldsetid;
	}
	/**
	 * 判断当前用户是否有此考核计划的权限   用于安全改造
	 * zhaoxg add 2014-9-10（绩效模块）
	 * @param u
	 * @param planid
	 * @return
	 * @throws GeneralException 
	 */
	public boolean isHavePriv(UserView u,String planid) throws GeneralException{
		boolean flag=true;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			if(planid==null||planid.length()<1){
				return flag;
			}
			RowSet rs = null;
			String _sql = "select * from per_plan where plan_id="+planid+"";
			rs = dao.search(_sql);
			if(!rs.next()){//如果per_plan表里面没plan_id 那么也不怕他乱改
				return flag;
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select * from per_plan where 1=1 and plan_id="+planid+"");
			if(u.hasTheFunction("3260207") && u.hasTheFunction("3260208")) {
                sql.toString();
            } else if(u.hasTheFunction("3260207")) // 负责人员和自己创建的绩效考核计划
            {
                sql.append(" and (object_type=2 or create_user='" + u.getUserName() + "')");
            } else if(u.hasTheFunction("3260208")) // 负责团队和自己创建的绩效考核计划
            {
                sql.append(" and (object_type!=2 or create_user='" + u.getUserName() + "')");
            } else if((!u.hasTheFunction("3260207")) && (!u.hasTheFunction("3260208"))) // 没有授权任何绩效计划,只显示自己创建的绩效考核计划
            {
                sql.append(" and (create_user='" + u.getUserName() + "')");
            }
			rs.close();
			rs=dao.search(sql.toString());
			if(!rs.next()){
				flag = false;
				throw GeneralExceptionHandler.Handle(new GeneralException("没有授权"+planid+"计划或者计划不是本人所建！"));
			}
			rs.close();
			sql.append(this.getUserViewPrivWhere(u));
			rs=dao.search(sql.toString());
			if(!rs.next()){
				flag = false;
				throw GeneralExceptionHandler.Handle(new GeneralException(""+planid+"计划不在您的用户权限范围内！"));
			}
			rs.close();
			String controlByKHMoudle = getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			rs = dao.search(sql.toString());
			if(rs.next())
			{					
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rs.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)){
							flag = false;
							throw GeneralExceptionHandler.Handle(new GeneralException("您没有"+planid+"计划所关联模板的权限！"));
						}					
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	
	
	/**
	 * 判断当前用户是否有此考核计划的权限   用于安全改造(自助)
	 * zhaoxg add 2014-9-10（绩效模块）
	 * @param u
	 * @param planid
	 * @return
	 * @throws GeneralException 
	 */
	public boolean isHavePriv_self(UserView u,String planid) throws GeneralException{
		boolean flag=true;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			if(planid==null||planid.length()<1){
				return flag;
			}
			RowSet rs = null;
			String _sql = "select * from per_plan where plan_id="+planid+"";
			rs = dao.search(_sql);
			if(!rs.next()){//如果per_plan表里面没plan_id 那么也不怕他乱改
				return flag;
			}
			StringBuffer sql = new StringBuffer();
			sql.append("select * from per_plan where 1=1 and plan_id="+planid+"");	
			sql.append(this.getUserViewPrivWhere(u));
			rs=dao.search(sql.toString());
			if(!rs.next()){
				flag = false;
				throw GeneralExceptionHandler.Handle(new GeneralException(""+planid+"计划不在您的用户权限范围内！"));
			}
			rs.close();
			String controlByKHMoudle = getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			rs = dao.search(sql.toString());
			if(rs.next())
			{					
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && "True".equalsIgnoreCase(controlByKHMoudle))
				{
					String template_id = rs.getString("template_id");				
					if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
					{
						//  写权限 template_id  读权限 template_id+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id)){
							flag = false;
							throw GeneralExceptionHandler.Handle(new GeneralException("您没有"+planid+"计划所关联模板的权限！"));
						}					
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	
	
	
	/**
	 * 判断计划号和模板号是否搭配   用于安全改造
	 * zhaoxg add 2014-9-10
	 * @param tempid
	 * @param planid
	 * @return
	 */
	public boolean isTempAndPlan(String tempid,String planid){
		boolean flag=true;
		ContentDAO dao = new ContentDAO(this.conn);
		try{
			if(tempid==null||tempid.length()<1||"isNull".equals(tempid)||planid==null||planid.length()<1){
				return flag;
			}
			String sql = "select template_id from per_plan where plan_id="+planid;
			RowSet rs=dao.search(sql);
			if(rs.next()){
				if(!tempid.equals(rs.getString("template_id"))){
					flag=false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	/**
	 * 根据用户权限获得计划列表
	 * 1、业务用户：先取业务操作单位->操作单位->管理范围
     * 2、自助用户：先取关联的业务用户的（业务操作单位->操作单位）->自身的业务操作单位->管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围 
	 * zhaoxg add 2014-9-10
	 */
	public String getUserViewPrivWhere(UserView userView)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.trim().length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    } else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2))) {
                        tempSql.append(" or  b0110 like '" + temp[i].substring(2) + "%'");
                    }
				}
				if(tempSql!=null && tempSql.toString().trim().length()>0) {
                    buf.append(" and ( " + tempSql.substring(3) + " or b0110 like 'HJSJ%' ) ");
                }
			} 
			else if((!userView.isSuper_admin()) && (!"UN`".equalsIgnoreCase(operOrg)))
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
                    {
                        buf.append(" and 1=1 ");
                    } else {
                        buf.append(" and ( b0110 like '" + codevalue + "%'  or b0110 like 'HJSJ%' )");
                    }
				} else {
                    buf.append(" and ( 1=2  or b0110 like 'HJSJ%' )");
                }
			}
			str = buf.toString();
		}

		return str;
	}
    /**
     * 考核计划按模板权限控制, True,False(默认) 
     * zhaoxg add 2014-9-10
     * @return
     */
	public String getControlByKHMoudle()
	{
		String controlByKHMoudle = "False";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
		    if ( rowSet.next())
		    {
				String str_value = rowSet.getString("str_value");
				if (str_value != null && str_value.trim().length()>0)
				{						
				    Document doc = PubFunc.generateDom(str_value);
				    String xpath = "//Per_Parameters";
				    XPath xpath_ = XPath.newInstance(xpath);
				    Element ele = (Element) xpath_.selectSingleNode(doc);
				    Element child;
				    if (ele != null)
				    {
						child = ele.getChild("Plan");
						if (child != null)
						{
						    controlByKHMoudle = child.getAttributeValue("ControlByKHMoudle");
						}
				    }
				}
		    }						
			if(rowSet!=null) {
                rowSet.close();
            }
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return controlByKHMoudle;
	}
	/**
	 * 判断当前用户是否有查看计划的权限   主要用于多人评分
	 * zhaoxg add 2014-9-16
	 * @param planid
	 * @return
	 * @throws GeneralException
	 */
	public boolean isPlanIdPriv(String planid) throws GeneralException{
		boolean flag = false;
		try{
			if(planid==null||planid.length()==0){
				return true;
			}
			ContentDAO dao = new ContentDAO(this.conn);		
			// 得到绩效考核计划列表
			String perPlanSql = "select plan_id from per_plan where ( status=4 or status=6 ) ";
			if (!userView.isSuper_admin()) {
                perPlanSql += "and plan_id in (select plan_id from per_mainbody where mainbody_id='"
                        + userView.getA0100() + "' )";
            }
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
                perPlanSql+=" and 1=2 ";
            }
			perPlanSql += "  and ( Method=1 or method is null ) order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			RowSet rs = dao.search(perPlanSql);
			while(rs.next()){
				if(planid.equals(rs.getString("plan_id"))){
					flag=true;
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	/**
	 * 判断当前用户是否有该考核指标的权限
	 * zhaoxg add 2014-9-17
	 * @return
	 * @throws GeneralException
	 */
	public boolean isHaveItemPriv(String point_id) throws GeneralException{
		boolean flag = true;
		try{
			if(point_id==null||point_id.length()==0|| "-1".equals(point_id)) {
                return flag;
            }
			if (point_id.startsWith("i_")){
			    return true;
			}
			if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
			{					
				if(!userView.isRWHaveResource(IResourceConstant.KH_FIELD,point_id)&&!userView.isRWHaveResource(IResourceConstant.KH_FIELD,point_id+"R"))
				{
					flag = false;
					throw GeneralExceptionHandler.Handle(new GeneralException(" 您没有"+point_id+"的指标权限！"));					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	/**
	 * 判读用户是否有此薪资类别|保险类别的权限
	 * @param salaryid
	 * @param gz_module 1:保险  0：薪资
	 * @return
	 * @author dengcan
	 * @throws GeneralException
	 */
	public  boolean isSalarySetResource(String salaryid,String gz_module) throws GeneralException
	{
		boolean isPriv=true;
		
		if(!this.userView.isHaveResource(IResourceConstant.GZ_SET,salaryid)&&!this.userView.isHaveResource(IResourceConstant.INS_SET,salaryid)) 
		{
			isPriv=false;
			
			if(gz_module==null) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noClassesAuthority")+"!"));
            } else if("1".equals(gz_module)) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noInsAuthority")+"!"));
            } else {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.noGzAuthority")+"!"));
            }
		}
		
		return isPriv;
	}
	
	
	/**
	 * 判断用户是否有凭证权限
	 * @param voucherID
	 * @return
	 */
	public boolean isVoucherPriv(String voucherID)
	{
		boolean isPriv=true;
		try{
			 
			if(!userView.isSuper_admin()&&!"1".equals(userView.getGroupId()))
			{	
				ContentDAO dao = new ContentDAO(this.conn);
				ArrayList list=new ArrayList();
				list.add(new Integer(voucherID));
				RowSet rowSet=dao.search("select * from GZ_Warrant where pn_id=? ",list);
				if(rowSet.next())
				{
					String c_scope=rowSet.getString("c_scope")!=null?rowSet.getString("c_scope"):"";
					String[] salaryids=c_scope.split(",");
					boolean _isPriv=false; 
					for(int i=0;i<salaryids.length;i++)
					{
						if(salaryids[i]!=null&&salaryids[i].trim().length()>0)
						{
							if(this.userView.isHaveResource(IResourceConstant.GZ_SET,salaryids[i].trim())) {
                                _isPriv=true;
                            }
							if(this.userView.isHaveResource(IResourceConstant.INS_SET,salaryids[i].trim())) {
                                _isPriv=true;
                            }
						}
					}
					isPriv=_isPriv;
					
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return isPriv;
	}
	/**
	 * 校验机构编码是否是有效机构   wangb  20180504
	 * @param checkvalue  机构编号  
	 * @return
	 */
	public  String orgValidCheck(String checkvalue){
		String items = "";
		items = checkvalue.replaceAll("UN","").replaceAll("UM","").replaceAll("`",",").replaceAll(",,",",");
		items = items.lastIndexOf(",")==items.length()-1 ? items.substring(0, items.length()-1):items;
		items = items.indexOf(",") == 0 ? items.substring(1):items;
		items = items.replaceAll(",","','");
		items = "'" + items + "'";
		String sql = "select codesetid,codeitemid from organization where codeitemid in (" +items+") and ";
		if(Sql_switcher.searchDbServer()==1) //sql server 库
        {
            sql = sql + " end_date > getDate()";
        } else if(Sql_switcher.searchDbServer()==2)// oracle 库
        {
            sql = sql + " end_date > sysdate";
        }
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		items = "";
		try {
			rs = dao.search(sql);
			while(rs.next()){
				items += rs.getString("codesetid") + rs.getString("codeitemid") + ",";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return items;
	}
	
	

}
