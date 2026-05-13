/*     */ package flex.messaging.io;
/*     */ 
/*     */ import flex.messaging.MessageException;
/*     */ import java.sql.SQLException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.sql.RowSet;
/*     */ 
/*     */ public class PageableRowSetProxy extends AbstractProxy
/*     */ {
/*     */   static final long serialVersionUID = 1121859941216924326L;
/*     */   public static final int HUGE_PAGE_SIZE = 2147483647;
/*     */   public static final String AS_TYPE_NAME = "RecordSet";
/*  33 */   public static final Integer RECORD_SET_VERSION = new Integer(1);
/*     */   public static final String TOTAL_COUNT = "totalCount";
/*     */   public static final String COLUMN_NAMES = "columnNames";
/*     */   public static final String INITIAL_DATA = "initialData";
/*     */   public static final String SERVICE_NAME = "serviceName";
/*     */   public static final String SERVER_INFO = "serverInfo";
/*     */   public static final String VERSION = "version";
/*     */   public static final String CURSOR = "cursor";
/*     */   public static final String ID = "id";
/*  47 */   public static final List propertyNameCache = new ArrayList();
/*     */ 
/*     */   public PageableRowSetProxy()
/*     */   {
/*  55 */     super(null);
/*  56 */     this.alias = "RecordSet";
/*     */   }
/*     */ 
/*     */   public PageableRowSetProxy(RowSet defaultInstance)
/*     */   {
/*  61 */     super(defaultInstance);
/*  62 */     this.alias = "RecordSet";
/*     */   }
/*     */ 
/*     */   public PageableRowSetProxy(PageableRowSet defaultInstance)
/*     */   {
/*  67 */     super(defaultInstance);
/*  68 */     this.alias = "RecordSet";
/*     */   }
/*     */ 
/*     */   public String getAlias(Object instance)
/*     */   {
/*  73 */     return "RecordSet";
/*     */   }
/*     */ 
/*     */   public List getPropertyNames(Object instance)
/*     */   {
/*  78 */     return propertyNameCache;
/*     */   }
/*     */ 
/*     */   public Class getType(Object instance, String propertyName)
/*     */   {
/*  83 */     if ("serverInfo".equals(propertyName))
/*     */     {
/*  85 */       return HashMap.class;
/*     */     }
/*     */ 
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public Object getValue(Object instance, String propertyName)
/*     */   {
/*  95 */     Object value = null;
/*     */ 
/*  97 */     if ((instance instanceof RowSet))
/*     */     {
/* 100 */       instance = new PagedRowSet((RowSet)instance, 2147483647, false);
/*     */     }
/*     */ 
/* 103 */     if ((instance instanceof PageableRowSet))
/*     */     {
/* 105 */       PageableRowSet prs = (PageableRowSet)instance;
/*     */ 
/* 107 */       if ("serverInfo".equals(propertyName))
/*     */       {
/*     */         try
/*     */         {
/* 111 */           HashMap serverInfo = new HashMap();
/* 112 */           serverInfo.put("id", prs.getID());
/*     */ 
/* 114 */           Map pageInfo = prs.getRecords(1, prs.getInitialDownloadCount());
/*     */ 
/* 116 */           serverInfo.put("totalCount", new Integer(prs.getRowCount()));
/* 117 */           serverInfo.put("initialData", pageInfo.get("Page"));
/* 118 */           serverInfo.put("cursor", pageInfo.get("Cursor"));
/* 119 */           serverInfo.put("serviceName", prs.getServiceName());
/* 120 */           serverInfo.put("columnNames", prs.getColumnNames());
/* 121 */           serverInfo.put("version", RECORD_SET_VERSION);
/* 122 */           value = serverInfo;
/*     */         }
/*     */         catch (SQLException ex)
/*     */         {
/* 126 */           MessageException e = new MessageException();
/* 127 */           e.setMessage("Error encountered serializing RowSet.");
/* 128 */           e.setRootCause(ex);
/* 129 */           throw e;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 134 */     return value;
/*     */   }
/*     */ 
/*     */   public void setValue(Object instance, String propertyName, Object value)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 144 */     PageableRowSetProxy proxy = new PageableRowSetProxy();
/* 145 */     proxy.setCloneFieldsFrom(this);
/* 146 */     return proxy;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  50 */     propertyNameCache.add("serverInfo");
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     flex.messaging.io.PageableRowSetProxy
 * JD-Core Version:    0.6.0
 */