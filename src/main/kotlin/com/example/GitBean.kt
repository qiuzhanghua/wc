package com.example

import org.eclipse.jgit.api.Git
import java.io.File
import java.nio.file.Paths
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class GitBean {

    @Inject
    lateinit var config: CheckerConfiguration

    fun init() {
        if (config.root == "") {
            config.root = Paths.get(System.getProperty("user.home")).toAbsolutePath()
                    .toString() + File.separator + "wc"
        }
        val root = File(config.root)
        if (!root.exists()) {
            root.mkdir()
        }
        for (s in config.repos) {
            val lastIndex = s.lastIndexOf("/")
            var s2 = s.substring(lastIndex)
            val x = s2.lastIndexOf(".git")
            if (x != -1) {
                s2 = s2.substring(1 until x)
//                println(s2)
            }
            val path = config.root + File.separator + s2
            if (!File(path).exists()) {
                val git = Git.cloneRepository()
                        .setURI(s)
                        .setDirectory(File(config.root + File.separator + s2))
                        .call()
                git.close()
            } else {
                val git = Git.open(File(path))
                // val walk = RevWalk(git.repository)
//                val commitLog = git.log().call()
//                commitLog.forEach(Consumer { r: RevCommit -> println(r.fullMessage) })
//                val result = git.fetch().setCheckFetchedObjects(true).call()
//                println("Messages: " + result.messages)
                val pull = git.pull()
                val result = pull.call()
                println(result)
                git.close()
            }

        }

    }
}