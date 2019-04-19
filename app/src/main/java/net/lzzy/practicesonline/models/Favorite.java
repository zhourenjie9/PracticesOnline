package net.lzzy.practicesonline.models;

import net.lzzy.sqllib.Ignored;
import net.lzzy.sqllib.Sqlitable;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class Favorite extends BaseEntity implements Sqlitable {
    @Ignored
    public static final String COL_OUESTION_ID ="questionId";
    private UUID questionId;

    public UUID getQuestionId() {
        return questionId;
    }

    public void setQuestionId(UUID questionId) {
        this.questionId = questionId;
    }

    @Override
    public boolean needUpdate() {
        return false;
    }
}
