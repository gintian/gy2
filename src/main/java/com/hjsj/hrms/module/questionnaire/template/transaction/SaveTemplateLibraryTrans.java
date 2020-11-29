package com.hjsj.hrms.module.questionnaire.template.transaction;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaveTemplateLibraryTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException{
		
		ArrayList updatelist=(ArrayList)this.getFormHM().get("updaterecord"); //修改的数据
		//区分是查看问卷模板还是创建问卷 changxy 20160808
		//SeeTemplate rpc 传递的参数，map("SeeTemplate","SeeTemplate")
		String seeTemplate=this.getFormHM().get("SeeTemplate")==null?"":(String)this.getFormHM().get("SeeTemplate");
		RowSet rs = null;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			if(updatelist.size()>0){
				ArrayList<RecordVo> votemlist = new ArrayList<RecordVo>();
				ArrayList<RecordVo> vouptliblist = new ArrayList<RecordVo>();
				//zhangh 2020-3-5 问卷调查模板名称列表
				List<String> qnnameList = new ArrayList<String>();
				List<String> qnIdList = new ArrayList<String>();
				rs = dao.search("select qnId,qnName from qn_template ");
				while(rs.next()){
					qnnameList.add(rs.getString("qnName"));
					qnIdList.add(rs.getString("qnId"));
				}
				for(int i=0;i<updatelist.size();i++){
					MorphDynaBean bean=(MorphDynaBean)updatelist.get(i);
					RecordVo resulttemVo = new RecordVo("qn_template");
					RecordVo resultlibVo = new RecordVo("qn_template_library");
					String qnname = (String)bean.get("qnname");
					String qnId = (String)bean.get("qnid");
					if(qnnameList.contains(qnname)){
						//【60943】V77问卷调查：查看模板，修改模板的问卷分类或所属机构或是否共享，不修改问卷名称，点保存，也会提示问卷名称已被使用
						if(!qnIdList.get(qnnameList.indexOf(qnname)).equalsIgnoreCase(qnId)){
							//【58514】V771问卷调查：在问卷设计页面【存为模板】，输入的模板名称已存在时候的提示与查看模板界面修改模板名称且名称已存在时候的提示不一致
							this.getFormHM().put("errorMsg", "此模板名称已被使用过，请修改模板名称！");
							return;
						}
					}else{
						qnnameList.add(qnname);
						qnIdList.add(qnId);
					}
					String qntype = (String)bean.get("qntype");
					String isShare="";
					if("SeeTemplate".equals(seeTemplate)){
						isShare=(String)bean.get("isshare");
					}
					if(qntype==null){
						qntype="";
					}
					String b0110 = (String)bean.get("b0110");
					if(!"".equals(b0110)&&b0110.toString().contains("`")){
						int index = b0110.indexOf("`");
						b0110 = b0110.substring(0,index);
					}
					resultlibVo.setString("b0110", b0110);
					String qnid = (String)bean.get("qnid");
					new TemplateBo(this.frameconn).templateVerification(qnname,b0110,qnid);
					if(!"".equals(qnname)){
						resulttemVo.setInt("qnid", Integer.parseInt(qnid));
						resulttemVo.setString("qnname", qnname);
						resulttemVo.setString("qnlongname",qnname);
					}
					resultlibVo.setInt("qnid", Integer.parseInt(qnid));
					resultlibVo.setString("qntype", qntype);
					if("SeeTemplate".equals(seeTemplate)){
						resultlibVo.setInt("isshare", Integer.parseInt(isShare));
					}
				
					votemlist.add(resulttemVo);
					vouptliblist.add(resultlibVo);
					if("SeeTemplate".equals(seeTemplate)){//查看问卷模板保存修改的问卷后 清空修改的问卷questionMap内的缓存 changxy 20160820
						TemplateBo bo=new TemplateBo();
						bo.removeCachMap(qnid);
					}
				}
				dao.updateValueObject(votemlist);
				dao.updateValueObject(vouptliblist);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
		}finally{
			PubFunc.closeDbObj(rs);
		}
	}

}
