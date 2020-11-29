/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 查询汇总数据
 * @author xujian
 * @version 1.0 2009-9-24
 */
public class QueryGzCollectTableDataTrans extends IBusiness {

	/**
	 * 
	 */
	public QueryGzCollectTableDataTrans() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		StringBuffer sqlview = new StringBuffer();
		String sum_fields_str = "";
		try{
		String salaryid=(String)this.getFormHM().get("salaryid");
		 
		//如果用户没有当前薪资类别的资源权限   20140903  dengcan
		CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
		safeBo.isSalarySetResource(salaryid,null);
		ArrayList fieldlist = (ArrayList)this.getFormHM().get("fieldlist");
		/**发放日期和发放次数*/
		String bosdate=(String)this.getFormHM().get("bosdate");
		String count=(String)this.getFormHM().get("count");
		sum_fields_str = (String)this.getFormHM().get("sum_fields_str");
		HashMap req = (HashMap) this.getFormHM().get("requestPamaHM");
		String a_code = (String)req.get("a_code");
		String sql = "";
		StringBuffer sqlview_self=new StringBuffer("");
		
		sqlview.delete(0, sqlview.length());
		sqlview.append("select b0110,"+sum_fields_str+",sp_flag from gz_sp_report where b0110 in(");
		sqlview_self.append("select b0110,"+sum_fields_str+",sp_flag from gz_sp_report where b0110 in(");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String temp = "";//每次合计在数据库中b0110存放的值
		if(a_code.length()>2){
			String codeitemid = a_code.substring(2,a_code.length());
	//		sqlview.append("'-1','sum"+codeitemid+"_0',");
			sqlview.append("'-2','sum"+codeitemid+"_0',");
			temp = "sum"+codeitemid+"_0";
			sql = "select codeitemid,codesetid from organization where parentid='"+codeitemid+"' and codeitemid<>parentid";
			sqlview_self.append("'"+codeitemid+"','sum"+codeitemid+"_0'");
		}else{//点击的根节点
			//sqlview.append("'0','sum_0',");
			sqlview.append("'-1','sum_0',");
			sqlview_self.append("'-1','sum_0'");
			temp = "sum_0";
			sql = "select codeitemid,codesetid from organization where codesetid='UN' and codeitemid=parentid";
		}
		this.frecset = dao.search(sql);
		String codesetid = "";
		while(this.frecset.next()){
			if("".equals(codesetid)){
				codesetid = this.frecset.getString("codesetid");
			}
			sqlview.append("'"+this.frecset.getString("codeitemid")+"',");
		}
		
		sqlview.delete(sqlview.length()-1,sqlview.length());
		sqlview.append(") and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3="+count+" and salaryid="+salaryid+" and userid='"+this.userView.getUserId()+"'");
		sqlview_self.append(") and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3="+count+" and salaryid="+salaryid+" and userid='"+this.userView.getUserId()+"'");
		sql = "delete from gz_sp_report where b0110='"+temp+"' and a00z2="+Sql_switcher.dateValue(bosdate)+" and a00z3="+count+" and salaryid="+salaryid+" and userid='"+this.userView.getUserId()+"'";
		dao.update(sql);
		RecordVo vo = new RecordVo("gz_sp_report");
		isLeaf=false; //不是叶子节点
		this.doCollect(dao, sqlview.toString(), sum_fields_str.split(","), vo, bosdate, count, temp, salaryid,sqlview_self.toString());
		sqlview.append(" order by b0110");
		
		
		if(isLeaf)
		{
			sqlview.setLength(0);
			sqlview.append(sqlview_self.toString());
		}
		
		if(!"".equals(codesetid)){
			for(int i=0;i<fieldlist.size();i++){
				Field field = (Field)fieldlist.get(i);
				if("b0110".equals(field.getName())){
					//field.setCodesetid(codesetid);
					field.setCodesetid("UM");
				}
			}
		}
		this.getFormHM().put("fieldlist", fieldlist);
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		 
		String  verify_ctrl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
		if(verify_ctrl==null||verify_ctrl.trim().length()==0) ////是否按审核条件控制
			verify_ctrl="0";
		if("1".equals(verify_ctrl))
		{
			String verify_ctrl_sp=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_sp");
			if(verify_ctrl_sp!=null&&verify_ctrl_sp.length()>0)
				verify_ctrl=verify_ctrl_sp;
		}
		this.getFormHM().put("verify_ctrl",verify_ctrl);
		
		String isControl=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"flag");   //该工资类别是否进行总额控制
		if("1".equals(isControl))
		{
			String amount_ctrl_ff=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.AMOUNT_CTRL,"amount_ctrl_ff");
			if(amount_ctrl_ff!=null&&amount_ctrl_ff.trim().length()>0)
				isControl=amount_ctrl_ff;
			
		}
		this.getFormHM().put("isTotalControl", isControl);
		
		
		String relation_id=gzbo.getSpRelationId();
		String  sp_actor_str="";
		if(relation_id.length()>0)
		{	 
			sp_actor_str=gzbo.getSpActorStr(relation_id);
		}
		if(sp_actor_str.length()>0)
		{
			String[] temps=sp_actor_str.split("`");
			if(temps.length==1)
			{
				temps=temps[0].split("##");
				this.getFormHM().put("spActorName",temps[1]);
			}
			else
				this.getFormHM().put("spActorName", "");
		}
		else
			this.getFormHM().put("spActorName", "");
		this.getFormHM().put("sp_actor_str", SafeCode.encode(sp_actor_str));
		this.getFormHM().put("relation_id",relation_id);
		
		String isSendMessage="0"; 
		if(gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"mail")!=null&& "1".equals(gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"mail")))
			isSendMessage="1";
		if(gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"sms")!=null&& "1".equals(gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.NOTE,"sms")))
			isSendMessage="1";
		this.getFormHM().put("isSendMessage", isSendMessage);
		
		}catch(Exception e){
			sqlview.delete(0, sqlview.length());
			sqlview.append("select b0110,"+sum_fields_str+",sp_flag from gz_sp_report where 1=2");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			String sql = sqlview.toString();
			this.getFormHM().put("sql", sqlview.toString());
		}
	}
	
	
	boolean isLeaf=false;
	/**
	 * 合计数据
	 * 
	 * @param dao
	 * @param sql
	 * @param sum_fields
	 *            汇总指标
	 * @param vo
	 *            gz_sp_report表RecordVo
	 * @param bosdate
	 *            发放日期
	 * @param count
	 *            发放次数
	 * @param key
	 *            汇总单位或部门
	 * @param salaryid
	 * @throws Exception
	 */
	private void doCollect(ContentDAO dao, String sql, String[] sum_fields,
			RecordVo vo, String bosdate, String count, String key,
			String salaryid,String sql_self) throws Exception {
		
		
		
		
		HashMap setMap=new HashMap();
		try
		{
			RowSet rowSet=dao.search("select itemlength,decwidth,itemdesc,itemid from salaryset where salaryid="+salaryid+" and upper(itemtype)='N'");
			LazyDynaBean abean=null;
			while(rowSet.next())
			{
				abean=new LazyDynaBean();
				abean.set("itemlength",rowSet.getString("itemlength")!=null?rowSet.getString("itemlength"):"0");
				abean.set("decwidth",rowSet.getString("decwidth")!=null?rowSet.getString("decwidth"):"0");
				abean.set("itemdesc",rowSet.getString("itemdesc"));
				abean.set("itemid",rowSet.getString("itemid"));
				setMap.put(rowSet.getString("itemid").toUpperCase(), abean);
			}
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		
		this.frecset = dao.search(sql);
		HashMap temp = new HashMap();// 用于保存个汇总字段的各个总和
		String sp_flag_temp = "";
		String sp_flag = "";
		boolean hasRecord = false;// 通过查询看看是否有满足汇总的记录
		while (this.frecset.next()) {
			hasRecord = true;
			for (int n = 0; n < sum_fields.length; n++) {
				String sum_field = sum_fields[n];
				try {
					temp
							.put(
									sum_field,
									(temp.get(sum_field) != null ? ((BigDecimal) temp
											.get(sum_field))
											: new BigDecimal(0))
											.add((this.frecset
													.getBigDecimal(sum_field))));
				} catch (Exception e) {
					temp.put(sum_field,
							(temp.get(sum_field) != null ? ((BigDecimal) temp
									.get(sum_field)) : new BigDecimal(0))
									.add(new BigDecimal(0)));
				}
			}
			if ("".equals(sp_flag)) {
				sp_flag = sp_flag_temp = this.frecset.getString("sp_flag");
			}
			sp_flag = this.frecset.getString("sp_flag");
		}
		
		if(!hasRecord)
		{
			isLeaf=true;
			this.frecset = dao.search(sql_self);
			while (this.frecset.next()) {
				hasRecord = true;
				for (int n = 0; n < sum_fields.length; n++) {
					String sum_field = sum_fields[n];
					try {
						temp
								.put(
										sum_field,
										(temp.get(sum_field) != null ? ((BigDecimal) temp
												.get(sum_field))
												: new BigDecimal(0))
												.add((this.frecset
														.getBigDecimal(sum_field))));
					} catch (Exception e) {
						temp.put(sum_field,
								(temp.get(sum_field) != null ? ((BigDecimal) temp
										.get(sum_field)) : new BigDecimal(0))
										.add(new BigDecimal(0)));
					}
				}
				if ("".equals(sp_flag)) {
					sp_flag = sp_flag_temp = this.frecset.getString("sp_flag");
				}
				sp_flag = this.frecset.getString("sp_flag");
			}
		}
		
		
		
		if (hasRecord) {
			if (!sp_flag.equalsIgnoreCase(sp_flag_temp)) {
				sp_flag = "02";// 选择单位或部门的人员薪资数据如果全部为已批状态，则汇总单位或部门记录为“已批”状态。如果全部为已驳回状态，则汇总单位或部门记录为“驳回”状态，否则汇总单位或部门记录为“已报批”状态。
			}

			vo.setDate("a00z2", bosdate.replaceAll("\\.", "-"));
			vo.setInt("a00z3", Integer.parseInt(count));
			vo.setString("b0110", key);
			vo.setString("userid", this.userView.getUserId());
			vo.setInt("salaryid", Integer.parseInt(salaryid));
//			try {
//				dao.findByPrimaryKey(vo);
//				vo.setString("sp_flag", sp_flag);
//				for (int n = 0; n < sum_fields.length; n++) {
//					String sum_field = sum_fields[n];
//					vo.setString(sum_field.toLowerCase(), String.valueOf(temp
//							.get(sum_field)));
//				}
//				dao.updateValueObject(vo);
//			} catch (Exception e) {
				vo.setString("sp_flag", sp_flag);
				for (int n = 0; n < sum_fields.length; n++) {
					String sum_field = sum_fields[n];
				//	vo.setString(sum_field.toLowerCase(), String.valueOf(temp.get(sum_field)));
					
					String value=((BigDecimal)temp.get(sum_field)).toString();
					if(setMap.get(sum_field.toUpperCase())!=null)
					{	 
						LazyDynaBean _bean=(LazyDynaBean)setMap.get(sum_field.toUpperCase()); 
						int decwidth=Integer.parseInt((String)_bean.get("decwidth"));
						value=PubFunc.round(value,decwidth);
					} 
					
					
					vo.setDouble(sum_field.toLowerCase(), Double.parseDouble(value));
				}
				dao.addValueObject(vo);
			//}

		}
	}
}
