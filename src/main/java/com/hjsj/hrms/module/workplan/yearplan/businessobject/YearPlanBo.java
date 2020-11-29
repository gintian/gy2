package com.hjsj.hrms.module.workplan.yearplan.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.htmlparser.Parser;
import org.htmlparser.visitors.TextExtractingVisitor;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 年度计划
 * changxy
 * */
public class YearPlanBo {

	private UserView userview;
	private Connection conn;
	
	
	
	public YearPlanBo(UserView userview,Connection conn) {
		this.userview=userview;
		this.conn=conn;
		
	}
	
	/**
	 * 拖拽排序
	 * */
	public ArrayList<LazyDynaBean> orderYearPlan(LazyDynaBean bean){
		try {
			String orP_id=(String)bean.get("orP_id");
			String orP_41=(String)bean.get("orP_41");
			String toP_id=(String)bean.get("toP_id");
			String toP_41=(String)bean.get("toP_41");
			String dropPosition=(String)bean.get("dropPosition");
			String year=(String)bean.get("year");
			if(StringUtils.isBlank(orP_41))
				orP_41="1";
			if(StringUtils.isBlank(toP_41))
				toP_41="1";
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList list=new ArrayList();
			String sql="";
			String direction="";
			if(Integer.parseInt(orP_41)>Integer.parseInt(toP_41)){
				direction="up";
			}else if(Integer.parseInt(orP_41)<Integer.parseInt(toP_41))
				direction="down";
			if("up".equals(direction)){//上移
				sql="update p17 set p1741=? where p1700=? and p1701=?";
				list.add(toP_41);
				list.add(orP_id);
				list.add(year);
				dao.update(sql, list);
				list=new ArrayList();
				list.add(toP_41);
				list.add(orP_41);
				list.add(year);
				if("before".equals(dropPosition)){//extjs拖拽时，移动对象相对目标对象是在其上
					sql="update p17 set p1741=p1741+1 where p1741>=? and p1741<=? and p1701=? and p1700<>? ";
					list.add(orP_id);
				}else{
					sql="update p17 set p1741=p1741+1 where p1741>=? and p1741<=? and p1701=? and p1700<>? ";
					list.add(toP_id);
				}
				dao.update(sql,list);
			}else if("down".equals(direction)){//下移
				sql="update p17 set p1741=? where p1700=? and p1701=?";
				list.add(toP_41);
				list.add(orP_id);
				list.add(year);
				dao.update(sql, list);
				list=new ArrayList();
				list.add(orP_41);
				list.add(toP_41);
				list.add(year);
				if("after".equals(dropPosition)){
					sql="update p17 set p1741=p1741-1 where p1741>=? and p1741<=? and p1701=? and p1700<>?";
					list.add(orP_id);
				}else{
					sql="update p17 set p1741=p1741-1 where p1741>=? and p1741<=? and p1701=? and p1700<>?";
					list.add(toP_id);
				}
				dao.update(sql, list);
			}
			list.remove(list.size()-1);//移除序号id
			sql="select p1741,p1700 from p17 where p1741>=? and p1741<=? and p1701=?";
			ArrayList<LazyDynaBean> datalist=new ArrayList<LazyDynaBean>();
			RowSet row=dao.search(sql,list);
			while(row.next()){
				bean=new LazyDynaBean();
				bean.set("p1700", row.getInt("p1700"));
				bean.set("p1741", row.getInt("p1741"));
				datalist.add(bean);
			}
			return datalist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/***
	 * 默认加载全部sql与年份切换显示计划sql
	 * */
	public String getDataSql(String year){
		StringBuffer sbf=new StringBuffer();
			sbf.append(" select * from p17 where 1=1");
		if(year!=null&&!"".equals(year)){
			sbf.append(" and p1701 =");
			sbf.append(Integer.parseInt(year));
		}
		return sbf.toString();
	}
	
	/**
	 * 显示年份
	 * */
	public ArrayList getYear(){
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list=new ArrayList();
		RowSet row=null;
		int year=0;
		String sql="select min(p1701) p1701 from p17";
		try {
			row=dao.search(sql);
			while(row.next()){
				year=row.getInt("p1701");
			}
			SimpleDateFormat sft=new SimpleDateFormat("yyyy");
			int currentYear=Integer.parseInt(sft.format(new Date()));
			if(year==0||year==currentYear){//年份记录为空或者为当前年的
				list.add(currentYear);
			}else{
				for (int i = 0; i<=currentYear-year; i++) {
					list.add(year+i);
				}
			}
			list.add(currentYear+1);
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(row);
		}
		return list;
	}
	
	/***
	 * 显示计划制定列
	 * */
	public ArrayList<ColumnsInfo> getColumnList(){
		ArrayList<ColumnsInfo> list=new ArrayList<ColumnsInfo>();
		try {
			ArrayList fieldList=DataDictionary.getFieldList("p17",Constant.USED_FIELD_SET);
			for (int i = 0; i < fieldList.size(); i++) {
				FieldItem item=(FieldItem)fieldList.get(i);
				ColumnsInfo info=new ColumnsInfo();
				info.setColumnId(item.getItemid());
				
				//haosl update 20170228
				if("0".equals(item.getState())){
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
				}
				info.setColumnDesc(item.getItemdesc());
				info.setColumnType(item.getItemtype());
				info.setEditableValidFunc("false");
				if(!"A".equalsIgnoreCase(item.getItemtype())&&!"M".equalsIgnoreCase(item.getItemtype())){//字符型默认居左
					info.setTextAlign("center");	
				}
				if("P1705".equalsIgnoreCase(item.getItemid())){//点击p1705 任务名称列进入编辑界面
					if(this.userview.hasTheFunction("0KR01000104"))
					info.setRendererFunc("yearPlan_me.editPlan");
					info.setColumnWidth(140);
					info.setLocked(true);//默认锁列
				}
				if("P1703".equalsIgnoreCase(item.getItemid())){
					info.setColumnWidth(140);
				}
				if("P1709".equalsIgnoreCase(item.getItemid())
						|| "P1711".equalsIgnoreCase(item.getItemid())
						|| "P1713".equalsIgnoreCase(item.getItemid())
						|| "P1715".equalsIgnoreCase(item.getItemid())){
					//haosl 第一到第四季度完成情况不导出 20170117
//					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//将季度情况的数据隐藏
					//创建自定义列用于点击链接查看季度完成情况
//					ColumnsInfo columnsInfo=new ColumnsInfo();
//					columnsInfo.setColumnId(item.getItemid()+"_");
//					columnsInfo.setColumnDesc(item.getItemdesc());
					info.setRendererFunc("yearPlan_me.sawCompletion");//查看第一至第四完成情况
				}	
				if("p1701".equalsIgnoreCase(item.getItemid())|| "p1735".equalsIgnoreCase(item.getItemid())|| "p1739".equalsIgnoreCase(item.getItemid())){
					info.setColumnType("A");
					info.setTextAlign("center");
					
				}
//				if("1".equalsIgnoreCase(item.getState()) && (item.getItemid().equalsIgnoreCase("p1700")||item.getItemid().equalsIgnoreCase("p1701"))){
//					info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);//初始化不显示  栏目设置后显示
//				}
				
				list.add(info);
			}
			//添加自定义字段
			ColumnsInfo p1709State=new ColumnsInfo();
			p1709State.setColumnId("p1709State");
			p1709State.setColumnDesc("第一季度状态");
			p1709State.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			p1709State.setColumnType("A");
			list.add(p1709State);

			ColumnsInfo p1711State=new ColumnsInfo();
			p1711State.setColumnId("p1711State");
			p1711State.setColumnDesc("第二季度状态");
			p1711State.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			p1711State.setColumnType("A");
			list.add(p1711State);
			
			ColumnsInfo p1713State=new ColumnsInfo();
			p1713State.setColumnId("p1713State");
			p1713State.setColumnDesc("第三季度状态");
			p1713State.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			p1713State.setColumnType("A");
			list.add(p1713State);
			
			ColumnsInfo p1715State=new ColumnsInfo();
			p1715State.setColumnId("p1715State");
			p1715State.setColumnDesc("第四季度状态");
			p1715State.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			p1715State.setColumnType("A");
			list.add(p1715State);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 查询年计划sql
	 * 
	 * */
	/*case  when P1743='01' then '起草' 
	   when P1743='04' then '发布'
	   when P1743='05' then '执行中'
	   when P1743='09' then '暂停'
	   when P1743='06' then '完成'
	   else ''
	   end  p1743
	 * */
	public String getsql(){
		ArrayList fieldList=DataDictionary.getFieldList("p17",Constant.USED_FIELD_SET);
		StringBuffer sbf=new StringBuffer();
		String sql=getDbsql(getDblist());//查询所有人员库的a0100 a0101
		
		sbf.append("select p17.p1700,");
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item=(FieldItem)fieldList.get(i);
			if("P1700".equalsIgnoreCase(item.getItemid()))
				continue;
			if("P1743".equalsIgnoreCase(item.getItemid())){
				sbf.append(" case  when P1743='01' then '起草中' when P1743='04' then '已发布' when P1743='05' then '执行中'  " +
							"when P1743='09' then '已暂停' when P1743='06' then '已完成' else ''   end  p1743 ");
			}else if("p1735".equalsIgnoreCase(item.getItemid())){
				sbf.append(Sql_switcher.dateToChar(item.getItemid(), "YYYY-MM-DD HH24:MI:SS")+"p1735");
			}else if("p1739".equalsIgnoreCase(item.getItemid())){
				sbf.append(Sql_switcher.dateToChar(item.getItemid(), "YYYY-MM-DD HH24:MI:SS")+"p1739");
			}else{
				sbf.append(item.getItemid());
			}
			sbf.append(",");
				
		}
		sbf.append("p1709State,p1711State,p1713State,p1715State");//haosl 20170317 update 查询出四个季度的审批状态，未批准的不允许查看
		
		sbf.append(" from p17 ");
		sbf.append(" left join (select p1700,approve_state p1709State from per_yearplan_approve where quarter=1) q1 on q1.p1700 = p17.p1700");
		sbf.append(" left join (select p1700,approve_state p1711State from per_yearplan_approve where quarter=2) q2 on q2.p1700 = p17.p1700");
		sbf.append(" left join (select p1700,approve_state p1713State from per_yearplan_approve where quarter=3) q3 on q3.p1700 = p17.p1700");
		sbf.append(" left join (select p1700,approve_state p1715State from per_yearplan_approve where quarter=4) q4 on q4.p1700 = p17.p1700");
		sbf.append(" where 1=1");
		StringBuffer bsbf=new StringBuffer();
		String b0110 = this.userview.getUnitIdByBusi("5");//牵头单位在操作人员管理范围（绩效业务管理范围）
		if(b0110.split("`")[0].length() > 2){//组织机构去除UN、UM后不为空：取本级，本级，下级。为空：最高权限
			String[] b0110Array = b0110.split("`");
			if(b0110Array.length>0){
				bsbf.append(" select distinct(p1700) from per_yearplan_obj where");
				bsbf.append("(obj_type='1' and (");
				for (int i = 0; i < b0110Array.length; i++) {
					bsbf.append(" obj_id like '"+b0110Array[i].substring(2)+"%'"); 
					if(i<b0110Array.length-1)
						bsbf.append(" or ");
				}
				bsbf.append(" )) or ");
				//haosl update 20170307   参与任务的可以看到   2：公司领导；3：审核人;7:责任人;8:审批人     
				String id = userview.getDbname()+userview.getA0100();
				
				String guidkey = getGuidKey(id,null); 
				bsbf.append("(obj_type in ('2','3','7','8') and obj_id = '"+guidkey+"')");
				
				//haosl end
			}
		}
		if(bsbf.length()>0){
			sbf.append(" and p17.p1700 in ( ");
			sbf.append( bsbf.toString() );
			sbf.append(" ) ");
		}
		
		return sbf.toString();
	}
	
	/**
	 * 创建计划制定
	 * */
	public boolean createYearPlan(HashMap map){
		
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			RecordVo vo=new RecordVo("P17");
			IDFactoryBean idFactory = new IDFactoryBean();
			ArrayList<FieldItem> fielditems = DataDictionary.getFieldList("p17", Constant.ALL_FIELD_SET);
			String id = idFactory.getId("P17.P1700", "", this.conn);//序号生成器
			rs=dao.search("select "+Sql_switcher.isnull("max(p1741)","0")+"+1 from p17 where p1701="+Integer.parseInt((String)map.get("p1701")));
			int p1741=0;
			if(rs.next()){
				p1741=rs.getInt(1);
			}else{
				p1741=1;
			}
			vo.setInt("p1700", Integer.parseInt(id));
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
			//haosl 20160123 任务创建时根据业务字典的指标控制
			for(int i=0;i<fielditems.size();i++){
				FieldItem fitem = fielditems.get(i);
				String itemid = fitem.getItemid();
				String value=(String)map.get(itemid);
				if(StringUtils.isNotBlank(value)){
					if("D".equals(fitem.getItemtype())){
						if("p1735".equals(itemid)
							||"p1739".equals(itemid)){//创建日期加时分秒
							vo.setDate(itemid, DateUtils.getSqlDate(sdf.parse(value)));
						}else{
							vo.setDate(itemid,DateUtils.getSqlDate(sdf1.parse(value)));
						}
					}else if("N".equals(fitem.getItemtype())){
						vo.setInt(itemid, Integer.valueOf(value));
					}else{
						vo.setString(itemid, value);
					}
				}
			}
			vo.setInt("p1741", p1741);
			dao.addValueObject(vo);
			addyearplanObj(id,(ArrayList)map.get("dutyUion"),"dutyUion");
			addyearplanObj(id,(ArrayList)map.get("leadUion"),"leadUion");
			addyearplanObj(id,(ArrayList)map.get("leader"),"leader");
			addyearplanObj(id, (ArrayList)map.get("approver"), "approver");//审批人
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return false;
	} 
	
	public boolean editYearPlan(HashMap map){
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			RecordVo vo=new RecordVo("P17");
			ArrayList<FieldItem> fielditems = DataDictionary.getFieldList("p17", Constant.ALL_FIELD_SET);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
			//haosl 20160123 任务创建时根据业务字典的指标控制
			for(int i=0;i<fielditems.size();i++){
				FieldItem fitem = fielditems.get(i);
				String itemid = fitem.getItemid();
				String value=(String)map.get(itemid);
				if(!map.containsKey(itemid))//haosl 20170307   update 未提交修改的不需要修改
					continue;
				if(StringUtils.isNotBlank(value)){
					if("D".equals(fitem.getItemtype())){
						if("p1735".equals(itemid)
								||"p1739".equals(itemid)){//创建日期加时分秒
							vo.setDate(itemid, DateUtils.getSqlDate(sdf.parse(value)));
						}else{
							vo.setDate(itemid,DateUtils.getSqlDate(sdf1.parse(value)));
						}
					}else if("N".equals(fitem.getItemtype())){
						vo.setInt(itemid, Integer.valueOf(value));
					}else{
						vo.setString(itemid, value);
					}
				}else
					vo.setDate(itemid,"");
			}
			vo.setInt("p1701", Integer.parseInt((String)map.get("p1701")));//年份 操作人在浏览界面选中年份时创建的
			dao.updateValueObject(vo);
			String id = (String)map.get("p1700");
			delYearPlanObj(id,(ArrayList)map.get("dutyUion"),"dutyUion");
			delYearPlanObj(id,(ArrayList)map.get("leadUion"),"leadUion");
			delYearPlanObj(id,(ArrayList)map.get("leader"),"leader");
			delYearPlanObj(id, (ArrayList)map.get("approver"), "approver");//审批人
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * @Title: addyearplanObj   
	 * @Description:    
	 * @param @param planid  使用序号生成器生成的年度计划id
	 * @param @param list    牵头单位责任单位公司领导等
	 * @param @param type	添加的类别
	 * @param @return 
	 * @return boolean    
	 * @throws
	 */
	public boolean addyearplanObj(String planid,ArrayList list,String type){
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList<RecordVo> listVo=new ArrayList<RecordVo>();
		if("dutyUion".equals(type)){//责任单位
			type="0";
		}else if("leadUion".equals(type)){//牵头单位
			type="1";
		}else if("leader".equals(type)){//公司领导
			type="2";
		}else if("approver".equals(type)){//审批人
			type="8";
		}	
		try {
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo=new RecordVo("per_yearplan_obj");	
					vo.setInt("p1700", Integer.parseInt(planid));
					vo.setString("obj_type", type);
				if(list.get(i)!=null&&!"".equals((String)list.get(i))){
					if("2".equals(type)||"8".equals(type)){//公司领导/审批人等人员存储guidkey
						String guidkey=getGuidKey(PubFunc.decrypt((String)list.get(i)),null);
						if(guidkey!=null&&!"".equals(guidkey)){
							vo.setString("obj_id",guidkey);
							listVo.add(vo);
						}
					}else{
						vo.setString("obj_id", PubFunc.decrypt((String)list.get(i)));
						listVo.add(vo);
					}
					
					
				}
			}
			dao.addValueObject(listVo);
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 执行修改保存操作先删除对应类型的id 再保存
	 * */
	public void delYearPlanObj(String planid,ArrayList list,String type){
		if("dutyUion".equals(type)){//责任单位
			type="0";
		}else if("leadUion".equals(type)){//牵头单位
			type="1";
		}else if("leader".equals(type)){//公司领导
			type="2";
		}else if("approver".equals(type)){//审批人
			type="8";
		}
		ContentDAO dao=new ContentDAO(this.conn);
		
		ArrayList<RecordVo> listVo=new ArrayList<RecordVo>();
		ArrayList idlist=new ArrayList();
		idlist.add(planid);
		idlist.add(type);
		try {
		
			//dao.deleteValueObject(vo);//编辑保存前，先将对应类型存储的id删除
			dao.delete("delete from per_yearplan_obj where p1700=? and obj_type=?", idlist);
			if(list.size()>0){
				for (int i = 0; i < list.size(); i++) {
					if(list.get(i)!=null&&!"".equals(list.get(i))){
						RecordVo vo=new RecordVo("per_yearplan_obj");
						vo.setInt("p1700", Integer.parseInt(planid));
						vo.setString("obj_type",type);
						if("2".equals(type)||"8".equals(type)){//存储guidkey 人员类
							vo.setString("obj_id", getGuidKey(PubFunc.decrypt((String)list.get(i)),null));
						}else{
							vo.setString("obj_id", PubFunc.decrypt((String)list.get(i)));
						}
						//}
						listVo.add(vo);
					}
				}
				dao.addValueObject(listVo);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询人员的唯一性标识
	 * 或根据gukid查询人员id
	 * */
	public String getGuidKey(String id,String guidkeys){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		//ArrayList<String> dblist=getDblist();//
		String idorkey="";
		try {
			if(id!=null&&guidkeys==null){
				String sql="select guidkey from "+id.substring(0, 3)+"A01 where a0100='"+id.substring(3)+"'"  ;
				
				rs=dao.search(sql);
				while(rs.next()){
					idorkey=rs.getString("guidkey");
				}
				
			}else if(id==null&&guidkeys!=null){//根据key找到人员id
				ArrayList dblist=getDblist();
				StringBuffer sbf=new StringBuffer();//"select guidkey from "+id.substring(0, 3)+"A01 where a0100='"+id.substring(3)+"'"  ;
				//sbf.append("select a.a0100 a0100,a.a0101 a0101  from (");
				for (int i = 0; i < dblist.size(); i++) {
					sbf.append(" select '"+dblist.get(i)+"'"+Sql_switcher.concat()+"A0100 a0100,a0101 from "+dblist.get(i)+"A01 where guidkey='"+guidkeys+"'");// );
					if(i<dblist.size()-1)
						sbf.append(" union all");
					
				}
				rs=dao.search(sbf.toString());
				while(rs.next()){
					idorkey=rs.getString("a0100")+","+rs.getString("a0101")+","+rs.getString("a0100").substring(0, 3);
				}
			}

		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return idorkey;
	}
	
	/**
	 * 
	 * @Title: updatePlanType   
	 * @Description:    
	 * @param @param list 计划序号
	 * @param @param type 计划类型：暂停 发布
	 * @param @return 
	 * @return boolean    
	 * @throws
	 */
	public boolean updatePlanType(ArrayList list,String type){
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList<RecordVo> listVo=new ArrayList<RecordVo>();
		try {
			for (int i = 0; i < list.size(); i++) {
				RecordVo vo=new RecordVo("p17");
				vo.setInt("p1700", (Integer)list.get(i));
				if("done".equalsIgnoreCase(type)){
					vo.setString("p1743", "06");
				}else if("assign".equalsIgnoreCase(type)){//发布的时候如果发现已经进行过指派，则直接将任务状态置为进行中
					vo.setString("p1743", "05");
				}else{
					
					vo.setString("p1743", "release".equals(type)?"04":"09");
				}
				listVo.add(vo);
			}
			dao.updateValueObject(listVo);
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 编辑或者任务追踪查看某一条计划
	 * */
	public ArrayList searchYearPlan(String id){
		ContentDAO dao=new ContentDAO(this.conn);
		
		RowSet rs=null;
		ArrayList list=new ArrayList();
		ArrayList typelist=new ArrayList();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			String sql="select * from p17 where p1700='"+id+"'";
			rs=dao.search(sql);
			while(rs.next()){
				HashMap map=new HashMap();
				ResultSetMetaData metaData = rs.getMetaData();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String value ="";
					String column = metaData.getColumnName(i);
					if(metaData.getColumnType(i)==2005){//解决oracle clob类型
						value = Sql_switcher.readMemo(rs,column);
					}else{
						String colTyep = metaData.getColumnTypeName(i);
						if("date".equalsIgnoreCase(colTyep)){
							if(Sql_switcher.searchDbServer()==Constant.ORACEL){
								if(rs.getTimestamp(column)!=null){
									value = sdf.format(rs.getTimestamp(column));
								}
							}else{
								if(rs.getDate(column)!=null){
									value = sdf.format(rs.getDate(column));
								}
							}
						}else
							value = rs.getString(column);
						if("p1700".equalsIgnoreCase(column)
								||"p1721".equalsIgnoreCase(column)){
							if(StringUtils.isNotBlank(column))
								value = PubFunc.encrypt(value);
						}
					}
					map.put(metaData.getColumnName(i).toLowerCase(), value);
				}
				list.add(map);
			}
			rs=dao.search("select * from per_yearplan_obj where p1700='"+id+"' order by obj_type");  
			String orgid="";
			HashMap responsMap=new HashMap();
			while(rs.next()){
				HashMap maps=new HashMap();
				String obj_id=rs.getString("obj_id");
				String obj_type=rs.getString("obj_type");
				maps.put("type", obj_type);//类别0：责任单位；1：牵头单位；2：责任处/作业区；3：公司领导；4：审核人；5：责任处/作业区；6：责任组；7：责任岗位；8：责任人 9 审批人
				maps.put("typeid",PubFunc.encrypt(obj_id));//组织机构代码或人员guidkey
				if("0".equals(obj_type)||"1".equals(obj_type)||"4".equals(obj_type)||"5".equals(obj_type)||"6".equals(obj_type)){
					CodeItem umItem=AdminCode.getCode("UM", obj_id);//
					CodeItem unItem=AdminCode.getCode("UN", obj_id);//
					CodeItem kItem=AdminCode.getCode("@K", obj_id);//单位
					if("1".equals(obj_type)){//牵头单位
						orgid+=obj_id+",";
					}
					if("4".equals(obj_type)||"6".equals(obj_type)){//单独统计责任处 责任岗位 任务指派修改时使用
						responsMap.put("obj"+obj_type, PubFunc.encrypt(obj_id));
					}
					if(umItem!=null){
						maps.put("codename", "UM");//存储单位部门或岗位类别
						maps.put("codedesc", umItem.getCodename());
					}else if(unItem!=null){
						maps.put("codename", "UN");
						maps.put("codedesc", unItem.getCodename());
					}else if(kItem!=null){
						maps.put("codename", "@K");
						maps.put("codedesc", kItem.getCodename());
					}
				}else{//人员类
					String[] arry=getGuidKey(null,obj_id).split(",");
					
					maps.put("id", PubFunc.encrypt(arry[0].substring(3)));//a0100
					maps.put("name", arry[1]);//姓名
					maps.put("nbase", PubFunc.encrypt(arry[2]));//库标识
					maps.put("ids", PubFunc.encrypt(arry[0]));
				}
				typelist.add(maps);
			}
			if(typelist.size()>0){
				HashMap map=new HashMap();
				map.put("typelist", typelist);
				map.put("orgid",orgid);//牵头单位
				list.add(map);
			}
			list.add(responsMap);

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 
	 * @Title: delPlan   
	 * @Description:    
	 * @param @param list 删除计划序列
	 * @param @return 
	 * @return boolean    
	 * @throws
	 */
	public boolean delPlan(ArrayList list){
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList<RecordVo> listVo=new ArrayList<RecordVo>();
		try {
			for (int i = 0; i < list.size(); i++) {
				ArrayList yearplanobj=new ArrayList();
				int id=(Integer)list.get(i);
				RecordVo vo=new RecordVo("p17");
				vo.setInt("p1700",id);
				listVo.add(vo);
/*				RecordVo vobj=new RecordVo("per_yearplan_obj");
				vobj.setInt("p1700", id);*/
				yearplanobj.add(id);
				dao.delete("delete from per_yearplan_obj where p1700=?", yearplanobj);
			}
			dao.deleteValueObject(listVo);
			//添加删除per_yearplan_obj对应的记录
			
			
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	/***
	 * 查询所有人员库sql
	 * select 'Usr'+A0100 a0100,a0101 from UsrA01 union 
		select 'Ret'+A0100 a0100,a0101 from RetA01 union
		select 'Trs'+A0100 a0100,a0101 from TrsA01 union
		select 'Oth'+A0100 a0100,a0101 from OthA01 
	 * */
	public String getDbsql(ArrayList<String> list){
		StringBuffer sbf=new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			String db=list.get(i);
			sbf.append(" select '"+db+"'"+Sql_switcher.concat()+"a0100 a0100,a0101 from "+db+"A01 ");
			if(i<list.size()-1)
				sbf.append(" union ");
		}
		
		return sbf.toString();
	}
	/***
	 * 查询上级部门
	 * @throws GeneralException 
	 * */
	public String getOrgName(ArrayList list,String orgid) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			String e0122="";
			boolean flag=false;
			
			String sql = "select parentid from organization where codeitemid=?";
			List values = new ArrayList();
			values.add(orgid);
			rs = dao.search(sql,values);
			if(rs.next())
				e0122 = rs.getString("parentid");
			for (int i = 0; i < list.size(); i++) {
				String id=PubFunc.decrypt((String)list.get(i));
				if(id.length()>e0122.length()){//查询出的上级单位大于牵头单位内的单位
					flag=true;
				}
			}
			if(flag){
				return "";//上级单位大于牵头单位返回上级单位为“”；
			}else{
				CodeItem code=AdminCode.getCode("UM", e0122);
				CodeItem code1=AdminCode.getCode("UN", e0122);
				if(code!=null){
					return e0122+","+code.getCodename();
				}else if(code1!=null){
					return e0122+","+code1.getCodename();
				}else{
					return "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(rs);
		}
		
	}
	
	
	/***
	 * 查询库
	 * */
	public ArrayList<String> getDblist(){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		ArrayList<String> list=new ArrayList<String>();
		try {
			String sql="select Pre from DBName";
			rs=dao.search(sql);
			while(rs.next()){
				list.add(rs.getString("Pre"));
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/***
	 * 
	 * @Title: addTaskAssign   
	 * @Description: 保存任务指派   
	 * @param @param bean
	 * @param @return 
	 * @return boolean    
	 * @throws
	 */
	public boolean addTaskAssign(MorphDynaBean bean){
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			String p1700 = (String)bean.get("p1700");
			p1700=PubFunc.decrypt(p1700);
			int id=Integer.parseInt(p1700);
			RecordVo vo=new RecordVo("p17");
			vo.setInt("p1700",id);
			vo.setString("p1731", (String)bean.get("p1731"));//责任人
			vo.setString("p1729", (String)bean.get("p1729"));//责任岗位
			vo.setString("p1727", (String)bean.get("p1727"));//责任组
			vo.setString("p1725", (String)bean.get("p1725"));//责任室
			vo.setString("p1723", (String)bean.get("p1723"));//审核人
			vo.setString("p1743", "05");//已发布的计划完成任务指派操作 状态改为执行中
			dao.updateValueObject(vo);
			
			ArrayList dellist=new ArrayList();
			dellist.add(id);
			dao.delete("delete from per_yearplan_obj where p1700=? and obj_type in('7','3','6','4','5')", dellist);
			
			dao.delete("delete from per_yearplan_approve where p1700=?", dellist);//任务重新指派删除任务跟踪相关数据
			
			
			ArrayList<RecordVo> listVo=new ArrayList<RecordVo>();
			String deperResponse =  detilPerson((ArrayList)bean.get("deperResponse"));
			if(StringUtils.isNotBlank(deperResponse)){
				String guidkey = getGuidKey(deperResponse, null);//责任人
				if(StringUtils.isNotBlank(guidkey)){
					vo=new RecordVo("per_yearplan_obj");
					vo.setInt("p1700", id);
					vo.setString("obj_type", "7");
					vo.setString("obj_id", guidkey);
					listVo.add(vo);
				}
			}
		
			String deperAuditor =  detilPerson((ArrayList)bean.get("deperAuditor"));
			if(StringUtils.isNotBlank(deperAuditor)){
				String guidkey=getGuidKey(deperAuditor, null);//审核人
				if(StringUtils.isNotBlank(guidkey)){
					vo=new RecordVo("per_yearplan_obj");
					vo.setInt("p1700", id);
					vo.setString("obj_type", "3");
					vo.setString("obj_id", guidkey);
					listVo.add(vo);
				}
			}
			MorphDynaBean beanid=(MorphDynaBean)bean.get("responseobj");
			String responsepostId = "";//责任岗位
			String operatingAreaId = "";//责任室
			String responsegroupId="";//责任组
			if(beanid!=null){//处理针对没有选择审核人负责人就保存的任务指派处理
				if(beanid.get("responsepostId")!=null)
					responsepostId = PubFunc.decrypt((String)beanid.get("responsepostId"));//责任岗位
				if(beanid.get("operatingAreaId")!=null)
					operatingAreaId = PubFunc.decrypt((String)beanid.get("operatingAreaId"));//责任室
				if(beanid.get("responsegroupId")!=null)
					responsegroupId = (String)beanid.get("responsegroupId");///责任组
			}
			
			if(StringUtils.isNotBlank(responsepostId)){
				vo=new RecordVo("per_yearplan_obj");
				vo.setInt("p1700", id);
				vo.setString("obj_type", "6");
				vo.setString("obj_id", responsepostId);
				listVo.add(vo);
			}
			
			if(StringUtils.isNotBlank(operatingAreaId)){
				
				vo=new RecordVo("per_yearplan_obj");
				vo.setInt("p1700", id);
				vo.setString("obj_type", "4");
				vo.setString("obj_id", operatingAreaId);
				listVo.add(vo);
			}
			
			if(StringUtils.isNotBlank(responsegroupId)){
				vo=new RecordVo("per_yearplan_obj");
				vo.setInt("p1700", id);
				vo.setString("obj_type", "5");
				vo.setString("obj_id", responsegroupId);
				listVo.add(vo);
			}
			//人员存储guidkey
			dao.addValueObject(listVo);
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 解密人员id
	 * */
	public String detilPerson(ArrayList list){
		String id="";
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i)!=null&&!"".equals(list.get(i))){
				id=PubFunc.decrypt((String)list.get(i));
			}
		}
		return id;
	}
	/**
	 * 查询给定的计划集合中是否有已经指派过的计划
	 * 有则返回计划集合，没有返回空集合
	 * @param arry
	 * @throws GeneralException 
	 */
	public ArrayList<Integer> hasPlanAssigned(ArrayList arry) throws GeneralException {
		ContentDAO dao  = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			ArrayList<Integer> list = new ArrayList<Integer>();
			if(arry.size()==0)
				return list;
			String ids = StringUtils.join(arry.toArray(),",");
			String sql = "select distinct(p1700) from per_yearplan_obj where p1700 in("+ids+") and obj_type=3 and obj_id is not null";
			rs = dao.search(sql);
			while(rs.next()){
				list.add(rs.getInt("p1700"));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 生成功能导航菜单的json串
	 * @param name 菜单名
	 * @param id   菜单id
	 * @param list 菜单功能集合
	 * @return
	 */
	public String getMenuStr(String name,String id,ArrayList list){
		StringBuffer str = new StringBuffer();
		try{
			if(name.length()>0){
				str.append("<jsfn>{xtype:'button',text:'"+name+"'");
			}
			if(StringUtils.isNotBlank(id)){
				str.append(",id:'");
				str.append(id);
				str.append("'");
			}
			str.append(",menu:{items:[");
			for(int i=0;i<list.size();i++){
				LazyDynaBean bean = (LazyDynaBean) list.get(i);
				if(i!=0)
					str.append(",");
				str.append("{");
				if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
					str.append("xtype:'"+bean.get("xtype")+"'");
				if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
					str.append("text:'"+bean.get("text")+"'");
				if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
					if(bean.get("xtype")!=null&& "datepicker".equalsIgnoreCase(bean.get("xtype").toString())){//时间控件单独处理一下 方法GzGlobal.aaa(picker, date)这样写
						str.append(",handler:function(picker, date){"+bean.get("handler")+";}");
					}else{
						str.append(",handler:function(){"+bean.get("handler")+";}");
					}				
				}
				String menuId = (String)bean.get("id");
				
				if(menuId!=null&&menuId.length()>0)//人事异动-手工选择按钮需要id（gaohy）
					str.append(",id:'"+menuId+"'");
				else
					menuId = "";
				if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
					str.append(",icon:'"+bean.get("icon")+"'");
				if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
					str.append(",value:"+bean.get("value")+"");
				ArrayList menulist = (ArrayList)bean.get("menu");
				if(menulist!=null&&menulist.size()>0){
					str.append(getMenuStr("",menuId, menulist));
				}
				str.append("}");
			}
			str.append("]}");
			if(name.length()>0){				
				str.append("}</jsfn>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str.toString();
	}
	
	/**
	 * 生成菜单的bean
	 * @param text    文本内容
	 * @param handler 触发事件
	 * @param icon    图标
	 * @param list    按钮集合
	 * @return
	 */
	public LazyDynaBean getMenuBean(String text,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	public ArrayList getHeadList(TableDataConfigCache tableCache) {
		ArrayList columns= tableCache.getTableColumns();
		ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
		LazyDynaBean headBean = null;
		int colNum = 0;
		for(int i=0;columns!=null && i<columns.size();i++){
			ColumnsInfo column = (ColumnsInfo) columns.get(i);
			headBean = new LazyDynaBean();
			String itemid = column.getColumnId();
			if(column.getLoadtype()!= ColumnsInfo.LOADTYPE_BLOCK){
				headBean.set("columnHidden", true);
			}
			HashMap headStyleMap = new HashMap();//表头样式设置
	    	headStyleMap.put("fillForegroundColor", HSSFColor.GREY_25_PERCENT.index);// 背景色
	    	if("p1709".equalsIgnoreCase(itemid)
	    			||"p1711".equalsIgnoreCase(itemid)
	    			||"p1713".equalsIgnoreCase(itemid)
	    			||"p1715".equalsIgnoreCase(itemid)){
	    		headStyleMap.put("columnWidth",column.getColumnWidth()*60);//表头宽度设置 
	    	}else
	    		headStyleMap.put("columnWidth",column.getColumnWidth()*30);//表头宽度设置 
			
			headBean.set("itemid",column.getColumnId());//列标题代码
			headBean.set("colType",column.getColumnType());//该列的类型，D：日期，N：数字，A：字符
			headBean.set("content",column.getColumnDesc());//表头
			headBean.set("columnLocked", column.isLocked());
			
			headBean.set("codesetid", column.getCodesetId().toUpperCase());//列头代码
			headBean.set("decwidth",  column.getDecimalWidth()+"");//列小数位数
			headBean.set("fromRowNum", 0);//单元格开始行
			headBean.set("toRowNum", 1);//单元格结束行
			headBean.set("fromColNum", colNum);//单元格开始行列
			headBean.set("toColNum", colNum);//单元格结束行列
	        headBean.set("headStyleMap", headStyleMap);//表头样式
	        headList.add(headBean);
	        colNum++;
		}
		return headList;
	}

	public ArrayList getDataList(TableDataConfigCache tableCache) throws GeneralException {
		ArrayList resultlist=new ArrayList();
		 RowSet rs=null;
		try{
			ContentDAO dao=new ContentDAO(conn);
			String tableSql = (String)tableCache.get("combineSql");
			String sortSql = tableCache.getSortSql();
			rs=dao.search(tableSql+" "+sortSql);

			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			int rownum = 2;
			while(rs.next()){
				LazyDynaBean rowDataBean=new LazyDynaBean();
				for(int i=0;i<columnCount;i++){
					LazyDynaBean bean = new LazyDynaBean();
					String fieldname = meta.getColumnName(i + 1).toLowerCase();
					String oTemp = PubFunc.getValueByFieldType(rs, meta, i+1,true);
					if(oTemp!=null)
					{
						if("i9999".equalsIgnoreCase(fieldname))
						{
							if(oTemp.indexOf(".")!=-1)
							{
								oTemp=oTemp.substring(0,oTemp.indexOf("."));
							}
						//未批准状态的季度总结不导出
						}else if("p1709".equalsIgnoreCase(fieldname)
									||"p1711".equalsIgnoreCase(fieldname)
									||"p1713".equalsIgnoreCase(fieldname)
									||"p1715".equalsIgnoreCase(fieldname)){
							if("03".equals(rs.getString(fieldname+"state"))){
								oTemp = getTextByHtml(oTemp);
							}else{
								oTemp = "";
							}
						//开始和结束日期去掉时分秒
						}else if(StringUtils.isNotBlank(oTemp) && ("p1745".equalsIgnoreCase(fieldname)
									||"p1747".equalsIgnoreCase(fieldname))){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
							SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
							
							oTemp = sdf1.format(sdf.parse(oTemp));
						}
						bean.set("content", oTemp);
					}else{
						bean.set("content","");				 
				    }
					bean.set("fromRowNum",rownum);
					bean.set("toRowNum",rownum);
					bean.set("fromColNum",i);
					bean.set("toColNum",i);
					rowDataBean.set(fieldname, bean);
				}
				rownum++;
				resultlist.add(rowDataBean);
			}
		}catch (Exception  ex){
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
	    }finally{
			PubFunc.closeResource(rs);
		}
		return resultlist;
	}
	/**
	 *  
	 * @param html
	 * @return
	 * @throws GeneralException
	 */
	private String getTextByHtml(String html) throws GeneralException{
		if(StringUtils.isBlank(html))
			return "";
		Parser parser;
		try {
			html = "<div>"+html+"</div>";//不加div标签会报错
			parser = new Parser(html);
			TextExtractingVisitor visitor = new  TextExtractingVisitor();
			parser.visitAllNodesWith(visitor);
			return visitor.getExtractedText(); 
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
}