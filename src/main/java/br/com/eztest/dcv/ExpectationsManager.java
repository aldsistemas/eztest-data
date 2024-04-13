package br.com.eztest.dcv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.eztest.dcv.expectation.Expectation;

public class ExpectationsManager {

    private static final Comparator<ExpectationMatcher> MATCHER_COMPARATOR = new Comparator<ExpectationMatcher>() {

                                                                               @Override
                                                                               public int compare(final ExpectationMatcher o1,
                                                                                       final ExpectationMatcher o2) {
                                                                                   return o1.getMatched().size() - o2.getMatched().size();
                                                                               }
                                                                           };

    private final List<Expectation>                     expectations       = new ArrayList<Expectation>();
    private final Asserter                              asserter;

    public ExpectationsManager(final Asserter asser) {
        this.asserter = asser;
    }

    public void addExpectation(final Expectation e) {
        for (Expectation current : this.expectations) {
            if (current.canMergeWith(e)) {
                current.mergeWith(e);
                return;
            }
        }
        this.expectations.add(e);
    }

    public void validate(final List<DiffItem> diff) {

        final List<ExpectationMatcher> ems = new ArrayList<ExpectationMatcher>();

        final List<ExpectationMatcher> emsAdd = new ArrayList<ExpectationMatcher>();
        final List<ExpectationMatcher> emsRemove = new ArrayList<ExpectationMatcher>();
        final List<ExpectationMatcher> emsChange = new ArrayList<ExpectationMatcher>();

        for (final Expectation e : this.expectations) {
            final ExpectationMatcher em = new ExpectationMatcher(e);
            ems.add(em);
            switch (e.getType()) {
                case ADD:
                    emsAdd.add(em);
                    break;
                case REMOVE:
                    emsRemove.add(em);
                    break;
                case CHANGE:
                    emsChange.add(em);
                    break;
                default:
                    throw new IllegalStateException("Unknown Expectation type: " + e.getType());
            }
        }
        for (final DiffItem item : diff) {
            boolean verified = false;
            for (final ExpectationMatcher em : ems) {
                em.resetExpectation();
                if (em.addIfMatch(item)) {
                    verified = true;
                }
            }
            if (!verified) {
                // quer dizer que o item nao se encaixou em nenhuma expectation em estado de reset
                this.asserter.fail("Data changed unexpectedly [" + item + "]");
            }
        }

        validateExpectationGroup(emsAdd);
        validateExpectationGroup(emsRemove);
        validateExpectationGroup(emsChange);
    }

    public void clear() {
        this.expectations.clear();
    }

