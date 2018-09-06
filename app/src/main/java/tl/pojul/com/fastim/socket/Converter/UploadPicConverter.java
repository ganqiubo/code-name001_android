package tl.pojul.com.fastim.socket.Converter;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.UnsplashEntity;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.objectsocket.utils.FileClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UploadPicConverter {

    public List<UploadPic> converter(List<UploadPic> raws){
        for (int i = 0; i < raws.size(); i++) {
            UploadPic uploadPic = raws.get(i);
            if(uploadPic == null || uploadPic.getPicsStr() == null || uploadPic.getPicsStr().isEmpty()){
                continue;
            }
            String[] picStrs = uploadPic.getPicsStr().split(";");
            List<Pic> pics = new ArrayList<>();
            for (int j = 0; j < picStrs.length; j++) {
                String[] itemPic = picStrs[j].split("\\*");
                if(itemPic.length != 5){
                    continue;
                }
                Pic pic = new Pic();
                try{
                    pic.setId(Long.parseLong(itemPic[0]));
                }catch (Exception e){}
                try{
                    pic.setUploadPicId(Long.parseLong(itemPic[1]));
                }catch (Exception e){}
                pic.setUploadPicUrl(FileClassUtil.createStringFile(itemPic[2]));
                try{
                    pic.setWidth(Integer.parseInt(itemPic[3]));
                }catch (Exception e){}
                try{
                    pic.setHeight(Integer.parseInt(itemPic[4]));
                }catch (Exception e){}
                pics.add(pic);
            }
            uploadPic.setPics(pics);
        }
        return raws;
    }

    public List<ExtendUploadPic> converterFootStrp(List<ExtendUploadPic> raws, BDLocation bdLocation) {
        for (int i = 0; i < raws.size(); i++) {
            ExtendUploadPic uploadPic = raws.get(i);
            uploadPic.setGalleryType("脚步");
            if(uploadPic == null || uploadPic.getPicsStr() == null || uploadPic.getPicsStr().isEmpty()){
                continue;
            }
            if(uploadPic.getUploadPicType() == 2 && bdLocation != null){
                double distance = DistanceUtil.getDistance(new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude()),
                        new LatLng(uploadPic.getUploadPicLatitude(), uploadPic.getUploadPicLongitude()));
                uploadPic.setDistance(distance);
            }
            String[] picStrs = uploadPic.getPicsStr().split(";");
            List<Pic> pics = new ArrayList<>();
            for (int j = 0; j < picStrs.length; j++) {
                String[] itemPic = picStrs[j].split("\\*");
                if(itemPic.length != 5){
                    continue;
                }
                Pic pic = new Pic();
                try{
                    pic.setId(Long.parseLong(itemPic[0]));
                }catch (Exception e){}
                try{
                    pic.setUploadPicId(Long.parseLong(itemPic[1]));
                }catch (Exception e){}
                pic.setUploadPicUrl(FileClassUtil.createStringFile(itemPic[2]));
                try{
                    pic.setWidth(Integer.parseInt(itemPic[3]));
                }catch (Exception e){}
                try{
                    pic.setHeight(Integer.parseInt(itemPic[4]));
                }catch (Exception e){}
                pics.add(pic);
            }
            uploadPic.setPics(pics);
        }
        return raws;
    }

    public List<ExtendUploadPic> converterUnsplash(List<UnsplashEntity> raws) {
        if(raws == null){
            return null;
        }
        List<ExtendUploadPic> extendUploadPics = new ArrayList<>();
        for (int i = 0; i < raws.size(); i++) {
            UnsplashEntity unsplashEntity = raws.get(i);
            ExtendUploadPic extendUploadPic = new ExtendUploadPic();
            List<Pic> pics = new ArrayList<>();
            Pic pic = new Pic();
            pic.setWidth(unsplashEntity.getWidth());
            pic.setHeight(unsplashEntity.getHeight());
            pic.setUploadPicUrl(FileClassUtil.createStringFile(unsplashEntity.getUrls().getRegular()));
            pics.add(pic);
            extendUploadPic.setPics(pics);
            extendUploadPic.setBrosePic(unsplashEntity.getUrls().getSmall());
            extendUploadPic.setGalleryType("unsplash");
            extendUploadPic.setNickName(unsplashEntity.getUser().getUsername());
            extendUploadPic.setUploadPicCity(unsplashEntity.getUser().getLocation());
            extendUploadPic.setPhoto(unsplashEntity.getUser().getProfile_image().getLarge());
            extendUploadPic.setThirdUid(unsplashEntity.getId());
            extendUploadPic.setUploadPicTime(unsplashEntity.getCreated_at());
            extendUploadPics.add(extendUploadPic);
        }
        return extendUploadPics;
    }

    public List<ExtendUploadPic> converterPexelsPics(String str) {
        List<Integer> widths = new ArrayList<>();
        List<Integer> heights = new ArrayList<>();
        List<String> picUrls = new ArrayList<>();
        Pattern pattern = Pattern.compile("width=\\\\\".*?\\\\\"");
        Matcher matcher = pattern.matcher(str);
        while(matcher.find()){
            try{
                String widthStr = str.substring((matcher.start() + 8), (matcher.end() - 2));
                int width = Integer.parseInt(widthStr);
                widths.add(width);
            }catch(Exception e){}
        }

        pattern = Pattern.compile("height=\\\\\".*?\\\\\"");
        matcher = pattern.matcher(str);
        while(matcher.find()){
            try{
                String heightStr = str.substring((matcher.start() + 9), (matcher.end() - 2));
                int height = Integer.parseInt(heightStr);
                heights.add(height);
            }catch(Exception e){}
        }
        pattern = Pattern.compile("data-big-src=\\\\\".*?\\?auto");
        matcher = pattern.matcher(str);
        while(matcher.find()){
            try{
                String picUrl = str.substring((matcher.start() + 15), (matcher.end() - 5));
                picUrls.add(picUrl);
            }catch (Exception e){}
        }
        int widthSize = widths.size();
        int heightSize = heights.size();
        int urlSize = picUrls.size();
        int minSize = Math.min(widthSize, heightSize);
        minSize = Math.min(minSize, urlSize);
        List<ExtendUploadPic> uploadPics = new ArrayList<>();
        for (int i = 0; i < minSize; i++) {
            ExtendUploadPic uploadPic = new ExtendUploadPic();
            List<Pic> pics = new ArrayList<>();
            Pic pic = new Pic();
            pic.setWidth(widths.get(i));
            pic.setHeight(heights.get(i));
            pic.setUploadPicUrl(FileClassUtil.createStringFile(picUrls.get(i)));
            pics.add(pic);
            uploadPic.setPics(pics);
            uploadPic.setBrosePic((picUrls.get(i) + "?auto=compress&cs=tinysrgb&h=350"));
            uploadPic.setGalleryType("pexels");
            uploadPics.add(uploadPic);
        }
        return uploadPics;
    }
}
