package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 编制控制支持条件控制业务类
 * @author xuj 2013-08-16
 * 编制控制调用步骤
 * ScanFormationBo scanFormationBo=new ScanFormationBo(conn,userView);
 * if(scanFormationBo.doScan()){
 * 		if(scanFormationBo.needDoScan(dbnames,itemids)){
 * 			调用端组装要插入或修改记录放到list<LazyDynaBean>
 * 			scanFormationBo.execDate2TmpTable(传入上面代码组装的list<LazyDynaBean>);
 * 			String mess=scanFormationBo.isOverstaffs();
 * 			if(!"ok".equals(mess)){
 * 				if("warn".equals(scanFormationBo.getMode())){
 * 					//提示，继续。
 * 				}else{
 * 					//提示，中止。
 * 				}
 * 			}
 * 		}
 * }
 * 编制参数保存格式
 *<?xml version="1.0" encoding="GB2312"?>
	<params>
	  <amounts setid="B07" sp_flag="D0701" ctrl_type="1" dbs="Usr" nextlevel="0" ctrlitemid="D0702">
    	<ctrl_item planitem="B0705" realitem="B0710" static="1" flag="0" method="0" message="阿萨德发生" ctrlorg="UN0101,UN010201,UN010202," nextorg="1" />
    	<ctrl_item planitem="B0715" realitem="B0717" static="2" flag="1" method="0" message="呜呜呜呜" ctrlorg="UN010201,UN010202," nextorg="0" />
      </amounts>
	</params>
		PosparameXML pos = new PosparameXML(this.frameconn);
		String ps_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
		String sp_flag = pos.getValue(PosparameXML.AMOUNTS,"sp_flag");
		String psvalid = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
		String dbpre = pos.getValue(PosparameXML.AMOUNTS,"dbs");
		String nextlevel = pos.getValue(PosparameXML.AMOUNTS,"nextlevel");
		nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
		ArrayList clist = pos.getChildList(PosparameXML.AMOUNTS,ps_set);
		if(clist.size()>0){
			for(int i=0;i<clist.size();i++){
				LazyDynaBean bean = new LazyDynaBean();
				String planitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"planitem");
				String realitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"realitem");
				String staticitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"static");
				String flag = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"flag");
				String method = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"method");
				String cond = pos.getTextValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString());
				String message = pos.getChildValue(PosparameXML.AMOUNTS, ps_set, clist.get(i).toString(), "message");
				message = message == null ?"":message;
				String ctrlorg = pos.getChildValue(PosparameXML.AMOUNTS, ps_set, clist.get(i).toString(), "ctrlorg");
				ctrlorg = ctrlorg == null ?"":ctrlorg;
			}
		}
 */
public class ScanFormationBo {

	private Category log = Category.getInstance(this.getClass().getName());
	private Connection conn;
	private UserView userView;
	private boolean doscan=false;
	private boolean orgscan=false;
	//编制人员库
	private String dbs;
	//编制控制方式预警|强制
	private String mode;
	private String regulationField;
	private ArrayList regulationFieldList= new ArrayList();
	//单位编制子集
	private String ps_set;
	private ArrayList clist;
	private PosparameXML pos;
	private Sys_Oth_Parameter sysbo;
	//岗位是否编制控制
	private String zwvalid;
	/**兼职参数 兼职是否启用*/
	private String part_flag;
	private String part_unit;
	private String part_setid;
	private String part_appoint;
	private String part_pos;
	private String part_dept;
	private String part_takeup_quota;
	private String part_occupy_quota;
	//岗位是否有修改
	private boolean isPosChange=true;
	
	private Map orgSetMap=new HashMap();
	private Map kSetMap = new HashMap();
	//人员信息变动标识
	private String InfoChangeFlag = "";
	
	public ScanFormationBo(Connection conn){
	    this.conn = conn;
	    
	}
	
