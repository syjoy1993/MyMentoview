package ce2team1.mentoview.admin.aop.annotation;

import io.micrometer.core.annotation.Timed;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Timed(value = "batch.default", longTask = true )
public @interface TrackBatchMetric {

    String value();
}
