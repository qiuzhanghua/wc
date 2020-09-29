package com.example

import io.quarkus.runtime.StartupEvent
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class MyApplicationLifecycle {
    fun onStart(@Observes ev: StartupEvent?, gitBean: GitBean) {
        gitBean.run()
    }
}