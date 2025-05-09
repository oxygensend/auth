package com.oxygensend.auth.port.adapter.out.persistence;

public interface DataSourceObjectAdapter<D, T> {

    D toDomain(T t);
    T toDataSource(D d);

}
