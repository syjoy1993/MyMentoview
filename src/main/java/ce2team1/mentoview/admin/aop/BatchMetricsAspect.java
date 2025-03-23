package ce2team1.mentoview.admin.aop;

import ce2team1.mentoview.admin.aop.annotation.TrackBatchMetric;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchMetricsAspect {

    private final MeterRegistry meterRegistry;

    @Around(value = "@annotation(trackBatchMetric)", argNames = "joinPoint,trackBatchMetric")
    public Object around(ProceedingJoinPoint joinPoint, TrackBatchMetric trackBatchMetric) throws Throwable {
        String batchName = trackBatchMetric.value();
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed(); // 실제 작업 proceed
            meterRegistry.counter("batchName." + batchName + ".success.count").increment();
            return result;
        }catch (Exception e) {
            meterRegistry.counter("batchName." + batchName + ".failure.count").increment();
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            long duration = end - start;
            meterRegistry.timer("batchName." + batchName + ".duration").record(duration, TimeUnit.MILLISECONDS);
            log.info("배치 작업 '{}' 완료 (소요 시간: {}ms)", batchName, duration);
        }
    }





}
