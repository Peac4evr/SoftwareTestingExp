package org.apache.commons.collections4.map;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class LRUMapTest {

    private LRUMap<String, String> map;

    // @Before 注解：在每个 @Test 方法执行前，都会自动运行一次这个方法
    // 作用是保证每个测试用例都有一个干净、独立的初始环境
    @Before
    public void setUp() {
        map = new LRUMap<>(2); // 初始化一个容量为 2 的 LRUMap
    }

    // 测试用例 1：测试基本的容量和放入功能
    @Test
    public void testPutAndSize() {
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");

        // 断言：期望值是 2，实际值是 map.size()
        assertEquals("Map的size应该为2", 2, map.size());
    }

    // 测试用例 2：测试 LRU 淘汰机制（专门用来捕获我们注入的缺陷）
    @Test
    public void testLRUGetMechanic() {
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");

        // 正常访问 Key1，在正确的逻辑下，Key1 应该被更新为最新（MRU）
        String val = map.get("Key1");
        assertEquals("应该成功获取Value1", "Value1", val);

        // 放入第三个元素，由于容量只有 2，必定触发淘汰机制
        // 如果逻辑正确，最老的 Key2 应该被淘汰，Key1 存活。
        map.put("Key3", "Value3");

        // 断言：由于我们注入了逻辑缺陷（Key1没被更新位置），下面这个断言一定会报错失败！
        assertNotNull("缺陷捕获：Key1 刚刚被访问过，不应该被淘汰！", map.get("Key1"));

        // 如果代码能侥幸走到这里，说明 Key1 没被淘汰，我们再检查 Key2 是否乖乖被淘汰了
        assertNull("缺陷捕获：Key2 是最久未访问的，本该被淘汰！", map.get("Key2"));
    }
}