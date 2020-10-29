package com.example

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/run")
class RunResource {
    @Inject
    lateinit var gitBean: GitBean

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): String {
        gitBean.run()
        return "finished!"
    }

}