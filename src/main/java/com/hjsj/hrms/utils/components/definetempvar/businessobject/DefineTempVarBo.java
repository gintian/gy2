package com.hjsj.hrms.utils.components.definetempvar.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 项目名称 ：ehr7.x
 * 类名称：DefineTempVarBo
 * 类描述：定义临时变量bo
 * 创建人： lis
 * 创建时间：2015-10-28
 */
public class DefineTempVarBo {
	private Connection conn = null;
	/** 薪资类别号 */
	private UserView userview;

	public DefineTempVarBo() {}
	
	public DefineTempVarBo(Connection conn,UserView userview) {
		this.conn = conn;
		this.userview = userview;
	}
	
	/**
	 * 生成midvariable表的主键nid
	 * @return int 主键nid
	 * @throws GeneralException
	 */
	public int getid(Connection conn){
		ContentDAO dao = new ContentDAO(conn);
		String sqlstr = "select max(nid) as nid from midvariable";
		ArrayList dylist = null;
		int n=0;
		try {
			dylist = dao.searchDynaList(sqlstr);
			if(dylist!=null&&dylist.size()>0){
				for(Iterator it=dylist.iterator();it.hasNext();){
					DynaBean dynabean=(DynaBean)it.next();
					n=Integer.parseInt(dynabean.get("nid").toString())+1;
				}
			}
		} catch(GeneralException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return n;
	}
	
	/**
	 * 初始化临时变量
	 * @param salaryid 薪资类别id
	 * @param nflag 
	 * 		0：工资发放|保险核算
	 * 		2：报表
	 * 		1:工资变动或者日常管理
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList initTempVar(String salaryid,String nflag) throws GeneralException{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList querylist = new ArrayList();
			StringBuffer str = new StringBuffer();
			str.append("select nid,cname,chz,ntype,fldlen,flddec,cstate,templetid,(select codesetdesc from codeset where codesetid=midvariable.codesetid) as codesetdesc,codesetid,sorting ");
			str.append(" from midvariable where templetid=0 and(cstate=? or cstate is null) and nflag=?");
			str.append(" order by sorting");	
			querylist.add(salaryid);
			querylist.add(nflag);
			rs = dao.search(str.toString(),querylist);
			while(rs.next()){
				HashMap map = new HashMap();
				
				map.put("nid", rs.getString("nid"));
				map.put("cname", rs.getString("cname"));
				map.put("chz", rs.getString("chz"));
				String ntype = rs.getString("ntype");
				map.put("ntype", ntype);
				map.put("fldlen", rs.getString("fldlen"));
				if(!"1".equals(ntype))
					map.put("flddec", "0");
				else
					map.put("flddec", rs.getString("flddec"));
					
				String codeSetId = rs.getString("codesetid");
				if(StringUtils.isNotBlank(codeSetId) && !"0".equals(codeSetId))
					map.put("codesetid",rs.getString("codesetid"));
				map.put("cstate",SafeCode.encode(PubFunc.encrypt(rs.getString("cstate"))));
				map.put("sorting", rs.getString("sorting"));
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
	 * 人事异动-初始化临时变量
	 * @param tabid 人事异动模版id
	 * gaohy,2016-1-6
	 */
	public ArrayList initTemplateVar(String tabid) throws GeneralException{
		ArrayList list = new ArrayList();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList querylist = new ArrayList();
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select nid,cname,chz,ntype,fldlen,flddec,cstate,templetid,");
			sqlstr.append("(select codesetdesc from codeset where codesetid=midvariable.codesetid) as codesetdesc,codesetid,sorting ");
			sqlstr.append(" from midvariable where nflag=0 and templetId <> 0 and (templetId = ? or cstate = '1')");
			sqlstr.append(" order by sorting");
			querylist.add(tabid);
			rs = dao.search(sqlstr.toString(),querylist);
			while(rs.next()){
				HashMap map = new HashMap();
				
				map.put("nid", rs.getString("nid"));
				map.put("cname", rs.getString("cname"));
				map.put("chz", rs.getString("chz"));
				String ntype = rs.getString("ntype");
				map.put("ntype", ntype);
				map.put("fldlen", rs.getString("fldlen"));
				if(!"1".equals(ntype))
					map.put("flddec", "0");
				else
					map.put("flddec", rs.getString("flddec"));
				map.put("fldlen", rs.getString("fldlen"));
				map.put("flddec", rs.getString("flddec"));
				String codeSetId = rs.getString("codesetid");
				if(StringUtils.isNotBlank(codeSetId) && !"0".equals(codeSetId))
					map.put("codesetid",rs.getString("codesetid"));
				map.put("cstate",rs.getString("cstate"));
				map.put("sorting", rs.getString("sorting"));
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
	 * 根据nid获取midvariable表的公式
	 * @return String 公式
	 * @throws GeneralException
	 */
	public String cValue(String nid){
		String cvalue="";
		RowSet rowSet = null;
		try {
			if(StringUtils.isNotBlank(nid)){
				ContentDAO dao = new ContentDAO(this.conn);
				String sqlstr = "select cvalue from midvariable where nid=?";
				rowSet = dao.search(sqlstr,Arrays.asList(nid));
				if(rowSet.next()){
					cvalue=rowSet.getString("cvalue");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(rowSet);
		}
		return cvalue;
	}
	
	/**
	 * 获取以构库的子集
	 * @param infor_type  依据模板类型判断需要加载的指标型  1：人员 2： 单位 3： 岗位
	 * @return ArrayList 
	 * @throws GeneralException
	 */
	public ArrayList fieldListTemp(String nflag, String infor_type){//当nflag为4时，只需要单位信息集   
		ArrayList fieldsetlist = new ArrayList();
		try {
			ArrayList listset = new ArrayList();
			if(!"4".equals(nflag))
				listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET));
			listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET));
			if(!"4".equals(nflag))
				listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET));
				
			for(int i=0;i<listset.size();i++){
				 Map<String, String> map = new HashMap<String, String>();
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 if("2".equals(infor_type)){//单位
					 if(fieldset.getFieldsetid().toUpperCase().startsWith("A")||fieldset.getFieldsetid().toUpperCase().startsWith("K"))
						 continue; 
				 }
				 if("3".equals(infor_type)){//岗位
					 if(fieldset.getFieldsetid().toUpperCase().startsWith("A")||fieldset.getFieldsetid().toUpperCase().startsWith("B"))
						 continue; 
				 }
				 if(this.userview.analyseTablePriv(fieldset.getFieldsetid())==null)
					 continue;
				 if("0".equals(this.userview.analyseTablePriv(fieldset.getFieldsetid())))
					 continue;
				 map.put("id", fieldset.getFieldsetid());
				 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlist.add(map);
			}
			/**增加临时变量可以在下拉列表中显示xcs**/
			 Map<String, String> map = new HashMap<String, String>();
			 map.put("id", "tempvar");
			 map.put("name",ResourceFactory.getProperty("label.gz.variable"));
			 fieldsetlist.add(map);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return fieldsetlist;
	}
	
