package com.hjsj.hrms.module.questionnaire.mobile;

import com.hjsj.hrms.module.questionnaire.template.businessobject.TemplateBo;
import com.hjsj.hrms.module.questionnaire.template.transaction.SaveAnswerTemplateTrans;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * <p>Title: QuestionnaireTrans </p>
 * <p>Description: 微信端问卷调查</p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2016-1-21 上午10:28:25</p>
 * @author jingq
 * @version 1.0
 */
public class QuestionnaireTrans extends IBusiness{

	private static final long serialVersionUID = 1L;
	private Random rand =  new Random();
	private enum TransType{
		/**加载题目数据**/
		loadData,
		/**保存题目答案**/
		saveData
	}

	@Override
    public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String succeed = "false";
		String message = "";
		try {
			String transType = (String) hm.get("transType");
			if(transType!=null){
				if(transType.equals(TransType.loadData.toString())){
					MorphDynaBean configObjbean = (MorphDynaBean) hm.get("configObj");
					HashMap configObj = PubFunc.DynaBean2Map(configObjbean);
					String  temsubObject =(String)configObj.get("subObject");
					String  temmainObject =(String)configObj.get("mainObject");
					String	mainObject = "";
					String subObject = "";
					// 验证 temmainObject=null时 条件为 false wangb 2017-4-19
					if(temmainObject!=null && !"null".equalsIgnoreCase(temmainObject)){
						mainObject = PubFunc.decrypt(temmainObject);
					}
					// 验证 temsubObject=null时 条件为 false wangb 2017-4-19
					if(temsubObject!=null && !"null".equalsIgnoreCase(temsubObject)){
						subObject = PubFunc.decrypt(temsubObject);
					}
					String planid1 = (String)hm.get("planid");
					String planid = PubFunc.decrypt(planid1);
					if("null".equals(planid)){
						planid = "0";
					}
					String qnid = "0";
					String status = "0";
					ContentDAO dao = new ContentDAO(this.frameconn);
					String sql = "select qnid,status from qn_plan where planid = '"+planid+"'";
					this.frowset = dao.search(sql);
					if(this.frowset.next()){
						qnid = (this.frowset.getString("qnid")==null)?"0":this.frowset.getString("qnid");
						status = (this.frowset.getString("status")==null)?"0":this.frowset.getString("status");
					}
					if(!"1".equalsIgnoreCase(status)){
						hm.put("status", "问卷未发布或已暂停！");
						return;
					}
					TemplateBo bo = new TemplateBo();
					JSONObject obj = new JSONObject();
					Object[] objs = new Object[1];
					objs[0] = "qnid="+qnid;
					String jsonobject = bo.getTemplate(objs);
					String cip = (String) hm.get("cip");
					if(jsonobject!=null){
						obj = JSONObject.fromObject(jsonobject);
						JSONObject qnset = (JSONObject)obj.get("qnset");
						//是否一个IP只能作答一次
						boolean isOneIp = false;
						DbWizard dw = new DbWizard(frameconn);
						String oneip = "";
						if(qnset!=null&&!qnset.isNullObject())
							oneip = (String)qnset.get("oneip");
						String tabname = "qn_"+qnid+"_data";
						String tabname1 = "qn_matrix_"+qnid+"_data";
						if("1".equals(oneip)){
							if(dw.isExistTable(tabname, false)){
								this.frowset = dao.search("select 1 from "+tabname+" where cip = '"+cip+"'");
								if(this.frowset.next())
									isOneIp = true;
							}else if(dw.isExistTable(tabname1, false)){
								this.frowset = dao.search("select 1 from "+tabname1+" where cip = '"+cip+"'");
								if(this.frowset.next())
									isOneIp = true;
							}
						}
						if(isOneIp){
							hm.put("oneip", "您已经答过题了，不能再答题了！");
							return;
						}

						String requiredlogin = "";
						if(qnset!=null&&!qnset.isNullObject()){
							requiredlogin = (String)qnset.get("requiredlogin");
						}
						if(requiredlogin!=null&& "1".equals(requiredlogin)){//是否登陆--不登录不允许答题
							if(userView==null){
								hm.put("errormessage", "该问卷必须登陆才能作答！");
								return;
							}
						}
					}
					ArrayList<HashMap<String, Object>> list = bo.getTemplateQuestion(Integer.parseInt(qnid), this.frameconn,subObject,mainObject,(String)hm.get("planid"));
					hm.put("planid", hm.get("planid"));
					hm.put("advanceendmsg", obj.get("advanceendmsg"));
					hm.put("qnlongname", obj.get("qnlongname"));
					hm.put("instruction", obj.get("instruction"));
					hm.put("finishmsg", obj.get("finishmsg"));//xiegh 20170714 bug:29669  移动端，答问卷，答完后提示信息，没有根据设置好的同步
					hm.put("params", list);
				} else if(transType.equals(TransType.saveData.toString())){
					MorphDynaBean bean = (MorphDynaBean) hm.get("values");
					ArrayList scoreparams = (ArrayList)hm.get("scoreparams");
					HashMap values = PubFunc.DynaBean2Map(bean);
					//zhangh 2020-1-20校验是否交了白卷
					boolean whiteRoll = isWhiteRoll(values);
					if(whiteRoll){
						hm.put("whiteRoll", true);
						return;
					}else{
						hm.put("whiteRoll", false);
					}
					MorphDynaBean configObjbean = (MorphDynaBean) hm.get("configObj");
					HashMap configObj = PubFunc.DynaBean2Map(configObjbean);
					String planid = PubFunc.decrypt((String) hm.get("planid"));
					String cip = (String) hm.get("cip");
					String qnid = null;
					ContentDAO dao = new ContentDAO(this.frameconn);
					String sql0 = "select qnid from qn_plan where planid = ?";
					ArrayList al0 = new ArrayList();
					al0.add(planid);
					this.frowset = dao.search(sql0, al0);
					if(this.frowset.next()){
						qnid = (this.frowset.getString("qnid")==null)?"":this.frowset.getString("qnid");
					}
					String  temsubObject =(String)configObj.get("subObject");
					String  temmainObject =(String)configObj.get("mainObject");
					String	mainObject = "";
					String subObject = "";
					if(temmainObject!=null&&!"null".equals(temmainObject)){//xiegh
						temmainObject = PubFunc.decrypt(temmainObject);
					}
					if(temsubObject!=null&&!"null".equals(temsubObject)){//xiegh
						temsubObject = PubFunc.decrypt(temsubObject);
					}
					TemplateBo bo = new TemplateBo();
					JSONObject obj = new JSONObject();
					Object[] objs = new Object[1];
					objs[0] = "qnid="+qnid;
					String jsonobject = bo.getTemplate(objs);
					int closeselected =0;
					int closevalue = 0;
					String requiredlogin = "0";
					if(jsonobject==null){
						return;
					}
					obj = JSONObject.fromObject(jsonobject);
					JSONObject qnset = (JSONObject)obj.get("qnset");
					if(qnset!=null&&!qnset.isNullObject()){
						requiredlogin = (String)qnset.get("requiredlogin");
						String autoClose = (String)qnset.get("autocloseselected");
						closeselected = autoClose!=null&&autoClose.length()>0?Integer.parseInt(autoClose):0;
						String autoclosevalue = (String)qnset.get("autoclosevalue");
						closevalue = autoclosevalue!=null&&autoclosevalue.length()>0?Integer.parseInt(autoclosevalue):0;
					}

					if(userView!=null)
						mainObject = userView.getA0100();
					else
						mainObject = cip+System.currentTimeMillis()+rand.nextInt(1000);

					if(temmainObject!=null&&!"null".equals(temmainObject)){//这谁改问题没改全   xiegh 20170714  
						mainObject = temmainObject;
					}
					
					boolean continueSave = bo.updateRecoveryCount(closeselected, Integer.parseInt(planid), closevalue, mainObject, userView, qnid, this.frameconn);
					if(!continueSave)
						return;
					
					String tabname = "qn_"+qnid+"_data";
					String tabname1 = "qn_matrix_"+qnid+"_data";
					
					StringBuffer checksql= new StringBuffer();
					StringBuffer checksql1= new StringBuffer();
					DbWizard dw = new DbWizard(frameconn);
					if(dw.isExistTable(tabname, false)&& "1".equals(requiredlogin)){
						checksql.append("delete  from "+tabname+" where planid='"+planid+"' and mainObject='"+mainObject+"'");
						dao.update(checksql.toString());
					}
					if(dw.isExistTable(tabname1, false)&& "1".equals(requiredlogin)){
						checksql1.append("select * from "+tabname1+" where planid='"+planid+"' and mainObject='"+mainObject+"'");
						dao.update(checksql1.toString());
					}
					DBMetaModel dm = new DBMetaModel(this.frameconn);
					dm.reloadTableModel(tabname);
					RecordVo vo = new RecordVo(tabname);
					boolean fla = false;
					String dataid1=SaveAnswerTemplateTrans.getQuesAnswerNextDataId(tabname,dao);
					if(dw.isExistTable(tabname, false)){
						vo.setString("dataid",dataid1);
						vo.setString("status","2");
						vo.setString("cip", cip);
						vo.setString("mainobject",mainObject);
						if(temsubObject!=null){
							vo.setString("subobject",temsubObject);
						}
						vo.setString("planid",planid==null?"":planid);
						fla = true;
					}
					
					ArrayList matrixList = (ArrayList)this.formHM.get("matrixList");
					ArrayList mtScoreList = (ArrayList)this.formHM.get("mtScoreList");
					
					ArrayList matrixVoList = new ArrayList();
					Iterator ite = values.keySet().iterator();
					
					while(ite.hasNext()) {
						String key = (String)ite.next();
						//key = key.toLowerCase();
						String[] keyArray = key.replaceAll("Q","").split("_");
						if(key.startsWith("Q") && matrixList.contains(Integer.parseInt(keyArray[0]))) {
							
							if(key.endsWith("_desc"))
								continue;
							
							RecordVo matrixVo = new RecordVo(tabname1);
							
							matrixVo.setString("mainobject", mainObject);
							if(temsubObject!=null){
								vo.setString("subobject",temsubObject);
							}
							matrixVo.setString("planid",planid==null?"":planid);
							matrixVo.setString("optid",keyArray[1]);
							matrixVo.setString("status","2");
							matrixVo.setString("itemid",keyArray[0]);
							matrixVo.setString("cip", cip);
							
							if(values.containsKey("Q"+keyArray[0]+"_desc")) {
								String quesDesc = (String)values.get("Q"+keyArray[0]+"_desc");
								matrixVo.setString("c_desc",quesDesc);
							}
							
							Object value = values.get(key);
							if(mtScoreList.contains(Integer.parseInt(keyArray[0]))) {
								matrixVo.setString("score", value.toString());
								matrixVoList.add(matrixVo);
								continue;
							}
							
							if(value instanceof ArrayList) {
								ArrayList valueList = (ArrayList)value;
								for(int i=0;i<valueList.size();i++) {
									Integer mvalue  = (Integer)valueList.get(i);
									matrixVo.setString("c"+(mvalue+1),"1");
								}
								matrixVoList.add(matrixVo);
								continue;
							}
							
							Integer mvalue  = (Integer)value;
							matrixVo.setString("c"+(mvalue+1),"1");
							matrixVoList.add(matrixVo);
							continue;
						}
						
						Object value = values.get(key);
						
						if(key.startsWith("D")) {
							key = key.replace("D", "Q");
							if(value instanceof String) {
								value = value.toString().split("`")[0];
							}
							
						}
						
						if(value instanceof ArrayList) {
							ArrayList valueList = (ArrayList)value;
							for(int i=0;i<valueList.size();i++) {
								Object optid = valueList.get(i);
								vo.setString(key.toLowerCase()+"_"+optid,"1");
							}
							continue;
						}
						
						if(keyArray.length==1)
							key = key+"_1";
						
						vo.setString(key.toLowerCase(),value.toString());
					}
					
					if(fla)
						dao.updateValueObject(vo);
					
					if(matrixVoList.size()>0)
						dao.addValueObject(matrixVoList);
					
					if(vo!=null)
						return;
					
				}else if(transType.equals(TransType.loadData.toString())){
					
				} 
			} else {
				message = ResourceFactory.getProperty("mobileapp.error");
				hm.put("msg", message);
			}
		} catch (Exception e) {
			succeed = "false";
			if("".equals(message)){
				message = e.getMessage();
			}
			hm.put("message", message);
			e.printStackTrace();
		} finally {
			hm.put("flag", succeed);
		}
	}

	/**
	 * 判断是否交了白卷
	 * @param values
	 * @return
	 */
	private boolean isWhiteRoll(HashMap values) {
		boolean whiteRoll = true;
		Iterator ite = values.keySet().iterator();
		while(ite.hasNext()) {
			String key = (String)ite.next();
			Object value = values.get(key);
			if(value instanceof Integer) {
				if(value !=null){
					return false;
				}
			}
			if(value instanceof String) {
				if(StringUtils.isNotBlank((String) value)){
					return  false;
				}
			}
			if(value instanceof ArrayList) {
				ArrayList valueList = (ArrayList)value;
				if(valueList !=null && valueList.size()>0){
					return  false;
				}
			}
		}
		return whiteRoll;
	}

}
