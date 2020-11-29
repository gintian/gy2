package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.configfile.businessobject.KhTemplateBo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 职称-配置-测评表配置
 * @createtime April 4, 2018 15:17:55 PM
 * @author linbz
 *
 */
@SuppressWarnings("serial")
public class InitEvaluationTableTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		// 1：获取模板信息  2：获取模板的详细信息 3:保存配置信息 4：只显示选中的模板信息
		String type = (String) this.getFormHM().get("type");
		
		JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
		try {
			//get tree data
			if("1".equals(type)){
				// 接收被选中子节点 根据已选值判断该子节点的选中状态
				String strValue = (String) this.getFormHM().get("value");
				
				String dataflag = (String) this.getFormHM().get("dataflag");
				// =0获取选中默认第一个模板表的路径id
				if("0".equals(dataflag)) {
					// 若测评表配置为空则直接返回
					if(StringUtils.isEmpty(strValue)) {
						this.getFormHM().put("firstSrc", "");
						return;
					}
					// 创建或编辑会议，阶段时已选模板表
					String selectTabids = (String) this.getFormHM().get("selectTabids");
					if(StringUtils.isNotEmpty(selectTabids))
						strValue = selectTabids;
					
					String firstSrc = jobtitleConfigBo.getFirstSrc(strValue);	
					if(firstSrc.startsWith(","))
						firstSrc = firstSrc.substring(1);
					String surl = "";
					String firstTempid = "";
					if(firstSrc.indexOf("|") > -1) {
						surl = firstSrc.split("\\|")[0];
						firstTempid = firstSrc.split("\\|")[1];
					}
					this.getFormHM().put("firstSrc", surl);
					this.getFormHM().put("firstTempid", firstTempid);
					// 返回已选模板的名称
					String selectidTexts = jobtitleConfigBo.getSelectidTextSrc(strValue);
					this.getFormHM().put("selectidTexts", selectidTexts);
				}
				// =1获取模板集合
				else if("1".equals(dataflag)) {
					// 获取树节点
					String note = (String) this.getFormHM().get("node");
					// 截取节点中的标识
					note = note.replaceAll("｜", "|").replaceAll("／", "/");
					
					ArrayList list = jobtitleConfigBo.getAllTreeData(note, strValue);	
			    	this.getFormHM().put("data", list);
				}
			}
			//get page
			else if("2".equals(type)){
				String tabid = (String) this.getFormHM().get("tabid");
//				tabid = PubFunc.decrypt(tabid);
				
				KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),this.userView, "1", "33");
				String tableContentHtml = bo.getObjectCardHtml(tabid, "menu");
				this.getFormHM().put("tableContent", tableContentHtml);
			} 
			// save config	
			else if("3".equals(type)){
				String tabids = (String) this.getFormHM().get("tabids");
				if(tabids.endsWith(",")) 
					tabids = tabids.substring(0, tabids.length()-1);
				// id|名称
				String tabidNames = (String) this.getFormHM().get("tabidNames");
				if(tabidNames.endsWith(",")) 
					tabidNames = tabidNames.substring(0, tabidNames.length()-1);
				/**
				 * 筛选是增加的指标  还是取消的指标
				 * 基于原来的参数 若不存在则视为取消勾选该模板表
				 * **/
				String cancelTabids = "";
				// 获取原来的参数
				String strValue = jobtitleConfigBo.getJobtitleParamConfig("per_templates");
				String[] value_a=strValue.split(",");//赋值给数组
				for(int i=0;i<value_a.length;i++){
					if(!(","+tabids+",").contains(","+value_a[i]+",") 
							&& StringUtils.isNotEmpty(value_a[i].trim()))
						cancelTabids = cancelTabids + value_a[i] + ",";
				}
				String msg = "";
				try {
					// 取消勾选的指标操作
					String noCancel = null;
					if(StringUtils.isNotEmpty(cancelTabids)) {
						noCancel = jobtitleConfigBo.isCanCancelTemplate(cancelTabids);
						//将不可取消的测评表去掉
						cancelTabids = ","+cancelTabids;
						if(StringUtils.isNotEmpty(noCancel)) {
							//用于提示用户那些不可以取消勾选
							this.formHM.put("noCancelTemplates", noCancel.replaceAll(",", "、"));
							return;
						}
						jobtitleConfigBo.cancelField(cancelTabids);
					}
					// 勾选的指标操作
					jobtitleConfigBo.addField(tabidNames);
					// 操作完业务字典，实时刷新
					DataDictionary.refresh();
				}catch (Exception e){
					throw GeneralExceptionHandler.Handle(e);
				}
				if(StringUtils.isNotEmpty(msg)) {
					this.getFormHM().put("msg", msg);
					return;
				}
				// 保存到职称参数中 JOBTITLE_CONFIG
				HashMap configMap = new HashMap();
				configMap.put("key", "per_templates");
				configMap.put("value", tabids);
				ArrayList<HashMap> configArr = new  ArrayList<HashMap>();
				configArr.add(configMap);
				msg = jobtitleConfigBo.saveJobtitleConfig(configArr);
				this.getFormHM().put("tabids", tabids);
				this.getFormHM().put("msg", msg);
			}
			//get 只显示选中的模板信息
			else if("4".equals(type)){
				// 接收被选中子节点  根据已选值判断该子节点的选中状态
				String strValue = (String) this.getFormHM().get("value");
				
				String dataflag = (String) this.getFormHM().get("dataflag");
				// =0获取选中默认第一个模板表的路径id
				if("0".equals(dataflag)) {
					// 若测评表配置为空则直接返回
					if(StringUtils.isEmpty(strValue)) {
						this.getFormHM().put("firstSrc", "");
						return;
					}
					// 创建或编辑会议，阶段时已选模板表
					String selectTabids = (String) this.getFormHM().get("selectTabids");
					if(StringUtils.isNotEmpty(selectTabids))
						strValue = selectTabids;
					
					String firstSrc = jobtitleConfigBo.getFirstSrc(selectTabids);	//strValue
					if(firstSrc.startsWith(","))
						firstSrc = firstSrc.substring(1);
					String surl = "";
					String firstTempid = "";
					if(firstSrc.indexOf("|") > -1) {
						surl = firstSrc.split("\\|")[0];
						firstTempid = firstSrc.split("\\|")[1];
					}
					this.getFormHM().put("firstSrc", surl);
					this.getFormHM().put("firstTempid", firstTempid);
					// 返回已选模板的名称
					String selectidTexts = jobtitleConfigBo.getSelectidTextSrc(selectTabids);//strValue
					this.getFormHM().put("selectidTexts", selectidTexts);
				}
				// =1获取模板集合
				else if("1".equals(dataflag)) {
					// 若测评表配置为空则直接返回
					if(StringUtils.isEmpty(strValue)) {
						this.getFormHM().put("data", new ArrayList());
						return;
					}
					// 创建或编辑会议，阶段时已选模板表
					String selectTabids = (String) this.getFormHM().get("selectTabids");
					//获取树节点
					String note = (String) this.getFormHM().get("node");
						
					ArrayList list = jobtitleConfigBo.getSelectTreeData(note, strValue, selectTabids);	
			    	this.getFormHM().put("data", list);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
