create table if not exists public.book (
    id bigserial primary key,
    title varchar(255),
    author varchar(127)
);

create table if not exists public.book_library (
    id bigserial primary key,
    book_id bigint,
    library_id bigint,
    foreign key (book_id) references public.book (id) ON UPDATE CASCADE ON DELETE CASCADE
);