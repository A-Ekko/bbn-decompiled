/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.util.StringTokenizer;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
/*     */ 
/*     */ class XMLFileHandler extends DefaultHandler
/*     */ {
/*     */   private static final String TAG_EVENT = "log4j:event";
/*     */   private static final String TAG_MESSAGE = "log4j:message";
/*     */   private static final String TAG_NDC = "log4j:NDC";
/*     */   private static final String TAG_THROWABLE = "log4j:throwable";
/*     */   private static final String TAG_LOCATION_INFO = "log4j:locationInfo";
/*     */   private final MyTableModel mModel;
/*     */   private int mNumEvents;
/*     */   private long mTimeStamp;
/*     */   private Priority mPriority;
/*     */   private String mCategoryName;
/*     */   private String mNDC;
/*     */   private String mThreadName;
/*     */   private String mMessage;
/*     */   private String[] mThrowableStrRep;
/*     */   private String mLocationDetails;
/*  58 */   private final StringBuffer mBuf = new StringBuffer();
/*     */ 
/*     */   XMLFileHandler(MyTableModel aModel)
/*     */   {
/*  66 */     this.mModel = aModel;
/*     */   }
/*     */ 
/*     */   public void startDocument()
/*     */     throws SAXException
/*     */   {
/*  73 */     this.mNumEvents = 0;
/*     */   }
/*     */ 
/*     */   public void characters(char[] aChars, int aStart, int aLength)
/*     */   {
/*  78 */     this.mBuf.append(String.valueOf(aChars, aStart, aLength));
/*     */   }
/*     */ 
/*     */   public void endElement(String aNamespaceURI, String aLocalName, String aQName)
/*     */   {
/*  86 */     if ("log4j:event".equals(aQName)) {
/*  87 */       addEvent();
/*  88 */       resetData();
/*  89 */     } else if ("log4j:NDC".equals(aQName)) {
/*  90 */       this.mNDC = this.mBuf.toString();
/*  91 */     } else if ("log4j:message".equals(aQName)) {
/*  92 */       this.mMessage = this.mBuf.toString();
/*  93 */     } else if ("log4j:throwable".equals(aQName)) {
/*  94 */       StringTokenizer st = new StringTokenizer(this.mBuf.toString(), "\n\t");
/*     */ 
/*  96 */       this.mThrowableStrRep = new String[st.countTokens()];
/*  97 */       if (this.mThrowableStrRep.length > 0) {
/*  98 */         this.mThrowableStrRep[0] = st.nextToken();
/*  99 */         for (int i = 1; i < this.mThrowableStrRep.length; i++)
/* 100 */           this.mThrowableStrRep[i] = ("\t" + st.nextToken());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void startElement(String aNamespaceURI, String aLocalName, String aQName, Attributes aAtts)
/*     */   {
/* 112 */     this.mBuf.setLength(0);
/*     */ 
/* 114 */     if ("log4j:event".equals(aQName)) {
/* 115 */       this.mThreadName = aAtts.getValue("thread");
/* 116 */       this.mTimeStamp = Long.parseLong(aAtts.getValue("timestamp"));
/* 117 */       this.mCategoryName = aAtts.getValue("logger");
/* 118 */       this.mPriority = Priority.toPriority(aAtts.getValue("level"));
/* 119 */     } else if ("log4j:locationInfo".equals(aQName)) {
/* 120 */       this.mLocationDetails = (aAtts.getValue("class") + "." + aAtts.getValue("method") + "(" + aAtts.getValue("file") + ":" + aAtts.getValue("line") + ")");
/*     */     }
/*     */   }
/*     */ 
/*     */   int getNumEvents()
/*     */   {
/* 129 */     return this.mNumEvents;
/*     */   }
/*     */ 
/*     */   private void addEvent()
/*     */   {
/* 138 */     this.mModel.addEvent(new EventDetails(this.mTimeStamp, this.mPriority, this.mCategoryName, this.mNDC, this.mThreadName, this.mMessage, this.mThrowableStrRep, this.mLocationDetails));
/*     */ 
/* 146 */     this.mNumEvents += 1;
/*     */   }
/*     */ 
/*     */   private void resetData()
/*     */   {
/* 151 */     this.mTimeStamp = 0L;
/* 152 */     this.mPriority = null;
/* 153 */     this.mCategoryName = null;
/* 154 */     this.mNDC = null;
/* 155 */     this.mThreadName = null;
/* 156 */     this.mMessage = null;
/* 157 */     this.mThrowableStrRep = null;
/* 158 */     this.mLocationDetails = null;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.chainsaw.XMLFileHandler
 * JD-Core Version:    0.6.0
 */