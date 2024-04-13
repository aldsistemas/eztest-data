package br.com.eztest.dcv;

import java.util.Map;

/**
 * Represetantacao de um conjunto de dados que pode ser identificado por um nome (tipo do conjunto de dados) e
 * enderecado por um ID. Representa, por exemplo, uma entidade um uma tupla do banco de dados. No caso de uma tupla, o
 * nome do DataUnit seria o nome da tabela, e o ID a chave primaria. No entanto, nao se limita a banco de dados. Pode
 * por exemplo representar entidades hibernate ou qualquer outro conjunto de dados organizados desta maneira, ou seja,
 * que possa ser enderecado quanto ao tipo e quanto a chave primaria.
 * 
 * @param <T>
 *            tipo de dado armazenado
 */
public class DataUnit<T> {

    private final String           dataType;
    private final Object           dataId;
    private T                      data;
    private final DataValuesMapper mapper;

    /**
     * Constroi um {@link DataUnit}.
     * 
     * @param type
     *            tipo do dado encapsulado.
     * @param id
     *            identificacao do dado encapsulado.
     * @param contentData
     *            dado armazenado
     * @param dataMapper
     *            mapper capaz de traduzir os dados contidos no objeto armazenado.
     */
    public DataUnit(final String type, final Object id, final T contentData, final DataValuesMapper dataMapper) {
        this.dataType = type;
        this.dataId = id;
        this.data = contentData;
        this.mapper = dataMapper;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataUnit<T> other = (DataUnit<T>) obj;
        if (this.dataId == null) {
            if (other.dataId != null) {
                return false;
            }
        } else if (!this.dataId.equals(other.dataId)) {
            return false;
        }
        if (this.dataType == null) {
            if (other.dataType != null) {
                return false;
            }
        } else if (!this.dataType.equals(other.dataType)) {
            return false;
        }
        return true;
    }

    /**
     * Busca pelo objeto armazenado.
     * 
     * @return objeto armazenado.
     */
    public T getData() {
        return this.data;
    }

    /**
     * Obtem o ID do conjunto de dados armazenado.
     * 
     * @return ID.
     */
    public Object getDataId() {
        return this.dataId;
    }

    /**
     * Tipo de dado aramazenado.
     * 
     * @return tipo de dado.
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * Obtem um atributo do objeto armazenado.
     * 
     * @param key
     *            nome do atributo.
     * @return valor do atributo. Null caso nao exista o atributo dentro do objeto.
     */
    public Object getValue(final String key) {
        return this.mapper.getValue(key, (DataUnit<Object>) this);
    }

    /**
     * Obtem o mapa de valores armazenados no objeto.
     * 
     * @return mapa de valores no formato nome do atributo/valor.
     */
    public Map<String, Object> getValues() {
        return this.mapper.getValues((DataUnit<Object>) this);
    }

    /**
     * Verifica se ha diferenca entre os dados locais com os dados de um outro objeto.
     * 
     * @param postValue
     *            objeto comparado
     * @return true caso haja diferenca entre os atributos do objeto local com o objeto passado como parametro
     */
    public boolean hasChanged(final DataUnit<T> postValue) {

        final Map<String, Object> valuesPre = getValues();
        final Map<String, Object> valuesPost = postValue.getValues();

        return !valuesPre.equals(valuesPost);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (this.dataId == null ? 0 : this.dataId.hashCode());
        result = 31 * result + (this.dataType == null ? 0 : this.dataType.hashCode());
        return result;
    }

    /**
     * Verifica se o objeto armazenado contem uma determinada propriedade (atributo).
     * 
     * @param key
     *            nome da propriedade.
     * @return true caso contenha a propriedade; false caso contrario.
     */
    public boolean hasProperty(final String key) {
        return this.mapper.hasProperty(key, (DataUnit<Object>) this);
    }

    /**
     * Registra um objeto de dados.
     * 
     * @param dataObj
     *            objeto de dados.
     */
    public void setData(final T dataObj) {
        this.data = dataObj;
    }

    @Override
    public String toString() {
        return "DataType: " + this.dataType + "\nDataID: " + this.dataId + "\nValues:\n" + getValues();
    }

}
