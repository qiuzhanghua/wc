package com.example

import io.quarkus.scheduler.Scheduled
import io.quarkus.scheduler.ScheduledExecution
import java.util.concurrent.atomic.AtomicInteger
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class CounterBean {
    private val counter = AtomicInteger()
    fun get(): Int {
        return counter.get()
    }

    @Scheduled(every = "10s")
    fun increment() {
        counter.incrementAndGet()
    }

    @Scheduled(cron = "0 15 10 * * ?")
    fun cronJob(execution: ScheduledExecution) {
        counter.incrementAndGet()
        println(execution.scheduledFireTime)
    }

    @Scheduled(cron = "{cron.expr}")
    fun cronJobWithExpressionInConfig() {
        counter.incrementAndGet()
//        println("Cron expression configured in application.properties")
    }
}