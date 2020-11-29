package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.org.orgpre.OrgPreBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class SaveUpdateTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String name=(String)hm.get("position_set_table");
		cat.debug("table name="+name);
		ArrayList list=(ArrayList)hm.get("position_set_record");
		
		PosparameXML pos = new PosparameXML(this.frameconn);  
		String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
		ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
		String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag"); 
		ArrayList methodlist = pos.getMethodChildList(PosparameXML.AMOUNTS,setid);
		String levelnext = pos.getValue(PosparameXML.AMOUNTS,"nextlevel"); 
		levelnext=levelnext!=null&&levelnext.trim().length()>0?levelnext:"0";
		
		FieldSet fs = DataDictionary.getFieldSetVo(name);
		String changeflag = fs.getChangeflag();
		changeflag = changeflag= "".equals(changeflag)?"0":changeflag;
		
		String ctrl_type ="1";

		// 当子集权限为读，指标为写时可以修改。注掉此子集权限判断
		//String fieldPri = this.userView.analyseTablePriv(name);
		//if(!fieldPri.equals("2"))
		//	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.update.record.competence")+"！"));
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			int[] updatevalue = new int[planitemlist.size()];
			String B0110 = "";
			String codeid="";
			OrgPreBo orgprebo = new OrgPreBo(this.frameconn,this.userView);
			String B0110str="";
			ArrayList saveVoList = new ArrayList();
			for(int i=0;i<list.size();i++){
				RecordVo vo=(RecordVo)list.get(i);
				
				//一般变化子集不控制
				if(!"0".equals(changeflag)){
				   //控制年月标示 和 次数不能为空 by gdd 13-7-9
					String dateflag = vo.getString(name.toLowerCase()+"z0");
					String numflag = vo.getString(name.toLowerCase()+"z1");
					dateflag = dateflag==null?"":dateflag;
					numflag = numflag==null || numflag==""?"0":numflag;
					float itemcount = Float.parseFloat(numflag);
					if(dateflag.length()<1){
						String codeitemdesc = DataDictionary.getFieldItem(name.toLowerCase()+"z0").getItemdesc();
						throw GeneralExceptionHandler.Handle(new Exception("请填写"+codeitemdesc+"！"));
					}
					else if(itemcount<1){
						String codeitemdesc = DataDictionary.getFieldItem(name.toLowerCase()+"z1").getItemdesc();
						throw GeneralExceptionHandler.Handle(new Exception("请填写"+codeitemdesc+"！"));
					}
				}
				//控制结束
				
				
				B0110 = vo.getString("b0110");
				B0110=B0110!=null?B0110:"";

				String codename = AdminCode.getCodeName("UN",B0110);
				codename=codename!=null&&codename.trim().length()>0?codename:"";
				if(codename.trim().length()<1){
					codename = AdminCode.getCodeName("UM",B0110);
					codeid="UM";
				}else{
					codeid="UN";
				}
				
				String spflag = vo.getString(sp_flag.toLowerCase());
				if("02".equals(spflag))
					//为已报批或已批审核状态的数据未能参与计算 不需要前台提示直接跳过即可
					continue;
				if("03".equals(spflag))
					//为已批状态，不能修改，只能修改起草、驳回状态的记录 不需要前台提示直接跳过即可
					continue;
				else if(StringUtils.isBlank(spflag))//有可能保存的时候没有状态，这样将状态置为起草[27930]
					vo.setString(sp_flag.toLowerCase(), "01");
					
					
				String a_code = (String) this.getUserView().getHm().get("a_code");
				for(int j=0;j<planitemlist.size();j++){
					String planitem=planitemlist.get(j).toString(); 
					String method=methodlist.get(j).toString(); 
					int values = vo.getInt(planitem.toLowerCase());
					updatevalue[j]+=values;
						
					codename=codename!=null&&codename.trim().length()>0?codename:orgprebo.orgCodeName(B0110);
					if(!"1".equals(method)&&!orgprebo.planDownPerson(planitem,B0110,values)&&
							(("1".equals(levelnext)&&B0110.equalsIgnoreCase(a_code))||"0".equals(levelnext))){//如果“是否只控制下一级机构”参数设置了是，那么判断也判断到两级，维护第二级的时候不判断第三级了 zhaoxg add 2016-12-12
						FieldItem fielditem = DataDictionary.getFieldItem(planitem);
						if("1".equals(ctrl_type)){
							throw GeneralExceptionHandler.Handle(new Exception(codename+"的["+fielditem.getItemdesc()+"]小于下级单位或部门["+fielditem.getItemdesc()+"]！"));
						}else{
							throw GeneralExceptionHandler.Handle(new Exception(codename+"的["+fielditem.getItemdesc()+"]小于下级单位或部门["+fielditem.getItemdesc()+"]！"));
						}
					}
				}
				if("K".equalsIgnoreCase(name.substring(0,1))){
					String E0122 = vo.getString("e0122");
					E0122=E0122!=null?E0122:"";
					String E01A1 = vo.getString("e01a1");
					E01A1=E01A1!=null?E01A1:"";
					if(E0122.indexOf(B0110)==-1&&E0122.length()>0)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.depart")+"！"));//单位里面不包含此部门,请重新选择部门
					if(E01A1.indexOf(E0122)==-1&&name.indexOf("A01")!=-1&&E01A1.length()>0)
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.includes.job")+"！"));//部门里面不包含此岗位，请重新选择岗位
				}
				
				//如果没有b0100的编制数据，则添加一条空记录
				if(chRecord(dao,name,B0110)){
					//添加一条空记录
					String i9999 = initSubSetValue(name,B0110,"add");
					
					//一般变化子集不添加
					if(!"0".equals(changeflag))
						//初始化时添加时间和次数字段，否则检查不通过后数据不显示了
					    initZ0item(name,B0110);
					
					vo.setString("i9999", i9999);
				}
				
				saveVoList.add(vo);
				B0110str +="'"+B0110+"',";
			}
			
			if(StringUtils.isNotEmpty(B0110str) && B0110str.endsWith(","))
				B0110str = B0110str.substring(0, B0110str.length() - 1);
			
			if("1".equals(ctrl_type)){
				CodeItem codeitem = AdminCode.getCode(codeid,B0110);
				if(codeitem!=null){
					String parentid  = codeitem.getPcodeitem();
					for(int j=0;j<planitemlist.size();j++){
						String planitem=planitemlist.get(j).toString(); 
						String method=methodlist.get(j).toString(); 
						if(!"1".equals(method)&&!orgprebo.planUpPerson(parentid,planitem,B0110,updatevalue[j],B0110str)){
							FieldItem fielditem = DataDictionary.getFieldItem(planitem);
							throw GeneralExceptionHandler.Handle(new Exception("当前单位或部门["+fielditem.getItemdesc()+"]大于上级单位或部门["+fielditem.getItemdesc()+"]！"));
						}
					}
				}
			}
			
			
			dao.updateValueObject(saveVoList);
		}catch(Exception ex){
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private boolean chRecord(ContentDAO dao,String tablename,String fieldvalue){
		boolean chk = true;
		String itemid = "B0110";
		if("K".equalsIgnoreCase(tablename.substring(0,1))){
			itemid = "E01A1";
		}
		StringBuffer buf = new StringBuffer();
		buf.append("select "+itemid+" from ");
		buf.append(tablename);
		buf.append(" where "+itemid+"='"+fieldvalue+"'");
		try {
			RowSet rs = dao.search(buf.toString());
			if(rs.next())
				chk = false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return chk;
	}
	
	private void initZ0item(String tableset,String b0110){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    String sql = " update "+tableset+" set "+tableset+"z1=1, "+tableset+"z0 ="+Sql_switcher.dateValue(sdf.format(new Date()));	
	    if("K".equalsIgnoreCase(tableset.substring(0,1))){
	    	sql += " where e01a1='"+b0110+"'";
	    }else{
	    	sql += " where b0110='"+b0110+"'";
	    }
	    
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try {
			dao.update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String  initSubSetValue(String setname,String setvalue,String checkadd)throws GeneralException {
		GzDataMaintBo gzbo = new GzDataMaintBo(this.getFrameconn());
		String itemid = "";
		String infor = "2";
		String i9999= "";
		if(!"K".equalsIgnoreCase(setname.substring(0,1))){
			itemid="B0110";
			i9999=gzbo.insertSubSet2(setname,itemid,setvalue,checkadd,infor);
		}else{
			itemid="E01A1";
			infor = "3";
			String e0122 = codeItemid(setvalue,"UM");
			String b0110 = codeItemid(e0122,"UN");
			i9999=gzbo.insertSubSet2(setname,itemid,setvalue,"E0122",e0122,checkadd,infor);
		}
		return i9999;
	}
	private String codeItemid(String code,String codesetid){
		String codeitemid="";
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codeitemid,codesetid,parentid from organization where codeitemid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(strsql.toString());
			if(this.frowset.next()){		
				String codeid =this.frowset.getString("codeitemid");
				String setid =this.frowset.getString("codesetid");
				String paprentid =this.frowset.getString("parentid");
				if(setid.equalsIgnoreCase(codesetid)){
					codeitemid=codeid;
				}else{
					codeitemid = codeItemid(paprentid,codesetid);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return codeitemid;
	}
}
