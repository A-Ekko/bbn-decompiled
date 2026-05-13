/*     */ package org.apache.log4j.lf5.viewer.categoryexplorer;
/*     */ 
/*     */ import java.util.LinkedList;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class CategoryPath
/*     */ {
/*  31 */   protected LinkedList _categoryElements = new LinkedList();
/*     */ 
/*     */   public CategoryPath()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CategoryPath(String category)
/*     */   {
/*  49 */     String processedCategory = category;
/*     */ 
/*  51 */     if (processedCategory == null) {
/*  52 */       processedCategory = "Debug";
/*     */     }
/*     */ 
/*  55 */     processedCategory.replace('/', '.');
/*  56 */     processedCategory = processedCategory.replace('\\', '.');
/*     */ 
/*  58 */     StringTokenizer st = new StringTokenizer(processedCategory, ".");
/*  59 */     while (st.hasMoreTokens()) {
/*  60 */       String element = st.nextToken();
/*  61 */       addCategoryElement(new CategoryElement(element));
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  73 */     int count = this._categoryElements.size();
/*     */ 
/*  75 */     return count;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  79 */     boolean empty = false;
/*     */ 
/*  81 */     if (this._categoryElements.size() == 0) {
/*  82 */       empty = true;
/*     */     }
/*     */ 
/*  85 */     return empty;
/*     */   }
/*     */ 
/*     */   public void removeAllCategoryElements()
/*     */   {
/*  93 */     this._categoryElements.clear();
/*     */   }
/*     */ 
/*     */   public void addCategoryElement(CategoryElement categoryElement)
/*     */   {
/* 100 */     this._categoryElements.addLast(categoryElement);
/*     */   }
/*     */ 
/*     */   public CategoryElement categoryElementAt(int index)
/*     */   {
/* 107 */     return (CategoryElement)this._categoryElements.get(index);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 112 */     StringBuffer out = new StringBuffer(100);
/*     */ 
/* 114 */     out.append("\n");
/* 115 */     out.append("===========================\n");
/* 116 */     out.append("CategoryPath:                   \n");
/* 117 */     out.append("---------------------------\n");
/*     */ 
/* 119 */     out.append("\nCategoryPath:\n\t");
/*     */ 
/* 121 */     if (size() > 0)
/* 122 */       for (int i = 0; i < size(); i++) {
/* 123 */         out.append(categoryElementAt(i).toString());
/* 124 */         out.append("\n\t");
/*     */       }
/*     */     else {
/* 127 */       out.append("<<NONE>>");
/*     */     }
/*     */ 
/* 130 */     out.append("\n");
/* 131 */     out.append("===========================\n");
/*     */ 
/* 133 */     return out.toString();
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.categoryexplorer.CategoryPath
 * JD-Core Version:    0.6.0
 */