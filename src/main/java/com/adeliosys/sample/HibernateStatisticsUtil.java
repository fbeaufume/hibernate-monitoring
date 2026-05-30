package com.adeliosys.sample;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.function.Function;

public class HibernateStatisticsUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSZ").withZone(ZoneId.systemDefault());

    public static void resetStats(EntityManagerFactory entityManagerFactory) {
        entityManagerFactory.unwrap(SessionFactory.class).getStatistics().clear();
    }

    /**
     * Generate a custom HTML stats report for the Hibernate session factory from a given entity manager factory.
     */
    public static String generateStatsReport(EntityManagerFactory entityManagerFactory) {
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();

        StringBuilder buffer = new StringBuilder(32768);

        // Add the various parts of the stats file
        writeHeader(buffer);
        writeGeneralStats(stats, buffer);
        writeEntityStats(stats, buffer);
        writeCollectionStats(stats, buffer);
        writeCacheStats(stats, "Cache Statistics", stats::getDomainDataRegionStatistics, buffer);
        writeQueryStats(stats, buffer);

        return buffer.toString();
    }

    private static void writeHeader(StringBuilder buffer) {
        buffer.append("<html><head>\n");
        buffer.append("<meta charset=\"UTF-8\">\n");
        buffer.append("<style type=\"text/css\">\n");
        buffer.append("body, table { font: normal 14px Verdana, Arial, sans-serif; }\n");
        buffer.append("table { border-collapse: collapse; }\n");
        buffer.append("tr:nth-of-type(odd) { background-color:#EEEEEE; }\n");
        buffer.append("td, th { border: 1px solid lightgrey; }\n");
        buffer.append("</style>\n");
        buffer.append("<title>Hibernate Statistics</title></head><body><center><b>Hibernate Statistics</b><br>\n");
    }

    private static void writeGeneralStats(Statistics stats, StringBuilder buffer) {
        buffer.append("<br>\n");
        buffer.append("<table>\n");
        buffer.append("<tr><th colspan=2>General Statistics</th></tr>");
        buffer.append("<tr><td><i>Name</i></td><td><i>Value</i></td></tr>\n");
        writeObjectStat("Statistics Enabled", stats.isStatisticsEnabled(), buffer);
        writeObjectStat("Start Time", getFormattedTimestamp(stats.getStart()), buffer);
        writeObjectStat("Sessions",
                "Opened=" + stats.getSessionOpenCount() +
                        ", Closed=" + stats.getSessionCloseCount(), buffer);
        writeObjectStat("Transactions",
                "Count=" + stats.getTransactionCount() +
                        ", Successful=" + stats.getSuccessfulTransactionCount() +
                        ", Optimistic Failures=" + stats.getOptimisticFailureCount(), buffer);
        writeObjectStat("Flushes", stats.getFlushCount(), buffer);
        writeObjectStat("Connections Obtained", stats.getConnectCount(), buffer);
        writeObjectStat("Statements",
                "Prepared=" + stats.getPrepareStatementCount() +
                        ", Closed=" + stats.getCloseStatementCount(), buffer);
        writeObjectStat("L2 Cache",
                "Hits=" + stats.getSecondLevelCacheHitCount() +
                        ", Misses=" + stats.getSecondLevelCacheMissCount() +
                        ", Puts=" + stats.getSecondLevelCachePutCount(), buffer);
        writeObjectStat("Entities",
                "Fetches=" + stats.getEntityFetchCount() +
                        ", Loads=" + stats.getEntityLoadCount() +
                        ", Inserts=" + stats.getEntityInsertCount() +
                        ", Updates=" + stats.getEntityUpdateCount() +
                        ", Deletes=" + stats.getEntityDeleteCount(), buffer);
        writeObjectStat("Collections",
                "Fetches=" + stats.getCollectionFetchCount() +
                        ", Loads=" + stats.getCollectionLoadCount() +
                        ", Updates=" + stats.getCollectionUpdateCount() +
                        ", Recreates=" + stats.getCollectionRecreateCount() +
                        ", Removes=" + stats.getCollectionRemoveCount(), buffer);
        writeObjectStat("Queries",
                "Executions=" + stats.getQueryExecutionCount() +
                        ", Hits=" + stats.getQueryCacheHitCount() +
                        ", Misses=" + stats.getQueryCacheMissCount() +
                        ", Puts=" + stats.getQueryCachePutCount() +
                        ", Max Time=" + stats.getQueryExecutionMaxTime(), buffer);
        writeObjectStat("Query Plan",
                "Hits=" + stats.getQueryPlanCacheHitCount() +
                        ", Misses=" + stats.getQueryPlanCacheMissCount(), buffer);
        buffer.append("</table>\n");
    }

    private static void writeObjectStat(String name, Object value, StringBuilder buffer) {
        buffer.append("<tr><td>");
        buffer.append(name);
        buffer.append("</td><td>");
        buffer.append(value);
        buffer.append("</td></tr>\n");
    }

    private static void writeEntityStats(Statistics stats, StringBuilder buffer) {
        buffer.append("<br>\n");
        buffer.append("<table>\n");
        buffer.append("<tr><th colspan=7>Entity Statistics</th></tr>");
        buffer.append("<tr><td><i>Class Name</i></td><td><i>Fetches</i></td><td><i>Loads</i></td><td><i>Inserts</i>");
        buffer.append("<td><i>Updates</i></td><td><i>Deletes</i></td><td><i>Optimistic Failures</i></td></tr>");

        String[] entityNames = stats.getEntityNames();
        Arrays.sort(entityNames);
        for (String entityName : entityNames) {
            EntityStatistics entityStats = stats.getEntityStatistics(entityName);

            long fetchCount = entityStats.getFetchCount();
            long loadCount = entityStats.getLoadCount();
            long insertCount = entityStats.getInsertCount();
            long updateCount = entityStats.getUpdateCount();
            long deleteCount = entityStats.getDeleteCount();
            long optimisticFailureCount = entityStats.getOptimisticFailureCount();

            if (fetchCount + loadCount + insertCount + updateCount + deleteCount + optimisticFailureCount <= 0) {
                // Hide unused entities
                continue;
            }

            buffer.append("<tr><td>");
            buffer.append(entityName);
            buffer.append("</td><td>");
            buffer.append(fetchCount);
            buffer.append("</td><td>");
            buffer.append(loadCount);
            buffer.append("</td><td>");
            buffer.append(insertCount);
            buffer.append("</td><td>");
            buffer.append(updateCount);
            buffer.append("</td><td>");
            buffer.append(deleteCount);
            buffer.append("</td><td>");
            buffer.append(optimisticFailureCount);
            buffer.append("</td></tr>\n");
        }
        buffer.append("</table>\n");
    }

    private static void writeCollectionStats(Statistics stats, StringBuilder buffer) {
        buffer.append("<br>\n");
        buffer.append("<table>\n");
        buffer.append("<tr><th colspan=6>Collection Statistics</th></tr>");
        buffer.append("<tr><td><i>Role Name</i></td><td><i>Fetches</i></td><td><i>Loads</i></td>");
        buffer.append("<td><i>Updates</i></td><td><i>Recreates</i></td><td><i>Removes</i></td></tr>");

        String[] collectionRoleNames = stats.getCollectionRoleNames();
        Arrays.sort(collectionRoleNames);

        for (String collectionRoleName : collectionRoleNames) {
            CollectionStatistics collectionStats = stats.getCollectionStatistics(collectionRoleName);

            long fetchCount = collectionStats.getFetchCount();
            long loadCount = collectionStats.getLoadCount();
            long updateCount = collectionStats.getUpdateCount();
            long recreateCount = collectionStats.getRecreateCount();
            long removeCount = collectionStats.getRemoveCount();

            if (fetchCount + loadCount + updateCount + recreateCount + removeCount <= 0) {
                // Hide unused collections
                continue;
            }

            buffer.append("<tr><td>");
            buffer.append(collectionRoleName);
            buffer.append("</td><td>");
            buffer.append(fetchCount);
            buffer.append("</td><td>");
            buffer.append(loadCount);
            buffer.append("</td><td>");
            buffer.append(updateCount);
            buffer.append("</td><td>");
            buffer.append(recreateCount);
            buffer.append("</td><td>");
            buffer.append(removeCount);
            buffer.append("</td></tr>\n");
        }
        buffer.append("</table>\n");
    }

    private static void writeCacheStats(Statistics stats, String title, Function<String, CacheRegionStatistics> finder, StringBuilder buffer) {
        buffer.append("<br>\n");
        buffer.append("<table>\n");
        buffer.append("<tr><th colspan=4>");
        buffer.append(title);
        buffer.append("</th></tr>");
        buffer.append("<tr><td><i>Region Name</i></td><td><i>Hits</i></td><td><i>Misses</i></td><td><i>Puts</i></td></tr>");

        String[] names = stats.getSecondLevelCacheRegionNames();
        Arrays.sort(names);
        for (String name : names) {
            CacheRegionStatistics cacheStats;
            try {
                cacheStats = finder.apply(name);
                if (cacheStats == null) {
                    continue;
                }
            }
            catch (Exception e) {
                continue;
            }

            if (cacheStats.getHitCount() + cacheStats.getMissCount() + cacheStats.getPutCount() <= 0) {
                // Hide unused caches
                continue;
            }

            buffer.append("<tr><td>");
            buffer.append(name);
            buffer.append("</td><td>");
            buffer.append(cacheStats.getHitCount());
            buffer.append("</td><td>");
            buffer.append(cacheStats.getMissCount());
            buffer.append("</td><td>");
            buffer.append(cacheStats.getPutCount());
            buffer.append("</td></tr>\n");
        }
        buffer.append("</table>\n");
    }

    private static void writeQueryStats(Statistics stats, StringBuilder buffer) {
        buffer.append("<br>\n");
        buffer.append("<table>\n");
        buffer.append("<tr><th colspan=10>Query Statistics</th></tr>");
        buffer.append("<tr><td><i>Query</i></td><td><i>Executions</i></td><td><i>Min Time</i></td><td><i>Max Time</i></td><td><i>Avg Time</i></td>");
        buffer.append("<td><i>Total Time</i></td><td><i>Total Rows</i></td><td><i>Hits</i></td><td><i>Misses</i></td><td><i>Puts</i></td></tr>");

        String[] queries = stats.getQueries();
        Arrays.sort(queries);
        for (String query : queries) {
            QueryStatistics queryStats = stats.getQueryStatistics(query);
            buffer.append("<tr><td>");
            buffer.append(query);
            buffer.append("</td><td>");
            buffer.append(queryStats.getExecutionCount()); // Count
            buffer.append("</td><td>");
            buffer.append(queryStats.getExecutionCount() <= 0 ? 0 : queryStats.getExecutionMinTime()); // Min
            buffer.append("</td><td>");
            buffer.append(queryStats.getExecutionMaxTime()); // Max
            buffer.append("</td><td>");
            buffer.append(queryStats.getExecutionAvgTime()); // Average
            buffer.append("</td><td>");
            buffer.append(queryStats.getExecutionCount() * queryStats.getExecutionAvgTime()); // Total
            buffer.append("</td><td>");
            buffer.append(queryStats.getExecutionRowCount()); // Rows
            buffer.append("</td><td>");
            buffer.append(queryStats.getCacheHitCount()); // Hit
            buffer.append("</td><td>");
            buffer.append(queryStats.getCacheMissCount()); // Miss
            buffer.append("</td><td>");
            buffer.append(queryStats.getCachePutCount()); // Put
            buffer.append("</td></tr>\n");
        }
        buffer.append("</table>\n");
    }

    private static String getFormattedTimestamp(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }
}
