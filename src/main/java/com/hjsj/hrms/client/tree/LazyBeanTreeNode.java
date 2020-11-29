/**
 * 
 */
package com.hjsj.hrms.client.tree;

import org.apache.commons.beanutils.LazyDynaBean;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author chenmengqing
 *
 */
public class LazyBeanTreeNode extends DefaultMutableTreeNode {
	/**
	 * 节点名称
	 */
	private String nodetext="node";
	/**
	 * 
	 */
	public LazyBeanTreeNode(String nodetext,LazyDynaBean userobject) {
		this.nodetext=nodetext;
		setUserObject(userobject);
	}

	@Override
    public String toString() {
		return nodetext;
	}
	
	
}
