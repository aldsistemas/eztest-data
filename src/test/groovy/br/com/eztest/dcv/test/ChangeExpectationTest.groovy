package br.com.eztest.dcv.test


import org.spockframework.util.Assert
import spock.lang.Specification
import spock.lang.Unroll

class ChangeExpectationTest extends Specification {
    private static HashMapDataMapper mapper = new HashMapDataMapper();

    private static br.com.eztest.dcv.DataUnit p1_1 = generate("type1", 1, ["id": 1, "name": "name1", "age": 10, "address": "address1"])
    private static br.com.eztest.dcv.DataUnit p1_2 = generate("type1", 1, ["id": 1, "name": "name1", "age": 10, "address": "address_"])
    private static br.com.eztest.dcv.DataUnit p1_3 = generate("type1", 1, ["id": 1, "name": "name1", "age": 11, "address": "address_"])

    private static br.com.eztest.dcv.DataUnit p2_1 = generate("type1", 2, ["id": 2, "name": "name2", "age": 20, "address": "address2"])
    private static br.com.eztest.dcv.DataUnit p2_2 = generate("type1", 2, ["id": 2, "name": "name2", "age": 20, "address": "address_"])
    private static br.com.eztest.dcv.DataUnit p2_3 = generate("type1", 2, ["id": 2, "name": "name2", "age": 21, "address": "address_"])

    private static br.com.eztest.dcv.DataUnit p3_1 = generate("type1", 3, ["id": 3, "name": "name3", "age": 30, "address": "address3"])

