package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title: 员工管理-计算-公式项保存、删除、更新</p>
 * <p>Description: 员工管理-计算-公式项保存、删除、更新</p>
 * <p>Company:hjsj</p>
 * <p>create time: </p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class OperatFormulaTrnas extends IBusiness {

	public void execute() throws GeneralException {
		String flag = (String)this.getFormHM().get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		if("del".equalsIgnoreCase(flag)){
			String id = (String)this.getFormHM().get("id");
			id=id!=null&&id.trim().length()>0?id:"";
			
			String unit_type = (String)this.getFormHM().get("unit_type");
			unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"2";
			
			delItem(id,unit_type);
		}else if("save".equalsIgnoreCase(flag)){
			String formulastr = (String)this.getFormHM().get("formulastr");
			formulastr=formulastr!=null&&formulastr.trim().length()>0?formulastr:"";
			formulastr=SafeCode.decode(formulastr);
			
			String formula = (String)this.getFormHM().get("formula");
			formula=formula!=null&&formula.trim().length()>0?formula:"";
			formula=SafeCode.decode(formula);
			
			String infor = (String)this.getFormHM().get("infor");
			infor=infor!=null&&infor.trim().length()>0?infor:"1";
			infor= "6".equals(infor)?"2":infor;
			String infor_flag = infor;
			
			String unit_type = (String)this.getFormHM().get("unit_type");
			unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"2";
			String setname = (String)this.getFormHM().get("setname");
			setname=setname!=null&&setname.trim().length()>0?setname:"";
			String isSetId=(String)this.getFormHM().get("isSetId");
			if("1".equals(infor))
				unit_type="2";
			else if("2".equals(infor)){
				unit_type="3";
			}else if("3".equals(infor)){
				unit_type="4";
			}else if("4".equals(infor)){
				unit_type="1";
				infor_flag = "1";
			}else if("5".equals(infor)){
				unit_type="5";
			}
			
			String id = (String)this.getFormHM().get("id");
			id=id!=null&&id.trim().length()>0?id:"";
			formulastr = PubFunc.keyWord_reback(formulastr);
			formula = PubFunc.keyWord_reback(formula);
			setname = PubFunc.keyWord_reback(setname);
			saveItem(formulastr,formula,id,infor,unit_type,setname,isSetId);
		}else if("update".equalsIgnoreCase(flag)){
			String id = (String)this.getFormHM().get("id");
			id=id!=null&&id.trim().length()>0?id:"";
			
			String unit_type = (String)this.getFormHM().get("unit_type");
			unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"2";
			
			String check = (String)this.getFormHM().get("check");
			check=check!=null&&check.trim().length()>0?check:"";
			
			updateItem(id,check,unit_type);
		}
	}
	private void delItem(String id,String unit_type){
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlstr = "delete from HRPFormula where fid='"+id+"' and unit_type='"+unit_type+"'";
		try {
			dao.update(sqlstr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void updateItem(String id,String flag,String unit_type){
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlstr = "update HRPFormula set flag="+flag+" where fid='"+id+"' and unit_type='"+unit_type+"'";
		try {
			dao.update(sqlstr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void saveItem(String formulastr,String formula,String id,String infor,String unit_type,String setname,String isSetId){
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList list = new ArrayList();
		String arr[]=formulastr.split("`");
		int fid = fid(dao,unit_type);
		int n=0;
		String ids = id;
		ArrayList listvalue = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("update HRPFormula set formula=? where fid=? and unit_type=?");
		for(int i=0;i<arr.length;i++){
			String[] item_arr = arr[i].split("::");
			if(item_arr.length==3){
				String[] fid_arr = item_arr[0].split("_");
				if(fid_arr.length==2){
					String[] new_arr = fid_arr[0].split(":");
					if(new_arr.length==2){
						String itemid = fid_arr[1];
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						ArrayList fidlist = new ArrayList();
						fidlist.add((fid+n+1)+"");
						fidlist.add(item_arr[2]);
						fidlist.add(item_arr[1]);
						fidlist.add(fielditem.getFieldsetid());
						fidlist.add(itemid);
						if(item_arr[0].equalsIgnoreCase(id)){
							fidlist.add(formula);
							ids=(fid+n+1)+"_"+itemid;
						}else{
							fidlist.add(null);
						}
						fidlist.add(unit_type);
						list.add(fidlist);
						n++;
					}else{
						if(item_arr[0].equalsIgnoreCase(id)){
							ArrayList listformula = new ArrayList();
							listformula.add(formula);
							listformula.add(fid_arr[0]);
							listformula.add(unit_type);
							listvalue.add(listformula);
						}
					}
				}
			}
		}
		if(listvalue.size()>0){
			try {
				dao.batchUpdate(buf.toString(), listvalue);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		/*【5372】员工管理：记录录入，数据库HRPFormula表中有两个计算公式在前台显示不出来，
         * 但是新增计算公式后，这两个公式就在记录录入中显示出来了，刷新页面后就又没了。
         * jingq add 2014.11.26
         */
		
        ArrayList dataList = new ArrayList();
        ArrayList fieldsetlist = new ArrayList();
        String setidList="";
        if (!"5".equals(infor)) {
        	if("1".equals(infor)||"4".equals(infor)){
        		fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
        	}else if("2".equals(infor)){
        		fieldsetlist = this.userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
        	}else if("3".equals(infor)){
        		fieldsetlist = this.userView.getPrivFieldSetList(Constant.POS_FIELD_SET);
        	}
            
            if (fieldsetlist != null) {
                for (int i = 0; i < fieldsetlist.size(); i++) {
                    FieldSet fs = (FieldSet) fieldsetlist.get(i);
                    /*
                     * if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.
                     * getFieldsetid()))){//读权限 continue; }
                     */
                    if ("A00".equalsIgnoreCase(fs.getFieldsetid())) {
                        continue;
                    }
                    CommonData cd = new CommonData(fs.getFieldsetid(), fs.getCustomdesc());
                    dataList.add(cd);
                }
            }
            
            if (dataList.size() == 0) {
                try {
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.computer.nopriv")));
                } catch (GeneralException e) {
                    e.printStackTrace();
                }
            }
            
            for (int i = 0; i < dataList.size(); i++) {
                setidList += ((CommonData) dataList.get(i)).getDataValue() + ",";
            }
        }
		StringBuffer sqlstr = new StringBuffer();
		StringBuffer sortstr = new StringBuffer();
		sqlstr.append("insert into HRPFormula(fid,flag,forname,setid,itemid,formula,");
		sqlstr.append("unit_type) values(?,?,?,?,?,?,?)");
		String sql = "select fid,flag,forname,itemid,setid from HRPFormula where unit_type="+unit_type;
		if(isSetId!=null&& "1".equals(isSetId))
		{
			sql+=" and upper(setid)='"+setname.toUpperCase()+"'";
		}
		sql+=" order by db_type";
		try {
			dao.batchInsert(sqlstr.toString(),list);
			this.frowset = dao.search(sql);
			while(this.frowset.next()){
//				liwc 业务指标不存在授权
//				if(!this.userView.analyseFieldPriv(this.frowset.getString("itemid")).equals("2"))
//					continue;
				
			    String setid = this.frowset.getString("setid");
                if(!"5".equals(infor) && (setidList).toUpperCase().indexOf(setid.toUpperCase())==-1){
                    continue;
                }
				String itemid = this.frowset.getString("fid")+"_"+this.frowset.getString("itemid");
				/*【6128】自助服务/员工信息维护，计算，新增计算公式后，把所有无权限的公式也写进来了，还能删除，不对。
                 * 修改为有读或写权限的公式才能显示     jingq upd 2014.12.18
                 */
				if (!"5".equals(infor)) {
                    String fielditemid = this.frowset.getString("itemid");
                    if ("0".equalsIgnoreCase(this.userView.analyseFieldPriv(fielditemid))) {
                        continue;
                    }
                }
				
				if("1".equals(infor)){
                    //子集无权限
                    if((setidList).toUpperCase().indexOf(setid.toUpperCase()) == -1)
                        continue;
                    
                    FieldItem item = DataDictionary.getFieldItem(this.frowset.getString("itemid"), setid);
                    //指标不存在或未构库
                    if(item == null || "0".equals(item.getUseflag()))
                        continue;
                    
                    //无指标读写权限
                    if("0".equals(this.userView.analyseFieldPriv(item.getItemid()))) {
                        continue;
                    }
                }
				
				String forname = this.frowset.getString("forname");
				int flag = this.frowset.getInt("flag");
				sortstr.append(itemid+"::");
				sortstr.append(forname+"::");
				sortstr.append(flag+"`");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("formulastr",sortstr.toString());
		this.getFormHM().put("id",ids);
	}
	private int fid(ContentDAO dao,String unit_type){
		int fid=0;
		String sqlstr = "select max(fid) as fid from HRPFormula where unit_type='"+unit_type+"'";
		try {
			this.frowset=dao.search(sqlstr);
			while(this.frowset.next()){
				fid=this.frowset.getInt("fid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fid;
	}
}
