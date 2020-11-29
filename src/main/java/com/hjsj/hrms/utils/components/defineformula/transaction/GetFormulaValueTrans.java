package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.businessobject.general.salarychange.ChangeFormulaBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.module.kq.holiday.businessobject.HolidayBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GetFormulaValueTrans 
 * 类描述：获取公式内容以及修改公式类型 
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 3:50:52 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 3:50:52 PM
 * 修改备注： 
 * @version
 */
public class GetFormulaValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String itemid=(String)hm.get("itemid"); 
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		
		String groupid=(String)hm.get("groupid");//公式组id
		groupid=groupid!=null&&groupid.length()>0?groupid:"";
		
		String itemname=(String) hm.get("itemname");
		itemname = itemname!=null&&itemname.length()>0?itemname:"";
		
		String id = (String)hm.get("id"); // 薪资类别id，或者人事异动模版Id
		id=id!=null&&id.length()>0?id:"";
		
		String runflag = (String)hm.get("runflag");
		runflag=runflag!=null&&runflag.length()>0?runflag:"";
		ContentDAO dao = new ContentDAO(this.frameconn);
		String module = (String)hm.get("module");
		String formulaType = (String)hm.get("formulaType");
		String hoildayType = (String)hm.get("hoildayType");
		String hoildayYear = (String)hm.get("hoildayYear");
		
		try {
			if("2".equals(formulaType)){
				//根据itemid字段和salaryid获取计算公式值
				String formulavalue = this.spFormulaValue(this.frameconn,id,itemid);
				hm.put("formulavalue",formulavalue);
			}else if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id)); //解码和解密
				if(runflag!=null&&runflag.length()>0){
					RecordVo vo=new RecordVo("salaryformula");
					vo.setInt("salaryid", Integer.parseInt(id));
					vo.setInt("itemid", Integer.parseInt(itemid));
					vo = dao.findByPrimaryKey(vo);	
					String updatesql = "update salaryformula set runflag="+runflag+" where salaryid="+id+" and itemid="+itemid;
					dao.update(updatesql);
				}else{
					String sql = "select runflag from salaryformula  where salaryid="+id+" and itemid='"+itemid+"'";
					RowSet rs = dao.search(sql);
					if(rs.next()){
						runflag = rs.getString("runflag");
					}
					hm.put("runflag",runflag);
					hm.put("standid",this.standId(this.frameconn,id,itemid));
				}
				String formulavalue = this.formulavalue(this.frameconn,id,itemid);//获取子标计算公式值
				
				hm.put("formulavalue",formulavalue);		
			}else if("3".equals(module)){//人事异动,gaohy
				if(itemname.length()>0){
					String seq=(String)hm.get("seq"); 
					String arrayvalues[]=formulaTempValue(id,groupid,itemname,seq).split("\\|");//根据点击的项目itemname获取计算公式
					String formulavalue=arrayvalues[0];
					String formulaItems=arrayvalues[1];
					hm.put("formulavalue",formulavalue);
					hm.put("formulaItems",formulaItems);
				}
			} else if("4".equals(module)){
				//考勤假期管理
			    if(StringUtils.isNotEmpty(hoildayType))
			        hoildayType = PubFunc.decrypt(hoildayType);
			    
			    String formulavalue = getKqFormula(itemname, hoildayType, hoildayYear);
			    hm.put("formulavalue", formulavalue);
			}
		} catch(SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 
	* <p>Description: 联动-根据点击的计算项目获得计算公式</p>
	* <p>Company: HJSOFT</p> 
	* @author gaohy
	* @date 2015-12-16 下午03:17:59
	 */
	public String  formulaTempValue(String tableid,String id,String itemname,String seq) throws GeneralException{
		tableid=tableid!=null&&tableid.trim().length()>0?tableid:"";//模版id
		id= id!=null&&id.length()>0?id:"";//公式组id
		
		
		/**判断用户是否拥有该模版资源的权限**/
        boolean isCorrect=false;
        if(this.userView.isHaveResource(IResourceConstant.RSBD,tableid))//人事移动
            isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.ORG_BD,tableid))//组织变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.POS_BD,tableid))//岗位变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.GZBD,tableid))//工资变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.INS_BD,tableid))//保险变动
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS_FG,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS_GX,tableid))
                isCorrect=true;
        if(!isCorrect)
            if(this.userView.isHaveResource(IResourceConstant.PSORGANS_JCG,tableid))
                isCorrect=true;
        if(!isCorrect){
            throw new GeneralException("当前用户不具有相应的权限");
        }
		ArrayList itemlist = new ArrayList();
		ArrayList affterlist = new ArrayList();
		String affteritem_arr = "";
		String stritem="";
		if(tableid.length()>0){
			TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
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
				if(fielditem.getVarible()==2){//去掉子集
						continue;
					}
				if ("start_date".equalsIgnoreCase(fielditem.getItemid())){
					//前台判断全部按下划线_，兼容,防止报错，保存的时候再替换回来。
					fielditem.setItemid("start*date");
				}
			//	if(fielditem.isChangeAfter()&&!fielditem.isMemo()){
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
			//	if(!fielditem.getItemid().equalsIgnoreCase("photo")&&!fielditem.getItemid().equalsIgnoreCase("ext")&&!fielditem.isMemo())
				if(!"photo".equalsIgnoreCase(fielditem.getItemid())&&!"ext".equalsIgnoreCase(fielditem.getItemid())&&!"attachment".equalsIgnoreCase(fielditem.getItemid()))
				{
					CommonData dataobj = new CommonData(fielditem.getItemid()+":"+itemdesc,itemdesc);
					itemlist.add(dataobj);
				}
			}
		}
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList templist = tempvarbo.getMidVariableList(this.getFrameconn(),tableid);
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
		ChangeFormulaBo formulabo = new ChangeFormulaBo();
		ContentDAO dao = new ContentDAO(this.frameconn);
		//得到计算公式
		String[] itemFactor = formulabo.getItem(dao,tableid,id,affterlist);
		item = itemFactor[0];
		item=item.replace("START_DATE", "START*DATE");
		item=item.replace("start_date", "start*date");
		//根据点击的项目itemname，获得对应的计算公式
		HashMap formulaTemps = new HashMap();
		String items[]=item.split("`");
		for (int i = 0; i < items.length; i++) {
		    /*以下取法不对
            String itemFormula[]=items[i].split("=");
            if(itemFormula.length==2&&itemFormula[1]!=null&&itemFormula[1].trim().length()>0){
                formulaTemps.put(itemFormula[0].substring(0, itemFormula[0].lastIndexOf("_")),itemFormula[1]);
            }else if(itemFormula.length==3){
                formulaTemps.put(itemFormula[0].substring(0, itemFormula[0].lastIndexOf("_")),itemFormula[1]+"="+itemFormula[2]);
            }else
                formulaTemps.put(itemFormula[0].substring(0, itemFormula[0].lastIndexOf("_")),"");
				*/
			String strItem=items[i];
			int k = strItem.indexOf("=");
			String itemid= strItem.substring(0,k);
			itemid = itemid.substring(0,itemid.lastIndexOf("_"));
//			itemid= itemid.substring(0, itemid.lastIndexOf("_"));
			String formula= strItem.substring(k+1,strItem.length());
			formulaTemps.put(itemid.toUpperCase(),formula);//bug 38580 itemid值为0_a1902，下面查找时toUpperCase导致查找不到对应的值无法返回计算公式
			
			
		}
		String formula=(String) formulaTemps.get(seq + "_" +  itemname.toUpperCase());
		if(StringUtils.isBlank(formula))
			formula = "";
		String formulaValue=formula+"|"+item;
		return formulaValue;
	}
	
	/**
	 * 根据itemid字段和salaryid获取子标计算公式值
	 * @return String 
	 * @throws GeneralException 
	 * 
	 */
	public String formulavalue(Connection conn,String salaryid,String itemid) throws GeneralException{
		try {
			String formula = "";
			ContentDAO dao = new ContentDAO(conn);
			String sqlstr = "select rexpr from salaryformula  where salaryid="+salaryid+" and itemid='"+itemid+"'";
			try {
				RowSet rs = dao.search(sqlstr);
				if(rs.next()){
					formula = rs.getString("rexpr");
					formula=formula!=null&&formula.length()>0?formula:"";
				}
			} catch(SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return formula;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * @Title: spFormulaValue 
	 * @Description: 根据itemid字段和salaryid获取计算公式值
	 * @param conn 数据连接
	 * @param salaryid 薪资类别id
	 * @param itemid 公式id
	 * @return String
	 * @author lis  
	 * @throws GeneralException 
	 * @date 2015-8-31 上午10:37:58
	 */
	public String spFormulaValue(Connection conn,String salaryid,String itemid) throws GeneralException{
		try {
			String formula = "";
			ArrayList list = new ArrayList();
			list.add(itemid);
			ContentDAO dao = new ContentDAO(conn);
			String sqlstr = "select formula from hrpchkformula  where chkid=?";
			try {
				RowSet rs = dao.search(sqlstr,list);
				if(rs.next()){
					formula = rs.getString("formula");
					formula=formula!=null&&formula.length()>0?formula:"";
				}
			} catch(SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
			return formula;
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 根据itemid字段和salaryid获取standid
	 * @return String 
	 * @throws GeneralException 
	 * 
	 */
	public String standId(Connection conn,String salaryid,String itemid) throws GeneralException{
		String standid = "";
		ContentDAO dao = new ContentDAO(conn);
		ArrayList list = new ArrayList();
		String sqlstr = "select standid from salaryformula  where salaryid=? and itemid=?";
		try {
			list.add(salaryid);
			list.add(itemid);
			RowSet rs = dao.search(sqlstr,list);
			if(rs.next()){
				standid = rs.getString("standid");
				standid=standid!=null&&standid.length()>0?standid:"";
			}
		} catch(SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return standid;
	}

    /**
     * 获取考勤假期管理中公式的具体内容
     * 
     * @param itemname
     *            指标id
     * @param hoildayType
     *            假期类型
     * @param hoildayYear
     *            假期年份
     * @return
     */
    public String getKqFormula(String itemname, String hoildayType, String hoildayYear) {
        String kqFormula = "";
        try {
        	// 34594 获取考勤计算公式方法优化
            HolidayBo bo = new HolidayBo(this.getFrameconn(), this.userView);
            kqFormula = bo.getParameter("", itemname, hoildayType, hoildayYear);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kqFormula;
    }
}
