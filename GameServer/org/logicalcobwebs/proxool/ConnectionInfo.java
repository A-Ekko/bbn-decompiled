/*     */ package org.logicalcobwebs.proxool;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import org.logicalcobwebs.proxool.util.FastArrayList;
/*     */ 
/*     */ class ConnectionInfo
/*     */   implements ConnectionInfoIF
/*     */ {
/*     */   private Date birthDate;
/*     */   private long age;
/*     */   private long id;
/*     */   private int mark;
/*     */   private int status;
/*     */   private long timeLastStartActive;
/*     */   private long timeLastStopActive;
/*     */   private String requester;
/*     */   private String delegateDriver;
/*     */   private String delegateUrl;
/*     */   private String proxyHashcode;
/*     */   private String delegateHashcode;
/*  48 */   private List sqlCalls = new FastArrayList();
/*     */ 
/*     */   public Date getBirthDate() {
/*  51 */     return this.birthDate;
/*     */   }
/*     */ 
/*     */   public long getBirthTime() {
/*  55 */     return this.birthDate.getTime();
/*     */   }
/*     */ 
/*     */   public void setBirthDate(Date birthDate) {
/*  59 */     this.birthDate = birthDate;
/*     */   }
/*     */ 
/*     */   public long getAge() {
/*  63 */     return this.age;
/*     */   }
/*     */ 
/*     */   public void setAge(long age) {
/*  67 */     this.age = age;
/*     */   }
/*     */ 
/*     */   public long getId() {
/*  71 */     return this.id;
/*     */   }
/*     */ 
/*     */   public void setId(long id) {
/*  75 */     this.id = id;
/*     */   }
/*     */ 
/*     */   public int getMark() {
/*  79 */     return this.mark;
/*     */   }
/*     */ 
/*     */   public void setMark(int mark) {
/*  83 */     this.mark = mark;
/*     */   }
/*     */ 
/*     */   public int getStatus() {
/*  87 */     return this.status;
/*     */   }
/*     */ 
/*     */   public void setStatus(int status) {
/*  91 */     this.status = status;
/*     */   }
/*     */ 
/*     */   public long getTimeLastStartActive() {
/*  95 */     return this.timeLastStartActive;
/*     */   }
/*     */ 
/*     */   public void setTimeLastStartActive(long timeLastStartActive) {
/*  99 */     this.timeLastStartActive = timeLastStartActive;
/*     */   }
/*     */ 
/*     */   public long getTimeLastStopActive() {
/* 103 */     return this.timeLastStopActive;
/*     */   }
/*     */ 
/*     */   public void setTimeLastStopActive(long timeLastStopActive) {
/* 107 */     this.timeLastStopActive = timeLastStopActive;
/*     */   }
/*     */ 
/*     */   public String getRequester() {
/* 111 */     return this.requester;
/*     */   }
/*     */ 
/*     */   public void setRequester(String requester) {
/* 115 */     this.requester = requester;
/*     */   }
/*     */ 
/*     */   public String getDelegateDriver() {
/* 119 */     return this.delegateDriver;
/*     */   }
/*     */ 
/*     */   public void setDelegateDriver(String delegateDriver) {
/* 123 */     this.delegateDriver = delegateDriver;
/*     */   }
/*     */ 
/*     */   public String getDelegateUrl() {
/* 127 */     return this.delegateUrl;
/*     */   }
/*     */ 
/*     */   public void setDelegateUrl(String delegateUrl) {
/* 131 */     this.delegateUrl = delegateUrl;
/*     */   }
/*     */ 
/*     */   public String getProxyHashcode() {
/* 135 */     return this.proxyHashcode;
/*     */   }
/*     */ 
/*     */   public void setProxyHashcode(String proxyHashcode) {
/* 139 */     this.proxyHashcode = proxyHashcode;
/*     */   }
/*     */ 
/*     */   public String getDelegateHashcode() {
/* 143 */     return this.delegateHashcode;
/*     */   }
/*     */ 
/*     */   public void setDelegateHashcode(String delegateHashcode) {
/* 147 */     this.delegateHashcode = delegateHashcode;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object o)
/*     */   {
/* 157 */     return new Long(((ConnectionInfoIF)o).getId()).compareTo(new Long(getId()));
/*     */   }
/*     */ 
/*     */   public String[] getSqlCalls() {
/* 161 */     return (String[])(String[])this.sqlCalls.toArray(new String[0]);
/*     */   }
/*     */ 
/*     */   public void addSqlCall(String sqlCall) {
/* 165 */     this.sqlCalls.add(sqlCall);
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.ConnectionInfo
 * JD-Core Version:    0.6.0
 */