package tl.pojul.com.fastim.dao;

import com.pojul.fastIM.entity.Pic;
import com.pojul.fastIM.entity.UploadPic;

import java.util.ArrayList;
import java.util.List;

import tl.pojul.com.fastim.dao.Util.DaoUtil;

public class UploadPicDao {

    public int saveUploadPic(UploadPic uploadPic, String userName, int uploadStatus){
        String sql = "insert into upload_pic(user_name, upload_pic_type, is_delete, uplod_pic_theme,"
                + "uplod_pic_label, upload_pic_country, upload_pic_city, upload_pic_district, "
                + "upload_pic_addr, upload_pic_longitude, upload_pic_latitude, upload_pic_altitude,"
                + "upload_pic_locnote, upload_pic_time, upload_pic_province, pic_time, pic_loc_type, "
                + "pic_loc_upload_status, upload_pic_locshow) values ("
                + "'" + userName + "', "
                + "'" + uploadPic.getUploadPicType() + "', "
                + "'" + uploadPic.getIsDelete() + "', "
                + "'" + uploadPic.getUplodPicTheme() + "', "
                + "'" + uploadPic.getUplodPicLabel() + "', "
                + "'" + uploadPic.getUploadPicCountry() + "', "
                + "'" + uploadPic.getUploadPicCity() + "', "
                + "'" + uploadPic.getUploadPicDistrict() + "', "
                + "'" + uploadPic.getUploadPicAddr() + "', "
                + "'" + Double.toString(uploadPic.getUploadPicLongitude()) + "', "
                + "'" + Double.toString(uploadPic.getUploadPicLatitude()) + "', "
                + "'" + Double.toString(uploadPic.getUploadPicAltitude()) + "', "
                + "'" + uploadPic.getUploadPicLocnote() + "', "
                + "'" + uploadPic.getUploadPicTime() + "', "
                + "'" + uploadPic.getUploadPicProvince() + "', "
                + "'" + uploadPic.getPicTime() + "', "
                + "'" + uploadPic.getPicLocType() + "', "
                + "'" + uploadStatus + "', "
                + "'" + uploadPic.getUploadPicLocshow() + "'"
                + ")";
        int lastInsertId = DaoUtil.executeUpdate(sql, true);
        if(uploadPic.getPics() != null && uploadPic.getPics().size() > 0){
            new PicDao().savePic(uploadPic.getPics(), lastInsertId);
        }
        return 0;
    }

    public int modifyUploadPic(UploadPic uploadPic, long uploadPicId, String userName, int uploadStatus){
        deleteUploadPic(uploadPicId);
        new PicDao().deleteUploadPic(uploadPicId);
        saveUploadPic(uploadPic, userName, uploadStatus);
        return 0;
    }

    public int deleteUploadPic(long uploadPicId){
        String sql = "delete from upload_pic where id = '" + uploadPicId + "'";
        DaoUtil.executeUpdate(sql, false);
        new PicDao().deleteUploadPic(uploadPicId);
        return 0;
    }

    public List<UploadPic> queryMyUploadPic(String userName, int uploadStatus){
        String sql = "select * from upload_pic where user_name = '" + userName + "' and pic_loc_upload_status = '" + uploadStatus + "'";
        List<UploadPic> uploadPics = DaoUtil.executeQuery(sql, UploadPic.class);
        if(uploadPics == null || uploadPics.size() <= 0){
            return new ArrayList<>();
        }
        for(int i =0; i< uploadPics.size(); i++){
            UploadPic uploadPic = uploadPics.get(i);
            List<Pic> pics = new PicDao().queryUploadPic(uploadPic.getId());
            uploadPic.setPics(pics);
        }
        return uploadPics;
    }
}
