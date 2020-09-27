package com.example

import org.eclipse.microprofile.config.ConfigProvider
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("/")
class HomeResource {

    @Inject
    lateinit var gitBean: GitBean

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): String {

//        val repos = ConfigProvider.getConfig().getValue("wc.repos", String::class.java)
//        println(repos)
        gitBean.init()

        return "Work Checker!"
    }
}