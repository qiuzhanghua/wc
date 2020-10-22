package com.example

import com.jcraft.jsch.JSch
import io.quarkus.arc.config.ConfigProperties
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.util.FS
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.ArrayList
import javax.inject.Inject
import javax.json.JsonArray
import javax.json.JsonObject

@ConfigProperties(prefix = "wc")
class CheckerConfiguration {
    var root: String = ""   // where to save repos
    var key: String = "/Users/quick/.ssh/id_rsa"    // SSH key
    var passwd: String = ""   // SSH key password

    @Inject
    @ConfigProperty(name = "repos")
    var repos: JsonObject? = null

    fun getAllRepos(): HashMap<String, List<String>> {
        var ans = HashMap<String, List<String>>()
        repos?.entries?.forEach { entry ->
            val key = entry.key
            val v = entry.value as JsonArray
            var list = ArrayList<String>()
            v.forEach {
                list.add(it.toString())
            }
            ans[key] = list
        }
        return ans
    }


    fun getSshSessionFactory() : SshSessionFactory {
        return object : JschConfigSessionFactory() {
            override fun createDefaultJSch(fs: FS?): JSch {
                var jsch = super.createDefaultJSch(fs)
                jsch.addIdentity(key, "")
                return jsch
            }
        }
    }

}
