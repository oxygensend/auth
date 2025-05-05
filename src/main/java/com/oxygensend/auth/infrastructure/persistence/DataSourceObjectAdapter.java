package com.oxygensend.auth.infrastructure.persistence;

interface DataSourceObjectAdapter<D, T> {

    D toDomain(T t);
    T toDataSource(D d);

}
