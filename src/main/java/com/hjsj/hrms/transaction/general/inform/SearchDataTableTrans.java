/**
 * 
 */
package com.hjsj.hrms.transaction.general.inform;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hjsj.hrms.businessobject.general.inform.CommonSql;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.org.orgdata.OrgDataBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.DyParameter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *<p>Title:SearchDataTableTrans</p> 
 *<p>Description:查询数据表交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-15:下午02:03:54</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchDataTableTrans extends IBusiness {
	
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @return
	 */
	private ArrayList getFieldList(String setname){
		ArrayList fieldlist=new ArrayList();
		
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		if(setname!=null&& "t_vorg_staff".equalsIgnoreCase(setname)){
			return gzbo.torgItemList();
		}
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		//ArrayList list=DataDictionary.getFieldList(setname, Constant.USED_FIELD_SET);
		ArrayList list = gzbo.itemList(fieldset);
		
		Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.getFrameconn());
		String units=othparam.getValue(Sys_Oth_Parameter.UNITS);
		String place=othparam.getValue(Sys_Oth_Parameter.PLACE);
		String reserveitem = "";
		
		if(!fieldset.isMainset()){
			Field tempfield=new Field("A0100","A0100");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setLength(8);
			tempfield.setReadonly(true);			
			tempfield.setVisible(false);
			fieldlist.add(tempfield);

		}else{
		    FieldItem item3=new FieldItem();
			item3.setFieldsetid(setname);
			item3.setItemid("oper");
			item3.setItemdesc(ResourceFactory.getProperty("column.operation"));
			item3.setItemtype("A");
			item3.setCodesetid("0");
			item3.setAlign("center");
			item3.setReadonly(true);
			fieldlist.add(item3.cloneField());
		    
			Field tempfield=new Field("A0100","A0100");
			tempfield.setDatatype(DataType.STRING);
			tempfield.setLength(8);
			tempfield.setVisible(false);
			fieldlist.add(tempfield);

			/**有排序功能时，让其对A0000字段的值可维护,也即手动排序*/
			tempfield=new Field("A0000",ResourceFactory.getProperty("kjg.gather.xuhao"));
			tempfield.setDatatype(DataType.INT);
			tempfield.setVisible(false);
			fieldlist.add(tempfield);
		}
		int I9999 = 1;
		String i9999str = "";
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			String itemid=field.getName();
			
			if(fieldset.isMainset()){
				if("B0110".equalsIgnoreCase(itemid)){
					if(units!=null&& "1".equals(units)){
						field.setLabel(field.getLabel()+"<font color='red'>*</font>");
					}
				}else if("E01A1".equalsIgnoreCase(itemid)){
					if(place!=null&& "1".equals(place)){
						field.setLabel(field.getLabel()+"<font color='red'>*</font>");
						
					}
				}
			}else{
				if("B0110".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
					if(units!=null&& "1".equals(units)){
						field.setLabel(field.getLabel()+"<font color='red'>*</font>");
					}
				}else if("E0122".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
				}else if("E01A1".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
					if(place!=null&& "1".equals(place)){
						field.setLabel(field.getLabel()+"<font color='red'>*</font>");
					}
				}else if("A0101".equalsIgnoreCase(itemid)){
					field.setReadonly(true);
				}
			}
			if("0".equals(this.userView.analyseFieldPriv(itemid,0))&& "0".equals(this.userView.analyseFieldPriv(itemid,1)))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(itemid,0))|| "1".equals(this.userView.analyseFieldPriv(itemid,1)))
				field.setReadonly(true);
			
			if(field.getLabel().indexOf("<font color='red'>*</font>")!=-1){
				if(!field.isReadonly()){
					reserveitem+=field.getName()+",."+field.getLabel().replace("<font color='red'>*</font>", "")+"`";
				}
			}

			field.setSortable(true);
			fieldlist.add(field);
			if(!fieldset.isMainset()){
				if("A0101".equalsIgnoreCase(itemid)&&I9999>0){
					Field tempfield=new Field("I9999","序号");
					tempfield.setDatatype(DataType.INT);
					tempfield.setReadonly(true);
					tempfield.setVisible(true);
					fieldlist.add(tempfield);
					I9999=0;
					i9999str = "i9999";
				}
			}
		}//i loop end.
		if(!fieldset.isMainset()){
			if(i9999str.trim().length()<1){
				Field tempfield=new Field("I9999","序号");
				tempfield.setDatatype(DataType.INT);
				tempfield.setReadonly(true);
				tempfield.setVisible(true);
				fieldlist.add(tempfield);
			}
		}
		this.getFormHM().put("reserveitem", reserveitem);
		return fieldlist;
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @return
	 */
	private String getCFields(ArrayList list,String maintable,String itemtable,String fieldsetid)
	{
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<list.size();i++)
		{
			Field field=(Field)list.get(i);
			if("A0100".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".A0100,");
			}else if("B0110".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".B0110,");
			}else if("E0122".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".E0122,");
			}else if("E01A1".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".E01A1,");
			}else if("A0101".equalsIgnoreCase(field.getName())){
				buf.append(maintable);
				buf.append(".A0101,");
			}else if("downole".equalsIgnoreCase(field.getName()))
				buf.append("'' downole,");
			else if("upole".equalsIgnoreCase(field.getName()))
				buf.append("'' upole,");
			else{
				if("state".equalsIgnoreCase(field.getName())){
					if("A00".equalsIgnoreCase(fieldsetid)){
						buf.append(" CASE WHEN ");
						buf.append(Sql_switcher.length(itemtable+"."+field.getName()));
						buf.append("=1 THEN '0'"+Sql_switcher.getCatOp()+itemtable+"."+field.getName());
						buf.append(" ELSE "+itemtable+"."+field.getName()+" END");
						buf.append(" AS "+field.getName()+",");
					}else{
						buf.append(itemtable+"."+field.getName()+",");
					}
				}else if("flag".equalsIgnoreCase(field.getName())){
					if("A00".equalsIgnoreCase(fieldsetid)){
						buf.append("(select SORTNAME from mediasort where FLAG="+itemtable+".flag) as flag,");
					}else{
						buf.append(itemtable+"."+field.getName()+",");
					}
				}else{
					if(!"oper".equalsIgnoreCase(field.getName()))
						buf.append(itemtable+"."+field.getName()+",");
				}
			}
		}//for i loop end.
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
	public void execute() throws GeneralException {
		try
		{
			String setname=(String)this.getFormHM().get("setname");
			setname=setname!=null&&setname.length()>2?setname:"A01";
			
			String dbname=(String)this.getFormHM().get("dbname");
			String viewsearch=(String)this.getFormHM().get("viewsearch");
			viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";
			
			EmpMaintenanBo embo = new EmpMaintenanBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			/**对人员信息集*/
			String tablename=dbname+setname;
			if("t_vorg_staff".equalsIgnoreCase(setname))
				tablename = setname;
			String a_code=(String)this.getFormHM().get("a_code");
			a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
			a_code= "all".equalsIgnoreCase(a_code)||a_code==null?"":a_code;
			
			String inforflag=(String)this.getFormHM().get("inforflag");
			inforflag=inforflag!=null&&inforflag.trim().length()>0?inforflag:"1";
			
			/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
			String t_code=a_code.toUpperCase();

			String orgCode = "";
            //inforflag 1:员工管理 2：外部培训
			if("2".equals(inforflag)){
				if(!this.userView.isSuper_admin()){
					
//					if("".equals(t_code)&&userView.getStatus()==4){
//						t_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
//					}
					
					/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
					//else if("".equals(t_code)&&userView.getStatus()==0){
//						String codeall = userView.getUnit_id();
//						codeall = PubFunc.getTopOrgDept(codeall);
						TrainCourseBo bo = new TrainCourseBo(this.userView);
						String codeall = bo.getUnitIdByBusi();
						//String unitarr[] = codeall.split("`");
						//if(unitarr!=null&&unitarr.length>0)
							orgCode=codeall;//unitarr[0];
						// if("".equals(t_code))
						//	t_code=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
					//}
					if("".equals(orgCode))
						throw new GeneralException(ResourceFactory.getProperty("train.job.authorization1"));
	//				
	//				if(t_code.startsWith("UN")||t_code.startsWith("UM")||t_code.startsWith("@K"))
	//					a_code=t_code;
				}
			}else{
				if("".equals(t_code)&&!userView.isSuper_admin()){
					orgCode=this.getUserView().getManagePrivCode()+this.getUserView().getManagePrivCodeValue();
				}
			}
			
			String codeitemid=(String)this.getFormHM().get("codeitemid");
			codeitemid=codeitemid!=null&&codeitemid.trim().length()>0?codeitemid:"";
			
			String viewdata=(String)this.getFormHM().get("viewdata");
			viewdata=viewdata!=null&&viewdata.trim().length()>0?viewdata:"0";
			
			StringBuffer buf1=new StringBuffer();
			
			String[] codearr =t_code.split("`");
			boolean vorg = false;
			for (int i = 0; i < codearr.length; i++) {
				vorg = false;
				if(codearr!=null&&codearr[i].length()!=3)
					vorg = isVorg(dao,codearr[i]);
				buf1.append("("+CommonSql.whereCodeStr(this.userView,codearr[i],dbname,vorg)+") or ");
			}
			StringBuffer buf=new StringBuffer();
			if(buf1.toString().trim().endsWith("or"))
				buf1.setLength(buf1.length()-4);
			buf.append("("+buf1+")");

			if(!"".equals(orgCode)){
				buf1.setLength(0);
				codearr = orgCode.split("`");
				vorg = false;
				for (int i = 0; i < codearr.length; i++) {
					vorg = false;
					if(codearr!=null&&codearr[i].length()!=3)
						vorg = isVorg(dao,codearr[i]);
					buf1.append("("+CommonSql.whereCodeStr(this.userView,codearr[i],dbname,vorg)+") or ");
				}

				if(buf1.toString().trim().endsWith("or"))
					buf1.setLength(buf1.length()-4);
				if(buf.length() > 0)
					buf.append(" AND ");
				buf.append("("+buf1+")");
			}

			String temptable = this.userView.getUserName()+dbname+"result";
			int msg = this.userView.getStatus();//判读是否是自助用户 4：自助用户  1：业务用户
			
			if(4==msg)
			    temptable = "t_sys_result";

			ArrayList list=getFieldList(setname);
			if("A00".equalsIgnoreCase(setname)){
				OrgDataBo orgbo = new OrgDataBo(this.frameconn,this.userView);
				list.addAll(orgbo.a00ItemList("1"));
			}
			
			
			StringBuffer strsql=new StringBuffer();
			FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
			String prive = this.userView.analyseTablePriv(setname);//子集权限值
			 DbNameBo dbbo=new DbNameBo(this.getFrameconn());
//			 if(a_code.trim().length()>2){
//				 if(a_code.indexOf("UN")!=-1||a_code.indexOf("UM")!=-1||a_code.indexOf("@K")!=-1){
//					 code = a_code.substring(2);
//				 }else{
//					 if(!this.userView.isSuper_admin()){
//						 code = this.userView.getManagePrivCodeValue();
//					 }
//				 }
//			 }else{
//				 if(!this.userView.isSuper_admin()){
//					 code = this.userView.getManagePrivCodeValue();
//				 }
//			 }
			 String strWhere="";
			 for (int i = 0; i < codearr.length; i++) {
				 if(codearr!=null&&codearr[i].trim().length()>3)
					 strWhere+=dbbo.getQueryFromPartLike(this.userView, dbname,codearr[i].substring(2),buf.toString())+" or";
			 }
			 if(strWhere.trim().endsWith("or"))
				 strWhere=strWhere.substring(0, strWhere.length()-3);
			if(codearr!=null&&codearr.length==3)
				strWhere = "";
			if(fieldset!=null&&fieldset.isMainset())
			{
				strsql.append("select ");
				for(int i=0;i<list.size();i++)
				{
					Field field=(Field)list.get(i);	
					if(!"oper".equalsIgnoreCase(field.getName())){
						strsql.append(tablename+"."+field.getName()+",");
					}
				}
				strsql.append("'' oper from ");				
				strsql.append(tablename);
				if(viewsearch!=null&& "1".equals(viewsearch)){
					strsql.append(" right join ");
					strsql.append(temptable);
					strsql.append(" on ");
					strsql.append(temptable);
					if(4==msg)
					    strsql.append(".obj_id=");
					else
					    strsql.append(".A0100=");
					strsql.append(tablename);
					strsql.append(".A0100 ");
				}
				if(vorg){
					strsql.append(" right join ");
					strsql.append("t_vorg_staff");
					strsql.append(" on ");
					strsql.append("t_vorg_staff");
					strsql.append(".A0100=");
					strsql.append(tablename);
					strsql.append(".A0100 ");
				}
				
				if(strWhere!=null&&strWhere.trim().length()>5){
					if(buf.length()> 1){
						strsql.append(" where (" );
						strsql.append(buf.toString());
						strsql.append(" or ");
						strsql.append(strWhere);		
						strsql.append(")");
						if(viewsearch!=null&& "1".equals(viewsearch) && 4==msg)
						    strsql.append("and upper("+ temptable +".username)='"+this.userView.getUserName().toUpperCase()+"'");
					}
				}else{
					if(buf.length()>1){
						strsql.append(" where ");
						strsql.append(buf.toString());
						if(viewsearch!=null&& "1".equals(viewsearch) && 4==msg)
                            strsql.append("and upper("+ temptable +".username)='"+this.userView.getUserName().toUpperCase()+"'");
					}
				}
				
				String orgWhr = "";
				if(a_code!=null && !"".equals(a_code.trim()) && 2<a_code.length() && !"all".equalsIgnoreCase(a_code)){
				    String codeKind = a_code.trim().substring(0,2);
				    String codeValue = a_code.trim().substring(2);
				    if("UN".equalsIgnoreCase(codeKind))
				        orgWhr = "B0110 LIKE '" + codeValue + "%'";
				    else if("UM".equalsIgnoreCase(codeKind))
				        orgWhr = "E0122 LIKE '" + codeValue + "%'";
				    else if("@K".equalsIgnoreCase(codeKind))
				        orgWhr = "E01A1 LIKE '" + codeValue + "%'";
				}
				
				if(!"".equals(orgWhr)){
				    if(strsql.toString().toUpperCase().indexOf("WHERE")==-1)
				        strsql.append(" WHERE ");
				    
				    strsql.append(" AND ");
				    strsql.append(tablename+"."+orgWhr);
				} 
				
				strsql.append(" order by "+tablename+".A0000");
			}else//子集，关联主集姓名字段
			{
				prive="0";
				String maintable=dbname+"A01";
				String childtable=dbname+setname;
				if("t_vorg_staff".equalsIgnoreCase(setname)){
					childtable = setname;
					FieldItem fielditem = DataDictionary.getFieldItem("A0101");
					fielditem.setVisible(true);
					list.add(4,fielditem.cloneField());
				}
				String fields=getCFields(list, maintable,"a",setname);				
				
				strsql.append("select ");
				strsql.append(fields);
				strsql.append(" from ");
				strsql.append(childtable);
				strsql.append(" a right join ");
				strsql.append(maintable);
				strsql.append(" on ");
				strsql.append(maintable);
				strsql.append(".A0100=");
				strsql.append(" a");
				strsql.append(".A0100 ");
				if(viewsearch!=null&& "1".equals(viewsearch)){
					strsql.append(" right join ");
					strsql.append(temptable);
					strsql.append(" on ");
					strsql.append(temptable);
					if(4==msg)
                        strsql.append(".obj_id=");
                    else
                        strsql.append(".A0100=");
					strsql.append(maintable);
					strsql.append(".A0100 ");
				}
				if(vorg){
					strsql.append(" right join ");
					strsql.append("t_vorg_staff");
					strsql.append(" on ");
					strsql.append("t_vorg_staff");
					strsql.append(".A0100=");
					strsql.append("a.A0100 ");
				}
				if(strWhere!=null&&strWhere.trim().length()>5){
					if(buf.length()>1){
						strsql.append(" where ");
						strsql.append("(");
						strsql.append(buf.toString());
						strsql.append(" or ");
						strsql.append(strWhere);
						strsql.append(")");
					}
				}else{
					if(buf.length()>1){
						strsql.append(" where ");
						strsql.append(buf.toString());
					}
				}
				if(viewdata!=null&&viewdata.trim().length()>0&& "1".equals(viewdata)){
					if(strsql.indexOf("where")!=-1)
						strsql.append(" and ");
					else
						strsql.append(" where ");
					strsql.append(" (a.I9999=(select max(I9999) from ");
					strsql.append(tablename);
					strsql.append(" where ");
					strsql.append("A0100=a.A0100 ) or a.I9999 is null or a.I9999='')");
				}
				strsql.append(" order by "+maintable+".A0000,a.I9999");
			}
			 
			/**主子集排序*/
			String a0100 = (String)this.getFormHM().get("a0100");
			String sort_record_scope = (String)this.getFormHM().get("sort_record_scope");
			String sort_fields = (String)this.getFormHM().get("sort_str");
			this.getFormHM().put("sort_str","");
			this.getFormHM().put("sort_record_scope","");
			this.getFormHM().put("a0100","");
			if(sort_record_scope==null || "".equals(sort_record_scope)){
				// 主集排序
				String orderby = embo.getoMianOrderbyStr(sort_fields);
				if(orderby!=null && orderby.length()>0) 
					embo.sortMainTable(this.userView,orderby,dbname);
					//embo.sortMainTable(orderby,dbname);
			}else if("all".equalsIgnoreCase(sort_record_scope)){
				// 子集所有记录排序
				String orderby = embo.getoMianOrderbyStr(sort_fields);
				if(orderby!=null && orderby.length()>0) 
					embo.sortSubsetTable(orderby, a0100, dbname,this.getUserView().getUserName());			
			}else if("selected".equalsIgnoreCase(sort_record_scope)){
				// 子集选中的记录排序
				String orderby = embo.getoMianOrderbyStr(sort_fields);
				if(orderby!=null && orderby.length()>0) 
					embo.sortSubsetTable(orderby, a0100, dbname);			
			}
			/**照片大小*/
			LazyDynaBean lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_w",this.frameconn);
			String photo_w=(String)lazyDynaBean.get("photo_w");
			photo_w=photo_w!=null&&photo_w.trim().length()>0?photo_w:"155";			 
			lazyDynaBean= (LazyDynaBean)DyParameter.getParameter(Sys_Oth_Parameter.PHOTO,"photo_h",this.frameconn);
			String photo_h=(String)lazyDynaBean.get("photo_h");
			photo_h=photo_h!=null&&photo_h.trim().length()>0?photo_h:"110";
			this.getFormHM().put("photo_w",photo_w);
			this.getFormHM().put("photo_h",photo_h);
			
			String keys = "";
			if("A01".equalsIgnoreCase(setname)){
				keys = dbname+"A01.A0100";
			}else{
				keys = dbname+"A01.A0100";
			}
			keys="";// 0018016 CS中调整了兼职人员的顺序，BS表格录入中兼职人员的显示顺序不	排序因为key是a0100而orderby为a0000所以导致排序不正确	
			this.getFormHM().put("sql",strsql.toString());
			this.getFormHM().put("tablename",tablename);
			this.getFormHM().put("keys",keys);
			this.getFormHM().put("dbname",dbname);
			this.getFormHM().put("viewsearch",viewsearch);
			this.getFormHM().put("fieldlist",list);
			this.getFormHM().put("searchlist",searchTable(dao,"1"));
			this.getFormHM().put("prive",prive);
			this.getFormHM().put("inforflag",inforflag);
			String visible="true";
			String pri = "2";
			if(!this.userView.isSuper_admin()){
				pri = this.userView.analyseTablePriv(setname);
			}
			if("2".equals(inforflag)&& "a01".equalsIgnoreCase(setname)){
				visible="false";
			}
			if(!"2".equals(pri)){
				visible="false";
			}
			this.getFormHM().put("viewbutton",visible);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private ArrayList searchTable(ContentDAO dao,String type){
		ArrayList searchlist = new ArrayList();
		
		String sqlstr = "select id,name from LExpr where Type="+type;
		try {
			this.frowset=dao.search(sqlstr);
			int n=1;
			while(this.frowset.next()){
				if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
				CommonData job=new CommonData();
				job.setDataName(this.frowset.getString("id"));
				job.setDataValue(this.frowset.getString("id")+"."+this.frowset.getString("name"));
				searchlist.add(job);
				n++;
				if(n>7)
					break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchlist;
	}
	private boolean isVorg(ContentDAO dao,String a_code){
		boolean flag = false;
		String codesetid = "";
		String codeitemid = "";
		if(a_code==null||a_code.length()<3)
			return false;
		codesetid = a_code.substring(0,2);
		codeitemid = a_code.substring(2);
		
		
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select codesetid from vorganization where codeitemid='");
		sqlstr.append(codeitemid);
		sqlstr.append("' and codesetid='");
		sqlstr.append(codesetid);
		sqlstr.append("'");
		try {
			this.frowset=dao.search(sqlstr.toString());
			if(this.frowset.next()){
				flag = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
