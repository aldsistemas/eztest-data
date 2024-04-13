package br.com.eztest.dcv;

import java.util.ArrayList;
import java.util.List;

import br.com.eztest.dcv.expectation.Expectation;
import br.com.eztest.dcv.expectation.ExpectationFactory;

/**
 * Classe principal responsavel por administrar contexto de dados. Sua principal funcao e registrar um contexto
 * {@link ContextManager#registerState()} em um momento, prover uma interface que permita informar as alteracoes
 * esperadas neste contexto {@link ContextManager#expect()} , e comparar o contexto obtido em um segundo momento com o
 * primeiro registrado {@link ContextManager#compareState()}.
 * 
 * @param <T>
 *            tido de dados manipulados.
 */
public class ContextManager<T> {

    private final List<ContextFactory<T>> factories;

    private final List<Context<T>>        preContexts;
    private final List<Context<T>>        postContexts;

    private final ExpectationsManager     expectations;

    private ExpectationFactory<T>         expectationFactory;

    /**
     * Controi um {@link ContextManager}.
     *
     * @param factory
     *            Factory de contextos.
     * @param testAsserter
     *            asserter usado para informar condicoes de comparacao ao framework de teste.
     */
    public ContextManager(final ContextFactory<T> factory, final Asserter testAsserter) {
        this.factories = new ArrayList<>();
        this.factories.add(factory);
        this.preContexts = new ArrayList<>();
        this.postContexts = new ArrayList<>();
        this.expectations = new ExpectationsManager(testAsserter);
    }

    /**
     * Controi um {@link ContextManager}.
     *
     * @param factoryList
     *            lista de factories de contextos.
     * @param testAsserter
     *            asserter usado para informar condicoes de comparacao ao framework de teste.
     */
    public ContextManager(final ContextFactory<T>[] factoryList, final Asserter testAsserter) {
        this.factories = new ArrayList<>();
        for (ContextFactory<T> x : factoryList) {
            this.factories.add(x);
        }
        this.preContexts = new ArrayList<>();
        this.postContexts = new ArrayList<>();
        this.expectations = new ExpectationsManager(testAsserter);
    }

    /**
     * Adiciona uma expectativa de mudanca ao contexto inicial registrado.
     * 
     * @param e
     *            expectativa.
     */
    public void add(final Expectation e) {
        this.expectations.addExpectation(e);
    }

    /**
     * Registra um segundo contexto de dados e compara com o primeiro contexto registrado. Caso haja alguma diferenca
     * nao esperada, uma excecao emitida pelo {@link Asserter} sera lancada.
     * 
     * @see #add(Expectation)
     * @see ContextManager#expect()
     */
    public void compareState() {
        for (final ContextFactory<T> fac : this.factories) {
            final Context<T> ctx = fac.createContext();
            ctx.load();
            this.postContexts.add(ctx);
        }
        if (this.expectationFactory != null) {
            this.expectationFactory.flush();
        }
        doCompare();
    }

    /**
     * Obtem uma interface utilitaria para inclusao de expectativas de mudanca de contexto.
     * 
     * @return fabrica de expectativas.
     */
    public ExpectationFactory<T> expect() {
        if (this.expectationFactory == null) {
            this.expectationFactory = new ExpectationFactory<>(this);
        }
        return this.expectationFactory;
    }

    /**
     * Registra um contexto inicial de dados. Os dados registrados servirao de referoncia para, em conjunto com as
     * expectativas inseridas, proverem uma conclusao sobre o as modificacao ocorridas no contexto.
     */
    public void registerState() {
        reset();
        for (final ContextFactory<T> fac : this.factories) {
            final Context<T> ctx = fac.createContext();
            ctx.load();
            this.preContexts.add(ctx);
        }
    }

    /**
     * Limpa os registros de dados e de expectativas.
     */
    public void reset() {
        this.preContexts.clear();
        this.postContexts.clear();
        this.expectations.clear();
    }

    /**
     * Metodo utilitario para comparacao de contextos.
     * 
     * @see #doCompare()
     */
    private void doCompare() {

        final List<DiffItem> diff = new ArrayList<>();
        for (int i = 0; i < this.factories.size(); i++) {
            final Context<T> pre = this.preContexts.get(i);
            final Context<T> post = this.postContexts.get(i);
            diff.addAll(pre.diff(post));

        }
        this.expectations.validate(diff);
    }
}
