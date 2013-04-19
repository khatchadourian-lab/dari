package com.psddev.dari.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

@Metric.Embedded
public class Metric extends Record {

    private transient final MetricDatabase metricDatabase;

    protected Metric(State state, String fieldInternalName) {
        ObjectField field = state.getField(fieldInternalName);

        this.metricDatabase = new MetricDatabase(state, field.getUniqueName());
        this.metricDatabase.setEventDateProcessor(field.as(MetricDatabase.FieldData.class).getEventDateProcessor());
    }

    /**
     * Increases the metric value by the given {@code amount} right now.
     */
    public void incrementNow(double amount) {
        increment(amount, null, null);
    }

    /**
     * Increases the metric value by the given {@code amount} and associate it
     * with the given {@code dimension} and {@code date}.
     *
     * @param dimension May be {@code null}.
     * @param date If {@code null}, right now.
     */
    public void increment(double amount, String dimension, DateTime date) {
        try {
            metricDatabase.setEventDate(date);
            metricDatabase.incrementMetric(dimension, amount);
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.incrementMetric() : " + e.getLocalizedMessage());
        }
    }

    /** Deletes all metric values. */
    public void deleteAll() {
        try {
            metricDatabase.deleteMetric();
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.deleteMetric() : " + e.getLocalizedMessage());
        }
    }

    /**
     * Returns the metric value associated with the given {@code dimension}
     * between the given {@code start} and {@code end}.
     *
     * @param dimension May be {@code null}.
     * @param start If {@code null}, beginning of time.
     * @param end If {@code null}, end of time.
     */
    public double getByDimensionBetween(String dimension, DateTime start, DateTime end) {
        try {
            metricDatabase.setQueryDateRange(start, end);
            Double metricValue = metricDatabase.getMetric(dimension);
            return metricValue == null ? 0.0 : metricValue;
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.getMetric() : " + e.getLocalizedMessage());
        }
    }

    /**
     * Returns the metric value associated with the given {@code dimension}.
     *
     * @param dimension May be {@code null}.
     */
    public double getByDimension(String dimension) {
        return getByDimensionBetween(dimension, null, null);
    }

    /**
     * Returns the metric value associated with the main ({@code null})
     * dimension between the given {@code start} and {@code end}.
     */
    public double get(DateTime start, DateTime end) {
        return getByDimensionBetween(null, start, end);
    }

    /**
     * Returns the metric value associated with the main ({@code null})
     * dimension.
     */
    public double get() {
        return getByDimensionBetween(null, null, null);
    }

    /**
     * Returns the sum of all metric values in each dimension between the given
     * {@code start} and {@code end}.
     *
     * @param start If {@code null}, beginning of time.
     * @param end If {@code null}, end of time.
     */
    public double getSumBetween(DateTime start, DateTime end) {
        try {
            metricDatabase.setQueryDateRange(start, end);
            Double metricValue = metricDatabase.getMetricSum();
            return metricValue == null ? 0.0 : metricValue;
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.getMetric() : " + e.getLocalizedMessage());
        }
    }

    /**
     * Returns the sum of all metric values in each dimension.
     */
    public double getSum() {
        return getSumBetween(null, null);
    }

    /**
     * Groups the metric values between the given {@code start} and {@code end}
     * by each dimension.
     *
     * @param start If {@code null}, beginning of time.
     * @param end If {@code null}, end of time.
     * @return Never {@code null}.
     */
    public Map<String, Double> groupByDimensionBetween(DateTime start, DateTime end) {
        try {
            metricDatabase.setQueryDateRange(start, end);
            Map<String, Double> metricValues = metricDatabase.getMetricValues();
            return metricValues == null ? new HashMap<String, Double>() : metricValues;
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.getMetric() : " + e.getLocalizedMessage());
        }
    }

    /**
     * Groups the metric values by each dimension.
     *
     * @return Never {@code null}.
     */
    public Map<String, Double> groupByDimension() {
        return groupByDimensionBetween(null, null);
    }

    /**
     * Groups the metric values associated with the given {@code dimension}
     * between the given {@code start} and {@code end} by the given
     * {@code interval}.
     *
     * @param dimension May be {@code null}.
     * @param start If {@code null}, beginning of time.
     * @param end If {@code null}, end of time.
     * @return Never {@code null}.
     */
    public Map<DateTime, Double> groupByDate(String dimension, MetricInterval interval, DateTime start, DateTime end) {
        try {
            metricDatabase.setQueryDateRange(start, end);
            Map<DateTime, Double> metricTimeline = metricDatabase.getMetricTimeline(dimension, interval);
            return metricTimeline == null ? new HashMap<DateTime, Double>() : metricTimeline;
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.getMetricTimeline() : " + e.getLocalizedMessage());
        }
    }

    /**
     * Groups the sum of all metric values between the given {@code start} and
     * {@code end} by the given {@code interval}.
     *
     * @param start If {@code null}, beginning of time.
     * @param end If {@code null}, end of time.
     * @return Never {@code null}.
     */
    public Map<DateTime, Double> groupSumByDate(MetricInterval interval, DateTime start, DateTime end) {
        try {
            metricDatabase.setQueryDateRange(start, end);
            Map<DateTime, Double> metricTimeline = metricDatabase.getMetricSumTimeline(interval);
            return metricTimeline == null ? new HashMap<DateTime, Double>() : metricTimeline;
        } catch (SQLException e) {
            throw new DatabaseException(metricDatabase.getDatabase(), "Error in MetricDatabase.getSumTimeline() : " + e.getLocalizedMessage());
        }
    }
}