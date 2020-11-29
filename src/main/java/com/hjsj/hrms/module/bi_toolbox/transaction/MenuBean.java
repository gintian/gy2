package com.hjsj.hrms.module.bi_toolbox.transaction;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class MenuBean {
    private String menuid;
    private String name;
    private String url;
    private List<MenuBean> children;

    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MenuBean> getChildren() {
        return children;
    }

    public void setChildren(List<MenuBean> children) {
        this.children = children;
    }
    @Override
    public String toString(){
        String str = "";
        String children = "";
        List<MenuBean> list = this.getChildren();
        if(list!=null&&list.size()>0){
            for(MenuBean bean: list){
                if(bean!=null){
                    children += "{" + bean.toString() + "},";
                }
            }
            if(StringUtils.isNotBlank(children)){
                children = children.substring(0,children.length()-1);
            }
        }
        if(StringUtils.isNotBlank(this.menuid)){
            str += "menuid:'"+this.getMenuid()+"',";
            str += "name:'"+this.getName()+"',";
            str += "url:'"+this.getUrl()+"',";
            str += "children:["+children+"]";
        }
        return str;
    }
}
