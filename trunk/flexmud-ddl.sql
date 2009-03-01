
    alter table context 
        drop constraint FK38B735AF327F008E;

    alter table context 
        drop constraint FK38B735AF5F7A0CC0;

    alter table context_command 
        drop constraint FKDCAF9CBBD5B02B7A;

    drop table context if exists;

    drop table context_command if exists;

    drop table context_group if exists;

    drop table object if exists;

    create table context (
        context_id bigint generated by default as identity (start with 1),
        name varchar(255),
        child_group_id bigint,
        parent_group_id bigint,
        primary key (context_id)
    );

    create table context_command (
        context_id bigint not null,
        command varchar(255) not null,
        primary key (context_id, command)
    );

    create table context_group (
        context_group_id bigint generated by default as identity (start with 1),
        primary key (context_group_id)
    );

    create table object (
        object_id bigint generated by default as identity (start with 1),
        name varchar(255),
        primary key (object_id)
    );

    alter table context 
        add constraint FK38B735AF327F008E 
        foreign key (child_group_id) 
        references context_group;

    alter table context 
        add constraint FK38B735AF5F7A0CC0 
        foreign key (parent_group_id) 
        references context_group;

    alter table context_command 
        add constraint FKDCAF9CBBD5B02B7A 
        foreign key (context_id) 
        references context;
