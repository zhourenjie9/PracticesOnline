package net.lzzy.practicesonline.models;

import net.lzzy.sqllib.Sqlitable;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class Option extends BaseEntity implements Sqlitable {
    public static final String COL_PRACTICE_ID = "questionId";
    private  String content;
    private String label;
    private UUID questionid;
    private boolean isAnswer;
    private  int aniInt;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public UUID getQuestionid() {
        return questionid;
    }

    public void setQuestionid(UUID questionid) {
        this.questionid = questionid;
    }

    public boolean isAnswer() {
        return isAnswer;
    }

    public void setAnswer(boolean answer) {
        isAnswer = answer;
    }

    public int getAniInt() {
        return aniInt;
    }

    public void setAniInt(int aniInt) {
        this.aniInt = aniInt;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
