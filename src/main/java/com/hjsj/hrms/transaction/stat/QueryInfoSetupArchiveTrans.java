package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.stat.InfoSetupBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * 归档显示信息集设置
 * @author xujian
 *Mar 22, 2010
 */
public class QueryInfoSetupArchiveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String inforkind = (String) hm.get("infokind");
		String type = (String)hm.get("type");
		type=type!=null?type:"";
		String id = (String)hm.get("id");
		id=id!=null?id:"";
		try{

			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql="";
			ArrayList unitdatalist = new ArrayList();//按月年变化单位子集
			String unit="",seasonal="",archive="",auto="0",unit_level="1",ctrl="0",dept_level="1";
			ArrayList seansonaldatalist = new ArrayList();//变化周期
			ArrayList volist = new ArrayList();//归档指标
			ArrayList unittargetlist = new ArrayList();

			if("1".equals(type)){
				ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
				//ArrayList unitdatalist = new ArrayList();//按月年变化单位子集
				unitdatalist.add(new CommonData("",""));
				for(int i=0;i<fieldsetlist.size();i++){
					FieldSet fieldset = (FieldSet)fieldsetlist.get(i);
					if("1".equalsIgnoreCase(fieldset.getChangeflag())||"2".equalsIgnoreCase(fieldset.getChangeflag())){
						CommonData cd = new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
						unitdatalist.add(cd);
					}
				}
				
				//String sql = "select Archive_set,Archive_type,archive from sname where id='"+id+"'";
				sql = "select Archive_set,Archive_type,archive from sname where id='"+id+"'";
				//ContentDAO dao = new ContentDAO(this.frameconn);
				this.frecset = dao.search(sql);
				//String unit="",seasonal="",archive="",auto="0",unit_level="1",ctrl="0",dept_level="1";
				if(this.frecset.next()){
					unit = this.frecset.getString("Archive_set");
					unit = unit!=null?unit:"";
					seasonal= String.valueOf(this.frecset.getInt("Archive_type"));
					seasonal = seasonal!=null&&!"null".equals(seasonal)?seasonal:"";
					archive=this.frecset.getString("archive");
					ArchiveXml xml = new ArchiveXml(this.frameconn,id,archive);
					auto=xml.getValue("auto");
					unit_level=xml.getValue("unit_level");
					dept_level=xml.getValue("dept_level");
					ctrl = xml.getValue("dept_ctrl");
				}
				//ArrayList seansonaldatalist = new ArrayList();//变化周期
				//seansonaldatalist.add(new CommonData("",""));
				if(!"".equalsIgnoreCase(unit)){
					FieldSet fs = DataDictionary.getFieldSetVo(unit);
					if(fs!=null){
						if("1".equalsIgnoreCase(fs.getChangeflag())){
							seansonaldatalist.add(new CommonData("1",ResourceFactory.getProperty("stat.info.setup.archive_type.month")));
						}else if("2".equalsIgnoreCase(fs.getChangeflag())){
							seansonaldatalist.add(new CommonData("2",ResourceFactory.getProperty("stat.info.setup.archive_type.season")));
							seansonaldatalist.add(new CommonData("3",ResourceFactory.getProperty("stat.info.setup.archive_type.half")));
							seansonaldatalist.add(new CommonData("4",ResourceFactory.getProperty("stat.info.setup.archive_type.year")));
						}
					}
				}
				sql = "select norder,Legend,Archive_field from SLegend where id='"+id+"' order by norder";
				this.frecset = dao.search(sql);
				//ArrayList volist = new ArrayList();//归档指标
				while(this.frecset.next()){
					FieldItemView fielditem = new FieldItemView();
					fielditem.setFieldsetid(id);
					fielditem.setItemdesc(this.frecset.getString("Legend"));
					fielditem.setItemid(String.valueOf(this.frecset.getInt("norder")));
					fielditem.setValue(this.frecset.getString("Archive_field"));
					volist.add(fielditem);
				}
				//ArrayList unittargetlist = new ArrayList();
				//unittargetlist.add(new CommonData("",""));//归档关联的单位子集指标
				if(!"".equals(unit)){
					ArrayList fielditemlist = DataDictionary.getFieldList(unit, Constant.USED_FIELD_SET);
					if(fielditemlist!=null){
						for(int i=0;i<fielditemlist.size();i++){
							FieldItem fielditem = (FieldItem)fielditemlist.get(i);
							if("N".equalsIgnoreCase(fielditem.getItemtype()))
								unittargetlist.add(new CommonData(fielditem.getItemid(),fielditem.getItemdesc()));
						}
					}
				}
			}
			InfoSetupBo bo = new InfoSetupBo(this.frameconn);
			// 人员库
			String dbname = bo.getnbase(id);
			
			// 人员库列表
			ArrayList dbList = new ArrayList();
			String dbListSql = "select pre,dbname from dbname order by dbid";
			String dbpriv = this.userView.getDbpriv().toString();
			this.frecset = dao.search(dbListSql);
			while (this.frecset.next()) {
				String pre = this.frecset.getString("pre");
				if (dbpriv.indexOf(pre) != -1||this.userView.isSuper_admin()) {
					CommonData data = new CommonData();
					data.setDataName(this.frecset.getString("dbname"));
					data.setDataValue(this.frecset.getString("pre"));
					dbList.add(data);
				}
			}
			
			// 获得保存的常用条件列表
			ArrayList tempCondList = new ArrayList();
			
			tempCondList = bo.getCondList(id);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = sdf.format(new Date());
			sql = "select MAX(layer) as orglevel,min(codesetid) as norder from organization where codesetid='UN' and codeitemid in(select b0110 from B01)  and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date union select MAX(layer) as orglayer,min(codesetid) as norder from organization where codesetid='UM' and codeitemid in(select b0110 from B01)"
			+" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date order by norder desc";
			
			this.frowset = dao.search(sql);
			CommonData cd = null;
			int ff = 0;
			ArrayList unit_levellist=new ArrayList();
			ArrayList dept_levellist=new ArrayList();
			while(this.frowset.next()){
				
				int level = this.frowset.getInt("orglevel");
				for(int i=1;i<=level;i++){
					cd= new CommonData(i+"",i+"");
					if(ff==0){
						unit_levellist.add(cd);
					}else if(ff==1){
						dept_levellist.add(cd);
					}
				}
				ff++;
			}
			this.getFormHM().put("dept_levellist", dept_levellist);
			this.getFormHM().put("unit_levellist", unit_levellist);
			this.getFormHM().put("auto", auto);
			this.getFormHM().put("unit_level", unit_level);
			this.getFormHM().put("ctrl", ctrl);
			this.getFormHM().put("dept_level", dept_level);
			
			this.getFormHM().put("dbname", dbname);
			this.getFormHM().put("dbList", dbList);
			this.getFormHM().put("tempCondList", tempCondList);
			this.getFormHM().put("unitdatalist", unitdatalist);
			this.getFormHM().put("unit", unit);
			this.getFormHM().put("seasonal", seasonal);
			this.getFormHM().put("seansonaldatalist", seansonaldatalist);
			this.getFormHM().put("volist", volist);
			this.getFormHM().put("unittargetlist", unittargetlist);
			this.getFormHM().put("inforkind", inforkind);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
