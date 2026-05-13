/*     */ package org.logicalcobwebs.proxool.admin.servlet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.DateFormat;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.HttpServlet;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.logicalcobwebs.proxool.ConnectionInfoIF;
/*     */ import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
/*     */ import org.logicalcobwebs.proxool.ProxoolException;
/*     */ import org.logicalcobwebs.proxool.ProxoolFacade;
/*     */ import org.logicalcobwebs.proxool.Version;
/*     */ import org.logicalcobwebs.proxool.admin.SnapshotIF;
/*     */ import org.logicalcobwebs.proxool.admin.StatisticsIF;
/*     */ 
/*     */ public class AdminServlet extends HttpServlet
/*     */ {
/*  71 */   private static final Log LOG = LogFactory.getLog(AdminServlet.class);
/*     */ 
/*  82 */   private static final String[] STATUS_CLASSES = { "null", "available", "active", "offline" };
/*     */   public static final String OUTPUT_FULL = "full";
/*     */   public static final String OUTPUT_SIMPLE = "simple";
/*     */   private String output;
/*     */   private String cssFile;
/*     */   private static final String STATISTIC = "statistic";
/*     */   private static final String CORE_PROPERTY = "core-property";
/*     */   private static final String STANDARD_PROPERTY = "standard-property";
/*     */   private static final String DELEGATED_PROPERTY = "delegated-property";
/*     */   private static final String SNAPSHOT = "snapshot";
/* 163 */   private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
/*     */ 
/* 168 */   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*     */ 
/* 170 */   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
/*     */   private static final String DETAIL = "detail";
/*     */   private static final String DETAIL_MORE = "more";
/*     */   private static final String DETAIL_LESS = "less";
/*     */   private static final String TAB = "tab";
/*     */   private static final String TAB_DEFINITION = "definition";
/*     */   private static final String TAB_SNAPSHOT = "snapshot";
/*     */   private static final String TAB_STATISTICS = "statistics";
/*     */   private static final String ALIAS = "alias";
/*     */   private static final String CONNECTION_ID = "id";
/*     */ 
/*     */   public void init(ServletConfig servletConfig)
/*     */     throws ServletException
/*     */   {
/* 137 */     super.init(servletConfig);
/*     */ 
/* 140 */     this.output = servletConfig.getInitParameter("output");
/* 141 */     if (this.output != null) {
/* 142 */       if (this.output.equalsIgnoreCase("full")) {
/* 143 */         this.output = "full";
/* 144 */       } else if (this.output.equalsIgnoreCase("simple")) {
/* 145 */         this.output = "simple";
/*     */       } else {
/* 147 */         LOG.warn("Unrecognised output parameter for " + getClass().getName() + ". Expected: " + "full" + " or " + "simple");
/* 148 */         this.output = null;
/*     */       }
/*     */     }
/* 151 */     if (this.output == null) {
/* 152 */       this.output = "full";
/*     */     }
/*     */ 
/* 155 */     this.cssFile = servletConfig.getInitParameter("cssFile");
/*     */   }
/*     */ 
/*     */   protected void doPost(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 217 */     doGet(request, response);
/*     */   }
/*     */ 
/*     */   protected void doGet(HttpServletRequest request, HttpServletResponse response)
/*     */     throws ServletException, IOException
/*     */   {
/* 225 */     response.setHeader("Pragma", "no-cache");
/* 226 */     String link = request.getRequestURI();
/*     */ 
/* 230 */     String alias = request.getParameter("alias");
/*     */ 
/* 232 */     ConnectionPoolDefinitionIF def = null;
/* 233 */     if (alias != null) {
/*     */       try {
/* 235 */         def = ProxoolFacade.getConnectionPoolDefinition(alias);
/*     */       } catch (ProxoolException e) {
/* 237 */         alias = null;
/*     */       }
/*     */     }
/* 240 */     String[] aliases = ProxoolFacade.getAliases();
/* 241 */     if ((alias == null) && 
/* 242 */       (aliases.length > 0)) {
/* 243 */       alias = aliases[0];
/*     */     }
/*     */ 
/* 246 */     if ((def == null) && (alias != null)) {
/*     */       try {
/* 248 */         def = ProxoolFacade.getConnectionPoolDefinition(alias);
/*     */       } catch (ProxoolException e) {
/* 250 */         throw new ServletException("Couldn't find pool with alias " + alias);
/*     */       }
/*     */     }
/*     */ 
/* 254 */     String tab = request.getParameter("tab");
/* 255 */     if (tab == null) {
/* 256 */       tab = "definition";
/*     */     }
/*     */ 
/* 260 */     String snapshotDetail = request.getParameter("detail");
/*     */ 
/* 263 */     String snapshotConnectionId = request.getParameter("id");
/*     */     try
/*     */     {
/* 266 */       if (this.output.equals("full")) {
/* 267 */         response.setContentType("text/html");
/* 268 */         openHtml(response.getOutputStream());
/*     */       }
/* 270 */       response.getOutputStream().println("<div class=\"version\">Proxool " + Version.getVersion() + "</div>");
/* 271 */       doList(response.getOutputStream(), alias, tab, link);
/*     */ 
/* 273 */       if ((aliases != null) && (aliases.length > 0)) {
/* 274 */         StatisticsIF[] statisticsArray = ProxoolFacade.getStatistics(alias);
/* 275 */         boolean statisticsAvailable = (statisticsArray != null) && (statisticsArray.length > 0);
/* 276 */         boolean statisticsComingSoon = def.getStatistics() != null;
/*     */ 
/* 278 */         if ((!statisticsComingSoon) && (tab.equals("statistics"))) {
/* 279 */           tab = "definition";
/*     */         }
/* 281 */         doTabs(response.getOutputStream(), alias, link, tab, statisticsAvailable, statisticsComingSoon);
/* 282 */         if (tab.equals("definition"))
/* 283 */           doDefinition(response.getOutputStream(), def);
/* 284 */         else if (tab.equals("snapshot"))
/* 285 */           doSnapshot(response.getOutputStream(), def, link, snapshotDetail, snapshotConnectionId);
/* 286 */         else if (tab.equals("statistics"))
/* 287 */           doStatistics(response.getOutputStream(), statisticsArray, def);
/*     */         else
/* 289 */           throw new ServletException("Unrecognised tab '" + tab + "'");
/*     */       }
/*     */     }
/*     */     catch (ProxoolException e) {
/* 293 */       throw new ServletException("Problem serving Proxool Admin", e);
/*     */     }
/*     */ 
/* 296 */     if (this.output.equals("full"))
/* 297 */       closeHtml(response.getOutputStream());
/*     */   }
/*     */ 
/*     */   private void doTabs(ServletOutputStream out, String alias, String link, String tab, boolean statisticsAvailable, boolean statisticsComingSoon)
/*     */     throws IOException
/*     */   {
/* 312 */     out.println("<ul>");
/* 313 */     out.println("<li class=\"" + (tab.equals("definition") ? "active" : "inactive") + "\"><a class=\"quiet\" href=\"" + link + "?alias=" + alias + "&tab=" + "definition" + "\">Definition</a></li>");
/* 314 */     out.println("<li class=\"" + (tab.equals("snapshot") ? "active" : "inactive") + "\"><a class=\"quiet\" href=\"" + link + "?alias=" + alias + "&tab=" + "snapshot" + "\">Snapshot</a></li>");
/* 315 */     if (statisticsAvailable)
/* 316 */       out.println("<li class=\"" + (tab.equals("statistics") ? "active" : "inactive") + "\"><a class=\"quiet\" href=\"" + link + "?alias=" + alias + "&tab=" + "statistics" + "\">Statistics</a></li>");
/* 317 */     else if (statisticsComingSoon) {
/* 318 */       out.println("<li class=\"disabled\">Statistics</li>");
/*     */     }
/* 320 */     out.println("</ul>");
/*     */   }
/*     */ 
/*     */   private void doStatistics(ServletOutputStream out, StatisticsIF[] statisticsArray, ConnectionPoolDefinitionIF cpd)
/*     */     throws IOException
/*     */   {
/* 331 */     for (int i = 0; i < statisticsArray.length; i++) {
/* 332 */       StatisticsIF statistics = statisticsArray[i];
/*     */ 
/* 334 */       openDataTable(out);
/*     */ 
/* 336 */       printDefinitionEntry(out, "alias", cpd.getAlias(), "core-property");
/*     */ 
/* 339 */       printDefinitionEntry(out, "Period", TIME_FORMAT.format(statistics.getStartDate()) + " to " + TIME_FORMAT.format(statistics.getStopDate()), "statistic");
/*     */ 
/* 342 */       printDefinitionEntry(out, "Served", statistics.getServedCount() + " (" + DECIMAL_FORMAT.format(statistics.getServedPerSecond()) + "/s)", "statistic");
/*     */ 
/* 345 */       printDefinitionEntry(out, "Refused", statistics.getRefusedCount() + " (" + DECIMAL_FORMAT.format(statistics.getRefusedPerSecond()) + "/s)", "statistic");
/*     */ 
/* 348 */       printDefinitionEntry(out, "Average active time", DECIMAL_FORMAT.format(statistics.getAverageActiveTime() / 1000.0D) + "s", "statistic");
/*     */ 
/* 351 */       StringBuffer activityLevelBuffer = new StringBuffer();
/* 352 */       int activityLevel = (int)(100.0D * statistics.getAverageActiveCount() / cpd.getMaximumConnectionCount());
/* 353 */       activityLevelBuffer.append(activityLevel);
/* 354 */       activityLevelBuffer.append("%<br/>");
/* 355 */       String[] colours = { "0000ff", "eeeeee" };
/* 356 */       int[] lengths = { activityLevel, 100 - activityLevel };
/* 357 */       drawBarChart(activityLevelBuffer, colours, lengths);
/* 358 */       printDefinitionEntry(out, "Activity level", activityLevelBuffer.toString(), "statistic");
/*     */ 
/* 360 */       closeTable(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawBarChart(StringBuffer out, String[] colours, int[] lengths)
/*     */   {
/* 372 */     out.append("<table style=\"margin: 8px; font-size: 50%;\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr>");
/*     */ 
/* 375 */     int totalLength = 0;
/* 376 */     for (int i = 0; i < colours.length; i++) {
/* 377 */       totalLength += lengths[i];
/*     */     }
/*     */ 
/* 381 */     for (int j = 0; j < colours.length; j++) {
/* 382 */       String colour = colours[j];
/* 383 */       int length = lengths[j];
/* 384 */       if (length > 0) {
/* 385 */         out.append("<td style=\"background-color: #");
/* 386 */         out.append(colour);
/* 387 */         out.append("\" width=\"");
/* 388 */         out.append(100 * length / totalLength);
/* 389 */         out.append("%\">&nbsp;</td>");
/*     */       }
/*     */     }
/* 392 */     out.append("</tr></table>");
/*     */   }
/*     */ 
/*     */   private void doDefinition(ServletOutputStream out, ConnectionPoolDefinitionIF cpd)
/*     */     throws IOException
/*     */   {
/* 401 */     openDataTable(out);
/*     */ 
/* 406 */     printDefinitionEntry(out, "alias", cpd.getAlias(), "core-property");
/* 407 */     printDefinitionEntry(out, "driver-url", cpd.getUrl(), "core-property");
/* 408 */     printDefinitionEntry(out, "driver-class", cpd.getDriver(), "core-property");
/* 409 */     printDefinitionEntry(out, "minimum-connection-count", String.valueOf(cpd.getMinimumConnectionCount()), "standard-property");
/* 410 */     printDefinitionEntry(out, "maximum-connection-count", String.valueOf(cpd.getMaximumConnectionCount()), "standard-property");
/* 411 */     printDefinitionEntry(out, "prototype-count", cpd.getPrototypeCount() > 0 ? String.valueOf(cpd.getPrototypeCount()) : null, "standard-property");
/* 412 */     printDefinitionEntry(out, "simultaneous-build-throttle", String.valueOf(cpd.getSimultaneousBuildThrottle()), "standard-property");
/* 413 */     printDefinitionEntry(out, "maximum-connection-lifetime", formatMilliseconds(cpd.getMaximumConnectionLifetime()), "standard-property");
/* 414 */     printDefinitionEntry(out, "maximum-active-time", formatMilliseconds(cpd.getMaximumActiveTime()), "standard-property");
/* 415 */     printDefinitionEntry(out, "house-keeping-sleep-time", cpd.getHouseKeepingSleepTime() / 1000L + "s", "standard-property");
/* 416 */     printDefinitionEntry(out, "house-keeping-test-sql", cpd.getHouseKeepingTestSql(), "standard-property");
/* 417 */     printDefinitionEntry(out, "test-before-use", String.valueOf(cpd.isTestBeforeUse()), "standard-property");
/* 418 */     printDefinitionEntry(out, "test-after-use", String.valueOf(cpd.isTestAfterUse()), "standard-property");
/* 419 */     printDefinitionEntry(out, "recently-started-threshold", formatMilliseconds(cpd.getRecentlyStartedThreshold()), "standard-property");
/* 420 */     printDefinitionEntry(out, "overload-without-refusal-lifetime", formatMilliseconds(cpd.getOverloadWithoutRefusalLifetime()), "standard-property");
/* 421 */     printDefinitionEntry(out, "injectable-connection-interface", String.valueOf(cpd.getInjectableConnectionInterface()), "standard-property");
/* 422 */     printDefinitionEntry(out, "injectable-statement-interface", String.valueOf(cpd.getInjectableStatementInterface()), "standard-property");
/* 423 */     printDefinitionEntry(out, "injectable-callable-statement-interface", String.valueOf(cpd.getInjectableCallableStatementInterface()), "standard-property");
/* 424 */     printDefinitionEntry(out, "injectable-prepared-statement-interface", String.valueOf(cpd.getInjectablePreparedStatementInterface()), "standard-property");
/*     */ 
/* 427 */     String fatalSqlExceptions = null;
/* 428 */     if ((cpd.getFatalSqlExceptions() != null) && (cpd.getFatalSqlExceptions().size() > 0)) {
/* 429 */       StringBuffer fatalSqlExceptionsBuffer = new StringBuffer();
/* 430 */       Iterator i = cpd.getFatalSqlExceptions().iterator();
/* 431 */       while (i.hasNext()) {
/* 432 */         String s = (String)i.next();
/* 433 */         fatalSqlExceptionsBuffer.append(s);
/* 434 */         fatalSqlExceptionsBuffer.append(i.hasNext() ? ", " : "");
/*     */       }
/* 436 */       fatalSqlExceptions = fatalSqlExceptionsBuffer.toString();
/*     */     }
/* 438 */     printDefinitionEntry(out, "fatal-sql-exception", fatalSqlExceptions, "standard-property");
/* 439 */     printDefinitionEntry(out, "fatal-sql-exception-wrapper-class", cpd.getFatalSqlExceptionWrapper(), "standard-property");
/* 440 */     printDefinitionEntry(out, "statistics", cpd.getStatistics(), "standard-property");
/* 441 */     printDefinitionEntry(out, "statistics-log-level", cpd.getStatisticsLogLevel(), "standard-property");
/* 442 */     printDefinitionEntry(out, "verbose", String.valueOf(cpd.isVerbose()), "standard-property");
/* 443 */     printDefinitionEntry(out, "trace", String.valueOf(cpd.isTrace()), "standard-property");
/*     */ 
/* 445 */     Properties p = cpd.getDelegateProperties();
/* 446 */     Iterator i = p.keySet().iterator();
/* 447 */     while (i.hasNext()) {
/* 448 */       String name = (String)i.next();
/* 449 */       String value = p.getProperty(name);
/*     */ 
/* 451 */       if ((name.toLowerCase().indexOf("password") > -1) || (name.toLowerCase().indexOf("passwd") > -1)) {
/* 452 */         value = "******";
/*     */       }
/* 454 */       printDefinitionEntry(out, name + " (delegated)", value, "delegated-property");
/*     */     }
/*     */ 
/* 457 */     closeTable(out);
/*     */   }
/*     */ 
/*     */   private void doSnapshot(ServletOutputStream out, ConnectionPoolDefinitionIF cpd, String link, String level, String connectionId)
/*     */     throws IOException, ProxoolException
/*     */   {
/* 470 */     boolean detail = (level != null) && (level.equals("more"));
/* 471 */     SnapshotIF snapshot = ProxoolFacade.getSnapshot(cpd.getAlias(), detail);
/*     */ 
/* 473 */     if (snapshot != null)
/*     */     {
/* 475 */       openDataTable(out);
/*     */ 
/* 477 */       printDefinitionEntry(out, "alias", cpd.getAlias(), "core-property");
/*     */ 
/* 480 */       printDefinitionEntry(out, "Start date", DATE_FORMAT.format(snapshot.getDateStarted()), "snapshot");
/*     */ 
/* 483 */       printDefinitionEntry(out, "Snapshot", TIME_FORMAT.format(snapshot.getSnapshotDate()), "snapshot");
/*     */ 
/* 486 */       StringBuffer connectionsBuffer = new StringBuffer();
/* 487 */       connectionsBuffer.append(snapshot.getActiveConnectionCount());
/* 488 */       connectionsBuffer.append(" (active), ");
/* 489 */       connectionsBuffer.append(snapshot.getAvailableConnectionCount());
/* 490 */       connectionsBuffer.append(" (available), ");
/* 491 */       if (snapshot.getOfflineConnectionCount() > 0) {
/* 492 */         connectionsBuffer.append(snapshot.getOfflineConnectionCount());
/* 493 */         connectionsBuffer.append(" (offline), ");
/*     */       }
/* 495 */       connectionsBuffer.append(snapshot.getMaximumConnectionCount());
/* 496 */       connectionsBuffer.append(" (max)<br/>");
/* 497 */       String[] colours = { "ff9999", "66cc66", "cccccc" };
/* 498 */       int[] lengths = { snapshot.getActiveConnectionCount(), snapshot.getAvailableConnectionCount(), snapshot.getMaximumConnectionCount() - snapshot.getActiveConnectionCount() - snapshot.getAvailableConnectionCount() };
/*     */ 
/* 500 */       drawBarChart(connectionsBuffer, colours, lengths);
/* 501 */       printDefinitionEntry(out, "Connections", connectionsBuffer.toString(), "snapshot");
/*     */ 
/* 504 */       printDefinitionEntry(out, "Served", String.valueOf(snapshot.getServedCount()), "snapshot");
/*     */ 
/* 507 */       printDefinitionEntry(out, "Refused", String.valueOf(snapshot.getRefusedCount()), "snapshot");
/*     */ 
/* 509 */       if (!detail) {
/* 510 */         out.println("    <tr>");
/* 511 */         out.print("        <td colspan=\"2\" align=\"right\"><form action=\"" + link + "\" method=\"GET\">");
/* 512 */         out.print("<input type=\"hidden\" name=\"alias\" value=\"" + cpd.getAlias() + "\">");
/* 513 */         out.print("<input type=\"hidden\" name=\"tab\" value=\"snapshot\">");
/* 514 */         out.print("<input type=\"hidden\" name=\"detail\" value=\"more\">");
/* 515 */         out.print("<input type=\"submit\" value=\"More information&gt;\">");
/* 516 */         out.println("</form></td>");
/* 517 */         out.println("    </tr>");
/*     */       }
/*     */       else {
/* 520 */         out.println("    <tr>");
/* 521 */         out.print("      <th width=\"200\" valign=\"top\">");
/* 522 */         out.print("Details:<br>(click ID to drill down)");
/* 523 */         out.println("</th>");
/* 524 */         out.print("      <td>");
/*     */ 
/* 526 */         doSnapshotDetails(out, cpd, snapshot, link, connectionId);
/*     */ 
/* 528 */         out.println("</td>");
/* 529 */         out.println("    </tr>");
/*     */ 
/* 532 */         if (connectionId != null) {
/* 533 */           long drillDownConnectionId = Long.valueOf(connectionId).longValue();
/* 534 */           ConnectionInfoIF drillDownConnection = snapshot.getConnectionInfo(drillDownConnectionId);
/* 535 */           if (drillDownConnection != null) {
/* 536 */             out.println("    <tr>");
/* 537 */             out.print("      <th valign=\"top\">");
/* 538 */             out.print("Connection #" + connectionId);
/* 539 */             out.println("</td>");
/* 540 */             out.print("      <td>");
/*     */ 
/* 542 */             doDrillDownConnection(out, drillDownConnection);
/*     */ 
/* 544 */             out.println("</td>");
/* 545 */             out.println("    </tr>");
/*     */           }
/*     */         }
/*     */ 
/* 549 */         out.println("    <tr>");
/* 550 */         out.print("        <td colspan=\"2\" align=\"right\"><form action=\"" + link + "\" method=\"GET\">");
/* 551 */         out.print("<input type=\"hidden\" name=\"alias\" value=\"" + cpd.getAlias() + "\">");
/* 552 */         out.print("<input type=\"hidden\" name=\"tab\" value=\"snapshot\">");
/* 553 */         out.print("<input type=\"hidden\" name=\"detail\" value=\"less\">");
/* 554 */         out.print("<input type=\"submit\" value=\"&lt; Less information\">");
/* 555 */         out.println("</form></td>");
/* 556 */         out.println("    </tr>");
/*     */       }
/*     */ 
/* 559 */       closeTable(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doSnapshotDetails(ServletOutputStream out, ConnectionPoolDefinitionIF cpd, SnapshotIF snapshot, String link, String connectionId)
/*     */     throws IOException
/*     */   {
/* 576 */     long drillDownConnectionId = 0L;
/* 577 */     if (connectionId != null) {
/* 578 */       drillDownConnectionId = Long.valueOf(connectionId).longValue();
/*     */     }
/*     */ 
/* 581 */     if ((snapshot.getConnectionInfos() != null) && (snapshot.getConnectionInfos().length > 0)) {
/* 582 */       out.println("<table cellpadding=\"2\" cellspacing=\"0\" border=\"0\">");
/* 583 */       out.println("  <tbody>");
/*     */ 
/* 585 */       out.print("<tr>");
/* 586 */       out.print("<td>#</td>");
/* 587 */       out.print("<td align=\"center\">born</td>");
/* 588 */       out.print("<td align=\"center\">last<br>start</td>");
/* 589 */       out.print("<td align=\"center\">lap<br>(ms)</td>");
/* 590 */       out.print("<td>&nbsp;thread</td>");
/* 591 */       out.print("</tr>");
/*     */ 
/* 593 */       ConnectionInfoIF[] connectionInfos = snapshot.getConnectionInfos();
/* 594 */       for (int i = 0; i < connectionInfos.length; i++) {
/* 595 */         ConnectionInfoIF connectionInfo = connectionInfos[i];
/*     */ 
/* 597 */         if (connectionInfo.getStatus() == 0)
/*     */           continue;
/* 599 */         out.print("<tr>");
/*     */ 
/* 602 */         out.print("<td style=\"background-color: #");
/* 603 */         if (connectionInfo.getStatus() == 2)
/* 604 */           out.print("ffcccc");
/* 605 */         else if (connectionInfo.getStatus() == 1)
/* 606 */           out.print("ccffcc");
/* 607 */         else if (connectionInfo.getStatus() == 3) {
/* 608 */           out.print("ccccff");
/*     */         }
/* 610 */         out.print("\" style=\"");
/*     */ 
/* 612 */         if (drillDownConnectionId == connectionInfo.getId()) {
/* 613 */           out.print("border: 1px solid black;");
/* 614 */           out.print("\">");
/* 615 */           out.print(connectionInfo.getId());
/*     */         } else {
/* 617 */           out.print("border: 1px solid transparent;");
/* 618 */           out.print("\"><a href=\"");
/* 619 */           out.print(link);
/* 620 */           out.print("?");
/* 621 */           out.print("alias");
/* 622 */           out.print("=");
/* 623 */           out.print(cpd.getAlias());
/* 624 */           out.print("&");
/* 625 */           out.print("tab");
/* 626 */           out.print("=");
/* 627 */           out.print("snapshot");
/* 628 */           out.print("&");
/* 629 */           out.print("detail");
/* 630 */           out.print("=");
/* 631 */           out.print("more");
/* 632 */           out.print("&");
/* 633 */           out.print("id");
/* 634 */           out.print("=");
/* 635 */           out.print(connectionInfo.getId());
/* 636 */           out.print("\">");
/* 637 */           out.print(connectionInfo.getId());
/* 638 */           out.print("</a>");
/*     */         }
/* 640 */         out.print("</td>");
/*     */ 
/* 643 */         out.print("<td>&nbsp;");
/* 644 */         out.print(TIME_FORMAT.format(connectionInfo.getBirthDate()));
/* 645 */         out.print("</td>");
/*     */ 
/* 648 */         out.print("<td>&nbsp;");
/* 649 */         out.print(connectionInfo.getTimeLastStartActive() > 0L ? TIME_FORMAT.format(new Date(connectionInfo.getTimeLastStartActive())) : "-");
/* 650 */         out.print("</td>");
/*     */ 
/* 653 */         out.print("<td align=\"right\" class=\"");
/* 654 */         out.print(getStatusClass(connectionInfo));
/* 655 */         out.print("\">");
/* 656 */         String active = "&nbsp;";
/* 657 */         if (connectionInfo.getTimeLastStopActive() > 0L)
/* 658 */           active = String.valueOf((int)(connectionInfo.getTimeLastStopActive() - connectionInfo.getTimeLastStartActive()));
/* 659 */         else if (connectionInfo.getTimeLastStartActive() > 0L) {
/* 660 */           active = String.valueOf((int)(snapshot.getSnapshotDate().getTime() - connectionInfo.getTimeLastStartActive()));
/*     */         }
/* 662 */         out.print(active);
/* 663 */         out.print("&nbsp;&nbsp;</td>");
/*     */ 
/* 666 */         out.print("<td>&nbsp;");
/* 667 */         out.print(connectionInfo.getRequester() != null ? connectionInfo.getRequester() : "-");
/* 668 */         out.print("</td>");
/*     */ 
/* 670 */         out.println("</tr>");
/*     */       }
/*     */ 
/* 673 */       out.println("  </tbody>");
/* 674 */       out.println("</table>");
/*     */     }
/*     */     else {
/* 677 */       out.println("No connections yet");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String getStatusClass(ConnectionInfoIF info)
/*     */   {
/*     */     try
/*     */     {
/* 689 */       return STATUS_CLASSES[info.getStatus()];
/*     */     } catch (ArrayIndexOutOfBoundsException e) {
/* 691 */       LOG.warn("Unknown status: " + info.getStatus());
/* 692 */     }return "unknown-" + info.getStatus();
/*     */   }
/*     */ 
/*     */   private void doDrillDownConnection(ServletOutputStream out, ConnectionInfoIF drillDownConnection)
/*     */     throws IOException
/*     */   {
/* 699 */     String[] sqlCalls = drillDownConnection.getSqlCalls();
/* 700 */     for (int i = 0; (sqlCalls != null) && (i < sqlCalls.length); i++) {
/* 701 */       String sqlCall = sqlCalls[i];
/* 702 */       out.print("<div class=\"drill-down\">");
/* 703 */       out.print("sql = ");
/* 704 */       out.print(sqlCall);
/* 705 */       out.print("</div>");
/*     */     }
/*     */ 
/* 709 */     out.print("<div class=\"drill-down\">");
/* 710 */     out.print("proxy = ");
/* 711 */     out.print(drillDownConnection.getProxyHashcode());
/* 712 */     out.print("</div>");
/*     */ 
/* 715 */     out.print("<div class=\"drill-down\">");
/* 716 */     out.print("delegate = ");
/* 717 */     out.print(drillDownConnection.getDelegateHashcode());
/* 718 */     out.print("</div>");
/*     */ 
/* 721 */     out.print("<div class=\"drill-down\">");
/* 722 */     out.print("url = ");
/* 723 */     out.print(drillDownConnection.getDelegateUrl());
/* 724 */     out.print("</div>");
/*     */   }
/*     */ 
/*     */   private void openHtml(ServletOutputStream out) throws IOException
/*     */   {
/* 729 */     out.println("<html><header><title>Proxool Admin</title>");
/* 730 */     out.println("<style media=\"screen\">");
/* 731 */     out.println("body {background-color: #93bde6;}\ndiv.version {font-weight: bold; font-size: 100%; margin-bottom: 8px;}\nh1 {font-weight: bold; font-size: 100%}\noption {padding: 2px 24px 2px 4px;}\ninput {margin: 0px 0px 4px 12px;}\ntable.data {font-size: 90%; border-collapse: collapse; border: 1px solid black;}\ntable.data th {background: #bddeff; width: 25em; text-align: left; padding-right: 8px; font-weight: normal; border: 1px solid black;}\ntable.data td {background: #ffffff; vertical-align: top; padding: 0px 2px 0px 2px; border: 1px solid black;}\ntd.null {background: yellow;}\ntd.available {color: black;}\ntd.active {color: red;}\ntd.offline {color: blue;}\ndiv.drill-down {}\nul {list-style: none; padding: 0px; margin: 0px; position: relative; font-size: 90%;}\nli {padding: 0px; margin: 0px 4px 0px 0px; display: inline; border: 1px solid black; border-width: 1px 1px 0px 1px;}\nli.active {background: #bddeff;}\nli.inactive {background: #eeeeee;}\nli.disabled {background: #dddddd; color: #999999; padding: 0px 4px 0px 4px;}\na.quiet {color: black; text-decoration: none; padding: 0px 4px 0px 4px; }\na.quiet:hover {background: white;}\n");
/*     */ 
/* 751 */     out.println("</style>");
/*     */ 
/* 753 */     if (this.cssFile != null) {
/* 754 */       out.println("<link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"" + this.cssFile + "\"></script>");
/*     */     }
/* 756 */     out.println("</header><body>");
/*     */   }
/*     */ 
/*     */   private void closeHtml(ServletOutputStream out) throws IOException {
/* 760 */     out.println("</body></html>");
/*     */   }
/*     */ 
/*     */   private void openDataTable(ServletOutputStream out) throws IOException {
/* 764 */     out.println("<table cellpadding=\"2\" cellspacing=\"0\" border=\"1\" class=\"data\">");
/* 765 */     out.println("  <tbody>");
/*     */   }
/*     */ 
/*     */   private void closeTable(ServletOutputStream out) throws IOException {
/* 769 */     out.println("  </tbody>");
/* 770 */     out.println("</table>");
/* 771 */     out.println("<br/>");
/*     */   }
/*     */ 
/*     */   private void printDefinitionEntry(ServletOutputStream out, String name, String value, String type) throws IOException {
/* 775 */     out.println("    <tr>");
/* 776 */     out.print("      <th valign=\"top\">");
/* 777 */     out.print(name);
/* 778 */     out.println(":</th>");
/* 779 */     out.print("      <td class=\"" + type + "\"nowrap>");
/* 780 */     if ((value != null) && (!value.equals("null")))
/* 781 */       out.print(value);
/*     */     else {
/* 783 */       out.print("-");
/*     */     }
/* 785 */     out.print("</td>");
/* 786 */     out.println("    </tr>");
/*     */   }
/*     */ 
/*     */   private void doList(ServletOutputStream out, String alias, String tab, String link)
/*     */     throws IOException
/*     */   {
/* 799 */     String[] aliases = ProxoolFacade.getAliases();
/*     */ 
/* 801 */     if (aliases.length == 0) {
/* 802 */       out.println("<p>No pools have been registered.</p>");
/* 803 */     } else if (aliases.length != 1)
/*     */     {
/* 806 */       out.println("<form action=\"" + link + "\" method=\"GET\" name=\"alias\">");
/* 807 */       out.println("<select name=\"alias\" size=\"" + Math.min(aliases.length, 5) + "\">");
/* 808 */       for (int i = 0; i < aliases.length; i++) {
/* 809 */         out.print("  <option value=\"");
/* 810 */         out.print(aliases[i]);
/* 811 */         out.print("\"");
/* 812 */         out.print(aliases[i].equals(alias) ? " selected" : "");
/* 813 */         out.print(">");
/* 814 */         out.print(aliases[i]);
/* 815 */         out.println("</option>");
/*     */       }
/* 817 */       out.println("</select>");
/* 818 */       out.println("<input name=\"tab\" value=\"" + tab + "\" type=\"hidden\">");
/* 819 */       out.println("<input value=\"Show\" type=\"submit\">");
/* 820 */       out.println("</form>");
/*     */     }
/*     */   }
/*     */ 
/*     */   private String formatMilliseconds(long time)
/*     */   {
/* 832 */     if (time > 2147483647L) {
/* 833 */       return time + "ms";
/*     */     }
/* 835 */     Calendar c = Calendar.getInstance();
/* 836 */     c.clear();
/* 837 */     c.add(14, (int)time);
/* 838 */     return TIME_FORMAT.format(c.getTime());
/*     */   }
/*     */ }

/* Location:           C:\Users\Kevin\Desktop\MMO Servers\B\GameServer\game.jar
 * Qualified Name:     org.logicalcobwebs.proxool.admin.servlet.AdminServlet
 * JD-Core Version:    0.6.0
 */