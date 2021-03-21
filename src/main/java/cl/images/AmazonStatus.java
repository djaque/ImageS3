package cl.images;

/**
 * AmazonStatus is used as output from the client
 * reflect the status of the process
 * @author dany
 */
public class AmazonStatus {
	private String file;
	private String status;
	
	
	public AmazonStatus() {
		super();
	}
	
	public AmazonStatus(String file, String status) {
		super();
		this.file = file;
		this.status = status;
	}
	public String getFile() {
		return file;
	}
	public String getStatus() {
		return status;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
