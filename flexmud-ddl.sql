
    alter table account 
        drop constraint FKB9D38A2D508CA7D2;

    alter table account_character 
        drop constraint FK471F2B97BA1A5C8A;

    alter table account_character 
        drop constraint FK471F2B979531FB37;

    alter table account_role_context 
        drop constraint FK29E017B8D7CEC12A;

    alter table account_role_context 
        drop constraint FK29E017B8508CA7D2;

    alter table account_role_context_command 
        drop constraint FKC63E65C48906973A;

    alter table account_role_context_command 
        drop constraint FKC63E65C4508CA7D2;

    alter table character_role_context 
        drop constraint FK3B98603CBA1A5C8A;

    alter table character_role_context 
        drop constraint FK3B98603CD7CEC12A;

    alter table character_role_context_command 
        drop constraint FK5277EA488906973A;

    alter table character_role_context_command 
        drop constraint FK5277EA48BA1A5C8A;

    alter table context 
        drop constraint FK38B735AF327F008E;

    alter table context 
        drop constraint FK38B735AF5F7A0CC0;

    alter table context_command 
        drop constraint FKDCAF9CBBD5B02B7A;

    alter table context_command_alias 
        drop constraint FKF55DDDCC9FA1759B;

    alter table context_command_parameter 
        drop constraint FK49AADD259FA1759B;

    drop table account if exists;

    drop table account_character if exists;

    drop table account_role if exists;

    drop table account_role_context if exists;

    drop table account_role_context_command if exists;

    drop table character_role if exists;

    drop table character_role_context if exists;

    drop table character_role_context_command if exists;

    drop table context if exists;

    drop table context_command if exists;

    drop table context_command_alias if exists;

    drop table context_command_parameter if exists;

    drop table context_group if exists;

    drop table message if exists;

    drop table object if exists;

    create table account (
        id bigint generated by default as identity (start with 1),
        login varchar(255),
        password varchar(255),
        account_role_id bigint not null,
        primary key (id)
    );

    create table account_character (
        id bigint generated by default as identity (start with 1),
        name varchar(255),
        account_id bigint not null,
        character_role_id bigint not null,
        primary key (id)
    );

    create table account_role (
        id bigint generated by default as identity (start with 1),
        name varchar(255) not null,
        primary key (id)
    );

    create table account_role_context (
        account_role_id bigint not null,
        restrictedContexts_id bigint not null,
        primary key (account_role_id, restrictedContexts_id)
    );

    create table account_role_context_command (
        account_role_id bigint not null,
        restrictedContextCommands_id bigint not null,
        primary key (account_role_id, restrictedContextCommands_id)
    );

    create table character_role (
        id bigint generated by default as identity (start with 1),
        name varchar(255) not null,
        primary key (id)
    );

    create table character_role_context (
        character_role_id bigint not null,
        restrictedContexts_id bigint not null,
        primary key (character_role_id, restrictedContexts_id)
    );

    create table character_role_context_command (
        character_role_id bigint not null,
        restrictedContextCommands_id bigint not null,
        primary key (character_role_id, restrictedContextCommands_id)
    );

    create table context (
        id bigint generated by default as identity (start with 1),
        entry_message varchar(255),
        max_entries integer,
        max_entries_exceeded_message varchar(255),
        name varchar(255),
        prompt varchar(255),
        child_group_id bigint,
        parent_group_id bigint,
        primary key (id)
    );

    create table context_command (
        id bigint generated by default as identity (start with 1),
        command_class varchar(255) not null,
        flag varchar(255),
        description varchar(255),
        name varchar(255),
        sequence integer,
        context_id bigint not null,
        primary key (id)
    );

    create table context_command_alias (
        context_command_alias_id integer generated by default as identity (start with 1),
        is_accelerator bit not null,
        alias varchar(255) not null,
        is_bullet bit not null,
        context_command_id bigint not null,
        primary key (context_command_alias_id)
    );

    create table context_command_parameter (
        id integer generated by default as identity (start with 1),
        sequence integer not null,
        param_value varchar(255) not null,
        context_command_id bigint not null,
        primary key (id)
    );

    create table context_group (
        id bigint generated by default as identity (start with 1),
        primary key (id)
    );

    create table message (
        id bigint generated by default as identity (start with 1),
        message varchar(255),
        name varchar(255),
        primary key (id)
    );

    create table object (
        object_id bigint generated by default as identity (start with 1),
        name varchar(255),
        primary key (object_id)
    );

    alter table account 
        add constraint FKB9D38A2D508CA7D2 
        foreign key (account_role_id) 
        references account_role;

    alter table account_character 
        add constraint FK471F2B97BA1A5C8A 
        foreign key (character_role_id) 
        references character_role;

    alter table account_character 
        add constraint FK471F2B979531FB37 
        foreign key (account_id) 
        references account;

    alter table account_role_context 
        add constraint FK29E017B8D7CEC12A 
        foreign key (restrictedContexts_id) 
        references context;

    alter table account_role_context 
        add constraint FK29E017B8508CA7D2 
        foreign key (account_role_id) 
        references account_role;

    alter table account_role_context_command 
        add constraint FKC63E65C48906973A 
        foreign key (restrictedContextCommands_id) 
        references context_command;

    alter table account_role_context_command 
        add constraint FKC63E65C4508CA7D2 
        foreign key (account_role_id) 
        references account_role;

    alter table character_role_context 
        add constraint FK3B98603CBA1A5C8A 
        foreign key (character_role_id) 
        references character_role;

    alter table character_role_context 
        add constraint FK3B98603CD7CEC12A 
        foreign key (restrictedContexts_id) 
        references context;

    alter table character_role_context_command 
        add constraint FK5277EA488906973A 
        foreign key (restrictedContextCommands_id) 
        references context_command;

    alter table character_role_context_command 
        add constraint FK5277EA48BA1A5C8A 
        foreign key (character_role_id) 
        references character_role;

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

    alter table context_command_alias 
        add constraint FKF55DDDCC9FA1759B 
        foreign key (context_command_id) 
        references context_command;

    alter table context_command_parameter 
        add constraint FK49AADD259FA1759B 
        foreign key (context_command_id) 
        references context_command;
