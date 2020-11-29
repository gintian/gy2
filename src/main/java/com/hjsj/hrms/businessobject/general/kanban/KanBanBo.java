package com.hjsj.hrms.businessobject.general.kanban;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class KanBanBo {
	private UserView userView=null;
	private Connection conn = null;
	public KanBanBo(UserView userView,Connection conn){
		this.userView=userView;
		this.conn = conn;
	}
	public void addValue(ArrayList vlauelist,String person,String checkperson){
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("p05");
			IDGenerator idg = new IDGenerator(2, this.conn);
			String P0500 = idg.getId("P05.P0500");
			vo.setString("p0500", P0500);

			String personArr[] = person.split("::");
			if(personArr.length==3){
				vo.setString("nbase_0",personArr[0]);
				vo.setString("a0100_0",personArr[1]);
				vo.setString("a0101_0",personArr[2]);
				String orgvalue = getOrgValue(personArr[0],personArr[1]);
				personArr = orgvalue.split("::");
				if(personArr.length==3){
					vo.setString("b0110_0",personArr[0]);
					vo.setString("e0122_0",personArr[1]);
					vo.setString("e01a1_0",personArr[2]);
				}
			}

			String checkpersonArr[] = checkperson.split("::");
			if(checkpersonArr.length==3){
				vo.setString("nbase_1",checkpersonArr[0]);
				vo.setString("a0100_1",checkpersonArr[1]);
				vo.setString("a0101_1",checkpersonArr[2]);
				String orgvalue = getOrgValue(checkpersonArr[0],checkpersonArr[1]);
				checkpersonArr = orgvalue.split("::");
				if(personArr.length==3){
					vo.setString("b0110_1",checkpersonArr[0]);
					vo.setString("e0122_1",checkpersonArr[1]);
					vo.setString("e01a1_1",checkpersonArr[2]);
				}
			}

			for(int i=0;i<vlauelist.size();i++){
				FieldItem fielditem = (FieldItem) vlauelist.get(i);
				
				if("a0101_0".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("a0101_1".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}
				String itemid = fielditem.getItemid();
				String value = fielditem.getValue();

				if ("D".equals(fielditem.getItemtype())){
					 if("p0501".equalsIgnoreCase(fielditem.getItemid())
							 || "p0502".equalsIgnoreCase(fielditem.getItemid())){
						 String viewvalue = fielditem.getViewvalue();
						 vo.setDate(itemid, value+" "+viewvalue);
					 }else {
                         vo.setDate(itemid, value);
                     }
				} else if ("N".equals(fielditem.getItemtype())){
					value = PubFunc.round(value,fielditem.getDecimalwidth());
					vo.setString(itemid, value);
				} else {
                    vo.setString(itemid, value);
                }
			}
			vo.setString("p0513", "2");
			vo.setString("a0101", this.userView.getUserFullName());
			vo.setString("b0110", this.userView.getUserOrgId());
			vo.setString("e0122", this.userView.getUserDeptId());
			vo.setString("e01a1", this.userView.getUserPosId());
			vo.setString("a0100", this.userView.getA0100());
			vo.setString("nbase", this.userView.getDbname());
			dao.addValueObject(vo);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	synchronized public void updateValue(ArrayList vlauelist,String person,String checkperson,String p0500){
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("p05");
			vo.setString("p0500", p0500);
			vo = dao.findByPrimaryKey(vo);
			String personArr[] = person.split("::");
			if(personArr.length==3){
				vo.setString("nbase_0",personArr[0]);
				vo.setString("a0100_0",personArr[1]);
				vo.setString("a0101_0",personArr[2]);
				String orgvalue = getOrgValue(personArr[0],personArr[1]);
				personArr = orgvalue.split("::");
				if(personArr!=null&&personArr.length>0) {
                    vo.setString("b0110_0",personArr[0]);
                }
				if(personArr!=null&&personArr.length>1) {
                    vo.setString("e0122_0",personArr[1]);
                }
				if(personArr!=null&&personArr.length>2) {
                    vo.setString("e01a1_0",personArr[2]);
                }
			}

			String checkpersonArr[] = checkperson.split("::");
			if(checkpersonArr.length==3){
				vo.setString("nbase_1",checkpersonArr[0]);
				vo.setString("a0100_1",checkpersonArr[1]);
				vo.setString("a0101_1",checkpersonArr[2]);
				String orgvalue = getOrgValue(checkpersonArr[0],checkpersonArr[1]);
				personArr = orgvalue.split("::");
				if(personArr!=null&&personArr.length>0) {
                    vo.setString("b0110_1",personArr[0]);
                }
				if(personArr!=null&&personArr.length>1) {
                    vo.setString("e0122_1",personArr[1]);
                }
				if(personArr!=null&&personArr.length>2) {
                    vo.setString("e01a1_1",personArr[2]);
                }
			}
			for(int i=0;i<vlauelist.size();i++){
				FieldItem fielditem = (FieldItem) vlauelist.get(i);

				String itemid = fielditem.getItemid();
				String value = fielditem.getValue();
				if(value==null||value.trim().length()<1) {
                    continue;
                }
				if("a0101".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("b0110".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("e0122".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("e01a1".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("a0100".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("NBASE".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0500".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}

				if ("D".equalsIgnoreCase(fielditem.getItemtype())){
					 if("p0501".equalsIgnoreCase(fielditem.getItemid())
							 || "p0502".equalsIgnoreCase(fielditem.getItemid())){
						 String viewvalue = fielditem.getViewvalue();
						 vo.setDate(itemid, value+" "+viewvalue);
					 }else {
                         vo.setDate(itemid, value);
                     }
					
				} else if ("N".equalsIgnoreCase(fielditem.getItemtype())){
					value = PubFunc.round(value,fielditem.getDecimalwidth());
					vo.setString(itemid, value);
				} else {
                    vo.setString(itemid, value);
                }
			}
			dao.updateValueObject(vo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	synchronized public void deleteValue(String p0500arr){
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer delsql = new StringBuffer("delete from p05 where p0500 in('");
		String[] arr = p0500arr.split(",");
		for(int i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].trim().length()>0){
				delsql.append(arr[i]);
				delsql.append("','");
			}
		}
		delsql.append("')");
		try {
			dao.update(delsql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getOrgValue(String dbname,String A0100){
		String orgvalue="";
		if(dbname==null||dbname.trim().length()<1||A0100==null||A0100.trim().length()<1) {
            return ":::::";
        }
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select B0110,E0122,E01A1 from ");
		sqlstr.append(dbname);
		sqlstr.append("A01 where A0100='");
		sqlstr.append(A0100);
		sqlstr.append("'");
		RowSet rs;
		try {
			rs = dao.search(sqlstr.toString());
			if(rs.next()){
				orgvalue+=(rs.getString("B0110")!=null?rs.getString("B0110"):"")+"::";
				orgvalue+=(rs.getString("E0122")!=null?rs.getString("E0122"):"")+"::";
				orgvalue+=rs.getString("E01A1")!=null?rs.getString("E01A1"):"";
			}else{
				orgvalue=":::::";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return orgvalue;
	}
	synchronized public void replyValue(ArrayList vlauelist,String p0500){
		try {
			StringBuffer str = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("p05");
			vo.setString("p0500", p0500);
			vo = dao.findByPrimaryKey(vo);
			WeekUtils weekutils = new WeekUtils();
			String p0509 = vo.getString("p0509");
			p0509=p0509!=null?p0509:"";
			String lead = SystemConfig.getPropertyValue("workform");
			lead=lead!=null?lead:"";
			for(int i=0;i<vlauelist.size();i++){
				FieldItem fielditem = (FieldItem) vlauelist.get(i);
				if("p0509".equalsIgnoreCase(fielditem.getItemid())){
					if(p0509.trim().length()>5){
						str.append(p0509);
						str.append("<br>");
					}
					str.append("<span style=\"color:#3333cc\">");
					str.append(this.userView.getUserFullName());
					str.append(" ");
					str.append(weekutils.strDate());
					str.append(" ");
					str.append(weekutils.strHMS());
					str.append("</span><br>&nbsp;&nbsp;");
					str.append(fielditem.getValue());
				}
				if(lead.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1){
					String value = fielditem.getValue();
					if ("D".equalsIgnoreCase(fielditem.getItemtype())){
						vo.setDate(fielditem.getItemid(), fielditem.getValue());
					} else if ("N".equalsIgnoreCase(fielditem.getItemtype())){
						value = PubFunc.round(value,fielditem.getDecimalwidth());
						vo.setString(fielditem.getItemid(), value);
					} else {
                        vo.setString(fielditem.getItemid(), value);
                    }
				}
			}
			int p0504 = vo.getInt("p0504");
			vo.setInt("p0504", p0504+1);
			vo.setString("p0509", str.toString());
			dao.updateValueObject(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	synchronized public void fillValue(ArrayList vlauelist,String p0500,String subs){
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("p05");
			vo.setString("p0500", p0500);
			vo = dao.findByPrimaryKey(vo);
			
			boolean flag = false;
			
			WeekUtils weekutils = new WeekUtils();
			String p0509 = vo.getString("p0509");
			p0509=p0509!=null?p0509:"";
			StringBuffer str = new StringBuffer();
			int p0504 = vo.getInt("p0504");
			String p0507 = "0";
			for(int i=0;i<vlauelist.size();i++){
				FieldItem fielditem = (FieldItem) vlauelist.get(i);
				if("a0101".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("b0110".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("e0122".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("e01a1".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("a0100".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("NBASE".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0500".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0501".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0502".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0503".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0505".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0513".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}else if("p0504".equalsIgnoreCase(fielditem.getItemid())){
					continue;
				}
				if("p0507".equalsIgnoreCase(fielditem.getItemid())){
					p0507 = fielditem.getValue();
				}else if("p0509".equalsIgnoreCase(fielditem.getItemid())){
					if(fielditem.getValue()!=null&&fielditem.getValue().trim().length()>0){
						if(p0509.trim().length()>5){
							str.append(p0509);
							str.append("<br>");
						}
						str.append("<span style=\"color:#3333cc\">");
						str.append(this.userView.getUserFullName());
						str.append(" ");
						str.append(weekutils.strDate());
						str.append(" ");
						str.append(weekutils.strHMS());
						str.append("</span><br>&nbsp;&nbsp;");
						str.append(fielditem.getValue());
						flag = true;
					}
				}else{
					String value = fielditem.getValue();
					if ("D".equalsIgnoreCase(fielditem.getItemtype())){
						vo.setDate(fielditem.getItemid(), fielditem.getValue());
					} else if ("N".equalsIgnoreCase(fielditem.getItemtype())){
						value = PubFunc.round(value,fielditem.getDecimalwidth());
						vo.setString(fielditem.getItemid(), value);
					} else {
                        vo.setString(fielditem.getItemid(), value);
                    }
				}
			}
			if("1".equalsIgnoreCase(subs)){
				String timevalue = weekutils.strDate()+" "+weekutils.strHMS();
				vo.setDate("p0502", timevalue);
				p0507="100";
			}
			if(flag){
				vo.setInt("p0504", p0504+1);
				vo.setString("p0509", str.toString());
			}
			vo.setString("p0507", p0507);
			dao.updateValueObject(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	synchronized public void auditValue(ArrayList vlauelist,String p0500){
		try {
			StringBuffer str = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo = new RecordVo("p05");
			vo.setString("p0500", p0500);
			vo = dao.findByPrimaryKey(vo);
			WeekUtils weekutils = new WeekUtils();
			String p0509 = vo.getString("p0509");
			int p0504 = vo.getInt("p0504");
			boolean flag = false;
			p0509=p0509!=null?p0509:"";
			for(int i=0;i<vlauelist.size();i++){
				FieldItem fielditem = (FieldItem) vlauelist.get(i);
				if("p0509".equalsIgnoreCase(fielditem.getItemid())){
					if(fielditem.getValue()!=null&&fielditem.getValue().trim().length()>0){
						if(p0509.trim().length()>5){
							str.append(p0509);
							str.append("<br>");
						}
						str.append("<span style=\"color:#3333cc\">");
						str.append(this.userView.getUserFullName());
						str.append(" ");
						str.append(weekutils.strDate());
						str.append(" ");
						str.append(weekutils.strHMS());
						str.append("</span><br>&nbsp;&nbsp;");
						str.append(fielditem.getValue());
						flag = true;
						break;
					}
					
				}
			}
			if(flag){
				vo.setInt("p0504", p0504+1);
				vo.setString("p0509", str.toString());
			}
			vo.setString("p0513", "1");
			String a0100_0 = vo.getString("a0100_0");
			String a0100_1 = vo.getString("a0100_1");
			if(a0100_0.equalsIgnoreCase(a0100_1)){
				vo.setString("p0507", "100");
			}
			
			dao.updateValueObject(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public ArrayList fieldList(){
		ArrayList fieldlist = new ArrayList();
		FieldItem items = new FieldItem("A01","");
		items.setItemid("a0101");
		items.setItemdesc("发单人");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0100");
		items.setItemdesc("发单人人员编号");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("nbase");
		items.setItemdesc("发单人人员库");
		items.setItemtype("A");
		items.setCodesetid("@@");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0101_0");
		items.setItemdesc("接单人");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("nbase_0");
		items.setItemdesc("接单人人员库");
		items.setItemtype("A");
		items.setCodesetid("@@");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0100_0");
		items.setItemdesc("接单人人员编号");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0101_1");
		items.setItemdesc("任务审核人");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("nbase_1");
		items.setItemdesc("任务审核人人员库");
		items.setItemtype("A");
		items.setCodesetid("@@");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0100_1");
		items.setItemdesc("任务审核人人员编号");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		return fieldlist;
	}
	public ArrayList itemList(){
		ArrayList fieldlist = new ArrayList();
		ArrayList setlist = fieldList();
		FieldItem items = new FieldItem("A01","");
		items.setItemid("a0101");
		items.setItemdesc("发单人");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0101_0");
		items.setItemdesc("接单人");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		items = new FieldItem("A01","");
		items.setItemid("a0101_1");
		items.setItemdesc("任务审核人");
		items.setItemtype("A");
		items.setCodesetid("0");
		fieldlist.add(items);
		
		ArrayList list = DataDictionary.getFieldList("p05",Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			if("p0501".equalsIgnoreCase(fielditem.getItemid())
					|| "p0502".equalsIgnoreCase(fielditem.getItemid())){
				fielditem.setItemlength(20);
			}
			if(!fielditem.isVisible()) {
                continue;
            }
			if("N".equalsIgnoreCase(fielditem.getItemtype())&&fielditem.getDecimalwidth()>2) {
                fielditem.setDecimalwidth(2);
            }
			fieldlist.add(fielditem);
		}
		return fieldlist;
	}
	public ArrayList addValue(ArrayList list){
		 ArrayList fieldlist = new ArrayList();
		 FieldItem items = new FieldItem("A01","");
		 items = new FieldItem("A01","");
		 items.setItemid("a0101_0");
		 items.setItemdesc("接单人");
		 items.setItemtype("A");
		 items.setCodesetid("0");
		 items.setValue("");
		 items.setPriv_status(1);
		 fieldlist.add(items);
		 items = new FieldItem("A01","");
		 items.setItemid("a0101_1");
		 items.setItemdesc("任务审核人");
		 items.setItemtype("A");
		 items.setCodesetid("0");
		 items.setValue("");
		 items.setPriv_status(1);
		 fieldlist.add(items);
		 String lead = SystemConfig.getPropertyValue("workform");
		 lead=lead!=null?lead:"";

		 for(int i=0;i<list.size();i++){
			 FieldItem fielditem = (FieldItem)list.get(i);
			 if(!fielditem.isVisible()){
				 continue;
			 }
			 if("a0101".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("b0110".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("e0122".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("e01a1".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("a0100".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("NBASE".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0500".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0502".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0507".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0513".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0509".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0504".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }else if("p0521".equalsIgnoreCase(fielditem.getItemid())){
				 continue;
			 }
			 if(lead.toLowerCase().indexOf(fielditem.getItemid().toLowerCase())!=-1){
					if(this.userView.isDeptLeader()||this.userView.isOrgLeader()||isHaveRole("00000005")){
						fielditem.setPriv_status(1);
					}else{
						continue;
					}
			}
			 if(fielditem.getDecimalwidth()>2) {
                 fielditem.setDecimalwidth(2);
             }
			 if("D".equalsIgnoreCase(fielditem.getItemtype())){
				 WeekUtils weekutils = new WeekUtils();
				 fielditem.setValue(weekutils.strDate());
				 if("p0501".equalsIgnoreCase(fielditem.getItemid())|| "p0502".equalsIgnoreCase(fielditem.getItemid())){
					 fielditem.setViewvalue(weekutils.strHMS());
				 }else{
					 fielditem.setValue("");
					 fielditem.setViewvalue("");
				 }
			 }else{
				 fielditem.setValue("");
				 fielditem.setViewvalue("");
			 }

			 fielditem.setPriv_status(1);
			 fieldlist.add(fielditem);
		 }
		 
		 return fieldlist;
	 }
	 /***
     * 分析登录用户是否拥有此角色
     * @param role_id
     * @return
     */
    public boolean isHaveRole(String role_id){
//      if(userView.isSuper_admin())
//    	return true;
      /**登录用户拥有的角色*/
      boolean flag=false;
      ArrayList rolelist=userView.getRolelist();
      for(int i=0;i<rolelist.size();i++)
      {
    	  if(role_id.equals(rolelist.get(i)))
    	  {
    		  flag=true;
    		  break;
    	  }
      }
      return flag;
    }
}
