package com.adeliosys.sample;

import org.hibernate.BaseSessionEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This custom implementation is inspired by org.hibernate.engine.internal.StatisticalLoggingSessionEventListener
 * and is used to log Hibernate session metrics in a more developer friendly way.
 */
@SuppressWarnings("unused")
public class CustomSessionEventListener extends BaseSessionEventListener {

    protected static final Logger LOGGER = LoggerFactory.getLogger(CustomSessionEventListener.class);

    private int jdbcConnectionAcquisitionCount;
    private long jdbcConnectionAcquisitionTime;

    private int jdbcConnectionReleaseCount;
    private long jdbcConnectionReleaseTime;

    private int jdbcPrepareStatementCount;
    private long jdbcPrepareStatementTime;

    private int jdbcExecuteStatementCount;
    private long jdbcExecuteStatementTime;

    private int jdbcExecuteBatchCount;
    private long jdbcExecuteBatchTime;

    private int cachePutCount;
    private long cachePutTime;

    private int cacheHitCount;
    private long cacheHitTime;

    private int cacheMissCount;
    private long cacheMissTime;

    private int flushCount;
    private long flushEntityCount;
    private long flushCollectionCount;
    private long flushTime;

    private int partialFlushCount;
    private long partialFlushEntityCount;
    private long partialFlushCollectionCount;
    private long partialFlushTime;

    private long jdbcConnectionAcquisitionStart = -1;

    @Override
    public void jdbcConnectionAcquisitionStart() {
        assert jdbcConnectionAcquisitionStart < 0 : "Nested calls to jdbcConnectionAcquisitionStart";
        jdbcConnectionAcquisitionStart = System.nanoTime();
    }

    @Override
    public void jdbcConnectionAcquisitionEnd() {
        assert jdbcConnectionAcquisitionStart > 0 :
                "Unexpected call to jdbcConnectionAcquisitionEnd; expecting jdbcConnectionAcquisitionStart";

        jdbcConnectionAcquisitionCount++;
        jdbcConnectionAcquisitionTime += (System.nanoTime() - jdbcConnectionAcquisitionStart);
        jdbcConnectionAcquisitionStart = -1;
    }

    private long jdbcConnectionReleaseStart = -1;

    @Override
    public void jdbcConnectionReleaseStart() {
        assert jdbcConnectionReleaseStart < 0 : "Nested calls to jdbcConnectionReleaseStart";
        jdbcConnectionReleaseStart = System.nanoTime();
    }

    @Override
    public void jdbcConnectionReleaseEnd() {
        assert jdbcConnectionReleaseStart > 0 :
                "Unexpected call to jdbcConnectionReleaseEnd; expecting jdbcConnectionReleaseStart";

        jdbcConnectionReleaseCount++;
        jdbcConnectionReleaseTime += (System.nanoTime() - jdbcConnectionReleaseStart);
        jdbcConnectionReleaseStart = -1;
    }

    private long jdbcPrepStart = -1;

    @Override
    public void jdbcPrepareStatementStart() {
        assert jdbcPrepStart < 0 : "Nested calls to jdbcPrepareStatementStart";
        jdbcPrepStart = System.nanoTime();
    }

    @Override
    public void jdbcPrepareStatementEnd() {
        assert jdbcPrepStart > 0 : "Unexpected call to jdbcPrepareStatementEnd; expecting jdbcPrepareStatementStart";

        jdbcPrepareStatementCount++;
        jdbcPrepareStatementTime += (System.nanoTime() - jdbcPrepStart);
        jdbcPrepStart = -1;
    }

    private long jdbcExecutionStart = -1;

    @Override
    public void jdbcExecuteStatementStart() {
        assert jdbcExecutionStart < 0 : "Nested calls to jdbcExecuteStatementStart";
        jdbcExecutionStart = System.nanoTime();
    }

    @Override
    public void jdbcExecuteStatementEnd() {
        assert jdbcExecutionStart > 0 : "Unexpected call to jdbcExecuteStatementEnd; expecting jdbcExecuteStatementStart";

        jdbcExecuteStatementCount++;
        jdbcExecuteStatementTime += (System.nanoTime() - jdbcExecutionStart);
        jdbcExecutionStart = -1;
    }

    private long jdbcBatchExecutionStart = -1;

