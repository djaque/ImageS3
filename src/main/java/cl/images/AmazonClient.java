package cl.images;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * AmazonClient is a service to interact with AWS S3
 * @author dany
 *
 */
@Service
public class AmazonClient {

	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}")
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}")
	private String bucketName;
	@Value("${amazonProperties.accessKey}")
	private String accessKey;
	@Value("${amazonProperties.secretKey}")
	private String secretKey;
	@Value("${amazonProperties.region}")
	private String region;

	/**
	 * Build the Amazon client to work with the S3 service
	 */
	@PostConstruct
	private void initializeAmazon() {
		BasicAWSCredentials creds = new BasicAWSCredentials(this.accessKey, this.secretKey); 
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(creds))
				.withRegion(Regions.fromName(this.region))
				.build();
		this.setS3client(s3Client);

	}

	public AmazonS3 getS3client() {
		return s3client;
	}

	public void setS3client(AmazonS3 s3client) {
		this.s3client = s3client;
	}

	/**
	 * Convert the multipartFile of request into a new file 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	/**
	 * Generate a new name using the time as prefix, so the file name will be unique
	 * @param multiPart
	 * @return
	 */
	private String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	/**
	 * Send the content to S3, as new object in the bucket
	 * @param fileName
	 * @param file
	 */
	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file)
				.withCannedAcl(CannedAccessControlList.PublicRead)
		);
	}

	/**
	 * Process MultipartFile and send it to S3
	 * @param multipartFile
	 * @return AmazonStatus
	 */
	public AmazonStatus uploadFile(MultipartFile multipartFile) {
		AmazonStatus aStatus = new AmazonStatus();
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			aStatus.setFile(endpointUrl + "/" + bucketName + "/" + fileName);
			uploadFileTos3bucket(fileName, file);
			file.delete();
			aStatus.setStatus("uploaded");
		} catch (Exception e) {
			e.printStackTrace();
			aStatus.setStatus("error");	
		}
		return aStatus;
	}

	/**
	 * Delete a single object from the S3 bucket
	 * @param fileUrl
	 * @return AmazonStatus
	 */
	public AmazonStatus deleteFileFromS3Bucket(String fileUrl) {
		AmazonStatus aStatus = new AmazonStatus();

		aStatus.setFile(fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
		aStatus.setStatus("deleted");

		try {
			s3client.deleteObject(bucketName, aStatus.getFile());
		} catch(Exception e) {
			e.printStackTrace();
			aStatus.setStatus("error");

		}
		return aStatus;
	}
}
