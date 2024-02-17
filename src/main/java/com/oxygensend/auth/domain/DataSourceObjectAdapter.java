package com.oxygensend.auth.domain;

public interface DataSourceObjectAdapter<D, T> {

    D toDomain(T t);


    T toDataSource(D d);

}
