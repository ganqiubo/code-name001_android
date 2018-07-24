package tl.pojul.com.fastim.dao;

import com.pojul.fastIM.entity.Pic;
import java.util.List;

import tl.pojul.com.fastim.dao.Util.DaoUtil;

public class PicDao {

    public int savePic(List<Pic> pics, long uploadPicId){
        if(pics == null){
            return 0;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("insert into pic(upload_pic_id, upload_pic_url, is_delete) values ");
        for (int i = 0; i < pics.size(); i++) {
            if(i > 0) {
                sb.append(",");
            }
            Pic pic = pics.get(i);
            String value = "("
                    + "'" + uploadPicId + "', "
                    + "'" + pic.getUploadPicUrl().getFilePath() + "', "
                    + "'" + pic.getIsDelete() + "'"
                    + ")";
            sb.append(value);
        }
        String sql = sb.toString();
        DaoUtil.executeUpdate(sql, false);
        return 0;
    }

    public int deleteUploadPic(long uploadPicId){
        String sql = "delete from pic where upload_pic_id = '" + uploadPicId + "'";
        DaoUtil.executeUpdate(sql, false);
        return 0;
    }

    public List<Pic> queryUploadPic(long uploadPicId){
        String sql = "select * from pic where upload_pic_id = '" + uploadPicId + "'";
        return DaoUtil.executeQuery(sql, Pic.class);
    }

}
