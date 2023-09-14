package com.example.fuuplugins.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


object ZIP {
    /**
     * DeCompress the ZIP to the path
     * @param zipFileString  name of ZIP
     * @param outPathString   path to be unZIP
     * @throws Exception
     */
    @Throws(Exception::class)
    fun unZipFolder(zipFileString: String, outPathString: String) {
        val inZip = ZipInputStream(FileInputStream(zipFileString))
        var zipEntry: ZipEntry? = null
        var szName = ""
        while (inZip.nextEntry.also { zipEntry = it } != null) {
            szName = zipEntry!!.name
            if (zipEntry!!.isDirectory) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length - 1)
                val folder = File(outPathString + File.separator + szName)
                folder.mkdirs()
            } else {
                val file = File(outPathString + File.separator + szName)
                file.createNewFile()
                // get the output stream of the file
                val out = FileOutputStream(file)
                var len: Int
                val buffer = ByteArray(1024)
                // read (len) bytes into buffer
                while (inZip.read(buffer).also { len = it } != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len)
                    out.flush()
                }
                out.close()
            }
        }
        inZip.close()
    }

    /**
     * Compress file and folder
     * @param srcFileString   file or folder to be Compress
     * @param zipFileString   the path name of result ZIP
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zipFolder(srcFileString: String?, zipFileString: String?) {
        //create ZIP
        val outZip = ZipOutputStream(FileOutputStream(zipFileString))
        //create the file
        val file = File(srcFileString)
        //compress
        zipFiles(file.parent + File.separator, file.name, outZip)
        //finish and close
        outZip.finish()
        outZip.close()
    }

    /**
     * compress files
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun zipFiles(
        folderString: String,
        fileString: String,
        zipOutputSteam: ZipOutputStream?
    ) {
        if (zipOutputSteam == null) return
        val file = File(folderString + fileString)
        if (file.isFile) {
            val zipEntry = ZipEntry(fileString)
            val inputStream = FileInputStream(file)
            zipOutputSteam.putNextEntry(zipEntry)
            var len: Int
            val buffer = ByteArray(4096)
            while (inputStream.read(buffer).also { len = it } != -1) {
                zipOutputSteam.write(buffer, 0, len)
            }
            zipOutputSteam.closeEntry()
        } else {
            //folder
            val fileList = file.list()
            //no child file and compress
            if (fileList.size <= 0) {
                val zipEntry = ZipEntry(fileString + File.separator)
                zipOutputSteam.putNextEntry(zipEntry)
                zipOutputSteam.closeEntry()
            }
            //child files and recursion
            for (i in fileList.indices) {
                zipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam)
            } //end of for
        }
    }

    /**
     * return the InputStream of file in the ZIP
     * @param zipFileString  name of ZIP
     * @param fileString     name of file in the ZIP
     * @return InputStream
     * @throws Exception
     */
    @Throws(Exception::class)
    fun upZip(zipFileString: String?, fileString: String?): InputStream {
        val zipFile = ZipFile(zipFileString)
        val zipEntry = zipFile.getEntry(fileString)
        return zipFile.getInputStream(zipEntry)
    }

    /**
     * return files list(file and folder) in the ZIP
     * @param zipFileString     ZIP name
     * @param bContainFolder    contain folder or not
     * @param bContainFile      contain file or not
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getFileList(
        zipFileString: String?,
        bContainFolder: Boolean,
        bContainFile: Boolean
    ): List<*> {
        val fileList: MutableList<File> = ArrayList<File>()
        val inZip = ZipInputStream(FileInputStream(zipFileString))
        var zipEntry: ZipEntry
        var szName = ""
        while (inZip.nextEntry.also { zipEntry = it } != null) {
            szName = zipEntry.name
            if (zipEntry.isDirectory) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length - 1)
                val folder = File(szName)
                if (bContainFolder) {
                    fileList.add(folder)
                }
            } else {
                val file = File(szName)
                if (bContainFile) {
                    fileList.add(file)
                }
            }
        }
        inZip.close()
        return fileList
    }
}

