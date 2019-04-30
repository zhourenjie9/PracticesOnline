package net.lzzy.practicesonline.network;

import net.lzzy.practicesonline.models.Practice;

import org.json.JSONException;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lzzy_gxy on 2019/4/22.
 * Description:
 */
public class PracticeServiceTest {

    @Test
    public void testGetPracticesFromServer()  throws IOException {
        String actual = PracticeService.getPracticesFromServer();
        assertTrue(actual.contains("测试阶段的划分"));

    }

    @Test
    public void testgetPractices() throws IOException, IllegalAccessException, JSONException, InstantiationException {
        String json=PracticeService.getPracticesFromServer();
        List<Practice> practices = PracticeService.getPractices(json);
        assertEquals(5,practices.size());
        assertEquals(33,practices.get(0).getApiId());
        assertEquals("测试阶段的划分",practices.get(0).getName());
        assertEquals(9,practices.get(0).getQuestionCout());
        assertTrue(practices.get(0).getOutlines().contains("单元测试"));
    }
}