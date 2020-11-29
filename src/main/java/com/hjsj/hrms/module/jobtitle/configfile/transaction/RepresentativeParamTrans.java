package com.hjsj.hrms.module.jobtitle.configfile.transaction;

import com.hjsj.hrms.module.jobtitle.configfile.businessobject.RepresentativeMaterialsBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 代表作交易类
 * @author Administrator
 *
 */
public class RepresentativeParamTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		//firstStep secStep  thirdStep  fourthStep
		String flag=(String)this.getFormHM().get("flag");
		try {
			RepresentativeMaterialsBo bo=new RepresentativeMaterialsBo(this.userView, this.frameconn);
			if("firstStep".equals(flag)) {//第一步读取配置文件
				//申报人基础模板  //代表作摘要模板
				String type=(String)this.getFormHM().get("type");
				this.getFormHM().remove("type");
				if("1".equals(type)||"2".equals(type)) {
					String fullpath=(String)this.getFormHM().get("fullpath");//文件上传路径
					String localname=(String)this.getFormHM().get("localname");//原文件名称
					if(StringUtils.isEmpty(fullpath)||StringUtils.isEmpty(localname)) 
						throw new Exception("上传文件无效，请重新上传!");
					fullpath=PubFunc.decrypt(fullpath);
					File file=new File(fullpath);
					bo.getYunTemplateContext(file, localname, Integer.parseInt(type));
				}else if("3".equals(type)) {
					String tempid=(String)this.getFormHM().get("tempid");
					boolean isSame = bo.compareTemid(tempid);
					if(!isSame) {
						bo.delParamValueByKey("field_mapping");
						bo.delParamValueByKey("code_set");
						//bo.delParamValueByKey("other_materials");
						bo.delParamValueByKey("sub_filedMap");
						//
						//
					}
					bo.setTemplateMap(tempid);
				}else {
					HashMap map= RepresentativeMaterialsBo.getParamMap();
					if(map==null||map.size()==0) {
						bo.getParamValue();
					}
					if(map.containsKey("ids")) {
						List idList=(List)map.get("ids");//人事异动模板
						this.getFormHM().put("ids", idList);
					}
					
					String application_fileName=(String)map.get("application_fileName");
					String representative_fileName=(String)map.get("representative_fileName");
					
					this.getFormHM().put("application_fileName", application_fileName);
					this.getFormHM().put("representative_fileName", representative_fileName);
				}
			}else if("secStep".equals(flag)) {
				String type=(String)this.getFormHM().get("type");
				String tabids ="";
				if(RepresentativeMaterialsBo.getParamMap().containsKey("ids")) {
					List tabidlist = (List) RepresentativeMaterialsBo.getParamMap().get("ids");
					for(int i=0;i<tabidlist.size();i++) {
						String id = (String)tabidlist.get(i);
						String tabid = id.split(":")[0];
						if(i==0) {
							tabids = tabid;
						}else
							tabids+=","+tabid;
					}
				}
				if("0".equals(type)) {
					ArrayList itemlist = bo.getFieldList(tabids);
					JSONArray jsonArray = JSONArray.fromObject(itemlist);//将封装结果转换json格式
					this.getFormHM().put("list", jsonArray);//最终指标json串
					HashMap map = (HashMap) RepresentativeMaterialsBo.getParamMap().get("application_materials")==null?new HashMap():(HashMap) RepresentativeMaterialsBo.getParamMap().get("application_materials");
					LinkedList fieldlist = (LinkedList)map.get("fieldlist")==null?new LinkedList():(LinkedList)map.get("fieldlist");
					this.getFormHM().put("gridlist", fieldlist);
					this.getFormHM().put("codemap", map);
					HashMap fieldmappingmap = new HashMap();
					HashMap codesetmap = new HashMap();
					JSONObject fieldmapping = new JSONObject();
					JSONObject codeset = new JSONObject();
					if(RepresentativeMaterialsBo.getParamMap().containsKey("field_mapping")) {
						fieldmappingmap = (HashMap) RepresentativeMaterialsBo.getParamMap().get("field_mapping");
						fieldmapping = JSONObject.fromObject(fieldmappingmap);
					}
					if(RepresentativeMaterialsBo.getParamMap().containsKey("code_set")) {
						codesetmap = (HashMap) RepresentativeMaterialsBo.getParamMap().get("code_set");
						codeset = JSONObject.fromObject(codesetmap);
					}
					this.getFormHM().put("fieldmappingmap", fieldmapping);
					this.getFormHM().put("codesetmap", codeset);
				}else if("1".equals(type)) {
					String codesetid = (String)this.getFormHM().get("codesetid");
					ArrayList itemlist = bo.getCodeList(codesetid);
					JSONArray jsonArray = JSONArray.fromObject(itemlist);//将封装结果转换json格式
					this.getFormHM().put("list", jsonArray);//最终指标json串
				}else if("2".equals(type)) {
					MorphDynaBean fieldmappingmdb = (MorphDynaBean)this.getFormHM().get("fieldmappingmap");
					HashMap fieldmappingmap = PubFunc.DynaBean2Map(fieldmappingmdb);
					MorphDynaBean codesetmdb = (MorphDynaBean)this.getFormHM().get("codesetmap");
					HashMap codesetmap = PubFunc.DynaBean2Map(codesetmdb);
					for(Object key:codesetmap.keySet()) {
						ArrayList codesetlist = (ArrayList) codesetmap.get(key);
						ArrayList list  = new ArrayList();
						for(int i=0;i<codesetlist.size();i++) {
							MorphDynaBean mdb = (MorphDynaBean) codesetlist.get(i);
							HashMap map = PubFunc.DynaBean2Map(mdb);
							list.add(map);
						}
						codesetmap.put(key, list);
					}
					RepresentativeMaterialsBo.getParamMap().put("field_mapping", fieldmappingmap);
					RepresentativeMaterialsBo.getParamMap().put("code_set", codesetmap);
				}
			}
			else if("thirdStep".equals(flag)) {
				String type=(String)this.getFormHM().get("type");//0 查看  1 保存
				this.getFormHM().remove("type");
				if("0".equals(type)) {
					HashMap map= RepresentativeMaterialsBo.getParamMap();
					HashMap repreMap=null;
					if(map!=null) {
						if(map.containsKey("representative_materials")) {
							repreMap=(HashMap)map.get("representative_materials");
						}
						if(map.containsKey("ids")) {
							List<String> ids=(List<String>)map.get("ids");
							HashMap tempParamMap=new HashMap();
							for(String id:ids) {
								tempParamMap.put(id, bo.getTemplateSetInfo(id));
							}
							if(map.get("sub_filedMap")!=null) {
								this.getFormHM().put("sub_filedMap", map.get("sub_filedMap"));	
							}
							this.getFormHM().put("temp_ids", ids);	
							this.getFormHM().put("temlate_Param", tempParamMap);
						}
					}
					this.getFormHM().put("fieldMap", repreMap);
					
				}else if("1".equals(type)){
					HashMap<String,ArrayList> template_map=PubFunc.DynaBean2Map((MorphDynaBean)this.getFormHM().get("temlate_Param"));
					HashMap<String,HashMap<String,String>> sub_filedMap=PubFunc.DynaBean2Map((MorphDynaBean)this.getFormHM().get("sub_filedMap"));
					bo.updateRepresentativeParaMap(template_map, sub_filedMap);
				}
			}else if("fourthStep".equals(flag)) {
				String type=(String)this.getFormHM().get("type");
				if("0".equals(type)) {
					List tabidlist = new ArrayList();
					String tabids = "";
					if(RepresentativeMaterialsBo.getParamMap().containsKey("ids")) {
						tabidlist = (List) RepresentativeMaterialsBo.getParamMap().get("ids");
						for(int i=0;i<tabidlist.size();i++) {
							String id = (String)tabidlist.get(i);
							String tabid = id.split(":")[0];
							if(i==0) {
								tabids = tabid;
							}else
								tabids+=","+tabid;
						}
					}
					HashMap othermap = StringUtils.isNotEmpty(tabids)?bo.getOtherMateriaList(tabids):new HashMap();
					JSONObject othermateria = JSONObject.fromObject(othermap);
					this.getFormHM().put("othermateria", othermateria);
					JSONObject other_materials = new JSONObject();
					HashMap other_materialsmdb = new HashMap();
					if(RepresentativeMaterialsBo.getParamMap().containsKey("other_materials")) {
						other_materialsmdb = (HashMap) RepresentativeMaterialsBo.getParamMap().get("other_materials");
						other_materials = JSONObject.fromObject(other_materialsmdb);
					}
					this.getFormHM().put("other_materialsmap", other_materials);
				}else if("1".equals(type)) {
					MorphDynaBean othermaterialsobj = (MorphDynaBean)this.getFormHM().get("othermaterialsobj");
					HashMap othermaterials = PubFunc.DynaBean2Map(othermaterialsobj);
					RepresentativeMaterialsBo.getParamMap().put("other_materials", othermaterials);
				}else if("2".equals(type)) {//保存所有设置
					MorphDynaBean othermaterialsobj = (MorphDynaBean)this.getFormHM().get("othermaterialsobj");
					HashMap othermaterials = PubFunc.DynaBean2Map(othermaterialsobj);
					RepresentativeMaterialsBo.getParamMap().put("other_materials", othermaterials);
					//解析静态变量paramMap
					bo.analysisParamMap();
				}
			}else if("otherStep".equals(flag)) {
				bo.getParamValue();
			}
			this.getFormHM().put("status", true);
		} catch (Exception e) {
			this.getFormHM().put("status", false);
			this.getFormHM().put("eMsg", e.getMessage());
			e.printStackTrace();
		}
	}
}
