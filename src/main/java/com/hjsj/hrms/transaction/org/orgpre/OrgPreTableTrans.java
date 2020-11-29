package com.hjsj.hrms.transaction.org.orgpre;

import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
public class OrgPreTableTrans extends IBusiness {

	String year = Calendar.getInstance().get(Calendar.YEAR)+"";
	String month = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";
	String changeflag="0";
	
	public void execute() throws GeneralException {
	    try {
	        String unitId = this.userView.getUnit_id();
	        if(StringUtils.isEmpty(unitId) || "`".equalsIgnoreCase(unitId)) {
	            throw new GeneralException("", ResourceFactory.getProperty("org.orgpre.orgpretable.unitId.isEmpty"), "", "");
	        }
	        // 如果searchstr不为空，说明是条件查询后刷新页面后进来的
	        if(this.getFormHM().get("searchstr")!=null){
	            this.getFormHM().put("sqlstr", this.getFormHM().get("searchstr"));
	            this.getFormHM().put("searchstr", null);
	            return;
	        }
	        
	        /**
	         * 若修改查询语句，需注意条件查询类CreateSearchSqlTrans，searchstr是基于此sql语句生成的
	         */
	        HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	        PosparameXML pos = new PosparameXML(this.frameconn);  
	        String setid=pos.getValue(PosparameXML.AMOUNTS,"setid"); 
	        setid=setid!=null&&setid.trim().length()>0?setid:"";
	        FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
	        if(fieldset==null)
	            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.org.esta.notset")+"!"));
	        String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag"); 
	        this.doInitsp_flag(sp_flag, setid);
	        String realitem=""; 
	        ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
	        for(int i=0;i<planitemlist.size();i++){
	            realitem+=pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem")+",";
	        }
	        String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type"); 
	        ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
	        String flag = (String)this.getFormHM().get("flag");
	        flag=flag!=null&&flag.trim().length()>0?flag:"all";
	        
	        String levelnext = pos.getValue(PosparameXML.AMOUNTS,"nextlevel"); 
	        levelnext=levelnext!=null&&levelnext.trim().length()>0?levelnext:"0";
	        
	        String nextlevel = (String)hm.get("nextlevel");
	        nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
	        hm.remove("nextlevel");
	        
	        
	        String checkadd = (String)hm.get("checkadd");
	        checkadd=checkadd!=null&&checkadd.trim().length()>0?checkadd:"";
	        hm.remove("checkadd");
	        
	        Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	        String zwvalid=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"pos");
	        String pos_set="";
	        String pos_set_realitem="";
	        String pos_set_receptitem="";
	        RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",this.frameconn);
	        if(ps_workout_vo!=null){
	            String  ps_workout=ps_workout_vo.getString("str_value");
	            ps_workout=ps_workout!=null?ps_workout:"";
	            if(ps_workout.length()>0){
	                String strs[]=ps_workout.split("\\|");//K01|K0114,K0111
	                pos_set=strs[0];
	                String tmpitems="";
	                if(strs.length>1)
	                    tmpitems=strs[1];
	                String[] items = tmpitems.split(",");
	                if(items.length==2){
	                    pos_set_receptitem=items[0];
	                    pos_set_realitem=items[1];
	                }
	            }
	        }
	        if(pos_set.length()<2||pos_set_realitem.length()<2||pos_set_receptitem.length()<2)
	            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workdiary.message.pos.esta.notset")+"!"));
	        
	        String infor = (String)hm.get("infor");
	        infor=infor!=null&&infor.trim().length()>0?infor:"2";
	        hm.remove("infor");
	        
	        String unit_type = (String)hm.get("unit_type");
	        unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"3";
	        hm.remove("unit_type");
	        
	        String a_code = (String)hm.get("a_code");
	        a_code=a_code!=null?a_code:"";
	        hm.remove("a_code");
	        
	        this.getFormHM().put("a_code",a_code);
	        if(a_code.trim().length()>=2){
	            a_code=a_code.replace("UN","");
	            a_code=a_code.replace("UM","");
	        }
	        
	        String fieldPri = this.userView.analyseTablePriv(setid);
	        
	        ArrayList itemlist = new ArrayList();
	        
	        String cloumstr = "";
	        GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn,this.userView);
	        gzbo.setPageFlag("bz");
	        ArrayList list = gzbo.fieldList(setid);
	        StringBuffer sqlstr = new StringBuffer();
	        
	        
	        FieldItem item3=new FieldItem();
	        item3=new FieldItem();
	        item3.setFieldsetid(setid);
	        item3.setItemid("pospre1");
	        item3.setItemdesc(ResourceFactory.getProperty("workdiary.message.post.esta"));
	        item3.setItemtype("A");
	        item3.setCodesetid("0");
	        item3.setAlign("center");
	        item3.setReadonly(true);
	        itemlist.add(item3.cloneField());
	        sqlstr.append("select '' as pospre1,grade");
	        