	/**
	 * 检查编制用此构造方法
	 * @param conn
	 * @param userView
	 */
	public ScanFormationBo(Connection conn,UserView userView){
		this.userView=userView;
		this.conn=conn;
		pos = new PosparameXML(conn);
		sysbo =new Sys_Oth_Parameter(conn);
		mode=sysbo.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
		ps_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
		FieldSet fieldset = DataDictionary.getFieldSetVo(ps_set);
		dbs= pos.getValue(PosparameXML.AMOUNTS,"dbs");
		clist = pos.getChildList(PosparameXML.AMOUNTS,ps_set);
		if(clist.size()>0&&fieldset!=null&&!"0".equalsIgnoreCase(fieldset.getUseflag())){
			for(int i=0;i<clist.size();i++){
				String flag = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"flag");
				String planitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"planitem");
				FieldItem fielditem = DataDictionary.getFieldItem(planitem);
				if("1".equals(flag)&&fielditem!=null&&!"0".equals(fielditem.getUseflag())){
					doscan=true;
					break;
				}
			}
		}
		regulationField=this.getFiled();
		zwvalid=sysbo.getValue(Sys_Oth_Parameter.WORKOUT,"pos");
		this.initPartParam();
		
		updateTable();
	}
	
	/**
	 * 初始化兼职参数
	 */
	private void initPartParam(){
		ArrayList list = new ArrayList();
		list.add("flag");//启用标识
		list.add("unit");//兼职单位标识
		list.add("setid");//兼职子集
		list.add("appoint");//任免标识
		list.add("pos");//任免职务
		list.add("dept");//兼职部门			
		list.add("order");//排序
		list.add("format");//兼职内容显示格式
		list.add("takeup_quota");//兼职占用岗位编制：1占用，0或null 则不占用
		list.add("occupy_quota");//兼职占用单位部门编制：1占用，0或null 则不占用
		HashMap map = sysbo.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
		if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0) {
                part_flag=(String)map.get("flag");//启用标识
            }
			if(map.get("takeup_quota")!=null && ((String)map.get("takeup_quota")).trim().length()>0) {
                part_takeup_quota=(String)map.get("takeup_quota");//兼职岗位占用编制
            }
			if(map.get("occupy_quota")!=null && ((String)map.get("occupy_quota")).trim().length()>0) {
                part_occupy_quota=(String)map.get("occupy_quota");//兼职单位部门占用编制
            }
			if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0){
				part_unit=(String)map.get("unit");//兼职单位标识
				FieldItem item = DataDictionary.getFieldItem(part_unit);
				if(item==null||!"1".equals(item.getUseflag())) {
                    part_unit="";
                }
			}
			if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0){
				part_appoint=(String)map.get("appoint");//任免标识
				FieldItem item = DataDictionary.getFieldItem(part_appoint);
				if(item==null||!"1".equals(item.getUseflag())){
					part_appoint="";
					part_flag="false";
				}
			}
			if(map.get("pos")!=null && ((String)map.get("pos")).trim().length()>0){
				part_pos=(String)map.get("pos");//任免职务
				FieldItem item = DataDictionary.getFieldItem(part_pos);
				if(item==null||!"1".equals(item.getUseflag())){
					part_pos="";
					part_takeup_quota="0";
				}
			}
			if(map.get("dept")!=null && ((String)map.get("dept")).trim().length()>0){
				part_dept=(String)map.get("dept");//兼职部门
				FieldItem item = DataDictionary.getFieldItem(part_dept);
				if(item==null||!"1".equals(item.getUseflag())) {
                    part_dept="";
                }
			}
			if((this.part_dept==null || this.part_dept.length()==0)&&
					(this.part_unit==null || this.part_unit.length()==0)) {
                this.part_occupy_quota="0";
            }
				
			if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0){
				part_setid=(String)map.get("setid");//兼职子集
				FieldSet set = DataDictionary.getFieldSetVo(part_setid);
				if(set==null||!"1".equals(set.getUseflag())){
					part_setid="";
					part_flag="false";
				}
			}
			
			if(!"true".equals(part_flag)){
				part_takeup_quota="";
				part_occupy_quota="";
				part_unit="";
				part_appoint="";
				part_pos="";
				part_dept="";
			}
		}
	}
	/**是否需要进行编制检查。
	 * 
	 */
	public boolean doScan(){
		//检查是否存在有效的编制项
		//或岗位编制控制
		orgscan=doscan;
		return doscan||"true".equals(zwvalid);
	}
	/**
	 * 编制是预警还是强制控制。 force|warn
	 */
	public String getMode(){
		return mode;
	}
	/**
	 * 编制控制临时表名。
	 */
	private String getTmpTableName(){
		return "t_hr_scan_assist";
	}
	/**
	 * 编制人员库涉及的人员库列表（返回逗号分隔字符串）。
	 */
	private String getDbs(){
		return dbs;
	}
	/**
	 * 编制涉及哪些指标（返回一个指标列表）。
	 */
	private String getRegulationField(){
		//b0110,e0122,e01a1,+其他公式使用的指标
		return this.regulationField;
	}
	/**
	 * 编制涉及哪些指标（返回一个指标列表）。
	 */
	private ArrayList getRegulationFieldList(){
		//b0110,e0122,e01a1,+其他公式使用的指标
		return this.regulationFieldList;
	}
	
	/**
	 * 变动信息是否引起编制检查（人员库和指标列表用逗号分隔字符串）。
	 * @param dbnames 人员库
	 * @param itemids 指标列表(修改操作只传修改的指标（不能将所有页面指标全传递过来），新增操作可以将页面中涉及的全部指标也可以是有值的那些指标)
	 * @return 是否影响到编制
	 */
	public boolean needDoScan(String dbnames,String itemids){
		boolean dbflag = false;
		boolean itemflag = false;
		String[] dbnamess = dbnames.split(",");
		for(int i=0;i<dbnamess.length;i++){
		    String tempdbs = this.getDbs().toLowerCase();//为了兼容老数据中USR与现在数据中Usr人员库标识不一的情况做，所以改成这个模式
		    String tempdbnames =dbnamess[i].toLowerCase();
		    if(tempdbs.indexOf(tempdbnames)!=-1&&tempdbnames.length()==3){
                dbflag=true ;
                break;
            }
		    /**
			if(this.getDbs().indexOf(dbnamess[i])!=-1&&dbnamess[i].length()==3){
				dbflag=true ;
				break;
			}
			**/
		}
		String[] itemidss = itemids.toLowerCase().split(",");
		for(int i=0;i<itemidss.length;i++){
			if(this.getRegulationField().indexOf(","+itemidss[i]+",")!=-1){
				itemflag= true;
				break;
			}
		}
		
		orgscan = dbflag&&(itemflag||"All".equalsIgnoreCase(itemids));
		
		boolean flag = false;
		//删除信息时，若有兼职占编控制则需同时判断是否符合编制占编，同时岗位校验是否开启，如果不是删除则按原来的
		if("delete".equalsIgnoreCase(this.InfoChangeFlag)) {
            flag = ("true".equals(zwvalid) && needgetPartRec());
        } else {
            flag = ("true".equals(zwvalid));
        }
		        
		return dbflag //当前操作的人员库与编制检测的人员库是否一致
		       &&
		      (itemflag //变动的指标是否包含编制设置的指标
		         //是否是全部指标都需要校验
		       ||"All".equalsIgnoreCase(itemids) 
		       ||flag/*是否岗位编制控制 */);
	}
	/**
	 * 获取修改操作
	 * @param A0100
	 * @param table
	 * @return
	 */
	private String[] getOrg(String A0100,String table){
		String[] org = {"",""};
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search("select B0110,E0122 from "+table+" where A0100='"+A0100+"'");
			if(rs.next()){
				org[0]=rs.getString("B0110");
				org[1]=rs.getString("E0122");
			}else
			{
				org[0]="";
				org[1]="";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		    try {
		        
		        if(rs!=null) {
                    rs.close();
                }
            } catch (SQLException e) {
               e.printStackTrace();
            }
		}
		return org;
	}
	/**
	 * 获取b0110/e0122/e01a1
	 * @param bean
	 * @param itemids
	 * @return
	 */
	private LazyDynaBean getMainOrg(LazyDynaBean bean,String itemids){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search("select "+itemids+" from "+bean.get("nbase")+"A01 where A0100='"+bean.get("a0100")+"'");
			if(rs.next()){
				String[] itemidss = itemids.split(",");
				for(int i=0;i<itemidss.length;i++){
					String itemid = itemidss[i];
					if(itemid.length()==5){
						bean.set(itemid, rs.getString(itemid));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		    try {
		        
		        if(rs!=null) {
                    rs.close();
                }
            } catch (SQLException e) {
               e.printStackTrace();
            }
		}
		return bean;
	}
	
	/**
	 * 获取兼职单位、部门、岗位转b0110/e0122/e01a1
	 * @param bean
	 * @param itemids
	 * @return
	 */
	private LazyDynaBean getPartOrg(LazyDynaBean bean,String itemids){
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			StringBuffer tmp = new StringBuffer();
			ArrayList itemidlist = new ArrayList(); 
			String[] itemidss = itemids.split(",");
			for(int i=0;i<itemidss.length;i++){
				String itemid = itemidss[i];
				if(itemid.length()==5){
					if("b0110".equals(itemid)&&this.getPart_unit().length()>0){
						tmp.append(this.getPart_unit()+" as b0110");
						itemidlist.add(itemid);
					}else if("e0122".equals(itemid)&&this.getPart_dept().length()>0){
						tmp.append(this.getPart_dept()+" as e0122");
						itemidlist.add(itemid);
					}else if("e01a1".equals(itemid)&&this.getPart_pos().length()>0&&"1".equals(this.getPart_occupy_quota())){
						tmp.append(this.getPart_pos()+" as e01a1");
						itemidlist.add(itemid);
					}
					
				}
			}
			rs = dao.search("select "+tmp.toString()+" from "+bean.get("nbase")+this.getPart_setid()+" where A0100='"+bean.get("a0100")+"' and i9999='"+bean.get("i9999")+"'");
			if(rs.next()){
				for(int i=0;i<itemidlist.size();i++){
					String itemid = (String)itemidlist.get(i);
					bean.set(itemid, rs.getString(itemid));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
		    try {
		        
		        if(rs!=null) {
                    rs.close();
                }
            } catch (SQLException e) {
               e.printStackTrace();
            }
		}
		return bean;
	}
	
	/**
	 * 传送人员变动信息，页面中涉及到的指标（注意：（1）bean中key值一律必须小写（2）list中多个bean时每个bean的key个数必须都是一样的)
	 * @param beanList （参数：LazyDynaBean的List LazyDynaBean中的键值:nbase,a0100,objecttype,addflag,ispart(以上为bean中必填字段，但为兼职修改时i9999必填),b0110,e0122,a01a1（以上三字段可能没有）+界面中其他指标）
	 * 对于人事异动的移库修改操作bean的nbase值为原库前缀
	 */
	public void execDate2TmpTable(List beanList){
		
		try {
			ContentDAO dao = new ContentDAO(conn);
			ArrayList values = new ArrayList();
			values.add(this.userView.getUserName());
			dao.delete("delete from "+this.getTmpTableName()+" where curruser=?", values);
			log.debug("delete from "+this.getTmpTableName()+" where curruser="+this.userView.getUserName());
			if(beanList==null||beanList.size()==0) {
                return;
            }
			values.removeAll(values);
			Map parta0100Map= new HashMap();//用于获取新增兼职信息记录的人员的其他临时表必要指标值
			Map editGetPartRecMap = new HashMap();//<dbpre+a0100,bean>一个人的非兼职信息修改，编制控制时可能引起兼职单位的条件超编，要获取兼职作为的记录，同时过滤到传递过来的兼职记录
			Map excludPartRecMap = new HashMap();//<dbpre+a0100,StringBffer<i9999,i9999...>>穿过来的兼职记录，取原有库兼职记录需要排除掉
			Map allRecMap = new HashMap();
			for(int i=beanList.size()-1;i>=0;i--){
				LazyDynaBean bean = (LazyDynaBean)beanList.get(i);
				//处理兼职任免标识变化的问题 wangrd 2014-03-13
                if (bean.get("ispart") != null && ("1".equals((String) bean.get("ispart")))
                           && (!"".equals(this.getPart_appoint()))) {
                    String appointValue = (String) bean.get(this.getPart_appoint());
                    if (appointValue == null) {
                        appointValue = "1";
                    }
                    if ("1".equals(appointValue)) {// 免 不占编制 。
                        bean.set("b0110", ""); // 置为空 不占编
                        bean.set("e0122", "");
                        bean.set("e01a1", "");
                        String addflag = (String) bean.get("addflag");
                        if ((addflag != null) && ("1".equals(addflag))) {
                            bean.set("ispart", "0");
                        }
                    } else if ("0".equals(appointValue)) {// 任 相当于新增
                        bean.set("addflag", "1");
                    }
                }    
          
				RecordVo vo = new RecordVo(this.getTmpTableName());
				vo.setString("curruser", this.userView.getUserName());
				vo.setInt("id", i+1);
				vo.setString("nbase", (String)bean.get("nbase"));
				vo.setString("a0100", (String)bean.get("a0100"));
				vo.setInt("objecttype", Integer.parseInt((String)bean.get("objecttype")));
				vo.setInt("addflag", Integer.parseInt((String)bean.get("addflag")));
				if(bean.get("ispart")==null||((String)bean.get("ispart")).length()==0) {
                    bean.set("ispart", "0");
                }
				vo.setInt("ispart", Integer.parseInt((String)bean.get("ispart")));
				Object _i9999= bean.get("i9999");
				if("1".equals(bean.get("ispart"))){
					if("0".equals(bean.get("addflag"))){
						try{
							vo.setInt("i9999", Integer.parseInt((String)_i9999));
						}catch(Exception e){
							e.printStackTrace();
						}
					}else{
						String nbase = vo.getString("nbase");
						String a0100 = vo.getString("a0100");
						if(parta0100Map.containsKey(nbase)){
							ArrayList a0100list = (ArrayList)parta0100Map.get(nbase);
							if(!a0100list.contains(a0100)) {
                                a0100list.add(a0100);
                            }
						}else{
							ArrayList a0100list =new ArrayList();
							a0100list.add(a0100);
							parta0100Map.put(nbase, a0100list);
						}
					}
				}

				for(int n=this.getRegulationFieldList().size()-1;n>=0;n--){
					FieldItem item=(FieldItem)getRegulationFieldList().get(n);
					String itemtype = item.getItemtype();
					String itemid = item.getItemid();
					Object obj = bean.get(itemid.toLowerCase());
					if(obj!=null&& obj instanceof String){
						if("N".equals(itemtype)){
							vo.setInt(itemid, Integer.parseInt((String)obj));
						}else if("D".equals(itemtype)){
							vo.setDate(itemid, ((String)obj).replaceAll("\\.", "-"));
						}else{
							vo.setString(itemid, (String)obj);
						}
					}
				}
				/*if(0==vo.getInt("addflag")&&(vo.getString("b0110")==null||vo.getString("b0110").length()==0||vo.getString("e0122")==null||vo.getString("e0122").length()==0)){
					String [] org=this.getOrg(vo.getString("a0100"), vo.getString("nbase")+"A01");
					if(vo.getString("b0110")==null||vo.getString("b0110").length()==0)
						vo.setString("b0110", org[0]);
					if(vo.getString("e0122")==null||vo.getString("e0122").length()==0)
						vo.setString("e0122", org[1]);
				}*/
				if(vo.getString("nbase")==null||vo.getString("a0100")==null||vo.getString("b0110")==null||vo.getString("b0110").length()==0||(vo.getInt("addflag")==0&&vo.getInt("ispart")==1&&vo.getString("i9999")==null)){
					log.debug("人员编制条件统计必填项属性有空值-------》"+vo.toString());
				}
				values.add(vo);
				
				if(this.needgetPartRec()){
					allRecMap.put((String)bean.get("nbase")+(String)bean.get("a0100"), bean);
					//一个人的非兼职信息修改，编制控制时可能引起兼职单位的条件超编，要获取兼职作为的记录，同时过滤到传递过来的兼职记录
					if("0".equals(bean.get("addflag"))){
						if("1".equals(bean.get("ispart"))){
							StringBuffer i9999s = (StringBuffer)excludPartRecMap.get((String)bean.get("nbase")+(String)bean.get("a0100"));
							if(i9999s!=null){
								try{
									Integer.parseInt((String)bean.get("i9999"));
									i9999s.append(","+bean.get("i9999"));
								}catch(Exception e){}
							}else{
								try{
									Integer.parseInt((String)bean.get("i9999"));
									i9999s = new StringBuffer();
									i9999s.append(bean.get("i9999"));
									excludPartRecMap.put((String)bean.get("nbase")+(String)bean.get("a0100"), i9999s);
								}catch(Exception e){}
								
							}
						}else{
							editGetPartRecMap.put((String)bean.get("nbase")+(String)bean.get("a0100"), bean);
						}
					}else if("1".equals(bean.get("ispart"))){
						StringBuffer i9999s = (StringBuffer)excludPartRecMap.get((String)bean.get("nbase")+(String)bean.get("a0100"));
						if(i9999s!=null){
							try{
								Integer.parseInt((String)bean.get("i9999"));
								i9999s.append(","+bean.get("i9999"));
							}catch(Exception e){}
						}else{
							try{
								Integer.parseInt((String)bean.get("i9999"));
								i9999s = new StringBuffer();
								i9999s.append(bean.get("i9999"));
								excludPartRecMap.put((String)bean.get("nbase")+(String)bean.get("a0100"), i9999s);
							}catch(Exception e){}
							
						}
					}
				}
			}
			int id=beanList.size();
			//一个人的非兼职信息修改，编制控制时可能引起兼职单位的条件超编，要获取兼职作为的记录，同时过滤到传递过来的兼职记录
			if(this.needgetPartRec()){
				for(Iterator i = excludPartRecMap.keySet().iterator();i.hasNext();){
					String key= (String)i.next();
					if(!editGetPartRecMap.containsKey(key)){
						LazyDynaBean bean = (LazyDynaBean)allRecMap.get(key);
						bean = this.getMainBean(key, bean);
						
						RecordVo vo = new RecordVo(this.getTmpTableName());
						vo.setString("curruser", this.userView.getUserName());
						vo.setInt("id", ++id);
						vo.setString("nbase", (String)bean.get("nbase"));
						vo.setString("a0100", (String)bean.get("a0100"));
						vo.setInt("objecttype", Integer.parseInt((String)bean.get("objecttype")));
						vo.setInt("addflag", 0);
						vo.setInt("ispart", 0);

						for(int n=this.getRegulationFieldList().size()-1;n>=0;n--){
							FieldItem item=(FieldItem)getRegulationFieldList().get(n);
							String itemtype = item.getItemtype();
							String itemid = item.getItemid();
							Object obj = bean.get(itemid.toLowerCase());
							if(obj!=null&& obj instanceof String){
								if("N".equals(itemtype)){
									vo.setInt(itemid, Integer.parseInt((String)obj));
								}else if("D".equals(itemtype)){
									vo.setDate(itemid, ((String)obj).replaceAll("\\.", "-"));
								}else{
									vo.setString(itemid, (String)obj);
								}
							}
						}
						values.add(vo);
						
						editGetPartRecMap.put(key, bean);
					}
				}
				this.editGetPartRec(values, editGetPartRecMap, excludPartRecMap,id);
			}
			
			dao.addValueObject(values);
			
			Map setMap = new HashMap();//存储从实际库中提取到其他临时表涉及到的指标值，分子集存贮
			if(this.doscan){//条件统计时
				//调用端传递的只是其页面中涉及到的指标，对于修改记录还需要业务类中从实际库中提取到其他临时表涉及到的指标值
				LazyDynaBean bean = (LazyDynaBean)beanList.get(0);
				Map beanMap = bean.getMap();
				for(int i=this.getRegulationFieldList().size()-1;i>=0;i--){
					FieldItem item = (FieldItem)this.getRegulationFieldList().get(i);
					String itemid =item.getItemid().toLowerCase(); 
					if(!beanMap.containsKey(itemid)){
						String setid = item.getFieldsetid();
						if(setid.startsWith("A")){
							if(setMap.containsKey(setid)){
								List itemlist = (ArrayList)setMap.get(setid);
								itemlist.add(itemid);
							}else{
								ArrayList itemlist = new ArrayList();
								itemlist.add(itemid);
								setMap.put(setid, itemlist);
							}
						}else{
							if(setid.startsWith("K")){
								if(kSetMap.containsKey(setid)){
									List itemlist = (ArrayList)kSetMap.get(setid);
									itemlist.add(itemid);
								}else{
									ArrayList itemlist = new ArrayList();
									itemlist.add(itemid);
									kSetMap.put(setid, itemlist);
								}
							}else{
								if(orgSetMap.containsKey(setid)){
									List itemlist = (ArrayList)orgSetMap.get(setid);
									itemlist.add(itemid);
								}else{
									ArrayList itemlist = new ArrayList();
									itemlist.add(itemid);
									orgSetMap.put(setid, itemlist);
								}
							}
						}
					}
						
				}
				//兼职新增记录原有表指标值
				if(parta0100Map.keySet().size()>0&&setMap.keySet().size()>0){
					this.initPartAddData(parta0100Map, setMap);
				}
			}else{//值获取获取B0110,E0122,E01A1指标值
				ArrayList itemlist =  new ArrayList();
				LazyDynaBean bean = (LazyDynaBean)beanList.get(0);
				Map beanMap = bean.getMap();
				
				// 将 if 条件改为 beanmap不包含 itemid guodd 2014-22-15
				if(!beanMap.containsKey("b0110")){
					itemlist.add("b0110");
				}
				if(!beanMap.containsKey("e0122")){
					itemlist.add("e0122");
				}
				if(!beanMap.containsKey("e01a1")){
					itemlist.add("e01a1");
				}
				if(itemlist.size()>0) {
                    setMap.put("A01", itemlist);
                }
			}
			if(setMap.keySet().size()>0) {
                this.initTmpTableData(setMap);
            }
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("人员编制条件统计将数据同步到临时表失败------》"+this.userView.getUserName());
		}
		
	}
	
	/**
	 * 调用端传递的只是其页面中涉及到的指标，对于修改记录还需要业务类中从实际库中提取到其他临时表涉及到的指标值
	 * setMap<setid,list<itemid>>
	 */
	private void initTmpTableData(Map setMap){
		StringBuffer sql = new StringBuffer();
		StringBuffer copysql = new StringBuffer();
		sql.append("select nbase,a0100,i9999,ispart from "+this.getTmpTableName()+" where curruser='"+this.userView.getUserName()+"' and addflag=0");
		copysql.append("select a0100 from "+this.getTmpTableName()+" where curruser='"+this.userView.getUserName()+"' and addflag=0 and ispart=0");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			rs = dao.search(sql.toString());
			Map a0100Map = new HashMap();//非兼职修改记录
			Map parta0100Map = new HashMap();//兼职修改记录
			while(rs.next()){
				String nbase = rs.getString("nbase");
				if(0==rs.getInt("ispart")){
					if(a0100Map.containsKey(nbase)){
						List a0100list = (ArrayList)a0100Map.get(nbase);
						a0100list.add(rs.getString("a0100"));
					}else{
						ArrayList a0100list = new ArrayList();
						a0100list.add(rs.getString("a0100"));
						a0100Map.put(nbase, a0100list);
					}
				}else{
					if(parta0100Map.containsKey(nbase)){
						List a0100list = (ArrayList)parta0100Map.get(nbase);
						a0100list.add(rs.getString("a0100")+"`"+rs.getInt("i9999"));
					}else{
						ArrayList a0100list = new ArrayList();
						a0100list.add(rs.getString("a0100")+"`"+rs.getInt("i9999"));
						parta0100Map.put(nbase, a0100list);
					}
				}
			}
			//对Map a0100Map = new HashMap();//非兼职修改记录
			if(a0100Map.keySet().size()>0){
				sql.setLength(0);
				sql.append("select ###A01.a0100 a0100,'###' as nbase");
				StringBuffer fromsql = new StringBuffer(" from ###A01");
				StringBuffer wheresql = new StringBuffer(" where 1=1 ");
				int index = 0;
				ArrayList fieldlist = new ArrayList();
				for(Iterator i=setMap.keySet().iterator();i.hasNext();){
					String setid = (String)i.next();
					List itemlist = (List)setMap.get(setid);
					if(!"A01".equals(setid)) {
                        fromsql.append(" left join ###"+setid+" t"+index+" on ###A01.a0100=t"+index+".a0100");
                    }
					fieldlist.addAll(itemlist);
					for(int n=itemlist.size()-1;n>=0;n--){
						sql.append(","+itemlist.get(n));
					}
					if(!"A01".equalsIgnoreCase(setid)) {
                        wheresql.append(" and (t"+index+".i9999=(select max(tt"+index+".i9999) from ###"+setid+" tt"+index+" where tt"+index+".a0100=t"+index+".a0100) or t"+index+".i9999 is null)");
                    }
					index++;
				}
				sql.append(fromsql).append(wheresql);
				fromsql.setLength(0);
				index=0;
				for(Iterator i=a0100Map.keySet().iterator();i.hasNext();){
					String nbase = (String)i.next();
					if(index!=0){
						fromsql.append("union all");
					}
					fromsql.append(sql.toString().replaceAll("###", nbase)+" and "+nbase+"A01.a0100 in("+copysql.toString()+" and nbase='"+nbase+"')");
					//ArrayList a0100list = (ArrayList)a0100Map.get(nbase);
					//fromsql.append(sql.toString().replaceAll("###", nbase)+" and "+nbase+"A01.a0100 in('"+a0100list.toString().substring(1, a0100list.toString().length()-1).replaceAll(", ", "','")+"')");
					index++;
				}
				rs = dao.search(fromsql.toString());
				index = fieldlist.size();
				ArrayList values = new ArrayList();
				while(rs.next()){
					ArrayList vs = new ArrayList();
					for(int i=0;i<index;i++){
						String itemid = (String)fieldlist.get(i);
						if(itemid.equalsIgnoreCase(this.getPart_appoint())) {
                            continue;
                        }
						FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
						String itemtype = item.getItemtype();
						if("N".equals(itemtype)){
							vs.add(new Integer(rs.getInt(itemid)));
						}else if("D".equals(itemtype)){
							vs.add(rs.getDate(itemid));
						}else {
							vs.add(rs.getString(itemid));
						}
					}
					vs.add(rs.getString("a0100"));
					vs.add(rs.getString("nbase"));
					values.add(vs);
				}
				sql.setLength(0);
				sql.append("update "+this.getTmpTableName()+" set ");
				boolean apcomma=false;
				for(int i=0;i<index;i++){
					if(((String)fieldlist.get(i)).equalsIgnoreCase(this.getPart_appoint())) {
                        continue;
                    }
					if(apcomma) {
                        sql.append(",");
                    }
					sql.append((String)fieldlist.get(i)+"=?");
					apcomma=true;
				}
				sql.append(" where a0100=? and nbase=? and curruser='"+this.userView.getUserName()+"' and addflag=0 and ispart=0");
				if(apcomma) {
                    dao.batchUpdate(sql.toString(), values);
                }
			}
			//Map parta0100Map = new HashMap();//兼职修改记录
			if(parta0100Map.keySet().size()>0&&("1".equals(this.getPart_takeup_quota())||"1".equals(this.getPart_occupy_quota())&&this.getPart_appoint().length()>0)){
				if(this.getPart_setid().length()>0){
					sql.setLength(0);
					sql.append("select ###"+this.getPart_setid()+".a0100 a0100,###"+this.getPart_setid()+".i9999 i9999,'###' as nbase");
					StringBuffer fromsql = new StringBuffer(" from ###"+this.getPart_setid());
					StringBuffer wheresql = new StringBuffer(" where 1=1 ");
					StringBuffer updatesql = new StringBuffer("update "+this.getTmpTableName()+" set ");
					int index = 0;
					ArrayList fieldlist = new ArrayList();
					for(Iterator i=setMap.keySet().iterator();i.hasNext();){
						String setid = (String)i.next();
						List itemlist = (List)setMap.get(setid);
						if(!this.getPart_setid().equals(setid)) {
                            fromsql.append(" left join ###"+setid+" t"+index+" on ###"+this.getPart_setid()+".a0100=t"+index+".a0100");
                        }
						fieldlist.addAll(itemlist);
						for(int n=itemlist.size()-1;n>=0;n--){
							String itemid = (String)itemlist.get(n);
							if("b0110".equalsIgnoreCase(itemid)){
								if(this.getPart_unit().length()>0&&"1".equals(this.getPart_occupy_quota())) {
                                    sql.append(","+getPart_unit()+" as b0110");
                                }
							}else if("e0122".equalsIgnoreCase(itemid)){
								if(this.getPart_dept().length()>0&&"1".equals(this.getPart_occupy_quota())) {
                                    sql.append(","+getPart_dept()+" as e0122");
                                }
							}else if("e01a1".equalsIgnoreCase(itemid)){
								if(this.getPart_pos().length()>0&&"1".equals(this.getPart_takeup_quota())) {
                                    sql.append(","+this.getPart_pos()+" as e01a1");
                                }
							}else{
								sql.append(","+itemid);
							}
						}
						if(!"A01".equalsIgnoreCase(setid)&&!this.getPart_setid().equals(setid)) {
                            wheresql.append(" and (t"+index+".i9999=(select max(tt"+index+".i9999) from ###"+setid+" tt"+index+" where tt"+index+".a0100=t"+index+".a0100) or t"+index+".i9999 is null)");
                        }
						index++;
					}
					sql.append(fromsql).append(wheresql);
					ArrayList values = new ArrayList();
					index=fieldlist.size();
					for(Iterator i=parta0100Map.keySet().iterator();i.hasNext();){
						String nbase = (String)i.next();
						ArrayList a0100i9999list = (ArrayList)parta0100Map.get(nbase);
						for(int n=0;n<a0100i9999list.size();n++){
							fromsql.setLength(0);
							String a0100i9999 = (String)a0100i9999list.get(n);
							String[] tmps = a0100i9999.split("`");
							String a0100= tmps[0];
							String i9999 = tmps[1];
							fromsql.append(sql.toString().replaceAll("###", nbase)+" and "+nbase+this.getPart_setid()+".a0100 ='"+a0100+"' and "+nbase+this.getPart_setid()+".i9999='"+i9999+"'");
							rs = dao.search(fromsql.toString());
							if(rs.next()){
								ArrayList vs = new ArrayList();
								for(int m=0;m<index;m++){
									String itemid = (String)fieldlist.get(m);
									if("b0110".equalsIgnoreCase(itemid)){
										if(this.getPart_unit().length()>0&&"1".equals(this.getPart_occupy_quota())) {
                                            vs.add(rs.getString(itemid));
                                        }
									}else if("e0122".equalsIgnoreCase(itemid)){
										if(this.getPart_dept().length()>0&&"1".equals(this.getPart_occupy_quota())) {
                                            vs.add(rs.getString(itemid));
                                        }
									}else if("e01a1".equalsIgnoreCase(itemid)){
										if(this.getPart_pos().length()>0&&"1".equals(this.getPart_takeup_quota())) {
                                            vs.add(rs.getString(itemid));
                                        }
									}else{
										FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
										String itemtype = item.getItemtype();
										if("N".equals(itemtype)){
											vs.add(new Integer(rs.getInt(itemid)));
										}else if("D".equals(itemtype)){
											vs.add(rs.getDate(itemid));
										}else {
											vs.add(rs.getString(itemid));
										}
									}
								}
								vs.add(rs.getString("a0100"));
								vs.add(rs.getString("i9999"));
								vs.add(rs.getString("nbase"));
								values.add(vs);
							}
						}
					}
					
					sql.setLength(0);
					sql.append("update "+this.getTmpTableName()+" set ");
					boolean apcomma=false;
					for(int i=0;i<index;i++){
						String itemid = (String)fieldlist.get(i);
						if("b0110".equalsIgnoreCase(itemid)){
							if(this.getPart_unit().length()>0&&"1".equals(this.getPart_occupy_quota())){
								if(apcomma) {
                                    sql.append(",");
                                }
								sql.append(itemid+"=?");
								apcomma=true;
							}
						}else if("e0122".equalsIgnoreCase(itemid)){
							if(this.getPart_dept().length()>0&&"1".equals(this.getPart_occupy_quota())){
								if(apcomma) {
                                    sql.append(",");
                                }
								sql.append(itemid+"=?");
								apcomma=true;
							}
						}else if("e01a1".equalsIgnoreCase(itemid)){
							if(this.getPart_pos().length()>0&&"1".equals(this.getPart_takeup_quota())){
								if(apcomma) {
                                    sql.append(",");
                                }
								sql.append(itemid+"=?");
								apcomma=true;
							}
						}else{
							if(apcomma) {
                                sql.append(",");
                            }
							sql.append(itemid+"=?");
							apcomma=true;
						}
					}
					sql.append(" where a0100=? and i9999=? and nbase=? and curruser='"+this.userView.getUserName()+"' and ispart=1 and addflag=0");
					if(apcomma) {
                        dao.batchUpdate(sql.toString(), values);
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
	 * 兼职新增记录原有表指标值
	 * @param parta0100Map
	 * @param setMap
	 */
	private void initPartAddData(Map parta0100Map,Map setMap){
		RowSet rs=null;
		try{
			ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql = new StringBuffer();
		//Map parta0100Map = new HashMap();//兼职修改记录
		if(parta0100Map.keySet().size()>0&&("1".equals(this.getPart_takeup_quota())||"1".equals(this.getPart_occupy_quota())&&this.getPart_appoint().length()>0)){
			if(this.getPart_setid().length()>0){
				sql.setLength(0);
				sql.append("select ###A01.a0100 a0100,'###' as nbase");
				StringBuffer fromsql = new StringBuffer(" from ###A01");
				StringBuffer wheresql = new StringBuffer(" where 1=1 ");
				int index = 0;
				ArrayList fieldlist = new ArrayList();
				for(Iterator i=setMap.keySet().iterator();i.hasNext();){
					String setid = (String)i.next();
					List itemlist = (List)setMap.get(setid);
					if(!"A01".equals(setid)) {
                        fromsql.append(" left join ###"+setid+" t"+index+" on ###A01.a0100=t"+index+".a0100");
                    }
					fieldlist.addAll(itemlist);
					for(int n=itemlist.size()-1;n>=0;n--){
						String itemid = (String)itemlist.get(n);
						if("b0110".equalsIgnoreCase(itemid)){
							//if(this.getPart_unit().length()>0&&"1".equals(this.getPart_occupy_quota()))
								//sql.append(","+getPart_unit()+" as b0110");
						}else if("e0122".equalsIgnoreCase(itemid)){
							//if(this.getPart_dept().length()>0&&"1".equals(this.getPart_occupy_quota()))
								//sql.append(","+getPart_dept()+" as e0122");
						}else if("e01a1".equalsIgnoreCase(itemid)){
							//if(this.getPart_pos().length()>0&&"1".equals(this.getPart_takeup_quota()))
								//sql.append(","+this.getPart_pos()+" as e01a1");
						}else{
							sql.append(","+itemid);
						}
					}
					if(!"A01".equalsIgnoreCase(setid)) {
                        wheresql.append(" and (t"+index+".i9999=(select max(tt"+index+".i9999) from ###"+setid+" tt"+index+" where tt"+index+".a0100=t"+index+".a0100) or t"+index+".i9999 is null)");
                    }
					index++;
				}
				sql.append(fromsql).append(wheresql);
				ArrayList values = new ArrayList();
				index=fieldlist.size();
				for(Iterator i=parta0100Map.keySet().iterator();i.hasNext();){
					String nbase = (String)i.next();
					ArrayList a0100list = (ArrayList)parta0100Map.get(nbase);
					for(int n=0;n<a0100list.size();n++){
						fromsql.setLength(0);
						String a0100 = (String)a0100list.get(n);
						fromsql.append(sql.toString().replaceAll("###", nbase)+" and "+nbase+"A01.a0100 ='"+a0100+"'");
						rs = dao.search(fromsql.toString());
						if(rs.next()){
							ArrayList vs = new ArrayList();
							for(int m=0;m<index;m++){
								String itemid = (String)fieldlist.get(m);
								if("b0110".equalsIgnoreCase(itemid)){
									/*if(this.getPart_unit().length()>0&&"1".equals(this.getPart_occupy_quota()))
										vs.add(rs.getString(itemid));*/
								}else if("e0122".equalsIgnoreCase(itemid)){
									/*if(this.getPart_dept().length()>0&&"1".equals(this.getPart_occupy_quota()))
										vs.add(rs.getString(itemid));*/
								}else if("e01a1".equalsIgnoreCase(itemid)){
									/*if(this.getPart_pos().length()>0&&"1".equals(this.getPart_takeup_quota()))
										vs.add(rs.getString(itemid));*/
								}else{
									FieldItem item = DataDictionary.getFieldItem(itemid.toLowerCase());
									String itemtype = item.getItemtype();
									if("N".equals(itemtype)){
										vs.add(new Integer(rs.getInt(itemid)));
									}else if("D".equals(itemtype)){
										vs.add(rs.getDate(itemid));
									}else {
										vs.add(rs.getString(itemid));
									}
								}
							}
							vs.add(rs.getString("a0100"));
							vs.add(rs.getString("nbase"));
							values.add(vs);
						}
					}
				}
				
				sql.setLength(0);
				sql.append("update "+this.getTmpTableName()+" set ");
				boolean apcomma=false;
				for(int i=0;i<index;i++){
					
					String itemid = (String)fieldlist.get(i);
					if("b0110".equalsIgnoreCase(itemid)){
						/*if(this.getPart_unit().length()>0&&"1".equals(this.getPart_occupy_quota()))
							sql.append(itemid+"=?");*/
					}else if("e0122".equalsIgnoreCase(itemid)){
						/*if(this.getPart_dept().length()>0&&"1".equals(this.getPart_occupy_quota()))
							sql.append(itemid+"=?");*/
					}else if("e01a1".equalsIgnoreCase(itemid)){
						/*if(this.getPart_pos().length()>0&&"1".equals(this.getPart_takeup_quota()))
							sql.append(itemid+"=?");*/
					}else{
						if(apcomma) {
                            sql.append(",");
                        }
						sql.append(itemid+"=?");
						apcomma=true;
					}
				}
				sql.append(" where a0100=? and nbase=? and curruser='"+this.userView.getUserName()+"' and ispart=1 and addflag=1");
				if(apcomma) {
                    dao.batchUpdate(sql.toString(), values);
                }
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 人员移库判断为新增操作获取编制临时表必填信息
	 * @param nbaseA0100List<resourceNbase`a0100`destinationNbase> 人员库`a0100`目标库
	 * @return list<LazyDynaBean>对应execDate2TmpTable方法的参数
	 */
	public ArrayList getMoveAddPersonData(List nbaseA0100List){
		ArrayList infoList = new ArrayList();
	    StringBuffer sql = new StringBuffer();
	    ContentDAO dao = new ContentDAO(conn);
	    
	    RowSet rs = null;
	    // zgd 2014-8-14 缺陷3755 this.part_appoint为null
	    if(this.part_appoint==null){
	    	this.part_appoint = "";
	    }
        FieldItem fItem2 = DataDictionary.getFieldItem(this.part_appoint);
        boolean isPartTime = true;
        if(!"true".equals(this.part_flag))//未启用兼职
        {
            isPartTime = false;
        } else if(!("1".equals(part_occupy_quota) || "1".equals(part_occupy_quota)))//单位不占编制
        {
            isPartTime = false;
        } else if(fItem2 == null || (!"1".equals(fItem2.getUseflag()))) //任免指标未指定或任免指标为构库
        {
            isPartTime = false;
        }
        
	    try{
		if(this.doscan){//才需要搞到条件指标
			Map setMap = new HashMap();
			ArrayList itemList = this.getRegulationFieldList();
			for(int i=0;i<itemList.size();i++){
			    FieldItem item = (FieldItem)itemList.get(i);
			    String setid = item.getFieldsetid().toLowerCase();
			    if(setid.startsWith("a")){
				    if(setMap.containsKey(setid)){
				        String items = setMap.get(setid).toString();
				        items += item.getItemid()+",";
				        setMap.put(setid, items);
				    }else{
				        setMap.put(setid,item.getItemid()+",");
				    }
			    }
			}
			
			for(int i=0;i<nbaseA0100List.size();i++){
			    String[] info = nbaseA0100List.get(i).toString().split("`");
			    LazyDynaBean ldb = new LazyDynaBean();
			    Iterator ite = setMap.entrySet().iterator();
			    while(ite.hasNext()){
	                Entry  en = (Entry)ite.next();
	                sql.setLength(0);
	                sql.append("select ");
	                sql.append(en.getValue()+" a0100 from ");
	                sql.append(info[0]+en.getKey()+" A where a0100='"+info[1]+"' ");
	                if(!"a01".equals(en.getKey())){
	                    sql.append(" and i9999=(select max(i9999) from "+info[0]+en.getKey()+" where a0100=A.a0100 ) ");
	                }
	                rs = dao.search(sql.toString());
	                if(rs.next()){
	                    String[] items = en.getValue().toString().split(",");
	                    for(int f=0;f<items.length;f++){
	                    	Object obj =  rs.getObject(items[f]);
	                    	if(obj!=null){
	                    		ldb.set(items[f], String.valueOf(obj));
	                    	}
	                    }
	                }
	                
	            }
			    
			    ldb.set("nbase", info[2]);
                ldb.set("a0100", info[1]);
                //ldb.set("i9999", "");
                ldb.set("addflag", "1");
                ldb.set("objecttype", "1");
                ldb.set("ispart", "0");
                if(isPartTime) {
                    ldb.set(part_appoint.toLowerCase(), "");
                }
                infoList.add(ldb);
			    
			    if(isPartTime){
	                sql.setLength(0);
	                sql.append(" select ");
	                if(part_unit.length()>0) {
                        sql.append(part_unit+",");
                    }
	                if(part_dept.length()>0) {
                        sql.append(part_dept+",");
                    }
	                if(part_pos.length()>0) {
                        sql.append(part_pos+",");
                    }
	                sql.append(" i9999 from "+info[0]+part_setid+" where a0100='"+info[1]+"' and "+part_appoint+"=0 ");
	                rs = dao.search(sql.toString());
	                while(rs.next()){
	                    LazyDynaBean partLdb = new LazyDynaBean();
	                    
	                    Map valueM = ldb.getMap();
	                    Iterator vmite = valueM.keySet().iterator();
	                    while(vmite.hasNext()){
	                    	String key = (String)vmite.next();
	                    	String vvalue =(String)ldb.get(key);
	                        partLdb.set(key, vvalue);
	                    }
    	                partLdb.set("b0110", "");
    	                partLdb.set("e0122", "");
    	                partLdb.set("e01a1", "");
                        if(part_unit.length()>0) {
                            partLdb.set("b0110", rs.getString(part_unit));
                        }
                        if(part_dept.length()>0) {
                            partLdb.set("e0122", rs.getString(part_dept));
                        }
                        if(part_pos.length()>0 && "1".equals(part_takeup_quota)) {
                            partLdb.set("e01a1", rs.getString(part_pos));
                        }
                        //ldb.set("i9999", rs.getObject("i9999"));
                        partLdb.set("addflag", "1");
                        partLdb.set("objecttype", "1");
                        partLdb.set("ispart", "1");
                        partLdb.set(part_appoint.toLowerCase(),"0");
                        infoList.add(partLdb);
	                }
	            }
			}
			
			
		}else{//搞到b0100、e0122、e01a1指标值即可
			 for(int i=0;i<nbaseA0100List.size();i++){
			     String[] info = nbaseA0100List.get(i).toString().split("`");
			     sql.setLength(0);
			     sql.append(" select b0110,e0122,e01a1 from "+info[0]+"A01 where a0100='"+info[1]+"'");
			     rs = dao.search(sql.toString());
			     if(rs.next()){//nbase,a0100,objecttype,addflag,b0110,e0122
			         LazyDynaBean ldb = new LazyDynaBean();
			         ldb.set("nbase", info[2]);
			         ldb.set("a0100", info[1]);
			         ldb.set("b0110", rs.getString("b0110"));
			         ldb.set("e0122", rs.getString("e0122"));
			         ldb.set("e01a1", rs.getString("e01a1"));
			         //ldb.set("i9999", "");
			         ldb.set("addflag", "1");
			         ldb.set("objecttype", "1");
			         ldb.set("ispart", "0");
			         if(isPartTime) {
                         ldb.set(part_appoint.toLowerCase(), "");
                     }
			         infoList.add(ldb);
			         
			     }
			     
			     sql.setLength(0);
			     if(isPartTime){
			         sql.append(" select ");
			         if(part_unit.length()>0) {
                         sql.append(part_unit+",");
                     }
			         if(part_dept.length()>0) {
                         sql.append(part_dept+",");
                     }
			         if(part_pos.length()>0) {
                         sql.append(part_pos+",");
                     }
			         sql.append(" i9999 from "+info[0]+part_setid+" where a0100='"+info[1]+"' and "+part_appoint+"=0 ");
			         rs = dao.search(sql.toString());
			         while(rs.next()){
			             LazyDynaBean ldb = new LazyDynaBean();
			             ldb.set("nbase", info[2]);
			             ldb.set("a0100", info[1]);
			             ldb.set("b0110", "");
			             ldb.set("e0122", "");
			             ldb.set("e01a1", "");
			             if(part_unit.length()>0) {
                             ldb.set("b0110", rs.getString(part_unit));
                         }
	                     if(part_dept.length()>0) {
                             ldb.set("e0122", rs.getString(part_dept));
                         }
	                     if(part_pos.length()>0 && "1".equals(part_takeup_quota)) {
                             ldb.set("e01a1", rs.getString(part_pos));
                         }
	                     //ldb.set("i9999", rs.getInt("i9999")+"");
	                     ldb.set("addflag", "1");
	                     ldb.set("objecttype", "1");
	                     ldb.set("ispart", "1");
	                     ldb.set(part_appoint.toLowerCase(),"0");
	                     infoList.add(ldb);
			         }
			     }
			 }
		}
	    }catch(Exception e){
	        e.printStackTrace();
	    }finally{
	        try{
	            if(rs!=null) {
                    rs.close();
                }
	        }catch(Exception e){
	            
	        }
	    }
		return infoList;
	}
	//传送人员变动信息（参数1：用户。参数2：实现编制临时数据写入接口的类）。
	//数据量大，使用RecordVo效率不高时可用此方法。人员怎么加到编制临时表，调用编制的那个模块实现。
	
	/**是否超编（同步处理）
	 * param dataLink 是否联动岗位编制子集实有人数、兼职人数指标值 0否|1是
	 * return 返回值有二类：不超编(ok)、超编(超编信息)。
	*/
	
	 public String isOverstaffs(){
	     
	     synchronized(ScanFormationBo.class){
        		//启用编制控制指标
        		String ctrlitemid =pos.getValue(PosparameXML.AMOUNTS,"ctrlitemid");//1是|2否
        		String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");//控制部门编制 0不|1控制
        		
        		StringBuffer mess=new StringBuffer("、ok");
        		ResultSet rs = null;
        		try {
        			//-------------------岗位编制控制----------------------------
        			if("true".equals(zwvalid)&&this.isPosChange){
	        			List overPosList = this.checkPosOrPartWorkOut();
	        			if(overPosList!=null&&overPosList.size()>0){
	        				mess.setLength(0);
	        				for(int i=overPosList.size()-1;i>=0;i--){
	        					String posid = (String)overPosList.get(i);
	        					String unitdesc=AdminCode.getCodeName("@K",posid);
								mess.append("、"+unitdesc);
	        				}
	        				mess.append(ResourceFactory.getProperty("workdiary.message.person.excess")+"!");
	        				return mess.substring(1);
	        			}
        			}
        			//------------------单位部门编制控制---------------------------
        			//获取当前临时表人员涉及到的部门、单位并且是启用机构内的
        			if(orgscan){
	        			ContentDAO dao = new ContentDAO(this.conn);
	        			int csize = clist.size();
	        			for(int i=0;i<csize;i++){
	        				String flag = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"flag");
	        				String statics = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"static");
	        				statics=statics!=null&&statics.trim().length()>0?statics:"new";
	        				String method = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"method");
	        				String cond = pos.getTextValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString());
	        				if("1".equals(flag) && /*statics!=null&&statics.trim().length()>0&&*/(("1".equals(method)&&(cond!=null&&cond.length()>0))||!"1".equals(method))){
	        					
	        					StringBuffer sql = new StringBuffer();
	        					ArrayList orglist = new ArrayList();
	                			ArrayList orgidlist = new ArrayList();
	                			//获取临时表涉及到的最深orgid
	                			RecordVo vo = new RecordVo("LExpr");
	                			this.initOrgFieldsValue(kSetMap, "e01a1");
	                			if("1".equals(ctrl_type)){
	                				this.initOrgFieldsValue(orgSetMap, "e0122");
	                				sql.append("select e0122 from "+this.getTmpTableName()+" where nbase in('"+dbs.replaceAll(",", "','")+"') and (e0122 is not null");
	                				if(com.hrms.hjsj.utils.Sql_switcher.searchDbServer()==1) {
                                        sql.append(" and e0122<>''");
                                    }
	                				sql.append(") ");
	                				if(!"new".equals(statics)){
	                					vo.setInt("id",Integer.parseInt(statics));
	                					try{
	                						vo = dao.findByPrimaryKey(vo);
	                					}catch(Exception e){
	                						throw com.hrms.struts.exception.GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("org.static.item.setup.static")+(i+1)+ResourceFactory.getProperty("org.static.item.setup.static.m")+"\n"));
	                					}
	                					FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
	                							"Usr", false, false, true, 1, "su");
	                					String wherestr = factor.getSingleTableSqlExpression(this.getTmpTableName());
	                					sql.append(" and "+wherestr);
	                				}
	                				sql.append(" and curruser='"+this.userView.getUserName()+"'");
	                				sql.append(" group by e0122");
	                				rs = dao.search(sql.toString());
	                				
	                				while (rs.next()){
	                					orgidlist.add(rs.getString("e0122"));
	                				}
	                			}
	                			{
	                				this.initOrgFieldsValue(orgSetMap, "b0110");
	        	        			sql.setLength(0);
	        	        			sql.append("select b0110 from "+this.getTmpTableName()+" where nbase in('"+dbs.replaceAll(",", "','")+"') ");
	        	        			if("1".equals(ctrl_type)){
	        	        				sql.append("and (e0122 is null or e0122='') ");
	        	        			}
	        	        			if(!"new".equals(statics)){
	        	        				if(!"1".equals(ctrl_type)){
		                					vo.setInt("id",Integer.parseInt(statics));
		                					try{
		                						vo = dao.findByPrimaryKey(vo);
		                					}catch(Exception e){
		                						throw com.hrms.struts.exception.GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("org.static.item.setup.static")+(i+1)+ResourceFactory.getProperty("org.static.item.setup.static.m")+"\n"));
		                					}
		        	        			}
	                					FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
	                							"Usr", false, false, true, 1, "su");
	                					String wherestr = factor.getSingleTableSqlExpression(this.getTmpTableName());
	                					sql.append(" and "+wherestr);
	                				}
	        	        			sql.append(" and curruser='"+this.userView.getUserName()+"'");
	        	        			sql.append(" group by b0110");
	        	        			rs = dao.search(sql.toString());
	        	        			while (rs.next()){
	        	        				orgidlist.add(rs.getString("b0110"));
	        	        			}
	                			}
	                			ArrayList mxdeeplist = new ArrayList();
	                			for(int m=0;m<orgidlist.size();m++){
	                				String orgid = (String)orgidlist.get(m);
	                				if(orgid==null) {
                                        continue;//zhaogd 2013-12-6 判断是否为null，避免null指针错误。
                                    }
                					boolean fflag = true;
                					for(int n=0;n<mxdeeplist.size();n++){
                						String mxdeeporgid = (String)mxdeeplist.get(n);
                						if(mxdeeporgid.startsWith(orgid)){//wangrd 2014-03-14//if(mxdeeporgid.indexOf(orgid)!=-1){
                							fflag=false;
                							break;
                							
                						}else if(orgid.startsWith(mxdeeporgid)){
                							mxdeeplist.remove(n);
                							mxdeeplist.add(n, orgid);
                							fflag=false;
                							break;
                						}
                					}
                					if(fflag){
                						mxdeeplist.add(orgid);
                					}
	                			}
	                			//分析出所有当前临时表涉及的数据需要哪些机构的编制（所有上级节点都要检查一遍）
	                			sql.setLength(0);
	                			
	                			sql.append("select codesetid,codeitemid from organization where (1=2 ");
	                			for(int m=0;m<mxdeeplist.size();m++){
	                				String mxdeeporgid = (String)mxdeeplist.get(m);
	                				sql.append(" or '"+mxdeeporgid+"' like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%'");
	                			}
	                			
	                			sql.append(")");
	                			if(ctrlitemid!=null&&ctrlitemid.length()>0){
	            					FieldItem item = DataDictionary.getFieldItem(ctrlitemid);
	            					if(item!=null&&!"0".equals(item.getUseflag())) {
	            						
	            						sql.append(" and codeitemid in(select b0110 from "+this.ps_set+" t1");
	            						if(!"b01".equalsIgnoreCase(this.ps_set)){
	            							//zhangcq 2016-4-12 "启用编制控制指标"是主集中的指标，而编制子集不是主集
	            							sql.append(" where EXISTS(SELECT 1 FROM B01");
	            							sql.append(" WHERE "+ctrlitemid+"='1'");
	            							sql.append("AND B0110=t1.B0110");
	            							sql.append(")");
	            							//取编制子集最近一条记录（i9999最大的）
	            							sql.append(" and i9999=(select max(i9999) from "+this.ps_set+" t2 where t1.b0110=t2.b0110))");			
	            						}else{
	            							sql.append(" where "+ctrlitemid+"='1'");
	            							sql.append(" and i9999=(select max(i9999) from "+this.ps_set+" t2 where t1.b0110=t2.b0110))");
	            						}
	            					}
	            				}
	            				rs = dao.search(sql.toString());
	            				while (rs.next()){
	            					orglist.add(rs.getString("codesetid")+rs.getString("codeitemid"));
	            				}
	            				
	        					String ctrlorg=pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"ctrlorg");
	        					String nextorg=pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"nextorg");
	        					List ctrlorglist= null;
	        					if("1".equals(nextorg)){
	        						String[] ctrlorgs = ctrlorg.split(",");
	        						ctrlorglist= Arrays.asList(ctrlorgs);
	        					}
	        					ctrlorg=","+ctrlorg+",";
	        					ArrayList doscanorglist = new ArrayList();
	        					//获取与归属单位的交集
	        					if(ctrlorg.length()>2){
	        						for(int n=orglist.size()-1;n>=0;n--){
	        							String orgid = (String)orglist.get(n);
	        							if("1".equals(nextorg)){//包含下级机构
	        								for(int m=ctrlorglist.size()-1;m>=0;m--){
	        									if(orgid.substring(2).startsWith(((String)ctrlorglist.get(m)).substring(2))){
	        										doscanorglist.add(orgid);
	        										break;
	        									}
	        								}
	        							}else{
	        								if(ctrlorg.indexOf(","+orgid+",")!=-1){
	        									doscanorglist.add(orgid);
	        								}
	        							}
	        						}
	        					}else{
	        						doscanorglist.addAll(orglist);
	        					}
	        					if(doscanorglist.size()>0){//检查出是否有需要编制检查的机构
	        						String planitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"planitem");
	        						String realitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"realitem");
	        						String message=pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"message");
	        						ArrayList list = this.exprDep(DataDictionary.getFieldSetVo(ps_set), planitem, realitem, statics, dbs, doscanorglist, method, cond, i+1);
	        						if(list.size()>1){
	        							if("、ok".equals(mess.toString())) {
                                            mess.setLength(0);
                                        }
	        							for(int n=0;n<list.size()-1;n++){
	        								String orgid = (String)list.get(n);
	        								String codesetid = orgid.substring(0,2);
	        								String codeitemid = orgid.substring(2);
	        								String unitdesc=AdminCode.getCodeName(codesetid,codeitemid);
	        								mess.append("、"+unitdesc);
	        							}
	        							if(message!=null&&message.length()>0) {
                                            mess.append(message);
                                        } else {
                                            mess.append(list.get(list.size()-1)+ResourceFactory.getProperty("workdiary.message.person.excess")+"!");
                                        }
	        							break;
	        						}
	        					}
	        				}
	        			}
        			}
        		} catch (Exception e) {
        			mess.setLength(1);
        			mess.append(e.toString());
        			e.printStackTrace();
        		}
        		return mess.substring(1);
	     }
	}
	
	 /**
	  * 
	  * @Title: checkPosOrPartWorkOut   
	  * @Description:    检查岗位编制是否超编
	  * @param  
	  * @return void    
	  * @throws
	  */
	private List checkPosOrPartWorkOut(){
	    
	    ArrayList overStaffList = new ArrayList();

	    //不控制岗位编制 return
		if(!"true".equals(this.zwvalid)) {
            return overStaffList;
        }
		
		//没有设置岗位编制参数 return
		RecordVo vo = ConstantParamter.getRealConstantVo("PS_WORKOUT",this.conn);
		if(vo == null || "".equals(vo.getString("str_value"))) {
            return overStaffList;
        }
		
		String value = vo.getString("str_value");
		String posSet = value.substring(0, value.indexOf("|"));
		FieldSet fSet= DataDictionary.getFieldSetVo(posSet);
		//没有设置岗位编制子集  或子集没有构库 return
		if(posSet.length() <0 || fSet == null || (!"1".equals(fSet.getUseflag())) ) {
            return overStaffList;
        }
		
		String ps_worknum = value.substring(value.indexOf("|")+1, value.indexOf(","));
		FieldItem fItem = DataDictionary.getFieldItem(ps_worknum);
		//没有设置岗位定员数指标 或指标没有构库 return
		if(ps_worknum.length()<5 || fItem == null || (!"1".equals(fItem.getUseflag())) ) {
            return overStaffList;
        }
		
		
		boolean isPartTime = true;
		if ((this.part_setid==null)||("".equals(this.part_setid))
		        || (this.part_pos==null)||("".equals(this.part_pos))
		       || (this.part_appoint==null)||("".equals(this.part_appoint))) {// wangrd 2014-01-27 无兼职子集
		    isPartTime = false;	         
		}else{
		
		    fItem = DataDictionary.getFieldItem(this.part_pos);
			FieldItem fItem2 = DataDictionary.getFieldItem(this.part_appoint);
			
			if(!"true".equals(this.part_flag))//未启用兼职
            {
                isPartTime = false;
            } else if(!"1".equals(part_takeup_quota))//岗位不占编制
            {
                isPartTime = false;
            } else if(fItem == null || (!"1".equals(fItem.getUseflag())) || (!"@K".equals(fItem.getCodesetid().toUpperCase())) )//兼职指标为空或为构库或codesetid<>@K
            {
                isPartTime = false;
            } else if(fItem2 == null || (!"1".equals(fItem2.getUseflag()))) //任免指标未指定或任免指标为构库
            {
                isPartTime = false;
            }
		}   
		overStaffList = checkPosStaff(isPartTime,posSet,ps_worknum);
		return overStaffList;
	}
	
	/**
	 * 检查岗位编制
	 * @param isPartTime 是否需要检查兼职
	 * @param posSet  岗位编制子集
	 * @param ps_worknum 岗位定员字段
	 * @return arraylist 返回超编集合（如果为则不超编）
	 * @throws GeneralException 
	 */
	private ArrayList checkPosStaff(boolean isPartTime,String posSet,String ps_worknum){
	    ArrayList reList = new ArrayList();
	    ContentDAO dao = new ContentDAO(conn);
	    RowSet rs = null;
	    try{
	        String posids = "";
    	    StringBuffer sql = new StringBuffer();
    	    sql.append("select e01a1 from ");
            sql.append(getTmpTableName());
            sql.append(" where curruser='"+this.userView.getUserName()+"' ");
            sql.append(" and nbase in('"+dbs.replaceAll(",", "','")+"') ");
            if(!isPartTime) {
                sql.append(" and isPart<>1 ");
            }
            sql.append(" and "+Sql_switcher.length("e01a1")+">0 ");
            sql.append(" and objecttype=1 group by e01a1");
            rs = dao.search(sql.toString());
            while(rs.next()){
                posids += rs.getString("e01a1")+",";
            }
            if("".equals(posids)) {
                return reList;
            }
            posids = posids.substring(0, posids.length()-1);
            String[] nbases = dbs.split(",");
            HashMap posRealNum = new HashMap(); //岗位实际人数  
            HashMap posGivenNum = new HashMap(); //岗位编制人数    
            HashMap posChangeNum = new HashMap(); //临时表里的变动或添加人员人数  
            HashMap posPartNum = new HashMap(); //岗位兼职人数
            /****
             * 统计该岗位实际人数
             ****/
            sql.setLength(0);
            sql.append("select e01a1,sum(Num) as Num from (");
            for(int i=0;i<nbases.length;i++){
                sql.append(" select e01a1, count(1) as Num from "+nbases[i]+"A01 where not exists( ");
                sql.append(" select 1 from "+getTmpTableName()+" where "+getTmpTableName()+".nbase='"+nbases[i]+"' and "+getTmpTableName()+".a0100="+nbases[i]+"A01.A0100 and objecttype=1 and addflag=0 and curruser='"+userView.getUserName()+"' ");
                sql.append(" ) and e01a1 in ('"+posids.replaceAll(",", "','")+"') group by e01a1 union");
            }
            sql.delete(sql.length()-5, sql.length());
            sql.append(" ) A group by e01a1 ");
            rs = dao.search(sql.toString());
            while(rs.next()){
                posRealNum.put(rs.getObject("e01a1"),rs.getInt("Num")+"");
            }
            
            
            /******
             * 统计岗位编制人数
             ******/
            sql.setLength(0);
            sql.append(" select e01a1,"+ps_worknum+" from  "+posSet+" PSSET");
            sql.append(" where i9999=(select max(i9999) from "+posSet+" where e01a1=PSSET.e01a1) ");
            sql.append(" and e01a1 in ('"+posids.replaceAll(",", "','")+"')");
            rs = dao.search(sql.toString());
            while(rs.next()){
                posGivenNum.put(rs.getObject("e01a1"), rs.getInt(ps_worknum)+"");
            }
        
            /******
             * 统计临时表岗位相关人数
             ******/
            sql.setLength(0);
            sql.append(" select e01a1,sum(Num) as Num from (");
            sql.append(" select e01a1,count(1) as Num from ");
            sql.append(getTmpTableName());
            sql.append(" where e01a1 in ('"+posids.replaceAll(",", "','")+"') ");
            sql.append(" and nbase in('"+dbs.replaceAll(",", "','")+"') and curruser='"+userView.getUserName()+"' ");
            sql.append(" and isPart<>1 and objecttype=1 group by e01a1 ");
            if(isPartTime){
                sql.append(" union select e01a1,count(1) as Num from ");
                sql.append(getTmpTableName());
                sql.append(" where e01a1 in ('"+posids.replaceAll(",", "','")+"') ");
                sql.append(" and nbase in('"+dbs.replaceAll(",", "','")+"') and curruser='"+userView.getUserName()+"' ");
                sql.append(" and isPart=1 and "+this.part_appoint+"=0 and objecttype=1 group by e01a1 ");
            }
            sql.append(" ) A group by e01a1 ");
            rs = dao.search(sql.toString());
            while(rs.next()){
                posChangeNum.put(rs.getObject("e01a1"), rs.getInt("Num")+"");
            }
            
            /******
             * 查询该岗位兼职人数
             */
            if(isPartTime){
                sql.setLength(0);
                sql.append(" select e01a1,sum(Num) as Num from (");
                for(int i=0;i<nbases.length;i++){
                    sql.append(" select "+this.part_pos+" as e01a1,count(1) as Num from  ");
                    sql.append(nbases[i]+this.part_setid);
                    sql.append(" where not exists( select 1 from "+getTmpTableName());
                    sql.append(" where nbase='"+nbases[i]+"' and curruser='"+userView.getUserName()+"' ");
                    sql.append(" and a0100="+nbases[i]+this.part_setid+".a0100 and i9999="+nbases[i]+this.part_setid+".i9999 ");
                    sql.append(" and addflag=0 and objecttype=1 and isPart=1) and "+this.part_appoint+"=0 ");
                    sql.append(" and "+this.part_pos+" in ('"+posids.replaceAll(",", "','")+"') group by "+this.part_pos+" union");
                }
                sql.delete(sql.length()-5, sql.length());
                sql.append(" ) A group by e01a1 ");
                rs = dao.search(sql.toString());
                while(rs.next()){
                    posPartNum.put(rs.getObject("e01a1"), rs.getInt("Num")+"");
                }
            }
            
            String[] posidArray = posids.split(",");
            for(int i=0;i<posidArray.length;i++){
                
                int realNum = 0;
                if(posRealNum.containsKey(posidArray[i])) {
                    realNum = Integer.parseInt(posRealNum.get(posidArray[i]).toString());
                }
                
                int givenNum = 0;
                if(posGivenNum.containsKey(posidArray[i])) {
                    givenNum = Integer.parseInt(posGivenNum.get(posidArray[i]).toString());
                }
                
                int changeNum = 0;
                if(posChangeNum.containsKey(posidArray[i])) {
                    changeNum = Integer.parseInt(posChangeNum.get(posidArray[i]).toString());
                }
                
                int partNum = 0;
                if(posPartNum.containsKey(posidArray[i])) {
                    partNum = Integer.parseInt(posPartNum.get(posidArray[i]).toString());
                }
                
                if(isPartTime){
                    if((realNum+changeNum+partNum)>givenNum){
                        reList.add(posidArray[i]);
                    }
                }else{
                    if((realNum+changeNum)>givenNum) {
                        reList.add(posidArray[i]);
                    }
                }
            }
            
            
	    }catch(Exception e){
	        e.printStackTrace();
	    }finally{
	        try{
	            if(rs != null){
	                rs.close();
	            }
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	    }
        
	    return reList;
	}
	 
	/**
	 * 分析临时表字段
	 * @return
	 */
	private String getFiled(){
		StringBuffer sql = new StringBuffer();
		StringBuffer columnstr = new StringBuffer();
		ResultSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			sql.append("select * from "+this.getTmpTableName()+" where 1=2");
			rs = dao.search(sql.toString());
			ResultSetMetaData rsmd = rs.getMetaData();
			int size = rsmd.getColumnCount();
			for (int i = 1; i <= size; i++) {
				String itemid=rsmd.getColumnName(i);
				if (!("curruser".equalsIgnoreCase(itemid)
						|| "id".equalsIgnoreCase(itemid)
						|| "nbase".equalsIgnoreCase(itemid)
						|| "a0100".equalsIgnoreCase(itemid)
						|| "objecttype".equalsIgnoreCase(itemid)
						|| "addflag".equalsIgnoreCase(itemid)
						|| "i9999".equalsIgnoreCase(itemid)
						|| "ispart".equalsIgnoreCase(itemid)))
				{
					itemid = itemid.toLowerCase();
					FieldItem item=DataDictionary.getFieldItem(itemid);
					if(item!=null&&!"0".equals(item.getUseflag())){
						columnstr.append("," + itemid);
						this.getRegulationFieldList().add(item);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return columnstr.toString()+",";
	}
	
	/**
	 * 条件编制控制
	 * @param fieldset
	 * @param planitem
	 * @param realitem
	 * @param statics
	 * @param dbs
	 * @param doscanorglist
	 * @param ctrl_type
	 * @param method
	 * @param cond
	 * @param order
	 * @return 返回超编机构的机构编码
	 * @throws GeneralException
	 */
	private ArrayList exprDep(FieldSet fieldset,String planitem,String realitem,String statics,String dbs,ArrayList doscanorglist,String method,String cond,int order)throws GeneralException{
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try {
			//人员库是有人数
			Map countMap=null;
			StringBuffer b0110s = new StringBuffer();
			StringBuffer e0122s = new StringBuffer();
			for(int i=doscanorglist.size()-1;i>=0;i--){
				String orgid = (String)doscanorglist.get(i);
				if(orgid.startsWith("UN")){
					b0110s.append(",'"+orgid.substring(2)+"'");
				}else{
					e0122s.append(",'"+orgid.substring(2)+"'");
				}
			}
			boolean flag = false;
			
			//同步临时表中使用的岗位子集指标值
			this.initOrgFieldsValue(kSetMap, "e01a1");
			//临时表人数
			Map tmpCountMap=new HashMap();
			String lexprname="总";
			RecordVo vo = new RecordVo("LExpr");
			if(statics!=null&& "new".equalsIgnoreCase(statics)){
				countMap = countRealPerson(dao,dbs,b0110s,e0122s);
				
				StringBuffer buf = new StringBuffer();
				if(b0110s.length()>3){
					//同步临时表中使用的单位子集指标值
					this.initOrgFieldsValue(orgSetMap, "b0110");
					buf.append("select codeitemid as b0110,count(*) as num from "+this.getTmpTableName()+",organization where codeitemid in(");
					buf.append(b0110s.substring(1)+") and nbase in('"+dbs.replaceAll(",", "','")+"') ");
					buf.append(" and b0110 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' and curruser='"+this.userView.getUserName()+"'");
					buf.append(" group by codeitemid");
					rs = dao.search(buf.toString());
					while(rs.next()){
						tmpCountMap.put(rs.getString("b0110"), rs.getInt("num")+"");
					}
				}
				
				if(e0122s.length()>3){
					//同步临时表中使用的部门子集指标值
					this.initOrgFieldsValue(orgSetMap, "e0122");
					buf.setLength(0);
					
					buf.append("select codeitemid as b0110,count(*) as num from "+this.getTmpTableName()+",organization where codeitemid in(");
					buf.append(e0122s.substring(1)+") and nbase in('"+dbs.replaceAll(",", "','")+"')");
					buf.append(" and e0122 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' and curruser='"+this.userView.getUserName()+"'");
					buf.append(" group by codeitemid");
					rs = dao.search(buf.toString());
					while(rs.next()){
						tmpCountMap.put(rs.getString("b0110"), rs.getInt("num")+"");
					}
				}
				
			}else{
				countMap= new HashMap();
				vo.setInt("id",Integer.parseInt(statics));
				try{
					vo = dao.findByPrimaryKey(vo);
				}catch(Exception e){
					throw com.hrms.struts.exception.GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("org.static.item.setup.static")+order+ResourceFactory.getProperty("org.static.item.setup.static.m")+"\n"));
				}
				lexprname=vo.getString("name");
				String[] dbarr = dbs.split(",");
				StringBuffer sql = new StringBuffer();
				sql.append("Select B0110, sum(num) as num from (");
				for(int i=0;i<dbarr.length;i++){
					String dbpre = dbarr[i];
					FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
							dbpre, false, false, true, 1, "su");
					String wherestr = factor.getSqlExpression();
					if(b0110s.length()>3){
						if(flag) {
                            sql.append(" Union ");
                        }
						sql.append("Select codeitemid as B0110, count(*) AS Num from organization,");
						sql.append(wherestr.substring(5));
						sql.append(" and not Exists(Select 1 From "+this.getTmpTableName()+" where "+this.getTmpTableName()+".nbase='"+dbpre+"' and "+dbpre+"A01.a0100="
								+this.getTmpTableName()+".a0100 and objecttype=1 and addflag=0");
						sql.append(" and curruser='"+this.userView.getUserName()+"')");
						sql.append(" and codeitemid in("+b0110s.substring(1)+")");
						sql.append(" and B0110 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' ");
						sql.append("GROUP BY codeitemid");
						flag = true;
					}
					if(e0122s.length()>3){
						if(flag) {
                            sql.append(" Union ");
                        }
						sql.append("Select codeitemid as b0110, count(*) AS Num from organization,");
						sql.append(wherestr.substring(5));
						sql.append(" and not Exists(Select 1 From "+this.getTmpTableName()+" where "+this.getTmpTableName()+".nbase='"+dbpre+"' and "+dbpre+"A01.a0100="
								+this.getTmpTableName()+".a0100 and objecttype=1 and addflag=0");
						sql.append(" and curruser='"+this.userView.getUserName()+"')");
						sql.append(" and codeitemid in("+e0122s.substring(1)+")");
						sql.append(" and E0122 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' ");
						sql.append("GROUP BY codeitemid");
						flag = true;
					}
				}
				sql.append(") A  GROUP BY B0110");
				rs = dao.search(sql.toString());
				while(rs.next()){
					countMap.put(rs.getString("b0110"), rs.getInt("num")+"");
				}
				
				//临时表人数
				flag=false;
				StringBuffer buf = new StringBuffer();
				FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
						"Usr", false, false, true, 1, "su");
				String wherestr = factor.getSingleTableSqlExpression(this.getTmpTableName());
				if(b0110s.length()>3){
					
					//同步临时表中使用的单位子集指标值
					this.initOrgFieldsValue(orgSetMap, "b0110");
					sql.append("Select B0110, sum(num) as num from (");
					buf.append("select codeitemid as b0110,count(*) as num from "+this.getTmpTableName()+",organization where "+wherestr+" and codeitemid in(");
					buf.append(b0110s.substring(1)+") and nbase in('"+dbs.replaceAll(",", "','")+"') and ispart=0 ");
					buf.append(" and b0110 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%'  and curruser='"+this.userView.getUserName()+"'");
					buf.append("group by codeitemid");
					
					if("true".equals(part_flag)&&"1".equals(this.getPart_occupy_quota())){
						buf.append(" union ");
						buf.append("select codeitemid as b0110,count(*) as num from "+this.getTmpTableName()+",organization where "+wherestr+" and codeitemid in(");
						buf.append(b0110s.substring(1)+") and nbase in('"+dbs.replaceAll(",", "','")+"') and ispart=1  ");
						if(this.getPart_appoint().length()>0) {
                            buf.append("and "+this.getPart_appoint()+"='0'"); //zhangcq  判断任免标识是否为空的问题 2016/8/19
                        }
						buf.append(" and b0110 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' and curruser='"+this.userView.getUserName()+"'");
						buf.append("group by codeitemid");
					}
					sql.append(") A  GROUP BY B0110");
					
					rs = dao.search(buf.toString());
					while(rs.next()){
						tmpCountMap.put(rs.getString("b0110"), rs.getInt("num")+"");
					}
				}
				if(e0122s.length()>3){
					//同步临时表中使用的部门子集指标值
					this.initOrgFieldsValue(orgSetMap, "e0122");
					buf.setLength(0);
					sql.append("Select B0110, sum(num) as num from (");
					buf.append("select codeitemid as b0110,count(*) as num from "+this.getTmpTableName()+",organization where "+wherestr+" and codeitemid in(");
					buf.append(e0122s.substring(1)+") and nbase in('"+dbs.replaceAll(",", "','")+"') and ispart=0 ");
					buf.append(" and e0122 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' and curruser='"+this.userView.getUserName()+"'");
					buf.append("group by codeitemid");
					
					if("true".equals(part_flag)&&"1".equals(this.getPart_occupy_quota())){
						buf.append(" union ");
						buf.append("select codeitemid as b0110,count(*) as num from "+this.getTmpTableName()+",organization where "+wherestr+" and codeitemid in(");
						buf.append(e0122s.substring(1)+") and nbase in('"+dbs.replaceAll(",", "','")+"') and ispart=1 ");
						if(this.getPart_appoint().length()>0) {
                            buf.append("and "+this.getPart_appoint()+"='0'");
                        }
						buf.append(" and e0122 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%' and curruser='"+this.userView.getUserName()+"'");
						buf.append("group by codeitemid");
					}
					sql.append(") A  GROUP BY B0110");
					
					rs = dao.search(buf.toString());
					while(rs.next()){
						tmpCountMap.put(rs.getString("b0110"), rs.getInt("num")+"");
					}
				}
				
			}
			//编制数
			flag = false;
			Map bcountMap=new HashMap();
			StringBuffer buf = new StringBuffer();
			buf.append("select b0110,");
			buf.append(planitem);
			buf.append(" from ");
			buf.append(fieldset.getFieldsetid()+" a ");
			buf.append(" where b0110 in(");
			if(b0110s.length()>3){
				buf.append(b0110s.substring(1));
				flag = true;
			}
			if(e0122s.length()>3){
				if(flag) {
                    buf.append(",");
                }
				buf.append(e0122s.substring(1));
			}
			buf.append(")");
			if(!fieldset.isMainset()){
				buf.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where B0110=a.B0110)");
			}
			rs  = dao.search(buf.toString());
			while(rs.next()){
				bcountMap.put(rs.getString("b0110"),rs.getFloat(planitem)+"");
			}
			
			flag= false;
			//编制人员库中该编制兼职人数
			Map partCountMap = new HashMap();
			if("true".equals(part_flag)&&"1".equals(this.getPart_occupy_quota())){
				FieldSet partSet = DataDictionary.getFieldSetVo(this.getPart_setid().toUpperCase());
				FieldItem unititem = DataDictionary.getFieldItem(this.getPart_unit().toUpperCase());
				FieldItem deptitem = DataDictionary.getFieldItem(this.getPart_dept().toUpperCase());
				String[] dbarr = dbs.split(",");
					StringBuffer sql = new StringBuffer();
					sql.append("Select B0110, sum(num) as num from (");
					for(int i=0;i<dbarr.length;i++){
						String dbpre = dbarr[i];
						String wherestr="";
						if(!(statics!=null&& "new".equalsIgnoreCase(statics))){
							FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
									dbpre, false, false, true, 1, "su");
							wherestr = factor.getSqlExpression();
						}
						if(b0110s.length()>3&&partSet!=null&&"1".equals(partSet.getUseflag())&&unititem!=null&&"1".equals(unititem.getUseflag())){
							if(flag) {
                                sql.append(" Union ");
                            }
							sql.append("Select codeitemid as b0110, count(*) AS Num ");
							sql.append(" from "+dbpre+this.getPart_setid()+",organization where ");
							sql.append(" not Exists(Select 1 From "+this.getTmpTableName()+" where "+this.getTmpTableName()+".nbase='"+dbpre+"' and "+dbpre+this.getPart_setid()+".a0100="
									+this.getTmpTableName()+".a0100 and "+dbpre+this.getPart_setid()+".i9999="+this.getTmpTableName()+".i9999 and objecttype=1 and addflag=0 and ispart=1");
							sql.append(" and curruser='"+this.userView.getUserName()+"')");
							sql.append(" and "+this.getPart_unit()+" like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%'");
							sql.append(" and codeitemid in("+b0110s.substring(1)+")");
							if(this.getPart_appoint().length()>0) {
                                sql.append(" and "+this.getPart_appoint()+"='0' ");
                            }
							
							if(wherestr.length()>0) {
                                sql.append(" and a0100 in(select a0100 "+wherestr+")");
                            }
							sql.append("GROUP BY codeitemid");
							flag = true;
						}
						if(e0122s.length()>3&&partSet!=null&&"1".equals(partSet.getUseflag())&&deptitem!=null&&"1".equals(deptitem.getUseflag())){
							if(flag) {
                                sql.append(" Union ");
                            }
							sql.append("Select codeitemid as b0110, count(*) AS Num ");
							sql.append(" from "+dbpre+this.getPart_setid()+",organization where ");
							sql.append(" not Exists(Select 1 From "+this.getTmpTableName()+" where "+this.getTmpTableName()+".nbase='"+dbpre+"' and "+dbpre+this.getPart_setid()+".a0100="
									+this.getTmpTableName()+".a0100 and "+dbpre+this.getPart_setid()+".i9999="+this.getTmpTableName()+".i9999 and objecttype=1 and addflag=0 and ispart=1");
							sql.append(" and curruser='"+this.userView.getUserName()+"')");
							sql.append(" and "+this.getPart_dept()+" like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%'");
							sql.append(" and codeitemid in("+e0122s.substring(1)+")  ");
							if(this.getPart_appoint().length()>0) {
                                sql.append(" and "+this.getPart_appoint()+"='0' ");
                            }
							if(wherestr.length()>0) {
                                sql.append(" and a0100 in(select a0100 "+wherestr+")");
                            }
							sql.append("GROUP BY codeitemid");
							flag = true;
						}
					}
					sql.append(") A  GROUP BY B0110");
					rs = dao.search(sql.toString());
					while(rs.next()){
						partCountMap.put(rs.getString("b0110"), rs.getInt("num")+"");
					}
			}
			/**
			 * 人员库实有人数Map countMap<orgid<String>,<int>>
			 * 人员库兼职人数Map partCountMap<orgid<String>,<int>>
			 * 编制数 Map bcountMap<orgid<String>,<float>>
			 * 临时表人数  tmpCountMap<orgid<String>,<int>>
			 * 
			 */
			flag = partCountMap.keySet().size()>0;
			if(method!=null&& "1".equals(method)){
				//计算公式人数     <orgid<String>,<float>>
				Map realcountMap = countRealitem(dao,fieldset,realitem,b0110s,e0122s,cond);
				for(int i=doscanorglist.size()-1;i>=0;i--){
					String orgid = (String)doscanorglist.get(i);
					String codeitemid = orgid.substring(2);
					if(tmpCountMap.get(codeitemid)==null||"0".equals(tmpCountMap.get(codeitemid))) {
                        continue;
                    }
					int count = countMap.get(codeitemid)==null?0:Integer.parseInt((String)countMap.get(codeitemid));
					int partcount = 0;
					if(flag) {
                        partcount=partCountMap.get(codeitemid)==null?0:Integer.parseInt((String)partCountMap.get(codeitemid));
                    }
					int tmpcount = Integer.parseInt((String)tmpCountMap.get(codeitemid));
					float bcount = bcountMap.get(codeitemid)==null?0f:Float.parseFloat((String)bcountMap.get(codeitemid));
					float realcount = realcountMap.get(codeitemid)==null?0f:Float.parseFloat((String)realcountMap.get(codeitemid));
					log.debug(orgid+"("+lexprname+")"+"=>"+"原编制库实有人数："+count+",原编制库兼职人数："+partcount+",临时表人数（含兼职）:"+tmpcount+",计算公式人数："+realcount+",该编制数："+bcount);
					if(realcount>0f/*&&bcount>0f*/){
						if(flag){
							BigDecimal b1 = new BigDecimal(realcount+""); 
							BigDecimal b2 = new BigDecimal((count+tmpcount)+""); 
							double d = b2.divide(b1,2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
							if(d>bcount) {
                                list.add(orgid);
                            }
						}else{
							BigDecimal b1 = new BigDecimal(realcount+""); 
							BigDecimal b2 = new BigDecimal((count+tmpcount+partcount)+""); 
							double d = b2.divide(b1,2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
							if(d>bcount) {
                                list.add(orgid);
                            }
						}
					}
				}
			}else{
				for(int i=doscanorglist.size()-1;i>=0;i--){
					String orgid = (String)doscanorglist.get(i);
					String codeitemid = orgid.substring(2);
					if(tmpCountMap.get(codeitemid)==null||"0".equals(tmpCountMap.get(codeitemid))) {
                        continue;
                    }
					int count = countMap.get(codeitemid)==null?0:Integer.parseInt((String)countMap.get(codeitemid));
					int partcount = 0;
					if(flag) {
                        partcount=partCountMap.get(codeitemid)==null?0:Integer.parseInt((String)partCountMap.get(codeitemid));
                    }
					int tmpcount = Integer.parseInt((String)tmpCountMap.get(codeitemid));
					float bcount = bcountMap.get(codeitemid)==null?0f:Float.parseFloat((String)bcountMap.get(codeitemid));
					log.debug(orgid+"("+lexprname+")"+"=>"+"原编制库实有人数："+count+",原编制库兼职人数："+partcount+",临时表人数（含兼职）:"+tmpcount+",该编制数："+bcount);
					/*
					if(flag){
						if(bcount!=0f&&bcount<(count+tmpcount+partcount))
							list.add(orgid);
					}else{
						if(bcount!=0f&&bcount<(count+tmpcount))
							list.add(orgid);
					}
					*/
					//主集或者子集有了是否指标控制占编情况，不需要使用是否为0控制，不合理，屏蔽，朝阳卫生局 wangrd 20160912
					if(flag){
						if(bcount<(count+tmpcount+partcount)) {
                            list.add(orgid);
                        }
					}else{
						if(bcount<(count+tmpcount)) {
                            list.add(orgid);
                        }
					}
				}
			}
			list.add(lexprname);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		return list;
	}
	
	/**
	 * 计算实有人数
	 * @param dao
	 * @param dbs
	 * @param b0110s
	 * @param e0122s
	 * @return
	 */
	private Map countRealPerson(ContentDAO dao,String dbs,StringBuffer b0110s,StringBuffer e0122s){
		Map countmap = new HashMap();
		RowSet rs=null;
		try {
			/**
			 select o.codeitemid, count(*)
				from usrA01 a ,organization o
				where a.e0122 like o.codeitemid+'%' or a.b0110 like o.codeitemid+'%' 
				  and o.codeitemid = left(a.b0110, len(o.codeitemid))
				group by o.codeitemid	
				
						 
			  Select B0110, sum(num) from (
			　　Select B0110, count(*) AS Num from UsrA01 
			　　Where not Exists(Select 1 From 编制临时表 where 编制临时表.nbase=’Usr’ and 编制临时表.A0100=XXXA01.A0100 and 信息群类型=1 and 增加标志=0) and 符合编制统计条件的人员条件
			　　GROUP BY B0110
			　　Union
			　　Select E0122, count(*) AS Num from UsrA01 
			　　Where not Exists(Select 1 From 编制临时表 where 编制临时表.nbase=’Usr’ and 编制临时表.A0100=XXXA01.A0100 and 信息群类型=1 and 增加标志=0) and 符合编制统计条件的人员条件
			　　GROPU BY E0122
			　　Union
			　　其他人员库
			) A  GROUP BY B0110**/
			StringBuffer sql = new StringBuffer();
			String[] dbpres = dbs.split(",");
			sql.append("Select B0110, sum(num) as num from (");
			boolean flag = false;
			for(int i=0;i<dbpres.length;i++){
				String dbpre = dbpres[i];
				if(b0110s.length()>3){
					if(flag) {
                        sql.append(" Union all ");
                    }
					sql.append("Select codeitemid as B0110, count(*) AS Num from "+dbpre+"A01");
					sql.append(" ,organization");
					sql.append(" Where not Exists(Select 1 From "+this.getTmpTableName()+" where "+this.getTmpTableName()+".nbase='"+dbpre+"' and "+dbpre+"A01.a0100="
							+this.getTmpTableName()+".a0100 and objecttype=1 and addflag=0");
					sql.append(" and curruser='"+this.userView.getUserName()+"')");
					sql.append(" and b0110 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%'");
					sql.append(" and codeitemid in("+b0110s.substring(1)+")");
					sql.append("GROUP BY codeitemid");
					flag = true;
				}
				if(e0122s.length()>3){
					if(flag) {
                        sql.append(" Union ");
                    }
					sql.append("Select codeitemid as b0110, count(*) AS Num from "+dbpre+"A01 ");
					sql.append(" ,organization");
					sql.append(" Where not Exists(Select 1 From "+this.getTmpTableName()+" where "+this.getTmpTableName()+".nbase='"+dbpre+"' and "+dbpre+"A01.a0100="
							+this.getTmpTableName()+".a0100 and objecttype=1 and addflag=0");
					sql.append(" and curruser='"+this.userView.getUserName()+"')");
					sql.append(" and e0122 like codeitemid"+com.hrms.hjsj.utils.Sql_switcher.concat()+"'%'");
					sql.append(" and codeitemid in("+e0122s.substring(1)+")");
					sql.append("GROUP BY codeitemid");
					flag=true;
				}
			}
			sql.append(") A  GROUP BY B0110");
			rs = dao.search(sql.toString());
			while(rs.next()){
				countmap.put(rs.getString("b0110"), rs.getInt("num")+"");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return countmap;
	}
	
	/**
	 * 计算公式人数
	 * @param dao
	 * @param fieldset
	 * @param realitem
	 * @param b0110s
	 * @param e0122s
	 * @param cond
	 * @return
	 */
	private Map countRealitem(ContentDAO dao,FieldSet fieldset,String realitem,
			StringBuffer b0110s,StringBuffer e0122s,String cond){
		Map countMap = new HashMap();
		StringBuffer buf = new StringBuffer();
		String FSQL = this.getFQL(realitem, cond);
		BatchBo batchBo = new BatchBo();
		buf.append("select b0110,");
		if(FSQL.indexOf("SELECT_")!=-1){
			buf.append("(select ");
			buf.append(FSQL);
			buf.append(" from ");
			buf.append(batchBo.getTempTable(userView));
			buf.append(" where B0110=a.B0110)");
		}else{
			buf.append(FSQL);
		}
		buf.append(" as "+realitem+" from ");
		buf.append(fieldset.getFieldsetid()+" a ");
		buf.append(" where b0110 in(");
		boolean flag =false;
		if(b0110s.length()>3){
			buf.append(b0110s.substring(1));
			flag = true;
		}
		if(e0122s.length()>3){
			if(flag) {
                buf.append(",");
            }
			buf.append(e0122s.substring(1));
		}
		buf.append(")");
		if(!fieldset.isMainset()){
			buf.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where B0110=a.B0110)");
		}
		RowSet rs=null;
		try {
			rs = dao.search(buf.toString());
			while(rs.next()){
				countMap.put(rs.getString("b0110"), rs.getFloat(realitem)+"");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		
		return countMap;
	}
	
	/**
	 * 公式解析
	 * @param itemid
	 * @param c_expr
	 * @return
	 */
	private String getFQL(String itemid,String c_expr){
		String FSQL="";
		c_expr = SafeCode.decode(c_expr);
		try{
			if(this.userView==null) {
                this.userView = new UserView("su",this.conn);
            }
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			ArrayList alUsedFields = DataDictionary.getFieldList(fielditem.getFieldsetid(),Constant.USED_FIELD_SET);
			YksjParser yp = new YksjParser(this.userView,alUsedFields,YksjParser.forSearch,YksjParser.FLOAT,3,"","");
			yp.setCon(conn);
			yp.run(c_expr);   
	        FSQL=yp.getSQL();
		}catch(Exception e){
			e.printStackTrace();
		}
		return FSQL;
	}
	
	/**
	 * 同步临时表使用到的机构子集指标
	 * @param setMap涉及到的单位、部门、岗位指标
	 * @param keyfield b0110,e0122,e01a1
	 */
	private void initOrgFieldsValue(Map setMap,String keyfield){
		/**
		 * // sql
			update ta
			set zid=sdfsa, z2=sfdaf
			from b01
			where ta.b0110=b01.b0110
			
			// oracle
			update ta
			set (zid, z2)=(select sdfsa,sfdaf
			  from b01
			  where ta.b0110=b01.b0110)
			where curr=''
		 */
		try{
			String keyid = "b0110";
			if("e01a1".equals(keyfield)) {
                keyid=keyfield;
            }
			for(Iterator i=setMap.keySet().iterator();i.hasNext();){
				String setid = (String)i.next();
				List fieldlist = (List)setMap.get(setid);
				StringBuffer sql = new StringBuffer();
				if(1==com.hrms.hjsj.utils.Sql_switcher.searchDbServer()){//sql
					sql.append("update "+this.getTmpTableName()+" set ");
					for(int n=0;n<fieldlist.size();n++){
						String itemid = (String)fieldlist.get(n);
						if(n!=0){
							sql.append(",");
						}
						sql.append(this.getTmpTableName()+"."+itemid+"="+setid+"."+itemid);
					}
					sql.append(" from "+ setid);
					sql.append(" where "+this.getTmpTableName()+"."+keyfield+"="+setid+"."+keyid);
					if(!setid.endsWith("01")){
						sql.append(" and "+setid+".i9999=(select max(i9999) from "+setid+" tt where tt."+keyfield+"="+setid+"."+keyid+")");
					}
					sql.append(" and "+this.getTmpTableName()+".curruser='"+this.userView.getUserName()+"'");
				}else{//oracle
					sql.append("update "+this.getTmpTableName()+" set (");
					for(int n=0;n<fieldlist.size();n++){
						String itemid = (String)fieldlist.get(n);
						if(n!=0){
							sql.append(",");
						}
						sql.append(this.getTmpTableName()+"."+itemid);
					}
					sql.append(")=(select ");
					for(int n=0;n<fieldlist.size();n++){
						String itemid = (String)fieldlist.get(n);
						if(n!=0){
							sql.append(",");
						}
						sql.append(setid+"."+itemid);
					}
					sql.append(" from "+setid);
					sql.append(" where "+this.getTmpTableName()+"."+keyfield+"="+setid+"."+keyid);
					if(!setid.endsWith("01")){
						sql.append(" and "+setid+".i9999=(select max(i9999) from "+setid+" tt where tt."+keyfield+"="+setid+"."+keyid+")");
					}
					sql.append(")");
					sql.append(" where "+this.getTmpTableName()+".curruser='"+this.userView.getUserName()+"'");
				}
				ContentDAO dao = new ContentDAO(this.conn);
				dao.update(sql.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改编制临时表
	 * @param columnSet  临时表需要有的字段
	 */
	HashSet itemSet = new HashSet();
	private void updateTable(){
	   
	       try{
	    	   PosparameXML pos = new PosparameXML(this.conn);
			   String ps_set = pos.getValue(PosparameXML.AMOUNTS,"setid");
				ArrayList clist = pos.getChildList(PosparameXML.AMOUNTS,ps_set);
				if(clist.size()==0) {
                    return;
                }
				
				for(int i=0;i<clist.size();i++){
					String staticitem = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"static");
					String flag = pos.getChildValue(PosparameXML.AMOUNTS,ps_set,clist.get(i).toString(),"flag");
						
					if(!"1".equals(flag)) {
                        continue;
                    }
						
					searchItem(staticitem);
			    }
	    	   
			synchronized (ScanFormationBo.class) {   
	    	   sysbo =new Sys_Oth_Parameter(conn);
	    	   this.part_appoint =  sysbo.getAttributeValues(Sys_Oth_Parameter.PART_TIME, "appoint");
    	       Table table = new Table(this.getTmpTableName());
    	       DbWizard dbw = new DbWizard(this.conn);
    	       //this.getFiled();
    	       FieldItem item = null;
    	       boolean flag = false;
    	       for(int i=0;i<getRegulationFieldList().size();i++){
    	           item = (FieldItem)getRegulationFieldList().get(i);
    	           String itemid = item.getItemid();
    	           if(itemSet.contains(itemid)) {
                       itemSet.remove(itemid);
                   } else
    	               if("b0110".equals(itemid)||"e01a1".equals(itemid) || "e0122".equals(itemid) || "i9999".equals(itemid) || itemid.equals(this.part_appoint.toLowerCase())){
    	            	   itemSet.remove(itemid);
    	               }else{
    	                   table.addField(item);
                           flag = true;
    	               }
    	                   
    	           
    	       }
    	       if(flag) {
                   dbw.dropColumns(table);
               }
    	       
    	       table.clear();
    	       
    	       Iterator ite = itemSet.iterator();
    	       while(ite.hasNext()){
    	           item = DataDictionary.getFieldItem(ite.next().toString());
    	           if(item!=null&&!"0".equals(item.getUseflag())){
    	        	   table.addField(item);
					}
    	           
    	       }
	           if(table.size()>0) {
                   dbw.addColumns(table);
               }
	           DBMetaModel dbm = new DBMetaModel(conn);
	           dbm.reloadTableModel(this.getTmpTableName());
    	       item = null;
    	       table = null;
			} 
			
	       }catch(GeneralException e){
	           e.printStackTrace();
	       } 
	       
       
	   
	}
	
	   private void searchItem(String id){
			 String sql = "select factor from LExpr where Type='1' and id='"+id+"'";
			 String factor="";
			 RowSet rs = null;
			 try{
				 ContentDAO dao = new ContentDAO(this.conn);
				 rs = dao.search(sql);
				 if(rs.next()) {
                     factor = rs.getString("factor");
                 }
			 }catch(Exception e){
				 e.printStackTrace();
			 }finally{
				 if(rs!=null) {
                     PubFunc.closeDbObj(rs);
                 }
			 }
			 
			 if(factor.length()<5) {
                 return;
             }
			 
			 String[] factors = factor.split("`");
			 for(int i=0;i<factors.length;i++){
				 String itemid = factors[i].substring(0, 5);
				 this.itemSet.add(itemid.toLowerCase());
			 }
		}
	   
	   
	   
	/**
	 * 兼职参数设置修改临时表
	 * @param state
	 * @param fieldName
	 */
	public void updateAppoint(String state,String fieldName){
		 synchronized (ScanFormationBo.class) {
	    FieldItem fi = DataDictionary.getFieldItem(fieldName);
	    Table table = new Table(getTmpTableName());
	    DbWizard dbw = new DbWizard(this.conn);
	    
	    
	    
	    try{
	      if(fi != null){
	          
	        table.addField(fi);
	        if("insert".equals(state) && (!dbw.isExistField(getTmpTableName(), fieldName,false))){
	            dbw.addColumns(table);
	        }else if("delete".equals(state) && dbw.isExistField(getTmpTableName(), fieldName,false)){
	            dbw.dropColumns(table);
	        }
	        
	        DBMetaModel dbm = new DBMetaModel(conn);
	           dbm.reloadTableModel(this.getTmpTableName());
	      }
	    }catch(Exception e){
	    }
	    
		 }  
	}
	
	/**
	 * 获取主集中b0110、e0122、e01a1或兼职单位、部门、岗位转b0110、e0122、e01a1原库值使用（对模板中单位、部门、岗位与兼职子集兼职单位、部门、岗位不对于时此需要调用）
	 * @param bean 页面收集好的bean（含nbase,a0100,objecttype,addflag,ispart(对于ispart=1还必须有i9999)）
	 * @param itemids b0110,e0122,e01a1(当前bean中需要在原有库获取哪几个指标值多个用,号分隔)
	 * @return
	 */
	public LazyDynaBean getPartOrMainOrg(LazyDynaBean bean,String itemids){
		String addflag = (String)bean.get("addflag");
		if("1".equals(addflag)) {
            return bean;
        }
		String ispart = (String)bean.get("ispart");
		if("0".equals(ispart)){
			bean = this.getMainOrg(bean, itemids);
		}else if("1".equals(ispart)&&this.getPart_setid().length()>0){
			bean = this.getPartOrg(bean, itemids);
		}
		return bean;
	}
	
	/**
	 * 获取兼职子集任免标示
	 * @param bean
	 * @return
	 */
	public LazyDynaBean getPartPoint(LazyDynaBean bean){
		String addflag = (String)bean.get("addflag");
		if("1".equals(addflag)) {
            return bean;
        }
		String ispart = (String)bean.get("ispart");
		if("-1".equals(ispart)&&this.getPart_setid().length()>0){
			bean.set("ispart", "0");
			bean.set(this.part_appoint, "1");
		}else if("0".equals(ispart)&&this.getPart_setid().length()>0){
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = null;
			try {
				rs = dao.search("select "+this.getPart_appoint()+" from "+bean.get("nbase")+this.getPart_setid()+" where A0100='"+bean.get("a0100")+"' and i9999='"+bean.get("i9999")+"'");
				if(rs.next()){
					bean.set(this.getPart_appoint(), rs.getString(this.getPart_appoint()));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
			    try {
			        
			        if(rs!=null) {
                        rs.close();
                    }
	            } catch (SQLException e) {
	               e.printStackTrace();
	            }
			}
		}
		return bean;
	}
	
	/**
	 * 一个人的非兼职信息修改，编制控制时可能引起兼职单位的条件超编，要获取兼职作为的记录，同时过滤到传递过来的兼职记录
	 * @param values
	 * @param editGetPartRec
	 * @param excludPartRecMap
	 */
	private void editGetPartRec(ArrayList values,Map editGetPartRec,Map excludPartRecMap,int id){
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try{
			for(Iterator i= editGetPartRec.keySet().iterator();i.hasNext();){
				String dbprea0100 = (String)i.next();
				String dbpre = dbprea0100.substring(0,3);
				String a0100 = dbprea0100.substring(3);
				StringBuffer i9999s = (StringBuffer)excludPartRecMap.get(dbprea0100);
				LazyDynaBean bean = (LazyDynaBean)editGetPartRec.get(dbprea0100);
				sql.setLength(0);
				sql.append("select *");
				/*if(this.getPart_unit().length()>0)
					sql.append(this.getPart_unit()+" unit,");
				if(this.getPart_dept().length()>0)
					sql.append(this.getPart_dept()+" dept,");
				if(this.getPart_pos().length()>0)
					sql.append(this.getPart_pos()+" pos ");*/
				sql.append(" from "+dbpre+this.getPart_setid()+" where a0100='"+a0100+"' and ");
				if(this.getPart_appoint().length() > 0) {
                    sql.append(""+this.getPart_appoint()+"='0'");
                }
				if(i9999s!=null){
					sql.append(" and i9999 not in('"+i9999s.toString().replaceAll(",", "','")+"')");
				}
				rs = dao.search(sql.toString());
				ArrayList fields = DataDictionary.getFieldList(this.getPart_setid(), 1);
				while(rs.next()){
					RecordVo vo = new RecordVo(this.getTmpTableName());
					vo.setString("curruser", this.userView.getUserName());
					vo.setInt("id", ++id);
					vo.setString("nbase", (String)bean.get("nbase"));
					vo.setString("a0100", (String)bean.get("a0100"));
					vo.setInt("objecttype", Integer.parseInt((String)bean.get("objecttype")));
					vo.setInt("addflag", Integer.parseInt((String)bean.get("addflag")));
					bean.set("ispart", "1");
					vo.setInt("ispart", Integer.parseInt((String)bean.get("ispart")));
					int _i9999= rs.getInt("i9999");
					vo.setInt("i9999", _i9999);

					for(int n=this.getRegulationFieldList().size()-1;n>=0;n--){
						FieldItem item=(FieldItem)getRegulationFieldList().get(n);
						String itemtype = item.getItemtype();
						String itemid = item.getItemid();
						if("b0110".equals(itemid)||"e0122".equals(itemid)||"e01a1".equals(itemid)) {
                            continue;
                        }
						Object obj = bean.get(itemid.toLowerCase());
						if(obj!=null&& obj instanceof String){
							if("N".equals(itemtype)){
								vo.setInt(itemid, Integer.parseInt((String)obj));
							}else if("D".equals(itemtype)){
								vo.setDate(itemid, ((String)obj).replaceAll("\\.", "-"));
							}else{
								vo.setString(itemid, (String)obj);
							}
						}
					}
					for(int n=0;n<fields.size();n++){
						FieldItem item = (FieldItem)fields.get(n);
						String itemid = item.getItemid();
						String itemtype = item.getItemtype();
						if(this.getPart_unit().length()>0&&itemid.equalsIgnoreCase(this.getPart_unit())) {
                            vo.setString("b0110",rs.getString(itemid));
                        }
						if(this.getPart_dept().length()>0&&itemid.equalsIgnoreCase(this.getPart_dept())) {
                            vo.setString("e0122",rs.getString(itemid));
                        }
						if(this.getPart_pos().length()>0&&itemid.equalsIgnoreCase(this.getPart_pos())) {
                            vo.setString("e01a1",rs.getString(itemid));
                        }
						if(vo.hasAttribute(itemid.toLowerCase())){
							if("N".equals(itemtype)){
								vo.setInt(itemid, rs.getInt(itemid));
							}else if("D".equals(itemtype)){
								vo.setDate(itemid,rs.getTimestamp(itemid));
							}else{
								vo.setString(itemid, rs.getString(itemid));
							}
						}
					}
					
					values.add(vo);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
	}
	
	/**
	 * 获取主记录信息
	 * @param dbprea0100
	 * @param bean
	 * @return
	 */
	private LazyDynaBean getMainBean(String dbprea0100,LazyDynaBean bean){
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try{
			String dbpre = dbprea0100.substring(0,3);
			String a0100 = dbprea0100.substring(3);
			sql.append("select b0110,e0122,e01a1 from "+dbpre+"a01 where a0100='"+a0100+"'");
			rs = dao.search(sql.toString());
			if(rs.next()){
				bean.set("b0110",rs.getString("b0110"));
				bean.set("e0122",rs.getString("e0122"));
				bean.set("e01a1",rs.getString("e01a1"));
			}
				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		return bean;
	}
	
	/**
	 * 是否涉及兼职占编
	 * @return
	 */
	private boolean needgetPartRec(){
		boolean flag = true;
		if(this.getPart_setid()==null||this.getPart_setid().length()<=0) {
            flag = false;
        } else {
		    FieldSet fs = DataDictionary.getFieldSetVo(getPart_setid());
		    if(fs ==null||!"1".equals(fs.getUseflag())) {
                flag = false;
            }
		} 
		
		if(!(("1".equals(this.getPart_occupy_quota())&&getPart_unit()!=null&&this.getPart_unit().length()>0)||("1".equals(this.getPart_takeup_quota())&&getPart_pos()!=null&&this.getPart_pos().length()>0))){
			flag = false;
		}
		if(getPart_appoint()==null||this.getPart_appoint().length()<=0){
			flag = false;
		}
		return flag;
	}
	
	/**
	 * 启用标识 
	 */
	public String getPart_flag() {
		return part_flag;
	}
	
	/**
	 *  兼职单位标识 
	 */
	public String getPart_unit() {
		if(part_unit == null){
			return "";
		}else{
			return part_unit.toLowerCase();
		}
	}

	/**
	 * 兼职子集
	 */
	public String getPart_setid() {
		return part_setid;
	}

	/**
	 * 任免标识
	 */
	public String getPart_appoint() {
		if(part_appoint == null){
			return "";
		}else{
			return part_appoint.toLowerCase();
		}
	}

	/**
	 * 兼职岗位 
	 */
	public String getPart_pos() {
		if(part_pos == null){
			return "";
		}else{
			return part_pos.toLowerCase();
		}
	}
	/**
	 * 兼职部门	 
	 */
	public String getPart_dept() {
		if(part_dept == null){
			return "";
		}else{
			return part_dept.toLowerCase();
		}
	}

	/**
	 * 兼职占用岗位编制：1占用，0或null 则不占用 
	 */
	public String getPart_takeup_quota() {
		return part_takeup_quota;
	}
	
	/**
	 * 兼职占用单位部门编制：1占用，0或null 则不占用
	 */
	public String getPart_occupy_quota() {
		return part_occupy_quota;
	}
	
	public void setPosChange(boolean isPosChange) {
		this.isPosChange = isPosChange;
	}

    public String getInfoChangeFlag() {
        return InfoChangeFlag;
    }

    public void setInfoChangeFlag(String infoChangeFlag) {
        InfoChangeFlag = infoChangeFlag;
    }
	
	
}
