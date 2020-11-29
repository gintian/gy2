package com.hjsj.hrms.transaction.general.chkformula;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String c_expr = (String) this.getFormHM().get("c_expr");
		c_expr=c_expr!=null&&c_expr.trim().length()>0?c_expr:"";
		c_expr=SafeCode.decode(c_expr);
		
		String tabid = (String) this.getFormHM().get("tabid");
		tabid=tabid!=null&&tabid.trim().length()>0?tabid:"";
		
		String flag = (String) this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		
		String chkid = (String) this.getFormHM().get("chkid");
		chkid=chkid!=null&&chkid.trim().length()>0?chkid:"";
		c_expr = PubFunc.keyWord_reback(c_expr);
		String chkflag = "ok";
		if(c_expr.indexOf(ResourceFactory.getProperty("inform.muster.code.to.name"))!=-1){
			c_expr="";
			chkflag = ResourceFactory.getProperty("inform.muster.not.codetoname");
		}
		if(chkid.trim().length()>0){
			if(c_expr.trim().length()>0){
				ArrayList alUsedFields = new ArrayList();
				if("0".equals(flag)){
					alUsedFields=itemTempletList(tabid);
				}else{
					alUsedFields = getMidVariableList(tabid);
				}
				YksjParser yp = new YksjParser(getUserView(), alUsedFields, YksjParser.forNormal, YksjParser.LOGIC
						, YksjParser.forPerson, "Ht", "");
				yp.setCon(this.getFrameconn());
				boolean b = yp.Verify_where(c_expr.trim());			
				if (!b){ //验证没有通过
					chkflag = yp.getStrError();
				}else{
					saveFormula(chkid,c_expr);
					chkflag="ok";
				}
			}else{
				saveFormula(chkid,"");
				chkflag="ok";
			}
		}
		this.getFormHM().put("chkflag",SafeCode.encode(chkflag));
	}
	private ArrayList itemTempletList(String tableid){
		ArrayList alUsedFields = new ArrayList();
		try {
			TemplateTableBo changebo = new TemplateTableBo(this.frameconn,Integer.parseInt(tableid),this.userView);
			ArrayList itemlist = changebo.getAllFieldItem();
			HashMap sub_domain_map = changebo.getSub_domain_map();
			for(int i=0;i<itemlist.size();i++){
				FieldItem field = (FieldItem)itemlist.get(i);
				if(field.isChangeAfter()){
					field.setItemdesc(ResourceFactory.getProperty("inform.muster.to.be")+field.getItemdesc());
				}else if(field.isChangeBefore()){
					
					if(sub_domain_map!=null&&sub_domain_map.get(""+i)!=null&&sub_domain_map.get(""+i).toString().trim().length()>0){
						field.setItemdesc(""+sub_domain_map.get(""+i+"hz"));
					}
				}
				alUsedFields.add(field);
			}
			TempvarBo tempvarbo = new TempvarBo();
			alUsedFields.addAll(tempvarbo.getMidVariableList(this.frameconn,tableid));
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return alUsedFields;
	}
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		try{
			StringBuffer buf=new StringBuffer();
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec from ");
			buf.append(" midvariable where nflag=0 and templetid=0 ");
			buf.append(" and (cstate is null or cstate='");
			buf.append(salaryid);
			buf.append("')");
			ContentDAO dao=new ContentDAO(this.frameconn);
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("A01");//没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype"))
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
			String sqlstr = "select * from salaryset";
			if(salaryid!=null&&salaryid.trim().length()>0){
				sqlstr+=" where salaryid="+salaryid;
			}
			rset=dao.search(sqlstr);
			while(rset.next()){
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("ITEMID"));
				item.setItemdesc(rset.getString("ITEMDESC"));
				item.setFieldsetid(rset.getString("FIELDSETID"));
				item.setItemlength(rset.getInt("ITEMLENGTH"));
				item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
				item.setDecimalwidth(rset.getInt("DECWIDTH"));
				item.setItemtype(rset.getString("ITEMTYPE"));
				item.setVarible(1);
				fieldlist.add(item);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	private void saveFormula(String chkid,String formula){
		try {
			ContentDAO dao=new ContentDAO(this.frameconn);
			StringBuffer sqlstr = new StringBuffer();
			RecordVo vo = new RecordVo("hrpChkformula");
			vo.setInt("chkid", Integer.parseInt(chkid));
			vo = dao.findByPrimaryKey(vo);
			vo.setString("formula", formula);
			dao.updateValueObject(vo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
