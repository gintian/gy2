package com.hjsj.hrms.module.jobtitle.reviewmeeting.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.JobtitleConfigBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.ReviewFileBo;
import com.hjsj.hrms.module.jobtitle.reviewfile.businessobject.StartReviewBo;
import com.hjsj.hrms.module.jobtitle.reviewmeeting.businessobject.*;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/** 
 * 职称评审_发起评审
 * @createtime 
 * @author chent
 */
@SuppressWarnings("serial")	
public class ReviewConsoleOptTrans extends IBusiness {
	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		
		try {
			String opt = (String) this.getFormHM().get("opt");
			String w0301 = PubFunc.decrypt((String) this.getFormHM().get("w0301_e"));
			String review_links = (String) this.getFormHM().get("review_links");//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段 
			ReviewConsoleBo bo = new ReviewConsoleBo(this.getFrameconn(), this.getUserView());
			/** 
			 * 1：选择申报人页面快速查询
			 * 2：添加分组
			 * 3：修改分组信息
			 * 4：删除分组
			 * 5：应选人数设置保存
			 * 6：启动投票
			 * 7：重新启动
			 * 8：暂停投票
			 * 9：
			 * 10：添加申报人
			 * 11：删除申报人
			 * 12：分组表结构同步
			 * 13：选择申报人页面表格控件整理
			 * 14：统计票数 || 分数统计
			 * 15：导出投票账号、密码 || 导出审核账号、密码
			 * 16：票数归档 || 分数归档
			 * 17： 保存通过率参数
			 * 18：材料公示需要的指标和分组，分组名的集合
			 * 19：学科组评审人分组切换学科组，同时操作zc_expert_user表，更新 
			 * 20：定时获取投票数，每隔30秒
			 * 21：选择框MAP数据 : 获取状态不为结束的分组map(生成账号密码用到)
			 * 22：手工置为已结束
			 * 24：组排序
			 * 25：组内人员排序
			 *  */
			if("null".equals(opt) || StringUtils.isBlank(opt)) {// fast search
				
				String subModuleId = (String) this.getFormHM().get("subModuleId");
				if("jobtitle_reviewfile_console_selperson".equals(subModuleId)){
					String _type = (String)this.getFormHM().get("type");
					if("1".equals(_type)) {// 1:输入查询
						ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
						bo.diffSelPersonFastSearch(valuesList);
					}
				}
				// 关于票数|分数统计的快速查询 subModuleId都是以jobtitle_reviewfile_count为开头如 jobtitle_reviewfile_count1_0000000094_3
				else if(subModuleId.startsWith("jobtitle_reviewfile_count")){
					
					TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get(subModuleId);
		            if (catche != null) {
		                // 查询类型，1为输入查询，2为方案查询
		                String type = (String) this.getFormHM().get("type");
		                ArrayList<String> valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");
		                String exp = (null==this.getFormHM().get("exp"))?"":PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("exp").toString()));
		                String cond = (null==this.getFormHM().get("cond"))?"":PubFunc.keyWord_reback(SafeCode.decode(this.getFormHM().get("cond").toString()));
		                ReviewScorecountBo reviewScorecountBo = new ReviewScorecountBo(this.frameconn, this.userView);
		                // 获取查询SQL语句
		                String seachSql = reviewScorecountBo.getTableConfigSeachSql(type, valuesList, exp, cond);
		                catche.setQuerySql(seachSql);
	                	//保存快速查询条件备用
		                if(catche.getCustomParamHM()==null)
		                	catche.setCustomParamHM(new HashMap<String, String>());
		                catche.getCustomParamHM().put("fastQuerySql", seachSql);
		            }
				}
				
				
			} else if("2".equals(opt)) {// add categories
				String categories_name = (String) this.getFormHM().get("categories_name");
				String userType = (String) this.getFormHM().get("userType");//1：随机账号，2：非随机，选人的
				int errorcode = bo.addCategorie(w0301, review_links, categories_name, userType);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("3".equals(opt)) {// update categories
				ArrayList<DynaBean> savedata = (ArrayList<DynaBean>) this.getFormHM().get("savedata");
				String categories_id =  this.getFormHM().get("categories_id")==null?"":PubFunc.decrypt((String) this.getFormHM().get("categories_id"));
				int errorcode = 0;
				/*if(StringUtils.isNotBlank(categories_id)) {//存随机数的
					String value = (String)this.getFormHM().get("value");
					errorcode = bo.updateCategorie(categories_id,value);
				}else {*/
					errorcode = bo.updateCategorie(savedata);
				//}
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("4".equals(opt)) {// delete categories
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				int errorcode = bo.deleteCategorie(categories_id, w0301, review_links);
				int flag = bo.deleteMeetingPerson("", w0301, categories_id,review_links);
				int personCount = bo.getCount(w0301,review_links,"0");
				
				this.getFormHM().put("flag", flag);
				this.getFormHM().put("errorcode", errorcode);
				this.getFormHM().put("personCount", personCount);
				
			} else if("5".equals(opt)) {// save config
				String ids = (String) this.getFormHM().get("ids");
				
				HashMap<String, String> configMap = new HashMap<String, String>();
				configMap.put(review_links, ids);
				
				ReviewMeetingBo mettingBo = new ReviewMeetingBo(this.getFrameconn(), this.getUserView());
				int errorcode = mettingBo.saveW03Ctrl_param(w0301, review_links, configMap);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("6".equals(opt)) {// start
				StartReviewBo srbo = new StartReviewBo(this.getFrameconn(),this.getUserView());
			    ArrayList<MorphDynaBean> idlist = (ArrayList<MorphDynaBean>) this.getFormHM().get("idlist");
			    String evaluationType = (String)this.getFormHM().get("evaluationType");
			    String categories_id = (String)this.getFormHM().get("categories_id");
			    String userType = (String) this.getFormHM().get("userType");//1：随机账号，2：非随机，选人的
			    int operateType = (Integer) this.getFormHM().get("operateType");//1：启动，2：重启
			    JobtitleConfigBo jobtitleConfigBo = new JobtitleConfigBo(this.getFrameconn(), this.getUserView());
			    String msg = bo.startReview(evaluationType, srbo, idlist, w0301, review_links, categories_id, jobtitleConfigBo,userType, operateType);
			    this.getFormHM().put("msg", msg);
			} else if("8".equals(opt)) {// stop review
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				
				bo.stopCategories(categories_id);
				bo.updateApproval_state("","","",categories_id, "3");
				this.getFormHM().put("errorcode", 0);
				
			} else if("10".equals(opt)) {// add person
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				ArrayList<MorphDynaBean> infoList = (ArrayList<MorphDynaBean>) this.getFormHM().get("infoList");
				int queue = (Integer) this.getFormHM().get("queue");
				String c_level = (String) this.getFormHM().get("c_level");

				ArrayList<String> w0501_eList = bo.isExist(w0301, infoList, review_links, categories_id);
				
				int errorcode = bo.addCategories_relations(categories_id, w0501_eList, c_level, queue, w0301, review_links);
				int personCount = bo.getCount(w0301,review_links,"0");
				this.getFormHM().put("errorcode", errorcode);
				this.getFormHM().put("personCount", personCount);
				
			} else if("11".equals(opt)) {// delete person
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				String w0501 = PubFunc.decrypt((String) this.getFormHM().get("w0501"));
				String c_level = (String) this.getFormHM().get("c_level");
				String queue = (String) this.getFormHM().get("queue");
				
				int flag = bo.deleteMeetingPerson(w0501, w0301, "",review_links);
				int errorcode = 0;
				if(flag == 1) {
					errorcode = bo.deleteCategories_relations(categories_id, w0501, c_level,queue);
				}
				int personCount = bo.getCount(w0301,review_links,"0");
				this.getFormHM().put("personCount", personCount);
				this.getFormHM().put("flag", flag);
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("12".equals(opt)) {// async table zc_personnel_categories
				int errorcode = bo.asyncTableCategories();
				this.getFormHM().put("errorcode", errorcode);
				
			} else if("13".equals(opt)) {// selectperson tableconfig
				String evaluationType = (String) this.getFormHM().get("evaluationType");//1:投票  2：评分
				String config = bo.getTableConfigForDiffSelPerson(w0301, review_links, evaluationType);
				this.getFormHM().put("tableConfig", config.toString());
				
			} else if("14".equals(opt)) {
				// =1 票数统计 	=2 分数统计
				String evaluationType = (String) this.getFormHM().get("evaluationType");
				// 为空：初次进入页面 ；不为空：快速查询
				String subModuleId = (String) this.getFormHM().get("subModuleId");
				boolean isFinished = (Boolean)this.getFormHM().get("isFinished");
				ReviewScorecountBo reviewScorecountBo = new ReviewScorecountBo(this.frameconn, this.userView, w0301, review_links);
				// 初次进入页面 
				reviewScorecountBo.setFinished(isFinished);
				if(StringUtils.isEmpty(subModuleId)){
					// 所选的分组id串
					String groupids = (null==this.getFormHM().get("groupids")) ? "" : (String) this.getFormHM().get("groupids");
					String[] grouplist = groupids.split(",");
					String decryptGroups = "";
					for(int i=0;i<grouplist.length;i++) {
						decryptGroups += PubFunc.decrypt(grouplist[i]);
						if(i<grouplist.length-1)
							decryptGroups += ",";
					}
					
					// id规则jobtitle_reviewfile_count1(=1分数=2票数)_w0301(会议id)_review_links
					if("2".equals(evaluationType)) 
						subModuleId = "jobtitle_reviewfile_count2_"+w0301+"_"+review_links;
					else if("1".equals(evaluationType)) {
						subModuleId = "jobtitle_reviewfile_count1_"+w0301+"_"+review_links;
						ContentDAO dao = new ContentDAO(frameconn);
						// ***1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
						reviewScorecountBo.asyncPersonNum(w0301, Integer.parseInt(review_links));
						//同步投票 状态
						ReviewMeetingPortalBo rmpbo = new ReviewMeetingPortalBo(userView,frameconn);
						RecordVo vo = new RecordVo("w03");
						vo.setString("w0301", w0301);
						vo = dao.findByPrimaryKey(vo);
						List<LazyDynaBean> beans = rmpbo.getXmlParamByW03(vo.getString("extend_param"));
						String rate_control = "";//1 通过率按2/3控制 =2 则不控制
						for(LazyDynaBean bean : beans) {
							if(bean.get("flag")==null)
								continue;
							String flag = (String) bean.get("flag");
							if(flag.equals(review_links)) {
								rate_control = (String) bean.get("rate_control");
								break;
							}
						}
						if("1".equals(rate_control) && !"3".equals(review_links)) {//同行不自动统计	
							reviewScorecountBo.asyncPersonNum(w0301, Integer.parseInt(review_links));
							reviewScorecountBo.asyncStatus(w0301);						
						}
					}
					// 获取表格对象配置
					String config = reviewScorecountBo.getTableConfigForDiff(subModuleId, evaluationType, decryptGroups);
					
					this.getFormHM().put("subModuleId", subModuleId);
					this.getFormHM().put("tableConfig", config.toString());
				} 
				
			} else if("15".equals(opt)) {// get approval_state Progress
				int segment=Integer.parseInt(review_links);//1:评委会阶段 2：学科组阶段 3：同行专家阶段 4、二级单位评议阶段
				String  usetype = (String) this.getFormHM().get("evaluationType");//1:投票  2：评分
				int type =(Integer) this.getFormHM().get("type");
				int evaluationType=Integer.parseInt(usetype);
				String encodeCateIds=(String) this.getFormHM().get("encodeCateIds");
				OutPositionalStaffBo opsb=new OutPositionalStaffBo(this.frameconn, this.userView,w0301,segment,type);
				//生成账号密码并获取导出数据list
				ArrayList outExcelList=opsb.getOutExcelList(w0301,segment,type,encodeCateIds);
				//学科组id name 的map
				Map subjectGroupIdToNameMap=opsb.GetSubjectGroupIdToNameMap();
				//导出excel
				String fileName="";
				if(!outExcelList.isEmpty())
					fileName=opsb.exportReviewAccounts(w0301, outExcelList, String.valueOf(type), review_links,subjectGroupIdToNameMap);
				this.getFormHM().put("fileName",PubFunc.encrypt(fileName));// 表格名传进前台

			}else if("16".equals(opt)) {
				// =1 票数统计 	=2 分数统计
				String evaluationType = (String) this.getFormHM().get("evaluationType");
				// 1:未全部完成评审，直接归档  0||空需要判断
				String type = (String)this.getFormHM().get("type");
				
				ReviewScorecountBo reviewScorecountBo = new ReviewScorecountBo(this.frameconn, this.userView, w0301, review_links);
				String msg = reviewScorecountBo.countResultsArchiving(evaluationType, type);
				
				if(!"1".equals(type) && !"归档成功！".equalsIgnoreCase(msg))
					this.getFormHM().put("type", "1");
				
				this.getFormHM().put("msg", msg);
			}else if("17".equals(opt)) {
				// 保存通过率参数配置
				String rate_control = (String) formHM.get("rate_control");
				bo.saveRateControl(w0301,review_links,rate_control);
			}else if("18".equals(opt)) {//材料公示需要的指标和分组，分组名的集合
				String list = (String)this.getFormHM().get("list");//noticefielditems节点的值
				String type = (String)this.getFormHM().get("type");//type为1表示新增或者修改Str_value的值，type为2时表示查询Str_value的节点的值
				String evaluationType = (String) this.getFormHM().get("evaluationType");//1:投票  2：评分
				String userType = (String) this.getFormHM().get("userType");//1：随机账号，2：非随机，选人的
				ReviewFileBo reviewFileBo = new ReviewFileBo(this.getFrameconn(), this.userView);// 工具类
				String msg = reviewFileBo.changeConstant(list, type);
				
				ArrayList<String> cateIdWithNameList = bo.getCateIdName(w0301,review_links,userType);
				LinkedHashMap<String,String> w05ItemMap = bo.getW05Item(w0301,review_links,evaluationType);
				
				this.getFormHM().put("msg",msg);
				this.getFormHM().put("cateIdWithNameList", cateIdWithNameList);
				this.getFormHM().put("w05ItemMap", w05ItemMap);
			}else if("19".equals(opt)) {//学科组评审人分组切换学科组，同时操作zc_expert_user表，更新
				String group_id = (String)this.getFormHM().get("group_id");
				group_id = StringUtils.isBlank(group_id)?"":PubFunc.decrypt(group_id);
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				String count = (String) this.getFormHM().get("count");
				
				bo.updateZcExpertUser(count,group_id,categories_id);
			}else if("20".equals(opt)) {
				String evaluationType = (String) this.getFormHM().get("evaluationType");//1:投票  2：评分
				HashMap<String,String> personVoteDataMap = bo.getTimeData(w0301,review_links,evaluationType);
				//为了显示有哪些账号已经评价过
				HashMap<String, String> categoriesmap = bo.getCategoriesMap(w0301, review_links,evaluationType);
				this.getFormHM().put("personVoteDataMap", personVoteDataMap);
				this.getFormHM().put("categoriesmap", categoriesmap);
			}else if("21".equals(opt)) {//选择框MAP数据 : 获取状态不为结束的分组map(生成账号密码用到)
				String exportType = (String) this.getFormHM().get("exportType");
				String userType = (String) this.getFormHM().get("userType");//1：随机账号，2：非随机，选人的
				ArrayList<String> personCateList = bo.getCateIdNameNotEnd(w0301,review_links,exportType,userType);
				this.getFormHM().put("personCateList", personCateList);
			}else if("22".equals(opt)) {//手工置为已结束
				String categories_id = PubFunc.decrypt((String) this.getFormHM().get("categories_id_e"));
				String type = (String)this.getFormHM().get("type");
				String evaluationType = (String)this.getFormHM().get("evaluationType");
				if("1".equals(type)) {
					//校验是否存在未完成投票的人
					String msg = bo.checkToEnd(evaluationType, w0301, categories_id, review_links);
					if(StringUtils.isNotBlank(msg)) {
						formHM.put("confirmMsg", msg);//提示未完成投票的人
						return;
					}
				}
				bo.setUpToEnd(categories_id);
			}else if("23".equals(opt)) {//保存投票状态
				String w0501 = PubFunc.decrypt((String)formHM.get("w0501_e"));
				String updateColumns = (String)formHM.get("updateColumn");
				if(StringUtils.isEmpty(updateColumns)) {
					return;
				}
				bo.saveVoteResult(w0501,updateColumns,formHM);
				
			}else if("24".equals(opt)) {//排序
				String ori_categories = (String)this.getFormHM().get("ori_categories");
				int ori_seq = Integer.valueOf((String)this.getFormHM().get("ori_seq"));
				int to_seq = Integer.valueOf((String)this.getFormHM().get("to_seq"));
				String userType = (String) this.getFormHM().get("userType");//1：随机账号，2：非随机，选人的
				bo.sortCategories(w0301, review_links, userType, ori_categories, ori_seq, to_seq);
			}else if("25".equals(opt)) {//组内人员排序
				String ori_categories = PubFunc.decrypt((String)this.getFormHM().get("ori_categories"));
				String w0501_e = PubFunc.decrypt((String)this.getFormHM().get("w0501_e"));
				int to_seq = Integer.valueOf((String)this.getFormHM().get("to_seq"));
				bo.sortPerson(ori_categories, w0501_e, to_seq);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
}
