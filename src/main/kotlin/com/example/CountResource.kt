package com.example

import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("/count")
class CountResource {
    @Inject
    var counter: CounterBean? = null
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(): String {
        return "count: " + counter!!.get()
    }
}