package service;

import db.ThumbnailRepository;
import infrastructure.GiggleHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ThumbnailDomain {
    Logger logger = LogManager.getLogger("ThumbnailDomain");
    ThumbnailRepository thumbnailRepository;
    public ThumbnailDomain(){
        thumbnailRepository = new ThumbnailRepository();
    }

    public String getS3ThumbmailUrl(String url) {
        String imagePath = downloadImage(url);
        if(imagePath.equals("")){return "";}

        String croppedImagePath = "";

        if(!imagePath.equals("")) {
            croppedImagePath = cropImage(imagePath);
        }

        if(croppedImagePath.isEmpty()){
            return "";
        }
        String s3ImagePath = uploadToS3(croppedImagePath);

        return "https://s3.ap-northeast-2.amazonaws.com/"+s3ImagePath;
    }

    private String uploadToS3(String croppedImagePath) {
        String s3Url = thumbnailRepository.save(croppedImagePath);
        return s3Url;
    }

    private String cropImage(String imagePath) {
        int x = 0;
        int y = 0;
        int w = 978;
        int h = 465;
        File outputfile=null;
        try {
            File file = new File(imagePath);
            if(!file.exists()){
                logger.error("File does not exists");
                return "";
            }
            BufferedImage originalImage = ImageIO.read(file);

            BufferedImage subImage;
            float o_width = originalImage.getWidth();
            float o_height = originalImage.getHeight();
            float o_ratio = o_width/o_height;
            if(o_ratio >= 1 && o_ratio <= 3)//비율이 카드와 유사하면
            {
                originalImage = noPink(originalImage,978,465);
            }
            int type = BufferedImage.TYPE_INT_RGB;
            if (originalImage.getWidth() > w && originalImage.getHeight() > h){
                subImage = originalImage.getSubimage(((originalImage.getWidth()-w)/2), y, w, h);
            }
            else if (originalImage.getWidth() > w
                    && originalImage.getHeight() < h){
                subImage = originalImage.getSubimage(((originalImage.getWidth()-w)/2), y, w,
                        originalImage.getHeight());
            }
            else if (originalImage.getWidth() < w
                    && originalImage.getHeight() > h){
                subImage = originalImage.getSubimage(x,y,
                        originalImage.getWidth(), h);
            }
            else{
                subImage = originalImage;
            }
            //subImage = noPink(subImage,subImage.getWidth(),subImage.getHeight());

            outputfile = new File("src/main/resources/thumbnail/thumb_"+System.currentTimeMillis()+""+new Random().nextInt(1000)+".jpg");
            ImageIO.write(subImage, "jpg", outputfile);
            file.delete();
            return outputfile.getPath();
        } catch (IOException e) {
            logger.error("Can't crop image : " +e.toString());
            if(outputfile != null) {
                outputfile.delete();
            }
            return "";
        } catch (Exception e){
//            logger.error("Can't crop image : " +e.toString());
            if(outputfile!= null) {
                outputfile.delete();
            }
            return "";
        }
    }

    // 핑크색 이미지 다시 그리는 함수
    private static BufferedImage noPink(BufferedImage img, int width, int height) {
        BufferedImage originalImage;
        originalImage = img;
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    public String downloadImage(String url){
        return GiggleHttpClient.find(url);
    }


}
