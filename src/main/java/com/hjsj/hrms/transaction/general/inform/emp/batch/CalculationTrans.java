package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
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
public class CalculationTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String setname = (String)reqhm.get("setname");
		setname=setname!=null&&setname.trim().length()>0?setname:"";
		
		String dbname = (String)reqhm.get("dbname");
		dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
		
		String a_code = (String)reqhm.get("a_code");

		a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
		
		String viewsearch = (String)reqhm.get("viewsearch");
		viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";
		reqhm.remove("viewsearch");
		
		String unit_type = (String)reqhm.get("unit_type");//1,2,3,4,5 :合同,人员，单位 职位,培训费用
		unit_type=unit_type!=null&&unit_type.trim().length()>0?unit_type:"2";
		reqhm.remove("unit_type");
		
		String infor = (String)reqhm.get("infor");//1,2,3,4,5,6 :人员，单位 职位 合同 培训费用 编制控制
		infor=infor!=null&&infor.trim().length()>0?infor:"1";
		reqhm.remove("infor");
		
		String inforflag = (String)reqhm.get("inforflag");//1:员工管理BS表格录入 2：外部培训     null或"" :其它模块
        inforflag = inforflag != null && inforflag.trim().length() > 0 ? inforflag : "";
        reqhm.remove("inforflag");
        
		String entranceFlag=(String)reqhm.get("entranceFlag");
		entranceFlag=entranceFlag!=null&&entranceFlag.length()>0?entranceFlag:"0";//进入模块标志=1从工资管理的基础数据维护进入，默认为0从其他模块进入
		reqhm.remove("entranceFlag");
		
		if("6".equals(infor)){
			PosparameXML pos = new PosparameXML(this.frameconn); 
			String nextlevel = pos.getValue(PosparameXML.AMOUNTS,"nextlevel");
			nextlevel=nextlevel!=null&&nextlevel.trim().length()>0?nextlevel:"0";
			String code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
			code=code!=null?code:"";
			if(a_code.trim().length()<1||code.equalsIgnoreCase(a_code)){
				nextlevel = "0";
			}
			if("0".equals(nextlevel)){
				infor = "2";
			}
		}
		String infor_flag = infor;
		if("1".equals(infor))
			unit_type="2";
		else if("2".equals(infor)){
			unit_type="3";
		}else if("3".equals(infor)){
			unit_type="4";
		}else if("4".equals(infor)){
			unit_type="1";
			infor_flag="1";
		}else if("5".equals(infor)){
			unit_type="5";
		}else if("6".equals(infor)){
			unit_type="3";
		}
		
		//zgd 2014-2-26 对人员管理、记录录入、计算进行权限管理
        ArrayList dataList = new ArrayList();
        ArrayList fieldsetlist = new ArrayList();
        String setidList="";
        String fieldlist = "";
        if("1".equals(infor)){
            fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
	        if(fieldsetlist!=null){
	            for(int i=0;i<fieldsetlist.size();i++){
	                FieldSet fs = (FieldSet)fieldsetlist.get(i);
	                /*if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))){//读权限
	                    continue;
	                }*/
	                if("A00".equalsIgnoreCase(fs.getFieldsetid())){
	                    continue;
	                }
	                CommonData cd = new CommonData(fs.getFieldsetid(),fs.getCustomdesc());
	                dataList.add(cd);
	            }
	        }
        	if(dataList.size()==0){
        		throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.computer.nopriv")));
        	}
        	for(int i=0;i<dataList.size();i++){
        		setidList+=((CommonData)dataList.get(i)).getDataValue()+",";
        	}
        }

		StringBuffer tablestr = new StringBuffer();
		StringBuffer sortstr = new StringBuffer();
		tablestr.append("<table width=\"100%\" border=\"0\">");
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlstr = "select fid,flag,forname,setid,itemid from HRPFormula where unit_type="+unit_type+" ";//and upper(setid)='"+setname.toUpperCase()+"'
		if("1".equals(entranceFlag))
			sqlstr+="and upper(setid)='"+setname.toUpperCase()+"'";
		sqlstr+=" order by db_type";
		try {
			this.frowset=dao.search(sqlstr);
			while(this.frowset.next()){
				String itemid = this.frowset.getString("itemid");
				
				if("1".equals(infor)){
					String setid = this.frowset.getString("setid");
					//子集无权限
					if((setidList).toUpperCase().indexOf(setid.toUpperCase())==-1){
						continue;
					}
					
					FieldItem item = DataDictionary.getFieldItem(itemid, setid);
					//指标不存在或未构库
					if(item == null || "0".equals(item.getUseflag()))
					    continue;
					
					//指标只读权限
					if("0".equals(this.userView.analyseFieldPriv(itemid))) {
					    continue;
					}
				}
//				liwc 业务指标不存在授权
//				if(!this.userView.analyseFieldPriv(itemid).equals("2"))
//					continue;
				
				String id = this.frowset.getString("fid")+"_"+itemid;
				String forname = this.frowset.getString("forname");
				int flag = this.frowset.getInt("flag");
				tablestr.append("<tr><td align=\"left\" onclick=\"tr_bgcolor('");
				tablestr.append(id);
				tablestr.append("')\">");
				tablestr.append("<input type=\"checkbox\" id=\""+id+"\" name=\"");
				tablestr.append(id);
				tablestr.append("\" value=\"1\" onclick=\"setCheck(document.getElementById('"+id+"'),'sortstr');\"");
				if(flag==1){
					tablestr.append("checked");
				}
				tablestr.append(">");
				tablestr.append(forname);
				tablestr.append("</td></tr>");
				sortstr.append(id+"::");
				sortstr.append(forname+"::");
				sortstr.append(flag+"`");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		tablestr.append("</table>");
		BatchBo batchbo = new BatchBo();
		batchbo.setEntranceFlag(entranceFlag);
		String count = "0";
		if("6".equals(infor)){
			//String code = this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
			//code=code!=null?code:"";
			/*if(a_code.trim().length()>0&&!code.equalsIgnoreCase(a_code)){
				count = "1";
			}else*/{
				 count = batchbo.countDelItem(this.frameconn,this.userView,setname,dbname,a_code,viewsearch,"2",inforflag)+"";
			}
		}else{
			 a_code=a_code.replace("all","");
			 count = batchbo.countDelItem(this.frameconn,this.userView,setname,dbname,a_code,viewsearch,infor_flag,inforflag)+"";
		}
		
		this.getFormHM().put("tablestr",tablestr.toString());
		this.getFormHM().put("sortstr",sortstr.toString());
		this.getFormHM().put("setname",setname);
		this.getFormHM().put("dbname",dbname);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("viewsearch",viewsearch);
		this.getFormHM().put("unit_type",unit_type);
		this.getFormHM().put("infor",infor);
		this.getFormHM().put("count",count);
		this.getFormHM().put("entranceFlag", entranceFlag);
	}

}
