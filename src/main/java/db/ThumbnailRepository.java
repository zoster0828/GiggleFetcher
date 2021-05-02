package db;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class ThumbnailRepository {
    Logger logger = LogManager.getLogger("ThumbnailRepository");
    public String save(String path){
        String access ="AKIAJKHDJATV57NIM75Q";
        String secret ="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";
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(access, secret);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Can't upload to S3 file path={}",path);
        }
        String bucketName = "gigglethumbnail2";
        String keyName = getDate()+"/"+System.currentTimeMillis()+""+new Random().nextInt(1000)+".jpg";


        AmazonS3 s3client = new AmazonS3Client(awsCreds);
        s3client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        File file = new File(path);
        try {
            s3client.putObject(new PutObjectRequest(bucketName, keyName, file));
        } catch (AmazonServiceException ase) {
            logger.error("Can't upload to S3 file:    " + ase.getMessage());
        } catch (AmazonClientException ace) {
        } finally {
            file.delete();
        }

        return bucketName+"/"+keyName;
    }

    private String getDate(){
        Calendar cal = Calendar.getInstance();
        Date dt = new Date();
        cal.setTime(dt);
        return cal.get(Calendar.YEAR)+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DATE);
    }

}
