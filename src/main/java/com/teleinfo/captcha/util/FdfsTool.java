package com.teleinfo.captcha.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.github.tobato.fastdfs.domain.MataData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.proto.storage.DownloadFileWriter;
import com.github.tobato.fastdfs.service.FastFileStorageClient;

/**
 * 
 * 
 * @author TBC
 *
 */
@Component
public class FdfsTool {

	private final Logger logger = LoggerFactory.getLogger(FdfsTool.class);

	@Autowired
	private FastFileStorageClient storageClient;

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            文件对象
	 * @return 文件访问地址
	 * @throws IOException
	 */
	public String uploadFile(MultipartFile file) {
		try {
			StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
					FilenameUtils.getExtension(file.getOriginalFilename()), null);
			return storePath.getPath();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 上传文件
	 * 
	 * @param file
	 *            文件对象
	 * @return 文件访问地址
	 * @throws IOException
	 */
	public String uploadFile(File file) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			byte[] bs = IOUtils.toByteArray(fileInputStream);
			StorePath storePath = storageClient.uploadFile(fileInputStream, bs.length,
					FilenameUtils.getExtension(file.getName()), null);
			return storePath.getPath();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 将一段字符串生成一个文件上传
	 * 
	 * @param content
	 *            文件内容
	 * @param fileExtension
	 *            文件后缀,后缀不能带"."
	 * @return
	 */
	public String uploadFile(String content, String fileExtension) {
		byte[] buff = content.getBytes(Charset.forName("UTF-8"));
		ByteArrayInputStream stream = new ByteArrayInputStream(buff);
		StorePath storePath = storageClient.uploadFile(stream, buff.length, fileExtension, null);
		return storePath.getPath();
	}

	/**
	 * 将一段字符串生成一个文件上传
	 * 
	 * @param content
	 *            文件内容
	 * @param fileExtension
	 *            文件后缀,后缀不能带"."
	 * @return
	 */
	public String uploadFile(String groupName, String content, String fileExtension) {
		byte[] buff = content.getBytes(Charset.forName("UTF-8"));
		ByteArrayInputStream stream = new ByteArrayInputStream(buff);
		StorePath storePath = storageClient.uploadFile(groupName, stream, buff.length, fileExtension);
		return storePath.getPath();
	}

	/**
	 * 将一段字符串生成一个文件上传
	 * 
	 * @param content
	 *            文件内容
	 * @param fileExtension
	 *            文件后缀,后缀不能带"."
	 * @return
	 */
	public String uploadFile(InputStream inputStream, String fileExtension) {
		try {
			StorePath storePath = storageClient.uploadFile(inputStream, inputStream.available(), fileExtension, null);
			return storePath.getPath();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * 上传二进制数组
	 * 
	 * @param content
	 * @param fileExtension
	 * @return
	 */
	public String uploadFile(byte[] buff, String fileExtension) {
		ByteArrayInputStream stream = new ByteArrayInputStream(buff);
		StorePath storePath = storageClient.uploadFile(stream, stream.available(), fileExtension, null);
		return storePath.getPath();
	}

	/**
	 * 删除文件
	 * 
	 * @param fileUrl
	 *            group+"/"+path
	 * @return
	 */
	public void deleteFile(String fileUrl) {
		if (StringUtils.isEmpty(fileUrl)) {
			return;
		}
		try {
			StorePath storePath = StorePath.praseFromUrl(fileUrl);
			storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
		} catch (FdfsUnsupportStorePathException e) {
			logger.warn(e.getMessage());
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param groupName
	 *            组
	 * @param path
	 *            相对路径
	 */
	public void deleteFile(String groupName, String path) {
		if (StringUtils.isEmpty(path)) {
			return;
		}
		storageClient.deleteFile(groupName, path);
	}

	/**
	 * 上传图片并且生成缩略图
	 * 
	 * @param inputStream
	 *            输入流
	 * @param fileSize
	 *            文件大小
	 * @param fileExtName
	 *            后缀
	 * @param metaDataSet
	 *            文章元数据
	 * @return
	 */
	public String uploadImageAndCrtThumbImage(InputStream inputStream, long fileSize, String fileExtName,
			Set<MataData> metaDataSet) {
		StorePath storePath = storageClient.uploadImageAndCrtThumbImage(inputStream, fileSize, fileExtName,
				metaDataSet);
		return storePath.getPath();
	}

	/**
	 * 下载文件
	 * 
	 * @param groupName
	 * @param path
	 * @return
	 */
	public byte[] downloadFile(String groupName, String path) {
		return storageClient.downloadFile(groupName, path, new DownloadByteArray());
	}

	/**
	 * 下载文件
	 * 
	 * @param groupName
	 * @param path
	 * @return
	 */
	public InputStream downloadFileInputStream(String groupName, String path) {
		byte[] bs = storageClient.downloadFile(groupName, path, new DownloadByteArray());
		return new ByteArrayInputStream(bs);
	}

	/**
	 * 将文件下载到自定义位置
	 * 
	 * @param groupName
	 * @param path
	 * @param downloadFilPath
	 *            自定义文件位置
	 */
	public void downloadFile(String groupName, String path, String downloadFilPath) {
		storageClient.downloadFile(groupName, path, new DownloadFileWriter(downloadFilPath));
	}

}
