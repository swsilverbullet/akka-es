package commons.core

import java.io.{File, FilenameFilter, IOException}
import java.util.UUID

import commons.core.test.BuildSystem.{BuildSystem, Gradle, Maven}
import commons.core.time.SystemTimeProvider

import scala.util.control.NonFatal

package object test {

  object BuildSystem {
    sealed trait BuildSystem {
      def tempDirRoot: String
    }

    object Maven extends BuildSystem {
      override def tempDirRoot = "target"
    }

    object Gradle extends BuildSystem {
      override def tempDirRoot = "build"
    }
  }

  var buildSystem: BuildSystem = {
    val sysPropName = "commons.core.test.buildSystem"
    sys.props.get(sysPropName) match {
      case Some(value) =>
        val lower = value.toLowerCase
        lower match {
          case "" => Gradle
          case "gradle" => Gradle
          case "maven" => Maven
          case bad => sys.error(s"system property '$sysPropName' has an invalid value '$bad'")
        }
      case None => Gradle
    }
  }

  def createLocalTempDir(seed: String): File = createTempObject(seed, createDir)

  def createLocalTempFile(seed: String): File = createTempObject(seed, createFile)

  def withTempFile[T](seed: String)(block: File => T): T = {
    val file = createLocalTempFile(seed)
    try block(file)
    finally removeExisiting(file)
  }

  def withTempDir[T](seed: String)(block: File => T): T = {
    val dir = createLocalTempDir(seed)
    try block(dir)
    finally removeExisiting(dir)
  }

  def createTempObject(seed: String, onCreate: File => Unit): File = {
    val obj = createTempName(seed)
    onCreate(obj)
    obj
  }

  def createTempName(seed: String): File = {
    val parentDir = createTargetDirectory()
    val fileName = seed + ".tempfile"
    cleanupSimiliarOldFiles(parentDir, fileName)

    val uniqueSuffix = UUID.randomUUID()
    val uniqueFile = new File(parentDir, fileName + "." + uniqueSuffix)
    uniqueFile.deleteOnExit()
    uniqueFile
  }

  private def createTargetDirectory(): File = {
    val parentDir = new File(sys.props("user.dir"), buildSystem.tempDirRoot)
    createDir(parentDir)
    parentDir
  }

  private def cleanupSimiliarOldFiles(parentDir: File, fileName: String) =
    if (parentDir.exists()) {
      parentDir.listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = {
          val ref = new File(dir, name)
          val housekeepingAgeLimitMillis = 1000 * 60 * 5
          val isOld = (SystemTimeProvider.millis - ref.lastModified()) > housekeepingAgeLimitMillis
          isOld && name.startsWith(fileName)
        }
      }).foreach {
        f =>
          try {
            org.apache.commons.io.FileUtils.forceDelete(f)
          } catch {
            case ex: IOException => //ignored
          }
      }
    }

  private def removeExisiting(obj: File): Unit = if (obj.exists()) {
    try {
      org.apache.commons.io.FileUtils.forceDelete(obj)
    } catch {
      case ioex: IOException =>
        if (obj.exists()) {
          val wait = 3000
          System.out.println(s"cleanup of ${obj.getAbsolutePath} failed because ${ioex.getMessage} - retrying in ${wait} ms")
          Thread.sleep(wait)
        }

        try {
          org.apache.commons.io.FileUtils.forceDelete(obj)
        } catch {
          case ex: IOException => //ignored
        }
    }
  }

  private def createDir(f: File): Unit = {
    try f.mkdirs()
    catch {
      case NonFatal(e) => handleCreateFileError(f, e)
    }
  }

  private def createFile(f: File): Unit = {
    try f.createNewFile()
    catch {
      case NonFatal(e) => handleCreateFileError(f, e)
    }
  }
  private def handleCreateFileError(f: File, t: Throwable) = {
    throw new IllegalStateException(s"couldn't create $f", t)
  }
}
