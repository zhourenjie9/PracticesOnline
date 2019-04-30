package net.lzzy.practicesonline.models;

import net.lzzy.practicesonline.constants.ApiConstants;
import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Jsonable;
import net.lzzy.sqllib.Sqlitable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class Practice extends BaseEntity implements Sqlitable, Jsonable {
    @Ignored
    static final String COL_NAME = "name";
    @Ignored
    static final String COL_OUTLINES = "outlines";
    @Ignored
    static final String COL_API_ID = "apiId";

    private String name;
    private int questionCout;
    private Date downloadDate;
    private  String outlines;
    private  boolean isDownloaded;
    private int apiId;

    public Practice(){
        downloadDate = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuestionCout() {
        return questionCout;
    }

    public void setQuestionCout(int questionCout) {
        this.questionCout = questionCout;
    }

    public Date getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }

    public String getOutlines() {
        return outlines;
    }

    public void setOutlines(String outlines) {
        this.outlines = outlines;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return null;
    }

    @Override
    public void fromJson(JSONObject json) throws JSONException {
            apiId= json.getInt(ApiConstants.JSON_PRACTICE_API_ID);
            name = json.getString(ApiConstants.JSON_PRACTICE_NAME);
            outlines = json.getString(ApiConstants.JSON_PRACTICE_OUTLINES);
            questionCout = json.getInt(ApiConstants.JSON_PRACTICE_QUESTION_COUNT);
            downloadDate = new Date();
    }
}
