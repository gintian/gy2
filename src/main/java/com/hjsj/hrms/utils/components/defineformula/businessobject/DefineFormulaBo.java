package com.hjsj.hrms.utils.components.defineformula.businessobject;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.businessobject.kq.interfaces.KqConstant;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.module.kq.util.KqPrivBo;
import com.hjsj.hrms.module.kq.util.KqVer;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.SubField;
import com.hjsj.hrms.module.template.utils.javabean.SubSetDomain;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：DefineFormulaBo 
 * 类描述： 定义计算公式业务类（如果有人扩展可在里面加方法或者前面的交易类调用自己各个模块的业务类）
 * 创建人：zhaoxg
 * 创建时间：Jul 30, 2015 11:44:47 AM
 * 修改人：zhaoxg
 * 修改时间：Jul 30, 2015 11:44:47 AM
 * 修改备注： 
 * @version
 */
public class DefineFormulaBo {
	private Connection conn=null;
	/**登录用户*/
	private UserView userview;
	
	private int modeFlag = IParserConstant.forNormal;
	
	public DefineFormulaBo(Connection conn, UserView userview) {
		this.conn = conn; 
		this.userview=userview;
	}
	
    public void setModeFlag(int modeFlag) {
        this.modeFlag = modeFlag;
    }
    
    public int getModeFlag() {
        return this.modeFlag;
    }
    
	/**
	 * 删除计算公式
	 * @param salaryid
	 * @param itemid
	 * @return
	 * @throws GeneralException 
	 */
	public String delGzFormula(String salaryid,String itemid) throws GeneralException{
		String base="";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList list = new ArrayList();
			StringBuffer strsql = new StringBuffer();
			strsql.append("delete from  salaryformula where salaryid=? and itemid=?");
			list.add(salaryid);
			list.add(itemid);
			dao.delete(strsql.toString(),list);
			base = "ok";
		} catch(Exception e) {
			base = "no";
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return base;
	}
	
