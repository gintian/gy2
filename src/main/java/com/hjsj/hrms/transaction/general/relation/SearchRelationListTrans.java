package com.hjsj.hrms.transaction.general.relation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchRelationListTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		//国网月度考勤表中添加的参数 用于控制关闭按钮
		String isshowbutton = "";
		if(null != this.getFormHM().get("isshowbutton")){
			isshowbutton = this.getFormHM().get("isshowbutton").toString();
		}
		this.getFormHM().put("isshowbutton", isshowbutton);
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		//创建表结构
		DbWizard dbWizard=new DbWizard(this.getFrameconn());
		DBMetaModel dbmodel=null;
		
		if(!dbWizard.isExistTable("t_wf_relation", false)){
			Table table=new Table("t_wf_relation");
			table.setCreatekey(false);
			Field temp20=new Field("Relation_id","审批关系id");
			temp20.setDatatype(DataType.INT);
			temp20.setNullable(false);
			temp20.setKeyable(true);
			temp20.setVisible(false);	
			table.addField(temp20);
			Field temp21=new Field("Cname","关系名称");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(100);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("create_user","创建用户");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(50);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("Actor_type","参与者类型");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(1);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("create_time","创建日期");
			 temp21.setDatatype(DataType.DATE);
			 temp21.setVisible(false);
			 temp21.setNullable(true);
			 temp21.setAlign("right");			
			table.addField(temp21);
			 temp21=new Field("validflag","有效标识");
			 temp21.setDatatype(DataType.INT);
				temp21.setNullable(true);
				temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("Seq","排序号");
			 temp21.setDatatype(DataType.INT);
				temp21.setNullable(true);
				temp21.setVisible(false);			
			table.addField(temp21);
			
			dbWizard.createTable(table);
			dbWizard.addPrimaryKey(table);
			if(dbmodel==null)
				dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel(table.getName());
		}
		if(!dbWizard.isExistTable("t_wf_mainbody", false)){
			Table table=new Table("t_wf_mainbody");
			table.setCreatekey(false);
			Field temp20=new Field("Relation_id","审批关系id");
			temp20.setDatatype(DataType.INT);
			temp20.setNullable(false);
			temp20.setKeyable(true);
			temp20.setVisible(false);	
			table.addField(temp20);
			temp20=new Field("object_id","审批对象id");
			temp20.setDatatype(DataType.STRING);
			temp20.setLength(30);
			temp20.setNullable(false);
			temp20.setKeyable(true);
			temp20.setVisible(false);	
			table.addField(temp20);
			temp20=new Field("mainbody_id","审批主体id");
			temp20.setDatatype(DataType.STRING);
			temp20.setLength(30);
			temp20.setNullable(false);
			temp20.setKeyable(true);
			temp20.setVisible(false);	
			table.addField(temp20);
			Field  temp21=new Field("Actor_type","参与者类型");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(1);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("b0110","对象的单位");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(30);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			temp21=new Field("e0122","对象的部门");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(30);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);

			temp21=new Field("e01a1","对象的职位");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(30);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			temp21=new Field("a0101","对象名");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(30);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("sp_grade","审批层级");
			 temp21.setDatatype(DataType.INT);
				temp21.setNullable(true);
				temp21.setVisible(false);			
			table.addField(temp21);
			 temp21=new Field("groupid","用户组id");
			 temp21.setDatatype(DataType.INT);
				temp21.setNullable(true);
				temp21.setVisible(false);			
			table.addField(temp21);
			
			
			 temp21=new Field("create_user","创建用户");
			temp21.setDatatype(DataType.STRING);
			temp21.setLength(50);
			temp21.setNullable(true);
			temp21.setVisible(false);			
			table.addField(temp21);
			 
			 temp21=new Field("create_time","创建日期");
			 temp21.setDatatype(DataType.DATE);
			 temp21.setVisible(false);
			 temp21.setNullable(true);
			 temp21.setAlign("right");			
			table.addField(temp21);
			 temp21=new Field("mod_user","修改用户");
				temp21.setDatatype(DataType.STRING);
				temp21.setLength(50);
				temp21.setNullable(true);
				temp21.setVisible(false);			
				table.addField(temp21);
				 
				 temp21=new Field("mod_time","修改日期");
				 temp21.setDatatype(DataType.DATE);
				 temp21.setVisible(false);
				 temp21.setNullable(true);
				 temp21.setAlign("right");			
				table.addField(temp21);
				 
			
			dbWizard.createTable(table);
			dbWizard.addPrimaryKey(table);
			if(dbmodel==null)
				dbmodel=new DBMetaModel(this.getFrameconn());
			dbmodel.reloadTableModel(table.getName());
		}
		try {
			if(hm.get("move")!=null){
				if("up".equals(hm.get("move"))){
					
				}else if("down".equals(hm.get("move"))){
					
				}
				hm.remove("move");
			}
				
			ArrayList setlist = this.searchCheckBodyObjectList();
			this.getFormHM().put("setlist", setlist);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		

	}

	public ArrayList searchCheckBodyObjectList()
			throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append(" select * from t_wf_relation ");
		buf.append(" order by  seq ");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(buf.toString());
			String relying="";
			String actor_type="";
			ResultSet ros=null;
			while (this.frowset.next()) {
				RecordVo vo = new RecordVo("t_wf_relation");
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("relation_id", this.frowset.getString("relation_id"));
				bean.set("cname", this.frowset.getString("cname"));
				bean.set("create_user", this.frowset.getString("create_user"));
				bean.set("actor_type", this.frowset.getString("actor_type"));
				bean.set("validflag", this.frowset.getString("validflag"));
				bean.set("seq", this.frowset.getString("seq"));
				bean.set("default_line", this.frowset.getString("default_line"));
				relying=this.frowset.getString("relying");
				actor_type=this.frowset.getString("actor_type");
				if(relying==null || relying.trim().length()<=0 || "0".equals(relying)){
					relying="";
				}else if(relying!=null && relying.trim().length()>0 && "-1".equals(relying)){
					relying="";
					String sql="select * from t_wf_relation where actor_type='"+actor_type+"' and default_line='1'";
					ros=dao.search(sql);
					if(ros.next()){
						relying=ros.getString("cname");
					}else{
						vo.setString("relation_id", this.frowset.getString("relation_id"));//删除时 所依赖关系被删除 那么清空自己依赖关系字段
						vo=dao.findByPrimaryKey(vo);
						vo.setInt("relying", 0);
						dao.updateValueObject(vo);
					}
				}else if(relying!=null && relying.trim().length()>0){
					String sql="select * from t_wf_relation where relation_id='"+relying+"'";
					ros=dao.search(sql);
					if(ros.next()){
						String ssql="select * from t_wf_relation where relation_id='"+relying+"' and default_line='1'";
						relying=ros.getString("cname");
						ros=dao.search(ssql);
						while(ros.next()){	
							vo.setString("relation_id", this.frowset.getString("relation_id"));//删除时 所依赖关系被删除 那么清空自己依赖关系字段
							vo=dao.findByPrimaryKey(vo);
							vo.setInt("relying", -1);
							dao.updateValueObject(vo);
						}
					}else{
						relying="";
						vo.setString("relation_id", this.frowset.getString("relation_id"));//删除时 所依赖关系被删除 那么清空自己依赖关系字段
						vo=dao.findByPrimaryKey(vo);
						vo.setInt("relying", 0);
						dao.updateValueObject(vo);
					}
				}
				bean.set("relying", relying);
				list.add(bean);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}

	
}
