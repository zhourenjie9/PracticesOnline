package net.lzzy.practicesonline.constants;

import net.lzzy.practicesonline.utils.AppUtils;

import java.net.URL;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */
public class ApiConstants {
    private static final String IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    private static final String PORT=AppUtils.loadServerSetting(AppUtils.getContext()).second;
    private static final String PROTOCOL="http://";

    /**
     * API地址
     */
    public static final String URL_API=PROTOCOL.concat(IP).concat(":").concat(PORT);

    /**
     * practices地址
     */
    private static final String ACTION_PRACTICES="/api/practices";

    public static final String URL_PRACTICES=URL_API.concat(ACTION_PRACTICES);

    /**
     * questions地址
     */
    private static final String ACTION_QUESTION="/api/pquestions?practiceid=";
    public static final String URL_QUESTIONS= URL_API.concat(ACTION_QUESTION);


    /**
     * json标签
     */
    public static final String JSON_PRACTICE_API_ID="Id";
    public static final String JSON_PRACTICE_NAME="Name";
    public static final String JSON_PRACTICE_OUTLINES="OutLines";
    public static final String JSON_PRACTICE_QUESTION_COUNT="QuestionCount";

    /**
     * Question的json标签
     */
    public static final String JSON_QUESTION_ANALYSIS="Analysis";
    public static final String JSON_QUESTION_CONTENT="Content";
    public static final String JSON_QUESTION_TYPE="QuestionType";
    public static final String JSON_QUESTION_OPTIONS="Options";
    public static final String JSON_QUESTION_ANSWER="Answers";


    public static final String JSON_OPTION_CONTENT="Content";
    public static final String JSON_OPTION_LABEL="Label";
    public static final String JSON_OPTION_API_ID="Id";
    public static final String JSON_ANSWER_OPTION_ID="OptionId";
}

