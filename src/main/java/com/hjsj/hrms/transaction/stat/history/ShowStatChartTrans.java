/*
 * Created on 2005-6-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.stat.history;

import com.hjsj.hrms.businessobject.stat.StatDataEncapsulation;
import com.hjsj.hrms.businessobject.sys.AnychartBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ShowStatChartTrans extends IBusiness {
	public ShowStatChartTrans() {
		super();
	}

	/*
	 * ( non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		float[] statvalues=null;
		String[] fieldDisplay;
		String SNameDisplay="";
		String chart_type= (String)this.getFormHM().get("chart_type");
		String userbase = (String) this.getFormHM().get("userbase");
		String statId = (String) this.getFormHM().get("statid");
		String querycond = (String) this.getFormHM().get("querycond");// 组织机构
		String infokind = (String) this.getFormHM().get("infokind");
		String backdates = (String) this.getFormHM().get("backdates");
		String allbackdates = (String)this.getFormHM().get("allbackdates");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList chart_types = new ArrayList();
		//分组折线图 类型值为11
    	CommonData data = new CommonData("11","平面折线图");
    	chart_types.add(data);
    	/*data = new CommonData("31","立体直方图");
    	chart_types.add(data);*/
    	data = new CommonData("29","平面直方图");
    	chart_types.add(data);
    	/*data = new CommonData("5","立体圆饼图");
    	chart_types.add(data);*/
    	data = new CommonData("20","平面圆饼图");
    	chart_types.add(data);
    	this.getFormHM().put("chart_types", chart_types);
    	
    	//快照指标  wangrd 2014-01-03
        String snap_fields="";
        try{
            RowSet rs =dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
            if(rs.next()){
                ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
                snap_fields =xml.getTextValue("/Emp_HisPoint/Struct");
            }else{
                   //设置的快照指标
                rs = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
                if(rs.next())
                    snap_fields=rs.getString("str_value");
            
            }   
            snap_fields = snap_fields+",B0110,E0122,E01A1,A0101,";
            snap_fields =","+snap_fields.toUpperCase()+",";
        }catch(Exception e){
            e.printStackTrace();
        }
        this.getFormHM().put("snap_fields", snap_fields);
    	
		/*
		 * start liubq  修改历史时点领导桌面进入报空指针错误
		 */

    	String sqlstr = "select create_date from hr_hisdata_list order by create_date desc";
    	
    	StringBuffer sb = new StringBuffer();
    	
    	try{
    		this.frowset = dao.search(sqlstr);
    		while(this.frowset.next())
    			sb.append(","+sdf.format(this.frowset.getDate("create_date")));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if(allbackdates==null){
    		allbackdates = sb.length()<1?"":sb.substring(1);
    		this.getFormHM().put("allbackdates", allbackdates);
    	}
    	
    	String backDate = "";
		if(backdates==null&&allbackdates!=null){
			backdates = (allbackdates.split(","))[0];
			backDate = backdates;
			this.getFormHM().put("backdates", backdates);
		}else if(backdates==null&&allbackdates==null){
			backdates = sdf.format(new Date());
			backDate = backdates;
			this.getFormHM().put("backdates", backdates);
		} else if(StringUtils.isNotEmpty(backdates)) {
		    // chenxg 选多个历史时点时默认用选择的第一个历史时点查询设置的历史时点的指标
            backdates = PubFunc.hireKeyWord_filter_reback(backdates);
            backDate = backdates.split("&")[0];
		}
    	/*
		 * end  liubq 
 		 */
		
		String uniqueitem = "";
		String strsql = "select snap_fields from hr_hisdata_list where create_date="+Sql_switcher.dateValue(backDate);
		try {
			this.frowset = dao.search(strsql);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		String fieldstr = "";
		try {
			if (this.frowset.next()) {
				fieldstr = Sql_switcher.readMemo(frowset, "snap_fields").toUpperCase();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		if(!fieldstr.endsWith(","))
			fieldstr=fieldstr+",";
		String[] str_values=fieldstr.split(",");
		for(int i=0;i<str_values.length;i++){
			if(str_values[i].length()!=5)
				continue;
			FieldItem fielditem = DataDictionary.getFieldItem(str_values[i].toLowerCase());
			if (fielditem == null||!"1".equals(fielditem.getUseflag()))
				fieldstr=fieldstr.replaceAll(str_values[i]+",", "");
			//zgd 2014-6-26 单个指标对比（前面是所有指标作为整体，和顺序有关，不对）
			if (snap_fields.indexOf(","+str_values[i].toUpperCase()+",")<0){
			    fieldstr=fieldstr.replaceAll(str_values[i]+",", ""); 
			}
		}
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//身份证
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
		String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		if(chkvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 uniquenessvalid="";
		 String chkcheck="",uniquenesscheck="";

		 if("0".equalsIgnoreCase(chkvalid)|| "".equalsIgnoreCase(chkvalid)){
			 chkcheck="";
		 }
		 else{
			 chkcheck="checked";
		 }
		 if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
			 uniquenesscheck="";
		 }
		 else{
			 uniquenesscheck="checked";
		 }
		StringBuffer setdb=new StringBuffer();
//		if(chk==null)
//			 chk="";
//		if(onlyname==null)
//			 onlyname = "";
//		if(chk.length()>0&&chkcheck.equals("checked")){
//			uniqueitem=chk.toLowerCase();
//		}else if(onlyname.length()>0&&uniquenesscheck.equals("checked")){
//			uniqueitem=onlyname.toLowerCase();
//		}else{
//			uniqueitem="a0100";
//		}
		//唯一性指标有可能在历史统计里不存在。导致查询报错顾改为a0100  wangb 2020-01-13
		uniqueitem="a0100";
		
		String[] tmp =allbackdates.split(",");
		ArrayList backdateslist = new ArrayList();
		for(int i=0;i<tmp.length;i++){
			CommonData data12 = new CommonData(tmp[i],tmp[i]);
			backdateslist.add(data12);
		}
		this.getFormHM().put("backdateslist", backdateslist);

		StatDataEncapsulation simplestat = new StatDataEncapsulation();
		if (!"".equals(statId)){		    
		    if ("1".equals(infokind)){
		        String  strMessage =simplestat.checkFactorItemIsInSnapFlds(statId);
		        if (!"".equals(strMessage)){
		            throw new GeneralException(strMessage); 		            
		        }
		    }
		}
		ArrayList arr = new ArrayList();
		if("29".equals(chart_type)||"31".equals(chart_type)){
			LinkedHashMap jfreemap = new LinkedHashMap();
			ArrayList datalist = new ArrayList();
			try {
				// 安全过滤时 会将参数中 半角字符转成全角，这里转回去  guodd 14-10-20        
				backdates = PubFunc.hireKeyWord_filter_reback(backdates);
				tmp = backdates.split("&");
				int size = tmp.length;
				
				for (int n = 0; n < size; n++) {
					ArrayList list = new ArrayList();
					String backdate = tmp[n];
					if(backdate.length()<10)
						continue;
					statvalues = simplestat.getLexprData(userbase, Integer
							.parseInt(statId), querycond, userView.getUserName(),
							userView.getManagePrivCode(), userView, infokind,backdate);
					SNameDisplay = simplestat.getSNameDisplay();
					if (statvalues != null && statvalues.length > 0) {
						fieldDisplay = simplestat.getDisplay();
						int statTotal = 0;
						for (int i = 0; i < statvalues.length; i++) {
							CommonData vo = new CommonData();
							String str = fieldDisplay[i];
							vo.setDataName(str);
							vo.setDataValue(String.valueOf(statvalues[i]));
							list.add(vo);
							statTotal += statvalues[i];
						}
						
					} else {
						StringBuffer sql = new StringBuffer();
						sql.append("select * from hr_hisdata_sname where id=");
						sql.append(statId);
						List rs = ExecuteSQL.executeMyQuery(sql.toString());
						if (!rs.isEmpty()) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							SNameDisplay = rec.get("name") != null ? rec
									.get("name").toString() : "";
						}
						CommonData vo = new CommonData();
						vo.setDataName("");
						vo.setDataValue("0");
						list.add(vo);
					}
					arr = list;
					jfreemap.put(backdate, list);
				}
				this.getFormHM().put("snamedisplay", SNameDisplay);
				
				if(statvalues!=null){
					for(int n=0;n<statvalues.length;n++){
						ArrayList dataList = new ArrayList();
						String categoryName = "";
						for(Iterator i = jfreemap.keySet().iterator();i.hasNext();){
							String key = (String)i.next();
							ArrayList l = (ArrayList)jfreemap.get(key);
							CommonData c = (CommonData)l.get(n);
							CommonData tc = new CommonData(c.getDataValue(),key);
							categoryName = c.getDataName();
							dataList.add(tc);
						}
						LazyDynaBean abean=new LazyDynaBean();
						 abean.set("categoryName", categoryName);
						 abean.set("dataList",dataList);
						 datalist.add(abean);
					} 
				}else{
					StringBuffer sql = new StringBuffer();
					sql.append("select * from hr_hisdata_sname where id=");
					sql.append(statId);
					List rs = ExecuteSQL.executeMyQuery(sql.toString());
					if (!rs.isEmpty()) {
						LazyDynaBean rec = (LazyDynaBean) rs.get(0);
						SNameDisplay = rec.get("name") != null ? rec
								.get("name").toString() : "";
					}
					CommonData vo = new CommonData();
					vo.setDataName("");
					vo.setDataValue("0");
					datalist.add(vo);
				}
				this.getFormHM().put("list", datalist);
				this.getFormHM().put("jfreemap", jfreemap);
			
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException("", e.toString(), "", "");
			}
		}else{
			LinkedHashMap jfreemap = new LinkedHashMap();
			try {
				
				// 安全过滤时 会将参数中 半角字符转成全角，这里转回去  guodd 14-10-20        
				backdates = PubFunc.hireKeyWord_filter_reback(backdates);
				tmp = backdates.split("&");
				int size = tmp.length;
				for (int n = 0; n < size; n++) {
					String backdate = tmp[n];
					if(backdate.length()<10)
						continue;
					ArrayList list = new ArrayList();
					statvalues = simplestat.getLexprData(userbase, Integer
							.parseInt(statId), querycond, userView.getUserName(),
							userView.getManagePrivCode(), userView, infokind,backdate);
					SNameDisplay = simplestat.getSNameDisplay();
					if (statvalues != null && statvalues.length > 0) {
						fieldDisplay = simplestat.getDisplay();
						int statTotal = 0;
						for (int i = 0; i < statvalues.length; i++) {
							CommonData vo = new CommonData();
							String str = fieldDisplay[i];
							vo.setDataName(str);
							vo.setDataValue(String.valueOf(statvalues[i]));
							list.add(vo);
							statTotal += statvalues[i];
						}
						this.getFormHM().put("snamedisplay", SNameDisplay);
						this.getFormHM().put("list", list);
						arr = list;
					} else {
						StringBuffer sql = new StringBuffer();
						sql.append("select * from hr_hisdata_sname where id=");
						sql.append(statId);
						List rs = ExecuteSQL.executeMyQuery(sql.toString());
						if (!rs.isEmpty()) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							SNameDisplay = rec.get("name") != null ? rec
									.get("name").toString() : "";
						}
						CommonData vo = new CommonData();
						vo.setDataName("");
						vo.setDataValue("0");
						list.add(vo);
						this.getFormHM().put("snamedisplay", SNameDisplay);
						this.getFormHM().put("list", list);
					}
					
					jfreemap.put(backdate, list);
				}
				
				
				this.getFormHM().put("jfreemap", jfreemap);
			
			} catch (Exception e) {
				e.printStackTrace();
				throw new GeneralException(e.toString());
			}
		}
		String xangle="";
		if(arr!=null && !arr.isEmpty())
			xangle = AnychartBo.computeXangle(arr);
		this.getFormHM().put("xangle", xangle);
		this.getFormHM().put("uniqueitem", uniqueitem.toLowerCase());
	}
}
