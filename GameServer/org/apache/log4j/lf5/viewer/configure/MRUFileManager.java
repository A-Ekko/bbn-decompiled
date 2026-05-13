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
/*  49 */   private int _maxSize = 0;
/*     */   private LinkedList _mruFileList;
/*     */ 
/*     */   public MRUFileManager()
/*     */   {
/*  56 */     load();
/*  57 */     setMaxSize(3);
/*     */   }
/*     */ 
/*     */   public MRUFileManager(int maxSize) {
/*  61 */     load();
/*  62 */     setMaxSize(maxSize);
/*     */   }
/*     */ 
/*     */   public void save()
/*     */   {
/*  72 */     File file = new File(getFilename());
/*     */     try
/*     */     {
/*  75 */       ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
/*     */ 
/*  77 */       oos.writeObject(this._mruFileList);
/*  78 */       oos.flush();
/*  79 */       oos.close();
/*     */     }
/*     */     catch (Exception e) {
/*  82 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  90 */     return this._mruFileList.size();
/*     */   }
/*     */ 
/*     */   public Object getFile(int index)
/*     */   {
/*  98 */     if (index < size()) {
/*  99 */       return this._mruFileList.get(index);
/*     */     }
/*     */ 
/* 102 */     return null;
/*     */   }
/*     */ 
/*     */   public InputStream getInputStream(int index)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 110 */     if (index < size()) {
/* 111 */       Object o = getFile(index);
/* 112 */       if ((o instanceof File)) {
/* 113 */         return getInputStream((File)o);
/*     */       }
/* 115 */       return getInputStream((URL)o);
/*     */     }
/*     */ 
/* 118 */     return null;
/*     */   }
/*     */ 
/*     */   public void set(File file)
/*     */   {
/* 125 */     setMRU(file);
/*     */   }
/*     */ 
/*     */   public void set(URL url)
/*     */   {
/* 132 */     setMRU(url);
/*     */   }
/*     */ 
/*     */   public String[] getMRUFileList()
/*     */   {
/* 139 */     if (size() == 0) {
/* 140 */       return null;
/*     */     }
/*     */ 
/* 143 */     String[] ss = new String[size()];
/*     */ 
/* 145 */     for (int i = 0; i < size(); i++) {
/* 146 */       Object o = getFile(i);
/* 147 */       if ((o instanceof File)) {
/* 148 */         ss[i] = ((File)o).getAbsolutePath();
/*     */       }
/*     */       else {
/* 151 */         ss[i] = o.toString();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 156 */     return ss;
/*     */   }
/*     */ 
/*     */   public void moveToTop(int index)
/*     */   {
/* 165 */     this._mruFileList.add(0, this._mruFileList.remove(index));
/*     */   }
/*     */ 
/*     */   public static void createConfigurationDirectory()
/*     */   {
/* 175 */     String home = System.getProperty("user.home");
/* 176 */     String sep = System.getProperty("file.separator");
/* 177 */     File f = new File(home + sep + "lf5");
/* 178 */     if (!f.exists())
/*     */       try {
/* 180 */         f.mkdir();
/*     */       } catch (SecurityException e) {
/* 182 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(File file)
/*     */     throws IOException, FileNotFoundException
/*     */   {
/* 198 */     BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
/*     */ 
/* 201 */     return reader;
/*     */   }
/*     */ 
/*     */   protected InputStream getInputStream(URL url)
/*     */     throws IOException
/*     */   {
/* 211 */     return url.openStream();
/*     */   }
/*     */ 
/*     */   protected void setMRU(Object o)
/*     */   {
/* 218 */     int index = this._mruFileList.indexOf(o);
/*     */ 
/* 220 */     if (index == -1) {
/* 221 */       this._mruFileList.add(0, o);
/* 222 */       setMaxSize(this._maxSize);
/*     */     } else {
/* 224 */       moveToTop(index);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void load()
/*     */   {
/* 233 */     createConfigurationDirectory();
/* 234 */     File file = new File(getFilename());
/* 235 */     if (file.exists())
/*     */       try {
/* 237 */         ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
/*     */ 
/* 239 */         this._mruFileList = ((LinkedList)ois.readObject());
/* 240 */         ois.close();
/*     */ 
/* 243 */         Iterator it = this._mruFileList.iterator();
/* 244 */         while (it.hasNext()) {
/* 245 */           Object o = it.next();
/* 246 */           if ((!(o instanceof File)) && (!(o instanceof URL)))
/* 247 */             it.remove();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {
/* 251 */         this._mruFileList = new LinkedList();
/*     */       }
/*     */     else
/* 254 */       this._mruFileList = new LinkedList();
/*     */   }
/*     */ 
/*     */   protected String getFilename()
/*     */   {
/* 260 */     String home = System.getProperty("user.home");
/* 261 */     String sep = System.getProperty("file.separator");
/*     */ 
/* 263 */     return home + sep + "lf5" + sep + "mru_file_manager";
/*     */   }
/*     */ 
/*     */   protected void setMaxSize(int maxSize)
/*     */   {
/* 270 */     if (maxSize < this._mruFileList.size()) {
/* 271 */       for (int i = 0; i < this._mruFileList.size() - maxSize; i++) {
/* 272 */         this._mruFileList.removeLast();
/*     */       }
/*     */     }
/*     */ 
/* 276 */     this._maxSize = maxSize;
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.apache.log4j.lf5.viewer.configure.MRUFileManager
 * JD-Core Version:    0.6.0
 */