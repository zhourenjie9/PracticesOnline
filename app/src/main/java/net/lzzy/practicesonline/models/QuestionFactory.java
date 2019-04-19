package net.lzzy.practicesonline.models;

import android.text.TextUtils;

import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/4/18.
 * Description:
 */
public class QuestionFactory {
    private static final QuestionFactory OUR_INSTANCE = new QuestionFactory();
    private SqlRepository<Question>repository;
    private SqlRepository<Option>optionRepository;

    public static QuestionFactory getInstance() {
        return OUR_INSTANCE;
    }

    private QuestionFactory() {
        repository = new SqlRepository<>(AppUtils.getContext(),Question.class, DbConstants.packager);
        optionRepository = new SqlRepository<>(AppUtils.getContext(),Option.class,DbConstants.packager);

    }

    private void completeQuestion(Question question) throws InstantiationException, IllegalAccessException {
        List<Option> options =optionRepository.getByKeyword(question.getId().toString(),
                new String[]{Option.COL_PRACTICE_ID},true);
        question.setOptions(options);
        question.setDpType(question.getDpType());

    }


    public Question getById(String questionId) throws InstantiationException {
       try {
           Question question = repository.getById(questionId);
           completeQuestion(question);
           return null;
       }catch (IllegalAccessException e) {
           e.printStackTrace();
       return null;
       }

    }

    public List<Question> getBypractice(String practiceId) {

            try {
                List<Question> questions = repository.getByKeyword(practiceId,
                        new String[]{Question.COL_PRACTICE_ID}, true);
                for (Question question : questions) {
                    completeQuestion(question);
                }
                return questions;
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            return new ArrayList<>();


    }

    public void insert(Question question){
        String q = repository.getInsertString(question);
        List<String> sqlActions = new ArrayList<>();
        for (Option option : question.getOptions()){
            sqlActions.add(optionRepository.getDeleteString(option));

        }
        sqlActions.add(q);
        repository.exeSqls(sqlActions);
    }
    public List<String> getDeleteSting(Question question){
        List<String>sqlActions = new ArrayList<>();
        sqlActions.add(repository.getDeleteString(question));
        for (Option option : question.getOptions()){
            sqlActions.add(optionRepository.getDeleteString(option));

        }
        String f = FavoniteFactory.getInstance().getDeleteString(question.getId().toString());
        if (!TextUtils.isEmpty(f)){
            sqlActions.add(f);
        }
        return sqlActions;

    }
}
