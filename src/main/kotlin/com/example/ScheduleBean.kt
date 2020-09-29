package com.example

import io.quarkus.scheduler.Scheduled
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class ScheduleBean {

    @Inject
    lateinit var gitBean: GitBean

//    @Scheduled(every = "10s")
//    fun increment() {
//    }
//
//    @Scheduled(cron = "0 15 10 * * ?")
//    fun cronJob(execution: ScheduledExecution) {
//    }
//

    @Scheduled(cron = "{cron.expr}")
    fun cronJobWithExpressionInConfig() {
        gitBean.run()
    }
}