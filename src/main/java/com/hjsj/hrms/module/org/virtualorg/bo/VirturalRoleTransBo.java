package com.hjsj.hrms.module.org.virtualorg.bo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.transaction.param.GetFieldBySetNameTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class VirturalRoleTransBo {
	private Connection conn;
	private UserView userView;
	private String virtualAxx;

	public VirturalRoleTransBo(Connection conn){
		this.conn=conn;
		this.virtualAxx = SystemConfig.getPropertyValue("virtualOrgSet");
	}

	public VirturalRoleTransBo(Connection conn,UserView userview){
		this.conn=conn;
		this.userView=userview;
		this.virtualAxx = SystemConfig.getPropertyValue("virtualOrgSet");
	}


	public ArrayList<ColumnsInfo> getColunmList(String code){

		ArrayList<ColumnsInfo> columnsInfo = new ArrayList<ColumnsInfo>();

		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);

		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name","1");

		if(onlyname!=null&&!"".equals(onlyname)&&!"A0101".equalsIgnoreCase(onlyname)){
			String onlyViewName = "";
			GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
			ArrayList<CommonData> chklist = gf.getUsedFieldBySetNameTrans("A01",this.userView);
			for(CommonData comDa:chklist){
				if(comDa.getDataValue().equals(onlyname)){ 
					onlyViewName = (comDa.getDataName()).split(":")[1];break;
				}
			}
			ColumnsInfo onlyName = new ColumnsInfo();
			onlyName.setColumnId("onlyname");
			onlyName.setColumnDesc(onlyViewName);
			onlyName.setEditableValidFunc("false");
			columnsInfo.add(onlyName);
		}

		ColumnsInfo A0100 = new ColumnsInfo();
		A0100.setColumnId("A0100");
		A0100.setColumnDesc("人员编号");
		A0100.setEncrypted(true);
		A0100.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		A0100.setEditableValidFunc("false");
		columnsInfo.add(A0100);
		
		ColumnsInfo i9999 = new ColumnsInfo();
		i9999.setColumnId("i9999");
		i9999.setColumnDesc("排序号");
		i9999.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		columnsInfo.add(i9999);
		
		ColumnsInfo nbase = new ColumnsInfo();
		nbase.setColumnId("NBASE");
		nbase.setColumnDesc("人员库前缀");
		nbase.setEncrypted(true);
		nbase.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);// 不显示
		nbase.setEditableValidFunc("false");
		columnsInfo.add(nbase);

		ColumnsInfo name = new ColumnsInfo();
		name.setColumnId("A0101");//姓名
		name.setColumnDesc("姓名");
		name.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);// 显示
		name.setEditableValidFunc("false");
		columnsInfo.add(name);

		ArrayList columlist=DataDictionary.getFieldList(virtualAxx,1);
		for (int i = 0; i < columlist.size(); i++) {
			FieldItem item=(FieldItem)columlist.get(i);
			String itemid=item.getItemid();
			ColumnsInfo infos=new ColumnsInfo(item);
			if(itemid.equalsIgnoreCase(virtualAxx+"01") || itemid.equalsIgnoreCase(virtualAxx+"02")){
				infos.setEditableValidFunc("false");
			}
			if(itemid.equalsIgnoreCase(virtualAxx+"03") && (code!=null && code.length()>0)){
				infos.setVorg(true);
				infos.setParentidFn("globalVirOrg.getParentId");
			}
			columnsInfo.add(infos); 
		}
		return columnsInfo;
	}

	/**
	 * 4.5.	信息浏览-查看虚拟组织成员sql
	 * 根据当前登录用户的库权限 选中某个组织机构
	 * orgid  选择树机构代码
	 * flag  显示当前机构所有人员 true选中 false 不选中
	 * */
	public String getVirturalColumsql(String code,boolean flag){
		String virtualAxx = SystemConfig.getPropertyValue("virtualOrgSet");
	    ArrayList fields = DataDictionary.getFieldList(virtualAxx, Constant.USED_FIELD_SET);
		ArrayList dblist=this.userView.getPrivDbList();
		for (int i = 0; i < dblist.size(); i++) {
			//虚拟机构不查招聘库将招聘库移除
			if(dblist.get(i).equals(getZPdbname())) {
				dblist.remove(i);
			}
		}
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name","1");
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select 'Usr' as nbase,UsrA01.A0100,UsrA01.A0000,UsrA01.A0101,");
		if(onlyname!=null&&!"".equals(onlyname)&&!"A0101".equalsIgnoreCase(onlyname))
			sqlStr.append("UsrA01.").append(onlyname).append(" onlyname,");
		for(int i=0;i<fields.size();i++){
			FieldItem f = (FieldItem)fields.get(i);
			sqlStr.append("A.").append(f.getItemid()).append(",");
			
		}
		sqlStr.append(" A.i9999  from Usr").append(virtualAxx).append(" A,UsrA01 where UsrA01.A0100=A.A0100");
		if(flag)
			sqlStr.append(" and (A."+virtualAxx+"01 like '").append(code).append("%' or A."+virtualAxx+"02 like '").append(code).append("%' or A."+virtualAxx+"03 like '").append(code).append("%' ) ");
		else
			sqlStr.append(" and (A."+virtualAxx+"01 = '").append(code).append("' or A."+virtualAxx+"02 = '").append(code).append("' or A."+virtualAxx+"03 = '").append(code).append("' ) ");	
		String sql = sqlStr.toString();
		sqlStr.setLength(0);
		for (int i = 0; i < dblist.size(); i++) {
			String nbase=(String)dblist.get(i);
			String newSql = sql.replace("Usr", nbase);
			sqlStr.append(newSql).append(" union all ");
		}
		sqlStr.delete(sqlStr.length()-10, sqlStr.length());
		return sqlStr.toString();
	}

	public void addNewMember(ArrayList<MorphDynaBean> al,String code){
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			
			
			String vUnit = "";
			String vDept = "";
			String vPos = "";
			int key = 40;
			CodeItem codeItem = AdminCode.getCode("@K", code);
			findUp:if(codeItem!=null){
				vPos = code;
				vDept = codeItem.getPcodeitem();
				codeItem = AdminCode.getCode("UN", vDept);
				if(codeItem!=null){
					vUnit = codeItem.getCodeitem();
					break findUp;
				}
				String parentid = AdminCode.getCode("UM", vDept).getPcodeitem();
				while(true && key>1){
					codeItem = AdminCode.getCode("UN",parentid);
					if(codeItem!=null){
						vUnit = codeItem.getCodeitem();
						break;
					}
					parentid = AdminCode.getCode("UM",parentid).getPcodeitem();
					key--;
				}
			}else{
				codeItem = AdminCode.getCode("UM", code);
				if(codeItem!=null){
					vDept = code;
					String parentid = AdminCode.getCode("UM", vDept).getPcodeitem();
					while(true && key>1){
						codeItem = AdminCode.getCode("UN",parentid);
						if(codeItem!=null){
							vUnit = codeItem.getCodeitem();
							break;
						}
						parentid = AdminCode.getCode("UM",parentid).getPcodeitem();
						key--;
					}
				}else{
					vUnit = code;
					vDept = code;
				}
			}
			
            
			ArrayList<RecordVo> voList = new ArrayList();
			String getA0100List = this.getA0100List(code);
			for(MorphDynaBean dyBean:al){
				String nbaseA0100 =  PubFunc.decrypt((String)dyBean.get("id"));
				String nbase = nbaseA0100.substring(0,3);
				String A0100 = nbaseA0100.substring(3);
				if(getA0100List.contains(nbaseA0100))
					continue;
				RecordVo vo = new RecordVo(nbase+virtualAxx);
				vo.setString("a0100", A0100);
				vo.setString(virtualAxx.toLowerCase()+"01", vUnit);
				vo.setString(virtualAxx.toLowerCase()+"02", vDept);
				vo.setString(virtualAxx.toLowerCase()+"03", vPos);
				vo.setInt("i9999", getMaxI9999(A0100,nbase));
				voList.add(vo);
			}
			
			dao.addValueObject(voList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getA0100List(String code){
		ContentDAO dao = new ContentDAO(this.conn);
		String a0100List = "";
		try {
			ArrayList al = this.userView.getPrivDbList();
			StringBuffer sql = new StringBuffer();
			String connSg = "+";
			if(Sql_switcher.searchDbServer()== Constant.ORACEL)
				connSg ="||";
			for(int i=0;i<al.size();i++){
				sql.append(" union select '"+al.get(i)+"'"+connSg+"A0100 nba from "+al.get(i)+virtualAxx+" where "+virtualAxx+"01='"+code+"' ");
			}
			RowSet rs = null;
			rs = dao.search(sql.substring(6));
			while(rs.next()){
				a0100List+=rs.getString("nba")+",";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a0100List;
	}
	
	private int getMaxI9999(String A0100,String nbase){
		String sql = "select max(i9999) from "+nbase+virtualAxx+" where a0100='"+A0100+"'";
		RowSet rs = null;
		int i  = 0;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs =dao.search(sql);
			if(rs.next())
			i = rs.getInt(1);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return i+1;
	}
	
	/**
	 * 保存虚拟机构
	 * xus 16/11/23
	 */
	public void saveMember(ArrayList<MorphDynaBean> al){
		try {
			ArrayList<RecordVo> voList = new ArrayList();
			ArrayList columlist=DataDictionary.getFieldList(virtualAxx,1);
			for(MorphDynaBean dyBean:al){
				String A0100 =  PubFunc.decrypt((String)dyBean.get("a0100_e"));
				String nbase =  PubFunc.decrypt((String)dyBean.get("nbase_e"));
				String i9999 = (String)dyBean.get("i9999");
				RecordVo vo = new RecordVo(nbase+virtualAxx);
				for(int i=0; i<columlist.size(); i++){
					FieldItem item = (FieldItem)columlist.get(i);
					String itemid = item.getItemid();
					
					//zxj 20170926 31867 不是所有数据都能强转成String的，所有用toString，避免数值型强转报错
					String value = null;
					if(null != dyBean.get(itemid))
					    value = dyBean.get(itemid).toString();
					
					if(value==null || "".equals(value.trim())){
						if("D".equals(item.getItemtype()))
							vo.setDate(item.getItemid().toLowerCase(), (String)null);
						else
							vo.setString(item.getItemid().toLowerCase(), (String)null);
					}else{
						if("D".equals(item.getItemtype())){
							int itemlength=item.getItemlength();
							String dateFormat="yyyy-MM-dd HH:mm:ss";
							switch (itemlength) {
							case 4:
								dateFormat="yyyy";
								break;
							case 7:
								dateFormat="yyyy-MM";
								break;
							case 10:
								dateFormat="yyyy-MM-dd";
								break;
							case 16:
								dateFormat="yyyy-MM-dd HH:mm";
								break;
							case 19:
								dateFormat="yyyy-MM-dd HH:mm:ss";
								break;
								
							default:
								break;
							}
							SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
							vo.setDate(item.getItemid().toLowerCase(),DateUtils.getSqlDate(sdf.parse( value.split("`")[0])));
						}else
							vo.setString(item.getItemid().toLowerCase(), value.split("`")[0]);
					}
				}
				vo.setString("a0100", A0100);
				vo.setString("i9999",i9999);
				voList.add(vo);
			}
			ContentDAO dao = new ContentDAO(this.conn);
			dao.updateValueObject(voList);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	
	/**
	 *删除虚拟机构人员 
	 * xus
	 * 16/11/24
	 */
	public void deleteMember(HashMap hm){
		String virtualAxx = SystemConfig.getPropertyValue("virtualOrgSet");
		ArrayList<MorphDynaBean> datas = (ArrayList)hm.get("datas");
		try {
			for(MorphDynaBean bean :datas){
				ArrayList al = new ArrayList();
				StringBuffer sql = new StringBuffer();
				sql.append("delete from ");
				sql.append(PubFunc.decrypt((String)bean.get("nbase"))+virtualAxx);
				sql.append(" where a0100 =? ");
				sql.append(" and i9999 =? ");
				al.add(PubFunc.decrypt((String)bean.get("a0100")));
				al.add((String)bean.get("i9999"));
				ContentDAO dao = new ContentDAO(this.conn);
				dao.delete(sql.toString(), al);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}


	/**
	 * 
	 * @Title: getZPdbname   
	 * @Description:    
	 * @param @return 
	 * @return String    
	 * @throws
	 * 查询招聘库
	 */
	public String getZPdbname(){

		String sql="select Str_Value from Constant where Constant='ZP_DBNAME'";
		ContentDAO dao=new ContentDAO(this.conn);
		String zp_dbname="";
		RowSet row=null;
		try {
			row=dao.search(sql);
			while (row.next()) {
				zp_dbname=row.getString("Str_Value");

			}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			PubFunc.closeResource(row);
		}
		return zp_dbname;
	}
	

	/**
	 * 添加根节点
	 * parentid 父节点
	 * codeitemid 选中的节点 changxy
	 * */
	public boolean addRoleTrans(ArrayList<MorphDynaBean> al){
		ContentDAO dao=new ContentDAO(this.conn);
		HashMap CodeitemIdMap=new HashMap();
		//获取登录用户所属机构id
		String orgid=this.userView.getUserOrgId();
		try {
			for (MorphDynaBean dybean :al) {
				String oldParentid=(String)dybean.get("parentid");
				String newParentid="";
				String codeitemid="";
				String id=(String)dybean.get("id");
				if(id.equals(oldParentid)){//创建顶级节点
					codeitemid=getMaxCodeItemid(oldParentid,true);
					newParentid=codeitemid;
					CodeitemIdMap.put((String)dybean.get("id"), codeitemid);//key ：旧id value ：新codeitemid
				}else if(!id.equals(oldParentid)&&"3".equals((String)dybean.get("type"))){
					newParentid=(String)CodeitemIdMap.get((String)dybean.get("parentid"));
					codeitemid=getMaxCodeItemid(newParentid, false);
					CodeitemIdMap.put((String)dybean.get("id"), codeitemid);
					/*newParentid=(String)CodeitemIddMap.get((String)dybean.get("id"));//当前节点的id
					parentMap.put((String)dybean.get("parentid"), newParentid);*/
				}else if(!id.equals(oldParentid)&&"2".equals((String)dybean.get("type"))){
					newParentid=(String)dybean.get("parentid");
					codeitemid=getMaxCodeItemid(newParentid, false);
					CodeitemIdMap.put((String)dybean.get("id"), codeitemid);
				}
				
				RecordVo vo=new RecordVo("codeitem");
				vo.setString("codesetid", "83");
				vo.setString("codeitemid",codeitemid);
				vo.setString("codeitemdesc", (String)dybean.get("text"));
				vo.setString("parentid", newParentid);
				vo.setString("childid", codeitemid);
				vo.setInt("flag", 1);//int
				vo.setString("b0110", (getOrgId(newParentid)==null||"".equals(getOrgId(newParentid)))?orgid:getOrgId(newParentid));
				vo.setInt("invalid", 1);//int
				vo.setInt("layer", (Integer)dybean.get("layer"));//int层级
				vo.setDate("start_date","1949-10-01 00:00:00.000");
				vo.setDate("end_date","9999-12-31 00:00:00.000");
				dao.addValueObject(vo);
				CodeItem item=new CodeItem("83",codeitemid);//向数据字典添加一条
				AdminCode.addCodeItem(item);	
			}
			
			
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除节点操作
	 * changxy
	 * */
	public boolean delVirRole(String codeitemid){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet set=null;
		try {
			String seachsql="select parentid,codeitemid from codeitem where codesetid='83' and codeitemid='"+codeitemid+"'";
			set=dao.search(seachsql);
			String parentid="";
			while (set.next()) {
				parentid=set.getString("codeitemid");
			}
			//String sqlcodeitem="delete codeitem where codesetid='83' and parentid='"+codeitemid+"'";
			if(!(parentid==null||"".equals(parentid))){
				String sql="delete codeitem where codesetid='83' and codeitemid like '"+parentid+"%'";
					dao.update(sql);
			}
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(set);
		}
		return false;
	}
	/**
	 * 修改虚拟角色节点名称操作
	 * */
	public boolean updateVirRole(ArrayList<MorphDynaBean> al){
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			for (MorphDynaBean dybean :al) {
				String sql="update codeitem set codeitemdesc='"+dybean.get("text")+"' where codeitemid='"+dybean.get("id")+"' and codesetid='83'";
			   dao.update(sql);
			}
			return true;
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return false;
	}
	/**
	 * 
	 * */
	public String getMaxCodeItemid(String parentid,boolean isroot){
		StringBuffer sbf=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		String codeitemid="";
		if(isroot)//生成最大顶级节点
			  sbf.append("select max(codeitemid) from codeitem  where codesetid='83' and parentid=codeitemid");
	    else//最大节点
	    	sbf.append("select max(codeitemid) from codeitem  where codesetid='83' and parentid<>codeitemid and parentid="+parentid);
		
		try {
			rs=dao.search(sbf.toString());
			while(rs.next()){
	    		codeitemid=rs.getString(1);
	    	}
			if((codeitemid==null||"".equals(codeitemid))&&isroot)
				return "01";
			else if((codeitemid==null||"".equals(codeitemid))&&!isroot){
				return parentid+"01";
			}else{
				 String newid="";
				 if(codeitemid.length()>2){
					 newid= getMaxCodeItemid(codeitemid.substring(codeitemid.length()-2,codeitemid.length()));
					 return parentid+newid;
				 }else{
					 return getMaxCodeItemid(codeitemid);
				 }
			} 
				
		    /*if(!rs.next()&&isroot)//顶级节点为空时	
		    	return "01";
		    if(!rs.next()&&!isroot){
		    	return parentid+"01";
		    	
		    }else{
		    	rs.previous();
		    	while(rs.next()){
		    		codeitemid=rs.getString(1);
		    	}
		    }*/
		   
		   // if(codeitemid.length()>2){
		    	
		   // }
		   /* else{
		    	newid=getMaxCodeItemid(codeitemid);
		    	return newid;
		    }*/
			/*// 03
			Stering newid = ..........*/
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return "";
	
	}
	/**
	 * 计算83号码最大父节点
	 * */
	public String getMaxCodeItemid(String codeitem){
		//父节点从01-99-0A--9Z
		int fstIndex=0;
		int scdIndex=1;
		char[] buf=codeitem.toCharArray();
		
		if(buf[fstIndex]=='9' && buf[scdIndex]=='9'){//如果最大值是99，下一个是0A
			buf[fstIndex] ='0';
		    buf[scdIndex] = 'A';
		}else if(buf[fstIndex]=='9' && buf[scdIndex]=='Z'){//如果 最大值是9Z；说明已经是范围内最大值了,不能再直接加了
			return null;
		}else if(buf[scdIndex]<'9' || (buf[scdIndex]>='A' && buf[scdIndex]<'Z')){//如果最后一位是0~8 或者a~y，最后一位直接+1
			buf[scdIndex] = (char)(buf[scdIndex]+1);
		}else if(buf[scdIndex]=='Z'){//如果最后一位是Z，倒数第二位+1，倒数第一位从A开始
			buf[fstIndex] = (char)(buf[fstIndex]+1);
			buf[scdIndex] = 'A';
		}else if(buf[scdIndex]=='9'){//如果最后一位是9，倒数第二位+1，倒数第一位从0开始
			buf[fstIndex] = (char)(buf[fstIndex]+1);
			buf[scdIndex] = '0';
		}
		return String.valueOf(buf);
	}
	/**
	 * 每次新增一条数据要判断同级或者上级 b0110的权限是否大于当前操作人的权限
	 * 大于上级或同级权限 插入数据要存储上级或同级的
	 * */
	public String getOrgId(String parentid){
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		String b0110="";
		String sql="Select min(codeitemid),b0110 from codeitem where codesetid='83' and parentid='"+parentid+"' group by b0110";
		try {
			rs=dao.search(sql);
			while(rs.next()){
				b0110=rs.getString("b0110");
			}
			if(b0110!=null&&!"".equals(b0110)){
				return b0110;
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
}
