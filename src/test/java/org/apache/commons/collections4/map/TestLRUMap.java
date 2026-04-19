package org.apache.commons.collections4.map;

public class TestLRUMap {
    public static void main(String[] args) {
        System.out.println("====== 开始执行 LRUMap 传统方式单元测试 ======");

        // 1. 初始化：创建一个最大容量只能装 2 个元素的 LRUMap
        LRUMap<String, String> map = new LRUMap<>(2);

        // --- 测试方法 1：put() 与 size() ---
        map.put("Key1", "Value1");
        map.put("Key2", "Value2");

        if (map.size() != 2) {
            System.err.println("错误：Map 的 size 记录异常！预期为 2，实际为 " + map.size());
        }

        // --- 测试方法 2：get() ---

        // 目前满载，内部的老化顺序是： Key1 (最老/LRU) -> Key2 (最新/MRU)
        // 现在访问一下 Key1，希望它变成“最新”被保护起来
        String val = map.get("Key1");

        if (val == null || !val.equals("Value1")) {
            System.err.println("错误：无法正常获取 Key1 的值！");
        }

        /*
         * 正常情况下：由于 get("Key1") 被调用，Key1 应该被移动到最新位置。
         * 此时的老化顺序应该是：Key2 (最老/LRU) -> Key1 (最新/MRU)。
         * 缺陷情况下：因为我们注入了 if(!updateToMRU) 的反转逻辑，Key1 并没有被更新位置！
         * 它依然排在“最老”的位置，随时面临被淘汰的风险。
         */

        // 放入第 3 个元素，触发容量上限，迫使 LRUMap 踢掉“最老”的元素
        map.put("Key3", "Value3");

        // 验证缺陷是否导致了错误的淘汰行为
        if (map.get("Key1") == null) {
            System.err.println("测试失败 (捕获到缺陷)！Key1 刚被访问过，却被错误地淘汰");
        } else {
            System.out.println("Key1 存活，符合预期逻辑。");
        }

        if (map.get("Key2") != null) {
            System.err.println("测试失败 (捕获到缺陷)！Key2 本该被淘汰却存活");
        }

        System.out.println("====== 传统测试执行完毕 ======");
    }
}