	/**
	 * @Title: deleteSpFormula 
	 * @Description: 删除审核公式
	 * @param salaryid  薪资类别
	 * @param chkid 公式id
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-29 下午02:29:14
	 */
	public String deleteGzSpFormula(String salaryid,ArrayList chkidList,String module) throws GeneralException{
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			
			RecordVo vo=new RecordVo("hrpchkformula");
			StringBuffer context = new StringBuffer();
			SalaryTemplateBo bo = new SalaryTemplateBo(conn, userview);
			String name = "";
			if("1".equals(module)){
				name=bo.getSalaryName(salaryid);
			}else if("3".equals(module)){//人事异动
				name=bo.getTemplateName(salaryid);
			}
			for (int i = 0; i < chkidList.size(); i++) {
				vo.setInt("chkid",Integer.parseInt(chkidList.get(i).toString()));
				vo = dao.findByPrimaryKey(vo);
				context.append(ResourceFactory.getProperty("lable.portal.main.del")+"："+name+"（"+salaryid+"）"+ResourceFactory.getProperty("gz_new.gz_accounting.deleteGzSpFormula")+"（"+vo.getString("name")+"）<br>");
				String sql = "delete from hrpchkformula where chkid=?";
				String chkid=chkidList.get(i)+"";
				ArrayList list = new ArrayList();
				list.add(chkid);
				dao.delete(sql,list);
			}
			
			return context.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 初始化计算公式
	 * @param salaryid
	 * @param fields
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList initGzFormula(String salaryid,String[] fields) throws GeneralException{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList querylist = new ArrayList();
			StringBuffer str = new StringBuffer();
			str.append("select itemid,useflag,hzname,itemname,runflag,itemtype,sortid");
			str.append(" from salaryformula where salaryid=?");
			str.append(" order by sortid,itemid desc ");	
			querylist.add(salaryid);
			rs = dao.search(str.toString(),querylist);
			while(rs.next()){
				HashMap map = new HashMap();
				for(int i=0;i<fields.length;i++){
					if("hzname".equalsIgnoreCase(fields[i]))//为了页面动态下拉框可自动定位，此处不单纯传汉字名称，要把字段带过去
						map.put(fields[i], rs.getString("itemname")+"#!#"+rs.getString(fields[i]));
					else if("seq".equalsIgnoreCase(fields[i])){
						map.put(fields[i], rs.getString("sortid"));
					}else
						map.put(fields[i], rs.getString(fields[i]));
				}
				list.add(map);
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	/**
	 * 
	* <p>Description: 初始化</p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-9 下午04:44:15
	*@param tableid
	*@param id
	*@param flag
	*@return
	 * @throws GeneralException 
	 */
	public ArrayList initGzFormulaTemp(String tableid,String id,String[] fields) throws GeneralException{
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		id=id!=null&&id.trim().length()>0?id:"";
		/**判断用户是否拥有该模版资源的权限**/
        boolean isCorrect=false;
        if(this.userview.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
                isCorrect=true;
        if(!isCorrect){
            throw new GeneralException("当前用户不具有相应的权限");
        }
		ArrayList itemlist = new ArrayList();
		ArrayList affterlist = new ArrayList();
		String affteritem_arr = "";
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.conn,Integer.parseInt(tableid),this.userview);
			ArrayList list = changebo.getAllFieldItem();
			int infor_type =changebo.getInfor_type();
			HashMap map = changebo.getSub_domain_map();
			HashMap field_name_map = changebo.getField_name_map();
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)(((FieldItem)list.get(i)).cloneItem());
				String itemdesc = "";
				if (infor_type != 1 && (fielditem.getItemid().equalsIgnoreCase("codesetid")
						|| fielditem.getItemid().equalsIgnoreCase("codeitemdesc")
						/* || fielditem.getItemid().equalsIgnoreCase("corcode") */ || fielditem.getItemid().equalsIgnoreCase(
								"parentid")/* ||fielditem.getItemid().equalsIgnoreCase("start_date") */)) {
					continue;
				}
				
				if(fielditem.getVarible()==2){//去掉子集
					continue;
				}
				if ("start_date".equalsIgnoreCase(fielditem.getItemid())){
					//前台判断全部按下划线_，兼容,防止报错，保存的时候再替换回来。
					fielditem.setItemid("start*date");
				}
				if(fielditem.isChangeAfter()){  //计算公式支持大字段类型
					if(stritem.indexOf(fielditem.getItemid()+"_2,")!=-1)
						continue;
					if(changebo.getOpinion_field()!=null&&changebo.getOpinion_field().length()>0&&changebo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
						continue;
					stritem+=fielditem.getItemid()+"_2,";
					itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
					CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					affterlist.add(dataobj);
					affteritem_arr+=fielditem.getItemid()+":"+ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc()+"`";
				}else if(fielditem.isChangeBefore()){
					//多个变化前加上_id
					String sub_domain_id="";
					if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
						sub_domain_id ="_"+(String)map.get(""+i);
					}
					if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1,")!=-1)
						continue;
					if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
						continue;
					stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
					if(sub_domain_id!=null&&sub_domain_id.length()>0){
						fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
						fielditem.setItemdesc(""+map.get(""+i+"hz"));
					}
				/*	if(fielditem.isMainSet()){
						itemdesc=fielditem.getItemdesc();
					}
					else */
					{
						itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
					}
				} else {
					if(stritem.indexOf(fielditem.getItemid())!=-1)
						continue;
					itemdesc=fielditem.getItemdesc();
					stritem+=fielditem.getItemid()+",";
				}
				if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())&&fielditem.getItemid().indexOf("attachment")==-1)
				{
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
					itemlist.add(dataobj);
				}
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.conn,tableid);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(fielditem.getItemid())!=-1)
				continue;
			stritem+=fielditem.getItemid()+",";
			CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),fielditem.getItemdesc());
			itemlist.add(dataobj);
		}
		
		CommonData dataobj = new CommonData(":","");
		itemlist.add(0,dataobj);
		String item = "";
		String item2 ="";
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		ContentDAO dao = new ContentDAO(this.conn);
			String[] itemFactor = formulabo.getItem(dao,tableid,id,affterlist);
			item = itemFactor[0];
			item2 = itemFactor[3];
		
		
		ArrayList listFormula = formulabo.itemListFormula(dao,item,affterlist,tableid,id,fields);

		return listFormula;
	}
	/**
	 * 
	* <p>Description:人事异动-计算公式-获取所有计算公式 </p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-18 下午04:26:17
	 */
	public String getFormulas(String tableid,String id,String gzFlag) throws GeneralException{
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";
		id=id!=null&&id.trim().length()>0?id:"";
		/**判断用户是否拥有该模版资源的权限**/
        boolean isCorrect=false;
        if(this.userview.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
                isCorrect=true;
        if(!isCorrect){
            throw new GeneralException("当前用户不具有相应的权限");
        }
		ArrayList itemlist = new ArrayList();
		ArrayList affterlist = new ArrayList();
		String affteritem_arr = "";
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.conn,Integer.parseInt(tableid),this.userview);
			ArrayList list = changebo.getAllFieldItem();
			int infor_type =changebo.getInfor_type();
			HashMap map = changebo.getSub_domain_map();
			HashMap field_name_map = changebo.getField_name_map();
			for(int i=0;i<list.size();i++){
				FieldItem fielditem = (FieldItem)(((FieldItem)list.get(i)).cloneItem());
				String itemdesc = "";
				if(infor_type!=1&&("codesetid".equalsIgnoreCase(fielditem.getItemid())|| "codeitemdesc".equalsIgnoreCase(fielditem.getItemid())|| "corcode".equalsIgnoreCase(fielditem.getItemid())|| "parentid".equalsIgnoreCase(fielditem.getItemid())/*||fielditem.getItemid().equalsIgnoreCase("start_date")*/))
				{
					continue;
				}
				if ("start_date".equalsIgnoreCase(fielditem.getItemid())){
					//前台判断全部按下划线_，兼容,防止报错，保存的时候再替换回来。
					fielditem.setItemid("start*date");
				}
				if(fielditem.getVarible()==2){//去掉子集
					continue;
				}
				if(fielditem.isChangeAfter()){  //计算公式支持大字段类型
					if(stritem.indexOf(fielditem.getItemid()+"_2,")!=-1)
						continue;
					if(changebo.getOpinion_field()!=null&&changebo.getOpinion_field().length()>0&&changebo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
						continue;
					stritem+=fielditem.getItemid()+"_2,";
					itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
					CommonData dataobj = new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					affterlist.add(dataobj);
					affteritem_arr+=fielditem.getItemid()+":"+ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc()+"`";
				}else if(fielditem.isChangeBefore()){
					//多个变化前加上_id
					String sub_domain_id="";
					if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
						sub_domain_id ="_"+(String)map.get(""+i);
					}
					if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1,")!=-1)
						continue;
					if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
						continue;
					stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
					if(sub_domain_id!=null&&sub_domain_id.length()>0){
					fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
					fielditem.setItemdesc(""+map.get(""+i+"hz"));
					}
				/*	if(fielditem.isMainSet()){
						itemdesc=fielditem.getItemdesc();
					}
					else */
					{
						itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
					}
				} else {
					if(stritem.indexOf(fielditem.getItemid())!=-1)
						continue;
					itemdesc=fielditem.getItemdesc();
					stritem+=fielditem.getItemid()+",";
				}
				if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())&&fielditem.getItemid().indexOf("attachment")==-1)
				{
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
					itemlist.add(dataobj);
				}
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.conn,tableid);
		for(int i=0;i<templist.size();i++){
			FieldItem fielditem = (FieldItem)templist.get(i);
			if(stritem.indexOf(fielditem.getItemid())!=-1)
				continue;
			stritem+=fielditem.getItemid()+",";
			CommonData dataobj = new CommonData(fielditem.getItemid()+":"+fielditem.getItemdesc(),fielditem.getItemdesc());
			itemlist.add(dataobj);
		}
		
		CommonData dataobj = new CommonData(":","");
		itemlist.add(0,dataobj);
		String item = "";
		String item2 ="";
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		ContentDAO dao = new ContentDAO(this.conn);
		
		String[] itemFactor = formulabo.getItem(dao,tableid,id,affterlist);
		if("0".equals(gzFlag)){//带前缀，0_A1905_2
			item = itemFactor[0];
		}else if("3".equals(gzFlag)){//不带前缀，A1905_2
			item = itemFactor[3];
		}
			
		return item;
	}
	/**
	 * @Title: getSpFormulaList 
	 * @Description: 取得薪资某工资类别的审批公式数据
	 * @param salaryid 薪资类别id
	 * @return ArrayList 
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-8-26 下午05:22:31
	 */
	public ArrayList<HashMap<String,String>> getGzSpFormulaList(String salaryid,String[] fields,String module) throws GeneralException
	{
		ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		RowSet rs = null;
		try
		{
			StringBuffer sql = new StringBuffer();
			ArrayList querylist = new ArrayList();
			if("1".equals(module)){
				sql.append("select chkid as itemid,name as spname,validflag,information,seq  from hrpchkformula where flag=1 and tabid=? order by seq");
			}else if("3".equals(module)){//人事异动，gaohy,2016-1-5
				sql.append("select chkid as itemid,name as spname,validflag,information,seq  from hrpchkformula where flag=0 and tabid=? order by seq");
			}
			ContentDAO dao = new ContentDAO(this.conn);
			querylist.add(salaryid);
			rs=dao.search(sql.toString(),querylist);
			while(rs.next())
			{
				HashMap<String,String> map = new HashMap<String,String>();
				for(int i=0;i<fields.length;i++){
					if("information".equals(fields[i]))
						map.put(fields[i], Sql_switcher.readMemo(rs,"information"));
					else
						map.put(fields[i], rs.getString(fields[i]));
				}
				list.add(map);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}finally{
			PubFunc.closeResource(rs);
		}
		return list;
	}
	/**
	 * 
	* <p>Description:人事异动-计算公式-联动项目下拉框数据 </p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-16 下午03:24:14
	 */
	public ArrayList tempIemCodeLinkage(String tableid,String opt,String _itemid,String formulaType,String formula) throws GeneralException{
		if("1".equals(opt)){//项目下拉框数据
			/**判断用户是否拥有该模版资源的权限**/
	        boolean isCorrect=false;
	        if(this.userview.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
	            isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tableid))
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
	                isCorrect=true;
	        if(!isCorrect)
	            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
	                isCorrect=true;
	        if(!isCorrect){
	            throw new GeneralException("当前用户不具有相应的权限");
	        }
			ArrayList itemlist = new ArrayList();
			HashMap<String,String> dataobj=new HashMap<String,String>();
			ArrayList affterlist = new ArrayList();
			String affteritem_arr = "";
			String stritem="";
			String sub_sta = ResourceFactory.getProperty("org.maip.subset.name").substring(0, 6);//统计表单子集
			//对于统计表单子集下拉框取当前子集的内容
			if(StringUtils.isNotBlank(formula) && formula.indexOf(sub_sta) > -1) {
				
				//将公式拆分拿到涉及的子集指标
				String formulaarrs []= formula.split(" ");
				String field = formulaarrs[1];
				if(field.endsWith("的")) {
					field = field.substring(0,field.length()-1);
				}
				TemplateUtilBo utilBo = new TemplateUtilBo(this.conn, this.userview);
				ArrayList cellList = utilBo.getPageCell(Integer.parseInt(tableid), -1);
				
				for (int k = 0; k < cellList.size(); k++) { 
					TemplateSet setBo = (TemplateSet) cellList.get(k);
					String field_hz = setBo.getField_hz();//指标的名字  没改过的有现拟
					String hz = setBo.getHz();//改过的名字
					int chg = setBo.getChgstate();
					//判断名字是否改动
					boolean isSubflag = setBo.isSubflag();
					if(isSubflag) {
						SubSetDomain subDomain = new SubSetDomain(setBo.getXml_param());
						ArrayList sublist=subDomain.getSubFieldList();
						hz = hz.replaceAll("[\\{\\}\\`]", "");
						if(!hz.equals(field_hz)) {//改了名字
							if(field.equals(hz)) {
								itemlist.addAll(getSubItemList(sublist));
							}
						}else {
							if(chg==1) {
								field_hz = "现"+field_hz;
							}else if(chg==2) {
								field_hz = "拟"+field_hz;
							}
							if(field.equals(field_hz)) {
								itemlist.addAll(getSubItemList(sublist));
							}
						}
					}
				}
			}
			if(tableid.length()>0){
				TemplateTableBo changebo = new TemplateTableBo(this.conn,Integer.parseInt(tableid),this.userview);
				ArrayList list = changebo.getAllFieldItem();
				int infor_type =changebo.getInfor_type();
				HashMap map = changebo.getSub_domain_map();
				HashMap field_name_map = changebo.getField_name_map();
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)(((FieldItem)list.get(i)).cloneItem());
					String itemdesc = "";
					//32576 审核公式显示组织单元类型、组织单元名称、上级组织单元名称这三个指标,计算公式不显示 liuyz
					if(!"2".equals(formulaType)&&infor_type!=1&&("codesetid".equalsIgnoreCase(fielditem.getItemid())|| "codeitemdesc".equalsIgnoreCase(fielditem.getItemid())|| "corcode".equalsIgnoreCase(fielditem.getItemid())|| "parentid".equalsIgnoreCase(fielditem.getItemid())/*||fielditem.getItemid().equalsIgnoreCase("start_date")*/))
					{
						continue;
					}
					if(fielditem.getVarible()==2){//去掉子集
						continue;
					}
					if(fielditem.getItemid().startsWith("S_")) {
						continue;//bug48574 参考项目去掉签章
					}
					if(fielditem.isChangeAfter()){  //计算公式支持大字段类型
						if(stritem.indexOf(fielditem.getItemid()+"_2,")!=-1)
							continue;
						if(changebo.getOpinion_field()!=null&&changebo.getOpinion_field().length()>0&&changebo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
							continue;
						stritem+=fielditem.getItemid()+"_2,";
						itemdesc=ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
						
						affteritem_arr+=fielditem.getItemid()+":"+ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc()+"`";
					}else if(fielditem.isChangeBefore()){
						//多个变化前加上_id
						String sub_domain_id="";
						if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
						
						sub_domain_id ="_"+(String)map.get(""+i);
						}
						if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1,")!=-1)
							continue;
						if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
							continue;
						stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
						if(sub_domain_id!=null&&sub_domain_id.length()>0){
						fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
						fielditem.setItemdesc(""+map.get(""+i+"hz"));
						}
					/*	if(fielditem.isMainSet()){
							itemdesc=fielditem.getItemdesc();
						}
						else */
						if("A01".equalsIgnoreCase(fielditem.getFieldsetid())){
							itemdesc=fielditem.getItemdesc();
						}else{
							itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
						}
					} else {
						if(stritem.indexOf(fielditem.getItemid())!=-1)
							continue;
						itemdesc=fielditem.getItemdesc();
						stritem+=fielditem.getItemid()+",";
					}
					if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())&&fielditem.getItemid().indexOf("attachment")==-1&&!"signature".equalsIgnoreCase(fielditem.getItemid()))
					{
						dataobj = new HashMap<String,String>();
						dataobj.put("id",fielditem.getItemid().toUpperCase()+":"+itemdesc);
						dataobj.put("name",fielditem.getItemid().toUpperCase()+":"+itemdesc);
						itemlist.add(dataobj);
					}
				}
			}
			TempvarBo tempvarbo = new TempvarBo();
			ArrayList templist = tempvarbo.getMidVariableList(this.conn,tableid);
			for(int i=0;i<templist.size();i++){
				FieldItem fielditem = (FieldItem)templist.get(i);
				if(stritem.indexOf(fielditem.getItemid())!=-1)
					continue;
				stritem+=fielditem.getItemid()+",";
				dataobj = new HashMap<String,String>();
				dataobj.put("id",fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				dataobj.put("name",fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
				itemlist.add(dataobj);
			}
			itemlist.add(0,dataobj);

			return itemlist;
		}else{//代码型下拉框数据
			TempvarBo tempvarbo = new TempvarBo();
			ArrayList codelist=new ArrayList();
			String itemid = _itemid.split(":")[0];
			codelist=tempvarbo.codeListFormula(this.conn,itemid);
			return codelist;
		}
	}
	
	private ArrayList getSubItemList(ArrayList sublist) {
		HashMap<String,String> dataobj = new HashMap<String,String>();
		ArrayList itemlist = new ArrayList();
		try {
			for(int j = 0; j < sublist.size(); j++) {
				SubField subField = (SubField)sublist.get(j);
				String itemId = subField.getFieldname();
				String name = subField.getTitle();
				dataobj = new HashMap<String,String>();
				dataobj.put("id",itemId.toUpperCase()+":"+name);
				dataobj.put("name",itemId.toUpperCase()+":"+name);
				itemlist.add(dataobj);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return itemlist;
	}
	/**
	 * 薪资项目数据联动
	 * @param salaryid
	 * @param opt
	 * @param _itemid
	 * @param formulaType 公式类别：2是审核公式
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList gzItemCodeLinkage(String salaryid,String opt,String _itemid,String formulaType) throws GeneralException{
		ArrayList list = new ArrayList();
		try{
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap<String,String> map = new HashMap<String,String>();
			if("1".equals(opt)){
				String sqlstr = "";
				String sqlstr1 = "";
				ArrayList fieldList = new ArrayList();
				sqlstr = "select itemid,itemdesc from salaryset where salaryid="+salaryid+" order by sortid";
				sqlstr1 = "select cname,chz from midvariable where nflag=0 and templetid=0 and (cstate='"+salaryid+"' or cstate is null)";
				ArrayList dylist = null;			
				if(!"-1".equals(salaryid)){
					dylist = dao.searchDynaList(sqlstr);
					for(Iterator it=dylist.iterator();it.hasNext();){
						DynaBean dynabean=(DynaBean)it.next();
						String itemid = dynabean.get("itemid").toString();
						if("A0100".equalsIgnoreCase(itemid))
							continue;
						if("A0000".equalsIgnoreCase(itemid))
							continue;	
						String itemdesc = dynabean.get("itemdesc").toString();
						map = new HashMap<String,String>();
						map.put("id", itemid+":"+itemdesc);
						map.put("name", itemid+":"+itemdesc);
						list.add(map);
					}
					dylist.clear();
				}else{
					int n = fieldList.size();
					for(int i=0;i<n;i++){
						FieldItem item = (FieldItem)fieldList.get(i);
						String itemid = item.getItemid().toUpperCase();
						if(itemid==null || "".equals(itemid))
							continue;
						String tempPriv = this.userview.analyseFieldPriv(itemid);
						if("0".equals(tempPriv))
							continue;
						String itemdesc = item.getItemdesc();
						map = new HashMap<String,String>();
						map.put("id", itemid+":"+itemdesc);
						map.put("name", itemid+":"+itemdesc);
						list.add(map);
					}
				}
					
				dylist = dao.searchDynaList(sqlstr1);
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					String itemid = dynabean.get("cname").toString();
					if("A0100".equalsIgnoreCase(itemid))
						continue;
					if("A0000".equalsIgnoreCase(itemid))
						continue;	
					String itemdesc = dynabean.get("chz").toString();
					map = new HashMap<String,String>();
					map.put("id", itemid+":"+itemdesc);
					map.put("name", itemid+":"+itemdesc);
					list.add(map);
				}
				if(!"2".equals(formulaType)){
					map = new HashMap<String,String>();
					map.put("id", "newcreate");
					map.put("name", ResourceFactory.getProperty("gz_new.gz_accounting.newMidVar"));
					list.add(map);
				}
			}else{
				_itemid = _itemid.split(":")[0];
				if(_itemid==null||_itemid.length()<1){
					map = new HashMap<String,String>();
					map.put("id", "");
					map.put("name", "");
					list.add(map);
					return list;
				}
				ArrayList dylist = null;
				FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(_itemid);
				String codesetid ="";
				if(fielditem==null){
				    fielditem=getMidVariableList(_itemid,"0");
				}

					if(fielditem!=null){
						codesetid = fielditem.getCodesetid();
						if(fielditem.isCode()||codesetid.trim().length()>0){
							if(codesetid!=null||codesetid.trim().length()>0){
								StringBuffer _sqlstr = new StringBuffer();
								if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
									_sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
									_sqlstr.append(codesetid);
									_sqlstr.append("' order by a0000");
								}else if("@@".equalsIgnoreCase(codesetid)){
									_sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
								}else
								{
									_sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
									_sqlstr.append(codesetid);
									_sqlstr.append("' and invalid=1");
									if(AdminCode.isRecHistoryCode(codesetid)){//按照是否有效和有效时间来卡住  zhaoxg add 2014-8-14
										String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
										_sqlstr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
									}
									_sqlstr.append(" order by a0000");								
								}
								dylist = dao.searchDynaList(_sqlstr.toString());
								for(Iterator it=dylist.iterator();it.hasNext();){
									DynaBean dynabean=(DynaBean)it.next();
									String codeitemid = dynabean.get("codeitemid").toString();
									String codeitemdesc = dynabean.get("codeitemdesc").toString();
									map = new HashMap<String,String>();
									map.put("id", codeitemid);
									map.put("name",codeitemid+":"+codeitemdesc);
									list.add(map);
								}
								map = new HashMap<String,String>();
								map.put("id", "");
								map.put("name", "");
								list.add(map);
							}else{
								map = new HashMap<String,String>();
								map.put("id", "");
								map.put("name", "");
								list.add(map);
							}
						}else{
							map = new HashMap<String,String>();
							map.put("id", "");
							map.put("name", "");
							list.add(map);
						}
					}else{
						map = new HashMap<String,String>();
						map.put("id", "");
						map.put("name", "");
						list.add(map);
						if("escope".equals(_itemid)){
							map = new HashMap<String,String>();
							map.put("id", "1");
							map.put("name", "1"+":"+ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_1"));
							list.add(map);
							
							map = new HashMap<String,String>();
							map.put("id", "2");
							map.put("name", "2"+":"+ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_2"));
							list.add(map);
							
							map = new HashMap<String,String>();
							map.put("id", "3");
							map.put("name", "3"+":"+ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_3"));
							list.add(map);
							
							map = new HashMap<String,String>();
							map.put("id", "4");
							map.put("name", "4"+":"+ResourceFactory.getProperty("report.actuarial_report.person_sort.U02_4"));
							list.add(map);
						}
					}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
     * 从临时变量中取得对应指标列表
     * @param itemid
     * @param nflag 
     * @return FieldItem对象列表
     * @throws GeneralException
     */
    public FieldItem getMidVariableList(String itemid,String nflag) throws GeneralException{
        FieldItem item=null;
        try{
            StringBuffer buf=new StringBuffer();
            buf.append("select nid,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
            buf.append(" midvariable where nflag="+nflag+"  and  ");
            Pattern pattern = Pattern.compile("[0-9]+");
            if(pattern.matcher(itemid.trim()).matches()) //整型 用nid
            {
                buf.append(" nid="+itemid);
            }
            else
                buf.append(" cname='"+itemid+"'"); 
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=dao.search(buf.toString());
            if(rset.next())
            {
                item=new FieldItem();
                item.setItemid(rset.getString("nid"));
                item.setFieldsetid("A01");//没有实际含义
                item.setItemdesc(rset.getString("chz"));
                item.setItemlength(rset.getInt("fldlen"));
                item.setDecimalwidth(rset.getInt("flddec"));
                item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
                switch(rset.getInt("ntype"))
                {
                case 1://
                    item.setItemtype("N");
                    item.setCodesetid("0");
                    break;
                case 2:
                    item.setItemtype("A");
                    item.setCodesetid("0");
                    break;
                case 3:
                    item.setItemtype("D");
                    item.setCodesetid("0");
                    break;
                case 4:
                    item.setItemtype("A");
                    item.setCodesetid(rset.getString("codesetid"));
                    break;
                }
                item.setVarible(1);
                
            }// while loop end.
        }catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return item;
    }
    /**
     * 编辑计算公式（新增和修改）
     * @param salaryid 薪资类别id
     * @param formulaitemid 计算公式id
     * @param addOrUpdate 是新增还是编辑
     * @param oleitemid 代码id
     * @throws GeneralException 
     */
    public void editGzFormula(String salaryid,String formulaitemid,String addOrUpdate,String oleitemid,ArrayList storeList) throws GeneralException{
    	String[] arr = formulaitemid.split("#!#");
    	ContentDAO dao = new ContentDAO(this.conn);
        try{
        	ArrayList list = new ArrayList();
        	LazyDynaBean bean = new LazyDynaBean();
    		if("update".equals(addOrUpdate)){
    			String hzname=arr[1];
    			String itemname=arr[0];
    			StringBuffer strsql = new StringBuffer();
    			strsql.append("update  salaryformula set hzname=? ,itemname=? where salaryid=? and itemid=?");

    			list.add(hzname);
    			list.add(itemname);
    			list.add(salaryid);
    			list.add(oleitemid);
    			
    			bean.set("hzname", hzname);
    			bean.set("itemname", itemname);
    			storeList.add(bean);
    			dao.update(strsql.toString(),list);
    		}else if("new".equals(addOrUpdate)){
    			String[] itemsort = this.itemSortid(dao,salaryid);
    			FieldItem fielditem = null;	
    			StringBuffer strsql = new StringBuffer();
    			strsql.append("insert into  salaryformula(salaryid,itemid,sortid,hzname,itemname,itemtype,runflag,useflag) values(?,?,?,?,?,?,?,?)");
    			list.add(salaryid);
    			if(itemsort.length==2){
    				list.add(itemsort[0]);
    				list.add(itemsort[1]);
        			bean.set("itemid", itemsort[0]);
        			bean.set("seq", itemsort[1]);
    			}else{
    				list.add(0);
        			list.add(0);
        			bean.set("itemid", 0);
        			bean.set("seq", 0);
    			}
    			
    			if(arr.length==2){
    				list.add(arr[1]);
        			list.add(arr[0]);
        			bean.set("hzname", arr[1]);
        			bean.set("itemname", arr[0]);
    				fielditem = DataDictionary.getFieldItem(arr[0]);
    			}else{
    				list.add(0);
        			list.add(0);
        			bean.set("hzname", 0);
        			bean.set("itemname", 0);
    			}
    			if(fielditem!=null){
    				list.add(fielditem.getItemtype());
    			}else{
    				list.add("N");
    			}
    			list.add(0);
    			bean.set("runflag", 0);
    			list.add(1);
    			bean.set("useflag", 1);
    			storeList.add(bean);
    			dao.update(strsql.toString(),list);
    		}
        }catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    /**
	 * @Title: editSpFormula 
	 * @Description: 新增或编辑审核公式
	 * @param salaryid 薪资类别id
	 * @param spFormulaId 薪资公式id
	 * @param spFormulaName 审核公式名称
	 * @param spComment 审核公式提示
	 * @param gz_module 模块号
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-29 下午04:46:30
	 */
	 public String editGzSpFormula(String salaryid,String spFormulaId,String spFormulaName,String spComment,String gz_module,ArrayList storeList) throws GeneralException{
	    	ContentDAO dao = new ContentDAO(this.conn);
	        try{
	        	String context = null;
	        	ArrayList list = new ArrayList();
	        	LazyDynaBean bean = new LazyDynaBean();
	        	SalaryTemplateBo bo = new SalaryTemplateBo(this.conn, this.userview);
	    		if(StringUtils.isNotBlank(spFormulaId)){ //编辑
	    			RecordVo vo=new RecordVo("hrpchkformula");
					vo.setInt("chkid",Integer.parseInt(spFormulaId));
					vo = dao.findByPrimaryKey(vo);	
					String _formula = vo.getString("name");
					//将编辑操作写入操作日志
					if(_formula!=null&&!_formula.equals(spFormulaName)){
						String name = bo.getSalaryName(salaryid);
						context = ResourceFactory.getProperty("lable.portal.main.edit")+"："+name+"（"+salaryid+"）"+ResourceFactory.getProperty("gz_new.gz_accounting.editSpFormulaName")+"（"+_formula+"--->"+spFormulaName+"）<br>";
					}else{
						String name = bo.getSalaryName(salaryid);
						context = ResourceFactory.getProperty("lable.portal.main.edit")+"："+name+"（"+salaryid+"）"+ResourceFactory.getProperty("gz_new.gz_accounting.editSpFormulaName2");
					}

	    			StringBuffer strsql = new StringBuffer("update  hrpchkformula set name=?");
	    			if(StringUtils.isNotBlank(spComment))
	    				strsql.append(",information=? ");
	    			strsql.append(" where chkid=?");

	    			list.add(spFormulaName);
	    			bean.set("spname", spFormulaName);
	    			if(StringUtils.isNotBlank(spComment)){
	    				list.add(spComment);
	    				bean.set("information", spComment);
	    			}
	    			list.add(spFormulaId);
	    			dao.update(strsql.toString(),list);
	    		}else{ //新增
	    			int seq=this.getGzSpSeq()+1;
					IDGenerator idg = new IDGenerator(2, this.conn);
					spFormulaId = idg.getId("hrpchkformula.chkid");
					String sql = "insert into hrpchkformula (chkid,name,information,seq,flag,tabid,validflag) values (?,?,?,?,?,?,?)";
					list = new ArrayList();
					list.add(spFormulaId);
					bean.set("itemid", spFormulaId);
					list.add(spFormulaName);
					bean.set("spname", spFormulaName);
					list.add(spComment);
					bean.set("information", spComment);
					list.add(seq+"");
					bean.set("seq", seq+"");
					if("3".equals(gz_module)){
						list.add("0");
					}else{
						list.add("1");
					}
					list.add(salaryid);
					list.add("0");//新增审核公式默认不启动
					bean.set("validflag", "0");
					dao.insert(sql.toString(), list);
					
					//将新增操作写入操作日志
					String name = "";
					if("3".equals(gz_module)){
						name=bo.getTemplateName(salaryid);
					}else{
						name=bo.getSalaryName(salaryid);
					}
					context = ResourceFactory.getProperty("button.new.add")+"："+name+"（"+salaryid+"）"+ResourceFactory.getProperty("gz_new.gz_accounting.addFormula")+"（"+spFormulaName+"）<br>";
	    		}
	    		storeList.add(bean);
	    		return context;
	        }catch(Exception ex){
	            ex.printStackTrace();
	            throw GeneralExceptionHandler.Handle(ex);
	        }
	    }
	 
	/**
	 * @Title: getGzSpSeq 
	 * @Description: 获取薪资审批公式最大排序号
	 * @return int
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-8-31 下午02:58:42
	 */
	public int getGzSpSeq() throws GeneralException {
		int seq = 0;
		RowSet frowset = null;
		try {
			String sql = "select max(seq) seq from hrpchkformula ";
			ContentDAO dao = new ContentDAO(this.conn);
			frowset = dao.search(sql);
			while (frowset.next()) {
				seq = frowset.getInt("seq");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(frowset);
		}
		return seq;
	}
	 
	/**
	 * 获取sortid和itemid
	 * @param dao 
	 * @param salaryid 薪资类别id
	 * @return String[]
	 * @throws GeneralException
	 */
	public String[] itemSortid(ContentDAO dao,String salaryid) throws GeneralException{
		String[] standid = {"0","0"};
		String sqlstr = "select max(itemid) as itemid,max(sortid) as sortid from salaryformula  where salaryid="+salaryid;
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				if(dynabean.get("itemid")!=null&&dynabean.get("itemid").toString().trim().length()>0){
					standid[0] = Integer.parseInt(dynabean.get("itemid").toString())+1+"";
				}
				if(dynabean.get("sortid")!=null&&dynabean.get("sortid").toString().trim().length()>0){
					standid[1] = Integer.parseInt(dynabean.get("sortid").toString())+1+"";
				}
			}
		} catch(GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return standid;
	}
	/**
	 * 获取编辑计算公式的下拉框
	 * @param salaryid 薪资类别id
	 * @return ArrayList
	 * @throws GeneralException 
	 */
	public ArrayList getFormulaCombox(String salaryid) throws GeneralException{
		ArrayList<HashMap> list = new ArrayList<HashMap>();
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			ArrayList dylist = null;
			String sqlstr = "select itemid,itemdesc from salaryset where salaryid="+salaryid+"  order by sortid";
			dylist = dao.searchDynaList(sqlstr);
			String str = ",a0100,a0000,a00z2,a00z3,nbase,b0110,e0122,a0101,";//不显示的列
			HashMap<String,String> map = new HashMap<String,String>();
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString();
				String itemdesc = dynabean.get("itemdesc").toString();
				if(str.indexOf(","+itemid.toLowerCase()+",")==-1){
					map = new HashMap<String,String>();
					map.put("id", itemid+"#!#"+itemdesc);
					map.put("name", itemdesc);
					list.add(map);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	/**
	 * 
	* <p>Description:人事异动-获取计算公式下拉框数据 </p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	 * @throws GeneralException 
	* @date 2015-12-11 下午05:50:41
	 */
	public ArrayList getFormulaComboxTemp(String tableid) throws GeneralException{
		ArrayList<HashMap> listFormula = new ArrayList<HashMap>();
		
		boolean isCorrect=false;
        if(this.userview.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userview.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
                isCorrect=true;
        if(!isCorrect){
            throw new GeneralException("当前用户不具有相应的权限");
        }
        ArrayList itemlist = new ArrayList();
		ArrayList affterlist = new ArrayList();
		String affteritem_arr = "";
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.conn,Integer.parseInt(tableid),this.userview);
			ArrayList list = changebo.getAllFieldItem();
			int infor_type =changebo.getInfor_type();
			HashMap map = changebo.getSub_domain_map();
			HashMap field_name_map = changebo.getField_name_map();
			for(int i=0;i<list.size();i++){
				
				FieldItem fielditem = (FieldItem)(((FieldItem)list.get(i)).cloneItem());
				String itemdesc = "";
				if (infor_type != 1 && (fielditem.getItemid().equalsIgnoreCase("codesetid")
						|| fielditem.getItemid().equalsIgnoreCase("codeitemdesc")
						/* || fielditem.getItemid().equalsIgnoreCase("corcode") */ || fielditem.getItemid().equalsIgnoreCase(
								"parentid")/* ||fielditem.getItemid().equalsIgnoreCase("start_date") */)) {
					continue;
				}
				if ("start_date".equalsIgnoreCase(fielditem.getItemid())){
					//前台判断全部按下划线_，兼容,防止报错，保存的时候再替换回来。
					fielditem.setItemid("start*date");
				}
				if(fielditem.getVarible()==2){//去掉子集
					continue;
				}
				if(fielditem.getItemid().indexOf("attachment")!=-1) {
					continue;//bug48080
				}
			//	if(fielditem.isChangeAfter()&&!fielditem.isMemo()){
				if(fielditem.isChangeAfter()){  //计算公式支持大字段类型
					if(stritem.indexOf(fielditem.getItemid()+"_2,")!=-1)
						continue;
					if(changebo.getOpinion_field()!=null&&changebo.getOpinion_field().length()>0&&changebo.getOpinion_field().equalsIgnoreCase(fielditem.getItemid()))
						continue;
					HashMap<String,String> mapFormula = new HashMap<String,String>();
					mapFormula.put("id", fielditem.getItemid().toUpperCase()+":"+fielditem.getItemdesc());
					mapFormula.put("name", fielditem.getItemdesc());
					//affteritem_arr+=fielditem.getItemid()+":"+fielditem.getItemdesc()+"`";
					listFormula.add(mapFormula);
				}else if(fielditem.isChangeBefore()){
					//多个变化前加上_id
					String sub_domain_id="";
					if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
					
					sub_domain_id ="_"+(String)map.get(""+i);
					}
					if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1,")!=-1)
						continue;
					if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
						continue;
					stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
					if(sub_domain_id!=null&&sub_domain_id.length()>0){
					fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
					fielditem.setItemdesc(""+map.get(""+i+"hz"));
					}
				/*	if(fielditem.isMainSet()){
						itemdesc=fielditem.getItemdesc();
					}
					else */
					{
						itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
					}
				} else {
					if(stritem.indexOf(fielditem.getItemid())!=-1)
						continue;
					itemdesc=fielditem.getItemdesc();
					stritem+=fielditem.getItemid()+",";
				}
			}
		}
		
		//去重  26392 linbz 原来是HashSet去重导致顺序错乱
		for(int i=0;i<listFormula.size();i++){
			for (int j=listFormula.size()-1;j>i;j--){  
				HashMap<String,String> mapFormula1 = (HashMap<String,String>) listFormula.get(i);
				String id1 = mapFormula1.get("id");
				String name1 = mapFormula1.get("name");
				
				HashMap<String,String> mapFormula2 = (HashMap<String,String>) listFormula.get(j);
				String id2 = mapFormula2.get("id");
				String name2 = mapFormula2.get("name");
				
				if(id1.equalsIgnoreCase(id2) && name1.equalsIgnoreCase(name2)){
					listFormula.remove(j);
				}
			}
		}
		return listFormula;
	}
	/**
	 * 公式校验
	 * @param c_expr 计算公司内容
	 * @param type 数据类别
	 * @param fieldlist 对应指标列表
	 * @return String
	 * @throws GeneralException 
	 */
	public String checkFormula(String c_expr,String type,ArrayList fieldlist) throws GeneralException{
		String flag = "";
		try{
			if (c_expr != null && c_expr.length() > 0) {
				YksjParser yp = new YksjParser(this.userview, fieldlist, this.getModeFlag(), getColumType(type)
						, YksjParser.forPerson,"Ht", "");		
				yp.setVarList(fieldlist);//使用“执行标准”函数时，临时变量需要用到单独传入的fielditem数据集 zhanghua 20170516
				yp.setSupportVar(true);//设置允许临时变量
				yp.setCon(this.conn);
				boolean b = false;
				try{
					b = yp.Verify_where(c_expr.trim());
				}catch (Exception e) {
					e.printStackTrace();
					
					b = false;
				}
				if (b) {// 校验通过
					flag="ok";
				}else{
					flag = yp.getStrError();
				} 
			}else{
				flag="ok";
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=YksjParser.STRVALUE;
		}else if("D".equals(type)){
			temp=YksjParser.DATEVALUE;
		}else if("N".equals(type)){
			temp=YksjParser.FLOAT;
		}else if("L".equals(type)){
			temp=YksjParser.LOGIC;
		}else{
			temp=YksjParser.STRVALUE;
		}
		return temp;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException 
	 * @throws GeneralException
	 */
	public ArrayList getGzMidVariableList(String salaryid) throws GeneralException{
		ArrayList fieldlist=new ArrayList();
		try{
			SalaryTemplateBo gzbo = new SalaryTemplateBo(conn, userview);
			fieldlist.addAll(gzbo.getMidVarItemList(salaryid));
			fieldlist.addAll(gzbo.getSalaryItemList(null, salaryid, 2));
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return fieldlist;
	}
	/**
	 * 保存公式内容
	 * @param salaryid 薪资类别id
	 * @param formula 公式内容
	 * @param itemid 公式代码id
	 * @throws GeneralException 
	 */
	public void saveGzFormula(String salaryid,String formula,String itemid) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			//保存公式多解了一次密
			//formula=SafeCode.decode(formula);
			//formula=PubFunc.keyWord_reback(formula);
			RecordVo vo=new RecordVo("salaryformula");
			vo.setInt("salaryid",Integer.parseInt(salaryid));
			vo.setInt("itemid",Integer.parseInt(itemid));
			vo = dao.findByPrimaryKey(vo);	
			vo.setString("rexpr", formula);
			dao.updateValueObject(vo);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * @Title: saveGzSpFormula 
	 * @Description: 保存薪资审批公式 
	 * @param formula 公式计算内容
	 * @param itemid 公式id
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-8-31 下午02:33:16
	 */
	public void saveGzSpFormula(String formula,String itemid) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			//公式多解了一次密
			//formula=SafeCode.decode(formula);
			//formula=PubFunc.keyWord_reback(formula);
			RecordVo vo=new RecordVo("hrpchkformula");
			vo.setInt("chkid",Integer.parseInt(itemid));
			vo = dao.findByPrimaryKey(vo);	
			vo.setString("formula", formula);
			dao.updateValueObject(vo);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 设置公式状态
	 * @param salaryid
	 * @param itemid
	 * @param flag
	 * @param batch
	 * @throws GeneralException 
	 */
	public void setGzFormulaValid(String salaryid,String itemid,String flag,String batch) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("update salaryformula set useflag='");
			buf.append(flag);
			buf.append("' where salaryid=");
			buf.append(salaryid);
			/**单个设置计算公式有效*/
			if("0".equalsIgnoreCase(batch))
			{
				buf.append(" and itemid='");
				buf.append(itemid);
				buf.append("'");
			}
			dao.update(buf.toString());
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * @Title: setGzSpFormulaValid 
	 * @Description: 设置审批公式状态 
	 * @param chkid 公式id
	 * @param flag 是否启用
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-8-31 下午04:27:51
	 */
	public void setGzSpFormulaValid(String chkid,String flag) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			StringBuffer buf=new StringBuffer();
			ArrayList list = new ArrayList();
			buf.append("update hrpchkformula set validflag=?  where chkid=?");
			list.add(flag);
			list.add(chkid);
			dao.update(buf.toString(),list);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * 拖拽计算公式
	 * @param salaryid 薪资类别id
	 * @param ori_itemid 源目标对象id
	 * @param ori_seq 源目标序号
	 * @param to_itemid 目标对象id
	 * @param to_seq 目标对象序号
	 * @param dropPosition 移动方式，上移或下移
	 * @throws GeneralException 
	 */
	public void removeFormula(String salaryid,String ori_itemid,String ori_seq,String to_itemid,String to_seq)  throws GeneralException{
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer str = new StringBuffer();		
			ArrayList list = new ArrayList();
			String dropPosition = "";
			if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
				dropPosition = "before";
			else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
				dropPosition = "after";
			else if(Integer.valueOf(ori_itemid) < Integer.valueOf(to_itemid))
				dropPosition = "before";
			else if(Integer.valueOf(ori_itemid) > Integer.valueOf(to_itemid))
				dropPosition = "after";
			if("before".equals(dropPosition)){//上移
				//将上移对象的SORTID替换成目标对象的
				str.append("update salaryformula set SORTID=? where  SALARYID=? and itemid=?");
				list.add(to_seq);
				list.add(salaryid);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
				str.setLength(0);
				list = new ArrayList();
				//在移动对象和目标对象之间的对象SORTID都加1.
				str.append("update salaryformula set SORTID = SORTID+1 where SORTID>=? and SORTID<=? and  SALARYID=? and itemid<>?");
				list.add(to_seq);
				list.add(ori_seq);
				list.add(salaryid);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
			}else if("after".equals(dropPosition)){//下移
				//将下移对象的SORTID替换成目标对象的
				str.append("update salaryformula set SORTID=? where  SALARYID=? and itemid=?");
				list.add(to_seq);
				list.add(salaryid);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
				str.setLength(0);
				list = new ArrayList();
				//在移动对象和目标对象之间的对象SORTID都减1.
				str.append("update salaryformula set SORTID = SORTID-1 where SORTID>=? and SORTID<=? and  SALARYID=? and itemid<>?");
				list.add(ori_seq);
				list.add(to_seq);
				list.add(salaryid);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 
	* <p>Description:人事异动-计算公式-拖拽公式组 </p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-24 下午05:43:42
	 */
	public void removeFormulaTemp(String TabId,String ori_itemid,String ori_seq,String to_itemid,String to_seq)  throws GeneralException{
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer str = new StringBuffer();		
			ArrayList list = new ArrayList();
			String dropPosition = "";
			if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
				dropPosition = "before";
			else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
				dropPosition = "after";
			else if(Integer.valueOf(ori_itemid) < Integer.valueOf(to_itemid))
				dropPosition = "before";
			else if(Integer.valueOf(ori_itemid) > Integer.valueOf(to_itemid))
				dropPosition = "after";
			if("before".equals(dropPosition)){//上移
				//将上移对象的SORTID替换成目标对象的
				str.append("update gzAdj_formula set nSort=? where  TabId=? and id=?");
				list.add(to_seq);
				list.add(TabId);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
				str.setLength(0);
				list = new ArrayList();
				//在移动对象和目标对象之间的对象SORTID都加1.
				str.append("update gzAdj_formula set nSort = nSort+1 where nSort>=? and nSort<=? and  TabId=? and id<>?");
				list.add(to_seq);
				list.add(ori_seq);
				list.add(TabId);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
			}else if("after".equals(dropPosition)){//下移
				//将下移对象的SORTID替换成目标对象的
				str.append("update gzAdj_formula set nSort=? where  TabId=? and id=?");
				list.add(to_seq);
				list.add(TabId);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
				str.setLength(0);
				list = new ArrayList();
				//在移动对象和目标对象之间的对象SORTID都减1.
				str.append("update gzAdj_formula set nSort = nSort-1 where nSort>=? and nSort<=? and  TabId=? and id<>?");
				list.add(ori_seq);
				list.add(to_seq);
				list.add(TabId);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * @Title: moveGzSpFormula 
	 * @Description: 移动薪资审批公式
	 * @param salaryid 薪资类别id
	 * @param ori_itemid 源目标对象id
	 * @param ori_seq 源目标序号
	 * @param to_itemid 目标对象id
	 * @param to_seq 目标对象序号
	 * @param dropPosition 移动方式，上移或下移
	 * @throws GeneralException
	 * @author lis  
	 * @date 2015-9-1 下午03:26:10
	 */
	public void moveGzSpFormula(String salaryid,String ori_itemid,String ori_seq,String to_itemid,String to_seq ) throws GeneralException{
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer str = new StringBuffer();		
			ArrayList list = new ArrayList();
			
			String dropPosition = "";
			if(Integer.valueOf(ori_seq) > Integer.valueOf(to_seq))
				dropPosition = "before";
			else if(Integer.valueOf(ori_seq) < Integer.valueOf(to_seq))
				dropPosition = "after";
			else if(Integer.valueOf(ori_itemid) < Integer.valueOf(to_itemid))//当目标排序号和移动的相同时根据id判断排序
				dropPosition = "before";
			else if(Integer.valueOf(ori_itemid) > Integer.valueOf(to_itemid))
				dropPosition = "after";
			
			//将移动对象的seq替换成目标对象的
			str.append("update hrpchkformula set seq =? where chkid=?");
			list.add(to_seq);
			list.add(ori_itemid);
			dao.update(str.toString(),list);
			
			str.setLength(0);
			list = new ArrayList();
			if("before".equals(dropPosition)){//上移
				//在移动对象和目标对象之间的对象seq都加1.
				str.append("update hrpchkformula set seq = seq+1 where seq>=? and seq<=? and  tabid=? and chkid<>?");
				list.add(to_seq);
				list.add(ori_seq);
				list.add(salaryid);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
			}else if("after".equals(dropPosition)){//下移
				//在移动对象和目标对象之间的对象seq都减1.
				str.append("update hrpchkformula set seq = seq-1 where seq>=? and seq<=? and  tabid=? and chkid<>?");
				list.add(ori_seq);
				list.add(to_seq);
				list.add(salaryid);
				list.add(ori_itemid);
				dao.update(str.toString(),list);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	/**
	 * @Title: getComputeCond 
	 * @Description: 获得计算公式内容
	 * @param salaryid 新增类别id
	 * @param itemid 公式代码id
	 * @return String
	 * @throws GeneralException
	 * @author zhaoxg  
	 * @date 2015-9-1 下午03:24:17
	 */
	public String getComputeCond(String salaryid,String itemid) throws GeneralException{
		String cond = "";
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			RowSet rs = dao.search("select cond from salaryformula where itemid='"+itemid+"' and  salaryid="+salaryid);
			if(rs.next()){
				cond = rs.getString("cond");
				cond=cond!=null&&cond.trim().length()>0?cond:"";
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return cond;
	}
	/**
	 * @Title: getTempFormula 
	 * @Description: 人事异动-获得计算公式内容
	 * @param tabId 模版id
	 * @param groupId 公式代码id
	 * @return String
	 * @throws GeneralException
	 * @author gaohy  
	 * @date 2016-1-5 上午10:22:23
	 */
	public String getTempFormula(String tabId,String groupId) throws GeneralException{
		String cFactor = "";
		ContentDAO dao=new ContentDAO(this.conn);
		try{
			RowSet rs = dao.search("select cFactor from gzAdj_formula where TabId='"+tabId+"' and Id='"+groupId+"'");
			if(rs.next()){
				cFactor = rs.getString("cFactor");
				cFactor=cFactor!=null&&cFactor.trim().length()>0?cFactor:"";
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return cFactor;
	}
    /**
     * 税率明细表列表
     * @param id
     * @return
     */
    public ArrayList getTaxDetailTableList(String taxid){
    	ArrayList list = new ArrayList();
    	try{
    		StringBuffer buf = new StringBuffer();
			StringBuffer sql= new StringBuffer("select * from  gz_taxrate_item where taxid in(");
			if(taxid.indexOf(",")==-1)
			{
				buf.append(taxid);
				sql.append(buf.toString());
			}else
			{
				String[] arr= taxid.split(",");
				for(int i=0;i<arr.length;i++)
				{
					buf.append(",");
					buf.append(arr[i]);
				}
				sql.append(buf.toString().substring(1));
			}
			sql.append(") order by taxid,taxitem");
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs= null;
    		rs=dao.search(sql.toString());
    		DecimalFormat myformat1 = new DecimalFormat("########.###");//
    		while(rs.next()){
    			LazyDynaBean bean = new LazyDynaBean();
    			bean.set("taxitem",rs.getString("taxitem"));
    			bean.set("ynse_down",getXS(String.valueOf(rs.getFloat("ynse_down")),2));
    			bean.set("ynse_up",getXS(String.valueOf(rs.getFloat("ynse_up")),2));
    			bean.set("sl",rs.getString("sl")==null?"0":myformat1.format(rs.getDouble("sl")));
    			bean.set("sskcs",getXS(String.valueOf(rs.getFloat("sskcs")),2));
    			bean.set("flag",rs.getString("flag"));
    			bean.set("description",rs.getString("description")==null?"":rs.getString("description"));
    			bean.set("kc_base",getXS(String.valueOf(rs.getFloat("kc_base")),2));
    			bean.set("taxid",String.valueOf(rs.getInt("taxid")));
    			list.add(bean);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    private String getXS(String str,int scale){
    	if(str==null|| "null".equalsIgnoreCase(str)|| "".equals(str))
    		str="0.00";
    	BigDecimal m=new BigDecimal(str);
    	BigDecimal one = new BigDecimal("1");
    	return m.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
    }
    /**
     * 计税方式列表
     */
    public ArrayList getTaxTypeList(){
    	ArrayList list = new ArrayList();
		RowSet rs = null;
    	try{
    		String sql="select codeitemid,codeitemdesc from codeitem where codesetid='46' and "+Sql_switcher.sqlNow()+"" +
					" between start_date and end_date order by a0000";
    		ContentDAO dao = new ContentDAO(this.conn);

    		rs=dao.search(sql);
    		HashMap map;
    		while(rs.next()){
    			map = new HashMap();
    			map.put("id", rs.getString("codeitemid"));
    			map.put("name", rs.getString("codeitemdesc"));
    			list.add(map);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
    		PubFunc.closeDbObj(rs);
		}
		return list;
    }
    /**
     * 应纳所得税列表
     */
    public ArrayList getIncomeList(String salaryid){
    	ArrayList list = new ArrayList();
    	try{
    		String sql="select * from salaryset where itemtype='N' and salaryid='"+salaryid+"' order by sortid ";
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(sql);
    		HashMap map = new HashMap();
    		while(rs.next()){
    			map = new HashMap();
    			map.put("id", rs.getString("itemid"));
    			map.put("name", rs.getString("itemdesc"));
    			list.add(map);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 得到税率表基数
     * @param id
     * @return
     */
    public String getK_base(String id){
		String k_base="";
		String sql = "select k_base from gz_tax_rate where taxid="+id;
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		try{
			rs=dao.search(sql);
			while(rs.next()){
				k_base=rs.getString("k_base");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return getXS(k_base,2);
	}
    /**
     * 获取税率表列表
     * @return
     */
    public ArrayList getTaxrate(){
    	ArrayList list = new ArrayList();
    	try{
    		String sqlstr = "select taxid,description from gz_tax_rate";
    		ArrayList dylist = null;
    		try {
    			ContentDAO dao = new ContentDAO(this.conn);
    			dylist = dao.searchDynaList(sqlstr);
    			HashMap map = new HashMap();
    			for(Iterator it=dylist.iterator();it.hasNext();){
    				DynaBean dynabean=(DynaBean)it.next();
    				String taxid = dynabean.get("taxid").toString();
    				String description = dynabean.get("description").toString();
        			map = new HashMap();
        			map.put("id", taxid);
        			map.put("name", description);
    				list.add(map);
    			}
    		} catch(GeneralException e) {
    			e.printStackTrace();
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return list;
    }
    /**
     * 取主键最大值加1
     * @param tableName
     * @param idColumnName
     * @return
     */
    public int getTaxId(String tableName,String idColumnName)
    {
    	int i=0;
    	try
    	{
    		StringBuffer sql = new StringBuffer();
    		sql.append("select max(");
    		sql.append(idColumnName);
    		sql.append(") id  from ");
    		sql.append(tableName);
    		ContentDAO dao = new ContentDAO(this.conn);
    		RowSet rs = null;
    		rs=dao.search(sql.toString());
    		while(rs.next())
    		{
    			i=rs.getInt("id");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return i==0?1:i+1;
 
    }
    
    /**
     * @author lis
     * @Description: 获得薪资标准历史套id
     * @date 2016-2-4
     * @param conn
     * @param id
     * @return
     * @throws GeneralException 
     */
    public String pkgId(Connection conn,String id) throws GeneralException{
		String pkgid = "";
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select pkg_id as pkgid from gz_stand_pkg where status=1";//select max(pkg_id) as pkgid from gz_stand_history  where id='"+id+"'
		ArrayList dylist = null;
		try {
			dylist = dao.searchDynaList(sqlstr);
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				pkgid = dynabean.get("pkgid").toString();
			}
		} catch(GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return pkgid;
	}
    /**
          * 获取考勤假期管理可以设置计算公式的指标
     * @return
     */
    public ArrayList<HashMap<String,String>> getKqFormulaTemp() {
        ArrayList<HashMap<String,String>> kqList = new ArrayList<HashMap<String,String>>();
        ArrayList fieldlist = DataDictionary.getFieldList("Q17", Constant.USED_FIELD_SET);
        HashMap<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put("NBASE", "");
        fieldMap.put("Q1701", "");
        fieldMap.put("I9999", "");
        fieldMap.put("A0100", "");
        fieldMap.put("B0110", "");
        fieldMap.put("E0122", "");
        fieldMap.put("E01A1", "");
        fieldMap.put("A0101", "");
        fieldMap.put("Q1709", "");
        
        for (int i = 0; i < fieldlist.size(); i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            FieldItem fielditem = (FieldItem) fieldlist.get(i);
            if(fieldMap.containsKey(fielditem.getItemid().toUpperCase()))
                continue;
            
            map.put("itemname", fielditem.getItemid());
            map.put("hzname", fielditem.getItemid().toUpperCase() + ":" + fielditem.getItemdesc());
            kqList.add(map);
        }
        
        return kqList;
    }

    /**
          * 获取考勤假期管理联动信息集、指标、代码
     * 
     * @param opt
          *            查询类型：=0 信息集；=1 指标；=2 代码
     * @param id
          *            查询时需要的id：指标时，是子集id；代码时，是指标id
     * @return
     */
    public ArrayList<HashMap<String, String>> getFieldList(String opt, String id) {
        ArrayList<HashMap<String, String>> fieldList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = null;
        if ("0".equalsIgnoreCase(opt)) {
            ArrayList<FieldSet> list = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,
                    Constant.EMPLOY_FIELD_SET);
            for (int i = 0; i < list.size(); i++) {
                FieldSet fielSet = list.get(i);
                if (fielSet == null)
                    continue;

                map = new HashMap<String, String>();
                map.put("id", fielSet.getFieldsetid());
                map.put("name", fielSet.getFieldsetid().toUpperCase() + ":"
                        + fielSet.getFieldsetdesc());
                fieldList.add(map);
            }
        } else if ("1".equalsIgnoreCase(opt)) {
            if (StringUtils.isEmpty(id))
                id = "A01";

            ArrayList<FieldItem> list = DataDictionary.getFieldList(id, Constant.USED_FIELD_SET);
            for (int i = 0; i < list.size(); i++) {
                FieldItem fi = list.get(i);
                if ("a0100".equalsIgnoreCase(fi.getItemid()))
                    continue;

                map = new HashMap<String, String>();
                map.put("id", fi.getItemid().toUpperCase() + ":" + fi.getItemdesc());
                map.put("name", fi.getItemid().toUpperCase() + ":" + fi.getItemdesc());
                fieldList.add(map);
            }
        } else {
            String itemid = id.split(":")[0];
            FieldItem fieldItem = DataDictionary.getFieldItem(itemid, 0);
            if (fieldItem == null)
                return fieldList;
            
            String codesetid = fieldItem.getCodesetid();
            if (StringUtils.isBlank(codesetid) || "0".equalsIgnoreCase(codesetid))
                return fieldList;

            if (!"0".equalsIgnoreCase(codesetid)) {
                ArrayList<CodeItem> codeItemList = AdminCode.getCodeItemList(codesetid);
                for (int i = 0; i < codeItemList.size(); i++) {
                    CodeItem item = codeItemList.get(i);
                    if (item == null)
                        continue;

                    map = new HashMap<String, String>();
                    map.put("id", item.getCodeitem());
                    map.put("name", item.getCodeitem() + ":" + item.getCodename());
                    fieldList.add(map);
                }
            }
        }

        return fieldList;
    }

    /**
     * 考勤假期管理保存计算公式
     * 
     * @param itemid
     *            指标id
     * @param formula
     *            公式内容
     * @param hoildayType
     *            假期类型
     * @param hoildayYear
     *            假期年份
     */
    public void saveKqFormula(String itemid, String formula, String hoildayType, String hoildayYear) throws GeneralException{
        RowSet rs = null;
        try {
            String code = "UN";
            KqVer kqVer = new KqVer();
            // 61574 新考勤不区分单位(0：老考勤；1：新考勤)
            if (!this.userview.isSuper_admin() && KqConstant.Version.STANDARD == kqVer.getVersion()) {
            	// 34276 保存计算公式应保存本人的单位部门
                KqPrivBo bo = new KqPrivBo(userview, conn);
                code = bo.getUNB0110();
            }

            StringBuffer sql = new StringBuffer();
            sql.append("select 1 from kq_parameter");

            StringBuffer where = new StringBuffer();
            where.append(" where b0110=?");
            where.append(" and name=?");
            ArrayList<String> valueList = new ArrayList<String>();
            valueList.add(code);
            valueList.add("REST_" + itemid.toUpperCase() + "_" + hoildayType + "_" + hoildayYear);
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString() + where.toString(), valueList);
            if (rs.next()) {
                StringBuffer update = new StringBuffer();
                update.append("update kq_parameter");
                update.append(" set content=?");
                valueList.add(0, formula);
                dao.update(update.toString() + where.toString(), valueList);
            } else {
                StringBuffer insert = new StringBuffer();
                // 34610 新增公式SQL insert 缺少into
                insert.append("insert into kq_parameter ");
                insert.append(" (b0110,name,description,content,status) ");
                insert.append(" values (?,?,?,?,?)");
                valueList.clear();
                valueList.add(code);
                valueList.add("REST_" + itemid.toUpperCase() + "_" + hoildayType + "_" + hoildayYear);
                String hoildayTypeName = AdminCode.getCodeName("ZU", hoildayType);
                valueList.add(hoildayTypeName + "计算公式");
                valueList.add(formula);
                valueList.add("1");
                dao.insert(insert.toString(), valueList);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }finally{
            PubFunc.closeResource(rs);
        }
    }

}
