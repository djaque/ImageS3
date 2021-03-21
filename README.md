# ImageS3
Easy way to upload files to AWS S3 using spring boot

Based on:

https://medium.com/oril/uploading-files-to-aws-s3-bucket-using-spring-boot-483fcb6f8646

I made some improvements, like:
- Remove deprecated objects
- Create new output class called AmazonStatus
- Fix delete process

To work with you need to add your bucket info on application.yml:
```
amazonProperties:
  endpointUrl: https://s3.REGION.amazonaws.com
  accessKey: YourKey
  secretKey: YourSecret
  bucketName: bucketName
  region: REGION
```
 
 To save an image use:
```bash
 curl --location --request POST 'http://localhost:8080/storage/uploadFile' \
--form 'file=@"/PathToYourFile/computin.png"'
```
You should receive something like:
```json
{
    "file": "https://s3.REGION.amazonaws.com/bucket/1616366315537-computin.png",
    "status": "uploaded"
}
```
To delete an image
```bash
curl --location --request DELETE 'http://localhost:8080/storage/deleteFile' \
--form 'url="https://s3.REGION.amazonaws.com/bucket/1616366315537-computin.png"'
```
You should receive something like this:
```json
{
    "file": "1616366315537-computin.png",
    "status": "deleted"
}
```
For comments and feedback, send me an email to djaque@gmail.com