    private void validateExpectationGroup(final List<ExpectationMatcher> em) {

        final ExpectationMatcher[] needProcess = new ExpectationMatcher[em.size()];
        for (int i = 0; i < em.size(); i++) {
            needProcess[i] = em.get(i);
        }
        // Flag que indica que uma expectation se satisfez com todos os DiffItens que a atenderam
        boolean needReview = true;

        while (needReview) {
            needReview = false;
            for (int i = 0; i < needProcess.length; i++) {
                final ExpectationMatcher m = needProcess[i];
                if (m == null) {
                    continue;
                }
                if (!m.trySatisfyWithSome()) {
                    // quer dizer que nao ha um subconjunto minimo de diffs que satisfazem a expectativa
                    this.asserter.fail("Expectation not satisfied [" + m.getExpectation() + "]");
                    return;
                }
                if (m.trySatisfyWithAll() == null) {
                    // todos sao necessarios para satisfazer a expectativa. Logo, os diffs que atendem a expectativa nao
                    // podem ser reagrupados. Todos os DiffItens da expectations sao removidos da lista de Matched das
                    // outras expectations e o processo recomeca.

                    // Marca para reinicio da avaliacao de todas as expectations
                    needReview = true;

                    // Remove a expectation da lista a ser processada
                    needProcess[i] = null;

                    // Remove os itens usado pela expectation de todas as outras expectations que podem ter dado Match
                    // nesses itens. Como a expectation atual foi removida na linha anterior, a verificacao se i == j
                    // nao precisa ser feita.
                    final List<DiffItem> matched = m.getMatched();
                    for (int j = 0; j < needProcess.length; j++) {
                        final ExpectationMatcher rem = needProcess[j];
                        if (rem != null) {
                            rem.removeAll(matched);
                        }
                    }
                    break;
                }
            }
        }

        final List<ExpectationMatcher> needCombination = new ArrayList<ExpectationMatcher>();
        for (ExpectationMatcher e : needProcess) {
            if (e != null) {
                needCombination.add(e);
            }
        }

        if (needCombination.isEmpty()) {
            return;
        }

        // Ordena de forma crescente as Expectations quanto ao numero de itens que as atendem por uma questao de
        // probabilidade quando chegar a etapa de tentativa de todas as combinacoes para se obter sucesso de
        // relacionamento com as expectativas. Parte-se do principio que uma Expectation com menos itens relacionados
        // deve ter prioridade de "casamento" sobre esses itens, o que reduziria o numero de tentativas.
        Collections.sort(needCombination, MATCHER_COMPARATOR);

        // A partir daki se tentara todas as combinacoes de DiffItens com Expectations para se tentar em uma
        // configuracao que se constitua em sucesso de atendimento as expectativas.
        final List<DiffItem> itensCombinados = new ArrayList<DiffItem>();
        final List<Expectation> expectationsCombinadas = new ArrayList<Expectation>();
        for (final ExpectationMatcher m : needCombination) {
            expectationsCombinadas.add(m.getExpectation());
            for (final DiffItem item : m.getMatched()) {
                // A logica a partir daki visa garantir que nenhum DiffItem repetido sera inserido na lista
                // itensCombinados, independente da implementacao de equals() e hashCode(). A verificacao eh feita pela
                // comparacao "=="
                boolean contains = false;
                for (final DiffItem i : itensCombinados) {
                    if (item == i) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    itensCombinados.add(item);
                }
            }
        }

        final int expectationSize = expectationsCombinadas.size();
        int permutationSize = expectationSize;
        if (permutationSize > 5) {
            permutationSize = 5;
        }

        // TODO: verificar possibilidade de se tambem considerar a permutacao de posicoes dos diff itens.
        final int[][] positions = PermutationUtil.getPermutationIndexes(permutationSize);
        for (int[] permutacao : positions) {
            final List<Expectation> orderedExpecations = new ArrayList<Expectation>();
            for (int position : permutacao) {
                final Expectation expec = expectationsCombinadas.get(position);
                expec.reset();
                orderedExpecations.add(expec);
            }
            for (int i = permutationSize; i < expectationSize; i++) {
                final Expectation expec = expectationsCombinadas.get(i);
                expec.reset();
                orderedExpecations.add(expec);
            }

            boolean abort = false;
            for (final DiffItem di : itensCombinados) {
                boolean verified = false;
                for (final Expectation e : orderedExpecations) {
                    if (e.register(di)) {
                        verified = true;
                        break;
                    }
                }
                if (!verified) {
                    // quer dizer que esta permutacao nao funcionou.
                    abort = true;
                    break;
                }
            }
            if (!abort) {
                for (final Expectation e : orderedExpecations) {
                    if (!e.isSatisfied()) {
                        abort = true;
                        break;
                    }
                }
            }
            if (abort) {
                continue;
            }
            // Se chegou aqui, a permutacao foi bem sucedida no arranjo dos DiffItens
            return;
        }

        final StringBuilder error = new StringBuilder("Diff data couldn't be matched with expectations.\n");
        error.append("============= Data: ==================\n");
        for (DiffItem i : itensCombinados) {
            error.append(i + "\n");
        }
        error.append("\n=========== Expectations ============\n");
        for (Expectation e : expectationsCombinadas) {
            error.append(e + "\n");
        }
        this.asserter.fail(error.toString());

    }

    private static class ExpectationMatcher {

        private final Expectation    expectation;
        private final List<DiffItem> matched = new ArrayList<DiffItem>();

        public ExpectationMatcher(final Expectation e) {
            this.expectation = e;
        }

        public void removeAll(List<DiffItem> matched2) {
            DiffItem[] temp = new DiffItem[matched.size()];
            temp = matched.toArray(temp);
            matched.clear();
            for (DiffItem d1 : matched2) {
                for (int i = 0; i < temp.length; i++) {
                    if (d1 == temp[i]) {
                        temp[i] = null;
                    }
                }
            }
            for (DiffItem d : temp) {
                if (d != null) {
                    matched.add(d);
                }
            }
        }

        public boolean addIfMatch(final DiffItem item) {
            if (this.expectation.register(item)) {
                this.matched.add(item);
                return true;
            }
            return false;
        }

        public void resetExpectation() {
            this.expectation.reset();
        }

        public DiffItem trySatisfyWithAll() {
            this.expectation.reset();
            for (final DiffItem item : this.matched) {
                if (!this.expectation.register(item)) {
                    return item;
                }
            }
            return null;
        }

        public boolean trySatisfyWithSome() {
            this.expectation.reset();
            for (final DiffItem item : this.matched) {
                this.expectation.register(item);
            }
            return this.expectation.isSatisfied();
        }

        protected Expectation getExpectation() {
            return this.expectation;
        }

        protected List<DiffItem> getMatched() {
            return this.matched;
        }
    }
}