    def "teste sem mudanca"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_1]]
        def e = []
        then:
        runTest(data, e)
    }

    @Unroll
    def "teste com alteracao"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_2]]
        def e = [expectations]
        then:
        runTest(data, e)

        where:
        expectations << [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2) },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("name", "name2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_").eq("name", "name2") },
        ]
    }

    @Unroll
    def "teste de falha de alteracao com expectation nao atendida"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_2]]
        def e = [expectations]
        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        //Duas falhas: uma pela mudanca sem expectation, e outra pela expectation nao atendida
        asserter.counter == 2

        where:
        expectations << [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_").eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address2").eq("name", "name2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("address", "address2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("address", "address_").eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("address", "address2").eq("name", "name2") },
        ]
    }
    @Unroll
    def "teste de falha de alteracao sem expectation"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_2]]
        def e = []
        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        asserter.counter == 1
    }

    @Unroll
    def "teste com inclusao"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_1, p3_1]]
        def e = [expectations]
        then:
        runTest(data, e)

        where:
        expectations << [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3").eq("age", 30).eq("address", "address3") },
        ]
    }
    @Unroll
    def "teste de falha de inclusao com expectation nao atendida"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_1, p3_1]]
        def e = [expectations]

        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        asserter.counter == 2
        where:
        expectations << [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name_") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3").eq("age", 31).eq("address", "address3") },
        ]
    }
    @Unroll
    def "teste de falha de inclusao sem expectation"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_1, p3_1]]
        def e = []

        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        asserter.counter == 1
    }

    @Unroll
    def "teste de falha de remocao com expectation nao atendida"() {


        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1]]
        def e = [expectations]
        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        //Duas falhas: uma pela mudanca sem expectation, e outra pela expectation nao atendida
        asserter.counter == 2

        where:
        expectations << [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_").eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address2").eq("name", "name2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("address", "address2_") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("address", "address2").eq("name", "name") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 2).eq("address", "address_").eq("name", "name2") },
        ]
    }
    @Unroll
    def "teste de falha de remocao sem expectation"() {


        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1]]
        def e = []
        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        //Duas falhas: uma pela mudanca sem expectation, e outra pela expectation nao atendida
        asserter.counter == 1

    }
    @Unroll
    def "teste de falha de inclusao sem expectation"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p1_1, p2_1, p3_1]]
        def e = []

        br.com.eztest.dcv.Asserter asserter = new AsserterCounter()
        runTest(data, e, asserter)

        then:
        asserter.counter == 1
    }

    @Unroll
    def "teste com inclusao com alteracao"() {

        when: "teste executado"
        def data = [[p2_1], [p2_2, p3_1]]
        def e = expectations
        then:
        runTest(data, e)

        where:
        expectations << combineExpectations(
                [
                        { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1") },
                        { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3") },
                        { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3").eq("age", 30).eq("address", "address3") },
                ], [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2) },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("name", "name2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_").eq("name", "name2") }
        ]
        )
    }

    @Unroll
    def "teste com inclusao, alteracao e remocao"() {

        when: "teste executado"
        def data = [[p1_1, p2_1], [p2_2, p3_1]]
        def e = expectations
        then:
        runTest(data, e)

        where:
        expectations << combineExpectations(
                [
                        { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1") },
                        { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3") },
                        { br.com.eztest.dcv.ContextManager manager -> manager.expect().creation("type1").eq("name", "name3").eq("age", 30).eq("address", "address3") },
                ], [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2) },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("name", "name2") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().change("type1", 2).eq("address", "address_").eq("name", "name2") }
        ], [
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 1) },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 1).eq("address", "address1") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 1).eq("name", "name1") },
                { br.com.eztest.dcv.ContextManager manager -> manager.expect().removal("type1", 1).eq("address", "address1").eq("name", "name1") }
        ]
        )
    }

    def combineExpectations(List l1, List l2) {
        def l = []
        for (i1 in l1)
            for (i2 in l2)
                l.add([i1, i2])
        return l
    }

    def combineExpectations(List l1, List l2, List l3) {
        def l = []
        for (i1 in l1)
            for (i2 in l2)
                for (i3 in l3)
                    l.add([i1, i2, i3])
        return l
    }

    private boolean runTest(List<List<br.com.eztest.dcv.DataUnit>> contexts, List<Closure> expectations) {
        return runTest(contexts, expectations) {
            Assert.fail(it)
        }
    }

    private boolean runTest(List<List<br.com.eztest.dcv.DataUnit>> contexts, List<Closure> expectations, br.com.eztest.dcv.Asserter asserter) {

        LocalContextLoader loader = new LocalContextLoader(contexts);
        br.com.eztest.dcv.ContextFactory factory = new LocalContextFactory(loader);
        br.com.eztest.dcv.ContextManager contextManager = new br.com.eztest.dcv.ContextManager(factory, asserter);
        contextManager.registerState();
        expectations.forEach { it(contextManager) }
        contextManager.compareState();
        return true
    }

    private static class LocalContextFactory implements br.com.eztest.dcv.ContextFactory {

        private final LocalContextLoader loader;

        LocalContextFactory(LocalContextLoader loader) {
            this.loader = loader;
        }

        @Override
        br.com.eztest.dcv.Context createContext() {
            return new LocalContext(loader);
        }
    }

    private static class LocalContext extends br.com.eztest.dcv.Context {

        LocalContext(br.com.eztest.dcv.ContextLoader loader) {
            super(loader);
        }
    }

    private static class LocalContextLoader implements br.com.eztest.dcv.ContextLoader {

        private List<List<br.com.eztest.dcv.DataUnit>> contexts;
        private int nextIndex = 0;

        LocalContextLoader(List<List<br.com.eztest.dcv.DataUnit>> contexts) {
            this.contexts = contexts;
        }

        @Override
        List<br.com.eztest.dcv.DataUnit> loadData() {
            return this.contexts.get(nextIndex++);
        }
    }

    private static br.com.eztest.dcv.DataUnit generate(String type, Object id, Map<String, Object> data) {
        return new br.com.eztest.dcv.DataUnit(type, id, data, mapper);
    }

    private static class HashMapDataMapper implements br.com.eztest.dcv.DataValuesMapper {

        @Override
        Map<String, Object> getValues(br.com.eztest.dcv.DataUnit<Object> data) {
            Object localData = data.getData();
            return (Map<String, Object>) localData;
        }

        @Override
        Object getValue(String key, br.com.eztest.dcv.DataUnit<Object> data) {
            Object localData = data.getData();
            Map<String, Object> map = (Map<String, Object>) localData;
            return map.get(key);
        }

        @Override
        boolean hasProperty(String key, br.com.eztest.dcv.DataUnit<Object> data) {
            Object localData = data.getData();
            Map<String, Object> map = (Map<String, Object>) localData;
            return map.containsKey(key);
        }

        @Override
        Object getId(Object obj) {
            Map<String, Object> map = (Map<String, Object>) obj;
            return map.get("id");
        }
    }

    private static class AsserterCounter implements br.com.eztest.dcv.Asserter {

        int counter = 0;

        @Override
        void fail(String message) {
            counter++
        }
    }
}
