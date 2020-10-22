package com.example

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.util.io.NullOutputStream
import java.io.File
import java.nio.file.Paths
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject


@ApplicationScoped
class GitBean {

    @Inject
    lateinit var config: CheckerConfiguration

    fun run() {
        if (config.root.isEmpty()) {
            config.root = Paths.get(System.getProperty("user.home")).toAbsolutePath()
                    .toString() + File.separator + "wc"
        }
        val root = File(config.root)
        if (!root.exists()) {
            root.mkdir()
        }

        config.getAllRepos().entries.forEach { it ->
//            val id = it.key
            val v = it.value

            for (s in v) {
                val lastIndex = s.lastIndexOf("/")
                var s2 = s.substring(lastIndex)
                val x = s2.lastIndexOf(".git")
                if (x != -1) {
                    s2 = s2.substring(1 until x)
//                println(s2)
                }
                val path = config.root + File.separator + s2

                if (!File(path).exists()) {             // clone
                    val git = Git.cloneRepository()
                            .setURI(s)
                            .setTransportConfigCallback {transport ->
                                (transport as SshTransport).sshSessionFactory = config.getSshSessionFactory()
                            }
                            .setDirectory(File(config.root + File.separator + s2))
                            .call()
                    git.close()
                } else {  // pull
                    val git = Git.open(File(path))
                    val pull = git.pull()
                    val result = pull.call()
                    println(result)
                    git.close()
                }

                val git = Git.open(File(path))
                // use the following instead to list commits on a specific branch
                //ObjectId branchId = repository.resolve("HEAD");
                //Iterable<RevCommit> commits = git.log().add(branchId).call();
                val commits: Iterable<RevCommit> = git.log().all().call()
                var count = 0
                val diffFormatter = DiffFormatter(NullOutputStream.INSTANCE)
                diffFormatter.setRepository(git.repository)
                for (commit in commits) {
                    // commit.commitTime  // commit 时间
                    val head = commit.tree
                    val parents = commit.parents
                    git.repository.newObjectReader().use { reader ->
                        val oldTreeIter: AbstractTreeIterator
                        if (parents.isNotEmpty()) {
                            oldTreeIter = CanonicalTreeParser()
                            val oldHead = commit.parents[0].tree
                            oldTreeIter.reset(reader, oldHead)
                        } else {
                            oldTreeIter = EmptyTreeIterator()
                        }
                        val newTreeIter = CanonicalTreeParser()
                        newTreeIter.reset(reader, head)
                        println("**************")
                        println(head.id)
                        // var config = DiffConfig::getRenameLimit
                        Git(git.repository).use { git ->
//                        val diffs = git.diff()
//                                .setNewTree(newTreeIter)
//                                .setOldTree(oldTreeIter)
//                                // .setShowNameAndStatusOnly(true)
//                                .call()
                            val diffs = diffFormatter.scan(oldTreeIter, newTreeIter)
                            for (entry in diffs) {
                                println("Entry: " + entry + ", from: " + entry.oldId + ", to: " + entry.newId)
                                println(entry.newPath)  // 文件或者路径名
                                if (entry.newId.name() != "0000000000000000000000000000000000000000") {
                                    val loader = git.repository.open(entry.newId.toObjectId())
                                    println("${loader.size} bytes, ${entry.newMode}, ${loader.type} , ${loader.isLarge}")
                                }
                                val fileHeader = diffFormatter.toFileHeader(entry)
                                for (hunk in fileHeader.hunks) {
                                    println("$hunk")
                                    println(hunk.toEditList())  // 可以用来计算增加了多少行，如果列表为空或者是[0-0]，就是增加或者二进制
                                }
                            }
                        }

                    }
                    count++
                }
                println(count)
            }
        }
    }
}


//private fun getDiff(file1: String, file2: String): String? {
//    val out: OutputStream = ByteArrayOutputStream()
//    try {
//        val rt1 = RawText(File(file1))
//        val rt2 = RawText(File(file2))
//        val diffList = EditList()
//
//        diffList.addAll(HistogramDiff().diff(C, rt1, rt2))
//        DiffFormatter(out).format(diffList, rt1, rt2)
//    } catch (e: IOException) {
//        e.printStackTrace()
//    }
//    return out.toString()
//}