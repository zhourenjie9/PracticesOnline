package net.lzzy.practicesonline.models.view;

import androidx.annotation.IdRes;

import net.lzzy.practicesonline.constants.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/8.
 * Description:
 */
public class PracticeResult {
    private List<QuestionResult> results;
    private int id;
    private String info;
    public static final String SPLITTER = ",";

    public PracticeResult(List<QuestionResult> results,int apiId,String info){
        this.id=apiId;
        this.info=info;
        this.results=results;
    }

    public List<QuestionResult> getResults(){
        return results;
    }

    public int getId(){
        return id;
    }

    public String getInfo(){
        return info;
    }

    private double getRatio(){
        int rightCount=0;
        for (QuestionResult result:results){
            if (result.isRight()){
                rightCount++;
            }
        }
        return rightCount*1.0/results.size();
    }
    private String getWrongOrders(){
        int i=0;
        String ids="";
        for (QuestionResult result:results){
            i++;
            if (!result.isRight()){
                ids=ids.concat(i+SPLITTER);
            }
        }
        if (ids.endsWith(SPLITTER)){
            ids=ids.substring(0,ids.length()-1);
        }
        return ids;

    }

    public JSONObject toJson(){
        JSONObject jsonObject=new JSONObject();
        //put方法赋值
        try {
            jsonObject.put(ApiConstants.JSON_RESULT_API_ID,id);
            jsonObject.put(ApiConstants.JSON_RESULT_PERSON_INFO,info);
            jsonObject.put(ApiConstants.JSON_RESULT_SCORE_RATIO,
                    new DecimalFormat("#.00").format(getRatio()));
            jsonObject.put(ApiConstants.JSON_RESULT_WRONG_IDS,getWrongOrders());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }




}
