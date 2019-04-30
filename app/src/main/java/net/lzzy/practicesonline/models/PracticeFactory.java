package net.lzzy.practicesonline.models;

import android.net.NetworkInfo;

import net.lzzy.practicesonline.constants.DbConstants;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.sqllib.SqlRepository;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/17.
 * Description:
 */
public class PracticeFactory {
    private static final PracticeFactory OUR_INSTANCE = new PracticeFactory();
    private SqlRepository<Practice> repository;

    public static PracticeFactory getInstance() {
        return OUR_INSTANCE;
    }

    private PracticeFactory() {
        repository = new SqlRepository<>(AppUtils.getContext(), Practice.class, DbConstants.packager);
    }

    public List<Practice> get() {
        return repository.get();
    }

    public Practice getBYId(String id) {
        return repository.getById(id);
    }

    public List<Practice> search(String kw) {
        try {
            return repository.getByKeyword(kw
                    , new String[]{Practice.COL_NAME, Practice.COL_OUTLINES}, false);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return new ArrayList<>();


        }

    }

    public boolean add(Practice practice) {
        if (ispracticeInDb(practice)) {
            return false;
        }
        repository.insert(practice);
        return true;

    }

    private boolean ispracticeInDb(Practice practice) {
        try {
            return repository.getByKeyword(String.valueOf(practice.getApiId()),
                    new String[]{practice.COL_API_ID}, true).size() > 0;
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return true;

        }
    }

    public UUID getPracticeId(int apiId) {
        try {
            List<Practice> practices = repository.getByKeyword(String.valueOf(apiId),
                    new String[]{Practice.COL_API_ID}, true);
            if (practices.size() > 0) {
                return practices.get(0).getId();
            }
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();

        }
        return null;
    }

    public void setPracticeDown(String id) {
        Practice practice = getBYId(id);
        if (practice != null) {
            practice.setDownloaded(true);
            repository.update(practice);
        }
    }

    public void saveQuestions(List<Question> questions, UUID practiceId) {
        for (Question q : questions) {
            QuestionFactory.getInstance().insert(q);
        }
        setPracticeDown(practiceId.toString());
    }

    public boolean deletePracticeAndRelated(Practice practice) {
        try {
            List<String> sqlActions = new ArrayList<>();
            sqlActions.add(repository.getDeleteString(practice));
            QuestionFactory factory = QuestionFactory.getInstance();
            List<Question> questions = factory.getBypractice(practice.getId().toString());
            if (questions.size() > 0) {
                for (Question q : questions) {
                    sqlActions.addAll(factory.getDeleteSting(q));
                }
            }
            repository.exeSqls(sqlActions);
            if (!ispracticeInDb(practice)) {
                //todo:清除Cookies
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

