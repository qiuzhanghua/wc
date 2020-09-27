package com.example

import io.quarkus.arc.config.ConfigProperties
@ConfigProperties(prefix = "wc")
class CheckerConfiguration {
    var root: String = ""   // where to save repos
    var key: String = ""    // SSH key
    var passwd: String = ""   // SSH key password
    lateinit var repos: List<String>  // List of repository
}