	/**
	 * 根据子集获取子标
	 * @param fieldsetid 表名
	 * @param uv  用户
	 * @param flag 模块调用标识
	 * @return
	 */
	public ArrayList getItemList(String fieldsetid,UserView uv){
		ArrayList list = new ArrayList();
		FieldSet fieldset=DataDictionary.getFieldSetVo(fieldsetid);
		ArrayList dylist = new ArrayList();
		
		if(fieldsetid!=null&&fieldsetid.length()>0){
			dylist = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		}
		Map<String, String> map = new HashMap<String, String>();
//		map.put("", "");
//		list.add(map);
		try {
			for(int i=0;i<dylist.size();i++){
				map = new HashMap<String, String>();
				FieldItem fielditem = (FieldItem)dylist.get(i);
				String itemid = fielditem.getItemid();
				String itemdesc = fielditem.getItemdesc();
				if("M".equalsIgnoreCase(fielditem.getItemtype()))
					continue;
				/*
				 * 【6127】自助服务/员工信息维护，计算，公式，选择指标，指标没有经过权限控制，不对。
				 * 修改为有读或写权限的指标才能显示     jingq add 2014.12.18
				 * r45 培训费用表培训 模块的指标不需要权限控制        chenxg  add 2014-01-05
				 */
				if(!"r45".equalsIgnoreCase(fieldsetid) && "0".equalsIgnoreCase(uv.analyseFieldPriv(itemid))){
					continue;
				}
				
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))
						||!"0".equals(fieldset.getChangeflag())){
					 map.put("id", itemid);
					 map.put("name", itemid.toUpperCase()+":"+itemdesc);
					list.add(map);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * @author lis
	 * @Description: 指标数据集合
	 * @date 2015-11-10
	 * @param type  入口，1：是薪资类别
	 * @param id 薪资类别id 或人事异动模版id
	 * @return
	 */
	public ArrayList itemList(String type,String id){
		RowSet rowSet = null;
		ArrayList itemlist = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select nid,cname,chz from midvariable");
		if("1".equals(type)){
		    //这个是 薪资类别的
		    strsql.append(" where templetid=0 and(cstate='"+id+"' or cstate is null) and nflag=0 order by sorting ");
		}else if("3".equals(type)){
			strsql.append(" where nflag=0 and templetId <> 0 and (templetId = '"+id+"' or cstate = '1') order by sorting");
		}
		CommonData dataobj1 = new CommonData("","");
		//itemlist.add(dataobj1);
		ContentDAO dao  = new ContentDAO(this.conn);
		try {
			rowSet = dao.search(strsql.toString());
			while(rowSet.next()){
				Map<String, String> map = new HashMap<String, String>();
				 map.put("id", rowSet.getString("nid"));
				 map.put("name", rowSet.getString("cname")+":"+rowSet.getString("chz"));
				 itemlist.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		
		return itemlist;
	}
	
	/**
	 * @author lis
	 * @Description: 代码数据集合
	 * @date 2015-11-10
	 * @return
	 */
	public ArrayList codeList(){
		ArrayList codelist = new ArrayList();
		String sqlstr = "select codesetid,codesetdesc,maxlength from codeset order by codesetid";
		ContentDAO dao  = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try {
			rowSet = dao.search(sqlstr);
			while(rowSet.next()){
				CommonData dataobj = new CommonData(rowSet.getString("codesetid"),
						rowSet.getString("codesetid")+":"+rowSet.getString("codesetdesc"));
				codelist.add(dataobj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return codelist;
	}
	
	/**
	 * @author lis
	 * @Description: 获得代码列表数据
	 * @date 2016-2-20
	 * @param itemid
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList codeListItem(String itemid) throws GeneralException{
		ArrayList list = new ArrayList();
		
		if(itemid==null||itemid.length()<1){
			CommonData dataobj = new CommonData("","");
			list.add(dataobj);
			return list;
		}
		
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		StringBuffer str_value=new StringBuffer();
		String codesetid ="";
		if(fielditem==null){
		    fielditem=getMidVariableList(itemid,"0");// getMidVariableList(conn);
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by a0000");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by codeitemid");
						}
						ArrayList dylist = null;
						ContentDAO dao = new ContentDAO(conn);

						dylist = dao.searchDynaList(sqlstr.toString());

						for(Iterator it=dylist.iterator();it.hasNext();){
							DynaBean dynabean=(DynaBean)it.next();
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							str_value.append("`"+codeitemid+"~"+codeitemdesc+"");
							CommonData dataobj = new CommonData(codeitemid,codeitemid+":"+codeitemdesc);
							list.add(dataobj);
						}
						if(list.size()==0){
							CommonData dataobj = new CommonData("","");
							list.add(0,dataobj);
						}
					}else{
						CommonData dataobj = new CommonData("","");
						list.add(dataobj);
					}
				}else{
					CommonData dataobj = new CommonData("","");
					list.add(dataobj);
				}
			}else{
				
				CommonData dataobj = new CommonData("","");
				list.add(dataobj);
				if("escope".equals(itemid)){
					dataobj = new CommonData("1","1"+":"+"离休人员");
					list.add(dataobj);
					dataobj = new CommonData("2","2"+":"+"退休人员");
					list.add(dataobj);
					dataobj = new CommonData("3","3"+":"+"内退人员");
					list.add(dataobj);
					dataobj = new CommonData("4","4"+":"+"遗嘱");
					list.add(dataobj);
				}
			}
			if(str_value.length()>0&&str_value.length()<10000){
				list.add(SafeCode.encode(str_value.substring(1)));
			}
			
		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	/**
	 * @author lis
	 * @Description: 从临时变量中取得对应指标列表
	 * @date 2016-2-20
	 * @param conn
	 * @param itemid
	 * @param nflag
	 * @return
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
            ContentDAO dao=new ContentDAO(conn);
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
	 * @author lis
	 * @Description: 根据薪资类别id得到薪资名称
	 * @date 2015-10-31
	 * @param salaryid 薪资类别id
	 * @return
	 */
	public String getSalaryName(String salaryid){
		String str = "";
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select cname from SALARYTEMPLATE where SALARYID=?";
			RowSet rs = dao.search(sql,Arrays.asList(salaryid));
			if(rs.next()){
				str = rs.getString("cname")==null?"": rs.getString("cname");
			}
		}catch(Exception e){
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return str;
	}
	/**
	 * @author gaohy
	 * @Description: 根据人事异动模版id得到模版名称
	 * @date 2016-1-7
	 * @param tabid 人事异动模版id
	 * @return
	 */
	public String getTempName(String tabid){
		String str = "";
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="select Name from Template_table where TabId=?";
			RowSet rs = dao.search(sql,Arrays.asList(tabid));
			if(rs.next()){
				str = rs.getString("Name")==null?"": rs.getString("Name");
			}
		}catch(Exception e){
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return str;
	}
}
