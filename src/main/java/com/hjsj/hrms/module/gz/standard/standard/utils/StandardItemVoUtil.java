package com.hjsj.hrms.module.gz.standard.standard.utils;

import com.hrms.hjsj.sys.FieldItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Title 类名
 * @Description 类说明
 * @Company hjsj
 * @Author 编写人
 * @Date
 * @Version 1.0.0
 */

public class StandardItemVoUtil implements Serializable{

        private ArrayList h_List=new ArrayList();
        private ArrayList v_List=new ArrayList();
        private int  h_bottomColumn_num=0;
        private int  v_bottomColumn_num=0;
        private ArrayList gzItemList=new ArrayList();
        private String resultItemType="N";  // N ; C
        private String codesetid="";
        private FieldItem resultItem=null;

        private String hfactor="";
        private String s_hfactor="";
        private String vfactor="";
        private String s_vfactor="";
        private String item="";
        private String hcontent="";
        private String vcontent="";




        public ArrayList getGzItemList() {
            return gzItemList;
        }
        public void setGzItemList(ArrayList gzItemList) {
            this.gzItemList = gzItemList;
        }
        public int getH_bottomColumn_num() {
            return h_bottomColumn_num;
        }
        public void setH_bottomColumn_num(int column_num) {
            h_bottomColumn_num = column_num;
        }
        public ArrayList getH_List() {
            return h_List;
        }
        public void setH_List(ArrayList list) {
            h_List = list;
        }
        public int getV_bottomColumn_num() {
            return v_bottomColumn_num;
        }
        public void setV_bottomColumn_num(int column_num) {
            v_bottomColumn_num = column_num;
        }
        public ArrayList getV_List() {
            return v_List;
        }
        public void setV_List(ArrayList list) {
            v_List = list;
        }
        public String getCodesetid() {
            return codesetid;
        }
        public void setCodesetid(String codesetid) {
            this.codesetid = codesetid;
        }
        public String getResultItemType() {
            return resultItemType;
        }
        public void setResultItemType(String resultItemType) {
            this.resultItemType = resultItemType;
        }
        public String getHcontent() {
            return hcontent;
        }
        public void setHcontent(String hcontent) {
            this.hcontent = hcontent;
        }
        public String getHfactor() {
            return hfactor;
        }
        public void setHfactor(String hfactor) {
            this.hfactor = hfactor;
        }
        public String getItem() {
            return item;
        }
        public void setItem(String item) {
            this.item = item;
        }
        public String getS_hfactor() {
            return s_hfactor;
        }
        public void setS_hfactor(String s_hfactor) {
            this.s_hfactor = s_hfactor;
        }
        public String getS_vfactor() {
            return s_vfactor;
        }
        public void setS_vfactor(String s_vfactor) {
            this.s_vfactor = s_vfactor;
        }
        public String getVcontent() {
            return vcontent;
        }
        public void setVcontent(String vcontent) {
            this.vcontent = vcontent;
        }
        public String getVfactor() {
            return vfactor;
        }
        public void setVfactor(String vfactor) {
            this.vfactor = vfactor;
        }
        public FieldItem getResultItem() {
            return resultItem;
        }
        public void setResultItem(FieldItem resultItem) {
            this.resultItem = resultItem;
        }

}