	        String readonlyitem = this.condStat(setid);
	        for(int i=0;i<list.size();i++){
	            Field fielditem = (Field)list.get(i);
	            String pri = this.userView.analyseFieldPriv(fielditem.getName());
	            if("2".equals(pri))
	                fielditem.setReadonly(false);
	            else if("1".equals(pri))
	                fielditem.setReadonly(true);
	            else
	                fielditem.setVisible(false);
	            if(fielditem.getName().equalsIgnoreCase(sp_flag)){
	                fielditem.setLabel(ResourceFactory.getProperty("org.performance.status"));
	                fielditem.setReadonly(true);
	                itemlist.add(1,fielditem);
	            }else if(realitem.indexOf(fielditem.getName().toUpperCase())!=-1){
	                fielditem.setReadonly(true);
	                itemlist.add(fielditem);
	            }else if("I9999".equalsIgnoreCase(fielditem.getName())){
	                fielditem.setReadonly(true);
	                fielditem.setVisible(false);
	                itemlist.add(fielditem);
	            }else if("B0110".equalsIgnoreCase(fielditem.getName())){
	                fielditem.setReadonly(true);
	                fielditem.setVisible(true);
	                itemlist.add(fielditem);
	            }else{
	                if(readonlyitem.indexOf(fielditem.getName().toUpperCase())!=-1){
	                    fielditem.setReadonly(true);
	                }
	                itemlist.add(fielditem);
	            }
	            cloumstr+=fielditem.getName().toUpperCase()+",";
	        }
	        
	        if(setid!=null&&setid.trim().length()>0){
	            sqlstr.append(","+gzbo.vilStrOrg(setid,itemlist,false));	
	        }
	        
	        cloumstr=cloumstr.replace("B0110,","").replace("B0110","").trim();
	        cloumstr=cloumstr.replace("I9999,","").replace("I9999","").trim();
	        cloumstr=cloumstr.replace(sp_flag.toUpperCase()+",","").replace(sp_flag.toUpperCase(),"").trim();
	        cloumstr=cloumstr.replace("E0122,","").replace("E0122","").trim();
	        cloumstr=cloumstr.replace("E01A1,","").replace("E01A1","").trim();
	        
	        
	        
	        if(!fieldset.isMainset()){
	            item3.setFieldsetid(setid);
	            item3.setItemid("hispre");
	            item3.setItemdesc(ResourceFactory.getProperty("workdiary.message.history.esta"));
	            item3.setItemtype("A");
	            item3.setCodesetid("0");
	            item3.setReadonly(true);
	            item3.setAlign("center");
	            itemlist.add(item3.cloneField());
	            sqlstr.append(",'' as hispre");
	        }
	        
	        item3=new FieldItem();
	        item3.setFieldsetid(setid);
	        item3.setItemid("pospre");
	        item3.setItemdesc(ResourceFactory.getProperty("workdiary.message.post.esta"));
	        item3.setItemtype("A");
	        item3.setCodesetid("0");
	        item3.setAlign("center");
	        item3.setReadonly(true);
	        itemlist.add(item3.cloneField());
	        sqlstr.append(",'' as pospre");
	        
	        item3=new FieldItem();
	        item3.setFieldsetid(setid);
	        item3.setItemid("b0110name");
	        item3.setItemdesc(ResourceFactory.getProperty("hrms.b0110"));
	        item3.setItemtype("A");
	        item3.setCodesetid("0");
	        item3.setVisible(false);
	        item3.setReadonly(true);
	        itemlist.add(item3.cloneField());
	        sqlstr.append(",org.codeitemid as b0110name");
	        
	        this.getUserView().getHm().put("a_code", a_code);//这块记下，因为保存用的dataset标签里面的保存按钮，这个参数传不过去 zhaoxg add 2016-12-12
	        if("2".equals(nextlevel))
	            sqlstr.append(gzbo.whereStrOrg(setid,a_code));
	        else 
	            sqlstr.append(gzbo.whereStrOrg(setid,a_code,"1",ctrl_type,checkadd));
	        int i = sqlstr.length();
	        sqlstr.append(" and (I9999=(select max(I9999) from ");
	        sqlstr.append(setid);
	        sqlstr.append(" where ");
	        changeflag=fieldset.getChangeflag();
	        if(!"0".equals(changeflag)){
	            sqlstr.append(setid+"z0=(select max("+setid+"z0) from "+setid+" where B0110=a.b0110) and ");
	        }
	        sqlstr.append(" B0110=a.B0110 )");
	        sqlstr.append(" or a.B0110 is null)");
	        
	        sqlstr.append(" and org.codesetid in('UN','UM')");
	        if(fieldset.isMainset()){
	            if(!"all".equalsIgnoreCase(flag)){
	                sqlstr.append(" and "+sp_flag+"='"+flag+"'");
	            }
	        }else{
	            if(!"all".equalsIgnoreCase(flag)){
	                sqlstr.append(" and a."+sp_flag+"='"+flag+"'");
	            }
	        }
	        
