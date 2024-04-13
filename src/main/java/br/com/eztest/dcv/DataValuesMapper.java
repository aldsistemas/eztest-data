package br.com.eztest.dcv;

import java.util.Map;

/**
 * Interface de mapeamento de dados. Prove um mecanismo de extracao de informacoes contidos nas entidades de dados.
 */
public interface DataValuesMapper {

    /**
     * Converte todos os dados contidos em um {@link DataUnit} para um mapa do tipo chave/valor.
     * 
     * @param data
     *            dados encapsulados
     * @return mapa dos dados em formato chave/valor.
     */
    Map<String, Object> getValues(DataUnit<Object> data);

    /**
     * Extrai um dado especifico de um {@link DataUnit}.
     * 
     * @param key
     *            identificacao do dado a ser extraido.
     * @param data
     *            data unit que contem os dados.
     * @return dado extraido.
     */
    Object getValue(String key, DataUnit<Object> data);

    /**
     * Verifica se um determinado objeto contem uma propriedade. Pode ser usado para validar se e possivel uma dada
     * navegacao do tipo attr1.attr2.attr3 em um dado objeto. Caso attr1 ou attr2 resultem e null, o metodo retorna false.
     * 
     * @param key
     *            nome da propriedade
     * @param data
     *            objeto de dados
     * @return true caso contenha a propriedade; false caso contrario.
     */
    boolean hasProperty(String key, DataUnit<Object> data);

    /**
     * Extrai o ID de um Objeto.
     * 
     * @param obj
     *            objeto.
     * @return ID do objeto.
     */
    Object getId(Object obj);
}
