begin;
    delete from accounts where id > 0;
    delete from users where id > 0;

    insert into users(id, phone) values (1, 'p1');
    insert into users(id, phone) values (2, 'p2');
    insert into users(id, phone) values (3, 'p3');

    insert into accounts(id, user_id, amount) values (111, 1, 100);
    insert into accounts(id, user_id, amount) values (222, 2, 200);
    insert into accounts(id, user_id, amount) values (333, 3, 300);
commit;