	        if(!"0".equals(changeflag)){
	            sqlstr.append(" and ("+setid+"z0=(select max("+setid+"z0) from "+setid+" where B0110=a.b0110) or "+setid+"z0 is null)");
	        }
	        
	        String searchWhere = sqlstr.substring(i);
	        sqlstr.append(" order by a0000");
	        
	        //xuj 增加设置人员库 2010-5-1
	        /*	String view_scan = pos.getValue(PosparameXML.AMOUNTS,"orgpre"); 
		view_scan=view_scan!=null&&view_scan.trim().length()>1?view_scan:"Usr,";*/
	        
	        String getyear = (String)hm.get("yearnum");
	        hm.remove("yearnum");
	        getyear=getyear!=null&&getyear.length()>0?getyear:year+"";
	        String getmonth = (String)hm.get("monthnum");
	        hm.remove("monthnum");
	        getmonth=getmonth!=null&&getmonth.length()>0?getmonth:month+"";
	        this.getFormHM().put("monthnum", getmonth);
	        this.getFormHM().put("yearnum", getyear);
	        this.getFormHM().put("splist",spList());
	        this.getFormHM().put("sqlstr",sqlstr.toString());
	        this.getFormHM().put("itemlist",itemlist);
	        this.getFormHM().put("tablename",setid);
	        this.getFormHM().put("ctrl_type",ctrl_type);
	        this.getFormHM().put("setid",setid);
	        this.getFormHM().put("sp_flag",sp_flag);
	        this.getFormHM().put("realitem",realitem);
	        this.getFormHM().put("infor",infor);
	        this.getFormHM().put("unit_type",unit_type);
	        this.getFormHM().put("cloumstr",cloumstr);
	        this.getFormHM().put("flag",flag);
	        this.getFormHM().put("nextlevel",nextlevel);
	        this.getFormHM().put("levelnext", levelnext);
	        this.getFormHM().put("searchWhere", searchWhere);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } 
	}
	private ArrayList spList(){
		ArrayList list = new ArrayList();
		CommonData dataobj = new CommonData("all",ResourceFactory.getProperty("hire.jp.pos.all"));
		list.add(dataobj);
		dataobj = new CommonData("01",ResourceFactory.getProperty("hire.jp.pos.draftout"));
		list.add(dataobj);
		dataobj = new CommonData("02",ResourceFactory.getProperty("workdiary.message.apped"));
		list.add(dataobj);
		dataobj = new CommonData("03",ResourceFactory.getProperty("label.hiremanage.status3"));
		list.add(dataobj);
		dataobj = new CommonData("07",ResourceFactory.getProperty("edit_report.status.dh"));
		list.add(dataobj);
		return list;
	}
	
	/**
	 * 将null或''的审批情况字段默认为起草01
	 * @param sp_flag
	 */
	private void doInitsp_flag(String sp_flag,String setid){
		if(sp_flag!=null&&sp_flag.length()>0){
			String sql = "";
			if(Sql_switcher.searchDbServer()==1){//mssql
				sql ="update "+setid+" set "+sp_flag+"='01' where "+sp_flag+" is null or "+sp_flag+"=''";
			}else{
				sql ="update "+setid+" set "+sp_flag+"='01' where "+sp_flag+" is null";
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			try {
				dao.update(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获得年月标识时间
	 */
	private String getId(){
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9)
				return year+"."+month;
			else
				return year+".0"+month;
		}
	}
	
	private String condStat(String fieldsetid){
		String readonlyitem = "";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,Expression from fielditem where fieldsetid='");
		sqlstr.append(fieldsetid);
		sqlstr.append("'");
		ContentDAO dao = new ContentDAO(this.frameconn);
		RowSet rs = null;
		ArrayList statNumItemlist = new ArrayList();
		try {
			rs= dao.search(sqlstr.toString());
			while(rs.next()){
				String itemid = rs.getString("itemid");
				String Expression = rs.getString("Expression");
				Expression=Expression!=null?Expression:"";
				if(Expression.trim().length()>0){
					if("1".equals(Expression.substring(0,1))||
							"2".equals(Expression.substring(0,1))){
						readonlyitem+=itemid+",";
					}
					String exprArr[] = exprDecom(Expression);
					//设置了统计项球个数的公式的指标
					if(exprArr[0]!=null&& "2".equals(exprArr[0])&&exprArr[1]!=null&& "0".equals(exprArr[1])){
						statNumItemlist.add(itemid.toLowerCase());
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			this.getFormHM().put("statNumItemlist", statNumItemlist);
			try{
				if(rs!=null)
					rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return readonlyitem.toUpperCase();
	}
	
	private String[] exprDecom(String expr){
		expr=expr!=null?expr:"";
		String[] exprArr = expr.split("::");
		return exprArr;
	}
}