    @Override
    public void jdbcExecuteBatchStart() {
        assert jdbcBatchExecutionStart < 0 : "Nested calls to jdbcExecuteBatchStart";
        jdbcBatchExecutionStart = System.nanoTime();
    }

    @Override
    public void jdbcExecuteBatchEnd() {
        assert jdbcBatchExecutionStart > 0 : "Unexpected call to jdbcExecuteBatchEnd; expecting jdbcExecuteBatchStart";

        jdbcExecuteBatchCount++;
        jdbcExecuteBatchTime += (System.nanoTime() - jdbcBatchExecutionStart);
        jdbcBatchExecutionStart = -1;
    }

    private long cachePutStart = -1;

    @Override
    public void cachePutStart() {
        assert cachePutStart < 0 : "Nested calls to cachePutStart";
        cachePutStart = System.nanoTime();
    }

    @Override
    public void cachePutEnd() {
        assert cachePutStart > 0 : "Unexpected call to cachePutEnd; expecting cachePutStart";

        cachePutCount++;
        cachePutTime += (System.nanoTime() - cachePutStart);
        cachePutStart = -1;
    }

    private long cacheGetStart = -1;

    @Override
    public void cacheGetStart() {
        assert cacheGetStart < 0 : "Nested calls to cacheGetStart";
        cacheGetStart = System.nanoTime();
    }

    @Override
    public void cacheGetEnd(boolean hit) {
        assert cacheGetStart > 0 : "Unexpected call to cacheGetEnd; expecting cacheGetStart";

        if (hit) {
            cacheHitCount++;
            cacheHitTime += (System.nanoTime() - cacheGetStart);
        } else {
            cacheMissCount++;
            cacheMissTime += (System.nanoTime() - cacheGetStart);
        }
        cacheGetStart = -1;
    }

    private long flushStart = -1;

    @Override
    public void flushStart() {
        assert flushStart < 0 : "Nested calls to flushStart";
        flushStart = System.nanoTime();
    }

    @Override
    public void flushEnd(int numberOfEntities, int numberOfCollections) {
        assert flushStart > 0 : "Unexpected call to flushEnd; expecting flushStart";

        flushCount++;
        flushEntityCount += numberOfEntities;
        flushCollectionCount += numberOfCollections;
        flushTime += (System.nanoTime() - flushStart);
        flushStart = -1;
    }

    private long partialFlushStart = -1;

    @Override
    public void partialFlushStart() {
        assert partialFlushStart < 0 : "Nested calls to partialFlushStart";
        partialFlushStart = System.nanoTime();
    }

    @Override
    public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
        assert partialFlushStart > 0 : "Unexpected call to partialFlushEnd; expecting partialFlushStart";

        partialFlushCount++;
        partialFlushEntityCount += numberOfEntities;
        partialFlushCollectionCount += numberOfCollections;
        partialFlushTime += (System.nanoTime() - partialFlushStart);
        partialFlushStart = -1;
    }

    @Override
    public void end() {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }

        // Lgo metrics only for session that were actually used
        if (jdbcConnectionAcquisitionCount <= 0) {
            return;
        }

        LOGGER.debug("Session metrics:\n"
                        + "    connections  : acquired {} ({} μs)\n"
                        + "    statements   : prepared {} ({} μs), executed {} ({} μs)\n"
                        + "    JDBC batches : executed {} ({} μs)\n"
                        + "    cache        : {} puts ({} μs), {} hits ({} μs), {} misses ({} μs)\n"
                        + "    flushes      : executed {} ({} μs) for {} entities and {} collections, executed {} partials ({} μs) for {} entities and {} collections",
                jdbcConnectionAcquisitionCount,
                jdbcConnectionAcquisitionTime / 1000,
                jdbcPrepareStatementCount,
                jdbcPrepareStatementTime / 1000,
                jdbcExecuteStatementCount,
                jdbcExecuteStatementTime / 1000,
                jdbcExecuteBatchCount,
                jdbcExecuteBatchTime / 1000,
                cachePutCount,
                cachePutTime,
                cacheHitCount,
                cacheHitTime,
                cacheMissCount,
                cacheMissTime,
                flushCount,
                flushTime / 1000,
                flushEntityCount,
                flushCollectionCount,
                partialFlushCount,
                partialFlushTime / 1000,
                partialFlushEntityCount,
                partialFlushCollectionCount
        );
    }
}
