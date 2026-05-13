/*     */ package org.apache.log4j.lf5.viewer.configure;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.URL;
/*     */ import java.util.AbstractSequentialList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ 
/*     */ public class MRUFileManager
/*     */ {
/*     */   private static final String CONFIG_FILE_NAME = "mru_file_manager";
/*     */   private static final int DEFAULT_MAX_SIZE = 3;
/*  40 */   private int _maxSize = 0;
/*     */   private LinkedList _mruFileList;
/*     */ 
/*     */   public MRUFileManager()
/*     */   {
/*  47 */     load();
/*  48 */     setMaxSize(3);
/*     */   }
/*     */ 
/*     */   public MRUFileManager(int maxSize) {
/*  52 */     load();
/*  53 */     setMaxSize(maxSize);
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*  63 */     File file = new File(getFilename());
/*     */     try
/*     */     {
/*  66 */       ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
/*     */ 
/*  68 */       oos.writeObject(this._mruFileList);
/*  69 */       oos.flush();
/*  70 */       oos.close();
/*     */     }
/*     */     catch (Exception e) {
/*  73 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  81 */     return this._mruFileList.size();
/*     */   }
/*     */ 
/*     */   public Object getFile(int index)
/*     */   {
/*  89 */     if (index < size()) {
/*  90 */       return this._mruFileList.get(index);
/*     */     }
/*     */ 
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream(int index)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 101 */     if (index < size()) {
/* 102 */       Object o = getFile(index);
/* 103 */       if ((o instanceof File)) {
/* 104 */         return getInputStream((File)o);
/*     */       }
/* 106 */       return getInputStream((URL)o);
/*     */     }
/*     */ 
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */   public void set(File file)
/*     */   {
/* 116 */     setMRU(file);
/*     */   }
/*     */ 
/*     */   public void set(URL url)
/*     */   {
/* 123 */     setMRU(url);
/*     */   }
/*     */ 
/*     */   public String[] getMRUFileList()
/*     */   {
/* 130 */     if (size() == 0) {
/* 131 */       return null;
/*     */     }
/*     */ 
/* 134 */     String[] ss = new String[size()];
/*     */ 
/* 136 */     for (int i = 0; i < size(); i++) {
/* 137 */       Object o = getFile(i);
/* 138 */       if ((o instanceof File)) {
/* 139 */         ss[i] = ((File)o).getAbsolutePath();
/*     */       }
/*     */       else {
/* 142 */         ss[i] = o.toString();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 147 */     return ss;
/*     */   }
/*     */ 
/*     */   public void moveToTop(int index)
/*     */   {
/* 156 */     this._mruFileList.add(0, this._mruFileList.remove(index));
/*     */   }
/*     */ 
/*     */   public static void createConfigurationDirectory()
/*     */   {
/* 166 */     String home = System.getProperty("user.home");
/* 167 */     String sep = System.getProperty("file.separator");
/* 168 */     File f = new File(home + sep + "lf5");
/* 169 */     if (!f.exists())
/*     */       try {
/* 171 */         f.mkdir();
/*     */       } catch (SecurityException e) {
/* 173 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 189 */     BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
/*     */ 
/* 192 */     return reader;
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(URL url)
/*     */     throws IOException
/*     */   {
/* 202 */     return url.openStream();
/*     */   }
/*     */ 
/*     */   protected void setMRU(Object o)
/*     */   {
/* 209 */     int index = this._mruFileList.indexOf(o);
/*     */ 
/* 211 */     if (index == -1) {
/* 212 */       this._mruFileList.add(0, o);
/* 213 */       setMaxSize(this._maxSize);
/*     */     } else {
/* 215 */       moveToTop(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 224 */     createConfigurationDirectory();
/* 225 */     File file = new File(getFilename());
/* 226 */     if (file.exists())
/*     */       try {
/* 228 */         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
/*     */ 
/* 230 */         this._mruFileList = ((LinkedList)ois.readObject());
/* 231 */         ois.close();
/*     */ 
/* 234 */         Iterator it = this._mruFileList.iterator();
/* 235 */         while (it.hasNext()) {
/* 236 */           Object o = it.next();
/* 237 */           if ((!(o instanceof File)) && (!(o instanceof URL)))
/* 238 */             it.remove();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 242 */         this._mruFileList = new LinkedList();
/*     */       }
/*     */     else
/* 245 */       this._mruFileList = new LinkedList();
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 251 */     String home = System.getProperty("user.home");
/* 252 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 254 */     return home + sep + "lf5" + sep + "mru_file_manager";
/*     */   }
/*     */ 
/*     */   protected void setMaxSize(int maxSize)
/*     */   {
/* 261 */     if (maxSize < this._mruFileList.size()) {
/* 262 */       for (int i = 0; i < this._mruFileList.size() - maxSize; i++) {
/* 263 */         this._mruFileList.removeLast();
/*     */       }
/*     */     }
/*     */ 
/* 267 */     this._maxSize = maxSize;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\DataServer\dataServer.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.configure.MRUFileManager
 * JD-Core Version:    0.6.0
 */