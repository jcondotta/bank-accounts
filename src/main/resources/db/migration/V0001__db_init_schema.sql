create table bank_account (
    bank_account_id bigint not null auto_increment primary key,
    date_of_opening datetime not null,
    iban varchar(40) not null
) engine=InnoDB;

create table if not exists account_holder (
    account_holder_id bigint not null auto_increment primary key,
    account_holder_name varchar(255) not null,
    date_of_birth date not null,
    email_address varchar(255) not null,
    bank_account_id bigint not null
) engine=InnoDB;

alter table account_holder add constraint FKrab8uc310ybxjbhk1t4osar2k foreign key (bank_account_id) references bank_account (bank_account_id);