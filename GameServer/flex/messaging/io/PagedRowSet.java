/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.util.UUIDUtils;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.RowSet;
/*     */ 
/*     */ public class PagedRowSet
/*     */   implements PageableRowSet
/*     */ {
/*     */   private RowSet rowSet;
/*     */   private String[] colNames;
/*  44 */   private int pageSize = 50;
/*  45 */   private int colCount = 0;
/*  46 */   private int rowCount = 0;
/*     */ 
/*  48 */   private String id = null;
/*  49 */   private String serviceName = null;
/*     */   public static final String DEFAULT_PAGING_SERVICE_NAME = "PageableRowSetCache";
/*     */ 
/*     */   public PagedRowSet(RowSet r, int p)
/*     */   {
/*  68 */     this.serviceName = "PageableRowSetCache";
/*  69 */     this.rowSet = r;
/*  70 */     this.pageSize = p;
/*  71 */     this.id = UUIDUtils.createUUID();
/*  72 */     init();
/*     */   }
/*     */ 
/*     */   public PagedRowSet(RowSet r, int p, boolean createID)
/*     */   {
/*  82 */     this.serviceName = "PageableRowSetCache";
/*  83 */     this.rowSet = r;
/*  84 */     this.pageSize = p;
/*  85 */     if (createID)
/*     */     {
/*  87 */       this.id = UUIDUtils.createUUID();
/*     */     }
/*  89 */     init();
/*     */   }
/*     */ 
/*     */   private void init()
/*     */   {
/*  94 */     if (this.rowSet != null)
/*     */     {
/*  97 */       initColumns();
/*     */ 
/* 100 */       initRecords();
/*     */     }
/*     */     else
/*     */     {
/* 104 */       this.colNames = new String[0];
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void initColumns()
/*     */   {
/*     */     try
/*     */     {
/* 113 */       ResultSetMetaData rsmd = this.rowSet.getMetaData();
/* 114 */       if (rsmd != null)
/*     */       {
/* 116 */         this.colCount = rsmd.getColumnCount();
/*     */       }
/*     */     }
/*     */     catch (SQLException ex)
/*     */     {
/* 121 */       this.colCount = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   private synchronized void initRecords()
/*     */   {
/* 129 */     if (this.rowSet != null)
/*     */     {
/*     */       try
/*     */       {
/* 133 */         int currentIndex = this.rowSet.getRow();
/*     */ 
/* 136 */         if (this.rowSet.last())
/*     */         {
/* 138 */           this.rowCount = this.rowSet.getRow();
/*     */         }
/*     */ 
/* 142 */         if (currentIndex > 0)
/*     */         {
/* 144 */           this.rowSet.absolute(currentIndex);
/*     */         }
/*     */         else
/*     */         {
/* 148 */           this.rowSet.beforeFirst();
/*     */         }
/*     */ 
/*     */       }
/*     */       catch (SQLException ex)
/*     */       {
/*     */         try
/*     */         {
/* 156 */           this.rowSet.first();
/*     */         }
/*     */         catch (SQLException se)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getColumnNames()
/*     */     throws SQLException
/*     */   {
/* 176 */     if (this.colNames == null)
/*     */     {
/*     */       try
/*     */       {
/* 181 */         if (this.colCount == 0)
/*     */         {
/* 183 */           initColumns();
/*     */         }
/*     */ 
/* 186 */         this.colNames = new String[this.colCount];
/*     */ 
/* 188 */         for (int i = 0; i < this.colCount; i++)
/*     */         {
/* 191 */           this.colNames[i] = this.rowSet.getMetaData().getColumnName(i + 1);
/*     */         }
/*     */       }
/*     */       catch (SQLException ex)
/*     */       {
/* 196 */         this.colNames = new String[0];
/*     */       }
/*     */     }
/*     */ 
/* 200 */     return this.colNames;
/*     */   }
/*     */ 
/*     */   public synchronized Map getRecords(int startIndex, int count)
/*     */     throws SQLException
/*     */   {
/* 213 */     List aRecords = new ArrayList();
/*     */ 
/* 216 */     if (this.colCount == 0)
/*     */     {
/* 218 */       initColumns();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 224 */       if (startIndex < 1) {
/* 225 */         startIndex = 1;
/*     */       }
/*     */ 
/* 228 */       if (this.rowSet.absolute(startIndex))
/*     */       {
/* 231 */         for (int i = 0; i < count; i++)
/*     */         {
/* 233 */           boolean hasNext = true;
/*     */           List row;
/* 237 */           if (this.colCount > 0)
/*     */           {
/* 239 */             List row = new ArrayList(this.rowCount + 1);
/*     */ 
/* 241 */             for (int j = 1; j <= this.colCount; j++)
/*     */             {
/* 243 */               row.add(this.rowSet.getObject(j));
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 248 */             row = new ArrayList();
/*     */             try
/*     */             {
/* 254 */               for (int j = 1; j <= 50; j++)
/*     */               {
/* 256 */                 Object o = this.rowSet.getObject(j);
/* 257 */                 if (o == null)
/*     */                   break;
/* 259 */                 row.add(o);
/*     */               }
/*     */ 
/*     */             }
/*     */             catch (SQLException ex)
/*     */             {
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 273 */           aRecords.add(row.toArray());
/*     */ 
/* 275 */           hasNext = this.rowSet.next();
/*     */ 
/* 278 */           if (!hasNext)
/*     */           {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (SQLException ex)
/*     */     {
/* 287 */       throw ex;
/*     */     }
/*     */ 
/* 290 */     Map result = new HashMap(2);
/* 291 */     result.put("Page", aRecords.toArray());
/* 292 */     result.put("Cursor", new Integer(startIndex));
/*     */ 
/* 294 */     return result;
/*     */   }
/*     */ 
/*     */   public int getRowCount()
/*     */   {
/* 302 */     return this.rowCount;
/*     */   }
/*     */ 
/*     */   public int getInitialDownloadCount()
/*     */   {
/* 313 */     return this.pageSize;
/*     */   }
/*     */ 
/*     */   public String getID()
/*     */   {
/* 318 */     return this.id;
/*     */   }
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/* 326 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */   public void setServicename(String serviceName)
/*     */   {
/* 334 */     this.serviceName = serviceName;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.PagedRowSet
 * JD-Core Version:    0.6.0
 */