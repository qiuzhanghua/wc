package com.example

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffConfig
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.patch.BinaryHunk
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.util.io.DisabledOutputStream
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

//            if (!File(path).exists()) {             // clone
//                val git = Git.cloneRepository()
//                        .setURI(s)
//                        .setDirectory(File(config.root + File.separator + s2))
//                        .call()
//                git.close()
//            } else {  // pull
//                val git = Git.open(File(path))
//                val pull = git.pull()
//                val result = pull.call()
//                println(result)
//                git.close()
//            }

            val git = Git.open(File(path))
            // use the following instead to list commits on a specific branch
            //ObjectId branchId = repository.resolve("HEAD");
            //Iterable<RevCommit> commits = git.log().add(branchId).call();
            val commits: Iterable<RevCommit> = git.log().all().call()
            var count = 0

            for (commit in commits) {
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
//                            val diffs = git.diff()
//                                    .setNewTree(newTreeIter)
//                                    .setOldTree(oldTreeIter)
//                                    // .setShowNameAndStatusOnly(true)
//                                    .call()
//                            for (entry in diffs) {
//                                println("Entry: $entry")
//                                println("${entry.diffAttribute}")
//                                println("Entry: " + entry + ", from: " + entry.oldId + ", to: " + entry.newId)
//                                DiffFormatter(System.out).use { formatter ->
//                                    formatter.setRepository(git.repository)
//                                    formatter.format(entry)
//                                }
//                            }
                        DiffFormatter(DisabledOutputStream.INSTANCE).use { diffFormatter ->
                            diffFormatter.setRepository(git.repository)
                            val diffEntries: List<DiffEntry> = diffFormatter.scan(oldTreeIter, newTreeIter)
                            for (entry in diffEntries) {
                                println(entry)
                                val fileHeader = diffFormatter.toFileHeader(entry)
                                for (hunk in fileHeader.hunks) {
                                    // TODO hunk is BinaryHunk?
                                    println("offset : ${hunk.startOffset}, ${hunk.endOffset}")
                                    println(hunk.toEditList())
                                }
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