package com.oxygensend.auth.infrastructure.persistence;

public interface DataSourceObjectAdapter<D, T> {

    D toDomain(T t);
    T toDataSource(D d);

}
