package net.lzzy.practicesonline.models.view;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public enum QuestionType {
    /**
     * 题目类型
     */
    SINCLE_CHOICE("单项选择"),MULTI_CHOICE("不定项选择"),JUDGE("判断");

    private String name;

    QuestionType(String name){
        this .name =name;
    }

    @Override
    public String toString() {
        return name;
    }
    public static QuestionType getInstance(int ordinal){
        for (QuestionType type : QuestionType.values()){
            if (type.ordinal() == ordinal){
                return type;
            }
        }
        return null;
    }
}