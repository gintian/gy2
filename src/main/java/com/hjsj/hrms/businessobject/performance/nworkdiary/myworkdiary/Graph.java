package com.hjsj.hrms.businessobject.performance.nworkdiary.myworkdiary;

import java.util.ArrayList;

public class Graph {
 // 存储节点信息
 private float[] vertices;
 // 存储边的信息
 private int[][] arcs;
 private int vexnum;
 // 记录第i个节点是否被访问过
 private boolean[] visited;

 public Graph(int n) {
  vexnum = n;
  vertices = new float[n];
  arcs = new int[n][n];
  visited = new boolean[n];
  for (int i = 0; i < vexnum; i++) {
     for (int j = 0; j < vexnum; j++) {
     arcs[i][j] = 0;
     }
  }

 }

 public void addVertex(float[] obj) {
  this.vertices = obj;
 }

 public void addEdge(int i, int j) {
  if (i == j)return;
  arcs[i][j] = 1;
  arcs[j][i] = 1;
 }

 public int firstAdjVex(int i) {
  for (int j = 0; j < vexnum; j++) {
   if (arcs[i][j] > 0)
    return j;
  }
  return -1;
 }

 public int nextAdjVex(int i, int k) {
  for (int j = k + 1; j < vexnum; j++) {
   if (arcs[i][j] > 0)
    return j;
  }
  return -1;
 }

 // 深度优先遍历
 public void depthTraverse(ArrayList totallist) {
	  for (int i = 0; i < vexnum; i++) {
	   visited[i] = false;
	  }
	  for (int i = 0; i < vexnum; i++) {
		  ArrayList list = new ArrayList();
		  if (!visited[i])
			  traverse(list,i);
		  totallist.add(list);
	  }
 }

 // 一个连通图的深度递归遍历
 public void traverse(ArrayList list,int i) {
  // TODO Auto-generated method stub
  visited[i] = true;
  visit(list,i);
  for (int j = this.firstAdjVex(i); j >= 0; j = this.nextAdjVex(i, j)) {
   if (!visited[j])
    this.traverse(list,j);
  }
 }


 private void visit(ArrayList list,int i) {
  list.add(vertices[i]+"");
 }

}

