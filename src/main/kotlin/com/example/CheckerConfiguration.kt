package com.example

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import io.quarkus.arc.config.ConfigProperties
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.util.FS
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.File
import java.nio.file.Paths
import java.util.ArrayList
import javax.inject.Inject
import javax.json.JsonArray
import javax.json.JsonObject

@ConfigProperties(prefix = "wc")
class CheckerConfiguration {
    var root: String = ""   // where to save repos
    var key: String = ""    // SSH key
    var passwd: String = ""   // SSH key password

    var repos: JsonObject? = null

    fun getAllRepos(): HashMap<String, List<String>> {
        var ans = HashMap<String, List<String>>()
        repos?.values?.forEach { arr ->
            val a = arr as JsonArray
            a.forEach {
                val map = it as JsonObject
                map.entries.forEach { (k, v) ->
                    val key = k.toString()
                    val list = v as JsonArray
                    ans[key] = list.map { j ->
                        j.toString()
                    }
                }
            }
        }
        return ans
    }


    fun getSshSessionFactory(): SshSessionFactory {
        return object : JschConfigSessionFactory() {
            override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
                super.configure(hc, session)
                session?.setConfig("StrictHostKeyChecking", "no")
            }

            override fun createDefaultJSch(fs: FS?): JSch {
                var jsch = super.createDefaultJSch(fs)
                if (key.isEmpty()) {
                    key = Paths.get(System.getProperty("user.home")).toAbsolutePath()
                            .toString() + File.separator + ".ssh" + File.separator + "id_rsa"
                }
                jsch.addIdentity(key, passwd)
                return jsch
            }
        }
    }

}
